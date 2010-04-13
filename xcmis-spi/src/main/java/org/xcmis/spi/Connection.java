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

package org.xcmis.spi;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.AccessControlPropagation;
import org.xcmis.spi.model.AllowableActions;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.CapabilityACL;
import org.xcmis.spi.model.CapabilityRendition;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.IncludeRelationships;
import org.xcmis.spi.model.ObjectInfo;
import org.xcmis.spi.model.ObjectParent;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.RelationshipDirection;
import org.xcmis.spi.model.Rendition;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.UnfileObject;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.model.impl.DecimalProperty;
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
 * Connection to CMIS storage. It should be used for all operation with storage.
 * The <code>Connection</code> object is associated with <code>Storage</code>
 * object. When <code>Connection</code> is no longer needed then method
 * {@link #close()} should be used to release all associated resources. After
 * this connection should not be in use any more.
 *
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: Connection.java 332 2010-03-11 17:24:56Z andrew00x $
 */
public abstract class Connection
{
   private static final Log LOG = ExoLogger.getLogger(Connection.class);

   protected Storage storage;

   public Connection(Storage storage)
   {
      this.storage = storage;
   }

   /**
    * Add un-filed object in folder.
    *
    * @param objectId the id of object to be added in folder
    * @param folderId the target folder id
    * @param allVersions to add all versions of the object to the folder if the
    *        storage supports version-specific filing
    * @throws ObjectNotFoundException if <code>objectId</code> or
    *         <code>folderId</code> were not found
    * @throws InvalidArgumentException if <code>objectId</code> is id of object
    *         that is not fileable or if <code>folderId</code> is id of object
    *         that base type is not Folder
    * @throws ConstraintException if destination folder is not supported object
    *         type that should be added
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

      ((FolderData)folder).addObject(object);

   }

   /**
    * Add new type.
    *
    * @param type type definition
    * @return ID of newly added type
    * @throws ConstraintException if any of the following conditions are met:
    *         <ul>
    *         <li>Storage already has type with the same id, see
    *         {@link CmisTypeDefinitionType#getId()}</li>
    *         <li>Base type is not specified or is one of optional type that is
    *         not supported by storage, see
    *         {@link CmisTypeDefinitionType#getBaseId()}</li>
    *         <li>Parent type is not specified or does not exists, see
    *         {@link CmisTypeDefinitionType#getParentId()}</li>
    *         <li>New type has at least one property definitions that has
    *         unsupported type, invalid id, so on</li>
    * @throws StorageException if type can't be added (save changes) cause to
    *         storage internal problem
    */
   public String addType(TypeDefinition type) throws ConstraintException, StorageException
   {
      checkConnection();

      String id = storage.addType(type);
      return id;
   }

   /**
    * Adds or(and) remove the given Access Control Entries to(from) the Access
    * Control List of object.
    *
    * @param objectId identifier of object for which should be added specified
    *        ACEs
    * @param addACL ACEs that will be added from object's ACL
    * @param removeACL ACEs that will be removed from object's ACL
    * @param propagation specifies how ACEs should be handled:
    *        <ul>
    *        <li>objectonly: ACEs must be applied without changing the ACLs of
    *        other objects</li>
    *        <li>propagate: ACEs must be applied by propagate the changes to all
    *        inheriting objects</li>
    *        <li>repositorydetermined: Indicates that the client leaves the
    *        behavior to the storage</li>
    *        </ul>
    * @throws ObjectNotFoundException if <code>objectId</code> or does not
    *         exists
    * @throws ConstraintException if any of the following conditions are met:
    *         <ul>
    *         <li>The specified object's Object-Type definition's attribute for
    *         controllableACL is <code>false</code></li>
    *         <li>The value for ACLPropagation does not match the values as
    *         returned via getACLCapabilities</li>
    *         <li>At least one of the specified values for permission in ANY of
    *         the ACEs does not match ANY of the permissionNames as returned by
    *         getACLCapability and is not a CMIS Basic permission</li>
    *         </ul>
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
    * Applies a specified policy to an object.
    *
    * @param policyId the policy to be applied to object
    * @param objectId target object for policy
    * @throws ObjectNotFoundException if object with <code>objectId</code> or
    *         <code>policyId</code> does not exist
    * @throws ConstraintException if object with id <code>objectId</code> is not
    *         controllable by policy
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
      object.applyPolicy((PolicyData)policy);

      storage.saveObject(object);
   }

   /**
    * Discard the check-out operation. As result Private Working Copy (PWC) must
    * be removed and storage ready to next check-out operation.
    *
    * @param documentId document id. May be PWC id or id of any other Document
    *        in Version Series
    * @throws ConstraintException if the object is not versionable
    * @throws UpdateConflictException if update an object that is no longer
    *         current
    * @throws VersioningException if object is a non-current document version
    * @throws StorageException if PWC can't be removed from storage cause to
    *         storage internal problem
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
      ((DocumentData)document).cancelCheckout();
   }

   /**
    * Check-in Private Working Copy.
    *
    * @param documentId document id
    * @param major <code>true</code> is new version should be marked as major
    *        <code>false</code> otherwise
    * @param properties properties to be applied to new version
    * @param content content of document
    * @param checkinComment check-in comment
    * @param addACL set Access Control Entry to be applied for newly created
    *        document, either using the ACL from <code>folderId</code> if
    *        specified, or being applied if no <code>folderId</code> is
    *        specified
    * @param removeACL set Access Control Entry that MUST be removed from the
    *        newly created document, either using the ACL from
    *        <code>folderId</code> if specified, or being ignored if no
    *        <code>folderId</code> is specified
    * @param policies list of policy id that MUST be applied to the newly
    *        created document
    * @return ID of checked-in document
    * @throws ConstraintException if the object is not versionable
    * @throws UpdateConflictException if update an object that is no longer
    *         current
    * @throws StreamNotSupportedException if document does not supports content
    *         stream
    * @throws IOException if any i/o error occurs when try to set document's
    *         content stream
    * @throws StorageException if newly version of Document can't be saved in
    *         storage cause to its internal problem
    */
   public String checkin(String documentId, boolean major, Map<String, Property<?>> properties, ContentStream content,
      String checkinComment, List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL,
      Collection<String> policies) throws ConstraintException, UpdateConflictException, StreamNotSupportedException,
      IOException
   {
      checkConnection();

      ObjectData pwc = storage.getObject(documentId);

      if (pwc.getBaseType() != BaseType.DOCUMENT)
      {
         throw new InvalidArgumentException("Object " + documentId + " is not a Document object.");
      }

      if (!((DocumentData)pwc).isPWC())
      {
         throw new VersioningException("Object " + documentId + " is not Private Working Copy.");
      }

      if (properties != null)
      {
         pwc.setProperties(properties);
      }

      if (content != null)
      {
         ((DocumentData)pwc).setContentStream(content);
      }

      if ((addACL != null && addACL.size() > 0) || (removeACL != null && removeACL.size() > 0))
      {
         applyACL(pwc, addACL, removeACL);
      }

      if (policies != null && policies.size() > 0)
      {
         applyPolicies(pwc, policies);
      }

      DocumentData version = ((DocumentData)pwc).checkin(major, checkinComment);

      return version.getObjectId();
   }

   /**
    * Check-out document.
    *
    * @param documentId document id. Storage MAY allow checked-out ONLY latest
    *        version of Document
    * @return ID of checked-out document (PWC)
    * @throws ConstraintException if the object is not versionable
    * @throws UpdateConflictException if update an object that is no longer
    *         current
    * @throws VersioningException if object is a non-current document version
    * @throws StorageException if newly created PWC can't be saved in storage
    *         cause to storage internal problem
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

      DocumentData pwc = ((DocumentData)document).checkout();

      return pwc.getObjectId();
   }

   /**
    * Close this connection. Release underlying resources. Not able to use this
    * connection any more.
    */
   public abstract void close();

   /**
    * Create a document object.
    *
    * @param folderId parent folder id for object. May be null if storage
    *        supports unfiling
    * @param properties properties that MAY be applied to newly created document
    * @param content document content
    * @param addACL Access Control Entries that MUST added for newly created
    *        document, either using the ACL from <code>folderId</code> if
    *        specified, or being applied if no <code>folderId</code> is
    *        specified
    * @param removeACL set Access Control Entries that MUST be removed from the
    *        newly created document, either using the ACL from
    *        <code>folderId</code> if specified, or being ignored if no
    *        <code>folderId</code> is specified
    * @param policies list of policy id that MUST be applied to the newly
    *        created document
    * @param versioningState enumeration specifying what the versioning state of
    *        the newly created object shall be
    * @return ID of newly created document
    * @throws ObjectNotFoundException if target folder with specified id
    *         <code>folderId</code> does not exist
    * @throws ConstraintException if any of following condition are met:
    *         <ul>
    *         <li><code>cmis:objectTypeId</code> property value is not an object
    *         type whose baseType is Document</li>
    *         <li><code>cmis:objectTypeId</code> property value is not in the
    *         list of AllowedChildObjectTypeIds of the parent-folder specified
    *         by <code>folderId</code></li>
    *         <li>value of any of the properties violates the
    *         min/max/required/length constraints specified in the property
    *         definition in the object type
    *         <li>
    *         <li>contentStreamAllowed attribute of the object type definition
    *         specified by the <code>cmis:objectTypeId</code> property value is
    *         set to <i>required</i> and no content input parameter is provided</li>
    *         <li>versionable attribute of the object type definition specified
    *         by the <code>cmis:objectTypeId</code> property value is set to
    *         <code>false</code> and a value for the versioningState input
    *         parameter is provided that is something other than <i>none</i></li>
    *         <li>versionable attribute of the object type definition specified
    *         by the <code>cmis:objectTypeId</code> property value is set to
    *         <code>true</code> and the value for the versioningState input
    *         parameter is provided that is <i>none</i></li>
    *         <li>controllablePolicy attribute of the object type definition
    *         specified by the <code>cmis:objectTypeId</code> property value is
    *         set to <code>false</code> and at least one policy is provided</li>
    *         <li>controllableACL attribute of the object type definition
    *         specified by the <code>cmis:objectTypeId</code> property value is
    *         set to <code>false</code> and at least one ACE is provided</li>
    *         <li>at least one of the permissions is used in an ACE provided
    *         which is not supported by the storage</li>
    *         </ul>
    * @throws InvalidArgumentException if object with id <code>folderId</code>
    *         is not a Folder
    * @throws StreamNotSupportedException if the contentStreamAllowed attribute
    *         of the object type definition specified by the
    *         <code>cmis:objectTypeId</code> property value is set to 'not
    *         allowed' and a contentStream input parameter is provided
    * @throws NameConstraintViolationException violation is detected with the
    *         given <code>cmis:name</code> property value. Storage MAY chose
    *         other name which does not conflict
    * @throws IOException if any i/o error occurs when try to set document's
    *         content stream
    * @throws StorageException if new Document can't be saved in storage cause
    *         to storage internal problem
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
      Property<?> typeProperty = properties.get(CmisConstants.OBJECT_TYPE_ID);
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

      DocumentData newDocument = storage.createDocument((FolderData)folder, typeId, versioningState);

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
    * Create a document object as a copy of the given source document in the
    * <code>folderId</code>.
    *
    * @param sourceId id for the source document
    * @param folderId parent folder id for object. May be null if storage
    *        supports unfiling
    * @param properties properties that MAY be applied to newly created document
    * @param addACL Access Control Entries that MUST added for newly created
    *        document, either using the ACL from <code>folderId</code> if
    *        specified, or being applied if no <code>folderId</code> is
    *        specified
    * @param removeACL set Access Control Entries that MUST be removed from the
    *        newly created document, either using the ACL from
    *        <code>folderId</code> if specified, or being ignored if no
    *        <code>folderId</code> is specified
    * @param policies list of policy id that MUST be applied to the newly
    *        created document
    * @param versioningState enumeration specifying what the versioning state of
    *        the newly created object shall be
    * @return ID of newly created document
    * @throws ObjectNotFoundException if target folder with specified id
    *         <code>folderId</code> or source document with id
    *         <code>sourceId</code> does not exist
    * @throws ConstraintException if any of following condition are met:
    *         <ul>
    *         <li>sourceId is not an Object whose baseType is Document</li>
    *         <li>source document's <code>cmis:objectTypeId</code> property
    *         value is NOT in the list of AllowedChildObjectTypeIds of the
    *         parent-folder specified by <code>folderId</code></li>
    *         <li>versionable attribute of the object type definition specified
    *         by the <code>cmis:objectTypeId</code> property value is set to
    *         <code>false</code> and a value for the versioningState input
    *         parameter is provided that is something other than <i>none</i></li>
    *         <li>versionable attribute of the object type definition specified
    *         by the <code>cmis:objectTypeId</code> property value is set to
    *         <code>true</code> and the value for the versioningState input
    *         parameter is provided that is <i>none</i></li>
    *         <li>controllablePolicy attribute of the object type definition
    *         specified by the <code>cmis:objectTypeId</code> property value is
    *         set to <code>false</code> and at least one policy is provided</li>
    *         <li>controllableACL attribute of the object type definition
    *         specified by the <code>cmis:objectTypeId</code> property value is
    *         set to <code>false</code> and at least one ACE is provided</li>
    *         <li>At least one of the permissions is used in an ACE provided
    *         which is not supported by the storage</li>
    *         </ul>
    * @throws InvalidArgumentException if object with id <code>folderId</code>
    *         is not a Folder
    * @throws NameConstraintViolationException violation is detected with the
    *         given <code>cmis:name</code> property value. Storage MAY chose
    *         other name which does not conflict
    * @throws StorageException if new Document can't be saved in storage cause
    *         to storage internal problem
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

      DocumentData newDocument = storage.copyDocument((DocumentData)source, (FolderData)folder, versioningState);

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
    * Create a folder object.
    *
    * @param folderId parent folder id for new folder
    * @param properties properties that MAY be applied to newly created folder
    * @param addACL Access Control Entries that MUST added for newly created
    *        Folder, either using the ACL from <code>folderId</code> if
    *        specified, or being applied if no <code>folderId</code> is
    *        specified
    * @param removeACL set Access Control Entry that MUST be removed from the
    *        newly created folder, either using the ACL from
    *        <code>folderId</code> if specified, or being ignored if no
    *        <code>folderId</code> is specified
    * @param policies list of policy id that MUST be applied to the newly
    *        created folder
    * @return ID of newly created folder
    * @throws ObjectNotFoundException if target folder with specified id
    *         <code>folderId</code> does not exist
    * @throws ConstraintException if any of following condition are met:
    *         <ul>
    *         <li><code>cmis:objectTypeId</code> property value is not an object
    *         type whose baseType is Folder</li>
    *         <li>value of any of the properties violates the
    *         min/max/required/length constraints specified in the property
    *         definition in the object type</li>
    *         <li><code>cmis:objectTypeId</code> property value is not in the
    *         list of AllowedChildObjectTypeIds of the parent-folder specified
    *         by <code>folderId</code></li>
    *         <li>controllablePolicy attribute of the object type definition
    *         specified by the <code>cmis:objectTypeId</code> property value is
    *         set to <code>false</code> and at least one policy is provided</li>
    *         <li>controllableACL attribute of the object type definition
    *         specified by the <code>cmis:objectTypeId</code> property value is
    *         set to <code>false</code> and at least one ACE is provided</li>
    *         <li>at least one of the permissions is used in an ACE provided
    *         which is not supported by the storage</li>
    *         </ul>
    * @throws InvalidArgumentException if object with id <code>folderId</code>
    *         is not a Folder
    * @throws NameConstraintViolationException violation is detected with the
    *         given <code>cmis:name</code> property value. Storage MAY chose
    *         other name which does not conflict.
    * @throws StorageException if new Folder can't be saved in storage cause to
    *         storage internal problem
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
      Property<?> typeProperty = properties.get(CmisConstants.OBJECT_TYPE_ID);
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

      ObjectData newFolder = storage.createFolder((FolderData)folder, typeId);

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
    * Create a policy object.
    *
    * @param folderId parent folder id may be null if policy object type is not
    *        fileable
    * @param properties properties to be applied to newly created Policy
    * @param addACL Access Control Entries that MUST added for newly created
    *        Policy, either using the ACL from <code>folderId</code> if
    *        specified, or being applied if no <code>folderId</code> is
    *        specified
    * @param removeACL set Access Control Entry that MUST be removed from the
    *        newly created Policy, either using the ACL from
    *        <code>folderId</code> if specified, or being ignored if no
    *        <code>folderId</code> is specified
    * @param policies list of policy id that MUST be applied to the newly
    *        created policy
    * @return ID of newly created policy
    * @throws ObjectNotFoundException if target folder with specified id
    *         <code>folderId</code> does not exist
    * @throws ConstraintException if any of following condition are met:
    *         <ul>
    *         <li><code>cmis:objectTypeId</code> property value is not an object
    *         type whose baseType is Policy</li>
    *         <li>value of any of the properties violates the
    *         min/max/required/length constraints specified in the property
    *         definition in the object type</li>
    *         <li><code>cmis:objectTypeId</code> property value is NOT in the
    *         list of AllowedChildObjectTypeIds of the parent-folder specified
    *         by folderId</li>
    *         <li>controllablePolicy attribute of the object type definition
    *         specified by the <code>cmis:objectTypeId</code> property value is
    *         set to <code>false</code> and at least one policy is provided</li>
    *         <li>controllableACL attribute of the object type definition
    *         specified by the <code>cmis:objectTypeId</code> property value is
    *         set to <code>false</code> and at least one ACE is provided</li>
    *         <li>at least one of the permissions is used in an ACE provided
    *         which is not supported by the storage</li>
    *         </ul>
    * @throws InvalidArgumentException if object with id <code>folderId</code>
    *         is not a Folder
    * @throws NameConstraintViolationException violation is detected with the
    *         given <code>cmis:name</code> property value. Storage MAY chose
    *         other name which does not conflict
    * @throws StorageException if new Policy can't be saved in storage cause to
    *         storage internal problem
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
      Property<?> typeProperty = properties.get(CmisConstants.OBJECT_TYPE_ID);
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

      ObjectData newPolicy = storage.createPolicy((FolderData)folder, typeId);

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
    * Create a relationship object.
    *
    * @param properties properties to be applied to newly created relationship
    * @param addACL set Access Control Entry to be applied for newly created
    *        relationship
    * @param removeACL set Access Control Entry that MUST be removed from the
    *        newly created relationship
    * @param policies list of policy id that MUST be applied to the newly
    *        created relationship.
    * @return ID of newly created relationship
    * @throws ObjectNotFoundException if <code>cmis:sourceId</code> or
    *         <code>cmis:targetId</code> property value is id of object that
    *         can't be found in storage
    * @throws ConstraintException if any of following condition are met:
    *         <ul>
    *         <li><code>cmis:objectTypeId</code> property value is not an object
    *         type whose baseType is Relationship</li>
    *         <li>value of any of the properties violates the
    *         min/max/required/length constraints specified in the property
    *         definition in the object type</li>
    *         <li>sourceObjectId ObjectType is not in the list of
    *         AllowedSourceTypes specified by the object type definition
    *         specified by <code>cmis:objectTypeId</code> property value</li>
    *         <li>targetObjectId ObjectType is not in the list of
    *         AllowedTargetTypes specified by the object type definition
    *         specified by <code>cmis:objectTypeId</code> property value</li>
    *         <li>controllablePolicy attribute of the object type definition
    *         specified by the <code>cmis:objectTypeId</code> property value is
    *         set to <code>false</code> and at least one policy is provided</li>
    *         <li>controllableACL attribute of the object type definition
    *         specified by the <code>cmis:objectTypeId</code> property value is
    *         set to <code>false</code> and at least one ACE is provided</li>
    *         <li>at least one of the permissions is used in an ACE provided
    *         which is not supported by the storage</li>
    *         </ul>
    * @throws NameConstraintViolationException violation is detected with the
    *         given <code>cmis:name</code> property value. Storage MAY chose
    *         other name which does not conflict
    * @throws StorageException if new Relationship can't be saved in storage
    *         cause to storage internal problem
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
      Property<?> typeProperty = properties.get(CmisConstants.OBJECT_TYPE_ID);
      if (typeProperty != null && typeProperty.getValues().size() > 0)
      {
         typeId = (String)typeProperty.getValues().get(0);
      }
      if (typeId == null)
      {
         throw new InvalidArgumentException("Required Type property ('cmis:objectTypeId') is not specified.");
      }

      String sourceId = null;
      Property<?> sourceProperty = properties.get(CmisConstants.SOURCE_ID);
      if (sourceProperty != null && sourceProperty.getValues().size() > 0)
      {
         sourceId = (String)sourceProperty.getValues().get(0);
      }
      if (sourceId == null)
      {
         throw new InvalidArgumentException("Required Source Id property ('cmis:sourceId') is not specified.");
      }

      String targetId = null;
      Property<?> targetProperty = properties.get(CmisConstants.TARGET_ID);
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
    * Delete the content stream for the specified Document object.
    *
    * @param documentId document id
    * @param changeTokenHolder is used for optimistic locking and/or concurrency
    *        checking to ensure that user updates do not conflict. This
    *        parameter must never be <code>null</code> but
    *        {@link ChangeTokenHolder#getValue()} may return <code>null</code>
    *        if caller does not provide change token. After successful deleting
    *        content stream <code>changeTokenHolder</code> may contains updated
    *        change token if backend support this feature
    * @return ID of updated object
    * @throws ObjectNotFoundException if document with specified id
    *         <code>documentId</code> does not exist
    * @throws ConstraintException if object's type definition
    *         <i>contentStreamAllowed</i> attribute is set to <i>required</i>
    * @throws UpdateConflictException if update an object that is no longer
    *         current. Storage determine this by using change token
    * @throws VersioningException if object is a non-current (latest) document
    *         version and updatiing other then latest version is not supported
    * @throws StorageException if content of document can not be removed cause
    *         to storage internal problem
    */
   public String deleteContentStream(String documentId, ChangeTokenHolder changeTokenHolder)
      throws ObjectNotFoundException, ConstraintException, UpdateConflictException, VersioningException,
      StorageException
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

      ((DocumentData)document).setContentStream(null);

      storage.saveObject(document);

      String changeToken = document.getChangeToken();
      changeTokenHolder.setValue(changeToken);

      return document.getObjectId();
   }

   /**
    * Delete the specified object.
    *
    * @param objectId the object id
    * @param deleteAllVersions if <code>true</code> (Default if not specified)
    *        then delete all versions of the document. If <code>false</code>,
    *        delete only the document object specified. This parameter will be
    *        ignored if parameter when <code>objectId</code> non-document object
    *        or non-versionable document
    * @throws ObjectNotFoundException if object with specified id
    *         <code>objectId</code> does not exist
    * @throws ConstraintException if objectId is folder that contains one or
    *         more children
    * @throws UpdateConflictException if object that is no longer current (as
    *         determined by the storage)
    * @throws StorageException if object can not be removed cause to storage
    *         internal problem
    */
   public void deleteObject(String objectId, Boolean deleteAllVersions) throws ObjectNotFoundException,
      ConstraintException, UpdateConflictException, VersioningException, StorageException
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
    * Delete the specified folder object and all of its child- and
    * descendant-objects.
    *
    * @param folderId folder id
    * @param deleteAllVersions if <code>true</code> (Default if not specified)
    *        then delete all versions of the document. If <code>false</code>,
    *        delete only the document object specified. This parameter will be
    *        ignored if parameter when <code>objectId</code> non-document object
    *        or non-versionable document
    * @param unfileObject an enumeration specifying how the storage MUST process
    *        file-able child objects:
    *        <ul>
    *        <li>unfile: Unfile all fileable objects</li>
    *        <li>deletesinglefiled: Delete all fileable non-folder objects whose
    *        only parent-folders are in the current folder tree. Unfile all
    *        other fileable non-folder objects from the current folder tree</li>
    *        <li>delete: Delete all fileable objects</li>
    *        </ul>
    * @param continueOnFailure if <code>true</code>, then the stprage SHOULD
    *        continue attempting to perform this operation even if deletion of a
    *        child object in the specified folder cannot be deleted. Default is
    *        <code>false</code>.
    * @return list of id that were not deleted
    * @throws ObjectNotFoundException if folder with specified id
    *         <code>folderId</code> does not exist
    * @throws UpdateConflictException if object that is no longer current (as
    *         determined by the storage)
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

      if (((FolderData)folder).isRoot())
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
         storage.deleteTree((FolderData)folder, deleteAllVersions, unfileObject, continueOnFailure);

      return failedDelete;
   }

   /**
    * Get the ACL currently applied to the specified object.
    *
    * @param objectId identifier of object
    * @param onlyBasicPermissions if <code>true</code> then return only the CMIS
    *        Basic permissions
    * @return actual ACL or <code>null</code> if no ACL applied to object
    * @throws ObjectNotFoundException if <code>objectId</code> or does not
    *         exists
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

   /**
    * Get the list of allowable actions for an Object.
    *
    * @param objectId object id
    * @return allowable actions for object
    * @throws ObjectNotFoundException if object with specified id
    *         <code>objectId</code> does not exist
    */
   public AllowableActions getAllowableActions(String objectId) throws ObjectNotFoundException
   {
      checkConnection();
      ObjectData object = storage.getObject(objectId);
      return storage.calculateAllowableActions(object);
   }

   /**
    * Get all documents in version series.
    *
    * @param versionSeriesId version series id
    * @param includeAllowableActions <code>true</code> if allowable actions
    *        should be included in response <code>false</code> otherwise
    * @param includeObjectInfo if <code>true</code> then in result must be
    *        included external information about each object. See
    *        {@link ObjectInfo}. Particular this info may be used by REST Atom
    *        binding for building correct Atom document
    * @param propertyFilter comma-delimited list of property definition Query
    *        Names. A wildcard '*' is supported and minds return all properties.
    *        If empty string or <code>null</code> provided than storage MAY
    *        return storage specific set of properties
    * @return documents in the specified <code>versionSeriesId</code>sorted by
    *         'cmis:creationDate' descending
    * @throws ObjectNotFoundException if object with specified id
    *         <code>versionSeriesId</code> does not exist
    * @throws FilterNotValidException if <code>propertyFilter</code> has invalid
    *         syntax or contains at least one property name that is not in
    *         object's property definition
    */
   public List<CmisObject> getAllVersions(String versionSeriesId, boolean includeAllowableActions,
      boolean includeObjectInfo, String propertyFilter) throws ObjectNotFoundException, FilterNotValidException
   {
      checkConnection();

      Collection<DocumentData> versions = storage.getAllVersions(versionSeriesId);

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
    * Gets the list of policies currently applied to the specified object.
    *
    * @param objectId the object id
    * @param includeObjectInfo if <code>true</code> then in result must be
    *        included external information about each object. See
    *        {@link ObjectInfo}. Particular this info may be used by REST Atom
    *        binding for building correct Atom document
    * @param propertyFilter comma-delimited list of property definition Query
    *        Names. A wildcard '*' is supported and minds return all properties.
    *        If empty string or <code>null</code> provided than storage MAY
    *        return storage specific set of properties
    * @return list of policy objects. If object has not applied policies that
    *         empty list will be returned
    * @throws ObjectNotFoundException if object with <code>objectId</code> does
    *         not exist
    * @throws FilterNotValidException if <code>propertyFilter</code> has invalid
    *         syntax or contains at least one property name that is not in
    *         object's property definition
    */
   public List<CmisObject> getAppliedPolicies(String objectId, boolean includeObjectInfo, String propertyFilter)
      throws ObjectNotFoundException, FilterNotValidException
   {
      checkConnection();

      ObjectData object = storage.getObject(objectId);

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      Collection<PolicyData> policies = object.getPolicies();
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
    * Documents that are checked out that the user has access to.
    *
    * @param folderId folder from which get checked-out documents if null get
    *        all checked-out documents in storage
    * @param includeAllowableActions if <code>true</code> then allowable actions
    *        should be included in response
    * @param includeRelationships indicates what relationships of object must be
    *        returned
    * @param includeObjectInfo if <code>true</code> then result must include
    *        external information about each object. See {@link ObjectInfo}.
    *        Particular this info may be used by REST Atom binding for building
    *        correct Atom document
    * @param propertyFilter comma-delimited list of property definition Query
    *        Names. A wildcard '*' is supported and minds return all properties.
    *        If empty string or <code>null</code> provided than storage MAY
    *        return storage specific set of properties
    * @param renditionFilter renditions kinds or mimetypes that must be included
    *        in result. If <code>null</code> or empty string provided then no
    *        renditions will be returned. The Rendition Filter grammar is
    *        defined as follows:
    *
    *        <pre>
    *        &lt;renditionInclusion&gt; ::= &lt;none&gt; | &lt;wildcard&gt; | &lt;termlist&gt;
    *        &lt;termlist&gt; ::= &lt;term&gt; | &lt;term&gt; ',' &lt;termlist&gt;
    *        &lt;term&gt; ::= &lt;kind&gt; | &lt;mimetype&gt;
    *        &lt;kind&gt; ::= &lt;text&gt;
    *        &lt;mimetype&gt; ::= &lt;type&gt; '/' &lt;subtype&gt;
    *        &lt;type&gt; ::= &lt;text&gt;
    *        &lt;subtype&gt; ::= &lt;text&gt; | &lt;wildcard&gt;
    *        &lt;text&gt; ::= any char except whitespace
    *        &lt;wildcard&gt; ::= '*
    *        &lt;none&gt; ::= 'cmis:none'
    * </pre>
    *
    *        An inclusion pattern allows:
    *        <ul>
    *        <li>Wildcard : include all associated Renditions</li>
    *        <li>Comma-separated list of Rendition kinds or mimetypes : include
    *        only those Renditions that match one of the specified kinds or
    *        mimetypes</li>
    *        <li>cmis:none: exclude all associated Renditions</li>
    *        </ul>
    * @param orderBy comma-separated list of query names and the ascending
    *        modifier 'ASC' or the descending modifier 'DESC' for each query
    *        name. A storage's handling of the orderBy input is storage-specific
    *        and storage may ignore this parameter if it not able sort items
    * @param maxItems max items in result
    * @param skipCount skip items
    * @return checked-out documents
    * @throws ObjectNotFoundException if <code>folderId</code> is not
    *         <code>null</code> and object with <code>folderId</code> was not
    *         found
    * @throws InvalidArgumentException if <code>folderId</code> is id of object
    *         that base type is not a Folder
    * @throws FilterNotValidException if <code>propertyFilter</code> has invalid
    *         syntax or contains at least one property name that is not in
    *         object's property definition or <code>renditionFilter</code> has
    *         invalid syntax or contains at least one unknown rendition
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

      ItemsIterator<DocumentData> iterator = storage.getCheckedOutDocuments(folder, orderBy);

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

      ItemsList<CmisObject> checkedout = new ItemsList<CmisObject>();

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

   /**
    * Get the list of child objects contained in the specified folder.
    *
    * @param folderId folder id
    * @param includeAllowableActions if <code>true</code> then allowable actions
    *        for each should be included in response
    * @param includeRelationships indicates what relationships of object must be
    *        returned
    * @param includePathSegments if <code>true</code> then returns a PathSegment
    *        for each child object
    * @param includeObjectInfo if <code>true</code> then result must include
    *        external information about each object. See {@link ObjectInfo}.
    *        Particular this info may be used by REST Atom binding for building
    *        correct Atom document
    * @param propertyFilter comma-delimited list of property definition Query
    *        Names. A wildcard '*' is supported and minds return all properties.
    *        If empty string or <code>null</code> provided than storage MAY
    *        return storage specific set of properties
    * @param renditionFilter renditions kinds or mimetypes that must be included
    *        in result. If <code>null</code> or empty string provided then no
    *        renditions will be returned. The Rendition Filter grammar is
    *        defined as follows:
    *
    *        <pre>
    *        &lt;renditionInclusion&gt; ::= &lt;none&gt; | &lt;wildcard&gt; | &lt;termlist&gt;
    *        &lt;termlist&gt; ::= &lt;term&gt; | &lt;term&gt; ',' &lt;termlist&gt;
    *        &lt;term&gt; ::= &lt;kind&gt; | &lt;mimetype&gt;
    *        &lt;kind&gt; ::= &lt;text&gt;
    *        &lt;mimetype&gt; ::= &lt;type&gt; '/' &lt;subtype&gt;
    *        &lt;type&gt; ::= &lt;text&gt;
    *        &lt;subtype&gt; ::= &lt;text&gt; | &lt;wildcard&gt;
    *        &lt;text&gt; ::= any char except whitespace
    *        &lt;wildcard&gt; ::= '*
    *        &lt;none&gt; ::= 'cmis:none'
    * </pre>
    *
    *        An inclusion pattern allows:
    *        <ul>
    *        <li>Wildcard : include all associated Renditions</li>
    *        <li>Comma-separated list of Rendition kinds or mimetypes : include
    *        only those Renditions that match one of the specified kinds or
    *        mimetypes</li>
    *        <li>cmis:none: exclude all associated Renditions</li>
    *        </ul>
    * @param orderBy comma-separated list of query names and the ascending
    *        modifier 'ASC' or the descending modifier 'DESC' for each query
    *        name. A storage's handling of the orderBy input is storage-specific
    *        and storage may ignore this parameter if it not able sort items
    * @param maxItems max items in result
    * @param skipCount skip items
    * @return folder's children
    * @throws ObjectNotFoundException if object with <code>folderId</code> was
    *         not found
    * @throws InvalidArgumentException if object with id <code>folderId</code>
    *         is not a Folder
    * @throws FilterNotValidException if <code>propertyFilter</code> has invalid
    *         syntax or contains at least one property name that is not in
    *         object's property definition or <code>renditionFilter</code> has
    *         invalid syntax or contains at least one unknown rendition
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
      ItemsIterator<ObjectData> iterator = ((FolderData)folder).getChildren(orderBy);
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

      ItemsList<CmisObject> cmisChildren = new ItemsList<CmisObject>();
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
    * Gets content changes. This service is intended to be used by search
    * crawlers or other applications that need to efficiently understand what
    * has changed in the storage.
    *
    * @param changeLogToken if {@link ChangeLogTokenHolder#getToken()} return
    *        value other than <code>null</code>, then change event corresponded
    *        to the value of the specified change log token will be returned as
    *        the first result in the output. If not specified, then will be
    *        returned the first change event recorded in the change log. When
    *        set of changes passed is returned then <code>changeLogToken</code>
    *        must contains log token corresponded to the last change event. Then
    *        it may be used by client for getting next set on change events.
    * @param includeProperties if <code>true</code>, then the result includes
    *        the updated property values for 'updated' change events. If
    *        <code>false</code>, then the result will not include the updated
    *        property values for 'updated' change events. The single exception
    *        to this is that the objectId MUST always be included
    * @param propertyFilter comma-delimited list of property definition Query
    *        Names. A wildcard '*' is supported and minds return all properties.
    *        If empty string or <code>null</code> provided than storage MAY
    *        return storage specific set of properties
    * @param includePolicyIDs if <code>true</code>, then the include the IDs of
    *        Policies applied to the object referenced in each change event, if
    *        the change event modified the set of policies applied to the object
    * @param includeAcl if <code>true</code>, then include ACL applied to the
    *        object referenced in each change event
    * @param includeObjectInfo if <code>true</code> then result must include
    *        external information about each object. See {@link ObjectInfo}.
    *        Particular this info may be used by REST Atom binding for building
    *        correct Atom document
    * @param maxItems max items in result
    * @return content changes
    * @throws ConstraintException if the event corresponding to the change log
    *         token provided as an input parameter is no longer available in the
    *         change log. (E.g. because the change log was truncated)
    * @throws FilterNotValidException if <code>propertyFilter</code> has invalid
    *         syntax or contains at least one property name that is not in
    *         object's property definition
    */
   public ItemsList<CmisObject> getContentChanges(ChangeLogTokenHolder changeLogToken, boolean includeProperties,
      String propertyFilter, boolean includePolicyIDs, boolean includeAcl, boolean includeObjectInfo, int maxItems)
      throws ConstraintException, FilterNotValidException
   {
      // TODO : implement
      throw new NotSupportedException("Changes log feature is not supported.");
   }

   /**
    * Get object's content stream.
    *
    * @param objectId object id
    * @param streamId identifier for the rendition stream, when used to get a
    *        rendition stream. For Documents, if not provided then this method
    *        returns the content stream. For Folders (if Folders supports
    *        renditions) this parameter must be provided
    * @param offset first byte of the content to retrieve
    * @param length get exactly number of bytes from content stream
    * @return object content or <code>null</code> if object has not content
    *         stream
    * @throws ObjectNotFoundException if object with specified id
    *         <code>objectId</code> does not exist
    * @throws ConstraintException if the object specified by objectId does NOT
    *         have a content stream or rendition stream
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
         contentStream = ((DocumentData)object).getContentStream();
      }

      if (contentStream == null)
      {
         throw new ConstraintException("Object does not have content stream.");
      }

      return contentStream;
   }

   /**
    * Get the collection of descendant objects contained in the specified folder
    * and any of its child-folders.
    *
    * @param folderId folder id
    * @param depth depth for discover descendants if -1 then discovery
    *        descendants at all levels
    * @param includeAllowableActions if <code>true</code> then allowable actions
    *        for each object should be included in response
    * @param includeRelationships indicates what relationships of object must be
    *        returned
    * @param includePathSegments if <code>true</code> then returns a PathSegment
    *        for each child object
    * @param includeObjectInfo if <code>true</code> then result must include
    *        external information about each object. See {@link ObjectInfo}.
    *        Particular this info may be used by REST Atom binding for building
    *        correct Atom document
    * @param propertyFilter comma-delimited list of property definition Query
    *        Names. A wildcard '*' is supported and minds return all properties.
    *        If empty string or <code>null</code> provided than storage MAY
    *        return storage specific set of properties
    * @param renditionFilter renditions kinds or mimetypes that must be included
    *        in result. If <code>null</code> or empty string provided then no
    *        renditions will be returned. The Rendition Filter grammar is
    *        defined as follows:
    *
    *        <pre>
    *        &lt;renditionInclusion&gt; ::= &lt;none&gt; | &lt;wildcard&gt; | &lt;termlist&gt;
    *        &lt;termlist&gt; ::= &lt;term&gt; | &lt;term&gt; ',' &lt;termlist&gt;
    *        &lt;term&gt; ::= &lt;kind&gt; | &lt;mimetype&gt;
    *        &lt;kind&gt; ::= &lt;text&gt;
    *        &lt;mimetype&gt; ::= &lt;type&gt; '/' &lt;subtype&gt;
    *        &lt;type&gt; ::= &lt;text&gt;
    *        &lt;subtype&gt; ::= &lt;text&gt; | &lt;wildcard&gt;
    *        &lt;text&gt; ::= any char except whitespace
    *        &lt;wildcard&gt; ::= '*
    *        &lt;none&gt; ::= 'cmis:none'
    * </pre>
    *
    *        An inclusion pattern allows:
    *        <ul>
    *        <li>Wildcard : include all associated Renditions</li>
    *        <li>Comma-separated list of Rendition kinds or mimetypes : include
    *        only those Renditions that match one of the specified kinds or
    *        mimetypes</li>
    *        <li>cmis:none: exclude all associated Renditions</li>
    *        </ul>
    * @return folder's tree
    * @throws ObjectNotFoundException if object with <code>folderId</code> was
    *         not found
    * @throws InvalidArgumentException if object with id <code>folderId</code>
    *         is not a Folder
    * @throws FilterNotValidException if <code>propertyFilter</code> has invalid
    *         syntax or contains at least one property name that is not in
    *         object's property definition or <code>renditionFilter</code> has
    *         invalid syntax or contains at least one unknown rendition
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
    * Get parent for specified folder. This method MUST NOT be used for getting
    * parents of other fileable objects.
    *
    * @param folderId folder id
    * @param includeObjectInfo if <code>true</code> then result must include
    *        external information about object. See {@link ObjectInfo}.
    *        Particular this info may be used by REST Atom binding for building
    *        correct Atom document
    * @param propertyFilter comma-delimited list of property definition Query
    *        Names. A wildcard '*' is supported and minds return all properties.
    *        If empty string or <code>null</code> provided than storage MAY
    *        return storage specific set of properties
    * @return folder's parent
    * @throws ObjectNotFoundException if object with <code>folderId</code> was
    *         not found
    * @throws InvalidArgumentException if the <code>folderId</code> is id of the
    *         root folder
    * @throws FilterNotValidException if <code>propertyFilter</code> has invalid
    *         syntax or contains at least one property name that is not in
    *         object's property definition
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

      if (((FolderData)folder).isRoot())
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
    * Get the collection of descendant folder objects contained in the specified
    * folder and any of its child-folders.
    *
    * @param folderId folder id
    * @param depth depth for discover descendants if -1 then discovery
    *        descendants at all levels
    * @param includeAllowableActions if <code>true</code> then allowable actions
    *        for each object should be included in response
    * @param includeRelationships indicates what relationships of object must be
    *        returned
    * @param includePathSegments if <code>true</code> then returns a PathSegment
    *        for each child object
    * @param includeObjectInfo if <code>true</code> then result must include
    *        external information about each object. See {@link ObjectInfo}.
    *        Particular this info may be used by REST Atom binding for building
    *        correct Atom document
    * @param propertyFilter comma-delimited list of property definition Query
    *        Names. A wildcard '*' is supported and minds return all properties.
    *        If empty string or <code>null</code> provided than storage MAY
    *        return storage specific set of properties
    * @param renditionFilter renditions kinds or mimetypes that must be included
    *        in result. If <code>null</code> or empty string provided then no
    *        renditions will be returned. The Rendition Filter grammar is
    *        defined as follows:
    *
    *        <pre>
    *        &lt;renditionInclusion&gt; ::= &lt;none&gt; | &lt;wildcard&gt; | &lt;termlist&gt;
    *        &lt;termlist&gt; ::= &lt;term&gt; | &lt;term&gt; ',' &lt;termlist&gt;
    *        &lt;term&gt; ::= &lt;kind&gt; | &lt;mimetype&gt;
    *        &lt;kind&gt; ::= &lt;text&gt;
    *        &lt;mimetype&gt; ::= &lt;type&gt; '/' &lt;subtype&gt;
    *        &lt;type&gt; ::= &lt;text&gt;
    *        &lt;subtype&gt; ::= &lt;text&gt; | &lt;wildcard&gt;
    *        &lt;text&gt; ::= any char except whitespace
    *        &lt;wildcard&gt; ::= '*
    *        &lt;none&gt; ::= 'cmis:none'
    * </pre>
    *
    *        An inclusion pattern allows:
    *        <ul>
    *        <li>Wildcard : include all associated Renditions</li>
    *        <li>Comma-separated list of Rendition kinds or mimetypes : include
    *        only those Renditions that match one of the specified kinds or
    *        mimetypes</li>
    *        <li>cmis:none: exclude all associated Renditions</li>
    *        </ul>
    * @return folder's tree
    * @throws ObjectNotFoundException if object with <code>folderId</code> was
    *         not found
    * @throws InvalidArgumentException if object with id <code>folderId</code>
    *         is not a Folder
    * @throws FilterNotValidException if <code>propertyFilter</code> has invalid
    *         syntax or contains at least one property name that is not in
    *         object's property definition or <code>renditionFilter</code> has
    *         invalid syntax or contains at least one unknown rendition
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

   /**
    * Get object.
    *
    * @param objectId object id
    * @param includeAllowableActions if <code>true</code> then include object
    *        allowable actions for object
    * @param includeRelationships include object relationships
    * @param includePolicyIDs include policies applied to object
    * @param includeAcl include object's ACL
    * @param includeObjectInfo if <code>true</code> then in result must be
    *        included external information about object. See {@link ObjectInfo}.
    *        Particular this info may be used by REST Atom binding for building
    *        correct Atom document.
    * @param propertyFilter comma-delimited list of property definition Query
    *        Names. A wildcard '*' is supported and minds return all properties.
    *        If empty string or <code>null</code> provided than storage MAY
    *        return storage specific set of properties
    * @param renditionFilter renditions kinds or mimetypes that must be included
    *        in result. If <code>null</code> or empty string provided then no
    *        renditions will be returned. The Rendition Filter grammar is
    *        defined as follows:
    *
    *        <pre>
    *        &lt;renditionInclusion&gt; ::= &lt;none&gt; | &lt;wildcard&gt; | &lt;termlist&gt;
    *        &lt;termlist&gt; ::= &lt;term&gt; | &lt;term&gt; ',' &lt;termlist&gt;
    *        &lt;term&gt; ::= &lt;kind&gt; | &lt;mimetype&gt;
    *        &lt;kind&gt; ::= &lt;text&gt;
    *        &lt;mimetype&gt; ::= &lt;type&gt; '/' &lt;subtype&gt;
    *        &lt;type&gt; ::= &lt;text&gt;
    *        &lt;subtype&gt; ::= &lt;text&gt; | &lt;wildcard&gt;
    *        &lt;text&gt; ::= any char except whitespace
    *        &lt;wildcard&gt; ::= '*
    *        &lt;none&gt; ::= 'cmis:none'
    * </pre>
    *
    *        An inclusion pattern allows:
    *        <ul>
    *        <li>Wildcard : include all associated Renditions</li>
    *        <li>Comma-separated list of Rendition kinds or mimetypes : include
    *        only those Renditions that match one of the specified kinds or
    *        mimetypes</li>
    *        <li>cmis:none: exclude all associated Renditions</li>
    *        </ul>
    * @return retrieval object
    * @throws ObjectNotFoundException if object with specified id
    *         <code>objectId</code> does not exist
    * @throws FilterNotValidException if <code>propertyFilter</code> has invalid
    *         syntax or contains at least one property name that is not in
    *         object's property definition or <code>renditionFilter</code> has
    *         invalid syntax or contains at least one unknown rendition
    */
   public CmisObject getObject(String objectId, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includePolicyIDs, boolean includeAcl,
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
         getCmisObject(objectData, includeAllowableActions, includeRelationships, includePolicyIDs, includeAcl,
            includeObjectInfo, parsedPropertyFilter, parsedRenditionFilter);

      return cmisObject;
   }

   /**
    * Get object by specified path.
    *
    * @param path object's path
    * @param includeAllowableActions <code>true</code> if allowable actions
    *        should be included in response <code>false</code> otherwise
    * @param includeRelationships include object's relationship
    * @param includePolicyIDs include policies IDs applied to object
    * @param includeAcl include ACL
    * @param includeObjectInfo if <code>true</code> then in result must be
    *        included external information about object. See {@link ObjectInfo}.
    *        Particular this info may be used by REST Atom binding for building
    *        correct Atom document
    * @param propertyFilter comma-delimited list of property definition Query
    *        Names. A wildcard '*' is supported and minds return all properties.
    *        If empty string or <code>null</code> provided than storage MAY
    *        return storage specific set of properties
    * @param renditionFilter renditions kinds or mimetypes that must be included
    *        in result. If <code>null</code> or empty string provided then no
    *        renditions will be returned. The Rendition Filter grammar is
    *        defined as follows:
    *
    *        <pre>
    *        &lt;renditionInclusion&gt; ::= &lt;none&gt; | &lt;wildcard&gt; | &lt;termlist&gt;
    *        &lt;termlist&gt; ::= &lt;term&gt; | &lt;term&gt; ',' &lt;termlist&gt;
    *        &lt;term&gt; ::= &lt;kind&gt; | &lt;mimetype&gt;
    *        &lt;kind&gt; ::= &lt;text&gt;
    *        &lt;mimetype&gt; ::= &lt;type&gt; '/' &lt;subtype&gt;
    *        &lt;type&gt; ::= &lt;text&gt;
    *        &lt;subtype&gt; ::= &lt;text&gt; | &lt;wildcard&gt;
    *        &lt;text&gt; ::= any char except whitespace
    *        &lt;wildcard&gt; ::= '*
    *        &lt;none&gt; ::= 'cmis:none'
    * </pre>
    *
    *        An inclusion pattern allows:
    *        <ul>
    *        <li>Wildcard : include all associated Renditions</li>
    *        <li>Comma-separated list of Rendition kinds or mimetypes : include
    *        only those Renditions that match one of the specified kinds or
    *        mimetypes</li>
    *        <li>cmis:none: exclude all associated Renditions</li>
    *        </ul>
    * @return retrieval object
    * @throws ObjectNotFoundException if object with specified <code>path</code>
    *         does not exists
    * @throws FilterNotValidException if <code>propertyFilter</code> has invalid
    *         syntax or contains at least one property name that is not in
    *         object's property definition or <code>renditionFilter</code> has
    *         invalid syntax or contains at least one unknown rendition
    */
   public CmisObject getObjectByPath(String path, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includePolicyIDs, boolean includeAcl,
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
         getCmisObject(object, includeAllowableActions, includeRelationships, includePolicyIDs, includeAcl,
            includeObjectInfo, parsedPropertyFilter, parsedRenditionFilter);

      return cmis;
   }

   /**
    * Get the latest Document object in the version series.
    *
    * @param versionSeriesId version series id
    * @param major if <code>true</code> then return the properties for the
    *        latest major version object in the Version Series, otherwise return
    *        the properties for the latest (major or non-major) version. If the
    *        input parameter major is <code>true</code> and the Version Series
    *        contains no major versions, then the ObjectNotFoundException will
    *        be thrown.
    * @param includeAllowableActions <code>true</code> if allowable actions
    *        should be included in response <code>false</code> otherwise
    * @param includeRelationships include object's relationship
    * @param includePolicyIDs include policies IDs applied to object
    * @param includeAcl include ACL
    * @param includeObjectInfo if <code>true</code> then in result must be
    *        included external information about object. See {@link ObjectInfo}.
    *        Particular this info may be used by REST Atom binding for building
    *        correct Atom document
    * @param propertyFilter comma-delimited list of property definition Query
    *        Names. A wildcard '*' is supported and minds return all properties.
    *        If empty string or <code>null</code> provided than storage MAY
    *        return storage specific set of properties
    * @param renditionFilter renditions kinds or mimetypes that must be included
    *        in result. If <code>null</code> or empty string provided then no
    *        renditions will be returned. The Rendition Filter grammar is
    *        defined as follows:
    *
    *        <pre>
    *        &lt;renditionInclusion&gt; ::= &lt;none&gt; | &lt;wildcard&gt; | &lt;termlist&gt;
    *        &lt;termlist&gt; ::= &lt;term&gt; | &lt;term&gt; ',' &lt;termlist&gt;
    *        &lt;term&gt; ::= &lt;kind&gt; | &lt;mimetype&gt;
    *        &lt;kind&gt; ::= &lt;text&gt;
    *        &lt;mimetype&gt; ::= &lt;type&gt; '/' &lt;subtype&gt;
    *        &lt;type&gt; ::= &lt;text&gt;
    *        &lt;subtype&gt; ::= &lt;text&gt; | &lt;wildcard&gt;
    *        &lt;text&gt; ::= any char except whitespace
    *        &lt;wildcard&gt; ::= '*
    *        &lt;none&gt; ::= 'cmis:none'
    * </pre>
    *
    *        An inclusion pattern allows:
    *        <ul>
    *        <li>Wildcard : include all associated Renditions</li>
    *        <li>Comma-separated list of Rendition kinds or mimetypes : include
    *        only those Renditions that match one of the specified kinds or
    *        mimetypes</li>
    *        <li>cmis:none: exclude all associated Renditions</li>
    *        </ul>
    * @return object of latest version in version series
    * @throws ObjectNotFoundException if Version Series with id
    *         <code>versionSeriesId</code> does not exists or the input
    *         parameter <code>major</code> is <code>true</code> and the Version
    *         Series contains no major versions.
    * @throws FilterNotValidException if <code>propertyFilter</code> has invalid
    *         syntax or contains at least one property name that is not in
    *         object's property definition or <code>renditionFilter</code> has
    *         invalid syntax or contains at least one unknown rendition
    */
   public CmisObject getObjectOfLatestVersion(String versionSeriesId, boolean major, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includePolicyIDs, boolean includeAcl,
      boolean includeObjectInfo, String propertyFilter, String renditionFilter) throws ObjectNotFoundException,
      FilterNotValidException
   {
      checkConnection();

      Collection<DocumentData> versions = storage.getAllVersions(versionSeriesId);

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
      List<DocumentData> v = new ArrayList<DocumentData>(versions);
      Collections.sort(v, CmisUtils.versionComparator);

      if (!major)
      {
         return getCmisObject(v.get(0), includeAllowableActions, includeRelationships, false, false, includeObjectInfo,
            parsedPropertyFilter, parsedRenditionFilter);
      }

      for (DocumentData document : v)
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
    * Gets the parent folder(s) for the specified object.
    *
    * @param objectId object id
    * @param includeAllowableActions if <code>true</code> then allowable actions
    *        should be included in response
    * @param includeRelationships indicates what relationships of object must be
    *        returned
    * @param includeRelativePathSegment if <code>true</code>, returns a
    *        PathSegment for each child object
    * @param includeObjectInfo if <code>true</code> then result must include
    *        external information about object. See {@link ObjectInfo} and
    *        {@link ObjectParent#getObject()}. Particular this info may be used
    *        by REST Atom binding for building correct Atom document
    * @param propertyFilter comma-delimited list of property definition Query
    *        Names. A wildcard '*' is supported and minds return all properties.
    *        If empty string or <code>null</code> provided than storage MAY
    *        return storage specific set of properties
    * @param renditionFilter renditions kinds or mimetypes that must be included
    *        in result. If <code>null</code> or empty string provided then no
    *        renditions will be returned. The Rendition Filter grammar is
    *        defined as follows:
    *
    *        <pre>
    *        &lt;renditionInclusion&gt; ::= &lt;none&gt; | &lt;wildcard&gt; | &lt;termlist&gt;
    *        &lt;termlist&gt; ::= &lt;term&gt; | &lt;term&gt; ',' &lt;termlist&gt;
    *        &lt;term&gt; ::= &lt;kind&gt; | &lt;mimetype&gt;
    *        &lt;kind&gt; ::= &lt;text&gt;
    *        &lt;mimetype&gt; ::= &lt;type&gt; '/' &lt;subtype&gt;
    *        &lt;type&gt; ::= &lt;text&gt;
    *        &lt;subtype&gt; ::= &lt;text&gt; | &lt;wildcard&gt;
    *        &lt;text&gt; ::= any char except whitespace
    *        &lt;wildcard&gt; ::= '*
    *        &lt;none&gt; ::= 'cmis:none'
    * </pre>
    *
    *        An inclusion pattern allows:
    *        <ul>
    *        <li>Wildcard : include all associated Renditions</li>
    *        <li>Comma-separated list of Rendition kinds or mimetypes : include
    *        only those Renditions that match one of the specified kinds or
    *        mimetypes</li>
    *        <li>cmis:none: exclude all associated Renditions</li>
    *        </ul>
    * @return object's parents. Empty list for unfiled objects or for the root
    *         folder
    * @throws ObjectNotFoundException if object with <code>objectId</code> was
    *         not found
    * @throws ConstraintException if this method is invoked on an not fileable
    *         object
    * @throws FilterNotValidException if <code>propertyFilter</code> has invalid
    *         syntax or contains at least one property name that is not in
    *         object's property definition or <code>renditionFilter</code> has
    *         invalid syntax or contains at least one unknown rendition
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

      Collection<FolderData> parents = object.getParents();

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
            new ObjectParent(cmisParent, includeRelativePathSegment ? parent.getName() : null);

         cmisParents.add(parentType);
      }

      return cmisParents;
   }

   /**
    * Get all or a subset of relationships associated with an independent
    * object.
    *
    * @param objectId object id
    * @param direction relationship direction
    * @param typeId relationship type id. If <code>null</code> then return
    *        relationships of all types
    * @param includeSubRelationshipTypes if <code>true</code>, then the return
    *        all relationships whose object types are descendant types of
    *        <code>typeId</code>.
    * @param includeAllowableActions if <code>true</code> then allowable actions
    *        should be included in response
    * @param includeObjectInfo if <code>true</code> then in result must be
    *        included external information about each object. See
    *        {@link ObjectInfo}. Particular this info may be used by REST Atom
    *        binding for building correct Atom document
    * @param propertyFilter property filter as string
    * @param maxItems max items in result
    * @param skipCount skip items
    * @return object's relationships
    * @throws ObjectNotFoundException if object with <code>objectId</code> does
    *         not exist
    * @throws FilterNotValidException if <code>propertyFilter</code> has invalid
    *         syntax or contains at least one property name that is not in
    *         object's property definition
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

      ItemsIterator<RelationshipData> iterator = object.getRelationships(direction, type, includeSubRelationshipTypes);

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
      ItemsList<CmisObject> relationships = new ItemsList<CmisObject>();

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

   /**
    * Get object's properties.
    *
    * @param objectId object id
    * @param includeObjectInfo if <code>true</code> then in result must be
    *        included external information about object. See {@link ObjectInfo}.
    *        Particular this info may be used by REST Atom binding for building
    *        correct Atom document
    * @param propertyFilter comma-delimited list of property definition Query
    *        Names. A wildcard '*' is supported and minds return all properties.
    *        If empty string or <code>null</code> provided than storage MAY
    *        return storage specific set of properties
    * @return CMIS object that contains properties
    * @throws ObjectNotFoundException if object with specified id
    *         <code>objectId</code> does not exist
    * @throws FilterNotValidException if <code>propertyFilter</code> has invalid
    *         syntax or contains at least one property name that is not in
    *         object's property definition
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
    * Get properties of latest version in version series.
    *
    * @param versionSeriesId version series id
    * @param major if <code>true</code> then return the properties for the
    *        latest major version object in the Version Series, otherwise return
    *        the properties for the latest (major or non-major) version. If the
    *        input parameter major is <code>true</code> and the Version Series
    *        contains no major versions, then the ObjectNotFoundException will
    *        be thrown.
    * @param includeObjectInfo if <code>true</code> then in result must be
    *        included external information about object. See {@link ObjectInfo}.
    *        Particular this info may be used by REST Atom binding for building
    *        correct Atom document
    * @param propertyFilter comma-delimited list of property definition Query
    *        Names. A wildcard '*' is supported and minds return all properties.
    *        If empty string or <code>null</code> provided than storage MAY
    *        return storage specific set of properties
    * @return CMIS object that contains properties of latest version of object
    *         in version series
    * @throws ObjectNotFoundException if Version Series with id
    *         <code>versionSeriesId</code> does not exists or the input
    *         parameter <code>major</code> is <code>true</code> and the Version
    *         Series contains no major versions.
    * @throws FilterNotValidException if <code>propertyFilter</code> has invalid
    *         syntax or contains at least one property name that is not in
    *         object's property definition
    */
   public CmisObject getPropertiesOfLatestVersion(String versionSeriesId, boolean major, boolean includeObjectInfo,
      String propertyFilter) throws FilterNotValidException, ObjectNotFoundException
   {
      return getObjectOfLatestVersion(versionSeriesId, major, false, null, false, false, includeObjectInfo,
         propertyFilter, RenditionFilter.NONE_FILTER);
   }

   /**
    * Get the list of associated Renditions for the specified object. Only
    * rendition attributes are returned, not rendition stream.
    *
    * @param objectId object id
    * @param renditionFilter renditions kinds or mimetypes that must be included
    *        in result. If <code>null</code> or empty string provided then no
    *        renditions will be returned. The Rendition Filter grammar is
    *        defined as follows:
    *
    *        <pre>
    *        &lt;renditionInclusion&gt; ::= &lt;none&gt; | &lt;wildcard&gt; | &lt;termlist&gt;
    *        &lt;termlist&gt; ::= &lt;term&gt; | &lt;term&gt; ',' &lt;termlist&gt;
    *        &lt;term&gt; ::= &lt;kind&gt; | &lt;mimetype&gt;
    *        &lt;kind&gt; ::= &lt;text&gt;
    *        &lt;mimetype&gt; ::= &lt;type&gt; '/' &lt;subtype&gt;
    *        &lt;type&gt; ::= &lt;text&gt;
    *        &lt;subtype&gt; ::= &lt;text&gt; | &lt;wildcard&gt;
    *        &lt;text&gt; ::= any char except whitespace
    *        &lt;wildcard&gt; ::= '*
    *        &lt;none&gt; ::= 'cmis:none'
    * </pre>
    *
    *        An inclusion pattern allows:
    *        <ul>
    *        <li>Wildcard : include all associated Renditions</li>
    *        <li>Comma-separated list of Rendition kinds or mimetypes : include
    *        only those Renditions that match one of the specified kinds or
    *        mimetypes</li>
    *        <li>cmis:none: exclude all associated Renditions</li>
    *        </ul>
    * @param maxItems max items in response
    * @param skipCount skip specified number of objects in response
    * @return object's renditions
    * @throws ObjectNotFoundException if object with specified
    *         <code>objectId</code> does not exists
    * @throws FilterNotValidException if <code>renditionFilter</code> has
    *         invalid syntax or contains at least one unknown rendition
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

   /**
    * Gets the storage associated to this connection.
    *
    * @return storage
    */
   public Storage getStorage()
   {
      return storage;
   }

   /**
    * Set of object types.
    *
    * @param typeId the type id, if not <code>null</code> then return only
    *        specified Object Type and its direct descendant. If
    *        <code>null</code> then return base types.
    * @param includePropertyDefinition <code>true</code> if property definition
    *        should be included <code>false</code> otherwise
    * @param maxItems max number of items in response
    * @param skipCount skip items
    * @return list of all base types or specified object type and its direct
    *         children
    * @throws TypeNotFoundException if type <code>typeId</code> does not exist
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

      ItemsList<TypeDefinition> typeChildren = new ItemsList<TypeDefinition>();

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
    * Get type definition for type <code>typeId</code> include property
    * definition, see {@link #getTypeDefinition(String, boolean)}
    *
    * @param typeId type Id
    * @return type definition
    * @throws TypeNotFoundException if type <code>typeId</code> does not exist
    */
   public TypeDefinition getTypeDefinition(String typeId) throws TypeNotFoundException
   {
      return getTypeDefinition(typeId, true);
   }

   /**
    * Get type definition for type <code>typeId</code>.
    *
    * @param typeId type Id
    * @param includePropertyDefinition if <code>true</code> property definition
    *        should be included
    * @return type definition
    * @throws TypeNotFoundException if type <code>typeId</code> does not exist
    */
   public TypeDefinition getTypeDefinition(String typeId, boolean includePropertyDefinition)
      throws TypeNotFoundException
   {
      checkConnection();

      return storage.getTypeDefinition(typeId, includePropertyDefinition);
   }

   /**
    * Get all descendants of specified <code>typeId</code> in hierarchy. If
    * <code>typeId</code> is <code>null</code> then return all types and ignore
    * the value of the <code>depth</code> parameter.
    *
    * @param typeId the type id
    * @param depth the depth of level in hierarchy
    * @param includePropertyDefinition true if property definition should be
    *        included false otherwise
    * @return list of descendant types
    * @throws TypeNotFoundException if type <code>typeId</code> does not exist
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

   /**
    * Moves the specified file-able object from one folder to another.
    *
    * @param objectId object id
    * @param targetFolderId target folder for moving object
    * @param sourceFolderId move object from which object to be moved
    * @return moved object ID
    * @throws ObjectNotFoundException if object with <code>objectId</code> or
    *         <code>sourceFolderId</code> or <code>targetFolderId</code> were
    *         not found
    * @throws ConstraintException if type of the given object is NOT in the list
    *         of AllowedChildObjectTypeIds of the parent-folder specified by
    *         <code>targetFolderId</code>
    * @throws InvalidArgumentException if the service is invoked with a missing
    *         <code>sourceFolderId</code> or the <code>sourceFolderId</code>
    *         doesn't match the specified object's parent folder (or one of the
    *         parent folders if the storage supports multifiling.).
    * @throws UpdateConflictException if object that is no longer current (as
    *         determined by the storage)
    * @throws VersioningException if object is a non-current (latest) document
    *         version
    * @throws StorageException if object can not be moved (save changes) cause
    *         to storage internal problem
    */
   public String moveObject(String objectId, String targetFolderId, String sourceFolderId)
      throws ObjectNotFoundException, ConstraintException, InvalidArgumentException, UpdateConflictException,
      VersioningException, StorageException
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

      ObjectData movedObject = storage.moveObject(object, (FolderData)target, (FolderData)source);

      return movedObject.getObjectId();
   }

   /**
    * Executes a CMIS-SQL query statement against the contents of the CMIS
    * Storage.
    *
    * @param statement SQL statement
    * @param searchAllVersions if <code>false</code>, then include latest
    *        versions of documents in the query search scope otherwise all
    *        versions. If the Storage does not support the optional
    *        capabilityAllVersionsSearchable capability, then this parameter
    *        value MUST be set to <code>false</code>
    * @param includeAllowableActions if <code>true</code> return allowable
    *        actions in request
    * @param includeRelationships indicates what relationships of object must be
    *        returned
    * @param includeObjectInfo if <code>true</code> then result must include
    *        external information about each object. See {@link ObjectInfo}.
    *        Particular this info may be used by REST Atom binding for building
    *        correct Atom document
    * @param renditionFilter renditions kinds or mimetypes that must be included
    *        in result. If <code>null</code> or empty string provided then no
    *        renditions will be returned. The Rendition Filter grammar is
    *        defined as follows:
    *
    *        <pre>
    *        &lt;renditionInclusion&gt; ::= &lt;none&gt; | &lt;wildcard&gt; | &lt;termlist&gt;
    *        &lt;termlist&gt; ::= &lt;term&gt; | &lt;term&gt; ',' &lt;termlist&gt;
    *        &lt;term&gt; ::= &lt;kind&gt; | &lt;mimetype&gt;
    *        &lt;kind&gt; ::= &lt;text&gt;
    *        &lt;mimetype&gt; ::= &lt;type&gt; '/' &lt;subtype&gt;
    *        &lt;type&gt; ::= &lt;text&gt;
    *        &lt;subtype&gt; ::= &lt;text&gt; | &lt;wildcard&gt;
    *        &lt;text&gt; ::= any char except whitespace
    *        &lt;wildcard&gt; ::= '*
    *        &lt;none&gt; ::= 'cmis:none'
    * </pre>
    *
    *        An inclusion pattern allows:
    *        <ul>
    *        <li>Wildcard : include all associated Renditions</li>
    *        <li>Comma-separated list of Rendition kinds or mimetypes : include
    *        only those Renditions that match one of the specified kinds or
    *        mimetypes</li>
    *        <li>cmis:none: exclude all associated Renditions</li>
    *        </ul>
    * @param maxItems max items in result
    * @param skipCount skip items
    * @return query results
    * @throws FilterNotValidException if <code>renditionFilter</code> has
    *         invalid syntax or contains unknown rendition kinds or mimetypes
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

      ItemsList<CmisObject> list = new ItemsList<CmisObject>();

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

   /**
    * Remove an existing fileable non-folder object from a folder.
    *
    * @param objectId the id of object to be removed
    * @param folderId the folder from which the object is to be removed. If
    *        null, then remove the object from all folders in which it is
    *        currently filed
    * @throws ObjectNotFoundException if <code>objectId</code> or
    *         <code>folderId</code> were not found
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

         ((FolderData)folder).removeObject(object);
      }
      else
      {
         storage.unfileObject(object);
      }

   }

   /**
    * Removes a specified policy from an object.
    *
    * @param policyId id of policy to be removed from object
    * @param objectId id of object
    * @throws ObjectNotFoundException if object with <code>objectId</code> does
    *         not exist
    * @throws ConstraintException if object with id <code>objectId</code> is not
    *         controllable by policy
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

      object.removePolicy((PolicyData)policyData);

      storage.saveObject(object);
   }

   /**
    * Remove type definition for type <code>typeId</code> .
    *
    * @param typeId type Id
    * @throws TypeNotFoundException if type <code>typeId</code> does not exist
    * @throws ConstraintException if removing type violates a storage
    *         constraint. For example, if storage already contains object of
    *         this type
    * @throws StorageException if type can't be removed (save changes) cause to
    *         storage internal problem
    */
   public void removeType(String typeId) throws TypeNotFoundException, ConstraintException, StorageException
   {
      checkConnection();

      storage.removeType(typeId);
   }

   /**
    * Sets the content stream for the specified Document object.
    *
    * @param documentId document id
    * @param content content stream to be applied to object
    * @param changeTokenHolder is used for optimistic locking and/or concurrency
    *        checking to ensure that user updates do not conflict. This
    *        parameter must never be <code>null</code> but
    *        {@link ChangeTokenHolder#getValue()} may return <code>null</code>
    *        if caller does not provide change token. After successful updating
    *        content stream <code>changeTokenHolder</code> may contains updated
    *        change token if backend support this feature
    * @param overwriteFlag if <code>true</code> on object's content stream
    *        exists it will be overridden. If <code>false</code> and context
    *        stream already exists then ContentAlreadyExistsException will be
    *        thrown
    * @return ID of updated object
    * @throws ObjectNotFoundException if document with specified id
    *         <code>documentId</code> does not exist
    * @throws ContentAlreadyExistsException if the input parameter
    *         <code>overwriteFlag</code> is <code>false</code> and the Object
    *         already has a content-stream
    * @throws StreamNotSupportedException will be thrown if the
    *         contentStreamAllowed attribute of the object type definition
    *         specified by the <code>cmis:objectTypeId</code> property value of
    *         the given document is set to not allowed
    * @throws UpdateConflictException if update an object that is no longer
    *         current. Storage determine this by using change token
    * @throws VersioningException if object is a non-current (latest) document
    *         version and updatiing other then latest version is not supported
    * @throws IOException if any i/o error occurs when try to set document's
    *         content stream
    * @throws StorageException if object's content stream can not be updated
    *         (save changes) cause to storage internal problem
    */
   public String setContentStream(String documentId, ContentStream content, ChangeTokenHolder changeTokenHolder,
      boolean overwriteFlag) throws ObjectNotFoundException, ContentAlreadyExistsException,
      StreamNotSupportedException, UpdateConflictException, VersioningException, IOException, StorageException
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

      if (!overwriteFlag && ((DocumentData)document).hasContent())
      {
         throw new ContentAlreadyExistsException("Document already has content stream and 'overwriteFlag' is false.");
      }

      // Validate change token, object may be already updated.
      validateChangeToken(document, changeTokenHolder.getValue());

      ((DocumentData)document).setContentStream(content);

      storage.saveObject(document);

      String changeToken = document.getChangeToken();
      changeTokenHolder.setValue(changeToken);

      return document.getObjectId();
   }

   /**
    * Update object properties.
    *
    * @param objectId object id
    * @param changeTokenHolder is used for optimistic locking and/or concurrency
    *        checking to ensure that user updates do not conflict. This
    *        parameter must never be <code>null</code> but
    *        {@link ChangeTokenHolder#getValue()} may return <code>null</code>
    *        if caller does not provide change token. After successful updating
    *        properties <code>changeTokenHolder</code> may contains updated
    *        change token if backend support this feature
    * @param properties properties to be applied for object
    * @return ID of updated object
    * @throws ObjectNotFoundException if document with specified id
    *         <code>objectId</code> does not exist
    * @throws ConstraintException if value of any of the properties violates the
    *         min/max/required/length constraints specified in the property
    *         definition in the object type
    * @throws NameConstraintViolationException if <i>cmis:name</i> specified in
    *         properties throws conflict
    * @throws UpdateConflictException if update an object that is no longer
    *         current. Storage determine this by using change token
    * @throws VersioningException if any of following conditions are met:
    *         <ul>
    *         <li>The object is not checked out and any of the properties being
    *         updated are defined in their object type definition have an
    *         attribute value of Updatability 'when checked-out'</li>
    *         <li>if object is a non-current (latest) document version and
    *         updatiing other then latest version is not supported</li>
    *         </ul>
    * @throws StorageException if object's properties can not be updated (save
    *         changes) cause to storage internal problem
    */
   public String updateProperties(String objectId, ChangeTokenHolder changeTokenHolder,
      Map<String, Property<?>> properties) throws ObjectNotFoundException, ConstraintException,
      NameConstraintViolationException, UpdateConflictException, VersioningException, StorageException
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
         object.applyPolicy((PolicyData)policy);
      }
   }

   private List<ItemsTree<CmisObject>> getTree(String folderId, int depth, BaseType typeFilter,
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

      for (ItemsIterator<ObjectData> children = ((FolderData)folder).getChildren(null); children.hasNext();)
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

         tree.add(new ItemsTree<CmisObject>(container, subTree));
      }

      return tree;
   }

   private List<ItemsTree<TypeDefinition>> getTypeTree(String typeId, int depth, boolean includePropertyDefinition)
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

         tree.add(new ItemsTree<TypeDefinition>(childType, subTree));
      }

      return tree;
   }

   /**
    * Check is connection may be used at the moment, e.g. it may be already
    * closed.
    *
    * @throws IllegalStateException if connection may not be used any more
    */
   protected abstract void checkConnection() throws IllegalStateException;

   protected CmisObject getCmisObject(ObjectData object, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includePolicyIds, boolean includeACL,
      boolean includeObjectInfo, PropertyFilter parsedPropertyFilter, RenditionFilter parsedRenditionFilter)
   {
      CmisObject cmis = new CmisObject();

      Map<String, Property<?>> properties = object.getProperties(parsedPropertyFilter);
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
            for (ItemsIterator<RelationshipData> iter = object.getRelationships(direction, null, true); iter.hasNext();)
            {
               RelationshipData next = iter.next();
               cmis.getRelationship().add(
                  getCmisObject(next, false, includeRelationships, false, false, includeObjectInfo, PropertyFilter.ALL,
                     RenditionFilter.NONE));
            }
         }
      }
      if (includePolicyIds)
      {
         for (Iterator<PolicyData> iter = object.getPolicies().iterator(); iter.hasNext();)
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

         ObjectInfo objectInfo = new ObjectInfo();
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
            objectInfo.setParentId(((FolderData)object).isRoot() ? null : object.getParent().getObjectId());
         }
         else if (baseType == BaseType.DOCUMENT)
         {
            DocumentData doc = (DocumentData)object;
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
            RelationshipData rel = (RelationshipData)object;
            objectInfo.setSourceId(rel.getSourceId());
            objectInfo.setTargetId(rel.getTargetId());
         }

         cmis.setObjectInfo(objectInfo);
      }
      return cmis;
   }

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

}
