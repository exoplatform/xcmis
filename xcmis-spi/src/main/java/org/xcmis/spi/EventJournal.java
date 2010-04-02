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

package org.xcmis.spi;


/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface EventJournal
{

   /**
    * Add new event.
    *
    * @param objectId id of update/created/removed object.
    * @param eventType event type
    * @return change log token for add event
    */
   String addEvent(String objectId, ChangeEvent eventType);

   /**
    * Get event by log token.
    *
    * @param logToken change log token
    * @return change event or <code>null</code> if there is no event for
    *         specified log token. It may happen if <code>logToken</code> is
    *         invalid or if changes log was truncated
    */
   ChangeEvent getEvent(String logToken);

}
