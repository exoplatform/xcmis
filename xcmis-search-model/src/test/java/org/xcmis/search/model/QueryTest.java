/*
 * ModeShape (http://www.modeshape.org) See the COPYRIGHT.txt file distributed
 * with this work for information regarding copyright ownership. Some portions
 * may be licensed to Red Hat, Inc. under one or more contributor license
 * agreements. See the AUTHORS.txt file in the distribution for a full listing
 * of individual contributors.
 * 
 * ModeShape is free software. Unless otherwise indicated, all code in ModeShape
 * is licensed to you under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * ModeShape is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.xcmis.search.model;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.xcmis.search.Visitors;
import org.xcmis.search.model.column.Column;
import org.xcmis.search.model.constraint.Constraint;
import org.xcmis.search.model.constraint.PropertyExistence;
import org.xcmis.search.model.operand.NodeName;
import org.xcmis.search.model.ordering.Order;
import org.xcmis.search.model.ordering.Ordering;
import org.xcmis.search.model.source.Selector;
import org.xcmis.search.model.source.SelectorName;
import org.xcmis.search.model.source.Source;

import java.util.Collections;
import java.util.List;

/**
 * 
 */
public class QueryTest
{

   private Query query;

   private Source source;

   private Constraint constraint;

   private List<Ordering> orderings;

   private List<Column> columns;

   private Limit limits;

   private boolean distinct;

   @Test(expected = IllegalArgumentException.class)
   public void testShouldNotCreateWithNullSource()
   {
      new Query(null);
   }

   @Test(expected = IllegalArgumentException.class)
   public void testShouldNotCreateWithNullSourceWhenSupplyingOtherParameters()
   {
      source = null;
      constraint = mock(Constraint.class);
      orderings = Collections.emptyList();
      columns = Collections.emptyList();
      limits = null;
      new Query(source, constraint, orderings, columns, limits);
   }

   @Test
   public void testShouldAllowNullConstraint()
   {
      source = mock(Source.class);
      constraint = null;
      orderings = Collections.emptyList();
      columns = Collections.emptyList();
      limits = null;
      query = new Query(source, constraint, orderings, columns, limits);
      assertThat(query.getSource(), is(source));
      assertNull(query.getConstraint());
      assertEquals(query.getOrderings(), orderings);
      assertEquals(query.getColumns(), columns);
   }

   @Test
   public void testShouldAllowNullOrderingsList()
   {
      source = mock(Source.class);
      constraint = mock(Constraint.class);
      orderings = null;
      columns = Collections.emptyList();
      limits = null;
      query = new Query(source, constraint, orderings, columns, limits);
      assertThat(query.getSource(), is(sameInstance(source)));
      assertThat(query.getConstraint(), is(sameInstance(constraint)));
      assertThat(query.getOrderings().isEmpty(), is(true));
      assertThat(query.getColumns(), is(sameInstance(columns)));
   }

   @Test
   public void testShouldAllowNullColumnsList()
   {
      source = mock(Source.class);
      constraint = mock(Constraint.class);
      orderings = Collections.emptyList();
      columns = null;
      limits = null;
      query = new Query(source, constraint, orderings, columns, limits);
      assertThat(query.getSource(), is(sameInstance(source)));
      assertThat(query.getConstraint(), is(sameInstance(constraint)));
      assertThat(query.getOrderings(), is(sameInstance(orderings)));
      assertThat(query.getColumns().isEmpty(), is(true));
   }

   @Test
   public void testShouldCreateWithNonNullParameters()
   {
      source = mock(Source.class);
      constraint = mock(Constraint.class);
      orderings = Collections.emptyList();
      columns = Collections.emptyList();
      limits = null;
      query = new Query(source, constraint, orderings, columns, limits);
      assertThat(query.getSource(), is(sameInstance(source)));
      assertThat(query.getConstraint(), is(sameInstance(constraint)));
      assertThat(query.getOrderings(), is(sameInstance(orderings)));
      assertThat(query.getColumns(), is(sameInstance(columns)));
   }

   @Test
   public void testShouldConstructReadableString()
   {
      source = new Selector(selector("nt:unstructured"));
      columns = Collections.singletonList(new Column(selector("selector1")));
      constraint = new PropertyExistence(selector("selector1"), "jcr:uuid");
      orderings = Collections.singletonList(new Ordering(new NodeName(selector("selector1")), Order.ASCENDING));
      query = new Query(source, constraint, orderings, columns, limits);
      assertThat(Visitors.readable(query),
      is("SELECT selector1.* FROM nt:unstructured WHERE selector1.jcr:uuid IS NOT NULL ORDER BY NAME(selector1) ASC"));
   }

   @Test
   public void testShouldConstructReadableStringWithLimits()
   {
      source = new Selector(selector("nt:unstructured"));
      columns = Collections.singletonList(new Column(selector("selector1")));
      constraint = new PropertyExistence(selector("selector1"), "jcr:uuid");
      orderings = Collections.singletonList(new Ordering(new
      NodeName(selector("selector1")), Order.ASCENDING));
      limits = new Limit(10, 100);
      query = new Query(source, constraint, orderings, columns, limits);
      assertThat(
         Visitors.readable(query),
         is("SELECT selector1.* FROM nt:unstructured WHERE selector1.jcr:uuid IS NOT NULL ORDER BY NAME(selector1) ASC LIMIT 10 OFFSET 100"));
   }

   @Test
   public void testShouldConstructReadableStringWithNoColumns()
   {
      source = new Selector(selector("nt:unstructured"));
      columns = Collections.emptyList();
      constraint = new PropertyExistence(selector("selector1"), "jcr:uuid");
      orderings = Collections.singletonList(new Ordering(new
      NodeName(selector("selector1")), Order.ASCENDING));
      query = new Query(source, constraint, orderings, columns, limits);
      assertThat(Visitors.readable(query),
      is("SELECT * FROM nt:unstructured WHERE selector1.jcr:uuid IS NOT NULL ORDER BY NAME(selector1) ASC"));
   }

   @Test
   public void testShouldConstructReadableStringWithNoOrderings()
   {
      source = new Selector(selector("nt:unstructured"));
      columns = Collections.singletonList(new Column(selector("selector1")));
      constraint = new PropertyExistence(selector("selector1"), "jcr:uuid");
      orderings = Collections.emptyList();
      query = new Query(source, constraint, orderings, columns, limits);
      assertThat(Visitors.readable(query),
      is("SELECT selector1.* FROM nt:unstructured WHERE selector1.jcr:uuid IS NOT NULL"));
   }

   @Test
   public void testShouldConstructReadableStringWithNoConstraint()
   {
      source = new Selector(selector("nt:unstructured"));
      columns = Collections.singletonList(new Column(selector("selector1")));
      constraint = null;
      orderings = Collections.singletonList(new Ordering(new
      NodeName(selector("selector1")), Order.ASCENDING));
      query = new Query(source, constraint, orderings, columns, limits);
      assertThat(Visitors.readable(query),
      is("SELECT selector1.* FROM nt:unstructured ORDER BY NAME(selector1) ASC"));
   }

   @Test
   public void testShouldConstructReadableStringWithNoConstraintOrColumnsOrOrderings()
   {
      source = new Selector(selector("nt:unstructured"));
      columns = Collections.emptyList();
      constraint = null;
      orderings = Collections.emptyList();
      query = new Query(source, constraint, orderings, columns, limits);
      assertThat(Visitors.readable(query), is("SELECT * FROM nt:unstructured"));

   }

   protected SelectorName selector(String name)
   {
      return new SelectorName(name);
   }
}
