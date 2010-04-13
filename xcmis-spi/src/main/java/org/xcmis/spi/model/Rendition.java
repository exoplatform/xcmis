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

package org.xcmis.spi.model;

/**
 * CMIS rendition.
 *
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Rendition
{

   private String streamId;

   private String kind;

   private String mimeType;

   private long length;

   private int height;

   private int width;

   private String title;

   private String renditionDocumentId;

   public Rendition(String streamId, String kind, String mimeType, long length, int height, int width, String title,
      String renditionDocumentId)
   {
      this.streamId = streamId;
      this.kind = kind;
      this.mimeType = mimeType;
      this.length = length;
      this.height = height;
      this.width = width;
      this.title = title;
      this.renditionDocumentId = renditionDocumentId;
   }

   public Rendition()
   {
   }

   /**
    * Identifies the rendition stream ID.
    *
    * @return rendition stream ID
    */
   public String getStreamId()
   {
      return streamId;
   }

   /**
    * A categorization String associated with the rendition.
    *
    * @return rendition kind
    */
   public String getKind()
   {
      return kind;
   }

   /**
    * The mime-type of the rendition stream.
    *
    * @return rendition stream mime-type
    */
   public String getMimeType()
   {
      return mimeType;
   }

   /**
    * @return length of the rendition stream in bytes. This attribute is
    *         optional
    */
   public long getLength()
   {
      return length;
   }

   /**
    * Typically used for 'image' renditions (expressed as pixels). Should be
    * provided if kind of rendition is 'cmis:thumbnail'.
    *
    * @return rendition height
    */
   public int getHeight()
   {
      return height;
   }

   /**
    * Typically used for 'image' renditions (expressed as pixels). Should be
    * provided if kind of rendition is 'cmis:thumbnail'.
    *
    * @return rendition weight
    */
   public int getWidth()
   {
      return width;
   }

   /**
    * Optional human readable information about the rendition.
    *
    * @return rendition title or <code>null</code> if not provided
    */
   public String getTitle()
   {
      return title;
   }

   /**
    * If specified, then the rendition can also be accessed as a document object
    * in the CMIS services. If not set, then the rendition can only be accessed
    * via the rendition services.
    *
    * @return rendition document ID or <code>null</code> if rendition is not
    *         provided as separate document
    */
   public String getRenditionDocumentId()
   {
      return renditionDocumentId;
   }

   // --- Setters

   public void setStreamId(String streamId)
   {
      this.streamId = streamId;
   }

   public void setKind(String kind)
   {
      this.kind = kind;
   }

   public void setMimeType(String mimeType)
   {
      this.mimeType = mimeType;
   }

   public void setLength(long length)
   {
      this.length = length;
   }

   public void setHeight(int height)
   {
      this.height = height;
   }

   public void setWidth(int width)
   {
      this.width = width;
   }

   public void setTitle(String title)
   {
      this.title = title;
   }

   public void setRenditionDocumentId(String renditionDocumentId)
   {
      this.renditionDocumentId = renditionDocumentId;
   }

}
