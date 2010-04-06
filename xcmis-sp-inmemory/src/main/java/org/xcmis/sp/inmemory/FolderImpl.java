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

import org.xcmis.spi.CMIS;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.Folder;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.impl.BaseItemsIterator;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.utils.CmisUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
class FolderImpl extends BaseObjectData implements Folder
{

   public FolderImpl(Entry entry, TypeDefinition type, StorageImpl storage)
   {
      super(entry, type, storage);
   }

   public FolderImpl(Folder parent, TypeDefinition type, StorageImpl storage)
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
         children.add(storage.getObject(ch));
      }

      return new BaseItemsIterator<ObjectData>(children);
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

   protected void save() throws StorageException
   {
      String name = getName();
      if (name == null || name.length() == 0)
      {
         throw new NameConstraintViolationException("Object name may noy be null or empty string.");
      }

      for (ItemsIterator<ObjectData> children = getParent().getChildren(null); children.hasNext();)
      {
         if (name.equals(children.next().getName()))
         {
            throw new NameConstraintViolationException("Object with name " + name + " already exists in parent folder.");
         }
      }

      if (isNew())
      {
         String id = StorageImpl.generateId();

         entry.setValue(CMIS.OBJECT_ID, //
            new StringValue(id));
         entry.setValue(CMIS.CREATED_BY, //
            new StringValue(""));
         entry.setValue(CMIS.CREATION_DATE, //
            new DateValue(Calendar.getInstance()));

         storage.children.get(parent.getObjectId()).add(id);

         Set<String> parents = new CopyOnWriteArraySet<String>();
         parents.add(parent.getObjectId());
         storage.parents.put(id, parents);

         storage.children.put(id, new CopyOnWriteArraySet<String>());
      }

      entry.setValue(CMIS.LAST_MODIFIED_BY, //
         new StringValue(""));
      entry.setValue(CMIS.LAST_MODIFICATION_DATE, //
         new DateValue(Calendar.getInstance()));
      entry.setValue(CMIS.CHANGE_TOKEN, //
         new StringValue(StorageImpl.generateId()));

      storage.entries.put(entry.getId(), entry);
   }

   private String calculatePath()
   {
      if (isRoot())
      {
         return "/";
      }

      LinkedList<String> pathSegms = new LinkedList<String>();
      pathSegms.add(getName());

      Folder parent = getParent();
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

}
