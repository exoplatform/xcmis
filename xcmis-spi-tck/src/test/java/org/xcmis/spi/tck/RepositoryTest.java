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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xcmis.spi.CmisRegistry;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.ItemsTree;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.model.CapabilityACL;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.RepositoryInfo;
import org.xcmis.spi.model.RepositoryShortInfo;
import org.xcmis.spi.model.TypeDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 2.2.2 Repository Services The Repository Services (getRepositories,
 * getRepositoryInfo, getTypeChildren, getTypeDescendants, getTypeDefinition)
 * are used to discover information about the repository, including information
 * about the repository and the object-types defined for the repository.
 *
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey
 *         Zavizionov</a>
 * @version $Id$
 */
public class RepositoryTest extends BaseTest
{

   @BeforeClass
   public static void start() throws Exception
   {
      BaseTest.setUp();
      System.out.println("Running Repository Service tests");
   }

   /**
    * 2.2.2.1 getRepositories
    *
    * Returns a list of CMIS repositories available from this CMIS service
    * endpoint.
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
         assertNotNull("Repository name  is null.", repositoryShortInfo.getRepositoryName());
         assertTrue("Repository name  is empty.", !repositoryShortInfo.getRepositoryName().equals(""));
         assertNotNull("Root folder ID  is null.", repositoryShortInfo.getRootFolderId());
      }
   }

   /**
    * 2.2.2.2 getRepositoryInfo.
    * <p>
    * Returns information about the CMIS repository, the optional capabilities
    * it supports and its Access Control information if applicable.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetRepositoryInfo() throws Exception
   {
      RepositoryInfo repositoryInfo = connection.getStorage().getRepositoryInfo();
      assertNotNull("Repository Info  is null.", repositoryInfo);
      assertNotNull("Repository Info ID  is null.", repositoryInfo.getRepositoryId());
      assertTrue("Repository Info ID  is empty.", repositoryInfo.getRepositoryId().length() > 0);
      assertNotNull("Repository Info Name  is null.", repositoryInfo.getRepositoryName());
      assertNotNull("Repository Description  is null.", repositoryInfo.getRepositoryDescription());

      assertNotNull("Repository VendorName  is null.", repositoryInfo.getVendorName());
      assertNotNull("Repository ProductName  is null.", repositoryInfo.getProductName());
      assertNotNull("Repository PropductVersion  is null.", repositoryInfo.getProductVersion());
      assertNotNull("Repository Root folder ID  is null.", repositoryInfo.getRootFolderId());
      assertNotNull("Repository Capabilities  is null.", repositoryInfo.getCapabilities());

      assertNotNull("Repository version supported  is null.", repositoryInfo.getCmisVersionSupported());
      assertNotNull("Repository Changes on type  is null.", repositoryInfo.getChangesOnType());
      assertNotNull("Repository ACL capability  is null.", repositoryInfo.getAclCapability());

      if (!repositoryInfo.getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         assertNotNull("Repository supported permissions  is null.", repositoryInfo.getAclCapability()
            .getSupportedPermissions());
         assertNotNull("Repository ACL propagation  is null.", repositoryInfo.getAclCapability().getPropagation());
         assertNotNull("Repository ACL permissions  is null.", repositoryInfo.getAclCapability().getPermissions());
         assertNotNull("Repository ACL mapping  is null.", repositoryInfo.getAclCapability().getMapping());
      }
      assertNotNull("Repository principal anonymous  is null.", repositoryInfo.getPrincipalAnonymous());
      assertNotNull("Repository principal anyone  is null.", repositoryInfo.getPrincipalAnyone());

   }

   /**
    * 2.2.2.3 getTypeChildren.
    * <p>
    * Returns the list of Object-Types defined for the Repository that are
    * children of the specified Type.
    * </p>
    *
    * @throws Exception
    */
   @Test
   public void testGetTypeChildren_Root() throws Exception
   {
      ItemsList<TypeDefinition> typeChildren = connection.getTypeChildren(null, true, -1, 0);
      List<String> tids = new ArrayList<String>();
      for (TypeDefinition t : typeChildren.getItems())
      {
         tids.add(t.getId());
      }
      List<String> exp = new ArrayList<String>();
      exp.add("cmis:document");
      exp.add("cmis:folder");
      if (isPoliciesSupported)
      {
         exp.add("cmis:policy");
      }
      if (isRelationshipsSupported)
      {
         exp.add("cmis:relationship");
      }
      for (String s : exp)
      {
         assertTrue("Expected type " + s + " is not found in result. ", tids.contains(s));
      }
   }

   /**
    * 2.2.2.3 getTypeChildren.
    * <p>
    * Returns the list of Object-Types defined for the Repository that are
    * children of the specified Type. See section 2.2.1.1 "Paging". If optional
    * attribute 'maxItems' specified then number of items contained in the
    * response must not exceed specified value.
    * </p>
    */
   @Test
   public void testGetTypeChildren_RootWithMaxItems() throws Exception
   {
      ItemsList<TypeDefinition> types = connection.getTypeChildren(null, true, 1, 0);
      assertNotNull("Root type childrens is null.", types);
      assertTrue("Incorrect Root type childrens size.", types.getItems().size() <= 1);
   }

   /**
    * 2.2.2.3 getTypeChildren.
    *
    * Returns the list of Object-Types defined for the Repository that are
    * children of the specified Type.
    */
   @Test
   public void testGetTypeChildren_WithSkipCount() throws Exception
   {
      // Get all items first.
      ItemsList<TypeDefinition> types = connection.getTypeChildren(null, true, -1, 0);
      List<String> tids = new ArrayList<String>();
      for (TypeDefinition t : types.getItems())
      {
         tids.add(t.getId());
      }

      types = connection.getTypeChildren(null, true, -1, 1);
      List<String> tidsPage = new ArrayList<String>();
      for (TypeDefinition t : types.getItems())
      {
         tidsPage.add(t.getId());
      }
      // Skip 1 items.
      Iterator<String> iterator0 = tids.iterator();
      iterator0.next();
      iterator0.remove();

      assertEquals(tids, tidsPage);
   }

   /**
    * 2.2.2.3 getTypeChildren.
    *
    * {@link TypeNotFoundException} must be thrown if type for which children
    * requested is not exists.
    */
   @Test
   public void testGetTypeChildren_NonExistedType() throws Exception
   {
      String type = "cmis:document0000";
      try
      {
         connection.getTypeChildren(type, false, -1, 0);
         fail("TypeNotFoundException must be thrown, type definition " + type + " shouldn't exist. ");
      }
      catch (TypeNotFoundException e)
      {
      }
   }

   /**
    * 2.2.2.4 getTypeDescendants for root
    *
    * Returns the set of descendant Object-Types defined for the Repository
    * under the specified Type.
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
         assertNotNull("Items tree container ID is null.", itemsTree.getContainer().getId());

         assertTrue("Items tree container ID is empty.", !itemsTree.getContainer().getId().equals(""));
         assertNotNull("Items tree container DisplayName is empty.", itemsTree.getContainer().getDisplayName());
         assertNotNull("Items tree container LocalName is empty.", itemsTree.getContainer().getLocalName());
         assertNotNull("Items tree container QueryName is empty.", itemsTree.getContainer().getQueryName() == null);
         assertNotNull("Items tree container BaseId is empty.", itemsTree.getContainer().getBaseId());
         checkPropertyDefinitions(itemsTree.getContainer().getPropertyDefinitions());
      }
   }

   /**
    * 2.2.2.4 getTypeDescendants for Folder type
    *
    * Returns the set of descendant Object-Types defined for the Repository
    * under the specified Type.
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
    * Returns the set of descendant Object-Types defined for the Repository
    * under the specified Type.
    */
   @Test
   public void testGetTypeDescendants_Document() throws Exception
   {
      List<ItemsTree<TypeDefinition>> typeDescendants = null;
      typeDescendants = getConnection().getTypeDescendants("cmis:document", 2, true);
      assertNotNull("Type Descendants is null.", typeDescendants);
   }

   /**
    * 2.2.2.4 getTypeDescendants for root with IncludePropertyDefinition is
    * false.
    *
    * Returns the set of descendant Object-Types defined for the Repository
    * under the specified Type.
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
    * Returns the set of descendant Object-Types defined for the Repository
    * under the specified Type.
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
    * Returns the set of descendant Object-Types defined for the Repository
    * under the specified Type.
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
      assertNotNull("Document type definition is null.", documentTypeDefinition);
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
    * 2.2.2.5 getTypeDefinition for Folder type with IncludePropertyDefinition
    * is false.
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
   }
}
