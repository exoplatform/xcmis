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
import org.xcmis.search.query.plan.QueryExecutionStep.Type;
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

   private Schema schemata;

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

   protected void print(QueryExecutionPlan plan)
   {
      if (print)
      {
         System.out.println(plan);
      }
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
   protected void assertProjectNode(QueryExecutionStep node, String... columnNames)
   {
      assertThat(node.getType(), is(Type.PROJECT));
      if (columnNames.length != 0)
      {
         assertThat(node.getPropertyValue("PROJECT_COLUMNS"), notNullValue());
      }
      List<Column> columns = (List<Column>)node.getPropertyValue("PROJECT_COLUMNS");
      assertThat(columns.size(), is(columnNames.length));
      for (int i = 0; i != columns.size(); ++i)
      {
         Column column = columns.get(i);
         assertThat(column.getColumnName(), is(columnNames[i]));
      }
   }

   @SuppressWarnings("unchecked")
   protected void assertSourceNode(QueryExecutionStep node, String sourceName, String sourceAlias,
      String... availableColumns)
   {
      assertThat(node.getType(), is(Type.SOURCE));
      assertThat(((SelectorName)node.getPropertyValue("SOURCE_NAME")), is(selector(sourceName)));

      if (sourceAlias != null)
      {
         assertThat(((SelectorName)node.getPropertyValue("SOURCE_ALIAS")), is(selector(sourceAlias)));
      }
      else
      {

         assertThat((node.getPropertyValue("SOURCE_ALIAS")), nullValue());
      }
      Collection<Schema.Column> columns = (Collection)node.getPropertyValue("SOURCE_COLUMNS");
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
      schemata = schemataBuilder.addTable("my:mytype", "column1", "column2", "column3").build();
      query = builder.selectStar().from("my:mytype").query();
      queryContext = new QueryExecutionContext(schemata, problems, null);
      plan = planner.createPlan(queryContext, query);
      assertThat(problems.hasProblems(), is(false));
      assertProjectNode(plan.findStep(Type.PROJECT), "column1", "column2", "column3");
      assertThat(plan.size(), is(2));
      assertSourceNode(plan.findStep(Type.SOURCE), "my:mytype", null, "column1", "column2", "column3");
   }

   @Test
   public void testShouldProduceErrorWhenSelectingNonExistantTable() throws InvalidQueryException
   {
      schemata = schemataBuilder.addTable("someTable", "column1", "column2", "column3").build();
      query = builder.selectStar().from("otheTable").query();
      queryContext = new QueryExecutionContext(schemata, problems, null);
      plan = planner.createPlan(queryContext, query);
      assertThat(problems.hasProblems(), is(true));
   }

   @Test
   public void testShouldProduceErrorWhenSelectingNonExistantColumnOnExistingTable() throws InvalidQueryException
   {
      schemata = schemataBuilder.addTable("someTable", "column1", "column2", "column3").build();
      query = builder.select("column1", "column4").from("someTable").query();
      queryContext = new QueryExecutionContext(schemata, problems, null);
      plan = planner.createPlan(queryContext, query);
      assertThat(problems.hasProblems(), is(true));

   }

   //
   //   @Test
   //   public void testShouldProducePlanWhenSelectingAllColumnsOnExistingTable()
   //   {
   //      schemata = schemataBuilder.addTable("someTable", "column1", "column2", "column3").build();
   //      query = builder.selectStar().from("someTable").query();
   //      queryContext = new QueryContext(schemata, typeSystem, hints, problems);
   //      plan = planner.createPlan(queryContext, query);
   //      print(plan);
   //      assertThat(problems.hasErrors(), is(false));
   //      assertThat(problems.isEmpty(), is(true));
   //      assertProjectNode(plan, "column1", "column2", "column3");
   //      assertThat(plan.getType(), is(PlanNode.Type.PROJECT));
   //      assertThat(plan.getChildCount(), is(1));
   //      PlanNode source = plan.getFirstChild();
   //      assertSourceNode(source, "someTable", null, "column1", "column2", "column3");
   //      assertThat(source.getChildCount(), is(0));
   //   }
   //
   //   @Test
   //   public void testShouldProducePlanWhenSelectingColumnsFromTableWithoutAlias()
   //   {
   //      schemata = schemataBuilder.addTable("someTable", "column1", "column2", "column3").build();
   //      query =
   //         builder.select("column1", "column2").from("someTable").where().path("someTable").isEqualTo(1L).end().query();
   //      queryContext = new QueryContext(schemata, typeSystem, hints, problems);
   //      plan = planner.createPlan(queryContext, query);
   //      assertThat(problems.hasErrors(), is(false));
   //      assertThat(plan.getType(), is(PlanNode.Type.PROJECT));
   //      assertThat(plan.getSelectors(), is(selectors("someTable")));
   //   }
   //
   //   @Test
   //   public void testShouldProducePlanWhenSelectingColumnsFromTableWithAlias()
   //   {
   //      schemata = schemataBuilder.addTable("dna:someTable", "column1", "column2", "column3").build();
   //      query =
   //         builder.select("column1", "column2").from("dna:someTable AS t1").where().path("t1").isEqualTo(1L).end()
   //            .query();
   //      queryContext = new QueryContext(schemata, typeSystem, hints, problems);
   //      plan = planner.createPlan(queryContext, query);
   //      assertThat(problems.hasErrors(), is(false));
   //      print(plan);
   //      assertThat(plan.getType(), is(PlanNode.Type.PROJECT));
   //      assertThat(plan.getSelectors(), is(selectors("t1")));
   //   }
   //
   //   @Test
   //   public void testShouldProducePlanWhenSelectingAllColumnsFromTableWithAlias()
   //   {
   //      schemata = schemataBuilder.addTable("dna:someTable", "column1", "column2", "column3").build();
   //      query = builder.selectStar().from("dna:someTable AS t1").where().path("t1").isEqualTo(1L).end().query();
   //      queryContext = new QueryContext(schemata, typeSystem, hints, problems);
   //      plan = planner.createPlan(queryContext, query);
   //      assertThat(problems.hasErrors(), is(false));
   //      print(plan);
   //      assertThat(plan.getType(), is(PlanNode.Type.PROJECT));
   //      assertThat(plan.getSelectors(), is(selectors("t1")));
   //   }
   //
   //   @Test
   //   public void testShouldProduceErrorWhenFullTextSearchingTableWithNoSearchableColumns()
   //   {
   //      schemata = schemataBuilder.addTable("someTable", "column1", "column2", "column3").build();
   //      // Make sure the query without the search criteria does not have an error
   //      query = builder.select("column1", "column2").from("someTable").query();
   //      queryContext = new QueryContext(schemata, typeSystem, hints, problems);
   //      plan = planner.createPlan(queryContext, query);
   //      assertThat(problems.hasErrors(), is(false));
   //
   //      query = builder.select("column1", "column2").from("someTable").where().search("someTable", "term1").end().query();
   //      queryContext = new QueryContext(schemata, typeSystem, hints, problems);
   //      plan = planner.createPlan(queryContext, query);
   //      assertThat(problems.hasErrors(), is(true));
   //   }
   //
   //   @Test
   //   public void testShouldProducePlanWhenFullTextSearchingTableWithAtLeastOneSearchableColumn()
   //   {
   //      schemata =
   //         schemataBuilder.addTable("someTable", "column1", "column2", "column3").makeSearchable("someTable", "column1")
   //            .build();
   //      query = builder.select("column1", "column4").from("someTable").where().search("someTable", "term1").end().query();
   //      queryContext = new QueryContext(schemata, typeSystem, hints, problems);
   //      plan = planner.createPlan(queryContext, query);
   //      assertThat(problems.hasErrors(), is(true));
   //   }
   //
   @Test
   public void testShouldProduceErrorWhenFullTextSearchingColumnThatIsNotSearchable() throws InvalidQueryException
   {
      schemata = schemataBuilder.addTable("someTable", "column1", "column2", "column3").build();
      // Make sure the query without the search criteria does not have an error
      query = builder.select("column1", "column2").from("someTable").query();
      queryContext = new QueryExecutionContext(schemata, problems, null);
      plan = planner.createPlan(queryContext, query);
      assertThat(problems.hasProblems(), is(false));

      query =
         builder.select("column1", "column2").from("someTable").where().search("someTable", "column2", "term1").end()
            .query();
      queryContext = new QueryExecutionContext(schemata, problems, null);
      plan = planner.createPlan(queryContext, query);
      assertThat(problems.hasProblems(), is(true));
   }

   @Test
   public void testShouldProducePlanWhenFullTextSearchingColumnThatIsSearchable() throws InvalidQueryException
   {
      schemata =
         schemataBuilder.addTable("someTable", "column1", "column2", "column3").makeSearchable("someTable", "column1")
            .build();
      query =
         builder.select("column1", "column4").from("someTable").where().search("someTable", "column1", "term1").end()
            .query();
      queryContext = new QueryExecutionContext(schemata, problems, null);
      plan = planner.createPlan(queryContext, query);
      assertThat(problems.hasProblems(), is(true));
   }
   //
   //   @Test
   //   public void testShouldProducePlanWhenOrderByClauseIsUsed()
   //   {
   //      schemata = schemataBuilder.addTable("dna:someTable", "column1", "column2", "column3").build();
   //      query =
   //         builder.selectStar().from("dna:someTable AS t1").where().path("t1").isEqualTo(1L).end().orderBy().ascending()
   //            .propertyValue("t1", "column1").end().query();
   //      queryContext = new QueryContext(schemata, typeSystem, hints, problems);
   //      plan = planner.createPlan(queryContext, query);
   //      assertThat(problems.hasErrors(), is(false));
   //      print(plan);
   //      assertThat(plan.getType(), is(PlanNode.Type.SORT));
   //      assertThat(plan.getSelectors(), is(selectors("t1")));
   //   }
   //
   //   @Test
   //   public void testShouldProducePlanWhenOrderByClauseWithScoreIsUsed()
   //   {
   //      schemata = schemataBuilder.addTable("dna:someTable", "column1", "column2", "column3").build();
   //      query =
   //         builder.selectStar().from("dna:someTable AS t1").where().path("t1").isEqualTo(1L).end().orderBy().ascending()
   //            .fullTextSearchScore("t1").end().query();
   //      queryContext = new QueryContext(schemata, typeSystem, hints, problems);
   //      plan = planner.createPlan(queryContext, query);
   //      assertThat(problems.hasErrors(), is(false));
   //      print(plan);
   //      assertThat(plan.getType(), is(PlanNode.Type.SORT));
   //      assertThat(plan.getSelectors(), is(selectors("t1")));
   //   }
}
