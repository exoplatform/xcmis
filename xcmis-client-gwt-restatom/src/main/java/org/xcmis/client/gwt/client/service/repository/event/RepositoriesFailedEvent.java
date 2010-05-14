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
package org.xcmis.client.gwt.client.service.repository.event;

import org.xcmis.client.gwt.client.rest.ExceptionThrownEvent;
import org.xcmis.client.gwt.client.service.repository.ServiceNotfoundException;


/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class RepositoriesFailedEvent extends ExceptionThrownEvent
{
   
   /**
    * Service url
    */
   private String service;
   
   /**
    * @param service
    */
   public RepositoriesFailedEvent(String service){
      this.service = service;
   }


   /**
    * @return the service
    */
   public String getService()
   {
      return service;
   }
   
   /**
    * @see org.xcmis.client.gwt.client.rest.ExceptionThrownEvent#setException(java.lang.Throwable)
    */
   @Override
   public void setException(Throwable exception)
   {
      super.setException(new ServiceNotfoundException("Service "+service+" is not available."));
   }
}
