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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xcmis.spi.BaseContentStream;
import org.xcmis.spi.ChangeTokenHolder;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentAlreadyExistsException;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.DocumentData;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.NotSupportedException;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.PolicyData;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.AllowableActions;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.CapabilityACL;
import org.xcmis.spi.model.CapabilityRendition;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.ContentStreamAllowed;
import org.xcmis.spi.model.IncludeRelationships;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.PropertyType;
import org.xcmis.spi.model.Rendition;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.UnfileObject;
import org.xcmis.spi.model.Updatability;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.model.impl.IdProperty;
import org.xcmis.spi.model.impl.StringProperty;
import org.xcmis.spi.utils.MimeType;

public class ObjectTest extends BaseTest
{

   @BeforeClass
   public static void start() throws Exception
   {
      BaseTest.setUp();
   }

   /**
    * 2.2.4.1.1
    * The Content Stream that MUST be stored for the 
    * newly-created Document Object. The method of passing the contentStream 
    * to the server and the encoding mechanism will be specified by each specific binding. 
    */
   @Test
   public void testCreateDocument_CheckContent() throws Exception
   {
      String testname = "testCreateDocument_CheckContent";
      System.out.print("Running " + testname + "....                                ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         byte[] before = new byte[15];
         before = "1234567890aBcDE".getBytes();
         ContentStream cs = new BaseContentStream(before, null, new MimeType("text", "plain"));
         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         try
         {
            String docId =
               getConnection().createDocument(testroot.getObjectId(), getPropsMap(CmisConstants.DOCUMENT, "doc1"), cs,
                  null, null, null, VersioningState.MAJOR);
            ContentStream c = getStorage().getObjectById(docId).getContentStream(null);
            if (!cs.getMediaType().equals(c.getMediaType()))
               doFail(testname, "Media types does not match");

            byte[] after = new byte[15];
            c.getStream().read(after);
            assertArrayEquals(before, after);
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());
      }
   }

   /**
    * 2.2.4.1.1
    * The property values that MUST be applied to the newly-created Document Object.
    * @throws Exception
    */
   @Test
   public void testCreateDocument_CheckProperties() throws Exception
   {
      String testname = "testCreateDocument_CheckProperties";
      System.out.print("Running " + testname + "....                             ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         Map<String, Property<?>> properties = getPropsMap(CmisConstants.DOCUMENT, "doc1");
         try
         {
            String docId =
               getConnection().createDocument(testroot.getObjectId(), properties, cs, null, null, null,
                  VersioningState.MAJOR);
            ObjectData res = getStorage().getObjectById(docId);
            if (res.getProperty(CmisConstants.NAME) == null)
               doFail(testname, "NAME property is null;");
            if (!((String)res.getProperty(CmisConstants.NAME).getValues().get(0)).equals("doc1")) //TODO: test more properties
               doFail(testname, "Names does not match;");
            pass(testname);

         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.1.1
    * A list of policy IDs that MUST be applied to the newly-created Document object. 
    * @throws Exception
    */
   @Test
   public void testCreateDocument_ApplyPolicy() throws Exception
   {
      String testname = "testCreateDocument_ApplyPolicy";
      System.out.print("Running " + testname + "....                                 ");
      if (!IS_POLICIES_SUPPORTED)
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      PolicyData policy = null;
      String docId = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         policy = createPolicy(testroot, "policy1");

         ArrayList<String> policies = new ArrayList<String>();
         policies.add(policy.getObjectId());

         try
         {
            docId =
               getConnection().createDocument(testroot.getObjectId(), getPropsMap(CmisConstants.DOCUMENT, "doc1"), cs,
                  null, null, policies, VersioningState.MAJOR);
            ObjectData res = getStorage().getObjectById(docId);
            if (res.getPolicies().size() != 1)
               doFail(testname, "Properties size iz incorrect");
            Iterator<PolicyData> it = res.getPolicies().iterator();
            while (it.hasNext())
            {
               PolicyData one = it.next();
               if (!one.getName().equals("policy1"))
                  doFail(testname, "Policy names does not match");
               if (!one.getPolicyText().equals("testPolicyText"))
                  doFail(testname, "Policy text does not match");
            }
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (docId != null)
            getStorage().deleteObject(getStorage().getObjectById(docId), true);
         if (policy != null)
            getStorage().deleteObject(policy, true);
         if (testroot != null)
            clear(testroot.getObjectId());;
      }

   }

   /**
    * 2.2.4.1.1
    *   A list of ACEs that MUST be added to the newly-created Document object, 
    *   either using the ACL from folderId if specified, or being applied if no folderId is specified. 
    * @throws Exception
    */
   @Test
   public void testCreateDocument_AddACL() throws Exception
   {
      String testname = "testCreateDocument_AddACL";
      System.out.print("Running " + testname + "....                                    ");
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         AccessControlEntry acl = new AccessControlEntry();
         acl.setPrincipal("Makis");
         acl.getPermissions().add("cmis:read");
         ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
         addACL.add(acl);
         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         try
         {
            String docId =
               getConnection().createDocument(testroot.getObjectId(), getPropsMap(CmisConstants.DOCUMENT, "doc1"), cs,
                  addACL, null, null, VersioningState.MAJOR);
            ObjectData res = getStorage().getObjectById(docId);
            for (AccessControlEntry one : res.getACL(false))
            {
               if (one.getPrincipal().equalsIgnoreCase("Makis"))
               {
                  if (one.getPermissions().size() != 1)
                     doFail(testname, "Permissions size is incorrect");
                  if (!one.getPermissions().contains("cmis:read"))
                     doFail(testname, "Permissions does not match");
               }
            }
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());
      }

   }

   /**
    * 2.2.4.1.3 
    * • nameConstraintViolation:   
    * If the repository detects a violation with the given cmis:name property value, 
    * the repository MAY throw this exception or chose a name which does not conflict.  
    * @throws Exception
    */
   @Test
   public void testCreateDocument_NameConstraintViolationException() throws Exception
   {
      String testname = "testCreateDocument_NameConstraintViolationException";
      System.out.print("Running " + testname + "....            ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);
         try
         {
            String docId =
               getConnection().createDocument(testroot.getObjectId(), getPropsMap(CmisConstants.DOCUMENT, "doc1"), cs,
                  null, null, null, VersioningState.MAJOR);
            doFail(testname, "NameConstraintViolationException must be thrown;");
         }
         catch (NameConstraintViolationException ex)
         {
            pass(testname);
         }
         catch (Exception other)
         {
            doFail(testname, other.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }

   }

   /**
    * 2.2.4.1.3
    * The Repository MUST throw this exception if the “contentStreamAllowed” attribute 
    * of the Object-Type definition specified by the cmis:objectTypeId property 
    * value is set to “not allowed” and a contentStream input parameter is provided.
    * @throws Exception
    */
   @Test
   public void testCreateDocument_StreamNotSupportedException() throws Exception
   {
      String testname = "testCreateDocument_StreamNotSupportedException";
      System.out.print("Running " + testname + "....                 ");
      FolderData testroot = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         Map<String, PropertyDefinition<?>> propertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         org.xcmis.spi.model.PropertyDefinition<?> propDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "doc1", true, null, null);
         org.xcmis.spi.model.PropertyDefinition<?> propDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);
         //propertyDefinitions.put(CmisConstants.NAME, propDefName);
         //propertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, propDefObjectTypeId);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(propDefName.getId(), propDefName.getQueryName(),
            propDefName.getLocalName(), propDefName.getDisplayName(), "doc1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId
            .getQueryName(), propDefObjectTypeId.getLocalName(), propDefObjectTypeId.getDisplayName(), "cmis:kino"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document",
               "cmis:kino", "cmis:kino", true, false, true, true, false, false, false, true, null, null,
               ContentStreamAllowed.NOT_ALLOWED, propertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);
         try
         {
            String docId =
               getConnection().createDocument(testroot.getObjectId(), properties, cs, null, null, null,
                  VersioningState.MAJOR);
            doFail(testname, "StreamNotSupportedException must be thrown;");

         }
         catch (StreamNotSupportedException ex)
         {
            pass(testname);
         }
         catch (Exception other)
         {
            doFail(testname, other.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.1.3
    * The cmis:objectTypeId property value is not an Object-Type whose baseType is “Document”.
    * @throws Exception
    */
   @Test
   public void testCreateDocument_ConstraintException1() throws Exception
   {
      String testname = "testCreateDocument_ConstraintException1";
      System.out.print("Running " + testname + "....                        ");
      FolderData testroot = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         Map<String, PropertyDefinition<?>> propertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         org.xcmis.spi.model.PropertyDefinition<?> propDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "doc1", true, null, null);
         org.xcmis.spi.model.PropertyDefinition<?> propDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);
         //propertyDefinitions.put(CmisConstants.NAME, propDefName);
         //propertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, propDefObjectTypeId);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(propDefName.getId(), propDefName.getQueryName(),
            propDefName.getLocalName(), propDefName.getDisplayName(), "doc1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId
            .getQueryName(), propDefObjectTypeId.getLocalName(), propDefObjectTypeId.getDisplayName(), "cmis:kino"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.FOLDER, "cmis:kino", "cmis:kino", "", "cmis:folder", "cmis:kino",
               "cmis:kino", true, false, true, true, false, false, false, true, null, null,
               ContentStreamAllowed.NOT_ALLOWED, propertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);
         try
         {
            String docId =
               getConnection().createDocument(testroot.getObjectId(), properties, cs, null, null, null,
                  VersioningState.MAJOR);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.1.3 
    * The “contentStreamAllowed” attribute of the Object-Type definition specified by 
    * the cmis:objectTypeId property value is set to “required” and no contentStream input parameter is provided.  
    * @throws Exception
    */
   @Test
   public void testCreateDocument_ConstraintException2() throws Exception
   {
      String testname = "testCreateDocument_ConstraintException2";
      System.out.print("Running " + testname + "....                        ");
      FolderData testroot = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         Map<String, PropertyDefinition<?>> propertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         org.xcmis.spi.model.PropertyDefinition<?> propDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "doc1", true, null, null);
         org.xcmis.spi.model.PropertyDefinition<?> propDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);
         //propertyDefinitions.put(CmisConstants.NAME, propDefName);
         //propertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, propDefObjectTypeId);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(propDefName.getId(), propDefName.getQueryName(),
            propDefName.getLocalName(), propDefName.getDisplayName(), "doc1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId
            .getQueryName(), propDefObjectTypeId.getLocalName(), propDefObjectTypeId.getDisplayName(), "cmis:kino"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document",
               "cmis:kino", "cmis:kino", true, false, true, true, false, false, false, true, null, null,
               ContentStreamAllowed.REQUIRED, propertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);
         try
         {
            String docId =
               getConnection().createDocument(testroot.getObjectId(), properties, null, null, null, null,
                  VersioningState.MAJOR);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.1.3
    * The “versionable” attribute of the Object-Type definition specified by the cmis:objectTypeId property value is set to TRUE 
    * and the value for the versioningState input parameter is provided that is “none”.
    * @throws Exception
    */
   @Test
   public void testCreateDocument_ConstraintException3() throws Exception
   {
      String testname = "testCreateDocument_ConstraintException3";
      System.out.print("Running " + testname + "....                        ");
      FolderData testroot = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         Map<String, PropertyDefinition<?>> propertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         org.xcmis.spi.model.PropertyDefinition<?> propDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "doc1", true, null, null);
         org.xcmis.spi.model.PropertyDefinition<?> propDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);
         //propertyDefinitions.put(CmisConstants.NAME, propDefName);
         //propertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, propDefObjectTypeId);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(propDefName.getId(), propDefName.getQueryName(),
            propDefName.getLocalName(), propDefName.getDisplayName(), "doc1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId
            .getQueryName(), propDefObjectTypeId.getLocalName(), propDefObjectTypeId.getDisplayName(), "cmis:kino"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document",
               "cmis:kino", "cmis:kino", true, false, true, true, false, false, false, true, null, null,
               ContentStreamAllowed.ALLOWED, propertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);
         try
         {
            String docId =
               getConnection().createDocument(testroot.getObjectId(), properties, cs, null, null, null,
                  VersioningState.NONE);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.1.3
    * The “versionable” attribute of the Object-Type definition specified by the cmis:objectTypeId 
    * property value is set to FALSE and a value for the versioningState input parameter is provided that is something other than “none”.
    * @throws Exception
    */
   @Test
   public void testCreateDocument_ConstraintException4() throws Exception
   {
      String testname = "testCreateDocument_ConstraintException4";
      System.out.print("Running " + testname + "....                        ");
      FolderData testroot = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         Map<String, PropertyDefinition<?>> propertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         org.xcmis.spi.model.PropertyDefinition<?> propDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "doc1", true, null, null);
         org.xcmis.spi.model.PropertyDefinition<?> propDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(propDefName.getId(), propDefName.getQueryName(),
            propDefName.getLocalName(), propDefName.getDisplayName(), "doc1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId
            .getQueryName(), propDefObjectTypeId.getLocalName(), propDefObjectTypeId.getDisplayName(), "cmis:kino"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document",
               "cmis:kino", "cmis:kino", true, false, true, true, false, false, false, false, null, null,
               ContentStreamAllowed.ALLOWED, propertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);
         try
         {
            String docId =
               getConnection().createDocument(testroot.getObjectId(), properties, cs, null, null, null,
                  VersioningState.MAJOR);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.1.3
    * The “controllablePolicy” attribute of the Object-Type definition specified by the 
    * cmis:objectTypeId property value is set to FALSE and at least one policy is provided.
    * @throws Exception
    */
   @Test
   public void testCreateDocument_ConstraintException5() throws Exception
   {
      String testname = "testCreateDocument_ConstraintException5";
      System.out.print("Running " + testname + "....                        ");
      if (!IS_POLICIES_SUPPORTED)
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      String typeID = null;
      PolicyData policy = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         Map<String, PropertyDefinition<?>> propertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         org.xcmis.spi.model.PropertyDefinition<?> propDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "doc1", true, null, null);
         org.xcmis.spi.model.PropertyDefinition<?> propDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);
         //propertyDefinitions.put(CmisConstants.NAME, propDefName);
         //propertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, propDefObjectTypeId);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(propDefName.getId(), propDefName.getQueryName(),
            propDefName.getLocalName(), propDefName.getDisplayName(), "doc1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId
            .getQueryName(), propDefObjectTypeId.getLocalName(), propDefObjectTypeId.getDisplayName(), "cmis:kino"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document",
               "cmis:kino", "cmis:kino", true, false, true, true, false, false, false, false, null, null,
               ContentStreamAllowed.ALLOWED, propertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         policy = createPolicy(testroot, "policy1");

         ArrayList<String> policies = new ArrayList<String>();
         policies.add(policy.getObjectId());
         try
         {
            String docId =
               getConnection().createDocument(testroot.getObjectId(), properties, cs, null, null, policies,
                  VersioningState.MAJOR);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (policy != null)
            getStorage().deleteObject(policy, true);
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.1.3
    * The “controllableACL” attribute of the Object-Type definition specified by the cmis:objectTypeId 
    * property value is set to FALSE and at least one ACE is provided.
    * @throws Exception
    */
   @Test
   public void testCreateDocument_ConstraintException6() throws Exception
   {
      String testname = "testCreateDocument_ConstraintException6";
      System.out.print("Running " + testname + "....                        ");
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         Map<String, PropertyDefinition<?>> propertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         org.xcmis.spi.model.PropertyDefinition<?> propDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "doc1", true, null, null);
         org.xcmis.spi.model.PropertyDefinition<?> propDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);
         //propertyDefinitions.put(CmisConstants.NAME, propDefName);
         //propertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, propDefObjectTypeId);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(propDefName.getId(), propDefName.getQueryName(),
            propDefName.getLocalName(), propDefName.getDisplayName(), "doc1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId
            .getQueryName(), propDefObjectTypeId.getLocalName(), propDefObjectTypeId.getDisplayName(), "cmis:kino"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document",
               "cmis:kino", "cmis:kino", true, false, true, true, false, false, false, false, null, null,
               ContentStreamAllowed.ALLOWED, propertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         AccessControlEntry acl = new AccessControlEntry();
         acl.setPrincipal("Makis");
         acl.getPermissions().add("cmis:read");
         ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
         addACL.add(acl);

         try
         {
            String docId =
               getConnection().createDocument(testroot.getObjectId(), properties, cs, addACL, null, null,
                  VersioningState.MAJOR);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.1.3
    * At least one of the permissions is used in an ACE provided which is not supported by the repository.
    * @throws Exception
    */
   @Test
   public void testCreateDocument_ConstraintException7() throws Exception
   {
      String testname = "testCreateDocument_ConstraintException7";
      System.out.print("Running " + testname + "....                        ");
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         Map<String, PropertyDefinition<?>> propertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         org.xcmis.spi.model.PropertyDefinition<?> propDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "doc1", true, null, null);
         org.xcmis.spi.model.PropertyDefinition<?> propDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(propDefName.getId(), propDefName.getQueryName(),
            propDefName.getLocalName(), propDefName.getDisplayName(), "doc1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId
            .getQueryName(), propDefObjectTypeId.getLocalName(), propDefObjectTypeId.getDisplayName(), "cmis:kino"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document",
               "cmis:kino", "cmis:kino", true, false, true, true, false, false, true, false, null, null,
               ContentStreamAllowed.ALLOWED, propertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         AccessControlEntry acl = new AccessControlEntry();
         acl.setPrincipal("Makis");
         acl.getPermissions().add("cmis:unknown");
         ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
         addACL.add(acl);

         try
         {
            String docId =
               getConnection().createDocument(testroot.getObjectId(), properties, cs, addACL, null, null,
                  VersioningState.MAJOR);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.2
    * Creates a document object as a copy of the given source document in the (optionally) specified location.
    * @throws Exception
    */
   @Test
   public void testCreateDocumentFromSource_Simple() throws Exception
   {
      String testname = "testCreateDocumentFromSource_Simple";
      System.out.print("Running " + testname + "....                            ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         byte[] before = new byte[15];
         before = "1234567890aBcDE".getBytes();
         ContentStream cs = new BaseContentStream(before, null, new MimeType("text", "plain"));
         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);
         try
         {
            String docId =
               getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(),
                  getPropsMap(CmisConstants.DOCUMENT, "doc2"), null, null, null, VersioningState.MAJOR);
            ContentStream c = getStorage().getObjectById(docId).getContentStream(null);
            if (!cs.getMediaType().equals(c.getMediaType()))
               doFail(testname, "Media types does not match");

            byte[] after = new byte[15];
            c.getStream().read(after);
            assertArrayEquals(before, after);
            if (!testroot.getName().equals(getStorage().getObjectById(docId).getParent().getName()))
               doFail(testname, "Names does not match;");
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.2.1
    * The property values that MUST be applied to the Object.  
    * This list of properties SHOULD only contain properties whose values differ from the source document.
    * @throws Exception
    */
   @Test
   public void testCreateDocumentFromSource_Properties() throws Exception
   {
      String testname = "testCreateDocumentFromSource_Properties";
      System.out.print("Running " + testname + "....                        ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         byte[] before = new byte[15];
         before = "1234567890aBcDE".getBytes();
         ContentStream cs = new BaseContentStream(before, null, new MimeType("text", "plain"));
         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);
         try
         {
            String docId =
               getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(),
                  getPropsMap(CmisConstants.DOCUMENT, "doc2"), null, null, null, VersioningState.MAJOR);
            if (!getStorage().getObjectById(docId).getProperty(CmisConstants.NAME).getValues().get(0).equals("doc2"))
               doFail(testname, "Names doen not match;");
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.2.1
    * A list of policy IDs that MUST be applied to the newly-created Document object. 
    * @throws Exception
    */
   @Test
   public void testCreateDocumentFromSource_ApplyPolicy() throws Exception
   {
      String testname = "testCreateDocumentFromSource_ApplyPolicy";
      System.out.print("Running " + testname + "....                       ");
      if (!IS_POLICIES_SUPPORTED)
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      PolicyData policy = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         byte[] before = new byte[15];
         before = "1234567890aBcDE".getBytes();
         ContentStream cs = new BaseContentStream(before, null, new MimeType("text", "plain"));
         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);

         policy = createPolicy(testroot, "policy1");

         ArrayList<String> policies = new ArrayList<String>();
         policies.add(policy.getObjectId());
         try
         {
            String docId =
               getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(),
                  getPropsMap(CmisConstants.DOCUMENT, "doc2"), null, null, policies, VersioningState.MAJOR);
            ObjectData res = getStorage().getObjectById(docId);
            if (res.getPolicies().size() != 1)
               doFail(testname, "Properties size iz incorrect");
            Iterator<PolicyData> it = res.getPolicies().iterator();
            while (it.hasNext())
            {
               PolicyData one = it.next();
               if (!one.getName().equals("policy1"))
                  doFail(testname, "POlicy names does not match");
               if (!one.getPolicyText().equals("testPolicyText"))
                  doFail(testname, "Policy text does not match");
               res.removePolicy(one);
            }
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (policy != null)
            getStorage().deleteObject(policy, true);
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.2.1
    *  A list of ACEs that MUST be added to the newly-created Document object, 
    *  either using the ACL from folderId if specified, or being applied if no folderId is specified.  
    * @throws Exception
    */
   @Test
   public void testCreateDocumentFromSource_addACL() throws Exception
   {
      String testname = "testCreateDocumentFromSource_addACL";
      System.out.print("Running " + testname + "....                            ");
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         byte[] before = new byte[15];
         before = "1234567890aBcDE".getBytes();
         ContentStream cs = new BaseContentStream(before, null, new MimeType("text", "plain"));
         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);

         AccessControlEntry acl = new AccessControlEntry();
         acl.setPrincipal("Makis");
         acl.getPermissions().add("cmis:read");
         ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
         addACL.add(acl);
         try
         {
            String docId =
               getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(),
                  getPropsMap(CmisConstants.DOCUMENT, "doc2"), addACL, null, null, VersioningState.MAJOR);
            ObjectData res = getStorage().getObjectById(docId);
            for (AccessControlEntry one : res.getACL(false))
            {
               if (one.getPrincipal().equalsIgnoreCase("Makis"))
               {
                  if (one.getPermissions().size() != 1)
                     doFail(testname, "Permissions size is incorrect");
                  if (!one.getPermissions().contains("cmis:read"))
                     doFail(testname, "Permissions does not match");
               }
            }
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }

   }

   /**
    * 2.2.4.2.3
    *  If the repository detects a violation with the given cmis:name property value, 
    *  the repository MAY throw this exception or chose a name which does not conflict.
    * @throws Exception
    */
   @Test
   public void testCreateDocumentFromSource_NameConstraintViolationException() throws Exception
   {
      String testname = "testCreateDocumentFromSource_NameConstraintViolationException";
      System.out.print("Running " + testname + "....  ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         byte[] before = new byte[15];
         before = "1234567890aBcDE".getBytes();
         ContentStream cs = new BaseContentStream(before, null, new MimeType("text", "plain"));
         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);
         try
         {
            String docId =
               getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(),
                  getPropsMap(CmisConstants.DOCUMENT, "doc1"), null, null, null, VersioningState.MAJOR);
            ObjectData res = getStorage().getObjectById(docId);
            if (res.getName().equals("doc1"))
               doFail(testname, "Names must not match;");
         }
         catch (NameConstraintViolationException ex)
         {
            pass(testname);
         }
         catch (Exception other)
         {
            doFail(testname, other.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.2.3
    * constraint: The Repository MUST throw this exception if  the sourceId is not an Object whose baseType is “Document”.
    * @throws Exception
    */
   @Test
   public void testCreateDocumentFromSource_ConstraintException1() throws Exception
   {
      String testname = "testCreateDocumentFromSource_ConstraintException1";
      System.out.print("Running " + testname + "....              ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         FolderData test = createFolder(testroot, "123");

         try
         {
            String docId =
               getConnection().createDocumentFromSource(test.getObjectId(), testroot.getObjectId(),
                  getPropsMap(CmisConstants.DOCUMENT, "1"), null, null, null, VersioningState.MAJOR);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }

   }

   /**
    * 2.2.4.2.3
    * The source document’s cmis:objectTypeId property value is NOT in the list of AllowedChildObjectTypeIds 
    * of the parent-folder specified by folderId.
    * @throws Exception
    */
   @Test
   public void testCreateDocumentFromSource_ConstraintException2() throws Exception
   {
      String testname = "testCreateDocumentFromSource_ConstraintException2";
      System.out.print("Running " + testname + "....              ");
      FolderData testroot = null;
      String typeID = null;
      FolderData myfolder = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         //Creating type from cmis:folder with overriden  ALLOWED_CHILD_OBJECT_TYPE_IDS;

         Map<String, PropertyDefinition<?>> folderPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "myfolder", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "fold_type_id1", null, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefAllowedChild =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, PropertyType.ID,
               CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, null,
               CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, false, false, false, false, false, Updatability.READONLY,
               "fold_type_chld_ids", null, null, null);
         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();

         properties.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "myfolder"));

         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(),
            "cmis:myfolder"));

         properties.put(CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, new IdProperty(fPropDefAllowedChild.getId(),
            fPropDefAllowedChild.getQueryName(), fPropDefAllowedChild.getLocalName(), fPropDefAllowedChild
               .getDisplayName(), "cmis:folder"));

         TypeDefinition newType =
            new TypeDefinition("cmis:myfolder", BaseType.FOLDER, "cmis:myfolder", "cmis:myfolder", "", "cmis:folder",
               "cmis:myfolder", "cmis:myfolder", true, false, true, true, false, false, false, false, null, null,
               ContentStreamAllowed.NOT_ALLOWED, folderPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         myfolder = getStorage().createFolder(testroot, newType, properties, null, null);

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc"),
               cs, null, null, VersioningState.MAJOR);

         try
         {
            String docId =
               getConnection().createDocumentFromSource(doc1.getObjectId(), myfolder.getObjectId(),
                  getPropsMap(CmisConstants.DOCUMENT, "1"), null, null, null, VersioningState.MAJOR);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (myfolder != null)
            getStorage().deleteObject(myfolder, true);
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.2.3
    * The “versionable” attribute of the Object-Type definition specified by the cmis:objectTypeId property value is set to FALSE 
    * and a value for the versioningState input parameter is provided that is something other than “none”.
    * @throws Exception
    */
   @Test
   public void testCreateDocumentFromSource_ConstraintException3() throws Exception
   {
      String testname = "testCreateDocumentFromSource_ConstraintException3";
      System.out.print("Running " + testname + "....              ");
      FolderData testroot = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         //Creating type from cmis:folder with overriden  ALLOWED_CHILD_OBJECT_TYPE_IDS;

         Map<String, PropertyDefinition<?>> propertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         org.xcmis.spi.model.PropertyDefinition<?> propDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "doc1", true, null, null);
         org.xcmis.spi.model.PropertyDefinition<?> propDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);
         //propertyDefinitions.put(CmisConstants.NAME, propDefName);
         //propertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, propDefObjectTypeId);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(propDefName.getId(), propDefName.getQueryName(),
            propDefName.getLocalName(), propDefName.getDisplayName(), "doc1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId
            .getQueryName(), propDefObjectTypeId.getLocalName(), propDefObjectTypeId.getDisplayName(), "cmis:kino"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document",
               "cmis:kino", "cmis:kino", true, false, true, true, false, false, false, false, null, null,
               ContentStreamAllowed.ALLOWED, propertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc"),
               cs, null, null, VersioningState.MAJOR);
         try
         {
            String docId =
               getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(), properties, null,
                  null, null, VersioningState.MAJOR);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.2.3
    * The “versionable” attribute of the Object-Type definition specified by the cmis:objectTypeId property value is set to TRUE and 
    * the value for the versioningState input parameter is provided that is “none”.
    * @throws Exception
    */
   @Test
   public void testCreateDocumentFromSource_ConstraintException4() throws Exception
   {
      String testname = "testCreateDocumentFromSource_ConstraintException4";
      System.out.print("Running " + testname + "....              ");
      FolderData testroot = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         Map<String, PropertyDefinition<?>> propertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         org.xcmis.spi.model.PropertyDefinition<?> propDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "doc1", true, null, null);
         org.xcmis.spi.model.PropertyDefinition<?> propDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);
         //propertyDefinitions.put(CmisConstants.NAME, propDefName);
         //propertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, propDefObjectTypeId);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(propDefName.getId(), propDefName.getQueryName(),
            propDefName.getLocalName(), propDefName.getDisplayName(), "doc1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId
            .getQueryName(), propDefObjectTypeId.getLocalName(), propDefObjectTypeId.getDisplayName(), "cmis:kino"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document",
               "cmis:kino", "cmis:kino", true, false, true, true, false, false, false, true, null, null,
               ContentStreamAllowed.ALLOWED, propertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         DocumentData doc1 =
            getStorage().createDocument(testroot, newType, properties, cs, null, null, VersioningState.MAJOR);

         properties.put(CmisConstants.NAME, new StringProperty(propDefName.getId(), propDefName.getQueryName(),
            propDefName.getLocalName(), propDefName.getDisplayName(), "doc2"));

         try
         {
            String docId =
               getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(), properties, null,
                  null, null, VersioningState.NONE);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);

      }
   }

   /**
    * 2.2.4.2.3
    * The “controllablePolicy” attribute of the Object-Type definition 
    * specified by the cmis:objectTypeId property value is set to FALSE and at least one policy is provided.
    * 
    * @throws Exception
    */
   @Test
   public void testCreateDocumentFromSource_ConstraintException5() throws Exception
   {
      String testname = "testCreateDocumentFromSource_ConstraintException5";
      System.out.print("Running " + testname + "...               ");
      if (!IS_POLICIES_SUPPORTED)
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      String typeID = null;
      PolicyData policy = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         Map<String, PropertyDefinition<?>> propertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         org.xcmis.spi.model.PropertyDefinition<?> propDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "doc1", true, null, null);
         org.xcmis.spi.model.PropertyDefinition<?> propDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);
         //propertyDefinitions.put(CmisConstants.NAME, propDefName);
         //propertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, propDefObjectTypeId);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(propDefName.getId(), propDefName.getQueryName(),
            propDefName.getLocalName(), propDefName.getDisplayName(), "doc1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId
            .getQueryName(), propDefObjectTypeId.getLocalName(), propDefObjectTypeId.getDisplayName(), "cmis:kino"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document",
               "cmis:kino", "cmis:kino", true, false, true, true, false, false, false, true, null, null,
               ContentStreamAllowed.ALLOWED, propertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         policy = createPolicy(testroot, "policy1");

         ArrayList<String> policies = new ArrayList<String>();
         policies.add(policy.getObjectId());

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc"),
               cs, null, null, VersioningState.MAJOR);
         try
         {
            String docId =
               getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(), properties, null,
                  null, policies, VersioningState.NONE);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);
         if (policy != null)
            getStorage().deleteObject(policy, true);
      }
   }

   /**
    * 2.2.4.2.3
    * The “controllableACL” attribute of the Object-Type definition 
    * specified by the cmis:objectTypeId property value is set to FALSE and at least one ACE is provided.
    * 
    * @throws Exception
    */
   @Test
   public void testCreateDocumentFromSource_ConstraintException6() throws Exception
   {
      String testname = "testCreateDocumentFromSource_ConstraintException6";
      System.out.print("Running " + testname + "...               ");
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         Map<String, PropertyDefinition<?>> propertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         org.xcmis.spi.model.PropertyDefinition<?> propDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "doc1", true, null, null);
         org.xcmis.spi.model.PropertyDefinition<?> propDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);
         //propertyDefinitions.put(CmisConstants.NAME, propDefName);
         //propertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, propDefObjectTypeId);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(propDefName.getId(), propDefName.getQueryName(),
            propDefName.getLocalName(), propDefName.getDisplayName(), "doc1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId
            .getQueryName(), propDefObjectTypeId.getLocalName(), propDefObjectTypeId.getDisplayName(), "cmis:kino"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document",
               "cmis:kino", "cmis:kino", true, false, true, true, false, false, false, false, null, null,
               ContentStreamAllowed.ALLOWED, propertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         AccessControlEntry acl = new AccessControlEntry();
         acl.setPrincipal("Makis");
         acl.getPermissions().add("cmis:unknown");
         ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
         addACL.add(acl);

         DocumentData doc1 =
            getStorage().createDocument(testroot, newType, getPropsMap(CmisConstants.DOCUMENT, "doc"), cs, null, null,
               VersioningState.MAJOR);
         try
         {
            String docId =
               getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(), properties, addACL,
                  null, null, VersioningState.NONE);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.2.3
    * At least one of the permissions is used in an ACE provided which is not supported by the repository.
    * @throws Exception
    */
   @Test
   public void testCreateDocumentFromSource_ConstraintException7() throws Exception
   {
      String testname = "testCreateDocumentFromSource_ConstraintException7";
      System.out.print("Running " + testname + "....              ");
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         Map<String, PropertyDefinition<?>> propertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         org.xcmis.spi.model.PropertyDefinition<?> propDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "doc1", true, null, null);
         org.xcmis.spi.model.PropertyDefinition<?> propDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);
         //propertyDefinitions.put(CmisConstants.NAME, propDefName);
         //propertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, propDefObjectTypeId);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(propDefName.getId(), propDefName.getQueryName(),
            propDefName.getLocalName(), propDefName.getDisplayName(), "doc1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId
            .getQueryName(), propDefObjectTypeId.getLocalName(), propDefObjectTypeId.getDisplayName(), "cmis:kino"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document",
               "cmis:kino", "cmis:kino", true, false, true, true, false, false, true, false, null, null,
               ContentStreamAllowed.NOT_ALLOWED, propertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         AccessControlEntry acl = new AccessControlEntry();
         acl.setPrincipal("Makis");
         acl.getPermissions().add("cmis:unknown");
         ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
         addACL.add(acl);

         DocumentData doc1 =
            getStorage().createDocument(testroot, newType, getPropsMap(CmisConstants.DOCUMENT, "doc"), cs, null, null,
               VersioningState.MAJOR);
         try
         {
            String docId =
               getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(), properties, addACL,
                  null, null, VersioningState.NONE);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.3
    * Creates a folder object of the specified type in the specified location.
    * @throws Exception
    */
   @Test
   public void testCreateFolder_Simple() throws Exception
   {
      String testname = "testCreateFolder_Simple";
      System.out.print("Running " + testname + "....                                        ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         try
         {
            String docId =
               getConnection().createFolder(testroot.getObjectId(), getPropsMap(CmisConstants.FOLDER, "f1"), null,
                  null, null);
            ObjectData obj = getStorage().getObjectById(docId);
            if (!obj.getTypeId().equals(CmisConstants.FOLDER))
               doFail(testname, "Object types does not match;");
            if (!((FolderData)obj).getPath().equals("/testroot/f1"))
               doFail(testname, "Path is not correct;");
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.3.1
    * A list of policy IDs that MUST be applied to the newly-created Folder object.
    * @throws Exception
    */
   @Test
   public void testCreateFolder_ApplyPolicy() throws Exception
   {
      String testname = "testCreateFolder_ApplyPolicy";
      System.out.print("Running " + testname + "....                                   ");
      if (!IS_POLICIES_SUPPORTED)
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      PolicyData policy = null;
      String docId = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         policy = createPolicy(testroot, "policy1");

         ArrayList<String> policies = new ArrayList<String>();
         policies.add(policy.getObjectId());

         try
         {
            docId =
               getConnection().createFolder(testroot.getObjectId(), getPropsMap(CmisConstants.FOLDER, "f1"), null,
                  null, policies);
            ObjectData res = getStorage().getObjectById(docId);
            if (res.getPolicies().size() != 1)
               doFail(testname, "Properties size iz incorrect");
            Iterator<PolicyData> it = res.getPolicies().iterator();
            while (it.hasNext())
            {
               PolicyData one = it.next();
               if (!one.getName().equals("policy1"))
                  doFail(testname, "POlicy names does not match");
               if (!one.getPolicyText().equals("testPolicyText"))
                  doFail(testname, "Policy text does not match");
            }
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (docId != null)
            getStorage().deleteObject(getStorage().getObjectById(docId), true);
         if (policy != null)
            getStorage().deleteObject(policy, true);
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.3.1
    * A list of ACEs that MUST be added to the newly-created Folder object.
    * @throws Exception
    */
   @Test
   public void testCreateFolder_AddACL() throws Exception
   {
      String testname = "testCreateFolder_AddACL";
      System.out.print("Running " + testname + "....                                      ");
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      String docId = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         AccessControlEntry acl = new AccessControlEntry();
         acl.setPrincipal("Makis");
         acl.getPermissions().add("cmis:read");
         ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
         addACL.add(acl);

         try
         {
            docId =
               getConnection().createFolder(testroot.getObjectId(), getPropsMap(CmisConstants.FOLDER, "f1"), addACL,
                  null, null);
            ObjectData res = getStorage().getObjectById(docId);
            for (AccessControlEntry one : res.getACL(false))
            {
               if (one.getPrincipal().equalsIgnoreCase("Makis"))
                  if (one.getPermissions().size() != 1)
                     doFail(testname, "Permissions size is incorrect");
               if (!one.getPermissions().contains("cmis:read"))
                  doFail(testname, "Permissions does not match");
            }
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (docId != null)
            getStorage().deleteObject(getStorage().getObjectById(docId), true);
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.3
    * Creates a folder object of the specified type in the specified location.
    * @throws Exception
    */
   @Test
   public void testCreateFolder_NameConstraintViolationException() throws Exception
   {
      String testname = "testCreateFolder_NameConstraintViolationException";
      System.out.print("Running " + testname + "....                                        ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         getStorage().createFolder(testroot, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "f1"), null, null);
         try
         {
            String docId =
               getConnection().createFolder(testroot.getObjectId(), getPropsMap(CmisConstants.FOLDER, "f1"), null,
                  null, null);
            ObjectData res = getStorage().getObjectById(docId);
            if (!res.getName().equals("f1"))
               doFail(testname, "Names does not match;");
         }
         catch (NameConstraintViolationException ex)
         {
            pass(testname);
         }
         catch (Exception other)
         {
            doFail(testname, other.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.3.3
    * The Repository MUST throw this exception if the cmis:objectTypeId property 
    * value is not an Object-Type whose baseType is “Folder”.
    * @throws Exception
    */
   @Test
   public void testCreateFolder_ConstraintException1() throws Exception
   {
      String testname = "testCreateFolder_ConstraintException1";
      System.out.print("Running " + testname + "....                          ");
      FolderData testroot = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "f1", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         //fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
         //fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "f1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.POLICY, "cmis:kino", "cmis:kino", "", "cmis:policy", "cmis:kino",
               "cmis:kino", true, false, true, true, false, false, false, true, null, null,
               ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         try
         {
            String docId = getConnection().createFolder(testroot.getObjectId(), properties, null, null, null);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.3.3
    * The cmis:objectTypeId property value is NOT in the list of 
    * AllowedChildObjectTypeIds of the parent-folder specified by folderId. 
    * @throws Exception
    */
   @Test
   public void testCreateFolder_ConstraintException2() throws Exception
   {
      String testname = "testCreateFolder_ConstraintException2";
      System.out.print("Running " + testname + "....                          ");
      FolderData testroot = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "f1", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefAllowedChilds =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, PropertyType.ID,
               CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, null,
               CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, false, false, false, false, false, Updatability.READONLY,
               "Allowed childs", null, null, null);

         //fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
         //fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);
         // fPropertyDefinitions.put(CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, fPropDefAllowedChilds);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "f1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino"));
         properties.put(CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, new IdProperty(fPropDefAllowedChilds.getId(),
            fPropDefAllowedChilds.getQueryName(), fPropDefAllowedChilds.getLocalName(), fPropDefAllowedChilds
               .getDisplayName(), "cmis:document"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.FOLDER, "cmis:kino", "cmis:kino", "", "cmis:folder", "cmis:kino",
               "cmis:kino", true, false, true, true, false, false, false, true, null, null,
               ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         FolderData f1 = getStorage().createFolder(testroot, newType, properties, null, null);

         try
         {
            String docId =
               getConnection()
                  .createFolder(f1.getObjectId(), getPropsMap(CmisConstants.FOLDER, "f2"), null, null, null);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.3.3
    * The “controllablePolicy” attribute of the Object-Type definition specified by the 
    * cmis:objectTypeId property value is set to FALSE and at least one policy is provided.
    * @throws Exception
    */
   @Test
   public void testCreateFolder_ConstraintException3() throws Exception
   {
      String testname = "testCreateFolder_ConstraintException3";
      System.out.print("Running " + testname + "....                          ");
      if (!IS_POLICIES_SUPPORTED)
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      PolicyData policy = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "f1", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         //fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
         //fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "f1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.FOLDER, "cmis:kino", "cmis:kino", "", "cmis:folder", "cmis:kino",
               "cmis:kino", true, false, true, true, false, false, false, true, null, null,
               ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         policy = createPolicy(testroot, "policy1");

         ArrayList<String> policies = new ArrayList<String>();
         policies.add(policy.getObjectId());

         try
         {
            String docId = getConnection().createFolder(testroot.getObjectId(), properties, null, null, policies);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (policy != null)
            getStorage().deleteObject(policy, true);
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.3.3
    * The “controllableACL” attribute of the Object-Type definition specified 
    * by the cmis:objectTypeId property value is set to FALSE and at least one ACE is provided.
    * @throws Exception
    */
   @Test
   public void testCreateFolder_ConstraintException4() throws Exception
   {
      String testname = "testCreateFolder_ConstraintException4";
      System.out.print("Running " + testname + "....                          ");
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "f1", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         //fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
         //fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "f1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.FOLDER, "cmis:kino", "cmis:kino", "", "cmis:folder", "cmis:kino",
               "cmis:kino", true, false, true, true, false, false, false, true, null, null,
               ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         AccessControlEntry acl = new AccessControlEntry();
         acl.setPrincipal("Makis");
         acl.getPermissions().add("cmis:read");
         ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
         addACL.add(acl);

         try
         {
            String docId = getConnection().createFolder(testroot.getObjectId(), properties, addACL, null, null);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.3.3
    * At least one of the permissions is used in an ACE provided which is not supported by the repository.
    * @throws Exception
    */
   @Test
   public void testCreateFolder_ConstraintException5() throws Exception
   {
      String testname = "testCreateFolder_ConstraintException5";
      System.out.print("Running " + testname + "....                          ");
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "f1", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         //fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
         //fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "f1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.FOLDER, "cmis:kino", "cmis:kino", "", "cmis:folder", "cmis:kino",
               "cmis:kino", true, false, true, true, false, false, true, true, null, null,
               ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         AccessControlEntry acl = new AccessControlEntry();
         acl.setPrincipal("Makis");
         acl.getPermissions().add("cmis:unknown");
         ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
         addACL.add(acl);

         try
         {
            String docId = getConnection().createFolder(testroot.getObjectId(), properties, addACL, null, null);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.4
    * Creates a relationship object of the specified type.
    * @throws Exception
    */
   @Test
   public void testCreateRelationship_Simple() throws Exception
   {
      String testname = "testCreateRelationship_Simple";
      System.out.print("Running " + testname + "....                                  ");
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      ObjectData obj = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);
         DocumentData doc2 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc2"),
               cs, null, null, VersioningState.MAJOR);

         Map<String, Property<?>> props = getPropsMap("cmis:relationship", "rel1");

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefSource =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.SOURCE_ID, PropertyType.ID,
               CmisConstants.SOURCE_ID, CmisConstants.SOURCE_ID, null, CmisConstants.SOURCE_ID, true, false, false,
               false, false, Updatability.READONLY, "SourceId", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefTarget =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.TARGET_ID, PropertyType.ID,
               CmisConstants.TARGET_ID, CmisConstants.TARGET_ID, null, CmisConstants.TARGET_ID, false, false, false,
               false, false, Updatability.READONLY, "TargetId", null, null, null);

         props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
            fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc1.getObjectId()));
         props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
            fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));

         try
         {
            String docId = getConnection().createRelationship(props, null, null, null);
            obj = getStorage().getObjectById(docId);
            if (!obj.getTypeId().equals("cmis:relationship"))
               doFail(testname, "Cmis object types does not match;");
            if (!doc1.getObjectId().equals(((RelationshipData)obj).getSourceId()))
               doFail(testname, "Cmis objects ID does not match;");
            if (!doc2.getObjectId().equals(((RelationshipData)obj).getTargetId()))
               doFail(testname, "Cmis object ID  does not match;");
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.4.1
    * A list of policy IDs that MUST be applied to the newly-created Replationship object.
    * @throws Exception
    */
   @Test
   public void testCreateRelationship_ApplyPolicy() throws Exception
   {
      String testname = "testCreateRelationship_ApplyPolicy";
      System.out.print("Running " + testname + "....                             ");
      if (!IS_POLICIES_SUPPORTED || !IS_RELATIONSHIPS_SUPPORTED)
      {
         skip("ObjectTest." + testname);
         return;
      }

      FolderData testroot = null;
      ObjectData obj = null;
      PolicyData policy = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);
         DocumentData doc2 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc2"),
               cs, null, null, VersioningState.MAJOR);

         Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         Map<String, Property<?>> props = new HashMap<String, Property<?>>();

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefSource =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.SOURCE_ID, PropertyType.ID,
               CmisConstants.SOURCE_ID, CmisConstants.SOURCE_ID, null, CmisConstants.SOURCE_ID, true, false, false,
               false, false, Updatability.READONLY, "SourceId", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefTarget =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.TARGET_ID, PropertyType.ID,
               CmisConstants.TARGET_ID, CmisConstants.TARGET_ID, null, CmisConstants.TARGET_ID, false, false, false,
               false, false, Updatability.READONLY, "TargetId", null, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "rel1", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         props.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "rel1"));

         props.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino2"));

         props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
            fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc1.getObjectId()));
         props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
            fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));

         //fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
         //fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);

         TypeDefinition newType =
            new TypeDefinition("cmis:kino2", BaseType.RELATIONSHIP, "cmis:kino2", "cmis:kino2", "",
               "cmis:relationship", "cmis:kino2", "cmis:kino2", true, false, true, true, false, true, false, false,
               null, null, ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         policy = createPolicy(testroot, "policy1");
         ArrayList<String> policies = new ArrayList<String>();
         policies.add(policy.getObjectId());
         try
         {
            String docId = getConnection().createRelationship(props, null, null, policies);
            obj = getStorage().getObjectById(docId);
            if (obj.getPolicies().size() != 1)
               doFail(testname, "Object policies size is incorrect;");
            Iterator<PolicyData> it = obj.getPolicies().iterator();
            while (it.hasNext())
            {
               PolicyData one = it.next();
               if (!one.getName().equals("policy1"))
                  doFail(testname, "POlicy names does not match");
               if (!one.getPolicyText().equals("testPolicyText"))
                  doFail(testname, "Policy text does not match");
               obj.removePolicy(one);
            }
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         if (policy != null)
            getStorage().deleteObject(policy, true);
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.4.1
    * A list of ACEs that MUST be added to the newly-created Relationship object, either using the 
    * ACL from folderId if specified, or being applied if no folderId is specified. 
    * @throws Exception
    */
   @Test
   public void testCreateRelationship_AddACL() throws Exception
   {
      String testname = "testCreateRelationship_AddACL";
      System.out.print("Running " + testname + "....                                  ");
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         skip("ObjectTest." + testname);
         return;
      }
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      ObjectData obj = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);
         DocumentData doc2 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc2"),
               cs, null, null, VersioningState.MAJOR);

         Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         Map<String, Property<?>> props = new HashMap<String, Property<?>>();

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefSource =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.SOURCE_ID, PropertyType.ID,
               CmisConstants.SOURCE_ID, CmisConstants.SOURCE_ID, null, CmisConstants.SOURCE_ID, true, false, false,
               false, false, Updatability.READONLY, "SourceId", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefTarget =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.TARGET_ID, PropertyType.ID,
               CmisConstants.TARGET_ID, CmisConstants.TARGET_ID, null, CmisConstants.TARGET_ID, false, false, false,
               false, false, Updatability.READONLY, "TargetId", null, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "rel1", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         props.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "rel1"));

         props.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino"));

         props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefSource.getId(), fPropDefSource.getQueryName(),
            fPropDefSource.getLocalName(), fPropDefSource.getDisplayName(), doc1.getObjectId()));
         props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
            fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));

         //fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
         //fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.RELATIONSHIP, "cmis:kino", "cmis:kino", "", "cmis:relationship",
               "cmis:kino", "cmis:kino", true, false, true, true, false, true, true, false, null, null,
               ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         AccessControlEntry acl = new AccessControlEntry();
         acl.setPrincipal("Makis");
         acl.getPermissions().add("cmis:read");
         ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
         addACL.add(acl);

         try
         {
            String docId = getConnection().createRelationship(props, addACL, null, null);
            obj = getStorage().getObjectById(docId);
            for (AccessControlEntry one : obj.getACL(false))
            {
               if (one.getPrincipal().equalsIgnoreCase("Makis"))
               {
                  if (one.getPermissions().size() != 1)
                     doFail(testname, "Permissions size is incorrect");
                  if (!one.getPermissions().contains("cmis:read"))
                     doFail(testname, "Permissions does not match");
               }
            }
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.4.3
    * If the repository detects a violation with the given cmis:name property value, the repository MAY 
    * throw this exception or chose a name which does not conflict.
    * @throws Exception
    */
   @Test
   public void testCreateRelationship_NameConstraintViolationException() throws Exception
   {
      String testname = "testCreateRelationship_NameConstraintViolationException";
      System.out.print("Running " + testname + "....        ");
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      ObjectData obj = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);
         DocumentData doc2 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc2"),
               cs, null, null, VersioningState.MAJOR);

         //Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         Map<String, Property<?>> props = new HashMap<String, Property<?>>();

         Map<String, Property<?>> props2 = new HashMap<String, Property<?>>();
         org.xcmis.spi.model.PropertyDefinition<?> fPropDefName1 =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "rel", true, null, null);
         props2.put(CmisConstants.NAME, new StringProperty(fPropDefName1.getId(), fPropDefName1.getQueryName(),
            fPropDefName1.getLocalName(), fPropDefName1.getDisplayName(), "rel1"));

         getStorage().createRelationship(doc2, doc1, relationshipTypeDefinition, props2, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefSource =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.SOURCE_ID, PropertyType.ID,
               CmisConstants.SOURCE_ID, CmisConstants.SOURCE_ID, null, CmisConstants.SOURCE_ID, true, false, false,
               false, false, Updatability.READONLY, "SourceId", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefTarget =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.TARGET_ID, PropertyType.ID,
               CmisConstants.TARGET_ID, CmisConstants.TARGET_ID, null, CmisConstants.TARGET_ID, false, false, false,
               false, false, Updatability.READONLY, "TargetId", null, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "rel", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         props.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "rel1"));

         props.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(),
            "cmis:relationship"));

         props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
            fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc1.getObjectId()));
         props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
            fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));

         try
         {
            String docId = getConnection().createRelationship(props, null, null, null);
            obj = getStorage().getObjectById(docId);
            if (obj.getName().equals("rel1"))
               doFail(testname, "Names must not match;");
            pass(testname);
         }
         catch (NameConstraintViolationException ex)
         {
            pass(testname);
         }
         catch (Exception other)
         {
            doFail(testname, other.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.4.3
    * The cmis:objectTypeId property value is not an Object-Type whose baseType is “Relationship”.
    * @throws Exception
    */
   @Test
   public void testCreateRelationship_ConstraintException1() throws Exception
   {
      String testname = "testCreateRelationship_ConstraintException1";
      System.out.print("Running " + testname + "....                    ");
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      ObjectData obj = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);
         DocumentData doc2 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc2"),
               cs, null, null, VersioningState.MAJOR);

         Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         Map<String, Property<?>> props = new HashMap<String, Property<?>>();

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefSource =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.SOURCE_ID, PropertyType.ID,
               CmisConstants.SOURCE_ID, CmisConstants.SOURCE_ID, null, CmisConstants.SOURCE_ID, true, false, false,
               false, false, Updatability.READONLY, "SourceId", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefTarget =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.TARGET_ID, PropertyType.ID,
               CmisConstants.TARGET_ID, CmisConstants.TARGET_ID, null, CmisConstants.TARGET_ID, false, false, false,
               false, false, Updatability.READONLY, "TargetId", null, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "rel", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         props.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "rel1"));

         props.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:my"));

         props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
            fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc1.getObjectId()));
         props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
            fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));

         //fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
         //fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);

         TypeDefinition newType =
            new TypeDefinition("cmis:my", BaseType.FOLDER, "cmis:my", "cmis:my", "", "cmis:folder", "cmis:my",
               "cmis:my", true, false, true, true, false, true, true, false, null, null,
               ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         try
         {
            String docId = getConnection().createRelationship(props, null, null, null);
            obj = getStorage().getObjectById(docId);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.4.3
    * The sourceObjectId’s ObjectType is not in the list of “allowedSourceTypes” specified by 
    * the Object-Type definition specified by cmis:objectTypeId property value.
    * The targetObjectId’s ObjectType is not in the list of “allowedTargetTypes” specified by 
    * the Object-Type definition specified by cmis:objectTypeId property value.
    * @throws Exception
    */
   @Test
   public void testCreateRelationship_ConstraintException2() throws Exception
   {
      String testname = "testCreateRelationship_ConstraintException2";
      System.out.print("Running " + testname + "....                    ");
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      ObjectData obj = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);
         DocumentData doc2 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc2"),
               cs, null, null, VersioningState.MAJOR);

         Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         Map<String, Property<?>> props = new HashMap<String, Property<?>>();

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefSource =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.SOURCE_ID, PropertyType.ID,
               CmisConstants.SOURCE_ID, CmisConstants.SOURCE_ID, null, CmisConstants.SOURCE_ID, true, false, false,
               false, false, Updatability.READONLY, "SourceId", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefTarget =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.TARGET_ID, PropertyType.ID,
               CmisConstants.TARGET_ID, CmisConstants.TARGET_ID, null, CmisConstants.TARGET_ID, false, false, false,
               false, false, Updatability.READONLY, "TargetId", null, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "rel", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         props.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "rel1"));

         props.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:my"));

         props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
            fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc1.getObjectId()));
         props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
            fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));

         //fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
         //fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);
         String[] allowed = {"cmis:folder"};

         TypeDefinition newType =
            new TypeDefinition("cmis:my", BaseType.RELATIONSHIP, "cmis:my", "cmis:my", "", "cmis:relationship",
               "cmis:my", "cmis:my", true, false, true, true, false, true, true, false, allowed, allowed,
               ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         try
         {
            String docId = getConnection().createRelationship(props, null, null, null);
            obj = getStorage().getObjectById(docId);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.4.3
    * The “controllablePolicy” attribute of the Object-Type definition specified by the 
    * cmis:objectTypeId property value is set to FALSE and at least one policy is provided.
    * @throws Exception
    */
   @Test
   public void testCreateRelationship_ConstraintException3() throws Exception
   {
      String testname = "testCreateRelationship_ConstraintException3";
      System.out.print("Running " + testname + "....                    ");
      if (!IS_POLICIES_SUPPORTED || !IS_RELATIONSHIPS_SUPPORTED)
      {
         skip("ObjectTest." + testname);
         return;
      }

      FolderData testroot = null;
      ObjectData obj = null;
      String typeID = null;
      PolicyData policy = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);
         DocumentData doc2 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc2"),
               cs, null, null, VersioningState.MAJOR);

         Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         Map<String, Property<?>> props = new HashMap<String, Property<?>>();

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefSource =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.SOURCE_ID, PropertyType.ID,
               CmisConstants.SOURCE_ID, CmisConstants.SOURCE_ID, null, CmisConstants.SOURCE_ID, true, false, false,
               false, false, Updatability.READONLY, "SourceId", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefTarget =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.TARGET_ID, PropertyType.ID,
               CmisConstants.TARGET_ID, CmisConstants.TARGET_ID, null, CmisConstants.TARGET_ID, false, false, false,
               false, false, Updatability.READONLY, "TargetId", null, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "rel1", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         props.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "rel1"));

         props.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino2"));

         props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
            fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc1.getObjectId()));
         props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
            fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino2", BaseType.RELATIONSHIP, "cmis:kino2", "cmis:kino2", "",
               "cmis:relationship", "cmis:kino2", "cmis:kino2", true, false, true, true, false, false, false, false,
               null, null, ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         policy = createPolicy(testroot, "policy1");
         ArrayList<String> policies = new ArrayList<String>();
         policies.add(policy.getObjectId());
         try
         {
            String docId = getConnection().createRelationship(props, null, null, policies);
            obj = getStorage().getObjectById(docId);
            doFail(testname, "ConstraintException must be thrown;");
         }
         catch (ConstraintException xe)
         {
            pass(testname);
         }
         catch (Exception other)
         {
            doFail(testname, other.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         if (policy != null)
            getStorage().deleteObject(policy, true);
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.4.3
    * The “controllableACL” attribute of the Object-Type definition specified by the 
    * cmis:objectTypeId property value is set to FALSE and at least one ACE is provided.
    * @throws Exception
    */
   @Test
   public void testCreateRelationship_ConstraintException4() throws Exception
   {
      String testname = "testCreateRelationship_ConstraintException4";
      System.out.print("Running " + testname + "....                    ");
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         skip("ObjectTest." + testname);
         return;
      }
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      ObjectData obj = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);
         DocumentData doc2 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc2"),
               cs, null, null, VersioningState.MAJOR);

         Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         Map<String, Property<?>> props = new HashMap<String, Property<?>>();

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefSource =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.SOURCE_ID, PropertyType.ID,
               CmisConstants.SOURCE_ID, CmisConstants.SOURCE_ID, null, CmisConstants.SOURCE_ID, true, false, false,
               false, false, Updatability.READONLY, "SourceId", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefTarget =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.TARGET_ID, PropertyType.ID,
               CmisConstants.TARGET_ID, CmisConstants.TARGET_ID, null, CmisConstants.TARGET_ID, false, false, false,
               false, false, Updatability.READONLY, "TargetId", null, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "rel1", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         props.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "rel1"));

         props.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino"));

         props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
            fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc1.getObjectId()));
         props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
            fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));

         //fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
         //fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.RELATIONSHIP, "cmis:kino", "cmis:kino", "", "cmis:relationship",
               "cmis:kino", "cmis:kino", true, false, true, true, false, false, false, false, null, null,
               ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         AccessControlEntry acl = new AccessControlEntry();
         acl.setPrincipal("Makis");
         acl.getPermissions().add("cmis:read");
         ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
         addACL.add(acl);

         try
         {
            String docId = getConnection().createRelationship(props, addACL, null, null);
            obj = getStorage().getObjectById(docId);
            doFail(testname, "ConstraintException must be thrown;");
         }
         catch (ConstraintException e)
         {
            pass(testname);
         }
         catch (Exception other)
         {
            doFail(testname, other.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.4.3
    * At least one of the permissions is used in an ACE provided which is not supported by the repository. 
    * @throws Exception
    */
   @Test
   public void testCreateRelationship_ConstraintException5() throws Exception
   {
      String testname = "testCreateRelationship_ConstraintException5";
      System.out.print("Running " + testname + "....                    ");
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         skip("ObjectTest." + testname);
         return;
      }
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      ObjectData obj = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);
         DocumentData doc2 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc2"),
               cs, null, null, VersioningState.MAJOR);

         Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         Map<String, Property<?>> props = new HashMap<String, Property<?>>();

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefSource =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.SOURCE_ID, PropertyType.ID,
               CmisConstants.SOURCE_ID, CmisConstants.SOURCE_ID, null, CmisConstants.SOURCE_ID, true, false, false,
               false, false, Updatability.READONLY, "SourceId", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefTarget =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.TARGET_ID, PropertyType.ID,
               CmisConstants.TARGET_ID, CmisConstants.TARGET_ID, null, CmisConstants.TARGET_ID, false, false, false,
               false, false, Updatability.READONLY, "TargetId", null, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "rel1", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         props.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "rel1"));

         props.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino"));

         props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
            fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc1.getObjectId()));
         props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
            fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));

         //fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
         //fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.RELATIONSHIP, "cmis:kino", "cmis:kino", "", "cmis:relationship",
               "cmis:kino", "cmis:kino", true, false, true, true, false, false, true, false, null, null,
               ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);

         AccessControlEntry acl = new AccessControlEntry();
         acl.setPrincipal("Makis");
         acl.getPermissions().add("cmis:unknown");
         ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
         addACL.add(acl);

         try
         {
            String docId = getConnection().createRelationship(props, addACL, null, null);
            obj = getStorage().getObjectById(docId);
            doFail(testname, "ConstraintException must be thrown;");
         }
         catch (ConstraintException e)
         {
            pass(testname);
         }
         catch (Exception other)
         {
            doFail(testname, other.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.5
    * Creates a policy object of the specified type.
    * @throws Exception
    */
   @Test
   public void testCreatePolicy_Simple() throws Exception
   {
      String testname = "testCreatePolicy_Simple";
      System.out.print("Running " + testname + "....                                        ");
      if (!IS_POLICIES_SUPPORTED)
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      ObjectData obj = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         org.xcmis.spi.model.PropertyDefinition<?> def =
            PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);
         Map<String, Property<?>> properties = getPropsMap("cmis:policy", "policy");
         properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def.getId(), def.getQueryName(), def
            .getLocalName(), def.getDisplayName(), "testPolicyText"));

         try
         {
            String docId = getConnection().createPolicy(testroot.getObjectId(), properties, null, null, null);
            obj = getStorage().getObjectById(docId);
            if (!obj.getTypeId().equals("cmis:policy"))
               doFail(testname, "Cmis object types does not match");
            if (!((PolicyData)obj).getPolicyText().equals("testPolicyText"))
               doFail(testname, "Cmis policy text does not match");
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.5.1
    * A list of policy IDs that MUST be applied to the newly-created Policy object. 
    * @throws Exception
    */
   @Test
   public void testCreatePolicy_AddPolicy() throws Exception
   {
      String testname = "testCreatePolicy_AddPolicy";
      System.out.print("Running " + testname + "....                                     ");
      if (!IS_POLICIES_SUPPORTED)
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      ObjectData obj = null;
      PolicyData policy = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         org.xcmis.spi.model.PropertyDefinition<?> def =
            PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);
         Map<String, Property<?>> properties = getPropsMap("cmis:policy", "policy");
         properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def.getId(), def.getQueryName(), def
            .getLocalName(), def.getDisplayName(), "testPolicyText1"));

         policy = createPolicy(testroot, "policy1");

         ArrayList<String> policies = new ArrayList<String>();
         policies.add(policy.getObjectId());
         try
         {
            String docId = getConnection().createPolicy(testroot.getObjectId(), properties, null, null, policies);
            obj = getStorage().getObjectById(docId);
            Iterator<PolicyData> it = obj.getPolicies().iterator();
            while (it.hasNext())
            {
               PolicyData one = it.next();
               if (!one.getName().equals("policy1"))
                  doFail(testname, "POlicy names does not match");
               if (!one.getPolicyText().equals("testPolicyText"))
                  doFail(testname, "Policy text does not match");
               obj.removePolicy(one);
            }
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         if (policy != null)
            getStorage().deleteObject(policy, true);
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.5.1
    * A list of ACEs that MUST be added to the newly-created Policy object. 
    * @throws Exception
    */
   @Test
   public void testCreatePolicy_AddACL() throws Exception
   {
      String testname = "testCreatePolicy_AddACL";
      System.out.print("Running " + testname + "....                                        ");
      if (!IS_POLICIES_SUPPORTED)
      {
         skip("ObjectTest." + testname);
         return;
      }
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      ObjectData obj = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         org.xcmis.spi.model.PropertyDefinition<?> def =
            PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);
         Map<String, Property<?>> properties = getPropsMap("cmis:policy", "policy");
         properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def.getId(), def.getQueryName(), def
            .getLocalName(), def.getDisplayName(), "testPolicyText"));

         AccessControlEntry acl = new AccessControlEntry();
         acl.setPrincipal("Makis");
         acl.getPermissions().add("cmis:read");
         ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
         addACL.add(acl);

         try
         {
            String docId = getConnection().createPolicy(testroot.getObjectId(), properties, addACL, null, null);
            obj = getStorage().getObjectById(docId);
            for (AccessControlEntry one : obj.getACL(false))
            {
               if (one.getPrincipal().equalsIgnoreCase("Makis"))
               {
                  if (one.getPermissions().size() != 1)
                     doFail(testname, "Permissions size is incorrect");
                  if (!one.getPermissions().contains("cmis:read"))
                     doFail(testname, "Permissions does not match");
               }
            }
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.5.3
    * If the repository detects a violation with the given cmis:name property value, the repository MAY 
    * throw this exception or chose a name which does not conflict.
    * @throws Exception
    */
   @Test
   public void testCreatePolicy_NameConstraintViolationException() throws Exception
   {
      String testname = "testCreatePolicy_NameConstraintViolationException";
      System.out.print("Running " + testname + "....              ");
      if (!IS_POLICIES_SUPPORTED)
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      ObjectData obj = null;
      PolicyData policy = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         org.xcmis.spi.model.PropertyDefinition<?> def =
            PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);
         Map<String, Property<?>> properties = getPropsMap("cmis:policy", "policy1");
         properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def.getId(), def.getQueryName(), def
            .getLocalName(), def.getDisplayName(), "testPolicyText1"));
         policy = createPolicy(testroot, "policy1");
         obj = null;
         try
         {
            String docId = getConnection().createPolicy(testroot.getObjectId(), properties, null, null, null);
            obj = getStorage().getObjectById(docId);
            if (obj.getName().equals("policy1"))
               doFail(testname, "Names must not match;");
            pass(testname);
         }
         catch (NameConstraintViolationException ex)
         {
            pass(testname);
         }
         catch (Exception other)
         {
            doFail(testname, other.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         if (policy != null)
            getStorage().deleteObject(policy, true);
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.3.3
    * The Repository MUST throw this exception if  to  The cmis:objectTypeId 
    * property value is not an Object-Type whose baseType is “Policy”.
    * @throws Exception
    */
   @Test
   public void testCreatePolicy_ConstraintException1() throws Exception
   {
      String testname = "testCreatePolicy_ConstraintException1";
      System.out.print("Running " + testname + "....                          ");
      if (!IS_POLICIES_SUPPORTED)
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "f1", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> def =
            PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "policy1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino"));
         properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def.getId(), def.getQueryName(), def
            .getLocalName(), def.getDisplayName(), "testPolicyText1"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.FOLDER, "cmis:kino", "cmis:kino", "", "cmis:folder", "cmis:kino",
               "cmis:kino", true, false, true, true, false, false, false, true, null, null,
               ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         try
         {
            String docId = getConnection().createPolicy(testroot.getObjectId(), properties, null, null, null);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.5.3
    * The cmis:objectTypeId property value is NOT in the list of AllowedChildObjectTypeIds 
    * of the parent-folder specified by folderId. 
    * @throws Exception
    */
   @Test
   public void testCreatePolicy_ConstraintException2() throws Exception
   {
      String testname = "testCreatePolicy_ConstraintException2";
      System.out.print("Running " + testname + "....                          ");
      if (!IS_POLICIES_SUPPORTED)
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         Map<String, Property<?>> folderprops = getPropsMap(CmisConstants.FOLDER, "testroot");

         org.xcmis.spi.model.PropertyDefinition<?> def =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, PropertyType.ID,
               CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, null,
               CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, false, false, false, false, false, Updatability.READONLY,
               "allowed", null, null, null);
         folderprops.put(CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, new IdProperty(def.getId(), def.getQueryName(),
            def.getLocalName(), def.getDisplayName(), CmisConstants.DOCUMENT));

         testroot = getStorage().createFolder(rootFolder, folderTypeDefinition, folderprops, null, null);

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
            PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "policy1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino"));
         properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def2.getId(), def2.getQueryName(), def2
            .getLocalName(), def2.getDisplayName(), "testPolicyText1"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.FOLDER, "cmis:kino", "cmis:kino", "", "cmis:folder", "cmis:kino",
               "cmis:kino", true, false, true, true, false, false, false, true, null, null,
               ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         try
         {
            String docId = getConnection().createPolicy(testroot.getObjectId(), properties, null, null, null);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.5.3
    * The “controllablePolicy” attribute of the Object-Type definition specified by 
    * the cmis:objectTypeId property value is set to FALSE and at least one policy is provided.
    * @throws Exception
    */
   @Test
   public void testCreatePolicy_ConstraintException3() throws Exception
   {
      String testname = "testCreatePolicy_ConstraintException3";
      System.out.print("Running " + testname + "....                          ");
      if (!IS_POLICIES_SUPPORTED)
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

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
            PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "policy1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino"));
         properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def2.getId(), def2.getQueryName(), def2
            .getLocalName(), def2.getDisplayName(), "testPolicyText1"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.FOLDER, "cmis:kino", "cmis:kino", "", "cmis:folder", "cmis:kino",
               "cmis:kino", true, false, true, true, false, false, false, false, null, null,
               ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         PolicyData policy = createPolicy(testroot, "policy2");

         ArrayList<String> policies = new ArrayList<String>();
         policies.add(policy.getObjectId());

         try
         {
            String docId = getConnection().createPolicy(testroot.getObjectId(), properties, null, null, policies);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.5.3
    * The “controllableACL” attribute of the Object-Type definition specified by the cmis:objectTypeId 
    * property value is set to FALSE and at least one ACE is provided.
    * @throws Exception
    */
   @Test
   public void testCreatePolicy_ConstraintException4() throws Exception
   {
      String testname = "testCreatePolicy_ConstraintException4";
      System.out.print("Running " + testname + "....                          ");
      if (!IS_POLICIES_SUPPORTED)
      {
         skip("ObjectTest." + testname);
         return;
      }
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

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
            PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "policy1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino"));
         properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def2.getId(), def2.getQueryName(), def2
            .getLocalName(), def2.getDisplayName(), "testPolicyText1"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.FOLDER, "cmis:kino", "cmis:kino", "", "cmis:folder", "cmis:kino",
               "cmis:kino", true, false, true, true, false, false, false, false, null, null,
               ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         AccessControlEntry acl = new AccessControlEntry();
         acl.setPrincipal("Makis");
         acl.getPermissions().add("cmis:read");
         ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
         addACL.add(acl);
         try
         {
            String docId = getConnection().createPolicy(testroot.getObjectId(), properties, addACL, null, null);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.5.3
    * The “controllableACL” attribute of the Object-Type definition specified by the cmis:objectTypeId 
    * property value is set to FALSE and at least one ACE is provided.
    * @throws Exception
    */
   @Test
   public void testCreatePolicy_ConstraintException5() throws Exception
   {
      String testname = "testCreatePolicy_ConstraintException5";
      System.out.print("Running " + testname + "....                          ");
      if (!IS_POLICIES_SUPPORTED)
      {
         skip("ObjectTest." + testname);
         return;
      }
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         org.xcmis.spi.model.PropertyDefinition<?> def =
            PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);
         Map<String, Property<?>> properties = getPropsMap("cmis:policy", "policy");
         properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def.getId(), def.getQueryName(), def
            .getLocalName(), def.getDisplayName(), "testPolicyText"));

         AccessControlEntry acl = new AccessControlEntry();
         acl.setPrincipal("Makis");
         acl.getPermissions().add("cmis:unknown");
         ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
         addACL.add(acl);
         try
         {
            String docId = getConnection().createPolicy(testroot.getObjectId(), properties, addACL, null, null);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.6
    * Gets the list of allowable actions for an Object.
    * @throws Exception
    */
   @Test
   public void testGetAllowableActions_Simlpe() throws Exception
   {
      String testname = "testGetAllowableActions_Simlpe";
      System.out.print("Running " + testname + "....                                 ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         try
         {
            AllowableActions actions = getConnection().getAllowableActions(testroot.getObjectId());
            if (actions == null)
               doFail(testname, "Allowable actions is null;");
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.7
    * Gets the specified information for the Object. 
    * @throws Exception
    */
   @Test
   public void testGetObject_Simlpe() throws Exception
   {
      String testname = "testGetObject_Simlpe";
      System.out.print("Running " + testname + "....                                           ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         try
         {
            CmisObject obj =
               getConnection().getObject(testroot.getObjectId(), false, IncludeRelationships.NONE, false, false, true,
                  "", "*");
            if (!obj.getObjectInfo().getName().equals("testroot"))
               doFail(testname, "Names does not match;");
            if (!testroot.getObjectId().equals(obj.getObjectInfo().getId()))
               doFail(testname, "Object ID's does not match;");
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.7
    * Repositories SHOULD return only the properties specified in the property filter 
    * if they exist on the object’s type definition.
    * @throws Exception
    */
   @Test
   public void testGetObject_PropertyFiltered() throws Exception
   {
      String testname = "testGetObject_PropertyFiltered";
      System.out.print("Running " + testname + "....                                 ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         try
         {
            CmisObject obj =
               getConnection().getObject(testroot.getObjectId(), false, IncludeRelationships.NONE, false, false, false,
                  "cmis:name,cmis:path", "*");
            for (Map.Entry<String, Property<?>> e : obj.getProperties().entrySet())
            {
               if (e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path")) //Other props must be ignored
                  continue;
               else
                  doFail(testname, "Property filter does not work;");
            }
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.7
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   @Test
   public void testGetObject_IncludeRelationships() throws Exception
   {
      String testname = "testGetObject_IncludeRelationships";
      System.out.print("Running " + testname + "....                             ");
      FolderData testroot = null;
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

         RelationshipData reldata =
            getStorage().createRelationship(doc1, testroot, relationshipTypeDefinition,
               getPropsMap("cmis:relationship", "rel1"), null, null);

         try
         {
            CmisObject obj =
               getConnection().getObject(testroot.getObjectId(), false, IncludeRelationships.TARGET, false, false,
                  true, "", "*");
            if (obj.getRelationship().size() != 1)
            {
               doFail(testname, "Relationships count is incorrect;");
            }
            for (CmisObject e : obj.getRelationship())
            {
               if (!reldata.getObjectId().equals(e.getObjectInfo().getId()))
                  doFail(testname, "Object ID's does not match;");
            }
            pass(testname);
            getStorage().deleteObject(reldata, true);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.7
    * The Repository MUST return the Ids of the policies applied to the object.  Defaults to FALSE.
    * @throws Exception
    */
   @Test
   public void testGetObject_IncludePolicyIDs() throws Exception
   {
      String testname = "testGetObject_IncludePolicyIDs";
      System.out.print("Running " + testname + "....                                 ");
      if (!IS_POLICIES_SUPPORTED)
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      PolicyData policy = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         policy = createPolicy(testroot, "policy1");
         try
         {
            getConnection().applyPolicy(policy.getObjectId(), testroot.getObjectId());
            CmisObject obj =
               getConnection().getObject(testroot.getObjectId(), false, IncludeRelationships.TARGET, true, false, true,
                  "", "*");
            if (obj.getPolicyIds().size() != 1)
               doFail(testname, "Pilicy count is incorrect;");
            for (String e : obj.getPolicyIds())
            {
               if (!policy.getObjectId().equals(e))
                  doFail(testname, "Object ID's does not match;");
            }
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (policy != null)
            getStorage().deleteObject(policy, true);
      }
   }

   /**
    * 2.2.4.7
    * If TRUE, then the Repository MUST return the ACLs for each object in the result set.
    * @throws Exception
    */
   @Test
   public void testGetObject_IncludeACLs() throws Exception
   {
      String testname = "testGetObject_IncludeACLs";
      System.out.print("Running " + testname + "....                                      ");
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         AccessControlEntry acl = new AccessControlEntry();
         acl.setPrincipal("Makis");
         acl.getPermissions().add("cmis:read");
         ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
         addACL.add(acl);

         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         try
         {
            DocumentData doc1 =
               getStorage().createDocument(testroot, documentTypeDefinition,
                  getPropsMap(CmisConstants.DOCUMENT, "doc1"), cs, addACL, null, VersioningState.MAJOR);

            CmisObject obj =
               getConnection().getObject(doc1.getObjectId(), false, IncludeRelationships.TARGET, true, true, true, "",
                  "*");
            for (AccessControlEntry one : obj.getACL())
            {
               if (one.getPrincipal().equalsIgnoreCase("Makis"))
               {
                  if (one.getPermissions().size() != 1)
                     doFail(testname, "Permissions size is incorrect");
                  if (!one.getPermissions().contains("cmis:read"))
                     doFail(testname, "Permissions does not match");
               }
            }
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.7
    * If TRUE, then the Repository MUST return the available actions for each object in the result set. 
    * @throws Exception
    */
   @Test
   public void testGetObject_IncludeAllowableActions() throws Exception
   {
      String testname = "testGetObject_IncludeAllowableActions";
      System.out.print("Running " + testname + "....                          ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         try
         {
            CmisObject obj =
               getConnection().getObject(testroot.getObjectId(), true, IncludeRelationships.TARGET, false, false, true,
                  "", "*");
            AllowableActions actions = obj.getAllowableActions();
            if (actions == null)
               doFail(testname, "AllowableActions is null;");
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.7.3
    * The Repository MUST throw this exception if this property filter input parameter is not valid.
    * @throws Exception
    */
   @Test
   public void testGetObject_FilterNotValidException() throws Exception
   {
      String testname = "testGetObject_FilterNotValidException";
      System.out.print("Running " + testname + "....                          ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         try
         {
            CmisObject obj =
               getConnection().getObject(testroot.getObjectId(), false, IncludeRelationships.NONE, false, false, false,
                  "(,*", "*");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());
      }
   }

   /**
    * 2.2.4.8
    * Gets the list of properties for an Object.
    * 
    * @throws Exception
    */
   @Test
   public void testGetProperties_Filter() throws Exception
   {
      String testname = "testGetProperties_Filter";
      System.out.print("Running " + testname + " ....                                       ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         try
         {
            CmisObject obj = getConnection().getProperties(testroot.getObjectId(), true, "cmis:name,cmis:path");
            if (obj == null)
            {
               doFail(testname, "Get properties result is null");
            }
            for (Map.Entry<String, Property<?>> e : obj.getProperties().entrySet())
            {
               if (e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path")) //Other props must be ignored
                  continue;
               else
                  doFail(testname, "Property filter does not work;");
            }
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.8
    * The Repository MUST throw this exception if this property filter input parameter is not valid.
    * 
    * @throws Exception
    */
   @Test
   public void testGetProperties_FilterNotValidException() throws Exception
   {
      String testname = "testGetProperties_FilterNotValidException";
      System.out.print("Running " + testname + "....                      ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         try
         {
            CmisObject obj = getConnection().getProperties(testroot.getObjectId(), true, "(,*");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.9.1
    * Gets the specified object. 
    * @throws Exception
    */
   @Test
   public void testGetObjectByPath_Simlpe() throws Exception
   {
      String testname = "testGetObjectByPath_Simlpe";
      System.out.print("Running " + testname + "....                                     ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         try
         {
            CmisObject obj =
               getConnection().getObjectByPath("/testroot", false, IncludeRelationships.NONE, false, false, true, "",
                  "*");
            if (!obj.getObjectInfo().getName().equals("testroot"))
               doFail(testname, "Names does not match;");
            if (!testroot.getObjectId().equals(obj.getObjectInfo().getId()))
               doFail(testname, "Object ID's does not match;");
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.9.1
    * Repositories SHOULD return only the properties specified in the property filter 
    * if they exist on the object’s type definition.
    * @throws Exception
    */
   @Test
   public void testGetObjectByPath_PropertyFiltered() throws Exception
   {
      String testname = "testGetObjectByPath_PropertyFiltered";
      System.out.print("Running " + testname + "....                           ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         try
         {
            CmisObject obj =
               getConnection().getObjectByPath("/testroot", false, IncludeRelationships.NONE, false, false, false,
                  "cmis:name,cmis:path", "*");
            for (Map.Entry<String, Property<?>> e : obj.getProperties().entrySet())
            {
               if (e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path")) //Other props must be ignored
                  continue;
               else
                  doFail(testname, "Property filter does not work;");
            }
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.9.1
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   @Test
   public void testGetObjectByPath_IncludeRelationships() throws Exception
   {
      String testname = "testGetObjectByPath_IncludeRelationships";
      System.out.print("Running " + testname + "....                       ");
      FolderData testroot = null;
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

         RelationshipData reldata =
            getStorage().createRelationship(doc1, testroot, relationshipTypeDefinition,
               getPropsMap("cmis:relationship", "rel1"), null, null);

         try
         {
            CmisObject obj =
               getConnection().getObjectByPath("/testroot", false, IncludeRelationships.TARGET, false, false, true, "",
                  "*");
            if (obj.getRelationship().size() != 1)
               doFail(testname, "Incorect relationship size;");
            for (CmisObject e : obj.getRelationship())
            {
               if (!reldata.getObjectId().equals(e.getObjectInfo().getId()))
                  doFail(testname, "Object ID's does not match;");
            }
            getStorage().deleteObject(reldata, true);
            pass(testname);
         }
         catch (Exception e)
         {
            //e.printStackTrace();
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.9.1
    * The Repository MUST return the Ids of the policies applied to the object.  Defaults to FALSE.
    * @throws Exception
    */
   @Test
   public void testGetObjectByPath_IncludePolicyIDs() throws Exception
   {
      String testname = "testGetObjectByPath_IncludePolicyIDs";
      System.out.print("Running " + testname + "....                           ");
      if (!IS_POLICIES_SUPPORTED)
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      PolicyData policy = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         policy = createPolicy(testroot, "policy1");
         try
         {
            getConnection().applyPolicy(policy.getObjectId(), testroot.getObjectId());
            CmisObject obj =
               getConnection().getObjectByPath("/testroot", false, IncludeRelationships.TARGET, true, false, true, "",
                  "*");
            if (obj.getPolicyIds().size() != 1)
               doFail(testname, "Incorect policyIds size;");

            for (String e : obj.getPolicyIds())
            {
               if (!policy.getObjectId().equals(e))
                  doFail(testname, "Object ID's does not match; ");
            }
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (policy != null)
            getStorage().deleteObject(policy, true);
      }
   }

   /**
    * 2.2.4.9.1
    * If TRUE, then the Repository MUST return the ACLs for each object in the result set.
    * @throws Exception
    */
   @Test
   public void testGetObjectByPath_IncludeACLs() throws Exception
   {
      String testname = "testGetObjectByPath_IncludeACLs";
      System.out.print("Running " + testname + "....                                ");
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         skip("ObjectTest." + testname);
         return;
      }
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         AccessControlEntry acl = new AccessControlEntry();
         acl.setPrincipal("Makis");
         acl.getPermissions().add("cmis:read");
         ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
         addACL.add(acl);

         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         try
         {
            DocumentData doc1 =
               getStorage().createDocument(testroot, documentTypeDefinition,
                  getPropsMap(CmisConstants.DOCUMENT, "doc1"), cs, addACL, null, VersioningState.MAJOR);

            CmisObject obj =
               getConnection().getObjectByPath("/testroot/doc1", false, IncludeRelationships.TARGET, true, true, true,
                  "", "*");
            for (AccessControlEntry one : obj.getACL())
            {
               if (one.getPrincipal().equalsIgnoreCase("Makis"))
               {
                  if (one.getPermissions().size() != 1)
                     doFail(testname, "Permissions size is incorrect");
                  if (!one.getPermissions().contains("cmis:read"))
                     doFail(testname, "Permissions does not match");
               }
            }
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.9.1
    * : If TRUE, then the Repository MUST return the available actions for each object in the result set. 
    * @throws Exception
    */
   @Test
   public void testGetObjectByPath_IncludeAllowableActions() throws Exception
   {
      String testname = "testGetObjectByPath_IncludeAllowableActions";
      System.out.print("Running " + testname + "....                    ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         try
         {
            CmisObject obj =
               getConnection().getObjectByPath("/testroot", true, IncludeRelationships.TARGET, false, false, true, "",
                  "*");
            AllowableActions actions = obj.getAllowableActions();
            if (actions == null)
               doFail(testname, "AllowableActions must not be null");
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.9.3
    * The Repository MUST throw this exception if this property filter input parameter is not valid.
    * @throws Exception
    */
   @Test
   public void testGetObjectByPath_FilterNotValidException() throws Exception
   {
      String testname = "testGetObjectByPath_FilterNotValidException";
      System.out.print("Running " + testname + "....                    ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         try
         {
            CmisObject obj =
               getConnection()
                  .getObject("/testroot", false, IncludeRelationships.NONE, false, false, false, "(,*", "*");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.10
    * Gets the content stream for the specified Document object, or gets a 
    * rendition stream for a specified rendition of a document or folder object.
    * @throws Exception
    */
   @Test
   public void testGetContentStream_Simple() throws Exception
   {
      String testname = "testGetContentStream_Simple";
      System.out.print("Running " + testname + "....                                    ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         byte[] before = new byte[15];
         before = "1234567890aBcDE".getBytes();

         ContentStream cs = new BaseContentStream(before, null, new MimeType("text", "plain"));
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);
         try
         {
            ContentStream obj = getConnection().getContentStream(doc1.getObjectId(), null);
            byte[] after = new byte[15];
            obj.getStream().read(after);
            assertArrayEquals(before, after);
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.10.3
    * The Repository MUST throw this exception if the object specified by objectId does 
    * NOT have a content stream or rendition stream. 
    * @throws Exception
    */
   @Test
   public void testGetContentStream_ConstraintException() throws Exception
   {
      String testname = "testGetContentStream_ConstraintException";
      System.out.print("Running " + testname + "....                       ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               null, null, null, VersioningState.MAJOR);
         try
         {
            ContentStream obj = getConnection().getContentStream(doc1.getObjectId(), null);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.11
    * Gets the list of associated Renditions for the specified object.
    * @throws Exception
    */
   @Test
   public void testGetRenditions_Simple() throws Exception
   {
      String testname = "testGetRenditions_Simple";
      System.out.print("Running " + testname + "....                                       ");
      if (!getStorage().getRepositoryInfo().getCapabilities().getCapabilityRenditions()
         .equals(CapabilityRendition.READ))
      {
         skip("ObjectTest.testGetRenditions_Simple");
         return;
      }

      FolderData testroot = null;
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
         try
         {
            List<Rendition> obj = getConnection().getRenditions(doc1.getObjectId(), "", -1, 0);
            if (obj == null)
               doFail(testname, "Get renditions result is null;");
            pass(testname);
         }
         catch (NotSupportedException ex)
         {
            pass(testname);
         }
         catch (Exception other)
         {
            doFail(testname, other.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.11.3
    * The filter specified is not valid.
    * @throws Exception
    */
   @Test
   public void testGetRenditions_FilterNotValidException() throws Exception
   {
      String testname = "testGetRenditions_FilterNotValidException";
      System.out.print("Running " + testname + "....                      ");
      if (!getStorage().getRepositoryInfo().getCapabilities().getCapabilityRenditions()
         .equals(CapabilityRendition.READ))
      {
         skip("ObjectTest.testGetRenditions_FilterNotValidException");
         return;
      }

      FolderData testroot = null;
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
         try
         {
            List<Rendition> obj = getConnection().getRenditions(doc1.getObjectId(), "(,*", -1, 0);
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.12
    * Updates properties of the specified object.
    * @throws Exception
    */
   @Test
   public void testUpdateProperties_Simple() throws Exception
   {
      String testname = "testUpdateProperties_Simple";
      System.out.print("Running " + testname + "....                                    ");
      FolderData testroot = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "f1", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefCreated =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.CREATED_BY, PropertyType.STRING,
               CmisConstants.CREATED_BY, CmisConstants.CREATED_BY, null, CmisConstants.CREATED_BY, true, false, false,
               false, false, Updatability.READWRITE, "f2", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefType =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "doc1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefType.getId(), fPropDefType.getQueryName(),
            fPropDefType.getLocalName(), fPropDefType.getDisplayName(), "cmis:kino"));
         properties.put(CmisConstants.CREATED_BY, new StringProperty(fPropDefCreated.getId(), fPropDefCreated
            .getQueryName(), fPropDefCreated.getLocalName(), fPropDefCreated.getDisplayName(), "_anonimous"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document",
               "cmis:kino", "cmis:kino", true, false, true, true, false, false, false, false, null, null,
               ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         DocumentData doc1 =
            getStorage().createDocument(testroot, newType, properties, cs, null, null, VersioningState.MAJOR);

         Map<String, Property<?>> properties2 = new HashMap<String, Property<?>>();
         properties2.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "new1"));

         properties2.put(CmisConstants.CREATED_BY, new StringProperty(fPropDefCreated.getId(), fPropDefCreated
            .getQueryName(), fPropDefCreated.getLocalName(), fPropDefCreated.getDisplayName(), "Makiz"));

         try
         {
            String id = getConnection().updateProperties(doc1.getObjectId(), new ChangeTokenHolder(), properties2);
            ObjectData obj = getStorage().getObjectById(id);
            if (!obj.getName().equals("new1"))
               doFail(testname, "Names does not match;");
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.12.3
    * The object is not checked out and ANY of the properties being updated are defined in their 
    * Object-Type definition have an attribute value of Updatability when checked-out.
    * @throws Exception
    */
   @Test
   public void testUpdateProperties_VersioningException() throws Exception
   {
      String testname = "testUpdateProperties_VersioningException";
      System.out.print("Running " + testname + "....                       ");
      FolderData testroot = null;
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
         Map<String, Property<?>> properties2 = new HashMap<String, Property<?>>();

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefComment =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.CHECKIN_COMMENT, PropertyType.STRING,
               CmisConstants.CHECKIN_COMMENT, CmisConstants.CHECKIN_COMMENT, null, CmisConstants.CHECKIN_COMMENT, true,
               false, false, false, false, Updatability.WHENCHECKEDOUT, "f2", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "f1", true, null, null);

         properties2.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "new1"));
         properties2.put(CmisConstants.CHECKIN_COMMENT, new StringProperty(fPropDefComment.getId(), fPropDefComment
            .getQueryName(), fPropDefComment.getLocalName(), fPropDefComment.getDisplayName(), "comment"));

         try
         {
            String id = getConnection().updateProperties(doc1.getObjectId(), new ChangeTokenHolder(), properties2);
            ObjectData obj = getStorage().getObjectById(id);
            doFail(testname, "VersioningException must be thrown;");
         }
         catch (VersioningException ex)
         {
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.13
    * Moves the specified file-able object from one folder to another. 
    * @throws Exception
    */
   @Test
   public void testMoveObject_Simple() throws Exception
   {
      String testname = "testMoveObject_Simple";
      System.out.print("Running " + testname + "....                                          ");
      FolderData testroot = null;
      FolderData folder2 = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         folder2 =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "2"), null,
               null);

         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);

         try
         {
            String id = getConnection().moveObject(doc1.getObjectId(), folder2.getObjectId(), testroot.getObjectId());
            ObjectData obj = getStorage().getObjectById(id);
            if (!folder2.getName().equals(obj.getParent().getName()))
               doFail(testname, "Names does not match;");
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
         pass(testname);
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());
         if (folder2 != null)
            clear(folder2.getObjectId());
      }
   }

   /**
    * 2.2.4.13.3
    * The Repository MUST throw this exception if the service is invoked with a missing sourceFolderId or the 
    * sourceFolderId doesn’t match the specified object’s parent folder.
    * @throws Exception
    */
   @Test
   public void testMoveObject_InvalidArgumentException() throws Exception
   {
      String testname = "testMoveObject_InvalidArgumentException";
      System.out.print("Running " + testname + "....                        ");
      FolderData testroot = null;
      FolderData folder2 = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         folder2 =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "2"), null,
               null);

         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);

         try
         {
            String id = getConnection().moveObject(doc1.getObjectId(), testroot.getObjectId(), folder2.getObjectId());
            ObjectData obj = getStorage().getObjectById(id);
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (folder2 != null)
            clear(folder2.getObjectId());
      }
   }

   /**
    * 2.2.4.13.3
    * The Repository MUST throw this exception if the cmis:objectTypeId property value of the given object is NOT 
    * in the list of AllowedChildObjectTypeIds of the parent-folder specified by targetFolderId. 
    * @throws Exception
    */
   @Test
   public void testMoveObject_ConstraintException() throws Exception
   {
      String testname = "testMoveObject_ConstraintException";
      System.out.print("Running " + testname + "....                             ");
      FolderData testroot = null;
      FolderData folder2 = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         Map<String, Property<?>> props2 = new HashMap<String, Property<?>>();

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "f1", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefType =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefAllowedChild =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, PropertyType.ID,
               CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, null,
               CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, false, false, false, false, false, Updatability.READONLY,
               "fold_type_chld_ids", null, null, null);

         props2.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "doc1"));
         props2.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefType.getId(), fPropDefType.getQueryName(),
            fPropDefType.getLocalName(), fPropDefType.getDisplayName(), "cmis:kino"));
         props2.put(CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, new IdProperty(fPropDefAllowedChild.getId(),
            fPropDefAllowedChild.getQueryName(), fPropDefAllowedChild.getLocalName(), fPropDefAllowedChild
               .getDisplayName(), "cmis:folder"));

         Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.FOLDER, "cmis:kino", "cmis:kino", "", "cmis:folder", "cmis:kino",
               "cmis:kino", true, false, true, true, false, false, false, false, null, null,
               ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         folder2 = getStorage().createFolder(rootFolder, newType, props2, null, null);

         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);

         try
         {
            String id = getConnection().moveObject(doc1.getObjectId(), folder2.getObjectId(), testroot.getObjectId());
            ObjectData obj = getStorage().getObjectById(id);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (folder2 != null)
            clear(folder2.getObjectId());
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.13.3
    * The Repository MUST throw this exception if the service is invoked with a missing sourceFolderId or the 
    * sourceFolderId doesn’t match the specified object’s parent folder.
    * @throws Exception
    */
   @Test
   public void testMoveObject_NameConstraintException() throws Exception
   {
      String testname = "testMoveObject_NameConstraintException";
      System.out.print("Running " + testname + "....                         ");
      FolderData testroot = null;
      FolderData folder2 = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);

         folder2 =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "folder2"),
               null, null);

         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);

         DocumentData doc2 =
            getStorage().createDocument(folder2, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);
         try
         {
            String id = getConnection().moveObject(doc1.getObjectId(), folder2.getObjectId(), testroot.getObjectId());
            ObjectData obj = getStorage().getObjectById(id);
            if (obj.getName().equalsIgnoreCase(doc1.getName()))
               doFail(testname, "Names must not match;");
         }
         catch (NameConstraintViolationException ex)
         {
            pass(testname);
         }
         catch (Exception other)
         {
            doFail(testname, other.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
         if (folder2 != null)
            clear(folder2.getObjectId());
      }
   }

   /**
    * 2.2.4.14
    * Deletes the specified object.   
    * @throws Exception
    */
   @Test
   public void testDeleteObject_Simple() throws Exception
   {
      String testname = "testDeleteObject_Simple";
      System.out.print("Running " + testname + "....                                        ");
      FolderData testroot = null;
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
         String id = doc1.getObjectId();
         try
         {
            getConnection().deleteObject(doc1.getObjectId(), true);
            ObjectData obj = getStorage().getObjectById(id);
         }
         catch (ObjectNotFoundException ex)
         {
            pass(testname);
         }
         catch (Exception other)
         {
            doFail(testname, other.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.14
    * The Repository MUST throw this exception if the method is invoked on a Folder object that contains one or more objects. 
    * @throws Exception
    */
   @Test
   public void testDeleteObject_ConstraintException() throws Exception
   {
      String testname = "testDeleteObject_ConstraintException";
      System.out.print("Running " + testname + "....                           ");
      FolderData testroot = null;
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
         try
         {
            getConnection().deleteObject(testroot.getObjectId(), true);
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.15
    *  Deletes the specified folder object and all of its child- and descendant-objects.
    * @throws Exception
    */
   @Test
   public void testDeleteTree_Simple() throws Exception
   {
      String testname = "testDeleteTree_Simple";
      System.out.print("Running " + testname + "....                                          ");
      FolderData testroot = null;
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

         FolderData fol1 =
            getStorage().createFolder(testroot, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "fol1"), null,
               null);

         String id = testroot.getObjectId();

         try
         {
            Collection<String> str = getConnection().deleteTree(id, true, UnfileObject.DELETE, true);
            ObjectData root = getStorage().getObjectById(id);
         }
         catch (ObjectNotFoundException ex)
         {
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         //if (testroot != null) clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.15
    *  Deletes the specified folder object and all of its child- and descendant-objects.
    * @throws Exception
    */
   @Test
   public void testDeleteTree_Unfile() throws Exception
   {
      String testname = "testDeleteTree_Unfile";
      System.out.print("Running " + testname + "....                                          ");
      if (!getStorage().getRepositoryInfo().getCapabilities().isCapabilityUnfiling())
      {
         skip("ObjectTest.testDeleteTree_Unfile");
         return;
      }

      FolderData testroot = null;
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

         FolderData fol1 =
            getStorage().createFolder(testroot, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "fol1"), null,
               null);

         String id1 = doc1.getObjectId();
         String id2 = doc2.getObjectId();
         boolean found1 = false;
         boolean found2 = false;

         try
         {
            Collection<String> str =
               getConnection().deleteTree(testroot.getObjectId(), true, UnfileObject.UNFILE, true);
            Iterator<String> it = getStorage().getUnfiledObjectsId();
            while (it.hasNext())
            {
               String one = it.next();
               if (one.equals(id1))
                  found1 = true;
               if (one.equals(id2))
                  found2 = true;
            }
            if (found1 && found2)
               pass(testname);
            else
               doFail(testname, "Not all objects was unfiled;");
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         //if (testroot != null) clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.16
    * Sets the content stream for the specified Document object.
    * @throws Exception
    */
   @Test
   public void testSetContentStream_Simple() throws Exception
   {
      String testname = "testSetContentStream_Simple";
      System.out.print("Running " + testname + "....                                    ");

      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         byte[] before = "1234567890aBcDE".getBytes();
         byte[] after = "zzz".getBytes();
         byte[] result = new byte[3];

         ContentStream cs1 = new BaseContentStream(before, null, new MimeType("text", "plain"));
         ContentStream cs2 = new BaseContentStream(after, null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs1, null, null, VersioningState.MAJOR);

         try
         {
            String docid = getConnection().setContentStream(doc1.getObjectId(), cs2, new ChangeTokenHolder(), true);
            getStorage().getObjectById(docid).getContentStream(null).getStream().read(result);
            assertArrayEquals(after, result);
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.16.3
    * The Repository MUST throw this exception if the input parameter overwriteFlag is FALSE and the Object already has a content-stream. 
    * @throws Exception
    */
   @Test
   public void testSetContentStream_ContentAlreadyExistsException() throws Exception
   {
      String testname = "testSetContentStream_ContentAlreadyExistsException";
      System.out.print("Running " + testname + "....             ");

      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         byte[] before = "1234567890aBcDE".getBytes();
         byte[] after = "zzz".getBytes();

         ContentStream cs1 = new BaseContentStream(before, null, new MimeType("text", "plain"));
         ContentStream cs2 = new BaseContentStream(after, null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs1, null, null, VersioningState.MAJOR);

         try
         {
            String docid = getConnection().setContentStream(doc1.getObjectId(), cs2, new ChangeTokenHolder(), false);
            doFail(testname, "ContentAlreadyExistsException must be thrown;");
         }
         catch (ContentAlreadyExistsException ex)
         {
            pass(testname);
         }
         catch (Exception other)
         {
            doFail(testname, other.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());;
      }
   }

   /**
    * 2.2.4.16.3
    * The Repository MUST throw this exception if the “contentStreamAllowed” attribute of the Object-Type 
    * definition specified by the cmis:objectTypeId property value of the given document is set to “notallowed”. 
    * @throws Exception
    */
   @Test
   public void testSetContentStream_StreamNotSupportedException() throws Exception
   {
      String testname = "testSetContentStream_StreamNotSupportedException";
      System.out.print("Running " + testname + "....               ");

      FolderData testroot = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         byte[] before = "1234567890aBcDE".getBytes();
         byte[] after = "zzz".getBytes();

         ContentStream cs1 = new BaseContentStream(before, null, new MimeType("text", "plain"));
         ContentStream cs2 = new BaseContentStream(after, null, new MimeType("text", "plain"));

         Map<String, Property<?>> props2 = new HashMap<String, Property<?>>();

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "f1", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefType =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         props2.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "doc1"));
         props2.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefType.getId(), fPropDefType.getQueryName(),
            fPropDefType.getLocalName(), fPropDefType.getDisplayName(), "cmis:kino"));

         Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         //fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
         //fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefType);

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document",
               "cmis:kino", "cmis:kino", true, false, true, true, false, false, false, false, null, null,
               ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         DocumentData doc1 =
            getStorage().createDocument(testroot, newType, props2, null, null, null, VersioningState.MAJOR);

         try
         {
            String docid = getConnection().setContentStream(doc1.getObjectId(), cs2, new ChangeTokenHolder(), false);
            doFail(testname, "StreamNotSupportedException must be thrown;");
         }
         catch (StreamNotSupportedException ex)
         {
            pass(testname);
         }
         catch (Exception other)
         {
            doFail(testname, other.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.17
    * Deletes the content stream for the specified Document object.
    * @throws Exception
    */
   @Test
   public void testDeleteContentStream_Simple() throws Exception
   {
      String testname = "testDeleteContentStream_Simple";
      System.out.print("Running " + testname + "....                                 ");

      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         byte[] before = "1234567890aBcDE".getBytes();
         byte[] result = new byte[3];

         ContentStream cs1 = new BaseContentStream(before, null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs1, null, null, VersioningState.MAJOR);

         try
         {
            String docid = getConnection().deleteContentStream(doc1.getObjectId(), new ChangeTokenHolder());
            if (getStorage().getObjectById(docid).getContentStream(null) != null)
               doFail(testname, "Content stream must be null;");
            pass(testname);
         }
         catch (Exception e)
         {
            doFail(testname, e.getMessage());
         }
      }
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());
      }
   }

   /**
    * 2.2.4.17.3
    * The Repository MUST throw this exception if the Object’s Object-Type definition “contentStreamAllowed” 
    * attribute is set to “required”. 
    * @throws Exception
    */
   @Test
   public void testDeleteContentStream_ConstraintException() throws Exception
   {
      String testname = "testDeleteContentStream_ConstraintException";
      System.out.print("Running " + testname + "....                    ");

      FolderData testroot = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         byte[] before = "1234567890aBcDE".getBytes();
         byte[] after = "zzz".getBytes();

         ContentStream cs1 = new BaseContentStream(before, null, new MimeType("text", "plain"));

         Map<String, Property<?>> props2 = new HashMap<String, Property<?>>();

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "f1", true, null, null);

         org.xcmis.spi.model.PropertyDefinition<?> fPropDefType =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         props2.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "doc1"));
         props2.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefType.getId(), fPropDefType.getQueryName(),
            fPropDefType.getLocalName(), fPropDefType.getDisplayName(), "cmis:kino"));

         Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         //fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
         //fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefType);

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document",
               "cmis:kino", "cmis:kino", true, false, true, true, false, false, false, false, null, null,
               ContentStreamAllowed.REQUIRED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         DocumentData doc1 =
            getStorage().createDocument(testroot, newType, props2, cs1, null, null, VersioningState.MAJOR);

         try
         {
            String docid = getConnection().deleteContentStream(doc1.getObjectId(), new ChangeTokenHolder());
            doFail(testname, "ConstraintException must be thrown;");
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
      catch (Exception ez)
      {
         doFail(testname, ez.getMessage());;
      }
      finally
      {
         if (testroot != null)
            clear(testroot.getObjectId());
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   @AfterClass
   public static void stop() throws Exception
   {
      if (BaseTest.conn != null)
         BaseTest.conn.close();
   }

   protected void pass(String method) throws Exception
   {
      super.pass("ObjectTest." + method);
   }

   protected void doFail(String method, String message) throws Exception
   {
      super.doFail("ObjectTest." + method, message);
   }
}
