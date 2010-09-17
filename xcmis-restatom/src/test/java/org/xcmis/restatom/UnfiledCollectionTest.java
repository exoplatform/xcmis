/*
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
import org.w3c.dom.NodeList;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.ObjectData;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id:  $
 *
 */
public class UnfiledCollectionTest extends BaseTest
{

   public void testUnfiled() throws Exception
   {
      if (!conn.getStorage().getRepositoryInfo().getCapabilities().isCapabilityUnfiling())
         return;
      
      String docId = createDocument(testFolderId, "doc1", null, null);

      String s = "<?xml version='1.0' encoding='utf-8'?>" //
         + "<entry xmlns='http://www.w3.org/2005/Atom'" + " xmlns:cmis='" + CmisConstants.CMIS_NS_URI + "'"//
         + " xmlns:cmisra='" + AtomCMIS.CMISRA_NS_URI + "'>" //
         + "<title>title</title>" //
         + "<summary>summary</summary>" //
         + "<id>" + docId + "</id>" //
         + "<cmisra:object>" //
         + "<cmis:properties>" //
         + "<cmis:propertyId localName='cmis:objectId' propertyDefinitionId='cmis:objectId'>" //
         + "<cmis:value>" + docId + "</cmis:value></cmis:propertyId>" //
         + "</cmis:properties>" //
         + "</cmisra:object>" //
         + "</entry>";
      assertTrue("Should be parents for the document.", getParents(docId).size() > 0);

      String requestURI = "http://localhost:8080/rest/cmisatom/" + cmisRepositoryId + "/unfiled";
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse response =
         service("POST", requestURI, "http://localhost:8080/rest", null, s.getBytes(), writer);

      //      printBody(writer.getBody());

      assertEquals(201, response.getStatus());

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

      org.w3c.dom.Node entry = getNode("atom:entry", xmlDoc);
      assertNotNull(entry);
      validateObjectEntry(entry, "cmis:document");

      assertTrue("Should not be parents for the document.", getParents(docId).size() == 0);

      conn.deleteObject(docId, null);
   }

   public void testGetUnfiled() throws Exception
   {
      if (!conn.getStorage().getRepositoryInfo().getCapabilities().isCapabilityUnfiling())
         return;
         
      String doc1Id = createDocument(testFolderId, "doc1", null, null);
      
      ObjectData object = conn.getStorage().getObjectById(doc1Id);
      conn.getStorage().unfileObject(object);

      String requestURI = "http://localhost:8080/rest/cmisatom/" + cmisRepositoryId + "/unfiled";
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse response = service("GET", requestURI, "http://localhost:8080/rest", null, null, writer);

      //      printBody(writer.getBody());

      assertEquals(200, response.getStatus());

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

      org.w3c.dom.Node xmlFeed = getNode("atom:feed", xmlDoc);
      assertNotNull(xmlFeed);
      validateFeedCommons(xmlFeed);

      assertTrue(hasLink(AtomCMIS.LINK_SERVICE, xmlFeed));
      assertTrue(hasLink(AtomCMIS.LINK_FIRST, xmlFeed));

      NodeList entries = getNodeSet("atom:entry", xmlFeed);
      int length = entries.getLength();
      assertEquals(1, length);

      conn.deleteObject(doc1Id, null);
   }

}
