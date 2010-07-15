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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.BaseType;
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
import static org.junit.Assert.assertArrayEquals;

public class VersioningTest extends BaseTest
{
   /**
    * 2.2.7.1
    * Create a private working copy of the document.
    * @throws Exception
    */
   public void testCheckOut_Simple() throws Exception
   {
      System.out.print("Running testCheckOut_Simple....                                            ");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      try
      {
         String pwcID = getConnection().checkout(doc1.getObjectId());
         assertNotNull(pwcID);
         assertNotNull(getStorage().getObjectById(pwcID));
         pass();
      }
      catch (Exception other)
      {
         doFail(other.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }
   }

   
   /**
    * 2.2.7.1.3
    * •  constraint: The Repository MUST throw this exception if the Document’s Object-Type definition’s versionable attribute is FALSE. 
    * @throws Exception
    */
   public void testCheckOut_ConstraintException() throws Exception
   {
      System.out.print("Running testCheckOut_ConstraintException....                               ");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
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
      propertyDefinitions.put(CmisConstants.NAME, kinoPropDefName);
      //propertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, kinoPropDefObjectTypeId);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(kinoPropDefName.getId(), kinoPropDefName.getQueryName(),
         kinoPropDefName.getLocalName(), kinoPropDefName.getDisplayName(), "doc1"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(kinoPropDefObjectTypeId.getId(),
         kinoPropDefObjectTypeId.getQueryName(), kinoPropDefObjectTypeId.getLocalName(), kinoPropDefObjectTypeId
            .getDisplayName(), "cmis:kino"));

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document", "cmis:kino",
            "cmis:kino", true, false, true, true, false, false, false, false, null, null, ContentStreamAllowed.ALLOWED,
            propertyDefinitions);
      String typeID = getStorage().addType(newType);
      newType = getStorage().getTypeDefinition(typeID, true);

      DocumentData doc1 =
         getStorage().createDocument(testroot, newType, properties, cs, null, null, VersioningState.MAJOR);
      try
      {
         String pwcID = getConnection().checkout(doc1.getObjectId());
         doFail();
      }
      catch (ConstraintException ex){
         pass();
      }
      catch (Exception other)
      {
         doFail(other.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }
   
   
   /**
    * 2.2.7.2
    * Removes the private working copy of the checked-out document, allowing other documents 
    * in the version series to be checked out again.
    * @throws Exception
    */
   public void testCancelCheckOut_Simple() throws Exception
   {
      System.out.print("Running testCancelCheckOut_Simple....                                      ");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      String pwcID = getConnection().checkout(doc1.getObjectId());
      try
      {
         getConnection().cancelCheckout(pwcID);
         try
         {
            getStorage().getObjectById(pwcID);
         }
         catch (ObjectNotFoundException ex)
         {
            pass();
         }
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }
   }
   
   
   /**
    * 2.2.7.2.3
    * •  constraint: The Repository MUST throw this exception if the Document’s Object-Type definition’s versionable attribute is FALSE. 
    * @throws Exception
    */
   public void testCancelCheckOut_ConstraintException() throws Exception
   {
      System.out.print("Running testCancelCheckOut_ConstraintException....                         ");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
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
      kinoPropertyDefinitions.put(CmisConstants.NAME, kinoPropDefName2);
      //kinoPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, kinoPropDefObjectTypeId2);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(kinoPropDefName2.getId(), kinoPropDefName2.getQueryName(),
         kinoPropDefName2.getLocalName(), kinoPropDefName2.getDisplayName(), "doc1"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(kinoPropDefObjectTypeId2.getId(),
         kinoPropDefObjectTypeId2.getQueryName(), kinoPropDefObjectTypeId2.getLocalName(), kinoPropDefObjectTypeId2
            .getDisplayName(), "cmis:kino"));

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document", "cmis:kino",
            "cmis:kino", true, false, true, true, false, false, false, false, null, null, ContentStreamAllowed.ALLOWED,
            kinoPropertyDefinitions);
      String typeID = getStorage().addType(newType);
      newType = getStorage().getTypeDefinition(typeID, true);

      DocumentData doc1 =
         getStorage().createDocument(testroot, newType, properties, cs, null, null, VersioningState.MAJOR);
      String pwcID = getConnection().checkout(doc1.getObjectId());
      try
      {
         getConnection().cancelCheckout(pwcID);
         doFail();
      }
      catch (ConstraintException ex){
         pass();
      }
      catch (Exception other)
      {
         doFail(other.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }
   
   /**
    * 2.2.7.3
    * Checks-in the Private Working Copy document.
    * @throws Exception
    */
   public void testCheckIn_Simple() throws Exception
   {
      System.out.print("Running testCheckIn_Simple....                                             ");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      byte[] before = "zzz".getBytes();
      byte[] after = new byte[3];
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      ContentStream cs2 = new BaseContentStream(before, null, new MimeType("text", "plain"));

      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      String pwcID = getConnection().checkout(doc1.getObjectId());
      try
      {
         String chIn = getConnection().checkin(pwcID, true, null, cs2, "", null, null, null);
         assertNotNull(chIn);
///         assertNotNull(getStorage().getObjectById(chIn));
         getStorage().getObjectById(doc1.getObjectId()).getContentStream(null).getStream().read(after);
         assertArrayEquals(before, after);
         pass();
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }
   }
   
   
   /**
    * 2.2.7.3
    * Checks-in the Private Working Copy document.
    * @throws Exception
    */
   public void testCheckIn_AddACL() throws Exception
   {
      System.out.print("Running testCheckIn_AddACL....                                             ");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      byte[] before = "zzz".getBytes();
      byte[] after = new byte[3];
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      ContentStream cs2 = new BaseContentStream(after, null, new MimeType("text", "plain"));

      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      
      AccessControlEntry acl = new AccessControlEntry();
      acl.setPrincipal("Makis");
      acl.getPermissions().add("cmis:read");
      ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
      addACL.add(acl);
      
      String pwcID = getConnection().checkout(doc1.getObjectId());
      try
      {
         String chIn = getConnection().checkin(pwcID, true, null, cs2, "", addACL, null, null);
         assertNotNull(chIn);
         ObjectData obj = getStorage().getObjectById(chIn);
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
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }
   }
   
   
   /**
    * 2.2.7.3
    * Checks-in the Private Working Copy document.
    * @throws Exception
    */
   public void testCheckIn_ApplyPolicy() throws Exception
   {
      System.out.print("Running testCheckIn_ApplyPolicy....                                        ");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      byte[] before = "zzz".getBytes();
      byte[] after = new byte[3];
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      ContentStream cs2 = new BaseContentStream(after, null, new MimeType("text", "plain"));

      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      
      PolicyData policy = createPolicy(testroot, "policy1");

      ArrayList<String> policies = new ArrayList<String>();
      policies.add(policy.getObjectId());
      
      String pwcID = getConnection().checkout(doc1.getObjectId());
      try
      {
         String chIn = getConnection().checkin(pwcID, true, null, cs2, "", null, null, policies);
         assertNotNull(chIn);
         ObjectData obj = getStorage().getObjectById(chIn);
         Iterator<PolicyData> it = obj.getPolicies().iterator();
         while (it.hasNext())
         {
            PolicyData one = it.next();
            assertEquals("policy1", one.getName());
            assertEquals("testPolicyText", one.getPolicyText());
            obj.removePolicy(one);
         }
         pass();
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
      }
      finally
      {
         getStorage().deleteObject(policy, true);
         clear(testroot.getObjectId());
      }
   }
   
   
   /**
    * 2.2.7.3.3
    * •  constraint: The Repository MUST throw this exception if the Document’s Object-Type definition’s versionable attribute is FALSE. 
    * @throws Exception
    */
   public void testCheckIn_ConstraintException1() throws Exception
   {
      System.out.print("Running testCheckIn_ConstraintException1....                               ");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
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
      kinoPropertyDefinitions.put(CmisConstants.NAME, kinoPropDefName2);
      //kinoPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, kinoPropDefObjectTypeId2);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(kinoPropDefName2.getId(), kinoPropDefName2.getQueryName(),
         kinoPropDefName2.getLocalName(), kinoPropDefName2.getDisplayName(), "doc1"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(kinoPropDefObjectTypeId2.getId(),
         kinoPropDefObjectTypeId2.getQueryName(), kinoPropDefObjectTypeId2.getLocalName(), kinoPropDefObjectTypeId2
            .getDisplayName(), "cmis:kino"));

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document", "cmis:kino",
            "cmis:kino", true, false, true, true, false, false, false, false, null, null, ContentStreamAllowed.ALLOWED,
            kinoPropertyDefinitions);
      String typeID = getStorage().addType(newType);
      newType = getStorage().getTypeDefinition(typeID, true);

      DocumentData doc1 =
         getStorage().createDocument(testroot, newType, properties, cs, null, null, VersioningState.MAJOR);
      String pwcID = getConnection().checkout(doc1.getObjectId());
      try
      {
         String chIn = getConnection().checkin(pwcID, true, null, cs, "", null, null, null);
         doFail();
      }
      catch (ConstraintException ex){
         pass();
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }
   
   
   /**
    * 2.2.7.3.3
    * •  constraint: The Repository MUST throw this exception if the “contentStreamAllowed” attribute of the Object-Type definition specified by the cmis:objectTypeId 
    * property value is set to “not allowed” and a contentStream input parameter is provided.
    * @throws Exception
    */
   public void testCheckIn_ConstraintException2() throws Exception
   {
      System.out.print("Running testCheckIn_ConstraintException2....                               ");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
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
      kinoPropertyDefinitions.put(CmisConstants.NAME, kinoPropDefName2);
      //kinoPropertyDefinitions.put(CmisConstants.OBJECT_TYPE_ID, kinoPropDefObjectTypeId2);

      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(kinoPropDefName2.getId(), kinoPropDefName2.getQueryName(),
         kinoPropDefName2.getLocalName(), kinoPropDefName2.getDisplayName(), "doc1"));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(kinoPropDefObjectTypeId2.getId(),
         kinoPropDefObjectTypeId2.getQueryName(), kinoPropDefObjectTypeId2.getLocalName(), kinoPropDefObjectTypeId2
            .getDisplayName(), "cmis:kino"));

      TypeDefinition newType =
         new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document", "cmis:kino",
            "cmis:kino", true, false, true, true, false, false, false, false, null, null, ContentStreamAllowed.NOT_ALLOWED,
            kinoPropertyDefinitions);
      String typeID = getStorage().addType(newType);
      newType = getStorage().getTypeDefinition(typeID, true);

      DocumentData doc1 =
         getStorage().createDocument(testroot, newType, properties, null, null, null, VersioningState.MAJOR);
      String pwcID = getConnection().checkout(doc1.getObjectId());
      try
      {
         String chIn = getConnection().checkin(pwcID, true, null, cs, "", null, null, null);
         doFail();
      }
      catch (ConstraintException ex){
         pass();
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
         getStorage().removeType(typeID);
      }
   }
   
   
   
   /**
    * 2.2.7.4
    * Get a the latest Document object in the Version Series.
    * @throws Exception
    */
   public void testGetObjectOfLatestVersion_Simple() throws Exception
   {
      System.out.print("Running testGetObjectOfLatestVersion_Simple....                            ");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      ContentStream cs2 = new BaseContentStream("zzz".getBytes(), null, new MimeType("text", "plain"));

      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      String pwcID = getConnection().checkout(doc1.getObjectId());
      String chIn = getConnection().checkin(pwcID, true, null, cs2, "", null, null, null);
      try
      {
         CmisObject obj = getConnection().getObjectOfLatestVersion(doc1.getVersionSeriesId(), false, true, IncludeRelationships.BOTH, true, true, true, "", "");
         assertNotNull(obj);
         
         pass();
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }
   }
   
   /**
    * 2.2.7.4
    * Get a the latest Document object in the Version Series.
    * @throws Exception
    */
   public void testGetObjectOfLatestVersion_AllowableActions() throws Exception
   {
      System.out.print("Running testGetObjectOfLatestVersion_AllowableActions....                  ");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      ContentStream cs2 = new BaseContentStream("zzz".getBytes(), null, new MimeType("text", "plain"));

      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      String pwcID = getConnection().checkout(doc1.getObjectId());
      String chIn = getConnection().checkin(pwcID, true, null, cs2, "", null, null, null);
      try
      {
         CmisObject obj = getConnection().getObjectOfLatestVersion(doc1.getVersionSeriesId(), false, true, IncludeRelationships.BOTH, true, true, true, "", "");
         assertNotNull(obj);
         assertNotNull(obj.getAllowableActions());
         pass();
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }
   }
   
   
   /**
    * 2.2.7.4
    * Get a the latest Document object in the Version Series.
    * @throws Exception
    */
   public void testGetObjectOfLatestVersion_IncludePolicies() throws Exception
   {
      System.out.print("Running testGetObjectOfLatestVersion_IncludePolicies....                   ");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      ContentStream cs2 = new BaseContentStream("zzz".getBytes(), null, new MimeType("text", "plain"));
      
      PolicyData policy = createPolicy(testroot, "policy1");
      ArrayList<String> policies = new ArrayList<String>();
      policies.add(policy.getObjectId());
      
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      String pwcID = getConnection().checkout(doc1.getObjectId());
      String chIn = getConnection().checkin(pwcID, true, null, cs2, "", null, null, policies);
      try
      {
         CmisObject obj = getConnection().getObjectOfLatestVersion(doc1.getVersionSeriesId(), false, true, IncludeRelationships.BOTH, true, true, true, "", "");
         assertNotNull(obj);
         Iterator<String> it = obj.getPolicyIds().iterator();
         while (it.hasNext())
         {
            PolicyData one = (PolicyData)getStorage().getObjectById(it.next());
            assertEquals("policy1", one.getName());
            assertEquals("testPolicyText", one.getPolicyText());
         }
         pass();
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
         getStorage().deleteObject(policy, true);
      }
   }
   
   /**
    * 2.2.7.4
    * Get a the latest Document object in the Version Series.
    * @throws Exception
    */
   public void testGetObjectOfLatestVersion_IncludeACL() throws Exception
   {
      System.out.print("Running testGetObjectOfLatestVersion_IncludeACL....                        ");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      ContentStream cs2 = new BaseContentStream("zzz".getBytes(), null, new MimeType("text", "plain"));
      
      AccessControlEntry acl = new AccessControlEntry();
      acl.setPrincipal("Makis");
      acl.getPermissions().add("cmis:read");
      ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
      addACL.add(acl);

      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      String pwcID = getConnection().checkout(doc1.getObjectId());
      String chIn = getConnection().checkin(pwcID, true, null, cs2, "", addACL, null, null);
      try
      {
         CmisObject obj = getConnection().getObjectOfLatestVersion(doc1.getVersionSeriesId(), false, true, IncludeRelationships.BOTH, true, true, true, "", "");
         assertNotNull(obj);
         for (AccessControlEntry one : obj.getACL())
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
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }
   }
   
   
   /**
    * 2.2.7.4
    * Repositories SHOULD return only the properties specified in the property filter 
    * if they exist on the object’s type definition.
    * @throws Exception
    */
   public void testGetObjectOfLatestVersion_PropertiesFiltered() throws Exception
   {
      System.out.print("Running testGetObjectOfLatestVersion_PropertiesFiltered....                ");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      ContentStream cs2 = new BaseContentStream("zzz".getBytes(), null, new MimeType("text", "plain"));
      
      AccessControlEntry acl = new AccessControlEntry();
      acl.setPrincipal("Makis");
      acl.getPermissions().add("cmis:read");
      ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
      addACL.add(acl);

      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      String pwcID = getConnection().checkout(doc1.getObjectId());
      String chIn = getConnection().checkin(pwcID, true, null, cs2, "", addACL, null, null);
      try
      {
         CmisObject obj = getConnection().getObjectOfLatestVersion(doc1.getVersionSeriesId(), false, true, IncludeRelationships.BOTH, true, true, true, "cmis:name,cmis:path", "");
         assertNotNull(obj);
         for (Map.Entry<String, Property<?>> e : obj.getProperties().entrySet())
         {
            assertTrue(e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path")); //Other props must be ignored
         }
         pass();
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }
   }
   
   /**
    * 2.2.7.4.3
    * • filterNotValid: The Repository MUST throw this exception if this property filter input parameter is not valid.
    * @throws Exception
    */
   public void testGetObjectOfLatestVersion_FilterNotValidException() throws Exception
   {
      System.out.print("Running testGetObjectOfLatestVersion_FilterNotValidException....           ");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      ContentStream cs2 = new BaseContentStream("zzz".getBytes(), null, new MimeType("text", "plain"));
      
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      String pwcID = getConnection().checkout(doc1.getObjectId());
      String chIn = getConnection().checkin(pwcID, true, null, cs2, "", null, null, null);
      try
      {
         CmisObject obj = getConnection().getObjectOfLatestVersion(doc1.getVersionSeriesId(), false, true, IncludeRelationships.BOTH, true, true, true, "(,*", "");
         doFail();
      } catch (FilterNotValidException ex){
         pass();
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }
   }
  
   
   /**
    * 2.2.7.4.3
    * •  objectNotFound:  The Repository MUST throw this exception if the input parameter major is TRUE and the Version Series contains no major versions.
    * @throws Exception
    */
   public void testGetObjectOfLatestVersion_ObjectNotFoundException() throws Exception
   {
      System.out.print("Running testGetObjectOfLatestVersion_ObjectNotFoundException....           ");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      ContentStream cs2 = new BaseContentStream("zzz".getBytes(), null, new MimeType("text", "plain"));
      
      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MINOR);
      String pwcID = getConnection().checkout(doc1.getObjectId());
      String chIn = getConnection().checkin(pwcID, false, null, cs2, "", null, null, null);
      try
      {
         CmisObject obj = getConnection().getObjectOfLatestVersion(doc1.getVersionSeriesId(), true, true, IncludeRelationships.BOTH, true, true, true, "", "");
         doFail();
      } catch (ObjectNotFoundException ex){
         pass();
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }
   }
   
   
   /**
    * 2.2.7.5
    * Get a subset of the properties for the latest Document Object in the Version Series.  
    * @throws Exception
    */
   public void testGetPropertiesOfLatestVersion_Simple() throws Exception
   {
      System.out.print("Running testGetObjectOfLatestVersion_Simple....                            ");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      ContentStream cs2 = new BaseContentStream("zzz".getBytes(), null, new MimeType("text", "plain"));

      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      String pwcID = getConnection().checkout(doc1.getObjectId());
      String chIn = getConnection().checkin(pwcID, true, null, cs2, "", null, null, null);
      try
      {
         CmisObject obj = getConnection().getPropertiesOfLatestVersion(doc1.getVersionSeriesId(), true, true, "");
         assertNotNull(obj);
         assertNotNull(obj.getObjectInfo());
         pass();
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }
   }
   
   /**
    * 2.2.7.5
    * Repositories SHOULD return only the properties specified in the property filter 
    * if they exist on the object’s type definition.
    * @throws Exception
    */
   public void testGetPropertiesOfLatestVersion_PropertiesFiltered() throws Exception
   {
      System.out.print("Running testGetPropertiesOfLatestVersion_PropertiesFiltered....            ");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      ContentStream cs2 = new BaseContentStream("zzz".getBytes(), null, new MimeType("text", "plain"));

      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      String pwcID = getConnection().checkout(doc1.getObjectId());
      String chIn = getConnection().checkin(pwcID, true, null, cs2, "", null, null, null);
      try
      {
         CmisObject obj = getConnection().getPropertiesOfLatestVersion(doc1.getVersionSeriesId(), true, true, "cmis:name,cmis:path");
         assertNotNull(obj);
         for (Map.Entry<String, Property<?>> e : obj.getProperties().entrySet())
         {
            assertTrue(e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path")); //Other props must be ignored
         }
         pass();
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }
   }
   
   
   
   /**
    * 2.2.7.5.3
    * •  filterNotValid: The Repository MUST throw this exception if this property filter input parameter is not valid.
    * @throws Exception
    */
   public void testGetPropertiesOfLatestVersion_FilterNotValidException() throws Exception
   {
      System.out.print("Running testGetPropertiesOfLatestVersion_FilterNotValidException....       ");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      ContentStream cs2 = new BaseContentStream("zzz".getBytes(), null, new MimeType("text", "plain"));

      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MAJOR);
      String pwcID = getConnection().checkout(doc1.getObjectId());
      String chIn = getConnection().checkin(pwcID, true, null, cs2, "", null, null, null);
      try
      {
         CmisObject obj = getConnection().getPropertiesOfLatestVersion(doc1.getVersionSeriesId(), true, true, "(,*");
         doFail();
      }
      catch (FilterNotValidException ex){
         pass();
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }
   }
   
   
   /**
    * 2.2.7.5.3
    * •  objectNotFound:  The Repository MUST throw this exception if the input parameter major is TRUE and the Version Series contains no major versions.
    * @throws Exception
    */
   public void testGetPropertiesOfLatestVersion_ObjectNotFoundException() throws Exception
   {
      System.out.print("Running testGetPropertiesOfLatestVersion_ObjectNotFoundException....       ");
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      FolderData testroot =
         getStorage()
            .createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
      ContentStream cs2 = new BaseContentStream("zzz".getBytes(), null, new MimeType("text", "plain"));

      DocumentData doc1 =
         getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null,
            null, VersioningState.MINOR);
      String pwcID = getConnection().checkout(doc1.getObjectId());
      String chIn = getConnection().checkin(pwcID, false, null, cs2, "", null, null, null);
      try
      {
         CmisObject obj = getConnection().getPropertiesOfLatestVersion(doc1.getVersionSeriesId(), true, true, "");
         doFail();
      }
      catch (ObjectNotFoundException ex){
         pass();
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
      }
      finally
      {
         clear(testroot.getObjectId());
      }
   }
}
