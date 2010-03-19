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

import org.xcmis.search.content.Schema.Column;

/**
 * In memory column definition   
 */
public class InMemoryColumn implements Column
{
   public static final boolean DEFAULT_FULL_TEXT_SEARCHABLE = false;

   private final boolean fullTextSearchable;

   private final String name;

   private final String type;

   public InMemoryColumn(String name, String type)
   {
      this(name, type, DEFAULT_FULL_TEXT_SEARCHABLE);
   }

   public InMemoryColumn(String name, String type, boolean fullTextSearchable)
   {
      this.name = name;
      this.type = type;
      this.fullTextSearchable = fullTextSearchable;

   }

   /**
    * @see org.xcmis.search.content.Schema.Column#getName()
    */
   public String getName()
   {
      return name;
   }

   /**
    * @see org.xcmis.search.content.Schema.Column#getPropertyType()
    */
   public String getPropertyType()
   {
      return type;
   }

   /**
    * @see org.xcmis.search.content.Schema.Column#isFullTextSearchable()
    */
   public boolean isFullTextSearchable()
   {
      return fullTextSearchable;
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return this.name + "(" + type + ")";
   }
}