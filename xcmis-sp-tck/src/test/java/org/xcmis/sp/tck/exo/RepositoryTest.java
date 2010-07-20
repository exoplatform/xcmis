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
      String testname = "testGeneral";
      System.out.print("Running " + testname + "....                                                    ");
      if (storageProvider == null)
         doFail(testname, "Storage provider is null;");
      if (storageProvider.getConnection() == null)
         doFail(testname, "Connection is null;");
      if (storageProvider.getConnection().getStorage() == null)
         doFail(testname, "Storage  is null;");
      if (storageProvider.getConnection().getStorage().getId() == null)
         doFail(testname, "Storage  ID is null;");
      pass(testname);
   }

   /**
    * 2.2.2.1 getRepositories
    * 
    * Returns a list of CMIS repositories available from this CMIS service endpoint.
    */
   public void testGetRepositories() throws Exception
   {
      String testname = "testGetRepositories";
      System.out.print("Running " + testname + "....                                            ");
      Set<RepositoryShortInfo> storageInfos = CmisRegistry.getInstance().getStorageInfos();
      if (storageInfos == null)
         doFail(testname, "StorageInfo  is null;");
      if (storageInfos.isEmpty())
         doFail(testname, "StorageInfo  is empty;");
      for (RepositoryShortInfo repositoryShortInfo : storageInfos)
      {
         if (repositoryShortInfo.getRepositoryId() == null)
            doFail(testname, "Repository Short Info  is null;");
         if (repositoryShortInfo.getRepositoryId().isEmpty())
            doFail(testname, "Repository Short Info  is empty;");
         if (repositoryShortInfo.getRepositoryName() == null)
            doFail(testname, "Repository name  is null;");
         if (repositoryShortInfo.getRepositoryName().isEmpty())
            doFail(testname, "Repository name  is empty;");
         if (repositoryShortInfo.getRootFolderId() == null)
            doFail(testname, "Root folder ID  is null");
      }
      pass(testname);
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
      String testname = "testGetRepositoryInfo";
      System.out.print("Running " + testname + "....                                          ");
      if (getStorage().getRepositoryInfo() == null)
         doFail(testname, "Repository Info  is null;");
      if (getStorage().getRepositoryInfo().getRepositoryId() == null)
         doFail(testname, "Repository Info ID  is null;");
      if (getStorage().getRepositoryInfo().getRepositoryId().isEmpty())
         doFail(testname, "Repository Info ID  is empty;");
      if (getStorage().getRepositoryInfo().getRepositoryName() == null)
         doFail(testname, "Repository Info Name  is null;");
      if (getStorage().getRepositoryInfo().getRepositoryDescription() == null)
         doFail(testname, "Repository Description  is null;");
      if (getStorage().getRepositoryInfo().getVendorName() == null)
         doFail(testname, "Repository VendorName  is null;");
      if (getStorage().getRepositoryInfo().getProductName() == null)
         doFail(testname, "Repository ProductName  is null;");
      if (getStorage().getRepositoryInfo().getProductVersion() == null)
         doFail(testname, "Repository PropductVersion  is null;");
      if (getStorage().getRepositoryInfo().getRootFolderId() == null)
         doFail(testname, "Repository Root folder ID  is null;");
      if (getStorage().getRepositoryInfo().getCapabilities() == null)
         doFail(testname, "Repository Capabilities  is null;");
      //      assertNotNull(getStorage().getRepositoryInfo().getLatestChangeLogToken());
      if (getStorage().getRepositoryInfo().getCmisVersionSupported() == null)
         doFail(testname, "Repository version supported  is null;");
      //      assertNotNull(getStorage().getRepositoryInfo().getThinClientURI());
      //      if(getStorage().getRepositoryInfo().isChangesIncomplete().)
      //         doFail(testname, "Repository Description  is null;");
      if (getStorage().getRepositoryInfo().getChangesOnType() == null)
         doFail(testname, "Repository Changes on type  is null;");
      if (getStorage().getRepositoryInfo().getAclCapability() == null)
         doFail(testname, "Repository ACL capability  is null;");
      if (getStorage().getRepositoryInfo().getAclCapability().getSupportedPermissions() == null)
         doFail(testname, "Repository supported permissions  is null;");
      if (getStorage().getRepositoryInfo().getAclCapability().getPropagation() == null)
         doFail(testname, "Repository ACL propagation  is null;");
      if (getStorage().getRepositoryInfo().getAclCapability().getPermissions() == null)
         doFail(testname, "Repository ACL permissions  is null;");
      if (getStorage().getRepositoryInfo().getAclCapability().getMapping() == null)
         doFail(testname, "Repository ACL mapping  is null;");
      if (getStorage().getRepositoryInfo().getPrincipalAnonymous() == null)
         doFail(testname, "Repository principal anonymous  is null;");
      if (getStorage().getRepositoryInfo().getPrincipalAnyone() == null)
         doFail(testname, "Repository principal anyone  is null;");
      pass(testname);
   }

   /**
    * 2.2.2.3 getTypeChildren
    * 
    * Returns the list of Object-Types defined for the Repository 
    * that are children of the specified Type.
    */
   public void testGetTypeChildren_Root() throws Exception
   {
      String testname = "testGetTypeChildren_Root";
      System.out.print("Running " + testname + "....                                       ");
      // root types
      ItemsList<TypeDefinition> typeChildren0 = null;
      try
      {
         typeChildren0 = getConnection().getTypeChildren(null, true, -1, 0);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
      if (typeChildren0 == null)
         doFail(testname, "Root typer childrens is null;");
      List<TypeDefinition> typeChildrenList = typeChildren0.getItems();
      if (typeChildrenList == null)
         doFail(testname, "Root typer childrens is empty;");
      List<String> ll = new ArrayList<String>();
      ll.add(CmisConstants.DOCUMENT);
      ll.add(CmisConstants.FOLDER);
      for (TypeDefinition typeDefinition : typeChildrenList)
      {
         if (typeDefinition == null)
            doFail(testname, "TypeDefinition is null;");
         if (typeDefinition.getId() == null)
            doFail(testname, "TypeDefinition  ID is null;");
         if (typeDefinition.getId().isEmpty())
            doFail(testname, "TypeDefinition  ID is empty;");
         if (typeDefinition.getBaseId() == null)
            doFail(testname, "TypeDefinition  BaseId is empty;");
         if (!typeDefinition.getId().equals(typeDefinition.getBaseId().value()))
            doFail(testname, "TypeDefinition  BaseId  does not match;");
         if (typeDefinition.getDisplayName() == null)
            doFail(testname, "TypeDefinition  display name is null;");
         if (typeDefinition.getLocalName() == null)
            doFail(testname, "TypeDefinition  local name is null;");
         if (typeDefinition.getQueryName() == null)
            doFail(testname, "TypeDefinition query name is null;");
         checkPropertyDefinitions(typeDefinition.getPropertyDefinitions());

         if (ll.size() > 0)
            if (!ll.contains(typeDefinition.getId()))
               doFail(testname, "Mandatory type definition not found;");
         ll.remove(typeDefinition.getId());
      }
      if (!ll.isEmpty())
         doFail(testname, "Not all mandatory types found;");
      pass(testname);
   }

   /**
    * 2.2.2.3 getTypeChildren With MaxItems
    * 
    * Returns the list of Object-Types defined for the Repository 
    * that are children of the specified Type.
    */
   public void testGetTypeChildren_RootWithMaxItems() throws Exception
   {
      String testname = "testGetTypeChildren_RootWithMaxItems";
      System.out.print("Running " + testname + "....                           ");
      // root types with maxItems
      ItemsList<TypeDefinition> typeChildren3 = null;
      try
      {
         typeChildren3 = getConnection().getTypeChildren(null, true, 1, 0);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
      if (typeChildren3 == null)
         doFail(testname, "Root type childrens is null;");
      if (typeChildren3.getItems().size() != 1)
         doFail(testname, "Incorrect Root type childrens size;");
      pass(testname);
   }

   /**
    * 2.2.2.3 getTypeChildren With SkipCount
    * 
    * Returns the list of Object-Types defined for the Repository 
    * that are children of the specified Type.
    */
   public void testGetTypeChildren_RootWithSkipCount() throws Exception
   {
      String testname = "testGetTypeChildren_RootWithSkipCount";
      System.out.print("Running " + testname + "....                          ");
      // get size of root types
      ItemsList<TypeDefinition> typeChildren0 = null;
      try
      {
         typeChildren0 = getConnection().getTypeChildren(null, true, -1, 0);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
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
         doFail(testname, e.getMessage());
      }
      if (typeChildren4 == null)
         doFail(testname, "Root type childrens is null;");
      if (typeChildren4.getItems().size() != 1)
         doFail(testname, "Incorrect Root type childrens size;");
      pass(testname);
   }

   /**
    * 2.2.2.3 getTypeChildren for Folder type
    * 
    * Returns the list of Object-Types defined for the Repository 
    * that are children of the specified Type.
    */
   public void testGetTypeChildren_Folder() throws Exception
   {
      String testname = "testGetTypeChildren_Folder";
      System.out.print("Running " + testname + "....                                     ");
      // folder
      ItemsList<TypeDefinition> typeChildren1 = null;
      try
      {
         typeChildren1 = getConnection().getTypeChildren("cmis:folder", true, -1, 0);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
      if (typeChildren1 == null)
         doFail(testname, "Root type childrens is null;");
      if (!typeChildren1.isHasMoreItems())
         doFail(testname, "Has more items not set in result;");
      pass(testname);
   }

   /**
    * 2.2.2.3 getTypeChildren for Document type
    * 
    * Returns the list of Object-Types defined for the Repository 
    * that are children of the specified Type.
    */
   public void testGetTypeChildren_Document() throws Exception
   {
      String testname = "testGetTypeChildren_Document";
      System.out.print("Running " + testname + "....                                   ");
      // document
      ItemsList<TypeDefinition> typeChildren2 = null;
      try
      {
         typeChildren2 = getConnection().getTypeChildren("cmis:document", true, -1, 0);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
      if (typeChildren2 == null)
         doFail(testname, "Root type childrens is null;");
      if (!typeChildren2.isHasMoreItems())
         doFail(testname, "Has more items not set in result;");
      pass(testname);
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
      String testname = "testGetTypeChildren_NonExistedType";
      System.out.print("Running " + testname + "....                             ");
      try
      {
         getConnection().getTypeChildren("cmis:kino", false, -1, 0);
         doFail(testname, "The type definition \"cmis:kino\" shouldn't exist.'");
      }
      catch (TypeNotFoundException e)
      {
         pass(testname);
      }
      catch (Exception ex)
      {
         doFail(testname, ex.getMessage());
      }
   }

   /**
    * 2.2.2.4 getTypeDescendants for root
    * 
    * Returns the set of descendant Object-Types defined for the Repository under the specified Type.
    */
   public void testGetTypeDescendants() throws Exception
   {
      String testname = "testGetTypeDescendants";
      System.out.print("Running " + testname + "....                                         ");
      List<ItemsTree<TypeDefinition>> typeDescendants = null;
      try
      {
         typeDescendants = getConnection().getTypeDescendants(null, 2, true);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
      if (typeDescendants == null)
         doFail(testname, "Type Descendants is null;");
      if (!(typeDescendants.size() > 0))
         doFail(testname, "Type Descendants is empty;");
      for (ItemsTree<TypeDefinition> itemsTree : typeDescendants)
      {
         if (itemsTree == null)
            doFail(testname, "Items tree is null;");
         if (itemsTree.getChildren() == null)
            doFail(testname, "Items tree children is null;");
         if (itemsTree.getChildren().size() != 0)
            doFail(testname, "Items tree children is not empty;");
         if (itemsTree.getContainer() == null)
            doFail(testname, "Items tree container is null;");
         if (itemsTree.getContainer().getId() == null)
            doFail(testname, "Items tree container ID is null;");
         if (itemsTree.getContainer().getId().isEmpty())
            doFail(testname, "Items tree container ID is empty;");
         if (itemsTree.getContainer().getDisplayName() == null)
            doFail(testname, "Items tree container DisplayName is empty;");
         if (itemsTree.getContainer().getLocalName() == null)
            doFail(testname, "Items tree container LocalName is empty;");
         if (itemsTree.getContainer().getQueryName() == null)
            doFail(testname, "Items tree container QueryName is empty;");
         if (itemsTree.getContainer().getBaseId() == null)
            doFail(testname, "Items tree container BaseId is empty;");
         checkPropertyDefinitions(itemsTree.getContainer().getPropertyDefinitions());
      }
      pass(testname);
   }

   /**
    * 2.2.2.4 getTypeDescendants for Folder type
    * 
    * Returns the set of descendant Object-Types defined for the Repository under the specified Type.
    */
   public void testGetTypeDescendants_Folder() throws Exception
   {
      String testname = "testGetTypeDescendants_Folder";
      System.out.print("Running " + testname + "....                                  ");
      List<ItemsTree<TypeDefinition>> typeDescendants = null;
      try
      {
         typeDescendants = getConnection().getTypeDescendants("cmis:folder", 2, true);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
      if (typeDescendants == null)
         doFail(testname, "Type Descendants is null;");
      if (typeDescendants.size() != 0)
         doFail(testname, "Type Descendants is not empty;");
      pass(testname);
   }

   /**
    * 2.2.2.4 getTypeDescendants for Document type
    * 
    * Returns the set of descendant Object-Types defined for the Repository under the specified Type.
    */
   public void testGetTypeDescendants_Document() throws Exception
   {
      String testname = "testGetTypeDescendants_Document";
      System.out.print("Running " + testname + "....                                ");
      List<ItemsTree<TypeDefinition>> typeDescendants = null;
      try
      {
         typeDescendants = getConnection().getTypeDescendants("cmis:document", 2, true);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
      if (typeDescendants == null)
         doFail(testname, "Type Descendants is null;");
      if (typeDescendants.size() != 0)
         doFail(testname, "Type Descendants is not empty;");
      pass(testname);
   }

   /**
    * 2.2.2.4 getTypeDescendants for root with IncludePropertyDefinition is false.
    * 
    * Returns the set of descendant Object-Types defined for the Repository under the specified Type.
    */
   public void testGetTypeDescendants_IncludePropertyDefinitionFalse() throws Exception
   {
      String testname = "testGetTypeDescendants_IncludePropertyDefinitionFalse";
      System.out.print("Running " + testname + "....          ");
      List<ItemsTree<TypeDefinition>> typeDescendants = null;
      try
      {
         typeDescendants = getConnection().getTypeDescendants(null, 2, false);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
      if (typeDescendants == null)
         doFail(testname, "Type Descendants is null;");
      if (!(typeDescendants.size() > 0))
         doFail(testname, "Type Descendants is empty;");
      for (ItemsTree<TypeDefinition> itemsTree : typeDescendants)
      {
         if (itemsTree == null)
            doFail(testname, "Items tree is null;");
         if (itemsTree.getContainer() == null)
            doFail(testname, "Items tree container is null;");
         if (itemsTree.getContainer().getPropertyDefinitions() != null)
            doFail(testname, "Property definitions must be empty;");
      }
      pass(testname);
   }

   /**
    * 2.2.2.4 getTypeDescendants for root with depth 1.
    * 
    * Returns the set of descendant Object-Types defined for the Repository under the specified Type.
    */
   public void testGetTypeDescendants_RootWithDepth1() throws Exception
   {
      String testname = "testGetTypeDescendants_RootWithDepth1";
      System.out.print("Running " + testname + "....                          ");
      List<ItemsTree<TypeDefinition>> typeDescendants = null;
      try
      {
         typeDescendants = getConnection().getTypeDescendants(null, 1, true);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
      if (typeDescendants == null)
         doFail(testname, "Type Descendants is null;");
      if (!(typeDescendants.size() > 0))
         doFail(testname, "Type Descendants is empty;");
      for (ItemsTree<TypeDefinition> itemsTree : typeDescendants)
      {
         if (itemsTree == null)
            doFail(testname, "Items tree is null;");
         if (itemsTree.getChildren() != null)
            doFail(testname, "Childrens must be empty;");
      }
      pass(testname);
   }

   /**
    * 2.2.2.4 getTypeDescendants for non existed type.
    * 
    * Returns the set of descendant Object-Types defined for the Repository under the specified Type.
    */
   public void testGetTypeDescendants_NonExistedType() throws Exception
   {
      String testname = "testGetTypeDescendants_NonExistedType";
      System.out.print("Running " + testname + "....                          ");
      try
      {
         getConnection().getTypeDescendants("cmis:kino", 2, true);
         doFail(testname, "The type definition \"cmis:kino\" shouldn't exist.'");
      }
      catch (TypeNotFoundException e)
      {
         pass(testname);
      }
      catch (Exception other)
      {
         doFail(testname, other.getMessage());
      }
   }

   /**
    * 2.2.2.5 getTypeDefinition for Folder type
    * 
    * Gets the definition of the specified Object-Type.
    */
   public void testGetTypeDefinition_Folder() throws Exception
   {
      String testname = "testGetTypeDefinition_Folder";
      System.out.print("Running " + testname + "....                                   ");
      if (folderTypeDefinition == null)
         doFail(testname, "Folder type definition is null;");
      if (folderTypeDefinition.getId() == null)
         doFail(testname, "Folder type definition ID is null;");
      if (folderTypeDefinition.getId().isEmpty())
         doFail(testname, "Folder type definition ID is empty;");
      if (folderTypeDefinition.getLocalName() == null)
         doFail(testname, "Folder type definition local name is empty;");
      if (folderTypeDefinition.getQueryName() == null)
         doFail(testname, "Folder type definition query name is empty;");
      checkPropertyDefinitions(folderTypeDefinition.getPropertyDefinitions());
      pass(testname);
   }

   /**
    * 2.2.2.5 getTypeDefinition for Document type
    * 
    * Gets the definition of the specified Object-Type.
    */
   public void testGetTypeDefinition_Document() throws Exception
   {
      String testname = "testGetTypeDefinition_Document";
      System.out.print("Running " + testname + "....                                 ");
      if (documentTypeDefinition == null)
         doFail(testname, "Document type definition is null;");
      if (documentTypeDefinition.getId() == null)
         doFail(testname, "Document type definition ID is null;");
      if (documentTypeDefinition.getId().isEmpty())
         doFail(testname, "Document type definition ID is empty;");
      if (documentTypeDefinition.getLocalName() == null)
         doFail(testname, "Document type definition local name is empty;");
      if (documentTypeDefinition.getQueryName() == null)
         doFail(testname, "Document type definition query name is empty;");
      checkPropertyDefinitions(documentTypeDefinition.getPropertyDefinitions());
      pass(testname);
   }

   /**
    * 2.2.2.5 getTypeDefinition for non existed type
    * 
    * Gets the definition of the specified Object-Type.
    */
   public void testGetTypeDefinition_NonExistedType() throws Exception
   {
      String testname = "testGetTypeDefinition_NonExistedType";
      System.out.print("Running " + testname + "....                           ");
      try
      {
         getConnection().getTypeDefinition("cmis:kino", false);
         doFail(testname, "The type definition \"cmis:kino\" shouldn't exist.'");
      }
      catch (TypeNotFoundException e)
      {
         pass(testname);
      }
      catch (Exception other)
      {
         doFail(testname, other.getMessage());
      }
   }

   /**
    * 2.2.2.5 getTypeDefinition for Folder type with IncludePropertyDefinition is false. 
    * 
    * Gets the definition of the specified Object-Type.
    */
   public void testGetTypeDefinition_IncludePropertyDefinitionFalse() throws Exception
   {
      String testname = "testGetTypeDefinition_IncludePropertyDefinitionFalse";
      System.out.print("Running " + testname + "....           ");
      TypeDefinition typeDefinition = null;
      try
      {
         typeDefinition = getConnection().getTypeDefinition("cmis:folder", false);
      }
      catch (Exception e)
      {
         doFail(testname, e.getMessage());
      }
      if (typeDefinition == null)
         doFail(testname, "Type definition is null;");
      if (typeDefinition.getId() == null)
         doFail(testname, "Type definition ID is null;");
      if (typeDefinition.getId().isEmpty())
         doFail(testname, "Type definition ID is empty;");
      if (typeDefinition.getLocalName() == null)
         doFail(testname, "Type definition local name is empty;");
      if (typeDefinition.getQueryName() == null)
         doFail(testname, "Type definition query name is empty;");
      Collection<PropertyDefinition<?>> propertyDefinitions = typeDefinition.getPropertyDefinitions();
      if (propertyDefinitions != null)
         doFail(testname, "Property definitions must not be included;");
      pass(testname);
   }

   private void checkPropertyDefinitions(Collection<PropertyDefinition<?>> propertyDefinitions) throws Exception
   {
      String testname = "checkPropertyDefinitions";
      if (propertyDefinitions == null)
         doFail(testname, "propertyDefinitions is null;");
      if (!(propertyDefinitions.size() > 0))
         doFail(testname, "propertyDefinitions is not empty;");
      for (PropertyDefinition<?> propertyDefinition : propertyDefinitions)
      {
         if (propertyDefinition == null)
            doFail(testname, "Type definition is null;");
         if (propertyDefinition.getId() == null)
            doFail(testname, "Type definition ID is null;");
         if (propertyDefinition.getId().isEmpty())
            doFail(testname, "Type definition ID is empty;");
         if (propertyDefinition.getLocalName() == null)
            doFail(testname, "Type definition local name is empty;");
         if (propertyDefinition.getQueryName() == null)
            doFail(testname, "Type definition query name is empty;");
         if (propertyDefinition.getPropertyType() == null)
            doFail(testname, "Type definition property type is empty;");
      }
   }

   protected void pass(String method) throws Exception
   {
      super.pass("RepositoryTest." + method);
   }
   
   protected void doFail( String method,  String message) throws Exception
   {
      super.doFail( "RepositoryTest." + method,  message);
   }
}
