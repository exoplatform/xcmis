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

import javax.jcr.query.qom.QueryObjectModel;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34027 2009-07-15 23:26:43Z
 *          aheritier $
 */
public abstract class DocumentMatcherFactory implements QueryObjectModelVisitor
{

   /**
    * {@inheritDoc}
    */
   public Object visit(AndImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(BindVariableValueImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(ChildNodeImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(ColumnImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(ComparisonImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(DescendantNodeImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(DynamicOperandImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(FullTextSearchImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(FullTextSearchScoreImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(JoinImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(LengthImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(LiteralImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(LowerCaseImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(NodeLocalNameImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(NodeNameImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(NotImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(OrderingImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(OrImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(PropertyExistenceImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(PropertyValueImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(QueryObjectModel node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(SameNodeImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(SelectorImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(SourceImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(UpperCaseImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(InFolderNode node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Object visit(InTreeNode node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }
}
