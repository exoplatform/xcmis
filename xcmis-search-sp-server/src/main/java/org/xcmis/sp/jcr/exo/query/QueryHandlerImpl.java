/**
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

package org.xcmis.sp.jcr.exo.query;

import org.apache.tools.ant.taskdefs.PathConvert;
import org.exoplatform.services.jcr.core.nodetype.NodeTypeData;
import org.exoplatform.services.jcr.core.nodetype.NodeTypeDataManager;
import org.exoplatform.services.jcr.core.nodetype.PropertyDefinitionData;
import org.exoplatform.services.jcr.core.nodetype.PropertyDefinitionDatas;
import org.exoplatform.services.jcr.datamodel.InternalQName;
import org.exoplatform.services.jcr.datamodel.QPath;
import org.exoplatform.services.jcr.datamodel.QPathEntry;
import org.exoplatform.services.jcr.impl.core.JCRPath;
import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.xcmis.search.SearchServiceException;
import org.xcmis.search.Visitors;
import org.xcmis.search.config.IndexConfuguration;
import org.xcmis.search.config.IndexConfurationImpl;
import org.xcmis.search.config.SearchServiceConfiguration;
import org.xcmis.search.content.Schema;
import org.xcmis.search.content.Schema.Table;
import org.xcmis.search.content.command.InvocationContext;
import org.xcmis.search.content.interceptors.CommandInterceptor;
import org.xcmis.search.content.interceptors.InterceptorChain;
import org.xcmis.search.lucene.LuceneQueryableIndexStorage;
import org.xcmis.search.lucene.LuceneSearchService;
import org.xcmis.search.model.Query;
import org.xcmis.search.model.column.Column;
import org.xcmis.search.model.source.SelectorName;
import org.xcmis.search.parser.CmisQueryParser;
import org.xcmis.search.result.ScoredRow;
import org.xcmis.search.value.NameConverter;
import org.xcmis.search.value.PathSplitter;
import org.xcmis.search.value.ToStringNameConverter;
import org.xcmis.sp.jcr.exo.query.index.StartableJcrIndexingService;
import org.xcmis.sp.jcr.exo.query.lucene.LuceneVirtualTableResolver;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.object.ItemsIterator;
import org.xcmis.spi.query.QueryHandler;
import org.xcmis.spi.query.Result;
import org.xcmis.spi.query.Score;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: QueryHandlerImpl.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class QueryHandlerImpl implements QueryHandler
{

   private final ContentProxy contenProxy;

   //private QueryNameResolver resolver;

   private CmisQueryParser queryParser;

   private LuceneSearchService luceneSearchService;

   private NodeTypeShema schema;

   private NameConverter nameConverter;

   private final LocationFactory locationFactory;

   public QueryHandlerImpl(final ContentProxy contenProxy, IndexConfuguration indexConfiguration,
      NodeTypeDataManager nodeTypeManager, LocationFactory locationFactory) throws SearchServiceException
   {
      super();

      this.contenProxy = contenProxy;
      this.locationFactory = locationFactory;
      //  this.resolver = resolver;
      this.queryParser = new CmisQueryParser();
      NameConverter<String> nameConverter = new ToStringNameConverter();
      schema = new NodeTypeShema(nodeTypeManager, locationFactory);
      LuceneVirtualTableResolver tableResolver = null;
      try
      {
         tableResolver = new LuceneVirtualTableResolver(nodeTypeManager, locationFactory);
      }
      catch (javax.jcr.RepositoryException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      ((IndexConfurationImpl)indexConfiguration).setIndexRecoverService(contenProxy.getIndexigService()
         .getRecoverService());

      SearchServiceConfiguration configuration = new SearchServiceConfiguration();
      configuration.setIndexConfuguration(indexConfiguration);
      configuration.setContentReader(contenProxy);
      configuration.setNameConverter(nameConverter);
      configuration.setPathSplitter(new JcrPathSplitter(locationFactory));
      configuration.setNameConverter(new DoNothingNameConverter());
      luceneSearchService = new LuceneSearchService(configuration)
      {

         /**
          * @see org.xcmis.search.lucene.LuceneSearchService#addQueryableIndexStorageInterceptor(org.xcmis.search.content.interceptors.InterceptorChain)
          */
         @Override
         protected void addQueryableIndexStorageInterceptor(InterceptorChain interceptorChain)
            throws SearchServiceException
         {
            super.addQueryableIndexStorageInterceptor(interceptorChain);
            List<CommandInterceptor> indexStorage =
               interceptorChain.getInterceptorsWhichExtend(LuceneQueryableIndexStorage.class);
            StartableJcrIndexingService indexigService = contenProxy.getIndexigService();
            indexigService.initStorage(((LuceneQueryableIndexStorage)indexStorage.get(0)).getIndexDataManager());
            indexigService.start();
         }

      };

   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<Result> handleQuery(org.xcmis.spi.query.Query query) throws InvalidArgumentException,
      RepositoryException
   {
      try
      {
         InvocationContext invocationContext = new InvocationContext();
         invocationContext.setSchema(schema);
         invocationContext.setTableResolver(contenProxy.getIndexigService().getVirtualTableResolver());
         invocationContext.setNameConverter(nameConverter);
         invocationContext.setPathSplitter(new JcrPathSplitter(locationFactory));
         invocationContext.setNameConverter(new DoNothingNameConverter());
         luceneSearchService.setInvocationContext(invocationContext);

         Query qom = queryParser.parseQuery(query.getStatement());
         List<ScoredRow> result = luceneSearchService.execute(qom, Collections.EMPTY_MAP);
         //qom.setSearchAllVersions(query.isSearchAllVersions());

         return new QueryResultIterator(result, Visitors.getSelectorsReferencedBy(qom), qom);
      }

      catch (org.xcmis.search.InvalidQueryException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (javax.jcr.RepositoryException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return null;
   }
   private class DoNothingNameConverter implements NameConverter<String>{


      /**
       * @see org.xcmis.search.value.NameConverter#convertName(java.lang.Object)
       */
      @Override
      public String convertName(String name)
      {
         return name;
      }
      
   }
   private  class JcrPathSplitter implements PathSplitter<String>{
      private final LocationFactory locationFactory;
      
      /**
       * @param locationFactory
       */
      public JcrPathSplitter(LocationFactory locationFactory)
      {
         super();
         this.locationFactory = locationFactory;
      }

      /**
       * @see org.xcmis.search.value.PathSplitter#splitPath(java.lang.String)
       */
      @Override
      public String[] splitPath(String path)
      {
         String[] result = new String[0];
         try
         {
            QPath jcrPath = locationFactory.parseJCRPath(path).getInternalPath();
            QPathEntry[] pathEntries = jcrPath.getEntries();
            result = new String[pathEntries.length];
            for (int i = 0; i < pathEntries.length; i++)
            {
               result[i]= locationFactory.createJCRName(pathEntries[i]).getAsString();
            }
         }
         catch (javax.jcr.RepositoryException e)
         {
            
         }
         return result;
      }
      
   } 
   /**
    * 
    * ExtendedNodeTypeManager based schema
    *
    */
   private class NodeTypeShema implements Schema
   {
      private final NodeTypeDataManager nodeTypeDataManager;

      private final LocationFactory locationFactory;

      /**
       * @param nodeTypeDataManager
       * @param locationFactory 
       */
      public NodeTypeShema(NodeTypeDataManager nodeTypeDataManager, LocationFactory locationFactory)
      {
         super();
         this.nodeTypeDataManager = nodeTypeDataManager;
         this.locationFactory = locationFactory;
      }

      /**
       * @see org.xcmis.search.content.Schema#getTable(org.xcmis.search.model.source.SelectorName)
       */
      public Table getTable(SelectorName name)
      {

         NodeTypeData nt;
         try
         {
            nt = nodeTypeDataManager.getNodeType(locationFactory.parseJCRName(name.getName()).getInternalName());
            if (nt != null)
            {
               return new NodeTypeTable(nt, nodeTypeDataManager, locationFactory);
            }
         }
         catch (javax.jcr.RepositoryException e)
         {
         }
         return null;

      }

   }

   /**
    * 
    * NodeType based Table
    *
    */
   private class NodeTypeTable implements Table
   {

      private final NodeTypeData nodeType;

      private final NodeTypeDataManager nodeTypeDataManager;

      private final LocationFactory locationFactory;

      /**
       * @param nt
       * @param locationFactory 
       * @param nodeTypeDataManager 
       */
      public NodeTypeTable(NodeTypeData nt, NodeTypeDataManager nodeTypeDataManager, LocationFactory locationFactory)
      {
         super();
         this.nodeType = nt;
         this.nodeTypeDataManager = nodeTypeDataManager;
         this.locationFactory = locationFactory;
      }

      /**
       * @see org.xcmis.search.content.Schema.Table#getColumn(java.lang.String)
       */
      public org.xcmis.search.content.Schema.Column getColumn(String name)
      {
         try
         {
            InternalQName columntName = locationFactory.parseJCRName(name).getInternalName();
            final PropertyDefinitionDatas defs =
               nodeTypeDataManager.getPropertyDefinitions(columntName, nodeType.getName());
            if (defs != null)
            {
               return new PropertyDefinitionColumn(locationFactory.createJCRName(defs.getAnyDefinition().getName())
                  .getAsString());
            }
         }
         catch (javax.jcr.RepositoryException e)
         {

         }
         return null;
      }

      /**
       * @see org.xcmis.search.content.Schema.Table#getColumns()
       */
      public List<org.xcmis.search.content.Schema.Column> getColumns()
      {
         List<org.xcmis.search.content.Schema.Column> result = new ArrayList<org.xcmis.search.content.Schema.Column>();
         final PropertyDefinitionData[] defs = nodeTypeDataManager.getAllPropertyDefinitions(nodeType.getName());
         for (int i = 0; i < defs.length; i++)
         {
            try
            {
               result.add(new PropertyDefinitionColumn(locationFactory.createJCRName(defs[i].getName()).getAsString()));
            }
            catch (javax.jcr.RepositoryException e)
            {
               //TODO check ignore.
            }
         }
         return result;
      }

      /**
       * @see org.xcmis.search.content.Schema.Table#getColumnsByName()
       */
      public Map<String, org.xcmis.search.content.Schema.Column> getColumnsByName()
      {
         Map<String, org.xcmis.search.content.Schema.Column> result =
            new HashMap<String, org.xcmis.search.content.Schema.Column>();
         final PropertyDefinitionData[] defs = nodeTypeDataManager.getAllPropertyDefinitions(nodeType.getName());
         for (int i = 0; i < defs.length; i++)
         {
            try
            {
               String name = locationFactory.createJCRName(defs[i].getName()).getAsString();
               result.put(name, new PropertyDefinitionColumn(name));
            }
            catch (javax.jcr.RepositoryException e)
            {
               //TODO check ignore.
            }

         }
         return result;
      }

      /**
       * @see org.xcmis.search.content.Schema.Table#getName()
       */
      public SelectorName getName()
      {
         try
         {
            return new SelectorName(locationFactory.createJCRName(nodeType.getName()).getAsString());
         }
         catch (javax.jcr.RepositoryException e)
         {

         }
         return null;
      }
   }

   private class PropertyDefinitionColumn implements org.xcmis.search.content.Schema.Column
   {

      private final String name;

      /**
       * @param name
       */
      public PropertyDefinitionColumn(String name)
      {
         super();
         this.name = name;
      }

      /**
       * @see org.xcmis.search.content.Schema.Column#getName()
       */
      public String getName()
      {
         return name;
      }

      /**
       * @see org.xcmis.search.content.Schema.Column#getPropertyType()
       */
      public String getPropertyType()
      {
         return "String";
      }

      /**
       * @see org.xcmis.search.content.Schema.Column#isFullTextSearchable()
       */
      public boolean isFullTextSearchable()
      {
         return true;
      }

   }

   //
   //   private CmisQueryObjectModel createQOM(String statement) throws InvalidArgumentException
   //   {
   //      CmisQueryObjectModelFactory<Query> factory = indexingService.getQOMFactory();
   //      try
   //      {
   //         final CMISSQLLexer lexer = new CMISSQLLexer(new ANTLRStringStream(statement));
   //         final CommonTokenStream tokens = new CommonTokenStream(lexer);
   //         // process parsing
   //         final CMISSQLParser parser = new CMISSQLParser(tokens);
   //         final CMISSQLParser.query_return result = parser.query();
   //         // check exceptions
   //         if (lexer.hasExceptions())
   //         {
   //            throw new InvalidArgumentException(lexer.getExceptionMessage());
   //         }
   //         if (parser.hasExceptions())
   //         {
   //            throw new InvalidArgumentException(parser.getExceptionMessage());
   //         }
   //
   //         // process query build
   //         final CommonTree tree = (CommonTree)result.getTree();
   //         final CommonTreeNodeStream nodes = new CommonTreeNodeStream(tree);
   //         final CMISSQLTreeWalker treeWalker = new CMISSQLTreeWalker(nodes);
   //         CmisQueryObjectModel qom =
   //            treeWalker.query(factory, resolver, new JcrValueFactoryAdapter(indexingService.getValueFactory()));
   //         return qom;
   //      }
   //      catch (RecognitionException e)
   //      {
   //         throw new InvalidArgumentException(e.getLocalizedMessage(), e);
   //      }
   //   }

   class QueryResultIterator implements ItemsIterator<Result>
   {

      private final Iterator<ScoredRow> rows;

      private final Set<SelectorName> selectors;

      private final int size;

      private final Query qom;

      private Result next;

      public QueryResultIterator(List<ScoredRow> rows, Set<SelectorName> set, Query qom)
      {
         this.size = rows.size();
         this.rows = rows.iterator();
         this.selectors = set;
         this.qom = qom;
         fetchNext();
      }

      /**
       * {@inheritDoc}
       */
      public boolean hasNext()
      {
         return next != null;
      }

      /**
       * {@inheritDoc}
       */
      public Result next()
      {
         if (next == null)
         {
            throw new NoSuchElementException();
         }
         Result r = next;
         fetchNext();
         return r;
      }

      /**
       * {@inheritDoc}
       */
      public void remove()
      {
         throw new UnsupportedOperationException("remove");
      }

      /**
       * {@inheritDoc}
       */
      public long size()
      {
         return size;
      }

      /**
       * {@inheritDoc}
       */
      public void skip(long skip) throws NoSuchElementException
      {
         while (skip-- > 0)
         {
            next();
         }
      }

      /**
       * To fetch next <code>Result</code>.
       */
      protected void fetchNext()
      {
         next = null;
         while (next == null && rows.hasNext())
         {
            ScoredRow row = rows.next();
            for (SelectorName selectorName : selectors)
            {
               String objectId = row.getNodeIdentifer(selectorName.getName());
               List<String> properties = null;
               Score score = null;
               for (Column column : qom.getColumns())
               {
                  //TODO check
                  if (true)
                  {
                     score = new Score(column.getColumnName(), BigDecimal.valueOf(row.getScore()));
                  }
                  else
                  {
                     if (selectorName.getName().equals(column.getSelectorName()))
                     {
                        if (column.getPropertyName() != null)
                        {
                           if (properties == null)
                           {
                              properties = new ArrayList<String>();
                           }
                           properties.add(column.getPropertyName());
                        }
                     }
                  }
               }
               next = new ResultImpl(objectId, //
                  properties == null ? null : properties.toArray(new String[properties.size()]), //
                  score);
            }
         }
      }

   }

}
