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

import org.xcmis.core.CmisRepositoryInfoType;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.RepositoryService;
import org.xcmis.messaging.CmisRepositoryEntryType;
import org.xcmis.messaging.CmisTypeContainer;
import org.xcmis.messaging.CmisTypeDefinitionListType;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.RepositoriesManager;
import org.xcmis.spi.Repository;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.object.ItemsIterator;

import java.math.BigInteger;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RepositoryServiceImpl.java 2118 2009-07-13 20:40:48Z andrew00x
 *          $
 */
public class RepositoryServiceImpl implements RepositoryService
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(RepositoryServiceImpl.class);

   /** Repositories manager. */ 
   protected final RepositoriesManager repositoriesManager;

   /**
    * Construct instance <code>RepositoryServiceImpl</code>.
    * 
    * @param repositoriesManager the RepositoriesManager
    */
   public RepositoryServiceImpl(RepositoriesManager repositoriesManager)
   {
      this.repositoriesManager = repositoriesManager;
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisRepositoryEntryType> getRepositories()
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Get all repositories");
      return repositoriesManager.getRepositories();
   }

   /**
    * {@inheritDoc}
    */
   public Repository getRepository(String repositoryId)
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Get repository " + repositoryId);
      Repository repository = repositoriesManager.getRepository(repositoryId);
      if (repository == null)
      {
         String msg = "Repository " + repositoryId + " does not exists or unreachable.";
         throw new InvalidArgumentException(msg);
      }
      return repository;
   }

   /**
    * {@inheritDoc}
    */
   public CmisRepositoryInfoType getRepositoryInfo(String repositoryId)
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Get repository info " + repositoryId);
      Repository repository = getRepository(repositoryId);
      return repository.getRepositoryInfo();
   }

   /**
    * {@inheritDoc}
    */
   public CmisTypeDefinitionListType getTypeChildren(String repositoryId, String typeId,
      boolean includePropertyDefinitions, int maxItems, int skipCount) throws RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("In getTypeChildren, repository " + repositoryId + ", type " + typeId);
      if (skipCount < 0)
      {
         String msg = "SkipCount parameter is negative.";
         throw new InvalidArgumentException(msg);
      }
      if (maxItems < 0)
      {
         String msg = "MaxItems parameter is negative.";
         throw new InvalidArgumentException(msg);
      }

      Repository repository = getRepository(repositoryId);
      ItemsIterator<CmisTypeDefinitionType> items = repository.getTypeChildren(typeId, includePropertyDefinitions);
      try
      {
         if (skipCount > 0)
            items.skip(skipCount);
      }
      catch (NoSuchElementException nse)
      {
         String msg = "skipCount parameter is greater then total number of argument";
         throw new InvalidArgumentException(msg);
      }
      CmisTypeDefinitionListType list = new CmisTypeDefinitionListType();
      for (int count = 0; items.hasNext() && count < maxItems; count++)
         list.getTypes().add(items.next());
      // Indicate that we have some more results or not.
      list.setHasMoreItems(items.hasNext());
      long total = items.size();
      if (total != -1)
         list.setNumItems(BigInteger.valueOf(total));
      return list;
   }

   /**
    * {@inheritDoc}
    */
   public CmisTypeDefinitionType getTypeDefinition(String repositoryId, String typeId) throws RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Get type definition, repository " + repositoryId + ", type " + typeId);
      Repository repository = getRepository(repositoryId);
      return repository.getTypeDefinition(typeId);
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisTypeContainer> getTypeDescendants(String repositoryId, String typeId, int depth,
      boolean includePropertyDefinitions) throws RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Get type descendants, repositoryId " + repositoryId + ", typeId " + typeId);
      Repository repository = getRepository(repositoryId);
      return repository.getTypeDescendants(typeId, depth, includePropertyDefinitions);
   }

}
