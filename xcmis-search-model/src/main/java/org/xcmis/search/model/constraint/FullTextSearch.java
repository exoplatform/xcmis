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
import org.xcmis.search.model.operand.StaticOperand;
import org.xcmis.search.model.source.SelectorName;

/**
 * A constraint that evaluates to true only when a full-text search applied to the search scope results in positive findings. If a
 * property name is supplied, then the search is limited to the value(s) of the named property on the node(s) in the search scope.
 */
public class FullTextSearch extends Constraint
{
   /**
    * 
    */
   private static final long serialVersionUID = 565310580065900807L;

   /**
    * Full-text search expression.
    */
   private final StaticOperand fullTextSearchExpression;

   /**
    * Name of the property.
    */
   private final String propertyName;

   /**
    * Name of the selector against which to apply this constraint.
    */
   private final SelectorName selectorName;

   /**
    * Create a constraint defining a full-text search against the property values on node within the search scope.
    * 
    * @param selectorName the name of the node selector defining the search scope
    * @param propertyName the name of the property to be searched; may be null if all property values are to be searched
    * @param fullTextSearchExpression the search expression
    */
   public FullTextSearch(SelectorName selectorName, String propertyName, StaticOperand fullTextSearchExpression)
   {
      this.selectorName = selectorName;
      this.propertyName = propertyName;
      this.fullTextSearchExpression = fullTextSearchExpression;
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
      if (obj instanceof FullTextSearch)
      {
         FullTextSearch that = (FullTextSearch)obj;
         if (!this.selectorName.equals(that.selectorName))
         {
            return false;
         }
         if (!this.propertyName.equals(that.propertyName))
         {
            return false;
         }
         if (!this.fullTextSearchExpression.equals(that.fullTextSearchExpression))
         {
            return false;
         }
         return true;
      }
      return false;
   }

   /**
    * Get the full-text search expression, as a string.
    * 
    * @return the search expression; never null
    */
   public final StaticOperand getFullTextSearchExpression()
   {
      return fullTextSearchExpression;
   }

   /**
    * Get the name of the property that is to be searched.
    * 
    * @return the property name; never null
    */
   public final String getPropertyName()
   {
      return propertyName;
   }

   /**
    * Get the name of the selector that is to be searched
    * 
    * @return the selector name; never null
    */
   public final SelectorName getSelectorName()
   {
      return selectorName;
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
