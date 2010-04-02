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

import org.xcmis.spi.data.ContentStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Inmemory byte array based content stream.
 *
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
final class ByteArrayContentStream implements ContentStream
{

   /** The bytes[]. */
   private final byte[] bytes;

   /** Name of content file. */
   private final String fileName;

   /** Media type of stream. */
   private final String mediaType;

   /** Content length. */
   private final int length;

   /**
    * @param bytes source bytes of content
    * @param fileName name of content file
    * @param mediaType media type of content
    */
   public ByteArrayContentStream(byte[] bytes, String fileName, String mediaType)
   {
      this.bytes = bytes;
      this.fileName = fileName;
      this.mediaType = mediaType;
      this.length = bytes.length;
   }

   public ByteArrayContentStream(ContentStream source) throws IOException
   {
      this.mediaType = source.getMediaType();
      this.fileName = source.getFileName();
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      InputStream in = source.getStream();
      byte[] buf = new byte[1024];
      int r = -1;
      while ((r = in.read(buf)) != -1)
      {
         bout.write(buf, 0, r);
      }
      this.bytes = bout.toByteArray();
      this.length = bytes.length;
   }

   public ByteArrayContentStream(ByteArrayContentStream that)
   {
      this.mediaType = that.getMediaType();
      this.fileName = that.getFileName();
      this.bytes = new byte[that.bytes.length];
      this.length = this.bytes.length;
      System.arraycopy(that.bytes, 0, this.bytes, 0, this.length);
   }

   public ByteArrayContentStream copy()
   {
      return new ByteArrayContentStream(this);
   }

   /**
    * Gets the data.
    *
    * @return the data
    */
   public byte[] getData()
   {
      return bytes;
   }

   /**
    * {@inheritDoc}
    */
   public String getFileName()
   {
      return fileName;
   }

   /**
    * {@inheritDoc}
    */
   public String getMediaType()
   {
      return mediaType;
   }

   /**
    * {@inheritDoc}
    */
   public InputStream getStream() throws IOException
   {
      return new ByteArrayInputStream(bytes);
   }

   /**
    * {@inheritDoc}
    */
   public long length()
   {
      return length;
   }

}
