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

package org.xcmis.core.impl;

import org.xcmis.core.AccessControlService;
import org.xcmis.core.CmisAccessControlEntryType;
import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.EnumACLPropagation;
import org.xcmis.core.RepositoryService;
import org.xcmis.core.impl.property.PropertyService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.Repository;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.object.Entry;

import java.util.List;

/**
 * Implementation for manages object permission via ACL.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: AccessControlServiceImpl.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class AccessControlServiceImpl extends CmisObjectProducer implements AccessControlService
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(AccessControlServiceImpl.class);

   /** CMIS repository service. */
   protected final RepositoryService repositoryService;

   /**
    * Construct instance <tt>AccessControlServiceImpl</tt>.
    * 
    * @param repositoryService the repository service for getting repositories
    * @param propertyService the property service for getting properties
    */
   public AccessControlServiceImpl(RepositoryService repositoryService, PropertyService propertyService)
   {
      super(propertyService);
      this.repositoryService = repositoryService;
   }

   /**
    * {@inheritDoc}
    */
   public CmisAccessControlListType applyACL(String repositoryId, String objectId, CmisAccessControlListType addACL,
      CmisAccessControlListType removeACL, EnumACLPropagation propagation) throws ConstraintException,
      RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Apply ACL, repository id " + repositoryId + ", object id " + objectId);
      // TODO change token
      Repository repository = repositoryService.getRepository(repositoryId);
      if (propagation != null && propagation != EnumACLPropagation.REPOSITORYDETERMINED
         && repository.getRepositoryInfo().getAclCapability().getPropagation() != propagation)
      {
         String msg = "ACLPropagation " + propagation.value() + " is not supported by repository.";
         throw new ConstraintException(msg);
      }
      Entry entry = repository.getObjectById(objectId);
      if (removeACL != null)
         entry.removePermissions(removeACL.getPermission());
      if (addACL != null)
         entry.addPermissions(addACL.getPermission());
      entry.save();
      List<CmisAccessControlEntryType> permissions = entry.getPermissions();
      CmisAccessControlListType actualACL = new CmisAccessControlListType();
      actualACL.getPermission().addAll(permissions);
      return actualACL;
   }

   /**
    * {@inheritDoc}
    */
   public CmisAccessControlListType getACL(String repositoryId, String objectId, boolean onlyBasicPermissions)
      throws RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Get ACL, repository id " + repositoryId + ", object id " + objectId);
      Repository repository = repositoryService.getRepository(repositoryId);
      Entry entry = repository.getObjectById(objectId);
      CmisAccessControlListType actualACL = new CmisAccessControlListType();
      actualACL.getPermission().addAll(entry.getPermissions());
      return actualACL;
   }

}
