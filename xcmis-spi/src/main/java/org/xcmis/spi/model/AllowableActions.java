/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software(); you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation(); either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY(); without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software(); if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xcmis.spi.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class AllowableActions
{

   public static final String CAN_GET_DESCENDENTS = "canGetDescendents";

   public static final String CAN_GET_FOLDER_TREE = "canGetFolderTree";

   public static final String CAN_GET_CHILDREN = "canGetChildren";

   public static final String CAN_GET_OBJECT_PARENTS = "canGetObjectParents";

   public static final String CAN_GET_FOLDER_PARENT = "canGetFolderParent";

   public static final String CAN_CREATE_DOCUMENT = "canCreateDocument";

   public static final String CAN_CREATE_FOLDER = "canCreateFolder";

   public static final String CAN_CREATE_RELATIONSHIP = "canCreateRelationship";

   public static final String CAN_GET_PROPERTIES = "canGetProperties";

   public static final String CAN_GET_CONTENT_STREAM = "canGetContentStream";

   public static final String CAN_UPDATE_PROPERTIES = "canUpdateProperties";

   public static final String CAN_MOVE_OBJECT = "canMoveObject";

   public static final String CAN_DELETE = "canDelete";

   public static final String CAN_DELETE_TREE = "canDeleteTree";

   public static final String CAN_SET_CONTENT_STREAM = "canSetContentStream";

   public static final String CAN_DELETE_CONTENT_STREAM = "canDeleteContentStream";

   public static final String CAN_GET_RENDITIONS = "canRenditions";

   public static final String CAN_ADD_TO_FOLDER = "canAddToFolder";

   public static final String CAN_REMOVE_OBJECT_FROM_FOLDER = "canRemoveObjectFromFolder";

   public static final String CAN_CHECKOUT = "canCheckout";

   public static final String CAN_CANCEL_CHECKOUT = "canCancelCheckout";

   public static final String CAN_CHECKIN = "canCheckin";

   public static final String CAN_GET_ALL_VERSIONS = "canGetAllVersions";

   public static final String CAN_GET_OBJECT_RELATIONSHIPS = "canGetObjectRelationships";

   public static final String CAN_ADD_POLICY = "canAddPolicy";

   public static final String CAN_REMOVE_POLICY = "canRemovePolicy";

   public static final String CAN_GET_APPLIED_POLICIES = "canGetAppliedPolicies";

   public static final String CAN_GET_ACL = "canGetACL";

   public static final String CAN_APPLY_ACL = "canApplyACL";

   public static final Collection<String> DEFAULT =
      Collections.unmodifiableCollection(Arrays.asList(CAN_GET_DESCENDENTS, CAN_GET_FOLDER_TREE, CAN_GET_CHILDREN,
         CAN_GET_OBJECT_PARENTS, CAN_GET_FOLDER_PARENT, CAN_CREATE_DOCUMENT, CAN_CREATE_FOLDER,
         CAN_CREATE_RELATIONSHIP, CAN_GET_PROPERTIES, CAN_GET_CONTENT_STREAM, CAN_UPDATE_PROPERTIES, CAN_MOVE_OBJECT,
         CAN_DELETE, CAN_DELETE_TREE, CAN_SET_CONTENT_STREAM, CAN_DELETE_CONTENT_STREAM, CAN_GET_RENDITIONS,
         CAN_ADD_TO_FOLDER, CAN_REMOVE_OBJECT_FROM_FOLDER, CAN_CHECKOUT, CAN_CANCEL_CHECKOUT, CAN_CHECKIN,
         CAN_GET_ALL_VERSIONS, CAN_GET_OBJECT_RELATIONSHIPS, CAN_ADD_POLICY, CAN_REMOVE_POLICY,
         CAN_GET_APPLIED_POLICIES, CAN_GET_ACL, CAN_APPLY_ACL));

   private final Set<String> actions = new HashSet<String>();

   /**
    * Add <code>action</code> in allowed actions list.
    *
    * @param action action to be add in list
    */
   public void addAction(String action)
   {
      actions.add(action);
   }

   /**
    * Check is <code>action</code> is in allowed actions list.
    *
    * @param action action
    * @return <code>true</code> if <code>action</code> is allowed and
    *         <code>false</code> otherwise
    */
   public boolean isActionAllowed(String action)
   {
      return actions.contains(action);
   }

   public boolean isCanAddObjectToFolder()
   {
      return isActionAllowed(CAN_ADD_TO_FOLDER);
   }

   public boolean isCanApplyACL()
   {
      return isActionAllowed(CAN_APPLY_ACL);
   }

   public boolean isCanApplyPolicy()
   {
      return isActionAllowed(CAN_ADD_POLICY);
   }

   public boolean isCanCancelCheckOut()
   {
      return isActionAllowed(CAN_CANCEL_CHECKOUT);
   }

   public boolean isCanCheckIn()
   {
      return isActionAllowed(CAN_CHECKIN);
   }

   public boolean isCanCheckOut()
   {
      return isActionAllowed(CAN_CHECKOUT);
   }

   public boolean isCanCreateDocument()
   {
      return isActionAllowed(CAN_CREATE_DOCUMENT);
   }

   public boolean isCanCreateFolder()
   {
      return isActionAllowed(CAN_CREATE_FOLDER);
   }

   public boolean isCanCreateRelationship()
   {
      return isActionAllowed(CAN_CREATE_RELATIONSHIP);
   }

   public boolean isCanDeleteContentStream()
   {
      return isActionAllowed(CAN_DELETE_CONTENT_STREAM);
   }

   public boolean isCanDeleteObject()
   {
      return isActionAllowed(CAN_DELETE);
   }

   public boolean isCanDeleteTree()
   {
      return isActionAllowed(CAN_DELETE_TREE);
   }

   public boolean isCanGetACL()
   {
      return isActionAllowed(CAN_GET_ACL);
   }

   public boolean isCanGetAllVersions()
   {
      return isActionAllowed(CAN_GET_ALL_VERSIONS);
   }

   public boolean isCanGetAppliedPolicies()
   {
      return isActionAllowed(CAN_GET_APPLIED_POLICIES);
   }

   public boolean isCanGetChildren()
   {
      return isActionAllowed(CAN_GET_CHILDREN);
   }

   public boolean isCanGetContentStream()
   {
      return isActionAllowed(CAN_GET_CONTENT_STREAM);
   }

   public boolean isCanGetDescendants()
   {
      return isActionAllowed(CAN_GET_DESCENDENTS);
   }

   public boolean isCanGetFolderParent()
   {
      return isActionAllowed(CAN_GET_FOLDER_PARENT);
   }

   public boolean isCanGetFolderTree()
   {
      return isActionAllowed(CAN_GET_FOLDER_TREE);
   }

   public boolean isCanGetObjectParents()
   {
      return isActionAllowed(CAN_GET_OBJECT_PARENTS);
   }

   public boolean isCanGetObjectRelationships()
   {
      return isActionAllowed(CAN_GET_OBJECT_RELATIONSHIPS);
   }

   public boolean isCanGetProperties()
   {
      return isActionAllowed(CAN_GET_PROPERTIES);
   }

   public boolean isCanGetRenditions()
   {
      return isActionAllowed(CAN_GET_RENDITIONS);
   }

   public boolean isCanMoveObject()
   {
      return isActionAllowed(CAN_MOVE_OBJECT);
   }

   public boolean isCanRemoveObjectFromFolder()
   {
      return isActionAllowed(CAN_REMOVE_OBJECT_FROM_FOLDER);
   }

   public boolean isCanRemovePolicy()
   {
      return isActionAllowed(CAN_REMOVE_POLICY);
   }

   public boolean isCanSetContentStream()
   {
      return isActionAllowed(CAN_SET_CONTENT_STREAM);
   }

   public boolean isCanUpdateProperties()
   {
      return isActionAllowed(CAN_UPDATE_PROPERTIES);
   }

   /**
    * Remove <code>action</code> from allowed actions list.
    *
    * @param action action to be removed from the list
    */
   public void removeAction(String action)
   {
      actions.remove(action);
   }

   public void setCanAddObjectToFolder(boolean canAddObjectToFolder)
   {
      if (canAddObjectToFolder)
      {
         addAction(CAN_ADD_TO_FOLDER);
      }
      else
      {
         removeAction(CAN_ADD_TO_FOLDER);
      }
   }

   public void setCanApplyACL(boolean canApplyACL)
   {
      if (canApplyACL)
      {
         addAction(CAN_APPLY_ACL);
      }
      else
      {
         removeAction(CAN_APPLY_ACL);
      }
   }

   public void setCanApplyPolicy(boolean canApplyPolicy)
   {
      if (canApplyPolicy)
      {
         addAction(CAN_ADD_POLICY);
      }
      else
      {
         removeAction(CAN_ADD_POLICY);
      }
   }

   public void setCanCancelCheckOut(boolean canCancelCheckOut)
   {
      if (canCancelCheckOut)
      {
         addAction(CAN_CANCEL_CHECKOUT);
      }
      else
      {
         removeAction(CAN_CANCEL_CHECKOUT);
      }
   }

   public void setCanCheckIn(boolean canCheckIn)
   {
      if (canCheckIn)
      {
         addAction(CAN_CHECKIN);
      }
      else
      {
         removeAction(CAN_CHECKIN);
      }
   }

   public void setCanCheckOut(boolean canCheckOut)
   {
      if (canCheckOut)
      {
         addAction(CAN_CHECKOUT);
      }
      else
      {
         removeAction(CAN_CHECKOUT);
      }
   }

   public void setCanCreateDocument(boolean canCreateDocument)
   {
      if (canCreateDocument)
      {
         addAction(CAN_CREATE_DOCUMENT);
      }
      else
      {
         removeAction(CAN_CREATE_DOCUMENT);
      }

   }

   public void setCanCreateFolder(boolean canCreateFolder)
   {
      if (canCreateFolder)
      {
         addAction(CAN_CREATE_FOLDER);
      }
      else
      {
         removeAction(CAN_CREATE_FOLDER);
      }

   }

   public void setCanCreateRelationship(boolean canCreateRelationship)
   {
      if (canCreateRelationship)
      {
         addAction(CAN_CREATE_RELATIONSHIP);
      }
      else
      {
         removeAction(CAN_CREATE_RELATIONSHIP);
      }

   }

   public void setCanDeleteContentStream(boolean canDeleteContentStream)
   {
      if (canDeleteContentStream)
      {
         addAction(CAN_DELETE_CONTENT_STREAM);
      }
      else
      {
         removeAction(CAN_DELETE_CONTENT_STREAM);
      }
   }

   public void setCanDeleteObject(boolean canDeleteObject)
   {
      if (canDeleteObject)
      {
         addAction(CAN_DELETE);
      }
      else
      {
         removeAction(CAN_DELETE);
      }
   }

   public void setCanDeleteTree(boolean canDeleteTree)
   {
      if (canDeleteTree)
      {
         addAction(CAN_DELETE_TREE);
      }
      else
      {
         removeAction(CAN_DELETE_TREE);
      }
   }

   public void setCanGetACL(boolean canGetACL)
   {
      if (canGetACL)
      {
         addAction(CAN_GET_ACL);
      }
      else
      {
         removeAction(CAN_GET_ACL);
      }
   }

   public void setCanGetAllVersions(boolean canGetAllVersions)
   {
      if (canGetAllVersions)
      {
         addAction(CAN_GET_ALL_VERSIONS);
      }
      else
      {
         removeAction(CAN_GET_ALL_VERSIONS);
      }
   }

   public void setCanGetAppliedPolicies(boolean canGetAppliedPolicies)
   {
      if (canGetAppliedPolicies)
      {
         addAction(CAN_GET_APPLIED_POLICIES);
      }
      else
      {
         removeAction(CAN_GET_APPLIED_POLICIES);
      }
   }

   public void setCanGetChildren(boolean canGetChildren)
   {
      if (canGetChildren)
      {
         addAction(CAN_GET_CHILDREN);
      }
      else
      {
         removeAction(CAN_GET_CHILDREN);
      }
   }

   public void setCanGetContentStream(boolean canGetContentStream)
   {
      if (canGetContentStream)
      {
         addAction(CAN_GET_CONTENT_STREAM);
      }
      else
      {
         removeAction(CAN_GET_CONTENT_STREAM);
      }
   }

   public void setCanGetDescendants(boolean canGetDescendants)
   {
      if (canGetDescendants)
      {
         addAction(CAN_GET_DESCENDENTS);
      }
      else
      {
         removeAction(CAN_GET_DESCENDENTS);
      }
   }

   public void setCanGetFolderParent(boolean canGetFolderParent)
   {
      if (canGetFolderParent)
      {
         addAction(CAN_GET_FOLDER_PARENT);
      }
      else
      {
         removeAction(CAN_GET_FOLDER_PARENT);
      }
   }

   public void setCanGetFolderTree(boolean canGetFolderTree)
   {
      if (canGetFolderTree)
      {
         addAction(CAN_GET_FOLDER_TREE);
      }
      else
      {
         removeAction(CAN_GET_FOLDER_TREE);
      }
   }

   public void setCanGetObjectParents(boolean canGetObjectParents)
   {
      if (canGetObjectParents)
      {
         addAction(CAN_GET_OBJECT_PARENTS);
      }
      else
      {
         removeAction(CAN_GET_OBJECT_PARENTS);
      }
   }

   public void setCanGetObjectRelationships(boolean canGetObjectRelationships)
   {
      if (canGetObjectRelationships)
      {
         addAction(CAN_GET_OBJECT_RELATIONSHIPS);
      }
      else
      {
         removeAction(CAN_GET_OBJECT_RELATIONSHIPS);
      }
   }

   public void setCanGetProperties(boolean canGetProperties)
   {
      if (canGetProperties)
      {
         addAction(CAN_GET_PROPERTIES);
      }
      else
      {
         removeAction(CAN_GET_PROPERTIES);
      }
   }

   public void setCanGetRenditions(boolean canGetRenditions)
   {
      if (canGetRenditions)
      {
         addAction(CAN_GET_RENDITIONS);
      }
      else
      {
         removeAction(CAN_GET_RENDITIONS);
      }
   }

   public void setCanMoveObject(boolean canMoveObject)
   {
      if (canMoveObject)
      {
         addAction(CAN_MOVE_OBJECT);
      }
      else
      {
         removeAction(CAN_MOVE_OBJECT);
      }
   }

   public void setCanRemoveObjectFromFolder(boolean canRemoveObjectFromFolder)
   {
      if (canRemoveObjectFromFolder)
      {
         addAction(CAN_REMOVE_OBJECT_FROM_FOLDER);
      }
      else
      {
         removeAction(CAN_REMOVE_OBJECT_FROM_FOLDER);
      }
   }

   public void setCanRemovePolicy(boolean canRemovePolicy)
   {
      if (canRemovePolicy)
      {
         addAction(CAN_REMOVE_POLICY);
      }
      else
      {
         removeAction(CAN_REMOVE_POLICY);
      }
   }

   public void setCanSetContentStream(boolean canSetContentStream)
   {
      if (canSetContentStream)
      {
         addAction(CAN_SET_CONTENT_STREAM);
      }
      else
      {
         removeAction(CAN_SET_CONTENT_STREAM);
      }
   }

   public void setCanUpdateProperties(boolean canUpdateProperties)
   {
      if (canUpdateProperties)
      {
         addAction(CAN_UPDATE_PROPERTIES);
      }
      else
      {
         removeAction(CAN_UPDATE_PROPERTIES);
      }
   }

}
