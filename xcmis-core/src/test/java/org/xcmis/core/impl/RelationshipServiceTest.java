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

import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumRelationshipDirection;
import org.xcmis.core.RelationshipService;
import org.xcmis.spi.object.CmisObjectList;
import org.xcmis.spi.object.Entry;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RelationshipServiceTest.java 1989 2009-07-07 04:37:53Z
 *          andrew00x $
 */
public class RelationshipServiceTest extends BaseTest
{

   private RelationshipService relationshipService;

   public void setUp() throws Exception
   {
      super.setUp();
      relationshipService = new RelationshipServiceImpl(repositoryService, propertyService);
   }

   public void testGetRelationships() throws Exception
   {
      Entry doc1 = createDocument(testFolder, "doc1", null);
      Entry doc2 = createDocument(testFolder, "doc2", null);

      CmisTypeDefinitionType type = repository.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value());
      doc1.addRelationship("relationship1", doc1, type);
      doc1.addRelationship("relationship2", doc2, type);
      doc2.addRelationship("relationship3", doc1, type);
      doc2.addRelationship("relationship4", doc2, type);
      EnumRelationshipDirection direction = EnumRelationshipDirection.EITHER;
      CmisObjectList relationships =
         relationshipService.getObjectRelationships(repositoryId, doc1.getObjectId(), direction, null, true, false,
            null, 10, 0, false);
      assertEquals(3, relationships.getObjects().size());
   }

}
