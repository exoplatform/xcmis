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
package org.xcmis.search.lucene.search.visitor;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.qom.column.ColumnImpl;
import org.xcmis.search.qom.constraint.AndImpl;
import org.xcmis.search.qom.constraint.ComparisonImpl;
import org.xcmis.search.qom.constraint.ConstraintImpl;
import org.xcmis.search.qom.constraint.NotImpl;
import org.xcmis.search.qom.constraint.OrImpl;
import org.xcmis.search.qom.operand.DynamicOperandImpl;
import org.xcmis.search.qom.operand.LengthImpl;
import org.xcmis.search.qom.operand.LowerCaseImpl;
import org.xcmis.search.qom.operand.PropertyValueImpl;
import org.xcmis.search.qom.operand.StaticOperandImpl;
import org.xcmis.search.qom.operand.UpperCaseImpl;
import org.xcmis.search.qom.ordering.OrderingImpl;
import org.xcmis.search.qom.source.JoinImpl;
import org.xcmis.search.qom.source.SelectorImpl;
import org.xcmis.search.qom.source.SourceImpl;
import org.xcmis.search.qom.source.join.JoinConditionImpl;

import javax.jcr.query.qom.Column;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.QueryObjectModel;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id$
 */
public class QueryObjectModelTraversingVisitor extends QueryObjectModelVisitorImpl
{
   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(getClass().getName());

   /**
    * Calls accept on each of the attached constraints of the AND node.
    */
   public Object visit(AndImpl node, Object context) throws Exception
   {
      Object newContext = ((ConstraintImpl)node.getConstraint1()).accept(this, context);
      newContext = ((ConstraintImpl)node.getConstraint2()).accept(this, newContext);
      return newContext;
   }

   /**
    * Calls accept on the two operands in the comparison node.
    */
   public Object visit(ComparisonImpl node, Object context) throws Exception
   {
      Object newContext = ((DynamicOperandImpl)node.getOperand1()).accept(this, context);
      newContext = ((StaticOperandImpl)node.getOperand2()).accept(this, newContext);
      return context;
   }

   /**
    * Calls accept on the two sources and the join condition in the join node.
    */
   public Object visit(JoinImpl node, Object context) throws Exception
   {
      Object newContext = ((SourceImpl)node.getRight()).accept(this, context);
      newContext = ((SourceImpl)node.getLeft()).accept(this, newContext);
      newContext = ((JoinConditionImpl)node.getJoinCondition()).accept(this, newContext);
      return context;
   }

   /**
    * Calls accept on the property value in the length node.
    */
   public Object visit(LengthImpl node, Object context) throws Exception
   {
      Object newContext = ((PropertyValueImpl)node.getPropertyValue()).accept(this, context);
      return newContext;
   }

   /**
    * Calls accept on the dynamic operand in the lower-case node.
    */
   public Object visit(LowerCaseImpl node, Object context) throws Exception
   {
      Object newContext = ((DynamicOperandImpl)node.getOperand()).accept(this, context);
      return newContext;
   }

   /**
    * Calls accept on the constraint in the NOT node.
    */
   public Object visit(NotImpl node, Object context) throws Exception
   {
      Object newContext = ((ConstraintImpl)node.getConstraint()).accept(this, context);
      return newContext;
   }

   /**
    * Calls accept on the dynamic operand in the ordering node.
    */
   public Object visit(OrderingImpl node, Object context) throws Exception
   {
      Object newContext = ((DynamicOperandImpl)node.getOperand()).accept(this, context);
      return newContext;
   }

   /**
    * Calls accept on each of the attached constraints of the OR node.
    */
   public Object visit(OrImpl node, Object context) throws Exception
   {
      Object newContext = ((ConstraintImpl)node.getConstraint1()).accept(this, context);
      newContext = ((ConstraintImpl)node.getConstraint2()).accept(this, newContext);
      return newContext;
   }

   /**
    * Calls accept on the following contained QOM nodes:
    * <ul>
    * <li>Source</li>
    * <li>Constraints</li>
    * <li>Orderings</li>
    * <li>Columns</li>
    * </ul>
    */
   public Object visit(QueryObjectModel node, Object context) throws Exception
   {
      Object newContext = ((SourceImpl)node.getSource()).accept(this, context);
      ConstraintImpl constraint = (ConstraintImpl)node.getConstraint();
      if (constraint != null)
      {
         newContext = constraint.accept(this, newContext);
      }
      Ordering[] orderings = node.getOrderings();
      for (int i = 0; i < orderings.length; i++)
      {
         newContext = ((OrderingImpl)orderings[i]).accept(this, newContext);
      }
      Column[] columns = node.getColumns();
      for (int i = 0; i < columns.length; i++)
      {
         newContext = ((ColumnImpl)columns[i]).accept(this, newContext);
      }
      return newContext;
   }

   @Override
   public Object visit(SelectorImpl node, Object context) throws Exception
   {
      return super.visit(node, context);
   }

   @Override
   public Object visit(SourceImpl node, Object context) throws Exception
   {
      return super.visit(node, context);
   }

   /**
    * Calls accept on the dynamic operand in the lower-case node.
    */
   public Object visit(UpperCaseImpl node, Object context) throws Exception
   {
      Object newContext = ((DynamicOperandImpl)node.getOperand()).accept(this, context);
      return newContext;
   }

}
