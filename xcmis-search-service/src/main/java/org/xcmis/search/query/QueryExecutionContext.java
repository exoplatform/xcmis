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
package org.xcmis.search.query;

import org.xcmis.search.content.Schema;
import org.xcmis.search.model.operand.BindVariableName;

import java.util.Map;

/**
 * An immutable context in which queries are to be executed. 
 * Each query context defines the information that is available during
 * query execution.
 */
public class QueryExecutionContext
{
   private final Map<String, Object> variables;

   private final Schema schema;

   private final QueryExecutionExceptions executionExceptions;

   /**
    * @param variables the mapping of variables and values, or null if there are no such variables
    */
   public QueryExecutionContext(Schema schema, QueryExecutionExceptions executionExceptions,
      Map<String, Object> variables)
   {
      this.schema = schema;
      this.executionExceptions = executionExceptions;
      this.variables = variables;
   }

   /**
     * Get the definition of the tables available within this query context.
     * 
     * @return the schemata; never null

    * @return the schema
    */
   public Schema getSchema()
   {
      return schema;
   }

   /**
      * Get the exceptions container used by this query context. Any problems that have been encountered will be accumlated in this
     * container.
     * 
     * @return the executionExceptions container; never null

    */
   public QueryExecutionExceptions getExecutionExceptions()
   {
      return executionExceptions;
   }

   /**
    * Get the variables that are to be substituted into the {@link BindVariableName} used in the query.
    * 
    * @return immutable map of variable values keyed by their name; never null but possibly empty
    */
   public Map<String, Object> getVariables()
   {
      return variables;
   }

}
