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
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.messaging.CmisExtensionType;
import org.xcmis.messaging.CmisObjectInFolderContainerType;
import org.xcmis.messaging.CmisObjectInFolderListType;
import org.xcmis.messaging.CmisObjectListType;
import org.xcmis.messaging.CmisObjectParentsType;
import org.xcmis.soap.NavigationServicePort;
import org.xcmis.wssoap.impl.NavigationServicePortImpl;

import java.math.BigInteger;
import java.util.List;

public class NavigationServiceTest extends BaseTest
{

   private NavigationServicePort port;

   /** Service name. */
   private final static String SERVICE_NAME = "NavigationService";

   /** Address. */
   private final static String SERVICE_ADDRESS = "http://localhost:8081/" + SERVICE_NAME;

   /** Server. */
   private Server server;

   public void setUp() throws Exception
   {
      super.setUp();
      server =
         complexDeployService(SERVICE_ADDRESS, new NavigationServicePortImpl(storageProvider), null, null, true);
      port = getNavigationService(SERVICE_ADDRESS);
      assertNotNull(server);
      assertNotNull(port);
   }

   public void testGetChilren() throws Exception
   {
      for (int i = 0; i < 3; i++)
         createDocument(testFolderId, "doc" + i);

      CmisObjectInFolderListType children = port.getChildren(//
         repositoryId, //
         testFolderId, //
         null, // Filter
         null, // OrderBy
         false, // Allowable action
         EnumIncludeRelationships.NONE, null, // Renditions
         true, // Path-segment
         null, // Max items
         null, // Skip count
         null // Extension
         );
      assertNotNull(children);
      assertEquals(3, children.getNumItems());
   }

   public void testGetFolderParent() throws Exception
   {
      String id = createFolder(testFolderId, "folder");
      CmisObjectType parent = port.getFolderParent(repositoryId, id, null, new CmisExtensionType());
      assertEquals(testFolderId, getObjectId(parent));
   }

   public void testGetDescendants() throws Exception
   {
      String id = testFolderId;
      for (int i = 0; i < 5; i++)
         id = createFolder(id, "folder" + i);

      List<CmisObjectInFolderContainerType> resp2 =
         port.getDescendants(repositoryId, testFolderId, BigInteger.valueOf(3), null, false,
            EnumIncludeRelationships.NONE, null, false, new CmisExtensionType());
      assertNotNull(resp2);
   }

   public void testGetObjectParents() throws Exception
   {
      String id = createDocument(testFolderId, "doc");
      List<CmisObjectParentsType> parents = port.getObjectParents(//
         repositoryId, //
         id, //
         null, // Property filter
         false, // Allowable actions
         EnumIncludeRelationships.NONE, // 
         null, // Rendition filter
         true, // Include relative path segments
         null // Extension
         );
      assertEquals(1, parents.size());
      assertEquals(testFolderId, getObjectId(parents.get(0).getObject()));
   }

   public void testGetCheckedoutDocs() throws Exception
   {
      String id = createDocument(testFolderId, "doc");
      conn.checkout(id);
      CmisObjectListType checkedout = port.getCheckedOutDocs(//
         repositoryId, //
         testFolderId, //
         null, // Property Filter
         null, // OrderBy
         false, // Allowable actions
         EnumIncludeRelationships.NONE, //
         null, // Rendition Filter
         null, // Max items
         null, // Skip count
         null // Extension
         );
      assertNotNull(checkedout);
      assertEquals(1, checkedout.getNumItems());
   }

   public void testGetFolderTree() throws Exception
   {
      String lev1 = createFolder(testFolderId, "folder1");
      String lev2 = createFolder(lev1, "folder2");
      List<CmisObjectInFolderContainerType> tree = port.getFolderTree(//
         repositoryId, //
         testFolderId, //
         BigInteger.valueOf(-1), // depth
         null, // Property Filter
         false, // Allowable actions
         EnumIncludeRelationships.NONE, //
         null, // Rendition filter
         true, // Include path-segments
         null // Extension
         );
      assertNotNull(tree);
      assertEquals(lev1, getObjectId(tree.get(0).getObjectInFolder().getObject()));
      assertEquals(1, tree.get(0).getChildren().size());
      assertEquals(lev2, getObjectId(tree.get(0).getChildren().get(0).getObjectInFolder().getObject()));
   }

   private NavigationServicePort getNavigationService(String address)
   {
      org.apache.cxf.jaxws.JaxWsProxyFactoryBean client = new org.apache.cxf.jaxws.JaxWsProxyFactoryBean();
      client.setServiceClass(NavigationServicePort.class);
      client.setAddress(address);
      Object obj = client.create();
      return (NavigationServicePort)obj;
   }

   protected void tearDown() throws Exception
   {
      server.stop();
      super.tearDown();
   }
}
