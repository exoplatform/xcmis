package org.xcmis.search.parser;

import org.junit.Before;
import org.junit.Test;
import org.xcmis.search.InvalidQueryException;
import org.xcmis.search.model.source.SelectorName;
import org.xcmis.search.parser.CmisQueryParser;

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

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@exoplatform.org">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z ksm $
 *
 */
public class CmisSqlQueryParserTest
{

   private CmisQueryParser parser;

   @Before
   public void beforeEach()
   {
      parser = new CmisQueryParser();
   }

   // ----------------------------------------------------------------------------------------------------------------
   // parseQuery
   // ----------------------------------------------------------------------------------------------------------------

   @Test
   public void shouldParseNominalQueries() throws InvalidQueryException
   {
      parse("SELECT * FROM tableA");
      parse("SELECT column1 FROM tableA");
      parse("SELECT tableA.column1 FROM tableA");
      parse("SELECT tableA.column1, tableB.column2 FROM tableA JOIN tableB ON (tableA.id = tableB.id)");
      parse("SELECT tableA.column1, tableB.column2 FROM tableA INNER JOIN tableB ON (tableA.id = tableB.id)");
      //parse("SELECT tableA.column1, tableB.column2 FROM tableA OUTER JOIN tableB ON (tableA.id = tableB.id)");
      parse("SELECT tableA.column1, tableB.column2 FROM tableA LEFT OUTER JOIN tableB ON (tableA.id = tableB.id)");
      //parse("SELECT tableA.column1, tableB.column2 FROM tableA RIGHT OUTER JOIN tableB ON (tableA.id = tableB.id)");
   }

   @Test
   public void shouldParseQueriesWithNonSqlColumnNames() throws InvalidQueryException
   {
      parse("SELECT * FROM [exo:tableA]");
      parse("SELECT [jcr:column1] FROM [exo:tableA]");
      //parse("SELECT 'jcr:column1' FROM 'exo:tableA'");
      //parse("SELECT \"jcr:column1\" FROM \"exo:tableA\"");
   }

   @Test
   public void shouldParseQueriesSelectingFromAllTables() throws InvalidQueryException
   {
      parse("SELECT * FROM nt:base");
   }

   @Test(expected = InvalidQueryException.class)
   public void shouldFailToParseQueriesWithNoFromClause() throws InvalidQueryException
   {
      parse("SELECT 'jcr:column1'");
   }

   @Test(expected = InvalidQueryException.class)
   public void shouldFailToParseQueriesWithIncompleteFromClause() throws InvalidQueryException
   {
      parse("SELECT 'jcr:column1' FROM  ");
   }

   @Test(expected = InvalidQueryException.class)
   public void shouldFailToParseQueriesWithUnmatchedSingleQuoteCharacters() throws InvalidQueryException
   {
      parse("SELECT 'jcr:column1' FROM \"exo:tableA'");
   }

   @Test(expected = InvalidQueryException.class)
   public void shouldFailToParseQueriesWithUnmatchedDoubleQuoteCharacters() throws InvalidQueryException
   {
      parse("SELECT \"jcr:column1' FROM \"exo:tableA\"");
   }

   @Test(expected = InvalidQueryException.class)
   public void shouldFailToParseQueriesWithUnmatchedBracketQuoteCharacters() throws InvalidQueryException
   {
      parse("SELECT [jcr:column1' FROM [exo:tableA]");
   }

   @Test
   public void shouldParseQueriesWithSelectStar() throws InvalidQueryException
   {
      parse("SELECT * FROM tableA");
      parse("SELECT tableA.* FROM tableA");
      parse("SELECT tableA.column1, tableB.* FROM tableA JOIN tableB ON (tableA.id = tableB.id)");
      parse("SELECT tableA.*, tableB.column2 FROM tableA JOIN tableB ON (tableA.id = tableB.id)");
      parse("SELECT tableA.*, tableB.* FROM tableA JOIN tableB ON (tableA.id = tableB.id)");
   }

   @Test
   public void shouldParseQueriesWithAllKindsOfJoins() throws InvalidQueryException
   {
      parse("SELECT tableA.column1, tableB.column2 FROM tableA JOIN tableB ON (tableA.id = tableB.id)");
      parse("SELECT tableA.column1, tableB.column2 FROM tableA INNER JOIN tableB ON (tableA.id = tableB.id)");
      //parse("SELECT tableA.column1, tableB.column2 FROM tableA OUTER JOIN tableB ON (tableA.id = tableB.id)");
      parse("SELECT tableA.column1, tableB.column2 FROM tableA LEFT OUTER JOIN tableB ON (tableA.id = tableB.id)");
      //parse("SELECT tableA.column1, tableB.column2 FROM tableA RIGHT OUTER JOIN tableB ON (tableA.id = tableB.id)");
      //parse("SELECT tableA.column1, tableB.column2 FROM tableA FULL OUTER JOIN tableB ON (tableA.id = tableB.id)");
      //parse("SELECT tableA.column1, tableB.column2 FROM tableA CROSS JOIN tableB ON (tableA.id = tableB.id)");
   }

   @Test
   public void shouldParseQueriesWithMultipleJoins() throws InvalidQueryException
   {
      parse("SELECT * FROM tableA JOIN tableB ON (tableA.id = tableB.id)");
      parse("SELECT * FROM tableA JOIN tableB ON (tableA.id = tableB.id) JOIN tableC ON (tableA.id2 = tableC.id2)");
   }

   @Test
   public void shouldParseQueriesWithEquiJoinCriteria() throws InvalidQueryException
   {
      parse("SELECT tableA.column1, tableB.column2 FROM tableA JOIN tableB ON (tableA.id = tableB.id)");
      parse("SELECT * FROM tableA JOIN tableB ON (tableA.id = tableB.id) JOIN tableC ON (tableA.id2 = tableC.id2)");
   }

   @Test(expected = InvalidQueryException.class)
   public void shouldFailToParseEquiJoinCriteriaMissingPropertyName() throws InvalidQueryException
   {
      parse("SELECT tableA.column1, tableB.column2 FROM tableA JOIN tableB ON tableA = tableB.id");
   }

   @Test(expected = InvalidQueryException.class)
   public void shouldFailToParseEquiJoinCriteriaMissingTableName() throws InvalidQueryException
   {
      parse("SELECT tableA.column1, tableB.column2 FROM tableA JOIN tableB ON column1 = tableB.id");
   }

   @Test(expected = InvalidQueryException.class)
   public void shouldFailToParseEquiJoinCriteriaMissingEquals() throws InvalidQueryException
   {
      parse("SELECT tableA.column1, tableB.column2 FROM tableA JOIN tableB ON column1 tableB.id");
   }

   @Test
   public void shouldParseQueriesOnMultpleLines() throws InvalidQueryException
   {
      parse("SELECT * \nFROM tableA");
      parse("SELECT \ncolumn1 \nFROM tableA");
      parse("SELECT \ntableA.column1 \nFROM\n tableA");
      parse("SELECT tableA.\ncolumn1, \ntableB.column2 \nFROM tableA JOIN \ntableB ON (tableA.id \n= tableB.\nid)");
   }

   @Test
   public void shouldParseQueriesThatUseDifferentCaseForKeywords() throws InvalidQueryException
   {
      parse("select * from tableA");
      parse("SeLeCt * from tableA");
      parse("select column1 from tableA");
      parse("select tableA.column1 from tableA");
      parse("select tableA.column1, tableB.column2 from tableA join tableB on (tableA.id = tableB.id)");
   }

   // ----------------------------------------------------------------------------------------------------------------
   // parseSetQuery
   // ----------------------------------------------------------------------------------------------------------------

   // ----------------------------------------------------------------------------------------------------------------
   // parseSelect
   // ----------------------------------------------------------------------------------------------------------------

   // ----------------------------------------------------------------------------------------------------------------
   // parseFrom
   // ----------------------------------------------------------------------------------------------------------------

   // ----------------------------------------------------------------------------------------------------------------
   // parseJoinCondition
   // ----------------------------------------------------------------------------------------------------------------

   // ----------------------------------------------------------------------------------------------------------------
   // parseWhere
   // ----------------------------------------------------------------------------------------------------------------

   // ----------------------------------------------------------------------------------------------------------------
   // parseConstraint
   // ----------------------------------------------------------------------------------------------------------------

   // ----------------------------------------------------------------------------------------------------------------
   // Utility methods
   // ----------------------------------------------------------------------------------------------------------------

   protected void parse(String query) throws InvalidQueryException
   {
      parser.parseQuery(query);
   }

   protected SelectorName selectorName(String name)
   {
      return new SelectorName(name);
   }

}
