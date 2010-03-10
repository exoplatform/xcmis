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

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Plan of the query execution.
 */
public class QueryExecutionPlan extends LinkedList<QueryExecutionStep>
{

   private static final long serialVersionUID = 1L;

   /**
    * @see java.lang.Iterable#iterator()
    */
   public Iterator<QueryExecutionStep> iterator()
   {
      return Collections.unmodifiableList(this).iterator();
   }

}
