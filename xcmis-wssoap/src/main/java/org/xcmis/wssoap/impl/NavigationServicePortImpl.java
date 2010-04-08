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
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.messaging.CmisExtensionType;
import org.xcmis.messaging.CmisObjectInFolderContainerType;
import org.xcmis.messaging.CmisObjectInFolderListType;
import org.xcmis.messaging.CmisObjectListType;
import org.xcmis.messaging.CmisObjectParentsType;
import org.xcmis.soap.CmisException;
import org.xcmis.soap.NavigationServicePort;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.Connection;
import org.xcmis.spi.StorageProvider;
import org.xcmis.spi.model.IncludeRelationships;
import org.xcmis.spi.model.ObjectParent;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:max.shaposhnik@exoplatform.com">Max Shaposhnik</a>
 * @version $Id: NavigationServicePortImpl.java 2 2010-02-04 17:21:49Z andrew00x $
 */
@javax.jws.WebService(// name = "NavigationServicePort",
serviceName = "NavigationService", //
portName = "NavigationServicePort", //
targetNamespace = "http://docs.oasis-open.org/ns/cmis/ws/200908/", //
wsdlLocation = "/wsdl/CMISWS-Service.wsdl" //,
//      endpointInterface = "org.xcmis.soap.NavigationServicePort"
)
public class NavigationServicePortImpl implements NavigationServicePort
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(NavigationServicePortImpl.class);

   /** StorageProvider. */
   private final StorageProvider storageProvider;

   /**
    * Constructs instance of <code>NavigationServicePortImpl</code> .
    *
    * @param storageProvider StorageProvider
    */
   public NavigationServicePortImpl(StorageProvider storageProvider)
   {
      this.storageProvider = storageProvider;
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectListType getCheckedOutDocs(String repositoryId, //
      String folderId, //
      String propertyFilter, //
      String orderBy, //
      Boolean includeAllowableActions, //
      EnumIncludeRelationships includeRelationships, //
      String renditionFilter, //
      BigInteger maxItems, //
      BigInteger skipCount, //
      CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation getCheckedoutDocs");
      }
      Connection conn = null;
      try
      {
         conn = storageProvider.getConnection(repositoryId);
         return TypeConverter.getCmisObjectListType(conn.getCheckedOutDocs(folderId, //
            includeAllowableActions == null ? false : includeAllowableActions, //
            includeRelationships == null ? IncludeRelationships.NONE : IncludeRelationships
               .fromValue(includeRelationships.value()), //
            true, propertyFilter, //
            renditionFilter, //
            orderBy, //
            maxItems == null ? CMIS.MAX_ITEMS : maxItems.intValue(), //
            skipCount == null ? 0 : skipCount.intValue()));
      }
      catch (Exception e)
      {
         LOG.error("Get checked-out documents error: " + e.getMessage(), e);
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
   public CmisObjectInFolderListType getChildren(String repositoryId, //
      String folderId, //
      String propertyFilter, //
      String orderBy, //
      Boolean includeAllowableActions, //
      EnumIncludeRelationships includeRelationships, //
      String renditionFilter, //
      Boolean includePathSegments, //
      BigInteger maxItems, //
      BigInteger skipCount, //
      CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation getChildren");
      }
      Connection conn = null;
      try
      {
         conn = storageProvider.getConnection(repositoryId);
         return TypeConverter.getCmisObjectInFolderListType(conn.getChildren(folderId, //
            includeAllowableActions == null ? false : includeAllowableActions, //
            includeRelationships == null ? IncludeRelationships.NONE : IncludeRelationships
               .fromValue(includeRelationships.value()), //
            includePathSegments == null ? false : includePathSegments, //
            true, propertyFilter, //
            renditionFilter, //
            orderBy, //
            maxItems == null ? CMIS.MAX_ITEMS : maxItems.intValue(), //
            skipCount == null ? 0 : skipCount.intValue()));

      }
      catch (Exception e)
      {
         LOG.error("Get children error: " + e.getMessage(), e);
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
   public List<CmisObjectInFolderContainerType> getDescendants(String repositoryId, //
      String folderId, //
      BigInteger depth, //
      String propertyFilter, //
      Boolean includeAllowableActions, //
      EnumIncludeRelationships includeRelationships, //
      String renditionFilter, //
      Boolean includePathSegments, //
      CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation getDescendants");
      }
      Connection conn = null;
      try
      {
         conn = storageProvider.getConnection(repositoryId);
         return TypeConverter.getCmisObjectInFolderContainerType(conn.getDescendants(folderId, //
            depth == null ? 1 : depth.intValue(), //
            includeAllowableActions == null ? false : includeAllowableActions, //
            includeRelationships == null ? IncludeRelationships.NONE : IncludeRelationships
               .fromValue(includeRelationships.value()), //
            includePathSegments == null ? false : includePathSegments, //
            true, propertyFilter, //
            renditionFilter));
      }
      catch (Exception e)
      {
         LOG.error("Get descendants error: " + e.getMessage(), e);
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
   public CmisObjectType getFolderParent(String repositoryId, String folderId, String propertyFilter,
      CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation getFolderParent");
      }
      Connection conn = null;
      try
      {
         conn = storageProvider.getConnection(repositoryId);
         return TypeConverter.getCmisObjectType(conn.getFolderParent(folderId, true, propertyFilter));
      }
      catch (Exception e)
      {
         LOG.error("Get folder parent error: " + e.getMessage());
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
   public List<CmisObjectInFolderContainerType> getFolderTree(String repositoryId, //
      String folderId, //
      BigInteger depth, //
      String propertyFilter, //
      Boolean includeAllowableActions, //
      EnumIncludeRelationships includeRelationships, //
      String renditionFilter, //
      Boolean includePathSegments, //
      CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation getFolderTree");
      }
      Connection conn = null;
      try
      {
         conn = storageProvider.getConnection(repositoryId);
         return TypeConverter.getCmisObjectInFolderContainerType(conn.getFolderTree(folderId, //
            depth == null ? 1 : depth.intValue(), //
            includeAllowableActions == null ? false : includeAllowableActions, //
            includeRelationships == null ? IncludeRelationships.NONE : IncludeRelationships
               .fromValue(includeRelationships.value()), //
            includePathSegments == null ? false : includePathSegments, //
            true, propertyFilter, //
            renditionFilter));
      }
      catch (Exception e)
      {
         LOG.error("Get folder tree error: " + e.getMessage());
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
   public List<CmisObjectParentsType> getObjectParents(String repositoryId, //
      String objectId, //
      String propertyFilter, //
      Boolean includeAllowableActions, //
      EnumIncludeRelationships includeRelationships, //
      String renditionFilter, //
      Boolean includeRelativePathSegment, //
      CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation getObjectParents");
      }
      Connection conn = null;
      try
      {
         conn = storageProvider.getConnection(repositoryId);
         List<CmisObjectParentsType> res = new ArrayList<CmisObjectParentsType>();
         List<ObjectParent> out =
            conn.getObjectParents(objectId, //
               includeAllowableActions == null ? false : includeAllowableActions, //
               includeRelationships == null ? IncludeRelationships.NONE : IncludeRelationships
                  .fromValue(includeRelationships.value()), //
               includeRelativePathSegment == null ? true : includeRelativePathSegment, //
               true, propertyFilter, //
               renditionFilter);

         for (ObjectParent one : out)
         {
            res.add(TypeConverter.getCmisObjectParentsType(one));
         }
         return res;
      }
      catch (Exception e)
      {
         LOG.error("Get object parents error: " + e.getMessage());
         throw ExceptionFactory.generateException(e);
      }
      finally
      {
         conn.close();
      }
   }
}
