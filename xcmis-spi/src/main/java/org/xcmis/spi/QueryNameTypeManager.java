/*
 * Copyright (C) 2012 eXo Platform SAS.
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

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A wrapper for TypeManager implementation that make possible to get TypeDefinition by queryName attribute.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class QueryNameTypeManager extends DelegatedTypeManager
{
   private final Map<String, String> aliases;

   public QueryNameTypeManager(TypeManager typeManager)
   {
      super(typeManager);
      aliases = new ConcurrentHashMap<String, String>();
   }

   /**
    * Get TypeDefinition by queryName of type.
    * @param queryName queryName attribute of type definition
    * @param includePropertyDefinition if <code>true</code> property definition
    *        should be included
    * @return type definition
    * @throws TypeNotFoundException if type <code>queryName</code> does not exist
    */
   public TypeDefinition getTypeDefinitionByQueryName(String queryName, boolean includePropertyDefinition)
      throws TypeNotFoundException
   {
      String typeId = aliases.get(queryName);
      if (typeId == null)
      {
         TypeDefinition type = findTypeDefinitionByQueryName(queryName, includePropertyDefinition);
         aliases.put(type.getQueryName(), type.getId());
         return type;
      }
      try
      {
        return getTypeDefinition(typeId, includePropertyDefinition);
      }
      catch (TypeNotFoundException e)
      {
         // re-throw for correct message
         throw new TypeNotFoundException("Type with queryName '" + queryName + "' not found.");
      }
   }

   private TypeDefinition findTypeDefinitionByQueryName(String queryName, boolean includePropertyDefinition)
      throws TypeNotFoundException
   {
      LinkedList<TypeDefinition> q = new LinkedList<TypeDefinition>();
      // Add root types.
      for (ItemsIterator<TypeDefinition> i = getTypeChildren(null, true); i.hasNext(); )
      {
         q.add(i.next());
      }

      while (!q.isEmpty())
      {
         TypeDefinition type = q.pop();
         if (type.getQueryName().equals(queryName))
         {
            return type;
         }
         for (ItemsIterator<TypeDefinition> i = getTypeChildren(type.getId(), true); i.hasNext(); )
         {
            q.push(i.next());
         }
      }
      throw new TypeNotFoundException("Type with queryName '" + queryName + "' not found.");
   }
}
