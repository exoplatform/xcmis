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

package org.xcmis.spi.tck;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.ItemsTree;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.PropertyFilter;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.RenditionFilter;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.IncludeRelationships;
import org.xcmis.spi.model.ObjectParent;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.RelationshipDirection;
import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class NavigationTest extends BaseTest
{

   static String testroot;

   @BeforeClass
   public static void start() throws Exception
   {
      BaseTest.setUp();
      testroot = BaseTest.createFolderTree();
   }

   @Test
   public void testGetChildren_Relationships() throws Exception
   {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", 10, 0);
         assertTrue("Unexpected items number;", result.getItems().size() == 6);
         int relCount = 0;
         if (IS_RELATIONSHIPS_SUPPORTED)
         {
            for (CmisObject one : result.getItems())
            {
               if (one.getRelationship().size() > 0)
                  relCount++;
            }
            assertTrue("Unexpected items number;", relCount == 3);//three relationships are present
         }
   }

   /**
    * 2.2.3.1.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   @Test
   public void testGetChildren_NoRelationships() throws Exception
   {
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.NONE, true, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", 10, 0);
         int relCount = 0;
         for (CmisObject one : result.getItems())
         {
            if (one.getRelationship().size() > 0)
               relCount++;
         }
         assertTrue("Unexpected items number;", relCount == 0); //no relationships are present
   }

   /**
    * 2.2.3.1.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set. 
    * @throws Exception
    */
   @Test
   public void testGetChildren_AllowableActions() throws Exception
   {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            assertNotNull("Allowable actions must be present in result;", one.getAllowableActions()); //allowable actions are present
         }
   }

   /**
    * 2.2.3.1.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set. 
    * @throws Exception
    */
   @Test
   public void testGetChildren_NoAllowableActions() throws Exception
   {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, false, IncludeRelationships.BOTH, true, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            assertNull(one.getAllowableActions()); //allowable actions are not present
         }
   }

   /**
    * 2.2.3.1.1
    * If TRUE, returns a PathSegment for each child object for use in constructing that object’s path.
    * @throws Exception
    */
   @Test
   public void testGetChildren_PathSegments() throws Exception
   {
      String testname = "testGetChildren_PathSegments";
      System.out.print("Running " + testname + "....                                   ");
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            assertNotNull("Path segment must be present in result", one.getPathSegment()); //path segment is present
         }
   }

   /**
    * 2.2.3.1.1
    * If TRUE, returns a PathSegment for each child object for use in constructing that object’s path.
    * @throws Exception
    */
   @Test
   public void testGetChildren_NoPathSegments() throws Exception
   {
      String testname = "testGetChildren_NoPathSegments";
      System.out.print("Running " + testname + "....                                 ");
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, false, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            assertNull("Path segment must not be present in result", one.getPathSegment()); //no path segments are present
         }
   }

   /**
    * 2.2.3.1.1
    * If TRUE, returns a object info  for each child object.s
    * @throws Exception
    */
   @Test
   public void testGetChildren_ObjectInfo() throws Exception
   {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            assertNotNull("ObjectInfo must be present in result", one.getObjectInfo()); //obj info is present
         }
   }

   @Test
   public void testGetChildren_NoObjectInfo() throws Exception
   {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, false, PropertyFilter.ALL,
               RenditionFilter.ANY, "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            assertNull(one.getObjectInfo()); // no obj info present
         }
   }

   /**
    * 2.2.3.1.1
    * Repositories SHOULD return only the properties specified in the property filter 
    * if they exist on the object’s type definition.
    * @throws Exception
    */
   @Test
   public void testGetChildren_PropertyFiltered() throws Exception
   {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "cmis:name,cmis:path",
               RenditionFilter.NONE, "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            for (Map.Entry<String, Property<?>> e : one.getProperties().entrySet())
            {
               if (e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path")) //Other props must be ignored
                  continue;
               else
                 fail("Property filter works incorrect;");
            }
         }
   }

   /**
    * 2.2.3.1.1
    * The Repository MUST return the set of renditions whose kind matches this filter.
    * @throws Exception
    */
   @Test
   public void testGetChildren_RenditionFiltered() throws Exception
   {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, PropertyFilter.ALL,
               RenditionFilter.NONE, "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            for (Map.Entry<String, Property<?>> e : one.getProperties().entrySet())
            {
               assertTrue("Rendition filter works incorrect;", one.getRenditions().size() == 0);
            }
         }
          
      
   }

   /**
    * 2.2.3.1.1
    * TRUE if the Repository contains additional items after those contained in the response.  FALSE otherwise.
    * @throws Exception
    */
   @Test
   public void testGetChildren_HasMoreItems() throws Exception
   {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", 2, 0);
         if (!result.isHasMoreItems())
            fail("Has more items property is incorrect;");
   }

   /**
    * 2.2.3.1.1.
    * This is the maximum number of items to return in a response.  The repository MUST NOT exceed this maximum.
    * @throws Exception
    */
   @Test
   public void testGetChildren_MaxItems() throws Exception
   {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", 3, 0);
         assertTrue("Items number is incorrect;", result.getItems().size() == 3);
   }

   /**
    * 2.2.3.1.1.
    * If the repository knows the total number of items in a result set, the repository SHOULD include the number here. 
    * If the repository does not know the number of items in a result set, this parameter SHOULD not be set.
    * @throws Exception
    */
   @Test
   public void testGetChildren_NumItems() throws Exception
   {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", 2, 0);
         assertTrue("NumItems test failed;", result.getNumItems() == -1 || result.getNumItems() == 6);
   }

   /**
    * 2.2.3.1.1.
    * This is the number of potential results that the repository MUST skip/page over before returning any results.
    * @throws Exception
    */
   @Test
   public void testGetChildren_SkipCount() throws Exception
   {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", 10, 1);
         assertTrue("Items number is incorrect;", result.getItems().size()  == 5);
   }

   /**
    * 2.2.3.1.3
    * The Repository MUST throw this exception if the specified folder is not a folder.
    * @throws Exception
    */
   @Test
   public void testGetChildren_InvalidArgumentException() throws Exception
   {
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(getStorage().getObjectByPath("/testroot/doc1").getObjectId(), true,
               IncludeRelationships.BOTH, true, true, PropertyFilter.ALL, RenditionFilter.ANY, "", 10, 10);
         fail("InvalidArgumentException must be thrown;");
      }
      catch (InvalidArgumentException ex)
      {
          
      }
   }

   /**
    * 2.2.3.1.3
    * The Repository MUST throw this exception if this property filter input parameter is not valid.
    * @throws Exception
    */
   @Test
   public void testGetChildren_FilterNotValidException() throws Exception
   {
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "(,*",
               RenditionFilter.NONE, "", 10, 0);
         fail("FilterNotValidException must be thrown;");
      }
      catch (FilterNotValidException ex)
      {
          
      }
   }

   /**
    * 2.2.3.2
    * Gets the set of descendant objects contained in the specified folder or any of its child-folders.
    * @throws Exception
    */
   @Test
   public void testGetDescendants_Simple() throws Exception
   {
      if (!IS_CAPABILITY_DESCENDANTS)
      {
         //SKIP
         return;
      }
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.BOTH, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         assertTrue("Items number is incorrect;", objectTreeToList(result).size() == 9);
   }

   /**
    * 2.2.3.2.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set. 
    * @throws Exception
    */
   @Test
   public void testGetDescendants_AllowableActions() throws Exception
   {
      if (!IS_CAPABILITY_DESCENDANTS)
      {
         //SKIP
         return;
      }
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.BOTH, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            assertNotNull("Allowable actions must be present in result;", one.getAllowableActions()); //allowable actions are present
         }
   }

   /**
    * 2.2.3.2.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set. 
    * @throws Exception
    */
   @Test
   public void testGetDescendants_NoAllowableActions() throws Exception
   {
      if (!IS_CAPABILITY_DESCENDANTS)
      {
         //SKIP
         return;
      }
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, false, IncludeRelationships.BOTH, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            assertNull("Allowable actions must not be present in result;", one.getAllowableActions()); //allowable actions are not present
         }
   }

   /**
    * 2.2.3.2.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   @Test
   public void testGetDescendants_Relationships() throws Exception
   {
      if (!IS_CAPABILITY_DESCENDANTS)
      {
         //SKIP
         return;
      }
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.SOURCE, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         List<CmisObject> list = objectTreeToList(result);
         int relCount = 0;
         for (CmisObject one : list)
         {
            if (one.getRelationship().size() > 0)
               relCount++;
         }
         assertTrue("Items number is incorrect;", relCount == 3);
   }

   /**
    * 2.2.3.2.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   @Test
   public void testGetDescendants_NoRelationships() throws Exception
   {
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.NONE, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         List<CmisObject> list = objectTreeToList(result);
         int relCount = 0;
         for (CmisObject one : list)
         {
            if (one.getRelationship().size() > 0)
               relCount++;
         }
         assertTrue("Items number is incorrect;", relCount == 0);
   }

   /**
    * 2.2.3.2.1
    * If TRUE, returns a PathSegment for each child object for use in constructing that object’s path.
    * @throws Exception
    */
   @Test
   public void testGetDescendants_PathSegment() throws Exception
   {
      if (!IS_CAPABILITY_DESCENDANTS)
      {
         //SKIP
         return;
      }
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.NONE, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            assertNotNull(one.getPathSegment());
         }
   }

   @Test
   public void testGetDescendants_ObjectInfo() throws Exception
   {
      String testname = "testGetDescendants_ObjectInfo";
      System.out.print("Running " + testname + "....                                  ");
      if (!IS_CAPABILITY_DESCENDANTS)
      {
         //SKIP
         return;
      }
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.NONE, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            assertNotNull("Object info must be present in result;", one.getObjectInfo());
         }
   }

   @Test
   public void testGetDescendants_NoObjectInfo() throws Exception
   {
      if (!IS_CAPABILITY_DESCENDANTS)
      {
         //SKIP
         return;
      }
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.NONE, true, false,
               PropertyFilter.ALL, RenditionFilter.ANY);
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            assertNull("Object info must not be present in result;", one.getObjectInfo());
         }
   }

   /**
    * 2.2.3.2.1
    * Repositories SHOULD return only the properties specified in the property filter 
    * if they exist on the object’s type definition.
    * @throws Exception
    */
   @Test
   public void testGetDescendants_PropertyFiltered() throws Exception
   {
      if (!IS_CAPABILITY_DESCENDANTS)
      {
         //SKIP
         return;
      }
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.NONE, true, true,
               "cmis:name,cmis:path", RenditionFilter.ANY);
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            for (Map.Entry<String, Property<?>> e : one.getProperties().entrySet())
            {
               if (e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path")) //Other props must be ignored
                  continue;
               else
                  fail("Property filter works incorrect;");
            }
         }
   }

   /**
    * 2.2.3.2.1
    * The Repository MUST return the set of renditions whose kind matches this filter. 
    * @throws Exception
    */
   @Test
   public void testGetDescendants_RenditionsFiltered() throws Exception
   {
      if (!IS_CAPABILITY_DESCENDANTS)
      {
         //SKIP
         return;
      }
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.NONE, true, true,
               PropertyFilter.ALL, RenditionFilter.NONE);
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            assertTrue("Rendition filter works incorrect;", one.getRenditions().size() == 0);
         }
   }

   /**
    * 2.2.3.2.1
    * The number of levels of depth in the folder hierarchy from which to return results.
    * @throws Exception
    */
   @Test
   public void testGetDescendants_DepthLimit() throws Exception
   {
      if (!IS_CAPABILITY_DESCENDANTS)
      {
         //SKIP
         return;
      }
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, 2, true, IncludeRelationships.NONE, true, true,
               PropertyFilter.ALL, RenditionFilter.NONE);
         List<CmisObject> list = objectTreeToList(result);
        assertTrue("Unexpected items number;", list.size() == 8); //skipping last level with Doc4
   }

   /**
    * 2.2.3.2.3
    * The Repository MUST throw this exception if this property filter input parameter is not valid.
    * @throws Exception
    */
   @Test
   public void testGetDescendants_FilterNotValidException() throws Exception
   {
      if (!IS_CAPABILITY_DESCENDANTS)
      {
         //SKIP
         return;
      }
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, 2, true, IncludeRelationships.NONE, true, true, "(,*",
               RenditionFilter.NONE);
         fail("FilterNotValidException must be thrown;");
      }
      catch (FilterNotValidException ex)
      {
          
      }
   }

   /**
    * 2.2.3.2.3
    * The Repository MUST throw this exception if the service is invoked with “depth = 0”.
    * @throws Exception
    */
   @Test
   public void testGetDescendants_InvalidArgumentException() throws Exception
   {
      if (!IS_CAPABILITY_DESCENDANTS)
      {
         //SKIP
         return;
      }
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, 0, true, IncludeRelationships.NONE, true, true,
               PropertyFilter.ALL, RenditionFilter.NONE);
         fail("InvalidArgumentException must be thrown;");
      }
      catch (InvalidArgumentException ex)
      {
          
      }
   }

   /**
    * 2.2.3.2.3
    * The Repository MUST throw this exception if the specified folder is not a folder.
    * @throws Exception
    */
   @Test
   public void testGetDescendants_InvalidArgumentException2() throws Exception
   {
      if (!IS_CAPABILITY_DESCENDANTS)
      {
         //SKIP
         return;
      }
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(getStorage().getObjectByPath("/testroot/doc1").getObjectId(), 0, true,
               IncludeRelationships.NONE, true, true, PropertyFilter.ALL, RenditionFilter.ANY);
         fail("InvalidArgumentException must be thrown;");
      }
      catch (InvalidArgumentException ex)
      {
          
      }
   }

   /**
    * 2.2.3.3
    * Gets the set of descendant folder objects contained in the specified folder.
    * @throws Exception
    */
   @Test
   public void testGetFolderTree_Simple() throws Exception
   {
      if (!IS_CAPABILITY_FOLDER_TREE)
      {
         //SKIP
         return;
      }
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, true, IncludeRelationships.BOTH, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         assertTrue("Unexpected number of items;", objectTreeToList(result).size() == 3);
   }

   /**
    * 2.2.3.3.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set.  Defaults to FALSE
    * @throws Exception
    */
   @Test
   public void testGetFolderTree_AllowableActions() throws Exception
   {
      if (!IS_CAPABILITY_FOLDER_TREE)
      {
         //SKIP
         return;
      }
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, true, IncludeRelationships.BOTH, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            assertNotNull(one.getAllowableActions()); //allowable actions are present
         }
   }

   /**
    * 2.2.3.3.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set.  Defaults to FALSE
    * @throws Exception
    */
   @Test
   public void testGetFolderTree_NoAllowableActions() throws Exception
   {
      if (!IS_CAPABILITY_FOLDER_TREE)
      {
         //SKIP
         return;
      }
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, false, IncludeRelationships.BOTH, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            assertNull("Allowable actions must not be present in result", one.getAllowableActions()); //allowable actions not present
         }
   }

   /**
    * 2.2.3.3.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   @Test
   public void testGetFolderTree_Relationships() throws Exception
   {
      if (!IS_CAPABILITY_FOLDER_TREE)
      {
         //SKIP
         return;
      }
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, true, IncludeRelationships.SOURCE, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         List<CmisObject> list = objectTreeToList(result);
         int relCount = 0;
         for (CmisObject one : list)
         {
            if (one.getRelationship().size() > 0)
               relCount++;
         }
         assertTrue("Incorrect items number in result;", relCount == 1);
   }

   /**
    * 2.2.3.3.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   @Test
   public void testGetFolderTree_NoRelationships() throws Exception
   {
      if (!IS_CAPABILITY_FOLDER_TREE)
      {
         //SKIP
         return;
      }
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, true, IncludeRelationships.NONE, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         List<CmisObject> list = objectTreeToList(result);
         int relCount = 0;
         for (CmisObject one : list)
         {
            if (one.getRelationship().size() > 0)
               relCount++;
         }
         assertTrue("Incorrect items number in result;", relCount == 0);
   }

   /**
    * 2.2.3.3.1
    * If TRUE, returns a PathSegment for each child object for use in constructing that object’s path.
    * @throws Exception
    */
   @Test
   public void testGetFolderTree_PathSegment() throws Exception
   {
      if (!IS_CAPABILITY_FOLDER_TREE)
      {
         //SKIP
         return;
      }
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, true, IncludeRelationships.NONE, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            assertNotNull("Path segment must be present in result;", one.getPathSegment());
         }
   }

   @Test
   public void testGetFolderTree_ObjectInfo() throws Exception
   {
      if (!IS_CAPABILITY_FOLDER_TREE)
      {
         //SKIP
         return;
      }
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, true, IncludeRelationships.NONE, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            assertNotNull("Object info must be present in result;", one.getObjectInfo());
         }
   }

   /**
    * 2.2.3.3.1
    * Repositories SHOULD return only the properties specified in the property filter 
    * if they exist on the object’s type definition.
    * @throws Exception
    */
   @Test
   public void testGetFolderTree_PropertyFiltered() throws Exception
   {
      if (!IS_CAPABILITY_FOLDER_TREE)
      {
         //SKIP
         return;
      }
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, true, IncludeRelationships.NONE, true, true,
               "cmis:name,cmis:path", RenditionFilter.ANY);
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            for (Map.Entry<String, Property<?>> e : one.getProperties().entrySet())
            {
               if (e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path")) //Other props must be ignored
                  continue;
               else
                  fail("Property filter works incorrect;");
            }
         }
          
   }

   /**
    * 2.2.3.3.1
    * The Repository MUST return the set of renditions whose kind matches this filter.  
    * @throws Exception
    */
   @Test
   public void testGetFolderTree_RenditionsFiltered() throws Exception
   {
      if (!IS_CAPABILITY_FOLDER_TREE)
      {
         //SKIP
         return;
      }
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, true, IncludeRelationships.NONE, true, true,
               PropertyFilter.ALL, RenditionFilter.NONE);
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            assertTrue("Rendition filter works incorrect;", one.getRenditions().size() == 0);
         }
   }

   /**
    * 2.2.3.3.1
    * The number of levels of depth in the folder hierarchy from which to return results.
    * @throws Exception
    */
   @Test
   public void testGetFolderTree_DepthLimit() throws Exception
   {
      if (!IS_CAPABILITY_FOLDER_TREE)
      {
         //SKIP
         return;
      }
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, 1, true, IncludeRelationships.NONE, true, true, PropertyFilter.ALL,
               RenditionFilter.NONE);
         List<CmisObject> list = objectTreeToList(result);
         assertTrue("Incorrect items number in result;", list.size() == 2); //skipping last level with Doc4
   }

   /**
    * 2.2.3.3.3
    * The Repository MUST throw this exception if this property filter input parameter is not valid.
    * @throws Exception
    */
   @Test
   public void testGetFolderTree_FilterNotValidException() throws Exception
   {
      if (!IS_CAPABILITY_FOLDER_TREE)
      {
         //SKIP
         return;
      }
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, 2, true, IncludeRelationships.NONE, true, true, "(,*",
               RenditionFilter.NONE);
         fail("FilterNotValidException must be thrown;");
      }
      catch (FilterNotValidException ex)
      {
          
      }
   }

   /**
    * 2.2.3.3.3
    * The Repository MUST throw this exception if the service is invoked with an invalid depth.
    * @throws Exception
    */
   @Test
   public void testGetFolderTree_InvalidArgumentException() throws Exception
   {
      if (!IS_CAPABILITY_FOLDER_TREE)
      {
         //SKIP
         return;
      }
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, 0, true, IncludeRelationships.NONE, true, true, PropertyFilter.ALL,
               RenditionFilter.ANY);
         fail("InvalidArgumentException must be thrown;");
      }
      catch (InvalidArgumentException ex)
      {
          
      }
   }

   /**
    * 2.2.3.3.3
    * The Repository MUST throw this exception if the specified folder is not a folder.
    * @throws Exception
    */
   @Test
   public void testGetFolderTree_InvalidArgumentException2() throws Exception
   {
      if (!IS_CAPABILITY_FOLDER_TREE)
      {
         //SKIP
         return;
      }
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(getStorage().getObjectByPath("/testroot/doc1").getObjectId(), -1, true,
               IncludeRelationships.NONE, true, true, PropertyFilter.ALL, RenditionFilter.ANY);
         fail("InvalidArgumentException must be thrown;");
      }
      catch (InvalidArgumentException ex)
      {
          
      }
   }

   /**
   * 2.2.3.4
   * Gets the parent folder object for the specified folder object.  
   * @throws Exception
   */
   @Test
   public void testGetFolderParent_Simple() throws Exception
   {
         ObjectData fold = getStorage().getObjectByPath("/testroot/folder1");
         CmisObject result = getConnection().getFolderParent(fold.getObjectId(), true, PropertyFilter.ALL);
         assertNotNull("Result is empty", result);
   }

   @Test
   public void testGetFolderParent_IncludeObjectInfo() throws Exception
   {
         ObjectData fold = getStorage().getObjectByPath("/testroot/folder1");
         CmisObject result = getConnection().getFolderParent(fold.getObjectId(), true, PropertyFilter.ALL);
         assertNotNull("ObjectInfo must be present in result;", result.getObjectInfo());
   }

   @Test
   public void testGetFolderParent_NoIncludeObjectInfo() throws Exception
   {
         ObjectData fold = getStorage().getObjectByPath("/testroot/folder1");
         CmisObject result = getConnection().getFolderParent(fold.getObjectId(), false, PropertyFilter.ALL);
         assertNull("ObjectInfo must  not be present in result;", result.getObjectInfo());
   }

   /**
    * 2.2.3.4.1 
    * Repositories SHOULD return only the properties specified in the property filter 
    * if they exist on the object’s type definition.
    * @throws Exception
    */
   @Test
   public void testGetFolderParent_PropertyFiltered() throws Exception
   {
         ObjectData fold = getStorage().getObjectByPath("/testroot/folder1");
         CmisObject result = getConnection().getFolderParent(fold.getObjectId(), true, "cmis:name,cmis:path");
         for (Map.Entry<String, Property<?>> e : result.getProperties().entrySet())
         {
            if (e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path")) //Other props must be ignored
               continue;
            else
               fail("Property filter works incorrect");
         }
          
   }

   /**
    * 2.2.3.4.3
    * The Repository MUST throw this exception if this property filter input parameter is not valid.
    * @throws Exception
    */
   @Test
   public void testGetFolderParent_FilterNotValidException() throws Exception
   {
      try
      {
         ObjectData fold = getStorage().getObjectByPath("/testroot/folder1");
         CmisObject result = getConnection().getFolderParent(fold.getObjectId(), false, "(,*");
         fail("FilterNotValidException must be thrown;");
      }
      catch (FilterNotValidException ex)
      {
          
      }
   }

   /**
    * 2.2.3.4.3
    * The Repository MUST throw this exception if the folderId input is the root folder.
    * @throws Exception
    */
   @Test
   public void testGetFolderParent_InvalidArgumentException() throws Exception
   {
      try
      {
         ObjectData fold = getStorage().getObjectByPath("/");
         CmisObject result = getConnection().getFolderParent(fold.getObjectId(), true, PropertyFilter.ALL);
         fail("InvalidArgumentException must be thrown;");
      }
      catch (InvalidArgumentException ex)
      {
          
      }
      
   }

   /**
    * 2.2.3.5
    * Gets the parent folder(s) for the specified non-folder, fileable object..
    * @throws Exception
    */
   @Test
   public void testGetObjectParents_Simple() throws Exception
   {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), true, IncludeRelationships.BOTH, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         assertTrue("Incorrect items number in result;", result.size() == 1);
   }

   /**
    * 2.2.3.5.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   @Test
   public void testGetObjectParents_IncludeRelatioships() throws Exception
   {
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), true, IncludeRelationships.BOTH, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         for (ObjectParent one : result)
         {
            assertTrue("Incorrect items number in result;", one.getObject().getRelationship().size() > 0);
         }
   }

   /**
    * 2.2.3.5.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   @Test
   public void testGetObjectParents_NoRelationships() throws Exception
   {
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), true, IncludeRelationships.NONE, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         for (ObjectParent one : result)
         {
            assertTrue("Incorrect items number in result;", one.getObject().getRelationship().size() == 0);
         }
   }

   /**
    * 2.2.3.5.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set.  Defaults to FALSE.
    * @throws Exception
    */
   @Test
   public void testGetObjectParents_AllowableActions() throws Exception
   {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), true, IncludeRelationships.BOTH, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         for (ObjectParent one : result)
         {
            assertNotNull("AllowableActions must be present in result;", one.getObject().getAllowableActions());
         }
          
   }

   /**
    * 2.2.3.5.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set.  Defaults to FALSE.
    * @throws Exception
    */
   @Test
   public void testGetObjectParents_NoAllowableActions() throws Exception
   {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), false, IncludeRelationships.BOTH, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         for (ObjectParent one : result)
         {
            assertNull("AllowableActions must not be present in result;", one.getObject().getAllowableActions());
         }
   }

   /**
    * 2.2.3.5.1
    * Folder and object path segments are specified by pathSegment 
    * tokens which can be retrieved by all services that take an includePathSegments parameter. 
    * @throws Exception
    */
   @Test
   public void testGetObjectParents_IncludePathSegment() throws Exception
   {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), true, IncludeRelationships.BOTH, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         for (ObjectParent one : result)
         {
            assertNotNull("RelativePathSegment must be present in result;", one.getRelativePathSegment());
         }
   }

   /**
    * 2.2.3.5.1
    * Folder and object path segments are specified by pathSegment 
    * tokens which can be retrieved by all services that take an includePathSegments parameter. 
    * @throws Exception
    */
   @Test
   public void testGetObjectParents_NoPathSegment() throws Exception
   {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), false, IncludeRelationships.BOTH, false, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         for (ObjectParent one : result)
         {
            assertNull("RelativePathSegment must not be present in result;", one.getRelativePathSegment());
         }
   }

   /**
    * 2.2.3.5.1
    * The Repository MUST return the set of renditions whose kind matches this filter.  
    * @throws Exception
    */
   @Test
   public void testGetObjectParents_NoRenditions() throws Exception
   {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), true, IncludeRelationships.BOTH, true, true,
               PropertyFilter.ALL, RenditionFilter.NONE);
         for (ObjectParent one : result)
         {
            assertTrue("Renditions filter works incorrect;", one.getObject().getRenditions().size() == 0);
         }
   }

   @Test
   public void testGetObjectParents_ObjectInfo() throws Exception
   {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), true, IncludeRelationships.BOTH, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         for (ObjectParent one : result)
         {
            assertNotNull("ObjectInfo must be present in result;", one.getObject().getObjectInfo());
         }
   }

   @Test
   public void testGetObjectParents_NoObjectInfo() throws Exception
   {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), false, IncludeRelationships.BOTH, true, false,
               PropertyFilter.ALL, RenditionFilter.ANY);
         for (ObjectParent one : result)
         {
            assertNull("ObjectInfo must not be present in result;", one.getObject().getObjectInfo());
         }
   }

   /**
    * 2.2.3.5.1
    * Repositories SHOULD return only the properties specified in the property filter 
    * if they exist on the object’s type definition.
    * @throws Exception
    */
   @Test
   public void testGetObjectParents_PropertiesFIlter() throws Exception
   {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), false, IncludeRelationships.BOTH, true, false,
               "cmis:name,cmis:path", RenditionFilter.ANY);
         for (ObjectParent one : result)
         {
            for (Map.Entry<String, Property<?>> e : one.getObject().getProperties().entrySet())
            {
               if (e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path")) //Other props must be ignored
                  continue;
               else
                  fail("Property filter works incorrect;");
            }
         }
   }

   /**
    * 2.2.3.5.3
    * The Repository MUST throw this exception if this property filter input parameter is not valid. 
    * @throws Exception
    */
   @Test
   public void testGetObjectParents_FilterNotValidException() throws Exception
   {
      try
      {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), true, IncludeRelationships.BOTH, true, true, "(,*",
               RenditionFilter.ANY);
         fail("FilterNotValidException must be thrown;");
      }
      catch (FilterNotValidException ex)
      {
         //OK
      }
   }

   /**
    * 2.2.3.5.3
    * The Repository MUST throw this exception if this method is invoked on an object who 
    * Object-Type Definition specifies that it is not fileable.
    * @throws Exception
    */
   @Test
   public void testGetObjectParents_ConstraintException() throws Exception
   {
      try
      {
         ObjectData folder = getStorage().getObjectByPath("/testroot/folder2");
         ItemsIterator<RelationshipData> it =
            folder.getRelationships(RelationshipDirection.EITHER, relationshipTypeDefinition, true);
         if (it.hasNext())
         {
            List<ObjectParent> result =
               getConnection().getObjectParents(it.next().getObjectId(), true, IncludeRelationships.BOTH, true, true,
                  PropertyFilter.ALL, RenditionFilter.ANY);
            fail("ConstraintException must be thrown;");
         }

      }
      catch (ConstraintException ex)
      {
         //OK
      }
   }

   /**
    * 2.2.3.6
    * Gets the list of documents that are checked out that the user has access to.
    * @throws Exception
    */
   @Test
   public void testGetCheckedOutDocs_Simple() throws Exception
   {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(null, true, IncludeRelationships.BOTH, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", -1, 0);
         assertTrue("Unexpected items number;", result.getItems().size() == 3);
   }

   /**
    * 2.2.3.6.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set.  Defaults to FALSE.
    * @throws Exception
    */
   @Test
   public void testGetCheckedOutDocs_AllowableActions() throws Exception
   {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(null, true, IncludeRelationships.BOTH, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", -1, 0);
         for (CmisObject one : result.getItems())
         {
            assertNotNull("AllowableActions must be present in result;" ,one.getAllowableActions());
         }
   }

   /**
    * 2.2.3.6.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set. 
    * @throws Exception
    */
   @Test
   public void testGetCheckedOutDocs_NoAllowableActions() throws Exception
   {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(null, false, IncludeRelationships.BOTH, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", -1, 0);
         for (CmisObject one : result.getItems())
         {
            assertNull("AllowableActions must not be present in result;", one.getAllowableActions());
         }
   }

   /**
    * 2.2.3.6.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   @Test
   public void testGetCheckedOutDocs_Relationships() throws Exception
   {
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, false, IncludeRelationships.BOTH, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", -1, 0);
         boolean found = false;
         for (CmisObject one : result.getItems())
         {
            if (one.getRelationship().size() > 0)
               found = true;
         }
         assertTrue( "Relationship not found in result;", found);
   }

   /**
    * 2.2.3.6.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   @Test
   public void testGetCheckedOutDocs_NoRelationships() throws Exception
   {
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, false, IncludeRelationships.NONE, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", -1, 0);
         boolean found = false;
         for (CmisObject one : result.getItems())
         {
            if (one.getRelationship().size() > 0)
               found = true;
         }
         assertFalse("Relationship must not not found in result;", found);
   }

   @Test
   public void testGetCheckedOutDocs_ObjectInfo() throws Exception
   {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, false, IncludeRelationships.BOTH, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", -1, 0);
         for (CmisObject one : result.getItems())
         {
            assertNotNull("ObjectInfo must be present in result;", one.getObjectInfo());
         }
   }

   @Test
   public void testGetCheckedOutDocs_NoObjectInfo() throws Exception
   {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, false, IncludeRelationships.NONE, false, PropertyFilter.ALL,
               RenditionFilter.ANY, "", -1, 0);
         for (CmisObject one : result.getItems())
         {
            assertNull("ObjectInfo must not be present in result;", one.getObjectInfo());
         }
   }

   /**
    * 2.2.3.6.1 
    * The Repository MUST return the set of renditions whose kind matches this filter.  
    * @throws Exception
    */
   @Test
   public void testGetCheckedOutDocs_NoRenditions() throws Exception
   {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, false, IncludeRelationships.NONE, true, PropertyFilter.ALL,
               RenditionFilter.NONE, "", -1, 0);
         for (CmisObject one : result.getItems())
         {
            assertTrue("Rendition filter works incorrect;", one.getRenditions().size() == 0);
         }
   }

   /**
    * 2.2.3.6.1
    * Repositories SHOULD return only the properties specified in the property filter 
    * if they exist on the object’s type definition.
    * @throws Exception
    */
   @Test
   public void testGetCheckedOutDocs_PropertyFiltered() throws Exception
   {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, false, IncludeRelationships.NONE, true, "cmis:name,cmis:path",
               RenditionFilter.ANY, "", -1, 0);
         for (CmisObject one : result.getItems())
         {
            for (Map.Entry<String, Property<?>> e : one.getProperties().entrySet())
            {
               if (e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path")) //Other props must be ignored
                  continue;
               else
                  fail( "Property filter works incorrect;");
            }
         }
   }

   /**
    * 2.2.3.6.1
    * This is the maximum number of items to return in a response.  
    * The repository MUST NOT exceed this maximum.  
    * @throws Exception
    */
   @Test
   public void testGetCheckedOutDocs_MaxItems() throws Exception
   {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, true, IncludeRelationships.BOTH, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", 2, 0);
         assertTrue("Unexpected items number;", result.getItems().size() == 2);
   }

   /**
    * 2.2.3.6.1
    * This is the number of potential results that the repository 
    * MUST skip/page over before returning any results.  Defaults to 0.
    * @throws Exception
    */
   @Test
   public void testGetCheckedOutDocs_SkipCount() throws Exception
   {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, true, IncludeRelationships.BOTH, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", -1, 1);
         assertTrue("Unexpected items number;", result.getItems().size() == 2);
   }

   /**
    * 2.2.3.6.3
    * The Repository MUST throw this exception if this property filter input parameter is not valid.
    * @throws Exception
    */
   @Test
   public void testGetCheckedOutDocs_FilterNotValidException() throws Exception
   {
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, false, IncludeRelationships.NONE, true, ",*)",
               RenditionFilter.ANY, "", -1, 0);
        fail("FilterNotValidException must be thrown;");
      }
      catch (FilterNotValidException ex)
      {
         //OK
      }
   }

   @AfterClass
   public static void shutDown() throws Exception
   {
      clearTree(testroot);
      testroot = null;

      if (BaseTest.conn != null)
         BaseTest.conn.close();
   }
}
