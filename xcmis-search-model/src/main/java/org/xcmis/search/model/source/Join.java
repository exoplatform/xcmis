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
package org.xcmis.search.model.source;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.xcmis.search.QueryObjectModelVisitor;
import org.xcmis.search.VisitException;
import org.xcmis.search.Visitors;
import org.xcmis.search.model.source.join.JoinCondition;
import org.xcmis.search.model.source.join.JoinType;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: JoinImpl.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class Join extends Source
{
   /**
    * 
    */
   private static final long serialVersionUID = 1211161706067474072L;

   /**
    * Join condition.
    */
   private final JoinCondition joinCondition;

   /**
    * Join type.
    */
   private final JoinType joinType;

   /**
    * Left node-tuple source.
    */
   private final Source left;

   /**
    * Right node-tuple source.
    */
   private final Source right;

   private final int hcode;

   /**
    * @param left
    *           - Left node-tuple source.
    * @param right
    *           - Right node-tuple source.
    * @param joinType
    *           - Join type.
    * @param joinCondition
    *           - join condition.
    */
   public Join(Source left, Source right, JoinType joinType, JoinCondition joinCondition)
   {
      Validate.notNull(left, "The left argument may not be null");
      Validate.notNull(right, "The right argument may not be null");
      Validate.notNull(joinType, "The joinType argument may not be null");
      Validate.notNull(joinCondition, "The joinCondition argument may not be null");

      this.left = left;
      this.right = right;
      this.joinType = joinType;
      this.joinCondition = joinCondition;

      this.hcode = new HashCodeBuilder()
                   .append(left)
                   .append(right)
                   .append(right)
                   .append(joinCondition)
                   .toHashCode();

   }

   /**
    * {@inheritDoc}
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
      Join rhs = (Join)obj;
      return new EqualsBuilder()
                    .append(left, rhs.left)
                    .append(right, rhs.right)
                    .append(joinType, rhs.joinType)
                    .append(joinCondition, rhs.joinCondition)
                    .isEquals();
   }

   /**
    * Get the join condition
    * 
    * @return the join condition; never null
    */
   public final JoinCondition getJoinCondition()
   {
      return joinCondition;
   }

   /**
    * Get the source that represents the left-hand-side of the join.
    * 
    * @return the left-side source; never null
    */
   public final Source getLeft()
   {
      return left;
   }

   /**
    * Get the source that represents the right-hand-side of the join.
    * 
    * @return the right-side source; never null
    */
   public final Source getRight()
   {
      return right;
   }

   /**
    * Get the type of join.
    * 
    * @return the join type; never null
    */
   public final JoinType getType()
   {
      return joinType;
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
