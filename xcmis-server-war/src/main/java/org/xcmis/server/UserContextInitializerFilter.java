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

package org.xcmis.server;

import org.xcmis.spi.UserContext;
import org.xcmis.spi.utils.Logger;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * Set current context if user authenticated in web-container.
 *
 * @see UserContext
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class UserContextInitializerFilter implements Filter
{

   private static final Logger LOG = Logger.getLogger(UserContextInitializerFilter.class);

   /**
    * {@inheritDoc}
    */
   public void destroy()
   {
   }

   /**
    * {@inheritDoc}
    */
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
      ServletException
   {
      HttpServletRequest httpRequest = (HttpServletRequest)request;
      String userId = httpRequest.getRemoteUser();
      try
      {
         if (userId != null)
         {
            UserContext ctx = new UserContext(userId);
            UserContext.setCurrent(ctx);
            if (LOG.isDebugEnabled())
            {
               LOG.debug("Set current context for user: " + userId);
            }
         }
         chain.doFilter(request, response);
      }
      finally
      {
         try
         {
            UserContext.setCurrent(null);
         }
         catch (Exception e)
         {
            LOG.warn(e.getMessage(), e);
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public void init(FilterConfig filterConfig) throws ServletException
   {
   }

}
