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

import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.Folder;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.Relationship;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.model.TypeDefinition;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
class RelationshipImpl extends BaseObjectData implements Relationship
{

   protected ObjectData source;

   protected ObjectData target;

   public RelationshipImpl(Entry entry, TypeDefinition type, StorageImpl storage)
   {
      super(entry, type, storage);
   }

   public RelationshipImpl(TypeDefinition type, ObjectData source, ObjectData target, StorageImpl storage)
   {
      super((Folder)null, type, storage);
      this.source = source;
      this.target = target;
   }

   public ContentStream getContentStream(String streamId)
   {
      // no content or renditions for relationship
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Folder getParent() throws ConstraintException
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Collection<Folder> getParents()
   {
      return Collections.emptyList();
   }

   public String getSourceId()
   {
      if (isNew())
      {
         return source.getObjectId();
      }
      return getString(CmisConstants.SOURCE_ID);
   }

   public String getTargetId()
   {
      if (isNew())
      {
         return target.getObjectId();
      }
      return getString(CmisConstants.TARGET_ID);
   }

   @Override
   protected void save() throws StorageException
   {
      String name = getName();
      if (name == null || name.length() == 0)
      {
         throw new NameConstraintViolationException("Object name may noy be null or empty string.");
      }

      // TODO : check relationship same names

      String id;

      if (isNew())
      {
         id = StorageImpl.generateId();

         entry.setValue(CmisConstants.OBJECT_ID, new StringValue(id));
         entry.setValue(CmisConstants.OBJECT_TYPE_ID, new StringValue(getTypeId()));
         entry.setValue(CmisConstants.BASE_TYPE_ID, new StringValue(getBaseType().value()));
         entry.setValue(CmisConstants.CREATED_BY, new StringValue());
         entry.setValue(CmisConstants.CREATION_DATE, new DateValue(Calendar.getInstance()));
         entry.setValue(CmisConstants.SOURCE_ID, new StringValue(source.getObjectId()));
         entry.setValue(CmisConstants.TARGET_ID, new StringValue(target.getObjectId()));

         Set<String> sourceRelationships = storage.relationships.get(source.getObjectId());
         if (sourceRelationships == null)
         {
            sourceRelationships = new CopyOnWriteArraySet<String>();
            storage.relationships.put(source.getObjectId(), sourceRelationships);
         }
         sourceRelationships.add(id);

         Set<String> targetRelationships = storage.relationships.get(target.getObjectId());
         if (targetRelationships == null)
         {
            targetRelationships = new CopyOnWriteArraySet<String>();
            storage.relationships.put(target.getObjectId(), targetRelationships);
         }
         targetRelationships.add(id);

         storage.parents.put(id, StorageImpl.EMPTY_PARENTS);

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

   protected void delete() throws ConstraintException, StorageException
   {
      String objectId = getObjectId();
      String sourceId = getSourceId();
      String targetId = getTargetId();
      storage.properties.remove(objectId);
      storage.policies.remove(objectId);
      storage.permissions.remove(objectId);
      storage.parents.remove(objectId);
      storage.relationships.get(sourceId).remove(objectId);
      storage.relationships.get(targetId).remove(objectId);
   }
}
