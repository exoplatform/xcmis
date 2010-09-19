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

   /** Max memory size allowed to be used for store objects. */
   private long maxMem;

   /** Max number of objects allowed to be added in storage. */
   private long maxItemsNum;

   StorageConfiguration()
   {
   }

   /**
    * @param id the id
    * @param name name
    * @param description description
    * @param maxMem max memory size
    * @param maxItemsNum max objects count
    */
   public StorageConfiguration(String id, String name, String description, long maxMem, long maxItemsNum)
   {
      this.id = id;
      this.name = name;
      this.description = description;
      this.maxMem = maxMem;
      this.maxItemsNum = maxItemsNum;
   }

   /**
    * @return the repository id
    */
   public String getId()
   {
      return id;
   }

   /**
    * @param id storage id
    */
   public void setId(String id)
   {
      this.id = id;
   }

   /**
    * @return the repository name
    */
   public String getName()
   {
      return name;
   }

   /**
    * @param name repository name
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return max number of items allowed to be added in storage
    */
   public long getMaxItemsNum()
   {
      return maxItemsNum;
   }

   /**
    * @param maxItemsNum max number of items allowed to be added in storage
    */
   public void setMaxItemsNum(long maxItemsNum)
   {
      this.maxItemsNum = maxItemsNum;
   }

   /**
    * @return max memory size to be used for store objects
    */
   public long getMaxMem()
   {
      return maxMem;
   }

   /**
    * @param maxMem max memory size to be used for store objects
    */
   public void setMaxMem(long maxMem)
   {
      this.maxMem = maxMem;
   }

   /**
    * @return the repository description
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * @param description description
    */
   public void setDescription(String description)
   {
      this.description = description;
   }

}
