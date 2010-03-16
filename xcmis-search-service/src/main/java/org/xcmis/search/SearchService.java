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
import org.xcmis.search.content.command.InvocationContext;
import org.xcmis.search.content.command.index.ApplyChangesToTheIndexCommand;
import org.xcmis.search.content.command.query.ExecuteSelectorCommand;
import org.xcmis.search.content.command.query.ParseQueryCommand;
import org.xcmis.search.content.command.query.ProcessQueryCommand;
import org.xcmis.search.content.interceptors.InterceptorChain;
import org.xcmis.search.content.interceptors.QueryProcessorInterceptor;
import org.xcmis.search.content.interceptors.QueryableIndexStorage;
import org.xcmis.search.model.Query;
import org.xcmis.search.query.optimize.CriteriaBasedOptimizer;
import org.xcmis.search.query.plan.SimplePlaner;
import org.xcmis.search.result.ScoredRow;

import java.util.List;
import java.util.Map;

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
    * Default invocation context;
    */
   private InvocationContext invocationContext;

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

      interceptorChain.addBeforeInterceptor(new QueryProcessorInterceptor(new SimplePlaner(),
         new CriteriaBasedOptimizer()), QueryableIndexStorage.class);
      // parse statements
      addQueryParserInterceptor(interceptorChain);
   }

   /**
    * Execute query of the given type
    * 
    * @param query
    * @param type
    * @return
    * @throws InvalidQueryException
    */
   @SuppressWarnings("unchecked")
   public List<ScoredRow> execute(Query query, Map<String, Object> bindVariablesValues) throws InvalidQueryException
   {
      ProcessQueryCommand processQueryCommand = new ProcessQueryCommand(query, bindVariablesValues);

      try
      {
         return (List<ScoredRow>)interceptorChain.invoke(getInvocationContext(), processQueryCommand);
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
   public Query parse(String query, String type) throws InvalidQueryException
   {
      ParseQueryCommand submitStatementCommand = new ParseQueryCommand(query, type);

      try
      {
         return (Query)interceptorChain.invoke(getInvocationContext(), submitStatementCommand);
      }
      catch (Throwable e)
      {
         throw new InvalidQueryException(e.getLocalizedMessage(), e);
      }
   }

   /**
    * @return the invocationContext
    */
   public InvocationContext getInvocationContext()
   {
      return invocationContext == null ? configuration.getDefaultInvocationContext() : invocationContext;
   }

   /**
    * @param invocationContext the invocationContext to set
    */
   public void setInvocationContext(InvocationContext invocationContext)
   {
      this.invocationContext = invocationContext;
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
