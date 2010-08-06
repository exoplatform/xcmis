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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xcmis.spi.BaseContentStream;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.DocumentData;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.PolicyData;
import org.xcmis.spi.PropertyFilter;
import org.xcmis.spi.RenditionFilter;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.CapabilityACL;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.ContentStreamAllowed;
import org.xcmis.spi.model.IncludeRelationships;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.PropertyType;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.Updatability;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.model.impl.IdProperty;
import org.xcmis.spi.model.impl.StringProperty;
import org.xcmis.spi.utils.MimeType;

import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class VersioningTest extends BaseTest
{

   static FolderData testroot = null;
   
   @BeforeClass
   public static void start() throws Exception
   {
      BaseTest.setUp();
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      testroot =
         getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "versioning_testroot"),
            null, null);
      System.out.print("Running Versioning Service tests....");
   }

   /**
    * 2.2.7.1
    * Create a private working copy of the document.
    * @throws Exception
    */
   @Test
   public void testCheckOut_Simple() throws Exception
   {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "testCheckOut_Simple"),
               cs, null, null, VersioningState.MAJOR);
         String pwcID = getConnection().checkout(doc1.getObjectId());
         assertNotNull("Checkout failed;", pwcID);
         assertNotNull("Object not found;", getStorage().getObjectById(pwcID));
   }

   /**
    * 2.2.7.1.3
    * •  constraint: The Repository MUST throw this exception if the Document’s Object-Type definition’s versionable attribute is FALSE. 
    * @throws Exception
    */
   @Test
   public void testCheckOut_ConstraintException() throws Exception
   {
      String typeID = new String();
      DocumentData doc1 = null;
      try
      {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         Map<String, PropertyDefinition<?>> propertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, false, false, false, false, false, Updatability.READWRITE,
               "doc1", true, null, null);
         org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(kinoPropDefName.getId(), kinoPropDefName.getQueryName(),
            kinoPropDefName.getLocalName(), kinoPropDefName.getDisplayName(), "testCheckOut_ConstraintException"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(kinoPropDefObjectTypeId.getId(),
            kinoPropDefObjectTypeId.getQueryName(), kinoPropDefObjectTypeId.getLocalName(), kinoPropDefObjectTypeId
               .getDisplayName(), "cmis:versiontype1"));

         TypeDefinition newType =
            new TypeDefinition("cmis:versiontype1", BaseType.DOCUMENT, "cmis:versiontype1", "cmis:versiontype1", "", "cmis:document",
               "cmis:versiontype1", "cmis:versiontype1", true, false, true, true, false, false, false, false, null, null,
               ContentStreamAllowed.ALLOWED, propertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         doc1 =
            getStorage().createDocument(testroot, newType, properties, cs, null, null, VersioningState.MAJOR);
         String pwcID = getConnection().checkout(doc1.getObjectId());
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
         getStorage().deleteObject(doc1, true);
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.7.2
    * Removes the private working copy of the checked-out document, allowing other documents 
    * in the version series to be checked out again.
    * @throws Exception
    */
   @Test
   public void testCancelCheckOut_Simple() throws Exception
   {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "testCancelCheckOut_Simple"),
               cs, null, null, VersioningState.MAJOR);
         String pwcID = getConnection().checkout(doc1.getObjectId());
         getConnection().cancelCheckout(pwcID);
         try
         {
            getStorage().getObjectById(pwcID);
            fail("PWC must be deleted after cancel checkout;");
         }
         catch (ObjectNotFoundException ex)
         {
            //OK
         }
   }

   /**
    * 2.2.7.2.3
    * •  constraint: The Repository MUST throw this exception if the Document’s Object-Type definition’s versionable attribute is FALSE. 
    * @throws Exception
    */
   @Test
   public void testCancelCheckOut_ConstraintException() throws Exception
   {
      String typeID = new String();
      DocumentData doc1 = null;
      try
      {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         Map<String, PropertyDefinition<?>> kinoPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefName2 =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, false, false, false, false, false, Updatability.READWRITE,
               "doc1", true, null, null);
         org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefObjectTypeId2 =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(kinoPropDefName2.getId(), kinoPropDefName2
            .getQueryName(), kinoPropDefName2.getLocalName(), kinoPropDefName2.getDisplayName(), "testCancelCheckOut_ConstraintException"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(kinoPropDefObjectTypeId2.getId(),
            kinoPropDefObjectTypeId2.getQueryName(), kinoPropDefObjectTypeId2.getLocalName(), kinoPropDefObjectTypeId2
               .getDisplayName(), "cmis:versiontype2"));

         TypeDefinition newType =
            new TypeDefinition("cmis:versiontype2", BaseType.DOCUMENT, "cmis:versiontype2", "cmis:versiontype2", "", "cmis:document",
               "cmis:versiontype2", "cmis:versiontype2", true, false, true, true, false, false, false, false, null, null,
               ContentStreamAllowed.ALLOWED, kinoPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         doc1 =
            getStorage().createDocument(testroot, newType, properties, cs, null, null, VersioningState.MAJOR);
         String pwcID = getConnection().checkout(doc1.getObjectId());
         getConnection().cancelCheckout(pwcID);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
         getStorage().deleteObject(doc1, true);
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.7.3
    * Checks-in the Private Working Copy document.
    * @throws Exception
    */
   @Test
   public void testCheckIn_Simple() throws Exception
   {
         byte[] before = "zzz".getBytes();
         byte[] after = new byte[3];
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         ContentStream cs2 = new BaseContentStream(before, null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "testCheckIn_Simple"),
               cs, null, null, VersioningState.MAJOR);
         String pwcID = getConnection().checkout(doc1.getObjectId());
         String chIn = getConnection().checkin(pwcID, true, null, cs2, "", null, null, null);
         assertNotNull("Check-in failed;", chIn);
         getStorage().getObjectById(chIn).getContentStream(null).getStream().read(after);
         assertArrayEquals(before, after);
   }

   /**
    * 2.2.7.3
    * Checks-in the Private Working Copy document.
    * @throws Exception
    */
   @Test
   public void testCheckIn_AddACL() throws Exception
   {
      if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE)){
         //SKIP
         return;
      }
         byte[] before = "zzz".getBytes();
         byte[] after = new byte[3];
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         ContentStream cs2 = new BaseContentStream(after, null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "testCheckIn_AddACL"),
               cs, null, null, VersioningState.MAJOR);

         String username = "username";
         List<AccessControlEntry> addACL = createACL(username, "cmis:read");

         String pwcID = getConnection().checkout(doc1.getObjectId());
         String chIn = getConnection().checkin(pwcID, true, null, cs2, "", addACL, null, null);
         assertNotNull("Check-in failed;", chIn);
         ObjectData obj = getStorage().getObjectById(chIn);
         for (AccessControlEntry one : obj.getACL(false))
         {
            if (one.getPrincipal().equalsIgnoreCase(username))
            {
               assertTrue("Items number incorrect in result;", one.getPermissions().size() == 1);
               assertTrue("ACL adding failed;", one.getPermissions().contains("cmis:read"));
            }
         }
   }

   /**
    * 2.2.7.3
    * Checks-in the Private Working Copy document.
    * @throws Exception
    */
   @Test
   public void testCheckIn_ApplyPolicy() throws Exception
   {
      if (!IS_POLICIES_SUPPORTED)
      {
        //SKIP
         return;
      }
      PolicyData policy = null;
      try
      {
         byte[] before = "zzz".getBytes();
         byte[] after = new byte[3];
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         ContentStream cs2 = new BaseContentStream(after, null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "testCheckIn_ApplyPolicy"),
               cs, null, null, VersioningState.MAJOR);

         policy = createPolicy(testroot, "testCheckIn_ApplyPolicy_policy1");

         ArrayList<String> policies = new ArrayList<String>();
         policies.add(policy.getObjectId());

         String pwcID = getConnection().checkout(doc1.getObjectId());
         String chIn = getConnection().checkin(pwcID, true, null, cs2, "", null, null, policies);
         assertNotNull("Check-in failed;", chIn);
         ObjectData obj = getStorage().getObjectById(chIn);
         Iterator<PolicyData> it = obj.getPolicies().iterator();
         while (it.hasNext())
         {
            PolicyData one = it.next();
            assertTrue("Policy adding failed;", one.getName().equals("policy1"));
            assertTrue("Policy adding failed;", one.getPolicyText().equals("testPolicyText"));
            obj.removePolicy(one);
         }
      }
      finally
      {
         if (policy != null)
            getStorage().deleteObject(policy, true);
      }
   }

   /**
    * 2.2.7.3.3
    * •  constraint: The Repository MUST throw this exception if the Document’s Object-Type definition’s versionable attribute is FALSE. 
    * @throws Exception
    */
   @Test
   public void testCheckIn_ConstraintExceptionNotVersionable() throws Exception
   {
      DocumentData doc1 = null;
      String typeID = new String();
      try
      {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         Map<String, PropertyDefinition<?>> kinoPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefName2 =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, false, false, false, false, false, Updatability.READWRITE,
               "doc1", true, null, null);
         org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefObjectTypeId2 =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(kinoPropDefName2.getId(), kinoPropDefName2
            .getQueryName(), kinoPropDefName2.getLocalName(), kinoPropDefName2.getDisplayName(), "testCheckIn_ConstraintExceptionNotVersionable"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(kinoPropDefObjectTypeId2.getId(),
            kinoPropDefObjectTypeId2.getQueryName(), kinoPropDefObjectTypeId2.getLocalName(), kinoPropDefObjectTypeId2
               .getDisplayName(), "cmis:versiontype3"));

         TypeDefinition newType =
            new TypeDefinition("cmis:versiontype3", BaseType.DOCUMENT, "cmis:versiontype3", "cmis:versiontype3", "", "cmis:document",
               "cmis:versiontype3", "cmis:versiontype3", true, false, true, true, false, false, false, false, null, null,
               ContentStreamAllowed.ALLOWED, kinoPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

          doc1 =
            getStorage().createDocument(testroot, newType, properties, cs, null, null, VersioningState.MAJOR);

         String pwcID = getConnection().checkout(doc1.getObjectId());
         String chIn = getConnection().checkin(pwcID, true, null, cs, "", null, null, null);
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
        //OK
      }
      finally
      {
         getStorage().deleteObject(doc1, true);
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.7.3.3
    * •  constraint: The Repository MUST throw this exception if the “contentStreamAllowed” attribute of the Object-Type definition specified by the cmis:objectTypeId 
    * property value is set to “not allowed” and a contentStream input parameter is provided.
    * @throws Exception
    */
   @Test
   public void testCheckIn_ConstraintExceptionContentNotAllowed() throws Exception
   {
      String pwcID = null;
      String typeID = new String();
      DocumentData doc1 = null;
      try
      {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         Map<String, PropertyDefinition<?>> kinoPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefName2 =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, false, false, false, false, false, Updatability.READWRITE,
               "doc1", true, null, null);
         org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefObjectTypeId2 =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(kinoPropDefName2.getId(), kinoPropDefName2
            .getQueryName(), kinoPropDefName2.getLocalName(), kinoPropDefName2.getDisplayName(), "testCheckIn_ConstraintExceptionContentNotAllowed"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(kinoPropDefObjectTypeId2.getId(),
            kinoPropDefObjectTypeId2.getQueryName(), kinoPropDefObjectTypeId2.getLocalName(), kinoPropDefObjectTypeId2
               .getDisplayName(), "cmis:versiontype4"));

         TypeDefinition newType =
            new TypeDefinition("cmis:versiontype4", BaseType.DOCUMENT, "cmis:versiontype4", "cmis:versiontype4", "", "cmis:document",
               "cmis:versiontype4", "cmis:versiontype4", true, false, true, true, false, false, false, true, null, null,
               ContentStreamAllowed.NOT_ALLOWED, kinoPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         doc1 = getStorage().createDocument(testroot, newType, properties, null, null, null, VersioningState.MAJOR);
         pwcID = getConnection().checkout(doc1.getObjectId());
         String chIn = getConnection().checkin(pwcID, true, null, cs, "", null, null, null);
         fail("ConstraintException must be thrown;");

      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
         getStorage().deleteObject(doc1, true);
         if (typeID != null)
            getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.7.4
    * Get a the latest Document object in the Version Series.
    * @throws Exception
    */
   @Test
   public void testGetObjectOfLatestVersion_Simple() throws Exception
   {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         ContentStream cs2 = new BaseContentStream("zzz".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "testGetObjectOfLatestVersion_Simple"),
               cs, null, null, VersioningState.MAJOR);
         String pwcID = getConnection().checkout(doc1.getObjectId());
         String chIn = getConnection().checkin(pwcID, true, null, cs2, "", null, null, null);
         CmisObject obj =
            getConnection().getObjectOfLatestVersion(doc1.getVersionSeriesId(), false, true, IncludeRelationships.BOTH,
               true, true, true, PropertyFilter.ALL, RenditionFilter.NONE);
         assertNotNull("GetObjectOfLatestVersion failed;", obj);
   }

   /**
    * 2.2.7.4
    * Get a the latest Document object in the Version Series.
    * @throws Exception
    */
   @Test
   public void testGetObjectOfLatestVersion_AllowableActions() throws Exception
   {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         ContentStream cs2 = new BaseContentStream("zzz".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "testGetObjectOfLatestVersion_AllowableActions"),
               cs, null, null, VersioningState.MAJOR);
         String pwcID = getConnection().checkout(doc1.getObjectId());
         String chIn = getConnection().checkin(pwcID, true, null, cs2, "", null, null, null);
         CmisObject obj =
            getConnection().getObjectOfLatestVersion(doc1.getVersionSeriesId(), false, true, IncludeRelationships.BOTH,
               true, true, true, PropertyFilter.ALL, RenditionFilter.NONE);
         assertNotNull("GetObjectOfLatestVersion failed;", obj);
         assertNotNull(" AllowableActions must be present in result;", obj.getAllowableActions());
   }

   /**
    * 2.2.7.4
    * Get a the latest Document object in the Version Series.
    * @throws Exception
    */
   @Test
   public void testGetObjectOfLatestVersion_IncludePolicies() throws Exception
   {
      if (!IS_POLICIES_SUPPORTED)
      {
         //SKIP
         return;
      }
      PolicyData policy = null;
      try
      {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         ContentStream cs2 = new BaseContentStream("zzz".getBytes(), null, new MimeType("text", "plain"));

         policy = createPolicy(testroot, "policy_testGetObjectOfLatestVersion");
         ArrayList<String> policies = new ArrayList<String>();
         policies.add(policy.getObjectId());

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "testGetObjectOfLatestVersion_IncludePolicies"),
               cs, null, null, VersioningState.MAJOR);
         String pwcID = getConnection().checkout(doc1.getObjectId());
         String chIn = getConnection().checkin(pwcID, true, null, cs2, "", null, null, policies);
         CmisObject obj =
            getConnection().getObjectOfLatestVersion(doc1.getVersionSeriesId(), false, true, IncludeRelationships.BOTH,
               true, true, true, PropertyFilter.ALL, RenditionFilter.NONE);
         assertNotNull("GetObjectOfLatestVersion failed;", obj);
         Iterator<String> it = obj.getPolicyIds().iterator();
         while (it.hasNext())
         {
            PolicyData one = (PolicyData)getStorage().getObjectById(it.next());
            assertTrue("Policy adding failed;", one.getName().equals("policy1"));
            assertTrue("Policy text failed;", one.getPolicyText().equals("testPolicyText"));
         }
      }
      finally
      {
         if (policy != null)
            getStorage().deleteObject(policy, true);
      }
   }

   /**
    * 2.2.7.4
    * Get a the latest Document object in the Version Series.
    * @throws Exception
    */
   @Test
   public void testGetObjectOfLatestVersion_IncludeACL() throws Exception
   {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         ContentStream cs2 = new BaseContentStream("zzz".getBytes(), null, new MimeType("text", "plain"));

         String username = "username";
         List<AccessControlEntry> addACL = createACL(username, "cmis:read");

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "testGetObjectOfLatestVersion_IncludeACL"),
               cs, null, null, VersioningState.MAJOR);
         String pwcID = getConnection().checkout(doc1.getObjectId());
         String chIn = getConnection().checkin(pwcID, true, null, cs2, "", addACL, null, null);
         CmisObject obj =
            getConnection().getObjectOfLatestVersion(doc1.getVersionSeriesId(), false, true, IncludeRelationships.BOTH,
               true, true, true, PropertyFilter.ALL, RenditionFilter.NONE);
         assertNotNull("GetObjectOfLatestVersion failed;", obj);
         for (AccessControlEntry one : obj.getACL())
         {
            if (one.getPrincipal().equalsIgnoreCase(username))
            {
               assertTrue("Permission setting failed;", one.getPermissions().size() == 1);
               assertTrue("Permission setting failed;", one.getPermissions().contains("cmis:read"));
            }
         }
   }

   /**
    * 2.2.7.4
    * Repositories SHOULD return only the properties specified in the property filter 
    * if they exist on the object’s type definition.
    * @throws Exception
    */
   @Test
   public void testGetObjectOfLatestVersion_PropertiesFiltered() throws Exception
   {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         ContentStream cs2 = new BaseContentStream("zzz".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "testGetObjectOfLatestVersion_PropertiesFiltered"),
               cs, null, null, VersioningState.MAJOR);
         String pwcID = getConnection().checkout(doc1.getObjectId());
         String chIn = getConnection().checkin(pwcID, true, null, cs2, "", null, null, null);
         CmisObject obj =
            getConnection().getObjectOfLatestVersion(doc1.getVersionSeriesId(), false, true, IncludeRelationships.BOTH,
               true, true, true, "cmis:name,cmis:path", RenditionFilter.NONE);
         assertNotNull("GetObjectOfLatestVersion failed;", obj);
         for (Map.Entry<String, Property<?>> e : obj.getProperties().entrySet())
         {
            if (e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path"))//Other props must be ignored
               continue;
            else
              fail("Property filter works incorrect;");
         }
   }

   /**
    * 2.2.7.4.3
    * • filterNotValid: The Repository MUST throw this exception if this property filter input parameter is not valid.
    * @throws Exception
    */
   @Test
   public void testGetObjectOfLatestVersion_FilterNotValidException() throws Exception
   {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         ContentStream cs2 = new BaseContentStream("zzz".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "testGetObjectOfLatestVersion_FilterNotValidException"),
               cs, null, null, VersioningState.MAJOR);
         String pwcID = getConnection().checkout(doc1.getObjectId());
         String chIn = getConnection().checkin(pwcID, true, null, cs2, "", null, null, null);
         try {
         CmisObject obj =
            getConnection().getObjectOfLatestVersion(doc1.getVersionSeriesId(), false, true, IncludeRelationships.BOTH,
               true, true, true, "(,*", RenditionFilter.NONE);
         fail("FilterNotValidException must be thrown;");
      }
      catch (FilterNotValidException ex)
      {
         //OK
      }
   }

   /**
    * 2.2.7.4.3
    * •  objectNotFound:  The Repository MUST throw this exception if the input parameter major is TRUE and the Version Series contains no major versions.
    * @throws Exception
    */
   @Test
   public void testGetObjectOfLatestVersion_ObjectNotFoundException() throws Exception
   {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         ContentStream cs2 = new BaseContentStream("zzz".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "testGetObjectOfLatestVersion_ObjectNotFoundException"),
               cs, null, null, VersioningState.MINOR);
         String pwcID = getConnection().checkout(doc1.getObjectId());
         String chIn = getConnection().checkin(pwcID, false, null, cs2, "", null, null, null);
         try {
         CmisObject obj =
            getConnection().getObjectOfLatestVersion(doc1.getVersionSeriesId(), true, true, IncludeRelationships.BOTH,
               true, true, true, PropertyFilter.ALL, RenditionFilter.NONE);
         fail("ObjectNotFoundException must be thrown;");
      }
      catch (ObjectNotFoundException ex)
      {
         //OK
      }
   }

   /**
    * 2.2.7.5
    * Get a subset of the properties for the latest Document Object in the Version Series.  
    * @throws Exception
    */
   @Test
   public void testGetPropertiesOfLatestVersion_Simple() throws Exception
   {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         ContentStream cs2 = new BaseContentStream("zzz".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "testGetPropertiesOfLatestVersion_Simple"),
               cs, null, null, VersioningState.MAJOR);
         String pwcID = getConnection().checkout(doc1.getObjectId());
         String chIn = getConnection().checkin(pwcID, true, null, cs2, "", null, null, null);
         CmisObject obj =
            getConnection().getPropertiesOfLatestVersion(doc1.getVersionSeriesId(), true, true, PropertyFilter.ALL);
         assertNotNull("GetPropertiesOfLatestVersion failed;", obj);
         assertNotNull("ObjectInfo must be present in result;", obj.getObjectInfo());
   }

   /**
    * 2.2.7.5
    * Repositories SHOULD return only the properties specified in the property filter 
    * if they exist on the object’s type definition.
    * @throws Exception
    */
   @Test
   public void testGetPropertiesOfLatestVersion_PropertiesFiltered() throws Exception
   {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         ContentStream cs2 = new BaseContentStream("zzz".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "testGetPropertiesOfLatestVersion_PropertiesFiltered"),
               cs, null, null, VersioningState.MAJOR);
         String pwcID = getConnection().checkout(doc1.getObjectId());
         String chIn = getConnection().checkin(pwcID, true, null, cs2, "", null, null, null);
         CmisObject obj =
            getConnection().getPropertiesOfLatestVersion(doc1.getVersionSeriesId(), true, true, "cmis:name,cmis:path");
         assertNotNull("GetPropertiesOfLatestVersion failed;", obj);
            
         for (Map.Entry<String, Property<?>> e : obj.getProperties().entrySet())
         {
            if (e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path")) //Other props must be ignored
               continue;
            else
               fail("Property filter works incorrect");
         }
   }

   /**
    * 2.2.7.5.3
    * •  filterNotValid: The Repository MUST throw this exception if this property filter input parameter is not valid.
    * @throws Exception
    */
   @Test
   public void testGetPropertiesOfLatestVersion_FilterNotValidException() throws Exception
   {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         ContentStream cs2 = new BaseContentStream("zzz".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "testGetPropertiesOfLatestVersion_FilterNotValidException"),
               cs, null, null, VersioningState.MAJOR);
         String pwcID = getConnection().checkout(doc1.getObjectId());
         String chIn = getConnection().checkin(pwcID, true, null, cs2, "", null, null, null);
         try {
         CmisObject obj = getConnection().getPropertiesOfLatestVersion(doc1.getVersionSeriesId(), true, true, "(,*");
         fail("FilterNotValidException must be thrown;");
      }
      catch (FilterNotValidException ex)
      {
         //OK
      }
   }

   /**
    * 2.2.7.5.3
    * •  objectNotFound:  The Repository MUST throw this exception if the input parameter major is TRUE and the Version Series contains no major versions.
    * @throws Exception
    */
   @Test
   public void testGetPropertiesOfLatestVersion_ObjectNotFoundException() throws Exception
   {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         ContentStream cs2 = new BaseContentStream("zzz".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "testGetPropertiesOfLatestVersion_ObjectNotFoundException"),
               cs, null, null, VersioningState.MINOR);
         String pwcID = getConnection().checkout(doc1.getObjectId());
         String chIn = getConnection().checkin(pwcID, false, null, cs2, "", null, null, null);
         try {
         CmisObject obj =
            getConnection().getPropertiesOfLatestVersion(doc1.getVersionSeriesId(), true, true, PropertyFilter.ALL);
         fail("ObjectNotFoundException must be thrown;");
      }
      catch (ObjectNotFoundException ex)
      {
         //OK
      }
   }

   @AfterClass
   public static void stop() throws Exception
   {
      if (testroot != null)
         clear(testroot.getObjectId());
      if (BaseTest.conn != null)
         BaseTest.conn.close();
      System.out.println("done;");
   }
}
