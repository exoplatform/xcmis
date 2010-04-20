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
import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.EnumACLPropagation;
import org.xcmis.messaging.CmisACLType;
import org.xcmis.messaging.CmisExtensionType;
import org.xcmis.soap.ACLServicePort;
import org.xcmis.soap.CmisException;
import org.xcmis.spi.CmisStorageInitializer;
import org.xcmis.spi.Connection;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.AccessControlPropagation;

import java.util.List;

/**
 * @author <a href="mailto:max.shaposhnik@exoplatform.com">Max Shaposhnik</a>
 * @version $Id: ACLServicePortImpl.java 2 2010-02-04 17:21:49Z andrew00x $
 */
@javax.jws.WebService(// name = "ACLServicePort",
serviceName = "ACLService", //
portName = "AccessControlServicePort", //
targetNamespace = "http://docs.oasis-open.org/ns/cmis/ws/200908/", //
wsdlLocation = "/wsdl/CMISWS-Service.wsdl" //,
//   endpointInterface = "org.xcmis.soap.ACLServicePort"
)
public class ACLServicePortImpl implements ACLServicePort
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(ACLServicePortImpl.class);

   /** StorageProvider. */
   //   private final StorageProvider storageProvider;

   /**
    * Constructs instance of <code>ACLServicePortImpl</code>.
    *
    * @param storageProvider StorageProvider
    */
   public ACLServicePortImpl(/*StorageProvider storageProvider*/)
   {
      //      this.storageProvider = storageProvider;
   }

   /**
    * {@inheritDoc}
    */
   public CmisACLType applyACL(String repositoryId, String objectId, CmisAccessControlListType addACEs,
      CmisAccessControlListType removeACEs, EnumACLPropagation aclPropagation, CmisExtensionType extension)
      throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation applyACL");
      }
      Connection conn = null;
      try
      {
         //         conn = storageProvider.getConnection(repositoryId);
         conn = CmisStorageInitializer.getInstance().getConnection(repositoryId);
         conn.applyACL(objectId, //
            TypeConverter.getAccessControlEntryList(addACEs.getPermission()), //
            TypeConverter.getAccessControlEntryList(removeACEs.getPermission()), //
            aclPropagation == null ? AccessControlPropagation.REPOSITORYDETERMINED : AccessControlPropagation
               .fromValue(aclPropagation.value()));
         CmisACLType res = new CmisACLType();
         return res;
      }
      catch (Exception e)
      {
         LOG.error("Apply ACL error: " + e.getMessage());
         throw ExceptionFactory.generateException(e);
      }
      finally
      {
         if (conn != null)
         {
            conn.close();
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public CmisACLType getACL(String repositoryId, String objectId, Boolean onlyBasicPermissions,
      CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation getACL");
      }
      Connection conn = null;

      try
      {
         //         conn = storageProvider.getConnection(repositoryId);
         conn = CmisStorageInitializer.getInstance().getConnection(repositoryId);

         List<AccessControlEntry> list =
            conn.getACL(objectId, onlyBasicPermissions == null ? true : onlyBasicPermissions);
         CmisAccessControlListType type = TypeConverter.getCmisAccessControlListType(list);
         CmisACLType res = new CmisACLType();
         res.setACL(type);
         return res;
      }
      catch (Exception e)
      {
         LOG.error("Get ACL error: " + e.getMessage());
         throw ExceptionFactory.generateException(e);
      }
      finally
      {
         if (conn != null)
         {
            conn.close();
         }
      }
   }

}
