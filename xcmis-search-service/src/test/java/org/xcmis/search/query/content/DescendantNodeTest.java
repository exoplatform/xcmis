/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.xcmis.search.query.content;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.xcmis.search.InvalidQueryException;
import org.xcmis.search.SearchServiceException;
import org.xcmis.search.model.Query;
import org.xcmis.search.query.QueryExecutionException;

/**
 * <code>DescendantNodeTest</code> contains test cases related to QOM
 * DescendantNode constraints.
 */
public class DescendantNodeTest extends AbstractQOMTest
{
   @Test
   public void testDescendantNode() throws SearchServiceException, InvalidQueryException
   {
      Node n = testRootNode.addNode(nodeName1, testNodeType);
      save(testRootNode);

      Query query = qf.from(testNodeType + " AS s").where().isBelowPath("s", testRootNode.getPath()).end().query();
      checkQOM(query, "s", new Node[]{n});
   }

   @Test
   public void testDescendantNodes() throws SearchServiceException, InvalidQueryException
   {
      Node n1 = testRootNode.addNode(nodeName1, testNodeType);
      Node n2 = testRootNode.addNode(nodeName2, testNodeType);
      Node n21 = n2.addNode(nodeName1, testNodeType);
      save(testRootNode);

      Query query = qf.from(testNodeType + " AS s").where().isBelowPath("s", testRootNode.getPath()).end().query();

      checkQOM(query, "s", new Node[]{n1, n2, n21});
   }

   @Test
   public void testPathDoesNotExist() throws SearchServiceException, InvalidQueryException
   {
      Query query =
         qf.from(testNodeType + " AS s").where().isBelowPath("s", testRootNode.getPath() + "/" + nodeName1).end()
            .query();

      checkQOM(query, "s", new Node[]{});
   }

   @Test
   public void testDescendantNodesDoNotMatchSelector() throws SearchServiceException, InvalidQueryException
   {
      testRootNode.addNode(nodeName1, testNodeType);
      save(testRootNode);

      Query query =
         qf.from(testNodeType2 + " AS s").where().isBelowPath("s", testRootNode.getPath() + "/" + nodeName1).end()
            .query();

      checkQOM(query, "s", new Node[]{});
   }

   // TODO fix validation of path
   // @Test(expected = InvalidQueryException.class)
   public void testRelativePath() throws QueryExecutionException, InvalidQueryException
   {

      Query query = qf.from(testNodeType + " AS s").where().isBelowPath("s", "child/relPath/../..").end().query();
      checkQOM(query, "s", new Node[]{});
      fail("DescendantNode with relative path argument must throw InvalidQueryException");
   }

   // TODO fix validation of path
   // @Test(expected = InvalidQueryException.class)
   public void testSyntacticallyInvalidPath() throws QueryExecutionException, InvalidQueryException
   {

      String invalidPath = "/" + nodeName1 + "[";
      Query query = qf.from(testNodeType + " AS s").where().isBelowPath("s", invalidPath).end().query();
      checkQOM(query, "s", new Node[]{});
      fail("DescendantNode with syntactically invalid path argument must throw InvalidQueryException");
   }

   @Test(expected = InvalidQueryException.class)
   public void testNotASelectorName() throws QueryExecutionException, InvalidQueryException
   {
      Query query = qf.from(testNodeType + " AS s").where().isBelowPath("x", testRootNode.getPath()).end().query();
      checkQOM(query, "s", new Node[]{});
      fail("DescendantNode with an unknown selector name must throw InvalidQueryException");

   }

}
