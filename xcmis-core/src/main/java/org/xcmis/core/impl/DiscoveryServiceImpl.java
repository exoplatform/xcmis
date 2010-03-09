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
import org.xcmis.core.CmisPropertyDecimal;
import org.xcmis.core.DiscoveryService;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.core.RepositoryService;
import org.xcmis.core.impl.object.RenditionFilter;
import org.xcmis.core.impl.property.PropertyFilter;
import org.xcmis.core.impl.property.PropertyService;
import org.xcmis.messaging.CmisObjectListType;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.NotSupportedException;
import org.xcmis.spi.Repository;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.object.CmisObject;
import org.xcmis.spi.object.CmisObjectList;
import org.xcmis.spi.object.ItemsIterator;
import org.xcmis.spi.object.RenditionManager;
import org.xcmis.spi.object.impl.CmisObjectListImpl;
import org.xcmis.spi.query.Query;
import org.xcmis.spi.query.QueryHandler;
import org.xcmis.spi.query.Result;
import org.xcmis.spi.query.Score;

import java.math.BigInteger;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class DiscoveryServiceImpl extends CmisObjectProducer implements DiscoveryService
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(DiscoveryServiceImpl.class);

   /** Repository service. */
   private final RepositoryService repositoryService;

   /**
    * Construct instance <tt>QueryServiceImpl</tt>.
    * 
    * @param repositoryService the repository service for getting repositories
    * @param propertyService the property service for getting properties
    */
   public DiscoveryServiceImpl(RepositoryService repositoryService, PropertyService propertyService)
   {
      super(propertyService);
      this.repositoryService = repositoryService;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectListType getContentChanges(String repositoryId, String changeLogToken, boolean includeProperties,
      String propertyFilter, boolean includePolicyIds, boolean includeACL, int maxItems)
   {
      // TODO
      throw new NotSupportedException("The repository does not support the change log feature.");
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectList query(String repositoryId, String statement, boolean searchAllVersions,
      boolean includeAllowableActions, EnumIncludeRelationships includeRelationships, String renditionFilter,
      int maxItems, int skipCount, boolean includeObjectInfo) throws RepositoryException, FilterNotValidException
   {

      if (LOG.isDebugEnabled())
         LOG.debug("Query repository " + repositoryId + ", query statement " + statement);

      if (skipCount < 0)
      {
         String msg = "skipCount parameter is negative.";
         throw new InvalidArgumentException(msg);
      }
      if (maxItems < 0)
      {
         String msg = "maxItems parameter is negative.";
         throw new InvalidArgumentException(msg);
      }

      Repository repository = repositoryService.getRepository(repositoryId);
      QueryHandler queryHandler = repository.getQueryHandler();

      ItemsIterator<Result> items = queryHandler.handleQuery(new Query(statement, searchAllVersions));

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

      CmisObjectList list = new CmisObjectListImpl();
      int count = 0;
      while (items.hasNext() && count < maxItems)
      {
         Result result = items.next();
         StringBuilder propertyFilter = new StringBuilder();
         if (result.getPropertyNames() != null)
         {
            for (String s : result.getPropertyNames())
            {
               if (propertyFilter.length() > 0)
                  propertyFilter.append(',');
               propertyFilter.append(s);
            }
         }
         CmisObject object =
            getCmisObject(repository.getObjectById(result.getObjectId()), includeAllowableActions, includeRelationships,
               false, false, new PropertyFilter(propertyFilter.toString()), new RenditionFilter(renditionFilter),
               (RenditionManager)repository, includeObjectInfo);
         
         Score score = result.getScore();
         if (score != null)
         {
            CmisPropertyDecimal scoreProperty = new CmisPropertyDecimal();
            scoreProperty.setLocalName(score.getScoreColumnName());
            scoreProperty.setDisplayName(score.getScoreColumnName());
            scoreProperty.setPropertyDefinitionId(score.getScoreColumnName());
            scoreProperty.getValue().add(score.getScoreValue());
            object.getProperties().getProperty().add(0, scoreProperty);
         }
         list.getObjects().add(object);
         count++;
      }
      
      // Indicate that we have some more results.
      list.setHasMoreItems(items.hasNext());
      long total = items.size();
      if (total != -1)
         list.setNumItems(BigInteger.valueOf(total));
      return list;
   }
   
}
