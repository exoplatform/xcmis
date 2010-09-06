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

import org.xcmis.search.content.command.InvocationContext;
import org.xcmis.search.content.command.VisitableCommand;
import org.xcmis.search.content.command.index.ModifyIndexCommand;
import org.xcmis.search.content.command.query.ExecuteSelectorCommand;
import org.xcmis.search.content.command.query.ProcessQueryCommand;
import org.xcmis.search.content.command.read.GetChildEntriesCommand;
import org.xcmis.search.content.command.read.GetContentEntryCommand;
import org.xcmis.search.content.command.read.GetUnfiledEntriesCommand;

/**
 * 
 */
public class AbstractVisitor implements Visitor
{

   /**
    * @see org.xcmis.search.content.interceptors.Visitor#visitChildEntriesCommand(org.xcmis.search.content.command.InvocationContext, org.xcmis.search.content.command.read.GetChildEntriesCommand)
    */
   public Object visitChildEntriesCommand(InvocationContext ctx, GetChildEntriesCommand command) throws Throwable
   {
      return handleDefault(ctx, command);
   }

   /**
    * @see org.xcmis.search.content.interceptors.Visitor#visitExecuteSelectorCommand(org.xcmis.search.content.command.InvocationContext,
    *      org.xcmis.search.content.command.query.ExecuteSelectorCommand)
    */
   public Object visitExecuteSelectorCommand(InvocationContext ctx, ExecuteSelectorCommand command) throws Throwable
   {
      return handleDefault(ctx, command);
   }

   /**
    * @see org.xcmis.search.content.interceptors.Visitor#visitGetContentEntryCommand(org.xcmis.search.content.command.InvocationContext, org.xcmis.search.content.command.read.GetContentEntryCommand)
    */
   public Object visitGetContentEntryCommand(InvocationContext ctx, GetContentEntryCommand command) throws Throwable
   {
      return handleDefault(ctx, command);
   }

   /**
    * @see org.xcmis.search.content.interceptors.Visitor#visitGetUnfilledEntriesCommand(org.xcmis.search.content.command.InvocationContext, org.xcmis.search.content.command.read.GetUnfilledEntriesCommand)
    */
   public Object visitGetUnfiledEntriesCommand(InvocationContext ctx, GetUnfiledEntriesCommand command)
      throws Throwable
   {
      return handleDefault(ctx, command);
   }

   /**
    * @see org.xcmis.search.content.interceptors.Visitor#visitModifyIndexCommand(org.xcmis.search.content.command.InvocationContext,
    *      org.xcmis.search.content.command.index.ModifyIndexCommand)
    */
   public Object visitModifyIndexCommand(InvocationContext ctx, ModifyIndexCommand command) throws Throwable
   {
      return handleDefault(ctx, command);
   }

   /**
    * @throws Throwable
    * @see org.xcmis.search.content.interceptors.Visitor#visitProcessQueryCommand(org.xcmis.search.content.command.InvocationContext,
    *      org.xcmis.search.content.command.query.ProcessQueryCommand)
    */
   public Object visitProcessQueryCommand(InvocationContext ctx, ProcessQueryCommand command) throws Throwable
   {
      return handleDefault(ctx, command);
   }

   /**
    * A default handler for all commands visited. This is called for any visit
    * method called, unless a visit command is appropriately overridden.
    * 
    * @param ctx
    *           invocation context
    * @param command
    *           command to handle
    * @return return value
    * @throws Throwable
    *            in the case of a problem
    */
   protected Object handleDefault(InvocationContext ctx, VisitableCommand command) throws Throwable
   {
      return handleDefault(ctx, command);
   }
}
