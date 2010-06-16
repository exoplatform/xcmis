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

import org.xcmis.spi.ItemsList;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.model.TypeDefinition;

import java.util.List;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id:  $
 */
public class StorageTest extends BaseTest
{

   @Override
   public void setUp() throws Exception
   {
      super.setUp();
   }

   public void testGeneral() throws Exception
   {
      assertNotNull(storageProvider);
      assertNotNull(storageProvider.getConnection());
      assertNotNull(storageProvider.getConnection().getStorage());
      assertNotNull(storageProvider.getConnection().getStorage().getId());
   }

   public void testRepositoryInfo() throws Exception
   {
      assertNotNull(getStorage().getRepositoryInfo());
      assertNotNull(getStorage().getRepositoryInfo().getRepositoryId());
      assertNotNull(getStorage().getRepositoryInfo().getRootFolderId());
      assertNotNull(getStorage().getRepositoryInfo().getCapabilities());
   }

   public void testGetTypeDescendants()
   {
      ItemsList<TypeDefinition> typeChildren = null;
      try
      {
         typeChildren = getConnection().getTypeChildren("cmis:folder", true, -1, 0);
      }
      catch (TypeNotFoundException e)
      {
         e.printStackTrace();
      }
      assertNotNull(typeChildren);
      assertFalse(typeChildren.isHasMoreItems());
      
      ItemsList<TypeDefinition> typeChildren2 = null;
      try
      {
         typeChildren2 = getConnection().getTypeChildren("cmis:document", true, -1, 0);
      }
      catch (TypeNotFoundException e)
      {
         e.printStackTrace();
      }
      assertNotNull(typeChildren2);
      assertFalse(typeChildren2.isHasMoreItems());
   }

}
