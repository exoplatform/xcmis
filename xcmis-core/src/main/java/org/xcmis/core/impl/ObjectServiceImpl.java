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

package org.xcmis.core.impl;

import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.CmisAction;
import org.xcmis.core.CmisAllowableActionsType;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisPropertyId;
import org.xcmis.core.CmisPropertyString;
import org.xcmis.core.CmisRenditionType;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumCapabilityRendition;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.core.EnumUnfileObject;
import org.xcmis.core.EnumVersioningState;
import org.xcmis.core.ObjectService;
import org.xcmis.core.RepositoryService;
import org.xcmis.core.impl.object.RenditionFilter;
import org.xcmis.core.impl.property.PropertyFilter;
import org.xcmis.core.impl.property.PropertyService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentAlreadyExistsException;
import org.xcmis.spi.EntryNameProducer;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.NotSupportedException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.Repository;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.object.ContentStream;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.ItemsIterator;
import org.xcmis.spi.object.RenditionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Implementation of the ObjectService.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: ObjectServiceImpl.java 218 2010-02-15 07:38:06Z andrew00x $
 */
public class ObjectServiceImpl extends CmisObjectProducer implements ObjectService
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(ObjectServiceImpl.class.getName());

   /** CMIS repository service. */
   protected final RepositoryService repositoryService;

   /**
    * Construct instance <tt>ObjectServiceImpl</tt>.
    * 
    * @param repositoryService the repository service for getting repositories
    * @param propertyService the property service for getting properties
    */
   public ObjectServiceImpl(RepositoryService repositoryService, PropertyService propertyService)
   {
      super(propertyService);
      this.repositoryService = repositoryService;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType createDocument(String repositoryId, String folderId, CmisPropertiesType properties,
      ContentStream content, EnumVersioningState versioningState, CmisAccessControlListType addACL,
      CmisAccessControlListType removeACL, List<String> policies) throws IOException, StreamNotSupportedException,
      ConstraintException, NameConstraintViolationException, RepositoryException
   {
      String typeId = null;
      String name = null;
      if (properties != null)
      {
         for (CmisProperty p : properties.getProperty())
         {
            if (p.getPropertyDefinitionId().equals(CMIS.OBJECT_TYPE_ID))
               typeId = ((CmisPropertyId)p).getValue().get(0);
            if (p.getPropertyDefinitionId().equals(CMIS.NAME))
               name = ((CmisPropertyString)p).getValue().get(0);
         }
      }
      if (typeId == null)
      {
         String msg = "Object Type id not found.";
         throw new InvalidArgumentException(msg);
      }

      if (LOG.isDebugEnabled())
         LOG.debug("CreateDocument in repository " + repositoryId + ", object type " + typeId + ", parent folder "
            + folderId);

      Repository repository = repositoryService.getRepository(repositoryId);

      CmisTypeDefinitionType type = repository.getTypeDefinition(typeId);
      if (type.getBaseId() != EnumBaseObjectTypeIds.CMIS_DOCUMENT)
      {
         String msg = "The typeId " + typeId + " represents object-type whose baseType is not a Document.";
         throw new ConstraintException(msg);
      }

      if (name == null || name.length() == 0)
         // Generate name if it is not specified.
         name = ((EntryNameProducer)repository).getEntryName(folderId, EnumBaseObjectTypeIds.CMIS_DOCUMENT, typeId);

      Entry newDoc = null;

      if (folderId == null)
      {
         if (!repository.getRepositoryInfo().getCapabilities().isCapabilityUnfiling())
         {
            String msg = "Repository does not support unfiling. Parent folder id must be specified.";
            throw new InvalidArgumentException(msg);
         }
         else
         {
            // If repository supports unfiling
            newDoc = repository.createObject(type, versioningState);
         }
      }
      else
      {
         Entry folder = repository.getObjectById(folderId);
         newDoc = folder.createChild(type, name, versioningState);
      }

      for (CmisProperty prop : properties.getProperty())
         propertyService.setProperty(newDoc, prop, CmisAction.CREATE);

      //      setProperties(newDoc, properties);
      if (policies != null)
      {
         for (String policyId : policies)
            newDoc.applyPolicy(repository.getObjectById(policyId));
      }
      if (removeACL != null && removeACL.getPermission().size() > 0)
         newDoc.removePermissions(removeACL.getPermission());
      if (addACL != null && addACL.getPermission().size() > 0)
         newDoc.addPermissions(addACL.getPermission());
      newDoc.setContent(content);
      newDoc.save();
      RenditionManager renditionManager = repository.getRenditionManager();
      boolean renditionCreated = renditionManager.createRenditions(newDoc);
      if (LOG.isDebugEnabled())
      {
         if (renditionCreated)
            LOG.debug("Created renditions for document with content stream media type " + content.getMediaType());
         LOG.debug("Created new document " + newDoc.getObjectId());
      }
      return getCmisObject(newDoc, false, EnumIncludeRelationships.NONE, false, false, PropertyFilter.ALL,
         RenditionFilter.NONE, renditionManager);
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType createDocumentFromSource(String repositoryId, String sourceId, String folderId,
      CmisPropertiesType properties, EnumVersioningState versioningState, CmisAccessControlListType addACL,
      CmisAccessControlListType removeACL, List<String> policies) throws ConstraintException,
      NameConstraintViolationException, RepositoryException
   {
      if (sourceId == null)
      {
         String msg = "Source document ID is not specified. Creating document from source failed.";
         throw new RuntimeException(msg);
      }

      if (LOG.isDebugEnabled())
         LOG.debug("Create Document from source in repository: " + repositoryId + ", source:  " + sourceId);

      Repository repository = repositoryService.getRepository(repositoryId);
      if (folderId == null && !repository.getRepositoryInfo().getCapabilities().isCapabilityUnfiling())
      {
         String msg = "Repository does not supports unfiling. Parent folder id must be specified.";
         throw new InvalidArgumentException(msg);
      }
      Entry newDoc = repository.copyObject(sourceId, folderId, versioningState);
      if (properties != null)
      {
         for (CmisProperty prop : properties.getProperty())
            propertyService.setProperty(newDoc, prop, CmisAction.CREATE);
      }
      if (policies != null)
      {
         for (String policyId : policies)
            newDoc.applyPolicy(repository.getObjectById(policyId));
      }
      if (removeACL != null && removeACL.getPermission().size() > 0)
         newDoc.removePermissions(removeACL.getPermission());
      if (addACL != null && addACL.getPermission().size() > 0)
         newDoc.addPermissions(addACL.getPermission());
      newDoc.save();
      if (LOG.isDebugEnabled())
         LOG.debug("Created new document " + newDoc.getObjectId() + " from source document " + sourceId);
      return getCmisObject(newDoc, false, EnumIncludeRelationships.NONE, false, false, PropertyFilter.ALL,
         RenditionFilter.NONE, repository.getRenditionManager());
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType createFolder(String repositoryId, String folderId, CmisPropertiesType properties,
      CmisAccessControlListType addACL, CmisAccessControlListType removeACL, List<String> policies)
      throws ConstraintException, NameConstraintViolationException, RepositoryException
   {
      String typeId = null;
      String name = null;
      if (properties != null)
      {
         for (CmisProperty p : properties.getProperty())
         {
            if (p.getPropertyDefinitionId().equals(CMIS.OBJECT_TYPE_ID))
               typeId = ((CmisPropertyId)p).getValue().get(0);
            if (p.getPropertyDefinitionId().equals(CMIS.NAME))
               name = ((CmisPropertyString)p).getValue().get(0);
         }
      }
      if (typeId == null)
      {
         String msg = "Object Type id not found.";
         throw new InvalidArgumentException(msg);
      }

      if (LOG.isDebugEnabled())
         LOG.debug("Create folder in repository " + repositoryId + ", object type " + typeId + " parent folder "
            + folderId);

      Repository repository = repositoryService.getRepository(repositoryId);
      if (folderId == null && !repository.getRepositoryInfo().getCapabilities().isCapabilityUnfiling())
      {
         String msg = "Repository does not supports unfiling. Parent folder id must be specified.";
         throw new InvalidArgumentException(msg);
      }
      CmisTypeDefinitionType type = repository.getTypeDefinition(typeId);
      if (type.getBaseId() != EnumBaseObjectTypeIds.CMIS_FOLDER)
      {
         String msg = "The typeId " + typeId + " represents object-type whose baseType is not a Folder.";
         throw new ConstraintException(msg);
      }

      // Generate name for folder.
      if (name == null || name.length() == 0)
         name = ((EntryNameProducer)repository).getEntryName(folderId, EnumBaseObjectTypeIds.CMIS_FOLDER, typeId);

      Entry parentFolder = repository.getObjectById(folderId);
      Entry newFolder = parentFolder.createChild(type, name, null);

      for (CmisProperty prop : properties.getProperty())
         propertyService.setProperty(newFolder, prop, CmisAction.CREATE);

      if (policies != null)
      {
         for (String policyId : policies)
            newFolder.applyPolicy(repository.getObjectById(policyId));
      }
      if (removeACL != null && removeACL.getPermission().size() > 0)
         newFolder.removePermissions(removeACL.getPermission());
      if (addACL != null && addACL.getPermission().size() > 0)
         newFolder.addPermissions(addACL.getPermission());
      newFolder.save();
      if (LOG.isDebugEnabled())
         LOG.debug("Created new folder " + newFolder.getObjectId());
      return getCmisObject(newFolder, false, EnumIncludeRelationships.NONE, false, false, PropertyFilter.ALL,
         RenditionFilter.NONE, repository.getRenditionManager());
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType createPolicy(String repositoryId, String folderId, CmisPropertiesType properties,
      CmisAccessControlListType addACL, CmisAccessControlListType removeACL, List<String> policies)
      throws ConstraintException, NameConstraintViolationException, RepositoryException
   {
      String typeId = null;
      String name = null;
      if (properties != null)
      {
         for (CmisProperty p : properties.getProperty())
         {
            if (p.getPropertyDefinitionId().equals(CMIS.OBJECT_TYPE_ID))
               typeId = ((CmisPropertyId)p).getValue().get(0);
            if (p.getPropertyDefinitionId().equals(CMIS.NAME))
               name = ((CmisPropertyString)p).getValue().get(0);
         }
      }
      if (typeId == null)
      {
         String msg = "Object Type id not found.";
         throw new InvalidArgumentException(msg);
      }

      if (LOG.isDebugEnabled())
         LOG.debug("Create policy in repository " + repositoryId + ", object type " + typeId + " parent folder "
            + folderId);

      Repository repository = repositoryService.getRepository(repositoryId);
      if (folderId == null && !repository.getRepositoryInfo().getCapabilities().isCapabilityUnfiling())
      {
         String msg = "Repository does not supports unfiling. Parent folder id must be specified.";
         throw new InvalidArgumentException(msg);
      }
      CmisTypeDefinitionType type = repository.getTypeDefinition(typeId);

      if (type.getBaseId() != EnumBaseObjectTypeIds.CMIS_POLICY)
      {
         String msg = "The typeId " + typeId + " represents object-type whose baseType is not a Policy.";
         throw new ConstraintException(msg);
      }

      if (name == null || name.length() == 0)
         // Generate name if it is not specified.
         name = ((EntryNameProducer)repository).getEntryName(folderId, EnumBaseObjectTypeIds.CMIS_POLICY, typeId);

      Entry folder = repository.getObjectById(folderId);
      Entry newPolicy = folder.createChild(type, name, EnumVersioningState.NONE);

      for (CmisProperty prop : properties.getProperty())
         propertyService.setProperty(newPolicy, prop, CmisAction.CREATE);

      if (policies != null)
      {
         for (String policyId : policies)
            newPolicy.applyPolicy(repository.getObjectById(policyId));
      }
      if (removeACL != null && removeACL.getPermission().size() > 0)
         newPolicy.removePermissions(removeACL.getPermission());
      if (addACL != null && addACL.getPermission().size() > 0)
         newPolicy.addPermissions(addACL.getPermission());
      newPolicy.save();
      if (LOG.isDebugEnabled())
         LOG.debug("Created new policy " + newPolicy.getObjectId());
      return getCmisObject(newPolicy, false, EnumIncludeRelationships.NONE, false, false, PropertyFilter.ALL,
         RenditionFilter.NONE, repository.getRenditionManager());
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType createRelationship(String repositoryId, CmisPropertiesType properties,
      CmisAccessControlListType addACL, CmisAccessControlListType removeACL, List<String> policies)
      throws ConstraintException, RepositoryException
   {
      String typeId = null;
      String name = null;
      String sourceObjectId = null;
      String targetObjectId = null;
      if (properties != null)
      {
         for (CmisProperty p : properties.getProperty())
         {
            String pn = p.getPropertyDefinitionId();
            if (pn.equals(CMIS.OBJECT_TYPE_ID))
               typeId = ((CmisPropertyId)p).getValue().get(0);
            else if (p.getPropertyDefinitionId().equals(CMIS.NAME))
               name = ((CmisPropertyString)p).getValue().get(0);
            else if (pn.equals(CMIS.SOURCE_ID))
               sourceObjectId = ((CmisPropertyId)p).getValue().get(0);
            else if (pn.equals(CMIS.TARGET_ID))
               targetObjectId = ((CmisPropertyId)p).getValue().get(0);
         }
      }
      if (typeId == null)
      {
         String msg = "Object Type id not found.";
         throw new InvalidArgumentException(msg);
      }
      if (sourceObjectId == null)
      {
         String msg = "Source Object id not found.";
         throw new InvalidArgumentException(msg);
      }
      if (targetObjectId == null)
      {
         String msg = "Target Object id not found.";
         throw new InvalidArgumentException(msg);
      }

      if (LOG.isDebugEnabled())
         LOG.debug("Create relationship in repository " + repositoryId + ", object type " + typeId + ", sourceId "
            + sourceObjectId + ", targetId " + targetObjectId);

      Repository repository = repositoryService.getRepository(repositoryId);
      CmisTypeDefinitionType relationshipType = repository.getTypeDefinition(typeId);
      if (relationshipType.getBaseId() != EnumBaseObjectTypeIds.CMIS_RELATIONSHIP)
      {
         String msg = "The typeId " + typeId + " represents object-type whose baseType is not a Relationship.";
         throw new ConstraintException(msg);
      }

      if (name == null || name.length() == 0)
         // Generate name if it is not specified.
         name = ((EntryNameProducer)repository).getEntryName(null, EnumBaseObjectTypeIds.CMIS_RELATIONSHIP, typeId);

      Entry source = repository.getObjectById(sourceObjectId);
      Entry target = repository.getObjectById(targetObjectId);

      Entry newRelationship = source.addRelationship(name, target, relationshipType);

      for (CmisProperty prop : properties.getProperty())
         propertyService.setProperty(newRelationship, prop, CmisAction.CREATE);

      if (policies != null)
      {
         for (String policyId : policies)
            newRelationship.applyPolicy(repository.getObjectById(policyId));
      }
      if (removeACL != null && removeACL.getPermission().size() > 0)
         newRelationship.removePermissions(removeACL.getPermission());
      if (addACL != null && addACL.getPermission().size() > 0)
         newRelationship.addPermissions(addACL.getPermission());
      newRelationship.save();
      if (LOG.isDebugEnabled())
         LOG.debug("Created new relationship " + newRelationship.getObjectId());
      return getCmisObject(newRelationship, false, EnumIncludeRelationships.NONE, false, false, PropertyFilter.ALL,
         RenditionFilter.NONE, repository.getRenditionManager());
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType deleteContentStream(String repositoryId, String documentId, String changeToken)
      throws ConstraintException, UpdateConflictException, RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Delete content stream, repository " + repositoryId + ", document " + documentId);
      Repository repository = repositoryService.getRepository(repositoryId);
      Entry doc = repository.getObjectById(documentId);

      if (!repository.getChangeTokenMatcher().isMatch(doc, changeToken))
      {
         String msg = "Change token provided by client is not match.";
         throw new UpdateConflictException(msg);
      }
      try
      {
         doc.setContent(null);
      }
      catch (StreamNotSupportedException snse)
      {
         // If Object is not document or content stream not allowed for object.
         throw new InvalidArgumentException(snse.getMessage());
      }
      catch (IOException e)
      {
         // Must never be thrown cause to null content.
      }
      doc.save();
      repository.getRenditionManager().removeRenditions(doc);
      if (LOG.isDebugEnabled())
         LOG.debug("Deleted contents stream of document " + documentId);
      return getCmisObject(doc, false, EnumIncludeRelationships.NONE, false, false, PropertyFilter.ALL,
         RenditionFilter.NONE, repository.getRenditionManager());
   }

   /**
    * {@inheritDoc}
    */
   public void deleteObject(String repositoryId, String objectId, boolean deleteAllVersion) throws ConstraintException,
      UpdateConflictException, RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Delete object, repository " + repositoryId + ", object " + objectId);
      Entry object = repositoryService.getRepository(repositoryId).getObjectById(objectId);
      if (object.getScope() == EnumBaseObjectTypeIds.CMIS_FOLDER && object.getChildren().hasNext())
      {
         String msg = "Folder is not empty.";
         throw new ConstraintException(msg);
      }
      object.delete();
      if (LOG.isDebugEnabled())
         LOG.debug("Deleted object " + objectId);
   }

   /**
    * {@inheritDoc}
    */
   public List<String> deleteTree(String repositoryId, String folderId, EnumUnfileObject unfileObjects,
      boolean continueOnFailure) throws UpdateConflictException, RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Delete tree, repository " + repositoryId + ", folder " + folderId);

      Repository repository = repositoryService.getRepository(repositoryId);

      Entry folder = repository.getObjectById(folderId);
      if (folder.getScope() != EnumBaseObjectTypeIds.CMIS_FOLDER)
      {
         String msg = "Object " + folderId + " is not a Folder.";
         throw new InvalidArgumentException(msg);
      }

      if (unfileObjects == null)
         unfileObjects = EnumUnfileObject.DELETE; // Default value.

      if (unfileObjects == EnumUnfileObject.DELETE)
      {
         // Try delete specified folder and all its descendants.
         folder.delete();
         if (LOG.isDebugEnabled())
            LOG.debug("Deleted folder tree " + folderId);
      }
      else
      {
         if (!repository.getRepositoryInfo().getCapabilities().isCapabilityUnfiling())
         {
            String msg =
               "Repository does not support unfiling capability, unfileObjects"
                  + " parameter may not be other then DELETE.";
            throw new NotSupportedException(msg);
         }

         if (unfileObjects == EnumUnfileObject.DELETESINGLEFILED)
         {
            // XXX Not clear from specification how it should work.
            Set<Entry> discovered = new HashSet<Entry>();
            discovered.add(folder);
            deleteSingleFiled(folder, discovered);
            if (LOG.isDebugEnabled())
               LOG.debug("Deleted single-filed objects from folder tree " + folderId);
         }
         else if (unfileObjects == EnumUnfileObject.UNFILE)
         {
            // XXX Not clear from specification how it should work.
            unfile(folder);
            if (LOG.isDebugEnabled())
               LOG.debug("Unfiled 'fileable' objects from folder tree " + folderId);
         }
      }

      // TODO : if continueOnFailure is TRUE return list of failed to delete/unfile objects
      return Collections.emptyList();
   }

   /**
    * {@inheritDoc}
    */
   public CmisAllowableActionsType getAllowableActions(String repositoryId, String objectId)
      throws ObjectNotFoundException, RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Get allowable actions, repository " + repositoryId + ", object " + objectId);
      Repository repository = repositoryService.getRepository(repositoryId);
      Entry entry = repository.getObjectById(objectId);
      return getAllowableActions(entry);
   }

   /**
    * {@inheritDoc}
    */
   public ContentStream getContentStream(String repositoryId, String documentId, String streamId, long offset,
      long length) throws ConstraintException, RepositoryException
   {
      // TODO process streamId, offset, length.
      if (LOG.isDebugEnabled())
         LOG.debug("Get content stream, repository " + repositoryId + ", document " + documentId);
      Repository repository = repositoryService.getRepository(repositoryId);
      Entry doc = repository.getObjectById(documentId);
      ContentStream content = doc.getContent(streamId);
      /*      if (content == null)
            {
               String msg = "Document has not content.";
               throw new ConstraintException(msg);
            }
      */
      return content;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType getObject(String repositoryId, String objectId, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, boolean includePolicyIds, boolean includeACL,
      String propertyFilter, String renditionFilter) throws ObjectNotFoundException, FilterNotValidException,
      RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Get object, repository " + repositoryId + ", object id " + objectId);
      Repository repository = repositoryService.getRepository(repositoryId);
      Entry entry = repository.getObjectById(objectId);
      return getCmisObject(entry, includeAllowableActions, EnumIncludeRelationships.NONE, includePolicyIds, includeACL,
         new PropertyFilter(propertyFilter), new RenditionFilter(renditionFilter), repository.getRenditionManager());
   }

   /**
    * {@inheritDoc}
    * @throws ObjectNotFoundException 
    */
   public CmisObjectType getObjectByPath(String repositoryId, String path, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, boolean includePolicyIds, boolean includeACL,
      String propertyFilter, String renditionFilter) throws ObjectNotFoundException, FilterNotValidException,
      RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Get object, repository " + repositoryId + ", path " + path);
      Repository repository = repositoryService.getRepository(repositoryId);
      Entry entry = repository.getObjectByPath(path);
      return getCmisObject(entry, includeAllowableActions, EnumIncludeRelationships.NONE, includePolicyIds, includeACL,
         new PropertyFilter(propertyFilter), new RenditionFilter(renditionFilter), repository.getRenditionManager());
   }

   /**
    * {@inheritDoc}
    */
   public CmisPropertiesType getProperties(String repositoryId, String objectId, String propertyFilter)
      throws FilterNotValidException, RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Get object's properties, repository " + repositoryId + ", object " + objectId);
      Repository repository = repositoryService.getRepository(repositoryId);
      Entry entry = repository.getObjectById(objectId);
      CmisPropertiesType resp = new CmisPropertiesType();
      resp.getProperty().addAll(
         getCmisObject(entry, false, EnumIncludeRelationships.NONE, false, false, new PropertyFilter(propertyFilter),
            RenditionFilter.NONE, repository.getRenditionManager()).getProperties().getProperty());
      return resp;
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisRenditionType> getRenditions(String repositoryId, String objectId, String renditionFilter,
      int maxItems, int skipCount) throws FilterNotValidException, ObjectNotFoundException, RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Get renditions, repository " + repositoryId + ", object " + objectId);

      Repository repository = repositoryService.getRepository(repositoryId);
      if (EnumCapabilityRendition.NONE == repository.getRepositoryInfo().getCapabilities().getCapabilityRenditions())
      {
         String msg = "Rendition capability is not supported by repository " + repositoryId;
         throw new NotSupportedException(msg);
      }
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

      ItemsIterator<CmisRenditionType> items = repository.getRenditionManager().getRenditions(objectId);
      try
      {
         if (skipCount > 0)
            items.skip(skipCount);
      }
      catch (NoSuchElementException nse)
      {
         String msg = "skipCount parameter is greater then total number of argument";
         throw new InvalidArgumentException(msg);
      }
      List<CmisRenditionType> renditions = new ArrayList<CmisRenditionType>();
      RenditionFilter renditionFilterInst = new RenditionFilter(renditionFilter);
      int count = 0;
      while (items.hasNext() && count < maxItems)
      {
         CmisRenditionType item = items.next();
         if (renditionFilterInst.accept(item))
            renditions.add(item);
         count++;
      }

      return renditions;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType moveObject(String repositoryId, String objectId, String targetFolderId, String sourceFolderId)
      throws ConstraintException, UpdateConflictException, RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Move object repository " + repositoryId + ", object " + objectId + ", destination "
            + targetFolderId);
      Repository repository = repositoryService.getRepository(repositoryId);
      repository.moveObject(objectId, targetFolderId, sourceFolderId);

      // ID of object is the same.
      CmisObjectType moved =
         getCmisObject(repository.getObjectById(objectId), false, EnumIncludeRelationships.NONE, false, false,
            PropertyFilter.ALL, RenditionFilter.NONE, repository.getRenditionManager());
      return moved;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType setContentStream(String repositoryId, String documentId, ContentStream content,
      String changeToken, boolean overwriteFlag) throws ConstraintException, ContentAlreadyExistsException,
      StreamNotSupportedException, UpdateConflictException, IOException, RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Get content stream, repository " + repositoryId + ", document " + documentId);
      // TODO change token
      Repository repository = repositoryService.getRepository(repositoryId);
      Entry doc = repository.getObjectById(documentId);
      if (!repository.getChangeTokenMatcher().isMatch(doc, changeToken))
      {
         String msg = "Change token provided by client is not match.";
         throw new UpdateConflictException(msg);
      }
      if (doc.getContent(null) != null && !overwriteFlag)
      {
         String msg = "Content Stream already exists, overwriteFlag " + overwriteFlag;
         throw new ContentAlreadyExistsException(msg);
      }
      doc.setContent(content);
      doc.save();
      RenditionManager renditionManager = repository.getRenditionManager();
      renditionManager.removeRenditions(doc);
      boolean renditionCreated = renditionManager.createRenditions(doc);
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Update content stream of document " + doc.getObjectId());
         if (renditionCreated)
            LOG.debug("Created renditions for document with content stream media type " + content.getMediaType());
      }
      return getCmisObject(doc, false, EnumIncludeRelationships.NONE, false, false, PropertyFilter.ALL,
         RenditionFilter.NONE, repository.getRenditionManager());
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType updateProperties(String repositoryId, String objectId, String changeToken,
      CmisPropertiesType properties) throws ConstraintException, NameConstraintViolationException,
      UpdateConflictException, RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Update properties, repository " + repositoryId + ", object " + objectId);

      Repository repository = repositoryService.getRepository(repositoryId);
      Entry object = repository.getObjectById(objectId);
      if (!repository.getChangeTokenMatcher().isMatch(object, changeToken))
      {
         String msg = "Change token provided by client is not match.";
         throw new UpdateConflictException(msg);
      }
      if (properties != null)
      {
         for (CmisProperty prop : properties.getProperty())
            propertyService.setProperty(object, prop, CmisAction.UPDATE_OBJECT_PROPERTIES);
         object.save();
      }
      if (LOG.isDebugEnabled())
         LOG.debug("Update properties of object " + object.getObjectId());
      return getCmisObject(object, false, EnumIncludeRelationships.NONE, false, false, PropertyFilter.ALL,
         RenditionFilter.NONE, repository.getRenditionManager());
   }

   private void deleteSingleFiled(Entry folder, Set<Entry> discovered) throws UnsupportedOperationException,
      RepositoryException
   {
      for (ItemsIterator<Entry> children = folder.getChildren(); children.hasNext();)
      {
         Entry current = children.next();
         if (current.getType().isFileable() //
            && current.getScope() != EnumBaseObjectTypeIds.CMIS_FOLDER)
         {
            List<Entry> parents = current.getParents();
            if (parents.size() == 1 && discovered.containsAll(parents))
               current.delete();
            else
               folder.removeChild(current.getObjectId());
         }
         else
         {
            discovered.add(current);
            deleteSingleFiled(current, discovered);
         }
      }
   }

   /**
    * Set properties.
    * 
    * @param entry the CMIS entry
    * @param properties the properties to set to the property service
    * @throws RepositoryException if any repository error occurs
    */
   //   private void setProperties(Entry entry, CmisPropertiesType properties) throws RepositoryException
   //   {
   //      for (CmisProperty prop : properties.getProperty())
   //         propertyService.setProperty(entry, prop);
   //   }

   private void unfile(Entry folder) throws UnsupportedOperationException, RepositoryException
   {
      for (ItemsIterator<Entry> children = folder.getChildren(); children.hasNext();)
      {
         Entry current = children.next();
         if (current.getType().isFileable() //
            && current.getScope() != EnumBaseObjectTypeIds.CMIS_FOLDER)
            folder.removeChild(current.getObjectId());
         else
            unfile(current);
      }
   }

}
