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
import org.xcmis.spi.AccessControlEntry;
import org.xcmis.spi.BaseType;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.PropertyDefinition;
import org.xcmis.spi.PropertyFilter;
import org.xcmis.spi.RelationshipDirection;
import org.xcmis.spi.TypeDefinition;
import org.xcmis.spi.Updatability;
import org.xcmis.spi.data.Document;
import org.xcmis.spi.data.Folder;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.data.Policy;
import org.xcmis.spi.data.Relationship;
import org.xcmis.spi.impl.BaseItemsIterator;
import org.xcmis.spi.impl.CmisVisitor;
import org.xcmis.spi.object.Property;
import org.xcmis.spi.object.impl.StringProperty;
import org.xcmis.spi.utils.CmisUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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

   /**
    * Temporary storage for object properties. For newly create object all
    * properties will be stored here before calling {@link #save()}.
    */
   protected final Map<String, Property<?>> properties = new HashMap<String, Property<?>>();

   protected StorageImpl storage;

   protected String objectId;

   /**
    * Parent folder id for newly created fileable objects.
    */
   protected Folder parent;

   /**
    * Temporary storage for policies which should be applied to object.
    */
   protected Set<Policy> applyPolicies;

   /**
    * Temporary storage for policies which should be removed from object.
    */
   protected Set<Policy> removePolicies;

   /**
    * Temporary storage for ACL which should be applied to object.
    */
   protected List<AccessControlEntry> acl;

   public BaseObjectData(String objectId, TypeDefinition type, StorageImpl storage)
   {
      this.objectId = objectId;
      this.type = type;
      this.storage = storage;
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

      if (applyPolicies == null)
      {
         applyPolicies = new HashSet<Policy>();
      }
      applyPolicies.add(policy);
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

      if (isNew())
      {
         if (acl == null)
         {
            return Collections.emptyList();
         }
         return Collections.unmodifiableList(acl);
      }
      else
      {
         if (acl != null)
         {
            return Collections.unmodifiableList(acl);
         }
         Map<String, Set<String>> aces = storage.acls.get(objectId);
         if (aces == null)
         {
            return Collections.emptyList();
         }
         return CmisUtils.createAclFromPermissionMap(aces);
      }
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
      return objectId;
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

      if (storage.getRepositoryInfo().getRootFolderId().equals(objectId))
      {
         throw new ConstraintException("Unable get parent of root folder.");
      }

      Collection<Folder> parents = getParents();
      if (parents.size() > 1)
      {
         throw new ConstraintException("Object has more then one parent.");
      }
      else if (parents.size() == 0)
      {
         return null;
      }
      else
      {
         return parents.iterator().next();
      }
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

      Set<Folder> parents = new HashSet<Folder>();
      Set<String> parentIds = storage.parents.get(objectId);

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

      if (isNew())
      {
         if (applyPolicies == null)
         {
            return Collections.emptySet();
         }
         return Collections.unmodifiableSet(applyPolicies);
      }

      Set<Policy> policies = new HashSet<Policy>();
      Set<String> policyIds = storage.policies.get(objectId);

      if (policyIds != null)
      {
         for (String id : policyIds)
         {
            policies.add((Policy)storage.getObject(id));
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
         return CmisUtils.emptyItemsIterator();
      }

      Set<RelationshipInfo> relationshipsInfo = storage.relationships.get(objectId);
      if (relationshipsInfo == null)
      {
         return CmisUtils.emptyItemsIterator();
      }

      List<Relationship> relationships = new ArrayList<Relationship>();
      for (RelationshipInfo info : relationshipsInfo)
      {
         if (direction == RelationshipDirection.EITHER //
            || (direction == RelationshipDirection.SOURCE && info.getDirection() == RelationshipInfo.SOURCE) //
            || (direction == RelationshipDirection.TARGET && info.getDirection() == RelationshipInfo.TARGET))
         {
            Relationship relationship = (Relationship)storage.getObject(info.getRelationshipId());
            // TODO filter by type.
            relationships.add(relationship);
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
      return objectId == null;
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

      if (removePolicies == null)
      {
         removePolicies = new HashSet<Policy>();
      }
      removePolicies.add(policy);
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

      if (this.acl != null)
      {
         this.acl.clear(); // Not merged, just replaced.
      }

      if (aces != null && aces.size() > 0)
      {
         if (this.acl == null)
         {
            this.acl = new ArrayList<AccessControlEntry>();
         }
         this.acl.addAll(aces);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void setName(String name) throws NameConstraintViolationException
   {
      PropertyDefinition<?> definition = type.getPropertyDefinition(CMIS.NAME);
      StringProperty nameProperty =
         new StringProperty(CMIS.NAME, definition.getQueryName(), definition.getLocalName(), definition
            .getDisplayName(), name);
      properties.put(CMIS.NAME, nameProperty);
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
         properties.put(property.getId(), property);
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

   private Property<?> getProperty(PropertyDefinition<?> definition)
   {
      Property<?> property = properties.get(definition.getId());
      if (property == null)
      {
         property = storage.properties.get(objectId).get(definition.getId());
      }

      return property;
   }

   // Helpers for accessing properties of saved object. For internal usage only.

   protected Boolean getBoolean(String id)
   {
      if (isNew())
      {
         return null;
      }
      List<?> values = storage.properties.get(objectId).get(id).getValues();
      if (values.size() > 0)
      {
         return (Boolean)values.get(0);
      }
      return null;
   }

   protected Boolean[] getBooleans(String id)
   {
      if (isNew())
      {
         return null;
      }
      List<?> values = storage.properties.get(objectId).get(id).getValues();
      if (values.size() > 0)
      {
         return values.toArray(new Boolean[values.size()]);
      }
      return null;
   }

   protected Calendar getDate(String id)
   {
      if (isNew())
      {
         return null;
      }
      List<?> values = storage.properties.get(objectId).get(id).getValues();
      if (values.size() > 0)
      {
         return (Calendar)values.get(0);
      }
      return null;
   }

   protected Calendar[] getDates(String id)
   {
      if (isNew())
      {
         return null;
      }
      List<?> values = storage.properties.get(objectId).get(id).getValues();
      if (values.size() > 0)
      {
         return values.toArray(new Calendar[values.size()]);
      }
      return null;
   }

   protected BigDecimal getDecimal(String id)
   {
      if (isNew())
      {
         return null;
      }
      List<?> values = storage.properties.get(objectId).get(id).getValues();
      if (values.size() > 0)
      {
         return (BigDecimal)values.get(0);
      }
      return null;
   }

   protected BigDecimal[] getDecimals(String id)
   {
      if (isNew())
      {
         return null;
      }
      List<?> values = storage.properties.get(objectId).get(id).getValues();
      if (values.size() > 0)
      {
         return values.toArray(new BigDecimal[values.size()]);
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
      if (isNew())
      {
         return null;
      }
      List<?> values = storage.properties.get(objectId).get(id).getValues();
      if (values.size() > 0)
      {
         return (BigInteger)values.get(0);
      }
      return null;
   }

   protected BigInteger[] getIntegers(String id)
   {
      if (isNew())
      {
         return null;
      }
      List<?> values = storage.properties.get(objectId).get(id).getValues();
      if (values.size() > 0)
      {
         return values.toArray(new BigInteger[values.size()]);
      }
      return null;
   }

   protected String getString(String id)
   {
      if (isNew())
      {
         return null;
      }
      List<?> values = storage.properties.get(objectId).get(id).getValues();
      if (values.size() > 0)
      {
         return (String)values.get(0);
      }
      return null;
   }

   protected String[] getStrings(String id)
   {
      if (isNew())
      {
         return null;
      }
      List<?> values = storage.properties.get(objectId).get(id).getValues();
      if (values.size() > 0)
      {
         return values.toArray(new String[values.size()]);
      }
      return null;
   }

}
