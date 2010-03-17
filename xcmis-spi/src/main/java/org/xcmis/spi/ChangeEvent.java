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

import org.xcmis.spi.object.Property;

import java.util.Calendar;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: ChangeEvent.java 316 2010-03-09 15:20:28Z andrew00x $
 */
public interface ChangeEvent
{

   /**
    * Get change log token. It is unique identifier of change.
    * 
    * @return change log token
    */
   String getLogToken();

   /**
    * @return id of changed object
    */
   String getObjectId();

   /**
    * @return type of changes
    * @see ChangeType
    */
   ChangeType getType();

   /**
    * Time of change to the object.
    * 
    * @return time of change or <code>null</code> if this info is unavailable
    */
   Calendar getDate();

   /**
    * For events of changeType "updated", list may optionally include the new
    * values of properties on the object.
    * 
    * @return updated properties or <code>null</code>
    */
   List<Property<?>> getProperties();
}
