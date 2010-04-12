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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com.ua">Alexey
 *         Zavizionov</a>
 * @version $Id: RepositoryServiceTest.java 2734 2009-08-19 15:42:18Z andrew00x
 *          $ Jul 20, 2009
 */
public class RepositoryServiceTest extends BaseTest
{

   public void testGetRepositories() throws Exception
   {
      String requestURI = "http://localhost:8080/rest/cmisatom";

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse resp = service("GET", requestURI, "http://localhost:8080/rest", null, null, writer);

      //      printBody(writer.getBody());
      assertEquals(200, resp.getStatus());

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

      NodeList workspaces = getNodeSet("/app:service/app:workspace", xmlDoc);
      int length = workspaces.getLength();
      assertEquals(1, length);
      for (int i = 0; i < length; i++)
      {
         validateWorkspaceElement(workspaces.item(i));
      }
   }

   public void testGetRepositoryInfo() throws Exception
   {
      String requestURI = "http://localhost:8080/rest" + "/cmisatom/" + cmisRepositoryId;
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse resp = service("GET", requestURI, "http://localhost:8080/rest", null, null, writer);

      //      printBody(writer.getBody());
      assertEquals(200, resp.getStatus());

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

      org.w3c.dom.Node workspace = getNode("/app:service/app:workspace", xmlDoc);
      assertEquals(cmisRepositoryId, getStringElement("cmisra:repositoryInfo/cmis:repositoryId", workspace));
      validateWorkspaceElement(workspace);
   }

   public void _testCapability() throws Exception
   {

      String requestURI = "http://localhost:8080/rest/cmisatom/" + cmisRepositoryId + "/";
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();

      ContainerResponse resp = service("GET", requestURI, "http://localhost:8080/rest", null, null, writer);

      //      printBody(writer.getBody());

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);

      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

      XPath xp = XPathFactory.newInstance().newXPath();
      xp.setNamespaceContext(new NamespaceResolver());

      String capabilities = "/app:service/app:workspace/cmisra:repositoryInfo/cmis:capabilities";

      String r = (String)xp.evaluate(capabilities + "/cmis:capabilityACL", xmlDoc, XPathConstants.STRING);
      assertEquals("manage", r);
      r = (String)xp.evaluate(capabilities + "/cmis:capabilityAllVersionsSearchable", xmlDoc, XPathConstants.STRING);
      assertEquals("true", r);
      r = (String)xp.evaluate(capabilities + "/cmis:capabilityChanges", xmlDoc, XPathConstants.STRING);
      assertEquals("none", r);
      r =
         (String)xp.evaluate(capabilities + "/cmis:capabilityContentStreamUpdatability", xmlDoc, XPathConstants.STRING);
      assertEquals("anytime", r);
      r = (String)xp.evaluate(capabilities + "/cmis:capabilityGetDescendants", xmlDoc, XPathConstants.STRING);
      assertEquals("true", r);
      r = (String)xp.evaluate(capabilities + "/cmis:capabilityGetFolderTree", xmlDoc, XPathConstants.STRING);
      assertEquals("true", r);
      r = (String)xp.evaluate(capabilities + "/cmis:capabilityMultifiling", xmlDoc, XPathConstants.STRING);
      assertEquals("true", r);
      r = (String)xp.evaluate(capabilities + "/cmis:capabilityPWCSearchable", xmlDoc, XPathConstants.STRING);
      assertEquals("true", r);
      r = (String)xp.evaluate(capabilities + "/cmis:capabilityPWCUpdateable", xmlDoc, XPathConstants.STRING);
      assertEquals("true", r);
      r = (String)xp.evaluate(capabilities + "/cmis:capabilityQuery", xmlDoc, XPathConstants.STRING);
      assertEquals("bothcombined", r);
      r = (String)xp.evaluate(capabilities + "/cmis:capabilityRenditions", xmlDoc, XPathConstants.STRING);
      assertEquals("read", r);
      r = (String)xp.evaluate(capabilities + "/cmis:capabilityUnfiling", xmlDoc, XPathConstants.STRING);
      assertEquals("true", r);
      r = (String)xp.evaluate(capabilities + "/cmis:capabilityVersionSpecificFiling", xmlDoc, XPathConstants.STRING);
      assertEquals("false", r);
      r = (String)xp.evaluate(capabilities + "/cmis:capabilityJoin", xmlDoc, XPathConstants.STRING);
      assertEquals("none", r);
   }

   private void validateWorkspaceElement(org.w3c.dom.Node workspace) throws Exception
   {
      assertTrue(hasElementValue("atom:title", workspace));
      assertTrue(hasElementValue("cmisra:repositoryInfo", workspace));
      assertTrue(hasElementValue("cmisra:repositoryInfo/cmis:repositoryId", workspace));
      assertTrue(hasElementValue("cmisra:repositoryInfo/cmis:cmisVersionSupported", workspace));
      assertTrue(hasElementValue("cmisra:repositoryInfo/cmis:capabilities", workspace));

      NodeList templates = getNodeSet("cmisra:uritemplate", workspace);
      int length = templates.getLength();
      List<String> list = new ArrayList<String>();
      for (int i = 0; i < length; i++)
      {
         org.w3c.dom.Node template = templates.item(i);
         list.add(getStringElement("cmisra:type", template));
      }
      assertTrue("URI Template 'objectbyid' not found", list.contains("objectbyid"));
      assertTrue("URI Template 'objectbypath' not found", list.contains("objectbypath"));
      assertTrue("URI Template 'typebyid' not found", list.contains("typebyid"));
   }

}
