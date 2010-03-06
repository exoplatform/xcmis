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
import org.apache.lucene.store.FSDirectory;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.config.IndexConfigurationException;
import org.xcmis.search.config.IndexConfuguration;
import org.xcmis.search.index.IndexException;
import org.xcmis.search.index.IndexInfo;
import org.xcmis.search.index.IndexTransaction;
import org.xcmis.search.index.LuceneIndexDataManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: PersistentIndexDataKeeperFactory.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class PersistentIndexDataKeeperFactory extends LuceneIndexDataKeeperFactory
{

   /**
    * Class logger.
    */
   private static final Log LOG = ExoLogger.getLogger(PersistentIndexDataKeeperFactory.class);

   private final File indexDir;

   private final IndexInfo indexNames;

   private final IndexConfuguration indexConfuguration;

   /**
    * @throws IndexException
    * @throws IndexConfigurationException
    */
   public PersistentIndexDataKeeperFactory(final IndexConfuguration indexConfuguration) throws IndexException,
      IndexConfigurationException
   {
      super();
      this.indexConfuguration = indexConfuguration;
      indexDir = new File(indexConfuguration.getIndexDir());

      if (indexDir.isFile())
      {
         throw new IndexException("Fail to create directory : " + indexDir.getAbsolutePath() + " file already exists.");
      }

      if (!indexDir.exists() && !indexDir.mkdirs())
      {
         throw new IndexException("Fail to create directory : " + indexDir.getAbsolutePath());
      }

      indexNames = new IndexInfo("indexes");

   }

   /**
    * {@inheritDoc}
    */
   public LuceneIndexDataManager createNewIndexDataKeeper(final IndexTransaction<Document> changes)
      throws IndexException
   {
      final String newIndexName = indexNames.newName();
      FSDirectory dir;
      try
      {
         dir = FSDirectory.getDirectory(new File(indexDir, newIndexName));
         indexNames.addName(newIndexName);
         indexNames.write(indexDir);
      }
      catch (final IOException e)
      {
         throw new IndexException(e.getLocalizedMessage(), e);
      }

      final PersistedIndex persistedIndex = new PersistedIndex(dir);
      persistedIndex.save(changes);

      return persistedIndex;
   }

   public File getIndexDir()
   {
      return indexDir;
   }

   public List<PersistedIndex> init() throws IndexException
   {
      final List<PersistedIndex> result = new ArrayList<PersistedIndex>();
      if (indexNames.exists(indexDir))
      {
         indexNames.read(indexDir);
      }

      // open persistent indexes
      for (int i = 0; i < indexNames.size(); i++)
      {
         final File sub = new File(indexDir, indexNames.getName(i));
         // only open if it still exists
         // it is possible that indexNames still contains a name for
         // an index that has been deleted, but indexNames has not been
         // written to disk.
         if (!sub.exists())
         {
            PersistentIndexDataKeeperFactory.LOG.debug("index does not exist anymore: " + sub.getAbsolutePath());
            // move on to next index
            continue;
         }
         try
         {
            final FSDirectory dir = FSDirectory.getDirectory(sub);
            result.add(new PersistedIndex(dir));
         }
         catch (final IOException e)
         {
            throw new IndexException(e.getLocalizedMessage(), e);
         }
      }
      return result;
   }

   /**
    * {@inheritDoc}
    */
   public LuceneIndexDataManager merge(final Collection<LuceneIndexDataManager> chains) throws IndexException
   {
      Directory dir;
      try
      {
         final String newIndexName = indexNames.newName();
         dir = FSDirectory.getDirectory(new File(indexDir, newIndexName));
         final IndexWriter writer = new IndexWriter(dir, new StandardAnalyzer(), MaxFieldLength.UNLIMITED);
         final List<Directory> dirs = new ArrayList<Directory>();
         for (final LuceneIndexDataManager luceneIndexDataManager : chains)
         {
            // TODO remove get reader
            luceneIndexDataManager.getIndexReader();
            dirs.add(luceneIndexDataManager.getDirectory());
         }
         final Directory[] dirsToMerge = new Directory[dirs.size()];
         writer.addIndexesNoOptimize(dirs.toArray(dirsToMerge));
         writer.optimize();
         writer.close();
         indexNames.addName(newIndexName);
         indexNames.write(indexDir);
      }
      catch (final IOException e)
      {
         throw new IndexException(e.getLocalizedMessage(), e);
      }
      return new PersistedIndex(dir);
   }

}
