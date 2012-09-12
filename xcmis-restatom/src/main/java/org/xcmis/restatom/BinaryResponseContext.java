/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.xcmis.restatom;

import org.apache.abdera.protocol.server.context.SimpleResponseContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class BinaryResponseContext extends SimpleResponseContext
{
   private final InputStream in;

   public BinaryResponseContext(InputStream in, int status)
   {
      this.in = in;
      this.status = status;
   }

   @Override
   public void writeTo(OutputStream out) throws IOException
   {
      if (hasEntity())
      {
         try
         {
            byte[] buf = new byte[8192];
            int r;
            while ((r = in.read(buf)) != -1)
            {
               out.write(buf, 0, r);
            }
         }
         finally
         {
            in.close();
         }
      }
   }

   @Override
   protected void writeEntity(Writer out) throws IOException
   {
      if (hasEntity())
      {
         try
         {
            InputStreamReader reader = new InputStreamReader(in);
            char[] buf = new char[8192];
            int r;
            while ((r = reader.read(buf)) != -1)
            {
               out.write(buf, 0, r);
            }
         }
         finally
         {
            in.close();
         }
      }
   }

   @Override
   public final boolean hasEntity()
   {
      return in != null;
   }
}
