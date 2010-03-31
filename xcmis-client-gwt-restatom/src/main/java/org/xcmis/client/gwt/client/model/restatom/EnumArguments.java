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

package org.xcmis.client.gwt.client.model.restatom;

/**
 * @author 
 * @version $Id:
 *
 */
public enum EnumArguments 
{
   
   /**
    * Child types.
    */
   CHILD_TYPES("childTypes"), 
   
   /**
    * Continue on failure.
    */
   CONTINUE_ON_FAILURE("continueOnFailure"), 
   
   /**
    * Checkin.
    */
   CHECKIN("checkin"), 
   
   /**
    * Checkin comment.
    */
   CHECKIN_COMMENT("checkinComment"), 
   
   /**
    * Depth.
    */
   DEPTH("depth"), 
   
   /**
    * Direction.
    */
   DIRECTION("direction"), 
   
   /**
    * Filter.
    */
   FILTER("filter"), 
   
   /**
    * Folder id.
    */
   FOLDER_ID("folderId"), 
   
   /**
    * Include ACL.
    */
   INCLUDE_ACL("includeACL"), 
   
   /**
    * Include allowable actions.
    */
   INCLUDE_ALLOWABLE_ACTIONS("includeAllowableActions"), 
   
   /**
    * Include properties.
    */
   INCLUDE_PROPERTIES("includeProperties"), 
   
   /**
    * Include path segment.
    */
   INCLUDE_PATH_SEGMENT("includePathSegment"), 
   
   /**
    * Include relative path segment.
    */
   INCLUDE_RELATIVE_PATH_SEGMENT("includeRelativePathSegment"), 
   
   /**
    * Include property definitions.
    */
   INCLUDE_PROPERTY_DEFINITIONS("includePropertyDefinitions"), 
   
   /**
    * Include policy ids.
    */
   INCLUDE_POLICY_IDS("includePolicyIds"), 
   
   /**
    * Include relationships.
    */
   INCLUDE_RELATIONSHIPS("includeRelationships"), 
   
   /**
    * Include sub relationship types.
    */
   INCLUDE_SUB_RELATIONSHIP_TYPES("includeSubRelationshipTypes"), 
   
   /**
    * Length.
    */
   LENGTH("length"), 
   
   /**
    * Major.
    */
   MAJOR("major"), 
   
   /**
    * Max items.
    */
   MAX_ITEMS("maxItems"), 
   
   /**
    * Overwrite flag.
    */
   OVERWRITE_FLAG("overwriteFlag"), 
   
   /**
    * Relationship direction.
    */
   RELATIONSHIP_DIRECTION("relationshipDirection"), 
   
   /**
    * Relationship type.
    */
   RELATIONSHIP_TYPE("relationshipType"), 
   
   /**
    * Rendition filter.
    */
   RENDITION_FILTER("renditionFilter"), 
   
   /**
    * Remove from.
    */
   REMOVE_FROM("removeFrom"), 
   
   /**
    * Repository id.
    */
   REPOSITORY_ID("repositoryId"), 
   
   /**
    * Return version.
    */
   RETURN_VERSION("returnVersion"), 
   
   /**
    * Skip count.
    */
   SKIP_COUNT("skipCount"), 
   
   /**
    * Source folder id.
    */
   SOURCE_FOLDER_ID("sourceFolderId"), 
   
   /**
    * This version.
    */
   THIS_VERSION("thisVersion"), 
   
   /**
    * Type id.
    */
   TYPE_ID("typeId"), 
   
   /**
    * Types.
    */
   TYPES("types"), 
   
   /**
    * Unfile objects.
    */
   UNFILE_OBJECTS("unfileObjects"), 
   
   /**
    * Versioning sstate.
    */
   VERSIONING_SSTATE("versioningSstate");

   /**
    * Value.
    */
   private final String value;

   /**
    * @param v value
    */
   EnumArguments(String v)
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
    * @return {@link EnumArguments}
    */
   public static EnumArguments fromValue(String v)
   {
      for (EnumArguments c : EnumArguments.values())
      {
         if (c.value.equals(v))
         {
            return c;
         }
      }
      throw new IllegalArgumentException(v);
   }

}
