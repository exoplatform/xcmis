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

package org.xcmis.client.gwt.client.model;

/**
 * @author 
 * @version $Id: 
 *
 */
public enum EnumCapabilityChanges 
{
   /**
    * None.
    */
   NONE("none"), 
   
   /**
    * Object ids only.
    */
   OBJECTIDSONLY("objectidsonly"), 
   
   /**
    * Properties.
    */
   PROPERTIES("properties"), 
   
   /**
    * Include ACL.
    */
   INCLUDE_ACL("includeACL"), 
   
   /**
    * Include properties.
    */
   INCLUDE_PROPERTIES("includeProperties"), 
   
   /**
    * Include folders.
    */
   INCLUDE_FOLDERS("includeFolders"), 
   
   /**
    * Include documents.
    */
   INCLUDE_DOCUMENTS("includeDocuments"), 
   
   /**
    * Include relationships.
    */
   INCLUDE_RELATIONSHIPS("includeRelationships"), 
   
   /**
    * Include policies.
    */
   INCLUDE_POLICIES("includePolicies"), 
   
   /**
    * All.
    */
   ALL("all");
   
   /**
    * Value.
    */
   private final String value;

   /**
    * @param v value
    */
   EnumCapabilityChanges(String v)
   {
      value = v;
   }

   /**
    * @return String
    */
   public String value()
   {
      return value;
   }

   /**
    * @param v value
    * @return {@link EnumCapabilityChanges}
    */
   public static EnumCapabilityChanges fromValue(String v)
   {
      for (EnumCapabilityChanges c : EnumCapabilityChanges.values())
      {
         if (c.value.equals(v))
         {
            return c;
         }
      }
      throw new IllegalArgumentException(v);
   }

}
