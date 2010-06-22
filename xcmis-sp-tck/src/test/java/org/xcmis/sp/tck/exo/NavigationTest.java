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
import java.util.List;
import java.util.Map;

import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.ItemsTree;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.IncludeRelationships;
import org.xcmis.spi.model.ObjectParent;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.RelationshipDirection;

public class NavigationTest extends BaseTest
{

   /**
    * getChildren() test suite;
    * 
    */

   public void testGetChildrenWithRelationships() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "*", "*", "", 10, 0);
         assertEquals(6, result.getItems().size());
         int relCount = 0;
         for (CmisObject one : result.getItems())
         {
            if (one.getRelationship().size() > 0)
               relCount++;
         }
         assertEquals(3, relCount); //two relationships are present
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetChildrenWithOutRelationships() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.NONE, true, true, "*", "*", "", 10, 0);
         int relCount = 0;
         for (CmisObject one : result.getItems())
         {
            if (one.getRelationship().size() > 0)
               relCount++;
         }
         assertEquals(0, relCount); //no relationships are present
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetChildrenWithAllowableActions() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "*", "*", "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            assertNotNull(one.getAllowableActions()); //allowable actions are present
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetChildrenWithOutAllowableActions() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, false, IncludeRelationships.BOTH, true, true, "*", "*", "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            assertNull(one.getAllowableActions()); // no allowable actions are present
         }

      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetChildrenWithPathSegments() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "*", "*", "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            assertNotNull(one.getPathSegment()); //path segment is present
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetChildrenWithOutPathSegments() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, false, true, "*", "*", "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            assertNull(one.getPathSegment()); //no path segments are present
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetChildrenWithObjectInfo() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "*", "*", "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            assertNotNull(one.getObjectInfo()); //obj info is present
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetChildrenWithOutObjectInfo() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, false, "*", "*", "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            assertNull(one.getObjectInfo()); //no obj info is present
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetChildrenPropertyFiltered() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "cmis:name,cmis:path",
               "*", "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            for (Map.Entry<String, Property<?>> e : one.getProperties().entrySet())
            {
               assertTrue(e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path")); //Other props must be ignored
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetChildrenRenditionFiltered() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "", "cmis:none", "", 10,
               0);
         for (CmisObject one : result.getItems())
         {
            for (Map.Entry<String, Property<?>> e : one.getProperties().entrySet())
            {
               assertEquals(0, one.getRenditions().size());
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetChildrenOnHasMore() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "", "*", "", 2, 0);
         assertTrue(result.isHasMoreItems());
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetChildrenMaxItemsLimit() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "", "*", "", 3, 0);
         assertEquals(3, result.getItems().size());
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetChildrenNumItemsCorrect() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "", "*", "", 2, 0);
         if (result.getNumItems() == -1 || result.getNumItems() == 4)
         {
         }
         else
         {
            fail("NumItems test failed");
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetChildrenSkipCount() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "", "*", "", 10, 1);
         assertEquals(5, result.getItems().size());
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetChildrenObjectNotFoundException() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot + "11", true, IncludeRelationships.BOTH, true, true, "", "*", "", 10,
               0);
         fail();
      }
      catch (ObjectNotFoundException ex)
      {
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetChildrenInvaliArgumentException() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "", "*", "", 10, 10);
         fail();
      }
      catch (InvalidArgumentException ex)
      {
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetChildrenFilterNotValidException() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "(,*", "", "", 10, 0);
         fail();
      }
      catch (FilterNotValidException ex)
      {
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   /**
    * getDescendants() test suite;
    * 
    */

   public void testGetDescendantsSimple() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.BOTH, true, true, "", "*");
         assertEquals(9, objectTreeToList(result).size());
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetDescendantsWithAllowableActions() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.BOTH, true, true, "", "*");
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            assertNotNull(one.getAllowableActions()); //allowable actions are present
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetDescendantsWithOutAllowableActions() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, false, IncludeRelationships.BOTH, true, true, "", "*");
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            assertNull(one.getAllowableActions()); //no allowable actions are present
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetDescendantsWithRelationships() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.SOURCE, true, true, "", "*");
         List<CmisObject> list = objectTreeToList(result);
         int relCount = 0;
         for (CmisObject one : list)
         {
            if (one.getRelationship().size() > 0)
               relCount++;
         }
         assertEquals(3, relCount);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetDescendantsWithOutRelationships() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.NONE, true, true, "", "*");
         List<CmisObject> list = objectTreeToList(result);
         int relCount = 0;
         for (CmisObject one : list)
         {
            if (one.getRelationship().size() > 0)
               relCount++;
         }
         assertEquals(0, relCount);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetDescendantsWithPathSegment() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.NONE, true, true, "", "*");
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            assertNotNull(one.getPathSegment());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetDescendantsWithOutPathSegment() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.NONE, false, true, "", "*");
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            assertNull(one.getPathSegment());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetDescendantsWithObjectInfo() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.NONE, true, true, "", "*");
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            assertNotNull(one.getObjectInfo());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetDescendantsWithOutObjectInfo() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.NONE, true, false, "", "*");
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            assertNull(one.getObjectInfo());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetDescendantsPropertiesFiltered() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.NONE, true, true,
               "cmis:name,cmis:path", "*");
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            for (Map.Entry<String, Property<?>> e : one.getProperties().entrySet())
            {
               assertTrue(e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path")); //Other props must be ignored
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetDescendantsRenditionsFiltered() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.NONE, true, true, "", "cmis:none");
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            assertEquals(0, one.getRenditions().size());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetDescendantsDepthLimit() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, 2, true, IncludeRelationships.NONE, true, true, "", "cmis:none");
         List<CmisObject> list = objectTreeToList(result);
         assertEquals(8, list.size()); //skipping last level with Doc4
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetDescendantsFilterNotValidException() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection()
               .getDescendants(testroot, 2, true, IncludeRelationships.NONE, true, true, "(,*", "cmis:none");
         fail();
      }
      catch (FilterNotValidException ex)
      {

      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetDescendantsInvalidArgument() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, 0, true, IncludeRelationships.NONE, true, true, "", "*");
         fail();
      }
      catch (InvalidArgumentException ex)
      {

      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   /**
    * getFolderTree() test suite;
    * 
    */

   public void testGetFolderTreeSimple() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, true, IncludeRelationships.BOTH, true, true, "", "*");
         assertEquals(3, objectTreeToList(result).size());
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetFolderTreeWithAllowableActions() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, true, IncludeRelationships.BOTH, true, true, "", "*");
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            assertNotNull(one.getAllowableActions()); //allowable actions are present
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetFolderTreeWithOutAllowableActions() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, false, IncludeRelationships.BOTH, true, true, "", "*");
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            assertNull(one.getAllowableActions()); //no allowable actions are present
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetFolderTreeWithRelationships() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, true, IncludeRelationships.SOURCE, true, true, "", "*");
         List<CmisObject> list = objectTreeToList(result);
         int relCount = 0;
         for (CmisObject one : list)
         {
            if (one.getRelationship().size() > 0)
               relCount++;
         }
         assertEquals(1, relCount);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetFolderTreeWithOutRelationships() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, true, IncludeRelationships.NONE, true, true, "", "*");
         List<CmisObject> list = objectTreeToList(result);
         int relCount = 0;
         for (CmisObject one : list)
         {
            if (one.getRelationship().size() > 0)
               relCount++;
         }
         assertEquals(0, relCount);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetFolderTreeWithPathSegment() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, true, IncludeRelationships.NONE, true, true, "", "*");
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            assertNotNull(one.getPathSegment());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetFolderTreeWithOutPathSegment() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, true, IncludeRelationships.NONE, false, true, "", "*");
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            assertNull(one.getPathSegment());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetFolderTreeWithObjectInfo() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, true, IncludeRelationships.NONE, true, true, "", "*");
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            assertNotNull(one.getObjectInfo());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetFolderTreeWithOutObjectInfo() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, true, IncludeRelationships.NONE, true, false, "", "*");
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            assertNull(one.getObjectInfo());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetFolderTreePropertiesFiltered() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, true, IncludeRelationships.NONE, true, true,
               "cmis:name,cmis:path", "*");
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            for (Map.Entry<String, Property<?>> e : one.getProperties().entrySet())
            {
               assertTrue(e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path")); //Other props must be ignored
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetFolderTreeRenditionsFiltered() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, true, IncludeRelationships.NONE, true, true, "", "cmis:none");
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            assertEquals(0, one.getRenditions().size());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetFolderTreeDepthLimit() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, 1, true, IncludeRelationships.NONE, true, true, "", "cmis:none");
         List<CmisObject> list = objectTreeToList(result);
         assertEquals(2, list.size()); //skipping last level with Doc4
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetFolderTreeFilterNotValidException() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, 2, true, IncludeRelationships.NONE, true, true, "(,*", "cmis:none");
         fail();
      }
      catch (FilterNotValidException ex)
      {

      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetFolderTreeInvalidArgument() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, 0, true, IncludeRelationships.NONE, true, true, "", "*");
         fail();
      }
      catch (InvalidArgumentException ex)
      {

      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetFolderParentSimple() throws Exception
   {
      createFolderTree();
      try
      {
         ObjectData fold = getStorage().getObjectByPath("/testroot/folder1");
         CmisObject result = getConnection().getFolderParent(fold.getObjectId(), true, "");
         assertNotNull(result);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetFolderParentWithObjectInfo() throws Exception
   {
      createFolderTree();
      try
      {
         ObjectData fold = getStorage().getObjectByPath("/testroot/folder1");
         CmisObject result = getConnection().getFolderParent(fold.getObjectId(), true, "");
         assertNotNull(result.getObjectInfo());
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetFolderParentWithOutObjectInfo() throws Exception
   {
      createFolderTree();
      try
      {
         ObjectData fold = getStorage().getObjectByPath("/testroot/folder1");
         CmisObject result = getConnection().getFolderParent(fold.getObjectId(), false, "");
         assertNull(result.getObjectInfo());
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetFolderParentWithPropertiesFiltered() throws Exception
   {
      createFolderTree();
      try
      {
         ObjectData fold = getStorage().getObjectByPath("/testroot/folder1");
         CmisObject result = getConnection().getFolderParent(fold.getObjectId(), true, "cmis:name,cmis:path");
         for (Map.Entry<String, Property<?>> e : result.getProperties().entrySet())
         {
            assertTrue(e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path")); //Other props must be ignored
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetFolderParentFilterNotValid() throws Exception
   {
      createFolderTree();
      try
      {
         ObjectData fold = getStorage().getObjectByPath("/testroot/folder1");
         CmisObject result = getConnection().getFolderParent(fold.getObjectId(), false, "(,*");
         fail();
      }
      catch (FilterNotValidException ex)
      {
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetFolderParentInvalidArgument() throws Exception
   {
      createFolderTree();
      try
      {
         ObjectData fold = getStorage().getObjectByPath("/");
         CmisObject result = getConnection().getFolderParent(fold.getObjectId(), true, "");
         fail();
      }
      catch (InvalidArgumentException ex)
      {
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetObjectParentsSimple() throws Exception
   {
      createFolderTree();
      try
      {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), true, IncludeRelationships.BOTH, true, true, "", "*");
         assertEquals(1, result.size());
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetObjectParentsIncludeRelatioships() throws Exception
   {
      createFolderTree();
      try
      {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), true, IncludeRelationships.BOTH, true, true, "", "*");
         for (ObjectParent one : result)
         {
            assertTrue(one.getObject().getRelationship().size() > 0);
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetObjectParentsWithOutRelatioships() throws Exception
   {
      createFolderTree();
      try
      {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), true, IncludeRelationships.NONE, true, true, "", "*");
         for (ObjectParent one : result)
         {
            assertEquals(0, one.getObject().getRelationship().size());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetObjectParentsWithAllowableActions() throws Exception
   {
      createFolderTree();
      try
      {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), true, IncludeRelationships.BOTH, true, true, "", "*");
         for (ObjectParent one : result)
         {
            assertNotNull(one.getObject().getAllowableActions());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetObjectParentsWithOutAllowableActions() throws Exception
   {
      createFolderTree();
      try
      {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), false, IncludeRelationships.BOTH, true, true, "", "*");
         for (ObjectParent one : result)
         {
            assertNull(one.getObject().getAllowableActions());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetObjectParentsWithPathSegment() throws Exception
   {
      createFolderTree();
      try
      {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), true, IncludeRelationships.BOTH, true, true, "", "*");
         for (ObjectParent one : result)
         {
            assertNotNull(one.getRelativePathSegment());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetObjectParentsWithOutPathSegment() throws Exception
   {
      createFolderTree();
      try
      {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), false, IncludeRelationships.BOTH, false, true, "", "*");
         for (ObjectParent one : result)
         {
            assertNull(one.getRelativePathSegment());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetObjectParentsWithObjectInfo() throws Exception
   {
      createFolderTree();
      try
      {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), true, IncludeRelationships.BOTH, true, true, "", "*");
         for (ObjectParent one : result)
         {
            assertNotNull(one.getObject().getObjectInfo());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetObjectParentsWithOutRenditions() throws Exception
   {
      createFolderTree();
      try
      {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), true, IncludeRelationships.BOTH, true, true, "",
               "cmis:none");
         for (ObjectParent one : result)
         {
            assertEquals(0, one.getObject().getRenditions().size());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetObjectParentsWithOutObjectInfo() throws Exception
   {
      createFolderTree();
      try
      {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), false, IncludeRelationships.BOTH, true, false, "", "*");
         for (ObjectParent one : result)
         {
            assertNull(one.getObject().getObjectInfo());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetObjectParentsPropertiesFIltered() throws Exception
   {
      createFolderTree();
      try
      {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), false, IncludeRelationships.BOTH, true, false,
               "cmis:name,cmis:path", "*");
         for (ObjectParent one : result)
         {
            for (Map.Entry<String, Property<?>> e : one.getObject().getProperties().entrySet())
            {
               assertTrue(e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path")); //Other props must be ignored
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetObjectParentsFilterNotValid() throws Exception
   {
      createFolderTree();
      try
      {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection()
               .getObjectParents(doc.getObjectId(), true, IncludeRelationships.BOTH, true, true, "(,*", "*");
         fail();
      }
      catch (FilterNotValidException ex)
      {
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetObjectParentsConstraintException() throws Exception
   {
      createFolderTree();
      try
      {
         ObjectData folder = getStorage().getObjectByPath("/testroot/folder2");
         ItemsIterator<RelationshipData> it =
            folder.getRelationships(RelationshipDirection.EITHER, relationshipTypeDefinition, true);
         if (it.hasNext())
         {
            List<ObjectParent> result =
               getConnection().getObjectParents(it.next().getObjectId(), true, IncludeRelationships.BOTH, true, true,
                  "", "*");
            fail();
         }

      }
      catch (ConstraintException ex)
      {
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetCheckedOutDocsSimple() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(null, true, IncludeRelationships.BOTH, true, "", "", "", -1, 0);
         assertEquals(3, result.getItems().size());
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetCheckedOutDocsWithAllowableActions() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(null, true, IncludeRelationships.BOTH, true, "", "", "", -1, 0);
         for (CmisObject one : result.getItems())
         {
            assertNotNull(one.getAllowableActions());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetCheckedOutDocsWithOutAllowableActions() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(null, false, IncludeRelationships.BOTH, true, "", "", "", -1, 0);
         for (CmisObject one : result.getItems())
         {
            assertNull(one.getAllowableActions());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetCheckedOutDocsWithRelationships() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, false, IncludeRelationships.BOTH, true, "", "", "", -1, 0);
         boolean found = false;
         for (CmisObject one : result.getItems())
         {
            System.out.println(one.getObjectInfo().getName());
            if (one.getRelationship().size() > 0)
               found = true;
         }
         assertTrue(found);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetCheckedOutDocsWithOutRelationships() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, false, IncludeRelationships.NONE, true, "", "", "", -1, 0);
         boolean found = false;
         for (CmisObject one : result.getItems())
         {
            if (one.getRelationship().size() > 0)
               found = true;
         }
         assertFalse(found);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetCheckedOutDocsWithObjectInfo() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, false, IncludeRelationships.BOTH, true, "", "*", "", -1, 0);
         for (CmisObject one : result.getItems())
         {
            assertNotNull(one.getObjectInfo());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetCheckedOutDocsWithOutObjectInfo() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, false, IncludeRelationships.NONE, false, "", "*", "", -1, 0);
         for (CmisObject one : result.getItems())
         {
            assertNull(one.getObjectInfo());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetCheckedOutDocsWithNoRenditions() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, false, IncludeRelationships.NONE, true, "", "cmis:none", "",
               -1, 0);
         for (CmisObject one : result.getItems())
         {
            assertEquals(0, one.getRenditions().size());
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetCheckedOutDocsWithPropertyFiltered() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, false, IncludeRelationships.NONE, true, "cmis:name,cmis:path",
               "*", "", -1, 0);
         for (CmisObject one : result.getItems())
         {
            for (Map.Entry<String, Property<?>> e : one.getProperties().entrySet())
            {
               assertTrue(e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path")); //Other props must be ignored
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetCheckedOutDocsMaxItems() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, true, IncludeRelationships.BOTH, true, "", "", "", 2, 0);
         assertEquals(2, result.getItems().size());
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetCheckedOutDocsSkipCount() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, true, IncludeRelationships.BOTH, true, "", "", "", -1, 1);
         assertEquals(2, result.getItems().size());
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   public void testGetCheckedOutDocsFilterNotValidException() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, false, IncludeRelationships.NONE, true, ",*)", "*", "", -1, 0);
         fail();
      }
      catch (FilterNotValidException ex)
      {

      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }

   /// Helper methods

   @Override
   public void tearDown() throws Exception
   {
      clear();
   }
}
