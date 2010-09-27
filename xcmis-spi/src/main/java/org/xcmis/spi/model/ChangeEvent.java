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
import java.util.Set;

/**
 * Representation of a single action that occurred to an object. Each
 * {@link ChangeEvent} must contains following information:
 * <ul>
 * <li>objectId: The ObjectId of the object to which the change occurred.</li>
 * <li>changeType: Type of the change {@link ChangeType}.</li>
 * </ul>
 * And may provide optional information about:
 * <ul>
 * <li>properties: Additionally, for events of changeType "updated",
 * <code>ChangeEvent</code> optionally may include the new values of properties
 * on the object (if any).</li>
 * <li>policiIDs: Additionally, for events of changeType "security",
 * <code>ChangeEvent</code> optionally may include ids of policies applied to
 * the object referenced in the change event.</li>
 * <li>acl: Additionally, for events of changeType "security",
 * <code>ChangeEvent</code> optionally may include the ACLs applied to the
 * object referenced in the change event.</li>
 * </ul>
 * NOTE The ChangeEvent is just holder for information about changes. It does
 * not validate supplied information about changes in any kind.
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

   private Set<String> policiesId;

   private List<AccessControlEntry> acl;

   public ChangeEvent(String logToken, String objectId, ChangeType changeType, Calendar date)
   {
      this.logToken = logToken;
      this.objectId = objectId;
      this.changeType = changeType;
      this.date = date;
   }

   /**
    * Create change event.
    *
    * @param logToken unique token for this event. Each event can be retrieved
    *        by this token
    * @param objectId id of object to which the change occurred
    * @param changeType type of changes
    * @param date date when changes occurs
    * @param properties properties which were updated for object. Typically,
    *        this parameter should be provided ONLY when changes type is
    *        "updated". NOTE This implementation does not validate it
    * @see ChangeType
    * @deprecated
    */
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
    * Create change event.
    *
    * @param logToken unique token for this event. Each event can be retrieved
    *        by this token
    * @param objectId id of object to which the change occurred
    * @param changeType type of changes
    * @param date date when changes occurs
    * @param properties properties which were updated for object. Typically,
    *        this parameter should be provided ONLY when changes type is
    *        "updated". NOTE This implementation does not validate it
    * @param policiesId set of ids of policies applied to the object referenced
    *        in the change event. Typically, this parameter should NOT be
    *        provided if changes type is other then "security" or parameter
    *        <code>acl</code> is provided. In other worlds typically expect
    *        <code>policiesId</code> or <code>acl</code> but not both. NOTE This
    *        implementation does not validate it
    * @param acl ACLs applied to the object referenced in the change event.
    *        Typically, this parameter should NOT be provided if changes type is
    *        other then "security" or parameter <code>policiesId</code> is
    *        provided. In other worlds typically expect <code>acl</code> or
    *        <code>policiesId</code> but not both. NOTE This implementation does
    *        not validate it
    * @see ChangeType
    */
   public ChangeEvent(String logToken, String objectId, ChangeType changeType, Calendar date,
      List<Property<?>> properties, Set<String> policiesId, List<AccessControlEntry> acl)
   {
      this.logToken = logToken;
      this.objectId = objectId;
      this.changeType = changeType;
      this.date = date;
      this.properties = properties;
      this.policiesId = policiesId;
      this.acl = acl;
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

   /**
    * For event of changeType "security" may return ids of policies applied to
    * the object referenced in the change event.
    *
    * @return set of policies' ids or <code>null</code>
    */
   public Set<String> getPolicyIds()
   {
      return policiesId;
   }

   /**
    * For event of changeType "security" may return ACLs applied to the object
    * referenced in the change event.
    *
    * @return ACLs or <code>null</code>
    */
   public List<AccessControlEntry> getAcl()
   {
      return acl;
   }

}
