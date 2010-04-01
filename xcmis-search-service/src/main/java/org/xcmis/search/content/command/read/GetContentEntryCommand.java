/*
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.xcmis.search.content.command.read;

import org.xcmis.search.content.command.InvocationContext;
import org.xcmis.search.content.interceptors.Visitor;

/**
 *  Add node command
 */
public class GetContentEntryCommand implements AbstractReadDataCommand
{
   /**
    * Content entry unique identifier.
    */
   private final String entryUuid;

   /**
    * @param entryUuid
    */
   public GetContentEntryCommand(String entryUuid)
   {
      super();
      this.entryUuid = entryUuid;
   }

   /**
    * @see org.exoplatform.services.jcr.impl.storage.command.JcrCommand#acceptVisitor(org.exoplatform.services.jcr.impl.storage.command.JcrInvocationContext, org.jboss.cache.commands.Visitor)
    */
   public Object acceptVisitor(InvocationContext ctx, Visitor visitor) throws Throwable
   {
      return visitor.visitGetContentEntryCommand(ctx, this);
   }

   /**
    * @return the entryUuid
    */
   public String getEntryUuid()
   {
      return entryUuid;
   }
}
