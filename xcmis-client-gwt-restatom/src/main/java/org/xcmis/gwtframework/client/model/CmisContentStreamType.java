/**
 *  Copyright (C) 2010 eXo Platform SAS.
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xcmis.gwtframework.client.model;


/**
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id:
 *
 */
public class CmisContentStreamType
{

   /**
    * Length.
    */
   protected Integer length;

   /**
    * Mime type.
    */
   protected String mimeType;

   /**
    * File name.
    */
   protected String filename;

   /**
    * URI.
    */
   protected String uri;

   /**
    * Stream.
    */
   protected String stream;

   /**
    * @return {@link Integer}
    */
   public Integer getLength()
   {
      return length;
   }

   /**
    * @param value value
    */
   public void setLength(Integer value)
   {
      this.length = value;
   }

   /**
    * @return String
    */
   public String getMimeType()
   {
      return mimeType;
   }

   /**
    * @param value value
    */
   public void setMimeType(String value)
   {
      this.mimeType = value;
   }

   /**
    * @return String
    */
   public String getFilename()
   {
      return filename;
   }

   /**
    * @param value value
    */
   public void setFilename(String value)
   {
      this.filename = value;
   }

   /**
    * @return String
    */
   public String getUri()
   {
      return uri;
   }

   /**
    * @param value value
    */
   public void setUri(String value)
   {
      this.uri = value;
   }

   /**
    * @return String
    */
   public String getStream()
   {
      return stream;
   }

   /**
    * @param value value
    */
   public void setStream(String value)
   {
      this.stream = value;
   }

}
