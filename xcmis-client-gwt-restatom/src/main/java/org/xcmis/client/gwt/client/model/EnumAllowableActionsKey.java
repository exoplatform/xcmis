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
public enum EnumAllowableActionsKey 
{

   /**
    * Can get descendents Folder.
    */
   CAN_GET_DESCENDENTS_FOLDER("canGetDescendents.Folder"), 
   
   /**
    * Can get children Folder.
    */
   CAN_GET_CHILDREN_FOLDER("canGetChildren.Folder"), 
   
   /**
    * Can get parents Folder.
    */
   CAN_GET_PARENTS_FOLDER("canGetParents.Folder"), 
   
   /**
    * Can get folder parent Object.
    */
   CAN_GET_FOLDER_PARENT_OBJECT("canGetFolderParent.Object"),
   
   /**
    * Can create document Type.
    */
   CAN_CREATE_DOCUMENT_TYPE("canCreateDocument.Type"), 
   
   /**
    * Can create document Folder. 
    */
   CAN_CREATE_DOCUMENT_FOLDER("canCreateDocument.Folder"), 
   
   /**
    * Can create folder Type.
    */
   CAN_CREATE_FOLDER_TYPE("canCreateFolder.Type"), 
   
   /**
    * Can create folder Folder.
    */
   CAN_CREATE_FOLDER_FOLDER("canCreateFolder.Folder"), 
   
   /**
    * Can create relationship Type.
    */
   CAN_CREATE_RELATIONSHIP_TYPE("canCreateRelationship.Type"),
   
   /**
    * Can create relationship Source.
    */
   CAN_CREATE_RELATIONSHIP_SOURCE("canCreateRelationship.Source"), 
   
   /**
    * Can create relationship Target.
    */
   CAN_CREATE_RELATIONSHIP_TARGET("canCreateRelationship.Target"), 
   
   /**
    * Can create policy Type.
    */
   CAN_CREATE_POLICY_TYPE("canCreatePolicy.Type"), 
   
   /**
    * Can get properties Object.
    */
   CAN_GET_PROPERTIES_OBJECT("canGetProperties.Object"), 
   
   /**
    * Can view content Object.
    */
   CAN_VIEW_CONTENT_OBJECT("canViewContent.Object"), 
   
   /**
    * Can update properties Object.
    */
   CAN_UPDATE_PROPERTIES_OBJECT("canUpdateProperties.Object"), 
   
   /**
    * Can move Object.
    */
   CAN_MOVE_OBJECT("canMove.Object"), 
   
   /**
    * Can move Target.
    */
   CAN_MOVE_TARGET("canMove.Target"), 
   
   /**
    * Can move Source.
    */
   CAN_MOVE_SOURCE("canMove.Source"), 
   
   /**
    * Can delete Object.
    */
   CAN_DELETE_OBJECT("canDelete.Object"), 
   
   /**
    * Can delete tree Folder.
    */
   CAN_DELETE_TREE_FOLDER("canDeleteTree.Folder"), 
   
   /**
    * Can set content Document.
    */
   CAN_SET_CONTENT_DOCUMENT("canSetContent.Document"), 
   
   /**
    * Can delete content Document.
    */
   CAN_DELETE_CONTENT_DOCUMENT("canDeleteContent.Document"), 
   
   /**
    * Can add to folder Object.
    */
   CAN_ADD_TO_FOLDER_OBJECT("canAddToFolder.Object"), 
   
   /**
    * Can add to folder Folder.
    */
   CAN_ADD_TO_FOLDER_FOLDER("canAddToFolder.Folder"), 
   
   /**
    * Can remove from folder Object.
    */
   CAN_REMOVE_FROM_FOLDER_OBJECT("canRemoveFromFolder.Object"), 
   
   /**
    * Can remove from folder Folder.
    */
   CAN_REMOVE_FROM_FOLDER_FOLDER("canRemoveFromFolder.Folder"), 
   
   /**
    * Can checkout Document.
    */
   CAN_CHECKOUT_DOCUMENT("canCheckout.Document"), 
   
   /**
    * Can cancel checkout Document.
    */
   CAN_CANCEL_CHECKOUT_DOCUMENT("canCancelCheckout.Document"), 
   
   /**
    * Can checkin Document.
    */
   CAN_CHECKIN_DOCUMENT("canCheckin.Document"), 
   
   /**
    * Can cet all versions VersionSeries.
    */
   CAN_GET_ALL_VERSIONS_VERSION_SERIES("canGetAllVersions.VersionSeries"), 
   
   /**
    * Can get object relationships Object.
    */
   CAN_GET_OBJECT_RELATIONSHIPS_OBJECT("canGetObjectRelationships.Object"), 
   
   /**
    * Can add policy Object.
    */
   CAN_ADD_POLICY_OBJECT("canAddPolicy.Object"), 
   
   /**
    * Can add policy Policy.
    */
   CAN_ADD_POLICY_POLICY("canAddPolicy.Policy"), 
   
   /**
    * Can remove policy Object.
    */
   CAN_REMOVE_POLICY_OBJECT("canRemovePolicy.Object"), 
   
   /**
    * Can remove policy Policy.
    */
   CAN_REMOVE_POLICY_POLICY("canRemovePolicy.Policy"), 
   
   /**
    * Can get applied policies Object.
    */
   CAN_GET_APPLIED_POLICIES_OBJECT("canGetAppliedPolicies.Object"), 
   
   /**
    * Can get ACL Object.
    */
   CAN_GET_ACL_OBJECT("canGetACL.Object"), 
   
   /**
    * Can apply ACL Object.
    */
   CAN_APPLY_ACL_OBJECT("canApplyACL.Object");
   
   
   /**
    * Value.
    */
   private final String value;

   /**
    * @param v value
    */
   EnumAllowableActionsKey(String v)
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
    * @return {@link EnumAllowableActionsKey}
    */
   public static EnumAllowableActionsKey fromValue(String v)
   {
      for (EnumAllowableActionsKey c : EnumAllowableActionsKey.values())
      {
         if (c.value.equals(v))
         {
            return c;
         }
      }
      throw new IllegalArgumentException(v);
   }

}
