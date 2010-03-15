/*
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

package org.xcmis.spi.impl;

import org.xcmis.spi.Choice;
import org.xcmis.spi.DateResolution;
import org.xcmis.spi.Precision;
import org.xcmis.spi.PropertyDefinition;
import org.xcmis.spi.PropertyType;
import org.xcmis.spi.Updatability;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Simple plain implementation of {@link PropertyDefinition}.
 * 
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class PropertyDefinitionImpl<T> implements PropertyDefinition<T>
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

   private boolean openChoice;

   private boolean multivalued;

   private List<Choice<T>> choices;

   private DateResolution dateResolution;

   private Precision precision;

   private T[] defaultValue;

   private int maxLength;

   private BigInteger minInteger;

   private BigInteger maxInteger;

   private BigDecimal minDecimal;

   private BigDecimal maxDecimal;

   public PropertyDefinitionImpl(String id, String queryName, String localName, String localNamespace,
      String displayName, String description, PropertyType propertyType, Updatability updatability, boolean inherited,
      boolean required, boolean queryable, boolean orderable, boolean openChoice, boolean multivalued,
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

   public PropertyDefinitionImpl()
   {
   }

   /**
    * {@inheritDoc}
    */
   public String getId()
   {
      return id;
   }

   /**
    * {@inheritDoc}
    */
   public String getQueryName()
   {
      return queryName;
   }

   /**
    * {@inheritDoc}
    */
   public String getLocalName()
   {
      return localName;
   }

   /**
    * {@inheritDoc}
    */
   public String getLocalNamespace()
   {
      return localNamespace;
   }

   /**
    * {@inheritDoc}
    */
   public String getDisplayName()
   {
      return displayName;
   }

   /**
    * {@inheritDoc}
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * {@inheritDoc}
    */
   public PropertyType getPropertyType()
   {
      return propertyType;
   }

   /**
    * {@inheritDoc}
    */
   public Updatability getUpdatability()
   {
      return updatability;
   }

   /**
    * {@inheritDoc}
    */
   public Boolean getInherited()
   {
      return inherited;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isRequired()
   {
      return required;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isQueryable()
   {
      return queryable;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isOrderable()
   {
      return orderable;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isOpenChoice()
   {
      return openChoice;
   }

   /**
    * {@inheritDoc}
    */
   public List<Choice<T>> getChoices()
   {
      return choices;
   }

   /**
    * {@inheritDoc}
    */
   public DateResolution getDateResolution()
   {
      return dateResolution;
   }

   /**
    * {@inheritDoc}
    */
   public Precision getDecimalPrecision()
   {
      return precision;
   }

   /**
    * {@inheritDoc}
    */
   public T[] getDefaultValue()
   {
      return defaultValue;
   }

   /**
    * {@inheritDoc}
    */
   public BigDecimal getMaxDecimal()
   {
      return maxDecimal;
   }

   /**
    * {@inheritDoc}
    */
   public BigDecimal getMinDecimal()
   {
      return minDecimal;
   }

   /**
    * {@inheritDoc}
    */
   public BigInteger getMaxInteger()
   {
      return maxInteger;
   }

   /**
    * {@inheritDoc}
    */
   public BigInteger getMinInteger()
   {
      return minInteger;
   }

   /**
    * {@inheritDoc}
    */
   public int getMaxLength()
   {
      return maxLength;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isMultivalued()
   {
      return multivalued;
   }

   // --- Setters.

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

   public void setOpenChoice(boolean openChoice)
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

   public void setPrecision(Precision precision)
   {
      this.precision = precision;
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
