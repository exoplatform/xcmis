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

package org.xcmis.sp.inmemory;

import org.xcmis.core.CmisPropertyDecimal;
import org.xcmis.core.CmisPropertyDefinitionType;
import org.xcmis.core.EnumPropertyType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class DecimalPropertyData extends AbstractPropertyData<BigDecimal, CmisPropertyDecimal>
{

   public DecimalPropertyData(CmisPropertyDefinitionType propDef, BigDecimal value)
   {
      super(propDef, value);
   }

   public DecimalPropertyData(CmisPropertyDefinitionType propDef, List<BigDecimal> values)
   {
      super(propDef, values);
   }

   public DecimalPropertyData(CmisPropertyDecimal property)
   {
      this.propertyId = property.getPropertyDefinitionId();
      this.queryName = property.getQueryName();
      this.displayName = property.getDisplayName();
      this.localName = property.getLocalName();
      this.values = new ArrayList<BigDecimal>(property.getValue());
   }

   public DecimalPropertyData(DecimalPropertyData a)
   {
      super(a);
   }

   public CmisPropertyDecimal getProperty()
   {
      CmisPropertyDecimal decimal = new CmisPropertyDecimal();
      decimal.setDisplayName(displayName);
      decimal.setLocalName(localName);
      decimal.setPropertyDefinitionId(propertyId);
      decimal.setQueryName(queryName);
      decimal.getValue().addAll(values);
      return decimal;
   }

   public EnumPropertyType getPropertyType()
   {
      return EnumPropertyType.DECIMAL;
   }

   public void updateFromProperty(CmisPropertyDecimal property)
   {
      if (property != null)
         setValues(property.getValue());
      else
         setValues(null); // reset values
   }

}
