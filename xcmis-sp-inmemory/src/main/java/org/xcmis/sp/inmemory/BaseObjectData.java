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
import org.xcmis.spi.CMIS;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.PropertyFilter;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.data.Document;
import org.xcmis.spi.data.Folder;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.data.Policy;
import org.xcmis.spi.data.Relationship;
import org.xcmis.spi.impl.BaseItemsIterator;
import org.xcmis.spi.impl.CmisVisitor;
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
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
abstract class BaseObjectData implements ObjectData
{

   private static final Log LOG = ExoLogger.getLogger(BaseObjectData.class);

   protected final TypeDefinition type;

   protected StorageImpl storage;

   /**
    * Parent folder id for newly created fileable objects.
    */
   protected Folder parent;

   protected Entry entry;

   public BaseObjectData(Entry entry, TypeDefinition type, StorageImpl storage)
   {
      this.entry = entry;
      this.type = type;
      this.storage = storage;
   }

   public BaseObjectData(Folder parent, TypeDefinition type, StorageImpl storage)
   {
      this.parent = parent;
      this.type = type;
      this.storage = storage;
      this.entry = new Entry();
   }

   public void accept(CmisVisitor visitor)
   {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */
   public void applyPolicy(Policy policy) throws ConstraintException
   {
      if (!type.isControllablePolicy())
      {
         throw new ConstraintException("Type " + type.getId() + " is not controlable by Policy.");
      }

      if (policy.isNew())
      {
         throw new CmisRuntimeException("Unable apply newly created policy.");
      }

      entry.addPolicy(policy.getObjectId());
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
      return getString(CMIS.CHANGE_TOKEN);
   }

   /**
    * {@inheritDoc}
    */
   public String getCreatedBy()
   {
      return getString(CMIS.CREATED_BY);
   }

   /**
    * {@inheritDoc}
    */
   public Calendar getCreationDate()
   {
      return getDate(CMIS.CREATION_DATE);
   }

   /**
    * {@inheritDoc}
    */
   public Calendar getLastModificationDate()
   {
      return getDate(CMIS.LAST_MODIFICATION_DATE);
   }

   /**
    * {@inheritDoc}
    */
   public String getLastModifiedBy()
   {
      return getString(CMIS.LAST_MODIFIED_BY);
   }

   /**
    * {@inheritDoc}
    */
   public String getName()
   {
      return getString(CMIS.NAME);
   }

   /**
    * {@inheritDoc}
    */
   public String getObjectId()
   {
      if (isNew())
      {
         return null;
      }
      return entry.getId();
   }

   /**
    * {@inheritDoc}
    */
   public Folder getParent() throws ConstraintException
   {
      if (isNew())
      {
         return parent;
      }

      if (StorageImpl.ROOT_FOLDER_ID.equals(getObjectId()))
      {
         throw new ConstraintException("Unable get parent of root folder.");
      }

      Collection<Folder> parents = getParents();

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
   public Collection<Folder> getParents()
   {
      if (isNew())
      {
         if (parent != null)
         {
            List<Folder> parents = new ArrayList<Folder>(1);
            parents.add(parent);
            return parents;
         }

         return Collections.emptyList();
      }

      List<Folder> parents = new ArrayList<Folder>();
      Set<String> parentIds = storage.parents.get(getObjectId());

      if (parentIds != null)
      {
         for (String id : parentIds)
         {
            parents.add((Folder)storage.getObject(id));
         }
      }

      return parents;
   }

   /**
    * {@inheritDoc}
    */
   public Collection<Policy> getPolicies()
   {
      if (!type.isControllablePolicy())
      {
         return Collections.emptyList();
      }

      List<Policy> policies = new ArrayList<Policy>();
      for (String id : entry.getPolicies())
      {
         policies.add((Policy)storage.getObject(id));
      }
      return policies;
   }

   /**
    * {@inheritDoc}
    */
   public Map<String, Property<?>> getProperties()
   {
      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      for (PropertyDefinition<?> def : type.getPropertyDefinitions())
      {
         properties.put(def.getId(), getProperty(def));
      }
      return properties;
   }

   /**
    * {@inheritDoc}
    */
   public Property<?> getProperty(String id)
   {
      PropertyDefinition<?> definition = type.getPropertyDefinition(id);
      if (definition == null)
      {
         return null;
      }
      return getProperty(definition);
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<Relationship> getRelationships(RelationshipDirection direction, TypeDefinition type,
      boolean includeSubRelationshipTypes)
   {
      if (isNew())
      {
         // Newly created object may not have relationship.
         return CmisUtils.emptyItemsIterator();
      }

      Set<String> relationshipIds = storage.relationships.get(getObjectId());
      if (relationshipIds == null)
      {
         return CmisUtils.emptyItemsIterator();
      }

      List<Relationship> relationships = new ArrayList<Relationship>();
      for (String id : relationshipIds)
      {
         Relationship r = (Relationship)storage.getObject(id);
         if (direction == RelationshipDirection.EITHER //
            || (direction == RelationshipDirection.SOURCE && r.getSourceId().equals(getObjectId())) //
            || (direction == RelationshipDirection.TARGET && r.getTargetId().equals(getObjectId())))
         {
            // TODO filter by type.
            relationships.add(r);
         }
      }

      return new BaseItemsIterator<Relationship>(relationships);
   }

   /**
    * {@inheritDoc}
    */
   public Map<String, Property<?>> getSubset(PropertyFilter filter)
   {
      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      for (PropertyDefinition<?> definition : type.getPropertyDefinitions())
      {
         String queryName = definition.getQueryName();
         if (!filter.accept(queryName))
         {
            continue;
         }
         String id = definition.getId();
         properties.put(id, getProperty(definition));
      }
      return properties;
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

   /**
    * {@inheritDoc}
    */
   public boolean isNew()
   {
      return entry.getId() == null;
   }

   /**
    * {@inheritDoc}
    */
   public void removePolicy(Policy policy) throws ConstraintException
   {
      if (!type.isControllablePolicy())
      {
         throw new ConstraintException("Type " + type.getId() + " is not controlable by Policy.");
      }
      entry.removePolicy(policy.getObjectId());
   }

   /**
    * {@inheritDoc}
    */
   public void setACL(List<AccessControlEntry> aces) throws ConstraintException
   {
      if (!type.isControllableACL())
      {
         throw new ConstraintException("Type " + type.getId() + " is not controlable by ACL.");
      }

      CmisUtils.addAclToPermissionMap(entry.getPermissions(), aces);
   }

   /**
    * {@inheritDoc}
    */
   public void setName(String name) throws NameConstraintViolationException
   {
      if (name == null || name.length() == 0)
      {
         throw new NameConstraintViolationException("Invalid name '" + name + "'.");
      }

      entry.setValue(CMIS.NAME, new StringValue(name));
   }

   /**
    * {@inheritDoc}
    */
   public void setProperties(Map<String, Property<?>> properties) throws ConstraintException,
      NameConstraintViolationException
   {
      for (Property<?> property : properties.values())
      {
         setProperty(property);
      }
   }

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public void setProperty(Property<?> property) throws ConstraintException
   {
      PropertyDefinition<?> definition = type.getPropertyDefinition(property.getId());

      if (definition == null)
      {
         throw new ConstraintException("Property " + property.getId() + " is not in property definitions list of type "
            + type.getId());
      }

      if (property.getType() != definition.getPropertyType())
      {
         throw new ConstraintException("Property type is not match.");
      }

      if (!definition.isMultivalued() && property.getValues().size() > 1)
      {
         throw new ConstraintException("Property " + property.getId() + " is not multi-valued.");
      }

      if (definition.isRequired() && property.getValues().size() == 0)
      {
         throw new ConstraintException("Required property " + property.getId() + " can't be removed.");
      }

      for (Object v : property.getValues())
      {
         if (v == null)
         {
            throw new ConstraintException("Null value not allowed. List must not contains null items.");
         }
      }

      Updatability updatability = definition.getUpdatability();
      if (updatability == Updatability.READWRITE //
         || (updatability == Updatability.ONCREATE && isNew()) //
         || (updatability == Updatability.WHENCHECKEDOUT && getBaseType() == BaseType.DOCUMENT && ((Document)this)
            .isPWC()))
      {
         if (property.getType() == PropertyType.BOOLEAN)
         {
            List<Boolean> booleans = (List<Boolean>)property.getValues();
            entry.setValue(property.getId(), new BooleanValue(booleans));
         }
         else if (property.getType() == PropertyType.DATETIME)
         {
            List<Calendar> dates = (List<Calendar>)property.getValues();
            entry.setValue(property.getId(), new DateValue(dates));
         }
         else if (property.getType() == PropertyType.DECIMAL)
         {
            List<BigDecimal> decimals = (List<BigDecimal>)property.getValues();
            entry.setValue(property.getId(), new DecimalValue(decimals));
         }
         else if (property.getType() == PropertyType.INTEGER)
         {
            List<BigInteger> integers = (List<BigInteger>)property.getValues();
            entry.setValue(property.getId(), new IntegerValue(integers));
         }
         else if (property.getType() == PropertyType.URI)
         {
            List<URI> uris = (List<URI>)property.getValues();
            entry.setValue(property.getId(), new UriValue(uris));
         }
         else if (property.getType() == PropertyType.STRING || property.getType() == PropertyType.HTML
            || property.getType() == PropertyType.ID)
         {
            List<String> text = (List<String>)property.getValues();
            entry.setValue(property.getId(), new StringValue(text));
         }
      }
      else
      {
         // Some clients may send all properties even need update only one.
         if (LOG.isDebugEnabled())
         {
            LOG.debug("Property " + property.getId() + " is not updatable.");
         }
      }
   }

   public String toString()
   {
      return "type: " + getTypeId() + ", name: " + getName() + ", id: " + getObjectId();
   }

   private Property<?> getProperty(PropertyDefinition<?> definition)
   {
      // Check in updates for properties.
      Value value = entry.getValue(definition.getId());
      return createProperty(definition, value);
   }

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

   protected Entry getEntry()
   {
      return entry;
   }

   protected abstract void save() throws StorageException;

   protected abstract void delete() throws ConstraintException, StorageException;

}
