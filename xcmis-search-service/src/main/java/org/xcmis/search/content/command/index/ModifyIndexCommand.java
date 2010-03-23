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
package org.xcmis.search.content.command.index;

import org.xcmis.search.content.ContentEntry;
import org.xcmis.search.content.command.InvocationContext;
import org.xcmis.search.content.command.VisitableCommand;
import org.xcmis.search.content.interceptors.Visitor;

import java.util.List;
import java.util.Set;

/**
 * Add and remove documents from the index storage.
 */
public class ModifyIndexCommand implements VisitableCommand
{
   private final List<ContentEntry> addedDocuments;

   private final Set<String> deletedDocuments;

   /**
    * @param addedDocuments
    * @param deletedDocuments
    */
   public ModifyIndexCommand(List<ContentEntry> addedDocuments, Set<String> deletedDocuments)
   {
      super();
      this.addedDocuments = addedDocuments;
      this.deletedDocuments = deletedDocuments;
   }

   /**
    * @return the addedDocuments
    */
   public List<ContentEntry> getAddedDocuments()
   {
      return addedDocuments;
   }

   /**
    * @return the deletedDocuments
    */
   public Set<String> getDeletedDocuments()
   {
      return deletedDocuments;
   }

   /**
    * @see org.xcmis.search.content.command.VisitableCommand#acceptVisitor(org.xcmis.search.content.command.InvocationContext, org.xcmis.search.content.interceptors.Visitor)
    */
   public Object acceptVisitor(InvocationContext ctx, Visitor visitor) throws Throwable
   {
      return visitor.visitModifyIndexCommand(ctx, this);
   }

}
