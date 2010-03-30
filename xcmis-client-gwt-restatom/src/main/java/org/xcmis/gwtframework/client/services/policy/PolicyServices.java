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

package org.xcmis.gwtframework.client.services.policy;

import org.xcmis.gwtframework.client.model.EnumIncludeRelationships;
import org.xcmis.gwtframework.client.model.actions.ApplyPolicy;
import org.xcmis.gwtframework.client.model.actions.RemovePolicy;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public abstract class PolicyServices
{
   /**
    * Instance.
    */
   private static PolicyServices instance;

   /**
    * @return {@link PolicyServices}
    */
   public static PolicyServices getInstance()
   {
      return instance;
   }

   /**
    * Get instance of {@link PolicyServices}.
    */
   protected PolicyServices()
   {
      instance = this;
   }

   /**
    * Applies a specified policy to an object.
    * 
    * On success response received, PolicyAppliedEvent event is fired
    * 
    * @param url url
    * @param applyPolicy apply policy
    */
   public abstract void applyPolicy(String url, ApplyPolicy applyPolicy);

   /**
    * Removes a specified policy from an object.
    * 
    * On success response received, PolicyRemovedEvent event is fired
    * 
    * @param url url
    * @param removePolicy remove policy
    */
   public abstract void removePolicy(String url, RemovePolicy removePolicy);

   /**
    * Gets the list of policies currently applied to the specified object.
    * 
    * On success response received, AppliedPoliciesReceivedEvent event is fired
    * 
    * @param url url
    * @param filter filter
    */
   public abstract void getAppliedPolicies(String url, String filter);

   /**
    * Get all policies from repository.
    * 
    * On success response received, AllPoliciesReceivedEvent event is fired
    * 
    * @param url url
    * @param repositoryId repository id
    * @param searchAllVersions search all versions
    * @param includeAllowableActions include allowable actions
    * @param includeRelationships include relationships
    * @param renditionFilter rendition filter
    * @param maxItems max items
    * @param skipCount skipCount
    */
   public abstract void getAllPolicies(String url, String repositoryId, boolean searchAllVersions,
      boolean includeAllowableActions, EnumIncludeRelationships includeRelationships, 
      String renditionFilter, Long maxItems, Long skipCount);

}
