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

import junit.framework.AssertionFailedError;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpressionException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class ChangesLogCollectionTest extends BaseTest
{

   public void testGetChangesLog() throws Exception
   {
      String document = createDocument(testFolderId, "testChangesLog01", null, null);
      String requestURI = "http://localhost:8080/rest/cmisatom/" + cmisRepositoryId + "/changes";
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

      NodeList entries = getNodeSet("atom:entry", xmlFeed);
      int length = entries.getLength();
      // Should be vent about creation of document
      org.w3c.dom.Node n = entries.item(length - 1);
      validateChangeEvent(n);
      assertEquals(document, getNodeValueWithNodeProperty("cmisra:object/cmis:properties", "cmis:propertyId",
         "cmis:objectId", n));
      assertEquals("created", getStringElement("cmisra:object/cmis:changeEventInfo/cmis:changeType", n));
   }

   private void validateChangeEvent(org.w3c.dom.Node xmlEntry) throws XPathExpressionException
   {
      String[] expected = new String[]{ //
         "atom:id", //
            "atom:updated", //
            "atom:author", //
            "atom:author/atom:name", //
            "atom:title" //
         };

      for (String el : expected)
      {
         try
         {
            assertTrue("Not found xml element '" + el + "'", hasElementValue(el, xmlEntry));
         }
         catch (AssertionFailedError e)
         {
            String elNew = el.substring("atom:".length());
            assertTrue("Not found xml element '" + elNew + "'", hasElementValue(elNew, xmlEntry));
         }
      }
   }

}
