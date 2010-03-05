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
import org.xcmis.search.model.source.SelectorName;

/**
 * A constraint that evaluates to true only when a named property exists on a node.
 */
public class PropertyExistence extends Constraint
{
   private static final long serialVersionUID = 1L;

   private final String propertyName;

   private final SelectorName selectorName;

   /**
    * Create a constraint requiring that a property exist on a node.
    * 
    * @param selectorName the name of the node selector
    * @param propertyName the name of the property that must exist
    */
   public PropertyExistence(SelectorName selectorName, String propertyName)
   {
      this.selectorName = selectorName;
      this.propertyName = propertyName;
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
      if (obj instanceof PropertyExistence)
      {
         PropertyExistence that = (PropertyExistence)obj;
         return this.selectorName.equals(that.selectorName) && this.propertyName.equals(that.propertyName);
      }
      return false;
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
     * Get the name of the selector.
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
