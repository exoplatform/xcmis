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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ItemsTree;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.TypeDefinition;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PolicyTest extends BaseTest
{

   private static TypeDefinition folderType;

   private static TypeDefinition policyType;

   private static String controllablePolicyObject;

   private static TypeDefinition controllablePolicyType;

   private static String notControllablePolicyObject;

   private static TypeDefinition notControllablePolicyType;

   private static String testRootFolderId;

   @BeforeClass
   public static void start() throws Exception
   {
      BaseTest.setUp();
      folderType = connection.getTypeDefinition(CmisConstants.FOLDER);
      if (isPoliciesSupported)
      {
         policyType = connection.getTypeDefinition(CmisConstants.POLICY);
      }
      testRootFolderId = createFolder(rootFolderID, folderType.getId(), "policy_testroot", null, null, null);
      List<ItemsTree<TypeDefinition>> allTypes = connection.getTypeDescendants(null, -1, true);
      controllablePolicyType = getControllablePolicyType(allTypes);
      notControllablePolicyType = getNotControllablePolicyType(allTypes);
      if (controllablePolicyType != null)
      {
         switch (controllablePolicyType.getBaseId())
         {
            case DOCUMENT :
               controllablePolicyObject =
                  createDocument(testRootFolderId, controllablePolicyType.getId(), generateName(controllablePolicyType,
                     null), null, null, null, null, null);
               break;
            case FOLDER :
               controllablePolicyObject =
                  createFolder(testRootFolderId, controllablePolicyType.getId(), generateName(controllablePolicyType,
                     null), null, null, null);
               break;
            case POLICY :
               controllablePolicyObject =
                  createPolicy(testRootFolderId, controllablePolicyType.getId(), generateName(controllablePolicyType,
                     null), null, null, null, null);
               break;
            case RELATIONSHIP :
               String sourceId =
                  createDocument(testRootFolderId, CmisConstants.DOCUMENT, generateName(connection
                     .getTypeDefinition(CmisConstants.DOCUMENT), null), null, null, null, null, null);
               String targetId =
                  createDocument(testRootFolderId, CmisConstants.DOCUMENT, generateName(connection
                     .getTypeDefinition(CmisConstants.DOCUMENT), null), null, null, null, null, null);
               controllablePolicyObject =
                  createRelationship(controllablePolicyType.getId(), generateName(controllablePolicyType, null),
                     sourceId, targetId, null, null, null);
               break;
         }
      }
      if (notControllablePolicyType != null)
      {
         switch (notControllablePolicyType.getBaseId())
         {
            case DOCUMENT :
               notControllablePolicyObject =
                  createDocument(testRootFolderId, notControllablePolicyType.getId(), generateName(
                     notControllablePolicyType, null), null, null, null, null, null);
               break;
            case FOLDER :
               notControllablePolicyObject =
                  createFolder(testRootFolderId, notControllablePolicyType.getId(), generateName(
                     notControllablePolicyType, null), null, null, null);
               break;
            case POLICY :
               notControllablePolicyObject =
                  createPolicy(testRootFolderId, notControllablePolicyType.getId(), generateName(
                     notControllablePolicyType, null), null, null, null, null);
               break;
            case RELATIONSHIP :
               String sourceId =
                  createDocument(testRootFolderId, CmisConstants.DOCUMENT, generateName(connection
                     .getTypeDefinition(CmisConstants.DOCUMENT), null), null, null, null, null, null);
               String targetId =
                  createDocument(testRootFolderId, CmisConstants.DOCUMENT, generateName(connection
                     .getTypeDefinition(CmisConstants.DOCUMENT), null), null, null, null, null, null);
               notControllablePolicyObject =
                  createRelationship(notControllablePolicyType.getId(), generateName(notControllablePolicyType, null),
                     sourceId, targetId, null, null, null);
               break;
         }
      }
      System.out.println("Running Policy Service tests");
   }

   @AfterClass
   public static void stop() throws Exception
   {
      if (testRootFolderId != null)
      {
         clear(testRootFolderId);
      }
   }

   /**
    * 2.2.9.1 applyPolicy.
    *
    * @throws Exception
    */
   @Test
   public void testApplyPolicy() throws Exception
   {
      if (!isPoliciesSupported || controllablePolicyObject == null)
      {
         return;
      }

      String policy =
         createPolicy(policyType.isFileable() ? testRootFolderId : null, policyType.getId(), generateName(policyType,
            null), "policy1", null, null, null);

      connection.applyPolicy(policy, controllablePolicyObject);
      List<CmisObject> policies = connection.getAppliedPolicies(controllablePolicyObject, true, null);
      assertTrue(policies.size() >= 1);
      Set<String> policiesId = new HashSet<String>(policies.size());
      for (CmisObject o : policies)
      {
         policiesId.add(o.getObjectInfo().getId());
      }
      assertTrue("Expected policy is not found. ", policiesId.contains(policy));
   }

   /**
    * 2.2.9.1 applyPolicy.
    * <p>
    * {@link ConstraintException} must be thrown if the specified object's
    * object type definition's attribute for controllablePolicy is
    * <code>false</code>.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testApplyPolicy_ConstraintException() throws Exception
   {
      if (!isPoliciesSupported || notControllablePolicyObject == null)
      {
         return;
      }
      String policy =
         createPolicy(policyType.isFileable() ? testRootFolderId : null, policyType.getId(), generateName(policyType,
            null), "policy1", null, null, null);
      try
      {
         connection.applyPolicy(policy, notControllablePolicyObject);
         fail("ConstraintException must be thrown. ");
      }
      catch (ConstraintException e)
      {
      }
   }

   /**
    * 2.2.9.2 removePolicy.
    * <p>
    * {@link ConstraintException} must be thrown if the specified object's
    * object type definition's attribute for controllablePolicy is
    * <code>false</code>.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testRemovePolicy_ConstraintException() throws Exception
   {
      if (!isPoliciesSupported || notControllablePolicyObject == null)
      {
         return;
      }
      try
      {
         // Do not send any policies to be removed.
         // ConstraintException must be thrown without checking any policies
         // since object is not controllable by policy.
         connection.removePolicy(null, notControllablePolicyObject);
         fail("ConstraintException must be thrown. ");
      }
      catch (ConstraintException e)
      {
      }
   }

   /**
    * 2.2.9.2 removePolicy.
    *
    * @throws Exception
    */
   @Test
   public void testRemovePolicy_Simple() throws Exception
   {
      if (!isPoliciesSupported || controllablePolicyObject == null)
      {
         //SKIP
         return;
      }
      String policy =
         createPolicy(policyType.isFileable() ? testRootFolderId : null, policyType.getId(), generateName(policyType,
            null), "policy1", null, null, null);

      connection.applyPolicy(policy, controllablePolicyObject);
      connection.removePolicy(policy, controllablePolicyObject);
      List<CmisObject> policies = connection.getAppliedPolicies(controllablePolicyObject, true, null);
      Set<String> policiesId = new HashSet<String>(policies.size());
      for (CmisObject o : policies)
      {
         policiesId.add(o.getObjectInfo().getId());
      }
      assertFalse("Policy " + policy + " must be removed. ", policiesId.contains(policy));
   }

}
