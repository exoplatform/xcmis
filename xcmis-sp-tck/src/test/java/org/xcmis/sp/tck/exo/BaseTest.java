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

import junit.framework.TestCase;

import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.spi.BaseContentStream;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.Connection;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.DocumentData;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.ItemsTree;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.PolicyData;
import org.xcmis.spi.PropertyFilter;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.RenditionFilter;
import org.xcmis.spi.Storage;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.StorageProvider;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.IncludeRelationships;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.RepositoryCapabilities;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.UnfileObject;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.model.impl.IdProperty;
import org.xcmis.spi.model.impl.StringProperty;
import org.xcmis.spi.utils.MimeType;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id:  $
 */
public abstract class BaseTest extends TestCase
{

   //private static final Log LOG = ExoLogger.getLogger(BaseTest.class);

   protected StandaloneContainer container;

   protected StorageProvider storageProvider;

   protected TypeDefinition documentTypeDefinition;

   protected TypeDefinition folderTypeDefinition;

   protected TypeDefinition policyTypeDefinition;

   protected TypeDefinition relationshipTypeDefinition;

   protected String rootfolderID;

   protected FolderData rootFolder;

   protected List<String> passedTests = new ArrayList<String>();

   protected List<String> failedTests = new ArrayList<String>();

   protected List<String> skippedTests = new ArrayList<String>();

   private Connection conn;

   private static final String TCK_CONF_DEFAULT = "/conf/sp_inmem_exo/test-inmem-sp-configuration.xml";

   @Override
   public void setUp() throws Exception
   {

      String propertyTckConf = System.getProperty("tck.conf");

      String tck_conf =
         propertyTckConf == null || propertyTckConf.length() == 0 || propertyTckConf.equalsIgnoreCase("${tck.conf}")
            ? TCK_CONF_DEFAULT : propertyTckConf;

      String containerConf = getClass().getResource(tck_conf).toString();
      StandaloneContainer.addConfigurationURL(containerConf);
      container = StandaloneContainer.getInstance();

      ConversationState state = new ConversationState(new Identity("__system"));
      ConversationState.setCurrent(state);

      storageProvider = (StorageProvider)container.getComponentInstanceOfType(StorageProvider.class);

      rootfolderID = getStorage().getRepositoryInfo().getRootFolderId();
      rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      documentTypeDefinition = getStorage().getTypeDefinition(CmisConstants.DOCUMENT, true);
      folderTypeDefinition = getStorage().getTypeDefinition(CmisConstants.FOLDER, true);
      policyTypeDefinition = getStorage().getTypeDefinition(CmisConstants.POLICY, true);
      relationshipTypeDefinition = getStorage().getTypeDefinition(CmisConstants.RELATIONSHIP, true);
   }

   protected void tearDown() throws Exception
   {
      if (conn != null)
         conn.close();
      super.tearDown();
   }

   protected Connection getConnection()
   {
      if (conn == null)
         conn = storageProvider.getConnection();
      return conn;
   }

   protected Storage getStorage()
   {
      return getConnection().getStorage();
   }

   /**  
    *  STRUCTURE:
    *  
    *   root
    *    - testroot
    *       |- Folder1
    *       |- Folder2
    *       |   |- Folder3
    *       |   |   |- Doc4
    *       |   |- Doc3
    *       |- Doc1
    *       |- Doc2
    *       |- Doc5
    *       |- Doc6
    *       
    *   Rel1 = doc3, doc4
    *   Rel2 = doc1, doc2
    *   Rel3 = folder2, doc1
    *  
    */
   protected String createFolderTree() throws Exception
   {
      ContentStream cs = new BaseContentStream("1234567890".getBytes(), null, new MimeType("text", "plain"));

      FolderData testroot = createFolder(rootFolder, "testroot");

      FolderData folder1 = createFolder(testroot, "folder1");

      DocumentData doc1 = createDocument(testroot, "doc1", "1234567890");

      DocumentData doc2 = createDocument(testroot, "doc2", "1234567890");
      doc2.checkout();

      FolderData folder2 = createFolder(testroot, "folder2");

      DocumentData doc3 = createDocument(folder2, "doc3", "1234567890");

      FolderData folder3 = createFolder(folder2, "folder3");

      DocumentData doc4 = createDocument(folder3, "doc4", "1234567890");

      DocumentData doc5 = createDocument(testroot, "doc5", "1234567890");
      doc5.checkout();

      DocumentData doc6 = createDocument(testroot, "doc6", "1234567890");
      doc6.checkout();

      RelationshipData rel1 =
         getStorage().createRelationship(doc3, doc4, relationshipTypeDefinition,
            getPropsMap(CmisConstants.RELATIONSHIP, "rel1"), null, null);
      RelationshipData rel2 =
         getStorage().createRelationship(doc1, doc2, relationshipTypeDefinition,
            getPropsMap(CmisConstants.RELATIONSHIP, "rel2"), null, null);
      RelationshipData rel3 =
         getStorage().createRelationship(folder2, doc1, relationshipTypeDefinition,
            getPropsMap(CmisConstants.RELATIONSHIP, "rel3"), null, null);

      return testroot.getObjectId();
   }

   /**
    * @param testroot2
    * @param string
    * @param documentContent
    * @param major
    * @return
    * @throws IOException 
    * @throws StorageException 
    * @throws NameConstraintViolationException 
    * @throws ConstraintException 
    */
   protected DocumentData createDocument(FolderData parentFolder, String documentName, String documentContent)
      throws ConstraintException, NameConstraintViolationException, StorageException, IOException
   {
      ContentStream cs = new BaseContentStream(documentContent.getBytes(), null, new MimeType("text", "plain"));
      DocumentData doc =
         getStorage().createDocument(parentFolder, documentTypeDefinition,
            getPropsMap(CmisConstants.DOCUMENT, documentName), cs, null, null, VersioningState.MAJOR);
      return doc;
   }

   /**
    * @param parentFolder
    * @param folderTypeDefinition2
    * @return
    */
   protected FolderData createFolder(FolderData parentFolder, String folderName) throws StorageException,
      NameConstraintViolationException, ConstraintException
   {
      FolderData testroot =
         getStorage().createFolder(parentFolder, folderTypeDefinition, getPropsMap(CmisConstants.FOLDER, folderName),
            null, null);
      return testroot;
   }

   protected PolicyData createPolicy(FolderData where, String name) throws StorageException,
      NameConstraintViolationException, ConstraintException
   {
      org.xcmis.spi.model.PropertyDefinition<?> def =
         PropertyDefinitions.getPropertyDefinition(CmisConstants.POLICY, CmisConstants.POLICY_TEXT);
      Map<String, Property<?>> properties2 = getPropsMap(CmisConstants.POLICY, name);
      properties2.put(CmisConstants.POLICY_TEXT, new StringProperty(def.getId(), def.getQueryName(),
         def.getLocalName(), def.getDisplayName(), "testPolicyText"));
      PolicyData policy = getStorage().createPolicy(where, policyTypeDefinition, properties2, null, null);
      return policy;
   }

   protected void clearTree(String testroot)
   {
      try
      {
         FolderData rootFolder = (FolderData)getStorage().getObjectById(testroot);
         getStorage().deleteTree(rootFolder, true, UnfileObject.DELETE, true);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   protected void clear(String testroot)
   {
      try
      {
         removeRelationships(testroot);
         FolderData rootFolder = (FolderData)getStorage().getObjectById(testroot);
         List<String> failed = (List<String>)getStorage().deleteTree(rootFolder, true, UnfileObject.DELETE, true);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public List<CmisObject> objectTreeToList(List<ItemsTree<CmisObject>> source)
   {
      List<CmisObject> result = new ArrayList<CmisObject>();
      for (ItemsTree<CmisObject> one : source)
      {
         CmisObject type = one.getContainer();
         if (one.getChildren() != null)
         {
            result.addAll(objectTreeToList(one.getChildren()));
         }
         result.add(type);
      }

      return result;
   }

   protected Map<String, Property<?>> getPropsMap(String baseType, String name)
   {
      org.xcmis.spi.model.PropertyDefinition<?> def =
         PropertyDefinitions.getPropertyDefinition(baseType, CmisConstants.NAME);
      org.xcmis.spi.model.PropertyDefinition<?> def2 =
         PropertyDefinitions.getPropertyDefinition(baseType, CmisConstants.OBJECT_TYPE_ID);
      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      properties.put(CmisConstants.NAME, new StringProperty(def.getId(), def.getQueryName(), def.getLocalName(), def
         .getDisplayName(), name));
      properties.put(CmisConstants.OBJECT_TYPE_ID, new IdProperty(def2.getId(), def2.getQueryName(), def2
         .getLocalName(), def2.getDisplayName(), baseType));

      return properties;
   }

   protected RepositoryCapabilities getCapabilities()
   {
      return getStorage().getRepositoryInfo().getCapabilities();
   }

   protected void doFail(String mtd, String message) throws Exception
   {
      System.out.println("FAILED");
      failedTests.add(mtd);
      if (message != null)
         fail(message);
      else
         fail();
   }

   protected void pass(String o) throws Exception
   {
      System.out.println("PASSED");
      passedTests.add(o);
   }

   protected void skip(String o)
   {
      System.out.println("SKIPPED");
      skippedTests.add(o);
   }

   protected void removeRelationships(String testroot)
   {
      try
      {
         Connection connection = getConnection();
         List<ItemsTree<CmisObject>> descendants =
            connection.getDescendants(testroot, -1, false, IncludeRelationships.BOTH, false, true, PropertyFilter.ALL,
               RenditionFilter.NONE);
         for (ItemsTree<CmisObject> tr : descendants)
         {
            for (CmisObject relationship : tr.getContainer().getRelationship())
            {
               connection.deleteObject(relationship.getObjectInfo().getId(), null);
            }
            List<ItemsTree<CmisObject>> children = tr.getChildren();
            if (children != null && children.size() > 0)
            {
               removeRelationships(tr.getContainer().getObjectInfo().getId());
            }
         }
      }
      catch (ObjectNotFoundException e)
      {

      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
}
