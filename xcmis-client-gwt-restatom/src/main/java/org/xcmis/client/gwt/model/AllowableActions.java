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

package org.xcmis.client.gwt.model;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class AllowableActions 
{
   /**
    * Can add object to folder.
    */
   private boolean canAddObjectToFolder;

   /**
    * Can apply ACl.
    */
   private boolean canApplyACL;

   /**
    * Can apply policy.
    */
   private boolean canApplyPolicy;

   /**
    * Can cancel check out.
    */
   private boolean canCancelCheckOut;

   /**
    * Can check in.
    */
   private boolean canCheckIn;

   /**
    * Can check out.
    */
   private boolean canCheckOut;

   /**
    * Can create document.
    */
   private boolean canCreateDocument;

   /**
    * Can create folder.
    */
   private boolean canCreateFolder;

   /**
    * Can create relationship.
    */
   private boolean canCreateRelationship;

   /**
    * Can delete content stream.
    */
   private boolean canDeleteContentStream;

   /**
    * Can delete object.
    */
   private boolean canDeleteObject;

   /**
    * Can delete tree.
    */
   private boolean canDeleteTree;

   /**
    * Can get ACL.
    */
   private boolean canGetACL;

   /**
    * Can get all versions.
    */
   private boolean canGetAllVersions;

   /**
    * Can get applied policies.
    */
   private boolean canGetAppliedPolicies;

   /**
    * Can get children.
    */
   private boolean canGetChildren;

   /**
    * Can get content stream.
    */
   private boolean canGetContentStream;

   /**
    * can get descendants.
    */
   private boolean canGetDescendants;

   /**
    * can get folder parent.
    */
   private boolean canGetFolderParent;

   /**
    * Can get folder tree.
    */
   private boolean canGetFolderTree;

   /**
    * Can get object parents.
    */
   private boolean canGetObjectParents;

   /**
    * Can get object relationships.
    */
   private boolean canGetObjectRelationships;

   /**
    * Can get properties.
    */
   private boolean canGetProperties;

   /**
    * can get renditions.
    */
   private boolean canGetRenditions;

   /**
    * Can move object.
    */
   private boolean canMoveObject;

   /**
    * Can remove object from folder.
    */
   private boolean canRemoveObjectFromFolder;

   /**
    * Can remove policy.
    */
   private boolean canRemovePolicy;

   /**
    * Can set content stream.
    */
   private boolean canSetContentStream;

   /**
    * Can update properties.
    */
   private boolean canUpdateProperties;

   /**
    * @return boolean the canAddObjectToFolder
    */
   public boolean isCanAddObjectToFolder()
   {
      return canAddObjectToFolder;
   }

   /**
    * @param canAddObjectToFolder the canAddObjectToFolder to set
    */
   public void setCanAddObjectToFolder(boolean canAddObjectToFolder)
   {
      this.canAddObjectToFolder = canAddObjectToFolder;
   }

   /**
    * @return boolean the canApplyACL.
    */
   public boolean isCanApplyACL()
   {
      return canApplyACL;
   }

   /**
    * @param canApplyACL the canApplyACL to set
    */
   public void setCanApplyACL(boolean canApplyACL)
   {
      this.canApplyACL = canApplyACL;
   }

   /**
    * @return boolean the canApplyPolicy
    */
   public boolean isCanApplyPolicy()
   {
      return canApplyPolicy;
   }

   /**
    * @param canApplyPolicy the canApplyPolicy to set
    */
   public void setCanApplyPolicy(boolean canApplyPolicy)
   {
      this.canApplyPolicy = canApplyPolicy;
   }

   /**
    * @return boolean the canCancelCheckOut
    */
   public boolean isCanCancelCheckOut()
   {
      return canCancelCheckOut;
   }

   /**
    * @param canCancelCheckOut the canCancelCheckOut to set
    */
   public void setCanCancelCheckOut(boolean canCancelCheckOut)
   {
      this.canCancelCheckOut = canCancelCheckOut;
   }

   /**
    * @return boolean the canCheckIn
    */
   public boolean isCanCheckIn()
   {
      return canCheckIn;
   }

   /**
    * @param canCheckIn the canCheckIn to set
    */
   public void setCanCheckIn(boolean canCheckIn)
   {
      this.canCheckIn = canCheckIn;
   }

   /**
    * @return boolean the canCheckOut
    */
   public boolean isCanCheckOut()
   {
      return canCheckOut;
   }

   /**
    * @param canCheckOut the canCheckOut to set
    */
   public void setCanCheckOut(boolean canCheckOut)
   {
      this.canCheckOut = canCheckOut;
   }

   /**
    * @return boolean the canCreateDocument
    */
   public boolean isCanCreateDocument()
   {
      return canCreateDocument;
   }

   /**
    * @param canCreateDocument the canCreateDocument to set
    */
   public void setCanCreateDocument(boolean canCreateDocument)
   {
      this.canCreateDocument = canCreateDocument;
   }

   /**
    * @return boolean the canCreateFolder
    */
   public boolean isCanCreateFolder()
   {
      return canCreateFolder;
   }

   /**
    * @param canCreateFolder the canCreateFolder to set
    */
   public void setCanCreateFolder(boolean canCreateFolder)
   {
      this.canCreateFolder = canCreateFolder;
   }

   /**
    * @return boolean the canCreateRelationship
    */
   public boolean isCanCreateRelationship()
   {
      return canCreateRelationship;
   }

   /**
    * @param canCreateRelationship the canCreateRelationship to set
    */
   public void setCanCreateRelationship(boolean canCreateRelationship)
   {
      this.canCreateRelationship = canCreateRelationship;
   }

   /**
    * @return boolean the canDeleteContentStream
    */
   public boolean isCanDeleteContentStream()
   {
      return canDeleteContentStream;
   }

   /**
    * @param canDeleteContentStream the canDeleteContentStream to set
    */
   public void setCanDeleteContentStream(boolean canDeleteContentStream)
   {
      this.canDeleteContentStream = canDeleteContentStream;
   }

   /**
    * @return boolean the canDeleteObject
    */
   public boolean isCanDeleteObject()
   {
      return canDeleteObject;
   }

   /**
    * @param canDeleteObject the canDeleteObject to set
    */
   public void setCanDeleteObject(boolean canDeleteObject)
   {
      this.canDeleteObject = canDeleteObject;
   }

   /**
    * @return boolean the canDeleteTree
    */
   public boolean isCanDeleteTree()
   {
      return canDeleteTree;
   }

   /**
    * @param canDeleteTree the canDeleteTree to set
    */
   public void setCanDeleteTree(boolean canDeleteTree)
   {
      this.canDeleteTree = canDeleteTree;
   }

   /**
    * @return boolean the canGetACL
    */
   public boolean isCanGetACL()
   {
      return canGetACL;
   }

   /**
    * @param canGetACL the canGetACL to set
    */
   public void setCanGetACL(boolean canGetACL)
   {
      this.canGetACL = canGetACL;
   }

   /**
    * @return boolean the canGetAllVersions
    */
   public boolean isCanGetAllVersions()
   {
      return canGetAllVersions;
   }

   /**
    * @param canGetAllVersions the canGetAllVersions to set
    */
   public void setCanGetAllVersions(boolean canGetAllVersions)
   {
      this.canGetAllVersions = canGetAllVersions;
   }

   /**
    * @return boolean the canGetAppliedPolicies
    */
   public boolean isCanGetAppliedPolicies()
   {
      return canGetAppliedPolicies;
   }

   /**
    * @param canGetAppliedPolicies the canGetAppliedPolicies to set
    */
   public void setCanGetAppliedPolicies(boolean canGetAppliedPolicies)
   {
      this.canGetAppliedPolicies = canGetAppliedPolicies;
   }

   /**
    * @return boolean the canGetChildren
    */
   public boolean isCanGetChildren()
   {
      return canGetChildren;
   }

   /**
    * @param canGetChildren the canGetChildren to set
    */
   public void setCanGetChildren(boolean canGetChildren)
   {
      this.canGetChildren = canGetChildren;
   }

   /**
    * @return boolean the canGetContentStream
    */
   public boolean isCanGetContentStream()
   {
      return canGetContentStream;
   }

   /**
    * @param canGetContentStream the canGetContentStream to set
    */
   public void setCanGetContentStream(boolean canGetContentStream)
   {
      this.canGetContentStream = canGetContentStream;
   }

   /**
    * @return boolean the canGetDescendants
    */
   public boolean isCanGetDescendants()
   {
      return canGetDescendants;
   }

   /**
    * @param canGetDescendants the canGetDescendants to set
    */
   public void setCanGetDescendants(boolean canGetDescendants)
   {
      this.canGetDescendants = canGetDescendants;
   }

   /**
    * @return boolean the canGetFolderParent
    */
   public boolean isCanGetFolderParent()
   {
      return canGetFolderParent;
   }

   /**
    * @param canGetFolderParent the canGetFolderParent to set
    */
   public void setCanGetFolderParent(boolean canGetFolderParent)
   {
      this.canGetFolderParent = canGetFolderParent;
   }

   /**
    * @return boolean the canGetFolderTree
    */
   public boolean isCanGetFolderTree()
   {
      return canGetFolderTree;
   }

   /**
    * @param canGetFolderTree the canGetFolderTree to set
    */
   public void setCanGetFolderTree(boolean canGetFolderTree)
   {
      this.canGetFolderTree = canGetFolderTree;
   }

   /**
    * @return boolean the canGetObjectParents
    */
   public boolean isCanGetObjectParents()
   {
      return canGetObjectParents;
   }

   /**
    * @param canGetObjectParents the canGetObjectParents to set
    */
   public void setCanGetObjectParents(boolean canGetObjectParents)
   {
      this.canGetObjectParents = canGetObjectParents;
   }

   /**
    * @return boolean the canGetObjectRelationships
    */
   public boolean isCanGetObjectRelationships()
   {
      return canGetObjectRelationships;
   }

   /**
    * @param canGetObjectRelationships the canGetObjectRelationships to set
    */
   public void setCanGetObjectRelationships(boolean canGetObjectRelationships)
   {
      this.canGetObjectRelationships = canGetObjectRelationships;
   }

   /**
    * @return boolean the canGetProperties
    */
   public boolean isCanGetProperties()
   {
      return canGetProperties;
   }

   /**
    * @param canGetProperties the canGetProperties to set
    */
   public void setCanGetProperties(boolean canGetProperties)
   {
      this.canGetProperties = canGetProperties;
   }

   /**
    * @return boolean the canGetRenditions
    */
   public boolean isCanGetRenditions()
   {
      return canGetRenditions;
   }

   /**
    * @param canGetRenditions the canGetRenditions to set
    */
   public void setCanGetRenditions(boolean canGetRenditions)
   {
      this.canGetRenditions = canGetRenditions;
   }

   /**
    * @return boolean the canMoveObject
    */
   public boolean isCanMoveObject()
   {
      return canMoveObject;
   }

   /**
    * @param canMoveObject the canMoveObject to set
    */
   public void setCanMoveObject(boolean canMoveObject)
   {
      this.canMoveObject = canMoveObject;
   }

   /**
    * @return boolean the canRemoveObjectFromFolder
    */
   public boolean isCanRemoveObjectFromFolder()
   {
      return canRemoveObjectFromFolder;
   }

   /**
    * @param canRemoveObjectFromFolder the canRemoveObjectFromFolder to set
    */
   public void setCanRemoveObjectFromFolder(boolean canRemoveObjectFromFolder)
   {
      this.canRemoveObjectFromFolder = canRemoveObjectFromFolder;
   }

   /**
    * @return boolean the canRemovePolicy
    */
   public boolean isCanRemovePolicy()
   {
      return canRemovePolicy;
   }

   /**
    * @param canRemovePolicy the canRemovePolicy to set
    */
   public void setCanRemovePolicy(boolean canRemovePolicy)
   {
      this.canRemovePolicy = canRemovePolicy;
   }

   /**
    * @return boolean the canSetContentStream
    */
   public boolean isCanSetContentStream()
   {
      return canSetContentStream;
   }

   /**
    * @param canSetContentStream the canSetContentStream to set
    */
   public void setCanSetContentStream(boolean canSetContentStream)
   {
      this.canSetContentStream = canSetContentStream;
   }

   /**
    * @return boolean the canUpdateProperties
    */
   public boolean isCanUpdateProperties()
   {
      return canUpdateProperties;
   }

   /**
    * @param canUpdateProperties the canUpdateProperties to set
    */
   public void setCanUpdateProperties(boolean canUpdateProperties)
   {
      this.canUpdateProperties = canUpdateProperties;
   }
}
