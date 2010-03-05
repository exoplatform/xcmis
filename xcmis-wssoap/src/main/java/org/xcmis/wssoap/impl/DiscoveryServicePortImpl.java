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
import org.xcmis.core.DiscoveryService;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.messaging.CmisExtensionType;
import org.xcmis.messaging.CmisObjectListType;
import org.xcmis.messaging.Query;
import org.xcmis.messaging.QueryResponse;
import org.xcmis.soap.CmisException;
import org.xcmis.soap.DiscoveryServicePort;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.object.CmisObjectList;


/**
 * @author <a href="mailto:max.shaposhnik@exoplatform.com">Max Shaposhnik</a>
 * @version $Id$
 */
@javax.jws.WebService(// name = "DiscoveryServicePort",
serviceName = "DiscoveryService", //
portName = "DiscoveryServicePort", //
targetNamespace = "http://docs.oasis-open.org/ns/cmis/ws/200908/", //
wsdlLocation = "/wsdl/CMISWS-Service.wsdl" //,
//   endpointInterface = "org.xcmis.soap.DiscoveryServicePort"
)
public class DiscoveryServicePortImpl implements DiscoveryServicePort
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(DiscoveryServicePortImpl.class);

   /** Discovery service. */
   private DiscoveryService discoveryService;

   /**
    * Constructs instance of <code>DiscoveryServicePortImpl</code> .
    * 
    * @param discoveryService QueryService
    */
   public DiscoveryServicePortImpl(DiscoveryService discoveryService)
   {
      this.discoveryService = discoveryService;
   }

   /**
    * {@inheritDoc}
    */
   public QueryResponse query(Query parameters) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation query");
      try
      {
         String repositoryId = parameters.getRepositoryId();
         String statement = parameters.getStatement();
         boolean allVersions =
            parameters.getSearchAllVersions() == null || parameters.getSearchAllVersions().isNil() ? false : parameters
               .getSearchAllVersions().getValue();
         boolean includeAllowableActions =
            parameters.getIncludeAllowableActions() == null || parameters.getIncludeAllowableActions().isNil() ? false
               : parameters.getIncludeAllowableActions().getValue();
         EnumIncludeRelationships includeRelationships =
            parameters.getIncludeRelationships() == null || parameters.getIncludeRelationships().isNil()
               ? EnumIncludeRelationships.NONE : parameters.getIncludeRelationships().getValue();
         String renditionFilter =
            parameters.getRenditionFilter() == null || parameters.getRenditionFilter().isNil() ? null : parameters
               .getRenditionFilter().getValue();
         int maxItems =
            parameters.getMaxItems() == null || parameters.getMaxItems().isNil() ? CMIS.MAX_ITEMS : parameters
               .getMaxItems().getValue().intValue();
         int skipCount =
            parameters.getSkipCount() == null || parameters.getSkipCount().isNil() ? 0 : parameters.getSkipCount()
               .getValue().intValue();

         QueryResponse response = new QueryResponse();
         CmisObjectList result =
            discoveryService.query(repositoryId, statement, allVersions, includeAllowableActions, includeRelationships,
               renditionFilter, maxItems, skipCount, false);

         response.setObjects(result.toCmisObjectList());
         return response;
      }
      catch (Exception e)
      {
         LOG.error("Query error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void getContentChanges(String repositoryId, //
      javax.xml.ws.Holder<String> changeLogToken, //
      Boolean includeProperties, //
      String propertyFilter, //
      Boolean includePolicyIds, //
      Boolean includeACL, //
      java.math.BigInteger maxItems, //
      CmisExtensionType extension, //
      javax.xml.ws.Holder<CmisObjectListType> objects) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation getContentChanges");
      try
      {
         objects.value = discoveryService.getContentChanges(repositoryId, //
            changeLogToken == null ? null : changeLogToken.value, //
            includeProperties == null ? false : includeProperties, //
            propertyFilter, //
            includePolicyIds == null ? false : includePolicyIds, //
            includeACL == null ? false : includeACL, //
            maxItems == null ? CMIS.MAX_ITEMS : maxItems.intValue());
      }
      catch (Exception e)
      {
         LOG.error("Get content changes error: " + e.getMessage());
         throw ExceptionFactory.generateException(e);
      }
   }
}
