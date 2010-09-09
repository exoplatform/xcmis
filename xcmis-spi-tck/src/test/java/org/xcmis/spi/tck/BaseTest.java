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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.CmisRegistry;
import org.xcmis.spi.Connection;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.ItemsTree;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.PropertyFilter;
import org.xcmis.spi.RenditionFilter;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.UserContext;
import org.xcmis.spi.model.ACLCapability;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.CmisObject;
import org.xcmis.spi.model.ContentStreamAllowed;
import org.xcmis.spi.model.IncludeRelationships;
import org.xcmis.spi.model.Permission;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.PropertyType;
import org.xcmis.spi.model.RepositoryCapabilities;
import org.xcmis.spi.model.RepositoryShortInfo;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.UnfileObject;
import org.xcmis.spi.model.Updatability;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.model.impl.BooleanProperty;
import org.xcmis.spi.model.impl.DateTimeProperty;
import org.xcmis.spi.model.impl.DecimalProperty;
import org.xcmis.spi.model.impl.HtmlProperty;
import org.xcmis.spi.model.impl.IdProperty;
import org.xcmis.spi.model.impl.IntegerProperty;
import org.xcmis.spi.model.impl.StringProperty;
import org.xcmis.spi.model.impl.UriProperty;
import org.xcmis.spi.utils.CmisUtils;
import org.xcmis.spi.utils.MimeType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey
 *         Zavizionov</a>
 * @version $Id$
 */

public class BaseTest
{

   protected static ACLCapability aclCapability;

   protected static RepositoryCapabilities capabilities;

   protected static Connection connection;

   protected static boolean isPoliciesSupported = false;

   protected static boolean isRelationshipsSupported = false;

   protected static FolderData rootFolder;

   protected static String rootFolderID;

   protected static byte[] TEST_CONTENT = "__TEST_CONTENT__".getBytes();

   protected static final ContentStream TEST_CONTENT_STREAM = new ContentStream()
   {

      MimeType mimeType = new MimeType("text", "plain");

      public String getFileName()
      {
         return "";
      }

      public MimeType getMediaType()
      {
         return mimeType;
      }

      public InputStream getStream() throws IOException
      {
         return new ByteArrayInputStream(TEST_CONTENT);
      }

      public long length()
      {
         return TEST_CONTENT.length;
      }
   };

   protected static final String TEST_POLICY_TEXT = "__TEST_POLICY__";

   @BeforeClass
   public static void init() throws Exception
   {
      CmisRegistry reg = CmisRegistry.getInstance();
      Iterator<RepositoryShortInfo> it = reg.getStorageInfos().iterator();
      connection = reg.getConnection(it.next().getRepositoryId());

      UserContext ctx = new UserContext("root");
      UserContext.setCurrent(ctx);

      rootFolderID = connection.getStorage().getRepositoryInfo().getRootFolderId();

      try
      {
         connection.getTypeDefinition(CmisConstants.POLICY);
         isPoliciesSupported = true;
      }
      catch (TypeNotFoundException e)
      {
      }

      try
      {
         connection.getTypeDefinition(CmisConstants.RELATIONSHIP);
         isRelationshipsSupported = true;
      }
      catch (TypeNotFoundException e)
      {
      }

      capabilities = connection.getStorage().getRepositoryInfo().getCapabilities();
      aclCapability = connection.getStorage().getRepositoryInfo().getAclCapability();
   }

   public static void clear(String root)
   {
      try
      {
         if (isRelationshipsSupported)
         {
            removeRelationships(root);
         }
         connection.deleteTree(root, true, UnfileObject.DELETE, true);
         connection.close();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   protected static List<AccessControlEntry> createACL(String name, String... permission)
   {
      AccessControlEntry acl = new AccessControlEntry();
      acl.setPrincipal(name);
      acl.getPermissions().addAll(Arrays.asList(permission));
      ArrayList<AccessControlEntry> addACL = new ArrayList<AccessControlEntry>();
      addACL.add(acl);
      return addACL;
   }

   protected static String createDocument(String parentId, String typeId, String name, ContentStream content,
      List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL, Collection<String> policies,
      VersioningState versioningState) throws Exception
   {
      TypeDefinition type = connection.getTypeDefinition(typeId, true);

      boolean versionable = type.isVersionable();
      if (!versionable)
      {
         // Prevent errors if type is not versionable.
         versioningState = VersioningState.NONE;
      }

      // Prevent error if content stream is required but not provided.
      ContentStreamAllowed streamAllowed = type.getContentStreamAllowed();
      if (streamAllowed == ContentStreamAllowed.REQUIRED && content == null)
      {
         content = TEST_CONTENT_STREAM;
      }

      Map<String, Property<?>> properties = createPropertyMap(type);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add(name);
      }

      String documentId = connection.createDocument(parentId, //
         properties, //
         content, //
         addACL, //
         removeACL, //
         policies, //
         versioningState);

      return documentId;
   }

   protected static String createFolder(String parentId, String typeId, String name, List<AccessControlEntry> addACL,
      List<AccessControlEntry> removeACL, Collection<String> policies) throws Exception
   {
      TypeDefinition type = connection.getTypeDefinition(typeId, true);
      Map<String, Property<?>> properties = createPropertyMap(type);
      StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
      if (nameProperty != null)
      {
         nameProperty.getValues().add(name);
      }
      String folderId = connection.createFolder(parentId, //
         properties, //
         addACL, //
         removeACL, //
         policies);
      return folderId;
   }

   protected static String createPolicy(String parentId, String typeId, String name, String policyText,
      List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL, Collection<String> policies)
      throws Exception
   {
      String policyId = null;
      if (isPoliciesSupported)
      {
         TypeDefinition type = connection.getTypeDefinition(typeId, true);
         Map<String, Property<?>> properties = createPropertyMap(type);
         StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
         if (nameProperty != null)
         {
            nameProperty.getValues().add(name);
         }
         StringProperty policyTextProperty = (StringProperty)properties.get(CmisConstants.POLICY_TEXT);
         if (policyTextProperty != null)
         {
            policyTextProperty.getValues().add(policyText != null ? policyText : TEST_POLICY_TEXT);
         }
         policyId = connection.createPolicy(type.isFileable() ? parentId : null, properties, null, null, null);
      }
      return policyId;
   }

   protected static Map<String, Property<?>> createPropertyMap(TypeDefinition type) throws Exception
   {
      Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
      for (PropertyDefinition<?> propertyDefinition : type.getPropertyDefinitions())
      {
         Updatability updatability = propertyDefinition.getUpdatability();
         if (updatability == Updatability.ONCREATE || updatability == Updatability.READWRITE)
         {
            Property<?> property = null;
            PropertyType propertyType = propertyDefinition.getPropertyType();
            switch (propertyType)
            {
               case BOOLEAN :
                  property =
                     new BooleanProperty(propertyDefinition.getId(), propertyDefinition.getQueryName(),
                        propertyDefinition.getLocalName(), propertyDefinition.getDisplayName(), (Boolean)null);
                  break;
               case DATETIME :
                  property =
                     new DateTimeProperty(propertyDefinition.getId(), propertyDefinition.getQueryName(),
                        propertyDefinition.getLocalName(), propertyDefinition.getDisplayName(), (Calendar)null);
                  break;
               case DECIMAL :
                  property =
                     new DecimalProperty(propertyDefinition.getId(), propertyDefinition.getQueryName(),
                        propertyDefinition.getLocalName(), propertyDefinition.getDisplayName(), (BigDecimal)null);
                  break;
               case HTML :
                  property =
                     new HtmlProperty(propertyDefinition.getId(), propertyDefinition.getQueryName(), propertyDefinition
                        .getLocalName(), propertyDefinition.getDisplayName(), (String)null);
                  break;
               case ID :
                  property =
                     new IdProperty(propertyDefinition.getId(), propertyDefinition.getQueryName(), propertyDefinition
                        .getLocalName(), propertyDefinition.getDisplayName(), (String)null);
                  break;
               case INTEGER :
                  property =
                     new IntegerProperty(propertyDefinition.getId(), propertyDefinition.getQueryName(),
                        propertyDefinition.getLocalName(), propertyDefinition.getDisplayName(), (BigInteger)null);
                  break;
               case STRING :
                  property =
                     new StringProperty(propertyDefinition.getId(), propertyDefinition.getQueryName(),
                        propertyDefinition.getLocalName(), propertyDefinition.getDisplayName(), (String)null);
                  break;
               case URI :
                  property =
                     new UriProperty(propertyDefinition.getId(), propertyDefinition.getQueryName(), propertyDefinition
                        .getLocalName(), propertyDefinition.getDisplayName(), (URI)null);
                  break;
            }
            properties.put(propertyDefinition.getId(), property);
         }
      }
      // Be sure type is set.
      PropertyDefinition<?> typeIdPropertyDefinition = type.getPropertyDefinition(CmisConstants.OBJECT_TYPE_ID);
      IdProperty typeIdProperty =
         new IdProperty(typeIdPropertyDefinition.getId(), typeIdPropertyDefinition.getQueryName(),
            typeIdPropertyDefinition.getLocalName(), typeIdPropertyDefinition.getDisplayName(), type.getId());
      properties.put(typeIdPropertyDefinition.getId(), typeIdProperty);

      return properties;
   }

   protected static String createRelationship(String typeId, String name, String sourceId, String targetId,
      List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL, Collection<String> policies)
      throws Exception
   {
      String relationshipId = null;
      if (isRelationshipsSupported)
      {
         TypeDefinition type = connection.getTypeDefinition(typeId, true);
         Map<String, Property<?>> properties = createPropertyMap(type);
         StringProperty nameProperty = (StringProperty)properties.get(CmisConstants.NAME);
         if (nameProperty != null)
         {
            nameProperty.getValues().add(name);
         }
         IdProperty sourceIdProperty = (IdProperty)properties.get(CmisConstants.SOURCE_ID);
         if (sourceIdProperty != null)
         {
            sourceIdProperty.getValues().add(sourceId);
         }
         IdProperty targetIdProperty = (IdProperty)properties.get(CmisConstants.TARGET_ID);
         if (targetIdProperty != null)
         {
            targetIdProperty.getValues().add(targetId);
         }
         relationshipId = connection.createRelationship(properties, null, null, null);
      }
      return relationshipId;
   }

   protected static String generateName(TypeDefinition type, String suffix)
   {
      StringBuilder b = new StringBuilder();
      switch (type.getBaseId())
      {
         case DOCUMENT :
            b.append("document");
            break;
         case FOLDER :
            b.append("folder");
            break;
         case POLICY :
            b.append("policy");
            break;
         case RELATIONSHIP :
            b.append("relationship");
            break;
      }
      b.append('_').append(UUID.randomUUID().toString());
      if (suffix != null && suffix.length() > 0)
      {
         b.append('.').append(suffix);
      }
      return b.toString();
   }

   /**
    * Find first type which supports ACL.
    *
    * @param types tree of all available types
    * @return type which support ACL or <code>null</code> if there is no such
    *         type
    * @throws Exception if any error occurs
    */
   protected static TypeDefinition getControllableAclType(List<ItemsTree<TypeDefinition>> types) throws Exception
   {
      for (ItemsTree<TypeDefinition> item : types)
      {
         TypeDefinition container = item.getContainer();
         if (container.isControllableACL())
         {
            return container;
         }
         List<ItemsTree<TypeDefinition>> children = item.getChildren();
         if (children != null && !children.isEmpty())
         {
            return getControllableAclType(children);
         }
      }
      return null;
   }

   /**
    * Find first type which is controllable by policies.
    *
    * @param types tree of all available types
    * @return type which is controllable by policies or <code>null</code> if
    *         there is no such type
    * @throws Exception if any error occurs
    */
   protected static TypeDefinition getControllablePolicyType(List<ItemsTree<TypeDefinition>> types) throws Exception
   {
      if (isPoliciesSupported)
      {
         for (ItemsTree<TypeDefinition> item : types)
         {
            TypeDefinition container = item.getContainer();
            if (container.isControllablePolicy())
            {
               return container;
            }
            List<ItemsTree<TypeDefinition>> children = item.getChildren();
            if (children != null && !children.isEmpty())
            {
               return getControllablePolicyType(children);
            }
         }
      }
      return null;
   }

   /**
    * Find first type which does not support ACL.
    *
    * @param types tree of all available types
    * @return type which does not support ACL or <code>null</code> if there is
    *         no such type
    * @throws Exception if any error occurs
    */
   protected static TypeDefinition getNotControllableAclType(List<ItemsTree<TypeDefinition>> types) throws Exception
   {
      for (ItemsTree<TypeDefinition> item : types)
      {
         TypeDefinition container = item.getContainer();
         if (!container.isControllableACL())
         {
            return container;
         }
         List<ItemsTree<TypeDefinition>> children = item.getChildren();
         if (children != null && !children.isEmpty())
         {
            return getNotControllableAclType(children);
         }
      }
      return null;
   }

   /**
    * Find first type which is not controllable by policies.
    *
    * @param types tree of all available types
    * @return type which is not controllable by policies or <code>null</code> if
    *         there is no such type
    * @throws Exception if any error occurs
    */
   protected static TypeDefinition getNotControllablePolicyType(List<ItemsTree<TypeDefinition>> types) throws Exception
   {
      for (ItemsTree<TypeDefinition> item : types)
      {
         TypeDefinition container = item.getContainer();
         if (!container.isControllablePolicy())
         {
            return container;
         }
         List<ItemsTree<TypeDefinition>> children = item.getChildren();
         if (children != null && !children.isEmpty())
         {
            return getNotControllablePolicyType(children);
         }
      }
      return null;
   }

   /**
    * Find first document type which is not versionable.
    *
    * @param types tree of all available types
    * @return type which is not versionable or <code>null</code> if there is no
    *         such type
    * @throws Exception if any error occurs
    */
   protected static TypeDefinition getNotVersionableDocType(List<ItemsTree<TypeDefinition>> types) throws Exception
   {
      for (ItemsTree<TypeDefinition> item : types)
      {
         TypeDefinition container = item.getContainer();
         if (container.getBaseId() == BaseType.DOCUMENT)
         {
            if (!container.isVersionable())
            {
               return container;
            }
            List<ItemsTree<TypeDefinition>> children = item.getChildren();
            if (children != null && !children.isEmpty())
            {
               return getNotVersionableDocType(children);
            }
         }
      }
      return null;
   }

   /**
    * Find first document type which does not support content stream (
    * {@link TypeDefinition#getContentStreamAllowed()} is NOT_ALLOWED).
    *
    * @param types tree of all available types
    * @return type which does not support content stream or <code>null</code> if
    *         there is no such type
    * @throws Exception if any error occurs
    */
   protected static TypeDefinition getStreamNotSupportedDocType(List<ItemsTree<TypeDefinition>> types) throws Exception
   {
      for (ItemsTree<TypeDefinition> item : types)
      {
         TypeDefinition container = item.getContainer();
         if (container.getBaseId() == BaseType.DOCUMENT)
         {
            if (container.getContentStreamAllowed() == ContentStreamAllowed.NOT_ALLOWED)
            {
               return container;
            }
            List<ItemsTree<TypeDefinition>> children = item.getChildren();
            if (children != null && !children.isEmpty())
            {
               return getStreamNotSupportedDocType(children);
            }
         }
      }
      return null;
   }

   /**
    * Find first document type which required content stream (
    * {@link TypeDefinition#getContentStreamAllowed()} is REQUIRED).
    *
    * @param types tree of all available types
    * @return type which require content stream or <code>null</code> if there is
    *         no such type
    * @throws Exception if any error occurs
    */
   protected static TypeDefinition getStreamRequiredDocType(List<ItemsTree<TypeDefinition>> types) throws Exception
   {
      for (ItemsTree<TypeDefinition> item : types)
      {
         TypeDefinition container = item.getContainer();
         if (container.getBaseId() == BaseType.DOCUMENT)
         {
            if (container.getContentStreamAllowed() == ContentStreamAllowed.REQUIRED)
            {
               return container;
            }
            List<ItemsTree<TypeDefinition>> children = item.getChildren();
            if (children != null && !children.isEmpty())
            {
               return getStreamRequiredDocType(children);
            }
         }
      }
      return null;
   }

   /**
    * Find first document type which is versionable.
    *
    * @param types tree of all available types
    * @return type which is versionable or <code>null</code> if there is no such
    *         type
    * @throws Exception if any error occurs
    */
   protected static TypeDefinition getVersionableDocType(List<ItemsTree<TypeDefinition>> types) throws Exception
   {
      for (ItemsTree<TypeDefinition> item : types)
      {
         TypeDefinition container = item.getContainer();
         if (container.getBaseId() == BaseType.DOCUMENT)
         {
            if (container.isVersionable())
            {
               return container;
            }
            List<ItemsTree<TypeDefinition>> children = item.getChildren();
            if (children != null && !children.isEmpty())
            {
               return getVersionableDocType(children);
            }
         }
      }
      return null;
   }

   protected static void removeRelationships(String testroot)
   {
      try
      {
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

   /**
    * Check that two ACL are matched. It minds <code>actual</code> contains at
    * least all ACEs from <code>expected</code> but may have other ACEs.
    *
    * @param expected expected ACEs
    * @param actual actual ACEs
    */
   protected void checkACL(List<AccessControlEntry> expected, List<AccessControlEntry> actual)
   {
      Map<String, Set<String>> m1 = new HashMap<String, Set<String>>();
      CmisUtils.addAclToPermissionMap(m1, expected);
      Map<String, Set<String>> m2 = new HashMap<String, Set<String>>();
      CmisUtils.addAclToPermissionMap(m2, actual);
      for (Map.Entry<String, Set<String>> e : m1.entrySet())
      {
         String principal = e.getKey();
         Set<String> permissions2 = m2.get(principal);
         if (permissions2 == null)
         {
            fail("ACLs are not matched.");
         }
         if (!actual.contains("cmis:all"))
         {
            for (String permission : e.getValue())
            {
               assertTrue("ACLs are not matched. Permission " + permission + " for principal " + principal
                  + " not found", permissions2.contains(permission));
            }
         }
      }
   }

   protected List<CmisObject> objectTreeAsList(List<ItemsTree<CmisObject>> source)
   {
      List<CmisObject> result = new ArrayList<CmisObject>();
      for (ItemsTree<CmisObject> one : source)
      {
         CmisObject o = one.getContainer();
         if (one.getChildren() != null)
         {
            result.addAll(objectTreeAsList(one.getChildren()));
         }
         result.add(o);
      }
      return result;
   }

   protected List<TypeDefinition> typeTreeAsList(List<ItemsTree<TypeDefinition>> source)
   {
      List<TypeDefinition> result = new ArrayList<TypeDefinition>();
      for (ItemsTree<TypeDefinition> one : source)
      {
         TypeDefinition type = one.getContainer();
         if (one.getChildren() != null)
         {
            result.addAll(typeTreeAsList(one.getChildren()));
         }
         result.add(type);
      }
      return result;
   }

   /**
    * Validate that ACL contains only valid permissions.
    *
    * @param actual actual ACEs
    */
   protected void validateACL(List<AccessControlEntry> actual)
   {
      Map<String, Set<String>> m1 = new HashMap<String, Set<String>>();
      CmisUtils.addAclToPermissionMap(m1, actual);
      Set<String> allowed = new HashSet<String>();
      for (Permission p : aclCapability.getPermissions())
      {
         allowed.add(p.getPermission());
      }
      for (Map.Entry<String, Set<String>> e : m1.entrySet())
      {
         for (String permission : e.getValue())
         {
            assertTrue("ACLs contains unknown permission: " + permission, allowed.contains(permission));
         }
      }
   }
}
