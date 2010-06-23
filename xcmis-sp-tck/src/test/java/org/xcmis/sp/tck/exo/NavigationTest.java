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
      System.out.print("Running testGetChildrenWithRelationships....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetChildrenWithOutRelationships() throws Exception
   {
      System.out.print("Running testGetChildrenWithOutRelationships....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetChildrenWithAllowableActions() throws Exception
   {
      System.out.print("Running testGetChildrenWithAllowableActions....");
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "*", "*", "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            assertNotNull(one.getAllowableActions()); //allowable actions are present
         }
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetChildrenWithOutAllowableActions() throws Exception
   {
      System.out.print("Running testGetChildrenWithOutAllowableActions....");
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, false, IncludeRelationships.BOTH, true, true, "*", "*", "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            assertNull(one.getAllowableActions()); // no allowable actions are present
         }
       pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetChildrenWithPathSegments() throws Exception
   {
      System.out.print("Running testGetChildrenWithPathSegments....");
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "*", "*", "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            assertNotNull(one.getPathSegment()); //path segment is present
         }
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetChildrenWithOutPathSegments() throws Exception
   {
      System.out.print("Running testGetChildrenWithOutPathSegments....");
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, false, true, "*", "*", "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            assertNull(one.getPathSegment()); //no path segments are present
         }
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetChildrenWithObjectInfo() throws Exception
   {
      System.out.print("Running testGetChildrenWithObjectInfo....");
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "*", "*", "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            assertNotNull(one.getObjectInfo()); //obj info is present
         }
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetChildrenWithOutObjectInfo() throws Exception
   {
      System.out.print("Running testGetChildrenWithOutObjectInfo....");
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, false, "*", "*", "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            assertNull(one.getObjectInfo()); //no obj info is present
         }
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetChildrenPropertyFiltered() throws Exception
   {
      System.out.print("Running testGetChildrenPropertyFiltered....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetChildrenRenditionFiltered() throws Exception
   {
      System.out.print("Running testGetChildrenRenditionFiltered....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetChildrenOnHasMore() throws Exception
   {
      System.out.print("Running testGetChildrenOnHasMore....");
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "", "*", "", 2, 0);
         assertTrue(result.isHasMoreItems());
         pass();
      }
      catch (Exception e)
      {
          
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetChildrenMaxItemsLimit() throws Exception
   {
      System.out.print("Running testGetChildrenMaxItemsLimit....");
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "", "*", "", 3, 0);
         assertEquals(3, result.getItems().size());
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetChildrenNumItemsCorrect() throws Exception
   {
      System.out.print("Running testGetChildrenNumItemsCorrect....");
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "", "*", "", 2, 0);
         if (result.getNumItems() == -1 || result.getNumItems() == 4)
         {
            pass();
         }
         else
         {
            doFail("NumItems test failed");
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetChildrenSkipCount() throws Exception
   {
      System.out.print("Running testGetChildrenSkipCount....");
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "", "*", "", 10, 1);
         assertEquals(5, result.getItems().size());
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetChildrenObjectNotFoundException() throws Exception
   {
      System.out.print("Running testGetChildrenObjectNotFoundException....");
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot + "11", true, IncludeRelationships.BOTH, true, true, "", "*", "", 10,
               0);
          
         doFail();
      }
      catch (ObjectNotFoundException ex)
      {
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetChildrenInvaliArgumentException() throws Exception
   {
      System.out.print("Running testGetChildrenInvaliArgumentException....");
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "", "*", "", 10, 10);
          
         doFail();
      }
      catch (InvalidArgumentException ex)
      {
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetChildrenFilterNotValidException() throws Exception
   {
      System.out.print("Running testGetChildrenFilterNotValidException....");
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "(,*", "", "", 10, 0);
          
         doFail();
      }
      catch (FilterNotValidException ex)
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
    * getDescendants() test suite;
    * 
    */

   public void testGetDescendantsSimple() throws Exception
   {
      System.out.print("Running testGetDescendantsSimple....");
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.BOTH, true, true, "", "*");
         assertEquals(9, objectTreeToList(result).size());
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetDescendantsWithAllowableActions() throws Exception
   {
      System.out.print("Running testGetDescendantsWithAllowableActions....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetDescendantsWithOutAllowableActions() throws Exception
   {
      System.out.print("Running testGetDescendantsWithOutAllowableActions....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetDescendantsWithRelationships() throws Exception
   {
      System.out.print("Running testGetDescendantsWithRelationships....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetDescendantsWithOutRelationships() throws Exception
   {
      System.out.print("Running testGetDescendantsWithOutRelationships....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetDescendantsWithPathSegment() throws Exception
   {
      System.out.print("Running testGetDescendantsWithPathSegment....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetDescendantsWithOutPathSegment() throws Exception
   {
      System.out.print("Running testGetDescendantsWithOutPathSegment....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetDescendantsWithObjectInfo() throws Exception
   {
      System.out.print("Running testGetDescendantsWithObjectInfo....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetDescendantsWithOutObjectInfo() throws Exception
   {
      System.out.print("Running testGetDescendantsWithOutObjectInfo....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetDescendantsPropertiesFiltered() throws Exception
   {
      System.out.print("Running testGetDescendantsPropertiesFiltered....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetDescendantsRenditionsFiltered() throws Exception
   {
      System.out.print("Running testGetDescendantsRenditionsFiltered....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetDescendantsDepthLimit() throws Exception
   {
      System.out.print("Running testGetDescendantsDepthLimit....");
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, 2, true, IncludeRelationships.NONE, true, true, "", "cmis:none");
         List<CmisObject> list = objectTreeToList(result);
         assertEquals(8, list.size()); //skipping last level with Doc4
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetDescendantsFilterNotValidException() throws Exception
   {
      System.out.print("Running testGetDescendantsFilterNotValidException....");
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection()
               .getDescendants(testroot, 2, true, IncludeRelationships.NONE, true, true, "(,*", "cmis:none");
         doFail();
      }
      catch (FilterNotValidException ex)
      {
        pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetDescendantsInvalidArgument() throws Exception
   {
      System.out.print("Running testGetDescendantsInvalidArgument....");
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, 0, true, IncludeRelationships.NONE, true, true, "", "*");
         doFail();
      }
      catch (InvalidArgumentException ex)
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
    * getFolderTree() test suite;
    * 
    */

   public void testGetFolderTreeSimple() throws Exception
   {
      System.out.print("Running testGetFolderTreeSimple....");
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, true, IncludeRelationships.BOTH, true, true, "", "*");
         assertEquals(3, objectTreeToList(result).size());
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetFolderTreeWithAllowableActions() throws Exception
   {
      System.out.print("Running testGetFolderTreeWithAllowableActions....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetFolderTreeWithOutAllowableActions() throws Exception
   {
      System.out.print("Running testGetFolderTreeWithOutAllowableActions....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetFolderTreeWithRelationships() throws Exception
   {
      System.out.print("Running testGetFolderTreeWithRelationships....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetFolderTreeWithOutRelationships() throws Exception
   {
      System.out.print("Running testGetFolderTreeWithOutRelationships....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetFolderTreeWithPathSegment() throws Exception
   {
      System.out.print("Running testGetFolderTreeWithPathSegment....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetFolderTreeWithOutPathSegment() throws Exception
   {
      System.out.print("Running testGetFolderTreeWithOutPathSegment....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetFolderTreeWithObjectInfo() throws Exception
   {
      System.out.print("Running testGetFolderTreeWithObjectInfo....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetFolderTreeWithOutObjectInfo() throws Exception
   {
      System.out.print("Running testGetFolderTreeWithOutObjectInfo....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetFolderTreePropertiesFiltered() throws Exception
   {
      System.out.print("Running testGetFolderTreePropertiesFiltered....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetFolderTreeRenditionsFiltered() throws Exception
   {
      System.out.print("Running testGetFolderTreeRenditionsFiltered....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetFolderTreeDepthLimit() throws Exception
   {
      System.out.print("Running testGetFolderTreeDepthLimit....");
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, 1, true, IncludeRelationships.NONE, true, true, "", "cmis:none");
         List<CmisObject> list = objectTreeToList(result);
         assertEquals(2, list.size()); //skipping last level with Doc4
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetFolderTreeFilterNotValidException() throws Exception
   {
      System.out.print("Running testGetFolderTreeFilterNotValidException....");
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, 2, true, IncludeRelationships.NONE, true, true, "(,*", "cmis:none");
         doFail();
      }
      catch (FilterNotValidException ex)
      {
        pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetFolderTreeInvalidArgument() throws Exception
   {
      System.out.print("Running testGetFolderTreeInvalidArgument....");
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, 0, true, IncludeRelationships.NONE, true, true, "", "*");
         doFail();
      }
      catch (InvalidArgumentException ex)
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
    * getFolderParent() test suite;
    * 
    */
   public void testGetFolderParentSimple() throws Exception
   {
      System.out.print("Running testGetFolderParentSimple....");
      createFolderTree();
      try
      {
         ObjectData fold = getStorage().getObjectByPath("/testroot/folder1");
         CmisObject result = getConnection().getFolderParent(fold.getObjectId(), true, "");
         assertNotNull(result);
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetFolderParentWithObjectInfo() throws Exception
   {
      System.out.print("Running testGetFolderParentWithObjectInfo....");
      createFolderTree();
      try
      {
         ObjectData fold = getStorage().getObjectByPath("/testroot/folder1");
         CmisObject result = getConnection().getFolderParent(fold.getObjectId(), true, "");
         assertNotNull(result.getObjectInfo());
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetFolderParentWithOutObjectInfo() throws Exception
   {
      System.out.print("Running testGetFolderParentWithOutObjectInfo....");
      createFolderTree();
      try
      {
         ObjectData fold = getStorage().getObjectByPath("/testroot/folder1");
         CmisObject result = getConnection().getFolderParent(fold.getObjectId(), false, "");
         assertNull(result.getObjectInfo());
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetFolderParentWithPropertiesFiltered() throws Exception
   {
      System.out.print("Running testGetFolderParentWithPropertiesFiltered....");
      createFolderTree();
      try
      {
         ObjectData fold = getStorage().getObjectByPath("/testroot/folder1");
         CmisObject result = getConnection().getFolderParent(fold.getObjectId(), true, "cmis:name,cmis:path");
         for (Map.Entry<String, Property<?>> e : result.getProperties().entrySet())
         {
            assertTrue(e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path")); //Other props must be ignored
         }
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetFolderParentFilterNotValid() throws Exception
   {
      System.out.print("Running testGetFolderParentFilterNotValid....");
      createFolderTree();
      try
      {
         ObjectData fold = getStorage().getObjectByPath("/testroot/folder1");
         CmisObject result = getConnection().getFolderParent(fold.getObjectId(), false, "(,*");
         doFail();
      }
      catch (FilterNotValidException ex)
      {
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetFolderParentInvalidArgument() throws Exception
   {
      System.out.print("Running testGetFolderParentInvalidArgument....");
      createFolderTree();
      try
      {
         ObjectData fold = getStorage().getObjectByPath("/");
         CmisObject result = getConnection().getFolderParent(fold.getObjectId(), true, "");
         doFail();
      }
      catch (InvalidArgumentException ex)
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
    * getObjectParent() test suite;
    * 
    */
   public void testGetObjectParentsSimple() throws Exception
   {
      System.out.print("Running testGetObjectParentsSimple....");
      createFolderTree();
      try
      {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), true, IncludeRelationships.BOTH, true, true, "", "*");
         assertEquals(1, result.size());
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetObjectParentsIncludeRelatioships() throws Exception
   {
      System.out.print("Running testGetObjectParentsIncludeRelatioships....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetObjectParentsWithOutRelatioships() throws Exception
   {
      System.out.print("Running testGetObjectParentsWithOutRelatioships....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetObjectParentsWithAllowableActions() throws Exception
   {
      System.out.print("Running testGetObjectParentsWithAllowableActions....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetObjectParentsWithOutAllowableActions() throws Exception
   {
      System.out.print("Running testGetObjectParentsWithOutAllowableActions....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetObjectParentsWithPathSegment() throws Exception
   {
      System.out.print("Running testGetObjectParentsWithPathSegment....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetObjectParentsWithOutPathSegment() throws Exception
   {
      System.out.print("Running testGetObjectParentsWithOutPathSegment....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetObjectParentsWithObjectInfo() throws Exception
   {
      System.out.print("Running testGetObjectParentsWithObjectInfo....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetObjectParentsWithOutRenditions() throws Exception
   {
      System.out.print("Running testGetObjectParentsWithOutRenditions....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetObjectParentsWithOutObjectInfo() throws Exception
   {
      System.out.print("Running testGetObjectParentsWithOutObjectInfo....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetObjectParentsPropertiesFIltered() throws Exception
   {
      System.out.print("Running testGetObjectParentsPropertiesFIltered....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetObjectParentsFilterNotValid() throws Exception
   {
      System.out.print("Running testGetObjectParentsFilterNotValid....");
      createFolderTree();
      try
      {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection()
               .getObjectParents(doc.getObjectId(), true, IncludeRelationships.BOTH, true, true, "(,*", "*");
         doFail();
      }
      catch (FilterNotValidException ex)
      {
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetObjectParentsConstraintException() throws Exception
   {
      System.out.print("Running testGetObjectParentsConstraintException....");
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
            doFail();
         }

      }
      catch (ConstraintException ex)
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
    * getCheckedOutDocs() test suite;
    * 
    */
   public void testGetCheckedOutDocsSimple() throws Exception
   {
      System.out.print("Running testGetCheckedOutDocsSimple....");
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(null, true, IncludeRelationships.BOTH, true, "", "", "", -1, 0);
         assertEquals(3, result.getItems().size());
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetCheckedOutDocsWithAllowableActions() throws Exception
   {
      System.out.print("Running testGetCheckedOutDocsWithAllowableActions....");
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(null, true, IncludeRelationships.BOTH, true, "", "", "", -1, 0);
         for (CmisObject one : result.getItems())
         {
            assertNotNull(one.getAllowableActions());
         }
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetCheckedOutDocsWithOutAllowableActions() throws Exception
   {
      System.out.print("Running testGetCheckedOutDocsWithOutAllowableActions....");
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(null, false, IncludeRelationships.BOTH, true, "", "", "", -1, 0);
         for (CmisObject one : result.getItems())
         {
            assertNull(one.getAllowableActions());
         }
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetCheckedOutDocsWithRelationships() throws Exception
   {
      System.out.print("Running testGetCheckedOutDocsWithRelationships....");
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, false, IncludeRelationships.BOTH, true, "", "", "", -1, 0);
         boolean found = false;
         for (CmisObject one : result.getItems())
         {
            if (one.getRelationship().size() > 0)
               found = true;
         }
         assertTrue(found);
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetCheckedOutDocsWithOutRelationships() throws Exception
   {
      System.out.print("Running testGetCheckedOutDocsWithOutRelationships....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetCheckedOutDocsWithObjectInfo() throws Exception
   {
      System.out.print("Running testGetCheckedOutDocsWithObjectInfo....");
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, false, IncludeRelationships.BOTH, true, "", "*", "", -1, 0);
         for (CmisObject one : result.getItems())
         {
            assertNotNull(one.getObjectInfo());
         }
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetCheckedOutDocsWithOutObjectInfo() throws Exception
   {
      System.out.print("Running testGetCheckedOutDocsWithOutObjectInfo....");
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, false, IncludeRelationships.NONE, false, "", "*", "", -1, 0);
         for (CmisObject one : result.getItems())
         {
            assertNull(one.getObjectInfo());
         }
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetCheckedOutDocsWithNoRenditions() throws Exception
   {
      System.out.print("Running testGetCheckedOutDocsWithNoRenditions....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetCheckedOutDocsWithPropertyFiltered() throws Exception
   {
      System.out.print("Running testGetCheckedOutDocsWithPropertyFiltered....");
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
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetCheckedOutDocsMaxItems() throws Exception
   {
      System.out.print("Running testGetCheckedOutDocsMaxItems....");
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, true, IncludeRelationships.BOTH, true, "", "", "", 2, 0);
         assertEquals(2, result.getItems().size());
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetCheckedOutDocsSkipCount() throws Exception
   {
      System.out.print("Running testGetCheckedOutDocsSkipCount....");
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, true, IncludeRelationships.BOTH, true, "", "", "", -1, 1);
         assertEquals(2, result.getItems().size());
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
   }

   public void testGetCheckedOutDocsFilterNotValidException() throws Exception
   {
      System.out.print("Running testGetCheckedOutDocsFilterNotValidException....");
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, false, IncludeRelationships.NONE, true, ",*)", "*", "", -1, 0);
         doFail();
      }
      catch (FilterNotValidException ex)
      {
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
