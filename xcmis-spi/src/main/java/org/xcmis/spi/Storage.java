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

import org.xcmis.spi.data.DocumentData;
import org.xcmis.spi.data.FolderData;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.data.PolicyData;
import org.xcmis.spi.data.RelationshipData;
import org.xcmis.spi.query.Query;
import org.xcmis.spi.query.Result;

import java.util.Collection;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: Storage.java 332 2010-03-11 17:24:56Z andrew00x $
 */
public interface Storage extends org.xcmis.spi.TypeManager
{

   String getId();

   //
   AllowableActions calculateAllowableActions(ObjectData object) throws CmisRuntimeException;

   //

   ItemsIterator<ObjectData> getCheckedOutDocuments(ObjectData folder, String orderBy) throws CmisRuntimeException;

   //
   DocumentData createDocument(FolderData folder, String typeId, VersioningState versioningState);

   DocumentData createCopyOfDocument(DocumentData source, FolderData folder, VersioningState versioningState);

   FolderData createFolder(FolderData folder, String typeId);

   PolicyData createPolicy(FolderData folder, String typeId);

   RelationshipData createRelationship(ObjectData source, ObjectData target, String typeId);

   //
   void deleteObject(ObjectData object, boolean deleteAllVersions) throws UpdateConflictException, StorageException,
      CmisRuntimeException;

   Collection<String> deleteTree(FolderData folder, boolean deleteAllVersions, UnfileObject unfileObject,
      boolean continueOnFailure) throws UpdateConflictException, CmisRuntimeException;

   //
   ItemsIterator<ChangeEvent> getChangeLog(String changeLogToken) throws ConstraintException, CmisRuntimeException;

   /**
    * Handle specified SQL query.
    * 
    * @param query SQL query
    * @return set of query results
    * @throws InvalidArgumentException if specified <code>query</code> is
    *         invalid
    * @throws CmisRuntimeException if any other CMIS repository errors
    */
   ItemsIterator<Result> query(Query query) throws InvalidArgumentException, CmisRuntimeException;

   //
   ObjectData getObject(String objectId) throws CmisRuntimeException;

   ObjectData getObjectByPath(String path) throws CmisRuntimeException;

   ObjectData moveObject(ObjectData object, FolderData target, FolderData source) throws UpdateConflictException,
      StorageException, CmisRuntimeException;

   //

   ItemsIterator<Rendition> getRenditions(ObjectData object) throws CmisRuntimeException;

   //
   void saveObject(ObjectData object) throws StorageException, NameConstraintViolationException,
      UpdateConflictException;

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
    *         <code>versionSeriesId</code> does not exists
    */
   Collection<DocumentData> getAllVersions(String versionSeriesId) throws ObjectNotFoundException;

}
