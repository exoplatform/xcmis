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

package org.xcmis.sp.inmemory;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.xcmis.core.CmisACLCapabilityType;
import org.xcmis.core.CmisChoice;
import org.xcmis.core.CmisChoiceBoolean;
import org.xcmis.core.CmisChoiceDateTime;
import org.xcmis.core.CmisChoiceDecimal;
import org.xcmis.core.CmisChoiceHtml;
import org.xcmis.core.CmisChoiceId;
import org.xcmis.core.CmisChoiceInteger;
import org.xcmis.core.CmisChoiceString;
import org.xcmis.core.CmisChoiceUri;
import org.xcmis.core.CmisPermissionDefinition;
import org.xcmis.core.CmisPermissionMapping;
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
import org.xcmis.core.CmisRepositoryCapabilitiesType;
import org.xcmis.core.CmisRepositoryInfoType;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.CmisTypeDocumentDefinitionType;
import org.xcmis.core.CmisTypeFolderDefinitionType;
import org.xcmis.core.CmisTypePolicyDefinitionType;
import org.xcmis.core.CmisTypeRelationshipDefinitionType;
import org.xcmis.core.EnumACLPropagation;
import org.xcmis.core.EnumAllowableActionsKey;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumBasicPermissions;
import org.xcmis.core.EnumCapabilityACL;
import org.xcmis.core.EnumCapabilityChanges;
import org.xcmis.core.EnumCapabilityContentStreamUpdates;
import org.xcmis.core.EnumCapabilityJoin;
import org.xcmis.core.EnumCapabilityQuery;
import org.xcmis.core.EnumCapabilityRendition;
import org.xcmis.core.EnumCardinality;
import org.xcmis.core.EnumContentStreamAllowed;
import org.xcmis.core.EnumDateTimeResolution;
import org.xcmis.core.EnumPropertiesBase;
import org.xcmis.core.EnumPropertiesDocument;
import org.xcmis.core.EnumPropertiesFolder;
import org.xcmis.core.EnumPropertiesPolicy;
import org.xcmis.core.EnumPropertiesRelationship;
import org.xcmis.core.EnumPropertyType;
import org.xcmis.core.EnumUpdatability;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.Connection;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.Storage;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.impl.CmisObjectIdentifier;
import org.xcmis.spi.object.ContentStream;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.security.auth.login.LoginException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class InMemStorage implements Storage
{

   /** The Constant NO_PARENT. */
   protected static final Set<String> NO_PARENT = Collections.emptySet();

   /**
    * Generate id.
    * 
    * @return the string
    */
   public static String generateId()
   {
      return UUID.randomUUID().toString();
   }

   /** The config. */
   private StorageConfig config;

   /** The info. */
   private CmisRepositoryInfoType info;

   /** The Constant ROOT_FOLDER_ID. */
   public static final String ROOT_FOLDER_ID = generateId();

   /** The Constant ANY. */
   public static final String ANY = "any".intern();

   /** The Constant SYSTEM. */
   public static final String SYSTEM = "system".intern();

   /** The Constant ANONYMOUS. */
   public static final String ANONYMOUS = "anonymous".intern();

   /** TRUE if change token feature if 'on' FALSE otherwise. */
   final boolean changeTokenFeature;

   /** Map of id -> data. */
   final Map<String, CmisObjectIdentifier> objects;

   /** Map of id -> children IDs. */
   final Map<String, Set<String>> children;

   /** Map of id -> parent IDs, or null if unfiled. */
   private final Map<String, Set<String>> parents;

   /** Map of id -> versions. */
   private final Map<String, Set<String>> versions;

   private final Map<String, String> pwc;

   /** Unfiled objects set. */

   private final Set<String> unfiled;

   /** Map of id -> content. */
   final Map<String, ContentStream> contents;

   /** The map ID > types. */
   final Map<String, CmisTypeDefinitionType> types;

   final Map<String, Set<String>> typeChildren;

   /** The map ID > PropertyDefinitions. */
   final Map<String, List<CmisPropertyDefinitionType>> propertyDefinitions;

   /**
    * Instantiates a new repository impl.
    * 
    * @param repositoryConfiguration the repository configuration
    */
   public InMemStorage(StorageConfig config)
   {
      this.config = config;

      this.objects = new ConcurrentHashMap<String, CmisObjectIdentifier>();
      this.pwc = new ConcurrentHashMap<String, String>();
      this.children = new ConcurrentHashMap<String, Set<String>>();
      this.parents = new ConcurrentHashMap<String, Set<String>>();
      this.versions = new ConcurrentHashMap<String, Set<String>>();
      this.unfiled = new HashSet<String>();
      this.contents = new ConcurrentHashMap<String, ContentStream>();
      this.types = new ConcurrentHashMap<String, CmisTypeDefinitionType>();
      this.typeChildren = new ConcurrentHashMap<String, Set<String>>();
      this.propertyDefinitions = new ConcurrentHashMap<String, List<CmisPropertyDefinitionType>>();

      changeTokenFeature =
         config.getProperties().get("exo.cmis.changetoken.feature") != null ? (Boolean)config.getProperties().get(
            "exo.cmis.changetoken.feature") : true;

      init();

      CmisTypeDefinitionType typeDefinition = getTypeDefinition("cmis:folder");
      Calendar date = Calendar.getInstance();
      Map<String, PropertyData<?>> newProperties = new ConcurrentHashMap<String, PropertyData<?>>();

      newProperties.put(CMIS.OBJECT_ID, new IdPropertyData(getPropertyDefinition(
         typeDefinition.getPropertyDefinition(), CMIS.OBJECT_ID), ROOT_FOLDER_ID));

      newProperties.put(CMIS.NAME, new StringPropertyData(getPropertyDefinition(typeDefinition.getPropertyDefinition(),
         CMIS.NAME), ""));

      newProperties.put(CMIS.OBJECT_TYPE_ID, new IdPropertyData(getPropertyDefinition(typeDefinition
         .getPropertyDefinition(), CMIS.OBJECT_TYPE_ID), "cmis:folder"));

      newProperties.put(CMIS.BASE_TYPE_ID, new IdPropertyData(getPropertyDefinition(typeDefinition
         .getPropertyDefinition(), CMIS.BASE_TYPE_ID), typeDefinition.getBaseId().value()));

      newProperties.put(CMIS.CREATED_BY, new StringPropertyData(getPropertyDefinition(typeDefinition
         .getPropertyDefinition(), CMIS.CREATED_BY), "root"));

      newProperties.put(CMIS.CREATION_DATE, new DateTimePropertyData(getPropertyDefinition(typeDefinition
         .getPropertyDefinition(), CMIS.CREATION_DATE), date));

      newProperties.put(CMIS.LAST_MODIFIED_BY, new StringPropertyData(getPropertyDefinition(typeDefinition
         .getPropertyDefinition(), CMIS.LAST_MODIFIED_BY), "root"));

      newProperties.put(CMIS.LAST_MODIFICATION_DATE, new DateTimePropertyData(getPropertyDefinition(typeDefinition
         .getPropertyDefinition(), CMIS.LAST_MODIFICATION_DATE), date));

      newProperties.put(CMIS.CHANGE_TOKEN, new StringPropertyData(getPropertyDefinition(typeDefinition
         .getPropertyDefinition(), CMIS.CHANGE_TOKEN), generateId()));

      newProperties.put(CMIS.PARENT_ID, new IdPropertyData(getPropertyDefinition(
         typeDefinition.getPropertyDefinition(), CMIS.PARENT_ID), ROOT_FOLDER_ID));

      newProperties.put(CMIS.PATH, new StringPropertyData(getPropertyDefinition(typeDefinition.getPropertyDefinition(),
         CMIS.PATH), "/"));

      newProperties.put(CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS, new IdPropertyData(getPropertyDefinition(typeDefinition
         .getPropertyDefinition(), CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS), (String)null));

      objects.put(ROOT_FOLDER_ID, new InmemoryObjectData(ROOT_FOLDER_ID, newProperties, null, null));
   }

   private CmisPropertyDefinitionType getPropertyDefinition(List<CmisPropertyDefinitionType> all, String propertyId)
   {
      if (all != null)
      {
         for (CmisPropertyDefinitionType propDef : all)
         {
            if (propDef.getId().equals(propertyId))
               return propDef;
         }
      }
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public CmisRepositoryInfoType getInfo()
   {
      if (info == null)
      {
         info = new CmisRepositoryInfoType();
         info.setRepositoryId(config.getId());
         info.setRepositoryName(config.getId());
         info.setCmisVersionSupported(CMIS.SUPPORTED_VERSION);
         info.setRepositoryDescription("CMIS InMemory Storage");
         info.setProductName("xCMIS (Inmemory SP)");
         info.setVendorName("eXoPlatform");
         info.setProductVersion("1.0-Beta02");
         info.setRootFolderId(ROOT_FOLDER_ID);
         CmisRepositoryCapabilitiesType capabilities = new CmisRepositoryCapabilitiesType();
         capabilities.setCapabilityACL(EnumCapabilityACL.MANAGE);
         capabilities.setCapabilityAllVersionsSearchable(true);
         capabilities.setCapabilityChanges(EnumCapabilityChanges.NONE);
         capabilities.setCapabilityContentStreamUpdatability(EnumCapabilityContentStreamUpdates.ANYTIME);
         capabilities.setCapabilityGetDescendants(true);
         capabilities.setCapabilityGetFolderTree(true);
         capabilities.setCapabilityJoin(EnumCapabilityJoin.NONE);
         capabilities.setCapabilityMultifiling(true);
         capabilities.setCapabilityPWCSearchable(true);
         capabilities.setCapabilityPWCUpdatable(true);
         capabilities.setCapabilityQuery(EnumCapabilityQuery.NONE);
         capabilities.setCapabilityRenditions(EnumCapabilityRendition.NONE);
         capabilities.setCapabilityUnfiling(true);
         capabilities.setCapabilityVersionSpecificFiling(false);
         info.setCapabilities(capabilities);
         CmisACLCapabilityType aclCapabilities = new CmisACLCapabilityType();
         for (EnumBasicPermissions perm : EnumBasicPermissions.values())
         {
            CmisPermissionDefinition permDef = new CmisPermissionDefinition();
            permDef.setPermission(perm.value());
            aclCapabilities.getPermissions().add(permDef);
         }
         aclCapabilities.setPropagation(EnumACLPropagation.REPOSITORYDETERMINED);
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_GET_DESCENDENTS_FOLDER, // 
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_GET_CHILDREN_FOLDER, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_GET_PARENTS_FOLDER, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_GET_FOLDER_PARENT_OBJECT, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_CREATE_DOCUMENT_FOLDER, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_CREATE_FOLDER_FOLDER, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_CREATE_RELATIONSHIP_SOURCE, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_CREATE_RELATIONSHIP_TARGET, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_GET_PROPERTIES_OBJECT, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_VIEW_CONTENT_OBJECT, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_UPDATE_PROPERTIES_OBJECT, //
            EnumBasicPermissions.CMIS_WRITE.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_MOVE_OBJECT, //
            EnumBasicPermissions.CMIS_WRITE.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_MOVE_TARGET, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_MOVE_SOURCE, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_DELETE_OBJECT, //
            EnumBasicPermissions.CMIS_WRITE.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_DELETE_TREE_FOLDER, //
            EnumBasicPermissions.CMIS_WRITE.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_SET_CONTENT_DOCUMENT, //
            EnumBasicPermissions.CMIS_WRITE.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_DELETE_CONTENT_DOCUMENT, //
            EnumBasicPermissions.CMIS_WRITE.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_ADD_TO_FOLDER_OBJECT, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_ADD_TO_FOLDER_FOLDER, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_REMOVE_FROM_FOLDER_OBJECT, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_REMOVE_FROM_FOLDER_FOLDER, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_CHECKOUT_DOCUMENT, //
            EnumBasicPermissions.CMIS_WRITE.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_CANCEL_CHECKOUT_DOCUMENT, //
            EnumBasicPermissions.CMIS_WRITE.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_CHECKIN_DOCUMENT, //
            EnumBasicPermissions.CMIS_WRITE.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_GET_ALL_VERSIONS_VERSION_SERIES, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_GET_OBJECT_RELATIONSHIPS_OBJECT, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_ADD_POLICY_OBJECT, // 
            EnumBasicPermissions.CMIS_WRITE.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_ADD_POLICY_POLICY, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_REMOVE_POLICY_OBJECT, // 
            EnumBasicPermissions.CMIS_WRITE.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_REMOVE_POLICY_POLICY, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_GET_APPLIED_POLICIES_OBJECT, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_GET_ACL_OBJECT, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_APPLY_ACL_OBJECT, //
            EnumBasicPermissions.CMIS_WRITE.value()));
         info.setAclCapability(aclCapabilities);
         info.setPrincipalAnonymous(ANONYMOUS);
         info.setPrincipalAnyone(ANY);
      }
      return info;
   }

   /**
    * {@inheritDoc}
    */
   public Connection login(ConversationState conversation)
   {
      return new InmemoryConnection(this, conversation);
   }

   /**
    * {@inheritDoc}
    */
   public Connection login(String user, String password) throws LoginException
   {
      return new InmemoryConnection(this, new ConversationState(new Identity(user)));
   }

   /**
    * Create permission mapping object.
    * 
    * @param actionsKey the enumeration for allowable actions key
    * @param permissions the string array of permissions
    * @return CmisPermissionMapping
    */
   private CmisPermissionMapping createPermissionMapping(EnumAllowableActionsKey actionsKey, String... permissions)
   {
      if (permissions == null)
         throw new NullPointerException("permissions is null.");
      if (actionsKey == null)
         throw new NullPointerException("actionsKey is null.");
      CmisPermissionMapping mapping = new CmisPermissionMapping();
      mapping.setKey(actionsKey);
      if (permissions.length == 0)
      {
         mapping.getPermission().add(EnumBasicPermissions.CMIS_ALL.value());
      }
      else
      {
         for (String permission : permissions)
            mapping.getPermission().add(permission);
      }
      return mapping;
   }

   /**
    * {@inheritDoc}
    */
   public void addType(CmisTypeDefinitionType type) throws StorageException, InvalidArgumentException,
      CmisRuntimeException
   {
      if (type.getBaseId() == null)
         throw new InvalidArgumentException("Base type id must be specified.");
      if (type.getParentId() == null)
         throw new InvalidArgumentException("Unable add root type. Parent type id must be specified");
      if (types.get(type.getId()) != null)
         throw new InvalidArgumentException("Type " + type.getId() + " already exists.");
      try
      {
         getTypeDefinition(type.getParentId());
      }
      catch (TypeNotFoundException e)
      {
         throw new InvalidArgumentException("Specified parent type " + type.getParentId() + " does not exists.");
      }
      // Check new type does not use known property IDs.
      for (CmisPropertyDefinitionType pd : type.getPropertyDefinition())
      {
         // TODO : can get smarter ??
         if (pd.getId() == null)
            throw new InvalidArgumentException("Properties ID not found.");
         for (EnumPropertiesBase i : EnumPropertiesBase.values())
            if (i.value().equals(pd.getId()))
               throw new InvalidArgumentException("Unable to use known properties ID " + pd.getId() + " in new type.");
         for (EnumPropertiesDocument i : EnumPropertiesDocument.values())
            if (i.value().equals(pd.getId()))
               throw new InvalidArgumentException("Unable to use known properties ID " + pd.getId() + " in new type.");
         for (EnumPropertiesFolder i : EnumPropertiesFolder.values())
            if (i.value().equals(pd.getId()))
               throw new InvalidArgumentException("Unable to use known properties ID " + pd.getId() + " in new type.");
         for (EnumPropertiesPolicy i : EnumPropertiesPolicy.values())
            if (i.value().equals(pd.getId()))
               throw new InvalidArgumentException("Unable to use known properties ID " + pd.getId() + " in new type.");
         for (EnumPropertiesRelationship i : EnumPropertiesRelationship.values())
            if (i.value().equals(pd.getId()))
               throw new InvalidArgumentException("Unable to use known properties ID " + pd.getId() + " in new type.");
      }

      List<CmisPropertyDefinitionType> list = new ArrayList<CmisPropertyDefinitionType>();
      commonsPropertyDefinitions(type.getBaseId(), list);
      list.addAll(type.getPropertyDefinition());
      propertyDefinitions.put(type.getId(), Collections.unmodifiableList(list));

      // NOTE: keeps property definitions in separate map.
      type.getPropertyDefinition().clear();
      types.put(type.getId(), type);
      getTypeChildren(type.getParentId()).add(type.getId());
   }

   /**
    * {@inheritDoc}
    */
   public CmisTypeDefinitionType getTypeDefinition(String typeId) throws TypeNotFoundException
   {
      return getTypeDefinition(typeId, true);
   }

   /**
    * {@inheritDoc}
    */
   public CmisTypeDefinitionType getTypeDefinition(String typeId, boolean includePropertyDefinition)
      throws TypeNotFoundException
   {
      CmisTypeDefinitionType type = types.get(typeId);
      if (type == null)
         throw new TypeNotFoundException("Type not found " + typeId);

      // Source types is untouchable.
      CmisTypeDefinitionType copy = getCopy(type);
      if (includePropertyDefinition)
         copy.getPropertyDefinition().addAll(propertyDefinitions.get(typeId));
      return copy;
   }

   /**
    * {@inheritDoc}
    */
   public void removeType(String typeId) throws TypeNotFoundException, RepositoryException
   {
      getTypeDefinition(typeId, false); // Throws exception if type is not exists.
      types.remove(typeId);
      propertyDefinitions.remove(typeId);
   }

   /**
    * Commons property definitions.
    * 
    * @param type the type
    * @param list the list
    */
   private void commonsPropertyDefinitions(EnumBaseObjectTypeIds type, List<CmisPropertyDefinitionType> list)
   {
      list.add(propertyDefinition(CMIS.BASE_TYPE_ID, EnumPropertyType.ID, CMIS.BASE_TYPE_ID, CMIS.BASE_TYPE_ID, null,
         CMIS.BASE_TYPE_ID, false, false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY,
         "Base type id.", null, null, null));
      list.add(propertyDefinition(CMIS.OBJECT_TYPE_ID, EnumPropertyType.ID, CMIS.OBJECT_TYPE_ID, CMIS.OBJECT_TYPE_ID,
         null, CMIS.OBJECT_TYPE_ID, false, false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY,
         "Object type id.", null, null, null));
      list.add(propertyDefinition(CMIS.OBJECT_ID, EnumPropertyType.ID, CMIS.OBJECT_ID, CMIS.OBJECT_ID, null,
         CMIS.OBJECT_ID, false, false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY, "Object id.",
         null, null, null));
      list.add(propertyDefinition(CMIS.NAME, EnumPropertyType.STRING, CMIS.NAME, CMIS.NAME, null, CMIS.NAME, true,
         false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READWRITE, "Object name.", true, null, null));
      list.add(propertyDefinition(CMIS.CREATED_BY, EnumPropertyType.STRING, CMIS.CREATED_BY, CMIS.CREATED_BY, null,
         CMIS.CREATED_BY, false, false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY,
         "User who created the object.", null, null, null));
      list.add(propertyDefinition(CMIS.CREATION_DATE, EnumPropertyType.DATETIME, CMIS.CREATION_DATE,
         CMIS.CREATION_DATE, null, CMIS.CREATION_DATE, false, false, false, false, EnumCardinality.SINGLE,
         EnumUpdatability.READONLY, "DateTime when the object was created.", null, null, null));
      list.add(propertyDefinition(CMIS.LAST_MODIFIED_BY, EnumPropertyType.STRING, CMIS.LAST_MODIFIED_BY,
         CMIS.LAST_MODIFIED_BY, null, CMIS.LAST_MODIFIED_BY, false, false, false, false, EnumCardinality.SINGLE,
         EnumUpdatability.READONLY, "User who last modified the object.", null, null, null));
      list.add(propertyDefinition(CMIS.LAST_MODIFICATION_DATE, EnumPropertyType.DATETIME, CMIS.LAST_MODIFICATION_DATE,
         CMIS.LAST_MODIFICATION_DATE, null, CMIS.LAST_MODIFICATION_DATE, false, false, false, false,
         EnumCardinality.SINGLE, EnumUpdatability.READONLY, "DateTime when the object was last modified.", null, null,
         null));
      list.add(propertyDefinition(CMIS.CHANGE_TOKEN, EnumPropertyType.STRING, CMIS.CHANGE_TOKEN, CMIS.CHANGE_TOKEN,
         null, CMIS.CHANGE_TOKEN, false, false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY,
         "Opaque token used for optimistic locking.", null, null, null));

      if (type == EnumBaseObjectTypeIds.CMIS_DOCUMENT)
      {
         list.add(propertyDefinition(CMIS.IS_IMMUTABLE, EnumPropertyType.BOOLEAN, CMIS.IS_IMMUTABLE, CMIS.IS_IMMUTABLE,
            null, CMIS.IS_IMMUTABLE, false, false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY,
            "TRUE if the repository MUST throw an error at any attempt to update or delete the object.", null, null,
            null));
         list.add(propertyDefinition(CMIS.IS_LATEST_VERSION, EnumPropertyType.BOOLEAN, CMIS.IS_LATEST_VERSION,
            CMIS.IS_LATEST_VERSION, null, CMIS.IS_LATEST_VERSION, false, false, false, false, EnumCardinality.SINGLE,
            EnumUpdatability.READONLY, "TRUE if object represents latest version of object.", null, null, null));
         list.add(propertyDefinition(CMIS.IS_MAJOR_VERSION, EnumPropertyType.BOOLEAN, CMIS.IS_MAJOR_VERSION,
            CMIS.IS_MAJOR_VERSION, null, CMIS.IS_MAJOR_VERSION, false, false, false, false, EnumCardinality.SINGLE,
            EnumUpdatability.READONLY, "TRUE if object represents major version of object.", null, null, null));
         list.add(propertyDefinition(CMIS.IS_LATEST_MAJOR_VERSION, EnumPropertyType.BOOLEAN,
            CMIS.IS_LATEST_MAJOR_VERSION, CMIS.IS_LATEST_MAJOR_VERSION, null, CMIS.IS_LATEST_MAJOR_VERSION, false,
            false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY,
            "TRUE if object represents latest major version of object.", null, null, null));
         list.add(propertyDefinition(CMIS.VERSION_LABEL, EnumPropertyType.STRING, CMIS.VERSION_LABEL,
            CMIS.VERSION_LABEL, null, CMIS.VERSION_LABEL, false, false, false, false, EnumCardinality.SINGLE,
            EnumUpdatability.READONLY, "Version label.", null, null, null));
         list.add(propertyDefinition(CMIS.VERSION_SERIES_ID, EnumPropertyType.ID, CMIS.VERSION_SERIES_ID,
            CMIS.VERSION_SERIES_ID, null, CMIS.VERSION_SERIES_ID, false, false, false, false, EnumCardinality.SINGLE,
            EnumUpdatability.READONLY, "ID of version series.", null, null, null));
         list.add(propertyDefinition(CMIS.IS_VERSION_SERIES_CHECKED_OUT, EnumPropertyType.BOOLEAN,
            CMIS.IS_VERSION_SERIES_CHECKED_OUT, CMIS.IS_VERSION_SERIES_CHECKED_OUT, null,
            CMIS.IS_VERSION_SERIES_CHECKED_OUT, false, false, false, false, EnumCardinality.SINGLE,
            EnumUpdatability.READONLY, "TRUE if some document in version series is checkedout.", null, null, null));
         list.add(propertyDefinition(CMIS.VERSION_SERIES_CHECKED_OUT_BY, EnumPropertyType.STRING,
            CMIS.VERSION_SERIES_CHECKED_OUT_BY, CMIS.VERSION_SERIES_CHECKED_OUT_BY, null,
            CMIS.VERSION_SERIES_CHECKED_OUT_BY, false, false, false, false, EnumCardinality.SINGLE,
            EnumUpdatability.READONLY, "User who checkedout document.", null, null, null));
         list.add(propertyDefinition(CMIS.VERSION_SERIES_CHECKED_OUT_ID, EnumPropertyType.ID,
            CMIS.VERSION_SERIES_CHECKED_OUT_ID, CMIS.VERSION_SERIES_CHECKED_OUT_ID, null,
            CMIS.VERSION_SERIES_CHECKED_OUT_ID, false, false, false, false, EnumCardinality.SINGLE,
            EnumUpdatability.READONLY, "ID of checkedout document.", null, null, null));
         list.add(propertyDefinition(CMIS.CHECKIN_COMMENT, EnumPropertyType.STRING, CMIS.CHECKIN_COMMENT,
            CMIS.CHECKIN_COMMENT, null, CMIS.CHECKIN_COMMENT, false, false, false, false, EnumCardinality.SINGLE,
            EnumUpdatability.READONLY, "Check-In comment.", null, null, null));
         list
            .add(propertyDefinition(CMIS.CONTENT_STREAM_LENGTH, EnumPropertyType.INTEGER, CMIS.CONTENT_STREAM_LENGTH,
               CMIS.CONTENT_STREAM_LENGTH, null, CMIS.CONTENT_STREAM_LENGTH, false, false, false, false,
               EnumCardinality.SINGLE, EnumUpdatability.READONLY, "Length of document content in bytes.", null, null,
               null));
         list.add(propertyDefinition(CMIS.CONTENT_STREAM_MIME_TYPE, EnumPropertyType.STRING,
            CMIS.CONTENT_STREAM_MIME_TYPE, CMIS.CONTENT_STREAM_MIME_TYPE, null, CMIS.CONTENT_STREAM_MIME_TYPE, false,
            false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY, "Media type of document content.",
            null, null, null));
         list.add(propertyDefinition(CMIS.CONTENT_STREAM_FILE_NAME, EnumPropertyType.STRING,
            CMIS.CONTENT_STREAM_FILE_NAME, CMIS.CONTENT_STREAM_FILE_NAME, null, CMIS.CONTENT_STREAM_FILE_NAME, false,
            false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY, "Document's content file name.",
            null, null, null));
         list.add(propertyDefinition(CMIS.CONTENT_STREAM_ID, EnumPropertyType.ID, CMIS.CONTENT_STREAM_ID,
            CMIS.CONTENT_STREAM_ID, null, CMIS.CONTENT_STREAM_ID, false, false, false, false, EnumCardinality.SINGLE,
            EnumUpdatability.READONLY, "Document's content stream ID.", null, null, null));
      }
      else if (type == EnumBaseObjectTypeIds.CMIS_FOLDER)
      {
         list.add(propertyDefinition(CMIS.PARENT_ID, EnumPropertyType.ID, CMIS.PARENT_ID, CMIS.PARENT_ID, null,
            CMIS.PARENT_ID, false, false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY,
            "ID of parent folder.", null, null, null));
         list.add(propertyDefinition(CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS, EnumPropertyType.ID,
            CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS, CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS, null,
            CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS, false, false, false, false, EnumCardinality.MULTI,
            EnumUpdatability.READONLY, "Set of allowed child types for folder.", null, null, null));
         list.add(propertyDefinition(CMIS.PATH, EnumPropertyType.STRING, CMIS.PATH, CMIS.PATH, null, CMIS.PATH, false,
            false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY, "Full path to folder object.",
            null, null, null));
      }
      else if (type == EnumBaseObjectTypeIds.CMIS_POLICY)
      {
         list.add(propertyDefinition(CMIS.POLICY_TEXT, EnumPropertyType.STRING, CMIS.POLICY_TEXT, CMIS.POLICY_TEXT,
            null, CMIS.POLICY_TEXT, false, false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY,
            "User-friendly description of the policy.", null, null, null));
      }
      else if (type == EnumBaseObjectTypeIds.CMIS_RELATIONSHIP)
      {
         list.add(propertyDefinition(CMIS.SOURCE_ID, EnumPropertyType.ID, CMIS.SOURCE_ID, CMIS.SOURCE_ID, null,
            CMIS.SOURCE_ID, false, false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY,
            "ID of relationship's source object.", null, null, null));
         list.add(propertyDefinition(CMIS.TARGET_ID, EnumPropertyType.ID, CMIS.TARGET_ID, CMIS.TARGET_ID, null,
            CMIS.TARGET_ID, false, false, false, false, EnumCardinality.SINGLE, EnumUpdatability.READONLY,
            "ID of relationship's target object.", null, null, null));
      }
   }

   /**
    * Property definition.
    * 
    * @param id the id
    * @param propertyType the property type
    * @param queryName the query name
    * @param localName the local name
    * @param localNamespace the local namespace
    * @param displayName the display name
    * @param required the required
    * @param queryable the queryable
    * @param orderable the orderable
    * @param inherited the inherited
    * @param cardinality the cardinality
    * @param updatability the updatability
    * @param description the description
    * @param openChoice the open choice
    * @param choices the choices
    * @param defValue the def value
    * 
    * @return the cmis property definition type
    */
   @SuppressWarnings("unchecked")
   private <T extends CmisChoice, V extends CmisProperty> CmisPropertyDefinitionType propertyDefinition(//
      String id, //
      EnumPropertyType propertyType, //
      String queryName, //
      String localName, //
      String localNamespace, //
      String displayName, //
      boolean required, //
      boolean queryable, //
      boolean orderable, //
      boolean inherited, //
      EnumCardinality cardinality, //
      EnumUpdatability updatability, // 
      String description, //
      Boolean openChoice, //
      List<T> choices, //
      V defValue //
   )
   {
      CmisPropertyDefinitionType def = null;
      // property type specific.
      switch (propertyType)
      {
         case BOOLEAN :
            CmisPropertyBooleanDefinitionType bool = new CmisPropertyBooleanDefinitionType();
            if (choices != null && choices.size() > 0)
            {
               bool.setOpenChoice(openChoice);
               bool.getChoice().addAll((List<CmisChoiceBoolean>)choices);
            }
            bool.setDefaultValue((CmisPropertyBoolean)defValue);
            def = bool;
            break;
         case DATETIME :
            CmisPropertyDateTimeDefinitionType date = new CmisPropertyDateTimeDefinitionType();
            if (choices != null && choices.size() > 0)
            {
               date.setOpenChoice(openChoice);
               date.getChoice().addAll((List<CmisChoiceDateTime>)choices);
            }
            date.setDefaultValue((CmisPropertyDateTime)defValue);
            date.setResolution(EnumDateTimeResolution.TIME);
            def = date;
            break;
         case DECIMAL :
            CmisPropertyDecimalDefinitionType dec = new CmisPropertyDecimalDefinitionType();
            if (choices != null && choices.size() > 0)
            {
               dec.setOpenChoice(openChoice);
               dec.getChoice().addAll((List<CmisChoiceDecimal>)choices);
            }
            dec.setDefaultValue((CmisPropertyDecimal)defValue);
            dec.setMaxValue(CMIS.MAX_DECIMAL_VALUE);
            dec.setMinValue(CMIS.MIN_DECIMAL_VALUE);
            dec.setPrecision(CMIS.PRECISION);
            def = dec;
            break;
         case HTML :
            CmisPropertyHtmlDefinitionType html = new CmisPropertyHtmlDefinitionType();
            if (choices != null && choices.size() > 0)
            {
               html.setOpenChoice(openChoice);
               html.getChoice().addAll((List<CmisChoiceHtml>)choices);
            }
            html.setDefaultValue((CmisPropertyHtml)defValue);
            def = html;
            break;
         case ID :
            CmisPropertyIdDefinitionType i = new CmisPropertyIdDefinitionType();
            if (choices != null && choices.size() > 0)
            {
               i.setOpenChoice(openChoice);
               i.getChoice().addAll((List<CmisChoiceId>)choices);
            }
            i.setDefaultValue((CmisPropertyId)defValue);
            def = i;
            break;
         case INTEGER :
            CmisPropertyIntegerDefinitionType integ = new CmisPropertyIntegerDefinitionType();
            if (choices != null && choices.size() > 0)
            {
               integ.setOpenChoice(openChoice);
               integ.getChoice().addAll((List<CmisChoiceInteger>)choices);
            }
            integ.setDefaultValue((CmisPropertyInteger)defValue);
            integ.setMaxValue(CMIS.MAX_INTEGER_VALUE);
            integ.setMinValue(CMIS.MIN_INTEGER_VALUE);
            def = integ;
            break;
         case STRING :
            CmisPropertyStringDefinitionType str = new CmisPropertyStringDefinitionType();
            if (choices != null && choices.size() > 0)
            {
               str.setOpenChoice(openChoice);
               str.getChoice().addAll((List<CmisChoiceString>)choices);
            }
            str.setDefaultValue((CmisPropertyString)defValue);
            str.setMaxLength(CMIS.MAX_STRING_LENGTH);
            def = str;
            break;
         case URI :
            CmisPropertyUriDefinitionType uri = new CmisPropertyUriDefinitionType();
            if (choices != null && choices.size() > 0)
            {
               uri.setOpenChoice(openChoice);
               uri.getChoice().addAll((List<CmisChoiceUri>)choices);
            }
            uri.setDefaultValue((CmisPropertyUri)defValue);
            def = uri;
            break;
      }
      // commons
      def.setCardinality(cardinality);
      def.setDescription(description);
      def.setDisplayName(displayName);
      def.setId(id);
      def.setInherited(inherited);
      def.setLocalName(localName);
      def.setLocalNamespace(localNamespace);
      def.setOrderable(orderable);
      def.setPropertyType(propertyType);
      def.setQueryable(queryable);
      def.setQueryName(queryName);
      def.setRequired(required);
      def.setUpdatability(updatability);
      return def;
   }

   /**
    * Init type manager.
    */
   protected void init()
   {
      CmisTypeDocumentDefinitionType docType = new CmisTypeDocumentDefinitionType();
      docType.setBaseId(EnumBaseObjectTypeIds.CMIS_DOCUMENT);
      docType.setContentStreamAllowed(EnumContentStreamAllowed.ALLOWED);
      docType.setControllableACL(true);
      docType.setControllablePolicy(true);
      docType.setCreatable(true);
      docType.setDescription("Cmis Document Type");
      docType.setDisplayName(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      docType.setFileable(true);
      docType.setFulltextIndexed(true);
      docType.setId(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      docType.setIncludedInSupertypeQuery(true);
      docType.setLocalName(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      docType.setParentId(null);
      docType.setQueryable(true);
      docType.setQueryName(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      docType.setVersionable(true);
      // Document's property definitions.
      List<CmisPropertyDefinitionType> docPropDef = new ArrayList<CmisPropertyDefinitionType>();
      commonsPropertyDefinitions(EnumBaseObjectTypeIds.CMIS_DOCUMENT, docPropDef);
      propertyDefinitions.put(docType.getId(), Collections.unmodifiableList(docPropDef));
      types.put(docType.getId(), docType);

      CmisTypeFolderDefinitionType folderType = new CmisTypeFolderDefinitionType();
      folderType.setBaseId(EnumBaseObjectTypeIds.CMIS_FOLDER);
      folderType.setControllableACL(true);
      folderType.setControllablePolicy(true);
      folderType.setCreatable(true);
      folderType.setDescription("Cmis Folder Type");
      folderType.setDisplayName(EnumBaseObjectTypeIds.CMIS_FOLDER.value());
      folderType.setFileable(true);
      folderType.setFulltextIndexed(false);
      folderType.setId(EnumBaseObjectTypeIds.CMIS_FOLDER.value());
      folderType.setIncludedInSupertypeQuery(true);
      folderType.setLocalName(EnumBaseObjectTypeIds.CMIS_FOLDER.value());
      folderType.setParentId(null);
      folderType.setQueryable(true);
      folderType.setQueryName(EnumBaseObjectTypeIds.CMIS_FOLDER.value());
      // Folder's property definitions.
      List<CmisPropertyDefinitionType> folderPropDef = new ArrayList<CmisPropertyDefinitionType>();
      commonsPropertyDefinitions(EnumBaseObjectTypeIds.CMIS_FOLDER, folderPropDef);
      propertyDefinitions.put(folderType.getId(), Collections.unmodifiableList(folderPropDef));
      types.put(folderType.getId(), folderType);

      CmisTypePolicyDefinitionType policyType = new CmisTypePolicyDefinitionType();
      policyType.setBaseId(EnumBaseObjectTypeIds.CMIS_POLICY);
      policyType.setControllableACL(true);
      policyType.setControllablePolicy(true);
      policyType.setCreatable(true);
      policyType.setDescription("Cmis Policy Type");
      policyType.setDisplayName(EnumBaseObjectTypeIds.CMIS_POLICY.value());
      policyType.setFileable(true);
      policyType.setFulltextIndexed(false);
      policyType.setId(EnumBaseObjectTypeIds.CMIS_POLICY.value());
      policyType.setIncludedInSupertypeQuery(true);
      policyType.setLocalName(EnumBaseObjectTypeIds.CMIS_POLICY.value());
      policyType.setParentId(null);
      policyType.setQueryable(false);
      policyType.setQueryName(EnumBaseObjectTypeIds.CMIS_POLICY.value());
      // Policy property definitions.
      List<CmisPropertyDefinitionType> policyPropDef = new ArrayList<CmisPropertyDefinitionType>();
      commonsPropertyDefinitions(EnumBaseObjectTypeIds.CMIS_POLICY, policyPropDef);
      propertyDefinitions.put(policyType.getId(), Collections.unmodifiableList(policyPropDef));
      types.put(policyType.getId(), policyType);

      CmisTypeRelationshipDefinitionType relationshipType = new CmisTypeRelationshipDefinitionType();
      relationshipType.setBaseId(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP);
      relationshipType.setControllableACL(false);
      relationshipType.setControllablePolicy(false);
      relationshipType.setCreatable(true);
      relationshipType.setDescription("Cmis Relationship Type");
      relationshipType.setDisplayName(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      relationshipType.setFileable(false);
      relationshipType.setFulltextIndexed(false);
      relationshipType.setId(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      relationshipType.setIncludedInSupertypeQuery(false);
      relationshipType.setLocalName(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      relationshipType.setParentId(null);
      relationshipType.setQueryable(false);
      relationshipType.setQueryName(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      // Relationship's property definitions.
      List<CmisPropertyDefinitionType> relationshipPropDef = new ArrayList<CmisPropertyDefinitionType>();
      commonsPropertyDefinitions(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP, relationshipPropDef);
      propertyDefinitions.put(relationshipType.getId(), Collections.unmodifiableList(relationshipPropDef));
      types.put(relationshipType.getId(), relationshipType);
   }

   private CmisTypeDefinitionType getCopy(CmisTypeDefinitionType orig)
   {
      CmisTypeDefinitionType copy = null;
      if (orig instanceof CmisTypeDocumentDefinitionType)
      {
         CmisTypeDocumentDefinitionType docDef = new CmisTypeDocumentDefinitionType();
         docDef.setVersionable(((CmisTypeDocumentDefinitionType)orig).isVersionable());
         docDef.setContentStreamAllowed(((CmisTypeDocumentDefinitionType)orig).getContentStreamAllowed());
         copy = docDef;
      }
      else if (orig instanceof CmisTypeFolderDefinitionType)
      {
         CmisTypeFolderDefinitionType folderDef = new CmisTypeFolderDefinitionType();
         copy = folderDef;
      }
      else if (orig instanceof CmisTypePolicyDefinitionType)
      {
         CmisTypePolicyDefinitionType policyDef = new CmisTypePolicyDefinitionType();
         copy = policyDef;
      }
      else if (orig instanceof CmisTypeRelationshipDefinitionType)
      {
         CmisTypeRelationshipDefinitionType relspDef = new CmisTypeRelationshipDefinitionType();
         relspDef.getAllowedSourceTypes().addAll(((CmisTypeRelationshipDefinitionType)orig).getAllowedSourceTypes());
         relspDef.getAllowedTargetTypes().addAll(((CmisTypeRelationshipDefinitionType)orig).getAllowedTargetTypes());
         copy = relspDef;
      }
      else
      {
         // Must never happen.
         copy = new CmisTypeDefinitionType();
      }

      copy.setId(orig.getId());
      copy.setLocalName(orig.getLocalName());
      copy.setLocalNamespace(orig.getLocalNamespace());
      copy.setDisplayName(orig.getDisplayName());
      copy.setQueryName(orig.getQueryName());
      copy.setDescription(orig.getDescription());
      copy.setBaseId(orig.getBaseId());
      copy.setParentId(orig.getParentId());
      copy.setCreatable(orig.isCreatable());
      copy.setFileable(orig.isFileable());
      copy.setQueryable(orig.isQueryable());
      copy.setFulltextIndexed(orig.isFulltextIndexed());
      copy.setIncludedInSupertypeQuery(orig.isIncludedInSupertypeQuery());
      copy.setControllablePolicy(orig.isControllablePolicy());
      copy.setControllableACL(orig.isControllableACL());
      copy.getAny().addAll(orig.getAny());
      return copy;
   }

   boolean hasChildren(String objectId)
   {
      return null != children.get(objectId) && children.get(objectId).size() > 0;
   }

   boolean hasParents(String objectId)
   {
      return null != parents.get(objectId) && parents.get(objectId).size() > 0;
   }

   boolean hasVersions(String versionSeriesId)
   {
      return null != versions.get(versionSeriesId) && versions.get(versionSeriesId).size() > 0;
   }

   Set<String> getChildren(String objectId)
   {
      Set<String> v = children.get(objectId);
      if (v == null)
      {
         v = new CopyOnWriteArraySet<String>();
         children.put(objectId, v);
      }
      return v;
   }

   Set<String> getParents(String objectId)
   {
      Set<String> v = parents.get(objectId);
      if (v == null)
      {
         v = new CopyOnWriteArraySet<String>();
         parents.put(objectId, v);
      }
      return v;
   }

   Set<String> removeChildren(String objectId)
   {
      return children.remove(objectId);
   }

   Set<String> removeParents(String objectId)
   {
      return parents.remove(objectId);
   }

   Set<String> removeVersions(String versionSeriesId)
   {
      return versions.remove(versionSeriesId);
   }

   Set<String> getVersions(String versionSeriesId)
   {
      Set<String> v = versions.get(versionSeriesId);
      if (v == null)
      {
         v = new CopyOnWriteArraySet<String>();
         versions.put(versionSeriesId, v);
      }
      return v;
   }

   String getPwC(String versionSeriesId)
   {
      return pwc.get(versionSeriesId);
   }

   void putPwC(String versionSeriesId, String pwcId)
   {
      pwc.put(versionSeriesId, pwcId);
   }

   void putContent(String newDocumentId, ContentStream content)
   {
      if (content == null)
         contents.remove(newDocumentId);
      else
         contents.put(newDocumentId, content);
   }

   void removeContent(String newDocumentId)
   {
      contents.remove(newDocumentId);
   }

   ContentStream getContent(String documentId)
   {
      return contents.get(documentId);
   }

   Set<String> getUnfiled()
   {
      return unfiled;
   }

   Set<String> getTypeChildren(String typeId)
   {
      Set<String> v = typeChildren.get(typeId);
      if (v == null)
      {
         v = new CopyOnWriteArraySet<String>();
         typeChildren.put(typeId, v);
      }
      return v;
   }
   
   boolean hasTypeChildren(String typeId)
   {
      return null != typeChildren.get(typeId) && typeChildren.get(typeId).size() > 0;
   }

}
