/**
 * Copyright (C) 2010 eXo Platform SAS.
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


package org.xcmis.sp.jcr.exo.query.index;

import org.apache.lucene.document.Document;
import org.exoplatform.services.jcr.dataflow.ItemDataConsumer;
import org.exoplatform.services.jcr.dataflow.ItemDataVisitor;
import org.exoplatform.services.jcr.datamodel.NodeData;
import org.exoplatform.services.jcr.datamodel.PropertyData;
import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.xcmis.search.index.IndexException;
import org.xcmis.search.index.IndexRestoreService;
import org.xcmis.search.index.IndexTransaction;
import org.xcmis.search.index.IndexTransactionException;
import org.xcmis.search.lucene.index.LuceneIndexTransaction;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;

import javax.jcr.RepositoryException;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34027 2009-07-15 23:26:43Z
 *          aheritier $
 */
public class JcrIndexRestoreService implements ItemDataVisitor, IndexRestoreService
{
   /**
    * Max documents count in storage.
    */
   public static final int MAX_DOCUMENTS_COUNT = 1000;

   /**
    * Max binary property size. If property is bigger, then flush all documents.
    */
   public static final long MAX_VALUE_SIZE = 32 * 1024 * 1024;

   /**
    * File name. If this file exists, that means reindex was interrupted, so need
    * new reindex.
    */
   static final String REINDEX_RUN = "reindexProcessing";

   /** The added documents map. */
   private final HashMap<String, Document> addedDocuments;

   /**
    * Current position in node tree levels. Used to indicate finish of
    * traversing.
    */
   private int currentLevel = 0;

   /** The data consumer. */
   private final ItemDataConsumer dataConsumer;

   /** The index data keeper. */
   private final StartableJcrIndexingService indexDataKeeper;

   //   private final LocationFactory locationFactory;

   //   private final IndependentNodeIndexer nodeIndexer;

   /** The removed documents set. */
   private final HashSet<String> removedDocuments;

   /** The updated documents map. */
   private final HashMap<String, Document> updatedDocuments;

   /**
    * The Constructor.
    * 
    * @param dataConsumer the data consumer
    * @param locationFactory the location factory
    * @param nodeIndexer the node indexer
    * @param indexDataKeeper the index data keeper
    */
   public JcrIndexRestoreService(final ItemDataConsumer dataConsumer, final LocationFactory locationFactory,
      final IndependentNodeIndexer nodeIndexer, final StartableJcrIndexingService indexDataKeeper)
   {
      super();
      this.dataConsumer = dataConsumer;
      //      this.locationFactory = locationFactory;
      //      this.nodeIndexer = nodeIndexer;
      this.indexDataKeeper = indexDataKeeper;
      addedDocuments = new HashMap<String, Document>();
      updatedDocuments = new HashMap<String, Document>();
      removedDocuments = new HashSet<String>();
   }

   /**
    * {@inheritDoc}
    */
   public ItemDataConsumer getDataManager()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public void visit(final NodeData node) throws RepositoryException
   {
      currentLevel++;
      processNode(node.getIdentifier(), node);
      for (final NodeData data : dataConsumer.getChildNodesData(node))
      {
         data.accept(this);
      }
      currentLevel--;
      if (currentLevel == 0 && addedDocuments.size() > 0)
      {
         // It's a root - flush all documents.
         flush();
      }
   }

   /**
    * {@inheritDoc}
    */
   public void visit(final PropertyData property) throws RepositoryException
   {
      // TODO Auto-generated method stub
   }

   /**
    * Save all documents to index and clear temporary storage.
    * 
    * @throws IndexTransactionException if save exception occurs
    * @throws IndexException if save exception occurs
    */
   protected void flush() throws IndexException, IndexTransactionException
   {
      final IndexTransaction<Document> indexTransaction =
         new LuceneIndexTransaction(addedDocuments, updatedDocuments, removedDocuments);
      indexDataKeeper.save(indexTransaction);
      // cleanup document storage
      addedDocuments.clear();
      updatedDocuments.clear();
      removedDocuments.clear();
   }

   /**
    * Index node and put into storage. Check storage size and flush.
    * 
    * @param uuid - node's uuid
    * @param node - NodeData , set null if node not exists
    * @throws RepositoryException if indexing or save documents exceptions
    *           occurs.
    */
   protected void processNode(final String uuid, final NodeData node) throws RepositoryException
   {
      if (node == null)
      {
         throw new NullPointerException("UUID must be not null.");
      }
      boolean forceFlush = false;
      if (node != null)
      {
         Document doc;
         try
         {
            doc = indexDataKeeper.crateDocumentFromPersistentStorage(node);
         }
         catch (UnsupportedEncodingException e)
         {
            throw new RepositoryException(e.getLocalizedMessage(), e.getCause());
         }
         catch (IOException e)
         {
            throw new RepositoryException(e.getLocalizedMessage(), e.getCause());
         }

         if (indexDataKeeper.documentExists(node.getIdentifier()))
         {
            // TODO if index already contains document, do we need update doc?
            updatedDocuments.put(node.getIdentifier(), doc);
         }
         else
         {
            addedDocuments.put(node.getIdentifier(), doc);
         }
      }
      else
      {
         if (indexDataKeeper.documentExists(uuid))
         {
            removedDocuments.add(uuid);
         }
      }

      if (forceFlush
         || addedDocuments.size() + updatedDocuments.size() + removedDocuments.size() >= MAX_DOCUMENTS_COUNT)
      {
         flush();
      }
   }

}
