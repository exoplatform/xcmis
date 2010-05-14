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
import org.xcmis.search.content.Schema.Table;
import org.xcmis.search.model.constraint.Operator;
import org.xcmis.search.model.source.SelectorName;
import org.xcmis.search.value.PropertyType;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * In memory table definition.
 */
public class InMemoryTable implements Table
{
   private final SelectorName name;

   private final Map<String, Column> columnsByName;

   private final List<Column> columns;

   public InMemoryTable(SelectorName name, Iterable<Column> columns)
   {
      this(name, columns, (Iterable<Column>[])null);
   }

   public InMemoryTable(SelectorName name, Iterable<Column> columns, Iterable<Column>... keyColumns)
   {
      this.name = name;
      // Define the columns ...
      List<Column> columnList = new LinkedList<Column>();
      Map<String, Column> columnMap = new HashMap<String, Column>();
      for (Column column : columns)
      {
         Column old = columnMap.put(column.getName(), column);
         if (old != null)
         {
            columnList.set(columnList.indexOf(old), column);
         }
         else
         {
            columnList.add(column);
         }
      }
      this.columnsByName = Collections.unmodifiableMap(columnMap);
      this.columns = Collections.unmodifiableList(columnList);
   }

   public InMemoryTable(SelectorName name, Map<String, Column> columnsByName, List<Column> columns)
   {
      this.name = name;
      this.columns = columns;
      this.columnsByName = columnsByName;
   }

   /**
    * @see org.xcmis.search.content.Schema.Table#getColumn(java.lang.String)
    */
   public Column getColumn(String name)
   {
      return columnsByName.get(name);
   }

   /**
    * @see org.xcmis.search.content.Schema.Table#getColumns()
    */
   public List<Column> getColumns()
   {
      return columns;
   }

   /**
    * @see org.xcmis.search.content.Schema.Table#getColumnsByName()
    */
   public Map<String, Column> getColumnsByName()
   {
      return columnsByName;
   }

   /**
    * @see org.xcmis.search.content.Schema.Table#getName()
    */
   public SelectorName getName()
   {
      return name;
   }

   public InMemoryTable withColumn(String name, PropertyType type)
   {
      List<Column> newColumns = new LinkedList<Column>(columns);
      newColumns.add(new InMemoryColumn(name, type));
      return new InMemoryTable(getName(), newColumns);
   }

   public InMemoryTable withColumn(String name, PropertyType type, boolean fullTextSearchable,
      Operator[] availableQueryOperators)
   {
      List<Column> newColumns = new LinkedList<Column>(columns);
      newColumns.add(new InMemoryColumn(name, type, fullTextSearchable, availableQueryOperators));
      return new InMemoryTable(getName(), newColumns);
   }

   public InMemoryTable withColumns(Iterable<Column> columns)
   {
      List<Column> newColumns = new LinkedList<Column>(this.getColumns());
      for (Column column : columns)
      {
         newColumns.add(new InMemoryColumn(column.getName(), column.getPropertyType(), column.isFullTextSearchable(),
            column.getAvailableQueryOperators()));
      }
      return new InMemoryTable(getName(), newColumns);
   }

   public InMemoryTable with(SelectorName name)
   {
      return new InMemoryTable(name, columnsByName, columns);
   }
}
