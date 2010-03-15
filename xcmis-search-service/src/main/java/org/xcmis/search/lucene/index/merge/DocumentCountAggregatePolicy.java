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
import org.xcmis.search.lucene.index.LuceneIndexDataManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: DocumentCountAggregatePolicy.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class DocumentCountAggregatePolicy extends IndexSizeAggregatePolicy
{
   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(DocumentCountAggregatePolicy.class);

   /**
    * Maximum number documents for memory index chain.
    */
   public static final int DEFAULT_MAX_DOCUMENTS_4_DIR = 150;

   /**
    * Maximum number documents for memory index chain.
    */
   public static final int DEFAULT_MIN_DOCUMENTS_4_DIR = 0;

   private int maxDocuments4Dir;

   private int minDocuments4Dir;

   public DocumentCountAggregatePolicy()
   {
      super();
      this.maxDocuments4Dir = DEFAULT_MAX_DOCUMENTS_4_DIR;
      this.minDocuments4Dir = DEFAULT_MIN_DOCUMENTS_4_DIR;
   }

   public int getMinDocuments4Dir()
   {
      return minDocuments4Dir;
   }

   public void setMinDocuments4Dir(int minDocuments4Dir)
   {
      this.minDocuments4Dir = minDocuments4Dir;
   }

   /**
    * @return Maximum number documents for index chain.
    */
   public int getMaxDocuments4Dir()
   {
      return maxDocuments4Dir;
   }

   /**
    * @param maxDocuments4Dir set maximum number documents for index chain.
    */
   public void setMaxDocuments4Dir(int maxDocuments4Dir)
   {
      this.maxDocuments4Dir = maxDocuments4Dir;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Collection<LuceneIndexDataManager> findIndexDataManagerToAggrigate(
      Collection<LuceneIndexDataManager> aggregateCandidat, long requiredCandidatCount, long reguiredTotalCandidatSize)
   {

      Collection<LuceneIndexDataManager> result = null;
      if (requiredCandidatCount == 0 && reguiredTotalCandidatSize == 0)
      {
         result =
            new HashSet<LuceneIndexDataManager>(super.findIndexDataManagerToAggrigate(aggregateCandidat,
               requiredCandidatCount, reguiredTotalCandidatSize));
         for (LuceneIndexDataManager luceneIndexDataManager : aggregateCandidat)
         {
            if (minDocuments4Dir < luceneIndexDataManager.getDocumentCount()
               && luceneIndexDataManager.getDocumentCount() < maxDocuments4Dir)
            {
               // log.info("Aggrigate by doc count " +
               // luceneIndexDataManager.getDocumentCount() + " min="
               // + (minDocuments4Dir < luceneIndexDataManager.getDocumentCount()) +
               // " max="
               // + (luceneIndexDataManager.getDocumentCount() < maxDocuments4Dir));
               result.add(luceneIndexDataManager);
            }
         }

         result.addAll(super.findIndexDataManagerToAggrigate(aggregateCandidat, requiredCandidatCount,
            reguiredTotalCandidatSize));
      }
      else
      {
         if (requiredCandidatCount < aggregateCandidat.size())
         {
            result = new ArrayList<LuceneIndexDataManager>();
            LuceneIndexDataManager[] indexes = new LuceneIndexDataManager[aggregateCandidat.size()];
            aggregateCandidat.toArray(indexes);
            Arrays.sort(indexes, new DocumentCountComparator());
            int i = 0;
            while (result.size() < requiredCandidatCount && i < indexes.length)
            {

               LuceneIndexDataManager index = indexes[i++];
               if (minDocuments4Dir < index.getDocumentCount() && index.getDocumentCount() < maxDocuments4Dir)
               {
                  result.add(index);
               }

            }
         }
         else
         {
            result = aggregateCandidat;
         }
      }
      return result;
   }

   /**
    * 
    *
    *
    */
   public class DocumentCountComparator implements Comparator<LuceneIndexDataManager>
   {
      /**
       * {@inheritDoc}
       */
      public int compare(LuceneIndexDataManager o1, LuceneIndexDataManager o2)
      {
         return (int)(o1.getDocumentCount() - o2.getDocumentCount());
      }

   };
}
