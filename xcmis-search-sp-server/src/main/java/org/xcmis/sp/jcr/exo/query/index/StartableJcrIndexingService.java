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

import org.exoplatform.services.document.DocumentReaderService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.config.RepositoryEntry;
import org.exoplatform.services.jcr.config.WorkspaceEntry;
import org.exoplatform.services.jcr.core.NamespaceAccessor;
import org.exoplatform.services.jcr.core.nodetype.NodeTypeDataManager;
import org.exoplatform.services.jcr.dataflow.ItemStateChangesLog;
import org.exoplatform.services.jcr.datamodel.NodeData;
import org.exoplatform.services.jcr.impl.Constants;
import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.exoplatform.services.jcr.impl.dataflow.persistent.WorkspacePersistentDataManager;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.lucene.index.IndexException;
import org.xcmis.sp.jcr.exo.RepositoriesManagerImpl;
import org.xcmis.sp.jcr.exo.RepositoryImpl;
import org.xcmis.spi.RepositoriesManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: StartableJcrIndexingService.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class StartableJcrIndexingService extends JcrIndexingService
{
   /**
    * File name. If this file exists, that means reindex was interrupted, so need
    * new reindex.
    */
   private static final String REINDEX_RUN = "reindexProcessing";

   /** ChangesLog Buffer (used for saves before start). */
   private List<ItemStateChangesLog> changesLogBuffer = new ArrayList<ItemStateChangesLog>();

   /** The index dir. */
   private final File indexDir;

   /** The index restore service. */
   private final JcrIndexRestoreService indexRestoreService;

   /** Is started flag. */
   private boolean isStarted = false;

   /** Class logger. */
   private static final Log LOG = ExoLogger.getLogger(StartableJcrIndexingService.class);

   /** The node indexer. */
   private final IndependentNodeIndexer nodeIndexer;

   /** The workspace persistent data manager. */
   private final WorkspacePersistentDataManager workspacePersistentDataManager;

   /** The cmis repository id. */
   private final String cmisRepositoryId;

   private final RepositoriesManagerImpl repositoriesManager;

   /**
    * Instantiates a new startable jcr indexing service.
    * 
    * @param repositoryEntry the repository entry
    * @param workspaceEntry the workspace entry
    * @param cmisRepositoriesManager the cmis repositories manager
    * @param workspacePersistentDataManager the workspace persistent data manager
    * @param namespaceAccessor the namespace accessor
    * @param nodeTypeDataManager the node type data manager
    * @param extractor the extractor
    * @param repositoriesManager the CMIS repositories manager
    * 
    * @throws RepositoryConfigurationException the repository configuration exception
    * @throws RepositoryException the repository exception
    * @throws IndexException 
    */
   public StartableJcrIndexingService(final RepositoryEntry repositoryEntry, final WorkspaceEntry workspaceEntry,
      final RepositoriesManager cmisRepositoriesManager,
      final WorkspacePersistentDataManager workspacePersistentDataManager, final NamespaceAccessor namespaceAccessor,
      final NodeTypeDataManager nodeTypeDataManager, final DocumentReaderService extractor,
      final RepositoriesManagerImpl repositoriesManager) throws RepositoryConfigurationException, RepositoryException,
      IndexException
   {
      super(repositoryEntry, workspaceEntry, cmisRepositoriesManager, workspacePersistentDataManager,
         namespaceAccessor, nodeTypeDataManager, extractor);

      this.workspacePersistentDataManager = workspacePersistentDataManager;
      this.repositoriesManager = repositoriesManager;
      //this.workspacePersistentDataManager.addItemPersistenceListener(this);
      this.nodeIndexer = new IndependentNodeIndexer(namespaceAccessor, extractor);
      this.indexRestoreService =
         new JcrIndexRestoreService(this.workspacePersistentDataManager, new LocationFactory(namespaceAccessor),
            this.nodeIndexer, this);

      RepositoryImpl cmisRepository =
         (RepositoryImpl)((RepositoriesManagerImpl)cmisRepositoriesManager).getRepository(repositoryEntry.getName(),
            workspaceEntry.getName());
      this.cmisRepositoryId = cmisRepository.getId();

      indexDir = new File(cmisRepository.getRepositoryConfiguration().getIndexConfiguration().getIndexDir());
      if (!indexDir.exists() && !indexDir.mkdirs())
      {
         throw new IndexException("Fail to create index directory : " + indexDir.getAbsolutePath());
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onSaveItems(final ItemStateChangesLog itemStates)
   {
      if (!isStarted)
      {
         changesLogBuffer.add(itemStates);
      }
      else
      {
         super.onSaveItems(itemStates);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void start()
   {
      super.start();
      //repositoriesManager.addIndexService(cmisRepositoryId, this);
      // save buffered logs
      isStarted = true;
      for (final ItemStateChangesLog bufferedChangesLog : changesLogBuffer)
      {
         super.onSaveItems(bufferedChangesLog);
      }

      try
      {
         if (needIndexRestore())
         {
            // need restore index;
            try
            {
               restoreIndex();
            }
            catch (final IOException e)
            {
               LOG.error("Restore IOException occures " + e.getMessage(), e);
            }
         }
      }
      catch (final RepositoryException e)
      {
         LOG.error(e.getMessage(), e);
      }
      catch (IndexException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      changesLogBuffer.clear();
      changesLogBuffer = null;

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void stop()
   {
      super.stop();
      repositoriesManager.removeIndexService(cmisRepositoryId);
      workspacePersistentDataManager.removeItemPersistenceListener(this);
   }

   /**
    * Do we need restore index.
    * 
    * @return <code>true</code> if index not exist or restore was interrupted.
    * @throws IndexException if index read exception occurs.
    */
   private boolean needIndexRestore() throws IndexException
   {

      if (getDocumentCount() == 0)
      {
         return true;
      }
      final File flag = new File(indexDir, StartableJcrIndexingService.REINDEX_RUN);
      return flag.exists();

   }

   //   private boolean removeDirContent(final File dir)
   //   {
   //      boolean result = true;
   //      for (final File file : dir.listFiles())
   //      {
   //         if (file.isDirectory())
   //         {
   //            result &= removeDirContent(file);
   //         }
   //         result &= file.delete();
   //      }
   //      return result;
   //   }

   /**
    * Restore Index from full repository content.
    * 
    * @throws RepositoryException - restore exception
    * @throws IOException if reindex flag file was not created or was'nt removed.
    * @throws IndexException 
    */
   private void restoreIndex() throws RepositoryException, IOException, IndexException
   {

      if (workspacePersistentDataManager == null)
      {
         LOG.warn("Restore index can't be started - item manager is null.");
      }
      else
      {

         if (LOG.isDebugEnabled())
         {
            LOG.info("Restore index started.");
         }

         // clean TransactionLogs ??
         // no logs should be here
         // if (!removeDirContent(indexDir)) {
         // LOG.warn("Some files of out of date logs was not removed.");
         // }

         final File flag = new File(indexDir, StartableJcrIndexingService.REINDEX_RUN);
         if (!flag.exists())
         {
            if (!flag.createNewFile())
            {
               throw new IOException("Reindex flag file was not created.");
            }
         }

         // clean persisted index
         softCleanIndex();

         // get root node
         final NodeData root = (NodeData)workspacePersistentDataManager.getItemData(Constants.ROOT_UUID);
         // restore index from full content
         root.accept(this.indexRestoreService);

         if (!flag.delete())
         {
            throw new IOException("Can't remove reindex flag.");
         }

         // clean changesLogBuffer
         // all states in changesLogBuffer already indexed by IndexRestoreVisitor,
         // so it contains duplicated data
         changesLogBuffer.clear();

         if (LOG.isDebugEnabled())
         {
            LOG.info("Restore index finished.");
         }
      }
   }

}
