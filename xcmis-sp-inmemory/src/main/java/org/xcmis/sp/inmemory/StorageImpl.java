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

import org.xcmis.spi.AllowableActions;
import org.xcmis.spi.ChangeEvent;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.Rendition;
import org.xcmis.spi.RepositoryInfo;
import org.xcmis.spi.Storage;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.TypeDefinition;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.UnfileObject;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.VersioningState;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.Document;
import org.xcmis.spi.data.Folder;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.data.Policy;
import org.xcmis.spi.data.Relationship;
import org.xcmis.spi.object.Property;
import org.xcmis.spi.query.Query;
import org.xcmis.spi.query.Result;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class StorageImpl implements Storage
{

   /** Map of id -> data.*/
   final Map<String, Map<String, Property<?>>> properties;

   /** Map of id -> children IDs. */
   final Map<String, Set<String>> children;

   /** Map of id -> parent IDs, or null if unfiled. */
   final Map<String, Set<String>> parents;

   /** Map of id -> policies IDs. */
   final Map<String, Set<String>> policies;

   /** Map of id -> versions. */
   final Map<String, Set<String>> versions;

   /** Unfiled objects set. */
   final Set<String> unfiling;

   /** Map of id -> content. */
   final Map<String, ContentStream> contents;

   /** Map of id -> ACLs. */
   final Map<String, Map<String, Set<String>>> acls;

   final Map<String, Set<RelationshipInfo>> relationships;

   public StorageImpl()
   {
      this.properties = new ConcurrentHashMap<String, Map<String, Property<?>>>();
      this.children = new ConcurrentHashMap<String, Set<String>>();
      this.parents = new ConcurrentHashMap<String, Set<String>>();
      this.policies = new ConcurrentHashMap<String, Set<String>>();
      this.versions = new ConcurrentHashMap<String, Set<String>>();
      this.unfiling = new HashSet<String>();
      this.contents = new ConcurrentHashMap<String, ContentStream>();
      this.acls = new ConcurrentHashMap<String, Map<String, Set<String>>>();
      this.relationships = new ConcurrentHashMap<String, Set<RelationshipInfo>>();
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
      // TODO Auto-generated method stub
      return null;
   }

   public Folder createFolder(Folder folder, String typeId) throws ConstraintException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Policy createPolicy(Folder folder, String typeId) throws ConstraintException
   {
      // TODO Auto-generated method stub
      return null;
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
      // TODO Auto-generated method stub
      return null;
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
      // TODO Auto-generated method stub
      return null;
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
      // TODO Auto-generated method stub
      return null;
   }

   public void removeType(String typeId) throws TypeNotFoundException, StorageException, CmisRuntimeException
   {
      // TODO Auto-generated method stub

   }

}
