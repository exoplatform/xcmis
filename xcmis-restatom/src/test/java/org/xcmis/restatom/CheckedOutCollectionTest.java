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
import org.w3c.dom.NodeList;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.spi.object.Entry;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CheckedOutCollectionTest.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class CheckedOutCollectionTest extends BaseTest
{

   public void testCheckOut() throws Exception
   {
      Entry doc = createDocument(testFolderId, "doc1", null, null);
      String docId = doc.getObjectId();

      assertNull("Should be no checkedout document.", doc.getCheckedOutId());
      String requestURI = "http://localhost:8080/rest/cmisatom/" + cmisRepositoryId + "/checkedout/" + docId;
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse response = service("POST", requestURI, "http://localhost:8080/rest", null, null, writer);

      //      printBody(writer.getBody());
      assertEquals(201, response.getStatus());

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

      org.w3c.dom.Node entry = getNode("atom:entry", xmlDoc);
      validateObjectEntry(entry, "cmis:document");
      assertNotNull("Object must be checkedout.", doc.getCheckedOutId());
   }

   public void testGetCheckedOut() throws Exception
   {
      Entry doc1 = createDocument(testFolderId, "doc1", null, null);
      String doc1Id = doc1.getObjectId();
      String pwc1 = getObjectId(conn.checkout(doc1Id));

      Entry doc2 = createDocument(testFolderId, "doc2", null, null);
      String doc2Id = doc2.getObjectId();
      String pwc2 = getObjectId(conn.checkout(doc2Id));

      String requestURI = "http://localhost:8080/rest/cmisatom/" + cmisRepositoryId + "/checkedout/";
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse response = service("GET", requestURI, "http://localhost:8080/rest", null, null, writer);

      //      printBody(writer.getBody());
      assertEquals(200, response.getStatus());

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

      org.w3c.dom.Node xmlFeed = getNode("atom:feed", xmlDoc);
      validateFeedCommons(xmlFeed);

      assertTrue(hasLink(AtomCMIS.LINK_SERVICE, xmlFeed));
      assertTrue(hasLink(AtomCMIS.LINK_FIRST, xmlFeed));

      assertEquals(2, Integer.parseInt(getStringElement("cmisra:numItems", xmlFeed)));

      NodeList entries = getNodeSet("atom:entry", xmlFeed);
      int length = entries.getLength();
      assertEquals(2, length);
      List<String> checkedOut = new ArrayList<String>();
      for (int i = 0; i < length; i++)
      {
         org.w3c.dom.Node n = entries.item(i);
         validateObjectEntry(n, "cmis:document");
         checkedOut.add(getNodeValueWithNodeProperty("cmisra:object/cmis:properties", "cmis:propertyId",
            "cmis:objectId", n));
      }

      assertEquals(2, checkedOut.size());
      assertTrue(checkedOut.contains(pwc1));
      assertTrue(checkedOut.contains(pwc2));
   }

}
