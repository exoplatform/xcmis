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

import org.apache.commons.lang.Validate;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.config.IndexConfigurationException;
import org.xcmis.search.config.SearchServiceConfiguration;
import org.xcmis.search.content.interceptors.QueryableIndexStorage;
import org.xcmis.search.lucene.index.FieldNames;
import org.xcmis.search.lucene.index.IndexException;
import org.xcmis.search.lucene.index.IndexTransactionException;
import org.xcmis.search.lucene.index.LuceneIndexTransaction;
import org.xcmis.search.lucene.index.StartableIndexingService;

import java.io.IOException;

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
      Validate.notNull(serviceConfuguration.getIndexConfuguration().getIndexDir(),
         "The IndexDir may not be null in IndexConfiguration");
      this.indexDataManager =
         new StartableIndexingService(serviceConfuguration.getIndexConfuguration(), new IndexRecoveryTool(this,
            nodeIndexer, serviceConfuguration.getIndexConfuguration()));

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
}
