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
import org.xcmis.core.NavigationService;
import org.xcmis.messaging.CmisExtensionType;
import org.xcmis.messaging.CmisObjectInFolderContainerType;
import org.xcmis.messaging.CmisObjectInFolderListType;
import org.xcmis.messaging.CmisObjectListType;
import org.xcmis.messaging.CmisObjectParentsType;
import org.xcmis.soap.CmisException;
import org.xcmis.soap.NavigationServicePort;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.object.CmisObjectInFolderContainer;
import org.xcmis.spi.object.CmisObjectParents;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:max.shaposhnik@exoplatform.com">Max Shaposhnik</a>
 * @version $Id$
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

   /** Navigation service. */
   private NavigationService navigationService;

   /**
    * Constructs instance of <code>NavigationServicePortImpl</code> .
    * 
    * @param navigationService NavigationService
    */
   public NavigationServicePortImpl(NavigationService navigationService)
   {
      this.navigationService = navigationService;
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
         LOG.debug("Executing operation getCheckedoutDocs");
      try
      {
         return navigationService.getCheckedOutDocs(repositoryId, //
            folderId, //
            includeAllowableActions == null ? false : includeAllowableActions, //
            includeRelationships == null ? EnumIncludeRelationships.NONE : includeRelationships, //
            propertyFilter, //
            renditionFilter, //
            orderBy, //
            maxItems == null ? CMIS.MAX_ITEMS : maxItems.intValue(), //
            skipCount == null ? 0 : skipCount.intValue(), false).toCmisObjectList();
      }
      catch (Exception e)
      {
         LOG.error("Get checked-out documents error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
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
         LOG.debug("Executing operation getChildren");

      CmisObjectInFolderListType ret = new CmisObjectInFolderListType();
      try
      {
         ret.getObjects().addAll(navigationService.getChildren(repositoryId, //
            folderId, //
            includeAllowableActions == null ? false : includeAllowableActions, //
            includeRelationships == null ? EnumIncludeRelationships.NONE : includeRelationships, //
            includePathSegments == null ? false : includePathSegments, //
            propertyFilter, //
            renditionFilter, //
            orderBy, //
            maxItems == null ? CMIS.MAX_ITEMS : maxItems.intValue(), //
            skipCount == null ? 0 : skipCount.intValue(), false).toCmisObjectInFolderListType().getObjects());

      }
      catch (Exception e)
      {
         LOG.error("Get children error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
      return ret;
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
         LOG.debug("Executing operation getDescendants");
      try
      {
         List<CmisObjectInFolderContainer> descendants = navigationService.getDescendants(repositoryId, //
            folderId, //
            depth == null ? 1 : depth.intValue(), //
            includeAllowableActions == null ? false : includeAllowableActions, //
            includeRelationships == null ? EnumIncludeRelationships.NONE : includeRelationships, //
            includePathSegments == null ? false : includePathSegments, //
            propertyFilter, //
            renditionFilter, false);
         List<CmisObjectInFolderContainerType> result = new ArrayList<CmisObjectInFolderContainerType>();
         for (CmisObjectInFolderContainer objectInFolderCont : descendants)
         {
            result.add(objectInFolderCont.toCmisObjectInFolderContainerType());
         }
         return result;
      }
      catch (Exception e)
      {
         LOG.error("Get descendants error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType getFolderParent(String repositoryId, String folderId, String propertyFilter,
      CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation getFolderParent");
      try
      {
         return navigationService.getFolderParent(repositoryId, folderId, propertyFilter, false).toCmisObjectType();
      }
      catch (Exception e)
      {
         LOG.error("Get folder parent error: " + e.getMessage());
         throw ExceptionFactory.generateException(e);
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
         LOG.debug("Executing operation getFolderTree");
      try
      {
         List<CmisObjectInFolderContainer> tree = navigationService.getFolderTree(repositoryId, //
            folderId, //
            depth == null ? 1 : depth.intValue(), //
            includeAllowableActions == null ? false : includeAllowableActions, //
            includeRelationships == null ? EnumIncludeRelationships.NONE : includeRelationships, //
            includePathSegments == null ? false : includePathSegments, //
            propertyFilter, //
            renditionFilter, false);
         List<CmisObjectInFolderContainerType> result = new ArrayList<CmisObjectInFolderContainerType>();
         for (CmisObjectInFolderContainer objectInFolderCont : tree)
         {
            result.add(objectInFolderCont.toCmisObjectInFolderContainerType());
         }
         return result;

      }
      catch (Exception e)
      {
         LOG.error("Get folder tree error: " + e.getMessage());
         throw ExceptionFactory.generateException(e);
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
         LOG.debug("Executing operation getObjectParents");
      try
      {
         List<CmisObjectParents> parents = navigationService.getObjectParents(repositoryId, //
            objectId, //
            includeAllowableActions == null ? false : includeAllowableActions, //
            includeRelationships == null ? EnumIncludeRelationships.NONE : includeRelationships, //
            includeRelativePathSegment == null ? true : includeRelativePathSegment, //
            propertyFilter, //
            renditionFilter, false);
         List<CmisObjectParentsType> result = new ArrayList<CmisObjectParentsType>();
         for (CmisObjectParents objectInFolderCont : parents)
         {
            result.add(objectInFolderCont.toCmisObjectParentsType());
         }
         return result;

      }
      catch (Exception e)
      {
         LOG.error("Get object parents error: " + e.getMessage());
         throw ExceptionFactory.generateException(e);
      }
   }
}
