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

package org.xcmis.gwtframework.client.model.restatom;

/**
 * @author 
 * @version $Id:
 *
 */
public enum EnumLinkRelation 
{

   /**
    * Self.
    */
   SELF("self"), 
   
   /**
    * Edit.
    */
   EDIT("edit"), 
   
   /**
    * Edit media.
    */
   EDIT_MEDIA("edit-media"), 
   
   /**
    * Via.
    */
   VIA("via"), 
   
   /**
    * Up.
    */
   UP("up"), 
   
   /**
    * Down.
    */
   DOWN("down"), 
   
   /**
    * Alternate.
    */
   ALTERNATE("alternate"), 
   
   /**
    * Version history.
    */
   VERSION_HISTORY("version-history"), 
   
   /**
    * Current version.
    */
   CURRENT_VERSION("current-version"), 
   
   /**
    * Working copy.
    */
   WORKING_COPY("working-copy"), 
   
   /**
    * Service.
    */
   SERVICE("service"), 
   
   /**
    * Described by.
    */
   DESCRIBEDBY("describedby"), 
   
   /**
    * First.
    */
   FIRST("first"), 
   
   /**
    * Last.
    */
   LAST("last"), 
   
   /**
    * Next.
    */
   NEXT("next"), 
   
   /**
    * Previous.
    */
   PREVIOUS("previous"), 
   
   /**
    * CMIS allowable actions.
    */
   CMIS_ALLOWABLEACTIONS(
      "http://docs.oasis-open.org/ns/cmis/link/200908/allowableactions"), 
      
   /**
    * CMIS relationships.
    */
   CMIS_RELATIONSHIPS(
      "http://docs.oasis-open.org/ns/cmis/link/200908/relationships"), 
      
   /**
    * CMIS source.
    */
   CMIS_SOURCE(
      "http://docs.oasis-open.org/ns/cmis/link/200908/source"), 
      
   /**
    * CMIS target.
    */
   CMIS_TARGET(
      "http://docs.oasis-open.org/ns/cmis/link/200908/target"), 
      
   /**
    * CMIS policies.
    */
   CMIS_POLICIES(
      "http://docs.oasis-open.org/ns/cmis/link/200908/policies"), 
      
   /**
    * CMIS ACL.
    */
   CMIS_ACL(
      "http://docs.oasis-open.org/ns/cmis/link/200908/acl"), 
      
   /**
    * CMIS changes.
    */
   CMIS_CHANGES(
      "http://docs.oasis-open.org/ns/cmis/link/200908/changes"), 
      
   /**
    * CMIS foldertree.
    */
   CMIS_FOLDERTREE(
      "http://docs.oasis-open.org/ns/cmis/link/200908/foldertree"), 
      
   /**
    * CMIS type descendants.
    */
   CMIS_TYPEDESCENDANTS(
      "http://docs.oasis-open.org/ns/cmis/link/200908/typedescendants"), 
      
   /**
    * CMIS root descendants.
    */
   CMIS_ROOTDESCENDANTS(
      "http://docs.oasis-open.org/ns/cmis/link/200908/rootdescendants"), 
   
   /**
    * Enclosure.
    */
   ECLOSURE("enclosure");

   /**
    * Value.
    */
   private final String value;

   /**
    * @param v value
    */
   EnumLinkRelation(String v)
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
    * @return {@link EnumLinkRelation}
    */
   public static EnumLinkRelation fromValue(String v)
   {
      for (EnumLinkRelation c : EnumLinkRelation.values())
      {
         if (c.value.equals(v))
         {
            return c;
         }
      }
      throw new IllegalArgumentException(v);
   }

}
