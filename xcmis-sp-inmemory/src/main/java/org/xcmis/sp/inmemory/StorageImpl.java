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

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.xcmis.search.InvalidQueryException;
import org.xcmis.search.SearchService;
import org.xcmis.search.SearchServiceException;
import org.xcmis.search.Visitors;
import org.xcmis.search.config.IndexConfiguration;
import org.xcmis.search.config.SearchServiceConfiguration;
import org.xcmis.search.content.command.InvocationContext;
import org.xcmis.search.model.column.Column;
import org.xcmis.search.model.source.SelectorName;
import org.xcmis.search.parser.CmisQueryParser;
import org.xcmis.search.parser.QueryParser;
import org.xcmis.search.query.QueryExecutionException;
import org.xcmis.search.result.ScoredRow;
import org.xcmis.search.value.SlashSplitter;
import org.xcmis.search.value.ToStringNameConverter;
import org.xcmis.sp.inmemory.query.CmisContentReader;
import org.xcmis.sp.inmemory.query.CmisSchema;
import org.xcmis.sp.inmemory.query.CmisSchemaTableResolver;
import org.xcmis.sp.inmemory.query.IndexListener;
import org.xcmis.spi.BaseItemsIterator;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.DocumentData;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.PermissionService;
import org.xcmis.spi.PolicyData;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.RenditionManager;
import org.xcmis.spi.Storage;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.model.ACLCapability;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.AccessControlPropagation;
import org.xcmis.spi.model.AllowableActions;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.CapabilityACL;
import org.xcmis.spi.model.CapabilityChanges;
import org.xcmis.spi.model.CapabilityContentStreamUpdatable;
import org.xcmis.spi.model.CapabilityJoin;
import org.xcmis.spi.model.CapabilityQuery;
import org.xcmis.spi.model.CapabilityRendition;
import org.xcmis.spi.model.ChangeEvent;
import org.xcmis.spi.model.ContentStreamAllowed;
import org.xcmis.spi.model.Permission;
import org.xcmis.spi.model.PermissionMapping;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.Rendition;
import org.xcmis.spi.model.RepositoryCapabilities;
import org.xcmis.spi.model.RepositoryInfo;
import org.xcmis.spi.model.SupportedPermissions;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.UnfileObject;
import org.xcmis.spi.model.Updatability;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.model.Permission.BasicPermissions;
import org.xcmis.spi.query.Query;
import org.xcmis.spi.query.Result;
import org.xcmis.spi.query.Score;
import org.xcmis.spi.utils.CmisDocumentReaderService;
import org.xcmis.spi.utils.CmisUtils;
import org.xcmis.spi.utils.MimeType;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: StorageImpl.java 804 2010-04-16 16:48:59Z
 *          alexey.zavizionov@gmail.com $
 */
public class StorageImpl implements Storage
{
   private static final Log LOG = ExoLogger.getLogger(StorageImpl.class);

   public static String generateId()
   {
      return UUID.randomUUID().toString();
   }

   final Map<String, Entry> entries;

   final Map<String, Set<String>> children;

   final Map<String, Set<String>> parents;

   final Set<String> unfiled;

   final Map<String, Set<String>> relationships;

   final Map<String, List<String>> versions;

   final Map<String, String> workingCopies;

   final Map<String, TypeDefinition> types;

   final Map<String, Set<String>> typeChildren;

   private RepositoryInfo repositoryInfo;

   /** Searche service. */
   final SearchService searchService;

   /** Cmis query parser. */
   final QueryParser cmisQueryParser;

   /** The rendition manager. */
   protected RenditionManager renditionManager;

   private final StorageConfiguration configuration;

   final IndexListener indexListener;

   static final String ROOT_FOLDER_ID = "abcdef12-3456-7890-0987-654321fedcba";

   static final Set<String> EMPTY_PARENTS = Collections.emptySet();

   private final long maxStorageMemSize;

   private final int maxItemsNumber;

   private PermissionService permissionService;

   public StorageImpl(StorageConfiguration configuration, RenditionManager manager, PermissionService permissionService)
   {
      this(configuration);
      this.renditionManager = manager;
      this.permissionService = permissionService;
   }

   public StorageImpl(StorageConfiguration configuration)
   {
      this.configuration = configuration;

      this.maxStorageMemSize =
         configuration.getProperties().get(StorageConfiguration.MAX_STORAGE_MEM_SIZE) == null
            ? StorageConfiguration.DEFAULT_MAX_STORAGE_MEM_SIZE : StorageConfiguration.parseNumber(
               (String)configuration.getProperties().get(StorageConfiguration.MAX_STORAGE_MEM_SIZE)).longValue();

      this.maxItemsNumber =
         configuration.getProperties().get(StorageConfiguration.MAX_ITEMS_NUMBER) == null
            ? StorageConfiguration.DEFAULT_MAX_STORAGE_NUMBER_ITEMS : StorageConfiguration.parseNumber(
               (String)configuration.getProperties().get(StorageConfiguration.MAX_ITEMS_NUMBER)).intValue();

      this.entries = new ConcurrentHashMap<String, Entry>();
      this.children = new ConcurrentHashMap<String, Set<String>>();
      this.parents = new ConcurrentHashMap<String, Set<String>>();
      this.versions = new ConcurrentHashMap<String, List<String>>();
      this.workingCopies = new ConcurrentHashMap<String, String>();
      this.unfiled = new CopyOnWriteArraySet<String>();
      this.relationships = new ConcurrentHashMap<String, Set<String>>();
      this.types = new ConcurrentHashMap<String, TypeDefinition>();

      PermissionMapping permissionMapping = new PermissionMapping();
      permissionMapping.put(PermissionMapping.CAN_GET_DESCENDENTS_FOLDER, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_GET_FOLDER_TREE_FOLDER, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_GET_CHILDREN_FOLDER, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_GET_OBJECT_PARENTS_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_GET_FOLDER_PARENT_FOLDER, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_CREATE_DOCUMENT_FOLDER, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_CREATE_FOLDER_FOLDER, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_CREATE_RELATIONSHIP_SOURCE, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_CREATE_RELATIONSHIP_TARGET, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_GET_PROPERTIES_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_GET_CONTENT_STREAM_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_GET_RENDITIONS_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_UPDATE_PROPERTIES_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value(), BasicPermissions.CMIS_WRITE.value()));
      permissionMapping.put(PermissionMapping.CAN_MOVE_OBJECT_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value(), BasicPermissions.CMIS_WRITE.value()));
      permissionMapping.put(PermissionMapping.CAN_MOVE_OBJECT_TARGET, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_MOVE_OBJECT_SOURCE, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_DELETE_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value(), BasicPermissions.CMIS_WRITE.value()));
      permissionMapping.put(PermissionMapping.CAN_DELETE_TREE_FOLDER, //
         Arrays.asList(BasicPermissions.CMIS_READ.value(), BasicPermissions.CMIS_WRITE.value()));
      permissionMapping.put(PermissionMapping.CAN_SET_CONTENT_DOCUMENT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value(), BasicPermissions.CMIS_WRITE.value()));
      permissionMapping.put(PermissionMapping.CAN_DELETE_CONTENT_DOCUMENT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value(), BasicPermissions.CMIS_WRITE.value()));
      permissionMapping.put(PermissionMapping.CAN_ADD_TO_FOLDER_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_ADD_TO_FOLDER_FOLDER, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_REMOVE_OBJECT_FROM_FOLDER_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_REMOVE_OBJECT_FROM_FOLDER_FOLDER, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_CHECKOUT_DOCUMENT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value(), BasicPermissions.CMIS_WRITE.value()));
      permissionMapping.put(PermissionMapping.CAN_CANCEL_CHECKOUT_DOCUMENT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value(), BasicPermissions.CMIS_WRITE.value()));
      permissionMapping.put(PermissionMapping.CAN_CHECKIN_DOCUMENT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value(), BasicPermissions.CMIS_WRITE.value()));
      permissionMapping.put(PermissionMapping.CAN_GET_ALL_VERSIONS_DOCUMENT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_GET_OBJECT_RELATIONSHIPS_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_ADD_POLICY_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value(), BasicPermissions.CMIS_WRITE.value()));
      permissionMapping.put(PermissionMapping.CAN_ADD_POLICY_POLICY, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_REMOVE_POLICY_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_WRITE.value()));
      permissionMapping.put(PermissionMapping.CAN_REMOVE_POLICY_POLICY, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_GET_APPLIED_POLICIES_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_GET_ACL_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value()));
      permissionMapping.put(PermissionMapping.CAN_APPLY_ACL_OBJECT, //
         Arrays.asList(BasicPermissions.CMIS_READ.value(), BasicPermissions.CMIS_WRITE.value()));

      List<Permission> supportedPermissions = new ArrayList<Permission>(4);
      for (BasicPermissions b : BasicPermissions.values())
      {
         supportedPermissions.add(new Permission(b.value(), ""));
      }

      repositoryInfo =
         new RepositoryInfo(getId(), getId(), ROOT_FOLDER_ID, CmisConstants.SUPPORTED_VERSION,
            new RepositoryCapabilities(CapabilityACL.MANAGE, CapabilityChanges.NONE,
               CapabilityContentStreamUpdatable.ANYTIME, CapabilityJoin.NONE, CapabilityQuery.BOTHCOMBINED,
               CapabilityRendition.READ, false, true, true, true, false, true, true, false), new ACLCapability(
               permissionMapping, Collections.unmodifiableList(supportedPermissions),
               AccessControlPropagation.OBJECTONLY, SupportedPermissions.BASIC), "anonymous", "any", null, null, true,
            null, "eXo", "xCMIS (eXo InMemory SP)", "1.0", null);

      types.put("cmis:document", //
         new TypeDefinition("cmis:document", BaseType.DOCUMENT, "cmis:document", "cmis:document", "", null,
            "cmis:document", "Cmis Document Type", true, true, true, true, true, true, true, true, null, null,
            ContentStreamAllowed.ALLOWED, null));

      types.put("cmis:folder", //
         new TypeDefinition("cmis:folder", BaseType.FOLDER, "cmis:folder", "cmis:folder", "", null, "cmis:folder",
            "Cmis Folder type", true, true, true, false, true, true, true, false, null, null,
            ContentStreamAllowed.NOT_ALLOWED, null));

      types.put("cmis:policy", //
         new TypeDefinition("cmis:policy", BaseType.POLICY, "cmis:policy", "cmis:policy", "", null, "cmis:policy",
            "Cmis Policy type", true, false, true, false, true, true, true, false, null, null,
            ContentStreamAllowed.NOT_ALLOWED, null));

      types.put("cmis:relationship", //
         new TypeDefinition("cmis:relationship", BaseType.RELATIONSHIP, "cmis:relationship", "cmis:relationship", "",
            null, "cmis:relationship", "Cmis Relationship type.", true, false, true, false, true, true, true, false,
            null, null, ContentStreamAllowed.NOT_ALLOWED, null));

      typeChildren = new ConcurrentHashMap<String, Set<String>>();
      typeChildren.put("cmis:document", new HashSet<String>());
      typeChildren.put("cmis:folder", new HashSet<String>());
      typeChildren.put("cmis:policy", new HashSet<String>());
      typeChildren.put("cmis:relationship", new HashSet<String>());

      Map<String, Value> root = new ConcurrentHashMap<String, Value>();
      root.put(CmisConstants.NAME, new StringValue(""));
      root.put(CmisConstants.OBJECT_ID, new StringValue(ROOT_FOLDER_ID));
      root.put(CmisConstants.OBJECT_TYPE_ID, new StringValue("cmis:folder"));
      root.put(CmisConstants.BASE_TYPE_ID, new StringValue(BaseType.FOLDER.value()));
      root.put(CmisConstants.CREATION_DATE, new DateValue(Calendar.getInstance()));
      root.put(CmisConstants.CREATED_BY, new StringValue("system"));
      root.put(CmisConstants.LAST_MODIFICATION_DATE, new DateValue(Calendar.getInstance()));
      root.put(CmisConstants.LAST_MODIFIED_BY, new StringValue("system"));

      // TODO : concurrent
      Map<String, Set<String>> pm = new HashMap<String, Set<String>>();
      Set<String> perms = new HashSet<String>();
      perms.add("cmis:all");
      pm.put("any", perms);

      Entry rootEntry = new Entry(root, null, pm);

      entries.put(rootEntry.getId(), rootEntry);
      parents.put(ROOT_FOLDER_ID, EMPTY_PARENTS);
      children.put(ROOT_FOLDER_ID, new CopyOnWriteArraySet<String>());

      this.searchService = getInitializedSearchService();
      this.indexListener = new IndexListener(this, searchService);
      this.cmisQueryParser = new CmisQueryParser();
   }

   /**
    * {@inheritDoc}
    */
   public AllowableActions calculateAllowableActions(ObjectData object)
   {
      AllowableActions actions =
         permissionService.calculateAllowableActions(object, ConversationState.getCurrent().getIdentity(),
            getRepositoryInfo());
      return actions;
   }

   /**
    * {@inheritDoc}
    */
   public DocumentData copyDocument(DocumentData source, FolderData parent, Map<String, Property<?>> properties,
      List<AccessControlEntry> acl, Collection<PolicyData> policies, VersioningState versioningState)
      throws ConstraintException, NameConstraintViolationException, StorageException
   {
      String name = null;
      Property<?> nameProperty = properties.get(CmisConstants.NAME);
      if (nameProperty != null && nameProperty.getValues().size() > 0)
      {
         name = (String)nameProperty.getValues().get(0);
      }
      if (name == null || name.length() == 0)
      {
         name = source.getName();
      }

      if (parent != null)
      {
         for (ItemsIterator<ObjectData> iterator = parent.getChildren(null); iterator.hasNext();)
         {
            if (name.equals(iterator.next().getName()))
            {
               throw new NameConstraintViolationException("Object with name " + name
                  + " already exists in parent folder.");
            }
         }
      }

      Entry copyEntry = new Entry();

      TypeDefinition sourceType = source.getTypeDefinition();
      TypeDefinition typeDefinition =
         new TypeDefinition(sourceType.getId(), sourceType.getBaseId(), sourceType.getQueryName(), sourceType
            .getLocalName(), sourceType.getLocalNamespace(), sourceType.getParentId(), sourceType.getDisplayName(),
            sourceType.getDescription(), sourceType.isCreatable(), sourceType.isFileable(), sourceType.isQueryable(),
            sourceType.isFulltextIndexed(), sourceType.isIncludedInSupertypeQuery(), sourceType.isControllablePolicy(),
            sourceType.isControllableACL(), sourceType.isVersionable(), sourceType.getAllowedSourceTypes(), sourceType
               .getAllowedTargetTypes(), sourceType.getContentStreamAllowed(), PropertyDefinitions.getAll(sourceType
               .getId()));

      copyEntry.setValue(CmisConstants.OBJECT_TYPE_ID, new StringValue(typeDefinition.getId()));
      copyEntry.setValue(CmisConstants.BASE_TYPE_ID, new StringValue(typeDefinition.getBaseId().value()));
      String docId = StorageImpl.generateId();
      String verSerId = StorageImpl.generateId();
      copyEntry.setValue(CmisConstants.OBJECT_ID, new StringValue(docId));
      copyEntry.setValue(CmisConstants.VERSION_SERIES_ID, new StringValue(verSerId));
      String userId = getCurrentUser();
      copyEntry.setValue(CmisConstants.CREATED_BY, new StringValue(userId));
      copyEntry.setValue(CmisConstants.LAST_MODIFIED_BY, new StringValue(userId));
      Calendar cal = Calendar.getInstance();
      copyEntry.setValue(CmisConstants.CREATION_DATE, new DateValue(cal));
      copyEntry.setValue(CmisConstants.LAST_MODIFICATION_DATE, new DateValue(cal));
      copyEntry.setValue(CmisConstants.IS_LATEST_VERSION, new BooleanValue(true));
      copyEntry.setValue(CmisConstants.IS_MAJOR_VERSION, new BooleanValue(versioningState == VersioningState.MAJOR));
      copyEntry.setValue(CmisConstants.IS_LATEST_MAJOR_VERSION, new BooleanValue(
         versioningState == VersioningState.MAJOR));

      // TODO : support for checked-out initial state
      copyEntry.setValue(CmisConstants.VERSION_LABEL, new StringValue(PropertyDefinitions.LATEST_LABEL));
      copyEntry.setValue(CmisConstants.IS_VERSION_SERIES_CHECKED_OUT, new BooleanValue(false));
      //      copyEntry.setValue(CmisConstants.VERSION_SERIES_CHECKED_OUT_ID, new StringValue());
      //      copyEntry.setValue(CmisConstants.VERSION_SERIES_CHECKED_OUT_BY, new StringValue());

      try
      {
         ContentStream content = source.getContentStream();
         if (content != null)
         {
            ByteArrayValue cv = ByteArrayValue.fromStream(content.getStream());
            MimeType mimeType = content.getMediaType();
            copyEntry.setValue(CmisConstants.CONTENT_STREAM_MIME_TYPE, new StringValue(mimeType.getBaseType()));
            String charset = mimeType.getParameter(CmisConstants.CHARSET);
            if (charset != null)
            {
               copyEntry.setValue(CmisConstants.CHARSET, new StringValue(charset));
            }
            copyEntry.setValue(CmisConstants.CONTENT_STREAM_LENGTH, new IntegerValue(BigInteger
               .valueOf(cv.getBytes().length)));
         }
      }
      catch (IOException ioe)
      {
         throw new CmisRuntimeException("Unable copy content for new document. " + ioe.getMessage(), ioe);
      }

      for (Property<?> property : properties.values())
      {
         PropertyDefinition<?> definition = typeDefinition.getPropertyDefinition(property.getId());
         Updatability updatability = definition.getUpdatability();
         if (updatability == Updatability.READWRITE || updatability == Updatability.ONCREATE)
         {
            copyEntry.setProperty(property);
         }
      }

      if (policies != null && policies.size() > 0)
      {
         for (PolicyData policy : policies)
         {
            copyEntry.addPolicy(policy);
         }
      }

      if (acl != null && acl.size() > 0)
      {
         CmisUtils.addAclToPermissionMap(copyEntry.getPermissions(), acl);
      }

      if (parent != null)
      {
         children.get(parent.getObjectId()).add(docId);
         Set<String> set = new CopyOnWriteArraySet<String>();
         set.add(parent.getObjectId());
         parents.put(docId, set);
      }
      else
      {
         unfiled.add(docId);
         parents.put(docId, new CopyOnWriteArraySet<String>());
      }
      List<String> set = new CopyOnWriteArrayList<String>();
      set.add(docId);
      versions.put(verSerId, set);
      entries.put(docId, copyEntry);

      DocumentDataImpl copy = new DocumentDataImpl(copyEntry, typeDefinition, this);

      if (indexListener != null)
      {
         indexListener.created(copy);
      }

      return copy;
   }

   /**
    * {@inheritDoc}
    */
   public DocumentData createDocument(FolderData parent, TypeDefinition typeDefinition,
      Map<String, Property<?>> properties, ContentStream content, List<AccessControlEntry> acl,
      Collection<PolicyData> policies, VersioningState versioningState) throws ConstraintException,
      NameConstraintViolationException, IOException, StorageException
   {
      String name = null;
      Property<?> nameProperty = properties.get(CmisConstants.NAME);
      if (nameProperty != null && nameProperty.getValues().size() > 0)
      {
         name = (String)nameProperty.getValues().get(0);
      }
      if (name == null && content != null)
      {
         name = content.getFileName();
      }
      if (name == null || name.length() == 0)
      {
         throw new NameConstraintViolationException("Name for new document must be provided.");
      }

      if (parent != null)
      {
         for (ItemsIterator<ObjectData> iterator = parent.getChildren(null); iterator.hasNext();)
         {
            if (name.equals(iterator.next().getName()))
            {
               throw new NameConstraintViolationException("Object with name " + name
                  + " already exists in parent folder.");
            }
         }
      }

      Entry docEntry = new Entry();

      docEntry.setValue(CmisConstants.OBJECT_TYPE_ID, new StringValue(typeDefinition.getId()));
      docEntry.setValue(CmisConstants.BASE_TYPE_ID, new StringValue(typeDefinition.getBaseId().value()));
      String docId = StorageImpl.generateId();
      String verSerId = StorageImpl.generateId();
      docEntry.setValue(CmisConstants.OBJECT_ID, new StringValue(docId));
      docEntry.setValue(CmisConstants.VERSION_SERIES_ID, new StringValue(verSerId));
      String userId = getCurrentUser();
      docEntry.setValue(CmisConstants.CREATED_BY, new StringValue(userId));
      docEntry.setValue(CmisConstants.LAST_MODIFIED_BY, new StringValue(userId));
      Calendar cal = Calendar.getInstance();
      docEntry.setValue(CmisConstants.CREATION_DATE, new DateValue(cal));
      docEntry.setValue(CmisConstants.LAST_MODIFICATION_DATE, new DateValue(cal));
      docEntry.setValue(CmisConstants.IS_LATEST_VERSION, new BooleanValue(true));
      docEntry.setValue(CmisConstants.IS_MAJOR_VERSION, new BooleanValue(versioningState == VersioningState.MAJOR));
      docEntry.setValue(CmisConstants.IS_LATEST_MAJOR_VERSION, new BooleanValue(
         versioningState == VersioningState.MAJOR));

      // TODO : support for checked-out initial state
      docEntry.setValue(CmisConstants.VERSION_LABEL, new StringValue(PropertyDefinitions.LATEST_LABEL));
      docEntry.setValue(CmisConstants.IS_VERSION_SERIES_CHECKED_OUT, new BooleanValue(false));
      //      docEntry.setValue(CmisConstants.VERSION_SERIES_CHECKED_OUT_ID, new StringValue());
      //      docEntry.setValue(CmisConstants.VERSION_SERIES_CHECKED_OUT_BY, new StringValue());

      if (content != null)
      {
         ByteArrayValue cv = ByteArrayValue.fromStream(content.getStream());
         docEntry.setValue(PropertyDefinitions.CONTENT, cv);
         MimeType mimeType = content.getMediaType();
         docEntry.setValue(CmisConstants.CONTENT_STREAM_MIME_TYPE, new StringValue(mimeType.getBaseType()));
         String charset = mimeType.getParameter(CmisConstants.CHARSET);
         if (charset != null)
         {
            docEntry.setValue(CmisConstants.CHARSET, new StringValue(charset));
         }
         docEntry.setValue(CmisConstants.CONTENT_STREAM_LENGTH, new IntegerValue(BigInteger
            .valueOf(cv.getBytes().length)));
      }

      for (Property<?> property : properties.values())
      {
         PropertyDefinition<?> definition = typeDefinition.getPropertyDefinition(property.getId());
         Updatability updatability = definition.getUpdatability();
         if (updatability == Updatability.READWRITE || updatability == Updatability.ONCREATE)
         {
            docEntry.setProperty(property);
         }
      }

      if (policies != null && policies.size() > 0)
      {
         for (PolicyData policy : policies)
         {
            docEntry.addPolicy(policy);
         }
      }

      if (acl != null && acl.size() > 0)
      {
         CmisUtils.addAclToPermissionMap(docEntry.getPermissions(), acl);
      }

      if (parent != null)
      {
         children.get(parent.getObjectId()).add(docId);
         Set<String> set = new CopyOnWriteArraySet<String>();
         set.add(parent.getObjectId());
         parents.put(docId, set);
      }
      else
      {
         unfiled.add(docId);
         parents.put(docId, new CopyOnWriteArraySet<String>());
      }
      List<String> set = new CopyOnWriteArrayList<String>();
      set.add(docId);
      versions.put(verSerId, set);
      entries.put(docId, docEntry);

      DocumentDataImpl document = new DocumentDataImpl(docEntry, typeDefinition, this);

      if (indexListener != null)
      {
         indexListener.created(document);
      }

      return document;
   }

   /**
    * {@inheritDoc}
    */
   public FolderData createFolder(FolderData parent, TypeDefinition typeDefinition,
      Map<String, Property<?>> properties, List<AccessControlEntry> acl, Collection<PolicyData> policies)
      throws ConstraintException, NameConstraintViolationException, StorageException
   {
      if (parent == null)
      {
         throw new ConstraintException("Parent folder must be provided.");
      }

      String name = null;
      Property<?> nameProperty = properties.get(CmisConstants.NAME);
      if (nameProperty != null && nameProperty.getValues().size() > 0)
      {
         name = (String)nameProperty.getValues().get(0);
      }
      if (name == null || name.length() == 0)
      {
         throw new NameConstraintViolationException("Name for new folder must be provided.");
      }

      for (ItemsIterator<ObjectData> iterator = parent.getChildren(null); iterator.hasNext();)
      {
         if (name.equals(iterator.next().getName()))
         {
            throw new NameConstraintViolationException("Object with name " + name + " already exists in parent folder.");
         }
      }

      Entry folderEntry = new Entry();

      folderEntry.setValue(CmisConstants.OBJECT_TYPE_ID, new StringValue(typeDefinition.getId()));
      folderEntry.setValue(CmisConstants.BASE_TYPE_ID, new StringValue(typeDefinition.getBaseId().value()));
      String folderId = StorageImpl.generateId();
      folderEntry.setValue(CmisConstants.OBJECT_ID, new StringValue(folderId));
      String userId = getCurrentUser();
      folderEntry.setValue(CmisConstants.CREATED_BY, new StringValue(userId));
      folderEntry.setValue(CmisConstants.LAST_MODIFIED_BY, new StringValue(userId));
      Calendar cal = Calendar.getInstance();
      folderEntry.setValue(CmisConstants.CREATION_DATE, new DateValue(cal));
      folderEntry.setValue(CmisConstants.LAST_MODIFICATION_DATE, new DateValue(cal));

      for (Property<?> property : properties.values())
      {
         PropertyDefinition<?> definition = typeDefinition.getPropertyDefinition(property.getId());
         Updatability updatability = definition.getUpdatability();
         if (updatability == Updatability.READWRITE || updatability == Updatability.ONCREATE)
         {
            folderEntry.setProperty(property);
         }
      }

      if (policies != null && policies.size() > 0)
      {
         for (PolicyData policy : policies)
         {
            folderEntry.addPolicy(policy);
         }
      }

      if (acl != null && acl.size() > 0)
      {
         CmisUtils.addAclToPermissionMap(folderEntry.getPermissions(), acl);
      }

      children.get(parent.getObjectId()).add(folderId);
      Set<String> set = new CopyOnWriteArraySet<String>();
      set.add(parent.getObjectId());
      parents.put(folderId, set);
      entries.put(folderId, folderEntry);
      children.put(folderId, new CopyOnWriteArraySet<String>());

      FolderDataImpl folder = new FolderDataImpl(folderEntry, typeDefinition, this);

      if (indexListener != null)
      {
         indexListener.created(folder);
      }

      return folder;
   }

   /**
    * {@inheritDoc}
    */
   public PolicyData createPolicy(FolderData parent, TypeDefinition typeDefinition,
      Map<String, Property<?>> properties, List<AccessControlEntry> acl, Collection<PolicyData> policies)
      throws ConstraintException, NameConstraintViolationException, StorageException
   {
      String name = null;
      Property<?> nameProperty = properties.get(CmisConstants.NAME);
      if (nameProperty != null && nameProperty.getValues().size() > 0)
      {
         name = (String)nameProperty.getValues().get(0);
      }
      if (name == null || name.length() == 0)
      {
         throw new NameConstraintViolationException("Name for new policy must be provided.");
      }

      Entry policyEntry = new Entry();

      policyEntry.setValue(CmisConstants.OBJECT_TYPE_ID, new StringValue(typeDefinition.getId()));
      policyEntry.setValue(CmisConstants.BASE_TYPE_ID, new StringValue(typeDefinition.getBaseId().value()));
      String policyId = StorageImpl.generateId();
      policyEntry.setValue(CmisConstants.OBJECT_ID, new StringValue(policyId));
      String userId = getCurrentUser();
      policyEntry.setValue(CmisConstants.CREATED_BY, new StringValue(userId));
      policyEntry.setValue(CmisConstants.LAST_MODIFIED_BY, new StringValue(userId));
      Calendar cal = Calendar.getInstance();
      policyEntry.setValue(CmisConstants.CREATION_DATE, new DateValue(cal));
      policyEntry.setValue(CmisConstants.LAST_MODIFICATION_DATE, new DateValue(cal));

      for (Property<?> property : properties.values())
      {
         PropertyDefinition<?> definition = typeDefinition.getPropertyDefinition(property.getId());
         Updatability updatability = definition.getUpdatability();
         if (updatability == Updatability.READWRITE || updatability == Updatability.ONCREATE)
         {
            policyEntry.setProperty(property);
         }
      }

      if (policies != null && policies.size() > 0)
      {
         for (PolicyData policy : policies)
         {
            policyEntry.addPolicy(policy);
         }
      }

      if (acl != null && acl.size() > 0)
      {
         CmisUtils.addAclToPermissionMap(policyEntry.getPermissions(), acl);
      }

      parents.put(policyId, EMPTY_PARENTS);
      entries.put(policyId, policyEntry);

      PolicyDataImpl policy = new PolicyDataImpl(policyEntry, typeDefinition, this);

      if (indexListener != null)
      {
         indexListener.created(policy);
      }

      return policy;
   }

   /**
    * {@inheritDoc}
    */
   public RelationshipData createRelationship(ObjectData source, ObjectData target, TypeDefinition typeDefinition,
      Map<String, Property<?>> properties, List<AccessControlEntry> acl, Collection<PolicyData> policies)
      throws NameConstraintViolationException, StorageException
   {
      String name = null;
      Property<?> nameProperty = properties.get(CmisConstants.NAME);
      if (nameProperty != null && nameProperty.getValues().size() > 0)
      {
         name = (String)nameProperty.getValues().get(0);
      }
      if (name == null || name.length() == 0)
      {
         throw new NameConstraintViolationException("Name for new relationship must be provided.");
      }

      Entry relationshipEntry = new Entry();

      relationshipEntry.setValue(CmisConstants.OBJECT_TYPE_ID, new StringValue(typeDefinition.getId()));
      relationshipEntry.setValue(CmisConstants.BASE_TYPE_ID, new StringValue(typeDefinition.getBaseId().value()));
      String relationshipId = StorageImpl.generateId();
      relationshipEntry.setValue(CmisConstants.OBJECT_ID, new StringValue(relationshipId));
      String userId = getCurrentUser();
      relationshipEntry.setValue(CmisConstants.CREATED_BY, new StringValue(userId));
      relationshipEntry.setValue(CmisConstants.LAST_MODIFIED_BY, new StringValue(userId));
      Calendar cal = Calendar.getInstance();
      relationshipEntry.setValue(CmisConstants.CREATION_DATE, new DateValue(cal));
      relationshipEntry.setValue(CmisConstants.LAST_MODIFICATION_DATE, new DateValue(cal));
      relationshipEntry.setValue(CmisConstants.SOURCE_ID, new StringValue(source.getObjectId()));
      relationshipEntry.setValue(CmisConstants.TARGET_ID, new StringValue(target.getObjectId()));

      for (Property<?> property : properties.values())
      {
         PropertyDefinition<?> definition = typeDefinition.getPropertyDefinition(property.getId());
         Updatability updatability = definition.getUpdatability();
         if (updatability == Updatability.READWRITE || updatability == Updatability.ONCREATE)
         {
            relationshipEntry.setProperty(property);
         }
      }

      if (policies != null && policies.size() > 0)
      {
         for (PolicyData policy : policies)
         {
            relationshipEntry.addPolicy(policy);
         }
      }

      if (acl != null && acl.size() > 0)
      {
         CmisUtils.addAclToPermissionMap(relationshipEntry.getPermissions(), acl);
      }

      parents.put(relationshipId, EMPTY_PARENTS);
      entries.put(relationshipId, relationshipEntry);
      Set<String> sourceRels = relationships.get(source.getObjectId());
      if (sourceRels == null)
      {
         sourceRels = new CopyOnWriteArraySet<String>();
         relationships.put(source.getObjectId(), sourceRels);
      }
      sourceRels.add(relationshipId);
      Set<String> targetRels = relationships.get(target.getObjectId());
      if (targetRels == null)
      {
         targetRels = new CopyOnWriteArraySet<String>();
         relationships.put(target.getObjectId(), targetRels);
      }
      targetRels.add(relationshipId);

      RelationshipDataImpl relationship = new RelationshipDataImpl(relationshipEntry, typeDefinition, this);

      if (indexListener != null)
      {
         indexListener.created(relationship);
      }

      return relationship;
   }

   /**
    * {@inheritDoc}
    */
   public void deleteObject(ObjectData object, boolean deleteAllVersions) throws VersioningException,
      UpdateConflictException, StorageException
   {
      String objectId = object.getObjectId();

      ((BaseObjectData)object).delete();

      if (indexListener != null)
      {
         Set<String> removed = new HashSet<String>();
         removed.add(objectId);
         indexListener.removed(removed);
      }
   }

   /**
    * {@inheritDoc}
    */
   public Collection<String> deleteTree(FolderData folder, boolean deleteAllVersions, UnfileObject unfileObject,
      boolean continueOnFailure) throws UpdateConflictException
   {
      // TODO : unfile
      //      if (unfileObject != UnfileObject.DELETE)
      //      {
      //         throw new NotSupportedException("Parameter 'unfileObject' may not be other then 'DELETE'.");
      //      }

      Collection<String> failedToDelete = new ArrayList<String>();

      for (ItemsIterator<ObjectData> iterator = folder.getChildren(null); iterator.hasNext();)
      {
         ObjectData object = iterator.next();
         if (object.getBaseType() == BaseType.FOLDER)
         {
            deleteTree((FolderData)object, deleteAllVersions, unfileObject, continueOnFailure);
         }
         else
         {
            try
            {
               deleteObject(object, false);
            }
            catch (StorageException e)
            {
               if (continueOnFailure)
               {
                  failedToDelete.add(object.getObjectId());
               }
               else
               {
                  throw new CmisRuntimeException(e.getMessage(), e);
               }
            }
            catch (VersioningException e)
            {
               if (continueOnFailure)
               {
                  failedToDelete.add(object.getObjectId());
               }
               else
               {
                  throw new CmisRuntimeException(e.getMessage(), e);
               }
            }
         }
      }

      try
      {
         deleteObject(folder, false);
      }
      catch (StorageException e)
      {
         if (continueOnFailure)
         {
            failedToDelete.add(folder.getObjectId());
         }
         else
         {
            throw new CmisRuntimeException(e.getMessage(), e);
         }
      }
      catch (VersioningException e)
      {
         // should not happen for not versionable type
         if (continueOnFailure)
         {
            failedToDelete.add(folder.getObjectId());
         }
         else
         {
            throw new CmisRuntimeException(e.getMessage(), e);
         }
      }

      return failedToDelete;
   }

   /**
    * {@inheritDoc}
    */
   public Collection<DocumentData> getAllVersions(String versionSeriesId) throws ObjectNotFoundException
   {
      List<DocumentData> v = new ArrayList<DocumentData>();
      if (!workingCopies.containsKey(versionSeriesId) && !versions.containsKey(versionSeriesId))
      {
         throw new ObjectNotFoundException("Version series '" + versionSeriesId + "' does not exist.");
      }
      String pwc = workingCopies.get(versionSeriesId);
      if (pwc != null)
      {
         v.add((DocumentData)getObjectById(pwc));
      }
      for (String vId : versions.get(versionSeriesId))
      {
         v.add((DocumentData)getObjectById(vId));
      }
      Collections.reverse(v);
      return v;
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<ChangeEvent> getChangeLog(String changeLogToken) throws ConstraintException
   {
      // TODO
      return CmisUtils.emptyItemsIterator();
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<DocumentData> getCheckedOutDocuments(FolderData folder, String orderBy)
   {
      List<DocumentData> checkedOut = new ArrayList<DocumentData>();

      for (String pwcId : workingCopies.values())
      {
         DocumentData pwc = null;
         try
         {
            pwc = (DocumentData)getObjectById(pwcId);
         }
         catch (ObjectNotFoundException e)
         {
            LOG.warn("Object " + pwcId + " not found.");
            continue;
         }
         if (folder != null)
         {
            for (FolderData parent : pwc.getParents())
            {
               if (parent.getObjectId().equals(folder.getObjectId()))
               {
                  checkedOut.add(pwc);
               }
            }
         }
         else
         {
            checkedOut.add(pwc);
         }
      }
      return new BaseItemsIterator<DocumentData>(checkedOut);
   }

   /**
    * {@inheritDoc}
    */
   public String getId()
   {
      return configuration.getId();
   }

   /**
    * {@inheritDoc}
    */
   public ObjectData getObjectById(String objectId) throws ObjectNotFoundException
   {
      Entry entry = entries.get(objectId);
      if (entry == null)
      {
         throw new ObjectNotFoundException("Object '" + objectId + "' does not exist.");
      }
      BaseType baseType = entry.getBaseTypeId();
      String typeId = entry.getTypeId();
      TypeDefinition typeDefinition = null;
      try
      {
         typeDefinition = getTypeDefinition(typeId, true);
      }
      catch (TypeNotFoundException e)
      {
         throw new CmisRuntimeException(e.getMessage(), e);
      }
      switch (baseType)
      {
         case DOCUMENT :
            return new DocumentDataImpl(entry, typeDefinition, this);
         case FOLDER :
            return new FolderDataImpl(entry, typeDefinition, this);
         case POLICY :
            return new PolicyDataImpl(entry, typeDefinition, this);
         case RELATIONSHIP :
            return new RelationshipDataImpl(entry, typeDefinition, this);
      }
      // Must never happen.
      throw new CmisRuntimeException("Unknown base type. ");
   }

   /**
    * {@inheritDoc}
    */
   public ObjectData getObjectByPath(String path) throws ObjectNotFoundException
   {
      if (!path.startsWith("/"))
      {
         path = "/" + path;
      }
      StringTokenizer tokenizer = new StringTokenizer(path, "/");
      String point = StorageImpl.ROOT_FOLDER_ID;
      while (tokenizer.hasMoreTokens())
      {
         if (point == null)
         {
            break;
         }
         String segName = tokenizer.nextToken();
         Set<String> childrenIds = children.get(point);
         if (childrenIds == null || childrenIds.isEmpty())
         {
            point = null;
         }
         else
         {
            for (String id : childrenIds)
            {
               ObjectData seg = getObjectById(id);
               String name = seg.getName();
               if ((BaseType.FOLDER == seg.getBaseType() || !tokenizer.hasMoreElements()) && segName.equals(name))
               {
                  point = id;
                  break;
               }
               point = null;
            }
         }
      }

      if (point == null)
      {
         throw new ObjectNotFoundException("Path '" + path + "' not found.");
      }
      return getObjectById(point);
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<Rendition> getRenditions(ObjectData object)
   {
      if (renditionManager != null)
      {
         ItemsIterator<Rendition> renditions = renditionManager.getRenditions(object);
         return renditions;
      }
      return CmisUtils.emptyItemsIterator();
   }

   /**
    * {@inheritDoc}
    */
   public RepositoryInfo getRepositoryInfo()
   {
      return repositoryInfo;
   }

   /**
    * {@inheritDoc}
    */
   public ObjectData moveObject(ObjectData object, FolderData target, FolderData source)
      throws UpdateConflictException, VersioningException, NameConstraintViolationException, StorageException
   {
      String objectid = object.getObjectId();
      String sourceId = source.getObjectId();
      String targetId = target.getObjectId();
      children.get(sourceId).remove(objectid);
      children.get(targetId).add(objectid);
      parents.get(object.getObjectId()).remove(sourceId);
      parents.get(object.getObjectId()).add(targetId);
      try
      {
         return getObjectById(objectid);
      }
      catch (ObjectNotFoundException e)
      {
         throw new CmisRuntimeException("Unable get object after moving.");
      }
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<Result> query(Query query) throws InvalidArgumentException
   {
      try
      {
         org.xcmis.search.model.Query qom = cmisQueryParser.parseQuery(query.getStatement());
         List<ScoredRow> rows = searchService.execute(qom);
         //check if needed default sorting
         if (qom.getOrderings().size() == 0)
         {
            Set<SelectorName> selectorsReferencedBy = Visitors.getSelectorsReferencedBy(qom);
            Collections.sort(rows, new DocumentOrderResultSorter(selectorsReferencedBy.iterator().next().getName(),
               this));
         }
         return new QueryResultIterator(rows, qom);
      }
      catch (InvalidQueryException e)
      {
         throw new InvalidArgumentException(e.getLocalizedMessage(), e);
      }
      catch (QueryExecutionException e)
      {
         throw new CmisRuntimeException(e.getLocalizedMessage(), e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void unfileObject(ObjectData object)
   {
      Set<String> parentIds = parents.get(object.getObjectId());
      for (String id : parentIds)
      {
         children.get(id).remove(object.getObjectId());
      }
      parents.clear();
   }

   /**
    * {@inheritDoc}
    */
   public Iterator<String> getUnfiledObjectsId() throws StorageException
   {
      return unfiled.iterator();
   }

   /**
    * {@inheritDoc}
    */
   public String addType(TypeDefinition type) throws StorageException, CmisRuntimeException
   {
      if (types.get(type.getId()) != null)
      {
         throw new InvalidArgumentException("Type " + type.getId() + " already exists.");
      }
      if (type.getBaseId() == null)
      {
         throw new InvalidArgumentException("Base type id must be specified.");
      }
      if (type.getParentId() == null)
      {
         throw new InvalidArgumentException("Unable add root type. Parent type id must be specified");
      }

      TypeDefinition superType;
      try
      {
         superType = getTypeDefinition(type.getParentId(), true);
      }
      catch (TypeNotFoundException e)
      {
         throw new InvalidArgumentException("Specified parent type '" + type.getParentId() + "' does not exist.");
      }
      // Check new type does not use known property IDs.
      if (type.getPropertyDefinitions() != null)
      {
         for (PropertyDefinition<?> newDefinition : type.getPropertyDefinitions())
         {
            PropertyDefinition<?> definition = superType.getPropertyDefinition(newDefinition.getId());
            if (definition != null)
            {
               throw new InvalidArgumentException("Property " + newDefinition.getId() + " already defined");
            }
         }
      }

      Map<String, PropertyDefinition<?>> m = new HashMap<String, PropertyDefinition<?>>();
      for (Iterator<PropertyDefinition<?>> iterator = superType.getPropertyDefinitions().iterator(); iterator.hasNext();)
      {
         PropertyDefinition<?> next = iterator.next();
         m.put(next.getId(), next);
      }

      if (type.getPropertyDefinitions() != null)
      {
         for (Iterator<PropertyDefinition<?>> iterator = type.getPropertyDefinitions().iterator(); iterator.hasNext();)
         {
            PropertyDefinition<?> next = iterator.next();
            m.put(next.getId(), next);
         }
      }

      types.put(type.getId(), type);
      typeChildren.get(superType.getId()).add(type.getId());
      typeChildren.put(type.getId(), new HashSet<String>());
      PropertyDefinitions.putAll(type.getId(), m);

      return type.getId();
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<TypeDefinition> getTypeChildren(String typeId, boolean includePropertyDefinitions)
      throws TypeNotFoundException, CmisRuntimeException
   {
      List<TypeDefinition> types = new ArrayList<TypeDefinition>();
      if (typeId == null)
      {
         for (String t : new String[]{"cmis:document", "cmis:folder", "cmis:policy", "cmis:relationship"})
         {
            types.add(getTypeDefinition(t, includePropertyDefinitions));
         }
      }
      else
      {
         if (this.types.get(typeId) == null)
         {
            throw new TypeNotFoundException("Type '" + typeId + "' does not exist.");
         }

         for (String t : typeChildren.get(typeId))
         {
            types.add(getTypeDefinition(t, includePropertyDefinitions));
         }
      }
      return new BaseItemsIterator<TypeDefinition>(types);
   }

   /**
    * {@inheritDoc}
    */
   public TypeDefinition getTypeDefinition(String typeId, boolean includePropertyDefinition)
      throws TypeNotFoundException, CmisRuntimeException
   {
      TypeDefinition type = types.get(typeId);
      if (type == null)
      {
         throw new TypeNotFoundException("Type '" + typeId + "' does not exist.");
      }
      TypeDefinition copy =
         new TypeDefinition(type.getId(), type.getBaseId(), type.getQueryName(), type.getLocalName(), type
            .getLocalNamespace(), type.getParentId(), type.getDisplayName(), type.getDescription(), type.isCreatable(),
            type.isFileable(), type.isQueryable(), type.isFulltextIndexed(), type.isIncludedInSupertypeQuery(), type
               .isControllablePolicy(), type.isControllableACL(), type.isVersionable(), type.getAllowedSourceTypes(),
            type.getAllowedTargetTypes(), type.getContentStreamAllowed(), includePropertyDefinition
               ? PropertyDefinitions.getAll(typeId) : null);

      return copy;
   }

   /**
    * {@inheritDoc}
    */
   public void removeType(String typeId) throws TypeNotFoundException, StorageException, ConstraintException
   {
      TypeDefinition type = types.get(typeId);
      if (type == null)
      {
         throw new TypeNotFoundException("Type '" + typeId + "' does not exist.");
      }

      if (type.getParentId() == null)
      {
         throw new ConstraintException("Unable remove root type " + typeId);
      }

      if (typeChildren.get(typeId).size() > 0)
      {
         throw new ConstraintException("Unable remove type " + typeId + ". Type has descendant types.");
      }

      for (Iterator<Entry> iterator = entries.values().iterator(); iterator.hasNext();)
      {
         if (typeId.equals(iterator.next().getTypeId()))
         {
            throw new ConstraintException("Unable remove type definition if at least one object of this type exists.");
         }
      }
      types.remove(typeId);
      typeChildren.get(type.getParentId()).remove(typeId);

      PropertyDefinitions.removeAll(typeId);
   }

   protected String getCurrentUser()
   {
      ConversationState state = ConversationState.getCurrent();
      if (state != null)
      {
         return state.getIdentity().getUserId();
      }
      return getRepositoryInfo().getPrincipalAnonymous();
   }

   void validateMaxItemsNumber(ObjectData object) throws StorageException
   {
      if (entries.size() > maxItemsNumber)
      {
         throw new StorageException("Unable add new object in storage. Max number '" + maxItemsNumber
            + "' of items is reached."
            + " Increase or set storage configuration property 'org.xcmis.inmemory.maxitems'.");
      }
   }

   void validateMemSize(byte[] content) throws StorageException
   {
      if (content == null || content.length == 0)
      {
         return;
      }
      long size = 0;
      for (Entry c : entries.values())
      {
         ByteArrayValue contentValue = (ByteArrayValue)c.getValue(PropertyDefinitions.CONTENT);
         if (contentValue != null)
            size += contentValue.getBytes().length;
      }
      if ((size + content.length) > maxStorageMemSize)
      {
         throw new StorageException("Unable add new object in storage. Max allowed memory size '" + maxStorageMemSize
            + "' bytes is reached." + " Increase or set storage configuration property 'org.xcmis.inmemory.maxmem'.");
      }
   }

   private SearchService getInitializedSearchService()
   {
      CmisSchema schema = new CmisSchema(this);
      CmisSchemaTableResolver tableResolver = new CmisSchemaTableResolver(new ToStringNameConverter(), schema, this);

      IndexConfiguration indexConfiguration = new IndexConfiguration();
      indexConfiguration.setQueryableIndexStorage("org.xcmis.search.lucene.InMemoryLuceneQueryableIndexStorage");
      //indexConfiguration.setIndexDir("/tmp/dir/" + UUID.randomUUID().toString() + "/");
      indexConfiguration.setRootUuid(this.getRepositoryInfo().getRootFolderId());
      //if list of root parents is empty it will be indexed as empty string
      indexConfiguration.setRootParentUuid("");
      indexConfiguration.setDocumentReaderService(new CmisDocumentReaderService());

      //default invocation context
      InvocationContext invocationContext = new InvocationContext();
      invocationContext.setNameConverter(new ToStringNameConverter());

      invocationContext.setSchema(schema);
      invocationContext.setPathSplitter(new SlashSplitter());

      invocationContext.setTableResolver(tableResolver);

      SearchServiceConfiguration searchConfiguration = new SearchServiceConfiguration();
      searchConfiguration.setIndexConfiguration(indexConfiguration);
      searchConfiguration.setContentReader(new CmisContentReader(this));
      searchConfiguration.setNameConverter(new ToStringNameConverter());
      searchConfiguration.setDefaultInvocationContext(invocationContext);
      searchConfiguration.setTableResolver(tableResolver);
      searchConfiguration.setPathSplitter(new SlashSplitter());

      try
      {
         SearchService searchService = new SearchService(searchConfiguration);
         searchService.start();
         return searchService;
         //attach listener to the created storage
         //IndexListener indexListener = new IndexListener(this, searchService);
         //storage.setIndexListener(indexListener);

      }
      catch (SearchServiceException e)
      {
         LOG.error("Unable to initialize storage. ", e);
      }
      return null;

   }

   private class DocumentOrderResultSorter implements Comparator<ScoredRow>
   {

      /** The selector name. */
      private final String selectorName;

      private final Map<String, ObjectData> itemCache;

      private final Storage storage;

      DocumentOrderResultSorter(final String selectorName, Storage storage)
      {
         this.selectorName = selectorName;
         this.storage = storage;
         this.itemCache = new HashMap<String, ObjectData>();
      }

      /**
       * {@inheritDoc}
       */
      public int compare(ScoredRow o1, ScoredRow o2)
      {
         if (o1.equals(o2))
         {
            return 0;
         }
         final String path1 = getPath(o1.getNodeIdentifer(selectorName));
         final String path2 = getPath(o2.getNodeIdentifer(selectorName));
         // TODO should be checked
         if (path1 == null || path2 == null)
         {
            return 0;
         }
         return path1.compareTo(path2);
      }

      /**
       * Return comparable location of the object
       *
       * @param identifer
       * @return
       */
      public String getPath(String identifer)
      {
         ObjectData obj = itemCache.get(identifer);
         if (obj == null)
         {
            try
            {
               obj = storage.getObjectById(identifer);
            }
            catch (ObjectNotFoundException e)
            {
               // XXX : correct ?
               return null;
            }
            itemCache.put(identifer, obj);
         }
         if (obj.getBaseType() == BaseType.FOLDER)
         {
            if (((FolderData)obj).isRoot())
            {
               return obj.getName();
            }
         }
         Collection<FolderData> parents = obj.getParents();
         if (parents.size() == 0)
         {
            return obj.getName();
         }
         return parents.iterator().next().getPath() + "/" + obj.getName();
      }
   }

   /**
    * Single row from query result.
    */
   private class ResultImpl implements Result
   {

      private final String id;

      private final String[] properties;

      private final Score score;

      ResultImpl(String id, String[] properties, Score score)
      {
         this.id = id;
         this.properties = properties;
         this.score = score;
      }

      public String[] getPropertyNames()
      {
         return properties;
      }

      public String getObjectId()
      {
         return id;
      }

      public Score getScore()
      {
         return score;
      }

   }

   /**
    * Iterator over query result's.
    */
   private class QueryResultIterator implements ItemsIterator<Result>
   {

      private final Iterator<ScoredRow> rows;

      private final Set<SelectorName> selectors;

      private final int size;

      private final org.xcmis.search.model.Query qom;

      private Result next;

      QueryResultIterator(List<ScoredRow> rows, org.xcmis.search.model.Query qom)
      {
         this.size = rows.size();
         this.rows = rows.iterator();
         this.selectors = Visitors.getSelectorsReferencedBy(qom);
         this.qom = qom;
         fetchNext();
      }

      /**
       * {@inheritDoc}
       */
      public boolean hasNext()
      {
         return next != null;
      }

      /**
       * {@inheritDoc}
       */
      public Result next()
      {
         if (next == null)
         {
            throw new NoSuchElementException();
         }
         Result r = next;
         fetchNext();
         return r;
      }

      /**
       * {@inheritDoc}
       */
      public void remove()
      {
         throw new UnsupportedOperationException("remove");
      }

      /**
       * {@inheritDoc}
       */
      public int size()
      {
         return size;
      }

      /**
       * {@inheritDoc}
       */
      public void skip(int skip) throws NoSuchElementException
      {
         while (skip-- > 0)
         {
            next();
         }
      }

      /**
       * To fetch next <code>Result</code>.
       */
      protected void fetchNext()
      {
         next = null;
         while (next == null && rows.hasNext())
         {
            ScoredRow row = rows.next();
            for (SelectorName selectorName : selectors)
            {
               String objectId = row.getNodeIdentifer(selectorName.getName());
               List<String> properties = null;
               Score score = null;
               for (Column column : qom.getColumns())
               {
                  //TODO check
                  if (column.isFunction())
                  {
                     score = new Score(column.getColumnName(), BigDecimal.valueOf(row.getScore()));
                  }
                  else
                  {
                     if (selectorName.getName().equals(column.getSelectorName()))
                     {
                        if (column.getPropertyName() != null)
                        {
                           if (properties == null)
                           {
                              properties = new ArrayList<String>();
                           }
                           properties.add(column.getPropertyName());
                        }
                     }
                  }
               }
               next =
                  new ResultImpl(objectId, properties == null ? null : properties
                     .toArray(new String[properties.size()]), score);
            }
         }
      }
   }

}
