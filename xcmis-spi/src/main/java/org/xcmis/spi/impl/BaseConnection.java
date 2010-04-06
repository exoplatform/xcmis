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

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ChangeLogTokenHolder;
import org.xcmis.spi.ChangeTokenHolder;
import org.xcmis.spi.Connection;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentAlreadyExistsException;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.ItemsTree;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.NotSupportedException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.PropertyFilter;
import org.xcmis.spi.RenditionFilter;
import org.xcmis.spi.Storage;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.Document;
import org.xcmis.spi.data.Folder;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.data.Policy;
import org.xcmis.spi.data.Relationship;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.AccessControlPropagation;
import org.xcmis.spi.model.AllowableActions;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.CapabilityACL;
import org.xcmis.spi.model.CapabilityRendition;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.IncludeRelationships;
import org.xcmis.spi.model.ObjectParent;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.RelationshipDirection;
import org.xcmis.spi.model.Rendition;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.UnfileObject;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.model.impl.CmisObjectImpl;
import org.xcmis.spi.model.impl.DecimalProperty;
import org.xcmis.spi.model.impl.ObjectInfoImpl;
import org.xcmis.spi.model.impl.ObjectParentImpl;
import org.xcmis.spi.query.Query;
import org.xcmis.spi.query.Result;
import org.xcmis.spi.query.Score;
import org.xcmis.spi.utils.CmisUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: BaseConnection.java 332 2010-03-11 17:24:56Z andrew00x $
 */
public abstract class BaseConnection implements Connection
{

   private static final Log LOG = ExoLogger.getLogger(BaseConnection.class);

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
      InvalidArgumentException, ConstraintException
   {
      checkConnection();

      if (!storage.getRepositoryInfo().getCapabilities().isCapabilityMultifiling())
      {
         throw new NotSupportedException("Multi-filing is not supported.");
      }

      ObjectData object = storage.getObject(objectId);
      ObjectData folder = storage.getObject(folderId);

      if (folder.getBaseType() != BaseType.FOLDER)
      {
         throw new InvalidArgumentException("Object " + folderId + " is not a Folder object.");
      }

      if (!object.getTypeDefinition().isFileable())
      {
         throw new ConstraintException("Object " + objectId + " is not fileable.");
      }

      ((Folder)folder).addObject(object);
   }

   /**
    * {@inheritDoc}
    */
   public void removeObjectFromFolder(String objectId, String folderId) throws ObjectNotFoundException

   {
      checkConnection();

      if (!storage.getRepositoryInfo().getCapabilities().isCapabilityUnfiling())
      {
         throw new NotSupportedException("Unfiling is not supported.");
      }

      ObjectData object = storage.getObject(objectId);
      if (folderId != null)
      {
         ObjectData folder = storage.getObject(folderId);

         if (folder.getBaseType() != BaseType.FOLDER)
         {
            throw new InvalidArgumentException("Object " + folderId + " is not a Folder object.");
         }

         ((Folder)folder).removeObject(object);
      }
      else
      {
         storage.unfileObject(object);
      }
   }

   // ------- ACL Services -------

   /**
    * {@inheritDoc}
    */
   public void applyACL(String objectId, List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL,
      AccessControlPropagation propagation) throws ObjectNotFoundException, ConstraintException
   {
      if ((addACL == null || addACL.size() == 0) && (removeACL == null || removeACL.size() == 0))
      {
         return;
      }

      checkConnection();

      // TODO: check ACL propagation.
      ObjectData object = storage.getObject(objectId);
      applyACL(object, addACL, removeACL);

      storage.saveObject(object);
   }

   /**
    * {@inheritDoc}
    */
   public List<AccessControlEntry> getACL(String objectId, boolean onlyBasicPermissions) throws ObjectNotFoundException
   {
      checkConnection();

      if (storage.getRepositoryInfo().getCapabilities().getCapabilityACL() == CapabilityACL.NONE)
      {
         throw new NotSupportedException("ACL capability is not supported.");
      }

      ObjectData object = storage.getObject(objectId);

      List<AccessControlEntry> acl = object.getACL(onlyBasicPermissions);

      return acl;
   }

   // ------- Policy Services -------

   /**
    * {@inheritDoc}
    */
   public void applyPolicy(String policyId, String objectId) throws ConstraintException, ObjectNotFoundException
   {
      checkConnection();

      ObjectData object = storage.getObject(objectId);
      ObjectData policy = storage.getObject(policyId);
      if (policy.getBaseType() != BaseType.POLICY)
      {
         throw new InvalidArgumentException("Object " + policy.getObjectId() + " is not a Policy object.");
      }
      object.applyPolicy((Policy)policy);

      storage.saveObject(object);
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisObject> getAppliedPolicies(String objectId, boolean includeObjectInfo, String propertyFilter)
      throws ObjectNotFoundException, FilterNotValidException
   {
      checkConnection();

      ObjectData object = storage.getObject(objectId);

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      Collection<Policy> policies = object.getPolicies();
      List<CmisObject> policyIDs = new ArrayList<CmisObject>(policies.size());
      for (ObjectData policy : policies)
      {
         CmisObject cmisPolicy =
            getCmisObject(policy, false, IncludeRelationships.NONE, false, false, includeObjectInfo,
               parsedPropertyFilter, RenditionFilter.NONE);
         policyIDs.add(cmisPolicy);
      }
      return policyIDs;
   }

   /**
    * {@inheritDoc}
    */
   public void removePolicy(String policyId, String objectId) throws ConstraintException, ObjectNotFoundException
   {
      checkConnection();

      ObjectData object = storage.getObject(objectId);
      ObjectData policyData = storage.getObject(policyId);

      if (policyData.getBaseType() != BaseType.POLICY)
      {
         throw new InvalidArgumentException("Object " + policyId + " is not a Policy object.");
      }

      object.removePolicy((Policy)policyData);

      storage.saveObject(object);
   }

   // ------- Object Services --------

   /**
    * {@inheritDoc}
    */
   public String createDocument(String folderId, Map<String, Property<?>> properties, ContentStream content,
      List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL, Collection<String> policies,
      VersioningState versioningState) throws ObjectNotFoundException, ConstraintException, InvalidArgumentException,
      StreamNotSupportedException, NameConstraintViolationException, IOException, StorageException
   {
      if (properties == null)
      {
         throw new InvalidArgumentException("Properties may not by null.");
      }

      checkConnection();

      String typeId = null;
      Property<?> typeProperty = properties.get(CMIS.OBJECT_TYPE_ID);
      if (typeProperty != null && typeProperty.getValues().size() > 0)
      {
         typeId = (String)typeProperty.getValues().get(0);
      }
      if (typeId == null)
      {
         throw new InvalidArgumentException("Type is not specified.");
      }

      ObjectData folder = null;
      if (folderId != null)
      {
         folder = storage.getObject(folderId);
         if (folder.getBaseType() != BaseType.FOLDER)
         {
            throw new InvalidArgumentException("Object " + folderId + " is not a Folder object.");
         }
      }
      else if (!storage.getRepositoryInfo().getCapabilities().isCapabilityUnfiling())
      {
         throw new ConstraintException("Unfiling capability is not supported, parent folder must be provided.");
      }

      if (versioningState == null)
      {
         versioningState = VersioningState.MAJOR;
      }

      Document newDocument = storage.createDocument((Folder)folder, typeId, versioningState);

      newDocument.setProperties(properties);

      newDocument.setContentStream(content);

      if ((addACL != null && addACL.size() > 0) || (removeACL != null && removeACL.size() > 0))
      {
         applyACL(newDocument, addACL, removeACL);
      }

      if (policies != null && policies.size() > 0)
      {
         applyPolicies(newDocument, policies);
      }

      storage.saveObject(newDocument);

      return newDocument.getObjectId();
   }

   /**
    * {@inheritDoc}
    */
   public String createDocumentFromSource(String sourceId, String folderId, Map<String, Property<?>> properties,
      List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL, Collection<String> policies,
      VersioningState versioningState) throws ObjectNotFoundException, ConstraintException, InvalidArgumentException,
      NameConstraintViolationException, StorageException
   {
      checkConnection();

      ObjectData source = storage.getObject(sourceId);

      if (source.getBaseType() != BaseType.DOCUMENT)
      {
         throw new ConstraintException("Source object is not Document.");
      }

      ObjectData folder = null;
      if (folderId != null)
      {
         folder = storage.getObject(folderId);
         if (folder.getBaseType() != BaseType.FOLDER)
         {
            throw new InvalidArgumentException("Object " + folderId + " is not a Folder object.");
         }
      }
      else if (!storage.getRepositoryInfo().getCapabilities().isCapabilityUnfiling())
      {
         throw new ConstraintException("Unfiling capability is not supported, parent folder must be provided.");
      }

      Document newDocument = storage.createCopyOfDocument((Document)source, (Folder)folder, versioningState);

      if (properties != null)
      {
         newDocument.setProperties(properties);
      }

      if ((addACL != null && addACL.size() > 0) || (removeACL != null && removeACL.size() > 0))
      {
         applyACL(newDocument, addACL, removeACL);
      }

      if (policies != null && policies.size() > 0)
      {
         applyPolicies(newDocument, policies);
      }

      storage.saveObject(newDocument);

      return newDocument.getObjectId();
   }

   /**
    * {@inheritDoc}
    */
   public String createFolder(String folderId, Map<String, Property<?>> properties, List<AccessControlEntry> addACL,
      List<AccessControlEntry> removeACL, Collection<String> policies) throws ObjectNotFoundException,
      ConstraintException, InvalidArgumentException, NameConstraintViolationException, StorageException
   {
      if (properties == null)
      {
         throw new InvalidArgumentException("Properties may not by null.");
      }

      checkConnection();

      String typeId = null;
      Property<?> typeProperty = properties.get(CMIS.OBJECT_TYPE_ID);
      if (typeProperty != null && typeProperty.getValues().size() > 0)
      {
         typeId = (String)typeProperty.getValues().get(0);
      }
      if (typeId == null)
      {
         throw new InvalidArgumentException("Type is not specified.");
      }

      if (folderId == null)
      {
         throw new ConstraintException("Parent folder id is not specified.");
      }

      ObjectData folder = storage.getObject(folderId);
      if (folder.getBaseType() != BaseType.FOLDER)
      {
         throw new InvalidArgumentException("Object " + folderId + " is not a Folder object.");
      }

      ObjectData newFolder = storage.createFolder((Folder)folder, typeId);

      newFolder.setProperties(properties);

      if ((addACL != null && addACL.size() > 0) || (removeACL != null && removeACL.size() > 0))
      {
         applyACL(newFolder, addACL, removeACL);
      }

      if (policies != null && policies.size() > 0)
      {
         applyPolicies(newFolder, policies);
      }

      storage.saveObject(newFolder);

      return newFolder.getObjectId();
   }

   /**
    * {@inheritDoc}
    */
   public String createPolicy(String folderId, Map<String, Property<?>> properties, List<AccessControlEntry> addACL,
      List<AccessControlEntry> removeACL, Collection<String> policies) throws ObjectNotFoundException,
      ConstraintException, InvalidArgumentException, NameConstraintViolationException, StorageException
   {
      if (properties == null)
      {
         throw new InvalidArgumentException("Properties may not by null.");
      }

      checkConnection();

      String typeId = null;
      Property<?> typeProperty = properties.get(CMIS.OBJECT_TYPE_ID);
      if (typeProperty != null && typeProperty.getValues().size() > 0)
      {
         typeId = (String)typeProperty.getValues().get(0);
      }
      if (typeId == null)
      {
         throw new InvalidArgumentException("Type is not specified.");
      }

      ObjectData folder = null;
      if (folderId != null)
      {
         folder = storage.getObject(folderId);
         if (folder.getBaseType() != BaseType.FOLDER)
         {
            throw new InvalidArgumentException("Object " + folderId + " is not a Folder object.");
         }
      }
      else if (!storage.getRepositoryInfo().getCapabilities().isCapabilityUnfiling())
      {
         throw new ConstraintException("Unfiling capability is not supported, parent folder must be provided.");
      }

      ObjectData newPolicy = storage.createPolicy((Folder)folder, typeId);

      newPolicy.setProperties(properties);

      if ((addACL != null && addACL.size() > 0) || (removeACL != null && removeACL.size() > 0))
      {
         applyACL(newPolicy, addACL, removeACL);
      }

      if (policies != null && policies.size() > 0)
      {
         applyPolicies(newPolicy, policies);
      }

      storage.saveObject(newPolicy);

      return newPolicy.getObjectId();
   }

   /**
    * {@inheritDoc}
    */
   public String createRelationship(Map<String, Property<?>> properties, List<AccessControlEntry> addACL,
      List<AccessControlEntry> removeACL, Collection<String> policies) throws ObjectNotFoundException,
      ConstraintException, NameConstraintViolationException, StorageException
   {
      if (properties == null)
      {
         throw new InvalidArgumentException("Properties may not by null.");
      }

      checkConnection();

      String typeId = null;
      Property<?> typeProperty = properties.get(CMIS.OBJECT_TYPE_ID);
      if (typeProperty != null && typeProperty.getValues().size() > 0)
      {
         typeId = (String)typeProperty.getValues().get(0);
      }
      if (typeId == null)
      {
         throw new InvalidArgumentException("Required Type property ('cmis:objectTypeId') is not specified.");
      }

      String sourceId = null;
      Property<?> sourceProperty = properties.get(CMIS.SOURCE_ID);
      if (sourceProperty != null && sourceProperty.getValues().size() > 0)
      {
         sourceId = (String)sourceProperty.getValues().get(0);
      }
      if (sourceId == null)
      {
         throw new InvalidArgumentException("Required Source Id property ('cmis:sourceId') is not specified.");
      }

      String targetId = null;
      Property<?> targetProperty = properties.get(CMIS.TARGET_ID);
      if (targetProperty != null && targetProperty.getValues().size() > 0)
      {
         targetId = (String)targetProperty.getValues().get(0);
      }
      if (targetId == null)
      {
         throw new InvalidArgumentException("Required Target Id property ('cmis:targetId') is not specified.");
      }

      ObjectData newRelationship =
         storage.createRelationship(storage.getObject(sourceId), storage.getObject(targetId), typeId);

      newRelationship.setProperties(properties);

      if ((addACL != null && addACL.size() > 0) || (removeACL != null && removeACL.size() > 0))
      {
         applyACL(newRelationship, addACL, removeACL);
      }

      if (policies != null && policies.size() > 0)
      {
         applyPolicies(newRelationship, policies);
      }

      storage.saveObject(newRelationship);

      return newRelationship.getObjectId();
   }

   /**
    * {@inheritDoc}
    */
   public ContentStream getContentStream(String objectId, String streamId, long offset, long length)
      throws ObjectNotFoundException, ConstraintException
   {
      checkConnection();

      ObjectData object = storage.getObject(objectId);
      ContentStream contentStream = null;

      if (streamId != null)
      {
         contentStream = object.getContentStream(streamId);
      }
      else
      {
         contentStream = ((Document)object).getContentStream();
      }

      if (contentStream == null)
      {
         throw new ConstraintException("Object does not have content stream.");
      }

      return contentStream;
   }

   /**
    * {@inheritDoc}
    */
   public void deleteObject(String objectId, Boolean deleteAllVersions) throws ObjectNotFoundException,
      ConstraintException, UpdateConflictException, StorageException
   {
      checkConnection();

      ObjectData object = storage.getObject(objectId);

      if (deleteAllVersions == null)
      {
         deleteAllVersions = true; // Default.
      }

      storage.deleteObject(object, deleteAllVersions);
   }

   /**
    * {@inheritDoc}
    */
   public Collection<String> deleteTree(String folderId, Boolean deleteAllVersions, UnfileObject unfileObject,
      Boolean continueOnFailure) throws ObjectNotFoundException, UpdateConflictException
   {
      checkConnection();

      ObjectData folder = storage.getObject(folderId);

      if (folder.getBaseType() != BaseType.FOLDER)
      {
         throw new ConstraintException("Failed delete tree. Object " + folderId + " is not a Folder.");
      }

      if (((Folder)folder).isRoot())
      {
         throw new ConstraintException("Root folder can't be removed.");
      }

      if (unfileObject == null)
      {
         unfileObject = UnfileObject.DELETE; // Default value.
      }

      if (deleteAllVersions == null)
      {
         deleteAllVersions = true; // Default value.
      }

      if (continueOnFailure == null)
      {
         continueOnFailure = false;
      }

      // TODO : Check unfiling capability if 'unfileObject' is other then 'DELETE'

      Collection<String> failedDelete =
         storage.deleteTree((Folder)folder, deleteAllVersions, unfileObject, continueOnFailure);

      return failedDelete;
   }

   /**
    * {@inheritDoc}
    */
   public AllowableActions getAllowableActions(String objectId) throws ObjectNotFoundException
   {
      checkConnection();
      ObjectData object = storage.getObject(objectId);
      return storage.calculateAllowableActions(object);
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject getObject(String objectId, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includePolicyIDs, boolean includeACL,
      boolean includeObjectInfo, String propertyFilter, String renditionFilter) throws ObjectNotFoundException,
      FilterNotValidException
   {
      checkConnection();

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);
      if (includeRelationships == null)
      {
         includeRelationships = IncludeRelationships.NONE;
      }

      ObjectData objectData = storage.getObject(objectId);
      CmisObject cmisObject =
         getCmisObject(objectData, includeAllowableActions, includeRelationships, includePolicyIDs, includeACL,
            includeObjectInfo, parsedPropertyFilter, parsedRenditionFilter);

      return cmisObject;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject getObjectByPath(String path, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includePolicyIDs, boolean includeACL,
      boolean includeObjectInfo, String propertyFilter, String renditionFilter) throws ObjectNotFoundException,
      FilterNotValidException
   {
      checkConnection();

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);

      if (includeRelationships == null)
      {
         includeRelationships = IncludeRelationships.NONE; // Default.
      }

      ObjectData object = storage.getObjectByPath(path);

      CmisObject cmis =
         getCmisObject(object, includeAllowableActions, includeRelationships, includePolicyIDs, includeACL,
            includeObjectInfo, parsedPropertyFilter, parsedRenditionFilter);

      return cmis;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject getProperties(String objectId, boolean includeObjectInfo, String propertyFilter)
      throws ObjectNotFoundException, FilterNotValidException
   {
      checkConnection();

      ObjectData object = storage.getObject(objectId);
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);

      CmisObject cmis =
         getCmisObject(object, false, IncludeRelationships.NONE, false, false, includeObjectInfo, parsedPropertyFilter,
            RenditionFilter.NONE);

      return cmis;
   }

   /**
    * {@inheritDoc}
    */
   public String moveObject(String objectId, String targetFolderId, String sourceFolderId)
      throws ObjectNotFoundException, ConstraintException, InvalidArgumentException, UpdateConflictException,
      StorageException
   {
      checkConnection();

      ObjectData object = storage.getObject(objectId);

      ObjectData target = storage.getObject(targetFolderId);
      if (target.getBaseType() != BaseType.FOLDER)
      {
         throw new InvalidArgumentException("Object " + targetFolderId + " is not a Folder object.");
      }

      ObjectData source = storage.getObject(sourceFolderId);
      if (source.getBaseType() != BaseType.FOLDER)
      {
         throw new InvalidArgumentException("Object " + sourceFolderId + " is not a Folder object.");
      }

      boolean found = false;
      for (ObjectData one : object.getParents())
      {
         if (one.getObjectId().equals(sourceFolderId))
         {
            found = true;
         }
      }
      if (!found)
      {
         throw new InvalidArgumentException("Specified source folder " + sourceFolderId + " is not a parent of "
            + objectId);
      }

      ObjectData movedObject = storage.moveObject(object, (Folder)target, (Folder)source);

      return movedObject.getObjectId();
   }

   /**
    * {@inheritDoc}
    */
   public String deleteContentStream(String documentId, ChangeTokenHolder changeTokenHolder)
      throws ObjectNotFoundException, ConstraintException, UpdateConflictException, StorageException
   {
      if (changeTokenHolder == null)
      {
         throw new InvalidArgumentException("changeTokenHolder may not by null.");
      }

      checkConnection();

      ObjectData document = storage.getObject(documentId);

      if (document.getBaseType() != BaseType.DOCUMENT)
      {
         throw new InvalidArgumentException("Object " + documentId + " is not Document.");
      }

      // Validate change token, object may be already updated.
      validateChangeToken(document, changeTokenHolder.getValue());

      ((Document)document).setContentStream(null);

      storage.saveObject(document);

      String changeToken = document.getChangeToken();
      changeTokenHolder.setValue(changeToken);

      return document.getObjectId();
   }

   /**
    * {@inheritDoc}
    */
   public String setContentStream(String documentId, ContentStream content, ChangeTokenHolder changeTokenHolder,
      boolean overwriteFlag) throws ObjectNotFoundException, ContentAlreadyExistsException,
      StreamNotSupportedException, UpdateConflictException, IOException, StorageException
   {
      if (changeTokenHolder == null)
      {
         throw new InvalidArgumentException("changeTokenHolder may not by null.");
      }

      checkConnection();

      ObjectData document = storage.getObject(documentId);

      if (document.getBaseType() != BaseType.DOCUMENT)
      {
         throw new InvalidArgumentException("Object " + documentId + " is not Document.");
      }

      if (!overwriteFlag && ((Document)document).hasContent())
      {
         throw new ContentAlreadyExistsException("Document already has content stream and 'overwriteFlag' is false.");
      }

      // Validate change token, object may be already updated.
      validateChangeToken(document, changeTokenHolder.getValue());

      ((Document)document).setContentStream(content);

      storage.saveObject(document);

      String changeToken = document.getChangeToken();
      changeTokenHolder.setValue(changeToken);

      return document.getObjectId();
   }

   /**
    * {@inheritDoc}
    */
   public String updateProperties(String objectId, ChangeTokenHolder changeTokenHolder,
      Map<String, Property<?>> properties) throws ObjectNotFoundException, ConstraintException,
      NameConstraintViolationException, UpdateConflictException, StorageException
   {
      if (properties == null)
      {
         throw new InvalidArgumentException("Properties may not by null.");
      }

      if (changeTokenHolder == null)
      {
         throw new InvalidArgumentException("changeTokenHolder may not by null.");
      }

      checkConnection();

      ObjectData object = storage.getObject(objectId);

      // Validate change token, object may be already updated.
      validateChangeToken(object, changeTokenHolder.getValue());

      object.setProperties(properties);

      storage.saveObject(object);

      String changeToken = object.getChangeToken();
      changeTokenHolder.setValue(changeToken);

      return object.getObjectId();
   }

   /**
    * {@inheritDoc}
    */
   public List<Rendition> getRenditions(String objectId, String renditionFilter, int maxItems, int skipCount)
      throws ObjectNotFoundException, FilterNotValidException
   {
      checkConnection();

      if (storage.getRepositoryInfo().getCapabilities().getCapabilityRenditions() == CapabilityRendition.NONE)
      {
         throw new NotSupportedException("Renditions is not supported.");
      }

      if (skipCount < 0)
      {
         throw new InvalidArgumentException("skipCount parameter is negative.");
      }

      ObjectData objectData = storage.getObject(objectId);

      ItemsIterator<Rendition> iterator = storage.getRenditions(objectData);

      try
      {
         if (skipCount > 0)
         {
            iterator.skip(skipCount);
         }
      }
      catch (NoSuchElementException nse)
      {
         throw new InvalidArgumentException("'skipCount' parameter is greater then total number of argument");
      }

      List<Rendition> renditions = new ArrayList<Rendition>();
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);

      for (int count = 0; iterator.hasNext() && (maxItems < 0 || count < maxItems); count++)
      {
         Rendition r = iterator.next();
         if (parsedRenditionFilter.accept(r))
         {
            renditions.add(r);
         }
      }

      return renditions;
   }

   // ------- Versioning Services -------

   /**
    * {@inheritDoc}
    */
   public List<CmisObject> getAllVersions(String versionSeriesId, boolean includeAllowableActions,
      boolean includeObjectInfo, String propertyFilter) throws ObjectNotFoundException, FilterNotValidException
   {
      checkConnection();

      Collection<Document> versions = storage.getAllVersions(versionSeriesId);

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      List<CmisObject> cmisVersions = new ArrayList<CmisObject>();

      for (ObjectData objectData : versions)
      {
         cmisVersions.add(getCmisObject(objectData, includeAllowableActions, IncludeRelationships.NONE, false, false,
            includeObjectInfo, parsedPropertyFilter, RenditionFilter.NONE));
      }

      return cmisVersions;
   }

   /**
    * {@inheritDoc}
    */
   public void cancelCheckout(String documentId) throws ConstraintException, UpdateConflictException,
      VersioningException, StorageException
   {
      checkConnection();

      ObjectData document = storage.getObject(documentId);
      if (document.getBaseType() != BaseType.DOCUMENT)
      {
         throw new InvalidArgumentException("Object " + documentId + " is not a Document object.");
      }
      // cancelCheckedOut may be invoked on any object in version series.
      // In other way 'cmis:versionSeriesCheckedOutId' may not reflect
      // current PWC id.
      ((Document)document).cancelCheckout();
   }

   /**
    * {@inheritDoc}
    */
   public String checkin(String documentId, boolean major, Map<String, Property<?>> properties, ContentStream content,
      String checkinComment, List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL,
      Collection<String> policies) throws ConstraintException, UpdateConflictException, StreamNotSupportedException,
      IOException, StorageException
   {
      checkConnection();

      ObjectData pwc = storage.getObject(documentId);

      if (pwc.getBaseType() != BaseType.DOCUMENT)
      {
         throw new InvalidArgumentException("Object " + documentId + " is not a Document object.");
      }

      if (!((Document)pwc).isPWC())
      {
         throw new VersioningException("Object " + documentId + " is not Private Working Copy.");
      }

      if (properties != null)
      {
         pwc.setProperties(properties);
      }

      if (content != null)
      {
         ((Document)pwc).setContentStream(content);
      }

      if ((addACL != null && addACL.size() > 0) || (removeACL != null && removeACL.size() > 0))
      {
         applyACL(pwc, addACL, removeACL);
      }

      if (policies != null && policies.size() > 0)
      {
         applyPolicies(pwc, policies);
      }

      Document version = ((Document)pwc).checkin(major, checkinComment);

      return version.getObjectId();
   }

   /**
    * {@inheritDoc}
    */
   public String checkout(String documentId) throws ConstraintException, UpdateConflictException, VersioningException,
      StorageException
   {
      checkConnection();

      ObjectData document = storage.getObject(documentId);

      if (document.getBaseType() != BaseType.DOCUMENT)
      {
         throw new InvalidArgumentException("Object " + documentId + " is not a Document object.");
      }

      Document pwc = ((Document)document).checkout();

      return pwc.getObjectId();
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject getObjectOfLatestVersion(String versionSeriesId, boolean major, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includePolicyIDs, boolean includeACL,
      boolean includeObjectInfo, String propertyFilter, String renditionFilter) throws ObjectNotFoundException,
      FilterNotValidException
   {
      checkConnection();

      Collection<Document> versions = storage.getAllVersions(versionSeriesId);

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);

      if (includeRelationships == null)
      {
         includeRelationships = IncludeRelationships.NONE; // Default
      }

      // Even for not-versionable documents version series contains exactly one version of document.
      if (versions.size() == 1)
      {
         return getCmisObject(versions.iterator().next(), includeAllowableActions, includeRelationships, false, false,
            includeObjectInfo, parsedPropertyFilter, parsedRenditionFilter);
      }

      // Storage#getAllVersions(versionSeriesId) return sorted by
      // 'cmis:creationDate' descending. Latest version is version with latest
      // 'cmis:lastModificationDate'.
      List<Document> v = new ArrayList<Document>(versions);
      Collections.sort(v, CmisUtils.versionComparator);

      if (!major)
      {
         return getCmisObject(v.get(0), includeAllowableActions, includeRelationships, false, false, includeObjectInfo,
            parsedPropertyFilter, parsedRenditionFilter);
      }

      for (Document document : v)
      {
         if (document.isMajorVersion())
         {
            return getCmisObject(document, includeAllowableActions, includeRelationships, false, false,
               includeObjectInfo, parsedPropertyFilter, parsedRenditionFilter);
         }
      }

      // May happen only if major version requested but there is no any major version.
      throw new ObjectNotFoundException("Not found any major versions in version series.");
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject getPropertiesOfLatestVersion(String versionSeriesId, boolean major, boolean includeObjectInfo,
      String propertyFilter) throws FilterNotValidException, ObjectNotFoundException
   {
      return getObjectOfLatestVersion(versionSeriesId, major, false, null, false, false, includeObjectInfo,
         propertyFilter, RenditionFilter.NONE_FILTER);
   }

   // ------- Navigation Services -------

   /**
    * {@inheritDoc}
    */
   public ItemsList<CmisObject> getChildren(String folderId, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includePathSegments, boolean includeObjectInfo,
      String propertyFilter, String renditionFilter, String orderBy, int maxItems, int skipCount)
      throws ObjectNotFoundException, InvalidArgumentException, FilterNotValidException
   {
      checkConnection();

      if (skipCount < 0)
      {
         throw new InvalidArgumentException("skipCount parameter is negative.");
      }

      ObjectData folder = storage.getObject(folderId);
      if (folder.getBaseType() != BaseType.FOLDER)
      {
         throw new InvalidArgumentException("Can't get children. Object " + folderId + " is not a Folder.");
      }

      /* TODO : orderBy in some more usable form */
      ItemsIterator<ObjectData> iterator = ((Folder)folder).getChildren(orderBy);
      try
      {
         if (skipCount > 0)
         {
            iterator.skip(skipCount);
         }
      }
      catch (NoSuchElementException nse)
      {
         throw new InvalidArgumentException("'skipCount' parameter is greater then total number of argument");
      }

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);
      if (includeRelationships == null)
      {
         includeRelationships = IncludeRelationships.NONE; // Default
      }

      ItemsListImpl<CmisObject> cmisChildren = new ItemsListImpl<CmisObject>();
      for (int count = 0; iterator.hasNext() && (maxItems < 0 || count < maxItems); count++)
      {
         ObjectData chilData = iterator.next();

         CmisObject child =
            getCmisObject(chilData, includeAllowableActions, includeRelationships, false, false, includeObjectInfo,
               parsedPropertyFilter, parsedRenditionFilter);

         cmisChildren.getItems().add(child);
      }

      // Indicate that we have some more results.
      cmisChildren.setHasMoreItems(iterator.hasNext());
      cmisChildren.setNumItems(iterator.size()); // ItemsIterator gives -1 if total number is unknown.

      return cmisChildren;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject getFolderParent(String folderId, boolean includeObjectInfo, String propertyFilter)
      throws ObjectNotFoundException, InvalidArgumentException, FilterNotValidException
   {
      checkConnection();

      ObjectData folder = storage.getObject(folderId);
      if (folder.getBaseType() != BaseType.FOLDER)
      {
         throw new InvalidArgumentException("Object " + folderId + " is not a Folder.");
      }

      if (((Folder)folder).isRoot())
      {
         throw new InvalidArgumentException("Can't get parent of root folder.");
      }

      ObjectData parent = folder.getParent();

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);

      CmisObject cmisParent =
         getCmisObject(parent, false, IncludeRelationships.NONE, false, false, includeObjectInfo, parsedPropertyFilter,
            RenditionFilter.NONE);

      return cmisParent;
   }

   /**
    * {@inheritDoc}
    */
   public List<ObjectParent> getObjectParents(String objectId, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includeRelativePathSegment, boolean includeObjectInfo,
      String propertyFilter, String renditionFilter) throws ObjectNotFoundException, ConstraintException,
      FilterNotValidException
   {
      checkConnection();

      ObjectData object = storage.getObject(objectId);

      String typeId = object.getTypeId();
      TypeDefinition typeDefinition = getTypeDefinition(typeId, false);
      if (!typeDefinition.isFileable())
      {
         throw new ConstraintException("Can't get parents. Object " + objectId + " has type " + typeId
            + " that is not fileable");
      }

      Collection<Folder> parents = object.getParents();

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);

      if (includeRelationships == null)
      {
         includeRelationships = IncludeRelationships.NONE; // Default
      }

      List<ObjectParent> cmisParents = new ArrayList<ObjectParent>(parents.size());
      for (ObjectData parent : parents)
      {
         CmisObject cmisParent =
            getCmisObject(parent, includeAllowableActions, includeRelationships, false, false, includeObjectInfo,
               parsedPropertyFilter, parsedRenditionFilter);

         ObjectParent parentType =
            new ObjectParentImpl(cmisParent, includeRelativePathSegment ? parent.getName() : null);

         cmisParents.add(parentType);
      }

      return cmisParents;
   }

   /**
    * {@inheritDoc}
    */
   public List<ItemsTree<CmisObject>> getDescendants(String folderId, int depth, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includePathSegments, boolean includeObjectInfo,
      String propertyFilter, String renditionFilter) throws ObjectNotFoundException, InvalidArgumentException,
      FilterNotValidException
   {
      if (depth != -1 && !(depth >= 1))
      {
         throw new InvalidArgumentException("Invalid depth parameter. Must be 1 or greater then 1 or -1 but " + depth
            + " specified.");
      }

      checkConnection();

      return getTree(folderId, depth, null, includeAllowableActions, includeRelationships, includePathSegments,
         includeObjectInfo, propertyFilter, renditionFilter);
   }

   /**
    * {@inheritDoc}
    */
   public List<ItemsTree<CmisObject>> getFolderTree(String folderId, int depth, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includePathSegments, boolean includeObjectInfo,
      String propertyFilter, String renditionFilter) throws ObjectNotFoundException, InvalidArgumentException,
      FilterNotValidException
   {
      if (depth != -1 && !(depth >= 1))
      {
         throw new InvalidArgumentException("Invalid depth parameter. Must be 1 or greater then 1 or -1 but " + depth
            + " specified.");
      }

      checkConnection();

      return getTree(folderId, depth, BaseType.FOLDER, includeAllowableActions, includeRelationships,
         includePathSegments, includeObjectInfo, propertyFilter, renditionFilter);
   }

   protected List<ItemsTree<CmisObject>> getTree(String folderId, int depth, BaseType typeFilter,
      boolean includeAllowableActions, IncludeRelationships includeRelationships, boolean includePathSegments,
      boolean includeObjectInfo, String propertyFilter, String renditionFilter) throws ObjectNotFoundException,
      InvalidArgumentException, FilterNotValidException
   {
      ObjectData folder = storage.getObject(folderId);

      if (folder.getBaseType() != BaseType.FOLDER)
      {
         throw new InvalidArgumentException("Can't get children. Object " + folderId + " is not a Folder.");
      }

      List<ItemsTree<CmisObject>> tree = new ArrayList<ItemsTree<CmisObject>>();
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);

      for (ItemsIterator<ObjectData> children = ((Folder)folder).getChildren(null); children.hasNext();)
      {
         ObjectData child = children.next();

         if (typeFilter != null && child.getBaseType() != typeFilter)
         {
            continue;
         }

         CmisObject container =
            getCmisObject(child, includeAllowableActions, includeRelationships, false, false, includeObjectInfo,
               parsedPropertyFilter, parsedRenditionFilter);

         List<ItemsTree<CmisObject>> subTree =
            (child.getBaseType() == BaseType.FOLDER && (depth > 1 || depth == -1)) //
               ? getTree(child.getObjectId(), depth != -1 ? depth - 1 : depth, typeFilter, includeAllowableActions,
                  includeRelationships, includePathSegments, includeObjectInfo, propertyFilter, renditionFilter) //
               : null;

         tree.add(new ItemsTreeImpl<CmisObject>(container, subTree));
      }

      return tree;
   }

   /**
    * {@inheritDoc}
    */
   public ItemsList<CmisObject> getCheckedOutDocs(String folderId, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includeObjectInfo, String propertyFilter,
      String renditionFilter, String orderBy, int maxItems, int skipCount) throws ObjectNotFoundException,
      InvalidArgumentException, FilterNotValidException
   {
      checkConnection();

      if (skipCount < 0)
      {
         throw new InvalidArgumentException("skipCount parameter is negative.");
      }

      ObjectData folder = null;

      if (folderId != null)
      {
         folder = storage.getObject(folderId);

         if (folder.getBaseType() != BaseType.FOLDER)
         {
            throw new InvalidArgumentException("Can't get checkedout documents. Object " + folderId
               + " is not a Folder.");
         }
      }

      ItemsIterator<ObjectData> iterator = storage.getCheckedOutDocuments(folder, orderBy);

      try
      {
         if (skipCount > 0)
         {
            iterator.skip(skipCount);
         }
      }
      catch (NoSuchElementException nse)
      {
         throw new InvalidArgumentException("skipCount parameter is greater then total number of argument");
      }

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);
      if (includeRelationships == null)
      {
         includeRelationships = IncludeRelationships.NONE; // Default.
      }

      ItemsListImpl<CmisObject> checkedout = new ItemsListImpl<CmisObject>();

      for (int count = 0; iterator.hasNext() && (maxItems < 0 || count < maxItems); count++)
      {
         ObjectData pwcData = iterator.next();

         CmisObject pwc =
            getCmisObject(pwcData, includeAllowableActions, includeRelationships, false, false, includeObjectInfo,
               parsedPropertyFilter, parsedRenditionFilter);

         checkedout.getItems().add(pwc);
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
      boolean includeSubRelationshipTypes, boolean includeAllowableActions, boolean includeObjectInfo,
      String propertyFilter, int maxItems, int skipCount) throws FilterNotValidException, ObjectNotFoundException
   {
      checkConnection();

      if (skipCount < 0)
      {
         throw new InvalidArgumentException("skipCount parameter is negative.");
      }

      if (direction == null)
      {
         direction = RelationshipDirection.SOURCE; // Default
      }

      TypeDefinition type = getTypeDefinition(typeId == null ? BaseType.RELATIONSHIP.value() : typeId);

      if (type.getBaseId() != BaseType.RELATIONSHIP)
      {
         throw new InvalidArgumentException("Type " + typeId + " is not Relationship type.");
      }

      ObjectData object = storage.getObject(objectId);

      ItemsIterator<Relationship> iterator = object.getRelationships(direction, type, includeSubRelationshipTypes);

      try
      {
         if (skipCount > 0)
         {
            iterator.skip(skipCount);
         }
      }
      catch (NoSuchElementException nse)
      {
         throw new InvalidArgumentException("skipCount parameter is greater then total number of argument");
      }

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      ItemsListImpl<CmisObject> relationships = new ItemsListImpl<CmisObject>();

      for (int count = 0; iterator.hasNext() && (maxItems < 0 || count < maxItems); count++)
      {
         ObjectData rel = iterator.next();

         CmisObject cmis =
            getCmisObject(rel, includeAllowableActions, null, false, false, includeObjectInfo, parsedPropertyFilter,
               RenditionFilter.NONE);

         relationships.getItems().add(cmis);
      }

      // Indicate we have some more results or not
      relationships.setHasMoreItems(iterator.hasNext());
      relationships.setNumItems(iterator.size()); // ItemsIterator gives -1 if total number is unknown

      return relationships;
   }

   // ------- Repository Services. (Type Manager) -------

   /**
    * {@inheritDoc}
    */
   public ItemsList<TypeDefinition> getTypeChildren(String typeId, boolean includePropertyDefinition, int maxItems,
      int skipCount) throws TypeNotFoundException
   {
      checkConnection();

      if (skipCount < 0)
      {
         throw new InvalidArgumentException("skipCount parameter is negative.");
      }

      ItemsIterator<TypeDefinition> iterator = storage.getTypeChildren(typeId, includePropertyDefinition);
      try
      {
         if (skipCount > 0)
         {
            iterator.skip(skipCount);
         }
      }
      catch (NoSuchElementException nse)
      {
         throw new InvalidArgumentException("skipCount parameter is greater then total number of argument");
      }

      ItemsListImpl<TypeDefinition> typeChildren = new ItemsListImpl<TypeDefinition>();

      for (int count = 0; iterator.hasNext() && (maxItems < 0 || count < maxItems); count++)
      {
         TypeDefinition type = iterator.next();
         typeChildren.getItems().add(type);
      }
      // Indicate that we have some more results.
      typeChildren.setHasMoreItems(iterator.hasNext());
      typeChildren.setNumItems(iterator.size()); // ItemsIterator gives -1 if total number is unknown

      return typeChildren;
   }

   /**
    * {@inheritDoc}
    */
   public TypeDefinition getTypeDefinition(String typeId) throws TypeNotFoundException
   {
      return getTypeDefinition(typeId, true);
   }

   /**
    * {@inheritDoc}
    */
   public TypeDefinition getTypeDefinition(String typeId, boolean includePropertyDefinition)
      throws TypeNotFoundException
   {
      checkConnection();

      return storage.getTypeDefinition(typeId, includePropertyDefinition);
   }

   /**
    * {@inheritDoc}
    */
   public String addType(TypeDefinition type) throws StorageException
   {
      checkConnection();

      String id = storage.addType(type);
      return id;
   }

   /**
    * {@inheritDoc}
    */
   public List<ItemsTree<TypeDefinition>> getTypeDescendants(String typeId, int depth, boolean includePropertyDefinition)
      throws TypeNotFoundException
   {
      if (depth != -1 && !(depth >= 1))
      {
         throw new InvalidArgumentException("Invalid depth parameter. Must be 1 or greater then 1 or -1 but " + depth
            + " specified.");
      }

      checkConnection();

      return getTypeTree(typeId, depth, includePropertyDefinition);
   }

   protected List<ItemsTree<TypeDefinition>> getTypeTree(String typeId, int depth, boolean includePropertyDefinition)
      throws TypeNotFoundException
   {
      List<ItemsTree<TypeDefinition>> tree = new ArrayList<ItemsTree<TypeDefinition>>();

      for (ItemsIterator<TypeDefinition> children = storage.getTypeChildren(typeId, includePropertyDefinition); children
         .hasNext();)
      {
         TypeDefinition childType = children.next();

         List<ItemsTree<TypeDefinition>> subTree = (depth > 1 || depth == -1) //
            ? getTypeDescendants(childType.getId(), depth != -1 ? depth - 1 : depth, includePropertyDefinition) //
            : null;

         tree.add(new ItemsTreeImpl<TypeDefinition>(childType, subTree));
      }

      return tree;
   }

   /**
    * {@inheritDoc}
    */
   public void removeType(String typeId) throws TypeNotFoundException, ConstraintException, StorageException
   {
      checkConnection();

      storage.removeType(typeId);
   }

   // ------- Discovery Services -------

   /**
    * {@inheritDoc}
    */
   public ItemsList<CmisObject> getContentChanges(ChangeLogTokenHolder changeLogToken, boolean includeProperties,
      String propertyFilter, boolean includePolicyIDs, boolean includeACL, boolean includeObjectInfo, int maxItems)
      throws ConstraintException, FilterNotValidException
   {
      // TODO : implement
      throw new NotSupportedException("Changes log feature is not supported.");
   }

   /**
    * {@inheritDoc}
    */
   public ItemsList<CmisObject> query(String statement, boolean searchAllVersions, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includeObjectInfo, String renditionFilter, int maxItems,
      int skipCount) throws FilterNotValidException
   {
      checkConnection();

      if (skipCount < 0)
      {
         throw new InvalidArgumentException("skipCount parameter is negative.");
      }

      ItemsIterator<Result> iterator = storage.query(new Query(statement, searchAllVersions));

      try
      {
         if (skipCount > 0)
         {
            iterator.skip(skipCount);
         }
      }
      catch (NoSuchElementException nse)
      {
         String msg = "skipCount parameter is greater then total number of argument";
         throw new InvalidArgumentException(msg);
      }

      if (includeRelationships == null)
      {
         includeRelationships = IncludeRelationships.NONE; // Default.
      }

      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);

      ItemsListImpl<CmisObject> list = new ItemsListImpl<CmisObject>();

      for (int count = 0; iterator.hasNext() && (maxItems < 0 || count < maxItems); count++)
      {
         Result result = iterator.next();
         StringBuilder propertyFilter = new StringBuilder();
         if (result.getPropertyNames() != null)
         {
            for (String s : result.getPropertyNames())
            {
               if (propertyFilter.length() > 0)
               {
                  propertyFilter.append(',');
               }
               propertyFilter.append(s);
            }
         }

         ObjectData data = null;
         try
         {
            data = storage.getObject(result.getObjectId());
         }
         catch (ObjectNotFoundException e)
         {
            LOG.warn("Object " + result.getObjectId() + " was removed.");
         }

         CmisObject object =
            getCmisObject(data, includeAllowableActions, includeRelationships, false, false, includeObjectInfo,
               new PropertyFilter(propertyFilter.toString()), parsedRenditionFilter);

         Score score = result.getScore();
         if (score != null)
         {
            String scoreColumnName = score.getScoreColumnName();
            DecimalProperty scoreProperty =
               new DecimalProperty(scoreColumnName, scoreColumnName, scoreColumnName, scoreColumnName, Collections
                  .singletonList(score.getScoreValue()));
            object.getProperties().put(scoreColumnName, scoreProperty);
         }
         list.getItems().add(object);
      }

      // Indicate that we have some more results.
      list.setHasMoreItems(iterator.hasNext());
      list.setNumItems(iterator.size()); // ItemsIterator gives -1 if total number is unknown

      return list;
   }

   //

   /**
    * {@inheritDoc}
    */
   public Storage getStorage()
   {
      return storage;
   }

   //---------

   protected CmisObject getCmisObject(ObjectData object, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includePolicyIds, boolean includeACL,
      boolean includeObjectInfo, PropertyFilter parsedPropertyFilter, RenditionFilter parsedRenditionFilter)
   {
      CmisObjectImpl cmis = new CmisObjectImpl();

      Map<String, Property<?>> properties = object.getSubset(parsedPropertyFilter);
      if (properties.size() != 0)
      {
         cmis.getProperties().putAll(properties);
      }

      if (includeAllowableActions)
      {
         cmis.setAllowableActions(storage.calculateAllowableActions(object));
      }

      RelationshipDirection direction = null;
      if (includeRelationships != null)
      {
         switch (includeRelationships)
         {
            case BOTH :
               direction = RelationshipDirection.EITHER;
               break;
            case SOURCE :
               direction = RelationshipDirection.SOURCE;
               break;
            case TARGET :
               direction = RelationshipDirection.TARGET;
               break;
            case NONE :
               break;
         }
         if (direction != null)
         {
            for (ItemsIterator<Relationship> iter = object.getRelationships(direction, null, true); iter.hasNext();)
            {
               Relationship next = iter.next();
               cmis.getRelationship().add(
                  getCmisObject(next, false, includeRelationships, false, false, includeObjectInfo, PropertyFilter.ALL,
                     RenditionFilter.NONE));
            }
         }
      }
      if (includePolicyIds)
      {
         for (Iterator<Policy> iter = object.getPolicies().iterator(); iter.hasNext();)
         {
            cmis.getPolicyIds().add(iter.next().getObjectId());
         }
      }

      if (includeACL)
      {
         for (Iterator<AccessControlEntry> iter = object.getACL(true).iterator(); iter.hasNext();)
         {
            cmis.getACL().add(iter.next());
         }
      }

      if (!parsedRenditionFilter.isNone())
      {
         for (ItemsIterator<Rendition> renditions = storage.getRenditions(object); renditions.hasNext();)
         {
            Rendition r = renditions.next();
            if (parsedRenditionFilter.accept(r))
            {
               cmis.getRenditions().add(r);
            }
         }
      }

      if (includeObjectInfo)
      {
         BaseType baseType = object.getBaseType();

         ObjectInfoImpl objectInfo = new ObjectInfoImpl();
         objectInfo.setBaseType(baseType);
         objectInfo.setTypeId(object.getTypeId());
         objectInfo.setId(object.getObjectId());
         objectInfo.setName(object.getName());
         objectInfo.setCreatedBy(object.getCreatedBy());
         objectInfo.setCreationDate(object.getCreationDate());
         objectInfo.setLastModifiedBy(object.getLastModifiedBy());
         objectInfo.setLastModificationDate(object.getLastModificationDate());
         objectInfo.setChangeToken(object.getChangeToken());
         if (baseType == BaseType.FOLDER)
         {
            objectInfo.setParentId(((Folder)object).isRoot() ? null : object.getParent().getObjectId());
         }
         else if (baseType == BaseType.DOCUMENT)
         {
            Document doc = (Document)object;
            objectInfo.setLatestVersion(doc.isLatestVersion());
            objectInfo.setMajorVersion(doc.isMajorVersion());
            objectInfo.setLatestMajorVersion(doc.isLatestMajorVersion());
            objectInfo.setVersionSeriesId(doc.getVersionSeriesId());
            objectInfo.setVersionSeriesCheckedOutId(doc.getVersionSeriesCheckedOutId());
            objectInfo.setVersionSeriesCheckedOutBy(doc.getVersionSeriesCheckedOutBy());
            objectInfo.setVersionLabel(doc.getVersionLabel());
            objectInfo.setContentStreamMimeType(doc.getContentStreamMimeType());;
         }
         else if (baseType == BaseType.RELATIONSHIP)
         {
            Relationship rel = (Relationship)object;
            objectInfo.setSourceId(rel.getSourceId());
            objectInfo.setTargetId(rel.getTargetId());
         }

         cmis.setObjectInfo(objectInfo);
      }
      return cmis;
   }

   /**
    * Check is connection may be used at the moment, e.g. it may be already
    * closed.
    *
    * @throws IllegalStateException if connection may not be used any more
    */
   protected abstract void checkConnection() throws IllegalStateException;

   /**
    * Validate change token provided by caller with current change token of
    * object.
    *
    * @param object object
    * @param changeToken change token from 'client'
    * @throws UpdateConflictException if specified change token does not match
    *         to object change token
    */
   protected abstract void validateChangeToken(ObjectData object, String changeToken) throws UpdateConflictException;

   // ------------------------------- Helpers ---------------------------

   private void applyPolicies(ObjectData object, Collection<String> policies)
   {
      TypeDefinition typeDefinition = object.getTypeDefinition();
      if (!typeDefinition.isControllablePolicy())
      {
         throw new ConstraintException("Type " + typeDefinition.getId() + " is not controllable by Policy.");
      }

      for (String policyID : policies)
      {
         ObjectData policy = storage.getObject(policyID);
         if (policy.getBaseType() != BaseType.POLICY)
         {
            throw new InvalidArgumentException("Object " + policyID + " is not a Policy object.");
         }
         object.applyPolicy((Policy)policy);
      }
   }

   /**
    * Apply ACLs to specified object.
    *
    * @param object object
    * @param addACL ACL to be added
    * @param removeACL ACL to be removed
    */
   private void applyACL(ObjectData object, List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL)
   {
      CapabilityACL capabilityACL = storage.getRepositoryInfo().getCapabilities().getCapabilityACL();

      if (capabilityACL == CapabilityACL.NONE)
      {
         throw new NotSupportedException("ACL capability is not supported.");
      }
      else if (capabilityACL == CapabilityACL.DISCOVER)
      {
         throw new NotSupportedException("ACL can be discovered but not managed via CMIS services.");
      }

      TypeDefinition typeDefinition = object.getTypeDefinition();
      if (!typeDefinition.isControllableACL())
      {
         throw new ConstraintException("Type " + typeDefinition.getId() + " is not controllable by ACL.");
      }

      // Merge ACL include existed one. It may be inherited from parent even for newly created object .
      List<AccessControlEntry> mergedACL = CmisUtils.mergeACLs(object.getACL(false), addACL, removeACL);

      object.setACL(mergedACL);
   }

}
