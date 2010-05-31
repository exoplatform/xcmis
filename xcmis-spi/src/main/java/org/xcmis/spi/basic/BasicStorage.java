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
package org.xcmis.sp.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.DocumentData;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.PolicyData;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.Storage;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.TypeManager;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.AllowableActions;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.CapabilityRendition;
import org.xcmis.spi.model.ChangeEvent;
import org.xcmis.spi.model.ContentStreamAllowed;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.Rendition;
import org.xcmis.spi.model.RepositoryCapabilities;
import org.xcmis.spi.model.RepositoryInfo;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.query.Query;
import org.xcmis.spi.query.Result;
import org.xcmis.spi.utils.CmisUtils;
/**
 * Basic Storage impl
 */
public abstract class BasicStorage implements Storage {
  
  protected RepositoryInfo repositoryInfo;
  
  protected TypeManager typeManager;
  
  public BasicStorage(RepositoryInfo repositoryInfo, TypeManager typeManager) 
  {
    this.repositoryInfo = repositoryInfo;
    this.typeManager = typeManager;
  }

  /* (non-Javadoc)
   * @see org.xcmis.spi.Storage#getRepositoryInfo()
   */
  public RepositoryInfo getRepositoryInfo() 
  {
    return this.repositoryInfo;
  }
  
  
  /* (non-Javadoc)
   * @see org.xcmis.spi.Storage#calculateAllowableActions(org.xcmis.spi.ObjectData)
   */
  public AllowableActions calculateAllowableActions(ObjectData object)
  {
     AllowableActions actions = new AllowableActions();
     TypeDefinition type = object.getTypeDefinition();

     RepositoryCapabilities capabilities = getRepositoryInfo().getCapabilities();

     boolean isCheckedout = type.getBaseId() == BaseType.DOCUMENT //
        && type.isVersionable() //
        && ((DocumentData)object).isVersionSeriesCheckedOut();

     actions.setCanGetProperties(true);

     actions.setCanUpdateProperties(true); // TODO : need to check is it latest version ??

     actions.setCanApplyACL(type.isControllableACL());

     actions.setCanGetACL(type.isControllableACL());

     actions.setCanApplyPolicy(type.isControllablePolicy());

     actions.setCanGetAppliedPolicies(type.isControllablePolicy());

     actions.setCanRemovePolicy(type.isControllablePolicy());

     actions.setCanGetObjectParents(type.isFileable());

     actions.setCanMoveObject(type.isFileable());

     actions.setCanAddObjectToFolder(capabilities.isCapabilityMultifiling() //
        && type.isFileable() //
        && type.getBaseId() != BaseType.FOLDER);

     actions.setCanRemoveObjectFromFolder(capabilities.isCapabilityUnfiling() //
        && type.isFileable() //
        && type.getBaseId() != BaseType.FOLDER);

     actions.setCanGetDescendants(capabilities.isCapabilityGetDescendants() //
        && type.getBaseId() == BaseType.FOLDER);

     actions.setCanGetFolderTree(capabilities.isCapabilityGetFolderTree() //
        && type.getBaseId() == BaseType.FOLDER);

     actions.setCanCreateDocument(type.getBaseId() == BaseType.FOLDER);

     actions.setCanCreateFolder(type.getBaseId() == BaseType.FOLDER);

     actions.setCanDeleteTree(type.getBaseId() == BaseType.FOLDER);

     actions.setCanGetChildren(type.getBaseId() == BaseType.FOLDER);

     actions.setCanGetFolderParent(type.getBaseId() == BaseType.FOLDER);

     actions.setCanGetContentStream(type.getBaseId() == BaseType.DOCUMENT //
        && ((DocumentData)object).hasContent());

     actions.setCanSetContentStream(type.getBaseId() == BaseType.DOCUMENT //
        && type.getContentStreamAllowed() != ContentStreamAllowed.NOT_ALLOWED);

     actions.setCanDeleteContentStream(type.getBaseId() == BaseType.DOCUMENT //
        && type.getContentStreamAllowed() != ContentStreamAllowed.REQUIRED);

     actions.setCanGetAllVersions(type.getBaseId() == BaseType.DOCUMENT);

     actions.setCanGetRenditions(capabilities.getCapabilityRenditions() == CapabilityRendition.READ);

     actions.setCanCheckIn(isCheckedout);

     actions.setCanCancelCheckOut(isCheckedout);

     actions.setCanCheckOut(!isCheckedout);

     actions.setCanGetObjectRelationships(type.getBaseId() != BaseType.RELATIONSHIP);

     actions.setCanCreateRelationship(type.getBaseId() != BaseType.RELATIONSHIP);

     // TODO : applied policy, not empty folders, not latest versions may not be delete.
     actions.setCanDeleteObject(true);

     return actions;
  }

  /* (non-Javadoc)
   * @see org.xcmis.spi.TypeManager#addType(org.xcmis.spi.model.TypeDefinition)
   */
  public String addType(TypeDefinition type) throws StorageException,
      CmisRuntimeException {
    return this.typeManager.addType(type);
  }

  /* (non-Javadoc)
   * @see org.xcmis.spi.TypeManager#removeType(java.lang.String)
   */
  public void removeType(String typeId) throws TypeNotFoundException,
      StorageException, CmisRuntimeException {
    this.typeManager.removeType(typeId);

  }
  
  /* (non-Javadoc)
   * @see org.xcmis.spi.TypeManager#getTypeChildren(java.lang.String, boolean)
   */
  public ItemsIterator<TypeDefinition> getTypeChildren(String typeId,
      boolean includePropertyDefinitions) throws TypeNotFoundException,
      CmisRuntimeException {
    
    return this.typeManager.getTypeChildren(typeId, includePropertyDefinitions);
  }

  /* (non-Javadoc)
   * @see org.xcmis.spi.TypeManager#getTypeDefinition(java.lang.String, boolean)
   */
  public TypeDefinition getTypeDefinition(String typeId,
      boolean includePropertyDefinition) throws TypeNotFoundException,
      CmisRuntimeException {
    
    return this.typeManager.getTypeDefinition(typeId, includePropertyDefinition);
  }


  /* (non-Javadoc)
   * @see org.xcmis.spi.Storage#copyDocument(org.xcmis.spi.DocumentData, org.xcmis.spi.FolderData, java.util.Map, java.util.List, java.util.List, java.util.Collection, org.xcmis.spi.model.VersioningState)
   */
  public DocumentData copyDocument(DocumentData arg0, FolderData arg1,
      Map<String, Property<?>> arg2, List<AccessControlEntry> arg3,
      List<AccessControlEntry> arg4, Collection<ObjectData> arg5,
      VersioningState arg6) throws ConstraintException, StorageException {
    throw new UnsupportedOperationException();
  }

  
  /* (non-Javadoc)
   * @see org.xcmis.spi.Storage#createPolicy(org.xcmis.spi.FolderData, java.lang.String, java.util.Map, java.util.List, java.util.List, java.util.Collection)
   */
  public PolicyData createPolicy(FolderData arg0, String arg1,
      Map<String, Property<?>> arg2, List<AccessControlEntry> arg3,
      List<AccessControlEntry> arg4, Collection<ObjectData> arg5)
      throws ConstraintException {
    // TODO Auto-generated method stub
    return null;
  }


  /* (non-Javadoc)
   * @see org.xcmis.spi.Storage#createRelationship(org.xcmis.spi.ObjectData, org.xcmis.spi.ObjectData, java.lang.String, java.util.Map, java.util.List, java.util.List, java.util.Collection)
   */
  public RelationshipData createRelationship(ObjectData arg0, ObjectData arg1,
      String arg2, Map<String, Property<?>> arg3,
      List<AccessControlEntry> arg4, List<AccessControlEntry> arg5,
      Collection<ObjectData> arg6) throws ConstraintException {
    // TODO Auto-generated method stub
    return null;
  }


  /* (non-Javadoc)
   * @see org.xcmis.spi.Storage#getAllVersions(java.lang.String)
   */
  public Collection<DocumentData> getAllVersions(String versionSeriesId)
      throws ObjectNotFoundException {
    //throw new UnsupportedOperationException();
    
    // TODO
    
    ObjectData data = getObjectById(versionSeriesId);
    if (data.getBaseType() == BaseType.DOCUMENT)
    {
       List<DocumentData> l = new ArrayList<DocumentData>(1);
       l.add((DocumentData)data);
       return l;
    }
   return Collections.emptySet();
    
  }

  /* (non-Javadoc)
   * @see org.xcmis.spi.Storage#getChangeLog(java.lang.String)
   */
  public ItemsIterator<ChangeEvent> getChangeLog(String changeLogToken)
      throws ConstraintException {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see org.xcmis.spi.Storage#getCheckedOutDocuments(org.xcmis.spi.ObjectData, java.lang.String)
   */
  public ItemsIterator<DocumentData> getCheckedOutDocuments(ObjectData folder,
      String orderBy) {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see org.xcmis.spi.Storage#getId()
   */
  public String getId() {
    return repositoryInfo.getRepositoryId();
  }

  /* (non-Javadoc)
   * @see org.xcmis.spi.Storage#getRenditions(org.xcmis.spi.ObjectData)
   */
  public ItemsIterator<Rendition> getRenditions(ObjectData object) {
    return CmisUtils.emptyItemsIterator();
    //throw new UnsupportedOperationException();
  }



  /* (non-Javadoc)
   * @see org.xcmis.spi.Storage#moveObject(org.xcmis.spi.ObjectData, org.xcmis.spi.FolderData, org.xcmis.spi.FolderData)
   */
  public ObjectData moveObject(ObjectData object, FolderData target,
      FolderData source) throws ConstraintException, InvalidArgumentException,
      UpdateConflictException, VersioningException,
      NameConstraintViolationException, StorageException {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see org.xcmis.spi.Storage#query(org.xcmis.spi.query.Query)
   */
  public ItemsIterator<Result> query(Query query)
      throws InvalidArgumentException {
    throw new UnsupportedOperationException();
  }


  /* (non-Javadoc)
   * @see org.xcmis.spi.Storage#unfileObject(org.xcmis.spi.ObjectData)
   */
  public void unfileObject(ObjectData object) {
    throw new UnsupportedOperationException();
  }

/* (non-Javadoc)
 * @see org.xcmis.spi.Storage#getUnfiledObjectsId()
 */
public Iterator<String> getUnfiledObjectsId() throws StorageException {
	throw new UnsupportedOperationException();
}
  
  


}
