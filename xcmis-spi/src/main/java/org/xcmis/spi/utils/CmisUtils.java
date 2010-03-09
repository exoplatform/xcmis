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

import org.xcmis.core.CmisAccessControlEntryType;
import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.CmisAccessControlPrincipalType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisPropertyId;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.data.ObjectData;

import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
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

   public static CmisAccessControlListType createAclFromPermissionMap(Map<String, Set<String>> permissions)
   {
      CmisAccessControlListType acl = new CmisAccessControlListType();
      for (Map.Entry<String, Set<String>> e : permissions.entrySet())
      {
         CmisAccessControlEntryType ace = new CmisAccessControlEntryType();
         CmisAccessControlPrincipalType principal = new CmisAccessControlPrincipalType();
         principal.setPrincipalId(e.getKey());
         ace.getPermission().addAll(e.getValue());
         ace.setPrincipal(principal);
         acl.getPermission().add(ace);
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

   /**
    * Get object id from "cmis:objectId" property.
    * 
    * @param cmis the CMIS Object Type.
    * @return the object id property.
    */
   public static String getObjectId(CmisPropertiesType cmis)
   {
      return ((CmisPropertyId)getProperty(cmis, CMIS.OBJECT_ID)).getValue().get(0);
   }

   /**
    * Get property from CMIS object type with provided property name.
    * 
    * @param cmis the CMIS object type.
    * @param propName the property name.
    * @return the CMIS property.
    */
   public static CmisProperty getProperty(CmisPropertiesType cmis, String propName)
   {
      List<CmisProperty> props = cmis.getProperty();
      for (CmisProperty prop : props)
      {
         if (prop.getPropertyDefinitionId().equals(propName))
            return prop;
      }
      return null;
   }

   public static CmisAccessControlListType mergeAcls(CmisAccessControlListType existedAcl,
      CmisAccessControlListType addAcl, CmisAccessControlListType removeAcl)
   {
      Map<String, Set<String>> cache = new HashMap<String, Set<String>>();
      addAclToPermissionMap(cache, existedAcl);
      addAclToPermissionMap(cache, addAcl);
      removeAclFromPermissionMap(cache, removeAcl);
      return createAclFromPermissionMap(cache);
   }

   private static void addAclToPermissionMap(Map<String, Set<String>> map, CmisAccessControlListType acl)
   {
      if (acl != null)
      {
         for (CmisAccessControlEntryType ace : acl.getPermission())
         {
            String principal = ace.getPrincipal() != null ? ace.getPrincipal().getPrincipalId() : null;
            if (principal == null)
               continue;

            Set<String> permissions = map.get(principal);
            if (permissions == null)
            {
               permissions = new HashSet<String>();
               map.put(principal, permissions);
            }
            permissions.addAll(ace.getPermission());
         }
      }
   }

   private static void removeAclFromPermissionMap(Map<String, Set<String>> map, CmisAccessControlListType acl)
   {
      if (acl != null)
      {
         for (CmisAccessControlEntryType ace : acl.getPermission())
         {
            String principal = ace.getPrincipal() != null ? ace.getPrincipal().getPrincipalId() : null;
            if (principal == null)
               continue;

            Set<String> permissions = map.get(principal);
            if (permissions != null)
            {
               permissions.removeAll(ace.getPermission());
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
