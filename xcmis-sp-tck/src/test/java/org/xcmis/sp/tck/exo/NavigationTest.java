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

import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.ItemsTree;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.IncludeRelationships;
import org.xcmis.spi.model.Property;

public class NavigationTest extends BaseTest
{
   public void testGetChildrenWithRelationships() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "*", "*", "", 10, 0);
         assertEquals(4, result.getItems().size());
         int relCount = 0;
         for (CmisObject one : result.getItems())
         {
            if (one.getRelationship().size() > 0)
               relCount++;
         }
         assertEquals(2, relCount); //two relationships are present
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
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "", "cmis:none", "", 10, 0);
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
         assertEquals(3, result.getItems().size());
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

   public void testGetDescendantsSimple() throws Exception
   {
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.BOTH, true, true, "", "*");
         assertEquals(7, objectTreeToList(result).size());
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
         assertEquals(2, relCount);
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
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.NONE, true, true, "cmis:name,cmis:path", "*");
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
         assertEquals(6, list.size()); //skipping last level with Doc4
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
            getConnection().getDescendants(testroot, 2, true, IncludeRelationships.NONE, true, true, "(,*", "cmis:none");
         fail();
      }catch (FilterNotValidException ex){
         
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
      }catch (InvalidArgumentException ex){
         
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
   }
   
   
   /// Helper methods
   public List<CmisObject> objectTreeToList(List<ItemsTree<CmisObject>> source)
   {
      List<CmisObject> result = new ArrayList<CmisObject>();
      for (ItemsTree<CmisObject> one : source)
      {
         CmisObject type = one.getContainer();
         if (one.getChildren() != null)
         {
            result.addAll(objectTreeToList(one.getChildren()));
         }
         result.add(type);
      }

      return result;
   }

   @Override
   public void tearDown() throws Exception
   {
      clear();
   }
}
