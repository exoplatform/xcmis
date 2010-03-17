/**
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

package org.xcmis.sp.jcr.exo.query;

import org.xcmis.search.model.Query;
import org.xcmis.search.model.column.Column;
import org.xcmis.search.parser.CmisQueryParser;
import org.xcmis.search.result.ScoredRow;
import org.xcmis.sp.jcr.exo.query.index.JcrIndexingService;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.object.ItemsIterator;
import org.xcmis.spi.query.QueryHandler;
import org.xcmis.spi.query.Result;
import org.xcmis.spi.query.Score;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: QueryHandlerImpl.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class QueryHandlerImpl implements QueryHandler
{

   private JcrIndexingService indexingService;

   private QueryNameResolver resolver;

   private CmisQueryParser queryParser;

   public QueryHandlerImpl(JcrIndexingService indexingService, QueryNameResolver resolver)
   {
      super();
      this.indexingService = indexingService;
      this.resolver = resolver;
      this.queryParser = new CmisQueryParser();
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<Result> handleQuery(org.xcmis.spi.query.Query query) throws InvalidArgumentException,
      RepositoryException
   {
      try
      {
         Query qom = queryParser.parseQuery(query.getStatement());

         //qom.setSearchAllVersions(query.isSearchAllVersions());
         List<ScoredRow> result = Collections.EMPTY_LIST;//qom.execute();
         return new QueryResultIterator(result, new ArrayList<String>(Collections.EMPTY_LIST), qom);
      }

      catch (org.xcmis.search.InvalidQueryException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return null;
   }

   //
   //   private CmisQueryObjectModel createQOM(String statement) throws InvalidArgumentException
   //   {
   //      CmisQueryObjectModelFactory<Query> factory = indexingService.getQOMFactory();
   //      try
   //      {
   //         final CMISSQLLexer lexer = new CMISSQLLexer(new ANTLRStringStream(statement));
   //         final CommonTokenStream tokens = new CommonTokenStream(lexer);
   //         // process parsing
   //         final CMISSQLParser parser = new CMISSQLParser(tokens);
   //         final CMISSQLParser.query_return result = parser.query();
   //         // check exceptions
   //         if (lexer.hasExceptions())
   //         {
   //            throw new InvalidArgumentException(lexer.getExceptionMessage());
   //         }
   //         if (parser.hasExceptions())
   //         {
   //            throw new InvalidArgumentException(parser.getExceptionMessage());
   //         }
   //
   //         // process query build
   //         final CommonTree tree = (CommonTree)result.getTree();
   //         final CommonTreeNodeStream nodes = new CommonTreeNodeStream(tree);
   //         final CMISSQLTreeWalker treeWalker = new CMISSQLTreeWalker(nodes);
   //         CmisQueryObjectModel qom =
   //            treeWalker.query(factory, resolver, new JcrValueFactoryAdapter(indexingService.getValueFactory()));
   //         return qom;
   //      }
   //      catch (RecognitionException e)
   //      {
   //         throw new InvalidArgumentException(e.getLocalizedMessage(), e);
   //      }
   //   }

   class QueryResultIterator implements ItemsIterator<Result>
   {

      private final Iterator<ScoredRow> rows;

      private final List<String> selectors;

      private final int size;

      private final Query qom;

      private Result next;

      public QueryResultIterator(List<ScoredRow> rows, List<String> selectors, Query qom)
      {
         this.size = rows.size();
         this.rows = rows.iterator();
         this.selectors = selectors;
         this.qom = qom;
         fetchNext();
      }

      /**
       * {@inheritDoc}
       */
      public boolean hasNext()
      {
         return next != null;
      }

      /**
       * {@inheritDoc}
       */
      public Result next()
      {
         if (next == null)
         {
            throw new NoSuchElementException();
         }
         Result r = next;
         fetchNext();
         return r;
      }

      /**
       * {@inheritDoc}
       */
      public void remove()
      {
         throw new UnsupportedOperationException("remove");
      }

      /**
       * {@inheritDoc}
       */
      public long size()
      {
         return size;
      }

      /**
       * {@inheritDoc}
       */
      public void skip(long skip) throws NoSuchElementException
      {
         while (skip-- > 0)
         {
            next();
         }
      }

      /**
       * To fetch next <code>Result</code>.
       */
      protected void fetchNext()
      {
         next = null;
         while (next == null && rows.hasNext())
         {
            ScoredRow row = rows.next();
            for (String selectorName : selectors)
            {
               String objectId = row.getNodeIdentifer(selectorName);
               List<String> properties = null;
               Score score = null;
               for (Column column : qom.getColumns())
               {
                  //TODO check
                  if (true)
                  {
                     score = new Score(column.getColumnName(), BigDecimal.valueOf(row.getScore()));
                  }
                  else
                  {
                     if (selectorName.equals(column.getSelectorName()))
                     {
                        if (column.getPropertyName() != null)
                        {
                           if (properties == null)
                           {
                              properties = new ArrayList<String>();
                           }
                           properties.add(column.getPropertyName());
                        }
                     }
                  }
               }
               next = new ResultImpl(objectId, //
                  properties == null ? null : properties.toArray(new String[properties.size()]), //
                  score);
            }
         }
      }

   }

}
