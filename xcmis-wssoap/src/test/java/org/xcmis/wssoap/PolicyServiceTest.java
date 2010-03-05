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
import org.xcmis.messaging.CmisExtensionType;
import org.xcmis.soap.PolicyServicePort;
import org.xcmis.spi.object.CmisObject;
import org.xcmis.wssoap.impl.PolicyServicePortImpl;

import java.util.ArrayList;
import java.util.List;

public class PolicyServiceTest extends BaseTest
{
   private PolicyServicePort port;

   /** Service name. */
   private final static String SERVICE_NAME = "PolicyService";

   /** Address. */
   private final static String SERVICE_ADDRESS = "http://localhost:8081/" + SERVICE_NAME;

   /** Server. */
   private Server server;

   public void setUp() throws Exception
   {
      super.setUp();
      server = complexDeployService(SERVICE_ADDRESS, new PolicyServicePortImpl(policyService), null, null, true);
      port = getPolicyService(SERVICE_ADDRESS);
      assertNotNull(server);
      assertNotNull(port);
   }

   public void testApplyPolicy() throws Exception
   {
      String docId = createDocument(testFolderId, "doc1");
      String policyId = createPolicy(testFolderId, "policy1", "Test policy text");
      List<CmisObject> pol = policyService.getAppliedPolicies(repositoryId, docId, null, false);
      List<CmisObjectType> policies = new ArrayList<CmisObjectType>();
      for (CmisObject cmisObject : pol)
      {
         policies.add(cmisObject.toCmisObjectType());
      }
      assertEquals(0, policies.size());
      port.applyPolicy(repositoryId, policyId, docId, new CmisExtensionType());
      pol = policyService.getAppliedPolicies(repositoryId, docId, null, false);
      policies = new ArrayList<CmisObjectType>();
      for (CmisObject cmisObject : pol)
      {
         policies.add(cmisObject.toCmisObjectType());
      }
      assertEquals(1, policies.size());
      assertEquals(policyId, getObjectId(policies.get(0)));
   }

   public void testRemovePolicy() throws Exception
   {
      String docId = createDocument(testFolderId, "doc1");
      String policyId = createPolicy(testFolderId, "policy1", "Test policy text");
      policyService.applyPolicy(repositoryId, policyId, docId);
      List<CmisObject> pol = policyService.getAppliedPolicies(repositoryId, docId, null, false);
      List<CmisObjectType> policies = new ArrayList<CmisObjectType>();
      for (CmisObject cmisObject : pol)
      {
         policies.add(cmisObject.toCmisObjectType());
      }
      assertEquals(1, policies.size());
      port.removePolicy(repositoryId, policyId, docId, new CmisExtensionType());
      pol = policyService.getAppliedPolicies(repositoryId, docId, null, false);
      policies = new ArrayList<CmisObjectType>();
      for (CmisObject cmisObject : pol)
      {
         policies.add(cmisObject.toCmisObjectType());
      }
      assertEquals(0, policies.size());
   }

   public void testAppliedPolicies() throws Exception
   {
      String docId = createDocument(testFolderId, "doc1");
      String policy1Id = createPolicy(testFolderId, "policy1", "Test policy1 text");
      String policy2Id = createPolicy(testFolderId, "policy2", "Test policy2 text");
      String policy3Id = createPolicy(testFolderId, "policy3", "Test policy3 text");
      policyService.applyPolicy(repositoryId, policy1Id, docId);
      policyService.applyPolicy(repositoryId, policy2Id, docId);
      policyService.applyPolicy(repositoryId, policy3Id, docId);

      List<CmisObjectType> policies = port.getAppliedPolicies(repositoryId, docId, null, new CmisExtensionType());
      assertEquals(3, policies.size());
      List<String> ids = new ArrayList<String>();
      ids.add(getObjectId(policies.get(0)));
      ids.add(getObjectId(policies.get(1)));
      ids.add(getObjectId(policies.get(2)));
      assertTrue(ids.contains(policy1Id));
      assertTrue(ids.contains(policy2Id));
      assertTrue(ids.contains(policy3Id));
   }

   private PolicyServicePort getPolicyService(String address)
   {
      org.apache.cxf.jaxws.JaxWsProxyFactoryBean client = new org.apache.cxf.jaxws.JaxWsProxyFactoryBean();
      client.setServiceClass(PolicyServicePort.class);
      client.setAddress(address);
      Object obj = client.create();
      return (PolicyServicePort)obj;
   }

   protected void tearDown() throws Exception
   {
      server.stop();
      super.tearDown();
   }
}
