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
import org.xcmis.spi.PropertyFilter;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.RenditionFilter;
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

   String testroot;

   /**
    * 2.2.3.2.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   public void testGetChildren_Relationships() throws Exception
   {
      String testname = "testGetChildren_Relationships";
      System.out.print("Running " + testname + "....                                  ");
      this.testroot = createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", 10, 0);
         if (result.getItems().size() != 6)
            doFail(testname, "Unexpected items number;");
         int relCount = 0;
         for (CmisObject one : result.getItems())
         {
            if (one.getRelationship().size() > 0)
               relCount++;
         }
         if (relCount != 3)
            doFail(testname, "Unexpected items number;");//two relationships are present
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.1.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   public void testGetChildren_NoRelationships() throws Exception
   {
      String testname = "testGetChildren_NoRelationships";
      System.out.print("Running " + testname + "....                                ");
      this.testroot = createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.NONE, true, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", 10, 0);
         int relCount = 0;
         for (CmisObject one : result.getItems())
         {
            if (one.getRelationship().size() > 0)
               relCount++;
         }
         if (relCount == 0) //no relationships are present
            pass(testname);
         else
            doFail(testname, "Unexpected items number;");
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.1.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set. 
    * @throws Exception
    */
   public void testGetChildren_AllowableActions() throws Exception
   {
      String testname = "testGetChildren_AllowableActions";
      System.out.print("Running " + testname + "....                               ");
      this.testroot = createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            if (one.getAllowableActions() != null) //allowable actions are present
               continue;
            else
               doFail(testname, "Allowable actions must be present in result;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.1.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set. 
    * @throws Exception
    */
   public void testGetChildren_NoAllowableActions() throws Exception
   {
      String testname = "testGetChildren_NoAllowableActions";
      System.out.print("Running " + testname + "....                             ");
      this.testroot = createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, false, IncludeRelationships.BOTH, true, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            if (one.getAllowableActions() == null) //allowable actions are not present
               continue;
            else
               doFail(testname, "Allowable actions must not be present in result;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.1.1
    * If TRUE, returns a PathSegment for each child object for use in constructing that object’s path.
    * @throws Exception
    */
   public void testGetChildren_PathSegments() throws Exception
   {
      String testname = "testGetChildren_PathSegments";
      System.out.print("Running " + testname + "....                                   ");
      this.testroot = createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            if (one.getPathSegment() != null) //path segment is present
               continue;
            else
               doFail(testname, "Path segment must be present in result");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.1.1
    * If TRUE, returns a PathSegment for each child object for use in constructing that object’s path.
    * @throws Exception
    */
   public void testGetChildren_NoPathSegments() throws Exception
   {
      String testname = "testGetChildren_NoPathSegments";
      System.out.print("Running " + testname + "....                                 ");
      this.testroot = createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, false, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            if (one.getPathSegment() == null) //no path segments are present
               continue;
            else
               doFail(testname, "Path segment must not be present in result");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   public void testGetChildren_ObjectInfo() throws Exception
   {
      String testname = "testGetChildren_ObjectInfo";
      System.out.print("Running " + testname + "....                                     ");
      this.testroot = createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            if (one.getObjectInfo() != null) //obj info is present
               continue;
            else
               doFail(testname, "ObjectInfo must be present in result");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   public void testGetChildren_NoObjectInfo() throws Exception
   {
      String testname = "testGetChildren_NoObjectInfo";
      System.out.print("Running " + testname + "....                                   ");
      this.testroot = createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, false, PropertyFilter.ALL,
               RenditionFilter.ANY, "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            if (one.getObjectInfo() == null) // no obj info present
               continue;
            else
               doFail(testname, "ObjectInfo must not be present in result");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
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
      String testname = "testGetChildren_PropertyFiltered";
      System.out.print("Running " + testname + "....                               ");
      this.testroot = createFolderTree();
      try
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
                  doFail(testname, "Property filter works incorrect;");
            }
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.1.1
    * The Repository MUST return the set of renditions whose kind matches this filter.
    * @throws Exception
    */
   public void testGetChildren_RenditionFiltered() throws Exception
   {
      String testname = "testGetChildren_RenditionFiltered";
      System.out.print("Running " + testname + "....                              ");
      this.testroot = createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, PropertyFilter.ALL,
               RenditionFilter.NONE, "", 10, 0);
         for (CmisObject one : result.getItems())
         {
            for (Map.Entry<String, Property<?>> e : one.getProperties().entrySet())
            {
               if (one.getRenditions().size() != 0)
                  doFail(testname, "Rendition filter works incorrect;");
            }
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.1.1
    * TRUE if the Repository contains additional items after those contained in the response.  FALSE otherwise.
    * @throws Exception
    */
   public void testGetChildren_HasMoreItems() throws Exception
   {
      String testname = "testGetChildren_HasMoreItems";
      System.out.print("Running " + testname + "....                                   ");
      this.testroot = createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", 2, 0);
         if (!result.isHasMoreItems())
            doFail(testname, "Has more items property is incorrect;");
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.1.1.
    * This is the maximum number of items to return in a response.  The repository MUST NOT exceed this maximum.
    * @throws Exception
    */
   public void testGetChildren_MaxItems() throws Exception
   {
      String testname = "testGetChildren_MaxItems";
      System.out.print("Running " + testname + "....                                       ");
      this.testroot = createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", 3, 0);
         if (result.getItems().size() != 3)
           doFail(testname, "Items number is incorrect;");
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
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
      String testname = "testGetChildren_NumItems";
      System.out.print("Running " + testname + "....                                       ");
      this.testroot = createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", 2, 0);
         if (result.getNumItems() == -1 || result.getNumItems() == 4)
         {
            pass(testname);
         }
         else
         {
            doFail(testname, "NumItems test failed");
         }
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.1.1.
    * This is the number of potential results that the repository MUST skip/page over before returning any results.
    * @throws Exception
    */
   public void testGetChildren_SkipCount() throws Exception
   {
      String testname = "testGetChildren_SkipCount";
      System.out.print("Running " + testname + "....                                      ");
      this.testroot = createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", 10, 1);
         if (result.getItems().size() != 5)
            doFail(testname, "Items number is incorrect;");
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.1.3
    * The Repository MUST throw this exception if the specified folder is not a folder.
    * @throws Exception
    */
   public void testGetChildren_InvalidArgumentException() throws Exception
   {
      String testname = "testGetChildren_InvalidArgumentException";
      System.out.print("Running " + testname + "....                       ");
      this.testroot = createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(getStorage().getObjectByPath("/testroot/doc1").getObjectId(), true,
               IncludeRelationships.BOTH, true, true, PropertyFilter.ALL, RenditionFilter.ANY, "", 10, 10);
         doFail(testname, "InvalidArgumentException must be thrown;");
      }
      catch (InvalidArgumentException ex)
      {
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.1.3
    * The Repository MUST throw this exception if this property filter input parameter is not valid.
    * @throws Exception
    */
   public void testGetChildren_FilterNotValidException() throws Exception
   {
      String testname = "testGetChildren_FilterNotValidException";
      System.out.print("Running " + testname + "....                        ");
      this.testroot = createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getChildren(testroot, true, IncludeRelationships.BOTH, true, true, "(,*",
               RenditionFilter.NONE, "", 10, 0);
         doFail(testname, "FilterNotValidException must be thrown;");
      }
      catch (FilterNotValidException ex)
      {
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
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
      String testname = "testGetDescendants_Simple";
      System.out.print("Running " + testname + "....                                      ");
      this.testroot = createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.BOTH, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         if (objectTreeToList(result).size() != 9)
            doFail(testname, "Items number is incorrect;");
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.2.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set. 
    * @throws Exception
    */
   public void testGetDescendants_AllowableActions() throws Exception
   {
      String testname = "testGetDescendants_AllowableActions";
      System.out.print("Running " + testname + "....                            ");
      this.testroot = createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.BOTH, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            if (one.getAllowableActions() != null) //allowable actions are present
               continue;
            else
               doFail(testname, "Allowable actions must be present in result;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.2.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set. 
    * @throws Exception
    */
   public void testGetDescendants_NoAllowableActions() throws Exception
   {
      String testname = "testGetDescendants_NoAllowableActions";
      System.out.print("Running " + testname + "....                          ");
      this.testroot = createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, false, IncludeRelationships.BOTH, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            if (one.getAllowableActions() == null) //allowable actions are present
               continue;
            else
               doFail(testname, "Allowable actions must not be present in result;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.2.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   public void testGetDescendants_Relationships() throws Exception
   {
      String testname = "testGetDescendants_Relationships";
      System.out.print("Running " + testname + "....                               ");
      this.testroot = createFolderTree();
      try
      {
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
         if (relCount != 3)
            doFail(testname, "Items number is incorrect;");
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.2.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   public void testGetDescendants_NoRelationships() throws Exception
   {
      String testname = "testGetDescendants_NoRelationships";
      System.out.print("Running " + testname + "....                             ");
      this.testroot = createFolderTree();
      try
      {
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
         if (relCount != 0)
           doFail(testname, "Items number is incorrect;");
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.2.1
    * If TRUE, returns a PathSegment for each child object for use in constructing that object’s path.
    * @throws Exception
    */
   public void testGetDescendants_PathSegment() throws Exception
   {
      String testname = "testGetDescendants_PathSegment";
      System.out.print("Running " + testname + "....                                 ");
      this.testroot = createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.NONE, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            if (one.getPathSegment() != null)
               continue;
            else
               doFail(testname, "Path segment must be present in result;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   public void testGetDescendants_ObjectInfo() throws Exception
   {
      String testname = "testGetDescendants_ObjectInfo";
      System.out.print("Running " + testname + "....                                  ");
      this.testroot = createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.NONE, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            if (one.getObjectInfo() != null)
               continue;
            else
               doFail(testname, "Object info must be present in result;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   public void testGetDescendants_NoObjectInfo() throws Exception
   {
      String testname = "testGetDescendants_NoObjectInfo";
      System.out.print("Running " + testname + "....                                ");
      this.testroot = createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.NONE, true, false,
               PropertyFilter.ALL, RenditionFilter.ANY);
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            if (one.getObjectInfo() == null)
               continue;
            else
               doFail(testname, "Object info must not be present in result;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
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
      String testname = "testGetDescendants_PropertyFiltered";
      System.out.print("Running " + testname + "....                            ");
      this.testroot = createFolderTree();
      try
      {
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
                  doFail(testname, "Property filter works incorrect;");
            }
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.2.1
    * The Repository MUST return the set of renditions whose kind matches this filter. 
    * @throws Exception
    */
   public void testGetDescendants_RenditionsFiltered() throws Exception
   {
      String testname = "testGetDescendants_RenditionsFiltered";
      System.out.print("Running " + testname + "....                          ");
      this.testroot = createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, -1, true, IncludeRelationships.NONE, true, true,
               PropertyFilter.ALL, RenditionFilter.NONE);
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            if (one.getRenditions().size() != 0)
               doFail(testname, "Rendition filter works incorrect;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.2.1
    * The number of levels of depth in the folder hierarchy from which to return results.
    * @throws Exception
    */
   public void testGetDescendants_DepthLimit() throws Exception
   {
      String testname = "testGetDescendants_DepthLimit";
      System.out.print("Running " + testname + "....                                  ");
      this.testroot = createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, 2, true, IncludeRelationships.NONE, true, true,
               PropertyFilter.ALL, RenditionFilter.NONE);
         List<CmisObject> list = objectTreeToList(result);
         if (list.size() != 8) //skipping last level with Doc4
            doFail(testname, "Unexpected items number;");
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.2.3
    * The Repository MUST throw this exception if this property filter input parameter is not valid.
    * @throws Exception
    */
   public void testGetDescendants_FilterNotValidException() throws Exception
   {
      String testname = "testGetDescendants_FilterNotValidException";
      System.out.print("Running " + testname + "....                     ");
      this.testroot = createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, 2, true, IncludeRelationships.NONE, true, true, "(,*",
               RenditionFilter.NONE);
         doFail(testname, "FilterNotValidException must be thrown;");
      }
      catch (FilterNotValidException ex)
      {
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.2.3
    * The Repository MUST throw this exception if the service is invoked with “depth = 0”.
    * @throws Exception
    */
   public void testGetDescendants_InvalidArgumentException() throws Exception
   {
      String testname = "testGetDescendants_InvalidArgumentException";
      System.out.print("Running " + testname + "....                    ");
      this.testroot = createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(testroot, 0, true, IncludeRelationships.NONE, true, true,
               PropertyFilter.ALL, RenditionFilter.NONE);
         doFail(testname, "InvalidArgumentException must be thrown;");
      }
      catch (InvalidArgumentException ex)
      {
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.2.3
    * The Repository MUST throw this exception if the specified folder is not a folder.
    * @throws Exception
    */
   public void testGetDescendants_InvalidArgumentException2() throws Exception
   {
      String testname = "testGetDescendants_InvalidArgumentException2";
      System.out.print("Running " + testname + "....                   ");
      this.testroot = createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getDescendants(getStorage().getObjectByPath("/testroot/doc1").getObjectId(), 0, true,
               IncludeRelationships.NONE, true, true, PropertyFilter.ALL, RenditionFilter.ANY);
         doFail(testname, "InvalidArgumentException must be thrown;");
      }
      catch (InvalidArgumentException ex)
      {
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.3
    * Gets the set of descendant folder objects contained in the specified folder.
    * @throws Exception
    */
   public void testGetFolderTree_Simple() throws Exception
   {
      String testname = "testGetFolderTree_Simple";
      System.out.print("Running " + testname + "....                                       ");
      this.testroot = createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, true, IncludeRelationships.BOTH, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         if (objectTreeToList(result).size() != 3)
            doFail(testname, "Unexpected number of items;");
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.3.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set.  Defaults to FALSE
    * @throws Exception
    */
   public void testGetFolderTree_AllowableActions() throws Exception
   {
      String testname = "testGetFolderTree_AllowableActions";
      System.out.print("Running " + testname + "....                             ");
      this.testroot = createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, true, IncludeRelationships.BOTH, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            if (one.getAllowableActions() != null) //allowable actions are present
               continue;
            else
               doFail(testname, "Allowable actions must be present in result");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.3.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set.  Defaults to FALSE
    * @throws Exception
    */
   public void testGetFolderTree_NoAllowableActions() throws Exception
   {
      String testname = "testGetFolderTree_NoAllowableActions";
      System.out.print("Running " + testname + "....                           ");
      this.testroot = createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, false, IncludeRelationships.BOTH, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            if (one.getAllowableActions() == null) //allowable actions not present
               continue;
            else
               doFail(testname, "Allowable actions must not be present in result");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.3.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   public void testGetFolderTree_Relationships() throws Exception
   {
      String testname = "testGetFolderTree_Relationships";
      System.out.print("Running " + testname + "....                                ");
      this.testroot = createFolderTree();
      try
      {
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
         if (relCount != 1)
         {
            doFail(testname, "Incorrect items number in result;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.3.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   public void testGetFolderTree_NoRelationships() throws Exception
   {
      String testname = "testGetFolderTree_NoRelationships";
      System.out.print("Running " + testname + "....                              ");
      this.testroot = createFolderTree();
      try
      {
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
         if (relCount != 0)
         {
            doFail(testname, "Incorrect items number in result;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.3.1
    * If TRUE, returns a PathSegment for each child object for use in constructing that object’s path.
    * @throws Exception
    */
   public void testGetFolderTree_PathSegment() throws Exception
   {
      String testname = "testGetFolderTree_PathSegment";
      System.out.print("Running " + testname + "....                                  ");
      this.testroot = createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, true, IncludeRelationships.NONE, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            if (one.getPathSegment() != null)
               continue;
            else
               doFail(testname, "Path segment must be present in result;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   public void testGetFolderTree_ObjectInfo() throws Exception
   {
      String testname = "testGetFolderTree_ObjectInfo";
      System.out.print("Running " + testname + "....                                   ");
      this.testroot = createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, true, IncludeRelationships.NONE, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            if (one.getObjectInfo() != null)
               continue;
            else
               doFail(testname, "Object info must be present in result;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
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
      String testname = "testGetFolderTree_PropertyFiltered";
      System.out.print("Running " + testname + "....                             ");
      this.testroot = createFolderTree();
      try
      {
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
                  doFail(testname, "Property filter works incorrect;");
            }
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.3.1
    * The Repository MUST return the set of renditions whose kind matches this filter.  
    * @throws Exception
    */
   public void testGetFolderTree_RenditionsFiltered() throws Exception
   {
      String testname = "testGetFolderTree_RenditionsFiltered";
      System.out.print("Running " + testname + "....                           ");
      this.testroot = createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, -1, true, IncludeRelationships.NONE, true, true,
               PropertyFilter.ALL, RenditionFilter.NONE);
         List<CmisObject> list = objectTreeToList(result);
         for (CmisObject one : list)
         {
            if (one.getRenditions().size() == 0)
               continue;
            else
               doFail(testname, "Rendition filter works incorrect;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.3.1
    * The number of levels of depth in the folder hierarchy from which to return results.
    * @throws Exception
    */
   public void testGetFolderTree_DepthLimit() throws Exception
   {
      String testname = "testGetFolderTree_DepthLimit";
      System.out.print("Running " + testname + "....                                   ");
      this.testroot = createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, 1, true, IncludeRelationships.NONE, true, true, PropertyFilter.ALL,
               RenditionFilter.NONE);
         List<CmisObject> list = objectTreeToList(result);
         if (list.size() != 2) //skipping last level with Doc4
            doFail(testname, "Incorrect items number in result;");
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.3.3
    * The Repository MUST throw this exception if this property filter input parameter is not valid.
    * @throws Exception
    */
   public void testGetFolderTree_FilterNotValidException() throws Exception
   {
      String testname = "testGetFolderTree_FilterNotValidException";
      System.out.print("Running " + testname + "....                      ");
      this.testroot = createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, 2, true, IncludeRelationships.NONE, true, true, "(,*",
               RenditionFilter.NONE);
         doFail(testname, "FilterNotValidException must be thrown;");
      }
      catch (FilterNotValidException ex)
      {
         pass(testname);
      }
      catch (Exception other)
      {
         doFail(testname, other.getMessage());
      }
   }

   /**
    * 2.2.3.3.3
    * The Repository MUST throw this exception if the service is invoked with an invalid depth.
    * @throws Exception
    */
   public void testGetFolderTree_InvalidArgumentException() throws Exception
   {
      String testname = "testGetFolderTree_InvalidArgumentException";
      System.out.print("Running " + testname + "....                     ");
      this.testroot = createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(testroot, 0, true, IncludeRelationships.NONE, true, true, PropertyFilter.ALL,
               RenditionFilter.ANY);
         doFail(testname, "InvalidArgumentException must be thrown;");
      }
      catch (InvalidArgumentException ex)
      {
         pass(testname);
      }
      catch (Exception other)
      {
         doFail(testname, other.getMessage());
      }
   }

   /**
    * 2.2.3.3.3
    * The Repository MUST throw this exception if the specified folder is not a folder.
    * @throws Exception
    */
   public void testGetFolderTree_InvalidArgumentException2() throws Exception
   {
      String testname = "testGetFolderTree_InvalidArgumentException2";
      System.out.print("Running " + testname + "....                    ");
      this.testroot = createFolderTree();
      try
      {
         List<ItemsTree<CmisObject>> result =
            getConnection().getFolderTree(getStorage().getObjectByPath("/testroot/doc1").getObjectId(), -1, true,
               IncludeRelationships.NONE, true, true, PropertyFilter.ALL, RenditionFilter.ANY);
         doFail(testname, "InvalidArgumentException must be thrown;");
      }
      catch (InvalidArgumentException ex)
      {
         pass(testname);
      }
      catch (Exception other)
      {
         doFail(testname, other.getMessage());
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
      String testname = "testGetFolderParent_Simple";
      System.out.print("Running " + testname + "....                                     ");
      this.testroot = createFolderTree();
      try
      {
         ObjectData fold = getStorage().getObjectByPath("/testroot/folder1");
         CmisObject result = getConnection().getFolderParent(fold.getObjectId(), true, PropertyFilter.ALL);
         if (result == null)
            doFail(testname, "Result is empty");
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   public void testGetFolderParent_IncludeObjectInfo() throws Exception
   {
      String testname = "testGetFolderParent_IncludeObjectInfo";
      System.out.print("Running " + testname + "....                          ");
      this.testroot = createFolderTree();
      try
      {
         ObjectData fold = getStorage().getObjectByPath("/testroot/folder1");
         CmisObject result = getConnection().getFolderParent(fold.getObjectId(), true, PropertyFilter.ALL);
         if (result.getObjectInfo() == null)
            doFail(testname, "ObjectInfo must be present in result;");
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   public void testGetFolderParent_NoIncludeObjectInfo() throws Exception
   {
      String testname = "testGetFolderParent_NoIncludeObjectInfo";
      System.out.print("Running " + testname + "....                        ");
      this.testroot = createFolderTree();
      try
      {
         ObjectData fold = getStorage().getObjectByPath("/testroot/folder1");
         CmisObject result = getConnection().getFolderParent(fold.getObjectId(), false, PropertyFilter.ALL);
         if (result.getObjectInfo() != null)
            doFail(testname, "ObjectInfo must  not be present in result;");
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
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
      String testname = "testGetFolderParent_PropertyFiltered";
      System.out.print("Running " + testname + "....                           ");
      this.testroot = createFolderTree();
      try
      {
         ObjectData fold = getStorage().getObjectByPath("/testroot/folder1");
         CmisObject result = getConnection().getFolderParent(fold.getObjectId(), true, "cmis:name,cmis:path");
         for (Map.Entry<String, Property<?>> e : result.getProperties().entrySet())
         {
            if (e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path")) //Other props must be ignored
               continue;
            else
               doFail(testname, "Property filter works incorrect");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.4.3
    * The Repository MUST throw this exception if this property filter input parameter is not valid.
    * @throws Exception
    */
   public void testGetFolderParent_FilterNotValidException() throws Exception
   {
      String testname = "testGetFolderParent_FilterNotValidException";
      System.out.print("Running " + testname + "....                    ");
      this.testroot = createFolderTree();
      try
      {
         ObjectData fold = getStorage().getObjectByPath("/testroot/folder1");
         CmisObject result = getConnection().getFolderParent(fold.getObjectId(), false, "(,*");
         doFail(testname, "FilterNotValidException must be thrown;");
      }
      catch (FilterNotValidException ex)
      {
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.4.3
    * The Repository MUST throw this exception if the folderId input is the root folder.
    * @throws Exception
    */
   public void testGetFolderParent_InvalidArgumentException() throws Exception
   {
      String testname = "testGetFolderParent_InvalidArgumentException";
      System.out.print("Running " + testname + "....                   ");
      this.testroot = createFolderTree();
      try
      {
         ObjectData fold = getStorage().getObjectByPath("/");
         CmisObject result = getConnection().getFolderParent(fold.getObjectId(), true, PropertyFilter.ALL);
         doFail(testname, "InvalidArgumentException must be thrown;");
      }
      catch (InvalidArgumentException ex)
      {
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.5
    * Gets the parent folder(s) for the specified non-folder, fileable object..
    * @throws Exception
    */
   public void testGetObjectParents_Simple() throws Exception
   {
      String testname = "testGetObjectParents_Simple";
      System.out.print("Running " + testname + "....                                    ");
      this.testroot = createFolderTree();
      try
      {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), true, IncludeRelationships.BOTH, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         if (result.size() != 1)
            doFail(testname, "Incorrect items number in result;");
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.5.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   public void testGetObjectParents_IncludeRelatioships() throws Exception
   {
      String testname = "testGetObjectParents_IncludeRelatioships";
      System.out.print("Running " + testname + "....                       ");
      this.testroot = createFolderTree();
      try
      {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), true, IncludeRelationships.BOTH, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         for (ObjectParent one : result)
         {
            if (!(one.getObject().getRelationship().size() > 0))
               doFail(testname, "Incorrect items number in result;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.5.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   public void testGetObjectParents_NoRelationships() throws Exception
   {
      String testname = "testGetObjectParents_NoRelationships";
      System.out.print("Running " + testname + "....                            ");
      this.testroot = createFolderTree();
      try
      {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), true, IncludeRelationships.NONE, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         for (ObjectParent one : result)
         {
            if (one.getObject().getRelationship().size() != 0)
              doFail(testname, "Incorrect items number in result;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.5.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set.  Defaults to FALSE.
    * @throws Exception
    */
   public void testGetObjectParents_AllowableActions() throws Exception
   {
      String testname = "testGetObjectParents_AllowableActions";
      System.out.print("Running " + testname + "....                          ");
      this.testroot = createFolderTree();
      try
      {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), true, IncludeRelationships.BOTH, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         for (ObjectParent one : result)
         {
            if (one.getObject().getAllowableActions() != null)
               continue;
            else
               doFail(testname, "AllowableActions must be present in result;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.5.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set.  Defaults to FALSE.
    * @throws Exception
    */
   public void testGetObjectParents_NoAllowableActions() throws Exception
   {
      String testname = "testGetObjectParents_NoAllowableActions";
      System.out.print("Running " + testname + "....                        ");
      this.testroot = createFolderTree();
      try
      {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), false, IncludeRelationships.BOTH, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         for (ObjectParent one : result)
         {
            if (one.getObject().getAllowableActions() == null)
               continue;
            else
               doFail(testname, "AllowableActions must not be present in result;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
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
      String testname = "testGetObjectParents_IncludePathSegment";
      System.out.print("Running " + testname + "....                        ");
      this.testroot = createFolderTree();
      try
      {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), true, IncludeRelationships.BOTH, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         for (ObjectParent one : result)
         {
            if (one.getRelativePathSegment() != null)
               continue;
            else
               doFail(testname, "RelativePathSegment must be present in result;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
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
      String testname = "testGetObjectParents_NoPathSegment";
      System.out.print("Running " + testname + "....                             ");
      this.testroot = createFolderTree();
      try
      {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), false, IncludeRelationships.BOTH, false, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         for (ObjectParent one : result)
         {
            if (one.getRelativePathSegment() != null)
               doFail(testname, "RelativePathSegment must not be present in result;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.5.1
    * The Repository MUST return the set of renditions whose kind matches this filter.  
    * @throws Exception
    */
   public void testGetObjectParents_NoRenditions() throws Exception
   {
      String testname = "testGetObjectParents_NoRenditions";
      System.out.print("Running " + testname + "...                               ");
      this.testroot = createFolderTree();
      try
      {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), true, IncludeRelationships.BOTH, true, true,
               PropertyFilter.ALL, RenditionFilter.NONE);
         for (ObjectParent one : result)
         {
            if (one.getObject().getRenditions().size() != 0)
               doFail(testname, "Renditions filter works incorrect;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   public void testGetObjectParents_ObjectInfo() throws Exception
   {
      String testname = "testGetObjectParents_ObjectInfo";
      System.out.print("Running " + testname + "....                                ");
      this.testroot = createFolderTree();
      try
      {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), true, IncludeRelationships.BOTH, true, true,
               PropertyFilter.ALL, RenditionFilter.ANY);
         for (ObjectParent one : result)
         {
            if (one.getObject().getObjectInfo() == null)
               doFail(testname, "ObjectInfo must be present in result;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   public void testGetObjectParents_NoObjectInfo() throws Exception
   {
      String testname = "testGetObjectParents_NoObjectInfo";
      System.out.print("Running " + testname + "....                            ");
      this.testroot = createFolderTree();
      try
      {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), false, IncludeRelationships.BOTH, true, false,
               PropertyFilter.ALL, RenditionFilter.ANY);
         for (ObjectParent one : result)
         {
            if (one.getObject().getObjectInfo() != null)
               doFail(testname, "ObjectInfo must not be present in result;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
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
      String testname = "testGetObjectParents_PropertiesFIlter";
      System.out.print("Running " + testname + "....                          ");
      this.testroot = createFolderTree();
      try
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
                  doFail(testname, "Property filter works incorrect;");
            }
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.5.3
    * The Repository MUST throw this exception if this property filter input parameter is not valid. 
    * @throws Exception
    */

   public void testGetObjectParents_FilterNotValidException() throws Exception
   {
      String testname = "testGetObjectParents_FilterNotValidException";
      System.out.print("Running " + testname + "....                   ");
      this.testroot = createFolderTree();
      try
      {
         ObjectData doc = getStorage().getObjectByPath("/testroot/folder2/doc3");
         List<ObjectParent> result =
            getConnection().getObjectParents(doc.getObjectId(), true, IncludeRelationships.BOTH, true, true, "(,*",
               RenditionFilter.ANY);
         doFail(testname, "FilterNotValidException must be thrown;");
      }
      catch (FilterNotValidException ex)
      {
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
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
      String testname = "testGetObjectParents_ConstraintException";
      System.out.print("Running " + testname + "....                       ");
      this.testroot = createFolderTree();
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
            doFail(testname, "ConstraintException must be thrown;");
         }

      }
      catch (ConstraintException ex)
      {
         pass(testname);
      }
      catch (Exception other)
      {
         doFail(testname, other.getMessage());
      }
   }

   /**
    * 2.2.3.6
    * Gets the list of documents that are checked out that the user has access to.
    * @throws Exception
    */
   public void testGetCheckedOutDocs_Simple() throws Exception
   {
      String testname = "testGetCheckedOutDocs_Simple";
      System.out.print("Running " + testname + "....                                   ");
      this.testroot = createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(null, true, IncludeRelationships.BOTH, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", -1, 0);
         if (result.getItems().size() != 3)
            doFail(testname, "Unexpected items number;");
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.6.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set.  Defaults to FALSE.
    * @throws Exception
    */
   public void testGetCheckedOutDocs_AllowableActions() throws Exception
   {
      String testname = "testGetCheckedOutDocs_AllowableActions";
      System.out.print("Running " + testname + "....                         ");
      this.testroot = createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(null, true, IncludeRelationships.BOTH, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", -1, 0);
         for (CmisObject one : result.getItems())
         {
            if (one.getAllowableActions() == null)
               doFail(testname, "AllowableActions must be present in result;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.6.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set. 
    * @throws Exception
    */
   public void testGetCheckedOutDocs_NoAllowableActions() throws Exception
   {
      String testname = "testGetCheckedOutDocs_NoAllowableActions";
      System.out.print("Running " + testname + "....                       ");
      this.testroot = createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(null, false, IncludeRelationships.BOTH, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", -1, 0);
         for (CmisObject one : result.getItems())
         {
            if (one.getAllowableActions() != null)
               doFail(testname, "AllowableActions must not be present in result;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.6.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   public void testGetCheckedOutDocs_Relationships() throws Exception
   {
      String testname = "testGetCheckedOutDocs_Relationships";
      System.out.print("Running " + testname + "....                            ");
      this.testroot = createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, false, IncludeRelationships.BOTH, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", -1, 0);
         boolean found = false;
         for (CmisObject one : result.getItems())
         {
            if (one.getRelationship().size() > 0)
               found = true;
         }
         if (found)
            pass(testname);
         else
            doFail(testname, "Relationship not found in result;");
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.6.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   public void testGetCheckedOutDocs_NoRelationships() throws Exception
   {
      String testname = "testGetCheckedOutDocs_NoRelationships";
      System.out.print("Running " + testname + "....                          ");
      this.testroot = createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, false, IncludeRelationships.NONE, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", -1, 0);
         boolean found = false;
         for (CmisObject one : result.getItems())
         {
            if (one.getRelationship().size() > 0)
               found = true;
         }
         if (!found)
            pass(testname);
         else
            doFail(testname, "Relationship must not not found in result;");
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   public void testGetCheckedOutDocs_ObjectInfo() throws Exception
   {
      String testname = "testGetCheckedOutDocs_ObjectInfo";
      System.out.print("Running " + testname + "....                               ");
      this.testroot = createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, false, IncludeRelationships.BOTH, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", -1, 0);
         for (CmisObject one : result.getItems())
         {
            if (one.getObjectInfo() == null)
               doFail(testname, "ObjectInfo must be present in result;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   public void testGetCheckedOutDocs_NoObjectInfo() throws Exception
   {
      String testname = "testGetCheckedOutDocs_NoObjectInfo";
      System.out.print("Running " + testname + "....                             ");
      this.testroot = createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, false, IncludeRelationships.NONE, false, PropertyFilter.ALL,
               RenditionFilter.ANY, "", -1, 0);
         for (CmisObject one : result.getItems())
         {
            if (one.getObjectInfo() != null)
               doFail(testname, "ObjectInfo must not be present in result;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.6.1 
    * The Repository MUST return the set of renditions whose kind matches this filter.  
    * @throws Exception
    */
   public void testGetCheckedOutDocs_NoRenditions() throws Exception
   {
      String testname = "testGetCheckedOutDocs_NoRenditions";
      System.out.print("Running " + testname + "....                             ");
      this.testroot = createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, false, IncludeRelationships.NONE, true, PropertyFilter.ALL,
               RenditionFilter.NONE, "", -1, 0);
         for (CmisObject one : result.getItems())
         {
            if (one.getRenditions().size() != 0)
               doFail(testname, "rendition filter works incorrect;");
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
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
      String testname = "testGetCheckedOutDocs_PropertyFiltered";
      System.out.print("Running " + testname + "....                         ");
      this.testroot = createFolderTree();
      try
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
                  doFail(testname, "Property filter works incorrect;");
            }
         }
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
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
      String testname = "testGetCheckedOutDocs_MaxItems";
      System.out.print("Running " + testname + "....                                 ");
      this.testroot = createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, true, IncludeRelationships.BOTH, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", 2, 0);
         if (result.getItems().size() != 2)
            doFail(testname, "Items nimber incorrect in result;");
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
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
      String testname = "testGetCheckedOutDocs_SkipCount";
      System.out.print("Running " + testname + "....                                ");
      this.testroot = createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, true, IncludeRelationships.BOTH, true, PropertyFilter.ALL,
               RenditionFilter.ANY, "", -1, 1);
         if (result.getItems().size() == 2)
            pass(testname);
         else
            doFail(testname, "Unexpected items number;");
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   /**
    * 2.2.3.6.3
    * The Repository MUST throw this exception if this property filter input parameter is not valid.
    * @throws Exception
    */
   public void testGetCheckedOutDocs_FilterNotValidException() throws Exception
   {
      String testname = "testGetCheckedOutDocs_FilterNotValidException";
      System.out.print("Running " + testname + "....                  ");
      this.testroot = createFolderTree();
      try
      {
         ItemsList<CmisObject> result =
            getConnection().getCheckedOutDocs(testroot, false, IncludeRelationships.NONE, true, ",*)",
               RenditionFilter.ANY, "", -1, 0);
         doFail(testname, "FilterNotValidException must be thrown;");
      }
      catch (FilterNotValidException ex)
      {
         pass(testname);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
   }

   protected void pass(String method) throws Exception
   {
      super.pass("NavigationTest." + method);
   }
   
   protected void doFail(String method,  String message) throws Exception
   {
      super.doFail("NavigationTest." + method,  message);
   }

   @Override
   public void tearDown() throws Exception
   {
      clearTree(testroot);
      this.testroot = null;
      super.tearDown();
   }
}
