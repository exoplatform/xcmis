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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class ByteArrayValue extends Value
{

   private final byte[] values;

   public ByteArrayValue(byte[] a)
   {
      this.values = new byte[a.length];
      System.arraycopy(a, 0, this.values, 0, a.length);
   }

   @Override
   public byte[] getBytes()
   {
      return values;
   }

   @Override
   public boolean isContent()
   {
      return true;
   }

   public static ByteArrayValue fromStream(InputStream stream) throws IOException
   {
      if (stream == null)
      {
         return new ByteArrayValue(new byte[0]);
      }
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      byte[] buf = new byte[1024];
      int r = -1;
      while ((r = stream.read(buf)) != -1)
      {
         bout.write(buf, 0, r);
      }
      byte[] bytes = bout.toByteArray();
      return new ByteArrayValue(bytes);
   }

}
