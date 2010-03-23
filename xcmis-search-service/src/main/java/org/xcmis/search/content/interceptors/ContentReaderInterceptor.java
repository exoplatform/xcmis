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
import org.xcmis.search.content.command.read.GetChildNodesCommand;
import org.xcmis.search.content.command.read.GetNodeCommand;
import org.xcmis.search.content.command.read.GetPropertiesCommand;
import org.xcmis.search.content.command.read.GetPropertyCommand;

/**
 * Abstract class that interpret only read-only operation's.
 */
public abstract class ContentReaderInterceptor extends CommandInterceptor
{

   /**
    * 
    * @see org.xcmis.search.content.interceptors.AbstractVisitor#visitGetChildNodesCommand(org.xcmis.search.content.command.InvocationContext, org.xcmis.search.content.command.read.GetChildNodesCommand)
    */
   public abstract Object visitGetChildNodesCommand(InvocationContext ctx, GetChildNodesCommand command) throws Throwable;
   /**
    * 
    * @see org.xcmis.search.content.interceptors.AbstractVisitor#visitGetNodeCommand(org.xcmis.search.content.command.InvocationContext, org.xcmis.search.content.command.read.GetNodeCommand)
    */
   public abstract Object visitGetNodeCommand(InvocationContext ctx, GetNodeCommand command) throws Throwable;
   /**
    * 
    * @see org.xcmis.search.content.interceptors.AbstractVisitor#visitGetPropertiesCommand(org.xcmis.search.content.command.InvocationContext, org.xcmis.search.content.command.read.GetPropertiesCommand)
    */
   public abstract Object visitGetPropertiesCommand(InvocationContext ctx, GetPropertiesCommand command) throws Throwable;
   /**
    * 
    * @see org.xcmis.search.content.interceptors.AbstractVisitor#visitGetPropertyCommand(org.xcmis.search.content.command.InvocationContext, org.xcmis.search.content.command.read.GetPropertyCommand)
    */
   public abstract Object visitGetPropertyCommand(InvocationContext ctx, GetPropertyCommand command) throws Throwable;

}
