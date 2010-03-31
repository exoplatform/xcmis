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

import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.tools.ByteArrayContainerResponseWriter;
import org.xcmis.spi.object.CmisObject;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @author <a href="mailto:max.shaposhnik@exoplatform.com">Max Shaposhnik</a>
 * @version $$Id: PoliciesCollectionTest.java 2 2010-02-04 17:21:49Z andrew00x $$
 */
public class PoliciesCollectionTest extends BaseTest
{

   private String docId;

   private String policyId;

   public void setUp() throws Exception
   {
      super.setUp();
      docId = createDocument(testFolderId, "doc1", null, null);
      policyId = createPolicy(testFolderId, "policy1", "policy text");
   }

   public void tearDown() throws Exception
   {
      conn.removePolicy(policyId, docId);
      conn.deleteObject(policyId, null);
      super.tearDown();
   }

   public void testGetAppliedPolicies() throws Exception
   {
      conn.applyPolicy(policyId, docId);

      String requestURI = "http://localhost:8080/rest" //
         + "/cmisatom/" //
         + cmisRepositoryId //
         + "/policies/" //
         + docId;
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse resp = service("GET", requestURI, "http://localhost:8080/rest", null, null, writer);

      //      printBody(writer.getBody());
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
      validateObjectEntry(xmlEntry, "cmis:policy");
   }

   public void testApplyPolicy() throws Exception
   {
      String s =
         "<?xml version='1.0' encoding='UTF-8' standalone='yes'?>" //
            + "<atom:entry xmlns:app='http://www.w3.org/2007/app' " //
            + " xmlns:atom='http://www.w3.org/2005/Atom' "//  
            + " xmlns:cmis='http://docs.oasis-open.org/ns/cmis/core/200908/'" //
            + " xmlns:cmism='http://docs.oasis-open.org/ns/cmis/messaging/200908/'" //
            + " xmlns:cmisra='http://docs.oasis-open.org/ns/cmis/restatom/200908/'>" //
            + "<atom:author><atom:name>Makis</atom:name></atom:author>" //
            + "<atom:content src='http://cmisexample.oasis-open.org/rep1/"
            + policyId
            + "' />" //
            + "<atom:id>urn:uuid:" + policyId + "</atom:id>"
            + "<atom:title type='text'>Security Policy</atom:title>"
            + "<atom:updated>2009-11-26T11:49:00.212-02:00</atom:updated>" + "<cmisra:object>"
            + "<cmis:properties>"
            + "<cmis:propertyId localName='rep-cmis:objectId' propertyDefinitionId='cmis:objectId'>"
            + "<cmis:value>"
            + policyId + "</cmis:value>" + "</cmis:propertyId>" + "</cmis:properties>"
            + "</cmisra:object>"
            + "</atom:entry>";

      String requestURI = "http://localhost:8080/rest" //
         + "/cmisatom/" //
         + cmisRepositoryId //
         + "/policies/" //
         + docId;
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse resp = service("POST", requestURI, "http://localhost:8080/rest", null, s.getBytes(), writer);

      //      printBody(writer.getBody());
      assertEquals(201, resp.getStatus());

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

      org.w3c.dom.Node xmlEntry = getNode("atom:entry", xmlDoc);
      validateObjectEntry(xmlEntry, "cmis:policy");
   }

   public void testDeletePolicy() throws Exception
   {
      conn.applyPolicy(policyId, docId);
      String s =
         "<?xml version='1.0' encoding='UTF-8' standalone='yes'?>" //
            + "<atom:entry xmlns:app='http://www.w3.org/2007/app' " //
            + " xmlns:atom='http://www.w3.org/2005/Atom' "//  
            + " xmlns:cmis='http://docs.oasis-open.org/ns/cmis/core/200908/'" //
            + " xmlns:cmism='http://docs.oasis-open.org/ns/cmis/messaging/200908/'" //
            + " xmlns:cmisra='http://docs.oasis-open.org/ns/cmis/restatom/200908/'>" //
            + "<atom:author><atom:name>Makis</atom:name></atom:author>" //
            + "<atom:content src='http://cmisexample.oasis-open.org/rep1/"
            + policyId
            + "' />" //
            + "<atom:id>urn:uuid:" + policyId + "</atom:id>"
            + "<atom:title type='text'>Security Policy</atom:title>"
            + "<atom:updated>2009-11-26T11:49:00.212-02:00</atom:updated>" + "<cmisra:object>"
            + "<cmis:properties>"
            + "<cmis:propertyId localName='cmis:objectId' propertyDefinitionId='cmis:objectId'>"
            + "<cmis:value>"
            + policyId + "</cmis:value>" + "</cmis:propertyId>" + "</cmis:properties>"
            + "</cmisra:object>"
            + "</atom:entry>";

      String requestURI = "http://localhost:8080/rest" //
         + "/cmisatom/" //
         + cmisRepositoryId //
         + "/policies/" //
         + docId;
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse resp = service("DELETE", requestURI, "http://localhost:8080/rest", null, s.getBytes(), writer);

      assertEquals(200, resp.getStatus());
      List<CmisObject> res = conn.getAppliedPolicies(docId, true, null);
      assertEquals(0, res.size()); // No policies 
   }
}
