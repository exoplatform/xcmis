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

package org.xcmis.gwtframework.client.service.repository;

import org.xcmis.gwtframework.client.CmisArguments;
import org.xcmis.gwtframework.client.marshallers.TypeDefinitionMarshaller;
import org.xcmis.gwtframework.client.model.repository.CmisRepositoryInfo;
import org.xcmis.gwtframework.client.model.restatom.CmisRepositories;
import org.xcmis.gwtframework.client.model.restatom.TypeCollection;
import org.xcmis.gwtframework.client.model.restatom.TypeEntry;
import org.xcmis.gwtframework.client.model.restatom.TypeList;
import org.xcmis.gwtframework.client.model.type.CmisTypeDefinitionType;
import org.xcmis.gwtframework.client.rest.AsyncRequest;
import org.xcmis.gwtframework.client.rest.AsyncRequestCallback;
import org.xcmis.gwtframework.client.rest.HTTPHeader;
import org.xcmis.gwtframework.client.rest.HTTPMethod;
import org.xcmis.gwtframework.client.service.repository.event.BaseTypesReceivedEvent;
import org.xcmis.gwtframework.client.service.repository.event.RepositoriesReceivedEvent;
import org.xcmis.gwtframework.client.service.repository.event.RepositoryInfoReceivedEvent;
import org.xcmis.gwtframework.client.service.repository.event.TypeChildrenReceivedEvent;
import org.xcmis.gwtframework.client.service.repository.event.TypeCreatedEvent;
import org.xcmis.gwtframework.client.service.repository.event.TypeDefinitionReceivedEvent;
import org.xcmis.gwtframework.client.service.repository.event.TypeDeletedEvent;
import org.xcmis.gwtframework.client.service.repository.event.TypeDescendantsRecievedEvent;
import org.xcmis.gwtframework.client.service.repository.event.TypeListReceivedEvent;
import org.xcmis.gwtframework.client.unmarshallers.RepositoriesUnmarshaller;
import org.xcmis.gwtframework.client.unmarshallers.RepositoryInfoUnmarshaller;
import org.xcmis.gwtframework.client.unmarshallers.TypeChildrenUnmarshaller;
import org.xcmis.gwtframework.client.unmarshallers.TypeDefinitionUnmarshaller;
import org.xcmis.gwtframework.client.unmarshallers.TypeDescendantsUnmarshaller;
import org.xcmis.gwtframework.client.unmarshallers.TypeListUnmarshaller;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class RepositoryService
{
   /**
    * Event bus.
    */
   private HandlerManager eventBus;

   /**
    * @param eventBus eventBus
    */
   public RepositoryService(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }

   /**
    * Get a list of CMIS repositories available from this CMIS service endpoint.
    * 
    * On success response received, {@link RepositoriesReceivedEvent} event is fired
    * 
    * @param url url
    */
   public void getRepositories(String url)
   {
      CmisRepositories cmisService = new CmisRepositories();
      RepositoriesReceivedEvent event = new RepositoriesReceivedEvent(cmisService);
      RepositoriesUnmarshaller unmarshaller = new RepositoriesUnmarshaller(cmisService);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url).send(callback);
   }

   /**
    * Get information about the CMIS repository, the optional capabilities it supports and its 
    * Access Control information if applicable.
    * 
    * On success response received, {@link RepositoryInfoReceivedEvent} event is fired
    * 
    * @param url url
    */
   public void getRepositoryInfo(String url)
   {
      CmisRepositoryInfo repositoryInfo = new CmisRepositoryInfo();
      RepositoryInfoReceivedEvent event = new RepositoryInfoReceivedEvent(repositoryInfo);
      RepositoryInfoUnmarshaller unmarshaller = new RepositoryInfoUnmarshaller(repositoryInfo);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url).send(callback);
   }

   /**
    * Returns the list of Object-Types defined for the Repository that are children of the specified type.
    * 
    * On success response received, {@link TypeChildrenReceivedEvent} event is fired
    * 
    * @param url url
    * @param includePropertyDefinitions include property definitions
    * @param maxItems max items
    * @param skipCount skip count
    */
   public void getTypeChildren(String url, boolean includePropertyDefinitions, int maxItems, int skipCount)
   {
      TypeCollection typeCollection = new TypeCollection();
      TypeChildrenReceivedEvent event = new TypeChildrenReceivedEvent(typeCollection);
      TypeChildrenUnmarshaller unmarshaller = new TypeChildrenUnmarshaller(typeCollection);

      String params = "";
      params += (maxItems < 0) ? "" : CmisArguments.MAX_ITEMS + "=" + maxItems + "&";
      params += (skipCount < 0) ? "" : CmisArguments.SKIP_COUNT + "=" + skipCount + "&";
      params += CmisArguments.INCLUDE_PROPERTY_DEFINITIONS + "=" + includePropertyDefinitions;
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url + "?" + params).send(callback);
   }

   /**
    * Get the set of descendant Object-Types defined for the Repository under the specified type.
    * 
    * On success response received, {@link TypeDescendantsRecievedEvent} event is fired
    * 
    * @param url url
    * @param typeId type id
    * @param depth depth
    * @param includePropertyDefinition include property definition
    */
   public void getTypeDescendants(String url, String typeId, int depth, boolean includePropertyDefinition)
   {
      TypeCollection typeCollection = new TypeCollection();
      TypeDescendantsRecievedEvent event = new TypeDescendantsRecievedEvent(typeCollection);
      TypeDescendantsUnmarshaller unmarshaller = new TypeDescendantsUnmarshaller(typeCollection);
      String params = "";
      params += (depth < -1) ? "" : CmisArguments.DEPTH + "=" + depth + "&";
      params += CmisArguments.INCLUDE_PROPERTY_DEFINITIONS + "=" + includePropertyDefinition;

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url + "?" + params).send(callback);
   }

   /**
    * Gets the definition of the specified Object-Type.
    * 
    * On success response received, {@link TypeDefinitionReceivedEvent} event is fired
    * 
    * @param url url
    */
   public void getTypeDefinition(String url)
   {
      TypeEntry type = new TypeEntry();
      TypeDefinitionReceivedEvent event = new TypeDefinitionReceivedEvent(type);
      TypeDefinitionUnmarshaller unmarshaller = new TypeDefinitionUnmarshaller(type);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url).send(callback);
   }

   /**
    * Get base types of the repository.
    * 
    * On success response received, {@link BaseTypesReceivedEvent} event is fired
    * 
    * @param url url
    * @param includePropertyDefinition include property definition
    */
   public void getBaseTypes(String url, boolean includePropertyDefinition)
   {
      TypeCollection typeCollection = new TypeCollection();
      BaseTypesReceivedEvent event = new BaseTypesReceivedEvent(typeCollection);
      TypeChildrenUnmarshaller unmarshaller = new TypeChildrenUnmarshaller(typeCollection);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET,
         url + "?" + CmisArguments.INCLUDE_PROPERTY_DEFINITIONS + "=" + includePropertyDefinition).send(callback);
   }

   /**
    * Get types as a plain list.
    * 
    * On success response received, {@link TypeListReceivedEvent} event is fired
    * 
    * @param url url
    * @param includePropertyDefinition include property definition
    */
   public void getTypeList(String href, boolean includePropertyDefinition)
   {
      TypeList typeList = new TypeList();
      TypeListReceivedEvent event = new TypeListReceivedEvent(typeList);
      TypeListUnmarshaller unmarshaller = new TypeListUnmarshaller(typeList);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(
         RequestBuilder.GET,
         href + "?" + CmisArguments.INCLUDE_PROPERTY_DEFINITIONS + "=" + includePropertyDefinition + "&"
            + CmisArguments.DEPTH + "=" + -1).send(callback);
   }

   /**
    * Create type.
    * 
    * On success response received, {@link TypeCreatedEvent} event is fired
    * 
    * @param url url
    * @param type type
    */
   public void addType(String url, CmisTypeDefinitionType createType)
   {
      TypeEntry type = new TypeEntry();
      TypeCreatedEvent event = new TypeCreatedEvent(type);
      TypeDefinitionUnmarshaller unmarshaller = new TypeDefinitionUnmarshaller(type);
      TypeDefinitionMarshaller marshaller = new TypeDefinitionMarshaller(createType);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.POST, url).data(marshaller).send(callback);
   }

   /**
    * Delete type by url.
    * 
    * On success response received, {@link TypeDeletedEvent} event is fired
    * 
    * @param url url
    */
   public void deleteType(String url)
   {
      TypeDeletedEvent event = new TypeDeletedEvent();
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event);
      AsyncRequest.build(RequestBuilder.POST, url).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.DELETE).send(
         callback);
   }

}