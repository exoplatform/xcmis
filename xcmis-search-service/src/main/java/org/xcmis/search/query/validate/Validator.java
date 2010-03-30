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
package org.xcmis.search.query.validate;

import org.xcmis.search.InvalidQueryException;
import org.xcmis.search.VisitException;
import org.xcmis.search.Visitors.AbstractModelVisitor;
import org.xcmis.search.content.Schema;
import org.xcmis.search.content.TableDoesntExistException;
import org.xcmis.search.content.Schema.Table;
import org.xcmis.search.model.Query;
import org.xcmis.search.model.column.Column;
import org.xcmis.search.model.constraint.ChildNode;
import org.xcmis.search.model.constraint.Comparison;
import org.xcmis.search.model.constraint.DescendantNode;
import org.xcmis.search.model.constraint.FullTextSearch;
import org.xcmis.search.model.constraint.PropertyExistence;
import org.xcmis.search.model.constraint.SameNode;
import org.xcmis.search.model.operand.FullTextSearchScore;
import org.xcmis.search.model.operand.Length;
import org.xcmis.search.model.operand.LowerCase;
import org.xcmis.search.model.operand.NodeDepth;
import org.xcmis.search.model.operand.NodeLocalName;
import org.xcmis.search.model.operand.NodeName;
import org.xcmis.search.model.operand.PropertyValue;
import org.xcmis.search.model.operand.UpperCase;
import org.xcmis.search.model.ordering.Ordering;
import org.xcmis.search.model.source.SelectorName;
import org.xcmis.search.model.source.join.ChildNodeJoinCondition;
import org.xcmis.search.model.source.join.DescendantNodeJoinCondition;
import org.xcmis.search.model.source.join.EquiJoinCondition;
import org.xcmis.search.model.source.join.SameNodeJoinCondition;
import org.xcmis.search.query.QueryExecutionContext;
import org.xcmis.search.query.QueryExecutionExceptions;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link Visitor} implementation that validates a query's used of a {@link Schema} and records any problems as errors.
 */
public class Validator extends AbstractModelVisitor
{

   private final QueryExecutionContext context;

   private final QueryExecutionExceptions problems;

   private final Map<SelectorName, Table> selectorsByNameOrAlias;

   private final Map<SelectorName, Table> selectorsByName;

   private final Map<String, Schema.Column> columnsByAlias;

   private final boolean validateColumnExistence;

   /**
    * @param context the query context
    * @param selectorsByName the {@link Table tables} by their name or alias, as defined by the selectors
    */
   public Validator(QueryExecutionContext context, Map<SelectorName, Table> selectorsByName)
   {
      this.context = context;
      this.problems = this.context.getExecutionExceptions();
      this.selectorsByNameOrAlias = selectorsByName;
      this.selectorsByName = new HashMap<SelectorName, Table>();
      for (Table table : selectorsByName.values())
      {
         this.selectorsByName.put(table.getName(), table);
      }
      this.columnsByAlias = new HashMap<String, Schema.Column>();
      this.validateColumnExistence = true;
   }

   /**
    * Check if selector exists in list of selectors
    * @param selectorName
    * @return 
    */
   public Table checkSelectorExistance(SelectorName selectorName)
   {
      Table table = selectorsByNameOrAlias.get(selectorName);
      if (table == null)
      {
         // Try looking up the table by it's real name (if an alias were used) ...
         table = selectorsByName.get(selectorName);
      }
      if (table == null)
      {
         problems.addException(new TableDoesntExistException("Table " + selectorName.getName() + " doesnt exist"));
      }
      return table;
   }

   /**
    * Check if selector exists in list of selectors
    * @param selectorName
    */
   public Schema.Column checkTableAndColumnExistance(SelectorName selectorName, String propertyName,
      boolean columnIsRequired)
   {
      Table table = checkSelectorExistance(selectorName);
      //no need to check select ALL properties.
      if (table != null && !propertyName.equals("*"))
      {
         Schema.Column column = table.getColumn(propertyName);
         if (column == null)
         {
            // Maybe the supplied property name is really an alias ...
            column = this.columnsByAlias.get(propertyName);
            if (column == null && columnIsRequired)
            {
               problems.addException(new InvalidQueryException("Column " + propertyName + " doesnt exist on table "
                  + selectorName.getName() + " or not allowed for search"));
            }

         }
         return column;
      }
      return null;
   }

   /**
    * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.constraint.ChildNode)
    */
   @Override
   public void visit(ChildNode node) throws VisitException
   {
      checkSelectorExistance(node.getSelectorName());
   }

   /**
    * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.source.join.ChildNodeJoinCondition)
    */
   @Override
   public void visit(ChildNodeJoinCondition node) throws VisitException
   {
      checkSelectorExistance(node.getChildSelectorName());
      checkSelectorExistance(node.getParentSelectorName());

      if (node.getChildSelectorName().equals(node.getParentSelectorName()))
      {
         problems.addException(new InvalidQueryException(node.getChildSelectorName() + " is the same as "
            + node.getParentSelectorName()));
      }
   }

   /**
    * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.column.Column)
    */
   @Override
   public void visit(Column node) throws VisitException
   {
      if (!node.isFunction())
      {
         //column should exist's
         checkTableAndColumnExistance(node.getSelectorName(), node.getPropertyName(), true);
      }
   }

   /**
    * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.constraint.Comparison)
    */
   @Override
   public void visit(Comparison node) throws VisitException
   {

      super.visit(node);

      //TODO  available query operator for property
      //TODO NodeName is not accepteable for long and boolean
   }

   /**
    * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.constraint.DescendantNode)
    */
   @Override
   public void visit(DescendantNode node) throws VisitException
   {
      checkSelectorExistance(node.getSelectorName());
   }

   /**
    * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.source.join.DescendantNodeJoinCondition)
    */
   @Override
   public void visit(DescendantNodeJoinCondition node) throws VisitException
   {
      checkSelectorExistance(node.getAncestorSelectorName());
      checkSelectorExistance(node.getDescendantSelectorName());
   }

   /**
    * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.source.join.EquiJoinCondition)
    */
   @Override
   public void visit(EquiJoinCondition node) throws VisitException
   {
      checkTableAndColumnExistance(node.getSelector1Name(), node.getProperty1Name(), true);
      checkTableAndColumnExistance(node.getSelector2Name(), node.getProperty2Name(), true);
      if (node.getSelector1Name().equals(node.getSelector2Name()))
      {
         problems.addException(new InvalidQueryException(node.getSelector1Name() + " is the same as "
            + node.getSelector2Name()));
      }
   }

   /**
    * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.constraint.FullTextSearch)
    */
   @Override
   public void visit(FullTextSearch node) throws VisitException
   {
      SelectorName selectorName = node.getSelectorName();
      if (node.getPropertyName() != null)
      {
         Schema.Column column =
            checkTableAndColumnExistance(selectorName, node.getPropertyName(), this.validateColumnExistence);
         if (column != null)
         {
            // Make sure the column is full-text searchable ...
            if (!column.isFullTextSearchable())
            {
               problems.addException(new InvalidQueryException("Column " + column.getName() + " on table "
                  + selectorName + " does not support full-text searching"));
            }

         }
      }
      else
      {
         Table table = checkSelectorExistance(selectorName);

         if (table != null)
         {
            // Make sure there is at least one column on the table that is full-text searchable ...
            boolean searchable = false;
            for (Schema.Column column : table.getColumns())
            {
               if (column.isFullTextSearchable())
               {
                  searchable = true;
                  break;
               }
            }
            if (!searchable)
            {
               problems.addException(new InvalidQueryException("Table '" + selectorName
                  + "' has no columns that support full-text searching"));
            }
         }
      }
   }

   /**
    * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.operand.FullTextSearchScore)
    */
   @Override
   public void visit(FullTextSearchScore node) throws VisitException
   {
      checkSelectorExistance(node.getSelectorName());
   }

   /**
    * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.operand.Length)
    */
   @Override
   public void visit(Length node) throws VisitException
   {
      checkSelectorExistance(node.getSelectorName());
   }

   /**
    * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.operand.LowerCase)
    */
   @Override
   public void visit(LowerCase node) throws VisitException
   {
      checkSelectorExistance(node.getSelectorName());
   }

   /**
    * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.operand.NodeDepth)
    */
   @Override
   public void visit(NodeDepth depth) throws VisitException
   {
      checkSelectorExistance(depth.getSelectorName());
   }

   /**
    * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.operand.NodeLocalName)
    */
   @Override
   public void visit(NodeLocalName node) throws VisitException
   {
      checkSelectorExistance(node.getSelectorName());
   }

   /**
    * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.operand.NodeName)
    */
   @Override
   public void visit(NodeName node) throws VisitException
   {
      checkSelectorExistance(node.getSelectorName());
   }

   /**
   * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.constraint.PropertyExistence)
   */
   @Override
   public void visit(PropertyExistence node) throws VisitException
   {
      checkTableAndColumnExistance(node.getSelectorName(), node.getPropertyName(), true);
   }

   /**
    * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.operand.PropertyValue)
    */
   @Override
   public void visit(PropertyValue node) throws VisitException
   {
      checkTableAndColumnExistance(node.getSelectorName(), node.getPropertyName(), true);
   }

   /**
    * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.Query)
    */
   @Override
   public void visit(Query node) throws VisitException
   {
      // Collect the map of columns by alias for this query ...
      this.columnsByAlias.clear();
      for (Column column : node.getColumns())
      {
         if (!column.isFunction())
         {
            // Find the schemata column ...
            Table table = checkSelectorExistance(column.getSelectorName());
            if (table != null)
            {
               Schema.Column tableColumn = table.getColumn(column.getPropertyName());
               if (tableColumn != null)
               {
                  this.columnsByAlias.put(column.getColumnName(), tableColumn);
               }
            }
         }
      }

      super.visit(node);
   }

   /**
    * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.ordering.Ordering)
    */
   @Override
   public void visit(Ordering node) throws VisitException
   {
      //TODO check query ordereable
   }

   /**
    * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.constraint.SameNode)
    */
   @Override
   public void visit(SameNode node) throws VisitException
   {
      checkSelectorExistance(node.getSelectorName());
   }

   /**
    * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.source.join.SameNodeJoinCondition)
    */
   @Override
   public void visit(SameNodeJoinCondition node) throws VisitException
   {
      checkSelectorExistance(node.getSelector1Name());
      checkSelectorExistance(node.getSelector2Name());
      if (node.getSelector1Name().equals(node.getSelector2Name()))
      {
         problems.addException(new InvalidQueryException(node.getSelector1Name() + " is the same as "
            + node.getSelector2Name()));
      }
   }

   /**
    * @see org.xcmis.search.Visitors.AbstractModelVisitor#visit(org.xcmis.search.model.operand.UpperCase)
    */
   @Override
   public void visit(UpperCase node) throws VisitException
   {
      checkSelectorExistance(node.getSelectorName());
   }
}
