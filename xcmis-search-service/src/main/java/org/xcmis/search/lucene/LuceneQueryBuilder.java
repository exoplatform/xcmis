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
package org.xcmis.search.lucene;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.search.regex.RegexQuery;
import org.apache.lucene.util.NumericUtils;
import org.apache.lucene.util.Version;
import org.xcmis.search.InvalidQueryException;
import org.xcmis.search.QueryObjectModelVisitor;
import org.xcmis.search.VisitException;
import org.xcmis.search.Visitors;
import org.xcmis.search.antlr.FullTextLexer;
import org.xcmis.search.antlr.FullTextParser;
import org.xcmis.search.config.IndexConfiguration;
import org.xcmis.search.lucene.content.ErrorReporterImpl;
import org.xcmis.search.lucene.index.ExtendedNumberTools;
import org.xcmis.search.lucene.index.FieldNames;
import org.xcmis.search.lucene.index.IndexException;
import org.xcmis.search.lucene.search.CaseInsensitiveRangeQuery;
import org.xcmis.search.lucene.search.CaseInsensitiveRegexCapImpl;
import org.xcmis.search.lucene.search.CaseInsensitiveTermQuery;
import org.xcmis.search.lucene.search.ChildTraversingQueryNode;
import org.xcmis.search.lucene.search.DescendantQueryNode;
import org.xcmis.search.model.Limit;
import org.xcmis.search.model.column.Column;
import org.xcmis.search.model.constraint.And;
import org.xcmis.search.model.constraint.ChildNode;
import org.xcmis.search.model.constraint.Comparison;
import org.xcmis.search.model.constraint.DescendantNode;
import org.xcmis.search.model.constraint.FullTextSearch;
import org.xcmis.search.model.constraint.Not;
import org.xcmis.search.model.constraint.Operator;
import org.xcmis.search.model.constraint.Or;
import org.xcmis.search.model.constraint.PropertyExistence;
import org.xcmis.search.model.constraint.SameNode;
import org.xcmis.search.model.operand.BindVariableName;
import org.xcmis.search.model.operand.FullTextSearchScore;
import org.xcmis.search.model.operand.Length;
import org.xcmis.search.model.operand.Literal;
import org.xcmis.search.model.operand.LowerCase;
import org.xcmis.search.model.operand.NodeDepth;
import org.xcmis.search.model.operand.NodeLocalName;
import org.xcmis.search.model.operand.NodeName;
import org.xcmis.search.model.operand.PropertyValue;
import org.xcmis.search.model.operand.UpperCase;
import org.xcmis.search.model.ordering.Ordering;
import org.xcmis.search.model.source.Join;
import org.xcmis.search.model.source.Selector;
import org.xcmis.search.model.source.join.ChildNodeJoinCondition;
import org.xcmis.search.model.source.join.DescendantNodeJoinCondition;
import org.xcmis.search.model.source.join.EquiJoinCondition;
import org.xcmis.search.model.source.join.SameNodeJoinCondition;
import org.xcmis.search.value.NameConverter;
import org.xcmis.search.value.PathSplitter;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z
 *          aheritier $
 * 
 */
public class LuceneQueryBuilder implements QueryObjectModelVisitor
{
   public static final char LIKE_ESCAPE_CHAR = '\\';

   public static final char LIKE_MATCH_ONE_CHAR = '_';

   public static final char LIKE_MATCH_ZERO_OR_MORE_CHAR = '%';

   private Stack<Object> queryBuilderStack;

   private Map<String, Object> bindVariablesValues;

   private final NameConverter nameConverter;

   private final PathSplitter pathSplitter;

   private final Pattern fullTextFieldNamePattern = Pattern.compile("^(.*:FULL|FULL):.*$");

   /**
    * Lucene index reader.
    */
   private IndexReader indexReader;

   private final IndexConfiguration indexConfiguration;

   /**
    * @param indexReader 
    * @param nameConverter 
    * @param pathSplitter
    * @param bindVariablesValues
    * @param indexConfiguration
    */
   public LuceneQueryBuilder(IndexReader indexReader, NameConverter<?> nameConverter, PathSplitter<?> pathSplitter,
      Map<String, Object> bindVariablesValues, IndexConfiguration indexConfiguration)
   {
      this.indexConfiguration = indexConfiguration;
      Validate.notNull(indexReader, "The indexReader argument may not be null");

      this.indexReader = indexReader;
      this.nameConverter = nameConverter;
      this.pathSplitter = pathSplitter;
      this.bindVariablesValues = bindVariablesValues;
      this.queryBuilderStack = new Stack<Object>();
   }

   /**
    * 
    * @return Return lucene query.
    */
   public Query getQuery()
   {
      Query result = (Query)queryBuilderStack.pop();
      this.queryBuilderStack = new Stack<Object>();
      return result;
   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.constraint.And)
    */

   public void visit(And node) throws VisitException
   {
      // TODO check selector size;
      // final int selectors = node.getSelectorsNames().size();
      final int selectors = 1;

      if (selectors == 1)
      {
         // Operators will push query to stack
         Visitors.visit(node.getLeft(), this);
         Visitors.visit(node.getRight(), this);

         BooleanQuery booleanQuery = new BooleanQuery();

         booleanQuery.add((Query)queryBuilderStack.pop(), BooleanClause.Occur.MUST);
         booleanQuery.add((Query)queryBuilderStack.pop(), BooleanClause.Occur.MUST);

         queryBuilderStack.push(booleanQuery);
      }
      else
      {
         //TODO check
         throw new UnsupportedOperationException("More then one selector used");
      }

   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.operand.BindVariableName)
    */

   public void visit(BindVariableName node) throws VisitException
   {
      final Object variableValue = bindVariablesValues.get(nameConverter.convertName(node.getVariableName()));
      if (variableValue == null)
      {
         throw new VisitException("No value bound for " + node.getVariableName());
      }
      queryBuilderStack.push(variableValue);

   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.constraint.ChildNode)
    */

   public void visit(ChildNode node) throws VisitException
   {
      String parentPath = node.getParentPath();
      if (parentPath.charAt(0) == '[')
      {
         //uuid based absolute path
         Query parentQuery = new TermQuery(new Term(FieldNames.UUID, parentPath.substring(1, parentPath.length() - 1)));
         Query childNodeQuery = new ChildTraversingQueryNode(parentQuery, false);
         queryBuilderStack.push(childNodeQuery);

      }
      else
      {

         final Object[] entries = pathSplitter.splitPath(parentPath);
         if (entries.length > 0)
         {
            Query childNodeQuery = null;
            for (int i = 0; i < entries.length; i++)
            {
               if (i == 0)
               {
                  childNodeQuery = new TermQuery(new Term(FieldNames.UUID, indexConfiguration.getRootUuid()));
               }
               else
               {
                  final String stepName = nameConverter.convertName(entries[i]);
                  final Query nameQuery = new TermQuery(new Term(FieldNames.LABEL, stepName));
                  childNodeQuery = new DescendantQueryNode(nameQuery, childNodeQuery);
               }
            }

            // all child
            childNodeQuery = new ChildTraversingQueryNode(childNodeQuery, false);
            queryBuilderStack.push(childNodeQuery);
         }
         else
         {
            queryBuilderStack.push(new MatchAllDocsQuery());
         }
      }

   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.source.join.ChildNodeJoinCondition)
    */

   public void visit(ChildNodeJoinCondition node) throws VisitException
   {
   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.column.Column)
    */

   public void visit(Column node) throws VisitException
   {

   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.constraint.Comparison)
    */

   public void visit(Comparison node) throws VisitException
   {
      // Push static value to stack
      Visitors.visit(node.getOperand2(), this);
      // push operator to stack
      queryBuilderStack.push(node.getOperator());
      // push ignore case flag
      queryBuilderStack.push(new Boolean(false));
      // Push query from DynamicOperand to stack
      Visitors.visit(node.getOperand1(), this);

   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.constraint.DescendantNode)
    */

   public void visit(DescendantNode node) throws VisitException
   {
      String parentPath = node.getAncestorPath();
      if (parentPath.charAt(0) == '[')
      {
         //uuid based absolute path
         Query parentQuery = new TermQuery(new Term(FieldNames.UUID, parentPath.substring(1, parentPath.length() - 1)));
         Query childNodeQuery = new ChildTraversingQueryNode(parentQuery, true);
         queryBuilderStack.push(childNodeQuery);
      }
      else
      {
         final Object[] entries = pathSplitter.splitPath(parentPath);

         Query descendantQuery = new TermQuery(new Term(FieldNames.UUID, indexConfiguration.getRootUuid()));

         for (int i = 1; i < entries.length; i++)
         {
            final String stepName = nameConverter.convertName(entries[i]);
            final Query nameQuery = new TermQuery(new Term(FieldNames.LABEL, stepName));
            descendantQuery = new DescendantQueryNode(nameQuery, descendantQuery);
         }
         // all childs

         descendantQuery = new ChildTraversingQueryNode(descendantQuery, true);

         queryBuilderStack.push(descendantQuery);
      }
   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.source.join.DescendantNodeJoinCondition)
    */

   public void visit(DescendantNodeJoinCondition node) throws VisitException
   {

   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.source.join.EquiJoinCondition)
    */

   public void visit(EquiJoinCondition node) throws VisitException
   {

   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.constraint.FullTextSearch)
    */

   public void visit(FullTextSearch node) throws VisitException
   {
      // TODO selector unused, research it
      // TODO remove to string
      final CharStream input = new ANTLRStringStream(node.getFullTextSearchExpression().toString());

      final FullTextLexer lexer = new FullTextLexer(input);
      final CommonTokenStream tokens = new CommonTokenStream(lexer);
      final FullTextParser parser = new FullTextParser(tokens);

      final ErrorReporterImpl reporter = new ErrorReporterImpl();
      lexer.setErrorReporter(reporter);
      parser.setErrorReporter(reporter);

      final List<String> fields = new ArrayList<String>();
      // search by specific field
      if (node.getPropertyName() != null)
      {
         fields.add(FieldNames.createFullTextFieldName(node.getPropertyName()));
      }
      else
      {
         // search by all full text fields
         Set<String> names;
         try
         {
            names = getFieldNames();
         }
         catch (IndexException e)
         {
            throw new VisitException(e.getLocalizedMessage());
         }
         for (final String fieldName : names)
         {
            final Matcher matcher = fullTextFieldNamePattern.matcher(fieldName);
            if (matcher.matches())
            {
               fields.add(fieldName);
            }
         }
      }

      Query query = null;
      try
      {
         parser.fulltext(fields, new StandardAnalyzer(Version.LUCENE_35));
         query = parser.getQuery();
         final InvalidQueryException ex = reporter.getException();
         if (ex != null)
         {
            throw new VisitException(ex.getLocalizedMessage());
         }

      }
      catch (final RecognitionException e)
      {
         throw new VisitException(e.getMessage());
      }

      queryBuilderStack.push(query);

   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.operand.FullTextSearchScore)
    */

   public void visit(FullTextSearchScore node) throws VisitException
   {
      throw new UnsupportedOperationException("FulltextSearchScore is unsupported operation.");
   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.source.Join)
    */

   public void visit(Join node) throws VisitException
   {

   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.operand.Length)
    */

   public void visit(Length node) throws VisitException
   {
      Validate.isTrue(queryBuilderStack.peek() instanceof Boolean, "Stack should contains caseInsensitiveSearch flag");
      boolean caseInsensitiveSearch = (Boolean)queryBuilderStack.pop();

      Validate.isTrue(queryBuilderStack.peek() instanceof Operator, "Stack should contains comparation operator ");
      Operator operator = (Operator)queryBuilderStack.pop();

      Validate.isTrue(queryBuilderStack.peek() instanceof Long, "Invalid literal type, should be long. But found "
         + queryBuilderStack.peek().getClass().getCanonicalName());

      Long staticLongValue = (Long)queryBuilderStack.pop();
      String value = NumericUtils.longToPrefixCoded(staticLongValue);
      String propertyField = FieldNames.createFieldLengthName(node.getPropertyValue().getPropertyName());

      Term lengthTerm = new Term(propertyField, value);
      switch (operator)
      {
         case EQUAL_TO :
            queryBuilderStack.push(new TermQuery(lengthTerm));
            break;
         case NOT_EQUAL_TO :
            final BooleanQuery booleanQuery = new BooleanQuery();

            // property exists
            booleanQuery.add(new TermQuery(new Term(FieldNames.PROPERTIES_SET, node.getPropertyValue()
               .getPropertyName())), BooleanClause.Occur.SHOULD);

            booleanQuery.add(new TermQuery(lengthTerm), BooleanClause.Occur.MUST_NOT);
            queryBuilderStack.push(booleanQuery);
            break;
         case GREATER_THAN :
            queryBuilderStack.push(new TermRangeQuery(lengthTerm.field(), lengthTerm.text(), null, false, false));
            break;
         case GREATER_THAN_OR_EQUAL_TO :
            queryBuilderStack.push(new TermRangeQuery(lengthTerm.field(), lengthTerm.text(), null, true, true));
            break;
         case LESS_THAN :
            queryBuilderStack.push(new TermRangeQuery(lengthTerm.field(), null, lengthTerm.text(), false, false));
            break;
         case LESS_THAN_OR_EQUAL_TO :
            queryBuilderStack.push(new TermRangeQuery(lengthTerm.field(), null, lengthTerm.text(), true, true));
            break;
         case LIKE :
            throw new VisitException("Unsupported operation for Length operator");
         default :
            throw new VisitException("Invalid operator " + operator);
      }

   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.Limit)
    */

   public void visit(Limit limit) throws VisitException
   {

   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.operand.Literal)
    */

   public void visit(Literal node) throws VisitException
   {
      queryBuilderStack.push(node.getValue());
   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.operand.LowerCase)
    */

   public void visit(LowerCase node) throws VisitException
   {
      Validate.isTrue(queryBuilderStack.peek() instanceof Boolean, "Stack should contains caseInsensitiveSearch flag");
      boolean caseInsensitiveSearch = (Boolean)queryBuilderStack.pop();

      final String value = (String)queryBuilderStack.peek();
      if (!caseInsensitiveSearch && !StringUtils.isAllLowerCase(value))
      {
         // search nothing because static value in different case
         queryBuilderStack.push(new BooleanQuery());
      }

      queryBuilderStack.push(new Boolean(true));
      // push dynamic query to stack;
      Visitors.visit(node.getOperand(), this);
   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.operand.NodeDepth)
    */

   public void visit(NodeDepth depth) throws VisitException
   {

   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.operand.NodeLocalName)
    */

   public void visit(NodeLocalName node) throws VisitException
   {
      Validate.isTrue(queryBuilderStack.peek() instanceof Boolean, "Stack should contains caseInsensitiveSearch flag");
      boolean caseInsensitiveSearch = (Boolean)queryBuilderStack.pop();

      Validate.isTrue(queryBuilderStack.peek() instanceof Operator, "Stack should contains comparation operator ");
      Operator operator = (Operator)queryBuilderStack.pop();

      Validate.isTrue(queryBuilderStack.peek() instanceof String, "Stack should contains static value. But found "
         + queryBuilderStack.peek().getClass().getCanonicalName());
      String staticStingValue = (String)queryBuilderStack.pop();

      Term staticValueTerm = new Term(FieldNames.LABEL, staticStingValue);

      switch (operator)
      {
         case EQUAL_TO :

            if (caseInsensitiveSearch)
            {
               throw new VisitException("Unsupported operation of caseinsensetive search and NodeLocalName");
            }
            final BooleanQuery equalToQuery = new BooleanQuery();
            equalToQuery.add(new WildcardQuery(new Term(FieldNames.LABEL, "*?:" + staticStingValue)),
               BooleanClause.Occur.SHOULD);
            equalToQuery.add(new TermQuery(staticValueTerm), BooleanClause.Occur.SHOULD);
            queryBuilderStack.push(equalToQuery);

            break;
         case NOT_EQUAL_TO :
            if (caseInsensitiveSearch)
            {
               throw new VisitException("Unsupported operation of caseinsensetive search and NodeLocalName");
            }
            final BooleanQuery notEqualToQuery = new BooleanQuery();
            // property exists
            notEqualToQuery.add(new MatchAllDocsQuery(), BooleanClause.Occur.SHOULD);
            final BooleanQuery q = new BooleanQuery();
            q.add(new WildcardQuery(new Term(FieldNames.LABEL, "*?:" + staticStingValue)), BooleanClause.Occur.SHOULD);
            q.add(new TermQuery(new Term(FieldNames.LABEL, staticStingValue)), BooleanClause.Occur.SHOULD);

            notEqualToQuery.add(q, BooleanClause.Occur.MUST_NOT);

            queryBuilderStack.push(notEqualToQuery);
            break;
         case GREATER_THAN :
         case GREATER_THAN_OR_EQUAL_TO :
         case LESS_THAN :
         case LESS_THAN_OR_EQUAL_TO :
            throw new VisitException("Unsupported comparation :" + operator.toString() + " and NodeLocalName");

         case LIKE :

            final String likeExpression = staticStingValue;
            Query likeQuery = null;
            if (likeExpression.equals("%"))
            {
               // property exists
               likeQuery = new MatchAllDocsQuery();
            }
            else
            {
               final String term = "(.+:)?" + likePatternToRegex(likeExpression);
               likeQuery = new RegexQuery(new Term(FieldNames.LABEL, term));
               if (caseInsensitiveSearch)
               {
                  ((RegexQuery)likeQuery).setRegexImplementation(new CaseInsensitiveRegexCapImpl());
               }
            }
            queryBuilderStack.push(likeQuery);

            break;
         default :
            throw new VisitException("Invalid operator " + operator);
      }

   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.operand.NodeName)
    */

   public void visit(NodeName node) throws VisitException
   {
      Validate.isTrue(queryBuilderStack.peek() instanceof Boolean, "Stack should contains caseInsensitiveSearch flag");
      boolean caseInsensitiveSearch = (Boolean)queryBuilderStack.pop();

      Validate.isTrue(queryBuilderStack.peek() instanceof Operator, "Stack should contains comparation operator ");
      Operator operator = (Operator)queryBuilderStack.pop();

      Validate.isTrue(queryBuilderStack.peek() instanceof String, "Stack should contains static value. But found "
         + queryBuilderStack.peek().getClass().getCanonicalName());
      String staticStingValue = (String)queryBuilderStack.pop();

      Term staticValueTerm = new Term(FieldNames.LABEL, staticStingValue);

      switch (operator)
      {
         case EQUAL_TO :
            if (caseInsensitiveSearch)
            {
               queryBuilderStack.push(new CaseInsensitiveTermQuery(new Term(FieldNames.LABEL, staticStingValue
                  .toLowerCase())));
            }
            else
            {
               queryBuilderStack.push(new TermQuery(staticValueTerm));
            }
            break;
         case NOT_EQUAL_TO :
            final BooleanQuery booleanQuery = new BooleanQuery();
            // property exists
            booleanQuery.add(new MatchAllDocsQuery(), BooleanClause.Occur.SHOULD);
            // property not equal to
            if (caseInsensitiveSearch)
            {
               booleanQuery.add(new CaseInsensitiveTermQuery(staticValueTerm), BooleanClause.Occur.MUST_NOT);

            }
            else
            {
               booleanQuery.add(new TermQuery(staticValueTerm), BooleanClause.Occur.MUST_NOT);
            }
            queryBuilderStack.push(booleanQuery);
            break;
         case GREATER_THAN :
            if (caseInsensitiveSearch)
            {
               queryBuilderStack.push(new CaseInsensitiveRangeQuery(staticValueTerm.field(), 
                       staticValueTerm.text().toLowerCase(), null, false, false));
            }
            else
            {
               queryBuilderStack.push(new TermRangeQuery(staticValueTerm.field(), 
                       staticValueTerm.text().toLowerCase(), null, false, false));
            }
            break;
         case GREATER_THAN_OR_EQUAL_TO :

            if (caseInsensitiveSearch)
            {
               queryBuilderStack.push(new CaseInsensitiveRangeQuery(staticValueTerm.field(), 
                       staticValueTerm.text().toLowerCase(), null, true, true));
            }
            else
            {
               queryBuilderStack.push(new TermRangeQuery(staticValueTerm.field(), 
                       staticValueTerm.text().toLowerCase(), null, true, true));
            }
            break;
         case LESS_THAN :
            if (caseInsensitiveSearch)
            {
               queryBuilderStack.push(new CaseInsensitiveRangeQuery(staticValueTerm.field(),
                       null, staticValueTerm.text().toUpperCase(), false, false));
            }
            else
            {
               queryBuilderStack.push(new TermRangeQuery(staticValueTerm.field(), 
                       null, staticValueTerm.text().toUpperCase(), false, false));
            }
            break;
         case LESS_THAN_OR_EQUAL_TO :
            if (caseInsensitiveSearch)
            {
               queryBuilderStack.push(new CaseInsensitiveRangeQuery(staticValueTerm.field(), 
                       null, staticValueTerm.text().toUpperCase(), true, true));
            }
            else
            {
               queryBuilderStack.push(new TermRangeQuery(staticValueTerm.field(), 
                       null, staticValueTerm.text().toUpperCase(), true, true));
            }
            break;
         case LIKE :

            final String likeExpression = staticStingValue;
            if (likeExpression.equals("%"))
            {
               // property exists
               queryBuilderStack.push(new MatchAllDocsQuery());
            }
            else
            {
               final String term = likePatternToRegex(likeExpression);
               RegexQuery query = new RegexQuery(new Term(FieldNames.LABEL, term));
               if (caseInsensitiveSearch)
               {
                  (query).setRegexImplementation(new CaseInsensitiveRegexCapImpl());
               }
               queryBuilderStack.push(query);
            }

            break;
         default :
            throw new VisitException("Invalid operator " + operator);
      }
   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.constraint.Not)
    */

   public void visit(Not node) throws VisitException
   {
      // Push query from Constraint to stack
      Visitors.visit(node.getConstraint(), this);

      final BooleanQuery resultQuery = new BooleanQuery();
      // get query builded by Constraint.
      resultQuery.add((Query)queryBuilderStack.pop(), Occur.MUST_NOT);
      // combine with previous
      if (queryBuilderStack.size() > 0)
      {
         resultQuery.add((Query)queryBuilderStack.pop(), Occur.MUST);
      }
      else
      {
         // TODO optimize by adding initial query
         resultQuery.add(new MatchAllDocsQuery(), Occur.MUST);
      }

      queryBuilderStack.push(resultQuery);

   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.constraint.Or)
    */

   public void visit(Or node) throws VisitException
   {
      // Push query from left constraint to stack
      Visitors.visit(node.getLeft(), this);
      // Push query from right constraint to stack
      Visitors.visit(node.getRight(), this);

      final BooleanQuery resultQuery = new BooleanQuery();
      // get query builded by left constraint.
      resultQuery.add((Query)queryBuilderStack.pop(), BooleanClause.Occur.SHOULD);
      // get query builded by right constraint.
      resultQuery.add((Query)queryBuilderStack.pop(), BooleanClause.Occur.SHOULD);

      queryBuilderStack.push(resultQuery);

   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.ordering.Ordering)
    */

   public void visit(Ordering node) throws VisitException
   {

   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.constraint.PropertyExistence)
    */

   public void visit(PropertyExistence node) throws VisitException
   {
      queryBuilderStack.push(new TermQuery(new Term(FieldNames.PROPERTIES_SET, node.getPropertyName())));

   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.operand.PropertyValue)
    */

   public void visit(PropertyValue node) throws VisitException
   {
      Validate.isTrue(queryBuilderStack.peek() instanceof Boolean, "Stack should contains caseInsensitiveSearch flag");
      boolean caseInsensitiveSearch = (Boolean)queryBuilderStack.pop();

      Validate.isTrue(queryBuilderStack.peek() instanceof Operator, "Stack should contains comparation operator ");
      Operator operator = (Operator)queryBuilderStack.pop();
      Object staticValue = queryBuilderStack.peek();
      Validate.isTrue((staticValue instanceof String || staticValue instanceof Double || staticValue instanceof Long
         || staticValue instanceof Calendar || staticValue instanceof Boolean),
         "Stack should contains static value. But found " + queryBuilderStack.peek().getClass().getCanonicalName());
      staticValue = queryBuilderStack.pop();

      String staticStingValue = null;
      //convert static value to string
      //TODO check cast system
      if (staticValue instanceof String)
      {
         staticStingValue = (String)staticValue;
      }
      else if (staticValue instanceof Double)
      {
         staticStingValue = ExtendedNumberTools.doubleToString((Double)staticValue);
      }
      else if (staticValue instanceof Long)
      {
         staticStingValue = NumericUtils.longToPrefixCoded((Long)staticValue);
      }
      else if (staticValue instanceof Calendar)
      {
         staticStingValue = DateTools.dateToString(((Calendar)staticValue).getTime(), DateTools.Resolution.MILLISECOND);
      }
      else if (staticValue instanceof Boolean)
      {
         staticStingValue = staticValue.toString();
      }

      Term propertyValueTerm = new Term(FieldNames.createPropertyFieldName(node.getPropertyName()), staticStingValue);
      TermQuery propertyValueQuery = new TermQuery(propertyValueTerm);
      Term maxFildValue = new Term(FieldNames.createPropertyFieldName(node.getPropertyName()), "\uFFFF");
      switch (operator)
      {
         case EQUAL_TO :
            if (caseInsensitiveSearch)
            {
               queryBuilderStack.push(new CaseInsensitiveTermQuery(propertyValueTerm));
            }
            else
            {
               queryBuilderStack.push(propertyValueQuery);
            }
            break;
         case NOT_EQUAL_TO :
            final BooleanQuery notEqualQuery = new BooleanQuery();

            // property exists
            notEqualQuery.add(new TermQuery(new Term(FieldNames.PROPERTIES_SET, node.getPropertyName())),
               BooleanClause.Occur.SHOULD);
            // property not equal to
            if (caseInsensitiveSearch)
            {
               notEqualQuery.add(new CaseInsensitiveTermQuery(propertyValueTerm), BooleanClause.Occur.MUST_NOT);
            }
            else
            {
               notEqualQuery.add(propertyValueQuery, BooleanClause.Occur.MUST_NOT);
            }

            queryBuilderStack.push(notEqualQuery);
            break;
         case GREATER_THAN :
            if (caseInsensitiveSearch)
            {
               queryBuilderStack.push(new CaseInsensitiveRangeQuery(FieldNames.createPropertyFieldName(node.getPropertyName()), 
                       propertyValueTerm.text(), maxFildValue.text(), false, false));
            }
            else
            {
               queryBuilderStack.push(new TermRangeQuery(FieldNames.createPropertyFieldName(node.getPropertyName()), 
                       propertyValueTerm.text(), maxFildValue.text(), false, false));
            }
            break;
         case GREATER_THAN_OR_EQUAL_TO :

            if (caseInsensitiveSearch)
            {
               queryBuilderStack.push(new CaseInsensitiveRangeQuery(FieldNames.createPropertyFieldName(node.getPropertyName()), 
                       propertyValueTerm.text(), maxFildValue.text(), true, true));
            }
            else
            {
               queryBuilderStack.push(new TermRangeQuery(FieldNames.createPropertyFieldName(node.getPropertyName()), 
                       propertyValueTerm.text(), maxFildValue.text(), true, true));
            }
            break;
         case LESS_THAN :
            if (caseInsensitiveSearch)
            {
               queryBuilderStack.push(new CaseInsensitiveRangeQuery(FieldNames.createPropertyFieldName(node.getPropertyName()), 
                       "", propertyValueTerm.text(), false, false));
            }
            else
            {
               queryBuilderStack.push(new TermRangeQuery(FieldNames.createPropertyFieldName(node.getPropertyName()), 
                       "", propertyValueTerm.text(), false, false));
            }
            break;
         case LESS_THAN_OR_EQUAL_TO :
            if (caseInsensitiveSearch)
            {
               queryBuilderStack.push(new CaseInsensitiveRangeQuery(FieldNames.createPropertyFieldName(node.getPropertyName()), 
                       "", propertyValueTerm.text(), true, true));
            }
            else
            {

               queryBuilderStack.push(new TermRangeQuery(FieldNames.createPropertyFieldName(node.getPropertyName()), 
                       "", propertyValueTerm.text(), true, true));
            }
            break;
         case LIKE :
            if (staticStingValue.equals("%"))
            {
               // property exists
               queryBuilderStack.push(new TermQuery(new Term(FieldNames.PROPERTIES_SET, node.getPropertyName())));
            }
            else
            {
               final String term = likePatternToRegex(staticStingValue);
               Query likeQuery =
                  new RegexQuery(new Term(FieldNames.createPropertyFieldName(node.getPropertyName()), term));
               if (caseInsensitiveSearch)
               {
                  ((RegexQuery)likeQuery).setRegexImplementation(new CaseInsensitiveRegexCapImpl());
               }
               queryBuilderStack.push(likeQuery);
            }

            break;
         default :
            throw new VisitException("Invalid operator " + operator);
      }

   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.Query)
    */

   public void visit(org.xcmis.search.model.Query node) throws VisitException
   {

   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.constraint.SameNode)
    */

   public void visit(SameNode node) throws VisitException
   {
      final Object[] entries = pathSplitter.splitPath(node.getPath());
      Query descendantQuery = null;

      for (int i = 0; i < entries.length; i++)
      {
         if (i == 0)
         {
            descendantQuery = new TermQuery(new Term(FieldNames.UUID, indexConfiguration.getRootUuid()));
         }
         else
         {
            final String stepName = nameConverter.convertName(entries[i]);
            final Query nameQuery = new TermQuery(new Term(FieldNames.LABEL, stepName));
            descendantQuery = new DescendantQueryNode(nameQuery, descendantQuery);
         }
      }

      queryBuilderStack.push(descendantQuery);

   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.source.join.SameNodeJoinCondition)
    */

   public void visit(SameNodeJoinCondition node) throws VisitException
   {

   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.source.Selector)
    */

   public void visit(Selector selector) throws VisitException
   {

   }

   /**
    * @see org.xcmis.search.QueryObjectModelVisitor#visit(org.xcmis.search.model.operand.UpperCase)
    */

   public void visit(UpperCase node) throws VisitException
   {
      Validate.isTrue(queryBuilderStack.peek() instanceof Boolean, "Stack should contains caseInsensitiveSearch flag");
      boolean caseInsensitiveSearch = (Boolean)queryBuilderStack.pop();

      final String value = (String)queryBuilderStack.peek();
      if (!caseInsensitiveSearch && !StringUtils.isAllUpperCase(value))
      {
         // search nothing because static value in different case
         queryBuilderStack.push(new BooleanQuery());
      }

      queryBuilderStack.push(new Boolean(true));
      // push dynamic query to stack;
      Visitors.visit(node.getOperand(), this);

   }

   /**
    * {@inheritDoc}
    */
   private Set<String> getFieldNames() throws IndexException
   {
      final Set<String> fildsSet = new HashSet<String>();
      @SuppressWarnings("unchecked")
      final Collection fields = indexReader.getFieldNames(IndexReader.FieldOption.ALL);
      for (final Object field : fields)
      {
         fildsSet.add((String)field);
      }
      return fildsSet;
   }

   /**
    * Transform Like pattern to regular expression.
    * 
    * @param pattern Like pattern
    * @return String regular expression
    */
   private String likePatternToRegex(final String pattern)
   {
      // - escape all non alphabetic characters
      // - escape constructs like \<alphabetic char> into \\<alphabetic char>
      // - replace non escaped _ % into . and .*
      final StringBuffer regexp = new StringBuffer();
      regexp.append("^");
      boolean escaped = false;
      for (int i = 0; i < pattern.length(); i++)
      {
         if (pattern.charAt(i) == LIKE_ESCAPE_CHAR)
         {
            if (escaped)
            {
               regexp.append("\\\\");
               escaped = false;
            }
            else
            {
               escaped = true;
            }
         }
         else
         {
            if (Character.isLetterOrDigit(pattern.charAt(i)))
            {
               if (escaped)
               {
                  regexp.append(pattern.charAt(i)); // append("\\\\")
                  escaped = false;
               }
               else
               {
                  regexp.append(pattern.charAt(i));
               }
            }
            else
            {
               if (escaped)
               {
                  regexp.append('\\').append(pattern.charAt(i));
                  escaped = false;
               }
               else
               {
                  switch (pattern.charAt(i))
                  {
                     case LIKE_MATCH_ONE_CHAR :
                        regexp.append('.');
                        break;
                     case LIKE_MATCH_ZERO_OR_MORE_CHAR :
                        regexp.append(".*");
                        break;
                     default :
                        regexp.append('\\').append(pattern.charAt(i));
                  }
               }
            }
         }
      }
      regexp.append("$");
      return regexp.toString();
   }
}
