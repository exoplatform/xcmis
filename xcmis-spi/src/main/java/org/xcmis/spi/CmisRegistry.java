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
 * @version $Id:$
 */

public class CmisRegistry
{

   private static class SimpleCmisRegistryFactory implements CmisRegistryFactory
   {

      CmisRegistry registry;

      public SimpleCmisRegistryFactory(CmisRegistry registry)
      {
         this.registry = registry;
      }

      public CmisRegistry getRegistry()
      {
         return registry;
      }
   };

   private static AtomicReference<CmisRegistryFactory> service = new AtomicReference<CmisRegistryFactory>();

   public static CmisRegistry getInstance()
   {
      CmisRegistryFactory sf = service.get();
      if (sf == null)
      {
         service.compareAndSet(null, new SimpleCmisRegistryFactory(new CmisRegistry()));
         sf = service.get();
      }
      return sf.getRegistry();
   }

   public static void setFactory(CmisRegistryFactory inst)
   {
      service.set(inst);
   }

   protected List<String> providers = new ArrayList<String>();

   protected Map<String, StorageProvider> storageProviders;

   protected CmisRegistry()
   {
      this.storageProviders = new TreeMap<String, StorageProvider>();
   }

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
         info.setRootFolderId(storageProviders.get(id).getConnection().getStorage().getRepositoryInfo()
            .getRootFolderId());
         set.add(info);
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
         this.providers.add(provider);
      }
   }
}
