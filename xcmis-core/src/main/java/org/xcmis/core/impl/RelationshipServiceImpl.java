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
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.EnumRelationshipDirection;
import org.xcmis.core.RelationshipService;
import org.xcmis.core.RepositoryService;
import org.xcmis.core.impl.object.RenditionFilter;
import org.xcmis.core.impl.property.PropertyFilter;
import org.xcmis.core.impl.property.PropertyService;
import org.xcmis.messaging.CmisObjectListType;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.Repository;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.ItemsIterator;
import org.xcmis.spi.object.RenditionManager;

import java.math.BigInteger;
import java.util.NoSuchElementException;

/**
 * Implementation of the RelationshipService.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: JcrRelationshipService.java 386 2009-06-25 10:22:12Z andrew00x
 *          $
 */
public class RelationshipServiceImpl extends CmisObjectProducer implements RelationshipService
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(RelationshipServiceImpl.class.getName());

   /** CMIS repository service. */
   protected final RepositoryService repositoryService;

   /**
    * Construct instance <tt>RelationshipServiceImpl</tt>.
    * 
    * @param repositoryService the repository service for getting repositories
    * @param propertyService the property service for getting properties
    */
   public RelationshipServiceImpl(RepositoryService repositoryService, PropertyService propertyService)
   {
      super(propertyService);
      this.repositoryService = repositoryService;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectListType getObjectRelationships(String repositoryId, String objectId,
      EnumRelationshipDirection direction, String typeId, boolean includeSubRelationshipTypes,
      boolean includeAllowableActions, String propertyFilter, int maxItems, int skipCount)
      throws FilterNotValidException, RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Retrieve relationships, repository " + repositoryId + ", object " + objectId);

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

      Repository repository = repositoryService.getRepository(repositoryId);
      Entry obj = repository.getObjectById(objectId);

      CmisTypeDefinitionType type = typeId != null ? repository.getTypeDefinition(typeId, false) : null;
      ItemsIterator<Entry> items;
      try
      {
         items = obj.getRelationships(direction, includeSubRelationshipTypes, type);
      }
      catch (RepositoryException e)
      {
         String msg = "Unable get relationthips for object " + objectId;
         throw new RuntimeException(msg, e);
      }

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

      CmisObjectListType list = new CmisObjectListType();
      long count = 0;
      RenditionManager renditionManager = repository.getRenditionManager();
      while (items.hasNext() && count < maxItems)
      {
         Entry entry = items.next();
         CmisObjectType cmis =
            getCmisObject(entry, includeAllowableActions, null, false, false, new PropertyFilter(propertyFilter),
               RenditionFilter.NONE, renditionManager);
         list.getObjects().add(cmis);
         count++;
      }

      // Indicate we have some more results or not
      list.setHasMoreItems(items.hasNext());
      long total = items.size();
      if (total != -1)
         list.setNumItems(BigInteger.valueOf(total));
      return list;
   }

}
