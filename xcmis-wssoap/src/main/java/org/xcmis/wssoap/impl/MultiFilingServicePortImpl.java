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
import org.xcmis.core.MultifilingService;
import org.xcmis.messaging.CmisExtensionType;
import org.xcmis.soap.CmisException;
import org.xcmis.soap.MultiFilingServicePort;


/**
 * @author <a href="mailto:max.shaposhnik@exoplatform.com">Max Shaposhnik</a>
 * @version $Id$
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

   /** Multifiling service. */
   private MultifilingService multiService;

   /**
    * Constructs instance of <code>MultiFilingServicePortImpl</code> .
    * 
    * @param multiService MultifilingService
    */
   public MultiFilingServicePortImpl(MultifilingService multiService)
   {
      this.multiService = multiService;
   }

   /**
    * {@inheritDoc}
    */
   public CmisExtensionType addObjectToFolder(String repositoryId, String objectId, String folderId,
      Boolean allVersions, CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation addObjectToFolder");
      try
      {
         multiService.addObjectToFolder(repositoryId, objectId, folderId, allVersions);
      }
      catch (Exception e)
      {
         LOG.error(e.getMessage());
         throw ExceptionFactory.generateException(e);
      }
      return new CmisExtensionType();
   }

   /**
    * {@inheritDoc}
    */
   public CmisExtensionType removeObjectFromFolder(String repositoryId, String objectId, String folderId,
      CmisExtensionType extension) throws CmisException
   {
      try
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Executing operation removeObjectFromFolder");
         multiService.removeObjectFromFolder(repositoryId, objectId, folderId);
      }
      catch (Exception e)
      {
         LOG.error(e.getMessage());
         throw ExceptionFactory.generateException(e);
      }
      return new CmisExtensionType();
   }

}
