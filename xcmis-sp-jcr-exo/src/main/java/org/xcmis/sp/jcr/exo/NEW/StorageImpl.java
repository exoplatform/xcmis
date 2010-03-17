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

package org.xcmis.sp.jcr.exo.NEW;

import org.exoplatform.services.jcr.core.ExtendedSession;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.sp.jcr.exo.JcrCMIS;
import org.xcmis.spi.ACLCapability;
import org.xcmis.spi.AllowableActions;
import org.xcmis.spi.BaseType;
import org.xcmis.spi.CapabilityACL;
import org.xcmis.spi.CapabilityChanges;
import org.xcmis.spi.CapabilityContentStreamUpdatable;
import org.xcmis.spi.CapabilityJoin;
import org.xcmis.spi.CapabilityQuery;
import org.xcmis.spi.CapabilityRendition;
import org.xcmis.spi.ChangeEvent;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentStreamAllowed;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.NotSupportedException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.Rendition;
import org.xcmis.spi.RepositoryCapabilities;
import org.xcmis.spi.RepositoryInfo;
import org.xcmis.spi.Storage;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.TypeDefinition;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.UnfileObject;
import org.xcmis.spi.Updatability;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.VersioningState;
import org.xcmis.spi.data.DocumentData;
import org.xcmis.spi.data.FolderData;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.data.PolicyData;
import org.xcmis.spi.data.RelationshipData;
import org.xcmis.spi.impl.AllowableActionsImpl;
import org.xcmis.spi.impl.CmisVisitor;
import org.xcmis.spi.query.Query;
import org.xcmis.spi.query.Result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeType;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class StorageImpl implements Storage, RepositoryInfo, RepositoryCapabilities
{

   private static final Log LOG = ExoLogger.getLogger(StorageImpl.class);

   static String latestLabel = "latest";

   static String pwcLabel = "pwc";

   /**
    * Get CMIS object type id by the JCR node type name.
    * 
    * @param ntName the JCR node type name
    * @return CMIS object type id
    */
   public static String getCmisTypeId(String ntName)
   {
      if (ntName.equals(JcrCMIS.NT_FILE))
         return BaseType.DOCUMENT.value();
      if (ntName.equals(JcrCMIS.NT_FOLDER) || ntName.equals(JcrCMIS.NT_UNSTRUCTURED))
         return BaseType.FOLDER.value();
      return ntName;
   }

   /**
    * Get JCR node type name by the CMIS object type id.
    * 
    * @param typeId the CMIS base object type id
    * @return JCR string node type
    */
   public static String getNodeTypeName(String typeId)
   {
      if (typeId.equals(BaseType.DOCUMENT.value()))
         return JcrCMIS.NT_FILE;
      if (typeId.equals(BaseType.FOLDER.value()))
         return JcrCMIS.NT_FOLDER;
      return typeId;
   }

   protected final Session session;

   private String storageID;

   public StorageImpl(Session session)
   {
      this.session = session;
   }

   /**
    * {@inheritDoc}
    */
   public AllowableActions calculateAllowableActions(ObjectData object)
   {
      AllowableActionsImpl actions = new AllowableActionsImpl();
      TypeDefinition type = object.getTypeDefinition();

      actions.setCanGetProperties(true);

      actions.setCanUpdateProperties(true); // TODO : need to check is it latest version ??

      actions.setCanApplyACL(type.isControllableACL());

      actions.setCanGetACL(type.isControllableACL());

      actions.setCanApplyPolicy(type.isControllablePolicy());

      actions.setCanGetAppliedPolicies(type.isControllablePolicy());

      actions.setCanRemovePolicy(type.isControllablePolicy());

      actions.setCanGetObjectParents(type.isFileable());

      actions.setCanMoveObject(type.isFileable());

      actions.setCanAddObjectToFolder(getCapabilities().isCapabilityMultifiling() //
         && type.isFileable() //
         && type.getBaseId() != BaseType.FOLDER);

      actions.setCanRemoveObjectFromFolder(getCapabilities().isCapabilityUnfiling() //
         && type.isFileable() //
         && type.getBaseId() != BaseType.FOLDER);

      actions.setCanGetDescendants(getCapabilities().isCapabilityGetDescendants() //
         && type.getBaseId() == BaseType.FOLDER);

      actions.setCanGetFolderTree(getCapabilities().isCapabilityGetFolderTree() //
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

      actions.setCanGetRenditions(getCapabilities().getCapabilityRenditions() == CapabilityRendition.READ);

      boolean isCheckedout = type.getBaseId() == BaseType.DOCUMENT //
         && type.isVersionable() //
         && ((DocumentData)object).isVersionSeriesCheckedOut();

      actions.setCanCheckIn(isCheckedout);

      actions.setCanCancelCheckOut(isCheckedout);

      actions.setCanCheckOut(!isCheckedout);

      actions.setCanGetObjectRelationships(type.getBaseId() != BaseType.RELATIONSHIP);

      actions.setCanCreateRelationship(type.getBaseId() != BaseType.RELATIONSHIP);

      // TODO : applied policy, not empty folders, not latest versions may not be delete.
      actions.setCanDeleteObject(true);

      return actions;
   }

   public DocumentData createCopyOfDocument(DocumentData source, FolderData folder, VersioningState versioningState)
      throws ConstraintException, StorageException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public DocumentData createDocument(FolderData folder, String typeId, VersioningState versioningState)
      throws ConstraintException
   {
      // TODO : remove when implement unfiling feature.
      if (folder == null) // Exception should be raised before but re-check to avoid NPE.
         throw new NotSupportedException("Unfiling capability is not supported.");

      if (!folder.isAllowedChildType(typeId))
         throw new ConstraintException("Type " + typeId + " is not in list of allowed child type for folder "
            + folder.getObjectId());

      TypeDefinition typeDefinition = getTypeDefinition(typeId, true);

      if (typeDefinition.getBaseId() != BaseType.DOCUMENT)
         throw new ConstraintException("Type " + typeId + " is ID of type whose base type is not Document.");

      DocumentData document = (DocumentData)folder.createChild(typeDefinition);

      return document;
   }

   /**
    * {@inheritDoc}
    */
   public FolderData createFolder(FolderData folder, String typeId) throws ConstraintException
   {
      if (folder == null) // Exception should be raised before but re-check to avoid NPE.
         throw new NotSupportedException("Parent folder must be provided.");

      if (!folder.isAllowedChildType(typeId))
         throw new ConstraintException("Type " + typeId + " is not in list of allowed child type for folder "
            + folder.getObjectId());

      TypeDefinition typeDefinition = getTypeDefinition(typeId, true);

      if (typeDefinition.getBaseId() != BaseType.FOLDER)
         throw new ConstraintException("Type " + typeId + " is ID of type whose base type is not Folder.");

      FolderData newFolder = (FolderData)folder.createChild(typeDefinition);

      return newFolder;
   }

   /**
    * {@inheritDoc}
    */
   public PolicyData createPolicy(FolderData folder, String typeId) throws ConstraintException
   {
      // TODO : remove when implement unfiling feature.
      if (folder == null) // Exception should be raised before but re-check to avoid NPE.
         throw new NotSupportedException("Unfiling capability is not supported.");

      if (!folder.isAllowedChildType(typeId))
         throw new ConstraintException("Type " + typeId + " is not in list of allowed child type for folder "
            + folder.getObjectId());

      TypeDefinition typeDefinition = getTypeDefinition(typeId, true);

      if (typeDefinition.getBaseId() != BaseType.POLICY)
         throw new ConstraintException("Type " + typeId + " is ID of type whose base type is not Policy.");

      PolicyData policy = (PolicyData)folder.createChild(typeDefinition);

      return policy;
   }

   public RelationshipData createRelationship(ObjectData source, ObjectData target, String typeId)
      throws ConstraintException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public void deleteObject(ObjectData object, boolean deleteAllVersions) throws ConstraintException,
      UpdateConflictException, StorageException
   {
      if (object.getBaseType() == BaseType.DOCUMENT)
      {
         // Throw exception to avoid unexpected removing data. 
         // Any way at the moment we are not able remove 'base version' of
         // versionable node, so have not common behavior.
         if (object.getTypeDefinition().isVersionable() && !deleteAllVersions)
            throw new CmisRuntimeException("Unable delete only specified version.");
      }
      else if (object.getBaseType() == BaseType.FOLDER)
      {
         if (((FolderData)object).isRoot())
            throw new ConstraintException("Root folder can't be removed.");

         if (((FolderData)object).hasChildren())
            throw new ConstraintException("Failed delete object. Object " + object
               + " is Folder and contains one or more objects.");
      }
      else if (object.getBaseType() == BaseType.POLICY)
      {
         // TODO : check is policy applied to any object
      }
      
      ((ObjectDataImpl)object).delete();
   }

   /**
    * {@inheritDoc}
    */
   public Collection<String> deleteTree(FolderData folder, boolean deleteAllVersions, UnfileObject unfileObject,
      boolean continueOnFailure) throws UpdateConflictException
   {
      final List<String> failedToDelete = new ArrayList<String>();
      try
      {
         deleteObject(folder, deleteAllVersions);
      }
      catch (StorageException se)
      {
         // Objects in the folder tree that were not deleted.
         // All or nothing should be removed in this implementation,
         // so return list of all object's IDs in this tree.
         LOG.warn(">>> " + se.getMessage(), se);
         try
         {
            // Discard all changes in session.
            session.refresh(false);
         }
         catch (RepositoryException re)
         {
            throw new CmisRuntimeException(re.getMessage(), re);
         }
         folder.accept(new CmisVisitor()
         {
            public void visit(ObjectData object)
            {
               if (object.getBaseType() == BaseType.FOLDER)
               {
                  for (ItemsIterator<ObjectData> children = ((FolderData)object).getChildren(null); children.hasNext();)
                     children.next().accept(this);
               }
               failedToDelete.add(object.getObjectId());
            }
         });
      }
      return failedToDelete;
   }

   public Collection<DocumentData> getAllVersions(String versionSeriesId) throws ObjectNotFoundException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<ChangeEvent> getChangeLog(String changeLogToken) throws ConstraintException
   {
      throw new NotSupportedException("Changes log feature is not supported.");
   }

   public ItemsIterator<ObjectData> getCheckedOutDocuments(ObjectData folder, String orderBy)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public String getId()
   {
      return storageID;
   }

   public ObjectData getObject(String objectId) throws ObjectNotFoundException
   {
      try
      {
         Node node = ((ExtendedSession)session).getNodeByIdentifier(objectId);
         TypeDefinition type = getTypeDefinition(node.getPrimaryNodeType(), true);
         return new ObjectDataImpl(node, type);
      }
      catch (ItemNotFoundException nfe)
      {
         return null;
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException(re.getMessage(), re);
      }
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

   public ObjectData moveObject(ObjectData object, FolderData target, FolderData source) throws ConstraintException,
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

   public void saveObject(ObjectData object) throws StorageException, NameConstraintViolationException,
      UpdateConflictException
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

   public ACLCapability getAclCapability()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public RepositoryCapabilities getCapabilities()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public boolean getChangesIncomplete()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public Collection<BaseType> getChangesOnType()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getCmisVersionSupported()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getLatestChangeLogToken()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getPrincipalAnonymous()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getPrincipalAnyone()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getProductName()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getProductVersion()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getRepositoryDescription()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getRepositoryId()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getRepositoryName()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getRootFolderId()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getThinClientURI()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getVendorName()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public CapabilityACL getCapabilityACL()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public CapabilityChanges getCapabilityChanges()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public CapabilityContentStreamUpdatable getCapabilityContentStreamUpdatable()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public CapabilityJoin getCapabilityJoin()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public CapabilityQuery getCapabilityQuery()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public CapabilityRendition getCapabilityRenditions()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public boolean isCapabilityAllVersionsSearchable()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isCapabilityGetDescendants()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isCapabilityGetFolderTree()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isCapabilityMultifiling()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isCapabilityPWCSearchable()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isCapabilityPWCUpdatable()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isCapabilityUnfiling()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isCapabilityVersionSpecificFiling()
   {
      // TODO Auto-generated method stub
      return false;
   }
   
   // ------------ Implementation -----------------
   
   /**
    * Get object type definition.
    * 
    * @param nt JCR back-end node
    * @param includePropertyDefinition true if need include property definition
    *        false otherwise
    * @return object definition or <code>null</code> if specified JCR node-type
    *         has not corresponded CMIS type
    * @throws NotSupportedNodeTypeException if specified node-type is
    *         unsupported by xCMIS
    */
   public TypeDefinition getTypeDefinition(NodeType nt, boolean includePropertyDefinition)
      throws NotSupportedNodeTypeException
   {
      if (nt.isNodeType(JcrCMIS.NT_FILE))
         return getDocumentDefinition(nt, includePropertyDefinition);
      else if (nt.isNodeType(JcrCMIS.NT_FOLDER) || nt.isNodeType(JcrCMIS.NT_UNSTRUCTURED))
         return getFolderDefinition(nt, includePropertyDefinition);
      else if (nt.isNodeType(JcrCMIS.CMIS_NT_RELATIONSHIP))
         return getRelationshipDefinition(nt, includePropertyDefinition);
      else if (nt.isNodeType(JcrCMIS.CMIS_NT_POLICY))
         return getPolicyDefinition(nt, includePropertyDefinition);
      else
         throw new NotSupportedNodeTypeException("Type " + nt.getName() + " is unsupported for xCMIS.");
   }

   /**
    * Document type definition.
    * 
    * @param nt node type
    * @param includePropertyDefinition true if need include property definition
    *        false otherwise
    * @return document type definition
    */
   protected CmisTypeDocumentDefinitionType getDocumentDefinition(NodeType nt, boolean includePropertyDefinition)
   {
      CmisTypeDocumentDefinitionType def = new CmisTypeDocumentDefinitionType();
      String localTypeName = nt.getName();
      String typeId = getCmisTypeId(localTypeName);
      def.setBaseId(EnumBaseObjectTypeIds.CMIS_DOCUMENT);
      def.setContentStreamAllowed(EnumContentStreamAllowed.ALLOWED);
      def.setControllableACL(true);
      def.setControllablePolicy(true);
      def.setCreatable(true);
      def.setDescription("Cmis Document Type");
      def.setDisplayName(typeId);
      def.setFileable(true);
      def.setFulltextIndexed(true);
      def.setId(typeId);
      def.setIncludedInSupertypeQuery(true);
      def.setLocalName(localTypeName);
      def.setLocalNamespace(JcrCMIS.EXO_CMIS_NS_URI);
      if (typeId.equals(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value()))
      {
         def.setParentId(null); // no parents for root type
      }
      else
      {
         // Try determine parent type.
         NodeType[] superTypes = nt.getDeclaredSupertypes();
         for (NodeType superType : superTypes)
         {
            if (superType.isNodeType(JcrCMIS.NT_FILE))
            {
               // Take first type that is super for cmis:document or is cmis:document.
               def.setParentId(getCmisTypeId(superType.getName()));
               break;
            }
         }
      }
      def.setQueryable(true);
      def.setQueryName(typeId);
      def.setVersionable(true);
      if (includePropertyDefinition)
         addPropertyDefinitions(def, nt);
      return def;
   }

   /**
    * Folder type definition.
    * 
    * @param nt node type
    * @param includePropertyDefinition true if need include property definition
    *        false otherwise
    * @return folder type definition
    */
   protected CmisTypeFolderDefinitionType getFolderDefinition(NodeType nt, boolean includePropertyDefinition)
   {
      CmisTypeFolderDefinitionType def = new CmisTypeFolderDefinitionType();
      String localTypeName = nt.getName();
      String typeId = getCmisTypeId(localTypeName);
      def.setBaseId(EnumBaseObjectTypeIds.CMIS_FOLDER);
      def.setControllableACL(true);
      def.setControllablePolicy(true);
      def.setCreatable(true);
      def.setDescription("Cmis Folder Type");
      def.setDisplayName(typeId);
      def.setFileable(true);
      def.setFulltextIndexed(false);
      def.setId(typeId);
      def.setIncludedInSupertypeQuery(true);
      def.setLocalName(localTypeName);
      def.setLocalNamespace(JcrCMIS.EXO_CMIS_NS_URI);
      if (typeId.equals(EnumBaseObjectTypeIds.CMIS_FOLDER.value()))
      {
         def.setParentId(null); // no parents for root type
      }
      else
      {
         // Try determine parent type.
         NodeType[] superTypes = nt.getDeclaredSupertypes();
         for (NodeType superType : superTypes)
         {
            if (superType.isNodeType(JcrCMIS.NT_FOLDER))
            {
               // Take first type that is super for cmis:folder or is cmis:folder.
               def.setParentId(getCmisTypeId(superType.getName()));
               break;
            }
         }
      }
      def.setQueryable(true);
      def.setQueryName(typeId);
      if (includePropertyDefinition)
         addPropertyDefinitions(def, nt);
      return def;
   }

   /**
    * Get policy type definition.
    * 
    * @param nt node type
    * @param includePropertyDefinition true if need include property definition
    *        false otherwise
    * @return type policy definition
    */
   protected CmisTypeDefinitionType getPolicyDefinition(NodeType nt, boolean includePropertyDefinition)
   {
      CmisTypePolicyDefinitionType def = new CmisTypePolicyDefinitionType();
      String localTypeName = nt.getName();
      String typeId = getCmisTypeId(localTypeName);
      def.setBaseId(EnumBaseObjectTypeIds.CMIS_POLICY);
      def.setControllableACL(true);
      def.setControllablePolicy(true);
      def.setCreatable(true);
      def.setDescription("Cmis Policy Type");
      def.setDisplayName(typeId);
      def.setFileable(true);
      def.setFulltextIndexed(false);
      def.setId(typeId);
      def.setIncludedInSupertypeQuery(true);
      def.setLocalName(localTypeName);
      def.setLocalNamespace(JcrCMIS.EXO_CMIS_NS_URI);
      if (typeId.equals(EnumBaseObjectTypeIds.CMIS_POLICY.value()))
      {
         def.setParentId(null); // no parents for root type
      }
      else
      {
         // Try determine parent type.
         NodeType[] superTypes = nt.getDeclaredSupertypes();
         for (NodeType superType : superTypes)
         {
            if (superType.isNodeType(JcrCMIS.CMIS_NT_POLICY))
            {
               // Take first type that is super for cmis:policy or is cmis:policy.
               def.setParentId(getCmisTypeId(superType.getName()));
               break;
            }
         }
      }
      def.setQueryable(false);
      def.setQueryName(typeId);
      if (includePropertyDefinition)
         addPropertyDefinitions(def, nt);
      return def;
   }

   /**
    * Get relationship type definition.
    * 
    * @param nt node type
    * @param includePropertyDefinition true if need include property definition
    *        false otherwise
    * @return type relationship definition
    */
   protected CmisTypeRelationshipDefinitionType getRelationshipDefinition(NodeType nt, boolean includePropertyDefinition)
   {
      CmisTypeRelationshipDefinitionType def = new CmisTypeRelationshipDefinitionType();
      String localTypeName = nt.getName();
      String typeId = getCmisTypeId(localTypeName);
      def.setBaseId(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP);
      def.setControllableACL(false);
      def.setControllablePolicy(false);
      def.setCreatable(true);
      def.setDescription("Cmis Relationship Type");
      def.setDisplayName(typeId);
      def.setFileable(false);
      def.setFulltextIndexed(false);
      def.setId(typeId);
      def.setIncludedInSupertypeQuery(false);
      def.setLocalName(localTypeName);
      def.setLocalNamespace(JcrCMIS.EXO_CMIS_NS_URI);
      if (typeId.equals(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value()))
      {
         def.setParentId(null); // no parents for root type
      }
      else
      {
         // Try determine parent type.
         NodeType[] superTypes = nt.getDeclaredSupertypes();
         for (NodeType superType : superTypes)
         {
            if (superType.isNodeType(JcrCMIS.CMIS_NT_RELATIONSHIP))
            {
               // Take first type that is super for cmis:relationship or is cmis:relationship.
               def.setParentId(getCmisTypeId(superType.getName()));
               break;
            }
         }
      }
      def.setQueryable(false);
      def.setQueryName(typeId);
      if (includePropertyDefinition)
         addPropertyDefinitions(def, nt);
      return def;
   }

   /**
    * Add property definitions.
    * 
    * @param typeDefinition the object type definition
    * @param nt the JCR node type.
    */
   private void addPropertyDefinitions(CmisTypeDefinitionType typeDefinition, NodeType nt)
   {
      // Known described in spec. property definitions
      for (CmisPropertyDefinitionType propDef : PropertyDefinitionsMap.getAll(typeDefinition.getBaseId().value()))
         typeDefinition.getPropertyDefinition().add(propDef);

      Set<String> knownIds = PropertyDefinitionsMap.getPropertyIds(typeDefinition.getBaseId().value());
      for (javax.jcr.nodetype.PropertyDefinition pd : nt.getPropertyDefinitions())
      {
         String pdName = pd.getName();
         // TODO : Do not use any constraint about prefixes, need discovery
         // hierarchy of JCR types or so on.
         if (pdName.startsWith("cmis:"))
         {
            // Do not process known properties
            if (!knownIds.contains(pdName))
            {
               CmisPropertyDefinitionType cmisPropDef = null;
               switch (pd.getRequiredType())
               {
                  case javax.jcr.PropertyType.BOOLEAN :
                     CmisPropertyBooleanDefinitionType boolDef = new CmisPropertyBooleanDefinitionType();
                     boolDef.setPropertyType(EnumPropertyType.BOOLEAN);
                     cmisPropDef = boolDef;
                     break;
                  case javax.jcr.PropertyType.DATE :
                     CmisPropertyDateTimeDefinitionType dateDef = new CmisPropertyDateTimeDefinitionType();
                     dateDef.setPropertyType(EnumPropertyType.DATETIME);
                     dateDef.setResolution(EnumDateTimeResolution.TIME);
                     cmisPropDef = dateDef;
                     break;
                  case javax.jcr.PropertyType.DOUBLE :
                     CmisPropertyDecimalDefinitionType decimalDef = new CmisPropertyDecimalDefinitionType();
                     decimalDef.setPrecision(CMIS.PRECISION);
                     decimalDef.setPropertyType(EnumPropertyType.DECIMAL);
                     cmisPropDef = decimalDef;
                     break;
                  case javax.jcr.PropertyType.LONG :
                     CmisPropertyIntegerDefinitionType integerDef = new CmisPropertyIntegerDefinitionType();
                     integerDef.setMaxValue(CMIS.MAX_INTEGER_VALUE);
                     integerDef.setMinValue(CMIS.MIN_INTEGER_VALUE);
                     integerDef.setPropertyType(EnumPropertyType.INTEGER);
                     cmisPropDef = integerDef;
                     break;
                  case javax.jcr.PropertyType.NAME : // TODO
                     //                     CmisPropertyIdDefinitionType idDef = new CmisPropertyIdDefinitionType();
                     //                     idDef.setPropertyType(EnumPropertyType.ID);
                     //                     cmisPropDef = idDef;
                     //                     break;
                  case javax.jcr.PropertyType.REFERENCE :
                  case javax.jcr.PropertyType.STRING :
                  case javax.jcr.PropertyType.PATH :
                  case javax.jcr.PropertyType.BINARY :
                  case javax.jcr.PropertyType.UNDEFINED :
                     CmisPropertyStringDefinitionType stringDef = new CmisPropertyStringDefinitionType();
                     stringDef.setPropertyType(EnumPropertyType.STRING);
                     stringDef.setMaxLength(CMIS.MAX_STRING_LENGTH);
                     cmisPropDef = stringDef;
                     break;
               }
               // TODO : default values.
               cmisPropDef.setM(pd.isMultiple() ? EnumCardinality.MULTI : Updatability.SINGLE);
               cmisPropDef.setDescription("");
               cmisPropDef.setDisplayName(pdName);
               cmisPropDef.setId(pdName);
               cmisPropDef.setInherited(false);
               cmisPropDef.setLocalName(pdName);
               cmisPropDef.setOrderable(true);
               cmisPropDef.setQueryable(true);
               cmisPropDef.setQueryName(pdName);
               cmisPropDef.setRequired(pd.isMandatory());
               cmisPropDef.setUpdatability(pd.isProtected() ? Updatability.READONLY : Updatability.READWRITE);
               typeDefinition.getPropertyDefinition().add(cmisPropDef);
            }
         }
      }
   }

   protected NodeType getNodeType(String name) throws NoSuchNodeTypeException, javax.jcr.RepositoryException
   {
      NodeType nt = session.getWorkspace().getNodeTypeManager().getNodeType(name);
      return nt;
   }

}
