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

package org.xcmis.spi.model.impl;

import org.xcmis.spi.model.AllowableActions;

/**
 * Simple plain implementation of {@link AllowableActions}.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class AllowableActionsImpl implements AllowableActions
{

   private boolean canAddObjectToFolder;

   private boolean canApplyACL;

   private boolean canApplyPolicy;

   private boolean canCancelCheckOut;

   private boolean canCheckIn;

   private boolean canCheckOut;

   private boolean canCreateDocument;

   private boolean canCreateFolder;

   private boolean canCreateRelationship;

   private boolean canDeleteContentStream;

   private boolean canDeleteObject;

   private boolean canDeleteTree;

   private boolean canGetACL;

   private boolean canGetAllVersions;

   private boolean canGetAppliedPolicies;

   private boolean canGetChildren;

   private boolean canGetContentStream;

   private boolean canGetDescendants;

   private boolean canGetFolderParent;

   private boolean canGetFolderTree;

   private boolean canGetObjectParents;

   private boolean canGetObjectRelationships;

   private boolean canGetProperties;

   private boolean canGetRenditions;

   private boolean canMoveObject;

   private boolean canRemoveObjectFromFolder;

   private boolean canRemovePolicy;

   private boolean canSetContentStream;

   private boolean canUpdateProperties;

   /**
    * {@inheritDoc}
    */
   public boolean isCanAddObjectToFolder()
   {
      return canAddObjectToFolder;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCanApplyACL()
   {
      return canApplyACL;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCanApplyPolicy()
   {
      return canApplyPolicy;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCanCancelCheckOut()
   {
      return canCancelCheckOut;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCanCheckIn()
   {
      return canCheckIn;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCanCheckOut()
   {
      return canCheckOut;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCanCreateDocument()
   {
      return canCreateDocument;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCanCreateFolder()
   {
      return canCreateFolder;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCanCreateRelationship()
   {
      return canCreateRelationship;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCanDeleteContentStream()
   {
      return canDeleteContentStream;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCanDeleteObject()
   {
      return canDeleteObject;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCanDeleteTree()
   {
      return canDeleteTree;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCanGetACL()
   {
      return canGetACL;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCanGetAllVersions()
   {
      return canGetAllVersions;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCanGetAppliedPolicies()
   {
      return canGetAppliedPolicies;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCanGetChildren()
   {
      return canGetChildren;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCanGetContentStream()
   {
      return canGetContentStream;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCanGetDescendants()
   {
      return canGetDescendants;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCanGetFolderParent()
   {
      return canGetFolderParent;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCanGetFolderTree()
   {
      return canGetFolderTree;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCanGetObjectParents()
   {
      return canGetObjectParents;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCanGetObjectRelationships()
   {
      return canGetObjectRelationships;
   }

   /**
    * {@inheritDoc}
    */

   public boolean isCanGetProperties()
   {
      return canGetProperties;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCanGetRenditions()
   {
      return canGetRenditions;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCanMoveObject()
   {
      return canMoveObject;
   }

   /**
    * {@inheritDoc}
    */

   public boolean isCanRemoveObjectFromFolder()
   {
      return canRemoveObjectFromFolder;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCanRemovePolicy()
   {
      return canRemovePolicy;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCanSetContentStream()
   {
      return canSetContentStream;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCanUpdateProperties()
   {
      return canUpdateProperties;
   }

   // ---- setters

   public void setCanAddObjectToFolder(boolean canAddObjectToFolder)
   {
      this.canAddObjectToFolder = canAddObjectToFolder;
   }

   public void setCanApplyACL(boolean canApplyACL)
   {
      this.canApplyACL = canApplyACL;
   }

   public void setCanApplyPolicy(boolean canApplyPolicy)
   {
      this.canApplyPolicy = canApplyPolicy;
   }

   public void setCanCancelCheckOut(boolean canCancelCheckOut)
   {
      this.canCancelCheckOut = canCancelCheckOut;
   }

   public void setCanCheckIn(boolean canCheckIn)
   {
      this.canCheckIn = canCheckIn;
   }

   public void setCanCheckOut(boolean canCheckOut)
   {
      this.canCheckOut = canCheckOut;
   }

   public void setCanCreateDocument(boolean canCreateDocument)
   {
      this.canCreateDocument = canCreateDocument;
   }

   public void setCanCreateFolder(boolean canCreateFolder)
   {
      this.canCreateFolder = canCreateFolder;
   }

   public void setCanCreateRelationship(boolean canCreateRelationship)
   {
      this.canCreateRelationship = canCreateRelationship;
   }

   public void setCanDeleteContentStream(boolean canDeleteContentStream)
   {
      this.canDeleteContentStream = canDeleteContentStream;
   }

   public void setCanDeleteObject(boolean canDeleteObject)
   {
      this.canDeleteObject = canDeleteObject;
   }

   public void setCanDeleteTree(boolean canDeleteTree)
   {
      this.canDeleteTree = canDeleteTree;
   }

   public void setCanGetACL(boolean canGetACL)
   {
      this.canGetACL = canGetACL;
   }

   public void setCanGetAllVersions(boolean canGetAllVersions)
   {
      this.canGetAllVersions = canGetAllVersions;
   }

   public void setCanGetAppliedPolicies(boolean canGetAppliedPolicies)
   {
      this.canGetAppliedPolicies = canGetAppliedPolicies;
   }

   public void setCanGetChildren(boolean canGetChildren)
   {
      this.canGetChildren = canGetChildren;
   }

   public void setCanGetContentStream(boolean canGetContentStream)
   {
      this.canGetContentStream = canGetContentStream;
   }

   public void setCanGetDescendants(boolean canGetDescendants)
   {
      this.canGetDescendants = canGetDescendants;
   }

   public void setCanGetFolderParent(boolean canGetFolderParent)
   {
      this.canGetFolderParent = canGetFolderParent;
   }

   public void setCanGetFolderTree(boolean canGetFolderTree)
   {
      this.canGetFolderTree = canGetFolderTree;
   }

   public void setCanGetObjectParents(boolean canGetObjectParents)
   {
      this.canGetObjectParents = canGetObjectParents;
   }

   public void setCanGetObjectRelationships(boolean canGetObjectRelationships)
   {
      this.canGetObjectRelationships = canGetObjectRelationships;
   }

   public void setCanGetProperties(boolean canGetProperties)
   {
      this.canGetProperties = canGetProperties;
   }

   public void setCanGetRenditions(boolean canGetRenditions)
   {
      this.canGetRenditions = canGetRenditions;
   }

   public void setCanMoveObject(boolean canMoveObject)
   {
      this.canMoveObject = canMoveObject;
   }

   public void setCanRemoveObjectFromFolder(boolean canRemoveObjectFromFolder)
   {
      this.canRemoveObjectFromFolder = canRemoveObjectFromFolder;
   }

   public void setCanRemovePolicy(boolean canRemovePolicy)
   {
      this.canRemovePolicy = canRemovePolicy;
   }

   public void setCanSetContentStream(boolean canSetContentStream)
   {
      this.canSetContentStream = canSetContentStream;
   }

   public void setCanUpdateProperties(boolean canUpdateProperties)
   {
      this.canUpdateProperties = canUpdateProperties;
   }

}
