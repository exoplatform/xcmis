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

package org.xcmis.spi;

import org.xcmis.spi.model.RepositoryShortInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

/**
 * xCMIS SPI entry point.
 *
 * Contains list of all known CMIS StorageProviders with convenience addStorage
 * method for theirs initializing as well as the method getConnection for
 * accessing data from concrete Storage (visible on top level as CMIS
 * Repository)
 *
 * @version $Id:$
 */

public class CmisRegistry
{

   static final String XCMIS_REGISTRY_FACTORY = "org.xcmis.CmisRegistryFactory";

   private static class CmisRegistryFactory0 implements CmisRegistryFactory
   {

      CmisRegistry registry;

      public CmisRegistryFactory0(CmisRegistry registry)
      {
         this.registry = registry;
      }

      public CmisRegistry getRegistry()
      {
         return registry;
      }
   };

   private static AtomicReference<CmisRegistryFactory> crfs = new AtomicReference<CmisRegistryFactory>();

   /**
    * Singleton method for obtaining "current" CmisRegistry object stored in
    * AtomicReference-ed CmisRegistryFactory. Such a factory should be set by
    * compilmentary (in some sence) setFactory(CmisRegistryFactory inst) method
    * in advance.
    *
    * Otherwise, it will try to find it using
    * CmisRegistryFactoryFinder.findCmisRegistry()
    *
    * @return "current" CmisRegistry
    */
   public static CmisRegistry getInstance()
   {
      CmisRegistryFactory crf = crfs.get();
      if (crf != null)
      {
         return crf.getRegistry();
      }
      synchronized (crfs)
      {
         crf = crfs.get();
         if (crf != null)
         {
            return crf.getRegistry();
         }
         crf = CmisRegistryFactoryFinder.findCmisRegistry();
         if (crf == null)
         {
            crf = new CmisRegistryFactory0(new CmisRegistry());
         }
         crfs.compareAndSet(null, crf);
      }
      return crfs.get().getRegistry();
   }

   /**
    * Sets the "current" instance of CmisRegistryFactory
    *
    * @param inst
    */
   public static void setFactory(CmisRegistryFactory factory)
   {
      crfs.set(factory);
   }

   protected List<String> renditionProviders = new ArrayList<String>();

   protected Map<String, StorageProvider> storageProviders;

   /**
    * Default constructor
    */
   public CmisRegistry()
   {
      this.storageProviders = new TreeMap<String, StorageProvider>();
   }

   /**
    * Registers StorageProvider
    *
    * @param storageProvider to be registerd
    */
   public void addStorage(StorageProvider storageProvider)
   {
      String id = storageProvider.getStorageID();
      if (this.storageProviders.get(id) != null)
      {
         throw new CmisRuntimeException("Storage " + id + " already registered.");
      }
      this.storageProviders.put(id, storageProvider);
   }

   /**
    * Create new connection. Delegated to appropriate StorageProvider method
    *
    * @param storageId storage id
    * @return connection
    * @throws InvalidArgumentException if storage with specified id is not
    *         registered
    */
   public Connection getConnection(String storageId)
   {
      StorageProvider storageProvider = storageProviders.get(storageId);
      if (storageProvider == null)
      {
         throw new InvalidArgumentException("Storage '" + storageId + "' does not exist.");
      }
      return storageProvider.getConnection();
   }

   /**
    * Get id of all available storages.
    *
    * @return short information about storages, see {@link RepositoryShortInfo}.
    *         If no one storages configured than empty set returned never null
    */
   public Set<RepositoryShortInfo> getStorageInfos()
   {
      SortedSet<RepositoryShortInfo> set = new TreeSet<RepositoryShortInfo>();
      Iterator<String> it = storageProviders.keySet().iterator();
      while (it.hasNext())
      {
         String id = it.next();
         RepositoryShortInfo info = new RepositoryShortInfo(id, id);
         Connection connection = null;
         try
         {
            connection = storageProviders.get(id).getConnection();
            info.setRootFolderId(connection.getStorage().getRepositoryInfo().getRootFolderId());
            set.add(info);
         }
         finally
         {
            if (connection != null)
            {
               connection.close();
            }
         }
      }
      return Collections.unmodifiableSortedSet(set);
   }

   /**
    * Adds an extra rendition provider .
    *
    * @param provider String FQN of provider to add.
    *
    */
   public void addRenditionProvider(String provider)
   {
      if (provider != null)
      {
         this.renditionProviders.add(provider);
      }
   }
}
