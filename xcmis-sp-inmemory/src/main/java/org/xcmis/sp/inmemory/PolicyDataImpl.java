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
import org.xcmis.spi.FolderData;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.PolicyData;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.model.RelationshipDirection;
import org.xcmis.spi.model.TypeDefinition;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
class PolicyDataImpl extends BaseObjectData implements PolicyData
{

   public PolicyDataImpl(Entry entry, TypeDefinition type, StorageImpl storage)
   {
      super(entry, type, storage);
   }

   public PolicyDataImpl(TypeDefinition type, StorageImpl storage)
   {
      super((FolderData)null, type, storage);
   }

   /**
    * {@inheritDoc}
    */
   public ContentStream getContentStream(String streamId)
   {
      // no content or renditions for policy
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public FolderData getParent() throws ConstraintException
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Collection<FolderData> getParents()
   {
      return Collections.emptyList();
   }

   /**
    * {@inheritDoc}
    */
   public String getPolicyText()
   {
      return getString(CmisConstants.POLICY_TEXT);
   }

   @Override
   protected void save() throws StorageException
   {
      String name = getName();
      if (name == null || name.length() == 0)
      {
         throw new NameConstraintViolationException("Object name may noy be null or empty string.");
      }

      // TODO : check policies same names
      if (getString(CmisConstants.POLICY_TEXT) == null)
      {
         throw new ConstraintException("Required property 'cmis:policyText' is not set.");
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

         storage.properties.put(id, new ConcurrentHashMap<String, Value>());
         storage.policies.put(id, new CopyOnWriteArraySet<String>());
         storage.permissions.put(id, new ConcurrentHashMap<String, Set<String>>());

         storage.parents.put(id, StorageImpl.EMPTY_PARENTS);
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

      for (Iterator<Set<String>> iterator = storage.policies.values().iterator(); iterator.hasNext();)
      {
         if (iterator.next().contains(objectId))
         {
            throw new ConstraintException("Unable delete applied policy");
         }
      }

      ItemsIterator<RelationshipData> relationships = getRelationships(RelationshipDirection.EITHER, null, true);
      if (relationships.hasNext())
      {
         throw new ConstraintException("Object can't be deleted cause to storage referential integrity. "
            + "Object is source or target at least one Relationship.");
      }

      storage.properties.remove(objectId);
      storage.policies.remove(objectId);
      storage.permissions.remove(objectId);
      storage.parents.remove(objectId);
   }
}
