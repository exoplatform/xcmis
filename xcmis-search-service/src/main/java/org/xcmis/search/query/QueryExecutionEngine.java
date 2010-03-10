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
package org.xcmis.search.query;

import org.apache.commons.lang.Validate;
import org.xcmis.search.model.Query;
import org.xcmis.search.query.plan.Optimizer;
import org.xcmis.search.query.plan.QueryExecutionPlan;
import org.xcmis.search.query.plan.QueryExecutionPlaner;
import org.xcmis.search.query.request.QueryProcessor;

/**
 * A query engine that is able to execute formal queries expressed in the
 * Abstract Query Model on given RequestProcessor.
 */
public class QueryExecutionEngine
{

   private final QueryExecutionPlaner planner;

   private final Optimizer optimizer;

   private final QueryProcessor processor;

   /**
    * Create a new query engine given the {@link QueryExecutionPlaner planner}, {@link Optimizer optimizer}, {@link QueryProcessor processor}.
    * 
    * @param planner the planner that should be used to generate canonical query plans for the queries;
    * @param optimizer the optimizer that should be used to optimize the canonical query plan
    * @param processor the processor implementation that should be used to process the planned query and return the results
    * @throws IllegalArgumentException if the processor reference is null
    */
   public QueryExecutionEngine(QueryExecutionPlaner planner, Optimizer optimizer, QueryProcessor processor)
   {
      super();
      this.planner = planner;
      this.optimizer = optimizer;
      this.processor = processor;

   }

   /**
   * Execute the supplied query by planning, optimizing, and then processing it.
   * 
   * @param context the context in which the query should be executed
   * @param query the query that is to be executed
   * @return the query results; never null
   * @throws IllegalArgumentException if the context or query references are null
   */
   public QueryExecutionResult execute(QueryExecutionContext context, Query query)
   {
      Validate.notNull(context, "The context argument may not be null");
      Validate.notNull(query, "The query argument may not be null");

      // Create the plan ...
      QueryExecutionPlan executionPlan = planner.createPlan(context, query);
      // Optimize the plan ...
      QueryExecutionPlan optimizedPlan = optimizer.optimize(context, executionPlan);
      // Execute the plan ...
      return processor.execute(context, query, optimizedPlan);
   };
}
