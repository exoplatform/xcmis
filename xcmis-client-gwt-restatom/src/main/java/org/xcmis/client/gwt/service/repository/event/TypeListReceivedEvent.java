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

package org.xcmis.client.gwt.service.repository.event;

import org.xcmis.client.gwt.model.restatom.TypeList;

import com.google.gwt.event.shared.GwtEvent;

/**
 * This event is fired when plain type list is received
 * 
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class TypeListReceivedEvent extends GwtEvent<TypeListReceivedHandler>
{
   /**
    * Type.
    */
   public static final GwtEvent.Type<TypeListReceivedHandler> TYPE = new GwtEvent.Type<TypeListReceivedHandler>();

   /**
    * Type list.
    */
   private TypeList typeList;

   /**
    * @param typeList typeList
    */
   public TypeListReceivedEvent(TypeList typeList)
   {
      this.typeList = typeList;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    * 
    * @param handler handler
    */
   @Override
   protected void dispatch(TypeListReceivedHandler handler)
   {
      handler.onTypeListReceived(this);
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    * 
    * @return Type {@link TypeListReceivedHandler}
    */
   @Override
   public Type<TypeListReceivedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @return {@link TypeList}
    */
   public TypeList getTypeList()
   {
      return typeList;
   }
}
