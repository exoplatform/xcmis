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

package org.xcmis.client.gwt.client;

import org.xcmis.client.gwt.client.model.restatom.EnumArguments;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public interface CmisArguments
{
   /**
    *  All versions.
    */
   String ALL_VERSIONS = "allVersions";

   /**
    * Child types.
    */
   String CHILD_TYPES = EnumArguments.CHILD_TYPES.value();

   /**
    * Continue on failure.
    */
   String CONTINUE_ON_FAILURE = EnumArguments.CONTINUE_ON_FAILURE.value();

   /**
    * Checkin.
    */
   String CHECKIN = EnumArguments.CHECKIN.value();

   /**
    * Checkin comment.
    */
   String CHECKIN_COMMENT = EnumArguments.CHECKIN_COMMENT.value();

   /**
    * Depth.
    */
   String DEPTH = EnumArguments.DEPTH.value();

   /**
    * Direction.
    */
   String DIRECTION = EnumArguments.DIRECTION.value();

   /**
    * Filter.
    */
   String FILTER = EnumArguments.FILTER.value();

   /**
    * Folder id.
    */
   String FOLDER_ID = EnumArguments.FOLDER_ID.value();

   /**
    * Include acl.
    */
   String INCLUDE_ACL = EnumArguments.INCLUDE_ACL.value();

   /**
    * Include allowable actions.
    */
   String INCLUDE_ALLOWABLE_ACTIONS = EnumArguments.INCLUDE_ALLOWABLE_ACTIONS.value();

   /**
    * Include properties.
    */
   String INCLUDE_PROPERTIES = EnumArguments.INCLUDE_PROPERTIES.value();

   /**
    * Include path segment.
    */
   String INCLUDE_PATH_SEGMENT = EnumArguments.INCLUDE_PATH_SEGMENT.value();

   /**
    * Include relative path segment.
    */
   String INCLUDE_RELATIVE_PATH_SEGMENT = EnumArguments.INCLUDE_RELATIVE_PATH_SEGMENT.value();

   /**
    * Include property definitions.
    */
   String INCLUDE_PROPERTY_DEFINITIONS = EnumArguments.INCLUDE_PROPERTY_DEFINITIONS.value();

   /**
    * Include policy ids.
    */
   String INCLUDE_POLICY_IDS = EnumArguments.INCLUDE_POLICY_IDS.value();

   /**
    * Include relationships.
    */
   String INCLUDE_RELATIONSHIPS = EnumArguments.INCLUDE_RELATIONSHIPS.value();

   /**
    * Include sub relationship types.
    */
   String INCLUDE_SUB_RELATIONSHIP_TYPES = EnumArguments.INCLUDE_SUB_RELATIONSHIP_TYPES.value();

   /**
    * Length.
    */
   String LENGTH = EnumArguments.LENGTH.value();

   /**
    * Major.
    */
   String MAJOR = EnumArguments.MAJOR.value();

   /**
    * Max items.
    */
   String MAX_ITEMS = EnumArguments.MAX_ITEMS.value();

   /**
    * Overwrite flag.
    */
   String OVERWRITE_FLAG = EnumArguments.OVERWRITE_FLAG.value();

   /**
    * Only basic permissions.
    */
   String ONLY_BASIC_PERMISSIONS = "onlyBasicPermissions";

   /**
    * Relationship direction.
    */
   String RELATIONSHIP_DIRECTION = EnumArguments.RELATIONSHIP_DIRECTION.value();

   /**
    * Relationship type.
    */
   String RELATIONSHIP_TYPE = EnumArguments.RELATIONSHIP_TYPE.value();

   /**
    * Rendition filter.
    */
   String RENDITION_FILTER = EnumArguments.RENDITION_FILTER.value();

   /**
    * Remove from.
    */
   String REMOVE_FROM = EnumArguments.REMOVE_FROM.value();

   /**
    * Repository id.
    */
   String REPOSITORY_ID = EnumArguments.REPOSITORY_ID.value();

   /**
    * Return version.
    */
   String RETURN_VERSION = EnumArguments.RETURN_VERSION.value();

   /**
    * Skip count.
    */
   String SKIP_COUNT = EnumArguments.SKIP_COUNT.value();

   /**
    * Source folder id.
    */
   String SOURCE_FOLDER_ID = EnumArguments.SOURCE_FOLDER_ID.value();

   /**
    * This version.
    */
   String THIS_VERSION = EnumArguments.THIS_VERSION.value();

   /**
    * Type id.
    */
   String TYPE_ID = EnumArguments.TYPE_ID.value();

   /**
    * Types.
    */
   String TYPES = EnumArguments.TYPES.value();

   /**
    * Unfile objects.
    */
   String UNFILE_OBJECTS = EnumArguments.UNFILE_OBJECTS.value();

   /**
    * Versioning state.
    */
   String VERSIONING_STATE = EnumArguments.VERSIONING_SSTATE.value();
}
