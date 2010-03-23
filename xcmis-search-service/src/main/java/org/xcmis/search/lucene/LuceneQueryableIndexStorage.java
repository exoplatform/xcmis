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

import org.apache.lucene.index.IndexReader;
import org.xcmis.search.config.IndexConfigurationException;
import org.xcmis.search.config.SearchServiceConfiguration;
import org.xcmis.search.content.interceptors.QueryableIndexStorage;
import org.xcmis.search.lucene.index.IndexException;
import org.xcmis.search.lucene.index.IndexTransactionException;
import org.xcmis.search.lucene.index.LuceneIndexTransaction;
import org.xcmis.search.lucene.index.StartableIndexingService;

/**
 * Lucene persisted implementation of {@link QueryableIndexStorage}  
 * 
 */
public class LuceneQueryableIndexStorage extends AbstractLuceneQueryableIndexStorage
{

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
      this.indexDataManager = new StartableIndexingService(serviceConfuguration.getIndexConfuguration());

   }

   /**
    * @see org.xcmis.search.lucene.AbstractLuceneQueryableIndexStorage#getIndexReader()
    */
   @Override
   protected IndexReader getIndexReader()
   {
      try
      {
         return indexDataManager.getIndexReader();
      }
      catch (IndexException e)
      {
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
