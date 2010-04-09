/*
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
package org.xcmis.sp.inmemory.query;

import org.xcmis.search.content.InMemoryColumn;
import org.xcmis.search.content.Schema;
import org.xcmis.search.model.source.SelectorName;
import org.xcmis.search.value.PropertyType;
import org.xcmis.spi.TypeManager;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.TypeDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@exoplatform.org">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z ksm $
 *
 */
public class CmisSchema implements Schema
{
   public static Map<org.xcmis.spi.model.PropertyType, PropertyType> PROPERTY_TYPES_MAP;
   static
   {
      PROPERTY_TYPES_MAP = new HashMap<org.xcmis.spi.model.PropertyType, PropertyType>();
      PROPERTY_TYPES_MAP.put(org.xcmis.spi.model.PropertyType.BOOLEAN, PropertyType.BOOLEAN);
      PROPERTY_TYPES_MAP.put(org.xcmis.spi.model.PropertyType.ID, PropertyType.STRING);
      PROPERTY_TYPES_MAP.put(org.xcmis.spi.model.PropertyType.INTEGER, PropertyType.LONG);
      PROPERTY_TYPES_MAP.put(org.xcmis.spi.model.PropertyType.DATETIME, PropertyType.DATE);
      PROPERTY_TYPES_MAP.put(org.xcmis.spi.model.PropertyType.DECIMAL, PropertyType.DOUBLE);
      PROPERTY_TYPES_MAP.put(org.xcmis.spi.model.PropertyType.HTML, PropertyType.STRING);
      PROPERTY_TYPES_MAP.put(org.xcmis.spi.model.PropertyType.STRING, PropertyType.STRING);
      PROPERTY_TYPES_MAP.put(org.xcmis.spi.model.PropertyType.URI, PropertyType.STRING);
   }

   private final TypeManager typeManager;

   /**
    * @param storage
    */
   public CmisSchema(TypeManager typeManager)
   {
      super();
      this.typeManager = typeManager;
   }

   /**
    * @see org.xcmis.search.content.Schema#getTable(org.xcmis.search.model.source.SelectorName)
    */
   public Table getTable(SelectorName name)
   {
      TypeDefinition typeDefinition = typeManager.getTypeDefinition(name.getName(), true);
      if (typeDefinition != null)
      {
         return new CmisTableDefinition(name, typeDefinition);
      }
      return null;
   }

   private static class CmisTableDefinition implements Table
   {
      private final SelectorName name;

      private final TypeDefinition typeDefinition;

      /**
       * @param typeDefinition
       * @param typeId
       * @param typeManager
       */
      public CmisTableDefinition(SelectorName name, TypeDefinition typeDefinition)
      {
         super();
         this.name = name;
         this.typeDefinition = typeDefinition;
      }

      /**
       * @see org.xcmis.search.content.Schema.Table#getColumn(java.lang.String)
       */
      public Column getColumn(String name)
      {
         PropertyDefinition<?> propertyDefinition = typeDefinition.getPropertyDefinition(name);
         if (propertyDefinition != null)
         {
            return new InMemoryColumn(name, PROPERTY_TYPES_MAP.get(propertyDefinition.getPropertyType()).toString(),
               true);
         }
         return null;
      }

      /**
       * @see org.xcmis.search.content.Schema.Table#getColumns()
       */
      public List<Column> getColumns()
      {
         Collection<PropertyDefinition<?>> props = typeDefinition.getPropertyDefinitions();
         List<Column> result = new ArrayList<Column>(props.size());
         for (PropertyDefinition<?> propertyDefinition : props)
         {
            result.add(new InMemoryColumn(propertyDefinition.getQueryName(), PROPERTY_TYPES_MAP.get(
               propertyDefinition.getPropertyType()).toString(), true));
         }

         return result;
      }

      /**
       * @see org.xcmis.search.content.Schema.Table#getColumnsByName()
       */
      public Map<String, Column> getColumnsByName()
      {
         Collection<PropertyDefinition<?>> props = typeDefinition.getPropertyDefinitions();
         Map<String, Column> result = new HashMap<String, Column>(props.size());
         for (PropertyDefinition<?> propertyDefinition : props)
         {
            result.put(propertyDefinition.getQueryName(), new InMemoryColumn(propertyDefinition.getQueryName(),
               PROPERTY_TYPES_MAP.get(propertyDefinition.getPropertyType()).toString(), true));
         }

         return result;
      }

      /**
       * @see org.xcmis.search.content.Schema.Table#getName()
       */
      public SelectorName getName()
      {
         return name;
      }
   }
}
