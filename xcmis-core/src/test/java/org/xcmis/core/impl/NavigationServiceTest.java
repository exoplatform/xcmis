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

import org.xcmis.core.CmisObjectType;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.core.NavigationService;
import org.xcmis.core.impl.NavigationServiceImpl;
import org.xcmis.messaging.CmisObjectInFolderContainerType;
import org.xcmis.messaging.CmisObjectInFolderListType;
import org.xcmis.messaging.CmisObjectParentsType;
import org.xcmis.messaging.GetObjectParents;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.utils.CmisUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: NavigationServiceTest.java 2094 2009-07-13 06:41:44Z andrew00x
 *          $
 */
public class NavigationServiceTest extends BaseTest
{

   private NavigationService navigationService;

   public void setUp() throws Exception
   {
      super.setUp();
      navigationService = new NavigationServiceImpl(repositoryService, propertyService);
   }

   public void testGetChildren() throws Exception
   {
      Entry folder = createFolder(testFolder, "folder");
      createDocument(folder, "doc1", null);
      createDocument(folder, "doc2", null);
      createDocument(folder, "doc3", null);
      createDocument(folder, "doc4", null);
      createDocument(folder, "doc5", null);
      createFolder(folder, "folder1");
      CmisObjectInFolderListType resp =
         navigationService.getChildren(repositoryId, folder.getObjectId(), false, EnumIncludeRelationships.NONE,
            false, null, null, null, 10, 0);
      assertEquals(6, resp.getItems().size());
      assertFalse(resp.isHasMoreItems());
   }

   public void testGetChildrenSkip() throws Exception
   {
      Entry folder = createFolder(testFolder, "folder");
      createDocument(folder, "doc1", null);
      createDocument(folder, "doc2", null);
      createDocument(folder, "doc3", null);
      createDocument(folder, "doc4", null);
      createDocument(folder, "doc5", null);
      CmisObjectInFolderListType resp =
         navigationService.getChildren(repositoryId, folder.getObjectId(), false, EnumIncludeRelationships.NONE,
            false, null, null, null, 10, 3);
      assertEquals(2, resp.getItems().size());
      assertFalse(resp.isHasMoreItems());
   }

   public void testGetChildrenMaxItems() throws Exception
   {
      Entry folder = createFolder(testFolder, "folder");
      createDocument(folder, "doc1", null);
      createDocument(folder, "doc2", null);
      createDocument(folder, "doc3", null);
      createDocument(folder, "doc4", null);
      createDocument(folder, "doc5", null);

      CmisObjectInFolderListType resp =
         navigationService.getChildren(repositoryId, folder.getObjectId(), false, EnumIncludeRelationships.NONE,
            false, null, null, null, 2, 0);
      assertEquals(2, resp.getItems().size());
      assertTrue(resp.isHasMoreItems());
   }

   public void testGetDescendents() throws Exception
   {
      Entry folder = createFolder(testFolder, "folder");
      Entry doc1 = createDocument(folder, "doc1", null);
      Entry doc2 = createDocument(folder, "doc2", null);
      Entry doc3 = createDocument(folder, "doc3", null);

      Entry folder1 = createFolder(folder, "folder1");
      Entry doc11 = createDocument(folder1, "doc4", null);
      Entry doc12 = createDocument(folder1, "doc5", null);
      Entry doc13 = createDocument(folder1, "doc6", null);
      List<CmisObjectInFolderContainerType> resp =
         navigationService.getDescendants(repositoryId, testFolderId, -1, false, EnumIncludeRelationships.NONE,
            false, null, null);
      assertEquals(1, resp.size()); // folder
      CmisObjectInFolderContainerType folderCont = resp.get(0);
      assertEquals(folder.getObjectId(), CmisUtils.getObjectId(folderCont.getObjectInFolder().getObject()));
      List<CmisObjectInFolderContainerType> children = folderCont.getChildren();
      assertEquals(4, children.size()); // doc1, doc2, doc3, folder1
      List<String> expected =
         Arrays.asList(doc1.getObjectId(), folder1.getObjectId(), doc2.getObjectId(), doc3.getObjectId());
      assertTrue(expected.contains(CmisUtils.getObjectId(children.get(0).getObjectInFolder().getObject())));
      assertTrue(expected.contains(CmisUtils.getObjectId(children.get(1).getObjectInFolder().getObject())));
      assertTrue(expected.contains(CmisUtils.getObjectId(children.get(2).getObjectInFolder().getObject())));
      assertTrue(expected.contains(CmisUtils.getObjectId(children.get(3).getObjectInFolder().getObject())));
      for (CmisObjectInFolderContainerType item : children)
      {
         String objectId = CmisUtils.getObjectId(item.getObjectInFolder().getObject());
         if (objectId.equals(folder1.getObjectId()))
         {
            children = item.getChildren();
            assertEquals(3, children.size());
            expected = Arrays.asList(doc11.getObjectId(), doc12.getObjectId(), doc13.getObjectId());
            assertTrue(expected.contains(CmisUtils.getObjectId(children.get(0).getObjectInFolder().getObject())));
            assertTrue(expected.contains(CmisUtils.getObjectId(children.get(1).getObjectInFolder().getObject())));
            assertTrue(expected.contains(CmisUtils.getObjectId(children.get(2).getObjectInFolder().getObject())));
         }
      }
      resp =
         navigationService.getDescendants(repositoryId, testFolderId, 1, false, EnumIncludeRelationships.NONE,
            false, null, null);
      assertEquals(1, resp.size());
      assertEquals(0, resp.get(0).getChildren().size());
   }

   public void testGetObjectParents() throws Exception
   {
      Entry folder = createFolder(testFolder, "folder");
      Entry document = createDocument(folder, "doc1", null);
      GetObjectParents req = new GetObjectParents();
      req.setRepositoryId(repositoryId);
      req.setObjectId(document.getObjectId());
      //
      List<CmisObjectParentsType> objectParents =
         navigationService.getObjectParents(repositoryId, document.getObjectId(), false,
            EnumIncludeRelationships.NONE, false, null, null);
      assertEquals(1, objectParents.size());
      CmisObjectType cmis = objectParents.get(0).getObject();
      assertEquals(folder.getObjectId(), CmisUtils.getObjectId(cmis));
   }

   public void testGetFolderParent() throws Exception
   {
      Entry folder = createFolder(testFolder, "folder");
      CmisObjectType parent = navigationService.getFolderParent(repositoryId, folder.getObjectId(), null);
      assertEquals(testFolder.getObjectId(), CmisUtils.getObjectId(parent));
   }

   public void testGetFolderTree() throws Exception
   {
      Entry folder = createFolder(testFolder, "folder");
      Entry folder1 = createFolder(folder, "folder1");
      Entry folder2 = createFolder(folder, "folder2");
      Entry folder3 = createFolder(folder, "folder3");
      createDocument(folder, "doc1", null); // must not be in result
      Entry folder4 = createFolder(folder2, "folder4");
      Entry folder5 = createFolder(folder3, "folder5");
      List<CmisObjectInFolderContainerType> list =
         navigationService.getFolderTree(repositoryId, folder.getObjectId(), -1, false,
            EnumIncludeRelationships.NONE, false, null, null);
      List<String> expected = Arrays.asList(folder1.getObjectId(), folder2.getObjectId(), folder3.getObjectId());
      assertEquals(3, list.size()); // folder1, folder2, folder3
      assertTrue(expected.contains(CmisUtils.getObjectId(list.get(0).getObjectInFolder().getObject())));
      assertTrue(expected.contains(CmisUtils.getObjectId(list.get(1).getObjectInFolder().getObject())));
      assertTrue(expected.contains(CmisUtils.getObjectId(list.get(2).getObjectInFolder().getObject())));
      expected = Arrays.asList(folder4.getObjectId(), folder5.getObjectId());
      for (CmisObjectInFolderContainerType one : list)
      {
         if (!one.getChildren().isEmpty())
         {
            assertEquals(1, one.getChildren().size()); // folder4 or folder5
            assertTrue(expected.contains(CmisUtils.getObjectId(one.getChildren().get(0).getObjectInFolder().getObject())));
         }
      }
   }

}
