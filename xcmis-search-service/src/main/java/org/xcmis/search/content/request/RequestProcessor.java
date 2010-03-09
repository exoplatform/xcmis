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

/**
 * A component that is used to process and execute {@link SearchServiceRequest}
 * s. This class is intended to be subclasses and methods overwritten to define
 * the behavior for executing the different kinds of requests. Abstract methods
 * must be overridden, but non-abstract methods all have meaningful default
 * implementations.
 */
public abstract class RequestProcessor
{
   protected boolean rollback = false;

   /**
    * Process a request by determining the type of request and delegating to the
    * appropriate <code>process</code> method for that type.
    * <p>
    * This method does nothing if the request is null.
    * </p>
    * 
    * @param request
    *           the general request
    */
   public void process(SearchContentRequest request)
   {
      if (request == null)
         return;
      try
      {
         if (request.isCancelled())
            return;
         // if (request instanceof CompositeRequest)
         // {
         // process((CompositeRequest)request);
         // }
         else
         {
            processUnknownRequest(request);
         }
      }
      catch (RuntimeException e)
      {
         request.setError(e);
      }
   }

   /**
    * Method that is called by {@link #process(SearchServiceRequest)} when the
    * request was found to be of a request type that is not known by this
    * processor. By default this method sets an
    * {@link InvalidRequestException unsupported request error} on the
    * request.
    * 
    * @param request
    *           the unknown request
    */
   protected void processUnknownRequest(SearchContentRequest request)
   {
      request.setError(new InvalidRequestException("Requests of type " + request.getClass().getName()
         + " are unsupported; actual request was to " + request));
   }

   /**
    * Mark for roll back.
    */
   public void markForRollback()
   {
      rollback = true;
   }

   /**
    * Close processor and apply or discard changes.
    */
   public void close()
   {
      if (rollback)
         rollback();
      else
         commit();
   }

   /**
    * Subclasses should implement this method to throw away any work that has
    * been done with this processor.
    */
   protected abstract void rollback();

   /**
    * Subclasses should implement this method to commit and save any work that
    * has been done with this processor.
    */
   protected abstract void commit();
}
