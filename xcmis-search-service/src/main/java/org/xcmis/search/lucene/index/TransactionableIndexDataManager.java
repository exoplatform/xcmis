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
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.config.IndexConfigurationException;
import org.xcmis.search.config.IndexConfiguration;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: TransactionableIndexDataManager.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class TransactionableIndexDataManager extends CacheableIndexDataManager
{

   protected static final String TRANSACTION_LOG_STORAGE_NAME = "logs";

   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(TransactionableIndexDataManager.class);

   private final IndexRecoverService recoverService;

   private final FSIndexTransactionService transactionService;

   public TransactionableIndexDataManager(final IndexConfiguration indexConfuguration) throws IndexException,
      IndexConfigurationException
   {
      super(indexConfuguration);
      this.recoverService = indexConfuguration.getIndexRecoverService();

      final File indexDir = new File(indexConfuguration.getIndexDir());

      if (!indexDir.exists() && !indexDir.mkdirs())
      {
         throw new IndexException("Fail to create index directory : " + indexDir.getAbsolutePath());
      }

      final File storageDir = new File(indexDir, TRANSACTION_LOG_STORAGE_NAME);
      if (!storageDir.exists() && !storageDir.mkdirs())
      {
         throw new IndexException("Fail to create directory : " + storageDir.getAbsolutePath());
      }

      this.transactionService = new FSIndexTransactionService(storageDir, new ReadWriteDirectoryFactory());

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public IndexTransactionModificationReport save(final IndexTransaction<Document> changes) throws IndexException,
      IndexTransactionException
   {

      final boolean removeTransaction =
         changes.getRemovedDocuments().size() > 0 && changes.getAddedDocuments().size() == 0;

      final LoggedIndexTransactionImpl loggedIndexTransaction =
         new LoggedIndexTransactionImpl(changes.getAddedDocuments(), changes.getRemovedDocuments(),
            this.transactionService);

      loggedIndexTransaction.log();

      final IndexTransactionModificationReport result = super.save(loggedIndexTransaction);

      // all changes applied
      if (removeTransaction)
      {
         loggedIndexTransaction.end();
      }

      return result;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void start()
   {
      // TODO Auto-generated method stub
      super.start();
      try
      {
         if (this.transactionService.hasUncommitedTransactions())
         {
            // get logs from storage
            final List<TransactionLog> logs = this.transactionService.getTransactionLogs();

            // load all logs
            final CompositeTransactionLog compositeTransactionLog = new CompositeTransactionLog(logs);
            // create list of compromised uuids of documents
            final Set<String> compromisedUuids = new HashSet<String>();
            compromisedUuids.addAll(compositeTransactionLog.getAddedList());
            compromisedUuids.addAll(compositeTransactionLog.getRemovedList());

            // start recovering process
            this.recoverService.recover(compromisedUuids);
            //clear old logs
            for (TransactionLog transactionLog : logs)
            {
               transactionLog.removeLog();
            }

         }
      }
      catch (final TransactionLogException e)
      {
         throw new RuntimeException(e.getLocalizedMessage(), e);
      }
      catch (final IndexException e)
      {
         throw new RuntimeException(e.getLocalizedMessage(), e);
      }

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void stop()
   {
      // TODO Auto-generated method stub
      super.stop();
   }
}
