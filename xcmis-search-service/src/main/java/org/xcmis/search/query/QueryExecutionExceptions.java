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

import java.util.ArrayList;
import java.util.List;

/**
 * Exceptions what occurs during execution of query.
 */
public class QueryExecutionExceptions
{
   private final List<Throwable> exceptions;

   /**
    * 
    */
   public QueryExecutionExceptions()
   {
      this.exceptions = new ArrayList<Throwable>();
   }

   public void addException(Throwable throwable)
   {
      exceptions.add(throwable);
   }

   /**
    * Determine if there are problems in this collection.
    * 
    * @return true if there is at least one exception, or false if it is empty
    */
   public boolean hasProblems()
   {
      return !exceptions.isEmpty();
   };

   public Throwable getTopException()
   {
      if (!exceptions.isEmpty())
      {
         return exceptions.get(0);
      }
      return null;
   }
}
