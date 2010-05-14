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

package org.xcmis.client.gwt.client.service.object;

import org.xcmis.client.gwt.client.CmisArguments;
import org.xcmis.client.gwt.client.marshallers.CreateDocumentFromSourceMarshaller;
import org.xcmis.client.gwt.client.marshallers.CreateDocumentMarshaller;
import org.xcmis.client.gwt.client.marshallers.CreateFolderMarshaller;
import org.xcmis.client.gwt.client.marshallers.CreatePolicyMarshaller;
import org.xcmis.client.gwt.client.marshallers.CreateRelationshipMarshaller;
import org.xcmis.client.gwt.client.marshallers.MoveObjectMarshaller;
import org.xcmis.client.gwt.client.marshallers.UpdatePropertiesMarshaller;
import org.xcmis.client.gwt.client.model.AllowableActions;
import org.xcmis.client.gwt.client.model.CmisContentStreamType;
import org.xcmis.client.gwt.client.model.EnumIncludeRelationships;
import org.xcmis.client.gwt.client.model.EnumUnfileObject;
import org.xcmis.client.gwt.client.model.actions.CreateDocument;
import org.xcmis.client.gwt.client.model.actions.CreateDocumentFromSource;
import org.xcmis.client.gwt.client.model.actions.CreateFolder;
import org.xcmis.client.gwt.client.model.actions.CreatePolicy;
import org.xcmis.client.gwt.client.model.actions.CreateRelationship;
import org.xcmis.client.gwt.client.model.actions.MoveObject;
import org.xcmis.client.gwt.client.model.actions.UpdateProperties;
import org.xcmis.client.gwt.client.model.restatom.AtomEntry;
import org.xcmis.client.gwt.client.rest.AsyncRequest;
import org.xcmis.client.gwt.client.rest.AsyncRequestCallback;
import org.xcmis.client.gwt.client.rest.HTTPHeader;
import org.xcmis.client.gwt.client.rest.HTTPMethod;
import org.xcmis.client.gwt.client.service.object.event.AllowableActionsReceivedEvent;
import org.xcmis.client.gwt.client.service.object.event.ContentStreamDeletedEvent;
import org.xcmis.client.gwt.client.service.object.event.ContentStreamReceivedEvent;
import org.xcmis.client.gwt.client.service.object.event.ContentStreamSetEvent;
import org.xcmis.client.gwt.client.service.object.event.DocumentCreatedEvent;
import org.xcmis.client.gwt.client.service.object.event.DocumentFromSourceCreatedEvent;
import org.xcmis.client.gwt.client.service.object.event.EmptyDocumentCreatedEvent;
import org.xcmis.client.gwt.client.service.object.event.FolderCreatedEvent;
import org.xcmis.client.gwt.client.service.object.event.ObjectDeletedEvent;
import org.xcmis.client.gwt.client.service.object.event.ObjectMovedEvent;
import org.xcmis.client.gwt.client.service.object.event.ObjectReceivedEvent;
import org.xcmis.client.gwt.client.service.object.event.PolicyCreatedEvent;
import org.xcmis.client.gwt.client.service.object.event.PropertiesReceivedEvent;
import org.xcmis.client.gwt.client.service.object.event.PropertiesUpdatedEvent;
import org.xcmis.client.gwt.client.service.object.event.RelationshipCreatedEvent;
import org.xcmis.client.gwt.client.service.object.event.TreeDeletedEvent;
import org.xcmis.client.gwt.client.unmarshallers.AllowableActionsUnmarshaller;
import org.xcmis.client.gwt.client.unmarshallers.ContentStreamUnmarshaller;
import org.xcmis.client.gwt.client.unmarshallers.EntryUnmarshaller;

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
public class ObjectService
{
   /**
    * Event bus.
    */
   private HandlerManager eventBus;

   /**
    * @param eventBus eventBus
    */
   public ObjectService(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }

   /**
    * Creates a document object of the specified type (given by the cmis:objectTypeId property) 
    * in the (optionally) specified location.
    * 
    * On success response received, {@link DocumentCreatedEvent} event is fired.
    * 
    * @param url url
    * @param createDocument createDocument
    */
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
    * On success response received, {@link EmptyDocumentCreatedEvent} event is fired.
    * 
    * @param url url
    * @param createDocument createDocument
    */
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
    * Creates a document object as a copy of the given source document in the (optionally) 
    * specified location.
    * 
    * * On success response received, {@link DocumentFromSourceCreatedEvent} event is fired.
    * 
    * @param url url
    * @param createDocumentFromSource createDocumentFromSource
    */
   public void createDocumentFromSource(String url, CreateDocumentFromSource createDocumentFromSource)
   {
      AtomEntry document = new AtomEntry();
      DocumentFromSourceCreatedEvent event = new DocumentFromSourceCreatedEvent(document);
      EntryUnmarshaller unmarshaller = new EntryUnmarshaller(document);
      CreateDocumentFromSourceMarshaller marshaller = new CreateDocumentFromSourceMarshaller(createDocumentFromSource);

      String params =
         (createDocumentFromSource.getVersioningState() == null) ? "" : CmisArguments.VERSIONING_STATE + "="
            + createDocumentFromSource.getVersioningState().value();
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).data(marshaller).send(callback);
   }

   /**
    * Creates a folder object of the specified type in the specified location.
    * 
    * On success response received, {@link FolderCreatedEvent} event is fired.
    * 
    * @param url url
    * @param createFolder createFolder
    */
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
    * Creates a relationship object of the specified type.
    * 
    * On success response received, {@link RelationshipCreatedEvent} event is fired.
    * 
    * @param url url
    * @param createRelationship createRelationship
    */
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
    * Creates a policy object of the specified type
    * with pointed name and policy text.
    * 
    * On success response received, {@link PolicyCreatedEvent} event is fired.
    * 
    * @param url url
    * @param createPolicy createPolicy
    */
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
    * Gets the specified information for the Object.
    * 
    * On success response received, 
    * {@link org.xcmis.client.gwt.client.service.object.event.AllowableActionsReceivedEvent 
    * AllowableActionsReceivedEvent} event is fired
    * 
    * @param url url
    */
   public void getAllowableActions(String url)
   {
      AllowableActions allowableActions = new AllowableActions();
      AllowableActionsReceivedEvent event = new AllowableActionsReceivedEvent(allowableActions);
      AllowableActionsUnmarshaller unmarshaller = new AllowableActionsUnmarshaller(allowableActions);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url).send(callback);
   }

   /**
    * Gets the specified information for the Object.
    * 
    * On success response received, 
    * {@link org.xcmis.client.gwt.client.service.object.event.ObjectReceivedEvent 
    * ObjectReceivedEvent} event is fired
    * 
    * @param url url
    * @param filter filter
    * @param includeEnumIncludeRelationships includeEnumIncludeRelationships
    * @param includePolicyIds includePolicyIds
    * @param renditionFilter renditionFilter
    * @param includeACL includeACL
    * @param includeAllowableActions includeAllowableActions
    */
   public void getObject(String url, String filter, EnumIncludeRelationships includeRelationships,
      boolean includePolicyIds, String renditionFilter, boolean includeACL, boolean includeAllowableActions)
   {
      AtomEntry entry = new AtomEntry();
      ObjectReceivedEvent event = new ObjectReceivedEvent(entry);
      EntryUnmarshaller unmarshaller = new EntryUnmarshaller(entry);

      String params = "";
      params += (filter == null || filter.length() <= 0) ? "" : CmisArguments.FILTER + "=" + filter + "&";
      params += CmisArguments.INCLUDE_RELATIONSHIPS + "=" + includeRelationships.value() + "&";
      params +=
         (renditionFilter == null || renditionFilter.length() <= 0) ? "" : CmisArguments.RENDITION_FILTER + "="
            + renditionFilter + "&";
      params += CmisArguments.INCLUDE_ACL + "=" + includeACL + "&";
      params += CmisArguments.INCLUDE_POLICY_IDS + "=" + includePolicyIds + "&";
      params += CmisArguments.INCLUDE_ALLOWABLE_ACTIONS + "=" + includeAllowableActions;

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url + "?" + params).send(callback);
   }

   /**
    * Gets the list of properties for an Object.
    * 
    * On success response received, {@link PropertiesReceivedEvent} event is fired.
    * 
    * @param url url
    * @param filter filter
    */
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
    * Gets the content stream for the specified Document object, 
    * or gets a rendition stream for a specified rendition of a document or folder object.
    * 
    * On success response received, {@link ContentStreamReceivedEvent}  event is fired.
    * 
    * @param url url
    * @param streamId streamId
    */
   public void getContentStream(String url, String streamId)
   {
      CmisContentStreamType contentStream = new CmisContentStreamType();
      ContentStreamReceivedEvent event = new ContentStreamReceivedEvent(contentStream);
      ContentStreamUnmarshaller unmarshaller = new ContentStreamUnmarshaller(contentStream);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url + "?nocache=" + String.valueOf(Random.nextDouble())).send(callback);
   }

   /**
    * Updates properties of the specified object.
    * 
    * On success response received, {@link PropertiesUpdatedEvent} event is fired.
    * 
    * @param url url
    * @param updateProperties updateProperties
    */
   public void updateProperties(String url, UpdateProperties updateProperties)
   {
      AtomEntry entry = new AtomEntry();
      PropertiesUpdatedEvent event = new PropertiesUpdatedEvent(entry);
      EntryUnmarshaller unmarshaller = new EntryUnmarshaller(entry);
      UpdatePropertiesMarshaller marshaller = new UpdatePropertiesMarshaller(updateProperties);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.POST, url).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PUT).header(
         HTTPHeader.CONTENT_TYPE, "application/atom+xml;type=entry").data(marshaller).send(callback);
   }

   /**
    * Moves the specified file-able object from one folder to another.
    * 
    * On success response received, {@link ObjectMovedEvent} event is fired.
    * 
    * @param url url
    * @param moveObject moveObject
    */
   public void moveObject(String url, MoveObject moveObject)
   {
      AtomEntry entry = new AtomEntry();
      ObjectMovedEvent event = new ObjectMovedEvent(entry);
      EntryUnmarshaller unmarshaller = new EntryUnmarshaller(entry);
      MoveObjectMarshaller marshaller = new MoveObjectMarshaller(moveObject);
      String param = (moveObject.getSourceFolderId() == null)? "" : CmisArguments.SOURCE_FOLDER_ID + "="+moveObject.getSourceFolderId();
      
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.POST, url+"?"+param).data(marshaller).send(callback);
   }

   /**
    * Deletes the specified object.
    * 
    * On success response received, {@link ObjectDeletedEvent} event is fired.
    * 
    * @param url url
    * @param deleteAllVersions deleteAllVersions
    */
   public void deleteObject(String url, boolean allVersions)
   {
      ObjectDeletedEvent event = new ObjectDeletedEvent();
      String params = CmisArguments.ALL_VERSIONS + "=" + allVersions;
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event);
      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE,
         HTTPMethod.DELETE).send(callback);
   }

   /**
    * Delete tree of objects in specified folder
    * 
    * On success results received {@link TreeDeletedEvent} event is fired
    * 
    * @param url url
    * @param allVersions all versions
    * @param unfileObject unfile object
    * @param continueOnFailure continue on failure
    */
   public void deleteTree(String url, boolean allVersions, EnumUnfileObject unfileObject, boolean continueOnFailure)
   {
      TreeDeletedEvent event = new TreeDeletedEvent();
      String params = CmisArguments.ALL_VERSIONS + "=" + allVersions + "&";
      params += (unfileObject == null) ? "" : CmisArguments.UNFILE_OBJECTS + "=" + unfileObject.value() + "&";
      params += CmisArguments.CONTINUE_ON_FAILURE + "=" + continueOnFailure;
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event);
      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE,
         HTTPMethod.DELETE).send(callback);
   }

   /**
    * Sets the content stream for the specified Document object.
    * 
    * On success response received, {@link ContentStreamSetEvent} event is fired.
    * 
    * @param url
    * @param contentStream content stream
    * @param overwriteFlag overwrite flag
    * @param changeToken change token
    */
   public void setContentStream(String url, CmisContentStreamType contentStream, boolean overwriteFlag,
      String changeToken)
   {
      ContentStreamSetEvent event = new ContentStreamSetEvent();
      String params = CmisArguments.OVERWRITE_FLAG + "=" + overwriteFlag + "&";
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event);
      AsyncRequest.build(RequestBuilder.POST, url + "?" + params).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE,
         HTTPMethod.PUT).header(HTTPHeader.CONTENTTYPE, contentStream.getMimeType()).data(contentStream.getStream())
         .send(callback);
   }

   /**
    * Deletes the content stream for the specified Document object.
    * 
    * On success result received {@link ContentStreamDeletedEvent} is fired
    * 
    * @param url url
    * @param changeToken change token
    */
   public void deleteContentStream(String url, String changeToken)
   {
      ContentStreamDeletedEvent event = new ContentStreamDeletedEvent();
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event);
      AsyncRequest.build(RequestBuilder.POST, url).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.DELETE).send(
         callback);
   }

}
