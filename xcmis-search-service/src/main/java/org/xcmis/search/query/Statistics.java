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
package org.xcmis.search.query;

import org.apache.commons.lang.Validate;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class Statistics implements Comparable<Statistics>, Serializable
{
   private static final long serialVersionUID = 1L;

   protected static final Statistics EMPTY_STATISTICS = new Statistics();

   private final long planningMillis;

   private final long optimizationMillis;

   private final long resultFormulationMillis;

   private final long executionMillis;

   public Statistics()
   {
      this(0L, 0L, 0L, 0L);
   }

   public Statistics(long planningMillis)
   {
      this(planningMillis, 0L, 0L, 0L);
   }

   public Statistics(long planningMillis, long optimizationMillis, long resultFormulationMillis, long executionMillis)
   {
      this.planningMillis = planningMillis;
      this.optimizationMillis = optimizationMillis;
      this.resultFormulationMillis = resultFormulationMillis;
      this.executionMillis = executionMillis;
   }

   /**
    * Get the time required to come up with the canonical plan.
    * 
    * @param unit the time unit that should be used
    * @return the time to plan, in the desired units
    * @throws IllegalArgumentException if the unit is null
    */
   public long getPlanningTime(TimeUnit unit)
   {
      Validate.notNull(unit, "Unit should not be null");
      return unit.convert(planningMillis, TimeUnit.MILLISECONDS);
   }

   /**
    * Get the time required to determine or select a (more) optimal plan.
    * 
    * @param unit the time unit that should be used
    * @return the time to determine an optimal plan, in the desired units
    * @throws IllegalArgumentException if the unit is null
    */
   public long getOptimizationTime(TimeUnit unit)
   {
      Validate.notNull(unit, "Unit should not be null");
      return unit.convert(optimizationMillis, TimeUnit.MILLISECONDS);
   }

   /**
    * Get the time required to formulate the structure of the results.
    * 
    * @param unit the time unit that should be used
    * @return the time to formulate the results, in the desired units
    * @throws IllegalArgumentException if the unit is null
    */
   public long getResultFormulationTime(TimeUnit unit)
   {
      Validate.notNull(unit, "Unit should not be null");
      return unit.convert(resultFormulationMillis, TimeUnit.MILLISECONDS);
   }

   /**
    * Get the time required to execute the query.
    * 
    * @param unit the time unit that should be used
    * @return the time to execute the query, in the desired units
    * @throws IllegalArgumentException if the unit is null
    */
   public long getExecutionTime(TimeUnit unit)
   {
      return unit.convert(executionMillis, TimeUnit.MILLISECONDS);
   }

   /**
    * Get the time required to execute the query.
    * 
    * @param unit the time unit that should be used
    * @return the time to execute the query, in the desired units
    * @throws IllegalArgumentException if the unit is null
    */
   public long getTotalTime(TimeUnit unit)
   {
      return unit.convert(totalTime(), TimeUnit.MILLISECONDS);
   }

   protected long totalTime()
   {
      return planningMillis + optimizationMillis + resultFormulationMillis + executionMillis;
   }

   /**
    * Create a new statistics object that has the supplied planning time.
    * 
    * @param planningMillis the number of milliseconds required by planning
    * @return the new statistics object; never null
    * @throws IllegalArgumentException if the time value is negative
    */
   public Statistics withPlanningTime(long planningMillis)
   {
      Validate.isTrue(planningMillis >= 0, "planningMillis should be >=0");
      return new Statistics(planningMillis, optimizationMillis, resultFormulationMillis, executionMillis);
   }

   /**
    * Create a new statistics object that has the supplied optimization time.
    * 
    * @param optimizationMillis the number of milliseconds required by optimization
    * @return the new statistics object; never null
    * @throws IllegalArgumentException if the time value is negative
    */
   public Statistics withOptimizationTime(long optimizationMillis)
   {
      Validate.isTrue(optimizationMillis >= 0, "optimizationMillis should be >=0");
      return new Statistics(planningMillis, optimizationMillis, resultFormulationMillis, executionMillis);
   }

   /**
    * Create a new statistics object that has the supplied result formulation time.
    * 
    * @param resultFormulationMillis the number of milliseconds required by result formulation
    * @return the new statistics object; never null
    * @throws IllegalArgumentException if the time value is negative
    */
   public Statistics withResultsFormulationTime(long resultFormulationMillis)
   {
      Validate.isTrue(resultFormulationMillis >= 0, "resultFormulationMillis should be >=0");
      return new Statistics(planningMillis, optimizationMillis, resultFormulationMillis, executionMillis);
   }

   /**
    * Create a new statistics object that has the supplied execution time.
    * 
    * @param executionMillis the number of milliseconds required to execute the query
    * @return the new statistics object; never null
    * @throws IllegalArgumentException if the time value is negative
    */
   public Statistics withExecutionTime(long executionMillis)
   {
      Validate.isTrue(executionMillis >= 0, "executionMillis should be >=0");
      return new Statistics(planningMillis, optimizationMillis, resultFormulationMillis, executionMillis);
   }

   /**
    * Create a new statistics object that has the supplied planning time.
    * 
    * @param planning the time required to plan the query
    * @param unit the time unit
    * @return the new statistics object; never null
    * @throws IllegalArgumentException if the unit is null or if the time value is negative
    */
   public Statistics withPlanningTime(long planning, TimeUnit unit)
   {
      Validate.isTrue(planning >= 0, "planning should be >=0");
      Validate.notNull(unit, "Unit should not be null");
      long planningMillis = TimeUnit.NANOSECONDS.convert(planning, unit);
      return new Statistics(planningMillis, optimizationMillis, resultFormulationMillis, executionMillis);
   }

   /**
    * Create a new statistics object that has the supplied optimization time.
    * 
    * @param optimization the time required by optimization
    * @param unit the time unit
    * @return the new statistics object; never null
    * @throws IllegalArgumentException if the unit is null or if the time value is negative
    */
   public Statistics withOptimizationTime(long optimization, TimeUnit unit)
   {

      Validate.isTrue(optimization >= 0, "optimization should be >=0");
      Validate.notNull(unit, "Unit should not be null");
      long optimizationMillis = TimeUnit.NANOSECONDS.convert(optimization, unit);
      return new Statistics(planningMillis, optimizationMillis, resultFormulationMillis, executionMillis);
   }

   /**
    * Create a new statistics object that has the supplied result formulation time.
    * 
    * @param resultFormulation the time required to formulate the results
    * @param unit the time unit
    * @return the new statistics object; never null
    * @throws IllegalArgumentException if the unit is null or if the time value is negative
    */
   public Statistics withResultsFormulationTime(long resultFormulation, TimeUnit unit)
   {

      Validate.isTrue(resultFormulation >= 0, "resultFormulation should be >=0");
      Validate.notNull(unit, "Unit should not be null");

      long resultFormulationMillis = TimeUnit.MILLISECONDS.convert(resultFormulation, unit);
      return new Statistics(planningMillis, optimizationMillis, resultFormulationMillis, executionMillis);
   }

   /**
    * Create a new statistics object that has the supplied execution time.
    * 
    * @param execution the time required to execute the query
    * @param unit the time unit
    * @return the new statistics object; never null
    * @throws IllegalArgumentException if the unit is null or if the time value is negative
    */
   public Statistics withExecutionTime(long execution, TimeUnit unit)
   {

      Validate.isTrue(execution >= 0, "execution should be >=0");
      Validate.notNull(unit, "Unit should not be null");
      long executionMillis = TimeUnit.MILLISECONDS.convert(execution, unit);
      return new Statistics(planningMillis, optimizationMillis, resultFormulationMillis, executionMillis);
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   public int compareTo(Statistics that)
   {
      if (that == this)
      {
         return 0;
      }
      long diff = this.totalTime() - that.totalTime();
      if (diff < 0)
      {
         return -1;
      }
      if (diff > 0)
      {
         return 1;
      }
      return 0;
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder();
      readable(totalTime(), sb);
      boolean first = false;
      if (planningMillis != 0L)
      {
         sb.append(" (plan=");
         readable(planningMillis, sb);
         first = false;
      }
      if (optimizationMillis != 0L)
      {
         if (first)
         {
            first = false;
            sb.append(" (");
         }
         else
         {
            sb.append(" ,");
         }
         sb.append("opt=");
         readable(optimizationMillis, sb);
      }
      if (resultFormulationMillis != 0L)
      {
         if (first)
         {
            first = false;
            sb.append(" (");
         }
         else
         {
            sb.append(" ,");
         }
         sb.append("res=");
         readable(resultFormulationMillis, sb);
      }
      if (executionMillis != 0L)
      {
         if (first)
         {
            first = false;
            sb.append(" (");
         }
         else
         {
            sb.append(" ,");
         }
         sb.append("exec=");
         readable(executionMillis, sb);
      }
      if (!first)
      {
         sb.append(')');
      }
      return sb.toString();
   }

   protected void readable(long millis, StringBuilder sb)
   {
      // 3210987654321
      // XXXXXXXXXXXXX millis
      // XXXXXXXXXX micros
      // XXXXXXX millis
      // XXXX seconds
      //TODO check conversion
      if (millis < 1000)
      {
         sb.append(millis).append(" ns");
      }
      else if (millis < 1000000)
      {
         double value = millis / 1000d;
         sb.append(FORMATTER.get().format(value)).append(" usec");
      }
      else if (millis < 1000000000)
      {
         double value = millis / 1000000d;
         sb.append(FORMATTER.get().format(value)).append(" ms");
      }
      else
      {
         double value = millis / 1000000000d;
         sb.append(FORMATTER.get().format(value)).append(" sec");
      }
   }

   static ThreadLocal<DecimalFormat> FORMATTER = new ThreadLocal<DecimalFormat>()
            {
      @Override
      protected synchronized DecimalFormat initialValue()
      {
         return new DecimalFormat("###,###,##0.0##");
      }
            };
}
