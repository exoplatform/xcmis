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

import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.AllowableActions;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.CapabilityACL;
import org.xcmis.spi.model.PermissionMapping;
import org.xcmis.spi.model.RepositoryCapabilities;
import org.xcmis.spi.model.RepositoryInfo;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.Permission.BasicPermissions;
import org.xcmis.spi.utils.CmisUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Common service for resolve CMIS object permissions.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class PermissionService
{

   /**
    * Calculate allowable actions for specified object.
    *
    * @param object object
    * @param userIdentity user's identity
    * @param repositoryInfo RepositoryInfo
    * @return allowable actions for object
    * @see Identity
    */
   public AllowableActions calculateAllowableActions(ObjectData object, String userId, RepositoryInfo repositoryInfo)
   {

      if (repositoryInfo.getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         return AllowableActions.ALL();
      }

      if (userId == null)
      {
         userId = repositoryInfo.getPrincipalAnonymous();
      }

      PermissionMapping permissionMapping = repositoryInfo.getAclCapability().getMapping();

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
                  userId, repositoryInfo))
            {
               actions.setCanGetDescendants(true);
            }
         }
         else if (AllowableActions.CAN_GET_FOLDER_TREE.equals(action))
         {
            if (capabilities.isCapabilityGetFolderTree()
               && BaseType.FOLDER == type.getBaseId()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_GET_FOLDER_TREE_FOLDER),
                  userId, repositoryInfo))
            {
               actions.setCanGetFolderTree(true);
            }
         }
         else if (AllowableActions.CAN_GET_CHILDREN.equals(action))
         {
            if (BaseType.FOLDER == type.getBaseId()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_GET_CHILDREN_FOLDER),
                  userId, repositoryInfo))
            {
               actions.setCanGetChildren(true);
            }
         }
         else if (AllowableActions.CAN_GET_OBJECT_PARENTS.equals(action))
         {
            if (type.isFileable()
               && hasPermission(object, permissionMapping
                  .getPermissions(PermissionMapping.CAN_GET_OBJECT_PARENTS_OBJECT), userId, repositoryInfo))
            {
               actions.setCanGetObjectParents(true);
            }
         }
         else if (AllowableActions.CAN_GET_FOLDER_PARENT.equals(action))
         {
            if (BaseType.FOLDER == type.getBaseId()
               && hasPermission(object, permissionMapping
                  .getPermissions(PermissionMapping.CAN_GET_FOLDER_PARENT_FOLDER), userId, repositoryInfo))
            {
               actions.setCanGetFolderParent(true);
            }
         }
         else if (AllowableActions.CAN_CREATE_DOCUMENT.equals(action))
         {
            if (BaseType.FOLDER == type.getBaseId()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_CREATE_DOCUMENT_FOLDER),
                  userId, repositoryInfo))
            {
               actions.setCanCreateDocument(true);
            }
         }
         else if (AllowableActions.CAN_CREATE_FOLDER.equals(action))
         {
            if (BaseType.FOLDER == type.getBaseId()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_CREATE_FOLDER_FOLDER),
                  userId, repositoryInfo))
            {
               actions.setCanCreateFolder(true);
            }
         }
         else if (AllowableActions.CAN_CREATE_RELATIONSHIP.equals(action))
         {
            if (BaseType.RELATIONSHIP != type.getBaseId()
               && hasPermission(object, permissionMapping
                  .getPermissions(PermissionMapping.CAN_CREATE_RELATIONSHIP_SOURCE), userId, repositoryInfo)
               && hasPermission(object, permissionMapping
                  .getPermissions(PermissionMapping.CAN_CREATE_RELATIONSHIP_TARGET), userId, repositoryInfo))
            {
               actions.setCanCreateRelationship(true);
            }
         }
         else if (AllowableActions.CAN_GET_PROPERTIES.equals(action))
         {
            if (hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_GET_PROPERTIES_OBJECT),
               userId, repositoryInfo))
            {
               actions.setCanGetProperties(true);
            }
         }
         else if (AllowableActions.CAN_GET_CONTENT_STREAM.equals(action))
         {
            if (BaseType.DOCUMENT == type.getBaseId()
               && hasPermission(object, permissionMapping
                  .getPermissions(PermissionMapping.CAN_GET_CONTENT_STREAM_OBJECT), userId, repositoryInfo))
            {
               actions.setCanGetContentStream(true);
            }
         }
         else if (AllowableActions.CAN_UPDATE_PROPERTIES.equals(action))
         {
            if (hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_UPDATE_PROPERTIES_OBJECT),
               userId, repositoryInfo))
            {
               actions.setCanUpdateProperties(true);
            }
         }
         else if (AllowableActions.CAN_MOVE_OBJECT.equals(action))
         {
            if (type.isFileable()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_MOVE_OBJECT_OBJECT),
                  userId, repositoryInfo))
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
                     userId, repositoryInfo))
               {
                  actions.setCanDeleteObject(true);
               }
            }
            else if (hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_DELETE_OBJECT),
               userId, repositoryInfo))
            {
               actions.setCanDeleteObject(true);
            }
         }
         else if (AllowableActions.CAN_DELETE_TREE.equals(action))
         {
            if (BaseType.FOLDER == type.getBaseId()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_DELETE_TREE_FOLDER),
                  userId, repositoryInfo))
            {
               actions.setCanDeleteTree(true);
            }
         }
         else if (AllowableActions.CAN_SET_CONTENT_STREAM.equals(action))
         {
            if (BaseType.DOCUMENT == type.getBaseId()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_SET_CONTENT_DOCUMENT),
                  userId, repositoryInfo))
            {
               actions.setCanSetContentStream(true);
            }
         }
         else if (AllowableActions.CAN_DELETE_CONTENT_STREAM.equals(action))
         {
            if (BaseType.DOCUMENT == type.getBaseId()
               && hasPermission(object,
                  permissionMapping.getPermissions(PermissionMapping.CAN_DELETE_CONTENT_DOCUMENT), userId,
                  repositoryInfo))
            {
               actions.setCanDeleteContentStream(true);
            }
         }
         else if (AllowableActions.CAN_GET_RENDITIONS.equals(action))
         {
            if (BaseType.DOCUMENT == type.getBaseId()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_GET_RENDITIONS_OBJECT),
                  userId, repositoryInfo))
            {
               actions.setCanGetRenditions(true);
            }
         }
         else if (AllowableActions.CAN_ADD_TO_FOLDER.equals(action))
         {
            if (type.isFileable()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_ADD_TO_FOLDER_OBJECT),
                  userId, repositoryInfo))
            {
               actions.setCanAddObjectToFolder(true);
            }
         }
         else if (AllowableActions.CAN_REMOVE_OBJECT_FROM_FOLDER.equals(action))
         {
            if (type.isFileable() //
               && type.getBaseId() != BaseType.FOLDER //
               && hasPermission(object, permissionMapping
                  .getPermissions(PermissionMapping.CAN_REMOVE_OBJECT_FROM_FOLDER_OBJECT), userId, repositoryInfo) //
               && (repositoryInfo.getCapabilities().isCapabilityUnfiling() || object.getParents().size() > 1))
            {
               actions.setCanRemoveObjectFromFolder(true);
            }
         }
         else if (AllowableActions.CAN_CHECKOUT.equals(action))
         {
            if (type.isVersionable()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_CHECKOUT_DOCUMENT),
                  userId, repositoryInfo))
            {
               actions.setCanCheckOut(true);
            }
         }
         else if (AllowableActions.CAN_CANCEL_CHECKOUT.equals(action))
         {
            if (type.isVersionable()
               && ((DocumentData)object).isVersionSeriesCheckedOut()
               && hasPermission(object, permissionMapping
                  .getPermissions(PermissionMapping.CAN_CANCEL_CHECKOUT_DOCUMENT), userId, repositoryInfo))
            {
               actions.setCanCancelCheckOut(true);
            }
         }
         else if (AllowableActions.CAN_CHECKIN.equals(action))
         {
            if (type.isVersionable()
               && ((DocumentData)object).isPWC()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_CHECKIN_DOCUMENT),
                  userId, repositoryInfo))
            {
               actions.setCanCancelCheckOut(true);
            }
         }
         else if (AllowableActions.CAN_GET_ALL_VERSIONS.equals(action))
         {
            if (BaseType.DOCUMENT == type.getBaseId()
               && hasPermission(object, permissionMapping
                  .getPermissions(PermissionMapping.CAN_GET_ALL_VERSIONS_DOCUMENT), userId, repositoryInfo))
            {
               actions.setCanGetAllVersions(true);
            }
         }
         else if (AllowableActions.CAN_GET_OBJECT_RELATIONSHIPS.equals(action))
         {
            if (BaseType.RELATIONSHIP != type.getBaseId()
               && hasPermission(object, permissionMapping
                  .getPermissions(PermissionMapping.CAN_GET_OBJECT_RELATIONSHIPS_OBJECT), userId, repositoryInfo))
            {
               actions.setCanGetObjectRelationships(true);
            }
         }
         else if (AllowableActions.CAN_ADD_POLICY.equals(action))
         {
            if (type.isControllablePolicy()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_ADD_POLICY_OBJECT),
                  userId, repositoryInfo))
            {
               actions.setCanApplyPolicy(true);
            }
         }
         else if (AllowableActions.CAN_REMOVE_POLICY.equals(action))
         {
            if (type.isControllablePolicy()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_REMOVE_POLICY_OBJECT),
                  userId, repositoryInfo))
            {
               actions.setCanRemovePolicy(true);
            }
         }
         else if (AllowableActions.CAN_GET_APPLIED_POLICIES.equals(action))
         {
            if (type.isControllablePolicy()
               && hasPermission(object, permissionMapping
                  .getPermissions(PermissionMapping.CAN_GET_APPLIED_POLICIES_OBJECT), userId, repositoryInfo))
            {
               actions.setCanGetAppliedPolicies(true);
            }
         }
         else if (AllowableActions.CAN_GET_ACL.equals(action))
         {
            if (type.isControllableACL()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_GET_ACL_OBJECT), userId,
                  repositoryInfo))
            {
               actions.setCanGetACL(true);
            }
         }
         else if (AllowableActions.CAN_APPLY_ACL.equals(action))
         {
            if (type.isControllableACL()
               && hasPermission(object, permissionMapping.getPermissions(PermissionMapping.CAN_APPLY_ACL_OBJECT),
                  userId, repositoryInfo))
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
    * @param userId user's id
    * @param repositoryInfo
    * @return <code>true</code> if user has all <code>permissions</code> and
    *         <code>false</code> otherwise
    */
   public boolean hasPermission(ObjectData object, Collection<String> permissions, String userId,
      RepositoryInfo repositoryInfo)
   {
      if (permissions == null || permissions.size() == 0)
      {
         throw new CmisRuntimeException("Permissions set may not be null or empty.");
      }
      if (userId == null)
      {
         userId = repositoryInfo.getPrincipalAnonymous();
      }
      List<AccessControlEntry> acl = object.getACL(false);
      if (acl.size() == 0)
      {
         return true;
      }
      Map<String, Set<String>> map = new HashMap<String, Set<String>>();
      CmisUtils.addAclToPermissionMap(map, acl);

      // Check for 'any principal' first the for current principal.
      for (String principal : new String[]{repositoryInfo.getPrincipalAnyone(), userId})
      {
         Set<String> p = map.get(principal);
         if (p != null)
         {
            if (p.contains(BasicPermissions.CMIS_ALL.value()))
            {
               // All operations allowed.
               return true;
            }
            return p.containsAll(permissions);
         }
      }
      return false;
   }
}
