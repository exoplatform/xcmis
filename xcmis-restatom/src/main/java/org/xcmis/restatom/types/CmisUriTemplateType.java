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

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id: CmisUriTemplateType.java 34360 2009-07-22 23:58:59Z sunman $
 *
 */
public class CmisUriTemplateType
{

   protected String template;

   protected String type;

   protected String mediatype;

   /**
    * Gets the value of the template property.
    * @return
    */
   public String getTemplate()
   {
      return template;
   }

   /**
    * Sets the value of the template property.
    * @param value
    */
   public void setTemplate(String value)
   {
      this.template = value;
   }

   /**
    * Gets the value of the type property.
    * @return
    */
   public String getType()
   {
      return type;
   }

   /**
    * Sets the value of the type property.
    * @param value
    */
   public void setType(String value)
   {
      this.type = value;
   }

   /**
    * Gets the value of the mediatype property.
    * @return
    */
   public String getMediatype()
   {
      return mediatype;
   }

   /**
    * Sets the value of the mediatype property.
    * @param value
    */
   public void setMediatype(String value)
   {
      this.mediatype = value;
   }

}
