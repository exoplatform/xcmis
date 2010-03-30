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

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.exoplatform.services.document.DocumentReaderService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.config.RepositoryEntry;
import org.exoplatform.services.jcr.config.WorkspaceEntry;
import org.exoplatform.services.jcr.core.NamespaceAccessor;
import org.exoplatform.services.jcr.core.nodetype.NodeTypeDataManager;
import org.exoplatform.services.jcr.dataflow.ItemDataConsumer;
import org.exoplatform.services.jcr.datamodel.InternalQName;
import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;
import org.xcmis.search.lucene.LuceneQueryableIndexStorage;
import org.xcmis.search.lucene.index.IndexException;
import org.xcmis.search.lucene.index.IndexTransaction;
import org.xcmis.search.lucene.index.IndexTransactionException;
import org.xcmis.search.lucene.index.IndexTransactionModificationReport;
import org.xcmis.search.lucene.index.TransactionableIndexDataManager;
import org.xcmis.sp.jcr.exo.RepositoriesManagerImpl;
import org.xcmis.sp.jcr.exo.RepositoryImpl;
import org.xcmis.sp.jcr.exo.query.lucene.CmisVirtualTableResolver;
import org.xcmis.spi.RepositoriesManager;
import org.xcmis.spi.Repository;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.RepositoryException;

/**
 * @author <a href="mailto:foo@bar.org">Foo Bar</a>
 * @version $Id: exo-jboss-codetemplates.xml 34027 2009-07-15 23:26:43Z
 *          aheritier $
 */
public class LuceneIndexingService implements Startable
{

   //   /** The document matcher factory. */
   //   private final DocumentMatcherFactory documentMatcherFactory;

   /** The index data manager. */
   private TransactionableIndexDataManager indexDataManager;

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

   private LuceneQueryableIndexStorage storage;

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
    * @param contentProxy 
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

      //      IndexConfurationImpl indexConfuguration = new IndexConfurationImpl();
      //      indexConfuguration.setIndexDir(repo.getRepositoryConfiguration().getIndexConfiguration().getIndexPath());

      indexDataManager = null;//new TransactionableIndexDataManager(indexConfuguration, recoverService);

      //documentMatcherFactory = new DocumentMatcherFactoryImpl();
   }

   /**
    * @return the recoverService
    */
   public JcrIndexRecoverService getRecoverService()
   {
      return recoverService;
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
      catch (IndexException e)
      {
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
    * {@inheritDoc}
    */
   public IndexTransactionModificationReport save(final IndexTransaction<Document> changes) throws IndexException
   {
      try
      {
         return indexDataManager.save(changes);
      }
      catch (IndexTransactionException e)
      {
         throw new IndexException(e.getLocalizedMessage(), e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void start()
   {
      indexDataManager.start();
   }

   /**
    * Initialize storage.
    * @param storage
    */
   public void initStorage(TransactionableIndexDataManager indexDataManager)
   {
      this.indexDataManager = indexDataManager;
   }

   /**
    * {@inheritDoc}
    */
   public void stop()
   {
      indexDataManager.stop();
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
   public CmisVirtualTableResolver getVirtualTableResolver() throws RepositoryException
   {
      Repository repo = cmisRepositoriesManager.getRepository(repositoryId);
      return new CmisVirtualTableResolver(this.nodeTypeDataManager, locationFactory, repo);
   }

   protected void softCleanIndex() throws IndexException
   {
      if (indexDataManager.getDocumentCount() > 0)
      {
         final Directory dir = indexDataManager.getDirectory();
         if (dir != null)
         {
            synchronized (dir)
            {
               try
               {
                  final IndexWriter writer =
                     new IndexWriter(indexDataManager.getDirectory(), new StandardAnalyzer(), MaxFieldLength.UNLIMITED);
                  writer.deleteDocuments(new MatchAllDocsQuery());
                  writer.commit();
                  writer.optimize();
                  writer.close();
               }
               catch (final CorruptIndexException e)
               {
                  throw new IndexException(e.getLocalizedMessage(), e);
               }
               catch (final LockObtainFailedException e)
               {
                  throw new IndexException(e.getLocalizedMessage(), e);
               }
               catch (final IOException e)
               {
                  throw new IndexException(e.getLocalizedMessage(), e);
               }
            }
         }
      }
   }

}
