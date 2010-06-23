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
package org.xcmis.sp.tck.exo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.xcmis.spi.BaseContentStream;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.PolicyData;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.model.impl.StringProperty;
import org.xcmis.spi.utils.MimeType;

import static org.junit.Assert.*;


public class ObjectTest extends BaseTest
{

   /**
    * createDocument() test suite;
    * 
    */

   public void testCreateDocumentCheckContent() throws Exception
   {
      System.out.print("Running testCreateDocumentCheckContent....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      byte[] before = new byte[15];
      before = "1234567890aBcDE".getBytes();
      ContentStream cs = new BaseContentStream(before, null, new MimeType("text", "plain"));
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      try
      {
         String docId =
            getConnection().createDocument(testroot.getObjectId(), getPropsMap("cmis:document", "doc1"), cs, null,
               null, null, VersioningState.MAJOR);
         ContentStream c = getStorage().getObjectById(docId).getContentStream(null);
         assertEquals(cs.getMediaType(), c.getMediaType());

         byte[] after = new byte[15];
         c.getStream().read(after);
         assertArrayEquals(before, after);
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }

   }

   public void testCreateDocumentCheckProperties() throws Exception
   {
      System.out.print("Running testCreateDocumentCheckProperties....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      byte[] before = new byte[15];
      before = "1234567890aBcDE".getBytes();
      ContentStream cs = new BaseContentStream(before, null, new MimeType("text", "plain"));
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      try
      {
         String docId =
            getConnection().createDocument(testroot.getObjectId(), getPropsMap("cmis:document", "doc1"), cs, null,
               null, null, VersioningState.MAJOR);
         ObjectData res = getStorage().getObjectById(docId);
         assertNotNull(res.getProperty("cmis:name"));
         assertEquals("doc1", (String)res.getProperty("cmis:name").getValues().get(0)); //TODO: test more properties
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }

   }

   public void testCreateDocumentApplyPolicy() throws Exception
   {
      System.out.print("Running testCreateDocumentApplyPolicy....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      byte[] before = new byte[15];
      before = "1234567890aBcDE".getBytes();
      ContentStream cs = new BaseContentStream(before, null, new MimeType("text", "plain"));
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      org.xcmis.spi.model.PropertyDefinition<?> def =
         PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);
      Map<String, Property<?>> properties = getPropsMap("cmis:policy", "policy1");
      properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def.getId(), def.getQueryName(), def.getLocalName(),
         def.getDisplayName(), "testPolicyText"));

      PolicyData policy = getStorage().createPolicy(testroot, policyTypeDefinition, properties, null, null);

      ArrayList<String> policies = new ArrayList<String>();
      policies.add(policy.getObjectId());
      try
      {
         String docId =
            getConnection().createDocument(testroot.getObjectId(), getPropsMap("cmis:document", "doc1"), cs, null,
               null, policies, VersioningState.MAJOR);
         ObjectData res = getStorage().getObjectById(docId);
         assertEquals(1, res.getPolicies().size());
         Iterator<PolicyData> it = res.getPolicies().iterator();
         while (it.hasNext())
         {
            PolicyData one = it.next();
            assertEquals("policy1", one.getName());
            assertEquals("testPolicyText", one.getPolicyText());
         }
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }

   }

   @Override
   public void tearDown() throws Exception
   {
      clear();
   }
}
