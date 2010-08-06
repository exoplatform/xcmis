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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
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
import org.xcmis.spi.PolicyData;
import org.xcmis.spi.model.BaseType;
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

public class PolicyTest extends BaseTest
{

   static FolderData testroot = null;
   
   @BeforeClass
   public static void start() throws Exception
   {
      BaseTest.setUp();
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      testroot =
         getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, "policy_testroot"),
            null, null);
      System.out.print("Running Policy Service tests....");
   }

   /**
    * 2.2.9.1
    * Applies a specified policy to an object.
    * @throws Exception
    */
   @Test
   public void testApplyPolicy_Simple() throws Exception
   {
      if (!IS_POLICIES_SUPPORTED)
      {
         //SKIP
         return;
      }
      PolicyData policy = null;
      try
      {
         String policyName = "testApplyPolicy_Simple_policy1";
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));
         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "testApplyPolicy_Simple"),
               cs, null, null, VersioningState.MAJOR);

         policy = createPolicy(testroot, policyName);
         getConnection().applyPolicy(policy.getObjectId(), doc1.getObjectId());
         ObjectData res = getStorage().getObjectById(doc1.getObjectId());
         assertTrue("Policies number incorrect;", res.getPolicies().size() == 1);
         Iterator<PolicyData> it = res.getPolicies().iterator();
         while (it.hasNext())
         {
            PolicyData one = it.next();
            assertTrue("Policy name does not match;",one.getName().equals(policyName));
            assertTrue("Policy text does not match;",one.getPolicyText().equals("testPolicyText"));
            res.removePolicy(one);
         }
      }
      finally
      {
         getStorage().deleteObject(policy, false);
      }
   }

   /**
    * 2.2.9.1.2
    * constraint : The Repository MUST throw this exception if the specified object's Object-Type 
    * definition's attribute for controllablePolicy is FALSE.
    * @throws Exception
    */
   @Test
   public void testApplyPolicy_ConstraintException() throws Exception
   {
      if (!IS_POLICIES_SUPPORTED)
      {
         //SKIP
         return;
      }
      DocumentData doc1 = null;
      PolicyData policy = null;
      String typeID = null;
      try
      {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         Map<String, PropertyDefinition<?>> kinoPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefName2 =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "doc1", true, null, null);
         org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefObjectTypeId2 =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(kinoPropDefName2.getId(), kinoPropDefName2
            .getQueryName(), kinoPropDefName2.getLocalName(), kinoPropDefName2.getDisplayName(), "testApplyPolicy_ConstraintException"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(kinoPropDefObjectTypeId2.getId(),
            kinoPropDefObjectTypeId2.getQueryName(), kinoPropDefObjectTypeId2.getLocalName(), kinoPropDefObjectTypeId2
               .getDisplayName(), "cmis:policytype1"));

         TypeDefinition newType =
            new TypeDefinition("cmis:policytype1", BaseType.DOCUMENT, "cmis:policytype1", "cmis:policytype1", "", "cmis:document",
               "cmis:policytype1", "cmis:policytype1", true, false, true, true, false, false, false, false, null, null,
               ContentStreamAllowed.ALLOWED, kinoPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

          doc1 =
            getStorage().createDocument(testroot, newType, properties, cs, null, null, VersioningState.MAJOR);

         policy = createPolicy(testroot, "testApplyPolicy_ConstraintException_policy1");
         getConnection().applyPolicy(policy.getObjectId(), doc1.getObjectId());
         fail("ConstraintException must be thrown;");
      }
      catch (ConstraintException ex)
      {
         //OK
      }
      finally
      {
         getStorage().deleteObject(doc1, false);
         getStorage().deleteObject(policy, false);
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.9.2
    * Removes a specified policy from an object.
    * @throws Exception
    */
   @Test
   public void testRemovePolicy_Simple() throws Exception
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

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs,
               null, null, VersioningState.MAJOR);

         policy = createPolicy(testroot, "testRemovePolicy_Simple_policy1");
         getConnection().applyPolicy(policy.getObjectId(), doc1.getObjectId());
         getConnection().removePolicy(policy.getObjectId(), doc1.getObjectId());
         ObjectData res = getStorage().getObjectById(doc1.getObjectId());
         assertTrue("Policy removing error;", res.getPolicies().size() == 0);
         assertNotNull("Policy object deleted;",getStorage().getObjectById(policy.getObjectId()));
      }
      finally
      {
         getStorage().deleteObject(policy, false);
      }
   }

   /**
    * 2.2.9.2.2
    * •  constraint: The Repository MUST throw this exception if the specified object's Object-Type 
    *  definition's attribute for controllablePolicy is FALSE.
    * @throws Exception
    */
   @Test
   public void testRemovePolicy_ConstraintException() throws Exception
   {
      if (!IS_POLICIES_SUPPORTED)
      {
         //SKIP
         return;
      }
      String typeID = null;
      PolicyData policy = null;
      DocumentData doc1 = null;
      try
      {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         Map<String, PropertyDefinition<?>> kinoPropertyDefinitions = new HashMap<String, PropertyDefinition<?>>();
         org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefName2 =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.NAME, PropertyType.STRING, CmisConstants.NAME,
               CmisConstants.NAME, null, CmisConstants.NAME, true, false, false, false, false, Updatability.READWRITE,
               "doc1", true, null, null);
         org.xcmis.spi.model.PropertyDefinition<?> kinoPropDefObjectTypeId2 =
            PropertyDefinitions.createPropertyDefinition(CmisConstants.OBJECT_TYPE_ID, PropertyType.ID,
               CmisConstants.OBJECT_TYPE_ID, CmisConstants.OBJECT_TYPE_ID, null, CmisConstants.OBJECT_TYPE_ID, false,
               false, false, false, false, Updatability.READONLY, "type_id1", null, null, null);

         Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
         properties.put(CmisConstants.NAME, new StringProperty(kinoPropDefName2.getId(), kinoPropDefName2
            .getQueryName(), kinoPropDefName2.getLocalName(), kinoPropDefName2.getDisplayName(), "testRemovePolicy_ConstraintException1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(kinoPropDefObjectTypeId2.getId(),
            kinoPropDefObjectTypeId2.getQueryName(), kinoPropDefObjectTypeId2.getLocalName(), kinoPropDefObjectTypeId2
               .getDisplayName(), "cmis:policytype2"));

         TypeDefinition newType =
            new TypeDefinition("cmis:policytype2", BaseType.DOCUMENT, "cmis:policytype2", "cmis:policytype2", "", "cmis:document",
               "cmis:policytype2", "cmis:policytype2", true, false, true, true, false, true, false, false, null, null,
               ContentStreamAllowed.ALLOWED, kinoPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         doc1 =
            getStorage().createDocument(testroot, newType, properties, cs, null, null, VersioningState.MAJOR);

         policy = createPolicy(testroot, "testRemovePolicy_ConstraintException_policy1");
         getConnection().applyPolicy(policy.getObjectId(), doc1.getObjectId());
         getConnection().removePolicy(policy.getObjectId(), doc1.getObjectId());
         fail("Constraint exception must be thrown;");
      }
      catch (ConstraintException ex)
      {
        //OK
      }
      finally
      {
         getStorage().deleteObject(doc1, true);
         if (policy != null)
            getStorage().deleteObject(policy, false);
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.9.3
    * Gets the list of policies currently applied to the specified object.
    * @throws Exception
    */
   @Test
   public void testGetAppliedPolicies_Simple() throws Exception
   {
      if (!IS_POLICIES_SUPPORTED)
      {
         //SKIP
         return;
      }
      PolicyData policy = null;
      DocumentData doc1 = null;
      try
      {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

          doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "testGetAppliedPolicies_Simple1"),
               cs, null, null, VersioningState.MAJOR);

         policy = createPolicy(testroot, "testGetAppliedPolicies_Simple_policy1");
         getConnection().applyPolicy(policy.getObjectId(), doc1.getObjectId());
         List<CmisObject> res = getConnection().getAppliedPolicies(doc1.getObjectId(), true, "");
         assertNotNull("getAppliedPolicies() failed;", res);
         for (CmisObject one : res)
         {
            assertNotNull("ObjectInfo is not present in result;", one.getObjectInfo());
            assertTrue("Not a policy type object;", one.getObjectInfo().getTypeId().equals(CmisConstants.POLICY));
        }
      }
      finally
      {
         getStorage().deleteObject(doc1, true);
         getStorage().deleteObject(policy, false);
      }
   }

   /**
    * 2.2.9.3.1
    * Repositories SHOULD return only the properties specified in the property filter 
    * if they exist on the object’s type definition.
    * @throws Exception
    */
   @Test
   public void testGetAppliedPolicies_PropertiesFiltered() throws Exception
   {
      if (!IS_POLICIES_SUPPORTED)
      {
         //SKIP
         return;
      }
      PolicyData policy = null;
      DocumentData doc1 = null;
      try
      {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "testGetAppliedPolicies_PropertiesFiltered1"), cs,
               null, null, VersioningState.MAJOR);

         policy = createPolicy(testroot, "testGetAppliedPolicies_policy1");
         getConnection().applyPolicy(policy.getObjectId(), doc1.getObjectId());
         List<CmisObject> res = getConnection().getAppliedPolicies(doc1.getObjectId(), true, "cmis:name, cmis:path");
         for (CmisObject one : res)
         {
            for (Map.Entry<String, Property<?>> e : one.getProperties().entrySet())
            {
               if (e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path")) //Other props must be ignored
                  continue;
               else
                 fail("Property filter works incorrect;");
            }
         }
      }
      finally
      {
         if (doc1 != null)
         getStorage().deleteObject(doc1, true);
         if (policy != null)
         getStorage().deleteObject(policy, false);
      }
   }

   /**
    * 2.2.9.3.3
    * •   filterNotValid: The Repository MUST throw this exception if this property filter input parameter is not valid.
    * @throws Exception
    */
   @Test
   public void testGetAppliedPolicies_FilterNotValidException() throws Exception
   {
      if (!IS_POLICIES_SUPPORTED)
      {
         //SKIP
         return;
      }
      PolicyData policy = null;
      DocumentData doc1 = null;
      try
      {
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "testGetAppliedPolicies_FilterNotValidException1"),
               cs, null, null, VersioningState.MAJOR);

         policy = createPolicy(testroot, "testGetAppliedPolicies_FilterNotValidException_policy1");
         getConnection().applyPolicy(policy.getObjectId(), doc1.getObjectId());
         List<CmisObject> res = getConnection().getAppliedPolicies(doc1.getObjectId(), true, "(,*");
        fail("FilterNotValidException must be thrown;");
      }
      catch (FilterNotValidException ex)
      {
         //OK
      }
      finally
      {
         getStorage().deleteObject(doc1, true);
         getStorage().deleteObject(policy, false);
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
