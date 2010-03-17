/*
 * ModeShape (http://www.modeshape.org)
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * See the AUTHORS.txt file in the distribution for a full listing of 
 * individual contributors.
 *
 * ModeShape is free software. Unless otherwise indicated, all code in ModeShape
 * is licensed to you under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * ModeShape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xcmis.search.query.process;

import org.apache.commons.lang.StringUtils;
import org.xcmis.search.query.QueryExecutionExceptions;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * The resulting output of a query.
 */
public class QueryResults implements org.xcmis.search.query.QueryResults
{
   private static final QueryExecutionExceptions NO_PROBLEMS = new QueryExecutionExceptions();

   private static final long serialVersionUID = 1L;

   private final QueryExecutionExceptions problems;

   private final Columns columns;

   private final List<Object[]> tuples;

   private final Statistics statistics;

   private final String plan;

   /**
    * Create a results object for the supplied context, command, and result columns and with the supplied tuples.
    * 
    * @param columns the definition of the query result columns
    * @param statistics the statistics for this query; may not be null
    * @param tuples the tuples
    * @param problems the problems; may be null if there are no problems
    * @param plan the text representation of the query plan, if the hints asked for it
    */
   public QueryResults(Columns columns, Statistics statistics, List<Object[]> tuples,
      QueryExecutionExceptions problems, String plan)
   {
      assert columns != null;
      assert statistics != null;
      this.problems = problems != null ? problems : NO_PROBLEMS;
      this.columns = columns;
      this.tuples = tuples;
      this.statistics = statistics;
      this.plan = plan;
   }

   /**
    * Create a results object for the supplied context, command, and result columns and with the supplied tuples.
    * 
    * @param columns the definition of the query result columns
    * @param statistics the statistics for this query; may not be null
    * @param tuples the tuples
    */
   public QueryResults(Columns columns, Statistics statistics, List<Object[]> tuples)
   {
      this(columns, statistics, tuples, NO_PROBLEMS, null);
   }

   /**
    * Create an empty {@link QueryResults} object for the supplied context, command, and result columns.
    * 
    * @param columns the definition of the query result columns
    * @param statistics the statistics for this query; may not be null
    * @param problems the problems; may be null if there are no problems
    */
   public QueryResults(Columns columns, Statistics statistics, QueryExecutionExceptions problems)
   {
      this(columns, statistics, Collections.<Object[]> emptyList(), problems, null);
   }

   /**
    * Create an empty {@link QueryResults} object for the supplied context, command, and result columns.
    * 
    * @param columns the definition of the query result columns
    * @param statistics the statistics for this query; may not be null
    */
   public QueryResults(Columns columns, Statistics statistics)
   {
      this(columns, statistics, Collections.<Object[]> emptyList(), null, null);
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.modeshape.graph.query.QueryResults#getColumns()
    */
   public Columns getColumns()
   {
      return columns;
   }

   /**
    * Get a cursor that can be used to walk through the results.
    * 
    * @return the cursor; never null, though possibly empty (meaning {@link Cursor#hasNext()} may return true)
    */
   public Cursor getCursor()
   {
      return new TupleCursor(columns, tuples.iterator());
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.modeshape.graph.query.QueryResults#getTuples()
    */
   public List<Object[]> getTuples()
   {
      return tuples;
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.modeshape.graph.query.QueryResults#getRowCount()
    */
   public int getRowCount()
   {
      return tuples.size();
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.modeshape.graph.query.QueryResults#getPlan()
    */
   public String getPlan()
   {
      return plan;
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.modeshape.graph.query.QueryResults#getProblems()
    */
   public QueryExecutionExceptions getProblems()
   {
      return problems;
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.modeshape.graph.query.QueryResults#hasErrors()
    */
   public boolean hasErrors()
   {
      return getProblems().hasProblems();
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.modeshape.graph.query.QueryResults#hasWarnings()
    */
   public boolean hasWarnings()
   {
      return getProblems().hasProblems();
   }

   /**
    * {@inheritDoc}
    * 
    * @see org.modeshape.graph.query.QueryResults#getStatistics()
    */
   public Statistics getStatistics()
   {
      return statistics;
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return toString(Integer.MAX_VALUE);
   }

   /**
    * Get a string representation of this result object, with a maximum number of tuples to include.
    * 
    * @param typeSystem the type system that can be used to convert the values to a string; may be null if
    *        {@link Object#toString()} should be used
    * @param maxTuples the maximum number of tuples to print, or {@link Integer#MAX_VALUE} if all the tuples are to be printed
    * @return the string representation; never null
    */
   public String toString(int maxTuples)
   {
      StringBuilder sb = new StringBuilder();
      toString(sb, maxTuples);
      return sb.toString();
   }

   /**
    * Get a string representation of this result object.
    * 
    * @param typeSystem the type system that can be used to convert the values to a string; may be null if
    *        {@link Object#toString()} should be used
    * @param sb the string builder to which the results should be written; may not be null
    */
   public void toString(StringBuilder sb)
   {
      toString(sb, Integer.MAX_VALUE);
   }

   /**
    * Get a string representation of this result object, with a maximum number of tuples to include.
    * 
    * @param typeSystem the type system that can be used to convert the values to a string; may be null if
    *        {@link Object#toString()} should be used
    * @param sb the string builder to which the results should be written; may not be null
    * @param maxTuples the maximum number of tuples to print, or {@link Integer#MAX_VALUE} if all the tuples are to be printed
    */
   public void toString(StringBuilder sb, int maxTuples)
   {
      int[] columnWidths = determineColumnWidths(Integer.MAX_VALUE, true);
      printDelimiterLine(sb, columnWidths, true);
      printHeader(sb, columnWidths);
      printDelimiterLine(sb, columnWidths, true);
      printLines(sb, columnWidths, maxTuples);
      printDelimiterLine(sb, columnWidths, false);
   }

   /**
    * Determine the width of each column.
    * 
    * @param typeSystem the type system that can be used to convert the values to a string; may be null if
    *        {@link Object#toString()} should be used
    * @param maxWidth the maximum width; must be positive
    * @param useData true if the data should be used to compute the length, or false if just the column names should be used
    * @return the array of widths for each column, excluding any decorating characters; never null
    */
   protected int[] determineColumnWidths(int maxWidth, boolean useData)
   {
      assert maxWidth > 0;
      int tupleLength = columns.getTupleSize();
      int[] columnWidths = new int[tupleLength + 1]; // +1 for the row number column
      for (int i = 0; i != columnWidths.length; ++i)
      {
         columnWidths[i] = 0;
      }
      // Determine the width of the first column that shows the row number ...
      String rowNumber = Integer.toString(getTuples().size());
      columnWidths[0] = rowNumber.length();

      // Compute the column names ...
      List<String> tupleValueNames = columns.getTupleValueNames();
      for (int i = 0, j = 1, max = tupleValueNames.size(); i != max; ++i, ++j)
      {
         String name = tupleValueNames.get(i);
         columnWidths[j] = Math.max(Math.min(maxWidth, name.length()), columnWidths[j]);
      }
      // Look at the data ...
      if (useData)
      {
         for (Object[] tuple : getTuples())
         {
            for (int i = 0, j = 1; i != tupleLength; ++i, ++j)
            {
               String valueStr = stringOf(tuple[i]);
               if (valueStr == null)
               {
                  continue;
               }
               columnWidths[j] = Math.max(Math.min(maxWidth, valueStr.length()), columnWidths[j]);
            }
         }
      }
      return columnWidths;
   }

   protected String stringOf(Object value)
   {
      if (value == null)
      {
         return null;
      }
      return value.toString();
   }

   protected void printHeader(StringBuilder sb, int[] columnWidths)
   {
      // Print the row number column ...
      sb.append("| ").append(StringUtils.leftPad("#", columnWidths[0], ' ')).append(' ');
      // Print the name line ...
      sb.append('|');
      int i = 1;
      for (String name : columns.getTupleValueNames())
      {
         sb.append(' ');
         sb.append(StringUtils.leftPad(name, columnWidths[i], ' '));
         sb.append(" |");
         ++i;
      }
      sb.append('\n');
   }

   protected void printLines(StringBuilder sb, int[] columnWidths, int maxRowsToPrint)
   {
      int rowNumber = 1;
      int tupleLength = columns.getTupleSize();
      // Should they all be printed ?
      if (maxRowsToPrint > tuples.size())
      {
         // Print all tuples ...
         for (Object[] tuple : getTuples())
         {
            printTuple(sb, columnWidths, rowNumber, tupleLength, tuple);
            ++rowNumber;
         }
      }
      else
      {
         // Print max number of rows ...
         for (Object[] tuple : getTuples())
         {
            printTuple(sb, columnWidths, rowNumber, tupleLength, tuple);
            if (rowNumber >= maxRowsToPrint)
            {
               break;
            }
            ++rowNumber;
         }
      }

   }

   private final void printTuple(StringBuilder sb, int[] columnWidths, int rowNumber, int tupleLength, Object[] tuple)
   {
      // Print the row number column ...
      sb.append("| ").append(StringUtils.leftPad(Integer.toString(rowNumber), columnWidths[0], ' ')).append(' ');
      // Print the remaining columns ...
      for (int i = 0, j = 1; i != tupleLength; ++i, ++j)
      {
         String valueStr = stringOf(tuple[i]);
         valueStr = StringUtils.leftPad(valueStr, columnWidths[j], ' ');
         sb.append('|').append(' ').append(valueStr).append(' ');
      }
      sb.append('|');
      sb.append('\n');
   }

   protected void printDelimiterLine(StringBuilder sb, int[] columnWidths, boolean includeLineFeed)
   {
      sb.append('+');
      for (int i = 0, max = columnWidths.length; i != max; ++i)
      {
         for (int j = 0, width = columnWidths[i] + 2; j != width; ++j)
         { // +1 for space before, +1 for space after
            sb.append('-');
         }
         sb.append('+');
      }
      if (includeLineFeed)
      {
         sb.append('\n');
      }
   }

   /**
    * An interface used to walk through the results.
    */
   public final class TupleCursor implements Cursor
   {
      private final Columns columns;

      private final Iterator<Object[]> iterator;

      private Object[] currentTuple;

      private int tupleIndex;

      protected TupleCursor(Columns columns, Iterator<Object[]> iterator)
      {
         this.iterator = iterator;
         this.columns = columns;
         this.tupleIndex = -1;
      }

      /**
       * {@inheritDoc}
       * 
       * @see org.modeshape.graph.query.QueryResults.Cursor#hasNext()
       */
      public boolean hasNext()
      {
         return iterator.hasNext();
      }

      /**
       * {@inheritDoc}
       * 
       * @see org.modeshape.graph.query.QueryResults.Cursor#next()
       */
      public void next()
      {
         currentTuple = iterator.next();
         ++tupleIndex;
      }

      /**
       * {@inheritDoc}
       * 
       * @see org.modeshape.graph.query.QueryResults.Cursor#getLocation(int)
       */
      public Object getLocation(int columnNumber)
      {
         return currentTuple[columns.getLocationIndexForColumn(columnNumber)];
      }

      /**
       * {@inheritDoc}
       * 
       * @see org.modeshape.graph.query.QueryResults.Cursor#getLocation(java.lang.String)
       */
      public Object getLocation(String selectorName)
      {
         return currentTuple[columns.getLocationIndex(selectorName)];
      }

      /**
       * {@inheritDoc}
       * 
       * @see org.modeshape.graph.query.QueryResults.Cursor#getRowIndex()
       */
      public int getRowIndex()
      {
         return tupleIndex;
      }

      /**
       * {@inheritDoc}
       * 
       * @see org.modeshape.graph.query.QueryResults.Cursor#getValue(int)
       */
      public Object getValue(int columnNumber)
      {
         if (columnNumber >= columns.getColumnCount())
         {
            throw new IndexOutOfBoundsException();
         }
         if (currentTuple == null)
         {
            throw new IllegalStateException("The 'next()' method must be called before 'getValue()");
         }
         return currentTuple[columnNumber];
      }

      /**
       * {@inheritDoc}
       * 
       * @see org.modeshape.graph.query.QueryResults.Cursor#getValue(java.lang.String)
       */
      public Object getValue(String columnName)
      {
         if (currentTuple == null)
         {
            throw new IllegalStateException("The 'next()' method must be called before 'getValue()");
         }
         return currentTuple[columns.getColumnIndexForName(columnName)];
      }
   }
}
