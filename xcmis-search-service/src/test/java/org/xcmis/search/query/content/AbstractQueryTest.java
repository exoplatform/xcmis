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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NotImplementedException;
import org.exoplatform.services.document.DocumentReaderService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.junit.After;
import org.junit.Before;
import org.omg.CORBA.portable.ValueFactory;
import org.xcmis.search.SearchService;
import org.xcmis.search.config.IndexConfiguration;
import org.xcmis.search.config.SearchServiceConfiguration;
import org.xcmis.search.content.ContentEntry;
import org.xcmis.search.content.InMemorySchema;
import org.xcmis.search.content.IndexModificationException;
import org.xcmis.search.content.Property;
import org.xcmis.search.content.Schema;
import org.xcmis.search.content.InMemorySchema.Builder;
import org.xcmis.search.content.Property.SimpleValue;
import org.xcmis.search.content.command.InvocationContext;
import org.xcmis.search.content.interceptors.ContentReaderInterceptor;
import org.xcmis.search.lucene.LuceneSearchService;
import org.xcmis.search.lucene.content.SchemaTableResolver;
import org.xcmis.search.model.Query;
import org.xcmis.search.query.QueryBuilder;
import org.xcmis.search.result.ScoredRow;
import org.xcmis.search.value.CastSystem;
import org.xcmis.search.value.NameConverter;
import org.xcmis.search.value.PropertyType;
import org.xcmis.search.value.SlashSplitter;
import org.xcmis.search.value.ToStringNameConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Abstract base class for query test cases.
 */
public abstract class AbstractQueryTest
{
   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(AbstractQueryTest.class);

   /**
    * Resolved Name for jcr:score
    */
   protected String jcrScore;

   /**
    * Resolved Name for jcr:path
    */
   protected String jcrPath;

   /**
    * Resolved Name for jcr:root
    */
   protected String jcrRoot;

   /**
    * Resolved Name for jcr:contains
    */
   protected String jcrContains;

   /**
    * Resolved Name for jcr:deref
    */
   protected String jcrDeref;

   /**
    * The string /${jcrRoot}${testRoot} with all components of the test path
    * properly escaped for XPath.
    *
    * @see <a href="https://issues.apache.org/jira/browse/JCR-714">JCR-714</a>
    */
   protected String xpathRoot;

   /**
    * The query object model factory for {@link #superuser}.
    */
   protected QueryBuilder qf;

   /**
    * The value factory for creating literals for the query object model.
    */
   protected ValueFactory vf;

   /**
    * The query manager for {@link #superuser}
    */
   protected SearchService qm;

   protected Node testRootNode;

   protected String rootNodeType = "rootNodeType";

   protected String testNodeType = "testNodeType";

   protected String nodeName1 = "nodeName1";

   protected String nodeName2 = "nodeName2";

   protected String propertyName1 = "propertyName1";

   protected String propertyName2 = "propertyName2";

   private File tempDir;

   private Schema schema;

   protected SearchService searchService;

   /**
    * Set-up the configuration values used for the test. Per default retrieves
    * a session, configures testRoot, and nodetype and checks if the query
    * language for the current language is available.<br>
    */
   @Before
   public void setUp() throws Exception
   {

      tempDir = new File(System.getProperty("java.io.tmpdir"), "search-service");
      if (tempDir.exists())
      {
         assertThat(FileUtils.deleteQuietly(tempDir), is(true));
      }
      assertThat(tempDir.mkdirs(), is(true));

      Builder schemaBuilder = InMemorySchema.createBuilder();
      schema =
         schemaBuilder.addTable(rootNodeType, propertyName1, propertyName2).addTable(testNodeType, propertyName1,
            propertyName2).build();
      qf = new QueryBuilder(mock(CastSystem.class));

      testRootNode =
         new Node(null, "", new String[]{rootNodeType}, UUID.randomUUID().toString(),
            new String[]{Node.ROOT_PARENT_UUID}, new Property[0]);

      //value
      NameConverter<String> nameConverter = new ToStringNameConverter();
      SchemaTableResolver tableResolver = new SchemaTableResolver(nameConverter, schema);

      //index configuration
      IndexConfiguration indexConfuration = new IndexConfiguration();
      indexConfuration.setIndexDir(tempDir.getAbsolutePath());
      indexConfuration.setRootParentUuid(testRootNode.getParentIdentifiers()[0]);
      indexConfuration.setRootUuid(testRootNode.getIdentifier());
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

      searchService = new LuceneSearchService(configuration);
      searchService.start();

      List<ContentEntry> contentEntries = new ArrayList<ContentEntry>();
      contentEntries.add(testRootNode);
      searchService.update(contentEntries, Collections.EMPTY_SET);
   }

   @After
   public void tearDown() throws Exception
   {
      Set<String> removed = new HashSet<String>();
      //removed.add(testRootNode.getIdentifier());

      for (ContentEntry entry : testRootNode.getTree())
      {
         removed.add(entry.getIdentifier());
      }
      searchService.update(Collections.EMPTY_LIST, removed);

      qm = null;
      qf = null;
      vf = null;

   }

   /**
    * Create a {@link Query} for a given {@link Statement}.
    *
    * @param statement the query should be created for
    * @return
    *
    * @throws RepositoryException
    * @see #createQuery(String, String)
    */
   protected Query createQuery(String statement)
   {
      //return createQuery(statement.getStatement(), statement.getLanguage());
      throw new NotImplementedException();
   }

   /**
    * Creates a {@link Query} for the given statement in the requested
    * language, treating optional languages gracefully
    * @throws RepositoryException
    */
   protected Query createQuery(String statement, String language)
   {
      throw new NotImplementedException();
   }

   /**
    * Creates and executes a {@link Query} for the given {@link Statement}
    *
    * @param statement to execute
    * @return
    *
    * @throws RepositoryException
    * @see #execute(String, String)
    */
   protected void execute(String statement)
   {
      throw new NotImplementedException();
   }

   /**
    * Creates and executes a {@link Query} for a given Statement in a given
    * query language
    *
    * @param statement the query should be build for
    * @param language  query language the stement is written in
    * @return
    *
    * @throws RepositoryException
    */
   protected void execute(String statement, String language)
   {
      throw new NotImplementedException();
   }

   /**
    * Checks if the <code>result</code> contains a number of
    * <code>hits</code>.
    *
    * @param result the <code>QueryResult</code>.
    * @param hits   the number of expected hits.
    * @throws RepositoryException if an error occurs while iterating over the
    *                             result nodes.
    */
   protected void checkResult(List<ScoredRow> result, int hits)
   {

      long count = result.size();
      if (count == 0)
      {
         LOG.info(" NONE");
      }
      else if (count == -1)
      {
         // have to count in a loop
         count = 0;
         for (ScoredRow scoredRow : result)
         {
            count++;
         }

      }
      assertEquals("Wrong hit count.", hits, count);
   }

   /**
    * Checks if the <code>result</code> contains a number of <code>hits</code>
    * and <code>properties</code>.
    *
    * @param result     the <code>QueryResult</code>.
    * @param hits       the number of expected hits.
    * @param properties the number of expected properties.
    * @throws RepositoryException if an error occurs while iterating over the
    *                             result nodes.
    */
   protected void checkResult(List<ScoredRow> result, int hits, int properties)
   {
      //      checkResult(result, hits);
      //      // now check property count
      //      int count = 0;
      //      LOG.info("Properties:");
      //      String[] propNames = result.getColumnNames();
      //      for (RowIterator it = result.getRows(); it.hasNext();)
      //      {
      //         StringBuffer msg = new StringBuffer();
      //         Value[] values = it.nextRow().getValues();
      //         for (int i = 0; i < propNames.length; i++, count++)
      //         {
      //            msg.append("  ").append(propNames[i]).append(": ");
      //            if (values[i] == null)
      //            {
      //               msg.append("null");
      //            }
      //            else
      //            {
      //               msg.append(values[i].getString());
      //            }
      //         }
      //         LOG.info(msg);
      //      }
      //      if (count == 0)
      //      {
      //         LOG.info("  NONE");
      //      }
      //      assertEquals("Wrong property count.", properties, count);
      throw new NotImplementedException();
   }

   /**
    * Checks if the {@link QueryResult} is ordered according order property in
    * direction of related argument.
    *
    * @param queryResult to be tested
    * @param propName    Name of the porperty to order by
    * @param descending  if <code>true</code> order has to be descending
    * @throws RepositoryException
    * @throws NotExecutableException in case of less than two results or all
    *                                results have same size of value in its
    *                                order-property
    */
   protected void evaluateResultOrder(List<ScoredRow> result, String propName, boolean descending)
   {
      //      NodeIterator nodes = queryResult.getNodes();
      //      if (getSize(nodes) < 2)
      //      {
      //         fail("Workspace does not contain sufficient content to test ordering on result nodes.");
      //      }
      //      // need to re-aquire nodes, {@link #getSize} may consume elements.
      //      nodes = queryResult.getNodes();
      //      int changeCnt = 0;
      //      String last = descending ? "\uFFFF" : "";
      //      while (nodes.hasNext())
      //      {
      //         String value = nodes.nextNode().getProperty(propName).getString();
      //         int cp = value.compareTo(last);
      //         // if value changed evaluate if the ordering is correct
      //         if (cp != 0)
      //         {
      //            changeCnt++;
      //            if (cp > 0 && descending)
      //            {
      //               fail("Repository doesn't order properly descending");
      //            }
      //            else if (cp < 0 && !descending)
      //            {
      //               fail("Repository doesn't order properly ascending");
      //            }
      //         }
      //         last = value;
      //      }
      //      if (changeCnt < 1)
      //      {
      //         fail("Workspace does not contain distinct values for " + propName);
      //      }
      throw new NotImplementedException();
   }

   /**
    * Executes the <code>sql</code> query and checks the results against
    * the specified <code>nodes</code>.
    * @param session the session to use for the query.
    * @param sql the sql query.
    * @param nodes the expected result nodes.
    * @throws NotExecutableException 
    */
   protected void executeSqlQuery(String sql, String[] nodes)
   {
      throw new NotImplementedException();
   }

   protected void save(Node node) throws IndexModificationException
   {
      searchService.update(node.getTree(), Collections.EMPTY_SET);
   }

   /**
    * Checks if the result set contains exactly the <code>nodes</code>.
    * @param result the query result.
    * @param nodes the expected nodes in the result set.
    */
   protected void checkResult(List<ScoredRow> result, String selectorName, Node[] nodes)
   {
      // collect paths
      Set<String> expectedPaths = new HashSet<String>();
      for (int i = 0; i < nodes.length; i++)
      {
         expectedPaths.add(nodes[i].getIdentifier());
      }
      Set<String> resultPaths = new HashSet<String>();
      for (ScoredRow object : result)
      {

         resultPaths.add(object.getNodeIdentifer(selectorName));
      }
      // check if all expected are in result
      for (Iterator it = expectedPaths.iterator(); it.hasNext();)
      {
         String path = (String)it.next();
         assertTrue(path + " is not part of the result set", resultPaths.contains(path));
      }
      // check result does not contain more than expected
      for (Iterator it = resultPaths.iterator(); it.hasNext();)
      {
         String path = (String)it.next();
         assertTrue(path + " is not expected to be part of the result set", expectedPaths.contains(path));
      }
      //throw new NotImplementedException();
   }

   //   /**
   //    * Returns the nodes in <code>it</code> as an array of Nodes.
   //    * @param it the NodeIterator.
   //    * @return the elements of the iterator as an array of Nodes.
   //    */
   //   protected String[] toArray(NodeIterator it)
   //   {
   //      List nodes = new ArrayList();
   //      while (it.hasNext())
   //      {
   //         nodes.add(it.nextNode());
   //      }
   //      return (Node[])nodes.toArray(new Node[nodes.size()]);
   //   }

   /**
    * Escape an identifier suitable for the SQL parser
    * @TODO currently only handles dash character
    */
   protected String escapeIdentifierForSQL(String identifier)
   {

      boolean needsEscaping = identifier.indexOf('-') >= 0;

      if (!needsEscaping)
      {
         return identifier;
      }
      else
      {
         return '"' + identifier + '"';
      }
   }

   //   /**
   //    * @param language a query language.
   //    * @return <code>true</code> if <code>language</code> is supported;
   //    *         <code>false</code> otherwise.
   //    * @throws RepositoryException if an error occurs.
   //    */
   //   protected boolean isSupportedLanguage(String language) throws RepositoryException
   //   {
   //      return Arrays.asList(qm.getSupportedQueryLanguages()).contains(language);
   //   }

   public static class Node extends ContentEntry
   {
      public static final String ROOT_PARENT_UUID = "";

      private final List<Node> childNodes;

      private final Map<String, Property<?>> properties;

      private final Node parentNode;

      /**
       * @param name
       * @param tableNames
       * @param identifer
       * @param parentIdentifiers
       * @param properties
       */
      public Node(Node parentNode, String name, String[] tableNames, String identifer, String[] parentIdentifiers,
         Property<?>[] properties)
      {
         super(name, tableNames, identifer, parentIdentifiers, properties);
         this.parentNode = parentNode;
         this.childNodes = new ArrayList<Node>();
         this.properties = new HashMap<String, Property<?>>();
      }

      /**
       * @param nodeName1
       * @param testNodeType
       * @return
       */
      public Node addNode(String nodeName, String testNodeType)
      {

         Node child =
            new Node(this, nodeName, new String[]{testNodeType}, UUID.randomUUID().toString(),
               new String[]{getIdentifier()}, new Property[0]);
         childNodes.add(child);
         return child;
      }

      /**
       * @param propertyName1
       * @param string
       */
      public void setProperty(String propertyName1, String string)
      {
         properties.put(propertyName1, new Property<String>(PropertyType.STRING, propertyName1,
            new SimpleValue<String>(string)));
      }

      /**
       * @see org.xcmis.search.content.ContentEntry#getProperties()
       */
      @Override
      public Property<?>[] getProperties()
      {
         return properties.values().toArray(new Property[properties.size()]);
      }

      /**
       * @return
       */
      public String getPath()
      {
         if (parentNode == null)
         {
            return "/";
         }
         return parentNode.getPath() + "/" + getName();
      }

      public List<ContentEntry> getTree()
      {
         List<ContentEntry> nodes = new ArrayList<ContentEntry>();
         nodes.add(this);
         for (Node childNode : childNodes)
         {
            nodes.addAll(childNode.getTree());
         }
         return nodes;
      }
   }
}
