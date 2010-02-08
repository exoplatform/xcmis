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

package org.xcmis.sp.jcr.exo.object;

import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.sp.jcr.exo.JcrCMIS;
import org.xcmis.sp.jcr.exo.object.EntryImpl;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.object.BaseContentStream;
import org.xcmis.spi.object.ContentStream;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.ItemsIterator;

import java.util.Calendar;

import javax.jcr.Node;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class EntryFolderTest extends EntryTest
{

   public void testAddRelationship() throws Exception
   {
      EntryImpl folder1 = createFolder(testRootFolderId, "folder1");
      EntryImpl folder2 = createFolder(testRootFolderId, "folder2");
      CmisTypeDefinitionType rtype = folder1.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      Entry rel = folder1.addRelationship("relationship1", folder2, rtype);
      assertEquals(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP, rel.getScope());
      assertTrue(relationshipsNode.hasNode(folder1.getObjectId()));
      assertTrue(relationshipsNode.getNode(folder1.getObjectId()).hasNode("relationship1"));
      assertEquals(1, folder1.getNode().getReferences().getSize());
      assertEquals(1, folder2.getNode().getReferences().getSize());
   }

   public void testCanAddPolicy() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      assertTrue(folder.canAddPolicy());
   }

   public void testCanAddToFolder() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      assertFalse(folder.canAddToFolder());
   }

   public void testCanApplyACL() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      assertTrue(folder.canApplyACL());
   }

   public void testCanCancelCheckOut() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      assertFalse(folder.canCancelCheckOut());
   }

   public void testCanCheckIn() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      assertFalse(folder.canCheckIn());
   }

   public void testCanCheckOut() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      assertFalse(folder.canCheckOut());
   }

   public void testCanCreateDocument() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      assertTrue(folder.canCreateDocument());
   }

   public void testCanCreateFolder() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      assertTrue(folder.canCreateFolder());
   }

   //   public void testCanCreatePolicy() throws Exception
   //   {
   //      EntryImpl folder = createFolder(testRootFolderId, "folder");
   //      assertTrue(folder.canCreatePolicy());
   //   }

   public void testCanCreateRelationship() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      assertTrue(folder.canCreateRelationship());
   }

   public void testCanDelete() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      assertTrue(folder.canDelete());
   }

   public void testCanDeleteContent() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      assertFalse(folder.canDeleteContent());
   }

   public void testCanDeleteTree() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      assertTrue(folder.canDeleteTree());
   }

   public void testCanGetAllVersions() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      assertFalse(folder.canGetAllVersions());
   }

   public void testCanGetAppliedPolicies() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      assertTrue(folder.canGetAppliedPolicies());
   }

   public void testCanGetChildren() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      assertTrue(folder.canGetChildren());
   }

   public void testCanGetDescendants() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      assertTrue(folder.canGetDescendants());
   }

   public void testCanGetFolderParent() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      assertTrue(folder.canGetFolderParent());
   }

   public void testCanGetParents() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      assertTrue(folder.canGetParents());
   }

   public void testCanGetProperties() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      assertTrue(folder.canGetProperties());
   }

   public void testCanGetRelationships() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      assertTrue(folder.canGetRelationships());
   }

   public void testCanMove() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      assertTrue(folder.canMove());
   }

   public void testCanRemoveFromFolder() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      assertFalse(folder.canRemoveFromFolder());
   }

   public void testCanRemovePolicy() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      assertTrue(folder.canRemovePolicy());
   }

   public void testCanSetContent() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      assertFalse(folder.canSetContent());
   }

   public void testCanUpdateProperties() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      assertTrue(folder.canUpdateProperties());
   }

   public void testCanViewContent() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      assertFalse(folder.canGetContent());
   }

   public void testCreateFolderChild() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "cmisfolder");
      assertFalse(folder.getNode().hasNodes());
      Entry f =
         folder.createChild(cmisRepository.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_FOLDER.value()), "folder1",
            null);
      f.save();
      assertTrue(folder.getNode().hasNodes());
      assertTrue(folder.getNode().hasNode("folder1"));
      Node child = folder.getNode().getNode("folder1");
      assertEquals(JcrCMIS.NT_FOLDER, child.getPrimaryNodeType().getName());
   }

   public void testCreateDocumentChild() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "cmisfolder");
      assertFalse(folder.getNode().hasNodes());
      Entry doc =
         folder
            .createChild(cmisRepository.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value()), "doc1", null);
      doc.save();
      assertTrue(folder.getNode().hasNodes());
      assertTrue(folder.getNode().hasNode("doc1"));
      Node child = folder.getNode().getNode("doc1");
      assertEquals(JcrCMIS.NT_FILE, child.getPrimaryNodeType().getName());
   }

   public void testCreatePolicyChild() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "cmispolicy");
      assertFalse(folder.getNode().hasNodes());
      Entry pol =
         folder.createChild(cmisRepository.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_POLICY.value()), "policy1",
            null);
      pol.setString(CMIS.POLICY_TEXT, "test policy");
      pol.save();
      assertTrue(folder.getNode().hasNodes());
      assertTrue(folder.getNode().hasNode("policy1"));
      Node child = folder.getNode().getNode("policy1");
      assertEquals(JcrCMIS.CMIS_NT_POLICY, child.getPrimaryNodeType().getName());
   }

   public void testCreateRelationshipChild() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "cmisfolder");
      assertFalse(folder.getNode().hasNodes());
      try
      {
         folder.createChild(cmisRepository.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value()),
            "relationship1", null);
         fail("Relationship object is not fileable.");
      }
      catch (InvalidArgumentException cve)
      {
      }
   }

   public void testGetChildren() throws Exception
   {
      Entry folder = createFolder(testRootFolderId, "cmisfolder");
      assertFalse(folder.getChildren().hasNext());
      Node folderNode = ((EntryImpl)folder).getNode();
      folderNode.addNode("test", "nt:base");
      folderNode.save();
      createDocument(folder.getObjectId(), "cmisdocument1", new byte[0], "");
      createDocument(folder.getObjectId(), "cmisdocument2", new byte[0], "");
      createFolder(folder.getObjectId(), "cmisfolder1");
      createFolder(folder.getObjectId(), "cmisfolder2");
      ItemsIterator<Entry> iter = folder.getChildren();
      assertTrue(iter.hasNext());
      int count = 0;
      for (; iter.hasNext(); count++)
         iter.next();
      assertEquals(4, count);
   }

   public void testGetContentStream() throws Exception
   {
      EntryImpl folder = createFolder(testRootFolderId, "folder");
      try
      {
         folder.getContent(null);
         fail("ConstraintViolationException should be thrown.");
      }
      catch (ConstraintException e)
      {
      }
   }

   public void testGetParent() throws Exception
   {
      Entry folder = createFolder(testRootFolderId, "folder");
      assertEquals(testRootFolderId, folder.getParents().get(0).getObjectId());
   }

   public void testGetParentOfRootFolder() throws Exception
   {
      Entry folder = new EntryImpl(root);
      assertEquals(0, folder.getParents().size());
   }

   public void testGetType() throws Exception
   {
      Entry folder = createFolder(testRootFolderId, "folder");
      CmisTypeDefinitionType type = folder.getType();
      assertEquals(EnumBaseObjectTypeIds.CMIS_FOLDER, type.getBaseId());
      assertNotNull(type.getDescription());
      assertNotNull(type.getDisplayName());
      assertEquals(EnumBaseObjectTypeIds.CMIS_FOLDER.value(), type.getId());
      assertEquals(EnumBaseObjectTypeIds.CMIS_FOLDER.value(), type.getLocalName());
      assertNotNull(type.getLocalNamespace());
      assertNull(type.getParentId());
      assertNotNull(type.getPropertyDefinition());
      assertEquals(EnumBaseObjectTypeIds.CMIS_FOLDER.value(), type.getQueryName());
   }

   public void testRemove() throws Exception
   {
      Entry folder = createFolder(testRootFolderId, "folder");
      assertTrue(testRootFolder.getNode().hasNode("folder"));
      folder.delete();
      assertFalse(testRootFolder.getNode().hasNode("folder"));
   }

   public void testRemoveWithRelationship() throws Exception
   {
      // 1. Create folder
      // 2. Add child folder1 in folder
      // 3. Create two documents in folder/folder1
      // 4. Add relationships folder/folder1/doc2 -> folder/folder1/doc1
      // 5. Create folder2 in folder folder/folder1
      // 6. Add relationship folder/folder1/folder2 -> folder/folder1
      // 7. Create document doc3 in folder folder/folder1/folder2
      // 8. Add relationships folder/folder1/folder2/doc3 -> folder/folder1/doc1
      //    and folder/folder1/folder2/doc3 <-> self
      // 9. Folder must be deleted.
      Entry folder = createFolder(testRootFolderId, "folder");
      Entry folder1 = createFolder(folder.getObjectId(), "folder1");
      Entry doc1 = createDocument(folder1.getObjectId(), "doc1", new byte[0], "");
      Entry doc2 = createDocument(folder1.getObjectId(), "doc2", new byte[0], "");
      CmisTypeDefinitionType relType =
         cmisRepository.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      doc2.addRelationship("relationship1", doc1, relType);
      Entry folder2 = createFolder(folder1.getObjectId(), "folder2");
      folder2.addRelationship("relationship2", folder1, relType);
      Entry doc3 = createDocument(folder2.getObjectId(), "doc3", new byte[0], "");
      doc3.addRelationship("relationship3", doc3, relType);
      doc3.addRelationship("relationship4", doc1, relType);
      assertTrue(root.getNode("cmis:system/cmis:relationships").hasNodes());
      try
      {
         folder.delete();
      }
      catch (org.xcmis.spi.RepositoryException re)
      {
         fail("Unable delete folder");
      }
      assertFalse(root.getNode("cmis:system/cmis:relationships").hasNodes());
   }

   public void testSetContentStream() throws Exception
   {
      Entry folder = createFolder(testRootFolderId, "folder");
      try
      {
         ContentStream cs = new BaseContentStream(new byte[0], "test", "");
         folder.setContent(cs);
         folder.save();
         fail("Folder does not support stream.");
      }
      catch (StreamNotSupportedException snse)
      {
      }
   }

   protected Node createNode() throws Exception
   {
      Node folder = testRootFolder.getNode().addNode("node", JcrCMIS.NT_FOLDER);
      folder.addMixin(JcrCMIS.CMIS_MIX_FOLDER);
      folder.setProperty(CMIS.NAME, folder.getName());
      folder.setProperty(CMIS.OBJECT_TYPE_ID, "cmis:folder");
      folder.setProperty(CMIS.BASE_TYPE_ID, "cmis:folder");
      folder.setProperty(CMIS.CREATED_BY, credentials.getUserID());
      folder.setProperty(CMIS.CREATION_DATE, Calendar.getInstance());
      folder.setProperty(CMIS.LAST_MODIFIED_BY, credentials.getUserID());
      folder.setProperty(CMIS.LAST_MODIFICATION_DATE, Calendar.getInstance());
      session.save();
      return folder;
   }
}
