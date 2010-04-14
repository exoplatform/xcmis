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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.LoginException;

/**
 * @version $Id:$
 */

public abstract class CmisService {
  
  protected Map <String, StorageProvider> storageProviders;
  
  
  protected CmisService() 
  { 
    this.storageProviders = new HashMap <String, StorageProvider>();
  }
  
  /**
   * Create new connection. Delegated to appropriate StorageProvider method 
   * @throws InvalidArgumentException if storage with <code>id</code> does not
   *         exists
   */
  public final Connection getConnection(String storageId, String user, String password) throws LoginException,
     InvalidArgumentException 
  {
    // TODO provide authentication facility here? probably it is not storage specific?
    // so perhaps we do not need StorageProvider(String, String, String) at all?
    return storageProviders.get(storageId).getConnection(storageId, user, password);
  }

  /**
   * Create new connection. Delegated to appropriate StorageProvider method 
   *
   * @param storageId storage id
   * @return connection
   */
  public final Connection getConnection(String storageId) 
  {
    return storageProviders.get(storageId).getConnection(storageId);
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
    // TODO gather all the per-provider info dynamically or cache and retrieve 
    return null;
  }



}
