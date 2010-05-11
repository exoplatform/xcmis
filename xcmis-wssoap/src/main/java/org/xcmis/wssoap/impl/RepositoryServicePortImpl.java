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
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.messaging.CmisExtensionType;
import org.xcmis.messaging.CmisRepositoryEntryType;
import org.xcmis.messaging.CmisTypeContainer;
import org.xcmis.messaging.CmisTypeDefinitionListType;
import org.xcmis.soap.CmisException;
import org.xcmis.soap.RepositoryServicePort;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.CmisRegistry;
import org.xcmis.spi.Connection;
import org.xcmis.spi.StorageInfo;
import org.xcmis.spi.model.RepositoryInfo;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:max.shaposhnik@exoplatform.com">Max Shaposhnik</a>
 * @version $Id: RepositoryServicePortImpl.java 2 2010-02-04 17:21:49Z andrew00x
 *          $
 */
@javax.jws.WebService(// name = "RepositoryServicePort",
serviceName = "RepositoryService", //
portName = "RepositoryServicePort", //
targetNamespace = "http://docs.oasis-open.org/ns/cmis/ws/200908/", //
wsdlLocation = "/wsdl/CMISWS-Service.wsdl" //,
// endpointInterface = "org.xcmis.soap.RepositoryServicePort"
)
public class RepositoryServicePortImpl implements RepositoryServicePort
{
   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(RepositoryServicePortImpl.class);

   /**
    * Constructs instance of <code>RepositoryServicePortImpl</code> .
    *
    */
   public RepositoryServicePortImpl()
   {
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisRepositoryEntryType> getRepositories(CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation getRepositories");
      }
      Set<StorageInfo> entries = CmisRegistry.getInstance().getStorageInfos();

      Connection conn = null;

      try
      {
         List<CmisRepositoryEntryType> res = new ArrayList<CmisRepositoryEntryType>();
         for (StorageInfo storageInfo : entries)
         {
            conn = CmisRegistry.getInstance().getConnection(storageInfo.getStorageId());

            RepositoryInfo repoInfo = conn.getStorage().getRepositoryInfo();
            CmisRepositoryEntryType type = new CmisRepositoryEntryType();
            type.setRepositoryId(repoInfo.getRepositoryId());
            type.setRepositoryName(repoInfo.getRepositoryName());
            res.add(type);
         }
         return res;
      }
      catch (Exception e)
      {
         LOG.error("Get type children error: " + e.getMessage(), e);
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
   public org.xcmis.core.CmisRepositoryInfoType getRepositoryInfo(String repositoryId, CmisExtensionType extension)
      throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation getRepositoryInfo");
      }
      Connection conn = null;
      conn = CmisRegistry.getInstance().getConnection(repositoryId);

      return TypeConverter.getCmisRepositoryInfoType(conn.getStorage().getRepositoryInfo());
   }

   /**
    * {@inheritDoc}
    */
   public CmisTypeDefinitionListType getTypeChildren(String repositoryId, //
      String typeId, //
      Boolean includePropertyDefinitions, //
      BigInteger maxItems, //
      BigInteger skipCount, //
      CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation getTypeChildren");
      }
      Connection conn = null;
      try
      {
         conn = CmisRegistry.getInstance().getConnection(repositoryId);

         return TypeConverter.getCmisTypeDefinitionListType(conn.getTypeChildren(typeId, //
            includePropertyDefinitions == null ? false : includePropertyDefinitions, //
            maxItems == null ? CmisConstants.MAX_ITEMS : maxItems.intValue(), //
            skipCount == null ? 0 : skipCount.intValue()));
      }
      catch (Exception e)
      {
         LOG.error("Get type children error: " + e.getMessage(), e);
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
   public CmisTypeDefinitionType getTypeDefinition(String repositoryId, String typeId, CmisExtensionType extension)
      throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation getTypeDefinition");
      }
      Connection conn = null;
      try
      {
         conn = CmisRegistry.getInstance().getConnection(repositoryId);

         return TypeConverter.getCmisTypeDefinitionType(conn.getTypeDefinition(typeId));
      }
      catch (Exception e)
      {
         LOG.error("Get type definition error: " + e.getMessage(), e);
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
   public List<CmisTypeContainer> getTypeDescendants(String repositoryId, String typeId, BigInteger depth,
      Boolean includePropertyDefinitions, CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation getTypeDescendants");
      }
      Connection conn = null;
      try
      {
         conn = CmisRegistry.getInstance().getConnection(repositoryId);

         return TypeConverter.getCmisTypeContainerList(conn.getTypeDescendants(typeId, //
            depth == null ? 1 : depth.intValue(), //
            includePropertyDefinitions == null ? false : includePropertyDefinitions));
      }
      catch (Exception e)
      {
         LOG.error("Get type descendants error: " + e.getMessage(), e);
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
