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

package org.xcmis.sp.jcr.exo;

import org.exoplatform.services.jcr.access.AccessControlList;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.CredentialsImpl;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.xcmis.spi.AccessControlEntry;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.PropertyDefinition;
import org.xcmis.spi.Storage;
import org.xcmis.spi.TypeDefinition;
import org.xcmis.spi.UnfileObject;
import org.xcmis.spi.VersioningState;
import org.xcmis.spi.data.BaseContentStream;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.Document;
import org.xcmis.spi.data.Folder;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.data.Policy;
import org.xcmis.spi.data.Relationship;
import org.xcmis.spi.impl.AccessControlEntryImpl;
import org.xcmis.spi.object.Property;
import org.xcmis.spi.object.impl.StringProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.Session;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class StorageTest extends BaseTest
{

   protected Storage storage;

   protected Folder rootFolder;

   public void setUp() throws Exception
   {
      super.setUp();
      storage = storageProvider.getConnection(cmisRepositoryId, null).getStorage();
      rootFolder = new FolderImpl(storage.getTypeDefinition("cmis:folder", true), root);
   }

   public void testApplyACL() throws Exception
   {
      Document document = createDocument(rootFolder, "applyACLTestDocument", "cmis:document", null, null);
      AccessControlEntry ace =
         new AccessControlEntryImpl("root", new HashSet<String>(Arrays.asList("cmis:read", "cmis:write")));
      document.setACL(Arrays.asList(ace));
      storage.saveObject(document);
      //      document.save();

      Node documentNode = (Node)session.getItem("/applyACLTestDocument");
      AccessControlList acl = ((ExtendedNode)documentNode).getACL();

      List<String> permissions = acl.getPermissions("root");
      assertTrue(permissions.contains(PermissionType.READ));
      assertTrue(permissions.contains(PermissionType.REMOVE));
      assertTrue(permissions.contains(PermissionType.SET_PROPERTY));
      assertTrue(permissions.contains(PermissionType.ADD_NODE));

      System.out.println(document.getACL(false));
   }

   public void testApplyPolicy() throws Exception
   {
      Document document = createDocument(rootFolder, "applyPolicyTestDocument", "cmis:document", null, null);
      Policy policy = createPolicy(rootFolder, "applyPolicyTestPolicy01", "test apply policy", "cmis:policy");
      document.applyPolicy(policy);
      storage.saveObject(document);
      //document.save();

      Node documentNode = (Node)session.getItem("/applyPolicyTestDocument");
      assertTrue(documentNode.hasProperty(policy.getObjectId()));

      Collection<Policy> policies = document.getPolicies();
      assertEquals(1, policies.size());
      assertEquals(policy.getObjectId(), policies.iterator().next().getObjectId());
   }

   public void testCheckOut() throws Exception
   {
      //      Document document = createDocument(rootFolder, "checkoutTest", "cmis:document", null, null);
      //      document.checkout();
      //      for (NodeIterator i = root.getNodes(); i.hasNext();)
      //         System.out.println(">>>> "+i.nextNode().getName());
   }

   public void testChildren() throws Exception
   {
      Folder folder = createFolder(rootFolder, "folderChildrenTest", "cmis:folder");
      Set<String> source = new HashSet<String>();
      String name = "testChildren";
      for (int i = 1; i <= 20; i++)
      {
         Document document = createDocument(folder, name + i, "cmis:document", null, null);
         storage.saveObject(document);
         //document.save();
         source.add(document.getObjectId());
      }
      // Check children viewing with paging. It should be close to real usage.
      int maxItems = 5;
      for (int i = 0, skipCount = 0; i < 4; i++, skipCount += maxItems)
      {
         ItemsIterator<ObjectData> children = folder.getChildren(null);
         children.skip(skipCount);
         for (int count = 0; children.hasNext() && count < maxItems; count++)
         {
            ObjectData next = children.next();
            //            System.out.println(next.getName());
            source.remove(next.getObjectId());
         }
      }
      if (source.size() > 0)
      {
         StringBuilder sb = new StringBuilder();
         for (String s : source)
         {
            if (sb.length() > 0)
               sb.append(',');
            sb.append(s);
         }
         fail("Object(s) " + sb.toString() + " were not found in children list.");
      }
   }

   public void testCreateDocument() throws Exception
   {
      PropertyDefinition<?> def = PropertyDefinitions.getPropertyDefinition("cmis:document", CMIS.NAME);
      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CMIS.NAME, new StringProperty(def.getId(), def.getQueryName(), def.getLocalName(), def
         .getDisplayName(), "createDocumentTest"));

      ContentStream cs =
         new BaseContentStream("to be or not to be".getBytes(), /*"createDocumentTest"*/null, "text/plain");
      Document document = storage.createDocument(rootFolder, "cmis:document", VersioningState.MAJOR);
      document.setProperties(properties);
      //      document.setName("createDocumentTest");
      document.setContentStream(cs);
      AccessControlEntry ace =
         new AccessControlEntryImpl("root", new HashSet<String>(Arrays.asList("cmis:read", "cmis:write")));
      document.setACL(Arrays.asList(ace));
      storage.saveObject(document);
      //document.save();

      assertTrue(session.itemExists("/createDocumentTest"));
      Node documentNode = (Node)session.getItem("/createDocumentTest");

      // check content.
      assertEquals("nt:file", documentNode.getPrimaryNodeType().getName());
      assertEquals("to be or not to be", documentNode.getProperty("jcr:content/jcr:data").getString());
      assertEquals("text/plain", documentNode.getProperty("jcr:content/jcr:mimeType").getString());

      // check permissions
      List<String> permissions = ((ExtendedNode)documentNode).getACL().getPermissions("root");
      assertTrue(permissions.size() > 0); // ACL applied to back-end node.
      System.out.println("root: " + permissions);

      // CMIS properties
      assertEquals(true, document.isLatestVersion());
      assertEquals(true, document.isMajorVersion());
      assertEquals(true, document.isLatestMajorVersion());
      assertEquals("root", document.getCreatedBy());
      assertEquals("root", document.getLastModifiedBy());
      assertNotNull(document.getCreationDate());
      assertNotNull(document.getLastModificationDate());
      assertEquals(documentNode.getVersionHistory().getUUID(), document.getVersionSeriesId());
      assertNull(document.getVersionSeriesCheckedOutBy());
      assertNull(document.getVersionSeriesCheckedOutId());
      assertFalse(document.isVersionSeriesCheckedOut());
      assertEquals("latest", document.getVersionLabel());
      assertEquals("text/plain", document.getContentStreamMimeType());
      assertEquals("createDocumentTest", document.getContentStream().getFileName());
   }

   public void testCreateDocumentFromSource() throws Exception
   {
      ContentStream cs = new BaseContentStream("to be or not to be".getBytes(), null, "text/plain");
      Document document = createDocument(rootFolder, "createDocumentSource", "cmis:document", cs, null);

      Document documentCopy = storage.createCopyOfDocument(document, rootFolder, VersioningState.MINOR);
      documentCopy.setName("createDocumentSourceCopy");
      storage.saveObject(documentCopy);
      //      documentCopy.save();

      // Check is node and content copied.
      assertTrue(session.itemExists("/createDocumentSourceCopy"));
      Node documentNode = (Node)session.getItem("/createDocumentSourceCopy");
      assertEquals("nt:file", documentNode.getPrimaryNodeType().getName());
      assertEquals("to be or not to be", documentNode.getProperty("jcr:content/jcr:data").getString());
      assertEquals("text/plain", documentNode.getProperty("jcr:content/jcr:mimeType").getString());

      assertFalse("Copy must have different name.", document.getName().equals(documentCopy.getName()));
      assertFalse("Copy must have different ID.", document.getObjectId().equals(documentCopy.getObjectId()));
      assertFalse("Copy must have different versionSeriesId.", document.getVersionSeriesId().equals(
         documentCopy.getVersionSeriesId()));
      assertFalse(documentCopy.isMajorVersion());
   }

   public void testCreateFolder() throws Exception
   {
      PropertyDefinition<?> def = PropertyDefinitions.getPropertyDefinition("cmis:folder", CMIS.NAME);
      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CMIS.NAME, new StringProperty(def.getId(), def.getQueryName(), def.getLocalName(), def
         .getDisplayName(), "createFolderTest"));

      Folder newFolder = storage.createFolder(rootFolder, "cmis:folder");
      newFolder.setProperties(properties);
      storage.saveObject(newFolder);
      //      newFolder.save();

      assertTrue(session.itemExists("/createFolderTest"));
      Node folderNode = (Node)session.getItem("/createFolderTest");
      assertEquals("nt:folder", folderNode.getPrimaryNodeType().getName());
   }

   public void testCreatePolicy() throws Exception
   {
      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();

      PropertyDefinition<?> defName = PropertyDefinitions.getPropertyDefinition("cmis:policy", CMIS.NAME);
      properties.put(CMIS.NAME, new StringProperty(defName.getId(), defName.getQueryName(), defName.getLocalName(),
         defName.getDisplayName(), "createPolicyTest"));

      PropertyDefinition<?> defPolicyText = PropertyDefinitions.getPropertyDefinition("cmis:policy", CMIS.POLICY_TEXT);
      properties.put(CMIS.POLICY_TEXT, new StringProperty(defPolicyText.getId(), defPolicyText.getQueryName(),
         defPolicyText.getLocalName(), defPolicyText.getDisplayName(), "simple policy"));

      ObjectData policy = storage.createPolicy(rootFolder, "cmis:policy");
      policy.setProperties(properties);
      storage.saveObject(policy);
      //      policy.save();

      assertTrue(session.itemExists("/createPolicyTest"));
      Node policyNode = (Node)session.getItem("/createPolicyTest");

      assertEquals("cmis:policy", policyNode.getPrimaryNodeType().getName());
      assertEquals("simple policy", policyNode.getProperty("cmis:policyText").getString());
   }

   public void testCreateRelationship() throws Exception
   {
      ObjectData sourceDoc = createDocument(rootFolder, "createRelationshipSource", "cmis:document", null, null);
      ObjectData targetDoc = createDocument(rootFolder, "createRelationshipTarget", "cmis:document", null, null);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      PropertyDefinition<?> defName = PropertyDefinitions.getPropertyDefinition("cmis:relationship", CMIS.NAME);
      properties.put(CMIS.NAME, new StringProperty(defName.getId(), defName.getQueryName(), defName.getLocalName(),
         defName.getDisplayName(), "createRelationshipTest"));

      Relationship relationship = storage.createRelationship(sourceDoc, targetDoc, "cmis:relationship");
      relationship.setProperties(properties);
      storage.saveObject(relationship);
      //      relationship.save();

      assertTrue(root
         .hasNode("xcmis:system/xcmis:relationships/" + sourceDoc.getObjectId() + "/createRelationshipTest"));
      Node relationshipNode =
         (Node)session.getItem("/xcmis:system/xcmis:relationships/" + sourceDoc.getObjectId()
            + "/createRelationshipTest");
      assertEquals("cmis:relationship", relationshipNode.getPrimaryNodeType().getName());
      assertEquals(sourceDoc.getObjectId(), relationshipNode.getProperty("cmis:sourceId").getString());
      assertEquals(targetDoc.getObjectId(), relationshipNode.getProperty("cmis:targetId").getString());
   }

   public void testDeleteContent() throws Exception
   {
      ContentStream cs = new BaseContentStream("to be or not to be".getBytes(), null, "text/plain");
      Document document = createDocument(rootFolder, "removeContentTest", "cmis:document", cs, null);
      Node documentNode = (Node)session.getItem("/removeContentTest");
      assertEquals("to be or not to be", documentNode.getProperty("jcr:content/jcr:data").getString());
      assertEquals("text/plain", documentNode.getProperty("jcr:content/jcr:mimeType").getString());

      document.setContentStream(null);
      storage.saveObject(document);
      //document.save();

      documentNode = (Node)session.getItem("/removeContentTest");
      assertEquals("", documentNode.getProperty("jcr:content/jcr:data").getString());
      assertEquals("", documentNode.getProperty("jcr:content/jcr:mimeType").getString());
   }

   public void testDeleteDocument() throws Exception
   {
      Document document = createDocument(rootFolder, "deleteDocumentTest", "cmis:document", null, null);
      storage.deleteObject(document, true);
      assertFalse(session.itemExists("/deleteDocumentTest"));
   }

   public void testDeleteFolder() throws Exception
   {
      Folder folder = createFolder(rootFolder, "deleteFolderTest", "cmis:folder");
      storage.deleteObject(folder, true);
      assertFalse(session.itemExists("/deleteFolderTest"));
   }

   public void testDeleteFolderWithChildren() throws Exception
   {
      Folder folder = createFolder(rootFolder, "deleteFolderWithChildrenTest", "cmis:folder");
      Document document = createDocument(folder, "child1", "cmis:document", null, null);
      try
      {
         storage.deleteObject(folder, true);
         fail("ConstraintException should be thrown");
      }
      catch (ConstraintException e)
      {
         // OK
      }
      storage.deleteObject(document, true);
      // No children any more , should be able delete.
      storage.deleteObject(folder, true);
   }

   public void testDeleteObjectWithRelationship() throws Exception
   {
      ObjectData sourceDoc =
         createDocument(rootFolder, "deleteObjectWithRelationshipSource", "cmis:document", null, null);
      ObjectData targetDoc =
         createDocument(rootFolder, "deleteObjectWithRelationshipTarget", "cmis:document", null, null);

      Relationship relationship = storage.createRelationship(sourceDoc, targetDoc, "cmis:relationship");
      relationship.setName("relationship01");
      storage.saveObject(relationship);
      //      relationship.save();

      try
      {
         storage.deleteObject(targetDoc, true);
         fail("ConstraintException should be thrown");
      }
      catch (ConstraintException e)
      {
         // OK
         System.out.println(e.getMessage());
      }
   }

   public void testDeletePolicy() throws Exception
   {
      Document document = createDocument(rootFolder, "applyPolicyTestDocument", "cmis:document", null, null);
      Policy policy = createPolicy(rootFolder, "applyPolicyTestPolicy01", "test apply policy", "cmis:policy");
      document.applyPolicy(policy);
      storage.saveObject(document);
      //document.save();

      try
      {
         storage.deleteObject(policy, true);
      }
      catch (ConstraintException e)
      {
         // OK. Applied policy may not be deleted.
      }
      document.removePolicy(policy);
      storage.saveObject(document);
      //document.save();

      // Should be able delete now.
      storage.deleteObject(policy, true);
   }

   public void testDeleteRootFolder() throws Exception
   {
      try
      {
         storage.deleteObject(rootFolder, true);
         fail("ConstraintException should be thrown");
      }
      catch (ConstraintException e)
      {
         // OK
      }
   }

   public void testDeleteTree() throws Exception
   {
      // Create tree.
      Folder folder1 = createFolder(rootFolder, "1", "cmis:folder");
      Folder folder2 = createFolder(folder1, "2", "cmis:folder");
      String folder2Id = folder2.getObjectId();
      Folder folder3 = createFolder(folder2, "3", "cmis:folder");
      Folder folder4 = createFolder(folder3, "4", "cmis:folder");
      Folder folder5 = createFolder(folder1, "5", "cmis:folder");
      Folder folder6 = createFolder(folder5, "6", "cmis:folder");
      Folder folder7 = createFolder(folder3, "7", "cmis:folder");
      Document doc1 = createDocument(folder2, "doc1", "cmis:document", null, null);
      Document doc2 = createDocument(folder2, "doc2", "cmis:document", null, null);
      Document doc3 = createDocument(folder2, "doc3", "cmis:document", null, null);
      Document doc4 = createDocument(folder4, "doc4", "cmis:document", null, null);

      //      /
      //      |_ 1 
      //        |_2
      //        | |_doc1
      //        | |_doc2
      //        | |_doc3
      //        | |_3
      //        |   |_4
      //        |   | |_doc4
      //        |   |_7 
      //        |_5 
      //          |_6

      // Disable removing whole tree for user 'exo'
      ExtendedNode folder1Node = (ExtendedNode)((FolderImpl)folder1).getNode();
      folder1Node.addMixin("exo:privilegeable");
      folder1Node.setPermission("root", PermissionType.ALL);
      folder1Node.setPermission("exo", new String[]{PermissionType.READ});
      folder1Node.removePermission("any", PermissionType.SET_PROPERTY);
      folder1Node.removePermission("any", PermissionType.REMOVE);
      folder1Node.removePermission("any", PermissionType.ADD_NODE);
      folder1Node.removePermission("any", PermissionType.READ);
      session.save();

      // All object in this list must not be deleted.
      Set<String> expectedFailedDelete = new HashSet<String>();
      expectedFailedDelete.add(doc4.getObjectId());
      expectedFailedDelete.add(folder2Id);
      expectedFailedDelete.add(folder3.getObjectId());
      expectedFailedDelete.add(folder4.getObjectId());
      expectedFailedDelete.add(folder7.getObjectId());
      expectedFailedDelete.add(doc1.getObjectId());
      expectedFailedDelete.add(doc2.getObjectId());
      expectedFailedDelete.add(doc3.getObjectId());

      // Get storage for user 'exo'
      Storage _storage = storageProvider.getConnection(cmisRepositoryId, "exo", "exo").getStorage();
      Folder _folder2 = (Folder)_storage.getObject(folder2Id);
      Collection<String> failedDelete = _storage.deleteTree(_folder2, true, UnfileObject.DELETE, true);

      assertNotNull(failedDelete);
      assertEquals(expectedFailedDelete.size(), failedDelete.size());
      for (String id : failedDelete)
         assertTrue("Object " + id + " must be in 'failed delete list'.", expectedFailedDelete.contains(id));
   }

   public void testGetTypeChildren()
   {
      ItemsIterator<TypeDefinition> iterator = storage.getTypeChildren(null, true);
      List<String> result = new ArrayList<String>();
      while (iterator.hasNext())
      {
         TypeDefinition next = iterator.next();
         result.add(next.getId() + "," + next.getLocalName());
      }
      assertEquals(4, result.size());
      assertTrue(result.contains("cmis:document,nt:file"));;
      assertTrue(result.contains("cmis:folder,nt:folder"));;
      assertTrue(result.contains("cmis:policy,cmis:policy"));;
      assertTrue(result.contains("cmis:relationship,cmis:relationship"));;
   }

   public void testMoveDocument() throws Exception
   {
      ObjectData document = createDocument(rootFolder, "moveDocumentTest", "cmis:document", null, null);
      Folder targetFolder = createFolder(rootFolder, "moveDocumentTestDestination", "cmis:folder");

      assertTrue(session.itemExists("/moveDocumentTest"));
      assertFalse(session.itemExists("/moveDocumentTestDestination/moveDocumentTest"));
      storage.moveObject(document, targetFolder, rootFolder);
      assertFalse(session.itemExists("/moveDocumentTest"));
      assertTrue(session.itemExists("/moveDocumentTestDestination/moveDocumentTest"));
   }

   public void testMoveFolder() throws Exception
   {
      Folder folder = createFolder(rootFolder, "moveFolderTest", "cmis:folder");
      createDocument(folder, "childDocument", "cmis:document", null, null);
      Folder targetFolder = createFolder(rootFolder, "moveFolderTestDestination", "cmis:folder");

      assertTrue(session.itemExists("/moveFolderTest/childDocument"));
      assertTrue(session.itemExists("/moveFolderTest"));
      assertFalse(session.itemExists("/moveFolderTestDestination/moveFolderTest/childDocument"));
      assertFalse(session.itemExists("/moveFolderTestDestination/moveFolderTest"));
      storage.moveObject(folder, targetFolder, rootFolder);
      assertFalse(session.itemExists("/moveFolderTest/childDocument"));
      assertFalse(session.itemExists("/moveFolderTest"));
      assertTrue(session.itemExists("/moveFolderTestDestination/moveFolderTest"));
      assertTrue(session.itemExists("/moveFolderTestDestination/moveFolderTest/childDocument"));
   }

   public void testRenameDocument() throws Exception
   {
      ContentStream cs = new BaseContentStream("to be or not to be".getBytes(), null, "text/plain");
      Document document = createDocument(rootFolder, "renameDocumentTest", "cmis:document", cs, null);
      document.setName("renameDocumentTest01");
      storage.saveObject(document);
      //document.save();

      assertTrue(session.itemExists("/renameDocumentTest01"));

      assertEquals("renameDocumentTest01", document.getName());
      assertEquals("renameDocumentTest01", document.getProperty(CMIS.CONTENT_STREAM_FILE_NAME).getValues().get(0));
   }

   public void testRenameFolder() throws Exception
   {
      Folder folder = createFolder(rootFolder, "renameFolderTest", "cmis:folder");
      createDocument(folder, "child1", "cmis:document", null, null);
      folder.setName("renameFolderTest01");
      storage.saveObject(folder);
      //      folder.save();

      assertTrue(session.itemExists("/renameFolderTest01"));
      assertTrue(session.itemExists("/renameFolderTest01/child1"));

      assertEquals("renameFolderTest01", folder.getName());
   }

   public void testSetContent() throws Exception
   {
      Document document = createDocument(rootFolder, "setContentTest", "cmis:document", null, null);
      Node documentNode = (Node)session.getItem("/setContentTest");
      assertEquals("", documentNode.getProperty("jcr:content/jcr:data").getString());
      assertEquals("", documentNode.getProperty("jcr:content/jcr:mimeType").getString());

      ContentStream cs = new BaseContentStream("to be or not to be".getBytes(), null, "text/plain");
      document.setContentStream(cs);
      storage.saveObject(document);
      //document.save();

      documentNode = (Node)session.getItem("/setContentTest");
      assertEquals("to be or not to be", documentNode.getProperty("jcr:content/jcr:data").getString());
      assertEquals("text/plain", documentNode.getProperty("jcr:content/jcr:mimeType").getString());
   }

   protected Document createDocument(Folder folder, String name, String typeId, ContentStream content,
      VersioningState versioningState) throws Exception
   {
      Document document =
         storage.createDocument(folder, typeId, versioningState == null ? VersioningState.MAJOR : versioningState);
      document.setName(name);
      document.setContentStream(content);
      storage.saveObject(document);
      //document.save();
      return document;
   }

   protected Folder createFolder(Folder folder, String name, String typeId) throws Exception
   {
      Folder newFolder = storage.createFolder(folder, typeId);
      newFolder.setName(name);
      storage.saveObject(newFolder);
      //      newFolder.save();
      return newFolder;
   }

   protected Policy createPolicy(Folder folder, String name, String policyText, String typeId) throws Exception
   {
      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();

      PropertyDefinition<?> defName = PropertyDefinitions.getPropertyDefinition("cmis:policy", CMIS.NAME);
      properties.put(CMIS.NAME, new StringProperty(defName.getId(), defName.getQueryName(), defName.getLocalName(),
         defName.getDisplayName(), name));

      PropertyDefinition<?> defPolicyText = PropertyDefinitions.getPropertyDefinition("cmis:policy", CMIS.POLICY_TEXT);
      properties.put(CMIS.POLICY_TEXT, new StringProperty(defPolicyText.getId(), defPolicyText.getQueryName(),
         defPolicyText.getLocalName(), defPolicyText.getDisplayName(), policyText));

      Policy policy = storage.createPolicy(folder, typeId);
      policy.setProperties(properties);
      storage.saveObject(policy);
      //      policy.save();

      return policy;
   }

   /*   public void testMultifiling() throws Exception
      {
         Document document = createDocument(rootFolder, "multifilingTestDocument", "cmis:document", null, null);
         Folder folder1 = createFolder(rootFolder, "multifilingTestFolder1", "cmis:folder");
         Folder folder2 = createFolder(rootFolder, "multifilingTestFolder2", "cmis:folder");
         Folder folder3 = createFolder(rootFolder, "multifilingTestFolder3", "cmis:folder");
         Folder folder4 = createFolder(rootFolder, "multifilingTestFolder4", "cmis:folder");
         folder1.addObject(document);
         folder2.addObject(document);
         folder3.addObject(document);
         folder4.addObject(document);
         Collection<Folder> parents = document.getParents();
         for (Folder f : parents)
            System.out.println(f.getPath());
         folder4.removeObject(document);
         parents = document.getParents();
         for (Folder f : parents)
            System.out.println(f.getPath());
      }
   */
}
