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
package org.xcmis.search.lucene;

import org.xcmis.search.SearchService;
import org.xcmis.search.SearchServiceException;
import org.xcmis.search.config.SearchServiceConfiguration;
import org.xcmis.search.content.interceptors.ContentReaderInterceptor;
import org.xcmis.search.content.interceptors.InterceptorChain;

/**
 *  Basic implementation on Lucene storage.
 */
public class LuceneSearchService extends SearchService
{

   /**
    * @param configuration
    * @throws SearchServiceException 
    */
   public LuceneSearchService(SearchServiceConfiguration configuration) throws SearchServiceException
   {
      super(configuration);
   }

   /**
    * @see org.xcmis.search.SearchService#addIndexStarageInterceptor(org.xcmis.search.content.interceptors.InterceptorChain)
    */
   @Override
   protected void addQueryableIndexStorageInterceptor(InterceptorChain interceptorChain) throws SearchServiceException
   {

      interceptorChain.addBeforeInterceptor(new LuceneQueryableIndexStorage(configuration),
         ContentReaderInterceptor.class);

   }

}
