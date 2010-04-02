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

package org.xcmis.sp.inmemory;

import org.xcmis.spi.StorageException;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.Folder;
import org.xcmis.spi.data.Policy;
import org.xcmis.spi.model.TypeDefinition;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class PolicyImpl extends BaseObjectData implements Policy
{

   public PolicyImpl(Entry entry, TypeDefinition type, StorageImpl storage)
   {
      super(entry, type, storage);
      // TODO Auto-generated constructor stub
   }

   public PolicyImpl(Folder parent, TypeDefinition type, StorageImpl storage)
   {
      super(parent, type, storage);
      // TODO Auto-generated constructor stub
   }

   @Override
   protected void save() throws StorageException
   {
      // TODO Auto-generated method stub

   }

   public String getPolicyText()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public ContentStream getContentStream(String streamId)
   {
      // TODO Auto-generated method stub
      return null;
   }

}
