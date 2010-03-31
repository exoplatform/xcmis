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

import org.junit.Test;
import org.xcmis.search.InvalidQueryException;
import org.xcmis.search.content.IndexModificationException;
import org.xcmis.search.model.Query;
import org.xcmis.search.result.ScoredRow;

import java.util.List;

/**
 * <code>AndConstraintTest</code> contains tests that check AND constraints.
 */
public class AndConstraintTest extends AbstractQueryTest
{
   @Test
   public void testAnd() throws InvalidQueryException, IndexModificationException
   {
      Node n1 = testRootNode.addNode(nodeName1, testNodeType);
      n1.setProperty(propertyName1, "foo");
      n1.setProperty(propertyName2, "bar");
      Node n2 = testRootNode.addNode(nodeName2, testNodeType);
      n2.setProperty(propertyName2, "bar");
      save(n1);

      Query query =
         qf.from(testNodeType + " AS s").where().isChild("s", testRootNode.getPath()).and().hasProperty("s",
            propertyName1).and().hasProperty("s", propertyName2).end().query();

      List<ScoredRow> result = searchService.execute(query);
      checkResult(result, "s", new Node[]{n1});

      String stmt =
         "SELECT * FROM [" + testNodeType + "] AS s WHERE " + "ISDESCENDANTNODE(s, [" + testRootNode.getPath() + "]) "
            + "AND s.[" + propertyName1 + "] IS NOT NULL " + "AND s.[" + propertyName2 + "] IS NOT NULL";
      //    result = qm.createQuery(stmt, Query.JCR_SQL2).execute();
      //  checkResult(result, new Node[]{n1});
   }
}
