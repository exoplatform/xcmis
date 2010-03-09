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

import org.xcmis.messaging.CmisRepositoryEntryType;
import org.xcmis.messaging.CmisTypeContainer;
import org.xcmis.messaging.CmisTypeDefinitionListType;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.Repository;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.TypeNotFoundException;

import java.util.List;

/**
 * For getting information about available repositories.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface RepositoryService
{

   /**
    * Get list of available repositories.
    * 
    * @return list of <tt>CmisRepositoryEntryType</tt>
    */
   List<CmisRepositoryEntryType> getRepositories();

   /**
    * Get Repository by specified id.
    * 
    * @param repositoryId repository id
    * @return repository 
    * @throws InvalidArgumentException if repository with <code>repositoryId</code>
    *            does not exists
    */
   Repository getRepository(String repositoryId) throws InvalidArgumentException;

   /**
    * Retrieve information about CMIS repository and the capabilities it supports.
    * 
    * @param repositoryId repository id
    * @return repository info
    * @throws InvalidArgumentException if repository with <code>repositoryId</code>
    *            does not exists
    */
   CmisRepositoryInfoType getRepositoryInfo(String repositoryId) throws InvalidArgumentException;

   /**
    * Retrieve information about CMIS type.
    * 
    * @param repositoryId repository id
    * @param typeId type id
    * @return type definition
    * @throws InvalidArgumentException if repository with <code>repositoryId</code>
    *            does not exists
    * @throws TypeNotFoundException if type <code>typeId</code> does not exist
    * @throws RepositoryException if any other CMIS repository errors occurs
    */
   CmisTypeDefinitionType getTypeDefinition(String repositoryId, String typeId) throws TypeNotFoundException,
      InvalidArgumentException, RepositoryException;

   /**
    * Get information CMIS types that are children of specified type.
    * If <code>typeId</code> is null than all base object types will be returned.  
    * 
    * @param repositoryId repository id
    * @param typeId type id
    * @param includePropertyDefinitions if TRUE include property definition in
    *          response
    * @param maxItems max number of items in response
    * @param skipCount skip items
    * @return set of object type definitions
    * @throws InvalidArgumentException if repository with <code>repositoryId</code>
    *            does not exists
    * @throws TypeNotFoundException if type <code>typeId</code> does not exist
    * @throws RepositoryException if any other CMIS repository errors occurs
    */
   CmisTypeDefinitionListType getTypeChildren(String repositoryId, String typeId, boolean includePropertyDefinitions,
      int maxItems, int skipCount) throws InvalidArgumentException, TypeNotFoundException, RepositoryException;

   /**
    * Get all descendants of specified <code>typeId</code> in hierarchy.
    * If <code>typeId</code> is <code>null</code> then return all types
    * and value of the <code>depth</code> parameter will be ignored.
    * 
    * @param repositoryId repository id
    * @param typeId type id
    * @param depth the number of level of depth in type hierarchy from which to return result
    * @param includePropertyDefinitions if true include property definition in
    *          response
    * @return set of object type definitions
    * @throws InvalidArgumentException if repository with <code>repositoryId</code>
    *            does not exists
    * @throws TypeNotFoundException if type <code>typeId</code> does not exist
    * @throws RepositoryException if any CMIS repository errors occurs
    */
   List<CmisTypeContainer> getTypeDescendants(String repositoryId, String typeId, int depth,
      boolean includePropertyDefinitions) throws InvalidArgumentException, TypeNotFoundException, RepositoryException;
}
