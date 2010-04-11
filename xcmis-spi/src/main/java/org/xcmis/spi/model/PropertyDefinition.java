/*
 * Copyright (C); 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option); any later version.
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

package org.xcmis.spi.model;

import org.xcmis.spi.ConstraintException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class PropertyDefinition<T>
{

   private String id;

   private String queryName;

   private String localName;

   private String localNamespace;

   private String displayName;

   private String description;

   private PropertyType propertyType;

   private Updatability updatability;

   private boolean inherited;

   private boolean required;

   private boolean queryable;

   private boolean orderable;

   private Boolean openChoice;

   private boolean multivalued;

   private List<Choice<T>> choices;

   private DateResolution dateResolution;

   private Precision decimalPrecision;

   private T[] defaultValue;

   private int maxLength;

   private BigInteger minInteger;

   private BigInteger maxInteger;

   private BigDecimal minDecimal;

   private BigDecimal maxDecimal;

   public PropertyDefinition(String id, String queryName, String localName, String localNamespace,
      String displayName, String description, PropertyType propertyType, Updatability updatability, boolean inherited,
      boolean required, boolean queryable, boolean orderable, Boolean openChoice, boolean multivalued,
      List<Choice<T>> choices, T[] defaultValue)
   {
      this.id = id;
      this.queryName = queryName;
      this.localName = localName;
      this.localNamespace = localNamespace;
      this.displayName = displayName;
      this.description = description;
      this.propertyType = propertyType;
      this.updatability = updatability;
      this.inherited = inherited;
      this.required = required;
      this.queryable = queryable;
      this.orderable = orderable;
      this.openChoice = openChoice;
      this.multivalued = multivalued;
      this.choices = choices;
      this.defaultValue = defaultValue;
   }

   public PropertyDefinition()
   {
   }

   /**
    * @return property id
    */
   public String getId()
   {
      return id;
   }

   /**
    * Property Query name. It is used in SQL queries as column name.
    *
    * @return property query name
    */
   public String getQueryName()
   {
      return queryName;
   }

   /**
    * Local (internal) property name.
    *
    * @return property local name
    */
   public String getLocalName()
   {
      return localName;
   }

   /**
    * Local (internal) name-space for property.
    *
    * @return property local name-space
    */
   public String getLocalNamespace()
   {
      return localNamespace;
   }

   /**
    * Optional property display name. It may be used in representation purposes.
    *
    * @return display name or <code>null</code> if not provided
    */
   public String getDisplayName()
   {
      return displayName;
   }

   /**
    * Optional property description.
    *
    * @return property description or <code>null</code> if not provided
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * @return type of property
    * @see PropertyType
    */
   public PropertyType getPropertyType()
   {
      return propertyType;
   }

   /**
    * When property may be updated.
    *
    * @return property updatability
    * @see Updatability
    */
   public Updatability getUpdatability()
   {
      return updatability;
   }

   /**
    * Is property inherited from the super type or defined directly for type
    * provides property definition.
    *
    * @return <code>true</code> if inherited <code>false</code> otherwise
    */
   public Boolean getInherited()
   {
      return inherited;
   }

   /**
    * Is property required. If required it minds property may be never set it
    * 'value not set' state.
    *
    * @return <code>true</code> if property required <code>false</code>
    *         otherwise
    */
   public boolean isRequired()
   {
      return required;
   }

   /**
    * Is property queryable. It indicates can it be used in <code>WHERE</code>
    * clause of SQL statement.
    *
    * @return <code>true</code> if property queryable <code>false</code>
    *         otherwise
    */
   public boolean isQueryable()
   {
      return queryable;
   }

   /**
    * Is property orderable. It indicates can it be used in <code>ORDER</code>
    * clause of SQL statement.
    *
    * @return <code>true</code> if property orderable <code>false</code>
    *         otherwise
    */
   public boolean isOrderable()
   {
      return orderable;
   }

   /**
    * Indicates is choice for property value is open. If <code>false</code> then
    * value of property must be one of provided by {@link #getChoices()} If
    * <code>true</code> then value of can be other then provided by method
    * described above. This attribute should be provide only for properties that
    * provides choices (method {@link #getChoices()} returns other then
    * <code>null</code> or empty list). For other properties this method should
    * return <code>null</code>.
    *
    * @return <code>true</code> if choice of value of property is open
    *         <code>false</code> otherwise and <code>null</code> for properties
    *         that not provide choices
    */
   public Boolean isOpenChoice()
   {
      return openChoice;
   }

   /**
    * @return choices for property value
    * @see Choice
    */
   public List<Choice<T>> getChoices()
   {
      if (choices == null)
      {
         choices = new ArrayList<Choice<T>>();
      }
      return choices;
   }

   /**
    * @return precision supported for DateTime property. There is no sense for
    *         properties other then DateTime and should be <code>null</code>
    */
   public DateResolution getDateResolution()
   {
      return dateResolution;
   }

   /**
    * @return decimal property precision. There is no sense for properties other
    *         then {@link BigDecimal} and should be <code>null</code>
    */
   public Precision getDecimalPrecision()
   {
      return decimalPrecision;
   }

   /**
    * @return default property value. This value may be used if value for
    *         property is not provided
    */
   public T[] getDefaultValue()
   {
      return defaultValue;
   }

   /**
    * @return maximal value for {@link BigDecimal} properties. There is no sense
    *         for properties other then {@link BigDecimal} and should be
    *         <code>null</code>. {@link ConstraintException} should be throw is
    *         application tries set value greater then this value
    */
   public BigDecimal getMaxDecimal()
   {
      return maxDecimal;
   }

   /**
    * @return minimal value for {@link BigDecimal} properties. There is no sense
    *         for properties other then {@link BigDecimal} and should be
    *         <code>null</code>. {@link ConstraintException} should be throw is
    *         application tries set value lower then this value
    */
   public BigDecimal getMinDecimal()
   {
      return minDecimal;
   }

   /**
    * @return maximal value for {@link BigInteger} properties. There is no sense
    *         for properties other then {@link BigInteger} and should be
    *         <code>null</code>. {@link ConstraintException} should be throw is
    *         application tries set value greater then this value
    */
   public BigInteger getMaxInteger()
   {
      return maxInteger;
   }

   /**
    * @return minimal value for {@link BigInteger} properties. There is no sense
    *         for properties other then {@link BigInteger} and should be
    *         <code>null</code>. {@link ConstraintException} should be throw is
    *         application tries set value lower then this value
    */
   public BigInteger getMinInteger()
   {
      return minInteger;
   }

   /**
    * @return max length for String properties. There is no sense for other
    *         property types and should be -1
    */
   public int getMaxLength()
   {
      return maxLength;
   }

   /**
    * Indicates is property id multi-valued or not.
    *
    * @return <code>true</code> if property is multi-valued and
    *         <code>false</code> otherwise
    */
   public boolean isMultivalued()
   {
      return multivalued;
   }

   // -------------------- Setters -------------------

   public void setId(String id)
   {
      this.id = id;
   }

   public void setQueryName(String queryName)
   {
      this.queryName = queryName;
   }

   public void setLocalName(String localName)
   {
      this.localName = localName;
   }

   public void setLocalNamespace(String localNamespace)
   {
      this.localNamespace = localNamespace;
   }

   public void setDisplayName(String displayName)
   {
      this.displayName = displayName;
   }

   public void setDescription(String description)
   {
      this.description = description;
   }

   public void setPropertyType(PropertyType propertyType)
   {
      this.propertyType = propertyType;
   }

   public void setUpdatability(Updatability updatability)
   {
      this.updatability = updatability;
   }

   public void setInherited(boolean inherited)
   {
      this.inherited = inherited;
   }

   public void setRequired(boolean required)
   {
      this.required = required;
   }

   public void setQueryable(boolean queryable)
   {
      this.queryable = queryable;
   }

   public void setOrderable(boolean orderable)
   {
      this.orderable = orderable;
   }

   public void setOpenChoice(Boolean openChoice)
   {
      this.openChoice = openChoice;
   }

   public void setMultivalued(boolean multivalued)
   {
      this.multivalued = multivalued;
   }

   public void setChoices(List<Choice<T>> choices)
   {
      this.choices = choices;
   }

   public void setDateResolution(DateResolution dateResolution)
   {
      this.dateResolution = dateResolution;
   }

   public void setDecimalPrecision(Precision decimalPrecision)
   {
      this.decimalPrecision = decimalPrecision;
   }

   public void setDefaultValue(T[] defaultValue)
   {
      this.defaultValue = defaultValue;
   }

   public void setMaxLength(int maxLength)
   {
      this.maxLength = maxLength;
   }

   public void setMinInteger(BigInteger minInteger)
   {
      this.minInteger = minInteger;
   }

   public void setMaxInteger(BigInteger maxInteger)
   {
      this.maxInteger = maxInteger;
   }

   public void setMinDecimal(BigDecimal minDecimal)
   {
      this.minDecimal = minDecimal;
   }

   public void setMaxDecimal(BigDecimal maxDecimal)
   {
      this.maxDecimal = maxDecimal;
   }

}
