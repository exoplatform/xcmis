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

package org.xcmis.gwtframework.client.services.versioning;

import org.xcmis.gwtframework.client.model.actions.CheckIn;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public abstract class VersioningServices
{
   /**
    * Instance.
    */
   private static VersioningServices instance;

   /**
    * @return {@link VersioningServices}
    */
   public static VersioningServices getInstance()
   {
      return instance;
   }

   /**
    * Get instance of {@link VersioningServices}.
    */
   protected VersioningServices()
   {
      instance = this;
   }
   
   /**
    * Create a private working copy of the document.
    * 
    * On success response received, CheckoutReceivedEvent event is fired
    * 
    * @param url url
    * @param objectId object id
    */
   public abstract void checkOut(String url, String objectId);

   /**
    * Reverses the effect of a check-out. 
    * Removes the private working copy of the checked-out document, 
    * allowing other documents in the version series to be checked out again.
    * 
    * On success response received, CancelCheckoutReceivedEvent event is fired
    * 
    * @param url url
    */
   public abstract void cancelCheckout(String url);

   /**
    * Checks-in the Private Working Copy document.
    * 
    * On success response received, CheckinReceivedEvent event is fired
    * 
    * @param url url
    * @param checkIn checkIn
    */
   public abstract void checkin(String url, CheckIn checkIn);

   /**
    * Returns the list of all Document Objects in the specified Version Series, 
    * sorted by cmis:creationDate descending.
    * 
    * On success response received, AllVersionsReceivedEvent event is fired
    *  
    * @param url url
    * @param filter filter
    * @param includeAllowableActions include allowable actions
    */
   public abstract void getAllVersions(String url, String filter, boolean includeAllowableActions);

}
