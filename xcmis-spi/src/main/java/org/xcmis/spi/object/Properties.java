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

package org.xcmis.spi.object;

import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.PropertyFilter;
import org.xcmis.spi.Storage;
import org.xcmis.spi.data.ObjectData;

import java.util.Map;

/**
 * CMIS properties.
 * 
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface Properties
{
   /**
    * @return set of CMIS properties
    */
   Map<String, Property<?>> getAll();

   /**
    * @param id property ID
    * @return property with specified ID or <code>null</code>
    */
   Property<?> getProperty(String id);

   /**
    * Set or update property. Changes may be updated immediately or after
    * calling {@link Storage#saveObject(ObjectData)}. This is implementation
    * specific. <code>null</code> value for property minds the property will be
    * in 'value not set' state. If property is required then
    * {@link ConstraintException} will be thrown.
    * 
    * @param value new property
    * @throws ConstraintException if value of the property violates the
    *         min/max/required/length constraints specified in the property
    *         definition in the object type
    * @throws NameConstraintViolationException if <i>cmis:name</i> specified in
    *         properties throws conflict
    */
   void setProperty(Property<?> value) throws ConstraintException;

   /**
    * Get subset of properties accepted by {@link PropertyFilter}
    * 
    * @param filter property filter
    * @return subset of properties
    */
   Map<String, Property<?>> getSubset(PropertyFilter filter);

   /**
    * Set or add new properties. Properties will be merged with existed one and
    * not replace whole set of existed properties. Changes may be updated
    * immediately or after calling {@link Storage#saveObject(ObjectData)}. This
    * is implementation specific. <code>null</code> value for property minds the
    * property will be in 'value not set' state. If property is required then
    * {@link ConstraintException} will be thrown.
    * 
    * @param values new set of properties
    * @throws ConstraintException if value of any of the properties violates the
    *         min/max/required/length constraints specified in the property
    *         definition in the object type
    * @throws NameConstraintViolationException if <i>cmis:name</i> specified in
    *         properties throws conflict
    */
   void setValues(Map<String, Property<?>> values) throws ConstraintException, NameConstraintViolationException;

}
