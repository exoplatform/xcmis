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

package org.xcmis.gwtframework.client.service.discovery.event;

import org.xcmis.gwtframework.client.model.restatom.EntryCollection;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event is fired when search results received
 * 
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Anna Zhuleva</a>
 * @version $Id:
 */

public class QueryResultReceivedEvent extends GwtEvent<QueryResultReceivedHandler>
{

   /**
    * Type.
    */
   public static final GwtEvent.Type<QueryResultReceivedHandler> TYPE = 
      new GwtEvent.Type<QueryResultReceivedHandler>();

   /**
    * Entry collection.
    */
   private EntryCollection entryCollection;
   
   /**
    * @param entryCollection entryCollection
    */
   public QueryResultReceivedEvent(EntryCollection entryCollection)
   {
      this.entryCollection = entryCollection;
   }
   
   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    * 
    * @param handler handler
    * 
    */
   @Override
   protected void dispatch(QueryResultReceivedHandler handler)
   {
      handler.onQueryResultReceived(this);
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    * 
    * @return Type {@link QueryResultReceivedHandler}
    */
   @Override
   public Type<QueryResultReceivedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @return {@link EntryCollection}
    */
   public EntryCollection getEntryCollection()
   {
      return entryCollection;
   }
   
}
