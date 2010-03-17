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
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.core.VersioningService;
import org.xcmis.messaging.CmisContentStreamType;
import org.xcmis.messaging.CmisExtensionType;
import org.xcmis.soap.CmisException;
import org.xcmis.soap.VersioningServicePort;
import org.xcmis.spi.data.BaseContentStream;
import org.xcmis.spi.utils.CmisUtils;

import java.util.List;

/**
 * @author <a href="mailto:max.shaposhnik@exoplatform.com">Max Shaposhnik</a>
 * @version $Id: VersioningServicePortImpl.java 2 2010-02-04 17:21:49Z andrew00x $
 */
@javax.jws.WebService(// name = "VersioningServicePort",
serviceName = "VersioningService", //
portName = "VersioningServicePort", //
targetNamespace = "http://docs.oasis-open.org/ns/cmis/ws/200908/", //
wsdlLocation = "/wsdl/CMISWS-Service.wsdl" //,
// endpointInterface = "org.xcmis.soap.VersioningServicePort"
)
public class VersioningServicePortImpl implements VersioningServicePort
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(VersioningServicePortImpl.class);

   /** Versioning service. */   
   private VersioningService versioningService;

   /**
    * Constructs instance of <code>VersioningServicePortImpl</code> .
    * 
    * @param versioningService VersioningService
    */
   public VersioningServicePortImpl(VersioningService versioningService)
   {
      this.versioningService = versioningService;
   }

   /**
    * {@inheritDoc}
    */
   public CmisExtensionType cancelCheckOut(String repositoryId, String documentId, CmisExtensionType extension)
      throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation cancelCheckOut");
      try
      {
         versioningService.cancelCheckout(repositoryId, documentId);
         return new CmisExtensionType();
      }
      catch (Exception e)
      {
         LOG.error("Cancel checkout error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void checkIn(String repositoryId, //
      javax.xml.ws.Holder<String> documentId, //
      Boolean major, //
      CmisPropertiesType properties, //
      CmisContentStreamType contentStream, //
      String checkinComment, //
      List<String> policies, //
      CmisAccessControlListType addACEs, //
      CmisAccessControlListType removeACEs, //
      javax.xml.ws.Holder<CmisExtensionType> extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation checkIn");
      BaseContentStream cs = null;
      try
      {
         if (contentStream != null)
            cs =
               new BaseContentStream(contentStream.getStream().getInputStream(), contentStream.getFilename(),
                  contentStream.getMimeType());
         CmisObjectType res = versioningService.checkin(repositoryId, //
            documentId.value, //
            major == null ? true : major, // major as default
            properties, //
            cs, //
            checkinComment, //
            addACEs, //
            removeACEs, //
            policies);
         documentId.value = CmisUtils.getObjectId(res);
         CmisExtensionType ext = new CmisExtensionType();
         ext.getAny().addAll(res.getAny());
         extension.value = ext;
      }
      catch (Exception e)
      {
         LOG.error("CheckIn error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void checkOut(String repositoryId, javax.xml.ws.Holder<String> documentId,
      javax.xml.ws.Holder<CmisExtensionType> extension, javax.xml.ws.Holder<Boolean> contentCopied)
      throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation checkOut");
      try
      {
         CmisObjectType res = versioningService.checkout(repositoryId, documentId.value);
         documentId.value = CmisUtils.getObjectId(res);
         CmisExtensionType ext = new CmisExtensionType();
         ext.getAny().addAll(res.getAny());
         extension.value = ext;
         contentCopied.value = true;
      }
      catch (Exception e)
      {
         LOG.error("Checkout error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisObjectType> getAllVersions(String repositoryId, String versionSeriesId, String propertyFilter,
      Boolean includeAllowableActions, CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation getAllVersions");
      try
      {
         return versioningService.getAllVersions(repositoryId, //
            versionSeriesId, //
            includeAllowableActions == null ? false : includeAllowableActions, //
            propertyFilter);
      }
      catch (Exception e)
      {
         LOG.error("Get all versions error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType getObjectOfLatestVersion(String repositoryId, String versionSeriesId, //
      Boolean major, //
      String propertyFilter, //
      Boolean includeAllowableActions, //
      EnumIncludeRelationships includeRelationships, //
      String renditionFilter, //
      Boolean includePolicyIds, //
      Boolean includeACL, //
      CmisExtensionType extension) throws CmisException
   {
      try
      {
         return versioningService.getObjectOfLatestVersion(repositoryId, //
            versionSeriesId, //
            major == null ? false : major, //
            includeAllowableActions == null ? false : includeAllowableActions, //
            includeRelationships == null ? EnumIncludeRelationships.NONE : includeRelationships, //
            includePolicyIds == null ? false : includePolicyIds, //
            includeACL == null ? false : includeACL, //
            propertyFilter, //
            renditionFilter);
      }
      catch (Exception e)
      {
         LOG.error("Get object of latest version error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public CmisPropertiesType getPropertiesOfLatestVersion(String repositoryId, String objectId, Boolean major,
      String filter, CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation getPropertiesOfLatestVersion");
      try
      {
         return versioningService.getPropertiesOfLatestVersion(repositoryId, //
            objectId, //
            major == null ? false : major, //
            filter);
      }
      catch (Exception e)
      {
         LOG.error("Get properties of latest version error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
   }

}
