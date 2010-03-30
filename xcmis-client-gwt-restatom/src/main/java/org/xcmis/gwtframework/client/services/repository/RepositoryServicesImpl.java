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

package org.xcmis.gwtframework.client.services.repository;

import org.xcmis.gwtframework.client.CmisArguments;
import org.xcmis.gwtframework.client.marshallers.TypeDefinitionMarshaller;
import org.xcmis.gwtframework.client.model.repository.CmisRepositoryInfo;
import org.xcmis.gwtframework.client.model.restatom.CmisRepositories;
import org.xcmis.gwtframework.client.model.restatom.TypeCollection;
import org.xcmis.gwtframework.client.model.restatom.TypeEntry;
import org.xcmis.gwtframework.client.model.restatom.TypeList;
import org.xcmis.gwtframework.client.model.type.CmisTypeDefinitionType;
import org.xcmis.gwtframework.client.services.repository.event.BaseTypesReceivedEvent;
import org.xcmis.gwtframework.client.services.repository.event.RepositoriesReceivedEvent;
import org.xcmis.gwtframework.client.services.repository.event.RepositoryInfoReceivedEvent;
import org.xcmis.gwtframework.client.services.repository.event.TypeChildrenReceivedEvent;
import org.xcmis.gwtframework.client.services.repository.event.TypeCreatedEvent;
import org.xcmis.gwtframework.client.services.repository.event.TypeDefinitionReceivedEvent;
import org.xcmis.gwtframework.client.services.repository.event.TypeDeletedEvent;
import org.xcmis.gwtframework.client.services.repository.event.TypeDescendantsRecievedEvent;
import org.xcmis.gwtframework.client.services.repository.event.TypeListReceivedEvent;
import org.xcmis.gwtframework.client.unmarshallers.RepositoriesUnmarshaller;
import org.xcmis.gwtframework.client.unmarshallers.RepositoryInfoUnmarshaller;
import org.xcmis.gwtframework.client.unmarshallers.TypeChildrenUnmarshaller;
import org.xcmis.gwtframework.client.unmarshallers.TypeDefinitionUnmarshaller;
import org.xcmis.gwtframework.client.unmarshallers.TypeDescendantsUnmarshaller;
import org.xcmis.gwtframework.client.unmarshallers.TypeListUnmarshaller;
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
public class RepositoryServicesImpl extends RepositoryServices
{
   /**
    * Event bus.
    */
   private HandlerManager eventBus;

   /**
    * @param eventBus eventBus
    */
   public RepositoryServicesImpl(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }
   
   /**
    * @see org.xcmis.gwtframework.client.services.repository.RepositoryServices#getRepositories(
    * java.lang.String)
    * 
    * @param url url
    */
   @Override
   public void getRepositories(String url)
   {
      CmisRepositories cmisService = new CmisRepositories();
      RepositoriesReceivedEvent event = new RepositoriesReceivedEvent(cmisService);
      RepositoriesUnmarshaller unmarshaller = new RepositoriesUnmarshaller(cmisService);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url).send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.repository.RepositoryServices#getRepositoryInfo(
    * java.lang.String)
    * 
    * @param url url
    */
   @Override
   public void getRepositoryInfo(String url)
   {
      CmisRepositoryInfo repositoryInfo = new CmisRepositoryInfo();
      RepositoryInfoReceivedEvent event = new RepositoryInfoReceivedEvent(repositoryInfo);
      RepositoryInfoUnmarshaller unmarshaller = new RepositoryInfoUnmarshaller(repositoryInfo);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url).send(callback);
   }

   
   /**
    * @see org.xcmis.gwtframework.client.services.repository.RepositoryServices#getTypeChildren(
    * java.lang.String, boolean, int, int)
    * 
    * @param url url
    * @param includePropertyDefinitions include property definitions
    * @param maxItems max items
    * @param skipCount skip count
    */
   @Override
   public void getTypeChildren(String url, boolean includePropertyDefinitions, int maxItems,
      int skipCount)
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
    * @see org.xcmis.gwtframework.client.services.repository.RepositoryServices#getTypeDescendants(
    * java.lang.String, java.lang.String, int, boolean)
    * 
    * @param url url
    * @param typeId type id
    * @param depth depth
    * @param includePropertyDefinition include property definition
    */
   @Override
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
    * @see org.xcmis.gwtframework.client.services.repository.RepositoryServices#getTypeDefinition(
    * java.lang.String)
    * 
    * @param url url
    */
   @Override
   public void getTypeDefinition(String url)
   {
      TypeEntry type = new TypeEntry();
      TypeDefinitionReceivedEvent event = new TypeDefinitionReceivedEvent(type);
      TypeDefinitionUnmarshaller unmarshaller = new TypeDefinitionUnmarshaller(type);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url).send(callback);
   }
   
   /**
    * @see org.xcmis.gwtframework.client.services.repository.RepositoryServices#getBaseTypes(
    * java.lang.String, boolean)
    * 
    * @param url url
    * @param includePropertyDefinition include property definition
    */
   @Override
   public void getBaseTypes(String url, boolean includePropertyDefinition)
   {
      TypeCollection typeCollection = new TypeCollection();
      BaseTypesReceivedEvent event = new BaseTypesReceivedEvent(typeCollection);
      TypeChildrenUnmarshaller unmarshaller = new TypeChildrenUnmarshaller(typeCollection);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(
         RequestBuilder.GET,
         url + "?" + CmisArguments.INCLUDE_PROPERTY_DEFINITIONS + "=" + includePropertyDefinition)
         .send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.repository.RepositoryServices#getTypeList(
    * java.lang.String, boolean)
    * 
    * @param href href
    * @param includePropertyDefinition include property definition
    */
   @Override
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
    * @see org.xcmis.gwtframework.client.services.repository.RepositoryServices#addType(
    * java.lang.String, org.xcmis.gwtframework.client.model.type.CmisTypeDefinitionType)
    * 
    * @param url url
    * @param createType create type
    */
   @Override
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
    * @see org.xcmis.gwtframework.client.services.repository.RepositoryServices#deleteType(
    * java.lang.String)
    * 
    * @param url url
    */
   @Override
   public void deleteType(String url)
   {
      TypeDeletedEvent event = new TypeDeletedEvent();
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event);
      AsyncRequest.build(RequestBuilder.POST, url)
         .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.DELETE)
         .send(callback);
   }

}
