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

package org.xcmis.client.gwt.client.service.relationship;

import org.xcmis.client.gwt.client.CmisArguments;
import org.xcmis.client.gwt.client.model.EnumRelationshipDirection;
import org.xcmis.client.gwt.client.model.restatom.EntryCollection;
import org.xcmis.client.gwt.client.rest.AsyncRequest;
import org.xcmis.client.gwt.client.rest.AsyncRequestCallback;
import org.xcmis.client.gwt.client.service.relationship.event.RelationshipsReceivedEvent;
import org.xcmis.client.gwt.client.unmarshallers.EntryCollectionUnmarshaller;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class RelationshipService
{
   /**
    * Event bus.
    */
   private HandlerManager eventBus;

   /**
    * @param eventBus eventBus
    */
   public RelationshipService(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }

   /**
    * Gets all or a subset of relationships associated with an independent object.
    * 
    * On success response received, {@link RelationshipsReceivedEvent} event is fired
    * 
    * @param url url
    * @param includeSubRelationshipTypes include sub relationship types
    * @param relationshipDirection relationship direction
    * @param typeId type id
    * @param maxItems max items
    * @param skipCount skip count
    * @param filter filter
    * @param includeAllowableActions include allowable actions
    */
   public void getObjectRelationships(String url, boolean includeSubRelationshipTypes,
      EnumRelationshipDirection relationshipDirection, String typeId, int maxItems, int skipCount, String filter,
      boolean includeAllowableActions)
   {
      EntryCollection relationships = new EntryCollection();
      RelationshipsReceivedEvent event = new RelationshipsReceivedEvent(relationships, relationshipDirection);
      EntryCollectionUnmarshaller unmarshaller = new EntryCollectionUnmarshaller(relationships);

      String params = "";
      params += (maxItems < 0) ? "" : CmisArguments.MAX_ITEMS + "=" + maxItems + "&";
      params += (skipCount < 0) ? "" : CmisArguments.SKIP_COUNT + "=" + skipCount + "&";
      params += (filter == null || filter.length() <= 0) ? "" : CmisArguments.FILTER + "=" + filter + "&";
      params += CmisArguments.INCLUDE_ALLOWABLE_ACTIONS + "=" + includeAllowableActions + "&";
      params += CmisArguments.INCLUDE_SUB_RELATIONSHIP_TYPES + "=" + includeSubRelationshipTypes;
      params += (skipCount < 0) ? "" : CmisArguments.SKIP_COUNT + "=" + skipCount;
      params +=
         (relationshipDirection == null) ? "" : CmisArguments.RELATIONSHIP_DIRECTION + "="
            + relationshipDirection.value();

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url + "?" + params).send(callback);
   }
}
