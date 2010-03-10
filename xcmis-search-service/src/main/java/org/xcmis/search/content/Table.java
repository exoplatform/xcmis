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

import org.xcmis.search.model.source.SelectorName;

import java.util.List;
import java.util.Map;

/**
 * Information about a queryable table.
 */
public interface Table
{
   /**
    * Get the name for this table.
    * 
    * @return the table name; never null
    */
   SelectorName getName();

   /**
    * Get the information for a column with the supplied name within this table.
    * <p>
    * The resulting column definition is immutable.
    * </p>
    * 
    * @param name the column name; may not be null
    * @return the column information, or null if there is no such column
    */
   Column getColumn(String name);

   /**
    * Get the queryable columns in this table.
    * 
    * @return the immutable map of immutable column objects by their name; never null
    */
   Map<String, Column> getColumnsByName();

   /**
    * Get the queryable columns in this table.
    * 
    * @return the immutable, ordered list of immutable column objects; never null
    */
   List<Column> getColumns();
}
