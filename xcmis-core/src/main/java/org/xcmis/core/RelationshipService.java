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

import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.object.CmisObjectList;

/**
 * The RelationshipService are used to retrieve the relationship objects
 * associated with an independent object.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface RelationshipService
{

   /**
    *  Get all or a subset of relationships associated with an independent object.
    *  
    * @param repositoryId repository id
    * @param objectId object id
    * @param direction relationship direction
    * @param typeId relationship type id
    * @param includeSubRelationshipTypes if true, then the return all
    *          relationships whose object types are descendant types of
    *          <code>typeId</code>.
    * @param includeAllowableActions if TRUE then allowable actions should be
    *          included in response
    * @param propertyFilter property filter as string
    * @param maxItems number of max items in response
    * @param skipCount skip items
    * @param includeObjectInfo TODO
    * @return set of object's relationships
    * @throws FilterNotValidException if <code>propertyFilter</code> is invalid
    * @throws ObjectNotFoundException if object with <code>objectId</code> does not exist            
    * @throws RepositoryException if any other errors in repository occur
    * @see EnumRelationshipDirection
    */
   CmisObjectList getObjectRelationships(String repositoryId, String objectId, EnumRelationshipDirection direction,
      String typeId, boolean includeSubRelationshipTypes, boolean includeAllowableActions, String propertyFilter,
      int maxItems, int skipCount, boolean includeObjectInfo) throws FilterNotValidException, ObjectNotFoundException, RepositoryException;

}
