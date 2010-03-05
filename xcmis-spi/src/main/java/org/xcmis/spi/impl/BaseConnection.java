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
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisPropertyBoolean;
import org.xcmis.core.CmisPropertyId;
import org.xcmis.core.CmisPropertyString;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.CmisTypeDocumentDefinitionType;
import org.xcmis.core.CmisTypeRelationshipDefinitionType;
import org.xcmis.core.EnumACLPropagation;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumContentStreamAllowed;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.core.EnumRelationshipDirection;
import org.xcmis.core.EnumUnfileObject;
import org.xcmis.core.EnumVersioningState;
import org.xcmis.messaging.CmisObjectInFolderListType;
import org.xcmis.messaging.CmisObjectInFolderType;
import org.xcmis.messaging.CmisObjectListType;
import org.xcmis.messaging.CmisObjectParentsType;
import org.xcmis.messaging.CmisTypeDefinitionListType;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.Connection;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentAlreadyExistsException;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.object.ContentStream;
import org.xcmis.spi.object.ItemsIterator;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public abstract class BaseConnection implements Connection
{

   public void addObjectToFolder(String objectId, String folderId, boolean allVersions) throws ObjectNotFoundException,
      InvalidArgumentException, ConstraintException, CmisRuntimeException
   {
      CmisObjectIdentifier object = getObject(objectId);
      String typeId = object.getTypeId();
      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId, false);
      if (!typeDefinition.isFileable())
         throw new InvalidArgumentException("Object " + objectId + " is not fileable.");
      CmisObjectIdentifier folder = getObject(folderId);
      if (folder.getBaseType() != EnumBaseObjectTypeIds.CMIS_FOLDER)
         throw new InvalidArgumentException("Object " + folderId + " is not Folder.");
      CmisPropertyId allowedChildTypes = (CmisPropertyId)getProperty(folder, CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS);
      if (allowedChildTypes != null && allowedChildTypes.getValue().size() > 0
         && !allowedChildTypes.getValue().contains(typeId))
         throw new ConstraintException("Type " + typeId + " is not allowed as child for " + folder.getTypeId());
      addObjectToFolder(object, folder, allVersions);
   }

   protected abstract void addObjectToFolder(CmisObjectIdentifier object, CmisObjectIdentifier folder,
      boolean allVersions) throws CmisRuntimeException;

   public void applyAcl(String objectId, CmisAccessControlListType addAcl, CmisAccessControlListType removeAcl,
      EnumACLPropagation propagation) throws ObjectNotFoundException, ConstraintException, CmisRuntimeException
   {
      CmisObjectIdentifier object = getObject(objectId);
      String typeId = object.getTypeId();
      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId, false);
      if (!typeDefinition.isControllableACL())
         throw new ConstraintException("Type " + typeId + " is not controllable by ACL.");
      applyAcl(object, addAcl, removeAcl, propagation);
   }

   protected abstract void applyAcl(CmisObjectIdentifier object, CmisAccessControlListType addAcl,
      CmisAccessControlListType removeAcl, EnumACLPropagation propagation) throws CmisRuntimeException;

   /**
    * {@inheritDoc}
    */
   public void applyPolicy(String policyId, String objectId) throws ConstraintException, ObjectNotFoundException,
      CmisRuntimeException
   {
      CmisObjectIdentifier object = getObject(objectId);

      String typeId = object.getTypeId();
      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId, false);
      if (!typeDefinition.isControllablePolicy())
         throw new ConstraintException("Type " + typeId + " is not controllable by Policy.");

      CmisObjectIdentifier policy = getObject(objectId);
      applyPolicy(object, policy);
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType createDocument(String folderId, CmisPropertiesType properties, ContentStream content,
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

      CmisObjectIdentifier folder = null;
      if (folderId != null)
      {
         folder = getObject(folderId);
         if (folder.getBaseType() != EnumBaseObjectTypeIds.CMIS_FOLDER)
            throw new InvalidArgumentException("Object " + folderId + " is not a Folder.");
         CmisPropertyId allowedChildTypes = (CmisPropertyId)getProperty(folder, CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS);
         if (allowedChildTypes != null && allowedChildTypes.getValue().size() > 0
            && !allowedChildTypes.getValue().contains(typeId))
            throw new ConstraintException("Type " + typeId + " is not allowed as child for " + folder.getTypeId());
      }

      CmisObjectIdentifier document =
         createDocument(folder, typeDefinition, properties, content, addAcl, removeAcl, policies, versioningState);
      return createCmisObject(document, false, EnumIncludeRelationships.NONE, false, true, PropertyFilter.ALL,
         RenditionFilter.NONE);
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType createDocumentFromSource(String sourceId, String folderId, CmisPropertiesType properties,
      CmisAccessControlListType addAcl, CmisAccessControlListType removeAcl, List<String> policies,
      EnumVersioningState versioningState) throws ObjectNotFoundException, ConstraintException,
      InvalidArgumentException, NameConstraintViolationException, StorageException, CmisRuntimeException
   {
      CmisObjectIdentifier source = getObject(sourceId);

      if (source.getBaseType() != EnumBaseObjectTypeIds.CMIS_DOCUMENT)
         throw new ConstraintException("Source object is not Document.");

      String typeId = source.getTypeId();
      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId);

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

      CmisObjectIdentifier folder = null;
      if (folderId != null)
      {
         folder = getObject(folderId);
         if (folder.getBaseType() != EnumBaseObjectTypeIds.CMIS_FOLDER)
            throw new InvalidArgumentException("Object " + folderId + " is not a Folder.");
         CmisPropertyId allowedChildTypes = (CmisPropertyId)getProperty(folder, CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS);
         if (allowedChildTypes != null && allowedChildTypes.getValue().size() > 0
            && !allowedChildTypes.getValue().contains(typeId))
            throw new ConstraintException("Type " + typeId + " is not allowed as child for " + folder.getTypeId());
      }

      CmisObjectIdentifier document =
         createDocumentFromSource(source, folder, properties, addAcl, removeAcl, policies, versioningState);
      return createCmisObject(document, false, EnumIncludeRelationships.NONE, false, true, PropertyFilter.ALL,
         RenditionFilter.NONE);
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType createFolder(String folderId, CmisPropertiesType properties, CmisAccessControlListType addAcl,
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

      CmisObjectIdentifier folder = getObject(folderId);

      if (folder.getBaseType() != EnumBaseObjectTypeIds.CMIS_FOLDER)
         throw new InvalidArgumentException("Object " + folderId + " is not a Folder.");

      CmisPropertyId allowedChildTypes = (CmisPropertyId)getProperty(folder, CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS);
      if (allowedChildTypes != null && allowedChildTypes.getValue().size() > 0
         && !allowedChildTypes.getValue().contains(typeId))
         throw new ConstraintException("Type " + typeId + " is not allowed as child for " + folder.getTypeId());

      CmisObjectIdentifier newFolder = createFolder(folder, typeDefinition, properties, addAcl, removeAcl, policies);
      return createCmisObject(newFolder, false, EnumIncludeRelationships.NONE, false, true, PropertyFilter.ALL,
         RenditionFilter.NONE);
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType createPolicy(String folderId, CmisPropertiesType properties, CmisAccessControlListType addAcl,
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

      if (!typeDefinition.isControllableACL()
         && (((addAcl != null && addAcl.getPermission().size() > 0) || (addAcl != null && addAcl.getPermission().size() > 0))))
         throw new ConstraintException("Type " + typeId + " is not controllable by ACL but at least one ACL provided.");

      if (!typeDefinition.isControllablePolicy() && policies != null && policies.size() > 0)
         throw new ConstraintException("Type " + typeId
            + " is not controllable by Policy but at least one Policy provided.");

      CmisObjectIdentifier folder = null;
      if (folderId != null)
      {
         folder = getObject(folderId);
         if (folder.getBaseType() != EnumBaseObjectTypeIds.CMIS_FOLDER)
            throw new InvalidArgumentException("Object " + folderId + " is not a Folder.");
         CmisPropertyId allowedChildTypes = (CmisPropertyId)getProperty(folder, CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS);
         if (allowedChildTypes != null && allowedChildTypes.getValue().size() > 0
            && !allowedChildTypes.getValue().contains(typeId))
            throw new ConstraintException("Type " + typeId + " is not allowed as child for " + folder.getTypeId());
      }

      CmisObjectIdentifier policy = createPolicy(folder, typeDefinition, properties, addAcl, removeAcl, policies);
      return createCmisObject(policy, false, EnumIncludeRelationships.NONE, false, false, PropertyFilter.ALL,
         RenditionFilter.NONE);
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType createRelationship(CmisPropertiesType properties, CmisAccessControlListType addAcl,
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

      CmisObjectIdentifier source = getObject(sourceId);
      String sourceTypeId = source.getTypeId();
      if (!(source.getBaseType() == EnumBaseObjectTypeIds.CMIS_DOCUMENT
         || source.getBaseType() == EnumBaseObjectTypeIds.CMIS_FOLDER || source.getBaseType() == EnumBaseObjectTypeIds.CMIS_POLICY))
         throw new InvalidArgumentException("Object with id: " + sourceId + " and type: " + sourceTypeId
            + " is not independent object and may not be used as 'source' of relationship");
      CmisObjectIdentifier target = getObject(targetId);
      String targetTypeId = target.getTypeId();
      if (!(target.getBaseType() == EnumBaseObjectTypeIds.CMIS_DOCUMENT
         || target.getBaseType() == EnumBaseObjectTypeIds.CMIS_FOLDER || target.getBaseType() == EnumBaseObjectTypeIds.CMIS_POLICY))
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

      CmisObjectIdentifier relationship =
         createRelationship(typeDefinition, source, target, properties, addAcl, removeAcl, policies);
      return createCmisObject(relationship, false, EnumIncludeRelationships.NONE, false, false, PropertyFilter.ALL,
         RenditionFilter.NONE);
   }

   /**
    * {@inheritDoc}
    */
   public void deleteContentStream(String documentId, String changeToken) throws ObjectNotFoundException,
      ConstraintException, UpdateConflictException, StorageException, CmisRuntimeException
   {
      CmisObjectIdentifier document = getObject(documentId);

      if (document.getBaseType() != EnumBaseObjectTypeIds.CMIS_DOCUMENT)
         throw new InvalidArgumentException("Object " + documentId + " is not Document.");

      validateChangeToken(changeToken);
      String typeId = document.getTypeId();
      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId, false);

      EnumContentStreamAllowed contentStreamAllowed =
         ((CmisTypeDocumentDefinitionType)typeDefinition).getContentStreamAllowed();
      if (contentStreamAllowed == EnumContentStreamAllowed.REQUIRED)
         throw new ConstraintException("Content required for type " + typeId + " and can't be removed.");

      deleteContentStream(document);
   }

   /**
    * {@inheritDoc}
    */
   public void deleteObject(String objectId, boolean deleteAllVersions) throws ObjectNotFoundException,
      ConstraintException, UpdateConflictException, StorageException, CmisRuntimeException
   {
      CmisObjectIdentifier object = getObject(objectId);
      if (object.getBaseType() == EnumBaseObjectTypeIds.CMIS_FOLDER)
      {
         if (getStorageInfo().getRootFolderId().equals(objectId))
            throw new ConstraintException("Root folder can't be removed.");
         if (hasChildren(object))
            throw new ConstraintException("Failed delete object. Object " + objectId
               + " is Folder and contains one or more objects.");
      }
      deleteObject(object, deleteAllVersions);
   }

   /**
    * {@inheritDoc}
    */
   public List<String> deleteTree(String folderId, boolean deleteAllVersions, EnumUnfileObject unfileObject,
      boolean continueOnFailure) throws ObjectNotFoundException, UpdateConflictException, StorageException,
      CmisRuntimeException
   {
      CmisObjectIdentifier folder = getObject(folderId);

      if (folder.getBaseType() != EnumBaseObjectTypeIds.CMIS_FOLDER)
         throw new ConstraintException("Failed delete tree. Object " + folderId + " is not a Folder.");
      if (getStorageInfo().getRootFolderId().equals(folderId))
         throw new ConstraintException("Root folder can't be removed.");

      if (unfileObject == null)
         unfileObject = EnumUnfileObject.DELETE; // Default value.

      List<String> failedDelete = new ArrayList<String>();
      deleteTree(folder, deleteAllVersions, failedDelete, unfileObject, continueOnFailure);
      return failedDelete;
   }

   public CmisAccessControlListType getAcl(String objectId, boolean onlyBasicPermissions)
      throws ObjectNotFoundException, ConstraintException, CmisRuntimeException
   {
      CmisObjectIdentifier object = getObject(objectId);
      String typeId = object.getTypeId();
      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId, false);
      if (!typeDefinition.isControllableACL())
         throw new ConstraintException("Type " + typeId + " is not controllable by ACL.");
      return getAcl(object, onlyBasicPermissions);
   }

   protected abstract CmisAccessControlListType getAcl(CmisObjectIdentifier object, boolean onlyBasicPermissions)
      throws CmisRuntimeException;

   /**
    * {@inheritDoc}
    */
   public CmisAllowableActionsType getAllowableActions(String objectId) throws ObjectNotFoundException,
      CmisRuntimeException
   {
      CmisObjectIdentifier object = getObject(objectId);
      return getAllowableActions(object);
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisObjectType> getAppliedPolicies(String objectId, String propertyFilter)
      throws ObjectNotFoundException, FilterNotValidException, CmisRuntimeException
   {
      CmisObjectIdentifier object = getObject(objectId);

      String typeId = object.getTypeId();
      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId, false);
      if (!typeDefinition.isControllablePolicy())
         throw new ConstraintException("Type " + typeId + " is not controllable by Policy.");

      PropertyFilter parsedFilter = new PropertyFilter(propertyFilter);
      List<CmisObjectIdentifier> policyDatas = getAppliedPolicies(object);
      List<CmisObjectType> policies = new ArrayList<CmisObjectType>(policyDatas.size());
      for (CmisObjectIdentifier policyData : policyDatas)
      {
         policies.add(createCmisObject(policyData, false, EnumIncludeRelationships.NONE, false, false, parsedFilter,
            RenditionFilter.NONE));
      }
      return policies;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectInFolderListType getChildren(String folderId, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, boolean includePathSegments, String propertyFilter,
      String renditionFilter, String orderBy, int maxItems, int skipCount) throws ObjectNotFoundException,
      InvalidArgumentException, FilterNotValidException, CmisRuntimeException
   {
      if (skipCount < 0)
      {
         String msg = "skipCount parameter is negative.";
         throw new InvalidArgumentException(msg);
      }
      if (maxItems < 0)
      {
         String msg = "maxItems parameter is negative.";
         throw new InvalidArgumentException(msg);
      }

      CmisObjectIdentifier folder = getObject(folderId);

      if (folder.getBaseType() != EnumBaseObjectTypeIds.CMIS_FOLDER)
         throw new InvalidArgumentException("Can't get children. Object " + folderId + " is not a Folder.");

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);
      if (includeRelationships == null)
         includeRelationships = EnumIncludeRelationships.NONE;
      /* TODO : orderBy in some more usable form */

      ItemsIterator<CmisObjectIdentifier> iterator = getChildren(folder, orderBy);
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

      CmisObjectInFolderListType children = new CmisObjectInFolderListType();
      int count = 0;
      while (iterator.hasNext() && count < maxItems)
      {
         CmisObjectIdentifier data = iterator.next();
         CmisObjectType child =
            createCmisObject(data, includeAllowableActions, includeRelationships, false, false, parsedPropertyFilter,
               parsedRenditionFilter);
         CmisObjectInFolderType objectInFolder = new CmisObjectInFolderType();
         objectInFolder.setObject(child);
         objectInFolder.setPathSegment(data.getName());
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
   public ContentStream getContentStream(String objectId, String streamId, long offset, long length)
      throws ObjectNotFoundException, ConstraintException, CmisRuntimeException
   {
      CmisObjectIdentifier object = getObject(objectId);
      if (object.getBaseType() == EnumBaseObjectTypeIds.CMIS_FOLDER && streamId == null)
      {
         // May be rendition stream only.
         throw new ConstraintException("streamId is not specified.");
      }
      return getContentStream(object, streamId, offset, length);
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType getFolderParent(String folderId, String propertyFilter) throws ObjectNotFoundException,
      InvalidArgumentException, FilterNotValidException, CmisRuntimeException
   {
      CmisObjectIdentifier folder = getObject(folderId);

      if (folder.getBaseType() != EnumBaseObjectTypeIds.CMIS_FOLDER)
         throw new InvalidArgumentException("Object " + folderId + " is not a Folder.");
      if (getStorageInfo().getRootFolderId().equals(folderId))
         throw new InvalidArgumentException("Can't get parent of root folder.");

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      CmisObjectIdentifier parent = getFolderParent(folder);
      return createCmisObject(parent, false, EnumIncludeRelationships.NONE, false, false, parsedPropertyFilter,
         RenditionFilter.NONE);
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType getObject(String objectId, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, boolean includePolicyIDs, boolean includeAcl,
      String propertyFilter, String renditionFilter) throws ObjectNotFoundException, FilterNotValidException,
      CmisRuntimeException
   {
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);
      if (includeRelationships == null)
         includeRelationships = EnumIncludeRelationships.NONE;

      CmisObjectIdentifier object = getObject(objectId);
      return createCmisObject(object, includeAllowableActions, includeRelationships, includePolicyIDs, includeAcl,
         parsedPropertyFilter, parsedRenditionFilter);
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType getObjectByPath(String path, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, boolean includePolicyIDs, boolean includeAcl,
      String propertyFilter, String renditionFilter) throws ObjectNotFoundException, FilterNotValidException,
      CmisRuntimeException
   {
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);
      if (includeRelationships == null)
         includeRelationships = EnumIncludeRelationships.NONE;
      CmisObjectIdentifier object = getObjectByPath(path);
      return createCmisObject(object, includeAllowableActions, includeRelationships, includePolicyIDs, includeAcl,
         parsedPropertyFilter, parsedRenditionFilter);
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType getObjectOfLatestVersion(String versionSeriesId, boolean major,
      boolean includeAllowableActions, EnumIncludeRelationships includeRelationships, boolean includePolicyIDs,
      boolean includeAcl, String propertyFilter, String renditionFilter) throws ObjectNotFoundException,
      FilterNotValidException, CmisRuntimeException
   {
      List<CmisObjectIdentifier> versions = getVersions(versionSeriesId);
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);
      if (includeRelationships == null)
         includeRelationships = EnumIncludeRelationships.NONE;
      if (major)
         return createCmisObject(versions.get(0), includeAllowableActions, includeRelationships, false, false,
            parsedPropertyFilter, parsedRenditionFilter);

      for (CmisObjectIdentifier object : versions)
      {
         CmisPropertyBoolean majorProperty = (CmisPropertyBoolean)getProperty(object, CMIS.IS_MAJOR_VERSION);
         if (majorProperty.getValue().get(0))
            return createCmisObject(object, includeAllowableActions, includeRelationships, false, false,
               parsedPropertyFilter, parsedRenditionFilter);
      }
      throw new ObjectNotFoundException("Not found any major versions in version series.");
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisObjectParentsType> getObjectParents(String objectId, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, boolean includeRelativePathSegment, String propertyFilter,
      String renditionFilter) throws ObjectNotFoundException, ConstraintException, FilterNotValidException,
      CmisRuntimeException
   {
      CmisObjectIdentifier object = getObject(objectId);

      String typeId = object.getTypeId();
      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId, false);
      if (!typeDefinition.isFileable())
         throw new ConstraintException("Can't get parents. Object " + objectId + " has type " + typeId
            + " that is not fileable");

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);
      if (includeRelationships == null)
         includeRelationships = EnumIncludeRelationships.NONE;

      List<CmisObjectIdentifier> parentDatas = getObjectParents(object);
      List<CmisObjectParentsType> parents = new ArrayList<CmisObjectParentsType>(parentDatas.size());
      for (CmisObjectIdentifier parentData : parentDatas)
      {
         CmisObjectType parent =
            createCmisObject(parentData, includeAllowableActions, includeRelationships, false, false,
               parsedPropertyFilter, parsedRenditionFilter);
         CmisObjectParentsType parentType = new CmisObjectParentsType();
         parentType.setObject(parent);
         if (includeRelativePathSegment)
            parentType.setRelativePathSegment(parentData.getName());
         parents.add(parentType);
      }
      return parents;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectListType getObjectRelationships(String objectId, EnumRelationshipDirection direction,
      String typeId, boolean includeSubRelationshipTypes, boolean includeAllowableActions, String propertyFilter,
      int maxItems, int skipCount) throws FilterNotValidException, ObjectNotFoundException
   {
      if (skipCount < 0)
      {
         String msg = "SkipCount parameter is negative.";
         throw new InvalidArgumentException(msg);
      }

      if (maxItems < 0)
      {
         String msg = "MaxItems parameter is negative.";
         throw new InvalidArgumentException(msg);
      }
      if (direction == null)
         direction = EnumRelationshipDirection.SOURCE;

      CmisObjectIdentifier object = getObject(objectId);
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);

      ItemsIterator<CmisObjectIdentifier> iterator =
         getObjectRelationships(object, direction, typeId, includeSubRelationshipTypes, includeAllowableActions,
            parsedPropertyFilter);

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

      CmisObjectListType relationship = new CmisObjectListType();
      long count = 0;
      while (iterator.hasNext() && count < maxItems)
      {
         CmisObjectIdentifier data = iterator.next();
         CmisObjectType cmis =
            createCmisObject(data, includeAllowableActions, null, false, false, parsedPropertyFilter,
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

   /**
    * {@inheritDoc}
    */
   public CmisPropertiesType getProperties(String objectId, String propertyFilter) throws ObjectNotFoundException,
      FilterNotValidException, CmisRuntimeException
   {
      CmisObjectIdentifier object = getObject(objectId);
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      return getProperties(object, parsedPropertyFilter);
   }

   public CmisPropertiesType getPropertiesOfLatestVersion(String versionSeriesId, boolean major, String propertyFilter)
      throws FilterNotValidException, ObjectNotFoundException, CmisRuntimeException
   {
      List<CmisObjectIdentifier> versions = getVersions(versionSeriesId);
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      if (major)
         return getProperties(versions.get(0), parsedPropertyFilter);

      for (CmisObjectIdentifier object : versions)
      {
         CmisPropertyBoolean majorProperty = (CmisPropertyBoolean)getProperty(object, CMIS.IS_MAJOR_VERSION);
         if (majorProperty.getValue().get(0))
            return getProperties(object, parsedPropertyFilter);
      }
      throw new ObjectNotFoundException("Not found any major versions in version series.");
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
   public CmisObjectType moveObject(String objectId, String targetFolderId, String sourceFolderId)
      throws ObjectNotFoundException, ConstraintException, InvalidArgumentException, UpdateConflictException,
      StorageException, CmisRuntimeException
   {
      if (sourceFolderId == null)
         throw new InvalidArgumentException("sourceFolderId is not specified.");

      CmisObjectIdentifier object = getObject(objectId);
      CmisObjectIdentifier target = getObject(targetFolderId);
      CmisObjectIdentifier source = getObject(sourceFolderId);

      if (target.getBaseType() != EnumBaseObjectTypeIds.CMIS_FOLDER)
         throw new InvalidArgumentException("Object " + targetFolderId + " is not a Folder.");
      if (source.getBaseType() != EnumBaseObjectTypeIds.CMIS_FOLDER)
         throw new InvalidArgumentException("Object " + sourceFolderId + " is not a Folder.");

      String objectTypeId = object.getTypeId();
      CmisPropertyId allowedChildTypes = (CmisPropertyId)getProperty(target, CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS);
      if (allowedChildTypes != null && allowedChildTypes.getValue().size() > 0
         && !allowedChildTypes.getValue().contains(objectTypeId))
         throw new ConstraintException("Type " + objectTypeId + " is not allowed as child for " + target.getTypeId());

      return moveObject(object, target, source);
   }

   /**
    * {@inheritDoc}
    */
   public void removePolicy(String policyId, String objectId) throws ConstraintException, ObjectNotFoundException,
      CmisRuntimeException
   {
      CmisObjectIdentifier object = getObject(objectId);
      String typeId = object.getTypeId();
      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId, false);
      if (!typeDefinition.isControllablePolicy())
         throw new ConstraintException("Type " + typeId + " is not controllable by Policy.");
      CmisObjectIdentifier policy = getObject(policyId);
      removePolicy(object, policy);
   }

   /**
    * {@inheritDoc}
    */
   public void setContentStream(String documentId, ContentStream content, String changeToken, boolean overwriteFlag)
      throws ObjectNotFoundException, ContentAlreadyExistsException, StreamNotSupportedException,
      UpdateConflictException, IOException, StorageException, CmisRuntimeException
   {
      CmisObjectIdentifier document = getObject(documentId);

      if (document.getBaseType() != EnumBaseObjectTypeIds.CMIS_DOCUMENT)
         throw new InvalidArgumentException("Object " + documentId + " is not a Document.");

      validateChangeToken(changeToken);
      String typeId = document.getTypeId();
      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId, false);
      EnumContentStreamAllowed contentStreamAllowed =
         ((CmisTypeDocumentDefinitionType)typeDefinition).getContentStreamAllowed();
      if (contentStreamAllowed == EnumContentStreamAllowed.NOTALLOWED)
         throw new StreamNotSupportedException("Content is not allowed for type " + typeId);

      if (!overwriteFlag && hasContent(document))
         throw new ContentAlreadyExistsException("Document already has content stream and 'overwriteFlag' is false.");

      setContentStream(document, content);
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType updateProperties(String objectId, String changeToken, CmisPropertiesType properties)
      throws ObjectNotFoundException, ConstraintException, NameConstraintViolationException, UpdateConflictException,
      StorageException, CmisRuntimeException
   {
      CmisObjectIdentifier object = getObject(objectId);
      validateChangeToken(changeToken);
      return updateProperties(object, properties);
   }

   public List<CmisObjectType> getAllVersions(String versionSeriesId, boolean includeAllowableActions,
      String propertyFilter) throws ObjectNotFoundException, FilterNotValidException, CmisRuntimeException
   {
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      List<CmisObjectType> versions = new ArrayList<CmisObjectType>();
      for (CmisObjectIdentifier object : getVersions(versionSeriesId))
      {
         versions.add(createCmisObject(object, includeAllowableActions, EnumIncludeRelationships.NONE, false, false,
            parsedPropertyFilter, RenditionFilter.NONE));
      }
      return versions;
   }

   protected abstract List<CmisObjectIdentifier> getVersions(String versionSeriesId) throws ObjectNotFoundException;

   protected abstract void validateChangeToken(String changeToken) throws UpdateConflictException;

   protected abstract void applyPolicy(CmisObjectIdentifier object, CmisObjectIdentifier policy)
      throws CmisRuntimeException;

   protected abstract CmisObjectIdentifier createDocument(CmisObjectIdentifier folder,
      CmisTypeDefinitionType typeDefinition, CmisPropertiesType properties, ContentStream content,
      CmisAccessControlListType addAcl, CmisAccessControlListType removeACEs, List<String> policies,
      EnumVersioningState versioningState) throws StorageException, NameConstraintViolationException,
      CmisRuntimeException;

   protected abstract CmisObjectIdentifier createDocumentFromSource(CmisObjectIdentifier source,
      CmisObjectIdentifier folder, CmisPropertiesType properties, CmisAccessControlListType addAcl,
      CmisAccessControlListType removeAcl, List<String> policies, EnumVersioningState versioningState)
      throws StorageException, NameConstraintViolationException, CmisRuntimeException;

   protected abstract CmisObjectIdentifier createFolder(CmisObjectIdentifier folder,
      CmisTypeDefinitionType typeDefinition, CmisPropertiesType properties, CmisAccessControlListType addAcl,
      CmisAccessControlListType removeAcl, List<String> policies) throws StorageException,
      NameConstraintViolationException, CmisRuntimeException;

   protected abstract CmisObjectIdentifier createPolicy(CmisObjectIdentifier folder,
      CmisTypeDefinitionType typeDefinition, CmisPropertiesType properties, CmisAccessControlListType addAcl,
      CmisAccessControlListType removeAcl, List<String> policies) throws StorageException,
      NameConstraintViolationException, CmisRuntimeException;

   protected abstract CmisObjectIdentifier createRelationship(CmisTypeDefinitionType typeDefinition,
      CmisObjectIdentifier source, CmisObjectIdentifier target, CmisPropertiesType properties,
      CmisAccessControlListType addAcl, CmisAccessControlListType removeAcl, List<String> policies)
      throws StorageException, NameConstraintViolationException, CmisRuntimeException;

   protected abstract void deleteContentStream(CmisObjectIdentifier document) throws StorageException,
      CmisRuntimeException;

   protected abstract void deleteTree(CmisObjectIdentifier folder, boolean deleteAllVersions,
      List<String> failedDelete, EnumUnfileObject unfileObject, boolean continueOnFailure)
      throws UpdateConflictException, StorageException, CmisRuntimeException;

   protected abstract void deleteObject(CmisObjectIdentifier object, boolean deleteAllVersion)
      throws UpdateConflictException, StorageException, CmisRuntimeException;

   protected abstract CmisAllowableActionsType getAllowableActions(CmisObjectIdentifier object)
      throws CmisRuntimeException;

   protected abstract List<CmisObjectIdentifier> getAppliedPolicies(CmisObjectIdentifier object)
      throws CmisRuntimeException;

   protected abstract ItemsIterator<CmisObjectIdentifier> getChildren(CmisObjectIdentifier folder, String orderBy)
      throws CmisRuntimeException;

   protected abstract CmisObjectType createCmisObject(CmisObjectIdentifier object, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, boolean includePolicyIds, boolean includeAcl,
      PropertyFilter parsedPropertyFilter, RenditionFilter parsedRenditionFilter);

   protected abstract ContentStream getContentStream(CmisObjectIdentifier object, String streamId, long offset,
      long length) throws ConstraintException, CmisRuntimeException;

   protected abstract CmisObjectIdentifier getFolderParent(CmisObjectIdentifier folder) throws CmisRuntimeException;

   protected String getName(CmisPropertiesType properties)
   {
      CmisPropertyString property = (CmisPropertyString)getProperty(properties, CMIS.NAME);
      if (property != null && property.getValue().size() > 0)
         return property.getValue().get(0);
      return null;
   }

   protected abstract CmisObjectIdentifier getObject(String objectId) throws ObjectNotFoundException,
      CmisRuntimeException;

   protected abstract CmisObjectIdentifier getObjectByPath(String path) throws ObjectNotFoundException,
      CmisRuntimeException;

   protected abstract List<CmisObjectIdentifier> getObjectParents(CmisObjectIdentifier object)
      throws CmisRuntimeException;

   protected abstract ItemsIterator<CmisObjectIdentifier> getObjectRelationships(CmisObjectIdentifier object,
      EnumRelationshipDirection direction, String typeId, boolean includeSubRelationshipTypes,
      boolean includeAllowableActions, PropertyFilter propertyFilter) throws CmisRuntimeException;

   protected abstract CmisPropertiesType getProperties(CmisObjectIdentifier object, PropertyFilter propertyFilter)
      throws CmisRuntimeException;

   protected abstract CmisProperty getProperty(CmisObjectIdentifier object, String propertyId)
      throws CmisRuntimeException;

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

   protected abstract boolean hasChildren(CmisObjectIdentifier object) throws CmisRuntimeException;

   protected abstract boolean hasContent(CmisObjectIdentifier document) throws CmisRuntimeException;

   protected abstract CmisObjectType moveObject(CmisObjectIdentifier object, CmisObjectIdentifier target,
      CmisObjectIdentifier source) throws UpdateConflictException, StorageException, CmisRuntimeException;

   protected abstract void removePolicy(CmisObjectIdentifier object, CmisObjectIdentifier policy)
      throws CmisRuntimeException;

   protected abstract void setContentStream(CmisObjectIdentifier document, ContentStream content) throws IOException,
      StorageException, CmisRuntimeException;

   protected abstract CmisObjectType updateProperties(CmisObjectIdentifier object, CmisPropertiesType properties)
      throws ConstraintException, NameConstraintViolationException, StorageException, CmisRuntimeException;

   protected abstract CmisTypeIdentifier getType(String typeId) throws TypeNotFoundException, CmisRuntimeException;

   protected abstract ItemsIterator<CmisTypeIdentifier> getTypeChildren(CmisTypeIdentifier type)
      throws CmisRuntimeException;

   /**
    * {@inheritDoc}
    */
   public CmisTypeDefinitionListType getTypeChildren(String typeId, boolean includePropertyDefinition, int maxItems,
      int skipCount) throws TypeNotFoundException, CmisRuntimeException
   {
      if (skipCount < 0)
      {
         String msg = "skipCount parameter is negative.";
         throw new InvalidArgumentException(msg);
      }
      if (maxItems < 0)
      {
         String msg = "maxItems parameter is negative.";
         throw new InvalidArgumentException(msg);
      }

      CmisTypeIdentifier type = getType(typeId);
      ItemsIterator<CmisTypeIdentifier> iterator = getTypeChildren(type);
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

      CmisTypeDefinitionListType children = new CmisTypeDefinitionListType();
      int count = 0;
      while (iterator.hasNext() && count < maxItems)
      {
         CmisTypeIdentifier identf = iterator.next();
         children.getTypes().add(getTypeDefinition(identf.getTypeId(), includePropertyDefinition));
      }
      // Indicate that we have some more results.
      children.setHasMoreItems(iterator.hasNext());
      long total = iterator.size();
      if (total != -1)
         children.setNumItems(BigInteger.valueOf(total));
      return children;
   }
}
