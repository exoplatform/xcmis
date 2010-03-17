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

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public abstract class ObjectServices
{
   /**
    * Instance.
    */
   private static ObjectServices instance;

   /**
    * @return {@link ObjectServices}
    */
   public static ObjectServices getInstance()
   {
      return instance;
   }

   /**
    * Get instance of {@link ObjectServices}.
    */
   protected ObjectServices()
   {
      instance = this;
   }

   /**
    * Creates a document object of the specified type (given by the cmis:objectTypeId property) 
    * in the (optionally) specified location.
    * 
    * On success response received, DocumentCreatedEvent event is fired.
    * 
    * @param url url
    * @param createDocument createDocument
    */
   public abstract void createDocument(String url, CreateDocument createDocument);

   /**
    * Creates a document object as a copy of the given source document in the (optionally) 
    * specified location.
    * 
    * @param url url
    * @param createDocumentFromSource createDocumentFromSource
    */
   public abstract void createDocumentFromSource(String url, CreateDocumentFromSource createDocumentFromSource);

   /**
    * Creates a folder object of the specified type in the specified location.
    * 
    * On success response received, FolderCreatedEvent event is fired.
    * 
    * @param url url
    * @param createFolder createFolder
    */
   public abstract void createFolder(String url, CreateFolder createFolder);

   /**
    * On success response received, EmptyDocumentCreatedEvent event is fired.
    * 
    * @param url url
    * @param createDocument createDocument
    */
   public abstract void createEmptyDocument(String url, CreateDocument createDocument);

   /**
    * Creates a relationship object of the specified type.
    * 
    * On success response received, RelationshipCreatedEvent event is fired.
    * 
    * @param url url
    * @param createRelationship createRelationship
    */
   public abstract void createRelationship(String url, CreateRelationship createRelationship);

   /**
    * Creates a policy object of the specified type
    * with pointed name and policy text.
    * 
    * On success response received, PolicyCreatedEvent event is fired.
    * 
    * @param url url
    * @param createPolicy createPolicy
    */
   public abstract void createPolicy(String url, CreatePolicy createPolicy);

   /**
    * Gets the specified information for the Object.
    * 
    * On success response received, 
    * {@link org.xcmis.gwtframework.client.services.object.event.AllowableActionsReceivedEvent 
    * AllowableActionsReceivedEvent} event is fired
    * 
    * @param url url
    */
   public abstract void getAllowableActions(String url);

   /**
    * Gets the specified information for the Object.
    * 
    * On success response received, 
    * {@link org.xcmis.gwtframework.client.services.object.event.ObjectReceivedEvent 
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
   public abstract void getObject(String url, String filter, EnumIncludeRelationships includeEnumIncludeRelationships,
      boolean includePolicyIds, String renditionFilter, boolean includeACL, boolean includeAllowableActions);

   /**
    * Gets the list of properties for an Object.
    * 
    * On success response received, 
    * {@link org.xcmis.gwtframework.client.services.object.event.PropertiesReceivedEvent 
    * PropertiesReceivedEvent} event is fired.
    * 
    * @param url url
    * @param filter filter
    */
   public abstract void getProperties(String url, String filter);

   /**
    * Gets the content stream for the specified Document object, 
    * or gets a rendition stream for a specified rendition of a document or folder object.
    * 
    * On success response received, ContentStreamReceivedEvent  event is fired.
    * 
    * @param url url
    * @param streamId streamId
    */
   public abstract void getContentStream(String url, String streamId);

   /**
    * Updates properties of the specified object.
    * 
    * On success response received, PropertiesUpdatedEvent event is fired.
    * 
    * @param url url
    * @param updateProperties updateProperties
    */
   public abstract void updateProperties(String url, UpdateProperties updateProperties);

   /**
    * Moves the specified file-able object from one folder to another.
    * 
    * On success response received, ObjectMovedEvent event is fired.
    * 
    * @param url url
    * @param moveObject moveObject
    */
   public abstract void moveObject(String url, MoveObject moveObject);

   /**
    * Deletes the specified object.
    * 
    * On success response received, ObjectDeletedEvent event is fired.
    * 
    * @param url url
    * @param deleteAllVersions deleteAllVersions
    */
   public abstract void deleteObject(String url, boolean deleteAllVersions);

   /**
    * @param url url
    * @param allVersions all versions
    * @param unfileObject unfile object
    * @param continueOnFailure continue on failure
    */
   public abstract void deleteTree(String url, boolean allVersions, EnumUnfileObject unfileObject,
      boolean continueOnFailure);

   /**
    * Sets the content stream for the specified Document object.
    * 
    * On success response received, ContentStreamSetEvent event is fired.
    * 
    * @param url url
    * @param contentStream content stream
    * @param overwriteFlag overwrite flag
    * @param changeToken change token
    */
   public abstract void setContentStream(String url, CmisContentStreamType contentStream, boolean overwriteFlag,
      String changeToken);

   /**
    * Deletes the content stream for the specified Document object.
    * 
    * @param url url
    * @param changeToken change token
    */
   public abstract void deleteContentStream(String url, String changeToken);

}
