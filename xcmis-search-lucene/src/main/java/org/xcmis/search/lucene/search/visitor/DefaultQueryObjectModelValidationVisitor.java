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

import org.exoplatform.services.jcr.datamodel.InternalQName;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.qom.column.ColumnImpl;
import org.xcmis.search.qom.constraint.ChildNodeImpl;
import org.xcmis.search.qom.constraint.ComparisonImpl;
import org.xcmis.search.qom.constraint.DescendantNodeImpl;
import org.xcmis.search.qom.constraint.FullTextSearchImpl;
import org.xcmis.search.qom.constraint.PropertyExistenceImpl;
import org.xcmis.search.qom.constraint.SameNodeImpl;
import org.xcmis.search.qom.operand.BindVariableValueImpl;
import org.xcmis.search.qom.operand.FullTextSearchScoreImpl;
import org.xcmis.search.qom.operand.NodeLocalNameImpl;
import org.xcmis.search.qom.operand.NodeNameImpl;
import org.xcmis.search.qom.operand.PropertyValueImpl;
import org.xcmis.search.qom.source.SelectorImpl;
import org.xcmis.search.qom.source.join.ChildNodeJoinConditionImpl;
import org.xcmis.search.qom.source.join.DescendantNodeJoinConditionImpl;
import org.xcmis.search.qom.source.join.EquiJoinConditionImpl;
import org.xcmis.search.qom.source.join.SameNodeJoinConditionImpl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.jcr.PropertyType;
import javax.jcr.Value;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.qom.Column;
import javax.jcr.query.qom.DynamicOperand;
import javax.jcr.query.qom.Literal;
import javax.jcr.query.qom.NodeName;
import javax.jcr.query.qom.StaticOperand;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34027 2009-07-15 23:26:43Z
 *          aheritier $
 */
public class DefaultQueryObjectModelValidationVisitor extends QueryObjectModelTraversingVisitor
{
   protected final Set<String> bindVariables;

   protected final Map<String, Column> columns;

   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(getClass().getName());

   /**
    * Selectors and its node type list.
    */
   protected final Map<String, InternalQName> selectors;

   /**
    * Default constructor.
    */
   public DefaultQueryObjectModelValidationVisitor()
   {
      super();
      selectors = new HashMap<String, InternalQName>();
      bindVariables = new HashSet<String>();
      columns = new HashMap<String, Column>();
   }

   public Set<String> getBindVariables()
   {
      return bindVariables;
   }

   public Column[] getColumns()
   {

      if (!columns.isEmpty())
      {
         Iterator it = columns.values().iterator();
         Column[] ret = new Column[columns.size()];
         int i = 0;
         while (it.hasNext())
         {
            Column c = (Column)it.next();
            ret[i] = c;
            i++;
         }
         return ret;
      }
      else
      {
         return new Column[0];
      }
   }

   public Set<String> getSelectors()
   {
      return selectors.keySet();
   }

   @Override
   public Object visit(BindVariableValueImpl node, Object context) throws Exception
   {
      bindVariables.add(node.getBindVariableName());
      return super.visit(node, context);
   }

   @Override
   public Object visit(ChildNodeImpl node, Object context) throws Exception
   {
      if (node.getSelectorName() != null && !selectors.containsKey(node.getSelectorName()))
      {
         throw new InvalidQueryException("Selector " + node.getSelectorName() + " desn't exists");
      }
      return super.visit(node, context);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(ChildNodeJoinConditionImpl node, Object context) throws Exception
   {
      if (!selectors.containsKey(node.getChildSelectorName()))
      {
         throw new InvalidQueryException("Selector " + node.getChildSelectorName() + " desn't exists");
      }
      if (!selectors.containsKey(node.getParentSelectorName()))
      {
         throw new InvalidQueryException("Selector " + node.getParentSelectorName() + " desn't exists");
      }

      if (node.getChildSelectorName().equals(node.getParentSelectorName()))
      {
         throw new InvalidQueryException(node.getChildSelectorName() + " is the same as "
            + node.getParentSelectorName());
      }
      return super.visit(node, context);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(ColumnImpl node, Object context) throws Exception
   {
      String selectorName = node.getSelectorName();
      if (selectorName != null && !selectors.containsKey(selectorName))
      {
         throw new InvalidQueryException("Selector " + selectorName + " desn't exists");
      }

      return super.visit(node, context);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(ComparisonImpl node, Object context) throws Exception
   {
      // TODO Auto-generated method stub
      super.visit(node, context);

      DynamicOperand dynamicOperandImpl = node.getOperand1();
      StaticOperand staticOperand = node.getOperand2();
      if (dynamicOperandImpl instanceof NodeName)
      {
         if (staticOperand instanceof Literal)
         {
            Value value = ((Literal)staticOperand).getLiteralValue();
            if (value.getType() == PropertyType.LONG || value.getType() == PropertyType.BOOLEAN)
            {
               throw new InvalidQueryException("Unsuported use of comparation NodeName and value with type "
                  + value.getType());
            }
         }

      }
      return new Object();
   }

   @Override
   public Object visit(DescendantNodeImpl node, Object context) throws Exception
   {
      if (node.getSelectorName() != null && !selectors.containsKey(node.getSelectorName()))
      {
         throw new InvalidQueryException("Selector " + node.getSelectorName() + " desn't exists");
      }
      return super.visit(node, context);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(DescendantNodeJoinConditionImpl node, Object context) throws Exception
   {
      if (!selectors.containsKey(node.getAncestorSelectorName()))
      {
         throw new InvalidQueryException("Selector " + node.getAncestorSelectorName() + " desn't exists");
      }
      if (!selectors.containsKey(node.getDescendantSelectorName()))
      {
         throw new InvalidQueryException("Selector " + node.getDescendantSelectorName() + " desn't exists");
      }

      if (node.getAncestorSelectorName().equals(node.getDescendantSelectorName()))
      {
         throw new InvalidQueryException(node.getAncestorSelectorName() + " is the same as "
            + node.getDescendantSelectorName());
      }
      return super.visit(node, context);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(EquiJoinConditionImpl node, Object context) throws Exception
   {

      if (!selectors.containsKey(node.getSelector1Name()))
      {
         throw new InvalidQueryException("Selector " + node.getSelector1Name() + " desn't exists");
      }
      if (!selectors.containsKey(node.getSelector2Name()))
      {
         throw new InvalidQueryException("Selector " + node.getSelector2Name() + " desn't exists");
      }

      if (node.getSelector1Name().equals(node.getSelector2Name()))
      {
         throw new InvalidQueryException(node.getSelector1Name() + " is the same as " + node.getSelector2Name());
      }
      return super.visit(node, context);

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(FullTextSearchImpl node, Object context) throws Exception
   {
      if (node.getSelectorName() != null && !selectors.containsKey(node.getSelectorName()))
      {
         throw new InvalidQueryException("Selector " + node.getSelectorName() + " desn't exists");
      }

      return super.visit(node, context);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(FullTextSearchScoreImpl node, Object context) throws Exception
   {
      if (node.getSelectorName() != null && !selectors.containsKey(node.getSelectorName()))
      {
         throw new InvalidQueryException("Selector " + node.getSelectorName() + " desn't exists");
      }
      return super.visit(node, context);
   }

   @Override
   public Object visit(NodeLocalNameImpl node, Object context) throws Exception
   {
      if (node.getSelectorName() != null && !selectors.containsKey(node.getSelectorName()))
      {
         throw new InvalidQueryException("Selector " + node.getSelectorName() + " desn't exists");
      }
      return super.visit(node, context);
   }

   @Override
   public Object visit(NodeNameImpl node, Object context) throws Exception
   {
      if (node.getSelectorName() != null && !selectors.containsKey(node.getSelectorName()))
      {
         throw new InvalidQueryException("Selector " + node.getSelectorName() + " desn't exists");
      }
      return super.visit(node, context);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(PropertyExistenceImpl node, Object context) throws Exception
   {
      if (node.getSelectorName() != null && !selectors.containsKey(node.getSelectorName()))
      {
         throw new InvalidQueryException("Selector " + node.getSelectorName() + " desn't exists");
      }
      return super.visit(node, context);
   }

   @Override
   public Object visit(PropertyValueImpl node, Object context) throws Exception
   {
      if (node.getSelectorName() != null && !selectors.containsKey(node.getSelectorName()))
      {
         throw new InvalidQueryException("Selector " + node.getSelectorName() + " desn't exists");
      }
      return super.visit(node, context);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(SameNodeImpl node, Object context) throws Exception
   {
      if (node.getSelectorName() != null && !selectors.containsKey(node.getSelectorName()))
      {
         throw new InvalidQueryException("Selector " + node.getSelectorName() + " desn't exists");
      }
      return super.visit(node, context);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(SameNodeJoinConditionImpl node, Object context) throws Exception
   {
      if (!selectors.containsKey(node.getSelector1Name()))
      {
         throw new InvalidQueryException("Selector " + node.getSelector1Name() + " desn't exists");
      }
      if (!selectors.containsKey(node.getSelector2Name()))
      {
         throw new InvalidQueryException("Selector " + node.getSelector2Name() + " desn't exists");
      }

      if (node.getSelector1Name().equals(node.getSelector2Name()))
      {
         throw new InvalidQueryException(node.getSelector1Name() + " is the same as " + node.getSelector2Name());
      }
      return super.visit(node, context);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(SelectorImpl node, Object context) throws Exception
   {
      String selectorName = node.getSelectorName();
      selectors.put(selectorName, node.getNodeTypeQName());
      return super.visit(node, context);
   }

}
