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
import org.exoplatform.services.document.DocumentReaderService;
import org.exoplatform.services.jcr.core.NamespaceAccessor;
import org.exoplatform.services.jcr.dataflow.ItemDataConsumer;
import org.exoplatform.services.jcr.datamodel.ItemData;
import org.exoplatform.services.jcr.datamodel.NodeData;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.index.IndexException;
import org.xcmis.search.lucene.index.IndexRecoverService;
import org.xcmis.search.lucene.index.LuceneIndexTransaction;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.RepositoryException;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34027 2009-07-15 23:26:43Z
 *          aheritier $
 */
public class JcrIndexRecoverService implements IndexRecoverService
{

   /** The data manager. */
   private final ItemDataConsumer dataManager;

   /** The index data keeper. */
   private final LuceneIndexingService indexDataKeeper;

   //   private final LocationFactory locationFactory;

   /**
    * Class logger.
    */
   private static final Log LOG = ExoLogger.getLogger(JcrIndexRecoverService.class);

   //   private final IndependentNodeIndexer nodeIndexer;

   /**
    * The Constructor.
    * 
    * @param dataManager ItemDataConsumer
    * @param namespaceAccessor the namespace accessor
    * @param extractor the extractor
    * @param indexingService the indexing service
    */
   public JcrIndexRecoverService(final ItemDataConsumer dataManager, final NamespaceAccessor namespaceAccessor,
      final DocumentReaderService extractor, final LuceneIndexingService indexingService)
   {
      this.dataManager = dataManager;
      indexDataKeeper = indexingService;
      //      locationFactory = new LocationFactory(namespaceAccessor);
      //      nodeIndexer = new IndependentNodeIndexer(namespaceAccessor, extractor);
   }

   /**
    * {@inheritDoc}
    */
   public void recover(final Set<String> uuids) throws IndexException
   {
      if (indexDataKeeper == null)
      {
         throw new IndexException("IndexingService not inintialized");
      }

      final HashMap<String, Document> updatedDocuments = new HashMap<String, Document>();
      final HashMap<String, Document> addedDocuments = new HashMap<String, Document>();
      final HashSet<String> removedDocuments = new HashSet<String>();
      for (final String nodeUuid : uuids)
      {
         try
         {
            final ItemData itemData = dataManager.getItemData(nodeUuid);
            if (itemData == null)
            {
               if (indexDataKeeper.documentExists(nodeUuid))
               {
                  // item exist in index storage but doesn't exist in persistence storage
                  removedDocuments.add(nodeUuid);
               }
            }
            else
            {
               // Item is property
               if (!itemData.isNode())
               {
                  LOG.error("Fail to recover item " + nodeUuid + ". It is not a node.");
               }
               else
               {
                  final NodeData nodeData = (NodeData)itemData;
                  Document doc;
                  doc = ((JcrIndexingService)indexDataKeeper).crateDocumentFromPersistentStorage(nodeData);
                  if (indexDataKeeper.documentExists(nodeUuid))
                  {
                     updatedDocuments.put(nodeUuid, doc);
                  }
                  else
                  {
                     addedDocuments.put(nodeUuid, doc);
                  }
               }
            }
         }
         catch (final RepositoryException e)
         {
            LOG.error("Fail to recover item " + nodeUuid);
         }
         catch (UnsupportedEncodingException e)
         {
            LOG.error("Fail to recover item " + nodeUuid);
         }
         catch (IOException e)
         {
            LOG.error("Fail to recover item " + nodeUuid);
         }
      }

      indexDataKeeper.save(new LuceneIndexTransaction(addedDocuments, updatedDocuments, removedDocuments));
   }
}
