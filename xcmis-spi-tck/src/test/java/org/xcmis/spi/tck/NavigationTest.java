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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.ItemsTree;
import org.xcmis.spi.RenditionFilter;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.CapabilityRendition;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.IncludeRelationships;
import org.xcmis.spi.model.ObjectParent;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.TypeDefinition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NavigationTest extends BaseTest
{

   private static String testRootFolderId;

   private static String folder1;

   private static String folder2;

   private static String folder3;

   private static String doc1;

   private static String doc2;

   private static String doc3;

   private static String doc4;

   private static String doc5;

   private static String doc6;

   private static String pwc2;

   private static String pwc5;

   private static String pwc6;

   private static String rel1;

   private static String rel2;

   private static String rel3;

   @AfterClass
   public static void stop() throws Exception
   {
      if (testRootFolderId != null)
      {
         clear(testRootFolderId);
      }
   }

   /**
    * Create next structure:
    *
    * <pre>
    *   root
    *    - testroot
    *       |- folder1
    *       |- folder2
    *       |   |- folder3
    *       |   |   |- doc4
    *       |   |- doc3
    *       |- doc1
    *       |- doc2
    *       |- doc5
    *       |- doc6
    *  Rel1 = doc3, doc4
    *  Rel2 = doc1, doc2
    *  Rel3 = folder2, doc1
    * </pre>
    */
   @BeforeClass
   public static void start() throws Exception
   {
      TypeDefinition documentType = connection.getTypeDefinition(CmisConstants.DOCUMENT);
      TypeDefinition folderType = connection.getTypeDefinition(CmisConstants.FOLDER);

      testRootFolderId = createFolder(rootFolderID, folderType.getId(), "navigation_testroot", null, null, null);

      folder1 = createFolder(testRootFolderId, folderType.getId(), "folder1", null, null, null);

      doc1 = createDocument(testRootFolderId, documentType.getId(), "doc1", null, null, null, null, null);

      doc2 = createDocument(testRootFolderId, documentType.getId(), "doc2", null, null, null, null, null);

      folder2 = createFolder(testRootFolderId, folderType.getId(), "folder2", null, null, null);

      doc3 = createDocument(folder2, documentType.getId(), "doc3", null, null, null, null, null);

      folder3 = createFolder(folder2, folderType.getId(), "folder3", null, null, null);

      doc4 = createDocument(folder3, documentType.getId(), "doc4", null, null, null, null, null);
      doc5 = createDocument(testRootFolderId, documentType.getId(), "doc5", null, null, null, null, null);
      doc6 = createDocument(testRootFolderId, documentType.getId(), "doc6", null, null, null, null, null);

      if (documentType.isVersionable())
      {
         pwc2 = connection.checkout(doc2);
         pwc5 = connection.checkout(doc5);
         pwc6 = connection.checkout(doc6);
      }

      if (isRelationshipsSupported)
      {
         TypeDefinition relationshipType = connection.getTypeDefinition(CmisConstants.RELATIONSHIP);
         rel1 = createRelationship(relationshipType.getId(), "rel1", doc3, doc4, null, null, null);
         rel2 = createRelationship(relationshipType.getId(), "rel2", doc1, doc2, null, null, null);
         rel3 = createRelationship(relationshipType.getId(), "rel3", folder2, doc1, null, null, null);
      }
      System.out.println("Running Navigation Service tests");
   }

   /**
    * 2.2.3.6 getCheckedOutDocs.
    * <p>
    * Gets the list of documents that are checked out that the user has access
    * to. There is no any additional information such as relationships,
    * allowable actions, renditions.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetCheckedOutDocs() throws Exception
   {
      List<String> exp = new ArrayList<String>(3);
      for (String s : new String[]{pwc2, pwc5, pwc6})
      {
         if (s != null)
         {
            exp.add(s);
         }
      }
      ItemsList<CmisObject> pwcs =
         connection.getCheckedOutDocs(null, false, IncludeRelationships.NONE, true, null, RenditionFilter.NONE, null,
            -1, 0);
      List<String> res = new ArrayList<String>();
      for (CmisObject o : pwcs.getItems())
      {
         res.add(o.getObjectInfo().getId());
      }
      assertEquals(exp.size(), res.size());
      for (String s : res)
      {
         assertTrue("Unexpected item in result set " + s, exp.contains(s));
      }

      for (CmisObject o : pwcs.getItems())
      {
         // Be sure there is no any additional information since we did not request it.
         assertNull(o.getPathSegment());
         assertNull(o.getAllowableActions());
         assertEquals(0, o.getPolicyIds().size());
         assertEquals(0, o.getRelationship().size());
         assertEquals(0, o.getRenditions().size());
         res.add(o.getObjectInfo().getId());
      }
   }

   /**
    * 2.2.3.6 getCheckedOutDocs.
    * <p>
    * Gets the list of documents that are checked out that the user has access
    * to include additional information about allowable actions.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetCheckedOutDocs_AllowableActions() throws Exception
   {
      ItemsList<CmisObject> pwcs =
         connection.getCheckedOutDocs(null, true, IncludeRelationships.NONE, true, null, RenditionFilter.NONE, null,
            -1, 0);
      for (CmisObject o : pwcs.getItems())
      {
         assertNotNull("AllowableActions must be present in result.", o.getAllowableActions());
      }
   }

   /**
    * 2.2.3.6 getCheckedOutDocs.
    * <p>
    * {@link FilterNotValidException} must be throwing if the specified filter
    * is invalid (contain not allowed characters).
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetCheckedOutDocs_FilterNotValidException() throws Exception
   {
      try
      {
         String filter = "(,*";
         connection.getCheckedOutDocs(testRootFolderId, false, IncludeRelationships.NONE, true, filter,
            RenditionFilter.NONE, null, -1, 0);
         fail("Filter " + filter + " is not valid, FilterNotValidException must be thrown. ");
      }
      catch (FilterNotValidException ex)
      {
      }
   }

   /**
    * 2.2.3.6 getCheckedOutDocs.
    * <p>
    * See section 2.2.1.1 "Paging". If optional attribute 'maxItems' specified
    * then number of items contained in the response must not exceed specified
    * value.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetCheckedOutDocs_MaxItems() throws Exception
   {
      ItemsList<CmisObject> pwcs =
         connection.getCheckedOutDocs(testRootFolderId, false, IncludeRelationships.NONE, true, null,
            RenditionFilter.NONE, null, 2, 0);
      // Must not be more then 2 but may be empty e.g. if documents is not versionable.
      assertTrue("Unexpected items number. ", pwcs.getItems().size() <= 2);
   }

   /**
    * 2.2.3.6 getCheckedOutDocs.
    * <p>
    * Gets the list of documents that are checked out that the user has access
    * to and use filter for properties. Result SHOULD contains only the
    * properties specified in the property filter if they exist on the object's
    * type definition.
    *
    * @throws Exception
    */
   @Test
   public void testGetCheckedOutDocs_PropertyFiltered() throws Exception
   {
      String filter = "cmis:name,cmis:path";
      ItemsList<CmisObject> pwcs =
         connection.getCheckedOutDocs(testRootFolderId, false, IncludeRelationships.NONE, true, filter,
            RenditionFilter.NONE, null, -1, 0);
      Set<String> queryNames = new HashSet<String>();
      for (CmisObject o : pwcs.getItems())
      {
         for (Map.Entry<String, Property<?>> e : o.getProperties().entrySet())
         {
            queryNames.add(e.getValue().getQueryName());
         }
         // cmis:path is not expected since it is not determined for cmis:document type.
         assertEquals(1, queryNames.size());
         for (String q : queryNames)
         {
            assertTrue("Unexpected property " + q + " in result. ", q.equalsIgnoreCase("cmis:name"));
         }
      }
   }

   /**
    * 2.2.3.6 getCheckedOutDocs.
    * <p>
    * Gets the list of documents that are checked out that the user has access
    * to include additional information about relationships. If relationships is
    * not supported then this test will be skipped.
    * </p>
    *
    * @throws Exception
    */
   @Test
   @Ignore("Skip this test at he moment since it is not clear how-to determine" //
      + " which behavior storage supports." //
      + " See section 2.1.9.8 'Version Specific/Independent membership in Relationships'")
   public void testGetCheckedOutDocs_Relationships() throws Exception
   {
      if (!isRelationshipsSupported)
      {
         return;
      }
      ItemsList<CmisObject> pwcs =
         connection.getCheckedOutDocs(testRootFolderId, false, IncludeRelationships.BOTH, true, null,
            RenditionFilter.NONE, null, -1, 0);
      for (CmisObject o : pwcs.getItems())
      {
         if (o.getObjectInfo().getId().equals(pwc2))
         {
            // Relationship rel2 may be "inherited" from current version doc2 if repository
            // support version independent behavior. Section 2.1.9.8 in specification.
            assertTrue("Expected relationship " + rel2 + " not found. ", o.getRelationship().size() == 1
               && o.getRelationship().contains(rel2));
         }
         else
         {
            assertEquals(0, o.getRelationship().size());
         }
      }
   }

   /**
    * 2.2.3.6 getCheckedOutDocs.
    * <p>
    * Gets the list of documents that are checked out that the user has access
    * to and use rendition filter. Result MUST return the set of renditions
    * whose kind matches this filter.
    * <p>
    *
    * @throws Exception
    */
   @Test
   public void testGetCheckedOutDocs_RenditionsFiltered() throws Exception
   {
      ItemsList<CmisObject> pwcs =
         connection.getCheckedOutDocs(testRootFolderId, false, IncludeRelationships.NONE, true, null,
            RenditionFilter.NONE, null, -1, 0);
      // Since we can't be sure renditions available for all objects just check
      // we able to reject all renditions.
      for (CmisObject one : pwcs.getItems())
      {
         assertTrue("Rendition filter works incorrect. ", one.getRenditions().size() == 0);
      }
   }

   /**
    * 2.2.3.6 getCheckedOutDocs.
    * <p>
    * See section 2.2.1.1 "Paging". If optional attribute 'skipCount' is
    * specified then specified number of items must be skipped in result.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetCheckedOutDocs_SkipCount() throws Exception
   {
      List<String> exp = new ArrayList<String>(3);
      for (String s : new String[]{pwc2, pwc5, pwc6})
      {
         if (s != null)
         {
            exp.add(s);
         }
      }
      if (exp.isEmpty())
      {
         // No PWCs at all.
         return;
      }
      // Get all first.
      ItemsList<CmisObject> pwcs =
         connection.getCheckedOutDocs(testRootFolderId, true, IncludeRelationships.NONE, true, null,
            RenditionFilter.NONE, null, -1, 0);
      List<String> ids0 = new ArrayList<String>(pwcs.getItems().size());
      for (CmisObject o : pwcs.getItems())
      {
         ids0.add(o.getObjectInfo().getId());
      }

      // Get page.
      pwcs =
         connection.getCheckedOutDocs(testRootFolderId, true, IncludeRelationships.NONE, true, null,
            RenditionFilter.NONE, null, -1, 1);
      List<String> ids1 = new ArrayList<String>(pwcs.getItems().size());
      for (CmisObject o : pwcs.getItems())
      {
         ids1.add(o.getObjectInfo().getId());
      }
      // Remove first item
      ids0.remove(0);

      assertEquals(ids0, ids1);
   }

   /**
    * 2.2.3.1 getChildren.
    * <p>
    * Get children of folder with id <code>testRootFolderId</code> without any
    * additional information, such as allowable actions, relationships,
    * renditions.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetChildren() throws Exception
   {
      ItemsList<CmisObject> children =
         connection.getChildren(testRootFolderId, false, IncludeRelationships.NONE, false, true, null,
            RenditionFilter.NONE, null, -1, 0);
      assertEquals(6, children.getItems().size());
      Set<String> childrenIDs = new HashSet<String>(6);
      for (CmisObject child : children.getItems())
      {
         childrenIDs.add(child.getObjectInfo().getId());
      }
      assertTrue("Expected child " + folder1 + " not found. ", childrenIDs.contains(folder1));
      assertTrue("Expected child " + folder2 + " not found. ", childrenIDs.contains(folder2));
      assertTrue("Expected child " + doc1 + " not found. ", childrenIDs.contains(doc1));
      assertTrue("Expected child " + doc2 + " not found. ", childrenIDs.contains(doc2));
      assertTrue("Expected child " + doc5 + " not found. ", childrenIDs.contains(doc5));
      assertTrue("Expected child " + doc6 + " not found. ", childrenIDs.contains(doc6));

      // Be sure there is no any additional information since we did not request it.
      for (CmisObject o : children.getItems())
      {
         assertNull(o.getPathSegment());
         assertNull(o.getAllowableActions());
         assertEquals(0, o.getPolicyIds().size());
         assertEquals(0, o.getRelationship().size());
         assertEquals(0, o.getRenditions().size());
      }
   }

   /**
    * 2.2.3.1 getChildren.
    * <p>
    * Get children of folder with id <code>testRootFolderId</code> with
    * additional information about allowable actions.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetChildren_AllowableActions() throws Exception
   {
      ItemsList<CmisObject> children =
         connection.getChildren(testRootFolderId, true, IncludeRelationships.NONE, false, true, null,
            RenditionFilter.NONE, null, -1, 0);
      assertEquals(6, children.getItems().size());
      for (CmisObject child : children.getItems())
      {
         assertNotNull("Allowable actions must be present in result.", child.getAllowableActions());
      }
   }

   /**
    * 2.2.3.1 getChildren.
    * <p>
    * {@link FilterNotValidException} must be throwing if the specified filter
    * is invalid (contain not allowed characters).
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetChildren_FilterNotValidException() throws Exception
   {
      try
      {
         String filter = "(,*";
         connection.getChildren(testRootFolderId, false, IncludeRelationships.NONE, false, true, filter,
            RenditionFilter.NONE, null, -1, 0);
         fail("Filter " + filter + " is not valid, FilterNotValidException must be thrown. ");
      }
      catch (FilterNotValidException e)
      {
      }
   }

   /**
    * 2.2.3.1 getChildren.
    * <p>
    * See section 2.2.1.1 "Paging". If {@link ItemsList#isHasMoreItems()} must
    * return <code>true</code> if storage contains additional items after those
    * contained in the response.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetChildren_HasMoreItems() throws Exception
   {
      ItemsList<CmisObject> children =
         connection.getChildren(testRootFolderId, false, IncludeRelationships.NONE, false, true, null,
            RenditionFilter.NONE, null, 2, 0);
      // Since total number of items 6 and only 2 was requested ItemsList#isHasMoreItems() must return true.
      assertTrue(children.isHasMoreItems());
   }

   /**
    * 2.2.3.1 getChildren.
    * <p>
    * {@link InvalidArgumentException} must be throwing if the specified object
    * is not a folder.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetChildren_InvalidArgumentException() throws Exception
   {
      try
      {
         connection.getChildren(doc1, false, IncludeRelationships.NONE, false, true, null, RenditionFilter.NONE, null,
            -1, 0);
         fail("InvalidArgumentException must be thrown since try get children from not folder object. ");
      }
      catch (InvalidArgumentException e)
      {
      }
   }

   /**
    * 2.2.3.1 getChildren.
    * <p>
    * See section 2.2.1.1 "Paging". If optional attribute 'maxItems' specified
    * then number of items contained in the response must not exceed specified
    * value.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetChildren_MaxItems() throws Exception
   {
      ItemsList<CmisObject> children =
         connection.getChildren(testRootFolderId, false, IncludeRelationships.NONE, false, true, null,
            RenditionFilter.NONE, null, 3, 0);
      assertTrue("Wrong number of items in result. ", children.getItems().size() <= 3);
   }

   /**
    * 2.2.3.1 getChildren.
    * <p>
    * See section 2.2.1.1 "Paging". If the repository knows the total number of
    * items in a result set then method {@link ItemsList#getNumItems()} must
    * return total number of items. If total number of items is unknown then
    * {@link ItemsList#getNumItems()} must return -1.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetChildren_NumItems() throws Exception
   {
      ItemsList<CmisObject> children =
         connection.getChildren(testRootFolderId, false, IncludeRelationships.NONE, false, true, null,
            RenditionFilter.NONE, null, 2, 0);
      int numItems = children.getNumItems();
      assertTrue("NumItems must be 6 or -1. ", numItems == -1 || numItems == 6);
   }

   /**
    *
    * 2.2.3.1 getChildren.
    * <p>
    * Get children of folder with id <code>testRootFolderId</code> with
    * additional information about path segments. PathSegment must be include
    * for each child object for use in constructing that object's path.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetChildren_PathSegments() throws Exception
   {
      ItemsList<CmisObject> children =
         connection.getChildren(testRootFolderId, false, IncludeRelationships.BOTH, true, true, null,
            RenditionFilter.NONE, null, -1, 0);
      assertEquals(6, children.getItems().size());
      for (CmisObject child : children.getItems())
      {
         // Path segment value is repository specific. Don't check value here
         // just be sure path segment info is available.
         assertNotNull("Path segment must be present in result.", child.getPathSegment());
      }
   }

   /**
    * 2.2.3.1 getChildren.
    * <p>
    * Get children of folder with id <code>testRootFolderId</code> and use
    * filter for properties. Result SHOULD contains only the properties
    * specified in the property filter if they exist on the object's type
    * definition.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetChildren_PropertyFiltered() throws Exception
   {
      ItemsList<CmisObject> children =
         connection.getChildren(testRootFolderId, false, IncludeRelationships.NONE, false, true, "cmis:name,cmis:path",
            RenditionFilter.NONE, null, -1, 0);
      assertEquals(6, children.getItems().size());
      for (CmisObject child : children.getItems())
      {
         BaseType baseType = child.getObjectInfo().getBaseType();
         Set<String> queryNames = new HashSet<String>();
         for (Map.Entry<String, Property<?>> e : child.getProperties().entrySet())
         {
            queryNames.add(e.getValue().getQueryName());
         }
         if (baseType == BaseType.DOCUMENT)
         {
            // cmis:path is not in defined for cmis:document, so only cmis:name must be in result.
            assertEquals(1, queryNames.size());
            assertTrue("Unexpected property in result. ", queryNames.iterator().next().equalsIgnoreCase("cmis:name"));
         }
         else if (baseType == BaseType.FOLDER)
         {
            // cmis:path and cmis:name must be in result.
            assertEquals(2, queryNames.size());
            for (String q : queryNames)
            {
               assertTrue("Unexpected property " + q + " in result. ", q.equalsIgnoreCase("cmis:name")
                  || q.equalsIgnoreCase("cmis:path"));
            }
         }
      }
   }

   /**
    * 2.2.3.1 getChildren.
    * <p>
    * Get children of folder with id <code>testRootFolderId</code> with
    * additional information about relationships.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetChildren_Relationships() throws Exception
   {
      if (!isRelationshipsSupported)
      {
         return;
      }

      ItemsList<CmisObject> children =
         connection.getChildren(testRootFolderId, false, IncludeRelationships.BOTH, false, true, null,
            RenditionFilter.NONE, null, -1, 0);
      assertEquals(6, children.getItems().size());

      for (CmisObject child : children.getItems())
      {
         String childId = child.getObjectInfo().getId();
         List<CmisObject> relationships = child.getRelationship();
         Set<String> relationshipIDs = new HashSet<String>(relationships.size());
         for (CmisObject rel : relationships)
         {
            relationshipIDs.add(rel.getObjectInfo().getId());
         }
         if (childId.equals(doc1))
         {
            assertEquals(2, relationshipIDs.size());
            assertTrue("Expected relationship " + rel2 + " not found. ", relationshipIDs.contains(rel2));
            assertTrue("Expected relationship " + rel3 + " not found. ", relationshipIDs.contains(rel3));
         }
         else if (childId.equals(doc2))
         {
            assertEquals(1, relationshipIDs.size());
            assertTrue("Expected relationship " + rel2 + " not found. ", relationshipIDs.contains(rel2));
         }
         else if (childId.equals(folder2))
         {
            assertEquals(1, relationshipIDs.size());
            assertTrue("Expected relationship " + rel3 + " not found. ", relationshipIDs.contains(rel3));
         }
         else
         {
            // Other object has not relationships.
            assertEquals(0, relationshipIDs.size());
         }
      }
   }

   /**
    * 2.2.3.1 getChildren.
    * <p>
    * Get children of folder with id <code>testRootFolderId</code> and use
    * rendition filter. Result MUST return the set of renditions whose kind
    * matches this filter.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetChildren_RenditionFiltered() throws Exception
   {
      if (capabilities.getCapabilityRenditions() == CapabilityRendition.NONE)
      {
         return;
      }

      // Since we can't be sure renditions available for all objects just check
      // we able to reject all renditions.
      ItemsList<CmisObject> children =
         connection.getChildren(testRootFolderId, false, IncludeRelationships.NONE, false, true, null,
            RenditionFilter.NONE, null, -1, 0);
      for (CmisObject child : children.getItems())
      {
         assertTrue("Rendition filter works incorrect.", child.getRenditions().size() == 0);
      }
   }

   /**
    * 2.2.3.1 getChildren.
    * <p>
    * See section 2.2.1.1 "Paging". If optional attribute 'skipCount' is
    * specified then specified number of items must be skipped in result.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetChildren_SkipCount() throws Exception
   {
      ItemsList<CmisObject> children =
         connection.getChildren(testRootFolderId, false, IncludeRelationships.NONE, false, true, null,
            RenditionFilter.NONE, null, -1, 0);
      // Get all items first.
      List<String> childrenIDs = new ArrayList<String>(6);
      for (CmisObject child : children.getItems())
      {
         childrenIDs.add(child.getObjectInfo().getId());
      }
      assertEquals(6, childrenIDs.size());

      children =
         connection.getChildren(testRootFolderId, false, IncludeRelationships.NONE, false, true, null,
            RenditionFilter.NONE, null, -1, 3);
      List<String> childrenIDsPage = new ArrayList<String>(6);
      for (CmisObject child : children.getItems())
      {
         childrenIDsPage.add(child.getObjectInfo().getId());
      }
      assertEquals(3, childrenIDsPage.size());

      // Skip 3 items.
      Iterator<String> iterator0 = childrenIDs.iterator();
      int skip = 3;
      while (skip-- > 0)
      {
         iterator0.next();
         iterator0.remove();
      }
      assertEquals(childrenIDs, childrenIDsPage);
   }

   /**
    * 2.2.3.2 getDescendants.
    * <p>
    * Get descendant of folder with id <code>testRootFolderId</code> and any its
    * child folders without any additional information, such as allowable
    * actions, relationships, renditions.
    * <p>
    *
    * @throws Exception
    */
   @Test
   public void testGetDescendants() throws Exception
   {
      if (!capabilities.isCapabilityGetDescendants())
      {
         // Even descendants processing done for all storages respect to storage
         // capabilities since method
         // {@link Connection#getDescendants(String, int, boolean, IncludeRelationships, boolean, boolean, String, String)}
         // can be overridden and descendants feature disabled.
         return;
      }
      List<ItemsTree<CmisObject>> descendants =
         connection.getDescendants(testRootFolderId, -1, false, IncludeRelationships.NONE, false, true, null,
            RenditionFilter.NONE);

      Map<String, List<String>> exp = new HashMap<String, List<String>>();
      exp.put(doc1, null);
      exp.put(doc2, null);
      exp.put(doc3, null);
      exp.put(doc4, null);
      exp.put(doc5, null);
      exp.put(doc6, null);
      exp.put(folder1, new ArrayList<String>());
      exp.put(folder2, Arrays.asList(folder3, doc3));
      exp.put(folder3, Arrays.asList(doc4));

      validateTree(descendants, exp);

      // Be sure there is no any additional information since we did not request it.
      for (CmisObject o : objectTreeAsList(descendants))
      {
         assertNull(o.getPathSegment());
         assertNull(o.getAllowableActions());
         assertEquals(0, o.getPolicyIds().size());
         assertEquals(0, o.getRelationship().size());
         assertEquals(0, o.getRenditions().size());
      }
   }

   /**
    * 2.2.3.2 getDescendants.
    * <p>
    * Get descendant of folder with id <code>testRootFolderId</code> and any its
    * child folders with additional information about allowable actions.
    * <p>
    *
    * @throws Exception
    */
   @Test
   public void testGetDescendants_AllowableActions() throws Exception
   {
      if (!capabilities.isCapabilityGetDescendants())
      {
         return;
      }

      List<ItemsTree<CmisObject>> descendants =
         connection.getDescendants(testRootFolderId, -1, true, IncludeRelationships.NONE, false, true, null,
            RenditionFilter.NONE);
      // All items from tree in plain form.
      List<CmisObject> list = objectTreeAsList(descendants);
      assertEquals(9, list.size());
      for (CmisObject o : list)
      {
         assertNotNull("Allowable actions must be present in result.", o.getAllowableActions());
      }
   }

   /**
    * 2.2.3.2 getDescendants.
    * <p>
    * Use invalid depth argument 0. Expect to get
    * {@link InvalidArgumentException}.
    * <p>
    *
    * @throws Exception
    */
   @Test
   public void testGetDescendants_Depth_InvalidArgumentException() throws Exception
   {
      if (!capabilities.isCapabilityGetDescendants())
      {
         return;
      }

      try
      {
         connection.getDescendants(testRootFolderId, 0, false, IncludeRelationships.NONE, false, true, null,
            RenditionFilter.NONE);
         fail("InvalidArgumentException must be thrown, depth 0 is not allowed. ");
      }
      catch (InvalidArgumentException e)
      {
      }
   }

   /**
    * 2.2.3.2 getDescendants.
    * <p>
    * Get descendant of folder with id <code>testRootFolderId</code> and any its
    * child folders with respect to depth parameter.
    * <p>
    *
    * @throws Exception
    */
   @Test
   public void testGetDescendants_DepthLimit() throws Exception
   {
      if (!capabilities.isCapabilityGetDescendants())
      {
         return;
      }

      List<ItemsTree<CmisObject>> descendants =
         connection.getDescendants(testRootFolderId, 2, false, IncludeRelationships.NONE, false, true, null,
            RenditionFilter.NONE);

      Map<String, List<String>> exp = new HashMap<String, List<String>>();
      exp.put(doc1, null);
      exp.put(doc2, null);
      exp.put(doc3, null);
      exp.put(doc5, null);
      exp.put(doc6, null);
      exp.put(folder1, new ArrayList<String>());
      exp.put(folder2, Arrays.asList(folder3, doc3));
      exp.put(folder3, new ArrayList<String>());

      // doc4 must not be in tree.
      validateTree(descendants, exp);
   }

   /**
    * 2.2.3.2 getDescendants.
    * <p>
    * {@link FilterNotValidException} must be throwing if the specified filter
    * is invalid (contain not allowed characters).
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetDescendants_FilterNotValidException() throws Exception
   {
      if (!capabilities.isCapabilityGetDescendants())
      {
         return;
      }

      try
      {
         String filter = "(,*";
         connection.getDescendants(testRootFolderId, 2, false, IncludeRelationships.NONE, false, true, filter,
            RenditionFilter.NONE);
         fail("Filter " + filter + " is not valid, FilterNotValidException must be thrown. ");
      }
      catch (FilterNotValidException e)
      {
      }
   }

   /**
    * 2.2.3.2 getDescendants.
    * <p>
    * {@link InvalidArgumentException} must be throwing if the specified object
    * is not a folder.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetDescendants_InvalidArgumentException() throws Exception
   {
      if (!capabilities.isCapabilityGetDescendants())
      {
         return;
      }
      try
      {
         connection.getDescendants(doc1, -1, false, IncludeRelationships.NONE, false, true, null, RenditionFilter.NONE);
         fail("InvalidArgumentException must be thrown since try get descendants from not folder object. ");
      }
      catch (InvalidArgumentException ex)
      {
      }
   }

   /**
    * 2.2.3.2 getDescendants.
    * <p>
    * Get descendant of folder with id <code>testRootFolderId</code> and any its
    * child folders with additional information about path segments. PathSegment
    * must be include for each child object for use in constructing that
    * object's path.
    * <p>
    *
    * @throws Exception
    */
   @Test
   public void testGetDescendants_PathSegment() throws Exception
   {
      if (!capabilities.isCapabilityGetDescendants())
      {
         return;
      }

      List<ItemsTree<CmisObject>> descendants =
         connection.getDescendants(testRootFolderId, -1, false, IncludeRelationships.NONE, true, true, null,
            RenditionFilter.NONE);
      List<CmisObject> list = objectTreeAsList(descendants);
      assertEquals(9, list.size());
      for (CmisObject o : list)
      {
         // Path segment value is repository specific. Don't check value here
         // just be sure path segment info is available.
         assertNotNull("Path segment must be present in result.", o.getPathSegment());
      }
   }

   /**
    * 2.2.3.2 getDescendants.
    * <p>
    * Get descendant of folder with id <code>testRootFolderId</code> and any its
    * child folders and use filter for properties. Result SHOULD contains only
    * the properties specified in the property filter if they exist on the
    * object's type definition.
    * <p>
    *
    * @throws Exception
    */
   @Test
   public void testGetDescendants_PropertyFiltered() throws Exception
   {
      if (!capabilities.isCapabilityGetDescendants())
      {
         return;
      }

      List<ItemsTree<CmisObject>> descendants =
         connection.getDescendants(testRootFolderId, -1, false, IncludeRelationships.NONE, false, true,
            "cmis:name,cmis:path", RenditionFilter.NONE);
      List<CmisObject> list = objectTreeAsList(descendants);
      assertEquals(9, list.size());
      for (CmisObject o : list)
      {
         BaseType baseType = o.getObjectInfo().getBaseType();
         Set<String> queryNames = new HashSet<String>();
         for (Map.Entry<String, Property<?>> e : o.getProperties().entrySet())
         {
            queryNames.add(e.getValue().getQueryName());
         }
         if (baseType == BaseType.DOCUMENT)
         {
            // cmis:path is not in defined for cmis:document, so only cmis:name must be in result.
            assertEquals(1, queryNames.size());
            assertTrue("Unexpected property in result. ", queryNames.iterator().next().equalsIgnoreCase("cmis:name"));
         }
         else if (baseType == BaseType.FOLDER)
         {
            // cmis:path and cmis:name must be in result.
            assertEquals(2, queryNames.size());
            for (String q : queryNames)
            {
               assertTrue("Unexpected property " + q + " in result. ", q.equalsIgnoreCase("cmis:name")
                  || q.equalsIgnoreCase("cmis:path"));
            }
         }
      }
   }

   /**
    * 2.2.3.2 getDescendants.
    * <p>
    * Get descendant of folder with id <code>testRootFolderId</code> and any its
    * child folders with additional information about relationships. If
    * relationships is not supported then this test will be skipped.
    * <p>
    *
    * @throws Exception
    */
   @Test
   public void testGetDescendants_Relationships() throws Exception
   {
      if (!(capabilities.isCapabilityGetDescendants() && isRelationshipsSupported))
      {
         return;
      }

      List<ItemsTree<CmisObject>> descendants =
         connection.getDescendants(testRootFolderId, -1, false, IncludeRelationships.SOURCE, false, true, null,
            RenditionFilter.NONE);
      List<CmisObject> list = objectTreeAsList(descendants);
      assertEquals(9, list.size());

      // Check relationships. NOTE only sources must be present!
      for (CmisObject o : list)
      {
         String id = o.getObjectInfo().getId();
         List<CmisObject> relationships = o.getRelationship();
         Set<String> relationshipIDs = new HashSet<String>(relationships.size());
         for (CmisObject rel : relationships)
         {
            relationshipIDs.add(rel.getObjectInfo().getId());
         }
         if (id.equals(doc1))
         {
            // doc1 -> doc2
            assertEquals(1, relationshipIDs.size());
            assertTrue("Expected relationship " + rel2 + " not found. ", relationshipIDs.contains(rel2));
         }
         else if (id.equals(doc3))
         {
            // doc3 -> doc4
            assertEquals(1, relationshipIDs.size());
            assertTrue("Expected relationship " + rel1 + " not found. ", relationshipIDs.contains(rel1));
         }
         else if (id.equals(folder2))
         {
            // folder2 -> doc1
            assertEquals(1, relationshipIDs.size());
            assertTrue("Expected relationship " + rel3 + " not found. ", relationshipIDs.contains(rel3));
         }
         else
         {
            // Other object has not relationships.
            assertEquals(0, relationshipIDs.size());
         }
      }
   }

   /**
    * 2.2.3.2 getDescendants.
    * <p>
    * Get descendant of folder with id <code>testRootFolderId</code> and any its
    * child folders and use rendition filter. Result MUST return the set of
    * renditions whose kind matches this filter.
    * <p>
    *
    * @throws Exception
    */
   @Test
   public void testGetDescendants_RenditionsFiltered() throws Exception
   {
      if (!capabilities.isCapabilityGetDescendants()
         && capabilities.getCapabilityRenditions() == CapabilityRendition.NONE)
      {
         return;
      }

      // Since we can't be sure renditions available for all objects just check
      // we able to reject all renditions.
      List<ItemsTree<CmisObject>> descendants =
         connection.getDescendants(testRootFolderId, -1, false, IncludeRelationships.NONE, false, true, null,
            RenditionFilter.NONE);
      List<CmisObject> list = objectTreeAsList(descendants);
      assertEquals(9, list.size());
      for (CmisObject o : list)
      {
         assertTrue("Rendition filter works incorrect.", o.getRenditions().size() == 0);
      }
   }

   /**
    * 2.2.3.4 getFolderParent.
    * <p>
    * Gets the parent folder object for the specified folder object.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetFolderParent() throws Exception
   {
      CmisObject parent = connection.getFolderParent(folder1, true, null);
      assertNotNull("No parent s for folder. ", parent);
      assertEquals(testRootFolderId, parent.getObjectInfo().getId());
      // Be sure there is no any additional information since we did not request it.
      assertNull(parent.getPathSegment());
      assertNull(parent.getAllowableActions());
      assertEquals(0, parent.getPolicyIds().size());
      assertEquals(0, parent.getRelationship().size());
      assertEquals(0, parent.getRenditions().size());
   }

   /**
    * 2.2.3.4 getFolderParent.
    * <p>
    * {@link FilterNotValidException} must be throwing if the specified filter
    * is invalid (contain not allowed characters).
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetFolderParent_FilterNotValidException() throws Exception
   {
      try
      {
         String filter = "(,*";
         connection.getFolderParent(folder1, false, filter);
         fail("Filter " + filter + " is not valid, FilterNotValidException must be thrown. ");
      }
      catch (FilterNotValidException ex)
      {
      }
   }

   /**
    * 2.2.3.4 getFolderParent.
    * <p>
    * Gets the parent folder object for the specified folder object and use
    * filter for properties. Result SHOULD contains only the properties
    * specified in the property filter if they exist on the object's type
    * definition.
    *
    * @throws Exception
    */
   @Test
   public void testGetFolderParent_PropertyFiltered() throws Exception
   {
      CmisObject parent = connection.getFolderParent(folder1, true, "cmis:name,cmis:path");
      assertNotNull("No parent s for folder. ", parent);
      Set<String> queryNames = new HashSet<String>();
      for (Map.Entry<String, Property<?>> e : parent.getProperties().entrySet())
      {
         queryNames.add(e.getValue().getQueryName());
      }
      assertEquals(2, queryNames.size());
      for (String q : queryNames)
      {
         assertTrue("Unexpected property " + q + " in result. ", q.equalsIgnoreCase("cmis:name")
            || q.equalsIgnoreCase("cmis:path"));
      }
   }

   /**
    * 2.2.3.4 getFolderParent.
    * <p>
    * {@link InvalidArgumentException} must be thrown folderId input is the root
    * folder.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetFolderParent_RootFolder_InvalidArgumentException() throws Exception
   {
      try
      {
         connection.getFolderParent(rootFolderID, true, null);
         fail("InvalidArgumentException must be thrown, can't get parent of root folder. ");
      }
      catch (InvalidArgumentException ex)
      {
      }
   }

   /**
    * 2.2.3.3 getFolderTree.
    * <p>
    * Get descendant <b>folder objects</b> of folder with id
    * <code>testRootFolderId</code> and any its child folders without any
    * additional information, such as allowable actions, relationships,
    * renditions.
    * <p>
    *
    * @throws Exception
    */
   @Test
   public void testGetFolderTree() throws Exception
   {
      if (!capabilities.isCapabilityGetFolderTree())
      {
         // Even folder tree processing done for all storages respect to storage
         // capabilities since method
         // {@link Connection#getFolderTree(String, int, boolean, IncludeRelationships, boolean, boolean, String, String)}
         // can be overridden and folder tree feature disabled.
         return;
      }

      List<ItemsTree<CmisObject>> tree =
         connection.getFolderTree(testRootFolderId, -1, false, IncludeRelationships.NONE, false, true, null,
            RenditionFilter.NONE);

      Map<String, List<String>> exp = new HashMap<String, List<String>>();
      exp.put(folder1, new ArrayList<String>());
      exp.put(folder2, Arrays.asList(folder3));
      exp.put(folder3, new ArrayList<String>());

      validateTree(tree, exp);

      // Be sure there is no any additional information since we did not request it.
      for (CmisObject o : objectTreeAsList(tree))
      {
         assertNull(o.getPathSegment());
         assertNull(o.getAllowableActions());
         assertEquals(0, o.getPolicyIds().size());
         assertEquals(0, o.getRelationship().size());
         assertEquals(0, o.getRenditions().size());
      }
   }

   /**
    * 2.2.3.3 getFolderTree.
    * <p>
    * Get descendant <b>folder objects</b> of folder with id
    * <code>testRootFolderId</code> and any its child folders with additional
    * information about allowable actions.
    * <p>
    *
    * @throws Exception
    */
   @Test
   public void testGetFolderTree_AllowableActions() throws Exception
   {
      if (!capabilities.isCapabilityGetFolderTree())
      {
         return;
      }

      List<ItemsTree<CmisObject>> tree =
         connection.getFolderTree(testRootFolderId, -1, true, IncludeRelationships.NONE, false, true, null,
            RenditionFilter.NONE);
      // All items from tree in plain form.
      List<CmisObject> list = objectTreeAsList(tree);
      assertEquals(3, list.size());
      for (CmisObject o : list)
      {
         assertNotNull("Allowable actions must be present in result.", o.getAllowableActions());
      }
   }

   /**
    * 2.2.3.3 getFolderTree.
    * <p>
    * Use invalid depth argument 0. Expect to get
    * {@link InvalidArgumentException}.
    * <p>
    *
    * @throws Exception
    */
   @Test
   public void testGetFolderTree_Depth_InvalidArgumentException() throws Exception
   {
      if (!capabilities.isCapabilityGetFolderTree())
      {
         return;
      }

      try
      {
         connection.getFolderTree(testRootFolderId, 0, false, IncludeRelationships.NONE, false, true, null,
            RenditionFilter.NONE);
         fail("InvalidArgumentException must be thrown, depth 0 is not allowed. ");
      }
      catch (InvalidArgumentException e)
      {
      }
   }

   /**
    * 2.2.3.3 getFolderTree.
    * <p>
    * Get descendant <b>folder objects</b> of folder with id
    * <code>testRootFolderId</code> and any its child folders with respect to
    * depth parameter.
    * <p>
    *
    * @throws Exception
    */
   @Test
   public void testGetFolderTree_DepthLimit() throws Exception
   {
      if (!capabilities.isCapabilityGetFolderTree())
      {
         return;
      }

      List<ItemsTree<CmisObject>> tree =
         connection.getFolderTree(testRootFolderId, 1, false, IncludeRelationships.NONE, false, true, null,
            RenditionFilter.NONE);

      Map<String, List<String>> exp = new HashMap<String, List<String>>();
      exp.put(folder1, new ArrayList<String>());
      exp.put(folder2, new ArrayList<String>());

      // Only two folders from first level in tree.
      validateTree(tree, exp);
   }

   /**
    * 2.2.3.3 getFolderTree.
    * <p>
    * {@link FilterNotValidException} must be throwing if the specified filter
    * is invalid (contain not allowed characters).
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetFolderTree_FilterNotValidException() throws Exception
   {
      if (!capabilities.isCapabilityGetFolderTree())
      {
         return;
      }

      try
      {
         String filter = "(,*";
         connection.getFolderTree(testRootFolderId, 2, false, IncludeRelationships.NONE, false, true, filter,
            RenditionFilter.NONE);
         fail("Filter " + filter + " is not valid, FilterNotValidException must be thrown. ");
      }
      catch (FilterNotValidException e)
      {
      }
   }

   /**
    * 2.2.3.3 getFolderTree.
    * <p>
    * {@link InvalidArgumentException} must be throwing if the specified object
    * is not a folder.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetFolderTree_InvalidArgumentException() throws Exception
   {
      if (!capabilities.isCapabilityGetFolderTree())
      {
         return;
      }
      try
      {
         connection.getDescendants(doc1, -1, false, IncludeRelationships.NONE, false, true, null, RenditionFilter.NONE);
         fail("InvalidArgumentException must be thrown since try get descendants folders from not folder object. ");
      }
      catch (InvalidArgumentException ex)
      {
      }
   }

   /**
    * 2.2.3.3 getFolderTree.
    * <p>
    * Get descendant <b>folder objects</b> of folder with id
    * <code>testRootFolderId</code> and any its child folders with additional
    * information about path segments. PathSegment must be include for each
    * child object for use in constructing that object's path.
    * <p>
    *
    * @throws Exception
    */
   @Test
   public void testGetFolderTree_PathSegment() throws Exception
   {
      if (!capabilities.isCapabilityGetFolderTree())
      {
         return;
      }

      List<ItemsTree<CmisObject>> tree =
         connection.getFolderTree(testRootFolderId, -1, false, IncludeRelationships.NONE, true, true, null,
            RenditionFilter.NONE);
      List<CmisObject> list = objectTreeAsList(tree);
      assertEquals(3, list.size());
      for (CmisObject o : list)
      {
         // Path segment value is repository specific. Don't check value here
         // just be sure path segment info is available.
         assertNotNull("Path segment must be present in result.", o.getPathSegment());
      }
   }

   /**
    * 2.2.3.3 getFolderTree.
    * <p>
    * Get descendant <b>folder objects</b> of folder with id
    * <code>testRootFolderId</code> and any its child folders and use filter for
    * properties. Result SHOULD contains only the properties specified in the
    * property filter if they exist on the object's type definition.
    * <p>
    *
    * @throws Exception
    */
   @Test
   public void testGetFolderTree_PropertyFiltered() throws Exception
   {
      if (!capabilities.isCapabilityGetFolderTree())
      {
         return;
      }

      List<ItemsTree<CmisObject>> tree =
         connection.getFolderTree(testRootFolderId, -1, false, IncludeRelationships.NONE, false, true,
            "cmis:name,cmis:path", RenditionFilter.NONE);
      List<CmisObject> list = objectTreeAsList(tree);
      assertEquals(3, list.size());
      for (CmisObject o : list)
      {
         Set<String> queryNames = new HashSet<String>();
         for (Map.Entry<String, Property<?>> e : o.getProperties().entrySet())
         {
            queryNames.add(e.getValue().getQueryName());
         }
         // cmis:path and cmis:name must be in result.
         assertEquals(2, queryNames.size());
         for (String q : queryNames)
         {
            assertTrue("Unexpected property " + q + " in result. ", q.equalsIgnoreCase("cmis:name")
               || q.equalsIgnoreCase("cmis:path"));
         }
      }
   }

   /**
    * 2.2.3.3 getFolderTree.
    * <p>
    * Get descendant <b>folder objects</b> of folder with id
    * <code>testRootFolderId</code> and any its child folders with additional
    * information about relationships. If relationships is not supported then
    * this test will be skipped.
    * <p>
    *
    * @throws Exception
    */
   @Test
   public void testGetFolderTree_Relationships() throws Exception
   {
      if (!(capabilities.isCapabilityGetFolderTree() && isRelationshipsSupported))
      {
         return;
      }

      List<ItemsTree<CmisObject>> tree =
         connection.getFolderTree(testRootFolderId, -1, false, IncludeRelationships.SOURCE, false, true, null,
            RenditionFilter.NONE);
      List<CmisObject> list = objectTreeAsList(tree);
      assertEquals(3, list.size());

      // Check relationships. NOTE only sources must be present!
      for (CmisObject o : list)
      {
         String id = o.getObjectInfo().getId();
         List<CmisObject> relationships = o.getRelationship();
         Set<String> relationshipIDs = new HashSet<String>(relationships.size());
         for (CmisObject rel : relationships)
         {
            relationshipIDs.add(rel.getObjectInfo().getId());
         }
         if (id.equals(folder2))
         {
            // folder2 -> doc1
            assertEquals(1, relationshipIDs.size());
            assertTrue("Expected relationship " + rel3 + " not found. ", relationshipIDs.contains(rel3));
         }
         else
         {
            // Other object has not relationships.
            assertEquals(0, relationshipIDs.size());
         }
      }
   }

   /**
    * 2.2.3.3 getFolderTree.
    * <p>
    * Get descendant <b>folder objects</b> of folder with id
    * <code>testRootFolderId</code> and any its child folders and use rendition
    * filter. Result MUST return the set of renditions whose kind matches this
    * filter.
    * <p>
    *
    * @throws Exception
    */
   @Test
   public void testGetFolderTree_RenditionsFiltered() throws Exception
   {
      if (!capabilities.isCapabilityGetFolderTree()
         && capabilities.getCapabilityRenditions() == CapabilityRendition.NONE)
      {
         return;
      }

      // Since we can't be sure renditions available for all objects just check
      // we able to reject all renditions.
      List<ItemsTree<CmisObject>> tree =
         connection.getFolderTree(testRootFolderId, -1, false, IncludeRelationships.NONE, false, true, null,
            RenditionFilter.NONE);
      List<CmisObject> list = objectTreeAsList(tree);
      assertEquals(3, list.size());
      for (CmisObject o : list)
      {
         assertTrue("Rendition filter works incorrect.", o.getRenditions().size() == 0);
      }
   }

   /**
    * 2.2.3.5 getObjectParents.
    * <p>
    * Gets the parent folder(s) for the specified non-folder, fileable object.
    * There no any additional information in result such as allowable action,
    * relationships, path segments, renditions.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetObjectParents() throws Exception
   {
      List<ObjectParent> parents =
         connection.getObjectParents(doc1, false, IncludeRelationships.NONE, false, true, null, RenditionFilter.NONE);
      assertEquals(1, parents.size());
      assertNotNull(parents.get(0));
      assertEquals(testRootFolderId, parents.get(0).getObject().getObjectInfo().getId());
      // Be sure there is no any additional information since we did not request it.
      CmisObject o = parents.get(0).getObject();
      assertNull(o.getPathSegment());
      assertNull(o.getAllowableActions());
      assertEquals(0, o.getPolicyIds().size());
      assertEquals(0, o.getRelationship().size());
      assertEquals(0, o.getRenditions().size());
   }

   /**
    * 2.2.3.5 getObjectParents.
    * <p>
    * Gets the parent folder(s) for the specified non-folder, fileable object
    * include additional information about allowable actions.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetObjectParents_AllowableActions() throws Exception
   {
      List<ObjectParent> parents =
         connection.getObjectParents(doc1, true, IncludeRelationships.NONE, false, true, null, RenditionFilter.NONE);
      assertEquals(1, parents.size());
      assertNotNull(parents.get(0));
      CmisObject parent = parents.get(0).getObject();
      assertNotNull("AllowableActions must be present in result.", parent.getAllowableActions());
   }

   /**
    * 2.2.3.5 getObjectParents.
    * <p>
    * {@link FilterNotValidException} must be throwing if the specified filter
    * is invalid (contain not allowed characters).
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetObjectParents_FilterNotValidException() throws Exception
   {
      try
      {
         String filter = "(,*";
         connection.getObjectParents(doc1, false, IncludeRelationships.NONE, false, true, filter, RenditionFilter.NONE);
         fail("Filter " + filter + " is not valid, FilterNotValidException must be thrown. ");
      }
      catch (FilterNotValidException e)
      {
      }
   }

   /**
    * 2.2.3.5 getObjectParents.
    * <p>
    * Get the parent folder(s) for the specified non-folder, fileable object
    * include additional information about path segments.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetObjectParents_IncludePathSegment() throws Exception
   {
      List<ObjectParent> parents =
         connection.getObjectParents(doc3, false, IncludeRelationships.NONE, true, true, null, RenditionFilter.NONE);
      assertEquals(1, parents.size());
      assertNotNull(parents.get(0));
      ObjectParent parent = parents.get(0);
      assertNotNull("RelativePathSegment must be present in result.", parent.getRelativePathSegment());
   }

   /**
    * 2.2.3.5 getObjectParents.
    * <p>
    * {@link ConstraintException} must be thrown if specified object is not
    * fileable.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetObjectParents_NotFileable_ConstraintException() throws Exception
   {
      try
      {
         if (rel1 != null)
         {
            connection
               .getObjectParents(rel1, false, IncludeRelationships.NONE, false, true, null, RenditionFilter.NONE);
            fail("ConstraintException must be thrown since object " + rel1 + " (relationship) is not fileable.");
         }
      }
      catch (ConstraintException e)
      {
      }
   }

   /**
    * 2.2.3.5 getObjectParents.
    * <p>
    * Get the parent folder(s) for the specified non-folder, fileable object and
    * use filter for properties. Result SHOULD contains only the properties
    * specified in the property filter if they exist on the object's type
    * definition.
    *
    * @throws Exception
    */
   @Test
   public void testGetObjectParents_PropertiesFIlter() throws Exception
   {
      String filter = "cmis:name,cmis:path";
      List<ObjectParent> parents =
         connection.getObjectParents(doc3, false, IncludeRelationships.NONE, false, true, filter, RenditionFilter.NONE);
      assertEquals(1, parents.size());
      assertNotNull(parents.get(0));
      Set<String> queryNames = new HashSet<String>();
      for (Map.Entry<String, Property<?>> e : parents.get(0).getObject().getProperties().entrySet())
      {
         queryNames.add(e.getValue().getQueryName());
      }
      assertEquals(2, queryNames.size());
      for (String q : queryNames)
      {
         assertTrue("Unexpected property " + q + " in result. ", q.equalsIgnoreCase("cmis:name")
            || q.equalsIgnoreCase("cmis:path"));
      }
   }

   /**
    * 2.2.3.5 getObjectParents.
    * <p>
    * Gets the parent folder(s) for the specified non-folder, fileable object
    * include additional information about relationships. If relationships is
    * not supported then this test will be skipped.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetObjectParents_Relationships() throws Exception
   {
      if (!isRelationshipsSupported)
      {
         return;
      }

      List<ObjectParent> parents =
         connection.getObjectParents(doc3, false, IncludeRelationships.BOTH, false, true, null, RenditionFilter.NONE);
      assertEquals(1, parents.size());
      assertNotNull(parents.get(0));
      CmisObject parent = parents.get(0).getObject();
      List<String> relationships = new ArrayList<String>();
      for (CmisObject rel : parent.getRelationship())
      {
         relationships.add(rel.getObjectInfo().getId());
      }
      assertTrue("Expected relationship " + rel3 + " not found in result. ", relationships.size() == 1
         && relationships.contains(rel3));
   }

   /**
    * 2.2.3.5 getObjectParents.
    * <p>
    * Get the parent folder(s) for the specified non-folder, fileable object and
    * use rendition filter. Result MUST return the set of renditions whose kind
    * matches this filter.
    * <p>
    *
    * @throws Exception
    */
   @Test
   public void testGetObjectParents_RenditionsFiltered() throws Exception
   {
      List<ObjectParent> parents =
         connection.getObjectParents(doc3, false, IncludeRelationships.NONE, false, true, null, RenditionFilter.NONE);
      assertEquals(1, parents.size());
      assertNotNull(parents.get(0));
      // Since we can't be sure renditions available for all objects just check
      // we able to reject all renditions.
      assertTrue("Renditions filter works incorrect.", parents.get(0).getObject().getRenditions().size() == 0);
   }

   private void objectTreeAsMap(List<ItemsTree<CmisObject>> source, Map<String, List<String>> map)
   {
      for (ItemsTree<CmisObject> one : source)
      {
         CmisObject type = one.getContainer();
         String id = type.getObjectInfo().getId();
         List<ItemsTree<CmisObject>> children = one.getChildren();
         if (children != null)
         {
            List<String> l = new ArrayList<String>();
            if (children.size() > 0)
            {
               for (ItemsTree<CmisObject> c : children)
               {
                  l.add(c.getContainer().getObjectInfo().getId());
               }
               objectTreeAsMap(children, map);
            }
            map.put(id, l);
         }
         else
         {
            map.put(id, null);
         }
      }
   }

   private void validateTree(List<ItemsTree<CmisObject>> source, Map<String, List<String>> expected)
   {
      Map<String, List<String>> result = new HashMap<String, List<String>>();
      objectTreeAsMap(source, result);
      assertEquals(expected.size(), result.size());
      assertEquals(expected.keySet(), result.keySet());
      for (Map.Entry<String, List<String>> e : expected.entrySet())
      {
         List<String> v1 = e.getValue();
         List<String> v2 = result.get(e.getKey());
         if (v1 == null || v1.size() == 0)
         {
            assertTrue("Object " + e.getKey() + " must not have children. ", v2 == null || v2.size() == 0);
         }
         else
         {
            assertTrue("Number of children is incorrect. ", v2 != null && v2.size() > 0);
            for (String s : v1)
            {
               assertTrue("Missing item " + s + " in tree hierarchy. ", v2.contains(s));
            }
         }
      }
   }
}
