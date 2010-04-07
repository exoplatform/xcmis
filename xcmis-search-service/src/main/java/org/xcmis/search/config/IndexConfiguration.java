/*
 * Copyright (C) 2009 eXo Platform SAS.
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

import org.exoplatform.services.document.DocumentReaderService;
import org.xcmis.search.content.interceptors.QueryableIndexStorage;

/**
 *  Search service index configuration
 */
public class IndexConfiguration
{
   /**
    * Default implementation of {@link QueryableIndexStorage}
    */
   public static final String DEFAULT_QUERYABLEINDEXSTORAGE = "org.xcmis.search.lucene.LuceneQueryableIndexStorage";

   /**
    * Path where index should be stored 
    */
   private String indexDir;

   /**
    * Parent uuid of root element 
    */
   private String rootParentUuid;

   /**
    * Uuid of root element
    */
   private String rootUuid;

   /**
    * Implementation of {@link QueryableIndexStorage}
    */
   private String queryableIndexStorage = DEFAULT_QUERYABLEINDEXSTORAGE;

   /**
    * @return the queryableIndexStorage
    */
   public String getQueryableIndexStorage()
   {
      return queryableIndexStorage;
   }

   /**
    * @param queryableIndexStorage the queryableIndexStorage to set
    */
   public void setQueryableIndexStorage(String queryableIndexStorage)
   {
      this.queryableIndexStorage = queryableIndexStorage;
   }

   /**
    * Text extraction service.
    */
   private DocumentReaderService documentReaderService;

   /**
    * @return the documentReaderService
    */
   public DocumentReaderService getDocumentReaderService()
   {
      return documentReaderService;
   }

   /**
    * @param documentReaderService the documentReaderService to set
    */
   public void setDocumentReaderService(DocumentReaderService documentReaderService)
   {
      this.documentReaderService = documentReaderService;
   }

   /**
    * @return the indexDir
    */
   public String getIndexDir()
   {
      return indexDir;
   }

   /**
    * @return the rootParentUuid
    */
   public String getRootParentUuid()
   {
      return rootParentUuid;
   }

   /**
    * @return the rootUuid
    */
   public String getRootUuid()
   {
      return rootUuid;
   }

   /**
    * @param indexDir the indexDir to set
    */
   public void setIndexDir(String indexDir)
   {
      this.indexDir = indexDir;
   }

   /**
    * @param rootParentUuid the rootParentUuid to set
    */
   public void setRootParentUuid(String rootParentUuid)
   {
      this.rootParentUuid = rootParentUuid;
   }

   /**
    * @param rootUuid the rootUuid to set
    */
   public void setRootUuid(String rootUuid)
   {
      this.rootUuid = rootUuid;
   }

}
