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

import org.xcmis.spi.model.AllowableActions;
import org.xcmis.spi.model.ChangeEvent;
import org.xcmis.spi.model.Rendition;
import org.xcmis.spi.model.RepositoryInfo;
import org.xcmis.spi.model.UnfileObject;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.query.Query;
import org.xcmis.spi.query.Result;

import java.util.Collection;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: Storage.java 332 2010-03-11 17:24:56Z andrew00x $
 */
public interface Storage extends TypeManager
{

   /**
    * Get storage unique id.
    *
    * @return storage id
    */
   String getId();

   /**
    * Calculate allowable actions for specified object.
    *
    * @param object object
    * @return allowable actions for object
    */
   AllowableActions calculateAllowableActions(ObjectData object);

   /**
    * Get checkedout objects (private working copies) that user has access to.
    *
    * @param folder folder, if <code>null</code> then get all checked out
    *        objects in any folders
    * @param orderBy comma-separated list of query names and the ascending
    *        modifier 'ASC' or the descending modifier 'DESC' for each query
    *        name. A storage's handling of the orderBy input is storage-specific
    *        and storage may ignore this parameter if it not able sort items
    * @return iterator over checked out objects
    */
   ItemsIterator<DocumentData> getCheckedOutDocuments(ObjectData folder, String orderBy);

   /**
    * Create new instance of document with type <code>typeId</code> using
    * <code>folder</code> as parent. If <code>folder == null</code> then
    * document created in unfiling state. If unfiling is not supported
    * {@link ConstraintException} should be thrown. It is not persisted instance
    * and has not ID (method {@link ObjectData#getObjectId()} returns
    * <code>null</code>). To save this document method {@link ObjectData#save()}
    * must be used.
    *
    * @param parent parent folder or <code>null</code> if document should be
    *        created in unfiling state
    * @param typeId type id
    * @param versioningState versioning state
    * @return new unsaved instance of document
    * @throws ConstraintException if any of following condition are met:
    *         <ul>
    *         <li><code>folder == null</code> and unfiling capability is not
    *         supported</li>
    *         <li><code>typeId</code> is id of type whose baseType is not
    *         Document</li>
    *         <li><code>typeId</code> is not in the list of
    *         AllowedChildObjectTypeIds of the <code>folder</code> (method
    *         {@link FolderData#isAllowedChildType(String)} returns
    *         <code>false</code> for <code>typeId</code>)</li>
    *         <li>versionable attribute of the object type definition is
    *         <code>false</code> and a value of the versioningState parameter is
    *         other than <i>none</i></li>
    *         <li>versionable attribute of the object type definition is
    *         <code>true</code> and and the value of the versioningState
    *         parameter is <i>none</i></li>
    *         </ul>
    * @see VersioningState
    */
   DocumentData createDocument(FolderData parent, String typeId, VersioningState versioningState) throws ConstraintException;

   /**
    * Create new document as copy of the given <code>source</code> document and
    * use <code>folder</code> as parent. If <code>folder == null</code> then
    * document created in unfiling state. If unfiling is not supported
    * {@link ConstraintException} should be thrown. New document may be
    * persisted immediately and then updated to apply new properties or not
    * persisted instance may be created. This behavior is implementation
    * specific. In both cases caller may apply new properties and save it. See
    * {@link ObjectData#save()}.
    *
    * @param source source document
    * @param parent parent folder or <code>null</code> if document should be
    *        created in unfiling state
    * @param versioningState versioning state
    * @return new instance of document
    * @throws ConstraintException if any of following condition are met:
    *         <ul>
    *         <li><code>typeId</code> is id of type whose baseType is not
    *         Document</li>
    *         <li><code>typeId</code> is not in the list of
    *         AllowedChildObjectTypeIds of the <code>folder</code> (method
    *         {@link FolderData#isAllowedChildType(String)} returns
    *         <code>false</code> for <code>typeId</code>)</li>
    *         <li>versionable attribute of the object type definition is
    *         <code>false</code> and a value of the versioningState parameter is
    *         other than <i>none</i></li>
    *         <li>versionable attribute of the object type definition is
    *         <code>true</code> and and the value of the versioningState
    *         parameter is <i>none</i></li>
    *         </ul>
    * @throws StorageException if new document can be saved cause to storage
    *         internal problem
    * @see VersioningState
    */
   DocumentData copyDocument(DocumentData source, FolderData parent, VersioningState versioningState)
      throws ConstraintException, StorageException;

   /**
    * Create new instance of folder with type <code>typeId</code> using
    * <code>folder</code> as parent. It is not persisted instance and has not ID
    * (method {@link ObjectData#getObjectId()} returns <code>null</code>). To
    * save this folder method {@link ObjectData#save()} must be used.
    *
    * @param parent parent folder
    * @param typeId type id
    * @return new unsaved instance of folder
    * @throws ConstraintException if any of following condition are met:
    *         <ul>
    *         <li><code>typeId</code> is id of type whose baseType is not Folder
    *         </li>
    *         <li><code>typeId</code> is not in the list of
    *         AllowedChildObjectTypeIds of the <code>folder</code> (method
    *         {@link FolderData#isAllowedChildType(String)} returns
    *         <code>false</code> for <code>typeId</code>)</li>
    *         </ul>
    */
   FolderData createFolder(FolderData parent, String typeId) throws ConstraintException;

   /**
    * Create new instance of policy with type <code>typeId</code> using
    * <code>folder</code> as parent. If <code>folder == null</code> then policy
    * created in unfiling state. It is not persisted instance and has not ID
    * (method {@link ObjectData#getObjectId()} returns <code>null</code>). To
    * save this policy method {@link ObjectData#save()} must be used.
    *
    * @param parent parent folder
    * @param typeId type id
    * @return new unsaved instance of policy
    * @throws ConstraintException if any of following condition are met:
    *         <ul>
    *         <li><code>folder != null</code> and <code>typeId</code> is id of
    *         not fileable type</li>
    *         <li><code>typeId</code> is id of type whose baseType is not Policy
    *         </li>
    *         <li><code>typeId</code> is not in the list of
    *         AllowedChildObjectTypeIds of the <code>folder</code> (method
    *         {@link FolderData#isAllowedChildType(String)} returns
    *         <code>false</code> for <code>typeId</code>)</li>
    *         </ul>
    */
   PolicyData createPolicy(FolderData parent, String typeId) throws ConstraintException;

   /**
    * Create new instance of relationship for specified <code>source</code> and
    * <code>target</code>. It is not persisted instance and has not ID (method
    * {@link ObjectData#getObjectId()} returns <code>null</code>). To save this
    * relationship method {@link ObjectData#save()} must be used.
    *
    * @param source source of relationship
    * @param target target of relationship
    * @param typeId type of relationship
    * @return new unsaved instance of relationship
    * @throws ConstraintException if any of following condition are met:
    *         <ul>
    *         <li><code>typeId</code> is id of type whose baseType is not
    *         Relationship</li>
    *         <li><code>source</code> has object type that is not in the list of
    *         AllowedSourceTypes specified by the object type definition</li>
    *         <li><code>target</code> has object type that is not in the list of
    *         AllowedTargetTypes specified by the object type definition</li>
    *         </ul>
    */
   RelationshipData createRelationship(ObjectData source, ObjectData target, String typeId) throws ConstraintException;

   /**
    * Delete specified object. If multi-filed object is deleted then it is
    * removed from all folders it is filed in. If specified object is private
    * working copy the deletion object is the same as to cancel checkout
    * operation. See {@link DocumentData#cancelCheckout()}.
    *
    * @param object object to be deleted
    * @param deleteAllVersions if <code>false</code> then delete only the object
    *        specified, if <code>true</code> delete all versions of versionable
    *        document. This parameter must be ignored if specified object is not
    *        document or not versionable document
    * @throws ConstraintException if specified object is folder that contains
    *         one or more object or root folder
    * @throws UpdateConflictException if specified object is not current any
    *         more
    * @throws StorageException if object can't be delete (persist operation)
    *         cause to storage internal problem
    */
   void deleteObject(ObjectData object, boolean deleteAllVersions) throws ConstraintException, UpdateConflictException,
      StorageException;

   /**
    * Delete the specified folder object and all of its child- and
    * descendant-objects.
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
    * @param continueOnFailure if <code>true</code>, then the stprage SHOULD
    *        continue attempting to perform this operation even if deletion of a
    *        child object in the specified folder cannot be deleted. Default is
    *        <code>false</code>.
    * @return list of id that were not deleted
    * @throws UpdateConflictException if object that is no longer current (as
    *         determined by the storage)
    */
   Collection<String> deleteTree(FolderData folder, boolean deleteAllVersions, UnfileObject unfileObject,
      boolean continueOnFailure) throws UpdateConflictException;

   /**
    * Remove non-folder fileable object from all folder where in which it is
    * currently filed. This method never remove object itself .
    *
    * @param object object
    */
   void unfileObject(ObjectData object);

   /**
    * Save updated object or newly created object.
    *
    * @param object object to be saved
    * @throws StorageException if changes can't be saved cause storage internal
    *         errors
    * @throws NameConstraintViolationException if updated name (property
    *         'cmis:name') cause name conflict, e.g. object with the same name
    *         already exists
    * @throws UpdateConflictException if saved object is not current any more
    */
   String saveObject(ObjectData object) throws StorageException, NameConstraintViolationException,
      UpdateConflictException;

   /**
    * Gets content changes.
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
    * @param query SQL query
    * @return set of query results
    * @throws InvalidArgumentException if specified <code>query</code> is
    *         invalid
    */
   ItemsIterator<Result> query(Query query) throws InvalidArgumentException;

   /**
    * Get object by unique identifier.
    *
    * @param objectId object's ID
    * @return object
    * @throws ObjectNotFoundException if object with specified ID was not found
    */
   ObjectData getObject(String objectId) throws ObjectNotFoundException;

   /**
    * Get object by path.
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
    * @param object object to be moved
    * @param target destination folder
    * @param source folder from which object must be moved
    * @return
    * @throws ConstraintException if type of the given object is NOT in the list
    *         of AllowedChildObjectTypeIds of the <code>target</code>
    * @throws InvalidArgumentException if <code>source</code> is not object's
    *         parent folder (or one of the parent folders if the storage
    *         supports multi-filing.).
    * @throws UpdateConflictException if object that is no longer current (as
    *         determined by the storage).
    * @throws VersioningException if object is a non-current document version
    * @throws NameConstraintViolationException if moving operation cause name
    *         conflict, e.g. destination folder already contains object with the
    *         same name
    * @throws StorageException if object can not be moved (save changes) cause
    *         to storage internal problem
    */
   ObjectData moveObject(ObjectData object, FolderData target, FolderData source) throws ConstraintException,
      InvalidArgumentException, UpdateConflictException, VersioningException, NameConstraintViolationException,
      StorageException;

   /**
    * Get object renditions.
    *
    * @param object object
    * @return iterator over object's renditions. If object has not any
    *         renditions then empty iterator must be returned but never
    *         <code>null</code>
    */
   ItemsIterator<Rendition> getRenditions(ObjectData object);

   /**
    * Get description of storage and its capabilities.
    *
    * @return storage description
    */
   RepositoryInfo getRepositoryInfo();

   /**
    * Collection of all Document in the specified version series, sorted by
    * cmis:creationDate descending.
    *
    * @param versionSeriesId id of version series
    * @return document versions
    * @throws ObjectNotFoundException if version series with
    *         <code>versionSeriesId</code> does not exist
    */
   Collection<DocumentData> getAllVersions(String versionSeriesId) throws ObjectNotFoundException;

}
