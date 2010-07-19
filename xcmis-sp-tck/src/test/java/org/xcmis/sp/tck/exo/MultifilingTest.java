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

   @Override
   public void setUp() throws Exception
   {
      super.setUp();
   }

   /**
    * 2.2.5.1 addObjectToFolder
    * Adds an existing fileable non-folder object to a folder.
    * 
    * @throws Exception
    */
   public void testAddObjectToFolder() throws Exception
   {
      System.out.print("Running testAddObjectToFolder....                                          ");
      if (getCapabilities().isCapabilityUnfiling() && getCapabilities().isCapabilityMultifiling())
      {
         FolderData folder1 = null;
         try
         {
            folder1 = createFolder(rootFolder, "testFolder1");
            DocumentData doc1 = createDocument(null, "doc1", "doc1"); // unfiled document

            assertNull(doc1.getParent());
            assertNotNull(doc1.getParents());
            assertSame(0, doc1.getParents().size());

            ItemsList<CmisObject> children0 =
               getConnection().getChildren(folder1.getObjectId(), false, null, false, true, null, null, null, -1, 0);
            assertNotNull(children0);
            assertNotNull(children0.getItems());
            assertEquals("Should be no documents here", 0, children0.getItems().size());

            getConnection().addObjectToFolder(doc1.getObjectId(), folder1.getObjectId(), true);

            ItemsList<CmisObject> children =
               getConnection().getChildren(folder1.getObjectId(), false, null, false, true, null, null, null, -1, 0);
            assertNotNull(children);
            assertNotNull(children.getItems());
            List<CmisObject> listChildren = children.getItems();
            assertEquals("Should be a one document here, which was added in addObjectToFolder", 1, children.getItems()
               .size());
            for (CmisObject cmisObject : listChildren)
            {
               assertNotNull(cmisObject);
               assertNotNull(cmisObject.getObjectInfo());
               assertNotNull(cmisObject.getObjectInfo().getId());
               assertEquals(doc1.getObjectId(), cmisObject.getObjectInfo().getId());
            }
            pass();
         }
         catch (Exception ez)
         {
            doFail(ez.getMessage());
         }
         finally
         {
            clear(folder1.getObjectId());
         }
      }
      else
      {
         skip();
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
   public void testAddObjectToFolder_ConstraintException() throws Exception
   {
      System.out.print("Running testAddObjectToFolder_ConstraintException....                      ");
      if (getCapabilities().isCapabilityMultifiling())
      {
         FolderData folder1 = null;
         FolderData folder2 = null;
         String typeId = null;
         try
         {
            // create folder1 for document
            folder1 = createFolder(rootFolder, "testFolder1");

            // create folder2 for multifilling document
            folder2 = createFolder(rootFolder, "testFolder2");

            //////////// CHECK the ALLOWED_CHILD_OBJECT_TYPE_IDS property

            IdProperty prop = (IdProperty)folder2.getProperties().get("cmis:allowedChildObjectTypeIds");
            assertNotNull(prop);
            assertNotNull(prop.getValues());

            if (prop.getValues().size() != 0)
            {
               // create new document type "cmis:kino"
               Map<String, PropertyDefinition<?>> kinoPropertyDefinitions =
                  new HashMap<String, PropertyDefinition<?>>();

               TypeDefinition kinoType =
                  new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document",
                     "cmis:kino", "cmis:kino", true, false, true, true, false, false, false, true, null, null,
                     ContentStreamAllowed.ALLOWED, kinoPropertyDefinitions);
               typeId = getStorage().addType(kinoType);

               // get the new type definition for "cmis:kino"
               kinoType = getConnection().getTypeDefinition("cmis:kino");

               // create document with the new type "cmis:kino"
               ContentStream cs = new BaseContentStream("doc1".getBytes(), null, new MimeType("text", "plain"));

               org.xcmis.spi.model.PropertyDefinition<?> def =
                  createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
                     CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false,
                     Updatability.READWRITE, "Object name.", true, null, null);

               org.xcmis.spi.model.PropertyDefinition<?> def2 =
                  createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID, CmisConstants.OBJECT_TYPE_ID,
                     CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false, false, false, false,
                     false, Updatability.READONLY, "Object type id.", null, null, null);

               Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
               properties.put(CmisConstants.NAME, new StringProperty(def.getId(), def.getQueryName(), def
                  .getLocalName(), def.getDisplayName(), "doc1"));
               properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(def2.getId(), def2.getQueryName(), def2
                  .getLocalName(), def2.getDisplayName(), "cmis:kino"));

               DocumentData docKino =
                  getStorage().createDocument(folder1, kinoType, properties, cs, null, null, VersioningState.MAJOR);

               // check folder2
               ItemsList<CmisObject> children0 =
                  getConnection().getChildren(folder2.getObjectId(), false, null, false, true, null, null, null, -1, 0);
               assertNotNull(children0);
               assertNotNull(children0.getItems());
               assertEquals("Should be no documents here", 0, children0.getItems().size());

               // add object to folder
               try
               {
                  getConnection().addObjectToFolder(docKino.getObjectId(), folder2.getObjectId(), true);
                  doFail();
               }
               catch (ConstraintException e)
               {
                  pass();
               }
               catch (Exception other)
               {
                  doFail(other.getMessage());
               }
            }
         }
         catch (Exception ez)
         {
            doFail(ez.getMessage());
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
         skip();
      }
   }

   /**
    * 2.2.5.1 addObjectToFolder
    * Adds an existing fileable non-folder object to a folder.
    * 
    * @throws Exception
    */
   public void testAddObjectToFolder_AlreadyAddedToAnotherFolder() throws Exception
   {
      System.out.print("Running testAddObjectToFolder_AlreadyAddedToAnotherFolder....              ");
      if (getCapabilities().isCapabilityMultifiling())
      {
         FolderData folder1 = null;
         FolderData folder2 = null;
         try
         {
            folder1 = createFolder(rootFolder, "testFolder1");
            folder2 = createFolder(rootFolder, "testFolder2");
            DocumentData doc1 = createDocument(folder1, "doc1", "doc1");

            ItemsList<CmisObject> children0 =
               getConnection().getChildren(folder2.getObjectId(), false, null, false, true, null, null, null, -1, 0);
            assertNotNull(children0);
            assertNotNull(children0.getItems());
            assertEquals("Should be no documents here", 0, children0.getItems().size());

            getConnection().addObjectToFolder(doc1.getObjectId(), folder2.getObjectId(), true);

            ItemsList<CmisObject> children =
               getConnection().getChildren(folder2.getObjectId(), false, null, false, true, null, null, null, -1, 0);
            assertNotNull(children);
            assertNotNull(children.getItems());
            List<CmisObject> listChildren = children.getItems();
            assertEquals("Should be a one document here, which was added as addObjectToFolder", 1, children.getItems()
               .size());
            for (CmisObject cmisObject : listChildren)
            {
               assertNotNull(cmisObject);
               assertNotNull(cmisObject.getObjectInfo());
               assertNotNull(cmisObject.getObjectInfo().getId());
               assertEquals(doc1.getObjectId(), cmisObject.getObjectInfo().getId());
            }
         }
         catch (Exception ez)
         {
            doFail(ez.getMessage());
         }
         finally
         {
            pass();
            if (folder1 != null)
               clear(folder1.getObjectId());
            if (folder2 != null)
               clear(folder2.getObjectId());
         }
      }
      else
      {
         skip();
      }
   }

   /**
    * 2.2.5.2 removeObjectFromFolder
    * Removes an existing fileable non-folder object from a folder.
    * 
    * @throws Exception
    */
   public void testRemoveObjectFromFolder() throws Exception
   {
      System.out.print("Running testRemoveObjectFromFolder....                                     ");
      FolderData folder1 = null;
      if (getCapabilities().isCapabilityUnfiling())
      {
         try
         {
            folder1 = createFolder(rootFolder, "testFolder1");
            DocumentData doc1 = createDocument(null, "doc1", "doc1");// unfiled document

            assertNull(doc1.getParent());
            assertNotNull(doc1.getParents());
            assertSame(0, doc1.getParents().size());

            ItemsList<CmisObject> children0 =
               getConnection().getChildren(folder1.getObjectId(), false, null, false, true, null, null, null, -1, 0);
            assertNotNull(children0);
            assertNotNull(children0.getItems());
            assertEquals("Should be no documents here", 0, children0.getItems().size());

            getConnection().addObjectToFolder(doc1.getObjectId(), folder1.getObjectId(), true);

            ItemsList<CmisObject> children =
               getConnection().getChildren(folder1.getObjectId(), false, null, false, true, null, null, null, -1, 0);
            assertNotNull(children);
            assertNotNull(children.getItems());
            List<CmisObject> listChildren = children.getItems();
            assertEquals("Should be a one document here, which was added as addObjectToFolder", 1, children.getItems()
               .size());
            for (CmisObject cmisObject : listChildren)
            {
               assertNotNull(cmisObject);
               assertNotNull(cmisObject.getObjectInfo());
               assertNotNull(cmisObject.getObjectInfo().getId());
               assertEquals(doc1.getObjectId(), cmisObject.getObjectInfo().getId());
            }

            getConnection().removeObjectFromFolder(doc1.getObjectId(), folder1.getObjectId());

            ItemsList<CmisObject> children00 =
               getConnection().getChildren(folder1.getObjectId(), false, null, false, true, null, null, null, -1, 0);
            assertNotNull(children00);
            assertNotNull(children00.getItems());
            assertEquals("Should be no documents here", 0, children00.getItems().size());
            pass();
         }
         catch (Exception ez)
         {
            doFail(ez.getMessage());
         }
         finally
         {
            if (folder1 != null)
               clear(folder1.getObjectId());
         }
      }
      else
      {
         skip();
      }
   }

   /**
    * 2.2.5.2 removeObjectFromFolder
    * Removes an existing fileable non-folder object from a folder.
    * 
    * @throws Exception
    */
   public void testRemoveObjectFromFolder_AlreadyAddedToAnotherFolder() throws Exception
   {
      System.out.print("Running testRemoveObjectFromFolder_AlreadyAddedToAnotherFolder....         ");
      FolderData folder1 = null;
      FolderData folder2 = null;
      if (getCapabilities().isCapabilityUnfiling() && getCapabilities().isCapabilityMultifiling())
      {
         try
         {
            folder1 = createFolder(rootFolder, "testFolder1");
            folder2 = createFolder(rootFolder, "testFolder2");
            DocumentData doc1 = createDocument(folder1, "doc1", "doc1");

            ItemsList<CmisObject> children0 =
               getConnection().getChildren(folder2.getObjectId(), false, null, false, true, null, null, null, -1, 0);
            assertNotNull(children0);
            assertNotNull(children0.getItems());
            assertEquals("Should be no documents here", 0, children0.getItems().size());

            getConnection().addObjectToFolder(doc1.getObjectId(), folder2.getObjectId(), true);

            ItemsList<CmisObject> children =
               getConnection().getChildren(folder2.getObjectId(), false, null, false, true, null, null, null, -1, 0);
            assertNotNull(children);
            assertNotNull(children.getItems());
            List<CmisObject> listChildren = children.getItems();
            assertEquals("Should be a one document here, which was added as addObjectToFolder", 1, children.getItems()
               .size());
            for (CmisObject cmisObject : listChildren)
            {
               assertNotNull(cmisObject);
               assertNotNull(cmisObject.getObjectInfo());
               assertNotNull(cmisObject.getObjectInfo().getId());
               assertEquals(doc1.getObjectId(), cmisObject.getObjectInfo().getId());
            }

            getConnection().removeObjectFromFolder(doc1.getObjectId(), folder2.getObjectId());

            ItemsList<CmisObject> children00 =
               getConnection().getChildren(folder2.getObjectId(), false, null, false, true, null, null, null, -1, 0);
            assertNotNull(children00);
            assertNotNull(children00.getItems());
            assertEquals("Should be no documents here", 0, children00.getItems().size());
            pass();
         }
         catch (Exception ez)
         {
            doFail(ez.getMessage());
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
         skip();
      }
   }

   /**
    * @see org.xcmis.sp.tck.exo.BaseTest#tearDown()
    */
   @Override
   protected void tearDown() throws Exception
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
      super.tearDown();
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
}
