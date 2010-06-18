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

import java.util.Map;

import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsList;
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

   public void testGetChildrenNoRenditions() throws Exception
   {
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "*", "cmis:none", "",
               10, 0);
         for (CmisObject one : result.getItems())
         {
            assertEquals(0, one.getRenditions().size()); //no renditions are present
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
         if (result.getNumItems() == -1 || result.getNumItems() == 4){
         }else{
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
            getConnection().getChildren(testroot+"11", true, IncludeRelationships.BOTH, true, true, "", "*", "", 10, 0);
         fail();
      }
      catch (ObjectNotFoundException ex)
      {
      }
      catch (Exception e){
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
      catch (Exception e){
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
      catch (Exception e){
         e.printStackTrace();
         fail(e.getMessage());
      }
   }
   
   @Override
   public void tearDown() throws Exception
   {
      clear();
   }
}
