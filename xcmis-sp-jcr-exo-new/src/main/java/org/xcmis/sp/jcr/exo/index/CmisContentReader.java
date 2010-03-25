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
package org.xcmis.sp.jcr.exo.index;

import org.xcmis.search.content.command.InvocationContext;
import org.xcmis.search.content.command.read.GetChildNodesCommand;
import org.xcmis.search.content.command.read.GetNodeCommand;
import org.xcmis.search.content.command.read.GetPropertiesCommand;
import org.xcmis.search.content.command.read.GetPropertyCommand;
import org.xcmis.search.content.interceptors.ContentReaderInterceptor;
import org.xcmis.spi.Storage;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@exoplatform.org">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z ksm $
 *
 */
public class CmisContentReader extends ContentReaderInterceptor
{
   private final Storage storage;

   /**
    * @param storage
    */
   public CmisContentReader(Storage storage)
   {
      super();
      this.storage = storage;
   }

   /**
    * @see org.xcmis.search.content.interceptors.ContentReaderInterceptor#visitGetChildNodesCommand(org.xcmis.search.content.command.InvocationContext, org.xcmis.search.content.command.read.GetChildNodesCommand)
    */
   @Override
   public Object visitGetChildNodesCommand(InvocationContext ctx, GetChildNodesCommand command) throws Throwable
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.xcmis.search.content.interceptors.ContentReaderInterceptor#visitGetNodeCommand(org.xcmis.search.content.command.InvocationContext, org.xcmis.search.content.command.read.GetNodeCommand)
    */
   @Override
   public Object visitGetNodeCommand(InvocationContext ctx, GetNodeCommand command) throws Throwable
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.xcmis.search.content.interceptors.ContentReaderInterceptor#visitGetPropertiesCommand(org.xcmis.search.content.command.InvocationContext, org.xcmis.search.content.command.read.GetPropertiesCommand)
    */
   @Override
   public Object visitGetPropertiesCommand(InvocationContext ctx, GetPropertiesCommand command) throws Throwable
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.xcmis.search.content.interceptors.ContentReaderInterceptor#visitGetPropertyCommand(org.xcmis.search.content.command.InvocationContext, org.xcmis.search.content.command.read.GetPropertyCommand)
    */
   @Override
   public Object visitGetPropertyCommand(InvocationContext ctx, GetPropertyCommand command) throws Throwable
   {
      // TODO Auto-generated method stub
      return null;
   }

}
