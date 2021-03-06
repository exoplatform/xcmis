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

import org.xcmis.search.model.Query;
import org.xcmis.search.query.QueryExecutionContext;

/**
 * Interface for a query  execution planner.
 */
public interface QueryExecutionPlaner
{
   /**
    * Create a canonical query plan for the given command.
    * 
    * @param context the context in which the query is being planned
    * @param query the query command to be planned
    * @return the root node of the plan tree representing the canonical plan
    */
   public QueryExecutionPlan createPlan(QueryExecutionContext context, Query query);
}
