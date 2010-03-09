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

import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.CmisAllowableActionsType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisRenditionType;
import org.xcmis.core.CmisRepositoryInfoType;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.EnumUnfileObject;
import org.xcmis.core.EnumVersioningState;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.query.Query;
import org.xcmis.spi.query.Result;

import java.io.IOException;
import java.util.Collection;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface Storage extends org.xcmis.spi.impl.TypeManager
{

   CmisAllowableActionsType calculateAllowableActions(ObjectData object) throws CmisRuntimeException;

   //
   void cancelCheckout(String versionSeriesId) throws StorageException, CmisRuntimeException;

   ObjectData checkin(ObjectData document, boolean major, CmisPropertiesType properties, ContentStream content,
      String checkinComment, CmisAccessControlListType addACL, CmisAccessControlListType removeACL,
      Collection<String> policies) throws IOException, StorageException, CmisRuntimeException;

   ObjectData checkout(ObjectData document) throws VersioningException, StorageException, CmisRuntimeException;

   Collection<ObjectData> getVersions(String versionSeriesId) throws ObjectNotFoundException;

   ItemsIterator<ObjectData> getCheckedOutDocuments(ObjectData folder, String orderBy) throws CmisRuntimeException;

   //
   ObjectData createDocument(ObjectData folder, CmisTypeDefinitionType typeDefinition, CmisPropertiesType properties,
      ContentStream content, CmisAccessControlListType addAcl, CmisAccessControlListType removeACEs,
      Collection<String> policies, EnumVersioningState versioningState) throws StorageException,
      NameConstraintViolationException, CmisRuntimeException;

   ObjectData createDocumentFromSource(ObjectData source, ObjectData folder, CmisPropertiesType properties,
      CmisAccessControlListType addAcl, CmisAccessControlListType removeAcl, Collection<String> policies,
      EnumVersioningState versioningState) throws StorageException, NameConstraintViolationException,
      CmisRuntimeException;

   ObjectData createFolder(ObjectData folder, CmisTypeDefinitionType typeDefinition, CmisPropertiesType properties,
      CmisAccessControlListType addAcl, CmisAccessControlListType removeAcl, Collection<String> policies)
      throws StorageException, NameConstraintViolationException, CmisRuntimeException;

   ObjectData createPolicy(ObjectData folder, CmisTypeDefinitionType typeDefinition, CmisPropertiesType properties,
      CmisAccessControlListType addAcl, CmisAccessControlListType removeAcl, Collection<String> policies)
      throws StorageException, NameConstraintViolationException, CmisRuntimeException;

   ObjectData createRelationship(CmisTypeDefinitionType typeDefinition, ObjectData source, ObjectData target,
      CmisPropertiesType properties, CmisAccessControlListType addAcl, CmisAccessControlListType removeAcl,
      Collection<String> policies) throws StorageException, NameConstraintViolationException, CmisRuntimeException;

   //
   void deleteObject(ObjectData object, boolean deleteAllVersion) throws UpdateConflictException, StorageException,
      CmisRuntimeException;

   Collection<ObjectData> deleteTree(ObjectData folder, boolean deleteAllVersions, EnumUnfileObject unfileObject,
      boolean continueOnFailure) throws UpdateConflictException, StorageException, CmisRuntimeException;

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

   ItemsIterator<ObjectData> getChildren(ObjectData folder, String orderBy) throws CmisRuntimeException;

   //
   void addObjectToFolder(ObjectData object, ObjectData folder, boolean allVersions) throws CmisRuntimeException;

   void removeObjectFromFolder(ObjectData object, ObjectData folder) throws CmisRuntimeException;

   void moveObject(ObjectData object, ObjectData target, ObjectData source) throws UpdateConflictException,
      StorageException, CmisRuntimeException;

   //
   ContentStream getContentStream(ObjectData objectData, String streamId, long offset, long length);

   void setContentStream(ObjectData document, ContentStream content) throws IOException, StorageException,
      CmisRuntimeException;

   void deleteContentStream(ObjectData document) throws StorageException, CmisRuntimeException;
   
   ItemsIterator<CmisRenditionType> getRenditions(ObjectData object) throws CmisRuntimeException;

   boolean hasContent(ObjectData documentData);

   //
   void saveObject(ObjectData object) throws StorageException;

   /**
    * Get description of storage and its capabilities.
    * 
    * @return storage description
    */
   CmisRepositoryInfoType getRepositoryInfo() throws CmisRuntimeException;

}
