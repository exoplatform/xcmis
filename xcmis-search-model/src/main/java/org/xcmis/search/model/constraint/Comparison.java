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

import org.xcmis.search.QueryObjectModelVisitor;
import org.xcmis.search.VisitException;
import org.xcmis.search.Visitors;
import org.xcmis.search.model.operand.DynamicOperand;
import org.xcmis.search.model.operand.StaticOperand;

/**
 * A constraint that evaluates to true when the defined operation evaluates to true.
 */

public class Comparison extends Constraint
{

   private static final long serialVersionUID = 1L;

   private final DynamicOperand operand1;

   private final StaticOperand operand2;

   private final Operator operator;

   public Comparison(DynamicOperand operand1, Operator operator, StaticOperand operand2)
   {
      this.operand1 = operand1;
      this.operand2 = operand2;
      this.operator = operator;
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
      if (obj == this)
      {
         return true;
      }
      if (obj instanceof Comparison)
      {
         Comparison that = (Comparison)obj;
         if (!this.operator.equals(that.operator))
         {
            return false;
         }
         if (!this.operand1.equals(that.operand1))
         {
            return false;
         }
         if (!this.operand2.equals(that.operand2))
         {
            return false;
         }
         return true;
      }
      return false;
   }

   /**
    * Get the dynamic operand of this comparison.
    * 
    * @return the dynamic operand; never null
    */
   public final DynamicOperand getOperand1()
   {
      return operand1;
   }

   /**
    * Get the dynamic operand of this comparison.
    * 
    * @return the dynamic operand; never null
    */
   public final StaticOperand getOperand2()
   {
      return operand2;
   }

   /**
    * Get the operator for this comparison
    * 
    * @return the operator; never null
    */
   public final Operator getOperator()
   {
      return operator;
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
