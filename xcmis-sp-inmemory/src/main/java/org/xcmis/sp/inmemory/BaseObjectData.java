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
import org.xcmis.spi.DocumentData;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.ObjectDataVisitor;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.PolicyData;
import org.xcmis.spi.PropertyFilter;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.PropertyType;
import org.xcmis.spi.model.RelationshipDirection;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.Updatability;
import org.xcmis.spi.model.impl.BooleanProperty;
import org.xcmis.spi.model.impl.DateTimeProperty;
import org.xcmis.spi.model.impl.DecimalProperty;
import org.xcmis.spi.model.impl.HtmlProperty;
import org.xcmis.spi.model.impl.IdProperty;
import org.xcmis.spi.model.impl.IntegerProperty;
import org.xcmis.spi.model.impl.StringProperty;
import org.xcmis.spi.model.impl.UriProperty;
import org.xcmis.spi.utils.CmisUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: BaseObjectData.java 1197 2010-05-28 08:15:37Z
 *          alexey.zavizionov@gmail.com $
 */
abstract class BaseObjectData implements ObjectData
{

   private static final Log LOG = ExoLogger.getLogger(BaseObjectData.class);

   protected final TypeDefinition type;

   protected final StorageImpl storage;

   protected final Entry entry;

   public BaseObjectData(Entry entry, TypeDefinition type, StorageImpl storage)
   {
      this.entry = entry;
      this.type = type;
      this.storage = storage;
   }

   /**
    * {@inheritDoc}
    */
   public void accept(ObjectDataVisitor visitor)
   {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */
   public void applyPolicy(PolicyData policy)
   {
      entry.addPolicy(policy);
      try
      {
         save();
      }
      catch (StorageException e)
      {
         throw new CmisRuntimeException("Unable apply policy. " + e.getMessage(), e);
      }
   }

   public boolean equals(Object obj)
   {
      if (obj == null)
      {
         return false;
      }
      if (obj.getClass() != getClass())
      {
         return false;
      }
      return ((BaseObjectData)obj).getObjectId().equals(getObjectId());
   }

   /**
    * {@inheritDoc}
    */
   public List<AccessControlEntry> getACL(boolean onlyBasicPermissions)
   {
      if (!type.isControllableACL())
      {
         return Collections.emptyList();
      }
      return CmisUtils.createAclFromPermissionMap(entry.getPermissions());
   }

   /**
    * {@inheritDoc}
    */
   public BaseType getBaseType()
   {
      return type.getBaseId();
   }

   /**
    * {@inheritDoc}
    */
   public String getChangeToken()
   {
      return getString(CmisConstants.CHANGE_TOKEN);
   }

   /**
    * {@inheritDoc}
    */
   public String getCreatedBy()
   {
      return getString(CmisConstants.CREATED_BY);
   }

   /**
    * {@inheritDoc}
    */
   public Calendar getCreationDate()
   {
      return getDate(CmisConstants.CREATION_DATE);
   }

   /**
    * {@inheritDoc}
    */
   public Calendar getLastModificationDate()
   {
      return getDate(CmisConstants.LAST_MODIFICATION_DATE);
   }

   /**
    * {@inheritDoc}
    */
   public String getLastModifiedBy()
   {
      return getString(CmisConstants.LAST_MODIFIED_BY);
   }

   /**
    * {@inheritDoc}
    */
   public String getName()
   {
      return getString(CmisConstants.NAME);
   }

   /**
    * {@inheritDoc}
    */
   public String getObjectId()
   {
      return entry.getId();
   }

   /**
    * {@inheritDoc}
    */
   public FolderData getParent() throws ConstraintException
   {
      if (StorageImpl.ROOT_FOLDER_ID.equals(getObjectId()))
      {
         throw new ConstraintException("Unable get parent of root folder.");
      }

      Collection<FolderData> parents = getParents();
      if (parents.size() > 1)
      {
         throw new ConstraintException("Object has more then one parent.");
      }
      if (parents.size() == 1)
      {
         return parents.iterator().next();
      }
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Collection<FolderData> getParents()
   {
      Set<String> parentIds = storage.parents.get(getObjectId());
      Set<FolderData> parents = new HashSet<FolderData>(parentIds.size());

      if (parentIds != null)
      {
         for (String id : parentIds)
         {
            try
            {
               parents.add((FolderData)storage.getObjectById(id));
            }
            catch (ObjectNotFoundException e)
            {
               LOG.warn("Not found folder " + id);
            }
         }
      }
      return parents;
   }

   /**
    * {@inheritDoc}
    */
   public Collection<PolicyData> getPolicies()
   {
      if (!type.isControllablePolicy())
      {
         return Collections.emptyList();
      }

      List<PolicyData> policies = new ArrayList<PolicyData>();
      for (String id : entry.getPolicies())
      {
         try
         {
            policies.add((PolicyData)storage.getObjectById(id));
         }
         catch (ObjectNotFoundException e)
         {
            LOG.warn("Not found policy " + id);
         }
      }
      return policies;
   }

   /**
    * {@inheritDoc}
    */
   public Map<String, Property<?>> getProperties()
   {
      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      for (PropertyDefinition<?> definition : type.getPropertyDefinitions())
      {
         properties.put(definition.getId(), doGetProperty(definition));
      }
      return properties;
   }

   /**
    * {@inheritDoc}
    */
   public Map<String, Property<?>> getProperties(PropertyFilter filter)
   {
      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      for (PropertyDefinition<?> definition : type.getPropertyDefinitions())
      {
         String queryName = definition.getQueryName();
         if (filter.accept(queryName))
         {
            String id = definition.getId();
            properties.put(id, doGetProperty(definition));
         }
      }
      return properties;
   }

   /**
    * {@inheritDoc}
    */
   public Property<?> getProperty(String id)
   {
      PropertyDefinition<?> definition = type.getPropertyDefinition(id);
      if (definition != null)
      {
         return doGetProperty(definition);
      }
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<RelationshipData> getRelationships(RelationshipDirection direction, TypeDefinition type,
      boolean includeSubRelationshipTypes)
   {
      Set<String> relationshipIds = storage.relationships.get(getObjectId());
      if (relationshipIds == null)
      {
         return CmisUtils.emptyItemsIterator();
      }

      Set<RelationshipData> relationships = new java.util.HashSet<RelationshipData>();
      for (String id : relationshipIds)
      {
         RelationshipData relationship = null;
         try
         {
            relationship = (RelationshipData)storage.getObjectById(id);
         }
         catch (ObjectNotFoundException e)
         {
            LOG.warn("Not found relationship " + id + ".");
            continue;
         }
         if (direction == RelationshipDirection.EITHER //
            || (direction == RelationshipDirection.SOURCE && relationship.getSourceId().equals(getObjectId())) //
            || (direction == RelationshipDirection.TARGET && relationship.getTargetId().equals(getObjectId())))
         {
            // TODO filter by type.
            relationships.add(relationship);
         }
      }
      return new BaseItemsIterator<RelationshipData>(relationships);
   }

   /**
    * {@inheritDoc}
    */
   public TypeDefinition getTypeDefinition()
   {
      return type;
   }

   /**
    * {@inheritDoc}
    */
   public String getTypeId()
   {
      return type.getId();
   }

   public int hashCode()
   {
      return getObjectId().hashCode();
   }

   /**
    * {@inheritDoc}
    */
   public void removePolicy(PolicyData policy)
   {
      entry.removePolicy(policy);
      try
      {
         save();
      }
      catch (StorageException e)
      {
         throw new CmisRuntimeException("Unable remove policy. " + e.getMessage(), e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void setACL(List<AccessControlEntry> acl)
   {
      Map<String, Set<String>> permissions = entry.getPermissions();
      permissions.clear();
      CmisUtils.addAclToPermissionMap(permissions, acl);
      try
      {
         save();
      }
      catch (StorageException e)
      {
         throw new CmisRuntimeException("Unable set ACL. " + e.getMessage(), e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void setProperties(Map<String, Property<?>> properties) throws NameConstraintViolationException,
      UpdateConflictException, VersioningException, StorageException
   {
      for (Property<?> property : properties.values())
      {
         doSetProperty(property);
      }
      save();
   }

   /**
    * {@inheritDoc}
    */
   public void setProperty(Property<?> property) throws NameConstraintViolationException, StorageException,
      UpdateConflictException, VersioningException
   {
      doSetProperty(property);
      save();
   }

   /**
    * {@inheritDoc}
    */
   public String toString()
   {
      return "type: " + getTypeId() + ", name: " + getName() + ", id: " + getObjectId();
   }

   /**
    * To create the new property.
    *
    * @param def the property definition
    * @param value the value
    * @return the new property
    */
   private Property<?> createProperty(PropertyDefinition<?> def, Value value)
   {
      if (def.getPropertyType() == PropertyType.BOOLEAN)
      {
         return new BooleanProperty(def.getId(), def.getQueryName(), def.getLocalName(), def.getDisplayName(),
            value == null ? null : Arrays.asList(value.getBooleans()));
      }
      else if (def.getPropertyType() == PropertyType.DATETIME)
      {
         return new DateTimeProperty(def.getId(), def.getQueryName(), def.getLocalName(), def.getDisplayName(),
            value == null ? null : Arrays.asList(value.getDates()));
      }
      else if (def.getPropertyType() == PropertyType.DECIMAL)
      {
         return new DecimalProperty(def.getId(), def.getQueryName(), def.getLocalName(), def.getDisplayName(),
            value == null ? null : Arrays.asList(value.getDecimals()));
      }
      else if (def.getPropertyType() == PropertyType.HTML)
      {
         return new HtmlProperty(def.getId(), def.getQueryName(), def.getLocalName(), def.getDisplayName(),
            value == null ? null : Arrays.asList(value.getStrings()));
      }
      else if (def.getPropertyType() == PropertyType.ID)
      {
         return new IdProperty(def.getId(), def.getQueryName(), def.getLocalName(), def.getDisplayName(), value == null
            ? null : Arrays.asList(value.getStrings()));
      }
      else if (def.getPropertyType() == PropertyType.INTEGER)
      {
         return new IntegerProperty(def.getId(), def.getQueryName(), def.getLocalName(), def.getDisplayName(),
            value == null ? null : Arrays.asList(value.getIntegers()));
      }
      else if (def.getPropertyType() == PropertyType.STRING)
      {
         return new StringProperty(def.getId(), def.getQueryName(), def.getLocalName(), def.getDisplayName(),
            value == null ? null : Arrays.asList(value.getStrings()));
      }
      else if (def.getPropertyType() == PropertyType.URI)
      {
         return new UriProperty(def.getId(), def.getQueryName(), def.getLocalName(), def.getDisplayName(),
            value == null ? null : Arrays.asList(value.getURI()));
      }
      else
      {
         throw new CmisRuntimeException("Unknown property type.");
      }
   }

   protected abstract void delete() throws StorageException, UpdateConflictException, VersioningException;

   protected Property<?> doGetProperty(PropertyDefinition<?> definition)
   {
      Value value = entry.getValue(definition.getId());
      if (value == null && CmisConstants.PATH.equals(definition.getId()) && type.getBaseId() == BaseType.FOLDER)
      {
         value = new StringValue(((FolderData)this).getPath());
         // add other virtual property
      }
      return createProperty(definition, value);
   }

   /**
    * Update properties, skip on-create and read-only properties
    *
    * @param property property to be updated
    */
   protected void doSetProperty(Property<?> property) throws NameConstraintViolationException
   {
      PropertyDefinition<?> definition = type.getPropertyDefinition(property.getId());
      Updatability updatability = definition.getUpdatability();
      if (updatability == Updatability.READWRITE //
         || (updatability == Updatability.WHENCHECKEDOUT && getBaseType() == BaseType.DOCUMENT && ((DocumentData)this)
            .isPWC()))
      {

         // Do not store nulls
         for (Iterator<?> i = property.getValues().iterator(); i.hasNext();)
         {
            Object v = i.next();
            if (v == null)
            {
               i.remove();
            }
         }

         if (CmisConstants.NAME.equals(property.getId()))
         {
            String name = null;
            List<?> values = property.getValues();
            if (values.size() > 0)
            {
               name = (String)values.get(0);
            }

            if (name == null || name.length() == 0)
            {
               throw new NameConstraintViolationException("Name can't be null or empty string.");
            }
            if (name.equals(getName()))
            {
               return;
            }

            for (FolderData parent : getParents())
            {
               for (ItemsIterator<ObjectData> iterator = parent.getChildren(null); iterator.hasNext();)
               {
                  if (name.equals(iterator.next().getName()))
                  {
                     throw new NameConstraintViolationException("Object with name " + name
                        + " already exists in parent folder.");
                  }
               }
            }
         }

         entry.setProperty(property);
      }
      else
      {
         if (LOG.isDebugEnabled())
         {
            LOG.debug("Property " + property.getId() + " is not updatable.");
         }
      }
   }

   protected Boolean getBoolean(String id)
   {
      Value value = entry.getValue(id);
      if (value != null)
      {
         Boolean[] booleans = value.getBooleans();
         return booleans.length > 0 ? booleans[0] : null;
      }
      return null;
   }

   protected Boolean[] getBooleans(String id)
   {
      Value value = entry.getValue(id);
      if (value != null)
      {
         return value.getBooleans();
      }
      return null;
   }

   protected Calendar getDate(String id)
   {
      Value value = entry.getValue(id);
      if (value != null)
      {
         Calendar[] dates = value.getDates();
         return dates.length > 0 ? dates[0] : null;
      }
      return null;
   }

   protected Calendar[] getDates(String id)
   {
      Value value = entry.getValue(id);
      if (value != null)
      {
         return value.getDates();
      }
      return null;
   }

   protected BigDecimal getDecimal(String id)
   {
      Value value = entry.getValue(id);
      if (value != null)
      {
         BigDecimal[] decimals = value.getDecimals();
         return decimals.length > 0 ? decimals[0] : null;
      }
      return null;
   }

   protected BigDecimal[] getDecimals(String id)
   {
      Value value = entry.getValue(id);
      if (value != null)
      {
         return value.getDecimals();
      }
      return null;
   }

   protected Entry getEntry()
   {
      return entry;
   }

   protected String getId(String id)
   {
      return getString(id);
   }

   protected String[] getIds(String id)
   {
      return getStrings(id);
   }

   protected BigInteger getInteger(String id)
   {
      Value value = entry.getValue(id);
      if (value != null)
      {
         BigInteger[] integers = value.getIntegers();
         return integers.length > 0 ? integers[0] : null;
      }
      return null;
   }

   protected BigInteger[] getIntegers(String id)
   {
      Value value = entry.getValue(id);
      if (value != null)
      {
         return value.getIntegers();
      }
      return null;
   }

   protected String getString(String id)
   {
      Value value = entry.getValue(id);
      if (value != null)
      {
         String[] strings = value.getStrings();
         return strings.length > 0 ? strings[0] : null;
      }
      return null;
   }

   protected String[] getStrings(String id)
   {
      Value value = entry.getValue(id);
      if (value != null)
      {
         return value.getStrings();
      }
      return null;
   }

   protected void save() throws StorageException
   {
      if (storage.entries.get(entry.getId()) == null)
      {
         throw new CmisRuntimeException("Object was removed from storage.");
      }

      entry.setValue(CmisConstants.LAST_MODIFIED_BY, new StringValue(storage.getCurrentUser()));
      entry.setValue(CmisConstants.LAST_MODIFICATION_DATE, new DateValue(Calendar.getInstance()));
      entry.setValue(CmisConstants.CHANGE_TOKEN, new StringValue(StorageImpl.generateId()));

      storage.entries.put(entry.getId(), entry);
      if (storage.indexListener != null)
      {
         storage.indexListener.updated(this);
      }
   }

}
