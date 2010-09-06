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
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com.ua">Alexey
 *         Zavizionov</a>
 * @version $Id: CmisSoapServletExt.java 2 2010-02-04 17:21:49Z andrew00x $ Sep 17, 2008
 */
public class CmisSoapServletExt extends CXFNonSpringServlet
{

   /** VersionUID. */
   private static final long serialVersionUID = -8056126542859194574L;

   /** Logger.  */
   private static final Log LOG = ExoLogger.getLogger(CmisSoapServletExt.class);

   /**
    * The filter configuration
    */
   protected ServletConfig config;

   /**
    * The Servlet context name
    */
   protected String servletContextName;

   /**
    * Indicates if we need a portal environment.
    */
   private volatile Boolean requirePortalEnvironment;

   /**
    * {@inheritDoc}
    */
   public final void init(ServletConfig config) throws ServletException
   {
      this.config = config;
      this.servletContextName = config.getServletContext().getServletContextName();
      super.init(config);
      afterInit(config);
   }

   /**
    * Allows sub-classes to initialize 
    * @param config the current servlet configuration
    */
   protected void afterInit(ServletConfig config) throws ServletException
   {
   }

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
      if (LOG.isDebugEnabled())
         LOG.debug("loadBus method entering");

      final ExoContainer oldContainer = ExoContainerContext.getCurrentContainer();
      // Keep the old ClassLoader
      final ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
      ExoContainer container = null;
      boolean hasBeenSet = false;
      try
      {
         container = getContainer();
         if (!container.equals(oldContainer))
         {
            if (container instanceof PortalContainer)
            {
               PortalContainer.setInstance((PortalContainer)container);
            }
            ExoContainerContext.setCurrentContainer(container);
            hasBeenSet = true;
         }
         if (requirePortalEnvironment() && container instanceof PortalContainer)
         {
            if (PortalContainer.getInstanceIfPresent() == null)
            {
               // The portal container has not been set
               PortalContainer.setInstance((PortalContainer)container);
               hasBeenSet = true;
            }
            // Set the full classloader of the portal container
            Thread.currentThread().setContextClassLoader(((PortalContainer)container).getPortalClassLoader());
         }
         onService(container);
      }
      finally
      {
         if (hasBeenSet)
         {
            if (container instanceof PortalContainer)
            {
               // Remove the current Portal Container and the current ExoContainer
               PortalContainer.setInstance(null);
            }
            // Re-set the old container
            ExoContainerContext.setCurrentContainer(oldContainer);
         }
         if (requirePortalEnvironment())
         {
            // Re-set the old classloader
            Thread.currentThread().setContextClassLoader(currentClassLoader);
         }
      }
   }

   /**
    * Allow the sub classes to execute a task when the method <code>service</code> is called 
    * @param container the eXo container
    * @param req the {@link HttpServletRequest}
    * @param res the {@link HttpServletResponse}
    */
   protected void onService(ExoContainer container)
   {

      if (LOG.isDebugEnabled())
         LOG.debug("SOAPServlet.onService() container = " + container);

      WebServiceLoader loader = (WebServiceLoader)container.getComponentInstance(WebServiceLoader.class);
      if (LOG.isDebugEnabled())
         LOG.debug("SOAPServlet.onService() loader = " + loader);
      loader.init();

      Bus bus = getBus();
      BusFactory.setDefaultBus(bus);
   }

   /**
    * @return Gives the {@link ExoContainer} that fits best with the current context
    */
   protected final ExoContainer getContainer()
   {
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      if (container instanceof RootContainer)
      {
         // The top container is a RootContainer, thus we assume that we are in a portal mode
         container = PortalContainer.getCurrentInstance(config.getServletContext());
         if (container == null)
         {
            container = ExoContainerContext.getTopContainer();
         }
      }
      // The container is a PortalContainer or a StandaloneContainer
      return container;
   }

   /**
    * Indicates if it requires that a full portal environment must be set
    * @return <code>true</code> if it requires the portal environment <code>false</code> otherwise.
    */
   protected boolean requirePortalEnvironment()
   {
      if (requirePortalEnvironment == null)
      {
         synchronized (this)
         {
            if (requirePortalEnvironment == null)
            {
               this.requirePortalEnvironment = PortalContainer.isPortalContainerName(servletContextName);
            }
         }
      }
      return requirePortalEnvironment.booleanValue();
   }

}
