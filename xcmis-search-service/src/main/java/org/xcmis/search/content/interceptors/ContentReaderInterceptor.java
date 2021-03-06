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
package org.xcmis.search.content.interceptors;

import org.xcmis.search.content.command.InvocationContext;
import org.xcmis.search.content.command.read.GetChildEntriesCommand;
import org.xcmis.search.content.command.read.GetContentEntryCommand;
import org.xcmis.search.content.command.read.GetUnfiledEntriesCommand;

/**
 * Abstract class that interpret only read-only operation's.
 */
public abstract class ContentReaderInterceptor extends CommandInterceptor
{

   /**
    * @see org.xcmis.search.content.interceptors.AbstractVisitor#visitChildEntriesCommand(org.xcmis.search.content.command.InvocationContext, org.xcmis.search.content.command.read.GetChildEntriesCommand)
    */
   @Override
   public abstract Object visitChildEntriesCommand(InvocationContext ctx, GetChildEntriesCommand command)
      throws Throwable;

   /**
    * @see org.xcmis.search.content.interceptors.AbstractVisitor#visitGetContentEntryCommand(org.xcmis.search.content.command.InvocationContext, org.xcmis.search.content.command.read.GetContentEntryCommand)
    */
   @Override
   public abstract Object visitGetContentEntryCommand(InvocationContext ctx, GetContentEntryCommand command)
      throws Throwable;

   /**
    * @see org.xcmis.search.content.interceptors.AbstractVisitor#visitGetUnfilledEntriesCommand(org.xcmis.search.content.command.InvocationContext, org.xcmis.search.content.command.read.GetUnfilledEntriesCommand)
    */
   @Override
   public abstract Object visitGetUnfiledEntriesCommand(InvocationContext ctx, GetUnfiledEntriesCommand command)
      throws Throwable;

}
