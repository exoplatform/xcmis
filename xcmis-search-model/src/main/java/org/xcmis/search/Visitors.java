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
package org.xcmis.search;

import org.xcmis.search.model.Limit;
import org.xcmis.search.model.Query;
import org.xcmis.search.model.QueryElement;
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
import org.xcmis.search.model.source.SelectorName;
import org.xcmis.search.model.source.join.ChildNodeJoinCondition;
import org.xcmis.search.model.source.join.DescendantNodeJoinCondition;
import org.xcmis.search.model.source.join.EquiJoinCondition;
import org.xcmis.search.model.source.join.SameNodeJoinCondition;

import java.util.LinkedList;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@exoplatform.org">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z ksm $
 *
 */
public class Visitors
{
   /**
    * Visit the supplied {@link QueryElement object} using the supplied {@link QueryObjectModelVisitor}, which must be responsible for navigation as
    * well as any business logic.
    * 
    * @param <GeneralVisitor> the type of visitor
    * @param visitable the top-level object to be visited
    * @param visitor the visitor that is to be used
    * @return the visitor, allowing the caller to easily invoke operations on the visitor after visitation has completed
    * @throws VisitException 
    */
   public static <GeneralVisitor extends QueryObjectModelVisitor> GeneralVisitor visit(QueryElement visitable,
                                                                        GeneralVisitor visitor) throws VisitException
   {
      if (visitable != null)
      {
         visitable.accept(visitor);
      }
      return visitor;
   }

   /**
    * Using a visitor, obtain the readable string representation of the supplied {@link Visitable object}
    * 
    * @param visitable the visitable
    * @return the string representation
    */
   public static String readable(QueryElement visitable)
   {
      try
      {
         return visit(visitable, new ReadableVisitor()).getString();
      }
      catch (VisitException e)
      {
         return "";
      }
   }

   /**
    * A common base class for all visitors, which provides no-op implementations for all {@code visit(...)} methods. Visitor
    * implementations can subclass and implement only those methods that they need to implement.
    * <p>
    * This is often an excellent base class for <i> visitors</i>, which simply are {@link QueryObjectModelVisitor} implementations that
    * are responsible only for visiting the supplied object but that never call {@link QueryElement#accept(QueryObjectModelVisitor)}. 
    * </p>
    */
   public static class AbstractModelVisitor implements QueryObjectModelVisitor
   {

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.constraint.And)
       */
      public void visit(And node) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.operand.BindVariableName)
       */
      public void visit(BindVariableName node) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.constraint.ChildNode)
       */
      public void visit(ChildNode node) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.source.join.ChildNodeJoinCondition)
       */
      public void visit(ChildNodeJoinCondition node) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.column.Column)
       */
      public void visit(Column node) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.constraint.Comparison)
       */
      public void visit(Comparison node) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.constraint.DescendantNode)
       */
      public void visit(DescendantNode node) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.source.join.DescendantNodeJoinCondition)
       */
      public void visit(DescendantNodeJoinCondition node) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.source.join.EquiJoinCondition)
       */
      public void visit(EquiJoinCondition node) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.constraint.FullTextSearch)
       */
      public void visit(FullTextSearch node) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.operand.FullTextSearchScore)
       */
      public void visit(FullTextSearchScore node) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.source.Join)
       */
      public void visit(Join node) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.operand.Length)
       */
      public void visit(Length node) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.Limit)
       */
      public void visit(Limit limit) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.operand.Literal)
       */
      public void visit(Literal node) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.operand.LowerCase)
       */
      public void visit(LowerCase node) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.operand.NodeDepth)
       */
      public void visit(NodeDepth depth) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.operand.NodeLocalName)
       */
      public void visit(NodeLocalName node) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.operand.NodeName)
       */
      public void visit(NodeName node) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.constraint.Not)
       */
      public void visit(Not node) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.constraint.Or)
       */
      public void visit(Or node) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.ordering.Ordering)
       */
      public void visit(Ordering node) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.constraint.PropertyExistence)
       */
      public void visit(PropertyExistence node) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.operand.PropertyValue)
       */
      public void visit(PropertyValue node) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.Query)
       */
      public void visit(Query node) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.constraint.SameNode)
       */
      public void visit(SameNode node) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.source.join.SameNodeJoinCondition)
       */
      public void visit(SameNodeJoinCondition node) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.operand.UpperCase)
       */
      public void visit(UpperCase node) throws VisitException
      {

      }

      /**
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.source.Selector)
       */
      public void visit(Selector selector)
      {

      }

   }

   /**
    * An abstract visitor implementation that performs navigation of the query object.
    * <p>
    * Subclasses should always implement the {@code visit(T object)} methods by performing the following actions:
    * <ol>
    * <li>Call <code>strategy.visit(object);</code></li>
    * <li>Add any children of {@code object} that are to be visited using {@link #enqueue(QueryElement)}</li>
    * <li>Call {@link #visitNext()}</code></li>
    * </ol>
    * </p>
    */
   public static abstract class NavigationVisitor implements QueryObjectModelVisitor
   {
      protected final QueryObjectModelVisitor strategy;

      private final LinkedList<? super QueryElement> itemQueue = new LinkedList<QueryElement>();

      /**
       * Create a visitor that walks all query objects.
       * 
       * @param strategy the visitor that should be called at every node.
       */
      protected NavigationVisitor(QueryObjectModelVisitor strategy)
      {
         assert strategy != null;
         this.strategy = strategy;
      }

      protected final void enqueue(Iterable<? extends QueryElement> objectsToBeVisited)
      {
         for (QueryElement objectToBeVisited : objectsToBeVisited)
         {
            if (objectToBeVisited != null)
            {
               itemQueue.add(objectToBeVisited);
            }
         }
      }

      protected final void enqueue(QueryElement objectToBeVisited)
      {
         if (objectToBeVisited != null)
         {
            itemQueue.add(objectToBeVisited);
         }
      }

      protected final void visitNext() throws VisitException
      {
         if (!itemQueue.isEmpty())
         {
            QueryElement first = (QueryElement)itemQueue.removeFirst();
            assert first != null;
            first.accept(this);
         }
      }
   }

   /**
    * A visitor implementation that walks the entire query object tree and delegates to another supplied visitor to do the actual
    * work.
    */
   public static class WalkAllVisitor extends NavigationVisitor
   {

      /**
       * Create a visitor that walks all query objects.
       * 
       * @param strategy the visitor that should be called at every node.
       */
      protected WalkAllVisitor(QueryObjectModelVisitor strategy)
      {
         super(strategy);
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(And)
       */
      public void visit(And and) throws VisitException
      {
         strategy.visit(and);
         enqueue(and.getLeft());
         enqueue(and.getRight());
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(BindVariableName)
       */
      public void visit(BindVariableName variableName) throws VisitException
      {
         strategy.visit(variableName);
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(ChildNode)
       */
      public void visit(ChildNode child) throws VisitException
      {
         strategy.visit(child);
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(ChildNodeJoinCondition)
       */
      public void visit(ChildNodeJoinCondition joinCondition) throws VisitException
      {
         strategy.visit(joinCondition);
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(Column)
       */
      public void visit(Column column) throws VisitException
      {
         strategy.visit(column);
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(Comparison)
       */
      public void visit(Comparison comparison) throws VisitException
      {
         strategy.visit(comparison);
         enqueue(comparison.getOperand1());
         enqueue(comparison.getOperand2());
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(DescendantNode)
       */
      public void visit(DescendantNode descendant) throws VisitException
      {
         strategy.visit(descendant);
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(DescendantNodeJoinCondition)
       */
      public void visit(DescendantNodeJoinCondition condition) throws VisitException
      {
         strategy.visit(condition);
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(EquiJoinCondition)
       */
      public void visit(EquiJoinCondition condition) throws VisitException
      {
         strategy.visit(condition);
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(FullTextSearch)
       */
      public void visit(FullTextSearch fullTextSearch) throws VisitException
      {
         strategy.visit(fullTextSearch);
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(FullTextSearchScore)
       */
      public void visit(FullTextSearchScore score) throws VisitException
      {
         strategy.visit(score);
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(Join)
       */
      public void visit(Join join) throws VisitException
      {
         strategy.visit(join);
         enqueue(join.getLeft());
         enqueue(join.getJoinCondition());
         enqueue(join.getRight());
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(Length)
       */
      public void visit(Length length) throws VisitException
      {
         strategy.visit(length);
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(Limit)
       */
      public void visit(Limit limit) throws VisitException
      {
         strategy.visit(limit);
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(Literal)
       */
      public void visit(Literal literal) throws VisitException
      {
         strategy.visit(literal);
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(LowerCase)
       */
      public void visit(LowerCase lowerCase) throws VisitException
      {
         strategy.visit(lowerCase);
         enqueue(lowerCase.getOperand());
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(NodeDepth)
       */
      public void visit(NodeDepth depth) throws VisitException
      {
         strategy.visit(depth);
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(NodeLocalName)
       */
      public void visit(NodeLocalName nodeLocalName) throws VisitException
      {
         strategy.visit(nodeLocalName);
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(NodeName)
       */
      public void visit(NodeName nodeName) throws VisitException
      {
         strategy.visit(nodeName);
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see {@link QueryObjectModelVisitor#visit(Not)}Not)
       */
      public void visit(Not not) throws VisitException
      {
         strategy.visit(not);
         enqueue(not.getConstraint());
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(Or)
       */
      public void visit(Or or) throws VisitException
      {
         strategy.visit(or);
         enqueue(or.getLeft());
         enqueue(or.getRight());
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(Ordering)
       */
      public void visit(Ordering ordering) throws VisitException
      {
         strategy.visit(ordering);
         enqueue(ordering.getOperand());
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(PropertyExistence)
       */
      public void visit(PropertyExistence existence) throws VisitException
      {
         strategy.visit(existence);
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(PropertyValue)
       */
      public void visit(PropertyValue propertyValue) throws VisitException
      {
         strategy.visit(propertyValue);
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(Query)
       */
      public void visit(Query query) throws VisitException
      {
         strategy.visit(query);
         enqueue(query.getSource());
         enqueue(query.getColumns());
         enqueue(query.getConstraint());
         enqueue(query.getOrderings());
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(SameNode)
       */
      public void visit(SameNode sameNode) throws VisitException
      {
         strategy.visit(sameNode);
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(SameNodeJoinCondition)
       */
      public void visit(SameNodeJoinCondition condition) throws VisitException
      {
         strategy.visit(condition);
         visitNext();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(UpperCase)
       */
      public void visit(UpperCase upperCase) throws VisitException
      {
         strategy.visit(upperCase);
         enqueue(upperCase.getOperand());
         visitNext();
      }

      /**
       * @throws VisitException 
       * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.source.Selector)
       */
      public void visit(Selector selector) throws VisitException
      {
         strategy.visit(selector);
         visitNext();
      }
   }

   public static class ReadableVisitor implements QueryObjectModelVisitor
   {
      private final StringBuilder sb = new StringBuilder();

      public ReadableVisitor()
      {
      }

      protected final ReadableVisitor append(String string)
      {
         sb.append(string);
         return this;
      }

      protected final ReadableVisitor append(char character)
      {
         sb.append(character);
         return this;
      }

      protected final ReadableVisitor append(int value)
      {
         sb.append(value);
         return this;
      }

      protected final ReadableVisitor append(SelectorName name)
      {
         sb.append(name.getName());
         return this;
      }

      /**
       * Get the string representation of the visited objects.
       * 
       * @return the string representation
       */
      public final String getString()
      {
         return sb.toString();
      }

      /**
       * {@inheritDoc}
       * 
       * @see java.lang.Object#toString()
       */
      @Override
      public String toString()
      {
         return sb.toString();
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(And)
       */
      public void visit(And and) throws VisitException
      {
         append('(');
         and.getLeft().accept(this);
         append(" AND ");
         and.getRight().accept(this);
         append(')');
      }

      /**
       * {@inheritDoc}
       * 
       * @see QueryObjectModelVisitor#visit(BindVariableName)
       */
      public void visit(BindVariableName variable)
      {
         append('$').append(variable.getVariableName());
      }

      /**
       * {@inheritDoc}
       * 
       * @see QueryObjectModelVisitor#visit(ChildNode)
       */
      public void visit(ChildNode child)
      {
         append("ISCHILDNODE(");
         append(child.getSelectorName());
         append(',');
         append(child.getParentPath());
         append(')');
      }

      /**
       * {@inheritDoc}
       * 
       * @see QueryObjectModelVisitor#visit(ChildNodeJoinCondition)
       */
      public void visit(ChildNodeJoinCondition condition)
      {
         append("ISCHILDNODE(");
         append(condition.getChildSelectorName());
         append(',');
         append(condition.getParentSelectorName());
         append(')');
      }

      /**
       * {@inheritDoc}
       * 
       * @see QueryObjectModelVisitor#visit(Column)
       */
      public void visit(Column column)
      {
         append(column.getSelectorName());
         if (column.getPropertyName() == null)
         {
            append(".*");
         }
         else
         {
            String propertyName = column.getPropertyName();
            append('.').append(propertyName);
            if (!propertyName.equals(column.getColumnName()) && !propertyName.equals(column.getColumnName()))
            {
               append(" AS ").append(column.getColumnName());
            }
         }
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(Comparison)
       */
      public void visit(Comparison comparison) throws VisitException
      {
         comparison.getOperand1().accept(this);
         append(' ').append(comparison.getOperator().getSymbol()).append(' ');
         comparison.getOperand2().accept(this);
      }

      /**
       * {@inheritDoc}
       * 
       * @see QueryObjectModelVisitor#visit(DescendantNode)
       */
      public void visit(DescendantNode descendant)
      {
         append("ISDESCENDANTNODE(");
         append(descendant.getSelectorName());
         append(',');
         append(descendant.getAncestorPath());
         append(')');
      }

      /**
       * {@inheritDoc}
       * 
       * @see QueryObjectModelVisitor#visit(DescendantNodeJoinCondition)
       */
      public void visit(DescendantNodeJoinCondition condition)
      {
         append("ISDESCENDANTNODE(");
         append(condition.getDescendantSelectorName());
         append(',');
         append(condition.getAncestorSelectorName());
         append(')');
      }

      /**
       * {@inheritDoc}
       * 
       * @see QueryObjectModelVisitor#visit(EquiJoinCondition)
       */
      public void visit(EquiJoinCondition condition)
      {
         append(condition.getSelector1Name()).append('.').append(condition.getProperty1Name());
         append(" = ");
         append(condition.getSelector2Name()).append('.').append(condition.getProperty2Name());
      }

      /**
       * {@inheritDoc}
       * 
       * @see QueryObjectModelVisitor#visit(FullTextSearch)
       */
      public void visit(FullTextSearch fullText)
      {
         append("CONTAINS(").append(fullText.getSelectorName());
         if (fullText.getPropertyName() != null)
         {
            append('.').append(fullText.getPropertyName());
         }
         sb.append(",'").append(fullText.getFullTextSearchExpression()).append("')");
      }

      /**
       * {@inheritDoc}
       * 
       * @see QueryObjectModelVisitor#visit(FullTextSearchScore)
       */
      public void visit(FullTextSearchScore score)
      {
         append("SCORE(").append(score.getSelectorName()).append(')');
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(Join)
       */
      public void visit(Join join) throws VisitException
      {
         join.getLeft().accept(this);
         // if (join.getType() != JoinType.INNER) {
         sb.append(' ').append(join.getType().getSymbol());
         // } else {
         // sb.append(',');
         // }
         append(' ');
         join.getRight().accept(this);
         append(" ON ");
         join.getJoinCondition().accept(this);
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(Length)
       */
      public void visit(Length length) throws VisitException
      {
         append("LENGTH(");
         length.getPropertyValue().accept(this);
         append(')');
      }

      /**
       * {@inheritDoc}
       * 
       * @see QueryObjectModelVisitor#visit(Limit)
       */
      public void visit(Limit limit)
      {
         append("LIMIT ").append(limit.getRowLimit());
         if (limit.getOffset() != 0)
         {
            append(" OFFSET ").append(limit.getOffset());
         }
      }

      /**
       * {@inheritDoc}
       * 
       * @see QueryObjectModelVisitor#visit(Literal)
       */
      public void visit(Literal literal)
      {
         Object value = literal.getValue();
         boolean quote = value instanceof String;
         if (quote)
         {
            append('\'');
         }
         append(literal.getValue().toString());

         if (quote)
         {
            append('\'');
         }
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(LowerCase)
       */
      public void visit(LowerCase lowerCase) throws VisitException
      {
         append("LOWER(");
         lowerCase.getOperand().accept(this);
         append(')');
      }

      /**
       * {@inheritDoc}
       * 
       * @see QueryObjectModelVisitor#visit(NodeDepth)
       */
      public void visit(NodeDepth depth)
      {
         append("DEPTH(").append(depth.getSelectorName()).append(')');
      }

      /**
       * {@inheritDoc}
       * 
       * @see QueryObjectModelVisitor#visit(NodeLocalName)
       */
      public void visit(NodeLocalName name)
      {
         append("LOCALNAME(").append(name.getSelectorName()).append(')');
      }

      /**
       * {@inheritDoc}
       * 
       * @see QueryObjectModelVisitor#visit(NodeName)
       */
      public void visit(NodeName name)
      {
         append("NAME(").append(name.getSelectorName()).append(')');
      }

      /**
       * {@inheritDoc}
       * 
       * @see QueryObjectModelVisitor#visit(NamedSelector)
       */
      public void visit(Selector selector)
      {
         append(selector.getName());
         if (selector.hasAlias())
         {
            append(" AS ").append(selector.getAlias());
         }
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(Not)
       */
      public void visit(Not not) throws VisitException
      {
         append('(');
         append("NOT ");
         not.getConstraint().accept(this);
         append(')');
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(Or)
       */
      public void visit(Or or) throws VisitException
      {
         append('(');
         or.getLeft().accept(this);
         append(" OR ");
         or.getRight().accept(this);
         append(')');
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(Ordering)
       */
      public void visit(Ordering ordering) throws VisitException
      {
         ordering.getOperand().accept(this);
         append(' ').append(ordering.getOrder().getSymbol());
      }

      /**
       * {@inheritDoc}
       * 
       * @see QueryObjectModelVisitor#visit(PropertyExistence)
       */
      public void visit(PropertyExistence existence)
      {
         append(existence.getSelectorName()).append('.').append(existence.getPropertyName()).append(" IS NOT NULL");
      }

      /**
       * {@inheritDoc}
       * 
       * @see QueryObjectModelVisitor#visit(PropertyValue)
       */
      public void visit(PropertyValue value)
      {
         append(value.getSelectorName()).append('.').append(value.getPropertyName());
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(Query)
       */
      public void visit(Query query) throws VisitException
      {
         append("SELECT ");

         if (query.getColumns().isEmpty())
         {
            append('*');
         }
         else
         {
            boolean isFirst = true;
            for (Column column : query.getColumns())
            {
               if (isFirst)
               {
                  isFirst = false;
               }
               else
               {
                  append(',');
               }
               column.accept(this);
            }
         }
         append(" FROM ");
         query.getSource().accept(this);
         if (query.getConstraint() != null)
         {
            append(" WHERE ");
            query.getConstraint().accept(this);
         }
         if (!query.getOrderings().isEmpty())
         {
            append(" ORDER BY ");
            boolean isFirst = true;
            for (Ordering ordering : query.getOrderings())
            {
               if (isFirst)
               {
                  isFirst = false;
               }
               else
               {
                  append(',');
               }
               ordering.accept(this);
            }
         }
         if (!query.getLimits().isUnlimited())
         {
            append(' ');
            query.getLimits().accept(this);
         }
      }

      /**
       * {@inheritDoc}
       * 
       * @see QueryObjectModelVisitor#visit(SameNode)
       */
      public void visit(SameNode sameNode)
      {
         append("ISSAMENODE(").append(sameNode.getSelectorName()).append(',').append(sameNode.getPath()).append(')');
      }

      /**
       * {@inheritDoc}
       * 
       * @see QueryObjectModelVisitor#visit(SameNodeJoinCondition)
       */
      public void visit(SameNodeJoinCondition condition)
      {
         append("ISSAMENODE(").append(condition.getSelector1Name()).append(',').append(condition.getSelector2Name());
         if (condition.getSelector2Path() != null)
         {
            append(',').append(condition.getSelector2Path());
         }
         append(')');
      }

      /**
       * {@inheritDoc}
       * @throws VisitException 
       * 
       * @see QueryObjectModelVisitor#visit(UpperCase)
       */
      public void visit(UpperCase upperCase) throws VisitException
      {
         append("UPPER(");
         upperCase.getOperand().accept(this);
         append(')');
      }

   }

}
