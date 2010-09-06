/*
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

import org.xcmis.spi.DocumentData;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.model.AccessControlEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@exoplatform.org">Sergey
 *         Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z ksm $
 *
 */
public class StorageTest extends BaseTest
{

   public void testAddAcl() throws Exception
   {
      DocumentData document = createDocument(rootFolder, "aclTest", documentTypeDefinition, null, null);
      String id = document.getObjectId();
      document.setACL(Collections.singletonList(new AccessControlEntry("root", new HashSet<String>(Arrays.asList(
         "cmis:read", "cmis:write")))));
      document = (DocumentData)storage.getObjectById(id);
      System.out.println(document.getACL(true));
   }

   public void testMultifiledChild() throws Exception
   {
      DocumentData document = createDocument(rootFolder, "multifiledChildTest", documentTypeDefinition, null, null);
      FolderData folder1 = createFolder(rootFolder, "multifiledChildFolderTest01");
      DocumentData child1 = createDocument(folder1, "child1", documentTypeDefinition, null, null);

      List<String> chs = new ArrayList<String>();
      for (ItemsIterator<ObjectData> children = folder1.getChildren(null); children.hasNext();)
      {
         chs.add(children.next().getObjectId());
      }
      assertEquals(1, chs.size());

      folder1.addObject(document);

      chs.clear();
      for (ItemsIterator<ObjectData> children = folder1.getChildren(null); children.hasNext();)
      {
         chs.add(children.next().getObjectId());
      }

      assertEquals(2, chs.size());
   }

   public void testMultifiling() throws Exception
   {
      DocumentData document = createDocument(rootFolder, "multifilingDocumentTest", documentTypeDefinition, null, null);
      FolderData folder1 = createFolder(rootFolder, "multifilingFolderTest1");
      FolderData folder2 = createFolder(rootFolder, "multifilingFolderTest2");
      FolderData folder3 = createFolder(rootFolder, "multifilingFolderTest3");
      FolderData folder4 = createFolder(rootFolder, "multifilingFolderTest4");
      folder1.addObject(document);
      folder2.addObject(document);
      folder3.addObject(document);
      folder4.addObject(document);

      Set<String> expectedParents =
         new HashSet<String>(Arrays.asList(rootFolder.getObjectId(), folder1.getObjectId(), folder2.getObjectId(),
            folder3.getObjectId(), folder4.getObjectId()));
      Collection<FolderData> parents = document.getParents();

      assertEquals(expectedParents.size(), parents.size());
      for (FolderData f : parents)
      {
         assertTrue("Folder " + f.getObjectId() + " must be in parents list.", expectedParents
            .contains(f.getObjectId()));
      }

      // remove from three folders and check parents again
      folder1.removeObject(document);
      folder3.removeObject(document);
      rootFolder.removeObject(document);
      expectedParents = new HashSet<String>(Arrays.asList(folder2.getObjectId(), folder4.getObjectId()));

      parents = document.getParents();

      assertEquals(expectedParents.size(), parents.size());
      for (FolderData f : parents)
      {
         assertTrue("Folder " + f.getObjectId() + " must be in parents list.", expectedParents
            .contains(f.getObjectId()));
      }
      //      System.out.println(" StorageTest.testMultifiling > new location: "
      //         + ((DocumentDataImpl)document).getNode().getPath());
   }

   public void testUnfiling() throws Exception
   {
      assertEquals(0, getSize(storage.getUnfiledObjectsId()));
      DocumentData document = createDocument(rootFolder, "unfilingDocumentTest", documentTypeDefinition, null, null);
      assertTrue(rootFolder.getChildren(null).hasNext());
      rootFolder.removeObject(document);
      assertFalse(rootFolder.getChildren(null).hasNext());

      Collection<FolderData> parents = document.getParents();
      assertEquals(0, parents.size());
      storage.getObjectById(document.getObjectId());

      assertEquals(1, getSize(storage.getUnfiledObjectsId()));
   }

   private int getSize(Iterator<String> iterator)
   {
      int result = 0;

      while (iterator.hasNext())
      {
         iterator.next();
         result++;
      }
      return result;
   }
}
