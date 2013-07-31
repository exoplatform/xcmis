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
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.restatom.AtomUtils;
import org.xcmis.restatom.abdera.ObjectTypeElement;
import org.xcmis.spi.ChangeLogTokenHolder;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.Connection;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.model.CmisObject;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class ChangesLogCollection extends AbstractCmisCollection<CmisObject>
{

   public ChangesLogCollection(Connection connection)
   {
      super(connection);
      setHref("/changes");
   }

   /**
    * {@inheritDoc}
    */
   public String getAuthor(RequestContext request) throws ResponseContextException
   {
      // To be conform with Atom protocol.
      return SYSTEM;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public List<Person> getAuthors(CmisObject object, RequestContext request) throws ResponseContextException
   {
      Person p = request.getAbdera().getFactory().newAuthor();
      p.setName(getAuthor(request));
      return Collections.singletonList(p);
   }

   /**
    * {@inheritDoc}
    */
   public Iterable<CmisObject> getEntries(RequestContext request) throws ResponseContextException
   {
      throw new UnsupportedOperationException("entries");
   }

   /**
    * {@inheritDoc}
    */
   public CmisObject getEntry(String resourceName, RequestContext request) throws ResponseContextException
   {
      throw new UnsupportedOperationException("entry");
   }

   /**
    * {@inheritDoc}
    */
   public String getId(CmisObject object) throws ResponseContextException
   {
      return object.getObjectInfo().getId();
   }

   /**
    * {@inheritDoc}
    */
   public String getId(RequestContext request)
   {
      return "cmis:changes:" + getRepositoryId(request);
   }

   /**
    * {@inheritDoc}
    */
   public String getName(CmisObject object) throws ResponseContextException
   {
      return "changes";
   }

   /**
    * {@inheritDoc}
    */
   public String getTitle(CmisObject object) throws ResponseContextException
   {
      return "Change log event";
   }

   /**
    * {@inheritDoc}
    */
   public String getTitle(RequestContext request)
   {
      return "Changes log";
   }

   /**
    * {@inheritDoc}
    */
   public Date getUpdated(CmisObject object) throws ResponseContextException
   {
      Calendar changeTime = object.getChangeInfo().getChangeTime();
      if (changeTime != null)
      {
         return changeTime.getTime();
      }
      // To be conform with Atom protocol.
      return new Date();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected String addEntryDetails(RequestContext request, Entry entry, IRI feedIri, CmisObject object)
      throws ResponseContextException
   {
      String objectId = getId(object);
      entry.setId(objectId);
      // Updated and published is incorrect when pass Date.
      // Abdera uses Calendar.getInstance(TimeZone.getTimeZone("GMT"))
      // See org.apache.abdera.model.AtomDate .
      entry.setUpdated(AtomUtils.getAtomDate(getUpdated(object)));
      entry.setSummary("Change Log Even");
      for (Person person : getAuthors(object, request))
      {
         entry.addAuthor(person);
      }

      entry.setTitle(getTitle(object));

      // Service link.
      String service = getServiceLink(request);
      entry.addLink(service, AtomCMIS.LINK_SERVICE, AtomCMIS.MEDIATYPE_ATOM_SERVICE, null, null, -1);

      ObjectTypeElement objectElement = new ObjectTypeElement(request.getAbdera().getFactory(), AtomCMIS.OBJECT);
      objectElement.build(object);
      entry.addExtension(objectElement);

      return objectId;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void addFeedDetails(Feed feed, RequestContext request) throws ResponseContextException
   {
      try
      {
         String changeLogToken = request.getParameter(AtomCMIS.PARAM_CHANGE_LOG_TOKEN);
         boolean includeProperties = getBooleanParameter(request, AtomCMIS.PARAM_INCLUDE_PROPERTIES, false);
         boolean includePolicyIds = getBooleanParameter(request, AtomCMIS.PARAM_INCLUDE_POLICY_IDS, false);
         boolean includeAcl = getBooleanParameter(request, AtomCMIS.PARAM_INCLUDE_ACL, false);
         String propertyFilter = request.getParameter(AtomCMIS.PARAM_FILTER);
         int maxItems = getIntegerParameter(request, AtomCMIS.PARAM_MAX_ITEMS, CmisConstants.MAX_ITEMS);

         Connection connection = getConnection(request);

         ChangeLogTokenHolder changeLogTokenHolder = new ChangeLogTokenHolder();
         if (changeLogToken != null)
         {
            changeLogTokenHolder.setValue(changeLogToken);
         }
         ItemsList<CmisObject> list =
            connection.getContentChanges(changeLogTokenHolder, includeProperties, propertyFilter, includePolicyIds,
               includeAcl, true, maxItems);

         addPageLinks(changeLogTokenHolder.getValue(), feed, "changes", maxItems, -1, list.getNumItems(), list
            .isHasMoreItems(), request);

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
      catch (FilterNotValidException fe)
      {
         throw new ResponseContextException(createErrorResponse(fe, 400));
      }
      catch (ConstraintException cve)
      {
         throw new ResponseContextException(createErrorResponse(cve, 409));
      }
      catch (Exception t)
      {
         throw new ResponseContextException(createErrorResponse(t, 500));
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void addPageLinks(String changeLogToken, Feed feed, String atomdocType, int maxItems, int skipCount,
      int total, boolean hasMore, RequestContext request)
   {
      Map<String, String> params = new HashMap<String, String>();
      params.put("repoid", getRepositoryId(request));
      params.put("atomdoctype", atomdocType);
      params.put(AtomCMIS.PARAM_CHANGE_LOG_TOKEN, changeLogToken);
      // Only next link will be provided. Next link may be provided even
      // there is no more change events yet. Client should revisit the
      // feed in future to get new set of changes.
      if (maxItems != CmisConstants.MAX_ITEMS)
         params.put(AtomCMIS.PARAM_MAX_ITEMS, Integer.toString(maxItems));
      feed.addLink(request.absoluteUrlFor("feed", params), AtomCMIS.LINK_NEXT, AtomCMIS.MEDIATYPE_ATOM_FEED, null,
         null, -1);
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
      // Updated is incorrect when pass Date.
      // Abdera uses Calendar.getInstance(TimeZone.getTimeZone("GMT"))
      // See org.apache.abdera.model.AtomDate .
      feed.setUpdated(AtomUtils.getAtomDate(Calendar.getInstance()));
      feed.addLink(getServiceLink(request), "service", "application/atomsvc+xml", null, null, -1);
      return feed;
   }

}
