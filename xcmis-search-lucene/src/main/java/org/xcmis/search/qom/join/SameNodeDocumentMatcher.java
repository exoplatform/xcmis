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
package org.xcmis.search.qom.join;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.exoplatform.services.jcr.datamodel.QPathEntry;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.SearchIndexingService;
import org.xcmis.search.SingleSourceNativeQuery;
import org.xcmis.search.SingleSourceNativeQueryImpl;
import org.xcmis.search.index.FieldNames;
import org.xcmis.search.lucene.search.DescendantQueryNode;
import org.xcmis.search.qom.source.join.SameNodeJoinConditionImpl;
import org.xcmis.search.result.DocumentMatcher;
import org.xcmis.search.result.ScoredRow;

import java.io.IOException;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.query.qom.Ordering;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id$
 */
public class SameNodeDocumentMatcher implements DocumentMatcher
{
   private final SearchIndexingService<Query> indexingService;

   private final boolean isSimpleMatch;

   private final SameNodeJoinConditionImpl joinCondition;

   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(this.getClass().getName());

   /**
    * @param joinCondition
    */
   public SameNodeDocumentMatcher(final SameNodeJoinConditionImpl joinCondition,
      final SearchIndexingService<Query> indexingService)
   {
      super();
      this.joinCondition = joinCondition;
      this.indexingService = indexingService;
      this.isSimpleMatch = joinCondition.getSelector2Path() == null || joinCondition.getSelector2Path().equals(".");
   }

   /**
    * {@inheritDoc}
    */
   public float match(final ScoredRow leftScore, final ScoredRow rightScore) throws IOException, RepositoryException
   {

      return this.isSimpleMatch ? this.simpleMatch(leftScore, rightScore) : this.reletedMatch(leftScore, rightScore);
   }

   private String getReletedNodeUuid(final String sourceNodeUuid) throws RepositoryException
   {

      final QPathEntry[] entries = this.joinCondition.getSelector2QPath().getEntries();
      Query descendantQuery = new TermQuery(new Term(FieldNames.UUID, sourceNodeUuid));

      for (final QPathEntry entrie : entries)
      {
         final String stepName = this.joinCondition.getLocationFactory().formatPathElement(entrie);
         final Query nameQuery = new TermQuery(new Term(FieldNames.LABEL, stepName));
         descendantQuery = new DescendantQueryNode(nameQuery, descendantQuery);
      }

      // TODO test SNS
      final SingleSourceNativeQuery<Query> ssquery =
         new SingleSourceNativeQueryImpl<Query>(descendantQuery, "nt", new Ordering[]{}, null, null, 0, 0);

      final List<ScoredRow> result = this.indexingService.search(ssquery);
      if (result.size() > 0)
      {
         return result.get(0).getNodeIdentifer("nt");
      }
      return null;
   }

   /**
    * @param leftScore
    * @param rightScore
    * @return
    * @throws IOException
    * @throws RepositoryException
    */
   private float reletedMatch(final ScoredRow leftScore, final ScoredRow rightScore) throws IOException,
      RepositoryException
   {
      if (leftScore.getNodeIdentifer(this.joinCondition.getSelector1Name()).equals(
         this.getReletedNodeUuid(rightScore.getNodeIdentifer(this.joinCondition.getSelector2Name()))))
      {
         return (float)Math.sqrt(leftScore.getScore() * leftScore.getScore() + rightScore.getScore()
            * rightScore.getScore());
      }
      return 0;
   }

   /**
    * @param leftScore
    * @param rightScore
    * @return
    * @throws IOException
    * @throws RepositoryException
    */
   private float simpleMatch(final ScoredRow leftScore, final ScoredRow rightScore) throws IOException,
      RepositoryException
   {
      if (leftScore.getNodeIdentifer(this.joinCondition.getSelector1Name()).equals(
         rightScore.getNodeIdentifer(this.joinCondition.getSelector2Name())))
      {
         return (float)Math.sqrt(leftScore.getScore() * leftScore.getScore() + rightScore.getScore()
            * rightScore.getScore());
      }
      return 0;
   }
}
