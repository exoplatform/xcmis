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
import org.xcmis.search.model.source.SelectorName;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@exoplatform.org">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z ksm $
 *
 */
public class DescendantNodeTest
{
   /**
    * Test method for constructor
    * {@link DescendantNode#DescendantNode(org.xcmis.search.model.source.SelectorName, String)
    * 
    */
   @Test(expected = IllegalArgumentException.class)
   public void testShouldNotCreateWithNullSelectorNameWhenSupplyingAncestorPath()
   {

      new DescendantNode(null, "ancestorPath");

   }

   /**
    * Test method for constructor
    * {@link DescendantNode#DescendantNode(org.xcmis.search.model.source.SelectorName, String)
    * 
    */
   @Test(expected = IllegalArgumentException.class)
   public void testShouldNotCreateWithNullAncestorPathWhenSupplyingSelectorName()
   {
      SelectorName selectorName = mock(SelectorName.class);
      new DescendantNode(selectorName, null);

   }

   @Test
   public void testShouldCreateWithNonNullParameters()
   {
      SelectorName selectorName = mock(SelectorName.class);
      String ancestorPath = "AncestorPath";
      DescendantNode descendantNode = new DescendantNode(selectorName, ancestorPath);

      assertThat(descendantNode.getSelectorName(), is(sameInstance(selectorName)));
      assertThat(descendantNode.getAncestorPath(), is(sameInstance(ancestorPath)));
   }
}
