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
package org.xcmis.search.model.constraint;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.xcmis.search.QueryObjectModelVisitor;
import org.xcmis.search.VisitException;
import org.xcmis.search.Visitors;

/**
 * A two-place logical connective that has the value true if both of its operands are true, otherwise a value of false.
 * 
 */
public class And extends Constraint
{
   private final Constraint left;

   private final Constraint right;

   private final int hc;

   public And(Constraint left, Constraint right)
   {
      Validate.notNull(left, "The left argument may not be null");
      Validate.notNull(right, "The right argument may not be null");
      this.left = left;
      this.right = right;
      this.hc = new HashCodeBuilder().append(left).append(right).toHashCode();
   }

   /**
   * Get the constraint that is on the left-hand-side of the AND operation.
   * 
   * @return the left-hand-side constraint
   */
   public final Constraint getLeft()
   {
      return left;
   }

   /**
   * Get the constraint that is on the right-hand-side of the AND operation.
   * 
   * @return the right-hand-side constraint
   */
   public final Constraint getRight()
   {
      return right;
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
      return hc;
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
      And rhs = (And)obj;
      return new EqualsBuilder()
                    .append(left, rhs.left)
                    .append(right, rhs.right)
                    .isEquals();

   }

   /**
    * @see org.xcmis.search.model.QueryElement#accept(org.xcmis.search.QueryObjectModelVisitor)
    */
   public void accept(QueryObjectModelVisitor visitor) throws VisitException
   {
      visitor.visit(this);

   }

}
