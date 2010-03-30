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
 * A join condition that evaluates to true only when the named node is a
 * descendant of another named node.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: DescendantNodeJoinConditionImpl.java 2 2010-02-04 17:21:49Z
 *          andrew00x $
 */
public class DescendantNodeJoinCondition extends JoinCondition
{

   /**
    * 
    */
   private static final long serialVersionUID = 2732835610719207500L;

   private final SelectorName descendantSelectorName;

   private final SelectorName ancestorSelectorName;

   private final int hcode;

   /**
    * Create a join condition that determines whether the node identified by the
    * descendant selector is indeed a descendant of the node identified by the
    * ancestor selector.
    * 
    * @param ancestorSelectorName
    *           the name of the ancestor selector
    * @param descendantSelectorName
    *           the name of the descendant selector
    */
   public DescendantNodeJoinCondition(SelectorName ancestorSelectorName, SelectorName descendantSelectorName)
   {

      Validate.notNull(ancestorSelectorName, "The ancestorSelectorName argument may not be null");
      Validate.notNull(descendantSelectorName, "The descendantSelectorName argument may not be null");

      this.descendantSelectorName = descendantSelectorName;
      this.ancestorSelectorName = ancestorSelectorName;
      this.hcode = new HashCodeBuilder()
                   .append(ancestorSelectorName)
                   .append(descendantSelectorName)
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
      DescendantNodeJoinCondition rhs = (DescendantNodeJoinCondition)obj;
      return new EqualsBuilder()
                    .append(descendantSelectorName, rhs.descendantSelectorName)
                    .append(ancestorSelectorName, rhs.ancestorSelectorName)
                    .isEquals();
   }



   /**
    * Get the name of the selector for the ancestor node.
    * 
    * @return the selector name of the ancestor node; never null
    */
   public final SelectorName getAncestorSelectorName()
   {
      return ancestorSelectorName;
   }

   /**
    * Get the name of the selector for the descedant node.
    * 
    * @ @Overridereturn the selector name of the descendant node; never null
    */
   public final SelectorName getDescendantSelectorName()
   {
      return descendantSelectorName;
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
