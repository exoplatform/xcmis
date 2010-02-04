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

import org.xcmis.core.CmisRepositoryInfoType;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.EnumVersioningState;
import org.xcmis.messaging.CmisTypeContainer;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.ItemsIterator;
import org.xcmis.spi.object.RenditionManager;
import org.xcmis.spi.object.VersionSeries;
import org.xcmis.spi.query.QueryHandler;

import java.util.List;

/**
 * CMIS repository.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface Repository extends TypeManager
{

   /**
    * Move object with specified id <code>objectId</code> to new folder
    * <code>destinationFolderId</code>.
    * 
    * @param objectId the object to be moved
    * @param destinationFolderId the destination folder id
    * @param versioningState the versioning state for new copied object. If object
    *           type is not versionable this parameter has not any effect for new 
    *           object.
    * @return new copied object
    * @throws ObjectNotFoundException if object or destination folder does not
    *           exist in repository
    * @throws ConstraintException if destination folder is not supported
    *           object type to be moved
    * @throws RepositoryException if any other CMIS repository errors occurs
    */
   Entry copyObject(String objectId, String destinationFolderId, EnumVersioningState versioningState)
      throws ConstraintException, ObjectNotFoundException, RepositoryException;

   /**
    * Create object in unfiling state.
    * 
    * @param type the object type
    * @param versioningState the versioning state for newly created object. If object
    *           type is not versionable this parameter has not any effect for new
    *           object.
    * @return newly created object
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   Entry createObject(CmisTypeDefinitionType type, EnumVersioningState versioningState) throws RepositoryException;

   /**
    * Get change token matcher.
    * 
    * @return change token matcher
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   ChangeTokenMatcher getChangeTokenMatcher() throws RepositoryException;

   /**
    * Gets the set of documents that are checked out that the user has access to.
    * 
    * @param folderId the folder in the repository from which documents should be
    *           returned. If this parameter is <code>null</code> that get all
    *           checked out documents in repository.
    * @return documents that are checked out
    * @throws ObjectNotFoundException if folder with id <code>folderId</code>
    *            does not exists
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   ItemsIterator<Entry> getCheckedOutDocuments(String folderId) throws ObjectNotFoundException, RepositoryException;

   /**
    * Get repository Id.
    * 
    * @return repository id
    */
   String getId();

   /**
    * Get object with specified Id.
    * 
    * @param objectId the object id
    * @return CMIS object that has specified id
    * @throws ObjectNotFoundException if object with supplied <code>objectId</code>
    *            does not exists
    * @throws RepositoryException if any other CMIS repository errors occurs
    */
   Entry getObjectById(String objectId) throws ObjectNotFoundException, RepositoryException;

   /**
    * Get object with specified path.
    * 
    * @param path the path
    * @return CMIS object that has specified id
    * @throws ObjectNotFoundException if object with supplied <code>path</code>
    *            does not exists
    * @throws RepositoryException if any other CMIS repository errors occurs
    */
   Entry getObjectByPath(String path) throws ObjectNotFoundException, RepositoryException;

   /**
    * Get SQL query handler.
    * 
    * @return query handler
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   QueryHandler getQueryHandler() throws RepositoryException;

   /**
    * Get rendition manager.
    * 
    * @return rendition manager
    * @throws RepositoryException if any CMIS repository errors occurs
    * @see RenditionManager
    */
   RenditionManager getRenditionManager() throws RepositoryException;

   /**
    * Information about repository.
    * 
    * @return CmisRepositoryInfoType
    * @see CmisRepositoryInfoType
    */
   CmisRepositoryInfoType getRepositoryInfo();

   /**
    * Get root folder of repository.
    * 
    * @return root folder
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   Entry getRootFolder() throws RepositoryException;

   /**
    * Iterator of object types in the repository.
    * 
    * @param typeId the type id, if not null then return only specified Object Type and its
    *          descendant
    * @param includePropertyDefinition true if property definition should be included false otherwise
    * @return list of all types in the repository or specified object type and
    *         its direct children
    * @throws TypeNotFoundException if type <code>typeId</code> does not exist
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   ItemsIterator<CmisTypeDefinitionType> getTypeChildren(String typeId, boolean includePropertyDefinition)
      throws TypeNotFoundException, RepositoryException;

   /**
    * Get all descendants of specified <code>typeId</code> in hierarchy.
    * If <code>typeId</code> is <code>null</code> then return all types
    * and ignore the value of the <code>depth</code> parameter.
    *
    * @param typeId the type id
    * @param depth the depth of level in hierarchy
    * @param includePropertyDefinition true if property definition should be included false otherwise
    * @return list of descendant types
    * @throws TypeNotFoundException if type <code>typeId</code> does not exist
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   List<CmisTypeContainer> getTypeDescendants(String typeId, int depth, boolean includePropertyDefinition)
      throws TypeNotFoundException, RepositoryException;

   /**
    * Get VersionSeries of document with specified id.
    * 
    * @param versionSeriesId the id of Version Series
    * @return version series
    * @throws ObjectNotFoundException if version series with
    *            <code>versionSeriesId</code> does not exists
    * @throws RepositoryException any repository errors
    */
   VersionSeries getVersionSeries(String versionSeriesId) throws ObjectNotFoundException, RepositoryException;

   /**
   * Move object with specified id <code>objectId</code> to new folder
   * <code>destinationFolderId</code>.
   * 
   * @param objectId the object to be moved
   * @param destinationFolderId the destination folder id
   * @param sourceFolderId the source folder id
   * @throws ObjectNotFoundException if object or destination folder does not
   *           exist in repository
   * @throws ConstraintException if destination folder is not supported
   *           object type to be moved
   * @throws RepositoryException if any other CMIS repository errors occurs
   */
   void moveObject(String objectId, String destinationFolderId, String sourceFolderId)
      throws ConstraintException, ObjectNotFoundException, RepositoryException;

}
