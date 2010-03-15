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

package org.xcmis.core.impl;

import org.xcmis.core.CmisPermissionMapping;
import org.xcmis.core.CmisPropertyStringDefinitionType;
import org.xcmis.core.CmisRepositoryInfoType;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumCardinality;
import org.xcmis.core.EnumPropertyType;
import org.xcmis.messaging.CmisRepositoryEntryType;
import org.xcmis.messaging.CmisTypeContainer;
import org.xcmis.messaging.CmisTypeDefinitionListType;
import org.xcmis.spi.TypeNotFoundException;

import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RepositoryServiceTest.java 2154 2009-07-15 16:21:24Z andrew00x
 *          $
 */
public class RepositoryServiceTest extends BaseTest
{

   protected CmisTypeDefinitionType additionalType;

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
   }

   public void testGetRepositories() throws Exception
   {
      List<CmisRepositoryEntryType> l = repositoryService.getRepositories();
      assertEquals(1, l.size());
   }

   public void testRepositoryInfo() throws Exception
   {
      CmisRepositoryInfoType info = repositoryService.getRepositoryInfo(repositoryId);
      assertNotNull(info);
      assertEquals(repositoryId, info.getRepositoryId());
      for (CmisPermissionMapping m : info.getAclCapability().getMapping())
         System.out.println(m.getKey().value() + "\t" + m.getPermission());
   }

   public void testGetTypes() throws Exception
   {
      CmisTypeDefinitionListType types = repositoryService.getTypeChildren(repositoryId, null, true, 10, 0);
      assertTrue(types.getTypes().size() >= 4); // should at least 4 required types
   }

   public void testGetTypeDescendants() throws Exception
   {
      repository.addType(additionalType);
      List<CmisTypeContainer> res =
         repositoryService.getTypeDescendants(repositoryId, EnumBaseObjectTypeIds.CMIS_FOLDER.value(), -1, true);
      assertEquals(1, res.size());
      assertEquals(additionalType.getId(), res.get(0).getType().getId());
      assertEquals(0, res.get(0).getChildren().size());
      assertNotNull(res.get(0).getType().getPropertyDefinition());
   }

   public void tearDown() throws Exception
   {
      super.tearDown();
      try
      {
         repository.removeType(additionalType.getId());
      }
      catch (TypeNotFoundException e)
      {
      }
   }

}
