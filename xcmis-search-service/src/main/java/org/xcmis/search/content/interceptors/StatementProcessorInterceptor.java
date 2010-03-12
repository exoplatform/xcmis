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
package org.xcmis.search.content.interceptors;

import org.xcmis.search.InvalidQueryException;
import org.xcmis.search.content.command.InvocationContext;
import org.xcmis.search.content.command.query.ParseQueryCommand;
import org.xcmis.search.content.command.query.ProcessQueryCommand;
import org.xcmis.search.content.command.query.SubmitStatementCommand;
import org.xcmis.search.model.Query;
import org.xcmis.search.query.QueryResults;

/**
 * Parse and execute given statement.
 * 
 */
public class StatementProcessorInterceptor extends CommandInterceptor
{

   /**
    * @see org.xcmis.search.content.interceptors.AbstractVisitor#visitSubmitStatementCommand(org.xcmis.search.content.command.InvocationContext,
    *      org.xcmis.search.content.command.query.SubmitStatementCommand)
    */
   @Override
   public Object visitSubmitStatementCommand(InvocationContext ctx, SubmitStatementCommand command) throws Throwable
   {
      ParseQueryCommand parseQueryCommand = new ParseQueryCommand(command.getStatement(),command.getType());
      Query queryModel = null;
      try
      {
         queryModel = (Query)invokeNextInterceptor(ctx, parseQueryCommand);
      }
      catch (Throwable e)
      {
         throw new InvalidQueryException(e.getLocalizedMessage(), e);
      }
      ProcessQueryCommand processQueryCommand = new ProcessQueryCommand(queryModel);
      return (QueryResults)invokeNextInterceptor(null, processQueryCommand);
   }

}
