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

import org.xcmis.spi.Connection;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.PermissionService;
import org.xcmis.spi.RenditionManager;
import org.xcmis.spi.StorageProvider;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class StorageProviderImpl implements StorageProvider
{

   //private static final Logger LOG = Logger.getLogger(StorageProviderImpl.class);

   private StorageImpl storageImpl = null;

   private StorageConfiguration storageConfig = null;

   private RenditionManager renditionManager;

   /**
    * Instantiates a new storage provider impl.
    *
    * @param repositoryId String repository id
    * @param repositoryName String repository name
    * @param description String description
    * @param maxStorageMemSize the max storage memory size in bytes or -1L for
    *        unbounded
    * @param maxItemsNumber the maximum items number, or -1L for unbounded
    */
   public StorageProviderImpl(String repositoryId, String repositoryName, String description, long maxStorageMemSize,
      long maxItemsNumber)
   {
      this.storageConfig =
         new StorageConfiguration(repositoryId, repositoryName, description, maxStorageMemSize, maxItemsNumber);
      this.renditionManager = RenditionManager.getInstance();
      this.storageImpl =
         new StorageImpl(storageConfig, renditionManager, new PermissionService());
   }

   /**
    * @see org.xcmis.spi.StorageProvider#getConnection()
    */
   public Connection getConnection()
   {
      if (storageImpl == null)
      {
         throw new InvalidArgumentException("CMIS repository does not exist.");
      }

      return new InmemConnection(storageImpl);
   }

   /**
    * @see org.xcmis.spi.StorageProvider#getStorageID()
    */
   public String getStorageID()
   {
      return storageConfig.getId();
   }

}
