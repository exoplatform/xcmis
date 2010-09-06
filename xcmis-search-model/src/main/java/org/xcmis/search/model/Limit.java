/*
 * Copyright (C) 2009 eXo Platform SAS.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.xcmis.search.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.xcmis.search.QueryObjectModelVisitor;
import org.xcmis.search.VisitException;
import org.xcmis.search.Visitors;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@exoplatform.org">Sergey Kabashnyuk</a>
 * @version $Id: Limit.java 34360 2009-07-22 23:58:59Z ksm $
 *
 */

public class Limit implements QueryElement
{
   public static final Limit NONE = new Limit(Integer.MAX_VALUE, 0);

   private static final long serialVersionUID = 1L;

   private final int offset;

   private final int rowLimit;

   private final int hcode;

   /**
    * Create a limit on the number of rows.
    * 
    * @param rowLimit
    *           the maximum number of rows
    */
   public Limit(int rowLimit)
   {
      this(rowLimit, 0);
   }

   /**
    * Create a limit on the number of rows and the number of initial rows to
    * skip.
    * 
    * @param rowLimit
    *           the maximum number of rows
    * @param offset
    *           the number of rows to skip before beginning the results
    */
   public Limit(int rowLimit, int offset)
   {
      this.rowLimit = rowLimit;
      this.offset = offset;
      this.hcode = new HashCodeBuilder()
                   .append(rowLimit)
                   .append(offset)
                   .toHashCode();

   }

   /**
    * @see org.xcmis.search.model.QueryElement#accept(org.xcmis.search.QueryObjectModelVisitor)
    */
   public void accept(QueryObjectModelVisitor visitor) throws VisitException
   {
      visitor.visit(this);
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
      Limit rhs = (Limit)obj;
      return new EqualsBuilder()
                    .append(rowLimit, rhs.rowLimit)
                    .append(offset, rhs.offset)
                    .isEquals();
   }

   /**
    * Get the number of rows skipped before the results begin.
    * 
    * @return the offset; always 0 or a positive number
    */
   public final int getOffset()
   {
      return offset;
   }

   /**
    * Get the maximum number of rows that are to be returned.
    * 
    * @return the maximum number of rows; always positive, or equal to
    *         {@link Integer#MAX_VALUE} if there is no limit
    */
   public final int getRowLimit()
   {
      return rowLimit;
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

   /**
    * Determine whether this limit clause defines a maximum limit
    * 
    * @return true if the number of rows are limited, or false if there is no
    *         limit to the number of rows
    */
   public final boolean hasRowLimited()
   {
      return rowLimit != Integer.MAX_VALUE;
   }

   /**
    * Determine whether this limit clause defines an offset.
    * 
    * @return true if there is an offset, or false if there is no offset
    */
   public final boolean isOffset()
   {
      return offset > 0;
   }

   /**
    * Determine whether this limit clause is necessary.
    * 
    * @return true if the number of rows is not limited and there is no offset,
    *         or false otherwise
    */
   public final boolean isUnlimited()
   {
      return rowLimit == Integer.MAX_VALUE && offset == 0;
   }

   public Limit withOffset(int offset)
   {
      return new Limit(rowLimit, offset);
   }

   public Limit withRowLimit(int rowLimit)
   {
      return new Limit(rowLimit, offset);
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
}
