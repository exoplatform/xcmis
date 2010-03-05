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

package org.xcmis.core.impl;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.core.NavigationService;
import org.xcmis.core.RepositoryService;
import org.xcmis.core.impl.object.RenditionFilter;
import org.xcmis.core.impl.property.PropertyFilter;
import org.xcmis.core.impl.property.PropertyService;
import org.xcmis.messaging.CmisObjectInFolderContainerType;
import org.xcmis.messaging.CmisObjectInFolderType;
import org.xcmis.messaging.CmisObjectParentsType;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.Repository;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.object.CmisObject;
import org.xcmis.spi.object.CmisObjectInFolder;
import org.xcmis.spi.object.CmisObjectInFolderContainer;
import org.xcmis.spi.object.CmisObjectInFolderContainerImpl;
import org.xcmis.spi.object.CmisObjectInFolderImpl;
import org.xcmis.spi.object.CmisObjectInFolderList;
import org.xcmis.spi.object.CmisObjectInFolderListImpl;
import org.xcmis.spi.object.CmisObjectList;
import org.xcmis.spi.object.CmisObjectListImpl;
import org.xcmis.spi.object.CmisObjectParents;
import org.xcmis.spi.object.CmisObjectParentsImpl;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.ItemsIterator;
import org.xcmis.spi.object.RenditionManager;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Implementation of the NavigationService.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: NavigationServiceImpl.java 2118 2009-07-13 20:40:48Z andrew00x
 *          $
 */
public class NavigationServiceImpl extends CmisObjectProducer implements NavigationService
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(NavigationServiceImpl.class);

   /** @see RepositoryService. */
   protected final RepositoryService repositoryService;

   /**
    * Construct instance <tt>NavigationServiceImpl</tt>.
    * 
    * @param repositoryService the repository service for getting repositories
    * @param propertyService the property service for getting properties
    */
   public NavigationServiceImpl(RepositoryService repositoryService, PropertyService propertyService)
   {
      super(propertyService);
      this.repositoryService = repositoryService;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectList getCheckedOutDocs(String repositoryId, String folderId,
      boolean includeAllowableActions, EnumIncludeRelationships includeRelationships, String propertyFilter,
      String renditionFilter, String orderBy, int maxItems, int skipCount, boolean includeObjectInfo) throws FilterNotValidException,
      RepositoryException
   {

      if (LOG.isDebugEnabled())
         LOG.debug("Get checkedout documents, repository " + repositoryId + ", object " + folderId);

      if (skipCount < 0)
      {
         String msg = "skipCount parameter is negative.";
         throw new InvalidArgumentException(msg);
      }
      if (maxItems < 0)
      {
         String msg = "maxItems parameter is negative.";
         throw new InvalidArgumentException(msg);
      }

      Repository repository = repositoryService.getRepository(repositoryId);
      ItemsIterator<Entry> items = repository.getCheckedOutDocuments(folderId);
      try
      {
         if (skipCount > 0)
            items.skip(skipCount);
      }
      catch (NoSuchElementException nse)
      {
         String msg = "skipCount parameter is greater then total number of argument";
         throw new InvalidArgumentException(msg);
      }

      CmisObjectList list = new CmisObjectListImpl();
      int count = 0;
      RenditionManager renditionManager = repository.getRenditionManager();
      while (items.hasNext() && count < maxItems)
      {
         Entry entry = items.next();
         CmisObject cmis =
            getCmisObject(entry, includeAllowableActions, includeRelationships, false, false, new PropertyFilter(
               propertyFilter), new RenditionFilter(renditionFilter), renditionManager, includeObjectInfo);
         list.getObjects().add(cmis);
         count++;
      }

      list.setHasMoreItems(items.hasNext());
      long total = items.size();
      if (total != -1)
         list.setNumItems(BigInteger.valueOf(total));
      return list;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectInFolderList getChildren(String repositoryId, String folderId, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, boolean includePathSegments, String propertyFilter,
      String renditionFilter, String orderBy, int maxItems, int skipCount, boolean includeObjectInfo) throws FilterNotValidException,
      RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Get children, repository " + repositoryId + ", object " + folderId);

      if (skipCount < 0)
      {
         String msg = "skipCount parameter is negative.";
         throw new InvalidArgumentException(msg);
      }
      if (maxItems < 0)
      {
         String msg = "maxItems parameter is negative.";
         throw new InvalidArgumentException(msg);
      }

      Repository repository = repositoryService.getRepository(repositoryId);
      Entry folder = repository.getObjectById(folderId);

      ItemsIterator<Entry> items;
      try
      {
         items = folder.getChildren(orderBy);
      }
      catch (UnsupportedOperationException usoe)
      {
         // Object is not a Folder.
         throw new InvalidArgumentException(usoe.getMessage());
      }

      try
      {
         if (skipCount > 0)
            items.skip(skipCount);
      }
      catch (NoSuchElementException nse)
      {
         String msg = "skipCount parameter is greater then total number of argument";
         throw new InvalidArgumentException(msg);
      }

      CmisObjectInFolderList list = new CmisObjectInFolderListImpl();
      RenditionManager renditionManager = repository.getRenditionManager();
      int count = 0;
      while (items.hasNext() && count < maxItems)
      {
         Entry obj = items.next();
         CmisObject cmis =
            getCmisObject(obj, includeAllowableActions, includeRelationships, false, false, new PropertyFilter(
               propertyFilter), new RenditionFilter(renditionFilter), renditionManager, includeObjectInfo);
         CmisObjectInFolder objectInFolder = new CmisObjectInFolderImpl();
         objectInFolder.setObject(cmis);
         objectInFolder.setPathSegment(obj.getName());
         list.getObjects().add(objectInFolder);
         count++;
      }

      // Indicate that we have some more results.
      list.setHasMoreItems(items.hasNext());
      long total = items.size();
      if (total != -1)
         list.setNumItems(BigInteger.valueOf(total));
      return list;
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisObjectInFolderContainer> getDescendants(String repositoryId, String folderId, int depth,
      boolean includeAllowableActions, EnumIncludeRelationships includeRelationships, boolean includePathSegments,
      String propertyFilter, String renditionFilter, boolean includeObjectInfo) throws FilterNotValidException, RepositoryException
   {

      if (LOG.isDebugEnabled())
         LOG.debug("Get descendants, repository " + repositoryId + ", object " + folderId + ", depth " + depth);

      if (depth == 0 || depth < -1)
      {
         String msg = "Depth must be greater or equal 1 or -1. But got " + depth;
         throw new InvalidArgumentException(msg);
      }

      Repository repository = repositoryService.getRepository(repositoryId);
      Entry folder = repository.getObjectById(folderId);
      ItemsIterator<Entry> children;
      try
      {
         children = folder.getChildren();
      }
      catch (UnsupportedOperationException usoe)
      {
         // Object is not fileable.
         throw new InvalidArgumentException(usoe.getMessage());
      }
      List<CmisObjectInFolderContainer> list = new ArrayList<CmisObjectInFolderContainer>();
      RenditionManager renditionManager = repository.getRenditionManager();
      while (children.hasNext())
      {
         list.add(getDescendants(children.next(), null, depth != -1 ? depth - 1 : depth, includeAllowableActions,
            includeRelationships, includePathSegments, new PropertyFilter(propertyFilter), new RenditionFilter(
               renditionFilter), renditionManager, includeObjectInfo));
      }

      return list;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject getFolderParent(String repositoryId, String folderId, String propertyFilter, boolean includeObjectInfo)
      throws FilterNotValidException, RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Get folder parent, repository " + repositoryId + ", object " + folderId);

      Repository repository = repositoryService.getRepository(repositoryId);
      Entry folder = repository.getObjectById(folderId);
      if (folder.getScope() != EnumBaseObjectTypeIds.CMIS_FOLDER)
      {
         String msg = "Object " + folderId + " is not a Folder. Use method getObjectParent instead.";
         throw new InvalidArgumentException(msg);
      }
      List<Entry> parents = folder.getParents();
      if (parents.size() == 0)
      {
         String msg = "Root folder may not have parent.";
         throw new InvalidArgumentException(msg);
      }

      RenditionManager renditionManager = repository.getRenditionManager();
      CmisObject cmis =
         getCmisObject(parents.get(0), false, EnumIncludeRelationships.NONE, false, false, new PropertyFilter(
            propertyFilter), RenditionFilter.NONE, renditionManager, includeObjectInfo);
      return cmis;
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisObjectInFolderContainer> getFolderTree(String repositoryId, String folderId, int depth,
      boolean includeAllowableActions, EnumIncludeRelationships includeRelationships, boolean includePathSegments,
      String propertyFilter, String renditionFilter, boolean includeObjectInfo) throws FilterNotValidException, RepositoryException
   {

      if (LOG.isDebugEnabled())
         LOG.debug("Get  FolderTree, repository " + repositoryId + ", object " + folderId + ", depth " + depth);

      if (depth == 0 || depth < -1)
      {
         String msg = "Depth must be greater or equal 1 or -1. But got " + depth;
         throw new InvalidArgumentException(msg);
      }
      Repository repository = repositoryService.getRepository(repositoryId);

      Entry folder = repository.getObjectById(folderId);
      if (folder.getScope() != EnumBaseObjectTypeIds.CMIS_FOLDER)
      {
         String msg = "Object " + folderId + " is not a Folder.";
         throw new InvalidArgumentException(msg);
      }

      ItemsIterator<Entry> children;
      try
      {
         children = folder.getChildren();
      }
      catch (UnsupportedOperationException usoe)
      {
         // Object is not fileable.
         throw new InvalidArgumentException(usoe.getMessage());
      }
      List<CmisObjectInFolderContainer> list = new ArrayList<CmisObjectInFolderContainer>();
      RenditionManager renditionManager = repository.getRenditionManager();
      while (children.hasNext())
      {
         Entry child = children.next();
         if (child.getScope() == EnumBaseObjectTypeIds.CMIS_FOLDER)
            list.add(getDescendants(child, EnumBaseObjectTypeIds.CMIS_FOLDER, depth != -1 ? depth - 1 : depth,
               includeAllowableActions, includeRelationships, includePathSegments, new PropertyFilter(
                  propertyFilter), new RenditionFilter(renditionFilter), renditionManager, includeObjectInfo));
      }

      return list;
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisObjectParents> getObjectParents(String repositoryId, String objectId,
      boolean includeAllowableActions, EnumIncludeRelationships includeRelationships,
      boolean includeRelativePathSegment, String propertyFilter, String renditionFilter, boolean includeObjectInfo)
      throws FilterNotValidException, RepositoryException
   {

      if (LOG.isDebugEnabled())
         LOG.debug("Get object parents, repository " + repositoryId + ", object " + objectId);

      Repository repository = repositoryService.getRepository(repositoryId);
      Entry obj = repository.getObjectById(objectId);
      if (obj.getScope() == EnumBaseObjectTypeIds.CMIS_FOLDER)
      {
         String msg = "Object " + objectId + " is a Folder. Use method getFolderParent instead.";
         throw new InvalidArgumentException(msg);
      }
      List<Entry> parents;
      try
      {
         parents = obj.getParents();
      }
      catch (UnsupportedOperationException usoe)
      {
         // Object is not fileable.
         throw new InvalidArgumentException(usoe.getMessage());
      }

      List<CmisObjectParents> cmisParents = new ArrayList<CmisObjectParents>(parents.size());
      RenditionManager renditionManager = repository.getRenditionManager();
      for (Entry parent : parents)
      {
         CmisObject cmis =
            getCmisObject(parent, includeAllowableActions, EnumIncludeRelationships.NONE, false, false,
               new PropertyFilter(propertyFilter), new RenditionFilter(renditionFilter), renditionManager, includeObjectInfo);
         CmisObjectParents cmisParent = new CmisObjectParentsImpl();
         cmisParent.setObject(cmis);
         cmisParent.setRelativePathSegment(parent.getName());
         cmisParents.add(cmisParent);
      }
      return cmisParents;
   }

   /**
    * Get descendants.
    * 
    * @param parent the CMISEntry parent for descendants
    * @param typeFilter the filter 
    * @param depth the depth for the descendants.
    * @param includeAllowableActions if TRUE then include allowable actions for object
    * @param includeRelationships indicates what relationships of object must be returned
    * @param includePathSegments whether to include path segments
    * @param propertyFilter the property filter
    * @param renditionFilter the rendition filter
    * @param renditionManager the rendition manager
    * @param includeObjectInfo TODO
    * @return the CMIS container for the object in folder
    * @throws RepositoryException if any repository error occurs
    */
   protected CmisObjectInFolderContainer getDescendants(Entry parent, EnumBaseObjectTypeIds typeFilter, int depth,
      boolean includeAllowableActions, EnumIncludeRelationships includeRelationships, boolean includePathSegments,
      PropertyFilter propertyFilter, RenditionFilter renditionFilter, RenditionManager renditionManager, boolean includeObjectInfo)
      throws RepositoryException
   {
      CmisObjectInFolderContainer objectContainer = new CmisObjectInFolderContainerImpl();
      CmisObjectInFolder objInFolder = new CmisObjectInFolderImpl();
      objInFolder.setObject(getCmisObject(parent, includeAllowableActions, includeRelationships, false, false,
         propertyFilter, renditionFilter, renditionManager, includeObjectInfo));
      objInFolder.setPathSegment(parent.getName());
      objectContainer.setObjectInFolder(objInFolder);

      if ((depth == -1 || depth > 0) && parent.getScope() == EnumBaseObjectTypeIds.CMIS_FOLDER)
      {
         for (ItemsIterator<Entry> iter = parent.getChildren(); iter.hasNext();)
         {
            Entry item = iter.next();
            if (typeFilter == null || typeFilter == item.getScope())
            {
               objectContainer.getChildren().add(
                  getDescendants(item, typeFilter, depth != -1 ? depth - 1 : depth, includeAllowableActions,
                     includeRelationships, includePathSegments, propertyFilter, renditionFilter, renditionManager, includeObjectInfo));
            }
         }
      }
      return objectContainer;
   }
}
