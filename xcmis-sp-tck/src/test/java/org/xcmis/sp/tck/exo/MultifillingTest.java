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

import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.DocumentData;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsList;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.CmisObject;

import java.util.List;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id:  $
 */
public class MultifillingTest extends BaseTest
{

   @Override
   public void setUp() throws Exception
   {
      super.setUp();
   }

   public void testAddObjectToFolder() throws Exception
   {
      FolderData folder1 = createFolder(rootFolder, "testFolder1");
      FolderData folder2 = createFolder(rootFolder, "testFolder2");
      DocumentData doc1 = createDocument(folder1, "doc1", "doc1");

      ItemsList<CmisObject> children0 =
         getConnection().getChildren(folder2.getObjectId(), false, null, false, true, null, null, null, -1, 0);
      assertNotNull(children0);
      assertNotNull(children0.getItems());
      assertEquals("Should be no documents here", 0, children0.getItems().size());

      getConnection().addObjectToFolder(doc1.getObjectId(), folder2.getObjectId(), true);

      ItemsList<CmisObject> children =
         getConnection().getChildren(folder2.getObjectId(), false, null, false, true, null, null, null, -1, 0);
      assertNotNull(children);
      assertNotNull(children.getItems());
      List<CmisObject> listChildren = children.getItems();
      assertEquals("Should be a one document here, which was added as addObjectToFolder", 1, children.getItems().size());
      for (CmisObject cmisObject : listChildren)
      {
         assertNotNull(cmisObject);
         assertNotNull(cmisObject.getObjectInfo());
         assertNotNull(cmisObject.getObjectInfo().getId());
         assertEquals(doc1.getObjectId(), cmisObject.getObjectInfo().getId());
      }
   }

   public void testRemoveObjectFromFolder() throws Exception
   {

      FolderData folder1 = createFolder(rootFolder, "testFolder1");
      FolderData folder2 = createFolder(rootFolder, "testFolder2");
      DocumentData doc1 = createDocument(folder1, "doc1", "doc1");

      ItemsList<CmisObject> children0 =
         getConnection().getChildren(folder2.getObjectId(), false, null, false, true, null, null, null, -1, 0);
      assertNotNull(children0);
      assertNotNull(children0.getItems());
      assertEquals("Should be no documents here", 0, children0.getItems().size());

      getConnection().addObjectToFolder(doc1.getObjectId(), folder2.getObjectId(), true);

      ItemsList<CmisObject> children =
         getConnection().getChildren(folder2.getObjectId(), false, null, false, true, null, null, null, -1, 0);
      assertNotNull(children);
      assertNotNull(children.getItems());
      List<CmisObject> listChildren = children.getItems();
      assertEquals("Should be a one document here, which was added as addObjectToFolder", 1, children.getItems().size());
      for (CmisObject cmisObject : listChildren)
      {
         assertNotNull(cmisObject);
         assertNotNull(cmisObject.getObjectInfo());
         assertNotNull(cmisObject.getObjectInfo().getId());
         assertEquals(doc1.getObjectId(), cmisObject.getObjectInfo().getId());
      }

      getConnection().removeObjectFromFolder(doc1.getObjectId(), folder2.getObjectId());

      ItemsList<CmisObject> children00 =
         getConnection().getChildren(folder2.getObjectId(), false, null, false, true, null, null, null, -1, 0);
      assertNotNull(children00);
      assertNotNull(children00.getItems());
      assertEquals("Should be no documents here", 0, children00.getItems().size());
   }

   /**
    * @see org.xcmis.sp.tck.exo.BaseTest#tearDown()
    */
   @Override
   protected void tearDown() throws Exception
   {
      ItemsList<CmisObject> children =
         getConnection().getChildren(rootfolderID, false, null, false, true, null, null, null, -1, 0);
      if (children != null && children.getItems() != null)
      {
         List<CmisObject> listChildren = children.getItems();
         for (CmisObject cmisObject : listChildren)
         {
            remove(cmisObject);
         }
      }
      super.tearDown();
   }

   /**
    * @param cmisObject
    * @throws StorageException 
    * @throws VersioningException 
    * @throws UpdateConflictException 
    * @throws ConstraintException 
    * @throws ObjectNotFoundException 
    * @throws FilterNotValidException 
    * @throws InvalidArgumentException 
    */
   private void remove(CmisObject cmisObject) throws ObjectNotFoundException, ConstraintException,
      UpdateConflictException, VersioningException, StorageException, InvalidArgumentException, FilterNotValidException
   {

      if (cmisObject.getObjectInfo().getBaseType().equals(BaseType.FOLDER))
      {
         ItemsList<CmisObject> children =
            getConnection().getChildren(cmisObject.getObjectInfo().getId(), false, null, false, true, null, null, null,
               -1, 0);
         if (children != null && children.getItems() != null)
         {
            List<CmisObject> listChildren = children.getItems();
            for (CmisObject cmisObject0 : listChildren)
            {
               remove(cmisObject0);
            }
         }
      }
      getConnection().deleteObject(cmisObject.getObjectInfo().getId(), true);
   }

}
