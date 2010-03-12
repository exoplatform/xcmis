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
import org.xcmis.search.query.plan.Optimizer;
import org.xcmis.search.query.plan.QueryExecutionPlan;

import java.util.LinkedList;

/**
 * Interface that defines an {@link Optimizer} criteria.
 */
public interface OptimizerCriteria
{
   /**
    * Optimize the supplied plan using the supplied context, hints, and yet-to-be-run rules.
    * 
    * @param context the context in which the query is being optimized; never null
    * @param plan the plan to be optimized; never null
    * @param criteriaStack the stack of rules that will be run after this rule; never null
    * @return the optimized plan; never null
    */
   QueryExecutionPlan execute(QueryExecutionContext context, QueryExecutionPlan plan,
      LinkedList<OptimizerCriteria> criteriaStack);

}
