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

import org.xcmis.search.content.command.InvocationContext;
import org.xcmis.search.content.interceptors.ContentReaderInterceptor;
import org.xcmis.search.lucene.content.VirtualTableResolver;
import org.xcmis.search.value.NameConverter;
import org.xcmis.search.value.PathSplitter;

/**
 * Configuration of search service.
 */
public class SearchServiceConfiguration
{
   /**
    * Mandatory parameter for service. To be able to read content from base
    * storage.
    */
   private ContentReaderInterceptor contentReader;

   /**
    * Reselve selector names to lucene querys.
    */
   private VirtualTableResolver tableResolver;

   /**
    * Convert one Sting name to other String name.
    */
   private NameConverter nameConverter;

   /**
    * Split path  string to names
    */
   private PathSplitter pathSplitter;

   /**
    * Index configuration.
    */
   private IndexConfuguration indexConfuguration;

   /**
    * @return the defaultInvocationContext
    */
   public InvocationContext getDefaultInvocationContext()
   {
      return defaultInvocationContext;
   }

   /**
    * @param defaultInvocationContext the defaultInvocationContext to set
    */
   public void setDefaultInvocationContext(InvocationContext defaultInvocationContext)
   {
      this.defaultInvocationContext = defaultInvocationContext;
   }

   /**
    * Default invocation context;
    */
   private InvocationContext defaultInvocationContext;

   public ContentReaderInterceptor getContentReader()
   {
      return contentReader;
   }

   /**
    * @return the indexConfuguration
    */
   public IndexConfuguration getIndexConfuguration()
   {
      return indexConfuguration;
   }

   /**
    * @return
    */
   public NameConverter getNameConverter()
   {
      return nameConverter;
   }

   /**
    * @return
    */
   public PathSplitter getPathSplitter()
   {
      return pathSplitter;
   }

   /**
    * @return
    */
   public VirtualTableResolver getTableResolver()
   {
      return tableResolver;
   }

   /**
    * @param contentReader the contentReader to set
    */
   public void setContentReader(ContentReaderInterceptor contentReader)
   {
      this.contentReader = contentReader;
   }

   /**
    * @param indexConfuguration the indexConfuguration to set
    */
   public void setIndexConfuguration(IndexConfuguration indexConfuguration)
   {
      this.indexConfuguration = indexConfuguration;
   }

   /**
    * @param nameConverter the nameConverter to set
    */
   public void setNameConverter(NameConverter nameConverter)
   {
      this.nameConverter = nameConverter;
   }

   /**
    * @param pathSplitter the pathSplitter to set
    */
   public void setPathSplitter(PathSplitter pathSplitter)
   {
      this.pathSplitter = pathSplitter;
   }

   /**
    * @param tableResolver the tableResolver to set
    */
   public void setTableResolver(VirtualTableResolver tableResolver)
   {
      this.tableResolver = tableResolver;
   }
}
