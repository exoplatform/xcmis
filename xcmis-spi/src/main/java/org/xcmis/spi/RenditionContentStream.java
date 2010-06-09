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

import org.xcmis.spi.utils.MimeType;

import java.io.InputStream;

/**
 * Rendition {@link ContentStream}.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public final class RenditionContentStream extends BaseContentStream
{

   private final String kind;

   private final int height;

   private final int width;

   public RenditionContentStream(byte[] bytes, String fileName, MimeType mediaType, String kind, int height, int width)
   {
      super(bytes, fileName, mediaType);
      this.kind = kind;
      this.height = height;
      this.width = width;
   }

   public RenditionContentStream(InputStream in, long length, String fileName, MimeType mediaType, String kind,
      int height, int width)
   {
      super(in, length, fileName, mediaType);
      this.kind = kind;
      this.height = height;
      this.width = width;
   }

   public RenditionContentStream(InputStream in, String fileName, MimeType mediaType, String kind, int height, int width)
   {
      super(in, fileName, mediaType);
      this.kind = kind;
      this.height = height;
      this.width = width;
   }

   public RenditionContentStream(byte[] bytes, String fileName, MimeType mediaType, String kind)
   {
      super(bytes, fileName, mediaType);
      this.kind = kind;
      this.width = -1;
      this.height = -1;
   }

   public RenditionContentStream(InputStream in, long length, String fileName, MimeType mediaType, String kind)
   {
      super(in, length, fileName, mediaType);
      this.kind = kind;
      this.width = -1;
      this.height = -1;
   }

   public RenditionContentStream(InputStream in, String fileName, MimeType mediaType, String kind)
   {
      super(in, fileName, mediaType);
      this.kind = kind;
      this.width = -1;
      this.height = -1;
   }

   /**
    * @return rendition width if rendition kind is 'cmis:thumbnail'. If
    *         rendition kind is different or width can't be determined then -1
    *         will be returned
    */
   public int getWidth()
   {
      return width;
   }

   /**
    * @return rendition height if rendition kind is 'cmis:thumbnail'. If
    *         rendition kind is different or height can't be determined then -1
    *         will be returned
    */
   public int getHeight()
   {
      return height;
   }

   /**
    * @return rendition kind
    */
   public String getKind()
   {
      return kind;
   }

}
