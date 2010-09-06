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
import org.exoplatform.services.document.DocumentReaderService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xcmis.search.InvalidQueryException;
import org.xcmis.search.SearchService;
import org.xcmis.search.SearchServiceException;
import org.xcmis.search.config.IndexConfiguration;
import org.xcmis.search.config.SearchServiceConfiguration;
import org.xcmis.search.content.ContentEntry;
import org.xcmis.search.content.InMemorySchema;
import org.xcmis.search.content.Property;
import org.xcmis.search.content.Schema;
import org.xcmis.search.content.InMemorySchema.Builder;
import org.xcmis.search.content.Property.BinaryValue;
import org.xcmis.search.content.command.InvocationContext;
import org.xcmis.search.content.interceptors.ContentReaderInterceptor;
import org.xcmis.search.lucene.InMemoryLuceneQueryableIndexStorage;
import org.xcmis.search.lucene.content.SchemaTableResolver;
import org.xcmis.search.model.Query;
import org.xcmis.search.result.ScoredRow;
import org.xcmis.search.value.CastSystem;
import org.xcmis.search.value.NameConverter;
import org.xcmis.search.value.PropertyType;
import org.xcmis.search.value.SlashSplitter;
import org.xcmis.search.value.ToStringNameConverter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * Test for search service.
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
      SearchService searchService = new SearchService(configuration);
   }

   @Test
   public void testShouldCreateSearchService() throws SearchServiceException
   {
      NameConverter<String> nameConverter = new ToStringNameConverter();
      SchemaTableResolver tableResolver = new SchemaTableResolver(nameConverter, schema);

      //index configuration
      IndexConfiguration indexConfuration = new IndexConfiguration();
      indexConfuration.setIndexDir(tempDir.getAbsolutePath());
      indexConfuration.setRootParentUuid("rootParentUuid");
      indexConfuration.setRootUuid("rootUuid");
      indexConfuration.setDocumentReaderService(mock(DocumentReaderService.class));

      //search service configuration
      SearchServiceConfiguration configuration = new SearchServiceConfiguration();
      configuration.setIndexConfiguration(indexConfuration);
      configuration.setContentReader(mock(ContentReaderInterceptor.class));
      configuration.setPathSplitter(new SlashSplitter());
      configuration.setTableResolver(tableResolver);
      configuration.setNameConverter(nameConverter);
      SearchService luceneSearchService = new SearchService(configuration);

      assertThat(luceneSearchService, notNullValue());
   }

   @Test
   public void testShouldCreateSearchServiceWithRamDirectory() throws SearchServiceException
   {
      NameConverter<String> nameConverter = new ToStringNameConverter();
      SchemaTableResolver tableResolver = new SchemaTableResolver(nameConverter, schema);

      //index configuration
      IndexConfiguration indexConfuration = new IndexConfiguration();
      indexConfuration.setIndexDir(tempDir.getAbsolutePath());
      indexConfuration.setRootParentUuid("rootParentUuid");
      indexConfuration.setRootUuid("rootUuid");
      indexConfuration.setDocumentReaderService(mock(DocumentReaderService.class));
      indexConfuration.setQueryableIndexStorage(InMemoryLuceneQueryableIndexStorage.class.getName());

      //search service configuration
      SearchServiceConfiguration configuration = new SearchServiceConfiguration();
      configuration.setIndexConfiguration(indexConfuration);
      configuration.setContentReader(mock(ContentReaderInterceptor.class));
      configuration.setPathSplitter(new SlashSplitter());
      configuration.setTableResolver(tableResolver);
      configuration.setNameConverter(nameConverter);
      SearchService luceneSearchService = new SearchService(configuration);

      assertThat(luceneSearchService, notNullValue());
   }

   @Test(expected = SearchServiceException.class)
   public void testShouldNotCreateSearchServiceWithWrongStorage() throws SearchServiceException
   {
      NameConverter<String> nameConverter = new ToStringNameConverter();
      SchemaTableResolver tableResolver = new SchemaTableResolver(nameConverter, schema);

      //index configuration
      IndexConfiguration indexConfuration = new IndexConfiguration();
      indexConfuration.setIndexDir(tempDir.getAbsolutePath());
      indexConfuration.setRootParentUuid("rootParentUuid");
      indexConfuration.setRootUuid("rootUuid");
      indexConfuration.setDocumentReaderService(mock(DocumentReaderService.class));
      indexConfuration.setQueryableIndexStorage(DocumentReaderService.class.getName());

      //search service configuration
      SearchServiceConfiguration configuration = new SearchServiceConfiguration();
      configuration.setIndexConfiguration(indexConfuration);
      configuration.setContentReader(mock(ContentReaderInterceptor.class));
      configuration.setPathSplitter(new SlashSplitter());
      configuration.setTableResolver(tableResolver);
      configuration.setNameConverter(nameConverter);
      SearchService luceneSearchService = new SearchService(configuration);

   }

   //TODO check
   @Test(expected = InvalidQueryException.class)
   public void testShouldRunQuerySearchServie() throws SearchServiceException, InvalidQueryException
   {
      //value
      NameConverter<String> nameConverter = new ToStringNameConverter();
      SchemaTableResolver tableResolver = new SchemaTableResolver(nameConverter, schema);

      //index configuration
      IndexConfiguration indexConfuration = new IndexConfiguration();
      indexConfuration.setIndexDir(tempDir.getAbsolutePath());
      indexConfuration.setRootParentUuid("rootParentUuid");
      indexConfuration.setRootUuid("rootUuid");
      indexConfuration.setDocumentReaderService(mock(DocumentReaderService.class));

      //search service configuration
      SearchServiceConfiguration configuration = new SearchServiceConfiguration();
      configuration.setIndexConfiguration(indexConfuration);
      configuration.setContentReader(mock(ContentReaderInterceptor.class));
      configuration.setNameConverter(nameConverter);
      configuration.setTableResolver(tableResolver);
      configuration.setPathSplitter(new SlashSplitter());
      InvocationContext invocationContext = new InvocationContext();
      invocationContext.setSchema(schema);

      invocationContext.setTableResolver(tableResolver);
      invocationContext.setNameConverter(nameConverter);
      configuration.setDefaultInvocationContext(invocationContext);

      SearchService luceneSearchService = new SearchService(configuration);

      Query query = builder.selectStar().from("someTable").query();

      luceneSearchService.execute(query);
   }

   @Test
   public void testShouldIndexBinaryDocument() throws SearchServiceException, InvalidQueryException
   {
      //value
      NameConverter<String> nameConverter = new ToStringNameConverter();
      SchemaTableResolver tableResolver = new SchemaTableResolver(nameConverter, schema);

      //index configuration
      IndexConfiguration indexConfuration = new IndexConfiguration();
      indexConfuration.setIndexDir(tempDir.getAbsolutePath());
      indexConfuration.setRootParentUuid("rootParentUuid");
      indexConfuration.setRootUuid("rootUuid");
      indexConfuration.setDocumentReaderService(mock(DocumentReaderService.class));

      //search service configuration
      SearchServiceConfiguration configuration = new SearchServiceConfiguration();
      configuration.setIndexConfiguration(indexConfuration);
      configuration.setContentReader(mock(ContentReaderInterceptor.class));
      configuration.setNameConverter(nameConverter);
      configuration.setTableResolver(tableResolver);
      configuration.setPathSplitter(new SlashSplitter());

      SearchService luceneSearchService = new SearchService(configuration);
      luceneSearchService.start();

      byte[] bytes =
         ("Apollo 7 (October 11-22, 1968) was the first manned mission "
            + "in the Apollo program to be launched. It was an eleven-day "
            + "Earth-orbital mission, the first manned launch of the "
            + "Saturn IB launch vehicle, and the first three-person " + "American space mission").getBytes();

      Collection<BinaryValue> values = new ArrayList<BinaryValue>();
      values.add(new BinaryValue(new ByteArrayInputStream(bytes), "plain/text", null, bytes.length));

      Property[] props = new Property[]{new Property(PropertyType.BINARY, "content", values)};
      List<ContentEntry> entys = new ArrayList<ContentEntry>();
      entys.add(new ContentEntry("doc", new String[]{"someTable"}, UUID.randomUUID().toString(), new String[]{UUID
         .randomUUID().toString()}, props));

      luceneSearchService.update(entys, new HashSet<String>());

      InvocationContext invocationContext = new InvocationContext();
      invocationContext.setSchema(schema);

      invocationContext.setTableResolver(tableResolver);
      invocationContext.setNameConverter(nameConverter);

      Query query = builder.selectStar().from("someTable AS someTable").query();
      List<ScoredRow> result = luceneSearchService.execute(query, new HashMap(), invocationContext);
      assertThat(result.size(), is(1));
   }
}
