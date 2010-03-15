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
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.spi.object.Entry;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com.ua">Alexey
 *         Zavizionov</a>
 * @version $Id: ParentsCollectionTest.java 2 2010-02-04 17:21:49Z andrew00x $ Aug 12, 2009
 */
public class ParentsCollectionTest extends BaseTest
{

   public void setUp() throws Exception
   {
      super.setUp();
   }

   /*   public void testGetFolderParent() throws Exception
      {
         Entry folder = createFolder(testFolderId, "folder1");
         String folderId = folder.getObjectId();

         String requestURI = "http://localhost:8080/rest" //
            + "/cmisatom/" //
            + cmisRepositoryId //
            + "/parents/" //
            + folderId;

         ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
         ContainerResponse resp = service("GET", requestURI, "http://localhost:8080/rest", null, null, writer);

         // printBody(writer.getBody());
         assertEquals(200, resp.getStatus());

         DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
         f.setNamespaceAware(true);
         org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

         org.w3c.dom.Node xmlFeed = getNode("atom:feed", xmlDoc);
         validateFeedCommons(xmlFeed);

         assertTrue(hasLink(AtomCMIS.LINK_SERVICE, xmlFeed));
         assertTrue(hasLink(AtomCMIS.LINK_SELF, xmlFeed));
         assertTrue(hasLink(AtomCMIS.LINK_VIA, xmlFeed));

         assertEquals("1", getStringElement("cmisra:numItems", xmlFeed));

         org.w3c.dom.Node xmlEntry = getNode("atom:entry", xmlFeed);
         validateObjectEntry(xmlEntry, "cmis:folder");

         assertEquals(testFolderName, getStringElement("cmisra:relativePathSegment", xmlEntry));
      }
   */
   
   public void testGetObjectParents() throws Exception
   {
      Entry doc = createDocument(testFolderId, "doc1", null, null);
      String docId = doc.getObjectId();

      String requestURI = "http://localhost:8080/rest" //
         + "/cmisatom/" //
         + cmisRepositoryId //
         + "/parents/" //
         + docId;

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

      assertEquals("1", getStringElement("cmisra:numItems", xmlFeed));

      org.w3c.dom.Node xmlEntry = getNode("atom:entry", xmlFeed);
      validateObjectEntry(xmlEntry, "cmis:folder");

      assertEquals(testFolderName, getStringElement("cmisra:relativePathSegment", xmlEntry));
   }
}
