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

package org.xcmis.gwtframework.client.services.object;

import org.xcmis.gwtframework.client.CmisArguments;
import org.xcmis.gwtframework.client.marshallers.CreateDocumentFromSourceMarshaller;
import org.xcmis.gwtframework.client.marshallers.CreateDocumentMarshaller;
import org.xcmis.gwtframework.client.marshallers.CreateFolderMarshaller;
import org.xcmis.gwtframework.client.marshallers.CreatePolicyMarshaller;
import org.xcmis.gwtframework.client.marshallers.CreateRelationshipMarshaller;
import org.xcmis.gwtframework.client.marshallers.MoveObjectMarshaller;
import org.xcmis.gwtframework.client.marshallers.UpdatePropertiesMarshaller;
import org.xcmis.gwtframework.client.model.CmisAllowableActionsType;
import org.xcmis.gwtframework.client.model.CmisContentStreamType;
import org.xcmis.gwtframework.client.model.EnumIncludeRelationships;
import org.xcmis.gwtframework.client.model.EnumUnfileObject;
import org.xcmis.gwtframework.client.model.actions.CreateDocument;
import org.xcmis.gwtframework.client.model.actions.CreateDocumentFromSource;
import org.xcmis.gwtframework.client.model.actions.CreateFolder;
import org.xcmis.gwtframework.client.model.actions.CreatePolicy;
import org.xcmis.gwtframework.client.model.actions.CreateRelationship;
import org.xcmis.gwtframework.client.model.actions.MoveObject;
import org.xcmis.gwtframework.client.model.actions.UpdateProperties;
import org.xcmis.gwtframework.client.model.restatom.AtomEntry;
import org.xcmis.gwtframework.client.services.object.event.AllowableActionsReceivedEvent;
import org.xcmis.gwtframework.client.services.object.event.ContentStreamDeletedEvent;
import org.xcmis.gwtframework.client.services.object.event.ContentStreamReceivedEvent;
import org.xcmis.gwtframework.client.services.object.event.ContentStreamSetEvent;
import org.xcmis.gwtframework.client.services.object.event.DocumentCreatedEvent;
import org.xcmis.gwtframework.client.services.object.event.DocumentFromSourceCreatedEvent;
import org.xcmis.gwtframework.client.services.object.event.EmptyDocumentCreatedEvent;
import org.xcmis.gwtframework.client.services.object.event.FolderCreatedEvent;
import org.xcmis.gwtframework.client.services.object.event.ObjectDeletedEvent;
import org.xcmis.gwtframework.client.services.object.event.ObjectMovedEvent;
import org.xcmis.gwtframework.client.services.object.event.ObjectReceivedEvent;
import org.xcmis.gwtframework.client.services.object.event.PolicyCreatedEvent;
import org.xcmis.gwtframework.client.services.object.event.PropertiesReceivedEvent;
import org.xcmis.gwtframework.client.services.object.event.PropertiesUpdatedEvent;
import org.xcmis.gwtframework.client.services.object.event.RelationshipCreatedEvent;
import org.xcmis.gwtframework.client.services.object.event.TreeDeletedEvent;
import org.xcmis.gwtframework.client.unmarshallers.AllowableActionsUnmarshaller;
import org.xcmis.gwtframework.client.unmarshallers.ContentStreamUnmarshaller;
import org.xcmis.gwtframework.client.unmarshallers.EntryUnmarshaller;
import org.xcmis.gwtframework.client.util.AsyncRequest;
import org.xcmis.gwtframework.client.util.AsyncRequestCallback;
import org.xcmis.gwtframework.client.util.HTTPHeader;
import org.xcmis.gwtframework.client.util.HTTPMethod;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.Random;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class ObjectServicesImpl extends ObjectServices
{
   /**
    * Event bus.
    */
   private HandlerManager eventBus;

   /**
    * @param eventBus eventBus
    */
   public ObjectServicesImpl(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }
   
   /**
    * @see org.xcmis.gwtframework.client.services.object.ObjectServices#createDocument(
    * java.lang.String, org.xcmis.gwtframework.client.model.actions.CreateDocument)
    * 
    * @param url url
    * @param createDocument createDocument
    */
   @Override
   public void createDocument(String url, CreateDocument createDocument)
   {
      AtomEntry document = new AtomEntry();
      DocumentCreatedEvent event = new DocumentCreatedEvent(document);
      EntryUnmarshaller unmarshaller = new EntryUnmarshaller(document);
      CreateDocumentMarshaller marshaller = new CreateDocumentMarshaller(createDocument);
      String params =
         (createDocument.getVersioningState() == null) ? "" : CmisArguments.VERSIONING_STATE + "="
            + createDocument.getVersioningState().value();

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).data(marshaller).send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.object.ObjectServices#createEmptyDocument(
    * java.lang.String, org.xcmis.gwtframework.client.model.actions.CreateDocument)
    * 
    * @param url url
    * @param createDocument createDocument
    */
   @Override
   public void createEmptyDocument(String url, CreateDocument createDocument)
   {
      AtomEntry document = new AtomEntry();
      EmptyDocumentCreatedEvent event = new EmptyDocumentCreatedEvent(document);
      EntryUnmarshaller unmarshaller = new EntryUnmarshaller(document);
      CreateDocumentMarshaller marshaller = new CreateDocumentMarshaller(createDocument);
      String params =
         (createDocument.getVersioningState() == null) ? "" : CmisArguments.VERSIONING_STATE + "="
            + createDocument.getVersioningState().value();
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).data(marshaller).send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.object.ObjectServices#createDocumentFromSource(
    * java.lang.String, org.xcmis.gwtframework.client.model.actions.CreateDocumentFromSource)
    * 
    * @param url url
    * @param createDocumentFromSource createDocumentFromSource
    */
   @Override
   public void createDocumentFromSource(String url, CreateDocumentFromSource createDocumentFromSource)
   {
      AtomEntry document = new AtomEntry();
      DocumentFromSourceCreatedEvent event = new DocumentFromSourceCreatedEvent(document);
      EntryUnmarshaller unmarshaller = new EntryUnmarshaller(document);
      CreateDocumentFromSourceMarshaller marshaller = 
         new CreateDocumentFromSourceMarshaller(createDocumentFromSource);

      String params =
         (createDocumentFromSource.getVersioningState() == null) 
          ? "" 
          : CmisArguments.VERSIONING_STATE + "="
            + createDocumentFromSource.getVersioningState().value();
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).data(marshaller).send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.object.ObjectServices#createFolder(
    * java.lang.String, org.xcmis.gwtframework.client.model.actions.CreateFolder)
    * 
    * @param url url
    * @param createFolder createFolder
    */
   @Override
   public void createFolder(String url, CreateFolder createFolder)
   {
      AtomEntry folder = new AtomEntry();
      FolderCreatedEvent event = new FolderCreatedEvent(folder);
      EntryUnmarshaller unmarshaller = new EntryUnmarshaller(folder);
      CreateFolderMarshaller marshaller = new CreateFolderMarshaller(createFolder);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.POST, url).data(marshaller).send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.object.ObjectServices#createRelationship(
    * java.lang.String, org.xcmis.gwtframework.client.model.actions.CreateRelationship)
    * 
    * @param url url
    * @param createRelationship createRelationship
    */
   @Override
   public void createRelationship(String url, CreateRelationship createRelationship)
   {
      AtomEntry relationship = new AtomEntry();
      RelationshipCreatedEvent event = new RelationshipCreatedEvent(relationship);
      EntryUnmarshaller unmarshaller = new EntryUnmarshaller(relationship);
      CreateRelationshipMarshaller marshaller = new CreateRelationshipMarshaller(createRelationship);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.POST, url).data(marshaller).send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.object.ObjectServices#createPolicy(
    * java.lang.String, org.xcmis.gwtframework.client.model.actions.CreatePolicy)
    * 
    * @param url url
    * @param createPolicy createPolicy
    */
   @Override
   public void createPolicy(String url, CreatePolicy createPolicy)
   {
      AtomEntry policy = new AtomEntry();
      EntryUnmarshaller unmarshaller = new EntryUnmarshaller(policy);
      PolicyCreatedEvent event = new PolicyCreatedEvent(policy);
      CreatePolicyMarshaller marshaller = new CreatePolicyMarshaller(createPolicy);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.POST, url).data(marshaller).send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.object.ObjectServices#getAllowableActions(
    * java.lang.String)
    * 
    * @param url url
    */
   @Override
   public void getAllowableActions(String url)
   {
      CmisAllowableActionsType allowableActions = new CmisAllowableActionsType();
      AllowableActionsReceivedEvent event = new AllowableActionsReceivedEvent(allowableActions);
      AllowableActionsUnmarshaller unmarshaller = new AllowableActionsUnmarshaller(allowableActions);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url).send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.object.ObjectServices#getObject(
    * java.lang.String, java.lang.String, 
    * org.xcmis.gwtframework.client.model.EnumIncludeRelationships, boolean, java.lang.String, 
    * boolean, boolean)
    * 
    * @param url url
    * @param filter filter
    * @param includeRelationships includeRelationships
    * @param includePolicyIds includePolicyIds
    * @param renditionFilter renditionFilter
    * @param includeACL includeACL
    * @param includeAllowableActions includeAllowableActions
    */
   @Override
   public void getObject(String url, String filter, EnumIncludeRelationships includeRelationships,
      boolean includePolicyIds, String renditionFilter, boolean includeACL, 
      boolean includeAllowableActions)
   {
      AtomEntry entry = new AtomEntry();
      ObjectReceivedEvent event = new ObjectReceivedEvent(entry);
      EntryUnmarshaller unmarshaller = new EntryUnmarshaller(entry);

      String params = "";
      params += (filter == null || filter.length() <= 0) 
                  ? "" 
                  : CmisArguments.FILTER + "=" + filter + "&";
      params += CmisArguments.INCLUDE_RELATIONSHIPS + "=" + includeRelationships.value() + "&";
      params +=
         (renditionFilter == null || renditionFilter.length() <= 0) 
            ? "" 
            : CmisArguments.RENDITION_FILTER + "=" + renditionFilter + "&";
      params += CmisArguments.INCLUDE_ACL + "=" + includeACL + "&";
      params += CmisArguments.INCLUDE_POLICY_IDS + "=" + includePolicyIds + "&";
      params += CmisArguments.INCLUDE_ALLOWABLE_ACTIONS + "=" + includeAllowableActions;

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).send(callback);
   }

  
   /**
    * @see org.xcmis.gwtframework.client.services.object.ObjectServices#getProperties(
    * java.lang.String, java.lang.String)
    * 
    * @param url url
    * @param filter filter
    */
   @Override
   public void getProperties(String url, String filter)
   {
      AtomEntry entry = new AtomEntry();
      PropertiesReceivedEvent event = new PropertiesReceivedEvent(entry);
      EntryUnmarshaller unmarshaller = new EntryUnmarshaller(entry);
      String params = (filter == null || filter.length() < 0) ? "" : CmisArguments.FILTER + "=" + filter;
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url + "?" + params).send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.object.ObjectServices#getContentStream(
    * java.lang.String, java.lang.String)
    * 
    * @param url url
    * @param streamId streamId
    */
   @Override
   public void getContentStream(String url, String streamId)
   {
      CmisContentStreamType contentStream = new CmisContentStreamType();
      ContentStreamReceivedEvent event = new ContentStreamReceivedEvent(contentStream);
      ContentStreamUnmarshaller unmarshaller = new ContentStreamUnmarshaller(contentStream);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url + "?nocache=" + String.valueOf(Random.nextDouble()))
                  .send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.object.ObjectServices#updateProperties(
    * java.lang.String, org.xcmis.gwtframework.client.model.actions.UpdateProperties)
    * 
    * @param url url
    * @param updateProperties updateProperties
    */
   @Override
   public void updateProperties(String url, UpdateProperties updateProperties)
   {
      AtomEntry entry = new AtomEntry();
      PropertiesUpdatedEvent event = new PropertiesUpdatedEvent(entry);
      EntryUnmarshaller unmarshaller = new EntryUnmarshaller(entry);
      UpdatePropertiesMarshaller marshaller = new UpdatePropertiesMarshaller(updateProperties);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.POST, url)
         .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PUT)
         .header(HTTPHeader.CONTENT_TYPE, "application/atom+xml;type=entry")
         .data(marshaller)
         .send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.object.ObjectServices#moveObject(
    * java.lang.String, org.xcmis.gwtframework.client.model.actions.MoveObject)
    * 
    * @param url url
    * @param moveObject moveObject
    */
   @Override
   public void moveObject(String url, MoveObject moveObject)
   {
      AtomEntry entry = new AtomEntry();
      ObjectMovedEvent event = new ObjectMovedEvent(entry);
      EntryUnmarshaller unmarshaller = new EntryUnmarshaller(entry);
      MoveObjectMarshaller marshaller = new MoveObjectMarshaller(moveObject);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.POST, url).data(marshaller).send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.object.ObjectServices#deleteObject(
    * java.lang.String, boolean)
    * 
    * @param url url
    * @param allVersions allVersions
    */
   @Override
   public void deleteObject(String url, boolean allVersions)
   {
      ObjectDeletedEvent event = new ObjectDeletedEvent();
      String params = CmisArguments.ALL_VERSIONS + "=" + allVersions;
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event);
      AsyncRequest.build(RequestBuilder.POST, url + "?" + params)
         .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.DELETE)
         .send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.object.ObjectServices#deleteTree(
    * java.lang.String, boolean, org.xcmis.gwtframework.client.model.EnumUnfileObject, boolean)
    * 
    * @param url url
    * @param allVersions allVersions
    * @param unfileObject unfileObject
    * @param continueOnFailure continueOnFailure
    */
   @Override
   public void deleteTree(String url, boolean allVersions, EnumUnfileObject unfileObject, 
      boolean continueOnFailure)
   {
      TreeDeletedEvent event = new TreeDeletedEvent();
      String params = CmisArguments.ALL_VERSIONS + "=" + allVersions + "&";
      params += (unfileObject == null) ? "" : CmisArguments.UNFILE_OBJECTS + "=" 
             + unfileObject.value() + "&";
      params += CmisArguments.CONTINUE_ON_FAILURE + "=" + continueOnFailure;
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event);
      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).header(
         HTTPHeader.X_HTTP_METHOD_OVERRIDE,
         HTTPMethod.DELETE).send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.object.ObjectServices#setContentStream(
    * java.lang.String, org.xcmis.gwtframework.client.model.CmisContentStreamType, 
    * boolean, java.lang.String)
    * 
    * @param url url
    * @param contentStream contentStream
    * @param overwriteFlag overwriteFlag
    * @param changeToken changeToken
    */
   @Override
   public void setContentStream(String url, CmisContentStreamType contentStream, boolean overwriteFlag,
      String changeToken)
   {
      ContentStreamSetEvent event = new ContentStreamSetEvent();
      String params = CmisArguments.OVERWRITE_FLAG + "=" + overwriteFlag + "&";
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event);
      AsyncRequest.build(RequestBuilder.POST, url + "?" + params)
         .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PUT)
         .header(HTTPHeader.CONTENTTYPE, contentStream.getMimeType())
         .data(contentStream.getStream())
         .send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.object.ObjectServices#deleteContentStream(
    * java.lang.String, java.lang.String)
    * 
    * @param url url
    * @param changeToken changeToken
    */
   @Override
   public void deleteContentStream(String url, String changeToken)
   {
      ContentStreamDeletedEvent event = new ContentStreamDeletedEvent();
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event);
      AsyncRequest.build(RequestBuilder.POST, url)
            .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.DELETE)
            .send(callback);
   }

}
