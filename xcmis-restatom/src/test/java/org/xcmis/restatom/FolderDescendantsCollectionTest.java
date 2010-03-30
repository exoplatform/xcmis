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
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ObjectNotFoundException;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com.ua">Alexey
 *         Zavizionov</a>
 * @version $Id: FolderDescendantsCollectionTest.java 2 2010-02-04 17:21:49Z andrew00x $ Jul 28, 2009
 */
public class FolderDescendantsCollectionTest extends BaseTest
{

   public void testDeleteTree() throws Exception
   {
      String folder1 = createFolder(testFolderId, "folder1");
      String folder2 = createFolder(folder1, "folder2");
      String folder3 = createFolder(folder1, "folder3");
      String folder4 = createFolder(folder1, "folder4");
      String folder5 = createFolder(folder2, "folder5");
      String folder6 = createFolder(folder2, "folder6");
      String folder7 = createFolder(folder5, "folder7");

      createDocument(folder3, "doc3", null, null);
      createDocument(folder5, "doc5", null, null);
      createDocument(folder6, "doc6", null, null);
      createDocument(folder7, "doc7", null, null);
      /**
       * <pre>
       * ...
       *     folder1
       *     |_folder2
       *     | |_folder5
       *     | | |_folder7
       *     | | | |_doc7
       *     | | |_doc5  
       *     | |_folder6
       *     |    |_doc6
       *     |_folder3
       *     | |_doc3
       *     |_folder4
       * </pre>
       */

      String folderId = folder1;
      String requestURI = "http://localhost:8080/rest" //
         + "/cmisatom/" //
         + cmisRepositoryId //
         + "/descendants/" //
         + folderId;

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse resp = service("DELETE", requestURI, "http://localhost:8080/rest", null, null, writer);

      assertEquals(204, resp.getStatus());
      try
      {
         conn.getProperties(folder1, true, CMIS.WILDCARD);
         fail("Folder tree should be removed.");
      }
      catch (ObjectNotFoundException onf)
      {
         // ok
      }
   }

   public void testGetDescendants() throws Exception
   {
      String parent = testFolderId;
      List<String> items = new ArrayList<String>(5);
      for (int i = 0; i < 5; i++)
      {
         items.add(parent);
         parent = createFolder(parent, "folder" + i);
      }

      Map<String, List<String>> expected = new HashMap<String, List<String>>();
      expected.put(items.get(1), Arrays.asList(items.get(2)));
      expected.put(items.get(2), Arrays.asList(items.get(3)));
      expected.put(items.get(3), Arrays.asList(items.get(4)));

      int depth = 4; // 4 level deep.
      String folderId = testFolderId;
      String requestURI = "http://localhost:8080/rest" //
         + "/cmisatom/" //
         + cmisRepositoryId //
         + "/descendants/" //
         + folderId //
         + "?depth=" //
         + depth;

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
      assertTrue(hasLink(AtomCMIS.LINK_DOWN, xmlFeed));
      assertTrue(hasLink(AtomCMIS.LINK_CMIS_FOLDERTREE, xmlFeed));
      assertTrue(hasLink(AtomCMIS.LINK_UP, xmlFeed));

      org.w3c.dom.NodeList entries = getNodeSet("atom:feed/atom:entry", xmlFeed);
      int length = entries.getLength();
      for (int i = 0; i < length; i++)
      {
         org.w3c.dom.Node entry = entries.item(i);
         checkTree(entry, expected);
      }
   }
}
