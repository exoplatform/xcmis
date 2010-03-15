/*
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

package org.xcmis.spi.object.impl;

import org.xcmis.spi.object.CmisObject;
import org.xcmis.spi.object.ObjectParent;

/**
 * Simple plain implementation of {@link ObjectParent}.
 * 
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ObjectParentImpl implements ObjectParent
{

   private final CmisObject parent;

   private final String relativePathSegment;

   public ObjectParentImpl(CmisObject parent, String relativePathSegment)
   {
      this.parent = parent;
      this.relativePathSegment = relativePathSegment;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject getObject()
   {
      return parent;
   }

   /**
    * {@inheritDoc}
    */
   public String getRelativePathSegment()
   {
      return relativePathSegment;
   }

}
