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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @version $Id:$
 */

public class CmisStorageInitializer
{

   private static AtomicReference<CmisStorageInitializer> service = new AtomicReference<CmisStorageInitializer>();

   protected List<String> providers = new ArrayList<String>();

   public static CmisStorageInitializer getInstance()
   {
      CmisStorageInitializer s = service.get();
      if (s == null)
      {
         service.compareAndSet(null, new CmisStorageInitializer());
         s = service.get();
      }
      return s;
   }

   public static void setInstance(CmisStorageInitializer inst)
   {
      service.set(inst);
   }

   protected Map<String, StorageProvider> storageProviders;

   protected CmisStorageInitializer()
   {
      this.storageProviders = new HashMap<String, StorageProvider>();
   }

   public final void addStorage(String id, StorageProvider storageProvider)
   {
      // TODO : Need retrieval all storages from StorageProvider ??
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
    */
   public final Connection getConnection(String storageId)
   {
      StorageProvider storageProvider = storageProviders.get(storageId);
      if (storageProvider == null)
      {
         throw new InvalidArgumentException("Storage " + storageId + " does not exists.");
      }
      return storageProvider.getConnection(storageId);
   }

   /**
    * Get id of all available storages.
    *
    * @return storages iDs if no one storages configured than empty set returned
    *         never null
    */
   // TODO : short info about storages, e.g. CmisRepositoryEntryType
   public final Set<String> getStorageIDs()
   {
      return Collections.unmodifiableSet(storageProviders.keySet());
   }

   /**
    * Adds an extra rendition provider .
    *
    * @param provider string FQN of provider to add.
    * 
    */
   public void addRenditionProvider(String provider)
   {
      if (provider != null)
         this.providers.add(provider);
   }
}
