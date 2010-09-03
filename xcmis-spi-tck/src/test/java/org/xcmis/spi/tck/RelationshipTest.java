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
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.RelationshipDirection;
import org.xcmis.spi.model.TypeDefinition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class RelationshipTest extends BaseTest
{
   private static String document0;

   private static String document1;

   private static String document2;

   private static String relationship0_1;

   private static String relationship1_2;

   private static String relationship2_0;

   private static TypeDefinition relationshipType;

   private static String testRootFolderId;

   @BeforeClass
   public static void start() throws Exception
   {
      BaseTest.setUp();
      TypeDefinition folderType = connection.getTypeDefinition(CmisConstants.FOLDER);
      testRootFolderId = createFolder(rootFolderID, folderType.getId(), "relationship_testroot", null, null, null);
      if (isRelationshipsSupported)
      {
         relationshipType = connection.getTypeDefinition(CmisConstants.RELATIONSHIP);
         TypeDefinition documentType = connection.getTypeDefinition(CmisConstants.DOCUMENT);
         document0 =
            createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
               null, null);
         document1 =
            createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
               null, null);
         document2 =
            createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
               null, null);
         relationship0_1 =
            createRelationship(relationshipType.getId(), generateName(relationshipType, null), document0, document1,
               null, null, null);
         relationship1_2 =
            createRelationship(relationshipType.getId(), generateName(relationshipType, null), document1, document2,
               null, null, null);
         relationship2_0 =
            createRelationship(relationshipType.getId(), generateName(relationshipType, null), document2, document0,
               null, null, null);
      }
      System.out.println("Running Relationship Service tests");
   }

   @AfterClass
   public static void stop() throws Exception
   {
      if (testRootFolderId != null)
      {
         clear(testRootFolderId);
      }
   }

   /**
    * 2.2.8.1 getObjectRelationships.
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
      if (!isRelationshipsSupported)
      {
         return;
      }
      ItemsList<CmisObject> relationships =
         connection
            .getObjectRelationships(document0, RelationshipDirection.EITHER, null, true, false, true, null, 1, 0);
      assertTrue("Wrong number of items in result. ", relationships.getItems().size() <= 1);
   }

   /**
    * 2.2.8.1 getObjectRelationships.
    * <p>
    * Gets all or a subset of relationships associated with an independent
    * object.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetObjectRelationships() throws Exception
   {
      if (!isRelationshipsSupported)
      {
         return;
      }
      ItemsList<CmisObject> relationships =
         connection.getObjectRelationships(document0, RelationshipDirection.EITHER, null, true, false, true, null, -1,
            0);
      Set<String> ids = new HashSet<String>(relationships.getItems().size());
      for (CmisObject o : relationships.getItems())
      {
         ids.add(o.getObjectInfo().getId());
      }
      assertEquals(2, ids.size());
      assertTrue("Expected relationship " + relationship0_1 + "is not found in result. ", ids.contains(relationship0_1));
      assertTrue("Expected relationship " + relationship2_0 + "is not found in result. ", ids.contains(relationship2_0));
   }

   /**
    * 2.2.8.1 getObjectRelationships.
    * <p>
    * Gets all or a subset of relationships associated with an independent
    * object and include allowable actions.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetObjectRelationships_AllowableActions() throws Exception
   {
      if (!isRelationshipsSupported)
      {
         return;
      }
      ItemsList<CmisObject> relationships =
         connection
            .getObjectRelationships(document0, RelationshipDirection.EITHER, null, true, true, true, null, -1, 0);
      for (CmisObject o : relationships.getItems())
      {
         assertNotNull("Allowable actions must be include in response. ", o.getAllowableActions());
      }
   }

   /**
    * 2.2.8.1 getObjectRelationships.
    * <p>
    * Gets all or a subset of relationships associated with an independent
    * object with respect to direction argument.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetObjectRelationships_Direction() throws Exception
   {
      if (!isRelationshipsSupported)
      {
         return;
      }
      ItemsList<CmisObject> relationships =
         connection.getObjectRelationships(document0, RelationshipDirection.SOURCE, null, true, false, true, null, -1,
            0);
      Set<String> ids = new HashSet<String>(relationships.getItems().size());
      for (CmisObject o : relationships.getItems())
      {
         ids.add(o.getObjectInfo().getId());
      }
      assertEquals(1, ids.size());
      assertTrue("Expected relationship " + relationship0_1 + "is not found in result. ", ids.contains(relationship0_1));
   }

   /**
    * 2.2.8.1 getObjectRelationships.
    * <p>
    * See section 2.2.1.1 "Paging". If optional attribute 'skipCount' is
    * specified then specified number of items must be skipped in result.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetObjectRelationships_SkipCount() throws Exception
   {
      if (!isRelationshipsSupported)
      {
         return;
      }
      ItemsList<CmisObject> relationships =
         connection.getObjectRelationships(document0, RelationshipDirection.EITHER, null, true, false, true, null, -1,
            0);
      // Get all items first.
      List<String> relationshipIDs = new ArrayList<String>(2);
      for (CmisObject o : relationships.getItems())
      {
         relationshipIDs.add(o.getObjectInfo().getId());
      }
      assertEquals(2, relationshipIDs.size());

      relationships =
         connection.getObjectRelationships(document0, RelationshipDirection.EITHER, null, true, false, true, null, -1,
            1);
      List<String> relationshipIDsPage = new ArrayList<String>(6);
      for (CmisObject o : relationships.getItems())
      {
         relationshipIDsPage.add(o.getObjectInfo().getId());
      }
      assertEquals(1, relationshipIDsPage.size());

      // Skip 1 items.
      Iterator<String> iterator0 = relationshipIDs.iterator();
      iterator0.next();
      iterator0.remove();

      assertEquals(relationshipIDs, relationshipIDsPage);
   }

}
