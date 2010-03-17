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

import org.exoplatform.services.jcr.access.SystemIdentity;
import org.exoplatform.services.jcr.core.ExtendedSession;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.core.nodetype.ExtendedNodeTypeManager;
import org.exoplatform.services.jcr.core.nodetype.NodeTypeValue;
import org.exoplatform.services.jcr.core.nodetype.PropertyDefinitionValue;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.util.IdGenerator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.xcmis.core.CmisACLCapabilityType;
import org.xcmis.core.CmisPermissionDefinition;
import org.xcmis.core.CmisPermissionMapping;
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
import org.xcmis.core.CmisRepositoryCapabilitiesType;
import org.xcmis.core.CmisRepositoryInfoType;
import org.xcmis.core.CmisTypeDefinitionType;
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
import org.xcmis.core.EnumSupportedPermissions;
import org.xcmis.core.EnumUpdatability;
import org.xcmis.core.EnumVersioningState;
import org.xcmis.messaging.CmisTypeContainer;
import org.xcmis.search.SearchServiceException;
import org.xcmis.sp.jcr.exo.object.EntryImpl;
import org.xcmis.sp.jcr.exo.object.EntryVersion;
import org.xcmis.sp.jcr.exo.object.VersionSeriesImpl;
import org.xcmis.sp.jcr.exo.query.ContentProxy;
import org.xcmis.sp.jcr.exo.query.QueryHandlerImpl;
import org.xcmis.sp.jcr.exo.query.QueryNameResolver;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ChangeTokenMatcher;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.EntryNameProducer;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.Repository;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.object.BaseItemsIterator;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.ItemsIterator;
import org.xcmis.spi.object.RenditionManager;
import org.xcmis.spi.object.VersionSeries;
import org.xcmis.spi.query.QueryHandler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Session;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.version.OnParentVersionAction;
import javax.jcr.version.Version;
import javax.jcr.version.VersionHistory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: RepositoryImpl.java 282 2010-03-05 12:16:25Z ur3cma $
 */
public class RepositoryImpl extends TypeManagerImpl implements Repository, EntryNameProducer, QueryNameResolver,
   ChangeTokenMatcher
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(RepositoryImpl.class.getName());

   /** Back-end JCR repository. */
   protected final javax.jcr.Repository backendRepo;

   /** CMIS repository configuration. */
   protected final CMISRepositoryConfiguration config;

   /** Session provider. */
   protected final SessionProvider sesProv;

   /** TRUE if change token feature if 'on' FALSE otherwise. */
   protected final boolean changeTokenFeature;

   /** JCR session. */
   private Session session;

   /** SQL Query handler. */
   private final QueryHandler queryHandler;

   /** Repository info & capabilities. */
   private CmisRepositoryInfoType info;

   /** The rendition manager. */
   private RenditionManager renditionManager;

   //private final Map<MimeType, RenditionProvider> renditionProviders;

   /**
    * Construct instance <tt>RepositoryImpl</tt>.
    * 
    * @param backendRepo the jcr repository
    * @param sesProv the session provider
    * @param config the CMIS repository configuration
    * @param renditionProviders List of rendition providers
    * @throws javax.jcr.RepositoryException if any repository error occurs
    * @throws SearchServiceException 
    */
   public RepositoryImpl(javax.jcr.Repository backendRepo, SessionProvider sesProv, ContentProxy contenProxy,
      CMISRepositoryConfiguration config, RenditionManager renditionManager) throws javax.jcr.RepositoryException,
      SearchServiceException
   {
      this.backendRepo = backendRepo;
      this.sesProv = sesProv;
      this.config = config;
      if (contenProxy != null && config != null)
      {
         this.queryHandler =
            new QueryHandlerImpl(contenProxy, this, config.getIndexConfiguration(),
               ((ExtendedNodeTypeManager)getSession().getWorkspace().getNodeTypeManager()).getNodeTypesHolder(),
               ((ExtendedSession)getSession()).getLocationFactory());
      }
      else
      {
         this.queryHandler = null;
      }

      // this.renditionProviders = renditionProviders;
      this.renditionManager = renditionManager;

      changeTokenFeature =
         config.getProperties().get("exo.cmis.changetoken.feature") != null ? (Boolean)config.getProperties().get(
            "exo.cmis.changetoken.feature") : true;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addType(CmisTypeDefinitionType type) throws RepositoryException
   {
      try
      {
         ExtendedNodeTypeManager nodeTypeManager =
            (ExtendedNodeTypeManager)getSession().getWorkspace().getNodeTypeManager();
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
         {
            declaredSupertypeNames.add(JcrCMIS.CMIS_MIX_DOCUMENT);
         }
         else if (parentType.getBaseId() == EnumBaseObjectTypeIds.CMIS_FOLDER)
         {
            declaredSupertypeNames.add(JcrCMIS.CMIS_MIX_FOLDER);
         }
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
                  case HTML :
                     jcrPropDef.setRequiredType(javax.jcr.PropertyType.STRING);
                     break;
                  case ID :
                     jcrPropDef.setRequiredType(javax.jcr.PropertyType.STRING);
                     break;
                  case INTEGER :
                     jcrPropDef.setRequiredType(javax.jcr.PropertyType.LONG);
                     break;
                  case STRING :
                     jcrPropDef.setRequiredType(javax.jcr.PropertyType.STRING);
                     break;
                  case URI :
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
                     {
                        defaultValues.add(v.toString());
                     }
                  }
               }
               else if (propDef instanceof CmisPropertyDateTimeDefinitionType)
               {
                  CmisPropertyDateTime defaultDate = ((CmisPropertyDateTimeDefinitionType)propDef).getDefaultValue();
                  if (defaultDate != null && defaultDate.getValue().size() > 0)
                  {
                     defaultValues = new ArrayList<String>();
                     for (XMLGregorianCalendar v : defaultDate.getValue())
                     {
                        defaultValues.add(v.toXMLFormat());
                     }
                  }
               }
               else if (propDef instanceof CmisPropertyDecimalDefinitionType)
               {
                  CmisPropertyDecimal defaultDecimal = ((CmisPropertyDecimalDefinitionType)propDef).getDefaultValue();
                  if (defaultDecimal != null && defaultDecimal.getValue().size() > 0)
                  {
                     defaultValues = new ArrayList<String>();
                     for (BigDecimal v : defaultDecimal.getValue())
                     {
                        defaultValues.add(v.toString());
                     }
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
                     {
                        defaultValues.add(v.toString());
                     }
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
         String msg = "Unable add new CMIS type. " + re.getMessage();
         throw new RepositoryException(msg);
      }
   }

   /**
    * {@inheritDoc}
    */
   public Entry copyObject(String objectId, String destinationFolderId, EnumVersioningState versioningState)
      throws ObjectNotFoundException, ConstraintException, RepositoryException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Copy object " + objectId + " to  folder " + destinationFolderId);
      }

      EntryImpl object = (EntryImpl)getObjectById(objectId);
      if (object.getScope() != EnumBaseObjectTypeIds.CMIS_DOCUMENT)
      {
         String msg = "Copying is not supported for object with type other than 'cmis:document'.";
         throw new ConstraintException(msg);
      }
      EntryImpl destinationFolder = (EntryImpl)getObjectById(destinationFolderId);
      if (destinationFolder.getScope() != EnumBaseObjectTypeIds.CMIS_FOLDER)
      {
         String msg = "Target object " + destinationFolderId + " is not a folder.";
         throw new ConstraintException(msg);
      }
      try
      {
         Node srcNode = object.getNode();
         Node destNode = destinationFolder.getNode();
         String srcPath = srcNode.getPath();
         String destPath = destNode.getPath();
         if (destPath.equals("/"))
         {
            destPath += srcNode.getName();
         }
         else
         {
            destPath += "/" + srcNode.getName();
         }

         getSession().getWorkspace().copy(srcPath, destPath);

         if (LOG.isDebugEnabled())
         {
            LOG.debug("Object copied in " + destPath);
         }

         Entry copy = new EntryImpl((Node)getSession().getItem(destPath));
         copy.setDate(CMIS.CREATION_DATE, Calendar.getInstance());
         ConversationState cstate = ConversationState.getCurrent();
         String userId = null;
         if (cstate != null)
         {
            userId = cstate.getIdentity().getUserId();
         }
         if (userId != null)
         {
            copy.setString(CMIS.CREATED_BY, userId);
         }
         if (versioningState == EnumVersioningState.CHECKEDOUT)
         {
            return copy.setBoolean(CMIS.IS_VERSION_SERIES_CHECKED_OUT, true);
         }
         else if (versioningState == EnumVersioningState.MAJOR)
         {
            copy.setBoolean(CMIS.IS_MAJOR_VERSION, true);
         }
         copy.save();
         return copy;
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to copy object. " + re.getMessage();
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public Entry createObject(CmisTypeDefinitionType type, EnumVersioningState versioningState)
   {
      throw new UnsupportedOperationException("createObject");
   }

   //   /**
   //    * {@inheritDoc}
   //    */
   //   public boolean createRenditions(Entry entry) throws InvalidArgumentException, RepositoryException
   //   {
   //      if (!entry.canGetContent())
   //         return false;
   //      ContentStream content;
   //      try
   //      {
   //         content = entry.getContent(null);
   //      }
   //      catch (ConstraintException cve)
   //      {
   //         return false;
   //      }
   //      if (content == null)
   //         return false;
   //      try
   //      {
   //         MimeType contentType = MimeType.fromString(content.getMediaType());
   //         int count = 0;
   //         for (Map.Entry<MimeType, RenditionProvider> e : renditionProviders.entrySet())
   //         {
   //            if (e.getKey().match(contentType))
   //            {
   //               RenditionProvider renditionProvider = e.getValue();
   //               RenditionContentStream renditionContentStream = renditionProvider.getRenditionStream(entry);
   //               Node rendition = ((EntryImpl)entry).getNode().addNode(IdGenerator.generate(), JcrCMIS.CMIS_NT_RENDITION);
   //               rendition.setProperty(JcrCMIS.CMIS_RENDITION_STREAM, renditionContentStream.getStream());
   //               rendition.setProperty(JcrCMIS.CMIS_RENDITION_MIME_TYPE, renditionContentStream.getMediaType());
   //               rendition.setProperty(JcrCMIS.CMIS_RENDITION_KIND, renditionContentStream.getKind());
   //               rendition.setProperty(JcrCMIS.CMIS_RENDITION_HEIGHT, renditionContentStream.getHeight());
   //               rendition.setProperty(JcrCMIS.CMIS_RENDITION_WIDTH, renditionContentStream.getWidth());
   //               count++;
   //            }
   //         }
   //         if (count > 0)
   //         {
   //            ((EntryImpl)entry).getNode().save();
   //            return true;
   //         }
   //         return false;
   //      }
   //      catch (javax.jcr.RepositoryException re)
   //      {
   //         String msg = "Failed create rendtions for object " + entry.getObjectId() + ". " + re.getMessage();
   //         throw new RepositoryException(msg, re);
   //      }
   //      catch (Exception other)
   //      {
   //         String msg = "Failed create rendtions for object " + entry.getObjectId() + ". " + other.getMessage();
   //         LOG.error(msg);
   //         return false;
   //      }
   //   }

   /**
    * {@inheritDoc}
    */
   public ChangeTokenMatcher getChangeTokenMatcher() throws RepositoryException
   {
      return this;
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<Entry> getCheckedOutDocuments(String folderId) throws RepositoryException
   {

      if (LOG.isDebugEnabled())
      {
         LOG.debug("Get all checked out document.");
      }

      List<Entry> checkedout = new ArrayList<Entry>();
      try
      {
         if (folderId == null)
         {
            checkedOutDocuments(new EntryImpl(getSession().getRootNode()), checkedout, true);
         }
         else
         {
            checkedOutDocuments(getObjectById(folderId), checkedout, false);
         }
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to get checked-out documents. " + re.getMessage();
         throw new RepositoryException(msg, re);
      }
      return new BaseItemsIterator<Entry>(checkedout);
   }

   /**
    * {@inheritDoc}
    */
   public String getEntryName(String parentId, EnumBaseObjectTypeIds scope, String typeId)
   {
      if (LOG.isDebugEnabled())
      {
         LOG
            .debug("Generate name for entry, parentId " + parentId + ", typedId " + typeId + ", scope " + scope.value());
      }
      try
      {
         String pattern;
         if (scope == EnumBaseObjectTypeIds.CMIS_DOCUMENT)
         {
            pattern = JcrCMIS.DEFAULT_DOCUMENT_NAME;
         }
         else if (scope == EnumBaseObjectTypeIds.CMIS_FOLDER)
         {
            pattern = JcrCMIS.DEFAULT_FOLDER_NAME;
         }
         else if (scope == EnumBaseObjectTypeIds.CMIS_POLICY)
         {
            pattern = JcrCMIS.DEFAULT_POLICY_NAME;
         }
         else if (scope == EnumBaseObjectTypeIds.CMIS_RELATIONSHIP)
         {
            return IdGenerator.generate();
         }
         else
         {
            throw new UnsupportedOperationException();
         }
         Node parent = ((ExtendedSession)getSession()).getNodeByIdentifier(parentId);
         NodeIterator items = parent.getNodes(pattern + CMIS.WILDCARD);
         long count = items.getSize();
         if (count < 0)
         {
            // Count is negative. May be true if information about length is not available.
            count = 0;
            while (items.hasNext())
            {
               items.next();
               count++;
            }
         }

         String name;
         if (count == 0)
         {
            // First item does not need index.
            return pattern;
         }
         else
         {
            // Be sure node with the same name does not exists
            count++;
            while (parent.hasNode(pattern + " (" + count + ")"))
            {
               count++;
            }
            name = pattern + " (" + String.valueOf(count) + ")";
         }
         if (LOG.isDebugEnabled())
         {
            LOG.debug("Entry name: " + name);
         }
         return name;
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to generate item's name. " + re.getMessage();
         throw new RuntimeException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public String getId()
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Get repository id");
      }
      return config.getId();
   }

   /**
    * {@inheritDoc}
    */
   public String getNodeTypeByQueryName(String queryName)
   {
      return getNodeTypeName(queryName);
   }

   /**
    * {@inheritDoc}
    */
   public Entry getObjectById(String objectId) throws ObjectNotFoundException, RepositoryException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Get object with id '" + objectId + "'");
      }
      try
      {
         Node node = ((ExtendedSession)getSession()).getNodeByIdentifier(objectId);
         Entry object = null;
         if (node.isNodeType(JcrCMIS.NT_VERSION))
         {
            object = new EntryVersion((Version)node);
         }
         else
         {
            object = new EntryImpl(node);
         }
         return object;
      }
      catch (ItemNotFoundException infe)
      {
         String msg = "Object '" + objectId + "' not found.";
         throw new ObjectNotFoundException(msg);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unexpected error. " + re.getMessage();
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public Entry getObjectByPath(String path) throws ObjectNotFoundException, RepositoryException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Get object with path " + path);
      }
      try
      {
         return new EntryImpl((Node)getSession().getItem(path.charAt(0) == '/' ? path : '/' + path));
      }
      catch (PathNotFoundException pnfe)
      {
         String msg = "Object at " + path + " not found.";
         throw new ObjectNotFoundException(msg);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unexpected error. " + re.getMessage();
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public QueryHandler getQueryHandler() throws RepositoryException
   {
      return queryHandler;
   }

   /**
    * {@inheritDoc}
    */
   public RenditionManager getRenditionManager()
   {
      return renditionManager;
   }

   //   /**
   //    * {@inheritDoc}
   //    */
   //   public ItemsIterator<CmisRenditionType> getRenditions(Entry entry) throws RepositoryException
   //   {
   //      try
   //      {
   //         return new RenditionIterator(((EntryImpl)entry).getNode().getNodes());
   //      }
   //      catch (javax.jcr.RepositoryException re)
   //      {
   //         String msg =
   //            "Unable get renditions for object " + entry.getObjectId() + " Unexpected error " + re.getMessage();
   //         throw new RepositoryException(msg, re);
   //      }
   //   }
   //
   //   /**
   //    * {@inheritDoc}
   //    */
   //   public ItemsIterator<CmisRenditionType> getRenditions(String objectId) throws ObjectNotFoundException,
   //      RepositoryException
   //   {
   //      return getRenditions(getObjectById(objectId));
   //   }

   /**
    * Get repository configuration.
    * 
    * @return repository configuration
    */
   public CMISRepositoryConfiguration getRepositoryConfiguration()
   {
      return config;
   }

   /**
    * {@inheritDoc}
    */
   public CmisRepositoryInfoType getRepositoryInfo()
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Get repository info");
      }

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
         capabilities.setCapabilityACL(EnumCapabilityACL.DISCOVER);
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
         // XXX Generated code (EnumBasicPermissions) looks incorrect and incomplete.
         // Bugs in XML schemas or schemas and specification are not corresponded to each other. 
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
         // See JcrCMISEntry.applyPolicy(CMISEntry)
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_ADD_POLICY_OBJECT, // 
            EnumBasicPermissions.CMIS_WRITE.value()));
         aclCapabilities.getMapping().add(createPermissionMapping(//
            EnumAllowableActionsKey.CAN_ADD_POLICY_POLICY, //
            EnumBasicPermissions.CMIS_READ.value()));
         // Specification says should be 'read' permission but because to
         // implementation policy feature we need write access.
         // See JcrCMISEntry.applyPolicy(CMISEntry)
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

   /**
    * {@inheritDoc}
    */
   public Entry getRootFolder() throws RepositoryException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Get root folder.");
      }
      try
      {
         return new EntryImpl(getSession().getRootNode());
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable get root folder. " + re.getMessage();
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<CmisTypeDefinitionType> getTypeChildren(String typeId, boolean includePropertyDefinition)
      throws RepositoryException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Get object types, typeId " + typeId);
      }
      try
      {
         List<CmisTypeDefinitionType> types = new ArrayList<CmisTypeDefinitionType>();
         if (typeId == null)
         {
            types.add(getTypeDefinition(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value(), includePropertyDefinition));
            types.add(getTypeDefinition(EnumBaseObjectTypeIds.CMIS_FOLDER.value(), includePropertyDefinition));
            types.add(getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value(), includePropertyDefinition));
            types.add(getTypeDefinition(EnumBaseObjectTypeIds.CMIS_POLICY.value(), includePropertyDefinition));
         }
         else
         {
            for (NodeTypeIterator iter = getSession().getWorkspace().getNodeTypeManager().getPrimaryNodeTypes(); iter
               .hasNext();)
            {
               NodeType nt = iter.nextNodeType();
               String internalTypeId = getNodeTypeName(typeId);
               // Get only direct children of specified type.
               if (nt.isNodeType(internalTypeId) && getTypeLevelHierarchy(nt, internalTypeId) == 1)
               {
                  types.add(getTypeDefinition(nt, includePropertyDefinition));
               }
            }
         }
         // XXX: Not efficient to get all types in list. Better to do it in
         // iterator to be skip not needed items and stop when reach 'maxItems'. 
         return new BaseItemsIterator<CmisTypeDefinitionType>(types);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to get object types. " + re.getMessage();
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisTypeContainer> getTypeDescendants(String typeId, int depth, boolean includePropertyDefinition)
      throws RepositoryException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Get descendants object types, typeId " + typeId + ", depth " + depth);
      }

      List<CmisTypeContainer> types = new ArrayList<CmisTypeContainer>();
      if (typeId == null)
      {
         // Check all four root types with depth -1.
         // In this case will got all types and its descendants. 
         CmisTypeContainer docContainer = new CmisTypeContainer();
         docContainer.setType(getTypeDefinition(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value()));
         docContainer.getChildren().addAll(
            getTypeDescendants(EnumBaseObjectTypeIds.CMIS_DOCUMENT.value(), -1, includePropertyDefinition));
         types.add(docContainer);

         CmisTypeContainer folderContainer = new CmisTypeContainer();
         folderContainer.setType(getTypeDefinition(EnumBaseObjectTypeIds.CMIS_FOLDER.value()));
         folderContainer.getChildren().addAll(
            getTypeDescendants(EnumBaseObjectTypeIds.CMIS_FOLDER.value(), -1, includePropertyDefinition));
         types.add(folderContainer);

         CmisTypeContainer relationshipContainer = new CmisTypeContainer();
         relationshipContainer.setType(getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value()));
         relationshipContainer.getChildren().addAll(
            getTypeDescendants(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value(), -1, includePropertyDefinition));
         types.add(relationshipContainer);

         CmisTypeContainer policyContainer = new CmisTypeContainer();
         policyContainer.setType(getTypeDefinition(EnumBaseObjectTypeIds.CMIS_POLICY.value()));
         policyContainer.getChildren().addAll(
            getTypeDescendants(EnumBaseObjectTypeIds.CMIS_POLICY.value(), -1, includePropertyDefinition));
         types.add(policyContainer);
      }
      else
      {
         try
         {
            Map<Integer, List<CmisTypeDefinitionType>> cache = new HashMap<Integer, List<CmisTypeDefinitionType>>();
            String internalTypeId = getNodeTypeName(typeId);
            for (NodeTypeIterator iter = getSession().getWorkspace().getNodeTypeManager().getPrimaryNodeTypes(); iter
               .hasNext();)
            {
               NodeType nt = iter.nextNodeType();
               if (nt.isNodeType(internalTypeId))
               {
                  // NodeType returned in order that is differ to hierarchical (plain).
                  // Need determine for each node-type is it still accepted by
                  // specified depth parameter.
                  int level = getTypeLevelHierarchy(nt, internalTypeId);

                  if ((depth == -1 || level <= depth) && depth != 0)
                  {
                     List<CmisTypeDefinitionType> temp = cache.get(level);
                     if (temp == null)
                     {
                        temp = new ArrayList<CmisTypeDefinitionType>();
                        cache.put(level, temp);
                     }
                     CmisTypeDefinitionType typeDefinition = getTypeDefinition(nt, includePropertyDefinition);
                     temp.add(typeDefinition);
                  }
               }
            }
            // Fill final list of types in hierarchy structure
            if (cache.get(1) != null)
            {
               for (CmisTypeDefinitionType t : cache.get(1))
               {
                  CmisTypeContainer containerType = new CmisTypeContainer();
                  containerType.setType(t);
                  addTypeDescendants(containerType, 2, cache);
                  types.add(containerType);
               }
            }
         }
         catch (javax.jcr.RepositoryException re)
         {
            String msg = "Unable to get object types. " + re.getMessage();
            throw new RepositoryException(msg, re);
         }
      }
      return types;
   }

   /**
    * {@inheritDoc}
    */
   public VersionSeries getVersionSeries(String versionSeriesId) throws ObjectNotFoundException, RepositoryException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("In getVersionSeries " + versionSeriesId);
      }
      try
      {
         Node node = ((ExtendedSession)getSession()).getNodeByIdentifier(versionSeriesId);
         return new VersionSeriesImpl((VersionHistory)node);
      }
      catch (ItemNotFoundException infe)
      {
         String msg = "Version series " + versionSeriesId + " dies not exist.";
         throw new ObjectNotFoundException(msg);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable get version series " + versionSeriesId + ". " + re.getMessage();
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public boolean isMatch(Entry entry, String expected) throws RepositoryException
   {
      if (changeTokenFeature)
      {
         return expected != null && expected.equals(entry.getString(CMIS.CHANGE_TOKEN));
      }
      // If change token feature is disabled don't check anything. 
      // We are not care about change tokens at all.
      return true;
   }

   /**
    * {@inheritDoc}
    */
   public void moveObject(String objectId, String destinationFolderId, String sourceFolderId)
      throws ObjectNotFoundException, ConstraintException, RepositoryException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("Move object " + objectId + " to new folder " + destinationFolderId);
      }

      EntryImpl object = (EntryImpl)getObjectById(objectId);

      if (!object.getType().isFileable())
      {
         String msg = "Object " + objectId + " is not fileable.";
         throw new ConstraintException(msg);
      }

      EntryImpl destinationFolder = (EntryImpl)getObjectById(destinationFolderId);
      if (destinationFolder.getScope() != EnumBaseObjectTypeIds.CMIS_FOLDER)
      {
         String msg = "Target object " + destinationFolderId + " is not a folder.";
         throw new ConstraintException(msg);
      }

      try
      {
         Node srcNode = object.getNode();
         Node destNode = destinationFolder.getNode();
         String srcPath = srcNode.getPath();
         String destPath = destNode.getPath();
         if (destPath.equals("/"))
         {
            destPath += srcNode.getName();
         }
         else
         {
            destPath += "/" + srcNode.getName();
         }

         getSession().getWorkspace().move(srcPath, destPath);

         if (LOG.isDebugEnabled())
         {
            LOG.debug("Object moved in " + destPath);
         }
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to move object. " + re.getMessage();
         throw new RepositoryException(msg, re);
      }
   }

   //   /**
   //    * {@inheritDoc}
   //    */
   //   public void removeRenditions(Entry entry) throws RepositoryException
   //   {
   //      try
   //      {
   //         int count = 0;
   //         for (NodeIterator iter = ((EntryImpl)entry).getNode().getNodes(); iter.hasNext();)
   //         {
   //            Node item = iter.nextNode();
   //            if (item.isNodeType(JcrCMIS.CMIS_NT_RENDITION))
   //            {
   //               item.remove();
   //               count++;
   //            }
   //         }
   //         if (count > 0)
   //            ((EntryImpl)entry).getNode().save();
   //      }
   //      catch (javax.jcr.RepositoryException re)
   //      {
   //         String msg = "Unable to remove renditions for object " + entry.getObjectId() + ". " + re.getMessage();
   //         throw new RepositoryException(msg, re);
   //      }
   //   }
   //
   //   /**
   //    * {@inheritDoc}
   //    */
   //   public void removeRenditions(String objectId) throws ObjectNotFoundException, RepositoryException
   //   {
   //      removeRenditions(getObjectById(objectId));
   //   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void removeType(String typeId) throws TypeNotFoundException, RepositoryException
   {
      // Throws exceptions if type with specified 'typeId' does not exists or is unsupported by CMIS.
      getTypeDefinition(typeId);

      try
      {
         ExtendedNodeTypeManager nodeTypeManager =
            (ExtendedNodeTypeManager)getSession().getWorkspace().getNodeTypeManager();
         nodeTypeManager.unregisterNodeType(typeId);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable remove CMIS type " + typeId + ". " + re.getMessage();
         throw new RepositoryException(msg);
      }
   }

   /**
    * Add descendants for the type.
    * 
    * @param containerType the CMIS type container
    * @param level discovering depth
    * @param cache map for adding types 
    */
   private void addTypeDescendants(CmisTypeContainer containerType, int level,
      Map<Integer, List<CmisTypeDefinitionType>> cache)
   {
      if (cache.get(level) != null)
      {
         for (CmisTypeDefinitionType type : cache.get(level))
         {
            CmisTypeContainer t = new CmisTypeContainer();
            t.setType(type);
            if (containerType.getType().getId().equals(type.getParentId()))
            {
               containerType.getChildren().add(t);
            }
            addTypeDescendants(t, level + 1, cache);
         }
      }
   }

   /**
    * Check checked-out documents.
    * 
    * @param folder the JCR node for the folder
    * @param docs the list of CMISEntry
    * @param recursive whether it is recursive
    * @throws javax.jcr.RepositoryException if any JCR repository errors
    */
   private void checkedOutDocuments(Entry folder, List<Entry> docs, boolean recursive) throws RepositoryException
   {
      for (ItemsIterator<Entry> iter = folder.getChildren(); iter.hasNext();)
      {
         EntryImpl entry = (EntryImpl)iter.next();
         if (entry.getScope() == EnumBaseObjectTypeIds.CMIS_DOCUMENT)
         {
            Entry pwc = entry.getVersionSeries().getCheckedOut();
            if (pwc != null)
            {
               docs.add(pwc);
            }
         }
         else if (recursive && entry.getScope() == EnumBaseObjectTypeIds.CMIS_FOLDER)
         {
            checkedOutDocuments(entry, docs, recursive);
         }
      }
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
      {
         throw new NullPointerException("permissions is null.");
      }
      if (actionsKey == null)
      {
         throw new NullPointerException("actionsKey is null.");
      }
      CmisPermissionMapping mapping = new CmisPermissionMapping();
      mapping.setKey(actionsKey);
      if (permissions.length == 0)
      {
         mapping.getPermission().add(EnumBasicPermissions.CMIS_ALL.value());
      }
      else
      {
         for (String permission : permissions)
         {
            mapping.getPermission().add(permission);
         }
      }
      return mapping;
   }

   /**
    * Get JCR session.
    * 
    * @return the JCR session
    * @throws javax.jcr.RepositoryException if any JCR repository errors
    */
   private Session getSession() throws javax.jcr.RepositoryException
   {
      if (session == null)
      {
         session = sesProv.getSession(config.getWorkspace(), (ManageableRepository)backendRepo);
      }
      return session;
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
    * {@inheritDoc}
    */
   protected NodeType getNodeType(String name) throws NoSuchNodeTypeException, javax.jcr.RepositoryException
   {
      NodeType nt = getSession().getWorkspace().getNodeTypeManager().getNodeType(name);
      return nt;
   }

}
