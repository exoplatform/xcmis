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
import org.xcmis.core.CmisRepositoryInfoType;

import javax.security.auth.login.LoginException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface Storage
{

   /**
    * Create new connection for user that has specified
    * <code>conversation</code>.
    * 
    * @param conversation user's state that contains user identity and some
    *        optional context specific attributes
    * @return connection
    */
   Connection login(ConversationState conversation);

   /**
    * Connect to storage by using plain user name and password.
    * 
    * @param user user name
    * @param password user password
    * @return connection
    * @throws LoginException if parameters <code>user</code> or
    *         <code>password</code> in invalid
    */
   Connection login(String user, String password) throws LoginException;

}
