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

import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.messaging.CmisObjectListType;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.RepositoryException;

/**
 * Is used to search for query-able objects in the Repository.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface DiscoveryService
{

   /**
    * Gets a list of content changes. This service is intended to be used by search
    * crawlers or other applications that need to efficiently understand what has
    * changed in the repository.
    * 
    * @param repositoryId repository id
    * @param changeLogToken if specified, then the Repository returns the change
    * event corresponding to the value of the specified change log token as the
    * first result in the output. If not specified, then the Repository returns
    * the first change event recorded in the change log.
    * @param includeProperties if TRUE, then the Repository includes the updated
    *           property values for 'updated' change events. If FALSE, then the
    *           Repository  will not include the updated property values for
    *           'updated' change events. The single exception to this is that
    *           the objectId MUST always be included.
    * @param propertyFilter property filter as string 
    * @param includePolicyIds if TRUE, then the include the IDs of Policies
    *           applied to the object referenced in each change event, if the
    *           change event  modified the set of policies applied to the object.
    * @param includeACL if TRUE, then include ACL applied to the object referenced
    *           in each change event
    * @param maxItems max items in result
    * @return list of content changes
    * @throws ConstraintException if the event corresponding to the
    *            change log token provided as an input parameter is no longer
    *            available in the change log. (E.g. because the change log
    *            was truncated).
    * @throws FilterNotValidException if <code>propertyFilter</code> is invalid
    * @throws RepositoryException if any error in repository occurs
    */
   CmisObjectListType getContentChanges(String repositoryId, String changeLogToken, boolean includeProperties,
      String propertyFilter, boolean includePolicyIds, boolean includeACL, int maxItems)
      throws ConstraintException, FilterNotValidException, RepositoryException;

   /**
    * Executes a CMIS-SQL query statement against the contents of the CMIS
    * Repository.
    * 
    * @param repositoryId repository id
    * @param statement SQL statement
    * @param searchAllVersions if FALSE, then include latest versions of documents
    *          in the query search scope otherwise all versions. If the Repository
    *          does not support the optional capabilityAllVersionsSearchable
    *          capability, then this parameter value MUST be set to FALSE.
    * @param includeAllowableActions if TRUE return allowable actions in request
    * @param includeRelationships indicates what relationships of object must be returned
    * @param renditionFilter rendition filter as string
    * @param maxItems max items in result
    * @param skipCount skip items
    * @return set of query results
    * @throws FilterNotValidException if <code>renditionFilter</code> is invalid
    * @throws RepositoryException if any error in repository occurs
    */
   CmisObjectListType query(String repositoryId, String statement, boolean searchAllVersions,
      boolean includeAllowableActions, EnumIncludeRelationships includeRelationships, String renditionFilter,
      int maxItems, int skipCount) throws FilterNotValidException, RepositoryException;
}
