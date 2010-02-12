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

package org.xcmis.restatom.collections;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.RepositoryService;
import org.xcmis.messaging.CmisTypeDefinitionListType;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.restatom.AtomUtils;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.RepositoryException;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: TypesChildrenCollection.java 2487 2009-07-31 14:14:34Z
 *          andrew00x $
 */

public class TypesChildrenCollection extends CmisTypeCollection
{

   /**
    * Instantiates a new types children collection.
    * 
    * @param repositoryService the repository service
    */
   public TypesChildrenCollection(RepositoryService repositoryService)
   {
      super(repositoryService);
      setHref("/types");
   }

   /**
    * {@inheritDoc}
    */
   protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException
   {
      String typeId = request.getTarget().getParameter(AtomCMIS.PARAM_TYPE_ID);
      boolean includePropertyDefinitions =
         Boolean.parseBoolean(request.getParameter(AtomCMIS.PARAM_INCLUDE_PROPERTY_DEFINITIONS));
      int maxItems;
      try
      {
         maxItems =
            request.getParameter(AtomCMIS.PARAM_MAX_ITEMS) == null
               || request.getParameter(AtomCMIS.PARAM_MAX_ITEMS).length() == 0 ? CMIS.MAX_ITEMS : Integer
               .parseInt(request.getParameter(AtomCMIS.PARAM_MAX_ITEMS));
      }
      catch (NumberFormatException nfe)
      {
         String msg = "Invalid parameter " + request.getParameter(AtomCMIS.PARAM_MAX_ITEMS);
         throw new ResponseContextException(msg, 400);
      }
      int skipCount;
      try
      {
         skipCount =
            request.getParameter(AtomCMIS.PARAM_SKIP_COUNT) == null
               || request.getParameter(AtomCMIS.PARAM_SKIP_COUNT).length() == 0 ? 0 : Integer.parseInt(request
               .getParameter(AtomCMIS.PARAM_SKIP_COUNT));
      }
      catch (NumberFormatException nfe)
      {
         String msg = "Invalid parameter " + request.getParameter(AtomCMIS.PARAM_SKIP_COUNT);
         throw new ResponseContextException(msg, 400);
      }
      try
      {
         String repositoryId = getRepositoryId(request);
         CmisTypeDefinitionListType list =
            repositoryService.getTypeChildren(repositoryId, typeId, includePropertyDefinitions, maxItems, skipCount);
         addPageLinks(typeId, feed, "types", maxItems, skipCount, list.getNumItems() == null ? -1 : list.getNumItems()
            .intValue(), list.isHasMoreItems(), request);

         String down = getTypeDescendantsLink(typeId, request);
         feed.addLink(down, AtomCMIS.LINK_DOWN, AtomCMIS.MEDIATYPE_CMISTREE, null, null, -1);

         if (typeId != null)
         {
            String typeLink = getObjectTypeLink(typeId, request);
            feed.addLink(typeLink, AtomCMIS.LINK_VIA, AtomCMIS.MEDIATYPE_ATOM_ENTRY, null, null, -1);

            CmisTypeDefinitionType type = repositoryService.getTypeDefinition(repositoryId, typeId);
            String parentType = type.getParentId();
            if (parentType != null)
            {
               String parent = getObjectTypeLink(parentType, request);
               feed.addLink(parent, AtomCMIS.LINK_UP, AtomCMIS.MEDIATYPE_ATOM_ENTRY, null, null, -1);
            }
         }
         for (CmisTypeDefinitionType type : list.getTypes())
         {
            Entry e = feed.addEntry();
            IRI feedIri = new IRI(getFeedIriForEntry(type, request));
            addEntryDetails(request, e, feedIri, type);
         }
      }
      catch (RepositoryException re)
      {
         throw new ResponseContextException(createErrorResponse(re, 500));
      }
      catch (Throwable t)
      {
         throw new ResponseContextException(createErrorResponse(t, 500));
      }
   }

   /**
    * {@inheritDoc}
    */
   public Iterable<CmisTypeDefinitionType> getEntries(RequestContext request) throws ResponseContextException
   {
      throw new UnsupportedOperationException("entries");
   }

   /**
    * {@inheritDoc}
    */
   public String getTitle(RequestContext request)
   {
      return "Types Children";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected Feed createFeedBase(RequestContext request) throws ResponseContextException
   {
      Factory factory = request.getAbdera().getFactory();
      Feed feed = factory.newFeed();
      feed.setId(getId(request));
      feed.setTitle(getTitle(request));
      feed.addAuthor(getAuthor(request));
      feed.setUpdated(AtomUtils.getAtomDate(Calendar.getInstance()));

      // Service link.
      feed.addLink(getServiceLink(request), AtomCMIS.LINK_SERVICE, AtomCMIS.MEDIATYPE_ATOM_SERVICE, null, null, -1);

      Map<String, String> params = new HashMap<String, String>();
      params.put("repoid", getRepositoryId(request));
      params.put("atomdoctype", "types");
      params.put("id", request.getTarget().getParameter("typeid"));
      String self = request.absoluteUrlFor(TargetType.ENTRY, params);
      feed.addLink(self, AtomCMIS.LINK_SELF, AtomCMIS.MEDIATYPE_ATOM_FEED, null, null, -1);

      return feed;
   }

}
