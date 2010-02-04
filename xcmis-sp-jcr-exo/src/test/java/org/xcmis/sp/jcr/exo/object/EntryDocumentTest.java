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
import org.xcmis.core.EnumRelationshipDirection;
import org.xcmis.core.EnumVersioningState;
import org.xcmis.sp.jcr.exo.JcrCMIS;
import org.xcmis.sp.jcr.exo.object.EntryImpl;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.object.BaseContentStream;
import org.xcmis.spi.object.ContentStream;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.ItemsIterator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.PropertyIterator;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class EntryDocumentTest extends EntryTest
{

   public void testAddRelationship() throws Exception
   {
      EntryImpl doc1 = createDocument(testRootFolderId, "doc1", new byte[0], "");
      EntryImpl doc2 = createDocument(testRootFolderId, "doc2", new byte[0], "");
      CmisTypeDefinitionType rtype = doc1.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      Entry rel = doc1.addRelationship("relationship1", doc2, rtype);
      assertEquals(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP, rel.getScope());
      assertTrue(relationshipsNode.hasNode(doc1.getObjectId()));
      assertTrue(relationshipsNode.getNode(doc1.getObjectId()).hasNode("relationship1"));
      int count = 0;
      for (PropertyIterator iter = doc1.getNode().getReferences(); iter.hasNext();)
         if (iter.nextProperty().getParent().isNodeType(JcrCMIS.CMIS_RELATIONSHIP))
            count++;
      assertEquals(1, count);
      count = 0;
      for (PropertyIterator iter = doc2.getNode().getReferences(); iter.hasNext();)
         if (iter.nextProperty().getParent().isNodeType(JcrCMIS.CMIS_RELATIONSHIP))
            count++;
      assertEquals(1, count);
   }

   public void testCanAddPolicy() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertTrue(doc.canAddPolicy());
   }

   public void testCanAddToFolder() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertFalse(doc.canAddToFolder());
   }

   public void testCanApplyACL() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertTrue(doc.canApplyACL());
   }

   public void testCanCancelCheckOut() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertFalse(doc.canCancelCheckOut());
   }

   public void testCanCheckIn() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertFalse(doc.canCheckIn());
   }

   public void testCanCheckOut() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertTrue(doc.canCheckOut());
   }

   public void testCanCreateDocument() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertFalse(doc.canCreateDocument());
   }

   public void testCanCreateFolder() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertFalse(doc.canCreateFolder());
   }

   //   public void testCanCreatePolicy() throws Exception
   //   {
   //      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
   //      assertFalse(doc.canCreatePolicy());
   //   }

   public void testCanCreateRelationship() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertTrue(doc.canCreateRelationship());
   }

   public void testCanDelete() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertTrue(doc.canDelete());
   }

   public void testCanDeleteContent() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertTrue(doc.canDeleteContent());
   }

   public void testCanDeleteTree() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertFalse(doc.canDeleteTree());
   }

   public void testCanGetAllVersions() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertTrue(doc.canGetAllVersions());
   }

   public void testCanGetAppliedPolicies() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertTrue(doc.canGetAppliedPolicies());
   }

   public void testCanGetChildren() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertFalse(doc.canGetChildren());
   }

   public void testCanGetContent() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertTrue(doc.canGetContent());
   }

   public void testCanGetDescendants() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertFalse(doc.canGetDescendants());
   }

   public void testCanGetFolderParent() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertFalse(doc.canGetFolderParent());
   }

   public void testCanGetParents() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertTrue(doc.canGetParents());
   }

   public void testCanGetProperties() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertTrue(doc.canGetProperties());
   }

   public void testCanGetRelationships() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertTrue(doc.canGetRelationships());
   }

   public void testCanMove() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertTrue(doc.canMove());
   }

   public void testCanRemoveFromFolder() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertFalse(doc.canRemoveFromFolder());
   }

   public void testCanRemovePolicy() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertTrue(doc.canRemovePolicy());
   }

   public void testCanSetContent() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertTrue(doc.canSetContent());
   }

   public void testCanUpdateProperties() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertTrue(doc.canUpdateProperties());
   }

   public void testCreateDocumentChild() throws Exception
   {
      Entry doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      try
      {
         doc.createChild(((EntryImpl)doc).getTypeDefinition(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value()), "doc1", null);
         fail("Document may not have children.");
      }
      catch (ConstraintException cve)
      {
      }
   }

   public void testCreateFolderChild() throws Exception
   {
      Entry doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      try
      {
         doc
            .createChild(((EntryImpl)doc).getTypeDefinition(EnumBaseObjectTypeIds.CMIS_FOLDER.value()), "folder1", null);
         fail("Document may not have children.");
      }
      catch (ConstraintException cve)
      {
      }
   }

   public void testCreatePolicyChild() throws Exception
   {
      Entry doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      try
      {
         doc
            .createChild(((EntryImpl)doc).getTypeDefinition(EnumBaseObjectTypeIds.CMIS_POLICY.value()), "policy1", null);
         fail("Document may not have children.");
      }
      catch (ConstraintException cve)
      {
      }
   }

   public void testCreateRelationshipChild() throws Exception
   {
      Entry doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      try
      {
         doc.createChild(((EntryImpl)doc).getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value()),
            "relationship1", null);
         fail("Document may not have children.");
      }
      catch (ConstraintException cve)
      {
      }
   }

   public void testGetChildren() throws Exception
   {
      try
      {
         Entry doc = createDocument(testRootFolderId, "doc", new byte[0], "");
         doc.getChildren();
         fail("Document may not have children.");
      }
      catch (UnsupportedOperationException e)
      {
      }
   }

   public void testGetContentStream() throws Exception
   {
      EntryImpl doc =
         (EntryImpl)createDocument(testRootFolderId, "doc", "test get content stream".getBytes("UTF-8"), "text/plain");

      ContentStream cs = doc.getContent(null);
      assertEquals(23, cs.length());
      assertEquals("text/plain", cs.getMediaType());
      InputStream in = cs.getStream();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      int rd = -1;
      while ((rd = in.read()) != -1)
         out.write(rd);

      assertEquals("test get content stream", new String(out.toByteArray()));
   }

   public void testGetParents() throws Exception
   {
      Entry folder = createFolder(testRootFolderId, "cmisfolder");
      Entry doc = createDocument(folder.getObjectId(), "doc", new byte[0], "");
      assertEquals(folder.getObjectId(), doc.getParents().get(0).getObjectId());
   }

   public void testGetRelationship() throws Exception
   {
      EntryImpl doc1 = createDocument(testRootFolderId, "doc1", new byte[0], "");
      EntryImpl doc2 = createDocument(testRootFolderId, "doc2", new byte[0], "");

      EntryImpl doc3 = createDocument(testRootFolderId, "doc3", new byte[0], "");
      EntryImpl doc4 = createDocument(testRootFolderId, "doc4", new byte[0], "");
      createRelationship(doc1.getObjectId(), doc1.getObjectId());
      createRelationship(doc1.getObjectId(), doc2.getObjectId());
      createRelationship(doc1.getObjectId(), doc3.getObjectId());
      createRelationship(doc1.getObjectId(), doc4.getObjectId());
      createRelationship(doc2.getObjectId(), doc1.getObjectId());

      ItemsIterator<Entry> iter = doc1.getRelationships(EnumRelationshipDirection.SOURCE, true, null);
      int count = 0;
      for (; iter.hasNext(); count++)
         iter.next();
      assertEquals(4, count);
      iter = doc1.getRelationships(EnumRelationshipDirection.TARGET, true, null);
      count = 0;
      for (; iter.hasNext(); count++)
         iter.next();
      assertEquals(2, count);
      iter = doc1.getRelationships(EnumRelationshipDirection.EITHER, true, null);
      count = 0;
      for (; iter.hasNext(); count++)
         iter.next();
      // Get 5 instead 6 as may be expected (4 + 2) because in one of
      // relationship has the same source and target.
      assertEquals(5, count);
   }

   public void testGetType() throws Exception
   {
      Entry doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      CmisTypeDefinitionType type = doc.getType();
      assertEquals(EnumBaseObjectTypeIds.CMIS_DOCUMENT, type.getBaseId());
      assertNotNull(type.getDescription());
      assertNotNull(type.getDisplayName());
      assertEquals(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value(), type.getId());
      assertEquals(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value(), type.getLocalName());
      assertNotNull(type.getLocalNamespace());
      assertNull(type.getParentId());
      assertNotNull(type.getPropertyDefinition());
      assertEquals(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value(), type.getQueryName());
   }

   public void testRemove() throws Exception
   {
      Entry doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertTrue(testRootFolder.getNode().hasNode("doc"));
      doc.delete();
      assertFalse(testRootFolder.getNode().hasNode("doc"));
   }

   public void testRemoveWithRelationship() throws Exception
   {
      // 1. Create two objects
      // 2. Add relationship doc1 -> doc2
      // 3. Check both objects has relationships
      // 4. Remove doc1, relationship will be removed also
      // 5. doc2 is untouched but has not any relationships any more.
      EntryImpl doc1 = createDocument(testRootFolderId, "doc1", new byte[0], "");
      EntryImpl doc2 = createDocument(testRootFolderId, "doc2", new byte[0], "");
      String doc2Id = doc2.getObjectId();
      doc1.addRelationship("relationship1", doc2, cmisRepository
         .getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value()));
      assertTrue(doc1.getRelationships(EnumRelationshipDirection.EITHER, true,
         cmisRepository.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value())).hasNext());
      assertTrue(doc2.getRelationships(EnumRelationshipDirection.EITHER, true,
         cmisRepository.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value())).hasNext());
      doc1.delete();
      assertFalse(testRootFolder.getNode().hasNode("doc"));
      try
      {
         // Must be untouched.
         cmisRepository.getObjectById(doc2Id);
      }
      catch (ObjectNotFoundException e)
      {
         fail("Must not be removed.");
      }
      assertFalse(doc2.getRelationships(EnumRelationshipDirection.EITHER, true,
         cmisRepository.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value())).hasNext());
   }

   public void testSetContentStream() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      ContentStream cs = new BaseContentStream("to be or not to be".getBytes("UTF-8"), "test", "text/plain");
      doc.setContent(cs);
      doc.save();
      assertEquals("to be or not to be", doc.getNode().getNode("jcr:content").getProperty("jcr:data").getString());
      assertEquals("text/plain", doc.getNode().getNode("jcr:content").getProperty("jcr:mimeType").getString());
   }

   public void testGetVersionLabel() throws Exception
   {
      EntryImpl doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      assertEquals("current", doc.getVersionLabel());
   }

   public void testGetVersionLabelPwc() throws Exception
   {
      EntryImpl doc =
         createDocument(testRootFolderId, "doc", "nt:file", new byte[0], "", EnumVersioningState.CHECKEDOUT);
      assertEquals("pwc", doc.getVersionLabel());
   }

   protected Node createNode() throws Exception
   {
      Node node = testRootFolder.getNode().addNode("node", "nt:file");
      node.addMixin(JcrCMIS.CMIS_DOCUMENT);
      //      String docId = idResolver.getNodeIdentifier(node);
      node.setProperty(CMIS.NAME, node.getName());
      //      node.setProperty(CMIS.OBJECT_ID, docId);
      node.setProperty(CMIS.OBJECT_TYPE_ID, "cmis:document");
      node.setProperty(CMIS.BASE_TYPE_ID, "cmis:document");
      node.setProperty(CMIS.CREATED_BY, credentials.getUserID());
      node.setProperty(CMIS.CREATION_DATE, Calendar.getInstance());
      node.setProperty(CMIS.LAST_MODIFIED_BY, credentials.getUserID());
      node.setProperty(CMIS.LAST_MODIFICATION_DATE, Calendar.getInstance());
      node.setProperty(CMIS.IS_IMMUTABLE, false);
      //      node.setProperty(CMIS.VERSION_SERIES_ID, docId);
      node.setProperty(CMIS.CONTENT_STREAM_MIME_TYPE, "");
      node.setProperty(CMIS.CONTENT_STREAM_LENGTH, 0);
      node.setProperty(CMIS.CONTENT_STREAM_FILE_NAME, node.getName());
      //      node.setProperty(CMIS.CONTENT_STREAM_ID, docId);
      node.setProperty(CMIS.IS_LATEST_VERSION, true);
      node.setProperty("cmis:latestVersion", node);
      node.setProperty(CMIS.IS_MAJOR_VERSION, false);
      node.setProperty(CMIS.IS_LATEST_MAJOR_VERSION, false);
      node.setProperty(CMIS.VERSION_LABEL, "current");
      Node content = node.addNode("jcr:content", "nt:resource");
      content.setProperty("jcr:mimeType", "");
      content.setProperty("jcr:lastModified", Calendar.getInstance());
      content.setProperty("jcr:data", new ByteArrayInputStream(new byte[0]));
      session.save();
      return node;
   }

}
