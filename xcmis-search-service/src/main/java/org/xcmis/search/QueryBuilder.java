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
package org.xcmis.search;

import org.apache.commons.lang.Validate;

import org.xcmis.search.VisitException;
import org.xcmis.search.Visitors;
import org.xcmis.search.model.Limit;
import org.xcmis.search.model.Query;
import org.xcmis.search.model.column.Column;
import org.xcmis.search.model.constraint.And;
import org.xcmis.search.model.constraint.ChildNode;
import org.xcmis.search.model.constraint.Comparison;
import org.xcmis.search.model.constraint.Constraint;
import org.xcmis.search.model.constraint.DescendantNode;
import org.xcmis.search.model.constraint.FullTextSearch;
import org.xcmis.search.model.constraint.Not;
import org.xcmis.search.model.constraint.Operator;
import org.xcmis.search.model.constraint.Or;
import org.xcmis.search.model.constraint.PropertyExistence;
import org.xcmis.search.model.constraint.SameNode;
import org.xcmis.search.model.operand.BindVariableName;
import org.xcmis.search.model.operand.DynamicOperand;
import org.xcmis.search.model.operand.FullTextSearchScore;
import org.xcmis.search.model.operand.Length;
import org.xcmis.search.model.operand.Literal;
import org.xcmis.search.model.operand.LowerCase;
import org.xcmis.search.model.operand.NodeDepth;
import org.xcmis.search.model.operand.NodeLocalName;
import org.xcmis.search.model.operand.NodeName;
import org.xcmis.search.model.operand.PropertyValue;
import org.xcmis.search.model.operand.StaticOperand;
import org.xcmis.search.model.operand.UpperCase;
import org.xcmis.search.model.ordering.Order;
import org.xcmis.search.model.ordering.Ordering;
import org.xcmis.search.model.source.Join;
import org.xcmis.search.model.source.Selector;
import org.xcmis.search.model.source.SelectorName;
import org.xcmis.search.model.source.Source;
import org.xcmis.search.model.source.join.ChildNodeJoinCondition;
import org.xcmis.search.model.source.join.DescendantNodeJoinCondition;
import org.xcmis.search.model.source.join.EquiJoinCondition;
import org.xcmis.search.model.source.join.JoinCondition;
import org.xcmis.search.model.source.join.JoinType;
import org.xcmis.search.model.source.join.SameNodeJoinCondition;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A component that can be used to programmatically create {@link QueryCommand}
 * objects. Simply call methods to build the selector clause, from clause, join
 * criteria, where criteria, limits, and ordering, and then {@link #query()
 * obtain the query}. This builder should be adequate for most queries; however,
 * any query that cannot be expressed by this builder can always be constructed
 * by directly creating the Abstract Query Model classes.
 * <p>
 * This builder is stateful and therefore should only be used by one thread at a
 * time. However, once a query has been built, the builder can be
 * {@link #clear() cleared} and used to create another query.
 * </p>
 * <p>
 * The order in which the methods are called are (for the most part) important.
 * Simply call the methods in the same order that would be most natural in a
 * normal SQL query. For example, the following code creates a Query object that
 * is equivalent to " <code>SELECT * FROM table</code>":
 * 
 * <pre>
 * QueryCommand query = builder.selectStar().from(&quot;table&quot;).query();
 * </pre>
 * 
 * </p>
 * <p>
 * Here are a few other examples:
 * <table border="1" cellspacing="0" cellpadding="3" summary="">
 * <tr>
 * <th>SQL Statement</th>
 * <th>QueryBuilder code</th>
 * </tr>
 * <tr>
 * <td>
 * 
 * <pre>
 * SELECT * FROM table1
 *    INNER JOIN table2
 *            ON table2.c0 = table1.c0
 * </pre>
 * 
 * </td>
 * <td>
 * 
 * <pre>
 * query = builder.selectStar().from(&quot;table1&quot;).join(&quot;table2&quot;).on(&quot;table2.c0=table1.c0&quot;).query();
 * </pre>
 * 
 * </td>
 * </tr>
 * <tr>
 * <td>
 * 
 * <pre>
 * SELECT * FROM table1 AS t1
 *    INNER JOIN table2 AS t2
 *            ON t1.c0 = t2.c0
 * </pre>
 * 
 * </td>
 * <td>
 * 
 * <pre>
 * query = builder.selectStar().from(&quot;table1 AS t1&quot;).join(&quot;table2 AS t2&quot;).on(&quot;t1.c0=t2.c0&quot;).query();
 * </pre>
 * 
 * </td>
 * </tr>
 * <tr>
 * <td>
 * 
 * <pre>
 * SELECT * FROM table1 AS t1
 *    INNER JOIN table2 AS t2
 *            ON t1.c0 = t2.c0
 *    INNER JOIN table3 AS t3
 *            ON t1.c1 = t3.c1
 * </pre>
 * 
 * </td>
 * <td>
 * 
 * <pre>
 * query = builder.selectStar()
 *                .from(&quot;table1 AS t1&quot;)
 *                .innerJoin(&quot;table2 AS t2&quot;)
 *                .on(&quot;t1.c0=t2.c0&quot;)
 *                .innerJoin(&quot;table3 AS t3&quot;)
 *                .on(&quot;t1.c1=t3.c1&quot;)
 *                .query();
 * </pre>
 * 
 * </td>
 * </tr>
 * <tr>
 * <td>
 * 
 * <pre>
 * SELECT * FROM table1
 * UNION
 * SELECT * FROM table2
 * </pre>
 * 
 * </td>
 * <td>
 * 
 * <pre>
 * query = builder.selectStar().from(&quot;table1&quot;).union().selectStar().from(&quot;table2&quot;).query();
 * </pre>
 * 
 * </td>
 * </tr>
 * <tr>
 * <td>
 * 
 * <pre>
 * SELECT t1.c1,t1.c2,t2.c3 FROM table1 AS t1
 *    INNER JOIN table2 AS t2
 *            ON t1.c0 = t2.c0
 * UNION ALL
 * SELECT t3.c1,t3.c2,t4.c3 FROM table3 AS t3
 *    INNER JOIN table4 AS t4
 *            ON t3.c0 = t4.c0
 * </pre>
 * 
 * </td>
 * <td>
 * 
 * <pre>
 * query = builder.select(&quot;t1.c1&quot;,&quot;t1.c2&quot;,&quot;t2.c3&quot;,)
 *                .from(&quot;table1 AS t1&quot;)
 *                .innerJoin(&quot;table2 AS t2&quot;)
 *                .on(&quot;t1.c0=t2.c0&quot;)
 *                .union()
 *                .select(&quot;t3.c1&quot;,&quot;t3.c2&quot;,&quot;t4.c3&quot;,)
 *                .from(&quot;table3 AS t3&quot;)
 *                .innerJoin(&quot;table4 AS t4&quot;)
 *                .on(&quot;t3.c0=t4.c0&quot;)
 *                .query();
 * </pre>
 * 
 * </td>
 * </tr>
 * </table>
 * </pre>
 */

public class QueryBuilder
{

   protected final CastSystem castSystem;

   protected Source source = new Selector(new SelectorName("__not:defined__"));;

   protected Constraint constraint;

   protected List<Column> columns = new LinkedList<Column>();

   protected List<Ordering> orderings = new LinkedList<Ordering>();

   protected Limit limit = Limit.NONE;

   protected boolean distinct;

   protected Query firstQuery;

   protected boolean firstQueryAll;

   /**
    * Create a new builder that uses the supplied execution context.
    * 
    * @param context
    *           the execution context
    * @throws IllegalArgumentException
    *            if the context is null
    */
   public QueryBuilder(CastSystem castSystem)
   {
      Validate.notNull(castSystem, "The context argument may not be null");
      this.castSystem = castSystem;
   }

   /**
    * Clear this builder completely to start building a new query.
    * 
    * @return this builder object, for convenience in method chaining
    */
   public QueryBuilder clear()
   {
      return clear(true);
   }

   /**
    * Utility method that does all the work of the clear, but with a flag that
    * defines whether to clear the first query. This method is used by
    * {@link #clear()} as well as the {@link #union() many} {@link #intersect()
    * set} {@link #except() operations}.
    * 
    * @param clearFirstQuery
    *           true if the first query should be cleared, or false if the first
    *           query should be retained
    * @return this builder object, for convenience in method chaining
    */
   protected QueryBuilder clear(boolean clearFirstQuery)
   {
      source = new Selector(new SelectorName("__not:defined__"));
      constraint = null;
      columns = new LinkedList<Column>();
      orderings = new LinkedList<Ordering>();
      limit = Limit.NONE;
      distinct = false;
      if (clearFirstQuery)
      {
         this.firstQuery = null;
      }
      return this;
   }

   /**
    * Convenience method that creates a selector name object using the supplied
    * string.
    * 
    * @param name
    *           the name of the selector; may not be null
    * @return the selector name; never null
    */
   protected SelectorName selector(String name)
   {
      return new SelectorName(name.trim());
   }

   /**
    * Convenience method that creates a {@link Selector} object given a string
    * that contains the selector name and optionally an alias. The format of the
    * string parameter is <code>name [AS alias]</code>. Leading and trailing
    * whitespace are trimmed.
    * 
    * @param nameWithOptionalAlias
    *           the name and optional alias; may not be null
    * @return the named selector object; never null
    */
   protected Selector namedSelector(String nameWithOptionalAlias)
   {
      String[] parts = nameWithOptionalAlias.split("\\sAS\\s");
      if (parts.length == 2)
      {
         return new Selector(selector(parts[0]), selector(parts[1]));
      }
      return new Selector(selector(parts[0]));
   }

   /**
    * Create a {@link Column} given the supplied expression. The expression has
    * the form "<code>[tableName.]columnName</code>", where "
    * <code>tableName</code>" must be a valid table name or alias. If the table
    * name/alias is not specified, then there is expected to be a single FROM
    * clause with a single named selector.
    * 
    * @param nameExpression
    *           the expression specifying the columm name and (optionally) the
    *           table's name or alias; may not be null
    * @return the column; never null
    * @throws IllegalArgumentException
    *            if the table's name/alias is not specified, but the query has
    *            more than one named source
    */
   protected Column column(String nameExpression)
   {
      String[] parts = nameExpression.split("(?<!\\\\)\\."); // a . not preceded
                                                             // by an escaping
                                                             // slash
      for (int i = 0; i != parts.length; ++i)
      {
         parts[i] = parts[i].trim();
      }
      SelectorName name = null;
      String propertyName = null;
      String columnName = null;
      if (parts.length == 2)
      {
         name = selector(parts[0]);
         propertyName = parts[1];
         columnName = parts[1];
      }
      else
      {
         if (source == null)
         {
            name = selector(parts[0]);
            propertyName = parts[0];
            columnName = parts[0];
         }
         else if (source instanceof Selector)
         {
            Selector selector = (Selector)source;
            name = selector.hasAlias() ? selector.getAlias() : selector.getName();
            propertyName = parts[0];
            columnName = parts[0];
         }

         else
         {
            throw new IllegalArgumentException("Column parts " + parts[0] + " must be scoped");
         }
      }
      return new Column(name, propertyName, columnName);
   }

   /**
    * Select all of the single-valued columns.
    * 
    * @return this builder object, for convenience in method chaining
    */
   public QueryBuilder selectStar()
   {
      columns.clear();
      return this;
   }

   /**
    * Add to the select clause the columns with the supplied names. Each column
    * name has the form " <code>[tableName.]columnName</code>", where "
    * <code>tableName</code>" must be a valid table name or alias. If the table
    * name/alias is not specified, then there is expected to be a single FROM
    * clause with a single named selector.
    * 
    * @param columnNames
    *           the column expressions; may not be null
    * @return this builder object, for convenience in method chaining
    * @throws IllegalArgumentException
    *            if the table's name/alias is not specified, but the query has
    *            more than one named source
    */
   public QueryBuilder select(String... columnNames)
   {
      for (String expression : columnNames)
      {
         columns.add(column(expression));
      }
      return this;
   }

   /**
    * Select all of the distinct values from the single-valued columns.
    * 
    * @return this builder object, for convenience in method chaining
    */
   public QueryBuilder selectDistinctStar()
   {
      distinct = true;
      return selectStar();
   }

   /**
    * Select the distinct values from the columns with the supplied names. Each
    * column name has the form " <code>[tableName.]columnName</code>", where "
    * <code>tableName</code>" must be a valid table name or alias. If the table
    * name/alias is not specified, then there is expected to be a single FROM
    * clause with a single named selector.
    * 
    * @param columnNames
    *           the column expressions; may not be null
    * @return this builder object, for convenience in method chaining
    * @throws IllegalArgumentException
    *            if the table's name/alias is not specified, but the query has
    *            more than one named source
    */
   public QueryBuilder selectDistinct(String... columnNames)
   {
      distinct = true;
      return select(columnNames);
   }

   /**
    * Specify the name of the table from which tuples should be selected. The
    * supplied string is of the form " <code>tableName [AS alias]</code>".
    * 
    * @param tableNameWithOptionalAlias
    *           the name of the table, optionally including the alias
    * @return this builder object, for convenience in method chaining
    */
   public QueryBuilder from(String tableNameWithOptionalAlias)
   {
      Selector selector = namedSelector(tableNameWithOptionalAlias);
      SelectorName oldName = this.source instanceof Selector ? ((Selector)source).getName() : null;
      // Go through the columns and change the selector name to use the new
      // alias ...
      for (int i = 0; i != columns.size(); ++i)
      {
         Column old = columns.get(i);
         if (old.getSelectorName().equals(oldName))
         {
            columns.set(i, new Column(selector.getAliasOrName(), old.getPropertyName(), old.getColumnName()));
         }
      }
      this.source = selector;
      return this;
   }

   /**
    * Begin the WHERE clause for this query by obtaining the constraint builder.
    * When completed, be sure to call {@link ConstraintBuilder#end() end()} on
    * the resulting constraint builder, or else the constraint will not be
    * applied to the current query.
    * 
    * @return the constraint builder that can be used to specify the criteria;
    *         never null
    */
   public ConstraintBuilder where()
   {
      return new ConstraintBuilder(null);
   }

   /**
    * Perform an inner join between the already defined source with the supplied
    * table. The supplied string is of the form "
    * <code>tableName [AS alias]</code>".
    * 
    * @param tableName
    *           the name of the table, optionally including the alias
    * @return the component that must be used to complete the join
    *         specification; never null
    */
   public JoinClause join(String tableName)
   {
      return innerJoin(tableName);
   }

   /**
    * Perform an inner join between the already defined source with the supplied
    * table. The supplied string is of the form "
    * <code>tableName [AS alias]</code>".
    * 
    * @param tableName
    *           the name of the table, optionally including the alias
    * @return the component that must be used to complete the join
    *         specification; never null
    */
   public JoinClause innerJoin(String tableName)
   {
      // Expect there to be a source already ...
      return new JoinClause(namedSelector(tableName), JoinType.INNER);
   }

   /**
    * Perform a left outer join between the already defined source with the
    * supplied table. The supplied string is of the form "
    * <code>tableName [AS alias]</code>".
    * 
    * @param tableName
    *           the name of the table, optionally including the alias
    * @return the component that must be used to complete the join
    *         specification; never null
    */
   public JoinClause leftOuterJoin(String tableName)
   {
      // Expect there to be a source already ...
      return new JoinClause(namedSelector(tableName), JoinType.LEFT_OUTER);
   }

   /**
    * Perform a right outer join between the already defined source with the
    * supplied table. The supplied string is of the form "
    * <code>tableName [AS alias]</code>".
    * 
    * @param tableName
    *           the name of the table, optionally including the alias
    * @return the component that must be used to complete the join
    *         specification; never null
    */
   public JoinClause rightOuterJoin(String tableName)
   {
      // Expect there to be a source already ...
      return new JoinClause(namedSelector(tableName), JoinType.RIGHT_OUTER);
   }

   /**
    * Specify the maximum number of rows that are to be returned in the results.
    * By default there is no limit.
    * 
    * @param rowLimit
    *           the maximum number of rows
    * @return this builder object, for convenience in method chaining
    * @throws IllegalArgumentException
    *            if the row limit is not a positive integer
    */
   public QueryBuilder limit(int rowLimit)
   {
      this.limit.withRowLimit(rowLimit);
      return this;
   }

   /**
    * Specify the number of rows that results are to skip. The default offset is
    * '0'.
    * 
    * @param offset
    *           the number of rows before the results are to begin
    * @return this builder object, for convenience in method chaining
    * @throws IllegalArgumentException
    *            if the row limit is a negative integer
    */
   public QueryBuilder offset(int offset)
   {
      this.limit.withOffset(offset);
      return this;
   }

   /**
    * Obtain a builder that will create the order-by clause (with one or more
    * {@link Ordering} statements) for the query. This method need be called
    * only once to build the order-by clause, but can be called multiple times
    * (it merely adds additional {@link Ordering} statements).
    * 
    * @return the order-by builder; never null
    */
   public OrderByBuilder orderBy()
   {
      return new OrderByBuilder();
   }

   /**
    * Return a {@link QueryCommand} representing the currently-built query.
    * 
    * @return the resulting query command; never null
    * @see #clear()
    */
   public Query query()
   {
      Query result = new Query(source, constraint, orderings, columns, limit);
      if (this.firstQuery != null)
      {
      }
      return result;
   }

   public interface OrderByOperandBuilder
   {
      /**
       * Adds to the order-by clause by using the length of the value for the
       * given table and property.
       * 
       * @param table
       *           the name of the table; may not be null and must refer to a
       *           valid name or alias of a table appearing in the FROM clause
       * @param property
       *           the name of the property; may not be null and must refer to a
       *           valid property name
       * @return the interface for completing the order-by specification; never
       *         null
       */
      public OrderByBuilder length(String table,
                                      String property);

      /**
       * Adds to the order-by clause by using the value for the given table and
       * property.
       * 
       * @param table
       *           the name of the table; may not be null and must refer to a
       *           valid name or alias of a table appearing in the FROM clause
       * @param property
       *           the name of the property; may not be null and must refer to a
       *           valid property name
       * @return the interface for completing the order-by specification; never
       *         null
       */
      public OrderByBuilder propertyValue(String table,
                                             String property);

      /**
       * Adds to the order-by clause by using the full-text search score for the
       * given table.
       * 
       * @param table
       *           the name of the table; may not be null and must refer to a
       *           valid name or alias of a table appearing in the FROM clause
       * @return the interface for completing the order-by specification; never
       *         null
       */
      public OrderByBuilder fullTextSearchScore(String table);

      /**
       * Adds to the order-by clause by using the depth of the node given by the
       * named table.
       * 
       * @param table
       *           the name of the table; may not be null and must refer to a
       *           valid name or alias of a table appearing in the FROM clause
       * @return the interface for completing the order-by specification; never
       *         null
       */
      public OrderByBuilder depth(String table);

      /**
       * Adds to the order-by clause by using the local name of the node given
       * by the named table.
       * 
       * @param table
       *           the name of the table; may not be null and must refer to a
       *           valid name or alias of a table appearing in the FROM clause
       * @return the interface for completing the order-by specification; never
       *         null
       */
      public OrderByBuilder nodeLocalName(String table);

      /**
       * Adds to the order-by clause by using the node name (including
       * namespace) of the node given by the named table.
       * 
       * @param table
       *           the name of the table; may not be null and must refer to a
       *           valid name or alias of a table appearing in the FROM clause
       * @return the interface for completing the order-by specification; never
       *         null
       */
      public OrderByBuilder nodeName(String table);

      /**
       * Adds to the order-by clause by using the uppercase form of the next
       * operand.
       * 
       * @return the interface for completing the order-by specification; never
       *         null
       */
      public OrderByOperandBuilder upperCaseOf();

      /**
       * Adds to the order-by clause by using the lowercase form of the next
       * operand.
       * 
       * @return the interface for completing the order-by specification; never
       *         null
       */
      public OrderByOperandBuilder lowerCaseOf();
   }

   /**
    * The component used to build the order-by clause. When the clause is
    * completed, {@link #end()} should be called to return to the
    * {@link QueryBuilder} instance.
    */
   public class OrderByBuilder
   {

      protected OrderByBuilder()
      {
      }

      /**
       * Begin specifying an order-by specification using
       * {@link Order#ASCENDING ascending order}.
       * 
       * @return the interface for specifying the operand that is to be ordered;
       *         never null
       */
      public OrderByOperandBuilder ascending()
      {
         return new SingleOrderByOperandBuilder(this, Order.ASCENDING);
      }

      /**
       * Begin specifying an order-by specification using
       * {@link Order#DESCENDING descending order}.
       * 
       * @return the interface for specifying the operand that is to be ordered;
       *         never null
       */
      public OrderByOperandBuilder descending()
      {
         return new SingleOrderByOperandBuilder(this, Order.DESCENDING);
      }

      /**
       * An optional convenience method that returns this builder, but which
       * makes the code using this builder more readable.
       * 
       * @return this builder; never null
       */
      public OrderByBuilder then()
      {
         return this;
      }

      /**
       * Complete the order-by clause and return the QueryBuilder instance.
       * 
       * @return the query builder instance; never null
       */
      public QueryBuilder end()
      {
         return QueryBuilder.this;
      }
   }

   protected class SingleOrderByOperandBuilder implements OrderByOperandBuilder
   {
      private final Order order;

      private final OrderByBuilder builder;

      protected SingleOrderByOperandBuilder(OrderByBuilder builder,
                                               Order order)
      {
         this.order = order;
         this.builder = builder;
      }

      protected OrderByBuilder addOrdering(DynamicOperand operand)
      {
         Ordering ordering = new Ordering(operand, order);
         QueryBuilder.this.orderings.add(ordering);
         return builder;
      }

      /**
       * {@inheritDoc}
       * 
       * @see org.modeshape.graph.query.QueryBuilder.OrderByOperandBuilder#propertyValue(java.lang.String,
       *      java.lang.String)
       */
      public OrderByBuilder propertyValue(String table,
                                             String property)
      {
         return addOrdering(new PropertyValue(selector(table), property));
      }

      /**
       * {@inheritDoc}
       * 
       * @see org.modeshape.graph.query.QueryBuilder.OrderByOperandBuilder#length(java.lang.String,
       *      java.lang.String)
       */
      public OrderByBuilder length(String table,
                                      String property)
      {
         return addOrdering(new Length(new PropertyValue(selector(table), property)));
      }

      /**
       * {@inheritDoc}
       * 
       * @see org.modeshape.graph.query.QueryBuilder.OrderByOperandBuilder#fullTextSearchScore(java.lang.String)
       */
      public OrderByBuilder fullTextSearchScore(String table)
      {
         return addOrdering(new FullTextSearchScore(selector(table)));
      }

      /**
       * {@inheritDoc}
       * 
       * @see org.modeshape.graph.query.QueryBuilder.OrderByOperandBuilder#depth(java.lang.String)
       */
      public OrderByBuilder depth(String table)
      {
         return addOrdering(new NodeDepth(selector(table)));
      }

      /**
       * {@inheritDoc}
       * 
       * @see org.modeshape.graph.query.QueryBuilder.OrderByOperandBuilder#nodeName(java.lang.String)
       */
      public OrderByBuilder nodeName(String table)
      {
         return addOrdering(new NodeName(selector(table)));
      }

      /**
       * {@inheritDoc}
       * 
       * @see org.modeshape.graph.query.QueryBuilder.OrderByOperandBuilder#nodeLocalName(java.lang.String)
       */
      public OrderByBuilder nodeLocalName(String table)
      {
         return addOrdering(new NodeLocalName(selector(table)));
      }

      /**
       * {@inheritDoc}
       * 
       * @see org.modeshape.graph.query.QueryBuilder.OrderByOperandBuilder#lowerCaseOf()
       */
      public OrderByOperandBuilder lowerCaseOf()
      {
         return new SingleOrderByOperandBuilder(builder, order)
         {
            /**
             * {@inheritDoc}
             * 
             * @see org.modeshape.graph.query.QueryBuilder.SingleOrderByOperandBuilder#addOrdering(org.modeshape.graph.query.model.DynamicOperand)
             */
            @Override
            protected OrderByBuilder addOrdering(DynamicOperand operand)
            {
               return super.addOrdering(new LowerCase(operand));
            }
         };
      }

      /**
       * {@inheritDoc}
       * 
       * @see org.modeshape.graph.query.QueryBuilder.OrderByOperandBuilder#upperCaseOf()
       */
      public OrderByOperandBuilder upperCaseOf()
      {
         return new SingleOrderByOperandBuilder(builder, order)
         {
            /**
             * {@inheritDoc}
             * 
             * @see org.modeshape.graph.query.QueryBuilder.SingleOrderByOperandBuilder#addOrdering(org.modeshape.graph.query.model.DynamicOperand)
             */
            @Override
            protected OrderByBuilder addOrdering(DynamicOperand operand)
            {
               return super.addOrdering(new UpperCase(operand));
            }
         };
      }
   }

   /**
    * Class used to specify a join clause of a query.
    * 
    * @see QueryBuilder#join(String)
    * @see QueryBuilder#innerJoin(String)
    * @see QueryBuilder#leftOuterJoin(String)
    * @see QueryBuilder#rightOuterJoin(String)
    * @see QueryBuilder#fullOuterJoin(String)
    */
   public class JoinClause
   {
      private final Selector rightSource;

      private final JoinType type;

      protected JoinClause(Selector rightTable,
                              JoinType type)
      {
         this.rightSource = rightTable;
         this.type = type;
      }

      /**
       * Walk the current source or the 'rightSource' to find the named selector
       * with the supplied name or alias
       * 
       * @param tableName
       *           the table name
       * @return the selector name matching the supplied table name; never null
       * @throws IllegalArgumentException
       *            if the table name could not be resolved
       */
      protected SelectorName nameOf(String tableName)
      {
         final SelectorName name = new SelectorName(tableName);
         // Look at the right source ...
         if (rightSource.getAliasOrName().equals(name))
            return name;
         // Look through the left source ...
         final AtomicBoolean notFound = new AtomicBoolean(true);
         try
         {
            Visitors.visit(source, new Visitors.AbstractModelVisitor()
            {

               @Override
               public void visit(Selector selector)
               {
                  if (notFound.get() && selector.getAliasOrName().equals(name))
                     notFound.set(false);
               }
            });
         }
         catch (VisitException e)
         {
            // ignore
         }
         if (notFound.get())
         {
            throw new IllegalArgumentException("Expected \"" + tableName + "\" to be a valid table name or alias");
         }
         return name;
      }

      /**
       * Define the join as using an equi-join criteria by specifying the
       * expression equating two columns. Each column reference must be
       * qualified with the appropriate table name or alias.
       * 
       * @param columnEqualExpression
       *           the equality expression between the two tables; may not be
       *           null
       * @return the query builder instance, for method chaining purposes
       * @throws IllegalArgumentException
       *            if the supplied expression is not an equality expression
       */
      public QueryBuilder on(String columnEqualExpression)
      {
         String[] parts = columnEqualExpression.split("=");
         if (parts.length != 2)
         {
            throw new IllegalArgumentException("Expected equality expression for columns, but found \""
                                                   + columnEqualExpression + "\"");
         }
         return createJoin(new EquiJoinCondition(column(parts[0]), column(parts[1])));
      }

      /**
       * Define the join criteria to require the two tables represent the same
       * node. The supplied tables must be a valid name or alias.
       * 
       * @param table1
       *           the name or alias of the first table
       * @param table2
       *           the name or alias of the second table
       * @return the query builder instance, for method chaining purposes
       */
      public QueryBuilder onSameNode(String table1,
                                        String table2)
      {
         return createJoin(new SameNodeJoinCondition(nameOf(table1), nameOf(table2)));
      }

      /**
       * Define the join criteria to require the node in one table is a
       * descendant of the node in another table. The supplied tables must be a
       * valid name or alias.
       * 
       * @param ancestorTable
       *           the name or alias of the table containing the ancestor node
       * @param descendantTable
       *           the name or alias of the table containing the descendant node
       * @return the query builder instance, for method chaining purposes
       */
      public QueryBuilder onDescendant(String ancestorTable,
                                          String descendantTable)
      {
         return createJoin(new DescendantNodeJoinCondition(nameOf(ancestorTable), nameOf(descendantTable)));
      }

      /**
       * Define the join criteria to require the node in one table is a child of
       * the node in another table. The supplied tables must be a valid name or
       * alias.
       * 
       * @param parentTable
       *           the name or alias of the table containing the parent node
       * @param childTable
       *           the name or alias of the table containing the child node
       * @return the query builder instance, for method chaining purposes
       */
      public QueryBuilder onChildNode(String parentTable,
                                         String childTable)
      {
         return createJoin(new ChildNodeJoinCondition(nameOf(parentTable), nameOf(childTable)));
      }

      protected QueryBuilder createJoin(JoinCondition condition)
      {

         // Otherwise, just create using usual precedence ...
         source = new Join(source, type, rightSource, condition);

         return QueryBuilder.this;
      }
   }

   /**
    * Interface that defines a dynamic operand portion of a criteria.
    */
   public interface DynamicOperandBuilder
   {
      /**
       * Constrains the nodes in the the supplied table such that they must have
       * a property value whose length matches the criteria.
       * 
       * @param table
       *           the name of the table; may not be null and must refer to a
       *           valid name or alias of a table appearing in the FROM clause
       * @param property
       *           the name of the property; may not be null and must refer to a
       *           valid property name
       * @return the interface for completing the value portion of the criteria
       *         specification; never null
       */
      public ComparisonBuilder length(String table,
                                         String property);

      /**
       * Constrains the nodes in the the supplied table such that they must have
       * a matching value for the named property.
       * 
       * @param table
       *           the name of the table; may not be null and must refer to a
       *           valid name or alias of a table appearing in the FROM clause
       * @param property
       *           the name of the property; may not be null and must refer to a
       *           valid property name
       * @return the interface for completing the value portion of the criteria
       *         specification; never null
       */
      public ComparisonBuilder propertyValue(String table,
                                                String property);

      /**
       * Constrains the nodes in the the supplied table such that they must
       * satisfy the supplied full-text search on the nodes' property values.
       * 
       * @param table
       *           the name of the table; may not be null and must refer to a
       *           valid name or alias of a table appearing in the FROM clause
       * @return the interface for completing the value portion of the criteria
       *         specification; never null
       */
      public ComparisonBuilder fullTextSearchScore(String table);

      /**
       * Constrains the nodes in the the supplied table based upon criteria on
       * the node's depth.
       * 
       * @param table
       *           the name of the table; may not be null and must refer to a
       *           valid name or alias of a table appearing in the FROM clause
       * @return the interface for completing the value portion of the criteria
       *         specification; never null
       */
      public ComparisonBuilder depth(String table);

      /**
       * Constrains the nodes in the the supplied table based upon criteria on
       * the node's local name.
       * 
       * @param table
       *           the name of the table; may not be null and must refer to a
       *           valid name or alias of a table appearing in the FROM clause
       * @return the interface for completing the value portion of the criteria
       *         specification; never null
       */
      public ComparisonBuilder nodeLocalName(String table);

      /**
       * Constrains the nodes in the the supplied table based upon criteria on
       * the node's name.
       * 
       * @param table
       *           the name of the table; may not be null and must refer to a
       *           valid name or alias of a table appearing in the FROM clause
       * @return the interface for completing the value portion of the criteria
       *         specification; never null
       */
      public ComparisonBuilder nodeName(String table);

      /**
       * Begin a constraint against the uppercase form of a dynamic operand.
       * 
       * @return the interface for completing the criteria specification; never
       *         null
       */
      public DynamicOperandBuilder upperCaseOf();

      /**
       * Begin a constraint against the lowercase form of a dynamic operand.
       * 
       * @return the interface for completing the criteria specification; never
       *         null
       */
      public DynamicOperandBuilder lowerCaseOf();
   }

   public class ConstraintBuilder implements DynamicOperandBuilder
   {
      private final ConstraintBuilder parent;

      /** Used for the current operations */
      private Constraint constraint;

      /** Set when a logical criteria is started */
      private Constraint left;

      private boolean and;

      private boolean negateConstraint;

      protected ConstraintBuilder(ConstraintBuilder parent)
      {
         this.parent = parent;
      }

      /**
       * Complete this constraint specification.
       * 
       * @return the query builder, for method chaining purposes
       */
      public QueryBuilder end()
      {
         buildLogicalConstraint();
         QueryBuilder.this.constraint = constraint;
         return QueryBuilder.this;
      }

      /**
       * Simulate the use of an open parenthesis in the constraint. The
       * resulting builder should be used to define the constraint within the
       * parenthesis, and should always be terminated with a
       * {@link #closeParen()}.
       * 
       * @return the constraint builder that should be used to define the
       *         portion of the constraint within the parenthesis; never null
       * @see #closeParen()
       */
      public ConstraintBuilder openParen()
      {
         return new ConstraintBuilder(this);
      }

      /**
       * Complete the specification of a constraint clause, and return the
       * builder for the parent constraint clause.
       * 
       * @return the constraint builder that was used to create this
       *         parenthetical constraint clause builder; never null
       * @throws IllegalStateException
       *            if there was not an {@link #openParen() open parenthesis} to
       *            close
       */
      public ConstraintBuilder closeParen()
      {
         Validate.notNull(parent, "Unexpected parent == null");
         buildLogicalConstraint();
         return parent.setConstraint(constraint);
      }

      /**
       * Signal that the previous constraint clause be AND-ed together with
       * another constraint clause that will be defined immediately after this
       * method call.
       * 
       * @return the constraint builder for the remaining constraint clause;
       *         never null
       */
      public ConstraintBuilder and()
      {
         buildLogicalConstraint();
         left = constraint;
         constraint = null;
         and = true;
         return this;
      }

      /**
       * Signal that the previous constraint clause be OR-ed together with
       * another constraint clause that will be defined immediately after this
       * method call.
       * 
       * @return the constraint builder for the remaining constraint clause;
       *         never null
       */
      public ConstraintBuilder or()
      {
         buildLogicalConstraint();
         left = constraint;
         constraint = null;
         and = false;
         return this;
      }

      /**
       * Signal that the next constraint clause (defined immediately after this
       * method) should be negated.
       * 
       * @return the constraint builder for the constraint clause that is to be
       *         negated; never null
       */
      public ConstraintBuilder not()
      {
         negateConstraint = true;
         return this;
      }

      protected ConstraintBuilder buildLogicalConstraint()
      {
         if (negateConstraint && constraint != null)
         {
            constraint = new Not(constraint);
            negateConstraint = false;
         }
         if (left != null && constraint != null)
         {
            if (and)
            {
               // If the left constraint is an OR, we need to rearrange things
               // since AND is higher precedence ...
               if (left instanceof Or)
               {
                  Or previous = (Or)left;
                  constraint = new Or(previous.getLeft(), new And(previous.getRight(), constraint));
               }
               else
               {
                  constraint = new And(left, constraint);
               }
            }
            else
            {
               constraint = new Or(left, constraint);
            }
            left = null;
         }
         return this;
      }

      /**
       * Define a constraint clause that the node within the named table is the
       * same node as that appearing at the supplied path.
       * 
       * @param table
       *           the name of the table; may not be null and must refer to a
       *           valid name or alias of a table appearing in the FROM clause
       * @param asNodeAtPath
       *           the path to the node
       * @return the constraint builder that was used to create this clause;
       *         never null
       */
      public ConstraintBuilder isSameNode(String table,
                                             String asNodeAtPath)
      {
         return setConstraint(new SameNode(selector(table), asNodeAtPath));
      }

      /**
       * Define a constraint clause that the node within the named table is the
       * child of the node at the supplied path.
       * 
       * @param childTable
       *           the name of the table; may not be null and must refer to a
       *           valid name or alias of a table appearing in the FROM clause
       * @param parentPath
       *           the path to the parent node
       * @return the constraint builder that was used to create this clause;
       *         never null
       */
      public ConstraintBuilder isChild(String childTable,
                                          String parentPath)
      {
         return setConstraint(new ChildNode(selector(childTable), parentPath));
      }

      /**
       * Define a constraint clause that the node within the named table is a
       * descendant of the node at the supplied path.
       * 
       * @param descendantTable
       *           the name of the table; may not be null and must refer to a
       *           valid name or alias of a table appearing in the FROM clause
       * @param ancestorPath
       *           the path to the ancestor node
       * @return the constraint builder that was used to create this clause;
       *         never null
       */
      public ConstraintBuilder isBelowPath(String descendantTable,
                                              String ancestorPath)
      {
         return setConstraint(new DescendantNode(selector(descendantTable), ancestorPath));
      }

      /**
       * Define a constraint clause that the node within the named table has at
       * least one value for the named property.
       * 
       * @param table
       *           the name of the table; may not be null and must refer to a
       *           valid name or alias of a table appearing in the FROM clause
       * @param propertyName
       *           the name of the property
       * @return the constraint builder that was used to create this clause;
       *         never null
       */
      public ConstraintBuilder hasProperty(String table,
                                              String propertyName)
      {
         return setConstraint(new PropertyExistence(selector(table), propertyName));
      }

      /**
       * Define a constraint clause that the node within the named table have at
       * least one property that satisfies the full-text search expression.
       * 
       * @param table
       *           the name of the table; may not be null and must refer to a
       *           valid name or alias of a table appearing in the FROM clause
       * @param searchExpression
       *           the full-text search expression
       * @return the constraint builder that was used to create this clause;
       *         never null
       */
      public ConstraintBuilder search(String table,
                                         String searchExpression)
      {
         return setConstraint(new FullTextSearch(selector(table), null, searchExpression));
      }

      /**
       * Define a constraint clause that the node within the named table have a
       * value for the named property that satisfies the full-text search
       * expression.
       * 
       * @param table
       *           the name of the table; may not be null and must refer to a
       *           valid name or alias of a table appearing in the FROM clause
       * @param propertyName
       *           the name of the property to be searched
       * @param searchExpression
       *           the full-text search expression
       * @return the constraint builder that was used to create this clause;
       *         never null
       */
      public ConstraintBuilder search(String table,
                                         String propertyName,
                                         String searchExpression)
      {
         return setConstraint(new FullTextSearch(selector(table), propertyName, searchExpression));
      }

      protected ComparisonBuilder comparisonBuilder(DynamicOperand operand)
      {
         return new ComparisonBuilder(this, operand);
      }

      /**
       * {@inheritDoc}
       * 
       * @see org.modeshape.graph.query.QueryBuilder.DynamicOperandBuilder#length(java.lang.String,
       *      java.lang.String)
       */
      public ComparisonBuilder length(String table,
                                         String property)
      {
         return comparisonBuilder(new Length(new PropertyValue(selector(table), property)));
      }

      /**
       * {@inheritDoc}
       * 
       * @see org.modeshape.graph.query.QueryBuilder.DynamicOperandBuilder#propertyValue(String,
       *      String)
       */
      public ComparisonBuilder propertyValue(String table,
                                                String property)
      {
         return comparisonBuilder(new PropertyValue(selector(table), property));
      }

      /**
       * {@inheritDoc}
       * 
       * @see org.modeshape.graph.query.QueryBuilder.DynamicOperandBuilder#fullTextSearchScore(String)
       */
      public ComparisonBuilder fullTextSearchScore(String table)
      {
         return comparisonBuilder(new FullTextSearchScore(selector(table)));
      }

      /**
       * {@inheritDoc}
       * 
       * @see org.modeshape.graph.query.QueryBuilder.DynamicOperandBuilder#depth(java.lang.String)
       */
      public ComparisonBuilder depth(String table)
      {
         return comparisonBuilder(new NodeDepth(selector(table)));
      }

      /**
       * {@inheritDoc}
       * 
       * @see org.modeshape.graph.query.QueryBuilder.DynamicOperandBuilder#nodeLocalName(String)
       */
      public ComparisonBuilder nodeLocalName(String table)
      {
         return comparisonBuilder(new NodeLocalName(selector(table)));
      }

      /**
       * {@inheritDoc}
       * 
       * @see org.modeshape.graph.query.QueryBuilder.DynamicOperandBuilder#nodeName(String)
       */
      public ComparisonBuilder nodeName(String table)
      {
         return comparisonBuilder(new NodeName(selector(table)));
      }

      /**
       * {@inheritDoc}
       * 
       * @see org.modeshape.graph.query.QueryBuilder.DynamicOperandBuilder#upperCaseOf()
       */
      public DynamicOperandBuilder upperCaseOf()
      {
         return new UpperCaser(this);
      }

      /**
       * {@inheritDoc}
       * 
       * @see org.modeshape.graph.query.QueryBuilder.DynamicOperandBuilder#lowerCaseOf()
       */
      public DynamicOperandBuilder lowerCaseOf()
      {
         return new LowerCaser(this);
      }

      protected ConstraintBuilder setConstraint(Constraint constraint)
      {
         if (this.constraint != null && this.left == null)
         {
            and();
         }
         this.constraint = constraint;
         return buildLogicalConstraint();
      }
   }

   /**
    * A specialized form of the {@link ConstraintBuilder} that always wraps the
    * generated constraint in a {@link UpperCase} instance.
    */
   protected class UpperCaser extends ConstraintBuilder
   {
      private final ConstraintBuilder delegate;

      protected UpperCaser(ConstraintBuilder delegate)
      {
         super(null);
         this.delegate = delegate;
      }

      @Override
      protected ConstraintBuilder setConstraint(Constraint constraint)
      {
         Comparison comparison = (Comparison)constraint;
         return delegate.setConstraint(new Comparison(new UpperCase(comparison.getOperand1()),
            comparison.getOperator(),
                                                         comparison.getOperand2()));
      }
   }

   /**
    * A specialized form of the {@link ConstraintBuilder} that always wraps the
    * generated constraint in a {@link LowerCase} instance.
    */
   protected class LowerCaser extends ConstraintBuilder
   {
      private final ConstraintBuilder delegate;

      protected LowerCaser(ConstraintBuilder delegate)
      {
         super(null);
         this.delegate = delegate;
      }

      @Override
      protected ConstraintBuilder setConstraint(Constraint constraint)
      {
         Comparison comparison = (Comparison)constraint;
         return delegate.setConstraint(new Comparison(new LowerCase(comparison.getOperand1()),
            comparison.getOperator(),
                                                         comparison.getOperand2()));
      }
   }

   public abstract class CastAs<ReturnType>
   {
      protected final Object value;

      protected CastAs(Object value)
      {
         this.value = value;
      }

      /**
       * Define the right-hand side literal value cast as the specified type.
       * 
       * @param type
       *           the property type; may not be null
       * @return the constraint builder; never null
       */
      public abstract ReturnType as(PropertyType type);

      /**
       * Define the right-hand side literal value cast as a
       * {@link PropertyType#STRING}.
       * 
       * @return the constraint builder; never null
       */
      public ReturnType asString()
      {
         return as(PropertyType.STRING);
      }

      /**
       * Define the right-hand side literal value cast as a
       * {@link PropertyType#BOOLEAN}.
       * 
       * @return the constraint builder; never null
       */
      public ReturnType asBoolean()
      {
         return as(PropertyType.BOOLEAN);
      }

      /**
       * Define the right-hand side literal value cast as a
       * {@link PropertyType#LONG}.
       * 
       * @return the constraint builder; never null
       */
      public ReturnType asLong()
      {
         return as(PropertyType.LONG);
      }

      /**
       * Define the right-hand side literal value cast as a
       * {@link PropertyType#DOUBLE}.
       * 
       * @return the constraint builder; never null
       */
      public ReturnType asDouble()
      {
         return as(PropertyType.DOUBLE);
      }

      /**
       * Define the right-hand side literal value cast as a
       * {@link PropertyType#DATE}.
       * 
       * @return the constraint builder; never null
       */
      public ReturnType asDate()
      {
         return as(PropertyType.DATE);
      }

      /**
       * Define the right-hand side literal value cast as a
       * {@link PropertyType#PATH}.
       * 
       * @return the constraint builder; never null
       */
      public ReturnType asPath()
      {
         return as(PropertyType.DATE);
      }
   }

   public class CastAsRightHandSide extends CastAs<ConstraintBuilder>
   {
      private final RightHandSide rhs;

      protected CastAsRightHandSide(RightHandSide rhs,
                                       Object value)
      {
         super(value);
         this.rhs = rhs;
      }

      /**
       * Define the right-hand side literal value cast as the specified type.
       * 
       * @param type
       *           the property type; may not be null
       * @return the constraint builder; never null
       */
      @Override
      public ConstraintBuilder as(PropertyType type)
      {
         return rhs.comparisonBuilder.is(rhs.operator, castSystem.cast(value, type));
      }
   }

   public class RightHandSide
   {
      protected final Operator operator;

      protected final ComparisonBuilder comparisonBuilder;

      protected RightHandSide(ComparisonBuilder comparisonBuilder,
                                 Operator operator)
      {
         this.operator = operator;
         this.comparisonBuilder = comparisonBuilder;
      }

      /**
       * Define the right-hand side of a comparison.
       * 
       * @param literal
       *           the literal value;
       * @return the constraint builder; never null
       */
      public ConstraintBuilder literal(String literal)
      {
         return comparisonBuilder.is(operator, literal);
      }

      /**
       * Define the right-hand side of a comparison.
       * 
       * @param literal
       *           the literal value;
       * @return the constraint builder; never null
       */
      public ConstraintBuilder literal(int literal)
      {
         return comparisonBuilder.is(operator, literal);
      }

      /**
       * Define the right-hand side of a comparison.
       * 
       * @param literal
       *           the literal value;
       * @return the constraint builder; never null
       */
      public ConstraintBuilder literal(long literal)
      {
         return comparisonBuilder.is(operator, literal);
      }

      /**
       * Define the right-hand side of a comparison.
       * 
       * @param literal
       *           the literal value;
       * @return the constraint builder; never null
       */
      public ConstraintBuilder literal(float literal)
      {
         return comparisonBuilder.is(operator, literal);
      }

      /**
       * Define the right-hand side of a comparison.
       * 
       * @param literal
       *           the literal value;
       * @return the constraint builder; never null
       */
      public ConstraintBuilder literal(double literal)
      {
         return comparisonBuilder.is(operator, literal);
      }

      /**
       * Define the right-hand side of a comparison.
       * 
       * @param literal
       *           the literal value;
       * @return the constraint builder; never null
       */
      public ConstraintBuilder literal(Calendar literal)
      {
         return comparisonBuilder.is(operator, literal.getTimeInMillis());
      }

      /**
       * Define the right-hand side of a comparison.
       * 
       * @param literal
       *           the literal value;
       * @return the constraint builder; never null
       */
      public ConstraintBuilder literal(URI literal)
      {
         return comparisonBuilder.is(operator, literal);
      }

      /**
       * Define the right-hand side of a comparison.
       * 
       * @param literal
       *           the literal value;
       * @return the constraint builder; never null
       */
      public ConstraintBuilder literal(UUID literal)
      {
         return comparisonBuilder.is(operator, literal);
      }

      /**
       * Define the right-hand side of a comparison.
       * 
       * @param literal
       *           the literal value;
       * @return the constraint builder; never null
       */
      public ConstraintBuilder literal(BigDecimal literal)
      {
         return comparisonBuilder.is(operator, literal);
      }

      /**
       * Define the right-hand side of a comparison.
       * 
       * @param literal
       *           the literal value;
       * @return the constraint builder; never null
       */
      public ConstraintBuilder literal(boolean literal)
      {
         return comparisonBuilder.is(operator, literal);
      }

      /**
       * Define the right-hand side of a comparison.
       * 
       * @param variableName
       *           the name of the variable
       * @return the constraint builder; never null
       */
      public ConstraintBuilder variable(String variableName)
      {
         return comparisonBuilder.is(operator, variableName);
      }

      /**
       * Define the right-hand side of a comparison.
       * 
       * @param literal
       *           the literal value that is to be cast
       * @return the constraint builder; never null
       */
      public CastAs<ConstraintBuilder> cast(int literal)
      {
         return new CastAsRightHandSide(this, literal);
      }

      /**
       * Define the right-hand side of a comparison.
       * 
       * @param literal
       *           the literal value that is to be cast
       * @return the constraint builder; never null
       */
      public CastAs<ConstraintBuilder> cast(String literal)
      {
         return new CastAsRightHandSide(this, literal);
      }

      /**
       * Define the right-hand side of a comparison.
       * 
       * @param literal
       *           the literal value that is to be cast
       * @return the constraint builder; never null
       */
      public CastAs<ConstraintBuilder> cast(boolean literal)
      {
         return new CastAsRightHandSide(this, literal);
      }

      /**
       * Define the right-hand side of a comparison.
       * 
       * @param literal
       *           the literal value that is to be cast
       * @return the constraint builder; never null
       */
      public CastAs<ConstraintBuilder> cast(long literal)
      {
         return new CastAsRightHandSide(this, literal);
      }

      /**
       * Define the right-hand side of a comparison.
       * 
       * @param literal
       *           the literal value that is to be cast
       * @return the constraint builder; never null
       */
      public CastAs<ConstraintBuilder> cast(double literal)
      {
         return new CastAsRightHandSide(this, literal);
      }

      /**
       * Define the right-hand side of a comparison.
       * 
       * @param literal
       *           the literal value that is to be cast
       * @return the constraint builder; never null
       */
      public CastAs<ConstraintBuilder> cast(BigDecimal literal)
      {
         return new CastAsRightHandSide(this, literal);
      }

      /**
       * Define the right-hand side of a comparison.
       * 
       * @param literal
       *           the literal value that is to be cast
       * @return the constraint builder; never null
       */
      public CastAs<ConstraintBuilder> cast(Calendar literal)
      {
         return new CastAsRightHandSide(this, literal.getTimeInMillis());
      }

      /**
       * Define the right-hand side of a comparison.
       * 
       * @param literal
       *           the literal value that is to be cast
       * @return the constraint builder; never null
       */
      public CastAs<ConstraintBuilder> cast(UUID literal)
      {
         return new CastAsRightHandSide(this, literal);
      }

      /**
       * Define the right-hand side of a comparison.
       * 
       * @param literal
       *           the literal value that is to be cast
       * @return the constraint builder; never null
       */
      public CastAs<ConstraintBuilder> cast(URI literal)
      {
         return new CastAsRightHandSide(this, literal);
      }
   }

   /**
    * An interface used to set the right-hand side of a constraint.
    */
   public class ComparisonBuilder
   {
      protected final DynamicOperand left;

      protected final ConstraintBuilder constraintBuilder;

      protected ComparisonBuilder(ConstraintBuilder constraintBuilder,
                                    DynamicOperand left)
      {
         this.left = left;
         this.constraintBuilder = constraintBuilder;
      }

      /**
       * Define the operator that will be used in the comparison, returning an
       * interface that can be used to define the right-hand-side of the
       * comparison.
       * 
       * @param operator
       *           the operator; may not be null
       * @return the interface used to define the right-hand-side of the
       *         comparison
       */
      public RightHandSide is(Operator operator)
      {
         Validate.notNull(operator, "The operator argument may not be null");
         return new RightHandSide(this, operator);
      }

      /**
       * Use the 'equal to' operator in the comparison, returning an interface
       * that can be used to define the right-hand-side of the comparison.
       * 
       * @return the interface used to define the right-hand-side of the
       *         comparison
       */
      public RightHandSide isEqualTo()
      {
         return is(Operator.EQUAL_TO);
      }

      /**
       * Use the 'equal to' operator in the comparison, returning an interface
       * that can be used to define the right-hand-side of the comparison.
       * 
       * @return the interface used to define the right-hand-side of the
       *         comparison
       */
      public RightHandSide isNotEqualTo()
      {
         return is(Operator.NOT_EQUAL_TO);
      }

      /**
       * Use the 'equal to' operator in the comparison, returning an interface
       * that can be used to define the right-hand-side of the comparison.
       * 
       * @return the interface used to define the right-hand-side of the
       *         comparison
       */
      public RightHandSide isGreaterThan()
      {
         return is(Operator.GREATER_THAN);
      }

      /**
       * Use the 'equal to' operator in the comparison, returning an interface
       * that can be used to define the right-hand-side of the comparison.
       * 
       * @return the interface used to define the right-hand-side of the
       *         comparison
       */
      public RightHandSide isGreaterThanOrEqualTo()
      {
         return is(Operator.GREATER_THAN_OR_EQUAL_TO);
      }

      /**
       * Use the 'equal to' operator in the comparison, returning an interface
       * that can be used to define the right-hand-side of the comparison.
       * 
       * @return the interface used to define the right-hand-side of the
       *         comparison
       */
      public RightHandSide isLessThan()
      {
         return is(Operator.LESS_THAN);
      }

      /**
       * Use the 'equal to' operator in the comparison, returning an interface
       * that can be used to define the right-hand-side of the comparison.
       * 
       * @return the interface used to define the right-hand-side of the
       *         comparison
       */
      public RightHandSide isLessThanOrEqualTo()
      {
         return is(Operator.LESS_THAN_OR_EQUAL_TO);
      }

      /**
       * Use the 'equal to' operator in the comparison, returning an interface
       * that can be used to define the right-hand-side of the comparison.
       * 
       * @return the interface used to define the right-hand-side of the
       *         comparison
       */
      public RightHandSide isLike()
      {
         return is(Operator.LIKE);
      }

      /**
       * Define the right-hand-side of the constraint using the supplied
       * operator.
       * 
       * @param operator
       *           the operator; may not be null
       * @param variableName
       *           the name of the variable
       * @return the builder used to create the constraint clause, ready to be
       *         used to create other constraints clauses or complete
       *         already-started clauses; never null
       */
      public ConstraintBuilder isVariable(Operator operator,
                                            String variableName)
      {

         Validate.notNull(operator, "The operator argument may not be null");
         return this.constraintBuilder
            .setConstraint(new Comparison(left, operator, new BindVariableName(variableName)));
      }

      /**
       * Define the right-hand-side of the constraint using the supplied
       * operator.
       * 
       * @param operator
       *           the operator; may not be null
       * @param literal
       *           the literal value
       * @return the builder used to create the constraint clause, ready to be
       *         used to create other constraints clauses or complete
       *         already-started clauses; never null
       */
      public ConstraintBuilder is(Operator operator,
                                    Object literal)
      {
         assert operator != null;
         Literal value = literal instanceof Literal ? (Literal)literal : new Literal(literal);
         return this.constraintBuilder.setConstraint(new Comparison(left, operator, value));
      }

      /**
       * Define the right-hand-side of the constraint to be equivalent to the
       * value of the supplied variable.
       * 
       * @param variableName
       *           the name of the variable
       * @return the builder used to create the constraint clause, ready to be
       *         used to create other constraints clauses or complete
       *         already-started clauses; never null
       */
      public ConstraintBuilder isEqualToVariable(String variableName)
      {
         return isVariable(Operator.EQUAL_TO, variableName);
      }

      /**
       * Define the right-hand-side of the constraint to be greater than the
       * value of the supplied variable.
       * 
       * @param variableName
       *           the name of the variable
       * @return the builder used to create the constraint clause, ready to be
       *         used to create other constraints clauses or complete
       *         already-started clauses; never null
       */
      public ConstraintBuilder isGreaterThanVariable(String variableName)
      {
         return isVariable(Operator.GREATER_THAN, variableName);
      }

      /**
       * Define the right-hand-side of the constraint to be greater than or
       * equal to the value of the supplied variable.
       * 
       * @param variableName
       *           the name of the variable
       * @return the builder used to create the constraint clause, ready to be
       *         used to create other constraints clauses or complete
       *         already-started clauses; never null
       */
      public ConstraintBuilder isGreaterThanOrEqualToVariable(String variableName)
      {
         return isVariable(Operator.GREATER_THAN_OR_EQUAL_TO, variableName);
      }

      /**
       * Define the right-hand-side of the constraint to be less than the value
       * of the supplied variable.
       * 
       * @param variableName
       *           the name of the variable
       * @return the builder used to create the constraint clause, ready to be
       *         used to create other constraints clauses or complete
       *         already-started clauses; never null
       */
      public ConstraintBuilder isLessThanVariable(String variableName)
      {
         return isVariable(Operator.LESS_THAN, variableName);
      }

      /**
       * Define the right-hand-side of the constraint to be less than or equal
       * to the value of the supplied variable.
       * 
       * @param variableName
       *           the name of the variable
       * @return the builder used to create the constraint clause, ready to be
       *         used to create other constraints clauses or complete
       *         already-started clauses; never null
       */
      public ConstraintBuilder isLessThanOrEqualToVariable(String variableName)
      {
         return isVariable(Operator.LESS_THAN_OR_EQUAL_TO, variableName);
      }

      /**
       * Define the right-hand-side of the constraint to be LIKE the value of
       * the supplied variable.
       * 
       * @param variableName
       *           the name of the variable
       * @return the builder used to create the constraint clause, ready to be
       *         used to create other constraints clauses or complete
       *         already-started clauses; never null
       */
      public ConstraintBuilder isLikeVariable(String variableName)
      {
         return isVariable(Operator.LIKE, variableName);
      }

      /**
       * Define the right-hand-side of the constraint to be not equal to the
       * value of the supplied variable.
       * 
       * @param variableName
       *           the name of the variable
       * @return the builder used to create the constraint clause, ready to be
       *         used to create other constraints clauses or complete
       *         already-started clauses; never null
       */
      public ConstraintBuilder isNotEqualToVariable(String variableName)
      {
         return isVariable(Operator.NOT_EQUAL_TO, variableName);
      }

      /**
       * Define the right-hand-side of the constraint to be equivalent to the
       * supplied literal value.
       * 
       * @param literal
       *           the literal value
       * @return the builder used to create the constraint clause, ready to be
       *         used to create other constraints clauses or complete
       *         already-started clauses; never null
       */
      public ConstraintBuilder isEqualTo(Object literal)
      {
         return is(Operator.EQUAL_TO, literal);
      }

      /**
       * Define the right-hand-side of the constraint to be greater than the
       * supplied literal value.
       * 
       * @param literal
       *           the literal value
       * @return the builder used to create the constraint clause, ready to be
       *         used to create other constraints clauses or complete
       *         already-started clauses; never null
       */
      public ConstraintBuilder isGreaterThan(Object literal)
      {
         return is(Operator.GREATER_THAN, literal);
      }

      /**
       * Define the right-hand-side of the constraint to be greater than or
       * equal to the supplied literal value.
       * 
       * @param literal
       *           the literal value
       * @return the builder used to create the constraint clause, ready to be
       *         used to create other constraints clauses or complete
       *         already-started clauses; never null
       */
      public ConstraintBuilder isGreaterThanOrEqualTo(Object literal)
      {
         return is(Operator.GREATER_THAN_OR_EQUAL_TO, literal);
      }

      /**
       * Define the right-hand-side of the constraint to be less than the
       * supplied literal value.
       * 
       * @param literal
       *           the literal value
       * @return the builder used to create the constraint clause, ready to be
       *         used to create other constraints clauses or complete
       *         already-started clauses; never null
       */
      public ConstraintBuilder isLessThan(Object literal)
      {
         return is(Operator.LESS_THAN, literal);
      }

      /**
       * Define the right-hand-side of the constraint to be less than or equal
       * to the supplied literal value.
       * 
       * @param literal
       *           the literal value
       * @return the builder used to create the constraint clause, ready to be
       *         used to create other constraints clauses or complete
       *         already-started clauses; never null
       */
      public ConstraintBuilder isLessThanOrEqualTo(Object literal)
      {
         return is(Operator.LESS_THAN_OR_EQUAL_TO, literal);
      }

      /**
       * Define the right-hand-side of the constraint to be LIKE the supplied
       * literal value.
       * 
       * @param literal
       *           the literal value
       * @return the builder used to create the constraint clause, ready to be
       *         used to create other constraints clauses or complete
       *         already-started clauses; never null
       */
      public ConstraintBuilder isLike(Object literal)
      {
         return is(Operator.LIKE, literal);
      }

      /**
       * Define the right-hand-side of the constraint to be not equal to the
       * supplied literal value.
       * 
       * @param literal
       *           the literal value
       * @return the builder used to create the constraint clause, ready to be
       *         used to create other constraints clauses or complete
       *         already-started clauses; never null
       */
      public ConstraintBuilder isNotEqualTo(Object literal)
      {
         return is(Operator.NOT_EQUAL_TO, literal);
      }

   }

   public class AndBuilder<T>
   {
      private final T object;

      protected AndBuilder(T object)
      {
         assert object != null;
         this.object = object;
      }

      /**
       * Return the component
       * 
       * @return the component; never null
       */
      public T and()
      {
         return this.object;
      }
   }
}
