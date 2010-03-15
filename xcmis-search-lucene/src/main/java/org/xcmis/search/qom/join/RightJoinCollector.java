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

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.ScoredNodesImpl;
import org.xcmis.search.result.DocumentMatcher;
import org.xcmis.search.result.JoinCollector;
import org.xcmis.search.result.ScoredRow;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.query.qom.JoinCondition;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: RightJoinCollector.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class RightJoinCollector implements JoinCollector
{
   private final DocumentMatcher documentMatcher;

   private final JoinCondition joinCondition;

   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(getClass().getName());

   public RightJoinCollector(JoinCondition joinCondition, DocumentMatcher documentMatcher)
   {
      this.joinCondition = joinCondition;
      this.documentMatcher = documentMatcher;
   }

   public List<ScoredRow> join(List<ScoredRow> leftSource, List<ScoredRow> rightSource) throws IOException,
      RepositoryException
   {
      if (log.isDebugEnabled())
      {
         log.info("Before RightJoinCollector");
         ResultDumper.dump(leftSource);
         ResultDumper.dump(rightSource);
      }

      List<ScoredRow> result = new LinkedList<ScoredRow>();
      for (ScoredRow rightScoredNode : rightSource)
      {
         boolean isMatchFound = false;
         for (ScoredRow leftScoredNode : leftSource)
         {
            float match = documentMatcher.match(leftScoredNode, rightScoredNode);
            if (match > 0)
            {
               result.add(ScoredNodesImpl.merge(leftScoredNode, rightScoredNode, match));
               isMatchFound = true;
            }
         }
         if (!isMatchFound)
         {
            result.add(rightScoredNode);
         }
      }
      if (log.isDebugEnabled())
      {
         log.info("After RightJoinCollector");
         ResultDumper.dump(result);
      }
      return result;
   }
}
