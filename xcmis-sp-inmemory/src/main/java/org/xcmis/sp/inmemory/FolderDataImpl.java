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

import org.xcmis.spi.BaseItemsIterator;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.DocumentData;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.RelationshipDirection;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.utils.CmisUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
class FolderDataImpl extends BaseObjectData implements FolderData
{

   public FolderDataImpl(Entry entry, TypeDefinition type, StorageImpl storage)
   {
      super(entry, type, storage);
   }

   public FolderDataImpl(FolderData parent, TypeDefinition type, StorageImpl storage)
   {
      super(parent, type, storage);
   }

   /**
    * {@inheritDoc}
    */
   public void addObject(ObjectData object) throws ConstraintException
   {
      if (isNew())
      {
         throw new UnsupportedOperationException("Unable add object in newly created folder.");
      }
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
      if (isNew())
      {
         return CmisUtils.emptyItemsIterator();
      }

      Set<String> childrenIds = storage.children.get(getObjectId());
      List<ObjectData> children = new ArrayList<ObjectData>(childrenIds.size());
      for (String ch : childrenIds)
      {
         ObjectData object = storage.getObject(ch);
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
      if (isNew())
      {
         throw new UnsupportedOperationException("Unable remove object from newly created folder.");
      }
      storage.children.get(getObjectId()).remove(object.getObjectId());
      storage.parents.get(object.getObjectId()).remove(getObjectId());
      if (storage.parents.get(object.getObjectId()).size() == 0)
      {
         storage.unfiled.add(object.getObjectId());
      }
      storage.indexListener.updated(object);
   }

   private String calculatePath()
   {
      if (isRoot())
      {
         return "/";
      }

      LinkedList<String> pathSegms = new LinkedList<String>();
      pathSegms.add(getName());

      FolderData parent = getParent();
      while (!parent.isRoot())
      {
         pathSegms.addFirst(parent.getName());
         parent = parent.getParent();
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

   protected void delete() throws ConstraintException, StorageException
   {
      if (isRoot())
      {
         throw new ConstraintException("Root folder can't be removed.");
      }

      if (hasChildren())
      {
         throw new ConstraintException("Failed delete object. Object " + getObjectId()
            + " is Folder and contains one or more objects.");
      }

      ItemsIterator<RelationshipData> relationships = getRelationships(RelationshipDirection.EITHER, null, true);
      if (relationships.hasNext())
      {
         throw new ConstraintException("Object can't be deleted cause to storage referential integrity. "
            + "Object is source or target at least one Relationship.");
      }

      String objectId = getObjectId();
      storage.properties.remove(objectId);
      storage.policies.remove(objectId);
      storage.permissions.remove(objectId);
      for (String parent : storage.parents.get(objectId))
      {
         storage.children.get(parent).remove(objectId);
      }
      storage.parents.remove(objectId);
      storage.children.remove(objectId);
   }

   protected void save() throws StorageException
   {
      String name = getName();
      if (name == null || name.length() == 0)
      {
         throw new NameConstraintViolationException("Object name may noy be null or empty string.");
      }

      for (ItemsIterator<ObjectData> iterator = getParent().getChildren(null); iterator.hasNext();)
      {
         ObjectData object = iterator.next();
         if (object.getObjectId().equals(getObjectId()))
         {
            continue;
         }
         if (name.equals(object.getName()))
         {
            throw new NameConstraintViolationException("Object with name " + name + " already exists in parent folder.");
         }
      }

      String id;

      if (isNew())
      {
         id = StorageImpl.generateId();

         entry.setValue(CmisConstants.OBJECT_ID, new StringValue(id));
         entry.setValue(CmisConstants.OBJECT_TYPE_ID, new StringValue(getTypeId()));
         entry.setValue(CmisConstants.BASE_TYPE_ID, new StringValue(getBaseType().value()));
         entry.setValue(CmisConstants.CREATED_BY, new StringValue());
         entry.setValue(CmisConstants.CREATION_DATE, new DateValue(Calendar.getInstance()));

         storage.children.get(parent.getObjectId()).add(id);

         Set<String> parents = new CopyOnWriteArraySet<String>();
         parents.add(parent.getObjectId());
         storage.parents.put(id, parents);

         storage.children.put(id, new CopyOnWriteArraySet<String>());

         storage.properties.put(id, new ConcurrentHashMap<String, Value>());
         storage.policies.put(id, new CopyOnWriteArraySet<String>());
         storage.permissions.put(id, new ConcurrentHashMap<String, Set<String>>());
      }
      else
      {
         id = getObjectId();
      }

      entry.setValue(CmisConstants.LAST_MODIFIED_BY, new StringValue());
      entry.setValue(CmisConstants.LAST_MODIFICATION_DATE, new DateValue(Calendar.getInstance()));
      entry.setValue(CmisConstants.CHANGE_TOKEN, new StringValue(StorageImpl.generateId()));

      storage.properties.get(id).putAll(entry.getValues());
      storage.policies.get(id).addAll(entry.getPolicies());
      storage.permissions.get(id).putAll(entry.getPermissions());

   }
}
