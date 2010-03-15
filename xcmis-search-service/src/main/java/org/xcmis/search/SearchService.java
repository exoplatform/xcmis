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
package org.xcmis.search;

import org.apache.commons.lang.Validate;
import org.xcmis.search.config.SearchServiceConfiguration;
import org.xcmis.search.content.command.index.ApplyChangesToTheIndexCommand;
import org.xcmis.search.content.command.query.ExecuteSelectorCommand;
import org.xcmis.search.content.command.query.ParseQueryCommand;
import org.xcmis.search.content.command.query.ProcessQueryCommand;
import org.xcmis.search.content.command.query.SubmitStatementCommand;
import org.xcmis.search.content.interceptors.InterceptorChain;
import org.xcmis.search.content.interceptors.StatementProcessorInterceptor;
import org.xcmis.search.model.Query;
import org.xcmis.search.query.QueryResults;

/**
 * Main entry point to the search service.
 * 
 */
public abstract class SearchService implements Startable
{
   /**
    * Configuration of search service.
    */
   protected final SearchServiceConfiguration configuration;

   private final InterceptorChain interceptorChain;

   /**
    * @param configuration
    * @throws SearchServiceException 
    */
   public SearchService(SearchServiceConfiguration configuration) throws SearchServiceException
   {
      Validate.notNull(configuration, "The configuration argument may not be null");
      Validate.notNull(configuration.getContentReader(), "The configuration.getContentReader()  may not be null");
      this.configuration = configuration;
      this.interceptorChain = new InterceptorChain(configuration.getContentReader());

      addQueryableIndexStorageInterceptor(interceptorChain);

      addQueryParserInterceptor(interceptorChain);
      // parse and execute statements
      interceptorChain.setFirstInChain(new StatementProcessorInterceptor());
   }

   /**
    * Execute query of the given type
    * 
    * @param query
    * @param type
    * @return
    * @throws InvalidQueryException
    */
   public QueryResults execute(String query, String type) throws InvalidQueryException
   {
      SubmitStatementCommand submitStatementCommand = new SubmitStatementCommand(query, type);

      try
      {
         return (QueryResults)interceptorChain.invoke(null, submitStatementCommand);
      }
      catch (Throwable e)
      {
         throw new InvalidQueryException(e.getLocalizedMessage(), e);
      }
   }

   /**
    * Execute query of the given type
    * 
    * @param query
    * @param type
    * @return
    * @throws InvalidQueryException
    */
   public QueryResults execute(Query query) throws InvalidQueryException
   {
      ProcessQueryCommand processQueryCommand = new ProcessQueryCommand(query);

      try
      {
         return (QueryResults)interceptorChain.invoke(null, processQueryCommand);
      }
      catch (Throwable e)
      {
         throw new InvalidQueryException(e.getLocalizedMessage(), e);
      }
   }

   /**
    * @see org.xcmis.search.Startable#start()
    */

   public void start()
   {

   }

   /**
    * @see org.xcmis.search.Startable#stop()
    */
   public void stop()
   {

   }

   /**
    * Add interceptors that handle {@link ApplyChangesToTheIndexCommand} and
    * {@link ExecuteSelectorCommand}
    * 
    * @param interceptorChain
    * @throws SearchServiceException 
    */
   protected abstract void addQueryableIndexStorageInterceptor(InterceptorChain interceptorChain)
      throws SearchServiceException;

   /**
    * Add interceptors that handle {@link ParseQueryCommand}.
    * 
    * @param interceptorChain
    */
   protected abstract void addQueryParserInterceptor(InterceptorChain interceptorChain);

}
