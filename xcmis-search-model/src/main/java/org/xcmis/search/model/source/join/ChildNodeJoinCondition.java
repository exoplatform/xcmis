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
import org.xcmis.search.model.source.SelectorName;

/**
 * 
 * A join condition that evaluates to true only when the named child node is
 * indeed a child of the named parent node.
 * 
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: ChildNodeJoinConditionImpl.java 2 2010-02-04 17:21:49Z
 *          andrew00x $
 */
public class ChildNodeJoinCondition extends JoinCondition
{

   private static final long serialVersionUID = -3147655062331492585L;

   private final SelectorName childSelectorName;

   private final SelectorName parentSelectorName;

   private final int hcode;

   /**
    * Create a join condition that determines whether the node identified by the
    * child selector is a child of the node identified by the parent selector.
    * 
    * @param parentSelectorName
    *           the first selector
    * @param childSelectorName
    *           the second selector
    */
   public ChildNodeJoinCondition(SelectorName parentSelectorName, SelectorName childSelectorName)
   {
      Validate.notNull(parentSelectorName, "The parentSelectorName argument may not be null");
      Validate.notNull(childSelectorName, "The childSelectorName argument may not be null");

      this.childSelectorName = childSelectorName;
      this.parentSelectorName = parentSelectorName;

      this.hcode = new HashCodeBuilder()
                   .append(parentSelectorName)
                   .append(childSelectorName)
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
      ChildNodeJoinCondition rhs = (ChildNodeJoinCondition)obj;
      return new EqualsBuilder()
                    .append(childSelectorName, rhs.childSelectorName)
                    .append(parentSelectorName, rhs.parentSelectorName)
                    .isEquals();
   }



   /**
    * Get the name of the selector that represents the child.
    * 
    * @return the selector name of the child node; never null
    */
   public final SelectorName getChildSelectorName()
   {
      return childSelectorName;
   }

   /**
    * Get the name of the selector that represents the parent.
    * 
    * @return the selector name of the parent node; never null
    */
   public final SelectorName getParentSelectorName()
   {
      return parentSelectorName;
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
