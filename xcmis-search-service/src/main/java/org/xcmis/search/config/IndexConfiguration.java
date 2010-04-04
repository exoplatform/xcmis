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
import org.xcmis.search.lucene.index.IndexRecoverService;
import org.xcmis.search.lucene.index.IndexRestoreService;

/**
 *  Search service index configuration
 */
public class IndexConfiguration
{
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
    * Path to the Tika configuration
    */
   private String tikaConfiguration;
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
    * @return the tikaConfiguration
    */
   public String getTikaConfiguration()
   {
      return tikaConfiguration;
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
   /**
    * @param tikaConfiguration the tikaConfiguration to set
    */
   public void setTikaConfiguration(String tikaConfiguration)
   {
      this.tikaConfiguration = tikaConfiguration;
   }

}
