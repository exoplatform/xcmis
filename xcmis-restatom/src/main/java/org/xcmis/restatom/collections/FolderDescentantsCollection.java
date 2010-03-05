/**
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xcmis.restatom.collections;

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.core.NavigationService;
import org.xcmis.core.ObjectService;
import org.xcmis.core.RepositoryService;
import org.xcmis.core.VersioningService;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.object.CmisObject;
import org.xcmis.spi.object.CmisObjectInFolderContainer;

import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: FolderChildrenCollection.java 2487 2009-07-31 14:14:34Z
 *          andrew00x $
 */
public class FolderDescentantsCollection extends CmisObjectCollection
{

   /** The navigation service. */
   protected NavigationService navigationService;

   /**
    * Instantiates a new folder descentants collection.
    * 
    * @param repositoryService the repository service
    * @param objectService the object service
    * @param versioningService the versioning service
    * @param navigationService the navigation service
    */
   public FolderDescentantsCollection(RepositoryService repositoryService, ObjectService objectService,
      VersioningService versioningService, NavigationService navigationService)
   {
      super(repositoryService, objectService, versioningService);
      this.navigationService = navigationService;
      setHref("/descendants");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Iterable<CmisObject> getEntries(RequestContext request) throws ResponseContextException
   {
      // To process hierarchically structure override addFeedDetails(Feed, RequestContext) method.
      throw new UnsupportedOperationException("entries");
   }

   /**
    * {@inheritDoc}
    */
   public String getTitle(RequestContext request)
   {
      return "Folder Descendants";
   }

   /**
    * Recursively discovery all levels.
    * 
    * @param entry current parent entry
    * @param children children
    * @param feedIri root level feed IRI
    * @param request request
    * @throws ResponseContextException if error occurs
    */
   protected void addChildren(Entry entry, List<CmisObjectInFolderContainer> children, IRI feedIri,
      RequestContext request) throws ResponseContextException
   {
      Element childrenElement = entry.addExtension(AtomCMIS.CHILDREN);

      // In this case entry is parent for feed, so use info from entry for new feed.
      String entryId = entry.getId().toString();
      Feed childFeed = request.getAbdera().getFactory().newFeed(childrenElement);
      childFeed.setId("ch:" + entryId); // TODO : entry use objectId and may not have two items with the same id.
      childFeed.setTitle("Folder Children");
      childFeed.addAuthor(entry.getAuthor());
      childFeed.setUpdated(entry.getUpdated());

      // Copy some links from entry.
      List<Link> links =
         entry.getLinks(AtomCMIS.LINK_SERVICE, AtomCMIS.LINK_SELF, AtomCMIS.LINK_DOWN, AtomCMIS.LINK_CMIS_FOLDERTREE,
            AtomCMIS.LINK_UP);
      for (Link l : links)
         childFeed.addLink((Link)l.clone());

      childFeed.addLink(getObjectLink(entryId, request), AtomCMIS.LINK_VIA, AtomCMIS.MEDIATYPE_ATOM_ENTRY, null, null,
         -1);

      // add cmisra:numItems
      Element numItems = request.getAbdera().getFactory().newElement(AtomCMIS.NUM_ITEMS, childrenElement);
      numItems.setText(Integer.toString(children.size()));
      for (CmisObjectInFolderContainer oifContainer : children)
      {
         Entry ch = request.getAbdera().getFactory().newEntry(childFeed);
         addEntryDetails(request, ch, feedIri, oifContainer.getObjectInFolder().getObject());
         if (oifContainer.getObjectInFolder().getPathSegment() != null)
         {
            // add cmisra:pathSegment
            Element pathSegment = ch.addExtension(AtomCMIS.PATH_SEGMENT);
            pathSegment.setText(oifContainer.getObjectInFolder().getPathSegment());
         }
         if (oifContainer.getChildren().size() > 0)
            addChildren(ch, oifContainer.getChildren(), feedIri, request);
      }
   }

   /**
    * {@inheritDoc}
    */
   protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException
   {
      boolean includeAllowableActions =
         Boolean.parseBoolean(request.getParameter(AtomCMIS.PARAM_INCLUDE_ALLOWABLE_ACTIONS));
      boolean includePathSegments = Boolean.parseBoolean(request.getParameter(AtomCMIS.PARAM_INCLUDE_PATH_SEGMENT));
      // XXX At the moment get all properties from back-end. We need some of them for build correct feed.
      // Filter will be applied during build final Atom Document.
      //      String propertyFilter = request.getParameter(AtomCMIS.PARAM_FILTER);
      String propertyFilter = null;
      String renditionFilter = request.getParameter(AtomCMIS.PARAM_RENDITION_FILTER);
      EnumIncludeRelationships includeRelationships;
      try
      {
         includeRelationships =
            request.getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS) == null
               || request.getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS).length() == 0
               ? EnumIncludeRelationships.NONE : EnumIncludeRelationships.fromValue(request
                  .getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS));
      }
      catch (IllegalArgumentException iae)
      {
         String msg = "Invalid parameter " + request.getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS);
         throw new ResponseContextException(msg, 400);
      }
      int depth;
      try
      {
         depth =
            request.getParameter(AtomCMIS.PARAM_DEPTH) == null
               || request.getParameter(AtomCMIS.PARAM_DEPTH).length() == 0 ? 1 : Integer.parseInt(request
               .getParameter(AtomCMIS.PARAM_DEPTH));
      }
      catch (NumberFormatException nfe)
      {
         String msg = "Invalid parameter " + request.getParameter(AtomCMIS.PARAM_DEPTH);
         throw new ResponseContextException(msg, 400);
      }
      try
      {
         List<CmisObjectInFolderContainer> descendants =
            navigationService.getDescendants(getRepositoryId(request), getId(request), depth, includeAllowableActions,
               includeRelationships, includePathSegments, propertyFilter, renditionFilter, true);

         if (descendants.size() > 0)
         {
            // add cmisra:numItems
            Element numItems = feed.addExtension(AtomCMIS.NUM_ITEMS);
            numItems.setText(Integer.toString(descendants.size()));

            for (CmisObjectInFolderContainer oifContainer : descendants)
            {
               Entry e = feed.addEntry();
               IRI feedIri = new IRI(getFeedIriForEntry(oifContainer.getObjectInFolder().getObject(), request));
               addEntryDetails(request, e, feedIri, oifContainer.getObjectInFolder().getObject());
               if (oifContainer.getObjectInFolder().getPathSegment() != null)
               {
                  // add cmis:pathSegment
                  Element pathSegment = e.addExtension(AtomCMIS.PATH_SEGMENT);
                  pathSegment.setText(oifContainer.getObjectInFolder().getPathSegment());
               }
               if (oifContainer.getChildren().size() > 0)
                  addChildren(e, oifContainer.getChildren(), feedIri, request);
            }
         }
      }
      catch (RepositoryException re)
      {
         throw new ResponseContextException(createErrorResponse(re, 500));
      }
      catch (FilterNotValidException fe)
      {
         throw new ResponseContextException(createErrorResponse(fe, 400));
      }
      catch (ObjectNotFoundException onfe)
      {
         throw new ResponseContextException(createErrorResponse(onfe, 404));
      }
      catch (InvalidArgumentException iae)
      {
         throw new ResponseContextException(createErrorResponse(iae, 400));
      }
      catch (Throwable t)
      {
         throw new ResponseContextException(createErrorResponse(t, 500));
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected Feed createFeedBase(RequestContext request) throws ResponseContextException
   {
      Feed feed = super.createFeedBase(request);
      // Add required links.
      String id = getId(request);
      feed.addLink(getChildrenLink(id, request), AtomCMIS.LINK_DOWN, AtomCMIS.MEDIATYPE_ATOM_FEED, null, null, -1);
      String descendants = getDescendantsLink(id, request);
      if (descendants != null)
         feed.addLink(descendants, AtomCMIS.LINK_DOWN, AtomCMIS.MEDIATYPE_CMISTREE, null, null, -1);

      String folderTree = getFolderTreeLink(id, request);
      if (folderTree != null)
         feed.addLink(folderTree, AtomCMIS.LINK_CMIS_FOLDERTREE, AtomCMIS.MEDIATYPE_ATOM_FEED, null, null, -1);

      String repositoryId = getRepositoryId(request);
      if (!id.equals(repositoryService.getRepositoryInfo(repositoryId).getRootFolderId()))
      {
         try
         {
            CmisObject parent = navigationService.getFolderParent(repositoryId, id, null, true);
            feed.addLink(getObjectLink(getId(parent), request), AtomCMIS.LINK_UP, AtomCMIS.MEDIATYPE_ATOM_ENTRY, null,
               null, -1);
         }
         catch (RepositoryException re)
         {
            throw new ResponseContextException(createErrorResponse(re, 500));
         }
         catch (FilterNotValidException fe)
         {
            throw new ResponseContextException(createErrorResponse(fe, 400));
         }
         catch (ObjectNotFoundException onfe)
         {
            throw new ResponseContextException(createErrorResponse(onfe, 404));
         }
         catch (InvalidArgumentException iae)
         {
            throw new ResponseContextException(createErrorResponse(iae, 400));
         }
         catch (Throwable t)
         {
            throw new ResponseContextException(createErrorResponse(t, 500));
         }
      }
      return feed;
   }

}
