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

package org.xcmis.sp.inmemory;

import org.xcmis.spi.Connection;
import org.xcmis.spi.Storage;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.data.ObjectData;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class InmemConnection extends Connection
{

   private boolean closed;

   public InmemConnection(Storage storage)
   {
      super(storage);
   }

   protected void checkConnection() throws IllegalStateException
   {
      if (closed)
      {
         throw new IllegalStateException("Connection closed.");
      }
   }

   protected void validateChangeToken(ObjectData object, String changeToken) throws UpdateConflictException
   {
   }

   public void close()
   {
      closed = true;
   }

}
