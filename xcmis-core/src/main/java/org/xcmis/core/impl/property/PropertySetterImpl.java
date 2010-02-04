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

package org.xcmis.core.impl.property;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisPropertyBoolean;
import org.xcmis.core.CmisPropertyDateTime;
import org.xcmis.core.CmisPropertyDecimal;
import org.xcmis.core.CmisPropertyHtml;
import org.xcmis.core.CmisPropertyId;
import org.xcmis.core.CmisPropertyInteger;
import org.xcmis.core.CmisPropertyString;
import org.xcmis.core.CmisPropertyUri;
import org.xcmis.core.EnumPropertyType;
import org.xcmis.spi.object.Entry;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com.ua>Alexey Zavizionov</a>
 * @version $Id$
 */
public class PropertySetterImpl implements PropertySetter
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(PropertySetterImpl.class);

   /**
    * {@inheritDoc}
    */
   public void setProperty(EnumPropertyType propertyType, Entry cmis, CmisProperty property)
      throws org.xcmis.spi.RepositoryException
   {
      String propertyName = property.getLocalName();
      if (propertyType == EnumPropertyType.BOOLEAN)
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Boolean property, name " + propertyName);
         List<Boolean> value = ((CmisPropertyBoolean)property).getValue();
         if (value.size() == 1)
         {
            cmis.setBoolean(propertyName, value.get(0));
         }
         else if (value.size() > 1)
         {
            boolean[] bvalues = new boolean[value.size()];
            for (int i = 0; i < bvalues.length; i++)
               bvalues[i] = value.get(i);
            cmis.setBooleans(propertyName, bvalues);
         }
      }
      else if (propertyType == EnumPropertyType.DATETIME)
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Date property, name " + propertyName);
         List<XMLGregorianCalendar> value = ((CmisPropertyDateTime)property).getValue();
         if (value.size() == 1)
         {
            cmis.setDate(propertyName, value.get(0).toGregorianCalendar());
         }
         else if (value.size() > 1)
         {
            Calendar[] dates = new Calendar[value.size()];
            for (int i = 0; i < dates.length; i++)
               dates[i] = value.get(i).toGregorianCalendar();
            cmis.setDates(propertyName, dates);
         }
      }
      else if (propertyType == EnumPropertyType.DECIMAL)
      {
         if (LOG.isDebugEnabled())
            LOG.debug("BigDecimal property, name " + propertyName);
         List<BigDecimal> value = ((CmisPropertyDecimal)property).getValue();
         if (value.size() == 1)
            cmis.setDecimal(propertyName, value.get(0));
         else if (value.size() > 1)
            cmis.setDecimals(propertyName, value.toArray(new BigDecimal[value.size()]));
      }
      else if (propertyType == EnumPropertyType.HTML)
      {
         if (LOG.isDebugEnabled())
            LOG.debug("HTML property, name " + propertyName);
         List<String> value = ((CmisPropertyHtml)property).getValue();
         if (value.size() == 1)
            cmis.setString(propertyName, value.get(0));
         else if (value.size() > 1)
            cmis.setStrings(propertyName, value.toArray(new String[value.size()]));
      }
      else if (propertyType == EnumPropertyType.ID)
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Id property, name " + propertyName);
         List<String> value = ((CmisPropertyId)property).getValue();
         // no special type for id use string instead
         if (value.size() == 1)
            cmis.setString(propertyName, value.get(0));
         else if (value.size() > 1)
            cmis.setStrings(propertyName, value.toArray(new String[value.size()]));
      }
      else if (propertyType == EnumPropertyType.INTEGER)
      {
         if (LOG.isDebugEnabled())
            LOG.debug("BigInteger property, name " + propertyName);
         List<BigInteger> value = ((CmisPropertyInteger)property).getValue();
         if (value.size() == 1)
            cmis.setInteger(propertyName, value.get(0));
         else if (value.size() > 1)
            cmis.setIntegers(propertyName, value.toArray(new BigInteger[value.size()]));
      }
      else if (propertyType == EnumPropertyType.STRING)
      {
         if (LOG.isDebugEnabled())
            LOG.debug("String property, name " + propertyName);
         List<String> value = ((CmisPropertyString)property).getValue();
         if (value.size() == 1)
            cmis.setString(propertyName, value.get(0));
         else if (value.size() > 1)
            cmis.setStrings(propertyName, value.toArray(new String[value.size()]));
      }
      else if (propertyType == EnumPropertyType.URI)
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Uri property, name " + propertyName);
         List<String> value = ((CmisPropertyUri)property).getValue();
         if (value.size() == 1)
            // save as string, no reason to do double transformation
            cmis.setString(propertyName, value.get(0));
         else if (value.size() > 1)
            // save as strings, no reason to do double transformation
            cmis.setStrings(propertyName, value.toArray(new String[value.size()]));
      }
   }

   /**
    * {@inheritDoc}
    */
   public String getPropertyId()
   {
      return null;
   }

}
