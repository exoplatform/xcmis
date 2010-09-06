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
package org.xcmis.search.query.optimizer;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.xcmis.search.content.Schema;
import org.xcmis.search.query.QueryExecutionContext;
import org.xcmis.search.query.QueryExecutionExceptions;
import org.xcmis.search.query.optimize.OptimizerCriteria;
import org.xcmis.search.query.optimize.PushWhereCriteria;
import org.xcmis.search.query.plan.QueryExecutionPlan;

import java.util.LinkedList;

/**
 * Test for  {@link PushWhereCriteria}
 */
public class PushWhereCriteriaTest
{
   private PushWhereCriteria rule;

   private QueryExecutionContext context;

   @Before
   public void beforeEach()
   {
      context = new QueryExecutionContext(mock(Schema.class), new QueryExecutionExceptions(), null);
      rule = PushWhereCriteria.INSTANCE;
   }

   /**
    * Before:
    * 
    * <pre>
    *          ...
    *           |
    *        PROJECT      with the list of columns being SELECTed
    *           |
    *        SELECT1
    *           |         One or more SELECT plan nodes that each have
    *        SELECT2      a single non-join constraint that are then all AND-ed
    *           |         together
    *        SELECTn
    *           |
    *        ACCESS
    *           |
    *        SOURCE
    * </pre>
    * 
    * And after:
    * 
    * <pre>
    *          ...
    *           |
    *        PROJECT      with the list of columns being SELECTed
    *           |
    *        ACCESS
    *           |
    *        SELECT1
    *           |         One or more SELECT plan nodes that each have
    *        SELECT2      a single non-join constraint that are then all AND-ed
    *           |         together
    *        SELECTn
    *           |
    *        SOURCE
    * </pre>
    */
   @Test
   public void shouldPushDownAllSelectNodesThatApplyToSelectorBelowAccessNodeButAboveSourceNodeUsingSameSelector()
   {
      // Each of the PROJECT, SELECT, and SELECT nodes must have the names of the selectors that they apply to ...
      QueryExecutionPlan project = null;// new QueryExecutionPlan(Type.PROJECT, selector("Selector1"));
      QueryExecutionPlan select1 = null;// //new QueryExecutionPlan(Type.WHERE, project, selector("Selector1"));
      QueryExecutionPlan select2 = null;//new QueryExecutionPlan(Type.WHERE, select1, selector("Selector1"));
      QueryExecutionPlan select3 = null;//new QueryExecutionPlan(Type.WHERE, select2, selector("Selector1"));
      QueryExecutionPlan select4 = null;//new QueryExecutionPlan(Type.WHERE, select3, selector("Selector1"));
      QueryExecutionPlan access = null;//new QueryExecutionPlan(Type.ACCESS, select4, selector("Selector1"));
      QueryExecutionPlan source = null;//new QueryExecutionPlan(Type.SELECTOR, access, selector("Selector1"));

      // Execute the rule ...
      QueryExecutionPlan result = rule.execute(context, project, new LinkedList<OptimizerCriteria>());
      assertThat(result, is(sameInstance(project)));
      assertChildren(project, access);
      assertChildren(access, select1);
      assertChildren(select1, select2);
      assertChildren(select2, select3);
      assertChildren(select3, select4);
      assertChildren(select4, source);
      assertChildren(source);
   }

   @Test
   public void shouldNotPushDownSelectNodesThatUseDifferentSelectorNamesThanSourceNode()
   {
      // Each of the PROJECT, SELECT, and SELECT nodes must have the names of the selectors that they apply to ...
      QueryExecutionPlan project = null;// new QueryExecutionPlan(Type.PROJECT, selector("Selector1"));
      QueryExecutionPlan select1 = null;//new QueryExecutionPlan(Type.WHERE, project, selector("Selector2"));
      QueryExecutionPlan select2 = null;//new QueryExecutionPlan(Type.WHERE, select1, selector("Selector1"));
      QueryExecutionPlan select3 = null;//new QueryExecutionPlan(Type.WHERE, select2, selector("Selector2"));
      QueryExecutionPlan select4 = null;// new QueryExecutionPlan(Type.WHERE, select3, selector("Selector1"));
      QueryExecutionPlan access = null;// new QueryExecutionPlan(Type.ACCESS, select4, selector("Selector1"));
      QueryExecutionPlan source = null;//new QueryExecutionPlan(Type.SELECTOR, access, selector("Selector1"));

      // Execute the rule ...
      QueryExecutionPlan result = rule.execute(context, project, new LinkedList<OptimizerCriteria>());
      assertThat(result, is(sameInstance(project)));
      assertChildren(project, select1);
      assertChildren(select1, select3);
      assertChildren(select3, access);
      assertChildren(access, select2);
      assertChildren(select2, select4);
      assertChildren(select4, source);
      assertChildren(source);
   }

   /**
    * Before:
    * 
    * <pre>
    *          ...
    *           |
    *        PROJECT ('s1','s2')      with the list of columns being SELECTed (from 's1' and 's2' selectors)
    *           |
    *        SELECT1 ('s1')
    *           |                     One or more SELECT plan nodes that each have
    *        SELECT2 ('s2')           a single non-join constraint that are then all AND-ed
    *           |                     together, and that each have the selector(s) they apply to
    *        SELECT3 ('s1','s2')
    *           |
    *        SELECT4 ('s1')
    *           |
    *         JOIN ('s1','s2')
    *        /     \
    *       /       \
    *   ACCESS     ACCESS
    *    ('s1')    ('s2')
    *     |           |
    *   SOURCE     SOURCE
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
    *        SELECT3 ('s1','s2')      Any SELECT plan nodes that apply to multiple selectors are left above
    *           |                     the ACCESS nodes.
    *         JOIN ('s1','s2')
    *        /     \
    *       /       \
    *   ACCESS     ACCESS
    *   ('s1')     ('s2')
    *     |           |
    *  SELECT1     SELECT2
    *   ('s1')     ('s2')
    *     |           |
    *  SELECT4     SOURCE
    *   ('s1')     ('s2')
    *     |
    *   SOURCE
    *   ('s1')
    * </pre>
    */
   @Test
   public void shouldPushDownAllSelectNodesThatApplyToOneSelectorToBelowAccessNodeForThatSelector()
   {
      // Each of the PROJECT, SELECT, and SELECT nodes must have the names of the selectors that they apply to ...
      QueryExecutionPlan project = null;//new QueryExecutionPlan(Type.PROJECT, selector("Selector1"), selector("Selector2"));
      QueryExecutionPlan select1 = null;// new QueryExecutionPlan(Type.WHERE, project, selector("Selector1"));
      QueryExecutionPlan select2 = null;//new QueryExecutionPlan(Type.WHERE, select1, selector("Selector2"));
      QueryExecutionPlan select3 = null;//         new QueryExecutionPlan(Type.WHERE, select2, selector("Selector1"), selector("Selector2"));
      QueryExecutionPlan select4 = null;//new QueryExecutionPlan(Type.WHERE, select3, selector("Selector1"));
      QueryExecutionPlan join = null;//         new QueryExecutionPlan(Type.JOIN, select4, selector("Selector1"), selector("Selector2"));
      QueryExecutionPlan s1Access = null;// new QueryExecutionPlan(Type.ACCESS, join, selector("Selector1"));
      QueryExecutionPlan s1Source = null;//new QueryExecutionPlan(Type.SELECTOR, s1Access, selector("Selector1"));
      QueryExecutionPlan s2Access = null;// new QueryExecutionPlan(Type.ACCESS, join, selector("Selector2"));
      QueryExecutionPlan s2Source = null;//new QueryExecutionPlan(Type.SELECTOR, s2Access, selector("Selector2"));
      // Set the join type ...
      //join.setProperty("JOIN_TYPE", JoinType.INNER);

      // Execute the rule ...
      QueryExecutionPlan result = rule.execute(context, project, new LinkedList<OptimizerCriteria>());

      // System.out.println(result);

      assertThat(result, is(sameInstance(project)));
      assertChildren(project, select3);
      assertChildren(select3, join);
      assertChildren(join, s1Access, s2Access);
      assertChildren(s1Access, select1);
      assertChildren(select1, select4);
      assertChildren(select4, s1Source);
      assertChildren(s2Access, select2);
      assertChildren(select2, s2Source);
      assertChildren(s2Source);
      assertChildren(s1Source);
   }

   private void assertChildren(Object obj, Object obj2, Object obj3)
   {

   }

   private void assertChildren(Object obj, Object obj2)
   {

   }

   private void assertChildren(Object obj)
   {

   }
}
