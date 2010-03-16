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

import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.object.CmisObject;
import org.xcmis.spi.object.ObjectParent;
import org.xcmis.spi.object.PropertyData;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Connection to CMIS storage.
 * 
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: Connection.java 332 2010-03-11 17:24:56Z andrew00x $
 */
public interface Connection
{
   // ACL Services

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
    * @throws CmisRuntimeException if any others errors occur
    */
   void applyACL(String objectId, List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL,
      AccessControlPropagation propagation) throws ObjectNotFoundException, ConstraintException, CmisRuntimeException;

   /**
    * Get the ACL currently applied to the specified object.
    * 
    * @param objectId identifier of object
    * @param onlyBasicPermissions if <code>true</code> then return only the CMIS
    *        Basic permissions
    * @return actual ACL or <code>null</code> if no ACL applied to object
    * @throws ObjectNotFoundException if <code>objectId</code> or does not
    *         exists
    * @throws CmisRuntimeException if any others errors occur
    */
   List<AccessControlEntry> getACL(String objectId, boolean onlyBasicPermissions) throws ObjectNotFoundException,
      CmisRuntimeException;

   // Query Services

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
    * @param maxItems max items in result
    * @return content changes
    * @throws ConstraintException if the event corresponding to the change log
    *         token provided as an input parameter is no longer available in the
    *         change log. (E.g. because the change log was truncated)
    * @throws FilterNotValidException if <code>propertyFilter</code> has invalid
    *         syntax or contains at least one property name that is not in
    *         object's property definition
    * @throws CmisRuntimeException if any others errors occur
    */
   ItemsList<CmisObject> getContentChanges(ChangeLogTokenHolder changeLogToken, boolean includeProperties,
      String propertyFilter, boolean includePolicyIDs, boolean includeAcl, int maxItems) throws ConstraintException,
      FilterNotValidException, CmisRuntimeException;

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
    * @throws CmisRuntimeException if any others errors occur
    */
   ItemsList<CmisObject> query(String statement, boolean searchAllVersions, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, String renditionFilter, int maxItems, int skipCount)
      throws FilterNotValidException, CmisRuntimeException;

   // Multi-filing Services

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
    * @throws CmisRuntimeException if any others errors occur
    */
   void addObjectToFolder(String objectId, String folderId, boolean allVersions) throws ObjectNotFoundException,
      InvalidArgumentException, ConstraintException, CmisRuntimeException;

   /**
    * Remove an existing fileable non-folder object from a folder.
    * 
    * @param objectId the id of object to be removed
    * @param folderId the folder from which the object is to be removed. If
    *        null, then remove the object from all folders in which it is
    *        currently filed
    * @throws ObjectNotFoundException if <code>objectId</code> or
    *         <code>folderId</code> were not found
    * @throws CmisRuntimeException if any others errors occur
    */
   void removeObjectFromFolder(String objectId, String folderId) throws ObjectNotFoundException, CmisRuntimeException;

   // Navigation Services

   /**
    * Documents that are checked out that the user has access to.
    * 
    * @param folderId folder from which get checked-out documents if null get
    *        all checked-out documents in storage
    * @param includeAllowableActions if <code>true</code> then allowable actions
    *        should be included in response
    * @param includeRelationships indicates what relationships of object must be
    *        returned
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
    * @throws CmisRuntimeException if any others errors occur
    */
   ItemsList<CmisObject> getCheckedOutDocs(String folderId, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, String propertyFilter, String renditionFilter, String orderBy,
      int maxItems, int skipCount) throws ObjectNotFoundException, InvalidArgumentException, FilterNotValidException,
      CmisRuntimeException;

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
    * @throws CmisRuntimeException if any others errors occur
    */
   ItemsList<CmisObject> getChildren(String folderId, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includePathSegments, String propertyFilter,
      String renditionFilter, String orderBy, int maxItems, int skipCount) throws ObjectNotFoundException,
      InvalidArgumentException, FilterNotValidException, CmisRuntimeException;

   /**
    * Get parent for specified folder. This method MUST NOT be used for getting
    * parents of other fileable objects.
    * 
    * @param folderId folder id
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
    * @throws CmisRuntimeException if any others errors occur
    */
   CmisObject getFolderParent(String folderId, String propertyFilter) throws ObjectNotFoundException,
      InvalidArgumentException, FilterNotValidException, CmisRuntimeException;

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
    *         folder.
    * @throws ObjectNotFoundException if object with <code>objectId</code> was
    *         not found
    * @throws ConstraintException if this method is invoked on an not fileable
    *         object
    * @throws FilterNotValidException if <code>propertyFilter</code> has invalid
    *         syntax or contains at least one property name that is not in
    *         object's property definition or <code>renditionFilter</code> has
    *         invalid syntax or contains at least one unknown rendition
    * @throws CmisRuntimeException if any others errors occur
    */
   List<ObjectParent> getObjectParents(String objectId, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includeRelativePathSegment, String propertyFilter,
      String renditionFilter) throws ObjectNotFoundException, ConstraintException, FilterNotValidException,
      CmisRuntimeException;

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
    * @throws CmisRuntimeException if any others errors occur
    */
   List<ItemsTree<CmisObject>> getDescendants(String folderId, int depth, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includePathSegments, String propertyFilter,
      String renditionFilter) throws ObjectNotFoundException, InvalidArgumentException, FilterNotValidException,
      CmisRuntimeException;

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
    * @throws CmisRuntimeException if any others errors occur
    */
   List<ItemsTree<CmisObject>> getFolderTree(String folderId, int depth, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includePathSegments, String propertyFilter,
      String renditionFilter) throws ObjectNotFoundException, InvalidArgumentException, FilterNotValidException,
      CmisRuntimeException;

   // Object Services

   /**
    * Create a document object.
    * 
    * @param folderId parent folder id for object. May be null if storage
    *        supports unfiling
    * @param propertyData properties that MAY be applied to newly created document
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
    * @return newly created document
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
    * @throws CmisRuntimeException if any others errors occur
    */
   CmisObject createDocument(String folderId, PropertyData propertyData, ContentStream content,
      List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL, List<String> policies,
      VersioningState versioningState) throws ObjectNotFoundException, ConstraintException, InvalidArgumentException,
      StreamNotSupportedException, NameConstraintViolationException, IOException, StorageException,
      CmisRuntimeException;

   /**
    * Create a document object as a copy of the given source document in the
    * <code>folderId</code>.
    * 
    * @param sourceId id for the source document
    * @param folderId parent folder id for object. May be null if storage
    *        supports unfiling
    * @param propertyData properties that MAY be applied to newly created document
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
    * @return newly created document
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
    * @throws CmisRuntimeException if any others errors occur
    */
   CmisObject createDocumentFromSource(String sourceId, String folderId, PropertyData propertyData,
      List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL, List<String> policies,
      VersioningState versioningState) throws ObjectNotFoundException, ConstraintException, InvalidArgumentException,
      NameConstraintViolationException, StorageException, CmisRuntimeException;

   /**
    * Create a folder object.
    * 
    * @param folderId parent folder id for new folder
    * @param propertyData properties that MAY be applied to newly created folder
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
    * @return newly created folder
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
    * @throws CmisRuntimeException if any others errors occur
    */
   CmisObject createFolder(String folderId, PropertyData propertyData, List<AccessControlEntry> addACL,
      List<AccessControlEntry> removeACL, List<String> policies) throws ObjectNotFoundException, ConstraintException,
      InvalidArgumentException, NameConstraintViolationException, StorageException, CmisRuntimeException;

   /**
    * Create a policy object.
    * 
    * @param folderId parent folder id may be null if policy object type is not
    *        fileable
    * @param propertyData properties to be applied to newly created Policy
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
    * @return newly created policy
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
    * @throws CmisRuntimeException if any others errors occur
    */
   CmisObject createPolicy(String folderId, PropertyData propertyData, List<AccessControlEntry> addACL,
      List<AccessControlEntry> removeACL, List<String> policies) throws ObjectNotFoundException, ConstraintException,
      InvalidArgumentException, NameConstraintViolationException, StorageException, CmisRuntimeException;

   /**
    * Create a relationship object.
    * 
    * @param propertyData properties to be applied to newly created relationship
    * @param addACL set Access Control Entry to be applied for newly created
    *        relationship
    * @param removeACL set Access Control Entry that MUST be removed from the
    *        newly created relationship
    * @param policies list of policy id that MUST be applied to the newly
    *        created relationship.
    * @return newly created relationship
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
    * @throws CmisRuntimeException if any others errors occur
    */
   CmisObject createRelationship(PropertyData propertyData, List<AccessControlEntry> addACL,
      List<AccessControlEntry> removeACL, List<String> policies) throws ObjectNotFoundException, ConstraintException,
      NameConstraintViolationException, StorageException, CmisRuntimeException;

   /**
    * Delete the content stream for the specified Document object.
    * 
    * @param documentId document id
    * @param changeToken is used for optimistic locking and/or concurrency
    *        checking to ensure that user updates do not conflict
    * @throws ObjectNotFoundException if document with specified id
    *         <code>documentId</code> does not exist
    * @throws ConstraintException if object's type definition
    *         <i>contentStreamAllowed</i> attribute is set to <i>required</i>
    * @throws UpdateConflictException if update an object that is no longer
    *         current. Storage determine this by using change token
    * @throws StorageException if content of document can not be removed cause
    *         to storage internal problem
    * @throws CmisRuntimeException if any others errors occur
    */
   void deleteContentStream(String documentId, String changeToken) throws ObjectNotFoundException, ConstraintException,
      UpdateConflictException, StorageException, CmisRuntimeException;

   /**
    * Delete the specified object.
    * 
    * @param objectId document id
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
    * @throws CmisRuntimeException if any others errors occur
    */
   void deleteObject(String objectId, Boolean deleteAllVersions) throws ObjectNotFoundException, ConstraintException,
      UpdateConflictException, StorageException, CmisRuntimeException;

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
    * @throws CmisRuntimeException if any others errors occur
    */
   Collection<String> deleteTree(String folderId, Boolean deleteAllVersions, UnfileObject unfileObject,
      Boolean continueOnFailure) throws ObjectNotFoundException, UpdateConflictException, CmisRuntimeException;

   /**
    * Get the list of allowable actions for an Object.
    * 
    * @param objectId object id
    * @return allowable actions for object
    * @throws ObjectNotFoundException if object with specified id
    *         <code>objectId</code> does not exist
    * @throws CmisRuntimeException if any others errors occur
    */
   AllowableActions getAllowableActions(String objectId) throws ObjectNotFoundException, CmisRuntimeException;

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
    * @throws CmisRuntimeException if any others errors occur
    */
   ContentStream getContentStream(String objectId, String streamId, long offset, long length)
      throws ObjectNotFoundException, ConstraintException, CmisRuntimeException;

   /**
    * Get object.
    * 
    * @param objectId object id
    * @param includeAllowableActions if <code>true</code> then include object
    *        allowable actions for object
    * @param includeRelationships include object relationships
    * @param includePolicyIDs include policies applied to object
    * @param includeAcl include object's ACL
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
    * @throws CmisRuntimeException if any others errors occur
    */
   CmisObject getObject(String objectId, boolean includeAllowableActions, IncludeRelationships includeRelationships,
      boolean includePolicyIDs, boolean includeAcl, String propertyFilter, String renditionFilter)
      throws ObjectNotFoundException, FilterNotValidException, CmisRuntimeException;

   /**
    * Get object by specified path.
    * 
    * @param path object's path
    * @param includeAllowableActions <code>true</code> if allowable actions
    *        should be included in response <code>false</code> otherwise
    * @param includeRelationships include object's relationship
    * @param includePolicyIDs include policies IDs applied to object
    * @param includeAcl include ACL
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
    * @throws CmisRuntimeException if any others errors occur
    */
   CmisObject getObjectByPath(String path, boolean includeAllowableActions, IncludeRelationships includeRelationships,
      boolean includePolicyIDs, boolean includeAcl, String propertyFilter, String renditionFilter)
      throws ObjectNotFoundException, FilterNotValidException, CmisRuntimeException;

   /**
    * Get object's properties.
    * 
    * @param objectId object id
    * @param propertyFilter comma-delimited list of property definition Query
    *        Names. A wildcard '*' is supported and minds return all properties.
    *        If empty string or <code>null</code> provided than storage MAY
    *        return storage specific set of properties
    * @return object's properties
    * @throws ObjectNotFoundException if object with specified id
    *         <code>objectId</code> does not exist
    * @throws FilterNotValidException if <code>propertyFilter</code> has invalid
    *         syntax or contains at least one property name that is not in
    *         object's property definition
    * @throws CmisRuntimeException if any others errors occur
    */
   PropertyData getProperties(String objectId, String propertyFilter) throws ObjectNotFoundException,
      FilterNotValidException, CmisRuntimeException;

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
    * @throws CmisRuntimeException if any others errors occur
    */
   List<Rendition> getRenditions(String objectId, String renditionFilter, int maxItems, int skipCount)
      throws ObjectNotFoundException, FilterNotValidException, CmisRuntimeException;

   /**
    * Moves the specified file-able object from one folder to another.
    * 
    * @param objectId object id
    * @param targetFolderId target folder for moving object
    * @param sourceFolderId move object from which object to be moved
    * @return moved object
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
    *         determined by the storage).
    * @throws StorageException if object can not be moved (save changes) cause
    *         to storage internal problem
    * @throws CmisRuntimeException if any others errors occur
    */
   CmisObject moveObject(String objectId, String targetFolderId, String sourceFolderId) throws ObjectNotFoundException,
      ConstraintException, InvalidArgumentException, UpdateConflictException, StorageException, CmisRuntimeException;

   /**
    * Sets the content stream for the specified Document object.
    * 
    * @param documentId document id
    * @param content content stream to be applied to object
    * @param changeToken is used for optimistic locking and/or concurrency
    *        checking to ensure that user updates do not conflict
    * @param overwriteFlag if <code>true</code> on object's content stream
    *        exists it will be overridden. If <code>false</code> and context
    *        stream already exists then ContentAlreadyExistsException will be
    *        thrown
    * @return updated object
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
    * @throws IOException if any i/o error occurs when try to set document's
    *         content stream
    * @throws StorageException if object's content stream can not be updated
    *         (save changes) cause to storage internal problem
    * @throws CmisRuntimeException if any others errors occur
    */
   void setContentStream(String documentId, ContentStream content, String changeToken, boolean overwriteFlag)
      throws ObjectNotFoundException, ContentAlreadyExistsException, StreamNotSupportedException,
      UpdateConflictException, IOException, StorageException, CmisRuntimeException;

   /**
    * Update object properties.
    * 
    * @param objectId object id
    * @param changeToken is used for optimistic locking and/or concurrency
    *        checking to ensure that user updates do not conflict
    * @param propertyData properties to be applied for object
    * @return updated object
    * @throws ObjectNotFoundException if document with specified id
    *         <code>objectId</code> does not exist
    * @throws ConstraintException if value of any of the properties violates the
    *         min/max/required/length constraints specified in the property
    *         definition in the object type
    * @throws NameConstraintViolationException if <i>cmis:name</i> specified in
    *         properties throws conflict
    * @throws UpdateConflictException if update an object that is no longer
    *         current. Storage determine this by using change token
    * @throws StorageException if object's properties can not be updated (save
    *         changes) cause to storage internal problem
    * @throws CmisRuntimeException if any others errors occur
    */
   CmisObject updateProperties(String objectId, String changeToken, PropertyData propertyData)
      throws ObjectNotFoundException, ConstraintException, NameConstraintViolationException, UpdateConflictException,
      StorageException, CmisRuntimeException;

   // ---

   /**
    * Applies a specified policy to an object.
    * 
    * @param policyId the policy to be applied to object
    * @param objectId target object for policy
    * @throws ObjectNotFoundException if object with <code>objectId</code> or
    *         <code>policyId</code> does not exist
    * @throws ConstraintException if object with id <code>objectId</code> is not
    *         controllable by policy
    * @throws CmisRuntimeException if any others errors occur
    */
   void applyPolicy(String policyId, String objectId) throws ConstraintException, ObjectNotFoundException,
      CmisRuntimeException;

   /**
    * Gets the list of policies currently applied to the specified object.
    * 
    * @param objectId the object id
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
    * @throws CmisRuntimeException if any others errors occur
    */
   List<CmisObject> getAppliedPolicies(String objectId, String propertyFilter) throws ObjectNotFoundException,
      FilterNotValidException, CmisRuntimeException;

   /**
    * Removes a specified policy from an object.
    * 
    * @param policyId id of policy to be removed from object
    * @param objectId id of object
    * @throws ObjectNotFoundException if object with <code>objectId</code> does
    *         not exist
    * @throws ConstraintException if object with id <code>objectId</code> is not
    *         controllable by policy
    * @throws CmisRuntimeException if any others errors occur
    */
   void removePolicy(String policyId, String objectId) throws ConstraintException, ObjectNotFoundException,
      CmisRuntimeException;

   // ---

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
    * @param propertyFilter property filter as string
    * @param maxItems max items in result
    * @param skipCount skip items
    * @return object's relationships
    * @throws ObjectNotFoundException if object with <code>objectId</code> does
    *         not exist
    * @throws FilterNotValidException if <code>propertyFilter</code> has invalid
    *         syntax or contains at least one property name that is not in
    *         object's property definition
    * @throws CmisRuntimeException if any others errors occur
    */
   ItemsList<CmisObject> getObjectRelationships(String objectId, RelationshipDirection direction, String typeId,
      boolean includeSubRelationshipTypes, boolean includeAllowableActions, String propertyFilter, int maxItems,
      int skipCount) throws FilterNotValidException, ObjectNotFoundException, CmisRuntimeException;

   // Versioning Service

   /**
    * Check-out document.
    * 
    * @param documentId document id. Storage MAY allow checked-out ONLY latest
    *        version of Document
    * @return checked-out document (PWC)
    * @throws ConstraintException if the object is not versionable
    * @throws UpdateConflictException if update an object that is no longer
    *         current
    * @throws VersioningException if object is a non-current document version
    * @throws StorageException if newly created PWC can't be saved in storage
    *         cause to storage internal problem
    * @throws CmisRuntimeException if any others errors occur
    */
   CmisObject checkout(String documentId) throws ConstraintException, UpdateConflictException, VersioningException,
      StorageException, CmisRuntimeException;

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
    * @throws CmisRuntimeException if any others errors occur
    */
   void cancelCheckout(String documentId) throws ConstraintException, UpdateConflictException, VersioningException,
      StorageException, CmisRuntimeException;

   /**
    * Check-in Private Working Copy.
    * 
    * @param documentId document id
    * @param major <code>true</code> is new version should be marked as major
    *        <code>false</code> otherwise
    * @param propertyData properties to be applied to new version
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
    * @return checked-in document
    * @throws ConstraintException if the object is not versionable
    * @throws UpdateConflictException if update an object that is no longer
    *         current
    * @throws StreamNotSupportedException if document does not supports content
    *         stream
    * @throws IOException if any i/o error occurs when try to set document's
    *         content stream
    * @throws StorageException if newly version of Document can't be saved in
    *         storage cause to its internal problem
    * @throws CmisRuntimeException if any others errors occur
    */
   CmisObject checkin(String documentId, boolean major, PropertyData propertyData, ContentStream content,
      String checkinComment, List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL, List<String> policies)
      throws ConstraintException, UpdateConflictException, StreamNotSupportedException, IOException, StorageException;

   /**
    * Get all documents in version series.
    * 
    * @param versionSeriesId version series id
    * @param includeAllowableActions <code>true</code> if allowable actions
    *        should be included in response <code>false</code> otherwise
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
    * @throws CmisRuntimeException if any others errors occur
    */
   List<CmisObject> getAllVersions(String versionSeriesId, boolean includeAllowableActions, String propertyFilter)
      throws ObjectNotFoundException, FilterNotValidException, CmisRuntimeException;

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
    * @throws CmisRuntimeException if any others errors occur
    */
   CmisObject getObjectOfLatestVersion(String versionSeriesId, boolean major, boolean includeAllowableActions,
      IncludeRelationships includeRelationships, boolean includePolicyIDs, boolean includeAcl, String propertyFilter,
      String renditionFilter) throws ObjectNotFoundException, FilterNotValidException, CmisRuntimeException;

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
    * @param propertyFilter comma-delimited list of property definition Query
    *        Names. A wildcard '*' is supported and minds return all properties.
    *        If empty string or <code>null</code> provided than storage MAY
    *        return storage specific set of properties
    * @return properties of latest version of object in version series
    * @throws ObjectNotFoundException if Version Series with id
    *         <code>versionSeriesId</code> does not exists or the input
    *         parameter <code>major</code> is <code>true</code> and the Version
    *         Series contains no major versions.
    * @throws FilterNotValidException if <code>propertyFilter</code> has invalid
    *         syntax or contains at least one property name that is not in
    *         object's property definition
    * @throws CmisRuntimeException if any others errors occur
    */
   PropertyData getPropertiesOfLatestVersion(String versionSeriesId, boolean major, String propertyFilter)
      throws FilterNotValidException, ObjectNotFoundException, CmisRuntimeException;

   // Type Managment

   /**
    * Add new type.
    * 
    * @param type type definition
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
    * @throws CmisRuntimeException if any others errors occur
    */
   void addType(TypeDefinition type) throws StorageException, CmisRuntimeException;

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
    * @throws CmisRuntimeException if any others errors occur
    */
   ItemsList<TypeDefinition> getTypeChildren(String typeId, boolean includePropertyDefinition, int maxItems,
      int skipCount) throws TypeNotFoundException, CmisRuntimeException;

   /**
    * Get type definition for type <code>typeId</code> include property
    * definition, see {@link #getTypeDefinition(String, boolean)}
    * 
    * @param typeId type Id
    * @return type definition
    * @throws TypeNotFoundException if type <code>typeId</code> does not exist
    * @throws CmisRuntimeException if any others errors occur
    */
   TypeDefinition getTypeDefinition(String typeId) throws TypeNotFoundException, CmisRuntimeException;

   /**
    * Get type definition for type <code>typeId</code>.
    * 
    * @param typeId type Id
    * @param includePropertyDefinition if <code>true</code> property definition
    *        should be included
    * @return type definition
    * @throws TypeNotFoundException if type <code>typeId</code> does not exist
    * @throws CmisRuntimeException if any others errors occur
    */
   TypeDefinition getTypeDefinition(String typeId, boolean includePropertyDefinition) throws TypeNotFoundException,
      CmisRuntimeException;

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
    * @throws CmisRuntimeException if any others errors occur
    */
   List<ItemsTree<TypeDefinition>> getTypeDescendants(String typeId, int depth, boolean includePropertyDefinition)
      throws TypeNotFoundException, CmisRuntimeException;

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
    * @throws CmisRuntimeException if any others errors occur
    */
   void removeType(String typeId) throws TypeNotFoundException, ConstraintException, StorageException,
      CmisRuntimeException;

   //---------
   /**
    * Gets the storage associated to this connection.
    * 
    * @return storage
    */
   Storage getStorage();

   /**
    * Close this connection.
    */
   void close();

}
