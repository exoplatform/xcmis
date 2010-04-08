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
import org.xcmis.core.CmisRenditionType;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.core.EnumUnfileObject;
import org.xcmis.core.EnumVersioningState;
import org.xcmis.messaging.CmisContentStreamType;
import org.xcmis.messaging.CmisExtensionType;
import org.xcmis.messaging.DeleteTreeResponse;
import org.xcmis.soap.CmisException;
import org.xcmis.soap.ObjectServicePort;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ChangeTokenHolder;
import org.xcmis.spi.Connection;
import org.xcmis.spi.StorageProvider;
import org.xcmis.spi.data.BaseContentStream;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.model.IncludeRelationships;
import org.xcmis.spi.model.UnfileObject;
import org.xcmis.spi.model.VersioningState;

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

   /** StorageProvider. */
   private final StorageProvider storageProvider;

   /**
    * Constructs instance of <code>ObjectServicePortImpl</code> .
    *
    * @param objectService ObjectService
    */
   public ObjectServicePortImpl(StorageProvider storageProvider)
   {
      this.storageProvider = storageProvider;
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
      {
         LOG.debug("Executing operation createDocument");
      }
      ContentStream cs = null;
      Connection conn = null;
      try
      {
         conn = storageProvider.getConnection(repositoryId);
         if (contentStream != null)
         {
            cs =
               new BaseContentStream(contentStream.getStream().getInputStream(), contentStream.getFilename(),
                  contentStream.getMimeType());
         }
         objectId.value =
            conn.createDocument(folderId, //
               TypeConverter.getPropertyMap(properties), //
               cs, //
               TypeConverter.getCmisListAccessControlEntry(addACEs), //
               TypeConverter.getCmisListAccessControlEntry(removeACEs), //
               policies, versioningState == null ? VersioningState.MAJOR : VersioningState.fromValue(versioningState
                  .value()) //
               );
      }
      catch (Exception e)
      {
         LOG.error("Create document error: " + e.getMessage(), e);
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
      {
         LOG.debug("Executing operation createDocumentFromSource");
      }
      Connection conn = null;
      try
      {
         conn = storageProvider.getConnection(repositoryId);
         objectId.value =
            conn.createDocumentFromSource(sourceId, //
               folderId, //
               TypeConverter.getPropertyMap(properties), //
               TypeConverter.getCmisListAccessControlEntry(addACEs), //
               TypeConverter.getCmisListAccessControlEntry(removeACEs), //
               policies, versioningState == null ? VersioningState.MAJOR : VersioningState.fromValue(versioningState
                  .value()) //
               );
      }
      catch (Exception e)
      {
         LOG.error("Create document from source error: " + e.getMessage(), e);
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
      {
         LOG.debug("Executing operation createFolder");
      }
      Connection conn = null;
      try
      {
         conn = storageProvider.getConnection(repositoryId);
         objectId.value =
            conn.createFolder(folderId, TypeConverter.getPropertyMap(properties), TypeConverter
               .getCmisListAccessControlEntry(addACEs), //
               TypeConverter.getCmisListAccessControlEntry(removeACEs), //
               policies);
      }
      catch (Exception e)
      {
         LOG.error("Create folder error: " + e.getMessage(), e);
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
      {
         LOG.debug("Executing operation createPolicy");
      }
      Connection conn = null;
      try
      {
         conn = storageProvider.getConnection(repositoryId);
         objectId.value =
            conn.createPolicy(folderId, TypeConverter.getPropertyMap(properties), TypeConverter
               .getCmisListAccessControlEntry(addACEs), //
               TypeConverter.getCmisListAccessControlEntry(removeACEs), policies);
      }
      catch (Exception e)
      {
         LOG.error("Create policy error: " + e.getMessage(), e);
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
   public void createRelationship(String repositoryId, //
      CmisPropertiesType properties, //
      List<String> policies, //
      CmisAccessControlListType addACEs, //
      CmisAccessControlListType removeACEs, //
      javax.xml.ws.Holder<CmisExtensionType> extension, //
      javax.xml.ws.Holder<String> objectId) throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation createRelationship");
      }
      Connection conn = null;
      try
      {
         conn = storageProvider.getConnection(repositoryId);
         objectId.value =
            conn.createRelationship(TypeConverter.getPropertyMap(properties), TypeConverter
               .getCmisListAccessControlEntry(addACEs), //
               TypeConverter.getCmisListAccessControlEntry(removeACEs), policies);
      }
      catch (Exception e)
      {
         LOG.error("Create relationship error: " + e.getMessage(), e);
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
   public void deleteContentStream(String repositoryId, javax.xml.ws.Holder<String> documentId,
      javax.xml.ws.Holder<String> changeToken, javax.xml.ws.Holder<CmisExtensionType> extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation deleteContentStream");
      }
      Connection conn = null;
      try
      {
         conn = storageProvider.getConnection(repositoryId);
         ChangeTokenHolder hold = new ChangeTokenHolder();
         if (changeToken != null)
         {
            hold.setValue(changeToken.value);
         }
         documentId.value = conn.deleteContentStream(documentId.value, //
            changeToken != null ? hold : null);
      }
      catch (Exception e)
      {
         LOG.error("Delete document's content error: " + e.getMessage(), e);
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
   public CmisExtensionType deleteObject(String repositoryId, String objectId, Boolean allVersions,
      CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation deleteObject");
      }
      Connection conn = null;
      try
      {
         conn = storageProvider.getConnection(repositoryId);
         conn.deleteObject(objectId, allVersions);
      }
      catch (Exception e)
      {
         LOG.error("Delete object error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
      finally
      {
         conn.close();
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
      {
         LOG.debug("Executing operation deleteTree");
      }
      Connection conn = null;
      try
      {
         conn = storageProvider.getConnection(repositoryId);
         DeleteTreeResponse.FailedToDelete failed = new DeleteTreeResponse.FailedToDelete();
         failed.getObjectIds().addAll(conn.deleteTree(folderId, //
            allVersions, //
            unfileObject == null ? UnfileObject.DELETE : UnfileObject.fromValue(unfileObject.value()), //
            continueOnFailure == null ? false : continueOnFailure));
         return failed;
      }
      catch (Exception e)
      {
         LOG.error("Delete folder tree error: " + e.getMessage(), e);
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
   public CmisAllowableActionsType getAllowableActions(String repositoryId, String objectId, CmisExtensionType extension)
      throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation getAllowableActions");
      }
      Connection conn = null;
      try
      {
         conn = storageProvider.getConnection(repositoryId);
         return TypeConverter.getAllowableActionsType(conn.getAllowableActions(objectId));
      }
      catch (Exception e)
      {
         LOG.error("Get allowable actions error: " + e.getMessage(), e);
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
   public CmisContentStreamType getContentStream(String repositoryId, //
      String objectId, //
      String streamId, //
      java.math.BigInteger offset, //
      java.math.BigInteger length, //
      CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation getContentStream");
      }
      Connection conn = null;
      try
      {
         conn = storageProvider.getConnection(repositoryId);
         CmisContentStreamType stream = new CmisContentStreamType();
         ContentStream cs = conn.getContentStream(objectId, //
            streamId, //
            offset != null ? offset.longValue() : 0, //
            length != null ? length.longValue() : -1);

         stream.setFilename(cs.getFileName());
         stream.setMimeType(cs.getMediaType());
         if (cs.length() != -1)
         {
            stream.setLength(BigInteger.valueOf(cs.length()));
         }
         stream.setStream(new DataHandler(cs.getStream(), cs.getMediaType()));
         return stream;
      }
      catch (Exception e)
      {
         LOG.error("Get content stream error: " + e.getMessage(), e);
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
      {
         LOG.debug("Executing operation getObject");
      }
      Connection conn = null;
      try
      {
         conn = storageProvider.getConnection(repositoryId);
         return TypeConverter.getCmisObjectType(conn.getObject(objectId, //
            includeAllowableActions == null ? false : includeAllowableActions, //
            includeRelationships == null ? IncludeRelationships.NONE : IncludeRelationships
               .fromValue(includeRelationships.value()), //
            includePolicyIds == null ? false : includePolicyIds, //
            includeACL == null ? false : includeACL, //
            true, propertyFilter, //
            renditionFilter));
      }
      catch (Exception e)
      {
         LOG.error("Get object error: " + e.getMessage(), e);
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
      {
         LOG.debug("Executing operation getObjectByPath");
      }
      Connection conn = null;
      try
      {
         conn = storageProvider.getConnection(repositoryId);
         return TypeConverter.getCmisObjectType(conn.getObjectByPath(path, //
            includeAllowableActions == null ? false : includeAllowableActions, //
            includeRelationships == null ? IncludeRelationships.NONE : IncludeRelationships
               .fromValue(includeRelationships.value()), //
            includePolicyIds == null ? false : includePolicyIds, //
            includeACL == null ? false : includeACL, //
            true, propertyFilter, //
            renditionFilter));
      }
      catch (Exception e)
      {
         LOG.error("Get object by path error: " + e.getMessage(), e);
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
   public CmisPropertiesType getProperties(String repositoryId, String objectId, String propertyFilter,
      CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation getProperties");
      }
      Connection conn = null;
      try
      {
         conn = storageProvider.getConnection(repositoryId);
         return TypeConverter.getCmisPropertiesType(conn.getProperties(objectId, true, propertyFilter));
      }
      catch (Exception e)
      {
         LOG.error("Get properties error: " + e.getMessage(), e);
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
   public List<CmisRenditionType> getRenditions(String repositoryId, String objectId, String renditionFilter,
      BigInteger maxItems, BigInteger skipCount, CmisExtensionType extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation getRenditions");
      }
      Connection conn = null;
      try
      {
         conn = storageProvider.getConnection(repositoryId);
         return TypeConverter.getRenditionList(conn.getRenditions(objectId, //
            renditionFilter, //
            maxItems == null ? CMIS.MAX_ITEMS : maxItems.intValue(), //
            skipCount == null ? 0 : skipCount.intValue()));
      }
      catch (Exception e)
      {
         LOG.error("Get renditions error: " + e.getMessage(), e);
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
   public void moveObject(String repositoryId, javax.xml.ws.Holder<String> objectId, String targetFolderId,
      String sourceFolderId, javax.xml.ws.Holder<CmisExtensionType> extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation moveObject");
      }
      Connection conn = null;
      try
      {
         conn = storageProvider.getConnection(repositoryId);
         objectId.value = conn.moveObject(objectId.value, targetFolderId, sourceFolderId);
      }
      catch (Exception e)
      {
         LOG.error("Move object error: " + e.getMessage(), e);
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
   public void setContentStream(String repositoryId, //
      javax.xml.ws.Holder<String> documentId, //
      Boolean overwriteFlag, //
      javax.xml.ws.Holder<String> changeToken, //
      CmisContentStreamType contentStream, //
      javax.xml.ws.Holder<CmisExtensionType> extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation setContentStream");
      }
      Connection conn = null;
      try
      {
         conn = storageProvider.getConnection(repositoryId);
         ContentStream cs = null;
         if (contentStream != null)
         {
            cs = new BaseContentStream(contentStream.getStream().getInputStream(), //
               contentStream.getFilename(), //
               contentStream.getMimeType());
         }

         ChangeTokenHolder hold = new ChangeTokenHolder();
         if (changeToken != null)
         {
            hold.setValue(changeToken.value);
         }

         documentId.value = conn.setContentStream(documentId.value, //
            cs, //
            changeToken == null ? null : hold, //
            overwriteFlag == null ? true : overwriteFlag);
      }
      catch (Exception e)
      {
         LOG.error("Set content stream error: " + e.getMessage(), e);
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
   public void updateProperties(String repositoryId, //
      javax.xml.ws.Holder<String> objectId, //
      javax.xml.ws.Holder<String> changeToken, //
      CmisPropertiesType properties, //
      javax.xml.ws.Holder<CmisExtensionType> extension) throws CmisException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Executing operation updateProperties");
      }
      Connection conn = null;
      try
      {
         conn = storageProvider.getConnection(repositoryId);
         ChangeTokenHolder hold = new ChangeTokenHolder();
         if (changeToken != null)
         {
            hold.setValue(changeToken.value);
         }

         objectId.value = conn.updateProperties(objectId.value, //
            changeToken.value == null ? null : hold, //
            TypeConverter.getPropertyMap(properties));
      }
      catch (Exception e)
      {
         LOG.error("Update properties error: " + e.getMessage(), e);
         throw ExceptionFactory.generateException(e);
      }
      finally
      {
         conn.close();
      }
   }

}
