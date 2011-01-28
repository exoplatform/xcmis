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

import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.messaging.CmisContentStreamType;
import org.xcmis.messaging.CmisExtensionType;
import org.xcmis.soap.CmisException;
import org.xcmis.soap.VersioningServicePort;
import org.xcmis.spi.BaseContentStream;
import org.xcmis.spi.CmisRegistry;
import org.xcmis.spi.Connection;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.IncludeRelationships;
import org.xcmis.spi.utils.Logger;
import org.xcmis.spi.utils.MimeType;

import java.io.FileOutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import java.math.*;

/**
 * @author <a href="mailto:max.shaposhnik@exoplatform.com">Max Shaposhnik</a>
 * @version $Id: VersioningServicePortImpl.java 2 2010-02-04 17:21:49Z andrew00x
 *          $
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
   private static final Logger LOG = Logger.getLogger(VersioningServicePortImpl.class);

   /**
    * Constructs instance of <code>VersioningServicePortImpl</code> .
    *
    */
   public VersioningServicePortImpl()
   {
   }

   /**
    * {@inheritDoc}
    */
   public CmisExtensionType cancelCheckOut(String repositoryId, String documentId, CmisExtensionType extension)
      throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation cancelCheckOut");
      }
      Connection conn = null;
      try
      {
         conn = CmisRegistry.getInstance().getConnection(repositoryId);

         conn.cancelCheckout(documentId);
         return new CmisExtensionType();
      }
      catch (Exception e)
      {
         LOG.error("Cancel checkout error: " + e.getMessage(), e);
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
      {
         LOG.debug("Executing operation checkIn");
      }
      Connection conn = null;
      BaseContentStream cs = null;
      try
      {
         conn = CmisRegistry.getInstance().getConnection(repositoryId);

         if (contentStream != null)
         {
            cs =
               new BaseContentStream(contentStream.getStream().getInputStream(), contentStream.getFilename(), MimeType
                  .fromString(contentStream.getMimeType()));
         }
         String res = conn.checkin(documentId.value, //
            major == null ? true : major, // major as default
            TypeConverter.getPropertyMap(properties), //
            cs, //
            checkinComment, //
            TypeConverter.getListAccessControlEntry(addACEs), //
            TypeConverter.getListAccessControlEntry(removeACEs), //
            policies);
         documentId.value = res;
         CmisExtensionType ext = new CmisExtensionType();
         extension.value = ext;
      }
      catch (Exception e)
      {
         LOG.error("CheckIn error: " + e.getMessage(), e);
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
   public void checkOut(String repositoryId, javax.xml.ws.Holder<String> documentId,
      javax.xml.ws.Holder<CmisExtensionType> extension, javax.xml.ws.Holder<Boolean> contentCopied)
      throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation checkOut");
      }
      Connection conn = null;
      try
      {
         conn = CmisRegistry.getInstance().getConnection(repositoryId);

         String res = conn.checkout(documentId.value);
         documentId.value = res;
         CmisExtensionType ext = new CmisExtensionType();
         extension.value = ext;
         contentCopied.value = true;
      }
      catch (Exception e)
      {
         LOG.error("Checkout error: " + e.getMessage(), e);
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
   public List<CmisObjectType> getAllVersions(String repositoryId, String versionSeriesId, String propertyFilter,
      Boolean includeAllowableActions, CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation getAllVersions");
      }
      Connection conn = null;
      List<CmisObjectType> res = new ArrayList<CmisObjectType>();
      try
      {
         conn = CmisRegistry.getInstance().getConnection(repositoryId);

         List<CmisObject> list = conn.getAllVersions(versionSeriesId, //
            includeAllowableActions == null ? false : includeAllowableActions, //
            false, propertyFilter);
         for (CmisObject one : list)
         {
            res.add(TypeConverter.getCmisObjectType(one));
         }
      }
      catch (Exception e)
      {
         LOG.error("Get all versions error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
      finally
      {
         if (conn != null)
         {
            conn.close();
         }
      }
      return res;
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
      Connection conn = null;
      try
      {
         conn = CmisRegistry.getInstance().getConnection(repositoryId);

         return TypeConverter.getCmisObjectType(conn.getObjectOfLatestVersion(versionSeriesId, //
            major == null ? false : major, //
            includeAllowableActions == null ? false : includeAllowableActions, //
            includeRelationships == null ? IncludeRelationships.NONE : IncludeRelationships
               .fromValue(includeRelationships.value()), //
            includePolicyIds == null ? false : includePolicyIds, //
            includeACL == null ? false : includeACL, //
            false, propertyFilter, //
            renditionFilter));
      }
      catch (Exception e)
      {
         LOG.error("Get object of latest version error: " + e.getMessage(), e);
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
   public CmisPropertiesType getPropertiesOfLatestVersion(String repositoryId, String objectId, Boolean major,
      String filter, CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation getPropertiesOfLatestVersion");
      }
      Connection conn = null;
      try
      {
         conn = CmisRegistry.getInstance().getConnection(repositoryId);

         return TypeConverter.getCmisPropertiesType(conn.getPropertiesOfLatestVersion(objectId, //
            major == null ? false : major, //
            false, filter));
      }
      catch (Exception e)
      {
         LOG.error("Get properties of latest version error: " + e.getMessage(), e);
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
