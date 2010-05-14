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
package org.xcmis.search.lucene.content;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.xcmis.search.content.Schema;
import org.xcmis.search.lucene.index.FieldNames;
import org.xcmis.search.value.NameConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@exoplatform.org">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z ksm $
 *
 */
public class SchemaTableResolver implements VirtualTableResolver<Query>
{
   private final NameConverter nameConverter;

   private final Schema schema;

   /**
    * @param nameConverter
    * @param schema
    */
   public SchemaTableResolver(NameConverter nameConverter, Schema schema)
   {
      super();
      this.nameConverter = nameConverter;
      this.schema = schema;
   }

   /**
    * @see org.xcmis.search.lucene.content.VirtualTableResolver#resolve(java.lang.String, boolean)
    */
   public Query resolve(String tableName, boolean includeInheritedTables)
   {
      final List<Term> terms = new ArrayList<Term>();
      Query query = null;

      terms.add(new Term(FieldNames.TABLE_NAME, nameConverter.convertName(tableName)));

      if (includeInheritedTables)
      {
         // now search for all node types that are derived from base
         final Set<String> allTypes = getSubTypes(tableName);
         for (final String descendantNt : allTypes)
         {
            terms.add(new Term(FieldNames.TABLE_NAME, nameConverter.convertName(descendantNt)));
         }
      }

      if (terms.size() == 0)
      {
         // exception occured
         query = new BooleanQuery();
      }
      else if (terms.size() == 1)
      {
         query = new TermQuery(terms.get(0));
      }
      else
      {
         final BooleanQuery b = new BooleanQuery();
         for (final Object element : terms)
         {
            b.add(new TermQuery((Term)element), Occur.SHOULD);
         }
         query = b;
      }
      return query;
   }

   /**
    * @param tableName
    * @return
    */
   protected Set<String> getSubTypes(String tableName)
   {
      return Collections.emptySet();
   }

}
