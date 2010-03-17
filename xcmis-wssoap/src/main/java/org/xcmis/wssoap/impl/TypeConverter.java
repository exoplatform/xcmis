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
package org.xcmis.wssoap.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;

import org.xcmis.core.CmisACLCapabilityType;
import org.xcmis.core.CmisAccessControlEntryType;
import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.CmisAccessControlPrincipalType;
import org.xcmis.core.CmisAllowableActionsType;
import org.xcmis.core.CmisChangeEventType;
import org.xcmis.core.CmisListOfIdsType;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisRenditionType;
import org.xcmis.core.CmisRepositoryCapabilitiesType;
import org.xcmis.core.CmisRepositoryInfoType;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.EnumACLPropagation;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumCapabilityACL;
import org.xcmis.core.EnumSupportedPermissions;
import org.xcmis.core.EnumTypeOfChanges;
import org.xcmis.messaging.CmisContentStreamType;
import org.xcmis.messaging.CmisExtensionType;
import org.xcmis.messaging.CmisObjectListType;
import org.xcmis.messaging.CmisRepositoryEntryType;
import org.xcmis.messaging.CmisObjectParentsType;
import org.xcmis.spi.ACLCapability;
import org.xcmis.spi.AccessControlEntry;
import org.xcmis.spi.AllowableActions;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.RepositoryCapabilities;
import org.xcmis.spi.RepositoryInfo;
import org.xcmis.spi.TypeDefinition;
import org.xcmis.spi.Rendition;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.object.ChangeInfo;
import org.xcmis.spi.object.CmisObject;
import org.xcmis.spi.object.ObjectParent;
import org.xcmis.spi.object.Properties;
import org.xcmis.spi.object.Property;
import org.xcmis.spi.utils.CmisUtils;

/**
 * @author <a href="mailto:max.shaposhnik@exoplatform.com">Max Shaposhnik</a>
 * @version $Id$
 */
public class TypeConverter
{

   public static CmisACLCapabilityType getAclCapabilityType(ACLCapability source)
   {
      CmisACLCapabilityType result = new CmisACLCapabilityType();
      result.setPropagation(EnumACLPropagation.fromValue(source.getPropagation().value()));
      result.setSupportedPermissions(EnumSupportedPermissions.fromValue(source.getSupportedPermissions().value()));
      return result;
   }
   
   public static CmisAllowableActionsType getAllowableActionsType(AllowableActions source)
   {
      CmisAllowableActionsType result = new CmisAllowableActionsType();
      result.setCanAddObjectToFolder(source.canAddObjectToFolder());
      result.setCanApplyACL(source.canApplyACL());
      result.setCanApplyPolicy(source.canApplyPolicy());
      result.setCanCancelCheckOut(source.canCancelCheckOut());
      result.setCanCheckIn(source.canCheckIn());
      result.setCanCheckOut(source.canCheckOut());
      result.setCanCreateDocument(source.canCreateDocument());
      result.setCanCreateFolder(source.canCreateFolder());
      result.setCanCreateRelationship(source.canCreateRelationship());
      result.setCanDeleteContentStream(source.canDeleteContentStream());
      result.setCanDeleteObject(source.canDeleteObject());
      result.setCanDeleteTree(source.canDeleteTree());
      result.setCanGetACL(source.canGetACL());
      result.setCanGetAllVersions(source.canGetAllVersions());
      result.setCanGetAppliedPolicies(source.canGetAppliedPolicies());
      result.setCanGetChildren(source.canGetChildren());
      result.setCanGetContentStream(source.canGetContentStream());
      result.setCanGetDescendants(source.canGetDescendants());
      result.setCanGetFolderParent(source.canGetFolderParent());
      result.setCanGetFolderTree(source.canGetFolderTree());
      result.setCanGetObjectParents(source.canGetObjectParents());
      result.setCanGetObjectRelationships(source.canGetObjectRelationships());
      result.setCanGetProperties(source.canGetProperties());
      result.setCanGetRenditions(source.canGetRenditions());
      result.setCanMoveObject(source.canMoveObject());
      result.setCanRemoveObjectFromFolder(source.canRemoveObjectFromFolder());
      result.setCanRemovePolicy(source.canRemovePolicy());
      result.setCanSetContentStream(source.canSetContentStream());
      result.setCanUpdateProperties(source.canUpdateProperties());
      return result;
   }

   
   public static CmisAccessControlListType getCmisAccessControlListType(List<AccessControlEntry> source)
   {
      CmisAccessControlListType result = new CmisAccessControlListType();
      for (AccessControlEntry one : source)
      {
         result.getPermission().add(getCmisAccessControlEntryType(one));
      }
      return result;
   }

   
   public static CmisObjectType getCmisObjectType(CmisObject object)
   {
      CmisObjectType result = new CmisObjectType();

      CmisPropertiesType props = new CmisPropertiesType();
      for (Map.Entry<String, Property<?>> e : object.getProperties().entrySet())
      {
         props.getProperty().add(getProperty(e.getValue()));
      }
      result.setProperties(props);
      result.setAcl(getCmisAccessControlListType(object.getACL()));
      result.setAllowableActions(getAllowableActionsType(object.getAllowableActions()));
      result.setChangeEventInfo(getChangeEventType(object.getChangeInfo()));
      result.setExactACL(object.isExactACL());
      result.setPolicyIds(getCmisListOfIdsType(object.getPolicyIds()));
      return result;
   }

   public static CmisExtensionType getCmisExtensionType(Object any)
   {
      CmisExtensionType result = new CmisExtensionType();
      result.getAny().add(any);
      return result;
   }

   public static CmisRepositoryInfoType getCmisRepositoryInfoType(RepositoryInfo source)
   {
      CmisRepositoryInfoType result = new CmisRepositoryInfoType();
      result.setAclCapability(getAclCapabilityType(source.getAclCapability()));
      result.setCapabilities(getCmisRepositoryCapabilitiesType(source.getCapabilities()));
      result.setChangesIncomplete(source.getChangesIncomplete());
      result.setCmisVersionSupported(source.getCmisVersionSupported());
      result.setLatestChangeLogToken(source.getLatestChangeLogToken());
      result.setPrincipalAnonymous(source.getPrincipalAnonymous());
      result.setPrincipalAnyone(source.getPrincipalAnyone());
      result.setProductName(source.getProductName());
      result.setProductVersion(source.getProductVersion());
      result.setRepositoryDescription(source.getRepositoryDescription());
      result.setRepositoryId(source.getRepositoryId());
      result.setRepositoryName(source.getRepositoryName());
      result.setRootFolderId(source.getRootFolderId());
      result.setThinClientURI(source.getThinClientURI());
      result.setVendorName(source.getVendorName());
      return result;
   }

   public static CmisRepositoryEntryType getCmisRepositoryEntryType()
   {
      //TODO implement;
      return null;
   }

   public static CmisObjectListType getCmisObjectListType(ItemsList<?> source)
   {
      CmisObjectListType result = new CmisObjectListType();
      result.getObjects().addAll((List<CmisObjectType>)source.getItems()); //TODO: right ?
      result.setHasMoreItems(source.isHasMoreItems());
      result.setNumItems(BigInteger.valueOf(source.getNumItems()));
      return result;
   }

   public static CmisContentStreamType getCmisContentStreamType(ContentStream source)
   {
      CmisContentStreamType result = new CmisContentStreamType();
      result.setFilename(source.getFileName());
      result.setLength(BigInteger.valueOf(source.length()));
      result.setMimeType(source.getMediaType());
      try
      {
         result.setStream(new DataHandler(new ByteArrayDataSource(source.getStream(), source.getMediaType())));
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      return result;
   }

   public static CmisTypeDefinitionType getCmisTypeDefinitionType(TypeDefinition source)
   {
      CmisTypeDefinitionType result = new CmisTypeDefinitionType();
      result.setBaseId(EnumBaseObjectTypeIds.fromValue(source.getBaseId().value()));
      result.setControllableACL(source.isControllableACL());
      result.setControllablePolicy(source.isControllablePolicy());
      result.setCreatable(source.isCreatable());
      result.setDescription(source.getDescription());
      result.setDisplayName(source.getDisplayName());
      result.setFileable(source.isFileable());
      result.setFulltextIndexed(source.isFulltextIndexed());
      result.setId(source.getId());
      result.setIncludedInSupertypeQuery(source.isIncludedInSupertypeQuery());
      result.setLocalName(source.getLocalName());
      result.setLocalNamespace(source.getLocalNamespace());
      result.setParentId(source.getParentId());
      result.setQueryable(source.isQueryable());
      result.setQueryName(source.getQueryName());
      return result;
   }

   public static CmisRepositoryCapabilitiesType getCmisRepositoryCapabilitiesType(RepositoryCapabilities source)
   {
      CmisRepositoryCapabilitiesType result = new CmisRepositoryCapabilitiesType();
      result.setCapabilityACL(EnumCapabilityACL.fromValue(source.getCapabilityACL().value()));
      return result;
   }

   public static CmisPropertiesType getCmisPropertiesType(Properties source)
   {
      CmisPropertiesType result = new CmisPropertiesType();
      for (Map.Entry<String, Property<?>> e : source.getAll().entrySet())
      {
         result.getProperty().add(getProperty(e.getValue()));
      }
      return result;
   }

   public static CmisRenditionType getCmisRenditionType(Rendition source)
   {
      CmisRenditionType result = new CmisRenditionType();
      result.setHeight(BigInteger.valueOf(source.getHeight()));
      result.setKind(source.getKind());
      result.setLength(BigInteger.valueOf(source.getLength()));
      result.setMimetype(source.getMimeType());
      result.setRenditionDocumentId(source.getRenditionDocumentId());
      result.setStreamId(source.getStreamId());
      result.setTitle(source.getTitle());
      result.setWidth(BigInteger.valueOf(source.getWidth()));
      return result;
   }

   

   public static CmisChangeEventType getChangeEventType(ChangeInfo source)
   {
      CmisChangeEventType result = new CmisChangeEventType();
      Calendar cal = source.getChangeTime();
      result.setChangeTime(CmisUtils.fromCalendar(cal));
      result.setChangeType(EnumTypeOfChanges.fromValue(source.getChangeType().value()));
      return result;
   }

   public static CmisProperty getProperty(Property<?> source)
   {
      CmisProperty result = new CmisProperty();
      result.setDisplayName(source.getDisplayName());
      result.setLocalName(source.getLocalName());
      result.setPropertyDefinitionId(source.getId());
      result.setQueryName(source.getQueryName());
      return result;
   }

   
   public static CmisListOfIdsType getCmisListOfIdsType(Collection<String> list)
   {
      CmisListOfIdsType result = new CmisListOfIdsType();
      for (String one : list)
      {
         result.getId().add(one);
      }
      return result;
   }

   public static CmisObjectParentsType getCmisObjectParentsType(ObjectParent source)
   {
      CmisObjectParentsType result = new CmisObjectParentsType();
      result.setObject(getCmisObjectType(source.getObject()));
      result.setRelativePathSegment(source.getRelativePathSegment());
      return result;
   }

   public static CmisAccessControlEntryType getCmisAccessControlEntryType(AccessControlEntry source)
   {
      CmisAccessControlEntryType result = new CmisAccessControlEntryType();
      CmisAccessControlPrincipalType principal = new CmisAccessControlPrincipalType();
      principal.setPrincipalId(source.getPrincipal());
      result.setPrincipal(principal);
      result.getPermission().addAll(source.getPermissions());
      return result;
   }

}
