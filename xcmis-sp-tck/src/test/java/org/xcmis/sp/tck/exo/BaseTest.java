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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.UnfileObject;
import org.xcmis.spi.model.Updatability;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.model.impl.StringProperty;
import org.xcmis.spi.utils.MimeType;
import org.xcmis.spi.BaseContentStream;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.Connection;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.DocumentData;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.Storage;
import org.xcmis.spi.StorageProvider;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id:  $
 */
public abstract class BaseTest extends TestCase
{

   private static final Log LOG = ExoLogger.getLogger(BaseTest.class);

   protected StandaloneContainer container;

   protected StorageProvider storageProvider;
   
   protected TypeDefinition documentTypeDefinition;

   protected TypeDefinition folderTypeDefinition;

   protected TypeDefinition policyTypeDefinition;

   protected TypeDefinition relationshipTypeDefinition;
   
   protected String rootfolderID;
   
   protected String testroot;
   
   protected List<String> passedTests;
   protected List<String> failedTests;
   

   @Override
   public void setUp() throws Exception
   {
      String containerConf = getClass().getResource("/conf/standalone/test-jcr-sp-configuration.xml").toString();
      StandaloneContainer.addConfigurationURL(containerConf);
      container = StandaloneContainer.getInstance();

      storageProvider = (StorageProvider)container.getComponentInstanceOfType(StorageProvider.class);
      
      rootfolderID = getStorage().getRepositoryInfo().getRootFolderId();
      documentTypeDefinition = getStorage().getTypeDefinition("cmis:document", true);
      folderTypeDefinition = getStorage().getTypeDefinition("cmis:folder", true);
      policyTypeDefinition = getStorage().getTypeDefinition("cmis:policy", true);
      relationshipTypeDefinition = getStorage().getTypeDefinition("cmis:relationship", true);
   }

   protected Connection getConnection()
   {
      return storageProvider.getConnection();
   }

   protected Storage getStorage()
   {
      return storageProvider.getConnection().getStorage();
   }
   
   /**  
    *   root
    *     |
    *   testroot
    *     |
    *     |-------------------------------------------------------------------
    *         |                              |                      |                       |           
    *     Folder1               Folder2 -<rel>- Doc1-<rel>--Doc2      Doc5   Doc6
    *                                     |
    *                                      ------------------
    *                                         |                      |                
    *                                      Folder3          Doc3          
    *                                          |                     |
    *                                        Doc4 --<rel>-  
    */   
   
   protected void  createFolderTree() throws Exception
   {
      FolderData rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);
      ContentStream cs =
         new BaseContentStream("1234567890".getBytes(),null, new MimeType("text", "plain"));
      
      FolderData testroot = getStorage().createFolder(rootFolder, folderTypeDefinition, getPropsMap("cmis:folder", "testroot"), null, null);
      
      FolderData folder1 = getStorage().createFolder(testroot, folderTypeDefinition, getPropsMap("cmis:folder", "folder1"), null, null);

      DocumentData doc1 = getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc1"), cs, null, null, VersioningState.MAJOR);
      
      DocumentData doc2 =getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc2"), cs, null, null, VersioningState.MAJOR);
      doc2.checkout();

      FolderData folder2 = getStorage().createFolder(testroot, folderTypeDefinition, getPropsMap("cmis:folder", "folder2"), null, null);

      DocumentData doc3 = getStorage().createDocument(folder2, documentTypeDefinition, getPropsMap("cmis:document", "doc3"), cs, null, null, VersioningState.MAJOR);

      FolderData folder3 = getStorage().createFolder(folder2, folderTypeDefinition, getPropsMap("cmis:folder", "folder3"), null, null);

      DocumentData doc4 = getStorage().createDocument(folder3, documentTypeDefinition, getPropsMap("cmis:document", "doc4"), cs, null, null, VersioningState.MAJOR);
      
      DocumentData doc5 =getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc5"), cs, null, null, VersioningState.MAJOR);
      doc5.checkout();
      
      DocumentData doc6 =getStorage().createDocument(testroot, documentTypeDefinition, getPropsMap("cmis:document", "doc6"), cs, null, null, VersioningState.MAJOR);
      doc6.checkout();

      RelationshipData rel1    = getStorage().createRelationship(doc3, doc4, relationshipTypeDefinition, getPropsMap("cmis:relationship", "rel1"), null, null);

      RelationshipData rel2    = getStorage().createRelationship(doc1, doc2, relationshipTypeDefinition, getPropsMap("cmis:relationship", "rel2"), null, null);
      
      RelationshipData rel3    = getStorage().createRelationship(folder2, doc1, relationshipTypeDefinition, getPropsMap("cmis:relationship", "rel3"), null, null);
      
      this.testroot = testroot.getObjectId();
   }

   protected void clear(){
      try {
      FolderData rootFolder = (FolderData)getStorage().getObjectById(testroot);
      getStorage().deleteTree(rootFolder, true, UnfileObject.DELETE, true);
      } catch (Exception e){
         e.printStackTrace();
      }
   }
   
   private Map<String, Property<?>>  getPropsMap(String baseType, String name){
      org.xcmis.spi.model.PropertyDefinition<?> def = PropertyDefinitions.getPropertyDefinition(baseType, CmisConstants.NAME);
      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(def.getId(), def.getQueryName(), def.getLocalName(), def
         .getDisplayName(), name));
      return properties;
   }
   
}
