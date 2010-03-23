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

import org.apache.lucene.document.Document;
import org.xcmis.search.content.ContentIndexer;
import org.xcmis.search.lucene.index.IndexRecoverService;
import org.xcmis.search.lucene.index.IndexRestoreService;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34027 2009-07-15 23:26:43Z
 *          aheritier $
 */
public class IndexConfurationImpl implements IndexConfuguration
{
   private ContentIndexer<Document> contentIndexer;

   private String indexDir;

   private IndexRecoverService indexRecoverService;

   private IndexRestoreService indexRestoreService;

   /**
    * @see org.xcmis.search.config.IndexConfuguration#getContentIndexer()
    */
   public ContentIndexer<Document> getContentIndexer()
   {
      return contentIndexer;
   }

   /**
    * {@inheritDoc}
    */
   public String getIndexDir()
   {
      return indexDir;
   }

   /**
    * @return the indexRecoverService
    */
   public IndexRecoverService getIndexRecoverService()
   {
      return indexRecoverService;
   }

   /**
    * @see org.xcmis.search.config.IndexConfuguration#getIndexRestoreService()
    */
   public IndexRestoreService getIndexRestoreService()
   {
      return indexRestoreService;
   }

   /**
    * @param contentIndexer the contentIndexer to set
    */
   public void setContentIndexer(ContentIndexer<Document> contentIndexer)
   {
      this.contentIndexer = contentIndexer;
   }

   /**
    * @param indexDir
    *           the indexDir to set
    */
   public void setIndexDir(String indexDir)
   {
      this.indexDir = indexDir;
   }

   /**
    * @param indexRecoverService
    *           the indexRecoverService to set
    */
   public void setIndexRecoverService(IndexRecoverService indexRecoverService)
   {
      this.indexRecoverService = indexRecoverService;
   }

   /**
    * @param indexRestoreService the indexRestoreService to set
    */
   public void setIndexRestoreService(IndexRestoreService indexRestoreService)
   {
      this.indexRestoreService = indexRestoreService;
   }

}
