/*
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

package org.xcmis.spi;

import java.util.Collection;

/**
 * Mapping of actions that can be performed to required permissions.
 * 
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface PermissionMapping
{

   String CAN_GET_DESCENDENTS_FOLDER = "canGetDescendents.Folder";

   String CAN_GET_CHILDREN_FOLDER = "canGetChildren.Folder";

   String CAN_GET_PARENTS_FOLDER = "canGetParents.Folder";

   String CAN_GET_FOLDER_PARENT_OBJECT = "canGetFolderParent.Object";

   String CAN_CREATE_DOCUMENT_FOLDER = "canCreateDocument.Folder";

   String CAN_CREATE_FOLDER_FOLDER = "canCreateFolder.Folder";

   String CAN_CREATE_RELATIONSHIP_SOURCE = "canCreateRelationship.Source";

   String CAN_CREATE_RELATIONSHIP_TARGET = "canCreateRelationship.Target";

   String CAN_GET_PROPERTIES_OBJECT = "canGetProperties.Object";

   String CAN_VIEW_CONTENT_OBJECT = "canViewContent.Object";

   String CAN_UPDATE_PROPERTIES_OBJECT = "canUpdateProperties.Object";

   String CAN_MOVE_OBJECT = "canMove.Object";

   String CAN_MOVE_TARGET = "canMove.Target";

   String CAN_MOVE_SOURCE = "canMove.Source";

   String CAN_DELETE_OBJECT = "canDelete.Object";

   String CAN_DELETE_TREE_FOLDER = "canDeleteTree.Folder";

   String CAN_SET_CONTENT_DOCUMENT = "canSetContent.Document";

   String CAN_DELETE_CONTENT_DOCUMENT = "canDeleteContent.Document";

   String CAN_ADD_TO_FOLDER_OBJECT = "canAddToFolder.Object";

   String CAN_ADD_TO_FOLDER_FOLDER = "canAddToFolder.Folder";

   String CAN_REMOVE_FROM_FOLDER_OBJECT = "canRemoveFromFolder.Object";

   String CAN_REMOVE_FROM_FOLDER_FOLDER = "canRemoveFromFolder.Folder";

   String CAN_CHECKOUT_DOCUMENT = "canCheckout.Document";

   String CAN_CANCEL_CHECKOUT_DOCUMENT = "canCancelCheckout.Document";

   String CAN_CHECKIN_DOCUMENT = "canCheckin.Document";

   String CAN_GET_ALL_VERSIONS_VERSION_SERIES = "canGetAllVersions.VersionSeries";

   String CAN_GET_OBJECT_RELATIONSHIPS_OBJECT = "canGetObjectRelationships.Object";

   String CAN_ADD_POLICY_OBJECT = "canAddPolicy.Object";

   String CAN_ADD_POLICY_POLICY = "canAddPolicy.Policy";

   String CAN_REMOVE_POLICY_OBJECT = "canRemovePolicy.Object";

   String CAN_REMOVE_POLICY_POLICY = "canRemovePolicy.Policy";

   String CAN_GET_APPLIED_POLICIES_OBJECT = "canGetAppliedPolicies.Object";

   String CAN_GET_ACL_OBJECT = "canGetACL.Object";

   String CAN_APPLY_ACL_OBJECT = "canApplyACL.Object";

   /**
    * @return actionName action name, by default expected to one of CAN_... .
    */
   String getKey();
   
   /**
    * @return set of permission needed to perform action {@link #getKey()}
    */
   Collection<String> getPermissions();

}
