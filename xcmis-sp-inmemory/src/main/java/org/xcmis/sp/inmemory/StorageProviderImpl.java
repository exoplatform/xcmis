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
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;
import org.xcmis.spi.Connection;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.PermissionService;
import org.xcmis.spi.RenditionManager;
import org.xcmis.spi.StorageProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class StorageProviderImpl implements StorageProvider
{

   private static final Log LOG = ExoLogger.getLogger(StorageProviderImpl.class);

   private StorageImpl storageImpl = null;

   private StorageConfiguration storageConfig = null;

   private RenditionManager renditionManager;

   public StorageProviderImpl(InitParams initParams)
   {
      if (initParams != null)
      {
         ObjectParameter param = initParams.getObjectParam("configs");

         if (param == null)
         {
            LOG.error("Init-params does not contain configuration for any CMIS repository.");
         }

         StorageProviderConfig confs = (StorageProviderConfig)param.getObject();
         this.storageConfig = confs.getStorage();
         this.renditionManager = RenditionManager.getInstance();
         this.storageImpl = new StorageImpl(storageConfig, renditionManager,
          new PermissionService());
      }
      else
      {
         LOG.error("Not found configuration for any storages.");
      }
   }
   
   public StorageProviderImpl(String repositoryId, String repositoryName, String description, String maxStorageMemSize,
      String maxItemsNumber)
   {
      this.storageConfig =
         new StorageConfiguration(repositoryId, repositoryName, description, maxStorageMemSize, maxItemsNumber);
      this.renditionManager = RenditionManager.getInstance();
      this.storageImpl = new StorageImpl(storageConfig, renditionManager, new PermissionService());
   }
   
   public Connection getConnection()
   {
      if (storageImpl == null)
      {
         throw new InvalidArgumentException("CMIS repository does not exist.");
      }

      return new InmemConnection(storageImpl);
   }

   public String getStorageID()
   {
      return storageConfig.getId();
   }

   /**
    * @see org.picocontainer.Startable#stop()
    */
   public void stop()
   {

   }

   public static class StorageProviderConfig
   {

      /**
       * The storage configuration.
       */
      private StorageConfiguration storage;

      /**
       * @return the storage configuration
       */
      public StorageConfiguration getStorage()
      {
         return storage;
      }

      /**
       * @param configs storage configuration
       */
      public void setStorage(StorageConfiguration storage)
      {
         this.storage = storage;
      }
   }

}
