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
    *  an array of table names which identifies this content.
    */
   private final String[] tableNames;

   /**
    * Name of the entry.
    */
   private final String name;

   /**
    * An array of parent entry identifiers.
    */
   private final String[] parentIdentifiers;

   /**
    *  Entry identifier.
    */
   private final String identifier;

   /**
    * An array of entry properties.
    */
   private final Property[] properties;

   /**
    * @param name String name
    * @param tableNames String[]
    * @param identifier String
    * @param parentIdentifiers String[]
    * @param properties Property[]
    */
   public ContentEntry(String name, String[] tableNames, String identifer, String[] parentIdentifiers,
      Property[] properties)
   {
      Validate.notNull(name, "The value argument may not be null");
      Validate.notEmpty(tableNames, "The tableNames may not be empty");
      Validate.notNull(identifer, "The identifier argument may not be null");
      this.name = name;
      this.tableNames = tableNames;
      this.identifier = identifer;
      this.parentIdentifiers = parentIdentifiers;
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
    * @return the parentIdentifiers
    */
   public String[] getParentIdentifiers()
   {
      return parentIdentifiers;
   }

   /**
    * @return the identifier
    */
   public String getIdentifier()
   {
      return identifier;
   }

   /**
    * @return the properties
    */
   public Property[] getProperties()
   {
      return properties;
   }

}
