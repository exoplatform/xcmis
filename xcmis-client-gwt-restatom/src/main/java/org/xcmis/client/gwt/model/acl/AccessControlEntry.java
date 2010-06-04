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

package org.xcmis.client.gwt.model.acl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class AccessControlEntry
{

   /**
    * Direct
    */
   private boolean direct;
   
   /**
    * Principal
    */
   private AccessControlPrincipal principal;

   /**
    * Permissions
    */
   private Set<String> permissions;

   public AccessControlEntry()
   {
   }

   public AccessControlEntry(AccessControlPrincipal principal, Set<String> permissions)
   {
      this.principal = principal;
      this.permissions = permissions;
   }

   /**
    * {@inheritDoc}
    */
   public Collection<String> getPermissions()
   {
      if (permissions == null)
         permissions = new HashSet<String>();
      return permissions;
   }

   /**
    * {@inheritDoc}
    */
   public AccessControlPrincipal getPrincipal()
   {
      return principal;
   }

   public void setPrincipal(AccessControlPrincipal principal)
   {
      this.principal = principal;
   }

   public String toString()
   {
      return "principal: " + principal + ", permissions: " + permissions;
   }

   /**
    * @return the direct
    */
   public boolean isDirect()
   {
      return direct;
   }

   /**
    * @param direct the direct to set
    */
   public void setDirect(boolean direct)
   {
      this.direct = direct;
   }
   
   

}
