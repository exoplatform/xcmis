/*
 * Copyright (C) 2010 eXo Platform SAS.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.xcmis.search.content.request;

import org.xcmis.search.content.SearchContentService;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The abstract base class for all classes representing requests to be executed
 * against a {@link SearchContentService}.
 */
public class SearchContentRequest implements Serializable
{
   private AtomicBoolean cancelled = new AtomicBoolean(false);

   private Throwable error;

   /** The serialVersionUID. */
   private static final long serialVersionUID = 1L;

   /**
    * Set the error for this request.
    * 
    * @param error
    *           the error to be associated with this request, or null if this
    *           request is to have no error
    */
   public void setError(Throwable error)
   {
      this.error = error;
   }

   /**
    * Return whether there is an error associated with this request.
    * 
    * @return true if there is an error, or false otherwise
    */
   public boolean hasError()
   {
      return this.error != null;
   }

   /**
    * Get the error associated with this request, if there is such an error.
    * 
    * @return the error, or null if there is none
    */
   public Throwable getError()
   {
      return error;
   }

   /**
    * Check whether this request has been cancelled. Although it is a
    * recommendation that the result of this method be followed wherever
    * possible, it is not required to immediately stop processing the request if
    * this method returns <code>true</code>. For example, if processing is
    * almost complete, it may be appropriate to simply finish processing the
    * request.
    * <p>
    * This method is safe to be called by different threads.
    * </p>
    * 
    * @return true if this request has been cancelled, or false otherwise.
    */
   public boolean isCancelled()
   {
      return cancelled.get();
   }

   /**
    * Set the cancelled state of this request. All requests are initially marked
    * as not cancelled. Note that this is designed so that the same
    * {@link AtomicBoolean} instance can be passed to multiple requests,
    * allowing a single flag to dictate the cancelled state of all of those
    * requests.
    * <p>
    * So, by default, each request should already be set up to not be cancelled,
    * so for most cases this method does not need to be called at all. This
    * method should be called when this flag is to be shared among multiple
    * requests, usually when the requests are being initialized or assembled.
    * </p>
    * 
    * @param cancelled
    *           the new (potentially shared) cancelled state for the request;
    *           may not be null
    */
   void setCancelledFlag(AtomicBoolean cancelled)
   {
      assert cancelled != null;
      this.cancelled = cancelled;
   }
}
