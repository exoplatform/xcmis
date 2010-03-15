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


package org.xcmis.sp.jcr.exo.query.index;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocCollector;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.exoplatform.services.document.DocumentReaderService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.config.RepositoryEntry;
import org.exoplatform.services.jcr.config.WorkspaceEntry;
import org.exoplatform.services.jcr.core.NamespaceAccessor;
import org.exoplatform.services.jcr.core.nodetype.NodeTypeDataManager;
import org.exoplatform.services.jcr.dataflow.ItemDataConsumer;
import org.exoplatform.services.jcr.datamodel.InternalQName;
import org.exoplatform.services.jcr.datamodel.ItemData;
import org.exoplatform.services.jcr.datamodel.NodeData;
import org.exoplatform.services.jcr.datamodel.PropertyData;
import org.exoplatform.services.jcr.datamodel.QPath;
import org.exoplatform.services.jcr.datamodel.ValueData;
import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.exoplatform.services.jcr.impl.core.query.lucene.FieldNames;
import org.exoplatform.services.jcr.impl.core.value.NameValue;
import org.exoplatform.services.jcr.impl.core.value.PathValue;
import org.exoplatform.services.jcr.impl.core.value.ValueFactoryImpl;
import org.exoplatform.services.jcr.impl.dataflow.AbstractPersistedValueData;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;
import org.xcmis.search.NativeQueryBuilder;
import org.xcmis.search.ScoredNodesImpl;
import org.xcmis.search.SearchIndexingService;
import org.xcmis.search.SingleSourceNativeQuery;
import org.xcmis.search.SingleSourceNativeQueryImpl;
import org.xcmis.search.config.IndexConfurationImpl;
import org.xcmis.search.index.IndexException;
import org.xcmis.search.index.IndexTransaction;
import org.xcmis.search.index.IndexTransactionException;
import org.xcmis.search.index.IndexTransactionModificationReport;
import org.xcmis.search.lucene.index.TransactionableIndexDataManager;
import org.xcmis.search.lucene.search.UUIDFieldSelector;
import org.xcmis.search.lucene.search.visitor.DocumentMatcherFactory;
import org.xcmis.search.result.ScoredRow;
import org.xcmis.sp.jcr.exo.RepositoriesManagerImpl;
import org.xcmis.sp.jcr.exo.RepositoryImpl;
import org.xcmis.sp.jcr.exo.query.lucene.CmisLuceneNativeQueryBuilder;
import org.xcmis.sp.jcr.exo.query.lucene.CmisVirtualTableResolver;
import org.xcmis.sp.jcr.exo.query.qom.CmisQueryObjectModelFactory;
import org.xcmis.sp.jcr.exo.query.qom.DocumentMatcherFactoryImpl;
import org.xcmis.sp.jcr.exo.query.result.JcrResultSorterFactory;
import org.xcmis.spi.RepositoriesManager;
import org.xcmis.spi.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.qom.Ordering;

/**
 * @author <a href="mailto:foo@bar.org">Foo Bar</a>
 * @version $Id: exo-jboss-codetemplates.xml 34027 2009-07-15 23:26:43Z
 *          aheritier $
 */
public class LuceneIndexingService implements SearchIndexingService<Query>, Startable
{

   /** The document matcher factory. */
   private final DocumentMatcherFactory documentMatcherFactory;

   /** The index data manager. */
   private final TransactionableIndexDataManager indexDataManager;

   /** The item data consumer. */
   private final ItemDataConsumer itemDataConsumer;

   /** The location factory. */
   private final LocationFactory locationFactory;

   /** Repository service used to get repository. */
   private final RepositoriesManager cmisRepositoriesManager;

   /** Associated repository id. */
   private final String repositoryId;

   /** Class logger. */
   private static final Log LOG = ExoLogger.getLogger(LuceneIndexingService.class);

   /** The namespace accessor. */
   private final NamespaceAccessor namespaceAccessor;

   /** The node type data manager. */
   private final NodeTypeDataManager nodeTypeDataManager;

   /** The recover service. */
   private final JcrIndexRecoverService recoverService;

   /**
    * The Constructor.
    * 
    * @param repositoryEntry the repository entry
    * @param workspaceEntry the workspace entry
    * @param cmisRepositoriesManager the cmis repositories manager
    * @param itemDataConsumer the item data consumer
    * @param namespaceAccessor the namespace accessor
    * @param nodeTypeDataManager the node type data manager
    * @param extractor the extractor
    * 
    * @throws RepositoryConfigurationException the repository configuration exception
    * @throws RepositoryException if any errors in CMIS repository occurs
    */
   public LuceneIndexingService(final RepositoryEntry repositoryEntry, final WorkspaceEntry workspaceEntry,
      final RepositoriesManager cmisRepositoriesManager, final ItemDataConsumer itemDataConsumer,
      final NamespaceAccessor namespaceAccessor, final NodeTypeDataManager nodeTypeDataManager,
      final DocumentReaderService extractor) throws RepositoryConfigurationException, RepositoryException
   {

      this.itemDataConsumer = itemDataConsumer;
      this.namespaceAccessor = namespaceAccessor;
      this.nodeTypeDataManager = nodeTypeDataManager;
      this.locationFactory = new LocationFactory(namespaceAccessor);
      this.recoverService = new JcrIndexRecoverService(itemDataConsumer, namespaceAccessor, extractor, this);
      this.cmisRepositoriesManager = cmisRepositoriesManager;

      RepositoryImpl repo =
         (RepositoryImpl)((RepositoriesManagerImpl)cmisRepositoriesManager).getRepository(repositoryEntry.getName(),
            workspaceEntry.getName());

      repositoryId = repo.getId();

      IndexConfurationImpl indexConfuguration = new IndexConfurationImpl();
      indexConfuguration.setIndexDir(repo.getRepositoryConfiguration().getIndexConfiguration().getIndexPath());

      indexDataManager = new TransactionableIndexDataManager(indexConfuguration, recoverService);

      documentMatcherFactory = new DocumentMatcherFactoryImpl();
   }

   /**
    * {@inheritDoc}
    */
   public boolean documentExists(final String uuid)
   {
      try
      {
         return indexDataManager.getDocument(uuid) != null;
      }
      catch (final IndexException e)
      {
         LOG.error(e.getLocalizedMessage(), e);
      }
      return false;
   }

   /**
    * Get lucene Document by uuid.
    * 
    * @param uuid - string id
    * @return lucene Document
    * @throws IndexException if index is invalid
    */
   public Document getDocument(final String uuid) throws IndexException
   {
      return indexDataManager.getDocument(uuid);
   }

   /**
    * {@inheritDoc}
    */
   public Set<String> getFieldNames() throws IndexException
   {
      final Set<String> fildsSet = new HashSet<String>();
      @SuppressWarnings("unchecked")
      final Collection fields = indexDataManager.getIndexReader().getFieldNames(IndexReader.FieldOption.ALL);
      for (final Object field : fields)
      {
         fildsSet.add((String)field);
      }
      return fildsSet;
   }

   /**
    * Return set of uuid of nodes. Contains in names prefixes mapped to the given
    * uri.
    * 
    * @param uri the uri
    * @return the nodes set by uri
    * 
    * @throws RepositoryException if any errors in CMIS repository occurs
    */
   public Set<String> getNodesByUri(final String uri) throws RepositoryException
   {
      Set<String> result;
      final int defaultClauseCount = BooleanQuery.getMaxClauseCount();
      try
      {
         // final LocationFactory locationFactory = new LocationFactory(this);
         final ValueFactoryImpl valueFactory = new ValueFactoryImpl(locationFactory);
         BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
         BooleanQuery query = new BooleanQuery();

         final String prefix = namespaceAccessor.getNamespacePrefixByURI(uri);
         query.add(new WildcardQuery(new Term(FieldNames.LABEL, prefix + ":*")), Occur.SHOULD);
         // name of the property
         query.add(new WildcardQuery(new Term(FieldNames.PROPERTIES_SET, prefix + ":*")), Occur.SHOULD);

         result = getNodes(query);

         // value of the property
         try
         {
            final Set<String> props = getFieldNames();
            query = new BooleanQuery();
            for (final String fieldName : props)
            {
               if (!FieldNames.PROPERTIES_SET.equals(fieldName))
               {
                  query.add(new WildcardQuery(new Term(fieldName, "*" + prefix + ":*")), Occur.SHOULD);
               }
            }
         }
         catch (final IndexException e)
         {
            throw new RepositoryException(e.getLocalizedMessage(), e);
         }

         final Set<String> propSet = getNodes(query);
         // Manually check property values;
         for (final String uuid : propSet)
         {
            if (isPrefixMatch(valueFactory, uuid, prefix))
            {
               result.add(uuid);
            }
         }
      }
      finally
      {
         BooleanQuery.setMaxClauseCount(defaultClauseCount);
      }
      return result;
   }

   /**
    * {@inheritDoc}
    */
   public CmisQueryObjectModelFactory<Query> getQOMFactory()
   {
      return new CmisQueryObjectModelFactory<Query>(this, locationFactory);
   }

   /**
    * {@inheritDoc}
    */
   public NativeQueryBuilder<Query> getQueryBuilder(final String language) throws IndexException
   {
      try
      {
         JcrResultSorterFactory resultSorterFactory = new JcrResultSorterFactory(itemDataConsumer, namespaceAccessor);
         return new CmisLuceneNativeQueryBuilder(this, resultSorterFactory, namespaceAccessor,
            getVirtualTableResolver(), documentMatcherFactory);
      }
      catch (RepositoryException e)
      {
         // TODO change method signature or wrap with cmis.RepositoryException
         throw new IndexException(e.getLocalizedMessage(), e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public IndexTransactionModificationReport save(final IndexTransaction<Document> changes) throws IndexException
   {
      try
      {
         return indexDataManager.save(changes);
      }
      catch (final IndexTransactionException e)
      {
         throw new IndexException(e.getLocalizedMessage(), e.getCause());
      }
   }

   /**
    * {@inheritDoc}
    */
   public List<ScoredRow> search(final SingleSourceNativeQuery<Query> nativeQuery) throws IndexException
   {
      List<ScoredRow> resultNodes = new ArrayList<ScoredRow>();
      // Open writer

      IndexSearcher searcher = null;
      try
      {
         // get result
         searcher = new org.apache.lucene.search.IndexSearcher(indexDataManager.getIndexReader());
         // Hits hits = searcher.search(nativeQuery.getQuery());
         final TopDocCollector collector = new TopDocCollector(10000);

         searcher.search(nativeQuery.getQuery(), collector);
         final TopDocs docs = collector.topDocs();
         resultNodes = new LinkedList<ScoredRow>();
         for (int i = 0; i < docs.totalHits; i++)
         {
            // get identifiers
            final Document doc = searcher.doc(docs.scoreDocs[i].doc, new UUIDFieldSelector());
            final String id = doc.get(FieldNames.UUID);
            resultNodes.add(new ScoredNodesImpl(nativeQuery.getSelectorName(), id, docs.scoreDocs[i].score));
         }
      }
      catch (final CorruptIndexException e)
      {
         throw new IndexException(e.getLocalizedMessage(), e);
      }
      catch (final IOException e)
      {
         throw new IndexException(e.getLocalizedMessage(), e);
      }
      finally
      {
         try
         {
            searcher.close();
         }
         catch (final IOException e)
         {
            throw new IndexException(e.getLocalizedMessage(), e);
         }
      }
      return resultNodes;
   }

   /**
    * {@inheritDoc}
    */
   public void start()
   {
      indexDataManager.start();
   }

   /**
    * {@inheritDoc}
    */
   public void stop()
   {
      indexDataManager.stop();
   }

   /**
    * Gets the nodes.
    * 
    * @param query the query
    * @return the nodes
    * @throws RepositoryException if any errors in CMIS repository occurs
    */
   private Set<String> getNodes(final Query query) throws RepositoryException
   {
      final SingleSourceNativeQuery<Query> nativeQuery =
         new SingleSourceNativeQueryImpl<Query>(query, "nt", new Ordering[]{}, null, null, 0, 0);
      final List<ScoredRow> hits = search(nativeQuery);
      final Set<String> result = new HashSet<String>(hits.size());
      for (final ScoredRow scoredRow : hits)
      {
         result.add(scoredRow.getNodeIdentifer(nativeQuery.getSelectorName()));
      }
      return result;
   }

   /**
    * Checks if is prefix match.
    * 
    * @param value the value
    * @param prefix the prefix
    * @return true, if is prefix match
    * @throws RepositoryException the repository exception
    */
   private boolean isPrefixMatch(final InternalQName value, final String prefix) throws RepositoryException
   {
      return value.getNamespace().equals(namespaceAccessor.getNamespaceURIByPrefix(prefix));
   }

   /**
    * Checks if is prefix match.
    * 
    * @param value the value
    * @param prefix the prefix
    * @return true, if is prefix match
    * @throws RepositoryException the repository exception
    */
   private boolean isPrefixMatch(final QPath value, final String prefix) throws RepositoryException
   {
      for (int i = 0; i < value.getEntries().length; i++)
      {
         if (isPrefixMatch(value.getEntries()[i], prefix))
         {
            return true;
         }
      }
      return false;
   }

   /**
    * Checks if is prefix match.
    * 
    * @param valueFactory the value factory
    * @param uuid the uuid
    * @param prefix the prefix
    * @return true, if checks if is prefix match
    * @throws RepositoryException if any errors in CMIS repository occurs
    */
   private boolean isPrefixMatch(final ValueFactoryImpl valueFactory, final String uuid, final String prefix)
      throws RepositoryException
   {

      final ItemData node = itemDataConsumer.getItemData(uuid);
      if (node != null && node.isNode())
      {
         final List<PropertyData> props = itemDataConsumer.getChildPropertiesData((NodeData)node);
         for (final PropertyData propertyData : props)
         {
            if (propertyData.getType() == PropertyType.PATH || propertyData.getType() == PropertyType.NAME)
            {
               for (final ValueData vdata : propertyData.getValues())
               {
                  final Value val =
                     valueFactory.loadValue(((AbstractPersistedValueData)vdata).createTransientCopy(), propertyData
                        .getType());
                  if (propertyData.getType() == PropertyType.PATH)
                  {
                     if (isPrefixMatch(((PathValue)val).getQPath(), prefix))
                     {
                        return true;
                     }
                  }
                  else if (propertyData.getType() == PropertyType.NAME)
                  {
                     if (isPrefixMatch(((NameValue)val).getQName(), prefix))
                     {
                        return true;
                     }
                  }
               }
            }
         }
      }
      return false;
   }

   /**
    * Gets the document count.
    * 
    * @return number of documents.
    * 
    */
   protected long getDocumentCount()
   {
      return indexDataManager.getDocumentCount();
   }

   /**
    * Create new CmisVirtualTableResolver with current RepositoryImpl state.
    * 
    * @return CmisVirtualTableResolver
    * @throws javax.jcr.RepositoryException on CmisVirtualTableResolver
    *           constructor
    */
   protected CmisVirtualTableResolver getVirtualTableResolver() throws RepositoryException
   {
      Repository repo = cmisRepositoriesManager.getRepository(repositoryId);
      return new CmisVirtualTableResolver(this.nodeTypeDataManager, this.namespaceAccessor, repo);
   }

   protected void softCleanIndex() throws IndexException
   {
      //      if (indexDataManager.getDocumentCount() > 0)
      //      {
      //         final Directory dir = indexDataManager.getDirectory();
      //         if (dir != null)
      //         {
      //            synchronized (dir)
      //            {
      //               try
      //               {
      //                  final IndexWriter writer =
      //                     new IndexWriter(indexDataManager.getDirectory(), new StandardAnalyzer(), MaxFieldLength.UNLIMITED);
      //                  writer.deleteDocuments(new MatchAllDocsQuery());
      //                  writer.commit();
      //                  writer.optimize();
      //                  writer.close();
      //               }
      //               catch (final CorruptIndexException e)
      //               {
      //                  throw new IndexException(e.getLocalizedMessage(), e);
      //               }
      //               catch (final LockObtainFailedException e)
      //               {
      //                  throw new IndexException(e.getLocalizedMessage(), e);
      //               }
      //               catch (final IOException e)
      //               {
      //                  throw new IndexException(e.getLocalizedMessage(), e);
      //               }
      //            }
      //         }
      //      }
   }

}
