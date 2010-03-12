/*
 * Copyright (C) 2010 eXo Platform SAS.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.xcmis.search.content.command.query;

import org.xcmis.search.content.command.InvocationContext;
import org.xcmis.search.content.command.VisitableCommand;
import org.xcmis.search.content.interceptors.Visitor;

/**
 * First command of the query of the given type. Next step will be
 * {@link ParseQueryCommand} then {@link ProcessQueryCommand}.
 */
public class SubmitStatementCommand implements VisitableCommand
{

   private final String statement;

   private final String type;

   /**
    * @param query
    * @param type
    */
   public SubmitStatementCommand(String statement, String type)
   {
      super();
      this.statement = statement;
      this.type = type;
   }

   /**
    * @see org.xcmis.search.content.command.VisitableCommand#acceptVisitor(org.xcmis.search.content.command.InvocationContext,
    *      org.xcmis.search.content.interceptors.Visitor)
    */
   @Override
   public Object acceptVisitor(InvocationContext ctx, Visitor visitor) throws Throwable
   {

      return visitor.visitSubmitStatementCommand(ctx, this);
   }

   /**
    * @return the statement
    */
   public String getStatement()
   {
      return statement;
   }

   /**
    * @return the type
    */
   public String getType()
   {
      return type;
   }

}
