/*
 * Copyright (C) 2010 eXo Platform SAS.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.xcmis.search.content;

import org.apache.commons.lang.Validate;
import org.xcmis.search.model.Query;
import org.xcmis.search.model.constraint.Operator;
import org.xcmis.search.model.source.SelectorName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In memory {@link Schema} implementation.
 */
public class InMemorySchema implements Schema
{
   /**
    * Obtain a new instance for building Schema objects.
    * 
    * @return the new builder; never null
    */
   public static Builder createBuilder()
   {
      return new Builder();
   }

   /**
    * A builder of immutable {@link Schema} objects.
    */

   public static class Builder
   {

      private final Map<SelectorName, InMemoryTable> tables = new HashMap<SelectorName, InMemoryTable>();

      private final Map<SelectorName, Query> viewDefinitions = new HashMap<SelectorName, Query>();

      protected Builder()
      {
      }

      /**
       * Add a table with the supplied name and column names. Each column will
       * be given a default type. The table will also overwrite any existing
       * table definition with the same name.
       * 
       * @param name
       *           the name of the new table
       * @param columnNames
       *           the names of the columns.
       * @return this builder, for convenience in method chaining; never null
       */
      public Builder addTable(String name, String... columnNames)
      {
         Validate.notEmpty(name, " name may not be empty");
         Validate.notEmpty(columnNames, "columnNames may not be empty");
         List<Column> columns = new ArrayList<Column>();
         int i = 0;
         for (String columnName : columnNames)
         {
            Validate.notEmpty(columnName, "columnName[" + (i++) + "] may not be empty");;
            // TODO default type
            columns.add(new InMemoryColumn(columnName, "String"));
         }
         InMemoryTable table = new InMemoryTable(new SelectorName(name), columns);
         tables.put(table.getName(), table);
         return this;
      }

      /**
       * Add a table with the supplied name and column names and types. The
       * table will also overwrite any existing table definition with the same
       * name.
       * 
       * @param name
       *           the name of the new table
       * @param columnNames
       *           the names of the columns
       * @param types
       *           the types for the columns
       * @return this builder, for convenience in method chaining; never null
       */
      public Builder addTable(String name, String[] columnNames, String[] types)
      {
         Validate.notEmpty(name, " name may not be empty");
         Validate.notEmpty(columnNames, " columnNames may not be empty");
         Validate.notEmpty(types, " types may not be empty");
         Validate.isTrue(columnNames.length == types.length, "columnNames.length should be equal types.length");
         List<Column> columns = new ArrayList<Column>();
         assert columnNames.length == types.length;
         for (int i = 0; i != columnNames.length; ++i)
         {
            String columnName = columnNames[i];
            Validate.notEmpty(columnName, " columnName[" + i + "] may not be empty");
            columns.add(new InMemoryColumn(columnName, types[i]));
         }
         InMemoryTable table = new InMemoryTable(new SelectorName(name), columns);
         tables.put(table.getName(), table);
         return this;
      }

      /**
       * Add a column with the supplied name and type to the named table. Any
       * existing column with that name will be replaced with the new column. If
       * the table does not yet exist, it will be added.
       * 
       * @param tableName
       *           the name of the new table
       * @param columnName
       *           the names of the column
       * @param type
       *           the type for the column
       * @return this builder, for convenience in method chaining; never null
       */
      public Builder addColumn(String tableName, String columnName, String type)
      {
         Validate.notEmpty(tableName, " tableName may not be empty");
         Validate.notEmpty(columnName, " columnName may not be empty");
         Validate.notNull(type, " type may not be null");

         return addColumn(tableName, columnName, type, InMemoryColumn.DEFAULT_FULL_TEXT_SEARCHABLE, Operator.ALL);
      }

      /**
       * Add a column with the supplied name and type to the named table. Any
       * existing column with that name will be replaced with the new column. If
       * the table does not yet exist, it will be added.
       * 
       * @param tableName
       *           the name of the new table
       * @param columnName
       *           the names of the column
       * @param type
       *           the type for the column
       * @param fullTextSearchable
       *           true if the column should be full-text searchable, or false
       *           if not
       * @return this builder, for convenience in method chaining; never null
       */
      public Builder addColumn(String tableName, String columnName, String type, boolean fullTextSearchable,
         Operator[] availableQueryOperators)
      {
         Validate.notEmpty(tableName, " tableName may not be empty");
         Validate.notEmpty(columnName, " columnName may not be empty");
         Validate.notNull(type, " type may not be null");
         SelectorName selector = new SelectorName(tableName);
         InMemoryTable existing = tables.get(selector);
         InMemoryTable table = null;
         if (existing == null)
         {
            List<Column> columns = new ArrayList<Column>();
            columns.add(new InMemoryColumn(columnName, type, fullTextSearchable, availableQueryOperators));
            table = new InMemoryTable(selector, columns);
         }
         else
         {
            table = existing.withColumn(columnName, type, fullTextSearchable, availableQueryOperators);
         }
         tables.put(table.getName(), table);
         return this;
      }

      /**
       * Make sure the column on the named table is searchable.
       * 
       * @param tableName
       *           the name of the new table
       * @param columnName
       *           the names of the column
       * @return this builder, for convenience in method chaining; never null
       */
      public Builder makeSearchable(String tableName, String columnName)
      {
         Validate.notEmpty(tableName, " tableName may not be empty");
         Validate.notEmpty(columnName, " columnName may not be empty");
         SelectorName selector = new SelectorName(tableName);
         InMemoryTable existing = tables.get(selector);
         InMemoryTable table = null;
         if (existing == null)
         {
            List<Column> columns = new ArrayList<Column>();
            // TODO default type
            columns.add(new InMemoryColumn(columnName, "String", true, Operator.ALL));
            table = new InMemoryTable(selector, columns);
         }
         else
         {
            Column column = existing.getColumn(columnName);
            // TODO default type
            String type = "String";
            if (column != null)
            {
               type = column.getPropertyType();
            }
            table = existing.withColumn(columnName, type, true, column.getAvailableQueryOperators());
         }
         tables.put(table.getName(), table);
         return this;
      }

      /**
       * Build the {@link Schema} instance, using the current state of the
       * builder. This method creates a snapshot of the tables (with their
       * columns) as they exist at the moment this method is called.
       * 
       * @return the new Schema; never null
       */
      public Schema build()
      {
         InMemorySchema schemata = new InMemorySchema(new HashMap<SelectorName, Table>(tables));

         return schemata;
      }
   }

   private final Map<SelectorName, Table> tables;

   protected InMemorySchema(Map<SelectorName, Table> tables)
   {
      this.tables = Collections.unmodifiableMap(tables);
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.modeshape.graph.query.validate.Schema#getTable(org.modeshape.graph.query.model.SelectorName)
    */
   public Table getTable(SelectorName name)
   {
      return tables.get(name);
   }

   public InMemorySchema with(Table table)
   {
      Map<SelectorName, Table> tables = new HashMap<SelectorName, Table>(this.tables);
      tables.put(table.getName(), table);
      return new InMemorySchema(tables);
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();
      boolean first = true;
      for (Table table : tables.values())
      {
         if (first)
         {
            first = false;
         }
         else
         {
            sb.append('\n');
         }
         sb.append(table);
      }
      return sb.toString();
   }

}
