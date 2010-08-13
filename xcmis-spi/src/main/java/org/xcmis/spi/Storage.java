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
package org.xcmis.spi;

import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.AllowableActions;
import org.xcmis.spi.model.ChangeEvent;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.Rendition;
import org.xcmis.spi.model.RepositoryInfo;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.UnfileObject;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.query.Query;
import org.xcmis.spi.query.Result;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: Storage.java 332 2010-03-11 17:24:56Z andrew00x $
 */
public interface Storage extends TypeManager
{
   /**
    * Gets storage unique id.
    * 
    * Implementation Compatibility: MUST be implemented
    * 
    * @return storage id
    */
   String getId();

   /**
    * Calculates allowable actions for specified object.
    * 
    * Implementation Compatibility: MUST be implemented
    * 
    * @param object object
    * @return allowable actions for object
    */
   AllowableActions calculateAllowableActions(ObjectData object);

   /**
    * Get checkedout objects (private working copies) that user has access to.
    * 
    * Implementation Compatibility: Optional. Repository versioning specific.
    * 
    * @param folder folder, if <code>null</code> then get all checked out
    *        objects in any folders
    * @param orderBy comma-separated list of query names and the ascending
    *        modifier 'ASC' or the descending modifier 'DESC' for each query
    *        name. A storage's handling of the orderBy input is storage-specific
    *        and storage may ignore this parameter if it not able sort items
    * @return iterator over checked out objects
    */
   ItemsIterator<DocumentData> getCheckedOutDocuments(FolderData folder, String orderBy);

   /**
    * Create new document with type <code>typeDefinition</code> using
    * <code>parent</code> as parent. If <code>parent == null</code> then
    * document created in unfiling state. If unfiling is not supported
    * {@link ConstraintException} should be thrown.
    * 
    * Implementation Compatibility: MUST be implemented
    * 
    * @param parent parent folder or <code>null</code> if document should be
    *        created in unfiling state
    * @param typeDefinition the document type definition
    * @param properties the document properties
    * @param content the document content
    * @param acl the list of ACEs to be applied to newly create document. May be
    *        <code>null</code> or empty list if no ACEs to be applied
    * @param policies the list of policies. May be <code>null</code> or empty
    *        collection if no policies to be applied
    * @param versioningState versioning state
    * @return newly created document
    * @throws ConstraintException if any of following condition are met:
    *         <ul>
    *         <li>versionable attribute of the object type definition is
    *         <code>false</code> and a value of the versioningState parameter is
    *         other than <i>none</i></li>
    *         <li>versionable attribute of the object type definition is
    *         <code>true</code> and and the value of the versioningState
    *         parameter is <i>none</i></li>
    *         <li>if <code>parent == null</code> and unfiling is not supported</li>
    *         </ul>
    * @throws NameConstraintViolationException if property 'cmis:name' throws
    *         conflict
    * @throws IOException if any i/o error occurs when try to set document
    *         content stream
    * @throws StorageException if object can not be saved cause to storage
    *         internal problem
    * @see VersioningState
    */
   DocumentData createDocument(FolderData parent, TypeDefinition typeDefinition, Map<String, Property<?>> properties,
      ContentStream content, List<AccessControlEntry> acl, Collection<PolicyData> policies,
      VersioningState versioningState) throws ConstraintException, NameConstraintViolationException, IOException,
      StorageException;

   /**
    * Create new document as copy of the given <code>source</code> document and
    * use <code>parent</code> as parent. If <code>parent == null</code> then
    * document created in unfiling state. If unfiling is not supported
    * {@link ConstraintException} should be thrown.
    * 
    * Implementation Compatibility: MUST be implemented
    * 
    * @param source source document
    * @param parent parent folder or <code>null</code> if document should be
    *        created in unfiling state
    * @param properties the document properties
    * @param acl the list of ACEs to be applie dto newly created document. May
    *        be <code>null</code> or empty list if no ACEs to be applied
    * @param policies the list of policies. May be <code>null</code> or empty
    *        collection if no policies to be applied
    * @param versioningState versioning state
    * @return newly created document
    * @throws ConstraintException if any of following condition are met:
    *         <ul>
    *         <li>versionable attribute of the object type definition is
    *         <code>false</code> and a value of the versioningState parameter is
    *         other than <i>none</i></li>
    *         <li>versionable attribute of the object type definition is
    *         <code>true</code> and and the value of the versioningState
    *         parameter is <i>none</i></li>
    *         <li>if <code>parent == null</code> and unfiling is not supported</li>
    *         </ul>
    * @throws NameConstraintViolationException if property 'cmis:name' throws
    *         conflict
    * @throws StorageException if new document can be saved cause to storage
    *         internal problem
    * @see VersioningState
    */
   DocumentData copyDocument(DocumentData source, FolderData parent, Map<String, Property<?>> properties,
      List<AccessControlEntry> acl, Collection<PolicyData> policies, VersioningState versioningState)
      throws ConstraintException, NameConstraintViolationException, StorageException;

   /**
    * Create new folder with type <code>typeDefinition</code> using
    * <code>folder</code> as parent.
    * 
    * Implementation Compatibility: MUST be implemented
    * 
    * @param parent parent folder
    * @param typeDefinition the folder type definition
    * @param properties the folder properties
    * @param acl the list of ACEs to be applied to newly created folder. May be
    *        <code>null</code> or empty list if no ACEs to be applied
    * @param policies the list of policies. May be <code>null</code> or empty
    *        collection if no policies to be applied
    * @return newly created folder
    * @throws ConstraintException if <code>parent == null</code>
    * @throws NameConstraintViolationException if property 'cmis:name' throws
    *         conflict
    * @throws StorageException if object can not be removed cause to storage
    *         internal problem
    */
   FolderData createFolder(FolderData parent, TypeDefinition typeDefinition, Map<String, Property<?>> properties,
      List<AccessControlEntry> acl, Collection<PolicyData> policies) throws ConstraintException,
      NameConstraintViolationException, StorageException;

   /**
    * Create new policy with type <code>typeDefinition</code> using
    * <code>parent</code> as parent. If <code>parent == null</code> then policy
    * created in unfiling state.
    * 
    * 2.2.4.5 createPolicy
    *      
    * Implementation Compatibility: the support for policy objects is optional, 
    * if implementation does not support cmis:policy object-type method should
    * throw {@link NotSupportedException} 
    * 
    * @param parent parent folder
    * @param typeDefinition the policy type definition
    * @param properties the policy properties
    * @param acl the list of ACEs to be applied to newly created policy. May be
    *        <code>null</code> or empty list if no ACEs to be applied
    * @param policies the list of policies. May be <code>null</code> or empty
    *        collection if no policies to be applied
    * @return newly created policy
    * @throws ConstraintException if <code>parent == null</code> and policy type
    *         is fileable
    * @throws NameConstraintViolationException if property 'cmis:name' throws
    *         conflict
    * @throws StorageException if object can not be saved cause to storage
    *         internal problem
    */
   PolicyData createPolicy(FolderData parent, TypeDefinition typeDefinition, Map<String, Property<?>> properties,
      List<AccessControlEntry> acl, Collection<PolicyData> policies) throws ConstraintException,
      NameConstraintViolationException, StorageException;

   /**
    * Create new relationship for specified <code>source</code> and
    * <code>target</code>.
    * 
    * Implementation Compatibility: the support for relationship objects is optional, 
    * if implementation does not support cmis:relationship object-type method should
    * throw {@link NotSupportedException} 
    * 
    * @param source source of relationship
    * @param target target of relationship
    * @param typeDefinition the relationship type definition
    * @param properties the relationship properties
    * @param acl the list of ACEs to be applied to newly created relationship.
    *        May be <code>null</code> or empty list if no ACEs to be applied
    * @param policies the list of policies. May be <code>null</code> or empty
    *        collection if no policies to be applied
    * @return newly created relationship
    * @throws NameConstraintViolationException if property 'cmis:name' throws
    *         conflict
    * @throws StorageException if object can not be removed cause to storage
    *         internal problem
    */
   RelationshipData createRelationship(ObjectData source, ObjectData target, TypeDefinition typeDefinition,
      Map<String, Property<?>> properties, List<AccessControlEntry> acl, Collection<PolicyData> policies)
      throws NameConstraintViolationException, StorageException;

   /**
    * Delete specified object. If multi-filed object is deleted then it is
    * removed from all folders it is filed in. If specified object is private
    * working copy the deletion object is the same as to cancel checkout
    * operation. See {@link DocumentData#cancelCheckout()}.
    * 
    * Implementation Compatibility: MUST be implemented
    * 
    * @param object object to be deleted
    * @param deleteAllVersions if <code>false</code> then delete only the object
    *        specified, if <code>true</code> delete all versions of versionable
    *        document. This parameter must be ignored if specified object is not
    *        document or not versionable document
    * @throws VersioningException if object can not be removed cause to
    *         versioning conflict
    * @throws UpdateConflictException if specified object is not current any
    *         more
    * @throws StorageException if object can't be delete (persist operation)
    *         cause to storage internal problem
    */
   void deleteObject(ObjectData object, boolean deleteAllVersions) throws VersioningException, UpdateConflictException,
      StorageException;

   /**
    * Delete the specified folder object and all of its child- and
    * descendant-objects.
    * 
    * 
    * Implementation Compatibility: MUST be implemented
    * 
    * @param folder folder to be deleted
    * @param deleteAllVersions if <code>true</code> then delete all versions of
    *        the document in this folder. If <code>false</code>, delete only the
    *        document object specified. This parameter will be ignored if
    *        parameter when <code>objectId</code> non-document object or
    *        non-versionable document
    * @param unfileObject an enumeration specifying how the storage MUST process
    *        file-able child objects:
    *        <ul>
    *        <li>unfile: Unfile all fileable objects</li>
    *        <li>deletesinglefiled: Delete all fileable non-folder objects whose
    *        only parent-folders are in the current folder tree. Unfile all
    *        other fileable non-folder objects from the current folder tree</li>
    *        <li>delete: Delete all fileable objects</li>
    *        </ul>
    * @param continueOnFailure if <code>true</code>, then the storage SHOULD
    *        continue attempting to perform this operation even if deletion of a
    *        child object in the specified folder cannot be deleted
    * @return list of id that were not deleted
    * @throws UpdateConflictException if object that is no longer current (as
    *         determined by the storage)
    */
   Collection<String> deleteTree(FolderData folder, boolean deleteAllVersions, UnfileObject unfileObject,
      boolean continueOnFailure) throws UpdateConflictException;

   /**
    * Remove non-folder fileable object from all folder where in which it is
    * currently filed. <b>NOTE</b> This method never remove object itself.
    * 
    * 2.2.5.2 removeObjectFromFolder
    * 
    * Implementation Compatibility: SHOULD be implemented if the repository 
    * supports the multifiling (capabilityMultifiling) and unfiling (capabilityUnfiling) optional capabilities.
    * Otherwise, {@link NotSupportedException} should be thrown.
    * 
    * @param object object
    */
   void unfileObject(ObjectData object);

   /**
    * Gets content changes.
    * 
    * 
    * Implementation Compatibility: SHOULD be implemented if the repository 
    * supports changes Capability (capabilityChanges != none).
    * Otherwise, {@link NotSupportedException} should be thrown.
    * 
    * 
    * @param changeLogToken if value other than <code>null</code>, then change
    *        event corresponded to the value of the specified change log token
    *        will be returned as the first result in the output. If not
    *        specified, then will be returned the first change event recorded in
    *        the change log. When set of changes passed is returned then
    *        <code>changeLogToken</code> must contains log token corresponded to
    *        the last change event. Then it may be used by caller for getting
    *        next set on change events
    * @return iterator over change log events
    * @throws ConstraintException if the event corresponding to the change log
    *         token provided as an input parameter is no longer available in the
    *         change log. (E.g. because the change log was truncated)
    */
   ItemsIterator<ChangeEvent> getChangeLog(String changeLogToken) throws ConstraintException;

   /**
    * Handle specified SQL query.
    * 
    * Implementation Compatibility: SHOULD be implemented if the repository 
    * supports query Capability (capabilityQuery != none)).
    * Otherwise, {@link NotSupportedException} should be thrown.
    * 
    * 
    * @param query SQL query
    * @return set of query results
    * @throws InvalidArgumentException if specified <code>query</code> is
    *         invalid
    */
   ItemsIterator<Result> query(Query query);

   /**
    * Get object by unique identifier.
    * 
    * Implementation Compatibility: MUST be implemented
    * 
    * @param objectId object's ID
    * @return object
    * @throws ObjectNotFoundException if object with specified ID was not found
    */
   ObjectData getObjectById(String objectId) throws ObjectNotFoundException;

   /**
    * Get object by path.
    * 
    * Implementation Compatibility: MUST be implemented
    * 
    * @param path path
    * @return object
    * @throws ObjectNotFoundException if object with specified path was not
    *         found
    */
   ObjectData getObjectByPath(String path) throws ObjectNotFoundException;

   /**
    * Move <code>object</code> from <code>source</code> to <code>target</code>.
    * If operation successful then changes saved immediately.
    * 
    * Implementation Compatibility: MUST be implemented
    * 
    * @param object object to be moved
    * @param target destination folder
    * @param source folder from which object must be moved
    * @return
    * @throws UpdateConflictException if object that is no longer current (as
    *         determined by the storage).
    * @throws VersioningException if object is a non-current document version
    * @throws NameConstraintViolationException if moving operation cause name
    *         conflict, e.g. destination folder already contains object with the
    *         same name
    * @throws StorageException if object can not be moved (save changes) cause
    *         to storage internal problem
    */
   ObjectData moveObject(ObjectData object, FolderData target, FolderData source) throws UpdateConflictException,
      VersioningException, NameConstraintViolationException, StorageException;

   /**
    * Get object renditions.
    * 
    * Implementation Compatibility: SHOULD be implemented if capabilityRenditions = read
    * 
    * @param object the object
    * @return iterator over object's renditions. If object has not any
    *         renditions then empty iterator must be returned but never
    *         <code>null</code>
    */
   ItemsIterator<Rendition> getRenditions(ObjectData object);

   /**
    * Get description of storage and its capabilities.
    * 
    * 2.2.2.2 getRepositoryInfo
    * 
    * Implementation Compatibility: MUST be implemented
    * 
    * The "Get Repository Information" service MUST also return implementation information including vendor
    * name, product name, product version, version of CMIS that it supports, the root folder ID (see section
    * 2.1.5.2 Folder Hierarchy), and MAY include other implementation-specific information. The version of
    * CMIS that the repository supports MUST be expressed as a Decimal that matches the specification
    * version.
    * 
    * @return storage description
    */
   RepositoryInfo getRepositoryInfo();

   /**
    * Collection of all Document in the specified version series, sorted by
    * cmis:creationDate descending.
    * 
    * Implementation Compatibility: SHOULD be implemented if the repository 
    * supports versioning.
    * Otherwise, {@link NotSupportedException} should be thrown.
    * 
    * @param versionSeriesId the id of version series
    * @return document versions
    * @throws ObjectNotFoundException if version series with
    *         <code>versionSeriesId</code> does not exist
    */
   Collection<DocumentData> getAllVersions(String versionSeriesId) throws ObjectNotFoundException;

   /**
    * Iterator of all unfilled documents identifiers.
    * 
    * Implementation Compatibility: SHOULD be implemented if the repository 
    * supports the unfiling (capabilityUnfiling) optional capabilities.
    * Otherwise, {@link NotSupportedException} should be thrown.
    * 
    * @return Iterator of all unfilled documents identifiers.
    * @throws StorageException if any storage error occurs
    */
   Iterator<String> getUnfiledObjectsId() throws StorageException;
}
