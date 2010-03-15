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

import org.xcmis.core.EnumRelationshipDirection;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.tools.ByteArrayContainerResponseWriter;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.object.Entry;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilderFactory;

public class RelationshipsCollectionTest extends BaseTest
{

   public void setUp() throws Exception
   {
      super.setUp();
   }

   public void testGetRelationships() throws Exception
   {
      Entry source = createDocument(testFolderId, "doc1", null, null);
      Entry target = createDocument(testFolderId, "doc2", null, null);
      source.addRelationship("rel1", target, repository.getTypeDefinition("cmis:relationship"));

      String requestURI = "http://localhost:8080/rest" //
         + "/cmisatom/" + cmisRepositoryId //
         + "/relationships/" //
         + source //
         + "?includeAllowableActions=true";
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse resp = service("GET", requestURI, "http://localhost:8080/rest", null, null, writer);

      //          printBody(writer.getBody());
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

      assertEquals("1", getStringElement("cmisra:numItems", xmlFeed));

      org.w3c.dom.Node xmlEntry = getNode("atom:entry", xmlFeed);
      validateObjectEntry(xmlEntry, "cmis:relationship");
   }

   public void testCreateRelationship() throws Exception
   {
      Entry source = createDocument(testFolderId, "doc1", null, null);
      Entry target = createDocument(testFolderId, "doc2", null, null);
      String s = "<?xml version='1.0' encoding='utf-8'?>" //
         + "<entry xmlns='http://www.w3.org/2005/Atom'" //
         + " xmlns:cmis='" + CMIS.CMIS_NS_URI + "'"//  
         + " xmlns:cmism='http://docs.oasis-open.org/ns/cmis/messaging/200908/'" //
         + " xmlns:cmisra='http://docs.oasis-open.org/ns/cmis/restatom/200908/'>" //
         + "<title>title</title><summary>summary</summary>" //
         + "<cmisra:object>" //
         + "<cmis:properties>" //
         + "<cmis:propertyId cmis:localName='cmis:objectTypeId' propertyDefinitionId='cmis:objectTypeId'>" //
         + "<cmis:value>" + "cmis:relationship" + "</cmis:value></cmis:propertyId>" //
         + "<cmis:propertyId cmis:localName='cmis:sourceId' propertyDefinitionId='cmis:sourceId'>" //
         + "<cmis:value>" + source.getObjectId() + "</cmis:value></cmis:propertyId>" //
         + "<cmis:propertyId cmis:localName='cmis:targetId' propertyDefinitionId='cmis:targetId'>" //
         + "<cmis:value>" + target.getObjectId() + "</cmis:value></cmis:propertyId>" //
         + "</cmis:properties>" //
         + "</cmisra:object></entry>";

      String requestURI = "http://localhost:8080/rest/cmisatom/" //
         + cmisRepositoryId //
         + "/relationships/" //
         + source.getObjectId();

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse resp = service("POST", requestURI, "http://localhost:8080/rest", null, s.getBytes(), writer);

      //          printBody(writer.getBody());
      assertEquals(201, resp.getStatus());

      assertEquals(1, source.getRelationships(EnumRelationshipDirection.EITHER, true, repository
         .getTypeDefinition("cmis:relationship")).size());

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

      org.w3c.dom.Node xmlEntry = getNode("atom:entry", xmlDoc);
      validateObjectEntry(xmlEntry, "cmis:relationship");
   }

}
