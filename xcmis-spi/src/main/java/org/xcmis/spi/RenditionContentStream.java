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
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class RenditionContentStream extends BaseContentStream
{

   /** Rendition's kind. */
   private final String kind;

   /** Height of thumbnail rendition. */
   private int height = -1;

   /** Width of thumbnail rendition. */
   private int width = -1;

   /**
    * @param bytes source bytes of content
    * @param fileName name of content file
    * @param mediaType media type of content
    * @param kind rendition's kind
    */
   public RenditionContentStream(byte[] bytes, String fileName, MimeType mediaType, String kind)
   {
      super(bytes, fileName, mediaType);
      this.kind = kind;
   }

   /**
    * @param in source stream
    * @param length content length. Must be -1 if content length is unknown.
    * @param fileName name of content file
    * @param mediaType media type of content
    * @param kind rendition's kind
    */
   public RenditionContentStream(InputStream in, long length, String fileName, MimeType mediaType, String kind)
   {
      super(in, length, fileName, mediaType);
      this.kind = kind;
   }

   /**
    * @param in source stream
    * @param fileName name of content file
    * @param mediaType media type of content
    * @param kind rendition's kind
    */
   public RenditionContentStream(InputStream in, String fileName, MimeType mediaType, String kind)
   {
      super(in, fileName, mediaType);
      this.kind = kind;
   }

   /**
    * Get rendition height. It should provider height of thumbnail if rendition
    * type is 'cmis:thumbnail'.
    *
    * @return rendition height or -1 if not able to determine height or
    *         rendition kind is not 'cmis:thumbnail
    */
   public int getHeight()
   {
      return height;
   }

   /**
    * Get rendition kind. At the moment just one expected kind 'cmis:thumbnail'.
    *
    * @return rendition kind
    */
   public String getKind()
   {
      return kind;
   }

   /**
    * Get rendition width. It should provider width of thumbnail if rendition
    * type is 'cmis:thumbnail'.
    *
    * @return rendition width or -1 if not able to determine width or rendition
    *         kind is not 'cmis:thumbnail
    */
   public int getWidth()
   {
      return width;
   }

   /**
    * Set height or rendition.
    *
    * @param height rendition height
    */
   public void setHeight(int height)
   {
      this.height = height;
   }

   /**
    * Set width or rendition.
    *
    * @param width rendition width
    */
   public void setWidth(int width)
   {
      this.width = width;
   }

}
