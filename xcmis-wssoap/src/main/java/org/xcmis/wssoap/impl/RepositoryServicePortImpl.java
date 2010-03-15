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
import org.xcmis.core.RepositoryService;
import org.xcmis.messaging.CmisExtensionType;
import org.xcmis.messaging.CmisRepositoryEntryType;
import org.xcmis.messaging.CmisTypeContainer;
import org.xcmis.messaging.CmisTypeDefinitionListType;
import org.xcmis.soap.CmisException;
import org.xcmis.soap.RepositoryServicePort;
import org.xcmis.spi.CMIS;

import java.math.BigInteger;
import java.util.List;

/**
 * @author <a href="mailto:max.shaposhnik@exoplatform.com">Max Shaposhnik</a>
 * @version $Id: RepositoryServicePortImpl.java 2 2010-02-04 17:21:49Z andrew00x $
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

   /** Repository service. */   
   private RepositoryService repoService;

   /**
    * Constructs instance of <code>RepositoryServicePortImpl</code> .
    * 
    * @param repoService RepositoryService
    */
   public RepositoryServicePortImpl(RepositoryService repoService)
   {
      this.repoService = repoService;
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisRepositoryEntryType> getRepositories(CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation getRepositories");
      return repoService.getRepositories();
   }

   /**
    * {@inheritDoc}
    */
   public org.xcmis.core.CmisRepositoryInfoType getRepositoryInfo(String repositoryId,
      CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation getRepositoryInfo");
      return repoService.getRepositoryInfo(repositoryId);
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
         LOG.debug("Executing operation getTypeChildren");
      try
      {
         return repoService.getTypeChildren(repositoryId, //
            typeId, //
            includePropertyDefinitions == null ? false : includePropertyDefinitions, //
            maxItems == null ? CMIS.MAX_ITEMS : maxItems.intValue(), //
            skipCount == null ? 0 : skipCount.intValue());
      }
      catch (Exception e)
      {
         LOG.error("Get type children error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }

   }

   /**
    * {@inheritDoc}
    */
   public CmisTypeDefinitionType getTypeDefinition(String repositoryId, String typeId, CmisExtensionType extension)
      throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation getTypeDefinition");
      try
      {
         return repoService.getTypeDefinition(repositoryId, typeId);
      }
      catch (Exception e)
      {
         LOG.error("Get type definition error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisTypeContainer> getTypeDescendants(String repositoryId, String typeId, BigInteger depth,
      Boolean includePropertyDefinitions, CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation getTypeDescendants");
      try
      {
         return repoService.getTypeDescendants(repositoryId, //
            typeId, //
            depth == null ? 1 : depth.intValue(), //
            includePropertyDefinitions == null ? false : includePropertyDefinitions);
      }
      catch (Exception e)
      {
         LOG.error("Get type descendants error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
   }

}
