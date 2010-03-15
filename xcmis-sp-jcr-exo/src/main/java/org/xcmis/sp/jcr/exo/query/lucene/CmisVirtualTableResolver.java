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
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.search.index.FieldNames;
import org.xcmis.spi.Repository;
import org.xcmis.spi.TypeNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NoSuchNodeTypeException;

/**
 * Created by The eXo Platform SAS. <br/>
 * 
 * @author <a href="karpenko.sergiy@gmail.com">Karpenko Sergiy</a>
 * @version $Id: CmisVirtualTableResolver.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class CmisVirtualTableResolver extends LuceneVirtualTableResolver
{

   /** The repository. */
   private Repository repository;

   /** The location factory. */
   private final LocationFactory locationFactory;

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(LuceneVirtualTableResolver.class);

   /** The mixin types field. */
   private final String mixinTypesField;

   /** The primary type field. */
   private final String primaryTypeField;

   /**
    * Constructor.
    * 
    * @param nodeTypeDataManager the node type data manager
    * @param namespaceAccessor the namespace accessor
    * @param repository the repository
    * 
    * @throws RepositoryException if any errors in CMIS repository occurs
    */
   public CmisVirtualTableResolver(NodeTypeDataManager nodeTypeDataManager, NamespaceAccessor namespaceAccessor,
      Repository repository) throws RepositoryException
   {
      super(nodeTypeDataManager, namespaceAccessor);
      locationFactory = new LocationFactory(namespaceAccessor);
      mixinTypesField = locationFactory.createJCRName(Constants.JCR_MIXINTYPES).getAsString();
      primaryTypeField = locationFactory.createJCRName(Constants.JCR_PRIMARYTYPE).getAsString();
      this.repository = repository;
   }

   /**
    * Check is nodeType extends or equal to baseNodeType.
    * 
    * @param baseNodeType - default document node type
    * @param nodeType the node type
    * @return true, if checks if is node type
    * @throws RepositoryException the repository exception
    */
   public boolean isNodeType(InternalQName nodeType, InternalQName baseNodeType) throws RepositoryException
   {
      if (nodeType.equals(baseNodeType))
      {
         return true;
      }

      final Set<InternalQName> allTypes = getSubTypes(baseNodeType);
      for (final InternalQName descendantNt : allTypes)
      {
         if (nodeType.equals(descendantNt))
         {
            return true;
         }
      }
      return false;
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
               try
               {
                  CmisTypeDefinitionType type = repository.getTypeDefinition(ntName, false);
                  if (type.isIncludedInSupertypeQuery())
                  {
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
               catch (TypeNotFoundException e)
               {
                  // skip this node type is not registered object type
                  LOG.warn(e.getLocalizedMessage(), e);
               }
               catch (org.xcmis.spi.RepositoryException e)
               {
                  // TODO Throw Exception
                  LOG.error(e.getMessage(), e);
               }
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
