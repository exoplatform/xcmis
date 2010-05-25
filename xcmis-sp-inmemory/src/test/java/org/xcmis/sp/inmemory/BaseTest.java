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

import org.exoplatform.services.log.LogConfigurator;
import org.exoplatform.services.log.impl.Log4JConfigurator;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.DocumentData;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.PolicyData;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.model.impl.StringProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: BaseTest.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public abstract class BaseTest extends TestCase
{

   protected StorageImpl storage;

   protected FolderData rootFolder;

   protected final String storageId = "inmem1";

   public void setUp() throws Exception
   {
      super.setUp();
      LogConfigurator lc = new Log4JConfigurator();
      Properties props = new Properties();
      props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("conf/log4j.properties"));
      lc.configure(props);

      HashMap<String, Object> properties = new HashMap<String, Object>();
      properties.put("exo.cmis.changetoken.feature", false);
      StorageConfiguration configuration = new StorageConfiguration(storageId, properties);

      storage = new StorageImpl(configuration);
      rootFolder = (FolderData)storage.getObjectById(storage.getRepositoryInfo().getRootFolderId());
   }

   protected FolderData createFolder(FolderData parent, String name, String type)
   {
      PropertyDefinition<?> def = PropertyDefinitions.getPropertyDefinition("cmis:folder", CmisConstants.NAME);
      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(def.getId(), def.getQueryName(), def.getLocalName(), def
         .getDisplayName(), "createFolderTest"));
      
      FolderData folder = storage.createFolder(parent, type, properties, null, null, null);
      folder.setName(name);
      return folder;
   }

   protected DocumentData createDocument(FolderData parent, String name, String type, ContentStream content,
      VersioningState versioningState)
   {
      
      PropertyDefinition<?> def = PropertyDefinitions.getPropertyDefinition("cmis:document", CmisConstants.NAME);
      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(def.getId(), def.getQueryName(), def.getLocalName(), def
         .getDisplayName(), "createFolderTest"));
      
      DocumentData doc = storage.createDocument(parent, type,properties, null,null,null,null, versioningState);
      doc.setName(name);
      try {
      doc.setContentStream(content);
      } catch (Exception ex){
         fail();
      }
      return doc;
   }

   protected PolicyData createPolicy(String name, String type, String policyText)
   {
      
      PropertyDefinition<?> def = PropertyDefinitions.getPropertyDefinition("cmis:policy", CmisConstants.POLICY_TEXT);
      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(def.getId(), def.getQueryName(), def.getLocalName(), def
         .getDisplayName(), "createFolderTest"));
      
      PolicyData policy = storage.createPolicy(null, type, properties, null,null,null);
      policy.setName(name);

      return policy;
   }

   public RelationshipData createRelationship(String name, ObjectData source, ObjectData target, String typeId)
   {
      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      PropertyDefinition<?> defName =
         PropertyDefinitions.getPropertyDefinition("cmis:relationship", CmisConstants.NAME);
      properties.put(CmisConstants.NAME, new StringProperty(defName.getId(), defName.getQueryName(), defName
         .getLocalName(), defName.getDisplayName(), "createRelationshipTest"));
      
      RelationshipData relationship = storage.createRelationship(source, target, typeId, properties, null, null, null);
      relationship.setName(name);
      return relationship;
   }
}
