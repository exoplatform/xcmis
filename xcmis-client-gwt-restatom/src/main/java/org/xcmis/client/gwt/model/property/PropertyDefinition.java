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

package org.xcmis.client.gwt.model.property;

import java.util.List;

import org.xcmis.client.gwt.model.Choice;
import org.xcmis.client.gwt.model.EnumCardinality;
import org.xcmis.client.gwt.model.EnumPropertyType;
import org.xcmis.client.gwt.model.EnumUpdatability;

/**
 * Simple plain implementation of {@link PropertyDefinition}.
 * 
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
    * @return property local name
    */
   String getLocalName();
   
   /**
    * @return property local namespace
    */
   String getLocalNamespace();
   
   /**
    * @return property query name
    */
   String getQueryName();
   
   /**
    * @return property display name
    */
   String getDisplayName();
   
   /**
    * @return description
    */
   String getDescription();

   /**
    * @return property type
    */
   EnumPropertyType getPropertyType();
   
   /**
    * @return cardinality
    */
   EnumCardinality getCardinality();
   
   /**
    * @return updatability
    */
   EnumUpdatability getUpdatability();
   
   /**
    * @return is inherited
    */
   Boolean isInherited();
   
   /**
    * @return is required
    */
   Boolean isRequired();
   
   /**
    * @return is querable
    */
   Boolean isQueryable();
   
   /**
    * @return is orderable
    */
   Boolean isOrderable();
   
   /**
    * @return choices for property value
    * @see Choice
    */
   List<Choice<T>> getChoices();
   
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
    * @return default property value. This value may be used if value for
    *         property is not provided
    */
   T[] getDefaultValue();
}
