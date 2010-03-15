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

import org.xcmis.core.CmisObjectType;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.core.PolicyService;
import org.xcmis.core.RepositoryService;
import org.xcmis.core.impl.object.RenditionFilter;
import org.xcmis.core.impl.property.PropertyFilter;
import org.xcmis.core.impl.property.PropertyService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.Repository;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.object.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the PolicyService.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: PolicyServiceImpl.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class PolicyServiceImpl extends CmisObjectProducer implements PolicyService
{
   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(PolicyServiceImpl.class);

   /** CMIS repository service. */
   protected final RepositoryService repositoryService;

   /**
    * Construct instance <tt>PolicyServiceImpl</tt>.
    * 
    * @param repositoryService the repository service for getting repositories
    * @param propertyService the property service for getting properties
    */
   public PolicyServiceImpl(RepositoryService repositoryService, PropertyService propertyService)
   {
      super(propertyService);
      this.repositoryService = repositoryService;
   }

   /**
    * {@inheritDoc}
    */
   public void applyPolicy(String repositoryId, String policyId, String objectId) throws ConstraintException,
      RepositoryException
   {
      if (LOG.isDebugEnabled())
      {
         LOG
            .debug("Apply policy:  repository " + repositoryId + ",policy Id  " + policyId + ",object Id " + objectId);
      }
      Repository repository = repositoryService.getRepository(repositoryId);
      Entry policy = repository.getObjectById(policyId);
      if (EnumBaseObjectTypeIds.CMIS_POLICY != policy.getType().getBaseId())
      {
         String msg = "Object " + policy.getObjectId() + " has not policy type.";
         throw new InvalidArgumentException(msg);
      }
      Entry entry = repository.getObjectById(objectId);
      entry.applyPolicy(policy);
      entry.save();
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisObjectType> getAppliedPolicies(String repositoryId, String objectId, String propertyFilter)
      throws FilterNotValidException, RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Get applied policies policy in repository " + repositoryId + " object Id " + objectId);
      List<CmisObjectType> list = new ArrayList<CmisObjectType>();
      Repository repository = repositoryService.getRepository(repositoryId);
      Entry entry = repository.getObjectById(objectId);
      for (Entry policy : entry.getAppliedPolicies())
         list.add(getCmisObject(policy, false, EnumIncludeRelationships.NONE, true, false, new PropertyFilter(
            propertyFilter), RenditionFilter.NONE, repository.getRenditionManager()));
      return list;
   }

   /**
    * {@inheritDoc}
    */
   public void removePolicy(String repositoryId, String policyId, String objectId) throws ConstraintException,
      RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Remove  policy: repository  " + repositoryId + ", policy Id  " + policyId + " object Id "
            + objectId);
      Repository repository = repositoryService.getRepository(repositoryId);
      Entry policy = repository.getObjectById(policyId);
      if (EnumBaseObjectTypeIds.CMIS_POLICY != policy.getType().getBaseId())
      {
         String msg = "Object " + policy.getObjectId() + " has not policy type.";
         throw new InvalidArgumentException(msg);
      }
      Entry entry = repository.getObjectById(objectId);
      entry.removePolicy(policy);
      entry.save();
   }

}
