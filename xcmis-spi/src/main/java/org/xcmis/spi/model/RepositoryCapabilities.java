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

package org.xcmis.spi.model;

/**
 * Capabilities of CMIS repository.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class RepositoryCapabilities
{

   private CapabilityACL capabilityACL;

   private CapabilityChanges capabilityChanges;

   private CapabilityContentStreamUpdatable capabilityContentStreamUpdatable;

   private CapabilityJoin capabilityJoin;

   private CapabilityQuery capabilityQuery;

   private CapabilityRendition capabilityRenditions;

   private boolean capabilityAllVersionsSearchable;

   private boolean capabilityGetDescendants;

   private boolean capabilityGetFolderTree;

   private boolean capabilityMultifiling;

   private boolean capabilityPWCSearchable;

   private boolean capabilityPWCUpdatable;

   private boolean capabilityUnfiling;

   private boolean capabilityVersionSpecificFiling;

   public CapabilityACL getCapabilityACL()
   {
      return capabilityACL;
   }

   public RepositoryCapabilities(CapabilityACL capabilityACL, CapabilityChanges capabilityChanges,
      CapabilityContentStreamUpdatable capabilityContentStreamUpdatable, CapabilityJoin capabilityJoin,
      CapabilityQuery capabilityQuery, CapabilityRendition capabilityRenditions,
      boolean capabilityAllVersionsSearchable, boolean capabilityGetDescendants, boolean capabilityGetFolderTree,
      boolean capabilityMultifiling, boolean capabilityPWCSearchable, boolean capabilityPWCUpdatable,
      boolean capabilityUnfiling, boolean capabilityVersionSpecificFiling)
   {
      this.capabilityACL = capabilityACL;
      this.capabilityChanges = capabilityChanges;
      this.capabilityContentStreamUpdatable = capabilityContentStreamUpdatable;
      this.capabilityJoin = capabilityJoin;
      this.capabilityQuery = capabilityQuery;
      this.capabilityRenditions = capabilityRenditions;
      this.capabilityAllVersionsSearchable = capabilityAllVersionsSearchable;
      this.capabilityGetDescendants = capabilityGetDescendants;
      this.capabilityGetFolderTree = capabilityGetFolderTree;
      this.capabilityMultifiling = capabilityMultifiling;
      this.capabilityPWCSearchable = capabilityPWCSearchable;
      this.capabilityPWCUpdatable = capabilityPWCUpdatable;
      this.capabilityUnfiling = capabilityUnfiling;
      this.capabilityVersionSpecificFiling = capabilityVersionSpecificFiling;
   }

   public RepositoryCapabilities()
   {
   }

   public CapabilityChanges getCapabilityChanges()
   {
      return capabilityChanges;
   }

   public CapabilityContentStreamUpdatable getCapabilityContentStreamUpdatable()
   {
      return capabilityContentStreamUpdatable;
   }

   public CapabilityJoin getCapabilityJoin()
   {
      return capabilityJoin;
   }

   public CapabilityQuery getCapabilityQuery()
   {
      return capabilityQuery;
   }

   public CapabilityRendition getCapabilityRenditions()
   {
      return capabilityRenditions;
   }

   public boolean isCapabilityAllVersionsSearchable()
   {
      return capabilityAllVersionsSearchable;
   }

   public boolean isCapabilityGetDescendants()
   {
      return capabilityGetDescendants;
   }

   public boolean isCapabilityGetFolderTree()
   {
      return capabilityGetFolderTree;
   }

   public boolean isCapabilityMultifiling()
   {
      return capabilityMultifiling;
   }

   public boolean isCapabilityPWCSearchable()
   {
      return capabilityPWCSearchable;
   }

   public boolean isCapabilityPWCUpdatable()
   {
      return capabilityPWCUpdatable;
   }

   public boolean isCapabilityUnfiling()
   {
      return capabilityUnfiling;
   }

   public boolean isCapabilityVersionSpecificFiling()
   {
      return capabilityVersionSpecificFiling;
   }

   // ------------------- setters --------------------

   public void setCapabilityACL(CapabilityACL capabilityACL)
   {
      this.capabilityACL = capabilityACL;
   }

   public void setCapabilityAllVersionsSearchable(boolean capabilityAllVersionsSearchable)
   {
      this.capabilityAllVersionsSearchable = capabilityAllVersionsSearchable;
   }

   public void setCapabilityChanges(CapabilityChanges capabilityChanges)
   {
      this.capabilityChanges = capabilityChanges;
   }

   public void setCapabilityContentStreamUpdatable(CapabilityContentStreamUpdatable capabilityContentStreamUpdatable)
   {
      this.capabilityContentStreamUpdatable = capabilityContentStreamUpdatable;
   }

   public void setCapabilityGetDescendants(boolean capabilityGetDescendants)
   {
      this.capabilityGetDescendants = capabilityGetDescendants;
   }

   public void setCapabilityGetFolderTree(boolean capabilityGetFolderTree)
   {
      this.capabilityGetFolderTree = capabilityGetFolderTree;
   }

   public void setCapabilityJoin(CapabilityJoin capabilityJoin)
   {
      this.capabilityJoin = capabilityJoin;
   }

   public void setCapabilityMultifiling(boolean capabilityMultifiling)
   {
      this.capabilityMultifiling = capabilityMultifiling;
   }

   public void setCapabilityPWCSearchable(boolean capabilityPWCSearchable)
   {
      this.capabilityPWCSearchable = capabilityPWCSearchable;
   }

   public void setCapabilityPWCUpdatable(boolean capabilityPWCUpdatable)
   {
      this.capabilityPWCUpdatable = capabilityPWCUpdatable;
   }

   public void setCapabilityQuery(CapabilityQuery capabilityQuery)
   {
      this.capabilityQuery = capabilityQuery;
   }

   public void setCapabilityRenditions(CapabilityRendition capabilityRenditions)
   {
      this.capabilityRenditions = capabilityRenditions;
   }

   public void setCapabilityUnfiling(boolean capabilityUnfiling)
   {
      this.capabilityUnfiling = capabilityUnfiling;
   }

   public void setCapabilityVersionSpecificFiling(boolean capabilityVersionSpecificFiling)
   {
      this.capabilityVersionSpecificFiling = capabilityVersionSpecificFiling;
   }

}
