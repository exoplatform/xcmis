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
package org.xcmis.search.lucene.search;

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FilteredTermEnum;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.util.ToStringUtils;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: CaseInsensitiveTermQuery.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class CaseInsensitiveTermQuery extends MultiTermQuery
{

   /** The serialVersionUID. */
   private static final long serialVersionUID = 8733959357848332771L;

   protected Term term;

   public CaseInsensitiveTermQuery(Term term)
   {
     this.term = term;
   }

   /**
    * Returns the pattern term.
    */
   public Term getTerm() {
     return term;
   }
   
   @Override
   protected FilteredTermEnum getEnum(IndexReader reader) throws IOException
   {
      return new CaseInsensitiveTermEnum(reader, term);
   }

   private class CaseInsensitiveTermEnum extends FilteredTermEnum
   {

      private String field;

      private String text;

      private boolean endEnum;

      /**
       * @throws IOException
       */
      public CaseInsensitiveTermEnum(IndexReader reader, Term term) throws IOException
      {
         super();
         this.field = term.field();
         this.text = term.text().toLowerCase();
         setEnum(reader.terms(new Term(term.field(), term.text().toUpperCase())));
      }

      @Override
      public void close() throws IOException
      {
         super.close();
         field = null;
         text = null;
      }

      @Override
      public float difference()
      {
         return 1.0f;
      }

      @Override
      protected boolean endEnum()
      {
         return endEnum;
      }

      @Override
      protected boolean termCompare(Term termCompare)
      {
         if (field == termCompare.field())
         {
            String searchText = termCompare.text();
            return text.equals(searchText.toLowerCase());
         }
         endEnum = true;
         return false;
      }
   }

  @Override
  public String toString(String field) {
    StringBuffer buffer = new StringBuffer();
    if (term != null) {
      if (!term.field().equals(field)) {
        buffer.append(term.field());
        buffer.append(":");
      }
      buffer.append(term.text());
    } else {
      buffer.append("termPattern:unknown");
    }
    buffer.append(ToStringUtils.boost(getBoost()));
    return buffer.toString();
  };
}
