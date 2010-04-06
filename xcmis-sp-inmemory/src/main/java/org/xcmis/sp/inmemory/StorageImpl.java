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
import org.xcmis.spi.impl.BaseItemsIterator;
import org.xcmis.spi.model.AllowableActions;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.ChangeEvent;
import org.xcmis.spi.model.ContentStreamAllowed;
import org.xcmis.spi.model.PropertyDefinition;
import org.xcmis.spi.model.Rendition;
import org.xcmis.spi.model.RepositoryInfo;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.UnfileObject;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.model.impl.TypeDefinitionImpl;
import org.xcmis.spi.query.Query;
import org.xcmis.spi.query.Result;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
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

   final Map<String, Map<String, Value>> properties;

   /** Map of id -> children IDs. */
   final Map<String, Set<String>> children;

   /** Map of id -> parent IDs, or null if unfiled. */
   final Map<String, Set<String>> parents;

   /** Map of id -> versions. */
   final Map<String, Set<String>> versions;

   /** Unfiled objects set. */
   final Set<String> unfiling;

   /** Map of id -> content. */
   final Map<String, byte[]> contents;

   final Map<String, Set<String>> policies;

   final Map<String, Set<RelationshipInfo>> relationships;

   final Map<String, Map<String, Set<String>>> permissions;

   final Map<String, TypeDefinitionImpl> types;

   final Map<String, Set<String>> typeChildren;

   static final String ROOT_FOLDER_ID = "abcdef12-3456-7890-0987-654321fedcba";

   static final Set<String> EMPTY_PARENTS = Collections.emptySet();

   public StorageImpl()
   {
      this.properties = new ConcurrentHashMap<String, Map<String, Value>>();
      this.children = new ConcurrentHashMap<String, Set<String>>();
      this.parents = new ConcurrentHashMap<String, Set<String>>();
      this.versions = new ConcurrentHashMap<String, Set<String>>();
      this.unfiling = new HashSet<String>();
      this.contents = new ConcurrentHashMap<String, byte[]>();
      this.relationships = new ConcurrentHashMap<String, Set<RelationshipInfo>>();
      this.policies = new ConcurrentHashMap<String, Set<String>>();
      this.permissions = new ConcurrentHashMap<String, Map<String, Set<String>>>();

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

      types.put("cmis:relationship", //
         new TypeDefinitionImpl("cmis:relationship", BaseType.RELATIONSHIP, "cmis:relationship", "cmis:relationship",
            "", null, "cmis:relationship", "Cmis Relationship type.", true, false, false /*no query support yet*/,
            false, false, true, true, false, null, null, ContentStreamAllowed.NOT_ALLOWED, null));

      typeChildren = new ConcurrentHashMap<String, Set<String>>();
      typeChildren.put("cmis:document", new HashSet<String>());
      typeChildren.put("cmis:folder", new HashSet<String>());
      typeChildren.put("cmis:policy", new HashSet<String>());
      typeChildren.put("cmis:relationship", new HashSet<String>());

      Map<String, Value> root = new HashMap<String, Value>();
      root.put(CMIS.NAME, //
         new StringValue(""));
      root.put(CMIS.OBJECT_ID, //
         new StringValue(ROOT_FOLDER_ID));
      root.put(CMIS.OBJECT_TYPE_ID, //
         new StringValue("cmis:folder"));
      root.put(CMIS.BASE_TYPE_ID, //
         new StringValue(BaseType.FOLDER.value()));
      root.put(CMIS.CREATION_DATE, //
         new DateValue(Calendar.getInstance()));
      root.put(CMIS.CREATED_BY, //
         new StringValue("system"));
      root.put(CMIS.LAST_MODIFICATION_DATE, //
         new DateValue(Calendar.getInstance()));
      root.put(CMIS.LAST_MODIFIED_BY, //
         new StringValue("system"));

      properties.put(ROOT_FOLDER_ID, root);
      policies.put(ROOT_FOLDER_ID, new HashSet<String>());
      permissions.put(ROOT_FOLDER_ID, new HashMap<String, Set<String>>());
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
      return new DocumentCopy(source, folder, getTypeDefinition(source.getTypeId(), true), versioningState, this);
   }

   /**
    * {@inheritDoc}
    */
   public Document createDocument(Folder folder, String typeId, VersioningState versioningState)
      throws ConstraintException
   {
      return new DocumentImpl(folder, getTypeDefinition(typeId, true), versioningState, this);
   }

   /**
    * {@inheritDoc}
    */
   public Folder createFolder(Folder folder, String typeId) throws ConstraintException
   {
      return new FolderImpl(folder, getTypeDefinition(typeId, true), this);
   }

   /**
    * {@inheritDoc}
    */
   public Policy createPolicy(Folder folder, String typeId) throws ConstraintException
   {
      return new PolicyImpl(getTypeDefinition(typeId, true), this);
   }

   /**
    * {@inheritDoc}
    */
   public Relationship createRelationship(ObjectData source, ObjectData target, String typeId)
      throws ConstraintException
   {
      return new RelationshipImpl(getTypeDefinition(typeId, true), source, target, this);
   }

   /**
    * {@inheritDoc}
    */
   public void deleteObject(ObjectData object, boolean deleteAllVersions) throws ConstraintException,
      UpdateConflictException, StorageException
   {
      ((BaseObjectData)object).delete();
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

   /**
    * {@inheritDoc}
    */
   public ObjectData getObject(String objectId) throws ObjectNotFoundException
   {
      Map<String, Value> values = properties.get(objectId);
      if (values == null)
      {
         throw new ObjectNotFoundException("Object " + objectId + "does not exists.");
      }
      BaseType baseType = BaseType.fromValue(values.get(CMIS.BASE_TYPE_ID).getStrings()[0]);
      String typeId = values.get(CMIS.OBJECT_TYPE_ID).getStrings()[0];
      switch (baseType)
      {
         case DOCUMENT :
            return new DocumentImpl(new Entry(values, new HashMap<String, Set<String>>(permissions.get(objectId)),
               new HashSet<String>(policies.get(objectId))), getTypeDefinition(typeId, true), this);
         case FOLDER :
            return new FolderImpl(new Entry(values, new HashMap<String, Set<String>>(permissions.get(objectId)),
               new HashSet<String>(policies.get(objectId))), getTypeDefinition(typeId, true), this);
         case POLICY :
            return new PolicyImpl(new Entry(values, new HashMap<String, Set<String>>(permissions.get(objectId)),
               new HashSet<String>(policies.get(objectId))), getTypeDefinition(typeId, true), this);
         case RELATIONSHIP :
            return new RelationshipImpl(new Entry(values, new HashMap<String, Set<String>>(permissions.get(objectId)),
               new HashSet<String>(policies.get(objectId))), getTypeDefinition(typeId, true), this);
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
               ObjectData seg = getObject(id);
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
      return getObject(point);
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

   /**
    * {@inheritDoc}
    */
   public ObjectData moveObject(ObjectData object, Folder target, Folder source) throws ConstraintException,
      InvalidArgumentException, UpdateConflictException, VersioningException, NameConstraintViolationException,
      StorageException
   {
      String objectid = object.getObjectId();
      String sourceId = source.getObjectId();
      String targetId = target.getObjectId();
      children.get(sourceId).remove(objectid);
      children.get(targetId).add(objectid);
      parents.get(object.getObjectId()).remove(sourceId);
      parents.get(object.getObjectId()).add(targetId);
      return getObject(objectid);
   }

   public ItemsIterator<Result> query(Query query) throws InvalidArgumentException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public String saveObject(ObjectData object) throws StorageException, NameConstraintViolationException,
      UpdateConflictException
   {
      ((BaseObjectData)object).save();
      return object.getObjectId();
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

      TypeDefinitionImpl superType;
      try
      {
         superType = (TypeDefinitionImpl)getTypeDefinition(type.getParentId(), true);
      }
      catch (TypeNotFoundException e)
      {
         throw new InvalidArgumentException("Specified parent type " + type.getParentId() + " does not exists.");
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

      types.put(type.getId(), (TypeDefinitionImpl)type);
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
      if (types.get(typeId) == null)
      {
         throw new TypeNotFoundException("Type " + typeId + " does not exists.");
      }

      Set<String> tc = typeChildren.get(typeId);
      List<TypeDefinition> types = new ArrayList<TypeDefinition>(tc.size());
      for (String t : tc)
      {
         types.add(getTypeDefinition(t, includePropertyDefinitions));
      }
      return new BaseItemsIterator<TypeDefinition>(types);
   }

   /**
    * {@inheritDoc}
    */
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

   /**
    * {@inheritDoc}
    */
   public void removeType(String typeId) throws TypeNotFoundException, StorageException, CmisRuntimeException
   {
      TypeDefinitionImpl type = types.get(typeId);
      if (type == null)
      {
         throw new TypeNotFoundException("Type " + typeId + " does not exists.");
      }

      if (type.getParentId() == null)
      {
         throw new ConstraintException("Unable remove root type " + typeId);
      }

      if (typeChildren.get(typeId).size() > 0)
      {
         throw new ConstraintException("Unable remove type " + typeId + ". Type has descendant types.");
      }

      for (Iterator<Map<String, Value>> iterator = properties.values().iterator(); iterator.hasNext();)
      {
         if (typeId.equals(iterator.next().get(CMIS.OBJECT_TYPE_ID).getStrings()[0]))
         {
            throw new ConstraintException("Unable remove type definition if at least one object of this type exists.");
         }
      }
      types.remove(typeId);
      typeChildren.get(type.getParentId()).remove(typeId);

      PropertyDefinitions.removeAll(typeId);
   }

}
