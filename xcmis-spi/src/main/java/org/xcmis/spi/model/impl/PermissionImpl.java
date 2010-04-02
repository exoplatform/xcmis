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

package org.xcmis.spi.model.impl;

import org.xcmis.spi.model.Permission;


/**
 * Simple plain implementation of {@link Permission}.
 * 
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class PermissionImpl implements Permission
{

   private String permission;

   private String description;

   public PermissionImpl()
   {
   }

   public PermissionImpl(String permission, String description)
   {
      this.permission = permission;
      this.description = description;
   }

   /**
    * {@inheritDoc}
    */
   public String getPermission()
   {
      return permission;
   }

   /**
    * {@inheritDoc}
    */
   public String getDescription()
   {
      return description;
   }

   public void setPermission(String permission)
   {
      this.permission = permission;
   }

   public void setDescription(String description)
   {
      this.description = description;
   }

}
