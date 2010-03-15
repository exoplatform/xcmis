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
package org.xcmis.search.query;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xcmis.search.SearchServiceException;
import org.xcmis.search.config.IndexConfurationImpl;
import org.xcmis.search.config.SearchServiceConfiguration;
import org.xcmis.search.content.InMemorySchema;
import org.xcmis.search.content.Schema;
import org.xcmis.search.content.InMemorySchema.Builder;
import org.xcmis.search.content.command.InvocationContext;
import org.xcmis.search.content.interceptors.ReadOnlyInterceptor;
import org.xcmis.search.lucene.LuceneSearchService;
import org.xcmis.search.model.Query;
import org.xcmis.search.value.CastSystem;

import java.io.File;
import java.util.HashMap;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@exoplatform.org">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z ksm $
 *
 */
public class SearchServiceTest
{
   private QueryBuilder builder;

   private File tempDir;

   private Builder schemaBuilder;

   private Schema schema;

   @Before
   public void beforeEach()
   {
      builder = new QueryBuilder(mock(CastSystem.class));

      schemaBuilder = InMemorySchema.createBuilder();
      schema = schemaBuilder.addTable("someTable", "column1", "column2", "column3").build();

      tempDir = new File(System.getProperty("java.io.tmpdir"), "search-service");
      if (tempDir.exists())
      {
         assertThat(FileUtils.deleteQuietly(tempDir), is(true));
      }
      assertThat(tempDir.mkdirs(), is(true));

   }

   @After
   public void tearDown() throws Exception
   {
      if (tempDir.exists())
      {
         assertThat(FileUtils.deleteQuietly(tempDir), is(true));
      }
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldNotCreateSearchServieWithEmptyReadOnlyInterceptor() throws SearchServiceException
   {
      SearchServiceConfiguration configuration = new SearchServiceConfiguration();
      LuceneSearchService luceneSearchService = new LuceneSearchService(configuration);
   }

   @Test
   public void testShouldCreateSearchServie() throws SearchServiceException
   {
      //index configuration
      IndexConfurationImpl indexConfuration = new IndexConfurationImpl();
      indexConfuration.setIndexDir(tempDir.getAbsolutePath());
      //search service configuration
      SearchServiceConfiguration configuration = new SearchServiceConfiguration();
      configuration.setIndexConfuguration(indexConfuration);
      configuration.setContentReader(mock(ReadOnlyInterceptor.class));
      LuceneSearchService luceneSearchService = new LuceneSearchService(configuration);

      assertThat(luceneSearchService, notNullValue());
   }

   @Test
   public void testShouldRunQuerySearchServie() throws SearchServiceException
   {
      //index configuration
      IndexConfurationImpl indexConfuration = new IndexConfurationImpl();
      indexConfuration.setIndexDir(tempDir.getAbsolutePath());

      //search service configuration
      SearchServiceConfiguration configuration = new SearchServiceConfiguration();
      configuration.setIndexConfuguration(indexConfuration);
      configuration.setContentReader(mock(ReadOnlyInterceptor.class));
      LuceneSearchService luceneSearchService = new LuceneSearchService(configuration);

      Query query = builder.selectStar().from("someTable").query();

      InvocationContext invocationContext = new InvocationContext();
      invocationContext.setSchema(schema);
      luceneSearchService.setInvocationContext(invocationContext);

      luceneSearchService.execute(query, new HashMap<String, Object>());
   }

   @Test
   public void testShouldBuildQueryWithThreeHasPropertyConstraint() throws SearchServiceException
   {

      //index configuration
      IndexConfurationImpl indexConfuration = new IndexConfurationImpl();
      indexConfuration.setIndexDir(tempDir.getAbsolutePath());

      //search service configuration
      SearchServiceConfiguration configuration = new SearchServiceConfiguration();
      configuration.setIndexConfuguration(indexConfuration);
      configuration.setContentReader(mock(ReadOnlyInterceptor.class));
      LuceneSearchService luceneSearchService = new LuceneSearchService(configuration);

      String strSql = "SELECT * FROM table AS nodes " + //
         "WHERE ((nodes.col1 IS NOT NULL " + //
         "AND nodes.col2 IS NOT NULL) " + //
         "AND nodes.col3 IS NOT NULL)";

      Query parsedQuery = luceneSearchService.parse(strSql, "SQL");

      Query query =
         builder.selectStar().from("table AS nodes").where().hasProperty("nodes", "col1").and().hasProperty("nodes",
            "col2").and().hasProperty("nodes", "col3").end().query();

      assertThat(parsedQuery, is(query));
   }
}
