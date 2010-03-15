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

package org.xcmis.core.impl;

import org.xcmis.core.CmisAllowableActionsType;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisPropertyId;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumPropertiesBase;
import org.xcmis.core.EnumUnfileObject;
import org.xcmis.core.EnumVersioningState;
import org.xcmis.core.impl.ObjectServiceImpl;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentAlreadyExistsException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.object.BaseContentStream;
import org.xcmis.spi.object.ContentStream;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.utils.CmisUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: ObjectServiceTest.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class ObjectServiceTest extends BaseTest
{

   private ObjectServiceImpl objectService;

   public void setUp() throws Exception
   {
      super.setUp();
      objectService = new ObjectServiceImpl(repositoryService, propertyService);
   }

   public void testCreateAndRemovePolicy() throws Exception
   {
      CmisPropertiesType properties = new CmisPropertiesType();
      CmisPropertyId type = new CmisPropertyId();
      type.setPropertyDefinitionId(EnumPropertiesBase.CMIS_OBJECT_TYPE_ID.value());
      type.getValue().add(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      properties.getProperty().add(type);

      CmisObjectType obj =
         objectService.createDocument(repositoryId, testFolderId, properties, null, EnumVersioningState.NONE, null,
            null, null);
      String id = CmisUtils.getObjectId(obj);
      Entry doc = repository.getObjectById(id);
      assertEquals(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value(), doc.getType().getId());

      properties = new CmisPropertiesType();
      type = new CmisPropertyId();
      type.setPropertyDefinitionId(EnumPropertiesBase.CMIS_OBJECT_TYPE_ID.value());
      type.getValue().add(EnumBaseObjectTypeIds.CMIS_POLICY.value());
      properties.getProperty().add(type);

      CmisObjectType newPolicy = objectService.createPolicy(repositoryId, testFolderId, properties, null, null, null);
      String policyId = null;
      for (CmisProperty p : newPolicy.getProperties().getProperty())
      {
         if (p.getPropertyDefinitionId().equals(EnumPropertiesBase.CMIS_OBJECT_ID.value()))
            policyId = ((CmisPropertyId)p).getValue().get(0);
      }

      Entry policy = repository.getObjectById(policyId);
      doc.applyPolicy(policy);
      doc.save();

      // Try delete policy. 
      try
      {
         objectService.deleteObject(repositoryId, policyId, true);
         fail();
      }
      catch (ConstraintException ex)
      {
         // Cannot delete policy which is already applied.
      }

      doc.removePolicy(policy);
      doc.save();
      objectService.deleteObject(repositoryId, policyId, true);
   }

   public void testCreateCheckedOutDocument() throws Exception
   {
      CmisPropertiesType properties = new CmisPropertiesType();
      CmisPropertyId type = new CmisPropertyId();
      type.setPropertyDefinitionId(CMIS.OBJECT_TYPE_ID);
      type.getValue().add(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      properties.getProperty().add(type);

      CmisObjectType obj =
         objectService.createDocument(repositoryId, testFolderId, properties, null, EnumVersioningState.CHECKEDOUT,
            null, null, null);
      String id = CmisUtils.getObjectId(obj);

      Entry doc = null;
      try
      {
         doc = repository.getObjectById(id);
      }
      catch (ObjectNotFoundException infe)
      {
         fail("Document does not exists.");
      }
      assertNotNull(doc);
      assertEquals(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value(), doc.getType().getId());
      assertFalse(doc.isMajor());
   }

   public void testCreateDocument() throws Exception
   {
      CmisPropertiesType properties = new CmisPropertiesType();
      CmisPropertyId type = new CmisPropertyId();
      type.setPropertyDefinitionId(CMIS.OBJECT_TYPE_ID);
      type.getValue().add(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      properties.getProperty().add(type);

      CmisObjectType obj =
         objectService.createDocument(repositoryId, testFolderId, properties, null, EnumVersioningState.NONE, null,
            null, null);
      String id = CmisUtils.getObjectId(obj);
      Entry doc = null;
      try
      {
         doc = repository.getObjectById(id);
      }
      catch (ObjectNotFoundException infe)
      {
         fail("Document does not exists.");
      }
      assertNotNull(doc);
      assertEquals(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value(), doc.getType().getId());
      assertFalse(doc.isMajor());
   }

   public void testCreateDocumentUnfiled() throws Exception
   {
      CmisPropertiesType properties = new CmisPropertiesType();
      CmisPropertyId type = new CmisPropertyId();
      type.setPropertyDefinitionId(CMIS.OBJECT_TYPE_ID);
      type.getValue().add(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      properties.getProperty().add(type);

      CmisObjectType obj =
         objectService.createDocument(repositoryId, null, properties, null, EnumVersioningState.NONE, null, null, null);
      String id = CmisUtils.getObjectId(obj);
      Entry doc = null;
      try
      {
         doc = repository.getObjectById(id);
      }
      catch (ObjectNotFoundException infe)
      {
         fail("Document does not exists.");
      }
      assertNotNull(doc);
      assertEquals(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value(), doc.getType().getId());
      assertFalse(doc.isMajor());
   }

   //   public void testDeleteTree() throws Exception
   //   {
   //      CmisPropertiesType properties = new CmisPropertiesType();
   //      CmisPropertyId type = new CmisPropertyId();
   //      type.setPropertyDefinitionId(CMIS.OBJECT_TYPE_ID);
   //      type.getValue().add(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
   //      properties.getProperty().add(type);
   //      
   //      setCapabilityUnfiling(true);
   //      
   //
   //      CmisObjectType obj =
   //         objectService.createDocument(repositoryId, null, properties, null, EnumVersioningState.NONE, null,
   //            null, null);
   //      String id = CmisUtils.getObjectId(obj);
   //      Entry doc = null;
   //      try
   //      {
   //         doc = repository.getObjectById(id);
   //      }
   //      catch (ObjectNotFoundException infe)
   //      {
   //         fail("Document does not exists.");
   //      }
   //      assertNotNull(doc);
   //      assertEquals(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value(), doc.getType().getId());
   //      assertFalse(doc.isMajor());
   //   }

   public void testCreateDocumentFromSource() throws Exception
   {
      Entry source = createFolder(testFolder, "source");
      Entry folder = createFolder(source, "folder");
      Entry doc1 =
         createDocument(folder, "doc1", new BaseContentStream("test".getBytes("UTF-8"), "doc1", "text/plain"));
      Entry target = createFolder(testFolder, "target");
      CmisPropertiesType properties = new CmisPropertiesType();
      CmisProperty prop = new CmisProperty();
      prop.setPropertyDefinitionId(CMIS.LAST_MODIFIED_BY);
      properties.getProperty().add(prop);

      CmisObjectType copy =
         objectService.createDocumentFromSource(repositoryId, doc1.getObjectId(), target.getObjectId(), properties,
            EnumVersioningState.MAJOR, null, null, null);
      assertNotNull(copy);
      String copyId = CmisUtils.getObjectId(copy);
      assertFalse("Copy must have different ID!", doc1.getObjectId().equals(copyId));
      // Check is content copied.
      byte[] b = new byte[128];
      int r = repository.getObjectById(copyId).getContent(null).getStream().read(b);
      assertEquals("test", new String(b, 0, r));
   }

   public void testCreateFolder() throws Exception
   {
      CmisPropertiesType properties = new CmisPropertiesType();
      CmisPropertyId type = new CmisPropertyId();
      type.setPropertyDefinitionId(CMIS.OBJECT_TYPE_ID);
      type.getValue().add(EnumBaseObjectTypeIds.CMIS_FOLDER.value());
      properties.getProperty().add(type);

      CmisObjectType obj = objectService.createFolder(repositoryId, testFolderId, properties, null, null, null);
      String id = CmisUtils.getObjectId(obj);

      Entry folder = null;
      try
      {
         folder = repository.getObjectById(id);
      }
      catch (ObjectNotFoundException infe)
      {
         fail("Folder does not exists.");
      }
      assertNotNull(folder);
      assertEquals(EnumBaseObjectTypeIds.CMIS_FOLDER.value(), folder.getType().getId());
   }

   public void testCreateMajorDocument() throws Exception
   {
      CmisPropertiesType properties = new CmisPropertiesType();
      CmisPropertyId type = new CmisPropertyId();
      type.setPropertyDefinitionId(CMIS.OBJECT_TYPE_ID);
      type.getValue().add(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      properties.getProperty().add(type);

      CmisObjectType obj =
         objectService.createDocument(repositoryId, testFolderId, properties, null, EnumVersioningState.MAJOR, null,
            null, null);
      String id = CmisUtils.getObjectId(obj);

      Entry doc = null;
      try
      {
         doc = repository.getObjectById(id);
      }
      catch (ObjectNotFoundException infe)
      {
         fail("Document does not exists.");
      }
      assertNotNull(doc);
      assertEquals(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value(), doc.getType().getId());
      assertTrue(doc.isMajor());
   }

   public void testCreateRelationship() throws Exception
   {
      Entry source = createDocument(testFolder, "doc1", null);
      Entry target = createDocument(testFolder, "doc2", null);
      CmisPropertiesType properties = new CmisPropertiesType();
      CmisPropertyId type = new CmisPropertyId();
      type.setPropertyDefinitionId(CMIS.OBJECT_TYPE_ID);
      type.getValue().add(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      CmisPropertyId sourceId = new CmisPropertyId();
      sourceId.setPropertyDefinitionId(CMIS.SOURCE_ID);
      sourceId.getValue().add(source.getObjectId());
      CmisPropertyId targetId = new CmisPropertyId();
      targetId.setPropertyDefinitionId(CMIS.TARGET_ID);
      targetId.getValue().add(target.getObjectId());
      properties.getProperty().add(type);
      properties.getProperty().add(sourceId);
      properties.getProperty().add(targetId);

      CmisObjectType obj = objectService.createRelationship(repositoryId, properties, null, null, null);
      String id = CmisUtils.getObjectId(obj);

      Entry relationship = null;
      try
      {
         relationship = repository.getObjectById(id);
      }
      catch (ObjectNotFoundException infe)
      {
         fail("Relationship does not exists.");
      }
      assertNotNull(relationship);
      assertEquals(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value(), relationship.getType().getId());
      assertEquals(source.getObjectId(), relationship.getString(CMIS.SOURCE_ID));
      assertEquals(target.getObjectId(), relationship.getString(CMIS.TARGET_ID));
   }

   public void testDeleteContentStream() throws Exception
   {
      ContentStream cs = new BaseContentStream("test".getBytes("UTF-8"), "test", "text/plain");
      Entry doc = createDocument(testFolder, "doc1", cs);
      String id = doc.getObjectId();

      objectService.deleteContentStream(repositoryId, id, null);
      // Stream must be empty.
      assertNull(repository.getObjectById(id).getContent(null));
   }

   public void testDeleteObject() throws Exception
   {
      Entry folder1 = createFolder(testFolder, "folder1");
      Entry folder2 = createFolder(testFolder, "folder2");
      Entry doc = createDocument(folder2, "doc1", null);
      try
      {
         objectService.deleteObject(repositoryId, folder2.getObjectId(), true);
         fail("ConstraintViolationException must be thrown, cmisfolder2 is not empty.");
      }
      catch (ConstraintException e)
      {
      }

      String id = folder1.getObjectId();
      // folder1 is empty , must be able to delete it
      objectService.deleteObject(repositoryId, id, true);
      try
      {
         repository.getObjectById(id);
         fail();
      }
      catch (ObjectNotFoundException e)
      {
      }

      id = doc.getObjectId();
      objectService.deleteObject(repositoryId, id, true); // delete child of folder2
      try
      {
         repository.getObjectById(id);
         fail();
      }
      catch (ObjectNotFoundException e)
      {
      }

      id = folder2.getObjectId();
      // now must be able delete folder2, it is empty
      objectService.deleteObject(repositoryId, id, true);
      try
      {
         repository.getObjectById(id);
         fail();
      }
      catch (ObjectNotFoundException e)
      {
      }
   }

   public void testDeleteRelationship() throws Exception
   {
      Entry source = createDocument(testFolder, "doc1", null);
      Entry target = createDocument(testFolder, "doc2", null);
      Entry rel =
         source.addRelationship("relationship1", target, repository
            .getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value()));

      String id = rel.getObjectId();

      objectService.deleteObject(repositoryId, id, true);
      try
      {
         repository.getObjectById(id);
         fail("Object must be removed.");
      }
      catch (ObjectNotFoundException e)
      {
      }
   }

   public void testDeleteTree_UnfileOprion_DELETE() throws Exception
   {
      // folder
      // - folder1
      //   - folder2
      //     - doc1
      //     - folder3
      //       - doc2
      Entry folder = createFolder(testFolder, "folder");
      Entry folder1 = createFolder(folder, "folder1");
      Entry folder2 = createFolder(folder1, "folder2");
      Entry doc1 = createDocument(folder2, "doc1", null);
      Entry folder3 = createFolder(folder2, "folder3");
      Entry doc2 = createDocument(folder3, "doc2", null);
      String id1 = folder1.getObjectId();

      objectService.deleteTree(repositoryId, id1, EnumUnfileObject.DELETE, true);
      try
      {
         repository.getObjectById(id1);
         fail();
      }
      catch (ObjectNotFoundException e)
      {
      }
      try
      {
         repository.getObjectById(folder2.getObjectId());
         fail();
      }
      catch (ObjectNotFoundException e)
      {
      }
      try
      {
         repository.getObjectById(folder3.getObjectId());
         fail();
      }
      catch (ObjectNotFoundException e)
      {
      }
      try
      {
         repository.getObjectById(doc1.getObjectId());
         fail();
      }
      catch (ObjectNotFoundException e)
      {
      }
      try
      {
         repository.getObjectById(doc2.getObjectId());
         fail();
      }
      catch (ObjectNotFoundException e)
      {
      }
   }

   public void testDeleteTree_UnfileOprion_DELETESINGLEFILED() throws Exception
   {
      // folder
      // - folder1
      //   - folder2
      //     - doc1
      //     - folder3
      //       - doc2
      Entry folder = createFolder(testFolder, "folder");
      Entry folder1 = createFolder(folder, "folder1");
      Entry folder2 = createFolder(folder1, "folder2");
      Entry doc1 = createDocument(folder2, "doc1", null);
      Entry folder3 = createFolder(folder2, "folder3");
      Entry doc2 = createDocument(folder3, "doc2", null);

      folder1.addChild(doc1);
      assertEquals(2, doc1.getParents().size());
      assertEquals(true, doc1.getParents().contains(folder1));
      assertEquals(true, doc1.getParents().contains(folder2));

      String folderID = folder.getObjectId();
      String folder1ID = folder1.getObjectId();
      String folder2ID = folder2.getObjectId();
      String folder3ID = folder3.getObjectId();
      String doc1ID = doc1.getObjectId();
      String doc2ID = doc2.getObjectId();

      objectService.deleteTree(repositoryId, folder2ID, EnumUnfileObject.DELETESINGLEFILED, true);
      try
      {
         repository.getObjectById(folderID);
      }
      catch (ObjectNotFoundException e)
      {
         fail("Folder 'folder' must be kept");
      }
      try
      {
         repository.getObjectById(folder1ID);
      }
      catch (ObjectNotFoundException e)
      {
         fail("Folder 'folder1' must be kept");
      }
      try
      {
         repository.getObjectById(folder2ID);
      }
      catch (ObjectNotFoundException e)
      {
         fail("Folder 'folder2' must be kept");
      }
      try
      {
         repository.getObjectById(folder3ID);
      }
      catch (ObjectNotFoundException e)
      {
         fail("Folder 'folder3' must be kept");
      }
      try
      {
         doc1 = repository.getObjectById(doc1ID);
         assertEquals(1, doc1.getParents().size());
         assertEquals(true, doc1.getParents().contains(folder1));
      }
      catch (ObjectNotFoundException e)
      {
         fail("Document 'doc1' must be kept because it has parents that is out of this folder tree.");
      }
      try
      {
         repository.getObjectById(doc2ID);
         fail("Document 'doc2' must be removed.");
      }
      catch (ObjectNotFoundException e)
      {
      }
   }

   public void testDeleteTreeUnfileOprionIsUNFILE() throws Exception
   {
      // folder
      // - folder1
      //   - folder2
      //     - doc1
      //     - folder3
      //       - doc2
      Entry folder = createFolder(testFolder, "folder");
      Entry folder1 = createFolder(folder, "folder1");
      Entry folder2 = createFolder(folder1, "folder2");
      Entry doc1 = createDocument(folder2, "doc1", null);
      Entry folder3 = createFolder(folder2, "folder3");
      Entry doc2 = createDocument(folder3, "doc2", null);

      String folderID = folder.getObjectId();
      String folder1ID = folder1.getObjectId();
      String folder2ID = folder2.getObjectId();
      String folder3ID = folder3.getObjectId();
      String doc1ID = doc1.getObjectId();
      String doc2ID = doc2.getObjectId();

      objectService.deleteTree(repositoryId, folder2ID, EnumUnfileObject.UNFILE, true);
      List<String> l = new ArrayList<String>();
      l.add(folderID);
      l.add(folder1ID);
      l.add(folder2ID);
      l.add(folder3ID);
      l.add(doc1ID);
      l.add(doc2ID);

      for (String id : l)
      {
         try
         {
            repository.getObjectById(id);
         }
         catch (ObjectNotFoundException e)
         {
            fail("Object " + id + "  must not be deleted.");
         }
      }
      // Documents are unfiled.
      assertEquals(1, folder.getChildren().size());
      assertEquals(1, folder1.getChildren().size());
      assertEquals(1, folder2.getChildren().size());
      assertEquals(0, folder3.getChildren().size());
      assertEquals(0, doc1.getParents().size());
      assertEquals(0, doc2.getParents().size());
   }

   public void testGetAllowableActionsDocument() throws Exception
   {
      Entry doc = createDocument(testFolder, "doc1", null);
      CmisAllowableActionsType actions = objectService.getAllowableActions(repositoryId, doc.getObjectId());
      assertTrue(actions.isCanApplyPolicy());
      assertFalse(actions.isCanAddObjectToFolder());
      assertFalse(actions.isCanCancelCheckOut());
      assertFalse(actions.isCanCheckIn());
      assertTrue(actions.isCanCheckOut());
      assertFalse(actions.isCanCreateDocument());
      assertFalse(actions.isCanCreateFolder());
      //      assertFalse(actions.isCanCreatePolicy());
      assertTrue(actions.isCanCreateRelationship());
      assertTrue(actions.isCanDeleteObject());
      assertTrue(actions.isCanDeleteContentStream());
      assertFalse(actions.isCanDeleteTree());
      assertTrue(actions.isCanGetAllVersions());
      assertTrue(actions.isCanGetAppliedPolicies());
      assertFalse(actions.isCanGetChildren());
      assertFalse(actions.isCanGetDescendants());
      assertFalse(actions.isCanGetFolderParent());
      assertTrue(actions.isCanGetObjectParents());
      assertTrue(actions.isCanGetProperties());
      assertTrue(actions.isCanGetObjectRelationships());
      assertTrue(actions.isCanMoveObject());
      assertFalse(actions.isCanRemoveObjectFromFolder());
      assertTrue(actions.isCanRemovePolicy());
      assertTrue(actions.isCanSetContentStream());
      assertTrue(actions.isCanUpdateProperties());
      assertTrue(actions.isCanGetContentStream());
   }

   public void testGetAllowableActionsFolder() throws Exception
   {
      Entry folder = createFolder(testFolder, "folder");
      CmisAllowableActionsType actions = objectService.getAllowableActions(repositoryId, folder.getObjectId());
      assertTrue(actions.isCanApplyPolicy());
      assertFalse(actions.isCanAddObjectToFolder());
      assertFalse(actions.isCanCancelCheckOut());
      assertFalse(actions.isCanCheckIn());
      assertFalse(actions.isCanCheckOut());
      assertTrue(actions.isCanCreateDocument());
      assertTrue(actions.isCanCreateFolder());
      //      assertTrue(actions.isCanCreatePolicy());
      assertTrue(actions.isCanCreateRelationship());
      assertTrue(actions.isCanDeleteObject());
      assertFalse(actions.isCanDeleteContentStream());
      assertTrue(actions.isCanDeleteTree());
      assertTrue(actions.isCanGetAppliedPolicies());
      assertTrue(actions.isCanGetChildren());
      assertTrue(actions.isCanGetDescendants());
      assertTrue(actions.isCanGetFolderParent());
      assertTrue(actions.isCanGetObjectParents());
      assertTrue(actions.isCanGetProperties());
      assertTrue(actions.isCanGetObjectRelationships());
      assertTrue(actions.isCanMoveObject());
      assertFalse(actions.isCanRemoveObjectFromFolder());
      assertTrue(actions.isCanRemovePolicy());
      assertFalse(actions.isCanSetContentStream());
      assertTrue(actions.isCanUpdateProperties());
      assertFalse(actions.isCanGetContentStream());
   }

   public void testGetAllowableActionsRelationship() throws Exception
   {
      Entry source = createDocument(testFolder, "doc1", null);
      Entry target = createDocument(testFolder, "doc2", null);
      Entry rel =
         source.addRelationship("relationship1", target, repository
            .getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value()));
      CmisAllowableActionsType actions = objectService.getAllowableActions(repositoryId, rel.getObjectId());
      assertFalse(actions.isCanApplyPolicy());
      assertFalse(actions.isCanAddObjectToFolder());
      assertFalse(actions.isCanCancelCheckOut());
      assertFalse(actions.isCanCheckIn());
      assertFalse(actions.isCanCancelCheckOut());
      assertFalse(actions.isCanCreateDocument());
      assertFalse(actions.isCanCreateFolder());
      //      assertFalse(actions.isCanCreatePolicy());
      assertFalse(actions.isCanCreateRelationship());
      assertTrue(actions.isCanDeleteObject());
      assertFalse(actions.isCanDeleteContentStream());
      assertFalse(actions.isCanDeleteTree());
      assertFalse(actions.isCanGetAppliedPolicies());
      assertFalse(actions.isCanGetChildren());
      assertFalse(actions.isCanGetDescendants());
      assertFalse(actions.isCanGetFolderParent());
      assertFalse(actions.isCanGetObjectParents());
      assertTrue(actions.isCanGetProperties());
      assertFalse(actions.isCanGetObjectRelationships());
      assertFalse(actions.isCanMoveObject());
      assertFalse(actions.isCanRemoveObjectFromFolder());
      assertFalse(actions.isCanRemovePolicy());
      assertFalse(actions.isCanSetContentStream());
      assertTrue(actions.isCanUpdateProperties());
      assertFalse(actions.isCanGetContentStream());
   }

   public void testGetContentStream() throws Exception
   {
      byte[] data = "test".getBytes("UTF-8");
      ContentStream cs = new BaseContentStream(data, "test", "text/plain");
      Entry doc = createDocument(testFolder, "doc1", cs);

      ContentStream cs2 = objectService.getContentStream(repositoryId, doc.getObjectId(), null, 0, data.length);
      assertEquals("text/plain", cs2.getMediaType());
      assertEquals("test".length(), cs2.length());
      InputStream in = cs2.getStream();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      int rd = -1;
      while ((rd = in.read()) != -1)
         out.write(rd);
      assertEquals("test", new String(out.toByteArray()));
   }

   public void testGetObjectByPath() throws Exception
   {
      Entry folder1 = createFolder(testFolder, "folder");
      Entry doc = createDocument(folder1, "doc1", null);
      try
      {
         objectService.getObjectByPath(repositoryId, "/testFolder/" + folder1.getName() + "/" + doc.getName(), true,
            null, false, false, null, null);
      }
      catch (ConstraintException e)
      {
         fail("ConstraintViolationException");
      }
      catch (ObjectNotFoundException e)
      {
         fail("ObjectNotFoundException");
      }
   }

   public void testMoveObject() throws Exception
   {
      //        /
      //         |_testFolder
      //           |_source
      //           | |_folder --------< moved
      //           |   |_document ---|
      //           |_target
      Entry source = createFolder(testFolder, "source");
      Entry folder = createFolder(source, "folder");
      createDocument(folder, "doc1", null);
      String folderId = folder.getObjectId();
      Entry target = createFolder(testFolder, "target");

      objectService.moveObject(repositoryId, folder.getObjectId(), target.getObjectId(), source.getObjectId());
      assertEquals(target.getObjectId(), repository.getObjectById(folderId).getParents().get(0).getObjectId());
   }

   public void testSetContentStream() throws Exception
   {
      ContentStream cs = new BaseContentStream("test".getBytes("UTF-8"), "test", "text/plain");

      Entry doc = createDocument(testFolder, "doc1", cs);

      ContentStream cs1 = new BaseContentStream("test111".getBytes("UTF-8"), "test", "text/plain");

      CmisObjectType cmis = null;
      try
      {
         cmis = objectService.setContentStream(repositoryId, doc.getObjectId(), cs1, null, false);
         fail("ContentAlreadyExistsException must be thrown. Overwrite is not specified.");
      }
      catch (ContentAlreadyExistsException e)
      {
      }

      cmis = objectService.setContentStream(repositoryId, doc.getObjectId(), cs1, null, true);

      assertEquals(doc.getObjectId(), CmisUtils.getObjectId(cmis));
      byte[] b = new byte[128];
      int r = doc.getContent(null).getStream().read(b);
      assertEquals("test111", new String(b, 0, r));
   }

}
