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

import org.xcmis.core.MultifilingService;
import org.xcmis.core.impl.MultifilingServiceImpl;
import org.xcmis.spi.NotSupportedException;
import org.xcmis.spi.object.Entry;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: MultifilingServiceTest.java 2178 2009-07-16 17:02:52Z andrew00x
 *          $
 */
public class MultifilingServiceTest extends BaseTest
{

   private MultifilingService multifilingService;

   public void setUp() throws Exception
   {
      super.setUp();
      multifilingService = new MultifilingServiceImpl(repositoryService, propertyService);

   }


   public void testAddObjectToFolder2() throws Exception
   {
      Entry doc1 = createDocument(null, "doc1", null);
      String id1 = doc1.getObjectId();

      assertEquals(0, doc1.getParents().size());
      assertEquals(0, testFolder.getChildren().size());
      try
      {
         multifilingService.addObjectToFolder(repositoryId, id1, testFolderId, true);
      }
      catch (NotSupportedException e)
      {
         fail("Multiling/unfilling is not supported");
      }
      // get updated objects.
      doc1 = repository.getObjectById(id1);
      assertEquals(1, doc1.getParents().size());
      assertEquals(1, testFolder.getChildren().size());
   }

   public void testRemoveObjectFromFolder() throws Exception
   {
      Entry doc1 = createDocument(testFolder, "doc1", null);
      String id1 = doc1.getObjectId();
      assertEquals(1, doc1.getParents().size());
      assertEquals(1, testFolder.getChildren().size());
      try
      {
         multifilingService.removeObjectFromFolder(repositoryId, id1, testFolderId);
      }
      catch (NotSupportedException e)
      {
         fail("Multiling/unfilling is not supported");
      }
      // get updated objects.
      doc1 = repository.getObjectById(id1);
      assertEquals(0, doc1.getParents().size());
      assertEquals(0, testFolder.getChildren().size());
   }
}
