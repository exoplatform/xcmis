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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xcmis.spi.ChangeTokenHolder;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.NotSupportedException;
import org.xcmis.spi.RenditionFilter;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.IncludeRelationships;
import org.xcmis.spi.model.ObjectParent;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.Updatability;
import org.xcmis.spi.model.impl.IdProperty;
import org.xcmis.spi.model.impl.StringProperty;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 2.2.5 The Multi-filing services (addObjectToFolder, removeObjectFromFolder)
 * are supported only if the repository supports the multifiling or unfiling
 * optional capabilities. The Multi-filing Services are used to file/un-file
 * objects into/from folders.
 *
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey
 *         Zavizionov</a>
 * @version $Id$
 *
 */
public class MultifilingTest extends BaseTest
{

   private static String testRootFolderId;

   @BeforeClass
   public static void start() throws Exception
   {
      testRootFolderId = createFolder(rootFolderID, CmisConstants.FOLDER, "acl_testroot", null, null, null);
      System.out.println("Running Multifiling Service tests");
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
    * 2.2.5.1 addObjectToFolder Adds an existing fileable non-folder object to a
    * folder.
    *
    * @throws Exception
    */
   @Test
   public void testAddObjectToFolder() throws Exception
   {
      if (!capabilities.isCapabilityMultifiling())
      {
         return;
      }

      TypeDefinition documentType = connection.getTypeDefinition(CmisConstants.DOCUMENT);
      TypeDefinition folderType = connection.getTypeDefinition(CmisConstants.FOLDER);

      String folder0 =
         createFolder(testRootFolderId, folderType.getId(), generateName(folderType, null), null, null, null);
      String document0 =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);
      connection.addObjectToFolder(document0, folder0, true);

      List<ObjectParent> parents =
         connection.getObjectParents(document0, false, IncludeRelationships.NONE, true, true, null,
            RenditionFilter.NONE);
      Set<String> parentIDs = new HashSet<String>(parents.size());
      for (ObjectParent item : parents)
      {
         CmisObject o = item.getObject();
         parentIDs.add(o.getObjectInfo().getId());
      }
      assertEquals(2, parentIDs.size());
      assertTrue("Expected parent not found. ", parentIDs.contains(folder0));
      assertTrue("Expected parent not found. ", parentIDs.contains(testRootFolderId));

      ItemsList<CmisObject> children =
         connection.getChildren(folder0, false, IncludeRelationships.NONE, true, true, null, RenditionFilter.NONE,
            null, -1, 0);
      Set<String> childrenIDs = new HashSet<String>(children.getItems().size());
      for (CmisObject child : children.getItems())
      {
         childrenIDs.add(child.getObjectInfo().getId());
      }
      assertEquals(1, childrenIDs.size());
      assertTrue("Expected child not found. ", childrenIDs.contains(document0));
   }

   /**
    * 2.2.5.1 addObjectToFolder Adds an existing fileable non-folder object to a
    * folder.
    *
    * The Repository MUST throw this exception if the cmis:objectTypeId property
    * value of the given object is NOT in the list of AllowedChildObjectTypeIds
    * of the parent-folder specified by folderId.
    *
    * cmis:allowedChildObjectTypeIds IDs of the set of Object-types that can be
    * created, moved or filed into this folder.
    *
    * @throws Exception
    */
   @Test
   public void testAddObjectToFolder_ConstraintException() throws Exception
   {
      if (!capabilities.isCapabilityMultifiling())
      {
         return;
      }

      TypeDefinition folderType = connection.getTypeDefinition(CmisConstants.FOLDER);
      TypeDefinition documentType = connection.getTypeDefinition(CmisConstants.DOCUMENT);

      String folder0 =
         createFolder(testRootFolderId, folderType.getId(), generateName(folderType, null), null, null, null);

      StringBuilder filter = new StringBuilder();
      filter.append(CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS) //
         .append(',') //
         .append(CmisConstants.CHANGE_TOKEN);
      Map<String, Property<?>> folderProperties =
         connection.getProperties(folder0, true, filter.toString()).getProperties();

      StringProperty changeTokenProperty = (StringProperty)folderProperties.get(CmisConstants.CHANGE_TOKEN);

      TypeDefinition fileableTypeDefinition = null;

      // Check if we able set property 'cmis:allowedChildObjectTypeIds'
      PropertyDefinition<?> childIdPropertyDefinition =
         folderType.getPropertyDefinition(CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS);
      Updatability updatability = childIdPropertyDefinition.getUpdatability();
      if (updatability == Updatability.READWRITE)
      {
         // Update property if possible (cmis:document as not allowed child type)
         ChangeTokenHolder holder = new ChangeTokenHolder();
         if (changeTokenProperty != null && changeTokenProperty.getValues().size() > 0)
         {
            holder.setValue(changeTokenProperty.getValues().get(0));
         }
         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(childIdPropertyDefinition.getId(), new IdProperty(childIdPropertyDefinition.getId(),
            childIdPropertyDefinition.getQueryName(), childIdPropertyDefinition.getLocalName(),
            childIdPropertyDefinition.getDisplayName(), folderType.getId()));
         connection.updateProperties(folder0, holder, properties);

         // Only folders allowed as child from now.
         fileableTypeDefinition = documentType;
      }
      if (fileableTypeDefinition == null)
      {
         // If there is no fileable type which is disable for cmis:folder do nothing in this test.
         return;
      }

      String documentO =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);

      try
      {
         connection.addObjectToFolder(documentO, folder0, true);
         fail("ConstraintException must be thrown since type 'cmis:document' is not allowed as child.");
      }
      catch (ConstraintException e)
      {
      }
   }

   /**
    * 2.2.5.1 addObjectToFolder Adds an existing fileable non-folder object to a
    * folder. If multiling is not supported then {@link NotSupportedException}
    * must be thrown.
    *
    * @throws Exception
    */
   @Test
   public void testAddObjectToFolder_NotSupported() throws Exception
   {
      if (capabilities.isCapabilityMultifiling())
      {
         // If multi-filing feature is supported skip this test
         return;
      }
      TypeDefinition documentType = connection.getTypeDefinition(CmisConstants.DOCUMENT);
      TypeDefinition folderType = connection.getTypeDefinition(CmisConstants.FOLDER);
      String folder0 =
         createFolder(testRootFolderId, folderType.getId(), generateName(folderType, null), null, null, null);
      String documentO =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);
      try
      {
         connection.addObjectToFolder(documentO, folder0, true);
      }
      catch (NotSupportedException e)
      {
      }
   }

   /**
    * 2.2.5.2 removeObjectFromFolder Removes an existing fileable non-folder
    * object from a folder. Try to remove object (document) from latest folder
    * where it is filled. This test will be skiped if unfiling capability is not
    * supported.
    *
    * @throws Exception
    */
   @Test
   public void testRemoveObjectFromFolder() throws Exception
   {
      if (!capabilities.isCapabilityUnfiling())
      {
         return;
      }
      TypeDefinition documentType = connection.getTypeDefinition(CmisConstants.DOCUMENT);
      String document0 =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);
      // Document filled only in one folder. Try remove from this folder.
      connection.removeObjectFromFolder(document0, testRootFolderId);
      List<ObjectParent> parents =
         connection.getObjectParents(document0, false, IncludeRelationships.NONE, true, true, null,
            RenditionFilter.NONE);
      assertTrue("Parents list must be empty. ", parents.isEmpty());

      ItemsList<CmisObject> children =
         connection.getChildren(testRootFolderId, false, IncludeRelationships.NONE, true, true, null,
            RenditionFilter.NONE, null, -1, 0);
      Set<String> childrenIDs = new HashSet<String>(children.getItems().size());
      for (CmisObject child : children.getItems())
      {
         childrenIDs.add(child.getObjectInfo().getId());
      }
      assertTrue("Object must be removed from folder. ", !childrenIDs.contains(document0));
   }

   /**
    * 2.2.5.2 removeObjectFromFolder Removes an existing fileable non-folder
    * object from a folder.
    *
    * @throws Exception
    */
   @Test
   public void testRemoveObjectFromFolder2() throws Exception
   {
      if (!capabilities.isCapabilityMultifiling())
      {
         return;
      }

      TypeDefinition documentType = connection.getTypeDefinition(CmisConstants.DOCUMENT);
      TypeDefinition folderType = connection.getTypeDefinition(CmisConstants.FOLDER);

      String folder0 =
         createFolder(testRootFolderId, folderType.getId(), generateName(folderType, null), null, null, null);
      String document0 =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);

      connection.addObjectToFolder(document0, folder0, true);

      List<ObjectParent> parents =
         connection.getObjectParents(document0, false, IncludeRelationships.NONE, true, true, null,
            RenditionFilter.NONE);
      assertEquals(2, parents.size());

      ItemsList<CmisObject> children =
         connection.getChildren(folder0, false, IncludeRelationships.NONE, true, true, null, RenditionFilter.NONE,
            null, -1, 0);
      assertEquals(1, children.getItems().size());

      // Document filled in two folders.
      connection.removeObjectFromFolder(document0, folder0);

      parents =
         connection.getObjectParents(document0, false, IncludeRelationships.NONE, true, true, null,
            RenditionFilter.NONE);
      assertEquals(1, parents.size());

      children =
         connection.getChildren(folder0, false, IncludeRelationships.NONE, true, true, null, RenditionFilter.NONE,
            null, -1, 0);
      assertTrue("Object must be removed from folder. ", children.getItems().isEmpty());
   }

   /**
    * 2.2.5.2 removeObjectFromFolder Removes an existing fileable non-folder
    * object from a folder. If folder id from which object should be removed is
    * not set then object removed from all folders in which it is currently
    * filled.
    *
    * @throws Exception
    */
   @Test
   public void testUnfile() throws Exception
   {
      if (!capabilities.isCapabilityUnfiling())
      {
         return;
      }

      TypeDefinition documentType = connection.getTypeDefinition(CmisConstants.DOCUMENT);
      String document0 =
         createDocument(testRootFolderId, documentType.getId(), generateName(documentType, null), null, null, null,
            null, null);
      String folder0 = null;
      if (capabilities.isCapabilityMultifiling())
      {
         // If multifiling supported add document in one more folder.
         TypeDefinition folderType = connection.getTypeDefinition(CmisConstants.FOLDER);
         folder0 = createFolder(testRootFolderId, folderType.getId(), generateName(folderType, null), null, null, null);
         connection.addObjectToFolder(document0, folder0, true);
      }

      connection.removeObjectFromFolder(document0, null);

      // Check parents
      List<ObjectParent> parents =
         connection.getObjectParents(document0, false, IncludeRelationships.NONE, true, true, null,
            RenditionFilter.NONE);
      assertTrue("Parents list must be empty. ", parents.isEmpty());

      ItemsList<CmisObject> children =
         connection.getChildren(testRootFolderId, false, IncludeRelationships.NONE, true, true, null,
            RenditionFilter.NONE, null, -1, 0);
      Set<String> childrenIDs = new HashSet<String>(children.getItems().size());
      for (CmisObject child : children.getItems())
      {
         childrenIDs.add(child.getObjectInfo().getId());
      }
      assertTrue("Object must be removed from folder. ", !childrenIDs.contains(document0));

      if (folder0 != null)
      {
         // If add in two folders.
         children =
            connection.getChildren(folder0, false, IncludeRelationships.NONE, true, true, null, RenditionFilter.NONE,
               null, -1, 0);
         assertTrue("Object must be removed from folder. ", children.getItems().isEmpty());
      }
   }
}
