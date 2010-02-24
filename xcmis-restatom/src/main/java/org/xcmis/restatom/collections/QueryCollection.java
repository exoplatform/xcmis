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
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.context.AbstractResponseContext;
import org.apache.abdera.protocol.server.context.BaseResponseContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.DiscoveryService;
import org.xcmis.core.ObjectService;
import org.xcmis.core.RepositoryService;
import org.xcmis.core.VersioningService;
import org.xcmis.restatom.AtomUtils;
import org.xcmis.restatom.abdera.QueryTypeElement;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.RepositoryException;

import java.io.IOException;
import java.util.Calendar;

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
   public Iterable<CmisObjectType> getEntries(RequestContext request) throws ResponseContextException
   {
      try
      {
         Document<Element> doc = request.getDocument();
         QueryTypeElement queryElement = (QueryTypeElement)doc.getRoot();
         return queryService.query(getRepositoryId(request), queryElement.getStatement(),
            queryElement.isSearchAllVersions(), queryElement.isIncludeAllowableActions(),
            queryElement.getIncludeRelationships(), queryElement.getRenditionFilter(), queryElement.getPageSize(),
            queryElement.getSkipCount()).getObjects();
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

}
