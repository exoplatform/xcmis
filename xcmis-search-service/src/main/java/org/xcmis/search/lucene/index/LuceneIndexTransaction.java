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
import org.xcmis.search.index.IndexTransaction;
import org.xcmis.search.index.IndexTransactionModificationReport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: LuceneIndexTransaction.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class LuceneIndexTransaction implements IndexTransaction<Document>
{
   /**
    * Map of documents what should be added to index.
    */
   private final Map<String, Document> addedDocuments;

   /**
    * Set of identifiers of documents what should be removed from index.
    */
   private final Set<String> deletedDocuments;

   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(LuceneIndexTransaction.class);

   /**
    * Map of documents what should be updated in index.
    */
   private final Map<String, Document> updatedDocuments;

   //
   // /**
   // * @param addedDocuments
   // * @param deletedDocuments
   // * @param updatedDocuments
   // */
   // public LuceneIndexTransaction(final IndexTransaction<Document> transaction,
   // final IndexTransactionModificationReport report) {
   // super();
   // this.addedDocuments = new HashMap<String, Document>();
   // this.deletedDocuments = new HashSet<String>();
   // this.updatedDocuments = new HashMap<String, Document>();
   //
   // this.addedDocuments.putAll(transaction.getAddedDocuments());
   // this.deletedDocuments.addAll(transaction.getRemovedDocuments());
   // this.updatedDocuments.putAll(transaction.getUpdatedDocuments());
   //
   // for (final String addedUuid : report.getAddedDocuments()) {
   // this.addedDocuments.remove(addedUuid);
   // }
   //
   // for (final String removedUuid : report.getRemovedDocuments()) {
   // this.deletedDocuments.remove(removedUuid);
   // }
   //
   // for (final String uodatedUuid : report.getUpdatedDocuments()) {
   // this.updatedDocuments.remove(uodatedUuid);
   // }
   // }

   /**
    * @param addedDocuments
    * @param updatedDocuments
    * @param deletedDocuments
    * @param storage TODO
    * @param transactionLogDir
    */
   public LuceneIndexTransaction(final Map<String, Document> addedDocuments,
      final Map<String, Document> updatedDocuments, final Set<String> deletedDocuments)
   {
      super();
      this.addedDocuments = addedDocuments;
      this.deletedDocuments = deletedDocuments;
      this.updatedDocuments = updatedDocuments;

   }

   /**
    * {@inheritDoc}
    */
   public IndexTransaction<Document> apply(final IndexTransactionModificationReport report)
   {
      final HashMap<String, Document> newAddedDocuments = new HashMap<String, Document>();
      final HashSet<String> newRemovedDocuments = new HashSet<String>();
      final HashMap<String, Document> newUpdatedDocuments = new HashMap<String, Document>();

      newAddedDocuments.putAll(this.getAddedDocuments());
      newRemovedDocuments.addAll(this.getRemovedDocuments());
      newUpdatedDocuments.putAll(this.getUpdatedDocuments());

      for (final String addedUuid : report.getAddedDocuments())
      {
         newAddedDocuments.remove(addedUuid);
      }

      for (final String removedUuid : report.getRemovedDocuments())
      {
         newRemovedDocuments.remove(removedUuid);
      }

      for (final String updatedUuid : report.getUpdatedDocuments())
      {
         newUpdatedDocuments.remove(updatedUuid);
      }
      return new LuceneIndexTransaction(newAddedDocuments, newUpdatedDocuments, newRemovedDocuments);
   }

   /**
    * {@inheritDoc}
    */
   public Map<String, Document> getAddedDocuments()
   {
      return this.addedDocuments;
   }

   /**
    * {@inheritDoc}
    */
   public long getAddedDocumentSizeInBytes()
   {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */
   public Set<String> getRemovedDocuments()
   {
      return this.deletedDocuments;
   }

   /**
    * {@inheritDoc}
    */
   public Map<String, Document> getUpdatedDocuments()
   {
      return this.updatedDocuments;
   }

   public boolean hasAddedDocuments()
   {
      return this.addedDocuments.size() > 0;
   }

   public boolean hasModifacationsDocuments()
   {
      return this.updatedDocuments.size() > 0 || this.deletedDocuments.size() > 0;
   }

}
