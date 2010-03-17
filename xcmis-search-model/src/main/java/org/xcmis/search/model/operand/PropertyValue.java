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
package org.xcmis.search.model.operand;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.xcmis.search.QueryObjectModelVisitor;
import org.xcmis.search.VisitException;
import org.xcmis.search.Visitors;
import org.xcmis.search.model.constraint.Comparison;
import org.xcmis.search.model.source.SelectorName;

/**
 * A dynamic operand that evaluates to the value(s) of a property on a selector,
 * used in a {@link Comparison} constraint.
 */

public class PropertyValue extends DynamicOperand
{
   private static final long serialVersionUID = 1L;

   private final String propertyName;

   private final int hcode;

   /**
    * Create a dynamic operand that evaluates to the property values of the node
    * identified by the selector.
    * 
    * @param selectorName
    *           the name of the selector
    * @param propertyName
    *           the name of the property
    * @throws IllegalArgumentException
    *            if the selector name or property name are null
    */
   public PropertyValue(SelectorName selectorName, String propertyName)
   {
      super(selectorName);

      Validate.notNull(propertyName, "The propertyName argument may not be null");
      this.propertyName = propertyName;
      this.hcode = new HashCodeBuilder()
                   .appendSuper(super.hashCode())
                   .append(propertyName)
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
      PropertyValue rhs = (PropertyValue)obj;
      return new EqualsBuilder()
                    .appendSuper(super.equals(obj))
                    .append(propertyName, rhs.propertyName)
                    .isEquals();
   }

   /**
    * Get the name of the property.
    * 
    * @return the property name; never null
    */
   public final String getPropertyName()
   {
      return propertyName;
   }

   /**
    * Get the selector symbol upon which this operand applies.
    * 
    * @return the one selector names used by this operand; never null
    */
   public SelectorName getSelectorName()
   {
      return getSelectorNames().iterator().next();
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
