/*
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

package org.xcmis.sp.jcr.exo;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;
import org.exoplatform.services.document.DocumentReaderService;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.core.CredentialsImpl;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;
import org.xcmis.search.SearchService;
import org.xcmis.search.SearchServiceException;
import org.xcmis.search.config.IndexConfiguration;
import org.xcmis.search.config.SearchServiceConfiguration;
import org.xcmis.search.content.command.InvocationContext;
import org.xcmis.search.value.SlashSplitter;
import org.xcmis.search.value.ToStringNameConverter;
import org.xcmis.sp.jcr.exo.index.CmisContentReader;
import org.xcmis.sp.jcr.exo.index.CmisSchema;
import org.xcmis.sp.jcr.exo.index.CmisSchemaTableResolver;
import org.xcmis.sp.jcr.exo.index.IndexListener;
import org.xcmis.spi.RenditionProvider;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.Connection;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.Storage;
import org.xcmis.spi.StorageProvider;
import org.xcmis.spi.utils.MimeType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.EventListenerIterator;
import javax.security.auth.login.LoginException;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class StorageProviderImpl implements StorageProvider, Startable
{

   public static class StorageProviderConfig
   {

      /**
       * The list of storages configuration.
       */
      private List<StorageConfiguration> storages;

      /** Rendition providers. */
      private List<Object> renditionProviders;

      /**
       * @return the list of storages configuration
       */
      public List<StorageConfiguration> getStorages()
      {
         if (storages == null)
         {
            storages = new ArrayList<StorageConfiguration>();
         }
         return storages;
      }

      /**
       * Get renditionProviders.
       *
       * @return renditionProviders .
       */

      public List<Object> getRenditionProviders()
      {
         return renditionProviders;
      }

      /**
       * @param storages the list of storages configuration
       */
      public void setStorages(List<StorageConfiguration> storages)
      {
         this.storages = storages;
      }

      /**
       * Set rendition providers.
       *
       * @param renditionProviders list renditionProviders
       */
      public void setRenditionProviders(List<Object> renditionProviders)
      {
         this.renditionProviders = renditionProviders;
      }
   }

   private static final Log LOG = ExoLogger.getLogger(StorageProviderImpl.class);

   private final RepositoryService repositoryService;

   private final DocumentReaderService documentReaderService;

   private final Map<String, StorageConfiguration> storageConfigs = new HashMap<String, StorageConfiguration>();

   private final Map<String, SearchService> searchServices = new HashMap<String, SearchService>();

   Map<MimeType, RenditionProvider> renditionProviders =
      new TreeMap<MimeType, RenditionProvider>(new Comparator<MimeType>()
      {
         public int compare(MimeType m1, MimeType m2)
         {
            if (m1.getType().equals(CmisConstants.WILDCARD) && !m2.getType().equals(CmisConstants.WILDCARD))
            {
               return 1;
            }
            if (!m1.getType().equals(CmisConstants.WILDCARD) && m2.getType().equals(CmisConstants.WILDCARD))
            {
               return -1;
            }
            if (m1.getSubType().equals(CmisConstants.WILDCARD) && !m2.getSubType().equals(CmisConstants.WILDCARD))
            {
               return 1;
            }
            if (!m1.getSubType().equals(CmisConstants.WILDCARD) && m2.getSubType().equals(CmisConstants.WILDCARD))
            {
               return -1;
            }
            return m1.toString().compareToIgnoreCase(m2.toString());
         }
      });

   public StorageProviderImpl(RepositoryService repositoryService, InitParams initParams,
      DocumentReaderService documentReaderService)
   {
      this.repositoryService = repositoryService;
      this.documentReaderService = documentReaderService;

      if (initParams != null)
      {
         ObjectParameter param = initParams.getObjectParam("configs");

         if (param == null)
         {
            LOG.error("Init-params does not contain configuration for any CMIS repository.");
         }

         StorageProviderConfig confs = (StorageProviderConfig)param.getObject();

         for (StorageConfiguration conf : confs.getStorages())
         {
            storageConfigs.put(conf.getId(), conf);
         }

//         if (confs.getRenditionProviders() != null)
//         {
//            addRenditionProviders(confs.getRenditionProviders());
//         }
      }
      else
      {
         LOG.error("Not found configuration for any storages.");
      }

   }

   /**
    * {@inheritDoc}
    */
   public Connection getConnection(String id)
   {
      StorageConfiguration configuration = storageConfigs.get(id);

      if (configuration == null)
      {
         throw new InvalidArgumentException("CMIS repository '" + id + "' does not exist.");
      }

      String repositoryId = configuration.getRepository();
      String ws = configuration.getWorkspace();

      try
      {
         ManageableRepository repository = repositoryService.getRepository(repositoryId);
         Session session = repository.login(ws);
         RenditionManagerImpl renditionManager = new RenditionManagerImpl(renditionProviders);

         SearchService searchService = getSearchService(id);
         Storage storage = new QueryableStorage(session, configuration, renditionManager, searchService);
         IndexListener indexListener = new IndexListener(storage, searchService);
         //TODO make this method public
         ((StorageImpl)storage).setIndexListener(indexListener);

         return new JcrConnection(storage);

      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get CMIS repository " + id + ". " + re.getMessage(), re);
      }
      catch (RepositoryConfigurationException rce)
      {
         throw new CmisRuntimeException("Unable get CMIS repository " + id + ". " + rce.getMessage(), rce);
      }
      catch (SearchServiceException rce)
      {
         throw new CmisRuntimeException("Unable get CMIS repository " + id + ". " + rce.getMessage(), rce);
      }
   }

   public Connection getConnection(String id, String user, String password) throws LoginException,
      InvalidArgumentException
   {
      StorageConfiguration configuration = storageConfigs.get(id);

      if (configuration == null)
      {
         throw new InvalidArgumentException("CMIS repository '" + id + "' does not exist.");
      }

      String repositoryId = configuration.getRepository();
      String ws = configuration.getWorkspace();

      try
      {
         ManageableRepository repository = repositoryService.getRepository(repositoryId);
         Credentials credentials = new CredentialsImpl(user, password.toCharArray());
         Session session = repository.login(credentials, ws);
         SearchService searchService = getSearchService(id);
         Storage storage = new QueryableStorage(session, configuration, searchService);
         IndexListener indexListener = new IndexListener(storage, searchService);
         //TODO make this method public
         ((StorageImpl)storage).setIndexListener(indexListener);
         return new JcrConnection(storage);
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get CMIS repository " + id + ". " + re.getMessage(), re);
      }
      catch (RepositoryConfigurationException rce)
      {
         throw new CmisRuntimeException("Unable get CMIS repository " + id + ". " + rce.getMessage(), rce);
      }
      catch (SearchServiceException rce)
      {
         throw new CmisRuntimeException("Unable get CMIS repository " + id + ". " + rce.getMessage(), rce);
      }
   }

   /**
    * Gets the search service. 
    * @return instance of {@link SearchService}
    * @throws SearchServiceException
    */
   private SearchService getSearchService(String id) throws SearchServiceException
   {
      return searchServices.get(id);
   }

   /**
    * {@inheritDoc}
    */
   public Set<String> getStorageIDs()
   {
      return Collections.unmodifiableSet(storageConfigs.keySet());
   }

   /**
    * {@inheritDoc}
    */
   public void start()
   {
      SessionProvider systemProvider = SessionProvider.createSystemProvider();

      try
      {
         for (Entry<String, StorageConfiguration> entry : storageConfigs.entrySet())
         {
            StorageConfiguration cmisRepositoryConfiguration = entry.getValue();
            ManageableRepository repository =
               repositoryService.getRepository(cmisRepositoryConfiguration.getRepository());

            Session session = systemProvider.getSession(cmisRepositoryConfiguration.getWorkspace(), repository);

            Node root = session.getRootNode();

            Node xCmisSystem = session.itemExists(StorageImpl.XCMIS_SYSTEM_PATH) //
               ? (Node)session.getItem(StorageImpl.XCMIS_SYSTEM_PATH) //
               : root.addNode(StorageImpl.XCMIS_SYSTEM_PATH.substring(1), "xcmis:system");

            if (!xCmisSystem.hasNode(StorageImpl.XCMIS_UNFILED))
            {
               xCmisSystem.addNode(StorageImpl.XCMIS_UNFILED, "xcmis:unfiled");
               if (LOG.isDebugEnabled())
               {
                  LOG.debug("CMIS unfiled storage " + StorageImpl.XCMIS_SYSTEM_PATH + "/" + StorageImpl.XCMIS_UNFILED
                     + " created.");
               }
            }

            if (!xCmisSystem.hasNode(StorageImpl.XCMIS_WORKING_COPIES))
            {
               xCmisSystem.addNode(StorageImpl.XCMIS_WORKING_COPIES, "xcmis:workingCopies");
               if (LOG.isDebugEnabled())
               {
                  LOG.debug("CMIS Working Copies store " + StorageImpl.XCMIS_SYSTEM_PATH + "/"
                     + StorageImpl.XCMIS_WORKING_COPIES + " created.");
               }
            }

            if (!xCmisSystem.hasNode(StorageImpl.XCMIS_RELATIONSHIPS))
            {
               xCmisSystem.addNode(StorageImpl.XCMIS_RELATIONSHIPS, "xcmis:relationships");
               if (LOG.isDebugEnabled())
               {
                  LOG.debug("CMIS relationship store " + StorageImpl.XCMIS_SYSTEM_PATH + "/"
                     + StorageImpl.XCMIS_RELATIONSHIPS + " created.");
               }
            }

            if (!xCmisSystem.hasNode(StorageImpl.XCMIS_POLICIES))
            {
               xCmisSystem.addNode(StorageImpl.XCMIS_POLICIES, "xcmis:policies");
               if (LOG.isDebugEnabled())
               {
                  LOG.debug("CMIS policies store " + StorageImpl.XCMIS_SYSTEM_PATH + "/" + StorageImpl.XCMIS_POLICIES
                     + " created.");
               }
            }

            session.save();

            Workspace workspace = session.getWorkspace();

            try
            {
               EventListenerIterator it = workspace.getObservationManager().getRegisteredEventListeners();
               boolean exist = false;
               while (it.hasNext())
               {
                  EventListener one = it.nextEventListener();
                  if (one.getClass() == UpdateListener.class)
                  {
                     exist = true;
                  }
               }

               if (!exist)
               {
                  workspace.getObservationManager().addEventListener(
                     new UpdateListener(repository, cmisRepositoryConfiguration.getWorkspace(), renditionProviders),
                     Event.NODE_ADDED | Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED, "/", true, null,
                     new String[]{JcrCMIS.NT_FILE, JcrCMIS.NT_RESOURCE}, false);
               }
            }
            catch (Exception ex)
            {
               LOG.error("Unable to create event listener, " + ex.getMessage());
            }
            //prepare search service
            StorageImpl storage = new StorageImpl(session, cmisRepositoryConfiguration);
            CmisSchema schema = new CmisSchema(storage);
            CmisSchemaTableResolver tableResolver =
               new CmisSchemaTableResolver(new ToStringNameConverter(), schema, storage);

            IndexConfiguration indexConfiguration = cmisRepositoryConfiguration.getIndexConfiguration();
            indexConfiguration.setRootUuid(storage.getRepositoryInfo().getRootFolderId());
            //if list of root parents is empty it will be indexed as empty string
            indexConfiguration.setRootParentUuid("");
            indexConfiguration.setDocumentReaderService(documentReaderService);

            //default invocation context
            InvocationContext invocationContext = new InvocationContext();
            invocationContext.setNameConverter(new ToStringNameConverter());

            invocationContext.setSchema(schema);
            invocationContext.setPathSplitter(new SlashSplitter());

            invocationContext.setTableResolver(tableResolver);

            SearchServiceConfiguration configuration = new SearchServiceConfiguration();
            configuration.setIndexConfiguration(indexConfiguration);
            configuration.setContentReader(new CmisContentReader(storage));
            configuration.setNameConverter(new ToStringNameConverter());
            configuration.setDefaultInvocationContext(invocationContext);
            configuration.setTableResolver(tableResolver);
            configuration.setPathSplitter(new SlashSplitter());

            SearchService searchService = new SearchService(configuration);
            searchService.start();

            //attach listener to the created storage
            IndexListener indexListener = new IndexListener(storage, searchService);
            storage.setIndexListener(indexListener);

            searchServices.put(entry.getKey(), searchService);

         }

      }
      catch (RepositoryConfigurationException rce)
      {
         LOG.error("Unable to initialize storage. ", rce);
      }
      catch (javax.jcr.RepositoryException re)
      {
         LOG.error("Unable to initialize storage. ", re);
      }
      catch (SearchServiceException e)
      {
         LOG.error("Unable to initialize storage. ", e);
      }
      finally
      {
         systemProvider.close();
      }
   }

   /**
    * {@inheritDoc}
    */
   public void stop()
   {
      for (SearchService searchService : searchServices.values())
      {
         searchService.stop();
      }
   }

   public StorageConfiguration getStorageConfiguration(String id)
   {
      return storageConfigs.get(id);
   }

   public void addRenditionProvider(Object prov)
   {
         for (String mimeType : ((RenditionProvider)prov).getSupportedMediaType())
         {
            renditionProviders.put(MimeType.fromString(mimeType), (RenditionProvider)prov);
         }
     
   }
}
