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

package org.xcmis.wssoap.impl.server;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com.ua">Alexey
 *         Zavizionov</a>
 * @version $Id: CmisSoapServlet.java 2 2010-02-04 17:21:49Z andrew00x $ Sep 17,
 *          2008
 */
public class CmisSoapServlet extends CXFNonSpringServlet
{
   /** VersionUID. */
   private static final long serialVersionUID = 8525887555654944318L;

   /**
    * Load bus.
    *
    * @param servletConfig ServletConfig
    * @see org.apache.cxf.transport.servlet.CXFNonSpringServlet#loadBus(javax.servlet.ServletConfig)
    * @throws ServletException servlet exception
    */
   @Override
   public void loadBus(ServletConfig servletConfig) throws ServletException
   {
      super.loadBus(servletConfig);
      WebServiceLoader loader =
         (WebServiceLoader)servletConfig.getServletContext().getAttribute("org.xcmis.wssoap.WebServiceLoader");
      if (loader == null)
      {
         loader = new WebServiceLoader();
         loader.init();
         servletConfig.getServletContext().setAttribute("org.xcmis.wssoap.WebServiceLoader", loader);
      }
      Bus bus = getBus();
      BusFactory.setDefaultBus(bus);
   }

}
