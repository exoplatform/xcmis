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
package org.xcmis.search.content;

import org.xcmis.search.Visitors;
import org.xcmis.search.content.Schema.Column;
import org.xcmis.search.content.Schema.View;
import org.xcmis.search.model.Query;
import org.xcmis.search.model.source.SelectorName;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@exoplatform.org">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z ksm $
 *
 */
public class InMemoryView extends InMemoryTable implements View
{
   private Query definition;

   protected InMemoryView(SelectorName name, Iterable<Column> columns, Query definition)
   {
      super(name, columns);
      this.definition = definition;
   }

   protected InMemoryView(SelectorName name, Iterable<Column> columns, Query definition, Iterable<Column>... keyColumns)
   {
      super(name, columns, keyColumns);
      this.definition = definition;
   }

   /**
    * @see org.xcmis.search.content.Schema.View#getDefinition()
    */
   public Query getDefinition()
   {
      return definition;
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      StringBuilder sb = new StringBuilder(getName().getName());
      sb.append('(');
      boolean first = true;
      for (Column column : getColumns())
      {
         if (first)
         {
            first = false;
         }
         else
         {
            sb.append(", ");
         }
         sb.append(column);
      }
      sb.append(") AS '");
      sb.append(Visitors.readable(definition));
      sb.append('\'');

      return sb.toString();
   }
}
