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

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.core.NavigationService;
import org.xcmis.core.ObjectService;
import org.xcmis.core.RepositoryService;
import org.xcmis.core.VersioningService;
import org.xcmis.messaging.CmisObjectListType;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.restatom.AtomUtils;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.UpdateConflictException;

import java.util.Calendar;

/**
 * Collection of checked-out documents.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class CheckedOutCollection extends CmisObjectCollection
{

   /** The navigation service. */
   protected final NavigationService navigationService;

   /**
    * Instantiates a new checked out collection.
    * 
    * @param repositoryService the repository service
    * @param objectService the object service
    * @param versioningService the versioning service
    * @param navigationService the navigation service
    */
   public CheckedOutCollection(RepositoryService repositoryService, ObjectService objectService,
      VersioningService versioningService, NavigationService navigationService)
   {
      super(repositoryService, objectService, versioningService);
      this.navigationService = navigationService;
      setHref("/checkedout");
   }

   /**
    * {@inheritDoc}
    */
   protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException
   {
      boolean includeAllowableActions =
         Boolean.parseBoolean(request.getParameter(AtomCMIS.PARAM_INCLUDE_ALLOWABLE_ACTIONS));
      String orderBy = request.getParameter(AtomCMIS.PARAM_ORDER_BY);
      // XXX At the moment get all properties from back-end. We need some of them for build correct feed.
      // Filter will be applied during build final Atom Document.
      //      String propertyFilter = request.getParameter(AtomCMIS.PARAM_FILTER);
      String propertyFilter = null;
      String renditionFilter = request.getParameter(AtomCMIS.PARAM_RENDITION_FILTER);
      EnumIncludeRelationships includeRelationships;
      try
      {
         includeRelationships =
            request.getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS) == null ? EnumIncludeRelationships.NONE
               : EnumIncludeRelationships.fromValue(request.getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS));
      }
      catch (IllegalArgumentException iae)
      {
         String msg = "Invalid parameter " + request.getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS);
         throw new ResponseContextException(msg, 400);
      }
      int maxItems;
      try
      {
         maxItems =
            request.getParameter(AtomCMIS.PARAM_MAX_ITEMS) == null ? CMIS.MAX_ITEMS : Integer.parseInt(request
               .getParameter(AtomCMIS.PARAM_MAX_ITEMS));
      }
      catch (NumberFormatException nfe)
      {
         String msg = "Invalid parameter " + request.getParameter(AtomCMIS.PARAM_MAX_ITEMS);
         throw new ResponseContextException(msg, 400);
      }
      int skipCount;
      try
      {
         skipCount =
            request.getParameter(AtomCMIS.PARAM_SKIP_COUNT) == null ? 0 : Integer.parseInt(request
               .getParameter(AtomCMIS.PARAM_SKIP_COUNT));
      }
      catch (NumberFormatException nfe)
      {
         String msg = "Invalid parameter " + request.getParameter(AtomCMIS.PARAM_SKIP_COUNT);
         throw new ResponseContextException(msg, 400);
      }
      try
      {
         // NOTE : Not use method getId(request) here. It may gives incorrect id.
         String folderId = request.getTarget().getParameter("objectid");
         CmisObjectListType list =
            navigationService.getCheckedOutDocs(getRepositoryId(request), folderId, includeAllowableActions,
               includeRelationships, renditionFilter, propertyFilter, orderBy, maxItems, skipCount);
         addPageLinks(folderId, feed, "checkedout", maxItems, skipCount, list.getNumItems() == null ? -1 : list
            .getNumItems().intValue(), list.isHasMoreItems(), request);
         if (list.getObjects().size() > 0)
         {
            if (list.getNumItems() != null)
            {
               // add cmisra:numItems
               Element numItems = feed.addExtension(AtomCMIS.NUM_ITEMS);
               numItems.setText(list.getNumItems().toString());
            }
            for (CmisObjectType object : list.getObjects())
            {
               Entry e = feed.addEntry();
               IRI feedIri = new IRI(getFeedIriForEntry(object, request));
               addEntryDetails(request, e, feedIri, object);
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
         throw new ResponseContextException(createErrorResponse(onfe, 400));
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
   public Iterable<CmisObjectType> getEntries(RequestContext request) throws ResponseContextException
   {
      throw new UnsupportedOperationException("entries");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getId(RequestContext request)
   {
      // XXX Not use this method for getting id of folder from which checked-out
      // must be retrieved. Folder identifier may be absent but it is not allowed
      // for Abdera.
      String id = super.getId(request);
      if (id != null)
         return id;
      // Need this for getCheckedOutDocuments when folderId is not specified.
      return "cmis:checkedout:" + getRepositoryId(request);
   }

   /**
    * {@inheritDoc}
    */
   public String getTitle(RequestContext request)
   {
      return "Checkedout collection";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ResponseContext postEntry(RequestContext request)
   {
      String id = null;
      try
      {
         id = getEntryFromRequest(request).getId().toString();
      }
      catch (ResponseContextException e1)
      {
         // support when id sent directly 
      }
      try
      {
         String pwcId = getId(versioningService.checkout(getRepositoryId(request), id == null ? getId(request) : id));
         Entry entry = request.getAbdera().getFactory().newEntry();
         try
         {
            addEntryDetails(request, entry, request.getResolvedUri(), getEntry(pwcId, request));
         }
         catch (ResponseContextException e)
         {
            return createErrorResponse(e);
         }
         String link = getObjectLink(pwcId, request);
         return buildCreateEntryResponse(link, entry);
      }
      catch (ConstraintException cve)
      {
         return createErrorResponse(cve, 409);
      }
      catch (RepositoryException re)
      {
         return createErrorResponse(re, 500);
      }
      catch (UpdateConflictException uce)
      {
         return createErrorResponse(uce, 409);
      }
      catch (ObjectNotFoundException onfe)
      {
         return createErrorResponse(onfe, 404);
      }
      catch (InvalidArgumentException iae)
      {
         return createErrorResponse(iae, 400);
      }
      catch (ResponseContextException e)
      {
         return createErrorResponse(e);
      }
      catch (Throwable t)
      {
         return createErrorResponse(t, 500);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected Feed createFeedBase(RequestContext request) throws ResponseContextException
   {
      Factory factory = request.getAbdera().getFactory();
      Feed feed = factory.newFeed();
      feed.setId(getId(request));
      feed.setTitle(getTitle(request));
      feed.addAuthor(getAuthor(request));
      // FIXME updated is incorrect when pass Date.
      // Abdera uses Calendar.getInstance(TimeZone.getTimeZone("GMT"))
      // See org.apache.abdera.model.AtomDate .
      feed.setUpdated(AtomUtils.getAtomDate(Calendar.getInstance()));

      feed.addLink(getServiceLink(request), "service", "application/atomsvc+xml", null, null, -1);
      return feed;
   }

}
