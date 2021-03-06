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
package org.xcmis.search.query.plan;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.xcmis.search.InvalidQueryException;
import org.xcmis.search.content.InMemorySchema;
import org.xcmis.search.content.Schema;
import org.xcmis.search.model.Query;
import org.xcmis.search.model.column.Column;
import org.xcmis.search.model.source.SelectorName;
import org.xcmis.search.query.QueryBuilder;
import org.xcmis.search.query.QueryExecutionContext;
import org.xcmis.search.query.QueryExecutionExceptions;
import org.xcmis.search.query.plan.QueryExecutionPlan.LimitExecutionPlan;
import org.xcmis.search.query.plan.QueryExecutionPlan.ProjectExecutionPlan;
import org.xcmis.search.query.plan.QueryExecutionPlan.SelectorExecutionPlan;
import org.xcmis.search.query.plan.QueryExecutionPlan.Type;
import org.xcmis.search.value.CastSystem;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Test simple planer.  
 *
 */
public class SimpleQueryPlanerTest
{
   private SimplePlaner planner;

   //   private TypeSystem typeSystem;

   private QueryBuilder builder;

   private Query query;

   private QueryExecutionPlan plan;

   private QueryExecutionExceptions problems;

   private Schema schema;

   private InMemorySchema.Builder schemataBuilder;

   private QueryExecutionContext queryContext;

   private boolean print;

   @Before
   public void beforeEach()
   {
      planner = new SimplePlaner();
      //      typeSystem = new ExecutionContext().getValueFactories().getTypeSystem();
      //      hints = new PlanHints();

      builder = new QueryBuilder(mock(CastSystem.class));
      problems = new QueryExecutionExceptions();
      schemataBuilder = InMemorySchema.createBuilder();
      print = false;
   }

   protected SelectorName selector(String name)
   {
      return new SelectorName(name);
   }

   protected Set<SelectorName> selectors(String... names)
   {
      Set<SelectorName> selectors = new HashSet<SelectorName>();
      for (String name : names)
      {
         selectors.add(selector(name));
      }
      return selectors;
   }

   @SuppressWarnings("unchecked")
   protected void assertProjectNode(QueryExecutionPlan executionPlan, String... columnNames)
   {

      assertThat(plan.getType(), is(Type.PROJECT));
      ProjectExecutionPlan node = (ProjectExecutionPlan)executionPlan;
      if (columnNames.length != 0)
      {
         assertThat(node.getColumns(), notNullValue());
      }
      List<Column> columns = node.getColumns();
      assertThat(columns.size(), is(columnNames.length));
      for (int i = 0; i != columns.size(); ++i)
      {
         Column column = columns.get(i);
         assertThat(column.getColumnName(), is(columnNames[i]));
      }
   }

   @SuppressWarnings("unchecked")
   protected void assertSourceNode(QueryExecutionPlan executionPlan, String sourceName, String sourceAlias,
      String... availableColumns)
   {
      assertThat(executionPlan.getType(), is(Type.SELECTOR));
      SelectorExecutionPlan node = (SelectorExecutionPlan)executionPlan;

      assertThat(node.getName(), is(selector(sourceName)));

      if (sourceAlias != null)
      {
         assertThat(node.getAlias(), is(selector(sourceAlias)));
      }
      else
      {

         assertThat(node.getAlias(), nullValue());
      }
      Collection<Schema.Column> columns = node.getColumns();
      assertThat(columns.size(), is(availableColumns.length));
      int i = 0;
      for (Schema.Column column : columns)
      {
         String expectedName = availableColumns[i++];
         assertThat(column.getName(), is(expectedName));
      }
   }

   @Test
   public void testShouldProducePlanForSelectStarFromTable() throws InvalidQueryException
   {
      schema = schemataBuilder.addTable("my:mytype", "column1", "column2", "column3").build();
      query = builder.selectStar().from("my:mytype").query();
      queryContext = new QueryExecutionContext(schema, problems, null);
      plan = planner.createPlan(queryContext, query);
      assertThat(problems.hasProblems(), is(false));
      assertProjectNode(plan.findPlanByType(Type.PROJECT), "column1", "column2", "column3");
      assertThat(plan.getSize(), is(2));
      assertSourceNode(plan.findPlanByType(Type.SELECTOR), "my:mytype", null, "column1", "column2", "column3");
   }

   @Test
   public void testShouldProduceErrorWhenSelectingNonExistantTable() throws InvalidQueryException
   {
      schema = schemataBuilder.addTable("someTable", "column1", "column2", "column3").build();
      query = builder.selectStar().from("otheTable").query();
      queryContext = new QueryExecutionContext(schema, problems, null);
      plan = planner.createPlan(queryContext, query);
      assertThat(problems.hasProblems(), is(true));
   }

   @Test
   public void testShouldProduceErrorWhenSelectingNonExistantColumnOnExistingTable() throws InvalidQueryException
   {
      schema = schemataBuilder.addTable("someTable", "column1", "column2", "column3").build();
      query = builder.select("column1", "column4").from("someTable").query();
      queryContext = new QueryExecutionContext(schema, problems, null);
      plan = planner.createPlan(queryContext, query);
      assertThat(problems.hasProblems(), is(true));

   }

   @Test
   public void testShouldProducePlanWhenSelectingAllColumnsOnExistingTable() throws InvalidQueryException
   {
      schema = schemataBuilder.addTable("someTable", "column1", "column2", "column3").build();
      query = builder.selectStar().from("someTable").query();
      queryContext = new QueryExecutionContext(schema, problems, null);
      plan = planner.createPlan(queryContext, query);

      assertThat(problems.hasProblems(), is(false));
      assertProjectNode(plan.findPlanByType(Type.PROJECT), "column1", "column2", "column3");
      assertThat(plan.getSize(), is(2));
      assertSourceNode(plan.findPlanByType(Type.SELECTOR), "someTable", null, "column1", "column2", "column3");

   }

   @Test
   public void testShouldProducePlanWhenSelectingColumnsFromTableWithoutAlias() throws InvalidQueryException
   {
      schema = schemataBuilder.addTable("someTable", "column1", "column2", "column3").build();
      query =
         builder.select("column1", "column2").from("someTable").where().nodeName("someTable").isEqualTo("nodeTestName")
            .end().query();
      queryContext = new QueryExecutionContext(schema, problems, null);
      plan = planner.createPlan(queryContext, query);
      assertThat(problems.hasProblems(), is(false));
      assertThat(plan.findPlanByType(Type.PROJECT), notNullValue());
      QueryExecutionPlan executionStep = plan.findPlanByType(Type.PROJECT);
      assertThat(executionStep.getType(), is(Type.PROJECT));

      assertThat(executionStep.getSelectors(), is(selectors("someTable")));
   }

   @Test
   public void testShouldProducePlanWhenSelectingColumnsFromTableWithAlias() throws InvalidQueryException
   {
      schema = schemataBuilder.addTable("test:someTable", "column1", "column2", "column3").build();
      query =
         builder.select("column1", "column2").from("test:someTable AS t1").where().nodeName("t1").isEqualTo(
            "nodeTestName").end().query();
      queryContext = new QueryExecutionContext(schema, problems, null);
      plan = planner.createPlan(queryContext, query);
      assertThat(problems.hasProblems(), is(false));
      assertThat(plan.findPlanByType(Type.PROJECT), notNullValue());
      QueryExecutionPlan executionStep = plan.findPlanByType(Type.PROJECT);

      assertThat(executionStep.getSelectors(), is(selectors("t1")));
   }

   @Test
   public void testShouldProducePlanWhenSelectingAllColumnsFromTableWithAlias() throws InvalidQueryException
   {
      schema = schemataBuilder.addTable("test:someTable", "column1", "column2", "column3").build();
      query =
         builder.selectStar().from("test:someTable AS t1").where().nodeName("t1").isEqualTo("node name").end().query();
      queryContext = new QueryExecutionContext(schema, problems, null);
      plan = planner.createPlan(queryContext, query);
      assertThat(problems.hasProblems(), is(false));
      assertThat(plan.findPlanByType(Type.PROJECT), notNullValue());
      QueryExecutionPlan executionStep = plan.findPlanByType(Type.PROJECT);

      assertThat(executionStep.getSelectors(), is(selectors("t1")));
   }

   @Test
   public void testShouldProduceErrorWhenFullTextSearchingTableWithNoSearchableColumns() throws InvalidQueryException
   {
      schema = schemataBuilder.addTable("someTable", "column1", "column2", "column3").build();
      // Make sure the query without the search criteria does not have an error
      query = builder.select("column1", "column2").from("someTable").query();
      queryContext = new QueryExecutionContext(schema, problems, null);
      plan = planner.createPlan(queryContext, query);
      assertThat(problems.hasProblems(), is(false));

      query = builder.select("column1", "column2").from("someTable").where().search("someTable", "term1").end().query();
      queryContext = new QueryExecutionContext(schema, problems, null);
      plan = planner.createPlan(queryContext, query);
      assertThat(problems.hasProblems(), is(true));
   }

   @Test
   public void testShouldProducePlanWhenFullTextSearchingTableWithAtLeastOneSearchableColumn()
      throws InvalidQueryException
   {
      schema =
         schemataBuilder.addTable("someTable", "column1", "column2", "column3").makeSearchable("someTable", "column1")
            .build();
      query = builder.select("column1", "column4").from("someTable").where().search("someTable", "term1").end().query();
      queryContext = new QueryExecutionContext(schema, problems, null);
      plan = planner.createPlan(queryContext, query);
      assertThat(problems.hasProblems(), is(true));
   }

   @Test
   public void testShouldProduceErrorWhenFullTextSearchingColumnThatIsNotSearchable() throws InvalidQueryException
   {
      schema = schemataBuilder.addTable("someTable", "column1", "column2", "column3").build();
      // Make sure the query without the search criteria does not have an error
      query = builder.select("column1", "column2").from("someTable").query();
      queryContext = new QueryExecutionContext(schema, problems, null);
      plan = planner.createPlan(queryContext, query);
      assertThat(problems.hasProblems(), is(false));

      query =
         builder.select("column1", "column2").from("someTable").where().search("someTable", "column2", "term1").end()
            .query();
      queryContext = new QueryExecutionContext(schema, problems, null);
      plan = planner.createPlan(queryContext, query);
      assertThat(problems.hasProblems(), is(true));
   }

   @Test
   public void testShouldProducePlanWhenFullTextSearchingColumnThatIsSearchable() throws InvalidQueryException
   {
      schema =
         schemataBuilder.addTable("someTable", "column1", "column2", "column3").makeSearchable("someTable", "column1")
            .build();
      query =
         builder.select("column1", "column4").from("someTable").where().search("someTable", "column1", "term1").end()
            .query();
      queryContext = new QueryExecutionContext(schema, problems, null);
      plan = planner.createPlan(queryContext, query);
      assertThat(problems.hasProblems(), is(true));
   }

   @Test
   public void testShouldProducePlanWhenOrderByClauseIsUsed() throws InvalidQueryException
   {
      schema =
         schemataBuilder.addTable("test:someTable", "column1", "column2", "column3").makeSearchable("test:someTable",
            "column1").build();
      query =
         builder.selectStar().from("test:someTable AS t1").where().search("t1", "column1", "term1").end().orderBy()
            .ascending().propertyValue("t1", "column1").end().query();
      queryContext = new QueryExecutionContext(schema, problems, null);
      plan = planner.createPlan(queryContext, query);
      assertThat(problems.hasProblems(), is(false));

      assertThat(plan.findPlanByType(Type.SORT), notNullValue());
      QueryExecutionPlan executionStep = plan.findPlanByType(Type.SORT);

      assertThat(executionStep.getSelectors(), is(selectors("t1")));
      //TODO ASC

   }

   @Test
   public void testShouldProducePlanWhenOrderByClauseWithScoreIsUsed() throws InvalidQueryException
   {
      schema =
         schemataBuilder.addTable("test:someTable", "column1", "column2", "column3").makeSearchable("test:someTable",
            "column1").build();
      query =
         builder.selectStar().from("test:someTable AS t1").where().search("t1", "column1", "term1").end().orderBy()
            .ascending().fullTextSearchScore("t1").end().query();
      queryContext = new QueryExecutionContext(schema, problems, null);
      plan = planner.createPlan(queryContext, query);
      assertThat(problems.hasProblems(), is(false));

      assertThat(plan.findPlanByType(Type.SORT), notNullValue());
      QueryExecutionPlan executionStep = plan.findPlanByType(Type.SORT);

      assertThat(executionStep.getSelectors(), is(selectors("t1")));
      //TODO ASC
   }

   @Test
   public void testShouldProducePlanWhenLimitClauseIsUsed() throws InvalidQueryException
   {
      schema =
         schemataBuilder.addTable("test:someTable", "column1", "column2", "column3").makeSearchable("test:someTable",
            "column1").build();
      query =
         builder.selectStar().from("test:someTable AS t1").where().search("t1", "column1", "term1").end().limit(10)
            .query();
      queryContext = new QueryExecutionContext(schema, problems, null);
      plan = planner.createPlan(queryContext, query);
      assertThat(problems.hasProblems(), is(false));

      assertThat(plan.findPlanByType(Type.LIMIT), notNullValue());
      LimitExecutionPlan executionStep = (LimitExecutionPlan)plan.findPlanByType(Type.LIMIT);

      assertThat(executionStep.getLimit().getRowLimit(), is(10));
      assertThat(executionStep.getLimit().getOffset(), is(0));
   }

   @Test
   public void testShouldProducePlanWhenOffsetClauseIsUsed() throws InvalidQueryException
   {
      schema =
         schemataBuilder.addTable("test:someTable", "column1", "column2", "column3").makeSearchable("test:someTable",
            "column1").build();
      query =
         builder.selectStar().from("test:someTable AS t1").where().search("t1", "column1", "term1").end().limit(20)
            .offset(10).query();
      queryContext = new QueryExecutionContext(schema, problems, null);
      plan = planner.createPlan(queryContext, query);
      assertThat(problems.hasProblems(), is(false));

      assertThat(plan.findPlanByType(Type.LIMIT), notNullValue());
      LimitExecutionPlan executionStep = (LimitExecutionPlan)plan.findPlanByType(Type.LIMIT);

      assertThat(executionStep.getLimit().getRowLimit(), is(20));
      assertThat(executionStep.getLimit().getOffset(), is(10));

   }
}
