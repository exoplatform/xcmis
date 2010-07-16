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
 * 2.2.2 Repository Services
 * The Repository Services (getRepositories, getRepositoryInfo, getTypeChildren, getTypeDescendants,
 * getTypeDefinition) are used to discover information about the repository, including information about the
 * repository and the object-types defined for the repository.
 * 
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
      System.out.print("Running testGeneral....                                                    ");
      assertNotNull(storageProvider);
      assertNotNull(storageProvider.getConnection());
      assertNotNull(storageProvider.getConnection().getStorage());
      assertNotNull(storageProvider.getConnection().getStorage().getId());
      pass();
   }

   /**
    * 2.2.2.1 getRepositories
    * 
    * Returns a list of CMIS repositories available from this CMIS service endpoint.
    */
   public void testGetRepositories()
   {
      System.out.print("Running testGetRepositories....                                            ");
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
      pass();
   }

   /**
    * 2.2.2.2 getRepositoryInfo
    * 
    * Returns information about the CMIS repository, the optional capabilities it supports and its Access Control information if applicable.
    * 
    * @throws Exception
    */
   public void testGetRepositoryInfo() throws Exception
   {
      System.out.print("Running testGetRepositoryInfo....                                          ");
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
      pass();
   }

   /**
    * 2.2.2.3 getTypeChildren
    * 
    * Returns the list of Object-Types defined for the Repository 
    * that are children of the specified Type.
    */
   public void testGetTypeChildren_Root() throws Exception
   {
      System.out.print("Running testGetTypeChildren_Root....                                       ");
      // root types
      ItemsList<TypeDefinition> typeChildren0 = null;
      try
      {
         typeChildren0 = getConnection().getTypeChildren(null, true, -1, 0);
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
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
      pass();
   }

   /**
    * 2.2.2.3 getTypeChildren With MaxItems
    * 
    * Returns the list of Object-Types defined for the Repository 
    * that are children of the specified Type.
    */
   public void testGetTypeChildren_RootWithMaxItems() throws Exception
   {
      System.out.print("Running testGetTypeChildren_RootWithMaxItems....                           ");
      // root types with maxItems
      ItemsList<TypeDefinition> typeChildren3 = null;
      try
      {
         typeChildren3 = getConnection().getTypeChildren(null, true, 1, 0);
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
      }
      assertNotNull(typeChildren3);
      assertEquals(1, typeChildren3.getItems().size());
      pass();
   }

   /**
    * 2.2.2.3 getTypeChildren With SkipCount
    * 
    * Returns the list of Object-Types defined for the Repository 
    * that are children of the specified Type.
    */
   public void testGetTypeChildren_RootWithSkipCount() throws Exception
   {
      System.out.print("Running testGetTypeChildren_RootWithSkipCount....                          ");
      // get size of root types
      ItemsList<TypeDefinition> typeChildren0 = null;
      try
      {
         typeChildren0 = getConnection().getTypeChildren(null, true, -1, 0);
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
      }
      List<TypeDefinition> typeChildrenList = typeChildren0.getItems();
      int sizeOfRootTypes = typeChildrenList.size();

      // root types with skipCount
      ItemsList<TypeDefinition> typeChildren4 = null;
      try
      {
         typeChildren4 = getConnection().getTypeChildren(null, true, -1, sizeOfRootTypes - 1);
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
      }
      assertNotNull(typeChildren4);
      assertEquals(1, typeChildren4.getItems().size());
      pass();
   }

   /**
    * 2.2.2.3 getTypeChildren for Folder type
    * 
    * Returns the list of Object-Types defined for the Repository 
    * that are children of the specified Type.
    */
   public void testGetTypeChildren_Folder() throws Exception
   {
      System.out.print("Running testGetTypeChildren_Folder....                                     ");
      // folder
      ItemsList<TypeDefinition> typeChildren1 = null;
      try
      {
         typeChildren1 = getConnection().getTypeChildren("cmis:folder", true, -1, 0);
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
      }
      assertNotNull(typeChildren1);
      assertFalse(typeChildren1.isHasMoreItems());
      pass();
   }

   /**
    * 2.2.2.3 getTypeChildren for Document type
    * 
    * Returns the list of Object-Types defined for the Repository 
    * that are children of the specified Type.
    */
   public void testGetTypeChildren_Document() throws Exception
   {
      System.out.print("Running testGetTypeChildren_Document....                                   ");
      // document
      ItemsList<TypeDefinition> typeChildren2 = null;
      try
      {
         typeChildren2 = getConnection().getTypeChildren("cmis:document", true, -1, 0);
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
      }
      assertNotNull(typeChildren2);
      assertFalse(typeChildren2.isHasMoreItems());
      pass();
   }

   /**
    * 2.2.2.3 getTypeChildren for Non existed type
    * 
    * Returns the list of Object-Types defined for the Repository 
    * that are children of the specified Type.
    */
   public void testGetTypeChildren_NonExistedType() throws Exception
   {
      // to get children for nonexistent type "cmis:kino"
      System.out.print("Running testGetTypeChildren_NonExistedType....                             ");
      try
      {
         getConnection().getTypeChildren("cmis:kino", false, -1, 0);
         doFail("The type definition \"cmis:kino\" shouldn't exist.'");
      }
      catch (TypeNotFoundException e)
      {
         pass();
      }
      catch (Exception ex)
      {
         doFail(ex.getMessage());
      }
   }

   /**
    * 2.2.2.4 getTypeDescendants for root
    * 
    * Returns the set of descendant Object-Types defined for the Repository under the specified Type.
    */
   public void testGetTypeDescendants() throws Exception
   {
      System.out.print("Running testGetTypeDescendants....                                         ");
      List<ItemsTree<TypeDefinition>> typeDescendants = null;
      try
      {
         typeDescendants = getConnection().getTypeDescendants(null, 2, true);
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
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
         checkPropertyDefinitions(itemsTree.getContainer().getPropertyDefinitions());
      }
      pass();
   }

   /**
    * 2.2.2.4 getTypeDescendants for Folder type
    * 
    * Returns the set of descendant Object-Types defined for the Repository under the specified Type.
    */
   public void testGetTypeDescendants_Folder() throws Exception
   {
      System.out.print("Running testGetTypeDescendants_Folder....                                  ");
      List<ItemsTree<TypeDefinition>> typeDescendants = null;
      try
      {
         typeDescendants = getConnection().getTypeDescendants("cmis:folder", 2, true);
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
      }
      assertNotNull(typeDescendants);
      assertTrue(typeDescendants.size() == 0);
      pass();
   }

   /**
    * 2.2.2.4 getTypeDescendants for Document type
    * 
    * Returns the set of descendant Object-Types defined for the Repository under the specified Type.
    */
   public void testGetTypeDescendants_Document() throws Exception
   {
      System.out.print("Running testGetTypeDescendants_Document....                                ");
      List<ItemsTree<TypeDefinition>> typeDescendants = null;
      try
      {
         typeDescendants = getConnection().getTypeDescendants("cmis:document", 2, true);
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
      }
      assertNotNull(typeDescendants);
      assertTrue(typeDescendants.size() == 0);
      pass();
   }

   /**
    * 2.2.2.4 getTypeDescendants for root with IncludePropertyDefinition is false.
    * 
    * Returns the set of descendant Object-Types defined for the Repository under the specified Type.
    */
   public void testGetTypeDescendants_IncludePropertyDefinitionFalse() throws Exception
   {
      System.out.print("Running testGetTypeDescendants_IncludePropertyDefinitionFalse....          ");
      List<ItemsTree<TypeDefinition>> typeDescendants = null;
      try
      {
         typeDescendants = getConnection().getTypeDescendants(null, 2, false);
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
      }
      assertNotNull(typeDescendants);
      assertTrue(typeDescendants.size() > 0);
      for (ItemsTree<TypeDefinition> itemsTree : typeDescendants)
      {
         assertNotNull(itemsTree);
         assertNotNull(itemsTree.getContainer());
         assertNull(itemsTree.getContainer().getPropertyDefinitions());
      }
      pass();
   }

   /**
    * 2.2.2.4 getTypeDescendants for root with depth 1.
    * 
    * Returns the set of descendant Object-Types defined for the Repository under the specified Type.
    */
   public void testGetTypeDescendants_RootWithDepth1() throws Exception
   {
      System.out.print("Running testGetTypeDescendants_RootWithDepth1....                          ");
      List<ItemsTree<TypeDefinition>> typeDescendants = null;
      try
      {
         typeDescendants = getConnection().getTypeDescendants(null, 1, true);
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
      }
      assertNotNull(typeDescendants);
      assertTrue(typeDescendants.size() > 0);
      for (ItemsTree<TypeDefinition> itemsTree : typeDescendants)
      {
         assertNotNull(itemsTree);
         assertNull(itemsTree.getChildren());
      }
      pass();
   }

   /**
    * 2.2.2.4 getTypeDescendants for non existed type.
    * 
    * Returns the set of descendant Object-Types defined for the Repository under the specified Type.
    */
   public void testGetTypeDescendants_NonExistedType() throws Exception
   {
      System.out.print("Running testGetTypeDescendants_NonExistedType....                          ");
      try
      {
         getConnection().getTypeDescendants("cmis:kino", 2, true);
         doFail("The type definition \"cmis:kino\" shouldn't exist.'");
      }
      catch (TypeNotFoundException e)
      {
         pass();
      }
      catch (Exception other)
      {
         doFail(other.getMessage());
      }
   }

   /**
    * 2.2.2.5 getTypeDefinition for Folder type
    * 
    * Gets the definition of the specified Object-Type.
    */
   public void testGetTypeDefinition_Folder() throws Exception
   {
      System.out.print("Running testGetTypeDefinition_Folder....                                   ");
      assertNotNull(folderTypeDefinition);
      assertNotNull(folderTypeDefinition.getId());
      assertFalse(folderTypeDefinition.getId().isEmpty());
      assertNotNull(folderTypeDefinition.getLocalName());
      assertNotNull(folderTypeDefinition.getQueryName());
      checkPropertyDefinitions(folderTypeDefinition.getPropertyDefinitions());
      pass();
   }

   /**
    * 2.2.2.5 getTypeDefinition for Document type
    * 
    * Gets the definition of the specified Object-Type.
    */
   public void testGetTypeDefinition_Document() throws Exception
   {
      System.out.print("Running testGetTypeDefinition_Document....                                 ");
      assertNotNull(documentTypeDefinition);
      assertNotNull(documentTypeDefinition.getId());
      assertFalse(documentTypeDefinition.getId().isEmpty());
      assertNotNull(documentTypeDefinition.getLocalName());
      assertNotNull(documentTypeDefinition.getQueryName());
      checkPropertyDefinitions(documentTypeDefinition.getPropertyDefinitions());
      pass();
   }

   /**
    * 2.2.2.5 getTypeDefinition for non existed type
    * 
    * Gets the definition of the specified Object-Type.
    */
   public void testGetTypeDefinition_NonExistedType() throws Exception
   {
      System.out.print("Running testGetTypeDefinition_NonExistedType....                           ");
      try
      {
         getConnection().getTypeDefinition("cmis:kino", false);
         doFail("The type definition \"cmis:kino\" shouldn't exist.'");
      }
      catch (TypeNotFoundException e)
      {
         pass();
      }
      catch (Exception other)
      {
         doFail(other.getMessage());
      }
   }

   /**
    * 2.2.2.5 getTypeDefinition for Folder type with IncludePropertyDefinition is false. 
    * 
    * Gets the definition of the specified Object-Type.
    */
   public void testGetTypeDefinition_IncludePropertyDefinitionFalse() throws Exception
   {
      System.out.print("Running testGetTypeDefinition_IncludePropertyDefinitionFalse....           ");
      TypeDefinition typeDefinition = null;
      try
      {
         typeDefinition = getConnection().getTypeDefinition("cmis:folder", false);
      }
      catch (Exception e)
      {
         doFail(e.getMessage());
      }
      assertNotNull(typeDefinition);
      assertNotNull(typeDefinition.getId());
      assertFalse(typeDefinition.getId().isEmpty());
      assertNotNull(typeDefinition.getLocalName());
      assertNotNull(typeDefinition.getQueryName());
      Collection<PropertyDefinition<?>> propertyDefinitions = typeDefinition.getPropertyDefinitions();
      assertNull(propertyDefinitions);
      pass();
   }

   private void checkPropertyDefinitions(Collection<PropertyDefinition<?>> propertyDefinitions) throws Exception
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
}
