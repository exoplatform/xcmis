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
package org.xcmis.search;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.index.IndexException;
import org.xcmis.search.model.ordering.Ordering;
import org.xcmis.search.result.ResultFilter;
import org.xcmis.search.result.ResultSorter;
import org.xcmis.search.result.ScoredRow;

import java.io.IOException;
import java.util.Collections;
import java.util.List;



/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: SingleSourceNativeQueryImpl.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class SingleSourceNativeQueryImpl<Q> implements SingleSourceNativeQuery<Q>
{
   private long limit;

   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(this.getClass().getName());

   private long offset;

   private final Ordering[] orderings;

   private final ResultFilter postFilter;

   private final ResultSorter postSorter;

   private final Q query;

   private final String selectorName;

   /**
    * @param query
    * @param selectorName
    * @param orderProperties
    * @param getOrderSpecs
    * @param limit
    * @param offset
    */
   public SingleSourceNativeQueryImpl(final Q query, final String selectorName, final Ordering[] orderings,
      final ResultSorter postSorter, final ResultFilter postFilter, final long limit, final long offset)
   {
      super();
      this.query = query;
      this.selectorName = selectorName;
      this.orderings = orderings;
      this.postSorter = postSorter;
      this.postFilter = postFilter;
      this.limit = limit;
      this.offset = offset;
   }

   /**
    * {@inheritDoc}
    */
   public List<ScoredRow> filter(final List<ScoredRow> results) throws IOException, SearchServiceException
   {
      return postFilter != null ? postFilter.filter(results) : results;
   }

   /**
    * {@inheritDoc}
    */
   public long getLimit()
   {
      return this.limit;
   }

   /**
    * {@inheritDoc}
    */
   public long getOffset()
   {
      return this.offset;
   }

   public Ordering[] getOrderings()
   {
      return this.orderings;
   }

   /**
    * {@inheritDoc}
    */
   public Q getQuery()
   {
      return this.query;
   }

   /**
    * {@inheritDoc}
    */
   public String getSelectorName()
   {
      return this.selectorName;
   }

   /**
    * {@inheritDoc}
    */
   public List<ScoredRow> search(final SearchIndexingService<Q> indexSearcher) throws IndexException
   {
      return indexSearcher.search(this);
   }

   /**
    * {@inheritDoc}
    */
   public void setLimit(final long limit)
   {
      this.limit = limit;

   }

   /**
    * {@inheritDoc}
    */
   public void setOffset(final long offset)
   {
      this.offset = offset;
   }

   /**
    * {@inheritDoc}
    */
   public List<ScoredRow> sort(final List<ScoredRow> results) throws IOException, SearchServiceException
   {
      if (postSorter != null)
      {
         Collections.sort(results, postSorter);
      }
      return results;
   }

   /**
    * {@inheritDoc}
    */
   public ResultFilter getPostFilter()
   {
      return postFilter;
   }

   /**
    * {@inheritDoc}
    */
   public ResultSorter getPostSorter()
   {
      return postSorter;
   }

}
