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

package org.xcmis.search.model;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.xcmis.search.QueryObjectModelVisitor;
import org.xcmis.search.VisitException;
import org.xcmis.search.Visitors;
import org.xcmis.search.model.column.Column;
import org.xcmis.search.model.constraint.Constraint;
import org.xcmis.search.model.ordering.Ordering;
import org.xcmis.search.model.source.Source;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@exoplatform.org">Sergey Kabashnyuk</a>
 * @version $Id: Query.java 34360 2009-07-22 23:58:59Z ksm $
 *
 */

public class Query implements QueryElement, Serializable
{
   private static final long serialVersionUID = 1L;

   private final List<Column> columns;

   private final Constraint constraint;

   private final Limit limits;

   private final List<Ordering> orderings;

   private final Source source;

   /**
    * Object hash code.
    */
   private final int hcode;

   /**
    * Create a new query that uses the supplied source.
    * 
    * @param source
    *           the source
    */
   public Query(Source source)
   {
      this(source, null, Collections.<Ordering> emptyList(), Collections.<Column> emptyList(), Limit.NONE);
   }

   /**
    * Create a new query that uses the supplied source, constraint, orderings,
    * columns and limits.
    * 
    * @param source
    *           the source
    * @param constraint
    *           the constraint (or composite constraint), or null or empty if
    *           there are no constraints
    * @param orderings
    *           the specifications of how the results are to be ordered, or null
    *           if the order is to be implementation determined
    * @param columns
    *           the columns to be included in the results, or null or empty if
    *           there are no explicit columns and the actual result columns are
    *           to be implementation determiend
    * @param limit
    *           the limit for the results, or null if all of the results are to
    *           be included
    * @param isDistinct
    *           true if duplicates are to be removed from the results
    * @throws IllegalArgumentException
    *            if the source is null
    */
   public Query(Source source, Constraint constraint, List<Ordering> orderings, List<Column> columns, Limit limit)
   {
      Validate.notNull(source, "The source argument may not be null");
      this.source = source;
      this.constraint = constraint;
      this.orderings = orderings != null ? orderings : Collections.<Ordering> emptyList();
      this.limits = limit != null ? limit : Limit.NONE;;
      this.columns = columns != null ? columns : Collections.<Column> emptyList();
      this.hcode =
         new HashCodeBuilder().append(this.source).append(this.constraint).append(this.columns).append(this.limits)
            .append(this.orderings).toHashCode();

   }

   /**
    * @see org.xcmis.search.model.QueryElement#accept(org.xcmis.search.QueryObjectModelVisitor)
    */
   public void accept(QueryObjectModelVisitor visitor) throws VisitException
   {
      visitor.visit(this);
   }

   /**
    * Create a copy of this query, but that returns results that include the
    * columns specified by this query as well as the supplied columns.
    * 
    * @param columns
    *           the additional columns that should be included in the the
    *           results; may not be null
    * @return the copy of the query returning the supplied result columns; never
    *         null
    */
   public Query adding(Column... columns)
   {
      List<Column> newColumns = null;
      if (this.columns != null)
      {
         newColumns = new ArrayList<Column>(this.columns);
         for (Column column : columns)
         {
            newColumns.add(column);
         }
      }
      else
      {
         newColumns = Arrays.asList(columns);
      }
      return new Query(source, constraint, getOrderings(), newColumns, getLimits());
   }

   /**
    * Create a copy of this query, but that returns results that are ordered by
    * the {@link #getOrderings() orderings} of this column as well as those
    * supplied.
    * 
    * @param orderings
    *           the additional orderings of the result rows; may no be null
    * @return the copy of the query returning the supplied result columns; never
    *         null
    */
   public Query adding(Ordering... orderings)
   {
      List<Ordering> newOrderings = null;
      if (this.getOrderings() != null)
      {
         newOrderings = new ArrayList<Ordering>(getOrderings());
         for (Ordering ordering : orderings)
         {
            newOrderings.add(ordering);
         }
      }
      else
      {
         newOrderings = Arrays.asList(orderings);
      }
      return new Query(source, constraint, newOrderings, columns, getLimits());
   }

   /**
    * Create a copy of this query, but one that uses the supplied constraint.
    * 
    * @param constraint
    *           the constraint that should be used; never null
    * @return the copy of the query that uses the supplied constraint; never
    *         null
    */
   public Query constrainedBy(Constraint constraint)
   {
      return new Query(source, constraint, getOrderings(), columns, getLimits());
   }

   /**
    * Create a copy of this query, but one in which there are no duplicate rows
    * in the results.
    * 
    * @return the copy of the query with no duplicate result rows; never null
    */
   public Query distinct()
   {
      return new Query(source, constraint, getOrderings(), columns, getLimits());
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj)
   {
      if (obj == null)
      {
         return false;
      }
      if (obj == this)
      {
         return true;
      }
      if (obj.getClass() != getClass())
      {
         return false;
      }
      Query rhs = (Query)obj;

      return new EqualsBuilder().append(source, rhs.source).append(constraint, rhs.constraint).append(columns,
         rhs.columns).append(limits, rhs.limits).isEquals();
   }

   /**
    * Return the columns defining the query results. If there are no columns,
    * then the columns are implementation determined.
    * 
    * @return the list of columns; never null
    */
   public final List<Column> getColumns()
   {
      return columns;
   }

   /**
    * Get the constraints, if there are any.
    * 
    * @return the constraint; may be null
    */
   public final Constraint getConstraint()
   {
      return constraint;
   }

   /**
    * Get the limits associated with this query.
    * 
    * @return the limits; never null but possibly {@link Limit#isUnlimited()
    *         unlimited}
    */
   public final Limit getLimits()
   {
      return limits;
   }

   /**
    * Return the orderings for this query.
    * 
    * @return the list of orderings; never null
    */
   public final List<Ordering> getOrderings()
   {
      return orderings;
   }

   /**
    * Get the source for the results.
    * 
    * @return the query source; never null
    */
   public final Source getSource()
   {
      return source;
   }

   /**
    * Create a copy of this query, but one whose results should be ordered by
    * the supplied orderings.
    * 
    * @param orderings
    *           the result ordering specification that should be used; never
    *           null
    * @return the copy of the query that uses the supplied ordering; never null
    */
   public Query orderedBy(List<Ordering> orderings)
   {
      return new Query(source, constraint, orderings, columns, getLimits());
   }

   /**
    * Create a copy of this query, but that returns results with the supplied
    * columns.
    * 
    * @param columns
    *           the columns of the results; may not be null
    * @return the copy of the query returning the supplied result columns; never
    *         null
    */
   public Query returning(List<Column> columns)
   {
      return new Query(source, constraint, getOrderings(), columns, getLimits());
   }

   /**
    * Create a copy of this query, but one that uses the supplied limit on the
    * number of result rows.
    * 
    * @param rowLimit
    *           the limit that should be used; must be a positive number
    * @return the copy of the query that uses the supplied limit; never null
    */
   public Query withLimit(int rowLimit)
   {
      return new Query(source, constraint, getOrderings(), columns, getLimits().withRowLimit(rowLimit));
   }

   /**
    * Create a copy of this query, but one that uses the supplied offset.
    * 
    * @param offset
    *           the limit that should be used; may not be negative
    * @return the copy of the query that uses the supplied offset; never null
    */
   public Query withOffset(int offset)
   {
      return new Query(source, constraint, getOrderings(), columns, getLimits().withOffset(offset));
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return Visitors.readable(this);
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      return hcode;
   }
}