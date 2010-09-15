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

   /** Storage name. */
   private String name;

   /** Description. */
   private String description;

   /** Additional properties. */
   private Map<String, Object> properties;

   public static String MAX_STORAGE_MEM_SIZE = "org.xcmis.inmemory.maxmem";

   public static String MAX_ITEMS_NUMBER = "org.xcmis.inmemory.maxitems";

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
      if (properties == null)
        this.properties = new HashMap<String, Object>();
      else
         this.properties = properties;
      if (!this.properties.containsKey(MAX_STORAGE_MEM_SIZE))
         this.properties.put(MAX_STORAGE_MEM_SIZE, -1L);
      if (!this.properties.containsKey(MAX_ITEMS_NUMBER))
         this.properties.put(MAX_ITEMS_NUMBER, -1L);
   }

   /**
    * @param id the id
    * @param name name
    * @param description description
    * @param maxMem max memory size
    * @param maxObjects max objects count
    */
   public StorageConfiguration(String id, String name, String description, long maxMem, long maxObjects)
   {
      this.id = id;
      this.name = name;
      this.description = description;
      this.properties = new HashMap<String, Object>();
      this.properties.put(MAX_STORAGE_MEM_SIZE, maxMem);
      this.properties.put(MAX_ITEMS_NUMBER, maxObjects);
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
    * Get repository description.
    * 
    * @return the repository description
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * Get repository name.
    * 
    * @return the repository name
    */
   public String getName()
   {
      return name;
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
    * @param name repository name
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @param properties properties
    */
   public void setProperties(Map<String, Object> properties)
   {
      this.properties = properties;
   }

   /**
    * @param description description
    */
   public void setDescription(String description)
   {
      this.description = description;
   }
}
