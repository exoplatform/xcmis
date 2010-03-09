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

import org.xcmis.search.model.Query;

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
 *      SELECT1
 *         |         One or more SELECT plan nodes that each have
 *      SELECT2      a single non-join constraint 
 *         |         
 *      SELECTn
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
    * @see org.xcmis.search.query.QueryExecutionPlaner#createPlan(org.xcmis.search.query.QueryExecutionContext, org.xcmis.search.model.Query)
    */
   public QueryExecutionPlan createPlan(QueryExecutionContext context, Query query)
   {
      // TODO Auto-generated method stub
      return null;
   }

}
