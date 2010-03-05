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

import static org.mockito.Mockito.mock;

import org.junit.Test;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@exoplatform.org">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z ksm $
 *
 */
public class AndTest
{

   /**
    * Test method for constructor
    * {@link And#And(Constraint, Constraint)
    * 
    */
   @Test(expected = IllegalArgumentException.class)
   public void testShouldNotCreateWithNullLeftConstraint()
   {
      Constraint constraint = mock(Constraint.class);
      new And(null, constraint);
   }

   /**
    * Test method for constructor
    * {@link And#And(Constraint, Constraint)
    * 
    */
   @Test(expected = IllegalArgumentException.class)
   public void testShouldNotCreateWithNullRightConstraint()
   {
      Constraint constraint = mock(Constraint.class);
      new And(constraint, null);
   }

   /**
    * Test method for constructor
    * {@link And#And(Constraint, Constraint)
    * 
    */
   @Test
   public void testShouldCreateWithNotNullConstraints()
   {
      Constraint constraint = mock(Constraint.class);
      new And(constraint, constraint);
   }
}
