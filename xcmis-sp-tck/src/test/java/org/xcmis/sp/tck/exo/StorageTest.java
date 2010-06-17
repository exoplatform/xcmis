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

import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.CmisRegistry;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.model.RepositoryShortInfo;
import org.xcmis.spi.model.TypeDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id:  $
 */
public class StorageTest extends BaseTest
{

   @Override
   public void setUp() throws Exception
   {
      super.setUp();
   }

   public void testGeneral() throws Exception
   {
      assertNotNull(storageProvider);
      assertNotNull(storageProvider.getConnection());
      assertNotNull(storageProvider.getConnection().getStorage());
      assertNotNull(storageProvider.getConnection().getStorage().getId());
   }
   
   public void testGetRepositories()
   {
      Set<RepositoryShortInfo> storageInfos = CmisRegistry.getInstance().getStorageInfos();
      assertNotNull(storageInfos);
      assertFalse(storageInfos.isEmpty());
      for (RepositoryShortInfo repositoryShortInfo : storageInfos)
      {
         assertNotNull(repositoryShortInfo.getRepositoryId());
         assertNotNull(repositoryShortInfo.getRootFolderId());
      }
   }

   public void testGetRepositoryInfo() throws Exception
   {
      assertNotNull(getStorage().getRepositoryInfo());
      assertNotNull(getStorage().getRepositoryInfo().getRepositoryId());
      assertNotNull(getStorage().getRepositoryInfo().getRootFolderId());
      assertNotNull(getStorage().getRepositoryInfo().getCapabilities());
   }

   public void testGetTypeChildren()
   {

      // root types
      ItemsList<TypeDefinition> typeChildren0 = null;
      try
      {
         typeChildren0 = getConnection().getTypeChildren(null, true, -1, 0);
      }
      catch (TypeNotFoundException e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
      assertNotNull(typeChildren0);
      List<TypeDefinition> typeChildrenList = typeChildren0.getItems();
      int sizeOfRootTypes = typeChildrenList.size();
      assertNotNull(typeChildrenList);
      List<String> ll = new ArrayList<String>();
      ll.add(CmisConstants.DOCUMENT);
      ll.add(CmisConstants.FOLDER);
      for (TypeDefinition typeDefinition : typeChildrenList)
      {
         assertNotNull(typeDefinition);
         assertNotNull(typeDefinition.getId());
         assertNotNull(typeDefinition.getBaseId());
         assertEquals(typeDefinition.getId(), typeDefinition.getBaseId().value());
         if (ll.size() > 0)
            assertTrue(ll.contains(typeDefinition.getId()));
         ll.remove(typeDefinition.getId());
      }

      // root types with maxItems
      ItemsList<TypeDefinition> typeChildren3 = null;
      try
      {
         typeChildren3 = getConnection().getTypeChildren(null, true, 1, 0);
      }
      catch (TypeNotFoundException e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
      assertNotNull(typeChildren3);
      assertEquals(1, typeChildren3.getItems().size());

      // root types with skipCount
      ItemsList<TypeDefinition> typeChildren4 = null;
      try
      {
         typeChildren4 = getConnection().getTypeChildren(null, true, -1, sizeOfRootTypes - 1);
      }
      catch (TypeNotFoundException e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
      assertNotNull(typeChildren4);
      assertEquals(1, typeChildren4.getItems().size());

      // folder
      ItemsList<TypeDefinition> typeChildren1 = null;
      try
      {
         typeChildren1 = getConnection().getTypeChildren("cmis:folder", true, -1, 0);
      }
      catch (TypeNotFoundException e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
      assertNotNull(typeChildren1);
      assertFalse(typeChildren1.isHasMoreItems());

      // document
      ItemsList<TypeDefinition> typeChildren2 = null;
      try
      {
         typeChildren2 = getConnection().getTypeChildren("cmis:document", true, -1, 0);
      }
      catch (TypeNotFoundException e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
      assertNotNull(typeChildren2);
      assertFalse(typeChildren2.isHasMoreItems());

      // to get children for nonexistent type "cmis:kino"
      try
      {
         getConnection().getTypeChildren("cmis:kino", false, -1, 0);
      }
      catch (TypeNotFoundException e)
      {
         e.printStackTrace();
      }
      fail("The type definition \"cmis:kino\" shouldn't exist.'");
   }
   
//   public void testGetTypeDescendants()
//   {
//      
//   }


}
