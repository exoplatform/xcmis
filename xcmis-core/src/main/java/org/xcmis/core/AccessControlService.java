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
import org.xcmis.core.EnumACLPropagation;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.RepositoryException;

/**
 * Manages object permission via ACL.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: AccessControlService.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public interface AccessControlService
{

   /**
    * Adds or removes the given Access Control Entries to or from the Access
    * Control List of object.
    *
    * @param repositoryId repository id
    * @param objectId identifier of object for which should be added or removed
    *           specified ACLs 
    * @param addACL list of ACE that will be added to object
    * @param removeACL list of ACE that will be removed from object
    * @param propagation specifies how ACEs should be handled:
    *          <ul>
    *          <li>objectonly: ACEs must be applied without changing the ACLs
    *          of other objects</li>
    *          <li>propagate: ACEs must be applied by propagate the changes to
    *          all inheriting objects</li>
    *          <li>repositorydetermined: Indicates that the client leaves the
    *          behavior to the repository</li>
    *          </ul>
    * @return actual list of ACEs
    * @throws ConstraintException if any of the following conditions are met:
    *          <ul>
    *          <li>The specified object's Object-Type definition's attribute for
    *          controllableACL is FALSE.</li>
    *          <li>The value for ACLPropagation does not match the values as returned
    *          via getACLCapabilities.</li>
    *          <li>At least one of the specified values for permission in ANY of the
    *          ACEs does not match ANY of the permissionNames as returned by
    *          getACLCapability and is not a CMIS Basic permission</li>
    *          </ul>
    * @throws RepositoryException if any others errors occurs
    */
   // TODO : specification is still not clear about using change tokens for this method.
   CmisAccessControlListType applyACL(String repositoryId, String objectId, CmisAccessControlListType addACL,
      CmisAccessControlListType removeACL, EnumACLPropagation propagation) throws ConstraintException,
      RepositoryException;

   /**
    *  Get the ACL currently applied to the specified object.
    *
    * @param repositoryId repository id
    * @param objectId identifier of object
    * @param onlyBasicPermissions if TRUE then return only the CMIS Basic
    *           permissions
    * @return actual ACL
    * @throws RepositoryException if any repository errors occurs
    */
   CmisAccessControlListType getACL(String repositoryId, String objectId, boolean onlyBasicPermissions)
      throws RepositoryException;

}
