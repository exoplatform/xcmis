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
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

import org.xcmis.spi.BaseContentStream;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.DocumentData;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.NotSupportedException;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.model.ACLCapability;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.AccessControlPropagation;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.CapabilityACL;
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

public class ACLTest extends BaseTest
{

   @BeforeClass
   public static void start() throws Exception
   {
      BaseTest.setUp();
   }

   /**
    * 2.2.10.1
    * Get the ACL currently applied to the specified document or folder object.
    * @throws Exception
    */
   @Test
   public void testGetACL_Simple() throws Exception
   {
      String testname = "testGetACL_Simple";
      System.out.print("Running " + testname + "....                                              ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         AccessControlEntry acl = new AccessControlEntry();
         acl.setPrincipal("Makis");
         acl.getPermissions().add("cmis:read");
         ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
         addACL.add(acl);

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, addACL, null, VersioningState.MAJOR);
         try
         {
            List<AccessControlEntry> res = getConnection().getACL(doc1.getObjectId(), false);
            if (res == null)
               doFail(testname, "Getting ACL failed;");
            for (AccessControlEntry one : res)
            {
               if (one.getPrincipal().equalsIgnoreCase("Makis"))
               {
                  if (one.getPermissions().size() != 1)
                     doFail(testname, "Incorrect items number in result;");
                  if (!one.getPermissions().contains("cmis:read"))
                     doFail(testname, "Setting ACL failed");
               }
            }
            pass(testname);
         }
         catch (NotSupportedException ex)
         {
            if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
               skip("ACLTest.testGetACL_Simple");
            else
               doFail(testname, "Capability ACL is supported but not supported exception thrown;");
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
    * 2.2.10.2
    * Adds or removes the given ACEs to or from the ACL of document or folder object.
    * @throws Exception
    */
   @Test
   public void testApplyACL_Simple() throws Exception
   {
      String testname = "testApplyACL_Simple";
      System.out.print("Running " + testname + "....                                            ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         AccessControlEntry acl = new AccessControlEntry();
         acl.setPrincipal("Makis");
         acl.getPermissions().add("cmis:read");
         ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
         addACL.add(acl);

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);
         try
         {
            getConnection().applyACL(doc1.getObjectId(), addACL, null, AccessControlPropagation.OBJECTONLY);
            ObjectData obj = getStorage().getObjectById(doc1.getObjectId());
            for (AccessControlEntry one : obj.getACL(false))
            {
               if (one.getPrincipal().equalsIgnoreCase("Makis"))
               {
                  if (one.getPermissions().size() != 1)
                     doFail(testname, "Incorrect items number in result;");
                  if (!one.getPermissions().contains("cmis:read"))
                     doFail(testname, "Setting ACL failed");
               }
            }
            pass(testname);
         }
         catch (NotSupportedException ex)
         {
            if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
               skip("ACLTest.testApplyACL_Simple");
            else
               doFail(testname, "Capability ACL is supported but not supported exception thrown");
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
    * 2.2.10.2
    * Adds or removes the given ACEs to or from the ACL of document or folder object.
    * @throws Exception
    */
   @Test
   public void testApplyACL_RemoveACE() throws Exception
   {
      String testname = "testApplyACL_RemoveACE";
      System.out.print("Running " + testname + "....                                         ");
      FolderData testroot = null;
      String typeID = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         AccessControlEntry acl = new AccessControlEntry();
         acl.setPrincipal("Makis");
         acl.getPermissions().add("cmis:read");
         ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
         addACL.add(acl);

         Map<String, PropertyDefinition<?>> propertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         org.xcmis.spi.model.PropertyDefinition<?> propDefName =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "doc1", true, null, null);
         org.xcmis.spi.model.PropertyDefinition<?> popDefObjectTypeId =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(propDefName.getId(), propDefName.getQueryName(),
            propDefName.getLocalName(), propDefName.getDisplayName(), "doc1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(popDefObjectTypeId.getId(), popDefObjectTypeId
            .getQueryName(), popDefObjectTypeId.getLocalName(), popDefObjectTypeId.getDisplayName(), "cmis:kino"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document",
               "cmis:kino", "cmis:kino", true, false, true, true, false, false, true, false, null, null,
               ContentStreamAllowed.ALLOWED, propertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         DocumentData doc1 =
            getStorage().createDocument(testroot, newType, properties, cs, null, null, VersioningState.MAJOR);
         try
         {
            getConnection().applyACL(doc1.getObjectId(), addACL, null, AccessControlPropagation.OBJECTONLY);
            ObjectData obj = getStorage().getObjectById(doc1.getObjectId());
            for (AccessControlEntry one : obj.getACL(false))
            {
               if (!one.getPrincipal().equalsIgnoreCase("Makis"))
               {
                  doFail(testname, "Remove ACE failed;");
               }
            }
            pass(testname);
         }
         catch (NotSupportedException ex)
         {
            if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
               skip("ACLTest.testApplyACL_RemoveACE");
            else
               doFail(testname, "Capability ACL is supported but not supported exception thrown");
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
    * 2.2.10.2.3
    * The specified object's Object-Type definition's attribute for controllableACL is FALSE.
    * @throws Exception
    */
   @Test
   public void testApplyACL_ConstraintExceptionACL() throws Exception
   {
      String testname = "testApplyACL_ConstraintExceptionACL";
      System.out.print("Running " + testname + "....                               ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         AccessControlEntry acl = new AccessControlEntry();
         acl.setPrincipal("Makis");
         acl.getPermissions().add("cmis:read");
         ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
         addACL.add(acl);

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, addACL, null, VersioningState.MAJOR);
         try
         {
            getConnection().applyACL(doc1.getObjectId(), null, addACL, AccessControlPropagation.OBJECTONLY);
            doFail(testname, "Constraint exception must be thrown;");
         }
         catch (NotSupportedException ex)
         {
            if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
               skip("ACLTest." + testname);
            else
               doFail(testname, "Capability ACL is supported but not supported exception thrown");
         }
         catch (ConstraintException ec)
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
    * 2.2.10.2.3
    * The value for ACLPropagation does not match the values as returned via getACLCapabilities.
    * @throws Exception
    */
   @Test
   public void testApplyACL_ConstraintExceptionACLPropagation() throws Exception
   {
      String testname = "testApplyACL_ConstraintExceptionACLPropagation";
      System.out.print("Running " + testname + "....                              ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "testroot"),
               null, null);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         AccessControlEntry acl = new AccessControlEntry();
         acl.setPrincipal("Makis");
         acl.getPermissions().add("cmis:read");
         ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
         addACL.add(acl);

         ACLCapability capability = getStorage().getRepositoryInfo().getAclCapability();

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);
         try
         {
            if (capability.getPropagation().equals(AccessControlPropagation.OBJECTONLY)
               || capability.getPropagation().equals(AccessControlPropagation.REPOSITORYDETERMINED))
               getConnection().applyACL(doc1.getObjectId(), addACL, null, AccessControlPropagation.PROPAGATE);
            else if (capability.getPropagation().equals(AccessControlPropagation.PROPAGATE))
               getConnection().applyACL(doc1.getObjectId(), addACL, null, AccessControlPropagation.OBJECTONLY);
            doFail(testname, "ConstraintException must be thrown;");
         }
         catch (ConstraintException ec)
         {
            pass(testname);
         }
         catch (NotSupportedException ex)
         {
            if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
               skip("ACLTest." + testname);
            else
               doFail(testname, "Capability ACL is supported but not supported exception thrown");
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
    * 2.2.10.2.3
    * At least one of the specified values for permission in ANY of the ACEs does not match ANY of the permissionNames as 
    * returned by getACLCapability and is not a CMIS Basic permission
    * @throws Exception
    */
   @Test
   public void testApplyACL_ConstraintExceptionACLNotMatch() throws Exception
   {
      String testname = "testApplyACL_ConstraintExceptionACLNotMatch";
      System.out.print("Running " + testname + "....                        ");
      FolderData testroot = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null,
               null);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         AccessControlEntry acl = new AccessControlEntry();
         acl.setPrincipal("Makis");
         acl.getPermissions().add("cmis:unknown");
         ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
         addACL.add(acl);

         ACLCapability capability = getStorage().getRepositoryInfo().getAclCapability();

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs,
               null, null, VersioningState.MAJOR);
         try
         {
            getConnection().applyACL(doc1.getObjectId(), addACL, null, AccessControlPropagation.OBJECTONLY);
         }
         catch (ConstraintException ec)
         {
            pass(testname);
         }
         catch (NotSupportedException ex)
         {
            if (getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
               skip("ACLTest." + testname);
            else
               doFail(testname, "Capability ACL is supported but not supported exception thrown");
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

   protected void pass(String method) throws Exception
   {
      super.pass("ACLTest." + method);
   }

   protected void doFail(String method, String message) throws Exception
   {
      super.doFail("ACLTest." + method, message);
   }

   @AfterClass
   public static void stop() throws Exception
   {
      if (BaseTest.conn != null)
         BaseTest.conn.close();
   }
}
