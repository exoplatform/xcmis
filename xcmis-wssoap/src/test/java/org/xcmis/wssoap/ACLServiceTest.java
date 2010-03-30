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
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.xcmis.core.CmisAccessControlEntryType;
import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.CmisAccessControlPrincipalType;
import org.xcmis.core.EnumACLPropagation;
import org.xcmis.core.EnumBasicPermissions;
import org.xcmis.messaging.CmisACLType;
import org.xcmis.messaging.CmisExtensionType;
import org.xcmis.soap.ACLServicePort;
import org.xcmis.spi.AccessControlPropagation;
import org.xcmis.wssoap.impl.ACLServicePortImpl;
import org.xcmis.wssoap.impl.TypeConverter;
import org.xcmis.wssoap.impl.server.IdentityInterceptor;

import java.util.ArrayList;
import java.util.List;

public class ACLServiceTest extends BaseTest
{
   private ACLServicePort port;

   /**
    * Service name.
    */
   private final static String SERVICE_NAME = "ACLService";

   /**
    * Address.
    */
   private final static String SERVICE_ADDRESS = "http://localhost:8081/" + SERVICE_NAME;

   /**
    * Server.
    */
   private Server server;

   public void setUp() throws Exception
   {
      super.setUp();
      ArrayList<AbstractPhaseInterceptor<?>> in = new ArrayList<AbstractPhaseInterceptor<?>>();
      in.add(new IdentityInterceptor());
      server = complexDeployService(SERVICE_ADDRESS, new ACLServicePortImpl(storageProvider), in, null, true);
      port = getAccessControlService(SERVICE_ADDRESS);
      assertNotNull(server);
      assertNotNull(port);
   }

   public void testApplyACL() throws Exception
   {
      assertNotNull(port);
      String docId = createDocument(testFolderId, "doc");
      CmisAccessControlListType addACL = new CmisAccessControlListType();
      CmisAccessControlListType removeACL = new CmisAccessControlListType();

      CmisAccessControlEntryType entry1 = new CmisAccessControlEntryType();
      CmisAccessControlPrincipalType principal1 = new CmisAccessControlPrincipalType();
      principal1.setPrincipalId("Makis");
      entry1.setPrincipal(principal1);
      entry1.getPermission().add(EnumBasicPermissions.CMIS_WRITE.value());

      CmisAccessControlEntryType entry2 = new CmisAccessControlEntryType();
      CmisAccessControlPrincipalType principal2 = new CmisAccessControlPrincipalType();
      principal2.setPrincipalId("root");
      entry2.setPrincipal(principal2);
      entry2.getPermission().add(EnumBasicPermissions.CMIS_READ.value());
      entry2.getPermission().add(EnumBasicPermissions.CMIS_WRITE.value());

      addACL.getPermission().add(entry1);
      addACL.getPermission().add(entry2);

      port.applyACL(repositoryId, //
         docId, //
         addACL, //
         removeACL, //
         EnumACLPropagation.REPOSITORYDETERMINED, //
         new CmisExtensionType());

      CmisAccessControlListType acl = TypeConverter.getCmisAccessControlListType(conn.getACL(docId, true));
      assertNotNull(acl.getPermission());
      for (CmisAccessControlEntryType ace : acl.getPermission())
      {
         if ("Makis".equals(ace.getPrincipal().getPrincipalId()))
         {
            assertEquals(1, ace.getPermission().size());
            assertEquals(EnumBasicPermissions.CMIS_WRITE.value(), ace.getPermission().get(0));
         }
         else if ("root".equals(ace.getPrincipal().getPrincipalId()))
         {
            if (1 == ace.getPermission().size())
            {
               assertEquals(EnumBasicPermissions.CMIS_ALL.value(), ace.getPermission().get(0));
            }
            else if (2 == ace.getPermission().size())
            {
               List<String> expected = new ArrayList<String>();
               expected.add(EnumBasicPermissions.CMIS_READ.value());
               expected.add(EnumBasicPermissions.CMIS_WRITE.value());
               assertEquals(true, ace.getPermission().containsAll(expected));
            }
            else
            {
               fail("Unexpected permissions set: " + ace.getPermission());
            }
         }
         else
         {
            fail("Unknown principal: " + ace.getPrincipal().getPrincipalId());
         }
      }
   }

   public void testGetACL() throws Exception
   {
      assertNotNull(port);
      String docId = createDocument(testFolderId, "doc");
      CmisAccessControlListType addACL = new CmisAccessControlListType();
      CmisAccessControlListType removeACL = new CmisAccessControlListType();
      CmisAccessControlEntryType entry = new CmisAccessControlEntryType();
      CmisAccessControlPrincipalType principal = new CmisAccessControlPrincipalType();
      principal.setPrincipalId("Makis");
      entry.setPrincipal(principal);
      entry.getPermission().add(EnumBasicPermissions.CMIS_WRITE.value());
      addACL.getPermission().add(entry);
      CmisAccessControlEntryType entry1 = new CmisAccessControlEntryType();
      CmisAccessControlPrincipalType principal1 = new CmisAccessControlPrincipalType();
      principal1.setPrincipalId("root");
      entry1.setPrincipal(principal1);
      entry1.getPermission().add(EnumBasicPermissions.CMIS_READ.value());
      entry1.getPermission().add(EnumBasicPermissions.CMIS_WRITE.value());
      addACL.getPermission().add(entry1);
      conn.applyACL(docId, TypeConverter.getCmisListAccessControlEntry(addACL), TypeConverter
         .getCmisListAccessControlEntry(removeACL), AccessControlPropagation.REPOSITORYDETERMINED);
      CmisACLType resp = port.getACL(repositoryId, docId, false, new CmisExtensionType());
      assertNotNull(resp);
      CmisAccessControlListType acl = resp.getACL();
      for (CmisAccessControlEntryType ace : acl.getPermission())
      {
         if ("root".equals(ace.getPrincipal().getPrincipalId()))
         {
            if (1 == ace.getPermission().size())
            {
               assertEquals(EnumBasicPermissions.CMIS_ALL.value(), ace.getPermission().get(0));
            }
            else if (2 == ace.getPermission().size())
            {
               List<String> expected = new ArrayList<String>();
               expected.add(EnumBasicPermissions.CMIS_READ.value());
               expected.add(EnumBasicPermissions.CMIS_WRITE.value());
               assertEquals(true, ace.getPermission().containsAll(expected));
            }
            else
            {
               fail("Unexpected permissions set: " + ace.getPermission());
            }
         }
         else if ("Makis".equals(ace.getPrincipal().getPrincipalId()))
         {
            assertEquals(1, ace.getPermission().size());
            assertEquals(EnumBasicPermissions.CMIS_WRITE.value(), ace.getPermission().get(0));
         }
         else
         {
            fail("Unknown principal: " + ace.getPrincipal().getPrincipalId());
         }
      }
   }

   private ACLServicePort getAccessControlService(String address) throws Exception
   {
      org.apache.cxf.jaxws.JaxWsProxyFactoryBean client = new org.apache.cxf.jaxws.JaxWsProxyFactoryBean();
      client.setServiceClass(ACLServicePort.class);
      client.setAddress(address);
      Object obj = client.create();
      return (ACLServicePort)obj;
   }

   protected void tearDown() throws Exception
   {
      server.stop();
      super.tearDown();
   }

}
