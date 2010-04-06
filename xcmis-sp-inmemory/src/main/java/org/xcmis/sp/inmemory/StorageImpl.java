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

import org.xcmis.spi.CMIS;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.Storage;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.data.Document;
import org.xcmis.spi.data.Folder;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.data.Policy;
import org.xcmis.spi.data.Relationship;
import org.xcmis.spi.model.AllowableActions;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.ChangeEvent;
import org.xcmis.spi.model.ContentStreamAllowed;
import org.xcmis.spi.model.Rendition;
import org.xcmis.spi.model.RepositoryInfo;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.UnfileObject;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.model.impl.TypeDefinitionImpl;
import org.xcmis.spi.query.Query;
import org.xcmis.spi.query.Result;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class StorageImpl implements Storage
{

   public static String generateId()
   {
      return UUID.randomUUID().toString();
   }

   /** Map of id -> data. */
   //   final Map<String, Map<String, Property<?>>> properties;

   final Map<String, Entry> entries;

   /** Map of id -> children IDs. */
   final Map<String, Set<String>> children;

   /** Map of id -> parent IDs, or null if unfiled. */
   final Map<String, Set<String>> parents;

   /** Map of id -> policies IDs. */
   //   final Map<String, Set<String>> policies;

   /** Map of id -> versions. */
   final Map<String, Set<String>> versions;

   /** Unfiled objects set. */
   final Set<String> unfiling;

   /** Map of id -> content. */
   final Map<String, byte[]> contents;

   final Set<String> policies;

   /** Map of id -> ACLs. */
   //   final Map<String, Map<String, Set<String>>> acls;

   final Map<String, Set<RelationshipInfo>> relationships;

   final Map<String, TypeDefinitionImpl> types;

   static final String ROOT_FOLDER_ID = "abcdef12-3456-7890-0987-654321fedcba";

   static final Set<String> EMPTY_PARENTS = Collections.emptySet();

   public StorageImpl()
   {
      this.entries = new ConcurrentHashMap<String, Entry>();
      //      this.properties = new ConcurrentHashMap<String, Map<String, Property<?>>>();
      this.children = new ConcurrentHashMap<String, Set<String>>();
      this.parents = new ConcurrentHashMap<String, Set<String>>();
      //      this.policies = new ConcurrentHashMap<String, Set<String>>();
      this.versions = new ConcurrentHashMap<String, Set<String>>();
      this.unfiling = new HashSet<String>();
      this.contents = new ConcurrentHashMap<String, byte[]>();
      //      this.acls = new ConcurrentHashMap<String, Map<String, Set<String>>>();
      this.relationships = new ConcurrentHashMap<String, Set<RelationshipInfo>>();
      this.policies = new CopyOnWriteArraySet<String>();

      this.types = new ConcurrentHashMap<String, TypeDefinitionImpl>();

      types.put("cmis:document", //
         new TypeDefinitionImpl("cmis:document", BaseType.DOCUMENT, "cmis:document", "cmis:document", "", null,
            "cmis:document", "Cmis Document Type", true, true, false /*no query support yet*/, false, false, true,
            true, true, null, null, ContentStreamAllowed.ALLOWED, null));

      types.put("cmis:folder", //
         new TypeDefinitionImpl("cmis:folder", BaseType.FOLDER, "cmis:folder", "cmis:folder", "", null, "cmis:folder",
            "Cmis Folder type", true, true, false /*no query support yet*/, false, false, true, true, false, null,
            null, ContentStreamAllowed.NOT_ALLOWED, null));

      types.put("cmis:policy", //
         new TypeDefinitionImpl("cmis:policy", BaseType.POLICY, "cmis:policy", "cmis:policy", "", null, "cmis:policy",
            "Cmis Policy type", true, false, false /*no query support yet*/, false, false, true, true, false, null,
            null, ContentStreamAllowed.NOT_ALLOWED, null));

      Entry root = new Entry(BaseType.FOLDER, "cmis:folder");
      root.setValue(CMIS.NAME, //
         new StringValue(""));
      root.setValue(CMIS.OBJECT_ID, //
         new StringValue(ROOT_FOLDER_ID));
      root.setValue(CMIS.CREATION_DATE, //
         new DateValue(Calendar.getInstance()));
      root.setValue(CMIS.CREATED_BY, //
         new StringValue("system"));
      root.setValue(CMIS.LAST_MODIFICATION_DATE, //
         new DateValue(Calendar.getInstance()));
      root.setValue(CMIS.LAST_MODIFIED_BY, //
         new StringValue("system"));
      entries.put(ROOT_FOLDER_ID, root);
      parents.put(ROOT_FOLDER_ID, EMPTY_PARENTS);
      children.put(ROOT_FOLDER_ID, new CopyOnWriteArraySet<String>());
   }

   public AllowableActions calculateAllowableActions(ObjectData object)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Document createCopyOfDocument(Document source, Folder folder, VersioningState versioningState)
      throws ConstraintException, StorageException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Document createDocument(Folder folder, String typeId, VersioningState versioningState)
      throws ConstraintException
   {
      return new DocumentImpl(folder, getTypeDefinition(typeId, true), versioningState, this);
   }

   public Folder createFolder(Folder folder, String typeId) throws ConstraintException
   {
      return new FolderImpl(folder, getTypeDefinition(typeId, true), this);
   }

   public Policy createPolicy(Folder folder, String typeId) throws ConstraintException
   {
      return new PolicyImpl((Folder)null, getTypeDefinition(typeId, true), this);
   }

   public Relationship createRelationship(ObjectData source, ObjectData target, String typeId)
      throws ConstraintException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public void deleteObject(ObjectData object, boolean deleteAllVersions) throws ConstraintException,
      UpdateConflictException, StorageException
   {
      // TODO Auto-generated method stub

   }

   public Collection<String> deleteTree(Folder folder, boolean deleteAllVersions, UnfileObject unfileObject,
      boolean continueOnFailure) throws UpdateConflictException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Collection<Document> getAllVersions(String versionSeriesId) throws ObjectNotFoundException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public ItemsIterator<ChangeEvent> getChangeLog(String changeLogToken) throws ConstraintException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public ItemsIterator<ObjectData> getCheckedOutDocuments(ObjectData folder, String orderBy)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getId()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public ObjectData getObject(String objectId) throws ObjectNotFoundException
   {
      Entry entry = entries.get(objectId);
      if (entry == null)
      {
         throw new ObjectNotFoundException("Object " + objectId + "does not exists.");
      }
      switch (entry.getBaseType())
      {
         case DOCUMENT :
            return new DocumentImpl(entry.copy(), getTypeDefinition(entry.getTypeId(), true), this);
         case FOLDER :
            return new FolderImpl(entry.copy(), getTypeDefinition(entry.getTypeId(), true), this);
         case POLICY :
            return new PolicyImpl(entry.copy(), getTypeDefinition(entry.getTypeId(), true), this);
         case RELATIONSHIP :
            return new RelationshipImpl(entry.copy(), getTypeDefinition(entry.getTypeId(), true), this);
      }
      // Must never happen.
      throw new CmisRuntimeException("Unknown base type. ");
   }

   public ObjectData getObjectByPath(String path) throws ObjectNotFoundException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public ItemsIterator<Rendition> getRenditions(ObjectData object)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public RepositoryInfo getRepositoryInfo()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public ObjectData moveObject(ObjectData object, Folder target, Folder source) throws ConstraintException,
      InvalidArgumentException, UpdateConflictException, VersioningException, NameConstraintViolationException,
      StorageException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public ItemsIterator<Result> query(Query query) throws InvalidArgumentException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String saveObject(ObjectData object) throws StorageException, NameConstraintViolationException,
      UpdateConflictException
   {
      ((BaseObjectData)object).save();
      return object.getObjectId();
   }

   public void unfileObject(ObjectData object)
   {
      // TODO Auto-generated method stub

   }

   public String addType(TypeDefinition type) throws StorageException, CmisRuntimeException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public ItemsIterator<TypeDefinition> getTypeChildren(String typeId, boolean includePropertyDefinitions)
      throws TypeNotFoundException, CmisRuntimeException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public TypeDefinition getTypeDefinition(String typeId, boolean includePropertyDefinition)
      throws TypeNotFoundException, CmisRuntimeException
   {
      TypeDefinitionImpl type = types.get(typeId);
      if (type == null)
      {
         throw new TypeNotFoundException("Type " + typeId + " does not exists.");
      }
      TypeDefinitionImpl copy =
         new TypeDefinitionImpl(type.getId(), type.getBaseId(), type.getQueryName(), type.getLocalName(), type
            .getLocalNamespace(), type.getParentId(), type.getDisplayName(), type.getDescription(), type.isCreatable(),
            type.isFileable(), type.isQueryable(), type.isFulltextIndexed(), type.isIncludedInSupertypeQuery(), type
               .isControllablePolicy(), type.isControllableACL(), type.isVersionable(), type.getAllowedSourceTypes(),
            type.getAllowedTargetTypes(), type.getContentStreamAllowed(), includePropertyDefinition
               ? PropertyDefinitions.getAll(typeId) : null);

      return copy;
   }

   public void removeType(String typeId) throws TypeNotFoundException, StorageException, CmisRuntimeException
   {
      // TODO Auto-generated method stub

   }

}
