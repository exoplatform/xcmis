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
import org.xcmis.spi.RepositoryException;

/**
 * The methods of MultifilingService are supported only if the repository
 * supports the multifiling or unfiling optional capabilities. The
 * MultifilingService provides methods are used to file/un-file objects
 * into/from folders.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: MultifilingService.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public interface MultifilingService
{

   /**
    * Add un-filed object in folder.
    * 
    * @param repositoryId the repository Id
    * @param objectId the id of object to be added in folder 
    * @param folderId the target folder id
    * @param allVersions to add all versions of the object to the folder if the repository
    *           supports version-specific filing
    * @throws ConstraintException if destination folder is not supported
    *           object type that should be added
    * @throws RepositoryException any repository error
    */
   void addObjectToFolder(String repositoryId, String objectId, String folderId, boolean allVersions)
      throws ConstraintException, RepositoryException;

   /**
    * Remove an existing fileable non-folder object from a folder.
    * 
    * @param repositoryId the repository Id
    * @param objectId the id of object to be removed 
    * @param folderId the folder from which the object is to be removed. If null, then
    *          remove the object from all folders in which it is currently filed
    * @throws RepositoryException any repository error
    */
   void removeObjectFromFolder(String repositoryId, String objectId, String folderId) throws RepositoryException;

}
