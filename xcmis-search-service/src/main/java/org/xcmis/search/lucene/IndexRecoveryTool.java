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
package org.xcmis.search.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.config.IndexConfiguration;
import org.xcmis.search.content.ContentEntry;
import org.xcmis.search.content.command.read.GetChildEntriesCommand;
import org.xcmis.search.content.command.read.GetContentEntryCommand;
import org.xcmis.search.content.command.read.GetUnfiledEntriesCommand;
import org.xcmis.search.lucene.index.IndexException;
import org.xcmis.search.lucene.index.IndexTransactionException;
import org.xcmis.search.lucene.index.LuceneIndexTransaction;
import org.xcmis.search.lucene.index.LuceneIndexer;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * Tools for index recovering. 
 */
public class IndexRecoveryTool
{

   /**
    * Class logger.
    */
   private static final Log LOG = ExoLogger.getLogger(IndexRecoveryTool.class);

   /**
    * Max documents count in buffer.
    */
   public static final int BUFFER_MAX_SIZE = 1000;

   /**
    * Convert {@link ContentEntry} to {@link Document}
    */
   private final LuceneIndexer nodeIndexer;

   private final LuceneQueryableIndexStorage indexStorage;

   /**
    * Configuration of index
    */
   private final IndexConfiguration indexConfiguration;

   /**
    * @param indexStorage
    * @param nodeIndexer
    * @param indexConfiguration
    */
   public IndexRecoveryTool(LuceneQueryableIndexStorage indexStorage, LuceneIndexer nodeIndexer,
      IndexConfiguration indexConfiguration)
   {
      super();
      this.indexStorage = indexStorage;
      this.nodeIndexer = nodeIndexer;
      this.indexConfiguration = indexConfiguration;
   }

   /**
    * Refresh the index of documents returned by 'uuids' iterator.
    * @param uuids
    * @throws IndexException
    */
   public void recover(Iterator<String> uuids) throws IndexException
   {
      IndexReader reader = indexStorage.getIndexReader();
      try
      {
         final HashMap<String, Document> addedDocuments = new HashMap<String, Document>();
         final HashSet<String> removedDocuments = new HashSet<String>();
         while (uuids.hasNext())
         {
            String nodeUuid = uuids.next();

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
            //flash if changes more when BUFFER_MAX_SIZE 
            if (checkFlush(addedDocuments, removedDocuments))
            {
               flash(addedDocuments, removedDocuments);
            }
         }
         //if some changes left
         if (addedDocuments.size() + removedDocuments.size() >= 0)
         {
            indexStorage.save(new LuceneIndexTransaction(addedDocuments, removedDocuments));
         }
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

   /**
    * Recover all content.
    * @throws IndexException
    */
   public void recoverAll() throws IndexException
   {
      Map<String, Document> documentBuffer = new HashMap<String, Document>();
      final HashSet<String> removedDocuments = new HashSet<String>();
      try
      {
         GetContentEntryCommand getCommand = new GetContentEntryCommand(indexConfiguration.getRootUuid());
         final ContentEntry rootEntry = (ContentEntry)indexStorage.invokeNextInterceptor(null, getCommand);
         if (rootEntry != null)
         {
            restoreBranch(rootEntry, documentBuffer, removedDocuments);
         }
         else
         {
            LOG.warn("Root element with id " + indexConfiguration.getRootUuid() + " not found ");
         }
         if (documentBuffer.size() > 0)
         {
            flash(documentBuffer, removedDocuments);
         }
         //recover unfiled documents.
         GetUnfiledEntriesCommand getUnfiledEntriesCommand = new GetUnfiledEntriesCommand();
         Iterator<String> uuids = (Iterator<String>)indexStorage.invokeNextInterceptor(null, getUnfiledEntriesCommand);
         if (uuids != null)
         {
            recover(uuids);
         }

      }
      catch (Throwable e)
      {
         throw new IndexException(e.getLocalizedMessage(), e);
      }
   }

   /**
    * 
    * @param documentBuffer
    * @return true if we need to flush buffer.
    */
   private boolean checkFlush(Map<String, Document> documentBuffer, HashSet<String> removedDocuments)
   {
      return documentBuffer.size() + removedDocuments.size() >= BUFFER_MAX_SIZE;
   }

   /**
    * Flash changes to the index and  cleans the lists.
    * @param documentBuffer
    * @param removedDocuments
    * @throws IndexTransactionException
    * @throws IndexException
    */
   private void flash(Map<String, Document> documentBuffer, HashSet<String> removedDocuments)
      throws IndexTransactionException, IndexException
   {
      indexStorage.save(new LuceneIndexTransaction(new HashMap<String, Document>(documentBuffer), new HashSet<String>(
         removedDocuments)));
      documentBuffer.clear();
      removedDocuments.clear();
   }

   /**
    * Restore content of branch starting from branchUuid.
    * 
    * @param branchUuid
    *           - Uuid of root element of branch.
    * @param documentBuffer
    * @param removedDocuments 
    * @throws Throwable
    */
   private void restoreBranch(ContentEntry branchRoot, Map<String, Document> documentBuffer,
      HashSet<String> removedDocuments) throws Throwable
   {
      // add root.

      documentBuffer.put(branchRoot.getIdentifier(), nodeIndexer.createDocument(branchRoot));
      if (checkFlush(documentBuffer, removedDocuments))
      {
         flash(documentBuffer, removedDocuments);
      }

      // add childs
      GetChildEntriesCommand getChildCommand = new GetChildEntriesCommand(branchRoot.getIdentifier());
      Collection<ContentEntry> childEntries =
         (Collection<ContentEntry>)indexStorage.invokeNextInterceptor(null, getChildCommand);
      if (childEntries != null)
      {
         for (ContentEntry contentEntry : childEntries)
         {
            restoreBranch(contentEntry, documentBuffer, removedDocuments);
         }
      }
      else
      {
         LOG.warn("Child elements for element with id " + branchRoot.getIdentifier() + " is not found ");
      }

   }
}
