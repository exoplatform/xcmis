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
import org.xcmis.core.EnumRelationshipDirection;
import org.xcmis.messaging.CmisExtensionType;
import org.xcmis.messaging.CmisObjectListType;
import org.xcmis.soap.RelationshipServicePort;
import org.xcmis.wssoap.impl.RelationshipServicePortImpl;

import java.util.ArrayList;
import java.util.List;

public class RelationshipServiceTest extends BaseTest
{

   /** Port. */
   private RelationshipServicePort port;

   /** Service name. */
   private final static String SERVICE_NAME = "RelationshipService";

   /** Address. */
   private final static String SERVICE_ADDRESS = "http://localhost:8081/" + SERVICE_NAME;

   /** Server. */
   private Server server;

   public void setUp() throws Exception
   {
      super.setUp();
      server =
         complexDeployService(SERVICE_ADDRESS, new RelationshipServicePortImpl(storageProvider), null, null, true);
      port = getRelationshipService(SERVICE_ADDRESS);
      assertNotNull(server);
      assertNotNull(port);
   }

   public void testGetObjectRelationships() throws Exception
   {
      String source = createDocument(testFolderId, "source1");
      String target = createDocument(testFolderId, "target1");
      String relationship1 = createRelationship(source, target);
      String relationship2 = createRelationship(target, source);
      CmisObjectListType resp = port.getObjectRelationships(//
         repositoryId, //
         source, //
         true, // Include sub-relationships types
         EnumRelationshipDirection.SOURCE, //
         null, // Relationship type
         null, // Property filter
         false, // Allowable actions
         null, // Max items
         null, // Skip count
         new CmisExtensionType() // Extension
         );
      assertEquals(1, resp.getObjects().size());
      assertEquals(relationship1, getObjectId(resp.getObjects().get(0)));
      resp = port.getObjectRelationships(//
         repositoryId, //
         source, //
         true, // Include sub-relationships types
         EnumRelationshipDirection.EITHER, //
         null, // Relationship type
         null, // Property filter
         false, // Allowable actions
         null, // Max items
         null, // Skip count
         new CmisExtensionType() // Extension
         );
      assertEquals(2, resp.getObjects().size());
      List<String> ids = new ArrayList<String>(2);
      ids.add(getObjectId(resp.getObjects().get(0)));
      ids.add(getObjectId(resp.getObjects().get(1)));
      assertTrue(ids.contains(relationship1));
      assertTrue(ids.contains(relationship2));
      conn.deleteObject(relationship1, true);
      conn.deleteObject(relationship2, true);
   }

   private RelationshipServicePort getRelationshipService(String address)
   {
      try
      {
         org.apache.cxf.jaxws.JaxWsProxyFactoryBean client = new org.apache.cxf.jaxws.JaxWsProxyFactoryBean();
         client.setServiceClass(RelationshipServicePort.class);
         client.setAddress(address);
         Object obj = client.create();
         return (RelationshipServicePort)obj;
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
      }
      return null;
   }

   protected void tearDown() throws Exception
   {
      server.stop();
      super.tearDown();
   }
}
