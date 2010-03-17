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
import org.xcmis.core.CmisAllowableActionsType;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisPropertyString;
import org.xcmis.core.CmisRenditionType;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.core.EnumUnfileObject;
import org.xcmis.core.EnumVersioningState;
import org.xcmis.core.ObjectService;
import org.xcmis.messaging.CmisContentStreamType;
import org.xcmis.messaging.CmisExtensionType;
import org.xcmis.messaging.DeleteTreeResponse;
import org.xcmis.soap.CmisException;
import org.xcmis.soap.ObjectServicePort;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.data.BaseContentStream;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.utils.CmisUtils;

import java.math.BigInteger;
import java.util.List;

import javax.activation.DataHandler;

/**
 * @author <a href="mailto:max.shaposhnik@exoplatform.com">Max Shaposhnik</a>
 * @version $Id: ObjectServicePortImpl.java 2 2010-02-04 17:21:49Z andrew00x $
 */
@javax.jws.WebService(// name = "ObjectServicePort",
serviceName = "ObjectService", // 
portName = "ObjectServicePort", //
targetNamespace = "http://docs.oasis-open.org/ns/cmis/ws/200908/", //
wsdlLocation = "/wsdl/CMISWS-Service.wsdl" //,
//      endpointInterface = "org.xcmis.soap.ObjectServicePort"
)
public class ObjectServicePortImpl implements ObjectServicePort
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(ObjectServicePortImpl.class);

   /** Object service. */
   private ObjectService objectService;

   /**
    * Constructs instance of <code>ObjectServicePortImpl</code> .
    * 
    * @param objectService ObjectService
    */
   public ObjectServicePortImpl(ObjectService objectService)
   {
      this.objectService = objectService;
   }

   /**
    * {@inheritDoc}
    */
   public void createDocument(String repositoryId, //
      CmisPropertiesType properties, //
      String folderId, //
      CmisContentStreamType contentStream, //
      EnumVersioningState versioningState, //
      List<String> policies, //
      CmisAccessControlListType addACEs, //
      CmisAccessControlListType removeACEs, //
      javax.xml.ws.Holder<CmisExtensionType> extension, //
      javax.xml.ws.Holder<String> objectId) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation createDocument");
      ContentStream cs = null;
      try
      {
         if (contentStream != null)
            cs =
               new BaseContentStream(contentStream.getStream().getInputStream(), contentStream.getFilename(),
                  contentStream.getMimeType());
         objectId.value = CmisUtils.getObjectId(objectService.createDocument(repositoryId, //
            folderId, //
            properties, //
            cs, //
            versioningState == null ? EnumVersioningState.MAJOR : versioningState, //
            addACEs, //
            removeACEs, //
            policies));
      }
      catch (Exception e)
      {
         LOG.error("Create document error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void createDocumentFromSource(String repositoryId, //
      String sourceId, //
      CmisPropertiesType properties, //
      String folderId, //
      EnumVersioningState versioningState, //
      List<String> policies, //
      CmisAccessControlListType addACEs, //
      CmisAccessControlListType removeACEs, //
      javax.xml.ws.Holder<CmisExtensionType> extension, //
      javax.xml.ws.Holder<String> objectId) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation createDocumentFromSource");
      try
      {
         objectId.value = CmisUtils.getObjectId(objectService.createDocumentFromSource(repositoryId, //
            sourceId, //
            folderId, //
            properties, //
            versioningState == null ? EnumVersioningState.MAJOR : versioningState, //
            addACEs, //
            removeACEs, //
            policies));
      }
      catch (Exception e)
      {
         LOG.error("Create document from source error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }

   }

   /**
    * {@inheritDoc}
    */
   public void createFolder(String repositoryId, //
      CmisPropertiesType properties, //
      String folderId, //
      List<String> policies, //
      CmisAccessControlListType addACEs, //
      CmisAccessControlListType removeACEs, //
      javax.xml.ws.Holder<CmisExtensionType> extension, //
      javax.xml.ws.Holder<String> objectId) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation createFolder");
      try
      {
         objectId.value =
            CmisUtils.getObjectId(objectService.createFolder(repositoryId, folderId, properties, addACEs, removeACEs,
               policies));
      }
      catch (Exception e)
      {
         LOG.error("Create folder error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void createPolicy(String repositoryId, //
      CmisPropertiesType properties, //
      String folderId, //
      List<String> policies, //
      CmisAccessControlListType addACEs, //
      CmisAccessControlListType removeACEs, //
      javax.xml.ws.Holder<CmisExtensionType> extension, //
      javax.xml.ws.Holder<String> objectId) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation createPolicy");
      try
      {
         objectId.value =
            CmisUtils.getObjectId(objectService.createPolicy(repositoryId, folderId, properties, addACEs, removeACEs,
               policies));
      }
      catch (Exception e)
      {
         LOG.error("Create policy error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void createRelationship(String repositoryId, //
      CmisPropertiesType properties, //
      List<String> policies, //
      CmisAccessControlListType addACEs, //
      CmisAccessControlListType removeACEs, //
      javax.xml.ws.Holder<CmisExtensionType> extension, //
      javax.xml.ws.Holder<String> objectId) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation createRelationship");
      try
      {
         objectId.value =
            CmisUtils.getObjectId(objectService.createRelationship(repositoryId, properties, addACEs, removeACEs,
               policies));
      }
      catch (Exception e)
      {
         LOG.error("Create relationship error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void deleteContentStream(String repositoryId, javax.xml.ws.Holder<String> documentId,
      javax.xml.ws.Holder<String> changeToken, javax.xml.ws.Holder<CmisExtensionType> extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation deleteContentStream");
      try
      {
         CmisObjectType document = objectService.deleteContentStream(repositoryId, //
            documentId.value, //
            changeToken != null ? changeToken.value : null);

         documentId.value = CmisUtils.getObjectId(document);
         CmisPropertyString token = (CmisPropertyString)CmisUtils.getProperty(document, CMIS.CHANGE_TOKEN);
         if (token != null && token.getValue().size() > 0)
            changeToken.value = token.getValue().get(0);
      }
      catch (Exception e)
      {
         LOG.error("Delete document's content error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public CmisExtensionType deleteObject(String repositoryId, String objectId, Boolean allVersions,
      CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation deleteObject");
      try
      {
         objectService.deleteObject(repositoryId, objectId, allVersions == null ? true : allVersions);
      }
      catch (Exception e)
      {
         LOG.error("Delete object error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
      return new CmisExtensionType();
   }

   /**
    * {@inheritDoc}
    */
   public DeleteTreeResponse.FailedToDelete deleteTree(String repositoryId, //
      String folderId, //
      Boolean allVersions, //
      EnumUnfileObject unfileObject, //
      Boolean continueOnFailure, //
      CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation deleteTree");
      try
      {
         DeleteTreeResponse.FailedToDelete failed = new DeleteTreeResponse.FailedToDelete();
         failed.getObjectIds().addAll(objectService.deleteTree(repositoryId, //
            folderId, //
            unfileObject == null ? EnumUnfileObject.DELETE : unfileObject, //
            continueOnFailure == null ? false : continueOnFailure));
         return failed;
      }
      catch (Exception e)
      {
         LOG.error("Delete folder tree error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public CmisAllowableActionsType getAllowableActions(String repositoryId, String objectId, 
      CmisExtensionType extension)
      throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation getAllowableActions");
      try
      {
         return objectService.getAllowableActions(repositoryId, objectId);
      }
      catch (Exception e)
      {
         LOG.error("Get allowable actions error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public CmisContentStreamType getContentStream(String repositoryId, //
      String objectId, //
      String streamId, //
      java.math.BigInteger offset, //
      java.math.BigInteger length, //
      CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation getContentStream");
      try
      {
         CmisContentStreamType stream = new CmisContentStreamType();
         ContentStream cs = objectService.getContentStream(repositoryId, //
            objectId, //
            streamId, //
            offset != null ? offset.longValue() : 0, //
            length != null ? length.longValue() : -1);

         stream.setFilename(cs.getFileName());
         stream.setMimeType(cs.getMediaType());
         if (cs.length() != -1)
            stream.setLength(BigInteger.valueOf(cs.length()));
         stream.setStream(new DataHandler(cs.getStream(), cs.getMediaType()));
         return stream;
      }
      catch (Exception e)
      {
         LOG.error("Get content stream error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType getObject(String repositoryId, //
      String objectId, //
      String propertyFilter, //
      Boolean includeAllowableActions, //
      EnumIncludeRelationships includeRelationships, //
      String renditionFilter, //
      Boolean includePolicyIds, //
      Boolean includeACL, //
      CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation getObject");
      try
      {
         return objectService.getObject(repositoryId, //
            objectId, //
            includeAllowableActions == null ? false : includeAllowableActions, //
            includeRelationships == null ? EnumIncludeRelationships.NONE : includeRelationships, //
            includePolicyIds == null ? false : includePolicyIds, //
            includeACL == null ? false : includeACL, //
            propertyFilter, //
            renditionFilter);
      }
      catch (Exception e)
      {
         LOG.error("Get object error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }

   }

   /**
    * {@inheritDoc}
    */
   public CmisObjectType getObjectByPath(String repositoryId, //
      String path, //
      String propertyFilter, //
      Boolean includeAllowableActions, //
      EnumIncludeRelationships includeRelationships, //
      String renditionFilter, //
      Boolean includePolicyIds, //
      Boolean includeACL, //
      CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation getObjectByPath");
      try
      {
         return objectService.getObjectByPath(repositoryId, //
            path, //
            includeAllowableActions == null ? false : includeAllowableActions, //
            includeRelationships == null ? EnumIncludeRelationships.NONE : includeRelationships, //
            includePolicyIds == null ? false : includePolicyIds, //
            includeACL == null ? false : includeACL, //
            propertyFilter, //
            renditionFilter);
      }
      catch (Exception e)
      {
         LOG.error("Get object by path error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }

   }

   /**
    * {@inheritDoc}
    */
   public CmisPropertiesType getProperties(String repositoryId, String objectId, String propertyFilter,
      CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation getProperties");
      try
      {
         return objectService.getProperties(repositoryId, objectId, propertyFilter);
      }
      catch (Exception e)
      {
         LOG.error("Get properties error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisRenditionType> getRenditions(String repositoryId, String objectId, String renditionFilter,
      BigInteger maxItems, BigInteger skipCount, CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation getRenditions");
      try
      {
         return objectService.getRenditions(repositoryId, //
            objectId, //
            renditionFilter, //
            maxItems == null ? CMIS.MAX_ITEMS : maxItems.intValue(), //
            skipCount == null ? 0 : skipCount.intValue());
      }
      catch (Exception e)
      {
         LOG.error("Get renditions error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void moveObject(String repositoryId, javax.xml.ws.Holder<String> objectId, String targetFolderId,
      String sourceFolderId, javax.xml.ws.Holder<CmisExtensionType> extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation moveObject");
      try
      {
         objectId.value =
            CmisUtils.getObjectId(objectService
               .moveObject(repositoryId, objectId.value, targetFolderId, sourceFolderId));
      }
      catch (Exception e)
      {
         LOG.error("Move object error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void setContentStream(String repositoryId, //
      javax.xml.ws.Holder<String> documentId, //
      Boolean overwriteFlag, //
      javax.xml.ws.Holder<String> changeToken, //
      CmisContentStreamType contentStream, //
      javax.xml.ws.Holder<CmisExtensionType> extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation setContentStream");
      try
      {
         ContentStream cs = null;
         if (contentStream != null)
            cs = new BaseContentStream(contentStream.getStream().getInputStream(), //
               contentStream.getFilename(), //
               contentStream.getMimeType());

         CmisObjectType document = objectService.setContentStream(repositoryId, //
            documentId.value, //
            cs, //
            changeToken == null ? null : changeToken.value, //
            overwriteFlag == null ? true : overwriteFlag);

         documentId.value = CmisUtils.getObjectId(document);
         CmisPropertyString token = (CmisPropertyString)CmisUtils.getProperty(document, CMIS.CHANGE_TOKEN);
         if (token != null && token.getValue().size() > 0)
            changeToken.value = token.getValue().get(0);
      }
      catch (Exception e)
      {
         LOG.error("Set content stream error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void updateProperties(String repositoryId, //
      javax.xml.ws.Holder<String> objectId, //
      javax.xml.ws.Holder<String> changeToken, //
      CmisPropertiesType properties, //
      javax.xml.ws.Holder<CmisExtensionType> extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Executing operation updateProperties");
      try
      {
         CmisObjectType object = objectService.updateProperties(repositoryId, //
            objectId.value, //
            changeToken.value == null ? null : changeToken.value, //
            properties);
         objectId.value = CmisUtils.getObjectId(object);
         CmisPropertyString token = (CmisPropertyString)CmisUtils.getProperty(object, CMIS.CHANGE_TOKEN);
         if (token != null && token.getValue().size() > 0)
            changeToken.value = token.getValue().get(0);
      }
      catch (Exception e)
      {
         LOG.error("Update properties error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
   }

}
