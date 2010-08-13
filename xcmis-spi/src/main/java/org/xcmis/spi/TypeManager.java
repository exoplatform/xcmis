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

import org.xcmis.spi.model.TypeDefinition;

/**
 * Produces type definition.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: TypeManager.java 316 2010-03-09 15:20:28Z andrew00x $
 */
public interface TypeManager
{

   /**
    * Add new type in repository.
    * 
    * 2.1.3 Object-Type 
    * A repository MAY define additional object-types beyond the CMIS Base Object-Types
    * 
    * Implementation Compatibility: MAY be implemented.
    *
    * @param type the type definition
    * @return ID of added type
    * @throws StorageException if type can't be added cause to storage internal
    *         problem
    * @throws ConstraintException if any of the following conditions are met:
    *         <ul>
    *         <li>Storage already has type with the same id, see
    *         {@link TypeDefinition#getId()}</li>
    *         <li>Base type is not specified or is one of optional type that is
    *         not supported by storage, see {@link TypeDefinition#getBaseId()}</li>
    *         <li>Parent type is not specified or does not exist, see
    *         {@link TypeDefinition#getParentId()}</li>
    *         <li>New type has at least one property definitions that has
    *         unsupported type, invalid id, so on</li>
    *         </ul>
    */
   String addType(TypeDefinition type) throws ConstraintException, StorageException;

   /**
    * Get type definition for type <code>typeId</code> .
    *
    * @param typeId type Id
    * @param includePropertyDefinition if <code>true</code> property definition
    *        should be included
    * @return type definition
    * @throws TypeNotFoundException if type <code>typeId</code> does not exist
    */
   TypeDefinition getTypeDefinition(String typeId, boolean includePropertyDefinition) throws TypeNotFoundException;

   /**
    * Iterator over object types.
    *
    * @param typeId the type id, if not <code>null</code> then return only
    *        specified Object Type and its direct descendant. If
    *        <code>null</code> then return base types
    * @param includePropertyDefinitions <code>true</code> if property definition
    *        should be included <code>false</code> otherwise
    * @return set of base types or specified object type and its direct children
    * @throws TypeNotFoundException if type <code>typeId</code> does not exist
    */
   ItemsIterator<TypeDefinition> getTypeChildren(String typeId, boolean includePropertyDefinitions)
      throws TypeNotFoundException;

   /**
    * Remove type definition for type <code>typeId</code> .
    *
    * @param typeId type Id
    * @throws TypeNotFoundException if type <code>typeId</code> not found in
    *         repository
    * @throws StorageException if type can't be added cause to storage internal
    *         problem
    * @throws ConstraintException if removing type violates a storage
    *         constraint. For example, if storage already contains object of
    *         this type
    */
   void removeType(String typeId) throws ConstraintException, TypeNotFoundException, StorageException;

}
