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

package org.xcmis.wssoap.impl;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.PolicyService;
import org.xcmis.messaging.CmisExtensionType;
import org.xcmis.soap.CmisException;
import org.xcmis.soap.PolicyServicePort;


/**
 * @author <a href="mailto:max.shaposhnik@exoplatform.com">Max Shaposhnik</a>
 * @version $Id$
 */
@javax.jws.WebService(//name = "PolicyServicePort",
serviceName = "PolicyService", //
portName = "PolicyServicePort", //
targetNamespace = "http://docs.oasis-open.org/ns/cmis/ws/200908/", //
wsdlLocation = "/wsdl/CMISWS-Service.wsdl" //,
//   endpointInterface = "org.xcmis.soap.PolicyServicePort"
)
public class PolicyServicePortImpl implements PolicyServicePort
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(PolicyServicePortImpl.class);

   /** Policy service. */
   private PolicyService policyService;

   /**
    * Constructs instance of <code>PolicyServicePortImpl</code> .
    * 
    * @param policyService PolicyService
    */
   public PolicyServicePortImpl(PolicyService policyService)
   {
      this.policyService = policyService;
   }

   /**
    * {@inheritDoc}
    */
   public CmisExtensionType applyPolicy(java.lang.String repositoryId, java.lang.String policyId,
      java.lang.String objectId, CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation applyPolicy");
      try
      {
         policyService.applyPolicy(repositoryId, policyId, objectId);
      }
      catch (Exception e)
      {
         LOG.error("Apply policy error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
      return new CmisExtensionType();
   }

   /**
    * {@inheritDoc}
    */
   public java.util.List<CmisObjectType> getAppliedPolicies(java.lang.String repositoryId, java.lang.String objectId,
      java.lang.String propertyFilter, CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation getAppliedPolicies");
      try
      {
         return policyService.getAppliedPolicies(repositoryId, objectId, propertyFilter);
      }
      catch (Exception e)
      {
         LOG.error("Get applied policies error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public CmisExtensionType removePolicy(java.lang.String repositoryId, java.lang.String policyId,
      java.lang.String objectId, CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation removePolicy");
      try
      {
         policyService.removePolicy(repositoryId, policyId, objectId);
      }
      catch (Exception e)
      {
         LOG.error("Remove applied policy error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
      return new CmisExtensionType();
   }
}
