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

package org.xcmis.sp.jcr.exo.NEW;

import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.CredentialsImpl;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisPropertyString;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.CmisTypeDocumentDefinitionType;
import org.xcmis.core.CmisTypeFolderDefinitionType;
import org.xcmis.core.CmisTypePolicyDefinitionType;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumContentStreamAllowed;
import org.xcmis.core.EnumUnfileObject;
import org.xcmis.core.EnumVersioningState;
import org.xcmis.sp.jcr.exo.BaseTest;
import org.xcmis.sp.jcr.exo.JcrCMIS;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.Storage;
import org.xcmis.spi.data.BaseContentStream;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.ObjectData;

import java.util.ArrayList;
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
 * @version $Id: $
 */
public class StorageTest extends BaseTest
{

   protected Storage storage;

   protected Map<String, CmisTypeDefinitionType> typeDefinitions;

   protected ObjectData rootFolder;

   public void setUp() throws Exception
   {
      super.setUp();
      storage = new JcrStorage(session);
      typeDefinitions = new HashMap<String, CmisTypeDefinitionType>();
      typeDefinitions.put("cmis:document", documentType());
      typeDefinitions.put("cmis:folder", folderType());
      typeDefinitions.put("cmis:policy", policyType());
      rootFolder = new ObjectDataImpl(root, typeDefinitions.get("cmis:folder"));
   }

   private CmisTypeDocumentDefinitionType documentType()
   {
      CmisTypeDocumentDefinitionType documentType = new CmisTypeDocumentDefinitionType();
      documentType.setBaseId(EnumBaseObjectTypeIds.CMIS_DOCUMENT);
      documentType.setContentStreamAllowed(EnumContentStreamAllowed.ALLOWED);
      documentType.setControllableACL(true);
      documentType.setControllablePolicy(true);
      documentType.setCreatable(true);
      documentType.setDescription("");
      documentType.setDisplayName("cmis:document");
      documentType.setFileable(true);
      documentType.setFulltextIndexed(true);
      documentType.setId("cmis:document");
      documentType.setIncludedInSupertypeQuery(true);
      documentType.setLocalName("nt:file");
      documentType.setLocalNamespace("");
      documentType.setParentId(null);
      documentType.setQueryable(true);
      documentType.setQueryName("cmis:document");
      documentType.setVersionable(true);
      documentType.getPropertyDefinition().addAll(PropertyDefinitionsMap.getAll("cmis:document"));
      return documentType;
   }

   private CmisTypeFolderDefinitionType folderType()
   {
      CmisTypeFolderDefinitionType folderType = new CmisTypeFolderDefinitionType();
      folderType.setBaseId(EnumBaseObjectTypeIds.CMIS_FOLDER);
      folderType.setControllableACL(true);
      folderType.setControllablePolicy(true);
      folderType.setCreatable(true);
      folderType.setDescription("");
      folderType.setDisplayName("cmis:folder");
      folderType.setFileable(true);
      folderType.setFulltextIndexed(false);
      folderType.setId("cmis:folder");
      folderType.setIncludedInSupertypeQuery(true);
      folderType.setLocalName("nt:folder");
      folderType.setLocalNamespace("");
      folderType.setParentId(null);
      folderType.setQueryable(true);
      folderType.setQueryName("cmis:folder");
      folderType.getPropertyDefinition().addAll(PropertyDefinitionsMap.getAll("cmis:folder"));
      return folderType;
   }

   private CmisTypePolicyDefinitionType policyType()
   {
      CmisTypePolicyDefinitionType policyType = new CmisTypePolicyDefinitionType();
      policyType.setBaseId(EnumBaseObjectTypeIds.CMIS_POLICY);
      policyType.setControllableACL(true);
      policyType.setControllablePolicy(true);
      policyType.setCreatable(true);
      policyType.setDescription("");
      policyType.setDisplayName("cmis:policy");
      policyType.setFileable(true);
      policyType.setFulltextIndexed(false);
      policyType.setId("cmis:policy");
      policyType.setIncludedInSupertypeQuery(true);
      policyType.setLocalName("cmis:policy");
      policyType.setLocalNamespace("");
      policyType.setParentId(null);
      policyType.setQueryable(true);
      policyType.setQueryName("cmis:policy");
      policyType.getPropertyDefinition().addAll(PropertyDefinitionsMap.getAll("cmis:policy"));
      return policyType;
   }

   public void testCreateDocument() throws Exception
   {
      CmisPropertiesType properties = new CmisPropertiesType();
      CmisPropertyString name = new CmisPropertyString();
      name.setPropertyDefinitionId(CMIS.NAME);
      name.getValue().add("createDocumentTest");
      properties.getProperty().add(name);

      ContentStream cs = new BaseContentStream("to be or not to be".getBytes(), null, "text/plain");
      ObjectData document =
         storage.createDocument(rootFolder, typeDefinitions.get("cmis:document"), properties, cs, null, null, null,
            EnumVersioningState.MAJOR);
      storage.saveObject(document);
      // new session to check is it really saved.
      Session session = repository.login(wsName);
      try
      {
         Node documentNode = (Node)session.getItem("/createDocumentTest");
         assertEquals("nt:file", documentNode.getPrimaryNodeType().getName());
         assertEquals("to be or not to be", documentNode.getProperty("jcr:content/jcr:data").getString());
         assertEquals("text/plain", documentNode.getProperty("jcr:content/jcr:mimeType").getString());
      }
      finally
      {
         session.logout();
      }
   }

   public void testCreateDocumentFromSource() throws Exception
   {
      ContentStream cs = new BaseContentStream("to be or not to be".getBytes(), null, "text/plain");
      ObjectData document = createDocument(rootFolder, "createDocumentSource", "cmis:document", cs, null);

      CmisPropertiesType properties = new CmisPropertiesType();
      CmisPropertyString name = new CmisPropertyString();
      name.setPropertyDefinitionId(CMIS.NAME);
      name.getValue().add("createDocumentSourceCopy");
      properties.getProperty().add(name);

      ObjectData documentCopy =
         storage
            .createDocumentFromSource(document, rootFolder, properties, null, null, null, EnumVersioningState.MINOR);
      storage.saveObject(documentCopy);

      assertFalse("Copy must have different name.", document.getName().equals(documentCopy.getName()));
      assertFalse("Copy must have different ID.", document.getObjectId().equals(documentCopy.getObjectId()));
      assertFalse("Copy must have different versionSeriesId.", document.getVersionSeriesId().equals(
         documentCopy.getVersionSeriesId()));
      assertFalse(documentCopy.isMajorVersion());

      // new session to check is it really saved.
      Session session = repository.login(wsName);
      try
      {
         Node documentNode = (Node)session.getItem("/createDocumentSourceCopy");
         assertEquals("nt:file", documentNode.getPrimaryNodeType().getName());
         assertEquals("to be or not to be", documentNode.getProperty("jcr:content/jcr:data").getString());
         assertEquals("text/plain", documentNode.getProperty("jcr:content/jcr:mimeType").getString());
      }
      finally
      {
         session.logout();
      }
   }

   public void testCreateFolder() throws Exception
   {
      CmisPropertiesType properties = new CmisPropertiesType();
      CmisPropertyString name = new CmisPropertyString();
      name.setPropertyDefinitionId(CMIS.NAME);
      name.getValue().add("createFolderTest");
      properties.getProperty().add(name);

      ObjectData newFolder =
         storage.createFolder(rootFolder, typeDefinitions.get("cmis:folder"), properties, null, null, null);
      storage.saveObject(newFolder);

      Session session = repository.login(wsName);
      try
      {
         Node folderNode = (Node)session.getItem("/createFolderTest");
         assertEquals("nt:folder", folderNode.getPrimaryNodeType().getName());
      }
      finally
      {
         session.logout();
      }
   }

   public void testCreatePolicy() throws Exception
   {
      CmisPropertiesType properties = new CmisPropertiesType();
      CmisPropertyString name = new CmisPropertyString();
      name.setPropertyDefinitionId(CMIS.NAME);
      name.getValue().add("createPolicyTest");

      CmisPropertyString policyText = new CmisPropertyString();
      policyText.setPropertyDefinitionId(CMIS.POLICY_TEXT);
      policyText.getValue().add("simple policy");

      properties.getProperty().add(name);
      properties.getProperty().add(policyText);

      ObjectData policy =
         storage.createPolicy(rootFolder, typeDefinitions.get("cmis:policy"), properties, null, null, null);
      storage.saveObject(policy);
      Session session = repository.login(wsName);
      try
      {
         Node policyNode = (Node)session.getItem("/createPolicyTest");
         assertEquals("cmis:policy", policyNode.getPrimaryNodeType().getName());
         assertEquals("simple policy", policyNode.getProperty("cmis:policyText").getString());
      }
      finally
      {
         session.logout();
      }
   }

   protected ObjectData createDocument(ObjectData folder, String name, String typeId, ContentStream content,
      EnumVersioningState versioningState) throws Exception
   {
      CmisPropertiesType properties = new CmisPropertiesType();
      CmisPropertyString nameProperty = new CmisPropertyString();
      nameProperty.setPropertyDefinitionId(CMIS.NAME);
      nameProperty.getValue().add(name);
      properties.getProperty().add(nameProperty);
      ObjectData document =
         storage.createDocument(folder, typeDefinitions.get(typeId), properties, content, null, null, null,
            versioningState == null ? EnumVersioningState.MAJOR : versioningState);
      storage.saveObject(document);
      return document;
   }

   protected ObjectData createFolder(ObjectData folder, String name, String typeId) throws Exception
   {
      CmisPropertiesType properties = new CmisPropertiesType();
      CmisPropertyString nameProperty = new CmisPropertyString();
      nameProperty.setPropertyDefinitionId(CMIS.NAME);
      nameProperty.getValue().add(name);
      properties.getProperty().add(nameProperty);
      ObjectData newFolder = storage.createFolder(folder, typeDefinitions.get(typeId), properties, null, null, null);
      storage.saveObject(newFolder);
      return newFolder;
   }

   public void testCreateRelationship() throws Exception
   {
      ObjectData sourceDoc = createDocument(rootFolder, "source", "cmis:document", null, null);
      ObjectData targetDoc = createDocument(rootFolder, "target", "cmis:document", null, null);

      CmisPropertiesType properties = new CmisPropertiesType();
      CmisPropertyString name = new CmisPropertyString();
      name.setPropertyDefinitionId(CMIS.NAME);
      name.getValue().add("createRelationshipTest");
      properties.getProperty().add(name);

      ObjectData policy =
         storage.createRelationship(storage.getTypeDefinition("cmis:relationship", true), sourceDoc, targetDoc,
            properties, null, null, null);
      storage.saveObject(policy);
      Session session = repository.login(wsName);
      try
      {
         Node relationshipNode =
            (Node)session.getItem("/xcmis:system/xcmis:relationships/" + sourceDoc.getObjectId()
               + "/createRelationshipTest");
         assertEquals("cmis:relationship", relationshipNode.getPrimaryNodeType().getName());
         assertEquals(sourceDoc.getObjectId(), relationshipNode.getProperty("cmis:sourceId").getString());
         assertEquals(targetDoc.getObjectId(), relationshipNode.getProperty("cmis:targetId").getString());
      }
      finally
      {
         session.logout();
      }
   }

   public void testSetContent() throws Exception
   {
      ObjectData document = createDocument(rootFolder, "setContentTest", "cmis:document", null, null);
      Node documentNode = (Node)session.getItem("/setContentTest");
      assertEquals("", documentNode.getProperty("jcr:content/jcr:data").getString());
      assertEquals("", documentNode.getProperty("jcr:content/jcr:mimeType").getString());

      ContentStream cs = new BaseContentStream("to be or not to be".getBytes(), null, "text/plain");
      storage.setContentStream(document, cs);
      storage.saveObject(document);

      // new session to check is it really saved.
      Session session = repository.login(wsName);
      try
      {
         Node documentNode2 = (Node)session.getItem("/setContentTest");
         assertEquals("to be or not to be", documentNode2.getProperty("jcr:content/jcr:data").getString());
         assertEquals("text/plain", documentNode2.getProperty("jcr:content/jcr:mimeType").getString());
      }
      finally
      {
         session.logout();
      }
   }

   public void testDeleteContent() throws Exception
   {
      ContentStream cs = new BaseContentStream("to be or not to be".getBytes(), null, "text/plain");
      ObjectData document = createDocument(rootFolder, "removeContentTest", "cmis:document", cs, null);
      Node documentNode = (Node)session.getItem("/removeContentTest");
      assertEquals("to be or not to be", documentNode.getProperty("jcr:content/jcr:data").getString());
      assertEquals("text/plain", documentNode.getProperty("jcr:content/jcr:mimeType").getString());

      storage.deleteContentStream(document);
      storage.saveObject(document);

      // new session to check is it really saved.
      Session session = repository.login(wsName);
      try
      {
         Node documentNode2 = (Node)session.getItem("/removeContentTest");
         assertEquals("", documentNode2.getProperty("jcr:content/jcr:data").getString());
         assertEquals("", documentNode2.getProperty("jcr:content/jcr:mimeType").getString());
      }
      finally
      {
         session.logout();
      }
   }

   public void testChildren() throws Exception
   {
      ObjectData folder = createFolder(rootFolder, "folderChildrenTest", "cmis:folder");
      Set<String> source = new HashSet<String>();
      String name = "testChildren";
      for (int i = 1; i <= 20; i++)
      {
         ObjectData document = createDocument(folder, name + i, "cmis:document", null, null);
         storage.saveObject(document);
         source.add(document.getObjectId());
      }
      // Check children viewing with paging. It should be close to real usage.
      int maxItems = 5;
      for (int i = 0, skipCount = 0; i < 4; i++, skipCount += maxItems)
      {
         ItemsIterator<ObjectData> children = storage.getChildren(folder, null);
         children.skip(skipCount);
         for (int count = 0; children.hasNext() && count < maxItems; count++)
         {
            ObjectData next = children.next();
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

   public void testDeleteTree() throws Exception
   {
      // Create tree.
      ObjectData folder1 = createFolder(rootFolder, "1", "cmis:folder");
      ObjectData folder2 = createFolder(folder1, "2", "cmis:folder");
      String folder2Id = folder2.getObjectId();
      ObjectData folder3 = createFolder(folder2, "3", "cmis:folder");
      ObjectData folder4 = createFolder(folder3, "4", "cmis:folder");
      ObjectData folder5 = createFolder(folder1, "5", "cmis:folder");
      ObjectData folder6 = createFolder(folder5, "6", "cmis:folder");
      ObjectData folder7 = createFolder(folder3, "7", "cmis:folder");
      ObjectData doc1 = createDocument(folder2, "doc1", "cmis:document", null, null);
      ObjectData doc2 = createDocument(folder2, "doc2", "cmis:document", null, null);
      ObjectData doc3 = createDocument(folder2, "doc3", "cmis:document", null, null);
      ObjectData doc4 = createDocument(folder4, "doc4", "cmis:document", null, null);

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
      ExtendedNode folder1Node = (ExtendedNode)((ObjectDataImpl)folder1).getNode();
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
      CredentialsImpl _credentials = new CredentialsImpl("exo", "exo".toCharArray());
      Session _session = repository.login(_credentials, wsName);
      Storage _storage = new JcrStorage(_session);
      ObjectData _folder2 = _storage.getObject(folder2Id);
      Collection<String> failedDelete = _storage.deleteTree(_folder2, true, EnumUnfileObject.DELETE, true);

      assertNotNull(failedDelete);
      assertEquals(expectedFailedDelete.size(), failedDelete.size());
      for (String id : failedDelete)
         assertTrue("Object " + id + " must be in 'failed delet list'.", expectedFailedDelete.contains(id));
   }

   public void testMoveDocument() throws Exception
   {
      ObjectData document = createDocument(rootFolder, "moveDocumentTest", "cmis:document", null, null);
      ObjectData targetFolder = createFolder(rootFolder, "destinationFolder", "cmis:folder");

      assertTrue(root.hasNode("moveDocumentTest"));
      assertFalse(root.hasNode("destinationFolder/moveDocumentTest"));
      storage.moveObject(document, targetFolder, rootFolder);
      assertFalse(root.hasNode("moveDocumentTest"));
      assertTrue(root.hasNode("destinationFolder/moveDocumentTest"));
   }

   public void testMoveFolder() throws Exception
   {
      ObjectData folder = createFolder(rootFolder, "moveFolderTest", "cmis:folder");
      createDocument(folder, "childDocument", "cmis:document", null, null);
      ObjectData targetFolder = createFolder(rootFolder, "destinationFolder", "cmis:folder");

      assertTrue(root.hasNode("moveFolderTest/childDocument"));
      assertTrue(root.hasNode("moveFolderTest"));
      assertFalse(root.hasNode("destinationFolder/moveFolderTest/childDocument"));
      assertFalse(root.hasNode("destinationFolder/moveFolderTest"));
      storage.moveObject(folder, targetFolder, rootFolder);
      assertFalse(root.hasNode("moveFolderTest/childDocument"));
      assertFalse(root.hasNode("moveFolderTest"));
      assertTrue(root.hasNode("destinationFolder/moveFolderTest"));
      assertTrue(root.hasNode("destinationFolder/moveFolderTest/childDocument"));
   }

   public void testGetTypeChildren()
   {
      ItemsIterator<CmisTypeDefinitionType> iterator = storage.getTypeChildren(null, true);
      List<String> result = new ArrayList<String>();
      while (iterator.hasNext())
      {
         CmisTypeDefinitionType next = iterator.next();
         result.add(next.getId() + "," + next.getLocalName());
      }
      assertEquals(4, result.size());
      assertTrue(result.contains("cmis:document,nt:file"));;
      assertTrue(result.contains("cmis:folder,nt:folder"));;
      assertTrue(result.contains("cmis:policy,cmis:policy"));;
      assertTrue(result.contains("cmis:relationship,cmis:relationship"));;
   }

   public void testCheckout() throws Exception
   {
      ObjectData document = createDocument(rootFolder, "checkedOutDocumentTest", "cmis:document", null, null);
      storage.checkout(document);
      Node pwcStorage = root.getNode(JcrCMIS.CMIS_SYSTEM + "/" + JcrCMIS.CMIS_WORKING_COPIES);
      assertTrue(pwcStorage.hasNode(document.getVersionSeriesId()));
      assertTrue(pwcStorage.hasNode(document.getVersionSeriesId() + "/" + document.getName()));
   }

   //   public void test1() throws Exception
   //   {
   //      ExtendedNode n1 = (ExtendedNode)root.addNode("1");
   //      ExtendedNode n2 = (ExtendedNode)n1.addNode("2");
   //      ExtendedNode n3 = (ExtendedNode)n2.addNode("3");
   //      String n1Id = n1.getIdentifier();
   //      String n2Id = n2.getIdentifier();
   //      String n3Id = n3.getIdentifier();
   //      n1.addMixin("exo:privilegeable");
   //      n1.setPermission("root", PermissionType.ALL);
   //            n1.setPermission("exo", new String[]{PermissionType.READ});
   //      n1.removePermission("any", PermissionType.SET_PROPERTY);
   //      n1.removePermission("any", PermissionType.REMOVE);
   //      n1.removePermission("any", PermissionType.ADD_NODE);
   //      n1.removePermission("any", PermissionType.READ);
   //
   //      n2.addMixin("exo:privilegeable");
   //      n2.setPermission("root", PermissionType.ALL);
   //      n2.setPermission("exo", new String[]{PermissionType.READ});
   //
   //      session.save();
   //
   //      CredentialsImpl credentials_ = new CredentialsImpl("exo", "exo".toCharArray());
   //      ExtendedSession session = (ExtendedSession)repository.login(credentials_, wsName);
   //      ((ExtendedSession)session).getNodeByIdentifier(n2Id).remove();
   //      try
   //      {
   //         session.save();
   //      }
   //      catch (Exception e)
   //      {
   //         System.out.println(">>>>>>>>>>>>>> "+e.getMessage());
   //      }
   //      session.refresh(false);
   //      session.getNodeByIdentifier(n2Id);
   //      
   //   }

}
