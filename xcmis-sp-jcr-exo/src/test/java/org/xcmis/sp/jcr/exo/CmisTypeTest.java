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
package org.xcmis.sp.jcr.exo;

import org.xcmis.spi.FolderData;
import org.xcmis.spi.Storage;
import org.xcmis.spi.model.PropertyType;
import org.xcmis.spi.model.TypeDefinition;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@exoplatform.org">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z ksm $
 *
 */
public class CmisTypeTest extends BaseTest
{
   protected Storage storage;

   protected FolderData rootFolder;

   @Override
   public void setUp() throws Exception
   {
      super.setUp();
      storage = storageProvider.getConnection(cmisRepositoryId).getStorage();
      rootFolder = (FolderData)storage.getObjectById(JcrCMIS.ROOT_FOLDER_ID);
   }

   public void testGetTypeRegisteredThrowXml() throws Exception
   {
      TypeDefinition td = storage.getTypeDefinition("cmis:type-test", true);
      assertPropertyType(PropertyType.BOOLEAN, td);
      assertPropertyType(PropertyType.DATETIME, td);
      assertPropertyType(PropertyType.DECIMAL, td);
      assertPropertyType(PropertyType.STRING, td);
      assertPropertyType(PropertyType.DATETIME, td);
      assertPropertyType(PropertyType.HTML, td);
      assertPropertyType(PropertyType.ID, td);
      assertPropertyType(PropertyType.URI, td);

   }

   private void assertPropertyType(PropertyType type, TypeDefinition typeDefinition)
   {
      assertEquals(type, typeDefinition.getPropertyDefinition("cmis:" + type.toString() + "-type").getPropertyType());
   }
}
