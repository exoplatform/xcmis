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
package org.xcmis.search.model.constraint;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.xcmis.search.QueryObjectModelVisitor;
import org.xcmis.search.VisitException;
import org.xcmis.search.Visitors;

/**
 * A constraint that evaluates to true when either of the other constraints
 * evaluates to true.
 */
public class Or extends Constraint
{
   private static final long serialVersionUID = 1L;

   private final Constraint left;

   private final Constraint right;

   private final int hcode;

   /**
    * Create a constraint that evaluates to true if either of the two supplied
    * constraints evaluates to true.
    * 
    * @param left
    *           the left constraint
    * @param right
    *           the right constraint
    * @throws IllegalArgumentException
    *            if the left or right constraints are null
    */
   public Or(Constraint left, Constraint right)
   {
      Validate.notNull(left, "The left argument may not be null");
      Validate.notNull(right, "The right argument may not be null");
      this.left = left;
      this.right = right;
      this.hcode = new HashCodeBuilder()
                   .append(left)
                   .append(right)
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
      if (obj == null) { return false; }
      if (obj == this) { return true; }
      if (obj.getClass() != getClass()) {
        return false;
      }
      Or rhs = (Or) obj;
      return new EqualsBuilder()
                    .append(left, rhs.left)
                    .append(right, rhs.right)
                    .isEquals();

   }

   /**
    * Get the left-hand constraint.
    * 
    * @return the left-hand constraint; never null
    */
   public final Constraint getLeft()
   {
      return left;
   }

   /**
    * Get the right-hand constraint.
    * 
    * @return the right-hand constraint; never null
    */
   public final Constraint getRight()
   {
      return right;
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
