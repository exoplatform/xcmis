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

package org.xcmis.restatom.types;

import java.io.InputStream;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey
 *         Zavizionov</a>
 * @version $Id: CmisContentType.java 34360 2009-07-22 23:58:59Z sunman $
 * 
 */
public class CmisContentType
{

   protected String mediatype;

   protected InputStream base64;

   /**
    * Gets the value of the mediatype property.
    * 
    * @return media type string
    */
   public String getMediatype()
   {
      return mediatype;
   }

   /**
    * Sets the value of the mediatype property.
    * 
    * @param value String value
    */
   public void setMediatype(String value)
   {
      this.mediatype = value;
   }

   public InputStream getBase64() {
      return base64;      
   }
   
   public void setBase64(InputStream base64) {
      this.base64 = base64;
   }

   @Override
   protected void finalize() throws Throwable {
      if (base64 != null) {
         base64.close();
      }
      super.finalize();
   }
}
