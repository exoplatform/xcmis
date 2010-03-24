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
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.core.CredentialsImpl;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.picocontainer.Startable;

import org.xcmis.sp.jcr.exo.rendition.ImageRenditionProvider;
import org.xcmis.sp.jcr.exo.rendition.PDFDocumentRenditionProvider;
import org.xcmis.sp.jcr.exo.rendition.RenditionProvider;
import org.xcmis.spi.CMIS;

import org.xcmis.sp.jcr.exo.index.IndexListenerFactory;

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
      private List<StorageConfiguration> configs;

      /**
       * @return the list of storages configuration
       */
      public List<StorageConfiguration> getConfigs()
      {
         if (configs == null)
            configs = new ArrayList<StorageConfiguration>();
         return configs;
      }

      /**
       * @param configs the list of storages configuration
       */
      public void setConfigs(List<StorageConfiguration> configs)
      {
         this.configs = configs;
      }
   }

   private static final Log LOG = ExoLogger.getLogger(StorageProviderImpl.class);

   private final RepositoryService repositoryService;
   
   private RenditionManagerImpl renditionManager;

   private final Map<String, StorageConfiguration> storages = new HashMap<String, StorageConfiguration>();
   
   /** The map for the rendition providers. */
   private final Map<MimeType, RenditionProvider> renditionProviders =
      new TreeMap<MimeType, RenditionProvider>(new Comparator<MimeType>(){
         public int compare(MimeType m1, MimeType m2)
         {
            if (m1.getType().equals(CMIS.WILDCARD) && !m2.getType().equals(CMIS.WILDCARD))
               return 1;
            if (!m1.getType().equals(CMIS.WILDCARD) && m2.getType().equals(CMIS.WILDCARD))
               return -1;
            if (m1.getSubType().equals(CMIS.WILDCARD) && !m2.getSubType().equals(CMIS.WILDCARD))
               return 1;
            if (!m1.getSubType().equals(CMIS.WILDCARD) && m2.getSubType().equals(CMIS.WILDCARD))
               return -1;
            return m1.toString().compareToIgnoreCase(m2.toString());
         }
      });

   private final IndexListenerFactory indexListenerFactory;

   public StorageProviderImpl(RepositoryService repositoryService, IndexListenerFactory indexListenerFactory,
      InitParams initParams)
   {
      this.repositoryService = repositoryService;
      this.indexListenerFactory = indexListenerFactory;

      if (initParams != null)
      {
         ObjectParameter param = initParams.getObjectParam("configs");

         if (param == null)
            LOG.error("Init-params does not contain configuration for any CMIS repository.");

         StorageProviderConfig confs = (StorageProviderConfig)param.getObject();

         for (StorageConfiguration conf : confs.getConfigs())
            storages.put(conf.getId(), conf);
      }
      else
      {
         LOG.error("Not found configuration for any storages.");
      }
      
      addRenditionProvider(new ImageRenditionProvider()); /* TODO : add form configuration ?? */
      addRenditionProvider(new PDFDocumentRenditionProvider()); /* TODO : add form configuration ?? */

   }

   /**
    * {@inheritDoc}
    */
   public Connection getConnection(String id, ConversationState conversation)
   {
      StorageConfiguration configuration = storages.get(id);

      if (configuration == null)
         throw new InvalidArgumentException("CMIS repository " + id + " does not exists.");

      String repositoryId = configuration.getRepository();
      String ws = configuration.getWorkspace();

      try
      {
         ManageableRepository repository = repositoryService.getRepository(repositoryId);
         Session session = repository.login(ws);

         
         if (renditionManager == null)
            renditionManager  =  new RenditionManagerImpl(renditionProviders, session);
         
         Storage storage = new StorageImpl(session, indexListenerFactory.getIndexListener(id), configuration,renditionManager);
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
   }

   public Connection getConnection(String id, String user, String password) throws LoginException,
      InvalidArgumentException
   {
      StorageConfiguration configuration = storages.get(id);

      if (configuration == null)
         throw new InvalidArgumentException("CMIS repository " + id + " does not exists.");

      String repositoryId = configuration.getRepository();
      String ws = configuration.getWorkspace();

      try
      {
         ManageableRepository repository = repositoryService.getRepository(repositoryId);
         Credentials credentials = new CredentialsImpl(user, password.toCharArray());
         Session session = repository.login(credentials, ws);
         Storage storage = new StorageImpl(session, indexListenerFactory.getIndexListener(id), configuration);
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
   }

   /**
    * {@inheritDoc}
    */
   public Set<String> getStorageIDs()
   {
      return Collections.unmodifiableSet(storages.keySet());
   }

   /**
    * {@inheritDoc}
    */
   public void start()
   {
      SessionProvider systemProvider = SessionProvider.createSystemProvider();

      try
      {
         for (StorageConfiguration cmisRepositoryConfiguration : storages.values())
         {
            ManageableRepository repository =
               repositoryService.getRepository(cmisRepositoryConfiguration.getRepository());

            Session session = systemProvider.getSession(cmisRepositoryConfiguration.getWorkspace(), repository);

            Node root = session.getRootNode();

            Node cmisSystem = root.hasNode(JcrCMIS.CMIS_SYSTEM) //
               ? root.getNode(JcrCMIS.CMIS_SYSTEM) //
               : root.addNode(JcrCMIS.CMIS_SYSTEM, JcrCMIS.CMIS_SYSTEM_NODETYPE);

            if (!cmisSystem.hasNode(JcrCMIS.CMIS_RELATIONSHIPS))
            {
               cmisSystem.addNode(JcrCMIS.CMIS_RELATIONSHIPS, JcrCMIS.NT_UNSTRUCTURED);
               if (LOG.isDebugEnabled())
                  LOG.debug("CMIS relationships storage " + JcrCMIS.CMIS_RELATIONSHIPS + " created.");
            }

            // TODO
            if (!cmisSystem.hasNode("xcmis:unfiled"))
            {
               cmisSystem.addNode("xcmis:unfiled", JcrCMIS.NT_UNSTRUCTURED);
            }

            if (!cmisSystem.hasNode(JcrCMIS.CMIS_WORKING_COPIES))
            {
               cmisSystem.addNode(JcrCMIS.CMIS_WORKING_COPIES, JcrCMIS.NT_UNSTRUCTURED);
               if (LOG.isDebugEnabled())
                  LOG.debug("CMIS Working Copies storage " + JcrCMIS.CMIS_WORKING_COPIES + " created.");
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
                     exist = true;
               }

               if (!exist)
                  workspace.getObservationManager().addEventListener(
                     new UpdateListener((ManageableRepository)repository, cmisRepositoryConfiguration.getWorkspace(),
                        renditionProviders),
                        Event.NODE_ADDED | Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED | Event.PROPERTY_REMOVED, "/",
                        true, null, new String[]{JcrCMIS.NT_FILE}, false);
            }
            catch (Exception ex)
            {
               LOG.error("Unable to create event listener, " + ex.getMessage());
            }
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
   }

   public StorageConfiguration getStorageConfiguration(String id)
   {
      return storages.get(id);
   }

   /**
    * Add the rendition provider.
    * 
    * @param renditionProvider rendition provider to be added
    */
   private void addRenditionProvider(RenditionProvider renditionProvider)
   {
      for (String mimeType : renditionProvider.getSupportedMediaType())
         renditionProviders.put(MimeType.fromString(mimeType), renditionProvider);
   }
}
