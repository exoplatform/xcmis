/*
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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.xcmis.search.Visitors;
import org.xcmis.search.model.Query;
import org.xcmis.search.value.CastSystem;

/**
 * 
 */
public class QueryBuilderTest
{

   private QueryBuilder builder;

   private Query query;

   @Before
   public void beforeEach()
   {
      builder = new QueryBuilder(mock(CastSystem.class));
   }

   protected void assertThatSql(Query query, Matcher<String> expected)
   {
      assertThat(Visitors.readable(query), expected);
   }

   @Test
   public void testShouldBuildSelectStarFromOneTable()
   {
      query = builder.selectStar().from("table").query();
      assertThatSql(query, is("SELECT * FROM table"));
   }

   @Test
   public void testShouldBuildSelectStarFromOneTableAs()
   {
      query = builder.selectStar().from("table AS nodes").query();
      assertThatSql(query, is("SELECT * FROM table AS nodes"));
   }

   @Test
   public void testShouldBuildSelectColumnsFromOneTable()
   {
      query = builder.select("col1", "col2").from("table").query();
      assertThatSql(query, is("SELECT table.col1,table.col2 FROM table"));
   }

   @Test
   public void testShouldBuildSelectColumnsFromOneTableAs()
   {
      query = builder.select("col1", "col2").from("table AS nodes").query();
      assertThatSql(query, is("SELECT nodes.col1,nodes.col2 FROM table AS nodes"));
   }

   @Test
   public void testShouldBuildSelectColumnsUsingAliasFromOneTableAs()
   {
      query = builder.select("col1", "nodes.col2").from("table AS  nodes").query();
      assertThatSql(query, is("SELECT nodes.col1,nodes.col2 FROM table AS nodes"));
   }

   @Test
   public void testShouldBuildEquiJoin()
   {
      query = builder.select("t1.c1", "t2.c2").from("table1 AS  t1").join("table2 as t2").on(" t1.c0= t2. c0").query();
      assertThatSql(query, is("SELECT t1.c1,t2.c2 FROM table1 AS t1 INNER JOIN table2 as t2 ON t1.c0 = t2.c0"));
   }

   @Test
   public void testShouldBuildInnerEquiJoin()
   {
      query =
         builder.select("t1.c1", "t2.c2").from("table1 AS  t1").innerJoin("table2 as t2").on(" t1.c0= t2. c0").query();
      assertThatSql(query, is("SELECT t1.c1,t2.c2 FROM table1 AS t1 INNER JOIN table2 as t2 ON t1.c0 = t2.c0"));
   }

   @Test
   public void testShouldBuildLeftOuterEquiJoin()
   {
      query =
         builder.select("t1.c1", "t2.c2").from("table1 AS  t1").leftOuterJoin("table2 as t2").on(" t1.c0= t2. c0")
            .query();
      assertThatSql(query, is("SELECT t1.c1,t2.c2 FROM table1 AS t1 LEFT OUTER JOIN table2 as t2 ON t1.c0 = t2.c0"));
   }

   @Test
   public void testShouldBuildRightOuterEquiJoin()
   {
      query =
         builder.select("t1.c1", "t2.c2").from("table1 AS  t1").rightOuterJoin("table2 as t2").on(" t1.c0= t2. c0")
            .query();
      assertThatSql(query, is("SELECT t1.c1,t2.c2 FROM table1 AS t1 RIGHT OUTER JOIN table2 as t2 ON t1.c0 = t2.c0"));
   }

   @Test
   public void testShouldBuildMultiJoinUsingEquiJoinCriteria()
   {
      query =
         builder.select("t1.c1", "t2.c2").from("table1 AS  t1").join("table2 as t2").on(" t1.c0= t2. c0").join(
            "table3 as t3").on(" t1.c0= t3. c0").query();
      assertThatSql(query, is("SELECT t1.c1,t2.c2 FROM table1 AS t1 " + //
         "INNER JOIN table2 as t2 ON t1.c0 = t2.c0 " + //
         "INNER JOIN table3 as t3 ON t1.c0 = t3.c0"));
   }

   @Test
   public void testShouldAddNoConstraintsIfConstraintBuilderIsNotUsedButIsEnded()
   {
      query = builder.selectStar().from("table AS nodes").where().end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes"));
   }

   @Test
   public void testShouldBuildQueryWithOneHasPropertyConstraint()
   {
      query = builder.selectStar().from("table AS nodes").where().hasProperty("nodes", "col1").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes WHERE nodes.col1 IS NOT NULL"));
   }

   @Test
   public void testShouldBuildQueryWithChildConstraint()
   {
      query = builder.selectStar().from("table AS nodes").where().isChild("nodes", "/parent/path").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes WHERE ISCHILDNODE(nodes,/parent/path)"));
   }

   @Test
   public void testShouldBuildQueryWithDescendantConstraint()
   {
      query = builder.selectStar().from("table AS nodes").where().isBelowPath("nodes", "/parent/path").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes WHERE ISDESCENDANTNODE(nodes,/parent/path)"));
   }

   @Test
   public void testShouldBuildQueryWithSameNodeConstraint()
   {
      query = builder.selectStar().from("table AS nodes").where().isSameNode("nodes", "/other/path").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes WHERE ISSAMENODE(nodes,/other/path)"));
   }

   @Test
   public void testShouldBuildQueryWithFullTextSearchConstraint()
   {
      query = builder.selectStar().from("table AS nodes").where().search("nodes", "expression").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes WHERE CONTAINS(nodes,'expression')"));
   }

   @Test
   public void testShouldBuildQueryWithPropertyFullTextSearchConstraint()
   {
      query =
         builder.selectStar().from("table AS nodes").where().search("nodes", "property", "expression").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes WHERE CONTAINS(nodes.property,'expression')"));
   }

   @Test
   public void testShouldBuildQueryWithTwoHasPropertyConstraint()
   {
      query =
         builder.selectStar().from("table AS nodes").where().hasProperty("nodes", "col1").and().hasProperty("nodes",
            "col2").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes WHERE (nodes.col1 IS NOT NULL AND nodes.col2 IS NOT NULL)"));
   }

   @Test
   public void testShouldBuildQueryWithThreeHasPropertyConstraint()
   {
      query =
         builder.selectStar().from("table AS nodes").where().hasProperty("nodes", "col1").and().hasProperty("nodes",
            "col2").and().hasProperty("nodes", "col3").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE ((nodes.col1 IS NOT NULL " + //
         "AND nodes.col2 IS NOT NULL) " + //
         "AND nodes.col3 IS NOT NULL)"));
   }

   @Test
   public void testShouldBuildQueryWithCorrectPrecedenceWithAndAndOr()
   {
      query =
         builder.selectStar().from("table AS nodes").where().hasProperty("nodes", "col1").or().hasProperty("nodes",
            "col2").and().hasProperty("nodes", "col3").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE (nodes.col1 IS NOT NULL " + //
         "OR (nodes.col2 IS NOT NULL " + //
         "AND nodes.col3 IS NOT NULL))"));
      query =
         builder.selectStar().from("table AS nodes").where().hasProperty("nodes", "col1").and().hasProperty("nodes",
            "col2").or().hasProperty("nodes", "col3").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE ((nodes.col1 IS NOT NULL " + //
         "AND nodes.col2 IS NOT NULL) " + //
         "OR nodes.col3 IS NOT NULL)"));
   }

   @Test
   public void testShouldBuildQueryWithMixureOfLogicalWithExplicitParenthesesWithHasPropertyConstraint()
   {
      query =
         builder.selectStar().from("table AS nodes").where().hasProperty("nodes", "col1").and().openParen()
            .hasProperty("nodes", "col2").and().hasProperty("nodes", "col3").closeParen().end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE (nodes.col1 IS NOT NULL " + //
         "AND (nodes.col2 IS NOT NULL " + //
         "AND nodes.col3 IS NOT NULL))"));
   }

   @Test
   public void testShouldBuildQueryWithMixureOfLogicalWithMultipleExplicitParenthesesWithHasPropertyConstraint()
   {
      query =
         builder.selectStar().from("table AS nodes").where().hasProperty("nodes", "col1").and().openParen().openParen()
            .hasProperty("nodes", "col2").and().hasProperty("nodes", "col3").closeParen().and().search("nodes",
               "expression").closeParen().end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE (nodes.col1 IS NOT NULL " + //
         "AND ((nodes.col2 IS NOT NULL " + //
         "AND nodes.col3 IS NOT NULL) " + //
         "AND CONTAINS(nodes,'expression')))"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLengthEqualTo()
   {
      query =
         builder.selectStar().from("table AS nodes").where().length("nodes", "property").isEqualTo("literal").end()
            .query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LENGTH(nodes.property) = 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLengthEqualToVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().length("nodes", "property").isEqualToVariable("literal")
            .end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LENGTH(nodes.property) = $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLengthNotEqualTo()
   {
      query =
         builder.selectStar().from("table AS nodes").where().length("nodes", "property").isNotEqualTo("literal").end()
            .query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LENGTH(nodes.property) != 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLengthNotEqualToVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().length("nodes", "property")
            .isNotEqualToVariable("literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LENGTH(nodes.property) != $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLengthLessThan()
   {
      query =
         builder.selectStar().from("table AS nodes").where().length("nodes", "property").isLessThan("literal").end()
            .query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LENGTH(nodes.property) < 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLengthLessThanVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().length("nodes", "property").isLessThanVariable("literal")
            .end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LENGTH(nodes.property) < $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLengthLessThanOrEqualTo()
   {
      query =
         builder.selectStar().from("table AS nodes").where().length("nodes", "property").isLessThanOrEqualTo("literal")
            .end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LENGTH(nodes.property) <= 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLengthLessThanOrEqualToVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().length("nodes", "property").isLessThanOrEqualToVariable(
            "literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LENGTH(nodes.property) <= $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLengthGreaterThan()
   {
      query =
         builder.selectStar().from("table AS nodes").where().length("nodes", "property").isGreaterThan("literal").end()
            .query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LENGTH(nodes.property) > 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLengthGreaterThanVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().length("nodes", "property").isGreaterThanVariable(
            "literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LENGTH(nodes.property) > $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLengthGreaterThanOrEqualTo()
   {
      query =
         builder.selectStar().from("table AS nodes").where().length("nodes", "property").isGreaterThanOrEqualTo(
            "literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LENGTH(nodes.property) >= 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLengthGreaterThanOrEqualToVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().length("nodes", "property")
            .isGreaterThanOrEqualToVariable("literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LENGTH(nodes.property) >= $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLengthLike()
   {
      query =
         builder.selectStar().from("table AS nodes").where().length("nodes", "property").isLike("literal").end()
            .query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LENGTH(nodes.property) LIKE 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLengthLikeVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().length("nodes", "property").isLikeVariable("literal")
            .end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LENGTH(nodes.property) LIKE $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeDepthEqualToLiteral()
   {
      query = builder.selectStar().from("table AS nodes").where().depth("nodes").isEqualTo(3).end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE DEPTH(nodes) = 3"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeDepthLessThanOrEqualToLongLiteral()
   {
      query = builder.selectStar().from("table AS nodes").where().depth("nodes").isLessThanOrEqualTo(3).end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE DEPTH(nodes) <= 3"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeDepthLessThanOrEqualToStringLiteral()
   {
      query = builder.selectStar().from("table AS nodes").where().depth("nodes").isLessThanOrEqualTo(3).end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE DEPTH(nodes) <= 3"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeDepthLessThanOrEqualToVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().depth("nodes").isLessThanOrEqualToVariable("value").end()
            .query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE DEPTH(nodes) <= $value"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeNameEqualTo()
   {
      query = builder.selectStar().from("table AS nodes").where().nodeName("nodes").isEqualTo("literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE NAME(nodes) = 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeNameEqualToVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().nodeName("nodes").isEqualToVariable("literal").end()
            .query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE NAME(nodes) = $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeNameNotEqualTo()
   {
      query =
         builder.selectStar().from("table AS nodes").where().nodeName("nodes").isNotEqualTo("literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE NAME(nodes) != 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeNameNotEqualToVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().nodeName("nodes").isNotEqualToVariable("literal").end()
            .query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE NAME(nodes) != $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeNameLessThan()
   {
      query = builder.selectStar().from("table AS nodes").where().nodeName("nodes").isLessThan("literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE NAME(nodes) < 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeNameLessThanVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().nodeName("nodes").isLessThanVariable("literal").end()
            .query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE NAME(nodes) < $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeNameLessThanOrEqualTo()
   {
      query =
         builder.selectStar().from("table AS nodes").where().nodeName("nodes").isLessThanOrEqualTo("literal").end()
            .query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE NAME(nodes) <= 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeNameLessThanOrEqualToVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().nodeName("nodes").isLessThanOrEqualToVariable("literal")
            .end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE NAME(nodes) <= $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeNameGreaterThan()
   {
      query =
         builder.selectStar().from("table AS nodes").where().nodeName("nodes").isGreaterThan("literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE NAME(nodes) > 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeNameGreaterThanVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().nodeName("nodes").isGreaterThanVariable("literal").end()
            .query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE NAME(nodes) > $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeNameGreaterThanOrEqualTo()
   {
      query =
         builder.selectStar().from("table AS nodes").where().nodeName("nodes").isGreaterThanOrEqualTo("literal").end()
            .query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE NAME(nodes) >= 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeNameGreaterThanOrEqualToVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().nodeName("nodes")
            .isGreaterThanOrEqualToVariable("literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE NAME(nodes) >= $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeNameLike()
   {
      query = builder.selectStar().from("table AS nodes").where().nodeName("nodes").isLike("literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE NAME(nodes) LIKE 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeNameLikeVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().nodeName("nodes").isLikeVariable("literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE NAME(nodes) LIKE $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeLocalNameEqualTo()
   {
      query =
         builder.selectStar().from("table AS nodes").where().nodeLocalName("nodes").isEqualTo("literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOCALNAME(nodes) = 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeLocalNameEqualToVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().nodeLocalName("nodes").isEqualToVariable("literal").end()
            .query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOCALNAME(nodes) = $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeLocalNameNotEqualTo()
   {
      query =
         builder.selectStar().from("table AS nodes").where().nodeLocalName("nodes").isNotEqualTo("literal").end()
            .query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOCALNAME(nodes) != 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeLocalNameNotEqualToVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().nodeLocalName("nodes").isNotEqualToVariable("literal")
            .end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOCALNAME(nodes) != $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeLocalNameLessThan()
   {
      query =
         builder.selectStar().from("table AS nodes").where().nodeLocalName("nodes").isLessThan("literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOCALNAME(nodes) < 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeLocalNameLessThanVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().nodeLocalName("nodes").isLessThanVariable("literal").end()
            .query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOCALNAME(nodes) < $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeLocalNameLessThanOrEqualTo()
   {
      query =
         builder.selectStar().from("table AS nodes").where().nodeLocalName("nodes").isLessThanOrEqualTo("literal")
            .end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOCALNAME(nodes) <= 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeLocalNameLessThanOrEqualToVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().nodeLocalName("nodes").isLessThanOrEqualToVariable(
            "literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOCALNAME(nodes) <= $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeLocalNameGreaterThan()
   {
      query =
         builder.selectStar().from("table AS nodes").where().nodeLocalName("nodes").isGreaterThan("literal").end()
            .query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOCALNAME(nodes) > 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeLocalNameGreaterThanVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().nodeLocalName("nodes").isGreaterThanVariable("literal")
            .end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOCALNAME(nodes) > $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeLocalNameGreaterThanOrEqualTo()
   {
      query =
         builder.selectStar().from("table AS nodes").where().nodeLocalName("nodes").isGreaterThanOrEqualTo("literal")
            .end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOCALNAME(nodes) >= 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeLocalNameGreaterThanOrEqualToVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().nodeLocalName("nodes").isGreaterThanOrEqualToVariable(
            "literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOCALNAME(nodes) >= $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeLocalNameLike()
   {
      query =
         builder.selectStar().from("table AS nodes").where().nodeLocalName("nodes").isLike("literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOCALNAME(nodes) LIKE 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingNodeLocalNameLikeVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().nodeLocalName("nodes").isLikeVariable("literal").end()
            .query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOCALNAME(nodes) LIKE $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingUppercaseOfNodeNameEqualTo()
   {
      query =
         builder.selectStar().from("table AS nodes").where().upperCaseOf().nodeName("nodes").isEqualTo("literal").end()
            .query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE UPPER(NAME(nodes)) = 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingUppercaseOfNodeNameEqualToVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().upperCaseOf().nodeName("nodes").isEqualToVariable(
            "literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE UPPER(NAME(nodes)) = $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingUppercaseOfNodeNameNotEqualTo()
   {
      query =
         builder.selectStar().from("table AS nodes").where().upperCaseOf().nodeName("nodes").isNotEqualTo("literal")
            .end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE UPPER(NAME(nodes)) != 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingUppercaseOfNodeNameNotEqualToVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().upperCaseOf().nodeName("nodes").isNotEqualToVariable(
            "literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE UPPER(NAME(nodes)) != $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingUppercaseOfNodeNameLessThan()
   {
      query =
         builder.selectStar().from("table AS nodes").where().upperCaseOf().nodeName("nodes").isLessThan("literal")
            .end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE UPPER(NAME(nodes)) < 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingUppercaseOfNodeNameLessThanVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().upperCaseOf().nodeName("nodes").isLessThanVariable(
            "literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE UPPER(NAME(nodes)) < $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingUppercaseOfNodeNameLessThanOrEqualTo()
   {
      query =
         builder.selectStar().from("table AS nodes").where().upperCaseOf().nodeName("nodes").isLessThanOrEqualTo(
            "literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE UPPER(NAME(nodes)) <= 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingUppercaseOfNodeNameLessThanOrEqualToVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().upperCaseOf().nodeName("nodes")
            .isLessThanOrEqualToVariable("literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE UPPER(NAME(nodes)) <= $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingUppercaseOfNodeNameGreaterThan()
   {
      query =
         builder.selectStar().from("table AS nodes").where().upperCaseOf().nodeName("nodes").isGreaterThan("literal")
            .end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE UPPER(NAME(nodes)) > 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingUppercaseOfNodeNameGreaterThanVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().upperCaseOf().nodeName("nodes").isGreaterThanVariable(
            "literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE UPPER(NAME(nodes)) > $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingUppercaseOfNodeNameGreaterThanOrEqualTo()
   {
      query =
         builder.selectStar().from("table AS nodes").where().upperCaseOf().nodeName("nodes").isGreaterThanOrEqualTo(
            "literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE UPPER(NAME(nodes)) >= 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingUppercaseOfNodeNameGreaterThanOrEqualToVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().upperCaseOf().nodeName("nodes")
            .isGreaterThanOrEqualToVariable("literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE UPPER(NAME(nodes)) >= $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingUppercaseOfNodeNameLike()
   {
      query =
         builder.selectStar().from("table AS nodes").where().upperCaseOf().nodeName("nodes").isLike("literal").end()
            .query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE UPPER(NAME(nodes)) LIKE 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingUppercaseOfNodeNameLikeVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().upperCaseOf().nodeName("nodes").isLikeVariable("literal")
            .end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE UPPER(NAME(nodes)) LIKE $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLowercaseOfNodeNameEqualTo()
   {
      query =
         builder.selectStar().from("table AS nodes").where().lowerCaseOf().nodeName("nodes").isEqualTo("literal").end()
            .query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOWER(NAME(nodes)) = 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLowercaseOfNodeNameEqualToVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().lowerCaseOf().nodeName("nodes").isEqualToVariable(
            "literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOWER(NAME(nodes)) = $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLowercaseOfNodeNameNotEqualTo()
   {
      query =
         builder.selectStar().from("table AS nodes").where().lowerCaseOf().nodeName("nodes").isNotEqualTo("literal")
            .end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOWER(NAME(nodes)) != 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLowercaseOfNodeNameNotEqualToVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().lowerCaseOf().nodeName("nodes").isNotEqualToVariable(
            "literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOWER(NAME(nodes)) != $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLowercaseOfNodeNameLessThan()
   {
      query =
         builder.selectStar().from("table AS nodes").where().lowerCaseOf().nodeName("nodes").isLessThan("literal")
            .end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOWER(NAME(nodes)) < 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLowercaseOfNodeNameLessThanVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().lowerCaseOf().nodeName("nodes").isLessThanVariable(
            "literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOWER(NAME(nodes)) < $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLowercaseOfNodeNameLessThanOrEqualTo()
   {
      query =
         builder.selectStar().from("table AS nodes").where().lowerCaseOf().nodeName("nodes").isLessThanOrEqualTo(
            "literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOWER(NAME(nodes)) <= 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLowercaseOfNodeNameLessThanOrEqualToVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().lowerCaseOf().nodeName("nodes")
            .isLessThanOrEqualToVariable("literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOWER(NAME(nodes)) <= $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLowercaseOfNodeNameGreaterThan()
   {
      query =
         builder.selectStar().from("table AS nodes").where().lowerCaseOf().nodeName("nodes").isGreaterThan("literal")
            .end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOWER(NAME(nodes)) > 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLowercaseOfNodeNameGreaterThanVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().lowerCaseOf().nodeName("nodes").isGreaterThanVariable(
            "literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOWER(NAME(nodes)) > $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLowercaseOfNodeNameGreaterThanOrEqualTo()
   {
      query =
         builder.selectStar().from("table AS nodes").where().lowerCaseOf().nodeName("nodes").isGreaterThanOrEqualTo(
            "literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOWER(NAME(nodes)) >= 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLowercaseOfNodeNameGreaterThanOrEqualToVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().lowerCaseOf().nodeName("nodes")
            .isGreaterThanOrEqualToVariable("literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOWER(NAME(nodes)) >= $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLowercaseOfNodeNameLike()
   {
      query =
         builder.selectStar().from("table AS nodes").where().lowerCaseOf().nodeName("nodes").isLike("literal").end()
            .query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOWER(NAME(nodes)) LIKE 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLowercaseOfNodeNameLikeVariable()
   {
      query =
         builder.selectStar().from("table AS nodes").where().lowerCaseOf().nodeName("nodes").isLikeVariable("literal")
            .end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOWER(NAME(nodes)) LIKE $literal"));
   }

   @Test
   public void testShouldBuildQueryWithCriteriaUsingLowercaseOfUppercaseOfNodeNameEqualTo()
   {
      query =
         builder.selectStar().from("table AS nodes").where().lowerCaseOf().upperCaseOf().nodeName("nodes").isEqualTo(
            "literal").end().query();
      assertThatSql(query, is("SELECT * FROM table AS nodes " + //
         "WHERE LOWER(UPPER(NAME(nodes))) = 'literal'"));
   }

   @Test
   public void testShouldBuildQueryWithOneOrderByClause()
   {
      query =
         builder.selectStar().from("table AS nodes").orderBy().ascending().fullTextSearchScore("nodes").then()
            .descending().length("nodes", "column").end().query();
   }
}
