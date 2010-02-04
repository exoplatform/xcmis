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

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
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
import org.xcmis.spi.CMIS;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.PermissionDeniedException;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.object.ContentStream;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.object.ItemsIterator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class EntryImpl extends TypeManagerImpl implements Entry
{

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(EntryImpl.class);

   /** CMIS object type. */
   protected CmisTypeDefinitionType type;

   /** Object Id. */
   protected String objectId;

   /** The properties. */
   private Map<String, Object[]> properties;

   /** The storage. */
   private Storage storage;

   /**
    * Instantiates a new entry impl.
    * 
    * @param objectId the object id
    * @param storage the storage
    * 
    * @throws RepositoryException the repository exception
    */
   public EntryImpl(String objectId, Storage storage) throws RepositoryException
   {
      this.objectId = objectId;
      this.storage = storage;
      this.properties = storage.getObjects().get(objectId);
      this.type = getTypeDefinition((String)properties.get(CMIS.OBJECT_TYPE_ID)[0]);
   }

   /**
    * Instantiates a new entry impl.
    * 
    * @param that the that
    * 
    * @throws RepositoryException the repository exception
    */
   private EntryImpl(EntryImpl that) throws RepositoryException
   {
      this.objectId = RepositoryImpl.generateId();
      this.storage = that.storage;
      this.type = that.type;

      properties = new ConcurrentHashMap<String, Object[]>();
      for (Map.Entry<String, Object[]> e : that.getProperties().entrySet())
      {
         if (!e.getKey().equals(CMIS.OBJECT_ID) //
            && !e.getKey().equals(CMIS.CREATION_DATE) //
            && !e.getKey().equals(CMIS.CREATED_BY))
            properties.put(e.getKey(), e.getValue().clone());
      }
      properties.put(CMIS.OBJECT_ID, new String[]{this.objectId});
      properties.put(CMIS.CREATION_DATE, new Calendar[]{Calendar.getInstance()});
      properties.put(CMIS.LAST_MODIFICATION_DATE, new Calendar[]{Calendar.getInstance()});
      ConversationState cstate = ConversationState.getCurrent();
      String userId = null;
      if (cstate != null)
         userId = cstate.getIdentity().getUserId();
      if (userId != null)
      {
         properties.put(CMIS.CREATED_BY, new String[]{userId});
         properties.put(CMIS.LAST_MODIFIED_BY, new String[]{userId});
      }
      storage.getObjects().put(this.objectId, properties);

      ContentStream originalContent = that.getContent(null);
      if (originalContent != null)
      {
         try
         {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = originalContent.getStream();
            byte[] buf = new byte[1024];
            int r = -1;
            while ((r = in.read(buf)) != -1)
               out.write(buf, 0, r);
            setContent(new SimpleContentStream( //
               out.toByteArray(), //
               originalContent.getFileName(), //
               originalContent.getMediaType()) //
            );
         }
         catch (IOException ioe)
         {
            throw new RepositoryException("Unable copy document's content", ioe);
         }
         catch (StreamNotSupportedException se)
         {
            throw new RepositoryException("Unable copy document's content", se);
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public void addChild(Entry child)
   {
      if (LOG.isDebugEnabled())
         LOG.debug("Add as child the object '" + child.getObjectId() + "'  to the object " + objectId);

      storage.getParents().get(child.getObjectId()).add(objectId);
      storage.getChildren().get(objectId).add(child.getObjectId());
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
      checkACL(aces);
      Map<String, Set<String>> acl = storage.getACLs().get(objectId);
      if (acl == null)
      {
         acl = new ConcurrentHashMap<String, Set<String>>();
         storage.getACLs().put(objectId, acl);
      }
      for (CmisAccessControlEntryType ace : aces)
      {
         String principal = ace.getPrincipal().getPrincipalId();
         Set<String> permissions = acl.get(principal);
         if (permissions == null)
         {
            permissions = new CopyOnWriteArraySet<String>();
            acl.put(principal, permissions);
         }
         if (ace.getPermission().contains(EnumBasicPermissions.CMIS_ALL.value()))
         {
            permissions.clear(); // Get all permissions, remove previous permissions, don't need it any more.
            permissions.add(EnumBasicPermissions.CMIS_ALL.value());
         }
         else
         {
            permissions.addAll(ace.getPermission());
         }
      }
      return getPermissions();
   }

   /**
    * {@inheritDoc}
    *     
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

      return createRelationship(name, relationshipType, target);
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
      storage.getPolicies().get(objectId).add(policy.getObjectId());
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
      if (getScope() == EnumBaseObjectTypeIds.CMIS_POLICY)
      {
         for (Set<String> applied : storage.getPolicies().values())
         {
            if (applied.contains(objectId))
               return false;
         }
      }
      return true;
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
      return true;
   }

   /**
    * Copy.
    * 
    * @return the entry impl
    * @throws RepositoryException the repository exception
    */
   public EntryImpl copy() throws RepositoryException
   {
      return new EntryImpl(this);
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
      if (type.getBaseId() == EnumBaseObjectTypeIds.CMIS_DOCUMENT)
         return createDocument(type, name, versioningState == null ? EnumVersioningState.MAJOR : versioningState);
      else if (type.getBaseId() == EnumBaseObjectTypeIds.CMIS_FOLDER)
         return createFolder(type, name);
      else if (type.getBaseId() == EnumBaseObjectTypeIds.CMIS_POLICY)
         return createPolicy(type, name);
      else
      {
         String msg = "Unsuported object type " + type.getDisplayName();
         throw new InvalidArgumentException(msg);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void delete() throws RepositoryException
   {
      if (!canDelete())
      {
         String msg = "Object may not be deleted.";
         throw new ConstraintException(msg);
      }
      if (type.isFileable())
      {
         for (Iterator<String> iterator = storage.getParents().get(objectId).iterator(); iterator.hasNext();)
            storage.getChildren().get(iterator.next()).remove(objectId);
      }
      if (getScope() == EnumBaseObjectTypeIds.CMIS_DOCUMENT || getScope() == EnumBaseObjectTypeIds.CMIS_RELATIONSHIP
         || getScope() == EnumBaseObjectTypeIds.CMIS_POLICY)
      {
         storage.getObjects().remove(objectId);
         if (getScope() == EnumBaseObjectTypeIds.CMIS_RELATIONSHIP)
            storage.getContents().remove(objectId);
      }
      else
      {
         delete(objectId);
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
      return objectId.equals(((EntryImpl)other).objectId);
   }

   /**
    * {@inheritDoc}
    */
   public List<Entry> getAppliedPolicies() throws RepositoryException
   {
      List<Entry> list = new ArrayList<Entry>();
      Set<String> p = storage.getPolicies().get(objectId);
      if (p == null)
         return list;
      for (String policyId : p)
         list.add(new EntryImpl(policyId, storage));
      return list;
   }

   /**
    * {@inheritDoc}
    */
   public boolean getBoolean(String name) throws RepositoryException
   {
      Object[] p = properties.get(name);
      if (p == null || p.length == 0)
         return false;
      if (Boolean.class == p[0].getClass() || "boolean".equals(p[0].getClass().getName()))
         return (Boolean)p[0];
      throw new InvalidArgumentException("Unable get property " + name + " as boolean value.");
   }

   /**
    * {@inheritDoc}
    */
   public boolean[] getBooleans(String name) throws RepositoryException
   {
      Object[] p = properties.get(name);
      if (p == null || p.length == 0)
         return null;
      if (Boolean.class == p[0].getClass() || "boolean".equals(p[0].getClass().getName()))
      {
         boolean[] res = new boolean[p.length];
         for (int i = 0; i < p.length; i++)
            res[i] = (Boolean)p[i];
         return res;
      }
      throw new InvalidArgumentException("Unable get property " + name + " as boolean[] value.");
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
      Set<String> c = storage.getChildren().get(objectId);
      List<Entry> list = new ArrayList<Entry>();
      if (c != null)
      {
         for (String chId : c)
            list.add(new EntryImpl(chId, storage));
      }
      return new SimpleItemsIterator<Entry>(list);
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
      // TODO : renditions
      //      if (streamId == null || streamId.equals(getObjectId()))
      //         return storage.getContents().get(objectId);
      //      return null;

      return storage.getContents().get(objectId);
   }

   /**
    * {@inheritDoc}
    */
   public Calendar getDate(String name) throws RepositoryException
   {
      Object[] p = properties.get(name);
      if (p == null || p.length == 0)
         return null;
      if (Calendar.class.isAssignableFrom(p[0].getClass()))
         return (Calendar)p[0];
      throw new InvalidArgumentException("Unable get property " + name + " as date value.");
   }

   /**
    * {@inheritDoc}
    */
   public Calendar[] getDates(String name) throws RepositoryException
   {
      Object[] p = properties.get(name);
      if (p == null || p.length == 0)
         return null;
      if (Calendar.class.isAssignableFrom(p[0].getClass()))
         return (Calendar[])p;
      throw new InvalidArgumentException("Unable get property " + name + " as date[] value.");
   }

   /**
    * {@inheritDoc}
    */
   public BigDecimal getDecimal(String name) throws RepositoryException
   {
      Object[] p = properties.get(name);
      if (p == null || p.length == 0)
         return null;
      if (BigDecimal.class.isAssignableFrom(p[0].getClass()))
         return (BigDecimal)p[0];
      throw new InvalidArgumentException("Unable get property " + name + " as decimal value.");
   }

   /**
    * {@inheritDoc}
    */
   public BigDecimal[] getDecimals(String name) throws RepositoryException
   {
      Object[] p = properties.get(name);
      if (p == null || p.length == 0)
         return null;
      if (BigDecimal.class.isAssignableFrom(p[0].getClass()))
         return (BigDecimal[])p;
      throw new InvalidArgumentException("Unable get property " + name + " as decimal[] value.");
   }

   /**
    * {@inheritDoc}
    */
   public String getHTML(String name) throws RepositoryException
   {
      return getString(name);
   }

   /**
    * {@inheritDoc}
    */
   public String[] getHTMLs(String name) throws RepositoryException
   {
      return getStrings(name);
   }

   /**
    * {@inheritDoc}
    */
   public BigInteger getInteger(String name) throws RepositoryException
   {
      Object[] p = properties.get(name);
      if (p == null || p.length == 0)
         return null;
      if (BigInteger.class.isAssignableFrom(p[0].getClass()))
         return (BigInteger)p[0];
      throw new InvalidArgumentException("Unable get property " + name + " as integer value.");
   }

   /**
    * {@inheritDoc}
    */
   public BigInteger[] getIntegers(String name) throws RepositoryException
   {
      Object[] p = properties.get(name);
      if (p == null || p.length == 0)
         return null;
      if (BigInteger.class.isAssignableFrom(p[0].getClass()))
         return (BigInteger[])p;
      throw new InvalidArgumentException("Unable get property " + name + " as integer[] value.");
   }

   /**
    * {@inheritDoc}
    */
   public String getName() throws RepositoryException
   {
      return getString(CMIS.NAME);
   }

   /**
    * {@inheritDoc}
    */
   public String getObjectId()
   {
      return objectId;
   }

   /**
    * {@inheritDoc}
    */
   public List<Entry> getParents() throws RepositoryException
   {
      if (!getType().isFileable())
      {
         String msg = "Object type " + getType().getId() + " is not fileable.";
         throw new UnsupportedOperationException(msg);
      }
      if (objectId == RepositoryImpl.ROOT_FOLDER_ID)
         return Collections.emptyList();

      List<Entry> p = new ArrayList<Entry>();
      for (String pId : storage.getParents().get(objectId))
         p.add(new EntryImpl(pId, storage));
      return p;
   }

   /**
    * {@inheritDoc}
    */
   public List<CmisAccessControlEntryType> getPermissions() throws RepositoryException
   {
      if (!getType().isControllableACL())
      {
         String msg = "Type " + getType().getId() + " is not controlable by ACL.";
         throw new ConstraintException(msg);
      }
      List<CmisAccessControlEntryType> list = new ArrayList<CmisAccessControlEntryType>();
      Map<String, Set<String>> acl = storage.getACLs().get(objectId);
      if (acl != null)
      {
         for (Map.Entry<String, Set<String>> e : acl.entrySet())
         {
            CmisAccessControlEntryType ace = new CmisAccessControlEntryType();
            CmisAccessControlPrincipalType principal = new CmisAccessControlPrincipalType();
            principal.setPrincipalId(e.getKey());
            ace.setPrincipal(principal);
            ace.getPermission().addAll(e.getValue());
            list.add(ace);
         }
      }
      return list;
   }

   /**
    * Gets the properties.
    * 
    * @return the properties
    */
   public Map<String, Object[]> getProperties()
   {
      return properties;
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<Entry> getRelationships(EnumRelationshipDirection direct, boolean includeSubRelationshipTypes,
      CmisTypeDefinitionType relationshipType) throws RepositoryException
   {
      List<Entry> list = new ArrayList<Entry>();
      for (String id : storage.getObjects().keySet())
      {
         Entry relationship = new EntryImpl(id, storage);
         if (relationship.getScope() == EnumBaseObjectTypeIds.CMIS_RELATIONSHIP)
         {
            if ((direct == EnumRelationshipDirection.SOURCE || direct == EnumRelationshipDirection.EITHER)
               && objectId.equals(relationship.getString(CMIS.SOURCE_ID))
               || (direct == EnumRelationshipDirection.TARGET || direct == EnumRelationshipDirection.EITHER)
               && objectId.equals(relationship.getString(CMIS.TARGET_ID)))
               list.add(relationship);
         }
      }
      return new SimpleItemsIterator<Entry>(list);
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
      Object[] p = properties.get(name);
      if (p == null || p.length == 0)
         return null;
      if (String.class == p[0].getClass())
         return (String)p[0];
      throw new InvalidArgumentException("Unable get property " + name + " as string value.");
   }

   /**
    * {@inheritDoc}
    */
   public String[] getStrings(String name) throws RepositoryException
   {
      Object[] p = properties.get(name);
      if (p == null || p.length == 0)
         return null;
      if (String.class == p[0].getClass())
         return (String[])p;
      throw new InvalidArgumentException("Unable get property " + name + " as string[] value.");
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
      Object[] p = properties.get(name);
      if (p == null || p.length == 0)
         return null;
      if (URI.class == p[0].getClass())
         return (URI)p[0];
      throw new InvalidArgumentException("Unable get property " + name + " as URI value.");
   }

   /**
    * {@inheritDoc}
    */
   public URI[] getURIs(String name) throws RepositoryException
   {
      Object[] p = properties.get(name);
      if (p == null || p.length == 0)
         return null;
      if (URI.class == p[0].getClass())
         return (URI[])p;
      throw new InvalidArgumentException("Unable get property " + name + " as URI value.");
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
      int hash = 8;
      hash = hash * 31 + getObjectId().hashCode();
      return hash;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isLatest() throws RepositoryException
   {
      if (getScope() == EnumBaseObjectTypeIds.CMIS_DOCUMENT)
      {
         return getBoolean(CMIS.IS_LATEST_VERSION);
      }
      return true;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isLatestMajor() throws RepositoryException
   {
      if (getScope() == EnumBaseObjectTypeIds.CMIS_DOCUMENT)
      {
         return getBoolean(CMIS.IS_LATEST_VERSION);
      }
      return false;
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
      if (getScope() != EnumBaseObjectTypeIds.CMIS_FOLDER)
         throw new UnsupportedOperationException("Object with type " + getType().getId() + " may not have children.");
      storage.getChildren().get(this.objectId).remove(objectId);
      storage.getParents().get(objectId).remove(this.objectId);
      return new EntryImpl(objectId, storage);
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
      checkACL(remove);
      Map<String, Set<String>> acl = storage.getACLs().get(objectId);
      if (acl != null)
      {
         for (CmisAccessControlEntryType ace : remove)
         {
            String principal = ace.getPrincipal().getPrincipalId();
            Set<String> permissions = acl.get(principal);
            if (permissions != null)
            {
               if (ace.getPermission().contains(EnumBasicPermissions.CMIS_ALL.value()))
               {
                  acl.remove(principal);
               }
               else
               {
                  if (permissions.contains(EnumBasicPermissions.CMIS_ALL.value()))
                  {
                     permissions.clear();
                     if (ace.getPermission().contains(EnumBasicPermissions.CMIS_READ.value()))
                        permissions.add(EnumBasicPermissions.CMIS_WRITE.value());
                     else if (ace.getPermission().contains(EnumBasicPermissions.CMIS_WRITE.value()))
                        permissions.add(EnumBasicPermissions.CMIS_READ.value());
                  }
                  permissions.removeAll(ace.getPermission());
                  if (permissions.isEmpty())
                     acl.remove(principal);
               }
            }
         }
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
      if (storage.getPolicies().get(objectId) != null)
         storage.getPolicies().get(objectId).remove(policy.getObjectId());
   }

   /**
    * {@inheritDoc}
    */
   public void save() throws RepositoryException
   {
      ConversationState cstate = ConversationState.getCurrent();
      String userId = null;
      if (cstate != null)
         userId = cstate.getIdentity().getUserId();

      if (userId != null)
      {
         String[] lm = (String[])properties.get(CMIS.LAST_MODIFIED_BY);
         if (lm == null)
            lm = new String[1];
         lm[0] = userId;
      }
      Calendar date = Calendar.getInstance();
      properties.put(CMIS.LAST_MODIFICATION_DATE, new Object[]{date});
      properties.put(CMIS.CHANGE_TOKEN, new String[]{Long.toString(date.getTimeInMillis())});
   }

   /**
    * {@inheritDoc}
    * @return 
    */
   public Entry setBoolean(String name, boolean value) throws RepositoryException
   {
      properties.put(name, new Boolean[]{value});
      return this;
   }

   /**
    * {@inheritDoc}
    * @return 
    */
   public Entry setBooleans(String name, boolean[] value) throws RepositoryException
   {
      if (value == null)
      {
         properties.remove(name);
      }
      else
      {
         Boolean[] b = new Boolean[value.length];
         for (int i = 0; i < value.length; i++)
            b[i] = Boolean.valueOf(value[i]);
         properties.put(name, b);
      }
      return this;
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
      if (content == null)
      {
         storage.getContents().remove(objectId);
         setInteger(CMIS.CONTENT_STREAM_LENGTH, null);
         setString(CMIS.CONTENT_STREAM_ID, null);
         setString(CMIS.CONTENT_STREAM_MIME_TYPE, null);
      }
      else
      {
         storage.getContents().put(objectId, content);
         setInteger(CMIS.CONTENT_STREAM_LENGTH, BigInteger.valueOf(content.length()));
         setString(CMIS.CONTENT_STREAM_ID, getObjectId());
         setString(CMIS.CONTENT_STREAM_MIME_TYPE, content.getMediaType());
      }
      return this;
   }

   /**
    * {@inheritDoc}
    * @return 
    */
   public Entry setDate(String name, Calendar value) throws RepositoryException
   {
      if (value == null)
         properties.remove(name);
      else
         properties.put(name, new Calendar[]{value});
      return this;
   }

   /**
    * {@inheritDoc}
    */
   public Entry setDates(String name, Calendar[] value) throws RepositoryException
   {
      if (value == null)
      {
         properties.remove(name);
      }
      else
      {
         Calendar[] c = new Calendar[value.length];
         System.arraycopy(value, 0, c, 0, c.length);
         properties.put(name, c);
      }
      return this;
   }

   /**
    * {@inheritDoc}
    */
   public Entry setDecimal(String name, BigDecimal value) throws RepositoryException
   {
      if (value == null)
         properties.remove(name);
      else
         properties.put(name, new BigDecimal[]{value});
      return this;
   }

   /**
    * {@inheritDoc}
    */
   public Entry setDecimals(String name, BigDecimal[] value) throws RepositoryException
   {
      if (value == null)
      {
         properties.remove(name);
      }
      else
      {
         BigDecimal[] d = new BigDecimal[value.length];
         System.arraycopy(value, 0, d, 0, d.length);
         properties.put(name, d);
      }
      return this;
   }

   /**
    * {@inheritDoc}
    * @return 
    */
   public Entry setHTML(String name, String value) throws RepositoryException
   {
      setString(name, value);
      return this;
   }

   /**
    * {@inheritDoc}
    * @return 
    */
   public Entry setHTMLs(String name, String[] value) throws RepositoryException
   {
      setStrings(name, value);
      return this;
   }

   /**
    * {@inheritDoc}
    * @return 
    */
   public Entry setInteger(String name, BigInteger value) throws RepositoryException
   {
      if (value == null)
         properties.remove(name);
      else
         properties.put(name, new Object[]{value});
      return this;
   }

   /**
    * {@inheritDoc}
    * @return 
    */
   public Entry setIntegers(String name, BigInteger[] value) throws RepositoryException
   {
      if (value == null)
      {
         properties.remove(name);
      }
      else
      {
         BigInteger[] i = new BigInteger[value.length];
         System.arraycopy(value, 0, i, 0, i.length);
         properties.put(name, i);
      }
      return this;
   }

   public void setName(String name) throws NameConstraintViolationException, RepositoryException
   {
      setString(CMIS.NAME, name);
   }

   /**
    * {@inheritDoc}
    * @return 
    */
   public Entry setString(String name, String value) throws RepositoryException
   {
      if (value == null)
         properties.remove(name);
      else
         properties.put(name, new Object[]{value});
      return this;
   }

   /**
    * {@inheritDoc}
    * @return 
    */
   public Entry setStrings(String name, String[] value) throws RepositoryException
   {
      if (value == null)
      {
         properties.remove(name);
      }
      else
      {
         String[] s = new String[value.length];
         System.arraycopy(value, 0, s, 0, s.length);
         properties.put(name, s);
      }
      return this;
   }

   /**
    * {@inheritDoc}
    * @return 
    */
   public Entry setURI(String name, URI value) throws RepositoryException
   {
      if (value == null)
         properties.remove(name);
      else
         properties.put(name, new Object[]{value});
      return this;
   }

   /**
    * {@inheritDoc}
    * @return 
    */
   public Entry setURIs(String name, URI[] value) throws RepositoryException
   {
      if (value == null)
      {
         properties.remove(name);
      }
      else
      {
         URI[] u = new URI[value.length];
         System.arraycopy(value, 0, u, 0, u.length);
         properties.put(name, u);
      }
      return this;
   }

   /**
    * {@inheritDoc}
    */
   public String toString()
   {
      return getObjectId();
   }

   /**
    * Check acl.
    * 
    * @param acl the acl
    */
   private void checkACL(List<CmisAccessControlEntryType> acl)
   {
      for (CmisAccessControlEntryType ace : acl)
      {
         if (ace.getPrincipal() == null || ace.getPrincipal().getPrincipalId() == null)
            throw new InvalidArgumentException("Principal Id can't be null.");
         for (String permission : ace.getPermission())
         {
            if (permission == null //
               || (!permission.equals(EnumBasicPermissions.CMIS_ALL.value())
                  && !permission.equals(EnumBasicPermissions.CMIS_READ.value()) && !permission
                  .equals(EnumBasicPermissions.CMIS_WRITE.value())))
               throw new ConstraintException("Unknown permission " + permission);
         }
      }
   }

   // TODO : Use this method for checking user's permissions.
   /**
    * Check permissions.
    * 
    * @param permission the permission
    * @throws RepositoryException the repository exception
    */
   private void checkPermissions(String permission) throws RepositoryException
   {
      List<CmisAccessControlEntryType> acl = getPermissions();
      if (acl.isEmpty())
         return; // Nothing set that all operations for any principals allowed.

      ConversationState cstate = ConversationState.getCurrent();
      String principal = null;
      if (cstate != null)
         principal = cstate.getIdentity().getUserId();
      boolean valid = false;
      for (CmisAccessControlEntryType ace : acl)
      {
         if (principal != null)
            valid = checkPermissions(principal, permission, ace);
         else
            valid = checkPermissions(RepositoryImpl.ANONYMOUS, permission, ace);
         if (!valid)
            valid = checkPermissions(RepositoryImpl.ANY, permission, ace);
         if (valid)
            break;
      }
      if (!valid)
         throw new PermissionDeniedException("Operation not permitted.");
   }

   /**
    * Check permissions.
    * 
    * @param principal the principal
    * @param permission the permission
    * @param ace the ace
    * @return true, if ace contains given permission
    */
   private boolean checkPermissions(String principal, String permission, CmisAccessControlEntryType ace)
   {
      return principal.equals(ace.getPrincipal().getPrincipalId())
         && (ace.getPermission().contains(permission) || ace.getPermission().contains(
            EnumBasicPermissions.CMIS_ALL.value()));
   }

   /**
    * Delete object an recursively all its descendants.
    * @param id object id
    */
   private void delete(String id)
   {
      Set<String> children = storage.getChildren().remove(id);
      if (children != null)
      {
         for (String child : children)
            delete(child);
      }
      storage.getObjects().remove(id);
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
    * Create document object in this folder.
    * 
    * @param type type definition of document
    * @param name document object name
    * @param versioningState versioning state for created object
    * @return newly created document
    * @throws RepositoryException if any repository exception occurs
    */
   protected Entry createDocument(CmisTypeDefinitionType type, String name, EnumVersioningState versioningState)
      throws RepositoryException
   {

      String childId = RepositoryImpl.generateId();
      String vsID = RepositoryImpl.generateId();

      Map<String, Object[]> child = new ConcurrentHashMap<String, Object[]>();
      child.put(CMIS.OBJECT_ID, new String[]{childId});
      child.put(CMIS.NAME, new String[]{name});
      child.put(CMIS.OBJECT_TYPE_ID, new String[]{type.getId()});
      child.put(CMIS.BASE_TYPE_ID, new String[]{type.getBaseId().value()});
      Calendar date = Calendar.getInstance();
      ConversationState cstate = ConversationState.getCurrent();
      String userId = null;
      if (cstate != null)
         userId = cstate.getIdentity().getUserId();
      if (userId != null)
         child.put(CMIS.CREATED_BY, new String[]{userId});
      child.put(CMIS.CREATION_DATE, new Calendar[]{date});
      child.put(CMIS.IS_IMMUTABLE, new Boolean[]{false});
      child.put(CMIS.IS_LATEST_VERSION, new Boolean[]{versioningState != EnumVersioningState.CHECKEDOUT});
      child.put(CMIS.IS_MAJOR_VERSION, new Boolean[]{versioningState == EnumVersioningState.MAJOR});
      child.put(CMIS.IS_LATEST_MAJOR_VERSION, new Boolean[]{versioningState == EnumVersioningState.MAJOR});
      child
         .put(CMIS.VERSION_LABEL, new String[]{versioningState == EnumVersioningState.CHECKEDOUT ? "pwc" : "current"});
      child.put(CMIS.IS_VERSION_SERIES_CHECKED_OUT, new Boolean[]{versioningState == EnumVersioningState.CHECKEDOUT});
      if (versioningState == EnumVersioningState.CHECKEDOUT)
         child.put(CMIS.VERSION_SERIES_CHECKED_OUT_ID, new String[]{childId});
      if (versioningState == EnumVersioningState.CHECKEDOUT && userId != null)
         child.put(CMIS.VERSION_SERIES_CHECKED_OUT_BY, new String[]{userId});
      child.put(CMIS.VERSION_SERIES_ID, new String[]{vsID});
      child.put(CMIS.CONTENT_STREAM_LENGTH, new BigInteger[]{BigInteger.ZERO});
      child.put(CMIS.CONTENT_STREAM_MIME_TYPE, new String[]{""});
      child.put(CMIS.CONTENT_STREAM_FILE_NAME, new String[]{name});
      child.put(CMIS.CONTENT_STREAM_ID, new String[]{childId});

      storage.getObjects().put(childId, child);

      storage.getChildren().get(objectId).add(childId);

      Set<String> parentsSet = new CopyOnWriteArraySet<String>();
      parentsSet.add(objectId);
      storage.getParents().put(childId, parentsSet);

      storage.getPolicies().put(childId, new CopyOnWriteArraySet<String>());

      Set<String> v = new CopyOnWriteArraySet<String>();
      v.add(childId);
      storage.getVersions().put(vsID, v);

      return new EntryImpl(childId, storage);
   }

   /**
    * Create folder object in this folder.
    * 
    * @param type type definition for child folder  
    * @param name child folder name
    * @return newly created child folder
    * @throws RepositoryException if any repository exception occurs
    */
   protected Entry createFolder(CmisTypeDefinitionType type, String name) throws RepositoryException
   {
      String childId = RepositoryImpl.generateId();

      Map<String, Object[]> child = new ConcurrentHashMap<String, Object[]>();
      child.put(CMIS.NAME, new String[]{name});
      child.put(CMIS.OBJECT_ID, new String[]{childId});
      child.put(CMIS.OBJECT_TYPE_ID, new String[]{type.getId()});
      child.put(CMIS.BASE_TYPE_ID, new String[]{type.getBaseId().value()});
      child.put(CMIS.PARENT_ID, new String[]{getObjectId()});
      Calendar date = Calendar.getInstance();
      ConversationState cstate = ConversationState.getCurrent();
      String userId = null;
      if (cstate != null)
         userId = cstate.getIdentity().getUserId();
      if (userId != null)
         child.put(CMIS.CREATED_BY, new String[]{userId});
      child.put(CMIS.CREATION_DATE, new Calendar[]{date});

      storage.getObjects().put(childId, child);

      storage.getChildren().get(objectId).add(childId);

      Set<String> parentsSet = new CopyOnWriteArraySet<String>();
      parentsSet.add(objectId);
      storage.getParents().put(childId, parentsSet);

      storage.getChildren().put(childId, new CopyOnWriteArraySet<String>());

      storage.getPolicies().put(childId, new CopyOnWriteArraySet<String>());

      return new EntryImpl(childId, storage);
   }

   /**
    * Create policy in current folder.
    * 
    * @param type the type definition for policy
    * @param name the policy object name
    * @return newly created policy object
    * @throws RepositoryException if any other error
    */
   protected Entry createPolicy(CmisTypeDefinitionType type, String name) throws RepositoryException
   {
      String childId = RepositoryImpl.generateId();

      Map<String, Object[]> child = new ConcurrentHashMap<String, Object[]>();
      child.put(CMIS.NAME, new String[]{name});
      child.put(CMIS.OBJECT_ID, new String[]{childId});
      child.put(CMIS.OBJECT_TYPE_ID, new String[]{type.getId()});
      child.put(CMIS.BASE_TYPE_ID, new String[]{type.getBaseId().value()});

      ConversationState cstate = ConversationState.getCurrent();
      String userId = null;
      if (cstate != null)
         userId = cstate.getIdentity().getUserId();
      if (userId != null)
         child.put(CMIS.CREATED_BY, new String[]{userId});
      Calendar date = Calendar.getInstance();
      child.put(CMIS.CREATION_DATE, new Calendar[]{date});

      storage.getObjects().put(childId, child);

      storage.getChildren().get(objectId).add(childId);

      Set<String> parentsSet = new CopyOnWriteArraySet<String>();
      parentsSet.add(objectId);
      storage.getParents().put(childId, parentsSet);

      return new EntryImpl(childId, storage);
   }

   /**
    * Create relationship and use this object as source.
    *  
    * @param name the object name 
    * @param type type of relationship
    * @param target the target object of relationship
    * @return newly created relationship
    * @throws RepositoryException if any repository exception occurs
    */
   protected Entry createRelationship(String name, CmisTypeDefinitionType type, Entry target)
      throws RepositoryException
   {
      String relationshipId = RepositoryImpl.generateId();

      Map<String, Object[]> relationship = new ConcurrentHashMap<String, Object[]>();
      relationship.put(CMIS.NAME, new String[]{name});
      relationship.put(CMIS.OBJECT_ID, new String[]{relationshipId});
      relationship.put(CMIS.OBJECT_TYPE_ID, new String[]{type.getId()});
      relationship.put(CMIS.BASE_TYPE_ID, new String[]{type.getBaseId().value()});

      ConversationState cstate = ConversationState.getCurrent();
      String userId = null;
      if (cstate != null)
         userId = cstate.getIdentity().getUserId();
      if (userId != null)
         relationship.put(CMIS.CREATED_BY, new String[]{userId});
      Calendar date = Calendar.getInstance();
      relationship.put(CMIS.CREATION_DATE, new Calendar[]{date});
      relationship.put(CMIS.SOURCE_ID, new String[]{objectId});
      relationship.put(CMIS.TARGET_ID, new String[]{target.getObjectId()});

      storage.getObjects().put(relationshipId, relationship);

      return new EntryImpl(relationshipId, storage);
   }

}
