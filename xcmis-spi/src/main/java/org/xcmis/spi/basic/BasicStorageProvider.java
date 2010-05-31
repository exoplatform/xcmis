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
package org.xcmis.sp.basic;

import java.util.Collection;
import java.util.List;

import org.xcmis.spi.StorageProvider;
import org.xcmis.spi.model.ACLCapability;
import org.xcmis.spi.model.AccessControlPropagation;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.CapabilityACL;
import org.xcmis.spi.model.CapabilityChanges;
import org.xcmis.spi.model.CapabilityContentStreamUpdatable;
import org.xcmis.spi.model.CapabilityJoin;
import org.xcmis.spi.model.CapabilityQuery;
import org.xcmis.spi.model.CapabilityRendition;
import org.xcmis.spi.model.Permission;
import org.xcmis.spi.model.PermissionMapping;
import org.xcmis.spi.model.RepositoryCapabilities;
import org.xcmis.spi.model.RepositoryInfo;
import org.xcmis.spi.model.SupportedPermissions;
/**
 * Basic Storage Provider
 */
public abstract class BasicStorageProvider implements StorageProvider {
  
  protected static String CMIS_VERSION_SUPPORTED = "1.0";
  protected static String PRINCIPAL_ANONYMOUS = "anonymous";
  protected static String PRINCIPAL_ANY = "any";
  protected static Collection<BaseType> CHANGES_ON_TYPE = null;
  protected static String LATEST_CHANGE_LOG_TOKEN = null;
  protected static boolean CHANGES_INCOMPLETE = true;
  protected static String REPOSITORY_DESCRIPTION = "An xCMIS descendant repository";
  protected static String VENDOR_NAME = "xCMIS.org";
  protected static String PRODUCT_NAME = "xCMIS";
  protected static String PRODUCT_VERSION = "1.0";
  protected static String THIN_CLIENT_URI = null;
  
  protected static CapabilityACL CAPABILITY_ACL = CapabilityACL.NONE;
  protected static CapabilityChanges CAPABILITY_CHANGES = CapabilityChanges.NONE;
  protected static CapabilityContentStreamUpdatable CAPABILITY_CONTENT_STREAM_UPDATABLE = CapabilityContentStreamUpdatable.NONE;
  protected static CapabilityJoin CAPABILITY_JOIN = CapabilityJoin.NONE;
  protected static CapabilityQuery CAPABILITY_QUERY = CapabilityQuery.NONE;
  protected static CapabilityRendition CAPABILITY_RENDITION = CapabilityRendition.NONE;
  protected static boolean CAPABILITY_ALL_VERSIONS_SEARCHABLE = false;
  protected static boolean CAPABILITY_GET_DESCENDANTS = false;
  protected static boolean CAPABILITY_GET_FOLDER_TREE = false;
  protected static boolean CAPABILITY_MULTIFILING = false;
  protected static boolean CAPABILITY_PWC_SEARCHABLE = false;
  protected static boolean CAPABILITY_PWC_UPDATABLE = false;
  protected static boolean CAPABILITY_UNFILING = false;
  protected static boolean CAPABILITY_VERSION_SPECIFIC_FILING = false;
  
  protected static PermissionMapping ACL_CAPABILITY_PERMISSION_MAPPING = null;
  protected static List<Permission> ACL_CAPABILITY_PERMISSIONS = null;
  protected static AccessControlPropagation ACL_CAPABILITY_PROPAGATION = AccessControlPropagation.OBJECTONLY;
  protected static SupportedPermissions SUPPORTED_PERMISSIONS = SupportedPermissions.BASIC;
  
  
  protected RepositoryInfo repositoryInfo;


  public BasicStorageProvider(String repositoryId, String repositoryName, String rootFolderId) {
    this.repositoryInfo = new RepositoryInfo(repositoryId, 
        repositoryName,
        rootFolderId,
        CMIS_VERSION_SUPPORTED, 
        new RepositoryCapabilities( 
            CAPABILITY_ACL, 
            CAPABILITY_CHANGES, 
            CAPABILITY_CONTENT_STREAM_UPDATABLE, 
            CAPABILITY_JOIN, 
            CAPABILITY_QUERY, 
            CAPABILITY_RENDITION, 
            CAPABILITY_ALL_VERSIONS_SEARCHABLE, 
            CAPABILITY_GET_DESCENDANTS, 
            CAPABILITY_GET_FOLDER_TREE, 
            CAPABILITY_MULTIFILING, 
            CAPABILITY_PWC_SEARCHABLE, 
            CAPABILITY_PWC_UPDATABLE, 
            CAPABILITY_UNFILING, 
            CAPABILITY_VERSION_SPECIFIC_FILING), 
        new ACLCapability( 
            ACL_CAPABILITY_PERMISSION_MAPPING, 
            ACL_CAPABILITY_PERMISSIONS, 
            ACL_CAPABILITY_PROPAGATION, 
            SUPPORTED_PERMISSIONS ), 
        PRINCIPAL_ANONYMOUS, 
        PRINCIPAL_ANY, 
        CHANGES_ON_TYPE, 
        LATEST_CHANGE_LOG_TOKEN, 
        CHANGES_INCOMPLETE, 
        REPOSITORY_DESCRIPTION, 
        VENDOR_NAME, 
        PRODUCT_NAME, 
        PRODUCT_VERSION, 
        THIN_CLIENT_URI); 
  }

}
