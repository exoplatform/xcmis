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
package org.xcmis.search.lucene.index.merge;

import org.xcmis.search.lucene.index.LuceneIndexDataManager;
import org.xcmis.spi.utils.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: ModificationTimeAggregatePolicy.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class ModificationTimeAggregatePolicy extends DocumentCountAggregatePolicy
{
   /**
    * The minimum time that must elapse after the previous index modification.
    */
   public static final int DEFAULT_MIN_MODIFACATION_TIME = 10 * 1000;

   /**
    * The minimum time that must elapse after the previous index modification.
    */
   public static final int DEFAULT_MAX_MODIFACATION_TIME = Integer.MAX_VALUE;

   private int minModificationTime;

   private int maxModificationTime;

   public ModificationTimeAggregatePolicy()
   {
      super();
      this.minModificationTime = DEFAULT_MIN_MODIFACATION_TIME;
      this.maxModificationTime = DEFAULT_MAX_MODIFACATION_TIME;
   }

   /**
    * @return The minimum time that must elapse after the previous index
    *         modification.
    */
   public int getMinModificationTime()
   {
      return minModificationTime;
   }

   /**
    * @param minModificationTime The minimum time that must elapse after the
    *          previous index modification.
    */
   public void setMinModificationTime(int minModificationTime)
   {
      this.minModificationTime = minModificationTime;
   }

   /**
    * Class logger.
    */
   private static final Logger LOG = Logger.getLogger(ModificationTimeAggregatePolicy.class);

   @Override
   public Collection<LuceneIndexDataManager> findIndexDataManagerToAggrigate(
      Collection<LuceneIndexDataManager> aggregateCandidat, long requiredCandidatCount, long reguiredTotalCandidatSize)
   {
      List<LuceneIndexDataManager> ready2aggregate = new ArrayList<LuceneIndexDataManager>();
      if (requiredCandidatCount == 0 && reguiredTotalCandidatSize == 0)
      {

         for (LuceneIndexDataManager luceneIndexDataManager : aggregateCandidat)
         {
            long timeSinceLastCheck = System.currentTimeMillis() - luceneIndexDataManager.getLastModifedTime();
            if (minModificationTime < timeSinceLastCheck && timeSinceLastCheck < maxModificationTime)
            {
               ready2aggregate.add(luceneIndexDataManager);
            }
         }

      }
      else
      {
         if (requiredCandidatCount * 1.3 < aggregateCandidat.size())
         {
            LuceneIndexDataManager[] indexes = new LuceneIndexDataManager[aggregateCandidat.size()];
            aggregateCandidat.toArray(indexes);
            Arrays.sort(indexes, new ModifficationTimeComparator());
            long variants = Math.round(requiredCandidatCount * 1.3);
            for (int i = 0; i < variants; i++)
            {
               ready2aggregate.add(indexes[i]);
            }
         }
         else
         {
            ready2aggregate.addAll(aggregateCandidat);
         }
      }
      return super.findIndexDataManagerToAggrigate(ready2aggregate, requiredCandidatCount, reguiredTotalCandidatSize);
   }

   private class ModifficationTimeComparator implements Comparator<LuceneIndexDataManager>
   {
      /**
       * {@inheritDoc}
       */
      public int compare(LuceneIndexDataManager o1, LuceneIndexDataManager o2)
      {
         return (int)(o1.getLastModifedTime() - o2.getLastModifedTime());
      }

   };
}
