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

package org.xcmis.gwtframework.client.services.acl;

import org.xcmis.gwtframework.client.model.actions.ApplyACL;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public abstract class ACLServices
{
   /**
    * Instance.
    */
   private static ACLServices instance;

   /**
    * @return {@link ACLServices}
    */
   public static ACLServices getInstance()
   {
      return instance;
   }

   /**
    * Get instance of {@link ACLServices}.
    */
   protected ACLServices()
   {
      instance = this;
   }

   /**
    * Get the ACL currently applied to the specified by url document or folder object.
    * 
    * @param url url
    * @param onlyBasicPermissions onlyBasicPermissions
    */
   public abstract void getACL(String url, boolean onlyBasicPermissions);

   /**
    * Adds or removes the given ACEs to or from the ACL of document or folder object pointed by url.
    * 
    * @param url url
    * @param applyACL applyACL
    */
   public abstract void applyACL(String url, ApplyACL applyACL);

}
