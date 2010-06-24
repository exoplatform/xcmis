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

import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import org.xcmis.spi.BaseContentStream;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.DocumentData;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.PolicyData;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.UnfileObject;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.model.impl.StringProperty;
import org.xcmis.spi.utils.MimeType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class ObjectTest extends BaseTest
{

   /**
    * createDocument() test suite;
    */

   /**
    * 2.2.4.1.1
    * The Content Stream that MUST be stored for the 
    * newly-created Document Object. The method of passing the contentStream 
    * to the server and the encoding mechanism will be specified by each specific binding. 
    */
   public void testCreateDocument_CheckContent() throws Exception
   {
      System.out.print("Running testCreateDocument_CheckContent....");
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

   /**
    * 2.2.4.1.1
    * The property values that MUST be applied to the newly-created Document Object.
    * @throws Exception
    */
   public void testCreateDocument_CheckProperties() throws Exception
   {
      System.out.print("Running testCreateDocument_CheckProperties....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
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

   /**
    * 2.2.4.1.1
    * A list of policy IDs that MUST be applied to the newly-created Document object. 
    * @throws Exception
    */
   public void testCreateDocument_ApplyPolicy() throws Exception
   {
      System.out.print("Running testCreateDocument_ApplyPolicy....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
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

   /**
    * 2.2.4.1.1
    *   A list of ACEs that MUST be added to the newly-created Document object, 
    *   either using the ACL from folderId if specified, or being applied if no folderId is specified. 
    * @throws Exception
    */
   /*
   public void testCreateDocument_ApplyACL() throws Exception
   {
      System.out.print("Running testCreateDocument_ApplyACL....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      AccessControlEntry acl = new AccessControlEntry();
      acl.setPrincipal("Makis");
      acl.getPermissions().add("cmis:read");
      ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
      addACL.add(acl);
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      try
      {

         String docId =
            getConnection().createDocument(testroot.getObjectId(), getPropsMap("cmis:document", "doc1"), cs, addACL,
               null, null, VersioningState.MAJOR);
         ObjectData res = getStorage().getObjectById(docId);
         for (AccessControlEntry one : res.getACL(false))
         {
            if (one.getPrincipal().equalsIgnoreCase("Makis"))
               assertEquals(1, one.getPermissions().size());
            assertTrue(one.getPermissions().contains("cmis:read"));
         }
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }

   }
    */
   /**
    * 2.2.4.1.3 • nameConstraintViolation:   
    * If the repository detects a violation with the given cmis:name property value, 
    * the repository MAY throw this exception or chose a name which does not conflict.  
    * @throws Exception
    */
   public void testCreateDocument_NameConstraintViolationException() throws Exception
   {
      System.out.print("Running testCreateDocument_NameConstraintViolationException....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      try
      {
         String docId =
            getConnection().createDocument(testroot.getObjectId(), getPropsMap("cmis:document", "doc1"), cs, null,
               null, null, VersioningState.MAJOR);
         doFail();
      }
      catch (NameConstraintViolationException ex)
      {
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   /**
    * 2.2.4.1.3
    * The Repository MUST throw this exception if the “contentStreamAllowed” attribute 
    * of the Object-Type definition specified by the cmis:objectTypeId property 
    * value is set to “not allowed” and a contentStream input parameter is provided.
    * @throws Exception
    */
   /*
   public void testCreateDocument_StreamNotSupportedException() throws Exception
   {
      System.out.print("Running testCreateDocument_StreamNotSupportedException....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      try
      {
         String docId =
            getConnection().createDocument(testroot.getObjectId(), getPropsMap("cmis:document", "doc1"), cs, null,
               null, null, VersioningState.MAJOR);
         doFail();
      }
      catch (StreamCorruptedException ex)
      {
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }
   */

   protected void tearDown()
   {
      try
      {
         FolderData testroot = (FolderData)getStorage().getObjectByPath("/testroot");
         getStorage().deleteTree(testroot, true, UnfileObject.DELETE, true);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

   }
}
