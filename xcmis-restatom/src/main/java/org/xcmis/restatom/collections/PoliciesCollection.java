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
import org.apache.abdera.protocol.server.context.EmptyResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisPropertyId;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.core.EnumPropertiesBase;
import org.xcmis.core.ObjectService;
import org.xcmis.core.PolicyService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: PoliciesCollection.java 2487 2009-07-31 14:14:34Z
 *          andrew00x $
 */
public class PoliciesCollection extends CmisObjectCollection
{

   /** The policy service. */
   protected final PolicyService policyService;

   /**
    * Instantiates a new policies collection.
    * 
    * @param repositoryService the repository service
    * @param objectService the object service
    * @param versioningService the versioning service
    * @param policyService the policy service
    */
   public PoliciesCollection(RepositoryService repositoryService, ObjectService objectService,
      VersioningService versioningService, PolicyService policyService)
   {
      super(repositoryService, objectService, versioningService);
      this.policyService = policyService;
      setHref("/policies");
   }

   /**
    * {@inheritDoc}
    */
   protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException
   {
      String propertyFilter = request.getParameter(AtomCMIS.PARAM_FILTER);

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
         String objectId = getId(request);
         List<CmisObjectType> list =
            policyService.getAppliedPolicies(getRepositoryId(request), objectId, propertyFilter);
         if (list.size() > 0)
         {
            // add cmisra:numItems
            Element numItems = feed.addExtension(AtomCMIS.NUM_ITEMS);
            numItems.setText(Integer.toString(list.size()));

            //Paging inks
            addPageLinks(objectId, //
               feed, //
               "policies", //
               maxItems, //
               skipCount, //
               list.size(), //
               (skipCount + maxItems) < list.size(), //
               request);

            for (CmisObjectType one : list)
            {
               Entry e = feed.addEntry();
               IRI feedIri = new IRI(getFeedIriForEntry(one, request));
               addEntryDetails(request, e, feedIri, one);
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
      catch (Throwable t)
      {
         throw new ResponseContextException(createErrorResponse(t, 500));
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ResponseContext postEntry(RequestContext request)
   {
      Entry entry;
      String objectId;
      try
      {
         entry = getEntryFromRequest(request);
         objectId = getId(request);
      }
      catch (ResponseContextException rce)
      {
         return rce.getResponseContext();
      }

      ObjectTypeElement objectElement = entry.getFirstChild(AtomCMIS.OBJECT);
      CmisObjectType object = objectElement.getObject();

      String policyId = null;

      for (CmisProperty p : object.getProperties().getProperty())
      {
         String pName = p.getPropertyDefinitionId();
         if (pName.equals(EnumPropertiesBase.CMIS_OBJECT_ID.value()))
            policyId = ((CmisPropertyId)p).getValue().get(0);
      }
      String repositoryId = getRepositoryId(request);
      try
      {
         // apply policy
         if (policyId != null)
            policyService.applyPolicy(repositoryId, policyId, objectId);
      }
      catch (ConstraintException cve)
      {
         return createErrorResponse(cve, 409);
      }
      catch (ObjectNotFoundException onfe)
      {
         return createErrorResponse(onfe, 404);
      }
      catch (InvalidArgumentException iae)
      {
         return createErrorResponse(iae, 400);
      }
      catch (RepositoryException re)
      {
         return createErrorResponse(re, 500);
      }
      catch (Throwable t)
      {
         return createErrorResponse(t, 500);
      }

      entry = request.getAbdera().getFactory().newEntry();
      try
      {
         // updated object
         addEntryDetails(request, entry, request.getResolvedUri(), (CmisObjectType)objectService.getObject(
            repositoryId, policyId, true, EnumIncludeRelationships.BOTH, true, true, null, null));
      }
      catch (ResponseContextException rce)
      {
         return rce.getResponseContext();
      }
      catch (ObjectNotFoundException onfe)
      {
         return createErrorResponse(onfe, 404);
      }
      catch (FilterNotValidException fae)
      {
         return createErrorResponse(fae, 400);
      }
      catch (RepositoryException re)
      {
         return createErrorResponse(re, 500);
      }

      Map<String, String> params = new HashMap<String, String>();
      String link = request.absoluteUrlFor(TargetType.ENTRY, params);
      return buildCreateEntryResponse(link, entry);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ResponseContext deleteEntry(RequestContext request)
   {
      Entry entry;
      String objectId;
      try
      {
         entry = getEntryFromRequest(request);
         objectId = getId(request);
      }
      catch (ResponseContextException rce)
      {
         return createErrorResponse(rce, 400);
      }

      ObjectTypeElement objectElement = entry.getFirstChild(AtomCMIS.OBJECT);
      CmisObjectType object = objectElement.getObject();

      String policyId = null;

      for (CmisProperty p : object.getProperties().getProperty())
      {
         String pName = p.getPropertyDefinitionId();
         if (pName.equals(EnumPropertiesBase.CMIS_OBJECT_ID.value()))
            policyId = ((CmisPropertyId)p).getValue().get(0);
      }
      try
      {
         if (policyId != null)
            policyService.removePolicy(getRepositoryId(request), policyId, objectId);
         ResponseContext response = new EmptyResponseContext(200);
         return response;
      }
      catch (ConstraintException cve)
      {
         return createErrorResponse(cve, 409);
      }
      catch (ObjectNotFoundException onfe)
      {
         return createErrorResponse(onfe, 404);
      }
      catch (InvalidArgumentException iae)
      {
         return createErrorResponse(iae, 400);
      }
      catch (RepositoryException re)
      {
         return createErrorResponse(re, 500);
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
   public Iterable<CmisObjectType> getEntries(RequestContext request) throws ResponseContextException
   {
      // To process hierarchically structure override addFeedDetails(Feed, RequestContext) method.
      throw new UnsupportedOperationException("policies");
   }

   /**
    * {@inheritDoc}
    */
   public String getTitle(RequestContext request)
   {
      return "Policies";
   }
}
