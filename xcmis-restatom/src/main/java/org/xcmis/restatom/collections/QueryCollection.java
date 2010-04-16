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
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.restatom.AtomUtils;
import org.xcmis.restatom.abdera.QueryTypeElement;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.Connection;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.RenditionFilter;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.IncludeRelationships;
import org.xcmis.spi.query.Query;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: QueryCollection.java 247 2010-02-24 19:29:00Z andrew00x $
 */
public class QueryCollection extends CmisObjectCollection
{

   /**
    * Instantiates a new query collection.
    * @param storageProvider TODO
    */
   public QueryCollection(/*StorageProvider storageProvider*/)
   {
      super(/*storageProvider*/);
      setHref("/query");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getId(RequestContext request)
   {
      return "cmis:query:" + getRepositoryId(request);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Iterable<CmisObject> getEntries(RequestContext request) throws ResponseContextException
   {
      throw new UnsupportedOperationException("entries");
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
      Connection conn = null;
      try
      {
         conn = getConnection(request);

         int maxItems = CmisConstants.MAX_ITEMS;
         int skipCount = CmisConstants.SKIP_COUNT;
         String q = null;
         String renditionFilter = RenditionFilter.NONE_FILTER;
         boolean isSearchAllVersions = false;
         IncludeRelationships includeRelationships = IncludeRelationships.NONE;
         boolean isIncludeAllowableActions = false;

         Document<Element> doc = null;
         try
         {
            doc = request.getDocument();
         }
         catch (org.apache.abdera.parser.ParseException e)
         {
            // Message: Content is not allowed in prolog.
         }

         if (doc == null)
         {
            // if it is GET method request
            q = request.getParameter("q");
            try
            {
               q = URLDecoder.decode(q, "UTF-8");
            }
            catch (UnsupportedEncodingException e)
            {
            }
            maxItems = getIntegerParameter(request, AtomCMIS.PARAM_MAX_ITEMS, CmisConstants.MAX_ITEMS);
            skipCount = getIntegerParameter(request, AtomCMIS.PARAM_SKIP_COUNT, CmisConstants.SKIP_COUNT);
            renditionFilter = request.getParameter(AtomCMIS.PARAM_RENDITION_FILTER);
         }
         else
         {
            // if it is POST method request
            QueryTypeElement queryElement = (QueryTypeElement)doc.getRoot();
            if (queryElement != null)
            {
               Query query = queryElement.getQuery();
               q = query.getStatement();
               maxItems = queryElement.getPageSize();
               skipCount = queryElement.getSkipCount();
               renditionFilter = queryElement.getRenditionFilter();
               isSearchAllVersions = queryElement.isSearchAllVersions();
               includeRelationships = queryElement.getIncludeRelationships();
               isIncludeAllowableActions = queryElement.isIncludeAllowableActions();
            }
            else
            {
               String msg = "Invalid parameter. There are no query request parameters or post body.";
               throw new ResponseContextException(msg, 400);
            }
         }

         ItemsList<CmisObject> list =
            conn.query(q, isSearchAllVersions, isIncludeAllowableActions, includeRelationships, true, renditionFilter,
               maxItems, skipCount);

         addPageLinks(q, feed, "query", maxItems, skipCount, list.getNumItems(), list.isHasMoreItems(), request);
         if (list.getItems().size() > 0)
         {
            if (list.getNumItems() != -1)
            {
               // add cmisra:numItems
               Element numItems = feed.addExtension(AtomCMIS.NUM_ITEMS);
               numItems.setText(Integer.toString(list.getNumItems()));
            }

            for (CmisObject oif : list.getItems())
            {
               Entry e = feed.addEntry();
               IRI feedIri = new IRI(getFeedIriForEntry(oif, request));
               addEntryDetails(request, e, feedIri, oif);
            }
         }

      }
      catch (StorageException re)
      {
         throw new ResponseContextException(createErrorResponse(re, 500));
      }
      catch (FilterNotValidException fe)
      {
         throw new ResponseContextException(createErrorResponse(fe, 400));
      }
      catch (ObjectNotFoundException onfe)
      {
         throw new ResponseContextException(createErrorResponse(onfe, 404));
      }
      catch (InvalidArgumentException iae)
      {
         throw new ResponseContextException(createErrorResponse(iae, 404));
      }
      catch (Throwable t)
      {
         throw new ResponseContextException(createErrorResponse(t, 500));
      }
      finally
      {
         if (conn != null)
         {
            conn.close();
         }
      }
   }

   @Override
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
            {
               skipCount = total - maxItems;
            }
            else if (pages != 0)
            {
               skipCount = skipCount + pages * maxItems;
            }
            params.put(AtomCMIS.PARAM_SKIP_COUNT, Integer.toString(skipCount));
            params.put(AtomCMIS.PARAM_MAX_ITEMS, Integer.toString(maxItems));
            feed.addLink(request.absoluteUrlFor("feed", params), AtomCMIS.LINK_LAST, AtomCMIS.MEDIATYPE_ATOM_FEED,
               null, null, -1);
         }
      }
   }

}
