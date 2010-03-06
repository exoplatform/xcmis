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
package org.xcmis.search.model.source.join;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.xcmis.search.QueryObjectModelVisitor;
import org.xcmis.search.VisitException;
import org.xcmis.search.Visitors;
import org.xcmis.search.model.column.Column;
import org.xcmis.search.model.source.SelectorName;

/**
 * A join condition that tests whether a property on a node is equal to a
 * property on another node. A node-tuple satisfies the constraint only if:
 * <ul>
 * <li>the {@code selector1Name} node has a property named {@code property1Name}
 * , and</li>
 * <li>the {@code selector2Name} node has a property named {@code property2Name}
 * , and</li>
 * <li>the value of property {@code property1Name} is equal to the value of
 * property {@code property2Name}</li>
 * </ul>
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: EquiJoinConditionImpl.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class EquiJoinCondition extends JoinCondition
{
   /**
    * 
    */
   private static final long serialVersionUID = 5357188353322766280L;

   private final SelectorName selector1Name;

   private final String property1Name;

   private final SelectorName selector2Name;

   private final String property2Name;

   private final int hcode;

   /**
    * Create an equi-join condition, given the columns.
    * 
    * @param column1
    *           the column for the left-side of the join; never null
    * @param column2
    *           the column for the right-side of the join; never null
    */
   public EquiJoinCondition(Column column1, Column column2)
   {
      this(column1.getSelectorName(), column1.getPropertyName(), column2.getSelectorName(), column2.getPropertyName());
   }

   /**
    * Create an equi-join condition, given the names of the selector and
    * property for the left- and right-hand-side of the join.
    * 
    * @param selector1Name
    *           the selector name appearing on the left-side of the join; never
    *           null
    * @param property1Name
    *           the property name for the left-side of the join; never null
    * @param selector2Name
    *           the selector name appearing on the right-side of the join; never
    *           null
    * @param property2Name
    *           the property name for the right-side of the join; never null
    */
   public EquiJoinCondition(SelectorName selector1Name, String property1Name, SelectorName selector2Name,
      String property2Name)
   {
      Validate.notNull(selector1Name, "The selector1Name argument may not be null");
      Validate.notNull(property1Name, "The property1Name argument may not be null");
      Validate.notNull(selector2Name, "The selector2Name argument may not be null");
      Validate.notNull(property2Name, "The property2Name argument may not be null");

      this.selector1Name = selector1Name;
      this.property1Name = property1Name;
      this.selector2Name = selector2Name;
      this.property2Name = property2Name;

      this.hcode = new HashCodeBuilder()
                   .append(selector1Name)
                   .append(property1Name)
                   .append(selector2Name)
                   .append(property2Name)
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
      EquiJoinCondition rhs = (EquiJoinCondition)obj;
      return new EqualsBuilder()
                    .append(selector1Name, rhs.selector1Name)
                    .append(property1Name, rhs.property1Name)
                    .append(selector2Name, rhs.selector2Name)
                    .append(property2Name, rhs.property2Name)
                    .isEquals();
   }

   /**
    * Get the name of the property that appears on the left-side of the join.
    * 
    * @return the property name for the left-side of the join; never null
    */
   public final String getProperty1Name()
   {
      return property1Name;
   }

   /**
    * Get the name of the property that appears on the left-side of the join.
    * 
    * @return the property name for the left-side of the join; never null
    */
   public final String getProperty2Name()
   {
      return property2Name;
   }

   /**
    * Get the name of the selector that appears on the left-side of the join.
    * 
    * @return the selector name appearing on the left-side of the join; never
    *         null
    */
   public final SelectorName getSelector1Name()
   {
      return selector1Name;
   }

   /**
    * Get the name of the selector that appears on the right-side of the join.
    * 
    * @return the selector name appearing on the right-side of the join; never
    *         null
    */
   public final SelectorName getSelector2Name()
   {
      return selector2Name;
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
