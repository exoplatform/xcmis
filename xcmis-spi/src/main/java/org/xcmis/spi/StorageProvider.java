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

import java.util.Set;

/**
 * Provide access to all available CMIS storages.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface StorageProvider
{

   /**
    * Get storage with specified id.
    * 
    * @param id storage id
    * @return storage
    * @throws InvalidArgumentException if storage with <code>id</code> does not
    *         exists
    */
   Storage getStorage(String id) throws InvalidArgumentException;

   /**
    * Get id of all available storages.
    * 
    * @return storages iDs if no one storages configured than empty set returned
    *         never null
    */
   Set<String> getStorageIDs();

}
