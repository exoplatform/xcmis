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

package org.xcmis.sp.inmemory;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;
import org.xcmis.messaging.CmisRepositoryEntryType;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;
import org.xcmis.spi.RepositoriesManager;
import org.xcmis.spi.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RepositoriesManagerImpl.java 2 2010-02-04 17:21:49Z andrew00x $
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

   /** Set of available repositories. */
   private final Map<String, CMISRepositoryConfiguration> cmisRepositoryConfs;

   /** Set of available repositories. */
   private final Map<String, Repository> cmisRepositories;

   /**
    * Instantiates a new repositories manager impl.
    * 
    * @param initParams the init params
    */
   public RepositoriesManagerImpl(InitParams initParams)
   {
      this.cmisRepositoryConfs = new HashMap<String, CMISRepositoryConfiguration>();
      if (initParams != null)
      {
         ObjectParameter param = initParams.getObjectParam("configs");
         if (param == null)
            LOG.error("Init-params does not contain configuration for any CMIS repository.");
         ServiceConfig confs = (ServiceConfig)param.getObject();
         for (CMISRepositoryConfiguration conf : confs.getConfigs())
            cmisRepositoryConfs.put(conf.getId(), conf);
      }
      else
      {
         LOG.error("Not found configuration for any CMIS repository.");
      }
      this.cmisRepositories = new HashMap<String, Repository>();
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisRepositoryEntryType> getRepositories()
   {
      List<CmisRepositoryEntryType> list = new ArrayList<CmisRepositoryEntryType>();
      for (String repositoryId : cmisRepositoryConfs.keySet())
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
      Repository repository = cmisRepositories.get(repositoryId);
      if (repository != null)
      {
         return repository;
      }
      CMISRepositoryConfiguration repositoryConfiguration = cmisRepositoryConfs.get(repositoryId);
      if (repositoryConfiguration != null)
      {
         repository = new RepositoryImpl(repositoryConfiguration);
         cmisRepositories.put(repositoryId, repository);
         return repository;
      }
      return null;
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

   }

}
