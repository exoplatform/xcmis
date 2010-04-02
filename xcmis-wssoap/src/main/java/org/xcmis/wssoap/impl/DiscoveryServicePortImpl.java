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
import org.xcmis.messaging.CmisExtensionType;
import org.xcmis.messaging.CmisObjectListType;
import org.xcmis.messaging.Query;
import org.xcmis.messaging.QueryResponse;
import org.xcmis.soap.CmisException;
import org.xcmis.soap.DiscoveryServicePort;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ChangeLogTokenHolder;
import org.xcmis.spi.Connection;
import org.xcmis.spi.StorageProvider;
import org.xcmis.spi.model.IncludeRelationships;

/**
 * @author <a href="mailto:max.shaposhnik@exoplatform.com">Max Shaposhnik</a>
 * @version $Id: DiscoveryServicePortImpl.java 2 2010-02-04 17:21:49Z andrew00x $
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

   /** StorageProvider. */
   private StorageProvider storageProvider;

   /**
    * Constructs instance of <code>DiscoveryServicePortImpl</code> .
    * 
    * @param storageProvider StorageProvider
    */
   public DiscoveryServicePortImpl(StorageProvider storageProvider)
   {
      this.storageProvider = storageProvider;
   }

   /**
    * {@inheritDoc}
    */
   public QueryResponse query(Query parameters) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation query");
      Connection conn = null;
      try
      {
         String repositoryId = parameters.getRepositoryId();
         conn = storageProvider.getConnection(repositoryId, null);
         String statement = parameters.getStatement();
         boolean allVersions =
            parameters.getSearchAllVersions() == null || parameters.getSearchAllVersions().isNil() ? false : parameters
               .getSearchAllVersions().getValue();
         boolean includeAllowableActions =
            parameters.getIncludeAllowableActions() == null || parameters.getIncludeAllowableActions().isNil() ? false
               : parameters.getIncludeAllowableActions().getValue();
         IncludeRelationships includeRelationships =
            parameters.getIncludeRelationships() == null || parameters.getIncludeRelationships().isNil()
               ? IncludeRelationships.NONE : IncludeRelationships.fromValue(parameters.getIncludeRelationships()
                  .getValue().value());
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
         CmisObjectListType result =
            TypeConverter.getCmisObjectListType(conn.query(statement, allVersions, includeAllowableActions,
               includeRelationships, true, renditionFilter, maxItems, skipCount));
         response.setObjects(result);
         return response;
      }
      catch (Exception e)
      {
         LOG.error("Query error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
      finally
      {
         conn.close();
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
      Connection conn = null;
      try
      {
         conn = storageProvider.getConnection(repositoryId, null);
         objects.value =
            TypeConverter.getCmisObjectListType(conn.getContentChanges(changeLogToken == null ? null
               : new ChangeLogTokenHolder(), //
               includeProperties == null ? false : includeProperties, //
               propertyFilter, //
               includePolicyIds == null ? false : includePolicyIds, //
               includeACL == null ? false : includeACL, //
               true, maxItems == null ? CMIS.MAX_ITEMS : maxItems.intValue()));
      }
      catch (Exception e)
      {
         LOG.error("Get content changes error: " + e.getMessage());
         throw ExceptionFactory.generateException(e);
      }
      finally
      {
         conn.close();
      }
   }
}
