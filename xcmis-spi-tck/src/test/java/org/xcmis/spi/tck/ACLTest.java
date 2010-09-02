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

import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ItemsTree;
import org.xcmis.spi.NotSupportedException;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.AccessControlPropagation;
import org.xcmis.spi.model.CapabilityACL;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.utils.CmisUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ACLTest extends BaseTest
{
   private static String testRootFolderId;

   private static TypeDefinition controllableAclType;

   private static TypeDefinition notControllableAclType;

   private static String controllableAclObject;

   private static String notControllableAclObject;

   private static String principal = "principal0";

   @BeforeClass
   public static void start() throws Exception
   {
      BaseTest.setUp();

      testRootFolderId = createFolder(rootFolderID, CmisConstants.FOLDER, "acl_testroot", null, null, null);

      List<ItemsTree<TypeDefinition>> allTypes = connection.getTypeDescendants(null, -1, false);
      controllableAclType = getControllableAclType(allTypes);
      notControllableAclType = getNotControllableAclType(allTypes);

      if (controllableAclType != null)
      {
         switch (controllableAclType.getBaseId())
         {
            case DOCUMENT :
               controllableAclObject =
                  createDocument(testRootFolderId, controllableAclType.getId(),
                     generateName(controllableAclType, null), null, null, null, null, null);
               break;
            case FOLDER :
               controllableAclObject =
                  createFolder(testRootFolderId, controllableAclType.getId(), generateName(controllableAclType, null),
                     null, null, null);
               break;
            case POLICY :
               controllableAclObject =
                  createPolicy(testRootFolderId, controllableAclType.getId(), generateName(controllableAclType, null),
                     null, null, null, null);
               break;
            case RELATIONSHIP :
               String sourceId =
                  createDocument(testRootFolderId, CmisConstants.DOCUMENT, generateName(connection
                     .getTypeDefinition(CmisConstants.DOCUMENT), null), null, null, null, null, null);
               String targetId =
                  createDocument(testRootFolderId, CmisConstants.DOCUMENT, generateName(connection
                     .getTypeDefinition(CmisConstants.DOCUMENT), null), null, null, null, null, null);
               controllableAclObject =
                  createRelationship(controllableAclType.getId(), generateName(controllableAclType, null), sourceId,
                     targetId, null, null, null);
               break;
         }
      }
      if (notControllableAclType != null)
      {
         switch (notControllableAclType.getBaseId())
         {
            case DOCUMENT :
               notControllableAclObject =
                  createDocument(testRootFolderId, notControllableAclType.getId(), generateName(notControllableAclType,
                     null), null, null, null, null, null);
               break;
            case FOLDER :
               notControllableAclObject =
                  createFolder(testRootFolderId, notControllableAclType.getId(), generateName(notControllableAclType,
                     null), null, null, null);
               break;
            case POLICY :
               notControllableAclObject =
                  createPolicy(testRootFolderId, notControllableAclType.getId(), generateName(notControllableAclType,
                     null), null, null, null, null);
               break;
            case RELATIONSHIP :
               String sourceId =
                  createDocument(testRootFolderId, CmisConstants.DOCUMENT, generateName(connection
                     .getTypeDefinition(CmisConstants.DOCUMENT), null), null, null, null, null, null);
               String targetId =
                  createDocument(testRootFolderId, CmisConstants.DOCUMENT, generateName(connection
                     .getTypeDefinition(CmisConstants.DOCUMENT), null), null, null, null, null, null);
               controllableAclObject =
                  createRelationship(notControllableAclType.getId(), generateName(notControllableAclType, null),
                     sourceId, targetId, null, null, null);
               break;
         }
      }
      System.out.println("Running ACL Service tests");
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
    * Find first type which supports ACL.
    *
    * @param types tree of all available types
    * @return type which support ACL or <code>null</code> if there is no such
    *         type
    * @throws Exception if any error occurs
    */
   private static TypeDefinition getControllableAclType(List<ItemsTree<TypeDefinition>> types) throws Exception
   {
      for (ItemsTree<TypeDefinition> item : types)
      {
         TypeDefinition container = item.getContainer();
         if (container.isControllableACL())
         {
            return container;
         }
         List<ItemsTree<TypeDefinition>> children = item.getChildren();
         if (children != null && !children.isEmpty())
         {
            return getControllableAclType(children);
         }
      }
      return null;
   }

   /**
    * Find first type which does not support ACL.
    *
    * @param types tree of all available types
    * @return type which does not support ACL or <code>null</code> if there is
    *         no such type
    * @throws Exception if any error occurs
    */
   private static TypeDefinition getNotControllableAclType(List<ItemsTree<TypeDefinition>> types) throws Exception
   {
      for (ItemsTree<TypeDefinition> item : types)
      {
         TypeDefinition container = item.getContainer();
         if (!container.isControllableACL())
         {
            return container;
         }
         List<ItemsTree<TypeDefinition>> children = item.getChildren();
         if (children != null && !children.isEmpty())
         {
            return getNotControllableAclType(children);
         }
      }
      return null;
   }

   /**
    * 2.2.10.2 Adds or removes the given ACEs to or from the ACL of document or
    * folder object.
    *
    * @throws Exception
    */
   @Test
   public void testApplyACL_Add() throws Exception
   {
      if (controllableAclObject == null)
      {
         return;
      }

      if (capabilities.getCapabilityACL() == CapabilityACL.MANAGE)
      {
         List<AccessControlEntry> acl = createACL(principal, "cmis:write");
         try
         {
            connection.applyACL(controllableAclObject, acl, null, aclCapability.getPropagation());
            List<AccessControlEntry> actualACL = connection.getACL(controllableAclObject, false);
            validateACL(acl);
            checkACL(acl, actualACL);
         }
         finally
         {
            // Restore previous ACL.
            connection.applyACL(controllableAclObject, null, acl, aclCapability.getPropagation());
         }
      }
   }

   /**
    * 2.2.10.2.3 At least one of the specified values for permission in ANY of
    * the ACEs does not match ANY of the permissionNames as returned by
    * getACLCapability and is not a CMIS Basic permission
    *
    * @throws Exception
    */
   @Test
   public void testApplyACL_ConstraintException_ACLNotMatch() throws Exception
   {
      if (notControllableAclObject == null)
      {
         return;
      }
      if (capabilities.getCapabilityACL() == CapabilityACL.MANAGE)
      {
         List<AccessControlEntry> acl = createACL(principal, "cmis:unknown");
         try
         {
            connection.applyACL(notControllableAclObject, acl, null, aclCapability.getPropagation());
            fail("ConstraintException must be thrown since type is not controllable by ACL.");
         }
         catch (ConstraintException e)
         {
         }
      }
   }

   /**
    * 2.2.10.2.3 The value for ACLPropagation does not match the values as
    * returned via getACLCapabilities.
    *
    * @throws Exception
    */
   @Test
   public void testApplyACL_ConstraintException_ACLPropagation() throws Exception
   {
      if (controllableAclObject == null)
      {
         return;
      }
      if (capabilities.getCapabilityACL() == CapabilityACL.MANAGE)
      {
         int l = AccessControlPropagation.values().length;
         AccessControlPropagation propagation = aclCapability.getPropagation();
         // Propagation which is not supported.
         int ord = propagation.ordinal();
         int p = ord == l - 1 ? ord - 1 : ord + 1;
         AccessControlPropagation propagation1 = AccessControlPropagation.values()[p];
         if (propagation != AccessControlPropagation.REPOSITORYDETERMINED)
         {
            try
            {
               connection.applyACL(controllableAclObject, createACL(principal, "cmis:write"), null, propagation1);
            }
            catch (ConstraintException e)
            {
            }
         }
      }
   }

   /**
    * 2.2.10.2.3 The specified object's Object-Type definition's attribute for
    * controllableACL is FALSE.
    *
    * @throws Exception
    */
   @Test
   public void testApplyACL_ConstraintException_NotControllable() throws Exception
   {
      if (notControllableAclObject == null)
      {
         return;
      }
      if (capabilities.getCapabilityACL() == CapabilityACL.MANAGE)
      {
         List<AccessControlEntry> acl = createACL(principal, "cmis:write");
         try
         {
            connection.applyACL(notControllableAclObject, acl, null, aclCapability.getPropagation());
            fail("ConstraintException must be thrown since type is not controllable by ACL.");
         }
         catch (ConstraintException e)
         {
         }
      }
   }

   /**
    * Managing of ACL is not supported but discovering may be supported.
    *
    * @throws Exception
    */
   @Test
   public void testApplyACL_NotSupportedException() throws Exception
   {
      // If managing is not supported then try to apply ACL to any type and
      // expect for org.xcmis.spi.NotSupportedException.
      if (controllableAclObject != null && capabilities.getCapabilityACL() != CapabilityACL.MANAGE)
      {
         List<AccessControlEntry> acl = createACL(principal, "cmis:write");
         try
         {
            connection.applyACL(controllableAclObject, acl, null, aclCapability.getPropagation());
            fail("NotSupportedException must be thrown since managing of ACL is not supported.");
         }
         catch (NotSupportedException e)
         {
         }
      }
   }

   /**
    * 2.2.10.1 Get the ACL currently applied to the specified document or folder
    * object.
    *
    * @throws Exception
    */
   @Test
   public void testGetACL_Simple() throws Exception
   {
      if (controllableAclObject == null)
      {
         return;
      }

      List<AccessControlEntry> actualACL = connection.getACL(controllableAclObject, false);
      if (actualACL.size() > 0)
      {
         // May contains some ACEs which are inherited from parent or assigned by repository itself.
         Map<String, Set<String>> m1 = new HashMap<String, Set<String>>();
         CmisUtils.addAclToPermissionMap(m1, actualACL);
         validateACL(actualACL);
      }
      else if (capabilities.getCapabilityACL() == CapabilityACL.MANAGE)
      {
         // If capability is MANAGE then try add and retrieve ACL.
         List<AccessControlEntry> acl = createACL(principal, "cmis:write");
         try
         {
            connection.applyACL(controllableAclObject, acl, null, aclCapability.getPropagation());
            actualACL = connection.getACL(controllableAclObject, false);
            validateACL(actualACL);
            checkACL(acl, actualACL);
         }
         finally
         {
            // Restore previous ACL.
            connection.applyACL(controllableAclObject, null, acl, aclCapability.getPropagation());
         }
      }
   }

}
