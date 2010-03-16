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
import org.xcmis.spi.AccessControlEntry;
import org.xcmis.spi.AccessControlPropagation;
import org.xcmis.spi.AllowableActions;
import org.xcmis.spi.BaseType;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.CapabilityACL;
import org.xcmis.spi.ChangeLogTokenHolder;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.Connection;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentAlreadyExistsException;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.IncludeRelationships;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.ItemsTree;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.NotSupportedException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.RelationshipDirection;
import org.xcmis.spi.Rendition;
import org.xcmis.spi.Storage;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.TypeDefinition;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.UnfileObject;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.VersioningState;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.DocumentData;
import org.xcmis.spi.data.FolderData;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.data.PolicyData;
import org.xcmis.spi.data.RelationshipData;
import org.xcmis.spi.object.CmisObject;
import org.xcmis.spi.object.ObjectParent;
import org.xcmis.spi.object.Properties;
import org.xcmis.spi.object.impl.DecimalProperty;
import org.xcmis.spi.object.impl.ObjectParentImpl;
import org.xcmis.spi.query.Query;
import org.xcmis.spi.query.Result;
import org.xcmis.spi.query.Score;
import org.xcmis.spi.utils.CmisUtils;

import java.io.IOException;
import java.util.ArrayList;
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
      InvalidArgumentException, ConstraintException, CmisRuntimeException
   {
      checkConnection();

      if (!storage.getRepositoryInfo().getCapabilities().isCapabilityMultifiling())
         throw new NotSupportedException("Multi-filing is not supported.");

      ObjectData object = storage.getObject(objectId);
      ObjectData folder = storage.getObject(folderId);

      if (folder.getBaseType() != BaseType.FOLDER)
         throw new InvalidArgumentException("Object " + folderId + " is not a Folder object.");

      if (!object.getTypeDefinition().isFileable())
         throw new ConstraintException("Object " + objectId + " is not fileable.");

      ((FolderData)folder).addObject(object);
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
         throw new InvalidArgumentException("Object " + folderId + " is not a Folder object.");

      ((FolderData)folder).removeObject(object);
   }

   // ------- ACL Services -------

   /**
    * {@inheritDoc}
    */
   public void applyACL(String objectId, List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL,
      AccessControlPropagation propagation) throws ObjectNotFoundException, ConstraintException, CmisRuntimeException
   {
      if ((addACL == null || addACL.size() == 0) && (removeACL == null || removeACL.size() == 0))
         return;

      checkConnection();

      // TODO: check ACL propagation.
      ObjectData object = storage.getObject(objectId);
      applyACL(object, addACL, removeACL);
      storage.saveObject(object);
   }

   /**
    * {@inheritDoc}
    */
   public List<AccessControlEntry> getACL(String objectId, boolean onlyBasicPermissions)
      throws ObjectNotFoundException, CmisRuntimeException
   {
      checkConnection();

      if (storage.getRepositoryInfo().getCapabilities().getCapabilityACL() == CapabilityACL.NONE)
         throw new NotSupportedException("ACL capability is not supported.");

      ObjectData object = storage.getObject(objectId);

      List<AccessControlEntry> acl = object.getACL(onlyBasicPermissions);

      return acl;
   }

   // ------- Policy Services -------

   /**
    * {@inheritDoc}
    */
   public void applyPolicy(String policyId, String objectId) throws ConstraintException, ObjectNotFoundException,
      CmisRuntimeException
   {
      checkConnection();

      ObjectData object = storage.getObject(objectId);
      ObjectData policy = storage.getObject(policyId);
      if (policy.getBaseType() != BaseType.POLICY)
         throw new InvalidArgumentException("Object " + policy.getObjectId() + " is not a Policy object.");
      object.applyPolicy((PolicyData)policy);
      storage.saveObject(object);
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisObject> getAppliedPolicies(String objectId, String propertyFilter) throws ObjectNotFoundException,
      FilterNotValidException, CmisRuntimeException
   {
      checkConnection();

      ObjectData object = storage.getObject(objectId);

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      Collection<PolicyData> policies = object.getPolicies();
      List<CmisObject> policyIDs = new ArrayList<CmisObject>(policies.size());
      for (ObjectData policy : policies)
      {
         CmisObject cmisPolicy =
            getCmisObject(policy, false, IncludeRelationships.NONE, false, false, parsedPropertyFilter,
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
      ObjectData policyData = storage.getObject(policyId);

      if (policyData.getBaseType() != BaseType.POLICY)
         throw new InvalidArgumentException("Object " + policyId + " is not a Policy object.");

      objectData.removePolicy((PolicyData)policyData);

      storage.saveObject(objectData);
   }

   // ------- Object Services --------

   /**
    * {@inheritDoc}
    */
   public CmisObject createDocument(String folderId, Properties propertyData, ContentStream content,
      List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL, List<String> policies,
      VersioningState versioningState) throws ObjectNotFoundException, ConstraintException, InvalidArgumentException,
      StreamNotSupportedException, NameConstraintViolationException, IOException, StorageException,
      CmisRuntimeException
   {
      if (propertyData == null)
         throw new InvalidArgumentException("Properties may not by null.");

      checkConnection();

      String typeId = propertyData.getId(CMIS.OBJECT_TYPE_ID);
      if (typeId == null)
         throw new InvalidArgumentException("Type is not specified.");

      ObjectData folder = null;
      if (folderId != null)
      {
         folder = storage.getObject(folderId);
         if (folder.getBaseType() != BaseType.FOLDER)
            throw new InvalidArgumentException("Object " + folderId + " is not a Folder object.");
      }
      else if (!storage.getRepositoryInfo().getCapabilities().isCapabilityUnfiling())
      {
         throw new ConstraintException("Unfiling capability is not supported, parent folder must be provided.");
      }

      if (versioningState == null)
         versioningState = VersioningState.MAJOR;

      DocumentData newDocument = storage.createDocument((FolderData)folder, typeId, versioningState);

      newDocument.getProperties().setProperties(propertyData.getAll());

      newDocument.setContentStream(content);

      if ((addACL != null && addACL.size() > 0) || (removeACL != null && removeACL.size() > 0))
         applyACL(newDocument, addACL, removeACL);

      if (policies != null && policies.size() > 0)
         applyPolicies(newDocument, policies);

      storage.saveObject(newDocument);

      CmisObject cmis =
         getCmisObject(newDocument, false, IncludeRelationships.NONE, false, true, PropertyFilter.ALL,
            RenditionFilter.NONE);

      return cmis;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject createDocumentFromSource(String sourceId, String folderId, Properties propertyData,
      List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL, List<String> policies,
      VersioningState versioningState) throws ObjectNotFoundException, ConstraintException, InvalidArgumentException,
      NameConstraintViolationException, StorageException, CmisRuntimeException
   {
      checkConnection();

      ObjectData source = storage.getObject(sourceId);

      if (source.getBaseType() != BaseType.DOCUMENT)
         throw new ConstraintException("Source object is not Document.");

      ObjectData folder = null;
      if (folderId != null)
      {
         folder = storage.getObject(folderId);
         if (folder.getBaseType() != BaseType.FOLDER)
            throw new InvalidArgumentException("Object " + folderId + " is not a Folder object.");
      }
      else if (!storage.getRepositoryInfo().getCapabilities().isCapabilityUnfiling())
      {
         throw new ConstraintException("Unfiling capability is not supported, parent folder must be provided.");
      }

      DocumentData newDocument =
         storage.createCopyOfDocument((DocumentData)source, (FolderData)folder, versioningState);

      if (propertyData != null)
         newDocument.getProperties().setProperties(propertyData.getAll());

      if ((addACL != null && addACL.size() > 0) || (removeACL != null && removeACL.size() > 0))
         applyACL(newDocument, addACL, removeACL);

      if (policies != null && policies.size() > 0)
         applyPolicies(newDocument, policies);

      storage.saveObject(newDocument);

      CmisObject cmis =
         getCmisObject(newDocument, false, IncludeRelationships.NONE, false, true, PropertyFilter.ALL,
            RenditionFilter.NONE);

      return cmis;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject createFolder(String folderId, Properties propertiesData, List<AccessControlEntry> addACL,
      List<AccessControlEntry> removeACL, List<String> policies) throws ObjectNotFoundException, ConstraintException,
      InvalidArgumentException, NameConstraintViolationException, StorageException, CmisRuntimeException
   {
      if (propertiesData == null)
         throw new InvalidArgumentException("Properties may not by null.");

      checkConnection();

      String typeId = propertiesData.getId(CMIS.OBJECT_TYPE_ID);
      if (typeId == null)
         throw new InvalidArgumentException("Type is not specified.");

      if (folderId == null)
         throw new ConstraintException("Parent folder id is not specified.");

      ObjectData folder = storage.getObject(folderId);
      if (folder.getBaseType() != BaseType.FOLDER)
         throw new InvalidArgumentException("Object " + folderId + " is not a Folder object.");

      ObjectData newFolder = storage.createFolder((FolderData)folder, typeId);

      newFolder.getProperties().setProperties(propertiesData.getAll());

      if ((addACL != null && addACL.size() > 0) || (removeACL != null && removeACL.size() > 0))
         applyACL(newFolder, addACL, removeACL);

      if (policies != null && policies.size() > 0)
         applyPolicies(newFolder, policies);

      storage.saveObject(newFolder);

      CmisObject cmis =
         getCmisObject(newFolder, false, IncludeRelationships.NONE, false, true, PropertyFilter.ALL,
            RenditionFilter.NONE);

      return cmis;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject createPolicy(String folderId, Properties propertyData, List<AccessControlEntry> addACL,
      List<AccessControlEntry> removeACL, List<String> policies) throws ObjectNotFoundException, ConstraintException,
      InvalidArgumentException, NameConstraintViolationException, StorageException, CmisRuntimeException
   {
      if (propertyData == null)
         throw new InvalidArgumentException("Properties may not by null.");

      checkConnection();

      String typeId = propertyData.getId(CMIS.OBJECT_TYPE_ID);
      if (typeId == null)
         throw new InvalidArgumentException("Type is not specified.");

      ObjectData folder = null;
      if (folderId != null)
      {
         folder = storage.getObject(folderId);
         if (folder.getBaseType() != BaseType.FOLDER)
            throw new InvalidArgumentException("Object " + folderId + " is not a Folder object.");
      }
      else if (!storage.getRepositoryInfo().getCapabilities().isCapabilityUnfiling())
      {
         throw new ConstraintException("Unfiling capability is not supported, parent folder must be provided.");
      }

      ObjectData newPolicy = storage.createPolicy((FolderData)folder, typeId);

      newPolicy.getProperties().setProperties(propertyData.getAll());

      if ((addACL != null && addACL.size() > 0) || (removeACL != null && removeACL.size() > 0))
         applyACL(newPolicy, addACL, removeACL);

      if (policies != null && policies.size() > 0)
         applyPolicies(newPolicy, policies);

      storage.saveObject(newPolicy);

      CmisObject cmis =
         getCmisObject(newPolicy, false, IncludeRelationships.NONE, false, false, PropertyFilter.ALL,
            RenditionFilter.NONE);

      return cmis;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject createRelationship(Properties propertyData, List<AccessControlEntry> addACL,
      List<AccessControlEntry> removeACL, List<String> policies) throws ObjectNotFoundException, ConstraintException,
      NameConstraintViolationException, StorageException, CmisRuntimeException
   {
      if (propertyData == null)
         throw new InvalidArgumentException("Properties may not by null.");

      checkConnection();

      String typeId = propertyData.getId(CMIS.OBJECT_TYPE_ID);
      if (typeId == null)
         throw new InvalidArgumentException("Type is not specified.");

      String sourceId = propertyData.getId(CMIS.SOURCE_ID);
      if (sourceId == null)
         throw new InvalidArgumentException("Required property 'cmis:sourceId' is not specified.");

      String targetId = propertyData.getId(CMIS.TARGET_ID);
      if (targetId == null)
         throw new InvalidArgumentException("Required property 'cmis:targetId' is not specified.");

      ObjectData newRelationship =
         storage.createRelationship(storage.getObject(sourceId), storage.getObject(targetId), typeId);

      newRelationship.getProperties().setProperties(propertyData.getAll());

      if ((addACL != null && addACL.size() > 0) || (removeACL != null && removeACL.size() > 0))
         applyACL(newRelationship, addACL, removeACL);

      if (policies != null && policies.size() > 0)
         applyPolicies(newRelationship, policies);

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

      ObjectData document = storage.getObject(documentId);

      if (document.getBaseType() != BaseType.DOCUMENT)
         throw new InvalidArgumentException("Object " + documentId + " is not Document.");

      // Validate change token, object may be already updated.
      validateChangeToken(document, changeToken);

      ((DocumentData)document).setContentStream(null);

      storage.saveObject(document);
   }

   /**
    * {@inheritDoc}
    */
   public ContentStream getContentStream(String objectId, String streamId, long offset, long length)
      throws ObjectNotFoundException, ConstraintException, CmisRuntimeException
   {
      checkConnection();

      ObjectData object = storage.getObject(objectId);
      ContentStream contentStream = null;

      if (streamId != null)
         contentStream = object.getContentStream(streamId);
      else
         contentStream = ((DocumentData)object).getContentStream();

      if (contentStream == null)
         throw new ConstraintException("Object does not have content stream.");

      return contentStream;
   }

   /**
    * {@inheritDoc}
    */
   public void deleteObject(String objectId, Boolean deleteAllVersions) throws ObjectNotFoundException,
      ConstraintException, UpdateConflictException, StorageException, CmisRuntimeException
   {
      checkConnection();

      ObjectData object = storage.getObject(objectId);

      if (deleteAllVersions == null)
         deleteAllVersions = true; // Default.

      if (object.getBaseType() == BaseType.FOLDER)
      {
         if (((FolderData)object).isRoot())
            throw new ConstraintException("Root folder can't be removed.");

         if (((FolderData)object).hasChildren())
            throw new ConstraintException("Failed delete object. Object " + objectId
               + " is Folder and contains one or more objects.");
      }
      storage.deleteObject(object, deleteAllVersions);
   }

   /**
    * {@inheritDoc}
    */
   public Collection<String> deleteTree(String folderId, Boolean deleteAllVersions, UnfileObject unfileObject,
      Boolean continueOnFailure) throws ObjectNotFoundException, UpdateConflictException, CmisRuntimeException
   {
      checkConnection();

      ObjectData folder = storage.getObject(folderId);

      if (folder.getBaseType() != BaseType.FOLDER)
         throw new ConstraintException("Failed delete tree. Object " + folderId + " is not a Folder.");

      if (((FolderData)folder).isRoot())
         throw new ConstraintException("Root folder can't be removed.");

      if (unfileObject == null)
         unfileObject = UnfileObject.DELETE; // Default value.

      if (deleteAllVersions == null)
         deleteAllVersions = true; // Default value.

      if (continueOnFailure == null)
         continueOnFailure = false;

      // TODO : Check unfiling capability if 'unfileObject' is other then 'DELETE'

      Collection<String> failedDelete =
         storage.deleteTree((FolderData)folder, deleteAllVersions, unfileObject, continueOnFailure);

      return failedDelete;
   }

   /**
    * {@inheritDoc}
    */
   public AllowableActions getAllowableActions(String objectId) throws ObjectNotFoundException, CmisRuntimeException
   {
      checkConnection();
      ObjectData object = storage.getObject(objectId);
      return storage.calculateAllowableActions(object);
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
         includeRelationships = IncludeRelationships.NONE; // Default.

      ObjectData object = storage.getObjectByPath(path);

      CmisObject cmis =
         getCmisObject(object, includeAllowableActions, includeRelationships, includePolicyIDs, includeAcl,
            parsedPropertyFilter, parsedRenditionFilter);

      return cmis;
   }

   /**
    * {@inheritDoc}
    */
   public Properties getProperties(String objectId, String propertyFilter) throws ObjectNotFoundException,
      FilterNotValidException, CmisRuntimeException
   {
      checkConnection();

      ObjectData object = storage.getObject(objectId);
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      return object.getProperties().getSubset(parsedPropertyFilter);
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

      ObjectData object = storage.getObject(objectId);

      ObjectData target = storage.getObject(targetFolderId);
      if (target.getBaseType() != BaseType.FOLDER)
         throw new InvalidArgumentException("Object " + targetFolderId + " is not a Folder object.");

      ObjectData source = storage.getObject(sourceFolderId);
      if (source.getBaseType() != BaseType.FOLDER)
         throw new InvalidArgumentException("Object " + sourceFolderId + " is not a Folder object.");

      ObjectData movedObject = storage.moveObject(object, (FolderData)target, (FolderData)source);

      CmisObject cmis =
         getCmisObject(movedObject, false, IncludeRelationships.NONE, false, false, PropertyFilter.ALL,
            RenditionFilter.NONE);

      return cmis;
   }

   /**
    * {@inheritDoc}
    */
   public void setContentStream(String documentId, ContentStream content, String changeToken, boolean overwriteFlag)
      throws ObjectNotFoundException, ContentAlreadyExistsException, StreamNotSupportedException,
      UpdateConflictException, IOException, StorageException, CmisRuntimeException
   {
      checkConnection();

      ObjectData document = storage.getObject(documentId);

      if (document.getBaseType() != BaseType.DOCUMENT)
         throw new InvalidArgumentException("Object " + documentId + " is not Document.");

      if (!overwriteFlag && ((DocumentData)document).hasContent())
         throw new ContentAlreadyExistsException("Document already has content stream and 'overwriteFlag' is false.");

      // Validate change token, object may be already updated.
      validateChangeToken(document, changeToken);

      ((DocumentData)document).setContentStream(null);

      storage.saveObject(document);
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject updateProperties(String objectId, String changeToken, Properties propertiesData)
      throws ObjectNotFoundException, ConstraintException, NameConstraintViolationException, UpdateConflictException,
      StorageException, CmisRuntimeException
   {
      checkConnection();

      ObjectData object = storage.getObject(objectId);

      // Validate change token, object may be already updated.
      validateChangeToken(object, changeToken);

      object.getProperties().setProperties(propertiesData.getAll());

      storage.saveObject(object);

      return getCmisObject(object, false, IncludeRelationships.NONE, false, false, PropertyFilter.ALL,
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

      ItemsIterator<Rendition> iterator = storage.getRenditions(objectData);

      try
      {
         if (skipCount > 0)
            iterator.skip(skipCount);
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
      checkConnection();

      Collection<DocumentData> versions = storage.getAllVersions(versionSeriesId);

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      List<CmisObject> cmisVersions = new ArrayList<CmisObject>();

      for (ObjectData objectData : versions)
      {
         cmisVersions.add(getCmisObject(objectData, includeAllowableActions, IncludeRelationships.NONE, false, false,
            parsedPropertyFilter, RenditionFilter.NONE));
      }

      return cmisVersions;
   }

   /**
    * {@inheritDoc}
    */
   public void cancelCheckout(String documentId) throws ConstraintException, UpdateConflictException,
      VersioningException, StorageException, CmisRuntimeException
   {
      checkConnection();

      ObjectData document = storage.getObject(documentId);
      if (document.getBaseType() != BaseType.DOCUMENT)
         throw new InvalidArgumentException("Object " + documentId + " is not a Document object.");
      // cancelCheckedOut may be invoked on any object in version series.
      // In other way 'cmis:versionSeriesCheckedOutId' may not reflect
      // current PWC id. 
      ((DocumentData)document).cancelCheckout();
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject checkin(String documentId, boolean major, Properties propertyData, ContentStream content,
      String checkinComment, List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL, List<String> policies)
      throws ConstraintException, UpdateConflictException, StreamNotSupportedException, IOException, StorageException
   {
      checkConnection();

      ObjectData pwc = storage.getObject(documentId);

      if (pwc.getBaseType() != BaseType.DOCUMENT)
         throw new InvalidArgumentException("Object " + documentId + " is not a Document object.");

      if (!((DocumentData)pwc).isPWC())
         throw new VersioningException("Object " + documentId + " is not Private Working Copy.");

      if (propertyData != null)
         pwc.getProperties().setProperties(propertyData.getAll());

      if (content != null)
         ((DocumentData)pwc).setContentStream(content);

      if ((addACL != null && addACL.size() > 0) || (removeACL != null && removeACL.size() > 0))
         applyACL(pwc, addACL, removeACL);

      if (policies != null && policies.size() > 0)
         applyPolicies(pwc, policies);

      DocumentData version = ((DocumentData)pwc).checkin(major, checkinComment);

      CmisObject cmis =
         getCmisObject(version, false, IncludeRelationships.NONE, false, false, PropertyFilter.ALL,
            RenditionFilter.NONE);

      return cmis;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject checkout(String documentId) throws ConstraintException, UpdateConflictException,
      VersioningException, StorageException, CmisRuntimeException
   {
      checkConnection();

      ObjectData document = storage.getObject(documentId);

      if (document.getBaseType() != BaseType.DOCUMENT)
         throw new InvalidArgumentException("Object " + documentId + " is not a Document object.");

      DocumentData pwc = ((DocumentData)document).checkout();

      CmisObject cmis =
         getCmisObject(pwc, false, IncludeRelationships.NONE, false, false, PropertyFilter.ALL, RenditionFilter.NONE);

      return cmis;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject getObjectOfLatestVersion(String versionSeriesId, boolean major, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includePolicyIDs, boolean includeAcl, String propertyFilter,
      String renditionFilter) throws ObjectNotFoundException, FilterNotValidException, CmisRuntimeException
   {
      checkConnection();

      Collection<DocumentData> versions = storage.getAllVersions(versionSeriesId);

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);

      if (includeRelationships == null)
         includeRelationships = IncludeRelationships.NONE; // Default

      // Even for not-versionable documents version series contains exactly one version of document.
      if (versions.size() == 1)
         return getCmisObject(versions.iterator().next(), includeAllowableActions, includeRelationships, false, false,
            parsedPropertyFilter, parsedRenditionFilter);

      // Storage#getAllVersions(versionSeriesId) return sorted by
      // 'cmis:creationDate' descending. Latest version is version with latest
      // 'cmis:lastModificationDate'.
      List<DocumentData> v = new ArrayList<DocumentData>(versions);
      Collections.sort(v, CmisUtils.versionComparator);

      if (!major) // If not need major version that simply take first in list. 
         return getCmisObject(v.get(0), includeAllowableActions, includeRelationships, false, false,
            parsedPropertyFilter, parsedRenditionFilter);

      for (DocumentData document : v)
      {
         if (document.isMajorVersion())
            return getCmisObject(document, includeAllowableActions, includeRelationships, false, false,
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
      return getObjectOfLatestVersion(versionSeriesId, major, false, null, false, false, propertyFilter,
         RenditionFilter.NONE_FILTER).getProperties();
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

      ObjectData folder = storage.getObject(folderId);
      if (folder.getBaseType() != BaseType.FOLDER)
         throw new InvalidArgumentException("Can't get children. Object " + folderId + " is not a Folder.");

      /* TODO : orderBy in some more usable form */
      ItemsIterator<ObjectData> iterator = ((FolderData)folder).getChildren(orderBy);
      try
      {
         if (skipCount > 0)
            iterator.skip(skipCount);
      }
      catch (NoSuchElementException nse)
      {
         throw new InvalidArgumentException("'skipCount' parameter is greater then total number of argument");
      }

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);
      if (includeRelationships == null)
         includeRelationships = IncludeRelationships.NONE; // Default

      ItemsListImpl<CmisObject> cmisChildren = new ItemsListImpl<CmisObject>();
      for (int count = 0; iterator.hasNext() && (maxItems < 0 || count < maxItems); count++)
      {
         ObjectData chilData = iterator.next();

         CmisObject child =
            getCmisObject(chilData, includeAllowableActions, includeRelationships, false, false, parsedPropertyFilter,
               parsedRenditionFilter);

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
   public CmisObject getFolderParent(String folderId, String propertyFilter) throws ObjectNotFoundException,
      InvalidArgumentException, FilterNotValidException, CmisRuntimeException
   {
      checkConnection();

      ObjectData folder = storage.getObject(folderId);
      if (folder.getBaseType() != BaseType.FOLDER)
         throw new InvalidArgumentException("Object " + folderId + " is not a Folder.");

      if (((FolderData)folder).isRoot())
         throw new InvalidArgumentException("Can't get parent of root folder.");

      ObjectData parent = folder.getParent();

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);

      CmisObject cmisParent =
         getCmisObject(parent, false, IncludeRelationships.NONE, false, false, parsedPropertyFilter,
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

      String typeId = object.getTypeId();
      TypeDefinition typeDefinition = getTypeDefinition(typeId, false);
      if (!typeDefinition.isFileable())
         throw new ConstraintException("Can't get parents. Object " + objectId + " has type " + typeId
            + " that is not fileable");

      Collection<FolderData> parents = object.getParents();

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);

      if (includeRelationships == null)
         includeRelationships = IncludeRelationships.NONE; // Default

      List<ObjectParent> cmisParents = new ArrayList<ObjectParent>(parents.size());
      for (ObjectData parent : parents)
      {
         CmisObject cmisParent =
            getCmisObject(parent, includeAllowableActions, includeRelationships, false, false, parsedPropertyFilter,
               parsedRenditionFilter);

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

      ObjectData folder = storage.getObject(folderId);

      if (folder.getBaseType() != BaseType.FOLDER)
         throw new InvalidArgumentException("Can't get children. Object " + folderId + " is not a Folder.");

      List<ItemsTree<CmisObject>> tree = new ArrayList<ItemsTree<CmisObject>>();
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);

      for (ItemsIterator<ObjectData> children = ((FolderData)folder).getChildren(null); children.hasNext();)
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

      ObjectData folder = null;

      if (folderId != null)
      {
         folder = storage.getObject(folderId);

         if (folder.getBaseType() != BaseType.FOLDER)
            throw new InvalidArgumentException("Can't get checkedout documents. Object " + folderId
               + " is not a Folder.");
      }

      ItemsIterator<ObjectData> iterator = storage.getCheckedOutDocuments(folder, orderBy);

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
         includeRelationships = IncludeRelationships.NONE; // Default.

      ItemsListImpl<CmisObject> checkedout = new ItemsListImpl<CmisObject>();

      for (int count = 0; iterator.hasNext() && (maxItems < 0 || count < maxItems); count++)
      {
         ObjectData pwcData = iterator.next();
         CmisObject pwc =
            getCmisObject(pwcData, includeAllowableActions, includeRelationships, false, false, parsedPropertyFilter,
               parsedRenditionFilter);
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
      boolean includeSubRelationshipTypes, boolean includeAllowableActions, String propertyFilter, int maxItems,
      int skipCount) throws FilterNotValidException, ObjectNotFoundException
   {
      checkConnection();

      if (skipCount < 0)
         throw new InvalidArgumentException("skipCount parameter is negative.");

      if (direction == null)
         direction = RelationshipDirection.SOURCE;

      ObjectData objectData = storage.getObject(objectId);

      ItemsIterator<RelationshipData> iterator =
         objectData.getRelationships(direction, typeId, includeSubRelationshipTypes);

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
      ItemsListImpl<CmisObject> relationships = new ItemsListImpl<CmisObject>();

      for (int count = 0; iterator.hasNext() && (maxItems < 0 || count < maxItems); count++)
      {
         ObjectData rel = iterator.next();

         CmisObject cmis =
            getCmisObject(rel, includeAllowableActions, null, false, false, parsedPropertyFilter, RenditionFilter.NONE);

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
      // TODO : implement
      throw new NotSupportedException("Changes log feature is not supported.");
   }

   /**
    * {@inheritDoc}
    */
   public ItemsList<CmisObject> query(String statement, boolean searchAllVersions, boolean includeAllowableActions,
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
         includeRelationships = IncludeRelationships.NONE; // Default.

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
                  propertyFilter.append(',');
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
            getCmisObject(data, includeAllowableActions, includeRelationships, false, false, new PropertyFilter(
               propertyFilter.toString()), parsedRenditionFilter);

         Score score = result.getScore();
         if (score != null)
         {
            String scoreColumnName = score.getScoreColumnName();
            DecimalProperty scoreProperty =
               new DecimalProperty(scoreColumnName, scoreColumnName, scoreColumnName, scoreColumnName, Collections
                  .singletonList(score.getScoreValue()));
            object.getProperties().getAll().put(scoreColumnName, scoreProperty);
         }
         list.getItems().add(object);
      }

      // Indicate that we have some more results.
      list.setHasMoreItems(iterator.hasNext());
      list.setNumItems(iterator.size()); // ItemsIterator gives -1 if total number is unknown

      return list;
   }

   //---------

   protected CmisObject getCmisObject(ObjectData object, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includePolicyIds, boolean includeAcl,
      PropertyFilter parsedPropertyFilter, RenditionFilter parsedRenditionFilter)
   {
      // TODO : 
      return null;
   }

   /**
    * Check is connection may be used at the moment, e.g. it may be already
    * closed.
    * 
    * @throws IllegalStateException if connection may not be used any more
    */
   protected abstract void checkConnection() throws IllegalStateException;

   protected abstract void validateChangeToken(ObjectData object, String changeToken) throws UpdateConflictException;

   // ------------------------------- Helpers ---------------------------

   private void applyPolicies(ObjectData object, List<String> policies)
   {
      TypeDefinition typeDefinition = object.getTypeDefinition();
      if (!typeDefinition.isControllablePolicy())
         throw new ConstraintException("Type " + typeDefinition.getId() + " is not controllable by Policy.");

      for (String policyID : policies)
      {
         ObjectData policy = storage.getObject(policyID);
         if (policy.getBaseType() != BaseType.POLICY)
            throw new InvalidArgumentException("Object " + policyID + " is not a Policy object.");
         object.applyPolicy((PolicyData)policy);
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
         throw new NotSupportedException("ACL capability is not supported.");
      else if (capabilityACL == CapabilityACL.DISCOVER)
         throw new NotSupportedException("ACL can be discovered but not managed via CMIS services.");

      TypeDefinition typeDefinition = object.getTypeDefinition();
      if (!typeDefinition.isControllableACL())
         throw new ConstraintException("Type " + typeDefinition.getId() + " is not controllable by ACL.");

      // Merge ACL include existed one. It may be inherited from parent even for newly created object .
      List<AccessControlEntry> mergedACL = CmisUtils.mergeACLs(object.getACL(false), addACL, removeACL);

      object.setACL(mergedACL);
   }

   //
   
   /**
    * {@inheritDoc}
    */
   public Storage getStorage()
   {
      return storage;
   }

}
