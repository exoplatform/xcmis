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

import org.xcmis.core.CmisPropertyDefinitionType;
import org.xcmis.core.CmisPropertyStringDefinitionType;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.CmisTypeDocumentDefinitionType;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumCardinality;
import org.xcmis.core.EnumContentStreamAllowed;
import org.xcmis.core.EnumPropertyType;
import org.xcmis.core.EnumUpdatability;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.tools.ByteArrayContainerResponseWriter;
import org.w3c.dom.NodeList;
import org.xcmis.restatom.AtomCMIS;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.TypeNotFoundException;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class TypesChildrenCollectionTest extends BaseTest
{

   private CmisTypeDocumentDefinitionType article;

   public void setUp() throws Exception
   {
      super.setUp();
      //cmis:article
      article = new CmisTypeDocumentDefinitionType();
      article.setBaseId(EnumBaseObjectTypeIds.CMIS_DOCUMENT);
      article.setControllableACL(false);
      article.setControllablePolicy(false);
      article.setCreatable(true);
      article.setDescription("addition type test");
      article.setDisplayName("cmis:article");
      article.setFileable(true);
      article.setFulltextIndexed(false);
      article.setId("cmis:article");
      article.setIncludedInSupertypeQuery(false);
      article.setLocalName("cmis:article");
      article.setParentId("cmis:document");
      article.setQueryable(false);
      article.setQueryName("cmis:article");
      article.setContentStreamAllowed(EnumContentStreamAllowed.ALLOWED);
      article.setVersionable(false);

      CmisPropertyStringDefinitionType pd = new CmisPropertyStringDefinitionType();
      pd.setCardinality(EnumCardinality.SINGLE);
      pd.setUpdatability(EnumUpdatability.READWRITE);
      pd.setDisplayName("cmis:hello");
      pd.setId("cmis:hello");
      pd.setInherited(false);
      pd.setPropertyType(EnumPropertyType.STRING);
      article.getPropertyDefinition().add(pd);

      repository.addType(article);
   }

   @Override
   public void tearDown() throws Exception
   {
      try
      {
         repository.removeType(article.getId());
      }
      catch (TypeNotFoundException ignored)
      {
      }
      super.tearDown();
   }

   public void testGetAllTypes() throws Exception
   {
      String requestURI = "http://localhost:8080/rest" //
         + "/cmisatom/" //
         + cmisRepositoryId //
         + "/types";

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse resp = service("GET", requestURI, "http://localhost:8080/rest", null, null, writer);
      assertEquals(200, resp.getStatus());

      //                printBody(writer.getBody());

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

      org.w3c.dom.Node xmlFeed = getNode("atom:feed", xmlDoc);
      validateFeedCommons(xmlFeed);

      NodeList entries = getNodeSet("atom:entry", xmlFeed);
      int length = entries.getLength();
      assertEquals(4, length);
      for (int i = 0; i < length; i++)
      {
         org.w3c.dom.Node xmlEntry = entries.item(i);
         validateTypeEntry(xmlEntry);
      }
   }

   public void testGetTypes() throws Exception
   {
      String requestURI = "http://localhost:8080/rest" //
         + "/cmisatom/" //
         + cmisRepositoryId //
         + "/types/" //
         + "cmis:document" //
         + "?includePropertyDefinitions=true";

      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
      ContainerResponse resp = service("GET", requestURI, "http://localhost:8080/rest", null, null, writer);

      //      printBody(writer.getBody());
      assertEquals(200, resp.getStatus());

      DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
      f.setNamespaceAware(true);
      org.w3c.dom.Document xmlDoc = f.newDocumentBuilder().parse(new ByteArrayInputStream(writer.getBody()));

      org.w3c.dom.Node xmlFeed = getNode("atom:feed", xmlDoc);
      validateFeedCommons(xmlFeed);

      org.w3c.dom.Node xmlEntry = getNode("atom:entry", xmlFeed);
      assertEquals("cmis:article", getStringElement("atom:id", xmlEntry));

      validateTypeEntry(xmlEntry);
      assertTrue(hasLink(AtomCMIS.LINK_UP, xmlEntry));
   }

   public void testAddType() throws Exception
   {
      String req = "<?xml version='1.0' encoding='utf-8'?>" // 
         + "<entry xmlns='http://www.w3.org/2005/Atom'" //
         + " xmlns:cmis='" + CMIS.CMIS_NS_URI + "'" //
         + " xmlns:cmisra='" + AtomCMIS.CMISRA_NS_URI + "'>" + "<id>cmis:folder1</id>"//
         + "<cmisra:type xmlns:cmis=\"http://docs.oasis-open.org/ns/cmis/core/200908/\">"//
         + "<cmis:id>cmis:folder1</cmis:id>"//
         + "<cmis:baseId>cmis:folder</cmis:baseId>"//
         + "<cmis:parentId>cmis:folder</cmis:parentId>"//
         + "<cmis:propertyIdDefinition>" //
         + "<cmis:id>cmis:newProperty</cmis:id>" //
         + "<cmis:propertyType>id</cmis:propertyType>" //
         + "<cmis:cardinality>single</cmis:cardinality>" //
         + "<cmis:updatability>readonly</cmis:updatability>"//
         + "<cmis:queryName>cmis:newProperty</cmis:queryName>" //
         + "<cmis:localName>cmis:newProperty</cmis:localName>"//
         + "<cmis:displayName>cmis:newProperty</cmis:displayName>" //
         + "<cmis:inherited>false</cmis:inherited>"//
         + "<cmis:required>false</cmis:required>"//
         + "<cmis:queryable>false</cmis:queryable>"//
         + "<cmis:orderable>false</cmis:orderable>"//
         + "</cmis:propertyIdDefinition>"//
         + "</cmisra:type>"//
         + "</entry>";

      try
      {
         repository.getTypeDefinition("cmis:folder1");
         fail();
      }
      catch (TypeNotFoundException e)
      {
         // OK
      }

      String requestURI = "http://localhost:8080/rest/cmisatom/" + cmisRepositoryId + "/types";
      ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();

      ContainerResponse resp = service("POST", requestURI, "http://localhost:8080/rest", null, req.getBytes(), writer);
      assertEquals(201, resp.getStatus());

      CmisTypeDefinitionType type = null;
      try
      {
         type = repository.getTypeDefinition("cmis:folder1");
      }
      catch (TypeNotFoundException e)
      {
         fail("Type 'cmis:folder1' must be added.");
      }
      boolean propDef = false;
      for (CmisPropertyDefinitionType d : type.getPropertyDefinition())
         if (d.getId().equals("cmis:newProperty"))
            propDef = true;
      
      assertTrue("Property definition for newly created type not found.", propDef);
   }
}
