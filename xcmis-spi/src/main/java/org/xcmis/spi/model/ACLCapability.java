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

package org.xcmis.spi.model;


import java.util.List;

/**
 * Repository ACL capability.
 * 
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ACLCapability
{
   /**
    * @return mapping performed actions to required permissions
    * @see PermissionMapping
    */
   List<PermissionMapping> getMapping();

   /**
    * @return set of supported permissions
    */
   List<Permission> getPermissions();

   /**
    * @return how ACL propagated in object hierarchy
    */
   AccessControlPropagation getPropagation();

   /**
    * @return kind of supported permissions
    */
   SupportedPermissions getSupportedPermissions();

}
