/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.xcmis.search.model.column;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.xcmis.search.util.Assert.assertAllEqual;

import org.junit.Test;
import org.xcmis.search.model.source.SelectorName;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z
 *          aheritier $
 * 
 */
public class ColumnTest
{

   /**
    * Test method for constructor
    * {@link org.xcmis.search.model.column.Column#Column(org.xcmis.search.model.source.SelectorName)}
    */
   @Test(expected = IllegalArgumentException.class)
   public void testShouldNotCreateWithNullSelectorName()
   {
      new Column(null);
   }

   /**
    * Test method for
    * {@link org.xcmis.search.model.column.Column#Column(org.xcmis.search.model.source.SelectorName, String, String)}
    */
   @Test(expected = IllegalArgumentException.class)
   public void testShouldNotCreateWithNullSourceWhenSupplyingOtherParameters()
   {
      new Column(null, "name", "name");
   }

   /**
    * Test method for
    * {@link org.xcmis.search.model.column.Column#Column(org.xcmis.search.model.source.SelectorName, String, String)}
    * 
    */
   @Test(expected = IllegalArgumentException.class)
   public void testShouldNotCreateWithNullPropertyNameWhenSupplyingOtherParameters() throws Exception
   {
      SelectorName selectorName = mock(SelectorName.class);
      new Column(selectorName, null, "name");
   }

   /**
    * Test method for
    * {@link org.xcmis.search.model.column.Column#Column(org.xcmis.search.model.source.SelectorName, String, String)}
    * 
    */
   @Test(expected = IllegalArgumentException.class)
   public void testShouldNotCreateWithNullColumnNameWhenSupplyingOtherParameters() throws Exception
   {
      SelectorName selectorName = mock(SelectorName.class);
      new Column(selectorName, "name", null);
   }

   @Test
   public void testShouldCreateWithNonNullParameters() throws Exception
   {
      SelectorName selectorName = mock(SelectorName.class);
      String propertyName = "name";
      String columntName = "columnName";
      Column column = new Column(selectorName, propertyName, columntName);
      assertThat(column.getSelectorName(), is(sameInstance(selectorName)));
      assertThat(column.getPropertyName(), is(sameInstance(propertyName)));
      assertThat(column.getColumnName(), is(sameInstance(columntName)));
   }

   /**
    * Test transitive equals
    * @throws Exception
    */
   @Test
   public void testShouldBeEqualsSameColumn() throws Exception
   {
      SelectorName selectorName = mock(SelectorName.class);
      assertAllEqual(new Column[]{
         new Column(selectorName, "name", "columnName"),
         new Column(selectorName, "name", "columnName"),
         new Column(selectorName, "name", "columnName")});
   }

   @Test
   public void testShouldConstructReadebleStringFromSelectAllColumn()
   {
      Column column = new Column(new SelectorName("selectorName"));
      assertThat(column.toString(), is("selectorName.*"));
   }

   @Test
   public void testShouldConstructReadebleStringFromSpecificColumn()
   {
      Column column = new Column(new SelectorName("selectorName"), "myColumn", "resultColumn");
      assertThat(column.toString(), is("selectorName.myColumn AS resultColumn"));
   }
}
