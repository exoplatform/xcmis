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

package org.xcmis.sp.inmemory;

import org.xcmis.core.CmisAccessControlEntryType;
import org.xcmis.core.CmisAccessControlPrincipalType;
import org.xcmis.core.EnumBasicPermissions;
import org.xcmis.core.EnumVersioningState;
import org.xcmis.sp.inmemory.RepositoryImpl;
import org.xcmis.spi.object.Entry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class EntryTest extends BaseTest
{

   public void setUp() throws Exception
   {
      super.setUp();
   }

   public void testApplyACL() throws Exception
   {
      Entry doc =
         repository.getRootFolder().createChild(repository.getTypeDefinition("cmis:document"), "doc1",
            EnumVersioningState.MAJOR);

      CmisAccessControlEntryType aceRead = new CmisAccessControlEntryType();
      CmisAccessControlPrincipalType principal1 = new CmisAccessControlPrincipalType();
      principal1.setPrincipalId("exo");
      aceRead.setPrincipal(principal1);
      aceRead.getPermission().add(EnumBasicPermissions.CMIS_READ.value());

      CmisAccessControlEntryType aceAll = new CmisAccessControlEntryType();
      CmisAccessControlPrincipalType principal2 = new CmisAccessControlPrincipalType();
      principal2.setPrincipalId("root");
      aceAll.setPrincipal(principal2);
      aceAll.getPermission().add(EnumBasicPermissions.CMIS_ALL.value());

      List<CmisAccessControlEntryType> acl = new ArrayList<CmisAccessControlEntryType>();
      acl.add(aceRead);
      acl.add(aceAll);
      List<CmisAccessControlEntryType> result = doc.addPermissions(acl);
      assertEquals(2, result.size());
      for (CmisAccessControlEntryType i : result)
      {
         if (i.getPrincipal().getPrincipalId().equals("root"))
         {
            assertEquals(1, i.getPermission().size());
            assertEquals(EnumBasicPermissions.CMIS_ALL.value(), i.getPermission().get(0));
         }
         else if (i.getPrincipal().getPrincipalId().equals("exo"))
         {
            assertEquals(1, i.getPermission().size());
            assertEquals(EnumBasicPermissions.CMIS_READ.value(), i.getPermission().get(0));
         }
         else
         {
            fail("Unknown principal " + i.getPrincipal().getPrincipalId());
         }
      }

      Map<String, Set<String>> acl1 = ((RepositoryImpl)repository).storage.getACLs().get(doc.getObjectId());
      assertNotNull(acl1);
      assertEquals(1, acl1.get("root").size());
      assertEquals(1, acl1.get("exo").size());
      assertEquals(EnumBasicPermissions.CMIS_ALL.value(), acl1.get("root").iterator().next());
      assertEquals(EnumBasicPermissions.CMIS_READ.value(), acl1.get("exo").iterator().next());
   }

   public void testRemoveACL() throws Exception
   {
      Entry doc =
         repository.getRootFolder().createChild(repository.getTypeDefinition("cmis:document"), "doc1",
            EnumVersioningState.MAJOR);
      Map<String, Set<String>> permissions = new HashMap<String, Set<String>>();
      Set<String> rootPerm = new HashSet<String>();
      rootPerm.add(EnumBasicPermissions.CMIS_READ.value());
      rootPerm.add(EnumBasicPermissions.CMIS_WRITE.value());
      permissions.put("root", rootPerm);
      Set<String> exoPerm = new HashSet<String>();
      exoPerm.add(EnumBasicPermissions.CMIS_READ.value());
      exoPerm.add(EnumBasicPermissions.CMIS_WRITE.value());
      permissions.put("exo", exoPerm);
      Set<String> andrewPerm = new HashSet<String>();
      andrewPerm.add(EnumBasicPermissions.CMIS_ALL.value());
      permissions.put("andrew", andrewPerm);

      ((RepositoryImpl)repository).storage.getACLs().put(doc.getObjectId(), permissions);

      CmisAccessControlEntryType exoACL = new CmisAccessControlEntryType();
      CmisAccessControlPrincipalType exo = new CmisAccessControlPrincipalType();
      exo.setPrincipalId("exo");
      exoACL.setPrincipal(exo);
      exoACL.getPermission().add(EnumBasicPermissions.CMIS_WRITE.value());

      CmisAccessControlEntryType rootACL = new CmisAccessControlEntryType();
      CmisAccessControlPrincipalType root = new CmisAccessControlPrincipalType();
      root.setPrincipalId("root");
      rootACL.setPrincipal(root);
      rootACL.getPermission().add(EnumBasicPermissions.CMIS_ALL.value());

      CmisAccessControlEntryType andrewACL = new CmisAccessControlEntryType();
      CmisAccessControlPrincipalType andrew = new CmisAccessControlPrincipalType();
      andrew.setPrincipalId("andrew");
      andrewACL.setPrincipal(andrew);
      andrewACL.getPermission().add(EnumBasicPermissions.CMIS_WRITE.value());

      List<CmisAccessControlEntryType> remove = new ArrayList<CmisAccessControlEntryType>();
      remove.add(exoACL);
      remove.add(rootACL);
      remove.add(andrewACL);

      doc.removePermissions(remove);

      Map<String, Set<String>> acl1 = ((RepositoryImpl)repository).storage.getACLs().get(doc.getObjectId());
      assertNotNull(acl1);
      // All ACEs removed
      assertNull(acl1.get("root"));
      assertEquals(1, acl1.get("exo").size());
      // Write permission is removed.
      assertEquals(EnumBasicPermissions.CMIS_READ.value(), acl1.get("exo").iterator().next());
      assertEquals(1, acl1.get("andrew").size());
      assertEquals(EnumBasicPermissions.CMIS_READ.value(), acl1.get("andrew").iterator().next());
   }

   public void testRemoveChild() throws Exception
   {
      Entry doc =
         repository.getRootFolder().createChild(repository.getTypeDefinition("cmis:document"), "doc1",
            EnumVersioningState.MAJOR);
      Entry folder1 =
         repository.getRootFolder().createChild(repository.getTypeDefinition("cmis:folder"), "folder1", null);
      Entry folder2 =
         repository.getRootFolder().createChild(repository.getTypeDefinition("cmis:folder"), "folder2", null);
      folder1.addChild(doc);
      folder2.addChild(doc);

      assertEquals(1, folder1.getChildren().size());
      assertEquals(1, folder2.getChildren().size());
      assertEquals(3, repository.getRootFolder().getChildren().size());

      Set<Entry> expected = new HashSet<Entry>();
      expected.add(repository.getRootFolder());
      expected.add(folder1);
      expected.add(folder2);

      List<Entry> parents = doc.getParents();
      assertEquals(expected.size(), parents.size());
      assertEquals(true, expected.containsAll(parents));

      // Remove from folder1
      folder1.removeChild(doc.getObjectId());
      expected.remove(folder1);
      parents = doc.getParents();
      assertEquals(expected.size(), parents.size());
      assertEquals(true, expected.containsAll(parents));
      assertEquals(0, folder1.getChildren().size());
      assertEquals(1, folder2.getChildren().size());
      assertEquals(3, repository.getRootFolder().getChildren().size());

      // Remove doc from root folder.
      repository.getRootFolder().removeChild(doc.getObjectId());
      expected.remove(repository.getRootFolder());
      parents = doc.getParents();
      assertEquals(expected.size(), parents.size());
      assertEquals(true, expected.containsAll(parents));
      assertEquals(0, folder1.getChildren().size());
      assertEquals(1, folder2.getChildren().size());
      assertEquals(2, repository.getRootFolder().getChildren().size());

   }

}
