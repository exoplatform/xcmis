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

import org.xcmis.core.EnumBaseObjectTypeIds;

/**
 * Purpose of implementations of this is produce uniquely name for newly created
 * CMIS entry. It is useful when back-end storage required any name for entry
 * but client does not provide any name for it.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface EntryNameProducer
{

   /**
    * Get name for CMIS entry. If parameter <code>parentId</code> is null and
    * repository does not support unfiling/multi-filing capabilities
    * OperationNotSupportedException may be thrown.
    * 
    * @param parentId parent folder where object will be created. May be null if
    *          object should be left unfiled.
    * @param scope object scope
    * @param typeId object type
    * @return object name
    */
   String getEntryName(String parentId, EnumBaseObjectTypeIds scope, String typeId);

}
