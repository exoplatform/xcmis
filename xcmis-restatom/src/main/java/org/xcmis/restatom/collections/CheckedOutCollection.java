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
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.restatom.AtomUtils;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.Connection;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.IncludeRelationships;

import java.util.Calendar;

/**
 * Collection of checked-out documents.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CheckedOutCollection.java 216 2010-02-12 17:19:50Z andrew00x $
 */
public class CheckedOutCollection extends CmisObjectCollection
{

   /**
    * Instantiates a new checked out collection.
    * @param storageProvider TODO
    *
    */
   public CheckedOutCollection(/*StorageProvider storageProvider*/)
   {
      super(/*storageProvider*/);
      setHref("/checkedout");
   }

   /**
    * {@inheritDoc}
    */
   protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException
   {
      boolean includeAllowableActions = getBooleanParameter(request, AtomCMIS.PARAM_INCLUDE_ALLOWABLE_ACTIONS, false);
      String orderBy = request.getParameter(AtomCMIS.PARAM_ORDER_BY);
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
      int maxItems = getIntegerParameter(request, AtomCMIS.PARAM_MAX_ITEMS, CmisConstants.MAX_ITEMS);
      int skipCount = getIntegerParameter(request, AtomCMIS.PARAM_SKIP_COUNT, CmisConstants.SKIP_COUNT);
      Connection conn = null;
      try
      {
         conn = getConnection(request);
         // NOTE : Not use method getId(request) here. It may gives incorrect id.
         String folderId = request.getTarget().getParameter("objectid");
         ItemsList<CmisObject> list =
            conn.getCheckedOutDocs(folderId, includeAllowableActions, includeRelationships, true, propertyFilter,
               renditionFilter, orderBy, maxItems, skipCount);
         addPageLinks(folderId, feed, "checkedout", maxItems, skipCount, list.getNumItems(), list.isHasMoreItems(),
            request);
         if (list.getItems().size() > 0)
         {
            if (list.getNumItems() != -1)
            {
               // add cmisra:numItems
               Element numItems = feed.addExtension(AtomCMIS.NUM_ITEMS);
               numItems.setText(Integer.toString(list.getNumItems()));
            }
            for (CmisObject object : list.getItems())
            {
               Entry e = feed.addEntry();
               IRI feedIri = new IRI(getFeedIriForEntry(object, request));
               addEntryDetails(request, e, feedIri, object);
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
   public Iterable<CmisObject> getEntries(RequestContext request) throws ResponseContextException
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
      {
         return id;
      }
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
      Connection conn = null;
      try
      {
         conn = getConnection(request);
         String pwcId = conn.checkout(id == null ? getId(request) : id);
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
      catch (StorageException re)
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
      catch (Throwable t)
      {
         return createErrorResponse(t, 500);
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
