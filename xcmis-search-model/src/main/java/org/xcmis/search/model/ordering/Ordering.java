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
package org.xcmis.search.model.ordering;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.xcmis.search.QueryObjectModelVisitor;
import org.xcmis.search.VisitException;
import org.xcmis.search.Visitors;
import org.xcmis.search.model.QueryElement;
import org.xcmis.search.model.operand.DynamicOperand;

/**
 * A specification of the ordering for the results.
 */
public class Ordering implements QueryElement
{
   private static final long serialVersionUID = 1L;

   private final DynamicOperand operand;

   private final Order order;

   private final int hcode;

   /**
    * Create a new ordering specification, given the supplied operand and order.
    * 
    * @param operand
    *           the operand being ordered
    * @param order
    *           the order type
    * @throws IllegalArgumentException
    *            if the operand or order type is null
    */
   public Ordering(DynamicOperand operand, Order order)
   {
      Validate.notNull(operand, "The operand argument may not be null");
      Validate.notNull(order, "The order argument may not be null");

      this.operand = operand;
      this.order = order;

      this.hcode = new HashCodeBuilder()
                   .append(operand)
                   .append(order)
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
      Ordering rhs = (Ordering)obj;
      return new EqualsBuilder()
                    .append(operand, rhs.operand)
                    .append(order, rhs.order)
                    .isEquals();
   }

   /**
    * Get the operand being ordered.
    * 
    * @return the operand; never null
    */
   public final DynamicOperand getOperand()
   {
      return operand;
   }

   /**
    * The order type.
    * 
    * @return the type; never null
    */
   public final Order getOrder()
   {
      return order;
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