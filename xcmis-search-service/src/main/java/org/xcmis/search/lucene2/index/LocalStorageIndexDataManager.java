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
package org.xcmis.search.lucene2.index;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.Startable;
import org.xcmis.search.config.IndexConfigurationException;
import org.xcmis.search.config.IndexConfuguration;
import org.xcmis.search.lucene2.index.merge.IndexAggregator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: LocalStorageIndexDataManager.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class LocalStorageIndexDataManager implements LuceneIndexDataManager, IndexAggregator, Startable
{
   /**
     * 
     */
   private List<PersistedIndex> chains;

   /**
     * 
     */
   private final PersistentIndexDataKeeperFactory indexFactory;

   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(LocalStorageIndexDataManager.class);

   private final IndexConfuguration indexConfuguration;

   /**
    * @throws IndexConfigurationException
    * @throws IndexException
    * @throws IndexConfigurationException
    */
   public LocalStorageIndexDataManager(final IndexConfuguration indexConfuguration) throws IndexException,
      IndexConfigurationException
   {
      super();
      this.indexConfuguration = indexConfuguration;

      indexFactory = new PersistentIndexDataKeeperFactory(indexConfuguration);

   }

   public IndexTransactionModificationReport aggregate(final Collection<LuceneIndexDataManager> indexes)
      throws IndexException, IndexTransactionException
   {
      // TODO Auto-generated method stub
      if (chains.size() == 0)
      {
         chains.add((PersistedIndex)indexFactory.merge(indexes));
      }
      else
      {
         try
         {
            final PersistedIndex index = chains.get(0);
            final IndexWriter writer =
               new IndexWriter(index.getDirectory(), new StandardAnalyzer(), MaxFieldLength.UNLIMITED);
            final List<Directory> dirs = new ArrayList<Directory>();
            for (final LuceneIndexDataManager luceneIndexDataManager : indexes)
            {
               // TODO remove get reader
               luceneIndexDataManager.getIndexReader();
               dirs.add(luceneIndexDataManager.getDirectory());
            }
            final Directory[] dirsToMerge = new Directory[dirs.size()];
            writer.addIndexesNoOptimize(dirs.toArray(dirsToMerge));
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
      return null;
   }

   public Directory getDirectory() throws IndexException
   {
      if (chains.size() != 0)
      {
         return chains.get(0).getDirectory();
      }
      return null;
   }

   public long getDirectorySize(final boolean includeInherited)
   {
      return 0;
   }

   public Document getDocument(final String uuid) throws IndexException
   {
      Document doc = null;
      synchronized (chains)
      {
         for (int i = 0; i < chains.size(); i++)
         {
            doc = chains.get(i).getDocument(uuid);
            if (doc != null)
            {
               break;
            }
         }
      }
      return doc;
   }

   public synchronized long getDocumentCount()
   {
      long result = 0;
      for (final PersistedIndex index : chains)
      {
         result += index.getDocumentCount();
      }
      return result;
   }

   public IndexReader getIndexReader() throws IndexException
   {
      IndexReader result = null;
      if (chains.size() > 0)
      {
         synchronized (chains)
         {
            if (chains.size() > 0)
            {
               final List<IndexReader> readers = new ArrayList<IndexReader>(chains.size());
               final Iterator<PersistedIndex> it = chains.iterator();

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
                  throw new RuntimeException("No readers found");
               }
            }
         }
         if (result == null)
         {
            throw new RuntimeException("No readers found");
         }
      }
      return result;
   }

   public long getLastModifedTime()
   {
      return 0;
   }

   public boolean isStarted()
   {
      return false;
   }

   public boolean isStoped()
   {
      return false;
   }

   public IndexTransactionModificationReport save(IndexTransaction<Document> changes) throws IndexException,
      IndexTransactionException
   {
      // notify all chains about changes
      synchronized (chains)
      {

         if (changes.hasModifacationsDocuments())
         {

            // reverse order
            changes = processModifed(changes);

            if (changes.getRemovedDocuments().size() > 0)
            {
               log.error(changes.getRemovedDocuments().size());
            }

         }
         // take care about added modification
         processAdded(changes);

      }
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public void start()
   {
      try
      {
         chains = indexFactory.init();
      }
      catch (final IndexException e)
      {
         throw new RuntimeException(e.getLocalizedMessage(), e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void stop()
   {
      for (final PersistedIndex index : chains)
      {
         try
         {
            index.getDirectory().close();
         }
         catch (final IndexException e)
         {
            e.printStackTrace();
         }
         catch (final IOException e)
         {
            e.printStackTrace();
         }
      }

   }

   /**
    * Process add
    * 
    * @param changes
    * @throws IndexException
    * @throws IndexTransactionException
    */
   private void processAdded(final IndexTransaction<Document> changes) throws IndexException, IndexTransactionException
   {
      if (changes.getAddedDocuments().size() > 0)
      {
         synchronized (chains)
         {
            if (chains.size() == 0)
            {
               final LuceneIndexDataManager indexDataKeeper = indexFactory.createNewIndexDataKeeper(changes);
               indexDataKeeper.start();
               chains.add((PersistedIndex)indexDataKeeper);
            }
            else
            {
               final LuceneIndexDataManager indexDataKeeper = chains.get(0);
               indexDataKeeper.save(changes);
            }

         }
      }
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
      synchronized (chains)
      {

         for (final Iterator<PersistedIndex> it = chains.iterator(); it.hasNext();)
         {
            final LuceneIndexDataManager chain = it.next();
            final IndexTransactionModificationReport report = chain.save(changes);

            if (report.isModifed())
            {
               changes = changes.apply(report);
               if (chain.getDocumentCount() == 0)
               {
                  indexFactory.dispose(chain);
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
