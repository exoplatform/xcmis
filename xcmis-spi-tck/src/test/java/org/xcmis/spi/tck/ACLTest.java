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
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.model.impl.IdProperty;
import org.xcmis.spi.model.impl.StringProperty;
import org.xcmis.spi.utils.MimeType;

public class ACLTest extends BaseTest
{
   static FolderData testroot = null;

   String username = "username";

   static CapabilityACL capability = null;

   @BeforeClass
   public static void start() throws Exception
   {
      BaseTest.setUp();
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      testroot =
         getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "acl_testroot"),
            null, null);
      capability = getCapabilities().getCapabilityACL();
      System.out.print("Running ACL Service tests....");
   }

   /**
    * 2.2.10.1
    * Get the ACL currently applied to the specified document or folder object.
    * @throws Exception
    */
   @Test
   public void testGetACL_Simple() throws Exception
   {
      try
      {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         List<AccessControlEntry> addACL = createACL(username, "cmis:read");

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition,
               getPropsMap(CmisConstants.DOCUMENT, "testGetACL_Simple"), cs, addACL, null, VersioningState.NONE);
         List<AccessControlEntry> res = getConnection().getACL(doc1.getObjectId(), false);
         assertNotNull("Getting ACL failed.", res);
         for (AccessControlEntry one : res)
         {
            if (one.getPrincipal().equalsIgnoreCase(username))
            {
               assertTrue("Incorrect items number in result.", one.getPermissions().size() == 1);
               assertTrue("Setting ACL failed.", one.getPermissions().contains("cmis:read"));
            }
         }
      }
      catch (NotSupportedException ex)
      {
         if (capability.equals(CapabilityACL.NONE))
         {
            //SKIP
         }
         else
            fail("Capability ACL is supported but not supported exception thrown.");
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
      try
      {
         List<AccessControlEntry> addACL = createACL(username, "cmis:read");
         DocumentData doc1 = createDocument(testroot, "testApplyACL_Simple", "1234567890aBcDE");

         getConnection().applyACL(doc1.getObjectId(), addACL, null, AccessControlPropagation.REPOSITORYDETERMINED);
         ObjectData obj = getStorage().getObjectById(doc1.getObjectId());
         for (AccessControlEntry one : obj.getACL(false))
         {
            if (one.getPrincipal().equalsIgnoreCase(username))
            {
               assertTrue("Incorrect items number in result.", one.getPermissions().size() == 1);
               assertTrue("Setting ACL failed.", one.getPermissions().contains("cmis:read"));
            }
         }
      }
      catch (NotSupportedException ex)
      {
         if (capability.equals(CapabilityACL.NONE))
         {
            //SKIP
         }
         else
            fail("Capability ACL is supported but not supported exception thrown.");
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
      String typeID = null;
      DocumentData doc1 = null;
      try
      {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         List<AccessControlEntry> addACL = createACL(username, "cmis:read");

         Map<String, PropertyDefinition<?>> propertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         org.xcmis.spi.model.PropertyDefinition<?> propDefName =
            PropertyDefinitions.getPropertyDefinition(CmisConstants.DOCUMENT, CmisConstants.NAME);
         org.xcmis.spi.model.PropertyDefinition<?> propDefObjectTypeId =
            PropertyDefinitions.getPropertyDefinition(CmisConstants.DOCUMENT, CmisConstants.OBJECT_TYPE_ID);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(propDefName.getId(), propDefName.getQueryName(),
            propDefName.getLocalName(), propDefName.getDisplayName(), "testApplyACL_RemoveACE"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(propDefObjectTypeId.getId(), propDefObjectTypeId
            .getQueryName(), propDefObjectTypeId.getLocalName(), propDefObjectTypeId.getDisplayName(), "cmis:acl2"));

         TypeDefinition newType =
            new TypeDefinition("cmis:acl2", BaseType.DOCUMENT, "cmis:acl2", "cmis:acl2", "", "cmis:document",
               "cmis:acl2", "cmis:acl2", true, false, true, true, false, false, true, false, null, null,
               ContentStreamAllowed.ALLOWED, propertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

          doc1 =
            getStorage().createDocument(testroot, newType, properties, cs, null, null, VersioningState.NONE);
         getConnection().applyACL(doc1.getObjectId(), addACL, null, AccessControlPropagation.REPOSITORYDETERMINED);
         ObjectData obj = getStorage().getObjectById(doc1.getObjectId());
         for (AccessControlEntry one : obj.getACL(false))
         {
            assertTrue("Remove ACE failed.", one.getPrincipal().equalsIgnoreCase(username));
         }
         getStorage().deleteObject(doc1, true);
         getStorage().removeType(typeID);
      }
      catch (NotSupportedException ex)
      {
         if (capability.equals(CapabilityACL.NONE))
         {
            //SKIP
         }
         else
            fail("Capability ACL is supported but not supported exception thrown.");
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
      String typeID = null;
      DocumentData doc1 = null;
      try
      {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         List<AccessControlEntry> addACL = createACL(username, "cmis:read");

         Map<String, PropertyDefinition<?>> propertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         org.xcmis.spi.model.PropertyDefinition<?> propDefName =
            PropertyDefinitions.getPropertyDefinition(CmisConstants.DOCUMENT, CmisConstants.NAME);
         org.xcmis.spi.model.PropertyDefinition<?> popDefObjectTypeId =
            PropertyDefinitions.getPropertyDefinition(CmisConstants.DOCUMENT, CmisConstants.OBJECT_TYPE_ID);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(propDefName.getId(), propDefName.getQueryName(),
            propDefName.getLocalName(), propDefName.getDisplayName(), "testApplyACL_RemoveACE"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(popDefObjectTypeId.getId(), popDefObjectTypeId
            .getQueryName(), popDefObjectTypeId.getLocalName(), popDefObjectTypeId.getDisplayName(), "cmis:acl1"));

         TypeDefinition newType =
            new TypeDefinition("cmis:acl1", BaseType.DOCUMENT, "cmis:acl1", "cmis:acl1", "", "cmis:document",
               "cmis:acl1", "cmis:acl1", true, false, true, true, false, false, false, false, null, null,
               ContentStreamAllowed.ALLOWED, propertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         doc1 = getStorage().createDocument(testroot, newType, properties, cs, null, null, VersioningState.NONE);
         getConnection().applyACL(doc1.getObjectId(), addACL, null, AccessControlPropagation.OBJECTONLY);
         fail("Constraint exception must be thrown.");
      }
      catch (NotSupportedException ex)
      {
         if (capability.equals(CapabilityACL.NONE))
         {
            //SKIP
         }
         else
            fail("Capability ACL is supported but not supported exception thrown.");
      }
      catch (ConstraintException ec)
      {
         //OK
      }
      finally
      {
         if (doc1 != null)
            getStorage().deleteObject(doc1, true);
         getStorage().removeType(typeID);
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
      try
      {
         List<AccessControlEntry> addACL = createACL(username, "cmis:read");
         ACLCapability capability = getStorage().getRepositoryInfo().getAclCapability();
         DocumentData doc1 =
            createDocument(testroot, "testApplyACL_ConstraintExceptionACLPropagation", "1234567890aBcDE");

         if (capability.getPropagation().equals(AccessControlPropagation.OBJECTONLY)
            || capability.getPropagation().equals(AccessControlPropagation.REPOSITORYDETERMINED))
            getConnection().applyACL(doc1.getObjectId(), addACL, null, AccessControlPropagation.PROPAGATE);
         else if (capability.getPropagation().equals(AccessControlPropagation.PROPAGATE))
            getConnection().applyACL(doc1.getObjectId(), addACL, null, AccessControlPropagation.OBJECTONLY);
         fail("ConstraintException must be thrown.");
      }
      catch (ConstraintException ec)
      {
         //OK
      }
      catch (NotSupportedException ex)
      {
         if (capability.equals(CapabilityACL.NONE))
         {
            //SKIP
         }
         else
            fail("Capability ACL is supported but not supported exception thrown.");
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
      try
      {
         List<AccessControlEntry> addACL = createACL(username, "cmis:unknown");
         DocumentData doc1 = createDocument(testroot, "testApplyACL_ConstraintExceptionACLNotMatch", "1234567890aBcDE");
         getConnection().applyACL(doc1.getObjectId(), addACL, null, AccessControlPropagation.OBJECTONLY);
      }
      catch (ConstraintException ec)
      {
         //OK
      }
      catch (NotSupportedException ex)
      {
         if (capability.equals(CapabilityACL.NONE))
         {
            //SKIP
         }
         else
            fail("Capability ACL is supported but not supported exception thrown.");
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
