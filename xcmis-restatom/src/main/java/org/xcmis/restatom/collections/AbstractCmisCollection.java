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

import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.context.AbstractResponseContext;
import org.apache.abdera.protocol.server.context.BaseResponseContext;
import org.apache.abdera.protocol.server.context.EmptyResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.apache.abdera.protocol.server.impl.AbstractEntityCollectionAdapter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.restatom.ProviderImpl;
import org.xcmis.spi.CmisRegistry;
import org.xcmis.spi.Connection;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: AbstractCmisCollection.java 2634 2009-08-12 06:26:41Z andrew00x
 *          $
 */
public abstract class AbstractCmisCollection<T> extends AbstractEntityCollectionAdapter<T>
{

   /** The logger. */
   private static final Log LOG = ExoLogger.getLogger(AbstractCmisCollection.class);

   /**
    * Instantiates a new abstract cmis collection.
    *
    */
   public AbstractCmisCollection()
   {
   }

   /**
    * {@inheritDoc}
    */
   public void deleteEntry(String resourceName, RequestContext request) throws ResponseContextException
   {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */
   public Object getContent(T entry, RequestContext request) throws ResponseContextException
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public ResponseContext postEntry(RequestContext request)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */
   public T postEntry(String title, IRI id, String summary, Date updated, List<Person> authors, Content content,
      RequestContext request) throws ResponseContextException
   {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */
   public void putEntry(T entry, String title, Date updated, List<Person> authors, String summary, Content content,
      RequestContext request) throws ResponseContextException
   {
      throw new UnsupportedOperationException();
   }

   /**
    * @param id id
    * @param feed feed to which are added links
    * @param atomdocType type of collections. See {@link ProviderImpl}.
    * @param maxItems max items in each response
    * @param skipCount number of skipped results from the begin of set
    * @param total total number items in result set. If total number is unknown
    *        then this parameter must be set as -1.
    * @param hasMore true if has more items in result set false otherwise
    * @param request request context
    */
   protected void addPageLinks(String id, Feed feed, String atomdocType, int maxItems, int skipCount, int total,
      boolean hasMore, RequestContext request)
   {
      Map<String, String> params = new HashMap<String, String>();
      params.put("repoid", getRepositoryId(request));
      params.put("atomdoctype", atomdocType);
      params.put("id", id);
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

   /**
    * {@inheritDoc}
    */
   protected ResponseContext buildCreateEntryResponse(String link, Entry entry)
   {
      Document<Entry> doc = entry.getDocument();
      ResponseContext rc = new BaseResponseContext<Document<Entry>>(doc);
      rc.setLocation(link);
      rc.setContentLocation(rc.getLocation().toString());
      //      rc.setEntityTag(ProviderHelper.calculateEntityTag(entry));
      rc.setStatus(201);
      return rc;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected ResponseContext buildGetEntryResponse(RequestContext request, Entry entry) throws ResponseContextException
   {
      // The same as in super class but without ETag.
      Document<Entry> doc = entry.getDocument();
      ResponseContext rc = new BaseResponseContext<Document<Entry>>(doc);
      //      rc.setEntityTag(ProviderHelper.calculateEntityTag(entry));
      return rc;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected ResponseContext buildGetFeedResponse(Feed feed)
   {
      // The same as in super class but without ETag.
      Document<Feed> document = feed.getDocument();
      AbstractResponseContext rc = new BaseResponseContext<Document<Feed>>(document);
      //      rc.setEntityTag(calculateEntityTag(document.getRoot()));
      return rc;
   }

   /**
    * Creates the error response.
    *
    * @param msg the msg
    * @param status the status
    *
    * @return the response context
    */
   protected ResponseContext createErrorResponse(String msg, int status)
   {
      LOG.error(msg);
      return new EmptyResponseContext(status, msg);
   }

   /**
    * Creates the error response.
    *
    * @param t the t
    * @param status the status
    *
    * @return the response context
    */
   protected ResponseContext createErrorResponse(Throwable t, int status)
   {
      LOG.error(t.getMessage(), t);
      return new EmptyResponseContext(status, t.getMessage());
   }

   /**
    * Create link to object type description.
    *
    * @param id object type id
    * @param request request context
    * @return link to AtomPub Document that describes object type
    */
   protected String getObjectTypeLink(String id, RequestContext request)
   {
      Map<String, String> params = new HashMap<String, String>();
      params.put("repoid", getRepositoryId(request));
      params.put("atomdoctype", "typebyid");
      params.put("id", id);
      String type = request.absoluteUrlFor(TargetType.ENTRY, params);
      return type;
   }

   /**
    * Get id of CMIS repository.
    *
    * @param request RequestContext
    * @return repositoryId string
    */
   protected String getRepositoryId(RequestContext request)
   {
      String id = request.getTarget().getParameter("repoid");
      try
      {
         return URLDecoder.decode(id, "UTF-8");
      }
      catch (UnsupportedEncodingException ex)
      {
         return id;
      }
   }

   /**
    * Create link to AtomPub Service Document contains the set of repositories
    * that are available.
    *
    * @param request the request context
    * @return link to AtomPub Service Document
    */
   protected String getServiceLink(RequestContext request)
   {
      Map<String, String> p = new HashMap<String, String>();
      p.put("repoid", getRepositoryId(request));
      String service = request.absoluteUrlFor(TargetType.SERVICE, p);
      return service;
   }

   /**
    * To get Connection for provided repository Id within request.
    *
    * @param request the request context
    * @return the Connection to CMIS storage
    */
   protected Connection getConnection(RequestContext request)
   {
      return CmisRegistry.getInstance().getConnection(getRepositoryId(request));
   }

   protected boolean getBooleanParameter(RequestContext request, String name, boolean defaultValue)
   {
      String param = request.getParameter(name);
      if (param != null && param.length() > 0)
      {
         return Boolean.parseBoolean(param);
      }
      else
      {
         return defaultValue;
      }
   }

   protected Integer getIntegerParameter(RequestContext request, String name, Integer defaultValue)
      throws ResponseContextException
   {
      Integer result;
      String param = request.getParameter(name);
      if (param != null && param.length() > 0)
      {
         try
         {
            result = new Integer(param);
         }
         catch (NumberFormatException nfe)
         {
            String msg = "Invalid parameter for name '" + name + "' with value: '" + name + "'";
            throw new ResponseContextException(msg, 400);
         }
      }
      else
      {
         result = defaultValue;
      }
      return result;
   }

}
