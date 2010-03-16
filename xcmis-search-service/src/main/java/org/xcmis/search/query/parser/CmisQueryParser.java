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
package org.xcmis.search.query.parser;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.xcmis.search.InvalidQueryException;
import org.xcmis.search.antlr.CMISSQLLexer;
import org.xcmis.search.antlr.CMISSQLParser;
import org.xcmis.search.antlr.CMISSQLTreeWalker;
import org.xcmis.search.model.Query;

/**
 * Parser for Cmis SQL
 */
public class CmisQueryParser implements QueryParser
{

   /**
    * @see org.xcmis.search.query.parser.QueryParser#getLanguage()
    */
   public String getLanguage()
   {
      return "CMIS_SQL";
   }

   /**
    * @see org.xcmis.search.query.parser.QueryParser#parseQuery(java.lang.String)
    */
   public Query parseQuery(String statement) throws InvalidQueryException
   {
      try
      {
         final CMISSQLLexer lexer = new CMISSQLLexer(new ANTLRStringStream(statement));
         final CommonTokenStream tokens = new CommonTokenStream(lexer);
         // process parsing
         final CMISSQLParser parser = new CMISSQLParser(tokens);
         final CMISSQLParser.query_return result = parser.query();
         // check exceptions
         if (lexer.hasExceptions())
         {
            throw new InvalidQueryException(lexer.getExceptionMessage());
         }
         if (parser.hasExceptions())
         {
            throw new InvalidQueryException(parser.getExceptionMessage());
         }

         // process query build
         final CommonTree tree = (CommonTree)result.getTree();
         final CommonTreeNodeStream nodes = new CommonTreeNodeStream(tree);
         final CMISSQLTreeWalker treeWalker = new CMISSQLTreeWalker(nodes);
         return treeWalker.query();
      }
      catch (RecognitionException e)
      {
         throw new InvalidQueryException(e.getLocalizedMessage(), e);
      }
   }

}
