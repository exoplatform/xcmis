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

package org.xcmis.wssoap;

import org.apache.cxf.endpoint.Server;
import org.xcmis.core.CmisPropertyStringDefinitionType;
import org.xcmis.core.CmisRepositoryInfoType;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.CmisTypeDocumentDefinitionType;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumCardinality;
import org.xcmis.core.EnumContentStreamAllowed;
import org.xcmis.core.EnumPropertyType;
import org.xcmis.core.EnumUpdatability;
import org.xcmis.messaging.CmisExtensionType;
import org.xcmis.messaging.CmisRepositoryEntryType;
import org.xcmis.messaging.CmisTypeContainer;
import org.xcmis.soap.RepositoryServicePort;
import org.xcmis.wssoap.impl.RepositoryServicePortImpl;
import org.xcmis.wssoap.impl.TypeConverter;

import java.math.BigInteger;
import java.util.List;

public class RepositoryServiceTest extends BaseTest
{

   private RepositoryServicePort port;

   /** Service name. */
   private final static String SERVICE_NAME = "RepositoryService";

   /** Address. */
   private final static String SERVICE_ADDRESS = "http://localhost:8081/" + SERVICE_NAME;

   /** Server. */
   private Server server;

   private CmisTypeDocumentDefinitionType article;

   public void setUp() throws Exception
   {
      super.setUp();
      server =
         complexDeployService(SERVICE_ADDRESS, new RepositoryServicePortImpl(storageProvider), null, null, true);
      port = getRepositoryService(SERVICE_ADDRESS);
      assertNotNull(server);
      assertNotNull(port);
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
      pd.setDisplayName("cmis:hello");
      pd.setId("cmis:hello");
      pd.setInherited(false);
      pd.setPropertyType(EnumPropertyType.STRING);
      pd.setUpdatability(EnumUpdatability.READWRITE);
      article.getPropertyDefinition().add(pd);
      
      conn.addType(TypeConverter.getTypeDefinition(article));
   }

   public void testGetRepositories() throws Exception
   {
      List<CmisRepositoryEntryType> l = port.getRepositories(new CmisExtensionType());
      assertEquals(1, l.size());
   }

   public void testGetRepositoryInfo() throws Exception
   {
      CmisRepositoryInfoType info = port.getRepositoryInfo(repositoryId, new CmisExtensionType());
      assertEquals(repositoryId, info.getRepositoryId());
   }

   public void testGetTypeDefinition() throws Exception
   {
      CmisTypeDefinitionType docType =
         port.getTypeDefinition(repositoryId, "cmis:document", new CmisExtensionType());
      assertNotNull(docType);
   }

   public void testGetTypeDescendants() throws Exception
   {
      List<CmisTypeContainer> typeDescendants =
         port.getTypeDescendants(repositoryId, "cmis:document", BigInteger.valueOf(1), true,
            new CmisExtensionType());
      assertEquals(1, typeDescendants.size());
      CmisTypeContainer level1 = typeDescendants.get(0);
      assertEquals("cmis:article", level1.getType().getId());
      assertEquals(0, level1.getChildren().size()); // depth limited as 1.
   }

   private RepositoryServicePort getRepositoryService(String address)
   {
      org.apache.cxf.jaxws.JaxWsProxyFactoryBean client = new org.apache.cxf.jaxws.JaxWsProxyFactoryBean();
      client.setServiceClass(RepositoryServicePort.class);
      client.setAddress(address);
      Object obj = client.create();
      return (RepositoryServicePort)obj;
   }

   protected void tearDown() throws Exception
   {
      conn.removeType(article.getId());
      server.stop();
      super.tearDown();
   }
}
