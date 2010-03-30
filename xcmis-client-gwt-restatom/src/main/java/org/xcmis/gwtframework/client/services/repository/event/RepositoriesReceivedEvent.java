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

package org.xcmis.gwtframework.client.services.repository.event;

import org.xcmis.gwtframework.client.model.restatom.CmisRepositories;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event is fired, when repositories response is received
 * 
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Anna Zhuleva</a>
 * @version $Id:
 */

public class RepositoriesReceivedEvent extends GwtEvent<RepositoriesReceivedHandler>
{

   /**
    * Type.
    */
   public static final GwtEvent.Type<RepositoriesReceivedHandler> TYPE = 
      new GwtEvent.Type<RepositoriesReceivedHandler>();

   /**
    * CMIS repositories
    */
   private CmisRepositories repositories;

   /**
    * @param repositories cmisService
    */
   public RepositoriesReceivedEvent(CmisRepositories repositories)
   {
      this.repositories = repositories;
   }
   
   /**
    * @return {@link CmisRepositories}
    */
   public CmisRepositories getRepositories()
   {
      return repositories;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    * 
    * @param handler handler
    */
   @Override
   protected void dispatch(RepositoriesReceivedHandler handler)
   {
      handler.onRepositoriesReceived(this);
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    * 
    * @return Type {@link RepositoriesReceivedHandler}
    */
   @Override
   public Type<RepositoriesReceivedHandler> getAssociatedType()
   {
      return TYPE;
   }

}
