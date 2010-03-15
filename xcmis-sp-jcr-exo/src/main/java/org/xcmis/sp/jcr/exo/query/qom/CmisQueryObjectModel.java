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

package org.xcmis.sp.jcr.exo.query.qom;

import org.apache.lucene.search.Query;
import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.xcmis.search.NativeQuery;
import org.xcmis.search.NativeQueryBuilder;
import org.xcmis.search.index.IndexException;
import org.xcmis.search.lucene.search.visitor.DefaultQueryObjectModelValidationVisitor;
import org.xcmis.search.lucene.search.visitor.QueryObjectModelVisitor;
import org.xcmis.search.qom.AbstractQueryObjectModelNode;
import org.xcmis.search.qom.column.ColumnImpl;
import org.xcmis.search.qom.ordering.OrderingImpl;
import org.xcmis.search.result.ScoredRow;
import org.xcmis.sp.jcr.exo.query.index.LuceneIndexingService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.qom.Column;
import javax.jcr.query.qom.Constraint;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.Source;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34027 2009-07-15 23:26:43Z
 *          aheritier $
 */
public class CmisQueryObjectModel extends AbstractQueryObjectModelNode implements QueryObjectModel
{

   /** The builder. */
   private final NativeQueryBuilder<Query> builder;

   /** The columns[]. */
   private Column[] columns;

   /** The constraint. */
   private final Constraint constraint;

   /** The indexing service. */
   private final LuceneIndexingService indexingService;

   /** The orderings[]. */
   private final Ordering[] orderings;

   /** The  string[] selectors . */
   private Set<String> selectors;

   /** The source. */
   private final Source source;

   /** The search all versions. */
   private boolean searchAllVersions = false;

   /**
    * The Constructor.
    * 
    * @param locationFactory the location factory
    * @param source the source
    * @param constraint the constraint
    * @param orderings the orderings
    * @param columns the columns
    * @param indexingService the indexing service
    * 
    * @throws IndexException the index exception
    * @throws InvalidQueryException the invalid query exception
    */
   public CmisQueryObjectModel(final Source source, final Constraint constraint, final Ordering[] orderings,
      final Column[] columns, final LuceneIndexingService indexingService, final LocationFactory locationFactory)
      throws IndexException, InvalidQueryException
   {
      super(locationFactory);
      this.source = source;
      this.constraint = constraint;
      this.orderings = orderings;
      this.columns = columns;
      // TODO create constant
      this.builder = indexingService.getQueryBuilder("CmisSQL");
      this.indexingService = indexingService;
      validate();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object accept(QueryObjectModelVisitor visitor, Object context) throws Exception
   {
      return visitor.visit(this, context);
   }

   /**
    * Execute.
    * 
    * @return the list< scored row>
    * @throws RepositoryException the repository exception
    */
   public List<ScoredRow> execute() throws RepositoryException
   {
      List<ScoredRow> hits;
      try
      {
         final NativeQuery<Query> nativeQuery = this.builder.createNativeQuery(this, new HashMap<String, Value>());
         hits = nativeQuery.search(this.indexingService);
         hits = nativeQuery.filter(hits);
         hits = nativeQuery.sort(hits);
      }
      catch (final IOException e)
      {
         throw new RepositoryException(e);
      }

      return hits;
   }

   /**
    * {@inheritDoc}
    */
   public Column[] getColumns()
   {
      return this.columns != null ? this.columns : new ColumnImpl[0];
   }

   /**
    * {@inheritDoc}
    */
   public Constraint getConstraint()
   {
      return this.constraint;
   }

   /**
    * {@inheritDoc}
    */
   public Ordering[] getOrderings()
   {
      return this.orderings != null ? this.orderings : new OrderingImpl[0];
   }

   /**
    * Gets the search all versions.
    * 
    * @return the searchAllVersions
    */
   public Boolean getSearchAllVersions()
   {
      return searchAllVersions;
   }

   /**
    * Gets the selectors.
    * 
    * @return the selectors
    */
   public Set<String> getSelectors()
   {
      return selectors;
   }

   /**
    * {@inheritDoc}
    */
   public Source getSource()
   {
      return this.source;
   }

   /**
    * Sets the search all versions.
    * 
    * @param searchAllVersions the searchAllVersions to set
    */
   public void setSearchAllVersions(Boolean searchAllVersions)
   {
      this.searchAllVersions = searchAllVersions;
   }

   /**
    * Internal validate.
    * 
    * @throws InvalidQueryException if an query error
    */
   private void validate() throws InvalidQueryException
   {
      final DefaultQueryObjectModelValidationVisitor validationVisitor = new DefaultQueryObjectModelValidationVisitor();
      final Map<String, Object> context = new HashMap<String, Object>();
      try
      {
         this.accept(validationVisitor, context);
         this.selectors = validationVisitor.getSelectors();
         // this.columns = validationVisitor.getColumns();
      }
      catch (final InvalidQueryException e)
      {
         throw e;
      }
      catch (final Exception e)
      {
         throw new InvalidQueryException(e.getLocalizedMessage(), e);
      }
   }

}
