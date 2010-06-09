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
package org.xcmis.search.content.interceptors;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.Validate;
import org.xcmis.search.content.command.InvocationContext;
import org.xcmis.search.content.command.query.ExecuteSelectorCommand;
import org.xcmis.search.content.command.query.ProcessQueryCommand;
import org.xcmis.search.model.Limit;
import org.xcmis.search.model.Query;
import org.xcmis.search.model.constraint.Constraint;
import org.xcmis.search.model.ordering.Ordering;
import org.xcmis.search.query.QueryExecutionContext;
import org.xcmis.search.query.QueryExecutionExceptions;
import org.xcmis.search.query.Statistics;
import org.xcmis.search.query.plan.Optimizer;
import org.xcmis.search.query.plan.QueryExecutionPlan;
import org.xcmis.search.query.plan.QueryExecutionPlaner;
import org.xcmis.search.query.plan.QueryExecutionPlan.JoinExecutionPlan;
import org.xcmis.search.query.plan.QueryExecutionPlan.LimitExecutionPlan;
import org.xcmis.search.query.plan.QueryExecutionPlan.ProjectExecutionPlan;
import org.xcmis.search.query.plan.QueryExecutionPlan.SelectorExecutionPlan;
import org.xcmis.search.query.plan.QueryExecutionPlan.SortExecutionPlan;
import org.xcmis.search.query.plan.QueryExecutionPlan.WhereExecutionPlan;
import org.xcmis.search.result.ScoredRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A query engine that is able to execute formal queries expressed in the
 * Abstract Query Model.
 */
public class QueryProcessorInterceptor extends CommandInterceptor
{
   private final QueryExecutionPlaner planner;

   private final Optimizer optimizer;

   /**
    * Create a new query engine given the {@link QueryExecutionPlaner planner},
    * {@link Optimizer optimizer}, {@link QueryProcessor processor}.
    * 
    * @param planner
    *           the planner that should be used to generate canonical query
    *           plans for the queries;
    * @param optimizer
    *           the optimizer that should be used to optimize the canonical
    *           query plan
    * @throws IllegalArgumentException
    *            if the processor reference is null
    */
   public QueryProcessorInterceptor(QueryExecutionPlaner planner, Optimizer optimizer)
   {
      super();
      this.planner = planner;
      this.optimizer = optimizer;

   }

   /**
    * @see org.xcmis.search.content.interceptors.AbstractVisitor#visitProcessQueryCommand(org.xcmis.search.content.command.InvocationContext,
    *      org.xcmis.search.content.command.query.ProcessQueryCommand)
    */
   @Override
   public Object visitProcessQueryCommand(InvocationContext ctx, ProcessQueryCommand command) throws Throwable
   {

      QueryExecutionExceptions executionExceptions = new QueryExecutionExceptions();
      try
      {
         return execute(ctx, new QueryExecutionContext(ctx.getSchema(), executionExceptions, command
            .getBindVariablesValues()), command.getQuery());
      }
      finally
      {
         if (executionExceptions.hasProblems())
         {
            throw executionExceptions.getTopException();
         }
      }

   }

   /**
    * Execute the supplied query by planning, optimizing, and then processing it.
    * @param ctx 
    * 
    * @param context the context in which the query should be executed
    * @param query the query that is to be executed
    * @return the query results; never null
    * @throws IllegalArgumentException if the context or query references are null
    */
   public List<ScoredRow> execute(InvocationContext ctx, QueryExecutionContext context, Query query)
   {
      Validate.notNull(context, "The context argument may not be null");
      Validate.notNull(query, "The query argument may not be null");

      // Create the plan ...
      long start = System.nanoTime();
      QueryExecutionPlan executionPlan = planner.createPlan(context, query);
      long duration = System.nanoTime() - start;
      Statistics stats = new Statistics(duration);
      if (!context.getExecutionExceptions().hasProblems())
      {
         // Optimize the plan ...
         start = System.nanoTime();
         QueryExecutionPlan optimizedPlan = optimizer.optimize(context, executionPlan);

         duration = System.nanoTime() - start;
         stats = stats.withOptimizationTime(duration);
         if (!context.getExecutionExceptions().hasProblems())
         {

            // Execute the plan ...
            try
            {
               start = System.nanoTime();
               return execute(ctx, context, query, stats, optimizedPlan);
            }
            finally
            {
               duration = System.nanoTime() - start;
               stats = stats.withOptimizationTime(duration);
            }
         }
      }
      return Collections.emptyList();
   }

   /**
    * Execute the supplied query by plan.
    * @param ctx 
    * @param context
    * @param query
    * @param stats
    * @param queryPlan
    */
   private List<ScoredRow> execute(InvocationContext ctx, QueryExecutionContext context, Query query, Statistics stats,
      QueryExecutionPlan queryPlan)
   {
      QueryExecuteableComponent component = createQueryExecuteableComponent(queryPlan);
      return component.executeComponent(ctx, context);
   };

   /**
    * Create component for execution.
    * @param queryExecutionPlan
    * @return
    */
   private QueryExecuteableComponent createQueryExecuteableComponent(QueryExecutionPlan queryExecutionPlan)
   {

      LimitExecutionPlan limitPlan = null;
      SortExecutionPlan sortPlan = null;
      ProjectExecutionPlan projectPlan = null;
      List<WhereExecutionPlan> constraintsPlan = new ArrayList<WhereExecutionPlan>();
      QueryExecutionPlan nextPlan = queryExecutionPlan;
      do
      {

         switch (nextPlan.getType())
         {
            case LIMIT :
               limitPlan = (LimitExecutionPlan)nextPlan;
               break;
            case SORT :
               sortPlan = (SortExecutionPlan)nextPlan;
               break;
            case PROJECT :
               projectPlan = (ProjectExecutionPlan)nextPlan;
               break;
            case WHERE :
               constraintsPlan.add((WhereExecutionPlan)nextPlan);
               break;
            case SELECTOR :
               return new SelectorExecuteableComponent(this, ((SelectorExecutionPlan)nextPlan), projectPlan,
                  constraintsPlan, sortPlan, limitPlan);
            case JOIN :
               JoinExecutionPlan joinPlan = (JoinExecutionPlan)nextPlan;
               QueryExecuteableComponent left = createQueryExecuteableComponent(joinPlan.getLeftPlan());
               QueryExecuteableComponent right = createQueryExecuteableComponent(joinPlan.getRightPlan());
               return new JoinExecutionComponent(this, joinPlan, left, right, projectPlan, constraintsPlan, sortPlan,
                  limitPlan);
            default :
               throw new NotImplementedException("Execution for plan " + queryExecutionPlan.getType().toString()
                  + " not implemented");
         }

         nextPlan = nextPlan.next();
      }
      while (nextPlan != null);
      return null;
   }

   /**
    * Generic cluss of query execution 
    *
    */
   private abstract class QueryExecuteableComponent
   {
      private final LimitExecutionPlan limitPlan;

      private final SortExecutionPlan sortPlan;

      private final ProjectExecutionPlan projectPlan;

      private final List<WhereExecutionPlan> constraintsPlan;

      private final CommandInterceptor interceptor;

      /**
       * @param projectPlan
       * @param constraintsPlan
       * @param sortPlan
       * @param limitPlan
       */
      public QueryExecuteableComponent(CommandInterceptor interceptor, ProjectExecutionPlan projectPlan,
         List<WhereExecutionPlan> constraintsPlan, SortExecutionPlan sortPlan, LimitExecutionPlan limitPlan)
      {
         super();
         this.interceptor = interceptor;
         this.projectPlan = projectPlan;
         this.constraintsPlan = constraintsPlan;
         this.sortPlan = sortPlan;
         this.limitPlan = limitPlan;
      }

      /**
       * @return the interceptor
       */
      public CommandInterceptor getInterceptor()
      {
         return interceptor;
      }

      /**
       * @return the limitPlan
       */
      public LimitExecutionPlan getLimitPlan()
      {
         return limitPlan;
      }

      /**
       * @return the limit
       */
      public Limit getLimit()
      {
         return limitPlan == null ? Limit.NONE : limitPlan.getLimit();
      }

      /**
       * @return the sortPlan
       */
      public SortExecutionPlan getSortPlan()
      {
         return sortPlan;
      }

      /**
       * @return the sortPlan
       */
      public List<Ordering> getOrder()
      {
         return sortPlan == null ? new ArrayList<Ordering>() : sortPlan.getOrderings();
      }

      /**
       * @return the projectPlan
       */
      public ProjectExecutionPlan getProjectPlan()
      {
         return projectPlan;
      }

      /**
       * @return the constraintsPlan
       */
      public List<WhereExecutionPlan> getConstraintsPlan()
      {
         return constraintsPlan;
      }

      /**
       * @return the constraintsPlan
       */
      public List<Constraint> getConstraints()
      {
         List<Constraint> constraints = new ArrayList<Constraint>(constraintsPlan.size());
         for (WhereExecutionPlan constrain : constraintsPlan)
         {
            constraints.add(constrain.getConstraint());
         }
         return constraints;
      }

      public abstract List<ScoredRow> executeComponent(InvocationContext ctx, QueryExecutionContext context);
   }

   /**
    * Execution component for source plan.
    *
    */
   private class SelectorExecuteableComponent extends QueryExecuteableComponent
   {

      private final SelectorExecutionPlan selectorExecutionPlan;

      /**
       * @param projectPlan
       * @param constraintsPlan
       * @param sortPlan
       * @param limitPlan
       */
      public SelectorExecuteableComponent(CommandInterceptor interceptor, SelectorExecutionPlan selectorExecutionPlan,
         ProjectExecutionPlan projectPlan, List<WhereExecutionPlan> constraintsPlan, SortExecutionPlan sortPlan,
         LimitExecutionPlan limitPlan)
      {
         super(interceptor, projectPlan, constraintsPlan, sortPlan, limitPlan);
         this.selectorExecutionPlan = selectorExecutionPlan;
      }

      /**
       * @return the sourceExecutionPlan
       */
      public SelectorExecutionPlan getSelectorExecutionPlan()
      {
         return selectorExecutionPlan;
      }

      /**
       * 
       * @see org.xcmis.search.content.interceptors.QueryProcessorInterceptor.QueryExecuteableComponent#executeComponent(org.xcmis.search.query.QueryExecutionContext)
       */
      @Override
      public List<ScoredRow> executeComponent(InvocationContext ctx, QueryExecutionContext context)
      {
         try
         {
            ExecuteSelectorCommand command =
               new ExecuteSelectorCommand(selectorExecutionPlan.getName(), selectorExecutionPlan.getAlias(),
                  getConstraints(), getLimit(), getOrder(), context.getVariables());
            return (List<ScoredRow>)getInterceptor().invokeNextInterceptor(ctx, command);
         }
         catch (Throwable e)
         {
            context.getExecutionExceptions().addException(e);
         }
         return null;
      }
   }

   private class JoinExecutionComponent extends QueryExecuteableComponent
   {

      private final JoinExecutionPlan joinPlan;

      /**
       * @param projectPlan
       * @param constraintsPlan
       * @param sortPlan
       * @param limitPlan
       */
      public JoinExecutionComponent(CommandInterceptor interceptor, JoinExecutionPlan joinPlan,
         QueryExecuteableComponent left, QueryExecuteableComponent right, ProjectExecutionPlan projectPlan,
         List<WhereExecutionPlan> constraintsPlan, SortExecutionPlan sortPlan, LimitExecutionPlan limitPlan)
      {
         super(interceptor, projectPlan, constraintsPlan, sortPlan, limitPlan);
         this.joinPlan = joinPlan;
      }

      /**
       * 
       * @see org.xcmis.search.content.interceptors.QueryProcessorInterceptor.QueryExecuteableComponent#executeComponent(org.xcmis.search.query.QueryExecutionContext)
       */
      @Override
      public List<ScoredRow> executeComponent(InvocationContext ctx, QueryExecutionContext context)
      {
         throw new NotImplementedException("Method not implemented");
      }

      /**
       * @return the joinPlan
       */
      public JoinExecutionPlan getJoinPlan()
      {
         return joinPlan;
      }

   }
}
