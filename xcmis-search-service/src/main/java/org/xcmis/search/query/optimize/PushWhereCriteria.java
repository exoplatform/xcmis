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
import org.xcmis.search.query.plan.QueryExecutionPlan;

import java.util.LinkedList;

/**
 * An {@link OptimizerCriteria optimizer criteria} that attempts to push the criteria nodes in a simple plan down as far as possible.
 * <p>
 * For example, here is a single-access plan before:
 * 
 * <pre>
 *          ...
 *           |
 *        PROJECT      with the list of columns being SELECTed
 *           |
 *        SELECT1
 *           |         One or more WHERE plan nodes that each have
 *        SELECT2      a single non-join constraint that are then all AND-ed
 *           |         together
 *        SELECTn
 *           |
 *        SELECTOR
 * </pre>
 * 
 * And after:
 * 
 * <pre>
 *          ...
 *           |
 *        ACCESS
 *           |
 *        PROJECT      with the list of columns being SELECTed
 *           |
 *        WHERE1
 *           |         One or more WHERE plan nodes that each have
 *        WHERE2      a single non-join constraint that are then all AND-ed
 *           |         together
 *        WHEREn
 *           |
 *        SELECTOR
 * </pre>
 * 
 * Here is another case, where multiple WHERE nodes above a simple JOIN and where each WHERE node applies to one or more of the
 * SELECTOR nodes (via the named selectors). Each WHERE node that applies to a single selector will get pushed toward that source,
 * but will have the same order relative to other WHERE nodes also pushed toward that SELECTOR. However, this rules does not push
 * WHERE nodes that apply to multiple selectors.
 * </p>
 * <p>
 * Before:
 * 
 * <pre>
 *          ...
 *           |
 *        PROJECT ('s1','s2')      with the list of columns being SELECTed (from 's1' and 's2' selectors)
 *           |
 *        WHERE1 ('s1')
 *           |                     One or more WHERE plan nodes that each have
 *        WHERE2 ('s2')           a single non-join constraint that are then all AND-ed
 *           |                     together, and that each have the selector(s) they apply to
 *        WHERE3 ('s1','s2')
 *           |
 *        WHERE4 ('s1')
 *           |
 *         JOIN ('s1','s2')
 *        /     \
 *       /       \
 *   ACCESS     ACCESS
 *    ('s1')    ('s2')
 *     |           |
 *   PROJECT    PROJECT
 *    ('s1')    ('s2')
 *     |           |
 *   SELECTOR     SELECTOR
 *    ('s1')    ('s2')
 * </pre>
 * 
 * And after:
 * 
 * <pre>
 *          ...
 *           |
 *        PROJECT ('s1','s2')      with the list of columns being SELECTed (from 's1' and 's2' selectors)
 *           |
 *        WHERE3 ('s1','s2')      Any WHERE plan nodes that apply to multiple selectors are left above
 *           |                     the ACCESS nodes.
 *         JOIN ('s1','s2')
 *        /     \
 *       /       \
 *   ACCESS     ACCESS
 *   ('s1')     ('s2')
 *     |           |
 *  PROJECT     PROJECT
 *   ('s1')     ('s2')
 *     |           |
 *  WHERE1     WHERE2
 *   ('s1')     ('s2')
 *     |           |
 *  WHERE4     SELECTOR
 *   ('s1')     ('s2')
 *     |
 *   SELECTOR
 *   ('s1')
 * </pre>
 * 
 * </p>
 * <p>
 * Also, any WHERE that applies to one side of an equi-join will be applied to <i>both</i> sides of the JOIN.
 * </p>
 */
public class PushWhereCriteria implements OptimizerCriteria
{
   public static final PushWhereCriteria INSTANCE = new PushWhereCriteria();

   /**
    * @see org.xcmis.search.query.optimize.OptimizerCriteria#execute(org.xcmis.search.query.QueryExecutionContext, org.xcmis.search.query.plan.QueryExecutionPlan, java.util.LinkedList)
    */
   public QueryExecutionPlan execute(QueryExecutionContext context, QueryExecutionPlan plan,
      LinkedList<OptimizerCriteria> criteriaStack)
   {
      // TODO Auto-generated method stub
      return null;
   }

}
