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
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.Document;
import org.xcmis.spi.Folder;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.Policy;
import org.xcmis.spi.Relationship;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.model.impl.StringProperty;

import java.util.HashMap;
import java.util.Properties;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: BaseTest.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public abstract class BaseTest extends TestCase
{

   protected StorageImpl storage;

   protected Folder rootFolder;

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
      rootFolder = (Folder)storage.getObject(storage.getRepositoryInfo().getRootFolderId());
   }

   protected Folder createFolder(Folder parent, String name, String type)
   {
      Folder folder = storage.createFolder(parent, type);
      folder.setName(name);
      storage.saveObject(folder);
      return folder;
   }

   protected Document createDocument(Folder parent, String name, String type, ContentStream content,
      VersioningState versioningState)
   {
      Document doc = storage.createDocument(parent, type, versioningState);
      doc.setName(name);
      doc.setContentStream(content);
      storage.saveObject(doc);
      return doc;
   }

   protected Policy createPolicy(String name, String type, String policyText)
   {
      Policy policy = storage.createPolicy(null, type);
      policy.setName(name);

      PropertyDefinition<?> defPolicyText = PropertyDefinitions.getPropertyDefinition("cmis:policy", CMIS.POLICY_TEXT);
      policy.setProperty(new StringProperty(defPolicyText.getId(), defPolicyText.getQueryName(), defPolicyText
         .getLocalName(), defPolicyText.getDisplayName(), policyText));

      storage.saveObject(policy);
      return policy;
   }

   public Relationship createRelationship(String name, ObjectData source, ObjectData target, String typeId)
   {
      Relationship relationship = storage.createRelationship(source, target, typeId);
      relationship.setName(name);
      storage.saveObject(relationship);
      return relationship;
   }
}
