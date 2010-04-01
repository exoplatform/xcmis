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
package org.xcmis.search.query;

import org.xcmis.search.InvalidQueryException;
import org.xcmis.search.content.command.InvocationContext;
import org.xcmis.search.model.Query;
import org.xcmis.search.result.ScoredRow;

import java.util.List;
import java.util.Map;

/**
  * Content searcher interface.
 */
public interface Searcher
{
   /**
    * Execute given query with empty map of bind variables and default InvocationContext  
    * @param query
    * @return
    * @throws InvalidQueryException
    */
   public List<ScoredRow> execute(Query query) throws InvalidQueryException, QueryExecutionException;

   /**
    * Execute given query with  default InvocationContext  
    * @param query
    * @return
    * @throws InvalidQueryException
    */

   public List<ScoredRow> execute(Query query, Map<String, Object> bindVariablesValues) throws InvalidQueryException,
      QueryExecutionException;

   /**
    * Execute query.  
    * @param query
    * @return
    * @throws InvalidQueryException
    */
   public List<ScoredRow> execute(Query query, Map<String, Object> bindVariablesValues,
      InvocationContext invocationContext) throws InvalidQueryException, QueryExecutionException;

}
