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

import org.xcmis.search.content.Schema;
import org.xcmis.search.lucene.content.SchemaTableResolver;
import org.xcmis.search.value.NameConverter;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.QueryNameTypeManager;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.utils.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * Class override getSubTypes method.
 *
 */
public class CmisSchemaTableResolver extends SchemaTableResolver
{

   private final QueryNameTypeManager typeManager;

   private static final Logger LOG = Logger.getLogger(CmisSchemaTableResolver.class);

   /**
    * @param nameConverter
    * @param schema
    */
   public CmisSchemaTableResolver(NameConverter nameConverter, Schema schema, QueryNameTypeManager typeManager)
   {
      super(nameConverter, schema);
      this.typeManager = typeManager;
   }

   /**
    * @see org.xcmis.search.lucene.content.SchemaTableResolver#getSubTypes(java.lang.String)
    */
   @Override
   protected Set<String> getSubTypes(String tableName)
   {
      Set<String> subTypes = new HashSet<String>();

      try
      {
         TypeDefinition type = typeManager.getTypeDefinitionByQueryName(tableName, false);
         ItemsIterator<TypeDefinition> typeChildren = typeManager.getTypeChildren(type.getId(), false);
         while (typeChildren.hasNext())
         {
            TypeDefinition typeDefinition = typeChildren.next();
            subTypes.add(typeDefinition.getQueryName());
         }
      }
      catch (TypeNotFoundException tnf)
      {
         LOG.error(tnf.getMessage(), tnf);
      }

      return subTypes;
   }
}
