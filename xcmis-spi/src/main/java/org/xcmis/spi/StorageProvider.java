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

import org.exoplatform.services.security.ConversationState;

import java.util.Set;

import javax.security.auth.login.LoginException;

/**
 * Provide access to all available CMIS storages.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: StorageProvider.java 316 2010-03-09 15:20:28Z andrew00x $
 */
public interface StorageProvider
{

   /**
    * Get storage with specified id.
    * 
    * @param storageId storage id
    * @param user user name
    * @param password user password
    * @return connection
    * @throws LoginException if parameters <code>user</code> or
    *         <code>password</code> in invalid
    * @throws InvalidArgumentException if storage with <code>id</code> does not
    *         exists
    */
   Connection getConnection(String storageId, String user, String password) throws LoginException, InvalidArgumentException;

   /**
    * Create new connection for user that has specified
    * <code>conversation</code>.
    * 
    * @param storageId storage id
    * @param conversation user's state that contains user identity and some
    *        optional context specific attributes
    * @return connection
    */
   Connection getConnection(String storageId, ConversationState conversation);

   /**
    * Get id of all available storages.
    * 
    * @return storages iDs if no one storages configured than empty set returned
    *         never null
    */
   // TODO : short info about storages, e.g. CmisRepositoryEntryType
   Set<String> getStorageIDs();

}
