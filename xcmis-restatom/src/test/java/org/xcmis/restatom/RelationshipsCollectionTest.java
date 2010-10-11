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

package org.xcmis.restatom;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.RelationshipDirection;
import org.xcmis.spi.model.impl.IdProperty;
import org.xcmis.spi.model.impl.StringProperty;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

public class RelationshipsCollectionTest extends BaseTest
{

   private String sourceId;

   @Override
   public void setUp() throws Exception
   {
      super.setUp();
   }

   @Override
   public void tearDown() throws Exception
   {
      List<CmisObject> rels =
         conn.getObjectRelationships(sourceId, RelationshipDirection.EITHER, null, true, false, true,
            AtomCMIS.WILDCARD, -1, 0).getItems();
      for (CmisObject cmisObject : rels)
      {
         conn.deleteObject(cmisObject.getObjectInfo().getId(), null);
      }
      super.tearDown();
   }

   public void testGetRelationships() throws Exception
   {
      sourceId = createDocument(testFolderId, "doc1", null, null);
      String targetId = createDocument(testFolderId, "doc2", null, null);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();

      Property<?> typeIdProperty =
         new IdProperty(CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID,
            CmisConstants.OBJECT_TYPE_ID, BaseType.RELATIONSHIP.value());
      Property<?> sourceIdProperty =
         new IdProperty(CmisConstants.SOURCE_ID, CmisConstants.SOURCE_ID, CmisConstants.SOURCE_ID,
            CmisConstants.SOURCE_ID, sourceId);
      Property<?> targetIdProperty =
         new IdProperty(CmisConstants.TARGET_ID, CmisConstants.TARGET_ID, CmisConstants.TARGET_ID,
            CmisConstants.TARGET_ID, targetId);
      Property<?> nameProperty =
         new StringProperty(CmisConstants.NAME, CmisConstants.NAME, CmisConstants.NAME, CmisConstants.NAME, "relation1");

      properties.put(CmisConstants.OBJECT_TYPE_ID, typeIdProperty);
      properties.put(CmisConstants.SOURCE_ID, sourceIdProperty);
      properties.put(CmisConstants.TARGET_ID, targetIdProperty);
      properties.put(CmisConstants.NAME, nameProperty);

      conn.createRelationship(properties, null, null, null);

      String requestURI = "http://localhost:8080/rest" //
         + "/cmisatom/" + cmisRepositoryId //
         + "/relationships/" //
         + sourceId //
         + "?includeAllowableActions=true";
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse resp = service("GET", requestURI, "http://localhost:8080/rest", null, null, writer);

      //printBody(writer.getBody());

      assertEquals(200, resp.getStatus());

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

      org.w3c.dom.Node xmlFeed = getNode("atom:feed", xmlDoc);
      validateFeedCommons(xmlFeed);

      assertTrue(hasLink(AtomCMIS.LINK_SERVICE, xmlFeed));
      assertTrue(hasLink(AtomCMIS.LINK_SELF, xmlFeed));
      assertTrue(hasLink(AtomCMIS.LINK_VIA, xmlFeed));
      assertTrue(hasLink(AtomCMIS.LINK_FIRST, xmlFeed));

      //assertEquals("1", getStringElement("cmisra:numItems", xmlFeed));

      org.w3c.dom.Node xmlEntry = getNode("atom:entry", xmlFeed);
      validateObjectEntry(xmlEntry, "cmis:relationship");
   }

   public void testCreateRelationship() throws Exception
   {
      sourceId = createDocument(testFolderId, "doc1", null, null);
      String targetId = createDocument(testFolderId, "doc2", null, null);
      String s = "<?xml version='1.0' encoding='utf-8'?>" //
         + "<entry xmlns='http://www.w3.org/2005/Atom'" //
         + " xmlns:cmis='" + CmisConstants.CMIS_NS_URI + "'"//
         + " xmlns:cmism='http://docs.oasis-open.org/ns/cmis/messaging/200908/'" //
         + " xmlns:cmisra='http://docs.oasis-open.org/ns/cmis/restatom/200908/'>" //
         + "<title>title</title><summary>summary</summary>" //
         + "<cmisra:object>" //
         + "<cmis:properties>" //
         + "<cmis:propertyId cmis:localName='cmis:objectTypeId' propertyDefinitionId='cmis:objectTypeId'>" //
         + "<cmis:value>" + "cmis:relationship" + "</cmis:value></cmis:propertyId>" //
         + "<cmis:propertyId cmis:localName='cmis:sourceId' propertyDefinitionId='cmis:sourceId'>" //
         + "<cmis:value>" + sourceId + "</cmis:value></cmis:propertyId>" //
         + "<cmis:propertyId cmis:localName='cmis:targetId' propertyDefinitionId='cmis:targetId'>" //
         + "<cmis:value>" + targetId + "</cmis:value></cmis:propertyId>" //
         + "</cmis:properties>" //
         + "</cmisra:object></entry>";

      String requestURI = "http://localhost:8080/rest/cmisatom/" //
         + cmisRepositoryId //
         + "/relationships/" //
         + sourceId;

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse resp = service("POST", requestURI, "http://localhost:8080/rest", null, s.getBytes(), writer);

      //          printBody(writer.getBody());
      assertEquals(201, resp.getStatus());

      assertEquals(1, conn.getObjectRelationships(sourceId, RelationshipDirection.EITHER, null, true, false, true,
         AtomCMIS.WILDCARD, -1, 0).getItems().size());

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

      org.w3c.dom.Node xmlEntry = getNode("atom:entry", xmlDoc);
      validateObjectEntry(xmlEntry, "cmis:relationship");
   }

}
