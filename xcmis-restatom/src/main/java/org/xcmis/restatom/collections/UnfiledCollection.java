/*
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
import org.xcmis.restatom.abdera.ObjectTypeElement;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.Connection;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.IncludeRelationships;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.impl.IdProperty;

import java.util.Calendar;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id:  $
 *
 */
public class UnfiledCollection extends CmisObjectCollection
{

   /**
    * Instantiates a new unfiled collection.
    */
   public UnfiledCollection()
   {
      super();
      setHref("/unfiled");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Iterable<CmisObject> getEntries(RequestContext request) throws ResponseContextException
   {
      throw new UnsupportedOperationException("entries");
   }

   /**
    * {@inheritDoc}
    */
   public String getTitle(RequestContext request)
   {
      return "Unfiled collection";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ResponseContext postEntry(RequestContext request)
   {
      Entry entry;
      try
      {
         entry = getEntryFromRequest(request);
      }
      catch (ResponseContextException rce)
      {
         return rce.getResponseContext();
      }

      ObjectTypeElement objectElement = entry.getFirstChild(AtomCMIS.OBJECT);
      boolean hasCMISElement = objectElement != null;
      CmisObject object = hasCMISElement ? object = objectElement.getObject() : new CmisObject();
      updatePropertiesFromEntry(object, entry);
      String id = null;
      if (hasCMISElement)
      {
         for (Property<?> p : object.getProperties().values())
         {
            String pName = p.getId();
            if (CmisConstants.OBJECT_ID.equals(pName))
            {
               id = ((IdProperty)p).getValues().get(0);
            }
         }
      }

      // TODO 
      //  The following arguments may be supplied. Please see the domain model for more information:
      //  • removeFrom: For repositories which support multi-filing, this parameter identifies which folder to
      //    remove this object from. If specified, it indicates the folder from which the object shall be moved.
      //    If not specified, the object will be removed from all folders.

      Connection conn = null;
      try
      {
         conn = getConnection(request);
         ObjectData objectData = conn.getStorage().getObjectById(id);
         conn.getStorage().unfileObject(objectData);

         entry = request.getAbdera().getFactory().newEntry();
         try
         {
            addEntryDetails(request, entry, request.getResolvedUri(), getEntry(id, request));
         }
         catch (ResponseContextException e)
         {
            return createErrorResponse(e);
         }
         String link = getObjectLink(id, request);
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

   /**
    * {@inheritDoc}
    */
   protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException
   {
      boolean includeAllowableActions = getBooleanParameter(request, AtomCMIS.PARAM_INCLUDE_ALLOWABLE_ACTIONS, false);
      String orderBy = request.getParameter(AtomCMIS.PARAM_ORDER_BY);
      String propertyFilter = request.getParameter(AtomCMIS.PARAM_FILTER);
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

         Iterator<String> iterator = conn.getStorage().getUnfiledObjectsId();

         try
         {
            if (skipCount > 0)
            {
               for (int count = 0; iterator.hasNext() && (skipCount < 0 || count < skipCount); count++)
               {
                  iterator.next();
               }
            }
         }
         catch (NoSuchElementException nse)
         {
            throw new InvalidArgumentException("skipCount parameter is greater then total number of argument");
         }

         ItemsList<CmisObject> list = new ItemsList<CmisObject>();
         for (int count = 0; iterator.hasNext() && (maxItems < 0 || count < maxItems); count++)
         {
            String objectId = iterator.next();
            list.getItems().add(
               conn.getObject(objectId, includeAllowableActions, includeRelationships, false, false, true,
                  propertyFilter, renditionFilter));
         }

         list.setHasMoreItems(iterator.hasNext());
         list.setNumItems(-1); // ItemsIterator gives -1 if total number is unknown

         addPageLinks("", feed, "unfiled", maxItems, skipCount, list.getNumItems(), list.isHasMoreItems(), request);
         if (list.getItems().size() > 0)
         {
            if (list.getNumItems() != -1)
            {
               // add cmisra:numItems
               Element numItems = feed.addExtension(AtomCMIS.NUM_ITEMS);
               numItems.setText(Integer.toString(list.getNumItems()));
            }

            //            // add cmisra:hasMoreItems
            //            Element hasMoreItems = feed.addExtension(AtomCMIS.HAS_MORE_ITEMS);
            //            hasMoreItems.setText(Boolean.toString(list.isHasMoreItems()));

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
   public String getId(RequestContext request)
   {
      return "cmis:unfiled:" + getRepositoryId(request);
   }

}
