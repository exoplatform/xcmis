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
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.exoplatform.services.jcr.core.NamespaceAccessor;
import org.exoplatform.services.jcr.core.nodetype.NodeTypeDataManager;
import org.exoplatform.services.jcr.datamodel.InternalQName;
import org.exoplatform.services.jcr.impl.Constants;
import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.index.FieldNames;
import org.xcmis.sp.jcr.exo.query.NodeTypeVirtualTableResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NoSuchNodeTypeException;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34027 2009-07-15 23:26:43Z
 *          aheritier $
 */
public class LuceneVirtualTableResolver extends NodeTypeVirtualTableResolver<Query>
{

   /** The location factory. */
   private final LocationFactory locationFactory;

   /**
    * Class logger.
    */
   private static final Log LOG = ExoLogger.getLogger(LuceneVirtualTableResolver.class);

   /** The mixin types field. */
   private final String mixinTypesField;

   /** The primary type field. */
   private final String primaryTypeField;

   /**
    * The Constructor.
    * 
    * @param nodeTypeDataManager the node type data manager
    * @param namespaceAccessor the namespace accessor
    * @throws RepositoryException if any errors in CMIS repository occurs
    */
   public LuceneVirtualTableResolver(final NodeTypeDataManager nodeTypeDataManager,
      final NamespaceAccessor namespaceAccessor) throws RepositoryException
   {
      super(nodeTypeDataManager);
      locationFactory = new LocationFactory(namespaceAccessor);
      mixinTypesField = locationFactory.createJCRName(Constants.JCR_MIXINTYPES).getAsString();
      primaryTypeField = locationFactory.createJCRName(Constants.JCR_PRIMARYTYPE).getAsString();
   }

   /**
    * {@inheritDoc}
    */
   public Query resolve(final InternalQName tableName, final boolean includeInheritedTables)
   {
      final List<Term> terms = new ArrayList<Term>();
      Query query = null;
      try
      {
         final String nodeTypeStringName = locationFactory.createJCRName(tableName).getAsString();

         if (isMixin(tableName))
         {
            // search for nodes where jcr:mixinTypes is set to this mixin
            final Term t = new Term(FieldNames.createPropertyFieldName(mixinTypesField), nodeTypeStringName);
            terms.add(t);
         }
         else
         {
            // search for nodes where jcr:primaryType is set to this type
            final Term t = new Term(FieldNames.createPropertyFieldName(primaryTypeField), nodeTypeStringName);
            terms.add(t);
         }
         if (includeInheritedTables)
         {
            // now search for all node types that are derived from base
            final Set<InternalQName> allTypes = getSubTypes(tableName);
            for (final InternalQName descendantNt : allTypes)
            {
               final String ntName = locationFactory.createJCRName(descendantNt).getAsString();
               Term t;
               if (isMixin(descendantNt))
               {
                  // search on jcr:mixinTypes
                  t = new Term(FieldNames.createPropertyFieldName(mixinTypesField), ntName);
               }
               else
               {
                  // search on jcr:primaryType
                  t = new Term(FieldNames.createPropertyFieldName(primaryTypeField), ntName);
               }
               terms.add(t);
            }
         }
      }
      catch (final NoSuchNodeTypeException e)
      {
         final BooleanQuery b = new BooleanQuery();
         // property exists
         b.add(new MatchAllDocsQuery(), BooleanClause.Occur.MUST_NOT);
         query = b;
      }
      catch (final RepositoryException e)
      {
         // ok will be empty query
         LOG.warn(e.getLocalizedMessage(), e);
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

}
