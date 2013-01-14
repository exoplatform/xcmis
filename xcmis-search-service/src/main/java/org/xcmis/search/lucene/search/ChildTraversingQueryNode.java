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
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.Weight;
import org.xcmis.search.lucene.index.FieldNames;
import org.xcmis.spi.utils.Logger;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: ChildTraversingQueryNode.java 2 2010-02-04 17:21:49Z andrew00x $
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
   private final static Logger log = Logger.getLogger(ChildTraversingQueryNode.class);

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
    * @param isIncludeParent
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
      if (newParentQuery.equals(parentQuery))
      {
         return this;
      }

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
   @Override
   public Weight createWeight(Searcher searcher) throws IOException
   {
      return new ChildTraversingQueryNodeWeight(searcher);
   }

   /**
    * Scorer for ChildTraversingQuery.
    * 
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
      
      private boolean scoreDocsInOrder;
      private boolean topScorer;

      /**
       * @param searcher - query searcher.
       * @param parentScorer - parent query scorer.
       * @param reader - query reader.
       */
      public ChildTraversingQueryNodeScorer(Searcher searcher, Scorer parentScorer, IndexReader reader, 
              boolean scoreDocsInOrder, boolean topScorer)
      {
         super(searcher.getSimilarity());
         this.searcher = searcher;
         this.parentScorer = parentScorer;
         this.reader = reader;
         this.scoreDocsInOrder = scoreDocsInOrder;
         this.topScorer = topScorer;
      }

      /**
       * {@inheritDoc}
       */
      public int docID()
      {
        return childScorer.docID();
      }

      /**
       * {@inheritDoc}
       */
      public int nextDoc() throws IOException
      {
        // no parent selected
        if (childScorer == null)
        {
          // search for parent
          if (parentScorer == null || parentScorer.nextDoc() == Scorer.NO_MORE_DOCS)
          {
            if (log.isDebugEnabled())
            {
              log.debug("Childs not found");
            }
            return Scorer.NO_MORE_DOCS;
          }
          // load childs of current parent
          reloadChildScorer();
        }

        return childScorer.nextDoc();

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
      public int advance(int target) throws IOException
      {
         return childScorer.advance(target);
      }

      private Query createOrQuery(Query first, Query second)
      {
         if (first == null)
         {
            return second;
         }
         else if (second == null)
         {
            return first;
         }
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
            int parentDoc = parentScorer.docID();
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
         while (parentScorer.nextDoc() < Scorer.NO_MORE_DOCS);
         if (log.isDebugEnabled())
         {
            log.debug("Sub query " + childQuery);
         }
         childScorer = childQuery.createWeight(searcher).scorer(reader, scoreDocsInOrder, topScorer);
      }

   }

   /**
    * Weight for ChildTraversingQuery.
    * 
    */
   private class ChildTraversingQueryNodeWeight extends Weight
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
      @Override
      public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topScorer) throws IOException
      {
         Scorer parentScorer = parentQuery.createWeight(searcher).scorer(reader, scoreDocsInOrder, topScorer);
         return new ChildTraversingQueryNodeScorer(searcher, parentScorer, reader, scoreDocsInOrder, topScorer);
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
