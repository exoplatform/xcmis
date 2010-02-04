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
package org.xcmis.search.qom.constraint;

import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.lucene.search.visitor.QueryObjectModelVisitor;
import org.xcmis.search.qom.AbstractQueryObjectModelNode;
import org.xcmis.search.qom.operand.DynamicOperandImpl;

import java.util.HashSet;
import java.util.Set;

import javax.jcr.query.qom.Comparison;
import javax.jcr.query.qom.DynamicOperand;
import javax.jcr.query.qom.QueryObjectModelConstants;
import javax.jcr.query.qom.StaticOperand;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id$
 */
public class ComparisonImpl extends ConstraintImpl implements Comparison
{
   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(getClass().getName());

   /**
    * First operand.
    */
   private final DynamicOperand operand1;

   /**
    * Second operand.
    */
   private final StaticOperand operand2;

   /**
    * Operator.
    * <ul>
    * <li>{@link QueryObjectModelConstants#OPERATOR_EQUAL_TO},</li>
    * <li>{@link QueryObjectModelConstants#OPERATOR_NOT_EQUAL_TO},</li>
    * <li>{@link QueryObjectModelConstants#OPERATOR_LESS_THAN},</li>
    * <li>{@link QueryObjectModelConstants#OPERATOR_LESS_THAN_OR_EQUAL_TO},</li>
    * <li>{@link QueryObjectModelConstants#OPERATOR_GREATER_THAN},</li>
    * <li>{@link QueryObjectModelConstants#OPERATOR_GREATER_THAN_OR_EQUAL_TO}, or
    * </li>
    * <li>{@link QueryObjectModelConstants#OPERATOR_LIKE}</li>
    * </ul>
    */
   private final String operator;

   /**
    * @param operand1 - First operand.
    * @param operator - Second operand.
    * @param operand2 - Operator.
    */
   public ComparisonImpl(LocationFactory locationFactory, DynamicOperand operand1, String operator,
      StaticOperand operand2)
   {
      super(locationFactory);

      this.operand1 = operand1;
      this.operand2 = operand2;
      this.operator = operator;
   }

   /**
    * {@inheritDoc}
    */
   public Object accept(QueryObjectModelVisitor visitor, Object context) throws Exception
   {
      return visitor.visit(this, context);
   }

   /**
    * {@inheritDoc}
    */
   public DynamicOperand getOperand1()
   {
      return operand1;
   }

   /**
    * {@inheritDoc}
    */
   public StaticOperand getOperand2()
   {
      return operand2;
   }

   @Override
   public Set<String> getSelectorsNames()
   {
      Set<String> selectors = new HashSet<String>();
      selectors.add(((DynamicOperandImpl)operand1).getSelectorName());
      return selectors;
   }

   /**
    * {@inheritDoc}
    */
   public void reloadLocation(LocationFactory locationFactory)
   {
      super.reloadLocation(locationFactory);
      ((AbstractQueryObjectModelNode)operand1).reloadLocation(locationFactory);
      ((AbstractQueryObjectModelNode)operand2).reloadLocation(locationFactory);
   }

   /**
    * {@inheritDoc}
    */
   public String toString()
   {
      return operand1 + " " + operator + " " + operand2;
   }

   /**
    * {@inheritDoc}
    */
   public String getOperator()
   {
      return operator;
   }

}
