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

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;
import org.xcmis.search.Startable;
import org.xcmis.spi.utils.Logger;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: PersistedIndex.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class PersistedIndex implements LuceneIndexDataManager, Startable
{
   /**
    * Class logger.
    */
   private static final Logger LOG = Logger.getLogger(PersistedIndex.class);

   private final Directory indexDirectiry;

   /**
    * Index storage.
    */
   private IndexReader indexReader;

   private long lastModifedTime;

   public PersistedIndex(final Directory directory)
   {
      super();
      this.indexDirectiry = directory;
      this.lastModifedTime = System.currentTimeMillis();

   }

   /**
    * {@inheritDoc}
    */
   public Directory getDirectory() throws IndexException
   {
      return this.indexDirectiry;
   }

   /**
    * {@inheritDoc}
    */
   public long getDirectorySize(final boolean includeInherited)
   {
      int result = 0;
      try
      {
         final String[] list = this.indexDirectiry.listAll();

         for (final String element : list)
         {
            result += this.indexDirectiry.fileLength(element);
         }
      }
      catch (final IOException e)
      {
         // e.printStackTrace();
      }
      return result;
   }

   /**
    * {@inheritDoc}
    */
   public Document getDocument(final String uuid) throws IndexException
   {

      final IndexReader reader = this.getIndexReader();

      return this.getDocument(uuid, reader);
   }

   /**
    * {@inheritDoc}
    */
   public long getDocumentCount()
   {
      try
      {
         return this.getIndexReader().numDocs();
      }
      catch (final IndexException e)
      {
         LOG.error(e.getMessage(), e);
      }
      return 0;
   }

   /**
    * {@inheritDoc}
    */
   public IndexReader getIndexReader() throws IndexException
   {
      try
      {
         if (this.indexReader == null)
         {
            this.indexReader = IndexReader.open(this.indexDirectiry);
         }
         else if (!this.indexReader.isCurrent())
         {
            this.indexReader = this.indexReader.reopen();
         }
      }
      catch (final CorruptIndexException e)
      {
         throw new IndexException(e.getLocalizedMessage(), e);
      }
      catch (final IOException e)
      {
         throw new IndexException(e.getLocalizedMessage(), e);
      }
      return this.indexReader;
   }

   /**
    * {@inheritDoc}
    */
   public long getLastModifedTime()
   {
      return this.lastModifedTime;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isStarted()
   {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isStoped()
   {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   public IndexTransactionModificationReport save(final IndexTransaction<Document> changes) throws IndexException
   {

      final Set<String> removedDocuments = new HashSet<String>();
      final Set<String> updatedDocuments = new HashSet<String>();

      try
      {
         // index already started
         synchronized (this.indexDirectiry)
         {

            final Set<String> removed = changes.getRemovedDocuments();
            IndexWriter writer = null;
            IndexReader reader = null;

            Map<String, Document> updated = null;
            for (final String removedUuid : removed)
            {

               if (reader == null)
               {
                  reader = this.getIndexReader();
               }

               if (this.getDocument(removedUuid, reader) != null)
               {
                  removedDocuments.add(removedUuid);
               }
            }

            if (removedDocuments.size() > 0 || changes.getAddedDocuments().size() > 0)
            {

               writer = new IndexWriter(this.indexDirectiry, new StandardAnalyzer(Version.LUCENE_35), MaxFieldLength.UNLIMITED);

               // removed
               for (final String uuid : removedDocuments)
               {
                  writer.deleteDocuments(new Term(FieldNames.UUID, uuid));
               }
               // updated
               for (final String uuid : updatedDocuments)
               {
                  // TODO possible use only delete
                  writer.updateDocument(new Term(FieldNames.UUID, uuid), updated.get(uuid));
               }
               // added
               for (final Document document : changes.getAddedDocuments().values())
               {
                  writer.addDocument(document);
               }

               writer.commit();
               writer.close();

               this.lastModifedTime = System.currentTimeMillis();
            }
         }

      }
      catch (final CorruptIndexException e)
      {
         throw new IndexException(e.getLocalizedMessage(), e);
      }
      catch (final IOException e)
      {
         throw new IndexException(e.getLocalizedMessage(), e);
      }

      return new IndexTransactionModificationReportImpl(changes.getAddedDocuments().keySet(), removedDocuments,
         updatedDocuments);
   }

   /**
    * {@inheritDoc}
    */
   public void start()
   {
   }

   /**
    * {@inheritDoc}
    */
   public void stop()
   {
      try
      {
         this.indexReader.close();
         this.indexDirectiry.close();
      }
      catch (final IOException e)
      {
         PersistedIndex.LOG.error(e.getLocalizedMessage(), e);
      }
   }

   private Document getDocument(final String uuid, final IndexReader reader) throws IndexException
   {

      try
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
      catch (final IOException e)
      {
         throw new IndexException(e.getLocalizedMessage(), e);
      }
      return null;
   }

}
