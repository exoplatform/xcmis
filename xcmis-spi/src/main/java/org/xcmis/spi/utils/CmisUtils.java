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

import org.xcmis.spi.AccessControlEntry;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.impl.AccessControlEntryImpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

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
         Calendar c1 = object1.getDate(CMIS.LAST_MODIFICATION_DATE);
         Calendar c2 = object2.getDate(CMIS.LAST_MODIFICATION_DATE);
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
      for (Map.Entry<String, Set<String>> e : permissions.entrySet())
      {
         AccessControlEntry ace = new AccessControlEntryImpl(e.getKey(), e.getValue());
         acl.add(ace);
      }
      return acl;
   }

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

   //   /**
   //    * Get "cmis:objectId" property.
   //    * 
   //    * @param properties the CMIS Object Type
   //    * @return the "cmis:objectId" property or <code>null</code>
   //    */
   //   public static String getObjectId(CmisPropertiesType properties)
   //   {
   //      CmisPropertyId property = (CmisPropertyId)getProperty(properties, CMIS.OBJECT_ID);
   //      if (property != null && property.getValue().size() > 0)
   //         return property.getValue().get(0);
   //      return null;
   //   }

   //   /**
   //    * Get property from CMIS object type with provided property name.
   //    * 
   //    * @param cmis the CMIS object type
   //    * @param propertyId the property ID
   //    * @return the CMIS property
   //    */
   //   public static CmisProperty getProperty(CmisPropertiesType properties, String propertyId)
   //   {
   //      if (properties != null)
   //      {
   //         List<CmisProperty> props = properties.getProperty();
   //         for (CmisProperty prop : props)
   //         {
   //            if (prop.getPropertyDefinitionId().equals(propertyId))
   //               return prop;
   //         }
   //      }
   //      return null;
   //
   //   }
   //
   //   /**
   //    * Get "cmis:name" property.
   //    * 
   //    * @param properties the CMIS Properties Type.
   //    * @return the "cmis:name" property or <code>null</code>
   //    */
   //   public static String getName(CmisPropertiesType properties)
   //   {
   //      CmisPropertyString property = (CmisPropertyString)getProperty(properties, CMIS.NAME);
   //      if (property != null && property.getValue().size() > 0)
   //         return property.getValue().get(0);
   //      return null;
   //   }
   //
   //   /**
   //    * Get "cmis:policyText" property.
   //    * 
   //    * @param properties the CMIS Properties Type.
   //    * @return the "cmis:policyText" property or <code>null</code>
   //    */
   //   public static String getPolicyText(CmisPropertiesType properties)
   //   {
   //      CmisPropertyString property = (CmisPropertyString)getProperty(properties, CMIS.POLICY_TEXT);
   //      if (property != null && property.getValue().size() > 0)
   //         return property.getValue().get(0);
   //      return null;
   //   }
   //
   //   /**
   //    * Get "cmis:sourceId" property.
   //    * 
   //    * @param properties the CMIS Properties Type.
   //    * @return the "cmis:sourceId" property or <code>null</code>
   //    */
   //   public static String getSourceId(CmisPropertiesType properties)
   //   {
   //      CmisPropertyId property = (CmisPropertyId)getProperty(properties, CMIS.SOURCE_ID);
   //      if (property != null && property.getValue().size() > 0)
   //         return property.getValue().get(0);
   //      return null;
   //   }
   //
   //   /**
   //    * Get "cmis:targetId" property.
   //    * 
   //    * @param properties the CMIS Properties Type.
   //    * @return the object "cmis:targetId" property or <code>null</code>
   //    */
   //   public static String getTargetId(CmisPropertiesType properties)
   //   {
   //      CmisPropertyId property = (CmisPropertyId)getProperty(properties, CMIS.TARGET_ID);
   //      if (property != null && property.getValue().size() > 0)
   //         return property.getValue().get(0);
   //      return null;
   //   }
   //
   //   /**
   //    * Get "cmis:objectTypeId" property.
   //    * 
   //    * @param properties the CMIS Properties Type.
   //    * @return the "cmis:objectTypeId" property or <code>null</code>
   //    */
   //   public static String getTypeId(CmisPropertiesType properties)
   //   {
   //      CmisPropertyId property = (CmisPropertyId)getProperty(properties, CMIS.OBJECT_TYPE_ID);
   //      if (property != null && property.getValue().size() > 0)
   //         return property.getValue().get(0);
   //      return null;
   //   }

   public static List<AccessControlEntry> mergeACLs(List<AccessControlEntry> existedAcl,
      List<AccessControlEntry> addAcl, List<AccessControlEntry> removeAcl)
   {
      Map<String, Set<String>> cache = new HashMap<String, Set<String>>();
      addAclToPermissionMap(cache, existedAcl);
      addAclToPermissionMap(cache, addAcl);
      removeAclFromPermissionMap(cache, removeAcl);
      return createAclFromPermissionMap(cache);
   }

   private static void addAclToPermissionMap(Map<String, Set<String>> map, List<AccessControlEntry> acl)
   {
      if (acl != null)
      {
         for (AccessControlEntry ace : acl)
         {
            String principal = ace.getPrincipal();
            if (principal == null)
               continue;

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

   private static void removeAclFromPermissionMap(Map<String, Set<String>> map, List<AccessControlEntry> acl)
   {
      if (acl != null)
      {
         for (AccessControlEntry ace : acl)
         {
            String principal = ace.getPrincipal();
            if (principal == null)
               continue;

            Set<String> permissions = map.get(principal);
            if (permissions != null)
            {
               permissions.removeAll(ace.getPermissions());
               if (permissions.size() == 0)
                  map.remove(principal);
            }
         }
      }
   }

   /**
    * Not instantiable.
    */
   private CmisUtils()
   {
   }

}
