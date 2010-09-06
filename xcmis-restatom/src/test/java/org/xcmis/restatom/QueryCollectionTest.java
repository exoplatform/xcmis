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

import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.xcmis.restatom.abdera.QueryTypeElement;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.query.Query;

import java.io.ByteArrayInputStream;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: QueryCollectionTest.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class QueryCollectionTest extends BaseTest
{

   public void testQueryElement() throws Exception
   {
      String s = "<?xml version='1.0' encoding='utf-8'?>" //
         + "<cmis:query xmlns='http://www.w3.org/2005/Atom' xmlns:cmis='" + CmisConstants.CMIS_NS_URI + "'>" //
         + "<cmis:statement>SELECT * FROM Document</cmis:statement>" //
         + "<cmis:maxItems>10</cmis:maxItems>" //
         + "<cmis:skipCount>0</cmis:skipCount>" //
         + "<cmis:searchAllVersions>true</cmis:searchAllVersions>" //
         + "<cmis:includeAllowableActions>false</cmis:includeAllowableActions>" //
         + "</cmis:query>";

      Document<Element> doc = AbderaFactory.getInstance().getParser().parse(new ByteArrayInputStream(s.getBytes()));
      QueryTypeElement q = (QueryTypeElement)doc.getRoot();
      Query qt = q.getQuery();
      assertEquals("SELECT * FROM Document", qt.getStatement());
//      assertEquals(BigInteger.valueOf(10), qt.getMaxItems());
//      assertEquals(BigInteger.valueOf(0), qt.getSkipCount());
      assertTrue(qt.isSearchAllVersions());
//      assertFalse(qt.isIncludeAllowableActions());
   }

   /*   public void testQuery() throws Exception
      {
         createDocument(testFolderId, "doc1", null, null);
         createDocument(testFolderId, "doc2", null, null);
         createDocument(testFolderId, "doc3", null, null);
         String s = "<?xml version='1.0' encoding='utf-8'?>" //
            + "<cmis:query xmlns='http://www.w3.org/2005/Atom' xmlns:cmis='" + CMIS.CMIS_NS_URI + "'>" //
            + "<cmis:statement>SELECT * FROM nt:file</cmis:statement>" //
            + "<cmis:maxItems>10</cmis:maxItems>" //
            + "<cmis:skipCount>0</cmis:skipCount>" //
            + "<cmis:searchAllVersions>true</cmis:searchAllVersions>" //
            + "<cmis:includeAllowableActions>true</cmis:includeAllowableActions>" //
            + "</cmis:query>";
         String requestURI = "http://localhost:8080/rest/cmisatom/" + cmisRepositoryId + "/query";
         ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
         ContainerResponse resp = service("POST", requestURI, "http://localhost:8080/rest", null, s.getBytes(), writer);

         //      printBody(writer.getBody());
         assertEquals(200, resp.getStatus());

         DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
         f.setNamespaceAware(true);
         org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

         org.w3c.dom.Node xmlFeed = getNode("atom:feed", xmlDoc);
         validateFeedCommons(xmlFeed);

         assertTrue(hasLink(AtomCMIS.LINK_SERVICE, xmlFeed));

         NodeList entries = getNodeSet("atom:entry", xmlFeed);
         int length = entries.getLength();
         assertEquals(3, length);
         for (int i = 0; i < length; i++)
         {
            org.w3c.dom.Node n = entries.item(i);
            validateObjectEntry(n, "cmis:document");
            validateObjectEntry(n, "cmis:document");
            validateObjectEntry(n, "cmis:document");
         }
      }
   */
}
