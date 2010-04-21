/*
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

import org.xcmis.spi.DocumentData;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.ObjectData;

import java.util.Collection;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@exoplatform.org">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z ksm $
 *
 */
public class StorageTest extends BaseTest
{
   public void testUnfiling() throws Exception
   {
      assertEquals(0, getSize(storage.getUnfiledObjects()));
      DocumentData document = createDocument(rootFolder, "unfilingDocumentTest", "cmis:document", null, null);
      assertTrue(rootFolder.getChildren(null).hasNext());
      rootFolder.removeObject(document);
      assertFalse(rootFolder.getChildren(null).hasNext());

      Collection<FolderData> parents = document.getParents();
      assertEquals(0, parents.size());
      storage.getObject(document.getObjectId());

      assertEquals(1, getSize(storage.getUnfiledObjects()));
   }

   private int getSize(ItemsIterator<ObjectData> iterator)
   {
      int result = 0;

      while (iterator.hasNext())
      {
         iterator.next();
         result++;
      }
      return result;
   }
}
