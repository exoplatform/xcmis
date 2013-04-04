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

import org.apache.tika.config.TikaConfig;
import org.apache.tika.exception.TikaException;
import org.apache.tika.mime.MimeTypeException;
import org.xcmis.search.content.interceptors.QueryableIndexStorage;

import java.io.IOException;

/**
 * Search service index configuration
 */
public class IndexConfiguration
{
   /**
    * Default implementation of {@link QueryableIndexStorage}.
    */
   public static final String DEFAULT_QUERYABLEINDEXSTORAGE =
      "org.xcmis.search.lucene.InMemoryLuceneQueryableIndexStorage";

   /**
    * Path where index should be stored.
    */
   private final String indexDir;

   /**
    * Parent uuid of root element.
    */
   private final String rootParentUuid;

   /**
    * Uuid of root element.
    */
   private final String rootUuid;

   /**
    * Tika configuration.
    */
   private final TikaConfig tikaConfiguration;

   /**
    * Implementation of {@link QueryableIndexStorage}.
    */
   private final String queryableIndexStorage;

   /**
    * In memory index storage with default Tika configuration
    * 
    * @param rootParentUuid
    * @param rootUuid
    * @throws IndexConfigurationException
    * @throws MimeTypeException
    * @throws IOException
    */
   public IndexConfiguration(String rootUuid) throws IndexConfigurationException, MimeTypeException, IOException, TikaException
   {
      this(null, "", rootUuid, DEFAULT_QUERYABLEINDEXSTORAGE, new TikaConfig());
   }

   /**
    * In memory index storage with default Tika configuration
    * 
    * @param rootParentUuid
    * @param rootUuid
    * @throws IndexConfigurationException
    * @throws MimeTypeException
    * @throws IOException
    */
   public IndexConfiguration(String rootParentUuid, String rootUuid) throws IndexConfigurationException,
      MimeTypeException, IOException, TikaException
   {
      this(null, rootParentUuid, rootUuid, DEFAULT_QUERYABLEINDEXSTORAGE, new TikaConfig());
   }

   public IndexConfiguration(String indexDir, String rootParentUuid, String rootUuid)
      throws IndexConfigurationException, MimeTypeException, IOException, TikaException
   {
      this(indexDir, rootParentUuid, rootUuid, "org.xcmis.search.lucene.LuceneQueryableIndexStorage", new TikaConfig());
   }

   public IndexConfiguration(String indexDir, String rootParentUuid, String rootUuid, String queryableIndexStorage,
      TikaConfig tikaConfiguration) throws IndexConfigurationException, MimeTypeException, IOException, TikaException
   {
      super();
      this.indexDir = indexDir;
      this.rootParentUuid = rootParentUuid;
      this.rootUuid = rootUuid;
      this.tikaConfiguration = tikaConfiguration;
      this.queryableIndexStorage = queryableIndexStorage;
   }

   /**
    * @return the indexDir
    */
   public String getIndexDir()
   {
      return indexDir;
   }

   /**
    * @return the queryableIndexStorage
    */
   public String getQueryableIndexStorage()
   {
      return queryableIndexStorage;
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
    * @return the tikaConfiguration
    */
   public TikaConfig getTikaConfiguration()
   {
      return tikaConfiguration;
   }

}
