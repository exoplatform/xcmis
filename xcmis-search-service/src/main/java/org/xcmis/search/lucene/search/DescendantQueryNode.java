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

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.BooleanClause.Occur;
import org.xcmis.search.lucene.index.FieldNames;
import org.xcmis.spi.utils.Logger;

import java.io.IOException;
import java.util.Set;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: DescendantQueryNode.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class DescendantQueryNode extends Query
{

   /** The serialVersionUID. */
   private static final long serialVersionUID = -6151493594236655389L;

   /**
    * Class logger.
    */
   private final static Logger log = Logger.getLogger(DescendantQueryNode.class);

   private final Query context;

   private final Query parentQuery;

   /**
    * 
    */
   public DescendantQueryNode(Query context, Query parentQuery)
   {
      this.context = context;
      this.parentQuery = parentQuery;
   }

   @Override
   public void extractTerms(Set terms)
   {
      context.extractTerms(terms);
      parentQuery.extractTerms(terms);
   }

   @Override
   public Query rewrite(IndexReader reader) throws IOException
   {
      Query cQuery = null;
      if (context != null)
      {
         cQuery = context.rewrite(reader);
      }
      Query pQuery = parentQuery.rewrite(reader);
      if (((cQuery != null && cQuery.equals(context)) || (cQuery == null && context == null))
         && pQuery.equals(parentQuery)
         )
      {
         return this;
      }
      return new DescendantQueryNode(cQuery, pQuery);
   }

   @Override
   public String toString()
   {
      return "(DescendantQueryNode Parent:" + parentQuery + " query:" + context + ")";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString(String field)
   {
      return "(DescendantQueryNode Parent:" + parentQuery + " query:" + context + ")";
   }

   @Override
   protected Weight createWeight(Searcher searcher) throws IOException
   {
      return new DescendantQueryNodeWeight(searcher);
   }

   private Query getConetextQuery()
   {
      return context;
   }

   private class DescendantQueryNodeScorer extends Scorer
   {
      /**
       * BitSet storing the id's of selected documents
       */

      private final IndexReader reader;

      private final Searcher searcher;

      private Scorer currentContextScorer;

      private final Scorer parentScorer;

      protected DescendantQueryNodeScorer(Searcher searcher, Scorer parentScorer, IndexReader reader)
      {

         super(searcher.getSimilarity());
         this.parentScorer = parentScorer;
         this.searcher = searcher;
         this.reader = reader;
      }

      @Override
      public int doc()
      {
         return currentContextScorer.doc();
      }

      @Override
      public Explanation explain(int doc) throws IOException
      {
         return new Explanation();
      }

      @Override
      public boolean next() throws IOException
      {
         if (currentContextScorer == null)
         {
            if (!parentScorer.next())
            {
               log.error("parent not found");
               return false;
            }

            int parentDoc = parentScorer.doc();

            Document parentDocument = reader.document(parentDoc, new UUIDFieldSelector());
            if (context != null)
            {
               BooleanQuery bq = new BooleanQuery();
               bq.add(context, Occur.MUST);
               bq.add(new TermQuery(new Term(FieldNames.PARENT, parentDocument.get(FieldNames.UUID))), Occur.MUST);
               if (log.isDebugEnabled())
               {
                  log.debug("Sub query " + bq);
               }
               currentContextScorer = bq.weight(searcher).scorer(reader);
            }
            else
            {
               TermQuery newQuery = new TermQuery(new Term(FieldNames.PARENT, parentDocument.get(FieldNames.UUID)));
               log.debug("Sub query " + newQuery);
               currentContextScorer = newQuery.weight(searcher).scorer(reader);
            }

         }

         return currentContextScorer.next();
      }

      @Override
      public float score() throws IOException
      {
         return currentContextScorer.score();
      }

      @Override
      public boolean skipTo(int target) throws IOException
      {
         return currentContextScorer.skipTo(target);
      }

   }

   private class DescendantQueryNodeWeight implements Weight
   {

      private final Searcher searcher;

      public DescendantQueryNodeWeight(Searcher searcher)
      {
         this.searcher = searcher;
      }

      public Explanation explain(IndexReader reader, int doc) throws IOException
      {
         return new Explanation();
      }

      public Query getQuery()
      {
         return DescendantQueryNode.this;
      }

      public float getValue()
      {
         return 1.0f;
      }

      public void normalize(float norm)
      {
      }

      public Scorer scorer(IndexReader reader) throws IOException
      {
         Scorer parentScorer = parentQuery.weight(searcher).scorer(reader);
         return new DescendantQueryNodeScorer(searcher, parentScorer, reader);
      }

      public float sumOfSquaredWeights() throws IOException
      {
         return 1.0f;
      }

   }
}
