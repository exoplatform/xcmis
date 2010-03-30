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

import org.xcmis.spi.object.ContentStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: SimpleContentStream.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class SimpleContentStream implements ContentStream
{

   /** The bytes[]. */
   private final byte[] bytes;

   /** Name of content file. */
   private final String fileName;

   /** Media type of stream. */
   private final String mediaType;

   /** Content length. */
   private long length;

   /**
    * @param bytes source bytes of content
    * @param fileName name of content file
    * @param mediaType media type of content
    */
   public SimpleContentStream(byte[] bytes, String fileName, String mediaType)
   {
      this.bytes = bytes;
      this.fileName = fileName;
      this.mediaType = mediaType;
      this.length = bytes.length;
   }

   /**
    * Gets the data.
    * 
    * @return the data
    * @throws IOException Signals that an I/O exception has occurred.
    */
   public byte[] getData() throws IOException
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
