/*
 * Copyright (C) 2010 eXo Platform SAS.
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

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.xcmis.search.model.operand.DynamicOperand;
import org.xcmis.search.model.operand.StaticOperand;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@exoplatform.org">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z ksm $
 *
 */
public class ComparisonTest
{
   /**
    * Test method for constructor
    * {@link Comparison#Comparison(org.xcmis.search.model.operand.DynamicOperand, Operator, org.xcmis.search.model.operand.StaticOperand)
    * 
    */
   @Test(expected = IllegalArgumentException.class)
   public void testShouldNotCreateWithNulDynamicOperandWhenSupplyingOtherParameters()
   {

      StaticOperand staticOperand = mock(StaticOperand.class);
      new Comparison(null, Operator.LIKE, staticOperand);

   }

   /**
    * Test method for constructor
    * {@link Comparison#Comparison(org.xcmis.search.model.operand.DynamicOperand, Operator, org.xcmis.search.model.operand.StaticOperand)
    * 
    */
   @Test(expected = IllegalArgumentException.class)
   public void testShouldNotCreateWithNulStaticOperandWhenSupplyingOtherParameters()
   {

      DynamicOperand dynamicOperand = mock(DynamicOperand.class);
      new Comparison(dynamicOperand, Operator.LIKE, null);
   }

   /**
    * Test method for constructor
    * {@link Comparison#Comparison(org.xcmis.search.model.operand.DynamicOperand, Operator, org.xcmis.search.model.operand.StaticOperand)
    * 
    */
   @Test(expected = IllegalArgumentException.class)
   public void testShouldNotCreateWithNulOperatorWhenSupplyingOtherParameters()
   {

      DynamicOperand dynamicOperand = mock(DynamicOperand.class);
      StaticOperand staticOperand = mock(StaticOperand.class);
      new Comparison(dynamicOperand, null, staticOperand);
   }

   @Test
   public void testShouldCreateWithNonNullParameters()
   {
      DynamicOperand dynamicOperand = mock(DynamicOperand.class);
      StaticOperand staticOperand = mock(StaticOperand.class);
      Operator operator = Operator.LIKE;
      Comparison comparison = new Comparison(dynamicOperand, operator, staticOperand);

      assertThat(comparison.getOperand1(), is(sameInstance(dynamicOperand)));
      assertThat(comparison.getOperand2(), is(sameInstance(staticOperand)));
      assertThat(comparison.getOperator(), is(sameInstance(operator)));

   }
}
