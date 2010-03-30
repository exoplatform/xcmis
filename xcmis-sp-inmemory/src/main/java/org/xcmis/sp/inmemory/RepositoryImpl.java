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

import org.xcmis.core.CmisACLCapabilityType;
import org.xcmis.core.CmisPermissionDefinition;
import org.xcmis.core.CmisPermissionMapping;
import org.xcmis.core.CmisRenditionType;
import org.xcmis.core.CmisRepositoryCapabilitiesType;
import org.xcmis.core.CmisRepositoryInfoType;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.CmisTypeDocumentDefinitionType;
import org.xcmis.core.EnumACLPropagation;
import org.xcmis.core.EnumAllowableActionsKey;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumBasicPermissions;
import org.xcmis.core.EnumCapabilityACL;
import org.xcmis.core.EnumCapabilityChanges;
import org.xcmis.core.EnumCapabilityContentStreamUpdates;
import org.xcmis.core.EnumCapabilityJoin;
import org.xcmis.core.EnumCapabilityQuery;
import org.xcmis.core.EnumCapabilityRendition;
import org.xcmis.core.EnumContentStreamAllowed;
import org.xcmis.core.EnumVersioningState;
import org.xcmis.messaging.CmisTypeContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ChangeTokenMatcher;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.EntryNameProducer;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.Repository;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.object.ContentStream;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.ItemsIterator;
import org.xcmis.spi.object.RenditionManager;
import org.xcmis.spi.object.VersionSeries;
import org.xcmis.spi.query.QueryHandler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RepositoryImpl.java 218 2010-02-15 07:38:06Z andrew00x $
 */
public class RepositoryImpl extends TypeManagerImpl implements Repository, EntryNameProducer, RenditionManager,
   ChangeTokenMatcher
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(RepositoryImpl.class.getName());

   /** The Constant NO_PARENT. */
   protected static final Set<String> NO_PARENT = Collections.emptySet();

   /** The config. */
   private CMISRepositoryConfiguration config;

   /** The Constant DEFAULT_FOLDER_NAME. */
   public static final String DEFAULT_FOLDER_NAME = "New Folder";

   /** The Constant DEFAULT_DOCUMENT_NAME. */
   public static final String DEFAULT_DOCUMENT_NAME = "New Document";

   /** The Constant DEFAULT_POLICY_NAME. */
   public static final String DEFAULT_POLICY_NAME = "New Policy";

   /** The info. */
   private CmisRepositoryInfoType info;

   /** The Constant ROOT_FOLDER_ID. */
   public static final String ROOT_FOLDER_ID = "CMIS_ROOT_FOLDER_UUID";

   /** The Constant ANY. */
   public static final String ANY = "any".intern();

   /** The Constant SYSTEM. */
   public static final String SYSTEM = "system".intern();

   /** The Constant ANONYMOUS. */
   public static final String ANONYMOUS = "anonymous".intern();

   /** TRUE if change token feature if 'on' FALSE otherwise. */
   protected final boolean changeTokenFeature;

   /** The storage. */
   protected Storage storage;

   /**
    * Generate id.
    * @return the string
    */
   public static String generateId()
   {
      return UUID.randomUUID().toString();
   }

   /**
    * Instantiates a new repository impl.
    * 
    * @param repositoryConfiguration the repository configuration
    */
   public RepositoryImpl(CMISRepositoryConfiguration repositoryConfiguration)
   {
      config = repositoryConfiguration;

      storage = new Storage();
      CmisTypeDefinitionType type = types.get(EnumBaseObjectTypeIds.CMIS_FOLDER.value());
      Map<String, Object[]> root = new ConcurrentHashMap<String, Object[]>();
      root.put(CMIS.NAME, new String[]{CMIS.ROOT_FOLDER_NAME});
      root.put(CMIS.OBJECT_ID, new String[]{ROOT_FOLDER_ID});
      root.put(CMIS.OBJECT_TYPE_ID, new String[]{type.getId()});
      root.put(CMIS.BASE_TYPE_ID, new String[]{type.getBaseId().value()});
      root.put(CMIS.PARENT_ID, new String[]{});
      root.put(CMIS.PATH, new String[]{"/"});
      Calendar date = Calendar.getInstance();
      ConversationState cstate = ConversationState.getCurrent();
      String userId = null;
      if (cstate != null)
         userId = cstate.getIdentity().getUserId();
      if (userId != null)
         root.put(CMIS.CREATED_BY, new String[]{userId});
      root.put(CMIS.CREATION_DATE, new Calendar[]{date});
      storage.getObjects().put(ROOT_FOLDER_ID, root);

      storage.getChildren().put(ROOT_FOLDER_ID, new CopyOnWriteArraySet<String>());
      storage.getParents().put(ROOT_FOLDER_ID, NO_PARENT);
      storage.getPolicies().put(ROOT_FOLDER_ID, new CopyOnWriteArraySet<String>());

      changeTokenFeature =
         repositoryConfiguration.getProperties().get("exo.cmis.changetoken.feature") != null ? (Boolean)config
            .getProperties().get("exo.cmis.changetoken.feature") : true;
   }

   /**
    * {@inheritDoc}
    */
   public Entry copyObject(String objectId, String destinationFolderId, EnumVersioningState versioningState)
      throws ConstraintException, ObjectNotFoundException, RepositoryException
   {
      EntryImpl object = (EntryImpl)getObjectById(objectId);
      if (object.getScope() != EnumBaseObjectTypeIds.CMIS_DOCUMENT)
      {
         String msg = "Copying is not supported for object with type other than 'cmis:document'.";
         throw new ConstraintException(msg);
      }
      Entry destinationFolder = getObjectById(destinationFolderId);
      if (destinationFolder.getScope() != EnumBaseObjectTypeIds.CMIS_FOLDER)
      {
         String msg = "Target object " + destinationFolderId + " is not a folder.";
         throw new ConstraintException(msg);
      }

      Entry copy = object.copy();
      storage.getChildren().get(destinationFolderId).add(copy.getObjectId());

      Set<String> copyParents = new HashSet<String>();
      copyParents.add(destinationFolderId);
      storage.getParents().put(copy.getObjectId(), copyParents);

      storage.getPolicies().put(copy.getObjectId(), new HashSet<String>());

      return new EntryImpl(copy.getObjectId(), storage);
   }

   /**
    * {@inheritDoc}
    */
   public Entry createObject(CmisTypeDefinitionType type, EnumVersioningState versioningState)
      throws RepositoryException
   {
      String objectId = RepositoryImpl.generateId();
      String vsID = RepositoryImpl.generateId();
      Map<String, Object[]> properties = new ConcurrentHashMap<String, Object[]>();
      properties.put(CMIS.OBJECT_ID, new String[]{objectId});
      properties.put(CMIS.NAME, new String[]{getEntryName(null, type.getBaseId(), type.getId())});
      properties.put(CMIS.OBJECT_TYPE_ID, new String[]{type.getId()});
      properties.put(CMIS.BASE_TYPE_ID, new String[]{type.getBaseId().value()});
      Calendar date = Calendar.getInstance();
      ConversationState cstate = ConversationState.getCurrent();
      String userId = null;
      if (cstate != null)
         userId = cstate.getIdentity().getUserId();
      if (userId != null)
         properties.put(CMIS.CREATED_BY, new String[]{userId});
      properties.put(CMIS.CREATION_DATE, new Calendar[]{date});
      properties.put(CMIS.IS_IMMUTABLE, new Boolean[]{false});
      properties.put(CMIS.IS_LATEST_VERSION, new Boolean[]{versioningState != EnumVersioningState.CHECKEDOUT});
      properties.put(CMIS.IS_MAJOR_VERSION, new Boolean[]{versioningState == EnumVersioningState.MAJOR});
      properties.put(CMIS.IS_LATEST_MAJOR_VERSION, new Boolean[]{versioningState == EnumVersioningState.MAJOR});
      properties.put(CMIS.VERSION_LABEL, new String[]{versioningState == EnumVersioningState.CHECKEDOUT ? "pwc"
         : "current"});
      properties.put(CMIS.IS_VERSION_SERIES_CHECKED_OUT,
         new Boolean[]{versioningState == EnumVersioningState.CHECKEDOUT});
      if (versioningState == EnumVersioningState.CHECKEDOUT)
         properties.put(CMIS.VERSION_SERIES_CHECKED_OUT_ID, new String[]{objectId});
      if (versioningState == EnumVersioningState.CHECKEDOUT && userId != null)
         properties.put(CMIS.VERSION_SERIES_CHECKED_OUT_BY, new String[]{userId});
      properties.put(CMIS.VERSION_SERIES_ID, new String[]{vsID});
      properties.put(CMIS.CONTENT_STREAM_LENGTH, new BigInteger[]{BigInteger.ZERO});
      properties.put(CMIS.CONTENT_STREAM_MIME_TYPE, new String[]{""});
      //      child.put(CMIS.CONTENT_STREAM_FILE_NAME, new String[]{name});
      properties.put(CMIS.CONTENT_STREAM_ID, new String[]{objectId});

      storage.getObjects().put(objectId, properties);
      //      storage.getChildren().put(objectId, Collections.EMPTY_SET);
      storage.getParents().put(objectId, new CopyOnWriteArraySet<String>());
      storage.getPolicies().put(objectId, new CopyOnWriteArraySet<String>());
      Set<String> v = new CopyOnWriteArraySet<String>();
      v.add(objectId);
      storage.getVersions().put(vsID, v);
      storage.getUnfiling().add(objectId);

      return new EntryImpl(objectId, storage);
   }

   /** 
    * {@inheritDoc}
    */
   public boolean createRenditions(Entry entry) throws InvalidArgumentException, RepositoryException
   {
      return true;
   }

   /**
    * {@inheritDoc}
    */
   public ChangeTokenMatcher getChangeTokenMatcher() throws RepositoryException
   {
      return this;
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<Entry> getCheckedOutDocuments(String folderId) throws ObjectNotFoundException,
      RepositoryException
   {
      Set<Entry> checkedout = new HashSet<Entry>();

      if (folderId == null)
         getCheckedOut(getRootFolder(), checkedout, true);
      else
         getCheckedOut(getObjectById(folderId), checkedout, false);
      return new SimpleItemsIterator<Entry>(checkedout);
   }

   /**
    * Gets the checked out.
    * 
    * @param folder the folder
    * @param checkedout the checkedout
    * @param recursive the recursive
    * @throws RepositoryException the repository exception
    */
   private void getCheckedOut(Entry folder, Set<Entry> checkedout, boolean recursive) throws RepositoryException
   {
      for (ItemsIterator<Entry> iter = folder.getChildren(); iter.hasNext();)
      {
         Entry entry = iter.next();
         if (entry.getScope() == EnumBaseObjectTypeIds.CMIS_DOCUMENT)
         {
            if (entry.getObjectId().equals(entry.getCheckedOutId()))
               checkedout.add(entry);
         }
         if (recursive && entry.getScope() == EnumBaseObjectTypeIds.CMIS_FOLDER)
            getCheckedOut(entry, checkedout, recursive);
      }
   }

   /** 
    * {@inheritDoc}
    */
   public String getEntryName(String parentId, EnumBaseObjectTypeIds scope, String typeId)
   {
      if (LOG.isDebugEnabled())
         LOG
            .debug("Generate name for entry, parentId " + parentId + ", typedId " + typeId + ", scope " + scope.value());

      if (parentId == null)
         return generateId();

      String pattern;
      if (scope == EnumBaseObjectTypeIds.CMIS_DOCUMENT)
         pattern = DEFAULT_DOCUMENT_NAME;
      else if (scope == EnumBaseObjectTypeIds.CMIS_FOLDER)
         pattern = DEFAULT_FOLDER_NAME;
      else if (scope == EnumBaseObjectTypeIds.CMIS_POLICY)
         pattern = DEFAULT_POLICY_NAME;
      else if (scope == EnumBaseObjectTypeIds.CMIS_RELATIONSHIP)
         return generateId();
      else
         throw new UnsupportedOperationException();

      if (storage.getChildren().get(parentId).isEmpty())
         return pattern;

      int count = 0;
      for (String key : storage.getChildren().get(parentId))
      {
         String tmp = (String)storage.getObjects().get(key).get(CMIS.NAME)[0];
         if (tmp.startsWith(pattern))
            count++;
      }

      if (count == 0)
         return pattern;

      String name;
      count++;
      while (storage.getChildren().get(parentId).contains(pattern + " (" + count + ")"))
         count++;
      name = pattern + " (" + String.valueOf(count) + ")";
      if (LOG.isDebugEnabled())
         LOG.debug("Entry name: " + name);
      return name;
   }

   /**
    * {@inheritDoc}
    */
   public String getId()
   {
      return config.getId();
   }

   /**
    * {@inheritDoc}
    */
   public Entry getObjectById(String objectId) throws ObjectNotFoundException, RepositoryException
   {
      if (!storage.hasObject(objectId))
         throw new ObjectNotFoundException("There is no object with the id = '" + objectId + "'.");
      return new EntryImpl(objectId, storage);
   }

   /**
    * {@inheritDoc}
    */
   public Entry getObjectByPath(String path) throws ObjectNotFoundException, RepositoryException
   {
      if (!path.startsWith("/"))
         path = "/" + path;
      StringTokenizer tokenizer = new StringTokenizer(path, "/");
      String point = ROOT_FOLDER_ID;
      while (tokenizer.hasMoreTokens())
      {
         if (point == null)
            break;
         String segName = tokenizer.nextToken();
         Set<String> childrenIds = storage.getChildren().get(point);
         if (childrenIds == null || childrenIds.isEmpty())
         {
            point = null;
         }
         else
         {
            for (String id : childrenIds)
            {
               Entry seg = new EntryImpl(id, storage);
               if ((seg.getScope() == EnumBaseObjectTypeIds.CMIS_FOLDER || !tokenizer.hasMoreTokens())//
                  && seg.getName().equals(segName))//
               {
                  point = id;
                  break;
               }
               point = null;
            }
         }
      }
      if (point == null)
         throw new ObjectNotFoundException("Path '" + path + "' not found.");
      return new EntryImpl(point, storage);
   }

   /**
    * {@inheritDoc}
    */
   public RenditionManager getRenditionManager() throws RepositoryException
   {
      return this;
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<CmisRenditionType> getRenditions(Entry entry) throws RepositoryException
   {
      // TODO 
      List<CmisRenditionType> list = new ArrayList<CmisRenditionType>(1);
      if (entry.getScope() == EnumBaseObjectTypeIds.CMIS_DOCUMENT)
      {
         if (((CmisTypeDocumentDefinitionType)entry.getType()).getContentStreamAllowed() != EnumContentStreamAllowed.NOTALLOWED)
         {
            // Simply copy stream.
            ContentStream content = entry.getContent(null);
            if (content != null)
            {
               CmisRenditionType rendition = new CmisRenditionType();
               rendition.setKind("cmis:simple");
               rendition.setLength(BigInteger.valueOf(content.length()));
               rendition.setMimetype(content.getMediaType());
               rendition.setStreamId(entry.getObjectId());
               rendition.setTitle("simple");
               list.add(rendition);
            }
         }
      }
      return new SimpleItemsIterator<CmisRenditionType>(list);
   }

   /**
    * {@inheritDoc}
    */
   public org.xcmis.spi.object.ItemsIterator<CmisRenditionType> getRenditions(String objectId)
      throws ObjectNotFoundException, RepositoryException
   {
      return getRenditions(getObjectById(objectId));
   }

   /**
    * {@inheritDoc}
    */
   public CmisRepositoryInfoType getRepositoryInfo()
   {
      if (info == null)
      {
         info = new CmisRepositoryInfoType();
         info.setRepositoryId(getId());
         info.setRepositoryName(getId());
         info.setCmisVersionSupported(CMIS.SUPPORTED_VERSION);
         info.setRepositoryDescription(""); // ?
         info.setProductName("eXo CMIS Simple");
         info.setVendorName("eXo CMIS Simple");
         info.setProductVersion("1.0-SNAPSHOT");
         info.setRootFolderId(ROOT_FOLDER_ID);
         CmisRepositoryCapabilitiesType capabilities = new CmisRepositoryCapabilitiesType();
         capabilities.setCapabilityACL(EnumCapabilityACL.MANAGE);
         capabilities.setCapabilityAllVersionsSearchable(true);
         capabilities.setCapabilityChanges(EnumCapabilityChanges.NONE);
         capabilities.setCapabilityContentStreamUpdatability(EnumCapabilityContentStreamUpdates.ANYTIME);
         capabilities.setCapabilityGetDescendants(true);
         capabilities.setCapabilityGetFolderTree(true);
         capabilities.setCapabilityJoin(EnumCapabilityJoin.NONE);
         capabilities.setCapabilityMultifiling(true);
         capabilities.setCapabilityPWCSearchable(true);
         capabilities.setCapabilityPWCUpdatable(true);
         capabilities.setCapabilityQuery(EnumCapabilityQuery.NONE);
         capabilities.setCapabilityRenditions(EnumCapabilityRendition.NONE);
         capabilities.setCapabilityUnfiling(true);
         capabilities.setCapabilityVersionSpecificFiling(false);
         info.setCapabilities(capabilities);
         CmisACLCapabilityType aclCapabilities = new CmisACLCapabilityType();
         for (EnumBasicPermissions perm : EnumBasicPermissions.values())
         {
            CmisPermissionDefinition permDef = new CmisPermissionDefinition();
            permDef.setPermission(perm.value());
            aclCapabilities.getPermissions().add(permDef);
         }
         aclCapabilities.setPropagation(EnumACLPropagation.REPOSITORYDETERMINED);
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_GET_DESCENDENTS_FOLDER, // 
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_GET_CHILDREN_FOLDER, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_GET_PARENTS_FOLDER, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_GET_FOLDER_PARENT_OBJECT, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_CREATE_DOCUMENT_FOLDER, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_CREATE_FOLDER_FOLDER, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_CREATE_RELATIONSHIP_SOURCE, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_CREATE_RELATIONSHIP_TARGET, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_GET_PROPERTIES_OBJECT, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_VIEW_CONTENT_OBJECT, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_UPDATE_PROPERTIES_OBJECT, //
            EnumBasicPermissions.CMIS_WRITE.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_MOVE_OBJECT, //
            EnumBasicPermissions.CMIS_WRITE.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_MOVE_TARGET, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_MOVE_SOURCE, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_DELETE_OBJECT, //
            EnumBasicPermissions.CMIS_WRITE.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_DELETE_TREE_FOLDER, //
            EnumBasicPermissions.CMIS_WRITE.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_SET_CONTENT_DOCUMENT, //
            EnumBasicPermissions.CMIS_WRITE.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_DELETE_CONTENT_DOCUMENT, //
            EnumBasicPermissions.CMIS_WRITE.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_ADD_TO_FOLDER_OBJECT, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_ADD_TO_FOLDER_FOLDER, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_REMOVE_FROM_FOLDER_OBJECT, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_REMOVE_FROM_FOLDER_FOLDER, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_CHECKOUT_DOCUMENT, //
            EnumBasicPermissions.CMIS_WRITE.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_CANCEL_CHECKOUT_DOCUMENT, //
            EnumBasicPermissions.CMIS_WRITE.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_CHECKIN_DOCUMENT, //
            EnumBasicPermissions.CMIS_WRITE.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_GET_ALL_VERSIONS_VERSION_SERIES, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_GET_OBJECT_RELATIONSHIPS_OBJECT, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_ADD_POLICY_OBJECT, // 
            EnumBasicPermissions.CMIS_WRITE.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_ADD_POLICY_POLICY, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_REMOVE_POLICY_OBJECT, // 
            EnumBasicPermissions.CMIS_WRITE.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_REMOVE_POLICY_POLICY, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_GET_APPLIED_POLICIES_OBJECT, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_GET_ACL_OBJECT, //
            EnumBasicPermissions.CMIS_READ.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_APPLY_ACL_OBJECT, //
            EnumBasicPermissions.CMIS_WRITE.value()));
         info.setAclCapability(aclCapabilities);
         info.setPrincipalAnonymous(ANONYMOUS);
         info.setPrincipalAnyone(ANY);
      }
      return info;
   }

   /**
    * {@inheritDoc}
    */
   public Entry getRootFolder() throws RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Get root folder.");
      return new EntryImpl(ROOT_FOLDER_ID, storage);
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<CmisTypeDefinitionType> getTypeChildren(String typeId, boolean includePropertyDefinition)
      throws TypeNotFoundException, RepositoryException
   {
      List<CmisTypeDefinitionType> list = new ArrayList<CmisTypeDefinitionType>();
      if (typeId == null)
      {
         list.add(getTypeDefinition(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value(), includePropertyDefinition));
         list.add(getTypeDefinition(EnumBaseObjectTypeIds.CMIS_FOLDER.value(), includePropertyDefinition));
         list.add(getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value(), includePropertyDefinition));
         list.add(getTypeDefinition(EnumBaseObjectTypeIds.CMIS_POLICY.value(), includePropertyDefinition));
      }
      else
      {
         for (CmisTypeDefinitionType type : types.values())
         {
            if (typeId.equals(type.getParentId()))
               list.add(getTypeDefinition(type.getId(), includePropertyDefinition));
         }
      }
      return new SimpleItemsIterator<CmisTypeDefinitionType>(list);
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisTypeContainer> getTypeDescendants(String typeId, int depth, boolean includePropertyDefinition)
      throws TypeNotFoundException, RepositoryException
   {
      List<CmisTypeContainer> list = new ArrayList<CmisTypeContainer>();
      if (typeId == null)
      {
         // Check all four root types with depth -1.
         // In this case will got all types and its descendants. 
         CmisTypeContainer docContainer = new CmisTypeContainer();
         docContainer.setType(getTypeDefinition(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value()));
         docContainer.getChildren().addAll(
            getTypeDescendants(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value(), -1, includePropertyDefinition));
         list.add(docContainer);

         CmisTypeContainer folderContainer = new CmisTypeContainer();
         folderContainer.setType(getTypeDefinition(EnumBaseObjectTypeIds.CMIS_FOLDER.value()));
         folderContainer.getChildren().addAll(
            getTypeDescendants(EnumBaseObjectTypeIds.CMIS_FOLDER.value(), -1, includePropertyDefinition));
         list.add(folderContainer);

         CmisTypeContainer relationshipContainer = new CmisTypeContainer();
         relationshipContainer.setType(getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value()));
         relationshipContainer.getChildren().addAll(
            getTypeDescendants(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value(), -1, includePropertyDefinition));
         list.add(relationshipContainer);

         CmisTypeContainer policyContainer = new CmisTypeContainer();
         policyContainer.setType(getTypeDefinition(EnumBaseObjectTypeIds.CMIS_POLICY.value()));
         policyContainer.getChildren().addAll(
            getTypeDescendants(EnumBaseObjectTypeIds.CMIS_POLICY.value(), -1, includePropertyDefinition));
         list.add(policyContainer);
      }
      else
      {

         EnumBaseObjectTypeIds scope = types.get(typeId).getBaseId();
         Map<Integer, List<CmisTypeDefinitionType>> cache = new HashMap<Integer, List<CmisTypeDefinitionType>>();

         CmisTypeDefinitionType point = types.get(typeId);
         // Should be skipped when traversing types list because they are
         //parents(direct and not) for discovering type.
         List<String> skipped = new ArrayList<String>();
         while (point.getParentId() != null)
         {
            point = types.get(point.getParentId());
            skipped.add(point.getId());
         }

         for (CmisTypeDefinitionType type : types.values())
         {
            if (scope != type.getBaseId() || skipped.contains(type.getId()))
               continue;

            // Traverse up hierarchy to to check is it in scope of depth.
            int level = 0;
            String current = type.getId();
            while (!typeId.equals(current))
            {
               current = types.get(current).getParentId();
               level++;
            }
            if ((depth == -1 || level <= depth) && level != 0)
            {
               List<CmisTypeDefinitionType> temp = cache.get(level);
               if (temp == null)
               {
                  temp = new ArrayList<CmisTypeDefinitionType>();
                  cache.put(level, temp);
               }
               CmisTypeDefinitionType typeDefinition = getTypeDefinition(type.getId(), includePropertyDefinition);
               temp.add(typeDefinition);
            }
         }
         if (cache.get(1) != null)
         {
            for (CmisTypeDefinitionType t : cache.get(1))
            {
               CmisTypeContainer containerType = new CmisTypeContainer();
               containerType.setType(t);
               addTypeDescendants(containerType, 2, cache);
               list.add(containerType);
            }
         }
      }

      return list;
   }

   /**
    * {@inheritDoc}
    */
   public VersionSeries getVersionSeries(String versionSeriesId) throws ObjectNotFoundException, RepositoryException
   {
      Set<String> vs = storage.getVersions().get(versionSeriesId);
      if (vs == null)
         throw new ObjectNotFoundException("Version series " + versionSeriesId + " does not exists.");
      return new VersionSeriesImpl(versionSeriesId, storage);
   }

   /**
    * {@inheritDoc}
    */
   public boolean isMatch(Entry entry, String expected) throws RepositoryException
   {
      if (changeTokenFeature)
         return expected != null && expected.equals(entry.getString(CMIS.CHANGE_TOKEN));
      // If change token feature is disabled don't check anything. 
      // We are not care about change tokens at all.
      return true;
   }

   /**
    * {@inheritDoc}
    */
   public void moveObject(String objectId, String destinationFolderId, String sourceFolderId)
      throws ConstraintException, ObjectNotFoundException, RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Move object " + objectId + " to new folder " + destinationFolderId);

      EntryImpl object = (EntryImpl)getObjectById(objectId);

      if (!object.getType().isFileable())
      {
         String msg = "Object '" + objectId + "' is not fileable.";
         throw new ConstraintException(msg);
      }

      EntryImpl destinationFolder = (EntryImpl)getObjectById(destinationFolderId);
      if (destinationFolder.getScope() != EnumBaseObjectTypeIds.CMIS_FOLDER)
      {
         String msg = "Target object '" + destinationFolderId + "' is not a folder.";
         throw new ConstraintException(msg);
      }

      if (sourceFolderId != null)
      {
         EntryImpl sourceFolder = (EntryImpl)getObjectById(sourceFolderId);
         if (sourceFolder.getScope() != EnumBaseObjectTypeIds.CMIS_FOLDER)
         {
            String msg = "Source object '" + sourceFolderId + "' is not a folder.";
            throw new ConstraintException(msg);
         }
         storage.getParents().get(objectId).remove(sourceFolderId);
         storage.getChildren().get(sourceFolderId).remove(objectId);
      }
      else
      {
         // remove this object as children from all parents
         Set<String> parentIds = storage.getParents().get(objectId);
         for (String parentId : parentIds)
            storage.getChildren().get(parentId).remove(objectId);

         // remove parents for this object
         storage.getParents().remove(objectId);
         storage.getParents().put(objectId, new HashSet<String>());
      }
      // add parents for the moved object
      storage.getParents().get(objectId).add(destinationFolderId);
      // add moved object to the children
      storage.getChildren().get(destinationFolderId).add(objectId);
   }

   /**
    * {@inheritDoc}
    */
   public void removeRenditions(Entry entry) throws RepositoryException
   {
      // TODO Auto-generated method stub
   }

   /**
    * {@inheritDoc}
    */
   public void removeRenditions(String objectId) throws ObjectNotFoundException, RepositoryException
   {
      // TODO Auto-generated method stub
   }

   /**
    * Add descendants for the type.
    * 
    * @param containerType the CMIS type container
    * @param level discovering depth
    * @param cache map for adding types 
    */
   private void addTypeDescendants(CmisTypeContainer containerType, int level,
      Map<Integer, List<CmisTypeDefinitionType>> cache)
   {
      if (cache.get(level) != null)
      {
         for (CmisTypeDefinitionType type : cache.get(level))
         {
            CmisTypeContainer t = new CmisTypeContainer();
            t.setType(type);
            if (containerType.getType().getId().equals(type.getParentId()))
               containerType.getChildren().add(t);
            addTypeDescendants(t, level + 1, cache);
         }
      }
   }

   /**
    * Create permission mapping object.
    * 
    * @param actionsKey the enumeration for allowable actions key
    * @param permissions the string array of permissions
    * @return CmisPermissionMapping
    */
   private CmisPermissionMapping createPermissionMapping(EnumAllowableActionsKey actionsKey, String... permissions)
   {
      if (permissions == null)
         throw new NullPointerException("permissions is null.");
      if (actionsKey == null)
         throw new NullPointerException("actionsKey is null.");
      CmisPermissionMapping mapping = new CmisPermissionMapping();
      mapping.setKey(actionsKey);
      if (permissions.length == 0)
      {
         mapping.getPermission().add(EnumBasicPermissions.CMIS_ALL.value());
      }
      else
      {
         for (String permission : permissions)
            mapping.getPermission().add(permission);
      }
      return mapping;
   }

   public QueryHandler getQueryHandler() throws RepositoryException
   {
      // TODO Auto-generated method stub
      return null;
   }

}
