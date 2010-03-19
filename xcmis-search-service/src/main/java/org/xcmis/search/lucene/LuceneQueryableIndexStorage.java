/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.xcmis.search.lucene;

import org.apache.commons.lang.NotImplementedException;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.xcmis.search.VisitException;
import org.xcmis.search.Visitors;
import org.xcmis.search.config.IndexConfigurationException;
import org.xcmis.search.config.SearchServiceConfiguration;
import org.xcmis.search.content.command.InvocationContext;
import org.xcmis.search.content.command.index.ApplyChangesToTheIndexCommand;
import org.xcmis.search.content.command.query.ExecuteSelectorCommand;
import org.xcmis.search.content.interceptors.QueryableIndexStorage;
import org.xcmis.search.lucene.content.VirtualTableResolver;
import org.xcmis.search.lucene.index.FieldNames;
import org.xcmis.search.lucene.index.IndexException;
import org.xcmis.search.lucene.index.LuceneIndexTransaction;
import org.xcmis.search.lucene.index.TransactionableIndexDataManager;
import org.xcmis.search.lucene.search.UUIDFieldSelector;
import org.xcmis.search.model.Limit;
import org.xcmis.search.model.constraint.Constraint;
import org.xcmis.search.model.operand.FullTextSearchScore;
import org.xcmis.search.model.operand.Length;
import org.xcmis.search.model.operand.LowerCase;
import org.xcmis.search.model.operand.NodeDepth;
import org.xcmis.search.model.operand.NodeLocalName;
import org.xcmis.search.model.operand.NodeName;
import org.xcmis.search.model.operand.PropertyValue;
import org.xcmis.search.model.operand.UpperCase;
import org.xcmis.search.model.ordering.Order;
import org.xcmis.search.model.ordering.Ordering;
import org.xcmis.search.result.ScoredNodesImpl;
import org.xcmis.search.result.ScoredRow;
import org.xcmis.search.value.NameConverter;
import org.xcmis.search.value.PathSplitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@exoplatform.org">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z ksm $
 *
 */
public class LuceneQueryableIndexStorage extends QueryableIndexStorage
{

   /**
    * The upper limit for the initial fetch size.
    */
   private static final int MAX_FETCH_SIZE = 32 * 1024;

   private final TransactionableIndexDataManager indexDataManager;

   /**
    * Reselve selector names to lucene querys.
    */
   private final VirtualTableResolver tableResolver;

   /**
    * Convert one Sting name to other String name.
    */
   private final NameConverter nameConverter;

   /**
    * Split path  string to names
    */
   private final PathSplitter pathSplitter;

   private FieldNameResolver fieldNameResolver;

   /**
    * @param indexConfuguration
    * @throws IndexException 
    * @throws IndexConfigurationException 
    * @throws org.xcmis.search.lucene.index.IndexException 
    */
   public LuceneQueryableIndexStorage(SearchServiceConfiguration serviveConfuguration) throws IndexException
   {
      super();
      this.indexDataManager = new TransactionableIndexDataManager(serviveConfuguration.getIndexConfuguration());
      this.fieldNameResolver = new FieldNameResolver(indexDataManager);

      this.tableResolver = serviveConfuguration.getTableResolver();
      this.nameConverter = serviveConfuguration.getNameConverter();
      this.pathSplitter = serviveConfuguration.getPathSplitter();

   }

   /**
    * @see org.xcmis.search.content.interceptors.QueryableIndexStorage#visitApplyChangesToTheIndexCommand(org.xcmis.search.content.command.InvocationContext, org.xcmis.search.content.command.index.ApplyChangesToTheIndexCommand)
    */
   @Override
   public Object visitApplyChangesToTheIndexCommand(InvocationContext ctx, ApplyChangesToTheIndexCommand command)
      throws Throwable
   {
      LuceneIndexTransaction indexTransaction =
         new LuceneIndexTransaction(((Map)command.getAddedDocuments()), command.getDeletedDocuments());

      return indexDataManager.save(indexTransaction);
   }

   /**
    * @see org.xcmis.search.content.interceptors.QueryableIndexStorage#visitExecuteSelectorCommand(org.xcmis.search.content.command.InvocationContext, org.xcmis.search.content.command.query.ExecuteSelectorCommand)
    */
   @Override
   public Object visitExecuteSelectorCommand(InvocationContext ctx, ExecuteSelectorCommand command) throws Throwable
   {

      List<ScoredRow> resultNodes = new ArrayList<ScoredRow>();
      Query query = (Query)ctx.getTableResolver().resolve(command.getSelector().getName(), true);
      if (command.getConstrains().size() > 0)
      {
         BooleanQuery booleanQuery = new BooleanQuery();

         for (Constraint constrain : command.getConstrains())
         {
            booleanQuery.add(getConstrainQuery(constrain, command.getBindVariablesValues()), Occur.MUST);
         }

         booleanQuery.add(query, Occur.MUST);
         query = booleanQuery;
      }
      // Open writer

      IndexSearcher searcher = null;
      try
      {
         // get result
         IndexReader indexReader = indexDataManager.getIndexReader();
         if (indexReader != null)
         {
            searcher = new IndexSearcher(indexReader);

            //query
            Limit limit = command.getLimit();
            int hits = Math.min(MAX_FETCH_SIZE, limit.getOffset() + limit.getRowLimit());
            TopFieldDocs topDocs = searcher.search(query, null, hits, getSort(command.getOrderings()));

            resultNodes = new LinkedList<ScoredRow>();
            for (int i = limit.getOffset(); i < topDocs.scoreDocs.length; i++)
            {
               // get identifiers
               final Document doc = searcher.doc(topDocs.scoreDocs[i].doc, new UUIDFieldSelector());
               final String id = doc.get(FieldNames.UUID);
               resultNodes.add(new ScoredNodesImpl(command.getAlias().getName(), id, topDocs.scoreDocs[i].score));
            }
         }
      }
      catch (final CorruptIndexException e)
      {
         throw new IndexException(e.getLocalizedMessage(), e);
      }
      catch (final IOException e)
      {
         throw new IndexException(e.getLocalizedMessage(), e);
      }
      finally
      {
         try
         {
            if (searcher != null)
            {
               searcher.close();
            }
         }
         catch (final IOException e)
         {
            throw new IndexException(e.getLocalizedMessage(), e);
         }
      }
      return resultNodes;
   }

   /**
    * Return lucene sorter by list of orderings
    * @param list
    * @return
    * @throws VisitException 
    */
   private Sort getSort(List<Ordering> list) throws VisitException
   {
      if (list.size() > 0)
      {
         SortField[] fields = new SortField[list.size()];
         SortFieldVisitor sortVisitor = new SortFieldVisitor();
         int i = 0;
         for (Ordering ordering : list)
         {
            Visitors.visitAll(ordering, sortVisitor);
            fields[i++] = sortVisitor.getSortField();
         }
         return new Sort(fields);

      }
      return new Sort();
   }

   private class SortFieldVisitor extends Visitors.AbstractModelVisitor
   {

      private Order order;

      private SortField sortField;

      public SortField getSortField()
      {
         return sortField;
      }

      /*
       * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.ordering.Ordering)
       */
      @Override
      public void visit(Ordering node) throws VisitException
      {
         order = node.getOrder();
      }

      /**
       * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.operand.FullTextSearchScore)
       */
      @Override
      public void visit(FullTextSearchScore node) throws VisitException
      {
         sortField = new SortField(null, SortField.SCORE, order == Order.DESCENDING);
      }

      /**
       * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.operand.Length)
       */
      @Override
      public void visit(Length node) throws VisitException
      {
         throw new NotImplementedException();
      }

      /**
       * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.operand.LowerCase)
       */
      @Override
      public void visit(LowerCase node) throws VisitException
      {
         throw new NotImplementedException();
      }

      /**
       * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.operand.NodeDepth)
       */
      @Override
      public void visit(NodeDepth depth) throws VisitException
      {
         throw new NotImplementedException();
      }

      /**
       * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.operand.NodeLocalName)
       */
      @Override
      public void visit(NodeLocalName node) throws VisitException
      {
         // TODO Auto-generated method stub
         super.visit(node);
      }

      /**
       * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.operand.NodeName)
       */
      @Override
      public void visit(NodeName node) throws VisitException
      {
         throw new NotImplementedException();
      }

      /**
       * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.operand.UpperCase)
       */
      @Override
      public void visit(UpperCase node) throws VisitException
      {
         throw new NotImplementedException();
      }

      /**
       * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.operand.PropertyValue)
       */
      @Override
      public void visit(PropertyValue node) throws VisitException
      {
         sortField =
            new SortField(FieldNames.createPropertyFieldName(node.getPropertyName()), order == Order.DESCENDING);
      }
   }

   public Query getConstrainQuery(Constraint constraint, Map<String, Object> bindVariablesValues) throws VisitException
   {
      LuceneQueryBuilder luceneQueryBuilder =
         new LuceneQueryBuilder(fieldNameResolver, nameConverter, pathSplitter, bindVariablesValues);
      Visitors.visit(constraint, luceneQueryBuilder);
      return luceneQueryBuilder.getQuery();
   }

   /**
    * @return the indexDataManager
    */
   public TransactionableIndexDataManager getIndexDataManager()
   {
      return indexDataManager;
   }

   /**
    * Collect hits from  offset untill limit exceeded
    *
    */
   @Deprecated
   private class LimitedHitCollector extends HitCollector
   {
      private final int limit;

      private final int offset;

      private final List<ScoreDoc> scoreDocs;

      private final static int MAX_INIT_SIZE = 1000;

      /**
       * Number of skipped hits;
       */
      private int skipped;

      /**
       * @param limit
       * @param offset
       */
      public LimitedHitCollector(int offset, int limit)
      {
         super();
         this.limit = limit;
         this.offset = offset;
         int hits = limit + offset;
         this.scoreDocs = new ArrayList<ScoreDoc>(hits < MAX_INIT_SIZE ? hits : MAX_INIT_SIZE);
      }

      /**
       * @see org.apache.lucene.search.HitCollector#collect(int, float)
       */
      @Override
      public void collect(int doc, float score)
      {
         //skip if needed
         if (skipped < offset)
         {
            skipped++;
         }
         else
         {
            scoreDocs.add(new ScoreDoc(doc, score));
            if (scoreDocs.size() >= limit)
            {
               throw new LimitedException("Limit " + limit + " of hits exceeded");
            }
         }
      }

      /**
       * @return the scoreDocs
       */
      public List<ScoreDoc> getScoreDocs()
      {
         return scoreDocs;
      }

   }

   /**
    * Throw if limit of hits exceeded 
    *
    */
   private class LimitedException extends RuntimeException
   {

      /**
       * @param string
       */
      public LimitedException(String string)
      {
         super(string);
      }

      /**
       * 
       */
      private static final long serialVersionUID = 7205608205464803956L;

   }

   /**
    * 
    * Return set of field in index. 
    */
   public static class FieldNameResolver
   {
      private final TransactionableIndexDataManager indexDataManager;

      /**
       * @param indexDataManager
       */
      public FieldNameResolver(TransactionableIndexDataManager indexDataManager)
      {
         super();
         this.indexDataManager = indexDataManager;
      }

      /**
       * Return set of field in index. 
       * @return
       */
      public Set<String> getFieldNames() throws IndexException
      {
         final Set<String> fildsSet = new HashSet<String>();
         @SuppressWarnings("unchecked")
         final Collection fields = indexDataManager.getIndexReader().getFieldNames(IndexReader.FieldOption.ALL);
         for (final Object field : fields)
         {
            fildsSet.add((String)field);
         }
         return fildsSet;
      }
   }

}
