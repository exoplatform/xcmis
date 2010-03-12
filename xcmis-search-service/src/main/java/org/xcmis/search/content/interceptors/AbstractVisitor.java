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
import org.xcmis.search.content.command.index.AddDocumentsToTheIndexCommand;
import org.xcmis.search.content.command.index.RemoveDocumentsFromIndexCommand;
import org.xcmis.search.content.command.query.ParseQueryCommand;
import org.xcmis.search.content.command.query.ExecuteSelectorCommand;
import org.xcmis.search.content.command.query.ProcessQueryCommand;
import org.xcmis.search.content.command.query.SubmitStatementCommand;
import org.xcmis.search.content.command.read.GetChildNodeCommand;
import org.xcmis.search.content.command.read.GetChildNodesCommand;
import org.xcmis.search.content.command.read.GetNodeCommand;
import org.xcmis.search.content.command.read.GetPropertiesCommand;
import org.xcmis.search.content.command.read.GetPropertyCommand;
import org.xcmis.search.content.command.tx.CommitCommand;
import org.xcmis.search.content.command.tx.RollBackCommand;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@exoplatform.org">Sergey
 *         Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z ksm $
 * 
 */
public class AbstractVisitor implements Visitor
{

   /**
    * @see org.xcmis.search.content.interceptors.Visitor#visitAddDocumentsToTheIndexCommand(org.xcmis.search.content.command.InvocationContext,
    *      org.xcmis.search.content.command.index.AddDocumentsToTheIndexCommand)
    */
   @Override
   public Object visitAddDocumentsToTheIndexCommand(InvocationContext ctx,
      AddDocumentsToTheIndexCommand command) throws Throwable
   {
      return handleDefault(ctx, command);
   }

   /**
    * @see org.xcmis.search.content.interceptors.Visitor#visitCommitCommand(org.xcmis.search.content.command.InvocationContext,
    *      org.xcmis.search.content.command.tx.CommitCommand)
    */
   public Object visitCommitCommand(InvocationContext ctx, CommitCommand command) throws Throwable
   {
      return handleDefault(ctx, command);
   }

   /**
    * @see org.xcmis.search.content.interceptors.Visitor#visitParseQueryCommand(org.xcmis.search.content.command.InvocationContext,
    *      org.xcmis.search.content.command.query.ParseQueryCommand)
    */
   @Override
   public Object visitParseQueryCommand(InvocationContext ctx, ParseQueryCommand command)
      throws Throwable
   {
      return handleDefault(ctx, command);
   }

   /**
    * @see org.xcmis.search.content.interceptors.Visitor#visitExecuteSelectorCommand(org.xcmis.search.content.command.InvocationContext,
    *      org.xcmis.search.content.command.query.ExecuteSelectorCommand)
    */
   public Object visitExecuteSelectorCommand(InvocationContext ctx, ExecuteSelectorCommand command)
      throws Throwable
   {
      return handleDefault(ctx, command);
   }

   /**
    * @see org.xcmis.search.content.interceptors.Visitor#visitGetChildNodeCommand(org.xcmis.search.content.command.InvocationContext,
    *      org.xcmis.search.content.command.read.GetChildNodeCommand)
    */
   public Object visitGetChildNodeCommand(InvocationContext ctx, GetChildNodeCommand command) throws Throwable
   {
      return handleDefault(ctx, command);
   }

   /**
    * @throws Throwable
    * @see org.xcmis.search.content.interceptors.Visitor#visitGetChildNodesCommand(org.xcmis.search.content.command.InvocationContext,
    *      org.xcmis.search.content.command.read.GetChildNodesCommand)
    */
   public Object visitGetChildNodesCommand(InvocationContext ctx, GetChildNodesCommand command) throws Throwable
   {
      return handleDefault(ctx, command);
   }

   /**
    * @throws Throwable
    * @see org.xcmis.search.content.interceptors.Visitor#visitGetNodeCommand(org.xcmis.search.content.command.InvocationContext,
    *      org.xcmis.search.content.command.read.GetNodeCommand)
    */
   public Object visitGetNodeCommand(InvocationContext ctx, GetNodeCommand command) throws Throwable
   {
      return handleDefault(ctx, command);
   }

   /**
    * @throws Throwable
    * @see org.xcmis.search.content.interceptors.Visitor#visitGetPropertiesCommand(org.xcmis.search.content.command.InvocationContext,
    *      org.xcmis.search.content.command.read.GetPropertiesCommand)
    */
   public Object visitGetPropertiesCommand(InvocationContext ctx, GetPropertiesCommand command) throws Throwable
   {
      return handleDefault(ctx, command);
   }

   /**
    * @throws Throwable
    * @see org.xcmis.search.content.interceptors.Visitor#visitGetPropertyCommand(org.xcmis.search.content.command.InvocationContext,
    *      org.xcmis.search.content.command.read.GetPropertyCommand)
    */
   public Object visitGetPropertyCommand(InvocationContext ctx, GetPropertyCommand command) throws Throwable
   {
      return handleDefault(ctx, command);
   }

   /**
    * @see org.xcmis.search.content.interceptors.Visitor#visitRemoveDocumentsFromIndexCommand(org.xcmis.search.content.command.InvocationContext,
    *      org.xcmis.search.content.command.index.RemoveDocumentsFromIndexCommand)
    */
   @Override
   public Object visitRemoveDocumentsFromIndexCommand(InvocationContext ctx,
      RemoveDocumentsFromIndexCommand command) throws Throwable
   {
      return handleDefault(ctx, command);
   }

   /**
    * @see org.xcmis.search.content.interceptors.Visitor#visitRollBackCommand(org.xcmis.search.content.command.InvocationContext,
    *      org.xcmis.search.content.command.tx.RollBackCommand)
    */
   public Object visitRollBackCommand(InvocationContext ctx, RollBackCommand command) throws Throwable
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

   /**
    * @throws Throwable
    * @see org.xcmis.search.content.interceptors.Visitor#visitProcessQueryCommand(org.xcmis.search.content.command.InvocationContext,
    *      org.xcmis.search.content.command.query.ProcessQueryCommand)
    */
   @Override
   public Object visitProcessQueryCommand(InvocationContext ctx, ProcessQueryCommand command) throws Throwable
   {
      return handleDefault(ctx, command);
   }

   /**
    * @see org.xcmis.search.content.interceptors.Visitor#visitSubmitStatementCommand(org.xcmis.search.content.command.InvocationContext,
    *      org.xcmis.search.content.command.query.SubmitStatementCommand)
    */
   @Override
   public Object visitSubmitStatementCommand(InvocationContext ctx, SubmitStatementCommand command) throws Throwable
   {
      return handleDefault(ctx, command);
   }
}
