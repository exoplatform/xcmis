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

import org.xcmis.core.CmisPropertyDateTime;
import org.xcmis.core.CmisPropertyDefinitionType;
import org.xcmis.core.EnumPropertyType;
import org.xcmis.spi.utils.CmisUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class DateTimePropertyData extends AbstractPropertyData<Calendar, CmisPropertyDateTime>
{

   public DateTimePropertyData(CmisPropertyDefinitionType propDef, Calendar value)
   {
      super(propDef, value);
   }

   public DateTimePropertyData(CmisPropertyDefinitionType propDef, List<Calendar> values)
   {
      super(propDef, values);
   }

   public DateTimePropertyData(CmisPropertyDateTime property)
   {
      this.propertyId = property.getPropertyDefinitionId();
      this.queryName = property.getQueryName();
      this.displayName = property.getDisplayName();
      this.localName = property.getLocalName();
      this.values = new ArrayList<Calendar>(property.getValue().size());
      for (XMLGregorianCalendar xmlgc : property.getValue())
         this.values.add(xmlgc.toGregorianCalendar());
   }

   public DateTimePropertyData(DateTimePropertyData a)
   {
      super(a);
   }

   public CmisPropertyDateTime getProperty()
   {
      CmisPropertyDateTime date = new CmisPropertyDateTime();
      date.setDisplayName(displayName);
      date.setLocalName(localName);
      date.setPropertyDefinitionId(propertyId);
      date.setQueryName(queryName);
      for (Calendar value : values)
      {
         if (value != null)
            date.getValue().add(CmisUtils.fromCalendar(value));
      }
      return date;
   }

   public EnumPropertyType getPropertyType()
   {
      return EnumPropertyType.DATETIME;
   }

   public void updateFromProperty(CmisPropertyDateTime property)
   {
      if (property != null)
      {
         List<Calendar> v = new ArrayList<Calendar>(property.getValue().size());
         for (XMLGregorianCalendar xmlgc : property.getValue())
            v.add(xmlgc.toGregorianCalendar());
         setValues(v);
      }
      else
      {
         setValues(null); // reset values
      }
   }

}
