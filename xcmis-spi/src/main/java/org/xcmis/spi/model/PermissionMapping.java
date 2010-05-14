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

import org.xcmis.spi.CmisRuntimeException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Mapping of actions that can be performed to required permissions. Mapping
 * table contains key-permissions pairs. Because several allowable actions may
 * require permissions on more than one object – for example, moving a document
 * from one folder to another may require permissions on the document and each
 * of the folders – the mapping table is defined in terms of permission "keys",
 * where each key combines the name of the allowable action as the object for
 * which the principal needs the required permission. For example – the
 * "canMoveObject.Source" key indicates the permissions that the principal must
 * have on the "source folder" to move an object from that folder into another
 * folder. Permissions represented as set of one or more permissions that the
 * principal must have to be allowed to perform action.
 *
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class PermissionMapping
{

   public static final String CAN_GET_DESCENDENTS_FOLDER = "canGetDescendents.Folder";

   public static final String CAN_GET_FOLDER_TREE_FOLDER = "canGetFolderTree.Folder";

   public static final String CAN_GET_CHILDREN_FOLDER = "canGetChildren.Folder";

   public static final String CAN_GET_OBJECT_PARENTS_OBJECT = "canGetObjectParents.Object";

   public static final String CAN_GET_FOLDER_PARENT_FOLDER = "canGetFolderParent.Folder";

   public static final String CAN_CREATE_DOCUMENT_FOLDER = "canCreateDocument.Folder";

   public static final String CAN_CREATE_FOLDER_FOLDER = "canCreateFolder.Folder";

   public static final String CAN_CREATE_RELATIONSHIP_SOURCE = "canCreateRelationship.Source";

   public static final String CAN_CREATE_RELATIONSHIP_TARGET = "canCreateRelationship.Target";

   public static final String CAN_GET_PROPERTIES_OBJECT = "canGetProperties.Object";

   public static final String CAN_GET_CONTENT_STREAM_OBJECT = "canGetContentStream.Object";

   public static final String CAN_UPDATE_PROPERTIES_OBJECT = "canUpdateProperties.Object";

   public static final String CAN_MOVE_OBJECT_OBJECT = "canMoveObject.Object";

   public static final String CAN_MOVE_OBJECT_TARGET = "canMoveObject.Target";

   public static final String CAN_MOVE_OBJECT_SOURCE = "canMoveObject.Source";

   public static final String CAN_DELETE_OBJECT = "canDelete.Object";

   public static final String CAN_DELETE_FOLDER = "canDelete.Folder";

   public static final String CAN_DELETE_TREE_FOLDER = "canDeleteTree.Folder";

   public static final String CAN_SET_CONTENT_DOCUMENT = "canSetContentStream.Document";

   public static final String CAN_DELETE_CONTENT_DOCUMENT = "canDeleteContentStream.Document";

   public static final String CAN_GET_RENDITIONS_OBJECT = "canRenditions.Object";

   public static final String CAN_ADD_TO_FOLDER_OBJECT = "canAddToFolder.Object";

   public static final String CAN_ADD_TO_FOLDER_FOLDER = "canAddToFolder.Folder";

   public static final String CAN_REMOVE_OBJECT_FROM_FOLDER_OBJECT = "canRemoveObjectFromFolder.Object";

   public static final String CAN_REMOVE_OBJECT_FROM_FOLDER_FOLDER = "canRemoveObjectFromFolder.Folder";

   public static final String CAN_CHECKOUT_DOCUMENT = "canCheckout.Document";

   public static final String CAN_CANCEL_CHECKOUT_DOCUMENT = "canCancelCheckout.Document";

   public static final String CAN_CHECKIN_DOCUMENT = "canCheckin.Document";

   public static final String CAN_GET_ALL_VERSIONS_DOCUMENT = "canGetAllVersions.Document";

   public static final String CAN_GET_OBJECT_RELATIONSHIPS_OBJECT = "canGetObjectRelationships.Object";

   public static final String CAN_ADD_POLICY_OBJECT = "canAddPolicy.Object";

   public static final String CAN_ADD_POLICY_POLICY = "canAddPolicy.Policy";

   public static final String CAN_REMOVE_POLICY_OBJECT = "canRemovePolicy.Object";

   public static final String CAN_REMOVE_POLICY_POLICY = "canRemovePolicy.Policy";

   public static final String CAN_GET_APPLIED_POLICIES_OBJECT = "canGetAppliedPolicies.Object";

   public static final String CAN_GET_ACL_OBJECT = "canGetACL.Object";

   public static final String CAN_APPLY_ACL_OBJECT = "canApplyACL.Object";

   public static final Collection<String> DEFAULT_KEYS =
      Collections.unmodifiableCollection(Arrays.asList(CAN_GET_DESCENDENTS_FOLDER, CAN_GET_FOLDER_TREE_FOLDER,
         CAN_GET_CHILDREN_FOLDER, CAN_GET_OBJECT_PARENTS_OBJECT, CAN_GET_FOLDER_PARENT_FOLDER,
         CAN_CREATE_DOCUMENT_FOLDER, CAN_CREATE_FOLDER_FOLDER, CAN_CREATE_RELATIONSHIP_SOURCE,
         CAN_CREATE_RELATIONSHIP_TARGET, CAN_GET_PROPERTIES_OBJECT, CAN_GET_CONTENT_STREAM_OBJECT,
         CAN_UPDATE_PROPERTIES_OBJECT, CAN_MOVE_OBJECT_OBJECT, CAN_MOVE_OBJECT_TARGET, CAN_MOVE_OBJECT_SOURCE,
         CAN_DELETE_OBJECT, CAN_DELETE_FOLDER, CAN_DELETE_TREE_FOLDER, CAN_SET_CONTENT_DOCUMENT,
         CAN_DELETE_CONTENT_DOCUMENT, CAN_GET_RENDITIONS_OBJECT, CAN_ADD_TO_FOLDER_OBJECT, CAN_ADD_TO_FOLDER_FOLDER,
         CAN_REMOVE_OBJECT_FROM_FOLDER_OBJECT, CAN_REMOVE_OBJECT_FROM_FOLDER_FOLDER, CAN_CHECKOUT_DOCUMENT,
         CAN_CANCEL_CHECKOUT_DOCUMENT, CAN_CHECKIN_DOCUMENT, CAN_GET_ALL_VERSIONS_DOCUMENT,
         CAN_GET_OBJECT_RELATIONSHIPS_OBJECT, CAN_ADD_POLICY_OBJECT, CAN_ADD_POLICY_POLICY, CAN_REMOVE_POLICY_OBJECT,
         CAN_REMOVE_POLICY_POLICY, CAN_GET_APPLIED_POLICIES_OBJECT, CAN_GET_ACL_OBJECT, CAN_APPLY_ACL_OBJECT));

   private final Map<String, Collection<String>> all = new HashMap<String, Collection<String>>();

   public PermissionMapping(Map<String, Collection<String>> map)
   {
      if (map == null)
      {
         throw new CmisRuntimeException("Pemission mapping may not be null.");
      }
      all.putAll(map);
   }

   public PermissionMapping()
   {
   }

   /**
    * @param key permission key
    * @param permissions set of permission needed
    */
   public void put(String key, Collection<String> permissions)
   {
      all.put(key, permissions);
   }

   /**
    * @param key permission key
    * @return set of permission needed to perform action
    */
   public Collection<String> getPermissions(String key)
   {
      Collection<String> permissions = all.get(key);
      if (permissions != null)
      {
         return Collections.unmodifiableCollection(permissions);
      }
      return null;
   }

   public Map<String, Collection<String>> getAll()
   {
      return Collections.unmodifiableMap(all);
   }
}
