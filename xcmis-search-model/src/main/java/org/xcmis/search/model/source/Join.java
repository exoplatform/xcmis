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
package org.xcmis.search.model.source;

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

   /**
    * @param left - Left node-tuple source.
    * @param right - Right node-tuple source.
    * @param joinType - Join type.
    * @param joinCondition - join condition.
    */
   public Join(Source left, Source right, JoinType joinType,
      JoinCondition joinCondition)
   {
      this.left = left;
      this.right = right;
      this.joinType = joinType;
      this.joinCondition = joinCondition;
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
      if (obj == this)
      {
         return true;
      }
      if (obj instanceof Join)
      {
         Join that = (Join)obj;
         if (!this.joinType.equals(that.joinType))
         {
            return false;
         }
         if (!this.left.equals(that.left))
         {
            return false;
         }
         if (!this.right.equals(that.right))
         {
            return false;
         }
         if (!this.joinCondition.equals(that.joinCondition))
         {
            return false;
         }
         return true;
      }
      return false;
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
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return Visitors.readable(this);
   }

}
