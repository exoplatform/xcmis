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
package org.xcmis.client.gwt.client.service.object.event;

import org.xcmis.client.gwt.client.model.restatom.AtomEntry;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event is fired when document content is updated.
 * 
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class DocumentContentUpdatedEvent extends GwtEvent<DocumentContentUpdatedHandler>
{
   public static final GwtEvent.Type<DocumentContentUpdatedHandler> TYPE =
      new GwtEvent.Type<DocumentContentUpdatedHandler>();

   /* Updated document*/
   private AtomEntry document;

   /**
    * @param document document
    */
   public DocumentContentUpdatedEvent(AtomEntry document)
   {
      this.document = document;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(DocumentContentUpdatedHandler handler)
   {
      handler.onDocumentContentUpdated(this);
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<DocumentContentUpdatedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @return the document
    */
   public AtomEntry getDocument()
   {
      return document;
   }
}
