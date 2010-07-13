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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.ItemsTree;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.ContentStreamAllowed;
import org.xcmis.spi.model.IncludeRelationships;
import org.xcmis.spi.model.ObjectParent;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.PropertyType;
import org.xcmis.spi.model.RelationshipDirection;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.Updatability;

public class NavigationTest extends BaseTest
{

   /**
    * getChildren() test suite;
    * 
    */

   /**
    * 2.2.3.2.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   public void testGetChildren_Relationships() throws Exception
   {
      System.out.print("Running testGetChildren_Relationships....                                  ");
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

   /**
    * 2.2.3.1.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   public void testGetChildren_NoRelationships() throws Exception
   {
      System.out.print("Running testGetChildren_NoRelationships....                                ");
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

   /**
    * 2.2.3.1.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set. 
    * @throws Exception
    */
   public void testGetChildren_AllowableActions() throws Exception
   {
      System.out.print("Running testGetChildren_AllowableActions....                               ");
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

   /**
    * 2.2.3.1.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set. 
    * @throws Exception
    */
   public void testGetChildren_NoAllowableActions() throws Exception
   {
      System.out.print("Running testGetChildren_NoAllowableActions....                             ");
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

   /**
    * 2.2.3.1.1
    * If TRUE, returns a PathSegment for each child object for use in constructing that object’s path.
    * @throws Exception
    */
   public void testGetChildren_PathSegments() throws Exception
   {
      System.out.print("Running testGetChildren_PathSegments....                                   ");
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

   /**
    * 2.2.3.1.1
    * If TRUE, returns a PathSegment for each child object for use in constructing that object’s path.
    * @throws Exception
    */
   public void testGetChildren_NoPathSegments() throws Exception
   {
      System.out.print("Running testGetChildren_NoPathSegments....                                 ");
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

   public void testGetChildren_ObjectInfo() throws Exception
   {
      System.out.print("Running testGetChildren_ObjectInfo....                                     ");
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

   public void testGetChildren_NoObjectInfo() throws Exception
   {
      System.out.print("Running testGetChildren_NoObjectInfo....                                   ");
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

   /**
    * 2.2.3.1.1
    * Repositories SHOULD return only the properties specified in the property filter 
    * if they exist on the object’s type definition.
    * @throws Exception
    */
   public void testGetChildren_PropertyFiltered() throws Exception
   {
      System.out.print("Running testGetChildren_PropertyFiltered....                               ");
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

   /**
    * 2.2.3.1.1
    * The Repository MUST return the set of renditions whose kind matches this filter.
    * @throws Exception
    */
   public void testGetChildren_RenditionFiltered() throws Exception
   {
      System.out.print("Running testGetChildren_RenditionFiltered....                              ");
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

   /**
    * 2.2.3.1.1
    * TRUE if the Repository contains additional items after those contained in the response.  FALSE otherwise.
    * @throws Exception
    */
   public void testGetChildren_HasMoreItems() throws Exception
   {
      System.out.print("Running testGetChildren_HasMoreItems....                                   ");
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

   /**
    * 2.2.3.1.1.
    * This is the maximum number of items to return in a response.  The repository MUST NOT exceed this maximum.
    * @throws Exception
    */
   public void testGetChildren_MaxItems() throws Exception
   {
      System.out.print("Running testGetChildren_MaxItems....                                       ");
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

   /**
    * 2.2.3.1.1.
    * If the repository knows the total number of items in a result set, the repository SHOULD include the number here. 
    * If the repository does not know the number of items in a result set, this parameter SHOULD not be set.
    * @throws Exception
    */
   public void testGetChildren_NumItems() throws Exception
   {
      System.out.print("Running testGetChildren_NumItems....                                       ");
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

   /**
    * 2.2.3.1.1.
    * This is the number of potential results that the repository MUST skip/page over before returning any results.
    * @throws Exception
    */
   public void testGetChildren_SkipCount() throws Exception
   {
      System.out.print("Running testGetChildren_SkipCount....                                      ");
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

   /**
    * 2.2.3.1.3
    * The Repository MUST throw this exception if the specified folder is not a folder.
    * @throws Exception
    */
   public void testGetChildren_InvalidArgumentException() throws Exception
   {
      System.out.print("Running testGetChildren_InvalidArgumentException....                       ");
      createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(getStorage().getObjectByPath("/testroot/doc1").getObjectId(), true,
               IncludeRelationships.BOTH, true, true, "", "*", "", 10, 10);
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
    * 2.2.3.1.3
    * The Repository MUST throw this exception if this property filter input parameter is not valid.
    * @throws Exception
    */
   public void testGetChildren_FilterNotValidException() throws Exception
   {
      System.out.print("Running testGetChildren_FilterNotValidException....                        ");
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

   /**
    * 2.2.3.2
    * Gets the set of descendant objects contained in the specified folder or any of its child-folders.
    * @throws Exception
    */
   public void testGetDescendants_Simple() throws Exception
   {
      System.out.print("Running testGetDescendants_Simple....                                      ");
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

   /**
    * 2.2.3.2.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set. 
    * @throws Exception
    */
   public void testGetDescendants_AllowableActions() throws Exception
   {
      System.out.print("Running testGetDescendants_AllowableActions....                            ");
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

   /**
    * 2.2.3.2.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set. 
    * @throws Exception
    */
   public void testGetDescendants_NoAllowableActions() throws Exception
   {
      System.out.print("Running testGetDescendants_NoAllowableActions....                          ");
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

   /**
    * 2.2.3.2.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   public void testGetDescendants_Relationships() throws Exception
   {
      System.out.print("Running testGetDescendants_Relationships....                               ");
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

   /**
    * 2.2.3.2.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   public void testGetDescendants_NoRelationships() throws Exception
   {
      System.out.print("Running testGetDescendants_NoRelationships....                             ");
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

   /**
    * 2.2.3.2.1
    * If TRUE, returns a PathSegment for each child object for use in constructing that object’s path.
    * @throws Exception
    */
   public void testGetDescendants_PathSegment() throws Exception
   {
      System.out.print("Running testGetDescendants_PathSegment....                                 ");
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

   /**
    * 2.2.3.2.1
    * If TRUE, returns a PathSegment for each child object for use in constructing that object’s path.
    * @throws Exception
    */
   public void testGetDescendants_NoPathSegment() throws Exception
   {
      System.out.print("Running testGetDescendants_NoPathSegment....                               ");
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

   public void testGetDescendants_ObjectInfo() throws Exception
   {
      System.out.print("Running testGetDescendants_ObjectInfo....                                  ");
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

   public void testGetDescendants_NoObjectInfo() throws Exception
   {
      System.out.print("Running testGetDescendants_NoObjectInfo....                                ");
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

   /**
    * 2.2.3.2.1
    * Repositories SHOULD return only the properties specified in the property filter 
    * if they exist on the object’s type definition.
    * @throws Exception
    */
   public void testGetDescendants_PropertyFiltered() throws Exception
   {
      System.out.print("Running testGetDescendants_PropertyFiltered....                            ");
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

   /**
    * 2.2.3.2.1
    * The Repository MUST return the set of renditions whose kind matches this filter. 
    * @throws Exception
    */
   public void testGetDescendants_RenditionsFiltered() throws Exception
   { 
      System.out.print("Running testGetDescendants_RenditionsFiltered....                          ");
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

   /**
    * 2.2.3.2.1
    * The number of levels of depth in the folder hierarchy from which to return results.
    * @throws Exception
    */
   public void testGetDescendants_DepthLimit() throws Exception
   {
      System.out.print("Running testGetDescendants_DepthLimit....                                            ");
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

   /**
    * 2.2.3.2.3
    * The Repository MUST throw this exception if this property filter input parameter is not valid.
    * @throws Exception
    */
   public void testGetDescendants_FilterNotValidException() throws Exception
   {
      System.out.print("Running testGetDescendants_FilterNotValidException....                     ");
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

   /**
    * 2.2.3.2.3
    * The Repository MUST throw this exception if the service is invoked with “depth = 0”.
    * @throws Exception
    */
   public void testGetDescendants_InvalidArgumentException() throws Exception
   {
      System.out.print("Running testGetDescendants_InvalidArgumentException....                    ");
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
    * 2.2.3.2.3
    * The Repository MUST throw this exception if the specified folder is not a folder.
    * @throws Exception
    */
   public void testGetDescendants_InvalidArgumentException2() throws Exception
   {
      System.out.print("Running testGetDescendants_InvalidArgumentException2....                   ");
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(getStorage().getObjectByPath("/testroot/doc1").getObjectId(), 0, true,
               IncludeRelationships.NONE, true, true, "", "*");
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

   /**
    * 2.2.3.3
    * Gets the set of descendant folder objects contained in the specified folder.
    * @throws Exception
    */
   public void testGetFolderTree_Simple() throws Exception
   {
      System.out.print("Running testGetFolderTree_Simple....                                       ");
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

   /**
    * 2.2.3.3.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set.  Defaults to FALSE
    * @throws Exception
    */
   public void testGetFolderTree_AllowableActions() throws Exception
   {
      System.out.print("Running testGetFolderTree_AllowableActions....                             ");
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

   /**
    * 2.2.3.3.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set.  Defaults to FALSE
    * @throws Exception
    */
   public void testGetFolderTree_NoAllowableActions() throws Exception
   {
      System.out.print("Running testGetFolderTree_NoAllowableActions....                           ");
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

   /**
    * 2.2.3.3.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   public void testGetFolderTree_Relationships() throws Exception
   {
      System.out.print("Running testGetFolderTree_Relationships....                                ");
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

   /**
    * 2.2.3.3.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   public void testGetFolderTree_NoRelationships() throws Exception
   {
      System.out.print("Running testGetFolderTree_NoRelationships....                              ");
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

   /**
    * 2.2.3.3.1
    * If TRUE, returns a PathSegment for each child object for use in constructing that object’s path.
    * @throws Exception
    */
   public void testGetFolderTree_PathSegment() throws Exception
   {
      System.out.print("Running testGetFolderTree_PathSegment....                                  ");
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

   /**
    * 2.2.3.3.1
    * If TRUE, returns a PathSegment for each child object for use in constructing that object’s path.
    * @throws Exception
    */
   public void testGetFolderTree_NoPathSegment() throws Exception
   {
      System.out.print("Running testGetFolderTree_NoPathSegment....                                ");
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

   public void testGetFolderTree_ObjectInfo() throws Exception
   {
      System.out.print("Running testGetFolderTree_ObjectInfo....                                   ");
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

   public void testGetFolderTree_NoObjectInfo() throws Exception
   {
      System.out.print("Running testGetFolderTree_NoObjectInfo....                                 ");
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

   /**
    * 2.2.3.3.1
    * Repositories SHOULD return only the properties specified in the property filter 
    * if they exist on the object’s type definition.
    * @throws Exception
    */
   public void testGetFolderTree_PropertyFiltered() throws Exception
   {
      System.out.print("Running testGetFolderTree_PropertyFiltered....                             ");
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

   /**
    * 2.2.3.3.1
    * The Repository MUST return the set of renditions whose kind matches this filter.  
    * @throws Exception
    */
   public void testGetFolderTree_RenditionsFiltered() throws Exception
   {
      System.out.print("Running testGetFolderTree_RenditionsFiltered....                           ");
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

   /**
    * 2.2.3.3.1
    * The number of levels of depth in the folder hierarchy from which to return results.
    * @throws Exception
    */
   public void testGetFolderTree_DepthLimit() throws Exception
   {
      System.out.print("Running testGetFolderTree_DepthLimit....                                   ");
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

   /**
    * 2.2.3.3.3
    * The Repository MUST throw this exception if this property filter input parameter is not valid.
    * @throws Exception
    */
   public void testGetFolderTree_FilterNotValidException() throws Exception
   {
      System.out.print("Running testGetFolderTree_FilterNotValidException....                      ");
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

   /**
    * 2.2.3.3.3
    * The Repository MUST throw this exception if the service is invoked with an invalid depth.
    * @throws Exception
    */
   public void testGetFolderTree_InvalidArgumentException() throws Exception
   {
      System.out.print("Running testGetFolderTree_InvalidArgumentException....                     ");
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
    * 2.2.3.3.3
    * The Repository MUST throw this exception if the specified folder is not a folder.
    * @throws Exception
    */
   public void testGetFolderTree_InvalidArgumentException2() throws Exception
   {
      System.out.print("Running testGetFolderTree_InvalidArgumentException2....                    ");
      createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(getStorage().getObjectByPath("/testroot/doc1").getObjectId(), -1, true,
               IncludeRelationships.NONE, true, true, "", "*");
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

   /**
    * 2.2.3.4
    * Gets the parent folder object for the specified folder object.  
    * @throws Exception
    */
   public void testGetFolderParent_Simple() throws Exception
   {
      System.out.print("Running testGetFolderParent_Simple....                                     ");
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

   public void testGetFolderParent_IncludeObjectInfo() throws Exception
   {
      System.out.print("Running testGetFolderParent_IncludeObjectInfo....                          ");
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

   public void testGetFolderParent_NoIncludeObjectInfo() throws Exception
   {
      System.out.print("Running testGetFolderParent_NoIncludeObjectInfo....                        ");
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

   /**
    * 2.2.3.4.1 
    * Repositories SHOULD return only the properties specified in the property filter 
    * if they exist on the object’s type definition.
    * @throws Exception
    */
   public void testGetFolderParent_PropertyFiltered() throws Exception
   {
      System.out.print("Running testGetFolderParent_PropertyFiltered....                           ");
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

   /**
    * 2.2.3.4.3
    * The Repository MUST throw this exception if this property filter input parameter is not valid.
    * @throws Exception
    */
   public void testGetFolderParent_FilterNotValidException() throws Exception
   {
      System.out.print("Running testGetFolderParent_FilterNotValidException....                    ");
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

   /**
    * 2.2.3.4.3
    * The Repository MUST throw this exception if the folderId input is the root folder.
    * @throws Exception
    */
   public void testGetFolderParent_InvalidArgumentException() throws Exception
   {
      System.out.print("Running testGetFolderParent_InvalidArgumentException....                   ");
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
    * getObjectParents() test suite;
    * 
    */

   /**
    * 2.2.3.5
    * Gets the parent folder(s) for the specified non-folder, fileable object..
    * @throws Exception
    */
   public void testGetObjectParents_Simple() throws Exception
   {
      System.out.print("Running testGetObjectParents_Simple....                                    ");
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

   /**
    * 2.2.3.5.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   public void testGetObjectParents_IncludeRelatioships() throws Exception
   {
      System.out.print("Running testGetObjectParents_IncludeRelatioships....                       ");
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

   /**
    * 2.2.3.5.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   public void testGetObjectParents_NoRelatioships() throws Exception
   {
      System.out.print("Running testGetObjectParents_NoRelatioships....                            ");
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

   /**
    * 2.2.3.5.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set.  Defaults to FALSE.
    * @throws Exception
    */
   public void testGetObjectParents_AllowableActions() throws Exception
   {
      System.out.print("Running testGetObjectParents_AllowableActions....                          ");
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

   /**
    * 2.2.3.5.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set.  Defaults to FALSE.
    * @throws Exception
    */
   public void testGetObjectParents_NoAllowableActions() throws Exception
   {
      System.out.print("Running testGetObjectParents_NoAllowableActions....                        ");
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

   /**
    * 2.2.3.5.1
    * Folder and object path segments are specified by pathSegment 
    * tokens which can be retrieved by all services that take an includePathSegments parameter. 
    * @throws Exception
    */
   public void testGetObjectParents_IncludePathSegment() throws Exception
   {
      System.out.print("Running testGetObjectParents_IncludePathSegment....                        ");
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

   /**
    * 2.2.3.5.1
    * Folder and object path segments are specified by pathSegment 
    * tokens which can be retrieved by all services that take an includePathSegments parameter. 
    * @throws Exception
    */
   public void testGetObjectParents_NoPathSegment() throws Exception
   {
      System.out.print("Running testGetObjectParents_NoPathSegment....                             ");
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

   /**
    * 2.2.3.5.1
    * The Repository MUST return the set of renditions whose kind matches this filter.  
    * @throws Exception
    */
   public void testGetObjectParents_NoRenditions() throws Exception
   {
      System.out.print("Running testGetObjectParents_NoRenditions...                               ");
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

   public void testGetObjectParents_ObjectInfo() throws Exception
   {
      System.out.print("Running testGetObjectParents_ObjectInfo....                                ");
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

   public void testGetObjectParents_NoObjectInfo() throws Exception
   {
      System.out.print("Running testGetObjectParents_NoObjectInfo()....                            ");
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

   /**
    * 2.2.3.5.1
    * Repositories SHOULD return only the properties specified in the property filter 
    * if they exist on the object’s type definition.
    * @throws Exception
    */
   public void testGetObjectParents_PropertiesFIlter() throws Exception
   {
      System.out.print("Running testGetObjectParents_PropertiesFIlter....                          ");
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

   /**
    * 2.2.3.5.3
    * The Repository MUST throw this exception if this property filter input parameter is not valid. 
    * @throws Exception
    */

   public void testGetObjectParents_FilterNotValidException() throws Exception
   {
      System.out.print("Running testGetObjectParents_FilterNotValidException....                   ");
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

   /**
    * 2.2.3.5.3
    * The Repository MUST throw this exception if this method is invoked on an object who 
    * Object-Type Definition specifies that it is not fileable.
    * @throws Exception
    */

   public void testGetObjectParents_ConstraintException() throws Exception
   {
      System.out.print("Running testGetObjectParents_ConstraintException....                       ");
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

   /**
    * 2.2.3.6
    * Gets the list of documents that are checked out that the user has access to.
    * @throws Exception
    */
   public void testGetCheckedOutDocs_Simple() throws Exception
   {
      System.out.print("Running testGetCheckedOutDocs_Simple....                                   ");
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

   /**
    * 2.2.3.6.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set.  Defaults to FALSE.
    * @throws Exception
    */
   public void testGetCheckedOutDocs_AllowableActions() throws Exception
   {
      System.out.print("Running testGetCheckedOutDocs_AllowableActions....                         ");
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

   /**
    * 2.2.3.6.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set. 
    * @throws Exception
    */
   public void testGetCheckedOutDocs_NoAllowableActions() throws Exception
   {
      System.out.print("Running testGetCheckedOutDocs_NoAllowableActions....                       ");
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

   /**
    * 2.2.3.6.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   public void testGetCheckedOutDocs_Relationships() throws Exception
   {
      System.out.print("Running testGetCheckedOutDocs_Relationships....                            ");
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

   /**
    * 2.2.3.6.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   public void testGetCheckedOutDocs_NoRelationships() throws Exception
   {
      System.out.print("Running testGetCheckedOutDocs_NoRelationships....                          ");
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

   public void testGetCheckedOutDocs_ObjectInfo() throws Exception
   {
      System.out.print("Running testGetCheckedOutDocs_ObjectInfo....                               ");
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

   public void testGetCheckedOutDocs_NoObjectInfo() throws Exception
   {
      System.out.print("Running testGetCheckedOutDocs_NoObjectInfo....                             ");
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

   /**
    * 2.2.3.6.1 
    * The Repository MUST return the set of renditions whose kind matches this filter.  
    * @throws Exception
    */
   public void testGetCheckedOutDocs_NoRenditions() throws Exception
   {
      System.out.print("Running testGetCheckedOutDocs_NoRenditions....                             ");
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

   /**
    * 2.2.3.6.1
    * Repositories SHOULD return only the properties specified in the property filter 
    * if they exist on the object’s type definition.
    * @throws Exception
    */
   public void testGetCheckedOutDocs_PropertyFiltered() throws Exception
   {
      System.out.print("Running testGetCheckedOutDocs_PropertyFiltered....                         ");
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

   /**
    * 2.2.3.6.1
    * This is the maximum number of items to return in a response.  
    * The repository MUST NOT exceed this maximum.  
    * @throws Exception
    */
   public void testGetCheckedOutDocs_MaxItems() throws Exception
   {
      System.out.print("Running testGetCheckedOutDocs_MaxItems....                                 ");
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

   /**
    * 2.2.3.6.1
    * This is the number of potential results that the repository 
    * MUST skip/page over before returning any results.  Defaults to 0.
    * @throws Exception
    */
   public void testGetCheckedOutDocs_SkipCount() throws Exception
   {
      System.out.print("Running testGetCheckedOutDocs_SkipCount....                                ");
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

   /**
    * 2.2.3.6.3
    * The Repository MUST throw this exception if this property filter input parameter is not valid.
    * @throws Exception
    */
   public void testGetCheckedOutDocs_FilterNotValidException() throws Exception
   {
      System.out.print("Running testGetCheckedOutDocs_FilterNotValidException....                  ");
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
      super.tearDown();
   }
}
