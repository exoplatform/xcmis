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
package org.xcmis.search.qom;

import org.exoplatform.services.jcr.datamodel.InternalQName;
import org.exoplatform.services.jcr.datamodel.QPath;
import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.qom.column.ColumnImpl;
import org.xcmis.search.qom.constraint.AndImpl;
import org.xcmis.search.qom.constraint.ChildNodeImpl;
import org.xcmis.search.qom.constraint.ComparisonImpl;
import org.xcmis.search.qom.constraint.DescendantNodeImpl;
import org.xcmis.search.qom.constraint.FullTextSearchImpl;
import org.xcmis.search.qom.constraint.InFolderNode;
import org.xcmis.search.qom.constraint.InFolderNodeImpl;
import org.xcmis.search.qom.constraint.InTreeNode;
import org.xcmis.search.qom.constraint.InTreeNodeImpl;
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
import org.xcmis.search.qom.source.join.ChildNodeJoinConditionImpl;
import org.xcmis.search.qom.source.join.DescendantNodeJoinConditionImpl;
import org.xcmis.search.qom.source.join.EquiJoinConditionImpl;
import org.xcmis.search.qom.source.join.SameNodeJoinConditionImpl;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.qom.And;
import javax.jcr.query.qom.BindVariableValue;
import javax.jcr.query.qom.ChildNode;
import javax.jcr.query.qom.ChildNodeJoinCondition;
import javax.jcr.query.qom.Column;
import javax.jcr.query.qom.Comparison;
import javax.jcr.query.qom.Constraint;
import javax.jcr.query.qom.DescendantNode;
import javax.jcr.query.qom.DescendantNodeJoinCondition;
import javax.jcr.query.qom.DynamicOperand;
import javax.jcr.query.qom.EquiJoinCondition;
import javax.jcr.query.qom.FullTextSearch;
import javax.jcr.query.qom.FullTextSearchScore;
import javax.jcr.query.qom.Join;
import javax.jcr.query.qom.JoinCondition;
import javax.jcr.query.qom.Length;
import javax.jcr.query.qom.Literal;
import javax.jcr.query.qom.LowerCase;
import javax.jcr.query.qom.NodeLocalName;
import javax.jcr.query.qom.NodeName;
import javax.jcr.query.qom.Not;
import javax.jcr.query.qom.Or;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.PropertyExistence;
import javax.jcr.query.qom.PropertyValue;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelConstants;
import javax.jcr.query.qom.QueryObjectModelFactory;
import javax.jcr.query.qom.SameNode;
import javax.jcr.query.qom.SameNodeJoinCondition;
import javax.jcr.query.qom.Selector;
import javax.jcr.query.qom.Source;
import javax.jcr.query.qom.StaticOperand;
import javax.jcr.query.qom.UpperCase;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id$
 */
public abstract class AbstractQueryObjectModelFactory implements QueryObjectModelFactory,
   ExtendedQueryObjectModelFactory
{
   /**
    * Location factory.
    */
   private LocationFactory locationFactory;

   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(AbstractQueryObjectModelFactory.class.getName());

   // protected SessionImpl session;

   /**
    *  
    * Creates a query with one or more selectors.
    * <p>
    * Returned QueryObjectModel will be marked as specified language query.
    *
    * @param source     the node-tuple source; non-null
    * @param constraint the constraint, or null if none
    * @param orderings  zero or more orderings; null is equivalent to a
    *                   zero-length array
    * @param columns    the columns; null is equivalent to a zero-length array
    * @return the query; non-null
    * @throws InvalidQueryException if a particular validity test is possible
    *                               on this method, the implemention chooses to perform that test and the
    *                               parameters given fail that test. See the individual QOM factory methods
    *                               for the validity criteria of each query element.
    * @throws RepositoryException   if another error occurs.
    */
   public abstract QueryObjectModel createQuery(final Source selector, final Constraint constraint,
      final Ordering[] orderings, final Column[] columns, final String language) throws InvalidQueryException,
      RepositoryException;

   /**
    * {@inheritDoc}
    */
   public And and(Constraint constraint1, Constraint constraint2) throws InvalidQueryException, RepositoryException
   {
      return new AndImpl(locationFactory, constraint1, constraint2);
   }

   /**
    * {@inheritDoc}
    */
   public Ordering ascending(DynamicOperand operand) throws InvalidQueryException, RepositoryException
   {
      return new OrderingImpl(locationFactory, operand, QueryObjectModelConstants.JCR_ORDER_ASCENDING);
   }

   /**
    * {@inheritDoc}
    */
   public BindVariableValue bindVariable(String bindVariableName) throws InvalidQueryException, RepositoryException
   {

      InternalQName bindVariableQName;
      try
      {
         bindVariableQName = locationFactory.parseJCRName(bindVariableName).getInternalName();
      }
      catch (RepositoryException e)
      {
         throw new InvalidQueryException(e.getLocalizedMessage(), e);
      }
      return new BindVariableValueImpl(locationFactory, bindVariableQName);
   }

   /**
    * {@inheritDoc}
    */
   public ChildNode childNode(String selectorName, String path) throws InvalidQueryException, RepositoryException
   {

      InternalQName selectorQName;
      QPath qPath;
      try
      {
         selectorQName = locationFactory.parseJCRName(selectorName).getInternalName();
         qPath = locationFactory.parseJCRPath(path).getInternalPath();
      }
      catch (RepositoryException e)
      {
         throw new InvalidQueryException(e.getLocalizedMessage(), e);
      }
      if (!qPath.isAbsolute())
      {
         throw new InvalidQueryException(path + " should be absolute path. See 6.7.21 ChildNode  ");
      }
      return new ChildNodeImpl(locationFactory, selectorQName, qPath);
   }

   /**
    * {@inheritDoc}
    */
   public ChildNodeJoinCondition childNodeJoinCondition(String childSelectorName, String parentSelectorName)
      throws InvalidQueryException, RepositoryException
   {

      InternalQName childSelectorQName;
      InternalQName parentSelectorQName;
      try
      {
         childSelectorQName = locationFactory.parseJCRName(childSelectorName).getInternalName();
         parentSelectorQName = locationFactory.parseJCRName(parentSelectorName).getInternalName();

      }
      catch (RepositoryException e)
      {
         throw new InvalidQueryException(e.getLocalizedMessage(), e);
      }

      return new ChildNodeJoinConditionImpl(locationFactory, childSelectorQName, parentSelectorQName);
   }

   /**
    * {@inheritDoc}
    */
   public Column column(String selectorName, String propertyName, String columnName) throws InvalidQueryException,
      RepositoryException
   {
      InternalQName selectorQName = null;
      InternalQName propertyQName = null;
      try
      {

         if (propertyName == null && columnName != null)
         {
            throw new InvalidQueryException("ColumnName should be null.");
         }
         if (propertyName != null && columnName == null)
         {
            throw new InvalidQueryException("ColumnName is required.");
         }

         // check names
         if (selectorName != null)
         {
            selectorQName = locationFactory.parseJCRName(selectorName).getInternalName();
         }
         if (propertyName != null)
         {
            propertyQName = locationFactory.parseJCRName(propertyName).getInternalName();
         }
      }
      catch (RepositoryException e)
      {
         throw new InvalidQueryException(e.getLocalizedMessage(), e);
      }

      return new ColumnImpl(locationFactory, selectorQName, propertyQName, columnName);
   }

   /**
    * {@inheritDoc}
    */
   public Comparison comparison(DynamicOperand operand1, String operator, StaticOperand operand2)
      throws InvalidQueryException, RepositoryException
   {
      return new ComparisonImpl(locationFactory, operand1, operator, operand2);
   }

   /**
    * {@inheritDoc}
    */
   public DescendantNode descendantNode(String selectorName, String path) throws InvalidQueryException,
      RepositoryException
   {
      InternalQName selectorQName;
      QPath qPath;
      try
      {
         selectorQName = locationFactory.parseJCRName(selectorName).getInternalName();
         qPath = locationFactory.parseJCRPath(path).getInternalPath();
      }
      catch (RepositoryException e)
      {
         throw new InvalidQueryException(e.getLocalizedMessage(), e);
      }
      if (!qPath.isAbsolute())
      {
         throw new InvalidQueryException(path + " should be absolute path. See 6.7.22 DescendantNode  ");
      }

      return new DescendantNodeImpl(locationFactory, selectorQName, qPath);
   }

   /**
    * {@inheritDoc}
    */
   public DescendantNodeJoinCondition descendantNodeJoinCondition(String descendantSelectorName,
      String ancestorSelectorName) throws InvalidQueryException, RepositoryException
   {
      InternalQName descendantSelectorQName;
      InternalQName ancestorSelectorQName;

      try
      {
         descendantSelectorQName = locationFactory.parseJCRName(descendantSelectorName).getInternalName();
         ancestorSelectorQName = locationFactory.parseJCRName(ancestorSelectorName).getInternalName();
      }
      catch (RepositoryException e)
      {
         throw new InvalidQueryException(e.getLocalizedMessage(), e);
      }
      return new DescendantNodeJoinConditionImpl(locationFactory, descendantSelectorQName, ancestorSelectorQName);
   }

   /**
    * {@inheritDoc}
    */
   public Ordering descending(DynamicOperand operand) throws InvalidQueryException, RepositoryException
   {
      return new OrderingImpl(locationFactory, operand, QueryObjectModelConstants.JCR_ORDER_DESCENDING);
   }

   /**
    * {@inheritDoc}
    */
   public EquiJoinCondition equiJoinCondition(String selector1Name, String property1Name, String selector2Name,
      String property2Name) throws InvalidQueryException, RepositoryException
   {
      InternalQName selector1QName;
      InternalQName property1QName;
      InternalQName selector2QName;
      InternalQName property2QName;

      try
      {

         selector1QName = locationFactory.parseJCRName(selector1Name).getInternalName();
         property1QName = locationFactory.parseJCRName(property1Name).getInternalName();
         selector2QName = locationFactory.parseJCRName(selector2Name).getInternalName();
         property2QName = locationFactory.parseJCRName(property2Name).getInternalName();

      }
      catch (RepositoryException e)
      {
         throw new InvalidQueryException(e.getLocalizedMessage(), e);
      }

      //

      return new EquiJoinConditionImpl(locationFactory, selector1QName, property1QName, selector2QName, property2QName);
   }

   public FullTextSearch fullTextSearch(String selectorName, String propertyName, StaticOperand fullTextSearchExpression)
      throws InvalidQueryException, RepositoryException
   {
      InternalQName selectorQName = null;
      InternalQName propertyQName = null;
      try
      {
         if (selectorName != null)
         {
            selectorQName = locationFactory.parseJCRName(selectorName).getInternalName();
         }
         if (propertyName != null)
         {
            propertyQName = locationFactory.parseJCRName(propertyName).getInternalName();
         }
      }
      catch (RepositoryException e)
      {
         throw new InvalidQueryException(e.getLocalizedMessage(), e);
      }
      return new FullTextSearchImpl(locationFactory, selectorQName, propertyQName, fullTextSearchExpression);
   }

   /**
    * {@inheritDoc}
    */
   public FullTextSearchScore fullTextSearchScore(String selectorName) throws InvalidQueryException,
      RepositoryException
   {

      InternalQName selectorQName;

      try
      {
         selectorQName = locationFactory.parseJCRName(selectorName).getInternalName();

      }
      catch (RepositoryException e)
      {
         throw new InvalidQueryException(e.getLocalizedMessage(), e);
      }

      return new FullTextSearchScoreImpl(locationFactory, selectorQName);
   }

   /**
    * @param locationFactory - location factory.
    * @throws RepositoryException
    * @throws RepositoryException
    */
   public void init(LocationFactory locationFactory) throws RepositoryException
   {

      this.locationFactory = locationFactory;
   }

   /**
    * {@inheritDoc}
    */
   public Join join(Source left, Source right, String joinType, JoinCondition joinCondition)
      throws InvalidQueryException, RepositoryException
   {
      return new JoinImpl(locationFactory, left, right, joinType, joinCondition);
   }

   /**
    * {@inheritDoc}
    */
   public Length length(PropertyValue propertyValue) throws InvalidQueryException, RepositoryException
   {
      return new LengthImpl(locationFactory, propertyValue);
   }

   /**
    * {@inheritDoc}
    */

   public Literal literal(Value value) throws InvalidQueryException, RepositoryException
   {

      if (value == null)
      {
         throw new InvalidQueryException("Value can't be null.");
      }
      return new LiteralImpl(locationFactory, value);
   }

   /**
    * {@inheritDoc}
    */
   public LowerCase lowerCase(DynamicOperand operand) throws InvalidQueryException, RepositoryException
   {
      if (operand == null || !(operand instanceof DynamicOperandImpl))
      {
         throw new InvalidQueryException("Invalid operator");
      }
      return new LowerCaseImpl(locationFactory, operand);
   }

   /**
    * {@inheritDoc}
    */
   public NodeLocalName nodeLocalName(String selectorName) throws InvalidQueryException, RepositoryException
   {
      InternalQName selectorQName;

      try
      {
         selectorQName = locationFactory.parseJCRName(selectorName).getInternalName();

      }
      catch (RepositoryException e)
      {
         throw new InvalidQueryException(e.getLocalizedMessage(), e);
      }
      return new NodeLocalNameImpl(locationFactory, selectorQName);
   }

   /**
    * {@inheritDoc}
    */
   public NodeName nodeName(String selectorName) throws InvalidQueryException, RepositoryException
   {
      InternalQName selectorQName;

      try
      {
         selectorQName = locationFactory.parseJCRName(selectorName).getInternalName();

      }
      catch (RepositoryException e)
      {
         throw new InvalidQueryException(e.getLocalizedMessage(), e);
      }
      return new NodeNameImpl(locationFactory, selectorQName);
   }

   /**
    * {@inheritDoc}
    */
   public Not not(Constraint constraint) throws InvalidQueryException, RepositoryException
   {
      return new NotImpl(locationFactory, constraint);
   }

   /**
    * {@inheritDoc}
    */
   public Or or(Constraint constraint1, Constraint constraint2) throws InvalidQueryException, RepositoryException
   {
      return new OrImpl(locationFactory, constraint1, constraint2);
   }

   /**
    * {@inheritDoc}
    */
   public PropertyExistence propertyExistence(String selectorName, String propertyName) throws InvalidQueryException,
      RepositoryException
   {
      InternalQName selectorQName;
      InternalQName propertyQName;
      try
      {
         selectorQName = locationFactory.parseJCRName(selectorName).getInternalName();
         propertyQName = locationFactory.parseJCRName(propertyName).getInternalName();
      }
      catch (RepositoryException e)
      {
         throw new InvalidQueryException(e.getLocalizedMessage(), e);
      }

      return new PropertyExistenceImpl(locationFactory, selectorQName, propertyQName);
   }

   /**
    * {@inheritDoc}
    */
   public PropertyValue propertyValue(String selectorName, String propertyName) throws InvalidQueryException,
      RepositoryException
   {
      InternalQName selectorQName;
      InternalQName propertyQName;
      try
      {
         selectorQName = locationFactory.parseJCRName(selectorName).getInternalName();
         propertyQName = locationFactory.parseJCRName(propertyName).getInternalName();
      }
      catch (RepositoryException e)
      {
         throw new InvalidQueryException(e.getLocalizedMessage(), e);
      }
      return new PropertyValueImpl(locationFactory, selectorQName, propertyQName);
   }

   /**
    * {@inheritDoc}
    */
   public SameNode sameNode(String selectorName, String path) throws InvalidQueryException, RepositoryException
   {
      InternalQName selectorQName;
      QPath qPath;
      try
      {
         selectorQName = locationFactory.parseJCRName(selectorName).getInternalName();
         qPath = locationFactory.parseJCRPath(path).getInternalPath();
      }
      catch (RepositoryException e)
      {
         throw new InvalidQueryException(e.getLocalizedMessage(), e);
      }

      if (!qPath.isAbsolute())
      {
         throw new InvalidQueryException(path + " should be absolute path. See 6.7.21 ChildNode  ");
      }

      return new SameNodeImpl(locationFactory, selectorQName, qPath);
   }

   /**
    * {@inheritDoc}
    */
   public SameNodeJoinCondition sameNodeJoinCondition(String selector1Name, String selector2Name, String selector2Path)
      throws InvalidQueryException, RepositoryException
   {

      InternalQName selector1QName;
      InternalQName selector2QName;
      QPath selector2QPath = null;
      try
      {
         selector1QName = locationFactory.parseJCRName(selector1Name).getInternalName();
         selector2QName = locationFactory.parseJCRName(selector2Name).getInternalName();
         if (selector2Path != null)
         {
            selector2QPath = locationFactory.parseJCRPath(selector2Path).getInternalPath();
         }
      }
      catch (RepositoryException e)
      {
         throw new InvalidQueryException(e.getLocalizedMessage(), e);
      }

      return new SameNodeJoinConditionImpl(locationFactory, selector1QName, selector2QName, selector2QPath);
   }

   /**
    * {@inheritDoc}
    */
   public Selector selector(String nodeTypeName, String selectorName) throws InvalidQueryException, RepositoryException
   {
      InternalQName nodeTypeQName;
      InternalQName selectorQName;
      try
      {
         nodeTypeQName = locationFactory.parseJCRName(nodeTypeName).getInternalName();
         selectorQName = locationFactory.parseJCRName(selectorName).getInternalName();
      }
      catch (RepositoryException e)
      {
         throw new InvalidQueryException(e.getLocalizedMessage(), e);
      }
      return new SelectorImpl(locationFactory, nodeTypeQName, selectorQName);
   }

   /**
    * {@inheritDoc}
    */
   public UpperCase upperCase(DynamicOperand operand) throws InvalidQueryException, RepositoryException
   {
      if (operand == null || !(operand instanceof DynamicOperandImpl))
      {
         throw new InvalidQueryException("Invalid operator");
      }
      return new UpperCaseImpl(locationFactory, operand);
   }

   /**
    * {@inheritDoc}
    */
   public InTreeNode inTree(String selectorName, String id) throws InvalidQueryException, RepositoryException
   {

      InternalQName selectorQName;

      try
      {
         selectorQName = locationFactory.parseJCRName(selectorName).getInternalName();
      }
      catch (RepositoryException e)
      {
         throw new InvalidQueryException(e.getLocalizedMessage(), e);
      }

      if (!id.matches("[a-fA-F0-9]+"))
      {
         throw new InvalidQueryException("Folder id [" + id + "] is not valid UUID.");
      }
      return new InTreeNodeImpl(locationFactory, selectorQName, id);
   }

   /**
    * {@inheritDoc}
    */
   public InFolderNode inFolder(String selectorName, String id) throws InvalidQueryException, RepositoryException
   {
      InternalQName selectorQName;

      try
      {
         selectorQName = locationFactory.parseJCRName(selectorName).getInternalName();
      }
      catch (RepositoryException e)
      {
         throw new InvalidQueryException(e.getLocalizedMessage(), e);
      }

      if (!id.matches("[a-fA-F0-9]+"))
      {
         throw new InvalidQueryException("Folder id [" + id + "] is not valid UUID.");
      }
      return new InFolderNodeImpl(locationFactory, selectorQName, id);
   }

}
