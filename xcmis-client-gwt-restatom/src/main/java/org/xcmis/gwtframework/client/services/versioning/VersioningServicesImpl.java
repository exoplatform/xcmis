/**
 *  Copyright (C) 2010 eXo Platform SAS.
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xcmis.gwtframework.client.services.versioning;

import org.xcmis.gwtframework.client.CmisArguments;
import org.xcmis.gwtframework.client.marshallers.CheckinMarshaller;
import org.xcmis.gwtframework.client.model.actions.CheckIn;
import org.xcmis.gwtframework.client.model.restatom.AtomEntry;
import org.xcmis.gwtframework.client.model.restatom.EntryCollection;
import org.xcmis.gwtframework.client.services.versioning.event.AllVersionsReceivedEvent;
import org.xcmis.gwtframework.client.services.versioning.event.CancelCheckoutReceivedEvent;
import org.xcmis.gwtframework.client.services.versioning.event.CheckinReceivedEvent;
import org.xcmis.gwtframework.client.services.versioning.event.CheckoutReceivedEvent;
import org.xcmis.gwtframework.client.unmarshallers.EntryCollectionUnmarshaller;
import org.xcmis.gwtframework.client.unmarshallers.EntryUnmarshaller;
import org.xcmis.gwtframework.client.util.AsyncRequest;
import org.xcmis.gwtframework.client.util.AsyncRequestCallback;
import org.xcmis.gwtframework.client.util.HTTPHeader;
import org.xcmis.gwtframework.client.util.HTTPMethod;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class VersioningServicesImpl extends VersioningServices
{
   /**
    * Event bus.
    */
   private HandlerManager eventBus;

   /**
    * @param eventBus eventBus
    */
   public VersioningServicesImpl(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }

   /**
    * @see org.xcmis.gwtframework.client.services.versioning.VersioningServices#checkOut(
    * java.lang.String, java.lang.String)
    * 
    * @param url url
    * @param objectId object id
    */
   @Override
   public void checkOut(String url, String objectId)
   {
      AtomEntry document = new AtomEntry();
      CheckoutReceivedEvent event = new CheckoutReceivedEvent(document);
      EntryUnmarshaller unmarshaller = new EntryUnmarshaller(document);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.POST, url + "?" + "objectid" + "=" + objectId).send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.versioning.VersioningServices#cancelCheckout(
    * java.lang.String)
    * 
    * @param url url
    */
   @Override
   public void cancelCheckout(String url)
   {
      CancelCheckoutReceivedEvent event = new CancelCheckoutReceivedEvent();
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event);
      AsyncRequest.build(RequestBuilder.POST, url).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.DELETE).send(
         callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.versioning.VersioningServices#checkin(
    * java.lang.String, org.xcmis.gwtframework.client.model.actions.CheckIn)
    * 
    * @param url url
    * @param checkIn checkIn
    */
   @Override
   public void checkin(String url, CheckIn checkIn)
   {
      AtomEntry document = new AtomEntry();
      CheckinReceivedEvent event = new CheckinReceivedEvent(document);
      EntryUnmarshaller unmarshaller = new EntryUnmarshaller(document);

      String params = "";
      params += CmisArguments.MAJOR + "=" + checkIn.getMajor() + "&";
      params +=
         (checkIn.getCheckinComment() == null || checkIn.getCheckinComment().length() <= 0) ? ""
            : CmisArguments.CHECKIN_COMMENT + "=" + checkIn.getCheckinComment();

      CheckinMarshaller marshaller = new CheckinMarshaller(checkIn);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.POST, url + "?" + CmisArguments.CHECKIN + "=true" + "&" + params)
         .header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PUT)
         .header(HTTPHeader.CONTENT_TYPE, "application/atom+xml;type=entry")
         .data(marshaller)
         .send(callback);
   }

   /**
    * @see org.xcmis.gwtframework.client.services.versioning.VersioningServices#getAllVersions(
    * java.lang.String, java.lang.String, boolean)
    * 
    * @param url url
    * @param filter filter
    * @param includeAllowableActions include allowable actions
    */
   @Override
   public void getAllVersions(String url, String filter, boolean includeAllowableActions)
   {
      EntryCollection entryCollection = new EntryCollection();
      AllVersionsReceivedEvent event = new AllVersionsReceivedEvent(entryCollection);
      EntryCollectionUnmarshaller unmarshaller = new EntryCollectionUnmarshaller(entryCollection);

      String params = CmisArguments.INCLUDE_ALLOWABLE_ACTIONS + "=" + includeAllowableActions + "&";
      params += (filter == null || filter.length() <= 0) ? "" : CmisArguments.FILTER + "=" + filter;

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url + "?" + params).send(callback);
   }

}
