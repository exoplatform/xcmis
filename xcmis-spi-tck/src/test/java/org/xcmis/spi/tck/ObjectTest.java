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

import static org.junit.Assert.*;

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

   static FolderData testroot;

   @BeforeClass
   public static void start() throws Exception
   {
      BaseTest.setUp();
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      testroot =
         getStorage().createFolder(rootFolder, folderTypeDefinition,
            getPropsMap(CmisConstants.FOLDER, "object_testroot"), null, null);
      System.out.print("Running Object Service tests....");
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
      byte[] before = new byte[15];
      before = "1234567890aBcDE".getBytes();
      ContentStream cs = new BaseContentStream(before, null, new MimeType("text", "plain"));
      String docId =
         getConnection().createDocument(testroot.getObjectId(),
            getPropsMap(CmisConstants.DOCUMENT, "testCreateDocument_CheckContent"), cs, null, null, null,
            VersioningState.MAJOR);
      ContentStream c = getStorage().getObjectById(docId).getContentStream(null);
      assertTrue("Media types does not match", cs.getMediaType().equals(c.getMediaType()));
      byte[] after = new byte[15];
      c.getStream().read(after);
      assertArrayEquals(before, after);
   }

   /**
    * 2.2.4.1.1
    * The property values that MUST be applied to the newly-created Document Object.
    * @throws Exception
    */
   @Test
   public void testCreateDocument_CheckProperties() throws Exception
   {
      String docname = "testCreateDocument_CheckProperties";
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      Map<String, Property<?>> properties = getPropsMap(CmisConstants.DOCUMENT, docname);
      String docId =
         getConnection()
            .createDocument(testroot.getObjectId(), properties, cs, null, null, null, VersioningState.MAJOR);
      ObjectData res = getStorage().getObjectById(docId);
      assertNotNull("NAME property is null;", res.getProperty(CmisConstants.NAME));
      if (!((String)res.getProperty(CmisConstants.NAME).getValues().get(0)).equals(docname))
         fail("Names does not match;");
   }

   /**
    * 2.2.4.1.1
    * A list of policy IDs that MUST be applied to the newly-created Document object. 
    * @throws Exception
    */
   @Test
   public void testCreateDocument_ApplyPolicy() throws Exception
   {
      if (!IS_POLICIES_SUPPORTED)
      {
         //SKIP
         return;
      }
      PolicyData policy = null;
      String docId = null;
      try
      {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         policy = createPolicy(testroot, "testCreateDocument_ApplyPolicy_policy1");

         ArrayList<String> policies = new ArrayList<String>();
         policies.add(policy.getObjectId());

         docId =
            getConnection().createDocument(testroot.getObjectId(),
               getPropsMap(CmisConstants.DOCUMENT, "testCreateDocument_ApplyPolicy"), cs, null, null, policies,
               VersioningState.MAJOR);
         ObjectData res = getStorage().getObjectById(docId);
         assertTrue("Properties size iz incorrect", res.getPolicies().size() == 1);
         Iterator<PolicyData> it = res.getPolicies().iterator();
         while (it.hasNext())
         {
            PolicyData one = it.next();
            assertTrue("Policy names does not match", one.getName().equals("testCreateDocument_ApplyPolicy_policy1"));
            assertTrue("Policy text does not match", one.getPolicyText().equals("testPolicyText"));
         }
      }
      finally
      {
         if (docId != null)
            getStorage().deleteObject(getStorage().getObjectById(docId), true);
         if (policy != null)
            getStorage().deleteObject(policy, true);
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
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         //SKIP
         return;
      }
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      String username = "username";
      List<AccessControlEntry> addACL = createACL(username, "cmis:read");

      String docId =
         getConnection().createDocument(testroot.getObjectId(),
            getPropsMap(CmisConstants.DOCUMENT, "testCreateDocument_AddACL"), cs, addACL, null, null,
            VersioningState.MAJOR);
      ObjectData res = getStorage().getObjectById(docId);
      for (AccessControlEntry one : res.getACL(false))
      {
         if (one.getPrincipal().equalsIgnoreCase(username))
         {
            assertTrue("Permissions size is incorrect", one.getPermissions().size() == 1);
            assertTrue("Permissions does not match", one.getPermissions().contains("cmis:read"));
         }
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
      String filename = "testCreateDocument_NameConstraintViolationException";
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, filename),
            cs, null, null, VersioningState.MAJOR);
      try
      {
         String docId =
            getConnection().createDocument(testroot.getObjectId(), getPropsMap(CmisConstants.DOCUMENT, filename), cs,
               null, null, null, VersioningState.MAJOR);
         fail("NameConstraintViolationException must be thrown;");
      }
      catch (NameConstraintViolationException ex)
      {
         //OK
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

      String typeID = null;
      String docId = null;

      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

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
         propDefName.getLocalName(), propDefName.getDisplayName(), "testCreateDocument_StreamNotSupportedException"));
      properties
         .put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId
            .getQueryName(), propDefObjectTypeId.getLocalName(), propDefObjectTypeId.getDisplayName(),
            "cmis:objecttype1"));

      TypeDefinition newType =
         new TypeDefinition("cmis:objecttype1", BaseType.DOCUMENT, "cmis:objecttype1", "cmis:objecttype1", "",
            "cmis:document", "cmis:objecttype1", "cmis:objecttype1", true, false, true, true, false, false, false,
            true, null, null, ContentStreamAllowed.NOT_ALLOWED, propertyDefinitions);
      typeID = getStorage().addType(newType);
      newType = getStorage().getTypeDefinition(typeID, true);
      try
      {
         docId =
            getConnection().createDocument(testroot.getObjectId(), properties, cs, null, null, null,
               VersioningState.MAJOR);
         fail("StreamNotSupportedException must be thrown;");
      }
      catch (StreamNotSupportedException ex)
      {
         //OK
      }
      finally
      {
         if (docId != null)
            getStorage().deleteObject(getStorage().getObjectById(docId), true);
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
   public void testCreateDocument_ConstraintExceptionWrongObjectType() throws Exception
   {
      String docId = null;
      String typeID = null;

      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

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
         propDefName.getLocalName(), propDefName.getDisplayName(),
         "testCreateDocument_ConstraintExceptionWrongObjectType"));
      properties
         .put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId
            .getQueryName(), propDefObjectTypeId.getLocalName(), propDefObjectTypeId.getDisplayName(),
            "cmis:objecttype2"));

      TypeDefinition newType =
         new TypeDefinition("cmis:objecttype2", BaseType.FOLDER, "cmis:objecttype2", "cmis:objecttype2", "",
            "cmis:folder", "cmis:objecttype2", "cmis:objecttype2", true, false, true, true, false, false, false, true,
            null, null, ContentStreamAllowed.NOT_ALLOWED, propertyDefinitions);
      typeID = getStorage().addType(newType);
      newType = getStorage().getTypeDefinition(typeID, true);
      try
      {
         docId =
            getConnection().createDocument(testroot.getObjectId(), properties, cs, null, null, null,
               VersioningState.MAJOR);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
         if (docId != null)
            getStorage().deleteObject(getStorage().getObjectById(docId), true);
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
   public void testCreateDocument_ConstraintExceptionContentAllowed() throws Exception
   {
      String docId = null;
      String typeID = null;

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
         propDefName.getLocalName(), propDefName.getDisplayName(),
         "testCreateDocument_ConstraintExceptionContentAllowed"));
      properties
         .put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId
            .getQueryName(), propDefObjectTypeId.getLocalName(), propDefObjectTypeId.getDisplayName(),
            "cmis:objecttype3"));

      TypeDefinition newType =
         new TypeDefinition("cmis:objecttype3", BaseType.DOCUMENT, "cmis:objecttype3", "cmis:objecttype3", "",
            "cmis:document", "cmis:objecttype3", "cmis:objecttype3", true, false, true, true, false, false, false,
            true, null, null, ContentStreamAllowed.REQUIRED, propertyDefinitions);
      typeID = getStorage().addType(newType);
      newType = getStorage().getTypeDefinition(typeID, true);
      try
      {
         docId =
            getConnection().createDocument(testroot.getObjectId(), properties, null, null, null, null,
               VersioningState.MAJOR);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
         if (docId != null)
            getStorage().deleteObject(getStorage().getObjectById(docId), true);
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
   public void testCreateDocument_ConstraintExceptionVersionable() throws Exception
   {
      String docId = null;
      String typeID = null;

      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

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
      properties
         .put(CmisConstants.NAME, new StringProperty(propDefName.getId(), propDefName.getQueryName(), propDefName
            .getLocalName(), propDefName.getDisplayName(), "testCreateDocument_ConstraintExceptionVersionable"));
      properties
         .put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId
            .getQueryName(), propDefObjectTypeId.getLocalName(), propDefObjectTypeId.getDisplayName(),
            "cmis:objecttype4"));

      TypeDefinition newType =
         new TypeDefinition("cmis:objecttype4", BaseType.DOCUMENT, "cmis:objecttype4", "cmis:objecttype4", "",
            "cmis:document", "cmis:objecttype4", "cmis:objecttype4", true, false, true, true, false, false, false,
            true, null, null, ContentStreamAllowed.ALLOWED, propertyDefinitions);
      typeID = getStorage().addType(newType);
      newType = getStorage().getTypeDefinition(typeID, true);
      try
      {
         docId =
            getConnection().createDocument(testroot.getObjectId(), properties, cs, null, null, null,
               VersioningState.NONE);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
         if (docId != null)
            getStorage().deleteObject(getStorage().getObjectById(docId), true);
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
   public void testCreateDocument_ConstraintExceptionNonVersinable() throws Exception
   {
      String docId = null;
      String typeID = null;

      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

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
         propDefName.getLocalName(), propDefName.getDisplayName(),
         "testCreateDocument_ConstraintExceptionNonVersinable"));
      properties
         .put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId
            .getQueryName(), propDefObjectTypeId.getLocalName(), propDefObjectTypeId.getDisplayName(),
            "cmis:objecttype5"));

      TypeDefinition newType =
         new TypeDefinition("cmis:objecttype5", BaseType.DOCUMENT, "cmis:objecttype5", "cmis:objecttype5", "",
            "cmis:document", "cmis:objecttype5", "cmis:objecttype5", true, false, true, true, false, false, false,
            false, null, null, ContentStreamAllowed.ALLOWED, propertyDefinitions);
      typeID = getStorage().addType(newType);
      newType = getStorage().getTypeDefinition(typeID, true);
      try
      {
         docId =
            getConnection().createDocument(testroot.getObjectId(), properties, cs, null, null, null,
               VersioningState.MAJOR);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
         if (docId != null)
            getStorage().deleteObject(getStorage().getObjectById(docId), true);
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
   public void testCreateDocument_ConstraintExceptionNotControllablePolicy() throws Exception
   {
      if (!IS_POLICIES_SUPPORTED)
      {
         //SKIP
         return;
      }

      String typeID = null;
      PolicyData policy = null;
      String docId = null;
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

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
         propDefName.getLocalName(), propDefName.getDisplayName(),
         "testCreateDocument_ConstraintExceptionNotControllablePolicy"));
      properties
         .put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId
            .getQueryName(), propDefObjectTypeId.getLocalName(), propDefObjectTypeId.getDisplayName(),
            "cmis:objecttype6"));

      TypeDefinition newType =
         new TypeDefinition("cmis:objecttype6", BaseType.DOCUMENT, "cmis:objecttype6", "cmis:objecttype6", "",
            "cmis:document", "cmis:objecttype6", "cmis:objecttype6", true, false, true, true, false, false, false,
            false, null, null, ContentStreamAllowed.ALLOWED, propertyDefinitions);
      typeID = getStorage().addType(newType);
      newType = getStorage().getTypeDefinition(typeID, true);

      policy = createPolicy(testroot, "object_policy1");

      ArrayList<String> policies = new ArrayList<String>();
      policies.add(policy.getObjectId());
      try
      {
         docId =
            getConnection().createDocument(testroot.getObjectId(), properties, cs, null, null, policies,
               VersioningState.MAJOR);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
         if (docId != null)
            getStorage().deleteObject(getStorage().getObjectById(docId), true);
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
   public void testCreateDocument_ConstraintExceptionNotControllableACL() throws Exception
   {
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         //SKIP
         return;
      }
      String docId = null;
      String typeID = null;
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

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
         propDefName.getLocalName(), propDefName.getDisplayName(),
         "testCreateDocument_ConstraintExceptionNotControllableACL"));
      properties
         .put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId
            .getQueryName(), propDefObjectTypeId.getLocalName(), propDefObjectTypeId.getDisplayName(),
            "cmis:objecttype7"));

      TypeDefinition newType =
         new TypeDefinition("cmis:objecttype7", BaseType.DOCUMENT, "cmis:objecttype7", "cmis:objecttype7", "",
            "cmis:document", "cmis:objecttype7", "cmis:objecttype7", true, false, true, true, false, false, false,
            false, null, null, ContentStreamAllowed.ALLOWED, propertyDefinitions);
      typeID = getStorage().addType(newType);
      newType = getStorage().getTypeDefinition(typeID, true);

      String username = "username";
      List<AccessControlEntry> addACL = createACL(username, "cmis:read");
      try
      {
         docId =
            getConnection().createDocument(testroot.getObjectId(), properties, cs, addACL, null, null,
               VersioningState.MAJOR);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK 
      }
      finally
      {
         if (docId != null)
            getStorage().deleteObject(getStorage().getObjectById(docId), true);
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
   public void testCreateDocument_ConstraintExceptionACENotSupp() throws Exception
   {
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         //SKIP
         return;
      }
      String docId = null;
      String typeID = null;

      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

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
         propDefName.getLocalName(), propDefName.getDisplayName(), "testCreateDocument_ConstraintExceptionACENotSupp"));
      properties
         .put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId
            .getQueryName(), propDefObjectTypeId.getLocalName(), propDefObjectTypeId.getDisplayName(),
            "cmis:objecttype8"));

      TypeDefinition newType =
         new TypeDefinition("cmis:objecttype8", BaseType.DOCUMENT, "cmis:objecttype8", "cmis:objecttype8", "",
            "cmis:document", "cmis:objecttype8", "cmis:objecttype8", true, false, true, true, false, false, true,
            false, null, null, ContentStreamAllowed.ALLOWED, propertyDefinitions);
      typeID = getStorage().addType(newType);
      newType = getStorage().getTypeDefinition(typeID, true);

      String username = "username";
      List<AccessControlEntry> addACL = createACL(username, "cmis:unknown");
      try
      {
         docId =
            getConnection().createDocument(testroot.getObjectId(), properties, cs, addACL, null, null,
               VersioningState.MAJOR);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
         if (docId != null)
            getStorage().deleteObject(getStorage().getObjectById(docId), true);
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
      byte[] before = new byte[15];
      before = "1234567890aBcDE".getBytes();
      ContentStream cs = new BaseContentStream(before, null, new MimeType("text", "plain"));
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition,
            getPropsMap(CmisConstants.DOCUMENT, "testCreateDocumentFromSource_Simple1"), cs, null, null,
            VersioningState.MAJOR);
      String docId =
         getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(),
            getPropsMap(CmisConstants.DOCUMENT, "testCreateDocumentFromSource_Simple2"), null, null, null,
            VersioningState.MAJOR);
      ContentStream c = getStorage().getObjectById(docId).getContentStream(null);
      assertTrue("Media types does not match", cs.getMediaType().equals(c.getMediaType()));

      byte[] after = new byte[15];
      c.getStream().read(after);
      assertArrayEquals(before, after);
      if (!testroot.getName().equals(getStorage().getObjectById(docId).getParent().getName()))
         fail("Names does not match;");
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
      String name = "testCreateDocumentFromSource_Properties2";
      byte[] before = new byte[15];
      before = "1234567890aBcDE".getBytes();
      ContentStream cs = new BaseContentStream(before, null, new MimeType("text", "plain"));
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition,
            getPropsMap(CmisConstants.DOCUMENT, "testCreateDocumentFromSource_Properties1"), cs, null, null,
            VersioningState.MAJOR);
      String docId =
         getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(),
            getPropsMap(CmisConstants.DOCUMENT, name), null, null, null, VersioningState.MAJOR);
      if (!getStorage().getObjectById(docId).getProperty(CmisConstants.NAME).getValues().get(0).equals(name))
         fail("Names does not match;");
   }

   /**
    * 2.2.4.2.1
    * A list of policy IDs that MUST be applied to the newly-created Document object. 
    * @throws Exception
    */
   @Test
   public void testCreateDocumentFromSource_ApplyPolicy() throws Exception
   {
      if (!IS_POLICIES_SUPPORTED)
      {
         //SKIP
         return;
      }

      PolicyData policy = null;
      try
      {
         byte[] before = new byte[15];
         before = "1234567890aBcDE".getBytes();
         ContentStream cs = new BaseContentStream(before, null, new MimeType("text", "plain"));
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition,
               getPropsMap(CmisConstants.DOCUMENT, "testCreateDocumentFromSource_ApplyPolicy1"), cs, null, null,
               VersioningState.MAJOR);

         policy = createPolicy(testroot, "object_policy2");

         ArrayList<String> policies = new ArrayList<String>();
         policies.add(policy.getObjectId());
         String docId =
            getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(),
               getPropsMap(CmisConstants.DOCUMENT, "testCreateDocumentFromSource_ApplyPolicy2"), null, null, policies,
               VersioningState.MAJOR);
         ObjectData res = getStorage().getObjectById(docId);
         assertTrue("Properties size is incorrect", res.getPolicies().size() == 1);
         Iterator<PolicyData> it = res.getPolicies().iterator();
         while (it.hasNext())
         {
            PolicyData one = it.next();
            assertTrue("Policy names does not match", one.getName().equals("object_policy2"));
            assertTrue("Policy text does not match", one.getPolicyText().equals("testPolicyText"));
            res.removePolicy(one);
         }
      }
      finally
      {
         if (policy != null)
            getStorage().deleteObject(policy, true);
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
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         //SKIP
         return;
      }
      byte[] before = new byte[15];
      before = "1234567890aBcDE".getBytes();
      ContentStream cs = new BaseContentStream(before, null, new MimeType("text", "plain"));
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition,
            getPropsMap(CmisConstants.DOCUMENT, "testCreateDocumentFromSource_addACL1"), cs, null, null,
            VersioningState.MAJOR);

      String username = "username";
      List<AccessControlEntry> addACL = createACL(username, "cmis:read");

      String docId =
         getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(),
            getPropsMap(CmisConstants.DOCUMENT, "testCreateDocumentFromSource_addACL2"), addACL, null, null,
            VersioningState.MAJOR);
      ObjectData res = getStorage().getObjectById(docId);
      for (AccessControlEntry one : res.getACL(false))
      {
         if (one.getPrincipal().equalsIgnoreCase(username))
         {
            assertTrue("Permissions size is incorrect", one.getPermissions().size() == 1);
            assertTrue("Permissions does not match", one.getPermissions().contains("cmis:read"));
         }
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
      String name = "testCreateDocumentFromSource_NameConstraintViolationException";
      byte[] before = new byte[15];
      before = "1234567890aBcDE".getBytes();
      ContentStream cs = new BaseContentStream(before, null, new MimeType("text", "plain"));
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, name), cs,
            null, null, VersioningState.MAJOR);
      try
      {
         String docId =
            getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(),
               getPropsMap(CmisConstants.DOCUMENT, name), null, null, null, VersioningState.MAJOR);
         ObjectData res = getStorage().getObjectById(docId);
         assertFalse("Names must not match;", res.getName().equals(name));
      }
      catch (NameConstraintViolationException ex)
      {
         //OK
      }
   }

   /**
    * 2.2.4.2.3
    * constraint: The Repository MUST throw this exception if  the sourceId is not an Object whose baseType is “Document”.
    * @throws Exception
    */
   @Test
   public void testCreateDocumentFromSource_ConstraintExceptionWrongBaseType() throws Exception
   {
      FolderData test = createFolder(testroot, "123");
      try
      {
         String docId =
            getConnection().createDocumentFromSource(test.getObjectId(), testroot.getObjectId(),
               getPropsMap(CmisConstants.DOCUMENT, "testCreateDocumentFromSource_ConstraintExceptionWrongBaseType1"),
               null, null, null, VersioningState.MAJOR);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
   }

   /**
    * 2.2.4.2.3
    * The source document’s cmis:objectTypeId property value is NOT in the list of AllowedChildObjectTypeIds 
    * of the parent-folder specified by folderId.
    * @throws Exception
    */
   @Test
   public void testCreateDocumentFromSource_ConstraintExceptionNotAllowedChild() throws Exception
   {
      String docId = null;
      String typeID = null;
      FolderData myfolder = null;

      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      try
      {
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
            getStorage().createDocument(testroot, documentTypeDefinition,
               getPropsMap(CmisConstants.DOCUMENT, "testCreateDocumentFromSource_ConstraintExceptionNotAllowedChild"),
               cs, null, null, VersioningState.MAJOR);

         docId =
            getConnection().createDocumentFromSource(doc1.getObjectId(), myfolder.getObjectId(),
               getPropsMap(CmisConstants.DOCUMENT, "testCreateDocumentFromSource_ConstraintExceptionNotAllowedChild1"),
               null, null, null, VersioningState.MAJOR);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
         if (docId != null)
            getStorage().deleteObject(getStorage().getObjectById(docId), true);
         if (myfolder != null)
            getStorage().deleteObject(myfolder, true);
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
   public void testCreateDocumentFromSource_ConstraintExceptionNotVersionable() throws Exception
   {
      String docId = null;
      String typeID = null;

      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

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

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(propDefName.getId(), propDefName.getQueryName(),
         propDefName.getLocalName(), propDefName.getDisplayName(),
         "testCreateDocumentFromSource_ConstraintExceptionNotVersionable"));
      properties
         .put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId
            .getQueryName(), propDefObjectTypeId.getLocalName(), propDefObjectTypeId.getDisplayName(),
            "cmis:objecttype9"));

      TypeDefinition newType =
         new TypeDefinition("cmis:objecttype9", BaseType.DOCUMENT, "cmis:objecttype9", "cmis:objecttype9", "",
            "cmis:document", "cmis:objecttype9", "cmis:objecttype9", true, false, true, true, false, false, false,
            false, null, null, ContentStreamAllowed.ALLOWED, propertyDefinitions);
      typeID = getStorage().addType(newType);
      newType = getStorage().getTypeDefinition(typeID, true);

      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc"), cs,
            null, null, VersioningState.MAJOR);
      try
      {
         docId =
            getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(), properties, null,
               null, null, VersioningState.MAJOR);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
         if (docId != null)
            getStorage().deleteObject(getStorage().getObjectById(docId), true);
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
   public void testCreateDocumentFromSource_ConstraintExceptionIsVesrionable() throws Exception
   {
      String docId = null;
      String typeID = null;

      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

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
         propDefName.getLocalName(), propDefName.getDisplayName(),
         "testCreateDocumentFromSource_ConstraintExceptionIsVesrionable"));
      properties.put(CmisConstants.OBJECT_TYPE_ID,
         new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId.getQueryName(), propDefObjectTypeId
            .getLocalName(), propDefObjectTypeId.getDisplayName(), "cmis:objecttype10"));

      TypeDefinition newType =
         new TypeDefinition("cmis:objecttype10", BaseType.DOCUMENT, "cmis:objecttype10", "cmis:objecttype10", "",
            "cmis:document", "cmis:objecttype10", "cmis:objecttype10", true, false, true, true, false, false, false,
            true, null, null, ContentStreamAllowed.ALLOWED, propertyDefinitions);
      typeID = getStorage().addType(newType);
      newType = getStorage().getTypeDefinition(typeID, true);

      DocumentData doc1 =
         getStorage().createDocument(testroot, newType, properties, cs, null, null, VersioningState.MAJOR);

      properties.put(CmisConstants.NAME, new StringProperty(propDefName.getId(), propDefName.getQueryName(),
         propDefName.getLocalName(), propDefName.getDisplayName(), "doc2"));
      try
      {
         docId =
            getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(), properties, null,
               null, null, VersioningState.NONE);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
         if (doc1 != null)
            getStorage().deleteObject(doc1, true);
         if (docId != null)
            getStorage().deleteObject(getStorage().getObjectById(docId), true);
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
   public void testCreateDocumentFromSource_ConstraintExceptionNotControllablePolicy() throws Exception
   {
      if (!IS_POLICIES_SUPPORTED)
      {
         //SKIP
         return;
      }
      String docId = null;
      String typeID = null;
      PolicyData policy = null;
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

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
         propDefName.getLocalName(), propDefName.getDisplayName(),
         "testCreateDocumentFromSource_ConstraintExceptionNotControllablePolicy"));
      properties.put(CmisConstants.OBJECT_TYPE_ID,
         new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId.getQueryName(), propDefObjectTypeId
            .getLocalName(), propDefObjectTypeId.getDisplayName(), "cmis:objecttype11"));

      TypeDefinition newType =
         new TypeDefinition("cmis:objecttype11", BaseType.DOCUMENT, "cmis:objecttype11", "cmis:objecttype11", "",
            "cmis:document", "cmis:objecttype11", "cmis:objecttype11", true, false, true, true, false, false, false,
            true, null, null, ContentStreamAllowed.ALLOWED, propertyDefinitions);
      typeID = getStorage().addType(newType);
      newType = getStorage().getTypeDefinition(typeID, true);

      policy = createPolicy(testroot, "object_policy4");

      ArrayList<String> policies = new ArrayList<String>();
      policies.add(policy.getObjectId());

      DocumentData doc1 =
         getStorage().createDocument(
            testroot,
            documentTypeDefinition,
            getPropsMap(CmisConstants.DOCUMENT,
               "testCreateDocumentFromSource_ConstraintExceptionNotControllablePolicy1"), cs, null, null,
            VersioningState.MAJOR);
      try
      {
         docId =
            getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(), properties, null,
               null, policies, VersioningState.NONE);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
         if (docId != null)
            getStorage().deleteObject(getStorage().getObjectById(docId), true);
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
   public void testCreateDocumentFromSource_ConstraintExceptionNotControllableACL() throws Exception
   {
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         //SKIP
         return;
      }
      String docId = null;
      String typeID = null;
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

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
         propDefName.getLocalName(), propDefName.getDisplayName(),
         "testCreateDocumentFromSource_ConstraintExceptionNotControllableACL"));
      properties.put(CmisConstants.OBJECT_TYPE_ID,
         new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId.getQueryName(), propDefObjectTypeId
            .getLocalName(), propDefObjectTypeId.getDisplayName(), "cmis:objecttype12"));

      TypeDefinition newType =
         new TypeDefinition("cmis:objecttype12", BaseType.DOCUMENT, "cmis:objecttype12", "cmis:objecttype12", "",
            "cmis:document", "cmis:objecttype12", "cmis:objecttype12", true, false, true, true, false, false, false,
            false, null, null, ContentStreamAllowed.ALLOWED, propertyDefinitions);
      typeID = getStorage().addType(newType);
      newType = getStorage().getTypeDefinition(typeID, true);

      String username = "username";
      List<AccessControlEntry> addACL = createACL(username, "cmis:unknown");

      DocumentData doc1 =
         getStorage().createDocument(testroot, newType,
            getPropsMap(CmisConstants.DOCUMENT, "testCreateDocumentFromSource_ConstraintExceptionNotControllableACL1"),
            cs, null, null, VersioningState.MAJOR);
      try
      {
         docId =
            getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(), properties, addACL,
               null, null, VersioningState.NONE);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
         if (doc1 != null)
            getStorage().deleteObject(doc1, true);
         if (docId != null)
            getStorage().deleteObject(getStorage().getObjectById(docId), true);
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
   public void testCreateDocumentFromSource_ConstraintExceptionUnknownACE() throws Exception
   {
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         //SKIP
         return;
      }
      String docId = null;
      String typeID = null;
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
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
         propDefName.getLocalName(), propDefName.getDisplayName(),
         "testCreateDocumentFromSource_ConstraintExceptionUnknownACE"));
      properties.put(CmisConstants.OBJECT_TYPE_ID,
         new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId.getQueryName(), propDefObjectTypeId
            .getLocalName(), propDefObjectTypeId.getDisplayName(), "cmis:objecttype13"));

      TypeDefinition newType =
         new TypeDefinition("cmis:objecttype13", BaseType.DOCUMENT, "cmis:objecttype13", "cmis:objecttype13", "",
            "cmis:document", "cmis:objecttype13", "cmis:objecttype13", true, false, true, true, false, false, true,
            false, null, null, ContentStreamAllowed.NOT_ALLOWED, propertyDefinitions);
      typeID = getStorage().addType(newType);
      newType = getStorage().getTypeDefinition(typeID, true);

      String username = "username";
      List<AccessControlEntry> addACL = createACL(username, "cmis:unknown");

      DocumentData doc1 =
         getStorage().createDocument(testroot, newType,
            getPropsMap(CmisConstants.DOCUMENT, "testCreateDocumentFromSource_ConstraintExceptionUnknownACE1"), cs,
            null, null, VersioningState.MAJOR);
      try
      {
         docId =
            getConnection().createDocumentFromSource(doc1.getObjectId(), testroot.getObjectId(), properties, addACL,
               null, null, VersioningState.NONE);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
         if (doc1 != null)
            getStorage().deleteObject(doc1, true);
         if (docId != null)
            getStorage().deleteObject(getStorage().getObjectById(docId), true);
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
      String docId =
         getConnection()
            .createFolder(testroot.getObjectId(), getPropsMap(CmisConstants.FOLDER, "f1"), null, null, null);
      ObjectData obj = getStorage().getObjectById(docId);
      assertTrue("Object types does not match;", obj.getTypeId().equals(CmisConstants.FOLDER));
      assertTrue("Path is not correct;", ((FolderData)obj).getPath().equals("/object_testroot/f1"));
   }

   /**
    * 2.2.4.3.1
    * A list of policy IDs that MUST be applied to the newly-created Folder object.
    * @throws Exception
    */
   @Test
   public void testCreateFolder_ApplyPolicy() throws Exception
   {
      if (!IS_POLICIES_SUPPORTED)
      {
         //SKIP
         return;
      }
      PolicyData policy = null;
      String docId = null;
      try
      {
         policy = createPolicy(testroot, "object_policy3");

         ArrayList<String> policies = new ArrayList<String>();
         policies.add(policy.getObjectId());

         docId =
            getConnection().createFolder(testroot.getObjectId(), getPropsMap(CmisConstants.FOLDER, "f2"), null, null,
               policies);
         ObjectData res = getStorage().getObjectById(docId);
         assertTrue("Properties size is incorrect;", res.getPolicies().size() == 1);
         Iterator<PolicyData> it = res.getPolicies().iterator();
         while (it.hasNext())
         {
            PolicyData one = it.next();
            assertTrue("POlicy names does not match", one.getName().equals("object_policy3"));
            assertTrue("Policy text does not match", one.getPolicyText().equals("testPolicyText"));
         }
      }
      finally
      {
         if (docId != null)
            getStorage().deleteObject(getStorage().getObjectById(docId), true);
         if (policy != null)
            getStorage().deleteObject(policy, true);
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
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         //SKIP
         return;
      }
      String docId = null;
      String username = "username";
      List<AccessControlEntry> addACL = createACL(username, "cmis:read");

      try
      {
         docId =
            getConnection().createFolder(testroot.getObjectId(), getPropsMap(CmisConstants.FOLDER, "testCreateFolder"),
               addACL, null, null);
         ObjectData res = getStorage().getObjectById(docId);
         for (AccessControlEntry one : res.getACL(false))
         {
            if (one.getPrincipal().equalsIgnoreCase(username))
               assertTrue("Permissions size is incorrect", one.getPermissions().size() == 1);
            assertTrue("Permissions does not match", one.getPermissions().contains("cmis:read"));
         }
      }
      finally
      {
         if (docId != null)
            getStorage().deleteObject(getStorage().getObjectById(docId), true);
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
      getStorage().createFolder(testroot, folderTypeDefinition,
         getPropsMap(CmisConstants.FOLDER, "testCreateFolder_NameConstraint"), null, null);
      try
      {
         String docId =
            getConnection().createFolder(testroot.getObjectId(),
               getPropsMap(CmisConstants.FOLDER, "testCreateFolder_NameConstraint"), null, null, null);
         ObjectData res = getStorage().getObjectById(docId);
         assertFalse("Names must not match;", res.getName().equals("f1"));
      }
      catch (NameConstraintViolationException ex)
      {
         //OK
      }
   }

   /**
    * 2.2.4.3.3
    * The Repository MUST throw this exception if the cmis:objectTypeId property 
    * value is not an Object-Type whose baseType is “Folder”.
    * @throws Exception
    */
   @Test
   public void testCreateFolder_ConstraintExceptionWrongBaseType() throws Exception
   {
      String docId = null;
      String typeID = null;

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
         fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "testCreateFolder_ConstraintExceptionWrong"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
         .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(),
         "cmis:objecttype14"));

      TypeDefinition newType =
         new TypeDefinition("cmis:objecttype14", BaseType.POLICY, "cmis:objecttype14", "cmis:objecttype14", "",
            "cmis:policy", "cmis:objecttype14", "cmis:objecttype14", true, false, true, true, false, false, false,
            true, null, null, ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
      typeID = getStorage().addType(newType);
      newType = getStorage().getTypeDefinition(typeID, true);

      try
      {
         docId = getConnection().createFolder(testroot.getObjectId(), properties, null, null, null);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
         if (docId != null)
            getStorage().deleteObject(getStorage().getObjectById(docId), true);
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
   public void testCreateFolder_ConstraintExceptionNotAllowedChild() throws Exception
   {
      String docId = null;
      String typeID = null;

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

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
         fPropDefName.getLocalName(), fPropDefName.getDisplayName(),
         "testCreateFolder_ConstraintExceptionNotAllowedChild"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
         .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(),
         "cmis:objecttype15"));
      properties.put(CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, new IdProperty(fPropDefAllowedChilds.getId(),
         fPropDefAllowedChilds.getQueryName(), fPropDefAllowedChilds.getLocalName(), fPropDefAllowedChilds
            .getDisplayName(), "cmis:document"));

      TypeDefinition newType =
         new TypeDefinition("cmis:objecttype15", BaseType.FOLDER, "cmis:objecttype15", "cmis:objecttype15", "",
            "cmis:folder", "cmis:objecttype15", "cmis:objecttype15", true, false, true, true, false, false, false,
            true, null, null, ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
      typeID = getStorage().addType(newType);
      newType = getStorage().getTypeDefinition(typeID, true);

      FolderData f1 = getStorage().createFolder(testroot, newType, properties, null, null);
      try
      {
         docId =
            getConnection().createFolder(f1.getObjectId(),
               getPropsMap(CmisConstants.FOLDER, "testCreateFolder_ConstraintExceptionNotAllowedChild2"), null, null,
               null);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
         if (f1 != null)
            getStorage().deleteObject(f1, true);
         if (docId != null)
            getStorage().deleteObject(getStorage().getObjectById(docId), true);
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
   public void testCreateFolder_ConstraintExceptionNotControllablePolicy() throws Exception
   {
      if (!IS_POLICIES_SUPPORTED)
      {
         //SKIP
         return;
      }
      String docId = null;
      PolicyData policy = null;
      String typeID = null;

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
         .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(),
         "cmis:objecttype16"));

      TypeDefinition newType =
         new TypeDefinition("cmis:objecttype16", BaseType.FOLDER, "cmis:objecttype16", "cmis:objecttype16", "",
            "cmis:folder", "cmis:objecttype16", "cmis:objecttype16", true, false, true, true, false, false, false,
            true, null, null, ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
      typeID = getStorage().addType(newType);
      newType = getStorage().getTypeDefinition(typeID, true);

      policy = createPolicy(testroot, "policy1");

      ArrayList<String> policies = new ArrayList<String>();
      policies.add(policy.getObjectId());
      try
      {
         docId = getConnection().createFolder(testroot.getObjectId(), properties, null, null, policies);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
         if (docId != null)
            getStorage().deleteObject(getStorage().getObjectById(docId), true);
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
   public void testCreateFolder_ConstraintExceptionNotControllableACL() throws Exception
   {
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         //SKIP
         return;
      }
      String docId = null;
      String typeID = null;

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
         fPropDefName.getLocalName(), fPropDefName.getDisplayName(),
         "testCreateFolder_ConstraintExceptionNotControllableACL"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
         .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(),
         "cmis:objecttype16"));

      TypeDefinition newType =
         new TypeDefinition("cmis:objecttype16", BaseType.FOLDER, "cmis:objecttype16", "cmis:objecttype16", "",
            "cmis:folder", "cmis:objecttype16", "cmis:objecttype16", true, false, true, true, false, false, false,
            true, null, null, ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
      typeID = getStorage().addType(newType);
      newType = getStorage().getTypeDefinition(typeID, true);

      String username = "username";
      List<AccessControlEntry> addACL = createACL(username, "cmis:read");
      try
      {
         docId = getConnection().createFolder(testroot.getObjectId(), properties, addACL, null, null);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
         if (docId != null)
            getStorage().deleteObject(getStorage().getObjectById(docId), true);
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
   public void testCreateFolder_ConstraintExceptionUnknownACE() throws Exception
   {
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         //SKIP
         return;
      }
      String docId = null;
      String typeID = null;

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
         fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "testCreateFolder_ConstraintExceptionUnknownACE"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
         .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(),
         "cmis:objecttype17"));

      TypeDefinition newType =
         new TypeDefinition("cmis:objecttype17", BaseType.FOLDER, "cmis:objecttype17", "cmis:objecttype17", "",
            "cmis:folder", "cmis:objecttype17", "cmis:objecttype17", true, false, true, true, false, false, true, true,
            null, null, ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
      typeID = getStorage().addType(newType);
      newType = getStorage().getTypeDefinition(typeID, true);

      String username = "username";
      List<AccessControlEntry> addACL = createACL(username, "cmis:unknown");

      try
      {
         docId = getConnection().createFolder(testroot.getObjectId(), properties, addACL, null, null);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK 
      }
      finally
      {
         if (docId != null)
            getStorage().deleteObject(getStorage().getObjectById(docId), true);
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
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }

      ObjectData obj = null;
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition,
            getPropsMap(CmisConstants.DOCUMENT, "testCreateRelationship_Simple1"), cs, null, null,
            VersioningState.MAJOR);
      DocumentData doc2 =
         getStorage().createDocument(testroot, documentTypeDefinition,
            getPropsMap(CmisConstants.DOCUMENT, "testCreateRelationship_Simple2"), cs, null, null,
            VersioningState.MAJOR);

      Map<String, Property<?>> props = getPropsMap("cmis:relationship", "objecttest_rel1");

      org.xcmis.spi.model.PropertyDefinition<?> fPropDefSource =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.SOURCE_ID, PropertyType.ID,
            CmisConstants.SOURCE_ID, CmisConstants.SOURCE_ID, null, CmisConstants.SOURCE_ID, true, false, false, false,
            false, Updatability.READONLY, "SourceId", true, null, null);

      org.xcmis.spi.model.PropertyDefinition<?> fPropDefTarget =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.TARGET_ID, PropertyType.ID,
            CmisConstants.TARGET_ID, CmisConstants.TARGET_ID, null, CmisConstants.TARGET_ID, false, false, false,
            false, false, Updatability.READONLY, "TargetId", null, null, null);

      props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefSource.getId(), fPropDefSource.getQueryName(),
         fPropDefSource.getLocalName(), fPropDefSource.getDisplayName(), doc1.getObjectId()));
      props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
         fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));

      String docId = getConnection().createRelationship(props, null, null, null);
      obj = getStorage().getObjectById(docId);
      assertTrue("Cmis object types does not match;", obj.getTypeId().equals("cmis:relationship"));
      assertTrue("Cmis objects ID does not match;", doc1.getObjectId().equals(((RelationshipData)obj).getSourceId()));
      assertTrue("Cmis object ID  does not match;", doc2.getObjectId().equals(((RelationshipData)obj).getTargetId()));
   }

   /**
    * 2.2.4.4.1
    * A list of policy IDs that MUST be applied to the newly-created Replationship object.
    * @throws Exception
    */
   @Test
   public void testCreateRelationship_ApplyPolicy() throws Exception
   {
      if (!IS_POLICIES_SUPPORTED || !IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }
      ObjectData obj = null;
      PolicyData policy = null;
      String typeID = null;
      try
      {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition,
               getPropsMap(CmisConstants.DOCUMENT, "testCreateRelationship_ApplyPolicy1"), cs, null, null,
               VersioningState.MAJOR);
         DocumentData doc2 =
            getStorage().createDocument(testroot, documentTypeDefinition,
               getPropsMap(CmisConstants.DOCUMENT, "testCreateRelationship_ApplyPolicy2"), cs, null, null,
               VersioningState.MAJOR);

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
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "objecttest_rel2"));

         props.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(),
            "cmis:objecttype22"));

         props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefSource.getId(), fPropDefSource.getQueryName(),
            fPropDefSource.getLocalName(), fPropDefSource.getDisplayName(), doc1.getObjectId()));
         props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
            fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));

         TypeDefinition newType =
            new TypeDefinition("cmis:objecttype22", BaseType.RELATIONSHIP, "cmis:objecttype22", "cmis:objecttype22",
               "", "cmis:relationship", "cmis:objecttype22", "cmis:objecttype22", true, false, true, true, false, true,
               false, false, null, null, ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         policy = createPolicy(testroot, "object_policy6");
         ArrayList<String> policies = new ArrayList<String>();
         policies.add(policy.getObjectId());
         String docId = getConnection().createRelationship(props, null, null, policies);
         obj = getStorage().getObjectById(docId);
         assertTrue("Object policies size is incorrect;", obj.getPolicies().size() == 1);
         Iterator<PolicyData> it = obj.getPolicies().iterator();
         while (it.hasNext())
         {
            PolicyData one = it.next();
            assertTrue("POlicy names does not match", one.getName().equals("object_policy6"));
            assertTrue("Policy text does not match", one.getPolicyText().equals("testPolicyText"));
            obj.removePolicy(one);
         }
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         if (policy != null)
            getStorage().deleteObject(policy, true);
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
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         //SKIP
         return;
      }

      ObjectData obj = null;
      String typeID = null;
      try
      {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition,
               getPropsMap(CmisConstants.DOCUMENT, "testCreateRelationship_AddACL1"), cs, null, null,
               VersioningState.MAJOR);
         DocumentData doc2 =
            getStorage().createDocument(testroot, documentTypeDefinition,
               getPropsMap(CmisConstants.DOCUMENT, "testCreateRelationship_AddACL2"), cs, null, null,
               VersioningState.MAJOR);

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
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "objecttest_rel3"));

         props.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(),
            "cmis:objecttype18"));

         props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefSource.getId(), fPropDefSource.getQueryName(),
            fPropDefSource.getLocalName(), fPropDefSource.getDisplayName(), doc1.getObjectId()));
         props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
            fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));

         TypeDefinition newType =
            new TypeDefinition("cmis:objecttype18", BaseType.RELATIONSHIP, "cmis:objecttype18", "cmis:objecttype18",
               "", "cmis:relationship", "cmis:objecttype18", "cmis:objecttype18", true, false, true, true, false, true,
               true, false, null, null, ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         String username = "username";
         List<AccessControlEntry> addACL = createACL(username, "cmis:read");

         String docId = getConnection().createRelationship(props, addACL, null, null);
         obj = getStorage().getObjectById(docId);
         for (AccessControlEntry one : obj.getACL(false))
         {
            if (one.getPrincipal().equalsIgnoreCase(username))
            {
               assertTrue("Permissions size is incorrect", one.getPermissions().size() == 1);
               assertTrue("Permissions does not match", one.getPermissions().contains("cmis:read"));
            }
         }
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
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
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }
      ObjectData obj = null;

      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition,
            getPropsMap(CmisConstants.DOCUMENT, "testCreateRelationship_NameConstraintViolationException1"), cs, null,
            null, VersioningState.MAJOR);
      DocumentData doc2 =
         getStorage().createDocument(testroot, documentTypeDefinition,
            getPropsMap(CmisConstants.DOCUMENT, "testCreateRelationship_NameConstraintViolationException2"), cs, null,
            null, VersioningState.MAJOR);
      Map<String, Property<?>> props = new HashMap<String, Property<?>>();

      Map<String, Property<?>> props2 = new HashMap<String, Property<?>>();
      org.xcmis.spi.model.PropertyDefinition<?> fPropDefName1 =
         PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
            CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
            "rel", true, null, null);
      props2.put(CmisConstants.NAME, new StringProperty(fPropDefName1.getId(), fPropDefName1.getQueryName(),
         fPropDefName1.getLocalName(), fPropDefName1.getDisplayName(), "objecttest_rel4"));

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
         .getLocalName(), fPropDefName.getDisplayName(), "objecttest_rel4"));

      props.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
         .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(),
         "cmis:relationship"));

      props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefSource.getId(), fPropDefSource.getQueryName(),
         fPropDefSource.getLocalName(), fPropDefSource.getDisplayName(), doc1.getObjectId()));
      props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
         fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));
      try
      {
         String docId = getConnection().createRelationship(props, null, null, null);
         obj = getStorage().getObjectById(docId);
         assertFalse("Names must not match;", obj.getName().equals("objecttest_rel4"));
      }
      catch (NameConstraintViolationException ex)
      {
         //OK
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
      }
   }

   /**
    * 2.2.4.4.3
    * The cmis:objectTypeId property value is not an Object-Type whose baseType is “Relationship”.
    * @throws Exception
    */
   @Test
   public void testCreateRelationship_ConstraintExceptionWrongBaseType() throws Exception
   {
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }
      ObjectData obj = null;

      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition,
            getPropsMap(CmisConstants.DOCUMENT, "testCreateRelationship_ConstraintExceptionWrongBaseType1"), cs, null,
            null, VersioningState.MAJOR);
      DocumentData doc2 =
         getStorage().createDocument(testroot, documentTypeDefinition,
            getPropsMap(CmisConstants.DOCUMENT, "testCreateRelationship_ConstraintExceptionWrongBaseType2"), cs, null,
            null, VersioningState.MAJOR);

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
         .getLocalName(), fPropDefName.getDisplayName(), "objecttest_rel5"));

      props.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
         .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(),
         CmisConstants.POLICY));

      props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefSource.getId(), fPropDefSource.getQueryName(),
         fPropDefSource.getLocalName(), fPropDefSource.getDisplayName(), doc1.getObjectId()));
      props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
         fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));

      try
      {
         String docId = getConnection().createRelationship(props, null, null, null);
         obj = getStorage().getObjectById(docId);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
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
   public void testCreateRelationship_ConstraintExceptionNotAllowedTypes() throws Exception
   {
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }

      ObjectData obj = null;
      String typeID = null;
      try
      {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition,
               getPropsMap(CmisConstants.DOCUMENT, "testCreateRelationship_ConstraintExceptionNotAllowedTypes1"), cs,
               null, null, VersioningState.MAJOR);
         DocumentData doc2 =
            getStorage().createDocument(testroot, documentTypeDefinition,
               getPropsMap(CmisConstants.DOCUMENT, "testCreateRelationship_ConstraintExceptionNotAllowedTypes2"), cs,
               null, null, VersioningState.MAJOR);

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

         props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefSource.getId(), fPropDefSource.getQueryName(),
            fPropDefSource.getLocalName(), fPropDefSource.getDisplayName(), doc1.getObjectId()));
         props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
            fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));
         String[] allowed = {"cmis:folder"};

         TypeDefinition newType =
            new TypeDefinition("cmis:my", BaseType.RELATIONSHIP, "cmis:my", "cmis:my", "", "cmis:relationship",
               "cmis:my", "cmis:my", true, false, true, true, false, true, true, false, allowed, allowed,
               ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         String docId = getConnection().createRelationship(props, null, null, null);
         obj = getStorage().getObjectById(docId);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
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
   public void testCreateRelationship_ConstraintExceptionNotControllablePolicy() throws Exception
   {
      if (!IS_POLICIES_SUPPORTED || !IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }
      ObjectData obj = null;
      String typeID = null;
      PolicyData policy = null;

      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition,
            getPropsMap(CmisConstants.DOCUMENT, "testCreateRelationship_ConstraintExceptionNotControllablePolicy1"),
            cs, null, null, VersioningState.MAJOR);
      DocumentData doc2 =
         getStorage().createDocument(testroot, documentTypeDefinition,
            getPropsMap(CmisConstants.DOCUMENT, "testCreateRelationship_ConstraintExceptionNotControllablePolicy2"),
            cs, null, null, VersioningState.MAJOR);

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
         .getLocalName(), fPropDefName.getDisplayName(), "objecttest_rel7"));

      props.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
         .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(),
         "cmis:objecttype32"));

      props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefSource.getId(), fPropDefSource.getQueryName(),
         fPropDefSource.getLocalName(), fPropDefSource.getDisplayName(), doc1.getObjectId()));
      props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
         fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));

      TypeDefinition newType =
         new TypeDefinition("cmis:objecttype32", BaseType.RELATIONSHIP, "cmis:objecttype32", "cmis:objecttype32", "",
            "cmis:relationship", "cmis:objecttype32", "cmis:objecttype32", true, false, true, true, false, false,
            false, false, null, null, ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
      typeID = getStorage().addType(newType);
      newType = getStorage().getTypeDefinition(typeID, true);

      policy = createPolicy(testroot, "objecttest_policy6");
      ArrayList<String> policies = new ArrayList<String>();
      policies.add(policy.getObjectId());
      try
      {
         String docId = getConnection().createRelationship(props, null, null, policies);
         obj = getStorage().getObjectById(docId);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException xe)
      {
         //OK 
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         if (policy != null)
            getStorage().deleteObject(policy, true);
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
   public void testCreateRelationship_ConstraintExceptionNotControllableACL() throws Exception
   {
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         //SKIP
         return;
      }

      ObjectData obj = null;
      String typeID = null;
      try
      {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition,
               getPropsMap(CmisConstants.DOCUMENT, "testCreateRelationship_ConstraintExceptionNotControllableACL1"),
               cs, null, null, VersioningState.MAJOR);
         DocumentData doc2 =
            getStorage().createDocument(testroot, documentTypeDefinition,
               getPropsMap(CmisConstants.DOCUMENT, "testCreateRelationship_ConstraintExceptionNotControllableACL2"),
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
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "objecttest_rel8"));

         props.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(),
            "cmis:objecttype21"));

         props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefSource.getId(), fPropDefSource.getQueryName(),
            fPropDefSource.getLocalName(), fPropDefSource.getDisplayName(), doc1.getObjectId()));
         props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
            fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));

         TypeDefinition newType =
            new TypeDefinition("cmis:objecttype21", BaseType.RELATIONSHIP, "cmis:objecttype21", "cmis:objecttype21",
               "", "cmis:relationship", "cmis:objecttype21", "cmis:objecttype21", true, false, true, true, false,
               false, false, false, null, null, ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         String username = "username";
         List<AccessControlEntry> addACL = createACL(username, "cmis:read");

         String docId = getConnection().createRelationship(props, addACL, null, null);
         obj = getStorage().getObjectById(docId);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException e)
      {
         //OK
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
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
   public void testCreateRelationship_ConstraintExceptionUnknownACE() throws Exception
   {
      if (!IS_RELATIONSHIPS_SUPPORTED)
      {
         //SKIP
         return;
      }
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         //SKIP
         return;
      }

      ObjectData obj = null;
      String typeID = null;
      try
      {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition,
               getPropsMap(CmisConstants.DOCUMENT, "testCreateRelationship_ConstraintExceptionUnknownACE1"), cs, null,
               null, VersioningState.MAJOR);
         DocumentData doc2 =
            getStorage().createDocument(testroot, documentTypeDefinition,
               getPropsMap(CmisConstants.DOCUMENT, "testCreateRelationship_ConstraintExceptionUnknownACE2"), cs, null,
               null, VersioningState.MAJOR);

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
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "objecttest_rel9"));

         props.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(),
            "cmis:objecttype22"));

         props.put(CmisConstants.SOURCE_ID, new IdProperty(fPropDefSource.getId(), fPropDefSource.getQueryName(),
            fPropDefSource.getLocalName(), fPropDefSource.getDisplayName(), doc1.getObjectId()));
         props.put(CmisConstants.TARGET_ID, new IdProperty(fPropDefTarget.getId(), fPropDefTarget.getQueryName(),
            fPropDefTarget.getLocalName(), fPropDefTarget.getDisplayName(), doc2.getObjectId()));

         TypeDefinition newType =
            new TypeDefinition("cmis:objecttype22", BaseType.RELATIONSHIP, "cmis:objecttype22", "cmis:objecttype22",
               "", "cmis:relationship", "cmis:objecttype22", "cmis:objecttype22", true, false, true, true, false,
               false, true, false, null, null, ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);

         String username = "username";
         List<AccessControlEntry> addACL = createACL(username, "cmis:unknown");

         String docId = getConnection().createRelationship(props, addACL, null, null);
         obj = getStorage().getObjectById(docId);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException e)
      {
         //OK
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
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
      if (!IS_POLICIES_SUPPORTED)
      {
         //SKIP
         return;
      }
      ObjectData obj = null;
      try
      {
         org.xcmis.spi.model.PropertyDefinition<?> def =
            PropertyDefinitions.getPropertyDefinition(CmisConstants.POLICY, CmisConstants.POLICY_TEXT);
         Map<String, Property<?>> properties = getPropsMap(CmisConstants.POLICY, "testCreatePolicy_Simple");
         properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def.getId(), def.getQueryName(), def
            .getLocalName(), def.getDisplayName(), "testPolicyText"));

         String docId = getConnection().createPolicy(testroot.getObjectId(), properties, null, null, null);
         obj = getStorage().getObjectById(docId);
         assertTrue("Cmis object types does not match", obj.getTypeId().equals(CmisConstants.POLICY));
         assertTrue("Cmis policy text does not match", ((PolicyData)obj).getPolicyText().equals("testPolicyText"));
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
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
      if (!IS_POLICIES_SUPPORTED)
      {
         //SKIP
         return;
      }

      ObjectData obj = null;
      PolicyData policy = null;
      try
      {
         org.xcmis.spi.model.PropertyDefinition<?> def =
            PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);
         Map<String, Property<?>> properties = getPropsMap(CmisConstants.POLICY, "testCreatePolicy_AddPolicy");
         properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def.getId(), def.getQueryName(), def
            .getLocalName(), def.getDisplayName(), "testPolicyText1"));

         policy = createPolicy(testroot, "testCreatePolicy_AddPolicy1");

         ArrayList<String> policies = new ArrayList<String>();
         policies.add(policy.getObjectId());
         String docId = getConnection().createPolicy(testroot.getObjectId(), properties, null, null, policies);
         obj = getStorage().getObjectById(docId);
         Iterator<PolicyData> it = obj.getPolicies().iterator();
         while (it.hasNext())
         {
            PolicyData one = it.next();
            assertTrue("Policy names does not match;", one.getName().equals("testCreatePolicy_AddPolicy1"));
            assertTrue("Policy text does not match", one.getPolicyText().equals("testPolicyText"));
            obj.removePolicy(one);
         }
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         if (policy != null)
            getStorage().deleteObject(policy, true);
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
      if (!IS_POLICIES_SUPPORTED || getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         //SKIP
         return;
      }
      ObjectData obj = null;
      try
      {
         org.xcmis.spi.model.PropertyDefinition<?> def =
            PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);
         Map<String, Property<?>> properties = getPropsMap("cmis:policy", "testCreatePolicy_AddACL");
         properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def.getId(), def.getQueryName(), def
            .getLocalName(), def.getDisplayName(), "testPolicyText"));

         String username = "username";
         List<AccessControlEntry> addACL = createACL(username, "cmis:read");

         String docId = getConnection().createPolicy(testroot.getObjectId(), properties, addACL, null, null);
         obj = getStorage().getObjectById(docId);
         for (AccessControlEntry one : obj.getACL(false))
         {
            if (one.getPrincipal().equalsIgnoreCase(username))
            {
               assertTrue("Permissions size is incorrect", one.getPermissions().size() == 1);
               assertTrue("Permissions does not match", one.getPermissions().contains("cmis:read"));
            }
         }
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
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
      if (!IS_POLICIES_SUPPORTED)
      {
         //SKIP
         return;
      }
      ObjectData obj = null;
      PolicyData policy = null;
      String policyname = "testCreatePolicy_NameConstraint";
      try
      {
         org.xcmis.spi.model.PropertyDefinition<?> def =
            PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);
         Map<String, Property<?>> properties = getPropsMap("cmis:policy", policyname);
         properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def.getId(), def.getQueryName(), def
            .getLocalName(), def.getDisplayName(), "testPolicyText1"));
         policy = createPolicy(testroot, policyname);
         obj = null;
         String docId = getConnection().createPolicy(testroot.getObjectId(), properties, null, null, null);
         obj = getStorage().getObjectById(docId);
         assertFalse("Names must not match;", obj.getName().equals(policyname));
      }
      catch (NameConstraintViolationException ex)
      {
         //OK
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
         if (policy != null)
            getStorage().deleteObject(policy, true);
      }
   }

   /**
    * 2.2.4.3.3
    * The Repository MUST throw this exception if  to  The cmis:objectTypeId 
    * property value is not an Object-Type whose baseType is “Policy”.
    * @throws Exception
    */
   @Test
   public void testCreatePolicy_ConstraintExceptionWrongBaseType() throws Exception
   {
      if (!IS_POLICIES_SUPPORTED)
      {
         //SKIP
         return;
      }
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
         fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "testCreatePolicy_ConstraintExceptionWrongBase"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
         .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(),
         "cmis:relationship"));
      properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def.getId(), def.getQueryName(), def.getLocalName(),
         def.getDisplayName(), "testPolicyText1"));

      try
      {
         getConnection().createPolicy(testroot.getObjectId(), properties, null, null, null);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
   }

   /**
    * 2.2.4.5.3
    * The cmis:objectTypeId property value is NOT in the list of AllowedChildObjectTypeIds 
    * of the parent-folder specified by folderId. 
    * @throws Exception
    */
   @Test
   public void testCreatePolicy_ConstraintExceptionNotAllowedChild() throws Exception
   {
      if (!IS_POLICIES_SUPPORTED)
      {
         //SKIP
         return;
      }
      String typeID = null;
      ObjectData obj = null;
      try
      {
         Map<String, Property<?>> folderprops = getPropsMap(CmisConstants.FOLDER, "testroot2");

         org.xcmis.spi.model.PropertyDefinition<?> def =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, PropertyType.ID,
               CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, null,
               CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, false, false, false, false, false, Updatability.READONLY,
               "allowed", null, null, null);
         folderprops.put(CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, new IdProperty(def.getId(), def.getQueryName(),
            def.getLocalName(), def.getDisplayName(), CmisConstants.DOCUMENT));

         FolderData testroot2 = getStorage().createFolder(testroot, folderTypeDefinition, folderprops, null, null);

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
         properties.put(CmisConstants.NAME,
            new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(), fPropDefName.getLocalName(),
               fPropDefName.getDisplayName(), "estCreatePolicy_ConstraintExceptionNotAllowed"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(),
            "cmis:mypolicy"));
         properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def2.getId(), def2.getQueryName(), def2
            .getLocalName(), def2.getDisplayName(), "testPolicyText1"));

         TypeDefinition newType =
            new TypeDefinition("cmis:mypolicy", BaseType.POLICY, "cmis:mypolicy", "cmis:mypolicy", "", "cmis:policy",
               "cmis:mypolicy", "cmis:mypolicy", true, false, true, true, false, false, false, true, null, null,
               ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         String docId = getConnection().createPolicy(testroot2.getObjectId(), properties, null, null, null);
         obj = getStorage().getObjectById(docId);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
         if (obj != null)
            getStorage().deleteObject(obj, true);
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
   public void testCreatePolicy_ConstraintExceptionNotControllablePolicy() throws Exception
   {
      if (!IS_POLICIES_SUPPORTED)
      {
         //SKIP
         return;
      }
      String typeID = null;
      try
      {
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
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(),
            "testCreatePolicy_ConstraintExceptionNotControllable"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(),
            "cmis:objecttype24"));
         properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def2.getId(), def2.getQueryName(), def2
            .getLocalName(), def2.getDisplayName(), "testPolicyText1"));

         TypeDefinition newType =
            new TypeDefinition("cmis:objecttype24", BaseType.FOLDER, "cmis:objecttype24", "cmis:objecttype24", "",
               "cmis:folder", "cmis:objecttype24", "cmis:objecttype24", true, false, true, true, false, false, false,
               false, null, null, ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         PolicyData policy = createPolicy(testroot, "testCreatePolicy_ConstraintExceptionNotControllable2");

         ArrayList<String> policies = new ArrayList<String>();
         policies.add(policy.getObjectId());

         getConnection().createPolicy(testroot.getObjectId(), properties, null, null, policies);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
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
   public void testCreatePolicy_ConstraintExceptionNotControllableACL() throws Exception
   {
      if (!IS_POLICIES_SUPPORTED || getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         //SKIP
         return;
      }
      String typeID = null;
      try
      {
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
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(),
            "testCreatePolicy_ConstraintExceptionNotControllable"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefObjectTypeId.getId(), fPropDefObjectTypeId
            .getQueryName(), fPropDefObjectTypeId.getLocalName(), fPropDefObjectTypeId.getDisplayName(),
            "cmis:objecttype25"));
         properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def2.getId(), def2.getQueryName(), def2
            .getLocalName(), def2.getDisplayName(), "testPolicyText1"));

         TypeDefinition newType =
            new TypeDefinition("cmis:objecttype25", BaseType.FOLDER, "cmis:objecttype25", "cmis:objecttype25", "",
               "cmis:folder", "cmis:objecttype25", "cmis:objecttype25", true, false, true, true, false, false, false,
               false, null, null, ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         String username = "username";
         List<AccessControlEntry> addACL = createACL(username, "cmis:read");
         getConnection().createPolicy(testroot.getObjectId(), properties, addACL, null, null);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.4.5.3
    * At least one of the permissions is used in an ACE provided which is not supported by the repository. 
    * @throws Exception
    */
   @Test
   public void testCreatePolicy_ConstraintExceptionUnknownACE() throws Exception
   {
      if (!IS_POLICIES_SUPPORTED || getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         //SKIP
         return;
      }
      try
      {
         org.xcmis.spi.model.PropertyDefinition<?> def =
            PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);
         Map<String, Property<?>> properties =
            getPropsMap("cmis:policy", "testCreatePolicy_ConstraintExceptionUnknownACE");
         properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def.getId(), def.getQueryName(), def
            .getLocalName(), def.getDisplayName(), "testPolicyText"));

         String username = "username";
         List<AccessControlEntry> addACL = createACL(username, "cmis:unknown");
         getConnection().createPolicy(testroot.getObjectId(), properties, addACL, null, null);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
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
      AllowableActions actions = getConnection().getAllowableActions(testroot.getObjectId());
      assertNotNull("Allowable actions is null;", actions);
   }

   /**
    * 2.2.4.7
    * Gets the specified information for the Object. 
    * @throws Exception
    */
   @Test
   public void testGetObject_Simple() throws Exception
   {
      CmisObject obj =
         getConnection().getObject(testroot.getObjectId(), false, IncludeRelationships.NONE, false, false, true, "",
            "*");
      assertTrue("Names does not match;", obj.getObjectInfo().getName().equals("testroot"));
      assertTrue("Object ID's does not match;", testroot.getObjectId().equals(obj.getObjectInfo().getId()));
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
      CmisObject obj =
         getConnection().getObject(testroot.getObjectId(), false, IncludeRelationships.NONE, false, false, false,
            "cmis:name,cmis:path", "*");
      for (Map.Entry<String, Property<?>> e : obj.getProperties().entrySet())
      {
         if (e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path")) //Other props must be ignored
            continue;
         else
            fail("Property filter does not work;");
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

      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition,
            getPropsMap(CmisConstants.DOCUMENT, "testGetObject_IncludeRelationships1"), cs, null, null,
            VersioningState.MAJOR);

      RelationshipData reldata =
         getStorage().createRelationship(doc1, testroot, relationshipTypeDefinition,
            getPropsMap("cmis:relationship", "testGetObject_IncludeRelationships_rel1"), null, null);

      CmisObject obj =
         getConnection().getObject(testroot.getObjectId(), false, IncludeRelationships.TARGET, false, false, true, "",
            "*");
      assertTrue("Relationships count is incorrect;", obj.getRelationship().size() == 1);
      for (CmisObject e : obj.getRelationship())
      {
         assertTrue("Object ID's does not match;", reldata.getObjectId().equals(e.getObjectInfo().getId()));
      }
      getStorage().deleteObject(reldata, true);
   }

   /**
    * 2.2.4.7
    * The Repository MUST return the Ids of the policies applied to the object.  Defaults to FALSE.
    * @throws Exception
    */
   @Test
   public void testGetObject_IncludePolicyIDs() throws Exception
   {
      if (!IS_POLICIES_SUPPORTED)
      {
         //SKIP
         return;
      }
      PolicyData policy = null;
      FolderData folder2 = null;
      try
      {
         folder2 =
            getStorage().createFolder(rootFolder, folderTypeDefinition,
               getPropsMap(CmisConstants.FOLDER, "testGetObject_IncludePolicyIDsF"), null, null);

         policy = createPolicy(folder2, "testGetObject_IncludePolicyID");
         getConnection().applyPolicy(policy.getObjectId(), folder2.getObjectId());
         CmisObject obj =
            getConnection().getObject(folder2.getObjectId(), false, IncludeRelationships.TARGET, true, false, true, "",
               "*");
         assertTrue("Policy count is incorrect;", obj.getPolicyIds().size() == 1);
         for (String e : obj.getPolicyIds())
         {
            assertTrue("Object ID's does not match;", policy.getObjectId().equals(e));
         }
      }
      finally
      {
         getStorage().deleteObject(folder2, true);
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
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         //SKIP
         return;
      }
      String username = "username";
      List<AccessControlEntry> addACL = createACL(username, "cmis:read");

      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition,
            getPropsMap(CmisConstants.DOCUMENT, "testGetObject_IncludeACLs1"), cs, addACL, null, VersioningState.MAJOR);

      CmisObject obj =
         getConnection().getObject(doc1.getObjectId(), false, IncludeRelationships.TARGET, true, true, true, "", "*");
      for (AccessControlEntry one : obj.getACL())
      {
         if (one.getPrincipal().equalsIgnoreCase(username))
         {
            assertTrue("Permissions size is incorrect", one.getPermissions().size() == 1);
            assertTrue("Permissions does not match", one.getPermissions().contains("cmis:read"));
         }
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
      CmisObject obj =
         getConnection().getObject(testroot.getObjectId(), true, IncludeRelationships.TARGET, false, false, true, "",
            "*");
      AllowableActions actions = obj.getAllowableActions();
      assertNotNull("AllowableActions is null;", actions);
   }

   /**
    * 2.2.4.7.3
    * The Repository MUST throw this exception if this property filter input parameter is not valid.
    * @throws Exception
    */
   @Test
   public void testGetObject_FilterNotValidException() throws Exception
   {
      try
      {
         getConnection().getObject(testroot.getObjectId(), false, IncludeRelationships.NONE, false, false, false,
            "(,*", "*");
         fail("FilterNotValidException must be thrown;");
      }
      catch (FilterNotValidException ex)
      {
         //OK 
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
      CmisObject obj = getConnection().getProperties(testroot.getObjectId(), true, "cmis:name,cmis:path");
      assertNotNull("Get properties result is null", obj);
      for (Map.Entry<String, Property<?>> e : obj.getProperties().entrySet())
      {
         if (e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path")) //Other props must be ignored
            continue;
         else
            fail("Property filter does not work;");
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
      try
      {
         getConnection().getProperties(testroot.getObjectId(), true, "(,*");
         fail("FilterNotValidException must be thrown;");
      }
      catch (FilterNotValidException ex)
      {

      }
   }

   /**
    * 2.2.4.9.1
    * Gets the specified object. 
    * @throws Exception
    */
   @Test
   public void testGetObjectByPath_Simple() throws Exception
   {
      CmisObject obj =
         getConnection().getObjectByPath("/object_testroot", false, IncludeRelationships.NONE, false, false, true, "",
            "*");
      assertTrue("Names does not match;", obj.getObjectInfo().getName().equals("object_testroot"));
      assertTrue("Object ID's does not match;", testroot.getObjectId().equals(obj.getObjectInfo().getId()));
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
      CmisObject obj =
         getConnection().getObjectByPath("/object_testroot", false, IncludeRelationships.NONE, false, false, false,
            "cmis:name,cmis:path", "*");
      for (Map.Entry<String, Property<?>> e : obj.getProperties().entrySet())
      {
         if (e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path")) //Other props must be ignored
            continue;
         else
            fail("Property filter does not work;");
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
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      FolderData folder2 =
         getStorage().createFolder(testroot, folderTypeDefinition,
            getPropsMap(CmisConstants.FOLDER, "testGetObjectByPathRelationships_folder"), null, null);

      DocumentData doc1 =
         getStorage().createDocument(folder2, documentTypeDefinition,
            getPropsMap(CmisConstants.DOCUMENT, "testGetObjectByPath_IncludeRelationships"), cs, null, null,
            VersioningState.MAJOR);

      RelationshipData reldata =
         getStorage().createRelationship(doc1, folder2, relationshipTypeDefinition,
            getPropsMap("cmis:relationship", "testGetObjectByPath_rel1"), null, null);

      CmisObject obj =
         getConnection().getObjectByPath("/object_testroot/testGetObjectByPathRelationships_folder", false,
            IncludeRelationships.TARGET, false, false, true, "", "*");
      assertTrue("Incorect relationship size;", obj.getRelationship().size() == 1);
      for (CmisObject e : obj.getRelationship())
      {
         assertTrue("Object ID's does not match;", reldata.getObjectId().equals(e.getObjectInfo().getId()));
      }
      getStorage().deleteObject(reldata, true);
   }

   /**
    * 2.2.4.9.1
    * The Repository MUST return the Ids of the policies applied to the object.  Defaults to FALSE.
    * @throws Exception
    */
   @Test
   public void testGetObjectByPath_IncludePolicyIDs() throws Exception
   {
      if (!IS_POLICIES_SUPPORTED)
      {
         //SKIP
         return;
      }
      PolicyData policy = null;
      FolderData folder2 =
         getStorage().createFolder(testroot, folderTypeDefinition,
            getPropsMap(CmisConstants.FOLDER, "testGetObjectByPath_IncludePolicyIDs"), null, null);

      try
      {
         policy = createPolicy(folder2, "testGetObjectByPath_IncludePolicy");
         getConnection().applyPolicy(policy.getObjectId(), folder2.getObjectId());
         CmisObject obj =
            getConnection().getObjectByPath("/object_testroot/testGetObjectByPath_IncludePolicyIDs", false,
               IncludeRelationships.TARGET, true, false, true, "", "*");
         assertTrue("Incorect policyIds size;", obj.getPolicyIds().size() == 1);
         for (String e : obj.getPolicyIds())
         {
            assertTrue("Object ID's does not match; ", policy.getObjectId().equals(e));
         }
      }
      finally
      {
         getStorage().deleteObject(folder2, true);
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
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         //SKIP
         return;
      }
      String username = "username";
      List<AccessControlEntry> addACL = createACL(username, "cmis:read");

      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition,
            getPropsMap(CmisConstants.DOCUMENT, "testGetObjectByPath_IncludeACLs"), cs, addACL, null,
            VersioningState.MAJOR);

      CmisObject obj =
         getConnection().getObjectByPath("/object_testroot/testGetObjectByPath_IncludeACLs", false,
            IncludeRelationships.TARGET, true, true, true, "", "*");
      for (AccessControlEntry one : obj.getACL())
      {
         if (one.getPrincipal().equalsIgnoreCase(username))
         {
            assertTrue("Permissions size is incorrect", one.getPermissions().size() == 1);
            assertTrue("Permissions does not match", one.getPermissions().contains("cmis:read"));
         }
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
      CmisObject obj =
         getConnection().getObjectByPath("/object_testroot", true, IncludeRelationships.TARGET, false, false, true, "",
            "*");
      AllowableActions actions = obj.getAllowableActions();
      assertNotNull("AllowableActions must not be null", actions);
   }

   /**
    * 2.2.4.9.3
    * The Repository MUST throw this exception if this property filter input parameter is not valid.
    * @throws Exception
    */
   @Test
   public void testGetObjectByPath_FilterNotValidException() throws Exception
   {
      try
      {
         getConnection().getObject("/object_testroot", false, IncludeRelationships.NONE, false, false, false, "(,*",
            "*");
         fail("FilterNotValidException must be thrown;");
      }
      catch (FilterNotValidException ex)
      {
         //OK
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

      byte[] before = new byte[15];
      before = "1234567890aBcDE".getBytes();

      ContentStream cs = new BaseContentStream(before, null, new MimeType("text", "plain"));
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition,
            getPropsMap(CmisConstants.DOCUMENT, "testGetContentStream_Simple"), cs, null, null, VersioningState.MAJOR);
      ContentStream obj = getConnection().getContentStream(doc1.getObjectId(), null);
      byte[] after = new byte[15];
      obj.getStream().read(after);
      assertArrayEquals(before, after);
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
      try
      {
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition,
               getPropsMap(CmisConstants.DOCUMENT, "testGetContentStream_ConstraintException"), null, null, null,
               VersioningState.MAJOR);
         getConnection().getContentStream(doc1.getObjectId(), null);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
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
      if (!getStorage().getRepositoryInfo().getCapabilities().getCapabilityRenditions()
         .equals(CapabilityRendition.READ))
      {
         //SKIP
         return;
      }

      try
      {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition,
               getPropsMap(CmisConstants.DOCUMENT, "testGetRenditions_Simple"), cs, null, null, VersioningState.MAJOR);
         List<Rendition> obj = getConnection().getRenditions(doc1.getObjectId(), "", -1, 0);
         assertNotNull("Get renditions result is null;", obj);
      }
      catch (NotSupportedException ex)
      {
         //SKIP    
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
      if (!getStorage().getRepositoryInfo().getCapabilities().getCapabilityRenditions()
         .equals(CapabilityRendition.READ))
      {
         //SKIP
         return;
      }
      try
      {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition,
               getPropsMap(CmisConstants.DOCUMENT, "testGetRenditions_FilterNotValidException"), cs, null, null,
               VersioningState.MAJOR);
         getConnection().getRenditions(doc1.getObjectId(), "(,*", -1, 0);
         fail("FilterNotValidException must be thrown;");
      }
      catch (FilterNotValidException ex)
      {
         //OK
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
      DocumentData doc1 = null;
      String typeID = null;
      try
      {
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
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "testUpdateProperties_Simple"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefType.getId(), fPropDefType.getQueryName(),
            fPropDefType.getLocalName(), fPropDefType.getDisplayName(), "cmis:objecttype26"));
         properties.put(CmisConstants.CREATED_BY, new StringProperty(fPropDefCreated.getId(), fPropDefCreated
            .getQueryName(), fPropDefCreated.getLocalName(), fPropDefCreated.getDisplayName(), "_anonimous"));

         TypeDefinition newType =
            new TypeDefinition("cmis:objecttype26", BaseType.DOCUMENT, "cmis:objecttype26", "cmis:objecttype26", "",
               "cmis:document", "cmis:objecttype26", "cmis:objecttype26", true, false, true, true, false, false, false,
               false, null, null, ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         doc1 = getStorage().createDocument(testroot, newType, properties, cs, null, null, VersioningState.MAJOR);

         Map<String, Property<?>> properties2 = new HashMap<String, Property<?>>();
         properties2.put(CmisConstants.NAME, new StringProperty(fPropDefName.getId(), fPropDefName.getQueryName(),
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "new1"));

         properties2.put(CmisConstants.CREATED_BY, new StringProperty(fPropDefCreated.getId(), fPropDefCreated
            .getQueryName(), fPropDefCreated.getLocalName(), fPropDefCreated.getDisplayName(), "Makiz"));

         String id = getConnection().updateProperties(doc1.getObjectId(), new ChangeTokenHolder(), properties2);
         ObjectData obj = getStorage().getObjectById(id);
         assertTrue("Names does not match;", obj.getName().equals("new1"));
      }
      finally
      {
         if (doc1 != null)
            getStorage().deleteObject(doc1, true);
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

      try
      {
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
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "testUpdateProperties_VersioningException"));
         properties2.put(CmisConstants.CHECKIN_COMMENT, new StringProperty(fPropDefComment.getId(), fPropDefComment
            .getQueryName(), fPropDefComment.getLocalName(), fPropDefComment.getDisplayName(), "comment"));

         String id = getConnection().updateProperties(doc1.getObjectId(), new ChangeTokenHolder(), properties2);
         ObjectData obj = getStorage().getObjectById(id);
         fail("VersioningException must be thrown;");
      }
      catch (VersioningException ex)
      {
         //OK
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
      FolderData folder2 = null;
      try
      {
         folder2 =
            getStorage().createFolder(rootFolder, folderTypeDefinition,
               getPropsMap(CmisConstants.FOLDER, "testMoveObject_Simple"), null, null);

         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition,
               getPropsMap(CmisConstants.DOCUMENT, "testMoveObject_Simple"), cs, null, null, VersioningState.MAJOR);

         String id = getConnection().moveObject(doc1.getObjectId(), folder2.getObjectId(), testroot.getObjectId());
         ObjectData obj = getStorage().getObjectById(id);
         assertTrue("Names does not match;", folder2.getName().equals(obj.getParent().getName()));
      }
      finally
      {
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

      FolderData folder2 = null;
      try
      {
         folder2 =
            getStorage().createFolder(rootFolder, folderTypeDefinition,
               getPropsMap(CmisConstants.FOLDER, "testMoveObject_InvalidArgumentException"), null, null);

         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition,
               getPropsMap(CmisConstants.DOCUMENT, "testMoveObject_InvalidArgumentException"), cs, null, null,
               VersioningState.MAJOR);

         String id = getConnection().moveObject(doc1.getObjectId(), testroot.getObjectId(), folder2.getObjectId());
         ObjectData obj = getStorage().getObjectById(id);
         fail("InvalidArgumentException must be thrown;");
      }
      catch (InvalidArgumentException ex)
      {
         //OK 
      }
      finally
      {
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
      FolderData folder2 = null;
      String typeID = null;
      try
      {
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
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "testMoveObject_ConstraintExceptionFolder"));
         props2.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefType.getId(), fPropDefType.getQueryName(),
            fPropDefType.getLocalName(), fPropDefType.getDisplayName(), "cmis:objecttype27"));
         props2.put(CmisConstants.ALLOWED_CHILD_OBJECT_TYPE_IDS, new IdProperty(fPropDefAllowedChild.getId(),
            fPropDefAllowedChild.getQueryName(), fPropDefAllowedChild.getLocalName(), fPropDefAllowedChild
               .getDisplayName(), "cmis:folder"));

         Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

         TypeDefinition newType =
            new TypeDefinition("cmis:objecttype27", BaseType.FOLDER, "cmis:objecttype27", "cmis:objecttype27", "",
               "cmis:folder", "cmis:objecttype27", "cmis:objecttype27", true, false, true, true, false, false, false,
               false, null, null, ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         folder2 = getStorage().createFolder(rootFolder, newType, props2, null, null);

         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition,
               getPropsMap(CmisConstants.DOCUMENT, "testMoveObject_ConstraintException"), cs, null, null,
               VersioningState.MAJOR);

         String id = getConnection().moveObject(doc1.getObjectId(), folder2.getObjectId(), testroot.getObjectId());
         ObjectData obj = getStorage().getObjectById(id);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
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
      FolderData folder2 = null;
      try
      {
         folder2 =
            getStorage().createFolder(rootFolder, folderTypeDefinition,
               getPropsMap(CmisConstants.FOLDER, "testMoveObject_NameConstraintException"), null, null);

         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition,
               getPropsMap(CmisConstants.DOCUMENT, "testMoveObject_NameConstraintException"), cs, null, null,
               VersioningState.MAJOR);

         DocumentData doc2 =
            getStorage().createDocument(folder2, documentTypeDefinition,
               getPropsMap(CmisConstants.DOCUMENT, "testMoveObject_NameConstraintException"), cs, null, null,
               VersioningState.MAJOR);
         String id = getConnection().moveObject(doc1.getObjectId(), folder2.getObjectId(), testroot.getObjectId());
         ObjectData obj = getStorage().getObjectById(id);
         assertFalse("Names must not match;", obj.getName().equalsIgnoreCase(doc1.getName()));
      }
      catch (NameConstraintViolationException ex)
      {
         //OK
      }
      finally
      {
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
      try
      {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition,
               getPropsMap(CmisConstants.DOCUMENT, "testDeleteObject_Simple"), cs, null, null, VersioningState.MAJOR);
         String id = doc1.getObjectId();
         getConnection().deleteObject(doc1.getObjectId(), true);
         ObjectData obj = getStorage().getObjectById(id);
      }
      catch (ObjectNotFoundException ex)
      {
         //OK
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

      try
      {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         FolderData testroot2 =
            getStorage().createFolder(rootFolder, folderTypeDefinition,
               getPropsMap(CmisConstants.FOLDER, "testDeleteObjectConstraintException"), null, null);

         DocumentData doc1 =
            getStorage().createDocument(testroot2, documentTypeDefinition,
               getPropsMap(CmisConstants.DOCUMENT, "testDeleteObject_ConstraintException"), cs, null, null,
               VersioningState.MAJOR);
         getConnection().deleteObject(testroot2.getObjectId(), true);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
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
      FolderData testroot2 =
         getStorage().createFolder(rootFolder, folderTypeDefinition,
            getPropsMap(CmisConstants.FOLDER, "testDeleteTree_Simple"), null, null);

      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      getStorage().createDocument(testroot2, documentTypeDefinition,
         getPropsMap(CmisConstants.DOCUMENT, "testDeleteTree_Simple1"), cs, null, null, VersioningState.MAJOR);

      getStorage().createDocument(testroot2, documentTypeDefinition,
         getPropsMap(CmisConstants.DOCUMENT, "testDeleteTree_Simple2"), cs, null, null, VersioningState.MAJOR);

      getStorage().createFolder(testroot2, folderTypeDefinition,
         getPropsMap(CmisConstants.FOLDER, "testDeleteTree_Simple_fol1"), null, null);
      String id = testroot2.getObjectId();

      try
      {
         Collection<String> str = getConnection().deleteTree(id, true, UnfileObject.DELETE, true);
         ObjectData root = getStorage().getObjectById(id);
         if (root != null)
            getStorage().deleteObject(root, true);
      }
      catch (ObjectNotFoundException ex)
      {
         //OK, deleted;
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
      if (!getStorage().getRepositoryInfo().getCapabilities().isCapabilityUnfiling())
      {
         //SKIP
         return;
      }
      FolderData testroot2 =
         getStorage().createFolder(rootFolder, folderTypeDefinition,
            getPropsMap(CmisConstants.FOLDER, "testDeleteTree_Unfile"), null, null);

      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      DocumentData doc1 =
         getStorage().createDocument(testroot2, documentTypeDefinition,
            getPropsMap(CmisConstants.DOCUMENT, "testDeleteTree_Unfile1"), cs, null, null, VersioningState.MAJOR);

      DocumentData doc2 =
         getStorage().createDocument(testroot2, documentTypeDefinition,
            getPropsMap(CmisConstants.DOCUMENT, "testDeleteTree_Unfile2"), cs, null, null, VersioningState.MAJOR);

      FolderData fol1 =
         getStorage().createFolder(testroot2, folderTypeDefinition,
            getPropsMap(CmisConstants.FOLDER, "testDeleteTree_Unfile_fol1"), null, null);

      String id1 = doc1.getObjectId();
      String id2 = doc2.getObjectId();
      boolean found1 = false;
      boolean found2 = false;

      Collection<String> str = getConnection().deleteTree(testroot2.getObjectId(), true, UnfileObject.UNFILE, true);
      Iterator<String> it = getStorage().getUnfiledObjectsId();
      while (it.hasNext())
      {
         String one = it.next();
         if (one.equals(id1))
            found1 = true;
         if (one.equals(id2))
            found2 = true;
      }
      assertTrue(found1);
      assertTrue(found2);
      if (testroot2 != null)
         getStorage().deleteObject(testroot2, true);
   }

   /**
    * 2.2.4.16
    * Sets the content stream for the specified Document object.
    * @throws Exception
    */
   @Test
   public void testSetContentStream_Simple() throws Exception
   {
      byte[] before = "1234567890aBcDE".getBytes();
      byte[] after = "zzz".getBytes();
      byte[] result = new byte[3];

      ContentStream cs1 = new BaseContentStream(before, null, new MimeType("text", "plain"));
      ContentStream cs2 = new BaseContentStream(after, null, new MimeType("text", "plain"));

      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition,
            getPropsMap(CmisConstants.DOCUMENT, "testSetContentStream_Simple"), cs1, null, null, VersioningState.MAJOR);

      String docid = getConnection().setContentStream(doc1.getObjectId(), cs2, new ChangeTokenHolder(), true);
      getStorage().getObjectById(docid).getContentStream(null).getStream().read(result);
      assertArrayEquals(after, result);
   }

   /**
    * 2.2.4.16.3
    * The Repository MUST throw this exception if the input parameter overwriteFlag is FALSE and the Object already has a content-stream. 
    * @throws Exception
    */
   @Test
   public void testSetContentStream_ContentAlreadyExistsException() throws Exception
   {
      try
      {
         byte[] before = "1234567890aBcDE".getBytes();
         byte[] after = "zzz".getBytes();

         ContentStream cs1 = new BaseContentStream(before, null, new MimeType("text", "plain"));
         ContentStream cs2 = new BaseContentStream(after, null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition,
               getPropsMap(CmisConstants.DOCUMENT, "testSetContentStream_ContentAlreadyExistsException"), cs1, null,
               null, VersioningState.MAJOR);

         String docid = getConnection().setContentStream(doc1.getObjectId(), cs2, new ChangeTokenHolder(), false);
         fail("ContentAlreadyExistsException must be thrown;");
      }
      catch (ContentAlreadyExistsException ex)
      {
         //OK
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
      String docid = null;
      String typeID = null;
      DocumentData doc1 = null;
      try
      {
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
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(),
            "testSetContentStream_StreamNotSupportedException"));
         props2.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefType.getId(), fPropDefType.getQueryName(),
            fPropDefType.getLocalName(), fPropDefType.getDisplayName(), "cmis:objecttype28"));

         Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

         TypeDefinition newType =
            new TypeDefinition("cmis:objecttype28", BaseType.DOCUMENT, "cmis:objecttype28", "cmis:objecttype28", "",
               "cmis:document", "cmis:objecttype28", "cmis:objecttype28", true, false, true, true, false, false, false,
               false, null, null, ContentStreamAllowed.NOT_ALLOWED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         doc1 = getStorage().createDocument(testroot, newType, props2, null, null, null, VersioningState.MAJOR);

         docid = getConnection().setContentStream(doc1.getObjectId(), cs2, new ChangeTokenHolder(), false);
         fail("StreamNotSupportedException must be thrown;");
      }
      catch (StreamNotSupportedException ex)
      {
         //OK
      }
      finally
      {
         if (docid != null)
            getStorage().deleteObject(getStorage().getObjectById(docid), true);
         if (doc1 != null)
            getStorage().deleteObject(doc1, true);
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
      byte[] before = "1234567890aBcDE".getBytes();
      ContentStream cs1 = new BaseContentStream(before, null, new MimeType("text", "plain"));

      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition,
            getPropsMap(CmisConstants.DOCUMENT, "testDeleteContentStream_Simple"), cs1, null, null,
            VersioningState.MAJOR);

      String docid = getConnection().deleteContentStream(doc1.getObjectId(), new ChangeTokenHolder());
      assertNull("Content stream must be null;", getStorage().getObjectById(docid).getContentStream(null));
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
      DocumentData doc1 = null;
      String typeID = null;
      try
      {
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
            fPropDefName.getLocalName(), fPropDefName.getDisplayName(), "testDeleteContentStream_ConstraintException"));
         props2.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(fPropDefType.getId(), fPropDefType.getQueryName(),
            fPropDefType.getLocalName(), fPropDefType.getDisplayName(), "cmis:objecttype29"));

         Map<String, PropertyDefinition<?>> fPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();

         TypeDefinition newType =
            new TypeDefinition("cmis:objecttype29", BaseType.DOCUMENT, "cmis:objecttype29", "cmis:objecttype29", "",
               "cmis:document", "cmis:objecttype29", "cmis:objecttype29", true, false, true, true, false, false, false,
               false, null, null, ContentStreamAllowed.REQUIRED, fPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         doc1 = getStorage().createDocument(testroot, newType, props2, cs1, null, null, VersioningState.MAJOR);

         String docid = getConnection().deleteContentStream(doc1.getObjectId(), new ChangeTokenHolder());
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
         if (doc1 != null)
            getStorage().deleteObject(doc1, true);
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   @AfterClass
   public static void stop() throws Exception
   {
      System.out.println("done;");
      if (BaseTest.conn != null)
         BaseTest.conn.close();
   }
}
