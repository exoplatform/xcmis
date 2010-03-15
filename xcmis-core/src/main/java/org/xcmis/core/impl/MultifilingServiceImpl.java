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

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.core.MultifilingService;
import org.xcmis.core.RepositoryService;
import org.xcmis.core.impl.property.PropertyService;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.NotSupportedException;
import org.xcmis.spi.Repository;
import org.xcmis.spi.RepositoryException;

/**
 * Implementation of the MultifilingService.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: MultifilingServiceImpl.java 2118 2009-07-13 20:40:48Z andrew00x
 *          $
 */
public class MultifilingServiceImpl extends CmisObjectProducer implements MultifilingService
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(MultifilingServiceImpl.class);

   /** CMIS repository service. */
   protected final RepositoryService repositoryService;

   /**
    * Construct instance <tt>MultifilingServiceImpl</tt>.
    * 
    * @param repositoryService the repository service for getting repositories
    * @param propertyService the property service for getting properties
    */
   public MultifilingServiceImpl(RepositoryService repositoryService, PropertyService propertyService)
   {
      super(propertyService);
      this.repositoryService = repositoryService;
   }

   /**
    * {@inheritDoc}
    */
   public void addObjectToFolder(String repositoryId, String objectId, String folderId, boolean allVersions)
      throws ConstraintException, RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Repository " + repositoryId + ", add " + objectId + " in folder " + folderId);

      Repository repository = repositoryService.getRepository(repositoryId);

      if (!repository.getRepositoryInfo().getCapabilities().isCapabilityMultifiling())
      {
         String msg = "Multifiling capabilities is not supported by repository";
         throw new NotSupportedException(msg);
      }
      repository.getObjectById(folderId).addChild(repository.getObjectById(objectId));
   }

   /**
    * {@inheritDoc}
    */
   public void removeObjectFromFolder(String repositoryId, String objectId, String folderId) throws RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Repository " + repositoryId + ", remove " + objectId + " from folder " + folderId);
      
      Repository repository = repositoryService.getRepository(repositoryId);

      if (!repository.getRepositoryInfo().getCapabilities().isCapabilityMultifiling())
      {
         String msg = "Multifiling capabilities is not supported by repository";
         throw new NotSupportedException(msg);
      }
      repository.getObjectById(folderId).removeChild(objectId);
   }
}
