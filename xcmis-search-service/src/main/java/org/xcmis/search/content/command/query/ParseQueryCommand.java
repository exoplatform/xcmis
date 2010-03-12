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
package org.xcmis.search.content.command.query;

import org.xcmis.search.content.command.InvocationContext;
import org.xcmis.search.content.command.VisitableCommand;
import org.xcmis.search.content.interceptors.Visitor;

/**
 * Execute query of the given type.
 *
 */
public class ParseQueryCommand implements VisitableCommand
{
   private final String query;
   
   private final String type;
   /**
    * @param query
    * @param type
    */
   public ParseQueryCommand(String query, String type)
   {
      super();
      this.query = query;
      this.type = type;
   }
   /**
    * @see org.xcmis.search.content.command.VisitableCommand#acceptVisitor(org.xcmis.search.content.command.InvocationContext, org.xcmis.search.content.interceptors.Visitor)
    */
   @Override
   public Object acceptVisitor(InvocationContext ctx, Visitor visitor) throws Throwable
   {

      return visitor.visitParseQueryCommand(ctx, this);
   }
   /**
    * @return the query
    */
   public String getQuery()
   {
      return query;
   }
   /**
    * @return the type
    */
   public String getType()
   {
      return type;
   }

}
