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

import junit.framework.TestCase;

import org.exoplatform.container.StandaloneContainer;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.RepositoryService;
import org.xcmis.core.impl.RepositoryServiceImpl;
import org.xcmis.core.impl.property.PropertyService;
import org.xcmis.spi.RepositoriesManager;
import org.xcmis.spi.Repository;
import org.xcmis.spi.object.ContentStream;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.ItemsIterator;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public abstract class BaseTest extends TestCase
{

   protected String repositoryId = "cmis1";

   protected RepositoryService repositoryService;

   protected PropertyService propertyService;

   protected Repository repository;

   protected Entry testFolder;

   protected String testFolderId;

   protected StandaloneContainer container;

   public void setUp() throws Exception
   {
      super.setUp();

      String config = System.getProperty("cmis.core.test.configuration.file");
      if (config == null)
         config = "/conf/standalone/test-configuration.xml";
      String containerConf = getClass().getResource(config).toString();
      String loginConf = Thread.currentThread().getContextClassLoader().getResource("login.conf").toString();
      StandaloneContainer.addConfigurationURL(containerConf);
      container = StandaloneContainer.getInstance();

      if (System.getProperty("java.security.auth.login.config") == null)
         System.setProperty("java.security.auth.login.config", loginConf);

      RepositoriesManager manager =
         (RepositoriesManager)container.getComponentInstanceOfType(RepositoriesManager.class);
      repositoryService = new RepositoryServiceImpl(manager);

      propertyService = (PropertyService)container.getComponentInstanceOfType(PropertyService.class);

      repository = manager.getRepository(repositoryId);

      Entry rootFolder = repository.getRootFolder();
      testFolder = createFolder(rootFolder, "testFolder");
      testFolderId = testFolder.getObjectId();
   }

   public void tearDown() throws Exception
   {
      for (ItemsIterator<Entry> iter = repository.getCheckedOutDocuments(null); iter.hasNext();)
         iter.next().delete();
      testFolder.delete();
   }

   protected Entry createDocument(Entry parent, String name, ContentStream cs) throws Exception
   {
      CmisTypeDefinitionType type = repository.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value());
      Entry doc;
      if (parent == null)
         doc = repository.createObject(type, null);
      else
         doc = parent.createChild(type, name, null);
      doc.setContent(cs);
      doc.save();
      return doc;
   }

   protected Entry createFolder(Entry parent, String name) throws Exception
   {
      CmisTypeDefinitionType type = repository.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_FOLDER.value());
      Entry folder = parent.createChild(type, name, null);
      folder.save();
      return folder;
   }

   protected Entry createPolicy(Entry parent, String name) throws Exception
   {
      CmisTypeDefinitionType type = repository.getTypeDefinition(EnumBaseObjectTypeIds.CMIS_POLICY.value());
      Entry policy = parent.createChild(type, name, null);
      policy.save();
      return policy;
   }

}
