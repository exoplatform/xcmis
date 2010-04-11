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

import org.apache.commons.codec.binary.Base64;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.rest.tools.ByteArrayContainerResponseWriter;
import org.w3c.dom.NodeList;
import org.xcmis.spi.BaseContentStream;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.model.CmisObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: FolderChildrenCollectionTest.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class FolderChildrenCollectionTest extends BaseTest
{

   public void testAllowableActions() throws Exception
   {
      String docId = createDocument(testFolderId, "doc1", null, null);
      String requestURI = "http://localhost:8080/rest/cmisatom/" + cmisRepositoryId + "/allowableactions/" + docId;
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();

      ContainerResponse resp = service("GET", requestURI, "http://localhost:8080/rest", null, null, writer);

      //      printBody(writer.getBody());
      assertEquals(200, resp.getStatus());

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);

      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

      org.w3c.dom.Node actions = getNode("cmis:allowableActions", xmlDoc);
      validateAllowableActions(actions);
   }

   public void testCreateDocument() throws Exception
   {
      String s = "<?xml version='1.0' encoding='utf-8'?>" //
         + "<entry xmlns='http://www.w3.org/2005/Atom'" + " xmlns:cmis='" + CmisConstants.CMIS_NS_URI + "'" //
         + " xmlns:cmisra='" + AtomCMIS.CMISRA_NS_URI + "'>" //
         + "<title>title</title><summary>summary</summary>" //
         + "<content type='text'>hello</content>" //
         + "<cmisra:object><cmis:properties>" //
         + "<cmis:propertyId localName='cmis:objectTypeId' propertyDefinitionId='cmis:objectTypeId'>" //
         + "<cmis:value>cmis:document</cmis:value></cmis:propertyId>" //
         + "</cmis:properties>" + "</cmisra:object></entry>";
      String requestURI = "http://localhost:8080/rest/cmisatom/" + cmisRepositoryId + "/children/" + testFolderId;
      assertFalse(getChildren(testFolderId).getItems().iterator().hasNext());
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();

      ContainerResponse resp = service("POST", requestURI, "http://localhost:8080/rest", null, s.getBytes(), writer);

      //            printBody(writer.getBody());
      assertEquals(201, resp.getStatus());

      assertNotNull(resp.getHttpHeaders().getFirst(HttpHeaders.LOCATION));
      assertTrue(getChildren(testFolderId).getItems().iterator().hasNext());

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

      org.w3c.dom.Node entry = getNode("atom:entry", xmlDoc);
      validateObjectEntry(entry, "cmis:document");
   }

   public void testCreateDocumentCmisContent() throws Exception
   {
      String s = "<?xml version='1.0' encoding='utf-8'?>" //
         + "<entry xmlns='http://www.w3.org/2005/Atom'" + " xmlns:cmis='" + CmisConstants.CMIS_NS_URI + "'" //
         + " xmlns:cmisra='" + AtomCMIS.CMISRA_NS_URI + "'>" //
         + "<title>title</title><summary>summary</summary>" //
         + "<cmisra:content>" //
         + "<cmisra:mediatype>text/plain</cmisra:mediatype>" //
         + "<cmisra:base64>" + new String(Base64.encodeBase64("hello".getBytes())) + "</cmisra:base64>" //
         + "</cmisra:content>" //
         + "<cmisra:object><cmis:properties>" //
         + "<cmis:propertyId localName='cmis:objectTypeId' propertyDefinitionId='cmis:objectTypeId'>" //
         + "<cmis:value>cmis:document</cmis:value></cmis:propertyId>" //
         + "</cmis:properties>" + "</cmisra:object></entry>";
      String requestURI = "http://localhost:8080/rest/cmisatom/" + cmisRepositoryId + "/children/" + testFolderId;
      assertFalse(getChildren(testFolderId).isHasMoreItems());
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();

      ContainerResponse resp = service("POST", requestURI, "http://localhost:8080/rest", null, s.getBytes(), writer);

      //            printBody(writer.getBody());
      assertEquals(201, resp.getStatus());

      assertNotNull(resp.getHttpHeaders().getFirst(HttpHeaders.LOCATION));
      assertTrue(getChildren(testFolderId).getItems().iterator().hasNext());
      CmisObject doc = getChildren(testFolderId).getItems().iterator().next();
      byte[] buff = new byte[1024];
      int rd = conn.getContentStream(doc.getObjectInfo().getId(), null, -1, -1).getStream().read(buff);
      assertEquals("hello", new String(buff, 0, rd));

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

      org.w3c.dom.Node entry = getNode("atom:entry", xmlDoc);
      validateObjectEntry(entry, "cmis:document");
   }

   public void testCreateFolder() throws Exception
   {
      String s = "<?xml version='1.0' encoding='utf-8'?>" //
         + "<entry xmlns='http://www.w3.org/2005/Atom' xmlns:cmis='" + CmisConstants.CMIS_NS_URI + "' "//
         + " xmlns:cmisra='" + AtomCMIS.CMISRA_NS_URI + "'>" //
         + "<title>title</title><summary>summary</summary>" //
         + "<cmisra:object><cmis:properties>" //
         + "<cmis:propertyId localName='cmis:objectTypeId' propertyDefinitionId='cmis:objectTypeId'>" //
         + "<cmis:value>cmis:folder</cmis:value></cmis:propertyId>" //
         + "</cmis:properties>" //
         + "</cmisra:object></entry>";

      String requestURI = "http://localhost:8080/rest/cmisatom/" + cmisRepositoryId + "/children/" + testFolderId;
      assertFalse(getChildren(testFolderId).getItems().iterator().hasNext());
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();

      ContainerResponse resp = service("POST", requestURI, "http://localhost:8080/rest", null, s.getBytes(), writer);
      //                  printBody(writer.getBody());
      assertEquals(201, resp.getStatus());

      assertNotNull(resp.getHttpHeaders().getFirst(HttpHeaders.LOCATION));
      assertTrue(getChildren(testFolderId).getItems().iterator().hasNext());

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

      org.w3c.dom.Node entry = getNode("atom:entry", xmlDoc);
      validateObjectEntry(entry, "cmis:folder");
   }

   public void testDeleteContent() throws Exception
   {
      ContentStream content = new BaseContentStream("to be or not to be".getBytes(), "file", "text/plain");
      String docId = createDocument(testFolderId, "doc1", null, content);
      ContentStream docStream = conn.getContentStream(docId, null, -1, -1);
      assertNotNull(docStream);

      String requestURI = "http://localhost:8080/rest/cmisatom/" + cmisRepositoryId + "/file/" + docId;

      ContainerResponse resp = service("DELETE", requestURI, "http://localhost:8080/rest", null, null);
      assertEquals(204, resp.getStatus());
      try
      {
         docStream = conn.getContentStream(docId, null, -1, -1);
         fail("Should be the ConstraintException 'Object does not have content stream.'");
      }
      catch (ConstraintException e)
      {
         // "Object does not have content stream."
      }
   }

   public void testDeleteObject() throws Exception
   {
      String docId = createDocument(testFolderId, "doc1", null, null);
      String requestURI = "http://localhost:8080/rest/cmisatom/" + cmisRepositoryId + "/object/" + docId;

      ContainerResponse resp = service("DELETE", requestURI, "http://localhost:8080/rest", null, null);
      assertEquals(204, resp.getStatus());
      try
      {
         getCmisObject(docId);
         fail("Object should be removed.");
      }
      catch (ObjectNotFoundException e)
      {
         // ok
      }
   }

   public void testGetChildren() throws Exception
   {
      String doc1 = createDocument(testFolderId, "doc1", null, null);
      String doc2 = createDocument(testFolderId, "doc2", null, null);

      String requestURI = "http://localhost:8080/rest/cmisatom/" + cmisRepositoryId + "/children/" + testFolderId;

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse resp = service("GET", requestURI, "http://localhost:8080/rest", null, null, writer);

      //            printBody(writer.getBody());
      assertEquals(200, resp.getStatus());

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

      org.w3c.dom.Node xmlFeed = getNode("atom:feed", xmlDoc);
      validateFeedCommons(xmlFeed);

      assertTrue(hasLink(AtomCMIS.LINK_SERVICE, xmlFeed));
      assertTrue(hasLink(AtomCMIS.LINK_SELF, xmlFeed));
      assertTrue(hasLink(AtomCMIS.LINK_VIA, xmlFeed));
      assertTrue(hasLink(AtomCMIS.LINK_DOWN, xmlFeed));
      assertTrue(hasLink(AtomCMIS.LINK_CMIS_FOLDERTREE, xmlFeed));
      assertTrue(hasLink(AtomCMIS.LINK_UP, xmlFeed));

      //      assertEquals("", getStringElement("cmisra:numItems", xmlFeed));

      NodeList entries = getNodeSet("atom:entry", xmlFeed);
      int length = entries.getLength();
      assertEquals(2, length);
      List<String> docs = new ArrayList<String>();
      for (int i = 0; i < length; i++)
      {
         org.w3c.dom.Node n = entries.item(i);
         validateObjectEntry(n, "cmis:document");
         docs.add(getNodeValueWithNodeProperty("cmisra:object/cmis:properties", "cmis:propertyId", "cmis:objectId", n));
      }

      assertEquals(2, docs.size());
      assertTrue(docs.contains(doc1));
      assertTrue(docs.contains(doc2));
   }

   public void testGetChildrenWithAllowableActions() throws Exception
   {
      String document1 = createDocument(testFolderId, "doc1", null, null);
      String document2 = createDocument(testFolderId, "doc2", null, null);

      String requestURI =
         "http://localhost:8080/rest/cmisatom/" + cmisRepositoryId + "/children/" + testFolderId
            + "?includeAllowableActions=true";

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse resp = service("GET", requestURI, "http://localhost:8080/rest", null, null, writer);

      //      printBody(writer.getBody());
      assertEquals(200, resp.getStatus());

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

      org.w3c.dom.Node xmlFeed = getNode("atom:feed", xmlDoc);
      validateFeedCommons(xmlFeed);

      //      assertEquals("2", getStringElement("cmisra:numItems", xmlFeed));

      NodeList entries = getNodeSet("atom:entry", xmlFeed);
      int length = entries.getLength();
      assertEquals(2, length);
      List<String> docs = new ArrayList<String>();
      for (int i = 0; i < length; i++)
      {
         org.w3c.dom.Node n = entries.item(i);
         validateObjectEntry(n, "cmis:document");
         validateAllowableActions(getNode("cmisra:object/cmis:allowableActions", n));
         docs.add(getNodeValueWithNodeProperty("cmisra:object/cmis:properties", "cmis:propertyId", "cmis:objectId", n));
      }

      assertEquals(2, docs.size());
      assertTrue(docs.contains(document1));
      assertTrue(docs.contains(document2));
   }

   public void testGetContent() throws Exception
   {
      ContentStream content = new BaseContentStream("to be or not to be".getBytes(), "file", "text/plain");
      String docId = createDocument(testFolderId, "doc1", null, content);

      String requestURI = "http://localhost:8080/rest/cmisatom/" + cmisRepositoryId + "/file/" + docId;
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();

      ContainerResponse resp = service("GET", requestURI, "http://localhost:8080/rest", null, null, writer);
      assertEquals("to be or not to be", new String(writer.getBody()));
      assertEquals(200, resp.getStatus());
   }

   public void testGetObject() throws Exception
   {
      String docId = createDocument(testFolderId, "doc1", null, null);
      String requestURI = "http://localhost:8080/rest/cmisatom/" + cmisRepositoryId + "/object/" + docId;
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();

      ContainerResponse resp = service("GET", requestURI, "http://localhost:8080/rest", null, null, writer);
      //            printBody(writer.getBody());
      assertEquals(200, resp.getStatus());
      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

      org.w3c.dom.Node entry = getNode("atom:entry", xmlDoc);

      validateObjectEntry(entry, "cmis:document");

      String resId =
         getNodeValueWithNodeProperty("cmisra:object/cmis:properties", "cmis:propertyId", "cmis:objectId", entry);

      assertEquals(docId, resId);
   }

   public void testGetObjectByPath() throws Exception
   {
      String docId = createDocument(testFolderId, "doc1", null, null);
      String requestURI =
         "http://localhost:8080/rest/cmisatom/" + cmisRepositoryId + "/objectbypath/" + testFolderName + "/doc1";
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();

      ContainerResponse resp = service("GET", requestURI, "http://localhost:8080/rest", null, null, writer);
      //            printBody(writer.getBody());
      assertEquals(200, resp.getStatus());
      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

      org.w3c.dom.Node entry = getNode("atom:entry", xmlDoc);

      validateObjectEntry(entry, "cmis:document");

      String resId =
         getNodeValueWithNodeProperty("cmisra:object/cmis:properties", "cmis:propertyId", "cmis:objectId", entry);

      assertEquals(docId, resId);
   }

   public void testMoveObject() throws Exception
   {
      String id = createDocument(testFolderId, "doc1", null, null);

      String s = "<?xml version='1.0' encoding='utf-8'?>" //
         + "<entry xmlns='http://www.w3.org/2005/Atom'" + " xmlns:cmis='" + CmisConstants.CMIS_NS_URI + "'" //
         + " xmlns:cmisra='" + AtomCMIS.CMISRA_NS_URI + "'>" //
         + "<title>title</title><summary>summary</summary>" //
         + "<cmisra:object><cmis:properties>" //
         + "<cmis:propertyId localName='cmis:objectId' propertyDefinitionId='cmis:objectId'><cmis:value>" //
         + id //
         + "</cmis:value></cmis:propertyId>" //
         + "</cmis:properties></cmisra:object></entry>";

      String requestURI =
         "http://localhost:8080/rest/cmisatom/" + cmisRepositoryId + "/children/" + rootFolderId + "?sourceFolderId="
            + testFolderId;
      assertEquals(testFolderId, getParents(id).get(0).getObject().getObjectInfo().getId());
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();

      ContainerResponse resp = service("POST", requestURI, "http://localhost:8080/rest", null, s.getBytes(), writer);

      //      printBody(writer.getBody());

      assertEquals(201, resp.getStatus());

      assertNotNull(resp.getHttpHeaders().getFirst(HttpHeaders.LOCATION));
      assertEquals(rootFolderId, getParents(id).get(0).getObject().getObjectInfo().getId());

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

      org.w3c.dom.Node entry = getNode("atom:entry", xmlDoc);
      validateObjectEntry(entry, "cmis:document");
   }

   public void testSetContent() throws Exception
   {
      ContentStream content = new BaseContentStream("to be or not to be".getBytes(), "file", "text/plain");
      String docId = createDocument(testFolderId, "doc1", null, content);

      String requestURI = "http://localhost:8080/rest/cmisatom/" + cmisRepositoryId + "/file/" + docId;
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      headers.putSingle(HttpHeaders.CONTENT_TYPE, "text/plain");
      ContainerResponse resp = service("PUT", requestURI, "http://localhost:8080/rest", headers, "to be".getBytes());
      assertEquals(201, resp.getStatus());
      byte[] b = new byte[128];
      ContentStream docStream = conn.getContentStream(docId, null, -1, -1);
      int r = docStream.getStream().read(b);
      assertEquals("to be", new String(b, 0, r));
   }

   public void testSetContentMultipart() throws Exception
   {
      String docId = createDocument(testFolderId, "doc1", null, null);

      String requestURI = "http://localhost:8080/rest/cmisatom/" + cmisRepositoryId + "/file/" + docId;
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      headers.putSingle("content-type", "multipart/form-data; boundary=abcdef");
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      PrintWriter w = new PrintWriter(out);
      w.write("--abcdef\r\n" //
         + "Content-Disposition: form-data; name=\"test-file\"; filename=\"test.txt\"\r\n" //
         + "Content-Type: text/plain\r\n" //
         + "\r\n" //
         + "to be or not to be" //
         + "\r\n" //
         + "--abcdef--\r\n");
      w.flush();
      byte[] data = out.toByteArray();

      //            printBody(data);
      ContainerResponse resp = service("POST", requestURI, "http://localhost:8080/rest", headers, data);
      assertEquals(201, resp.getStatus());
      byte[] b = new byte[128];
      ContentStream content = conn.getContentStream(docId, null, -1, -1);
      assertEquals("text/plain", content.getMediaType());
      int r = content.getStream().read(b);
      assertEquals("to be or not to be", new String(b, 0, r));
   }

   public void testUpdateProperties() throws Exception
   {
      String s = "<?xml version='1.0' encoding='utf-8'?>" //
         + "<entry xmlns='http://www.w3.org/2005/Atom'" //
         + " xmlns:cmis='" + CmisConstants.CMIS_NS_URI + "'" //
         + " xmlns:cmisra='" + AtomCMIS.CMISRA_NS_URI + "'>" //
         + "<title>title</title><summary>summary</summary>" //
         + "<cmisra:object><cmis:properties>" //
         // Do not update anything , all properties are read-only.
         + "</cmis:properties></cmisra:object></entry>";
      String docId = createDocument(testFolderId, "doc1", null, null);

      String requestURI = "http://localhost:8080/rest/cmisatom/" + cmisRepositoryId + "/object/" + docId;
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      headers.putSingle(HttpHeaders.CONTENT_TYPE, "application/atom+xml;type=entry");
      ContainerResponse resp = service("PUT", requestURI, "http://localhost:8080/rest", headers, s.getBytes(), writer);

      //            printBody(writer.getBody());
      assertEquals(200, resp.getStatus());

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

      org.w3c.dom.Node entry = getNode("atom:entry", xmlDoc);

      validateObjectEntry(entry, "cmis:document");
   }

}
