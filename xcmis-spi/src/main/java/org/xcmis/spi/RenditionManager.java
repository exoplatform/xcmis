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

import org.xcmis.spi.model.Rendition;

/**
 * Manage object's renditions.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface RenditionManager
{

   /**
    * Get all renditions of specified entry.
    *
    * @param obj ObjectData
    * @return set of object renditions. If object has not renditions then empty
    *            iterator will be returned.
    * @throws StorageException if any other CMIS repository error occurs
    */
   ItemsIterator<Rendition> getRenditions(ObjectData obj) throws StorageException;

   /**
    * Get rendition stream for objects with specified id.
    *
    * @param streamId stream id
    * @param ObjectData object
    * @return Renditions content stream
    */
    ContentStream getStream(ObjectData obj, String streamId);
}
