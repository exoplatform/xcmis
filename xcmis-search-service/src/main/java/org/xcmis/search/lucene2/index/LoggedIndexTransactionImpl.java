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

import org.apache.lucene.document.Document;

import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34027 2009-07-15 23:26:43Z
 *          aheritier $
 */
public class LoggedIndexTransactionImpl extends LuceneIndexTransaction implements LoggedIndexTransaction<Document>
{

   /**
    * islogged flag.
    */
   private boolean isTransactionLogged;

   /**
    * Log of the transaction.
    */
   private final TransactionLog transactionLog;

   /**
    * @param addedDocuments
    * @param updatedDocuments
    * @param deletedDocuments
    */
   public LoggedIndexTransactionImpl(final IndexTransaction<Document> indexTransaction,
      final TransactionLog transactionLog)
   {
      super(indexTransaction.getAddedDocuments(), indexTransaction.getRemovedDocuments());
      this.transactionLog = transactionLog;
      this.isTransactionLogged = true;
   }

   /**
    * {@inheritDoc}
    */
   /**
    * @param addedDocuments
    * @param updatedDocuments
    * @param deletedDocuments
    */
   public LoggedIndexTransactionImpl(final Map<String, Document> addedDocuments, final Set<String> deletedDocuments,
      final IndexTransactionService indexTransactionService)
   {
      super(addedDocuments, deletedDocuments);
      this.transactionLog =
         new FileSystemTransactionLog(addedDocuments.keySet(), deletedDocuments,
            (FSIndexTransactionService)indexTransactionService);
   }

   @Override
   public IndexTransaction<Document> apply(final IndexTransactionModificationReport report)
   {
      return new LoggedIndexTransactionImpl(super.apply(report), this.transactionLog);
   }

   /**
    * {@inheritDoc}
    */
   public void end() throws IndexTransactionException
   {
      if (!this.isTransactionLogged)
      {
         throw new IndexTransactionException("Transaction not started");
      }
      this.transactionLog.removeLog();
   }

   /**
    * {@inheritDoc}
    */
   public TransactionLog getTransactionLog()
   {
      return this.transactionLog;
   }

   /**
    * {@inheritDoc}
    */
   public void log() throws IndexTransactionException
   {
      if (this.isTransactionLogged)
      {
         throw new IndexTransactionException("Transaction already started");
      }
      this.transactionLog.log();
      this.isTransactionLogged = true;
   }

}
