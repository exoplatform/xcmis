/*
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.xcmis.search.lucene.index;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.index.IndexDataKeeper;
import org.xcmis.search.index.IndexException;
import org.xcmis.search.index.IndexTransaction;
import org.xcmis.search.index.LuceneIndexDataManager;
import org.xcmis.search.index.TransactionLog;
import org.xcmis.search.index.TransactionLogException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: InMemoryIndexDataKeeperFactory.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class InMemoryIndexDataKeeperFactory extends LuceneIndexDataKeeperFactory
{
   /**
    * Class logger.
    */

   private static final Log LOG = ExoLogger.getLogger(InMemoryIndexDataKeeperFactory.class);

   /**
    * {@inheritDoc}
    * 
    * @throws IndexException
    */
   public LuceneIndexDataManager createNewIndexDataKeeper(final IndexTransaction<Document> changes)
      throws IndexException
   {
      if (!(changes instanceof LoggedIndexTransactionImpl))
      {
         throw new IndexException("Fail to create in memory storage for not loged transaction");
      }
      return new ReducibleInMemoryIndexDataKeeper((LoggedIndexTransactionImpl)changes);
   }

   /**
    * {@inheritDoc}
    */

   public LuceneIndexDataManager merge(final Collection<LuceneIndexDataManager> chains) throws IndexException
   {
      final List<TransactionLog> transactionsLogs = new ArrayList<TransactionLog>();
      final List<Directory> mergeDirectorys = new ArrayList<Directory>();
      final Map<String, Document> documentsBuffer = new HashMap<String, Document>();
      final Map<String, Document> pendingBuffer = new HashMap<String, Document>();

      for (final IndexDataKeeper<Document> indexDataKeeper : chains)
      {
         final ReducibleInMemoryIndexDataKeeper reducibleInMemoryIndexDataKeeper =
            (ReducibleInMemoryIndexDataKeeper)indexDataKeeper;

         if (reducibleInMemoryIndexDataKeeper.getDocumentCount() > 0)
         {

            final RAMDirectory directory = (RAMDirectory)reducibleInMemoryIndexDataKeeper.getDirectory();
            if (directory.sizeInBytes() > 0)
            {
               mergeDirectorys.add(directory);
            }
            pendingBuffer.putAll(reducibleInMemoryIndexDataKeeper.getPendingDocumentsBuffer());
            documentsBuffer.putAll(reducibleInMemoryIndexDataKeeper.getDocumentsBuffer());
            transactionsLogs.add(reducibleInMemoryIndexDataKeeper.getTransactionLog());
         }
      }
      LuceneIndexDataManager reducibleInMemoryIndexDataKeeper = null;
      try
      {
         RAMDirectory newDirectory = null;

         if (mergeDirectorys.size() > 0)
         {

            newDirectory = new RAMDirectory();
            final IndexWriter newWriter =
               new IndexWriter(newDirectory, new StandardAnalyzer(), MaxFieldLength.UNLIMITED);
            final Directory[] dirsToMerge = new Directory[mergeDirectorys.size()];
            newWriter.addIndexesNoOptimize(mergeDirectorys.toArray(dirsToMerge));
            newWriter.optimize();
            newWriter.close();

            //
         }
         else
         {
            newDirectory = new RAMDirectory();
         }
         reducibleInMemoryIndexDataKeeper =
            new ReducibleInMemoryIndexDataKeeper(newDirectory, documentsBuffer, pendingBuffer,
               new CompositeTransactionLog(transactionsLogs));

      }
      catch (final IOException e)
      {
         throw new IndexException(e.getLocalizedMessage(), e);
      }
      catch (final TransactionLogException e)
      {
         throw new IndexException(e.getLocalizedMessage(), e);
      }

      return reducibleInMemoryIndexDataKeeper;
   }

}
