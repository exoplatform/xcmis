/**
 * Copyright (C) 2003-2009 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */

package org.xcmis.client.gwt.restatom.sample.client.application;

import org.xcmis.client.gwt.client.CMIS;
import org.xcmis.client.gwt.client.model.EnumBaseObjectTypeIds;
import org.xcmis.client.gwt.client.model.EnumIncludeRelationships;
import org.xcmis.client.gwt.client.model.actions.CreateDocument;
import org.xcmis.client.gwt.client.model.actions.CreateFolder;
import org.xcmis.client.gwt.client.model.property.CmisProperties;
import org.xcmis.client.gwt.client.model.property.IdProperty;
import org.xcmis.client.gwt.client.model.property.Property;
import org.xcmis.client.gwt.client.model.property.StringProperty;
import org.xcmis.client.gwt.client.model.repository.CmisRepositoryInfo;
import org.xcmis.client.gwt.client.model.restatom.AtomEntry;
import org.xcmis.client.gwt.client.model.restatom.AtomLink;
import org.xcmis.client.gwt.client.model.restatom.EnumCollectionType;
import org.xcmis.client.gwt.client.model.restatom.EnumLinkRelation;
import org.xcmis.client.gwt.client.service.navigation.NavigationService;
import org.xcmis.client.gwt.client.service.navigation.event.ChildrenReceivedEvent;
import org.xcmis.client.gwt.client.service.navigation.event.ChildrenReceivedHandler;
import org.xcmis.client.gwt.client.service.object.ObjectService;
import org.xcmis.client.gwt.client.service.object.event.DocumentCreatedEvent;
import org.xcmis.client.gwt.client.service.object.event.DocumentCreatedHandler;
import org.xcmis.client.gwt.client.service.object.event.FolderCreatedEvent;
import org.xcmis.client.gwt.client.service.object.event.FolderCreatedHandler;
import org.xcmis.client.gwt.client.service.repository.RepositoryService;
import org.xcmis.client.gwt.client.service.repository.event.RepositoriesReceivedEvent;
import org.xcmis.client.gwt.client.service.repository.event.RepositoriesReceivedHandler;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Anna Zhuleva</a>
 * @version $Id:
 */

public class SamplePresenter implements RepositoriesReceivedHandler, ChildrenReceivedHandler, FolderCreatedHandler,
   DocumentCreatedHandler
{
   private String url = "http://xcmis.org/rest/cmisatom";
   
   private RepositoryService repositoryService;
   
   private ObjectService objectService;
   
   private NavigationService navigationService;

   private CmisRepositoryInfo currentRepository;

   private int newFolderCount = 0;

   private int newDocumentCount = 0;

   public interface Display
   {
      void showObjects(List<AtomEntry> entries);

      void addNewObject(AtomEntry entry);

      String removeObject();

      void displayRepository(CmisRepositoryInfo repositoryInfo);

      HasClickHandlers getCreateFolderButton();

      HasClickHandlers getCreateDocumentButton();

      HasClickHandlers getDeleteButton();
      
      void setEnableCreateButtons(boolean enable);
      
      void setEnableDeleteButton(boolean enable);

   }

   private Display display;

   private HandlerManager eventBus;

   /**
    * @param eventBus
    */
   public SamplePresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      repositoryService = new RepositoryService(eventBus);
      navigationService = new NavigationService(eventBus);
      objectService = new ObjectService(eventBus);
   }

   /**
    * @param d
    */
   public void bindDisplay(Display d)
   {
      display = d;
      eventBus.addHandler(RepositoriesReceivedEvent.TYPE, this);
      eventBus.addHandler(ChildrenReceivedEvent.TYPE, this);
      eventBus.addHandler(FolderCreatedEvent.TYPE, this);
      eventBus.addHandler(DocumentCreatedEvent.TYPE, this);

      display.getCreateFolderButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent arg0)
         {
            createFolder();
         }
      });

      display.getCreateDocumentButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent arg0)
         {
            createDocument();
         }
      });

      display.getDeleteButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            String url = display.removeObject();
            if (url != null){
               deleteObject(url);
            }
         }
      });
      
      /*Get repositories provided by service pointed by url*/
      repositoryService.getRepositories(url);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.repository.event.RepositoriesReceivedHandler#onRepositoriesReceived(org.xcmis.gwtframework.client.services.repository.event.RepositoriesReceivedEvent)
    */
   public void onRepositoriesReceived(RepositoriesReceivedEvent event)
   {
      int maxItems = 10;
      int skipCount = 0;
      EnumIncludeRelationships includeRelationships = EnumIncludeRelationships.NONE;
      String renditionFilter = "*";
      boolean includeAllowableActions = true;
      boolean includePathSegment = false;

      List<CmisRepositoryInfo> repositories = event.getRepositories().getRepositories();
      for (CmisRepositoryInfo repository : event.getRepositories().getRepositories())
      {
         display.displayRepository(repository);
      }
      
      if (repositories.size() > 0)
      {
         currentRepository = repositories.get(0);
         String rootFolderUrl = currentRepository.getCollectionValue(EnumCollectionType.ROOT);
         navigationService.getChildren(rootFolderUrl, maxItems, skipCount, null, includeRelationships,
            renditionFilter, includeAllowableActions, includePathSegment);
         display.setEnableCreateButtons(true);
      } 

   }

   /**
    * @see org.xcmis.gwtframework.client.services.navigation.event.ChildrenReceivedHandler#onChildrenReceived(org.xcmis.gwtframework.client.services.navigation.event.ChildrenReceivedEvent)
    */
   public void onChildrenReceived(ChildrenReceivedEvent event)
   {
      int count = event.getEntries().getEntries().size();
      if (count > 0)
      {
         display.showObjects(event.getEntries().getEntries());
      }
      else
      {
         Window.alert("No objects found.");
      }
   }

   /**
    * Create new folder object
    */
   private void createFolder()
   {
      CreateFolder createFolder = new CreateFolder();
      createFolder.setRepositoryId(currentRepository.getRepositoryId());
      createFolder.setFolderId(currentRepository.getRootFolderId());

      newFolderCount++;
      CmisProperties properties = new CmisProperties(new HashMap<String, Property<?>>());
      String propertyId = CMIS.CMIS_NAME;
      String name = "Folder " + newFolderCount;
      properties.getProperties().put(propertyId, new StringProperty(propertyId, propertyId, propertyId, propertyId, name));
      propertyId = CMIS.CMIS_OBJECT_TYPE_ID;
      properties.getProperties().put(propertyId, new IdProperty(propertyId, propertyId, propertyId, propertyId, CMIS.BASE_TYPE_FOLDER));
      createFolder.setProperties(properties);

      String rootFolderUrl = currentRepository.getCollectionValue(EnumCollectionType.ROOT);
      objectService.createFolder(rootFolderUrl, createFolder);
   }

   /**
    * Create new document object
    */
   private void createDocument()
   {
      CreateDocument createDocument = new CreateDocument();
      createDocument.setRepositoryId(currentRepository.getRepositoryId());
      createDocument.setFolderId(currentRepository.getRootFolderId());

      newDocumentCount++;

      CmisProperties properties = new CmisProperties(new HashMap<String, Property<?>>());
      String propertyId = CMIS.CMIS_NAME;
      String name = "Document " + newDocumentCount;
      properties.getProperties().put(propertyId, new StringProperty(propertyId, propertyId, propertyId, propertyId, name));
      propertyId = CMIS.CMIS_OBJECT_TYPE_ID;
      properties.getProperties().put(propertyId, new IdProperty(propertyId, propertyId, propertyId, propertyId, CMIS.BASE_TYPE_DOCUMENT));
      createDocument.setProperties(properties);

      String rootFolderUrl = currentRepository.getCollectionValue(EnumCollectionType.ROOT);

      objectService.createDocument(rootFolderUrl, createDocument);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.object.event.FolderCreatedHandler#onFolderCreated(org.xcmis.gwtframework.client.services.object.event.FolderCreatedEvent)
    */
   public void onFolderCreated(FolderCreatedEvent event)
   {
      display.addNewObject(event.getFolder());
   }

   /**
    * @see org.xcmis.gwtframework.client.services.object.event.DocumentCreatedHandler#onDocumentCreated(org.xcmis.gwtframework.client.services.object.event.DocumentCreatedEvent)
    */
   public void onDocumentCreated(DocumentCreatedEvent event)
   {
      display.addNewObject(event.getDocument());
   }

   /**
    * Delete object
    * 
    * @param url
    */
   public void deleteObject(String url)
   {
      objectService.deleteObject(url, true);
   }

   /**
    * Get url, by which object will be deleted
    * 
    * @param baseObjectTypeIds
    * @param links
    * @return String
    */
   public String getUrlForDelete(EnumBaseObjectTypeIds baseObjectTypeIds, List<AtomLink> links)
   {
      EnumLinkRelation relation = null;

      if (baseObjectTypeIds.equals(EnumBaseObjectTypeIds.CMIS_FOLDER))
      {
         relation = EnumLinkRelation.DOWN;
      }
      else if (baseObjectTypeIds.equals(EnumBaseObjectTypeIds.CMIS_DOCUMENT))
      {
         /* If object is document get self */
         relation = EnumLinkRelation.SELF;
      }
      /* Get link with pointed relation*/
      for (AtomLink link : links)
      {
         if (link.getRelation().equals(relation))
         {
            return link.getHref();
         }
      }
      return null;
   }

}
