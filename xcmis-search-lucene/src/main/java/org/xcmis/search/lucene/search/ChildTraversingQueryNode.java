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
import org.apache.lucene.index.CorruptIndexException;
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
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.index.FieldNames;

import java.io.IOException;
import java.util.Set;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id$
 */
public class ChildTraversingQueryNode extends Query
{
   /**
    * Serial version UID required for safe serialization.
    */
   private static final long serialVersionUID = 7265002058181097050L;

   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger("jcr.ChildTraversingQueryNode");

   /**
    * Query what return parent node.
    */
   private final Query parentQuery;

   /**
    * If isDeep=true return only first level child.
    */
   private final boolean isDeep;

   private final boolean isIncludeParent;

   /**
    * @param parentQuery - parent query.
    * @param isDeep - if true return only first level child.
    */
   public ChildTraversingQueryNode(Query parentQuery, boolean isDeep)
   {
      super();
      this.parentQuery = parentQuery;
      this.isDeep = isDeep;
      this.isIncludeParent = false;
   }

   /**
    * @param parentQuery - parent query.
    * @param isDeep - if true return only first level child.
    */
   private ChildTraversingQueryNode(Query parentQuery, boolean isDeep, boolean isIncludeParent)
   {
      super();
      this.parentQuery = parentQuery;
      this.isDeep = isDeep;
      this.isIncludeParent = isIncludeParent;
   }

   /**
    * {@inheritDoc}
    */
   public void extractTerms(Set terms)
   {
      parentQuery.extractTerms(terms);
   }

   /**
    * {@inheritDoc}
    */
   public Query rewrite(IndexReader reader) throws IOException
   {
      Query newParentQuery = parentQuery.rewrite(reader);
      if (newParentQuery == parentQuery)
         return this;

      return new ChildTraversingQueryNode(newParentQuery, isDeep);
   }

   /**
    * {@inheritDoc}
    */
   public String toString()
   {
      return "(ChildTraversingQueryNode:" + parentQuery + "isDeep" + isDeep + ")";
   }

   /**
    * {@inheritDoc}
    */
   /**
    * {@inheritDoc}
    */
   public String toString(String field)
   {
      return "(ChildTraversingQueryNode:" + parentQuery + "isDeep" + isDeep + ")";
   }

   /**
    * {@inheritDoc}
    */
   protected Weight createWeight(Searcher searcher) throws IOException
   {
      return new ChildTraversingQueryNodeWeight(searcher);
   }

   /**
    * Scorer for ChildTraversingQuery.
    * 
    * @author sj
    */
   private class ChildTraversingQueryNodeScorer extends Scorer
   {
      /**
       * Query searcher.
       */
      private Searcher searcher;

      /**
       * Parent query scorer.
       */
      private Scorer parentScorer;

      /**
       * Query reader.
       */
      private IndexReader reader;

      /**
       * Child Scorer.
       */
      private Scorer childScorer;

      /**
       * @param searcher - query searcher.
       * @param parentScorer - parent query scorer.
       * @param reader - query reader.
       */
      public ChildTraversingQueryNodeScorer(Searcher searcher, Scorer parentScorer, IndexReader reader)
      {
         super(searcher.getSimilarity());
         this.searcher = searcher;
         this.parentScorer = parentScorer;
         this.reader = reader;

      }

      /**
       * {@inheritDoc}
       */
      public int doc()
      {
         return childScorer.doc();
      }

      /**
       * {@inheritDoc}
       */
      public Explanation explain(int doc) throws IOException
      {
         return new Explanation();
      }

      /**
       * {@inheritDoc}
       */
      public boolean next() throws IOException
      {
         // no parent selected
         if (childScorer == null)
         {
            // search for parent
            if (!parentScorer.next())
            {
               if (log.isDebugEnabled())
                  log.debug("Childs not found");
               return false;
            }
            // load childs of current parent
            reloadChildScorer();
         }

         return childScorer.next();

      }

      /**
       * {@inheritDoc}
       */
      public float score() throws IOException
      {

         return childScorer.score();
      }

      /**
       * {@inheritDoc}
       */
      public boolean skipTo(int target) throws IOException
      {
         if (log.isDebugEnabled())
            log.debug("Before " + doc() + "-" + reader.document(doc()).get(FieldNames.LABEL) + "=" + target + "-"
               + reader.document(target).get(FieldNames.LABEL));

         boolean result = childScorer.skipTo(target);

         if (log.isDebugEnabled())
         {

            try
            {
               log.debug("After " + doc() + "-" + reader.document(doc()).get(FieldNames.LABEL) + "=" + target + "-"
                  + reader.document(target).get(FieldNames.LABEL));
            }
            catch (Exception e)
            {
            }
         }

         return result;
      }

      private Query createOrQuery(Query first, Query second)
      {
         if (first == null)
            return second;
         else if (second == null)
            return first;
         BooleanQuery bq = new BooleanQuery();
         bq.add(first, Occur.SHOULD);
         bq.add(second, Occur.SHOULD);
         return bq;
      }

      /**
       * Create childScorer according to the result of parent query.
       * 
       * @throws CorruptIndexException
       * @throws IOException
       */
      private void reloadChildScorer() throws CorruptIndexException, IOException
      {
         Query childQuery = null;
         do
         {
            int parentDoc = parentScorer.doc();
            Document parentDocument = reader.document(parentDoc, new UUIDFieldSelector());

            Query parentTermQuery = new TermQuery(new Term(FieldNames.PARENT, parentDocument.get(FieldNames.UUID)));

            if (isDeep)
            {

               childQuery = createOrQuery(childQuery, new ChildTraversingQueryNode(parentTermQuery, true, true));

               if (isIncludeParent)
               {
                  childQuery =
                     createOrQuery(childQuery, new TermQuery(new Term(FieldNames.UUID, parentDocument
                        .get(FieldNames.UUID))));
               }
            }
            else
            {
               childQuery = createOrQuery(childQuery, parentTermQuery);
            }
         }
         while (parentScorer.next());
         if (log.isDebugEnabled())
            log.debug("Sub query " + childQuery);
         childScorer = childQuery.weight(searcher).scorer(reader);
      }
   }

   /**
    * Weight for ChildTraversingQuery.
    * 
    * @author sj
    */
   private class ChildTraversingQueryNodeWeight implements Weight
   {

      /**
       * Serial version UID required for safe serialization.
       */
      private static final long serialVersionUID = -6839886829560442055L;

      /**
       * Query searcher.
       */
      private final Searcher searcher;

      /**
       * @param searcher - Query searcher.
       */
      public ChildTraversingQueryNodeWeight(Searcher searcher)
      {
         this.searcher = searcher;
      }

      /**
       * {@inheritDoc}
       */
      public Explanation explain(IndexReader reader, int doc) throws IOException
      {
         return new Explanation();
      }

      /**
       * {@inheritDoc}
       */
      public Query getQuery()
      {
         return ChildTraversingQueryNode.this;
      }

      /**
       * {@inheritDoc}
       */
      public float getValue()
      {
         return 1.0f;
      }

      /**
       * {@inheritDoc}
       */
      public void normalize(float norm)
      {
      }

      /**
       * {@inheritDoc}
       */
      public Scorer scorer(IndexReader reader) throws IOException
      {
         Scorer parentScorer = parentQuery.weight(searcher).scorer(reader);
         return new ChildTraversingQueryNodeScorer(searcher, parentScorer, reader);
      }

      /**
       * {@inheritDoc}
       */
      public float sumOfSquaredWeights() throws IOException
      {
         return 1.0f;
      }
   }

}
