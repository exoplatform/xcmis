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

import static org.junit.Assert.*;

import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.CmisRegistry;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.ItemsTree;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.model.CapabilityACL;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.RepositoryShortInfo;
import org.xcmis.spi.model.TypeDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 2.2.2 Repository Services
 * The Repository Services (getRepositories, getRepositoryInfo, getTypeChildren, getTypeDescendants,
 * getTypeDefinition) are used to discover information about the repository, including information about the
 * repository and the object-types defined for the repository.
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id$
 */
public class RepositoryTest extends BaseTest
{

   @BeforeClass
   public static void start() throws Exception
   {
      BaseTest.setUp();
      System.out.print("Running Repository Service tests....");
   }

   /**
    * 2.2.2.1 getRepositories
    * 
    * Returns a list of CMIS repositories available from this CMIS service endpoint.
    */
   @Test
   public void testGetRepositories() throws Exception
   {
      Set<RepositoryShortInfo> storageInfos = CmisRegistry.getInstance().getStorageInfos();
      assertNotNull("StorageInfo  is null.", storageInfos);
      assertTrue("StorageInfo  is empty.", !storageInfos.isEmpty());
      for (RepositoryShortInfo repositoryShortInfo : storageInfos)
      {
         assertNotNull("Repository Short Info  is null.", repositoryShortInfo.getRepositoryId());
         assertTrue("Repository Short Info  is empty.", !repositoryShortInfo.getRepositoryId().equals(""));
         assertNotNull("Repository name  is null.",repositoryShortInfo.getRepositoryName());
         assertTrue("Repository name  is empty.",!repositoryShortInfo.getRepositoryName().equals(""));
         assertNotNull("Root folder ID  is null.", repositoryShortInfo.getRootFolderId());
      }
   }

   /**
    * 2.2.2.2 getRepositoryInfo
    * 
    * Returns information about the CMIS repository, the optional capabilities it supports and its Access Control information if applicable.
    * 
    * @throws Exception
    */
   @Test
   public void testGetRepositoryInfo() throws Exception
   {
      assertNotNull("Repository Info  is null.", getStorage().getRepositoryInfo());
      assertNotNull("Repository Info ID  is null.", getStorage().getRepositoryInfo().getRepositoryId());
      assertTrue("Repository Info ID  is empty.", !getStorage().getRepositoryInfo().getRepositoryId().equals(""));
      assertNotNull("Repository Info Name  is null.", getStorage().getRepositoryInfo().getRepositoryName());
      assertNotNull("Repository Description  is null.", getStorage().getRepositoryInfo().getRepositoryDescription());
         
      assertNotNull("Repository VendorName  is null.", getStorage().getRepositoryInfo().getVendorName());
      assertNotNull("Repository ProductName  is null.", getStorage().getRepositoryInfo().getProductName());
      assertNotNull("Repository PropductVersion  is null.", getStorage().getRepositoryInfo().getProductVersion());
      assertNotNull("Repository Root folder ID  is null.", getStorage().getRepositoryInfo().getRootFolderId());
      assertNotNull("Repository Capabilities  is null.", getStorage().getRepositoryInfo().getCapabilities());

      //      assertNotNull(getStorage().getRepositoryInfo().getLatestChangeLogToken());
      assertNotNull("Repository version supported  is null.", getStorage().getRepositoryInfo().getCmisVersionSupported());
      //      assertNotNull(getStorage().getRepositoryInfo().getThinClientURI());
      //      if(getStorage().getRepositoryInfo().isChangesIncomplete().)
      //         doFail(testname, "Repository Description  is null;");
      assertNotNull("Repository Changes on type  is null.", getStorage().getRepositoryInfo().getChangesOnType());
      assertNotNull("Repository ACL capability  is null.", getStorage().getRepositoryInfo().getAclCapability());
      
      if(!getStorage().getRepositoryInfo().getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE)) {
        assertNotNull("Repository supported permissions  is null.", getStorage().getRepositoryInfo().getAclCapability().getSupportedPermissions());
        assertNotNull("Repository ACL propagation  is null.", getStorage().getRepositoryInfo().getAclCapability().getPropagation());
        assertNotNull("Repository ACL permissions  is null.", getStorage().getRepositoryInfo().getAclCapability().getPermissions());
        assertNotNull("Repository ACL mapping  is null.", getStorage().getRepositoryInfo().getAclCapability().getMapping());
      }
      assertNotNull("Repository principal anonymous  is null.", getStorage().getRepositoryInfo().getPrincipalAnonymous());
      assertNotNull("Repository principal anyone  is null.", getStorage().getRepositoryInfo().getPrincipalAnyone());

   }

   /**
    * 2.2.2.3 getTypeChildren
    * 
    * Returns the list of Object-Types defined for the Repository 
    * that are children of the specified Type.
    */
   @Test
   public void testGetTypeChildren_Root() throws Exception
   {
      // root types
      ItemsList<TypeDefinition> typeChildren0 = null;
      typeChildren0 = getConnection().getTypeChildren(null, true, -1, 0);
      assertNotNull("Root typer childrens is null.", typeChildren0);
         
      List<TypeDefinition> typeChildrenList = typeChildren0.getItems();
      assertNotNull("Root typer childrens is empty.", typeChildrenList);
      List<String> ll = new ArrayList<String>();
      ll.add(CmisConstants.DOCUMENT);
      ll.add(CmisConstants.FOLDER);
      for (TypeDefinition typeDefinition : typeChildrenList)
      {
         assertNotNull("TypeDefinition is null.", typeDefinition);
         assertNotNull("TypeDefinition  ID is null.", typeDefinition.getId());
         assertTrue("TypeDefinition  ID is empty.", !typeDefinition.getId().equals(""));
         assertNotNull("TypeDefinition  BaseId is empty.", typeDefinition.getBaseId());
         assertTrue("TypeDefinition  BaseId  does not match.", typeDefinition.getId().equals(typeDefinition.getBaseId().value()));
            
         assertNotNull("TypeDefinition  display name is null.", typeDefinition.getDisplayName());
         assertNotNull("TypeDefinition  local name is null.", typeDefinition.getLocalName());
         assertNotNull("TypeDefinition query name is null.", typeDefinition.getQueryName());
         checkPropertyDefinitions(typeDefinition.getPropertyDefinitions());

         if (ll.size() > 0)
            if (!ll.contains(typeDefinition.getId()))
               fail("Mandatory type definition not found.");
         ll.remove(typeDefinition.getId());
      }
      if (!ll.isEmpty())
         fail("Not all mandatory types found;");
   }

   /**
    * 2.2.2.3 getTypeChildren With MaxItems
    * 
    * Returns the list of Object-Types defined for the Repository 
    * that are children of the specified Type.
    */
   @Test
   public void testGetTypeChildren_RootWithMaxItems() throws Exception
   {
      // root types with maxItems
      ItemsList<TypeDefinition> typeChildren3 = null;
         typeChildren3 = getConnection().getTypeChildren(null, true, 1, 0);
      assertNotNull("Root type childrens is null.", typeChildren3);
      assertTrue("Incorrect Root type childrens size.", typeChildren3.getItems().size() == 1);
   }

   /**
    * 2.2.2.3 getTypeChildren With SkipCount
    * 
    * Returns the list of Object-Types defined for the Repository 
    * that are children of the specified Type.
    */
   @Test
   public void testGetTypeChildren_RootWithSkipCount() throws Exception
   {
      // get size of root types
      ItemsList<TypeDefinition> typeChildren0 = null;
         typeChildren0 = getConnection().getTypeChildren(null, true, -1, 0);
      List<TypeDefinition> typeChildrenList = typeChildren0.getItems();
      int sizeOfRootTypes = typeChildrenList.size();

      // root types with skipCount
      ItemsList<TypeDefinition> typeChildren4 = null;
         typeChildren4 = getConnection().getTypeChildren(null, true, -1, sizeOfRootTypes - 1);
         assertNotNull("Root type childrens is null.", typeChildren4);
         assertTrue("Incorrect Root type childrens size.", typeChildren4.getItems().size() == 1);
   }

   /**
    * 2.2.2.3 getTypeChildren for Folder type
    * 
    * Returns the list of Object-Types defined for the Repository 
    * that are children of the specified Type.
    */
   @Test
   public void testGetTypeChildren_Folder() throws Exception
   {
      // folder
      ItemsList<TypeDefinition> typeChildren1 = null;
         typeChildren1 = getConnection().getTypeChildren("cmis:folder", true, -1, 0);
      assertNotNull("Root type childrens is null.", typeChildren1);
   }

   /**
    * 2.2.2.3 getTypeChildren for Document type
    * 
    * Returns the list of Object-Types defined for the Repository 
    * that are children of the specified Type.
    */
   @Test
   public void testGetTypeChildren_Document() throws Exception
   {
      // document
      ItemsList<TypeDefinition> typeChildren2 = null;
         typeChildren2 = getConnection().getTypeChildren("cmis:document", true, -1, 0);
         assertNotNull("Root type childrens is null.", typeChildren2);
   }

   /**
    * 2.2.2.3 getTypeChildren for Non existed type
    * 
    * Returns the list of Object-Types defined for the Repository 
    * that are children of the specified Type.
    */
   @Test
   public void testGetTypeChildren_NonExistedType() throws Exception
   {
      // to get children for nonexistent type "cmis:kino"
      try
      {
         getConnection().getTypeChildren("cmis:kino", false, -1, 0);
         fail("The type definition \"cmis:kino\" shouldn't exist.'");
      }
      catch (TypeNotFoundException e)
      {
         //OK
      }
   }

   /**
    * 2.2.2.4 getTypeDescendants for root
    * 
    * Returns the set of descendant Object-Types defined for the Repository under the specified Type.
    */
   @Test
   public void testGetTypeDescendants() throws Exception
   {
      List<ItemsTree<TypeDefinition>> typeDescendants = null;
         typeDescendants = getConnection().getTypeDescendants(null, 2, true);
      assertNotNull("Type Descendants is null.", typeDescendants);
      assertTrue("Type Descendants is empty.", typeDescendants.size() > 0);

      for (ItemsTree<TypeDefinition> itemsTree : typeDescendants)
      {
         assertNotNull("Items tree is null.", itemsTree);
         assertNotNull("Items tree children is null.", itemsTree.getChildren());
         assertNotNull("Items tree container is null.", itemsTree.getContainer());
         assertNotNull("Items tree container ID is null.",itemsTree.getContainer().getId());
            
         assertTrue("Items tree container ID is empty.", !itemsTree.getContainer().getId().equals(""));
         assertNotNull ("Items tree container DisplayName is empty.", itemsTree.getContainer().getDisplayName());
         assertNotNull("Items tree container LocalName is empty.", itemsTree.getContainer().getLocalName());
         assertNotNull("Items tree container QueryName is empty.", itemsTree.getContainer().getQueryName() == null);
         assertNotNull("Items tree container BaseId is empty.", itemsTree.getContainer().getBaseId());
         checkPropertyDefinitions(itemsTree.getContainer().getPropertyDefinitions());
      }
   }

   /**
    * 2.2.2.4 getTypeDescendants for Folder type
    * 
    * Returns the set of descendant Object-Types defined for the Repository under the specified Type.
    */
   @Test
   public void testGetTypeDescendants_Folder() throws Exception
   {
      List<ItemsTree<TypeDefinition>> typeDescendants = null;
         typeDescendants = getConnection().getTypeDescendants("cmis:folder", 2, true);
         assertNotNull("Type Descendants is null.", typeDescendants);
   }

   /**
    * 2.2.2.4 getTypeDescendants for Document type
    * 
    * Returns the set of descendant Object-Types defined for the Repository under the specified Type.
    */
   @Test
   public void testGetTypeDescendants_Document() throws Exception
   {
      List<ItemsTree<TypeDefinition>> typeDescendants = null;
         typeDescendants = getConnection().getTypeDescendants("cmis:document", 2, true);
         assertNotNull("Type Descendants is null.", typeDescendants);
   }

   /**
    * 2.2.2.4 getTypeDescendants for root with IncludePropertyDefinition is false.
    * 
    * Returns the set of descendant Object-Types defined for the Repository under the specified Type.
    */
   @Test
   public void testGetTypeDescendants_IncludePropertyDefinitionFalse() throws Exception
   {
      List<ItemsTree<TypeDefinition>> typeDescendants = null;
         typeDescendants = getConnection().getTypeDescendants(null, 2, false);
         assertNotNull("Type Descendants is null.", typeDescendants);
         assertTrue("Type Descendants is  empty.", typeDescendants.size() > 0);
      for (ItemsTree<TypeDefinition> itemsTree : typeDescendants)
      {
         assertNotNull("Items tree is null.", itemsTree);
         assertNotNull("Items tree container is null.", itemsTree.getContainer());
         assertNull("Property definitions must be empty.", itemsTree.getContainer().getPropertyDefinitions());
      }
   }

   /**
    * 2.2.2.4 getTypeDescendants for root with depth 1.
    * 
    * Returns the set of descendant Object-Types defined for the Repository under the specified Type.
    */
   @Test
   public void testGetTypeDescendants_RootWithDepth1() throws Exception
   {
      List<ItemsTree<TypeDefinition>> typeDescendants = null;
         typeDescendants = getConnection().getTypeDescendants(null, 1, true);
      assertNotNull("Type Descendants is null.", typeDescendants);
      assertTrue("Type Descendants is empty.", typeDescendants.size() > 0);
      for (ItemsTree<TypeDefinition> itemsTree : typeDescendants)
      {
         assertNotNull("Items tree is null.", itemsTree);
         assertNull("Childrens must be empty.", itemsTree.getChildren());
      }
   }

   /**
    * 2.2.2.4 getTypeDescendants for non existed type.
    * 
    * Returns the set of descendant Object-Types defined for the Repository under the specified Type.
    */
   @Test
   public void testGetTypeDescendants_NonExistedType() throws Exception
   {
      try
      {
         getConnection().getTypeDescendants("cmis:kino", 2, true);
        fail("The type definition \"cmis:kino\" shouldn't exist.'");
      }
      catch (TypeNotFoundException e)
      {
         //OK
      }
   }

   /**
    * 2.2.2.5 getTypeDefinition for Folder type
    * 
    * Gets the definition of the specified Object-Type.
    */
   @Test
   public void testGetTypeDefinition_Folder() throws Exception
   {
      assertNotNull("Folder type definition is null.", folderTypeDefinition);
      assertNotNull("Folder type definition ID is null.", folderTypeDefinition.getId());

      assertTrue("Folder type definition ID is empty.", !folderTypeDefinition.getId().equals(""));
      assertNotNull("Folder type definition local name is empty.", folderTypeDefinition.getLocalName());
      assertNotNull("Folder type definition query name is empty.", folderTypeDefinition.getQueryName());
      checkPropertyDefinitions(folderTypeDefinition.getPropertyDefinitions());
   }

   /**
    * 2.2.2.5 getTypeDefinition for Document type
    * 
    * Gets the definition of the specified Object-Type.
    */
   @Test
   public void testGetTypeDefinition_Document() throws Exception
   {
      assertNotNull("Document type definition is null.",documentTypeDefinition);
      assertNotNull("Document type definition ID is null.", documentTypeDefinition.getId());
         
      assertTrue("Document type definition ID is empty.", !documentTypeDefinition.getId().equals(""));
      assertNotNull("Document type definition local name is empty.", documentTypeDefinition.getLocalName());
      assertNotNull("Document type definition query name is empty.", documentTypeDefinition.getQueryName());
      checkPropertyDefinitions(documentTypeDefinition.getPropertyDefinitions());
   }

   /**
    * 2.2.2.5 getTypeDefinition for non existed type
    * 
    * Gets the definition of the specified Object-Type.
    */
   @Test
   public void testGetTypeDefinition_NonExistedType() throws Exception
   {
      try
      {
         getConnection().getTypeDefinition("cmis:kino", false);
         fail("The type definition \"cmis:kino\" shouldn't exist.'");
      }
      catch (TypeNotFoundException e)
      {
         //OK
      }
   }

   /**
    * 2.2.2.5 getTypeDefinition for Folder type with IncludePropertyDefinition is false. 
    * 
    * Gets the definition of the specified Object-Type.
    */
   @Test
   public void testGetTypeDefinition_IncludePropertyDefinitionFalse() throws Exception
   {
      TypeDefinition typeDefinition = null;
      typeDefinition = getConnection().getTypeDefinition("cmis:folder", false);
      assertNotNull("Type definition is null.", typeDefinition);
      assertNotNull("Type definition ID is null.", typeDefinition.getId());
      assertFalse("Type definition ID is empty.", typeDefinition.getId().equals(""));
      assertNotNull("Type definition local name is empty.", typeDefinition.getLocalName());
      assertNotNull("Type definition query name is empty.", typeDefinition.getQueryName());

      Collection<PropertyDefinition<?>> propertyDefinitions = typeDefinition.getPropertyDefinitions();
      assertNull("Property definitions must not be included;", propertyDefinitions);
   }

   private void checkPropertyDefinitions(Collection<PropertyDefinition<?>> propertyDefinitions) throws Exception
   {
      assertNotNull("PropertyDefinitions is null.", propertyDefinitions);
      for (PropertyDefinition<?> propertyDefinition : propertyDefinitions)
      {
         assertNotNull("Type definition is null.", propertyDefinition);
         assertNotNull("Type definition ID is null.", propertyDefinition.getId());
         assertFalse("Type definition ID is empty.", propertyDefinition.getId().equals(""));
         assertNotNull("Type definition local name is empty.", propertyDefinition.getLocalName());
         assertNotNull("Type definition query name is empty.", propertyDefinition.getQueryName());
         assertNotNull("Type definition property type is empty.", propertyDefinition.getPropertyType());
      }
   }

   @AfterClass
   public static void stop() throws Exception
   {
      System.out.println("done;");
      if (BaseTest.conn != null)
         BaseTest.conn.close();
   }
}
