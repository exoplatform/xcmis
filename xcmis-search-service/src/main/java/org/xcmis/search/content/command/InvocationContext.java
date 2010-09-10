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
package org.xcmis.search.content.command;

import org.xcmis.search.content.Schema;
import org.xcmis.search.lucene.content.VirtualTableResolver;
import org.xcmis.search.value.NameConverter;
import org.xcmis.search.value.PathSplitter;
import org.xcmis.search.value.SlashSplitter;
import org.xcmis.search.value.ToStringNameConverter;

/**
 * Context of command invocation.
 */
public class InvocationContext
{
   /**
    * Content Schema.
    */
   private final Schema schema;

   /**
    * Reselve selector names to lucene querys.
    */
   private final VirtualTableResolver tableResolver;

   /**
    * Convert one Sting name to other String name.
    */
   private final NameConverter nameConverter;

   /**
    * Split path string to names.
    */
   private final PathSplitter pathSplitter;

   public InvocationContext(Schema schema, VirtualTableResolver tableResolver)
   {
      this.schema = schema;
      this.tableResolver = tableResolver;
      this.nameConverter = new ToStringNameConverter();
      this.pathSplitter = new SlashSplitter();

   }

   public InvocationContext(Schema schema, VirtualTableResolver tableResolver, NameConverter nameConverter,
      PathSplitter pathSplitter)
   {
      super();
      this.schema = schema;
      this.tableResolver = tableResolver;
      this.nameConverter = nameConverter;
      this.pathSplitter = pathSplitter;
   }

   /**
    * @return the nameConverter
    */
   public NameConverter getNameConverter()
   {
      return nameConverter;
   }

   /**
    * @return the pathSplitter
    */
   public PathSplitter getPathSplitter()
   {
      return pathSplitter;
   }

   /**
    * @return the schema
    */
   public Schema getSchema()
   {
      return schema;
   }

   /**
    * @return the tableResolver
    */
   public VirtualTableResolver getTableResolver()
   {
      return tableResolver;
   }
}
