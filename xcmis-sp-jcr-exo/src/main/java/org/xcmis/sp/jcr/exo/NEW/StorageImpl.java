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
import org.exoplatform.services.jcr.core.nodetype.ExtendedNodeTypeManager;
import org.exoplatform.services.jcr.core.nodetype.NodeTypeValue;
import org.exoplatform.services.jcr.core.nodetype.PropertyDefinitionValue;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.sp.jcr.exo.JcrCMIS;
import org.xcmis.spi.AllowableActions;
import org.xcmis.spi.BaseType;
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
import org.xcmis.spi.PropertyDefinition;
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
import org.xcmis.spi.impl.BaseItemsIterator;
import org.xcmis.spi.impl.CmisVisitor;
import org.xcmis.spi.query.Query;
import org.xcmis.spi.query.Result;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.version.OnParentVersionAction;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class StorageImpl implements Storage
{

   private static final Log LOG = ExoLogger.getLogger(StorageImpl.class);

   static String latestLabel = "latest";

   static String pwcLabel = "pwc";

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

      ((AbstractObjectData)object).delete();
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

   /**
    * {@inheritDoc}
    */
   public ObjectData getObject(String objectId) throws ObjectNotFoundException
   {
      try
      {
         Node node = ((ExtendedSession)session).getNodeByIdentifier(objectId);
         TypeDefinition type = JcrTypeHelper.getTypeDefinition(node.getPrimaryNodeType(), true);
         
         if (type.getBaseId() == BaseType.DOCUMENT)
            return new DocumentDataImpl(node, type);
         else if (type.getBaseId() == BaseType.FOLDER)
            return new FolderDataImpl(node, type);
         else if (type.getBaseId() == BaseType.POLICY)
            return new PolicyDataImpl(node, type);
         else if (type.getBaseId() == BaseType.RELATIONSHIP)
            return new RelationshipDataImpl(node, type);
         
         // Must never happen.
         throw new CmisRuntimeException("Unknown base type. ");
      }
      catch (ItemNotFoundException nfe)
      {
         throw new ObjectNotFoundException("Object " + objectId + " does not exists.");
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException(re.getMessage(), re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public ObjectData getObjectByPath(String path) throws ObjectNotFoundException
   {
      try
      {
         Item item = session.getItem(path);
         if (!item.isNode())
            throw new ObjectNotFoundException("Object " + path + " does not exists.");
         
         Node node = (Node)item;
         
         TypeDefinition type = JcrTypeHelper.getTypeDefinition(node.getPrimaryNodeType(), true);

         if (type.getBaseId() == BaseType.DOCUMENT)
            return new DocumentDataImpl(node, type);
         else if (type.getBaseId() == BaseType.FOLDER)
            return new FolderDataImpl(node, type);
         else if (type.getBaseId() == BaseType.POLICY)
            return new PolicyDataImpl(node, type);
         else if (type.getBaseId() == BaseType.RELATIONSHIP)
            return new RelationshipDataImpl(node, type);
         
         // Must never happen.
         throw new CmisRuntimeException("Unknown base type. ");
      }
      catch (ItemNotFoundException nfe)
      {
         throw new ObjectNotFoundException("Object  " + path + " does not exists.");
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException(re.getMessage(), re);
      }
   }

   public ItemsIterator<Rendition> getRenditions(ObjectData object)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public RepositoryInfo getRepositoryInfo()
   {
      return new RepositoryInfoImpl(storageID);
   }

   /**
    * {@inheritDoc}
    */
   public ObjectData moveObject(ObjectData object, FolderData target, FolderData source) throws ConstraintException,
      InvalidArgumentException, UpdateConflictException, VersioningException, NameConstraintViolationException,
      StorageException
   {
      try
      {
         LOG.info(">>> Move object " + object + " to " + target + " from " + source);
         String objectPath = ((ObjectDataImpl)object).getPath();
         String destinationPath = ((ObjectDataImpl)target).getPath();
         destinationPath += destinationPath.equals("/") ? object.getName() : ("/" + object.getName());
         session.getWorkspace().move(objectPath, destinationPath);
         LOG.info("<<< Object moved in " + destinationPath);
         return getObjectByPath(destinationPath);
      }
      catch (ItemExistsException ie)
      {
         throw new NameConstraintViolationException("Object with the same name already exists in target folder.");
      }
      catch (javax.jcr.RepositoryException re)
      {
         throw new StorageException("Unable to move object. " + re.getMessage(), re);
      }
   }

   public ItemsIterator<Result> query(Query query) throws InvalidArgumentException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public void saveObject(ObjectData object) throws StorageException, NameConstraintViolationException,
      UpdateConflictException
   {
      LOG.info(">>> Save object " + object.getObjectId() + ", name " + object.getName());
      ((AbstractObjectData)object).save();
   }

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   public String addType(TypeDefinition type) throws StorageException, CmisRuntimeException
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
         TypeDefinition parentType = getTypeDefinition(parentId, false);

         List<String> declaredSupertypeNames = new ArrayList<String>();
         declaredSupertypeNames.add(JcrTypeHelper.getNodeTypeName(parentId));
         if (parentType.getBaseId() == BaseType.DOCUMENT)
            declaredSupertypeNames.add(JcrCMIS.CMIS_MIX_DOCUMENT);
         else if (parentType.getBaseId() == BaseType.FOLDER)
            declaredSupertypeNames.add(JcrCMIS.CMIS_MIX_FOLDER);

         nodeTypeValue.setDeclaredSupertypeNames(declaredSupertypeNames);
         nodeTypeValue.setMixin(false);
         nodeTypeValue.setName(type.getId());
         nodeTypeValue.setOrderableChild(false);
         nodeTypeValue.setPrimaryItemName("");

         List<PropertyDefinitionValue> jcrPropDefintions = null;
         if (type.getPropertyDefinitions().size() > 0)
         {
            jcrPropDefintions = new ArrayList<PropertyDefinitionValue>();

            for (PropertyDefinition<?> propDef : type.getPropertyDefinitions())
            {
               PropertyDefinitionValue jcrPropDef = new PropertyDefinitionValue();
               jcrPropDef.setMandatory(propDef.isRequired());
               jcrPropDef.setMultiple(propDef.isMultivalued());
               jcrPropDef.setName(propDef.getId());
               jcrPropDef.setOnVersion(OnParentVersionAction.COPY);
               jcrPropDef.setReadOnly(propDef.getUpdatability() != null
                  && propDef.getUpdatability() == Updatability.READONLY);

               if (propDef.getPropertyType() == null)
               {
                  String msg = "Property Type required.";
                  throw new InvalidArgumentException(msg);
               }

               List<String> defaultValues = null;

               switch (propDef.getPropertyType())
               {
                  case BOOLEAN :
                     jcrPropDef.setRequiredType(javax.jcr.PropertyType.BOOLEAN);
                     Boolean[] booleans = ((PropertyDefinition<Boolean>)propDef).getDefaultValue();
                     if (booleans != null && booleans.length > 0)
                     {
                        defaultValues = new ArrayList<String>(booleans.length);
                        for (Boolean v : booleans)
                           defaultValues.add(v.toString());
                     }
                     break;

                  case DATETIME :
                     jcrPropDef.setRequiredType(javax.jcr.PropertyType.DATE);
                     Calendar[] dates = ((PropertyDefinition<Calendar>)propDef).getDefaultValue();
                     if (dates != null && dates.length > 0)
                     {
                        defaultValues = new ArrayList<String>(dates.length);
                        for (Calendar v : dates)
                           defaultValues.add(createJcrDate(v));
                     }
                     break;

                  case DECIMAL :
                     jcrPropDef.setRequiredType(javax.jcr.PropertyType.DOUBLE);
                     BigDecimal[] decimals = ((PropertyDefinition<BigDecimal>)propDef).getDefaultValue();
                     if (decimals != null && decimals.length > 0)
                     {
                        defaultValues = new ArrayList<String>(decimals.length);
                        for (BigDecimal v : decimals)
                           defaultValues.add(Double.toString(v.doubleValue()));
                     }
                     break;

                  case INTEGER :
                     jcrPropDef.setRequiredType(javax.jcr.PropertyType.LONG);
                     BigInteger[] ints = ((PropertyDefinition<BigInteger>)propDef).getDefaultValue();
                     if (ints != null && ints.length > 0)
                     {
                        defaultValues = new ArrayList<String>(ints.length);
                        for (BigInteger v : ints)
                           defaultValues.add(Long.toString(v.longValue()));
                     }
                     break;

                  case ID : // TODO : need to separate ID type at least !!!
                     //                     jcrPropDef.setRequiredType(javax.jcr.PropertyType.NAME);
                     //                     break;
                  case HTML :
                  case URI :
                  case STRING :
                     jcrPropDef.setRequiredType(javax.jcr.PropertyType.STRING);
                     String[] str = ((PropertyDefinition<String>)propDef).getDefaultValue();
                     if (str != null && str.length > 0)
                     {
                        defaultValues = new ArrayList<String>(str.length);
                        for (String v : str)
                           defaultValues.add(v);
                     }
                     break;
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

            }
            
            nodeTypeValue.setDeclaredPropertyDefinitionValues(jcrPropDefintions);
         }

         NodeType nodeType = nodeTypeManager.registerNodeType(nodeTypeValue, ExtendedNodeTypeManager.FAIL_IF_EXISTS);

         return nodeType.getName();

      }
      catch (javax.jcr.RepositoryException re)
      {
         throw new StorageException("Unable add new CMIS type. " + re.getMessage(), re);
      }
   }

   /**
    * Create String representation of date in format required by JCR.
    * 
    * @param c Calendar
    * @return formated string date
    */
   // TODO : Add in common utils ?? 
   protected String createJcrDate(Calendar c)
   {
      return String.format("%04d-%02d-%02dT%02d:%02d:%02d.%03dZ", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c
         .get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND), c
         .get(Calendar.MILLISECOND));
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<TypeDefinition> getTypeChildren(String typeId, boolean includePropertyDefinitions)
      throws TypeNotFoundException, CmisRuntimeException
   {
      try
      {
         List<TypeDefinition> types = new ArrayList<TypeDefinition>();
         if (typeId == null)
         {
            for (String t : new String[]{"cmis:document", "cmis:folder", "cmis:policy", "cmis:relationship"})
               types.add(getTypeDefinition(t, includePropertyDefinitions));
         }
         else
         {
            String nodeTypeName = JcrTypeHelper.getNodeTypeName(typeId);
            for (NodeTypeIterator iter = session.getWorkspace().getNodeTypeManager().getPrimaryNodeTypes(); iter
               .hasNext();)
            {
               NodeType nt = iter.nextNodeType();
               // Get only direct children of specified type.
               if (nt.isNodeType(nodeTypeName) && getTypeLevelHierarchy(nt, nodeTypeName) == 1)
                  types.add(JcrTypeHelper.getTypeDefinition(nt, includePropertyDefinitions));
            }
         }
         return new BaseItemsIterator<TypeDefinition>(types);
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

   /**
    * {@inheritDoc}
    */
   public TypeDefinition getTypeDefinition(String typeId, boolean includePropertyDefinition)
      throws TypeNotFoundException, CmisRuntimeException
   {
      try
      {
         return JcrTypeHelper.getTypeDefinition(getNodeType(JcrTypeHelper.getNodeTypeName(typeId)), includePropertyDefinition);
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

   /**
    * {@inheritDoc}
    */
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

   protected NodeType getNodeType(String name) throws NoSuchNodeTypeException, javax.jcr.RepositoryException
   {
      NodeType nt = session.getWorkspace().getNodeTypeManager().getNodeType(name);
      return nt;
   }

}
