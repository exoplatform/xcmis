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
package org.xcmis.spi.basic;

import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.DocumentData;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.NotSupportedException;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.PermissionService;
import org.xcmis.spi.PolicyData;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.Storage;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.TypeManager;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.UserContext;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.AllowableActions;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.ChangeEvent;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.Rendition;
import org.xcmis.spi.model.RepositoryInfo;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.query.Query;
import org.xcmis.spi.query.Result;
import org.xcmis.spi.utils.CmisUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Basic Storage impl
 */
public abstract class BasicStorage implements Storage
{

   protected RepositoryInfo repositoryInfo;

   protected TypeManager typeManager;

   protected PermissionService permissionService;

   public BasicStorage(RepositoryInfo repositoryInfo, TypeManager typeManager, PermissionService permissionService)
   {
      this.repositoryInfo = repositoryInfo;
      this.typeManager = typeManager;
      this.permissionService = permissionService;
   }

   /**
    * {@inheritDoc}
    */
   public RepositoryInfo getRepositoryInfo()
   {
      return this.repositoryInfo;
   }

   /**
    * {@inheritDoc}
    */
   public AllowableActions calculateAllowableActions(ObjectData object)
   {
      UserContext ctx = UserContext.getCurrent();
      AllowableActions actions =
         permissionService.calculateAllowableActions(object, ctx != null ? ctx.getUserId() : null,
            getRepositoryInfo());
      return actions;
   }

   /**
    * {@inheritDoc}
    */
   public String addType(TypeDefinition type) throws ConstraintException, StorageException, CmisRuntimeException
   {
      return this.typeManager.addType(type);
   }

   /**
    * {@inheritDoc}
    */
   public void removeType(String typeId) throws TypeNotFoundException, ConstraintException, StorageException,
      CmisRuntimeException
   {
      this.typeManager.removeType(typeId);
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<TypeDefinition> getTypeChildren(String typeId, boolean includePropertyDefinitions)
      throws TypeNotFoundException, CmisRuntimeException
   {
      return this.typeManager.getTypeChildren(typeId, includePropertyDefinitions);
   }

   /**
    * {@inheritDoc}
    */
   public TypeDefinition getTypeDefinition(String typeId, boolean includePropertyDefinition)
      throws TypeNotFoundException, CmisRuntimeException
   {
      return this.typeManager.getTypeDefinition(typeId, includePropertyDefinition);
   }

   /**
    * {@inheritDoc}
    */
   public DocumentData createDocument(FolderData parent, TypeDefinition typeDefinition,
      Map<String, Property<?>> properties, ContentStream content, List<AccessControlEntry> acl,
      Collection<PolicyData> policies, VersioningState versioningState) throws ConstraintException,
      NameConstraintViolationException, IOException, StorageException
   {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */
   public PolicyData createPolicy(FolderData parent, TypeDefinition typeDefinition,
      Map<String, Property<?>> properties, List<AccessControlEntry> acl, Collection<PolicyData> policies)
      throws ConstraintException, NameConstraintViolationException, StorageException
   {
      throw new NotSupportedException();
   }

   /**
    * {@inheritDoc}
    */
   public RelationshipData createRelationship(ObjectData source, ObjectData target, TypeDefinition typeDefinition,
      Map<String, Property<?>> properties, List<AccessControlEntry> acl, Collection<PolicyData> policies)
      throws NameConstraintViolationException, StorageException
   {
      throw new NotSupportedException();
   }

   /**
    * {@inheritDoc}
    */
   public Collection<DocumentData> getAllVersions(String versionSeriesId) throws ObjectNotFoundException
   {
      ObjectData data = getObjectById(versionSeriesId);
      if (data.getBaseType() == BaseType.DOCUMENT)
      {
         List<DocumentData> l = new ArrayList<DocumentData>(1);
         l.add((DocumentData)data);
         return l;
      }
      return Collections.emptySet();

   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<ChangeEvent> getChangeLog(String changeLogToken) throws ConstraintException
   {
      throw new NotSupportedException();
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<DocumentData> getCheckedOutDocuments(FolderData folder, String orderBy)
   {
      return CmisUtils.emptyItemsIterator();
   }

   /**
    * {@inheritDoc}
    */
   public String getId()
   {
      return repositoryInfo.getRepositoryId();
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<Rendition> getRenditions(ObjectData object)
   {
      return CmisUtils.emptyItemsIterator();
   }

   /**
    * {@inheritDoc}
    */
   public ObjectData moveObject(ObjectData object, FolderData target, FolderData source)
      throws UpdateConflictException, VersioningException, NameConstraintViolationException, StorageException
   {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<Result> query(Query query) throws InvalidArgumentException
   {
      throw new NotSupportedException();
   }

   /**
    * {@inheritDoc}
    */
   public void unfileObject(ObjectData object)
   {
      throw new NotSupportedException();
   }

   /**
    * {@inheritDoc}
    */
   public Iterator<String> getUnfiledObjectsId() throws StorageException
   {
      throw new UnsupportedOperationException();
   }

}
