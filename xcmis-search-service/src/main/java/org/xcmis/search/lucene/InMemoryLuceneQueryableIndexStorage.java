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

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.xcmis.search.config.SearchServiceConfiguration;
import org.xcmis.search.content.IndexModificationException;
import org.xcmis.search.lucene.index.FieldNames;
import org.xcmis.search.lucene.index.IndexException;
import org.xcmis.search.lucene.index.IndexTransactionException;
import org.xcmis.search.lucene.index.LuceneIndexTransaction;

import java.io.IOException;
import java.util.Map.Entry;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z
 *          aheritier $
 * 
 */
public class InMemoryLuceneQueryableIndexStorage extends AbstractLuceneQueryableIndexStorage
{
   /**
    * Lucene in memory index directory.
    */
   private final RAMDirectory ramDirectory;

   private IndexReader indexReader;

   /**
    * @param serviceConfuguration
    * @throws IndexException
    */
   public InMemoryLuceneQueryableIndexStorage(SearchServiceConfiguration serviceConfuguration) throws IndexException
   {
      super(serviceConfuguration);
      this.ramDirectory = new RAMDirectory();
      initDirectory();
   }

   /**
    * @throws CorruptIndexException
    * @throws LockObtainFailedException
    * @throws IOException
    */
   private void initDirectory() throws IndexException
   {
      try
      {
         IndexWriter.MaxFieldLength fieldLength = new IndexWriter.MaxFieldLength(IndexWriter.DEFAULT_MAX_FIELD_LENGTH);
         IndexWriter iw = new IndexWriter(ramDirectory, new SimpleAnalyzer(), true, fieldLength);
         iw.close();
      }
      catch (IOException e)
      {
         throw new IndexException(e.getLocalizedMessage(), e);
      }
   }

   /**
    * @throws IndexException
    * @see org.xcmis.search.lucene.AbstractLuceneQueryableIndexStorage#getIndexReader()
    */
   @Override
   protected IndexReader getIndexReader() throws IndexException
   {
      try
      {
         // reopen if need
         if (this.indexReader == null)
         {
            this.indexReader = IndexReader.open(this.ramDirectory);
         }
         else if (!this.indexReader.isCurrent())
         {
            this.indexReader = this.indexReader.reopen();
         }
      }
      catch (CorruptIndexException e)
      {
         throw new IndexException(e.getLocalizedMessage(), e);
      }
      catch (IOException e)
      {
         throw new IndexException(e.getLocalizedMessage(), e);
      }
      return indexReader;
   }

   /**
    * @see org.xcmis.search.lucene.AbstractLuceneQueryableIndexStorage#save(org.xcmis.search.lucene.index.LuceneIndexTransaction)
    */
   @Override
   protected synchronized Object save(LuceneIndexTransaction indexTransaction) throws IndexException,
      IndexTransactionException
   {

      try
      {
         IndexWriter writer = new IndexWriter(ramDirectory, new StandardAnalyzer(), MaxFieldLength.UNLIMITED);

         // removed
         for (final String uuid : indexTransaction.getRemovedDocuments())
         {
            writer.deleteDocuments(new Term(FieldNames.UUID, uuid));
         }

         // added
         for (final Entry<String, Document> entry : indexTransaction.getAddedDocuments().entrySet())
         {
            writer.updateDocument(new Term(FieldNames.UUID, entry.getKey()), entry.getValue());
         }

         writer.commit();
         writer.close();
      }
      catch (CorruptIndexException e)
      {
         throw new IndexModificationException(e.getLocalizedMessage(), e);
      }
      catch (LockObtainFailedException e)
      {
         throw new IndexModificationException(e.getLocalizedMessage(), e);
      }
      catch (IOException e)
      {
         throw new IndexModificationException(e.getLocalizedMessage(), e);
      }
      return new Object();
   }

}
