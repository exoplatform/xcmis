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
package org.xcmis.spi.basic;

import org.xcmis.spi.BaseItemsIterator;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.TypeManager;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.ContentStreamAllowed;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.TypeDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Basic Type Manager
 */
public abstract class BasicTypeManager implements TypeManager
{

   protected final Map<String, TypeDefinition> types;

   protected final Map<String, Set<String>> typeChildren;

   public BasicTypeManager()
   {
      this.types = new ConcurrentHashMap<String, TypeDefinition>();

      types.put("cmis:document", //
         new TypeDefinition("cmis:document", BaseType.DOCUMENT, "cmis:document", "cmis:document", "", null,
            "cmis:document", "Cmis Document Type", true, true, true, true, true, true, true, true, null, null,
            ContentStreamAllowed.ALLOWED, null));

      types.put("cmis:folder", //
         new TypeDefinition("cmis:folder", BaseType.FOLDER, "cmis:folder", "cmis:folder", "", null, "cmis:folder",
            "Cmis Folder type", true, true, true, false, true, true, true, false, null, null,
            ContentStreamAllowed.NOT_ALLOWED, null));

      types.put("cmis:policy", //
         new TypeDefinition("cmis:policy", BaseType.POLICY, "cmis:policy", "cmis:policy", "", null, "cmis:policy",
            "Cmis Policy type", true, false, false /*no query support*/, false, false, true, true, false, null, null,
            ContentStreamAllowed.NOT_ALLOWED, null));

      types.put("cmis:relationship", //
         new TypeDefinition("cmis:relationship", BaseType.RELATIONSHIP, "cmis:relationship", "cmis:relationship", "",
            null, "cmis:relationship", "Cmis Relationship type.", true, false, false /*no query support*/, false,
            false, true, true, false, null, null, ContentStreamAllowed.NOT_ALLOWED, null));

      typeChildren = new ConcurrentHashMap<String, Set<String>>();
      typeChildren.put("cmis:document", new HashSet<String>());
      typeChildren.put("cmis:folder", new HashSet<String>());
      typeChildren.put("cmis:policy", new HashSet<String>());
      typeChildren.put("cmis:relationship", new HashSet<String>());
   }

   /**
    * {@inheritDoc}
    */
   public String addType(TypeDefinition type) throws StorageException
   {
      if (types.get(type.getId()) != null)
      {
         throw new InvalidArgumentException("Type " + type.getId() + " already exists.");
      }
      if (type.getBaseId() == null)
      {
         throw new InvalidArgumentException("Base type id must be specified.");
      }
      if (type.getParentId() == null)
      {
         throw new InvalidArgumentException("Unable add root type. Parent type id must be specified");
      }

      TypeDefinition superType;
      try
      {
         superType = getTypeDefinition(type.getParentId(), true);
      }
      catch (TypeNotFoundException e)
      {
         throw new InvalidArgumentException("Specified parent type " + type.getParentId() + " does not exists.");
      }
      // Check new type does not use known property IDs.
      if (type.getPropertyDefinitions() != null)
      {
         for (PropertyDefinition<?> newDefinition : type.getPropertyDefinitions())
         {
            PropertyDefinition<?> definition = superType.getPropertyDefinition(newDefinition.getId());
            if (definition != null)
            {
               throw new InvalidArgumentException("Property " + newDefinition.getId() + " already defined");
            }
         }
      }

      Map<String, PropertyDefinition<?>> m = new HashMap<String, PropertyDefinition<?>>();
      for (Iterator<PropertyDefinition<?>> iterator = superType.getPropertyDefinitions().iterator(); iterator.hasNext();)
      {
         PropertyDefinition<?> next = iterator.next();
         m.put(next.getId(), next);
      }

      if (type.getPropertyDefinitions() != null)
      {
         for (Iterator<PropertyDefinition<?>> iterator = type.getPropertyDefinitions().iterator(); iterator.hasNext();)
         {
            PropertyDefinition<?> next = iterator.next();
            m.put(next.getId(), next);
         }
      }

      types.put(type.getId(), type);
      typeChildren.get(superType.getId()).add(type.getId());
      typeChildren.put(type.getId(), new HashSet<String>());
      PropertyDefinitions.putAll(type.getId(), m);

      return type.getId();
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<TypeDefinition> getTypeChildren(String typeId, boolean includePropertyDefinitions)
      throws TypeNotFoundException
   {
      List<TypeDefinition> types = new ArrayList<TypeDefinition>();
      if (typeId == null)
      {
         for (String t : new String[]{"cmis:document", "cmis:folder", "cmis:policy", "cmis:relationship"})
         {
            types.add(getTypeDefinition(t, includePropertyDefinitions));
         }
      }
      else
      {
         if (this.types.get(typeId) == null)
         {
            throw new TypeNotFoundException("Type " + typeId + " does not exist.");
         }

         for (String t : typeChildren.get(typeId))
         {
            types.add(getTypeDefinition(t, includePropertyDefinitions));
         }
      }
      return new BaseItemsIterator<TypeDefinition>(types);
   }

   /**
    * {@inheritDoc}
    */
   public TypeDefinition getTypeDefinition(String typeId, boolean includePropertyDefinition)
      throws TypeNotFoundException
   {
      TypeDefinition type = types.get(typeId);
      if (type == null)
      {
         throw new TypeNotFoundException("Type " + typeId + " does not exist.");
      }
      TypeDefinition copy =
         new TypeDefinition(type.getId(), type.getBaseId(), type.getQueryName(), type.getLocalName(), type
            .getLocalNamespace(), type.getParentId(), type.getDisplayName(), type.getDescription(), type.isCreatable(),
            type.isFileable(), type.isQueryable(), type.isFulltextIndexed(), type.isIncludedInSupertypeQuery(), type
               .isControllablePolicy(), type.isControllableACL(), type.isVersionable(), type.getAllowedSourceTypes(),
            type.getAllowedTargetTypes(), type.getContentStreamAllowed(), includePropertyDefinition
               ? PropertyDefinitions.getAll(typeId) : null);

      return copy;
   }

   /**
    * {@inheritDoc}
    */
   public void removeType(String typeId) throws TypeNotFoundException, ConstraintException, StorageException
   {
      TypeDefinition type = types.get(typeId);
      if (type == null)
      {
         throw new TypeNotFoundException("Type " + typeId + " does not exist.");
      }

      if (type.getParentId() == null)
      {
         throw new ConstraintException("Unable remove root type " + typeId);
      }

      if (typeChildren.get(typeId).size() > 0)
      {
         throw new ConstraintException("Unable remove type " + typeId + ". Type has descendant types.");
      }

      types.remove(typeId);
      typeChildren.get(type.getParentId()).remove(typeId);

      PropertyDefinitions.removeAll(typeId);
   }

}
