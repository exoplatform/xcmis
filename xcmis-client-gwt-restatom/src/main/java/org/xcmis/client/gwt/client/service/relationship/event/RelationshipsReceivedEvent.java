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

package org.xcmis.client.gwt.client.service.relationship.event;

import org.xcmis.client.gwt.client.model.EnumRelationshipDirection;
import org.xcmis.client.gwt.client.model.restatom.EntryCollection;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event is fired when relationships response is received.
 * 
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Anna Zhuleva</a>
 * @version $Id: 
 */
public class RelationshipsReceivedEvent extends GwtEvent<RelationshipsReceivedHandler>
{

   /**
    * Type.
    */
   public static final GwtEvent.Type<RelationshipsReceivedHandler> TYPE =
      new GwtEvent.Type<RelationshipsReceivedHandler>();

   /**
    * Relationships.
    */
   private EntryCollection relationships;

   /**
    * Direction.
    */
   private EnumRelationshipDirection direction;

   /**
    * @param relationships relationships
    * @param direction direction
    */
   public RelationshipsReceivedEvent(EntryCollection relationships, EnumRelationshipDirection direction)
   {
      this.relationships = relationships;
      this.direction = direction;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    * 
    * @param handler handler
    */
   @Override
   protected void dispatch(RelationshipsReceivedHandler handler)
   {
      handler.onRelationshipsReceived(this);
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    * 
    * @return Type {@link RelationshipsReceivedHandler}
    */
   @Override
   public Type<RelationshipsReceivedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @return {@link EntryCollection}
    */
   public EntryCollection getRelationships()
   {
      return relationships;
   }

   /**
    * @return {@link org.xcmis.client.gwt.client.model.restatom.EnumLinkRelation EnumLinkRelation}
    */
   public EnumRelationshipDirection getDirection()
   {
      return direction;
   }
}
