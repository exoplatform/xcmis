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
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.xcmis.restatom.AtomUtils;
import org.xcmis.restatom.abdera.QueryTypeElement;
import org.xcmis.spi.Connection;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.model.CmisObject;

import java.io.IOException;
import java.util.Calendar;

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
      {
         rc.setStatus(201);
      }
      return rc;
   }

   /**
    * {@inheritDoc}
    */
   public Iterable<CmisObject> getEntries(RequestContext request) throws ResponseContextException
   {
      Connection conn = null;
      try
      {
         conn = getConnection(request);
         Document<Element> doc = request.getDocument();
         QueryTypeElement queryElement = (QueryTypeElement)doc.getRoot();
         return conn.query(queryElement.getStatement(), queryElement.isSearchAllVersions(),
            queryElement.isIncludeAllowableActions(), queryElement.getIncludeRelationships(), true,
            queryElement.getRenditionFilter(), queryElement.getPageSize(), queryElement.getSkipCount()).getItems();
      }
      catch (ParseException pe)
      {
         throw new ResponseContextException(createErrorResponse(pe, 500));
      }
      catch (IOException ioe)
      {
         throw new ResponseContextException(createErrorResponse(ioe, 500));
      }
      catch (StorageException re)
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
      finally
      {
         if (conn != null)
         {
            conn.close();
         }
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
