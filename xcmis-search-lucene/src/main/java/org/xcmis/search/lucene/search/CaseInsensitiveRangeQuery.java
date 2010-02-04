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
package org.xcmis.search.lucene.search;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.TermQuery;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.IOException;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id$
 */
public class CaseInsensitiveRangeQuery extends RangeQuery
{
   /**
    * 
    */
   private static final long serialVersionUID = 3351256558561558491L;

   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(getClass().getName());

   public CaseInsensitiveRangeQuery(Term lowerTerm, Term upperTerm, boolean inclusive)
   {
      super(modifyLowerTerm(lowerTerm), modifyUpperTerm(upperTerm), inclusive);
   }

   @Override
   public Query rewrite(IndexReader reader) throws IOException
   {
      BooleanQuery query = new BooleanQuery(true);
      TermEnum enumerator = reader.terms(getLowerTerm());

      try
      {

         String testField = getField();

         String lowerTerm = getLowerTerm().text().toLowerCase();
         do
         {
            Term term = enumerator.term();
            if (term != null && term.field() == testField)
            { // interned comparison

               String termText = term.text().toLowerCase();

               int lowerCompareResult = termText.compareTo(lowerTerm);
               // test lower limit and left border
               if (lowerCompareResult > 0 || (isInclusive() && lowerCompareResult == 0))
               {

                  if (getUpperTerm() != null)
                  {
                     // test upper limit of original term
                     if (getUpperTerm().text().compareTo(term.text()) < 0)
                        break;

                     int upperCompareResult = getUpperTerm().text().compareTo(termText);
                     // test ignore case limit.
                     if (upperCompareResult < 0 || !isInclusive() && upperCompareResult == 0)
                        continue;
                  }
                  TermQuery tq = new TermQuery(term); // found a match
                  tq.setBoost(getBoost()); // set the boost
                  query.add(tq, BooleanClause.Occur.SHOULD); // add to query
               }
            }
            else
            {
               break;
            }
         }
         while (enumerator.next());
      }
      finally
      {
         enumerator.close();
      }
      if (log.isDebugEnabled())
         log.debug(query);
      return query;
   }

   private static Term modifyLowerTerm(final Term lowerTerm)
   {
      if (lowerTerm != null)
         return new Term(lowerTerm.field(), lowerTerm.text().toUpperCase());
      return lowerTerm;
   }

   private static Term modifyUpperTerm(final Term upperTerm)
   {
      if (upperTerm != null)
         return new Term(upperTerm.field(), upperTerm.text().toLowerCase());
      return upperTerm;
   }
}
