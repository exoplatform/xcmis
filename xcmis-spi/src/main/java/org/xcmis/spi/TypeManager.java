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

package org.xcmis.spi;

import org.xcmis.core.CmisTypeDefinitionType;

/**
 * Produces type definition.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 * @deprecated
 */
public interface TypeManager
{

   /**
    * Add new type in repository.
    * 
    * @param type type definition
    * @throws RepositoryException if new type can't be added in repository
    */
   void addType(CmisTypeDefinitionType type) throws RepositoryException;

   /**
    * Get type definition for type <code>typeId</code> include property definition.
    * 
    * @param typeId type Id
    * @return type definition
    * @throws TypeNotFoundException if type <code>typeId</code> not found in
    *           repository
    * @throws RepositoryException if any other CMIS repository error occurs
    */
   CmisTypeDefinitionType getTypeDefinition(String typeId) throws TypeNotFoundException, RepositoryException;

   /**
    * Get type definition for type <code>typeId</code> .
    * 
    * @param typeId type Id
    * @param includePropertyDefinition if TRUE property definition should be included
    * @return type definition
    * @throws TypeNotFoundException if type <code>typeId</code> not found in
    *           repository
    * @throws RepositoryException if any other CMIS repository error occurs
    */
   CmisTypeDefinitionType getTypeDefinition(String typeId, boolean includePropertyDefinition)
      throws TypeNotFoundException, RepositoryException;

   /**
    * Remove type definition for type <code>typeId</code> .
    * 
    * @param typeId type Id
    * @throws TypeNotFoundException if type <code>typeId</code> not found in
    *           repository
    * @throws RepositoryException if any other CMIS repository error occurs
    */
   void removeType(String typeId) throws TypeNotFoundException, RepositoryException;

}
