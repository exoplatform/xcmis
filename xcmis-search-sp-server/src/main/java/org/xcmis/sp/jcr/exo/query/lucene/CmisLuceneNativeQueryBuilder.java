/**
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

package org.xcmis.sp.jcr.exo.query.lucene;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.exoplatform.services.jcr.core.NamespaceAccessor;
import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.xcmis.search.NativeQuery;
import org.xcmis.search.SearchIndexingService;
import org.xcmis.search.SingleSourceNativeQuery;
import org.xcmis.search.SingleSourceNativeQueryImpl;
import org.xcmis.search.index.FieldNames;
import org.xcmis.search.lucene.LuceneNativeQueryBuilder;
import org.xcmis.search.lucene.search.visitor.DocumentMatcherFactory;
import org.xcmis.search.qom.AbstractQueryObjectModelNode;
import org.xcmis.search.qom.source.SelectorImpl;
import org.xcmis.search.qom.source.SourceImpl;
import org.xcmis.search.result.ResultSorterFactory;
import org.xcmis.sp.jcr.exo.JcrCMIS;
import org.xcmis.sp.jcr.exo.query.qom.CmisQueryObjectModel;
import org.xcmis.spi.CMIS;

import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.qom.QueryObjectModel;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34027 2009-07-15 23:26:43Z
 *          aheritier $
 */
public class CmisLuceneNativeQueryBuilder extends LuceneNativeQueryBuilder
{

   /** The location factory. */
   private LocationFactory locationFactory;

   /** The table resolver. */
   private CmisVirtualTableResolver tableResolver;

   /**
    * The Constructor.
    * 
    * @param indexingService the indexing service
    * @param resultSorterFactory the result sorter factory
    * @param namespaceMappings the namespace mappings
    * @param tableResolver the table resolver
    * @param documentMatcherFactory the document matcher factory
    */
   public CmisLuceneNativeQueryBuilder(SearchIndexingService<Query> indexingService,
      ResultSorterFactory resultSorterFactory, NamespaceAccessor namespaceMappings,
      CmisVirtualTableResolver tableResolver, DocumentMatcherFactory documentMatcherFactory)
   {
      super(indexingService, resultSorterFactory, namespaceMappings, tableResolver, documentMatcherFactory);
      locationFactory = new LocationFactory(namespaceMappings);
      this.tableResolver = tableResolver;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @SuppressWarnings("unchecked")
   public NativeQuery<Query> createNativeQuery(QueryObjectModel queryObjectModel, Map<String, Value> bindValues)
      throws RepositoryException
   {
      super.createNativeQuery(queryObjectModel, bindValues);
      final AbstractQueryObjectModelNode queryObjectModelImpl = (AbstractQueryObjectModelNode)queryObjectModel;
      queryLocationFactory = queryObjectModelImpl.getLocationFactory();
      try
      {
         queryObjectModelImpl.reloadLocation(indexLocationFactory);
         final SourceImpl source = (SourceImpl)queryObjectModel.getSource();
         return (NativeQuery<Query>)source.accept(this, queryObjectModel);
      }
      catch (final InvalidQueryException e)
      {
         throw e;
      }
      catch (final Exception e)
      {
         throw new RepositoryException(e);
      }
      finally
      {
         queryObjectModelImpl.reloadLocation(queryLocationFactory);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object visit(final SelectorImpl node, final Object context) throws Exception
   {

      @SuppressWarnings("unchecked")
      SingleSourceNativeQuery<Query> nativeQuery = (SingleSourceNativeQuery<Query>)super.visit(node, context);

      Query query = nativeQuery.getQuery();
      final CmisQueryObjectModel queryObjectModel = (CmisQueryObjectModel)context;

      if ((!queryObjectModel.getSearchAllVersions().booleanValue())
         && tableResolver.isNodeType(node.getNodeTypeQName(), locationFactory.parseJCRName(JcrCMIS.NT_FILE)
            .getInternalName()))
      {
         // create boolean query - node must have property latestVersion=true
         Query isLatestQuery =
            new TermQuery(new Term(FieldNames.createPropertyFieldName(CMIS.IS_LATEST_VERSION), "true"));

         final BooleanQuery resultQuery = new BooleanQuery();
         resultQuery.add(isLatestQuery, Occur.MUST);
         resultQuery.add(query, Occur.MUST);

         return new SingleSourceNativeQueryImpl<Query>(resultQuery, nativeQuery.getSelectorName(), nativeQuery
            .getOrderings(), nativeQuery.getPostSorter(), nativeQuery.getPostFilter(), nativeQuery.getLimit(),
            nativeQuery.getOffset());
      }
      return nativeQuery;
   }

   /**
    * {@inheritDoc}
    */
   //   @Override
   //   public Object visit(InFolderNode node, Object context) throws Exception
   //   {
   //      InternalQName nodetype = getNodeType(node.getSelectorName());
   //      if (tableResolver.isNodeType(nodetype, locationFactory.parseJCRName(JcrCMIS.NT_CMIS_DOCUMENT)
   //         .getInternalName()))
   //      {
   //
   //         Query parentFolderId = new TermQuery(new Term(FieldNames.UUID, node.getFolderId()));
   //
   //         Query smisSubnode =
   //            new TermQuery(new Term(FieldNames.createPropertyFieldName(locationFactory.createJCRName(
   //               Constants.JCR_PRIMARYTYPE).getAsString()), JcrCMIS.NT_CMIS_VERSION_SERIES));
   //
   //         Query descendantQuery = new DescendantQueryNode(smisSubnode, parentFolderId);
   //
   //         // all child
   //         descendantQuery = new ChildTraversingQueryNode(descendantQuery, false);
   //
   //         final BooleanQuery resultQuery = new BooleanQuery();
   //         resultQuery.add((Query)context, Occur.MUST);
   //         resultQuery.add(descendantQuery, Occur.MUST);
   //         return resultQuery;
   //      }
   //      else
   //      {
   //         return super.visit(node, context);
   //      }
   //   }

}
