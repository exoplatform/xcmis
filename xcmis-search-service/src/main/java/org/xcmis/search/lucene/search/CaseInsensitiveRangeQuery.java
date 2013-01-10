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

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.xcmis.spi.utils.Logger;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: CaseInsensitiveRangeQuery.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class CaseInsensitiveRangeQuery extends TermRangeQuery
{

   /** The serialVersionUID. */
   private static final long serialVersionUID = 3351256558561558491L;

   /**
    * Class logger.
    */
   private final static Logger log = Logger.getLogger(CaseInsensitiveRangeQuery.class);
   
   private static String LOWER_TERM;
   
   private static String UPPER_TERM;
   
   private static String FIELD;
   
   private static boolean IS_LOWER_INCLUSIVE;
   
   private static boolean IS_UPPER_INCLUSIVE;
   
   public CaseInsensitiveRangeQuery(String field, String lowerTerm, String upperTerm, boolean includeLower, boolean includeUpper)
   {
     super(field, lowerTerm, upperTerm, includeLower, includeUpper, null);
     FIELD = field;
     LOWER_TERM = lowerTerm;
     UPPER_TERM = upperTerm;
     IS_LOWER_INCLUSIVE = includeLower;
     IS_UPPER_INCLUSIVE = includeUpper;
     setRewriteMethod(CASE_IN_SENSITIVE_FILTER_REWRITE);
   }

//   public CaseInsensitiveRangeQuery(Term lowerTerm, Term upperTerm, boolean inclusive)
//   {
//     super(
//       (lowerTerm == null) ? modifyUpperTerm(upperTerm).field() : modifyLowerTerm(lowerTerm).field(), 
//       (lowerTerm == null) ? null : modifyLowerTerm(lowerTerm).text(), 
//       (upperTerm == null) ? null : modifyUpperTerm(upperTerm).text(), 
//       inclusive, inclusive, null
//     );
//     LOWER_TERM = lowerTerm;
//     UPPER_TERM = upperTerm;
//     setRewriteMethod(CASE_IN_SENSITIVE_FILTER_REWRITE);
//   }
   
   public static final RewriteMethod CASE_IN_SENSITIVE_FILTER_REWRITE = new RewriteMethod() {

     private static final long serialVersionUID = 1L;

    @Override
     public Query rewrite(IndexReader reader, MultiTermQuery multiTermQuery) throws IOException
     {
       BooleanQuery query = new BooleanQuery(true);
       TermEnum enumerator = reader.terms(new Term(FIELD, LOWER_TERM));
       try
       {
         do
         {
           Term term = enumerator.term();
           if (term != null && term.field().equals(FIELD))
           { // interned comparison
             
             String termText = term.text().toLowerCase();
             
             int lowerCompareResult = termText.compareTo(LOWER_TERM);
             // test lower limit and left border
             if (lowerCompareResult > 0 || (IS_LOWER_INCLUSIVE && lowerCompareResult == 0))
             {
               
               if (UPPER_TERM != null)
               {
                 // test upper limit of original term
                 if (new Term(FIELD, UPPER_TERM).text().compareTo(term.text()) < 0)
                 {
                   break;
                 }
                 
                 int upperCompareResult = new Term(FIELD, UPPER_TERM).text().compareTo(termText);
                 // test ignore case limit.
                 if (upperCompareResult < 0 || !IS_UPPER_INCLUSIVE && upperCompareResult == 0)
                 {
                   continue;
                 }
               }
               TermQuery tq = new TermQuery(term); // found a match
               tq.setBoost(multiTermQuery.getBoost()); // set the boost
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
       {
         log.debug(query.toString());
       }
       return query;
     }
   };
}
