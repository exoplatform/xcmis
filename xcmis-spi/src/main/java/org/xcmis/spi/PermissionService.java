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

package org.xcmis.spi;

import org.exoplatform.services.security.Identity;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.AllowableActions;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.PermissionMapping;
import org.xcmis.spi.model.RepositoryCapabilities;
import org.xcmis.spi.model.RepositoryInfo;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.utils.CmisUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class PermissionService
{

   private final PermissionMapping permissionMapping;

   private final RepositoryInfo repositoryInfo;

   public PermissionService(RepositoryInfo repositoryInfo)
   {
      this.repositoryInfo = repositoryInfo;
      this.permissionMapping = repositoryInfo.getAclCapability().getMapping();
   }

   /**
    * Calculate allowable actions for specified object.
    *
    * @param object object
    * @param userIdentity user's identity
    * @return allowable actions for object
    * @see Identity
    */
   public AllowableActions calculateAllowableActions(ObjectData object, Identity userIdentity)
   {
      AllowableActions actions = new AllowableActions();
      TypeDefinition type = object.getTypeDefinition();
      RepositoryCapabilities capabilities = repositoryInfo.getCapabilities();

      for (String action : AllowableActions.DEFAULT)
      {
         if (AllowableActions.CAN_GET_DESCENDENTS.equals(action))
         {
            if (capabilities.isCapabilityGetDescendants()
               && BaseType.FOLDER == type.getBaseId()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_GET_DESCENDENTS_FOLDER),
                  userIdentity))
            {
               actions.setCanGetDescendants(true);
            }
         }
         else if (AllowableActions.CAN_GET_FOLDER_TREE.equals(action))
         {
            if (capabilities.isCapabilityGetFolderTree()
               && BaseType.FOLDER == type.getBaseId()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_GET_FOLDER_TREE_FOLDER),
                  userIdentity))
            {
               actions.setCanGetFolderTree(true);
            }
         }
         else if (AllowableActions.CAN_GET_CHILDREN.equals(action))
         {
            if (BaseType.FOLDER == type.getBaseId()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_GET_CHILDREN_FOLDER),
                  userIdentity))
            {
               actions.setCanGetChildren(true);
            }
         }
         else if (AllowableActions.CAN_GET_OBJECT_PARENTS.equals(action))
         {
            if (type.isFileable()
               && hasPermission(object, permissionMapping
                  .getPermissions(PermissionMapping.CAN_GET_OBJECT_PARENTS_OBJECT), userIdentity))
            {
               actions.setCanGetObjectParents(true);
            }
         }
         else if (AllowableActions.CAN_GET_FOLDER_PARENT.equals(action))
         {
            if (BaseType.FOLDER == type.getBaseId()
               && hasPermission(object, permissionMapping
                  .getPermissions(PermissionMapping.CAN_GET_FOLDER_PARENT_FOLDER), userIdentity))
            {
               actions.setCanGetFolderParent(true);
            }
         }
         else if (AllowableActions.CAN_CREATE_DOCUMENT.equals(action))
         {
            if (BaseType.FOLDER == type.getBaseId()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_CREATE_DOCUMENT_FOLDER),
                  userIdentity))
            {
               actions.setCanCreateDocument(true);
            }
         }
         else if (AllowableActions.CAN_CREATE_FOLDER.equals(action))
         {
            if (BaseType.FOLDER == type.getBaseId()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_CREATE_FOLDER_FOLDER),
                  userIdentity))
            {
               actions.setCanCreateFolder(true);
            }
         }
         else if (AllowableActions.CAN_CREATE_RELATIONSHIP.equals(action))
         {
            if (BaseType.RELATIONSHIP != type.getBaseId()
               && hasPermission(object, permissionMapping
                  .getPermissions(PermissionMapping.CAN_CREATE_RELATIONSHIP_SOURCE), userIdentity)
               && hasPermission(object, permissionMapping
                  .getPermissions(PermissionMapping.CAN_CREATE_RELATIONSHIP_TARGET), userIdentity))
            {
               actions.setCanCreateRelationship(true);
            }
         }
         else if (AllowableActions.CAN_GET_PROPERTIES.equals(action))
         {
            if (hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_GET_PROPERTIES_OBJECT),
               userIdentity))
            {
               actions.setCanGetProperties(true);
            }
         }
         else if (AllowableActions.CAN_GET_CONTENT_STREAM.equals(action))
         {
            if (BaseType.DOCUMENT == type.getBaseId()
               && hasPermission(object, permissionMapping
                  .getPermissions(PermissionMapping.CAN_GET_CONTENT_STREAM_OBJECT), userIdentity))
            {
               actions.setCanGetContentStream(true);
            }
         }
         else if (AllowableActions.CAN_UPDATE_PROPERTIES.equals(action))
         {
            if (hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_UPDATE_PROPERTIES_OBJECT),
               userIdentity))
            {
               actions.setCanUpdateProperties(true);
            }
         }
         else if (AllowableActions.CAN_MOVE_OBJECT.equals(action))
         {
            if (type.isFileable()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_MOVE_OBJECT_OBJECT),
                  userIdentity))
            {
               actions.setCanMoveObject(true);
            }
         }
         else if (AllowableActions.CAN_DELETE.equals(action))
         {
            if (BaseType.FOLDER == type.getBaseId())
            {
               if (!((FolderData)object).hasChildren()
                  && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_DELETE_OBJECT),
                     userIdentity))
               {
                  actions.setCanDeleteObject(true);
               }
            }
            else if (hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_DELETE_OBJECT),
               userIdentity))
            {
               actions.setCanDeleteObject(true);
            }
         }
         else if (AllowableActions.CAN_DELETE_TREE.equals(action))
         {
            if (BaseType.FOLDER == type.getBaseId()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_DELETE_TREE_FOLDER),
                  userIdentity))
            {
               actions.setCanDeleteTree(true);
            }
         }
         else if (AllowableActions.CAN_SET_CONTENT_STREAM.equals(action))
         {
            if (BaseType.DOCUMENT == type.getBaseId()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_SET_CONTENT_DOCUMENT),
                  userIdentity))
            {
               actions.setCanSetContentStream(true);
            }
         }
         else if (AllowableActions.CAN_DELETE_CONTENT_STREAM.equals(action))
         {
            if (BaseType.DOCUMENT == type.getBaseId()
               && hasPermission(object,
                  permissionMapping.getPermissions(PermissionMapping.CAN_DELETE_CONTENT_DOCUMENT), userIdentity))
            {
               actions.setCanDeleteContentStream(true);
            }
         }
         else if (AllowableActions.CAN_GET_RENDITIONS.equals(action))
         {
            if (BaseType.DOCUMENT == type.getBaseId()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_GET_RENDITIONS_OBJECT),
                  userIdentity))
            {
               actions.setCanGetRenditions(true);
            }
         }
         else if (AllowableActions.CAN_ADD_TO_FOLDER.equals(action))
         {
            if (type.isFileable()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_ADD_TO_FOLDER_OBJECT),
                  userIdentity))
            {
               actions.setCanAddObjectToFolder(true);
            }
         }
         else if (AllowableActions.CAN_REMOVE_OBJECT_FROM_FOLDER.equals(action))
         {
            if (type.isFileable()
               && hasPermission(object, permissionMapping
                  .getPermissions(PermissionMapping.CAN_REMOVE_OBJECT_FROM_FOLDER_OBJECT), userIdentity))
            {
               actions.setCanRemoveObjectFromFolder(true);
            }
         }
         else if (AllowableActions.CAN_CHECKOUT.equals(action))
         {
            if (type.isVersionable()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_CHECKOUT_DOCUMENT),
                  userIdentity))
            {
               actions.setCanCheckOut(true);
            }
         }
         else if (AllowableActions.CAN_CANCEL_CHECKOUT.equals(action))
         {
            if (type.isVersionable()
               && ((DocumentData)object).isVersionSeriesCheckedOut()
               && hasPermission(object, permissionMapping
                  .getPermissions(PermissionMapping.CAN_CANCEL_CHECKOUT_DOCUMENT), userIdentity))
            {
               actions.setCanCancelCheckOut(true);
            }
         }
         else if (AllowableActions.CAN_CHECKIN.equals(action))
         {
            if (type.isVersionable()
               && ((DocumentData)object).isPWC()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_CHECKIN_DOCUMENT),
                  userIdentity))
            {
               actions.setCanCancelCheckOut(true);
            }
         }
         else if (AllowableActions.CAN_GET_ALL_VERSIONS.equals(action))
         {
            if (BaseType.DOCUMENT == type.getBaseId()
               && hasPermission(object, permissionMapping
                  .getPermissions(PermissionMapping.CAN_GET_ALL_VERSIONS_DOCUMENT), userIdentity))
            {
               actions.setCanGetAllVersions(true);
            }
         }
         else if (AllowableActions.CAN_GET_OBJECT_RELATIONSHIPS.equals(action))
         {
            if (BaseType.RELATIONSHIP != type.getBaseId()
               && hasPermission(object, permissionMapping
                  .getPermissions(PermissionMapping.CAN_GET_OBJECT_RELATIONSHIPS_OBJECT), userIdentity))
            {
               actions.setCanCreateRelationship(true);
            }
         }
         else if (AllowableActions.CAN_ADD_POLICY.equals(action))
         {
            if (type.isControllablePolicy()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_ADD_POLICY_OBJECT),
                  userIdentity))
            {
               actions.setCanApplyPolicy(true);
            }
         }
         else if (AllowableActions.CAN_REMOVE_POLICY.equals(action))
         {
            if (type.isControllablePolicy()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_REMOVE_POLICY_OBJECT),
                  userIdentity))
            {
               actions.setCanRemovePolicy(true);
            }
         }
         else if (AllowableActions.CAN_GET_APPLIED_POLICIES.equals(action))
         {
            if (hasPermission(object, permissionMapping
               .getPermissions(PermissionMapping.CAN_GET_APPLIED_POLICIES_OBJECT), userIdentity))
            {
               actions.setCanGetAppliedPolicies(true);
            }
         }
         else if (AllowableActions.CAN_GET_ACL.equals(action))
         {
            if (hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_GET_ACL_OBJECT),
               userIdentity))
            {
               actions.setCanGetACL(true);
            }
         }
         else if (AllowableActions.CAN_APPLY_ACL.equals(action))
         {
            if (type.isControllableACL()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_APPLY_ACL_OBJECT),
                  userIdentity))
            {
               actions.setCanApplyACL(true);
            }
         }
      }

      return actions;
   }

   /**
    * @param object object
    * @param permissions set of actions to be checked
    * @param userIdentity user's identity
    * @return <code>true</code> if user has all <code>permissions</code> and
    *         <code>false</code> otherwise
    */
   public boolean hasPermission(ObjectData object, Collection<String> permissions, Identity userIdentity)
   {
      if (permissions == null || permissions.size() == 0)
      {
         throw new CmisRuntimeException("Permissions set may not be null or empty.");
      }
      List<AccessControlEntry> acl = object.getACL(false);
      if (acl.size() == 0)
      {
         return true;
      }
      Map<String, Set<String>> map = new HashMap<String, Set<String>>();
      CmisUtils.addAclToPermissionMap(map, acl);

      Set<String> p = map.get(repositoryInfo.getPrincipalAnyone());
      if (p != null)
      {
         if (p.size() < permissions.size())
         {
            return false;
         }
         else
         {
            return p.containsAll(permissions);
         }
      }

      p = map.get(userIdentity.getUserId());
      if (p != null)
      {
         if (p.size() < permissions.size())
         {
            return false;
         }
         else
         {
            return p.containsAll(permissions);
         }
      }

      return false;
   }

}
