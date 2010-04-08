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

package org.xcmis.client.gwt.client.model;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class Rendition
{

   /**
    * Stream id.
    */
   private String streamId;

   /**
    * Kind.
    */
   private String kind;

   /**
    * MIME type.
    */
   private String mimeType;

   /**
    * Leght.
    */
   private long length;

   /**
    * Height.
    */
   private int height;

   /**
    * Width.
    */
   private int width;

   /**
    * Title.
    */
   private String title;

   /**
    * Rendition document id.
    */
   private String renditionDocumentId;

   /**
    * Default constructor
    */
   public Rendition()
   {
   }

   /**
    * @param streamId stream id
    * @param kind kind
    * @param mimeType mime type
    * @param length lenght
    * @param height height
    * @param width width
    * @param title title
    * @param renditionDocumentId rendition document id
    */
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

 

}
