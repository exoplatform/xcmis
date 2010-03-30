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

package org.xcmis.gwtframework.client.services.acl;

import org.xcmis.gwtframework.client.CmisArguments;
import org.xcmis.gwtframework.client.marshallers.ApplyACLMarshaller;
import org.xcmis.gwtframework.client.model.acl.CmisAccessControlListType;
import org.xcmis.gwtframework.client.model.actions.ApplyACL;
import org.xcmis.gwtframework.client.services.acl.event.ACLAppliedEvent;
import org.xcmis.gwtframework.client.services.acl.event.ACLReceivedEvent;
import org.xcmis.gwtframework.client.unmarshallers.ACLUnmarshaller;
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
public class ACLServicesImpl extends ACLServices
{
   /**
    * Event bus.
    */
   private HandlerManager eventBus;

   /**
    * @param eventBus eventBus
    */
   public ACLServicesImpl(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }

   /**
    * @param url 
    *             url
    * @param applyACL 
    *             applyACL
    * 
    * @see org.xcmis.gwtframework.client.services.acl.ACLServices#applyACL(java.lang.String, 
    * org.xcmis.gwtframework.client.model.actions.ApplyACL)
    * 
    */
   @Override
   public void applyACL(String url, ApplyACL applyACL)
   {
      ACLAppliedEvent event = new ACLAppliedEvent();
      ApplyACLMarshaller marshaller = new ApplyACLMarshaller(applyACL);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event);
      AsyncRequest.build(RequestBuilder.POST, url).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PUT).header(
         HTTPHeader.CONTENT_TYPE, "application/atom+xml;type=entry").data(marshaller).send(callback);
   }

   /**
    * @param url url
    * @param onlyBasicPermissions onlyBasicPermissions
    * 
    * @see org.xcmis.gwtframework.client.services.acl.ACLServices#getACL(java.lang.String, boolean)
    */
   @Override
   public void getACL(String url, boolean onlyBasicPermissions)
   {
      CmisAccessControlListType accessControlListType = new CmisAccessControlListType();
      ACLReceivedEvent event = new ACLReceivedEvent(accessControlListType);
      ACLUnmarshaller unmarshaller = new ACLUnmarshaller(accessControlListType);
      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event);
      AsyncRequest.build(RequestBuilder.GET, url + "?" + CmisArguments.ONLY_BASIC_PERMISSIONS 
         + "=" + onlyBasicPermissions).send(callback);
   }
}
