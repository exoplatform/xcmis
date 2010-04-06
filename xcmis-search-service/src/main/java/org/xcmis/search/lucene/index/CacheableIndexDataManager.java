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

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.store.Directory;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.config.IndexConfiguration;
import org.xcmis.search.config.IndexConfigurationException;
import org.xcmis.search.lucene.index.merge.AggregatePolicy;
import org.xcmis.search.lucene.index.merge.DocumentCountAggregatePolicy;
import org.xcmis.search.lucene.index.merge.MaxCandidatsCountAggrigatePolicy;
import org.xcmis.search.lucene.index.merge.PendingAggregatePolicy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: CacheableIndexDataManager.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class CacheableIndexDataManager extends LocalIndexDataManagerProxy
{

   /**
    * Data keeper factory.
    */
   private final LuceneIndexDataKeeperFactory dataKeeperFactory;

   /**
    * 
    */
   private final AggregatePolicy inMemoryAggregationPolicy;

   /**
    * Class logger.
    */
   private static final Log LOG = ExoLogger.getLogger(CacheableIndexDataManager.class);

   /**
    * Index chains.
    */
   private final List<LuceneIndexDataManager> memoryChains;

   /**
    * 
    */
   private final PendingAggregatePolicy persistentAggregationPolicy;

   /**
    * The time this index was last flushed or a transaction was committed.
    */
   private volatile long lastFlushTime;

   /**
    * Task that is periodically called by {@link #FLUSH_TIMER} and checks if
    * index should be flushed.
    */
   private TimerTask flushTask;

   /**
    * Timer to schedule flushes of this index after some idle time.
    */
   private static final Timer FLUSH_TIMER = new Timer(true);

   private final static int DEFAULT_IDLE_TIME = 10 * 1000;

   /** 
    * If manager isStoped
    */
   private boolean isStoped = false;

   /**
    * @param queryHandlerEntry
    * @param dataKeeperFactory
    * @throws IndexConfigurationException
    * @throws IndexException
    */
   public CacheableIndexDataManager(final IndexConfiguration indexConfuguration) throws IndexException,
      IndexConfigurationException
   {
      super(indexConfuguration);

      dataKeeperFactory = new InMemoryIndexDataKeeperFactory();
      memoryChains = new ArrayList<LuceneIndexDataManager>();

      inMemoryAggregationPolicy = new MaxCandidatsCountAggrigatePolicy(new PendingAggregatePolicy());

      persistentAggregationPolicy = new PendingAggregatePolicy();
      persistentAggregationPolicy.setMaxDirSize(Integer.MAX_VALUE);
      persistentAggregationPolicy.setMinDirSize(1024 * 1024);
      persistentAggregationPolicy.setMaxDocuments4Dir(Integer.MAX_VALUE);
      persistentAggregationPolicy.setMinDocuments4Dir(100);
      persistentAggregationPolicy.setMinAggregateTime(1 * 1000);
      persistentAggregationPolicy.setMinModificationTime(3 * 1000);
      scheduleFlushTask();

   }

   @Override
   public IndexTransactionModificationReport aggregate(final Collection<LuceneIndexDataManager> indexes)
      throws IndexException, IndexTransactionException
   {
      // dump();
      synchronized (indexes)
      {
         if (indexes.size() > 2)
         {
            final Collection<LuceneIndexDataManager> candidats =
               inMemoryAggregationPolicy.findIndexDataManagerToAggrigate(indexes, 0, 0);
            // no candidates to merge
            if (candidats.size() > 1)
            {
               final LuceneIndexDataManager mergedChain = dataKeeperFactory.merge(candidats);
               for (final LuceneIndexDataManager luceneIndexDataManager : candidats)
               {
                  dataKeeperFactory.dispose(luceneIndexDataManager);
                  indexes.remove(luceneIndexDataManager);
               }
               indexes.add(mergedChain);
            }
         }
         final Collection<LuceneIndexDataManager> candidats2Save =
            persistentAggregationPolicy.findIndexDataManagerToAggrigate(indexes, 0, 0);
         if (candidats2Save.size() > 0)
         {
            super.aggregate(candidats2Save);

            for (final LuceneIndexDataManager luceneIndexDataManager : candidats2Save)
            {
               dataKeeperFactory.dispose(luceneIndexDataManager);
               ((TransactionableLuceneIndexDataManager)luceneIndexDataManager).getTransactionLog().removeLog();
               indexes.remove(luceneIndexDataManager);
            }
            lastFlushTime = System.currentTimeMillis();
         }
      }
      return null;
   }

   @Override
   public Directory getDirectory() throws IndexException
   {
      // TODO Auto-generated method stub
      return super.getDirectory();
   }

   @Override
   public long getDirectorySize(final boolean includeInherited)
   {
      long result = 0;
      if (memoryChains.size() != 0)
      {

         if (includeInherited)
         {
            result = super.getDirectorySize(true);
         }
         for (final LuceneIndexDataManager dm : memoryChains)
         {
            result += dm.getDirectorySize(includeInherited);
         }

      }
      return result;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Document getDocument(final String uuid) throws IndexException
   {
      Document doc = null;
      synchronized (memoryChains)
      {
         for (int i = 0; i < memoryChains.size(); i++)
         {
            doc = memoryChains.get(i).getDocument(uuid);
            if (doc != null)
            {
               break;
            }
         }
      }
      if (doc == null)
      {
         doc = super.getDocument(uuid);
      }
      return doc;
   }

   @Override
   public long getDocumentCount()
   {

      long result = super.getDocumentCount();
      for (final LuceneIndexDataManager dm : memoryChains)
      {
         result += dm.getDocumentCount();
      }

      return result;
   }

   /**
    * {@inheritDoc}
    * 
    * @throws IndexException
    */
   @Override
   public IndexReader getIndexReader() throws IndexException
   {

      IndexReader result = super.getIndexReader();
      synchronized (memoryChains)
      {
         if (memoryChains.size() > 0)
         {
            final List<IndexReader> readers = new ArrayList<IndexReader>(memoryChains.size());
            final Iterator<LuceneIndexDataManager> it = memoryChains.iterator();

            while (it.hasNext())
            {
               final LuceneIndexDataManager chain = it.next();

               final IndexReader indexReader = chain.getIndexReader();
               if (indexReader != null)
               {
                  readers.add(indexReader);
               }

            }
            if (result != null)
            {
               readers.add(result);
            }
            if (readers.size() > 1)
            {
               final IndexReader[] indexReaders = new IndexReader[readers.size()];
               result = new MultiReader(readers.toArray(indexReaders));
            }
            else if (readers.size() == 1)
            {
               result = readers.get(0);
            }
            else
            {
               throw new IndexReaderNotFoundException("No readers found");
            }
         }
      }
      if (result == null)
      {
         //TODO check this

         //throw new IndexReaderNotFoundException("No readers found");
      }
      return result;
   }

   /**
    * Cancel flush task and add new one
    */
   private void scheduleFlushTask()
   {
      // cancel task
      if (flushTask != null)
      {
         flushTask.cancel();
      }
      // clear canceled tasks
      FLUSH_TIMER.purge();
      // new flush task, cause canceled can't be re-used
      flushTask = new TimerTask()
      {
         public void run()
         {
            long idleTime = System.currentTimeMillis() - lastFlushTime;
            //10 sec
            if (idleTime > DEFAULT_IDLE_TIME)
            {
               if (memoryChains.size() > 0)
               {
                  synchronized (memoryChains)
                  {
                     try
                     {
                        flash();
                     }
                     catch (TransactionLogException e)
                     {
                        LOG.error(e.getLocalizedMessage(), e);
                     }
                     catch (IndexTransactionException e)
                     {
                        LOG.error(e.getLocalizedMessage(), e);
                     }
                     catch (IndexException e)
                     {
                        LOG.error(e.getLocalizedMessage(), e);
                     }
                  }
               }
            }

         }
      };
      FLUSH_TIMER.schedule(flushTask, 0, 1000);
      lastFlushTime = System.currentTimeMillis();
   }

   @Override
   public long getLastModifedTime()
   {
      return super.getLastModifedTime();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public IndexTransactionModificationReport save(IndexTransaction<Document> changes) throws IndexException,
      IndexTransactionException
   {
      // notify all chains about changes

      synchronized (memoryChains)
      {

         if (changes.hasModifacationsDocuments())
         {

            // reverse order
            changes = processModifed(changes);

         }
         // if transaction have less then DEFAULT_MAX_DOCUMENTS_4_DIR to add then
         // create memory index
         if (DocumentCountAggregatePolicy.DEFAULT_MAX_DOCUMENTS_4_DIR > changes.getAddedDocuments().size())
         {
            changes = changes.apply(processAdded(changes));
         }
         // if transaction have more then DEFAULT_MAX_DOCUMENTS_4_DIR to add or
         // some modification
         if (changes.hasAddedDocuments() || changes.hasModifacationsDocuments())
         {
            super.save(changes);
         }
      }
      aggregate(memoryChains);
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void stop()
   {
      try
      {
         if (memoryChains.size() > 0)
         {
            synchronized (memoryChains)
            {
               flash();
               isStoped = true;
            }
         }
         // cancel task
         if (flushTask != null)
         {
            flushTask.cancel();
         }
         // clear canceled tasks
         FLUSH_TIMER.purge();
         FLUSH_TIMER.cancel();
      }
      catch (final ConcurrentModificationException e)
      {
         e.printStackTrace();
      }
      catch (final IndexException e)
      {
         e.printStackTrace();
      }
      super.stop();
   }

   /**
    * @throws IndexException
    * @throws IndexTransactionException
    * @throws TransactionLogException
    */
   private void flash() throws IndexException, IndexTransactionException, TransactionLogException
   {
      if (!isStoped)
      {
         super.aggregate(memoryChains);
         for (final LuceneIndexDataManager luceneIndexDataManager : memoryChains)
         {
            dataKeeperFactory.dispose(luceneIndexDataManager);
            ((TransactionableLuceneIndexDataManager)luceneIndexDataManager).getTransactionLog().removeLog();

         }
         memoryChains.clear();
         lastFlushTime = System.currentTimeMillis();
      }
   }

   private void dump()
   {
      LOG.info("====" + memoryChains.size() + "=====");
      for (final LuceneIndexDataManager luceneIndexDataManager : memoryChains)
      {
         LOG.info(luceneIndexDataManager.getDirectorySize(false) + "\t\t" + luceneIndexDataManager.getDocumentCount()
            + "\t\t" + (System.currentTimeMillis() - luceneIndexDataManager.getLastModifedTime()) + " msec");
      }
   }

   /**
    * Process add
    * 
    * @param changes
    * @throws IndexException
    */
   private IndexTransactionModificationReportImpl processAdded(final IndexTransaction<Document> changes)
      throws IndexException
   {
      if (changes.getAddedDocuments().size() > 0)
      {
         final LuceneIndexDataManager indexDataKeeper = dataKeeperFactory.createNewIndexDataKeeper(changes);
         indexDataKeeper.start();
         synchronized (memoryChains)
         {
            memoryChains.add(indexDataKeeper);
         }
      }
      return new IndexTransactionModificationReportImpl(changes.getAddedDocuments().keySet(), new HashSet<String>(),
         new HashSet<String>());

   }

   /**
    * Process remove and update
    * 
    * @param changes
    * @return
    * @throws IndexException
    * @throws IndexTransactionException
    */
   private IndexTransaction<Document> processModifed(IndexTransaction<Document> changes) throws IndexException,
      IndexTransactionException
   {
      synchronized (memoryChains)
      {

         for (final Iterator<LuceneIndexDataManager> it = memoryChains.iterator(); it.hasNext();)
         {
            final LuceneIndexDataManager chain = it.next();
            final IndexTransactionModificationReport report = chain.save(changes);

            if (report.isModifed())
            {
               changes = changes.apply(report);
               if (chain.getDocumentCount() == 0)
               {
                  dataKeeperFactory.dispose(chain);
                  ((TransactionableLuceneIndexDataManager)chain).getTransactionLog().removeLog();
                  it.remove();
               }
            }
            if (!changes.hasModifacationsDocuments())
            {
               break;
            }

         }
      }
      return changes;
   }

}
