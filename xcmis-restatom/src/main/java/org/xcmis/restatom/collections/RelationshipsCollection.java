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
import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisPropertyId;
import org.xcmis.core.EnumRelationshipDirection;
import org.xcmis.core.ObjectService;
import org.xcmis.core.RelationshipService;
import org.xcmis.core.RepositoryService;
import org.xcmis.core.VersioningService;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.restatom.abdera.ObjectTypeElement;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.object.CmisObject;
import org.xcmis.spi.object.CmisObjectList;
import org.xcmis.spi.object.impl.CmisObjectImpl;

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

   /** The relationship service. */
   protected final RelationshipService relationshipService;

   /**
    * Instantiates a new relationships collection.
    * 
    * @param repositoryService the repository service
    * @param objectService the object service
    * @param versioningService the versioning service
    * @param relationshipService the relationship service
    */
   public RelationshipsCollection(RepositoryService repositoryService, ObjectService objectService,
      VersioningService versioningService, RelationshipService relationshipService)
   {
      super(repositoryService, objectService, versioningService);
      this.relationshipService = relationshipService;
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
      CmisObjectType cmisObjectType = objectElement.getObject();
      CmisObject object = new CmisObjectImpl(cmisObjectType);
      if (object.getProperties() == null)
         object.setProperties(new CmisPropertiesType());
      updatePropertiesFromEntry(object, entry);

      String typeId = null;
      String sourceId = null;
      String targetId = null;

      CmisPropertiesType properties = object.getProperties();
      for (CmisProperty p : properties.getProperty())
      {
         String pId = p.getPropertyDefinitionId();
         if (CMIS.OBJECT_TYPE_ID.equals(pId))
            typeId = ((CmisPropertyId)p).getValue().get(0);
         else if (CMIS.SOURCE_ID.equals(pId))
            sourceId = ((CmisPropertyId)p).getValue().get(0);
         else if (CMIS.TARGET_ID.equals(pId))
            targetId = ((CmisPropertyId)p).getValue().get(0);
      }

      if (typeId == null)
         return createErrorResponse("cmis:objectTypeId is not specified.", 400);
      if (sourceId == null)
         return createErrorResponse("cmis:sourceId is not specified.", 400);
      if (targetId == null)
         return createErrorResponse("cmis:targetId is not specified.", 400);

      CmisAccessControlListType addACL = null;
      CmisAccessControlListType removeACL = null;
      List<String> policies = null;
      CmisObject relationship;
      try
      {
         relationship =
            objectService.createRelationship(getRepositoryId(request), properties, addACL, removeACL, policies, true);
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
      EnumRelationshipDirection direction;
      try
      {
         direction =
            request.getParameter(AtomCMIS.PARAM_RELATIONSHIP_DIRECTION) == null
               || request.getParameter(AtomCMIS.PARAM_RELATIONSHIP_DIRECTION).length() == 0
               ? EnumRelationshipDirection.EITHER : EnumRelationshipDirection
                  .fromValue(AtomCMIS.PARAM_RELATIONSHIP_DIRECTION);
      }
      catch (IllegalArgumentException iae)
      {
         String msg = "Invalid parameter " + request.getParameter(AtomCMIS.PARAM_RELATIONSHIP_DIRECTION);
         throw new ResponseContextException(msg, 400);
      }
      try
      {
         CmisObjectList list =
            relationshipService.getObjectRelationships(getRepositoryId(request), objectId, direction, typeId,
               includeSubRelationship, includeAllowableActions, propertyFilter, maxItems, skipCount, true);
         if (list.getObjects().size() > 0)
         {
            // add cmisra:numItems
            if (list.getNumItems() != null)
            {
               Element numItems = feed.addExtension(AtomCMIS.NUM_ITEMS);
               numItems.setText(list.getNumItems().toString());
            }

            //Paging links
            addPageLinks(objectId, feed, "relationships", maxItems, skipCount, list.getNumItems() == null ? -1 : list
               .getNumItems().intValue(), list.isHasMoreItems(), request);

            for (CmisObject object : list.getObjects())
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
