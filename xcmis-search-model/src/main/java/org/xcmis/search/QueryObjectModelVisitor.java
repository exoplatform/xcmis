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
package org.xcmis.search;

import org.xcmis.search.model.Limit;
import org.xcmis.search.model.Query;
import org.xcmis.search.model.column.Column;
import org.xcmis.search.model.constraint.And;
import org.xcmis.search.model.constraint.ChildNode;
import org.xcmis.search.model.constraint.Comparison;
import org.xcmis.search.model.constraint.DescendantNode;
import org.xcmis.search.model.constraint.FullTextSearch;
import org.xcmis.search.model.constraint.Not;
import org.xcmis.search.model.constraint.Or;
import org.xcmis.search.model.constraint.PropertyExistence;
import org.xcmis.search.model.constraint.SameNode;
import org.xcmis.search.model.operand.BindVariableName;
import org.xcmis.search.model.operand.FullTextSearchScore;
import org.xcmis.search.model.operand.Length;
import org.xcmis.search.model.operand.Literal;
import org.xcmis.search.model.operand.LowerCase;
import org.xcmis.search.model.operand.NodeDepth;
import org.xcmis.search.model.operand.NodeLocalName;
import org.xcmis.search.model.operand.NodeName;
import org.xcmis.search.model.operand.PropertyValue;
import org.xcmis.search.model.operand.UpperCase;
import org.xcmis.search.model.ordering.Ordering;
import org.xcmis.search.model.source.Join;
import org.xcmis.search.model.source.Selector;
import org.xcmis.search.model.source.join.ChildNodeJoinCondition;
import org.xcmis.search.model.source.join.DescendantNodeJoinCondition;
import org.xcmis.search.model.source.join.EquiJoinCondition;
import org.xcmis.search.model.source.join.SameNodeJoinCondition;

/**
 * Interface to make use of the Visitor pattern programming style. A class that
 * ements this interface can traverse the contents of a query void model
 * elements just by calling the `accept' method which all classes have.
 * 
 */
public interface QueryObjectModelVisitor
{

   void visit(And node) throws VisitException;

   void visit(BindVariableName node) throws VisitException;

   void visit(ChildNode node) throws VisitException;

   void visit(ChildNodeJoinCondition node) throws VisitException;

   void visit(Column node) throws VisitException;

   void visit(Comparison node) throws VisitException;

   void visit(DescendantNode node) throws VisitException;

   void visit(DescendantNodeJoinCondition node) throws VisitException;

   void visit(EquiJoinCondition node) throws VisitException;

   void visit(FullTextSearch node) throws VisitException;

   void visit(FullTextSearchScore node) throws VisitException;

   void visit(Join node) throws VisitException;

   void visit(Length node) throws VisitException;

   void visit(Limit limit) throws VisitException;

   void visit(Literal node) throws VisitException;

   void visit(LowerCase node) throws VisitException;

   void visit(NodeDepth depth) throws VisitException;

   void visit(NodeLocalName node) throws VisitException;

   void visit(NodeName node) throws VisitException;

   void visit(Not node) throws VisitException;

   void visit(Or node) throws VisitException;

   void visit(Ordering node) throws VisitException;

   void visit(PropertyExistence node) throws VisitException;

   void visit(PropertyValue node) throws VisitException;

   void visit(Query node) throws VisitException;

   void visit(SameNode node) throws VisitException;

   void visit(SameNodeJoinCondition node) throws VisitException;

   void visit(Selector selector) throws VisitException;

   void visit(UpperCase node) throws VisitException;

}
