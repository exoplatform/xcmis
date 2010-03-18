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

package org.xcmis.sp.jcr.exo.NEW;

import org.xcmis.spi.BaseType;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.PropertyDefinition;
import org.xcmis.spi.PropertyFilter;
import org.xcmis.spi.PropertyType;
import org.xcmis.spi.TypeDefinition;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.impl.CmisVisitor;
import org.xcmis.spi.object.Properties;
import org.xcmis.spi.object.Property;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public abstract class AbstractObjectData implements ObjectData, Properties
{

   protected final TypeDefinition type;

   //   /**
   //    * Temporary storage for object properties. For newly create object all
   //    * properties will be stored here before calling
   //    * {@link Storage#saveObject(ObjectData)}.
   //    */
   //   protected final Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
   //
   //   /**
   //    * Temporary storage for policies applied to object. For newly created all
   //    * policies will be stored in here before calling
   //    * {@link Storage#saveObject(ObjectData)}.
   //    */
   //   protected Set<PolicyData> policies;
   //
   //   /**
   //    * Temporary storage for ACL applied to object. For newly created all ACL
   //    * will be stored in here before calling
   //    * {@link Storage#saveObject(ObjectData)}.
   //    */
   //   protected List<AccessControlEntry> acl;

   public AbstractObjectData(TypeDefinition type)
   {
      this.type = type;
   }

   /**
    * {@inheritDoc}
    */
   public void accept(CmisVisitor visitor)
   {
      visitor.visit(this);
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
   public Properties getProperties()
   {
      return this;
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

   // Properties

   /**
    * {@inheritDoc}
    */
   public Map<String, Property<?>> getAll()
   {
      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      for (PropertyDefinition<?> def : type.getPropertyDefinitions())
      {
         String id = def.getId();
         properties.put(id, getProperty(id));
      }
      return properties;
   }

   /**
    * {@inheritDoc}
    */
   public Map<String, Property<?>> getSubset(PropertyFilter filter)
   {
      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      for (PropertyDefinition<?> def : type.getPropertyDefinitions())
      {
         String queryName = def.getQueryName();
         if (!filter.accept(queryName))
            continue;
         String id = def.getId();
         properties.put(id, getProperty(id));
      }
      return properties;
   }

   /**
    * {@inheritDoc}
    */
   public void setValues(Map<String, Property<?>> values) throws ConstraintException, NameConstraintViolationException
   {
      for (Property<?> value : values.values())
      {
         validate(value);
         updateProperty(value);
      }
   }

   public void setProperty(Property<?> value) throws ConstraintException
   {
      validate(value);
      updateProperty(value);
   }

   protected abstract void updateProperty(Property<?> value);

   // ------- Shortcuts for accessing properties. -------

   /**
    * {@inheritDoc}
    */
   public Boolean getBoolean(String id)
   {
      Property<?> property = getProperty(id);
      if (property != null && property.getType() == PropertyType.BOOLEAN)
         return (Boolean)getValue(property);
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Boolean[] getBooleans(String id)
   {
      Property<?> property = getProperty(id);
      if (property != null && property.getType() == PropertyType.BOOLEAN)
         return property.getValues().toArray(new Boolean[property.getValues().size()]);
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Calendar getDate(String id)
   {
      Property<?> property = getProperty(id);
      if (property != null && property.getType() == PropertyType.DATETIME)
         return (Calendar)getValue(property);
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Calendar[] getDates(String id)
   {
      Property<?> property = getProperty(id);
      if (property != null && property.getType() == PropertyType.DATETIME)
         return property.getValues().toArray(new Calendar[property.getValues().size()]);
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public BigDecimal getDecimal(String id)
   {
      Property<?> property = getProperty(id);
      if (property != null && property.getType() == PropertyType.DECIMAL)
         return (BigDecimal)getValue(property);
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public BigDecimal[] getDecimals(String id)
   {
      Property<?> property = getProperty(id);
      if (property != null && property.getType() == PropertyType.DECIMAL)
         return property.getValues().toArray(new BigDecimal[property.getValues().size()]);
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public String getHTML(String id)
   {
      Property<?> property = getProperty(id);
      if (property != null && property.getType() == PropertyType.HTML)
         return (String)getValue(property);
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public String[] getHTMLs(String id)
   {
      Property<?> property = getProperty(id);
      if (property != null && property.getType() == PropertyType.HTML)
         return property.getValues().toArray(new String[property.getValues().size()]);
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public String getId(String id)
   {
      Property<?> property = getProperty(id);
      if (property != null && property.getType() == PropertyType.ID)
         return (String)getValue(property);
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public String[] getIds(String id)
   {
      Property<?> property = getProperty(id);
      if (property != null && property.getType() == PropertyType.ID)
         return property.getValues().toArray(new String[property.getValues().size()]);
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public BigInteger getInteger(String id)
   {
      Property<?> property = getProperty(id);
      if (property != null && property.getType() == PropertyType.INTEGER)
         return (BigInteger)getValue(property);
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public BigInteger[] getIntegers(String id)
   {
      Property<?> property = getProperty(id);
      if (property != null && property.getType() == PropertyType.INTEGER)
         return property.getValues().toArray(new BigInteger[property.getValues().size()]);
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public String getString(String id)
   {
      Property<?> property = getProperty(id);
      if (property != null && property.getType() == PropertyType.STRING)
         return (String)getValue(property);
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public String[] getStrings(String id)
   {
      Property<?> property = getProperty(id);
      if (property != null && property.getType() == PropertyType.STRING)
         return property.getValues().toArray(new String[property.getValues().size()]);
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public URI getURI(String id)
   {
      Property<?> property = getProperty(id);
      if (property != null && property.getType() == PropertyType.URI)
         return (URI)getValue(property);
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public URI[] getURIs(String id)
   {
      Property<?> property = getProperty(id);
      if (property != null && property.getType() == PropertyType.URI)
         return property.getValues().toArray(new URI[property.getValues().size()]);
      return null;
   }

   protected <V> void validate(Property<V> value)
   {
      PropertyDefinition<?> def = type.getPropertyDefinition(value.getId());

      if (def == null)
         throw new ConstraintException("Property " + value.getId() + " is not in property definitions list of type "
            + type.getId());

      if (value.getType() != def.getPropertyType())
         throw new ConstraintException("Property type is not match.");

      if (!def.isMultivalued() && value.getValues().size() > 1)
         throw new ConstraintException("Property " + value.getId() + " is not multi-valued.");

      if (def.isRequired() && value.getValues().size() == 0)
         throw new ConstraintException("Required property " + value.getId() + " can't be removed.");

      // TODO : validate min/max/length
   }

   // --------------- Implementation ----------------

   protected <V> V getValue(Property<V> property)
   {
      List<V> v = property.getValues();
      if (v.size() > 0)
         return v.get(0);
      return null;
   }

}
