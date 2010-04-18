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

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.xcmis.search.InvalidQueryException;
import org.xcmis.search.model.Query;
import org.xcmis.search.query.QueryExecutionException;
import org.xcmis.search.result.ScoredRow;

import java.util.List;

/**
 * <code>FullTextSearchScoreTest</code> contains fulltext search score tests.
 */
public class FullTextSearchScoreTest extends AbstractQOMTest
{

   private static final String TEXT = "the quick brown fox jumps over the lazy dog.";

   public void setUp() throws Exception
   {
      super.setUp();
      Node n1 = testRootNode.addNode(nodeName1, testNodeType);
      n1.setProperty(propertyName1, TEXT);
      Node n2 = testRootNode.addNode(nodeName2, testNodeType);
      n2.setProperty(propertyName1, TEXT);
      n2.setProperty(propertyName2, TEXT);
      save(testRootNode);
   }

   @Test
   public void testOrdering() throws QueryExecutionException, InvalidQueryException
   {

      Query query =
         qf.from(testNodeType + " AS s").where().search("s", "'fox'").and().isBelowPath("s", testRootNode.getPath())
            .end().orderBy().ascending().fullTextSearchScore("s").end().query();

      List<ScoredRow> result = searchService.execute(query);
      double previousScore = Double.NaN;

      for (ScoredRow scoredRow : result)
      {
         double score = scoredRow.getScore();
         if (!Double.isNaN(previousScore))
         {
            assertTrue("wrong order", previousScore <= score);
         }
         previousScore = score;
      }

   }
   //TODO implement fullTextSearchScore
   //@Test
   public void testConstraint() throws QueryExecutionException, InvalidQueryException
   {
      Query query =
         qf.from(testNodeType + " AS s").where().search("s", "'fox'").and().fullTextSearchScore("s").isGreaterThan()
            .literal(Double.MIN_VALUE)
            .end().orderBy().descending().fullTextSearchScore("s").end().query();

      List<ScoredRow> result = searchService.execute(query);
      double previousScore = Double.NaN;

      for (ScoredRow scoredRow : result)
      {
         double score = scoredRow.getScore();
         if (!Double.isNaN(previousScore))
         {
            assertTrue("wrong full text search score", Double.MIN_VALUE < score);
         }
         previousScore = score;
      }

   }

}
