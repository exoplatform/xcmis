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
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.Connection;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsTree;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.StorageProvider;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.IncludeRelationships;

import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: FolderChildrenCollection.java 2487 2009-07-31 14:14:34Z
 *          andrew00x $
 */
public class FolderDescentantsCollection extends CmisObjectCollection
{

   /**
    * Instantiates a new folder descentants collection.
    * @param storageProvider TODO
    */
   public FolderDescentantsCollection(StorageProvider storageProvider)
   {
      super(storageProvider);
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
   protected void addChildren(Entry entry, List<ItemsTree<CmisObject>> children, IRI feedIri, RequestContext request)
      throws ResponseContextException
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
      {
         childFeed.addLink((Link)l.clone());
      }

      childFeed.addLink(getObjectLink(entryId, request), AtomCMIS.LINK_VIA, AtomCMIS.MEDIATYPE_ATOM_ENTRY, null, null,
         -1);

      // add cmisra:numItems
      Element numItems = request.getAbdera().getFactory().newElement(AtomCMIS.NUM_ITEMS, childrenElement);
      numItems.setText(Integer.toString(children.size()));
      for (ItemsTree<CmisObject> oifContainer : children)
      {
         Entry ch = request.getAbdera().getFactory().newEntry(childFeed);
         addEntryDetails(request, ch, feedIri, oifContainer.getContainer());
         if (oifContainer.getContainer().getPathSegment() != null)
         {
            // add cmisra:pathSegment
            Element pathSegment = ch.addExtension(AtomCMIS.PATH_SEGMENT);
            pathSegment.setText(oifContainer.getContainer().getPathSegment());
         }
         if (oifContainer.getChildren() != null && oifContainer.getChildren().size() > 0)
         {
            addChildren(ch, oifContainer.getChildren(), feedIri, request);
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException
   {
      boolean includeAllowableActions = getBooleanParameter(request, AtomCMIS.PARAM_INCLUDE_ALLOWABLE_ACTIONS, false);
      boolean includePathSegments = getBooleanParameter(request, AtomCMIS.PARAM_INCLUDE_PATH_SEGMENT, false);
      // XXX At the moment get all properties from back-end. We need some of them for build correct feed.
      // Filter will be applied during build final Atom Document.
      //      String propertyFilter = request.getParameter(AtomCMIS.PARAM_FILTER);
      String propertyFilter = null;
      String renditionFilter = request.getParameter(AtomCMIS.PARAM_RENDITION_FILTER);
      IncludeRelationships includeRelationships;
      try
      {
         includeRelationships =
            request.getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS) == null
               || request.getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS).length() == 0 ? IncludeRelationships.NONE
               : IncludeRelationships.fromValue(request.getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS));
      }
      catch (IllegalArgumentException iae)
      {
         String msg = "Invalid parameter " + request.getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS);
         throw new ResponseContextException(msg, 400);
      }
      int depth = getIntegerParameter(request, AtomCMIS.PARAM_DEPTH, CmisConstants.DEPTH);
      Connection conn = null;
      try
      {
         conn = getConnection(request);
         List<ItemsTree<CmisObject>> descendants =
            conn.getDescendants(getId(request), depth, includeAllowableActions, includeRelationships,
               includePathSegments, true, propertyFilter, renditionFilter);

         if (descendants.size() > 0)
         {
            // add cmisra:numItems
            Element numItems = feed.addExtension(AtomCMIS.NUM_ITEMS);
            numItems.setText(Integer.toString(descendants.size()));

            for (ItemsTree<CmisObject> oifContainer : descendants)
            {
               Entry e = feed.addEntry();
               IRI feedIri = new IRI(getFeedIriForEntry(oifContainer.getContainer(), request));
               addEntryDetails(request, e, feedIri, oifContainer.getContainer());
               if (oifContainer.getContainer().getPathSegment() != null)
               {
                  // add cmis:pathSegment
                  Element pathSegment = e.addExtension(AtomCMIS.PATH_SEGMENT);
                  pathSegment.setText(oifContainer.getContainer().getPathSegment());
               }
               if (oifContainer.getChildren().size() > 0)
               {
                  addChildren(e, oifContainer.getChildren(), feedIri, request);
               }
            }
         }
      }
      catch (StorageException re)
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
      finally
      {
         if (conn != null)
         {
            conn.close();
         }
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
      {
         feed.addLink(descendants, AtomCMIS.LINK_DOWN, AtomCMIS.MEDIATYPE_CMISTREE, null, null, -1);
      }

      String folderTree = getFolderTreeLink(id, request);
      if (folderTree != null)
      {
         feed.addLink(folderTree, AtomCMIS.LINK_CMIS_FOLDERTREE, AtomCMIS.MEDIATYPE_ATOM_FEED, null, null, -1);
      }

      Connection conn = null;
      try
      {
         conn = getConnection(request);
         if (!id.equals(conn.getStorage().getRepositoryInfo().getRootFolderId()))
         {
            try
            {
               CmisObject parent = conn.getFolderParent(id, true, null);
               feed.addLink(getObjectLink(getId(parent), request), AtomCMIS.LINK_UP, AtomCMIS.MEDIATYPE_ATOM_ENTRY,
                  null, null, -1);
            }
            catch (StorageException re)
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
      }
      finally
      {
         if (conn != null)
         {
            conn.close();
         }
      }
      return feed;
   }

}
