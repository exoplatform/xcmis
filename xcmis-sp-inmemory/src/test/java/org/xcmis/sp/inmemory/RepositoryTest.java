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

package org.xcmis.sp.inmemory;

import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.ObjectNotFoundException;

import java.util.ArrayList;
import java.util.List;

public class RepositoryTest extends BaseTest
{

   private CmisTypeDefinitionType additionalType;

   private CmisTypeDefinitionType additionalType1;

   public void setUp() throws Exception
   {
      super.setUp();
      additionalType = new CmisTypeDefinitionType();
      additionalType.setBaseId(EnumBaseObjectTypeIds.CMIS_FOLDER);
      additionalType.setControllableACL(false);
      additionalType.setControllablePolicy(false);
      additionalType.setCreatable(true);
      additionalType.setDescription("addition type test");
      additionalType.setDisplayName("cmis:ext-folder");
      additionalType.setFileable(true);
      additionalType.setFulltextIndexed(false);
      additionalType.setId("cmis:ext-folder");
      additionalType.setIncludedInSupertypeQuery(false);
      additionalType.setLocalName("cmis:ext-folder");
      additionalType.setParentId("cmis:folder");
      additionalType.setQueryable(false);
      additionalType.setQueryName("cmis:ext-folder");

      CmisPropertyStringDefinitionType pd = new CmisPropertyStringDefinitionType();
      pd.setCardinality(EnumCardinality.SINGLE);
      pd.setDisplayName("cmis:hello");
      pd.setId("cmis:hello");
      pd.setInherited(false);
      pd.setPropertyType(EnumPropertyType.STRING);
      additionalType.getPropertyDefinition().add(pd);

      additionalType1 = new CmisTypeDefinitionType();
      additionalType1.setBaseId(EnumBaseObjectTypeIds.CMIS_FOLDER);
      additionalType1.setControllableACL(false);
      additionalType1.setControllablePolicy(false);
      additionalType1.setCreatable(true);
      additionalType1.setDescription("addition type test1");
      additionalType1.setDisplayName("cmis:ext-folder1");
      additionalType1.setFileable(true);
      additionalType1.setFulltextIndexed(false);
      additionalType1.setId("cmis:ext-folder1");
      additionalType1.setIncludedInSupertypeQuery(false);
      additionalType1.setLocalName("cmis:ext-folder1");
      additionalType1.setParentId("cmis:ext-folder");
      additionalType1.setQueryable(false);
      additionalType1.setQueryName("cmis:ext-folder1");
   }

   public void testGetDocumentType() throws Exception
   {
      CmisTypeDefinitionType docType = repository.getTypeDefinition("cmis:document", true);
      assertEquals("cmis:document", docType.getId());
      String[] expected =
         new String[]{"cmis:baseTypeId", "cmis:objectTypeId", "cmis:objectId", "cmis:name", "cmis:createdBy",
            "cmis:creationDate", "cmis:lastModifiedBy", "cmis:lastModificationDate", "cmis:changeToken",
            "cmis:isImmutable", "cmis:isLatestVersion", "cmis:isMajorVersion", "cmis:isLatestMajorVersion",
            "cmis:versionLabel", "cmis:versionSeriesId", "cmis:isVersionSeriesCheckedOut",
            "cmis:versionSeriesCheckedOutBy", "cmis:versionSeriesCheckedOutId", "cmis:checkinComment",
            "cmis:contentStreamLength", "cmis:contentStreamMimeType", "cmis:contentStreamFileName",
            "cmis:contentStreamId"};
      checkPropertyDefinitions(docType, expected);
   }

   public void testGetFolderType() throws Exception
   {
      CmisTypeDefinitionType folderType = repository.getTypeDefinition("cmis:folder", true);
      assertEquals("cmis:folder", folderType.getId());
      String[] expected =
         new String[]{"cmis:baseTypeId", "cmis:objectTypeId", "cmis:objectId", "cmis:name", "cmis:createdBy",
            "cmis:creationDate", "cmis:lastModifiedBy", "cmis:lastModificationDate", "cmis:changeToken",
            "cmis:parentId", "cmis:allowedChildObjectTypeIds", "cmis:path"};
      checkPropertyDefinitions(folderType, expected);
   }

   public void testGetPolicyType() throws Exception
   {
      CmisTypeDefinitionType policyType = repository.getTypeDefinition("cmis:policy", true);
      assertEquals("cmis:policy", policyType.getId());
      String[] expected =
         new String[]{"cmis:baseTypeId", "cmis:objectTypeId", "cmis:objectId", "cmis:name", "cmis:createdBy",
            "cmis:creationDate", "cmis:lastModifiedBy", "cmis:lastModificationDate", "cmis:changeToken",
            "cmis:policyText"};
      checkPropertyDefinitions(policyType, expected);
   }

   public void testGetRelationshipType() throws Exception
   {
      CmisTypeDefinitionType policyType = repository.getTypeDefinition("cmis:relationship", true);
      assertEquals("cmis:relationship", policyType.getId());
      String[] expected =
         new String[]{"cmis:baseTypeId", "cmis:objectTypeId", "cmis:objectId", "cmis:name", "cmis:createdBy",
            "cmis:creationDate", "cmis:lastModifiedBy", "cmis:lastModificationDate", "cmis:changeToken",
            "cmis:sourceId", "cmis:targetId"};
      checkPropertyDefinitions(policyType, expected);
   }

   public void testGetTypeChildren() throws Exception
   {
      ItemsIterator<CmisTypeDefinitionType> typeChildren = repository.getTypeChildren(null, false);
      List<CmisTypeDefinitionType> l = new ArrayList<CmisTypeDefinitionType>();
      while (typeChildren.hasNext())
         l.add(typeChildren.next());
      assertEquals(4, l.size()); // Base types.
      // Should be without property definitions.
      assertEquals(0, l.get(0).getPropertyDefinition().size());
      assertEquals(0, l.get(1).getPropertyDefinition().size());
      assertEquals(0, l.get(2).getPropertyDefinition().size());
      assertEquals(0, l.get(3).getPropertyDefinition().size());

      repository.addType(additionalType);

      typeChildren = repository.getTypeChildren("cmis:folder", true);
      l.clear();
      while (typeChildren.hasNext())
         l.add(typeChildren.next());
      assertEquals(1, l.size()); // Types that extends cmis:folder.
      assertEquals("cmis:ext-folder", l.get(0).getId());
   }

   public void testGetTypeDescendants() throws Exception
   {
      repository.addType(additionalType);
      repository.addType(additionalType1);
      List<CmisTypeContainer> l = repository.getTypeDescendants("cmis:folder", -1, false);
      assertEquals(additionalType.getId(), l.get(0).getType().getId());
      List<CmisTypeContainer> children = l.get(0).getChildren();
      assertEquals(1, children.size());
      assertEquals(additionalType1.getId(), children.get(0).getType().getId());
   }

   public void testCopy() throws Exception
   {
      Entry doc =
         repository.getRootFolder().createChild(repository.getTypeDefinition("cmis:document"), "doc1",
            EnumVersioningState.MAJOR);
      doc.setContent(new SimpleContentStream("test".getBytes(), "doc1", "text/plain"));

      Entry folder =
         repository.getRootFolder().createChild(repository.getTypeDefinition("cmis:folder"), "folder1", null);

      assertEquals(0, folder.getChildren().size());
      Entry copy = repository.copyObject(doc.getObjectId(), folder.getObjectId(), EnumVersioningState.MAJOR);
      assertFalse("Copy must have different id", doc.equals(copy));
      // Content copied.
      assertEquals("test", new String(((SimpleContentStream)copy.getContent(null)).getData()));
      assertEquals(1, folder.getChildren().size());
      assertEquals(copy.getObjectId(), folder.getChildren().next().getObjectId());
      assertEquals(folder.getObjectId(), copy.getParents().get(0).getObjectId());
   }

   public void testGetObjectByPath() throws Exception
   {
      Entry level1 = repository.getRootFolder().createChild(repository.getTypeDefinition("cmis:folder"), "1", null);
      Entry level2 = level1.createChild(repository.getTypeDefinition("cmis:folder"), "2", null);
      Entry level3 = level2.createChild(repository.getTypeDefinition("cmis:folder"), "3", null);
      Entry level4 = level3.createChild(repository.getTypeDefinition("cmis:folder"), "4", null);

      level1.createChild(repository.getTypeDefinition("cmis:folder"), "5", null) //
         .createChild(repository.getTypeDefinition("cmis:folder"), "6", null);
      Entry doc1 = level2.createChild(repository.getTypeDefinition("cmis:document"), "doc1", null);
      Entry doc2 = level2.createChild(repository.getTypeDefinition("cmis:document"), "doc2", null);
      Entry doc3 = level2.createChild(repository.getTypeDefinition("cmis:document"), "doc3", null);
      Entry folder1 = level3.createChild(repository.getTypeDefinition("cmis:folder"), "7", null);
      Entry doc4 = level4.createChild(repository.getTypeDefinition("cmis:document"), "doc4", EnumVersioningState.MAJOR);

      //      /
      //      |_ 1 
      //        |_2
      //        | |_doc1
      //        | |_doc2
      //        | |_doc3
      //        | |_3
      //        |   |_4
      //        |   | |_doc4
      //        |   |_7 
      //        |_5 
      //          |_6
      assertEquals(doc4, repository.getObjectByPath("/1/2/3/4/doc4"));
      assertEquals(level1, repository.getObjectByPath("/1"));
      assertEquals(doc1, repository.getObjectByPath("/1/2/doc1"));
      assertEquals(doc2, repository.getObjectByPath("/1/2/doc2"));
      assertEquals(doc3, repository.getObjectByPath("/1/2/doc3"));
      assertEquals(folder1, repository.getObjectByPath("/1/2/3/7"));
      try
      {
         repository.getObjectByPath("/1/2/3/3");
         fail("ObjectNotFoundException should be thrown.");
      }
      catch (ObjectNotFoundException e)
      {
         // OK
      }
   }

   private void checkPropertyDefinitions(CmisTypeDefinitionType type, String[] expected)
   {
      List<String> pId = new ArrayList<String>();
      for (CmisPropertyDefinitionType pd : type.getPropertyDefinition())
         pId.add(pd.getId());
      for (String id : expected)
         assertTrue("Not found expected property definition '" + id + "' for type '" + type.getId() + "'", //
            pId.contains(id));
   }

}
