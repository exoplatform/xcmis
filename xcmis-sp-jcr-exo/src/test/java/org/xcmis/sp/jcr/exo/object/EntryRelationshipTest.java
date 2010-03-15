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

import org.xcmis.core.CmisAccessControlEntryType;
import org.xcmis.core.CmisAccessControlPrincipalType;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumBasicPermissions;
import org.xcmis.core.EnumRelationshipDirection;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.core.ExtendedSession;
import org.xcmis.sp.jcr.exo.JcrCMIS;
import org.xcmis.sp.jcr.exo.object.EntryImpl;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.object.BaseContentStream;
import org.xcmis.spi.object.ContentStream;
import org.xcmis.spi.object.Entry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: EntryRelationshipTest.java 27 2010-02-08 07:49:20Z andrew00x $
 */
public class EntryRelationshipTest extends EntryTest
{

   @Override
   public void testAddPermission() throws Exception
   {
      Node node = createNode();
      Entry entry = new EntryImpl(node);
      CmisAccessControlEntryType ace = new CmisAccessControlEntryType();
      CmisAccessControlPrincipalType principal1 = new CmisAccessControlPrincipalType();
      principal1.setPrincipalId("exo1");
      ace.setPrincipal(principal1);
      ace.getPermission().add(EnumBasicPermissions.CMIS_READ.value());
      List<CmisAccessControlEntryType> acl = new ArrayList<CmisAccessControlEntryType>();
      acl.add(ace);
      try
      {
         entry.addPermissions(acl);
         fail("Relationships is not controllable by ACL.");
      }
      catch (ConstraintException ce)
      {
      }
   }

   @Override
   public void testAddPolicy() throws Exception
   {
      Node node = createNode();
      Entry entry = new EntryImpl(node);
      EntryImpl policy1 = createPolicy(testRootFolderId, "policy1", "");
      try
      {
         entry.applyPolicy(policy1);
         fail("Relationships is not controllable by policy.");
      }
      catch (ConstraintException ce)
      {
      }
   }

   public void testAddRelationship() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      try
      {
         relsp.addRelationship("relationship1", relsp, source.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP
            .value()));
         fail("Only independent objects may have relationships.");
      }
      catch (ConstraintException cve)
      {
      }
   }

   public void testCanAddPolicy() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertFalse(relsp.canAddPolicy());
   }

   public void testCanAddToFolder() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertFalse(relsp.canAddToFolder());
   }

   public void testCanApplyACL() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertFalse(relsp.canApplyACL());
   }

   public void testCanCancelCheckOut() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertFalse(relsp.canCancelCheckOut());
   }

   public void testCanCheckIn() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertFalse(relsp.canCheckIn());
   }

   public void testCanCheckOut() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertFalse(relsp.canCheckOut());
   }

   public void testCanCreateDocument() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertFalse(relsp.canCreateDocument());
   }

   public void testCanCreateFolder() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertFalse(relsp.canCreateFolder());
   }

   // ////////////////////////////////////////

   //   public void testCanCreatePolicy() throws Exception
   //   {
   //      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
   //      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
   //      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
   //      assertFalse(relsp.canCreatePolicy());
   //   }

   public void testCanCreateRelationship() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertFalse(relsp.canCreateRelationship());
   }

   public void testCanDelete() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertTrue(relsp.canDelete());
   }

   public void testCanDeleteContent() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertFalse(relsp.canDeleteContent());
   }

   public void testCanDeleteTree() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertFalse(relsp.canDeleteTree());
   }

   public void testCanGetAllVersions() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertFalse(relsp.canGetAllVersions());
   }

   public void testCanGetAppliedPolicies() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertFalse(relsp.canGetAppliedPolicies());
   }

   public void testCanGetChildren() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertFalse(relsp.canGetChildren());
   }

   public void testCanGetDescendants() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertFalse(relsp.canGetDescendants());
   }

   public void testCanGetFolderParent() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertFalse(relsp.canGetFolderParent());
   }

   public void testCanGetParents() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertFalse(relsp.canGetParents());
   }

   public void testCanGetProperties() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertTrue(relsp.canGetProperties());
   }

   public void testCanGetRelationships() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertFalse(relsp.canGetRelationships());
   }

   public void testCanMove() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertFalse(relsp.canMove());
   }

   public void testCanRemoveFromFolder() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertFalse(relsp.canRemoveFromFolder());
   }

   public void testCanRemovePolicy() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertFalse(relsp.canRemovePolicy());
   }

   public void testCanSetContent() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertFalse(relsp.canSetContent());
   }

   public void testCanUpdateProperties() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertTrue(relsp.canUpdateProperties());
   }

   public void testCanViewContent() throws Exception
   {
      EntryImpl source = createDocument(testRootFolderId, "source", new byte[0], "");
      EntryImpl target = createDocument(testRootFolderId, "target", new byte[0], "");
      EntryImpl relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertFalse(relsp.canGetContent());
   }

   public void testCreateChild() throws Exception
   {
      Entry source = createDocument(testRootFolderId, "source", new byte[0], "");
      Entry target = createDocument(testRootFolderId, "target", new byte[0], "");
      Entry relsp = createRelationship(source.getObjectId(), target.getObjectId());
      try
      {
         relsp.createChild(((EntryImpl)relsp).getTypeDefinition(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value()), "doc1",
            null);
         fail();
      }
      catch (ConstraintException cve)
      {
      }
   }

   public void testCreateChild2() throws Exception
   {
      Entry source = createDocument(testRootFolderId, "source", new byte[0], "");
      Entry target = createDocument(testRootFolderId, "target", new byte[0], "");
      Entry relsp = createRelationship(source.getObjectId(), target.getObjectId());
      try
      {
         relsp.createChild(((EntryImpl)relsp).getTypeDefinition(EnumBaseObjectTypeIds.CMIS_FOLDER.value()), "folder1",
            null);
         fail();
      }
      catch (ConstraintException cve)
      {
      }
   }

   public void testCreateChild3() throws Exception
   {
      Entry source = createDocument(testRootFolderId, "source", new byte[0], "");
      Entry target = createDocument(testRootFolderId, "target", new byte[0], "");
      Entry relsp = createRelationship(source.getObjectId(), target.getObjectId());
      try
      {
         relsp.createChild(((EntryImpl)relsp).getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value()),
            "relationship1", null);
         fail();
      }
      catch (ConstraintException cve)
      {
      }
   }

   public void testGetChildren() throws Exception
   {
      Entry source = createDocument(testRootFolderId, "source", new byte[0], "");
      Entry target = createDocument(testRootFolderId, "target", new byte[0], "");
      Entry relsp = createRelationship(source.getObjectId(), target.getObjectId());
      try
      {
         relsp.getChildren();
         fail();
      }
      catch (UnsupportedOperationException e)
      {
      }
   }

   public void testGetContentStream() throws Exception
   {
      Entry source = createDocument(testRootFolderId, "source", new byte[0], "");
      Entry target = createDocument(testRootFolderId, "target", new byte[0], "");
      Entry relsp = createRelationship(source.getObjectId(), target.getObjectId());

      try
      {
         relsp.getContent(null);
         fail("ConstraintViolationException should be thrown.");
      }
      catch (ConstraintException e)
      {
      }

   }

   public void testGetParent() throws Exception
   {
      Entry source = createDocument(testRootFolderId, "source", new byte[0], "");
      Entry target = createDocument(testRootFolderId, "target", new byte[0], "");
      Entry relsp = createRelationship(source.getObjectId(), target.getObjectId());
      try
      {
         relsp.getParents();
         fail("Relationship object should not have parent.");
      }
      catch (UnsupportedOperationException e)
      {
      }
   }

   public void testGetType() throws Exception
   {
      Entry source = createDocument(testRootFolderId, "source", new byte[0], "");
      Entry target = createDocument(testRootFolderId, "target", new byte[0], "");
      Entry relsp = createRelationship(source.getObjectId(), target.getObjectId());
      assertEquals(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP, relsp.getScope());
      CmisTypeDefinitionType type = relsp.getType();
      assertEquals(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP, type.getBaseId());
      assertEquals(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value(), type.getQueryName());
      assertEquals(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value(), type.getDisplayName());
      assertNull(type.getParentId());
      assertEquals(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value(), type.getId());
   }

   public void testRemove() throws Exception
   {
      Entry source = createDocument(testRootFolderId, "source", new byte[0], "");
      String sourceId = source.getObjectId();
      Calendar sourceLastMod = source.getDate(CMIS.LAST_MODIFICATION_DATE);
      Entry target = createDocument(testRootFolderId, "target", new byte[0], "");
      String targetId = target.getObjectId();
      Entry relsp = createRelationship(source.getObjectId(), target.getObjectId());
      Calendar targetLastMod = target.getDate(CMIS.LAST_MODIFICATION_DATE);
      assertEquals(1, source.getRelationships(EnumRelationshipDirection.EITHER, true, null).size());
      assertEquals(1, target.getRelationships(EnumRelationshipDirection.EITHER, true, null).size());

      String relspId = relsp.getObjectId();
      relsp.delete();
      try
      {
         ((ExtendedSession)session).getNodeByIdentifier(relspId);
         fail("Relationship must be removed.");
      }
      catch (ItemNotFoundException e)
      {
      }
      try
      {
         // Source must be untouched
         cmisRepository.getObjectById(sourceId);
      }
      catch (ObjectNotFoundException e)
      {
         fail();
      }
      try
      {
         // Target must be untouched
         cmisRepository.getObjectById(targetId);
      }
      catch (ObjectNotFoundException e)
      {
         fail();
      }
      assertEquals(sourceLastMod, source.getDate(CMIS.LAST_MODIFICATION_DATE));
      assertEquals(targetLastMod, target.getDate(CMIS.LAST_MODIFICATION_DATE));
      assertEquals(0, source.getRelationships(EnumRelationshipDirection.EITHER, true, null).size());
      assertEquals(0, target.getRelationships(EnumRelationshipDirection.EITHER, true, null).size());
   }

   @Override
   public void testRemovePermissions() throws Exception
   {
      Node node = createNode();
      Entry entry = new EntryImpl(node);
      CmisAccessControlEntryType ace = new CmisAccessControlEntryType();
      CmisAccessControlPrincipalType principal1 = new CmisAccessControlPrincipalType();
      principal1.setPrincipalId("exo1");
      ace.setPrincipal(principal1);
      ace.getPermission().add(EnumBasicPermissions.CMIS_READ.value());
      List<CmisAccessControlEntryType> acl = new ArrayList<CmisAccessControlEntryType>();
      acl.add(ace);
      try
      {
         entry.removePermissions(acl);
         fail("Relationships is not controllable by ACL.");
      }
      catch (ConstraintException ce)
      {
      }
   }

   public void testSetContentStream() throws Exception
   {
      Entry source = createDocument(testRootFolderId, "source", new byte[0], "");
      Entry target = createDocument(testRootFolderId, "target", new byte[0], "");
      Entry relsp = createRelationship(source.getObjectId(), target.getObjectId());
      try
      {
         ContentStream cs = new BaseContentStream(new byte[0], "test", "");
         relsp.setContent(cs);
         relsp.save();
         fail();
      }
      catch (StreamNotSupportedException snse)
      {
      }
   }

   @Override
   public void testGetPermission() throws Exception
   {
      Node node = createNode();
      node.addMixin("exo:privilegeable");
      ((ExtendedNode)node).setPermission("exo1", new String[]{"add_node", "read", "remove", "set_property"});
      node.save();
      Entry entry = new EntryImpl(node);
      try
      {
         entry.getPermissions();
         fail("Relationships is not controllable by ACL.");
      }
      catch (ConstraintException e)
      {
      }
   }

   @Override
   protected Node createNode() throws Exception
   {
      Node hierarchy = relationshipsNode.addNode("source", JcrCMIS.NT_UNSTRUCTURED);
      Node relationship = hierarchy.addNode("target", JcrCMIS.CMIS_NT_RELATIONSHIP);
      relationship.setProperty(CMIS.NAME, relationship.getName());
      relationship.setProperty(CMIS.OBJECT_TYPE_ID, "cmis:relationship");
      relationship.setProperty(CMIS.BASE_TYPE_ID, "cmis:relationship");
      relationship.setProperty(CMIS.CREATED_BY, credentials.getUserID());
      relationship.setProperty(CMIS.CREATION_DATE, Calendar.getInstance());
      relationship.setProperty(CMIS.SOURCE_ID, "source");
      relationship.setProperty(CMIS.TARGET_ID, "target");
      relationship.setProperty(CMIS.LAST_MODIFIED_BY, credentials.getUserID());
      relationship.setProperty(CMIS.LAST_MODIFICATION_DATE, Calendar.getInstance());
      session.save();
      return relationship;
   }

}
