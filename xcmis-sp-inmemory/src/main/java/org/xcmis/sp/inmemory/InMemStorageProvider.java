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

import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.Storage;
import org.xcmis.spi.StorageProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class InMemStorageProvider implements StorageProvider
{

   private final Map<String, Storage> storages;

   public InMemStorageProvider()
   {
      this.storages = new HashMap<String, Storage>();
   }

   public void addStorage(Storage storage)
   {
   }

   /**
    * {@inheritDoc}
    */
   public Storage getStorage(String id) throws InvalidArgumentException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Set<String> getStorageIDs()
   {
      // TODO Auto-generated method stub
      return null;
   }

}
