/*
 * Copyright (C) 2009 eXo Platform SAS.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the ied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.xcmis.search.result;

import org.apache.commons.lang.Validate;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.SearchServiceException;
import org.xcmis.search.VisitException;
import org.xcmis.search.Visitors;
import org.xcmis.search.model.operand.DynamicOperand;
import org.xcmis.search.model.operand.LowerCase;
import org.xcmis.search.model.operand.UpperCase;
import org.xcmis.search.model.ordering.Ordering;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: AbstractResultSorterFactory.java 34027 2009-07-15 23:26:43Z
 *          aheritier $
 */
public abstract class AbstractResultSorterFactory extends Visitors.AbstractModelVisitor implements ResultSorterFactory
{
   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(getClass().getName());

   protected ResultSorter sorter;

   /**
    * {@inheritDoc}
    * @throws SearchServiceException 
    */
   public ResultSorter getResultSorter(Ordering[] orderings) throws SearchServiceException
   {
      ResultSorter sorter = null;

      for (int i = 0; i < orderings.length; i++)
      {

         try
         {
            Visitors.visit(orderings[i].getOperand(), this);
         }
         catch (VisitException e)
         {
            throw new SearchServiceException(e.getLocalizedMessage(), e.getCause());
         }
      }
      if (sorter == null)
      {
         sorter = getDefaultResultSorter(new String[0]);
      }

      return sorter;
   }

   public ResultSorter getDefaultResultSorter(String[] selectorNames)
   {
      return new DummyResultSorter();
   }

   @Override
   public void visit(LowerCase node) throws VisitException
   {
      DynamicOperand operand = node.getOperand();
      Visitors.visit(operand, this);
   }

   @Override
   public void visit(UpperCase node) throws VisitException
   {
      DynamicOperand operand = node.getOperand();
      Visitors.visit(operand, this);
   }

   private static class SequencedResultSorter implements ResultSorter
   {
      private final ResultSorter majorSorter;

      private final ResultSorter minorSorter;

      /**
       * @param majorSorter
       * @param minorSorter
       */
      public SequencedResultSorter(ResultSorter majorSorter, ResultSorter minorSorter)
      {
         Validate.notNull(majorSorter, "The majorSorter argument may not be null");
         Validate.notNull(majorSorter, "The minorSorter argument may not be null");

         this.majorSorter = majorSorter;
         this.minorSorter = minorSorter;
      }

      /**
       * {@inheritDoc}
       */
      public int compare(ScoredRow o1, ScoredRow o2)
      {
         int result = majorSorter.compare(o1, o2);
         if (result == 0)
         {
            result = minorSorter.compare(o1, o2);
         }
         return result;
      }

   }
}
