/*
 * Copyright (C) 2010 eXo Platform SAS.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.xcmis.search.config;

import org.xcmis.search.content.Schema;
import org.xcmis.search.content.command.InvocationContext;
import org.xcmis.search.content.interceptors.ContentReaderInterceptor;
import org.xcmis.search.lucene.content.VirtualTableResolver;
import org.xcmis.search.value.NameConverter;
import org.xcmis.search.value.PathSplitter;

/**
 * Configuration of search service.
 */
public class SearchServiceConfiguration extends InvocationContext
{
   /**
    * Mandatory parameter for service. To be able to read content from base
    * storage.
    */
   private final ContentReaderInterceptor contentReader;

   /**
    * Index configuration.
    */
   private final IndexConfiguration indexConfiguration;

   public SearchServiceConfiguration(Schema schema, VirtualTableResolver tableResolver,
      ContentReaderInterceptor contentReader, IndexConfiguration indexConfiguration)
   {
      super(schema, tableResolver);
      this.contentReader = contentReader;
      this.indexConfiguration = indexConfiguration;
   }

   public SearchServiceConfiguration(Schema schema, VirtualTableResolver tableResolver, NameConverter nameConverter,
      PathSplitter pathSplitter, ContentReaderInterceptor contentReader, IndexConfiguration indexConfiguration)
   {
      super(schema, tableResolver, nameConverter, pathSplitter);
      this.contentReader = contentReader;
      this.indexConfiguration = indexConfiguration;
   }

   public ContentReaderInterceptor getContentReader()
   {
      return contentReader;
   }

   /**
    * @return the indexConfiguration
    */
   public IndexConfiguration getIndexConfuguration()
   {
      return indexConfiguration;
   }

}
