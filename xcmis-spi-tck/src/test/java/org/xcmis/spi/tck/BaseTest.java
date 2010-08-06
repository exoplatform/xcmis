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

package org.xcmis.spi.tck;

import org.exoplatform.container.StandaloneContainer;
import org.xcmis.spi.BaseContentStream;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.CmisRegistry;
import org.xcmis.spi.Connection;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.DocumentData;
import org.xcmis.spi.FolderData;
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
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.IncludeRelationships;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.RepositoryCapabilities;
import org.xcmis.spi.model.RepositoryShortInfo;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id$
 */

public class BaseTest
{

   protected static StandaloneContainer container;

   protected static StorageProvider storageProvider;

   protected static TypeDefinition documentTypeDefinition;

   protected static TypeDefinition folderTypeDefinition;

   protected static TypeDefinition policyTypeDefinition;

   protected static TypeDefinition relationshipTypeDefinition;

   protected static String rootfolderID;

   protected static FolderData rootFolder;

   protected static Connection conn;

   protected static boolean IS_RELATIONSHIPS_SUPPORTED = false;

   protected static boolean IS_POLICIES_SUPPORTED = false;

   protected static boolean IS_CAPABILITY_FOLDER_TREE = false;

   protected static boolean IS_CAPABILITY_DESCENDANTS = false;
   
   protected static boolean useConf = false;

   public static void setUp() throws Exception
   {
      ConversationState state = new ConversationState(new Identity("root"));
      ConversationState.setCurrent(state);

     
      rootfolderID = getStorage().getRepositoryInfo().getRootFolderId();
      rootFolder = (FolderData)getStorage().getObjectById(rootfolderID);

      try
      {
         if (getStorage().getTypeDefinition(CmisConstants.POLICY, false) != null)
            IS_POLICIES_SUPPORTED = true;
      }
      catch (TypeNotFoundException ex)
      {
         // Not supp;  
      }

      try
      {
         if (getStorage().getTypeDefinition(CmisConstants.RELATIONSHIP, false) != null)
            IS_RELATIONSHIPS_SUPPORTED = true;
      }
      catch (TypeNotFoundException ex)
      {
         //Not supp;
      }

      if (getStorage().getRepositoryInfo().getCapabilities().isCapabilityGetFolderTree())
         IS_CAPABILITY_FOLDER_TREE = true;

      if (getStorage().getRepositoryInfo().getCapabilities().isCapabilityGetDescendants())
         IS_CAPABILITY_DESCENDANTS = true;

      documentTypeDefinition = getStorage().getTypeDefinition(CmisConstants.DOCUMENT, true);
      folderTypeDefinition = getStorage().getTypeDefinition(CmisConstants.FOLDER, true);

      if (IS_POLICIES_SUPPORTED)
         policyTypeDefinition = getStorage().getTypeDefinition(CmisConstants.POLICY, true);
      if (IS_RELATIONSHIPS_SUPPORTED)
         relationshipTypeDefinition = getStorage().getTypeDefinition(CmisConstants.RELATIONSHIP, true);

   }

   protected static Connection getConnection()
   {
      CmisRegistry reg =CmisRegistry.getInstance(); 
      Iterator<RepositoryShortInfo> it = reg.getStorageInfos().iterator();
      conn = reg.getConnection(it.next().getRepositoryId());
      return conn;
   }

   protected static Storage getStorage()
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
   protected static String createFolderTree() throws Exception
   {
      FolderData testroot = createFolder(rootFolder, "navigation_testroot");

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

      if (IS_RELATIONSHIPS_SUPPORTED)
      {
         RelationshipData rel1 =
            getStorage().createRelationship(doc3, doc4, relationshipTypeDefinition,
               getPropsMap(CmisConstants.RELATIONSHIP, "rel1"), null, null);
         RelationshipData rel2 =
            getStorage().createRelationship(doc1, doc2, relationshipTypeDefinition,
               getPropsMap(CmisConstants.RELATIONSHIP, "rel2"), null, null);
         RelationshipData rel3 =
            getStorage().createRelationship(folder2, doc1, relationshipTypeDefinition,
               getPropsMap(CmisConstants.RELATIONSHIP, "rel3"), null, null);
      }

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
   protected static DocumentData createDocument(FolderData parentFolder, String documentName, String documentContent)
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
   protected static FolderData createFolder(FolderData parentFolder, String folderName) throws StorageException,
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
      if (IS_POLICIES_SUPPORTED)
      {
         org.xcmis.spi.model.PropertyDefinition<?> def =
            PropertyDefinitions.getPropertyDefinition(CmisConstants.POLICY, CmisConstants.POLICY_TEXT);
         Map<String, Property<?>> properties2 = getPropsMap(CmisConstants.POLICY, name);
         properties2.put(CmisConstants.POLICY_TEXT, new StringProperty(def.getId(), def.getQueryName(), def
            .getLocalName(), def.getDisplayName(), "testPolicyText"));
         PolicyData policy = getStorage().createPolicy(where, policyTypeDefinition, properties2, null, null);
         return policy;
      }
      else
      {
         return null;
      }
   }

   protected static List<AccessControlEntry> createACL(String name, String permission)
   {
      AccessControlEntry acl = new AccessControlEntry();
      acl.setPrincipal(name);
      acl.getPermissions().add(permission);
      ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
      addACL.add(acl);
      return addACL;
   }

   protected static void clearTree(String testroot)
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

   protected static void clear(String testroot)
   {
      try
      {
         if (IS_RELATIONSHIPS_SUPPORTED)
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

   protected static Map<String, Property<?>> getPropsMap(String baseType, String name)
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

   protected static RepositoryCapabilities getCapabilities()
   {
      return getStorage().getRepositoryInfo().getCapabilities();
   }


//   protected static void removeRelationships(String folderId)
//   {
//      try
//      {
//         Connection connection = getConnection();
//         ItemsList<CmisObject> childs =
//            connection.getChildren(folderId, false, IncludeRelationships.BOTH, false, true, PropertyFilter.ALL,
//               RenditionFilter.NONE, "", -1, 0);
//         for (CmisObject one : childs.getItems())
//         {
//
//            if (one.getObjectInfo().getBaseType().equals(BaseType.FOLDER))
//            {
//               removeRelationships(one.getObjectInfo().getId());
//            }
//            else
//            {
//               for (CmisObject relationship : one.getRelationship())
//               {
//                  try
//                  {
//                     connection.deleteObject(relationship.getObjectInfo().getId(), null);
//                  }
//                  catch (ObjectNotFoundException e)
//                  {
//                  }
//               }
//            }
//         }
//      }
//      catch (Exception e)
//      {
//         e.printStackTrace();
//      }
//   }
   
   protected static void removeRelationships(String testroot)
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
               try
               {
                  connection.deleteObject(relationship.getObjectInfo().getId(), null);
               }
               catch (ObjectNotFoundException e)
               {
               }
            }
            List<ItemsTree<CmisObject>> children = tr.getChildren();
            if (children != null && children.size() > 0)
            {
               removeRelationships(tr.getContainer().getObjectInfo().getId());
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
}
