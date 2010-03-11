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
      QueryExecutionPlan executionPlan = new QueryExecutionPlan();
      try
      {
         Map<SelectorName, Table> querySelectorsMap = new HashMap<SelectorName, Table>();
         //source
         populateSelectorPlan(context, query.getSource(), querySelectorsMap, executionPlan);
         //constrain
         populateConstrainPlan(context, query.getConstraint(), querySelectorsMap, executionPlan);
         //columns
         populateProject(context, query.getColumns(), querySelectorsMap, executionPlan);
         //order by
         populateSorting(context, query.getOrderings(), executionPlan);

         Visitors.visitAll(query, new Validator(context, querySelectorsMap));
      }
      catch (VisitException e)
      {
         context.getExecutionExceptions()
            .addException(new InvalidQueryException(e.getLocalizedMessage(), e.getCause()));
      }

      return executionPlan;
   }

   /**
    * @param context
    * @param constraint
    * @param querySelectorsMap
    * @param executionPlan 
    * @return
    */
   protected void populateConstrainPlan(final QueryExecutionContext context, final Constraint constraint,
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
         QueryExecutionStep criteriaSterp = new QueryExecutionStep(QueryExecutionStep.Type.WHERE);
         criteriaSterp.setProperty("WHERE_CRITERIA", andedConstrain);

         // Add selectors to the criteria node ...
         criteriaSterp.setProperty("WHERE_SELECTORS", Visitors.getSelectorsReferencedBy(andedConstrain));
         executionPlan.addFirst(criteriaSterp);
      }
   }

   /**
    * Populate plan for given selector.
    * @param context
    * @param source
    * @param querySelectorsMap
    * @param executionPlan 
    * @return
    * @throws VisitException 
    */
   protected void populateSelectorPlan(final QueryExecutionContext context, final Source source,
      final Map<SelectorName, Table> querySelectorsMap, final QueryExecutionPlan executionPlan) throws VisitException
   {
      final Stack<QueryExecutionStep> stepsStack = new Stack<QueryExecutionStep>();

      Visitors.visit(source, new Visitors.AbstractModelVisitor()
      {
         /**
          * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.source.Join)
          */
         @Override
         public void visit(Join node) throws VisitException
         {
            QueryExecutionStep executionStep = new QueryExecutionStep(QueryExecutionStep.Type.JOIN);
            executionStep.setProperty("JOIN_TYPE", node.getType());
            executionStep.setProperty("JOIN_ALGORITHM", JoinAlgorithm.NESTED_LOOP);
            executionStep.setProperty("JOIN_CONDITION", node.getJoinCondition());
            //left plan
            node.getLeft().accept(this);
            executionStep.setProperty("JOIN_LEFT_PLAN", stepsStack.pop());
            //right plan
            node.getRight().accept(this);
            executionStep.setProperty("JOIN_RIGHT_PLAN", stepsStack.pop());

         }

         /**
          * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.source.Selector)
          */
         @Override
         public void visit(Selector selector)
         {
            QueryExecutionStep executionStep = new QueryExecutionStep(QueryExecutionStep.Type.SOURCE);
            if (selector.hasAlias())
            {
               executionStep.addSelector(selector.getAlias());
               executionStep.setProperty("SOURCE_ALIAS", selector.getAlias());
               executionStep.setProperty("SOURCE_NAME", selector.getName());
            }
            else
            {
               executionStep.addSelector(selector.getName());
               executionStep.setProperty("SOURCE_NAME", selector.getName());
            }
            // Validate the source name and set the available columns ...
            Table table = context.getSchema().getTable(selector.getName());
            if (table != null)
            {

               if (querySelectorsMap.put(selector.getAliasOrName(), table) != null)
               {
                  // There was already a table with this alias or name ...
               }

               executionStep.setProperty("SOURCE_COLUMNS", table.getColumns());
            }
            else
            {
               context.getExecutionExceptions().addException(
                  new TableDoesntExistException("Table " + selector.getName() + " doesnt exist"));
            }
            stepsStack.push(executionStep);
         }
      });

      executionPlan.addFirst(stepsStack.pop());
   }

   /**
    * populate SORT node at top of executionPlan. The SORT may be pushed down to a source (or sources) if possible by the optimizer.
    * 
    * @param context the context in which the query is being planned
    * @param orderings list of orderings from the query
    * @param executionPlan the existing plan
    */
   protected void populateSorting(final QueryExecutionContext context, List<Ordering> orderings,
      final QueryExecutionPlan executionPlan)
   {
      if (!orderings.isEmpty())
      {

         QueryExecutionStep sortNode = new QueryExecutionStep(QueryExecutionStep.Type.SORT);

         sortNode.setProperty("SORT_ORDER_BY", orderings);
         for (Ordering ordering : orderings)
         {
            sortNode.addSelectors(Visitors.getSelectorsReferencedBy(ordering));
         }

         executionPlan.addFirst(sortNode);
      }
   }

   /**
    * Attach a PROJECT node at the top of the plan tree.
    * 
    * @param context the context in which the query is being planned
    * @param plan the existing plan
    * @param columns the columns being projected; may be null
    * @param selectors the selectors keyed by their alias or name
    * @return the updated plan
    */
   protected void populateProject(final QueryExecutionContext context, List<Column> columns,
      Map<SelectorName, Table> selectors, final QueryExecutionPlan executionPlan)
   {
      if (columns == null)
      {
         columns = Collections.emptyList();
      }
      QueryExecutionStep projectNode = new QueryExecutionStep(QueryExecutionStep.Type.PROJECT);

      if (columns.isEmpty())
      {
         columns = new LinkedList<Column>();
         // SELECT *, so find all of the columns that are available from all the sources ...
         for (Map.Entry<SelectorName, Table> entry : selectors.entrySet())
         {
            SelectorName tableName = entry.getKey();
            Table table = entry.getValue();
            // Add the selector that is being used ...
            projectNode.addSelector(tableName);
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
            SelectorName tableName = column.getSelectorName();
            // Add the selector that is being used ...
            projectNode.addSelector(tableName);
            // Verify that each column is available in the appropriate source ...
            Table table = selectors.get(tableName);
            if (table == null)
            {
               context.getExecutionExceptions().addException(
                  new TableDoesntExistException("Table " + tableName + " doesnt exist"));
            }
            else
            {
               // Make sure that the column is in the table ...
               String columnName = column.getPropertyName();
               String name = columnName;
               if (table.getColumn(name) == null)
               {
                  context.getExecutionExceptions().addException(
                     new ColumnDoesNotExistOnTable("Column  " + name + " on " + tableName + " doesnt exist"));
               }
            }
         }
      }
      projectNode.setProperty("PROJECT_COLUMNS", columns);
      executionPlan.addFirst(projectNode);
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
