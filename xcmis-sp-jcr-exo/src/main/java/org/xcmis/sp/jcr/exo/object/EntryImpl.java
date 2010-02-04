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

package org.xcmis.sp.jcr.exo.object;

import org.xcmis.core.CmisAccessControlEntryType;
import org.xcmis.core.CmisAccessControlPrincipalType;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.CmisTypeDocumentDefinitionType;
import org.xcmis.core.CmisTypeRelationshipDefinitionType;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumBasicPermissions;
import org.xcmis.core.EnumContentStreamAllowed;
import org.xcmis.core.EnumRelationshipDirection;
import org.xcmis.core.EnumVersioningState;
import org.exoplatform.services.jcr.access.AccessControlEntry;
import org.exoplatform.services.jcr.access.AccessControlList;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.impl.core.PropertyImpl;
import org.exoplatform.services.jcr.impl.core.value.BooleanValue;
import org.exoplatform.services.jcr.impl.core.value.DateValue;
import org.exoplatform.services.jcr.impl.core.value.DoubleValue;
import org.exoplatform.services.jcr.impl.core.value.LongValue;
import org.exoplatform.services.jcr.impl.core.value.StringValue;
import org.exoplatform.services.jcr.util.IdGenerator;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.xcmis.sp.jcr.exo.JcrCMIS;
import org.xcmis.sp.jcr.exo.TypeManagerImpl;
import org.xcmis.sp.jcr.exo.rendition.RenditionContentStream;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.PermissionDeniedException;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.object.BaseContentStream;
import org.xcmis.spi.object.BaseItemsIterator;
import org.xcmis.spi.object.ContentStream;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.ItemsIterator;
import org.xcmis.spi.object.VersionSeries;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.NodeType;

/**
 * Instances of this class represent current version of CMIS Document.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class EntryImpl extends TypeManagerImpl implements Entry
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(EntryImpl.class);

   private static Map<String, String> cmis2jcr = new HashMap<String, String>();

   static
   {
      cmis2jcr.put(CMIS.CREATION_DATE, "jcr:created");
      cmis2jcr.put(CMIS.CREATED_BY, "exo:owner");
      cmis2jcr.put(CMIS.CONTENT_STREAM_MIME_TYPE, "jcr:content/jcr:mimeType");
      cmis2jcr.put(CMIS.CONTENT_STREAM_ID, "jcr:content/jcr:uuid");
      cmis2jcr.put(CMIS.CONTENT_STREAM_ID, "jcr:content/jcr:lastModified");
   }

   static String latestLabel = "latest";

   static String pwcLabel = "pwc";

   /** Jcr node. */
   protected Node node;

   /** CMIS object type. */
   protected CmisTypeDefinitionType type;

   /** Object Id. */
   protected String id;

   /** JCR session. */
   protected final Session session;

   /**
    * Create EntryImpl instance.
    *     
    * @param node the JCR node
    * @throws InvalidArgumentException if node has unsupported node-type
    * @throws javax.jcr.RepositoryException if not able to create CMIS object
    *            because to occurred error in JCR back-end
    */
   public EntryImpl(Node node) throws InvalidArgumentException, javax.jcr.RepositoryException
   {
      session = node.getSession();
      init(node);
   }

   /**
    * Create EntryImpl instance.
    * 
    * @param node the JCR node
    * @param type the CMIS type definition
    * @throws javax.jcr.RepositoryException if not able to create CMIS object
    *            because to occurred error in JCR back-end
    */
   private EntryImpl(Node node, CmisTypeDefinitionType type) throws javax.jcr.RepositoryException
   {
      if (type == null)
         throw new NullPointerException("Type is null.");
      session = node.getSession();
      init(node, type);
   }

   /**
    * Not supported.
    *
    * {@inheritDoc}
    */
   public void addChild(Entry child)
   {
      throw new UnsupportedOperationException("addChild");
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisAccessControlEntryType> addPermissions(List<CmisAccessControlEntryType> aces)
      throws ConstraintException, RepositoryException
   {
      if (!getType().isControllableACL())
      {
         String msg = "Type " + getType().getId() + " is not controlable by ACL.";
         throw new ConstraintException(msg);
      }

      try
      {
         preUpdate();
         if (!node.isNodeType(JcrCMIS.EXO_PRIVILEGABLE))
            node.addMixin(JcrCMIS.EXO_PRIVILEGABLE);
         ((ExtendedNode)node).setPermissions(createPermissionMap(aces));
      }
      catch (AccessControlException ac)
      {
         String msg = "Operation not permitted.";
         throw new PermissionDeniedException(msg);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to apply ACEs to object. " + re.getMessage();
         throw new RepositoryException(msg);
      }
      return getPermissions();
   }

   /**
    * {@inheritDoc}
    *     
    * Relationships stored in following manner.
    * 
    * <pre>
    *  /ROOT
    *   |
    *   - RELATIONSHIPS_FOLDER
    *     |
    *      - relHierarchy (container node for relationships that has the same name as current (source) object Id)
    *        |
    *        - relationships (each one has the specified name)
    *        - ...
    * </pre>
    */
   public Entry addRelationship(String name, Entry target, CmisTypeDefinitionType relationshipType)
      throws RepositoryException
   {
      if (!this.isIndependent())
      {
         String msg =
            "Object with id: " + getObjectId() + " and type: " + this.getType().getId()
               + " may be used as 'source' of relationship";
         throw new ConstraintException(msg);
      }
      if (!((EntryImpl)target).isIndependent())
      {
         String msg =
            "Object with id: " + getObjectId() + " and type: " + target.getType().getId()
               + " may be used as 'target' of relationship";
         throw new ConstraintException(msg);
      }

      List<String> allowalableSources = ((CmisTypeRelationshipDefinitionType)relationshipType).getAllowedSourceTypes();
      if (allowalableSources.size() > 0 && !allowalableSources.contains(getType().getId()))
      {
         String msg =
            "Relationship object-type " + relationshipType.getId() + " does not support object-type "
               + getType().getId() + " as source.";
         throw new ConstraintException(msg);
      }
      List<String> allowalableTarget = ((CmisTypeRelationshipDefinitionType)relationshipType).getAllowedTargetTypes();
      if (allowalableTarget.size() > 0 && !allowalableTarget.contains(target.getType().getId()))
      {
         String msg =
            "Relationship object-type " + relationshipType.getId() + " does not support object-type "
               + target.getType().getId() + " as target.";
         throw new ConstraintException(msg);
      }

      try
      {
         return createRelationship(name, relationshipType, target);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to create relationship. " + re.getMessage();
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void applyPolicy(Entry policy) throws RepositoryException
   {
      if (!getType().isControllablePolicy())
      {
         String msg = "Type " + getType().getId() + " is not controlable by Policy.";
         throw new ConstraintException(msg);
      }

      try
      {
         preUpdate();
         node.setProperty(policy.getObjectId(), ((EntryImpl)policy).getNode());
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to apply policy. " + re.getMessage();
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public boolean canAddPolicy()
   {
      return getType().isControllablePolicy();
   }

   /**
    * {@inheritDoc}
    */
   public boolean canAddToFolder()
   {
      // AddObjectToFolder is supported only if the repository supports the
      // multi-filing optional capabilities.
      return false;
   }

   /**
    * {@inheritDoc}
    */
   public boolean canApplyACL()
   {
      return getType().isControllableACL();
   }

   /**
    * {@inheritDoc}
    */
   public boolean canCancelCheckOut()
   {
      try
      {
         return isVersionable() && getCheckedOutId() != null;
      }
      catch (RepositoryException re)
      {
         LOG.error("Unexpected error.", re);
         return false;
      }
   }

   /**
    * {@inheritDoc}
    */
   public boolean canCheckIn()
   {
      try
      {
         return isVersionable() && getCheckedOutId() != null;
      }
      catch (RepositoryException re)
      {
         LOG.error("Unexpected error.", re);
         return false;
      }
   }

   /**
    * {@inheritDoc}
    */
   public boolean canCheckOut()
   {
      try
      {
         return isVersionable() && getCheckedOutId() == null;
      }
      catch (RepositoryException re)
      {
         LOG.error("Unexpected error.", re);
         return false;
      }
   }

   /**
    * {@inheritDoc}
    */
   public boolean canCreateDocument()
   {
      return getScope() == EnumBaseObjectTypeIds.CMIS_FOLDER;
   }

   /**
    * {@inheritDoc}
    */
   public boolean canCreateFolder()
   {
      return getScope() == EnumBaseObjectTypeIds.CMIS_FOLDER;
   }

   //   /**
   //    * {@inheritDoc}
   //    */
   //   public boolean canCreatePolicy()
   //   {
   //      return getScope() == EnumBaseObjectTypeIds.CMIS_FOLDER;
   //   }

   /**
    * {@inheritDoc}
    */
   public boolean canCreateRelationship()
   {
      // Must be independent object, so may have any type except
      // 'cmis:relationship' and its child.
      return isIndependent();
   }

   /**
    * {@inheritDoc}
    */
   public boolean canDelete()
   {
      try
      {
         validateDelete();
         return true;
      }
      catch (ConstraintException ce)
      {
         LOG.error("Unexpected error. ", ce);
         return false;
      }
      catch (RepositoryException re)
      {
         LOG.error("Unexpected error. ", re);
         return false;
      }
   }

   /**
    * {@inheritDoc}
    */
   public boolean canDeleteContent()
   {
      return getScope() == EnumBaseObjectTypeIds.CMIS_DOCUMENT;
   }

   /**
    * {@inheritDoc}
    */
   public boolean canDeleteTree()
   {
      return getScope() == EnumBaseObjectTypeIds.CMIS_FOLDER;
   }

   /**
    * {@inheritDoc}
    */
   public boolean canGetACL()
   {
      return getType().isControllableACL();
   }

   /**
    * {@inheritDoc}
    */
   public boolean canGetAllVersions()
   {
      // Even document is not versionable version series contains at least one
      // version of document (this version). So does not check is object
      // versionable, just check is it Document.
      return getScope() == EnumBaseObjectTypeIds.CMIS_DOCUMENT;
   }

   /**
    * {@inheritDoc}
    */
   public boolean canGetAppliedPolicies()
   {
      return getType().isControllablePolicy();
   }

   /**
    * {@inheritDoc}
    */
   public boolean canGetChildren()
   {
      return getScope() == EnumBaseObjectTypeIds.CMIS_FOLDER;
   }

   /**
    * {@inheritDoc}
    */
   public boolean canGetContent()
   {
      return getScope() == EnumBaseObjectTypeIds.CMIS_DOCUMENT
         && ((CmisTypeDocumentDefinitionType)getType()).getContentStreamAllowed() != EnumContentStreamAllowed.NOTALLOWED;
   }

   /**
    * {@inheritDoc}
    */
   public boolean canGetDescendants()
   {
      return getScope() == EnumBaseObjectTypeIds.CMIS_FOLDER;
   }

   /**
    * {@inheritDoc}
    */
   public boolean canGetFolderParent()
   {
      return getScope() == EnumBaseObjectTypeIds.CMIS_FOLDER;
   }

   /**
    * {@inheritDoc}
    */
   public boolean canGetFolderTree()
   {
      return getScope() == EnumBaseObjectTypeIds.CMIS_FOLDER;
   }

   /**
    * {@inheritDoc}
    */
   public boolean canGetParents()
   {
      return getType().isFileable();
   }

   /**
    * {@inheritDoc}
    */
   public boolean canGetProperties()
   {
      return true;
   }

   /**
    * {@inheritDoc}
    */
   public boolean canGetRelationships()
   {
      // Must be independent object, so may have any type except
      // 'cmis:relationship' and its child.
      return isIndependent();
   }

   /**
    * {@inheritDoc}
    */
   public boolean canGetRenditions()
   {
      // Only Documents support renditions. 
      return getScope() == EnumBaseObjectTypeIds.CMIS_DOCUMENT;
   }

   /**
    * {@inheritDoc}
    */
   public boolean canMove()
   {
      return getType().isFileable();
   }

   /**
    * {@inheritDoc}
    */
   public boolean canRemoveFromFolder()
   {
      // CanRemoveFromFolder is supported only if the repository supports the
      // unfiling optional capabilities.
      return false;
   }

   /**
    * {@inheritDoc}
    */
   public boolean canRemovePolicy()
   {
      return getType().isControllablePolicy();
   }

   /**
    * {@inheritDoc}
    */
   public boolean canSetContent()
   {
      return getScope() == EnumBaseObjectTypeIds.CMIS_DOCUMENT
         && ((CmisTypeDocumentDefinitionType)getType()).getContentStreamAllowed() != EnumContentStreamAllowed.NOTALLOWED;
   }

   /**
    * {@inheritDoc}
    */
   public boolean canUpdateProperties()
   {
      try
      {
         validateUpdate();
         return true;
      }
      catch (ConstraintException ce)
      {
         LOG.error("Unexpected error. ", ce);
         return false;
      }
      catch (RepositoryException re)
      {
         LOG.error("Unexpected error. ", re);
         return false;
      }
   }

   /**
    * {@inheritDoc}
    */
   public Entry createChild(CmisTypeDefinitionType type, String name, EnumVersioningState versioningState)
      throws RepositoryException
   {
      if (getScope() != EnumBaseObjectTypeIds.CMIS_FOLDER)
      {
         String msg = "Object with type: " + getType().getId() + " may not have children.";
         throw new ConstraintException(msg);
      }
      try
      {
         if (type.getBaseId() == EnumBaseObjectTypeIds.CMIS_DOCUMENT)
         {
            return createDocument(type, name, versioningState == null ? EnumVersioningState.MAJOR : versioningState);
         }
         else if (type.getBaseId() == EnumBaseObjectTypeIds.CMIS_FOLDER)
         {
            return createFolder(type, name);
         }
         else if (type.getBaseId() == EnumBaseObjectTypeIds.CMIS_POLICY)
         {
            return createPolicy(type, name);
         }
         else
         {
            String msg = "Unsuported object type " + type.getDisplayName();
            throw new InvalidArgumentException(msg);
         }
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to create child object. " + re.getMessage();
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void delete() throws RepositoryException, ConstraintException
   {
      try
      {
         preDelete();

         // TODO : simplify delete
         if (getScope() == EnumBaseObjectTypeIds.CMIS_DOCUMENT)
         {
            VersionSeries versionSeries = getVersionSeries();
            Entry pwc = versionSeries.getCheckedOut();
            if (pwc != null)
            {
               if (this.equals(pwc))
               {
                  versionSeries.cancelCheckout();
                  return;
               }
               else
               {
                  String msg = "Unable to delete document from chekedout version series";
                  throw new ConstraintException(msg);
               }
            }
         }

         // Remove all relationships in which current object is target or source.
         removeRelationships();
         node.remove();
         session.save();
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to remove object. " + re.getMessage();
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object other)
   {
      if (other == null)
         return false;
      if (getClass() != other.getClass())
         return false;
      return id.equals(((EntryImpl)other).id);
   }

   /**
    * {@inheritDoc}
    */
   public List<Entry> getAppliedPolicies() throws RepositoryException
   {
      try
      {
         List<Entry> policies = new ArrayList<Entry>();
         for (PropertyIterator iter = node.getProperties(); iter.hasNext();)
         {
            Property prop = iter.nextProperty();
            if (prop.getType() == PropertyType.REFERENCE)
            {
               try
               {
                  Node rs = prop.getNode();
                  if (rs.getPrimaryNodeType().isNodeType(JcrCMIS.CMIS_POLICY))
                  {
                     if (LOG.isDebugEnabled())
                        LOG.debug("Add policy " + prop.getName());
                     policies.add(new EntryImpl(rs));
                  }
               }
               catch (ValueFormatException ignored)
               {
                  // Can be thrown id met multi-valued property.
                  // Not care about it cause policy reference may not be multi-valued.
               }
            }
         }
         return policies;
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable get applied policies to object. " + re.getMessage();
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public boolean getBoolean(String name) throws RepositoryException
   {
      try
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Get boolean " + name);
         return node.getProperty(name).getBoolean();
      }
      catch (javax.jcr.PathNotFoundException pnfe)
      {
         // TODO
         if (CMIS.IS_IMMUTABLE.equals(name))
         {
            return false;
         }
         else if (CMIS.IS_LATEST_VERSION.equals(name))
         {
            return true;
         }
         else if (CMIS.IS_MAJOR_VERSION.equals(name))
         {
            return false;
         }
         else if (CMIS.IS_LATEST_MAJOR_VERSION.equals(name))
         {
            if (getScope() == EnumBaseObjectTypeIds.CMIS_DOCUMENT)
               return isLatest() && isMajor();
            return false;
         }

         return false;
      }
      catch (javax.jcr.ValueFormatException vfe)
      {
         String msg = "Unable get property " + name + " as boolean value.";
         throw new InvalidArgumentException(msg, vfe);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to get property " + name;
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public boolean[] getBooleans(String name) throws RepositoryException
   {
      try
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Get boolean[] " + name);
         Property jcrProp = node.getProperty(name);
         if (((PropertyImpl)jcrProp).isMultiValued())
         {
            Value[] values = jcrProp.getValues();
            boolean[] res = new boolean[values.length];
            for (int i = 0; i < values.length; i++)
               res[i] = values[i].getBoolean();
            return res;
         }
         return new boolean[]{jcrProp.getBoolean()};
      }
      catch (javax.jcr.PathNotFoundException pnfe)
      {
         return null;
      }
      catch (javax.jcr.ValueFormatException vfe)
      {
         String msg = "Unable get property " + name + " as boolean[].";
         throw new InvalidArgumentException(msg, vfe);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to get property " + name;
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public String getCheckedOutBy() throws RepositoryException
   {
      return getString(CMIS.VERSION_SERIES_CHECKED_OUT_BY);
   }

   /**
    * {@inheritDoc}
    */
   public String getCheckedOutId() throws RepositoryException
   {
      return getString(CMIS.VERSION_SERIES_CHECKED_OUT_ID);
   }

   /**
    * {@inheritDoc}
    */
   public String getCheckInComment() throws RepositoryException
   {
      return getString(CMIS.CHECKIN_COMMENT);
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<Entry> getChildren() throws RepositoryException
   {
      if (getScope() != EnumBaseObjectTypeIds.CMIS_FOLDER)
      {
         String msg = "Object with type: " + getType().getId() + " may not have children.";
         throw new UnsupportedOperationException(msg);
      }
      try
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Get children of " + node.getPath());
         return new ItemsIteratorImpl(node.getNodes());
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to get children. " + re.getMessage();
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<Entry> getChildren(String orderBy) throws RepositoryException
   {
      // TODO : use 'orderBy'.
      return getChildren();
   }

   /**
    * {@inheritDoc}
    */
   public ContentStream getContent(String streamId) throws RepositoryException, ConstraintException
   {
      if (!canGetContent())
      {
         String msg = "Object does not support content stream.";
         throw new ConstraintException(msg);
      }
      try
      {
         if (streamId == null || streamId.equals(getString(CMIS.CONTENT_STREAM_ID)))
         {
            Node contentNode = node.getNode(JcrCMIS.JCR_CONTENT);
            Property fileContent = contentNode.getProperty(JcrCMIS.JCR_DATA);
            long length = fileContent.getLength();
            if (length == 0)
               return null; // No content, but node has empty stream.
            return new BaseContentStream(fileContent.getStream(), //
               length, //
               getName(), //
               contentNode.getProperty(JcrCMIS.JCR_MIMETYPE).getString());
         }
         else
         {
            Node rendition = null;
            try
            {
               rendition = node.getNode(streamId);
            }
            catch (PathNotFoundException pnfe)
            {
               String msg = "Stream with id " + streamId + " does not exist.";
               throw new InvalidArgumentException(msg);
            }
            Property renditionContent = rendition.getProperty(JcrCMIS.CMIS_RENDITION_STREAM);
            return new RenditionContentStream(//
               renditionContent.getStream(), //
               renditionContent.getLength(), //
               null, //
               rendition.getProperty(JcrCMIS.CMIS_RENDITION_MIME_TYPE).getString(), // 
               rendition.getProperty(JcrCMIS.CMIS_RENDITION_KIND).getString() //
            );
         }
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to get content stream. " + re.getMessage();
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public Calendar getDate(String name) throws RepositoryException
   {
      try
      {
         try
         {
            if (LOG.isDebugEnabled())
               LOG.debug("Get date " + name);
            //         return node.getProperty(name).getDate();
            return node.getProperty(getJcrPropertyName(name)).getDate();
         }
         catch (javax.jcr.PathNotFoundException pnfe)
         {
            return null;
         }
      }
      catch (javax.jcr.ValueFormatException vfe)
      {
         String msg = "Unable get property " + name + " as Calendar.";
         throw new InvalidArgumentException(msg, vfe);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to get property " + name;
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public Calendar[] getDates(String name) throws RepositoryException
   {
      try
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Get date[] " + name);
         //         Property jcrProp = node.getProperty(name);
         Property jcrProp = node.getProperty(getJcrPropertyName(name));
         if (((PropertyImpl)jcrProp).isMultiValued())
         {
            Value[] values = jcrProp.getValues();
            Calendar[] res = new Calendar[values.length];
            for (int i = 0; i < values.length; i++)
               res[i] = values[i].getDate();
            return res;
         }
         return new Calendar[]{jcrProp.getDate()};
      }
      catch (javax.jcr.PathNotFoundException pnfe)
      {
         return null;
      }
      catch (javax.jcr.ValueFormatException vfe)
      {
         String msg = "Unable get property " + name + " as Calendar[].";
         throw new InvalidArgumentException(msg, vfe);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to get property " + name;
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public BigDecimal getDecimal(String name) throws RepositoryException
   {
      try
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Get decimal " + name);
         //         return BigDecimal.valueOf(node.getProperty(name).getLong());
         return BigDecimal.valueOf(node.getProperty(getJcrPropertyName(name)).getLong());
      }
      catch (javax.jcr.PathNotFoundException pnfe)
      {
         return null;
      }
      catch (javax.jcr.ValueFormatException vfe)
      {
         String msg = "Unable get property " + name + " as Decimal.";
         throw new InvalidArgumentException(msg, vfe);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to get property " + name;
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public BigDecimal[] getDecimals(String name) throws RepositoryException
   {
      try
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Get decimal[] " + name);
         //         Property jcrProp = node.getProperty(name);
         Property jcrProp = node.getProperty(getJcrPropertyName(name));
         if (((PropertyImpl)jcrProp).isMultiValued())
         {
            Value[] values = jcrProp.getValues();
            BigDecimal[] res = new BigDecimal[values.length];
            for (int i = 0; i < values.length; i++)
               res[i] = BigDecimal.valueOf(values[i].getLong());
            return res;
         }
         return new BigDecimal[]{BigDecimal.valueOf(jcrProp.getLong())};
      }
      catch (javax.jcr.PathNotFoundException pnfe)
      {
         return null;
      }
      catch (javax.jcr.ValueFormatException vfe)
      {
         String msg = "Unable to get property " + name + " as Decimal[].";
         throw new InvalidArgumentException(msg, vfe);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to get property " + name;
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public String getHTML(String name) throws RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Get HTML " + name);
      return getString(name);
   }

   /**
    * {@inheritDoc}
    */
   public String[] getHTMLs(String name) throws RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Get HTML[] " + name);
      return getStrings(name);
   }

   /**
    * {@inheritDoc}
    */
   public BigInteger getInteger(String name) throws RepositoryException
   {
      try
      {
         try
         {
            if (LOG.isDebugEnabled())
               LOG.debug("Get integer " + name);
            return BigInteger.valueOf(node.getProperty(name).getLong());
         }
         catch (javax.jcr.PathNotFoundException pnfe)
         {
            /* TODO */
            if (CMIS.CONTENT_STREAM_LENGTH.equals(name))
               return BigInteger.valueOf(getNode().getNode(JcrCMIS.JCR_CONTENT) //
                  .getProperty(JcrCMIS.JCR_DATA).getLength());

            return null;
         }
      }
      catch (javax.jcr.ValueFormatException vfe)
      {
         String msg = "Unable to get property " + name + " as BigInteger.";
         throw new InvalidArgumentException(msg, vfe);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to get property " + name;
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public BigInteger[] getIntegers(String name) throws RepositoryException
   {
      try
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Get integer[] " + name);
         //         Property jcrProp = node.getProperty(name);
         Property jcrProp = node.getProperty(getJcrPropertyName(name));
         if (((PropertyImpl)jcrProp).isMultiValued())
         {
            Value[] values = jcrProp.getValues();
            BigInteger[] res = new BigInteger[values.length];
            for (int i = 0; i < values.length; i++)
               res[i] = BigInteger.valueOf(values[i].getLong());
            return res;
         }
         return new BigInteger[]{BigInteger.valueOf(jcrProp.getLong())};
      }
      catch (javax.jcr.PathNotFoundException pnfe)
      {
         return null;
      }
      catch (javax.jcr.ValueFormatException vfe)
      {
         String msg = "Unable to get property " + name + " as BigInteger[].";
         throw new InvalidArgumentException(msg, vfe);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to get property " + name;
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public String getName() throws RepositoryException
   {
      try
      {
         if (node.getDepth() == 0)
            return CMIS.ROOT_FOLDER_NAME;
         return node.getName();
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to get object name. " + re.getMessage();
         throw new RepositoryException(msg);
      }
   }

   /**
    * @return back end JCR node
    */
   public Node getNode()
   {
      return node;
   }

   /**
    * {@inheritDoc}
    */
   public String getObjectId()
   {
      return id;
   }

   /**
    * {@inheritDoc}
    */
   public List<Entry> getParents() throws RepositoryException
   {
      Entry parent = getParent(this);
      if (parent != null)
         return Collections.singletonList(parent);
      else
         return Collections.emptyList();
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisAccessControlEntryType> getPermissions() throws RepositoryException
   {
      try
      {
         if (!getType().isControllableACL())
         {
            String msg = "Type " + getType().getId() + " is not controlable by ACL.";
            throw new ConstraintException(msg);
         }
         if (!node.isNodeType(JcrCMIS.EXO_PRIVILEGABLE))
            return Collections.emptyList();

         Map<String, Set<String>> cache = new HashMap<String, Set<String>>();

         AccessControlList acl = ((ExtendedNode)node).getACL();
         List<AccessControlEntry> aces = acl.getPermissionEntries();
         for (AccessControlEntry ace : aces)
         {
            String identity = ace.getIdentity();
            Set<String> permissions = cache.get(identity);
            if (permissions == null)
            {
               permissions = new HashSet<String>();
               cache.put(identity, permissions);
            }
            permissions.add(ace.getPermission());
         }
         List<CmisAccessControlEntryType> list = new ArrayList<CmisAccessControlEntryType>();
         for (String key : cache.keySet())
         {
            CmisAccessControlEntryType entry = new CmisAccessControlEntryType();
            CmisAccessControlPrincipalType principal = new CmisAccessControlPrincipalType();
            principal.setPrincipalId(key);
            entry.setPrincipal(principal);
            Set<String> values = cache.get(key);
            // Represent JCR ACEs as CMIS ACEs. 
            if (values.size() == PermissionType.ALL.length)
               entry.getPermission().add(EnumBasicPermissions.CMIS_ALL.value());
            else if (values.contains(PermissionType.READ) && values.contains(PermissionType.ADD_NODE))
               entry.getPermission().add(EnumBasicPermissions.CMIS_READ.value());
            else if (values.contains(PermissionType.SET_PROPERTY) && values.contains(PermissionType.REMOVE))
               entry.getPermission().add(EnumBasicPermissions.CMIS_WRITE.value());
            list.add(entry);
         }
         return list;
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to get ACEs from object. " + re.getMessage();
         throw new RepositoryException(msg);
      }
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<Entry> getRelationships(EnumRelationshipDirection direct, boolean includeSubRelationshipTypes,
      CmisTypeDefinitionType relationshipType) throws RepositoryException
   {
      if (relationshipType == null)
         relationshipType = getTypeDefinition(EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value(), false);

      String relationshipNodeType = getNodeTypeName(relationshipType.getId());
      try
      {
         // Should not get one relationship twice.
         // It may happen if object has relation to it self.
         Set<Entry> cache = new HashSet<Entry>();
         if (direct == null)
            direct = EnumRelationshipDirection.EITHER;
         for (PropertyIterator iter = node.getReferences(); iter.hasNext();)
         {
            Property prop = iter.nextProperty();
            String propName = prop.getName();
            if ((direct == EnumRelationshipDirection.EITHER //
               && (propName.equals(CMIS.SOURCE_ID) || propName.equals(CMIS.TARGET_ID))) //
               || (direct == EnumRelationshipDirection.SOURCE && propName.equals(CMIS.SOURCE_ID)) //
               || (direct == EnumRelationshipDirection.TARGET && propName.equals(CMIS.TARGET_ID)))
            {
               Node relsNode = prop.getParent();
               if (relsNode.getPrimaryNodeType().getName().equals(relationshipNodeType) //
                  || (includeSubRelationshipTypes && relsNode.isNodeType(relationshipNodeType)))
               {
                  Entry relationship = new EntryImpl(relsNode);
                  if (LOG.isDebugEnabled())
                     LOG.debug("Add relationship " + relationship.getName());
                  cache.add(relationship);
               }
            }
         }
         return new BaseItemsIterator<Entry>(cache);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable get object's relationships. " + re.getMessage();
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public EnumBaseObjectTypeIds getScope()
   {
      return getType().getBaseId();
   }

   /**
    * {@inheritDoc}
    */
   public String getString(String name) throws RepositoryException
   {
      try
      {
         try
         {
            if (LOG.isDebugEnabled())
               LOG.debug("Get string " + name);
            //         return node.getProperty(name).getString();
            return node.getProperty(getJcrPropertyName(name)).getString();
         }
         catch (javax.jcr.PathNotFoundException pnfe)
         {
            // TODO
            if (CMIS.OBJECT_ID.equals(name))
            {
               return getObjectId();
            }
            else if (CMIS.NAME.equals(name))
            {
               return getName();
            }
            else if (CMIS.CONTENT_STREAM_FILE_NAME.equals(name))
            {
               return canGetContent() ? getName() : null;
            }
            else if (CMIS.BASE_TYPE_ID.equals(name))
            {
               return getType().getBaseId().value();
            }
            else if (CMIS.OBJECT_TYPE_ID.equals(name))
            {
               return getType().getId();
            }
            else if (CMIS.PATH.equals(name))
            {
               return node.getPath();
            }
            else if (CMIS.PARENT_ID.equals(name))
            {
               Entry parent = getParent(this);
               return parent != null ? parent.getObjectId() : null;
            }
            else if (CMIS.VERSION_LABEL.equals(name))
            {
               return null;
            }
            else if (CMIS.VERSION_SERIES_ID.equals(name))
            {
               if (getScope() == EnumBaseObjectTypeIds.CMIS_DOCUMENT)
                  return getVersionSeries().getVersionSeriesId();
               return null;
            }
            else if (CMIS.VERSION_SERIES_CHECKED_OUT_ID.equals(name))
            {
               Entry checkedout = getVersionSeries().getCheckedOut();
               if (checkedout != null)
                  return checkedout.getObjectId();
               return null;
            }
            else if (CMIS.VERSION_SERIES_CHECKED_OUT_BY.equals(name))
            {
               return null;
            }
            else if (CMIS.CHECKIN_COMMENT.equals(name))
            {
               return null;
            }

            return null;
         }
      }
      catch (javax.jcr.ValueFormatException vfe)
      {
         String msg = "Unable to get property " + name + " as String.";
         throw new InvalidArgumentException(msg, vfe);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to get property " + name;
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public String[] getStrings(String name) throws RepositoryException
   {
      try
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Get string[] " + name);
         //         Property jcrProp = node.getProperty(name);
         Property jcrProp = node.getProperty(getJcrPropertyName(name));
         if (((PropertyImpl)jcrProp).isMultiValued())
         {
            Value[] values = jcrProp.getValues();
            String[] res = new String[values.length];
            for (int i = 0; i < values.length; i++)
               res[i] = values[i].getString();
            return res;
         }
         
         return new String[]{jcrProp.getString()};
      }
      catch (javax.jcr.PathNotFoundException pnfe)
      {
         return null;
      }
      catch (javax.jcr.ValueFormatException vfe)
      {
         String msg = "Unable to get property " + name + " as String[].";
         throw new InvalidArgumentException(msg, vfe);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to get property " + name;
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public CmisTypeDefinitionType getType()
   {
      return type;
   }

   /**
    * {@inheritDoc}
    */
   public URI getURI(String name) throws RepositoryException
   {
      try
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Get URI " + name);
         //         return new URI(node.getProperty(name).getString());
         return new URI(node.getProperty(getJcrPropertyName(name)).getString());
      }
      catch (javax.jcr.PathNotFoundException pnfe)
      {
         return null;
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to get property " + name;
         throw new RepositoryException(msg, re);
      }
      catch (URISyntaxException e)
      {
         String msg = "Unable to get property " + name + ". Value of property can't be parsed as URI. ";
         throw new InvalidArgumentException(msg, e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public URI[] getURIs(String name) throws RepositoryException
   {
      try
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Get URI[] " + name);
         //         Property jcrProp = node.getProperty(name);
         Property jcrProp = node.getProperty(getJcrPropertyName(name));
         if (((PropertyImpl)jcrProp).isMultiValued())
         {
            Value[] values = jcrProp.getValues();
            URI[] res = new URI[values.length];
            for (int i = 0; i < values.length; i++)
               res[i] = new URI(values[i].getString());
            return res;
         }
         return new URI[]{new URI(jcrProp.getString())};
      }
      catch (javax.jcr.PathNotFoundException pnfe)
      {
         return new URI[0];
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to get property " + name;
         throw new RepositoryException(msg, re);
      }
      catch (URISyntaxException e)
      {
         String msg = "Unable to get property " + name + ". Value of property can't be parsed as URI.";
         throw new InvalidArgumentException(msg, e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public String getVersionLabel() throws RepositoryException
   {
      return getString(CMIS.VERSION_LABEL);
   }

   /**
    * {@inheritDoc}
    */
   public String getVersionSeriesId() throws RepositoryException
   {
      return getString(CMIS.VERSION_SERIES_ID);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      int hash = 7;
      hash = hash * 31 + getObjectId().hashCode();
      return hash;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isLatest() throws RepositoryException
   {
      return getBoolean(CMIS.IS_LATEST_VERSION);
   }

   /**
    * {@inheritDoc}
    */
   public boolean isLatestMajor() throws RepositoryException
   {
      return getBoolean(CMIS.IS_LATEST_MAJOR_VERSION);
   }

   /**
    * {@inheritDoc}
    */
   public boolean isMajor() throws RepositoryException
   {
      return getBoolean(CMIS.IS_MAJOR_VERSION);
   }

   /**
    * {@inheritDoc}
    */
   public boolean isVersionable() throws RepositoryException
   {
      return getScope() == EnumBaseObjectTypeIds.CMIS_DOCUMENT
         && ((CmisTypeDocumentDefinitionType)getType()).isVersionable();
   }

   /**
    * {@inheritDoc}
    */
   public Entry removeChild(String objectId) throws UnsupportedOperationException, InvalidArgumentException,
      RepositoryException
   {
      throw new UnsupportedOperationException("removeChild");
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisAccessControlEntryType> removePermissions(List<CmisAccessControlEntryType> remove)
      throws ConstraintException, RepositoryException
   {
      if (!getType().isControllableACL())
      {
         String msg = "Type " + getType().getId() + " is not controlable by ACL.";
         throw new ConstraintException(msg);
      }

      Map<String, String[]> aces = createPermissionMap(remove);
      preUpdate();
      try
      {
         for (String principal : aces.keySet())
         {
            for (String permission : aces.get(principal))
               ((ExtendedNode)node).removePermission(principal, permission);
         }
      }
      catch (AccessControlException ac)
      {
         String msg = "Operation not permitted.";
         throw new PermissionDeniedException(msg);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to remove ACEs from object. " + re.getMessage();
         throw new RepositoryException(msg);
      }
      return getPermissions();
   }

   /**
    * {@inheritDoc}
    */
   public void removePolicy(Entry policy) throws RepositoryException
   {
      if (!getType().isControllablePolicy())
      {
         String msg = "Type " + getType().getId() + " is not controlable by Policy.";
         throw new ConstraintException(msg);
      }
      try
      {
         preUpdate();
         node.setProperty(policy.getObjectId(), (Node)null);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unexpected error. Unable to remove policy. " + re.getMessage();
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void save() throws RepositoryException
   {
      try
      {
         // Update properties for nodes that have required mixin.
         if (node.isNodeType(JcrCMIS.CMIS_OBJECT))
         {
            Calendar date = Calendar.getInstance();
            //            if (node.isNew())
            //               node.setProperty(CMIS.CREATION_DATE, date);
            node.setProperty(CMIS.LAST_MODIFICATION_DATE, date);
            node.setProperty(CMIS.CHANGE_TOKEN, IdGenerator.generate());

            ConversationState cstate = ConversationState.getCurrent();
            if (cstate != null)
            {
               String userId = cstate.getIdentity().getUserId();
               //               if (node.isNew())
               //                  node.setProperty(CMIS.CREATED_BY, userId);
               node.setProperty(CMIS.LAST_MODIFIED_BY, userId);
            }
         }
         node.getParent().save();
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to save modifications. " + re.getMessage();
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    * @return 
    */
   public Entry setBoolean(String name, boolean value) throws RepositoryException
   {
      try
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Set boolean " + name + " value: " + value);
         preUpdate();
         node.setProperty(name, value);
         return this;
      }
      catch (javax.jcr.nodetype.ConstraintViolationException cve)
      {
         String msg = "Unable to set property " + name;
         throw new ConstraintException(msg, cve);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to set property " + name;
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    * @return 
    */
   public Entry setBooleans(String name, boolean[] value) throws RepositoryException
   {
      try
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Set boolean[] " + name + " value: " + value);
         Value[] jcrValue = new Value[value.length];
         for (int i = 0; i < value.length; i++)
            jcrValue[i] = new BooleanValue(value[i]);
         preUpdate();
         node.setProperty(name, jcrValue);
         return this;
      }
      catch (IOException ioe)
      {
         String msg = "Unable to set property " + name;
         throw new RuntimeException(msg, ioe);
      }
      catch (javax.jcr.nodetype.ConstraintViolationException cve)
      {
         String msg = "Unable to set property " + name;
         throw new ConstraintException(msg, cve);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to set property " + name;
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public Entry setContent(ContentStream content) throws IOException, StreamNotSupportedException, RepositoryException
   {
      if (getScope() != EnumBaseObjectTypeIds.CMIS_DOCUMENT
         || ((CmisTypeDocumentDefinitionType)getType()).getContentStreamAllowed() == EnumContentStreamAllowed.NOTALLOWED)
      {
         String msg = "Object does not support content stream.";
         throw new StreamNotSupportedException(msg);
      }
      try
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Set document content.");
         preUpdate();
         Node contentNode = node.getNode(JcrCMIS.JCR_CONTENT);
         if (content == null)
         {
            if (((CmisTypeDocumentDefinitionType)getType()).getContentStreamAllowed() == EnumContentStreamAllowed.REQUIRED)
            {
               String msg = "Content stream is required for object-type " + getType().getId();
               throw new ConstraintException(msg);
            }
            // null minds set content stream as empty, cause we can't remove mandatory items.
            contentNode.setProperty(JcrCMIS.JCR_MIMETYPE, "");
            contentNode.setProperty(JcrCMIS.JCR_DATA, new ByteArrayInputStream(new byte[0]));
            contentNode.setProperty(JcrCMIS.JCR_LAST_MODIFIED, Calendar.getInstance());
            // If work with existed nt:files (files were created not via CMIS services)
            // they may not have mixin that extends property definitions.
            if (node.isNodeType(JcrCMIS.CMIS_DOCUMENT))
            {
               node.setProperty(CMIS.CONTENT_STREAM_MIME_TYPE, (String)null);
               node.setProperty(CMIS.CONTENT_STREAM_LENGTH, 0);
            }
         }
         else
         {
            contentNode.setProperty(JcrCMIS.JCR_MIMETYPE, content.getMediaType());
            contentNode.setProperty(JcrCMIS.JCR_LAST_MODIFIED, Calendar.getInstance());
            Property contentProperty = contentNode.setProperty(JcrCMIS.JCR_DATA, content.getStream());
            // If work with existed nt:files (files were created not via CMIS services)
            // they may not have mixin that extends property definitions.
            if (node.isNodeType(JcrCMIS.CMIS_DOCUMENT))
            {
               node.setProperty(CMIS.CONTENT_STREAM_MIME_TYPE, content.getMediaType());
               // re-count content-length
               long length = contentProperty.getLength();
               node.setProperty(CMIS.CONTENT_STREAM_LENGTH, length);
            }
         }
         return this;
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to set content for document. " + re.getMessage();
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    * @return 
    */
   public Entry setDate(String name, Calendar value) throws RepositoryException
   {
      try
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Set date " + name + " value: " + value);
         preUpdate();
         node.setProperty(name, value);
         return this;
      }
      catch (javax.jcr.nodetype.ConstraintViolationException e)
      {
         String msg = "Unable to set property " + name;
         throw new ConstraintException(msg, e);
      }
      catch (javax.jcr.RepositoryException e)
      {
         String msg = "Unable to set property " + name;
         throw new RepositoryException(msg, e);
      }
   }

   /**
    * {@inheritDoc}
    * @return 
    */
   public Entry setDates(String name, Calendar[] value) throws RepositoryException
   {
      try
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Set date[] " + name + " value: " + value);
         Value[] jcrValue = new Value[value.length];
         for (int i = 0; i < value.length; i++)
            jcrValue[i] = new DateValue(value[i]);
         preUpdate();
         node.setProperty(name, jcrValue);
         return this;
      }
      catch (IOException ioe)
      {
         String msg = "Unable to set property " + name;
         throw new RuntimeException(msg, ioe);
      }
      catch (javax.jcr.nodetype.ConstraintViolationException cve)
      {
         String msg = "Unable to set property " + name;
         throw new ConstraintException(msg, cve);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to set property " + name;
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    * @return 
    */
   public Entry setDecimal(String name, BigDecimal value) throws RepositoryException
   {
      try
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Set decimal " + name + " value: " + value);
         preUpdate();
         node.setProperty(name, value.doubleValue());
         return this;
      }
      catch (javax.jcr.nodetype.ConstraintViolationException cve)
      {
         String msg = "Unable to set property " + name;
         throw new ConstraintException(msg, cve);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to set property " + name;
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    * @return 
    */
   public Entry setDecimals(String name, BigDecimal[] value) throws RepositoryException
   {
      try
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Set decimal[] " + name + " value: " + value);
         Value[] jcrValue = new Value[value.length];
         for (int i = 0; i < value.length; i++)
            jcrValue[i] = new DoubleValue(value[i].doubleValue());
         preUpdate();
         node.setProperty(name, jcrValue);
         return this;
      }
      catch (IOException ioe)
      {
         String msg = "Unable to set property " + name;
         throw new RuntimeException(msg, ioe);
      }
      catch (javax.jcr.nodetype.ConstraintViolationException cve)
      {
         String msg = "Unable to set property " + name;
         throw new ConstraintException(msg, cve);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to set property " + name;
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    * @return 
    */
   public Entry setHTML(String name, String value) throws RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Set HTML " + name + " value: " + value);
      preUpdate();
      setString(name, value);
      return this;
   }

   /**
    * {@inheritDoc}
    * @return 
    */
   public Entry setHTMLs(String name, String[] value) throws RepositoryException
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Set HTML " + name + " value: " + value);
      preUpdate();
      setStrings(name, value);
      return this;
   }

   /**
    * {@inheritDoc}
    * @return 
    */
   public Entry setInteger(String name, BigInteger value) throws RepositoryException
   {
      try
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Set integer.");
         preUpdate();
         node.setProperty(name, value.longValue());
         return this;
      }
      catch (javax.jcr.nodetype.ConstraintViolationException cve)
      {
         String msg = "Unable to set property " + name;
         throw new ConstraintException(msg, cve);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to set property " + name;
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    * @return 
    */
   public Entry setIntegers(String name, BigInteger[] value) throws RepositoryException
   {
      try
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Set integer[] " + name + " value: " + value);
         Value[] jcrValue = new Value[value.length];
         for (int i = 0; i < value.length; i++)
            jcrValue[i] = new LongValue(value[i].intValue());
         preUpdate();
         node.setProperty(name, jcrValue);
         return this;
      }
      catch (IOException ioe)
      {
         String msg = "Unable to set property " + name;
         throw new RuntimeException(msg, ioe);
      }
      catch (javax.jcr.nodetype.ConstraintViolationException cve)
      {
         String msg = "Unable to set property " + name;
         throw new ConstraintException(msg, cve);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to set property " + name;
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void setName(String name) throws NameConstraintViolationException, RepositoryException
   {
      if (name == null)
         throw new NullPointerException("Name may not be null.");
      preUpdate();
      try
      {
         String srcPath = node.getPath();
         String destPath = srcPath.substring(0, srcPath.lastIndexOf('/') + 1) + name;
         session.getWorkspace().move(srcPath, destPath);
         node = (Node)session.getItem(destPath);

         if (node.isNodeType(JcrCMIS.CMIS_OBJECT))
         {
            node.setProperty(CMIS.NAME, name);
            save();
         }
      }
      catch (ItemExistsException ie)
      {
         String msg = "Unable re-name entry. Entry with name " + name + " already exists.";
         throw new NameConstraintViolationException(msg);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable re-name entry. " + re.getMessage();
         throw new RepositoryException(msg);
      }
   }

   /**
    * {@inheritDoc}
    * @return 
    */
   public Entry setString(String name, String value) throws RepositoryException
   {
      try
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Set string " + name + " value: " + value);
         preUpdate();
         node.setProperty(name, value);
         return this;
      }
      catch (javax.jcr.nodetype.ConstraintViolationException cve)
      {
         String msg = "Unable to set property " + name;
         throw new ConstraintException(msg, cve);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to set property " + name;
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    * @return 
    */
   public Entry setStrings(String name, String[] value) throws RepositoryException
   {
      try
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Set string[] " + name + " value: " + value);
         Value[] jcrValue = new Value[value.length];
         for (int i = 0; i < value.length; i++)
            jcrValue[i] = new StringValue(value[i]);
         preUpdate();
         node.setProperty(name, jcrValue);
         return this;
      }
      catch (IOException ioe)
      {
         String msg = "Unable to set property " + name;
         throw new RuntimeException(msg, ioe);
      }
      catch (javax.jcr.nodetype.ConstraintViolationException cve)
      {
         String msg = "Unable to set property " + name;
         throw new ConstraintException(msg, cve);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to set property " + name;
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    * @return 
    */
   public Entry setURI(String name, URI value) throws RepositoryException
   {
      try
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Set URI " + name + " value: " + value);
         preUpdate();
         node.setProperty(name, value.toString());
         return this;
      }
      catch (javax.jcr.nodetype.ConstraintViolationException e)
      {
         String msg = "Unable to set property " + name;
         throw new ConstraintException(msg, e);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to set property " + name;
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * {@inheritDoc}
    * @return 
    */
   public Entry setURIs(String name, URI[] value) throws RepositoryException
   {
      try
      {
         if (LOG.isDebugEnabled())
            LOG.debug("Set URI[] " + name + " value: " + value);
         Value[] jcrValue = new Value[value.length];
         for (int i = 0; i < value.length; i++)
            jcrValue[i] = new StringValue(value[i].toString());
         preUpdate();
         node.setProperty(name, jcrValue);
         return this;
      }
      catch (IOException e)
      {
         String msg = "Unable to set property " + name;
         throw new RuntimeException(msg, e);
      }
      catch (javax.jcr.nodetype.ConstraintViolationException e)
      {
         String msg = "Unable to set property " + name;
         throw new ConstraintException(msg, e);
      }
      catch (javax.jcr.RepositoryException e)
      {
         String msg = "Unable to set property " + name;
         throw new RepositoryException(msg, e);
      }
   }

   /**
    * {@inheritDoc}
    */
   public String toString()
   {
      return getObjectId();
   }

   private void checkJcrCheckedoutState() throws RepositoryException
   {
      try
      {
         if (!node.isCheckedOut())
            node.checkout();
      }
      catch (javax.jcr.RepositoryException re)
      {
         throw new RepositoryException("Unexpected error. " + re.getMessage());
      }
   }

   /**
    * Create permission map which can be passed to JCR node.
    * 
    * @param source source ACL
    * @return permission map
    * @throws ConstraintException if at least permission is unknown
    */
   private Map<String, String[]> createPermissionMap(List<CmisAccessControlEntryType> source)
      throws ConstraintException
   {
      Map<String, Set<String>> cache = new HashMap<String, Set<String>>();
      for (CmisAccessControlEntryType ace : source)
      {
         String principal = ace.getPrincipal().getPrincipalId();
         Set<String> permissions = cache.get(principal);
         if (permissions == null)
         {
            permissions = new HashSet<String>();
            cache.put(principal, permissions);
         }
         for (String perm : ace.getPermission())
         {
            if (EnumBasicPermissions.CMIS_READ.value().equals(perm))
            {
               permissions.add(PermissionType.READ);
               // Child may be add without write permission for parent.
               permissions.add(PermissionType.ADD_NODE);
            }
            else if (EnumBasicPermissions.CMIS_WRITE.value().equals(perm))
            {
               permissions.add(PermissionType.SET_PROPERTY);
               permissions.add(PermissionType.REMOVE);
            }
            else if (EnumBasicPermissions.CMIS_ALL.value().equals(perm))
            {
               permissions.add(PermissionType.READ);
               permissions.add(PermissionType.ADD_NODE);
               permissions.add(PermissionType.SET_PROPERTY);
               permissions.add(PermissionType.REMOVE);
            }
            else
            {
               String msg = "Unknown permission " + perm;
               throw new ConstraintException(msg);
            }
         }
      }
      Map<String, String[]> aces = new HashMap<String, String[]>();
      for (Map.Entry<String, Set<String>> e : cache.entrySet())
         aces.put(e.getKey(), e.getValue().toArray(new String[e.getValue().size()]));
      return aces;
   }

   /**
    * Whether object represents independent object.
    * 
    * @return TRUE if object represents independent object such as Document,
    *            Folder or Policy and FALSE otherwise.
    */
   private boolean isIndependent()
   {
      return getScope() == EnumBaseObjectTypeIds.CMIS_DOCUMENT || getScope() == EnumBaseObjectTypeIds.CMIS_FOLDER
         || getScope() == EnumBaseObjectTypeIds.CMIS_POLICY;
   }

   /**
    * Collect references for provided node.
    * 
    * @param node the JCR node
    * @param refs the JCR references
    * @throws javax.jcr.RepositoryException if any JCR repository errors 
    */
   private void relationships(Node node, Map<String, Node> refs) throws javax.jcr.RepositoryException
   {
      for (PropertyIterator iter = node.getReferences(); iter.hasNext();)
      {
         Property prop = iter.nextProperty();
         if (prop.getName().equals(CMIS.SOURCE_ID) || prop.getName().equals(CMIS.TARGET_ID))
         {
            Node ref = prop.getParent();
            refs.put(((ExtendedNode)ref).getIdentifier(), ref);
         }
      }
      if (node.isNodeType(JcrCMIS.NT_CMIS_FOLDER))
      {
         for (NodeIterator iter = node.getNodes(); iter.hasNext();)
            relationships(iter.nextNode(), refs);
      }
   }

   /**
    * Create document object in this folder.
    * 
    * @param type type definition of document
    * @param name document object name
    * @param versioningState versioning state for created object
    * @return newly created document
    * @throws javax.jcr.RepositoryException if any JCR repository errors
    */
   protected Entry createDocument(CmisTypeDefinitionType type, String name, EnumVersioningState versioningState)
      throws javax.jcr.RepositoryException
   {
      Node childDocNode = node.addNode(name, getNodeTypeName(type.getId()));
      if (!childDocNode.isNodeType(JcrCMIS.CMIS_DOCUMENT)) // May be already inherited.
         childDocNode.addMixin(JcrCMIS.CMIS_DOCUMENT);
      // Initialize required structure of nodes.
      // From start document has not content. It may be added later.
      Node content = childDocNode.addNode(JcrCMIS.JCR_CONTENT, JcrCMIS.NT_RESOURCE);
      content.setProperty(JcrCMIS.JCR_MIMETYPE, "");
      content.setProperty(JcrCMIS.JCR_DATA, new ByteArrayInputStream(new byte[0]));
      content.setProperty(JcrCMIS.JCR_LAST_MODIFIED, Calendar.getInstance());
      //
      childDocNode.setProperty(CMIS.NAME, childDocNode.getName());
      childDocNode.setProperty(CMIS.BASE_TYPE_ID, type.getBaseId().value());
      childDocNode.setProperty(CMIS.OBJECT_TYPE_ID, type.getId());
      childDocNode.setProperty(CMIS.IS_IMMUTABLE, false);
      childDocNode.setProperty(JcrCMIS.CMIS_LATEST_VERSION, childDocNode);
      childDocNode.setProperty(CMIS.IS_LATEST_VERSION, true);
      childDocNode.setProperty(CMIS.IS_MAJOR_VERSION, versioningState == EnumVersioningState.MAJOR);
      childDocNode.setProperty(CMIS.VERSION_LABEL, versioningState == EnumVersioningState.CHECKEDOUT ? pwcLabel
         : latestLabel);
      if (versioningState == EnumVersioningState.CHECKEDOUT)
      {
         childDocNode.setProperty(CMIS.IS_VERSION_SERIES_CHECKED_OUT, true);
         childDocNode.setProperty(CMIS.VERSION_SERIES_CHECKED_OUT_ID, ((ExtendedNode)childDocNode).getIdentifier());
      }
      return new EntryImpl(childDocNode, type);
   }

   /**
    * Create folder object in this folder.
    * 
    * @param type type definition for child folder  
    * @param name child folder name
    * @return newly created child folder
    * @throws javax.jcr.RepositoryException if any JCR repository errors
    */
   protected Entry createFolder(CmisTypeDefinitionType type, String name) throws javax.jcr.RepositoryException
   {
      Node childFolderNode = node.addNode(name, getNodeTypeName(type.getId()));
      if (!childFolderNode.isNodeType(JcrCMIS.CMIS_FOLDER)) // May be already inherited.
         childFolderNode.addMixin(JcrCMIS.CMIS_FOLDER);
      childFolderNode.setProperty(CMIS.NAME, childFolderNode.getName());
      childFolderNode.setProperty(CMIS.OBJECT_ID, ((ExtendedNode)childFolderNode).getIdentifier());
      childFolderNode.setProperty(CMIS.OBJECT_TYPE_ID, type.getId());
      childFolderNode.setProperty(CMIS.BASE_TYPE_ID, type.getBaseId().value());
      return new EntryImpl(childFolderNode, type);
   }

   /**
    * Create policy in current folder.
    * 
    * @param type the type definition for policy
    * @param name the policy object name
    * @return newly created policy object
    * @throws javax.jcr.RepositoryException if any JCR repository errors
    * @throws RepositoryException if any other error
    */
   protected Entry createPolicy(CmisTypeDefinitionType type, String name) throws javax.jcr.RepositoryException,
      RepositoryException
   {
      Node childPolicyNode = node.addNode(name, getNodeTypeName(type.getId()));
      childPolicyNode.setProperty(CMIS.NAME, childPolicyNode.getName());
      childPolicyNode.setProperty(CMIS.OBJECT_ID, ((ExtendedNode)childPolicyNode).getIdentifier());
      childPolicyNode.setProperty(CMIS.OBJECT_TYPE_ID, type.getId());
      childPolicyNode.setProperty(CMIS.BASE_TYPE_ID, type.getBaseId().value());
      return new EntryImpl(childPolicyNode, type);
   }

   /**
    * Create relationship and use this object as source.
    *  
    * @param name the object name 
    * @param relationshipType type of relationship
    * @param target the target object of relationship
    * @return newly created relationship
    * @throws javax.jcr.RepositoryException if any JCR repository errors
    * @throws RepositoryException if any cmis error
    */
   protected Entry createRelationship(String name, CmisTypeDefinitionType relationshipType, Entry target)
      throws javax.jcr.RepositoryException, RepositoryException
   {
      Node relationships = (Node)session.getItem("/" + JcrCMIS.CMIS_RELATIONSHIPS);
      Node relHierarchy;
      String tContName = getObjectId();
      if (!relationships.hasNode(tContName))
         relHierarchy = relationships.addNode(tContName, JcrCMIS.CMIS_RELATIONSHIPS_HIERARCHY);
      else
         relHierarchy = relationships.getNode(tContName);
      Node relationshipNode = relHierarchy.addNode(name, getNodeTypeName(relationshipType.getId()));
      relationshipNode.setProperty(CMIS.SOURCE_ID, node);
      relationshipNode.setProperty(CMIS.TARGET_ID, ((EntryImpl)target).getNode());
      relationshipNode.setProperty(CMIS.NAME, relationshipNode.getName());
      relationshipNode.setProperty(CMIS.OBJECT_ID, ((ExtendedNode)relationshipNode).getIdentifier());
      relationshipNode.setProperty(CMIS.OBJECT_TYPE_ID, relationshipType.getId());
      relationshipNode.setProperty(CMIS.BASE_TYPE_ID, relationshipType.getBaseId().value());
      Entry relationship = new EntryImpl(relationshipNode, relationshipType);
      relationships.save();
      return relationship;
   }

   /**
    * {@inheritDoc}
    */
   protected NodeType getNodeType(String name) throws NoSuchNodeTypeException, javax.jcr.RepositoryException
   {
      return session.getWorkspace().getNodeTypeManager().getNodeType(name);
   }

   protected String getJcrPropertyName(String cmisName)
   {
      String jcrName = cmis2jcr.get(cmisName);
      if (jcrName != null)
         return jcrName;
      return cmisName;
   }

   /**
    * Get parent.
    * 
    * @param entry the CMISEntry
    * @return CMIS object that if front end to specified node
    * @throws RepositoryException if any repository
    *           error occurs
    */
   protected Entry getParent(EntryImpl entry) throws RepositoryException
   {
      if (!getType().isFileable())
      {
         String msg = "Object type " + getType().getId() + " is not fileable.";
         throw new UnsupportedOperationException(msg);
      }
      try
      {
         if (entry.getNode().getDepth() == 0)
            return null;
         Node parent = entry.getNode().getParent();
         return new EntryImpl(parent);
      }
      catch (ItemNotFoundException e)
      {
         // May not happen, n.getDepth() check is in not root folder.
         return null;
      }
      catch (AccessDeniedException ade)
      {
         String msg = "Unable to get parent. Operation not permitted.";
         throw new PermissionDeniedException(msg, ade);
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unable to get parent.";
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * Get version series that contains this object.
    * 
    * @return version series or null if object is not versionable
    * @throws RepositoryException if any CMIS repository error occurs
    */
   protected VersionSeries getVersionSeries() throws RepositoryException
   {
      if (isVersionable())
      {
         try
         {
            return new VersionSeriesImpl(node);
         }
         catch (javax.jcr.RepositoryException re)
         {
            String msg = "Unable get version series. " + re.getMessage();
            throw new RepositoryException(msg, re);
         }
      }
      return null;
   }

   /**
    * Initialize <code>EntryImpl</code> instance.
    * 
    * @param node the JCR node 
    * @throws javax.jcr.RepositoryException if any JCR repository errors
    */
   protected void init(Node node) throws javax.jcr.RepositoryException
   {
      init(node, getTypeDefinition(node.getPrimaryNodeType(), true));
   }

   /**
    * Initialize <code>EntryImpl</code> instance.
    * 
    * @param node the JCR node
    * @param type CMIS object type definition 
    * @throws javax.jcr.RepositoryException if any JCR repository errors
    */
   protected void init(Node node, CmisTypeDefinitionType type) throws javax.jcr.RepositoryException
   {
      this.node = node;
      this.type = type;
      this.id = ((ExtendedNode)node).getIdentifier();
   }

   protected void preDelete() throws RepositoryException
   {
      validateDelete();
      // TODO : check it. For now be sure node is in checked-out state.
      checkJcrCheckedoutState();
   }

   protected void preUpdate() throws RepositoryException
   {
      validateUpdate();
      // TODO : check it. For now be sure node is in checked-out state.
      checkJcrCheckedoutState();
   }

   /**
    * Remove all relationships. This method should be called before removing
    * object, to be sure there is no references to current node and all children.
    * 
    * @throws javax.jcr.RepositoryException if any JCR repository errors
    */
   protected void removeRelationships() throws javax.jcr.RepositoryException
   {
      if (node.isNodeType(JcrCMIS.NT_CMIS_DOCUMENT) || node.isNodeType(JcrCMIS.NT_CMIS_FOLDER)
         || node.isNodeType(JcrCMIS.CMIS_POLICY))
      {
         // Only independent object (Document, Folder or Policy) may have relationships.
         // Policy may not be removed if it is applied to object, for JCR it minds
         // has references.

         Map<String, Node> refs = new HashMap<String, Node>();
         relationships(node, refs);
         for (Iterator<Node> iter = refs.values().iterator(); iter.hasNext();)
         {
            Node ref = iter.next();
            Node parent = ref.getParent();
            ref.remove();
            if (!parent.hasNodes())
               parent.remove();
         }
      }
   }

   /**
    * Validate is current object may be deleted.
    *  
    * @throws ConstraintException if delete operation violate any constraints
    * @throws RepositoryException if any CMIS repository error occurs
    */
   // TODO : check other exceptions
   protected void validateDelete() throws ConstraintException, RepositoryException
   {
      try
      {
         if (getScope() == EnumBaseObjectTypeIds.CMIS_POLICY)
         {
            // Check is policy applied to at least one object.
            for (PropertyIterator iter = node.getReferences(); iter.hasNext();)
            {
               Node controllable = iter.nextProperty().getParent();
               if (controllable.isNodeType(JcrCMIS.NT_CMIS_DOCUMENT) //
                  || controllable.isNodeType(JcrCMIS.NT_CMIS_FOLDER) //
                  || controllable.isNodeType(JcrCMIS.CMIS_POLICY))
               {
                  String msg = "Unable to delete applied policy.";
                  throw new ConstraintException(msg);
               }
            }
         }
      }
      catch (javax.jcr.RepositoryException re)
      {
         String msg = "Unexpected error. " + re.getMessage();
         throw new RepositoryException(msg, re);
      }
   }

   /**
    * Validate is current object may be updated.
    *  
    * @throws ConstraintException if update operation violate any constraints
    * @throws RepositoryException if any CMIS repository error occurs
    */
   // TODO : check other exceptions
   protected void validateUpdate() throws ConstraintException, RepositoryException
   {
   }

}
