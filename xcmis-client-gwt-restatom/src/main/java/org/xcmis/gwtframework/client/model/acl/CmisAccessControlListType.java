/**
 *  Copyright (C) 2010 eXo Platform SAS.
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xcmis.gwtframework.client.model.acl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 
 * @version $Id: 
 *
 */
public class CmisAccessControlListType
{

   /**
    * Permission.
    */
   protected List<CmisAccessControlEntryType> permission;

   /**
    * List any.
    */
   protected List<Object> any;

   /**
    * @return List containing {@link CmisAccessControlEntryType} 
    */
   public List<CmisAccessControlEntryType> getPermission()
   {
      if (permission == null)
      {
         permission = new ArrayList<CmisAccessControlEntryType>();
      }
      return this.permission;
   }

   /**
    * @return List containing Object
    */
   public List<Object> getAny()
   {
      if (any == null)
      {
         any = new ArrayList<Object>();
      }
      return this.any;
   }

}
