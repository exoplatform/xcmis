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

import org.xcmis.search.qom.column.ColumnImpl;
import org.xcmis.search.qom.constraint.AndImpl;
import org.xcmis.search.qom.constraint.ChildNodeImpl;
import org.xcmis.search.qom.constraint.ComparisonImpl;
import org.xcmis.search.qom.constraint.DescendantNodeImpl;
import org.xcmis.search.qom.constraint.FullTextSearchImpl;
import org.xcmis.search.qom.constraint.InFolderNode;
import org.xcmis.search.qom.constraint.InTreeNode;
import org.xcmis.search.qom.constraint.NotImpl;
import org.xcmis.search.qom.constraint.OrImpl;
import org.xcmis.search.qom.constraint.PropertyExistenceImpl;
import org.xcmis.search.qom.constraint.SameNodeImpl;
import org.xcmis.search.qom.operand.BindVariableValueImpl;
import org.xcmis.search.qom.operand.DynamicOperandImpl;
import org.xcmis.search.qom.operand.FullTextSearchScoreImpl;
import org.xcmis.search.qom.operand.LengthImpl;
import org.xcmis.search.qom.operand.LiteralImpl;
import org.xcmis.search.qom.operand.LowerCaseImpl;
import org.xcmis.search.qom.operand.NodeLocalNameImpl;
import org.xcmis.search.qom.operand.NodeNameImpl;
import org.xcmis.search.qom.operand.PropertyValueImpl;
import org.xcmis.search.qom.operand.UpperCaseImpl;
import org.xcmis.search.qom.ordering.OrderingImpl;
import org.xcmis.search.qom.source.JoinImpl;
import org.xcmis.search.qom.source.SelectorImpl;
import org.xcmis.search.qom.source.SourceImpl;
import org.xcmis.search.qom.source.join.ChildNodeJoinConditionImpl;
import org.xcmis.search.qom.source.join.DescendantNodeJoinConditionImpl;
import org.xcmis.search.qom.source.join.EquiJoinConditionImpl;
import org.xcmis.search.qom.source.join.SameNodeJoinConditionImpl;

import javax.jcr.query.qom.QueryObjectModel;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id$
 */
public interface QueryObjectModelVisitor
{

   Object visit(AndImpl node, Object context) throws Exception;

   Object visit(BindVariableValueImpl node, Object context) throws Exception;

   Object visit(ChildNodeImpl node, Object context) throws Exception;

   Object visit(ChildNodeJoinConditionImpl node, Object context) throws Exception;

   Object visit(ColumnImpl node, Object context) throws Exception;

   Object visit(ComparisonImpl node, Object context) throws Exception;

   Object visit(DynamicOperandImpl node, Object context) throws Exception;

   Object visit(DescendantNodeImpl node, Object context) throws Exception;

   Object visit(DescendantNodeJoinConditionImpl node, Object context) throws Exception;

   Object visit(EquiJoinConditionImpl node, Object context) throws Exception;

   Object visit(FullTextSearchImpl node, Object context) throws Exception;

   Object visit(FullTextSearchScoreImpl node, Object context) throws Exception;

   Object visit(JoinImpl node, Object context) throws Exception;

   Object visit(LengthImpl node, Object context) throws Exception;

   Object visit(LiteralImpl node, Object context) throws Exception;

   Object visit(LowerCaseImpl node, Object context) throws Exception;

   Object visit(NodeLocalNameImpl node, Object context) throws Exception;

   Object visit(NodeNameImpl node, Object context) throws Exception;

   Object visit(NotImpl node, Object context) throws Exception;

   Object visit(OrderingImpl node, Object context) throws Exception;

   Object visit(OrImpl node, Object context) throws Exception;

   Object visit(PropertyExistenceImpl node, Object context) throws Exception;

   Object visit(PropertyValueImpl node, Object context) throws Exception;

   Object visit(QueryObjectModel node, Object context) throws Exception;

   Object visit(SameNodeImpl node, Object context) throws Exception;

   Object visit(SameNodeJoinConditionImpl node, Object context) throws Exception;

   Object visit(SelectorImpl node, Object context) throws Exception;

   Object visit(SourceImpl node, Object context) throws Exception;

   Object visit(UpperCaseImpl node, Object context) throws Exception;

   Object visit(InFolderNode node, Object context) throws Exception;

   Object visit(InTreeNode node, Object context) throws Exception;
}
