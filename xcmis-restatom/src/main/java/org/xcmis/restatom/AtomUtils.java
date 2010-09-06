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

package org.xcmis.restatom;

import org.xcmis.spi.utils.CmisUtils;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: AtomUtils.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class AtomUtils
{

   /** The z-format pattern. */
   private static final Pattern Z_FORMAT =
      Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})[Tt](\\d{2}):(\\d{2}):(\\d{2})(\\.(\\d{1,3}))?[zZ]");

   /** The td-format pattern. */
   private static final Pattern TD_FORMAT =
      Pattern
         .compile("(\\d{4})-(\\d{2})-(\\d{2})[Tt](\\d{2}):(\\d{2}):(\\d{2})(\\.(\\d{1,3}))?([+-])((\\d{2}):(\\d{2}))");

   /**
    * Gets the atom date.
    * 
    * @param c the Calendar
    * @return the atom date
    */
   public static String getAtomDate(Calendar c)
   {
      return String.format("%04d-%02d-%02dT%02d:%02d:%02d.%03dZ", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c
         .get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND), c
         .get(Calendar.MILLISECOND));
   }

   /**
    * Parses the calendar.
    * 
    * @param date the date
    * @return the calendar
    */
   public static Calendar parseCalendar(String date)
   {
      Matcher m = Z_FORMAT.matcher(date);
      if (m.matches())
      {
         Calendar c = Calendar.getInstance();
         c.set(Calendar.YEAR, Integer.parseInt(m.group(1)));
         c.set(Calendar.MONTH, Integer.parseInt(m.group(2)) - 1);
         c.set(Calendar.DATE, Integer.parseInt(m.group(3)));
         c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(m.group(4)));
         c.set(Calendar.MINUTE, Integer.parseInt(m.group(5)));
         c.set(Calendar.SECOND, Integer.parseInt(m.group(6)));
         c.set(Calendar.MILLISECOND, //
            m.group(7) == null ? 0 : Integer.parseInt(m.group(8)));
         return c;
      }
      else
      {
         m = TD_FORMAT.matcher(date);
         if (m.matches())
         {
            int t = m.group(9).equals("+") ? 1 : -1;
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, Integer.parseInt(m.group(1)));
            c.set(Calendar.MONTH, Integer.parseInt(m.group(2)) - 1);
            c.set(Calendar.DATE, Integer.parseInt(m.group(3)));
            c.set(Calendar.HOUR_OF_DAY, //
               Integer.parseInt(m.group(4)) + t * Integer.parseInt(m.group(11)));
            c.set(Calendar.MINUTE, //
               Integer.parseInt(m.group(5)) + t * Integer.parseInt(m.group(12)));
            c.set(Calendar.SECOND, Integer.parseInt(m.group(6)));
            c.set(Calendar.MILLISECOND, //
               m.group(7) == null ? 0 : Integer.parseInt(m.group(8)));
            return c;
         }
         else
         {
            throw new IllegalArgumentException("Unsupported date format " + date);
         }
      }
   }

   /**
    * Parses the xml calendar.
    * 
    * @param date the date
    * @return the xML gregorian calendar
    */
   public static XMLGregorianCalendar parseXMLCalendar(String date)
   {
      return CmisUtils.fromCalendar(parseCalendar(date));
   }

}
