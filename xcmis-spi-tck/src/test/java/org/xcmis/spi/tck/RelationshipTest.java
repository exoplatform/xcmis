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

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.xcmis.spi.BaseContentStream;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.DocumentData;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.PropertyFilter;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.model.AllowableActions;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.ContentStreamAllowed;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.PropertyType;
import org.xcmis.spi.model.RelationshipDirection;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.Updatability;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.model.impl.IdProperty;
import org.xcmis.spi.model.impl.StringProperty;
import org.xcmis.spi.utils.MimeType;

public class RelationshipTest extends BaseTest
{

   @BeforeClass
   public static void start() throws Exception
   {
      BaseTest.setUp();
   }

   /**
    * 2.2.8.1
    * Gets all or a subset of relationships associated with an independent object.
    * @throws Exception
    */
   @Test
   public void testGetObjectRelationships_Simple() throws Exception
   {
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }
      FolderData testroot = null;
      RelationshipData reldata = null;
      RelationshipData reldata2 = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);
         DocumentData doc2 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc2"),
               cs, null, null, VersioningState.MAJOR);
         DocumentData doc3 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc3"),
               cs, null, null, VersioningState.MAJOR);

         reldata =
            getStorage().createRelationship(doc1, doc2, relationshipTypeDefinition,
               getPropsMap(CmisConstants.RELATIONSHIP, "rel1"), null, null);

         reldata2 =
            getStorage().createRelationship(doc2, doc3, relationshipTypeDefinition,
               getPropsMap(CmisConstants.RELATIONSHIP, "rel2"), null, null);

         ItemsList<CmisObject> obj =
            getConnection().getObjectRelationships(doc2.getObjectId(), RelationshipDirection.EITHER, null, true, true,
               true, "", -1, 0);
         assertTrue("Unexpected items number;", obj.getItems().size() == 2);
      }
      finally
      {
         getStorage().deleteObject(reldata, true);
         getStorage().deleteObject(reldata2, true);
         if (testroot != null)
            clear(testroot.getObjectId());
      }
   }

   /**
    * 2.2.8.1.1
    * An enumeration specifying whether the Repository MUST return relationships where the 
    * specified Object is the source of the relationship, the target of the relationship, or both.
    * @throws Exception
    */
   @Test
   public void testGetObjectRelationships_CheckDirection() throws Exception
   {
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }
      FolderData testroot = null;
      RelationshipData reldata = null;
      RelationshipData reldata2 = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);

         DocumentData doc2 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc2"),
               cs, null, null, VersioningState.MAJOR);

         DocumentData doc3 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc3"),
               cs, null, null, VersioningState.MAJOR);

         reldata =
            getStorage().createRelationship(doc1, doc2, relationshipTypeDefinition,
               getPropsMap(CmisConstants.RELATIONSHIP, "rel1"), null, null);

         reldata2 =
            getStorage().createRelationship(doc2, doc3, relationshipTypeDefinition,
               getPropsMap(CmisConstants.RELATIONSHIP, "rel2"), null, null);

         ItemsList<CmisObject> obj =
            getConnection().getObjectRelationships(doc2.getObjectId(), RelationshipDirection.TARGET, null, true, true,
               true, PropertyFilter.ALL, -1, 0);
         assertTrue("Unexpected items number;", obj.getItems().size() == 1);
      }
      finally
      {
         getStorage().deleteObject(reldata, true);
         getStorage().deleteObject(reldata2, true);
         if (testroot != null)
            clear(testroot.getObjectId());
      }
   }

   /**
    * 2.2.8.1.1
    * If TRUE, then the Repository MUST return the available actions for each object in the result set.
    * @throws Exception
    */
   @Test
   public void testGetObjectRelationships_AllowableActions() throws Exception
   {
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }
      FolderData testroot = null;
      RelationshipData reldata = null;
      RelationshipData reldata2 = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);

         DocumentData doc2 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc2"),
               cs, null, null, VersioningState.MAJOR);

         DocumentData doc3 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc3"),
               cs, null, null, VersioningState.MAJOR);

         reldata =
            getStorage().createRelationship(doc1, doc2, relationshipTypeDefinition,
               getPropsMap(CmisConstants.RELATIONSHIP, "rel1"), null, null);

         reldata2 =
            getStorage().createRelationship(doc2, doc3, relationshipTypeDefinition,
               getPropsMap(CmisConstants.RELATIONSHIP, "rel2"), null, null);

         ItemsList<CmisObject> obj =
            getConnection().getObjectRelationships(doc2.getObjectId(), RelationshipDirection.TARGET, null, true, true,
               true, "", -1, 0);
         for (CmisObject one : obj.getItems())
         {
            AllowableActions actions = one.getAllowableActions();
            assertNotNull("Allowable actions not found;", actions);
         }
      }
      finally
      {
         getStorage().deleteObject(reldata, true);
         getStorage().deleteObject(reldata2, true);
         if (testroot != null)
            clear(testroot.getObjectId());
      }
   }

   /**
    * 2.2.8.1.1
    * If specified, then the Repository MUST return only relationships whose Object-Type is of the type specified.
    * @throws Exception
    */
   @Test
   public void testGetObjectRelationships_TypeId() throws Exception
   {
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }
      FolderData testroot = null;
      String typeID = null;
      RelationshipData reldata = null;
      RelationshipData reldata2 = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);

         DocumentData doc2 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc2"),
               cs, null, null, VersioningState.MAJOR);

         DocumentData doc3 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc3"),
               cs, null, null, VersioningState.MAJOR);

         Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "f1", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "rel2"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.RELATIONSHIP, "cmis:kino", "cmis:kino", "", "cmis:relationship",
               "cmis:kino", "cmis:kino", true, false, true, true, false, false, false, false, null, null,
               ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         reldata =
            getStorage().createRelationship(doc1, doc2, relationshipTypeDefinition,
               getPropsMap(CmisConstants.RELATIONSHIP, "rel1"), null, null);

         reldata2 = getStorage().createRelationship(doc2, doc3, newType, properties, null, null);

         ItemsList<CmisObject> obj =
            getConnection().getObjectRelationships(doc2.getObjectId(), RelationshipDirection.EITHER, "cmis:kino", true,
               true, true, "", -1, 0);
         assertTrue("Unexpected items number;", obj.getItems().size() == 1);
      }
      finally
      {
         getStorage().deleteObject(reldata, true);
         getStorage().deleteObject(reldata2, true);
         if (testroot != null)
            clear(testroot.getObjectId());
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.8.1.1
    * If TRUE, then the Repository MUST return all relationships whose Object-Types are descendant-types of the given object’s 
    * cmis:objectTypeId property value as well as relationships of the specified type. 
    * @throws Exception
    */
   @Test
   public void testGetObjectRelationships_IncludeSubrelationshipTypes() throws Exception
   {
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }
      FolderData testroot = null;
      String typeID = null;
      RelationshipData reldata = null;
      RelationshipData reldata2 = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);

         DocumentData doc2 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc2"),
               cs, null, null, VersioningState.MAJOR);

         DocumentData doc3 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc3"),
               cs, null, null, VersioningState.MAJOR);

         Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "f1", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> def2 =
            PropertyDefinitions.getPropertyDefinition(CmisConstants.POLICY, CmisConstants.POLICY_TEXT);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "rel2"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.RELATIONSHIP, "cmis:kino", "cmis:kino", "", "cmis:relationship",
               "cmis:kino", "cmis:kino", true, false, true, true, false, false, false, false, null, null,
               ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         reldata =
            getStorage().createRelationship(doc1, doc2, relationshipTypeDefinition,
               getPropsMap("cmis:relationship", "rel1"), null, null);

         reldata2 = getStorage().createRelationship(doc2, doc3, newType, properties, null, null);

         ItemsList<CmisObject> obj =
            getConnection().getObjectRelationships(doc2.getObjectId(), RelationshipDirection.EITHER,
               CmisConstants.RELATIONSHIP, true, true, true, "", -1, 0);
         assertTrue("Unexpected items number;",obj.getItems().size() == 2);

         ItemsList<CmisObject> obj2 =
            getConnection().getObjectRelationships(doc2.getObjectId(), RelationshipDirection.EITHER,
               CmisConstants.RELATIONSHIP, false, true, true, "", -1, 0);
         assertTrue("Unexpected items number;", obj2.getItems().size() == 1);
      }
      finally
      {
         getStorage().deleteObject(reldata, true);
         getStorage().deleteObject(reldata2, true);
         if (testroot != null)
            clear(testroot.getObjectId());
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.8.1.1
    * This is the maximum number of items to return in a response.  
    * The repository MUST NOT exceed this maximum.
    * @throws Exception
    */
   @Test
   public void testGetObjectRelationships_MaxItems() throws Exception
   {
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }
      FolderData testroot = null;
      RelationshipData reldata = null;
      RelationshipData reldata2 = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);

         DocumentData doc2 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc2"),
               cs, null, null, VersioningState.MAJOR);

         DocumentData doc3 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc3"),
               cs, null, null, VersioningState.MAJOR);

         reldata =
            getStorage().createRelationship(doc1, doc2, relationshipTypeDefinition,
               getPropsMap(CmisConstants.RELATIONSHIP, "rel1"), null, null);

         reldata2 =
            getStorage().createRelationship(doc2, doc3, relationshipTypeDefinition,
               getPropsMap(CmisConstants.RELATIONSHIP, "rel2"), null, null);

         ItemsList<CmisObject> obj =
            getConnection().getObjectRelationships(doc2.getObjectId(), RelationshipDirection.EITHER, null, true, true,
               true, "", 1, 0);
         assertTrue("Unexpected items number;",obj.getItems().size() == 1);
      }
      finally
      {
         getStorage().deleteObject(reldata, true);
         getStorage().deleteObject(reldata2, true);
         if (testroot != null)
            clear(testroot.getObjectId());
      }
   }

   /**
    * 2.2.8.1.1
    * This is the number of potential results that the repository 
    * MUST skip/page over before returning any results.  
    * @throws Exception
    */
   @Test
   public void testGetObjectRelationships_SkipCount() throws Exception
   {
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }
      FolderData testroot = null;
      RelationshipData reldata = null;
      RelationshipData reldata2 = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);

         DocumentData doc2 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc2"),
               cs, null, null, VersioningState.MAJOR);

         DocumentData doc3 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc3"),
               cs, null, null, VersioningState.MAJOR);

         reldata =
            getStorage().createRelationship(doc1, doc2, relationshipTypeDefinition,
               getPropsMap(CmisConstants.RELATIONSHIP, "rel1"), null, null);

         reldata2 =
            getStorage().createRelationship(doc2, doc3, relationshipTypeDefinition,
               getPropsMap(CmisConstants.RELATIONSHIP, "rel2"), null, null);

         ItemsList<CmisObject> obj =
            getConnection().getObjectRelationships(doc2.getObjectId(), RelationshipDirection.EITHER, null, true, true,
               true, "", -1, 1);
         assertTrue("Unexpected items number;",obj.getItems().size() == 1);
      }
      finally
      {
         getStorage().deleteObject(reldata, true);
         getStorage().deleteObject(reldata2, true);
         if (testroot != null)
            clear(testroot.getObjectId());
      }
   }

   /**
    * 2.2.8.1.1
    * If the repository knows the total number of items in a result set, the repository SHOULD include the number here.
    * •  Boolean hasMoreItems: TRUE if the Repository contains additional items after those contained in the response.  FALSE otherwise. 
    * @throws Exception
    */
   @Test
   public void testGetObjectRelationships_Paging() throws Exception
   {
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }
      FolderData testroot = null;
      RelationshipData reldata = null;
      RelationshipData reldata2 = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);

         DocumentData doc2 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc2"),
               cs, null, null, VersioningState.MAJOR);

         DocumentData doc3 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc3"),
               cs, null, null, VersioningState.MAJOR);

         reldata =
            getStorage().createRelationship(doc1, doc2, relationshipTypeDefinition,
               getPropsMap(CmisConstants.RELATIONSHIP, "rel1"), null, null);

         reldata2 =
            getStorage().createRelationship(doc2, doc3, relationshipTypeDefinition,
               getPropsMap(CmisConstants.RELATIONSHIP, "rel2"), null, null);

         ItemsList<CmisObject> obj =
            getConnection().getObjectRelationships(doc2.getObjectId(), RelationshipDirection.EITHER, null, true, true,
               true, "", 1, 0);
         assertTrue("Unexpected items number;",obj.getItems().size() == 1);

         if (obj.getNumItems() == 2 || obj.getNumItems() == -1)
         {
            //OK
         }
         else
            fail("Unexpected items number;");
         if (!obj.isHasMoreItems())
            fail("Has more items value is incorrect");
      }
      finally
      {
         getStorage().deleteObject(reldata, true);
         getStorage().deleteObject(reldata2, true);
         if (testroot != null)
            clear(testroot.getObjectId());
      }
   }

   /**
    * 2.2.8.1.3
    * Repositories SHOULD return only the properties specified in the property filter 
    * if they exist on the object’s type definition.
    * @throws Exception
    */
   @Test
   public void testGetObjectRelationships_PropertyFiltered() throws Exception
   {
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }
      FolderData testroot = null;
      RelationshipData reldata = null;
      RelationshipData reldata2 = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);

         DocumentData doc2 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc2"),
               cs, null, null, VersioningState.MAJOR);

         DocumentData doc3 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc3"),
               cs, null, null, VersioningState.MAJOR);

         reldata =
            getStorage().createRelationship(doc1, doc2, relationshipTypeDefinition,
               getPropsMap(CmisConstants.RELATIONSHIP, "rel1"), null, null);

         reldata2 =
            getStorage().createRelationship(doc2, doc3, relationshipTypeDefinition,
               getPropsMap(CmisConstants.RELATIONSHIP, "rel2"), null, null);

         ItemsList<CmisObject> obj =
            getConnection().getObjectRelationships(doc2.getObjectId(), RelationshipDirection.EITHER, null, true, true,
               true, "cmis:name,cmis:path", -1, 0);
         assertTrue("Unexpected items number;", obj.getItems().size() == 2);

         for (CmisObject one : obj.getItems())
         {
            for (Map.Entry<String, Property<?>> e : one.getProperties().entrySet())
            {
               if (e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path"))
                  continue;//Other props must be ignored
               else
                  fail("Property filter works incorrect");
            }
         }
      }
      finally
      {
         getStorage().deleteObject(reldata, true);
         getStorage().deleteObject(reldata2, true);
         if (testroot != null)
            clear(testroot.getObjectId());
      }
   }

   /**
    * 2.2.8.1.3
    * The Repository MUST throw this exception if this property filter input parameter is not valid.
    * @throws Exception
    */
   @Test
   public void testGetObjectRelationships_FilterNotValidException() throws Exception
   {
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }
      FolderData testroot = null;
      RelationshipData reldata = null;
      RelationshipData reldata2 = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);

         DocumentData doc2 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc2"),
               cs, null, null, VersioningState.MAJOR);

         DocumentData doc3 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc3"),
               cs, null, null, VersioningState.MAJOR);

         reldata =
            getStorage().createRelationship(doc1, doc2, relationshipTypeDefinition,
               getPropsMap(CmisConstants.RELATIONSHIP, "rel1"), null, null);

         reldata2 =
            getStorage().createRelationship(doc2, doc3, relationshipTypeDefinition,
               getPropsMap(CmisConstants.RELATIONSHIP, "rel2"), null, null);

         ItemsList<CmisObject> obj =
            getConnection().getObjectRelationships(doc2.getObjectId(), RelationshipDirection.EITHER, null, true, true,
               true, "(,*", -1, 0);
         fail("FilterNotValidException must be thrown;");
      }
      catch (FilterNotValidException ex)
      {
        //OK
      }
      finally
      {
         getStorage().deleteObject(reldata, true);
         getStorage().deleteObject(reldata2, true);
         if (testroot != null)
            clear(testroot.getObjectId());
      }
   }

   @AfterClass
   public static void stop() throws Exception
   {
      if (BaseTest.conn != null)
         BaseTest.conn.close();
   }

}
