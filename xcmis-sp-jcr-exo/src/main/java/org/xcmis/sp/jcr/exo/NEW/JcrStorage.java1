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

import org.exoplatform.services.jcr.access.SystemIdentity;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.core.ExtendedSession;
import org.exoplatform.services.jcr.core.nodetype.ExtendedNodeTypeManager;
import org.exoplatform.services.jcr.core.nodetype.NodeTypeValue;
import org.exoplatform.services.jcr.core.nodetype.PropertyDefinitionValue;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.core.CmisACLCapabilityType;
import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.CmisAllowableActionsType;
import org.xcmis.core.CmisPermissionDefinition;
import org.xcmis.core.CmisPermissionMapping;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisPropertyBoolean;
import org.xcmis.core.CmisPropertyBooleanDefinitionType;
import org.xcmis.core.CmisPropertyDateTime;
import org.xcmis.core.CmisPropertyDateTimeDefinitionType;
import org.xcmis.core.CmisPropertyDecimal;
import org.xcmis.core.CmisPropertyDecimalDefinitionType;
import org.xcmis.core.CmisPropertyDefinitionType;
import org.xcmis.core.CmisPropertyHtml;
import org.xcmis.core.CmisPropertyHtmlDefinitionType;
import org.xcmis.core.CmisPropertyId;
import org.xcmis.core.CmisPropertyIdDefinitionType;
import org.xcmis.core.CmisPropertyInteger;
import org.xcmis.core.CmisPropertyIntegerDefinitionType;
import org.xcmis.core.CmisPropertyString;
import org.xcmis.core.CmisPropertyStringDefinitionType;
import org.xcmis.core.CmisPropertyUri;
import org.xcmis.core.CmisPropertyUriDefinitionType;
import org.xcmis.core.CmisRenditionType;
import org.xcmis.core.CmisRepositoryCapabilitiesType;
import org.xcmis.core.CmisRepositoryInfoType;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.CmisTypeDocumentDefinitionType;
import org.xcmis.core.CmisTypeFolderDefinitionType;
import org.xcmis.core.CmisTypePolicyDefinitionType;
import org.xcmis.core.CmisTypeRelationshipDefinitionType;
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
import org.xcmis.core.EnumCardinality;
import org.xcmis.core.EnumContentStreamAllowed;
import org.xcmis.core.EnumDateTimeResolution;
import org.xcmis.core.EnumPropertyType;
import org.xcmis.core.EnumSupportedPermissions;
import org.xcmis.core.EnumUnfileObject;
import org.xcmis.core.EnumUpdatability;
import org.xcmis.core.EnumVersioningState;
import org.xcmis.sp.jcr.exo.JcrCMIS;
import org.xcmis.spi.BaseItemsIterator;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ChangeEvent;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.NotSupportedException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.Storage;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.impl.CmisVisitor;
import org.xcmis.spi.query.Query;
import org.xcmis.spi.query.Result;
import org.xcmis.spi.utils.CmisUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.jcr.Item;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.version.OnParentVersionAction;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JcrStorage implements Storage
{

   private static final Log LOG = ExoLogger.getLogger(JcrStorage.class);

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
         return EnumBaseObjectTypeIds.CMIS_DOCUMENT.value();
      if (ntName.equals(JcrCMIS.NT_FOLDER) || ntName.equals(JcrCMIS.NT_UNSTRUCTURED))
         return EnumBaseObjectTypeIds.CMIS_FOLDER.value();
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
      if (typeId.equals(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value()))
         return JcrCMIS.NT_FILE;
      if (typeId.equals(EnumBaseObjectTypeIds.CMIS_FOLDER.value()))
         return JcrCMIS.NT_FOLDER;
      return typeId;
   }

   protected final Session session;

   private CmisRepositoryInfoType info;

   private String storageID;

   public JcrStorage(Session session)
   {
      this.session = session;
   }

   public void addObjectToFolder(ObjectData object, ObjectData folder, boolean allVersions) throws CmisRuntimeException
   {
      throw new NotSupportedException("Multi-filing is not supported.");
   }

   public CmisAllowableActionsType calculateAllowableActions(ObjectData object) throws CmisRuntimeException
   {
      CmisAllowableActionsType actions = new CmisAllowableActionsType();
      CmisTypeDefinitionType type = ((ObjectDataImpl)object).getType();
      actions.setCanAddObjectToFolder(type.isFileable() && type.getBaseId() != EnumBaseObjectTypeIds.CMIS_FOLDER);
      actions.setCanApplyACL(type.isControllableACL());
      actions.setCanApplyPolicy(type.isControllablePolicy());
      actions.setCanCancelCheckOut(type.getBaseId() == EnumBaseObjectTypeIds.CMIS_DOCUMENT
         && ((CmisTypeDocumentDefinitionType)type).isVersionable())/* TODO */;
      actions.setCanCheckIn(type.getBaseId() == EnumBaseObjectTypeIds.CMIS_DOCUMENT
         && ((CmisTypeDocumentDefinitionType)type).isVersionable())/* TODO */;
      actions.setCanCheckOut(type.getBaseId() == EnumBaseObjectTypeIds.CMIS_DOCUMENT
         && ((CmisTypeDocumentDefinitionType)type).isVersionable())/* TODO */;
      actions.setCanCreateDocument(type.getBaseId() == EnumBaseObjectTypeIds.CMIS_FOLDER);
      actions.setCanCreateFolder(type.getBaseId() == EnumBaseObjectTypeIds.CMIS_FOLDER);
      actions.setCanCreateRelationship(type.getBaseId() != EnumBaseObjectTypeIds.CMIS_RELATIONSHIP);
      actions.setCanDeleteObject(true) /* TODO */;
      actions.setCanDeleteContentStream(type.getBaseId() == EnumBaseObjectTypeIds.CMIS_DOCUMENT
         && ((CmisTypeDocumentDefinitionType)type).getContentStreamAllowed() != EnumContentStreamAllowed.REQUIRED);
      actions.setCanDeleteTree(type.getBaseId() == EnumBaseObjectTypeIds.CMIS_FOLDER);
      actions.setCanGetACL(type.isControllableACL());
      actions.setCanGetAllVersions(type.getBaseId() == EnumBaseObjectTypeIds.CMIS_DOCUMENT);
      actions.setCanGetAppliedPolicies(type.isControllablePolicy());
      actions.setCanGetChildren(type.getBaseId() == EnumBaseObjectTypeIds.CMIS_FOLDER);
      actions.setCanGetContentStream(type.getBaseId() == EnumBaseObjectTypeIds.CMIS_DOCUMENT
         && ((CmisTypeDocumentDefinitionType)type).getContentStreamAllowed() != EnumContentStreamAllowed.NOTALLOWED);
      actions.setCanGetDescendants(type.getBaseId() == EnumBaseObjectTypeIds.CMIS_FOLDER);
      actions.setCanGetFolderParent(type.getBaseId() == EnumBaseObjectTypeIds.CMIS_FOLDER);
      actions.setCanGetFolderTree(type.getBaseId() == EnumBaseObjectTypeIds.CMIS_FOLDER);
      actions.setCanGetObjectParents(type.isFileable());
      actions.setCanGetObjectRelationships(type.getBaseId() != EnumBaseObjectTypeIds.CMIS_RELATIONSHIP);
      actions.setCanGetProperties(true);
      actions.setCanGetRenditions(type.getBaseId() == EnumBaseObjectTypeIds.CMIS_DOCUMENT);
      actions.setCanMoveObject(type.isFileable());
      actions.setCanRemoveObjectFromFolder(type.isFileable());
      actions.setCanRemovePolicy(type.isControllablePolicy());
      actions.setCanSetContentStream(type.getBaseId() == EnumBaseObjectTypeIds.CMIS_DOCUMENT
         && ((CmisTypeDocumentDefinitionType)type).getContentStreamAllowed() != EnumContentStreamAllowed.NOTALLOWED);
      actions.setCanUpdateProperties(true) /* TODO */;
      return actions;
      // TODO Auto-generated method stub
   }

   public void cancelCheckout(String versionSeriesId) throws StorageException, CmisRuntimeException
   {
      // TODO Auto-generated method stub

   }

   public ObjectData checkin(ObjectData document, boolean major, CmisPropertiesType properties, ContentStream content,
      String checkinComment, CmisAccessControlListType addACL, CmisAccessControlListType removeACL,
      Collection<String> policies) throws IOException, StorageException, CmisRuntimeException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public ObjectData checkout(ObjectData document) throws VersioningException, StorageException, CmisRuntimeException
   {
      try
      {
         if (!document.isLatestVersion())
         {
            throw new ConstraintException("Only latest version of document may be checked-out.");
         }
         Node pwcStorage = (Node)session.getItem("/" + JcrCMIS.CMIS_SYSTEM + "/" + JcrCMIS.CMIS_WORKING_COPIES);
         String versionSeriesId = document.getVersionSeriesId();
         if (pwcStorage.hasNode(versionSeriesId) && pwcStorage.getNode(versionSeriesId).hasNodes())
         {
            // Should not happen, it must be already checked is version series
            // has been checked-out. Re-check just to be sure. 
            throw new VersioningException("PWC for version series " + versionSeriesId + " already exists.");
         }
         Node pwcHolder = pwcStorage.addNode(versionSeriesId, JcrCMIS.NT_UNSTRUCTURED);
         pwcStorage.save();
         
         String sourcePath = ((ObjectDataImpl)document).getPath();
         String destinationPath = pwcHolder.getPath() + "/" + document.getName();
         session.getWorkspace().copy(sourcePath, destinationPath);
         
         Node pwcNode = (Node)session.getItem(destinationPath);
         pwcNode.setProperty(CMIS.OBJECT_ID, ((ExtendedNode)pwcNode).getIdentifier());
         pwcNode.setProperty(CMIS.IS_MAJOR_VERSION, false);
         pwcNode.setProperty(CMIS.VERSION_LABEL, pwcLabel);
         Calendar date = Calendar.getInstance();
         pwcNode.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_ID, ((ExtendedNode)pwcNode).getIdentifier());
         pwcNode.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_BY, session.getUserID());
         pwcNode.setProperty(CMIS.CREATION_DATE, date);
         pwcNode.setProperty(CMIS.LAST_MODIFICATION_DATE, date);
         pwcNode.save();
      }
      catch (PathNotFoundException pe)
      {
         throw new StorageException("Unable create PWC. " + pe.getMessage());
      }
      catch (RepositoryException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      // TODO Auto-generated method stub
      return null;
   }

   public ObjectData createDocument(ObjectData folder, CmisTypeDefinitionType typeDefinition,
      CmisPropertiesType properties, ContentStream content, CmisAccessControlListType addAcl,
      CmisAccessControlListType removeACEs, Collection<String> policies, EnumVersioningState versioningState)
      throws StorageException, NameConstraintViolationException, IOException, CmisRuntimeException
   {
      // TODO : remove when implement unfiling feature.
      if (folder == null) // Exception should be raised before but re-check to avoid NPE.
         throw new NotSupportedException("Unfiling capability is not supported.");

      LOG.info(">>> Create document in " + folder + ", document type: " + typeDefinition.getId()
         + ", local type name: " + typeDefinition.getLocalName());
      String name = CmisUtils.getName(properties);
      if (name == null || name.length() == 0)
         throw new NameConstraintViolationException("Name for new document must be provided.");
      try
      {
         Node folderNode = ((ObjectDataImpl)folder).getNode();
         if (folderNode.hasNode(name))
            throw new NameConstraintViolationException("Object with name " + name
               + " already exists in specified folder.");
         Node documentNode = folderNode.addNode(name, typeDefinition.getLocalName());
         if (!documentNode.isNodeType(JcrCMIS.CMIS_MIX_DOCUMENT)) // May be already inherited.
            documentNode.addMixin(JcrCMIS.CMIS_MIX_DOCUMENT);
         if (documentNode.canAddMixin(JcrCMIS.MIX_VERSIONABLE)) // Document type is versionable.
            documentNode.addMixin(JcrCMIS.MIX_VERSIONABLE);

         // CMIS properties
         documentNode.setProperty(CMIS.OBJECT_ID, ((ExtendedNode)documentNode).getIdentifier());
         documentNode.setProperty(CMIS.NAME, name);
         documentNode.setProperty(CMIS.BASE_TYPE_ID, "cmis:document");
         documentNode.setProperty(CMIS.OBJECT_TYPE_ID, typeDefinition.getId());
         documentNode.setProperty(CMIS.VERSION_SERIES_ID, documentNode.getProperty("jcr:versionHistory").getString());
         documentNode.setProperty(CMIS.IS_LATEST_VERSION, true);
         documentNode.setProperty(CMIS.IS_MAJOR_VERSION, versioningState == EnumVersioningState.MAJOR);
         documentNode.setProperty(CMIS.VERSION_LABEL, versioningState == EnumVersioningState.CHECKEDOUT ? pwcLabel
            : latestLabel);
         documentNode
            .setProperty(CMIS.IS_VERSION_SERIES_CHECKED_OUT, versioningState == EnumVersioningState.CHECKEDOUT);
         if (versioningState == EnumVersioningState.CHECKEDOUT)
         {
            documentNode.addMixin("cmis:pwc");
            documentNode.setProperty(JcrCMIS.CMIS_LATEST_VERSION, documentNode);
            documentNode.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_ID, ((ExtendedNode)documentNode).getIdentifier());
            documentNode.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_BY, session.getUserID());
         }
         ObjectDataImpl newDocument = new ObjectDataImpl(documentNode, typeDefinition);
         setContentStream(newDocument, content);
         return newDocument;
      }
      catch (RepositoryException re)
      {
         throw new StorageException("Unable create new document. " + re.getMessage(), re);
      }
   }

   public ObjectData createDocumentFromSource(ObjectData source, ObjectData folder, CmisPropertiesType properties,
      CmisAccessControlListType addAcl, CmisAccessControlListType removeAcl, Collection<String> policies,
      EnumVersioningState versioningState) throws StorageException, NameConstraintViolationException,
      CmisRuntimeException
   {
      // TODO : remove when implement unfiling feature.
      if (folder == null) // Exception should be raised before but re-check to avoid NPE.
         throw new NotSupportedException("Unfiling capability is not supported.");
      LOG.info(">>> Create document in " + folder + ", from source: " + source);
      String name = CmisUtils.getName(properties);
      if (name == null || name.length() == 0)
         name = source.getName();
      try
      {
         String sourcePath = ((ObjectDataImpl)source).getPath();
         String destinationPath = ((ObjectDataImpl)folder).getPath();
         destinationPath += destinationPath.equals("/") ? name : ("/" + name);
         session.getWorkspace().copy(sourcePath, destinationPath);
         LOG.info("<<< Object copied in " + destinationPath);

         Node copyNode = (Node)session.getItem(destinationPath);
         copyNode.setProperty(CMIS.OBJECT_ID, ((ExtendedNode)copyNode).getIdentifier());
         copyNode.setProperty(CMIS.NAME, name);
         copyNode.setProperty(CMIS.VERSION_SERIES_ID, copyNode.getProperty("jcr:versionHistory").getString());
         copyNode.setProperty(CMIS.IS_LATEST_VERSION, true);
         copyNode.setProperty(CMIS.IS_MAJOR_VERSION, versioningState == EnumVersioningState.MAJOR);
         copyNode.setProperty(CMIS.VERSION_LABEL, versioningState == EnumVersioningState.CHECKEDOUT ? pwcLabel
            : latestLabel);
         copyNode.setProperty(CMIS.IS_VERSION_SERIES_CHECKED_OUT, versioningState == EnumVersioningState.CHECKEDOUT);
         if (versioningState == EnumVersioningState.CHECKEDOUT)
         {
            copyNode.addMixin("cmis:pwc");
            copyNode.setProperty(JcrCMIS.CMIS_LATEST_VERSION, copyNode);
            copyNode.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_ID, ((ExtendedNode)copyNode).getIdentifier());
            copyNode.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_BY, session.getUserID());
         }
         copyNode.setProperty(CMIS.CREATION_DATE, Calendar.getInstance());
         copyNode.setProperty(CMIS.CREATED_BY, session.getUserID());
         ObjectDataImpl copyDocument =
            new ObjectDataImpl(copyNode, getTypeDefinition(copyNode.getPrimaryNodeType(), true));
         return copyDocument;
      }
      catch (javax.jcr.RepositoryException re)
      {
         throw new StorageException("Unable to copy object. " + re.getMessage(), re);
      }
   }

   public ObjectData createFolder(ObjectData folder, CmisTypeDefinitionType typeDefinition,
      CmisPropertiesType properties, CmisAccessControlListType addAcl, CmisAccessControlListType removeAcl,
      Collection<String> policies) throws StorageException, NameConstraintViolationException, CmisRuntimeException
   {
      if (folder == null) // Exception should be raised before but re-check to avoid NPE.
         throw new NotSupportedException("Parent folder must be provided.");

      LOG.info(">>> Create folder in " + folder + ", folder type: " + typeDefinition.getId() + ", local type name: "
         + typeDefinition.getLocalName());
      String name = CmisUtils.getName(properties);
      if (name == null || name.length() == 0)
         throw new NameConstraintViolationException("Name for new folder must be provided.");
      try
      {
         Node folderNode = ((ObjectDataImpl)folder).getNode();
         if (folderNode.hasNode(name))
            throw new NameConstraintViolationException("Object with name " + name
               + " already exists in specified folder.");
         Node chilFolderNode = folderNode.addNode(name, typeDefinition.getLocalName());
         if (!chilFolderNode.isNodeType(JcrCMIS.CMIS_MIX_FOLDER)) // May be already inherited.
            chilFolderNode.addMixin(JcrCMIS.CMIS_MIX_FOLDER);

         // CMIS properties
         chilFolderNode.setProperty(CMIS.OBJECT_ID, ((ExtendedNode)chilFolderNode).getIdentifier());
         chilFolderNode.setProperty(CMIS.NAME, name);
         chilFolderNode.setProperty(CMIS.BASE_TYPE_ID, "cmis:folder");
         chilFolderNode.setProperty(CMIS.OBJECT_TYPE_ID, typeDefinition.getId());
         chilFolderNode.setProperty(CMIS.PARENT_ID, folder.getObjectId());
         return new ObjectDataImpl(chilFolderNode, typeDefinition);
      }
      catch (RepositoryException re)
      {
         throw new StorageException("Unable create new folder. " + re.getMessage(), re);
      }
   }

   public ObjectData createPolicy(ObjectData folder, CmisTypeDefinitionType typeDefinition,
      CmisPropertiesType properties, CmisAccessControlListType addAcl, CmisAccessControlListType removeAcl,
      Collection<String> policies) throws StorageException, NameConstraintViolationException, CmisRuntimeException
   {
      // TODO : remove when implement unfiling feature.
      if (folder == null) // Exception should be raised before but re-check to avoid NPE.
         throw new NotSupportedException("Unfiling capability is not supported.");

      LOG.info(">>> Create policy in " + folder + ", folder type: " + typeDefinition.getId() + ", local type name: "
         + typeDefinition.getLocalName());
      String name = CmisUtils.getName(properties);
      if (name == null || name.length() == 0)
         throw new NameConstraintViolationException("Name for new policy must be provided.");
      try
      {
         Node folderNode = ((ObjectDataImpl)folder).getNode();
         if (folderNode.hasNode(name))
            throw new NameConstraintViolationException("Object with name " + name
               + " already exists in specified folder.");
         Node policyNode = folderNode.addNode(name, typeDefinition.getLocalName());
         policyNode.setProperty(CMIS.OBJECT_ID, ((ExtendedNode)policyNode).getIdentifier());
         policyNode.setProperty(CMIS.NAME, name);
         policyNode.setProperty(CMIS.OBJECT_TYPE_ID, typeDefinition.getId());
         policyNode.setProperty(CMIS.BASE_TYPE_ID, "cmis:policy");
         policyNode.setProperty(CMIS.POLICY_TEXT, CmisUtils.getPolicyText(properties));
         return new ObjectDataImpl(policyNode, typeDefinition);
      }
      catch (RepositoryException re)
      {
         throw new StorageException("Unable create new policy. " + re.getMessage(), re);
      }
   }

   /**
    * Relationships stored in following manner.
    * 
    * <pre>
    *  /ROOT
    *   |
    *   - xcmis:system
    *     |
    *     - xcmis:relationships
    *     |
    *      - hierarchy node (container node for relationships that has the same name as source object Id)
    *        |
    *        - relationship (each one has the specified name)
    *        - ...
    * </pre>
    */
   public ObjectData createRelationship(CmisTypeDefinitionType typeDefinition, ObjectData source, ObjectData target,
      CmisPropertiesType properties, CmisAccessControlListType addAcl, CmisAccessControlListType removeAcl,
      Collection<String> policies) throws StorageException, NameConstraintViolationException, CmisRuntimeException
   {
      LOG.info(">>> Create relationship, source: " + source + ", target: " + target.getObjectId()
         + ", local type name: " + typeDefinition.getLocalName());
      String name = CmisUtils.getName(properties);
      if (name == null || name.length() == 0)
         throw new NameConstraintViolationException("Name for new relationship must be provided.");
      try
      {
         Node relationships = (Node)session.getItem("/" + JcrCMIS.CMIS_SYSTEM + "/" + JcrCMIS.CMIS_RELATIONSHIPS);
         Node relHierarchy;
         String tContName = source.getObjectId();
         if (!relationships.hasNode(tContName))
            relHierarchy = relationships.addNode(tContName, JcrCMIS.NT_UNSTRUCTURED);
         else
            relHierarchy = relationships.getNode(tContName);
         Node relationshipNode = relHierarchy.addNode(name, getNodeTypeName(typeDefinition.getLocalName()));
         relationshipNode.setProperty(CMIS.OBJECT_ID, ((ExtendedNode)relationshipNode).getIdentifier());
         relationshipNode.setProperty(CMIS.NAME, name);
         relationshipNode.setProperty(CMIS.OBJECT_TYPE_ID, typeDefinition.getId());
         relationshipNode.setProperty(CMIS.BASE_TYPE_ID, "cmis:relationship");
         relationshipNode.setProperty(CMIS.SOURCE_ID, ((ObjectDataImpl)source).getNode());
         relationshipNode.setProperty(CMIS.TARGET_ID, ((ObjectDataImpl)target).getNode());
         return new ObjectDataImpl(relationshipNode, typeDefinition);
      }
      catch (RepositoryException re)
      {
         throw new StorageException("Unable create new relationship. " + re.getMessage(), re);
      }
   }

   public void deleteContentStream(ObjectData document) throws StorageException, CmisRuntimeException
   {
      try
      {
         ((ObjectDataImpl)document).setContentStream(null);
      }
      catch (IOException e)
      {
         // Should never happen because stream in null.
         throw new CmisRuntimeException("Unexpected error. " + e.getMessage(), e);
      }
   }

   public void deleteObject(ObjectData object, boolean deleteAllVersions) throws UpdateConflictException,
      StorageException, CmisRuntimeException
   {
      if (object.getBaseType() == EnumBaseObjectTypeIds.CMIS_DOCUMENT)
      {
         CmisTypeDocumentDefinitionType type = (CmisTypeDocumentDefinitionType)((ObjectDataImpl)object).getType();
         // Throw exception to avoid unexpected removing data. 
         // Any way at the moment we are not able remove 'base version' of
         // versionable node, so have not common behavior.
         if (type.isVersionable() && !deleteAllVersions)
            throw new CmisRuntimeException("Unable delete only specified version.");
      }
      ((ObjectDataImpl)object).delete();
   }

   public Collection<String> deleteTree(ObjectData folder, boolean deleteAllVersions, EnumUnfileObject unfileObject,
      boolean continueOnFailure) throws UpdateConflictException, CmisRuntimeException
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
               if (object.getBaseType() == EnumBaseObjectTypeIds.CMIS_FOLDER)
               {
                  for (ItemsIterator<ObjectData> children = getChildren(object, null); children.hasNext();)
                     children.next().accept(this);
               }
               failedToDelete.add(object.getObjectId());
            }
         });
      }
      return failedToDelete;
   }

   public ItemsIterator<ChangeEvent> getChangeLog(String changeLogToken) throws ConstraintException,
      CmisRuntimeException
   {
      // TODO : implement
      return CmisUtils.emptyItemsIterator();
   }

   public ItemsIterator<ObjectData> getCheckedOutDocuments(ObjectData folder, String orderBy)
      throws CmisRuntimeException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public ItemsIterator<ObjectData> getChildren(ObjectData folder, String orderBy) throws CmisRuntimeException
   {
      LOG.info(">>> Get children for folder " + folder.getObjectId() + ", name " + folder.getName());
      try
      {
         return new FolderChildrenIterator(((ObjectDataImpl)folder).getNode().getNodes(), this);
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get children for folder " + folder.getObjectId(), re);
      }
   }

   public ContentStream getContentStream(ObjectData objectData, String streamId, long offset, long length)
   {
      // TODO : content ranges
      return ((ObjectDataImpl)objectData).getContentStream(streamId);
   }

   public ObjectData getObject(String objectId) throws CmisRuntimeException
   {
      try
      {
         Node node = ((ExtendedSession)session).getNodeByIdentifier(objectId);
         CmisTypeDefinitionType type = getTypeDefinition(node.getPrimaryNodeType(), true);
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

   public ObjectData getObjectByPath(String path) throws CmisRuntimeException
   {
      try
      {
         Item item = session.getItem(path);
         if (!item.isNode())
            throw new ObjectNotFoundException("Object " + path + " does not exists.");
         Node node = (Node)item;
         CmisTypeDefinitionType type = getTypeDefinition(node.getPrimaryNodeType(), true);
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

   public ItemsIterator<CmisRenditionType> getRenditions(ObjectData object) throws CmisRuntimeException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public CmisRepositoryInfoType getRepositoryInfo() throws CmisRuntimeException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Get repository info, ID " + getId());

      if (info == null)
      {
         info = new CmisRepositoryInfoType();
         info.setRepositoryId(getId());
         info.setRepositoryName(getId());
         info.setCmisVersionSupported(CMIS.SUPPORTED_VERSION);
         info.setRepositoryDescription(""); // ?
         info.setProductName("xCMIS (eXo JCR SP)");
         info.setVendorName("eXo Platform");
         info.setProductVersion("1.0-SNAPSHOT");
         info.setRootFolderId(JcrCMIS.ROOT_FOLDER_ID);
         CmisRepositoryCapabilitiesType capabilities = new CmisRepositoryCapabilitiesType();
         capabilities.setCapabilityACL(EnumCapabilityACL.MANAGE);
         capabilities.setCapabilityAllVersionsSearchable(true);
         capabilities.setCapabilityChanges(EnumCapabilityChanges.NONE);
         capabilities.setCapabilityContentStreamUpdatability(EnumCapabilityContentStreamUpdates.ANYTIME);
         capabilities.setCapabilityGetDescendants(true);
         capabilities.setCapabilityGetFolderTree(true);
         capabilities.setCapabilityJoin(EnumCapabilityJoin.NONE);
         capabilities.setCapabilityMultifiling(false);
         capabilities.setCapabilityPWCSearchable(true);
         capabilities.setCapabilityPWCUpdatable(false);
         capabilities.setCapabilityQuery(EnumCapabilityQuery.BOTHSEPARATE);
         capabilities.setCapabilityRenditions(EnumCapabilityRendition.READ);
         capabilities.setCapabilityUnfiling(false);
         capabilities.setCapabilityVersionSpecificFiling(false);
         info.setCapabilities(capabilities);
         CmisACLCapabilityType aclCapabilities = new CmisACLCapabilityType();
         aclCapabilities.setPropagation(EnumACLPropagation.REPOSITORYDETERMINED);
         aclCapabilities.setSupportedPermissions(EnumSupportedPermissions.BASIC);
         for (EnumBasicPermissions perm : EnumBasicPermissions.values())
         {
            CmisPermissionDefinition permDef = new CmisPermissionDefinition();
            permDef.setPermission(perm.value());
            aclCapabilities.getPermissions().add(permDef);
         }
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
         // Specification says should be 'read' permission but because to
         // implementation policy feature we need write access.
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_ADD_POLICY_OBJECT, // 
            EnumBasicPermissions.CMIS_WRITE.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_ADD_POLICY_POLICY, //
            EnumBasicPermissions.CMIS_READ.value()));
         // Specification says should be 'read' permission but because to
         // implementation policy feature we need write access.
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
         info.setPrincipalAnonymous(SystemIdentity.ANONIM);
         info.setPrincipalAnyone(SystemIdentity.ANY);
      }
      return info;
   }

   public String getId()
   {
      return storageID;
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

   public Collection<ObjectData> getVersions(String versionSeriesId) throws ObjectNotFoundException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public boolean hasContent(ObjectData documentData)
   {
      return ((ObjectDataImpl)documentData).getContentStream(null) != null;
   }

   public void moveObject(ObjectData object, ObjectData target, ObjectData source) throws UpdateConflictException,
      StorageException, CmisRuntimeException
   {
      try
      {
         LOG.info(">>> Move object " + object + " to " + target + " from " + source);
         String objectPath = ((ObjectDataImpl)object).getPath();
         String destinationPath = ((ObjectDataImpl)target).getPath();
         destinationPath += destinationPath.equals("/") ? object.getName() : ("/" + object.getName());
         session.getWorkspace().move(objectPath, destinationPath);
         LOG.info("<<< Object moved in " + destinationPath);
      }
      catch (javax.jcr.RepositoryException re)
      {
         throw new StorageException("Unable to move object. " + re.getMessage(), re);
      }
   }

   public ItemsIterator<Result> query(Query query) throws InvalidArgumentException, CmisRuntimeException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public void removeObjectFromFolder(ObjectData object, ObjectData folder) throws CmisRuntimeException
   {
      throw new NotSupportedException("Unfiling is not supported.");
   }

   public void saveObject(ObjectData object) throws StorageException, NameConstraintViolationException,
      UpdateConflictException
   {
      LOG.info(">>> Save object " + object.getObjectId() + ", name " + object.getName());
      ((ObjectDataImpl)object).save();
   }

   public void setContentStream(ObjectData document, ContentStream content) throws IOException, StorageException,
      CmisRuntimeException
   {
      ((ObjectDataImpl)document).setContentStream(content);
   }

   public void addType(CmisTypeDefinitionType type) throws StorageException, CmisRuntimeException
   {
      try
      {
         ExtendedNodeTypeManager nodeTypeManager = (ExtendedNodeTypeManager)session.getWorkspace().getNodeTypeManager();
         NodeTypeValue nodeTypeValue = new NodeTypeValue();
         String parentId = type.getParentId();
         if (parentId == null)
         {
            String msg = "Unable add root type. Parent Type Id must be specified.";
            throw new InvalidArgumentException(msg);
         }
         // May throw exception if parent type is unknown or unsupported.
         CmisTypeDefinitionType parentType = getTypeDefinition(parentId, false);
         List<String> declaredSupertypeNames = new ArrayList<String>();
         declaredSupertypeNames.add(getNodeTypeName(parentId));
         if (parentType.getBaseId() == EnumBaseObjectTypeIds.CMIS_DOCUMENT)
            declaredSupertypeNames.add(JcrCMIS.CMIS_MIX_DOCUMENT);
         else if (parentType.getBaseId() == EnumBaseObjectTypeIds.CMIS_FOLDER)
            declaredSupertypeNames.add(JcrCMIS.CMIS_MIX_FOLDER);
         nodeTypeValue.setDeclaredSupertypeNames(declaredSupertypeNames);
         nodeTypeValue.setMixin(false);
         nodeTypeValue.setName(type.getId());
         nodeTypeValue.setOrderableChild(false);
         nodeTypeValue.setPrimaryItemName("");

         List<PropertyDefinitionValue> jcrPropDefintions = null;
         if (type.getPropertyDefinition().size() > 0)
         {
            jcrPropDefintions = new ArrayList<PropertyDefinitionValue>();
            for (CmisPropertyDefinitionType propDef : type.getPropertyDefinition())
            {
               PropertyDefinitionValue jcrPropDef = new PropertyDefinitionValue();
               jcrPropDef.setMandatory(propDef.isRequired());
               jcrPropDef.setMultiple(propDef.getCardinality() != null
                  && propDef.getCardinality() == EnumCardinality.MULTI);
               jcrPropDef.setName(propDef.getId());
               jcrPropDef.setOnVersion(OnParentVersionAction.COPY);
               jcrPropDef.setReadOnly(propDef.getUpdatability() != null
                  && propDef.getUpdatability() == EnumUpdatability.READONLY);
               if (propDef.getPropertyType() == null)
               {
                  String msg = "Property Type required.";
                  throw new InvalidArgumentException(msg);
               }
               switch (propDef.getPropertyType())
               {
                  case BOOLEAN :
                     jcrPropDef.setRequiredType(javax.jcr.PropertyType.BOOLEAN);
                     break;
                  case DATETIME :
                     jcrPropDef.setRequiredType(javax.jcr.PropertyType.DATE);
                     break;
                  case DECIMAL :
                     jcrPropDef.setRequiredType(javax.jcr.PropertyType.DOUBLE);
                     break;
                  case INTEGER :
                     jcrPropDef.setRequiredType(javax.jcr.PropertyType.LONG);
                     break;
                  case ID : // TODO
                     //                     jcrPropDef.setRequiredType(javax.jcr.PropertyType.NAME);
                     //                     break;
                  case HTML :
                  case URI :
                  case STRING :
                     jcrPropDef.setRequiredType(javax.jcr.PropertyType.STRING);
                     break;
               }
               List<String> defaultValues = null;
               if (propDef instanceof CmisPropertyBooleanDefinitionType)
               {
                  CmisPropertyBoolean defaultBool = ((CmisPropertyBooleanDefinitionType)propDef).getDefaultValue();
                  if (defaultBool != null && defaultBool.getValue().size() > 0)
                  {
                     defaultValues = new ArrayList<String>();
                     for (Boolean v : defaultBool.getValue())
                        defaultValues.add(v.toString());
                  }
               }
               else if (propDef instanceof CmisPropertyDateTimeDefinitionType)
               {
                  CmisPropertyDateTime defaultDate = ((CmisPropertyDateTimeDefinitionType)propDef).getDefaultValue();
                  if (defaultDate != null && defaultDate.getValue().size() > 0)
                  {
                     defaultValues = new ArrayList<String>();
                     for (XMLGregorianCalendar v : defaultDate.getValue())
                        defaultValues.add(v.toXMLFormat());
                  }
               }
               else if (propDef instanceof CmisPropertyDecimalDefinitionType)
               {
                  CmisPropertyDecimal defaultDecimal = ((CmisPropertyDecimalDefinitionType)propDef).getDefaultValue();
                  if (defaultDecimal != null && defaultDecimal.getValue().size() > 0)
                  {
                     defaultValues = new ArrayList<String>();
                     for (BigDecimal v : defaultDecimal.getValue())
                        defaultValues.add(v.toString());
                  }
               }
               else if (propDef instanceof CmisPropertyHtmlDefinitionType)
               {
                  CmisPropertyHtml defaultHtml = ((CmisPropertyHtmlDefinitionType)propDef).getDefaultValue();
                  if (defaultHtml != null && defaultHtml.getValue().size() > 0)
                  {
                     defaultValues = new ArrayList<String>();
                     defaultValues.addAll(defaultHtml.getValue());
                  }
               }
               else if (propDef instanceof CmisPropertyIdDefinitionType)
               {
                  CmisPropertyId defaultId = ((CmisPropertyIdDefinitionType)propDef).getDefaultValue();
                  if (defaultId != null && defaultId.getValue().size() > 0)
                  {
                     defaultValues = new ArrayList<String>();
                     defaultValues.addAll(defaultId.getValue());
                  }
               }
               else if (propDef instanceof CmisPropertyIntegerDefinitionType)
               {
                  CmisPropertyInteger defaultInteger = ((CmisPropertyIntegerDefinitionType)propDef).getDefaultValue();
                  if (defaultInteger != null && defaultInteger.getValue().size() > 0)
                  {
                     defaultValues = new ArrayList<String>();
                     for (BigInteger v : defaultInteger.getValue())
                        defaultValues.add(v.toString());
                  }
               }
               else if (propDef instanceof CmisPropertyStringDefinitionType)
               {
                  CmisPropertyString defaultString = ((CmisPropertyStringDefinitionType)propDef).getDefaultValue();
                  if (defaultString != null && defaultString.getValue().size() > 0)
                  {
                     defaultValues = new ArrayList<String>();
                     defaultValues.addAll(defaultString.getValue());
                  }
               }
               else if (propDef instanceof CmisPropertyUriDefinitionType)
               {
                  CmisPropertyUri defaultUri = ((CmisPropertyUriDefinitionType)propDef).getDefaultValue();
                  if (defaultUri != null && defaultUri.getValue().size() > 0)
                  {
                     defaultValues = new ArrayList<String>();
                     defaultValues.addAll(defaultUri.getValue());
                  }
               }

               if (defaultValues != null)
               {
                  jcrPropDef.setDefaultValueStrings(defaultValues);
                  jcrPropDef.setAutoCreate(true);
               }
               else
               {
                  jcrPropDef.setAutoCreate(false);
               }
               jcrPropDefintions.add(jcrPropDef);
               // TODO
               //               jcrPropDef.setValueConstraints();
            }
            nodeTypeValue.setDeclaredPropertyDefinitionValues(jcrPropDefintions);
         }

         nodeTypeManager.registerNodeType(nodeTypeValue, ExtendedNodeTypeManager.FAIL_IF_EXISTS);
      }
      catch (javax.jcr.RepositoryException re)
      {
         throw new StorageException("Unable add new CMIS type. " + re.getMessage(), re);
      }
   }

   public ItemsIterator<CmisTypeDefinitionType> getTypeChildren(String typeId, boolean includePropertyDefinitions)
      throws TypeNotFoundException, CmisRuntimeException
   {
      try
      {
         List<CmisTypeDefinitionType> types = new ArrayList<CmisTypeDefinitionType>();
         if (typeId == null)
         {
            for (String t : new String[]{"cmis:document", "cmis:folder", "cmis:policy", "cmis:relationship"})
               types.add(getTypeDefinition(t, includePropertyDefinitions));
         }
         else
         {
            String nodeTypeName = getNodeTypeName(typeId);
            for (NodeTypeIterator iter = session.getWorkspace().getNodeTypeManager().getPrimaryNodeTypes(); iter
               .hasNext();)
            {
               NodeType nt = iter.nextNodeType();
               // Get only direct children of specified type.
               if (nt.isNodeType(nodeTypeName) && getTypeLevelHierarchy(nt, nodeTypeName) == 1)
                  types.add(getTypeDefinition(nt, includePropertyDefinitions));
            }
         }
         return new BaseItemsIterator<CmisTypeDefinitionType>(types);
      }
      catch (javax.jcr.RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get type children. " + re.getMessage(), re);
      }
   }

   /**
    * Get the level of hierarchy.
    * 
    * @param discovered the node type
    * @param match the name of the node type
    * @return hierarchical level for node type
    */
   private int getTypeLevelHierarchy(NodeType discovered, String match)
   {
      // determine level of hierarchy
      int level = 0;
      for (NodeType sup : discovered.getSupertypes())
      {
         if (sup.isNodeType(match))
            level++;
      }
      return level;
   }

   public CmisTypeDefinitionType getTypeDefinition(String typeId, boolean includePropertyDefinition)
      throws TypeNotFoundException, CmisRuntimeException
   {
      try
      {
         return getTypeDefinition(getNodeType(getNodeTypeName(typeId)), includePropertyDefinition);
      }
      catch (NoSuchNodeTypeException e)
      {
         throw new TypeNotFoundException("Type with id " + typeId + " not found in repository.");
      }
      catch (javax.jcr.RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get object type " + typeId, re);
      }
   }

   public void removeType(String typeId) throws TypeNotFoundException, StorageException, CmisRuntimeException
   {
      // Throws exceptions if type with specified 'typeId' does not exists or is unsupported by CMIS.
      getTypeDefinition(typeId, false);
      try
      {
         ExtendedNodeTypeManager nodeTypeManager = (ExtendedNodeTypeManager)session.getWorkspace().getNodeTypeManager();
         nodeTypeManager.unregisterNodeType(typeId);
      }
      catch (RepositoryException re)
      {
         throw new StorageException("Unable remove CMIS type " + typeId + ". " + re.getMessage(), re);
      }
   }

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
   public CmisTypeDefinitionType getTypeDefinition(NodeType nt, boolean includePropertyDefinition)
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
               cmisPropDef.setCardinality(pd.isMultiple() ? EnumCardinality.MULTI : EnumCardinality.SINGLE);
               cmisPropDef.setDescription("");
               cmisPropDef.setDisplayName(pdName);
               cmisPropDef.setId(pdName);
               cmisPropDef.setInherited(false);
               cmisPropDef.setLocalName(pdName);
               cmisPropDef.setOrderable(true);
               cmisPropDef.setQueryable(true);
               cmisPropDef.setQueryName(pdName);
               cmisPropDef.setRequired(pd.isMandatory());
               cmisPropDef.setUpdatability(pd.isProtected() ? EnumUpdatability.READONLY : EnumUpdatability.READWRITE);
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
