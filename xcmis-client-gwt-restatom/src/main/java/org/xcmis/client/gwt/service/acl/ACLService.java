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

package org.xcmis.client.gwt.service.acl;

import org.xcmis.client.gwt.CmisArguments;
import org.xcmis.client.gwt.marshallers.ApplyACLMarshaller;
import org.xcmis.client.gwt.model.acl.AccessControlList;
import org.xcmis.client.gwt.model.actions.ApplyACL;
import org.xcmis.client.gwt.rest.AsyncRequest;
import org.xcmis.client.gwt.rest.AsyncRequestCallback;
import org.xcmis.client.gwt.rest.ExceptionThrownEvent;
import org.xcmis.client.gwt.rest.HTTPHeader;
import org.xcmis.client.gwt.rest.HTTPMethod;
import org.xcmis.client.gwt.service.acl.event.ACLAppliedEvent;
import org.xcmis.client.gwt.service.acl.event.ACLReceivedEvent;
import org.xcmis.client.gwt.unmarshallers.ACLUnmarshaller;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class ACLService
{
   /**
    * Event bus.
    */
   private HandlerManager eventBus;

   /**
    * @param eventBus eventBus
    */
   public ACLService(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }

   /**
    * Adds or removes the given ACEs to or from the ACL of document or folder object pointed by url.
    * 
    * On success response received {@link ACLAppliedEvent} 
    * 
    * @param url url
    * @param applyACL applyACL
    */
   public void applyACL(String url, ApplyACL applyACL)
   {
      ACLAppliedEvent event = new ACLAppliedEvent();
      ExceptionThrownEvent errorEvent = new ExceptionThrownEvent("ACL was not applied.");
      ApplyACLMarshaller marshaller = new ApplyACLMarshaller(applyACL);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event, errorEvent);
      AsyncRequest.build(RequestBuilder.POST, url).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PUT).header(
         HTTPHeader.CONTENT_TYPE, "application/atom+xml;type=entry").data(marshaller).send(callback);
   }

   /**
    * Get the ACL currently applied to the specified by url document or folder object.
    * 
    * On success response received, {@link ACLReceivedEvent} event is fired 
    * 
    * @param url
    * @param onlyBasicPermissions
    */
   public void getACL(String url, boolean onlyBasicPermissions)
   {
      AccessControlList accessControlListType = new AccessControlList();
      ACLReceivedEvent event = new ACLReceivedEvent(accessControlListType);
      ExceptionThrownEvent errorEvent = new ExceptionThrownEvent("Access Control List was not found.");
      ACLUnmarshaller unmarshaller = new ACLUnmarshaller(accessControlListType);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, errorEvent);
      AsyncRequest.build(RequestBuilder.GET,
         url + "?" + CmisArguments.ONLY_BASIC_PERMISSIONS + "=" + onlyBasicPermissions).send(callback);
   }
}
