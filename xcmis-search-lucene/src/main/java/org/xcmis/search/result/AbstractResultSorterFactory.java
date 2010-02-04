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
package org.xcmis.search.result;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.lucene.search.visitor.QueryObjectModelVisitorImpl;
import org.xcmis.search.qom.operand.DynamicOperandImpl;
import org.xcmis.search.qom.operand.FullTextSearchScoreImpl;
import org.xcmis.search.qom.operand.LengthImpl;
import org.xcmis.search.qom.operand.LowerCaseImpl;
import org.xcmis.search.qom.operand.NodeLocalNameImpl;
import org.xcmis.search.qom.operand.NodeNameImpl;
import org.xcmis.search.qom.operand.PropertyValueImpl;
import org.xcmis.search.qom.operand.UpperCaseImpl;
import org.xcmis.search.qom.ordering.OrderingImpl;

import javax.jcr.query.qom.Ordering;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34027 2009-07-15 23:26:43Z
 *          aheritier $
 */
public abstract class AbstractResultSorterFactory extends QueryObjectModelVisitorImpl implements ResultSorterFactory
{
   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(getClass().getName());

   /**
    * {@inheritDoc}
    */
   public ResultSorter getResultSorter(Ordering[] orderings)
   {
      ResultSorter sorter = null;

      for (int i = 0; i < orderings.length; i++)
      {
         DynamicOperandImpl operand = (DynamicOperandImpl)orderings[i].getOperand();
         try
         {

            if (sorter == null)
            {
               sorter = (ResultSorter)operand.accept(this, orderings[i]);
            }
            else
            {
               sorter = new SequencedResultSorter(sorter, (ResultSorter)operand.accept(this, orderings[i]));

            }
         }
         catch (Exception e)
         {
            log.error(e.getLocalizedMessage());
         }
      }
      if (sorter == null)
      {
         sorter = getDefaultResultSorter(new String[0]);
      }

      return sorter;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(FullTextSearchScoreImpl node, Object context) throws Exception
   {
      return new FullTextSearchScoreResultSorter((Ordering)context);
   };

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(LengthImpl node, Object context) throws Exception
   {
      return new DummyResultSorter();
   };

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(LowerCaseImpl node, Object context) throws Exception
   {
      DynamicOperandImpl operand = (DynamicOperandImpl)node.getOperand();
      Ordering ordering = new OrderingImpl(operand.getLocationFactory(), operand, ((Ordering)context).getOrder());
      return operand.accept(this, ordering);
   };

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(NodeLocalNameImpl node, Object context) throws Exception
   {
      return new DummyResultSorter();
   };

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(NodeNameImpl node, Object context) throws Exception
   {
      return new DummyResultSorter();
   };

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(PropertyValueImpl node, Object context) throws Exception
   {
      return new DummyResultSorter();
   };

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(UpperCaseImpl node, Object context) throws Exception
   {
      DynamicOperandImpl operand = (DynamicOperandImpl)node.getOperand();
      Ordering ordering = new OrderingImpl(operand.getLocationFactory(), operand, ((Ordering)context).getOrder());
      return operand.accept(this, ordering);
   };

   private class SequencedResultSorter implements ResultSorter
   {
      /**
       * @param majorSorter
       * @param minorSorter
       */
      public SequencedResultSorter(ResultSorter majorSorter, ResultSorter minorSorter)
      {
         super();
         this.majorSorter = majorSorter;
         this.minorSorter = minorSorter;
      }

      private final ResultSorter majorSorter;

      private final ResultSorter minorSorter;

      /**
       * {@inheritDoc}
       */
      public int compare(ScoredRow o1, ScoredRow o2)
      {
         int result = majorSorter.compare(o1, o2);
         if (result == 0)
            result = minorSorter.compare(o1, o2);
         return result;
      }

   }
}
