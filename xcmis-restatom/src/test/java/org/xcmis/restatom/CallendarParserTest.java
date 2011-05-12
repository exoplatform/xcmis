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

import junit.framework.TestCase;

import java.util.Calendar;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CallendarParserTest.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class CallendarParserTest extends TestCase
{

   public void testGetString()
   {
      Calendar c = Calendar.getInstance();
      c.set(Calendar.YEAR, 2009);
      c.set(Calendar.MONTH, 7);
      c.set(Calendar.DATE, 20);
      c.set(Calendar.HOUR_OF_DAY, 16);
      c.set(Calendar.MINUTE, 31);
      c.set(Calendar.SECOND, 27);
      c.set(Calendar.MILLISECOND, 123);
      c.set(Calendar.ZONE_OFFSET, 2*60*60*1000);
      c.set(Calendar.DST_OFFSET, 1*60*60*1000);
      assertEquals("2009-08-20T16:31:27.123+03:00", AtomUtils.getAtomDate(c));
   }

   public void testParseCalendarZ()
   {
      String date = "2009-08-20T16:31:27Z";
      Calendar c = AtomUtils.parseCalendar(date);
      assertEquals(2009, c.get(Calendar.YEAR));
      assertEquals(7, c.get(Calendar.MONTH));
      assertEquals(20, c.get(Calendar.DATE));
      assertEquals(16, c.get(Calendar.HOUR_OF_DAY));
      assertEquals(31, c.get(Calendar.MINUTE));
      assertEquals(27, c.get(Calendar.SECOND));
      assertEquals(0, c.get(Calendar.MILLISECOND));
   }

   public void testParseCalendarZMilliSec()
   {
      String date = "2009-08-20T16:31:27.456Z";
      Calendar c = AtomUtils.parseCalendar(date);
      assertEquals(2009, c.get(Calendar.YEAR));
      assertEquals(7, c.get(Calendar.MONTH));
      assertEquals(20, c.get(Calendar.DATE));
      assertEquals(16, c.get(Calendar.HOUR_OF_DAY));
      assertEquals(31, c.get(Calendar.MINUTE));
      assertEquals(27, c.get(Calendar.SECOND));
      assertEquals(456, c.get(Calendar.MILLISECOND));
   }

   public void testParseCalendarTD()
   {
      String date = "2009-08-20T16:21:27+03:30";
      Calendar c = AtomUtils.parseCalendar(date);
      assertEquals(2009, c.get(Calendar.YEAR));
      assertEquals(7, c.get(Calendar.MONTH));
      assertEquals(20, c.get(Calendar.DATE));
      assertEquals(16, c.get(Calendar.HOUR_OF_DAY));
      assertEquals(21, c.get(Calendar.MINUTE));
      assertEquals(27, c.get(Calendar.SECOND));
      assertEquals(0, c.get(Calendar.MILLISECOND));
      assertEquals(210, c.get(Calendar.ZONE_OFFSET)/(60*1000));
   }

   public void testParseCalendarTDMilliSec()
   {
      String date = "2009-08-20T16:21:27.456-03:30";
      Calendar c = AtomUtils.parseCalendar(date);
      assertEquals(2009, c.get(Calendar.YEAR));
      assertEquals(7, c.get(Calendar.MONTH));
      assertEquals(20, c.get(Calendar.DATE));
      assertEquals(16, c.get(Calendar.HOUR_OF_DAY));
      assertEquals(21, c.get(Calendar.MINUTE));
      assertEquals(27, c.get(Calendar.SECOND));
      assertEquals(456, c.get(Calendar.MILLISECOND));
      assertEquals(-210, c.get(Calendar.ZONE_OFFSET)/(60*1000));
   }

}
