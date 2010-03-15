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

import org.exoplatform.services.jcr.datamodel.InternalQName;
import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.xcmis.search.qom.AbstractQueryObjectModelFactory;
import org.xcmis.sp.jcr.exo.query.index.LuceneIndexingService;

import javax.jcr.RepositoryException;
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
public class CmisQueryObjectModelFactory<Q> extends AbstractQueryObjectModelFactory
{

   /** The Constant DEFAULT_SCORE_COLUMN_NAME. */
   public static final String DEFAULT_SCORE_COLUMN_NAME = "SEARCH_SCORE";

   /** The indexing service. */
   private final LuceneIndexingService indexingService;

   /** The location factory. */
   private final LocationFactory locationFactory;

   /**
    * The Constructor.
    * 
    * @param indexingService the indexing service
    * @param locationFactory the location factory
    */
   public CmisQueryObjectModelFactory(final LuceneIndexingService indexingService, final LocationFactory locationFactory)
   {
      super();
      this.indexingService = indexingService;
      this.locationFactory = locationFactory;
      try
      {
         init(locationFactory);
      }
      catch (RepositoryException e)
      {
         throw new RuntimeException(e.getLocalizedMessage(), e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public CmisQueryObjectModel createQuery(Source source, Constraint constraint, Ordering[] orderings, Column[] columns)
      throws InvalidQueryException, RepositoryException
   {
      return new CmisQueryObjectModel(source, constraint, orderings, columns, indexingService, locationFactory);
   }

   /**
    * This method is implementation of abstract method of AbstractQueryObjectModelFactory.
    * It is useless.
    * 
    * @param selector the selector
    * @param constraint the constraint
    * @param orderings the orderings
    * @param columns the columns
    * @param language the language
    * 
    * @return the query object model
    * 
    * @throws InvalidQueryException the invalid query exception
    * @throws RepositoryException the repository exception
    */
   @Deprecated
   public QueryObjectModel createQuery(Source selector, Constraint constraint, Ordering[] orderings, Column[] columns,
      String language) throws InvalidQueryException, RepositoryException
   {
      // this method is useless in cmis 
      throw new UnsupportedOperationException("CreateQuery with language parameter is useless in cmis.");
   }

   /**
    * Score column.
    * 
    * @param selectorName the selector name
    * @param columnName the column name
    * @return the score column
    * @throws InvalidQueryException the invalid query exception
    */
   public ScoreColumn scoreColumn(String selectorName, String columnName) throws InvalidQueryException
   {
      if (columnName == null)
      {
         columnName = DEFAULT_SCORE_COLUMN_NAME;
      }
      InternalQName selectorQName = null;
      // check names
      try
      {
         if (selectorName != null)
         {
            selectorQName = locationFactory.parseJCRName(selectorName).getInternalName();
         }
      }
      catch (RepositoryException e)
      {
         throw new InvalidQueryException(e.getLocalizedMessage(), e);
      }
      return new ScoreColumn(locationFactory, selectorQName, columnName);
   }
}
