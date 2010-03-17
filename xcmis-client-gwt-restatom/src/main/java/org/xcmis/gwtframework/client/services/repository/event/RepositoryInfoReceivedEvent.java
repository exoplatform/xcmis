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

import org.xcmis.gwtframework.client.model.repository.CmisRepositoryInfo;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event is fired, when repository information response is received
 * 
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class RepositoryInfoReceivedEvent extends GwtEvent<RepositoryInfoReceivedHandler>
{

   /**
    * Type.
    */
   public static final GwtEvent.Type<RepositoryInfoReceivedHandler> TYPE =
      new GwtEvent.Type<RepositoryInfoReceivedHandler>();

   /**
    * Repository info.
    */
   private CmisRepositoryInfo repositoryInfo;

   /**
    * @param repositoryInfo repositoryInfo
    */
   public RepositoryInfoReceivedEvent(CmisRepositoryInfo repositoryInfo)
   {
      this.repositoryInfo = repositoryInfo;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    * 
    * @param handler handler
    */
   @Override
   protected void dispatch(RepositoryInfoReceivedHandler handler)
   {
      handler.onRepositoryInfoReceived(this);
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    * 
    * @return Type {@link RepositoryInfoReceivedHandler}
    */
   @Override
   public Type<RepositoryInfoReceivedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @return {@link CmisRepositoryInfo}
    */
   public CmisRepositoryInfo getRepositoryInfo()
   {
      return repositoryInfo;
   }
}
