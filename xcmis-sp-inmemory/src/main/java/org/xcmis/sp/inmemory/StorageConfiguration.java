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

package org.xcmis.sp.inmemory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class StorageConfiguration
{
   /** Storage id. */
   private String id;

   /** Additional properties. */
   private Map<String, Object> properties;

   public static String MAX_STORAGE_MEM_SIZE = "org.xcmis.inmemory.maxmem";

   public static String MAX_ITEMS_NUMBER = "org.xcmis.inmemory.maxitems";

   public static int DEFAULT_MAX_STORAGE_MEM_SIZE = 104857600; // 100MB

   public static int DEFAULT_MAX_STORAGE_NUMBER_ITEMS = 100;

   public StorageConfiguration()
   {
   }

   /**
    * @param id the id
    * @param properties the properties
    */
   public StorageConfiguration(String id, Map<String, Object> properties)
   {
      this.id = id;
      this.properties = properties;
   }

   /**
    * Get repository id.
    *
    * @return the repository id
    */
   public String getId()
   {
      return id;
   }

   /**
    * @return properties
    */
   public Map<String, Object> getProperties()
   {
      if (properties == null)
      {
         properties = new HashMap<String, Object>();
      }
      return properties;
   }

   /**
    * @param id storage id
    */
   public void setId(String id)
   {
      this.id = id;
   }

   /**
    * @param properties properties
    */
   public void setProperties(Map<String, Object> properties)
   {
      this.properties = properties;
   }

   public static Double parseNumber(String text)
   {
      text = text.toUpperCase();

      if (text.endsWith("K"))
      {
         return Double.valueOf(text.substring(0, text.length() - 1)) * 1024d;
      }
      else if (text.endsWith("KB"))
      {
         return Double.valueOf(text.substring(0, text.length() - 2)) * 1024d;
      }
      else if (text.endsWith("M"))
      {
         return Double.valueOf(text.substring(0, text.length() - 1)) * 1048576d; // 1024 * 1024
      }
      else if (text.endsWith("MB"))
      {
         return Double.valueOf(text.substring(0, text.length() - 2)) * 1048576d; // 1024 * 1024
      }
      else if (text.endsWith("G"))
      {
         return Double.valueOf(text.substring(0, text.length() - 1)) * 1073741824d; // 1024 * 1024 * 1024
      }
      else if (text.endsWith("GB"))
      {
         return Double.valueOf(text.substring(0, text.length() - 2)) * 1073741824d; // 1024 * 1024 * 1024
      }
      else if (text.endsWith("T"))
      {
         return Double.valueOf(text.substring(0, text.length() - 1)) * 1099511627776d; // 1024 * 1024 * 1024 * 1024
      }
      else if (text.endsWith("TB"))
      {
         return Double.valueOf(text.substring(0, text.length() - 2)) * 1099511627776d; // 1024 * 1024 * 1024 * 1024
      }
      else
      {
         return Double.valueOf(text);
      }
   }
}
