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

import org.xcmis.spi.AccessControlEntry;
import org.xcmis.spi.AccessControlPropagation;
import org.xcmis.spi.AllowableActions;
import org.xcmis.spi.BaseType;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.CapabilityChanges;
import org.xcmis.spi.ChangeEvent;
import org.xcmis.spi.ChangeLogTokenHolder;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.Connection;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentAlreadyExistsException;
import org.xcmis.spi.ContentStreamAllowed;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.IncludeRelationships;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.ItemsTree;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.NotSupportedException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.PropertyDefinition;
import org.xcmis.spi.RelationshipDirection;
import org.xcmis.spi.Rendition;
import org.xcmis.spi.Storage;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.TypeDefinition;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.UnfileObject;
import org.xcmis.spi.Updatability;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.VersioningState;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.DocumentData;
import org.xcmis.spi.data.FolderData;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.data.PolicyData;
import org.xcmis.spi.object.CmisObject;
import org.xcmis.spi.object.ObjectParent;
import org.xcmis.spi.object.Properties;
import org.xcmis.spi.object.Property;
import org.xcmis.spi.object.impl.CmisObjectImpl;
import org.xcmis.spi.object.impl.ObjectParentImpl;
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
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: BaseConnection.java 332 2010-03-11 17:24:56Z andrew00x $
 */
public abstract class BaseConnection implements Connection
{

   protected Storage storage;

   public BaseConnection(Storage storage)
   {
      this.storage = storage;
   }

   // ------- Multi-filing/Unfiling -------

   /**
    * {@inheritDoc}
    */
   public void addObjectToFolder(String objectId, String folderId, boolean allVersions) throws ObjectNotFoundException,
      InvalidArgumentException, ConstraintException, CmisRuntimeException
   {
      checkConnection();

      if (!storage.getRepositoryInfo().getCapabilities().isCapabilityMultifiling())
         throw new NotSupportedException("Multi-filing is not supported.");

      ObjectData object = storage.getObject(objectId);

      TypeDefinition typeDefinition = object.getTypeDefinition();
      if (!typeDefinition.isFileable())
         throw new InvalidArgumentException("Object " + objectId + " is not fileable.");

      ObjectData folder = storage.getObject(folderId);

      if (folder.getBaseType() != BaseType.FOLDER)
         throw new InvalidArgumentException("Object " + folderId + " is not Folder.");

      validateChildObjectType((FolderData)folder, typeDefinition.getId());

      storage.addObjectToFolder(object, (FolderData)folder, allVersions);
   }

   /**
    * {@inheritDoc}
    */
   public void removeObjectFromFolder(String objectId, String folderId) throws ObjectNotFoundException,
      CmisRuntimeException
   {
      checkConnection();

      if (!storage.getRepositoryInfo().getCapabilities().isCapabilityUnfiling())
         throw new NotSupportedException("Unfiling is not supported.");

      ObjectData object = storage.getObject(objectId);

      ObjectData folder = storage.getObject(folderId);

      if (folder.getBaseType() != BaseType.FOLDER)
         throw new InvalidArgumentException("Object " + folderId + " is not Folder.");

      storage.removeObjectFromFolder(object, (FolderData)folder);
   }

   // ------- ACL Services -------

   /**
    * {@inheritDoc}
    */
   public void applyACL(String objectId, List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL,
      AccessControlPropagation propagation) throws ObjectNotFoundException, ConstraintException, CmisRuntimeException
   {
      checkConnection();

      ObjectData objectData = storage.getObject(objectId);

      setACL(objectData, addACL, removeACL);
      storage.saveObject(objectData);
   }

   /**
    * {@inheritDoc}
    */
   public List<AccessControlEntry> getACL(String objectId, boolean onlyBasicPermissions)
      throws ObjectNotFoundException, ConstraintException, CmisRuntimeException
   {
      checkConnection();

      ObjectData objectData = storage.getObject(objectId);

      TypeDefinition typeDefinition = objectData.getTypeDefinition();
      if (!typeDefinition.isControllableACL())
         throw new ConstraintException("Type " + typeDefinition.getId() + " is not controllable by ACL.");

      return objectData.getACL(onlyBasicPermissions);
   }

   // ------- Policy Services -------

   /**
    * {@inheritDoc}
    */
   public void applyPolicy(String policyId, String objectId) throws ConstraintException, ObjectNotFoundException,
      CmisRuntimeException
   {
      checkConnection();

      ObjectData objectData = storage.getObject(objectId);

      addPolicies(objectData, Collections.singletonList(policyId));
      storage.saveObject(objectData);
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisObject> getAppliedPolicies(String objectId, String propertyFilter) throws ObjectNotFoundException,
      FilterNotValidException, CmisRuntimeException
   {
      checkConnection();

      ObjectData objectData = storage.getObject(objectId);

      TypeDefinition typeDefinition = objectData.getTypeDefinition();
      if (!typeDefinition.isControllablePolicy())
         throw new ConstraintException("Type " + typeDefinition.getId() + " is not controllable by Policy.");

      PropertyFilter parsedFilter = new PropertyFilter(propertyFilter);
      Collection<PolicyData> policies = objectData.getPolicies();
      List<CmisObject> policyIDs = new ArrayList<CmisObject>(policies.size());
      for (ObjectData policyData : policies)
      {
         CmisObject cmisPolicy =
            getCmisObject(policyData, false, IncludeRelationships.NONE, false, false, parsedFilter,
               RenditionFilter.NONE);
         policyIDs.add(cmisPolicy);
      }
      return policyIDs;
   }

   /**
    * {@inheritDoc}
    */
   public void removePolicy(String policyId, String objectId) throws ConstraintException, ObjectNotFoundException,
      CmisRuntimeException
   {
      checkConnection();

      ObjectData objectData = storage.getObject(objectId);

      TypeDefinition typeDefinition = objectData.getTypeDefinition();
      if (!typeDefinition.isControllablePolicy())
         throw new ConstraintException("Type " + typeDefinition.getId() + " is not controllable by Policy.");

      ObjectData policyData = storage.getObject(policyId);

      if (policyData.getBaseType() != BaseType.POLICY)
         throw new InvalidArgumentException("Object " + policyId + " is not a Policy object.");

      objectData.removePolicy((PolicyData)policyData);

      storage.saveObject(objectData);
   }

   // -------

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public CmisObject createDocument(String folderId, Properties properties, ContentStream content,
      List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL, List<String> policies,
      VersioningState versioningState) throws ObjectNotFoundException, ConstraintException, InvalidArgumentException,
      StreamNotSupportedException, NameConstraintViolationException, IOException, StorageException,
      CmisRuntimeException
   {
      if (properties == null)
         throw new InvalidArgumentException("Properties may not by null.");

      checkConnection();

      String typeId = null;
      Property<?> typeProperty = properties.getProperty(CMIS.OBJECT_TYPE_ID);
      if (typeProperty != null && typeProperty.getValues().size() > 0)
         typeId = ((Property<String>)typeProperty).getValues().get(0);

      if (typeId == null)
         throw new InvalidArgumentException("Type is not specified.");

      ObjectData folder = null;
      if (folderId != null)
      {
         folder = storage.getObject(folderId);
         if (folder.getBaseType() != BaseType.FOLDER)
            throw new InvalidArgumentException("Object " + folderId + " is not a Folder.");

         validateChildObjectType((FolderData)folder, typeId);
      }
      else
      {
         if (!storage.getRepositoryInfo().getCapabilities().isCapabilityUnfiling())
            throw new ConstraintException("Unfiling capability is not supported, parent folder must be provided.");
      }

      if (versioningState == null)
         versioningState = VersioningState.MAJOR;

      DocumentData newDocument = storage.createDocument((FolderData)folder, typeId, versioningState);

      newDocument.setProperties(properties.getProperties());

      setContentStream(newDocument, content);

      setACL(newDocument, addACL, removeACL);

      addPolicies(newDocument, policies);

      storage.saveObject(newDocument);

      CmisObject cmis =
         getCmisObject(newDocument, false, IncludeRelationships.NONE, false, true, PropertyFilter.ALL,
            RenditionFilter.NONE);

      return cmis;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject createDocumentFromSource(String sourceId, String folderId, Properties properties,
      List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL, List<String> policies,
      VersioningState versioningState) throws ObjectNotFoundException, ConstraintException, InvalidArgumentException,
      NameConstraintViolationException, StorageException, CmisRuntimeException
   {
      checkConnection();

      ObjectData source = storage.getObject(sourceId);

      if (source.getBaseType() != BaseType.DOCUMENT)
         throw new ConstraintException("Source object is not Document.");

      String typeId = source.getTypeId();

      ObjectData folder = null;
      if (folderId != null)
      {
         folder = storage.getObject(folderId);
         if (folder.getBaseType() != BaseType.FOLDER)
            throw new InvalidArgumentException("Object " + folderId + " is not a Folder.");

         validateChildObjectType((FolderData)folder, typeId);
      }
      else
      {
         if (!storage.getRepositoryInfo().getCapabilities().isCapabilityUnfiling())
            throw new ConstraintException("Unfiling capability is not supported, parent folder must be provided.");
      }

      DocumentData newDocument =
         storage.createCopyOfDocument((DocumentData)source, (FolderData)folder, versioningState);

      if (properties != null)
         newDocument.setProperties(properties.getProperties());

      setACL(newDocument, addACL, removeACL);

      addPolicies(newDocument, policies);

      storage.saveObject(newDocument);

      CmisObject cmis =
         getCmisObject(newDocument, false, IncludeRelationships.NONE, false, true, PropertyFilter.ALL,
            RenditionFilter.NONE);

      return cmis;
   }

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public CmisObject createFolder(String folderId, Properties properties, List<AccessControlEntry> addACL,
      List<AccessControlEntry> removeACL, List<String> policies) throws ObjectNotFoundException, ConstraintException,
      InvalidArgumentException, NameConstraintViolationException, StorageException, CmisRuntimeException
   {
      if (properties == null)
         throw new InvalidArgumentException("Properties may not by null.");

      checkConnection();

      String typeId = null;
      Property<?> typeProperty = properties.getProperty(CMIS.OBJECT_TYPE_ID);
      if (typeProperty != null && typeProperty.getValues().size() > 0)
         typeId = ((Property<String>)typeProperty).getValues().get(0);

      if (typeId == null)
         throw new InvalidArgumentException("Type is not specified.");

      if (folderId == null)
         throw new ConstraintException("Parent folder id is not provided.");
      
      ObjectData folder = storage.getObject(folderId);
      if (folder.getBaseType() != BaseType.FOLDER)
         throw new InvalidArgumentException("Object " + folderId + " is not a Folder.");

      validateChildObjectType((FolderData)folder, typeId);

      ObjectData newFolder = storage.createFolder((FolderData)folder, typeId);
      
      newFolder.setProperties(properties.getProperties());
      
      setACL(newFolder, addACL, removeACL);
      
      addPolicies(newFolder, policies);
      
      storage.saveObject(newFolder);
      
      CmisObject cmis =
         getCmisObject(newFolder, false, IncludeRelationships.NONE, false, true, PropertyFilter.ALL,
            RenditionFilter.NONE);
      
      return cmis;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject createPolicy(String folderId, Properties properties, List<AccessControlEntry> addAcl,
      List<AccessControlEntry> removeAcl, List<String> policies) throws ObjectNotFoundException, ConstraintException,
      InvalidArgumentException, NameConstraintViolationException, StorageException, CmisRuntimeException
   {
      checkConnection();

      String typeId = CmisUtils.getTypeId(properties);
      if (typeId == null)
         throw new InvalidArgumentException("Type is not specified.");

      String policyText = CmisUtils.getPolicyText(properties);
      if (policyText == null)
         throw new ConstraintException("Required property 'cmis:policyText' is not provided.");

      TypeDefinition typeDefinition = getTypeDefinition(typeId);

      if (BaseType.POLICY != typeDefinition.getBaseId())
         throw new ConstraintException("The typeId " + typeId
            + " represents object-type whose baseType is not a Policy.");

      ObjectData folderData = null;
      if (folderId != null)
      {
         folderData = storage.getObject(folderId);
         if (folderData == null)
            throw new ObjectNotFoundException("Folder object " + folderId + " does not exists.");
         if (folderData.getBaseType() != BaseType.FOLDER)
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
         && (((addAcl != null && addAcl.size() > 0) || (removeAcl != null && removeAcl.size() > 0))))
         throw new ConstraintException("Type " + typeId + " is not controllable by ACL but at least one ACL provided.");

      if (!typeDefinition.isControllablePolicy() && policies != null && policies.size() > 0)
         throw new ConstraintException("Type " + typeId
            + " is not controllable by Policy but at least one Policy provided.");

      ObjectData newPolicy = storage.createPolicy(folderData, typeDefinition, properties, addAcl, removeAcl, policies);
      storage.saveObject(newPolicy);
      CmisObject cmisPolicy =
         getCmisObject(newPolicy, false, IncludeRelationships.NONE, false, false, PropertyFilter.ALL,
            RenditionFilter.NONE);
      return cmisPolicy;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject createRelationship(Properties properties, List<AccessControlEntry> addAcl,
      List<AccessControlEntry> removeAcl, List<String> policies) throws ObjectNotFoundException, ConstraintException,
      NameConstraintViolationException, StorageException, CmisRuntimeException
   {
      checkConnection();

      String typeId = CmisUtils.getTypeId(properties);
      if (typeId == null)
         throw new InvalidArgumentException("Type is not specified.");

      TypeDefinition typeDefinition = getTypeDefinition(typeId);

      if (BaseType.RELATIONSHIP != typeDefinition.getBaseId())
         throw new ConstraintException("The typeId " + typeId
            + " represents object-type whose baseType is not a Relationship.");

      String sourceId = CmisUtils.getSourceId(properties);
      if (sourceId == null)
         throw new InvalidArgumentException("Required property 'cmis:sourceId' is not specified.");

      String targetId = CmisUtils.getTargetId(properties);
      if (targetId == null)
         throw new InvalidArgumentException("Required property 'cmis:targetId' is not specified.");

      ObjectData sourceData = storage.getObject(sourceId);
      if (sourceData == null)
         throw new ObjectNotFoundException("Source object " + sourceId + " does not exists.");
      String sourceTypeId = sourceData.getTypeId();
      if (!(sourceData.getBaseType() == BaseType.DOCUMENT || sourceData.getBaseType() == BaseType.FOLDER || sourceData
         .getBaseType() == BaseType.POLICY))
         throw new InvalidArgumentException("Object with id: " + sourceId + " and type: " + sourceTypeId
            + " is not independent object and may not be used as 'source' of relationship");
      ObjectData targetData = storage.getObject(targetId);
      if (targetData == null)
         throw new ObjectNotFoundException("Target object " + targetId + " does not exists.");
      String targetTypeId = targetData.getTypeId();
      if (!(targetData.getBaseType() == BaseType.DOCUMENT || targetData.getBaseType() == BaseType.FOLDER || targetData
         .getBaseType() == BaseType.POLICY))
         throw new InvalidArgumentException("Object with id: " + targetId + " and type: " + targetTypeId
            + " is not independent object and may not be used as 'target' of relationship");

      Collection<String> allowedSourceTypes = typeDefinition.getAllowedSourceTypes();
      if (allowedSourceTypes != null && allowedSourceTypes.size() > 0 && !allowedSourceTypes.contains(sourceTypeId))
         throw new ConstraintException("Type " + sourceTypeId + " is not allowed as source for relationship " + typeId);

      Collection<String> allowedTargetTypes = typeDefinition.getAllowedTargetTypes();
      if (allowedTargetTypes != null && allowedTargetTypes.size() > 0 && !allowedTargetTypes.contains(sourceTypeId))
         throw new ConstraintException("Type " + targetTypeId + " is not allowed as target for relationship " + typeId);

      if (!typeDefinition.isControllableACL()
         && (((addAcl != null && addAcl.size() > 0) || (removeAcl != null && removeAcl.size() > 0))))
         throw new ConstraintException("Type " + typeId + " is not controllable by ACL but at least one ACL provided.");

      if (!typeDefinition.isControllablePolicy() && policies != null && policies.size() > 0)
         throw new ConstraintException("Type " + typeId
            + " is not controllable by Policy but at least one Policy provided.");

      ObjectData newRelationship =
         storage.createRelationship(typeDefinition, sourceData, targetData, properties, addAcl, removeAcl, policies);
      storage.saveObject(newRelationship);
      CmisObject cmisRelationship =
         getCmisObject(newRelationship, false, IncludeRelationships.NONE, false, false, PropertyFilter.ALL,
            RenditionFilter.NONE);
      return cmisRelationship;
   }

   /**
    * {@inheritDoc}
    */
   public void deleteContentStream(String documentId, String changeToken) throws ObjectNotFoundException,
      ConstraintException, UpdateConflictException, StorageException, CmisRuntimeException
   {
      checkConnection();

      ObjectData documentData = storage.getObject(documentId);
      if (documentData == null)
         throw new ObjectNotFoundException("Document object " + documentId + " does not exists.");
      if (documentData.getBaseType() != BaseType.DOCUMENT)
         throw new InvalidArgumentException("Object " + documentId + " is not Document.");

      String typeId = documentData.getTypeId();
      TypeDefinition typeDefinition = getTypeDefinition(typeId, false);

      ContentStreamAllowed contentStreamAllowed = typeDefinition.getContentStreamAllowed();
      if (contentStreamAllowed == ContentStreamAllowed.REQUIRED)
         throw new ConstraintException("Content required for type " + typeId + " and can't be removed.");

      // Validate change token, object may be already updated.
      validateChangeToken(documentData, changeToken);
      storage.deleteContentStream(documentData);
      storage.saveObject(documentData);
   }

   /**
    * {@inheritDoc}
    */
   public ContentStream getContentStream(String objectId, String streamId, long offset, long length)
      throws ObjectNotFoundException, ConstraintException, CmisRuntimeException
   {
      checkConnection();

      ObjectData objectData = storage.getObject(objectId);
      if (objectData == null)
         throw new ObjectNotFoundException("Object " + objectId + " does not exists.");
      if (objectData.getBaseType() == BaseType.FOLDER && streamId == null)
      {
         // May be rendition stream only.
         throw new ConstraintException("streamId is not specified.");
      }
      return storage.getContentStream(objectData, streamId, offset, length);
   }

   /**
    * {@inheritDoc}
    */
   public void deleteObject(String objectId, Boolean deleteAllVersions) throws ObjectNotFoundException,
      ConstraintException, UpdateConflictException, StorageException, CmisRuntimeException
   {
      checkConnection();

      ObjectData objectData = storage.getObject(objectId);
      if (objectData == null)
         throw new ObjectNotFoundException("Object " + objectId + " does not exists.");
      if (deleteAllVersions == null)
         deleteAllVersions = true; // Default.
      if (objectData.getBaseType() == BaseType.FOLDER)
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
   public List<String> deleteTree(String folderId, Boolean deleteAllVersions, UnfileObject unfileObject,
      boolean continueOnFailure) throws ObjectNotFoundException, UpdateConflictException, CmisRuntimeException
   {
      checkConnection();

      ObjectData folderData = storage.getObject(folderId);
      if (folderData == null)
         throw new ObjectNotFoundException("Folder object " + folderId + " does not exists.");
      if (folderData.getBaseType() != BaseType.FOLDER)
         throw new ConstraintException("Failed delete tree. Object " + folderId + " is not a Folder.");
      if (storage.getRepositoryInfo().getRootFolderId().equals(folderId))
         throw new ConstraintException("Root folder can't be removed.");

      if (unfileObject == null)
         unfileObject = UnfileObject.DELETE; // Default value.
      if (deleteAllVersions == null)
         deleteAllVersions = true; // Default value.

      // TODO : need to check unfiling capability if 'unfileObject' is other then 'DELETE' ??
      Collection<String> failedDelete =
         storage.deleteTree(folderData, deleteAllVersions, unfileObject, continueOnFailure);
      return new ArrayList<String>(failedDelete);
   }

   /**
    * {@inheritDoc}
    */
   public AllowableActions getAllowableActions(String objectId) throws ObjectNotFoundException, CmisRuntimeException
   {
      checkConnection();

      ObjectData objectData = storage.getObject(objectId);
      if (objectData == null)
         throw new ObjectNotFoundException("Object " + objectId + " does not exists.");
      return storage.calculateAllowableActions(objectData);
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject getObject(String objectId, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includePolicyIDs, boolean includeAcl, String propertyFilter,
      String renditionFilter) throws ObjectNotFoundException, FilterNotValidException, CmisRuntimeException
   {
      checkConnection();

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);
      if (includeRelationships == null)
         includeRelationships = IncludeRelationships.NONE;

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
      IncludeRelationships includeRelationships, boolean includePolicyIDs, boolean includeAcl, String propertyFilter,
      String renditionFilter) throws ObjectNotFoundException, FilterNotValidException, CmisRuntimeException
   {
      checkConnection();

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);
      if (includeRelationships == null)
         includeRelationships = IncludeRelationships.NONE;
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
   public Properties getProperties(String objectId, String propertyFilter) throws ObjectNotFoundException,
      FilterNotValidException, CmisRuntimeException
   {
      checkConnection();

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
      checkConnection();

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

      if (targetData.getBaseType() != BaseType.FOLDER)
         throw new InvalidArgumentException("Object " + targetFolderId + " is not a Folder.");
      if (sourceData.getBaseType() != BaseType.FOLDER)
         throw new InvalidArgumentException("Object " + sourceFolderId + " is not a Folder.");

      // Check is specified source folder is valid.
      if (!objectData.getParents().contains(sourceData))
         throw new InvalidArgumentException("Object " + sourceFolderId + " is not parent for object " + objectId);

      String objectTypeId = objectData.getTypeId();
      String[] allowedChildTypes = targetData.getIds(CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS);
      if (allowedChildTypes != null && allowedChildTypes.length > 0
         && !Arrays.asList(allowedChildTypes).contains(objectTypeId))
         throw new ConstraintException("Type " + objectTypeId + " is not allowed as child for "
            + targetData.getTypeId());

      storage.moveObject(objectData, targetData, sourceData);
      CmisObject movedObject =
         getCmisObject(objectData, false, IncludeRelationships.NONE, false, false, PropertyFilter.ALL,
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
      checkConnection();

      ObjectData documentData = storage.getObject(documentId);
      if (documentData == null)
         throw new ObjectNotFoundException("Object " + documentId + " does not exists.");

      if (documentData.getBaseType() != BaseType.DOCUMENT)
         throw new InvalidArgumentException("Object " + documentId + " is not a Document.");

      String typeId = documentData.getTypeId();
      TypeDefinition typeDefinition = getTypeDefinition(typeId, false);
      ContentStreamAllowed contentStreamAllowed = typeDefinition.getContentStreamAllowed();
      if (contentStreamAllowed == ContentStreamAllowed.NOT_ALLOWED)
         throw new StreamNotSupportedException("Content is not allowed for type " + typeId);

      if (!overwriteFlag && storage.hasContent(documentData))
         throw new ContentAlreadyExistsException("Document already has content stream and 'overwriteFlag' is false.");

      // Validate change token, object may be already updated.
      validateChangeToken(documentData, changeToken);
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
      checkConnection();

      ObjectData objectData = storage.getObject(objectId);
      if (objectData == null)
         throw new ObjectNotFoundException("Object " + objectId + " does not exists.");
      // Validate change token, object may be already updated.
      validateChangeToken(objectData, changeToken);
      if (properties != null)
      {
         Collection<PropertyDefinition> propertyDefinitions =
            getTypeDefinition(objectData.getTypeId(), true).getPropertyDefinitions();
         for (Property<?> property : properties.getProperty())
         {
            CmisPropertyDefinitionType def =
               getPropertyDefinition(propertyDefinitions, property.getPropertyDefinitionId());
            if (def.getUpdatability() == Updatability.READWRITE)
               // TODO : check for required 
               objectData.setProperty(property);
         }
         storage.saveObject(objectData);
      }
      return getCmisObject(objectData, false, IncludeRelationships.NONE, false, false, PropertyFilter.ALL,
         RenditionFilter.NONE);
   }

   /**
    * {@inheritDoc}
    */
   public List<Rendition> getRenditions(String objectId, String renditionFilter, int maxItems, int skipCount)
      throws ObjectNotFoundException, FilterNotValidException, CmisRuntimeException
   {
      checkConnection();

      if (skipCount < 0)
         throw new InvalidArgumentException("skipCount parameter is negative.");
      ObjectData objectData = storage.getObject(objectId);
      if (objectData == null)
         throw new ObjectNotFoundException("Object " + objectId + " does not exists.");
      ItemsIterator<Rendition> iterator = storage.getRenditions(objectData);
      try
      {
         if (skipCount > 0)
            iterator.skip(skipCount);
      }
      catch (NoSuchElementException nse)
      {
         throw new InvalidArgumentException("skipCount parameter is greater then total number of argument");
      }
      List<Rendition> renditions = new ArrayList<Rendition>();
      int count = 0;
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);
      while (iterator.hasNext() && (maxItems < 0 || count < maxItems))
      {
         Rendition r = iterator.next();
         if (parsedRenditionFilter.accept(r))
            renditions.add(r);
         count++;
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
      checkConnection();

      Collection<ObjectData> versionDatas = storage.getVersions(versionSeriesId);
      if (versionDatas == null)
         throw new ObjectNotFoundException("Version series " + versionSeriesId + " does not exists.");
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      List<CmisObject> versions = new ArrayList<CmisObject>();
      for (ObjectData objectData : versionDatas)
      {
         versions.add(getCmisObject(objectData, includeAllowableActions, IncludeRelationships.NONE, false, false,
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
      checkConnection();

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

   public CmisObject checkin(String documentId, boolean major, Properties properties, ContentStream content,
      String checkinComment, List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL, List<String> policies)
      throws ConstraintException, UpdateConflictException, StreamNotSupportedException, IOException, StorageException
   {
      checkConnection();

      ObjectData pwcData = storage.getObject(documentId);
      if (pwcData == null)
         throw new ObjectNotFoundException("Document " + documentId + " does not exists.");
      ObjectData version =
         storage.checkin(pwcData, major, properties, content, checkinComment, addACL, removeACL, policies);
      return getCmisObject(version, false, IncludeRelationships.NONE, false, false, PropertyFilter.ALL,
         RenditionFilter.NONE);
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject checkout(String documentId) throws ConstraintException, UpdateConflictException,
      VersioningException, StorageException, CmisRuntimeException
   {
      checkConnection();

      ObjectData documentData = storage.getObject(documentId);
      if (documentData == null)
         throw new ObjectNotFoundException("Document " + documentId + " does not exists.");
      if (!(getTypeDefinition(documentData.getTypeId(), false)).isVersionable())
         throw new ConstraintException("Type " + documentData.getTypeId() + " is not versionable.");
      if (documentData.isVersionSeriesCheckedOut())
         throw new VersioningException("One document in version series already checked-out.");
      ObjectData pwcData = storage.checkout(documentData);
      return getCmisObject(pwcData, false, IncludeRelationships.NONE, false, false, PropertyFilter.ALL,
         RenditionFilter.NONE);
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject getObjectOfLatestVersion(String versionSeriesId, boolean major, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includePolicyIDs, boolean includeAcl, String propertyFilter,
      String renditionFilter) throws ObjectNotFoundException, FilterNotValidException, CmisRuntimeException
   {
      checkConnection();

      Collection<ObjectData> versions = storage.getVersions(versionSeriesId);
      if (versions == null)
         throw new ObjectNotFoundException("Version series " + versionSeriesId + " does not exists.");

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);
      if (includeRelationships == null)
         includeRelationships = IncludeRelationships.NONE;
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
   public Properties getPropertiesOfLatestVersion(String versionSeriesId, boolean major, String propertyFilter)
      throws FilterNotValidException, ObjectNotFoundException, CmisRuntimeException
   {
      checkConnection();

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
   public ItemsList<CmisObject> getChildren(String folderId, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includePathSegments, String propertyFilter,
      String renditionFilter, String orderBy, int maxItems, int skipCount) throws ObjectNotFoundException,
      InvalidArgumentException, FilterNotValidException, CmisRuntimeException
   {
      checkConnection();

      if (skipCount < 0)
         throw new InvalidArgumentException("skipCount parameter is negative.");

      ObjectData folderData = storage.getObject(folderId);
      if (folderData == null)
         throw new ObjectNotFoundException("Fodler object " + folderId + " does not exists.");
      if (folderData.getBaseType() != BaseType.FOLDER)
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

      ItemsListImpl<CmisObject> children = new ItemsListImpl<CmisObject>();
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);
      if (includeRelationships == null)
         includeRelationships = IncludeRelationships.NONE;
      int count = 0;
      while (iterator.hasNext() && (maxItems < 0 || count < maxItems))
      {
         ObjectData chilData = iterator.next();
         CmisObject child =
            getCmisObject(chilData, includeAllowableActions, includeRelationships, false, false, parsedPropertyFilter,
               parsedRenditionFilter);
         children.getItems().add(child);
         count++;
      }

      // Indicate that we have some more results.
      children.setHasMoreItems(iterator.hasNext());
      children.setNumItems(iterator.size()); // ItemsIterator gives -1 if total number is unknown.
      return children;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject getFolderParent(String folderId, String propertyFilter) throws ObjectNotFoundException,
      InvalidArgumentException, FilterNotValidException, CmisRuntimeException
   {
      checkConnection();

      ObjectData folderData = storage.getObject(folderId);
      if (folderData == null)
         throw new ObjectNotFoundException("Fodler object " + folderId + " does not exists.");
      if (folderData.getBaseType() != BaseType.FOLDER)
         throw new InvalidArgumentException("Object " + folderId + " is not a Folder.");
      if (storage.getRepositoryInfo().getRootFolderId().equals(folderId))
         throw new InvalidArgumentException("Can't get parent of root folder.");

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      ObjectData parentData = folderData.getParent();
      CmisObject cmisParent =
         getCmisObject(parentData, false, IncludeRelationships.NONE, false, false, parsedPropertyFilter,
            RenditionFilter.NONE);
      return cmisParent;
   }

   /**
    * {@inheritDoc}
    */
   public List<ObjectParent> getObjectParents(String objectId, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includeRelativePathSegment, String propertyFilter,
      String renditionFilter) throws ObjectNotFoundException, ConstraintException, FilterNotValidException,
      CmisRuntimeException
   {
      checkConnection();

      ObjectData object = storage.getObject(objectId);
      if (object == null)
         throw new ObjectNotFoundException("Object " + objectId + " does not exists.");

      String typeId = object.getTypeId();
      TypeDefinition typeDefinition = getTypeDefinition(typeId, false);
      if (!typeDefinition.isFileable())
         throw new ConstraintException("Can't get parents. Object " + objectId + " has type " + typeId
            + " that is not fileable");

      Collection<ObjectData> parentDatas = object.getParents();
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);
      if (includeRelationships == null)
         includeRelationships = IncludeRelationships.NONE;
      List<ObjectParent> cmisParents = new ArrayList<ObjectParent>(parentDatas.size());
      for (ObjectData parentData : parentDatas)
      {
         CmisObject cmisParent =
            getCmisObject(parentData, includeAllowableActions, includeRelationships, false, false,
               parsedPropertyFilter, parsedRenditionFilter);
         ObjectParent parentType =
            new ObjectParentImpl(cmisParent, includeRelativePathSegment ? object.getName() : null);
         cmisParents.add(parentType);
      }
      return cmisParents;
   }

   /**
    * {@inheritDoc}
    */
   public List<ItemsTree<CmisObject>> getDescendants(String folderId, int depth, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includePathSegments, String propertyFilter,
      String renditionFilter) throws ObjectNotFoundException, InvalidArgumentException, FilterNotValidException,
      CmisRuntimeException
   {
      return getTree(folderId, depth, null, includeAllowableActions, includeRelationships, includePathSegments,
         propertyFilter, renditionFilter);
   }

   /**
    * {@inheritDoc}
    */
   public List<ItemsTree<CmisObject>> getFolderTree(String folderId, int depth, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includePathSegments, String propertyFilter,
      String renditionFilter) throws ObjectNotFoundException, InvalidArgumentException, FilterNotValidException,
      CmisRuntimeException
   {
      return getTree(folderId, depth, BaseType.FOLDER, includeAllowableActions, includeRelationships,
         includePathSegments, propertyFilter, renditionFilter);
   }

   protected List<ItemsTree<CmisObject>> getTree(String folderId, int depth, BaseType typeFilter,
      boolean includeAllowableActions, IncludeRelationships includeRelationships, boolean includePathSegments,
      String propertyFilter, String renditionFilter) throws ObjectNotFoundException, InvalidArgumentException,
      FilterNotValidException, CmisRuntimeException
   {
      checkConnection();

      ObjectData folderData = storage.getObject(folderId);
      if (folderData == null)
         throw new ObjectNotFoundException("Object " + folderId + " does not exists.");
      if (folderData.getBaseType() != BaseType.FOLDER)
         throw new InvalidArgumentException("Can't get children. Object " + folderId + " is not a Folder.");
      List<ItemsTree<CmisObject>> tree = new ArrayList<ItemsTree<CmisObject>>();
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);
      for (ItemsIterator<ObjectData> children = storage.getChildren(folderData, null); children.hasNext();)
      {
         ObjectData child = children.next();
         if (typeFilter != null && child.getBaseType() != typeFilter)
            continue;
         CmisObject container =
            getCmisObject(child, includeAllowableActions, includeRelationships, false, false, parsedPropertyFilter,
               parsedRenditionFilter);
         List<ItemsTree<CmisObject>> subTree =
            (child.getBaseType() == BaseType.FOLDER && depth > 1) //
               ? getTree(child.getObjectId(), depth - 1, typeFilter, includeAllowableActions, includeRelationships,
                  includePathSegments, propertyFilter, renditionFilter) //
               : null;
         tree.add(new ItemsTreeImpl<CmisObject>(container, subTree));
      }
      return tree;
   }

   /**
    * {@inheritDoc}
    */
   public ItemsList<CmisObject> getCheckedOutDocs(String folderId, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, String propertyFilter, String renditionFilter, String orderBy,
      int maxItems, int skipCount) throws ObjectNotFoundException, InvalidArgumentException, FilterNotValidException,
      CmisRuntimeException
   {
      checkConnection();

      if (skipCount < 0)
         throw new InvalidArgumentException("skipCount parameter is negative.");

      ObjectData folderData = null;
      if (folderId != null)
      {
         folderData = storage.getObject(folderId);
         if (folderData == null)
            throw new ObjectNotFoundException("Fodler object " + folderId + " does not exists.");
         if (folderData.getBaseType() != BaseType.FOLDER)
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
         includeRelationships = IncludeRelationships.NONE;
      ItemsListImpl<CmisObject> checkedout = new ItemsListImpl<CmisObject>();
      int count = 0;
      while (iterator.hasNext() && (maxItems < 0 || count < maxItems))
      {
         ObjectData pwcData = iterator.next();
         CmisObject pwc =
            getCmisObject(pwcData, includeAllowableActions, includeRelationships, false, false, parsedPropertyFilter,
               parsedRenditionFilter);
         checkedout.getItems().add(pwc);
         count++;
      }
      checkedout.setHasMoreItems(iterator.hasNext());
      checkedout.setNumItems(iterator.size()); // ItemsIterator gives -1 if total number is unknown
      return checkedout;
   }

   // Relationships services -------

   /**
    * {@inheritDoc}
    */
   public ItemsList<CmisObject> getObjectRelationships(String objectId, RelationshipDirection direction, String typeId,
      boolean includeSubRelationshipTypes, boolean includeAllowableActions, String propertyFilter, int maxItems,
      int skipCount) throws FilterNotValidException, ObjectNotFoundException
   {
      checkConnection();

      if (skipCount < 0)
         throw new InvalidArgumentException("skipCount parameter is negative.");

      if (direction == null)
         direction = RelationshipDirection.SOURCE;

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
      ItemsListImpl<CmisObject> relationship = new ItemsListImpl<CmisObject>();
      long count = 0;
      while (iterator.hasNext() && (maxItems < 0 || count < maxItems))
      {
         ObjectData relationshipData = iterator.next();
         CmisObject cmis =
            getCmisObject(relationshipData, includeAllowableActions, null, false, false, parsedPropertyFilter,
               RenditionFilter.NONE);
         relationship.getItems().add(cmis);
         count++;
      }

      // Indicate we have some more results or not
      relationship.setHasMoreItems(iterator.hasNext());
      relationship.setNumItems(iterator.size()); // ItemsIterator gives -1 if total number is unknown
      return relationship;
   }

   //   private CmisPropertiesType getProperties(ObjectData objectData, PropertyFilter parsedPropertyFilter)
   //   {
   //      CmisPropertiesType properties = new CmisPropertiesType();
   //      for (Map.Entry<String, CmisProperty> e : objectData.getProperties(parsedPropertyFilter).entrySet())
   //         properties.getProperty().add(e.getValue());
   //      return properties;
   //   }

   // ------- Repository Services. (Type Manager) -------

   /**
    * {@inheritDoc}
    */
   public ItemsList<TypeDefinition> getTypeChildren(String typeId, boolean includePropertyDefinition, int maxItems,
      int skipCount) throws TypeNotFoundException, CmisRuntimeException
   {
      checkConnection();

      if (skipCount < 0)
         throw new InvalidArgumentException("skipCount parameter is negative.");

      ItemsIterator<TypeDefinition> iterator = storage.getTypeChildren(typeId, includePropertyDefinition);
      try
      {
         if (skipCount > 0)
            iterator.skip(skipCount);
      }
      catch (NoSuchElementException nse)
      {
         throw new InvalidArgumentException("skipCount parameter is greater then total number of argument");
      }

      ItemsListImpl<TypeDefinition> children = new ItemsListImpl<TypeDefinition>();
      int count = 0;
      while (iterator.hasNext() && (maxItems < 0 || count < maxItems))
      {
         TypeDefinition type = iterator.next();
         children.getItems().add(type);
         count++;
      }
      // Indicate that we have some more results.
      children.setHasMoreItems(iterator.hasNext());
      children.setNumItems(iterator.size()); // ItemsIterator gives -1 if total number is unknown
      return children;
   }

   /**
    * {@inheritDoc}
    */
   public TypeDefinition getTypeDefinition(String typeId) throws TypeNotFoundException, CmisRuntimeException
   {
      return getTypeDefinition(typeId, true);
   }

   /**
    * {@inheritDoc}
    */
   public TypeDefinition getTypeDefinition(String typeId, boolean includePropertyDefinition)
      throws TypeNotFoundException, CmisRuntimeException
   {
      checkConnection();

      return storage.getTypeDefinition(typeId, includePropertyDefinition);
   }

   /**
    * {@inheritDoc}
    */
   public void addType(TypeDefinition type) throws StorageException, CmisRuntimeException
   {
      checkConnection();

      storage.addType(type);
   }

   /**
    * {@inheritDoc}
    */
   public List<ItemsTree<TypeDefinition>> getTypeDescendants(String typeId, int depth, boolean includePropertyDefinition)
      throws TypeNotFoundException, CmisRuntimeException
   {
      checkConnection();

      List<ItemsTree<TypeDefinition>> tree = new ArrayList<ItemsTree<TypeDefinition>>();
      for (ItemsIterator<TypeDefinition> children = storage.getTypeChildren(typeId, includePropertyDefinition); children
         .hasNext();)
      {
         TypeDefinition container = children.next();
         List<ItemsTree<TypeDefinition>> subTree = (typeId != null && depth > 1) // 
            ? getTypeDescendants(container.getId(), depth - 1, includePropertyDefinition) //
            : null;
         tree.add(new ItemsTreeImpl<TypeDefinition>(container, subTree));
      }
      return tree;
   }

   /**
    * {@inheritDoc}
    */
   public void removeType(String typeId) throws TypeNotFoundException, ConstraintException, StorageException,
      CmisRuntimeException
   {
      checkConnection();

      storage.removeType(typeId);
   }

   // ------- Discovery Services -------

   /**
    * {@inheritDoc}
    */
   public ItemsList<CmisObject> getContentChanges(ChangeLogTokenHolder changeLogToken, boolean includeProperties,
      String propertyFilter, boolean includePolicyIDs, boolean includeAcl, int maxItems) throws ConstraintException,
      FilterNotValidException, CmisRuntimeException
   {
      checkConnection();

      CapabilityChanges capabilityChanges = storage.getRepositoryInfo().getCapabilities().getCapabilityChanges();
      if (capabilityChanges == CapabilityChanges.NONE)
         throw new NotSupportedException("Changes log feature is not supported.");
      if (changeLogToken == null)
         throw new CmisRuntimeException("Change log token holder may not be null.");

      ItemsIterator<ChangeEvent> iterator = storage.getChangeLog(changeLogToken.getToken());
      int count = 0;
      ItemsListImpl<CmisObject> changes = new ItemsListImpl<CmisObject>();
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
         changes.getItems().add(ch);
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
      IncludeRelationships includeRelationships, String renditionFilter, int maxItems, int skipCount)
      throws FilterNotValidException, CmisRuntimeException
   {
      checkConnection();

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
         includeRelationships = IncludeRelationships.NONE;
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
         list.getItems().add(object);
         count++;
      }

      // Indicate that we have some more results.
      list.setHasMoreItems(iterator.hasNext());
      long total = iterator.size();
      if (total != -1)
         list.setNumItems(BigInteger.valueOf(total));
      return list;
   }

   //---------

   protected void setACL(ObjectData objectData, List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL)
   {
      if ((addACL == null || addACL.size() == 0) && (removeACL == null || removeACL.size() == 0))
         return; // Nothing to do. 

      TypeDefinition typeDefinition = objectData.getTypeDefinition();
      if (!typeDefinition.isControllableACL())
         throw new ConstraintException("Type " + typeDefinition.getId() + " is not controllable by ACL.");

      // Merge ACL include existed one. It may be inherited from parent even for newly created object .
      List<AccessControlEntry> mergedACL = CmisUtils.mergeACLs(objectData.getACL(false), addACL, removeACL);
      // Update ACL
      objectData.setACL(mergedACL);
   }

   protected void validateChildObjectType(FolderData folder, String childTypeId)
   {
      String[] allowedChildTypes = folder.getIds(CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS);
      if (allowedChildTypes != null && allowedChildTypes.length > 0
         && !Arrays.asList(allowedChildTypes).contains(childTypeId))
      {
         throw new ConstraintException("Type " + childTypeId + " is not allowed as child for " + folder.getTypeId());
      }
   }

   protected void setContentStream(ObjectData document, ContentStream contentStream) throws StreamNotSupportedException
   {
      TypeDefinition typeDefinition = document.getTypeDefinition();

      ContentStreamAllowed contentStreamAllowed = typeDefinition.getContentStreamAllowed();
      if (contentStreamAllowed == ContentStreamAllowed.NOT_ALLOWED)
      {
         if (contentStream != null)
            throw new StreamNotSupportedException("Content is not allowed for type " + typeDefinition.getId());
      }
      else if (contentStreamAllowed == ContentStreamAllowed.REQUIRED)
      {
         if (contentStream == null)
            throw new ConstraintException("Content required for type " + typeDefinition.getId() + " but it is null.");
      }

      // Content may be null if it is allowed by type definition 
      ((DocumentData)document).setContentStream(contentStream);
   }

   protected void addPolicies(ObjectData objectData, List<String> policies)
   {
      if (policies == null || policies.size() == 0)
         return; // Nothing to do. 

      TypeDefinition typeDefinition = objectData.getTypeDefinition();
      if (!typeDefinition.isControllablePolicy())
         throw new ConstraintException("Type " + typeDefinition.getId() + " is not controllable by Policy.");

      for (String policyID : policies)
      {
         ObjectData policy = storage.getObject(policyID);
         if (policy == null)
            throw new ObjectNotFoundException("Policy " + policyID + " does not exists.");

         if (policy.getBaseType() == BaseType.POLICY)
            objectData.applyPolicy((PolicyData)policy);
         else
            throw new InvalidArgumentException("Object " + policyID + " is not a Policy object.");
      }
   }

   // -------

   protected abstract CmisObject getCmisObject(ObjectData object, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includePolicyIds, boolean includeAcl,
      PropertyFilter parsedPropertyFilter, RenditionFilter parsedRenditionFilter);

   protected abstract void validateChangeToken(ObjectData object, String changeToken) throws UpdateConflictException;

   /**
    * Check is connection may be used at the moment, e.g. it may be already
    * closed.
    * 
    * @throws IllegalStateException if connection may not be used any more
    */
   protected abstract void checkConnection() throws IllegalStateException;

}
