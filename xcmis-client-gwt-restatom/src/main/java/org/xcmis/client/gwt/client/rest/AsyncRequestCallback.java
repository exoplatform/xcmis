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

package org.xcmis.client.gwt.client.rest;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;

/**
 * Created by The eXo Platform SAS        .
 * @version $Id: $
 */

public class AsyncRequestCallback implements RequestCallback
{

   // http code 207 is "Multi-Status"
   //IE misinterpreting HTTP status code 204 as 1223 (http://www.mail-archive.com/jquery-en@googlegroups.com/msg13093.html)

   private static final int[] DEFAULT_SUCCESS_CODES =
      {Response.SC_OK, Response.SC_CREATED, Response.SC_NO_CONTENT, 207, 1223};

   private final HandlerManager eventBus;

   private final int[] successCodes;

   protected final Unmarshallable payload;

   protected final GwtEvent<?> postEvent;

   protected final ServerExceptionEvent<?> exceptionEvent;

   public AsyncRequestCallback(final HandlerManager eventBus, GwtEvent<?> postEvent)
   {
      this(eventBus, null, postEvent, DEFAULT_SUCCESS_CODES);
   }

   public AsyncRequestCallback(final HandlerManager eventBus, GwtEvent<?> postEvent,
      ServerExceptionEvent<?> exceptionEvent)
   {
      this(eventBus, null, postEvent, exceptionEvent, DEFAULT_SUCCESS_CODES);
   }

   public AsyncRequestCallback(final HandlerManager eventBus, Unmarshallable payload, GwtEvent<?> postEvent)
   {
      this(eventBus, payload, postEvent, DEFAULT_SUCCESS_CODES);
   }

   public AsyncRequestCallback(final HandlerManager eventBus, Unmarshallable payload, GwtEvent<?> postEvent,
      final int[] successCodes)
   {
      this(eventBus, payload, postEvent, null, successCodes);
   }

   public AsyncRequestCallback(final HandlerManager eventBus, Unmarshallable payload, GwtEvent<?> postEvent,
      ServerExceptionEvent<?> exceptionEvent)
   {
      this(eventBus, payload, postEvent, exceptionEvent, DEFAULT_SUCCESS_CODES);
   }

   public AsyncRequestCallback(final HandlerManager eventBus, Unmarshallable payload, GwtEvent<?> postEvent,
      ServerExceptionEvent<?> exceptionEvent, final int[] successCodes)
   {
      this.payload = payload;
      this.postEvent = postEvent;
      this.exceptionEvent = exceptionEvent;
      this.eventBus = eventBus;
      this.successCodes = successCodes;
   }

   public void onError(Request request, Throwable exception)
   {
      if (Loader.getInstance() != null)
      {
         Loader.getInstance().hide();
      }
      fireEvent(new ExceptionThrownEvent(exception));
   }

   public void onResponseReceived(Request request, Response response)
   {
      if (Loader.getInstance() != null)
      {
         Loader.getInstance().hide();
      }

      if (success(response))
      {
         try
         {
            if (payload != null)
               payload.unmarshal(response.getText());

            if (postEvent != null)
               fireEvent(postEvent);
         }
         catch (Exception e)
         {
            fireEvent(new ExceptionThrownEvent(e));
         }
      }
      else
      {
         if (exceptionEvent != null)
         {
            exceptionEvent.setException(new ServerException(response));
            fireEvent(exceptionEvent);
         }
         else
         {
            fireEvent(new ExceptionThrownEvent(new ServerException(response)));
         }
      }

   }

   protected final boolean success(Response response)
   {
      for (int code : successCodes)
         if (response.getStatusCode() == code)
            return true;
      return false;
   }

   protected final void fireEvent(GwtEvent<?> event)
   {
      this.eventBus.fireEvent(event);
   }

}
