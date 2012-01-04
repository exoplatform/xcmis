/**
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

package org.xcmis.search.lucene.index;

import org.apache.lucene.document.Document;
import org.xcmis.search.config.IndexConfiguration;
import org.xcmis.search.config.IndexConfigurationException;
import org.xcmis.search.lucene.IndexRecoveryTool;
import org.xcmis.spi.utils.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: StartableJcrIndexingService.java 2 2010-02-04 17:21:49Z
 *          andrew00x $
 */
public class StartableIndexingService extends LuceneIndexingService
{
   /**
    * File name. If this file exists, that means reindex was interrupted, so
    * need new reindex.
    */
   private static final String REINDEX_RUN = "reindexProcessing";

   /** ChangesLog Buffer (used for saves before start). */
   private List<IndexTransaction<Document>> changesLogBuffer = new ArrayList<IndexTransaction<Document>>();

   /** The index dir. */
   private final File indexDir;

   /** The index restore service. */
   private final IndexRecoveryTool indexRecoveryTool;

   /** Is started flag. */
   private boolean isStarted = false;

   /** Class logger. */
   private static final Logger LOG = Logger.getLogger(StartableIndexingService.class);

   /**
    * @param configuration
    * @throws IndexConfigurationException
    * @throws IndexException
    */
   public StartableIndexingService(IndexConfiguration configuration, IndexRecoveryTool indexRecoveryTool)
      throws IndexConfigurationException, IndexException
   {
      super(configuration, indexRecoveryTool);
      this.indexRecoveryTool = indexRecoveryTool;
      this.indexDir = new File(configuration.getIndexDir());
      if (!indexDir.exists() && !indexDir.mkdirs())
      {
         throw new IndexException("Fail to create index directory : " + indexDir.getAbsolutePath());
      }
   }

   /**
    * @see org.xcmis.search.lucene.index.TransactionableIndexDataManager#save(org.xcmis.search.lucene.index.IndexTransaction)
    */
   @Override
   public IndexTransactionModificationReport save(IndexTransaction<Document> changes) throws IndexException,
      IndexTransactionException
   {

      if (isStarted)
      {
         return super.save(changes);
      }
      changesLogBuffer.add(changes);
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void start()
   {
      super.start();
      // save buffered logs
      isStarted = true;
      try
      {
         for (final IndexTransaction<Document> bufferedChangesLog : changesLogBuffer)
         {
            super.save(bufferedChangesLog);
         }

         if (needIndexRestore())
         {
            // need restore index;
            try
            {
               restoreIndex();
            }
            catch (final IOException e)
            {
               LOG.error("Restore IOException occurs " + e.getMessage(), e);
            }
         }
      }
      catch (IndexException e)
      {
         LOG.error(e.getMessage(), e);
      }

      changesLogBuffer.clear();
      changesLogBuffer = null;

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void stop()
   {
      super.stop();
   }

   /**
    * Do we need restore index.
    * 
    * @return <code>true</code> if index not exist or restore was interrupted.
    * @throws IndexException
    *            if index read exception occurs.
    */
   private boolean needIndexRestore() throws IndexException
   {

      if (getDocumentCount() == 0)
      {
         return true;
      }
      final File flag = new File(indexDir, StartableIndexingService.REINDEX_RUN);
      return flag.exists();

   }

   /**
    * Restore Index from full repository content.
    * 
    * @throws IOException
    *            if reindex flag file was not created or was'nt removed.
    * @throws IndexException
    */
   private void restoreIndex() throws IOException, IndexException
   {

      if (LOG.isDebugEnabled())
      {
         LOG.info("Restore index started.");
      }

      final File flag = new File(indexDir, StartableIndexingService.REINDEX_RUN);
      if (!flag.exists())
      {
         if (!flag.createNewFile())
         {
            throw new IOException("Reindex flag file was not created.");
         }
      }

      // clean persisted index
      softCleanIndex();

      indexRecoveryTool.recoverAll();

      if (!flag.delete())
      {
         throw new IOException("Can't remove reindex flag.");
      }

      // clean changesLogBuffer
      // all states in changesLogBuffer already indexed by IndexRestoreVisitor,
      // so it contains duplicated data
      changesLogBuffer.clear();

      if (LOG.isDebugEnabled())
      {
         LOG.info("Restore index finished.");
      }
   }
}
