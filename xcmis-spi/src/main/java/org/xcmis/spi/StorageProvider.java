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

package org.xcmis.spi;

/**
 * Provide access to all available CMIS storages.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: StorageProvider.java 316 2010-03-09 15:20:28Z andrew00x $
 */
public interface StorageProvider
{

   /**
    * Create new connection for user. This method may be used for anonymous user
    * or when environment able to determine current user. This implementation
    * specific.
    * 
    * @param storageId storage id
    * @return connection
    */
   Connection getConnection();

   /**
    * Get id of storage.
    * 
    * @return storage id
    *         never null
    */
   String getStorageID();

}
