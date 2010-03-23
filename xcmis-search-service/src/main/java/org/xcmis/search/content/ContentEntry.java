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
package org.xcmis.search.content;

import org.apache.commons.lang.Validate;

/**
 *  Describes the smallest piece of content.
 */
public class ContentEntry
{
   /**
    *  an array of table names which identifies this content
    */
   private final String[] tableNames;

   /**
    * Name of the entry
    */
   private final String name;

   /**
    * An array of parent entry identifiers
    */
   private final String[] parentIdentifers;

   /**
    *  Entry identifier.
    */
   private final String identifer;

   /**
    * An array of entry properties
    */
   private final Property[] properties;

   /**
    * @param name
    * @param tableNames
    * @param identifer
    * @param parentIdentifers
    * @param properties
    */
   public ContentEntry(String name, String[] tableNames, String identifer, String[] parentIdentifers,
      Property[] properties)
   {
      Validate.notNull(name, "The value argument may not be null");
      Validate.notEmpty(tableNames, "The tableNames may not be empty");
      Validate.notNull(identifer, "The identifer argument may not be null");
      Validate.notEmpty(parentIdentifers, "The parentIdentifers may not be empty");
      this.name = name;
      this.tableNames = tableNames;
      this.identifer = identifer;
      this.parentIdentifers = parentIdentifers;
      this.properties = properties;
   }

   /**
    * @return the tableNames
    */
   public String[] getTableNames()
   {
      return tableNames;
   }

   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }

   /**
    * @return the parentIdentifers
    */
   public String[] getParentIdentifers()
   {
      return parentIdentifers;
   }

   /**
    * @return the identifer
    */
   public String getIdentifer()
   {
      return identifer;
   }

   /**
    * @return the properties
    */
   public Property[] getProperties()
   {
      return properties;
   }

}
