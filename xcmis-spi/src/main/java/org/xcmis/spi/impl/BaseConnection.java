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

package org.xcmis.spi.impl;

import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.CmisAllowableActionsType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisPropertyDecimal;
import org.xcmis.core.CmisPropertyDefinitionType;
import org.xcmis.core.CmisPropertyId;
import org.xcmis.core.CmisPropertyString;
import org.xcmis.core.CmisRenditionType;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.CmisTypeDocumentDefinitionType;
import org.xcmis.core.CmisTypeRelationshipDefinitionType;
import org.xcmis.core.EnumACLPropagation;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumCapabilityChanges;
import org.xcmis.core.EnumContentStreamAllowed;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.core.EnumRelationshipDirection;
import org.xcmis.core.EnumUnfileObject;
import org.xcmis.core.EnumUpdatability;
import org.xcmis.core.EnumVersioningState;
import org.xcmis.messaging.CmisTypeContainer;
import org.xcmis.messaging.CmisTypeDefinitionListType;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ChangeEvent;
import org.xcmis.spi.ChangeLogTokenHolder;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.Connection;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentAlreadyExistsException;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.NotSupportedException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.Storage;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.object.CmisObject;
import org.xcmis.spi.object.CmisObjectInFolder;
import org.xcmis.spi.object.CmisObjectInFolderContainer;
import org.xcmis.spi.object.CmisObjectInFolderList;
import org.xcmis.spi.object.CmisObjectList;
import org.xcmis.spi.object.CmisObjectParents;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.object.impl.CmisObjectImpl;
import org.xcmis.spi.object.impl.CmisObjectInFolderContainerImpl;
import org.xcmis.spi.object.impl.CmisObjectInFolderImpl;
import org.xcmis.spi.object.impl.CmisObjectInFolderListImpl;
import org.xcmis.spi.object.impl.CmisObjectListImpl;
import org.xcmis.spi.object.impl.CmisObjectParentsImpl;
import org.xcmis.spi.query.Query;
import org.xcmis.spi.query.Result;
import org.xcmis.spi.query.Score;
import org.xcmis.spi.utils.CmisUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public abstract class BaseConnection implements Connection
{

   protected final Storage storage;

   protected boolean connected;

   public BaseConnection(Storage storage)
   {
      this.storage = storage;
      this.connected = true;
   }

   // ------- Multi-filing/Unfiling -------
   
   /**
    * {@inheritDoc}
    */
   public void addObjectToFolder(String objectId, String folderId, boolean allVersions) throws ObjectNotFoundException,
      InvalidArgumentException, ConstraintException, CmisRuntimeException
   {
      if (!storage.getRepositoryInfo().getCapabilities().isCapabilityMultifiling())
         throw new NotSupportedException("Multi-filing is not supported.");
      ObjectData objectData = storage.getObject(objectId);
      if (objectData == null)
         throw new ObjectNotFoundException("Object " + objectId + " does not exists.");
      String typeId = objectData.getTypeId();
      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId, false);
      if (!typeDefinition.isFileable())
         throw new InvalidArgumentException("Object " + objectId + " is not fileable.");
      ObjectData folderData = storage.getObject(folderId);
      if (folderData == null)
         throw new ObjectNotFoundException("Folder " + folderId + " does not exists.");
      if (folderData.getBaseType() != EnumBaseObjectTypeIds.CMIS_FOLDER)
         throw new InvalidArgumentException("Object " + folderId + " is not Folder.");
      String[] allowedChildTypes = folderData.getIds(CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS);
      if (allowedChildTypes != null && allowedChildTypes.length > 0
         && !Arrays.asList(allowedChildTypes).contains(typeId))
         throw new ConstraintException("Type " + typeId + " is not allowed as child for " + folderData.getTypeId());
      storage.addObjectToFolder(objectData, folderData, allVersions);
   }

   /**
    * {@inheritDoc}
    */
   public void removeObjectFromFolder(String objectId, String folderId) throws ObjectNotFoundException,
      CmisRuntimeException
   {
      if (!storage.getRepositoryInfo().getCapabilities().isCapabilityUnfiling())
         throw new NotSupportedException("Unfiling is not supported.");
      ObjectData objectData = storage.getObject(objectId);
      if (objectData == null)
         throw new ObjectNotFoundException("Object " + objectId + " does not exists.");
      ObjectData folderData = storage.getObject(folderId);
      if (folderData == null)
         throw new ObjectNotFoundException("Folder " + folderId + " does not exists.");
      if (folderData.getBaseType() != EnumBaseObjectTypeIds.CMIS_FOLDER)
         throw new InvalidArgumentException("Object " + folderId + " is not Folder.");
      storage.removeObjectFromFolder(objectData, folderData);
   }
   
   // ------- ACL Services -------

   /**
    * {@inheritDoc}
    */
   public void applyAcl(String objectId, CmisAccessControlListType addAcl, CmisAccessControlListType removeAcl,
      EnumACLPropagation propagation) throws ObjectNotFoundException, ConstraintException, CmisRuntimeException
   {
      ObjectData objectData = storage.getObject(objectId);
      if (objectData == null)
         throw new ObjectNotFoundException("Object " + objectId + " does not exists.");
      String typeId = objectData.getTypeId();
      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId, false);
      if (!typeDefinition.isControllableACL())
         throw new ConstraintException("Type " + typeId + " is not controllable by ACL.");
      CmisAccessControlListType mergedAcls = CmisUtils.mergeAcls(objectData.getAcl(false), addAcl, removeAcl);
      objectData.setAcl(mergedAcls);
      try
      {
         storage.saveObject(objectData);
      }
      catch (StorageException e)
      {
         throw new CmisRuntimeException("Unable apply ACL. " + e.getMessage(), e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public CmisAccessControlListType getAcl(String objectId, boolean onlyBasicPermissions)
      throws ObjectNotFoundException, ConstraintException, CmisRuntimeException
   {
      ObjectData objectData = storage.getObject(objectId);
      if (objectData == null)
         throw new ObjectNotFoundException("Object " + objectId + " does not exists.");
      String typeId = objectData.getTypeId();
      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId, false);
      if (!typeDefinition.isControllableACL())
         throw new ConstraintException("Type " + typeId + " is not controllable by ACL.");
      return objectData.getAcl(onlyBasicPermissions);
   }
   
   // ------- Policy Services -------

   /**
    * {@inheritDoc}
    */
   public void applyPolicy(String policyId, String objectId) throws ConstraintException, ObjectNotFoundException,
      CmisRuntimeException
   {
      ObjectData objectData = storage.getObject(objectId);
      if (objectData == null)
         throw new ObjectNotFoundException("Object " + objectId + " does not exists.");
      String typeId = objectData.getTypeId();
      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId, false);
      if (!typeDefinition.isControllablePolicy())
         throw new ConstraintException("Type " + typeId + " is not controllable by Policy.");
      ObjectData policyData = storage.getObject(policyId);
      if (policyData == null)
         throw new ObjectNotFoundException("Policy " + policyId + " does not exists.");
      if (policyData.getBaseType() != EnumBaseObjectTypeIds.CMIS_POLICY)
         throw new InvalidArgumentException("Object " + policyId + " is not a Policy.");
      objectData.applyPolicy(policyData);
      try
      {
         storage.saveObject(objectData);
      }
      catch (StorageException e)
      {
         throw new CmisRuntimeException("Unable apply Policy. " + e.getMessage(), e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisObject> getAppliedPolicies(String objectId, String propertyFilter) throws ObjectNotFoundException,
      FilterNotValidException, CmisRuntimeException
   {
      ObjectData objectData = storage.getObject(objectId);
      if (objectData == null)
         throw new ObjectNotFoundException("Object " + objectId + " does not exists.");

      String typeId = objectData.getTypeId();
      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId, false);
      if (!typeDefinition.isControllablePolicy())
         throw new ConstraintException("Type " + typeId + " is not controllable by Policy.");

      PropertyFilter parsedFilter = new PropertyFilter(propertyFilter);
      Collection<ObjectData> policyDatas = objectData.getPolicies();
      List<CmisObject> policies = new ArrayList<CmisObject>(policyDatas.size());
      for (ObjectData policyData : policyDatas)
      {
         CmisObject cmisPolicy =
            getCmisObject(policyData, false, EnumIncludeRelationships.NONE, false, false, parsedFilter,
               RenditionFilter.NONE);
         policies.add(cmisPolicy);
      }
      return policies;
   }

   /**
    * {@inheritDoc}
    */
   public void removePolicy(String policyId, String objectId) throws ConstraintException, ObjectNotFoundException,
      CmisRuntimeException
   {
      ObjectData objectData = storage.getObject(objectId);
      if (objectData == null)
         throw new ObjectNotFoundException("Object " + objectId + " does not exists.");
      String typeId = objectData.getTypeId();
      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId, false);
      if (!typeDefinition.isControllablePolicy())
         throw new ConstraintException("Type " + typeId + " is not controllable by Policy.");
      ObjectData policyData = storage.getObject(policyId);
      if (policyData == null)
         throw new ObjectNotFoundException("Policy object " + policyId + " does not exists.");
      objectData.removePolicy(policyData);
      try
      {
         storage.saveObject(objectData);
      }
      catch (StorageException e)
      {
         throw new CmisRuntimeException("Unable remove Policy. " + e.getMessage(), e);
      }
   }
   
   // -------

   /**
    * {@inheritDoc}
    */
   public CmisObject createDocument(String folderId, CmisPropertiesType properties, ContentStream content,
      CmisAccessControlListType addAcl, CmisAccessControlListType removeAcl, List<String> policies,
      EnumVersioningState versioningState) throws ObjectNotFoundException, ConstraintException,
      InvalidArgumentException, StreamNotSupportedException, NameConstraintViolationException, IOException,
      StorageException, CmisRuntimeException
   {
      String typeId = getTypeId(properties);
      if (typeId == null)
         throw new InvalidArgumentException("Type is not specified.");
      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId);

      if (EnumBaseObjectTypeIds.CMIS_DOCUMENT != typeDefinition.getBaseId())
         throw new ConstraintException("The typeId " + typeId
            + " represents object-type whose baseType is not a Document.");

      ObjectData folderData = null;
      if (folderId != null)
      {
         folderData = storage.getObject(folderId);
         if (folderData == null)
            throw new ObjectNotFoundException("Folder " + folderId + " does not exists.");
         if (folderData.getBaseType() != EnumBaseObjectTypeIds.CMIS_FOLDER)
            throw new InvalidArgumentException("Object " + folderId + " is not a Folder.");
         String[] allowedChildTypes = folderData.getIds(CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS);
         if (allowedChildTypes != null && allowedChildTypes.length > 0
            && !Arrays.asList(allowedChildTypes).contains(typeId))
            throw new ConstraintException("Type " + typeId + " is not allowed as child for " + folderData.getTypeId());
      }
      else
      {
         if (!storage.getRepositoryInfo().getCapabilities().isCapabilityUnfiling())
            throw new ConstraintException("Unfiling capability is not supported, parent folder must be provided.");
      }

      if (!typeDefinition.isControllableACL()
         && (((addAcl != null && addAcl.getPermission().size() > 0) || (addAcl != null && addAcl.getPermission().size() > 0))))
         throw new ConstraintException("Type " + typeId + " is not controllable by ACL but at least one ACL provided.");

      if (!typeDefinition.isControllablePolicy() && policies != null && policies.size() > 0)
         throw new ConstraintException("Type " + typeId
            + " is not controllable by Policy but at least one Policy provided.");

      EnumContentStreamAllowed contentStreamAllowed =
         ((CmisTypeDocumentDefinitionType)typeDefinition).getContentStreamAllowed();
      if (contentStreamAllowed == EnumContentStreamAllowed.NOTALLOWED)
      {
         if (content != null)
            throw new StreamNotSupportedException("Content is not allowed for type " + typeId);
      }
      else if (contentStreamAllowed == EnumContentStreamAllowed.REQUIRED)
      {
         if (content == null)
            throw new ConstraintException("Content required for type " + typeId + " but it is null.");
      }

      if (versioningState == null)
         versioningState = EnumVersioningState.MAJOR;
      boolean versionable = ((CmisTypeDocumentDefinitionType)typeDefinition).isVersionable();
      if (!versionable)
      {
         if (EnumVersioningState.NONE != versioningState)
            throw new ConstraintException("Type " + typeId + " is not versionable. Versionig state " + versioningState
               + " is not allowed.");
      }
      else
      {
         if (EnumVersioningState.NONE == versioningState)
            throw new ConstraintException("Type " + typeId + " is versionable. Versionig state " + versioningState
               + " is not allowed.");
      }

      ObjectData newDocument =
         storage.createDocument(folderData, typeDefinition, properties, content, addAcl, removeAcl, policies,
            versioningState);
      storage.saveObject(newDocument);
      CmisObject cmisDocument =
         getCmisObject(newDocument, false, EnumIncludeRelationships.NONE, false, true, PropertyFilter.ALL,
            RenditionFilter.NONE);
      return cmisDocument;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject createDocumentFromSource(String sourceId, String folderId, CmisPropertiesType properties,
      CmisAccessControlListType addAcl, CmisAccessControlListType removeAcl, List<String> policies,
      EnumVersioningState versioningState) throws ObjectNotFoundException, ConstraintException,
      InvalidArgumentException, NameConstraintViolationException, StorageException, CmisRuntimeException
   {
      ObjectData sourceData = storage.getObject(sourceId);

      if (sourceData == null)
         throw new ObjectNotFoundException("Source object " + sourceId + " does not exists.");
      if (sourceData.getBaseType() != EnumBaseObjectTypeIds.CMIS_DOCUMENT)
         throw new ConstraintException("Source object is not Document.");

      String typeId = sourceData.getTypeId();
      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId);

      ObjectData folderData = null;
      if (folderId != null)
      {
         folderData = storage.getObject(folderId);
         if (folderData == null)
            throw new ObjectNotFoundException("Folder " + folderId + " does not exists.");
         if (folderData.getBaseType() != EnumBaseObjectTypeIds.CMIS_FOLDER)
            throw new InvalidArgumentException("Object " + folderId + " is not a Folder.");
         String[] allowedChildTypes = folderData.getIds(CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS);
         if (allowedChildTypes != null && allowedChildTypes.length > 0
            && !Arrays.asList(allowedChildTypes).contains(typeId))
            throw new ConstraintException("Type " + typeId + " is not allowed as child for " + folderData.getTypeId());
      }
      else
      {
         if (!storage.getRepositoryInfo().getCapabilities().isCapabilityUnfiling())
            throw new ConstraintException("Unfiling capability is not supported, parent folder must be provided.");
      }

      if (!typeDefinition.isControllableACL()
         && (((addAcl != null && addAcl.getPermission().size() > 0) || (addAcl != null && addAcl.getPermission().size() > 0))))
         throw new ConstraintException("Type " + typeId + " is not controllable by ACL but at least one ACL provided.");

      if (!typeDefinition.isControllablePolicy() && policies != null && policies.size() > 0)
         throw new ConstraintException("Type " + typeId
            + " is not controllable by Policy but at least one Policy provided.");

      if (versioningState == null)
         versioningState = EnumVersioningState.MAJOR;
      boolean versionable = ((CmisTypeDocumentDefinitionType)typeDefinition).isVersionable();
      if (!versionable)
      {
         if (EnumVersioningState.NONE != versioningState)
            throw new ConstraintException("Type " + typeId + " is not versionable. Versionig state " + versioningState
               + " is not allowed.");
      }
      else
      {
         if (EnumVersioningState.NONE == versioningState)
            throw new ConstraintException("Type " + typeId + " is versionable. Versionig state " + versioningState
               + " is not allowed.");
      }

      ObjectData newDocument =
         storage.createDocumentFromSource(sourceData, folderData, properties, addAcl, removeAcl, policies,
            versioningState);
      storage.saveObject(newDocument);
      CmisObject cmisDocument =
         getCmisObject(newDocument, false, EnumIncludeRelationships.NONE, false, true, PropertyFilter.ALL,
            RenditionFilter.NONE);
      return cmisDocument;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject createFolder(String folderId, CmisPropertiesType properties, CmisAccessControlListType addAcl,
      CmisAccessControlListType removeAcl, List<String> policies) throws ObjectNotFoundException, ConstraintException,
      InvalidArgumentException, NameConstraintViolationException, StorageException, CmisRuntimeException
   {
      String typeId = getTypeId(properties);
      if (typeId == null)
         throw new InvalidArgumentException("Type is not specified.");

      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId);

      if (EnumBaseObjectTypeIds.CMIS_FOLDER != typeDefinition.getBaseId())
         throw new ConstraintException("The typeId " + typeId
            + " represents object-type whose baseType is not a Folder.");

      if (!typeDefinition.isControllableACL()
         && (((addAcl != null && addAcl.getPermission().size() > 0) || (addAcl != null && addAcl.getPermission().size() > 0))))
         throw new ConstraintException("Type " + typeId + " is not controllable by ACL but at least one ACL provided.");

      if (!typeDefinition.isControllablePolicy() && policies != null && policies.size() > 0)
         throw new ConstraintException("Type " + typeId
            + " is not controllable by Policy but at least one Policy provided.");

      if (folderId == null)
         throw new ConstraintException("Parent folder id is not provided.");

      ObjectData folderData = storage.getObject(folderId);
      if (folderData == null)
         throw new ObjectNotFoundException("Folder " + folderId + " does not exists.");
      if (folderData.getBaseType() != EnumBaseObjectTypeIds.CMIS_FOLDER)
         throw new InvalidArgumentException("Object " + folderId + " is not a Folder.");

      String[] allowedChildTypes = folderData.getIds(CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS);
      if (allowedChildTypes != null && allowedChildTypes.length > 0
         && !Arrays.asList(allowedChildTypes).contains(typeId))
         throw new ConstraintException("Type " + typeId + " is not allowed as child for " + folderData.getTypeId());

      ObjectData newFolder = storage.createFolder(folderData, typeDefinition, properties, addAcl, removeAcl, policies);
      storage.saveObject(newFolder);
      CmisObject cmisFolder =
         getCmisObject(newFolder, false, EnumIncludeRelationships.NONE, false, true, PropertyFilter.ALL,
            RenditionFilter.NONE);
      return cmisFolder;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject createPolicy(String folderId, CmisPropertiesType properties, CmisAccessControlListType addAcl,
      CmisAccessControlListType removeAcl, List<String> policies) throws ObjectNotFoundException, ConstraintException,
      InvalidArgumentException, NameConstraintViolationException, StorageException, CmisRuntimeException
   {
      String typeId = getTypeId(properties);
      if (typeId == null)
         throw new InvalidArgumentException("Type is not specified.");

      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId);

      if (EnumBaseObjectTypeIds.CMIS_POLICY != typeDefinition.getBaseId())
         throw new ConstraintException("The typeId " + typeId
            + " represents object-type whose baseType is not a Policy.");

      ObjectData folderData = null;
      if (folderId != null)
      {
         folderData = storage.getObject(folderId);
         if (folderData == null)
            throw new ObjectNotFoundException("Folder object " + folderId + " does not exists.");
         if (folderData.getBaseType() != EnumBaseObjectTypeIds.CMIS_FOLDER)
            throw new InvalidArgumentException("Object " + folderId + " is not a Folder.");
         String[] allowedChildTypes = folderData.getIds(CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS);
         if (allowedChildTypes != null && allowedChildTypes.length > 0
            && !Arrays.asList(allowedChildTypes).contains(typeId))
            throw new ConstraintException("Type " + typeId + " is not allowed as child for " + folderData.getTypeId());
      }
      else
      {
         if (!storage.getRepositoryInfo().getCapabilities().isCapabilityUnfiling())
            throw new ConstraintException("Unfiling capability is not supported, parent folder must be provided.");
      }

      if (!typeDefinition.isControllableACL()
         && (((addAcl != null && addAcl.getPermission().size() > 0) || (addAcl != null && addAcl.getPermission().size() > 0))))
         throw new ConstraintException("Type " + typeId + " is not controllable by ACL but at least one ACL provided.");

      if (!typeDefinition.isControllablePolicy() && policies != null && policies.size() > 0)
         throw new ConstraintException("Type " + typeId
            + " is not controllable by Policy but at least one Policy provided.");

      ObjectData newPolicy = storage.createPolicy(folderData, typeDefinition, properties, addAcl, removeAcl, policies);
      storage.saveObject(newPolicy);
      CmisObject cmisPolicy =
         getCmisObject(newPolicy, false, EnumIncludeRelationships.NONE, false, false, PropertyFilter.ALL,
            RenditionFilter.NONE);
      return cmisPolicy;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject createRelationship(CmisPropertiesType properties, CmisAccessControlListType addAcl,
      CmisAccessControlListType removeAcl, List<String> policies) throws ObjectNotFoundException, ConstraintException,
      NameConstraintViolationException, StorageException, CmisRuntimeException
   {
      String typeId = getTypeId(properties);
      if (typeId == null)
         throw new InvalidArgumentException("Type is not specified.");

      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId);

      if (EnumBaseObjectTypeIds.CMIS_RELATIONSHIP != typeDefinition.getBaseId())
         throw new ConstraintException("The typeId " + typeId
            + " represents object-type whose baseType is not a Relationship.");

      String sourceId = getSourceId(properties);
      if (sourceId == null)
         throw new InvalidArgumentException("Required property 'cmis:sourceId' is not specified.");

      String targetId = getTargetId(properties);
      if (targetId == null)
         throw new InvalidArgumentException("Required property 'cmis:targetId' is not specified.");

      ObjectData sourceData = storage.getObject(sourceId);
      if (sourceData == null)
         throw new ObjectNotFoundException("Source object " + sourceId + " does not exists.");
      String sourceTypeId = sourceData.getTypeId();
      if (!(sourceData.getBaseType() == EnumBaseObjectTypeIds.CMIS_DOCUMENT
         || sourceData.getBaseType() == EnumBaseObjectTypeIds.CMIS_FOLDER || sourceData.getBaseType() == EnumBaseObjectTypeIds.CMIS_POLICY))
         throw new InvalidArgumentException("Object with id: " + sourceId + " and type: " + sourceTypeId
            + " is not independent object and may not be used as 'source' of relationship");
      ObjectData targetData = storage.getObject(targetId);
      if (targetData == null)
         throw new ObjectNotFoundException("Target object " + targetId + " does not exists.");
      String targetTypeId = targetData.getTypeId();
      if (!(targetData.getBaseType() == EnumBaseObjectTypeIds.CMIS_DOCUMENT
         || targetData.getBaseType() == EnumBaseObjectTypeIds.CMIS_FOLDER || targetData.getBaseType() == EnumBaseObjectTypeIds.CMIS_POLICY))
         throw new InvalidArgumentException("Object with id: " + targetId + " and type: " + targetTypeId
            + " is not independent object and may not be used as 'target' of relationship");

      List<String> allowedSourceTypes = ((CmisTypeRelationshipDefinitionType)typeDefinition).getAllowedSourceTypes();
      if (allowedSourceTypes != null && allowedSourceTypes.size() > 0 && !allowedSourceTypes.contains(sourceTypeId))
         throw new ConstraintException("Type " + sourceTypeId + " is not allowed as source for relationship " + typeId);

      List<String> allowedTargetTypes = ((CmisTypeRelationshipDefinitionType)typeDefinition).getAllowedTargetTypes();
      if (allowedTargetTypes != null && allowedTargetTypes.size() > 0 && !allowedTargetTypes.contains(sourceTypeId))
         throw new ConstraintException("Type " + targetTypeId + " is not allowed as target for relationship " + typeId);

      if (!typeDefinition.isControllableACL()
         && (((addAcl != null && addAcl.getPermission().size() > 0) || (addAcl != null && addAcl.getPermission().size() > 0))))
         throw new ConstraintException("Type " + typeId + " is not controllable by ACL but at least one ACL provided.");

      if (!typeDefinition.isControllablePolicy() && policies != null && policies.size() > 0)
         throw new ConstraintException("Type " + typeId
            + " is not controllable by Policy but at least one Policy provided.");

      ObjectData newRelationship =
         storage.createRelationship(typeDefinition, sourceData, targetData, properties, addAcl, removeAcl, policies);
      storage.saveObject(newRelationship);
      CmisObject cmisRelationship =
         getCmisObject(newRelationship, false, EnumIncludeRelationships.NONE, false, false, PropertyFilter.ALL,
            RenditionFilter.NONE);
      return cmisRelationship;
   }

   /**
    * {@inheritDoc}
    */
   public void deleteContentStream(String documentId, String changeToken) throws ObjectNotFoundException,
      ConstraintException, UpdateConflictException, StorageException, CmisRuntimeException
   {
      ObjectData documentData = storage.getObject(documentId);
      if (documentData == null)
         throw new ObjectNotFoundException("Document object " + documentId + " does not exists.");
      if (documentData.getBaseType() != EnumBaseObjectTypeIds.CMIS_DOCUMENT)
         throw new InvalidArgumentException("Object " + documentId + " is not Document.");

      validateChangeToken(documentData, changeToken);
      String typeId = documentData.getTypeId();
      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId, false);

      EnumContentStreamAllowed contentStreamAllowed =
         ((CmisTypeDocumentDefinitionType)typeDefinition).getContentStreamAllowed();
      if (contentStreamAllowed == EnumContentStreamAllowed.REQUIRED)
         throw new ConstraintException("Content required for type " + typeId + " and can't be removed.");

      storage.deleteContentStream(documentData);
      storage.saveObject(documentData);
   }

   /**
    * {@inheritDoc}
    */
   public ContentStream getContentStream(String objectId, String streamId, long offset, long length)
      throws ObjectNotFoundException, ConstraintException, CmisRuntimeException
   {
      ObjectData objectData = storage.getObject(objectId);
      if (objectData == null)
         throw new ObjectNotFoundException("Object " + objectId + " does not exists.");
      if (objectData.getBaseType() == EnumBaseObjectTypeIds.CMIS_FOLDER && streamId == null)
      {
         // May be rendition stream only.
         throw new ConstraintException("streamId is not specified.");
      }
      return storage.getContentStream(objectData, streamId, offset, length);
   }

   /**
    * {@inheritDoc}
    */
   public void deleteObject(String objectId, boolean deleteAllVersions) throws ObjectNotFoundException,
      ConstraintException, UpdateConflictException, StorageException, CmisRuntimeException
   {
      ObjectData objectData = storage.getObject(objectId);
      if (objectData == null)
         throw new ObjectNotFoundException("Object " + objectId + " does not exists.");
      if (objectData.getBaseType() == EnumBaseObjectTypeIds.CMIS_FOLDER)
      {
         if (storage.getRepositoryInfo().getRootFolderId().equals(objectId))
            throw new ConstraintException("Root folder can't be removed.");
         if (storage.getChildren(objectData, null).size() > 0)
            throw new ConstraintException("Failed delete object. Object " + objectId
               + " is Folder and contains one or more objects.");
      }
      storage.deleteObject(objectData, deleteAllVersions);
   }

   /**
    * {@inheritDoc}
    */
   public List<String> deleteTree(String folderId, boolean deleteAllVersions, EnumUnfileObject unfileObject,
      boolean continueOnFailure) throws ObjectNotFoundException, UpdateConflictException, StorageException,
      CmisRuntimeException
   {
      ObjectData folderData = storage.getObject(folderId);
      if (folderData == null)
         throw new ObjectNotFoundException("Folder object " + folderId + " does not exists.");
      if (folderData.getBaseType() != EnumBaseObjectTypeIds.CMIS_FOLDER)
         throw new ConstraintException("Failed delete tree. Object " + folderId + " is not a Folder.");
      if (storage.getRepositoryInfo().getRootFolderId().equals(folderId))
         throw new ConstraintException("Root folder can't be removed.");

      if (unfileObject == null)
         unfileObject = EnumUnfileObject.DELETE; // Default value.

      // TODO : need to check unfiling capability if 'unfileObject' is other then delete ??
      Collection<ObjectData> failedDelete =
         storage.deleteTree(folderData, deleteAllVersions, unfileObject, continueOnFailure);
      List<String> failedIds = new ArrayList<String>(failedDelete.size());
      for (ObjectData object : failedDelete)
         failedIds.add(object.getObjectId());
      return failedIds;
   }

   /**
    * {@inheritDoc}
    */
   public CmisAllowableActionsType getAllowableActions(String objectId) throws ObjectNotFoundException,
      CmisRuntimeException
   {
      ObjectData objectData = storage.getObject(objectId);
      if (objectData == null)
         throw new ObjectNotFoundException("Object " + objectId + " does not exists.");
      return storage.calculateAllowableActions(objectData);
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject getObject(String objectId, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, boolean includePolicyIDs, boolean includeAcl,
      String propertyFilter, String renditionFilter) throws ObjectNotFoundException, FilterNotValidException,
      CmisRuntimeException
   {
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);
      if (includeRelationships == null)
         includeRelationships = EnumIncludeRelationships.NONE;

      ObjectData objectData = storage.getObject(objectId);
      if (objectData == null)
         throw new ObjectNotFoundException("Object " + objectId + " does not exists.");
      CmisObject cmisObject =
         getCmisObject(objectData, includeAllowableActions, includeRelationships, includePolicyIDs, includeAcl,
            parsedPropertyFilter, parsedRenditionFilter);
      return cmisObject;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject getObjectByPath(String path, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, boolean includePolicyIDs, boolean includeAcl,
      String propertyFilter, String renditionFilter) throws ObjectNotFoundException, FilterNotValidException,
      CmisRuntimeException
   {
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);
      if (includeRelationships == null)
         includeRelationships = EnumIncludeRelationships.NONE;
      ObjectData objectData = storage.getObjectByPath(path);
      if (objectData == null)
         throw new ObjectNotFoundException("Object " + path + " does not exists.");
      CmisObject cmisObject =
         getCmisObject(objectData, includeAllowableActions, includeRelationships, includePolicyIDs, includeAcl,
            parsedPropertyFilter, parsedRenditionFilter);
      return cmisObject;
   }

   /**
    * {@inheritDoc}
    */
   public CmisPropertiesType getProperties(String objectId, String propertyFilter) throws ObjectNotFoundException,
      FilterNotValidException, CmisRuntimeException
   {
      ObjectData objectData = storage.getObject(objectId);
      if (objectData == null)
         throw new ObjectNotFoundException("Object " + objectId + " does not exists.");
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      return getProperties(objectData, parsedPropertyFilter);
   }

   /**
    * {@inheritDoc}
    * 
    * @return
    */
   public CmisObject moveObject(String objectId, String targetFolderId, String sourceFolderId)
      throws ObjectNotFoundException, ConstraintException, InvalidArgumentException, UpdateConflictException,
      StorageException, CmisRuntimeException
   {
      if (sourceFolderId == null)
         throw new InvalidArgumentException("sourceFolderId is not specified.");

      ObjectData objectData = storage.getObject(objectId);
      if (objectData == null)
         throw new ObjectNotFoundException("Object " + objectId + " does not exists.");
      ObjectData targetData = storage.getObject(targetFolderId);
      if (targetData == null)
         throw new ObjectNotFoundException("Object " + targetFolderId + " does not exists.");
      ObjectData sourceData = storage.getObject(sourceFolderId);
      if (sourceData == null)
         throw new ObjectNotFoundException("Object " + sourceFolderId + " does not exists.");

      if (targetData.getBaseType() != EnumBaseObjectTypeIds.CMIS_FOLDER)
         throw new InvalidArgumentException("Object " + targetFolderId + " is not a Folder.");
      if (sourceData.getBaseType() != EnumBaseObjectTypeIds.CMIS_FOLDER)
         throw new InvalidArgumentException("Object " + sourceFolderId + " is not a Folder.");

      String objectTypeId = objectData.getTypeId();
      String[] allowedChildTypes = targetData.getIds(CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS);
      if (allowedChildTypes != null && allowedChildTypes.length > 0
         && !Arrays.asList(allowedChildTypes).contains(objectTypeId))
         throw new ConstraintException("Type " + objectTypeId + " is not allowed as child for "
            + targetData.getTypeId());

      storage.moveObject(objectData, targetData, sourceData);
      CmisObject movedObject =
         getCmisObject(objectData, false, EnumIncludeRelationships.NONE, false, false, PropertyFilter.ALL,
            RenditionFilter.NONE);
      return movedObject;
   }

   /**
    * {@inheritDoc}
    */
   public void setContentStream(String documentId, ContentStream content, String changeToken, boolean overwriteFlag)
      throws ObjectNotFoundException, ContentAlreadyExistsException, StreamNotSupportedException,
      UpdateConflictException, IOException, StorageException, CmisRuntimeException
   {
      ObjectData documentData = storage.getObject(documentId);
      if (documentData == null)
         throw new ObjectNotFoundException("Object " + documentId + " does not exists.");

      if (documentData.getBaseType() != EnumBaseObjectTypeIds.CMIS_DOCUMENT)
         throw new InvalidArgumentException("Object " + documentId + " is not a Document.");

      validateChangeToken(documentData, changeToken);
      String typeId = documentData.getTypeId();
      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId, false);
      EnumContentStreamAllowed contentStreamAllowed =
         ((CmisTypeDocumentDefinitionType)typeDefinition).getContentStreamAllowed();
      if (contentStreamAllowed == EnumContentStreamAllowed.NOTALLOWED)
         throw new StreamNotSupportedException("Content is not allowed for type " + typeId);

      if (!overwriteFlag && storage.hasContent(documentData))
         throw new ContentAlreadyExistsException("Document already has content stream and 'overwriteFlag' is false.");

      storage.setContentStream(documentData, content);
      storage.saveObject(documentData);
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject updateProperties(String objectId, String changeToken, CmisPropertiesType properties)
      throws ObjectNotFoundException, ConstraintException, NameConstraintViolationException, UpdateConflictException,
      StorageException, CmisRuntimeException
   {
      ObjectData objectData = storage.getObject(objectId);
      if (objectData == null)
         throw new ObjectNotFoundException("Object " + objectId + " does not exists.");
      validateChangeToken(objectData, changeToken);
      if (properties != null)
      {
         List<CmisPropertyDefinitionType> propertyDefinitions =
            getTypeDefinition(objectData.getTypeId(), true).getPropertyDefinition();
         for (CmisProperty property : properties.getProperty())
         {
            CmisPropertyDefinitionType def =
               getPropertyDefinition(propertyDefinitions, property.getPropertyDefinitionId());
            if (def.getUpdatability() == EnumUpdatability.READWRITE)
               // TODO : check for required 
               objectData.setProperty(property);
         }
         storage.saveObject(objectData);
      }
      return getCmisObject(objectData, false, EnumIncludeRelationships.NONE, false, false, PropertyFilter.ALL,
         RenditionFilter.NONE);
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisRenditionType> getRenditions(String objectId, String renditionFilter, int maxItems, int skipCount)
      throws ObjectNotFoundException, FilterNotValidException, CmisRuntimeException
   {
      if (skipCount < 0)
         throw new InvalidArgumentException("skipCount parameter is negative.");
      ObjectData objectData = storage.getObject(objectId);
      if (objectData == null)
         throw new ObjectNotFoundException("Object " + objectId + " does not exists.");
      ItemsIterator<CmisRenditionType> iterator = storage.getRenditions(objectData);
      try
      {
         if (skipCount > 0)
            iterator.skip(skipCount);
      }
      catch (NoSuchElementException nse)
      {
         throw new InvalidArgumentException("skipCount parameter is greater then total number of argument");
      }
      List<CmisRenditionType> renditions = new ArrayList<CmisRenditionType>();
      int count = 0;
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);
      while (iterator.hasNext() && (maxItems < 0 || count < maxItems))
      {
         CmisRenditionType r = iterator.next();
         if (parsedRenditionFilter.accept(r))
            renditions.add(r);
      }
      return renditions;
   }

   // ------- Versioning Services -------
   
   /**
    * {@inheritDoc}
    */
   public List<CmisObject> getAllVersions(String versionSeriesId, boolean includeAllowableActions, String propertyFilter)
      throws ObjectNotFoundException, FilterNotValidException, CmisRuntimeException
   {
      Collection<ObjectData> versionDatas = storage.getVersions(versionSeriesId);
      if (versionDatas == null)
         throw new ObjectNotFoundException("Version series " + versionSeriesId + " does not exists.");
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      List<CmisObject> versions = new ArrayList<CmisObject>();
      for (ObjectData objectData : versionDatas)
      {
         versions.add(getCmisObject(objectData, includeAllowableActions, EnumIncludeRelationships.NONE, false, false,
            parsedPropertyFilter, RenditionFilter.NONE));
      }
      return versions;
   }

   /**
    * {@inheritDoc}
    */
   public void cancelCheckout(String documentId) throws ConstraintException, UpdateConflictException,
      VersioningException, StorageException, CmisRuntimeException
   {
      ObjectData document = storage.getObject(documentId);
      if (!document.isVersionSeriesCheckedOut())
         return; // No PWC.
      // cancelCheckedOut may be invoked on any object in version series. In other way 
      // 'cmis:versionSeriesCheckedOutId' may not reflect current PWC id. 
      String versionSeriesId = document.getVersionSeriesId();
      // Than assume via version series should be able to cancel checkout even if 
      // 'cmis:versionSeriesCheckedOutId' for all document in version series is not 
      // specified.
      storage.cancelCheckout(versionSeriesId);
   }

   public CmisObject checkin(String documentId, boolean major, CmisPropertiesType properties, ContentStream content,
      String checkinComment, CmisAccessControlListType addACL, CmisAccessControlListType removeACL,
      List<String> policies) throws ConstraintException, UpdateConflictException, StreamNotSupportedException,
      IOException, StorageException
   {
      ObjectData pwcData = storage.getObject(documentId);
      if (pwcData == null)
         throw new ObjectNotFoundException("Document " + documentId + " does not exists.");
      ObjectData version =
         storage.checkin(pwcData, major, properties, content, checkinComment, addACL, removeACL, policies);
      return getCmisObject(version, false, EnumIncludeRelationships.NONE, false, false, PropertyFilter.ALL,
         RenditionFilter.NONE);
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject checkout(String documentId) throws ConstraintException, UpdateConflictException,
      VersioningException, StorageException, CmisRuntimeException
   {
      ObjectData documentData = storage.getObject(documentId);
      if (documentData == null)
         throw new ObjectNotFoundException("Document " + documentId + " does not exists.");
      if (!((CmisTypeDocumentDefinitionType)getTypeDefinition(documentData.getTypeId(), false)).isVersionable())
         throw new ConstraintException("Type " + documentData.getTypeId() + " is not versionable.");
      ObjectData pwcData = storage.checkout(documentData);
      return getCmisObject(pwcData, false, EnumIncludeRelationships.NONE, false, false, PropertyFilter.ALL,
         RenditionFilter.NONE);
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject getObjectOfLatestVersion(String versionSeriesId, boolean major, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, boolean includePolicyIDs, boolean includeAcl,
      String propertyFilter, String renditionFilter) throws ObjectNotFoundException, FilterNotValidException,
      CmisRuntimeException
   {
      Collection<ObjectData> versions = storage.getVersions(versionSeriesId);
      if (versions == null)
         throw new ObjectNotFoundException("Version series " + versionSeriesId + " does not exists.");

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);
      if (includeRelationships == null)
         includeRelationships = EnumIncludeRelationships.NONE;
      // Even for not-versionable documents version series contains exactly one version of document.
      if (versions.size() == 1)
         return getCmisObject(versions.iterator().next(), includeAllowableActions, includeRelationships, false, false,
            parsedPropertyFilter, parsedRenditionFilter);

      List<ObjectData> v = new ArrayList<ObjectData>(versions);
      Collections.sort(v, CmisUtils.versionComparator);
      if (!major)
         return getCmisObject(v.get(0), includeAllowableActions, includeRelationships, false, false,
            parsedPropertyFilter, parsedRenditionFilter);

      for (ObjectData object : v)
      {
         boolean majorProperty = object.getBoolean(CMIS.IS_MAJOR_VERSION);
         if (majorProperty)
            return getCmisObject(object, includeAllowableActions, includeRelationships, false, false,
               parsedPropertyFilter, parsedRenditionFilter);
      }

      // May happen only if major version requested but there is no any major version.
      throw new ObjectNotFoundException("Not found any major versions in version series.");
   }

   /**
    * {@inheritDoc}
    */
   public CmisPropertiesType getPropertiesOfLatestVersion(String versionSeriesId, boolean major, String propertyFilter)
      throws FilterNotValidException, ObjectNotFoundException, CmisRuntimeException
   {
      Collection<ObjectData> versions = storage.getVersions(versionSeriesId);
      if (versions == null)
         throw new ObjectNotFoundException("Version series " + versionSeriesId + " does not exists.");
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      // Even for not-versionable documents version series contains exactly one version of document.
      if (versions.size() == 1)
         return getProperties(versions.iterator().next(), parsedPropertyFilter);

      List<ObjectData> v = new ArrayList<ObjectData>(versions);
      Collections.sort(v, CmisUtils.versionComparator);
      if (!major)
         return getProperties(v.get(0), parsedPropertyFilter);

      for (ObjectData object : v)
      {
         boolean majorProperty = object.getBoolean(CMIS.IS_MAJOR_VERSION);
         if (majorProperty)
            return getProperties(object, parsedPropertyFilter);
      }

      // May happen only if major version requested but there is no any major version.
      throw new ObjectNotFoundException("Not found any major versions in version series.");
   }

   // ------- Navigation Services -------
   
   /**
    * {@inheritDoc}
    */
   public CmisObjectInFolderList getChildren(String folderId, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, boolean includePathSegments, String propertyFilter,
      String renditionFilter, String orderBy, int maxItems, int skipCount) throws ObjectNotFoundException,
      InvalidArgumentException, FilterNotValidException, CmisRuntimeException
   {
      if (skipCount < 0)
         throw new InvalidArgumentException("skipCount parameter is negative.");

      ObjectData folderData = storage.getObject(folderId);
      if (folderData == null)
         throw new ObjectNotFoundException("Fodler object " + folderId + " does not exists.");
      if (folderData.getBaseType() != EnumBaseObjectTypeIds.CMIS_FOLDER)
         throw new InvalidArgumentException("Can't get children. Object " + folderId + " is not a Folder.");

      /* TODO : orderBy in some more usable form */
      ItemsIterator<ObjectData> iterator = storage.getChildren(folderData, orderBy);
      try
      {
         if (skipCount > 0)
            iterator.skip(skipCount);
      }
      catch (NoSuchElementException nse)
      {
         throw new InvalidArgumentException("skipCount parameter is greater then total number of argument");
      }

      CmisObjectInFolderList children = new CmisObjectInFolderListImpl();
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);
      if (includeRelationships == null)
         includeRelationships = EnumIncludeRelationships.NONE;
      int count = 0;
      while (iterator.hasNext() && (maxItems < 0 || count < maxItems))
      {
         ObjectData chilData = iterator.next();
         CmisObject child =
            getCmisObject(chilData, includeAllowableActions, includeRelationships, false, false, parsedPropertyFilter,
               parsedRenditionFilter);
         CmisObjectInFolder objectInFolder = new CmisObjectInFolderImpl();
         objectInFolder.setObject(child);
         objectInFolder.setPathSegment(chilData.getName());
         children.getObjects().add(objectInFolder);
         count++;
      }

      // Indicate that we have some more results.
      children.setHasMoreItems(iterator.hasNext());
      long total = iterator.size();
      if (total != -1)
         children.setNumItems(BigInteger.valueOf(total));
      return children;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject getFolderParent(String folderId, String propertyFilter) throws ObjectNotFoundException,
      InvalidArgumentException, FilterNotValidException, CmisRuntimeException
   {
      ObjectData folderData = storage.getObject(folderId);
      if (folderData == null)
         throw new ObjectNotFoundException("Fodler object " + folderId + " does not exists.");
      if (folderData.getBaseType() != EnumBaseObjectTypeIds.CMIS_FOLDER)
         throw new InvalidArgumentException("Object " + folderId + " is not a Folder.");
      if (storage.getRepositoryInfo().getRootFolderId().equals(folderId))
         throw new InvalidArgumentException("Can't get parent of root folder.");

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      ObjectData parentData = folderData.getParent();
      CmisObject cmisParent =
         getCmisObject(parentData, false, EnumIncludeRelationships.NONE, false, false, parsedPropertyFilter,
            RenditionFilter.NONE);
      return cmisParent;
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisObjectParents> getObjectParents(String objectId, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, boolean includeRelativePathSegment, String propertyFilter,
      String renditionFilter) throws ObjectNotFoundException, ConstraintException, FilterNotValidException,
      CmisRuntimeException
   {
      ObjectData object = storage.getObject(objectId);
      if (object == null)
         throw new ObjectNotFoundException("Object " + objectId + " does not exists.");

      String typeId = object.getTypeId();
      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId, false);
      if (!typeDefinition.isFileable())
         throw new ConstraintException("Can't get parents. Object " + objectId + " has type " + typeId
            + " that is not fileable");

      Collection<ObjectData> parentDatas = object.getParents();
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);
      if (includeRelationships == null)
         includeRelationships = EnumIncludeRelationships.NONE;
      List<CmisObjectParents> cmisParents = new ArrayList<CmisObjectParents>(parentDatas.size());
      for (ObjectData parentData : parentDatas)
      {
         CmisObject cmisParent =
            getCmisObject(parentData, includeAllowableActions, includeRelationships, false, false,
               parsedPropertyFilter, parsedRenditionFilter);
         CmisObjectParents parentType = new CmisObjectParentsImpl();
         parentType.setObject(cmisParent);
         if (includeRelativePathSegment)
            parentType.setRelativePathSegment(parentData.getName());
         cmisParents.add(parentType);
      }
      return cmisParents;
   }
   
   /**
    * {@inheritDoc}
    */
   public List<CmisObjectInFolderContainer> getDescendants(String folderId, int depth, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, boolean includePathSegments, String propertyFilter,
      String renditionFilter) throws ObjectNotFoundException, InvalidArgumentException, FilterNotValidException,
      CmisRuntimeException
   {
      return getTree(folderId, depth, null, includeAllowableActions, includeRelationships, includePathSegments,
         propertyFilter, renditionFilter);
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisObjectInFolderContainer> getFolderTree(String folderId, int depth, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, boolean includePathSegments, String propertyFilter,
      String renditionFilter) throws ObjectNotFoundException, InvalidArgumentException, FilterNotValidException,
      CmisRuntimeException
   {
      return getTree(folderId, depth, EnumBaseObjectTypeIds.CMIS_FOLDER, includeAllowableActions, includeRelationships,
         includePathSegments, propertyFilter, renditionFilter);
   }

   protected List<CmisObjectInFolderContainer> getTree(String folderId, int depth, EnumBaseObjectTypeIds typeFilter,
      boolean includeAllowableActions, EnumIncludeRelationships includeRelationships, boolean includePathSegments,
      String propertyFilter, String renditionFilter) throws ObjectNotFoundException, InvalidArgumentException,
      FilterNotValidException, CmisRuntimeException
   {
      ObjectData folderData = storage.getObject(folderId);
      if (folderData == null)
         throw new ObjectNotFoundException("Object " + folderId + " does not exists.");
      if (folderData.getBaseType() != EnumBaseObjectTypeIds.CMIS_FOLDER)
         throw new InvalidArgumentException("Can't get children. Object " + folderId + " is not a Folder.");
      List<CmisObjectInFolderContainer> tree = new ArrayList<CmisObjectInFolderContainer>();
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);
      for (ItemsIterator<ObjectData> children = storage.getChildren(folderData, null); children.hasNext();)
      {
         ObjectData child = children.next();
         if (typeFilter != null && child.getBaseType() != typeFilter)
            continue;
         CmisObjectInFolderContainer container = new CmisObjectInFolderContainerImpl();
         CmisObjectInFolder of = new CmisObjectInFolderImpl();
         of.setObject(getCmisObject(child, includeAllowableActions, includeRelationships, false, false,
            parsedPropertyFilter, parsedRenditionFilter));
         if (includePathSegments)
            of.setPathSegment(child.getName());
         container.setObjectInFolder(of);
         if (child.getBaseType() == EnumBaseObjectTypeIds.CMIS_FOLDER && depth > 1)
            container.getChildren().addAll(
               getTree(child.getObjectId(), depth - 1, typeFilter, includeAllowableActions, includeRelationships,
                  includePathSegments, propertyFilter, renditionFilter));
         tree.add(container);
      }
      return tree;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectList getCheckedOutDocs(String folderId, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, String propertyFilter, String renditionFilter, String orderBy,
      int maxItems, int skipCount) throws ObjectNotFoundException, InvalidArgumentException, FilterNotValidException,
      CmisRuntimeException
   {
      if (skipCount < 0)
         throw new InvalidArgumentException("skipCount parameter is negative.");

      ObjectData folderData = null;
      if (folderId != null)
      {
         folderData = storage.getObject(folderId);
         if (folderData == null)
            throw new ObjectNotFoundException("Fodler object " + folderId + " does not exists.");
         if (folderData.getBaseType() != EnumBaseObjectTypeIds.CMIS_FOLDER)
            throw new InvalidArgumentException("Can't get checkedout documents. Object " + folderId
               + " is not a Folder.");
      }

      ItemsIterator<ObjectData> iterator = storage.getCheckedOutDocuments(folderData, orderBy);

      try
      {
         if (skipCount > 0)
            iterator.skip(skipCount);
      }
      catch (NoSuchElementException nse)
      {
         throw new InvalidArgumentException("skipCount parameter is greater then total number of argument");
      }

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);
      if (includeRelationships == null)
         includeRelationships = EnumIncludeRelationships.NONE;
      CmisObjectList checkedout = new CmisObjectListImpl();
      int count = 0;
      while (iterator.hasNext() && (maxItems < 0 || count < maxItems))
      {
         ObjectData pwcData = iterator.next();
         CmisObject pwc =
            getCmisObject(pwcData, includeAllowableActions, includeRelationships, false, false, parsedPropertyFilter,
               parsedRenditionFilter);
         checkedout.getObjects().add(pwc);
         count++;
      }
      checkedout.setHasMoreItems(iterator.hasNext());
      long total = iterator.size();
      if (total != -1)
         checkedout.setNumItems(BigInteger.valueOf(total));
      return checkedout;
   }

   // Relationships services -------
   
   /**
    * {@inheritDoc}
    */
   public CmisObjectList getObjectRelationships(String objectId, EnumRelationshipDirection direction, String typeId,
      boolean includeSubRelationshipTypes, boolean includeAllowableActions, String propertyFilter, int maxItems,
      int skipCount) throws FilterNotValidException, ObjectNotFoundException
   {
      if (skipCount < 0)
         throw new InvalidArgumentException("skipCount parameter is negative.");

      if (direction == null)
         direction = EnumRelationshipDirection.SOURCE;

      ObjectData objectData = storage.getObject(objectId);
      if (objectData == null)
         throw new ObjectNotFoundException("Object " + objectId + " does not exists.");

      ItemsIterator<ObjectData> iterator = objectData.getRelationships(direction, typeId, includeSubRelationshipTypes);

      try
      {
         if (skipCount > 0)
            iterator.skip(skipCount);
      }
      catch (NoSuchElementException nse)
      {
         throw new InvalidArgumentException("skipCount parameter is greater then total number of argument");
      }

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      CmisObjectList relationship = new CmisObjectListImpl();
      long count = 0;
      while (iterator.hasNext() && (maxItems < 0 || count < maxItems))
      {
         ObjectData relationshipData = iterator.next();
         CmisObject cmis =
            getCmisObject(relationshipData, includeAllowableActions, null, false, false, parsedPropertyFilter,
               RenditionFilter.NONE);
         relationship.getObjects().add(cmis);
         count++;
      }

      // Indicate we have some more results or not
      relationship.setHasMoreItems(iterator.hasNext());
      long total = iterator.size();
      if (total != -1)
         relationship.setNumItems(BigInteger.valueOf(total));
      return relationship;
   }

   private CmisPropertiesType getProperties(ObjectData objectData, PropertyFilter parsedPropertyFilter)
   {
      CmisPropertiesType properties = new CmisPropertiesType();
      for (Map.Entry<String, CmisProperty> e : objectData.getProperties(parsedPropertyFilter).entrySet())
         properties.getProperty().add(e.getValue());
      return properties;
   }

   // ------- Repository Services. (Type Manager) -------
   
   /**
    * {@inheritDoc}
    */
   public CmisTypeDefinitionListType getTypeChildren(String typeId, boolean includePropertyDefinition, int maxItems,
      int skipCount) throws TypeNotFoundException, CmisRuntimeException
   {
      if (skipCount < 0)
         throw new InvalidArgumentException("skipCount parameter is negative.");

      ItemsIterator<CmisTypeDefinitionType> iterator = storage.getTypeChildren(typeId, includePropertyDefinition);
      try
      {
         if (skipCount > 0)
            iterator.skip(skipCount);
      }
      catch (NoSuchElementException nse)
      {
         throw new InvalidArgumentException("skipCount parameter is greater then total number of argument");
      }

      CmisTypeDefinitionListType children = new CmisTypeDefinitionListType();
      int count = 0;
      while (iterator.hasNext() && (maxItems < 0 || count < maxItems))
      {
         CmisTypeDefinitionType type = iterator.next();
         children.getTypes().add(type);
         count++;
      }
      // Indicate that we have some more results.
      children.setHasMoreItems(iterator.hasNext());
      long total = iterator.size();
      if (total != -1)
         children.setNumItems(BigInteger.valueOf(total));
      return children;
   }

   /**
    * {@inheritDoc}
    */
   public CmisTypeDefinitionType getTypeDefinition(String typeId) throws TypeNotFoundException, CmisRuntimeException
   {
      return getTypeDefinition(typeId, true);
   }

   /**
    * {@inheritDoc}
    */
   public CmisTypeDefinitionType getTypeDefinition(String typeId, boolean includePropertyDefinition)
      throws TypeNotFoundException, CmisRuntimeException
   {
      return storage.getTypeDefinition(typeId, includePropertyDefinition);
   }

   /**
    * {@inheritDoc}
    */
   public void addType(CmisTypeDefinitionType type) throws StorageException, CmisRuntimeException
   {
      storage.addType(type);
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisTypeContainer> getTypeDescendants(String typeId, int depth, boolean includePropertyDefinition)
      throws TypeNotFoundException, CmisRuntimeException
   {
      List<CmisTypeContainer> tree = new ArrayList<CmisTypeContainer>();
      for (ItemsIterator<CmisTypeDefinitionType> children = storage.getTypeChildren(typeId, includePropertyDefinition); children
         .hasNext();)
      {
         CmisTypeDefinitionType child = children.next();
         CmisTypeContainer container = new CmisTypeContainer();
         container.setType(child);
         if (typeId != null && depth > 1)
            container.getChildren().addAll(getTypeDescendants(child.getId(), depth - 1, includePropertyDefinition));
         tree.add(container);
      }
      return tree;
   }
   
   /**
    * {@inheritDoc}
    */
   public void removeType(String typeId) throws TypeNotFoundException, ConstraintException, StorageException,
      CmisRuntimeException
   {
      storage.removeType(typeId);
   }

   // ------- Discovery Services -------

   /**
    * {@inheritDoc}
    */
   public CmisObjectList getContentChanges(ChangeLogTokenHolder changeLogToken, boolean includeProperties,
      String propertyFilter, boolean includePolicyIDs, boolean includeAcl, int maxItems) throws ConstraintException,
      FilterNotValidException, CmisRuntimeException
   {
      EnumCapabilityChanges capabilityChanges = storage.getRepositoryInfo().getCapabilities().getCapabilityChanges();
      if (capabilityChanges == EnumCapabilityChanges.NONE)
         throw new NotSupportedException("Changes log feature is not supported.");
      if (changeLogToken == null)
         throw new CmisRuntimeException("Change log token holder may not be null.");

      ItemsIterator<ChangeEvent> iterator = storage.getChangeLog(changeLogToken.getToken());
      int count = 0;
      CmisObjectList changes = new CmisObjectListImpl();
      // TODO :
      // 1. How-to include changes type ?? Id id not clear how to pass this info via generated code.
      // 2. Add policy IDs, ACL, properties. Need have this in storage, first.
      while (iterator.hasNext() && (maxItems < 0 || count < maxItems))
      {
         CmisObject ch = new CmisObjectImpl();
         CmisPropertiesType properties = new CmisPropertiesType();
         CmisPropertyId id = new CmisPropertyId();
         id.setPropertyDefinitionId(CMIS.OBJECT_ID);
         id.setDisplayName(CMIS.OBJECT_ID);
         id.setQueryName(CMIS.OBJECT_ID);
         id.setLocalName(CMIS.OBJECT_ID);
         id.getValue().add(iterator.next().getObjectId());
         properties.getProperty().add(id);
         ch.setProperties(properties);
         changes.getObjects().add(ch);
         count++;
      }
      changes.setHasMoreItems(iterator.hasNext());
      long total = iterator.size();
      if (total != -1)
         changes.setNumItems(BigInteger.valueOf(total));
      return changes;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectList query(String statement, boolean searchAllVersions, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, String renditionFilter, int maxItems, int skipCount)
      throws FilterNotValidException, CmisRuntimeException
   {
      if (skipCount < 0)
         throw new InvalidArgumentException("skipCount parameter is negative.");

      ItemsIterator<Result> iterator = storage.query(new Query(statement, searchAllVersions));

      try
      {
         if (skipCount > 0)
            iterator.skip(skipCount);
      }
      catch (NoSuchElementException nse)
      {
         String msg = "skipCount parameter is greater then total number of argument";
         throw new InvalidArgumentException(msg);
      }

      if (includeRelationships == null)
         includeRelationships = EnumIncludeRelationships.NONE;
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);
      CmisObjectList list = new CmisObjectListImpl();
      int count = 0;
      while (iterator.hasNext() && count < maxItems)
      {
         Result result = iterator.next();
         StringBuilder propertyFilter = new StringBuilder();
         if (result.getPropertyNames() != null)
         {
            for (String s : result.getPropertyNames())
            {
               if (propertyFilter.length() > 0)
                  propertyFilter.append(',');
               propertyFilter.append(s);
            }
         }
         ObjectData data = storage.getObject(result.getObjectId());
         if (data == null)
            throw new CmisRuntimeException("Object " + result.getObjectId() + " was removed.");
         CmisObject object =
            getCmisObject(data, includeAllowableActions, includeRelationships, false, false, new PropertyFilter(
               propertyFilter.toString()), parsedRenditionFilter);

         Score score = result.getScore();
         if (score != null)
         {
            CmisPropertyDecimal scoreProperty = new CmisPropertyDecimal();
            scoreProperty.setLocalName(score.getScoreColumnName());
            scoreProperty.setDisplayName(score.getScoreColumnName());
            scoreProperty.setPropertyDefinitionId(score.getScoreColumnName());
            scoreProperty.getValue().add(score.getScoreValue());
            object.getProperties().getProperty().add(0, scoreProperty);
         }
         list.getObjects().add(object);
         count++;
      }

      // Indicate that we have some more results.
      list.setHasMoreItems(iterator.hasNext());
      long total = iterator.size();
      if (total != -1)
         list.setNumItems(BigInteger.valueOf(total));
      return list;
   }

   // -------
   
   private CmisPropertyDefinitionType getPropertyDefinition(List<CmisPropertyDefinitionType> all, String id)
   {
      if (all != null)
      {
         for (CmisPropertyDefinitionType propDef : all)
         {
            if (propDef.getId().equals(id))
               return propDef;
         }
      }
      return null;
   }

   protected abstract CmisObject getCmisObject(ObjectData object, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, boolean includePolicyIds, boolean includeAcl,
      PropertyFilter parsedPropertyFilter, RenditionFilter parsedRenditionFilter);

   protected abstract void validateChangeToken(ObjectData documentData, String changeToken)
      throws UpdateConflictException;

   protected String getName(CmisPropertiesType properties)
   {
      CmisPropertyString property = (CmisPropertyString)getProperty(properties, CMIS.NAME);
      if (property != null && property.getValue().size() > 0)
         return property.getValue().get(0);
      return null;
   }

   protected CmisProperty getProperty(CmisPropertiesType all, String propertyId)
   {
      if (all != null)
      {
         List<CmisProperty> props = all.getProperty();
         for (CmisProperty prop : props)
         {
            if (prop.getPropertyDefinitionId().equals(propertyId))
               return prop;
         }
      }
      return null;
   }

   protected String getSourceId(CmisPropertiesType properties)
   {
      CmisPropertyId property = (CmisPropertyId)getProperty(properties, CMIS.SOURCE_ID);
      if (property != null && property.getValue().size() > 0)
         return property.getValue().get(0);
      return null;
   }

   protected String getTargetId(CmisPropertiesType properties)
   {
      CmisPropertyId property = (CmisPropertyId)getProperty(properties, CMIS.TARGET_ID);
      if (property != null && property.getValue().size() > 0)
         return property.getValue().get(0);
      return null;
   }

   protected String getTypeId(CmisPropertiesType properties)
   {
      CmisPropertyId property = (CmisPropertyId)getProperty(properties, CMIS.OBJECT_TYPE_ID);
      if (property != null && property.getValue().size() > 0)
         return property.getValue().get(0);
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public void close()
   {
      connected = false;
   }

}
