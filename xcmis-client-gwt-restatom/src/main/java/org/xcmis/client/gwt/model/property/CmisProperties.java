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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class CmisProperties
{
   private Map<String, Property<?>> properties;

   /**
    * @param properties
    */
   public CmisProperties(Map<String, Property<?>> properties)
   {
      this.properties = properties;
   }
   
   /**
    * @param properties properties
    */
   public void setProperties(Map<String, Property<?>> properties)
   {
      this.properties = properties;
   }

   /**
    * @return Map<String, Property<?>> properties map
    */
   public Map<String, Property<?>> getProperties()
   {
      if (properties == null)
      {
         properties = new HashMap<String, Property<?>>();
      }
      return properties;
   }

   public Property<?> getProperty(String id)
   {
      if (properties.get(id) == null)
      {
         return null;
      }
      else
      {
         return properties.get(id);
      }
   }

   public Boolean getBoolean(String id)
   {
      if (properties.get(id) != null)
      {
         List<?> values = properties.get(id).getValues();
         if (values.size() > 0)
         {
            return (Boolean)values.get(0);
         }
      }
      return null;
   }

   public Boolean[] getBooleans(String id)
   {
      if (properties.get(id) != null)
      {
         List<?> values = properties.get(id).getValues();
         if (values.size() > 0)
         {
            return values.toArray(new Boolean[values.size()]);
         }
      }
      return null;
   }

   public Date getDate(String id)
   {
      if (properties.get(id) != null)
      {
         List<?> values = properties.get(id).getValues();
         if (values.size() > 0)
         {
            return (Date)values.get(0);
         }
      }
      return null;
   }

   public Date[] getDates(String id)
   {
      if (properties.get(id) != null)
      {
         List<?> values = properties.get(id).getValues();
         if (values.size() > 0)
         {
            return values.toArray(new Date[values.size()]);
         }
      }
      return null;
   }

   public Double getDecimal(String id)
   {
      if (properties.get(id) != null)
      {
         List<?> values = properties.get(id).getValues();
         if (values.size() > 0)
         {
            return (Double)values.get(0);
         }
      }
      return null;
   }

   public Double[] getDecimals(String id)
   {
      if (properties.get(id) != null)
      {
         List<?> values = properties.get(id).getValues();
         if (values.size() > 0)
         {
            return values.toArray(new Double[values.size()]);
         }
      }
      return null;
   }

   public String getId(String id)
   {
      return getString(id);
   }

   public String[] getIds(String id)
   {
      return getStrings(id);
   }

   public Long getInteger(String id)
   {
      if (properties.get(id) != null)
      {
         List<?> values = properties.get(id).getValues();
         if (values.size() > 0)
         {
            return (Long)values.get(0);
         }
      }
      return null;
   }

   public Long[] getIntegers(String id)
   {
      if (properties.get(id) != null)
      {
         List<?> values = properties.get(id).getValues();
         if (values.size() > 0)
         {
            return values.toArray(new Long[values.size()]);
         }
      }
      return null;
   }

   public String getString(String id)
   {
      if (properties.get(id) != null)
      {
         List<?> values = properties.get(id).getValues();
         if (values.size() > 0)
         {
            return (String)values.get(0);
         }
      }
      return null;
   }

   public String[] getStrings(String id)
   {
      if (properties.get(id) != null)
      {
         List<?> values = properties.get(id).getValues();
         if (values.size() > 0)
         {
            return values.toArray(new String[values.size()]);
         }
      }
      return null;
   }

}
