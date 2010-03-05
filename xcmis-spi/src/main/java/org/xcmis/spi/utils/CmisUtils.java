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

package org.xcmis.spi.utils;

import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisPropertyId;
import org.xcmis.spi.CMIS;

import java.util.Calendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public final class CmisUtils
{

   /**
    * Private constructor.
    */
   private CmisUtils()
   {
   }

   /**
    * Get XMLGregorianCalendar that is based on Calendar.
    * 
    * @param calendar source Calendar
    * @return XMLGregorianCalendar
    */
   public static XMLGregorianCalendar fromCalendar(Calendar calendar)
   {
      XMLGregorianCalendar xmlCalendar;
      try
      {
         xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar();
      }
      catch (DatatypeConfigurationException e)
      {
         String msg = "Unable get XMLGregorianCalendar.";
         throw new RuntimeException(msg, e);
      }
      xmlCalendar.setYear(calendar.get(Calendar.YEAR));
      xmlCalendar.setMonth(calendar.get(Calendar.MONTH) + 1);
      xmlCalendar.setDay(calendar.get(Calendar.DAY_OF_MONTH));
      xmlCalendar.setTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar
         .get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND));
      return xmlCalendar;
   }

   /**
    * Get object id from "cmis:objectId" property.
    * 
    * @param cmis the CMIS Object Type.
    * @return the object id property.
    */
   public static String getObjectId(CmisPropertiesType cmis)
   {
      return ((CmisPropertyId)getProperty(cmis, CMIS.OBJECT_ID)).getValue().get(0);
   }

   /**
    * Get property from CMIS object type with provided property name.
    * 
    * @param cmis the CMIS object type.
    * @param propName the property name.
    * @return the CMIS property.
    */
   public static CmisProperty getProperty(CmisPropertiesType cmis, String propName)
   {
      List<CmisProperty> props = cmis.getProperty();
      for (CmisProperty prop : props)
      {
         if (prop.getPropertyDefinitionId().equals(propName))
            return prop;
      }
      return null;
   }

}
