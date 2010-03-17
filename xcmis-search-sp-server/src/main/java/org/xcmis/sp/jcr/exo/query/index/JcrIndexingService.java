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
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.config.RepositoryEntry;
import org.exoplatform.services.jcr.config.WorkspaceEntry;
import org.exoplatform.services.jcr.core.NamespaceAccessor;
import org.exoplatform.services.jcr.core.nodetype.NodeTypeDataManager;
import org.exoplatform.services.jcr.dataflow.ItemDataConsumer;
import org.exoplatform.services.jcr.dataflow.ItemState;
import org.exoplatform.services.jcr.dataflow.ItemStateChangesLog;
import org.exoplatform.services.jcr.dataflow.persistent.ItemsPersistenceListener;
import org.exoplatform.services.jcr.datamodel.InternalQName;
import org.exoplatform.services.jcr.datamodel.ItemData;
import org.exoplatform.services.jcr.datamodel.NodeData;
import org.exoplatform.services.jcr.datamodel.PropertyData;
import org.exoplatform.services.jcr.datamodel.QPath;
import org.exoplatform.services.jcr.datamodel.QPathEntry;
import org.exoplatform.services.jcr.impl.Constants;
import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.exoplatform.services.jcr.impl.core.value.ValueFactoryImpl;
import org.exoplatform.services.jcr.impl.dataflow.ValueDataConvertor;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.index.FieldNames;
import org.xcmis.search.index.IndexException;
import org.xcmis.search.index.IndexTransaction;
import org.xcmis.search.lucene.index.LuceneIndexTransaction;
import org.xcmis.spi.RepositoriesManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFactory;

/**
 * @author <a href="mailto:foo@bar.org">Foo Bar</a>
 * @version $Id: exo-jboss-codetemplates.xml 34027 2009-07-15 23:26:43Z
 *          aheritier $
 */
public class JcrIndexingService extends LuceneIndexingService implements ItemsPersistenceListener
{

   /** The item data consumer. */
   public final ItemDataConsumer itemDataConsumer;

   /** The location factory. */
   public final LocationFactory locationFactory;

   /** The jcr encoding field name. */
   private final String jcrEncodingFieldName;

   /** The jcr mime type field name. */
   private final String jcrMimeTypeFieldName;

   /**
    * Class logger.
    */
   private static final Log LOG = ExoLogger.getLogger(JcrIndexingService.class);

   // public final NamespaceAccessor namespaceAccessor;

   /** The node indexer. */
   private final IndependentNodeIndexer nodeIndexer;

   /** The node type data manager. */
   private final NodeTypeDataManager nodeTypeDataManager;

   /**
    * The Constructor.
    * 
    * @param repositoryEntry the repository entry
    * @param workspaceEntry the workspace entry
    * @param cmisRepositoriesManager the cmis repositories manager
    * @param itemDataConsumer the item data consumer
    * @param namespaceAccessor the namespace accessor
    * @param nodeTypeDataManager the node type data manager
    * @param extractor the extractor
    * 
    * @throws RepositoryConfigurationException the repository configuration exception
    * @throws RepositoryException if any errors in CMIS repository occurs
    */
   public JcrIndexingService(final RepositoryEntry repositoryEntry, final WorkspaceEntry workspaceEntry,
      final RepositoriesManager cmisRepositoriesManager, final ItemDataConsumer itemDataConsumer,
      final NamespaceAccessor namespaceAccessor, final NodeTypeDataManager nodeTypeDataManager,
      final DocumentReaderService extractor) throws RepositoryConfigurationException, RepositoryException
   {
      super(repositoryEntry, workspaceEntry, cmisRepositoriesManager, itemDataConsumer, namespaceAccessor,
         nodeTypeDataManager, extractor);
      this.itemDataConsumer = itemDataConsumer;
      // this.namespaceAccessor = namespaceAccessor;
      this.nodeTypeDataManager = nodeTypeDataManager;

      locationFactory = new LocationFactory(namespaceAccessor);
      jcrMimeTypeFieldName = locationFactory.createJCRName(Constants.JCR_MIMETYPE).getAsString();
      jcrEncodingFieldName = locationFactory.createJCRName(Constants.JCR_ENCODING).getAsString();
      nodeIndexer = new IndependentNodeIndexer(namespaceAccessor, extractor);

   }

   /**
    * Recreate document from persistent storage.
    * 
    * @param nodeUuid String
    * @return Document Document
    * @throws RepositoryException if any errors in CMIS repository occurs
    * @throws UnsupportedEncodingException if encoding is bad
    * @throws IOException if any io error
    */
   public Document crateDocumentFromPersistentStorage(String nodeUuid) throws RepositoryException,
      UnsupportedEncodingException, IOException
   {
      ItemData item = itemDataConsumer.getItemData(nodeUuid);
      return crateDocumentFromPersistentStorage(item);
   }

   /**
    * Crate document from persistent storage.
    * 
    * @param item the item
    * @return the document
    * @throws RepositoryException the repository exception
    * @throws UnsupportedEncodingException the unsupported encoding exception
    * @throws IOException Signals that an I/O exception has occurred.
    */
   public Document crateDocumentFromPersistentStorage(ItemData item) throws RepositoryException,
      UnsupportedEncodingException, IOException
   {

      if (item == null)
      {
         return null;
      }
      if (!item.isNode())
      {
         throw new IndexException("Item with id " + item.getIdentifier() + " is not a node");
      }

      NodeData node = (NodeData)item;
      final Document doc = nodeIndexer.createDocument(node);
      final List<PropertyData> propertiesNames = itemDataConsumer.listChildPropertiesData(node);
      // mimetype and encoding of jcr:content node;
      for (PropertyData propertyData : propertiesNames)
      {
         final QPathEntry[] entries = propertyData.getQPath().getEntries();
         if (propertyData.getType() != PropertyType.BINARY)
         {
            // if the prop obtainer from cache it will contains a values,
            // otherwise read prop with values from DM
            if (propertyData.getValues().size() == 0)
            {
               propertyData = (PropertyData)itemDataConsumer.getItemData(node, entries[entries.length - 1]);
            }

            nodeIndexer.addNonBinaryProperty(doc, propertyData);
         }
      }

      // if node inherited from nt:file
      // extract jcr:data
      if (nodeTypeDataManager.isNodeType(Constants.NT_FILE, node.getPrimaryTypeName(), node.getMixinTypeNames()))
      {
         NodeData jcrContentData =
            (NodeData)itemDataConsumer.getItemData(node, new QPathEntry(Constants.JCR_CONTENT, 1));
         PropertyData jcrContent =
            (PropertyData)itemDataConsumer.getItemData(jcrContentData, new QPathEntry(Constants.JCR_DATA, 1));
         ItemData mimetypeData =
            itemDataConsumer.getItemData(jcrContentData, new QPathEntry(Constants.JCR_MIMETYPE, 1));
         String mimetype = null;
         if (mimetypeData != null && !mimetypeData.isNode())
         {
            mimetype = ValueDataConvertor.readString(((PropertyData)mimetypeData).getValues().get(0));
         }
         ItemData encodingData =
            itemDataConsumer.getItemData(jcrContentData, new QPathEntry(Constants.JCR_ENCODING, 1));
         String encoding = null;
         if (encodingData != null && !encodingData.isNode())
         {
            encoding = ValueDataConvertor.readString(((PropertyData)encodingData).getValues().get(0));
         }

         nodeIndexer.addDocumentProperty(doc, jcrContent, mimetype, encoding);
      }

      return doc;

   }

   /**
    * Gets the value factory.
    * 
    * @return the value factory
    */
   public ValueFactory getValueFactory()
   {
      return new ValueFactoryImpl(locationFactory);
   }

   /**
    * {@inheritDoc}
    */
   public void onSaveItems(final ItemStateChangesLog itemStates)
   {
      System.currentTimeMillis();

      final List<ItemState> allStates = itemStates.getAllStates();

      try
      {
         // map of new documents
         final Map<String, Document> pendingDocuments = new HashMap<String, Document>();
         // map of updated documents
         final Map<String, Document> updatedDocuments = new HashMap<String, Document>();
         // map of binary properties
         final Map<String, PropertyData> binaryProperties = new HashMap<String, PropertyData>();
         // deleted document set
         final Set<String> deletedDocuments = new HashSet<String>();
         for (final ItemState itemState : allStates)
         {

            if (!itemState.isPersisted())
            {
               continue;
            }

            final String uuid =
               itemState.isNode() ? itemState.getData().getIdentifier() : itemState.getData().getParentIdentifier();
            if (itemState.isAdded())
            {
               if (itemState.isNode())
               {
                  if (deletedDocuments.remove(uuid))
                  {
                     updatedDocuments.put(uuid, nodeIndexer.createDocument((NodeData)itemState.getData()));
                  }
                  else
                  {
                     pendingDocuments.put(uuid, nodeIndexer.createDocument((NodeData)itemState.getData()));
                  }
               }
               else
               {
                  final PropertyData propertyData = (PropertyData)itemState.getData();
                  final Document doc =
                     getDocumentForModification(pendingDocuments, updatedDocuments, propertyData.getQPath(), uuid);
                  // node will be removed after;
                  if (doc != null)
                  {
                     if (propertyData.getType() == PropertyType.BINARY)
                     {
                        binaryProperties.put(uuid, propertyData);
                     }
                     else
                     {
                        nodeIndexer.addNonBinaryProperty(doc, propertyData);
                     }
                  }
               }
            }
            else if (itemState.isRenamed())
            {
               if (itemState.isNode())
               {
                  final ItemData data = itemState.getData();
                  final Document doc =
                     getDocumentForModification(pendingDocuments, updatedDocuments, data.getQPath(), uuid);
                  // node will be removed after;
                  if (doc != null)
                  {
                     nodeIndexer.rename(doc, (NodeData)data);
                     updatedDocuments.put(uuid, doc);
                  }
               }
               else
               {
                  LOG.warn("Unhandled property modification type 'renamed'");
               }

            }
            else if (itemState.isUpdated())
            {
               if (!itemState.isNode())
               {
                  final PropertyData propertyData = (PropertyData)itemState.getData();
                  final Document doc =
                     getDocumentForModification(pendingDocuments, updatedDocuments, propertyData.getQPath(), uuid);
                  // node will be removed after;
                  if (doc != null)
                  {
                     nodeIndexer.removeProperty(doc, propertyData);
                     if (propertyData.getType() == PropertyType.BINARY)
                     {
                        binaryProperties.put(uuid, propertyData);
                     }
                     else
                     {
                        nodeIndexer.addNonBinaryProperty(doc, propertyData);
                     }
                  }
               }
               else
               {
                  // renamed node
                  // possible be sns move
                  final ItemData data = itemState.getData();
                  final Document doc =
                     getDocumentForModification(pendingDocuments, updatedDocuments, data.getQPath(), uuid);

                  // node will be removed after;
                  if (doc != null)
                  {
                     nodeIndexer.rename(doc, (NodeData)data);
                     updatedDocuments.put(uuid, doc);
                  }
               }

            }
            else if (itemState.isMixinChanged())
            {
               // LOG.warn("Unhandled modification type 'isMixinChanged'");
            }
            else if (itemState.isDeleted())
            {
               if (itemState.isNode())
               {
                  updatedDocuments.remove(uuid);
                  binaryProperties.remove(uuid);
                  // pure delete
                  if (pendingDocuments.remove(uuid) == null)
                  {
                     deletedDocuments.add(uuid);
                  }
               }
               else
               {
                  final PropertyData propertyData = (PropertyData)itemState.getData();
                  final Document doc =
                     getDocumentForModification(pendingDocuments, updatedDocuments, propertyData.getQPath(), uuid);
                  // node will be removed after;
                  if (doc != null)
                  {
                     nodeIndexer.removeProperty(doc, propertyData);
                  }
               }
            }
         }

         // extract text from documents
         for (final Entry<String, PropertyData> entry : binaryProperties.entrySet())
         {

            final String uuid = entry.getKey();
            final PropertyData propertyData = entry.getValue();
            final Document doc;
            final String mimetype;
            final String encoding;
            if (isJcrContent(propertyData))
            {
               Document parentDoc =
                  getDocumentForModification(pendingDocuments, updatedDocuments, propertyData.getQPath(), uuid);

               mimetype = parentDoc.get(FieldNames.createPropertyFieldName(jcrMimeTypeFieldName));
               encoding = parentDoc.get(FieldNames.createPropertyFieldName(jcrEncodingFieldName));

               doc =
                  getDocumentForModification(pendingDocuments, updatedDocuments, propertyData.getQPath(), parentDoc
                     .getField(FieldNames.PARENT).stringValue());
            }
            else
            {
               doc = getDocumentForModification(pendingDocuments, updatedDocuments, propertyData.getQPath(), uuid);
               mimetype = doc.get(FieldNames.createPropertyFieldName(jcrMimeTypeFieldName));
               encoding = doc.get(FieldNames.createPropertyFieldName(jcrEncodingFieldName));

            }

            nodeIndexer.addDocumentProperty(doc, propertyData, mimetype, encoding);

         }
         // Open transaction
         final IndexTransaction<Document> indexTransaction =
            new LuceneIndexTransaction(pendingDocuments, updatedDocuments, deletedDocuments);

         if (pendingDocuments.size() == 0 && updatedDocuments.size() == 0 && deletedDocuments.size() == 0)
         {
            // there is no data to save
            return;
         }

         save(indexTransaction);

      }
      catch (final IOException e)
      {
         LOG.error("OnSaveItem exception " + e.getMessage(), e);
      }
      catch (final RepositoryException e)
      {
         LOG.error("OnSaveItem exception " + e.getMessage(), e);
      }
   }

   /**
    * Gets the document for modification.
    * 
    * @param pendingDocuments the pending documents
    * @param updatedDocuments the updated documents
    * @param uuid the uuid
    * @param nodePath the node path
    * @return the document for modification
    * 
    * @throws IOException Signals that an I/O exception has occurred.
    * @throws RepositoryException if any errors in CMIS repository occurs
    */
   private Document getDocumentForModification(final Map<String, Document> pendingDocuments,
      final Map<String, Document> updatedDocuments, final QPath nodePath, final String uuid)
      throws RepositoryException, IOException
   {
      // first of all in add cache
      Document doc = pendingDocuments.get(uuid);
      // updated cache next
      if (doc == null)
      {
         doc = updatedDocuments.get(uuid);
      }
      // not found in cache
      if (doc == null)
      {
         try
         {
            doc = crateDocumentFromPersistentStorage(uuid);
         }
         catch (UnsupportedEncodingException e)
         {
            throw new RuntimeException("Fail to create document uuid:" + uuid + " path" + nodePath.getAsString());
         }
         if (doc != null)
         {
            updatedDocuments.put(uuid, doc);
         }
         else
         {
            updatedDocuments.remove(uuid);
            pendingDocuments.remove(uuid);
         }
      }
      return doc;
   }

   /**
    * Checks if <code>itemData</code> belongs to the node with name
    * <code>Constants.JCR_CONTENT</code>.
    * 
    * @param itemData - ItemData to test.
    * @return Return true for node with name <code>Constants.JCR_CONTENT</code>
    *         and his properties.
    */
   private boolean isJcrContent(ItemData itemData)
   {
      if (itemData.isNode())
      {
         return Constants.JCR_CONTENT.equals(itemData.getQPath().getName());
      }

      QPathEntry[] entries = itemData.getQPath().getEntries();
      InternalQName parentName = entries[entries.length - 2];
      return Constants.JCR_CONTENT.equals(parentName);
   }

   /**
    * {@inheritDoc}
    */
   public boolean isTXAware()
   {
      return false;
   }
}
