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

package org.xcmis.gwtframework.client.services.relationship;

import org.xcmis.gwtframework.client.model.EnumRelationshipDirection;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public abstract class RelationshipServices
{
   /**
    * Instance.
    */
   private static RelationshipServices instance;

   /**
    * @return {@link RelationshipServices}
    */
   public static RelationshipServices getInstance()
   {
      return instance;
   }

   /**
    * Get instance of {@link RelationshipServices}.
    */
   protected RelationshipServices()
   {
      instance = this;
   }

   /**
    * Gets all or a subset of relationships associated with an independent object.
    * 
    * On success response received, RelationshipsReceivedEvent event is fired
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
   public abstract void getObjectRelationships(String url, boolean includeSubRelationshipTypes,
      EnumRelationshipDirection relationshipDirection, String typeId, int maxItems, int skipCount, 
      String filter, boolean includeAllowableActions);

}
