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
package org.xcmis.wssoap.test.client;

import junit.framework.TestCase;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.soap.client.CmisContentStreamType;
import org.xcmis.soap.client.CmisException;
import org.xcmis.soap.client.CmisPropertiesType;
import org.xcmis.soap.client.CmisPropertyId;
import org.xcmis.soap.client.CmisPropertyString;
import org.xcmis.soap.client.EnumBaseObjectTypeIds;
import org.xcmis.soap.client.ObjectService;
import org.xcmis.soap.client.ObjectServicePort;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.RenditionFilter;

import java.net.URL;

import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

public abstract class BaseTest extends TestCase
{

   protected final Log LOG = ExoLogger.getLogger(BaseTest.class);

   protected final String cmisRepositoryId = "cmis1";

   protected final String rootFolderId = "00exo0jcr0root0uuid0000000000000";

   protected final String username = "root";

   protected final String password = "exo";

   protected ObjectServicePort object_port;

   /**
    * @see junit.framework.TestCase#setUp()
    */
   @Override
   protected void setUp() throws Exception
   {
      QName OBJECT_SERVICE_NAME = new QName("http://docs.oasis-open.org/ns/cmis/ws/200908/", "ObjectService");
      URL wsdlURL = ObjectService.WSDL_LOCATION;
      ObjectService ss = new ObjectService(wsdlURL, OBJECT_SERVICE_NAME);
      object_port = ss.getObjectServicePort();
      ((BindingProvider)object_port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
      ((BindingProvider)object_port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
   }

   protected String createDocument(String name) throws Exception
   {
      org.xcmis.soap.client.CmisPropertiesType _createDocument_properties = new CmisPropertiesType();
      // typeId
      CmisPropertyId propTypeId = new CmisPropertyId();
      propTypeId.setPropertyDefinitionId(CmisConstants.OBJECT_TYPE_ID);
      propTypeId.getValue().add(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      // name
      CmisPropertyString propName = new CmisPropertyString();
      propName.setPropertyDefinitionId(CmisConstants.NAME);
      propName.getValue().add(name);
      // fill the _createDocument_properties
      _createDocument_properties.getProperty().add(propTypeId);
      _createDocument_properties.getProperty().add(propName);

      java.lang.String _createDocument_folderId = rootFolderId;
      org.xcmis.soap.client.CmisContentStreamType _createDocument_contentStream = new CmisContentStreamType();
      _createDocument_contentStream.setStream(new DataHandler(new String("Content from name:" + name), "text/plain"));
      _createDocument_contentStream.setMimeType("text/plain");

      org.xcmis.soap.client.EnumVersioningState _createDocument_versioningState = null;
      java.util.List<java.lang.String> _createDocument_policies = null;
      org.xcmis.soap.client.CmisAccessControlListType _createDocument_addACEs = null;
      org.xcmis.soap.client.CmisAccessControlListType _createDocument_removeACEs = null;
      org.xcmis.soap.client.CmisExtensionType _createDocument_extensionVal = null;
      javax.xml.ws.Holder<org.xcmis.soap.client.CmisExtensionType> _createDocument_extension =
         new javax.xml.ws.Holder<org.xcmis.soap.client.CmisExtensionType>(_createDocument_extensionVal);
      javax.xml.ws.Holder<java.lang.String> _createDocument_objectId = new javax.xml.ws.Holder<java.lang.String>();
      try
      {
         object_port.createDocument(cmisRepositoryId, _createDocument_properties, _createDocument_folderId,
            _createDocument_contentStream, _createDocument_versioningState, _createDocument_policies,
            _createDocument_addACEs, _createDocument_removeACEs, _createDocument_extension, _createDocument_objectId);
         return _createDocument_objectId.value;
      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
         return null;
      }
   }

   protected void deleteObject(String objectId)
   {
      java.lang.Boolean _deleteObject_allVersions = null;
      org.xcmis.soap.client.CmisExtensionType _deleteObject_extension = null;
      try
      {
         org.xcmis.soap.client.CmisExtensionType _deleteObject__return =
            object_port.deleteObject(cmisRepositoryId, objectId, _deleteObject_allVersions, _deleteObject_extension);
         System.out.println("deleteObject.result=" + _deleteObject__return);

      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
      }
   }

   protected boolean hasObject(String objectId) throws Exception
   {
      java.lang.String _getObject_objectId = objectId;
      java.lang.String _getObject_filter = "*";
      java.lang.Boolean _getObject_includeAllowableActions = Boolean.TRUE;
      org.xcmis.soap.client.EnumIncludeRelationships _getObject_includeRelationships = null;
      java.lang.String _getObject_renditionFilter = RenditionFilter.NONE_FILTER;
      java.lang.Boolean _getObject_includePolicyIds = Boolean.FALSE;
      java.lang.Boolean _getObject_includeACL = Boolean.FALSE;
      org.xcmis.soap.client.CmisExtensionType _getObject_extension = null;
      try
      {
         org.xcmis.soap.client.CmisObjectType _getObject__return =
            object_port.getObject(cmisRepositoryId, _getObject_objectId, _getObject_filter,
               _getObject_includeAllowableActions, _getObject_includeRelationships, _getObject_renditionFilter,
               _getObject_includePolicyIds, _getObject_includeACL, _getObject_extension);
         System.out.println("getObject.result=" + _getObject__return);
         return _getObject__return != null;
      }
      catch (CmisException e)
      {
         return false;
      }

   }

   protected String createFolder() throws Exception
   {
      String resultFolderId = null;

      org.xcmis.soap.client.CmisPropertiesType _createFolder_properties = new CmisPropertiesType();
      // typeId
      CmisPropertyId propTypeId = new CmisPropertyId();
      propTypeId.setPropertyDefinitionId(CmisConstants.OBJECT_TYPE_ID);
      propTypeId.getValue().add(EnumBaseObjectTypeIds.CMIS_FOLDER.value());
      // name
      CmisPropertyString propName = new CmisPropertyString();
      propName.setPropertyDefinitionId(CmisConstants.NAME);
      propName.getValue().add("testCreateFolder_folder1_" + System.nanoTime());
      // fill the _createDocument_properties
      _createFolder_properties.getProperty().add(propTypeId);
      _createFolder_properties.getProperty().add(propName);

      java.lang.String _createFolder_folderId = rootFolderId;
      java.util.List<java.lang.String> _createFolder_policies = null;
      org.xcmis.soap.client.CmisAccessControlListType _createFolder_addACEs = null;
      org.xcmis.soap.client.CmisAccessControlListType _createFolder_removeACEs = null;
      org.xcmis.soap.client.CmisExtensionType _createFolder_extensionVal = null;
      javax.xml.ws.Holder<org.xcmis.soap.client.CmisExtensionType> _createFolder_extension =
         new javax.xml.ws.Holder<org.xcmis.soap.client.CmisExtensionType>(_createFolder_extensionVal);
      javax.xml.ws.Holder<java.lang.String> _createFolder_objectId = new javax.xml.ws.Holder<java.lang.String>();
      try
      {
         object_port.createFolder(cmisRepositoryId, _createFolder_properties, _createFolder_folderId,
            _createFolder_policies, _createFolder_addACEs, _createFolder_removeACEs, _createFolder_extension,
            _createFolder_objectId);
         resultFolderId = _createFolder_objectId.value;
         assertTrue(hasObject(resultFolderId));
         return resultFolderId;
      }
      catch (CmisException e)
      {
         System.out.println("Expected exception: cmisException has occurred.");
         System.out.println(e.toString());
         fail(e.getMessage());
         return null;
      }
   }

}
