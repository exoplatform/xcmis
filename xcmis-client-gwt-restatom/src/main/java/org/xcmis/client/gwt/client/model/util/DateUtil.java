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

package org.xcmis.client.gwt.client.model.util;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * Date converter
 * 
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class DateUtil
{
   /**
    * Z format full.
    */
   private static final String Z_FORMAT_FULL =
      "(\\d{4})-(\\d{2})-(\\d{2})[Tt](\\d{2}):(\\d{2}):(\\d{2})(\\.(\\d{1,3}))?[zZ]";

   /**
    * Z format full pattern.
    */
   private static final String Z_FORMAT_FULL_PATTERN = "yyyy-MM-dd'T'hh:mm:ss'.'SSS'Z'";

   /**
    * Z format.
    */
   private static final String Z_FORMAT = "(\\d{4})-(\\d{2})-(\\d{2})[Tt](\\d{2}):(\\d{2}):(\\d{2})[zZ]";

   /**
    * Z format pattern.
    */
   private static final String Z_FORMAT_PATTERN = "yyyy-MM-dd'T'hh:mm:ss'Z'";

   /**
    * TD format full.
    */
   private static final String TD_FORMAT_FULL =
      "(\\d{4})-(\\d{2})-(\\d{2})[Tt](\\d{2}):(\\d{2}):(\\d{2})(\\.(\\d{1,3}))?([+-])((\\d{2}):(\\d{2}))";

   /**
    * TD format full pattern plus.
    */
   private static final String TD_FORMAT_FULL_PATTERN_PLUS = "yyyy-MM-dd'T'hh:mm:ss'.'SSS'+'hh:mm";

   /**
    * TD format full pattern minus.
    */
   private static final String TD_FORMAT_FULL_PATTERN_MINUS = "yyyy-MM-dd'T'hh:mm:ss'.'SSS'-'hh:mm";

   /**
    * TD format.
    */
   private static final String TD_FORMAT =
      "(\\d{4})-(\\d{2})-(\\d{2})[Tt](\\d{2}):(\\d{2}):(\\d{2})([+-])((\\d{2}):(\\d{2}))";

   /**
    * TD format pattern minus.
    */
   private static final String TD_FORMAT_PATTERN_MINUS = "yyyy-MM-dd'T'hh:mm:ss'-'hh:mm";

   /**
    * TD format pattern plus.
    */
   private static final String TD_FORMAT_PATTERN_PLUS = "yyyy-MM-dd'T'hh:mm:ss'+'hh:mm";
   
   
   /**
    * Constructor.
    */
   protected DateUtil()
   {
      throw new UnsupportedOperationException(); // prevents calls from subclass
   }

   /**
    * Returns date type.
    * 
    * @param value value
    * @return {@link Date}
    */
   public static Date parseDate(String value)
   {
      value = value.toUpperCase();
      if (value.matches(Z_FORMAT))
      {
         DateTimeFormat date = DateTimeFormat.getFormat(Z_FORMAT_PATTERN);
         return date.parse(value);
      }
      else if (value.matches(Z_FORMAT_FULL))
      {
         DateTimeFormat date = DateTimeFormat.getFormat(Z_FORMAT_FULL_PATTERN);
         return date.parse(value);
      }
      else if (value.matches(TD_FORMAT))
      {
         if (value.contains("+"))
         {
            DateTimeFormat date = DateTimeFormat.getFormat(TD_FORMAT_PATTERN_PLUS);
            return date.parse(value);
         }
         else
         {
            DateTimeFormat date = DateTimeFormat.getFormat(TD_FORMAT_PATTERN_MINUS);
            return date.parse(value);
         }
      }
      else if (value.matches(TD_FORMAT_FULL))
      {
         if (value.contains("+"))
         {
            DateTimeFormat date = DateTimeFormat.getFormat(TD_FORMAT_FULL_PATTERN_PLUS);
            return date.parse(value);
         }
         else
         {
            DateTimeFormat date = DateTimeFormat.getFormat(TD_FORMAT_FULL_PATTERN_MINUS);
            return date.parse(value);
         }
      }
      else
      {
         return null;
      }
   }

}
