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

package org.xcmis.sp.inmemory;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.spi.BaseItemsIterator;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.DocumentData;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.RelationshipDirection;
import org.xcmis.spi.model.TypeDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: FolderDataImpl.java 1197 2010-05-28 08:15:37Z
 *          alexey.zavizionov@gmail.com $
 */
class FolderDataImpl extends BaseObjectData implements FolderData
{

   private static final Log LOG = ExoLogger.getLogger(FolderDataImpl.class);

   public FolderDataImpl(Entry entry, TypeDefinition type, StorageImpl storage)
   {
      super(entry, type, storage);
   }

   /**
    * {@inheritDoc}
    */
   public void addObject(ObjectData object) throws ConstraintException
   {
      storage.children.get(getObjectId()).add(object.getObjectId());
      storage.parents.get(object.getObjectId()).add(getObjectId());
      storage.unfiled.remove(object.getObjectId());
      storage.indexListener.updated(object);
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<ObjectData> getChildren(String orderBy)
   {

      Set<String> childrenIds = storage.children.get(getObjectId());
      List<ObjectData> children = new ArrayList<ObjectData>(childrenIds.size());
      for (String ch : childrenIds)
      {
         ObjectData object = null;
         try
         {
            object = storage.getObjectById(ch);
         }
         catch (ObjectNotFoundException e)
         {
            LOG.warn("Object " + ch + " not found in storage.");
            continue;
         }
         if (object.getBaseType() == BaseType.DOCUMENT && !((DocumentData)object).isLatestVersion())
         {
            continue;
         }
         children.add(object);
      }

      return new BaseItemsIterator<ObjectData>(children);
   }

   /**
    * {@inheritDoc}
    */
   public ContentStream getContentStream(String streamId)
   {
      // TODO : renditions for Folder object.
      // It may be XML or HTML representation direct child or full tree.
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public String getPath()
   {
      return calculatePath().toString();
   }

   /**
    * {@inheritDoc}
    */
   public boolean hasChildren()
   {
      return storage.children.get(getObjectId()).size() > 0;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isAllowedChildType(String typeId)
   {
      Value value = entry.getValue(CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS);
      if (value != null && value.getStrings().length > 0 && !Arrays.asList(value.getStrings()).contains(typeId))
      {
         return false;
      }
      return true;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isRoot()
   {
      return StorageImpl.ROOT_FOLDER_ID.equals(getObjectId());
   }

   /**
    * {@inheritDoc}
    */
   public void removeObject(ObjectData object)
   {
      storage.children.get(getObjectId()).remove(object.getObjectId());
      storage.parents.get(object.getObjectId()).remove(getObjectId());
      if (storage.parents.get(object.getObjectId()).size() == 0)
      {
         storage.unfiled.add(object.getObjectId());
      }
      if (storage.indexListener != null)
      {
         storage.indexListener.updated(object);
      }
   }

   private String calculatePath()
   {
      if (isRoot())
      {
         return "/";
      }

      LinkedList<String> pathSegms = new LinkedList<String>();
      pathSegms.add(getName());

      try
      {
         FolderData parent = getParent();
         while (!parent.isRoot())
         {
            pathSegms.addFirst(parent.getName());
            parent = parent.getParent();
         }
      }
      catch (ConstraintException e)
      {
         // Should not happen:
         // 1. this object is folder
         // 2. not root folder
         // 3. when traversing up always check is folder root
         throw new CmisRuntimeException("Unable get object path.", e);
      }

      StringBuilder path = new StringBuilder();
      path.append('/');
      for (String seg : pathSegms)
      {
         if (path.length() > 1)
         {
            path.append('/');
         }
         path.append(seg);
      }

      return path.toString();
   }

   /**
    * {@inheritDoc}
    */
   protected void delete() throws UpdateConflictException, VersioningException, StorageException
   {
      TypeDefinition relationshipType = storage.types.get(CmisConstants.RELATIONSHIP);
      ItemsIterator<RelationshipData> relationships =
         getRelationships(RelationshipDirection.EITHER, relationshipType, true);
      if (relationships.hasNext())
      {
         throw new StorageException("Object can't be deleted cause to storage referential integrity. "
            + "Object is source or target at least one Relationship.");
      }

      String objectId = getObjectId();
      storage.entries.remove(objectId);
      for (String parent : storage.parents.get(objectId))
      {
         storage.children.get(parent).remove(objectId);
      }
      storage.parents.remove(objectId);
      storage.children.remove(objectId);
   }

}
