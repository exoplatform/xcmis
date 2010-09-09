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

import java.security.Permission;
import java.util.HashMap;
import java.util.Map;

/**
 * Holder information about current caller.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public final class UserContext
{

   public static final Permission USER_CONTEXT_PERMISSION = new RuntimePermission("updateUserContext");

   private static ThreadLocal<UserContext> tlHolder = new ThreadLocal<UserContext>();

   /**
    * Get context of current caller.
    *
    * @return current user context or <code>null</code> if current context is
    *         not initialized yet
    */
   public static UserContext getCurrent()
   {
      return tlHolder.get();
   }

   /**
    * Set context of current caller. To be able do this
    * <code>java.lang.RuntimePermission</code> "updateUserContext" required.
    *
    * @throws SecurityException if operation is not permitted
    * @see SecurityManager
    * @see Permission
    */
   public static void setCurrent(UserContext ctx)
   {
      checkPermissions();
      tlHolder.set(ctx);
   }

   private static void checkPermissions()
   {
      SecurityManager security = System.getSecurityManager();
      if (security != null)
      {
         security.checkPermission(USER_CONTEXT_PERMISSION);
      }
   }

   /** User's ID. */
   private final String userId;

   /** Optional properties. */
   private Map<String, String> properties;

   /**
    * Create new context for user with specified ID.
    *
    * @param userId user's ID
    */
   public UserContext(String userId)
   {
      if (userId == null)
      {
         throw new IllegalArgumentException("User ID may not be null. ");
      }
      this.userId = userId;
   }

   /**
    * Get user's ID.
    *
    * @return user's ID
    */
   public String getUserId()
   {
      return userId;
   }

   /**
    * Get optional context property with specified name.
    *
    * @param name property name
    * @return property value or <code>null</code>
    */
   public String getProperty(String name)
   {
      if (properties != null)
      {
         return properties.get(name);
      }
      return null;
   }

   /**
    * Update or set property.To be able do this
    * <code>java.lang.RuntimePermission</code> "updateUserContext" required.
    *
    * @param name property name
    * @param value property value
    * @throws SecurityException if operation is not permitted
    * @see SecurityManager
    * @see Permission
    */
   public void setProperty(String name, String value)
   {
      checkPermissions();
      if (properties == null)
      {
         properties = new HashMap<String, String>();
      }
      properties.put(name, value);
   }

}
