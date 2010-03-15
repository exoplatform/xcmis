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

import org.apache.cxf.endpoint.ServerImpl;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.core.AccessControlService;
import org.xcmis.core.DiscoveryService;
import org.xcmis.core.MultifilingService;
import org.xcmis.core.NavigationService;
import org.xcmis.core.ObjectService;
import org.xcmis.core.PolicyService;
import org.xcmis.core.RelationshipService;
import org.xcmis.core.RepositoryService;
import org.xcmis.core.VersioningService;
import org.xcmis.wssoap.impl.ACLServicePortImpl;
import org.xcmis.wssoap.impl.DiscoveryServicePortImpl;
import org.xcmis.wssoap.impl.MultiFilingServicePortImpl;
import org.xcmis.wssoap.impl.NavigationServicePortImpl;
import org.xcmis.wssoap.impl.ObjectServicePortImpl;
import org.xcmis.wssoap.impl.PolicyServicePortImpl;
import org.xcmis.wssoap.impl.RelationshipServicePortImpl;
import org.xcmis.wssoap.impl.RepositoryServicePortImpl;
import org.xcmis.wssoap.impl.VersioningServicePortImpl;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com.ua">Alexey
 *         Zavizionov</a>
 * @version $Id: WebServiceLoader.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class WebServiceLoader
{

   /**
    * Logger.
    */
   private static final Log LOG = ExoLogger.getLogger(WebServiceLoader.class);

   private final AccessControlService aclService;

   private final DiscoveryService queryService;

   private final MultifilingService multifilingService;

   private final NavigationService navigationService;

   private final ObjectService objectService;

   private final PolicyService policyService;

   private final RelationshipService relationshipService;

   private final RepositoryService repositoryService;

   private final VersioningService versioningService;

   /**
    * Constructs instance of WebServiceLoader.
    * 
    * @param containerContext the ExoContainer context.
    */
   public WebServiceLoader(AccessControlService aclService, DiscoveryService queryService,
      MultifilingService multifilingService, NavigationService navigationService, ObjectService objectService,
      PolicyService policyService, RelationshipService relationshipService, RepositoryService repositoryService,
      VersioningService versioningService)
   {
      this.aclService = aclService;
      this.queryService = queryService;
      this.multifilingService = multifilingService;
      this.navigationService = navigationService;
      this.objectService = objectService;
      this.policyService = policyService;
      this.relationshipService = relationshipService;
      this.repositoryService = repositoryService;
      this.versioningService = versioningService;
   }

   /**
    * Register all available container components in a CXF engine from Servlet.
    * 
    */
   public void init()
   {
      this.init(null);
   }

   /**
    * Register all available container components in a CXF engine from Servlet with given baseURL.
    * 
    * @param baseURL string base URL
    */
   public void init(String baseURL)
   {
      List<Object> services = new ArrayList<Object>();
      services.add(new ACLServicePortImpl(aclService));
      services.add(new DiscoveryServicePortImpl(queryService));
      services.add(new MultiFilingServicePortImpl(multifilingService));
      services.add(new NavigationServicePortImpl(navigationService));
      services.add(new ObjectServicePortImpl(objectService));
      services.add(new PolicyServicePortImpl(policyService));
      services.add(new RelationshipServicePortImpl(relationshipService));
      services.add(new RepositoryServicePortImpl(repositoryService));
      services.add(new VersioningServicePortImpl(versioningService));

      for (Object implementor : services)
      {
         String address = getAddress(baseURL, implementor);
         if (address != null)
         {
            deployService(address, implementor);
            LOG.info("New singleton WebService '" + address + "' registered.");
         }
      }
   }

   /**
    * Get service address with WebService annotation for implementor.
    * 
    * @param baseURL string base URL
    * @param implementor object implementor
    * @return address string
    */
   private String getAddress(String baseURL, Object implementor)
   {
      String serviceName = implementor.getClass().getAnnotation(WebService.class).serviceName();
      String portName = implementor.getClass().getAnnotation(WebService.class).portName();

      if (LOG.isDebugEnabled())
         LOG.debug(" serviceName = " + serviceName);
      if (LOG.isDebugEnabled())
         LOG.debug(" portName = " + portName);

      String address = new String();

      if (baseURL != null && baseURL.length() != 0)
         address += baseURL;

      if (serviceName != null && serviceName.length() != 0)
         address += "/" + serviceName;

      if (portName != null && portName.length() != 0)
         address += "/" + portName;

      if (LOG.isDebugEnabled())
      {
         LOG.debug("getAddress() - address = " + address);
         LOG.debug("getAddress() - implementor = " + implementor);
      }
      return address;
   }

   /**
    * Simple deploy service. Uses Endpoint class.
    * 
    * @param address string service address
    * @param object service object
    * @return endpoint Endpoint
    */
   public static Endpoint simpleDeployService(String address, Object object)
   {

      if (LOG.isDebugEnabled())
         LOG.debug("Starting Service: object = " + object + " at the address = " + address);
      Endpoint endpoint = Endpoint.publish(address, object);

      if (LOG.isDebugEnabled())
      {
         org.apache.cxf.jaxws.EndpointImpl endpointImpl = (org.apache.cxf.jaxws.EndpointImpl)endpoint;
         ServerImpl server = endpointImpl.getServer();
         server.getEndpoint().getInInterceptors().add(new LoggingInInterceptor());
         server.getEndpoint().getOutInterceptors().add(new LoggingOutInterceptor());
         //    server.getEndpoint().getOutFaultInterceptors().add(new FaultThrowingInterceptor());
      }

      if (endpoint.isPublished())
         LOG.info("The webservice '" + address + "' has been published SUCCESSFUL!");
      return endpoint;
   }

   /**
    * Simple deploy service. Uses Endpoint class.
    * 
    * @param address string service address
    * @param object service object
    * @return endpoint Endpoint
    */
   static Endpoint deployService(String address, Object object)
   {

      if (LOG.isDebugEnabled())
         LOG.debug("Starting Service: object = " + object + " at the address = " + address);
      Endpoint endpoint = Endpoint.publish(address, object);

      if (LOG.isDebugEnabled())
      {
         org.apache.cxf.jaxws.EndpointImpl endpointImpl = (org.apache.cxf.jaxws.EndpointImpl)endpoint;
         ServerImpl server = endpointImpl.getServer();
         server.getEndpoint().getInInterceptors().add(new LoggingInInterceptor());
         server.getEndpoint().getOutInterceptors().add(new LoggingOutInterceptor());
         //    server.getEndpoint().getOutFaultInterceptors().add(new FaultThrowingInterceptor());
      }

      if (endpoint.isPublished())
         LOG.info("The webservice '" + address + "' has been published SUCCESSFUL!");
      return endpoint;
   }
}
