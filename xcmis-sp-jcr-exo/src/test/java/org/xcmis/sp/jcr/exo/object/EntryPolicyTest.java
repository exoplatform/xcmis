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

package org.xcmis.sp.jcr.exo.object;

import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumRelationshipDirection;
import org.xcmis.sp.jcr.exo.JcrCMIS;
import org.xcmis.sp.jcr.exo.object.EntryImpl;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.object.BaseContentStream;
import org.xcmis.spi.object.ContentStream;
import org.xcmis.spi.object.Entry;

import java.util.Calendar;

import javax.jcr.Node;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class EntryPolicyTest extends EntryTest
{

   public void testAddRelationship() throws Exception
   {
      EntryImpl policy1 = createPolicy(testRootFolderId, "policy1", "");
      EntryImpl policy2 = createPolicy(testRootFolderId, "policy2", "");
      CmisTypeDefinitionType rtype = cmisRepository.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      Entry rel = policy1.addRelationship("relationship1", policy2, rtype);
      assertEquals(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP, rel.getScope());
      assertTrue(relationshipsNode.hasNode(policy1.getObjectId()));
      assertTrue(relationshipsNode.getNode(policy1.getObjectId()).hasNode("relationship1"));
      assertEquals(1, policy1.getNode().getReferences().getSize());
      assertEquals(1, policy2.getNode().getReferences().getSize());
   }

   public void testCanAddPolicy() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertTrue(policy.canAddPolicy());
   }

   public void testCanAddToFolder() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertFalse(policy.canAddToFolder());
   }

   public void testCanApplyPolicy() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertTrue(policy.canAddPolicy());
   }

   public void testCanCancelCheckOut() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertFalse(policy.canCancelCheckOut());
   }

   public void testCanCheckIn() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertFalse(policy.canCheckIn());
   }

   public void testCanCheckOut() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertFalse(policy.canCheckOut());
   }

   public void testCanCreateDocument() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertFalse(policy.canCreateDocument());
   }

   public void testCanCreateFolder() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertFalse(policy.canCreateFolder());
   }

   //   public void testCanCreatePolicy() throws Exception
   //   {
   //      Entry policy = createPolicy(testRootFolderId, "policy", "");
   //      assertFalse(policy.canCreatePolicy());
   //   }

   public void testCanCreateRelationship() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertTrue(policy.canCreateRelationship());
   }

   public void testCanDelete() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertTrue(policy.canDelete());
   }

   public void testCanDeleteContent() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertFalse(policy.canDeleteContent());
   }

   public void testCanDeleteTree() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertFalse(policy.canDeleteTree());
   }

   public void testCanGetAllVersions() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertFalse(policy.canGetAllVersions());
   }

   public void testCanGetAppliedPolicies() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertTrue(policy.canGetAppliedPolicies());
   }

   public void testCanGetChildren() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertFalse(policy.canGetChildren());
   }

   public void testCanGetContent() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertFalse(policy.canGetContent());
   }

   public void testCanGetDescendants() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertFalse(policy.canGetDescendants());
   }

   public void testCanGetFolderParent() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertFalse(policy.canGetFolderParent());
   }

   public void testCanGetParents() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertTrue(policy.canGetParents());
   }

   public void testCanGetProperties() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertTrue(policy.canGetProperties());
   }

   public void testCanGetRelationships() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertTrue(policy.canGetRelationships());
   }

   public void testCanMove() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertTrue(policy.canMove());
   }

   public void testCanRemoveFromFolder() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertFalse(policy.canRemoveFromFolder());
   }

   public void testCanRemovePolicy() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertTrue(policy.canRemovePolicy());
   }

   public void testCanSetContent() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertFalse(policy.canSetContent());
   }

   public void testCanUpdateProperties() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertTrue(policy.canUpdateProperties());
   }

   public void testCreateDocumentChild() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      try
      {
         policy.createChild(cmisRepository.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value()), "doc", null);
         fail("Policy may not have children");
      }
      catch (ConstraintException e)
      {
      }
   }

   public void testCreateFolderChild() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      try
      {
         policy
            .createChild(cmisRepository.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_FOLDER.value()), "folder", null);
         fail("Policy may not have children");
      }
      catch (ConstraintException e)
      {
      }
   }

   public void testCreateRelationshipChild() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      try
      {
         policy.createChild(cmisRepository.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value()),
            "relationship", null);
         fail("Policy may not have children");
      }
      catch (ConstraintException e)
      {
      }
   }

   public void testCreatePolicyChild() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      try
      {
         policy
            .createChild(cmisRepository.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_POLICY.value()), "policy", null);
         fail("Policy may not have children");
      }
      catch (ConstraintException e)
      {
      }
   }

   public void testGetChildren() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      try
      {
         policy.getChildren();
         fail("Policy may not have children");
      }
      catch (UnsupportedOperationException e)
      {
      }
   }

   public void testGetContentStream() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      try
      {
         policy.getContent(null);
         fail("ConstraintViolationException should be thrown.");
      }
      catch (ConstraintException e)
      {
      }
   }

   public void testGetParents() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertEquals(testRootFolderId, policy.getParents().get(0).getObjectId());
   }

   public void testGetType() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      CmisTypeDefinitionType type = policy.getType();
      assertEquals(EnumBaseObjectTypeIds.CMIS_POLICY, type.getBaseId());
      assertNotNull(type.getDescription());
      assertNotNull(type.getDisplayName());
      assertEquals(EnumBaseObjectTypeIds.CMIS_POLICY.value(), type.getId());
      assertEquals(EnumBaseObjectTypeIds.CMIS_POLICY.value(), type.getLocalName());
      assertNotNull(type.getLocalNamespace());
      assertNull(type.getParentId());
      assertNotNull(type.getPropertyDefinition());
      assertEquals(EnumBaseObjectTypeIds.CMIS_POLICY.value(), type.getQueryName());
   }

   public void testRemove() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      assertTrue(testRootFolder.getNode().hasNode("policy"));
      policy.delete();
      assertFalse(testRootFolder.getNode().hasNode("policy"));
   }

   public void testRemoveWithRelationship() throws Exception
   {
      // 1. Create two policy objects
      // 2. Add relationship policy1 -> policy2
      // 3. Check both policy objects has relationships
      // 4. Remove policy1, relationship will be removed also
      // 5. policy2 is untouched but has not any relationships any more.
      Entry policy1 = createPolicy(testRootFolderId, "policy1", "");
      Entry policy2 = createPolicy(testRootFolderId, "policy2", "");
      String policy2Id = policy2.getObjectId();
      policy1.addRelationship("relationship1", policy2, cmisRepository
         .getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value()));
      assertTrue(policy1.getRelationships(EnumRelationshipDirection.EITHER, true,
         cmisRepository.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value())).hasNext());
      assertTrue(policy2.getRelationships(EnumRelationshipDirection.EITHER, true,
         cmisRepository.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value())).hasNext());
      policy1.delete();
      try
      {
         // Must be untouched.
         cmisRepository.getObjectById(policy2Id);
      }
      catch (ObjectNotFoundException e)
      {
         fail("Must not be removed.");
      }
      assertFalse(policy2.getRelationships(EnumRelationshipDirection.EITHER, true,
         cmisRepository.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value())).hasNext());
   }

   public void testSetContentStream() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      try
      {
         ContentStream cs = new BaseContentStream(new byte[0], "test", "");
         policy.setContent(cs);
         policy.save();
         fail("Policy does not support stream.");
      }
      catch (StreamNotSupportedException snse)
      {
      }
   }

   public void testRemoveAppliedPolicy() throws Exception
   {
      Entry policy = createPolicy(testRootFolderId, "policy", "");
      String policyId = policy.getObjectId();
      Entry doc = createDocument(testRootFolderId, "doc", new byte[0], "");
      doc.applyPolicy(policy);
      doc.save();
      try
      {
         policy.delete();
         fail("Must not be removed, policy is applied to object.");
      }
      catch (ConstraintException e)
      {
      }
      doc.removePolicy(policy);
      doc.save();

      policy = cmisRepository.getObjectById(policyId);
      policy.delete();
      try
      {
         cmisRepository.getObjectById(policyId);
         fail("Must be removed.");
      }
      catch (ObjectNotFoundException e)
      {
      }
   }

   protected Node createNode() throws Exception
   {
      Node policy = testRootFolder.getNode().addNode("node", JcrCMIS.CMIS_NT_POLICY);
      policy.setProperty(CMIS.NAME, policy.getName());
      policy.setProperty(CMIS.OBJECT_TYPE_ID, "cmis:policy");
      policy.setProperty(CMIS.BASE_TYPE_ID, "cmis:policy");
      policy.setProperty(CMIS.CREATED_BY, credentials.getUserID());
      policy.setProperty(CMIS.CREATION_DATE, Calendar.getInstance());
      policy.setProperty(CMIS.LAST_MODIFIED_BY, credentials.getUserID());
      policy.setProperty(CMIS.LAST_MODIFICATION_DATE, Calendar.getInstance());
      policy.setProperty(CMIS.POLICY_TEXT, "test policy");
      session.save();
      return policy;
   }

}
