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

package org.xcmis.sp.jcr.exo;

import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.core.ExtendedSession;
import org.exoplatform.services.jcr.core.nodetype.ExtendedNodeTypeManager;
import org.exoplatform.services.jcr.core.nodetype.NodeTypeValue;
import org.exoplatform.services.jcr.core.nodetype.PropertyDefinitionValue;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.sp.jcr.exo.index.IndexListener;
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
import org.xcmis.spi.data.Document;
import org.xcmis.spi.data.Folder;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.data.Policy;
import org.xcmis.spi.data.Relationship;
import org.xcmis.spi.impl.AllowableActionsImpl;
import org.xcmis.spi.impl.BaseItemsIterator;
import org.xcmis.spi.object.RenditionManager;
import org.xcmis.spi.query.Query;
import org.xcmis.spi.query.Result;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.Item;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.ItemVisitor;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PropertyIterator;
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

   public static final String XCMIS_SYSTEM_PATH = "/xcmis:system";

   public static final String XCMIS_UNFILED = "xcmis:unfileStore";

   public static final String XCMIS_WORKING_COPIES = "xcmis:workingCopyStore";

   public static final String XCMIS_RELATIONSHIPS = "xcmis:relationshipStore";

   public static final String XCMIS_POLICIES = "xcmis:policiesStore";

   static String latestLabel = "latest";

   static String pwcLabel = "pwc";

   protected final Session session;

   private final StorageConfiguration configuration;

   /** The rendition manager. */
   private RenditionManager renditionManager;

   private IndexListener indexListener;

   public StorageImpl(Session session, StorageConfiguration configuration)
   {
      this.session = session;
      this.configuration = configuration;
   }

   public StorageImpl(Session session, StorageConfiguration configuration, RenditionManagerImpl renditionManager)
   {
      this.session = session;
      this.configuration = configuration;
      this.renditionManager = renditionManager;
   }

   /**
    * @return the indexListener
    */
   public IndexListener getIndexListener()
   {
      return indexListener;
   }

   /**
    * @param indexListener the indexListener to set
    */
   public void setIndexListener(IndexListener indexListener)
   {
      this.indexListener = indexListener;
   }

   public StorageImpl(Session session, IndexListener indexListener, StorageConfiguration configuration,
      RenditionManagerImpl renditionManager)
   {
      this.session = session;
      this.indexListener = indexListener;
      this.configuration = configuration;
      this.renditionManager = renditionManager;
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
         {
            declaredSupertypeNames.add(JcrCMIS.CMIS_MIX_DOCUMENT);
         }
         else if (parentType.getBaseId() == BaseType.FOLDER)
         {
            declaredSupertypeNames.add(JcrCMIS.CMIS_MIX_FOLDER);
         }

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
                        {
                           defaultValues.add(v.toString());
                        }
                     }
                     break;

                  case DATETIME :
                     jcrPropDef.setRequiredType(javax.jcr.PropertyType.DATE);
                     Calendar[] dates = ((PropertyDefinition<Calendar>)propDef).getDefaultValue();
                     if (dates != null && dates.length > 0)
                     {
                        defaultValues = new ArrayList<String>(dates.length);
                        for (Calendar v : dates)
                        {
                           defaultValues.add(createJcrDate(v));
                        }
                     }
                     break;

                  case DECIMAL :
                     jcrPropDef.setRequiredType(javax.jcr.PropertyType.DOUBLE);
                     BigDecimal[] decimals = ((PropertyDefinition<BigDecimal>)propDef).getDefaultValue();
                     if (decimals != null && decimals.length > 0)
                     {
                        defaultValues = new ArrayList<String>(decimals.length);
                        for (BigDecimal v : decimals)
                        {
                           defaultValues.add(Double.toString(v.doubleValue()));
                        }
                     }
                     break;

                  case INTEGER :
                     jcrPropDef.setRequiredType(javax.jcr.PropertyType.LONG);
                     BigInteger[] ints = ((PropertyDefinition<BigInteger>)propDef).getDefaultValue();
                     if (ints != null && ints.length > 0)
                     {
                        defaultValues = new ArrayList<String>(ints.length);
                        for (BigInteger v : ints)
                        {
                           defaultValues.add(Long.toString(v.longValue()));
                        }
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
                        {
                           defaultValues.add(v);
                        }
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
    * {@inheritDoc}
    */
   public AllowableActions calculateAllowableActions(ObjectData object)
   {
      AllowableActionsImpl actions = new AllowableActionsImpl();
      TypeDefinition type = object.getTypeDefinition();

      RepositoryCapabilities capabilities = getRepositoryInfo().getCapabilities();

      boolean isCheckedout = type.getBaseId() == BaseType.DOCUMENT //
         && type.isVersionable() //
         && ((Document)object).isVersionSeriesCheckedOut();

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
         && ((Document)object).hasContent());

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

   /**
    * {@inheritDoc}
    */
   public Document createCopyOfDocument(Document source, Folder folder, VersioningState versioningState)
      throws ConstraintException, StorageException
   {
      // TODO : remove when implement unfiling feature.
      if (folder == null)
      {
         throw new NotSupportedException("Unfiling capability is not supported.");
      }

      if (folder.isNew())
      {
         throw new CmisRuntimeException("Unable create document in newly created folder.");
      }

      if (source.isNew())
      {
         throw new CmisRuntimeException("Unable use newly created document as source.");
      }

      if (source.getBaseType() != BaseType.DOCUMENT)
      {
         throw new ConstraintException("Source object has type whose base type is not Document.");
      }

      if (!folder.isAllowedChildType(source.getTypeId()))
      {
         throw new ConstraintException("Type " + source.getTypeId()
            + " is not in list of allowed child type for folder " + folder.getObjectId());
      }

      DocumentCopy copy = new DocumentCopy(source, folder, session, versioningState);

      return copy;
   }

   /**
    * {@inheritDoc}
    */
   public Document createDocument(Folder folder, String typeId, VersioningState versioningState)
      throws ConstraintException
   {
      if (folder != null)
      {
         if (folder.isNew())
         {
            throw new CmisRuntimeException("Unable create document in newly created folder.");
         }
         if (!folder.isAllowedChildType(typeId))
         {
            throw new ConstraintException("Type " + typeId + " is not in list of allowed child type for folder "
               + folder.getObjectId());
         }
      }

      TypeDefinition typeDefinition = getTypeDefinition(typeId, true);

      if (typeDefinition.getBaseId() != BaseType.DOCUMENT)
      {
         throw new ConstraintException("Type " + typeId + " is ID of type whose base type is not Document.");
      }

      Document document = new DocumentImpl(typeDefinition, folder, session, versioningState);

      return document;
   }

   /**
    * {@inheritDoc}
    */
   public Folder createFolder(Folder folder, String typeId) throws ConstraintException
   {
      if (folder == null)
      {
         throw new ConstraintException("Parent folder must be provided.");
      }

      if (folder.isNew())
      {
         throw new CmisRuntimeException("Unable create child folder in newly created folder.");
      }

      if (!folder.isAllowedChildType(typeId))
      {
         throw new ConstraintException("Type " + typeId + " is not in list of allowed child type for folder "
            + folder.getObjectId());
      }

      TypeDefinition typeDefinition = getTypeDefinition(typeId, true);

      if (typeDefinition.getBaseId() != BaseType.FOLDER)
      {
         throw new ConstraintException("Type " + typeId + " is ID of type whose base type is not Folder.");
      }

      Folder newFolder = new FolderImpl(typeDefinition, folder, session);

      return newFolder;
   }

   /**
    * {@inheritDoc}
    */
   public Policy createPolicy(Folder folder, String typeId) throws ConstraintException
   {
      TypeDefinition typeDefinition = getTypeDefinition(typeId, true);

      if (typeDefinition.getBaseId() != BaseType.POLICY)
      {
         throw new ConstraintException("Type " + typeId + " is ID of type whose base type is not Policy.");
      }

      // TODO : need raise exception if parent folder is provided ??
      // Do not use parent folder, policy is not fileable.
      Policy policy = new PolicyImpl(typeDefinition, null, session);

      return policy;
   }

   /**
    * {@inheritDoc}
    */
   public Relationship createRelationship(ObjectData source, ObjectData target, String typeId)
      throws ConstraintException
   {
      if (source.isNew())
      {
         throw new CmisRuntimeException("Unable use newly created object as relationship source.");
      }

      if (target.isNew())
      {
         throw new CmisRuntimeException("Unable use newly created object as relationship target.");
      }

      TypeDefinition typeDefinition = getTypeDefinition(typeId, true);

      if (typeDefinition.getBaseId() != BaseType.RELATIONSHIP)
      {
         throw new ConstraintException("Type " + typeId + " is ID of type whose base type is not Relationship.");
      }

      Relationship relationship = new RelationshipImpl(typeDefinition, source, target, session);

      return relationship;
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
         {
            throw new CmisRuntimeException("Unable delete only specified version.");
         }
      }

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
   public Collection<String> deleteTree(Folder folder, boolean deleteAllVersions, UnfileObject unfileObject,
      boolean continueOnFailure) throws UpdateConflictException
   {
      if (!deleteAllVersions)
      {
         // Throw exception to avoid unexpected removing data.
         // Any way at the moment we are not able remove 'base version' of
         // versionable node, so have not common behavior.
         throw new CmisRuntimeException("Unable delete only specified version.");
      }

      final List<String> failedToDelete = new ArrayList<String>();
      DeleteTreeVisitor v = new DeleteTreeVisitor(folder.getPath(), unfileObject);

      try
      {
         v.visit(((FolderImpl)folder).getNode());

         for (String id : v.getDeleteLinks())
         {
            if (LOG.isDebugEnabled())
            {
               LOG.debug("Delete link " + id);
            }
            ((ExtendedSession)session).getNodeByIdentifier(id).remove();
         }

         for (Map.Entry<String, String> e : v.getMoveMapping().entrySet())
         {
            String scrPath = e.getKey();
            String destPath = e.getValue();

            if (destPath == null)
            {
               // No found links outside of current tree, then will move node in
               // special store for unfiled objects.
               ExtendedNode unfiledStore = (ExtendedNode)session.getItem(XCMIS_SYSTEM_PATH + "/" + XCMIS_UNFILED);
               ExtendedNode src = (ExtendedNode)session.getItem(scrPath);
               Node unfiled = unfiledStore.addNode(src.getIdentifier(), "xcmis:unfiledObject");
               destPath = unfiled.getPath() + "/" + src.getName();
            }
            else
            {
               // Remove link, it will be replaced by real node.
               session.getItem(destPath).remove();
            }

            if (LOG.isDebugEnabled())
            {
               LOG.debug("Move " + scrPath + " to " + destPath);
            }
            session.move(scrPath, destPath);
         }

         for (String e : v.getDeleteObjects())
         {
            if (LOG.isDebugEnabled())
            {
               LOG.debug("Delete: " + e);
            }
            ((ExtendedSession)session).getNodeByIdentifier(e).remove();
         }

         session.save();
         if (indexListener != null)
         {
            indexListener.removed(new HashSet<String>(v.getDeleteObjects()));
         }
      }
      catch (RepositoryException re)
      {
         // TODO : provide list of not deleted objects.
         // If fact plain list of all items in current tree.
         throw new CmisRuntimeException(re.getMessage(), re);
      }
      return failedToDelete;
   }

   public Collection<Document> getAllVersions(String versionSeriesId) throws ObjectNotFoundException
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
      try
      {
         Node workingCopies =
            (Node)session.getItem(StorageImpl.XCMIS_SYSTEM_PATH + "/" + StorageImpl.XCMIS_WORKING_COPIES);

         List<ObjectData> checkedOut = new ArrayList<ObjectData>();

         for (NodeIterator iterator = workingCopies.getNodes(); iterator.hasNext();)
         {
            Node wc = iterator.nextNode();
            Node node = wc.getNodes().nextNode();
            TypeDefinition type = JcrTypeHelper.getTypeDefinition(node.getPrimaryNodeType(), true);
            String latestVersion = node.getProperty("xcmis:latestVersionId").getString();
            PWC pwc = new PWC(type, node, (Document)getObject(latestVersion));
            if (folder != null)
            {
               for (Folder parent : pwc.getParents())
               {
                  // TODO equals and hashCode for objects
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

         return new BaseItemsIterator<ObjectData>(checkedOut);
      }
      catch (RepositoryException re)
      {
         throw new CmisRuntimeException("Unable get checked-out documents. " + re.getMessage(), re);
      }
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
   public ObjectData getObject(String objectId) throws ObjectNotFoundException
   {
      try
      {
         Node node = ((ExtendedSession)session).getNodeByIdentifier(objectId);
         if (node.isNodeType(JcrCMIS.NT_VERSION))
         {
            Node frozen = node.getNode(JcrCMIS.JCR_FROZEN_NODE);
            JcrTypeHelper.getTypeDefinition(
               getNodeType(frozen.getProperty(JcrCMIS.JCR_FROZEN_PRIMARY_TYPE).getString()), true);
         }
         TypeDefinition type = JcrTypeHelper.getTypeDefinition(node.getPrimaryNodeType(), true);

         if (type.getBaseId() == BaseType.DOCUMENT)
         {
            if (node.getParent().isNodeType("xcmis:workingCopy"))
            {
               // TODO get smarter (simpler)
               String latestVersion = node.getProperty("xcmis:latestVersionId").getString();
               return new PWC(type, node, (Document)getObject(latestVersion));
            }
            return new DocumentImpl(type, node, renditionManager);
         }
         else if (type.getBaseId() == BaseType.FOLDER)
         {
            return new FolderImpl(type, node);
         }
         else if (type.getBaseId() == BaseType.POLICY)
         {
            return new PolicyImpl(type, node);
         }
         else if (type.getBaseId() == BaseType.RELATIONSHIP)
         {
            return new RelationshipImpl(type, node);
         }

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
         {
            throw new ObjectNotFoundException("Object " + path + " does not exists.");
         }

         Node node = (Node)item;

         TypeDefinition type = JcrTypeHelper.getTypeDefinition(node.getPrimaryNodeType(), true);

         if (type.getBaseId() == BaseType.DOCUMENT)
         {
            return new DocumentImpl(type, node, renditionManager);
         }
         else if (type.getBaseId() == BaseType.FOLDER)
         {
            return new FolderImpl(type, node);
         }
         else if (type.getBaseId() == BaseType.POLICY)
         {
            return new PolicyImpl(type, node);
         }
         else if (type.getBaseId() == BaseType.RELATIONSHIP)
         {
            return new RelationshipImpl(type, node);
         }

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
      if (renditionManager != null)
      {
         return renditionManager.getRenditions(object);
      }
      else
      {
         return null;
      }
   }

   /**
    * {@inheritDoc}
    */
   public RepositoryInfo getRepositoryInfo()
   {
      return new RepositoryInfoImpl(configuration.getId());
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
            {
               types.add(getTypeDefinition(t, includePropertyDefinitions));
            }
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
               {
                  types.add(JcrTypeHelper.getTypeDefinition(nt, includePropertyDefinitions));
               }
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
    * {@inheritDoc}
    */
   public TypeDefinition getTypeDefinition(String typeId, boolean includePropertyDefinition)
      throws TypeNotFoundException, CmisRuntimeException
   {
      try
      {
         return JcrTypeHelper.getTypeDefinition(getNodeType(JcrTypeHelper.getNodeTypeName(typeId)),
            includePropertyDefinition);
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
   public ObjectData moveObject(ObjectData object, Folder target, Folder source) throws ConstraintException,
      InvalidArgumentException, UpdateConflictException, VersioningException, NameConstraintViolationException,
      StorageException
   {
      try
      {
         if (LOG.isDebugEnabled())
         {
            LOG.debug("Move object " + object + " to " + target + " from " + source);
         }

         String objectPath = ((BaseObjectData)object).getNode().getPath();
         String destinationPath = ((BaseObjectData)target).getNode().getPath();
         destinationPath += destinationPath.equals("/") ? object.getName() : ("/" + object.getName());
         session.getWorkspace().move(objectPath, destinationPath);

         if (LOG.isDebugEnabled())
         {
            LOG.debug("Object moved in " + destinationPath);
         }

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
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */
   public String saveObject(ObjectData object) throws StorageException, NameConstraintViolationException,
      UpdateConflictException
   {
      boolean isNew = object.isNew();

      ((BaseObjectData)object).save();

      if (indexListener != null)
      {
         if (isNew)
         {
            indexListener.created(object);
         }
         else
         {
            indexListener.updated(object);
         }
      }

      return object.getObjectId();
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

   /**
    * {@inheritDoc}
    */
   public void unfileObject(ObjectData object)
   {
      ((BaseObjectData)object).unfile();
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
         {
            level++;
         }
      }
      return level;
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

   protected NodeType getNodeType(String name) throws NoSuchNodeTypeException, javax.jcr.RepositoryException
   {
      NodeType nt = session.getWorkspace().getNodeTypeManager().getNodeType(name);
      return nt;
   }

   private class DeleteTreeVisitor implements ItemVisitor
   {

      private final String treePath;

      private final UnfileObject unfileObject;

      private final List<String> deleteObjects = new ArrayList<String>();

      private final List<String> deleteLinks = new ArrayList<String>();

      private final Map<String, String> moveMapping = new HashMap<String, String>();

      public DeleteTreeVisitor(String path, UnfileObject unfileObject)
      {
         this.treePath = path;
         this.unfileObject = unfileObject;
      }

      /**
       * {@inheritDoc}
       */
      public void visit(javax.jcr.Property property) throws RepositoryException
      {
      }

      /**
       * {@inheritDoc}
       */
      public void visit(Node node) throws RepositoryException
      {
         NodeType nt = node.getPrimaryNodeType();
         String uuid = ((ExtendedNode)node).getIdentifier();
         String path = node.getPath();

         if (nt.isNodeType(JcrCMIS.NT_FOLDER) || nt.isNodeType(JcrCMIS.NT_UNSTRUCTURED))
         {
            for (NodeIterator children = node.getNodes(); children.hasNext();)
            {
               children.nextNode().accept(this);
            }
            deleteObjects.add(uuid);
         }

         if (nt.isNodeType("nt:linkedFile"))
         {
            // Met link in tree. Simply remove all links in current tree.
            if (!deleteLinks.contains(uuid))
            {
               deleteLinks.add(uuid);
            }

            // Check target of link only if need delete all fileable objects.
            if (unfileObject == UnfileObject.DELETE)
            {
               Node doc = node.getProperty("jcr:content").getNode();
               String targetPath = doc.getPath();
               String targetUuid = ((ExtendedNode)doc).getIdentifier();
               if (!targetPath.startsWith(treePath) && !deleteObjects.contains(targetUuid))
               {
                  deleteObjects.add(targetUuid);
               }
               // Otherwise will met target of link in tree.
            }
         }
         else if (nt.isNodeType(JcrCMIS.NT_FILE))
         {
            String moveTo = null;

            // Check all link to current node.
            // Need to find at least one that is not in deleted tree. It can be
            // used as destination for unfiling document which has parent-folders
            // outside of the current folder tree. If no link out of current tree then
            // document will be moved to special store for unfiled objects.
            for (PropertyIterator references = node.getReferences(); references.hasNext();)
            {
               Node link = references.nextProperty().getParent();

               String linkPath = link.getPath();
               String linkUuid = ((ExtendedNode)link).getIdentifier();

               if ((unfileObject == UnfileObject.DELETE || linkPath.startsWith(treePath))
                  && !deleteLinks.contains(linkUuid))
               {
                  deleteLinks.add(linkUuid);
               }
               else if (!linkPath.startsWith(treePath) && moveTo == null)
               {
                  moveTo = linkPath;
               }
            }

            if ((unfileObject == UnfileObject.UNFILE || (unfileObject == UnfileObject.DELETESINGLEFILED && moveTo != null)))
            {
               moveMapping.put(path, moveTo);
            }
            else if (!deleteObjects.contains(uuid))
            {
               deleteObjects.add(uuid);
            }
         }
      }

      public List<String> getDeleteObjects()
      {
         return deleteObjects;
      }

      public List<String> getDeleteLinks()
      {
         return deleteLinks;
      }

      public Map<String, String> getMoveMapping()
      {
         return moveMapping;
      }

   }

}
