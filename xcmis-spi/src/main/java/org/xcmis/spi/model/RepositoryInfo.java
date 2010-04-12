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

import java.util.Collection;
import java.util.Collections;

/**
 * Information about CMIS repository and its capabilities.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class RepositoryInfo
{

   private String repositoryId;

   private String repositoryName;

   private String rootFolderId;

   private String cmisVersionSupported;

   private RepositoryCapabilities capabilities;

   private ACLCapability aclCapability;

   private String principalAnonymous;

   private String principalAnyone;

   private Collection<BaseType> changesOnType;

   private String latestChangeLogToken;

   private boolean changesIncomplete;

   private String repositoryDescription;

   private String vendorName;

   private String productName;

   private String productVersion;

   private String thinClientURI;

   public RepositoryInfo(String repositoryId, String repositoryName, String rootFolderId, String cmisVersionSupported,
      RepositoryCapabilities capabilities, ACLCapability aclCapability, String principalAnonymous,
      String principalAnyone, Collection<BaseType> changesOnType, String latestChangeLogToken,
      boolean changesIncomplete, String repositoryDescription, String vendorName, String productName,
      String productVersion, String thinClientURI)
   {
      this.repositoryId = repositoryId;
      this.repositoryName = repositoryName;
      this.rootFolderId = rootFolderId;
      this.cmisVersionSupported = cmisVersionSupported;
      this.capabilities = capabilities;
      this.aclCapability = aclCapability;
      this.principalAnonymous = principalAnonymous;
      this.principalAnyone = principalAnyone;
      this.changesOnType = changesOnType;
      this.latestChangeLogToken = latestChangeLogToken;
      this.changesIncomplete = changesIncomplete;
      this.repositoryDescription = repositoryDescription;
      this.vendorName = vendorName;
      this.productName = productName;
      this.productVersion = productVersion;
      this.thinClientURI = thinClientURI;
   }

   public RepositoryInfo()
   {
   }

   public ACLCapability getAclCapability()
   {
      return aclCapability;
   }

   public RepositoryCapabilities getCapabilities()
   {
      return capabilities;
   }

   public Collection<BaseType> getChangesOnType()
   {
      if (changesOnType == null)
      {
         return Collections.emptyList();
      }
      return changesOnType;
   }

   public String getCmisVersionSupported()
   {
      return cmisVersionSupported;
   }

   public String getLatestChangeLogToken()
   {
      return latestChangeLogToken;
   }

   public String getPrincipalAnonymous()
   {
      return principalAnonymous;
   }

   public String getPrincipalAnyone()
   {
      return principalAnyone;
   }

   public String getProductName()
   {
      return productName;
   }

   public String getProductVersion()
   {
      return productVersion;
   }

   public String getRepositoryDescription()
   {
      return repositoryDescription;
   }

   public String getRepositoryId()
   {
      return repositoryId;
   }

   public String getRepositoryName()
   {
      return repositoryName;
   }

   public String getRootFolderId()
   {
      return rootFolderId;
   }

   public String getThinClientURI()
   {
      return thinClientURI;
   }

   public String getVendorName()
   {
      return vendorName;
   }

   public boolean isChangesIncomplete()
   {
      return changesIncomplete;
   }

   public void setAclCapability(ACLCapability aclCapability)
   {
      this.aclCapability = aclCapability;
   }

   public void setCapabilities(RepositoryCapabilities capabilities)
   {
      this.capabilities = capabilities;
   }

   public void setChangesOnType(Collection<BaseType> changesOnType)
   {
      this.changesOnType = changesOnType;
   }

   public void setCmisVersionSupported(String cmisVersionSupported)
   {
      this.cmisVersionSupported = cmisVersionSupported;
   }

   public void setLatestChangeLogToken(String latestChangeLogToken)
   {
      this.latestChangeLogToken = latestChangeLogToken;
   }

   public void setPrincipalAnonymous(String principalAnonymous)
   {
      this.principalAnonymous = principalAnonymous;
   }

   public void setPrincipalAnyone(String principalAnyone)
   {
      this.principalAnyone = principalAnyone;
   }

   public void setProductName(String productName)
   {
      this.productName = productName;
   }

   public void setProductVersion(String productVersion)
   {
      this.productVersion = productVersion;
   }

   public void setRepositoryDescription(String repositoryDescription)
   {
      this.repositoryDescription = repositoryDescription;
   }

   public void setRepositoryId(String repositoryId)
   {
      this.repositoryId = repositoryId;
   }

   public void setRepositoryName(String repositoryName)
   {
      this.repositoryName = repositoryName;
   }

   public void setRootFolderId(String rootFolderId)
   {
      this.rootFolderId = rootFolderId;
   }

   public void setThinClientURI(String thinClientURI)
   {
      this.thinClientURI = thinClientURI;
   }

   public void setVendorName(String vendorName)
   {
      this.vendorName = vendorName;
   }

   public void setChangesIncomplete(boolean changesIncomplete)
   {
      this.changesIncomplete = changesIncomplete;
   }

}
