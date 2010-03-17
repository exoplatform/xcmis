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

package org.xcmis.gwtframework.client.services.object.event;

import org.xcmis.gwtframework.client.model.CmisContentStreamType;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS.
 * 
 *  Event is fired when content stream of document is received
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Anna Zhuleva</a>
 * @version $Id:
 */
public class ContentStreamReceivedEvent extends GwtEvent<ContentStreamReceivedHandler>
{
   /**
    * Type.
    */
   public static final GwtEvent.Type<ContentStreamReceivedHandler> TYPE = 
      new GwtEvent.Type<ContentStreamReceivedHandler>();

   /**
    * Content stream type.
    */
   private CmisContentStreamType contentStreamType;

   /**
    * @param contentStreamType content stream type
    */
   public ContentStreamReceivedEvent(CmisContentStreamType contentStreamType)
   {
      this.contentStreamType = contentStreamType;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    * 
    * @param handler handler
    */
   @Override
   protected void dispatch(ContentStreamReceivedHandler handler)
   {
      handler.onContentStreamReceived(this);
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    * 
    * @return Type {@link ContentStreamReceivedHandler}
    */
   @Override
   public Type<ContentStreamReceivedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @return {@link CmisContentStreamType}
    */
   public CmisContentStreamType getContentStreamType()
   {
      return contentStreamType;
   }
}
