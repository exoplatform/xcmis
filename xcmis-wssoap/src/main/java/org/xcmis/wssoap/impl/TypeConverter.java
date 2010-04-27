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

import org.xcmis.core.CmisACLCapabilityType;
import org.xcmis.core.CmisAccessControlEntryType;
import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.CmisAccessControlPrincipalType;
import org.xcmis.core.CmisAllowableActionsType;
import org.xcmis.core.CmisChangeEventType;
import org.xcmis.core.CmisChoiceBoolean;
import org.xcmis.core.CmisChoiceDateTime;
import org.xcmis.core.CmisChoiceDecimal;
import org.xcmis.core.CmisChoiceHtml;
import org.xcmis.core.CmisChoiceId;
import org.xcmis.core.CmisChoiceInteger;
import org.xcmis.core.CmisChoiceString;
import org.xcmis.core.CmisChoiceUri;
import org.xcmis.core.CmisListOfIdsType;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.CmisPermissionMapping;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisPropertyBoolean;
import org.xcmis.core.CmisPropertyBooleanDefinitionType;
import org.xcmis.core.CmisPropertyDateTime;
import org.xcmis.core.CmisPropertyDateTimeDefinitionType;
import org.xcmis.core.CmisPropertyDecimal;
import org.xcmis.core.CmisPropertyDecimalDefinitionType;
import org.xcmis.core.CmisPropertyDefinitionType;
import org.xcmis.core.CmisPropertyHtml;
import org.xcmis.core.CmisPropertyHtmlDefinitionType;
import org.xcmis.core.CmisPropertyId;
import org.xcmis.core.CmisPropertyIdDefinitionType;
import org.xcmis.core.CmisPropertyInteger;
import org.xcmis.core.CmisPropertyIntegerDefinitionType;
import org.xcmis.core.CmisPropertyString;
import org.xcmis.core.CmisPropertyStringDefinitionType;
import org.xcmis.core.CmisPropertyUri;
import org.xcmis.core.CmisPropertyUriDefinitionType;
import org.xcmis.core.CmisRenditionType;
import org.xcmis.core.CmisRepositoryCapabilitiesType;
import org.xcmis.core.CmisRepositoryInfoType;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.EnumACLPropagation;
import org.xcmis.core.EnumAllowableActionsKey;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumCapabilityACL;
import org.xcmis.core.EnumCapabilityChanges;
import org.xcmis.core.EnumCapabilityContentStreamUpdates;
import org.xcmis.core.EnumCapabilityJoin;
import org.xcmis.core.EnumCapabilityQuery;
import org.xcmis.core.EnumCapabilityRendition;
import org.xcmis.core.EnumCardinality;
import org.xcmis.core.EnumPropertyType;
import org.xcmis.core.EnumSupportedPermissions;
import org.xcmis.core.EnumTypeOfChanges;
import org.xcmis.core.EnumUpdatability;
import org.xcmis.messaging.CmisContentStreamType;
import org.xcmis.messaging.CmisExtensionType;
import org.xcmis.messaging.CmisObjectInFolderContainerType;
import org.xcmis.messaging.CmisObjectInFolderListType;
import org.xcmis.messaging.CmisObjectInFolderType;
import org.xcmis.messaging.CmisObjectListType;
import org.xcmis.messaging.CmisObjectParentsType;
import org.xcmis.messaging.CmisRepositoryEntryType;
import org.xcmis.messaging.CmisTypeContainer;
import org.xcmis.messaging.CmisTypeDefinitionListType;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.ItemsTree;
import org.xcmis.spi.model.ACLCapability;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.AllowableActions;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.ChangeInfo;
import org.xcmis.spi.model.Choice;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.ContentStreamAllowed;
import org.xcmis.spi.model.ObjectParent;
import org.xcmis.spi.model.PermissionMapping;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.PropertyType;
import org.xcmis.spi.model.Rendition;
import org.xcmis.spi.model.RepositoryCapabilities;
import org.xcmis.spi.model.RepositoryInfo;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.Updatability;
import org.xcmis.spi.model.impl.BooleanProperty;
import org.xcmis.spi.model.impl.DateTimeProperty;
import org.xcmis.spi.model.impl.DecimalProperty;
import org.xcmis.spi.model.impl.HtmlProperty;
import org.xcmis.spi.model.impl.IdProperty;
import org.xcmis.spi.model.impl.IntegerProperty;
import org.xcmis.spi.model.impl.StringProperty;
import org.xcmis.spi.model.impl.UriProperty;
import org.xcmis.spi.utils.CmisUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @author <a href="mailto:max.shaposhnik@exoplatform.com">Max Shaposhnik</a>
 * @version $Id$
 */
public class TypeConverter
{

   public static List<AccessControlEntry> getAccessControlEntryList(List<CmisAccessControlEntryType> source)
   {
      List<AccessControlEntry> res = new ArrayList<AccessControlEntry>();
      for (CmisAccessControlEntryType one : source)
      {
         res.add(getAccessControlEntry(one));
      }
      return res;
   }

   public static AccessControlEntry getAccessControlEntry(CmisAccessControlEntryType source)
   {
      AccessControlEntry res = new AccessControlEntry();
      res.getPermissions().addAll(source.getPermission());
      res.setPrincipal(source.getPrincipal().getPrincipalId());
      return res;
   }

   public static CmisACLCapabilityType getCmisAclCapabilityType(ACLCapability source)
   {
      CmisACLCapabilityType result = new CmisACLCapabilityType();
      result.setPropagation(EnumACLPropagation.fromValue(source.getPropagation().value()));
      result.setSupportedPermissions(EnumSupportedPermissions.fromValue(source.getSupportedPermissions().value()));
      if (source.getMapping() != null)
      {
            result.getMapping().addAll(getCmisPermissionMappingList(source.getMapping()));
      }
      return result;
   }

   public static CmisAllowableActionsType getCmisAllowableActionsType(AllowableActions source)
   {
      CmisAllowableActionsType result = new CmisAllowableActionsType();
      result.setCanAddObjectToFolder(source.isCanAddObjectToFolder());
      result.setCanApplyACL(source.isCanApplyACL());
      result.setCanApplyPolicy(source.isCanApplyPolicy());
      result.setCanCancelCheckOut(source.isCanCancelCheckOut());
      result.setCanCheckIn(source.isCanCheckIn());
      result.setCanCheckOut(source.isCanCheckOut());
      result.setCanCreateDocument(source.isCanCreateDocument());
      result.setCanCreateFolder(source.isCanCreateFolder());
      result.setCanCreateRelationship(source.isCanCreateRelationship());
      result.setCanDeleteContentStream(source.isCanDeleteContentStream());
      result.setCanDeleteObject(source.isCanDeleteObject());
      result.setCanDeleteTree(source.isCanDeleteTree());
      result.setCanGetACL(source.isCanGetACL());
      result.setCanGetAllVersions(source.isCanGetAllVersions());
      result.setCanGetAppliedPolicies(source.isCanGetAppliedPolicies());
      result.setCanGetChildren(source.isCanGetChildren());
      result.setCanGetContentStream(source.isCanGetContentStream());
      result.setCanGetDescendants(source.isCanGetDescendants());
      result.setCanGetFolderParent(source.isCanGetFolderParent());
      result.setCanGetFolderTree(source.isCanGetFolderTree());
      result.setCanGetObjectParents(source.isCanGetObjectParents());
      result.setCanGetObjectRelationships(source.isCanGetObjectRelationships());
      result.setCanGetProperties(source.isCanGetProperties());
      result.setCanGetRenditions(source.isCanGetRenditions());
      result.setCanMoveObject(source.isCanMoveObject());
      result.setCanRemoveObjectFromFolder(source.isCanRemoveObjectFromFolder());
      result.setCanRemovePolicy(source.isCanRemovePolicy());
      result.setCanSetContentStream(source.isCanSetContentStream());
      result.setCanUpdateProperties(source.isCanUpdateProperties());
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

   public static List<AccessControlEntry> getListAccessControlEntry(CmisAccessControlListType source)
   {
      List<AccessControlEntry> result = new ArrayList<AccessControlEntry>();
      if (source != null && source.getPermission().size() > 0)
      {
         for (CmisAccessControlEntryType one : source.getPermission())
         {
            result.add(getAccessControlEntry(one));
         }
      }
      return result;
   }

   public static Map<String, Property<?>> getPropertyMap(CmisPropertiesType input)
   {
      if (input == null)
      {
         return null;
      }
      Map<String, Property<?>> result = new HashMap<String, Property<?>>();
      for (CmisProperty source : input.getProperty())
      {
         if (source instanceof CmisPropertyHtml)
         {
            result.put(source.getPropertyDefinitionId(), new HtmlProperty(source.getPropertyDefinitionId(), source
               .getQueryName(), source.getLocalName(), source.getDisplayName(), ((CmisPropertyHtml)source).getValue()));
         }
         else if (source instanceof CmisPropertyDecimal)
         {
            result.put(source.getPropertyDefinitionId(), new DecimalProperty(source.getPropertyDefinitionId(), source
               .getQueryName(), source.getLocalName(), source.getDisplayName(), ((CmisPropertyDecimal)source)
               .getValue()));
         }
         else if (source instanceof CmisPropertyDateTime)
         {
            result.put(source.getPropertyDefinitionId(), new DateTimeProperty(source.getPropertyDefinitionId(), source
               .getQueryName(), source.getLocalName(), source.getDisplayName(),
               getCalendarList(((CmisPropertyDateTime)source).getValue())));
         }
         else if (source instanceof CmisPropertyId)
         {
            result.put(source.getPropertyDefinitionId(), new IdProperty(source.getPropertyDefinitionId(), source
               .getQueryName(), source.getLocalName(), source.getDisplayName(), ((CmisPropertyId)source).getValue()));
         }
         else if (source instanceof CmisPropertyString)
         {
            result.put(source.getPropertyDefinitionId(),
               new StringProperty(source.getPropertyDefinitionId(), source.getQueryName(), source.getLocalName(),
                  source.getDisplayName(), ((CmisPropertyString)source).getValue()));
         }
         else if (source instanceof CmisPropertyUri)
         {
            result.put(source.getPropertyDefinitionId(), new UriProperty(source.getPropertyDefinitionId(), source
               .getQueryName(), source.getLocalName(), source.getDisplayName(), getURIList(((CmisPropertyUri)source)
               .getValue())));
         }
         else if (source instanceof CmisPropertyBoolean)
         {
            result.put(source.getPropertyDefinitionId(), new BooleanProperty(source.getPropertyDefinitionId(), source
               .getQueryName(), source.getLocalName(), source.getDisplayName(), ((CmisPropertyBoolean)source)
               .getValue()));
         }
         else if (source instanceof CmisPropertyInteger)
         {
            result.put(source.getPropertyDefinitionId(), new IntegerProperty(source.getPropertyDefinitionId(), source
               .getQueryName(), source.getLocalName(), source.getDisplayName(), ((CmisPropertyInteger)source)
               .getValue()));
         }
      }
      return result;
   }

   public static CmisTypeDefinitionListType getCmisTypeDefinitionListType(ItemsList<TypeDefinition> source)
   {
      CmisTypeDefinitionListType result = new CmisTypeDefinitionListType();

      for (TypeDefinition one : source.getItems())
      {
         result.getTypes().add(getCmisTypeDefinitionType(one));
      }
      result.setHasMoreItems(source.isHasMoreItems());
      result.setNumItems(BigInteger.valueOf(source.getNumItems()));
      return result;
   }

   public static CmisExtensionType getCmisExtensionType(Object any)
   {
      CmisExtensionType result = new CmisExtensionType();
      result.getAny().add(any);
      return result;
   }

   public static CmisObjectType getCmisObjectType(CmisObject object)
   {
      CmisObjectType result = new CmisObjectType();

      CmisPropertiesType props = new CmisPropertiesType();
      for (Map.Entry<String, Property<?>> e : object.getProperties().entrySet())
      {
         props.getProperty().add(getCmisProperty(e.getValue()));
      }
      result.setProperties(props);
      result.setAcl(getCmisAccessControlListType(object.getACL()));
      if (object.getAllowableActions() != null)
      {
         result.setAllowableActions(getCmisAllowableActionsType(object.getAllowableActions()));
      }
      if (object.getChangeInfo() != null)
      {
         result.setChangeEventInfo(getCmisChangeEventType(object.getChangeInfo()));
      }
      result.setExactACL(object.isExactACL());
      result.setPolicyIds(getCmisListOfIdsType(object.getPolicyIds()));
      return result;
   }

   public static CmisRepositoryInfoType getCmisRepositoryInfoType(RepositoryInfo source)
   {
      CmisRepositoryInfoType result = new CmisRepositoryInfoType();
      result.setAclCapability(getCmisAclCapabilityType(source.getAclCapability()));
      result.setCapabilities(getCmisRepositoryCapabilitiesType(source.getCapabilities()));
      result.setChangesIncomplete(source.isChangesIncomplete());
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

   public static CmisObjectListType getCmisObjectListType(ItemsList<CmisObject> source)
   {
      CmisObjectListType result = new CmisObjectListType();
      for (CmisObject one : source.getItems())
      {
         result.getObjects().add(getCmisObjectType(one));
      }
      result.setHasMoreItems(source.isHasMoreItems());
      result.setNumItems(BigInteger.valueOf(source.getNumItems()));
      return result;
   }

   public static CmisObjectListType getCmisObjectListType(List<CmisObject> source)
   {
      CmisObjectListType result = new CmisObjectListType();
      for (CmisObject one : source)
      {
         result.getObjects().add(getCmisObjectType(one));
      }
      return result;
   }

   public static List<CmisObjectType> getCmisObjectTypeList(List<CmisObject> source)
   {
      List<CmisObjectType> result = new ArrayList<CmisObjectType>();
      for (CmisObject one : source)
      {
         result.add(getCmisObjectType(one));
      }
      return result;
   }

   public static CmisObjectInFolderListType getCmisObjectInFolderListType(ItemsList<?> source)
   {
      CmisObjectInFolderListType result = new CmisObjectInFolderListType();
      for (Object one : source.getItems())
      {
         if (one instanceof CmisObject)
         {
            result.getObjects().add(getCmisObjectInFolderType((CmisObject)one));
         }
      }
      result.setHasMoreItems(source.isHasMoreItems());
      result.setNumItems(BigInteger.valueOf(source.getNumItems()));
      return result;
   }

   public static CmisObjectInFolderType getCmisObjectInFolderType(CmisObject source)
   {
      CmisObjectInFolderType result = new CmisObjectInFolderType();
      result.setObject(getCmisObjectType(source));
      result.setPathSegment(source.getPathSegment());
      return result;
   }

   public static List<CmisTypeContainer> getCmisTypeContainerList(List<ItemsTree<TypeDefinition>> source)
   {
      List<CmisTypeContainer> result = new ArrayList<CmisTypeContainer>();
      for (ItemsTree<TypeDefinition> one : source)
      {
         CmisTypeContainer containerType = new CmisTypeContainer();
         CmisTypeDefinitionType type = getCmisTypeDefinitionType(one.getContainer());
         containerType.setType(type);
         if (one.getChildren() != null)
         {
            for (ItemsTree<TypeDefinition> d : one.getChildren())
            {
               containerType.getChildren().addAll(getCmisTypeContainerList(d.getChildren()));
            }
         }
         result.add(containerType);
      }
      return result;
   }

   public static List<CmisObjectInFolderContainerType> getCmisObjectInFolderContainerTypeList(
      List<ItemsTree<CmisObject>> source)
   {
      List<CmisObjectInFolderContainerType> result = new ArrayList<CmisObjectInFolderContainerType>();
      for (ItemsTree<CmisObject> one : source)
      {
         CmisObjectInFolderContainerType containerType = new CmisObjectInFolderContainerType();
         CmisObjectInFolderType type = getCmisObjectInFolderType(one.getContainer());
         containerType.setObjectInFolder(type);
         if (one.getChildren() != null)
         {
            for (ItemsTree<CmisObject> d : one.getChildren())
            {
               if (d != null)
               {
                  containerType.getChildren().addAll(getCmisObjectInFolderContainerTypeList(d.getChildren()));
               }
            }
         }
         result.add(containerType);
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

   public static CmisContentStreamType getCmisContentStreamType(ContentStream source)
   {
      CmisContentStreamType result = new CmisContentStreamType();
      result.setFilename(source.getFileName());
      result.setLength(BigInteger.valueOf(source.length()));
      result.setMimeType(source.getMediaType().toString());
      try
      {
         result
            .setStream(new DataHandler(new ByteArrayDataSource(source.getStream(), source.getMediaType().toString())));
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
      result.getPropertyDefinition().addAll(getCmisPropertyDefintitionTypeList(source.getPropertyDefinitions()));
      return result;
   }

   public static CmisChoiceBoolean getCmisChoiceBoolean(Choice<Boolean> source)
   {
      CmisChoiceBoolean cmisChoice = new CmisChoiceBoolean();
      cmisChoice.setDisplayName(source.getDisplayName());
      if (source.getValues() != null)
      {
         for (Boolean v : source.getValues())
         {
            cmisChoice.getValue().add(v);
         }
      }
      if (source.getChoices() != null && source.getChoices().size() > 0)
      {
         for (Choice<Boolean> c : source.getChoices())
         {
            cmisChoice.getChoice().add(getCmisChoiceBoolean(c));
         }
      }
      return cmisChoice;
   }

   public static CmisChoiceDateTime getCmisChoiceDateTime(Choice<Calendar> source)
   {
      CmisChoiceDateTime cmisChoice = new CmisChoiceDateTime();
      cmisChoice.setDisplayName(source.getDisplayName());
      if (source.getValues() != null)
      {
         for (Calendar v : source.getValues())
         {
            cmisChoice.getValue().add(CmisUtils.fromCalendar(v));
         }
      }
      if (source.getChoices() != null && source.getChoices().size() > 0)
      {
         for (Choice<Calendar> c : source.getChoices())
         {
            cmisChoice.getChoice().add(getCmisChoiceDateTime(c));
         }
      }
      return cmisChoice;
   }

   public static CmisChoiceDecimal getCmisChoiceDecimal(Choice<BigDecimal> source)
   {
      CmisChoiceDecimal cmisChoice = new CmisChoiceDecimal();
      cmisChoice.setDisplayName(source.getDisplayName());
      if (source.getValues() != null)
      {
         for (BigDecimal v : source.getValues())
         {
            cmisChoice.getValue().add(v);
         }
      }
      if (source.getChoices() != null && source.getChoices().size() > 0)
      {
         for (Choice<BigDecimal> c : source.getChoices())
         {
            cmisChoice.getChoice().add(getCmisChoiceDecimal(c));
         }
      }
      return cmisChoice;
   }

   public static CmisChoiceHtml getCmisChoiceHtml(Choice<String> source)
   {
      CmisChoiceHtml cmisChoice = new CmisChoiceHtml();
      cmisChoice.setDisplayName(source.getDisplayName());
      if (source.getValues() != null)
      {
         for (String v : source.getValues())
         {
            cmisChoice.getValue().add(v);
         }
      }
      if (source.getChoices() != null && source.getChoices().size() > 0)
      {
         for (Choice<String> c : source.getChoices())
         {
            cmisChoice.getChoice().add(getCmisChoiceHtml(c));
         }
      }
      return cmisChoice;
   }

   public static CmisChoiceId getCmisChoiceId(Choice<String> source)
   {
      CmisChoiceId cmisChoice = new CmisChoiceId();
      cmisChoice.setDisplayName(source.getDisplayName());
      if (source.getValues() != null)
      {
         for (String v : source.getValues())
         {
            cmisChoice.getValue().add(v);
         }
      }
      if (source.getChoices() != null && source.getChoices().size() > 0)
      {
         for (Choice<String> c : source.getChoices())
         {
            cmisChoice.getChoice().add(getCmisChoiceId(c));
         }
      }
      return cmisChoice;
   }

   public static CmisChoiceInteger getCmisChoiceInteger(Choice<BigInteger> source)
   {
      CmisChoiceInteger cmisChoice = new CmisChoiceInteger();
      cmisChoice.setDisplayName(source.getDisplayName());
      if (source.getValues() != null)
      {
         for (BigInteger v : source.getValues())
         {
            cmisChoice.getValue().add(v);
         }
      }
      if (source.getChoices() != null && source.getChoices().size() > 0)
      {
         for (Choice<BigInteger> c : source.getChoices())
         {
            cmisChoice.getChoice().add(getCmisChoiceInteger(c));
         }
      }
      return cmisChoice;
   }

   public static CmisChoiceString getCmisChoiceString(Choice<String> source)
   {
      CmisChoiceString cmisChoice = new CmisChoiceString();
      cmisChoice.setDisplayName(source.getDisplayName());
      if (source.getValues() != null)
      {
         for (String v : source.getValues())
         {
            cmisChoice.getValue().add(v);
         }
      }
      if (source.getChoices() != null && source.getChoices().size() > 0)
      {
         for (Choice<String> c : source.getChoices())
         {
            cmisChoice.getChoice().add(getCmisChoiceString(c));
         }
      }
      return cmisChoice;
   }

   public static CmisChoiceUri getCmisChoiceUri(Choice<URI> source)
   {
      CmisChoiceUri cmisChoice = new CmisChoiceUri();
      cmisChoice.setDisplayName(source.getDisplayName());
      if (source.getValues() != null)
      {
         for (URI v : source.getValues())
         {
            cmisChoice.getValue().add(v.toASCIIString());
         }
      }
      if (source.getChoices() != null && source.getChoices().size() > 0)
      {
         for (Choice<URI> c : source.getChoices())
         {
            cmisChoice.getChoice().add(getCmisChoiceUri(c));
         }
      }
      return cmisChoice;
   }

   @SuppressWarnings("unchecked")
   public static List<CmisPropertyDefinitionType> getCmisPropertyDefintitionTypeList(
      Collection<PropertyDefinition<?>> source)
   {
      if (source == null)
      {
         return Collections.emptyList();
      }

      List<CmisPropertyDefinitionType> result = new ArrayList<CmisPropertyDefinitionType>();
      for (PropertyDefinition<?> definition : source)
      {

         CmisPropertyDefinitionType cmisPropertyDefinition = null;

         switch (definition.getPropertyType())
         {
            case BOOLEAN :
               CmisPropertyBooleanDefinitionType bool = new CmisPropertyBooleanDefinitionType();
               if (definition.getChoices() != null && definition.getChoices().size() > 0)
               {
                  for (Choice<?> c : definition.getChoices())
                  {
                     bool.getChoice().add(getCmisChoiceBoolean((Choice<Boolean>)c));
                  }
               }
               if (definition.getDefaultValue() != null && definition.getDefaultValue().length > 0)
               {
                  CmisPropertyBoolean def = new CmisPropertyBoolean();
                  def.setDisplayName(definition.getDisplayName());
                  def.setLocalName(definition.getLocalName());
                  def.setPropertyDefinitionId(definition.getId());
                  def.setQueryName(definition.getQueryName());

                  for (Object o : definition.getDefaultValue())
                  {
                     def.getValue().add((Boolean)o);
                  }
                  bool.setDefaultValue(def);
               }
               cmisPropertyDefinition = bool;
               break;
            case DATETIME :
               CmisPropertyDateTimeDefinitionType date = new CmisPropertyDateTimeDefinitionType();
               if (definition.getChoices() != null && definition.getChoices().size() > 0)
               {
                  for (Choice<?> c : definition.getChoices())
                  {
                     date.getChoice().add(getCmisChoiceDateTime((Choice<Calendar>)c));
                  }
               }
               if (definition.getDefaultValue() != null && definition.getDefaultValue().length > 0)
               {
                  CmisPropertyDateTime def = new CmisPropertyDateTime();
                  def.setDisplayName(definition.getDisplayName());
                  def.setLocalName(definition.getLocalName());
                  def.setPropertyDefinitionId(definition.getId());
                  def.setQueryName(definition.getQueryName());

                  for (Object o : definition.getDefaultValue())
                  {
                     def.getValue().add(CmisUtils.fromCalendar((Calendar)o));
                  }
                  date.setDefaultValue(def);
               }
               cmisPropertyDefinition = date;
               break;
            case DECIMAL :
               CmisPropertyDecimalDefinitionType decimal = new CmisPropertyDecimalDefinitionType();
               if (definition.getChoices() != null && definition.getChoices().size() > 0)
               {
                  for (Choice<?> c : definition.getChoices())
                  {
                     decimal.getChoice().add(getCmisChoiceDecimal((Choice<BigDecimal>)c));
                  }
               }
               if (definition.getDefaultValue() != null && definition.getDefaultValue().length > 0)
               {
                  CmisPropertyDecimal def = new CmisPropertyDecimal();
                  def.setDisplayName(definition.getDisplayName());
                  def.setLocalName(definition.getLocalName());
                  def.setPropertyDefinitionId(definition.getId());
                  def.setQueryName(definition.getQueryName());

                  for (Object o : definition.getDefaultValue())
                  {
                     def.getValue().add((BigDecimal)o);
                  }
                  decimal.setDefaultValue(def);
               }

               decimal.setMaxValue(definition.getMaxDecimal());
               decimal.setMinValue(definition.getMinDecimal());
               cmisPropertyDefinition = decimal;
               break;
            case HTML :
               CmisPropertyHtmlDefinitionType html = new CmisPropertyHtmlDefinitionType();
               if (definition.getChoices() != null && definition.getChoices().size() > 0)
               {
                  for (Choice<?> c : definition.getChoices())
                  {
                     html.getChoice().add(getCmisChoiceHtml((Choice<String>)c));
                  }
               }
               if (definition.getDefaultValue() != null && definition.getDefaultValue().length > 0)
               {
                  CmisPropertyHtml def = new CmisPropertyHtml();
                  def.setDisplayName(definition.getDisplayName());
                  def.setLocalName(definition.getLocalName());
                  def.setPropertyDefinitionId(definition.getId());
                  def.setQueryName(definition.getQueryName());

                  for (Object o : definition.getDefaultValue())
                  {
                     def.getValue().add((String)o);
                  }
                  html.setDefaultValue(def);
               }
               cmisPropertyDefinition = html;
               break;
            case ID :
               CmisPropertyIdDefinitionType id = new CmisPropertyIdDefinitionType();
               if (definition.getChoices() != null && definition.getChoices().size() > 0)
               {
                  for (Choice<?> c : definition.getChoices())
                  {
                     id.getChoice().add(getCmisChoiceId((Choice<String>)c));
                  }
               }
               if (definition.getDefaultValue() != null && definition.getDefaultValue().length > 0)
               {
                  CmisPropertyId def = new CmisPropertyId();
                  def.setDisplayName(definition.getDisplayName());
                  def.setLocalName(definition.getLocalName());
                  def.setPropertyDefinitionId(definition.getId());
                  def.setQueryName(definition.getQueryName());

                  for (Object o : definition.getDefaultValue())
                  {
                     def.getValue().add((String)o);
                  }
                  id.setDefaultValue(def);
               }
               cmisPropertyDefinition = id;
               break;
            case INTEGER :
               CmisPropertyIntegerDefinitionType integ = new CmisPropertyIntegerDefinitionType();
               if (definition.getChoices() != null && definition.getChoices().size() > 0)
               {
                  for (Choice<?> c : definition.getChoices())
                  {
                     integ.getChoice().add(getCmisChoiceInteger((Choice<BigInteger>)c));
                  }
               }
               if (definition.getDefaultValue() != null && definition.getDefaultValue().length > 0)
               {
                  CmisPropertyInteger def = new CmisPropertyInteger();
                  def.setDisplayName(definition.getDisplayName());
                  def.setLocalName(definition.getLocalName());
                  def.setPropertyDefinitionId(definition.getId());
                  def.setQueryName(definition.getQueryName());

                  for (Object o : definition.getDefaultValue())
                  {
                     def.getValue().add((BigInteger)o);
                  }
                  integ.setDefaultValue(def);
               }

               integ.setMaxValue(definition.getMaxInteger());
               integ.setMinValue(definition.getMinInteger());
               cmisPropertyDefinition = integ;
               break;
            case STRING :
               CmisPropertyStringDefinitionType str = new CmisPropertyStringDefinitionType();
               if (definition.getChoices() != null && definition.getChoices().size() > 0)
               {
                  for (Choice<?> c : definition.getChoices())
                  {
                     str.getChoice().add(getCmisChoiceString((Choice<String>)c));
                  }
               }
               if (definition.getDefaultValue() != null && definition.getDefaultValue().length > 0)
               {
                  CmisPropertyString def = new CmisPropertyString();
                  def.setDisplayName(definition.getDisplayName());
                  def.setLocalName(definition.getLocalName());
                  def.setPropertyDefinitionId(definition.getId());
                  def.setQueryName(definition.getQueryName());

                  for (Object o : definition.getDefaultValue())
                  {
                     def.getValue().add((String)o);
                  }
                  str.setDefaultValue(def);
               }
               str.setMaxLength(BigInteger.valueOf(definition.getMaxLength()));
               cmisPropertyDefinition = str;
               break;
            case URI :
               CmisPropertyUriDefinitionType uri = new CmisPropertyUriDefinitionType();
               if (definition.getChoices() != null && definition.getChoices().size() > 0)
               {
                  for (Choice<?> c : definition.getChoices())
                  {
                     uri.getChoice().add(getCmisChoiceUri((Choice<URI>)c));
                  }
               }
               if (definition.getDefaultValue() != null && definition.getDefaultValue().length > 0)
               {
                  CmisPropertyUri def = new CmisPropertyUri();
                  def.setDisplayName(definition.getDisplayName());
                  def.setLocalName(definition.getLocalName());
                  def.setPropertyDefinitionId(definition.getId());
                  def.setQueryName(definition.getQueryName());

                  for (Object o : definition.getDefaultValue())
                  {
                     def.getValue().add((String)o);
                  }
                  uri.setDefaultValue(def);
               }
               cmisPropertyDefinition = uri;
               break;
         }

         cmisPropertyDefinition.setCardinality(definition.isMultivalued() ? EnumCardinality.MULTI
            : EnumCardinality.SINGLE);
         cmisPropertyDefinition.setDescription(definition.getDescription());
         cmisPropertyDefinition.setDisplayName(definition.getDisplayName());
         cmisPropertyDefinition.setId(definition.getId());
         cmisPropertyDefinition.setInherited(definition.getInherited());
         cmisPropertyDefinition.setLocalName(definition.getLocalName());
         cmisPropertyDefinition.setLocalNamespace(definition.getLocalNamespace());
         cmisPropertyDefinition.setOpenChoice(definition.isOpenChoice());
         cmisPropertyDefinition.setOrderable(definition.isOrderable());
         cmisPropertyDefinition.setPropertyType(EnumPropertyType.fromValue(definition.getPropertyType().value()));
         cmisPropertyDefinition.setQueryable(definition.isQueryable());
         cmisPropertyDefinition.setQueryName(definition.getQueryName());
         cmisPropertyDefinition.setRequired(definition.isRequired());
         cmisPropertyDefinition.setUpdatability(EnumUpdatability.fromValue(definition.getUpdatability().value()));

         result.add(cmisPropertyDefinition);
      }
      return result;
   }

   public static TypeDefinition getTypeDefinition(CmisTypeDefinitionType source)
   {
      TypeDefinition result =
         new TypeDefinition(source.getId(), BaseType.fromValue(source.getBaseId().value()), source.getQueryName(),
            source.getLocalName(), source.getLocalNamespace(), source.getParentId(), source.getDisplayName(), source
               .getDescription(), source.isCreatable(), source.isFileable(), source.isQueryable(), source
               .isFulltextIndexed(), source.isIncludedInSupertypeQuery(), source.isControllablePolicy(), source
               .isControllableACL(), false, null, null, ContentStreamAllowed.ALLOWED, getPropertyDefinitionMap(source
               .getPropertyDefinition()));

      return result;
   }

   @SuppressWarnings("unchecked")
   public static Map<String, PropertyDefinition<?>> getPropertyDefinitionMap(List<CmisPropertyDefinitionType> source)
   {

      Map<String, PropertyDefinition<?>> result = new HashMap<String, PropertyDefinition<?>>();
      for (CmisPropertyDefinitionType one : source)
      {
         result.put(one.getId(), new PropertyDefinition(one.getId(), one.getQueryName(), one.getLocalName(), one
            .getLocalNamespace(), one.getDisplayName(), one.getDescription(), PropertyType.fromValue(one
            .getPropertyType().value()), Updatability.fromValue(one.getUpdatability().value()), one.isInherited(), one
            .isRequired(), one.isQueryable(), one.isOrderable(), one.isOpenChoice(), false, null, null));

      }
      return result;
   }

   public static CmisRepositoryCapabilitiesType getCmisRepositoryCapabilitiesType(RepositoryCapabilities source)
   {
      CmisRepositoryCapabilitiesType result = new CmisRepositoryCapabilitiesType();
      result.setCapabilityACL(EnumCapabilityACL.fromValue(source.getCapabilityACL().value()));
      result.setCapabilityAllVersionsSearchable(source.isCapabilityAllVersionsSearchable());
      result.setCapabilityChanges(EnumCapabilityChanges.fromValue(source.getCapabilityChanges().value()));
      result.setCapabilityContentStreamUpdatability(EnumCapabilityContentStreamUpdates.fromValue(source
         .getCapabilityContentStreamUpdatable().value()));
      result.setCapabilityGetDescendants(source.isCapabilityGetDescendants());
      result.setCapabilityGetFolderTree(source.isCapabilityGetFolderTree());
      result.setCapabilityJoin(EnumCapabilityJoin.fromValue(source.getCapabilityJoin().value()));
      result.setCapabilityMultifiling(source.isCapabilityMultifiling());
      result.setCapabilityPWCSearchable(source.isCapabilityPWCSearchable());
      result.setCapabilityPWCUpdatable(source.isCapabilityPWCUpdatable());
      result.setCapabilityQuery(EnumCapabilityQuery.fromValue(source.getCapabilityQuery().value()));
      result.setCapabilityRenditions(EnumCapabilityRendition.fromValue(source.getCapabilityRenditions().value()));
      result.setCapabilityUnfiling(source.isCapabilityUnfiling());
      result.setCapabilityVersionSpecificFiling(source.isCapabilityVersionSpecificFiling());
      return result;
   }

   public static CmisPropertiesType getCmisPropertiesType(CmisObject source)
   {
      CmisPropertiesType result = new CmisPropertiesType();
      for (Map.Entry<String, Property<?>> e : source.getProperties().entrySet())
      {
         result.getProperty().add(getCmisProperty(e.getValue()));
      }
      return result;
   }

   public static List<CmisRenditionType> getCmisRenditionTypeList(List<Rendition> source)
   {
      List<CmisRenditionType> result = new ArrayList<CmisRenditionType>();
      for (Rendition one : source)
      {
         result.add(getCmisRenditionType(one));
      }
      return result;
   }

   public static Rendition getRendition(CmisRenditionType source)
   {
      Rendition result = new Rendition();
      result.setHeight(source.getHeight().intValue());
      result.setKind(source.getKind());
      result.setLength(source.getLength().intValue());
      result.setMimeType(source.getMimetype());
      result.setRenditionDocumentId(source.getRenditionDocumentId());
      result.setStreamId(source.getStreamId());
      result.setTitle(source.getTitle());
      result.setWidth(source.getWidth().intValue());
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

   public static CmisChangeEventType getCmisChangeEventType(ChangeInfo source)
   {
      CmisChangeEventType result = new CmisChangeEventType();
      Calendar cal = source.getChangeTime();
      result.setChangeTime(CmisUtils.fromCalendar(cal));
      result.setChangeType(EnumTypeOfChanges.fromValue(source.getChangeType().value()));
      return result;
   }

   public static List<CmisPermissionMapping> getCmisPermissionMappingList(PermissionMapping source)
   {
      List<CmisPermissionMapping> result = new ArrayList<CmisPermissionMapping>();
      for (Map.Entry<String, Collection<String>> e : source.getAll().entrySet())
      {
         CmisPermissionMapping one = new CmisPermissionMapping();
         if (e.getValue() != null && e.getValue().size() > 0)
         {
            one.setKey(EnumAllowableActionsKey.fromValue(e.getKey()));
            one.getPermission().addAll(e.getValue());
            result.add(one);
         }
      }
      return result;
   }

   @SuppressWarnings("unchecked")
   public static CmisProperty getCmisProperty(Property<?> source)
   {
      CmisProperty result = null;
      if (source instanceof BooleanProperty)
      {
         result = new CmisPropertyBoolean();
         ((CmisPropertyBoolean)result).getValue().addAll((Collection<? extends Boolean>)(source.getValues()));
      }
      else if (source instanceof DateTimeProperty)
      {
         result = new CmisPropertyDateTime();
         ((CmisPropertyDateTime)result).getValue().addAll(
            (Collection<? extends XMLGregorianCalendar>)(source.getValues()));
      }
      else if (source instanceof DecimalProperty)
      {
         result = new CmisPropertyDecimal();
         ((CmisPropertyDecimal)result).getValue().addAll((Collection<? extends BigDecimal>)(source.getValues()));
      }
      else if (source instanceof HtmlProperty)
      {
         result = new CmisPropertyHtml();
         ((CmisPropertyHtml)result).getValue().addAll((Collection<? extends String>)(source.getValues()));
      }
      else if (source instanceof IdProperty)
      {
         result = new CmisPropertyId();
         ((CmisPropertyId)result).getValue().addAll((Collection<? extends String>)(source.getValues()));
      }
      else if (source instanceof IntegerProperty)
      {
         result = new CmisPropertyInteger();
         ((CmisPropertyInteger)result).getValue().addAll((Collection<? extends BigInteger>)(source.getValues()));
      }
      else if (source instanceof StringProperty)
      {
         result = new CmisPropertyString();
         ((CmisPropertyString)result).getValue().addAll((Collection<? extends String>)(source.getValues()));
      }
      else if (source instanceof UriProperty)
      {
         result = new CmisPropertyUri();
         ((CmisPropertyUri)result).getValue().addAll((Collection<? extends String>)(source.getValues()));
      }
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

   public static CmisAccessControlEntryType getCmisAccessControlEntryType(AccessControlEntry source)
   {
      CmisAccessControlEntryType result = new CmisAccessControlEntryType();
      CmisAccessControlPrincipalType principal = new CmisAccessControlPrincipalType();
      principal.setPrincipalId(source.getPrincipal());
      result.setPrincipal(principal);
      result.getPermission().addAll(source.getPermissions());
      return result;
   }

   public static List<URI> getURIList(List<String> values)
   {
      List<URI> result = new ArrayList<URI>();
      for (String one : values)
      {
         if (one != null)
         {
            try
            {
               result.add(new URI(one));
            }
            catch (URISyntaxException e)
            {
               e.printStackTrace();
            }
         }
      }
      return result;
   }

   public static List<Calendar> getCalendarList(List<XMLGregorianCalendar> source)
   {
      List<Calendar> result = new ArrayList<Calendar>();
      for (XMLGregorianCalendar one : source)
      {
         if (one != null)
         {
            result.add(one.toGregorianCalendar());
         }
      }
      return result;
   }
}
