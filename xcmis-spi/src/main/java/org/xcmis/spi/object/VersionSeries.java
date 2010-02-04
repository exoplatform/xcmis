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

package org.xcmis.spi.object;

import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.VersioningException;

import java.util.List;

/**
 * A version series for a Document object is a transitively closed collection of
 * all Document objects that have been created from an original Document in the
 * Repository.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface VersionSeries
{

   /**
    * Discard the check-out operation. As result Private Working Copy must be
    * removed and repository ready to next check-out operation.
    * 
    * @throws ConstraintException if the object is not versionable
    * @throws RepositoryException if any errors in CMIS repository occurs
    */
   //   * @throws UpdateConflictException if update an object that is no longer current
   // TODO : Specification is still not clear about UpdateConflictException in this case.
   void cancelCheckout() throws ConstraintException/*, UpdateConflictException*/, RepositoryException;

   /**
    * Check-in private working copy of document.
    * 
    * @param major TRUE if new version should be marked as major
    * @param checkinComment check-in comment
    * @return checked-in document
    * @throws ConstraintException if object is not versionable
    * @throws VersioningException if object is not checked-out yet
    * @throws RepositoryException any others CMIS repository errors
    */
   Entry checkin(boolean major, String checkinComment) throws ConstraintException, VersioningException,
      RepositoryException;

   /**
    * Create private working copy of document.
    * 
    * @param documentId id of document
    * @return private working copy of document
    * @throws ConstraintException if object is not versionable
    * @throws VersioningException if one object in Version Series that
    *            contains this document already checked-out
    * @throws RepositoryException any others CMIS repository errors
    */
   Entry checkout(String documentId) throws ConstraintException, VersioningException, RepositoryException;

   /**
    * Get all versions of document in this version series.
    * 
    * @return all documents in version series, even document is not versionable
    *         version series contains at least one document
    * @throws RepositoryException if any errors in CMIS repository occurs
    */
   List<Entry> getAllVersions() throws RepositoryException;

   /**
    * Get checked-out document in version series.
    * 
    * @return checked out document or null
    * @throws RepositoryException if any errors in CMIS repository occurs
    */
   Entry getCheckedOut() throws RepositoryException;

   /**
    * Get latest major version in version series.
    * 
    * @return latest major version in version series
    * @throws RepositoryException if any errors in CMIS repository occurs
    */
   Entry getLatestMajorVersion() throws RepositoryException;

   /**
    * Get latest version in CMIS documents in version series.
    * 
    * @return latest document in version series
    * @throws RepositoryException if any errors in CMIS repository occurs
    */
   Entry getLatestVersion() throws RepositoryException;

   /**
    * Get version series Id.
    * 
    * @return id of version series.
    */
   String getVersionSeriesId();

   /**
    * Delete whole version series.
    * 
    * @throws RepositoryException if any errors in CMIS repository occurs
    */
   void delete() throws RepositoryException;

   /**
    * Delete specified version in version series.
    * 
    * @param versionId the version in version series
    * @throws RepositoryException if any errors in CMIS repository occurs
    */
   void deleteVersion(String versionId) throws RepositoryException;

}
