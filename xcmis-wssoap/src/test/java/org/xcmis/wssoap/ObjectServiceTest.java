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

package org.xcmis.wssoap;

import org.apache.cxf.endpoint.Server;
import org.xcmis.core.CmisAllowableActionsType;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisPropertyId;
import org.xcmis.core.CmisPropertyString;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.core.EnumPropertiesRelationship;
import org.xcmis.core.EnumVersioningState;
import org.xcmis.messaging.CmisContentStreamType;
import org.xcmis.messaging.CmisExtensionType;
import org.xcmis.soap.ObjectServicePort;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ChangeTokenHolder;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.IncludeRelationships;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.data.BaseContentStream;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.wssoap.impl.ObjectServicePortImpl;

import javax.activation.DataHandler;
import javax.xml.ws.Holder;

public class ObjectServiceTest extends BaseTest
{

   private ObjectServicePort port;

   /** Service name. */
   private final static String SERVICE_NAME = "ObjectService";

   /** Address. */
   private final static String SERVICE_ADDRESS = "http://localhost:8081/" + SERVICE_NAME;

   /** Server. */
   private Server server;

   public void setUp() throws Exception
   {
      super.setUp();
      server = complexDeployService(SERVICE_ADDRESS, new ObjectServicePortImpl(storageProvider), null, null, true);
      port = getObjectService(SERVICE_ADDRESS);
      assertNotNull(server);
      assertNotNull(port);
   }

   public void testCreateDocument() throws Exception
   {
      CmisPropertiesType props = new CmisPropertiesType();
      // typeId
      CmisPropertyId propTypeId = new CmisPropertyId();
      propTypeId.setPropertyDefinitionId(CMIS.OBJECT_TYPE_ID);
      propTypeId.getValue().add(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      // name
      CmisPropertyString propName = new CmisPropertyString();
      propName.setPropertyDefinitionId(CMIS.NAME);
      propName.getValue().add("document1");

      props.getProperty().add(propTypeId);
      props.getProperty().add(propName);

      javax.xml.ws.Holder<String> created = new javax.xml.ws.Holder<String>();
      port.createDocument(//
         repositoryId, //
         props, //
         testFolderId, // Parent
         null, // Content stream
         EnumVersioningState.MAJOR, //
         null, // Policies
         null, // add ACL
         null, // remove ACL
         new Holder<CmisExtensionType>(), // Extensions
         created // holder for id of created object
         );
      try
      {
         conn.getObject(created.value, false, IncludeRelationships.NONE, false, false, false, null, null);
      }
      catch (ObjectNotFoundException onfe)
      {
         fail("Document not found.");
      }
   }

   public void testCreateFolder() throws Exception
   {
      CmisPropertiesType props = new CmisPropertiesType();
      // typeId
      CmisPropertyId propTypeId = new CmisPropertyId();
      propTypeId.setPropertyDefinitionId(CMIS.OBJECT_TYPE_ID);
      propTypeId.getValue().add(EnumBaseObjectTypeIds.CMIS_FOLDER.value());
      // name
      CmisPropertyString propName = new CmisPropertyString();
      propName.setPropertyDefinitionId(CMIS.NAME);
      propName.getValue().add("folder1");

      props.getProperty().add(propTypeId);
      props.getProperty().add(propName);

      javax.xml.ws.Holder<String> created = new javax.xml.ws.Holder<String>();
      port.createFolder(//
         repositoryId, //
         props, //
         testFolderId, // Parent
         null, // Policies
         null, // Add ACL
         null, // Remove ACL
         new Holder<CmisExtensionType>(), // Extensions
         created // holder for id of created object
         );
      try
      {
         conn.getObject(created.value, false, IncludeRelationships.NONE, false, false, false, null, null);
      }
      catch (ObjectNotFoundException onfe)
      {
         fail("Folder not found.");
      }
   }

   public void testCreateRelationship() throws Exception
   {
      String source = createDocument(testFolderId, "source1");
      String target = createDocument(testFolderId, "target1");

      // typeId
      CmisPropertyId propTypeId = new CmisPropertyId();
      propTypeId.setPropertyDefinitionId(CMIS.OBJECT_TYPE_ID);
      propTypeId.getValue().add(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      // name
      CmisPropertyString propName = new CmisPropertyString();
      propName.setPropertyDefinitionId(CMIS.NAME);
      propName.getValue().add("relation1");
      // sourceId
      CmisPropertyId sourceId = new CmisPropertyId();
      sourceId.setPropertyDefinitionId(EnumPropertiesRelationship.CMIS_SOURCE_ID.value());
      sourceId.getValue().add(source);
      // targetId
      CmisPropertyId targetId = new CmisPropertyId();
      targetId.setPropertyDefinitionId(EnumPropertiesRelationship.CMIS_TARGET_ID.value());
      targetId.getValue().add(target);

      CmisPropertiesType props = new CmisPropertiesType();
      props.getProperty().add(propTypeId);
      props.getProperty().add(propName);
      props.getProperty().add(sourceId);
      props.getProperty().add(targetId);

      javax.xml.ws.Holder<String> created = new javax.xml.ws.Holder<String>();
      port.createRelationship(//
         repositoryId, //
         props, //
         null, // policies
         null, // add ACL
         null, // remove ACL
         new Holder<CmisExtensionType>(), // Extensions
         created // holder for id of created object
         );
      try
      {
         conn.getObject(created.value, false, IncludeRelationships.NONE, false, false, false, null, null);
      }
      catch (ObjectNotFoundException onfe)
      {
         fail("Relationship not found.");
      }
      conn.deleteObject(created.value, true);
   }

   public void testCreatePolicy() throws Exception
   {
      // typeId
      CmisPropertyId propTypeId = new CmisPropertyId();
      propTypeId.setPropertyDefinitionId(CMIS.OBJECT_TYPE_ID);
      propTypeId.setLocalName(CMIS.OBJECT_TYPE_ID);
      propTypeId.getValue().add(EnumBaseObjectTypeIds.CMIS_POLICY.value());
      // name
      CmisPropertyString propName = new CmisPropertyString();
      propName.setPropertyDefinitionId(CMIS.NAME);
      propName.setLocalName(CMIS.NAME);
      propName.getValue().add("policy1");

      CmisPropertyString propText = new CmisPropertyString();
      propText.setPropertyDefinitionId(CMIS.POLICY_TEXT);
      propText.setLocalName(CMIS.POLICY_TEXT);
      propText.getValue().add("policy23");

      CmisPropertiesType props = new CmisPropertiesType();
      props.getProperty().add(propTypeId);
      props.getProperty().add(propName);
      props.getProperty().add(propText);

      javax.xml.ws.Holder<String> created = new javax.xml.ws.Holder<String>();
      port.createPolicy(//
         repositoryId, //
         props, //
         testFolderId, //
         null, // policies
         null, // add ACL
         null, // remove ACL
         new Holder<CmisExtensionType>(), // Extensions
         created // holder for id of created object
         );
      try
      {
         conn.getObject(created.value, false, IncludeRelationships.NONE, false, false, false, null, null);
      }
      catch (ObjectNotFoundException onfe)
      {
         fail("Policy not found.");
      }
   }

   public void testDeleteContentStream() throws Exception
   {
      String docId = createDocument(testFolderId, "doc1");
      String content = "<?xml version='1.0' encoding='UTF-8'?>";
      ContentStream stream = new BaseContentStream(content.getBytes(), "test", "text/xml");
      String updated = conn.setContentStream(//
         docId, //
         stream, //
         new ChangeTokenHolder(), // change token
         true // overwrite
         );
      Holder<String> hId = new Holder<String>(updated);

      ContentStream cs = conn.getContentStream(updated, null, 0, -1);
      byte b[] = new byte[1024];
      int rd = cs.getStream().read(b);
      assertEquals(content, new String(b, 0, rd));

      // delete content
      port.deleteContentStream(repositoryId, hId, new Holder<String>(null), new Holder<CmisExtensionType>(
         new CmisExtensionType()));
      try
      {
         cs = conn.getContentStream(hId.value, null, 0, -1);
         fail();
      }
      catch (ConstraintException ex)
      {

      }

   }

   public void testDeleteObject() throws Exception
   {
      String id = createDocument(testFolderId, "doc1");
      port.deleteObject(repositoryId, id, true, null);
      try
      {
         conn.getObject(id, false, IncludeRelationships.NONE, false, false, false, null, null);
         fail("Object " + id + " must be removed.");
      }
      catch (ObjectNotFoundException ex)
      {
      }
   }

   public void testGetAllowableActions() throws Exception
   {
      String id = createDocument(testFolderId, "doc1");
      CmisAllowableActionsType actions = port.getAllowableActions(repositoryId, id, new CmisExtensionType());
      assertNotNull(actions);
   }

   public void testGetObjectById() throws Exception
   {
      String id = createDocument(testFolderId, "doc1");
      CmisObjectType obj = port.getObject(//
         repositoryId, //
         id, // 
         null, // Property filter
         false, // Allowable actions
         EnumIncludeRelationships.NONE, //
         null, // Rendition Filter
         false, // Include policies IDs
         false, // Include ACL
         new CmisExtensionType() // Extensions
         );
      assertNotNull(obj);
      assertEquals(id, getObjectId(obj));
   }

   public void testGetObjectByPath() throws Exception
   {
      String folderId = createFolder(testFolderId, "folder1");
      String docId = createDocument(folderId, "doc1");
      CmisObjectType res = port.getObjectByPath(//
         repositoryId, //
         "/testFolder/folder1/doc1", // 'testFolder' is root folder for test
         null, // Property Filter
         false, // Allowable actions
         EnumIncludeRelationships.NONE, //
         null, // Rendition filter
         false, // Include policies
         false, // Include ACL
         new CmisExtensionType() // Extensions
         );
      assertNotNull(res);
      assertEquals(docId, getObjectId(res));
   }

   public void testMoveObject() throws Exception
   {
      String id = createDocument(testFolderId, "doc1234");
      String targetId = createFolder(testFolderId, "folder1");
      Holder<String> hId = new Holder<String>(id);
      port.moveObject(//
         repositoryId, //
         hId, //
         targetId, // Target folder
         testFolderId, // Source folder 
         new Holder<CmisExtensionType>() // Extension
         );
      assertEquals(id, hId.value);
      // Check it is moved. 
      CmisObjectType moved = port.getObjectByPath(//
         repositoryId, //
         "/testFolder/folder1/doc1234", // 'testFolder' is root folder for test
         null, //
         false, //
         EnumIncludeRelationships.NONE, //
         null, //
         false, //
         false, //
         new CmisExtensionType() //
         );
      assertEquals(id, getObjectId(moved));
   }

   public void testSetContentStream() throws Exception
   {
      String docId = createDocument(testFolderId, "doc1");

      String content = "hello";
      Holder<String> hId = new Holder<String>(docId);
      CmisContentStreamType contentStreamType = new CmisContentStreamType();
      contentStreamType.setFilename("test");
      contentStreamType.setMimeType("text/plain");
      contentStreamType.setStream(new DataHandler(new String(content), "text/plain"));
      port.setContentStream(//
         repositoryId, //
         hId, // Document id holder
         true, // Overwrite
         new Holder<String>(null), // Change token
         contentStreamType, // Stream
         new Holder<CmisExtensionType>() // Extension
         );

      ContentStream cs = conn.getContentStream(hId.value, null, 0, -1);
      byte[] b = new byte[128];
      int rd = cs.getStream().read(b);
      assertEquals(content, new String(b, 0, rd));
   }

   /**
    * Get object service.
    * 
    * @return TicketOrderService
    */
   private ObjectServicePort getObjectService(String address)
   {
      org.apache.cxf.jaxws.JaxWsProxyFactoryBean client = new org.apache.cxf.jaxws.JaxWsProxyFactoryBean();
      client.setServiceClass(ObjectServicePort.class);
      client.setAddress(address);
      Object obj = client.create();
      return (ObjectServicePort)obj;
   }

   protected void tearDown() throws Exception
   {
      server.stop();
      super.tearDown();
   }

}
