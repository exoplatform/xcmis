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
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.ContentStreamAllowed;
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
      } finally{
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
      }finally{
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
      org.xcmis.spi.model.PropertyDefinition<?> def =
         PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);
      Map<String, Property<?>> properties = getPropsMap("cmis:policy", "policy1");
      properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def.getId(), def.getQueryName(), def.getLocalName(),
         def.getDisplayName(), "testPolicyText"));

      PolicyData policy = getStorage().createPolicy(testroot, policyTypeDefinition, properties, null, null);

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
   /*
   public void testCreateDocument_ApplyACL() throws Exception
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

   }
    */
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
      }finally{
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

      org.xcmis.spi.model.PropertyDefinition<?> def =
         PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);
      Map<String, Property<?>> properties2 = getPropsMap("cmis:policy", "policy1");
      properties2.put(CmisConstants.POLICY_TEXT, new StringProperty(def.getId(), def.getQueryName(),
         def.getLocalName(), def.getDisplayName(), "testPolicyText"));
      PolicyData policy = getStorage().createPolicy(testroot, policyTypeDefinition, properties2, null, null);

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
      }finally {
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
         assertEquals("doc2", getStorage().getObjectById(docId).getProperty(CmisConstants.NAME).getValues().get(0));
         pass();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         doFail(e.getMessage());
      }finally {
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

      org.xcmis.spi.model.PropertyDefinition<?> def =
         PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);
      PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);
      Map<String, Property<?>> properties = getPropsMap("cmis:policy", "policy1");
      properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def.getId(), def.getQueryName(), def.getLocalName(),
         def.getDisplayName(), "testPolicyText"));

      PolicyData policy = getStorage().createPolicy(testroot, policyTypeDefinition, properties, null, null);

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
   /*
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
   }
*/
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
          CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, null, CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, false,
          false, false, false, false, Updatability.READONLY, "fold_type_chld_ids", null, null, null);
    
    folderPropertyDefinitions.put(CmisConstants.NAME, fPropDefName2);
    folderPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, fPropDefObjectTypeId);
    folderPropertyDefinitions.put(CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, fPropDefAllowedChild);

    Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
    
    properties.put(CmisConstants.NAME, new StringProperty(fPropDefName2.getId(), fPropDefName2.getQueryName(),
       fPropDefName2.getLocalName(), fPropDefName2.getDisplayName(), "myfolder"));
    
    properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(),
       fPropDefObjectTypeId.getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId
          .getDisplayName(), "cmis:kino"));

    properties.put(CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, new IdProperty(fPropDefObjectTypeId.getId(),
       fPropDefObjectTypeId.getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId
          .getDisplayName(), "cmis:folder"));
    
    TypeDefinition newType =
       new TypeDefinition("cmis:myfolder", BaseType.FOLDER, "cmis:myfolder", "cmis:myfolder", "", "cmis:folder", "cmis:myfolder",
          "cmis:myfolder", true, false, true, true, false, false, false, false, null, null,
          ContentStreamAllowed.NOT_ALLOWED, folderPropertyDefinitions);
    String typeID = getStorage().addType(newType);

    FolderData myfolder =
         getStorage()
            .createFolder(testroot, newType, properties, null, null);

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
            getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(),
               properties, null, null, null, VersioningState.MAJOR);
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
            "cmis:kino", true, false, true, true, false, false, false, true, null, null,
            ContentStreamAllowed.ALLOWED, kinoPropertyDefinitions);
      String typeID = getStorage().addType(newType);
      
      DocumentData doc1 =
         getStorage().createDocument(testroot, newType, properties, cs, null,
            null, VersioningState.MAJOR);
      
      properties.put(CmisConstants.NAME, new StringProperty(kinoPropDefName2.getId(), kinoPropDefName2.getQueryName(),
         kinoPropDefName2.getLocalName(), kinoPropDefName2.getDisplayName(), "doc2"));
    
      try
      {
         String docId =
            getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(),
               properties, null, null, null, VersioningState.NONE);
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
    
      org.xcmis.spi.model.PropertyDefinition<?> def =
         PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);
      Map<String, Property<?>> properties2 = getPropsMap("cmis:policy", "policy1");
      properties2.put(CmisConstants.POLICY_TEXT, new StringProperty(def.getId(), def.getQueryName(),
         def.getLocalName(), def.getDisplayName(), "testPolicyText"));
      PolicyData policy = getStorage().createPolicy(testroot, policyTypeDefinition, properties2, null, null);

      ArrayList<String> policies = new ArrayList<String>();
      policies.add(policy.getObjectId());
      
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc"), cs, null,
            null, VersioningState.MAJOR);
      try
      {
         String docId =
            getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(),
               properties, null, null, policies, VersioningState.NONE);
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
   
   
   protected void tearDown()
   {
    
   }
}
