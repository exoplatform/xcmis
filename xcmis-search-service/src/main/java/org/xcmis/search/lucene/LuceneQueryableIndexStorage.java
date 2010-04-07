/*
 * Copyright (C) 2010 eXo Platform SAS.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.xcmis.search.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.config.IndexConfiguration;
import org.xcmis.search.config.IndexConfigurationException;
import org.xcmis.search.config.SearchServiceConfiguration;
import org.xcmis.search.content.ContentEntry;
import org.xcmis.search.content.command.read.GetChildEntriesCommand;
import org.xcmis.search.content.command.read.GetContentEntryCommand;
import org.xcmis.search.content.interceptors.QueryableIndexStorage;
import org.xcmis.search.lucene.index.FieldNames;
import org.xcmis.search.lucene.index.IndexDataKeeper;
import org.xcmis.search.lucene.index.IndexException;
import org.xcmis.search.lucene.index.IndexRecoverService;
import org.xcmis.search.lucene.index.IndexRestoreService;
import org.xcmis.search.lucene.index.IndexTransactionException;
import org.xcmis.search.lucene.index.LuceneIndexTransaction;
import org.xcmis.search.lucene.index.LuceneIndexer;
import org.xcmis.search.lucene.index.StartableIndexingService;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Lucene persisted implementation of {@link QueryableIndexStorage}
 * 
 */
public class LuceneQueryableIndexStorage extends AbstractLuceneQueryableIndexStorage
{
   /**
    * Class logger.
    */
   private static final Log LOG = ExoLogger.getLogger(LuceneQueryableIndexStorage.class);

   private final StartableIndexingService indexDataManager;

   /**
    * @param indexConfuguration
    * @throws IndexException
    * @throws IndexConfigurationException
    * @throws org.xcmis.search.lucene.index.IndexException
    */
   public LuceneQueryableIndexStorage(SearchServiceConfiguration serviceConfuguration) throws IndexException
   {
      super(serviceConfuguration);
      this.indexDataManager =
         new StartableIndexingService(serviceConfuguration.getIndexConfuguration(), new LuceneRecoverService(this,
            nodeIndexer), new LuceneRestoreService(this, nodeIndexer, serviceConfuguration.getIndexConfuguration()));

   }

   /**
    * @see org.xcmis.search.content.interceptors.CommandInterceptor#start()
    */
   @Override
   public void start()
   {
      super.start();
      this.indexDataManager.start();
   }

   /**
    * @see org.xcmis.search.content.interceptors.CommandInterceptor#stop()
    */
   @Override
   public void stop()
   {
      super.stop();
      this.indexDataManager.stop();
   }

   /**
    * 
    * @see org.xcmis.search.lucene.AbstractLuceneQueryableIndexStorage#getIndexReader()
    */
   @Override
   protected IndexReader getIndexReader() throws IndexException
   {
      return indexDataManager.getIndexReader();
   }

   protected Document getDocument(String uuid, IndexReader reader) throws IndexException
   {

      try
      {

         if (reader != null)
         {
            final TermDocs termDocs = reader.termDocs(new Term(FieldNames.UUID, uuid));
            if (termDocs.next())
            {
               final Document document = reader.document(termDocs.doc());
               if (termDocs.next())
               {
                  throw new IndexException("More then one document found for uuid:" + uuid);
               }
               return document;
            }
         }
      }
      catch (final IOException e)
      {
         throw new IndexException(e.getLocalizedMessage(), e);
      }
      return null;
   }

   /**
    * @throws IndexTransactionException
    * @throws IndexException
    * @see org.xcmis.search.lucene.AbstractLuceneQueryableIndexStorage#save(org.xcmis.search.lucene.index.LuceneIndexTransaction)
    */
   @Override
   protected Object save(LuceneIndexTransaction indexTransaction) throws IndexException, IndexTransactionException
   {
      return indexDataManager.save(indexTransaction);
   }

   public static class LuceneRecoverService implements IndexRecoverService
   {
      /**
       * Class logger.
       */
      private static final Log LOG = ExoLogger.getLogger(LuceneRecoverService.class);

      private final LuceneQueryableIndexStorage indexStorage;

      private final LuceneIndexer nodeIndexer;

      /**
       * @param indexStorage
       * @param nodeIndexer
       */
      public LuceneRecoverService(LuceneQueryableIndexStorage indexStorage, LuceneIndexer nodeIndexer)
      {
         super();
         this.indexStorage = indexStorage;
         this.nodeIndexer = nodeIndexer;
      }

      /**
       */
      public void recover(Set<String> uuids) throws IndexException
      {
         IndexReader reader = indexStorage.getIndexReader();
         try
         {
            final HashMap<String, Document> addedDocuments = new HashMap<String, Document>();
            final HashSet<String> removedDocuments = new HashSet<String>();
            for (final String nodeUuid : uuids)
            {
               GetContentEntryCommand getCommand = new GetContentEntryCommand(nodeUuid);
               final ContentEntry contentEntry = (ContentEntry)indexStorage.invokeNextInterceptor(null, getCommand);
               if (contentEntry == null)
               {
                  if (indexStorage.getDocument(nodeUuid, reader) != null)
                  {
                     // item exist in index storage but doesn't exist in
                     // persistence storage
                     removedDocuments.add(nodeUuid);
                  }
               }
               else
               {

                  Document doc = nodeIndexer.createDocument(contentEntry);

                  if (indexStorage.getDocument(nodeUuid, reader) != null)
                  {
                     // out dated content
                     addedDocuments.put(nodeUuid, doc);
                     removedDocuments.add(nodeUuid);
                  }
                  else
                  {
                     // content desn't exist
                     addedDocuments.put(nodeUuid, doc);
                  }
               }
            }
            indexStorage.save(new LuceneIndexTransaction(addedDocuments, removedDocuments));
         }
         catch (Throwable e)
         {
            throw new IndexException(e.getLocalizedMessage(), e);
         }
         finally
         {
            try
            {
               if (reader != null)
               {
                  reader.close();
               }
            }
            catch (IOException e)
            {
               throw new IndexException(e.getLocalizedMessage(), e);
            }
         }
      }
   }

   public static class LuceneRestoreService implements IndexRestoreService
   {
      /**
       * Max documents count in buffer.
       */
      public static final int BUFFER_MAX_SIZE = 1000;

      private final LuceneQueryableIndexStorage indexStorage;

      private final IndexConfiguration indexConfiguration;

      private final LuceneIndexer nodeIndexer;

      /**
       * @param indexStorage
       * @param nodeIndexer
       * @param indexConfiguration
       */
      public LuceneRestoreService(LuceneQueryableIndexStorage indexStorage, LuceneIndexer nodeIndexer,
         IndexConfiguration indexConfiguration)
      {
         super();
         this.indexStorage = indexStorage;
         this.nodeIndexer = nodeIndexer;
         this.indexConfiguration = indexConfiguration;
      }

      /**
       * @see org.xcmis.search.lucene.index.IndexRestoreService#restoreIndex(org.xcmis.search.lucene.index.IndexDataKeeper)
       */
      public void restoreIndex(IndexDataKeeper<Document> indexDataKeeper) throws IndexException
      {
         Map<String, Document> documentBuffer = new HashMap<String, Document>();
         try
         {
            GetContentEntryCommand getCommand = new GetContentEntryCommand(indexConfiguration.getRootUuid());
            final ContentEntry rootEntry = (ContentEntry)indexStorage.invokeNextInterceptor(null, getCommand);
            if (rootEntry != null)
            {
               restoreBranch(rootEntry, documentBuffer);
            }
            else
            {
               LOG.warn("Root element with id " + indexConfiguration.getRootUuid() + " not found ");
            }
            if (documentBuffer.size() > 0)
            {
               flash(documentBuffer);
            }
         }
         catch (Throwable e)
         {
            throw new IndexException(e.getLocalizedMessage(), e);
         }
      }

      /**
       * Restore content of branch starting from branchUuid.
       * 
       * @param branchUuid
       *           - Uuid of root element of branch.
       * @param documentBuffer
       * @throws Throwable
       */
      private void restoreBranch(ContentEntry branchRoot, Map<String, Document> documentBuffer) throws Throwable
      {
         // add root.

         documentBuffer.put(branchRoot.getIdentifier(), nodeIndexer.createDocument(branchRoot));
         if (checkFlush(documentBuffer))
         {
            flash(documentBuffer);
         }

         // add childs
         GetChildEntriesCommand getChildCommand = new GetChildEntriesCommand(branchRoot.getIdentifier());
         Collection<ContentEntry> childEntries =
            (Collection<ContentEntry>)indexStorage.invokeNextInterceptor(null, getChildCommand);
         if (childEntries != null)
         {
            for (ContentEntry contentEntry : childEntries)
            {
               restoreBranch(contentEntry, documentBuffer);
            }
         }
         else
         {
            LOG.warn("Child elements for element with id " + branchRoot.getIdentifier() + " is not found ");
         }

      }

      /**
       * 
       * @param documentBuffer
       * @return true if we need to flush buffer.
       */
      private boolean checkFlush(Map<String, Document> documentBuffer)
      {
         return documentBuffer.size() >= BUFFER_MAX_SIZE;
      }

      @SuppressWarnings("unchecked")
      private void flash(Map<String, Document> documentBuffer) throws IndexTransactionException, IndexException
      {
         indexStorage.save(new LuceneIndexTransaction(documentBuffer, Collections.EMPTY_SET));
      }

   }

}
