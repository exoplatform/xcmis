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
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.RepositoryException;

import java.util.List;

/**
 * Purposes of this service are used to apply or remove a policy object to a
 * controllable objects.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface PolicyService
{

   /**
    * Applies a specified policy to an object.
    * 
    * @param repositoryId the repository id
    * @param policyId the policy to be applied to object
    * @param objectId target object for policy
    * @throws ConstraintException if object with id <code>objectId</code>
    *            is not controllable by policy
    * @throws ObjectNotFoundException if object with <code>objectId</code> or
    *            <code>policyId</code> does not exist            
    * @throws RepositoryException if any other errors in CMIS repository occurs
    */
   void applyPolicy(String repositoryId, String policyId, String objectId) throws ConstraintException,
      ObjectNotFoundException, RepositoryException;

   /**
    * Gets the list of policies currently applied to the specified object.
    * 
    * @param repositoryId the repository id
    * @param objectId the object id
    * @param propertyFilter property filter as string
    * @return set of object's policies
    * @throws FilterNotValidException if <code>propertyFilter</code> is invalid
    * @throws ObjectNotFoundException if object with <code>objectId</code> does not exist            
    * @throws RepositoryException if any other errors in CMIS repository occurs
    */
   List<CmisObjectType> getAppliedPolicies(String repositoryId, String objectId, String propertyFilter)
      throws FilterNotValidException, ObjectNotFoundException, RepositoryException;

   /**
    * Removes a specified policy from an object.
    * 
    * @param repositoryId the repository id
    * @param policyId id of policy to be removed from object
    * @param objectId id of object
    * @throws ConstraintException if object with id <code>objectId</code>
    *            is not controllable by policy
    * @throws ObjectNotFoundException if object with <code>objectId</code> does not exist            
    * @throws RepositoryException if any other errors in CMIS repository occurs
    */
   void removePolicy(String repositoryId, String policyId, String objectId) throws ConstraintException,
      ObjectNotFoundException, RepositoryException;

}
