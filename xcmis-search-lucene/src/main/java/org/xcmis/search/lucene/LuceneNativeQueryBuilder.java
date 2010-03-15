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
package org.xcmis.search.lucene;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.exoplatform.services.jcr.core.NamespaceAccessor;
import org.exoplatform.services.jcr.datamodel.InternalQName;
import org.exoplatform.services.jcr.datamodel.QPathEntry;
import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.IndexConstants;
import org.xcmis.search.MultiSourceNativeQueryImpl;
import org.xcmis.search.NativeQuery;
import org.xcmis.search.NativeQueryBuilder;
import org.xcmis.search.SearchIndexingService;
import org.xcmis.search.SingleSourceNativeQuery;
import org.xcmis.search.SingleSourceNativeQueryImpl;
import org.xcmis.search.VirtualTableResolver;
import org.xcmis.search.antlr.FullTextLexer;
import org.xcmis.search.antlr.FullTextParser;
import org.xcmis.search.index.FieldNames;
import org.xcmis.search.lucene.search.ChildTraversingQueryNode;
import org.xcmis.search.lucene.search.DescendantQueryNode;
import org.xcmis.search.lucene.search.visitor.DocumentMatcherFactory;
import org.xcmis.search.lucene.search.visitor.EqualToOperatorVisitor;
import org.xcmis.search.lucene.search.visitor.GreaterThanOperatorVisitor;
import org.xcmis.search.lucene.search.visitor.GreaterThanOrEqualToOperatorVisitor;
import org.xcmis.search.lucene.search.visitor.LessThanOperatorVisitor;
import org.xcmis.search.lucene.search.visitor.LessThanOrEqualToOperatorVisitor;
import org.xcmis.search.lucene.search.visitor.LikeOperatorVisitor;
import org.xcmis.search.lucene.search.visitor.NotEqualToOperatorVisitor;
import org.xcmis.search.lucene.search.visitor.OperatorVisitor;
import org.xcmis.search.lucene.search.visitor.QueryObjectModelTraversingVisitor;
import org.xcmis.search.qom.constraint.AndImpl;
import org.xcmis.search.qom.constraint.ChildNodeImpl;
import org.xcmis.search.qom.constraint.ComparisonImpl;
import org.xcmis.search.qom.constraint.ConstraintImpl;
import org.xcmis.search.qom.constraint.DescendantNodeImpl;
import org.xcmis.search.qom.constraint.FullTextSearchImpl;
import org.xcmis.search.qom.constraint.InFolderNode;
import org.xcmis.search.qom.constraint.InTreeNode;
import org.xcmis.search.qom.constraint.NotImpl;
import org.xcmis.search.qom.constraint.OrImpl;
import org.xcmis.search.qom.constraint.PropertyExistenceImpl;
import org.xcmis.search.qom.constraint.SameNodeImpl;
import org.xcmis.search.qom.join.InnerJoinCollector;
import org.xcmis.search.qom.join.LeftJoinCollector;
import org.xcmis.search.qom.join.RightJoinCollector;
import org.xcmis.search.qom.operand.BindVariableValueImpl;
import org.xcmis.search.qom.operand.DynamicOperandImpl;
import org.xcmis.search.qom.operand.FullTextSearchScoreImpl;
import org.xcmis.search.qom.operand.LiteralImpl;
import org.xcmis.search.qom.operand.StaticOperandImpl;
import org.xcmis.search.qom.source.JoinImpl;
import org.xcmis.search.qom.source.SelectorImpl;
import org.xcmis.search.qom.source.SourceImpl;
import org.xcmis.search.qom.source.join.JoinConditionImpl;
import org.xcmis.search.result.DocumentMatcher;
import org.xcmis.search.result.JoinCollector;
import org.xcmis.search.result.ResultSorter;
import org.xcmis.search.result.ResultSorterFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.qom.NodeName;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.QueryObjectModel;
import javax.jcr.query.qom.QueryObjectModelConstants;
import javax.jcr.query.qom.Selector;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: LuceneNativeQueryBuilder.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class LuceneNativeQueryBuilder extends QueryObjectModelTraversingVisitor implements NativeQueryBuilder<Query>
{

   protected final LocationFactory indexLocationFactory;

   protected LocationFactory queryLocationFactory;

   private Map<String, Value> bindVariablesValues;

   private final DocumentMatcherFactory documentMatcherFactory;

   private final Pattern fullTextFieldNamePattern = Pattern.compile("^(.*:FULL|FULL):.*$");

   private final SearchIndexingService<Query> indexingService;

   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(this.getClass().getName());

   private final ResultSorterFactory resultSorterFactory;

   private final VirtualTableResolver<Query> tableResolver;

   /**
    * Map of selector names and nodetype names. Used for support specific CMIS
    * document descendant queries.
    */
   private Map<String, InternalQName> selectors = new HashMap<String, InternalQName>();

   /**
    * @param indexingService
    * @param resultSorterFactory
    * @param namespaceMappings
    * @param tableResolver
    * @param documentMatcherFactory
    */
   public LuceneNativeQueryBuilder(final SearchIndexingService<Query> indexingService,
      final ResultSorterFactory resultSorterFactory, final NamespaceAccessor namespaceMappings,
      final VirtualTableResolver<Query> tableResolver, final DocumentMatcherFactory documentMatcherFactory)
   {
      super();
      this.indexingService = indexingService;
      this.resultSorterFactory = resultSorterFactory;

      this.tableResolver = tableResolver;
      indexLocationFactory = new LocationFactory(namespaceMappings);
      this.documentMatcherFactory = documentMatcherFactory;
   }

   public NativeQuery<Query> createNativeQuery(final QueryObjectModel queryObjectModel,
      final Map<String, Value> bindValues) throws RepositoryException
   {

      bindVariablesValues = bindValues;
      selectors.clear();

      // final Abs<Query> queryObjectModelImpl = (QueryObjectModelImpl<Query>)
      // queryObjectModel;
      // queryLocationFactory = queryObjectModelImpl.getLocationFactory();
      // try {
      //
      // queryObjectModelImpl.reloadLocation(indexLocationFactory);
      // final SourceImpl source = (SourceImpl) queryObjectModel.getSource();
      // return (NativeQuery<Query>) source.accept(this, queryObjectModel);
      //
      // } catch (final InvalidQueryException e) {
      // throw e;
      // } catch (final Exception e) {
      // throw new RepositoryException(e);
      // } finally {
      // queryObjectModelImpl.reloadLocation(queryLocationFactory);
      // }
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(final AndImpl node, final Object context) throws Exception
   {

      final int selectors = node.getSelectorsNames().size();
      node.getConstraint1();
      node.getConstraint2();

      if (selectors == 1)
      {

         final BooleanQuery b = new BooleanQuery();
         b.add((Query)((ConstraintImpl)node.getConstraint1()).accept(this, context), BooleanClause.Occur.MUST);
         b.add((Query)((ConstraintImpl)node.getConstraint2()).accept(this, context), BooleanClause.Occur.MUST);

         return b;
      }
      throw new UnsupportedOperationException("More then one selector used");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(final BindVariableValueImpl node, final Object context) throws Exception
   {
      final Value variableValue = bindVariablesValues.get(node.getBindVariableName());
      if (variableValue == null)
      {
         throw new InvalidQueryException("No value bound for " + node.getBindVariableName());
      }
      return variableValue;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(final ChildNodeImpl node, final Object context) throws Exception
   {

      // node.getPath();

      final QPathEntry[] entries = node.getQPath().getEntries();
      Query descendantQuery = null;

      for (int i = 0; i < entries.length; i++)
      {
         if (i == 0)
         {
            descendantQuery = new TermQuery(new Term(FieldNames.UUID, IndexConstants.ROOT_UUID));
         }
         else
         {
            final String stepName = indexLocationFactory.formatPathElement(entries[i]);
            final Query nameQuery = new TermQuery(new Term(FieldNames.LABEL, stepName));
            descendantQuery = new DescendantQueryNode(nameQuery, descendantQuery);
         }
      }

      // all child
      descendantQuery = new ChildTraversingQueryNode(descendantQuery, false);
      final BooleanQuery resultQuery = new BooleanQuery();
      resultQuery.add((Query)context, Occur.MUST);
      resultQuery.add(descendantQuery, Occur.MUST);
      return resultQuery;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(final ComparisonImpl node, final Object context) throws Exception
   {
      OperatorVisitor operatorVisitor = null;
      final Value staticValue = (Value)((StaticOperandImpl)node.getOperand2()).accept(this, null);
      // test nodeName comparation
      if (node.getOperand1() instanceof NodeName && staticValue.getType() != PropertyType.NAME)
      {
         if (staticValue.getType() == PropertyType.STRING || staticValue.getType() == PropertyType.BINARY
            || staticValue.getType() == PropertyType.PATH || staticValue.getType() == PropertyType.PATH
         /*|| staticValue.getType() == PropertyType.URI*/)
         {
            String staticStingValue = staticValue.getString();

            //            if (staticValue.getType() == PropertyType.URI)
            //            {
            //               if (staticStingValue.startsWith("./"))
            //               {
            //                  staticStingValue = staticStingValue.substring(2);
            //               }
            //               // need to decode
            //               try
            //               {
            //                  staticStingValue = URLDecoder.decode(staticStingValue, "UTF-8");
            //               }
            //               catch (final UnsupportedEncodingException e)
            //               {
            //                  throw new RepositoryException(e);
            //               }
            //            }

            try
            {
               final LocationFactory sessionLocationFactory =
                  ((DynamicOperandImpl)node.getOperand1()).getLocationFactory();
               sessionLocationFactory.parseJCRName(staticStingValue).getInternalName();
            }
            catch (final RepositoryException e)
            {
               throw new InvalidQueryException("Fail to convert " + staticValue.getString() + " to name", e);
            }
         }
         else
         {
            throw new InvalidQueryException("Fail to convert " + staticValue.getString() + " to name");
         }
      }

      if (QueryObjectModelConstants.JCR_OPERATOR_EQUAL_TO.equals(node.getOperator()))
      {
         operatorVisitor = new EqualToOperatorVisitor(staticValue, indexLocationFactory);
      }
      else if (QueryObjectModelConstants.JCR_OPERATOR_NOT_EQUAL_TO.equals(node.getOperator()))
      {
         operatorVisitor = new NotEqualToOperatorVisitor(staticValue, indexLocationFactory);
      }
      else if (QueryObjectModelConstants.JCR_OPERATOR_LESS_THAN.equals(node.getOperator()))
      {
         operatorVisitor = new LessThanOperatorVisitor(staticValue, indexLocationFactory);
      }
      else if (QueryObjectModelConstants.JCR_OPERATOR_LESS_THAN_OR_EQUAL_TO.equals(node.getOperator()))
      {
         operatorVisitor = new LessThanOrEqualToOperatorVisitor(staticValue, indexLocationFactory);
      }
      else if (QueryObjectModelConstants.JCR_OPERATOR_GREATER_THAN.equals(node.getOperator()))
      {
         operatorVisitor = new GreaterThanOperatorVisitor(staticValue, indexLocationFactory);
      }
      else if (QueryObjectModelConstants.JCR_OPERATOR_GREATER_THAN_OR_EQUAL_TO.equals(node.getOperator()))
      {
         operatorVisitor = new GreaterThanOrEqualToOperatorVisitor(staticValue, indexLocationFactory);
      }
      else if (QueryObjectModelConstants.JCR_OPERATOR_LIKE.equals(node.getOperator()))
      {
         operatorVisitor = new LikeOperatorVisitor(staticValue, indexLocationFactory);
      }
      else
      {
         throw new InvalidQueryException("Invalid operator " + node.getOperator());
      }

      final BooleanQuery resultQuery = new BooleanQuery();
      resultQuery.add((Query)context, Occur.MUST);
      resultQuery.add((Query)((DynamicOperandImpl)node.getOperand1()).accept(operatorVisitor, context), Occur.MUST);

      return resultQuery;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(final DescendantNodeImpl node, final Object context) throws Exception
   {

      final QPathEntry[] entries = node.getQPath().getEntries();
      Query descendantQuery = null;

      for (int i = 0; i < entries.length; i++)
      {
         if (i == 0)
         {
            descendantQuery = new TermQuery(new Term(FieldNames.UUID, IndexConstants.ROOT_UUID));
         }
         else
         {
            final String stepName = indexLocationFactory.formatPathElement(entries[i]);
            final Query nameQuery = new TermQuery(new Term(FieldNames.LABEL, stepName));
            descendantQuery = new DescendantQueryNode(nameQuery, descendantQuery);
         }
      }
      // all childs

      descendantQuery = new ChildTraversingQueryNode(descendantQuery, true);
      final BooleanQuery resultQuery = new BooleanQuery();
      resultQuery.add((Query)context, Occur.MUST);
      resultQuery.add(descendantQuery, Occur.MUST);
      return resultQuery;

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(final FullTextSearchImpl node, final Object context) throws Exception
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
         final Set<String> names = indexingService.getFieldNames();
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
         parser.fulltext(fields, new StandardAnalyzer());
         query = parser.getQuery();
         final InvalidQueryException ex = reporter.getException();
         if (ex != null)
         {
            throw ex;
         }

      }
      catch (final RecognitionException e)
      {
         throw new InvalidQueryException(e.getMessage(), e);
      }

      final BooleanQuery resultQuery = new BooleanQuery();
      resultQuery.add((Query)context, Occur.MUST);
      resultQuery.add(query, Occur.MUST);

      return resultQuery;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(final FullTextSearchScoreImpl node, final Object context) throws Exception
   {
      throw new UnsupportedOperationException("FulltextSearchScore is unsupported operation.");
   }

   @Override
   public Object visit(final JoinImpl node, final Object context) throws Exception
   {

      final SingleSourceNativeQuery<Query> leftQuery =
         (SingleSourceNativeQuery<Query>)((SourceImpl)node.getLeft()).accept(this, context);
      final SingleSourceNativeQuery<Query> rightQuery =
         (SingleSourceNativeQuery<Query>)((SourceImpl)node.getRight()).accept(this, context);
      final DocumentMatcher documentMatcher =
         (DocumentMatcher)((JoinConditionImpl)node.getJoinCondition()).accept(documentMatcherFactory, context);
      JoinCollector joinCollector = null;

      if (QueryObjectModelConstants.JCR_JOIN_TYPE_INNER.equals(node.getJoinType()))
      {
         joinCollector = new InnerJoinCollector(node.getJoinCondition(), documentMatcher);
      }
      else if (QueryObjectModelConstants.JCR_JOIN_TYPE_LEFT_OUTER.equals(node.getJoinType()))
      {
         joinCollector = new LeftJoinCollector(node.getJoinCondition(), documentMatcher);
      }
      else if (QueryObjectModelConstants.JCR_JOIN_TYPE_RIGHT_OUTER.equals(node.getJoinType()))
      {
         joinCollector = new RightJoinCollector(node.getJoinCondition(), documentMatcher);
      }

      ResultSorter postResultSorter = null;

      final Ordering[] orderings = ((QueryObjectModel)context).getOrderings();
      if (orderings != null && orderings.length > 0)
      {
         postResultSorter = resultSorterFactory.getResultSorter(orderings);
      }

      return new MultiSourceNativeQueryImpl<Query>(leftQuery, rightQuery, joinCollector, ((QueryObjectModel)context)
         .getOrderings(), postResultSorter, null);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(final LiteralImpl node, final Object context) throws Exception
   {
      return node.getLiteralValue();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(final NotImpl node, final Object context) throws Exception
   {
      final BooleanQuery resultQuery = new BooleanQuery();
      resultQuery.add((Query)context, Occur.MUST);
      resultQuery.add((Query)((ConstraintImpl)node.getConstraint()).accept(this, context), Occur.MUST_NOT);
      return resultQuery;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(final OrImpl node, final Object context) throws Exception
   {

      final int selectors = node.getSelectorsNames().size();
      if (selectors == 1)
      {
         final BooleanQuery b = new BooleanQuery();
         b.add((Query)((ConstraintImpl)node.getConstraint1()).accept(this, context), BooleanClause.Occur.SHOULD);
         b.add((Query)((ConstraintImpl)node.getConstraint2()).accept(this, context), BooleanClause.Occur.SHOULD);
         return b;
      }
      throw new RepositoryException("More then one selector dousn't suported");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(final PropertyExistenceImpl node, final Object context) throws Exception
   {
      return new TermQuery(new Term(FieldNames.PROPERTIES_SET, node.getPropertyName()));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(final SameNodeImpl node, final Object context) throws Exception
   {
      // node.getPath();

      final QPathEntry[] entries = node.getQPath().getEntries();
      Query descendantQuery = null;

      for (int i = 0; i < entries.length; i++)
      {
         if (i == 0)
         {
            descendantQuery = new TermQuery(new Term(FieldNames.UUID, IndexConstants.ROOT_UUID));
         }
         else
         {
            final String stepName = indexLocationFactory.formatPathElement(entries[i]);
            final Query nameQuery = new TermQuery(new Term(FieldNames.LABEL, stepName));
            descendantQuery = new DescendantQueryNode(nameQuery, descendantQuery);
         }
      }

      final BooleanQuery resultQuery = new BooleanQuery();
      resultQuery.add((Query)context, Occur.MUST);
      resultQuery.add(descendantQuery, Occur.MUST);
      return resultQuery;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(final SelectorImpl node, final Object context) throws Exception
   {
      final InternalQName nodeName = node.getNodeTypeQName();
      selectors.put(node.getSelectorName(), nodeName);

      Query query = tableResolver.resolve(nodeName, true);

      final QueryObjectModel queryObjectModel = (QueryObjectModel)context;
      final ConstraintImpl constraint = (ConstraintImpl)queryObjectModel.getConstraint();
      final String selectorName = queryLocationFactory.createJCRName(node.getSelectorQName()).getAsString();
      final String indexSelectorName = indexLocationFactory.createJCRName(node.getSelectorQName()).getAsString();

      if (constraint != null)
      {
         if (constraint.getSelectorsNames().size() != 1)
         {
            throw new UnsupportedOperationException("Multiple selectors in constrains don't supported");
         }
         // TODO change
         final String str[] = new String[1];
         constraint.getSelectorsNames().toArray(str);
         if (str[0] == null || indexSelectorName == str[0] || indexSelectorName.equals(str[0]))
         {
            query = (Query)constraint.accept(this, query);
         }

      }

      ResultSorter resultSorter = null;
      Ordering[] orderings;
      if (queryObjectModel.getSource() instanceof Selector)
      {
         orderings = queryObjectModel.getOrderings();
         if (orderings == null || orderings.length == 0)
         {
            resultSorter = resultSorterFactory.getDefaultResultSorter(new String[]{selectorName});
         }
         else
         {
            resultSorter = resultSorterFactory.getResultSorter(orderings);
         }
      }
      else
      {
         orderings = new Ordering[]{};
      }

      return new SingleSourceNativeQueryImpl<Query>(query, selectorName, orderings, resultSorter, null, 0, 0);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(InFolderNode node, Object context) throws Exception
   {
      Query parentQuery = new TermQuery(new Term(FieldNames.UUID, node.getFolderId()));

      Query descendantQuery = new ChildTraversingQueryNode(parentQuery, false);
      final BooleanQuery resultQuery = new BooleanQuery();
      resultQuery.add((Query)context, Occur.MUST);
      resultQuery.add(descendantQuery, Occur.MUST);
      return resultQuery;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(InTreeNode node, Object context) throws Exception
   {
      Query parentQuery = new TermQuery(new Term(FieldNames.UUID, node.getFolderId()));

      Query descendantQuery = new ChildTraversingQueryNode(parentQuery, true);
      final BooleanQuery resultQuery = new BooleanQuery();
      resultQuery.add((Query)context, Occur.MUST);
      resultQuery.add(descendantQuery, Occur.MUST);
      return resultQuery;
   }

   /**
    * Returns InteranlQName nodetype associated with selector name.
    * 
    * @param selectorName - selector name
    * @return - InteranQName node type
    */
   public InternalQName getNodeType(String selectorName)
   {
      return selectors.get(selectorName);
   }
}
