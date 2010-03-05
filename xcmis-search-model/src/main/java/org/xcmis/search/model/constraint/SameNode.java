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
 * A constraint requiring that the selected node is reachable by the supplied absolute path
 */

public class SameNode extends Constraint
{
   private static final long serialVersionUID = 1L;

   private final String path;

   private final SelectorName selectorName;

   /**
    * Create a constraint requiring that the node identified by the selector is reachable by the supplied absolute path.
    * 
    * @param selectorName the name of the selector
    * @param path the absolute path
    * @throws IllegalArgumentException if the selector name or path are null
    */
   public SameNode(SelectorName selectorName, String path)
   {
      this.selectorName = selectorName;
      this.path = path;
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
      if (obj instanceof SameNode)
      {
         SameNode that = (SameNode)obj;
         if (!this.selectorName.equals(that.selectorName))
         {
            return false;
         }
         if (!this.path.equals(that.path))
         {
            return false;
         }
         return true;
      }
      return false;
   }

   /**
    * Get the absolute path for the node
    * 
    * @return the absolute path; never null
    */
   public final String getPath()
   {
      return path;
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
