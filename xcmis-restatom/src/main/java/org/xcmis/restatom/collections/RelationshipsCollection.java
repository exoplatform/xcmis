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
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.restatom.abdera.ObjectTypeElement;
import org.xcmis.spi.AccessControlEntry;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.RelationshipDirection;
import org.xcmis.spi.object.CmisObject;
import org.xcmis.spi.object.Property;
import org.xcmis.spi.object.impl.IdProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RelationshipsCollection.java 2722 2009-08-18 16:25:52Z
 *          andrew00x $
 */
public class RelationshipsCollection extends CmisObjectCollection
{

   /**
    * Instantiates a new relationships collection.
    */
   public RelationshipsCollection()
   {
      super();
      setHref("/relationships");
   }

   /**
    * {@inheritDoc}
    */
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
      return "Relationships";
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
      CmisObject object = objectElement.getObject();
      updatePropertiesFromEntry(object, entry);

      String typeId = null;
      String sourceId = null;
      String targetId = null;

      Map<String, Property<?>> properties = object.getProperties();
      for (Property<?> p : properties.values())
      {
         String pId = p.getId();
         if (CMIS.OBJECT_TYPE_ID.equals(pId))
            typeId = ((IdProperty)p).getValues().get(0);
         else if (CMIS.SOURCE_ID.equals(pId))
            sourceId = ((IdProperty)p).getValues().get(0);
         else if (CMIS.TARGET_ID.equals(pId))
            targetId = ((IdProperty)p).getValues().get(0);
      }

      if (typeId == null)
         return createErrorResponse("ObjectTypeId is not specified.", 400);
      if (sourceId == null)
         return createErrorResponse("Source id is not specified.", 400);
      if (targetId == null)
         return createErrorResponse("Traget id is not specified.", 400);

      List<AccessControlEntry> addACL = null;
      List<AccessControlEntry> removeACL = null;
      List<String> policies = null;

      String relationshipId;
      try
      {
         relationshipId = conn.createRelationship(properties, addACL, removeACL, policies);
      }
      catch (ConstraintException cve)
      {
         return createErrorResponse(cve, 409);
      }
      catch (RepositoryException re)
      {
         return createErrorResponse(re, 500);
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

      CmisObject relationship = conn.getProperties(relationshipId, true, CMIS.WILDCARD);

      entry = request.getAbdera().getFactory().newEntry();
      try
      {
         addEntryDetails(request, entry, request.getResolvedUri(), relationship);
      }
      catch (ResponseContextException rce)
      {
         return rce.getResponseContext();
      }
      Map<String, String> params = new HashMap<String, String>();
      String link = request.absoluteUrlFor(TargetType.ENTRY, params);
      return buildCreateEntryResponse(link, entry);
   }

   /**
    * {@inheritDoc}
    */
   protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException
   {

      String objectId = getId(request);
      String typeId = request.getParameter(AtomCMIS.PARAM_TYPE_ID);
      // XXX At the moment get all properties from back-end. We need some of them for build correct feed.
      // Filter will be applied during build final Atom Document.
      //      String propertyFilter = request.getParameter(AtomCMIS.PARAM_FILTER);
      String propertyFilter = null;
      boolean includeSubRelationship =
         Boolean.parseBoolean(request.getParameter(AtomCMIS.PARAM_INCLUDE_SUB_RELATIONSHIP_TYPES));
      boolean includeAllowableActions =
         Boolean.parseBoolean(request.getParameter(AtomCMIS.PARAM_INCLUDE_ALLOWABLE_ACTIONS));
      int maxItems;
      try
      {
         maxItems =
            request.getParameter(AtomCMIS.PARAM_MAX_ITEMS) == null
               || request.getParameter(AtomCMIS.PARAM_MAX_ITEMS).length() == 0 ? CMIS.MAX_ITEMS : Integer
               .parseInt(request.getParameter(AtomCMIS.PARAM_MAX_ITEMS));
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
            request.getParameter(AtomCMIS.PARAM_SKIP_COUNT) == null
               || request.getParameter(AtomCMIS.PARAM_SKIP_COUNT).length() == 0 ? 0 : Integer.parseInt(request
               .getParameter(AtomCMIS.PARAM_SKIP_COUNT));
      }
      catch (NumberFormatException nfe)
      {
         String msg = "Invalid parameter " + request.getParameter(AtomCMIS.PARAM_SKIP_COUNT);
         throw new ResponseContextException(msg, 400);
      }
      RelationshipDirection direction;
      try
      {
         direction =
            request.getParameter(AtomCMIS.PARAM_RELATIONSHIP_DIRECTION) == null
               || request.getParameter(AtomCMIS.PARAM_RELATIONSHIP_DIRECTION).length() == 0
               ? RelationshipDirection.EITHER : RelationshipDirection.fromValue(AtomCMIS.PARAM_RELATIONSHIP_DIRECTION);
      }
      catch (IllegalArgumentException iae)
      {
         String msg = "Invalid parameter " + request.getParameter(AtomCMIS.PARAM_RELATIONSHIP_DIRECTION);
         throw new ResponseContextException(msg, 400);
      }
      try
      {
         ItemsList<CmisObject> list =
            conn.getObjectRelationships(objectId, direction, typeId, includeSubRelationship, includeAllowableActions,
               true, propertyFilter, maxItems, skipCount);
         if (list.getItems().size() > 0)
         {
            // add cmisra:numItems
            Element numItems = feed.addExtension(AtomCMIS.NUM_ITEMS);
            numItems.setText(Integer.toString(list.getNumItems()));

            //Paging links
            addPageLinks(objectId, feed, "relationships", maxItems, skipCount, list.getNumItems(), list
               .isHasMoreItems(), request);

            for (CmisObject object : list.getItems())
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
