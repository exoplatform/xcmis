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

import junit.framework.TestCase;

import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.DocumentData;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.PermissionService;
import org.xcmis.spi.PolicyData;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.RenditionManager;
import org.xcmis.spi.UserContext;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.model.impl.StringProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: BaseTest.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public abstract class BaseTest extends TestCase
{

   protected StorageImpl storage;

   protected FolderData rootFolder;

   protected final String storageId = "inmem1";

   protected TypeDefinition documentTypeDefinition;

   protected String principal = "root";

   public void setUp() throws Exception
   {
      super.setUp();
      HashMap<String, Object> properties = new HashMap<String, Object>();
      properties.put("exo.cmis.changetoken.feature", false);
      StorageConfiguration configuration = new StorageConfiguration(storageId, storageId, null, -1, -1);

      storage = new StorageImpl(configuration, RenditionManager.getInstance(), new PermissionService());
      UserContext.setCurrent(new UserContext(principal));
      rootFolder = (FolderData)storage.getObjectById(storage.getRepositoryInfo().getRootFolderId());

      documentTypeDefinition = storage.getTypeDefinition("cmis:document", true);
   }

   protected FolderData createFolder(FolderData parent, String name) throws Exception
   {
      PropertyDefinition<?> def = PropertyDefinitions.getPropertyDefinition("cmis:folder", CmisConstants.NAME);
      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME,
         new StringProperty(def.getId(), def.getQueryName(), def.getLocalName(), def.getDisplayName(), name));

      TypeDefinition typeDefinition = storage.getTypeDefinition("cmis:folder", true);

      FolderData folder = storage.createFolder(parent, typeDefinition, properties, null, null);
      return folder;
   }

   protected DocumentData createDocument(FolderData parent, String name, TypeDefinition typeDefinition,
      ContentStream content, VersioningState versioningState) throws Exception
   {

      PropertyDefinition<?> def = PropertyDefinitions.getPropertyDefinition("cmis:document", CmisConstants.NAME);
      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME,
         new StringProperty(def.getId(), def.getQueryName(), def.getLocalName(), def.getDisplayName(), name));

      DocumentData doc =
         storage.createDocument(parent, typeDefinition, properties, content, null, null, versioningState);
      return doc;
   }

   protected PolicyData createPolicy(String name, TypeDefinition typeDefinition, String policyText) throws Exception
   {
      PropertyDefinition<?> def = PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);
      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME,
         new StringProperty(def.getId(), def.getQueryName(), def.getLocalName(), def.getDisplayName(), name));
      properties.put(CmisConstants.POLICY_TEXT, new StringProperty(def.getId(), def.getQueryName(), def.getLocalName(),
         def.getDisplayName(), policyText));
      PolicyData policy = storage.createPolicy(null, typeDefinition, properties, null, null);
      return policy;
   }

   public RelationshipData createRelationship(String name, ObjectData source, ObjectData target,
      TypeDefinition typeDefinition) throws Exception
   {
      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      PropertyDefinition<?> defName =
         PropertyDefinitions.getPropertyDefinition("cmis:relationship", CmisConstants.NAME);
      properties.put(CmisConstants.NAME,
         new StringProperty(defName.getId(), defName.getQueryName(), defName.getLocalName(), defName.getDisplayName(),
            name));

      RelationshipData relationship =
         storage.createRelationship(source, target, typeDefinition, properties, null, null);
      return relationship;
   }
}
