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
      if (storageProvider == null)
         doFail("Storage provider is null;");
      if (storageProvider.getConnection() == null)
         doFail("Connection is null;");
      if (storageProvider.getConnection().getStorage() == null)
         doFail("Storage  is null;");
      if (storageProvider.getConnection().getStorage().getId() == null)
         doFail("Storage  ID is null;");
      pass();
   }

   /**
    * 2.2.2.1 getRepositories
    * 
    * Returns a list of CMIS repositories available from this CMIS service endpoint.
    */
   public void testGetRepositories() throws Exception
   {
      System.out.print("Running testGetRepositories....                                            ");
      Set<RepositoryShortInfo> storageInfos = CmisRegistry.getInstance().getStorageInfos();
      if (storageInfos == null)
         doFail("StorageInfo  is null;");
      if (storageInfos.isEmpty())
         doFail("StorageInfo  is empty;");
      for (RepositoryShortInfo repositoryShortInfo : storageInfos)
      {
         if (repositoryShortInfo.getRepositoryId() == null)
            doFail("Repository Short Info  is null;");
         if (repositoryShortInfo.getRepositoryId().isEmpty())
            doFail("Repository Short Info  is empty;");
         if (repositoryShortInfo.getRepositoryName() == null)
            doFail("Repository name  is null;");
         if (repositoryShortInfo.getRepositoryName().isEmpty())
            doFail("Repository name  is empty;");
         if (repositoryShortInfo.getRootFolderId() == null)
            doFail("Root folder ID  is null");
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
      if (getStorage().getRepositoryInfo() == null)
         doFail("Repository Info  is null;");
      if (getStorage().getRepositoryInfo().getRepositoryId() == null)
         doFail("Repository Info ID  is null;");
      if (getStorage().getRepositoryInfo().getRepositoryId().isEmpty())
         doFail("Repository Info ID  is empty;");
      if (getStorage().getRepositoryInfo().getRepositoryName() == null)
         doFail("Repository Info Name  is null;");
      if (getStorage().getRepositoryInfo().getRepositoryDescription() == null)
         doFail("Repository Description  is null;");
      if (getStorage().getRepositoryInfo().getVendorName() == null)
         doFail("Repository VendorName  is null;");
      if (getStorage().getRepositoryInfo().getProductName() == null)
         doFail("Repository ProductName  is null;");
      if (getStorage().getRepositoryInfo().getProductVersion() == null)
         doFail("Repository PropductVersion  is null;");
      if (getStorage().getRepositoryInfo().getRootFolderId() == null)
         doFail("Repository Root folder ID  is null;");
      if (getStorage().getRepositoryInfo().getCapabilities() == null)
         doFail("Repository Capabilities  is null;");
      //      assertNotNull(getStorage().getRepositoryInfo().getLatestChangeLogToken());
      if (getStorage().getRepositoryInfo().getCmisVersionSupported() == null)
         doFail("Repository version supported  is null;");
      //      assertNotNull(getStorage().getRepositoryInfo().getThinClientURI());
      //      if(getStorage().getRepositoryInfo().isChangesIncomplete().)
      //         doFail("Repository Description  is null;");
      if (getStorage().getRepositoryInfo().getChangesOnType() == null)
         doFail("Repository Changes on type  is null;");
      if (getStorage().getRepositoryInfo().getAclCapability() == null)
         doFail("Repository ACL capability  is null;");
      if (getStorage().getRepositoryInfo().getAclCapability().getSupportedPermissions() == null)
         doFail("Repository supported permissions  is null;");
      if (getStorage().getRepositoryInfo().getAclCapability().getPropagation() == null)
         doFail("Repository ACL propagation  is null;");
      if (getStorage().getRepositoryInfo().getAclCapability().getPermissions() == null)
         doFail("Repository ACL permissions  is null;");
      if (getStorage().getRepositoryInfo().getAclCapability().getMapping() == null)
         doFail("Repository ACL mapping  is null;");
      if (getStorage().getRepositoryInfo().getPrincipalAnonymous() == null)
         doFail("Repository principal anonymous  is null;");
      if (getStorage().getRepositoryInfo().getPrincipalAnyone() == null)
         doFail("Repository principal anyone  is null;");
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
      if (typeChildren0 == null)
         doFail("Root typer childrens is null;");
      List<TypeDefinition> typeChildrenList = typeChildren0.getItems();
      if (typeChildrenList == null)
         ;
      doFail("Root typer childrens is empty;");
      List<String> ll = new ArrayList<String>();
      ll.add(CmisConstants.DOCUMENT);
      ll.add(CmisConstants.FOLDER);
      for (TypeDefinition typeDefinition : typeChildrenList)
      {
         if (typeDefinition == null)
            doFail("TypeDefinition is null;");
         if (typeDefinition.getId() == null)
            doFail("TypeDefinition  ID is null;");
         if (typeDefinition.getId().isEmpty())
            doFail("TypeDefinition  ID is empty;");
         if (typeDefinition.getBaseId() == null)
            doFail("TypeDefinition  BaseId is empty;");
         if (!typeDefinition.getId().equals(typeDefinition.getBaseId().value()))
            doFail("TypeDefinition  BaseId  does not match;");
         if (typeDefinition.getDisplayName() == null)
            doFail("TypeDefinition  display name is null;");
         if (typeDefinition.getLocalName() == null)
            doFail("TypeDefinition  local name is null;");
         if (typeDefinition.getQueryName() == null)
            doFail("TypeDefinition query name is null;");
         checkPropertyDefinitions(typeDefinition.getPropertyDefinitions());

         if (ll.size() > 0)
            if (!ll.contains(typeDefinition.getId()))
         doFail("Mandatory type definition not found;");
         ll.remove(typeDefinition.getId());
      }
      if (!ll.isEmpty())
         doFail("Not all mandatory types found;");
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
      if (typeChildren3 == null)
         doFail("Root type childrens is null;");
      if (typeChildren3.getItems().size() != 1)
         doFail("Incorrect Root type childrens size;");
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
      if (typeChildren4 == null)
         doFail("Root type childrens is null;");
      if (typeChildren4.getItems().size() != 1)
         doFail("Incorrect Root type childrens size;");
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
      if (typeChildren1 == null)
         doFail("Root type childrens is null;");
      if (!typeChildren1.isHasMoreItems())
         doFail("Has more items not set in result;");
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
      if (typeChildren2 == null)
         doFail("Root type childrens is null;");
      if (!typeChildren2.isHasMoreItems())
         doFail("Has more items not set in result;");
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
      if (typeDescendants == null)
         doFail("Type Descendants is null;");
      if (!(typeDescendants.size() > 0))
         doFail("Type Descendants is empty;");
      for (ItemsTree<TypeDefinition> itemsTree : typeDescendants)
      {
         if (itemsTree == null)
            doFail("Items tree is null;");
         if (itemsTree.getChildren() == null)
            doFail("Items tree children is null;");
         if (itemsTree.getChildren().size() != 0)
            doFail("Items tree children is not empty;");
         if (itemsTree.getContainer() == null)
            doFail("Items tree container is null;");
         if (itemsTree.getContainer().getId() == null)
            doFail("Items tree container ID is null;");
         if (itemsTree.getContainer().getId().isEmpty())
            doFail("Items tree container ID is empty;");
         if (itemsTree.getContainer().getDisplayName() == null)
            doFail("Items tree container DisplayName is empty;");
         if (itemsTree.getContainer().getLocalName() == null)
            doFail("Items tree container LocalName is empty;");
         if (itemsTree.getContainer().getQueryName() == null)
            doFail("Items tree container QueryName is empty;");
         if (itemsTree.getContainer().getBaseId() == null)
            doFail("Items tree container BaseId is empty;");
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
      if (typeDescendants == null)
         doFail("Type Descendants is null;");
      if (typeDescendants.size() != 0)
         doFail("Type Descendants is not empty;");
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
      if (typeDescendants == null)
         doFail("Type Descendants is null;");
      if (typeDescendants.size() != 0)
         doFail("Type Descendants is not empty;");
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
      if (typeDescendants == null)
         doFail("Type Descendants is null;");
      if (!(typeDescendants.size() > 0))
         doFail("Type Descendants is empty;");
      for (ItemsTree<TypeDefinition> itemsTree : typeDescendants)
      {
         if (itemsTree == null)
            doFail("Items tree is null;");
         if (itemsTree.getContainer() == null)
            doFail("Items tree container is null;");
         if (itemsTree.getContainer().getPropertyDefinitions() != null)
            doFail("Property definitions must be empty;");
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
      if (typeDescendants == null)
         doFail("Type Descendants is null;");
      if (!(typeDescendants.size() > 0))
         doFail("Type Descendants is empty;");
      for (ItemsTree<TypeDefinition> itemsTree : typeDescendants)
      {
         if (itemsTree == null)
            doFail("Items tree is null;");
         if (itemsTree.getChildren() != null)
            doFail("Childrens must be empty;");
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
      if (folderTypeDefinition == null)
         doFail("Folder type definition is null;");
      if (folderTypeDefinition.getId() == null)
         doFail("Folder type definition ID is null;");
      if (folderTypeDefinition.getId().isEmpty())
         doFail("Folder type definition ID is empty;");
      if (folderTypeDefinition.getLocalName() == null)
         doFail("Folder type definition local name is empty;");
      if (folderTypeDefinition.getQueryName() == null)
         doFail("Folder type definition query name is empty;");
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
      if (documentTypeDefinition == null)
         doFail("Document type definition is null;");
      if (documentTypeDefinition.getId() == null)
         doFail("Document type definition ID is null;");
      if (documentTypeDefinition.getId().isEmpty())
         doFail("Document type definition ID is empty;");
      if (documentTypeDefinition.getLocalName() == null)
         doFail("Document type definition local name is empty;");
      if (documentTypeDefinition.getQueryName() == null)
         doFail("Document type definition query name is empty;");
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
      if (typeDefinition == null)
         doFail("Type definition is null;");
      if (typeDefinition.getId() == null)
         doFail("Type definition ID is null;");
      if (typeDefinition.getId().isEmpty())
         doFail("Type definition ID is empty;");
      if (typeDefinition.getLocalName() == null)
         doFail("Type definition local name is empty;");
      if (typeDefinition.getQueryName() == null)
         doFail("Type definition query name is empty;");
      Collection<PropertyDefinition<?>> propertyDefinitions = typeDefinition.getPropertyDefinitions();
      if (propertyDefinitions != null)
         doFail("Property definitions must not be included;");
      pass();
   }

   private void checkPropertyDefinitions(Collection<PropertyDefinition<?>> propertyDefinitions) throws Exception
   {
      if (propertyDefinitions == null)
         doFail("propertyDefinitions is null;");
      if (!(propertyDefinitions.size() > 0))
         doFail("propertyDefinitions is not empty;");
      for (PropertyDefinition<?> propertyDefinition : propertyDefinitions)
      {
         if (propertyDefinition == null)
            doFail("Type definition is null;");
         if (propertyDefinition.getId() == null)
            doFail("Type definition ID is null;");
         if (propertyDefinition.getId().isEmpty())
            doFail("Type definition ID is empty;");
         if (propertyDefinition.getLocalName() == null)
            doFail("Type definition local name is empty;");
         if (propertyDefinition.getQueryName() == null)
            doFail("Type definition query name is empty;");
         if (propertyDefinition.getPropertyType() == null)
            doFail("Type definition property type is empty;");
      }
   }
}
