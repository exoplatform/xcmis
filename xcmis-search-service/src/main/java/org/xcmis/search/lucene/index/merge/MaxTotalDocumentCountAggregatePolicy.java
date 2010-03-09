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

import java.util.Collection;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: MaxTotalDocumentCountAggregatePolicy.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class MaxTotalDocumentCountAggregatePolicy extends InheritanceAggregationPolicy
{

   /**
    * Maximum number of chains.
    */
   public static final int DEFAULT_MAX_DOCUMENT_COUNT =
      MaxCandidatsCountAggrigatePolicy.DEFAULT_MAX_CHAINS_COUNT
         * DocumentCountAggregatePolicy.DEFAULT_MAX_DOCUMENTS_4_DIR;

   public MaxTotalDocumentCountAggregatePolicy(AggregatePolicy superAggregatePolicy)
   {
      super(superAggregatePolicy);
   }

   @Override
   public Collection<LuceneIndexDataManager> findIndexDataManagerToAggrigate(
      Collection<LuceneIndexDataManager> aggregateCandidat, long requiredCandidatCount, long reguiredTotalCandidatSize)
   {
      long totalDocuments = 0;
      for (LuceneIndexDataManager luceneIndexDataManager : aggregateCandidat)
      {
         totalDocuments += luceneIndexDataManager.getDocumentCount();
      }
      if (totalDocuments > DEFAULT_MAX_DOCUMENT_COUNT)
      {

      }
      return super.findIndexDataManagerToAggrigate(aggregateCandidat, requiredCandidatCount, reguiredTotalCandidatSize);
   }

   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(MaxTotalDocumentCountAggregatePolicy.class);
}
