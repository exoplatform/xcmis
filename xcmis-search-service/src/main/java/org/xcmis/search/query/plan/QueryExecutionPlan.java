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
package org.xcmis.search.query.plan;

import org.apache.commons.lang.Validate;
import org.xcmis.search.content.Schema.Column;
import org.xcmis.search.model.Limit;
import org.xcmis.search.model.constraint.Constraint;
import org.xcmis.search.model.ordering.Ordering;
import org.xcmis.search.model.source.SelectorName;
import org.xcmis.search.model.source.join.JoinCondition;
import org.xcmis.search.model.source.join.JoinType;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * One step from query execution plan.
 */
public abstract class QueryExecutionPlan
{

   /**
    * Plan properties.
    */
   private final Map<String, Object> properties;

   /** The set of named selectors (e.g., tables) that this node deals with. */
   private Set<SelectorName> selectors;

   private final Type type;

   /**
    */
   public QueryExecutionPlan(Type type)
   {
      this.type = type;
      this.properties = new HashMap<String, Object>();
      this.selectors = new HashSet<SelectorName>();
   }

   /**
    * Add a selector to this plan node. This method does nothing if the supplied selector is null.
    * 
    * @param symbol the symbol of the selector
    */
   public void addSelector(SelectorName symbol)
   {
      if (symbol != null)
      {
         selectors.add(symbol);
      }
   }

   /**
    * Add the selectors to this execution step. This method does nothing for any supplied selector that is null.
    * 
    * @param first the first symbol to be added
    * @param second the second symbol to be added
    */
   public void addSelector(SelectorName first, SelectorName second)
   {
      if (first != null)
      {
         selectors.add(first);
      }
      if (second != null)
      {
         selectors.add(second);
      }
   }

   /**
    * Add the selectors to this execution step. This method does nothing for any supplied selector that is null.
    * 
    * @param names the symbols to be added
    */
   public void addSelectors(Iterable<SelectorName> names)
   {
      for (SelectorName name : names)
      {
         if (name != null)
         {
            selectors.add(name);
         }
      }
   }

   /**
    * Return plan by type
    * @param type
    * @return
    */
   public QueryExecutionPlan findPlanByType(Type type)
   {

      if (this.type.equals(type))
      {
         return this;
      }
      return null;
   }

   /**
    * Get the selectors that are referenced by this execution step.
    * 
    * @return the names of the selectors; never null but possibly empty
    */
   public Set<SelectorName> getSelectors()
   {
      return selectors;
   }

   /**
    * @return the type of the step
    */
   public Type getType()
   {
      return type;
   }

   /**
    * 
    * @return size;
    */
   public int getSize()
   {
      return 1;
   }

   /**
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return type + "[" + properties + "]";
   }

   /**
    * Plan what join two sources. 
    *
    */
   public static class JoinExecutionPlan extends SourceExecutionPlan
   {
      /**
       * Join type.
       */
      private JoinType joinType;

      /**
       * Join algorithm.
       */
      private JoinAlgorithm joinAlgorithm;

      /**
       * Join condition.
       */
      private JoinCondition joinCondition;

      /**
       * Execution step for left source.
       */
      private SourceExecutionPlan leftPlan;

      /**
       * Execution step for right source.
       */
      private SourceExecutionPlan rightPlan;

      /**
       * @param type
       */
      public JoinExecutionPlan()
      {
         super(Type.JOIN);
      }

      /**
       * @see org.xcmis.search.query.plan.QueryExecutionPlan#getSize()
       */
      @Override
      public int getSize()
      {
         return 1 + leftPlan.getSize() + rightPlan.getSize();
      }

      /**
       * @return the joinAlgorithm
       */
      public JoinAlgorithm getJoinAlgorithm()
      {
         return joinAlgorithm;
      }

      /**
       * @return the joinCondition
       */
      public JoinCondition getJoinCondition()
      {
         return joinCondition;
      }

      /**
       * @return the joinType
       */
      public JoinType getJoinType()
      {
         return joinType;
      }

      /**
       * @return the leftPlan
       */
      public SourceExecutionPlan getLeftPlan()
      {
         return leftPlan;
      }

      /**
       * @return the rightPlan
       */
      public SourceExecutionPlan getRightPlan()
      {
         return rightPlan;
      }

      /**
       * @param joinAlgorithm the joinAlgorithm to set
       */
      public void setJoinAlgorithm(JoinAlgorithm joinAlgorithm)
      {
         this.joinAlgorithm = joinAlgorithm;
      }

      /**
       * @param joinCondition the joinCondition to set
       */
      public void setJoinCondition(JoinCondition joinCondition)
      {
         this.joinCondition = joinCondition;
      }

      /**
       * @param joinType the joinType to set
       */
      public void setJoinType(JoinType joinType)
      {
         this.joinType = joinType;
      }

      /**
       * @param leftPlan the leftPlan to set
       */
      public void setLeftPlan(SourceExecutionPlan leftPlan)
      {
         this.leftPlan = leftPlan;
      }

      /**
       * @param rightPlan the rightPlan to set
       */
      public void setRightPlan(SourceExecutionPlan rightPlan)
      {
         this.rightPlan = rightPlan;
      }

   }

   /**
    * Execution step for limit phase. 
    *
    */
   public static class LimitExecutionPlan extends NestedExecutionPlan
   {
      private Limit limit;

      /**
       * @param type
       */
      public LimitExecutionPlan()
      {
         super(Type.LIMIT);

      }

      /**
       * @param type
       */
      public LimitExecutionPlan(QueryExecutionPlan childPlan)
      {
         super(Type.LIMIT, childPlan);

      }

      /**
       * @return the limit
       */
      public Limit getLimit()
      {
         return limit;
      }

      /**
       * @param limit the limit to set
       */
      public void setLimit(Limit limit)
      {
         this.limit = limit;
      }

   }

   public static class NestedExecutionPlan extends QueryExecutionPlan
   {
      private QueryExecutionPlan childPlan;

      /**
       * @param type
       */
      public NestedExecutionPlan(Type type)
      {
         super(type);

      }

      /**
       * @param type
       */
      public NestedExecutionPlan(Type type, QueryExecutionPlan childPlan)
      {
         super(type);
         this.childPlan = childPlan;

      }

      /**
       * @see org.xcmis.search.query.plan.QueryExecutionPlan#getSize()
       */
      @Override
      public int getSize()
      {
         return 1 + childPlan.getSize();
      }

      /**
       * @see org.xcmis.search.query.plan.QueryExecutionPlan#findPlanByType(org.xcmis.search.query.plan.QueryExecutionPlan.Type)
       */
      @Override
      public QueryExecutionPlan findPlanByType(Type type)
      {
         if (getType().equals(type))
         {
            return this;
         }
         return childPlan.findPlanByType(type);
      }

      /**
       * @return the childPlan
       */
      public QueryExecutionPlan getChildPlan()
      {
         return childPlan;
      }

      /**
       * @param childPlan the childPlan to set
       */
      public void setChildPlan(QueryExecutionPlan childPlan)
      {
         this.childPlan = childPlan;
      }
   }

   /**
    * Execution step for project phase. 
    *
    */
   public static class ProjectExecutionPlan extends NestedExecutionPlan
   {
      private List<org.xcmis.search.model.column.Column> columns;

      /**
       * @param type
       */
      public ProjectExecutionPlan()
      {
         super(Type.PROJECT);

      }

      /**
       * @param type
       */
      public ProjectExecutionPlan(QueryExecutionPlan childPlan)
      {
         super(Type.PROJECT, childPlan);

      }

      /**
       * @return the columns
       */
      public List<org.xcmis.search.model.column.Column> getColumns()
      {
         return columns;
      }

      /**
       * @param columns2 the columns to set
       */
      public void setColumns(List<org.xcmis.search.model.column.Column> columns2)
      {
         this.columns = columns2;
      }
   }

   /**
    *  Plan for what accumulate information about source 
    */
   public static class SelectorExecutionPlan extends SourceExecutionPlan
   {
      /**
       * Source alias.
       */
      private SelectorName alias;

      /**
       * Source name
       */
      private SelectorName name;

      /**
       * Desire columns
       */
      private List<Column> columns;

      /**
       * @param type
       */
      public SelectorExecutionPlan()
      {
         super(Type.SELECTOR);
      }

      /**
       * @return the alias
       */
      public SelectorName getAlias()
      {
         return alias;
      }

      /**
       * @return the columns
       */
      public List<Column> getColumns()
      {
         return columns;
      }

      /**
       * @return the name
       */
      public SelectorName getName()
      {
         return name;
      }

      /**
       * @param alias the alias to set
       */
      public void setAlias(SelectorName alias)
      {
         this.alias = alias;
      }

      /**
       * @param columns the columns to set
       */
      public void setColumns(List<Column> columns)
      {
         this.columns = columns;
      }

      /**
       * @param name the name to set
       */
      public void setName(SelectorName name)
      {
         this.name = name;
      }
   }

   /**
    * Execution step for sort phase. 
    *
    */
   public static class SortExecutionPlan extends NestedExecutionPlan
   {
      private List<Ordering> orderings;

      /**
       * @param type
       */
      public SortExecutionPlan()
      {
         super(Type.SORT);

      }

      /**
       * @param type
       */
      public SortExecutionPlan(QueryExecutionPlan childPlan)
      {
         super(Type.SORT, childPlan);

      }

      /**
       * @return the orderings
       */
      public List<Ordering> getOrderings()
      {
         return orderings;
      }

      /**
       * @param orderings the orderings to set
       */
      public void setOrderings(List<Ordering> orderings)
      {
         this.orderings = orderings;
      }

   }

   /**
    * Ancestor for Selector and Join step.
    *
    */
   public static abstract class SourceExecutionPlan extends QueryExecutionPlan
   {

      /**
       * @param type
       */
      public SourceExecutionPlan(Type type)
      {
         super(type);
      }

   }

   /**
    * Execution step for constraint. 
    *
    */
   public static class WhereExecutionPlan extends NestedExecutionPlan
   {
      /**
       * Constraint for step.
       */
      private Constraint constraint;

      /**
       * @param type
       */
      public WhereExecutionPlan()
      {
         super(Type.WHERE);

      }

      /**
       * @param type
       */
      public WhereExecutionPlan(QueryExecutionPlan childPlan)
      {
         super(Type.WHERE, childPlan);

      }

      /**
       * @return the constraint
       */
      public Constraint getConstraint()
      {
         return constraint;
      }

      /**
       * @param constraint the constraint to set
       */
      public void setConstraint(Constraint constraint)
      {
         this.constraint = constraint;
      }
   }

   /**
    * An enumeration dictating the type of plan tree nodes.
    */
   public static enum Type {

      JOIN("Join"), // A node that defines the join type, join criteria, and join strategy

      SELECTOR("Selector"), //A node that defines the 'table' from which the tuples are being obtained

      PROJECT("Project"), //A node that defines the columns returned from the node.

      WHERE("Where"), //A node that selects a filters the tuples by applying a criteria evaluation filter node (WHERE )

      SORT("Sort"), //A node that defines the columns to sort on, the sort direction for each column, and whether to remove duplicates.

      LIMIT("Limit"); //A node that limits the number of tuples returned
      private static final Map<String, Type> TYPE_BY_SYMBOL;
      static
      {
         Map<String, Type> typesBySymbol = new HashMap<String, Type>();
         for (Type type : Type.values())
         {
            typesBySymbol.put(type.getSymbol().toUpperCase(), type);
         }
         TYPE_BY_SYMBOL = Collections.unmodifiableMap(typesBySymbol);
      }

      private final String symbol;

      private Type(String symbol)
      {
         this.symbol = symbol;
      }

      /**
       * Get the symbol representation of this node type.
       * 
       * @return the symbol; never null and never empty
       */
      public String getSymbol()
      {
         return symbol;
      }

      /**
       * {@inheritDoc}
       * 
       * @see java.lang.Enum#toString()
       */
      @Override
      public String toString()
      {
         return symbol;
      }

      /**
       * Attempt to find the Type given a symbol. The matching is done independent of case.
       * 
       * @param symbol the symbol
       * @return the Type having the supplied symbol, or null if there is no Type with the supplied symbol
       * @throws IllegalArgumentException if the symbol is null
       */
      public static Type forSymbol(String symbol)
      {
         Validate.notNull(symbol, "The symbol argument may not be null");
         return TYPE_BY_SYMBOL.get(symbol.toUpperCase().trim());
      }
   }
}
