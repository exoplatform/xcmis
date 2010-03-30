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
package org.xcmis.search.query.optimize;

import org.xcmis.search.query.QueryExecutionContext;
import org.xcmis.search.query.QueryExecutionExceptions;
import org.xcmis.search.query.plan.Optimizer;
import org.xcmis.search.query.plan.QueryExecutionPlan;

import java.util.LinkedList;

/**
* Optimizer implementation that optimizes a query using a stack of rules. Subclasses can override the
* {@link #populateCriteriaStack(LinkedList, QueryExecutionPlan)} method to define the stack of criteria they'd like to use, including the use of
* custom criteria's.
*/
public class CriteriaBasedOptimizer implements Optimizer
{

   /**
    * @see org.xcmis.search.query.plan.Optimizer#optimize(org.xcmis.search.query.QueryExecutionContext, org.xcmis.search.query.plan.QueryExecutionPlan)
    */
   public QueryExecutionPlan optimize(QueryExecutionContext context, QueryExecutionPlan plan)
   {

      LinkedList<OptimizerCriteria> rules = new LinkedList<OptimizerCriteria>();
      populateCriteriaStack(rules, plan);

      QueryExecutionExceptions problems = context.getExecutionExceptions();
      while (rules.peek() != null && !problems.hasProblems())
      {
         OptimizerCriteria nextRule = rules.poll();
         plan = nextRule.execute(context, plan, rules);
      }

      return plan;
   }

   /**
    * Method that is used to create the initial criteria stack. This method can be overridden by subclasses
    * 
    * @param ruleStack the stack where the rules should be placed; never null
    * @param hints the plan hints
    */
   protected void populateCriteriaStack(LinkedList<OptimizerCriteria> criteriaStack, QueryExecutionPlan plan)
   {

   }

}
