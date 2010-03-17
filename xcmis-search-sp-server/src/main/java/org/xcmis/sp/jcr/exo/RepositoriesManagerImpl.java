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

package org.xcmis.sp.jcr.exo;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;
import org.xcmis.messaging.CmisRepositoryEntryType;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;
import org.xcmis.sp.jcr.exo.query.index.JcrIndexingService;
import org.xcmis.sp.jcr.exo.rendition.ImageRenditionProvider;
import org.xcmis.sp.jcr.exo.rendition.PDFDocumentRenditionProvider;
import org.xcmis.sp.jcr.exo.rendition.RenditionProvider;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.RepositoriesManager;
import org.xcmis.spi.Repository;
import org.xcmis.spi.object.RenditionManager;
import org.xcmis.spi.utils.MimeType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventListener;
import javax.jcr.observation.EventListenerIterator;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RepositoriesManagerImpl.java 282 2010-03-05 12:16:25Z ur3cma $
 */
public class RepositoriesManagerImpl implements RepositoriesManager, Startable
{

   /**
    * Holds the list of CMIS repository configuration.
    */
   public static class ServiceConfig
   {

      /**
       * The list of CMIS repository configuration.
       */
      private List<CMISRepositoryConfiguration> configs;
      
      

      /**
       * Get configurations.
       * 
       * @return the list of CMIS repository configuration
       */
      public List<CMISRepositoryConfiguration> getConfigs()
      {
         if (configs == null)
            configs = new ArrayList<CMISRepositoryConfiguration>();
         return configs;
      }

      /**
       * Set configurations.
       * 
       * @param configs the list of CMIS repository configuration
       */
      public void setConfigs(List<CMISRepositoryConfiguration> configs)
      {
         this.configs = configs;
      }
   }

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(RepositoriesManagerImpl.class);

   /** Back end repository service. */
   private final RepositoryService jcrRepositoryService;

   /** Set of available repositories. */
   private final Map<String, CMISRepositoryConfiguration> cmisRepositories;

   /** Session provider service. */
   private final ThreadLocalSessionProviderService sessionProviderService;
   
   /** The rendition manager. */
   private RenditionManager renditionManager;
   
   private Session session;

   /** The map for the rendition providers. */
   private final Map<MimeType, RenditionProvider> renditionProviders =
      new TreeMap<MimeType, RenditionProvider>(new Comparator<MimeType>()
      {
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

   /**
    * @param jcrRepositoryService the back end repository service
    * @param sessionProviderService the JCR session provider service
    * @param initParams the initial parameters
    */
   public RepositoriesManagerImpl(RepositoryService jcrRepositoryService,
      ThreadLocalSessionProviderService sessionProviderService, InitParams initParams)
   {
      this.jcrRepositoryService = jcrRepositoryService;
      this.sessionProviderService = sessionProviderService;
      this.indexingServices = new HashMap<String, JcrIndexingService>();
      this.cmisRepositories = new HashMap<String, CMISRepositoryConfiguration>();
      addRenditionProvider(new ImageRenditionProvider()); /* TODO : add form configuration ?? */
            addRenditionProvider(new PDFDocumentRenditionProvider()); /* TODO : add form configuration ?? */
      if (initParams != null)
      {
         ObjectParameter param = initParams.getObjectParam("configs");
         if (param == null)
            LOG.error("Init-params does not contain configuration for any CMIS repository.");
         ServiceConfig confs = (ServiceConfig)param.getObject();
         for (CMISRepositoryConfiguration conf : confs.getConfigs())
            cmisRepositories.put(conf.getId(), conf);
      }
      else
      {
         LOG.error("Not found configuration for any CMIS repository.");
      }
   }

   /** The map for indexing services where the key is a repository id. */
   private final Map<String, JcrIndexingService> indexingServices;

   /**
    * Adding the index service.
    * 
    * @param repositoryId the repository id
    * @param indexService the indexing service
    */
   public void addIndexService(String repositoryId, JcrIndexingService indexService)
   {
      indexingServices.put(repositoryId, indexService);
   }

   /**
    * Add the rendition provider.
    * 
    * @param renditionProvider rendition provider to be added
    */
   public void addRenditionProvider(RenditionProvider renditionProvider)
   {
      for (String mimeType : renditionProvider.getSupportedMediaType())
         renditionProviders.put(MimeType.fromString(mimeType), renditionProvider);
   }

   /**
    * Remove the index service.
    * 
    * @param repositoryId the repository id
    */
   public void removeIndexService(String repositoryId)
   {
      indexingServices.remove(repositoryId);
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisRepositoryEntryType> getRepositories()
   {
      List<CmisRepositoryEntryType> list = new ArrayList<CmisRepositoryEntryType>();
      for (String repositoryId : cmisRepositories.keySet())
      {
         CmisRepositoryEntryType entry = new CmisRepositoryEntryType();
         entry.setRepositoryId(repositoryId);
         entry.setRepositoryName(repositoryId);
         list.add(entry);
      }
      return list;
   }

   /**
    * {@inheritDoc}
    */
   public Repository getRepository(String repositoryId)
   {
      CMISRepositoryConfiguration repositoryConfiguration = cmisRepositories.get(repositoryId);
      if (repositoryConfiguration != null)
      {
         try
         {
            javax.jcr.Repository jcrRepository =
               jcrRepositoryService.getRepository(repositoryConfiguration.getRepository());
            SessionProvider sessionProvider = sessionProviderService.getSessionProvider(null);
            // --- FIXME : Should never happen if environment is correct.
            // Top services must prepare session provider.
            // But need it for commons test environment at the moment.
            //                       if (sessionProvider == null)
            //                          sessionProvider = SessionProvider.createAnonimProvider();
            //                        sessionProviderService.setSessionProvider(null, sessionProvider);
            // ----
            if (renditionManager == null)
               renditionManager  =  new RenditionManagerImpl(renditionProviders, session);
               
            return new RepositoryImpl(jcrRepository, sessionProvider, indexingServices.get(repositoryId),
               repositoryConfiguration,renditionManager);
         }
         catch (javax.jcr.RepositoryException re)
         {
            LOG.error("Unable to get repository. " + re.getMessage());
         }
         catch (RepositoryConfigurationException rce)
         {
            LOG.error("Unable to get repository. " + rce.getMessage());
         }
      }
      return null;
   }

   /**
    * Get repository by specified JCR repository name and workspace name.
    * Result is CMIS repository that is wrapper around JCR stuff.
    * 
    * @param jcrRepositoryId the repository name
    * @param jcrWorkspaceId the workspace name
    * @return repository or null
    * @throws javax.jcr.RepositoryException if any repository exception occurs
    * @throws RepositoryConfigurationException if any configuration exception occurs
    */
   // TODO : Can get smarter ??
   public Repository getRepository(String jcrRepositoryId, String jcrWorkspaceId) throws javax.jcr.RepositoryException,
      RepositoryConfigurationException
   {
      for (Map.Entry<String, CMISRepositoryConfiguration> entry : cmisRepositories.entrySet())
      {
         CMISRepositoryConfiguration repositoryConfiguration = entry.getValue();
         if (repositoryConfiguration.getRepository().equals(jcrRepositoryId)
            && repositoryConfiguration.getWorkspace().equals(jcrWorkspaceId))
         {
            javax.jcr.Repository jcrRepository = jcrRepositoryService.getRepository(jcrRepositoryId);
            SessionProvider sessionProvider = sessionProviderService.getSessionProvider(null);

            if (sessionProvider == null)
               sessionProvider = SessionProvider.createSystemProvider();
            sessionProviderService.setSessionProvider(null, sessionProvider);
            
            if (renditionManager == null)
               renditionManager  =  new RenditionManagerImpl(renditionProviders, session);
            
            return new RepositoryImpl(jcrRepository, sessionProvider, indexingServices.get(repositoryConfiguration
               .getId()), repositoryConfiguration, renditionManager);
         }
      }
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public void start()
   {
      SessionProvider systemProvider = SessionProvider.createSystemProvider();
    
      try
      {
         for (CMISRepositoryConfiguration cmisRepositoryConfiguration : cmisRepositories.values())
         {
            ManageableRepository repository =
               jcrRepositoryService.getRepository(cmisRepositoryConfiguration.getRepository());
            session = systemProvider.getSession(cmisRepositoryConfiguration.getWorkspace(), repository);
            Node root = session.getRootNode();
            Node cmisSystem = null;
            if (!root.hasNode(JcrCMIS.CMIS_SYSTEM))
               cmisSystem = root.addNode(JcrCMIS.CMIS_SYSTEM, JcrCMIS.CMIS_SYSTEM_NODETYPE);
            else
               cmisSystem = root.getNode(JcrCMIS.CMIS_SYSTEM);
            
            if (!cmisSystem.hasNode(JcrCMIS.CMIS_RELATIONSHIPS))
            {
               cmisSystem.addNode(JcrCMIS.CMIS_RELATIONSHIPS, JcrCMIS.NT_UNSTRUCTURED);
               if (LOG.isDebugEnabled())
                  LOG.debug("CMIS relationships storage " + JcrCMIS.CMIS_RELATIONSHIPS + " created.");
            }
            if (!cmisSystem.hasNode(JcrCMIS.CMIS_WORKING_COPIES))
            {
               cmisSystem.addNode(JcrCMIS.CMIS_WORKING_COPIES);
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
               LOG.error("Ubable to create event listener, " + ex.getMessage());
            }
         }

      }
      catch (RepositoryConfigurationException rce)
      {
         LOG.error("Unable to initialize CMIS repository", rce);
      }
      catch (javax.jcr.RepositoryException re)
      {
         LOG.error("Unable to initialize CMIS repository", re);
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

}
