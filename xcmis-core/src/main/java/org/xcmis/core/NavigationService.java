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

import org.xcmis.core.CmisObjectType;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.messaging.CmisObjectInFolderContainerType;
import org.xcmis.messaging.CmisObjectInFolderListType;
import org.xcmis.messaging.CmisObjectListType;
import org.xcmis.messaging.CmisObjectParentsType;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.RepositoryException;

import java.util.List;

/**
 * Used to browsing the folder hierarchy in a CMIS Repository, and to locate
 * Documents that are checked out.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface NavigationService
{

   /**
    * Get the list of documents that are checked out that the user has access to.
    * 
    * @param repositoryId repository id
    * @param folderId folder from which get checked-out documents if null get all
    *           checked-out documents in repository
    * @param includeAllowableActions if TRUE then allowable actions should be
    *           included in response
    * @param includeRelationships indicates what relationships of object must be
    *           returned
    * @param propertyFilter property filter as string
    * @param renditionFilter rendition filter as string
    * @param orderBy order by
    * @param maxItems number of max items in result set
    * @param skipCount skip items
    * @return set of checked-out documents
    * @throws FilterNotValidException if <code>propertyFilter</code> or
    *            <code>renditionFilter</code> is invalid
    * @throws RepositoryException if any other errors in repository occur
    */
   CmisObjectListType getCheckedOutDocs(String repositoryId, String folderId, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, String propertyFilter, String renditionFilter, String orderBy,
      int maxItems, int skipCount) throws FilterNotValidException, RepositoryException;

   /**
    * Get the list of child objects contained in the specified folder.
    * 
    * @param repositoryId repository id
    * @param folderId folder id
    * @param includeAllowableActions if TRUE then allowable actions for each
    *           should be included in response
    * @param includeRelationships indicates what relationships of object must be
    *           returned
    * @param includePathSegments  if TRUE then returns a PathSegment for each
    *           child object
    * @param propertyFilter property filter as string
    * @param renditionFilter rendition filter as string
    * @param orderBy order by
    * @param maxItems number of max items in result set
    * @param skipCount skip items
    * @return set of folder's children
    * @throws FilterNotValidException if <code>propertyFilter</code> or
    *            <code>renditionFilter</code> is invalid
    * @throws InvalidArgumentException if object with id <code>folderId</code>
    *            is not a folder
    * @throws RepositoryException if any other errors in repository occur
    */
   CmisObjectInFolderListType getChildren(String repositoryId, String folderId, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, boolean includePathSegments, String propertyFilter,
      String renditionFilter, String orderBy, int maxItems, int skipCount) throws FilterNotValidException,
      InvalidArgumentException, RepositoryException;

   /**
    * Get the collection of descendant objects contained in the specified folder
    * and any of its child-folders.

    * @param repositoryId repository id
    * @param folderId folder id
    * @param depth depth for discover descendants if -1 then discovery descendants
    *           at all levels
    * @param includeAllowableActions if TRUE then allowable actions for each
    *           object should be included in response
    * @param includeRelationships indicates what relationships of object must be
    *           returned
    * @param includePathSegments if TRUE then returns a PathSegment for each child object
    * @param propertyFilter property filter as string
    * @param renditionFilter rendition filter as string
    * @return set of folder's descendants
    * @throws FilterNotValidException if <code>propertyFilter</code> or
    *            <code>renditionFilter</code> is invalid
    * @throws InvalidArgumentException if any of the following conditions are met:
    *            <ul>
    *            <li>if object with id <code>folderId</code> is not a folder</li>
    *            <li>id <code>depth</code> is invalid, e.g. if depth==0</li>
    *            </ul>
    * @throws RepositoryException if any other errors in repository occur
    */
   List<CmisObjectInFolderContainerType> getDescendants(String repositoryId, String folderId, int depth,
      boolean includeAllowableActions, EnumIncludeRelationships includeRelationships, boolean includePathSegments,
      String propertyFilter, String renditionFilter) throws FilterNotValidException, InvalidArgumentException,
      RepositoryException;

   /**
    * Get parent for specified folder.
    * 
    * @param repositoryId repository id
    * @param folderId folder id
    * @param propertyFilter property filter as string
    * @return folder's parent
    * @throws FilterNotValidException if <code>propertyFilter</code> is invalid
    * @throws InvalidArgumentException if the <code>folderId</code> is id of the
    *            root folder
    * @throws RepositoryException if any other errors in repository occur
    */
   CmisObjectType getFolderParent(String repositoryId, String folderId, String propertyFilter)
      throws FilterNotValidException, InvalidArgumentException, RepositoryException;

   /**
    * Get the set of descendant folder objects contained in the specified folder.
    * 
    * @param repositoryId repository id
    * @param folderId folder id
    * @param depth depth for discover descendants if -1 then discovery descendants
    *          at all levels
    * @param includeAllowableActions if TRUE then allowable actions for each
    *          object should be included in response
    * @param includeRelationships indicates what relationships of object must be
    *           returned
    * @param includePathSegments  if TRUE then returns a PathSegment for each child object
    * @param propertyFilter property filter as string
    * @param renditionFilter rendition filter as string
    * @return hierarchical set of folders
    * @throws FilterNotValidException if <code>propertyFilter</code> or
    *            <code>renditionFilter</code> is invalid
    * @throws InvalidArgumentException if any of the following conditions are met:
    *            <ul>
    *            <li>if object with id <code>folderId</code> is not a folder</li>
    *            <li>id <code>depth</code> is invalid, e.g. if depth==0</li>
    *            </ul>
    * @throws RepositoryException if any other errors in repository occur
    */
   List<CmisObjectInFolderContainerType> getFolderTree(String repositoryId, String folderId, int depth,
      boolean includeAllowableActions, EnumIncludeRelationships includeRelationships, boolean includePathSegments,
      String propertyFilter, String renditionFilter) throws FilterNotValidException, InvalidArgumentException,
      RepositoryException;

   /**
    * Gets the parent folder(s) for the specified non-folder object.
    * 
    * @param repositoryId repository id
    * @param objectId object id
    * @param includeAllowableActions if TRUE then allowable actions should be
    *          included in response
    * @param includeRelationships indicates what relationships of object must be
    *           returned
    * @param includeRelativePathSegment  if TRUE, returns a PathSegment for each child object
    * @param propertyFilter property filter as string
    * @param renditionFilter rendition filter as string
    * @return object's parents
    * @throws FilterNotValidException if <code>propertyFilter</code> is invalid
    * @throws InvalidArgumentException if object with id <code>objectId</code> is a folder.
    *            For getting parent of folder method {@link #getFolderParent(String, String, String)}
    *            must be used.
    * @throws RepositoryException if any other errors in repository occur
    */
   List<CmisObjectParentsType> getObjectParents(String repositoryId, String objectId, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, boolean includeRelativePathSegment, String propertyFilter,
      String renditionFilter) throws FilterNotValidException, InvalidArgumentException, RepositoryException;

}
