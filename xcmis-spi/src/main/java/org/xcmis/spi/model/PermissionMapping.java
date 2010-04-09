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

package org.xcmis.spi.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Mapping of actions that can be performed to required permissions.
 *
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class PermissionMapping
{

   public static final String CAN_GET_DESCENDENTS_FOLDER = "canGetDescendents.Folder";

   public static final String CAN_GET_CHILDREN_FOLDER = "canGetChildren.Folder";

   public static final String CAN_GET_PARENTS_FOLDER = "canGetParents.Folder";

   public static final String CAN_GET_FOLDER_PARENT_OBJECT = "canGetFolderParent.Object";

   public static final String CAN_CREATE_DOCUMENT_FOLDER = "canCreateDocument.Folder";

   public static final String CAN_CREATE_FOLDER_FOLDER = "canCreateFolder.Folder";

   public static final String CAN_CREATE_RELATIONSHIP_SOURCE = "canCreateRelationship.Source";

   public static final String CAN_CREATE_RELATIONSHIP_TARGET = "canCreateRelationship.Target";

   public static final String CAN_GET_PROPERTIES_OBJECT = "canGetProperties.Object";

   public static final String CAN_VIEW_CONTENT_OBJECT = "canViewContent.Object";

   public static final String CAN_UPDATE_PROPERTIES_OBJECT = "canUpdateProperties.Object";

   public static final String CAN_MOVE_OBJECT = "canMove.Object";

   public static final String CAN_MOVE_TARGET = "canMove.Target";

   public static final String CAN_MOVE_SOURCE = "canMove.Source";

   public static final String CAN_DELETE_OBJECT = "canDelete.Object";

   public static final String CAN_DELETE_TREE_FOLDER = "canDeleteTree.Folder";

   public static final String CAN_SET_CONTENT_DOCUMENT = "canSetContent.Document";

   public static final String CAN_DELETE_CONTENT_DOCUMENT = "canDeleteContent.Document";

   public static final String CAN_ADD_TO_FOLDER_OBJECT = "canAddToFolder.Object";

   public static final String CAN_ADD_TO_FOLDER_FOLDER = "canAddToFolder.Folder";

   public static final String CAN_REMOVE_FROM_FOLDER_OBJECT = "canRemoveFromFolder.Object";

   public static final String CAN_REMOVE_FROM_FOLDER_FOLDER = "canRemoveFromFolder.Folder";

   public static final String CAN_CHECKOUT_DOCUMENT = "canCheckout.Document";

   public static final String CAN_CANCEL_CHECKOUT_DOCUMENT = "canCancelCheckout.Document";

   public static final String CAN_CHECKIN_DOCUMENT = "canCheckin.Document";

   public static final String CAN_GET_ALL_VERSIONS_VERSION_SERIES = "canGetAllVersions.VersionSeries";

   public static final String CAN_GET_OBJECT_RELATIONSHIPS_OBJECT = "canGetObjectRelationships.Object";

   public static final String CAN_ADD_POLICY_OBJECT = "canAddPolicy.Object";

   public static final String CAN_ADD_POLICY_POLICY = "canAddPolicy.Policy";

   public static final String CAN_REMOVE_POLICY_OBJECT = "canRemovePolicy.Object";

   public static final String CAN_REMOVE_POLICY_POLICY = "canRemovePolicy.Policy";

   public static final String CAN_GET_APPLIED_POLICIES_OBJECT = "canGetAppliedPolicies.Object";

   public static final String CAN_GET_ACL_OBJECT = "canGetACL.Object";

   public static final String CAN_APPLY_ACL_OBJECT = "canApplyACL.Object";

   private String key;

   private Set<String> permissions;

   public PermissionMapping(String key, Collection<String> permissions)
   {
      this.key = key;
      if (permissions != null)
      {
         this.permissions = new HashSet<String>(permissions);
      }
   }

   /**
    * @return actionName action name, by default expected to one of CAN_... .
    */
   public String getKey()
   {
      return key;
   }

   /**
    * @return set of permission needed to perform action {@link #getKey()}
    */
   public Collection<String> getPermissions()
   {
      if (permissions == null)
      {
         return Collections.emptyList();
      }
      return Collections.unmodifiableSet(permissions);
   }
}
