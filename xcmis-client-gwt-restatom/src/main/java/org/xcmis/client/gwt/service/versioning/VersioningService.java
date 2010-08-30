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

package org.xcmis.client.gwt.service.versioning;

import org.xcmis.client.gwt.CmisArguments;
import org.xcmis.client.gwt.CmisMediaTypes;
import org.xcmis.client.gwt.marshallers.CheckinMarshaller;
import org.xcmis.client.gwt.marshallers.CheckoutMarshaller;
import org.xcmis.client.gwt.model.actions.CheckIn;
import org.xcmis.client.gwt.model.actions.CheckOut;
import org.xcmis.client.gwt.model.restatom.AtomEntry;
import org.xcmis.client.gwt.model.restatom.EntryCollection;
import org.xcmis.client.gwt.rest.AsyncRequest;
import org.xcmis.client.gwt.rest.AsyncRequestCallback;
import org.xcmis.client.gwt.rest.ExceptionThrownEvent;
import org.xcmis.client.gwt.rest.HTTPHeader;
import org.xcmis.client.gwt.rest.HTTPMethod;
import org.xcmis.client.gwt.service.versioning.event.AllVersionsReceivedEvent;
import org.xcmis.client.gwt.service.versioning.event.CancelCheckoutReceivedEvent;
import org.xcmis.client.gwt.service.versioning.event.CheckinReceivedEvent;
import org.xcmis.client.gwt.service.versioning.event.CheckoutReceivedEvent;
import org.xcmis.client.gwt.unmarshallers.EntryCollectionUnmarshaller;
import org.xcmis.client.gwt.unmarshallers.EntryUnmarshaller;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class VersioningService
{
   /**
    * Event bus.
    */
   private HandlerManager eventBus;

   /**
    * @param eventBus eventBus
    */
   public VersioningService(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }

   /**
    * Create a private working copy of the document.
    * 
    * On success response received, {@link CheckoutReceivedEvent} event is fired
    * 
    * @param url url
    * @param objectId object id
    */
   public void checkOut(String url, String objectId)
   {
      AtomEntry document = new AtomEntry();
      CheckoutReceivedEvent event = new CheckoutReceivedEvent(document);
      ExceptionThrownEvent errorEvent = new ExceptionThrownEvent("Check-out the document failed.");
      CheckOut checkOut = new CheckOut();
      checkOut.setObjectId(objectId);
      CheckoutMarshaller marshaller = new CheckoutMarshaller(checkOut);
      EntryUnmarshaller unmarshaller = new EntryUnmarshaller(document);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, errorEvent);
      AsyncRequest.build(RequestBuilder.POST, url).header(HTTPHeader.CONTENT_TYPE,
         CmisMediaTypes.ATOM_ENTRY).data(marshaller).send(callback);
   }

   /**
    * Reverses the effect of a check-out. 
    * Removes the private working copy of the checked-out document, 
    * allowing other documents in the version series to be checked out again.
    * 
    * On success response received, {@link CancelCheckoutReceivedEvent} event is fired
    * 
    * @param url url
    */
   public void cancelCheckout(String url)
   {
      CancelCheckoutReceivedEvent event = new CancelCheckoutReceivedEvent();
      ExceptionThrownEvent errorEvent = new ExceptionThrownEvent("Cancel check-out the document failed.");
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event, errorEvent);
      AsyncRequest.build(RequestBuilder.POST, url).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.DELETE).send(
         callback);
   }

   /**
    * Checks-in the Private Working Copy document.
    * 
    * On success response received, {@link CheckinReceivedEvent} event is fired
    * 
    * @param url url
    * @param checkIn checkIn
    */
   public void checkin(String url, CheckIn checkIn)
   {
      AtomEntry document = new AtomEntry();
      CheckinReceivedEvent event = new CheckinReceivedEvent(document);
      ExceptionThrownEvent errorEvent = new ExceptionThrownEvent("Check-in the document failed.");
      EntryUnmarshaller unmarshaller = new EntryUnmarshaller(document);

      String params = "";
      params += CmisArguments.MAJOR + "=" + checkIn.getMajor() + "&";
      params +=
         (checkIn.getCheckinComment() == null || checkIn.getCheckinComment().length() <= 0) ? ""
            : CmisArguments.CHECKIN_COMMENT + "=" + checkIn.getCheckinComment();

      CheckinMarshaller marshaller = new CheckinMarshaller(checkIn);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, errorEvent);
      AsyncRequest.build(RequestBuilder.POST, url + "?" + CmisArguments.CHECKIN + "=true" + "&" + params).header(
         HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PUT).header(HTTPHeader.CONTENT_TYPE,
            CmisMediaTypes.ATOM_ENTRY).data(marshaller).send(callback);
   }

   /**
    * Returns the list of all Document Objects in the specified Version Series, 
    * sorted by cmis:creationDate descending.
    * 
    * On success response received, {@link AllVersionsReceivedEvent} event is fired
    *  
    * @param url url
    * @param filter filter
    * @param includeAllowableActions include allowable actions
    */
   public void getAllVersions(String url, String filter, boolean includeAllowableActions)
   {
      EntryCollection entryCollection = new EntryCollection();
      AllVersionsReceivedEvent event = new AllVersionsReceivedEvent(entryCollection);
      ExceptionThrownEvent errorEvent = new ExceptionThrownEvent("All versions were not received.");
      EntryCollectionUnmarshaller unmarshaller = new EntryCollectionUnmarshaller(entryCollection);

      String params = CmisArguments.INCLUDE_ALLOWABLE_ACTIONS + "=" + includeAllowableActions + "&";
      params += (filter == null || filter.length() <= 0) ? "" : CmisArguments.FILTER + "=" + filter;

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, errorEvent);
      AsyncRequest.build(RequestBuilder.GET, url + "?" + params).send(callback);
   }

}
