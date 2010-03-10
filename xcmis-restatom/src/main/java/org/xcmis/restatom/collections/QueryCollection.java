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
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.xcmis.core.CmisQueryType;
import org.xcmis.core.DiscoveryService;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.core.ObjectService;
import org.xcmis.core.RepositoryService;
import org.xcmis.core.VersioningService;
import org.xcmis.core.impl.object.RenditionFilter;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.restatom.AtomUtils;
import org.xcmis.restatom.abdera.QueryTypeElement;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.object.CmisObject;
import org.xcmis.spi.object.CmisObjectList;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class QueryCollection extends CmisObjectCollection
{

   /** The query service. */
   protected final DiscoveryService queryService;

   /**
    * Instantiates a new query collection.
    * 
    * @param repositoryService the repository service
    * @param objectService the object service
    * @param versioningService the versioning service
    * @param queryService the query service
    */
   public QueryCollection(RepositoryService repositoryService, ObjectService objectService,
      VersioningService versioningService, DiscoveryService queryService)
   {
      super(repositoryService, objectService, versioningService);
      this.queryService = queryService;
      setHref("/query");
   }

   /**
    * {@inheritDoc}
    */
   public String getId(RequestContext request)
   {
      return "cmis:query:" + getRepositoryId(request);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected ResponseContext buildGetFeedResponse(Feed feed)
   {
      ResponseContext rc = super.buildGetFeedResponse(feed);
      // spec. says need 201 instead 200
      if (rc.getStatus() == 200)
         rc.setStatus(201);
      return rc;
   }

   /**
    * {@inheritDoc}
    */
   public Iterable<CmisObject> getEntries(RequestContext request) throws ResponseContextException
   {
      return query(request).getObjects();
   }

   /**
    * Process query request. 
    * 
    * @param request
    * @return CmisObjectList
    * @throws ResponseContextException
    */
   private CmisObjectList query(RequestContext request) throws ResponseContextException
   {
      try
      {
         Document<Element> doc = request.getDocument();
         if (doc == null)
         {
            String q = request.getParameter("q");
            try
            {
               q = URLDecoder.decode(q, "UTF-8");
            }
            catch (UnsupportedEncodingException e)
            {
            }
            boolean searchAllVersions = Boolean.parseBoolean(request.getParameter(AtomCMIS.PARAM_SEARCH_ALL_VERSIONS));
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
            boolean includeAllowableActions =
               Boolean.parseBoolean(request.getParameter(AtomCMIS.PARAM_INCLUDE_ALLOWABLE_ACTIONS));
            String includeRelationships = request.getParameter(AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS);
            EnumIncludeRelationships enumIncludeRelationships =
               (includeRelationships == null ? EnumIncludeRelationships.NONE : EnumIncludeRelationships
                  .fromValue(includeRelationships));
            String renditionFilter = RenditionFilter.NONE_FILTER;

            return queryService.query(getRepositoryId(request), q, searchAllVersions, includeAllowableActions,
               enumIncludeRelationships, renditionFilter, maxItems, skipCount, true);
         }
         else
         {
            QueryTypeElement queryElement = (QueryTypeElement)doc.getRoot();
            if (queryElement != null)
            {
               CmisQueryType query = queryElement.getQuery();
               return queryService.query(getRepositoryId(request), query.getStatement(), query.isSearchAllVersions()
                  .booleanValue(), query.isIncludeAllowableActions().booleanValue(), query.getIncludeRelationships(),
                  query.getRenditionFilter(), query.getMaxItems().intValue(), query.getSkipCount().intValue(), true);
            }
            else
            {
               String msg = "Invalid parameter. There are no query request parameters or post body.";
               throw new ResponseContextException(msg, 400);
            }
         }
      }
      catch (ParseException pe)
      {
         throw new ResponseContextException(createErrorResponse(pe, 500));
      }
      catch (IOException ioe)
      {
         throw new ResponseContextException(createErrorResponse(ioe, 500));
      }
      catch (RepositoryException re)
      {
         throw new ResponseContextException(createErrorResponse(re, 500));
      }
      catch (InvalidArgumentException iae)
      {
         throw new ResponseContextException(createErrorResponse(iae, 400));
      }
      catch (Throwable t)
      {
         throw new ResponseContextException(createErrorResponse(t, 500));
      }
   }

   /**
    * {@inheritDoc}
    */
   public String getTitle(RequestContext request)
   {
      return "Query";
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
      feed.addLink(getServiceLink(request), "service", "application/atomsvc+xml", null, null, -1);
      return feed;
   }

   @Override
   protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException
   {
      try
      {
         int maxItems;
         int skipCount;
         String q;

         Document<Element> doc = request.getDocument();
         if (doc == null)
         {
            q = request.getParameter("q");
            try
            {
               q = URLDecoder.decode(q, "UTF-8");
            }
            catch (UnsupportedEncodingException e)
            {
            }
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
         }
         else
         {
            QueryTypeElement queryElement = (QueryTypeElement)doc.getRoot();
            if (queryElement != null)
            {
               CmisQueryType query = queryElement.getQuery();
               q = query.getStatement();
               maxItems = query.getMaxItems().intValue();
               skipCount = query.getSkipCount().intValue();
            }
            else
            {
               String msg = "Invalid parameter. There are no query request parameters or post body.";
               throw new ResponseContextException(msg, 400);
            }
         }

         //         String objectId = getId(request);
         CmisObjectList list = query(request);

         addPageLinks(q, feed, "query", maxItems, skipCount, list.getNumItems() == null ? -1 : list.getNumItems()
            .intValue(), list.isHasMoreItems(), request);
         if (list.getObjects().size() > 0)
         {
            if (list.getNumItems() != null)
            {
               // add cmisra:numItems
               Element numItems = feed.addExtension(AtomCMIS.NUM_ITEMS);
               numItems.setText(list.getNumItems().toString());
            }

            for (CmisObject oif : list.getObjects())
            {
               Entry e = feed.addEntry();
               IRI feedIri = new IRI(getFeedIriForEntry(oif, request));
               addEntryDetails(request, e, feedIri, oif);
            }
         }
      }
      catch (FilterNotValidException fe)
      {
         throw new ResponseContextException(createErrorResponse(fe, 400));
      }
      catch (ObjectNotFoundException onfe)
      {
         throw new ResponseContextException(createErrorResponse(onfe, 404));
      }
      catch (IOException ioe)
      {
         throw new ResponseContextException(createErrorResponse(ioe, 500));
      }
      catch (InvalidArgumentException iae)
      {
         throw new ResponseContextException(createErrorResponse(iae, 404));
      }
      catch (Throwable t)
      {
         throw new ResponseContextException(createErrorResponse(t, 500));
      }
   }

   protected void addPageLinks(String q, Feed feed, String atomdocType, int maxItems, int skipCount, int total,
      boolean hasMore, RequestContext request)
   {
      Map<String, String> params = new HashMap<String, String>();
      params.put("repoid", getRepositoryId(request));
      params.put("atomdoctype", atomdocType);
      params.put("q", q);
      // First link
      params.put(AtomCMIS.PARAM_SKIP_COUNT, "0");
      params.put(AtomCMIS.PARAM_MAX_ITEMS, //
         Integer.toString((skipCount == 0) ? maxItems //
            : (maxItems < skipCount ? maxItems : skipCount) /* If started not from first page. */));
      feed.addLink(request.absoluteUrlFor("feed", params), AtomCMIS.LINK_FIRST, AtomCMIS.MEDIATYPE_ATOM_FEED, null,
         null, -1);
      // Previous link.
      if (skipCount > 0)
      {
         params.put(AtomCMIS.PARAM_MAX_ITEMS, Integer.toString(maxItems < skipCount ? maxItems : skipCount));
         params.put(AtomCMIS.PARAM_SKIP_COUNT, Integer.toString(maxItems < skipCount ? skipCount - maxItems : 0));
         feed.addLink(request.absoluteUrlFor("feed", params), AtomCMIS.LINK_PREVIOUS, AtomCMIS.MEDIATYPE_ATOM_FEED,
            null, null, -1);
      }
      if (hasMore)
      {
         // Next link.
         params.put(AtomCMIS.PARAM_SKIP_COUNT, Integer.toString(skipCount + maxItems));
         params.put(AtomCMIS.PARAM_MAX_ITEMS, Integer.toString(maxItems));
         // If has more items then provide next link.
         feed.addLink(request.absoluteUrlFor("feed", params), AtomCMIS.LINK_NEXT, AtomCMIS.MEDIATYPE_ATOM_FEED, null,
            null, -1);
         // Total link.
         if (total > 0)
         {
            // If total number result in set is unknown then unable to determine last page link.
            int pages = (total - skipCount) / maxItems;
            int rem = (total - skipCount) % maxItems;
            if (rem == 0)
               skipCount = total - maxItems;
            else if (pages != 0)
               skipCount = skipCount + pages * maxItems;
            params.put(AtomCMIS.PARAM_SKIP_COUNT, Integer.toString(skipCount));
            params.put(AtomCMIS.PARAM_MAX_ITEMS, Integer.toString(maxItems));
            feed.addLink(request.absoluteUrlFor("feed", params), AtomCMIS.LINK_LAST, AtomCMIS.MEDIATYPE_ATOM_FEED,
               null, null, -1);
         }
      }
   }

}
