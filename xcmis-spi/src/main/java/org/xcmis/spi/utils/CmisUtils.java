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

package org.xcmis.spi.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.model.AccessControlEntry;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CmisUtils.java 332 2010-03-11 17:24:56Z andrew00x $
 */
public final class CmisUtils
{
   public static final Comparator<ObjectData> versionComparator = new Comparator<ObjectData>()
   {
      public int compare(ObjectData object1, ObjectData object2)
      {
         Calendar c1 = object1.getLastModificationDate();
         Calendar c2 = object2.getLastModificationDate();
         return c2.compareTo(c1);
      }

   };

   public static ItemsIterator<Object> EMPTY_ITEMS_ITERATOR = new EmptyItemsIterator();

   private static class EmptyItemsIterator implements ItemsIterator<Object>
   {

      public int size()
      {
         return 0;
      }

      public void skip(int skip) throws NoSuchElementException
      {
         throw new NoSuchElementException("skip");
      }

      public boolean hasNext()
      {
         return false;
      }

      public Object next()
      {
         throw new NoSuchElementException("next");
      }

      public void remove()
      {
         throw new UnsupportedOperationException();
      }

   }

   public static <T> ItemsIterator<T> emptyItemsIterator()
   {
      return (ItemsIterator<T>)EMPTY_ITEMS_ITERATOR;
   }

   public static List<AccessControlEntry> createAclFromPermissionMap(Map<String, Set<String>> permissions)
   {
      List<AccessControlEntry> acl = new ArrayList<AccessControlEntry>();
      if (permissions != null)
      {
         for (Map.Entry<String, Set<String>> e : permissions.entrySet())
         {
            AccessControlEntry ace = new AccessControlEntry(e.getKey(), e.getValue());
            acl.add(ace);
         }
      }
      return acl;
   }

   public static List<AccessControlEntry> mergeACLs(List<AccessControlEntry> existedAcl,
      List<AccessControlEntry> addAcl, List<AccessControlEntry> removeAcl)
   {
      Map<String, Set<String>> cache = new HashMap<String, Set<String>>();
      addAclToPermissionMap(cache, existedAcl);
      addAclToPermissionMap(cache, addAcl);
      removeAclFromPermissionMap(cache, removeAcl);
      return createAclFromPermissionMap(cache);
   }

   public static void addAclToPermissionMap(Map<String, Set<String>> map, List<AccessControlEntry> acl)
   {
      if (acl != null)
      {
         for (AccessControlEntry ace : acl)
         {
            String principal = ace.getPrincipal();
            if (principal == null)
            {
               continue;
            }

            Set<String> permissions = map.get(principal);
            if (permissions == null)
            {
               permissions = new HashSet<String>();
               map.put(principal, permissions);
            }
            permissions.addAll(ace.getPermissions());
         }
      }
   }

   public static void removeAclFromPermissionMap(Map<String, Set<String>> map, List<AccessControlEntry> acl)
   {
      if (acl != null)
      {
         for (AccessControlEntry ace : acl)
         {
            String principal = ace.getPrincipal();
            if (principal == null)
            {
               continue;
            }

            Set<String> permissions = map.get(principal);
            if (permissions != null)
            {
               permissions.removeAll(ace.getPermissions());
               if (permissions.size() == 0)
               {
                  map.remove(principal);
               }
            }
         }
      }
   }

   // ----- Dates ------

   /** The z-format pattern. */
   private static final Pattern Z_FORMAT =
      Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})[Tt](\\d{2}):(\\d{2}):(\\d{2})(\\.(\\d{1,3}))?[zZ]");

   /** The td-format pattern. */
   private static final Pattern TD_FORMAT =
      Pattern
         .compile("(\\d{4})-(\\d{2})-(\\d{2})[Tt](\\d{2}):(\\d{2}):(\\d{2})(\\.(\\d{1,3}))?([+-])((\\d{2}):(\\d{2}))");
   
   /** The ISO 8601 date format */
   public static final DateFormat ISO_8601_DATE_TIME = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");

   /**
    * Get XMLGregorianCalendar that is based on Calendar.
    *
    * @param calendar source Calendar
    * @return XMLGregorianCalendar
    */
   public static XMLGregorianCalendar fromCalendar(Calendar calendar)
   {
      XMLGregorianCalendar xmlCalendar;
      try
      {
         xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar();
      }
      catch (DatatypeConfigurationException e)
      {
         String msg = "Unable get XMLGregorianCalendar.";
         throw new RuntimeException(msg, e);
      }
      xmlCalendar.setYear(calendar.get(Calendar.YEAR));
      xmlCalendar.setMonth(calendar.get(Calendar.MONTH) + 1);
      xmlCalendar.setDay(calendar.get(Calendar.DAY_OF_MONTH));
      xmlCalendar.setTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar
         .get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND));
      return xmlCalendar;
   }

   /**
    * Convert calendar to ISO8601 string representation.
    *
    * @param c the Calendar
    * @return the ISO8601 string
    */
   public static String convertToString(Calendar c)
   {
	  String outDate = ISO_8601_DATE_TIME.format(c.getTime());
	  if (outDate.endsWith("Z"))
         return outDate;
	  else 
         return outDate.substring(0, outDate.length()-2) + ":" + outDate.substring(outDate.length()-2);
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
      return fromCalendar(parseCalendar(date));
   }

   /**
    * Not instantiable.
    */
   private CmisUtils()
   {
   }

}
