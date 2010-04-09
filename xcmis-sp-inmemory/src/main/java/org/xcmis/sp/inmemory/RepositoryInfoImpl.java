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

package org.xcmis.sp.inmemory;

import org.xcmis.spi.CMIS;
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
import org.xcmis.spi.model.Permission.BasicPermissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * JCR implementation of {@link RepositoryInfo}. Assumes all CMIS repositories
 * have the same capabilities, permissions, etc.
 *
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class RepositoryInfoImpl implements RepositoryInfo, RepositoryCapabilities, ACLCapability
{

   private static final List<PermissionMapping> PERMISSION_MAPPING;

   private static final List<Permission> PERMISSIONS;

   static
   {
      List<PermissionMapping> pm = new ArrayList<PermissionMapping>(34);

      pm.add(createMapping(PermissionMapping.CAN_GET_DESCENDENTS_FOLDER, //
         BasicPermissions.CMIS_READ.value()));

      pm.add(createMapping(PermissionMapping.CAN_GET_CHILDREN_FOLDER, //
         BasicPermissions.CMIS_READ.value()));

      pm.add(createMapping(PermissionMapping.CAN_GET_PARENTS_FOLDER, //
         BasicPermissions.CMIS_READ.value()));

      pm.add(createMapping(PermissionMapping.CAN_GET_FOLDER_PARENT_OBJECT, //
         BasicPermissions.CMIS_READ.value()));

      pm.add(createMapping(PermissionMapping.CAN_CREATE_DOCUMENT_FOLDER, //
         BasicPermissions.CMIS_READ.value()));

      pm.add(createMapping(PermissionMapping.CAN_CREATE_FOLDER_FOLDER, //
         BasicPermissions.CMIS_READ.value()));

      pm.add(createMapping(PermissionMapping.CAN_CREATE_RELATIONSHIP_SOURCE, //
         BasicPermissions.CMIS_READ.value()));

      pm.add(createMapping(PermissionMapping.CAN_CREATE_RELATIONSHIP_TARGET, //
         BasicPermissions.CMIS_READ.value()));

      pm.add(createMapping(PermissionMapping.CAN_GET_PROPERTIES_OBJECT, //
         BasicPermissions.CMIS_READ.value()));

      pm.add(createMapping(PermissionMapping.CAN_VIEW_CONTENT_OBJECT, //
         BasicPermissions.CMIS_READ.value()));

      pm.add(createMapping(PermissionMapping.CAN_UPDATE_PROPERTIES_OBJECT, //
         BasicPermissions.CMIS_WRITE.value()));

      pm.add(createMapping(PermissionMapping.CAN_MOVE_OBJECT, //
         BasicPermissions.CMIS_WRITE.value()));

      pm.add(createMapping(PermissionMapping.CAN_MOVE_TARGET, //
         BasicPermissions.CMIS_READ.value()));

      pm.add(createMapping(PermissionMapping.CAN_MOVE_SOURCE, //
         BasicPermissions.CMIS_READ.value()));

      pm.add(createMapping(PermissionMapping.CAN_DELETE_OBJECT, //
         BasicPermissions.CMIS_WRITE.value()));

      pm.add(createMapping(PermissionMapping.CAN_DELETE_TREE_FOLDER, //
         BasicPermissions.CMIS_WRITE.value()));

      pm.add(createMapping(PermissionMapping.CAN_SET_CONTENT_DOCUMENT, //
         BasicPermissions.CMIS_WRITE.value()));

      pm.add(createMapping(PermissionMapping.CAN_DELETE_CONTENT_DOCUMENT, //
         BasicPermissions.CMIS_WRITE.value()));

      pm.add(createMapping(PermissionMapping.CAN_ADD_TO_FOLDER_OBJECT, //
         BasicPermissions.CMIS_READ.value()));

      pm.add(createMapping(PermissionMapping.CAN_ADD_TO_FOLDER_FOLDER, //
         BasicPermissions.CMIS_READ.value()));

      pm.add(createMapping(PermissionMapping.CAN_REMOVE_FROM_FOLDER_OBJECT, //
         BasicPermissions.CMIS_READ.value()));

      pm.add(createMapping(PermissionMapping.CAN_REMOVE_FROM_FOLDER_FOLDER, //
         BasicPermissions.CMIS_READ.value()));

      pm.add(createMapping(PermissionMapping.CAN_CHECKOUT_DOCUMENT, //
         BasicPermissions.CMIS_WRITE.value()));

      pm.add(createMapping(PermissionMapping.CAN_CANCEL_CHECKOUT_DOCUMENT, //
         BasicPermissions.CMIS_WRITE.value()));

      pm.add(createMapping(PermissionMapping.CAN_CHECKIN_DOCUMENT, //
         BasicPermissions.CMIS_WRITE.value()));

      pm.add(createMapping(PermissionMapping.CAN_GET_ALL_VERSIONS_VERSION_SERIES, //
         BasicPermissions.CMIS_READ.value()));

      pm.add(createMapping(PermissionMapping.CAN_GET_OBJECT_RELATIONSHIPS_OBJECT, //
         BasicPermissions.CMIS_READ.value()));

      pm.add(createMapping(PermissionMapping.CAN_ADD_POLICY_OBJECT, //
         BasicPermissions.CMIS_WRITE.value()));

      pm.add(createMapping(PermissionMapping.CAN_ADD_POLICY_POLICY, //
         BasicPermissions.CMIS_READ.value()));

      pm.add(createMapping(PermissionMapping.CAN_REMOVE_POLICY_OBJECT, //
         BasicPermissions.CMIS_WRITE.value()));

      pm.add(createMapping(PermissionMapping.CAN_REMOVE_POLICY_POLICY, //
         BasicPermissions.CMIS_READ.value()));

      pm.add(createMapping(PermissionMapping.CAN_GET_APPLIED_POLICIES_OBJECT, //
         BasicPermissions.CMIS_READ.value()));

      pm.add(createMapping(PermissionMapping.CAN_GET_ACL_OBJECT, //
         BasicPermissions.CMIS_READ.value()));

      pm.add(createMapping(PermissionMapping.CAN_APPLY_ACL_OBJECT, //
         BasicPermissions.CMIS_WRITE.value()));

      PERMISSION_MAPPING = Collections.unmodifiableList(pm);

      List<Permission> p = new ArrayList<Permission>(4);
      for (BasicPermissions b : BasicPermissions.values())
      {
         p.add(new Permission(b.value(), ""));
      }

      PERMISSIONS = Collections.unmodifiableList(p);
   }

   private static PermissionMapping createMapping(String action, String... permissions)
   {
      return new PermissionMapping(action, Arrays.asList(permissions));
   }

   //

   private final String repositoryId;

   public RepositoryInfoImpl(String repositoryId)
   {
      this.repositoryId = repositoryId;
   }

   /**
    * {@inheritDoc}
    */
   public ACLCapability getAclCapability()
   {
      return this;
   }

   /**
    * {@inheritDoc}
    */
   public RepositoryCapabilities getCapabilities()
   {
      return this;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isChangesIncomplete()
   {
      // Has not sense, changes log is not supported at the moment.
      return true;
   }

   /**
    * {@inheritDoc}
    */
   public Collection<BaseType> getChangesOnType()
   {
      // Changes Log feature not supported.
      return Collections.emptyList();
   }

   /**
    * {@inheritDoc}
    */
   public String getCmisVersionSupported()
   {
      return CMIS.SUPPORTED_VERSION;
   }

   /**
    * {@inheritDoc}
    */
   public String getLatestChangeLogToken()
   {
      // Changes Log feature not supported.
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public String getPrincipalAnonymous()
   {
      return "anonymous";
   }

   /**
    * {@inheritDoc}
    */
   public String getPrincipalAnyone()
   {
      return "any";
   }

   /**
    * {@inheritDoc}
    */
   public String getProductName()
   {
      return "xCMIS (eXo InMemory SP)";
   }

   /**
    * {@inheritDoc}
    */
   public String getProductVersion()
   {
      return "1.0-Beta02";
   }

   public String getRepositoryDescription()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public String getRepositoryId()
   {
      return repositoryId;
   }

   /**
    * {@inheritDoc}
    */
   public String getRepositoryName()
   {
      return repositoryId;
   }

   /**
    * {@inheritDoc}
    */
   public String getRootFolderId()
   {
      // TODO : move from StorageImpl
      return StorageImpl.ROOT_FOLDER_ID;
   }

   /**
    * {@inheritDoc}
    */
   public String getThinClientURI()
   {
      // Should be configurable.
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public String getVendorName()
   {
      return "eXo Platform";
   }

   /**
    * {@inheritDoc}
    */
   public CapabilityACL getCapabilityACL()
   {
      return CapabilityACL.MANAGE;
   }

   /**
    * {@inheritDoc}
    */
   public CapabilityChanges getCapabilityChanges()
   {
      return CapabilityChanges.NONE;
   }

   /**
    * {@inheritDoc}
    */
   public CapabilityContentStreamUpdatable getCapabilityContentStreamUpdatable()
   {
      return CapabilityContentStreamUpdatable.ANYTIME;
   }

   /**
    * {@inheritDoc}
    */
   public CapabilityJoin getCapabilityJoin()
   {
      return CapabilityJoin.NONE;
   }

   /**
    * {@inheritDoc}
    */
   public CapabilityQuery getCapabilityQuery()
   {
      return CapabilityQuery.NONE;
   }

   /**
    * {@inheritDoc}
    */
   public CapabilityRendition getCapabilityRenditions()
   {
      return CapabilityRendition.NONE;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCapabilityAllVersionsSearchable()
   {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCapabilityGetDescendants()
   {
      return true;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCapabilityGetFolderTree()
   {
      return true;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCapabilityMultifiling()
   {
      return true;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCapabilityPWCSearchable()
   {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCapabilityPWCUpdatable()
   {
      return true;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCapabilityUnfiling()
   {
      return true;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCapabilityVersionSpecificFiling()
   {
      return false;
   }

   /**
    * {@inheritDoc}
    */
   public List<PermissionMapping> getMapping()
   {
      return PERMISSION_MAPPING;
   }

   /**
    * {@inheritDoc}
    */
   public List<Permission> getPermissions()
   {
      return PERMISSIONS;
   }

   /**
    * {@inheritDoc}
    */
   public AccessControlPropagation getPropagation()
   {
      return AccessControlPropagation.REPOSITORYDETERMINED;
   }

   /**
    * {@inheritDoc}
    */
   public SupportedPermissions getSupportedPermissions()
   {
      return SupportedPermissions.BASIC;
   }

}
