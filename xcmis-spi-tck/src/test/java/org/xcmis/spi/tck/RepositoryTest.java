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
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.CmisRegistry;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.ItemsTree;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.model.BaseType;
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
      System.out.println("Running Repository Service tests");
   }

   @AfterClass
   public static void stop() throws Exception
   {
   }

   /**
    * 2.2.2.1 getRepositories
    * <p>
    * Returns a list of CMIS repositories available from this CMIS service
    * endpoint.
    * </p>
    */
   @Test
   public void testGetRepositories() throws Exception
   {
      Set<RepositoryShortInfo> storageInfos = CmisRegistry.getInstance().getStorageInfos();
      assertNotNull("StorageInfo is null.", storageInfos);
      assertTrue("StorageInfo is empty.", !storageInfos.isEmpty());
      for (RepositoryShortInfo repositoryShortInfo : storageInfos)
      {
         assertTrue("Repository id is missing.", repositoryShortInfo.getRepositoryId() != null
            && repositoryShortInfo.getRepositoryId().length() > 0);
         assertTrue("Repository name is missing.", repositoryShortInfo.getRepositoryName() != null
            && repositoryShortInfo.getRepositoryName().length() > 0);

         assertNotNull("Root folder id is missing.", repositoryShortInfo.getRootFolderId());
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
      assertNotNull("RepositoryInfo is null.", repositoryInfo);
      assertTrue("Repository id is missing.", repositoryInfo.getRepositoryId() != null
         && repositoryInfo.getRepositoryId().length() > 0);
      assertNotNull("Repository name is missing.", repositoryInfo.getRepositoryName());

      assertNotNull("Repository VendorName is missing.", repositoryInfo.getVendorName());
      assertNotNull("Repository ProductName is missing.", repositoryInfo.getProductName());
      assertNotNull("Repository PropductVersion is missing.", repositoryInfo.getProductVersion());
      assertNotNull("Repository Root Folder id is missing.", repositoryInfo.getRootFolderId());
      assertNotNull("Repository Capabilities is missing.", repositoryInfo.getCapabilities());

      assertNotNull("Repository version supported is missing.", repositoryInfo.getCmisVersionSupported());
      assertNotNull("Repository Changes on type is missing.", repositoryInfo.getChangesOnType());
      assertNotNull("Repository ACL capability is missing.", repositoryInfo.getAclCapability());

      if (!repositoryInfo.getCapabilities().getCapabilityACL().equals(CapabilityACL.NONE))
      {
         assertNotNull("Repository supported permissions is missing.", repositoryInfo.getAclCapability()
            .getSupportedPermissions());
         assertNotNull("Repository ACL propagation is missing.", repositoryInfo.getAclCapability().getPropagation());
         assertNotNull("Repository ACL permissions is missing.", repositoryInfo.getAclCapability().getPermissions());
         assertNotNull("Repository ACL mapping is missing.", repositoryInfo.getAclCapability().getMapping());
      }
      assertNotNull("Repository principal anonymous is missing.", repositoryInfo.getPrincipalAnonymous());
      assertNotNull("Repository principal anyone is missing.", repositoryInfo.getPrincipalAnyone());

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
   public void testGetTypeChildren() throws Exception
   {
      ItemsList<TypeDefinition> typeChildren = connection.getTypeChildren(null, false, -1, 0);
      List<String> tids = new ArrayList<String>();
      for (TypeDefinition t : typeChildren.getItems())
      {
         tids.add(t.getId());
         assertNull(t.getPropertyDefinitions()); // was not requested.
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
    * 2.2.2.3 getTypeDescendants.
    * <p>
    * Returns the set of children object types defined for the Repository under
    * the specified type and include property definitions.
    * </p>
    * 
    * @exception Exception
    */
   @Test
   public void testGetTypeChildren_IncludeProperty() throws Exception
   {
      ItemsList<TypeDefinition> typeChildren = connection.getTypeChildren(null, true, -1, 0);
      for (TypeDefinition t : typeChildren.getItems())
      {
         assertNotNull("Property definitions not found.", t.getPropertyDefinitions());
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
    * 
    * @throws Exception
    */
   @Test
   public void testGetTypeChildren_MaxItems() throws Exception
   {
      ItemsList<TypeDefinition> types = connection.getTypeChildren(null, false, 1, 0);
      assertNotNull("Root type childrens is null.", types);
      assertTrue("Incorrect Root type childrens size.", types.getItems().size() <= 1);
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
   public void testGetTypeChildren_SkipCount() throws Exception
   {
      // Get all items first.
      ItemsList<TypeDefinition> types = connection.getTypeChildren(null, false, -1, 0);
      List<String> tids = new ArrayList<String>();
      for (TypeDefinition t : types.getItems())
      {
         tids.add(t.getId());
      }

      types = connection.getTypeChildren(null, false, -1, 1);
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
    * <p>
    * {@link TypeNotFoundException} must be thrown if type for which children
    * requested is not exists.
    * </p>
    * 
    * @throws Exception
    */
   @Test
   public void testGetTypeChildren_TypeNotFoundException() throws Exception
   {
      String type = "cmis:document" + System.currentTimeMillis();
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
    * 2.2.2.5 getTypeDefinition.
    * 
    * @throws Exception
    */
   @Test
   public void testGetTypeDefinition_Document() throws Exception
   {
      TypeDefinition type = connection.getTypeDefinition(CmisConstants.DOCUMENT, false);
      assertEquals(CmisConstants.DOCUMENT, type.getId());
      assertEquals(BaseType.DOCUMENT, type.getBaseId());
      assertNotNull("Query name required. " + type.getQueryName());
      assertNull("Root type may not have parent type. ", type.getParentId());
      assertNull("Property definitions was not requested. ", type.getPropertyDefinitions());
   }

   /**
    * 2.2.2.5 getTypeDefinition.
    * 
    * @throws Exception
    */
   @Test
   public void testGetTypeDefinition_Folder() throws Exception
   {
      TypeDefinition type = connection.getTypeDefinition(CmisConstants.FOLDER, false);
      assertEquals(CmisConstants.FOLDER, type.getId());
      assertEquals(BaseType.FOLDER, type.getBaseId());
      assertNotNull("Query name required. " + type.getQueryName());
      assertNull("Root type may not have parent type. ", type.getParentId());
      assertTrue("Folder type is fileable. ", type.isFileable());
      assertNull("Property definitions was not requested. ", type.getPropertyDefinitions());
   }

   /**
    * 2.2.2.5 getTypeDefinition.
    * 
    * @throws Exception
    */
   @Test
   public void testGetTypeDefinition_Policy() throws Exception
   {
      TypeDefinition type = connection.getTypeDefinition(CmisConstants.POLICY, false);
      assertEquals(CmisConstants.POLICY, type.getId());
      assertEquals(BaseType.POLICY, type.getBaseId());
      assertNotNull("Query name required. " + type.getQueryName());
      assertNull("Root type may not have parent type. ", type.getParentId());
      assertNull("Property definitions was not requested. ", type.getPropertyDefinitions());
   }

   /**
    * 2.2.2.5 getTypeDefinition.
    * 
    * @throws Exception
    */
   @Test
   public void testGetTypeDefinition_Relationship() throws Exception
   {
      if (!isRelationshipsSupported)
      {
         return;
      }
      TypeDefinition type = connection.getTypeDefinition(CmisConstants.RELATIONSHIP, false);
      assertEquals(CmisConstants.RELATIONSHIP, type.getId());
      assertEquals(BaseType.RELATIONSHIP, type.getBaseId());
      assertNotNull("Query name required. " + type.getQueryName());
      assertNull("Root type may not have parent type. ", type.getParentId());
      assertFalse("Relationship type is not fileable. ", type.isFileable());
      assertNull("Property definitions was not requested. ", type.getPropertyDefinitions());
   }

   /**
    * 2.2.2.5 getTypeDefinition.
    * <p>
    * {@link TypeNotFoundException} must be thrown if type for which children
    * requested is not exists.
    * </p>
    * 
    * @throws Exception
    */
   @Test
   public void testGetTypeDefinition_TypeNotFoundException() throws Exception
   {
      String type = "cmis:document" + System.currentTimeMillis();
      try
      {
         connection.getTypeDefinition(type, false);
         fail("TypeNotFoundException must be thrown, type definition " + type + " shouldn't exist. ");
      }
      catch (TypeNotFoundException e)
      {
      }
   }

   /**
    * 2.2.2.4 getTypeDescendants.
    * <p>
    * Returns the set of descendant object types defined for the Repository
    * under the specified Type.
    * </p>
    * 
    * @throws Exception
    */
   @Test
   public void testGetTypeDescendants() throws Exception
   {
      List<ItemsTree<TypeDefinition>> typeDescendants = connection.getTypeDescendants(null, -1, false);
      List<TypeDefinition> treeAsList = typeTreeAsList(typeDescendants);
      List<String> tids = new ArrayList<String>();
      for (TypeDefinition t : treeAsList)
      {
         tids.add(t.getId());
         assertNull(t.getPropertyDefinitions()); // was not requested.
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
    * 2.2.2.4 getTypeDescendants.
    * <p>
    * Returns the set of descendant object types defined for the Repository
    * under the specified type and include property definitions.
    * </p>
    * 
    * @exception Exception
    */
   @Test
   public void testGetTypeDescendants_IncludeProperty() throws Exception
   {
      List<ItemsTree<TypeDefinition>> typeDescendants = connection.getTypeDescendants(null, -1, true);
      List<TypeDefinition> treeAsList = typeTreeAsList(typeDescendants);
      for (TypeDefinition t : treeAsList)
      {
         assertNotNull("Property definitions not found.", t.getPropertyDefinitions());
      }
   }

   /**
    * 2.2.2.4 getTypeChildren.
    * <p>
    * {@link TypeNotFoundException} must be thrown if type for which children
    * requested is not exists.
    * </p>
    * 
    * @throws Exception
    */
   @Test
   public void testGetTypeDescendants_TypeNotFoundException() throws Exception
   {
      String type = "cmis:document" + System.currentTimeMillis();
      try
      {
         connection.getTypeDescendants(type, -1, false);
         fail("TypeNotFoundException must be thrown, type definition " + type + " shouldn't exist. ");
      }
      catch (TypeNotFoundException e)
      {
      }
   }
}
