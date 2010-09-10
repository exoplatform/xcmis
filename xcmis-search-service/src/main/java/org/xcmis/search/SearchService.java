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
import org.xcmis.search.content.ContentEntry;
import org.xcmis.search.content.ContentModificationListener;
import org.xcmis.search.content.IndexModificationException;
import org.xcmis.search.content.command.InvocationContext;
import org.xcmis.search.content.command.index.ModifyIndexCommand;
import org.xcmis.search.content.command.query.ExecuteSelectorCommand;
import org.xcmis.search.content.command.query.ProcessQueryCommand;
import org.xcmis.search.content.interceptors.ContentReaderInterceptor;
import org.xcmis.search.content.interceptors.InterceptorChain;
import org.xcmis.search.content.interceptors.QueryProcessorInterceptor;
import org.xcmis.search.content.interceptors.QueryableIndexStorage;
import org.xcmis.search.model.Query;
import org.xcmis.search.query.QueryExecutionException;
import org.xcmis.search.query.Searcher;
import org.xcmis.search.query.optimize.CriteriaBasedOptimizer;
import org.xcmis.search.query.plan.SimplePlaner;
import org.xcmis.search.result.ScoredRow;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Main entry point to the search service.
 * 
 */
public class SearchService implements Startable, ContentModificationListener, Searcher
{

   /**
    * Configuration of search service.
    */
   protected final SearchServiceConfiguration configuration;

   private final InterceptorChain interceptorChain;

   /**
    * Default invocation context.
    */
   private final InvocationContext defaultInvocationContext;

   /**
    * @param configuration
    *           SearchServiceConfiguration
    * @throws SearchServiceException
    */
   public SearchService(SearchServiceConfiguration configuration) throws SearchServiceException
   {
      Validate.notNull(configuration, "The configuration argument may not be null");
      Validate.notNull(configuration.getContentReader(), "The configuration.getContentReader()  may not be null");
      this.configuration = configuration;
      this.interceptorChain = new InterceptorChain(configuration.getContentReader());
      this.defaultInvocationContext = configuration;

      addQueryableIndexStorageInterceptor(interceptorChain);

      interceptorChain.addBeforeInterceptor(new QueryProcessorInterceptor(new SimplePlaner(),
         new CriteriaBasedOptimizer()), QueryableIndexStorage.class);

   }

   /**
    * Execute query.
    * 
    * @param query
    *           Query
    * @return
    * @throws InvalidQueryException
    *            , QueryExecutionException
    */
   @SuppressWarnings("unchecked")
   public List<ScoredRow> execute(Query query) throws InvalidQueryException, QueryExecutionException
   {
      if (defaultInvocationContext == null)
      {
         throw new QueryExecutionException("DefaultInvocationContext can't be null");
      }
      return execute(query, Collections.EMPTY_MAP);
   }

   /**
    * Execute query.
    * 
    * @param query
    *           Query
    * @param bindVariablesValues
    *           Map<String, Object>
    * @return List<ScoredRow>
    * @throws InvalidQueryException
    *            , QueryExecutionException
    */
   public List<ScoredRow> execute(Query query, Map<String, Object> bindVariablesValues) throws InvalidQueryException,
      QueryExecutionException
   {
      if (defaultInvocationContext == null)
      {
         throw new QueryExecutionException("DefaultInvocationContext can't be null");
      }
      return execute(query, bindVariablesValues, defaultInvocationContext);
   }

   /**
    * @see org.xcmis.search.query.Searcher#execute(org.xcmis.search.model.Query,
    *      java.util.Map, org.xcmis.search.content.command.InvocationContext)
    */
   @SuppressWarnings("unchecked")
   public List<ScoredRow> execute(Query query, Map<String, Object> bindVariablesValues,
      InvocationContext invocationContext) throws InvalidQueryException, QueryExecutionException
   {
      ProcessQueryCommand processQueryCommand = new ProcessQueryCommand(query, bindVariablesValues);

      try
      {
         return (List<ScoredRow>)interceptorChain.invoke(invocationContext, processQueryCommand);
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
      interceptorChain.start();
   }

   /**
    * @see org.xcmis.search.Startable#stop()
    */
   public void stop()
   {
      interceptorChain.stop();
   }

   /**
    * @see org.xcmis.search.content.ContentModificationListener#update(org.xcmis.search.content.ContentEntry,
    *      java.lang.String)
    */
   public void update(ContentEntry addedEntry, String removedEntry) throws IndexModificationException
   {
      List<ContentEntry> addedEntries = new ArrayList<ContentEntry>(1);
      if (addedEntry != null)
      {
         addedEntries.add(addedEntry);
      }
      Set<String> removedSet = new HashSet<String>(1);
      if (removedEntry != null)
      {
         removedSet.add(removedEntry);
      }
      update(addedEntries, removedSet);
   }

   /**
    * @see org.xcmis.search.content.ContentModificationListener#update(java.util.List,
    *      java.util.Set)
    */
   public void update(List<ContentEntry> addedEntries, Set<String> removedEntries) throws IndexModificationException
   {
      update(addedEntries, removedEntries, defaultInvocationContext);
   }

   /**
    * @see org.xcmis.search.content.ContentModificationListener#update(java.util.List,
    *      java.util.Set, org.xcmis.search.content.command.InvocationContext)
    */
   public void update(List<ContentEntry> addedEntries, Set<String> removedEntries, InvocationContext invocationContext)
      throws IndexModificationException
   {
      ModifyIndexCommand modifyIndexCommand = new ModifyIndexCommand(addedEntries, removedEntries);

      try
      {
         interceptorChain.invoke(invocationContext, modifyIndexCommand);
      }
      catch (IndexModificationException e)
      {
         throw e;
      }
      catch (Throwable e)
      {
         throw new IndexModificationException(e.getLocalizedMessage(), e);
      }

   }

   /**
    * Add interceptors that handle {@link ModifyIndexCommand} and
    * {@link ExecuteSelectorCommand}
    * 
    * @param interceptorChain
    *           InterceptorChain
    * @throws SearchServiceException
    *            if error occurs
    */
   protected void addQueryableIndexStorageInterceptor(InterceptorChain interceptorChain) throws SearchServiceException
   {
      String className = configuration.getIndexConfuguration().getQueryableIndexStorage();
      try
      {
         Class<?> queryableIndexStorageClass = Class.forName(className);
         if (QueryableIndexStorage.class.isAssignableFrom(queryableIndexStorageClass))
         {

            Constructor<QueryableIndexStorage> constructor =
               (Constructor<QueryableIndexStorage>)queryableIndexStorageClass.getConstructor(configuration.getClass());
            QueryableIndexStorage queryableIndexStorage = constructor.newInstance(configuration);
            interceptorChain.addBeforeInterceptor(queryableIndexStorage, ContentReaderInterceptor.class);
         }
         else
         {
            throw new SearchServiceException(className + "is no assignable from " + QueryableIndexStorage.class);
         }
      }
      catch (ClassNotFoundException e)
      {
         throw new SearchServiceException(e.getLocalizedMessage(), e);
      }
      catch (SecurityException e)
      {
         throw new SearchServiceException(e.getLocalizedMessage(), e);
      }
      catch (NoSuchMethodException e)
      {
         throw new SearchServiceException(e.getLocalizedMessage(), e);
      }
      catch (IllegalArgumentException e)
      {
         throw new SearchServiceException(e.getLocalizedMessage(), e);
      }
      catch (InstantiationException e)
      {
         throw new SearchServiceException(e.getLocalizedMessage(), e);
      }
      catch (IllegalAccessException e)
      {
         throw new SearchServiceException(e.getLocalizedMessage(), e);
      }
      catch (InvocationTargetException e)
      {
         throw new SearchServiceException(e.getTargetException());
      }

   }
}
