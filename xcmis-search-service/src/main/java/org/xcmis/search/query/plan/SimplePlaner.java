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
package org.xcmis.search.query.plan;

import org.xcmis.search.InvalidQueryException;
import org.xcmis.search.VisitException;
import org.xcmis.search.Visitors;
import org.xcmis.search.content.ColumnDoesNotExistOnTable;
import org.xcmis.search.content.TableDoesntExistException;
import org.xcmis.search.content.Schema.Table;
import org.xcmis.search.model.Limit;
import org.xcmis.search.model.Query;
import org.xcmis.search.model.column.Column;
import org.xcmis.search.model.constraint.And;
import org.xcmis.search.model.constraint.Constraint;
import org.xcmis.search.model.ordering.Ordering;
import org.xcmis.search.model.source.Join;
import org.xcmis.search.model.source.Selector;
import org.xcmis.search.model.source.SelectorName;
import org.xcmis.search.model.source.Source;
import org.xcmis.search.query.QueryExecutionContext;
import org.xcmis.search.query.plan.QueryExecutionPlan.JoinExecutionPlan;
import org.xcmis.search.query.plan.QueryExecutionPlan.LimitExecutionPlan;
import org.xcmis.search.query.plan.QueryExecutionPlan.ProjectExecutionPlan;
import org.xcmis.search.query.plan.QueryExecutionPlan.SelectorExecutionPlan;
import org.xcmis.search.query.plan.QueryExecutionPlan.SortExecutionPlan;
import org.xcmis.search.query.plan.QueryExecutionPlan.SourceExecutionPlan;
import org.xcmis.search.query.plan.QueryExecutionPlan.WhereExecutionPlan;
import org.xcmis.search.query.validate.Validator;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * The planner that produces a simple query plan given a {@link Query query }.
 * <p>
 * A simple plan always has the same structure:
 * 
 * <pre>
 *       LIMIT       if row limit or offset are used
 *         |
 *      SORTING      if 'ORDER BY' is used with more then one Source
 *         |
 *      WHERE1
 *         |         One or more WHERE plan nodes that each have
 *      WHERE2      a single non-join constraint 
 *         |         
 *      WHEREn
 *         |
 *    SOURCE or JOIN     A single SOURCE or JOIN node, depending upon the query
 *              /  \
 *             /    \
 *           SOJ    SOJ    A SOURCE or JOIN node for the left and right side of the JOIN
 * </pre>
 * <p>
 * There leaves of the tree are always SOURCE nodes, so <i>conceptually</i> data always flows through this plan from the bottom
 * SOURCE nodes, is adjusted/filtered as it trickles up through the plan, and is then ready to be used by the caller as it emerges
 * from the top node of the plan.
 * </p>
 * <p>
 * This canonical plan, however, is later optimized and rearranged so that it performs faster.
 * </p>
 */
public class SimplePlaner implements QueryExecutionPlaner
{

   /**
    * @see org.xcmis.search.query.plan.QueryExecutionPlaner#createPlan(org.xcmis.search.query.QueryExecutionContext, org.xcmis.search.model.Query)
    */
   public QueryExecutionPlan createPlan(QueryExecutionContext context, Query query)

   {
      try
      {
         Map<SelectorName, Table> querySelectorsMap = new HashMap<SelectorName, Table>();
         //source
         QueryExecutionPlan plan = createSelectorPlan(context, query.getSource(), querySelectorsMap);
         //constrain
         plan = createConstrainPlan(context, query.getConstraint(), querySelectorsMap, plan);
         //columns
         plan = createProject(context, query.getColumns(), querySelectorsMap, plan);
         //order by
         plan = createSorting(context, query.getOrderings(), plan);
         //limit 
         plan = createLimits(context, query.getLimits(), plan);

         Visitors.visitAll(query, new Validator(context, querySelectorsMap));
         return plan;
      }
      catch (VisitException e)
      {
         context.getExecutionExceptions()
            .addException(new InvalidQueryException(e.getLocalizedMessage(), e.getCause()));
      }

      return null;
   }

   /**
    * @param context
    * @param constraint
    * @param querySelectorsMap
    * @param executionPlan 
    * @return
    */
   protected QueryExecutionPlan createConstrainPlan(final QueryExecutionContext context, final Constraint constraint,
      final Map<SelectorName, Table> querySelectorsMap, QueryExecutionPlan executionPlan)
   {
      // Extract the list of Constraint objects that all must be satisfied ...
      LinkedList<Constraint> andableConstraints = new LinkedList<Constraint>();
      separateAndConstraints(constraint, andableConstraints);
      // For each of these constraints, create a criteria (WHERE) node above the supplied (JOIN or SOURCE) node.
      // Do this in reverse order so that the top-most WHERE node corresponds to the first constraint.
      for (Constraint andedConstrain : andableConstraints)
      {
         // Create the where step ...

         WhereExecutionPlan whereExecutionPlan = new WhereExecutionPlan(executionPlan);
         whereExecutionPlan.setConstraint(andedConstrain);
         // Add selectors to the criteria node ...

         whereExecutionPlan.addSelectors(Visitors.getSelectorsReferencedBy(andedConstrain));

         executionPlan = whereExecutionPlan;
      }
      return executionPlan;
   }

   /**
    * Populate plan for given selector.
    * @param context
    * @param source
    * @param querySelectorsMap
    * @return QueryExecutionPlan
    * @throws VisitException 
    */
   protected QueryExecutionPlan createSelectorPlan(final QueryExecutionContext context, final Source source,
      final Map<SelectorName, Table> querySelectorsMap) throws VisitException
   {
      final Stack<QueryExecutionPlan> stepsStack = new Stack<QueryExecutionPlan>();

      Visitors.visit(source, new Visitors.AbstractModelVisitor()
      {
         /**
          * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.source.Join)
          */
         @Override
         public void visit(Join node) throws VisitException
         {
            JoinExecutionPlan joinPlan = new JoinExecutionPlan();
            joinPlan.setJoinType(node.getType());
            joinPlan.setJoinAlgorithm(JoinAlgorithm.NESTED_LOOP);
            joinPlan.setJoinCondition(node.getJoinCondition());
            //left plan
            node.getLeft().accept(this);
            joinPlan.setLeftPlan((SourceExecutionPlan)stepsStack.pop());
            //right plan
            node.getRight().accept(this);
            joinPlan.setRightPlan((SourceExecutionPlan)stepsStack.pop());

         }

         /**
          * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.source.Selector)
          */
         @Override
         public void visit(Selector selector)
         {
            //QueryExecutionPlan executionPlan = new QueryExecutionPlan(QueryExecutionPlan.Type.SELECTOR);
            SelectorExecutionPlan selectorPlan = new SelectorExecutionPlan();
            if (selector.hasAlias())
            {
               selectorPlan.addSelector(selector.getAlias());
               selectorPlan.setAlias(selector.getAlias());
               selectorPlan.setName(selector.getName());
            }
            else
            {
               selectorPlan.addSelector(selector.getName());
               selectorPlan.setName(selector.getName());
            }
            // Validate the source name and set the available columns ...
            Table table = context.getSchema().getTable(selector.getName());
            if (table != null)
            {

               if (querySelectorsMap.put(selector.getAliasOrName(), table) != null)
               {
                  // There was already a table with this alias or name ...
               }
               selectorPlan.setColumns(table.getColumns());
            }
            else
            {
               context.getExecutionExceptions().addException(
                  new TableDoesntExistException("Table " + selector.getName() + " doesn't exist"));
            }
            stepsStack.push(selectorPlan);
         }
      });

      return stepsStack.pop();
   }

   /**
    * populate SORT node at top of executionPlan. The SORT may be pushed down to a source (or sources) if possible by the optimizer.
    * 
    * @param context the context in which the query is being planned
    * @param orderings list of orderings from the query
    * @param executionPlan the existing plan
    */
   protected QueryExecutionPlan createSorting(final QueryExecutionContext context, List<Ordering> orderings,
      final QueryExecutionPlan executionPlan)
   {
      if (!orderings.isEmpty())
      {

         SortExecutionPlan sortExecutionPlan = new SortExecutionPlan(executionPlan);
         sortExecutionPlan.setOrderings(orderings);
         for (Ordering ordering : orderings)
         {
            sortExecutionPlan.addSelectors(Visitors.getSelectorsReferencedBy(ordering));
         }
         return sortExecutionPlan;
      }
      return executionPlan;
   }

   /**
    * Attach a PROJECT node at the top of the plan tree.
    * 
    * @param context the context in which the query is being planned
    * @param executionPlan the existing plan
    * @param columns the columns being projected; may be null
    * @param selectors the selectors keyed by their alias or name
    * @return the updated plan
    */
   protected QueryExecutionPlan createProject(final QueryExecutionContext context, List<Column> columns,
      Map<SelectorName, Table> selectors, QueryExecutionPlan executionPlan)
   {
      if (columns == null)
      {
         columns = Collections.emptyList();
      }
      //QueryExecutionPlan projectNode = new QueryExecutionPlan(QueryExecutionPlan.Type.PROJECT);
      ProjectExecutionPlan projectPlan = new ProjectExecutionPlan(executionPlan);

      if (columns.isEmpty() || (columns.size() == 1 && columns.get(0).getPropertyName().equals("*")))
      {
         columns = new LinkedList<Column>();
         // SELECT *, so find all of the columns that are available from all the sources ...
         for (Map.Entry<SelectorName, Table> entry : selectors.entrySet())
         {
            SelectorName tableName = entry.getKey();
            Table table = entry.getValue();
            // Add the selector that is being used ...
            projectPlan.addSelector(tableName);
            // Compute the columns from this selector ...
            for (org.xcmis.search.content.Schema.Column column : table.getColumns())
            {
               String columnName = column.getName();
               String propertyName = columnName;
               columns.add(new Column(tableName, propertyName, columnName));
            }
         }
      }
      else
      {
         // Add the selector used by each column ...
         for (Column column : columns)
         {
            if (!column.isFunction())
            {
               SelectorName tableName = column.getSelectorName();
               // Add the selector that is being used ...
               projectPlan.addSelector(tableName);
               // Verify that each column is available in the appropriate source ...
               Table table = selectors.get(tableName);
               if (table == null)
               {
                  context.getExecutionExceptions().addException(
                     new TableDoesntExistException("Table " + tableName + " doesn't exist"));
               }
               else
               {
                  // Make sure that the column is in the table ...
                  String name = column.getPropertyName();
                  if (table.getColumn(name) == null)
                  {
                     context.getExecutionExceptions().addException(
                        new ColumnDoesNotExistOnTable("Column  " + name + " on " + tableName + " doesn't exist"));
                  }
               }
            }
         }
      }
      projectPlan.setColumns(columns);
      return projectPlan;
   }

   /**
    * Attach a LIMIT node at the top of the plan tree.
    * 
    * @param context the context in which the query is being planned
    * @param limit the limit definition; may be null
    * @param executionPlan the existing plan    *
    */
   protected QueryExecutionPlan createLimits(QueryExecutionContext context, Limit limit,
      QueryExecutionPlan executionPlan)
   {
      if (limit != null && !limit.isUnlimited())
      {

         LimitExecutionPlan limitExecutionPlan = new LimitExecutionPlan(executionPlan);
         limitExecutionPlan.setLimit(limit);
         return limitExecutionPlan;
      }
      return executionPlan;
   }

   /**
   * Walk the supplied constraint to extract a list of the constraints that can be AND-ed together. For example, given the
   * constraint tree ((C1 AND C2) AND (C3 OR C4)), this method would result in a list of three separate criteria: [C1,C2,(C3 OR
   * C4)]. The resulting <code>andConstraints</code> list will contain Constraint objects that all must be true.
   * 
   * @param constraint the input constraint
   * @param andableConstraints the collection into which all non-{@link And AND} constraints should be placed
   */
   protected void separateAndConstraints(Constraint constraint, List<Constraint> andableConstraints)
   {
      if (constraint == null)
      {
         return;
      }

      if (constraint instanceof And)
      {
         And and = (And)constraint;
         separateAndConstraints(and.getLeft(), andableConstraints);
         separateAndConstraints(and.getRight(), andableConstraints);
      }
      else
      {
         andableConstraints.add(constraint);
      }
   }
}
