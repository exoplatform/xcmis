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

package org.xcmis.core;

import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentAlreadyExistsException;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.object.CmisObject;
import org.xcmis.spi.object.ContentStream;

import java.io.IOException;
import java.util.List;

/**
 * Provides methods for CRUD operations in repository.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface ObjectService
{

   /**
    * Create a document object.
    * 
    * @param repositoryId repository id
    * @param folderId parent folder id for object. May be null if repository
    *           supports unfiling.
    * @param properties properties to be applied to newly created document
    * @param content document content
    * @param versioningState enumeration specifying what the versioning state of
    *           the newly created object shall be
    * @param addACL set Access Control Entry to be applied for newly created
    *           document, either using the ACL from <code>folderId</code>
    *           if specified, or being applied if no <code>folderId</code>
    *           is specified
    * @param removeACL set Access Control Entry that MUST be removed from
    *           the newly created document, either using the ACL from
    *           <code>folderId</code> if specified, or being ignored if no
    *           <code>folderId</code> is specified
    * @param policies list of policy id that MUST be applied to the newly
    *           created document
    * @param includeObjectInfo TODO
    * @return newly created document
    * @throws StreamNotSupportedException if the contentStreamAllowed attribute
    *            of the object type definition specified by the cmis:objectTypeId
    *             property value is set to 'not allowed' and a contentStream
    *             input parameter is provided
    * @throws ConstraintException if any of following condition are met:
    *           <ul>
    *           <li>cmis:objectTypeId property value is not an object type
    *           whose baseType is Document</li>
    *           <li>cmis:objectTypeId property value is NOT in the list of
    *           AllowedChildObjectTypeIds of the parent-folder specified by
    *           <code>folderId</code></li>
    *           <li>value of any of the properties violates the
    *           min/max/required/length constraints specified in the property
    *           definition in the object type<li>
    *           <li>contentStreamAllowed attribute of the object type definition
    *           specified by the cmis:objectTypeId property value is set to
    *           <i>required</i> and no content input parameter is
    *           provided</li>
    *           <li>versionable attribute of the object type definition
    *           specified by the cmis:objectTypeId property value is set to FALSE
    *           and a value for the versioningState input parameter is provided
    *           that is something other than <i>none</i></li>
    *           <li>versionable attribute of the object type definition specified
    *           by the cmis:objectTypeId property value is set to TRUE and the
    *           value for the versioningState input parameter is provided that is
    *           <i>none</i></li>
    *           <li>controllablePolicy attribute of the object type definition
    *           specified by the cmis:objectTypeId property value is set to FALSE
    *           and at least one policy is provided</li>
    *           <li>controllableACL attribute of the object type definition
    *           specified by the cmis:objectTypeId property value is set to FALSE
    *           and at least one ACE is provided</li>
    *           <li>at least one of the permissions is used in an ACE provided
    *           which is not supported by the repository</li>
    *           </ul>
    * @throws NameConstraintViolationException violation is detected with the given
    *           <i>cmis:name</i> property value
    * @throws ObjectNotFoundException if target folder with specified id
    *            <code>folderId</code> does not exist
    * @throws IOException if any i/o error occurs when try to set document's
    *           content stream
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   CmisObject createDocument(String repositoryId, String folderId, CmisPropertiesType properties,
      ContentStream content, EnumVersioningState versioningState, CmisAccessControlListType addACL,
      CmisAccessControlListType removeACL, List<String> policies, boolean includeObjectInfo) throws StreamNotSupportedException,
      ConstraintException, NameConstraintViolationException, ObjectNotFoundException, IOException, RepositoryException;

   /**
    * Create a document object as a copy of the given source document in
    * the <code>folderId</code>.
    * 
    * @param repositoryId repository id
    * @param sourceId id for the source document
    * @param folderId parent folder id for object. May be null if repository
    *          supports unfiling.
    * @param properties properties to be applied to newly created document
    * @param versioningState enumeration specifying what the versioning state of
    *           the newly created object shall be
    * @param addACL set Access Control Entry to be applied for newly created
    *           document, either using the ACL from <code>folderId</code>
    *           if specified, or being applied if no <code>folderId</code>
    *           is specified
    * @param removeACL set Access Control Entry that MUST be removed from
    *           the newly created document, either using the ACL from
    *           <code>folderId</code> if specified, or being ignored if no
    *           <code>folderId</code> is specified
    * @param policies list of policy id that MUST be applied to the newly
    *           created document
    * @param includeObjectInfo TODO
    * @return newly created document
    * @throws ConstraintException if any of following condition are met:
    *           <ul>
    *           <li>sourceId is not an Object whose baseType is Document</li>
    *           <li>source document's cmis:objectTypeId property value is NOT
    *           in the list of AllowedChildObjectTypeIds of the parent-folder
    *           specified by <code>folderId</code></li>
    *           <li>versionable attribute of the object type definition specified
    *           by the cmis:objectTypeId property value is set to FALSE and a
    *           value for the versioningState input parameter is provided that
    *           is something other than <i>none</i></li>
    *           <li>versionable attribute of the object type definition specified
    *           by the cmis:objectTypeId property value is set to TRUE and the
    *           value for the versioningState input parameter is provided that
    *           is <i>none</i></li>
    *           <li>controllablePolicy attribute of the object type definition
    *           specified by the cmis:objectTypeId property value is set to
    *           FALSE and at least one policy is provided</li>
    *           <li>controllableACL attribute of the object type definition
    *           specified by the cmis:objectTypeId property value is set to
    *           FALSE and at least one ACE is provided</li>
    *           <li>At least one of the permissions is used in an ACE provided
    *           which is not supported by the repository</li>
    *           </ul>
    * @throws NameConstraintViolationException violation is detected with the given
    *           <i>cmis:name</i> property value
    * @throws ObjectNotFoundException if target folder with specified id
    *            <code>folderId</code> does not exist
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   CmisObject createDocumentFromSource(String repositoryId, String sourceId, String folderId,
      CmisPropertiesType properties, EnumVersioningState versioningState, CmisAccessControlListType addACL,
      CmisAccessControlListType removeACL, List<String> policies, boolean includeObjectInfo) throws ConstraintException,
      NameConstraintViolationException, ObjectNotFoundException, RepositoryException;

   /**
    * Create a folder object.
    * 
    * @param repositoryId repository id
    * @param folderId parent folder id for new folder
    * @param properties properties to be applied to newly created folder
    * @param addACL set Access Control Entry to be applied for newly created
    *          folder, either using the ACL from <code>folderId</code>
    *          if specified, or being applied if no <code>folderId</code>
    *          is specified
    * @param removeACL set Access Control Entry that MUST be removed from
    *          the newly created folder, either using the ACL from
    *          <code>folderId</code> if specified, or being ignored if no
    *          <code>folderId</code> is specified
    * @param policies list of policy id that MUST be applied to the newly
    *          created folder
    * @param includeObjectInfo TODO
    * @return newly created folder
    * @throws ConstraintException if any of following condition are met:
    *         <ul>
    *         <li>cmis:objectTypeId property value is not an object type  whose
    *         baseType is Folder</li>
    *         <li>value of any of the properties violates the min/max/required/length
    *         constraints specified in the property definition in the object type</li>
    *         <li>cmis:objectTypeId property value is NOT in the list
    *         of AllowedChildObjectTypeIds of the parent-folder specified by
    *         <code>folderId</code></li>
    *         <li>controllablePolicy attribute of the object type definition specified
    *         by the cmis:objectTypeId property value is set to FALSE and at least
    *         one policy is provided</li>
    *         <li> controllableACL attribute of the object type definition specified
    *         by the cmis:objectTypeId property value is set to FALSE and at least one
    *         ACE is provided</li>
    *         <li>at least one of the permissions is used in an ACE provided which is
    *         not supported by the repository</li>
    *         </ul>
    * @throws NameConstraintViolationException violation is detected with the given
    *           <i>cmis:name</i> property value
    * @throws ObjectNotFoundException if target folder with specified id
    *            <code>folderId</code> does not exist
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   CmisObject createFolder(String repositoryId, String folderId, CmisPropertiesType properties,
      CmisAccessControlListType addACL, CmisAccessControlListType removeACL, List<String> policies, boolean includeObjectInfo)
      throws ConstraintException, NameConstraintViolationException, ObjectNotFoundException, RepositoryException;

   /**
    * Create a policy object.
    * 
    * @param repositoryId repository id
    * @param folderId parent folder id may be null if policy object type is
    *          not fileable
    * @param properties properties to be applied to newly created 
    * @param addACL set Access Control Entry to be applied for newly created
    *          policy, either using the ACL from <code>folderId</code>
    *          if specified, or being applied if no <code>folderId</code>
    *          is specified
    * @param removeACL set Access Control Entry that MUST be removed from
    *          the newly created policy, either using the ACL from
    *          <code>folderId</code> if specified, or being ignored if no
    *          <code>folderId</code> is specified
    * @param policies list of policy id that MUST be applied to the newly
    *          created policy
    * @param includeObjectInfo TODO
    * @return newly created policy
    * @throws ConstraintException if any of following condition are met:
    *           <ul>
    *           <li>cmis:objectTypeId property value is not an object type whose
    *           baseType is Policy</li>
    *           <li>value of any of the properties violates the
    *           min/max/required/length constraints specified in the property
    *           definition in the object type</li>
    *           <li>cmis:objectTypeId property value is NOT in the list of
    *           AllowedChildObjectTypeIds of the parent-folder specified by folderId</li>
    *           <li>controllablePolicy attribute of the object type definition
    *           specified by the cmis:objectTypeId property value is set to FALSE
    *           and at least one policy is provided</li>
    *           <li>controllableACL attribute of the object type definition
    *           specified by the cmis:objectTypeId property value is set to FALSE
    *           and at least one ACE is provided</li>
    *           <li>at least one of the permissions is used in an ACE provided
    *           which is not supported by the repository</li>
    *           </ul>
    * @throws NameConstraintViolationException violation is detected with the given
    *           <i>cmis:name</i> property value
    * @throws ObjectNotFoundException if target folder with specified id
    *            <code>folderId</code> does not exist
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   CmisObject createPolicy(String repositoryId, String folderId, CmisPropertiesType properties,
      CmisAccessControlListType addACL, CmisAccessControlListType removeACL, List<String> policies, boolean includeObjectInfo)
      throws ConstraintException, NameConstraintViolationException, ObjectNotFoundException, RepositoryException;

   /**
    * Create a relationship object.
    * 
    * @param repositoryId repository id
    * @param properties properties to be applied to newly created relationship
    * @param addACL set Access Control Entry to be applied for newly created
    *          relationship
    * @param removeACL set Access Control Entry that MUST be removed from
    *          the newly created relationship
    * @param policies list of policy id that MUST be applied to the newly
    *          created relationship
    * @param includeObjectInfo TODO
    * @return newly created relationship
    * @throws ConstraintException if any of following condition are met:
    *            <ul>
    *            <li>cmis:objectTypeId property value is not an object type whose
    *            baseType is Relationship</li>
    *            <li>value of any of the properties violates the
    *            min/max/required/length constraints specified in the property
    *            definition in the object type</li>
    *            <li>sourceObjectId ObjectType is not in the list of
    *            AllowedSourceTypes specified by the object type definition
    *            specified by cmis:objectTypeId property value</li>
    *            <li>targetObjectId ObjectType is not in the list of
    *            AllowedTargetTypes specified by the object type definition
    *            specified by cmis:objectTypeId property value</li>
    *            <li>controllablePolicy attribute of the object type definition
    *            specified by the cmis:objectTypeId property value is set to FALSE
    *            and at least one policy is provided</li>
    *            <li>controllableACL attribute of the object type definition
    *            specified by the cmis:objectTypeId property value is set to FALSE
    *            and at least one ACE is provided</li>
    *            <li>at least one of the permissions is used in an ACE provided
    *            which is not supported by the repository</li>
    *            </ul>
    * @throws ObjectNotFoundException if source or target object for relationship
    *            does not exist
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   CmisObject createRelationship(String repositoryId, CmisPropertiesType properties,
      CmisAccessControlListType addACL, CmisAccessControlListType removeACL, List<String> policies, boolean includeObjectInfo)
      throws ConstraintException, ObjectNotFoundException, RepositoryException;

   /**
    * Delete the content stream for the specified Document object.
    * 
    * @param repositoryId repository id
    * @param documentId document id
    * @param changeToken is used for optimistic locking and/or concurrency
    *           checking to ensure that user updates do not conflict
    * @param includeObjectInfo TODO
    * @return CMIS object 
    * @throws ConstraintException if object's type definition
    *            contentStreamAllowed attribute is set to <i>required</i>
    * @throws UpdateConflictException if update an object that is no longer current.
    *            Repository determine this by using change token
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   CmisObject deleteContentStream(String repositoryId, String documentId, String changeToken, boolean includeObjectInfo)
      throws ConstraintException, UpdateConflictException, RepositoryException;

   /**
    * Delete the specified object.
    * 
    * @param repositoryId repository id
    * @param objectId document id
    * @param deleteAllVersion if TRUE then delete all versions of the document.
    *           If FALSE, delete only the document object specified.
    *           This parameter will be ignored if parameter when
    *           <code>objectId</code> non-document object or non-versionable
    *           document
    * @throws ConstraintException if objectId is folder that contains
    *            one or more children
    * @throws UpdateConflictException if object that is no longer current
    *            (as determined by the repository).
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   // TODO : not clear about UpdateConflictException in this case.
   void deleteObject(String repositoryId, String objectId, boolean deleteAllVersion)
      throws ConstraintException, UpdateConflictException, RepositoryException;

   /**
    * Delete the specified folder object and all of its child- and descendant-objects.
    * 
    * @param repositoryId repository id
    * @param folderId folder id
    * @param unfileObjects an enumeration specifying how the repository
    *          MUST process file-able child objects.
    *          <ul>
    *          <li>unfile: Unfile all fileable objects</li>
    *          <li>deletesinglefiled: Delete all fileable non-folder objects
    *          whose only parent-folders are in the current folder tree.
    *          Unfile all other fileable non-folder objects from the current
    *          folder tree</li>
    *          <li>delete: Delete all fileable objects</li>
    *          </ul>
    * @param continueOnFailure if TRUE, then the repository SHOULD continue
    *          attempting to perform this operation even if deletion of a child
    *          object in the specified folder cannot be deleted
    * @return list of id that were not deleted
    * @throws UpdateConflictException if object that is no longer current
    *            (as determined by the repository).
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   // TODO : not clear about UpdateConflictException in this case.
   List<String> deleteTree(String repositoryId, String folderId, EnumUnfileObject unfileObjects,
      boolean continueOnFailure) throws UpdateConflictException, RepositoryException;

   /**
    * Get the list of allowable actions for an Object.
    * 
    * @param repositoryId repository id
    * @param objectId object id
    * @return allowable actions for object
    * @throws ObjectNotFoundException if object with id <code>objectId</code>
    *            does not exists
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   CmisAllowableActionsType getAllowableActions(String repositoryId, String objectId) throws ObjectNotFoundException,
      RepositoryException;

   /**
    * Get object's content stream.
    * 
    * @param repositoryId repository id
    * @param documentId document id
    * @param streamId identifier for the rendition stream, when used
    *          to get a rendition stream. For Documents, if not provided
    *          then this method returns the content stream. For Folders
    *          (if Folders supports renditions) this parameter must be
    *          provided.
    * @param offset first byte of the content to retrieve
    * @param length get exactly number of bytes from content stream
    * @return object content
    * @throws ObjectNotFoundException if document with specified id
    *            <code>documentId</code> does not exist
    * @throws ConstraintException if object does not have content stream
    *            or rendition stream
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   ContentStream getContentStream(String repositoryId, String documentId, String streamId, long offset, long length)
      throws ConstraintException, ObjectNotFoundException, RepositoryException;

   /**
    * Get object. 
    * 
    * @param repositoryId repository id
    * @param objectId object id
    * @param includeAllowableActions if TRUE then include object allowable
    *          actions for object
    * @param includeRelationships include object relationships
    * @param includePolicyIds include policies applied to object
    * @param includeACL include object's ACL
    * @param propertyFilter property filter as string
    * @param renditionFilter rendition filter as string
    * @param includeObjectInfo TODO
    * @return retrieval object
    * @throws ObjectNotFoundException if object with specified id
    *            <code>objectId</code> does not exist
    * @throws FilterNotValidException if <code>propertyFilter</code> or
    *           <code>renditionFilter</code> is invalid 
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   CmisObject getObject(String repositoryId, String objectId, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, boolean includePolicyIds, boolean includeACL,
      String propertyFilter, String renditionFilter, boolean includeObjectInfo) throws ObjectNotFoundException, FilterNotValidException,
      RepositoryException;

   /**
    * Get object by specified path.
    * 
    * @param repositoryId repository id
    * @param path object's path
    * @param includeAllowableActions TRUE if allowable actions should be included
    *           in response false otherwise
    * @param includeRelationships include object's relationship
    * @param includePolicyIds include policies IDs applied to object
    * @param includeACL include ACL
    * @param propertyFilter property filter as string
    * @param renditionFilter rendition filter as string 
    * @param includeObjectInfo TODO
    * @return object
    * @throws ObjectNotFoundException if object with specified <code>path</code>
    *            not found in repository
    * @throws FilterNotValidException if <code>propertyFilter</code> or
    *            <code>renditionFilter</code> is invalid. 
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   CmisObject getObjectByPath(String repositoryId, String path, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, boolean includePolicyIds, boolean includeACL,
      String propertyFilter, String renditionFilter, boolean includeObjectInfo) throws ObjectNotFoundException, FilterNotValidException,
      RepositoryException;

   /**
    * Get object's properties.
    * 
    * @param repositoryId repository id
    * @param objectId object id
    * @param propertyFilter property filter as string
    * @return object properties
    * @throws FilterNotValidException if <code>propertyFilter</code> is invalid 
    * @throws ObjectNotFoundException if object with specified id
    *            <code>objectId</code> does not exist
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   CmisPropertiesType getProperties(String repositoryId, String objectId, String propertyFilter)
      throws FilterNotValidException, ObjectNotFoundException, RepositoryException;

   /**
    * Get the list of associated Renditions for the specified object.
    * Only rendition attributes are returned, not rendition stream.
    * 
    * @param repositoryId repository id
    * @param objectId object id
    * @param renditionFilter rendition filter as string
    * @param maxItems max items in response
    * @param skipCount skip specified number of objects in response 
    * @return set of renditions
    * @throws FilterNotValidException if <code>renditionFilter</code> is invalid
    * @throws ObjectNotFoundException if object with specified <code>objectId</code>
    *           not found in repository
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   List<CmisRenditionType> getRenditions(String repositoryId, String objectId, String renditionFilter, int maxItems,
      int skipCount) throws FilterNotValidException, ObjectNotFoundException, RepositoryException;

   /**
    *  Moves the specified file-able object from one folder to another.
    *  
    * @param repositoryId repository id
    * @param objectId object id
    * @param targetFolderId target folder for moving object
    * @param sourceFolderId move object from which object to be moved
    * @param includeObjectInfo TODO
    * @return moved object
    * @throws ConstraintException if cmis:objectTypeId property value
    *            of the given object is NOT in the list of AllowedChildObjectTypeIds
    *            of the parent-folder specified by <code>targetFolderId</code>
    * @throws UpdateConflictException if object that is no longer current
    *            (as determined by the repository).
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   // TODO : not clear about UpdateConflictException in this case.
   CmisObject moveObject(String repositoryId, String objectId, String targetFolderId, String sourceFolderId, boolean includeObjectInfo)
      throws ConstraintException, UpdateConflictException, RepositoryException;

   /**
    *  Sets the content stream for the specified Document object.
    *  
    * @param repositoryId repository id
    * @param documentId document id
    * @param content content stream to be applied to object
    * @param changeToken is used for optimistic locking and/or concurrency
    *          checking to ensure that user updates do not conflict
    * @param overwriteFlag if TRUE on object's content stream  exists it
    *          will be overridden. If FALSE and context stream already exists
    *          then ContentAlreadyExistsException will be thrown
    * @param includeObjectInfo TODO
    * @return updated object 
    * @throws ContentAlreadyExistsException if the input parameter overwriteFlag
    *           is FALSE and the Object already has a content-stream.
    * @throws ObjectNotFoundException if document with specified id
    *            <code>documentId</code> does not exist
    * @throws StreamNotSupportedException will be thrown if the contentStreamAllowed
    *           attribute of the object type definition specified by the
    *           cmis:objectTypeId property value of the given document is set
    *           to not allowed
    * @throws UpdateConflictException if update an object that is no longer current.
    *           Repository determine this by using change token
    * @throws IOException if any i/o error occurs when try to set document's
    *           content stream
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   CmisObject setContentStream(String repositoryId, String documentId, ContentStream content, String changeToken,
      boolean overwriteFlag, boolean includeObjectInfo) throws ContentAlreadyExistsException, ObjectNotFoundException,
      StreamNotSupportedException, UpdateConflictException, RepositoryException, IOException;

   /**
    * Update object properties.
    * 
    * @param repositoryId repository id
    * @param objectId object id
    * @param changeToken is used for optimistic locking and/or concurrency
    *           checking to ensure that user updates do not conflict
    * @param properties properties to be applied for object
    * @param includeObjectInfo TODO
    * @return updated object
    * @throws ConstraintException if value of any of the properties violates the
    *            min/max/required/length constraints specified in the property definition
    *            in the object type
    * @throws NameConstraintViolationException if <i>cmis:name</i> specified in properties
    *            throws conflict
    * @throws UpdateConflictException if update an object that is no longer current.
    *            Repository determine this by using change token
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   CmisObject updateProperties(String repositoryId, String objectId, String changeToken,
      CmisPropertiesType properties, boolean includeObjectInfo) throws ConstraintException, NameConstraintViolationException,
      UpdateConflictException, RepositoryException;

}
