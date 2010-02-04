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

package org.xcmis.sp.jcr.exo;

import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.CmisTypeDocumentDefinitionType;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumContentStreamAllowed;
import org.xcmis.sp.jcr.exo.TypeManagerImpl;
import org.xcmis.spi.TypeManager;

import javax.jcr.RepositoryException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeType;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: TypeDefinitionProviderTest.java 1986 2009-07-06 17:04:47Z
 *          andrew00x $
 */
public class TypeManagerImplTest extends BaseTest
{

   private class Factory extends TypeManagerImpl
   {
      protected NodeType getNodeType(String name) throws NoSuchNodeTypeException, RepositoryException
      {
         return session.getWorkspace().getNodeTypeManager().getNodeType(name);
      }
   }

   private TypeManager factory;

   public void setUp() throws Exception
   {
      super.setUp();
      factory = new Factory();
   }

   public void testTypeIdDocument() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      assertEquals(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value(), def.getId());
   }

   public void testTypeIdFolder() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_FOLDER.value());
      assertEquals(EnumBaseObjectTypeIds.CMIS_FOLDER.value(), def.getId());
   }

   public void testTypeIdRelationship() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      assertEquals(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value(), def.getId());
   }

   // 

   public void testBaseTypeDocument() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      assertEquals(EnumBaseObjectTypeIds.CMIS_DOCUMENT, def.getBaseId());
   }

   public void testBaseTypeFolder() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_FOLDER.value());
      assertEquals(EnumBaseObjectTypeIds.CMIS_FOLDER, def.getBaseId());
   }

   public void testBaseTypeRelationship() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      assertEquals(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP, def.getBaseId());
   }

   // 

   public void testContentStreamAllowedDocument() throws Exception
   {
      CmisTypeDocumentDefinitionType def =
         (CmisTypeDocumentDefinitionType)factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      assertEquals(EnumContentStreamAllowed.ALLOWED, def.getContentStreamAllowed());
   }

   // 

   public void testParentIdDocument() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      assertEquals(null, def.getParentId());
   }

   public void testParentIdFolder() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_FOLDER.value());
      assertEquals(null, def.getParentId());
   }

   public void testParentIdRelationship() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      assertEquals(null, def.getParentId());
   }

   //

   public void testQueryNameDocument() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      assertEquals(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value(), def.getQueryName());
   }

   public void testQueryNameFolder() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_FOLDER.value());
      assertEquals(EnumBaseObjectTypeIds.CMIS_FOLDER.value(), def.getQueryName());
   }

   public void testQueryNameRelationship() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      assertEquals(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value(), def.getQueryName());
   }

   //

   public void testControlableDocument() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      assertTrue(def.isControllablePolicy());
   }

   public void testControlableFolder() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_FOLDER.value());
      assertTrue(def.isControllablePolicy());
   }

   public void testControlableRelationship() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      assertFalse(def.isControllablePolicy());
   }

   //

   public void testCreatableDocument() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      assertTrue(def.isCreatable());
   }

   public void testCreatableFolder() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_FOLDER.value());
      assertTrue(def.isCreatable());
   }

   public void testCreatableRelationship() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      assertTrue(def.isCreatable());
   }

   //

   public void testFileableDocument() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      assertTrue(def.isFileable());
   }

   public void testFileableFolder() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_FOLDER.value());
      assertTrue(def.isFileable());
   }

   public void testFileableRelationship() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      assertFalse(def.isFileable());
   }

   //

   public void testIncludedInSupertypeQueryDocument() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      assertTrue(def.isIncludedInSupertypeQuery());
   }

   public void testIncludedInSupertypeQueryFolder() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_FOLDER.value());
      assertTrue(def.isIncludedInSupertypeQuery());
   }

   public void testIncludedInSupertypeQueryRelationship() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      assertFalse(def.isIncludedInSupertypeQuery());
   }

   //

   public void testQueryableDocument() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      assertTrue(def.isQueryable());
   }

   public void testQueryableFolder() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_FOLDER.value());
      assertTrue(def.isQueryable());
   }

   public void testQueryableRelationship() throws Exception
   {
      CmisTypeDefinitionType def = factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      assertFalse(def.isQueryable());
   }

   //

   public void testVersionableDocument() throws Exception
   {
      CmisTypeDocumentDefinitionType def =
         (CmisTypeDocumentDefinitionType)factory.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      assertTrue(def.isVersionable());
   }

}
