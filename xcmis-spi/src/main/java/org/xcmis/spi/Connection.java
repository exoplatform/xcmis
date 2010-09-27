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

import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.AccessControlPropagation;
import org.xcmis.spi.model.AllowableActions;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.CapabilityACL;
import org.xcmis.spi.model.CapabilityRendition;
import org.xcmis.spi.model.ChangeEvent;
import org.xcmis.spi.model.ChangeInfo;
import org.xcmis.spi.model.ChangeType;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.ContentStreamAllowed;
import org.xcmis.spi.model.IncludeRelationships;
import org.xcmis.spi.model.ObjectInfo;
import org.xcmis.spi.model.ObjectParent;
import org.xcmis.spi.model.Permission;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.RelationshipDirection;
import org.xcmis.spi.model.Rendition;
import org.xcmis.spi.model.RepositoryCapabilities;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.UnfileObject;
import org.xcmis.spi.model.Updatability;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.model.Permission.BasicPermissions;
import org.xcmis.spi.model.impl.DecimalProperty;
import org.xcmis.spi.model.impl.IdProperty;
import org.xcmis.spi.query.Query;
import org.xcmis.spi.query.Result;
import org.xcmis.spi.query.Score;
import org.xcmis.spi.utils.CmisUtils;
import org.xcmis.spi.utils.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

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

   protected static final int CREATE = 1;

   protected static final int UPDATE = 2;

   protected static final int VERSION = 4;

   private static final Logger LOG = Logger.getLogger(Connection.class);

   protected Storage storage;

   public Connection(Storage storage)
   {
      this.storage = storage;
   }

   /**
    * Adds an existing fileable non-folder object to a folder.
    *
    * 2.2.5.1 addObjectToFolder
    *
    *
    * @param objectId the id of the object
    * @param folderId the target folder id into which the object is to be filed
    * @param allVersions to add all versions of the object to the folder or only
    *        current document if the storage supports version-specific filing
    * @throws ObjectNotFoundException if <code>objectId</code> or
    *         <code>folderId</code> were not found
    * @throws ConstraintException MUST throw this exception if the
    *         cmis:objectTypeId property value of the given object is NOT in the
    *         list of AllowedChildObjectTypeIds of the parent-folder specified
    *         by folderId or if <code>allVersions</code> is <code>false</code>
    *         but version-specific filling capability is not supported by
    *         storage
    * @throws InvalidArgumentException if <code>objectId</code> is id of object
    *         that is not fileable or if <code>folderId</code> is id of object
    *         that base type is not Folder
    * @throws NotSupportedException if multifiling feature is not supported by
    *         backend storage
    * @see RepositoryCapabilities#isCapabilityVersionSpecificFiling()
    */
   public void addObjectToFolder(String objectId, String folderId, boolean allVersions) throws ObjectNotFoundException,
      ConstraintException
   {
      checkConnection();

      if (!storage.getRepositoryInfo().getCapabilities().isCapabilityMultifiling())
      {
         throw new NotSupportedException("Multi-filing is not supported.");
      }

      if (!allVersions && !storage.getRepositoryInfo().getCapabilities().isCapabilityVersionSpecificFiling())
      {
         throw new ConstraintException("Version-specific filling capability is not supported.");
      }

      ObjectData object = storage.getObjectById(objectId);
      ObjectData folder = storage.getObjectById(folderId);

      if (folder.getBaseType() != BaseType.FOLDER)
      {
         throw new InvalidArgumentException("Object " + folderId + " is not a folder object.");
      }

      if (!object.getTypeDefinition().isFileable())
      {
         throw new InvalidArgumentException("Object " + objectId + " is not fileable.");
      }

      if (!((FolderData)folder).isAllowedChildType(object.getTypeId()))
      {
         throw new ConstraintException("Object type " + object.getTypeId()
            + " is not allowed as child for destination folder");
      }

      ((FolderData)folder).addObject(object);
   }

   /**
    * Adds the new Object-type.
    *
    * It is not a standard CMIS feature (xCMIS specific)
    *
    * 2.1.3 Object-Type A repository MAY define additional object-types beyond
    * the CMIS Base Object-Types
    *
    *
    * @param type type definition
    * @return ID of newly added type
    * @throws ConstraintException if any of the following conditions are met:
    *         <ul>
    *         <li>Storage already has type with the same id, see
    *         {@link TypeDefinition#getId()}</li>
    *         <li>Base type is not specified or is one of optional type that is
    *         not supported by storage, see {@link TypeDefinition#getBaseId()}</li>
    *         <li>Parent type is not specified or does not exist, see
    *         {@link TypeDefinition#getParentId()}</li>
    *         <li>New type has at least one property definitions that has
    *         unsupported type, invalid id, so on</li>
    *         </ul>
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
    * Adds or(and) removes the given Access Control Entries to(from) the Access
    * Control List of object.
    *
    * 2.2.10.2 applyACL
    *
    * @param objectId the identifier of object for which should be applied
    *        specified ACEs
    * @param addACL the ACEs that will be added from object's ACL. May be
    *        <code>null</code> or empty list
    * @param removeACL the ACEs that will be removed from object's ACL. May be
    *        <code>null</code> or empty list
    * @param propagation specifies how ACEs should be handled:
    *        <ul>
    *        <li>objectonly: ACEs must be applied without changing the ACLs of
    *        other objects</li>
    *        <li>propagate: ACEs must be applied by propagate the changes to all
    *        inheriting objects</li>
    *        <li>repositorydetermined: Indicates that the client leaves the
    *        behavior to the storage</li>
    *        </ul>
    * @throws ObjectNotFoundException if object with <code>objectId</code> does
    *         not exist
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
    * @throws NotSupportedException if managing of ACL is not supported by
    *         backend storage
    */
   public void applyACL(String objectId, List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL,
      AccessControlPropagation propagation) throws ObjectNotFoundException, ConstraintException
   {
      if ((addACL == null || addACL.size() == 0) && (removeACL == null || removeACL.size() == 0))
      {
         return;
      }
      checkConnection();

      if (propagation == null)
      {
         propagation = AccessControlPropagation.REPOSITORYDETERMINED;
      }

      AccessControlPropagation storagePropagation = storage.getRepositoryInfo().getAclCapability().getPropagation();
      if (propagation != storagePropagation)
      {
         throw new ConstraintException("Specified ACL propagation '" + propagation
            + "' does not to supported by repository '" + storagePropagation + "' ");
      }

      ObjectData object = storage.getObjectById(objectId);
      TypeDefinition typeDefinition = object.getTypeDefinition();
      checkACL(typeDefinition, addACL, removeACL);
      // Merge ACL include existed one. It may be inherited from parent even for newly created object .
      List<AccessControlEntry> mergedACL = CmisUtils.mergeACLs(object.getACL(false), addACL, removeACL);
      object.setACL(mergedACL);
   }

   /**
    * Applies a specified policy to an object.
    *
    * 2.2.9.1 applyPolicy
    *
    * @param policyId the policy Id to be applied to object
    * @param objectId the target object Id for policy
    * @throws ObjectNotFoundException if object with <code>objectId</code> or
    *         <code>policyId</code> does not exist
    * @throws ConstraintException if object with id <code>objectId</code> is not
    *         controllable by policy
    * @throws InvalidArgumentException if object with id <code>policyId</code>
    *         is not object which base type is Policy
    */
   public void applyPolicy(String policyId, String objectId) throws ConstraintException, ObjectNotFoundException
   {
      checkConnection();

      ObjectData object = storage.getObjectById(objectId);
      if (!object.getTypeDefinition().isControllablePolicy())
      {
         throw new ConstraintException("Object type " + object.getTypeId() + " is not controllable by policy.");
      }
      ObjectData policy = storage.getObjectById(policyId);
      if (policy.getBaseType() != BaseType.POLICY)
      {
         throw new InvalidArgumentException("Object " + policy.getObjectId() + " is not a Policy object.");
      }
      object.applyPolicy((PolicyData)policy);
   }

   /**
    * Discard the check-out operation. As result Private Working Copy (PWC) must
    * be removed and storage ready to next check-out operation.
    *
    * 2.2.7.2 cancelCheckOut
    *
    * @param documentId document id. May be PWC id or id of any other Document
    *        in Version Series
    * @throws ObjectNotFoundException if object with <code>documentId</code>
    *         does not exist
    * @throws ConstraintException if the object is not versionable
    * @throws UpdateConflictException if update an object that is no longer
    *         current
    * @throws VersioningException if object is a non-current document version
    * @throws StorageException if PWC can't be removed from storage cause to
    *         storage internal problem
    * @throws InvalidArgumentException if object with <code>documentId</code> is
    *         not Document or if PWC in version series does not exist
    */
   public void cancelCheckout(String documentId) throws ObjectNotFoundException, ConstraintException,
      UpdateConflictException, VersioningException, StorageException
   {
      checkConnection();

      ObjectData document = storage.getObjectById(documentId);
      if (!document.getTypeDefinition().isVersionable())
      {
         throw new ConstraintException("Type " + document.getTypeId() + " is not versionable.");
      }
      if (document.getBaseType() != BaseType.DOCUMENT)
      {
         // be sure it is realy document type
         throw new InvalidArgumentException("Object " + documentId + " is not a Document object.");
      }
      if (!((DocumentData)document).isVersionSeriesCheckedOut())
      {
         throw new InvalidArgumentException("There is no Private Working Copy in version series.");
      }

      // cancelCheckedOut may be invoked on any object in version series.
      // In other way 'cmis:versionSeriesCheckedOutId' may not reflect
      // current PWC id.
      ((DocumentData)document).cancelCheckout();
   }

   /**
    * Check-in Private Working Copy.
    *
    * 2.2.7.3 checkIn
    *
    *
    * @param documentId document id
    * @param major <code>true</code> is new version should be marked as major
    *        <code>false</code> otherwise
    * @param properties properties to be applied to new version
    * @param content content of document
    * @param checkinComment check-in comment
    * @param addACL set Access Control Entry to be applied for newly created
    *        version of document. May be <code>null</code> or empty list
    * @param removeACL set Access Control Entry that MUST be removed from the
    *        newly created version of document. May be <code>null</code> or
    *        empty list
    * @param policies list of policy id that MUST be applied to the newly
    *        created document. May be <code>null</code> or empty collection
    * @return ID of checked-in document
    * @throws ObjectNotFoundException if object with <code>documentId</code>
    *         does not exist
    * @throws ConstraintException if the object is not versionable
    * @throws VersioningException if object is a not PWC
    * @throws NameConstraintViolationException if <i>cmis:name</i> specified in
    *         properties throws conflict
    * @throws UpdateConflictException if update an object that is no longer
    *         current
    * @throws StreamNotSupportedException if document does not supports content
    *         stream
    * @throws StorageException if changes can't be saved in storage cause to
    *         storage internal problem
    * @throws InvalidArgumentException if object with <code>documentId</code> is
    *         not Document
    */
   public String checkin(String documentId, boolean major, Map<String, Property<?>> properties, ContentStream content,
      String checkinComment, List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL,
      Collection<String> policies) throws ObjectNotFoundException, ConstraintException, VersioningException,
      NameConstraintViolationException, UpdateConflictException, StreamNotSupportedException, StorageException
   {
      checkConnection();

      ObjectData pwc = storage.getObjectById(documentId);

      if (!pwc.getTypeDefinition().isVersionable())
      {
         throw new ConstraintException("Type " + pwc.getTypeId() + " is not versionable.");
      }
      if (pwc.getBaseType() != BaseType.DOCUMENT)
      {
         // be sure it is realy document type
         throw new InvalidArgumentException("Object " + documentId + " is not a Document object.");
      }
      if (!((DocumentData)pwc).isPWC())
      {
         throw new VersioningException("Object " + documentId + " is not Private Working Copy.");
      }

      TypeDefinition typeDefinition = pwc.getTypeDefinition();

      checkProperties(typeDefinition, properties, VERSION);
      // Do not use method 'checkContent' because stream may be null if content does not changed.
      if (typeDefinition.getContentStreamAllowed() == ContentStreamAllowed.NOT_ALLOWED && content != null)
      {
         throw new StreamNotSupportedException("Content stream not allowed for object of type "
            + typeDefinition.getId());
      }
      checkACL(typeDefinition, addACL, removeACL);
      checkPolicies(typeDefinition, policies);

      DocumentData version =
         ((DocumentData)pwc).checkin(major, checkinComment, properties, content, CmisUtils.mergeACLs(pwc.getACL(false),
            addACL, removeACL), createPolicyList(policies));

      return version.getObjectId();
   }

   /**
    * Check-out document.
    *
    * 2.2.7.1 checkOut
    *
    * @param documentId document id. Storage MAY allow checked-out ONLY latest
    *        version of Document
    * @return ID of checked-out document (PWC)
    * @throws ObjectNotFoundException if object with <code>documentId</code>
    *         does not exist
    * @throws ConstraintException if the object is not versionable
    * @throws UpdateConflictException if update an object that is no longer
    *         current
    * @throws VersioningException if one of the following conditions are met:
    *         <ul>
    *         <li>object is not latest version of document version and it is not
    *         supported to checked-out other then latest version</li>
    *         <li>version series already have one checked-out document. It is
    *         not possible to have more then one PWC at time</li>
    *         </ul>
    * @throws StorageException if newly created PWC can't be saved in storage
    *         cause to storage internal problem
    * @throws InvalidArgumentException if object with <code>documentId</code> is
    *         not Document
    */
   public String checkout(String documentId) throws ObjectNotFoundException, ConstraintException,
      UpdateConflictException, VersioningException, StorageException
   {
      checkConnection();

      ObjectData document = storage.getObjectById(documentId);

      if (!document.getTypeDefinition().isVersionable())
      {
         throw new ConstraintException("Type " + document.getTypeId() + " is not versionable.");
      }
      if (document.getBaseType() != BaseType.DOCUMENT)
      {
         // be sure it is realy document type
         throw new InvalidArgumentException("Object " + documentId + " is not a Document object.");
      }

      DocumentData pwc = ((DocumentData)document).checkout();

      return pwc.getObjectId();
   }

   /**
    * Close the connection and release underlying resources. Not able to use
    * this connection any more.
    */
   public abstract void close();

   /**
    * Create a document object.
    *
    * @param parentId parent folder id for object. May be null if storage
    *        supports unfiling
    * @param properties properties that will be applied to newly created
    *        document. If <code>properties</code> contains some property which
    *        updatability is other then {@link Updatability#ONCREATE} or
    *        {@link Updatability#READWRITE} this properties will be ignored
    * @param content the document content. May be <code>null</code>. MUST be
    *        required if the type requires it.
    * @param addACL Access Control Entries that MUST added for newly created
    *        document, either using the ACL from <code>parentId</code> if
    *        specified, or being applied if no <code>parentId</code> is
    *        specified. May be <code>null</code> or empty list
    * @param removeACL set Access Control Entries that MUST be removed from the
    *        newly created document, either using the ACL from
    *        <code>parentId</code> if specified, or being ignored if no
    *        <code>parentId</code> is specified. May be <code>null</code> or
    *        empty list
    * @param policies list of policy id that MUST be applied to the newly
    *        created document. May be <code>null</code> or empty collection
    * @param versioningState enumeration specifying what the versioning state of
    *        the newly created object shall be
    * @return ID of newly created document
    * @throws ObjectNotFoundException if target folder with specified id
    *         <code>parentId</code> does not exist
    * @throws TypeNotFoundException if type specified by property
    *         <code>cmis:objectTypeId</code> does not exist
    * @throws ConstraintException if any of following condition are met:
    *         <ul>
    *         <li><code>cmis:objectTypeId</code> property value is not an object
    *         type whose baseType is Document</li>
    *         <li><code>cmis:objectTypeId</code> property value is not in the
    *         list of AllowedChildObjectTypeIds of the parent-folder specified
    *         by <code>parentId</code></li>
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
    * @throws StreamNotSupportedException if the contentStreamAllowed attribute
    *         of the object type definition specified by the
    *         <code>cmis:objectTypeId</code> property value is set to 'not
    *         allowed' and a contentStream input parameter is provided
    * @throws NameConstraintViolationException violation is detected with the
    *         given <code>cmis:name</code> property value. Storage MAY chose
    *         other name which does not conflict
    * @throws StorageException if new Document can't be saved in storage cause
    *         to storage internal problem
    */
   public String createDocument(String parentId, Map<String, Property<?>> properties, ContentStream content,
      List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL, Collection<String> policies,
      VersioningState versioningState) throws ObjectNotFoundException, TypeNotFoundException, ConstraintException,
      StreamNotSupportedException, NameConstraintViolationException, StorageException
   {
      checkConnection();

      if (properties == null)
      {
         throw new InvalidArgumentException("Properties may not by null.");
      }

      String typeId = getTypeId(properties);
      TypeDefinition typeDefinition = getTypeDefinition(typeId, true);
      if (typeDefinition.getBaseId() != BaseType.DOCUMENT)
      {
         throw new ConstraintException("Type " + typeId + " is not type whose base type is cmis:document.");
      }

      ObjectData parent = null;
      if (parentId != null)
      {
         parent = storage.getObjectById(parentId);
         if (parent.getBaseType() != BaseType.FOLDER)
         {
            throw new InvalidArgumentException("Object " + parentId + " is not a Folder object.");
         }
         if (!((FolderData)parent).isAllowedChildType(typeId))
         {
            throw new ConstraintException("Object type " + typeId + " is not allowed as child for destination folder");
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

      if (versioningState == VersioningState.NONE && typeDefinition.isVersionable())
      {
         throw new ConstraintException("Type " + typeDefinition.getId()
            + " is versionable, versioning state 'none' not allowed.");
      }

      // XXX : Do not throw ConstraintException if versioning is not supported
      // and versioning state is other than NONE. Some client may not specify
      // this attribute and may not be able create documents. Lets backend storage
      // to resolve this issue.

      // check inputs
      checkProperties(typeDefinition, properties, CREATE);
      checkContent(typeDefinition, content);
      checkACL(typeDefinition, addACL, removeACL);
      checkPolicies(typeDefinition, policies);

      try
      {
         DocumentData newDocument =
            storage.createDocument((FolderData)parent, typeDefinition, properties, content, CmisUtils.mergeACLs(
               parent != null ? parent.getACL(false) : null, addACL, removeACL), createPolicyList(policies),
               versioningState);

         return newDocument.getObjectId();
      }
      catch (IOException ioe)
      {
         throw new CmisRuntimeException(ioe.getMessage(), ioe);
      }
   }

   /**
    * Create a document object as a copy of the given source document in the
    * specified parent folder <code>parentId</code>.
    *
    *
    * @param sourceId id for the source document
    * @param parentId parent folder id for object. May be null if storage
    *        supports unfiling
    * @param properties properties that will be applied to newly created
    *        document
    * @param addACL Access Control Entries that MUST added for newly created
    *        document, either using the ACL from <code>parentId</code> if
    *        specified, or being applied if no <code>parentId</code> is
    *        specified. May be <code>null</code> or empty list
    * @param removeACL set Access Control Entries that MUST be removed from the
    *        newly created document, either using the ACL from
    *        <code>parentId</code> if specified, or being ignored if no
    *        <code>parentId</code> is specified. May be <code>null</code> or
    *        empty list
    * @param policies list of policy id that MUST be applied to the newly
    *        created document. May be <code>null</code> or empty collection
    * @param versioningState enumeration specifying what the versioning state of
    *        the newly created object shall be
    * @return ID of newly created document
    * @throws ObjectNotFoundException if target folder with specified id
    *         <code>parentId</code> or source document with id
    *         <code>sourceId</code> does not exist
    * @throws ConstraintException if any of following condition are met:
    *         <ul>
    *         <li>sourceId is not an Object whose baseType is Document</li>
    *         <li>source document's <code>cmis:objectTypeId</code> property
    *         value is NOT in the list of AllowedChildObjectTypeIds of the
    *         parent-folder specified by <code>parentId</code></li>
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
    * @throws NameConstraintViolationException violation is detected with the
    *         given <code>cmis:name</code> property value. Storage MAY chose
    *         other name which does not conflict
    * @throws StorageException if new Document can't be saved in storage cause
    *         to storage internal problem
    */
   public String createDocumentFromSource(String sourceId, String parentId, Map<String, Property<?>> properties,
      List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL, Collection<String> policies,
      VersioningState versioningState) throws ObjectNotFoundException, ConstraintException,
      NameConstraintViolationException, StorageException
   {
      checkConnection();

      ObjectData source = storage.getObjectById(sourceId);
      TypeDefinition typeDefinition = source.getTypeDefinition();
      if (typeDefinition.getBaseId() != BaseType.DOCUMENT)
      {
         throw new ConstraintException("Source object is not Document.");
      }

      ObjectData parent = null;
      if (parentId != null)
      {
         parent = storage.getObjectById(parentId);
         if (parent.getBaseType() != BaseType.FOLDER)
         {
            throw new InvalidArgumentException("Object " + parentId + " is not a Folder object.");
         }
         if (!((FolderData)parent).isAllowedChildType(typeDefinition.getId()))
         {
            throw new ConstraintException("Object type " + typeDefinition.getId()
               + " is not allowed as child for destination folder");
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

      // XXX : Do not throw ConstraintException if versioning is not supported
      // and versioning state is othe than NONE. Some client may not specify
      // this attribute and may not be able create documents. Lets backend storage
      // to resolve this issue.

      // check inputs
      checkProperties(typeDefinition, properties, CREATE);
      checkACL(typeDefinition, addACL, removeACL);
      checkPolicies(typeDefinition, policies);

      DocumentData newDocument =
         storage.copyDocument((DocumentData)source, (FolderData)parent, properties, CmisUtils.mergeACLs(parent != null
            ? parent.getACL(false) : null, addACL, removeACL), createPolicyList(policies), versioningState);

      return newDocument.getObjectId();
   }

   /**
    * Create a folder object.
    *
    *
    * @param parentId parent folder id for new folder
    * @param properties properties that will be applied to newly created folder
    * @param addACL Access Control Entries that MUST added for newly created
    *        Folder, either using the ACL from <code>parentId</code> if
    *        specified, or being applied if no <code>parentId</code> is
    *        specified. May be <code>null</code> or empty list
    * @param removeACL set Access Control Entry that MUST be removed from the
    *        newly created folder, either using the ACL from
    *        <code>parentId</code> if specified, or being ignored if no
    *        <code>parentId</code> is specified. May be <code>null</code> or
    *        empty list
    * @param policies list of policy id that MUST be applied to the newly
    *        created folder. May be <code>null</code> or empty collection
    * @return ID of newly created folder
    * @throws ObjectNotFoundException if target folder with specified id
    *         <code>parentId</code> does not exist
    * @throws TypeNotFoundException if type specified by property
    *         <code>cmis:objectTypeId</code> does not exist
    * @throws ConstraintException if any of following condition are met:
    *         <ul>
    *         <li><code>cmis:objectTypeId</code> property value is not an object
    *         type whose baseType is Folder</li>
    *         <li>value of any of the properties violates the
    *         min/max/required/length constraints specified in the property
    *         definition in the object type</li>
    *         <li><code>cmis:objectTypeId</code> property value is not in the
    *         list of AllowedChildObjectTypeIds of the parent-folder specified
    *         by <code>parentId</code></li>
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
    *         other name which does not conflict.
    * @throws StorageException if new Folder can't be saved in storage cause to
    *         storage internal problem
    */
   public String createFolder(String parentId, Map<String, Property<?>> properties, List<AccessControlEntry> addACL,
      List<AccessControlEntry> removeACL, Collection<String> policies) throws ObjectNotFoundException,
      TypeNotFoundException, ConstraintException, NameConstraintViolationException, StorageException
   {
      checkConnection();

      if (properties == null)
      {
         throw new InvalidArgumentException("Properties may not by null.");
      }
      if (parentId == null)
      {
         throw new ConstraintException("Parent folder id is not specified.");
      }

      String typeId = getTypeId(properties);
      TypeDefinition typeDefinition = getTypeDefinition(typeId, true);
      if (typeDefinition.getBaseId() != BaseType.FOLDER)
      {
         throw new ConstraintException("Type " + typeId + " is not type whose base type is cmis:folder.");
      }

      ObjectData parent = storage.getObjectById(parentId);
      if (parent.getBaseType() != BaseType.FOLDER)
      {
         throw new InvalidArgumentException("Object " + parentId + " is not a Folder object.");
      }
      if (!((FolderData)parent).isAllowedChildType(typeId))
      {
         throw new ConstraintException("Object type " + typeId + " is not allowed as child for destination folder");
      }

      checkProperties(typeDefinition, properties, CREATE);
      checkACL(typeDefinition, addACL, removeACL);
      checkPolicies(typeDefinition, policies);

      ObjectData newFolder =
         storage.createFolder((FolderData)parent, typeDefinition, properties, CmisUtils.mergeACLs(parent.getACL(false),
            addACL, removeACL), createPolicyList(policies));

      return newFolder.getObjectId();
   }

   /**
    * Create a policy object.
    *
    * 2.2.4.5 createPolicy
    *
    *
    * @param parentId parent folder id should be <code>null</code> if policy
    *        object type is not fileable
    * @param properties properties to be applied to newly created Policy
    * @param addACL Access Control Entries that MUST added for newly created
    *        Policy, either using the ACL from <code>parentId</code> if
    *        specified, or being applied if no <code>parentId</code> is
    *        specified. May be <code>null</code> or empty list
    * @param removeACL set Access Control Entry that MUST be removed from the
    *        newly created Policy, either using the ACL from
    *        <code>parentId</code> if specified, or being ignored if no
    *        <code>parentId</code> is specified. May be <code>null</code> or
    *        empty list
    * @param policies list of policy id that MUST be applied to the newly
    *        created policy. May be <code>null</code> or empty collection
    * @return ID of newly created policy
    * @throws ObjectNotFoundException if target folder with specified id
    *         <code>parentId</code> does not exist
    * @throws TypeNotFoundException if type specified by property
    *         <code>cmis:objectTypeId</code> does not exist
    * @throws ConstraintException if any of following condition are met:
    *         <ul>
    *         <li><code>cmis:objectTypeId</code> property value is not an object
    *         type whose baseType is Policy</li>
    *         <li>value of any of the properties violates the
    *         min/max/required/length constraints specified in the property
    *         definition in the object type</li>
    *         <li><code>cmis:objectTypeId</code> property value is NOT in the
    *         list of AllowedChildObjectTypeIds of the parent-folder specified
    *         by parentId</li>
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
    * @throws StorageException if new Policy can't be saved in storage cause to
    *         storage internal problem
    */
   public String createPolicy(String parentId, Map<String, Property<?>> properties, List<AccessControlEntry> addACL,
      List<AccessControlEntry> removeACL, Collection<String> policies) throws ObjectNotFoundException,
      TypeNotFoundException, ConstraintException, NameConstraintViolationException, StorageException
   {
      checkConnection();

      if (properties == null)
      {
         throw new InvalidArgumentException("Properties may not by null.");
      }

      String typeId = getTypeId(properties);
      TypeDefinition typeDefinition = getTypeDefinition(typeId, true);
      if (typeDefinition.getBaseId() != BaseType.POLICY)
      {
         throw new ConstraintException("Type " + typeId + " is not type whose base type is cmis:policy.");
      }

      ObjectData parent = null;
      if (parentId != null)
      {
         parent = storage.getObjectById(parentId);
         if (parent.getBaseType() != BaseType.FOLDER)
         {
            throw new InvalidArgumentException("Object " + parentId + " is not a Folder object.");
         }
         if (!((FolderData)parent).isAllowedChildType(typeId))
         {
            throw new ConstraintException("Object type " + typeId + " is not allowed as child for destination folder");
         }
      }
      else if (typeDefinition.isFileable())
      {
         throw new ConstraintException("Policy type is fileable. Parent folder must be provided.");
      }

      checkProperties(typeDefinition, properties, CREATE);
      checkACL(typeDefinition, addACL, removeACL);
      checkPolicies(typeDefinition, policies);

      ObjectData newPolicy =
         storage.createPolicy((FolderData)parent, typeDefinition, properties, CmisUtils.mergeACLs(parent != null
            ? parent.getACL(false) : null, addACL, removeACL), createPolicyList(policies));

      return newPolicy.getObjectId();
   }

   /**
    * Create a relationship object.
    *
    * @param properties properties to be applied to newly created relationship
    * @param addACL set Access Control Entry to be applied for newly created
    *        relationship. May be <code>null</code> or empty list
    * @param removeACL set Access Control Entry that MUST be removed from the
    *        newly created relationship. May be <code>null</code> or empty list
    * @param policies list of policy id that MUST be applied to the newly
    *        created relationship. May be <code>null</code> or empty collection
    * @return ID of newly created relationship
    * @throws ObjectNotFoundException if <code>cmis:sourceId</code> or
    *         <code>cmis:targetId</code> property value is id of object that
    *         can't be found in storage
    * @throws TypeNotFoundException if type specified by property
    *         <code>cmis:objectTypeId</code> does not exist
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
      TypeNotFoundException, ConstraintException, NameConstraintViolationException, StorageException
   {
      checkConnection();

      if (properties == null)
      {
         throw new InvalidArgumentException("Properties may not by null.");
      }

      String typeId = getTypeId(properties);
      TypeDefinition typeDefinition = getTypeDefinition(typeId, true);

      if (typeDefinition.getBaseId() != BaseType.RELATIONSHIP)
      {
         throw new ConstraintException("Type " + typeId + " is not type whose base type is cmis:relationship.");
      }

      String sourceId = getSourceId(properties);
      String targetId = getTargetId(properties);

      // check is source and target object types are supported
      ObjectData source = storage.getObjectById(sourceId);
      if (typeDefinition.getAllowedSourceTypes() != null
         && !(Arrays.asList(typeDefinition.getAllowedSourceTypes()).contains(source.getTypeId())))
      {
         throw new ConstraintException("Source object type " + source.getTypeId() + " is not supported.");
      }
      ObjectData target = storage.getObjectById(targetId);
      if (typeDefinition.getAllowedTargetTypes() != null
         && !(Arrays.asList(typeDefinition.getAllowedTargetTypes()).contains(target.getTypeId())))
      {
         throw new ConstraintException("Target object type " + target.getTypeId() + " is not supported.");
      }

      checkProperties(typeDefinition, properties, CREATE);
      checkACL(typeDefinition, addACL, removeACL);
      checkPolicies(typeDefinition, policies);

      ObjectData newRelationship =
         storage.createRelationship(source, target, typeDefinition, properties, CmisUtils.mergeACLs(null, addACL,
            removeACL), createPolicyList(policies));

      return newRelationship.getObjectId();
   }

   private String getTypeId(Map<String, Property<?>> properties)
   {
      String typeId = getSingleValue(properties, CmisConstants.OBJECT_TYPE_ID);
      if (typeId == null)
      {
         throw new InvalidArgumentException("Type Id ('cmis:objectTypeId') is not specified.");
      }
      return typeId;
   }

   private String getSourceId(Map<String, Property<?>> properties)
   {
      String typeId = getSingleValue(properties, CmisConstants.SOURCE_ID);
      if (typeId == null)
      {
         throw new InvalidArgumentException("Source Id ('cmis:sourceId')  is not specified.");
      }
      return typeId;
   }

   private String getTargetId(Map<String, Property<?>> properties)
   {
      String typeId = getSingleValue(properties, CmisConstants.TARGET_ID);
      if (typeId == null)
      {
         throw new InvalidArgumentException("Target Id ('cmis:targetId') is not specified.");
      }
      return typeId;
   }

   private String getSingleValue(Map<String, Property<?>> properties, String name)
   {
      String value = null;
      Property<?> typeProperty = properties.get(name);
      if (typeProperty != null && typeProperty.getValues().size() > 0)
      {
         value = (String)typeProperty.getValues().get(0);
      }
      return value;
   }

   /**
    * Delete the content stream for the specified Document object.
    *
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
    * @throws NullPointerException if <code>changeTokenHolder</code> is
    *         <code>null</code>
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
      checkConnection();

      if (changeTokenHolder == null)
      {
         throw new NullPointerException("changeTokenHolder may not by null.");
      }

      ObjectData document = storage.getObjectById(documentId);

      if (document.getBaseType() != BaseType.DOCUMENT)
      {
         // be sure object is document
         throw new InvalidArgumentException("Object " + documentId + " is not Document.");
      }
      if (document.getTypeDefinition().getContentStreamAllowed() == ContentStreamAllowed.REQUIRED)
      {
         throw new ConstraintException("Content stream is required for object and may not be removed.");
      }

      // Validate change token, object may be already updated.
      validateChangeToken(document, changeTokenHolder.getValue());

      try
      {
         ((DocumentData)document).setContentStream(null);
      }
      catch (IOException never)
      {
         // should never happen because to null content stream
         throw new CmisRuntimeException("Unable delete document content stream. " + never.getMessage(), never);
      }

      // Update change token
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
    *         more children or is root folder
    * @throws UpdateConflictException if object that is no longer current (as
    *         determined by the storage)
    * @throws VersioningException if object can not be removed cause to
    *         versioning conflict
    * @throws StorageException if object can not be removed cause to storage
    *         internal problem
    */
   public void deleteObject(String objectId, Boolean deleteAllVersions) throws ObjectNotFoundException,
      ConstraintException, UpdateConflictException, VersioningException, StorageException
   {
      checkConnection();

      ObjectData object = storage.getObjectById(objectId);

      if (object.getBaseType() == BaseType.FOLDER)
      {
         if (((FolderData)object).hasChildren())
         {
            throw new ConstraintException("Failed delete object. Object " + objectId
               + " is Folder and contains one or more objects.");
         }
         if (((FolderData)object).isRoot())
         {
            throw new ConstraintException("Root folder can't be deleted.");
         }
      }

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
    * @throws ConstraintException if folder with specified id
    *         <code>folderId</code> is root folder
    * @throws UpdateConflictException if some object(s) that is no longer
    *         current (as determined by the storage)
    */
   public Collection<String> deleteTree(String folderId, Boolean deleteAllVersions, UnfileObject unfileObject,
      Boolean continueOnFailure) throws ObjectNotFoundException, ConstraintException, UpdateConflictException
   {
      checkConnection();

      ObjectData folder = storage.getObjectById(folderId);

      if (folder.getBaseType() != BaseType.FOLDER)
      {
         throw new ConstraintException("Failed delete tree. Object " + folderId + " is not a Folder.");
      }
      if (((FolderData)folder).isRoot())
      {
         throw new ConstraintException("Root folder can't be removed.");
      }

      // Default values.
      if (unfileObject == null)
      {
         unfileObject = UnfileObject.DELETE;
      }
      if (deleteAllVersions == null)
      {
         deleteAllVersions = true;
      }
      if (continueOnFailure == null)
      {
         continueOnFailure = false;
      }

      // Check unfiling capability if 'unfileObject' is other then 'DELETE'
      if (unfileObject != UnfileObject.DELETE && !storage.getRepositoryInfo().getCapabilities().isCapabilityUnfiling())
      {
         throw new InvalidArgumentException(
            "Unfiling capability is not supported. Parameter 'unfileObject' may not be other then 'DELETE'.");
      }

      Collection<String> failedDelete =
         storage.deleteTree((FolderData)folder, deleteAllVersions, unfileObject, continueOnFailure);

      return failedDelete;
   }

   /**
    * Get the ACL currently applied to the specified object.
    *
    * 2.2.10.1 getACL
    *
    *
    * @param objectId identifier of object
    * @param onlyBasicPermissions if <code>true</code> then return only the CMIS
    *        Basic permissions
    * @return actual ACL or empty list if no ACL applied to object
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

      ObjectData object = storage.getObjectById(objectId);
      List<AccessControlEntry> acl = object.getACL(onlyBasicPermissions);
      return acl;
   }

   /**
    * Get the list of allowable actions for an Object.
    *
    *
    * @param objectId object id
    * @return allowable actions for object
    * @throws ObjectNotFoundException if object with specified id
    *         <code>objectId</code> does not exist
    */
   public AllowableActions getAllowableActions(String objectId) throws ObjectNotFoundException
   {
      checkConnection();
      ObjectData object = storage.getObjectById(objectId);
      return storage.calculateAllowableActions(object);
   }

   /**
    * Get all documents in version series.
    *
    * 2.2.7.6 getAllVersions
    *
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
    *         'cmis:creationDate' descending. Even not versionable documents
    *         must have exactly one document in version series
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
      List<CmisObject> cmisVersions = new ArrayList<CmisObject>(versions.size());
      for (ObjectData objectData : versions)
      {
         cmisVersions.add(getCmisObject(objectData, includeAllowableActions, IncludeRelationships.NONE, false, false,
            includeObjectInfo, parsedPropertyFilter, RenditionFilter.NONE_FILTER));
      }
      return cmisVersions;
   }

   /**
    * Gets the list of policies currently applied to the specified object.
    *
    * 2.2.9.3 getAppliedPolicies
    *
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

      ObjectData object = storage.getObjectById(objectId);

      Collection<PolicyData> policies = object.getPolicies();
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      List<CmisObject> policyIDs = new ArrayList<CmisObject>(policies.size());
      for (ObjectData policy : policies)
      {
         CmisObject cmisPolicy =
            getCmisObject(policy, false, IncludeRelationships.NONE, false, false, includeObjectInfo,
               parsedPropertyFilter, RenditionFilter.NONE_FILTER);
         policyIDs.add(cmisPolicy);
      }
      return policyIDs;
   }

   /**
    * Documents that are checked out that the user has access to.
    *
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
    * @param maxItems max number of items in response. If -1 then no limit of
    *        max items in result set
    * @param skipCount the skip items. Must be equals or greater the 0
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
         folder = storage.getObjectById(folderId);

         if (folder.getBaseType() != BaseType.FOLDER)
         {
            throw new InvalidArgumentException("Can't get checkedout documents. Object " + folderId
               + " is not a Folder.");
         }
      }

      ItemsIterator<DocumentData> iterator = storage.getCheckedOutDocuments((FolderData)folder, orderBy);

      try
      {
         if (skipCount > 0)
         {
            iterator.skip(skipCount);
         }
      }
      catch (NoSuchElementException nse)
      {
         throw new InvalidArgumentException("skipCount parameter is greater then total number of items");
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
    *        for each child object should be included in response
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
    *        and storage may ignore this parameter if it not able sort items.
    *        May be <code>null</code> if sorting is not required
    * @param maxItems max number of items in response. If -1 then no limit of
    *        max items in result set
    * @param skipCount the skip items. Must be equals or greater the 0
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

      ObjectData folder = storage.getObjectById(folderId);
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
         throw new InvalidArgumentException("'skipCount' parameter is greater then total number of items");
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
         ObjectData childData = iterator.next();

         CmisObject child =
            getCmisObject(childData, includeAllowableActions, includeRelationships, false, false, includeObjectInfo,
               parsedPropertyFilter, parsedRenditionFilter);

         if (includePathSegments)
         {
            child.setPathSegment(childData.getName());
         }

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
    * @param changeLogTokenHolder if {@link ChangeLogTokenHolder#getToken()} return
    *        value other than <code>null</code>, then change event corresponded
    *        to the value of the specified change log token will be returned as
    *        the first result in the output. If not specified, then will be
    *        returned the first change event recorded in the change log. When
    *        set of changes is returned then <code>changeLogToken</code> must
    *        contains log token corresponded to the last change event. Then it
    *        may be used by client for getting next set on change events.
    * @param includeProperties if <code>true</code>, then the result includes
    *        the updated property values for 'updated' change events. If
    *        <code>false</code>, then the result will not include the updated
    *        property values for 'updated' change events. The single exception
    *        to this is that the objectId MUST always be included
    * @param propertyFilter comma-delimited list of property definition Query
    *        Names. A wildcard '*' is supported and minds return all properties.
    *        If empty string or <code>null</code> provided than storage MAY
    *        return storage specific set of properties. This parameter will be
    *        ignored <code>includeProperties</code> is <code>false</code>
    * @param includePolicyIDs if <code>true</code>, then the include the IDs of
    *        Policies applied to the object referenced in each change event, if
    *        the change event modified the set of policies applied to the object
    * @param includeAcl if <code>true</code>, then include ACL applied to the
    *        object referenced in each change event
    * @param includeObjectInfo if <code>true</code> then result must include
    *        external information about each object. See {@link ObjectInfo}.
    *        Particular this info may be used by REST Atom binding for building
    *        correct Atom document
    * @param maxItems max number of items in response. If -1 then no limit of
    *        max items in result set
    * @return content changes
    * @throws NullPointerException if <code>changeLogTokenHolder</code> is
    *         <code>null</code>
    * @throws ConstraintException if the event corresponding to the change log
    *         token provided as an input parameter is no longer available in the
    *         change log. (E.g. because the change log was truncated)
    * @throws FilterNotValidException if <code>propertyFilter</code> has invalid
    *         syntax or contains at least one property name that is not in
    *         object's property definition
    */
   public ItemsList<CmisObject> getContentChanges(ChangeLogTokenHolder changeLogTokenHolder, boolean includeProperties,
      String propertyFilter, boolean includePolicyIDs, boolean includeAcl, boolean includeObjectInfo, int maxItems)
      throws ConstraintException, FilterNotValidException
   {
      if (changeLogTokenHolder == null)
      {
         throw new NullPointerException("ChangeLogTokenHolder may not by null.");
      }
      String token = changeLogTokenHolder.getValue();

      ItemsIterator<ChangeEvent> iterator = storage.getChangeLog(token);

      // Not need filter if all properties rejected.
      PropertyFilter parsedPropertyFilter = includeProperties ? new PropertyFilter(propertyFilter) : null;
      ItemsList<CmisObject> cmisChanges = new ItemsList<CmisObject>();
      for (int count = 0; iterator.hasNext() && (maxItems < 0 || count < maxItems); count++)
      {
         ChangeEvent event = iterator.next();
         String objectId = event.getObjectId();
         CmisObject cmis = new CmisObject();
         // policies
         if (includePolicyIDs && event.getType() == ChangeType.SECURITY && event.getPolicyIds() != null
            && event.getPolicyIds().size() > 0)
         {
            cmis.getPolicyIds().addAll(event.getPolicyIds());
         }
         // ACL
         if (includeAcl && event.getType() == ChangeType.SECURITY && event.getAcl() != null
            && event.getAcl().size() > 0)
         {
            cmis.getACL().addAll(event.getAcl());
         }
         // properties
         if (includeProperties && event.getType() == ChangeType.UPDATED && event.getProperties() != null)
         {
            for (Property<?> property : event.getProperties())
            {
               if (parsedPropertyFilter.accept(property.getQueryName()))
               {
                  String id = property.getId();
                  cmis.getProperties().put(id, property);
               }
            }
         }
         // cmis:objectId must be always returned
         if (cmis.getProperties().get(CmisConstants.OBJECT_ID) == null)
         {
            // NOTE Do not provide query, local and display names for property
            // since we can be not able to determine it.
            cmis.getProperties().put(CmisConstants.OBJECT_ID,
               new IdProperty(CmisConstants.OBJECT_ID, null, null, null, objectId));
         }
         // able to provide object id only
         if (includeObjectInfo)
         {
            ObjectInfo objectInfo = new ObjectInfo();
            objectInfo.setId(objectId);
            cmis.setObjectInfo(objectInfo);
         }
         cmis.setChangeInfo(new ChangeInfo(event.getDate(), event.getType()));
         // update token, it will keep latestChangeLogToken when all events
         // will be retrieved all maxItems limit reached.
         token = event.getLogToken();
      }

      // Indicate that we have some more results.
      cmisChanges.setHasMoreItems(iterator.hasNext());
      cmisChanges.setNumItems(iterator.size()); // ItemsIterator gives -1 if total number is unknown.
      changeLogTokenHolder.setValue(token);
      return cmisChanges;
   }

   /**
    * Get document's content stream.
    *
    * @param objectId object id
    * @param streamId identifier for the rendition stream, when used to get a
    *        rendition stream. For Documents, if not provided then this method
    *        returns the content stream. For Folders (if Folders supports
    *        renditions) this parameter must be provided
    * @return object's content stream or throws {@link ConstraintException} if
    *         object has not content stream. Never return null
    * @throws ObjectNotFoundException if object with specified id
    *         <code>objectId</code> does not exist
    * @throws ConstraintException if the object specified by objectId does NOT
    *         have a content stream or rendition stream
    */
   public ContentStream getContentStream(String objectId, String streamId) throws ObjectNotFoundException,
      ConstraintException
   {
      checkConnection();

      ObjectData object = storage.getObjectById(objectId);
      ContentStream contentStream = null;
      try
      {
         if (streamId != null)
         {
            contentStream = object.getContentStream(streamId);
         }
         else if (object.getBaseType() == BaseType.DOCUMENT)
         {
            contentStream = ((DocumentData)object).getContentStream();
         }
      }
      catch (IOException ioe)
      {
         throw new CmisRuntimeException("Unable get content stream. " + ioe.getMessage(), ioe);
      }
      if (contentStream == null)
      {
         throw new ConstraintException("Object does not have content stream.");
      }
      return contentStream;
   }

   /**
    * Get the collection of descendant objects contained in the specified folder
    * and any (according to <code>depth</code>) of its child-folders.
    *
    * 2.2.3.2 getDescendants
    *
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
    *         is not a Folder or if <code>depth != -1 && !(depth >= 1)</code>
    * @throws FilterNotValidException if <code>propertyFilter</code> has invalid
    *         syntax or contains at least one property name that is not in
    *         object's property definition or <code>renditionFilter</code> has
    *         invalid syntax or contains at least one unknown rendition
    */
   public List<ItemsTree<CmisObject>> getDescendants(String folderId, int depth, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includePathSegments, boolean includeObjectInfo,
      String propertyFilter, String renditionFilter) throws ObjectNotFoundException, FilterNotValidException
   {
      checkConnection();
      if (depth != -1 && !(depth >= 1))
      {
         throw new InvalidArgumentException("Invalid depth parameter. Must be 1 or greater then 1 or -1 but " + depth
            + " specified.");
      }
      return getObjectTree(folderId, depth, null, includeAllowableActions, includeRelationships, includePathSegments,
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
      throws ObjectNotFoundException, FilterNotValidException
   {
      checkConnection();

      ObjectData folder = storage.getObjectById(folderId);
      if (folder.getBaseType() != BaseType.FOLDER)
      {
         throw new InvalidArgumentException("Object " + folderId + " is not a Folder.");
      }
      if (((FolderData)folder).isRoot())
      {
         throw new InvalidArgumentException("Can't get parent of root folder.");
      }

      FolderData parent = null;
      try
      {
         parent = folder.getParent();
      }
      catch (ConstraintException never)
      {
         // Should never happen because we already determined object is folder
         // and it is not root folder so MUST have exactly one parent
         throw new CmisRuntimeException(never.getMessage());
      }

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);

      CmisObject cmisParent =
         getCmisObject(parent, false, IncludeRelationships.NONE, false, false, includeObjectInfo, parsedPropertyFilter,
            RenditionFilter.NONE_FILTER);

      return cmisParent;
   }

   /**
    * Get the collection of descendant folder objects contained in the specified
    * folder and any (according to <code>depth</code>) of its child-folders.
    *
    * 2.2.3.3 getFolderTree
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
    *         is not a Folder or if <code>depth != -1 && !(depth >= 1)</code>
    * @throws FilterNotValidException if <code>propertyFilter</code> has invalid
    *         syntax or contains at least one property name that is not in
    *         object's property definition or <code>renditionFilter</code> has
    *         invalid syntax or contains at least one unknown rendition
    */
   public List<ItemsTree<CmisObject>> getFolderTree(String folderId, int depth, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includePathSegments, boolean includeObjectInfo,
      String propertyFilter, String renditionFilter) throws ObjectNotFoundException, FilterNotValidException
   {
      checkConnection();
      if (depth != -1 && !(depth >= 1))
      {
         throw new InvalidArgumentException("Invalid depth parameter. Must be 1 or greater then 1 or -1 but " + depth
            + " specified.");
      }
      return getObjectTree(folderId, depth, BaseType.FOLDER, includeAllowableActions, includeRelationships,
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

      ObjectData objectData = storage.getObjectById(objectId);
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
    *         does not exist
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
    * 2.2.7.4 getObjectOfLatestVersion
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
    *         <code>versionSeriesId</code> does not exist or the input parameter
    *         <code>major</code> is <code>true</code> and the Version Series
    *         contains no major versions.
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
         return getCmisObject(versions.iterator().next(), includeAllowableActions, includeRelationships,
            includePolicyIDs, includeAcl, includeObjectInfo, parsedPropertyFilter, parsedRenditionFilter);
      }

      // Storage#getAllVersions(versionSeriesId) return sorted by
      // 'cmis:creationDate' descending. Latest version is version with latest
      // 'cmis:lastModificationDate'.
      List<DocumentData> v = new ArrayList<DocumentData>(versions);
      Collections.sort(v, CmisUtils.versionComparator);

      if (!major)
      {
         return getCmisObject(v.get(0), includeAllowableActions, includeRelationships, includePolicyIDs, includeAcl,
            includeObjectInfo, parsedPropertyFilter, parsedRenditionFilter);
      }

      for (DocumentData document : v)
      {
         if (document.isMajorVersion())
         {
            return getCmisObject(document, includeAllowableActions, includeRelationships, includePolicyIDs, includeAcl,
               includeObjectInfo, parsedPropertyFilter, parsedRenditionFilter);
         }
      }

      // May happen only if major version requested but there is no any major version.
      throw new ObjectNotFoundException("Not found any major versions in version series.");
   }

   /**
    * Gets the parent folder(s) for the specified object.
    *
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

      ObjectData object = storage.getObjectById(objectId);

      TypeDefinition typeDefinition = object.getTypeDefinition();

      if (!typeDefinition.isFileable())
      {
         throw new ConstraintException("Can't get parents. Object " + objectId + " has type " + object.getTypeId()
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
         ObjectParent parentType = new ObjectParent(cmisParent, includeRelativePathSegment ? object.getName() : null);
         cmisParents.add(parentType);
      }
      return cmisParents;
   }

   /**
    * Get all or a subset of relationships associated with an independent
    * object.
    *
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
    * @param maxItems max number of items in response. If -1 then no limit of
    *        max items in result set
    * @param skipCount the skip items. Must be equals or greater the 0
    * @return object's relationships
    * @throws ObjectNotFoundException if object with <code>objectId</code> does
    *         not exist
    * @throws TypeNotFoundException if <code>typeId != null</code> and type
    *         <code>typeId</code> does not exist
    * @throws FilterNotValidException if <code>propertyFilter</code> has invalid
    *         syntax or contains at least one property name that is not in
    *         object's property definition
    */
   public ItemsList<CmisObject> getObjectRelationships(String objectId, RelationshipDirection direction, String typeId,
      boolean includeSubRelationshipTypes, boolean includeAllowableActions, boolean includeObjectInfo,
      String propertyFilter, int maxItems, int skipCount) throws FilterNotValidException, ObjectNotFoundException,
      TypeNotFoundException
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

      ObjectData object = storage.getObjectById(objectId);

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
         throw new InvalidArgumentException("skipCount parameter is greater then total number of items");
      }

      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      ItemsList<CmisObject> relationships = new ItemsList<CmisObject>();

      for (int count = 0; iterator.hasNext() && (maxItems < 0 || count < maxItems); count++)
      {
         ObjectData rel = iterator.next();
         CmisObject cmis =
            getCmisObject(rel, includeAllowableActions, null, false, false, includeObjectInfo, parsedPropertyFilter,
               RenditionFilter.NONE_FILTER);
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

      ObjectData object = storage.getObjectById(objectId);
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);

      CmisObject cmis =
         getCmisObject(object, false, IncludeRelationships.NONE, false, false, includeObjectInfo, parsedPropertyFilter,
            RenditionFilter.NONE_FILTER);

      return cmis;
   }

   /**
    * Get properties of latest version in version series.
    *
    * 2.2.7.5 getPropertiesOfLatestVersion
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
    *         <code>versionSeriesId</code> does not exist or the input parameter
    *         <code>major</code> is <code>true</code> and the Version Series
    *         contains no major versions.
    * @throws FilterNotValidException if <code>propertyFilter</code> has invalid
    *         syntax or contains at least one property name that is not in
    *         object's property definition
    */
   public CmisObject getPropertiesOfLatestVersion(String versionSeriesId, boolean major, boolean includeObjectInfo,
      String propertyFilter) throws FilterNotValidException, ObjectNotFoundException
   {
      return getObjectOfLatestVersion(versionSeriesId, major, false, null, false, false, includeObjectInfo,
         propertyFilter, RenditionFilter.NONE);
   }

   /**
    * Get the list of associated Renditions for the specified object. Only
    * rendition attributes are returned, not rendition stream.
    *
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
    * @param maxItems max number of items in response. If -1 then no limit of
    *        max items in result set
    * @param skipCount the skip items. Must be equals or greater then 0
    * @return object's renditions
    * @throws ObjectNotFoundException if object with specified
    *         <code>objectId</code> does not exist
    * @throws FilterNotValidException if <code>renditionFilter</code> has
    *         invalid syntax or contains at least one unknown rendition
    */
   public List<Rendition> getRenditions(String objectId, String renditionFilter, int maxItems, int skipCount)
      throws ObjectNotFoundException, FilterNotValidException
   {
      checkConnection();

      if (skipCount < 0)
      {
         throw new InvalidArgumentException("skipCount parameter is negative.");
      }

      if (storage.getRepositoryInfo().getCapabilities().getCapabilityRenditions() == CapabilityRendition.NONE)
      {
         throw new NotSupportedException("Renditions is not supported.");
      }

      ObjectData objectData = storage.getObjectById(objectId);
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
         throw new InvalidArgumentException("skipCount parameter is greater then total number of items");
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
    * @param maxItems max number of items in response. If -1 then no limit of
    *        max items in result set
    * @param skipCount the skip items. Must be equals or greater then 0
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
         throw new InvalidArgumentException("skipCount parameter is greater then total number of items");
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
    * definition, see {@link #getTypeDefinition(String, boolean)}.
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
    * @throws InvalidArgumentException if
    *         <code>depth != -1 && !(depth >= 1)</code>
    */
   public List<ItemsTree<TypeDefinition>> getTypeDescendants(String typeId, int depth, boolean includePropertyDefinition)
      throws TypeNotFoundException
   {
      checkConnection();
      if (depth != -1 && !(depth >= 1))
      {
         throw new InvalidArgumentException("Invalid depth parameter. Must be 1 or greater then 1 or -1 but " + depth
            + " specified.");
      }
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
    * @throws NameConstraintViolationException violation is detected with the
    *         given <code>cmis:name</code> property value in destination folder
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
      throws ObjectNotFoundException, NameConstraintViolationException, ConstraintException, UpdateConflictException,
      VersioningException, StorageException
   {
      checkConnection();

      ObjectData object = storage.getObjectById(objectId);

      ObjectData target = storage.getObjectById(targetFolderId);
      if (target.getBaseType() != BaseType.FOLDER)
      {
         throw new InvalidArgumentException("Object " + targetFolderId + " is not a Folder object.");
      }
      if (!((FolderData)target).isAllowedChildType(object.getTypeId()))
      {
         throw new ConstraintException("Object with type " + object.getTypeId()
            + " is not allowed as child object fro target folder.");
      }

      if (sourceFolderId == null)
      {
         throw new InvalidArgumentException("sourceFolderId parameter may not be null");
      }

      boolean found = false;
      for (ObjectData one : object.getParents())
      {
         if (one.getObjectId().equals(sourceFolderId))
         {
            found = true;
            break;
         }
      }
      if (!found)
      {
         throw new InvalidArgumentException("Specified source folder " + sourceFolderId + " is not a parent of "
            + objectId);
      }

      ObjectData source = storage.getObjectById(sourceFolderId);
      if (source.getBaseType() != BaseType.FOLDER)
      {
         throw new InvalidArgumentException("Object " + sourceFolderId + " is not a Folder object.");
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
    * @param maxItems max number of items in response. If -1 then no limit of
    *        max items in result set
    * @param skipCount the skip items. Must be equals or greater then 0
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
         throw new InvalidArgumentException("skipCount parameter is greater then total number of items");
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
            data = storage.getObjectById(result.getObjectId());
         }
         catch (ObjectNotFoundException e)
         {
            // If object was removed but found in index
            LOG.warn("Object " + result.getObjectId() + " was removed.");
            continue;
         }

         CmisObject object =
            getCmisObject(data, includeAllowableActions, includeRelationships, false, false, includeObjectInfo,
               new PropertyFilter(propertyFilter.toString()), parsedRenditionFilter);

         Score score = result.getScore();
         if (score != null)
         {
            String scoreColumnName = score.getScoreColumnName();
            DecimalProperty scoreProperty =
               new DecimalProperty(scoreColumnName, scoreColumnName, scoreColumnName, scoreColumnName, score
                  .getScoreValue());
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
    * 2.2.5.2 removeObjectFromFolder
    *
    *
    * @param objectId the id of object to be removed
    * @param folderId the folder from which the object is to be removed. If
    *        null, then remove the object from all folders in which it is
    *        currently filed. In this case unfiling capability must be supported
    *        otherwise {@link NotSupportedException} will be thrown
    * @throws ObjectNotFoundException if <code>objectId</code> or
    *         <code>folderId</code> were not found
    * @throws InvalidArgumentException if object <code>objectId</code> is not
    *         fileable or is folder or if object <code>folderId</code> is not a
    *         folder
    * @throws NotSupportedException if unfiling capability is not supported by
    *         backend storage
    */
   public void removeObjectFromFolder(String objectId, String folderId) throws ObjectNotFoundException
   {
      checkConnection();

      ObjectData object = storage.getObjectById(objectId);
      Collection<FolderData> parents = object.getParents();
      if ((folderId == null || parents.size() == 1)
         && !storage.getRepositoryInfo().getCapabilities().isCapabilityUnfiling())
      {
         // May not remove object from all folders.
         throw new NotSupportedException("Unfiling is not supported.");
      }

      if (!object.getTypeDefinition().isFileable() || object.getBaseType() == BaseType.FOLDER)
      {
         throw new InvalidArgumentException("Object " + objectId + " is not fileable or folder. Can't be unfiled.");
      }

      if (folderId != null)
      {
         ObjectData folder = storage.getObjectById(folderId);

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
    *
    * @param policyId id of policy to be removed from object
    * @param objectId id of object
    * @throws ObjectNotFoundException if object with <code>objectId</code> does
    *         not exist
    * @throws ConstraintException if object with id <code>objectId</code> is not
    *         controllable by policy
    * @throws InvalidArgumentException if object with <code>policyId</code> is
    *         not object whose base type is Policy
    */
   public void removePolicy(String policyId, String objectId) throws ConstraintException, ObjectNotFoundException
   {
      checkConnection();

      ObjectData object = storage.getObjectById(objectId);
      if (!object.getTypeDefinition().isControllablePolicy())
      {
         throw new ConstraintException("Object is not controllable by policy.");
      }

      ObjectData policyData = storage.getObjectById(policyId);
      if (policyData.getBaseType() != BaseType.POLICY)
      {
         throw new InvalidArgumentException("Object " + policyId + " is not a Policy object.");
      }

      object.removePolicy((PolicyData)policyData);
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
    * @throws NullPointerException if <code>changeTokenHolder</code> is
    *         <code>null</code>
    * @throws ContentAlreadyExistsException if the input parameter
    *         <code>overwriteFlag</code> is <code>false</code> and the Object
    *         already has a content-stream
    * @throws ConstraintException if document type definition attribute
    *         {@link TypeDefinition#getContentStreamAllowed()} is 'notallowed'
    *         and specified <code>contentStream</code> is other then
    *         <code>null</code> or if
    *         {@link TypeDefinition#getContentStreamAllowed()} attribute is
    *         'required' and <code>contentStream</code> is <code>null</code>
    * @throws StreamNotSupportedException will be thrown if the
    *         contentStreamAllowed attribute of the object type definition
    *         specified by the <code>cmis:objectTypeId</code> property value of
    *         the given document is set to not allowed
    * @throws UpdateConflictException if update an object that is no longer
    *         current. Storage determine this by using change token
    * @throws VersioningException if object is a non-current (latest) document
    *         version and updatiing other then latest version is not supported
    * @throws StorageException if object's content stream can not be updated
    *         (save changes) cause to storage internal problem
    */
   public String setContentStream(String documentId, ContentStream content, ChangeTokenHolder changeTokenHolder,
      Boolean overwriteFlag) throws ObjectNotFoundException, ContentAlreadyExistsException, ConstraintException,
      StreamNotSupportedException, UpdateConflictException, VersioningException, StorageException
   {
      checkConnection();

      if (changeTokenHolder == null)
      {
         throw new NullPointerException("changeTokenHolder may not by null.");
      }

      ObjectData document = storage.getObjectById(documentId);

      if (document.getBaseType() != BaseType.DOCUMENT)
      {
         throw new InvalidArgumentException("Object " + documentId + " is not Document.");
      }

      checkContent(document.getTypeDefinition(), content);

      if (overwriteFlag == null)
      {
         overwriteFlag = true; // Default
      }
      if (!overwriteFlag && ((DocumentData)document).hasContent())
      {
         throw new ContentAlreadyExistsException("Document already has content stream and 'overwriteFlag' is false.");
      }

      // Validate change token, object may be already updated.
      validateChangeToken(document, changeTokenHolder.getValue());

      try
      {
         ((DocumentData)document).setContentStream(content);
      }
      catch (IOException ioe)
      {
         throw new CmisRuntimeException("Unable set document content stream. " + ioe.getMessage(), ioe);
      }

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
    * @param properties the properties to be applied for object
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
      checkConnection();

      if (properties == null)
      {
         throw new InvalidArgumentException("Properties may not by null.");
      }
      if (changeTokenHolder == null)
      {
         throw new NullPointerException("changeTokenHolder may not by null.");
      }

      ObjectData object = storage.getObjectById(objectId);

      // Validate change token, object may be already updated.
      validateChangeToken(object, changeTokenHolder.getValue());
      checkProperties(object.getTypeDefinition(), properties, UPDATE);

      object.setProperties(properties);

      String changeToken = object.getChangeToken();
      changeTokenHolder.setValue(changeToken);

      return object.getObjectId();
   }

   private List<ItemsTree<CmisObject>> getObjectTree(String folderId, int depth, BaseType typeFilter,
      boolean includeAllowableActions, IncludeRelationships includeRelationships, boolean includePathSegments,
      boolean includeObjectInfo, String propertyFilter, String renditionFilter) throws ObjectNotFoundException,
      InvalidArgumentException, FilterNotValidException
   {
      ObjectData folder = storage.getObjectById(folderId);

      if (folder.getBaseType() != BaseType.FOLDER)
      {
         throw new InvalidArgumentException("Can't get children. Object " + folderId + " is not a Folder.");
      }

      List<ItemsTree<CmisObject>> tree = new ArrayList<ItemsTree<CmisObject>>();
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      RenditionFilter parsedRenditionFilter = new RenditionFilter(renditionFilter);

      for (ItemsIterator<ObjectData> children = ((FolderData)folder).getChildren(null); children.hasNext();)
      {
         ObjectData childData = children.next();

         if (typeFilter != null && childData.getBaseType() != typeFilter)
         {
            continue;
         }

         CmisObject container =
            getCmisObject(childData, includeAllowableActions, includeRelationships, false, false, includeObjectInfo,
               parsedPropertyFilter, parsedRenditionFilter);

         if (includePathSegments)
         {
            container.setPathSegment(childData.getName());
         }

         List<ItemsTree<CmisObject>> subTree =
            (childData.getBaseType() == BaseType.FOLDER && (depth > 1 || depth == -1)) //
               ? getObjectTree(childData.getObjectId(), depth != -1 ? depth - 1 : depth, typeFilter,
                  includeAllowableActions, includeRelationships, includePathSegments, includeObjectInfo,
                  propertyFilter, renditionFilter) //
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
            TypeDefinition relBaseType = null;
            try
            {
               relBaseType = getTypeDefinition(BaseType.RELATIONSHIP.value());
            }
            catch (TypeNotFoundException e)
            {
               // If relationship is not supported
            }

            if (relBaseType != null)
            {
               for (ItemsIterator<RelationshipData> iter = object.getRelationships(direction, relBaseType, true); iter
                  .hasNext();)
               {
                  RelationshipData next = iter.next();
                  cmis.getRelationship().add(
                     getCmisObject(next, false, includeRelationships, false, false, includeObjectInfo,
                        PropertyFilter.ALL_FILTER, RenditionFilter.NONE_FILTER));
               }
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
            try
            {
               objectInfo.setParentId(((FolderData)object).isRoot() ? null : object.getParent().getObjectId());
            }
            catch (ConstraintException never)
            {
               // object.getParent() expression should never throw
               // ConstraintException becuase we already checked object is folder,
               // not root folder so it has exactly one parent
               throw new CmisRuntimeException(never.getMessage());
            }
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
            objectInfo.setContentStreamMimeType(doc.getContentStreamMimeType());
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

   //   private void checkInputs(TypeDefinition typeDefinition, Map<String, Property<?>> properties, ContentStream content,
   //      List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL, Collection<String> policies) throws
   //      ConstraintException, StreamNotSupportedException
   //   {
   //      checkContent(typeDefinition, content);
   //      checkProperties(typeDefinition, properties);
   //      checkACL(typeDefinition, addACL, removeACL);
   //      checkPolicies(typeDefinition, policies);
   //   }

   private void checkContent(TypeDefinition typeDefinition, ContentStream content) throws ConstraintException,
      StreamNotSupportedException
   {
      if (typeDefinition.getBaseId() == BaseType.DOCUMENT)
      {
         if (typeDefinition.getContentStreamAllowed() == ContentStreamAllowed.REQUIRED && content == null)
         {
            throw new ConstraintException("Content stream required for object of type " + typeDefinition.getId()
               + ", it can't be null.");
         }
         if (typeDefinition.getContentStreamAllowed() == ContentStreamAllowed.NOT_ALLOWED && content != null)
         {
            throw new StreamNotSupportedException("Content stream not allowed for object of type "
               + typeDefinition.getId());
         }
      }
      else if (content != null)
      {
         throw new ConstraintException("Object type " + typeDefinition.getId() + " may not have content stream.");
      }
   }

   private void checkProperties(TypeDefinition typeDefinition, Map<String, Property<?>> properties, int operation)
      throws ConstraintException
   {
      // NOTE do not check is property updatable. Lets storage to do it.
      // Some client may sent whole set of properties but storage should
      // modify only read-write properties and ignore others.
      for (PropertyDefinition<?> definition : typeDefinition.getPropertyDefinitions())
      {
         Property<?> property = null;
         if (properties != null && properties.size() != 0)
         {
            property = properties.get(definition.getId());
         }
         if (definition.isRequired() && operation == CREATE && (property == null || property.getValues().size() == 0))
         {
            throw new ConstraintException("Required property " + definition.getId() + " can't be not set.");
         }
         else if (property != null)
         {
            if (property.getType() != definition.getPropertyType())
            {
               throw new ConstraintException("Property type is not match. Property id " + property.getId());
            }
            if (!definition.isMultivalued() && property.getValues().size() > 1)
            {
               throw new ConstraintException("Property " + property.getId() + " is not multi-valued.");
            }
         }
         // TODO : validate min/max/length etc.
      }
   }

   private void checkACL(TypeDefinition typeDefinition, List<AccessControlEntry> addACL,
      List<AccessControlEntry> removeACL) throws ConstraintException
   {
      if ((addACL != null && addACL.size() != 0) || (removeACL != null && removeACL.size() != 0))
      {
         CapabilityACL capabilityACL = storage.getRepositoryInfo().getCapabilities().getCapabilityACL();
         if (capabilityACL != CapabilityACL.MANAGE)
         {
            throw new NotSupportedException("Managing of ACL is not supported.");
         }
         if (!typeDefinition.isControllableACL())
         {
            throw new ConstraintException("Type " + typeDefinition.getId() + " is not controllable by ACL.");
         }

         List<Permission> permissions = storage.getRepositoryInfo().getAclCapability().getPermissions();
         // create set of supported permissions
         Set<String> supportedPermissions = new HashSet<String>();
         if (permissions == null || permissions.size() == 0)
         {
            for (Permission perm : permissions)
            {
               supportedPermissions.add(perm.getPermission());
            }
         }

         // check is all permissions is valid
         validatePermissions(supportedPermissions, addACL);
         validatePermissions(supportedPermissions, removeACL);
      }
   }

   private void validatePermissions(Set<String> supportedPermissions, List<AccessControlEntry> acl)
      throws ConstraintException
   {
      if (acl != null)
      {
         for (AccessControlEntry ace : acl)
         {
            for (String perm : ace.getPermissions())
            {
               // From spec it looks like 'bacis permissions' not need be in the
               // list of permissions. So be tolerant if set does not contains
               // basic permissions.
               if (!supportedPermissions.contains(perm))
               {
                  try
                  {
                     BasicPermissions.fromValue(perm);
                  }
                  catch (IllegalArgumentException iae)
                  {
                     throw new ConstraintException("Unsupported permissions " + perm);
                  }
               }
            }
         }
      }
   }

   private void checkPolicies(TypeDefinition typeDefinition, Collection<String> policies) throws ConstraintException
   {
      if (policies != null && policies.size() != 0 && !typeDefinition.isControllablePolicy())
      {
         throw new ConstraintException("Type " + typeDefinition.getId() + " is not controllable by Policy.");
      }
   }

   private Collection<PolicyData> createPolicyList(Collection<String> policyIds) throws ObjectNotFoundException
   {
      if (policyIds == null)
      {
         return null;
      }
      Collection<PolicyData> policies = new HashSet<PolicyData>();
      for (String policyID : policyIds)
      {
         ObjectData policy = storage.getObjectById(policyID);
         if (policy.getBaseType() != BaseType.POLICY)
         {
            throw new InvalidArgumentException("Object " + policyID + " is not a Policy object.");
         }
         policies.add((PolicyData)policy);
      }
      return policies;
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
