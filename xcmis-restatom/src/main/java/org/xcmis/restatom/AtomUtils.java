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
import java.util.Date;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: AtomUtils.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class AtomUtils
{

   /**
    * Gets the atom date.
    * 
    * @param c the Calendar
    * @return the atom date
    */
   public static String getAtomDate(Calendar c)
   {
      return CmisUtils.convertToString(c);
   }

   /**
    * Gets the atom date.
    * 
    * @param d the Date
    * @return the atom date
    */
   public static String getAtomDate(Date d)
   {
      Calendar c = Calendar.getInstance();
      c.setTime(d);
      return CmisUtils.convertToString(c);
   }

   /**
    * Parses the calendar.
    * 
    * @param date the date
    * @return the calendar
    */
   public static Calendar parseCalendar(String date)
   {
      return CmisUtils.parseCalendar(date);
   }

}
