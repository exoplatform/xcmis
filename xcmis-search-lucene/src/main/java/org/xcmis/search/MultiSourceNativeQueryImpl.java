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
import org.xcmis.search.result.JoinCollector;
import org.xcmis.search.result.ResultFilter;
import org.xcmis.search.result.ResultSorter;
import org.xcmis.search.result.ScoredRow;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.qom.Ordering;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id$
 */
public class MultiSourceNativeQueryImpl<Q> implements MultiSourceNativaQuery<Q>
{
   private final JoinCollector joinCollector;

   private final NativeQuery<Q> leftSource;

   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(this.getClass().getName());

   private final Ordering[] orderings;

   private final ResultFilter postFilter;

   private final ResultSorter postSorter;

   private final NativeQuery<Q> rightSource;

   /**
    * @param leftSource
    * @param rightSource
    * @param orderings
    * @param joinCondition
    * @param joinType
    */
   public MultiSourceNativeQueryImpl(final NativeQuery<Q> leftSource, final NativeQuery<Q> rightSource,
      final JoinCollector joinCollector, final Ordering[] orderings, final ResultSorter postSorter,
      final ResultFilter postFilter)
   {
      super();
      this.leftSource = leftSource;
      this.rightSource = rightSource;
      this.joinCollector = joinCollector;
      this.orderings = orderings;
      this.postSorter = postSorter;
      this.postFilter = postFilter;

   }

   /**
    * {@inheritDoc}
    */
   public List<ScoredRow> filter(final List<ScoredRow> results) throws IOException, InvalidQueryException
   {
      return this.postFilter != null ? this.postFilter.filter(results) : results;
   }

   /**
    * {@inheritDoc}
    */
   public JoinCollector getJoinCollector()
   {
      return this.joinCollector;
   }

   /**
    * {@inheritDoc}
    */
   public NativeQuery<Q> getLeftSource()
   {
      return this.leftSource;
   }

   /**
    * {@inheritDoc}
    */
   public NativeQuery<Q> getRightSource()
   {
      return this.rightSource;
   }

   /**
    * {@inheritDoc}
    */
   public List<ScoredRow> search(final SearchIndexingService<Q> indexSearcher) throws IOException, RepositoryException
   {

      List<ScoredRow> leftSourceResult = this.leftSource.search(indexSearcher);
      leftSourceResult = this.leftSource.filter(leftSourceResult);
      leftSourceResult = this.leftSource.sort(leftSourceResult);
      List<ScoredRow> rightSourceResult = this.rightSource.search(indexSearcher);
      rightSourceResult = this.rightSource.filter(rightSourceResult);
      rightSourceResult = this.rightSource.sort(rightSourceResult);

      return this.joinCollector.join(leftSourceResult, rightSourceResult);
   }

   /**
    * {@inheritDoc}
    */
   public List<ScoredRow> sort(final List<ScoredRow> results) throws IOException, RepositoryException
   {
      if (postSorter != null)
      {
         Collections.sort(results, postSorter);
      }
      return results;
   }
}
