/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.xcmis.search.query.content;


/**
 * <code>AbstractJoinTest</code> provides utility methods for join related
 * tests.
 */
public abstract class AbstractJoinTest extends AbstractQOMTest
{

   //   /**
   //    * Name of the left selector.
   //    */
   //   protected static final String LEFT = "left";
   //
   //   /**
   //    * Name of the right selector.
   //    */
   //   protected static final String RI//
   //// /**
   //// * Binds the given <code>value</code> to the variable named
   //// * <code>var</code>.
   //// *
   //// * @param q     the query
   //// * @param var   name of variable in query
   //// * @param value value to bind
   //// * @throws IllegalArgumentException if <code>var</code> is not a valid
   //// *                                  variable in this query.
   //// * @throws RepositoryException      if an error occurs.
   //// */
   ////protected void bindVariableValue(Query q, String var, Object value) throws RepositoryException
   ////{
   ////   q.bindValue(var, value);
   ////}
   ////
   ////protected void checkResultOrder(QueryObjectModel qom, String[] selectorNames, Node[][] nodes)
   ////   throws RepositoryException
   ////{
   ////   checkResultOrder(qom.execute(), selectorNames, nodes);
   ////   checkResultOrder(qm.createQuery(qom.getStatement(), Query.JCR_SQL2).execute(), selectorNames, nodes);
   ////}
   ////
   ////protected void checkResultOrder(QueryResult result, String[] selectorNames, Node[][] nodes)
   ////   throws RepositoryException
   ////{
   ////   // collect rows
   ////   List expectedPaths = new ArrayList();
   ////   log.println("expected:");
   ////   for (int i = 0; i < nodes.length; i++)
   ////   {
   ////      StringBuffer aggregatedPaths = new StringBuffer();
   ////      for (int j = 0; j < nodes[i].length; j++)
   ////      {
   ////         aggregatedPaths.append(getPath(nodes[i][j]));
   ////         aggregatedPaths.append("|");
   ////      }
   ////      expectedPaths.add(aggregatedPaths.toString());
   ////      log.println(aggregatedPaths.toString());
   ////   }
   ////
   ////   List resultPaths = new ArrayList();
   ////   log.println("result:");
   ////   for (RowIterator it = result.getRows(); it.hasNext();)
   ////   {
   ////      Row r = it.nextRow();
   ////      StringBuffer aggregatedPaths = new StringBuffer();
   ////      for (int i = 0; i < selectorNames.length; i++)
   ////      {
   ////         aggregatedPaths.append(getPath(r.getNode(selectorNames[i])));
   ////         aggregatedPaths.append("|");
   ////      }
   ////      resultPaths.add(aggregatedPaths.toString());
   ////      log.println(aggregatedPaths.toString());
   ////   }
   ////
   ////   assertEquals("wrong result order", expectedPaths, resultPaths);
   ////}
   ////
   /////**
   //// * Checks the query object model by executing it directly and matching the
   //// * result against the given <code>nodes</code>. Then the QOM is executed
   //// * again using {@link QueryObjectModel#getStatement()} with {@link
   //// * Query#JCR_SQL2}.
   //// *
   //// * @param qom   the query object model to check.
   //// * @param nodes the result nodes.
   //// * @throws RepositoryException if an error occurs while executing the
   //// *                             query.
   //// */
   ////protected void checkQOM(QueryObjectModel qom, Node[] nodes) throws RepositoryException
   ////{
   ////   checkResult(qom.execute(), nodes);
   ////   checkResult(qm.createQuery(qom.getStatement(), Query.JCR_SQL2).execute(), nodes);
   ////}
   ////
   /////**
   //// * Checks the query object model by executing it directly and matching the
   //// * result against the given <code>nodes</code>. Then the QOM is executed
   //// * again using {@link QueryObjectModel#getStatement()} with
   //// * {@link Query#JCR_SQL2}.
   //// *
   //// * @param qom           the query object model to check.
   //// * @param selectorNames the selector names of the qom.
   //// * @param nodes         the result nodes.
   //// * @throws RepositoryException if an error occurs while executing the
   //// *                             query.
   //// */
   ////protected void checkQOM(QueryObjectModel qom, String[] selectorNames, Node[][] nodes) throws RepositoryException
   ////{
   ////   checkResult(qom.execute(), selectorNames, nodes);
   ////   checkResult(qm.createQuery(qom.getStatement(), Query.JCR_SQL2).execute(), selectorNames, nodes);
   ////}
   ////
   ////protected void checkResult(QueryResult result, String[] selectorNames, Node[][] nodes) throws RepositoryException
   ////{
   ////   // collect rows
   ////   Set expectedPaths = new HashSet();
   ////   log.println("expected:");
   ////   for (int i = 0; i < nodes.length; i++)
   ////   {
   ////      StringBuffer aggregatedPaths = new StringBuffer();
   ////      for (int j = 0; j < nodes[i].length; j++)
   ////      {
   ////         aggregatedPaths.append(getPath(nodes[i][j]));
   ////         aggregatedPaths.append("|");
   ////      }
   ////      expectedPaths.add(aggregatedPaths.toString());
   ////      log.println(aggregatedPaths.toString());
   ////   }
   ////
   ////   Set resultPaths = new HashSet();
   ////   log.println("result:");
   ////   for (RowIterator it = result.getRows(); it.hasNext();)
   ////   {
   ////      Row r = it.nextRow();
   ////      StringBuffer aggregatedPaths = new StringBuffer();
   ////      for (int i = 0; i < selectorNames.length; i++)
   ////      {
   ////         aggregatedPaths.append(getPath(r.getNode(selectorNames[i])));
   ////         aggregatedPaths.append("|");
   ////      }
   ////      resultPaths.add(aggregatedPaths.toString());
   ////      log.println(aggregatedPaths.toString());
   ////   }
   ////
   ////   // check if all expected are in result
   ////   for (Iterator it = expectedPaths.iterator(); it.hasNext();)
   ////   {
   ////      String path = (String)it.next();
   ////      assertTrue(path + " is not part of the result set", resultPaths.contains(path));
   ////   }
   ////   // check result does not contain more than expected
   ////   for (Iterator it = resultPaths.iterator(); it.hasNext();)
   ////   {
   ////      String path = (String)it.next();
   ////      assertTrue(path + " is not expected to be part of the result set", expectedPaths.contains(path));
   ////   }
   ////}
   ////
   /////**
   //// * Returns the path of the <code>node</code> or an empty string if
   //// * <code>node</code> is <code>null</code>.
   //// *
   //// * @param node a node or <code>null</code>.
   //// * @return the path of the node or an empty string if <code>node</code> is
   //// *         <code>null</code>.
   //// * @throws RepositoryException if an error occurs while reading from the
   //// *                             repository.
   //// */
   ////protected static String getPath(Node node) throws RepositoryException
   ////{
   ////   if (node != null)
   ////   {
   ////      return node.getPath();
   ////   }
   ////   else
   ////   {
   ////      return "";
   ////   }
   ////}
   ////
   /////**
   //// * Calls back the <code>callable</code> first with the <code>qom</code> and
   //// * then a JCR_SQL2 query created from {@link QueryObjectModel#getStatement()}.
   //// *
   //// * @param qom      a query object model.
   //// * @param callable the callback.
   //// * @throws RepositoryException if an error occurs.
   //// */
   ////protected void forQOMandSQL2(QueryObjectModel qom, Callable callable) throws RepositoryException
   ////{
   ////   List queries = new ArrayList();
   ////   queries.add(qom);
   ////   queries.add(qm.createQuery(qom.getStatement(), Query.JCR_SQL2));
   ////   for (Iterator it = queries.iterator(); it.hasNext();)
   ////   {
   ////      callable.call((Query)it.next());
   ////   }
   ////}
   //GHT = "right";
   //
   //   /**
   //    * The selector names for the join.
   //    */
   //   protected static final String[] SELECTOR_NAMES = new String[]{LEFT, RIGHT};
   //
   //   //--------------------------< utilities >-----------------------------------
   //
   //   protected void checkQOM(QueryObjectModel qom, Node[][] nodes) throws RepositoryException
   //   {
   //      checkQOM(qom, SELECTOR_NAMES, nodes);
   //   }
   //
   //   protected void checkResult(QueryResult result, Node[][] nodes) throws RepositoryException
   //   {
   //      checkResult(result, SELECTOR_NAMES, nodes);
   //   }
   //
   //   protected QueryObjectModel createQuery(String joinType, JoinCondition condition) throws RepositoryException
   //   {
   //      return createQuery(joinType, condition, null, null);
   //   }
   //
   //   protected QueryObjectModel createQuery(String joinType, JoinCondition condition, Constraint left, Constraint right)
   //      throws RepositoryException
   //   {
   //      // only consider nodes under test root
   //      Constraint constraint;
   //      if (QueryObjectModelConstants.JCR_JOIN_TYPE_LEFT_OUTER.equals(joinType))
   //      {
   //         constraint = qf.descendantNode(LEFT, testRoot);
   //      }
   //      else
   //      {
   //         constraint = qf.descendantNode(RIGHT, testRoot);
   //      }
   //
   //      if (left != null)
   //      {
   //         constraint = qf.and(constraint, left);
   //      }
   //      if (right != null)
   //      {
   //         constraint = qf.and(constraint, right);
   //      }
   //      Join join = qf.join(qf.selector(testNodeType, LEFT), qf.selector(testNodeType, RIGHT), joinType, condition);
   //      return qf.createQuery(join, constraint, null, null);
   //   }
}
