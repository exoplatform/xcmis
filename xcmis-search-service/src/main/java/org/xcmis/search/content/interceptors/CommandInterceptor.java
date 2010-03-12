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

/**
 * This is the base class for all interceptors to extend, and implements the
 * {@link Visitor} interface allowing it to intercept invocations on
 * {@link VisitableCommand}s.
 * <p/>
 * When writing interceptors, authors can either override a specific visitXXX()
 * method or the more generic
 * {@link #handleDefault(InvocationContext , VisitableCommand)} which is the
 * default behaviour of any visit method, as defined in
 * {@link AbstractVisitor#handleDefault(InvocationContext , VisitableCommand)}.
 * <p/>
 * The preferred approach is to override the specific visitXXX() methods that
 * are of interest rather than to override
 * {@link #handleDefault(InvocationContext , VisitableCommand)} and then write a
 * series of if statements or a switch block, if command-specific behaviour is
 * needed.
 * <p/>
 */
public class CommandInterceptor extends AbstractVisitor
{
   private CommandInterceptor next;

   public CommandInterceptor()
   {

   }

   /**
    * Retrieves the next interceptor in the chain.
    * 
    * @return the next interceptor in the chain.
    */
   public CommandInterceptor getNext()
   {
      return next;
   }

   /**
    * @return true if there is another interceptor in the chain after this;
    *         false otherwise.
    */
   public boolean hasNext()
   {
      return getNext() != null;
   }

   /**
    * Sets the next interceptor in the chain to the interceptor passed in.
    * 
    * @param next
    *           next interceptor in the chain.
    */
   public void setNext(CommandInterceptor next)
   {
      this.next = next;
   }

   /**
    * Invokes the next interceptor in the chain. This is how interceptor
    * implementations should pass a call up the chain to the next interceptor.
    * 
    * @param ctx
    *           invocation context
    * @param command
    *           command to pass up the chain.
    * @return return value of the invocation
    * @throws Throwable
    *            in the event of problems
    */
   public Object invokeNextInterceptor(InvocationContext ctx, VisitableCommand command) throws Throwable
   {
      return command.acceptVisitor(ctx, next);
   }

   /**
    * The default behaviour of the visitXXX methods, which is to ignore the call
    * and pass the call up to the next interceptor in the chain.
    * 
    * @param ctx
    *           invocation context
    * @param command
    *           command to invoke
    * @return return value
    * @throws Throwable
    *            in the event of problems
    */
   @Override
   protected Object handleDefault(InvocationContext ctx, VisitableCommand command) throws Throwable
   {
      return invokeNextInterceptor(ctx, command);
   }
}
