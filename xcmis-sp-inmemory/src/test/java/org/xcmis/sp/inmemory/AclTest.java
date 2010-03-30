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

import org.xcmis.core.CmisAccessControlEntryType;
import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.CmisAccessControlPrincipalType;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisPropertyId;
import org.xcmis.core.CmisPropertyString;
import org.xcmis.core.EnumBasicPermissions;
import org.xcmis.core.EnumVersioningState;
import org.xcmis.spi.CMIS;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class AclTest extends BaseTest
{

   private String objectId;

   public void setUp() throws Exception
   {
      super.setUp();
      CmisPropertiesType properties = new CmisPropertiesType();
      CmisPropertyId typeId = new CmisPropertyId();
      typeId.setPropertyDefinitionId(CMIS.OBJECT_TYPE_ID);
      typeId.getValue().add("cmis:document");

      CmisPropertyString name = new CmisPropertyString();
      name.setPropertyDefinitionId(CMIS.NAME);
      name.getValue().add("createDocumentTest");

      properties.getProperty().add(typeId);
      properties.getProperty().add(name);

      CmisObjectType document =
         connection.createDocument(connection.getStorageInfo().getRootFolderId(), properties, null, null, null, null,
            EnumVersioningState.MAJOR);

      objectId = getId(document);
   }

   public void testAddACL() throws Exception
   {
      CmisAccessControlListType aces = new CmisAccessControlListType();
      CmisAccessControlPrincipalType principal = new CmisAccessControlPrincipalType();
      principal.setPrincipalId("root");
      CmisAccessControlEntryType ace = new CmisAccessControlEntryType();
      ace.getPermission().add(EnumBasicPermissions.CMIS_ALL.value());
      ace.setPrincipal(principal);
      aces.getPermission().add(ace);

      assertNull(connection.getAcl(objectId, false));
      connection.applyAcl(objectId, aces, null, connection.getStorageInfo().getAclCapability().getPropagation());
      CmisAccessControlListType acl = connection.getAcl(objectId, false);
      assertNotNull(acl);
      assertEquals(1, acl.getPermission().size());
   }

}
