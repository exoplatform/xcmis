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

package org.xcmis.sp.jcr.exo;

import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.TypeDefinition;
import org.xcmis.spi.VersioningState;
import org.xcmis.spi.data.Folder;

import java.util.Collection;
import java.util.Collections;

import javax.jcr.Node;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
class UnfiledDocument extends DocumentImpl
{

   protected final Node unfiledStore;

   public UnfiledDocument(TypeDefinition type, Node unfiledStore, String name, VersioningState versioningState)
   {
      super(type, null, name, versioningState);
      this.unfiledStore = unfiledStore;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Folder getParent() throws ConstraintException
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Collection<Folder> getParents()
   {
      return Collections.emptyList();
   }

}
