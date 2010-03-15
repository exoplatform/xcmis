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

import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.object.ContentStream;

import java.io.IOException;
import java.util.List;

/**
 * Is used to navigate and manage Document's version series.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: VersioningService.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public interface VersioningService
{

   /**
    * Discard the check-out operation. As result Private Working Copy must be
    * removed and repository ready to next check-out operation.
    * 
    * @param repositoryId repository id
    * @param documentId document id
    * @throws ConstraintException if the object is not versionable
    * @throws UpdateConflictException if update an object that is no longer current
    * @throws RepositoryException if any errors in CMIS repository occurs
    */
   // TODO : Specification is still not clear about UpdateConflictException in this case.
   void cancelCheckout(String repositoryId, String documentId) throws ConstraintException,
      UpdateConflictException, RepositoryException;

   /**
    * Check-in Private Working Copy.
    * 
    * @param repositoryId repository id
    * @param documentId document id
    * @param major true is new version should be marked as major false otherwise
    * @param properties properties to be applied to new version
    * @param content content of document
    * @param checkinComment check-in comment
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
    * @return checked-in document
    * @throws ConstraintException if the object is not versionable
    * @throws UpdateConflictException if update an object that is no longer current
    * @throws StreamNotSupportedException if document does not supports content
    *           stream
    * @throws IOException if any i/o error occurs when try to set document's
    *           content stream
    * @throws RepositoryException if any errors in CMIS repository occurs
    */
   // TODO : Specification is still not clear about UpdateConflictException in this case.
   CmisObjectType checkin(String repositoryId, String documentId, boolean major, CmisPropertiesType properties,
      ContentStream content, String checkinComment, CmisAccessControlListType addACL,
      CmisAccessControlListType removeACL, List<String> policies) throws ConstraintException,
      UpdateConflictException, StreamNotSupportedException, IOException, RepositoryException;

   /**
    * Check-out document.
    * 
    * @param repositoryId repository id
    * @param documentId document id
    * @return checked-out document
    * @throws ConstraintException if the object is not versionable
    * @throws UpdateConflictException if update an object that is no longer current
    * @throws VersioningException if object is a non-current document version
    * @throws RepositoryException if any errors in CMIS repository occurs
    */
   // TODO : Specification is still not clear about UpdateConflictException in this case.
   CmisObjectType checkout(String repositoryId, String documentId) throws ConstraintException,
      UpdateConflictException, VersioningException, RepositoryException;

   /**
    * Get all documents in version series.
    * 
    * @param repositoryId repository id
    * @param versionSeriesId version series id
    * @param includeAllowableActions true if allowable actions should be included
    *          in response false otherwise
    * @param propertyFilter property filter as string
    * @return set of documents in the specified <code>versionSeriesId</code>
    * @throws FilterNotValidException if <code>propertyFilter</code> is invalid 
    * @throws RepositoryException if any errors in CMIS repository occurs
    */
   List<CmisObjectType> getAllVersions(String repositoryId, String versionSeriesId, boolean includeAllowableActions,
      String propertyFilter) throws RepositoryException, FilterNotValidException;

   /**
    * Get the latest Document object in the version series. 
    * 
    * @param repositoryId repository id
    * @param versionSeriesId version series id
    * @param major if true then return the properties for the latest
    *          major version object in the Version Series, otherwise return the
    *          properties for the latest (major or non-major) version. If the
    *          input parameter major is true and the Version Series contains no
    *          major versions, then the ObjectNotFoundException will be thrown.
    * @param includeAllowableActions TRUE if allowable actions should be included
    *          in response false otherwise
    * @param includeRelationships include object's relationship
    * @param includePolicyIds include policies IDs applied to object
    * @param includeACL include ACL
    * @param propertyFilter property filter as string
    * @param renditionFilter rendition filter as string 
    * @throws FilterNotValidException if <code>propertyFilter</code> or
    *           <code>renditionFilter</code> is invalid 
    * @throws ObjectNotFoundException if the input parameter <code>major</code>
    *            is TRUE and the Version Series contains no major versions.
    * @return object of latest version in version series
    * @throws RepositoryException if any errors in CMIS repository occurs
    */
   CmisObjectType getObjectOfLatestVersion(String repositoryId, String versionSeriesId, boolean major,
      boolean includeAllowableActions, EnumIncludeRelationships includeRelationships, boolean includePolicyIds,
      boolean includeACL, String propertyFilter, String renditionFilter) throws ObjectNotFoundException,
      FilterNotValidException, RepositoryException;

   /**
    * Get properties of latest version in version series.
    * 
    * @param repositoryId repository id
    * @param versionSeriesId version series id
    * @param major if true then return the properties for the latest
    *           major version object in the Version Series, otherwise return the
    *           properties for the latest (major or non-major) version. If the
    *           input parameter major is true and the Version Series contains no
    *           major versions, then the ObjectNotFoundException will be thrown.
    * @param propertyFilter property filter as string
    * @return properties of latest version of object in version series
    * @throws FilterNotValidException if <code>propertyFilter</code> is invalid 
    * @throws ObjectNotFoundException if the input parameter <code>major</code>
    *            is TRUE and the Version Series contains no major versions.
    * @throws RepositoryException if any errors in CMIS repository occurs
    */
   CmisPropertiesType getPropertiesOfLatestVersion(String repositoryId, String versionSeriesId, boolean major,
      String propertyFilter) throws FilterNotValidException, ObjectNotFoundException, RepositoryException;

}
