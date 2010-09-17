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

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
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
         validateWorkspaceElement(workspaces.item(i), true);
      }
   }

   public void testGetRepositoryInfo() throws Exception
   {
      String requestURI = "http://localhost:8080/rest" + "/cmisatom/" + cmisRepositoryId;
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse resp = service("GET", requestURI, "http://localhost:8080/rest", null, null, writer);

      //            printBody(writer.getBody());

      assertEquals(200, resp.getStatus());

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

      org.w3c.dom.Node workspace = getNode("/app:service/app:workspace", xmlDoc);
      assertEquals(cmisRepositoryId, getStringElement("cmisra:repositoryInfo/cmis:repositoryId", workspace));
      validateWorkspaceElement(workspace, false);
   }

   public void testCapability() throws Exception
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

      org.w3c.dom.Node capabilitiesNode = getNode(capabilities, xmlDoc);

      assertTrue("Not found xml element " + "cmis:capabilityACL", hasElementValue("cmis:capabilityACL",
         capabilitiesNode));
      assertTrue("Not found xml element " + "cmis:capabilityAllVersionsSearchable", hasElementValue(
         "cmis:capabilityAllVersionsSearchable", capabilitiesNode));
      assertTrue("Not found xml element " + "cmis:capabilityChanges", hasElementValue("cmis:capabilityChanges",
         capabilitiesNode));
      assertTrue("Not found xml element " + "cmis:capabilityContentStreamUpdatability", hasElementValue(
         "cmis:capabilityContentStreamUpdatability", capabilitiesNode));
      assertTrue("Not found xml element " + "cmis:capabilityGetDescendants", hasElementValue(
         "cmis:capabilityGetDescendants", capabilitiesNode));
      assertTrue("Not found xml element " + "cmis:capabilityGetFolderTree", hasElementValue(
         "cmis:capabilityGetFolderTree", capabilitiesNode));
      assertTrue("Not found xml element " + "cmis:capabilityMultifiling", hasElementValue("cmis:capabilityMultifiling",
         capabilitiesNode));
      assertTrue("Not found xml element " + "cmis:capabilityPWCSearchable", hasElementValue(
         "cmis:capabilityPWCSearchable", capabilitiesNode));
      assertTrue("Not found xml element " + "cmis:capabilityPWCUpdateable", hasElementValue(
         "cmis:capabilityPWCUpdateable", capabilitiesNode));
      assertTrue("Not found xml element " + "cmis:capabilityQuery", hasElementValue("cmis:capabilityQuery",
         capabilitiesNode));
      assertTrue("Not found xml element " + "cmis:capabilityRenditions", hasElementValue("cmis:capabilityRenditions",
         capabilitiesNode));
      assertTrue("Not found xml element " + "cmis:capabilityUnfiling", hasElementValue("cmis:capabilityUnfiling",
         capabilitiesNode));
      assertTrue("Not found xml element " + "cmis:capabilityVersionSpecificFiling", hasElementValue(
         "cmis:capabilityVersionSpecificFiling", capabilitiesNode));
      assertTrue("Not found xml element " + "cmis:capabilityJoin", hasElementValue("cmis:capabilityJoin",
         capabilitiesNode));

   }

   private void validateWorkspaceElement(org.w3c.dom.Node workspace, boolean isShortInfo) throws Exception
   {
      assertTrue("Not found xml element " + "atom:title", hasElementValue("atom:title", workspace));
      assertTrue("Not found xml element " + "cmisra:repositoryInfo",
         hasElementValue("cmisra:repositoryInfo", workspace));
      assertTrue("Not found xml element " + "cmisra:repositoryInfo/cmis:repositoryId", hasElementValue(
         "cmisra:repositoryInfo/cmis:repositoryId", workspace));
      assertTrue("Not found xml element " + "cmisra:repositoryInfo/cmis:repositoryName", hasElementValue(
         "cmisra:repositoryInfo/cmis:repositoryName", workspace));

      if (!isShortInfo)
      {
         assertTrue("Not found xml element " + "cmisra:repositoryInfo/cmis:cmisVersionSupported", hasElementValue(
            "cmisra:repositoryInfo/cmis:cmisVersionSupported", workspace));
         assertTrue("Not found xml element " + "cmisra:repositoryInfo/cmis:capabilities", hasElementValue(
            "cmisra:repositoryInfo/cmis:capabilities", workspace));

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

}
