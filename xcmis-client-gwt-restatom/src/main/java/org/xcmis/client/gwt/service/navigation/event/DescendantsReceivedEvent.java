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

import org.xcmis.client.gwt.model.restatom.EntryCollection;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event is fired, when the set of descendant objects contained in the specified folder 
 * or any of its child folders is received.
 * 
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Anna Zhuleva</a>
 * @version $Id:
 */

public class DescendantsReceivedEvent extends GwtEvent<DescendantsReceivedHander>
{
   /**
    * Type.
    */
   public static final GwtEvent.Type<DescendantsReceivedHander> TYPE = 
      new GwtEvent.Type<DescendantsReceivedHander>();

   /**
    * Entries.
    */
   private EntryCollection descendants;

   /**
    * @param entries entries
    */
   public DescendantsReceivedEvent(EntryCollection entries)
   {
      this.descendants = entries;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    * 
    * @param handler handler
    */
   @Override
   protected void dispatch(DescendantsReceivedHander handler)
   {
      handler.onDescendantsReceived(this);
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    * 
    * @return Type {@link DescendantsReceivedHander}
    */
   @Override
   public Type<DescendantsReceivedHander> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @return the descendants
    */
   public EntryCollection getDescendants()
   {
      return descendants;
   }
}
