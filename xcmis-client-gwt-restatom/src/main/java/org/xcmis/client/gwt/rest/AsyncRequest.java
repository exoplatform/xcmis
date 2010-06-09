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

package org.xcmis.client.gwt.rest;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.RequestBuilder.Method;

/**
 * Created by The eXo Platform SAS        .
 * @version $Id: $
 */

public class AsyncRequest
{
   
   protected RequestBuilder builder;

   protected AsyncRequest(RequestBuilder builder)
   {
      this.builder = builder;
   }

   public static final AsyncRequest build(Method method, String url)
   {
      String checkedURL = ProxyUtil.getCheckedURL(url);
      return new AsyncRequest(new RequestBuilder(method, checkedURL));
   }

   public final AsyncRequest header(String header, String value)
   {
      builder.setHeader(header, value);
      return this;
   }

   public final AsyncRequest user(String user)
   {
      builder.setUser(user);
      return this;
   }

   public final AsyncRequest password(String password)
   {
      builder.setPassword(password);
      return this;
   }

   public final AsyncRequest data(String requestData)
   {
      builder.setRequestData(requestData);
      return this;
   }

   public final AsyncRequest data(Marshallable requestMarshaller)
   {
      builder.setRequestData(requestMarshaller.marshal());
      return this;
   }

   public final void send(AsyncRequestCallback callback)
   {
      builder.setCallback(callback);

      if (Loader.getInstance() != null){
         Loader.getInstance().show();
      }

      try
      {
         builder.send();
      }
      catch (RequestException e)
      {
         if (Loader.getInstance() != null){
            Loader.getInstance().hide();
         }
         callback.fireEvent(new ExceptionThrownEvent(e));
      }
   }

}
