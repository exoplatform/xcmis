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

package org.xcmis.spi.model.impl;

import org.xcmis.spi.model.Rendition;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class RenditionImpl implements Rendition
{

   private String streamId;

   private String kind;

   private String mimeType;

   private long length;

   private int height;

   private int width;

   private String title;

   private String renditionDocumentId;

   public RenditionImpl()
   {
   }

   public RenditionImpl(String streamId, String kind, String mimeType, long length, int height, int width,
      String title, String renditionDocumentId)
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

   /**
    * {@inheritDoc}
    */
   public String getStreamId()
   {
      return streamId;
   }

   /**
    * {@inheritDoc}
    */
   public String getKind()
   {
      return kind;
   }

   /**
    * {@inheritDoc}
    */
   public String getMimeType()
   {
      return mimeType;
   }

   /**
    * {@inheritDoc}
    */
   public long getLength()
   {
      return length;
   }

   /**
    * {@inheritDoc}
    */
   public int getHeight()
   {
      return height;
   }

   /**
    * {@inheritDoc}
    */
   public int getWidth()
   {
      return width;
   }

   /**
    * {@inheritDoc}
    */
   public String getTitle()
   {
      return title;
   }

   /**
    * {@inheritDoc}
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
