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
import org.xcmis.search.content.command.tx.CommitCommand;
import org.xcmis.search.content.command.tx.RollBackCommand;

/**
 * This interface is the core of search service, where each
 * {@link VisitableCommand} can be visited by a Visitor implementation. Visitors
 * which are accepted by the {@link VisitableCommand} are able to modify the
 * command based on any logic encapsulated by the visitor.
 * 
 */
public interface Visitor
{

   /**
    * @param ctx
    * @param putDocumentsToTheIndexCommand
    * @return
    */
   Object visitModifyIndexCommand(InvocationContext ctx, ModifyIndexCommand command) throws Throwable;

   /**
    * Visits a CommitCommand.
    * 
    * @param ctx
    *           invocation context
    * @param command
    *           command to visit
    * @return response from the visit
    * @throws Throwable
    *            in the event of problems.
    */
   Object visitCommitCommand(InvocationContext ctx, CommitCommand command) throws Throwable;

   /**
    * @param ctx
    * @param executeQueryCommand
    * @return
    */
   Object visitExecuteSelectorCommand(InvocationContext ctx, ExecuteSelectorCommand command) throws Throwable;

   /**
    * @param ctx
    * @param getChildNodesCommand
    * @return
    * @throws Throwable
    */
   Object visitChildEntriesCommand(InvocationContext ctx, GetChildEntriesCommand command) throws Throwable;

   /**
    * @param ctx
    * @param getNodeCommand
    * @return
    */
   Object visitGetContentEntryCommand(InvocationContext ctx, GetContentEntryCommand command) throws Throwable;

   /**
    * Visits a RollBackCommand.
    * 
    * @param ctx
    *           invocation context
    * @param command
    *           command to visit
    * @return response from the visit
    * @throws Throwable
    *            in the event of problems.
    */
   Object visitRollBackCommand(InvocationContext ctx, RollBackCommand command) throws Throwable;

   /**
    * @param ctx
    * @param processQueryCommand
    * @return
    * @throws Throwable
    */
   Object visitProcessQueryCommand(InvocationContext ctx, ProcessQueryCommand command) throws Throwable;
}
