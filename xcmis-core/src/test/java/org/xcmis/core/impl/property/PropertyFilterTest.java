/**
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

package org.xcmis.core.impl.property;

import junit.framework.TestCase;

import org.xcmis.core.impl.property.PropertyFilter;
import org.xcmis.spi.FilterNotValidException;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: PropertyFilterTest.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class PropertyFilterTest extends TestCase
{

   public void testAllProperties() throws Exception
   {
      String filterString = "*";
      PropertyFilter filter = new PropertyFilter(filterString);
      assertTrue(filter.accept("" + System.currentTimeMillis()));
      filterString = " *   \t";
      filter = new PropertyFilter(filterString);
      assertTrue(filter.accept("" + System.currentTimeMillis()));
   }

   public void testListProperties() throws Exception
   {
      testListProperties(new PropertyFilter("property1, property2 , \tproperty3"), "property1", "property2", "property3");
   }

   public void testListPropertiesWithWildCard()
   {
      try
      {
         new PropertyFilter("property1, property2 , \tproperty3, *");
         fail("FilterNotValidException must be thrown");
      }
      catch (FilterNotValidException e)
      {
      }
   }

   public void testListPropertiesWithSameName() throws Exception
   {
      testListProperties(new PropertyFilter("property1, property2 , \tproperty2"), "property1", "property2");
   }

   public void testListPropertiesWithEmpty()
   {
      try
      {
         new PropertyFilter("property1, ,property2");
         fail("FilterNotValidException must be thrown");
      }
      catch (FilterNotValidException e)
      {
      }
   }

   public void testIllegalCharacters1()
   {
      try
      {
         new PropertyFilter("property 1, property2");
         fail("FilterNotValidException must be thrown");
      }
      catch (FilterNotValidException e)
      {
      }
   }

   public void testIllegalCharacters2()
   {
      try
      {
         new PropertyFilter("property'1, property2");
         fail("FilterNotValidException must be thrown");
      }
      catch (FilterNotValidException e)
      {
      }
   }

   private static void testListProperties(PropertyFilter filter, String... tokens) throws Exception
   {
      for (String token : tokens)
         assertTrue(filter.accept(token));
   }

}
