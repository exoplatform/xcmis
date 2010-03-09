/*
 * Copyright (C) 2009 eXo Platform SAS.
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

import org.xcmis.search.SearchServiceException;
import org.xcmis.search.result.ScoredRow;

import java.io.IOException;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: NativeQuery.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public interface NativeQuery<Q>
{
   /**
    * Filter query results.
    * 
    * @param results
    * @return
    * @throws IOException
    * @throws InvalidQueryException
    * @throws RepositoryException
    */
   // TODO TODO check if it possible to take a list of filters as params
   // or move this functionality to other class
   public List<ScoredRow> filter(List<ScoredRow> results) throws IOException, SearchServiceException;

   /**
    * Search.
    * 
    * @param indexSearcher
    * @return
    * @throws IOException
    * @throws InvalidQueryException
    * @throws RepositoryException
    */
   public List<ScoredRow> search(SearchIndexingService<Q> indexSearcher) throws IOException, SearchServiceException;

   /**
    * Sort query results.
    * 
    * @param results
    * @return
    * @throws IOException
    * @throws InvalidQueryException
    */
   // TODO check if it possible to take a list of sorters as params
   // or move this functionality to other class
   public List<ScoredRow> sort(List<ScoredRow> results) throws IOException, SearchServiceException;
}
