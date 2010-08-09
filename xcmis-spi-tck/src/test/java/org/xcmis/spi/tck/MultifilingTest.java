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

import org.xcmis.spi.BaseContentStream;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.DocumentData;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.Choice;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.ContentStreamAllowed;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.PropertyType;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.Updatability;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.model.impl.IdProperty;
import org.xcmis.spi.model.impl.StringProperty;
import org.xcmis.spi.utils.MimeType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.After;

/**
 * 2.2.5 The Multi-filing services (addObjectToFolder, removeObjectFromFolder) are supported only 
 * if the repository supports the multifiling or unfiling optional capabilities. 
 * The Multi-filing Services are used to file/un-file objects into/from folders.
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id$
 * 
 */
public class MultifilingTest extends BaseTest
{

   @BeforeClass
   public static void start() throws Exception
   {
      BaseTest.setUp();
      System.out.print("Running Multifiling Service tests....");
   }

   /**
    * 2.2.5.1 addObjectToFolder
    * Adds an existing fileable non-folder object to a folder.
    * 
    * @throws Exception
    */
   @Test
   public void testAddObjectToFolder() throws Exception
   {
      if (getCapabilities().isCapabilityUnfiling() && getCapabilities().isCapabilityMultifiling())
      {
         FolderData folder1 = null;
         try
         {
            folder1 = createFolder(rootFolder, "testFolder1");
            DocumentData doc1 = createDocument(null, "doc1", "doc1"); // unfiled document

            assertNull("Unfiling failed;", doc1.getParent());
            assertNotNull("Parents list is null;", doc1.getParents());
            if (doc1.getParents().size() != 0)
               fail("Parents list is not empty;");

            ItemsList<CmisObject> children0 =
               getConnection().getChildren(folder1.getObjectId(), false, null, false, true, null, null, null, -1, 0);
            assertNotNull("Unable to get children;", children0);
            assertNotNull("Children list is null;", children0.getItems());
            if (children0.getItems().size() != 0)
               fail("Clildren list is not empty;");

            getConnection().addObjectToFolder(doc1.getObjectId(), folder1.getObjectId(), true);

            ItemsList<CmisObject> children =
               getConnection().getChildren(folder1.getObjectId(), false, null, false, true, null, null, null, -1, 0);
            assertNotNull("Unable to get children;", children0);
            assertNotNull("Children list is null;", children0.getItems());
            List<CmisObject> listChildren = children.getItems();
            if (children.getItems().size() != 1)
              fail("Clildren list elements doent match;");
            for (CmisObject cmisObject : listChildren)
            {
               assertNotNull("Cannot get cmis object;",cmisObject);
               assertNotNull("Cannot get cmis object info;", cmisObject.getObjectInfo());
               assertNotNull("Cannot get cmis object ID;", cmisObject.getObjectInfo().getId());
               assertTrue("Objects doen not match;", doc1.getObjectId().equals(cmisObject.getObjectInfo().getId()));
            }
         }
         finally
         {
            clear(folder1.getObjectId());
         }
      }
      else
      {
         //SKIP
      }

   }

   /**
    * 2.2.5.1 addObjectToFolder
    * Adds an existing fileable non-folder object to a folder.
    * 
    * The Repository MUST throw this exception if the cmis:objectTypeId property value of 
    * the given object is NOT in the list of AllowedChildObjectTypeIds of 
    * the parent-folder specified by folderId.
    * 
    * cmis:allowedChildObjectTypeIds   Id’s of the set of Object-types that can be created, moved or filed into this folder.
    * 
    * @throws Exception
    */
   @Test
   public void testAddObjectToFolder_ConstraintException() throws Exception
   {
      if (getCapabilities().isCapabilityMultifiling())
      {
         FolderData folder1 = null;
         FolderData folder2 = null;
         String typeId = null;
         try
         {
            org.xcmis.spi.model.PropertyDefinition<?> def =
               PropertyDefinitions.getPropertyDefinition(CmisConstants.FOLDER, CmisConstants.NAME);
            org.xcmis.spi.model.PropertyDefinition<?> def2 =
               PropertyDefinitions.getPropertyDefinition(CmisConstants.FOLDER, CmisConstants.OBJECT_TYPE_ID);
            org.xcmis.spi.model.PropertyDefinition<?> def3 =
               PropertyDefinitions.getPropertyDefinition(CmisConstants.FOLDER,
                  CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS);
            Map<String, Property<?>> properties = new HashMap<String, Property<?>>();

            properties.put(CmisConstants.NAME, new StringProperty(def.getId(), def.getQueryName(), def.getLocalName(),
               def.getDisplayName(), "testMultifilingFolder1"));
            properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(def2.getId(), def2.getQueryName(), def2
               .getLocalName(), def2.getDisplayName(), CmisConstants.FOLDER));
            properties.put(CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, new IdProperty(def3.getId(), def3
               .getQueryName(), def3.getLocalName(), def3.getDisplayName(), CmisConstants.FOLDER));

            Map<String, Property<?>> properties2 = new HashMap<String, Property<?>>();
            properties2.put(CmisConstants.NAME, new StringProperty(def.getId(), def.getQueryName(), def.getLocalName(),
               def.getDisplayName(), "testMultifilingFolder2"));
            properties2.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(def2.getId(), def2.getQueryName(), def2
               .getLocalName(), def2.getDisplayName(), CmisConstants.FOLDER));
            properties2.put(CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, new IdProperty(def3.getId(), def3
               .getQueryName(), def3.getLocalName(), def3.getDisplayName(), CmisConstants.FOLDER));

            folder1 = getStorage().createFolder(rootFolder, folderTypeDefinition, properties, null, null);
            folder2 = getStorage().createFolder(rootFolder, folderTypeDefinition, properties2, null, null);
            //////////// CHECK the ALLOWED_CHILD_OBJECT_TYPE_IDS property
            IdProperty prop = (IdProperty)folder2.getProperties().get(CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS);
            assertNotNull("Properties is null;", prop);
            assertNotNull("Property values is null;",prop.getValues());


            if (prop.getValues().size() != 0)
            {
               // create new document type "cmis:kino"
               Map<String, PropertyDefinition<?>> kinoPropertyDefinitions =
                  new HashMap<String, PropertyDefinition<?>>();

               TypeDefinition kinoType =
                  new TypeDefinition("cmis:multifilingtype1", BaseType.DOCUMENT, "cmis:multifilingtype1", "cmis:multifilingtype1", "", "cmis:document",
                     "cmis:multifilingtype1", "cmis:multifilingtype1", true, false, true, true, false, false, false, true, null, null,
                     ContentStreamAllowed.ALLOWED, kinoPropertyDefinitions);
               typeId = getStorage().addType(kinoType);

               // get the new type definition for "cmis:kino"
               kinoType = getConnection().getTypeDefinition("cmis:multifilingtype1");

               // create document with the new type "cmis:kino"
               ContentStream cs = new BaseContentStream("doc1".getBytes(), null, new MimeType("text", "plain"));

               org.xcmis.spi.model.PropertyDefinition<?> ddef =
                  createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
                     CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false,
                     Updatability.READWRITE, "Object name.", true, null, null);

               org.xcmis.spi.model.PropertyDefinition<?> ddef2 =
                  createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID, CmisConstants.OBJECT_TYPE_ID,
                     CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false, false, false, false,
                     false, Updatability.READONLY, "Object type id.", null, null, null);

               Map<String, Property<?>> dproperties = new HashMap<String, Property<?>>();
               dproperties.put(CmisConstants.NAME, new StringProperty(ddef.getId(), ddef.getQueryName(), ddef
                  .getLocalName(), ddef.getDisplayName(), "testMultifilingdoc1"));
               dproperties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(ddef2.getId(), ddef2.getQueryName(), ddef2
                  .getLocalName(), ddef2.getDisplayName(), "cmis:multifilingtype1"));

               DocumentData docKino =
                  getStorage().createDocument(folder1, kinoType, dproperties, cs, null, null, VersioningState.NONE);

               // check folder2
               ItemsList<CmisObject> children0 =
                  getConnection().getChildren(folder2.getObjectId(), false, null, false, true, null, null, null, -1, 0);
               assertNotNull("Unable to get children;", children0);
               assertNotNull("Children list is null;", children0.getItems());
               if (children0.getItems().size() != 0)
                  fail("Clildren list is not empty;");

               // add object to folder
               getConnection().addObjectToFolder(docKino.getObjectId(), folder2.getObjectId(), true);
               fail("ConstraintException must be thrown;");
            }
         }
         catch (ConstraintException e)
         {
            //OK
         }
         finally
         {
            if (folder1 != null)
               clear(folder1.getObjectId());
            if (folder2 != null)
               clear(folder2.getObjectId());
            if (typeId != null)
               getStorage().removeType(typeId);
         }
      }
      else
      {
         //SKIP
      }
   }

   /**
    * 2.2.5.1 addObjectToFolder
    * Adds an existing fileable non-folder object to a folder.
    * 
    * @throws Exception
    */
   @Test
   public void testAddObjectToFolder_AlreadyAddedToAnotherFolder() throws Exception
   {
      if (getCapabilities().isCapabilityMultifiling())
      {
         FolderData folder1 = null;
         FolderData folder2 = null;
         try
         {
            folder1 = createFolder(rootFolder, "testAddObjectToFolder1");
            folder2 = createFolder(rootFolder, "testAddObjectToFolder2");
            DocumentData doc1 = createDocument(folder1, "testAddObjectToFolder_doc1", "doc1");

            ItemsList<CmisObject> children0 =
               getConnection().getChildren(folder2.getObjectId(), false, null, false, true, null, null, null, -1, 0);
            assertNotNull("Unable to get children;", children0);
            assertNotNull("Children list is null;", children0.getItems());
            if (children0.getItems().size() != 0)
               fail("Clildren list is not empty;");

            getConnection().addObjectToFolder(doc1.getObjectId(), folder2.getObjectId(), true);

            ItemsList<CmisObject> children =
               getConnection().getChildren(folder2.getObjectId(), false, null, false, true, null, null, null, -1, 0);
            assertNotNull("Unable to get children;", children);
            assertNotNull("Children list is null;", children.getItems());
            List<CmisObject> listChildren = children.getItems();

            assertTrue("Clildren list elements does not match;", children.getItems().size() == 1);
            for (CmisObject cmisObject : listChildren)
            {
               assertNotNull("Cannot get cmis object;",cmisObject);
               assertNotNull("Cannot get cmis object info;", cmisObject.getObjectInfo());
               assertNotNull("Cannot get cmis object ID;", cmisObject.getObjectInfo().getId());
               assertTrue("Objects doen not match;", doc1.getObjectId().equals(cmisObject.getObjectInfo().getId()));
            }
         }
         finally
         {
            if (folder1 != null)
               clear(folder1.getObjectId());
            if (folder2 != null)
               clear(folder2.getObjectId());
         }
      }
      else
      {
        //SKIP
      }
   }

   /**
    * 2.2.5.2 removeObjectFromFolder
    * Removes an existing fileable non-folder object from a folder.
    * 
    * @throws Exception
    */
   @Test
   public void testRemoveObjectFromFolder() throws Exception
   {
      FolderData folder1 = null;
      if (getCapabilities().isCapabilityUnfiling())
      {
         try
         {
            folder1 = createFolder(rootFolder, "testRemoveObjectFolder1");
            DocumentData doc1 = createDocument(null, "testRemoveObjectDoc1", "doc1");// unfiled document

            assertNull("Unfiling failed;", doc1.getParent());
            assertNotNull("Parents list is null;",doc1.getParents());
            if (doc1.getParents().size() != 0)
               fail("Parents list is not empty;");

            ItemsList<CmisObject> children0 =
               getConnection().getChildren(folder1.getObjectId(), false, null, false, true, null, null, null, -1, 0);
            assertNotNull("Unable to get children;", children0);
            assertNotNull("Children list is null;", children0.getItems());
            if (children0.getItems().size() != 0)
               fail("Clildren list is not empty;");

            getConnection().addObjectToFolder(doc1.getObjectId(), folder1.getObjectId(), true);

            ItemsList<CmisObject> children =
               getConnection().getChildren(folder1.getObjectId(), false, null, false, true, null, null, null, -1, 0);
            assertNotNull("Unable to get children;", children);
            assertNotNull("Children list is null;", children.getItems());
            List<CmisObject> listChildren = children.getItems();
            if (children.getItems().size() != 1)
               fail("Clildren list elements does not match;");
            for (CmisObject cmisObject : listChildren)
            {
               assertNotNull("Cannot get cmis object;",cmisObject);
               assertNotNull("Cannot get cmis object info;", cmisObject.getObjectInfo());
               assertNotNull("Cannot get cmis object ID;", cmisObject.getObjectInfo().getId());
               assertTrue("Objects does not match;", doc1.getObjectId().equals(cmisObject.getObjectInfo().getId()));
            }

            getConnection().removeObjectFromFolder(doc1.getObjectId(), folder1.getObjectId());

            ItemsList<CmisObject> children00 =
               getConnection().getChildren(folder1.getObjectId(), false, null, false, true, null, null, null, -1, 0);
            assertNotNull("Unable to get children;", children00);
            assertNotNull("Children list is null;", children00.getItems());
            if (children0.getItems().size() != 00)
               fail("Clildren list is not empty;");
         }
         finally
         {
            if (folder1 != null)
               clear(folder1.getObjectId());
         }
      }
      else
      {
         //SKIP
      }
   }

   /**
    * 2.2.5.2 removeObjectFromFolder
    * Removes an existing fileable non-folder object from a folder.
    * 
    * @throws Exception
    */
   @Test
   public void testRemoveObjectFromFolder_AlreadyAddedToAnotherFolder() throws Exception
   {
      FolderData folder1 = null;
      FolderData folder2 = null;
      if (getCapabilities().isCapabilityUnfiling() && getCapabilities().isCapabilityMultifiling())
      {
         try
         {
            folder1 = createFolder(rootFolder, "testRemoveObjectFromFolderFolder1");
            folder2 = createFolder(rootFolder, "testRemoveObjectFromFolderFolder2");
            DocumentData doc1 = createDocument(folder1, "testRemoveObjectFromFolderDoc1", "doc1");

            ItemsList<CmisObject> children0 =
               getConnection().getChildren(folder2.getObjectId(), false, null, false, true, null, null, null, -1, 0);
            assertNotNull("Unable to get children;", children0);
            assertNotNull("Children list is null;", children0.getItems());
            if (children0.getItems().size() != 0)
               fail("Clildren list is not empty;");

            getConnection().addObjectToFolder(doc1.getObjectId(), folder2.getObjectId(), true);
            ItemsList<CmisObject> children =
               getConnection().getChildren(folder2.getObjectId(), false, null, false, true, null, null, null, -1, 0);
            assertNotNull("Unable to get children;", children);
            assertNotNull("Children list is null;", children.getItems());

            List<CmisObject> listChildren = children.getItems();
            if (children.getItems().size() != 1)
               fail("Clildren list elements does not match;");
            for (CmisObject cmisObject : listChildren)
            {
               assertNotNull("Cannot get cmis object;",cmisObject);
               assertNotNull("Cannot get cmis object info;", cmisObject.getObjectInfo());
               assertNotNull("Cannot get cmis object ID;", cmisObject.getObjectInfo().getId());
               assertTrue("Objects doen not match;", doc1.getObjectId().equals(cmisObject.getObjectInfo().getId()));
            }
            getConnection().removeObjectFromFolder(doc1.getObjectId(), folder2.getObjectId());
            ItemsList<CmisObject> children00 =
               getConnection().getChildren(folder2.getObjectId(), false, null, false, true, null, null, null, -1, 0);
            assertNotNull("Unable to get children;", children00);
            assertNotNull("Children list is null;", children00.getItems());
            if (children0.getItems().size() != 00)
               fail("Clildren list is not empty;");
         }
         finally
         {
            if (folder1 != null)
               clear(folder1.getObjectId());
            if (folder2 != null)
               clear(folder2.getObjectId());
         }
      }
      else
      {
         //SKIP
      }
   }

   @After
   public void shutDown() throws Exception
   {
      ItemsList<CmisObject> children =
         getConnection().getChildren(rootfolderID, false, null, false, true, null, null, null, -1, 0);
      if (children != null && children.getItems() != null)
      {
         List<CmisObject> listChildren = children.getItems();
         for (CmisObject cmisObject : listChildren)
         {
            remove(cmisObject);
         }
      }
   }

   private void remove(CmisObject cmisObject) throws ObjectNotFoundException, ConstraintException,
      UpdateConflictException, VersioningException, StorageException, InvalidArgumentException, FilterNotValidException
   {

      if (cmisObject.getObjectInfo().getBaseType().equals(BaseType.FOLDER))
      {
         ItemsList<CmisObject> children =
            getConnection().getChildren(cmisObject.getObjectInfo().getId(), false, null, false, true, null, null, null,
               -1, 0);
         if (children != null && children.getItems() != null)
         {
            List<CmisObject> listChildren = children.getItems();
            for (CmisObject cmisObject0 : listChildren)
            {
               remove(cmisObject0);
            }
         }
      }
      getConnection().deleteObject(cmisObject.getObjectInfo().getId(), true);
   }

   private static <T> PropertyDefinition<T> createPropertyDefinition(String id, PropertyType propertyType,
      String queryName, String localName, String localNamespace, String displayName, boolean required,
      boolean queryable, boolean orderable, boolean inherited, boolean isMultivalued, Updatability updatability,
      String description, Boolean openChoice, List<Choice<T>> choices, T[] defValue)
   {
      PropertyDefinition<T> propertyDefinition =
         new PropertyDefinition<T>(id, queryName, localName, localNamespace, displayName, description, propertyType,
            updatability, inherited, required, queryable, orderable, openChoice, isMultivalued, choices, defValue);
      return propertyDefinition;
   }

   @AfterClass
   public static void stop() throws Exception
   {
      if (BaseTest.conn != null)
         BaseTest.conn.close();
      System.out.println("done;");
   }
}
