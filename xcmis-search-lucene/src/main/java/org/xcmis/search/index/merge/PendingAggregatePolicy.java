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
package org.xcmis.search.index.merge;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.index.LuceneIndexDataManager;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: PendingAggregatePolicy.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class PendingAggregatePolicy extends ModificationTimeAggregatePolicy
{
   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(PendingAggregatePolicy.class);

   /**
    * The minimum time that must elapse after the previous aggregate.
    */
   private static final int DEFAULT_MIN_AGGREGATE_TIME = 1 * 1000;

   private int minAggregateTime;

   public PendingAggregatePolicy()
   {
      super();
      this.minAggregateTime = DEFAULT_MIN_AGGREGATE_TIME;
   }

   /**
    * @return The minimum time that must elapse after the previous aggregate.
    */
   public int getMinAggregateTime()
   {
      return minAggregateTime;
   }

   /**
    * @param minAggregateTime The minimum time that must elapse after the
    *          previous aggregate.
    */
   public void setMinAggregateTime(int minAggregateTime)
   {
      this.minAggregateTime = minAggregateTime;
   }

   /**
    * Lust time of findIndexDataManagerToAggrigate run.
    */
   private long lastFindFinishedTime;

   @Override
   public Collection<LuceneIndexDataManager> findIndexDataManagerToAggrigate(
      Collection<LuceneIndexDataManager> aggregateCandidat, long requiredCandidatCount, long reguiredTotalCandidatSize)
   {

      if (requiredCandidatCount == 0 && reguiredTotalCandidatSize == 0)
         if (System.currentTimeMillis() - lastFindFinishedTime < DEFAULT_MIN_AGGREGATE_TIME)
            return new ArrayList<LuceneIndexDataManager>();
      Collection<LuceneIndexDataManager> indexDataManagerToAggrigate =
         super.findIndexDataManagerToAggrigate(aggregateCandidat, requiredCandidatCount, reguiredTotalCandidatSize);
      this.lastFindFinishedTime = System.currentTimeMillis();

      // log.info("====" + indexDataManagerToAggrigate.size() + "=====");
      // for (LuceneIndexDataManager luceneIndexDataManager :
      // indexDataManagerToAggrigate) {
      // log.info(luceneIndexDataManager.getDirectorySize(false) + "\t\t"
      // + luceneIndexDataManager.getDocumentCount() + "\t\t"
      // + (System.currentTimeMillis() -
      // luceneIndexDataManager.getLastModifedTime()) + " msec");
      // }
      return indexDataManagerToAggrigate;
   }
}
