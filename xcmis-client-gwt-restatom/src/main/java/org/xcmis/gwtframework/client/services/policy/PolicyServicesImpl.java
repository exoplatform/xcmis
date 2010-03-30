/**
 *  Copyright (C) 2010 eXo Platform SAS.
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xcmis.gwtframework.client.services.policy;

import org.xcmis.gwtframework.client.CmisArguments;
import org.xcmis.gwtframework.client.marshallers.ApplyPolicyMarshaller;
import org.xcmis.gwtframework.client.marshallers.QueryMarshaller;
import org.xcmis.gwtframework.client.marshallers.RemovePolicyMarshaller;
import org.xcmis.gwtframework.client.model.EnumBaseObjectTypeIds;
import org.xcmis.gwtframework.client.model.EnumIncludeRelationships;
import org.xcmis.gwtframework.client.model.actions.ApplyPolicy;
import org.xcmis.gwtframework.client.model.actions.Query;
import org.xcmis.gwtframework.client.model.actions.RemovePolicy;
import org.xcmis.gwtframework.client.model.restatom.AtomEntry;
import org.xcmis.gwtframework.client.model.restatom.EntryCollection;
import org.xcmis.gwtframework.client.services.policy.event.AllPoliciesReceivedEvent;
import org.xcmis.gwtframework.client.services.policy.event.AppliedPoliciesReceivedEvent;
import org.xcmis.gwtframework.client.services.policy.event.PolicyAppliedEvent;
import org.xcmis.gwtframework.client.services.policy.event.PolicyRemovedEvent;
import org.xcmis.gwtframework.client.unmarshallers.EntryCollectionUnmarshaller;
import org.xcmis.gwtframework.client.unmarshallers.EntryUnmarshaller;
import org.xcmis.gwtframework.client.util.AsyncRequest;
import org.xcmis.gwtframework.client.util.AsyncRequestCallback;
import org.xcmis.gwtframework.client.util.HTTPHeader;
import org.xcmis.gwtframework.client.util.HTTPMethod;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class PolicyServicesImpl extends PolicyServices
{
   /**
    * Event bus.
    */
   private HandlerManager eventBus;

   /**
    * @param eventBus eventBus
    */
   public PolicyServicesImpl(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }

   /**
    * @see org.xcmis.gwtframework.client.services.policy.PolicyServices#applyPolicy(
    * java.lang.String, org.xcmis.gwtframework.client.model.actions.ApplyPolicy)
    * 
    * @param url url
    * @param applyPolicy applyPolicy
    */
   @Override
   public void applyPolicy(String url, ApplyPolicy applyPolicy)
   {
      AtomEntry policy = new AtomEntry();
      PolicyAppliedEvent event = new PolicyAppliedEvent(policy);
      EntryUnmarshaller unmarshaller = new EntryUnmarshaller(policy);
      ApplyPolicyMarshaller marshaller = new ApplyPolicyMarshaller(applyPolicy);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.POST, url).data(marshaller).send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.policy.PolicyServices#removePolicy(
    * java.lang.String, org.xcmis.gwtframework.client.model.actions.RemovePolicy)
    * 
    * @param url url
    * @param removePolicy remove policy
    */
   @Override
   public void removePolicy(String url, RemovePolicy removePolicy)
   {
      PolicyRemovedEvent event = new PolicyRemovedEvent();
      RemovePolicyMarshaller marshaller = new RemovePolicyMarshaller(removePolicy);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, null, event);
      AsyncRequest.build(RequestBuilder.POST, url)
         .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.DELETE)
         .data(marshaller)
         .send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.policy.PolicyServices#getAppliedPolicies(
    * java.lang.String, java.lang.String)
    * 
    * @param url url
    * @param filter filter
    */
   @Override
   public void getAppliedPolicies(String url, String filter)
   {
      EntryCollection policies = new EntryCollection();
      AppliedPoliciesReceivedEvent event = new AppliedPoliciesReceivedEvent(policies);
      EntryCollectionUnmarshaller unmarshaller = new EntryCollectionUnmarshaller(policies);
      String params = "";
      params += (filter == null || filter.length() <= 0) ? "" : CmisArguments.FILTER + "=" + filter;
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url + "?" + params).send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.policy.PolicyServices#getAllPolicies(
    * java.lang.String, java.lang.String, boolean, boolean, 
    * org.xcmis.gwtframework.client.model.EnumIncludeRelationships, java.lang.String, 
    * java.lang.Long, java.lang.Long)
    * 
    * @param url url
    * @param repositoryId repository id
    * @param searchAllVersions search all versions
    * @param includeAllowableActions include allowable actions
    * @param includeRelationships include relationships
    * @param renditionFilter rendition filter
    * @param maxItems max items
    * @param skipCount skip count
    */
   @Override
   public void getAllPolicies(String url, String repositoryId, boolean searchAllVersions,
      boolean includeAllowableActions, EnumIncludeRelationships includeRelationships, 
      String renditionFilter, Long maxItems, Long skipCount)
   {
      EntryCollection policies = new EntryCollection();
      EntryCollectionUnmarshaller unmarshaller = new EntryCollectionUnmarshaller(policies);
      AllPoliciesReceivedEvent event = new AllPoliciesReceivedEvent(policies);

      /*Get all properties from repository using query*/
      String statement = "SELECT * FROM " + EnumBaseObjectTypeIds.CMIS_POLICY.value();
      Query query = new Query();
      query.setRepositoryId(repositoryId);
      query.setSearchAllVersions(searchAllVersions);
      query.setSkipCount(skipCount);
      query.setIncludeAllowableActions(includeAllowableActions);
      query.setMaxItems(maxItems);
      query.setRenditionFilter(renditionFilter);
      query.setIncludeRelationships(includeRelationships);
      query.setStatement(statement);
      QueryMarshaller marshaller = new QueryMarshaller(query);

      String params = "";
      params += (maxItems < 0) ? "" : CmisArguments.MAX_ITEMS + "=" + maxItems + "&";
      params += (skipCount < 0) ? "" : CmisArguments.SKIP_COUNT + "=" + skipCount + "&";
      params += CmisArguments.INCLUDE_RELATIONSHIPS + "=" + includeRelationships.value() + "&";
      params += (renditionFilter == null || renditionFilter.length() <= 0) 
                   ? "" 
                   : CmisArguments.RENDITION_FILTER + "=" + renditionFilter + "&";
      params += CmisArguments.INCLUDE_ALLOWABLE_ACTIONS + "=" + includeAllowableActions + "&";
      params += CmisArguments.ALL_VERSIONS + "=" + searchAllVersions;
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.POST, url).data(marshaller).send(callback);
   }

}
