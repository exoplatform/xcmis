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
import org.xcmis.soap.CmisException;
import org.xcmis.soap.MultiFilingServicePort;
import org.xcmis.spi.CmisStorageInitializer;
import org.xcmis.spi.Connection;

/**
 * @author <a href="mailto:max.shaposhnik@exoplatform.com">Max Shaposhnik</a>
 * @version $Id: MultiFilingServicePortImpl.java 2 2010-02-04 17:21:49Z
 *          andrew00x $
 */
@javax.jws.WebService(// name = "MultiFilingServicePort",
serviceName = "MultiFilingService", //
portName = "MultiFilingServicePort", //
targetNamespace = "http://docs.oasis-open.org/ns/cmis/ws/200908/", //
wsdlLocation = "/wsdl/CMISWS-Service.wsdl" //,
//   endpointInterface = "org.xcmis.soap.MultiFilingServicePort"
)
public class MultiFilingServicePortImpl implements MultiFilingServicePort
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(MultiFilingServicePortImpl.class);

   /**
    * Constructs instance of <code>MultiFilingServicePortImpl</code> .
    *
    */
   public MultiFilingServicePortImpl()
   {
   }

   /**
    * {@inheritDoc}
    */
   public CmisExtensionType addObjectToFolder(String repositoryId, String objectId, String folderId,
      Boolean allVersions, CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation addObjectToFolder");
      }
      Connection conn = null;
      try
      {
         conn = CmisStorageInitializer.getInstance().getConnection(repositoryId);

         conn.addObjectToFolder(objectId, folderId, allVersions);
      }
      catch (Exception e)
      {
         LOG.error(e.getMessage());
         throw ExceptionFactory.generateException(e);
      }
      finally
      {
         if (conn != null)
         {
            conn.close();
         }
      }
      return new CmisExtensionType();
   }

   /**
    * {@inheritDoc}
    */
   public CmisExtensionType removeObjectFromFolder(String repositoryId, String objectId, String folderId,
      CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation removeObjectFromFolder");
      }
      Connection conn = null;
      try
      {
         conn = CmisStorageInitializer.getInstance().getConnection(repositoryId);

         conn.removeObjectFromFolder(objectId, folderId);
      }
      catch (Exception e)
      {
         LOG.error(e.getMessage());
         throw ExceptionFactory.generateException(e);
      }
      finally
      {
         if (conn != null)
         {
            conn.close();
         }
      }
      return new CmisExtensionType();
   }

}
