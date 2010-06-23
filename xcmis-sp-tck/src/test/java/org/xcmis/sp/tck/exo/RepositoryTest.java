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
import org.xcmis.spi.ItemsTree;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.RepositoryShortInfo;
import org.xcmis.spi.model.TypeDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id:  $
 */
public class RepositoryTest extends BaseTest
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
         assertFalse(repositoryShortInfo.getRepositoryId().isEmpty());
         assertNotNull(repositoryShortInfo.getRepositoryName());
         assertFalse(repositoryShortInfo.getRepositoryName().isEmpty());
         assertNotNull(repositoryShortInfo.getRootFolderId());
      }
   }

   public void testGetRepositoryInfo() throws Exception
   {
      assertNotNull(getStorage().getRepositoryInfo());
      assertNotNull(getStorage().getRepositoryInfo().getRepositoryId());
      assertFalse(getStorage().getRepositoryInfo().getRepositoryId().isEmpty());
      assertNotNull(getStorage().getRepositoryInfo().getRepositoryName());
      assertNotNull(getStorage().getRepositoryInfo().getRepositoryDescription());
      assertNotNull(getStorage().getRepositoryInfo().getVendorName());
      assertNotNull(getStorage().getRepositoryInfo().getProductName());
      assertNotNull(getStorage().getRepositoryInfo().getProductVersion());
      assertNotNull(getStorage().getRepositoryInfo().getRootFolderId());
      assertNotNull(getStorage().getRepositoryInfo().getCapabilities());
      //      assertNotNull(getStorage().getRepositoryInfo().getLatestChangeLogToken());
      assertNotNull(getStorage().getRepositoryInfo().getCmisVersionSupported());
      //      assertNotNull(getStorage().getRepositoryInfo().getThinClientURI());
      assertNotNull(getStorage().getRepositoryInfo().isChangesIncomplete());
      assertNotNull(getStorage().getRepositoryInfo().getChangesOnType());
      assertNotNull(getStorage().getRepositoryInfo().getAclCapability());
      assertNotNull(getStorage().getRepositoryInfo().getAclCapability().getSupportedPermissions());
      assertNotNull(getStorage().getRepositoryInfo().getAclCapability().getPropagation());
      assertNotNull(getStorage().getRepositoryInfo().getAclCapability().getPermissions());
      assertNotNull(getStorage().getRepositoryInfo().getAclCapability().getMapping());
      assertNotNull(getStorage().getRepositoryInfo().getPrincipalAnonymous());
      assertNotNull(getStorage().getRepositoryInfo().getPrincipalAnyone());
   }

   public void testGetTypeChildren_Root()
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
      assertNotNull(typeChildrenList);
      List<String> ll = new ArrayList<String>();
      ll.add(CmisConstants.DOCUMENT);
      ll.add(CmisConstants.FOLDER);
      for (TypeDefinition typeDefinition : typeChildrenList)
      {
         assertNotNull(typeDefinition);
         assertNotNull(typeDefinition.getId());
         assertFalse(typeDefinition.getId().isEmpty());
         assertNotNull(typeDefinition.getBaseId());
         assertEquals(typeDefinition.getId(), typeDefinition.getBaseId().value());
         assertNotNull(typeDefinition.getDisplayName());
         assertNotNull(typeDefinition.getLocalName());
         assertNotNull(typeDefinition.getQueryName());
         checkPropertyDefinitions(typeDefinition.getPropertyDefinitions());
         
         if (ll.size() > 0)
            assertTrue(ll.contains(typeDefinition.getId()));
         ll.remove(typeDefinition.getId());
      }
      assertTrue("The prepared list of default types should be cleared. Some default type is missing:" + ll, ll
         .isEmpty());
   }

   public void testGetTypeChildren_RootWithMaxItems()
   {
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
   }

   public void testGetTypeChildren_RootWithSkipCount()
   {
      // get size of root types
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
      List<TypeDefinition> typeChildrenList = typeChildren0.getItems();
      int sizeOfRootTypes = typeChildrenList.size();

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
   }

   public void testGetTypeChildren_Folder()
   {
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
   }

   public void testGetTypeChildren_Document()
   {
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
   }

   public void testGetTypeChildren_NonExistedType()
   {
      // to get children for nonexistent type "cmis:kino"
      try
      {
         getConnection().getTypeChildren("cmis:kino", false, -1, 0);
         fail("The type definition \"cmis:kino\" shouldn't exist.'");
      }
      catch (TypeNotFoundException e)
      {
         // OK
      }
   }

   public void testGetTypeDescendants()
   {
      List<ItemsTree<TypeDefinition>> typeDescendants = null;
      try
      {
         typeDescendants = getConnection().getTypeDescendants(null, 2, true);
      }
      catch (TypeNotFoundException e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
      assertNotNull(typeDescendants);
      assertTrue(typeDescendants.size() > 0);
      for (ItemsTree<TypeDefinition> itemsTree : typeDescendants)
      {
         assertNotNull(itemsTree);
         assertNotNull(itemsTree.getChildren());
         assertTrue(itemsTree.getChildren().size() == 0);
         assertNotNull(itemsTree.getContainer());
         assertNotNull(itemsTree.getContainer().getId());
         assertFalse(itemsTree.getContainer().getId().isEmpty());
         assertNotNull(itemsTree.getContainer().getDisplayName());
         assertNotNull(itemsTree.getContainer().getLocalName());
         assertNotNull(itemsTree.getContainer().getQueryName());
         assertNotNull(itemsTree.getContainer().getBaseId());
         checkPropertyDefinitions( itemsTree.getContainer().getPropertyDefinitions());
      }
   }

   public void testGetTypeDescendants_Folder()
   {
      List<ItemsTree<TypeDefinition>> typeDescendants = null;
      try
      {
         typeDescendants = getConnection().getTypeDescendants("cmis:folder", 2, true);
      }
      catch (TypeNotFoundException e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
      assertNotNull(typeDescendants);
      assertTrue(typeDescendants.size() == 0);
   }

   public void testGetTypeDescendants_Document()
   {
      List<ItemsTree<TypeDefinition>> typeDescendants = null;
      try
      {
         typeDescendants = getConnection().getTypeDescendants("cmis:document", 2, true);
      }
      catch (TypeNotFoundException e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
      assertNotNull(typeDescendants);
      assertTrue(typeDescendants.size() == 0);
   }

   public void testGetTypeDescendants_IncludePropertyDefinitionFalse()
   {
      List<ItemsTree<TypeDefinition>> typeDescendants = null;
      try
      {
         typeDescendants = getConnection().getTypeDescendants(null, 2, false);
      }
      catch (TypeNotFoundException e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
      assertNotNull(typeDescendants);
      assertTrue(typeDescendants.size() > 0);
      for (ItemsTree<TypeDefinition> itemsTree : typeDescendants)
      {
         assertNotNull(itemsTree);
         assertNotNull(itemsTree.getContainer());
         assertNull(itemsTree.getContainer().getPropertyDefinitions());
      }
   }

   public void testGetTypeDescendants_RootWithDepth1()
   {
      List<ItemsTree<TypeDefinition>> typeDescendants = null;
      try
      {
         typeDescendants = getConnection().getTypeDescendants(null, 1, true);
      }
      catch (TypeNotFoundException e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
      assertNotNull(typeDescendants);
      assertTrue(typeDescendants.size() > 0);
      for (ItemsTree<TypeDefinition> itemsTree : typeDescendants)
      {
         assertNotNull(itemsTree);
         assertNull(itemsTree.getChildren());
      }
   }

   public void testGetTypeDescendants_NonExistedType()
   {
      try
      {
         getConnection().getTypeDescendants("cmis:kino", 2, true);
         fail("The type definition \"cmis:kino\" shouldn't exist.'");
      }
      catch (TypeNotFoundException e)
      {
         // OK
      }
   }

   public void testGetTypeDefinition_Folder()
   {
      TypeDefinition typeDefinition = null;
      try
      {
         typeDefinition = getConnection().getTypeDefinition("cmis:folder", true);
      }
      catch (TypeNotFoundException e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
      assertNotNull(typeDefinition);
      assertNotNull(typeDefinition.getId());
      assertFalse(typeDefinition.getId().isEmpty());
      assertNotNull(typeDefinition.getLocalName());
      assertNotNull(typeDefinition.getQueryName());
      checkPropertyDefinitions(typeDefinition.getPropertyDefinitions());
   }

   public void testGetTypeDefinition_Document()
   {
      TypeDefinition typeDefinition = null;
      try
      {
         typeDefinition = getConnection().getTypeDefinition("cmis:document", true);
      }
      catch (TypeNotFoundException e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
      assertNotNull(typeDefinition);
      assertNotNull(typeDefinition.getId());
      assertFalse(typeDefinition.getId().isEmpty());
      assertNotNull(typeDefinition.getLocalName());
      assertNotNull(typeDefinition.getQueryName());
      checkPropertyDefinitions(typeDefinition.getPropertyDefinitions());
   }

   private void checkPropertyDefinitions(Collection<PropertyDefinition<?>> propertyDefinitions)
   {
      assertNotNull(propertyDefinitions);
      assertTrue(propertyDefinitions.size() > 0);
      for (PropertyDefinition<?> propertyDefinition : propertyDefinitions)
      {
         assertNotNull(propertyDefinition);
         assertNotNull(propertyDefinition.getId());
         assertFalse(propertyDefinition.getId().isEmpty());
         assertNotNull(propertyDefinition.getLocalName());
         assertNotNull(propertyDefinition.getQueryName());
         assertNotNull(propertyDefinition.getPropertyType());
      }
   }

   public void testGetTypeDefinition_NonExistedType()
   {
      try
      {
         getConnection().getTypeDefinition("cmis:kino", false);
         fail("The type definition \"cmis:kino\" shouldn't exist.'");
      }
      catch (TypeNotFoundException e)
      {
         // OK
      }
   }

   public void testGetTypeDefinition_IncludePropertyDefinitionFalse()
   {
      TypeDefinition typeDefinition = null;
      try
      {
         typeDefinition = getConnection().getTypeDefinition("cmis:folder", false);
      }
      catch (TypeNotFoundException e)
      {
         e.printStackTrace();
         fail(e.getMessage());
      }
      assertNotNull(typeDefinition);
      assertNotNull(typeDefinition.getId());
      assertFalse(typeDefinition.getId().isEmpty());
      assertNotNull(typeDefinition.getLocalName());
      assertNotNull(typeDefinition.getQueryName());
      Collection<PropertyDefinition<?>> propertyDefinitions = typeDefinition.getPropertyDefinitions();
      assertNull(propertyDefinitions);
   }

}
