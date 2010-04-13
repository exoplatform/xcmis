/**
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

package org.xcmis.spi.model;

import java.util.Calendar;
import java.util.List;

/**
 * Representation of a single action that occurred to an object. Each
 * {@link ChangeEvent} must contains following information:
 * <ul>
 * <li>objectId: The ObjectId of the object to which the change occurred.</li>
 * <li>changeType: Type of the change {@link ChangeType}.</li>
 * <li>properties: Additionally, for events of changeType "updated",
 * <code>ChangeEvent</code> optionally may include the new values of properties
 * on the object (if any).</li>
 * </ul>
 *
 * @see RepositoryCapabilities#getCapabilityChanges()
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: ChangeEvent.java 316 2010-03-09 15:20:28Z andrew00x $
 */
public final class ChangeEvent
{

   private String logToken;

   private String objectId;

   private ChangeType changeType;

   private Calendar date;

   private List<Property<?>> properties;

   public ChangeEvent(String logToken, String objectId, ChangeType changeType, Calendar date,
      List<Property<?>> properties)
   {
      this.logToken = logToken;
      this.objectId = objectId;
      this.changeType = changeType;
      this.date = date;
      this.properties = properties;
   }

   /**
    * Get change log token. It is unique identifier of change.
    *
    * @return change log token
    */
   public String getLogToken()
   {
      return logToken;
   }

   /**
    * @return id of changed object
    */
   public String getObjectId()
   {
      return objectId;
   }

   /**
    * @return type of changes
    * @see ChangeType
    */
   public ChangeType getType()
   {
      return changeType;
   }

   /**
    * Time of change to the object.
    *
    * @return time of change or <code>null</code> if this info is unavailable
    */
   public Calendar getDate()
   {
      return date;
   }

   /**
    * For events of changeType "updated", list may optionally include the new
    * values of properties on the object.
    *
    * @return updated properties or <code>null</code>
    */
   public List<Property<?>> getProperties()
   {
      return properties;
   }
}
