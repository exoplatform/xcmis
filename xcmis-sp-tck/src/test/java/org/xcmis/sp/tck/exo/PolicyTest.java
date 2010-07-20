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
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.PolicyData;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.TypeNotFoundException;
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

public class PolicyTest extends BaseTest
{

   /**
    * 2.2.9.1
    * Applies a specified policy to an object.
    * @throws Exception
    */
   public void testApplyPolicy_Simple() throws Exception
   {
      String testname = "testApplyPolicy_Simple";
      System.out.print("Running " + testname + "....                                         ");
      try
      {
         getStorage().getTypeDefinition(CmisConstants.POLICY, false);
      }
      catch (TypeNotFoundException ex)
      {
         skip("PolicyTest.testApplyPolicy_Simple");
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
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap(CmisConstants.DOCUMENT, "doc1"),
               cs, null, null, VersioningState.MAJOR);

         policy = createPolicy(testroot, "policy1");
         try
         {
            getConnection().applyPolicy(policy.getObjectId(), doc1.getObjectId());
            ObjectData res = getStorage().getObjectById(doc1.getObjectId());
            if (res.getPolicies().size() != 1)
               doFail(testname, "Policies number incorrect;");
            Iterator<PolicyData> it = res.getPolicies().iterator();
            while (it.hasNext())
            {
               PolicyData one = it.next();
               if (!one.getName().equals("policy1"))
                  doFail(testname, "Policy name does not match;");
               if (!one.getPolicyText().equals("testPolicyText"))
                  doFail(testname, "Policy text does not match;");
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
         if (testroot != null)
            clear(testroot.getObjectId());
         getStorage().deleteObject(policy, false);
      }
   }

   /**
    * 2.2.9.1.2
    * constraint : The Repository MUST throw this exception if the specified object’s Object-Type 
    * definition’s attribute for controllablePolicy is FALSE.
    * @throws Exception
    */

   public void testApplyPolicy_ConstraintException() throws Exception
   {
      String testname = "testApplyPolicy_ConstraintException";
      System.out.print("Running " + testname + "....                            ");
      try
      {
         getStorage().getTypeDefinition(CmisConstants.POLICY, false);
      }
      catch (TypeNotFoundException ex)
      {
         skip("PolicyTest.testApplyPolicy_ConstraintException");
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
            .getQueryName(), kinoPropDefName2.getLocalName(), kinoPropDefName2.getDisplayName(), "doc1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(kinoPropDefObjectTypeId2.getId(),
            kinoPropDefObjectTypeId2.getQueryName(), kinoPropDefObjectTypeId2.getLocalName(), kinoPropDefObjectTypeId2
               .getDisplayName(), "cmis:kino"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document",
               "cmis:kino", "cmis:kino", true, false, true, true, false, false, false, false, null, null,
               ContentStreamAllowed.ALLOWED, kinoPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         DocumentData doc1 =
            getStorage().createDocument(testroot, newType, properties, cs, null, null, VersioningState.MAJOR);

         policy = createPolicy(testroot, "policy1");
         try
         {
            getConnection().applyPolicy(policy.getObjectId(), doc1.getObjectId());
            doFail(testname, "ConstraintException must be thrown;" );
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
         getStorage().deleteObject(policy, false);
         getStorage().removeType(typeID);
      }
   }

   /**
    * 2.2.9.2
    * Removes a specified policy from an object.
    * @throws Exception
    */
   public void testRemovePolicy_Simple() throws Exception
   {
      String testname = "testRemovePolicy_Simple";
      System.out.print("Running " + testname + "....                                        ");
      try
      {
         getStorage().getTypeDefinition(CmisConstants.POLICY, false);
      }
      catch (TypeNotFoundException ex)
      {
         skip("PolicyTest.testRemovePolicy_Simple");
         return;
      }
      FolderData testroot = null;
      PolicyData policy = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null,
               null);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs,
               null, null, VersioningState.MAJOR);

         policy = createPolicy(testroot, "policy1");
         getConnection().applyPolicy(policy.getObjectId(), doc1.getObjectId());
         try
         {
            getConnection().removePolicy(policy.getObjectId(), doc1.getObjectId());
            ObjectData res = getStorage().getObjectById(doc1.getObjectId());
            if (res.getPolicies().size() != 0)
               doFail(testname, "Policy removing error;");
            if (getStorage().getObjectById(policy.getObjectId()) == null)
               doFail(testname, "Policy object deleted;");
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
         getStorage().deleteObject(policy, false);
      }
   }

   /**
    * 2.2.9.2.2
    * •  constraint: The Repository MUST throw this exception if the specified object’s Object-Type 
    *  definition’s attribute for controllablePolicy is FALSE.
    * @throws Exception
    */
   public void testRemovePolicy_ConstraintException() throws Exception
   {
      String testname = "testRemovePolicy_ConstraintException";
      System.out.print("Running " + testname + "....                           ");
      try
      {
         getStorage().getTypeDefinition(CmisConstants.POLICY, false);
      }
      catch (TypeNotFoundException ex)
      {
         skip("PolicyTest.testRemovePolicy_ConstraintException");
         return;
      }
      FolderData testroot = null;
      String typeID = null;
      PolicyData policy = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null,
               null);
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
            .getQueryName(), kinoPropDefName2.getLocalName(), kinoPropDefName2.getDisplayName(), "doc1"));
         properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(kinoPropDefObjectTypeId2.getId(),
            kinoPropDefObjectTypeId2.getQueryName(), kinoPropDefObjectTypeId2.getLocalName(), kinoPropDefObjectTypeId2
               .getDisplayName(), "cmis:kino"));

         TypeDefinition newType =
            new TypeDefinition("cmis:kino", BaseType.DOCUMENT, "cmis:kino", "cmis:kino", "", "cmis:document",
               "cmis:kino", "cmis:kino", true, false, true, true, false, true, false, false, null, null,
               ContentStreamAllowed.ALLOWED, kinoPropertyDefinitions);
         typeID = getStorage().addType(newType);
         newType = getStorage().getTypeDefinition(typeID, true);

         DocumentData doc1 =
            getStorage().createDocument(testroot, newType, properties, cs, null, null, VersioningState.MAJOR);

         policy = createPolicy(testroot, "policy1");
         getConnection().applyPolicy(policy.getObjectId(), doc1.getObjectId());
         try
         {
            getConnection().removePolicy(policy.getObjectId(), doc1.getObjectId());
            doFail(testname, "Constraint exception must be thrown;");
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
   public void testGetAppliedPolicies_Simple() throws Exception
   {
      String testname = "testGetAppliedPolicies_Simple";
      System.out.print("Running " + testname + "....                                  ");
      FolderData testroot = null;
      PolicyData policy = null;
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

         policy = createPolicy(testroot, "policy1");
         getConnection().applyPolicy(policy.getObjectId(), doc1.getObjectId());
         try
         {
            List<CmisObject> res = getConnection().getAppliedPolicies(doc1.getObjectId(), true, "");
            if (res == null)
               doFail(testname, "getAppliedPolicies() failed;");
            for (CmisObject one : res)
            {
               if (one.getObjectInfo() == null)
                  doFail(testname, "ObjectInfo is not present in result;");
               if (!one.getObjectInfo().getTypeId().equals("cmis:policy"))
                  doFail(testname, "Not a policy type object;");
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
         getStorage().deleteObject(policy, false);
      }
   }

   /**
    * 2.2.9.3.1
    * Repositories SHOULD return only the properties specified in the property filter 
    * if they exist on the object’s type definition.
    * @throws Exception
    */
   public void testGetAppliedPolicies_PropertiesFiltered() throws Exception
   {
      String testname = "testGetAppliedPolicies_PropertiesFiltered";
      System.out.print("Running " + testname + "....                      ");
      FolderData testroot = null;
      PolicyData policy = null;
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

         testroot =
            getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null,
               null);
         ContentStream cs = new BaseContentStream("1234567890aBcDE".getBytes(), null, new MimeType("text", "plain"));

         DocumentData doc1 =
            getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs,
               null, null, VersioningState.MAJOR);

         policy = createPolicy(testroot, "policy1");
         getConnection().applyPolicy(policy.getObjectId(), doc1.getObjectId());
         try
         {
            List<CmisObject> res = getConnection().getAppliedPolicies(doc1.getObjectId(), true, "cmis:name, cmis:path");
            for (CmisObject one : res)
            {
               for (Map.Entry<String, Property<?>> e : one.getProperties().entrySet())
               {
                  if (e.getKey().equalsIgnoreCase("cmis:name") || e.getKey().equalsIgnoreCase("cmis:path")) //Other props must be ignored
                     continue;
                  else
                     doFail(testname, "Property filter works incorrect;");
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
         getStorage().deleteObject(policy, false);
      }
   }

   /**
    * 2.2.9.3.3
    * •   filterNotValid: The Repository MUST throw this exception if this property filter input parameter is not valid.
    * @throws Exception
    */
   public void testGetAppliedPolicies_FilterNotValidException() throws Exception
   {
      String testname = "testGetAppliedPolicies_FilterNotValidException";
      System.out.print("Running " + testname + "....                 ");
      FolderData testroot = null;
      PolicyData policy = null;
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

         policy = createPolicy(testroot, "policy1");
         getConnection().applyPolicy(policy.getObjectId(), doc1.getObjectId());
         try
         {
            List<CmisObject> res = getConnection().getAppliedPolicies(doc1.getObjectId(), true, "(,*");
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
         getStorage().deleteObject(policy, false);
      }
   }

   protected void pass(String method) throws Exception
   {
      super.pass("PolicyTest." + method);
   }
   
   protected void doFail( String method,  String message) throws Exception
   {
      super.doFail("PolicyTest." + method,  message);
   }
}
