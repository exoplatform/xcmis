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

import static org.junit.Assert.fail;

import org.junit.Test;
import org.xcmis.search.InvalidQueryException;
import org.xcmis.search.content.IndexModificationException;
import org.xcmis.search.model.Query;
import org.xcmis.search.query.QueryExecutionException;

/**
 * <code>ChildNodeTest</code> contains test cases that cover the QOM ChildNode
 * condition.
 */
public class ChildNodeTest extends AbstractQOMTest
{

   @Test
   public void testChildNode() throws IndexModificationException, QueryExecutionException, InvalidQueryException
   {
      Node n = testRootNode.addNode(nodeName1, testNodeType);
      save(n);
      Query query = qf.from(testNodeType + " AS s").where().isChild("s", testRootNode.getPath()).end().query();
      checkQOM(query, "s", new Node[]{n});
   }

   @Test
   public void testChildNodes() throws IndexModificationException, QueryExecutionException, InvalidQueryException
   {
      Node n1 = testRootNode.addNode(nodeName1, testNodeType);
      Node n2 = testRootNode.addNode(nodeName2, testNodeType);
      Node n3 = testRootNode.addNode(nodeName3, testNodeType);
      save(testRootNode);
      Query query = qf.from(testNodeType + " AS s").where().isChild("s", testRootNode.getPath()).end().query();

      checkQOM(query, "s", new Node[]{n1, n2, n3});
   }

   @Test
   public void testPathDoesNotExist() throws QueryExecutionException, InvalidQueryException
   {
      Query query =
         qf.from(testNodeType + " AS s").where().isChild("s", testRootNode.getPath() + "/" + nodeName1).end().query();

      checkQOM(query, "s", new Node[]{});
   }

   @Test
   public void testChildNodesDoNotMatchSelector() throws IndexModificationException, QueryExecutionException,
      InvalidQueryException
   {
      testRootNode.addNode(nodeName1, testNodeType);
      save(testRootNode);

      Query query = qf.from(testNodeType2 + " AS s").where().isChild("s", testRootNode.getPath()).end().query();

      checkQOM(query, "s", new Node[]{});

   }

   //TODO fix in feature
   //@Test(expected = InvalidQueryException.class)
   public void testRelativePath() throws QueryExecutionException, InvalidQueryException
   {
      Query query = qf.from(testNodeType + " AS s").where().isChild("s", "child/relPath/../..").end().query();
      checkQOM(query, "s", new Node[]{});
      fail("ChildNode with relative path argument must throw InvalidQueryException");

   }

   //TODO fix in feature
   //@Test(expected = InvalidQueryException.class)

   public void testSyntacticallyInvalidPath() throws QueryExecutionException, InvalidQueryException
   {
      String invalidPath = "/" + nodeName1 + "[";
      Query query = qf.from(testNodeType + " AS s").where().isChild("s", invalidPath).end().query();
      checkQOM(query, "s", new Node[]{});
      fail("ChildNode with syntactically invalid path argument must throw InvalidQueryException");
   }

   @Test(expected = InvalidQueryException.class)
   public void testNotASelectorName() throws QueryExecutionException, InvalidQueryException
   {
      Query query = qf.from(testNodeType + " AS s").where().isChild("x", testRootNode.getPath()).end().query();
      checkQOM(query, "s", new Node[]{});
      fail("ChildNode with an unknown selector name must throw InvalidQueryException");

   }
}
