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

import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import org.xcmis.spi.BaseContentStream;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.DocumentData;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.PolicyData;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.AllowableActions;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.ContentStreamAllowed;
import org.xcmis.spi.model.IncludeRelationships;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.PropertyType;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.UnfileObject;
import org.xcmis.spi.model.Updatability;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.model.impl.IdProperty;
import org.xcmis.spi.model.impl.StringProperty;
import org.xcmis.spi.utils.MimeType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class ObjectTest extends BaseTest
{

   /**
    * createDocument() test suite;
    */

   /**
    * 2.2.4.1.1
    * The Content Stream that MUST be stored for the 
    * newly-created Document Object. The method of passing the contentStream 
    * to the server and the encoding mechanism will be specified by each specific binding. 
    */
   public void testCreateDocument_CheckContent() throws Exception
   {
      System.out.print("Running testCreateDocument_CheckContent....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      byte[] before = new byte[15];
      before = "1234567890aBcDE".getBytes();
      ContentStream cs = new BaseContentStream(before, null, new MimeType("text", "plain"));
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      try
      {
         String docId =
            getConnection().createDocument(testroot.getObjectId(), getPropsMap("cmis:document", "doc1"), cs, null,
               null, null, VersioningState.MAJOR);
         ContentStream c = getStorage().getObjectById(docId).getContentStream(null);
         assertEquals(cs.getMediaType(), c.getMediaType());

         byte[] after = new byte[15];
         c.getStream().read(after);
         assertArrayEquals(before, after);
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }
   }

   /**
    * 2.2.4.1.1
    * The property values that MUST be applied to the newly-created Document Object.
    * @throws Exception
    */
   public void testCreateDocument_CheckProperties() throws Exception
   {
      System.out.print("Running testCreateDocument_CheckProperties....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      Map<String, Property<?>> properties = getPropsMap("cmis:document", "doc1");
      try
      {
         String docId =
            getConnection().createDocument(testroot.getObjectId(), properties, cs, null, null, null,
               VersioningState.MAJOR);
         ObjectData res = getStorage().getObjectById(docId);
         assertNotNull(res.getProperty(CmisConstants.NAME));
         assertEquals("doc1", (String)res.getProperty(CmisConstants.NAME).getValues().get(0)); //TODO: test more properties
         pass();

      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }
   }

   /**
    * 2.2.4.1.1
    * A list of policy IDs that MUST be applied to the newly-created Document object. 
    * @throws Exception
    */
   public void testCreateDocument_ApplyPolicy() throws Exception
   {
      System.out.print("Running testCreateDocument_ApplyPolicy....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      PolicyData policy = createPolicy(testroot, "policy1");

      ArrayList<String> policies = new ArrayList<String>();
      policies.add(policy.getObjectId());
      String docId = "";
      try
      {
         docId =
            getConnection().createDocument(testroot.getObjectId(), getPropsMap("cmis:document", "doc1"), cs, null,
               null, policies, VersioningState.MAJOR);
         ObjectData res = getStorage().getObjectById(docId);
         assertEquals(1, res.getPolicies().size());
         Iterator<PolicyData> it = res.getPolicies().iterator();
         while (it.hasNext())
         {
            PolicyData one = it.next();
            assertEquals("policy1", one.getName());
            assertEquals("testPolicyText", one.getPolicyText());
         }
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         getStorage().deleteObject(getStorage().getObjectById(docId), true);
         getStorage().deleteObject(policy, true);
         clear(testroot.getObjectId());
      }

   }

   /**
    * 2.2.4.1.1
    *   A list of ACEs that MUST be added to the newly-created Document object, 
    *   either using the ACL from folderId if specified, or being applied if no folderId is specified. 
    * @throws Exception
    */

   public void testCreateDocument_AddACL() throws Exception
   {
      System.out.print("Running testCreateDocument_ApplyACL....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      AccessControlEntry acl = new AccessControlEntry();
      acl.setPrincipal("Makis");
      acl.getPermissions().add("cmis:read");
      ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
      addACL.add(acl);
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      try
      {

         String docId =
            getConnection().createDocument(testroot.getObjectId(), getPropsMap("cmis:document", "doc1"), cs, addACL,
               null, null, VersioningState.MAJOR);
         ObjectData res = getStorage().getObjectById(docId);
         for (AccessControlEntry one : res.getACL(false))
         {
            if (one.getPrincipal().equalsIgnoreCase("Makis"))
            {
               assertEquals(1, one.getPermissions().size());
               assertTrue(one.getPermissions().contains("cmis:read"));
            }
         }
         pass();
      }
      catch (Exception e)
      {
         //e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }

   }

   /**
    * 2.2.4.1.3 • nameConstraintViolation:   
    * If the repository detects a violation with the given cmis:name property value, 
    * the repository MAY throw this exception or chose a name which does not conflict.  
    * @throws Exception
    */
   public void testCreateDocument_NameConstraintViolationException() throws Exception
   {
      System.out.print("Running testCreateDocument_NameConstraintViolationException....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      try
      {
         String docId =
            getConnection().createDocument(testroot.getObjectId(), getPropsMap("cmis:document", "doc1"), cs, null,
               null, null, VersioningState.MAJOR);
         doFail();
      }
      catch (NameConstraintViolationException ex)
      {
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }

   }

   /**
    * 2.2.4.1.3
    * The Repository MUST throw this exception if the “contentStreamAllowed” attribute 
    * of the Object-Type definition specified by the cmis:objectTypeId property 
    * value is set to “not allowed” and a contentStream input parameter is provided.
    * @throws Exception
    */

   public void testCreateDocument_StreamNotSupportedException() throws Exception
   {
      System.out.print("Running testCreateDocument_StreamNotSupportedException....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);

      Map<String, PropertyDefinition<?>> kinoPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
      org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefName2 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
            CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
            "doc1", true, null, null);
      org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefObjectTypeId2 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
            CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
            false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);
      kinoPropertyDefinitions.put(CmisConstants.NAME, kinoPropDefName2);
      kinoPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, kinoPropDefObjectTypeId2);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(kinoPropDefName2.getId(), kinoPropDefName2.getQueryName(),
         kinoPropDefName2.getLocalName(), kinoPropDefName2.getDisplayName(), "doc1"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(kinoPropDefObjectTypeId2.getId(),
         kinoPropDefObjectTypeId2.getQueryName(), kinoPropDefObjectTypeId2.getLocalName(), kinoPropDefObjectTypeId2
            .getDisplayName(), "cmis:kino"));

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document", "cmis:kino",
            "cmis:kino", true, false, true, true, false, false, false, true, null, null,
            ContentStreamAllowed.NOT_ALLOWED, kinoPropertyDefinitions);
      String typeID = getStorage().addType(newType);
      try
      {
         String docId =
            getConnection().createDocument(testroot.getObjectId(), properties, cs, null, null, null,
               VersioningState.MAJOR);
         doFail();

      }
      catch (StreamNotSupportedException ex)
      {
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.1.3
    * The cmis:objectTypeId property value is not an Object-Type whose baseType is “Document”.
    * @throws Exception
    */

   public void testCreateDocument_ConstraintException1() throws Exception
   {
      System.out.print("Running testCreateDocument_ConstraintException1....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);

      Map<String, PropertyDefinition<?>> kinoPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
      org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefName2 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
            CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
            "doc1", true, null, null);
      org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefObjectTypeId2 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
            CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
            false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);
      kinoPropertyDefinitions.put(CmisConstants.NAME, kinoPropDefName2);
      kinoPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, kinoPropDefObjectTypeId2);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(kinoPropDefName2.getId(), kinoPropDefName2.getQueryName(),
         kinoPropDefName2.getLocalName(), kinoPropDefName2.getDisplayName(), "doc1"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(kinoPropDefObjectTypeId2.getId(),
         kinoPropDefObjectTypeId2.getQueryName(), kinoPropDefObjectTypeId2.getLocalName(), kinoPropDefObjectTypeId2
            .getDisplayName(), "cmis:kino"));

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.FOLDER, "cmis:kino", "cmis:kino", "", "cmis:document", "cmis:kino",
            "cmis:kino", true, false, true, true, false, false, false, true, null, null,
            ContentStreamAllowed.NOT_ALLOWED, kinoPropertyDefinitions);
      String typeID = getStorage().addType(newType);
      try
      {
         String docId =
            getConnection().createDocument(testroot.getObjectId(), properties, cs, null, null, null,
               VersioningState.MAJOR);
         doFail();
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
      finally
      {
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.1.3 
    * The “contentStreamAllowed” attribute of the Object-Type definition specified by 
    * the cmis:objectTypeId property value is set to “required” and no contentStream input parameter is provided.  
    * @throws Exception
    */
   public void testCreateDocument_ConstraintException2() throws Exception
   {
      System.out.print("Running testCreateDocument_ConstraintException2....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);

      Map<String, PropertyDefinition<?>> kinoPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
      org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefName2 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
            CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
            "doc1", true, null, null);
      org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefObjectTypeId2 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
            CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
            false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);
      kinoPropertyDefinitions.put(CmisConstants.NAME, kinoPropDefName2);
      kinoPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, kinoPropDefObjectTypeId2);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(kinoPropDefName2.getId(), kinoPropDefName2.getQueryName(),
         kinoPropDefName2.getLocalName(), kinoPropDefName2.getDisplayName(), "doc1"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(kinoPropDefObjectTypeId2.getId(),
         kinoPropDefObjectTypeId2.getQueryName(), kinoPropDefObjectTypeId2.getLocalName(), kinoPropDefObjectTypeId2
            .getDisplayName(), "cmis:kino"));

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document", "cmis:kino",
            "cmis:kino", true, false, true, true, false, false, false, true, null, null, ContentStreamAllowed.REQUIRED,
            kinoPropertyDefinitions);
      String typeID = getStorage().addType(newType);
      try
      {
         String docId =
            getConnection().createDocument(testroot.getObjectId(), properties, null, null, null, null,
               VersioningState.MAJOR);
         doFail();
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
      finally
      {
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.1.3
    * The “versionable” attribute of the Object-Type definition specified by the cmis:objectTypeId property value is set to TRUE 
    * and the value for the versioningState input parameter is provided that is “none
    * @throws Exception
    */

   public void testCreateDocument_ConstraintException3() throws Exception
   {
      System.out.print("Running testCreateDocument_ConstraintException3....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);

      Map<String, PropertyDefinition<?>> kinoPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
      org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefName2 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
            CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
            "doc1", true, null, null);
      org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefObjectTypeId2 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
            CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
            false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);
      kinoPropertyDefinitions.put(CmisConstants.NAME, kinoPropDefName2);
      kinoPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, kinoPropDefObjectTypeId2);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(kinoPropDefName2.getId(), kinoPropDefName2.getQueryName(),
         kinoPropDefName2.getLocalName(), kinoPropDefName2.getDisplayName(), "doc1"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(kinoPropDefObjectTypeId2.getId(),
         kinoPropDefObjectTypeId2.getQueryName(), kinoPropDefObjectTypeId2.getLocalName(), kinoPropDefObjectTypeId2
            .getDisplayName(), "cmis:kino"));

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document", "cmis:kino",
            "cmis:kino", true, false, true, true, false, false, false, true, null, null,
            ContentStreamAllowed.NOT_ALLOWED, kinoPropertyDefinitions);
      String typeID = getStorage().addType(newType);
      try
      {
         String docId =
            getConnection().createDocument(testroot.getObjectId(), properties, cs, null, null, null,
               VersioningState.NONE);
         doFail();
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
      finally
      {
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.1.3
    * The “versionable” attribute of the Object-Type definition specified by the cmis:objectTypeId 
    * property value is set to FALSE and a value for the versioningState input parameter is provided that is something other than “none”.
    * @throws Exception
    */

   public void testCreateDocument_ConstraintException4() throws Exception
   {
      System.out.print("Running testCreateDocument_ConstraintException4....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);

      Map<String, PropertyDefinition<?>> kinoPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
      org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefName2 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
            CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
            "doc1", true, null, null);
      org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefObjectTypeId2 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
            CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
            false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);
      kinoPropertyDefinitions.put(CmisConstants.NAME, kinoPropDefName2);
      kinoPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, kinoPropDefObjectTypeId2);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(kinoPropDefName2.getId(), kinoPropDefName2.getQueryName(),
         kinoPropDefName2.getLocalName(), kinoPropDefName2.getDisplayName(), "doc1"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(kinoPropDefObjectTypeId2.getId(),
         kinoPropDefObjectTypeId2.getQueryName(), kinoPropDefObjectTypeId2.getLocalName(), kinoPropDefObjectTypeId2
            .getDisplayName(), "cmis:kino"));

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document", "cmis:kino",
            "cmis:kino", true, false, true, true, false, false, false, false, null, null,
            ContentStreamAllowed.NOT_ALLOWED, kinoPropertyDefinitions);
      String typeID = getStorage().addType(newType);
      try
      {
         String docId =
            getConnection().createDocument(testroot.getObjectId(), properties, cs, null, null, null,
               VersioningState.MAJOR);
         doFail();
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
      finally
      {
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.1.3
    * The “controllablePolicy” attribute of the Object-Type definition specified by the 
    * cmis:objectTypeId property value is set to FALSE and at least one policy is provided.
    * @throws Exception
    */

   public void testCreateDocument_ConstraintException5() throws Exception
   {
      System.out.print("Running testCreateDocument_ConstraintException5....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);

      Map<String, PropertyDefinition<?>> kinoPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
      org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefName2 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
            CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
            "doc1", true, null, null);
      org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefObjectTypeId2 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
            CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
            false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);
      kinoPropertyDefinitions.put(CmisConstants.NAME, kinoPropDefName2);
      kinoPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, kinoPropDefObjectTypeId2);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(kinoPropDefName2.getId(), kinoPropDefName2.getQueryName(),
         kinoPropDefName2.getLocalName(), kinoPropDefName2.getDisplayName(), "doc1"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(kinoPropDefObjectTypeId2.getId(),
         kinoPropDefObjectTypeId2.getQueryName(), kinoPropDefObjectTypeId2.getLocalName(), kinoPropDefObjectTypeId2
            .getDisplayName(), "cmis:kino"));

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document", "cmis:kino",
            "cmis:kino", true, false, true, true, false, false, false, false, null, null,
            ContentStreamAllowed.NOT_ALLOWED, kinoPropertyDefinitions);
      String typeID = getStorage().addType(newType);

      PolicyData policy = createPolicy(testroot, "policy1");

      ArrayList<String> policies = new ArrayList<String>();
      policies.add(policy.getObjectId());
      try
      {
         String docId =
            getConnection().createDocument(testroot.getObjectId(), properties, cs, null, null, policies,
               VersioningState.MAJOR);
         doFail();
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
      finally
      {
         clear(testroot.getObjectId());
         getStorage().deleteObject(policy, true);
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.1.3
    * The “controllableACL” attribute of the Object-Type definition specified by the cmis:objectTypeId 
    * property value is set to FALSE and at least one ACE is provided.
    * @throws Exception
    */

   public void testCreateDocument_ConstraintException6() throws Exception
   {
      System.out.print("Running testCreateDocument_ConstraintException6....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);

      Map<String, PropertyDefinition<?>> kinoPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
      org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefName2 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
            CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
            "doc1", true, null, null);
      org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefObjectTypeId2 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
            CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
            false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);
      kinoPropertyDefinitions.put(CmisConstants.NAME, kinoPropDefName2);
      kinoPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, kinoPropDefObjectTypeId2);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(kinoPropDefName2.getId(), kinoPropDefName2.getQueryName(),
         kinoPropDefName2.getLocalName(), kinoPropDefName2.getDisplayName(), "doc1"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(kinoPropDefObjectTypeId2.getId(),
         kinoPropDefObjectTypeId2.getQueryName(), kinoPropDefObjectTypeId2.getLocalName(), kinoPropDefObjectTypeId2
            .getDisplayName(), "cmis:kino"));

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document", "cmis:kino",
            "cmis:kino", true, false, true, true, false, false, false, false, null, null,
            ContentStreamAllowed.NOT_ALLOWED, kinoPropertyDefinitions);
      String typeID = getStorage().addType(newType);

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
         doFail();
      }
      catch (ConstraintException ex)
      {
         pass();
      }
      catch (Exception e)
      {
         //e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.1.3
    * At least one of the permissions is used in an ACE provided which is not supported by the repository.
    * @throws Exception
    */

   public void testCreateDocument_ConstraintException7() throws Exception
   {
      System.out.print("Running testCreateDocument_ConstraintException6....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);

      Map<String, PropertyDefinition<?>> kinoPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
      org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefName2 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
            CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
            "doc1", true, null, null);
      org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefObjectTypeId2 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
            CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
            false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);
      kinoPropertyDefinitions.put(CmisConstants.NAME, kinoPropDefName2);
      kinoPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, kinoPropDefObjectTypeId2);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(kinoPropDefName2.getId(), kinoPropDefName2.getQueryName(),
         kinoPropDefName2.getLocalName(), kinoPropDefName2.getDisplayName(), "doc1"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(kinoPropDefObjectTypeId2.getId(),
         kinoPropDefObjectTypeId2.getQueryName(), kinoPropDefObjectTypeId2.getLocalName(), kinoPropDefObjectTypeId2
            .getDisplayName(), "cmis:kino"));

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document", "cmis:kino",
            "cmis:kino", true, false, true, true, false, false, true, false, null, null,
            ContentStreamAllowed.NOT_ALLOWED, kinoPropertyDefinitions);
      String typeID = getStorage().addType(newType);

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
         doFail();
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
      finally
      {
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.2
    * Creates a document object as a copy of the given source document in the (optionally) specified location.
    * @throws Exception
    */
   public void testCreateDocumentFromSource_Simple() throws Exception
   {
      System.out.print("Running testCreateDocumentFromSource_Simple....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      byte[] before = new byte[15];
      before = "1234567890aBcDE".getBytes();
      ContentStream cs = new BaseContentStream(before, null, new MimeType("text", "plain"));
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      try
      {
         String docId =
            getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(),
               getPropsMap("cmis:document", "doc2"), null, null, null, VersioningState.MAJOR);
         ContentStream c = getStorage().getObjectById(docId).getContentStream(null);
         assertEquals(cs.getMediaType(), c.getMediaType());

         byte[] after = new byte[15];
         c.getStream().read(after);
         assertArrayEquals(before, after);
         assertEquals(testroot.getName(), getStorage().getObjectById(docId).getParent().getName());
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }
   }

   /**
    * 2.2.4.2.1
    * The property values that MUST be applied to the Object.  
    * This list of properties SHOULD only contain properties whose values differ from the source document.
    * @throws Exception
    */
   public void testCreateDocumentFromSource_Properties() throws Exception
   {
      System.out.print("Running testCreateDocumentFromSource_Properties....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      byte[] before = new byte[15];
      before = "1234567890aBcDE".getBytes();
      ContentStream cs = new BaseContentStream(before, null, new MimeType("text", "plain"));
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      try
      {
         String docId =
            getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(),
               getPropsMap("cmis:document", "doc2"), null, null, null, VersioningState.MAJOR);
         assertEquals("doc2", getStorage().getObjectById(docId).getProperty(CmisConstants.NAME).getValues().get(0));
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }
   }

   /**
    * 2.2.4.2.1
    * A list of policy IDs that MUST be applied to the newly-created Document object. 
    * @throws Exception
    */
   public void testCreateDocumentFromSource_ApplyPolicy() throws Exception
   {
      System.out.print("Running testCreateDocumentFromSource_ApplyPolicy....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      byte[] before = new byte[15];
      before = "1234567890aBcDE".getBytes();
      ContentStream cs = new BaseContentStream(before, null, new MimeType("text", "plain"));
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);

      PolicyData policy = createPolicy(testroot, "policy1");

      ArrayList<String> policies = new ArrayList<String>();
      policies.add(policy.getObjectId());
      try
      {
         String docId =
            getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(),
               getPropsMap("cmis:document", "doc2"), null, null, policies, VersioningState.MAJOR);
         ObjectData res = getStorage().getObjectById(docId);
         assertEquals(1, res.getPolicies().size());
         Iterator<PolicyData> it = res.getPolicies().iterator();
         while (it.hasNext())
         {
            PolicyData one = it.next();
            assertEquals("policy1", one.getName());
            assertEquals("testPolicyText", one.getPolicyText());
            res.removePolicy(one);
         }
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         getStorage().deleteObject(policy, true);
         clear(testroot.getObjectId());
      }
   }

   /**
    * 2.2.4.2.1
    *  A list of ACEs that MUST be added to the newly-created Document object, 
    *  either using the ACL from folderId if specified, or being applied if no folderId is specified.  
    * @throws Exception
    */

   public void testCreateDocumentFromSource_addACL() throws Exception
   {
      System.out.print("Running testCreateDocumentFromSource_ApplyPolicy....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      byte[] before = new byte[15];
      before = "1234567890aBcDE".getBytes();
      ContentStream cs = new BaseContentStream(before, null, new MimeType("text", "plain"));
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);

      AccessControlEntry acl = new AccessControlEntry();
      acl.setPrincipal("Makis");
      acl.getPermissions().add("cmis:read");
      ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
      addACL.add(acl);
      try
      {
         String docId =
            getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(),
               getPropsMap("cmis:document", "doc2"), addACL, null, null, VersioningState.MAJOR);
         ObjectData res = getStorage().getObjectById(docId);
         for (AccessControlEntry one : res.getACL(false))
         {
            if (one.getPrincipal().equalsIgnoreCase("Makis"))
            {
               assertEquals(1, one.getPermissions().size());
               assertTrue(one.getPermissions().contains("cmis:read"));
            }
         }
         pass();
      }
      catch (Exception e)
      {
         //e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }

   }

   /**
    * 2.2.4.2.3
    *  If the repository detects a violation with the given cmis:name property value, 
    *  the repository MAY throw this exception or chose a name which does not conflict.
    * @throws Exception
    */
   public void testCreateDocumentFromSource_NameConstraintViolationException() throws Exception
   {
      System.out.print("Running testCreateDocumentFromSource_NameConstraintViolationException....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      byte[] before = new byte[15];
      before = "1234567890aBcDE".getBytes();
      ContentStream cs = new BaseContentStream(before, null, new MimeType("text", "plain"));
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      try
      {
         String docId =
            getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(),
               getPropsMap("cmis:document", "doc1"), null, null, null, VersioningState.MAJOR);
         ObjectData res = getStorage().getObjectById(docId);
         assertNotSame("doc1", res.getName());
      }
      catch (NameConstraintViolationException ex)
      {
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }
   }

   /**
    * 2.2.4.2.3
    * •  constraint: The Repository MUST throw this exception if  the sourceId is not an Object whose baseType is “Document”.
    * @throws Exception
    */

   public void testCreateDocumentFromSource_ConstraintException1() throws Exception
   {
      System.out.print("Running testCreateDocumentFromSource_ConstraintException1....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);

      FolderData test = createFolder(testroot, "123");

      try
      {
         String docId =
            getConnection().createDocumentFromSource(test.getObjectId(), testroot.getObjectId(),
               getPropsMap("cmis:document", "1"), null, null, null, VersioningState.MAJOR);
         doFail();
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
      finally
      {
         clear(testroot.getObjectId());
      }

   }

   /**
    * 2.2.4.2.3
    * The source document’s cmis:objectTypeId property value is NOT in the list of AllowedChildObjectTypeIds 
    * of the parent-folder specified by folderId.
    * @throws Exception
    */

   public void testCreateDocumentFromSource_ConstraintException2() throws Exception
   {
      System.out.print("Running testCreateDocumentFromSource_ConstraintException2....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);

      //Creating type from cmis:folder with overriden  ALLOWED_CHILD_OBJECT_TYPE_IDS;

      Map<String, PropertyDefinition<?>> folderPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

      org.xcmis.spi.model.PropertyDefinition<?> fPropDefName2 =
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

      folderPropertyDefinitions.put(CmisConstants.NAME, fPropDefName2);
      folderPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);
      folderPropertyDefinitions.put(CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, fPropDefAllowedChild);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();

      properties.put(CmisConstants.NAME, new StringProperty(fPropDefName2.getId(), fPropDefName2.getQueryName(),
         fPropDefName2.getLocalName(), fPropDefName2.getDisplayName(), "myfolder"));

      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
         .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino"));

      properties.put(CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, new IdProperty(fPropDefObjectTypeId.getId(),
         fPropDefObjectTypeId.getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId
            .getDisplayName(), "cmis:folder"));

      TypeDefinition newType =
         new TypeDefinition("cmis:myfolder", BaseType.FOLDER, "cmis:myfolder", "cmis:myfolder", "", "cmis:folder",
            "cmis:myfolder", "cmis:myfolder", true, false, true, true, false, false, false, false, null, null,
            ContentStreamAllowed.NOT_ALLOWED, folderPropertyDefinitions);
      String typeID = getStorage().addType(newType);

      FolderData myfolder = getStorage().createFolder(testroot, newType, properties, null, null);

      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc"), cs, null,
            null, VersioningState.MAJOR);

      try
      {
         String docId =
            getConnection().createDocumentFromSource(doc1.getObjectId(), myfolder.getObjectId(),
               getPropsMap("cmis:document", "1"), null, null, null, VersioningState.MAJOR);
         doFail();
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
      finally
      {
         getStorage().deleteObject(myfolder, true);
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);

      }
   }

   /**
    * 2.2.4.2.3
    * The “versionable” attribute of the Object-Type definition specified by the cmis:objectTypeId property value is set to FALSE 
    * and a value for the versioningState input parameter is provided that is something other than “none”.
    * @throws Exception
    */

   public void testCreateDocumentFromSource_ConstraintException3() throws Exception
   {
      System.out.print("Running testCreateDocumentFromSource_ConstraintException3....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);

      //Creating type from cmis:folder with overriden  ALLOWED_CHILD_OBJECT_TYPE_IDS;

      Map<String, PropertyDefinition<?>> kinoPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
      org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefName2 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
            CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
            "doc1", true, null, null);
      org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefObjectTypeId2 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
            CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
            false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);
      kinoPropertyDefinitions.put(CmisConstants.NAME, kinoPropDefName2);
      kinoPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, kinoPropDefObjectTypeId2);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(kinoPropDefName2.getId(), kinoPropDefName2.getQueryName(),
         kinoPropDefName2.getLocalName(), kinoPropDefName2.getDisplayName(), "doc1"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(kinoPropDefObjectTypeId2.getId(),
         kinoPropDefObjectTypeId2.getQueryName(), kinoPropDefObjectTypeId2.getLocalName(), kinoPropDefObjectTypeId2
            .getDisplayName(), "cmis:kino"));

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document", "cmis:kino",
            "cmis:kino", true, false, true, true, false, false, false, false, null, null,
            ContentStreamAllowed.NOT_ALLOWED, kinoPropertyDefinitions);
      String typeID = getStorage().addType(newType);

      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc"), cs, null,
            null, VersioningState.MAJOR);
      try
      {
         String docId =
            getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(), properties, null,
               null, null, VersioningState.MAJOR);
         doFail();
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
      finally
      {
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.2.3
    * The “versionable” attribute of the Object-Type definition specified by the cmis:objectTypeId property value is set to TRUE and 
    * the value for the versioningState input parameter is provided that is “none”.
    * @throws Exception
    */

   public void testCreateDocumentFromSource_ConstraintException4() throws Exception
   {
      System.out.print("Running testCreateDocumentFromSource_ConstraintException4....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);

      //Creating type from cmis:folder with overriden  ALLOWED_CHILD_OBJECT_TYPE_IDS;

      Map<String, PropertyDefinition<?>> kinoPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
      org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefName2 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
            CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
            "doc1", true, null, null);
      org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefObjectTypeId2 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
            CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
            false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);
      kinoPropertyDefinitions.put(CmisConstants.NAME, kinoPropDefName2);
      kinoPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, kinoPropDefObjectTypeId2);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(kinoPropDefName2.getId(), kinoPropDefName2.getQueryName(),
         kinoPropDefName2.getLocalName(), kinoPropDefName2.getDisplayName(), "doc1"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(kinoPropDefObjectTypeId2.getId(),
         kinoPropDefObjectTypeId2.getQueryName(), kinoPropDefObjectTypeId2.getLocalName(), kinoPropDefObjectTypeId2
            .getDisplayName(), "cmis:kino"));

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document", "cmis:kino",
            "cmis:kino", true, false, true, true, false, false, false, true, null, null, ContentStreamAllowed.ALLOWED,
            kinoPropertyDefinitions);
      String typeID = getStorage().addType(newType);

      DocumentData doc1 =
         getStorage().createDocument(testroot, newType, properties, cs, null, null, VersioningState.MAJOR);

      properties.put(CmisConstants.NAME, new StringProperty(kinoPropDefName2.getId(), kinoPropDefName2.getQueryName(),
         kinoPropDefName2.getLocalName(), kinoPropDefName2.getDisplayName(), "doc2"));

      try
      {
         String docId =
            getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(), properties, null,
               null, null, VersioningState.NONE);
         doFail();
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
      finally
      {
         clear(testroot.getObjectId());
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

   public void testCreateDocumentFromSource_ConstraintException5() throws Exception
   {
      System.out.print("Running testCreateDocumentFromSource_ConstraintException5...");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);

      Map<String, PropertyDefinition<?>> kinoPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
      org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefName2 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
            CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
            "doc1", true, null, null);
      org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefObjectTypeId2 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
            CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
            false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);
      kinoPropertyDefinitions.put(CmisConstants.NAME, kinoPropDefName2);
      kinoPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, kinoPropDefObjectTypeId2);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(kinoPropDefName2.getId(), kinoPropDefName2.getQueryName(),
         kinoPropDefName2.getLocalName(), kinoPropDefName2.getDisplayName(), "doc1"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(kinoPropDefObjectTypeId2.getId(),
         kinoPropDefObjectTypeId2.getQueryName(), kinoPropDefObjectTypeId2.getLocalName(), kinoPropDefObjectTypeId2
            .getDisplayName(), "cmis:kino"));

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document", "cmis:kino",
            "cmis:kino", true, false, true, true, false, false, false, true, null, null,
            ContentStreamAllowed.NOT_ALLOWED, kinoPropertyDefinitions);
      String typeID = getStorage().addType(newType);

      PolicyData policy = createPolicy(testroot, "policy1");

      ArrayList<String> policies = new ArrayList<String>();
      policies.add(policy.getObjectId());

      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc"), cs, null,
            null, VersioningState.MAJOR);
      try
      {
         String docId =
            getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(), properties, null,
               null, policies, VersioningState.NONE);
         doFail();
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
      finally
      {
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
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

   public void testCreateDocumentFromSource_ConstraintException6() throws Exception
   {
      System.out.print("Running testCreateDocumentFromSource_ConstraintException6...");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);

      Map<String, PropertyDefinition<?>> kinoPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
      org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefName2 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
            CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
            "doc1", true, null, null);
      org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefObjectTypeId2 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
            CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
            false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);
      kinoPropertyDefinitions.put(CmisConstants.NAME, kinoPropDefName2);
      kinoPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, kinoPropDefObjectTypeId2);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(kinoPropDefName2.getId(), kinoPropDefName2.getQueryName(),
         kinoPropDefName2.getLocalName(), kinoPropDefName2.getDisplayName(), "doc1"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(kinoPropDefObjectTypeId2.getId(),
         kinoPropDefObjectTypeId2.getQueryName(), kinoPropDefObjectTypeId2.getLocalName(), kinoPropDefObjectTypeId2
            .getDisplayName(), "cmis:kino"));

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document", "cmis:kino",
            "cmis:kino", true, false, true, true, false, false, false, false, null, null,
            ContentStreamAllowed.NOT_ALLOWED, kinoPropertyDefinitions);
      String typeID = getStorage().addType(newType);

      AccessControlEntry acl = new AccessControlEntry();
      acl.setPrincipal("Makis");
      acl.getPermissions().add("cmis:unknown");
      ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
      addACL.add(acl);

      DocumentData doc1 =
         getStorage().createDocument(testroot, newType, getPropsMap("cmis:document", "doc"), cs, null, null,
            VersioningState.MAJOR);
      try
      {
         String docId =
            getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(), properties, addACL,
               null, null, VersioningState.NONE);
         doFail();
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
      finally
      {
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.2.3
    * At least one of the permissions is used in an ACE provided which is not supported by the repository.
    * 
    * @throws Exception
    */

   public void testCreateDocumentFromSource_ConstraintException7() throws Exception
   {
      System.out.print("Running testCreateDocumentFromSource_ConstraintException7...");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);

      Map<String, PropertyDefinition<?>> kinoPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
      org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefName2 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
            CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
            "doc1", true, null, null);
      org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefObjectTypeId2 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
            CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
            false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);
      kinoPropertyDefinitions.put(CmisConstants.NAME, kinoPropDefName2);
      kinoPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, kinoPropDefObjectTypeId2);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(kinoPropDefName2.getId(), kinoPropDefName2.getQueryName(),
         kinoPropDefName2.getLocalName(), kinoPropDefName2.getDisplayName(), "doc1"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(kinoPropDefObjectTypeId2.getId(),
         kinoPropDefObjectTypeId2.getQueryName(), kinoPropDefObjectTypeId2.getLocalName(), kinoPropDefObjectTypeId2
            .getDisplayName(), "cmis:kino"));

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document", "cmis:kino",
            "cmis:kino", true, false, true, true, false, false, true, false, null, null,
            ContentStreamAllowed.NOT_ALLOWED, kinoPropertyDefinitions);
      String typeID = getStorage().addType(newType);

      AccessControlEntry acl = new AccessControlEntry();
      acl.setPrincipal("Makis");
      acl.getPermissions().add("cmis:unknown");
      ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
      addACL.add(acl);

      DocumentData doc1 =
         getStorage().createDocument(testroot, newType, getPropsMap("cmis:document", "doc"), cs, null, null,
            VersioningState.MAJOR);
      try
      {
         String docId =
            getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(), properties, addACL,
               null, null, VersioningState.NONE);
         doFail();
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
      finally
      {
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.3
    * Creates a folder object of the specified type in the specified location.
    * @throws Exception
    */
   public void testCreateFolder_Simple() throws Exception
   {
      System.out.print("Running testCreateFolder_Simple....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      try
      {
         String docId =
            getConnection().createFolder(testroot.getObjectId(), getPropsMap("cmis:folder", "f1"), null, null, null);
         ObjectData obj = getStorage().getObjectById(docId);
         assertEquals("cmis:folder", obj.getTypeId());
         assertEquals("/testroot/f1", ((FolderData)obj).getPath());
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }
   }

   /**
    * 2.2.4.3.1
    * A list of policy IDs that MUST be applied to the newly-created Folder object.
    * @throws Exception
    */
   public void testCreateFolder_ApplyPolicy() throws Exception
   {
      System.out.print("Running testCreateFolder_ApplyPolicy....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);

      PolicyData policy = createPolicy(testroot, "policy1");

      ArrayList<String> policies = new ArrayList<String>();
      policies.add(policy.getObjectId());
      String docId = "";
      try
      {
         docId =
            getConnection()
               .createFolder(testroot.getObjectId(), getPropsMap("cmis:folder", "f1"), null, null, policies);
         ObjectData res = getStorage().getObjectById(docId);
         assertEquals(1, res.getPolicies().size());
         Iterator<PolicyData> it = res.getPolicies().iterator();
         while (it.hasNext())
         {
            PolicyData one = it.next();
            assertEquals("policy1", one.getName());
            assertEquals("testPolicyText", one.getPolicyText());
         }
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         getStorage().deleteObject(getStorage().getObjectById(docId), true);
         getStorage().deleteObject(policy, true);
         clear(testroot.getObjectId());
      }
   }

   /**
    * 2.2.4.3.1
    * A list of ACEs that MUST be added to the newly-created Folder object.
    * @throws Exception
    */
   /*
   public void testCreateFolder_AddACL() throws Exception
   {
      System.out.print("Running testCreateFolder_AddACL....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      
      AccessControlEntry acl = new AccessControlEntry();
      acl.setPrincipal("Makis");
      acl.getPermissions().add("cmis:read");
      ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
      addACL.add(acl);
      
      String docId = "";
      try
      {
          docId =
            getConnection().createFolder(testroot.getObjectId(), getPropsMap("cmis:folder", "f1"), null, null, policies);
         ObjectData res = getStorage().getObjectById(docId);
         for (AccessControlEntry one : res.getACL(false))
         {
            if (one.getPrincipal().equalsIgnoreCase("Makis"))
            assertEquals(1, one.getPermissions().size());
            assertTrue(one.getPermissions().contains("cmis:read"));
         }
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         getStorage().deleteObject(getStorage().getObjectById(docId), true);
         clear(testroot.getObjectId());
      }
   }
   */

   /**
    * 2.2.4.3
    * Creates a folder object of the specified type in the specified location.
    * @throws Exception
    */
   public void testCreateFolder_NameConstraintViolationException() throws Exception
   {
      System.out.print("Running testCreateFolder_Simple....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);

      getStorage().createFolder(testroot, folderTypeDefinition, getPropsMap("cmis:folder", "f1"), null, null);
      try
      {
         String docId =
            getConnection().createFolder(testroot.getObjectId(), getPropsMap("cmis:folder", "f1"), null, null, null);
         ObjectData res = getStorage().getObjectById(docId);
         assertNotSame("f1", res.getName());
      }
      catch (NameConstraintViolationException ex)
      {
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }
   }

   /**
    * 2.2.4.3.3
    * The Repository MUST throw this exception if the cmis:objectTypeId property 
    * value is not an Object-Type whose baseType is “Folder”.
    * @throws Exception
    */

   public void testCreateFolder_ConstraintException1() throws Exception
   {
      System.out.print("Running testCreateFolder_ConstraintException1....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);

      Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

      org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
            CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
            "f1", true, null, null);

      org.xcmis.spi.model.PropertyDefinition<?> fPropDefObjectTypeId =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
            CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
            false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

      fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
      fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
         fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "f1"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
         .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino"));

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.POLICY, "cmis:kino", "cmis:kino", "", "cmis:policy", "cmis:kino",
            "cmis:kino", true, false, true, true, false, false, false, true, null, null,
            ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
      String typeID = getStorage().addType(newType);

      try
      {
         String docId = getConnection().createFolder(testroot.getObjectId(), properties, null, null, null);
         doFail();
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
      finally
      {
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.3.3
    * The cmis:objectTypeId property value is NOT in the list of 
    * AllowedChildObjectTypeIds of the parent-folder specified by folderId. 
    * @throws Exception
    */
   public void testCreateFolder_ConstraintException2() throws Exception
   {
      System.out.print("Running testCreateFolder_ConstraintException2....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);

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

      fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
      fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);
      fPropertyDefinitions.put(CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, fPropDefAllowedChilds);

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
      String typeID = getStorage().addType(newType);

      FolderData f1 = getStorage().createFolder(testroot, newType, properties, null, null);

      try
      {
         String docId =
            getConnection().createFolder(f1.getObjectId(), getPropsMap("cmis:folder", "f2"), null, null, null);
         doFail();
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
      finally
      {
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.3.3
    * The “controllablePolicy” attribute of the Object-Type definition specified by the 
    * cmis:objectTypeId property value is set to FALSE and at least one policy is provided.
    * @throws Exception
    */

   public void testCreateFolder_ConstraintException3() throws Exception
   {
      System.out.print("Running testCreateFolder_ConstraintException3....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);

      Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

      org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
            CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
            "f1", true, null, null);

      org.xcmis.spi.model.PropertyDefinition<?> fPropDefObjectTypeId =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
            CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
            false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

      fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
      fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
         fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "f1"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
         .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino"));

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.FOLDER, "cmis:kino", "cmis:kino", "", "cmis:folder", "cmis:kino",
            "cmis:kino", true, false, true, true, false, false, false, true, null, null,
            ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
      String typeID = getStorage().addType(newType);

      PolicyData policy = createPolicy(testroot, "policy1");

      ArrayList<String> policies = new ArrayList<String>();
      policies.add(policy.getObjectId());

      try
      {
         String docId = getConnection().createFolder(testroot.getObjectId(), properties, null, null, policies);
         doFail();
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
      finally
      {
         clear(testroot.getObjectId());
         getStorage().deleteObject(policy, true);
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.3.3
    * The “controllableACL” attribute of the Object-Type definition specified 
    * by the cmis:objectTypeId property value is set to FALSE and at least one ACE is provided.
    * @throws Exception
    */

   public void testCreateFolder_ConstraintException4() throws Exception
   {
      System.out.print("Running testCreateFolder_ConstraintException4....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);

      Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

      org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
            CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
            "f1", true, null, null);

      org.xcmis.spi.model.PropertyDefinition<?> fPropDefObjectTypeId =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
            CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
            false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

      fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
      fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
         fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "f1"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
         .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino"));

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.FOLDER, "cmis:kino", "cmis:kino", "", "cmis:folder", "cmis:kino",
            "cmis:kino", true, false, true, true, false, false, false, true, null, null,
            ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
      String typeID = getStorage().addType(newType);

      AccessControlEntry acl = new AccessControlEntry();
      acl.setPrincipal("Makis");
      acl.getPermissions().add("cmis:read");
      ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
      addACL.add(acl);

      try
      {
         String docId = getConnection().createFolder(testroot.getObjectId(), properties, addACL, null, null);
         doFail();
      }
      catch (ConstraintException ex)
      {
         pass();
      }
      catch (Exception e)
      {
         //e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.3.3
    * At least one of the permissions is used in an ACE provided which is not supported by the repository.
    * @throws Exception
    */

   public void testCreateFolder_ConstraintException5() throws Exception
   {
      System.out.print("Running testCreateFolder_ConstraintException5....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);

      Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

      org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
            CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
            "f1", true, null, null);

      org.xcmis.spi.model.PropertyDefinition<?> fPropDefObjectTypeId =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
            CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
            false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

      fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
      fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
         fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "f1"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
         .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino"));

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.FOLDER, "cmis:kino", "cmis:kino", "", "cmis:folder", "cmis:kino",
            "cmis:kino", true, false, true, true, false, false, true, true, null, null,
            ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
      String typeID = getStorage().addType(newType);

      AccessControlEntry acl = new AccessControlEntry();
      acl.setPrincipal("Makis");
      acl.getPermissions().add("cmis:unknown");
      ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
      addACL.add(acl);

      try
      {
         String docId = getConnection().createFolder(testroot.getObjectId(), properties, addACL, null, null);
         doFail();
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
      finally
      {
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.4
    * Creates a relationship object of the specified type.
    * @throws Exception
    */
   public void testCreateRelationship_Simple() throws Exception
   {
      System.out.print("Running testCreateRelationship_Simple....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      DocumentData doc2 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc2"), cs, null,
            null, VersioningState.MAJOR);

      Map<String, Property<?>> props = getPropsMap("cmis:relationship", "rel1");

      org.xcmis.spi.model.PropertyDefinition<?> fPropDefSource =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.SOURCE_ID, PropertyType.ID,
            CmisConstants.SOURCE_ID, CmisConstants.SOURCE_ID, null, CmisConstants.SOURCE_ID, true, false, false, false,
            false, Updatability.READONLY, "SourceId", true, null, null);

      org.xcmis.spi.model.PropertyDefinition<?> fPropDefTarget =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.TARGET_ID, PropertyType.ID,
            CmisConstants.TARGET_ID, CmisConstants.TARGET_ID, null, CmisConstants.TARGET_ID, false, false, false,
            false, false, Updatability.READONLY, "TargetId", null, null, null);

      props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
         fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc1.getObjectId()));
      props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
         fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));
      ObjectData obj = null;
      try
      {
         String docId = getConnection().createRelationship(props, null, null, null);
         obj = getStorage().getObjectById(docId);
         assertEquals("cmis:relationship", obj.getTypeId());
         assertEquals(doc1.getObjectId(), ((RelationshipData)obj).getSourceId());
         assertEquals(doc2.getObjectId(), ((RelationshipData)obj).getTargetId());
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         clear(testroot.getObjectId());
      }
   }

   /**
    * 2.2.4.4.1
    * A list of policy IDs that MUST be applied to the newly-created Replationship object.
    * @throws Exception
    */

   public void testCreateRelationship_ApplyPolicy() throws Exception
   {
      System.out.print("Running testCreateRelationship_ApplyPolicy....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      DocumentData doc2 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc2"), cs, null,
            null, VersioningState.MAJOR);

      Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
      Map<String, Property<?>> props = new HashMap<String, Property<?>>();

      org.xcmis.spi.model.PropertyDefinition<?> fPropDefSource =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.SOURCE_ID, PropertyType.ID,
            CmisConstants.SOURCE_ID, CmisConstants.SOURCE_ID, null, CmisConstants.SOURCE_ID, true, false, false, false,
            false, Updatability.READONLY, "SourceId", true, null, null);

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

      props.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(), fPropDefName
         .getLocalName(), fPropDefName.getDisplayName(), "rel1"));

      props.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
         .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino2"));

      props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
         fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc1.getObjectId()));
      props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
         fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));

      fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
      fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);

      TypeDefinition newType =
         new TypeDefinition("cmis:kino2", BaseType.RELATIONSHIP, "cmis:kino2", "cmis:kino2", "", "cmis:relationship",
            "cmis:kino2", "cmis:kino2", true, false, true, true, false, true, false, false, null, null,
            ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
      String typeID = getStorage().addType(newType);

      ObjectData obj = null;
      PolicyData policy = createPolicy(testroot, "policy1");
      ArrayList<String> policies = new ArrayList<String>();
      policies.add(policy.getObjectId());
      try
      {
         String docId = getConnection().createRelationship(props, null, null, policies);
         obj = getStorage().getObjectById(docId);
         assertEquals(1, obj.getPolicies().size());
         Iterator<PolicyData> it = obj.getPolicies().iterator();
         while (it.hasNext())
         {
            PolicyData one = it.next();
            assertEquals("policy1", one.getName());
            assertEquals("testPolicyText", one.getPolicyText());
         }
         pass();
      }
      catch (Exception e)
      {
         //e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         getStorage().deleteObject(policy, true);
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.4.1
    * A list of ACEs that MUST be added to the newly-created Relationship object, either using the 
    * ACL from folderId if specified, or being applied if no folderId is specified. 
    * @throws Exception
    */
   public void testCreateRelationship_AddACL() throws Exception
   {
      System.out.print("Running testCreateRelationship_AddACL....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      DocumentData doc2 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc2"), cs, null,
            null, VersioningState.MAJOR);

      Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
      Map<String, Property<?>> props = new HashMap<String, Property<?>>();

      org.xcmis.spi.model.PropertyDefinition<?> fPropDefSource =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.SOURCE_ID, PropertyType.ID,
            CmisConstants.SOURCE_ID, CmisConstants.SOURCE_ID, null, CmisConstants.SOURCE_ID, true, false, false, false,
            false, Updatability.READONLY, "SourceId", true, null, null);

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

      props.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(), fPropDefName
         .getLocalName(), fPropDefName.getDisplayName(), "rel1"));

      props.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
         .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino"));

      props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
         fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc1.getObjectId()));
      props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
         fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));

      fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
      fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.RELATIONSHIP, "cmis:kino", "cmis:kino", "", "cmis:relationship",
            "cmis:kino", "cmis:kino", true, false, true, true, false, true, true, false, null, null,
            ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
      String typeID = getStorage().addType(newType);

      AccessControlEntry acl = new AccessControlEntry();
      acl.setPrincipal("Makis");
      acl.getPermissions().add("cmis:read");
      ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
      addACL.add(acl);

      ObjectData obj = null;
      try
      {
         String docId = getConnection().createRelationship(props, addACL, null, null);
         obj = getStorage().getObjectById(docId);
         for (AccessControlEntry one : obj.getACL(false))
         {
            if (one.getPrincipal().equalsIgnoreCase("Makis"))
            {
               assertEquals(1, one.getPermissions().size());
               assertTrue(one.getPermissions().contains("cmis:read"));
            }
         }
         pass();
      }
      catch (Exception e)
      {
         //e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.4.3
    * If the repository detects a violation with the given cmis:name property value, the repository MAY 
    * throw this exception or chose a name which does not conflict.
    * @throws Exception
    */

   public void testCreateRelationship_NameConstraintViolationException() throws Exception
   {
      System.out.print("Running testCreateRelationship_NameConstraintViolationException....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      DocumentData doc2 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc2"), cs, null,
            null, VersioningState.MAJOR);

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
            CmisConstants.SOURCE_ID, CmisConstants.SOURCE_ID, null, CmisConstants.SOURCE_ID, true, false, false, false,
            false, Updatability.READONLY, "SourceId", true, null, null);

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

      props.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(), fPropDefName
         .getLocalName(), fPropDefName.getDisplayName(), "rel1"));

      props.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
         .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(),
         "cmis:relationship"));

      props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
         fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc1.getObjectId()));
      props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
         fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));

      ObjectData obj = null;
      try
      {
         String docId = getConnection().createRelationship(props, null, null, null);
         obj = getStorage().getObjectById(docId);
         assertFalse(obj.getName().equals("doc1"));
         pass();
      }
      catch (NameConstraintViolationException ex)
      {

         pass();
      }
      catch (Exception e)
      {
         //e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         clear(testroot.getObjectId());
      }
   }

   /**
    * 2.2.4.4.3
    * The cmis:objectTypeId property value is not an Object-Type whose baseType is “Relationship”.
    * @throws Exception
    */
   public void testCreateRelationship_ConstraintException1() throws Exception
   {
      System.out.print("Running testCreateRelationship_ConstraintException1....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      DocumentData doc2 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc2"), cs, null,
            null, VersioningState.MAJOR);

      Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
      Map<String, Property<?>> props = new HashMap<String, Property<?>>();

      org.xcmis.spi.model.PropertyDefinition<?> fPropDefSource =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.SOURCE_ID, PropertyType.ID,
            CmisConstants.SOURCE_ID, CmisConstants.SOURCE_ID, null, CmisConstants.SOURCE_ID, true, false, false, false,
            false, Updatability.READONLY, "SourceId", true, null, null);

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

      props.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(), fPropDefName
         .getLocalName(), fPropDefName.getDisplayName(), "rel1"));

      props.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
         .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:my"));

      props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
         fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc1.getObjectId()));
      props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
         fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));

      fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
      fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);

      TypeDefinition newType =
         new TypeDefinition("cmis:my", BaseType.FOLDER, "cmis:my", "cmis:my", "", "cmis:folder", "cmis:my", "cmis:my",
            true, false, true, true, false, true, true, false, null, null, ContentStreamAllowed.NOT_ALLOWED,
            fPropertyDefinitions);
      String typeID = getStorage().addType(newType);

      ObjectData obj = null;
      try
      {
         String docId = getConnection().createRelationship(props, null, null, null);
         obj = getStorage().getObjectById(docId);
         doFail();
      }
      catch (ConstraintException ex)
      {
         pass();
      }
      catch (Exception e)
      {
         //e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         clear(testroot.getObjectId());
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
   public void testCreateRelationship_ConstraintException2() throws Exception
   {
      System.out.print("Running testCreateRelationship_ConstraintException2....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      DocumentData doc2 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc2"), cs, null,
            null, VersioningState.MAJOR);

      Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
      Map<String, Property<?>> props = new HashMap<String, Property<?>>();

      org.xcmis.spi.model.PropertyDefinition<?> fPropDefSource =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.SOURCE_ID, PropertyType.ID,
            CmisConstants.SOURCE_ID, CmisConstants.SOURCE_ID, null, CmisConstants.SOURCE_ID, true, false, false, false,
            false, Updatability.READONLY, "SourceId", true, null, null);

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

      props.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(), fPropDefName
         .getLocalName(), fPropDefName.getDisplayName(), "rel1"));

      props.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
         .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:my"));

      props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
         fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc1.getObjectId()));
      props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
         fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));

      fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
      fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);
      String[] allowed = {"cmis:folder"};

      TypeDefinition newType =
         new TypeDefinition("cmis:my", BaseType.RELATIONSHIP, "cmis:my", "cmis:my", "", "cmis:relationship", "cmis:my",
            "cmis:my", true, false, true, true, false, true, true, false, allowed, allowed,
            ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
      String typeID = getStorage().addType(newType);

      ObjectData obj = null;
      try
      {
         String docId = getConnection().createRelationship(props, null, null, null);
         obj = getStorage().getObjectById(docId);
         doFail();
      }
      catch (ConstraintException ex)
      {
         pass();
      }
      catch (Exception e)
      {
         //e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.4.3
    * The “controllablePolicy” attribute of the Object-Type definition specified by the 
    * cmis:objectTypeId property value is set to FALSE and at least one policy is provided.
    * @throws Exception
    */
   public void testCreateRelationship_ConstraintException3() throws Exception
   {
      System.out.print("Running testCreateRelationship_ConstraintException3....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      DocumentData doc2 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc2"), cs, null,
            null, VersioningState.MAJOR);

      Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
      Map<String, Property<?>> props = new HashMap<String, Property<?>>();

      org.xcmis.spi.model.PropertyDefinition<?> fPropDefSource =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.SOURCE_ID, PropertyType.ID,
            CmisConstants.SOURCE_ID, CmisConstants.SOURCE_ID, null, CmisConstants.SOURCE_ID, true, false, false, false,
            false, Updatability.READONLY, "SourceId", true, null, null);

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

      props.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(), fPropDefName
         .getLocalName(), fPropDefName.getDisplayName(), "rel1"));

      props.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
         .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino2"));

      props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
         fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc1.getObjectId()));
      props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
         fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));

      fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
      fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);

      TypeDefinition newType =
         new TypeDefinition("cmis:kino2", BaseType.RELATIONSHIP, "cmis:kino2", "cmis:kino2", "", "cmis:relationship",
            "cmis:kino2", "cmis:kino2", true, false, true, true, false, false, false, false, null, null,
            ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
      String typeID = getStorage().addType(newType);

      ObjectData obj = null;
      PolicyData policy = createPolicy(testroot, "policy1");
      ArrayList<String> policies = new ArrayList<String>();
      policies.add(policy.getObjectId());
      try
      {
         String docId = getConnection().createRelationship(props, null, null, policies);
         obj = getStorage().getObjectById(docId);
         doFail();
      }
      catch (ConstraintException xe)
      {
         //e.printStackTrace();
         pass();
      }
      catch (Exception e)
      {
         //e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         getStorage().deleteObject(policy, true);
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.4.3
    * The “controllableACL” attribute of the Object-Type definition specified by the 
    * cmis:objectTypeId property value is set to FALSE and at least one ACE is provided.
    * @throws Exception
    */
   public void testCreateRelationship_ConstraintException4() throws Exception
   {
      System.out.print("Running testCreateRelationship_ConstraintException4....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      DocumentData doc2 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc2"), cs, null,
            null, VersioningState.MAJOR);

      Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
      Map<String, Property<?>> props = new HashMap<String, Property<?>>();

      org.xcmis.spi.model.PropertyDefinition<?> fPropDefSource =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.SOURCE_ID, PropertyType.ID,
            CmisConstants.SOURCE_ID, CmisConstants.SOURCE_ID, null, CmisConstants.SOURCE_ID, true, false, false, false,
            false, Updatability.READONLY, "SourceId", true, null, null);

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

      props.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(), fPropDefName
         .getLocalName(), fPropDefName.getDisplayName(), "rel1"));

      props.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
         .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino"));

      props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
         fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc1.getObjectId()));
      props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
         fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));

      fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
      fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.RELATIONSHIP, "cmis:kino", "cmis:kino", "", "cmis:relationship",
            "cmis:kino", "cmis:kino", true, false, true, true, false, false, false, false, null, null,
            ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
      String typeID = getStorage().addType(newType);

      AccessControlEntry acl = new AccessControlEntry();
      acl.setPrincipal("Makis");
      acl.getPermissions().add("cmis:read");
      ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
      addACL.add(acl);

      ObjectData obj = null;
      try
      {
         String docId = getConnection().createRelationship(props, addACL, null, null);
         obj = getStorage().getObjectById(docId);
         doFail();
      }
      catch (ConstraintException e)
      {
         //e.printStackTrace();
         pass();
      }

      catch (Exception e)
      {
         //e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.4.3
    * At least one of the permissions is used in an ACE provided which is not supported by the repository. 
    * @throws Exception
    */
   public void testCreateRelationship_ConstraintException5() throws Exception
   {
      System.out.print("Running testCreateRelationship_ConstraintException5....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      DocumentData doc2 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc2"), cs, null,
            null, VersioningState.MAJOR);

      Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
      Map<String, Property<?>> props = new HashMap<String, Property<?>>();

      org.xcmis.spi.model.PropertyDefinition<?> fPropDefSource =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.SOURCE_ID, PropertyType.ID,
            CmisConstants.SOURCE_ID, CmisConstants.SOURCE_ID, null, CmisConstants.SOURCE_ID, true, false, false, false,
            false, Updatability.READONLY, "SourceId", true, null, null);

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

      props.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(), fPropDefName
         .getLocalName(), fPropDefName.getDisplayName(), "rel1"));

      props.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
         .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino"));

      props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
         fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc1.getObjectId()));
      props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
         fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));

      fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
      fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.RELATIONSHIP, "cmis:kino", "cmis:kino", "", "cmis:relationship",
            "cmis:kino", "cmis:kino", true, false, true, true, false, false, true, false, null, null,
            ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
      String typeID = getStorage().addType(newType);

      AccessControlEntry acl = new AccessControlEntry();
      acl.setPrincipal("Makis");
      acl.getPermissions().add("cmis:unknown");
      ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
      addACL.add(acl);

      ObjectData obj = null;
      try
      {
         String docId = getConnection().createRelationship(props, addACL, null, null);
         obj = getStorage().getObjectById(docId);
         doFail();
      }
      catch (ConstraintException e)
      {
         //e.printStackTrace();
         pass();
      }

      catch (Exception e)
      {
         //e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }
   
   
   /**
    * 2.2.4.5
    * Creates a policy object of the specified type.
    * @throws Exception
    */
   public void testCreatePolicy_Simple() throws Exception
   {
      System.out.print("Running testCreatePolicy_Simple....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);

      org.xcmis.spi.model.PropertyDefinition<?> def =
         PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);
      Map<String, Property<?>> properties = getPropsMap("cmis:policy", "policy");
      properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def.getId(), def.getQueryName(),
         def.getLocalName(), def.getDisplayName(), "testPolicyText"));
      
      
      ObjectData obj = null;
      try
      {
         String docId = getConnection().createPolicy(testroot.getObjectId(), properties, null, null, null);
         obj = getStorage().getObjectById(docId);
         assertEquals("cmis:policy", obj.getTypeId());
         assertEquals("testPolicyText", ((PolicyData)obj).getPolicyText());
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         clear(testroot.getObjectId());
      }
   }

   
   /**
    * 2.2.4.5.1
    * A list of policy IDs that MUST be applied to the newly-created Policy object. 
    * @throws Exception
    */
   public void testCreatePolicy_AddPolicy() throws Exception
   {
      System.out.print("Running testCreatePolicy_AddPolicy....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);

      org.xcmis.spi.model.PropertyDefinition<?> def =
         PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);
      Map<String, Property<?>> properties = getPropsMap("cmis:policy", "policy");
      properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def.getId(), def.getQueryName(),
         def.getLocalName(), def.getDisplayName(), "testPolicyText1"));
      
      
      PolicyData policy = createPolicy(testroot, "policy1");

      ArrayList<String> policies = new ArrayList<String>();
      policies.add(policy.getObjectId());
      
      ObjectData obj = null;
      try
      {
         String docId = getConnection().createPolicy(testroot.getObjectId(), properties, null, null, policies);
         obj = getStorage().getObjectById(docId);
         Iterator<PolicyData> it = obj.getPolicies().iterator();
         while (it.hasNext())
         {
            PolicyData one = it.next();
            assertEquals("policy1", one.getName());
            assertEquals("testPolicyText", one.getPolicyText());
         }
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {

         if (obj != null)
            getStorage().deleteObject(obj, true);
         getStorage().deleteObject(policy, true);
         clear(testroot.getObjectId());
      }
   }
   
   
   /**
    * 2.2.4.5.1
    * A list of ACEs that MUST be added to the newly-created Policy object. 
    * @throws Exception
    */
   public void testCreatePolicy_AddACL() throws Exception
   {
      System.out.print("Running testCreatePolicy_Simple....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);

      org.xcmis.spi.model.PropertyDefinition<?> def =
         PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);
      Map<String, Property<?>> properties = getPropsMap("cmis:policy", "policy");
      properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def.getId(), def.getQueryName(),
         def.getLocalName(), def.getDisplayName(), "testPolicyText"));
      
      
      AccessControlEntry acl = new AccessControlEntry();
      acl.setPrincipal("Makis");
      acl.getPermissions().add("cmis:read");
      ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
      addACL.add(acl);
      
      ObjectData obj = null;
      try
      {
         String docId = getConnection().createPolicy(doc1.getObjectId(), properties, addACL, null, null);
         obj = getStorage().getObjectById(docId);
         for (AccessControlEntry one : obj.getACL(false))
         {
            if (one.getPrincipal().equalsIgnoreCase("Makis"))
            {
               assertEquals(1, one.getPermissions().size());
               assertTrue(one.getPermissions().contains("cmis:read"));
            }
         }
         pass();
      }
      catch (Exception e)
      {
        // e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         clear(testroot.getObjectId());
      }
   }

   
   
   
   /**
    * 2.2.4.5.3
    * If the repository detects a violation with the given cmis:name property value, the repository MAY 
    * throw this exception or chose a name which does not conflict.
    * @throws Exception
    */
   public void testCreatePolicy_NameConstraintViolationException() throws Exception
   {
      System.out.print("Running testCreatePolicy_NameConstraintViolationException....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);

      org.xcmis.spi.model.PropertyDefinition<?> def =
         PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);
      Map<String, Property<?>> properties = getPropsMap("cmis:policy", "policy1");
      properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def.getId(), def.getQueryName(),
         def.getLocalName(), def.getDisplayName(), "testPolicyText1"));
      PolicyData policy = createPolicy(testroot, "policy1");
      ObjectData obj = null;
      try
      {
         String docId = getConnection().createPolicy(testroot.getObjectId(), properties, null, null, null);
         obj = getStorage().getObjectById(docId);
         assertNotSame("policy1", obj.getName());
         pass();
      }
      catch (NameConstraintViolationException ex) {
        pass(); 
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {

         if (obj != null)
            getStorage().deleteObject(obj, true);
         getStorage().deleteObject(policy, true);
         clear(testroot.getObjectId());
      }
   }
   
   
   
   /**
    * 2.2.4.3.3
    * The Repository MUST throw this exception if  to  The cmis:objectTypeId 
    * property value is not an Object-Type whose baseType is “Policy”.
    * @throws Exception
    */

   public void testCreatePolicy_ConstraintException1() throws Exception
   {
      System.out.print("Running testCreatePolicy_ConstraintException1....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);

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
      

      fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
      fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
         fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "policy1"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
         .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino"));
     properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def.getId(), def.getQueryName(),
         def.getLocalName(), def.getDisplayName(), "testPolicyText1"));

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.FOLDER, "cmis:kino", "cmis:kino", "", "cmis:folder", "cmis:kino",
            "cmis:kino", true, false, true, true, false, false, false, true, null, null,
            ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
      String typeID = getStorage().addType(newType);

      try
      {
         String docId = getConnection().createPolicy(doc1.getObjectId(), properties, null, null, null);
         doFail();
      }
      catch (ConstraintException ex)
      {
         pass();
      }
      catch (Exception e)
      {
         //e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }
   
   
   
   /**
    * 2.2.4.5.3
    * The cmis:objectTypeId property value is NOT in the list of AllowedChildObjectTypeIds 
    * of the parent-folder specified by folderId. 
    * @throws Exception
    */

   public void testCreatePolicy_ConstraintException2() throws Exception
   {
      System.out.print("Running testCreatePolicy_ConstraintException2....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      Map<String, Property<?>> folderprops =  getPropsMap("cmis:folder", "testroot");
      
      org.xcmis.spi.model.PropertyDefinition<?> def =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, PropertyType.ID,
            CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, null, CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, false,
            false, false, false, false, Updatability.READONLY, "allowed", null, null, null);
      folderprops.put(CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, new IdProperty(def.getId(), def.getQueryName(),
         def.getLocalName(), def.getDisplayName(), "cmis:document"));
      
      
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition,folderprops , null, null);

      Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

      org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
            CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
            "f1", true, null, null);

      org.xcmis.spi.model.PropertyDefinition<?> fPropDefObjectTypeId =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
            CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
            false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

      org.xcmis.spi.model.PropertyDefinition<?> def2=
         PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);
      

      fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
      fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
         fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "policy1"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
         .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino"));
     properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def2.getId(), def2.getQueryName(),
         def2.getLocalName(), def2.getDisplayName(), "testPolicyText1"));

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.FOLDER, "cmis:kino", "cmis:kino", "", "cmis:folder", "cmis:kino",
            "cmis:kino", true, false, true, true, false, false, false, true, null, null,
            ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
      String typeID = getStorage().addType(newType);

      try
      {
         String docId = getConnection().createPolicy(testroot.getObjectId(), properties, null, null, null);
         doFail();
      }
      catch (ConstraintException ex)
      {
         pass();
      }
      catch (Exception e)
      {
         //e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }
   
   
   /**
    * 2.2.4.5.3
    * The “controllablePolicy” attribute of the Object-Type definition specified by 
    * the cmis:objectTypeId property value is set to FALSE and at least one policy is provided.
    * @throws Exception
    */
   public void testCreatePolicy_ConstraintException3() throws Exception
   {
      System.out.print("Running testCreatePolicy_ConstraintException3....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition,getPropsMap("cmis:folder", "testroot") , null, null);

      Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

      org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
            CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
            "f1", true, null, null);

      org.xcmis.spi.model.PropertyDefinition<?> fPropDefObjectTypeId =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
            CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
            false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

      org.xcmis.spi.model.PropertyDefinition<?> def2=
         PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);
      

      fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
      fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
         fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "policy1"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
         .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino"));
     properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def2.getId(), def2.getQueryName(),
         def2.getLocalName(), def2.getDisplayName(), "testPolicyText1"));

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.FOLDER, "cmis:kino", "cmis:kino", "", "cmis:folder", "cmis:kino",
            "cmis:kino", true, false, true, true, false, false, false, false, null, null,
            ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
      String typeID = getStorage().addType(newType);
      
      PolicyData policy = createPolicy(testroot, "policy2");

      ArrayList<String> policies = new ArrayList<String>();
      policies.add(policy.getObjectId());

      try
      {
         String docId = getConnection().createPolicy(testroot.getObjectId(), properties, null, null, policies);
         doFail();
      }
      catch (ConstraintException ex)
      {
         pass();
      }
      catch (Exception e)
      {
         //e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }
   
   
   
   /**
    * 2.2.4.5.3
    * The “controllableACL” attribute of the Object-Type definition specified by the cmis:objectTypeId 
    * property value is set to FALSE and at least one ACE is provided.
    * @throws Exception
    */
   public void testCreatePolicy_ConstraintException4() throws Exception
   {
      System.out.print("Running testCreatePolicy_ConstraintException4....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition,getPropsMap("cmis:folder", "testroot") , null, null);

      Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

      org.xcmis.spi.model.PropertyDefinition<?> fPropDefName =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
            CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
            "f1", true, null, null);

      org.xcmis.spi.model.PropertyDefinition<?> fPropDefObjectTypeId =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
            CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
            false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

      org.xcmis.spi.model.PropertyDefinition<?> def2=
         PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);
      

      fPropertyDefinitions.put(CmisConstants.NAME, fPropDefName);
      fPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
         fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "policy1"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
         .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(), "cmis:kino"));
     properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def2.getId(), def2.getQueryName(),
         def2.getLocalName(), def2.getDisplayName(), "testPolicyText1"));

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.FOLDER, "cmis:kino", "cmis:kino", "", "cmis:folder", "cmis:kino",
            "cmis:kino", true, false, true, true, false, false, false, false, null, null,
            ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
      String typeID = getStorage().addType(newType);
      
      AccessControlEntry acl = new AccessControlEntry();
      acl.setPrincipal("Makis");
      acl.getPermissions().add("cmis:read");
      ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
      addACL.add(acl);
      try
      {
         String docId = getConnection().createPolicy(testroot.getObjectId(), properties, addACL, null, null);
         doFail();
      }
      catch (ConstraintException ex)
      {
         pass();
      }
      catch (Exception e)
      {
         //e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }
   
   
   
   /**
    * 2.2.4.5.3
    * The “controllableACL” attribute of the Object-Type definition specified by the cmis:objectTypeId 
    * property value is set to FALSE and at least one ACE is provided.
    * @throws Exception
    */
   public void testCreatePolicy_ConstraintException5() throws Exception
   {
      System.out.print("Running testCreatePolicy_ConstraintException5....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      
      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition,getPropsMap("cmis:folder", "testroot") , null, null);

      org.xcmis.spi.model.PropertyDefinition<?> def =
         PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);
      Map<String, Property<?>> properties = getPropsMap("cmis:policy", "policy");
      properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def.getId(), def.getQueryName(),
         def.getLocalName(), def.getDisplayName(), "testPolicyText"));
      
      
      AccessControlEntry acl = new AccessControlEntry();
      acl.setPrincipal("Makis");
      acl.getPermissions().add("cmis:unknown");
      ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
      addACL.add(acl);
      try
      {
         String docId = getConnection().createPolicy(testroot.getObjectId(), properties, addACL, null, null);
         doFail();
      }
      catch (ConstraintException ex)
      {
         pass();
      }
      catch (Exception e)
      {
         //e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
         //getStorage().removeType(typeID);
      }
   }
   
   
   /**
    * 2.2.4.6
    * Gets the list of allowable actions for an Object.
    * @throws Exception
    */
   public void testGetAllowableActions_Simlpe() throws Exception
   {
      System.out.print("Running testGetAllowableActions_Simlpe....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      try
      {
         AllowableActions actions = getConnection().getAllowableActions(testroot.getObjectId());
         assertNotNull(actions);
         assertNotNull(actions.isCanAddObjectToFolder());
         assertNotNull(actions.isCanApplyACL());
         assertNotNull(actions.isCanApplyPolicy());
         assertNotNull(actions.isCanCancelCheckOut());
         assertNotNull(actions.isCanCreateDocument());
         assertNotNull(actions.isCanCreateFolder());
         assertNotNull(actions.isCanCreateRelationship());
         assertNotNull(actions.isCanDeleteContentStream());
         assertNotNull(actions.isCanDeleteObject());
         assertNotNull(actions.isCanDeleteTree());
         assertNotNull(actions.isCanGetACL());
         assertNotNull(actions.isCanGetAllVersions());
         assertNotNull(actions.isCanGetAppliedPolicies());
         assertNotNull(actions.isCanGetChildren());
         assertNotNull(actions.isCanGetContentStream());
         assertNotNull(actions.isCanGetDescendants());
         assertNotNull(actions.isCanGetFolderParent());
         assertNotNull(actions.isCanGetFolderTree());
         assertNotNull(actions.isCanGetObjectParents());
         assertNotNull(actions.isCanGetObjectRelationships());
         assertNotNull(actions.isCanGetProperties());
         assertNotNull(actions.isCanGetRenditions());
         assertNotNull(actions.isCanMoveObject());
         assertNotNull(actions.isCanRemoveObjectFromFolder());
         assertNotNull(actions.isCanRemovePolicy());
         assertNotNull(actions.isCanSetContentStream());
         assertNotNull(actions.isCanUpdateProperties());
         pass();
      }
      catch (Exception e)
      {
         //e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }
   }
   
   
   
   
   /**
    * 2.2.4.7
    * Gets the specified information for the Object. 
    * @throws Exception
    */
   public void testGetObject_Simlpe() throws Exception
   {
      System.out.print("Running testGetObject_Simlpe....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      try
      {
       CmisObject obj  = getConnection().getObject(testroot.getObjectId(), false, IncludeRelationships.NONE, false, false, true, "", "*");
       assertEquals( "testroot", obj.getObjectInfo().getName());
       assertEquals(testroot.getObjectId(), obj.getObjectInfo().getId());
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }
   }
   
   
   /**
    * 2.2.4.7
    * Repositories SHOULD return only the properties specified in the property filter 
    * if they exist on the object’s type definition.
    * @throws Exception
    */
   public void testGetObject_PropertyFiltered() throws Exception
   {
      System.out.print("Running testGetObject_PropertyFiltered....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      try
      {
       CmisObject obj  = getConnection().getObject(testroot.getObjectId(), false, IncludeRelationships.NONE, false, false, false, "cmis:name,cmis:path", "*");
       for (Map.Entry<String, Property<?>> e : obj.getProperties().entrySet())
       {
          assertTrue(e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path")); //Other props must be ignored
       }
         pass();
      }
      catch (Exception e)
      {
         //e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }
   }
   
   
   
   /**
    * 2.2.4.7
    * Value indicating what relationships in which the objects returned participate MUST be returned, if any.
    * @throws Exception
    */
   public void testGetObject_IncludeRelationships() throws Exception
   {
      System.out.print("Running testGetObject_IncludeRelationships....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      
     ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      
     RelationshipData reldata =  getStorage().createRelationship(doc1, testroot, relationshipTypeDefinition , getPropsMap("cmis:relationship", "rel1"), null, null);
      
      try
      {
       CmisObject obj  = getConnection().getObject(testroot.getObjectId(), false, IncludeRelationships.TARGET, false, false, true, "", "*");
       assertEquals(1, obj.getRelationship().size());
       for (CmisObject e : obj.getRelationship())
       {
          assertEquals(reldata.getObjectId(),  e.getObjectInfo().getId());
       }
         pass();
      }
      catch (Exception e)
      {
         //e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }
   }
   
   
   /**
    * 2.2.4.7
    * The Repository MUST return the Ids of the policies applied to the object.  Defaults to FALSE.
    * @throws Exception
    */
   public void testGetObject_IncludePolicyIDs() throws Exception
   {
      System.out.print("Running testGetObject_IncludePolicyIDs....");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      PolicyData policy = createPolicy(testroot, "policy1");
      try
      {
       getConnection().applyPolicy(policy.getObjectId(), testroot.getObjectId());
       CmisObject obj  = getConnection().getObject(testroot.getObjectId(), false, IncludeRelationships.TARGET, true, false, true, "", "*");
       assertEquals(1, obj.getPolicyIds().size());
       for (String e : obj.getPolicyIds())
       {
          assertEquals(policy.getObjectId(),  e);
       }
         pass();
      }
      catch (Exception e)
      {
         //e.printStackTrace();
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }
   }
   
   protected void tearDown()
   {

   }
}
