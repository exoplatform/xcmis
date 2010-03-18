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

package org.xcmis.spi;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface PropertyDefinition<T>
{

   /**
    * @return property id
    */
   String getId();

   /**
    * Local (internal) property name.
    * 
    * @return property local name
    */
   String getLocalName();

   /**
    * Local (internal) name-space for property.
    * 
    * @return property local name-space
    */
   String getLocalNamespace();

   /**
    * Optional property display name. It may be used in representation purposes.
    * 
    * @return display name or <code>null</code> if not provided
    */
   String getDisplayName();

   /**
    * Property Query name. It is used in SQL queries as column name.
    * 
    * @return property query name
    */
   String getQueryName();

   /**
    * Optional property description.
    * 
    * @return property description or <code>null</code> if not provided
    */
   String getDescription();

   /**
    * @return type of property
    * @see PropertyType
    */
   PropertyType getPropertyType();

   /**
    * When property may be updated.
    * 
    * @return property updatability
    * @see Updatability
    */
   Updatability getUpdatability();

   /**
    * Is property inherited from the super type or defined directly for type
    * provides property definition.
    * 
    * @return <code>true</code> if inherited <code>false</code> otherwise
    */
   Boolean getInherited();

   /**
    * Is property required. If required it minds property may be never set it
    * 'value not set' state.
    * 
    * @return <code>true</code> if property required <code>false</code>
    *         otherwise
    */
   boolean isRequired();

   /**
    * Is property queryable. It indicates can it be used in <code>WHERE</code>
    * clause of SQL statement.
    * 
    * @return <code>true</code> if property queryable <code>false</code>
    *         otherwise
    */
   boolean isQueryable();

   /**
    * Is property orderable. It indicates can it be used in <code>ORDER</code>
    * clause of SQL statement.
    * 
    * @return <code>true</code> if property orderable <code>false</code>
    *         otherwise
    */
   boolean isOrderable();

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
   Boolean isOpenChoice();

   /**
    * Indicates is property id multi-valued or not.
    * 
    * @return <code>true</code> if property is multi-valued and
    *         <code>false</code> otherwise
    */
   boolean isMultivalued();

   /**
    * @return choices for property value
    * @see Choice
    */
   List<Choice<T>> getChoices();

   /**
    * @return default property value. This value may be used if value for
    *         property is not provided
    */
   T[] getDefaultValue();

   /**
    * @return max length for String properties. There is no sense for other
    *         property types and should be -1
    */
   int getMaxLength();

   /**
    * @return maximal value for {@link BigInteger} properties. There is no sense
    *         for properties other then {@link BigInteger} and should be
    *         <code>null</code>. {@link ConstraintException} should be throw is
    *         application tries set value greater then this value
    */
   BigInteger getMaxInteger();

   /**
    * @return minimal value for {@link BigInteger} properties. There is no sense
    *         for properties other then {@link BigInteger} and should be
    *         <code>null</code>. {@link ConstraintException} should be throw is
    *         application tries set value lower then this value
    */
   BigInteger getMinInteger();

   /**
    * @return maximal value for {@link BigDecimal} properties. There is no sense
    *         for properties other then {@link BigDecimal} and should be
    *         <code>null</code>. {@link ConstraintException} should be throw is
    *         application tries set value greater then this value
    */
   BigDecimal getMaxDecimal();

   /**
    * @return minimal value for {@link BigDecimal} properties. There is no sense
    *         for properties other then {@link BigDecimal} and should be
    *         <code>null</code>. {@link ConstraintException} should be throw is
    *         application tries set value lower then this value
    */
   BigDecimal getMinDecimal();

   /**
    * @return precision supported for DateTime property. There is no sense for
    *         properties other then DateTime and should be <code>null</code>
    */
   DateResolution getDateResolution();

   /**
    * @return decimal property precision. There is no sense for properties other
    *         then {@link BigDecimal} and should be <code>null</code>
    */
   Precision getDecimalPrecision();
}
