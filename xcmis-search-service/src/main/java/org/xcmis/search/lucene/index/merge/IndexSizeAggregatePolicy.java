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

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.lucene.LuceneIndexDataManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: IndexSizeAggregatePolicy.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class IndexSizeAggregatePolicy implements AggregatePolicy
{
   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(IndexSizeAggregatePolicy.class);

   /**
    * Maximum size of index chain.
    */
   public static final int DEFAULT_MAX_DIR_SIZE = 1024 * 1024;

   /**
    * Minimum size of index chain.
    */
   public static final int DEFAULT_MIN_DIR_SIZE = 0;

   /**
    * Maximum size of index chain.
    */
   private int maxDirSize;

   /**
    * Minimum size of index chain.
    */
   private int minDirSize;

   public IndexSizeAggregatePolicy()
   {
      super();
      this.maxDirSize = DEFAULT_MAX_DIR_SIZE;
      this.minDirSize = DEFAULT_MIN_DIR_SIZE;
   }

   /**
    * @return Minimum size of index chain.
    */
   public int getMinDirSize()
   {
      return minDirSize;
   }

   /**
    * @param minDirSize - Minimum size of index chain.
    */
   public void setMinDirSize(int minDirSize)
   {
      this.minDirSize = minDirSize;
   }

   /**
    * @return maximum directory size.
    */
   public int getMaxDirSize()
   {
      return maxDirSize;
   }

   /**
    * @param maxDirSize set maximim directory size.
    */
   public void setMaxDirSize(int maxDirSize)
   {
      this.maxDirSize = maxDirSize;
   }

   public Collection<LuceneIndexDataManager> findIndexDataManagerToAggrigate(
      Collection<LuceneIndexDataManager> aggregateCandidat, long requiredCandidatCount, long reguiredTotalCandidatSize)
   {
      Collection<LuceneIndexDataManager> result = null;
      if (requiredCandidatCount == 0 && reguiredTotalCandidatSize == 0)
      {
         result = new ArrayList<LuceneIndexDataManager>();
         for (LuceneIndexDataManager luceneIndexDataManager : aggregateCandidat)
         {
            if (minDirSize < luceneIndexDataManager.getDirectorySize(false)
               && luceneIndexDataManager.getDirectorySize(false) < maxDirSize)
            {
               result.add(luceneIndexDataManager);
            }
         }
      }
      else
      {
         if (requiredCandidatCount < aggregateCandidat.size())
         {
            result = new ArrayList<LuceneIndexDataManager>((int)requiredCandidatCount);
            LuceneIndexDataManager[] indexes = new LuceneIndexDataManager[aggregateCandidat.size()];
            aggregateCandidat.toArray(indexes);
            Arrays.sort(indexes, new IndexSizeComparator());
            for (int i = 0; i < requiredCandidatCount; i++)
            {
               result.add(indexes[i]);
            }
         }
         else
         {
            result = aggregateCandidat;
         }

      }
      return result;
   }

   public Collection<LuceneIndexDataManager> findIndexDataManagerToOptimize(
      Collection<LuceneIndexDataManager> aggregateCandidat)
   {
      return null;
   }

   private class IndexSizeComparator implements Comparator<LuceneIndexDataManager>
   {
      /**
       * {@inheritDoc}
       */
      public int compare(LuceneIndexDataManager o1, LuceneIndexDataManager o2)
      {
         return (int)(o1.getDirectorySize(false) - o2.getDirectorySize(false));
      }

   };
}
