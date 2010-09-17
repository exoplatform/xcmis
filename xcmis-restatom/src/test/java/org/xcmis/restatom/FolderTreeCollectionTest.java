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

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com.ua">Alexey
 *         Zavizionov</a>
 * @version $Id: FolderTreeCollectionTest.java 2 2010-02-04 17:21:49Z andrew00x $ Jul 28, 2009
 */
import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.impl.MultivaluedMapImpl;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.xcmis.spi.model.IncludeRelationships;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;
import javax.xml.parsers.DocumentBuilderFactory;

public class FolderTreeCollectionTest extends BaseTest
{

   public void testGetTree() throws Exception
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
      int depth = 3;
      String requestURI = "http://localhost:8080/rest" //
         + "/cmisatom/" //
         + cmisRepositoryId //
         + "/foldertree/" //
         + folderId + "?depth=" + depth //
         + "&" + AtomCMIS.PARAM_INCLUDE_RELATIONSHIPS + "=" + IncludeRelationships.NONE.value() //
         + "&" + AtomCMIS.PARAM_INCLUDE_PATH_SEGMENT + "=" + "true";

      MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();

      ContainerResponse resp = service("GET", requestURI, "http://localhost:8080/rest", headers, null, writer);

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

      Map<String, List<String>> expected = new HashMap<String, List<String>>();
      expected.put(folder2, Arrays.asList(folder5, folder6));
      expected.put(folder3, Arrays.asList(new String[0]));
      expected.put(folder4, Arrays.asList(new String[0]));
      expected.put(folder5, Arrays.asList(folder7));
      expected.put(folder6, Arrays.asList(new String[0]));

      org.w3c.dom.NodeList entries = getNodeSet("atom:feed/atom:entry", xmlFeed);
      int length = entries.getLength();
      for (int i = 0; i < length; i++)
      {
         org.w3c.dom.Node entry = entries.item(i);
         checkTree(entry, expected);
      }
   }

}
