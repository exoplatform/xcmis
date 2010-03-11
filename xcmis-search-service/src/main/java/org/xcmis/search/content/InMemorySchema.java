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

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.Validate;
import org.xcmis.search.InvalidQueryException;
import org.xcmis.search.Visitors;
import org.xcmis.search.model.Query;
import org.xcmis.search.model.source.SelectorName;
import org.xcmis.search.query.QueryExecutionContext;
import org.xcmis.search.query.plan.QueryExecutionPlan;
import org.xcmis.search.query.plan.QueryExecutionStep;
import org.xcmis.search.query.plan.SimplePlaner;
import org.xcmis.search.query.plan.QueryExecutionStep.Type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * In memory {@link Schemata} implementation.
 */
public class InMemorySchema implements Schema
{
   /**
    * Obtain a new instance for building Schemata objects.
    * 
    * @return the new builder; never null
    */
   public static Builder createBuilder()
   {
      return new Builder();
   }

   /**
    * A builder of immutable {@link Schemata} objects.
    */

   public static class Builder
   {

      private final Map<SelectorName, InMemoryTable> tables = new HashMap<SelectorName, InMemoryTable>();

      private final Map<SelectorName, Query> viewDefinitions = new HashMap<SelectorName, Query>();

      protected Builder()
      {
      }

      /**
       * Add a table with the supplied name and column names. Each column will be given a default type. The table will also
       * overwrite any existing table definition with the same name.
       * 
       * @param name the name of the new table
       * @param columnNames the names of the columns.
       * @return this builder, for convenience in method chaining; never null
       * @throws IllegalArgumentException if the table name is null or empty, any column name is null or empty, or if no column
       *         names are given
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
            //TODO default type 
            columns.add(new InMemoryColumn(columnName, "String"));
         }
         InMemoryTable table = new InMemoryTable(new SelectorName(name), columns);
         tables.put(table.getName(), table);
         return this;
      }

      /**
       * Add a table with the supplied name and column names and types. The table will also overwrite any existing table
       * definition with the same name.
       * 
       * @param name the name of the new table
       * @param columnNames the names of the columns
       * @param types the types for the columns
       * @return this builder, for convenience in method chaining; never null
       * @throws IllegalArgumentException if the table name is null or empty, any column name is null or empty, if no column
       *         names are given, or if the number of types does not match the number of columns
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
       * Add a view with the supplied name and SQL string definition. The column names and types will be inferred from the
       * source table(s) and views(s) used in the definition.
       * 
       * @param name the name of the new view
       * @param definition the SQL definition of the view
       * @return this builder, for convenience in method chaining; never null
       * @throws IllegalArgumentException if the view name is null or empty or the definition is null
       * @throws ParsingException if the supplied definition is cannot be parsed as a SQL query
       */
      public Builder addView(String name, String definition)
      {
         throw new NotImplementedException("Method addView(String name, String definition) not implemented");
      }

      /**
       * Add a view with the supplied name and definition. The column names and types will be inferred from the source table(s)
       * used in the definition.
       * 
       * @param name the name of the new view
       * @param definition the definition of the view
       * @return this builder, for convenience in method chaining; never null
       * @throws IllegalArgumentException if the view name is null or empty or the definition is null
       */
      public Builder addView(String name, Query definition)
      {
         Validate.notEmpty(name, " name may not be empty");
         Validate.notNull(definition, " definition may not be null");
         this.viewDefinitions.put(new SelectorName(name), definition);
         return this;
      }

      /**
       * Add a column with the supplied name and type to the named table. Any existing column with that name will be replaced
       * with the new column. If the table does not yet exist, it will be added.
       * 
       * @param tableName the name of the new table
       * @param columnName the names of the column
       * @param type the type for the column
       * @return this builder, for convenience in method chaining; never null
       * @throws IllegalArgumentException if the table name is null or empty, any column name is null or empty, if no column
       *         names are given, or if the number of types does not match the number of columns
       */
      public Builder addColumn(String tableName, String columnName, String type)
      {
         Validate.notEmpty(tableName, " tableName may not be empty");
         Validate.notEmpty(columnName, " columnName may not be empty");
         Validate.notNull(type, " type may not be null");

         return addColumn(tableName, columnName, type, InMemoryColumn.DEFAULT_FULL_TEXT_SEARCHABLE);
      }

      /**
       * Add a column with the supplied name and type to the named table. Any existing column with that name will be replaced
       * with the new column. If the table does not yet exist, it will be added.
       * 
       * @param tableName the name of the new table
       * @param columnName the names of the column
       * @param type the type for the column
       * @param fullTextSearchable true if the column should be full-text searchable, or false if not
       * @return this builder, for convenience in method chaining; never null
       * @throws IllegalArgumentException if the table name is null or empty, the column name is null or empty, or if the
       *         property type is null
       */
      public Builder addColumn(String tableName, String columnName, String type, boolean fullTextSearchable)
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
            columns.add(new InMemoryColumn(columnName, type, fullTextSearchable));
            table = new InMemoryTable(selector, columns);
         }
         else
         {
            table = existing.withColumn(columnName, type);
         }
         tables.put(table.getName(), table);
         return this;
      }

      /**
       * Make sure the column on the named table is searchable.
       * 
       * @param tableName the name of the new table
       * @param columnName the names of the column
       * @return this builder, for convenience in method chaining; never null
       * @throws IllegalArgumentException if the table name is null or empty or if the column name is null or empty
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
            //TODO default type 
            columns.add(new InMemoryColumn(columnName, "String", true));
            table = new InMemoryTable(selector, columns);
         }
         else
         {
            Column column = existing.getColumn(columnName);
            //TODO default type 
            String type = "String";
            if (column != null)
            {
               type = column.getPropertyType();
            }
            table = existing.withColumn(columnName, type, true);
         }
         tables.put(table.getName(), table);
         return this;
      }

      /**
       * Build the {@link Schemata} instance, using the current state of the builder. This method creates a snapshot of the
       * tables (with their columns) as they exist at the moment this method is called.
       * 
       * @return the new Schemata; never null
       * @throws InvalidQueryException 
       * @throws InvalidQueryException if any of the view definitions is invalid and cannot be resolved
       */
      public Schema build() throws InvalidQueryException
      {
         InMemorySchema schemata = new InMemorySchema(new HashMap<SelectorName, Table>(tables));

         // Make a copy of the view definitions, and create the views ...
         Map<SelectorName, Query> definitions = new HashMap<SelectorName, Query>(viewDefinitions);
         boolean added = false;
         do
         {
            added = false;
            Set<SelectorName> viewNames = new HashSet<SelectorName>(definitions.keySet());
            for (SelectorName name : viewNames)
            {
               Query command = definitions.get(name);
               // Create the canonical plan for the definition ...
               //TODO null
               QueryExecutionContext queryContext = new QueryExecutionContext(schemata, null, null);
               SimplePlaner planner = new SimplePlaner();
               QueryExecutionPlan plan = planner.createPlan(queryContext, command);
               if (queryContext.getExecutionExceptions().hasProblems())
               {
                  continue;
               }

               // Get the columns from the top-level PROJECT ...
               QueryExecutionStep project = plan.findStep(Type.PROJECT);
               assert project != null;
               List<org.xcmis.search.model.column.Column> columns =
                  (List<org.xcmis.search.model.column.Column>)project.getPropertyValue("PROJECT_COLUMNS");

               // Go through all the columns and look up the types ...
               List<Column> viewColumns = new ArrayList<Column>(columns.size());
               for (org.xcmis.search.model.column.Column column : columns)
               {
                  // Find the table that the column came from ...
                  Table source = schemata.getTable(column.getSelectorName());
                  if (source == null)
                  {
                     break;
                  }
                  String viewColumnName = column.getColumnName();
                  String sourceColumnName = column.getPropertyName(); // getColumnName() returns alias
                  Column sourceColumn = source.getColumn(sourceColumnName);
                  if (sourceColumn == null)
                  {
                     throw new InvalidQueryException(Visitors.readable(command)
                        + "The view references a non-existant column '" + column.getColumnName() + "' in '"
                        + source.getName() + "'");
                  }
                  viewColumns.add(new InMemoryColumn(viewColumnName, sourceColumn.getPropertyType(), sourceColumn
                     .isFullTextSearchable()));
               }
               if (viewColumns.size() != columns.size())
               {
                  // We weren't able to resolve all of the columns,
                  // so maybe the columns were referencing yet-to-be-built views ...
                  continue;
               }

               // If we could resolve the definition ...
               InMemoryView view = new InMemoryView(name, viewColumns, command);
               definitions.remove(name);
               schemata = schemata.with(view);
               added = true;
            }
         }
         while (added && !definitions.isEmpty());

         if (!definitions.isEmpty())
         {
            Query command = definitions.values().iterator().next();
            throw new InvalidQueryException(Visitors.readable(command) + "The view definition cannot be resolved: "
               + Visitors.readable(command));
         }

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
    * @see org.modeshape.graph.query.validate.Schemata#getTable(org.modeshape.graph.query.model.SelectorName)
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
