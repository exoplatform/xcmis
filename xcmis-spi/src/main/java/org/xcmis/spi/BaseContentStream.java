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




import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: BaseContentStream.java 316 2010-03-09 15:20:28Z andrew00x $
 */
public class BaseContentStream implements ContentStream
{
   /** Stream. */
   private final InputStream in;

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
   public BaseContentStream(byte[] bytes, String fileName, String mediaType)
   {
      this(new ByteArrayInputStream(bytes), bytes.length, fileName, mediaType);
   }

   /**
    * @param in source stream
    * @param fileName name of content file
    * @param length content length. Must be -1 if content length is unknown.
    * @param mediaType media type of content
    */
   public BaseContentStream(InputStream in, long length, String fileName, String mediaType)
   {
      this.in = in;
      this.fileName = fileName;
      this.mediaType = mediaType;
      this.length = length;
   }

   /**
    * @param in source stream
    * @param fileName name of content file
    * @param mediaType media type of content
    */
   public BaseContentStream(InputStream in, String fileName, String mediaType)
   {
      this(in, -1, fileName, mediaType);
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
      return in;
   }

   /**
    * {@inheritDoc}
    */
   public long length()
   {
      return length;
   }

}
