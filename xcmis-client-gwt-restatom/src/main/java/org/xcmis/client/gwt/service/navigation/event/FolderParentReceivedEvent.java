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

package org.xcmis.client.gwt.service.navigation.event;

import org.xcmis.client.gwt.model.restatom.AtomEntry;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class FolderParentReceivedEvent extends GwtEvent<FolderParentReceivedHandler>
{
   
   /**
    * Type.
    */
   public static final GwtEvent.Type<FolderParentReceivedHandler> TYPE = 
      new GwtEvent.Type<FolderParentReceivedHandler>();

   /**
    * Folder.
    */
   private AtomEntry folderParent;

   /**
    * @param folder folder
    */
   public FolderParentReceivedEvent(AtomEntry folder)
   {
      this.folderParent = folder;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    * 
    * @param handler handler
    */
   @Override
   protected void dispatch(FolderParentReceivedHandler handler)
   {
      handler.onFolderParentReceived(this);
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    * 
    * @return type {@link FolderParentReceivedHandler}
    */
   @Override
   public Type<FolderParentReceivedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @return {@link AtomEntry}
    */
   public AtomEntry getFolderParent()
   {
      return folderParent;
   }
}
