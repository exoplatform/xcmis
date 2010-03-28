/*
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

import org.exoplatform.services.security.ConversationState;
import org.xcmis.core.CmisAccessControlEntryType;
import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.CmisAccessControlPrincipalType;
import org.xcmis.core.CmisAllowableActionsType;
import org.xcmis.core.CmisObjectType;
import org.xcmis.core.CmisPropertiesType;
import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisPropertyBoolean;
import org.xcmis.core.CmisPropertyDateTime;
import org.xcmis.core.CmisPropertyDecimal;
import org.xcmis.core.CmisPropertyDefinitionType;
import org.xcmis.core.CmisPropertyHtml;
import org.xcmis.core.CmisPropertyId;
import org.xcmis.core.CmisPropertyInteger;
import org.xcmis.core.CmisPropertyString;
import org.xcmis.core.CmisPropertyUri;
import org.xcmis.core.CmisRenditionType;
import org.xcmis.core.CmisRepositoryInfoType;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.CmisTypeDocumentDefinitionType;
import org.xcmis.core.EnumACLPropagation;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumIncludeRelationships;
import org.xcmis.core.EnumPropertyType;
import org.xcmis.core.EnumRelationshipDirection;
import org.xcmis.core.EnumUnfileObject;
import org.xcmis.core.EnumVersioningState;
import org.xcmis.messaging.CmisObjectInFolderContainerType;
import org.xcmis.messaging.CmisObjectListType;
import org.xcmis.messaging.CmisTypeContainer;
import org.xcmis.messaging.CmisTypeDefinitionListType;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.FilterNotValidException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.NotSupportedException;
import org.xcmis.spi.ObjectNotFoundException;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.StreamNotSupportedException;
import org.xcmis.spi.TypeNotFoundException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.impl.BaseConnection;
import org.xcmis.spi.impl.CmisObjectIdentifier;
import org.xcmis.spi.impl.CmisVisitor;
import org.xcmis.spi.impl.PropertyFilter;
import org.xcmis.spi.impl.RenditionFilter;
import org.xcmis.spi.object.BaseContentStream;
import org.xcmis.spi.object.BaseItemsIterator;
import org.xcmis.spi.object.ContentStream;
import org.xcmis.spi.object.ItemsIterator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class InmemoryConnection extends BaseConnection
{

   private final InMemStorage storage;

   private final ConversationState user;

   public InmemoryConnection(InMemStorage storage, ConversationState user)
   {
      this.storage = storage;
      this.user = user;
   }

   public void addObjectToFolder(String objectId, String folderId, boolean allVersions) throws ObjectNotFoundException,
      InvalidArgumentException, ConstraintException, CmisRuntimeException
   {
      CmisObjectIdentifier object = getObject(objectId);
      String typeId = object.getTypeId();
      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId, false);
      if (!typeDefinition.isFileable())
         throw new InvalidArgumentException("Object " + objectId + " is not fileable.");
      CmisObjectIdentifier folder = getObject(folderId);
      if (folder.getBaseType() != EnumBaseObjectTypeIds.CMIS_FOLDER)
         throw new InvalidArgumentException("Object " + folderId + " is not Folder.");
      CmisPropertyId allowedChildTypes = (CmisPropertyId)getProperty(folder, CMIS.ALLOWED_CHILD_OBJECT_TYPE_IDS);
      if (allowedChildTypes != null && allowedChildTypes.getValue().size() > 0
         && !allowedChildTypes.getValue().contains(typeId))
         throw new ConstraintException("Type " + typeId + " is not allowed as child for " + folder.getTypeId());
      storage.getChildren(folderId).add(objectId);
      storage.getParents(objectId).add(folderId);
   }

   public void addType(CmisTypeDefinitionType type) throws StorageException, CmisRuntimeException
   {
      storage.addType(type);
   }

   public void applyAcl(String objectId, CmisAccessControlListType addAcl, CmisAccessControlListType removeAcl,
      EnumACLPropagation propagation) throws ObjectNotFoundException, ConstraintException, CmisRuntimeException
   {
      CmisObjectIdentifier object = getObject(objectId);

      String typeId = object.getTypeId();
      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId, false);
      if (!typeDefinition.isControllableACL())
         throw new ConstraintException("Type " + typeId + " is not controllable by ACL.");

      CmisAccessControlListType existedAcl = ((InmemoryObjectData)object).getAcl();
      CmisAccessControlListType mergedAcl = mergeAcls(existedAcl, addAcl, removeAcl);
      ((InmemoryObjectData)object).setAcl(mergedAcl);
   }

   public void cancelCheckout(String documentId) throws ConstraintException, UpdateConflictException,
      VersioningException, StorageException, CmisRuntimeException
   {
      CmisObjectIdentifier object = getObject(documentId);
      String checkedOutId =
         ((IdPropertyData)((InmemoryObjectData)object).getPropertyData(CMIS.VERSION_SERIES_CHECKED_OUT_ID)).getValue();
      if (checkedOutId != null)
      {
         String versionSeriesId =
            ((IdPropertyData)((InmemoryObjectData)object).getPropertyData(CMIS.VERSION_SERIES_ID)).getValue();
         storage.removeContent(checkedOutId);
         storage.removeParents(checkedOutId);
         storage.objects.remove(checkedOutId);
         storage.getVersions(versionSeriesId).remove(checkedOutId);
         for (String versionId : storage.getVersions(versionSeriesId))
         {
            InmemoryObjectData version = (InmemoryObjectData)getObject(versionId);
            ((BooleanPropertyData)version.getPropertyData(CMIS.IS_VERSION_SERIES_CHECKED_OUT)).setValue(false);
            ((IdPropertyData)version.getPropertyData(CMIS.VERSION_SERIES_CHECKED_OUT_ID)).setValue(null);
            ((StringPropertyData)version.getPropertyData(CMIS.VERSION_SERIES_CHECKED_OUT_BY)).setValue(null);
         }
      }
   }

   public CmisObjectType checkin(String documentId, boolean major, CmisPropertiesType properties,
      ContentStream content, String checkinComment, CmisAccessControlListType addACL,
      CmisAccessControlListType removeACL, Collection<String> policies) throws ConstraintException, UpdateConflictException,
      StreamNotSupportedException, IOException, StorageException
   {
      CmisObjectIdentifier pwc = getObject(documentId);
      CmisPropertyId checkedOutProperty = (CmisPropertyId)getProperty(pwc, CMIS.VERSION_SERIES_CHECKED_OUT_ID);
      if (checkedOutProperty == null || checkedOutProperty.getValue().size() == 0
         || !pwc.getObjectId().equals(checkedOutProperty.getValue().get(0)))
         throw new InvalidArgumentException("Object " + documentId + " is not Private Working Copy");

      if (content != null)
         setContentStream(pwc, content);
      if (properties != null)
         updateProperties(pwc, properties);

      String versionSeriesId =
         ((IdPropertyData)((InmemoryObjectData)pwc).getPropertyData(CMIS.VERSION_SERIES_ID)).getValue();

      Set<String> versions = storage.getVersions(versionSeriesId);
      ((StringPropertyData)((InmemoryObjectData)pwc).getPropertyData(CMIS.VERSION_LABEL)).setValue("ver. "
         + versions.size());

      for (String versionId : versions)
      {
         InmemoryObjectData version = (InmemoryObjectData)getObject(versionId);
         ((BooleanPropertyData)version.getPropertyData(CMIS.IS_VERSION_SERIES_CHECKED_OUT)).setValue(false);
         ((IdPropertyData)version.getPropertyData(CMIS.VERSION_SERIES_CHECKED_OUT_ID)).setValue(null);
         ((StringPropertyData)version.getPropertyData(CMIS.VERSION_SERIES_CHECKED_OUT_BY)).setValue(null);
      }
      return createCmisObject(pwc, false, EnumIncludeRelationships.NONE, false, false, PropertyFilter.ALL,
         RenditionFilter.NONE);
   }

   public CmisObjectType checkout(String documentId) throws ConstraintException, UpdateConflictException,
      VersioningException, StorageException, CmisRuntimeException
   {
      CmisObjectIdentifier document = getObject(documentId);
      if (document.getBaseType() != EnumBaseObjectTypeIds.CMIS_DOCUMENT)
         throw new InvalidArgumentException("Object is not Document.");

      String typeId = document.getTypeId();
      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId, false);
      if (!((CmisTypeDocumentDefinitionType)typeDefinition).isVersionable())
         throw new ConstraintException("Type " + typeId + " is not versionable.");

      String versionSeriesId =
         ((IdPropertyData)((InmemoryObjectData)document).getPropertyData(CMIS.VERSION_SERIES_ID)).getValue();
      if (storage.getPwC(versionSeriesId) != null)
         throw new InvalidArgumentException("Version series already has checked-out document.");

      Map<String, PropertyData<?>> newProperties = new ConcurrentHashMap<String, PropertyData<?>>();
      // Copy properties
      for (String propertyId : ((InmemoryObjectData)document).getPropertyNames())
      {
         PropertyData<?> pdata = ((InmemoryObjectData)document).getPropertyData(propertyId);
         if (pdata.getPropertyType() == EnumPropertyType.BOOLEAN)
            newProperties.put(propertyId, new BooleanPropertyData((BooleanPropertyData)pdata));
         else if (pdata.getPropertyType() == EnumPropertyType.DATETIME)
            newProperties.put(propertyId, new DateTimePropertyData((DateTimePropertyData)pdata));
         else if (pdata.getPropertyType() == EnumPropertyType.DECIMAL)
            newProperties.put(propertyId, new DecimalPropertyData((DecimalPropertyData)pdata));
         else if (pdata.getPropertyType() == EnumPropertyType.HTML)
            newProperties.put(propertyId, new HtmlPropertyData((HtmlPropertyData)pdata));
         else if (pdata.getPropertyType() == EnumPropertyType.ID)
            newProperties.put(propertyId, new IdPropertyData((IdPropertyData)pdata));
         else if (pdata.getPropertyType() == EnumPropertyType.INTEGER)
            newProperties.put(propertyId, new IntegerPropertyData((IntegerPropertyData)pdata));
         else if (pdata.getPropertyType() == EnumPropertyType.STRING)
            newProperties.put(propertyId, new StringPropertyData((StringPropertyData)pdata));
         else if (pdata.getPropertyType() == EnumPropertyType.URI)
            newProperties.put(propertyId, new UriPropertyData((UriPropertyData)pdata));
      }
      // Set properties that must be different from original object.
      String pwcId = InMemStorage.generateId();
      Calendar date = Calendar.getInstance();
      ((IdPropertyData)newProperties.get(CMIS.OBJECT_ID)).setValue(pwcId);
      ((StringPropertyData)newProperties.get(CMIS.CREATED_BY)).setValue(user != null ? user.getIdentity().getUserId()
         : null);
      ((DateTimePropertyData)newProperties.get(CMIS.CREATION_DATE)).setValue(date);
      ((StringPropertyData)newProperties.get(CMIS.LAST_MODIFIED_BY)).setValue(user != null ? user.getIdentity()
         .getUserId() : null);
      ((DateTimePropertyData)newProperties.get(CMIS.LAST_MODIFICATION_DATE)).setValue(date);
      ((BooleanPropertyData)newProperties.get(CMIS.IS_MAJOR_VERSION)).setValue(false);
      ((StringPropertyData)newProperties.get(CMIS.VERSION_LABEL)).setValue("pwc");
      ((BooleanPropertyData)newProperties.get(CMIS.IS_VERSION_SERIES_CHECKED_OUT)).setValue(true);
      ((IdPropertyData)newProperties.get(CMIS.VERSION_SERIES_CHECKED_OUT_ID)).setValue(pwcId);
      ((StringPropertyData)newProperties.get(CMIS.VERSION_SERIES_CHECKED_OUT_BY)).setValue(user != null ? user
         .getIdentity().getUserId() : null);

      for (String versionId : storage.getVersions(versionSeriesId))
      {
         InmemoryObjectData version = (InmemoryObjectData)getObject(versionId);
         ((BooleanPropertyData)version.getPropertyData(CMIS.IS_VERSION_SERIES_CHECKED_OUT)).setValue(true);
         ((IdPropertyData)version.getPropertyData(CMIS.VERSION_SERIES_CHECKED_OUT_ID)).setValue(pwcId);
         ((StringPropertyData)version.getPropertyData(CMIS.VERSION_SERIES_CHECKED_OUT_BY)).setValue(user != null ? user
            .getIdentity().getUserId() : null);
      }

      storage.getVersions(versionSeriesId).add(pwcId);
      storage.putPwC(versionSeriesId, pwcId);
      storage.getParents(pwcId).addAll(storage.getParents(documentId));
      InmemoryObjectData objectData = new InmemoryObjectData(pwcId, newProperties, null, null);

      putObject(objectData);
      CmisObjectType object =
         createCmisObject(objectData, false, EnumIncludeRelationships.NONE, false, false, PropertyFilter.ALL,
            RenditionFilter.NONE);
      return object;
   }

   public void close()
   {
   }

   public CmisAccessControlListType getAcl(String objectId, boolean onlyBasicPermissions)
      throws ObjectNotFoundException, ConstraintException, CmisRuntimeException
   {
      CmisObjectIdentifier object = getObject(objectId);
      String typeId = object.getTypeId();
      CmisTypeDefinitionType typeDefinition = getTypeDefinition(typeId, false);
      if (!typeDefinition.isControllableACL())
         throw new ConstraintException("Type " + typeId + " is not controllable by ACL.");
      return ((InmemoryObjectData)object).getAcl();
   }

   public List<CmisObjectType> getAllVersions(String versionSeriesId, boolean includeAllowableActions,
      String propertyFilter) throws ObjectNotFoundException, FilterNotValidException, CmisRuntimeException
   {
      if (!storage.hasVersions(versionSeriesId))
         throw new ObjectNotFoundException("Version series " + versionSeriesId + " does not exists.");
      PropertyFilter parsedPropertyFilter = new PropertyFilter(propertyFilter);
      List<CmisObjectType> versions = new ArrayList<CmisObjectType>();
      for (String vsId : storage.getVersions(versionSeriesId))
      {
         CmisObjectIdentifier object = getObject(vsId);
         versions.add(createCmisObject(object, includeAllowableActions, EnumIncludeRelationships.NONE, false, false,
            parsedPropertyFilter, RenditionFilter.NONE));
      }
      return versions;
   }

   public CmisObjectListType getCheckedOutDocs(String folderId, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, String propertyFilter, String renditionFilter, String orderBy,
      int maxItems, int skipCount) throws ObjectNotFoundException, InvalidArgumentException, FilterNotValidException,
      CmisRuntimeException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public CmisObjectListType getContentChanges(String changeLogToken, boolean includeProperties, String propertyFilter,
      boolean includePolicyIDs, boolean includeAcl, int maxItems, int skipCount) throws ConstraintException,
      FilterNotValidException, CmisRuntimeException
   {
      throw new NotSupportedException("change log feature is not supported.");
   }

   public List<CmisObjectInFolderContainerType> getDescendats(String folderId, int depth,
      boolean includeAllowableActions, EnumIncludeRelationships includeRelationships, boolean includePathSegments,
      String propertyFilter, String renditionFilter) throws ObjectNotFoundException, InvalidArgumentException,
      FilterNotValidException, CmisRuntimeException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public List<CmisObjectInFolderContainerType> getFolderTree(String folderId, int depth,
      boolean includeAllowableActions, EnumIncludeRelationships includeRelationships, boolean includePathSegments,
      String propertyFilter, String renditionFilter) throws ObjectNotFoundException, InvalidArgumentException,
      FilterNotValidException, CmisRuntimeException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public CmisObjectType getObjectOfLatestVersion(String versionSeriesId, boolean major,
      boolean includeAllowableActions, EnumIncludeRelationships includeRelationships, boolean includePolicyIDs,
      boolean includeAcl, String propertyFilter, String renditionFilter) throws ObjectNotFoundException,
      FilterNotValidException, CmisRuntimeException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public CmisPropertiesType getPropertiesOfLatestVersion(String versionSeriesId, boolean major, String propertyFilter)
      throws FilterNotValidException, ObjectNotFoundException, CmisRuntimeException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public List<CmisRenditionType> getRenditions(String objectId, String renditionFilter, int maxItems, int skipCount)
      throws ObjectNotFoundException, FilterNotValidException, CmisRuntimeException
   {
      throw new NotSupportedException("rendition is not supported.");
   }

   public CmisRepositoryInfoType getStorageInfo() throws CmisRuntimeException
   {
      return storage.getInfo();
   }

   // TODO : baseCollection
   public CmisTypeDefinitionListType getTypeChildren(String typeId, boolean includePropertyDefinition, int maxItems,
      int skipCount) throws TypeNotFoundException, CmisRuntimeException
   {
      if (skipCount < 0)
      {
         String msg = "skipCount parameter is negative.";
         throw new InvalidArgumentException(msg);
      }
      if (maxItems < 0)
      {
         String msg = "maxItems parameter is negative.";
         throw new InvalidArgumentException(msg);
      }

      CmisTypeIdentifier type = getType(typeId);
      ItemsIterator<CmisTypeIdentifier> iterator = getTypeChildren(type);
      try
      {
         if (skipCount > 0)
            iterator.skip(skipCount);
      }
      catch (NoSuchElementException nse)
      {
         String msg = "skipCount parameter is greater then total number of argument";
         throw new InvalidArgumentException(msg);
      }

      CmisTypeDefinitionListType children = new CmisTypeDefinitionListType();
      int count = 0;
      while (iterator.hasNext() && count < maxItems)
      {
         CmisTypeIdentifier identf = iterator.next();
         children.getTypes().add(getTypeDefinition(identf.getTypeId(), includePropertyDefinition));
      }
      // Indicate that we have some more results.
      children.setHasMoreItems(iterator.hasNext());
      long total = iterator.size();
      if (total != -1)
         children.setNumItems(BigInteger.valueOf(total));
      return children;
   }

   public CmisTypeDefinitionType getTypeDefinition(String typeId, boolean includePropertyDefinition)
      throws TypeNotFoundException, CmisRuntimeException
   {
      return storage.getTypeDefinition(typeId, includePropertyDefinition);
   }

   public List<CmisTypeContainer> getTypeDescendants(final String typeId, final int depth,
      final boolean includePropertyDefinition) throws TypeNotFoundException, CmisRuntimeException
   {
      getTypeDefinition(typeId);
      final List<CmisTypeContainer> descendats = new ArrayList<CmisTypeContainer>();
      getType(typeId).accept(new CmisTypeVisitor()
      {
         private int level = 0;

         private CmisTypeContainer container;

         public void visit(CmisTypeIdentifier typeIdentifier)
         {
            level++;
            if ((depth == -1 || level <= depth))
            {
               for (ItemsIterator<CmisTypeIdentifier> children = getTypeChildren(typeIdentifier); children.hasNext();)
               {
                  CmisTypeIdentifier next = children.next();
                  CmisTypeContainer parent = null;
                  if (container != null)
                     parent = container;
                  container = new CmisTypeContainer();
                  container.setType(getTypeDefinition(next.getTypeId()));
                  if (parent != null)
                     parent.getChildren().add(container);
                  else
                     descendats.add(container);
                  next.accept(this);
                  container = parent;
               }
            }
            level--;
         }
      });
//      print(descendats, 0);
      return descendats;
   }

   private void print(List<CmisTypeContainer> l, int t)
   {
      for (CmisTypeContainer c : l)
      {
         for (int i = 0; i < t; i++)
            System.out.print(" ");
         System.out.println(c.getType().getId());
         if (c.getChildren().size() > 0)
            print(c.getChildren(), t + 3);
      }
   }

   ////////////////////////////////////////

   protected ItemsIterator<CmisTypeIdentifier> getTypeChildren(CmisTypeIdentifier type)
   {
      // TODO : baseCollection
      if (!storage.hasTypeChildren(type.getTypeId()))
         return new BaseItemsIterator<CmisTypeIdentifier>(new ArrayList<CmisTypeIdentifier>(0));
      List<CmisTypeIdentifier> result = new ArrayList<CmisTypeIdentifier>();
      for (String chId : storage.getTypeChildren(type.getTypeId()))
         result.add(getType(chId));
      return new BaseItemsIterator<CmisTypeIdentifier>(result);
   }

   protected CmisTypeIdentifier getType(String typeId)
   {
      // TODO : baseCollection
      if (storage.types.get(typeId) == null)
         throw new TypeNotFoundException("Type " + typeId + " does not exists.");
      return new CmisTypeIdentifier(typeId);
   }

   private class CmisTypeIdentifier
   {

      private final String typeId;

      public CmisTypeIdentifier(String typeId)
      {
         this.typeId = typeId;
      }

      public String getTypeId()
      {
         return typeId;
      }

      public void accept(CmisTypeVisitor visitor)
      {
         visitor.visit(this);
      }

      public String toString()
      {
         return "{typeId: " + typeId + "}";
      }
   }

   private interface CmisTypeVisitor
   {
      public void visit(CmisTypeIdentifier tw);
   }

   ////////////////////////////////////////

   public CmisObjectListType query(String statement, boolean searchAllVersions, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, String renditionFilter, int maxItems, int skipCount)
      throws FilterNotValidException, CmisRuntimeException
   {
      throw new NotSupportedException("query is not supported.");
   }

   public void removeObjectFromFolder(String objectId, String folderId) throws ObjectNotFoundException,
      CmisRuntimeException
   {
      // TODO Auto-generated method stub

   }

   public void removeType(String typeId) throws TypeNotFoundException, ConstraintException, StorageException,
      CmisRuntimeException
   {
      storage.removeType(typeId);
   }

   private void addAclToPermissionMap(Map<String, Set<String>> map, CmisAccessControlListType acl)
   {
      if (acl != null)
      {
         for (CmisAccessControlEntryType ace : acl.getPermission())
         {
            String principal = ace.getPrincipal() != null ? ace.getPrincipal().getPrincipalId() : null;
            if (principal == null)
               continue;

            Set<String> permissions = map.get(principal);
            if (permissions == null)
            {
               permissions = new HashSet<String>();
               map.put(principal, permissions);
            }
            permissions.addAll(ace.getPermission());
         }
      }
   }

   private void setPropertyData(Map<String, PropertyData<?>> propertyDatas, CmisProperty property)
   {
      if (property instanceof CmisPropertyBoolean)
         propertyDatas.put(property.getPropertyDefinitionId(), new BooleanPropertyData((CmisPropertyBoolean)property));
      else if (property instanceof CmisPropertyDateTime)
         propertyDatas
            .put(property.getPropertyDefinitionId(), new DateTimePropertyData((CmisPropertyDateTime)property));
      else if (property instanceof CmisPropertyDecimal)
         propertyDatas.put(property.getPropertyDefinitionId(), new DecimalPropertyData((CmisPropertyDecimal)property));
      else if (property instanceof CmisPropertyHtml)
         propertyDatas.put(property.getPropertyDefinitionId(), new HtmlPropertyData((CmisPropertyHtml)property));
      else if (property instanceof CmisPropertyId)
         propertyDatas.put(property.getPropertyDefinitionId(), new IdPropertyData((CmisPropertyId)property));
      else if (property instanceof CmisPropertyInteger)
         propertyDatas.put(property.getPropertyDefinitionId(), new IntegerPropertyData((CmisPropertyInteger)property));
      else if (property instanceof CmisPropertyString)
         propertyDatas.put(property.getPropertyDefinitionId(), new StringPropertyData((CmisPropertyString)property));
      else if (property instanceof CmisPropertyUri)
         propertyDatas.put(property.getPropertyDefinitionId(), new UriPropertyData((CmisPropertyUri)property));
   }

   private void addPropertyAdapter(Map<String, PropertyData<?>> propertyAdapters, PropertyData<?> adapter)
   {
      propertyAdapters.put(adapter.getPropertyId(), adapter);
   }

   private StringBuilder calculateObjectPath(CmisObjectIdentifier object)
   {
      LinkedList<String> pathSegms = new LinkedList<String>();
      pathSegms.add(object.getName());

      CmisObjectIdentifier parent = getFolderParent(object);
      while (parent != null)
      {
         pathSegms.add(parent.getName());
         parent = getFolderParent(parent);
      }

      StringBuilder path = new StringBuilder();
      for (String seg : pathSegms)
      {
         if (path.length() > 1)
            path.append('/');
         path.append("".equals(seg) ? "/" : seg);
      }

      return path;
   }

   private void checkExists(CmisObjectIdentifier object)
   {
      CmisObjectIdentifier data = storage.objects.get(object.getObjectId());
      if (data == null)
         throw new CmisRuntimeException("Object was removed.");
   }

   private ContentStream copyOfContent(ContentStream content)
   {
      try
      {
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         InputStream in = content.getStream();
         byte[] buf = new byte[1024];
         int r = -1;
         while ((r = in.read(buf)) != -1)
            out.write(buf, 0, r);
         return new BaseContentStream(out.toByteArray(), content.getFileName(), content.getMediaType());
      }
      catch (IOException ioe)
      {
         throw new CmisRuntimeException("Unable copy document's content", ioe);
      }
   }

   private CmisAccessControlListType createAclFromPermissionMap(Map<String, Set<String>> permissions)
   {
      CmisAccessControlListType acl = new CmisAccessControlListType();
      for (Map.Entry<String, Set<String>> e : permissions.entrySet())
      {
         CmisAccessControlEntryType ace = new CmisAccessControlEntryType();
         CmisAccessControlPrincipalType principal = new CmisAccessControlPrincipalType();
         principal.setPrincipalId(e.getKey());
         ace.getPermission().addAll(e.getValue());
         ace.setPrincipal(principal);
         acl.getPermission().add(ace);
      }
      return acl;
   }

   private CmisPropertyDefinitionType getPropertyDefinition(List<CmisPropertyDefinitionType> all, String propertyId)
   {
      if (all != null)
      {
         for (CmisPropertyDefinitionType propDef : all)
         {
            if (propDef.getId().equals(propertyId))
               return propDef;
         }
      }
      return null;
   }

   private void removeAclFromPermissionMap(Map<String, Set<String>> map, CmisAccessControlListType acl)
   {
      if (acl != null)
      {
         for (CmisAccessControlEntryType ace : acl.getPermission())
         {
            String principal = ace.getPrincipal() != null ? ace.getPrincipal().getPrincipalId() : null;
            if (principal == null)
               continue;

            Set<String> permissions = map.get(principal);
            if (permissions != null)
            {
               permissions.removeAll(ace.getPermission());
               if (permissions.size() == 0)
                  map.remove(principal);
            }
         }
      }
   }

   private void validateName(CmisObjectIdentifier folder, String name)
   {
      Set<String> children = storage.getChildren(folder.getObjectId());
      if (children == null)
         return;
      for (String childId : children)
      {
         if (name.equals(getObject(childId).getName()))
            throw new NameConstraintViolationException("Folder already contains object with name " + name);
      }
   }

   protected void applyPolicy(CmisObjectIdentifier object, CmisObjectIdentifier policy) throws CmisRuntimeException
   {
      checkExists(object);
      ((InmemoryObjectData)object).getPolicies().add(policy.getObjectId());
   }

   protected CmisObjectType createCmisObject(CmisObjectIdentifier objectData, boolean includeAllowableActions,
      EnumIncludeRelationships includeRelationships, boolean includePolicyIds, boolean includeAcl,
      PropertyFilter parsedPropertyFilter, RenditionFilter parsedRenditionFilter)
   {
      CmisObjectType object = new CmisObjectType();
      CmisPropertiesType cmisProperties = new CmisPropertiesType();
      for (String propertyId : ((InmemoryObjectData)objectData).getPropertyNames())
         cmisProperties.getProperty().add(((InmemoryObjectData)objectData).getPropertyData(propertyId).getProperty());
      object.setProperties(cmisProperties);
      // TODO : continue
      return object;
   }

   protected CmisObjectIdentifier createDocument(CmisObjectIdentifier folder, CmisTypeDefinitionType typeDefinition,
      CmisPropertiesType properties, ContentStream content, CmisAccessControlListType addAcl,
      CmisAccessControlListType removeAcl, Collection<String> policies, EnumVersioningState versioningState)
      throws StorageException, NameConstraintViolationException, CmisRuntimeException
   {
      String name = getName(properties);
      if (name == null)
         throw new NameConstraintViolationException("Name is not specified.");
      String newDocumentId = InMemStorage.generateId();
      String versionSeriesId = InMemStorage.generateId();
      Map<String, PropertyData<?>> newProperties = new ConcurrentHashMap<String, PropertyData<?>>();
      Calendar date = Calendar.getInstance();
      List<CmisPropertyDefinitionType> propertyDefinitions = typeDefinition.getPropertyDefinition();
      addPropertyAdapter(newProperties, new IdPropertyData(getPropertyDefinition(propertyDefinitions, CMIS.OBJECT_ID),
         newDocumentId));
      addPropertyAdapter(newProperties, new StringPropertyData(getPropertyDefinition(propertyDefinitions, CMIS.NAME),
         name));
      addPropertyAdapter(newProperties, new IdPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.OBJECT_TYPE_ID), typeDefinition.getId()));
      addPropertyAdapter(newProperties, new IdPropertyData(
         getPropertyDefinition(propertyDefinitions, CMIS.BASE_TYPE_ID), typeDefinition.getBaseId().value()));
      addPropertyAdapter(newProperties, new StringPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.CREATED_BY), user != null ? user.getIdentity().getUserId() : null));
      addPropertyAdapter(newProperties, new DateTimePropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.CREATION_DATE), date));
      addPropertyAdapter(newProperties, new StringPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.LAST_MODIFIED_BY), user != null ? user.getIdentity().getUserId() : null));
      addPropertyAdapter(newProperties, new DateTimePropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.LAST_MODIFICATION_DATE), date));
      addPropertyAdapter(newProperties, new StringPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.CHANGE_TOKEN), InMemStorage.generateId()));
      addPropertyAdapter(newProperties, new BooleanPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.IS_IMMUTABLE), false));
      addPropertyAdapter(newProperties, new BooleanPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.IS_LATEST_VERSION), true));
      addPropertyAdapter(newProperties, new BooleanPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.IS_MAJOR_VERSION), versioningState == EnumVersioningState.MAJOR));
      //      addPropertyAdapter(newProperties, new StringPropertyData(getPropertyDefinition(propertyDefinitions,
      //         CMIS.VERSION_LABEL), versioningState == EnumVersioningState.CHECKEDOUT ? "pwc" : "current"));
      addPropertyAdapter(newProperties, new StringPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.VERSION_LABEL), "ver. 1"));
      addPropertyAdapter(newProperties, new BooleanPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.IS_VERSION_SERIES_CHECKED_OUT), versioningState == EnumVersioningState.CHECKEDOUT));
      addPropertyAdapter(newProperties, new IdPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.VERSION_SERIES_CHECKED_OUT_ID), versioningState == EnumVersioningState.CHECKEDOUT ? newDocumentId : null));
      addPropertyAdapter(newProperties, new StringPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.VERSION_SERIES_CHECKED_OUT_BY), versioningState == EnumVersioningState.CHECKEDOUT && user != null ? user
         .getIdentity().getUserId() : null));
      addPropertyAdapter(newProperties, new IdPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.VERSION_SERIES_ID), versionSeriesId));
      addPropertyAdapter(newProperties, new IntegerPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.CONTENT_STREAM_LENGTH), BigInteger.valueOf(content != null ? content.length() : 0)));
      addPropertyAdapter(newProperties, new StringPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.CONTENT_STREAM_MIME_TYPE), content != null ? content.getMediaType() : null));
      addPropertyAdapter(newProperties, new StringPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.CONTENT_STREAM_FILE_NAME), content != null ? name : null));
      addPropertyAdapter(newProperties, new IdPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.CONTENT_STREAM_ID), content != null ? newDocumentId : null));
      // custom properties
      for (CmisProperty customProp : properties.getProperty())
      {
         if (newProperties.get(customProp.getPropertyDefinitionId()) == null)
            continue; // skip already existed properties, e.g. 'cmis:name'
         CmisPropertyDefinitionType customPropDef =
            getPropertyDefinition(propertyDefinitions, customProp.getPropertyDefinitionId());
         if (customPropDef == null)
            throw new ConstraintException("Unsupported property " + customProp.getPropertyDefinitionId());
         setPropertyData(newProperties, customProp);
      }

      if (folder != null)
      {
         checkExists(folder);
         validateName(folder, name);
         storage.getChildren(folder.getObjectId()).add(newDocumentId);
         storage.getParents(newDocumentId).add(folder.getObjectId());
      }
      else
      {
         storage.getUnfiled().add(newDocumentId);
      }
      // Even type is not versionable version series must contains one Document. 
      storage.getVersions(versionSeriesId).add(newDocumentId);
      CmisAccessControlListType mergedAcl = null;
      if (addAcl != null)
         mergedAcl = mergeAcls(null, addAcl, removeAcl);
      if (content != null)
         storage.putContent(newDocumentId, content);

      InmemoryObjectData document = new InmemoryObjectData(newDocumentId, newProperties, policies, mergedAcl);
      putObject(document);
      return document;
   }

   protected CmisObjectIdentifier createDocumentFromSource(CmisObjectIdentifier source, CmisObjectIdentifier folder,
      CmisPropertiesType properties, CmisAccessControlListType addAcl, CmisAccessControlListType removeAcl,
      Collection<String> policies, EnumVersioningState versioningState) throws StorageException,
      NameConstraintViolationException, CmisRuntimeException
   {
      String name = getName(properties);
      String newDocumentId = InMemStorage.generateId();
      String versionSeriesId = InMemStorage.generateId();
      Calendar date = Calendar.getInstance();
      Map<String, PropertyData<?>> newProperties = new ConcurrentHashMap<String, PropertyData<?>>();
      // Copy properties
      for (String propertyId : ((InmemoryObjectData)source).getPropertyNames())
      {
         PropertyData<?> pdata = ((InmemoryObjectData)source).getPropertyData(propertyId);
         if (pdata.getPropertyType() == EnumPropertyType.BOOLEAN)
            newProperties.put(propertyId, new BooleanPropertyData((BooleanPropertyData)pdata));
         else if (pdata.getPropertyType() == EnumPropertyType.DATETIME)
            newProperties.put(propertyId, new DateTimePropertyData((DateTimePropertyData)pdata));
         else if (pdata.getPropertyType() == EnumPropertyType.DECIMAL)
            newProperties.put(propertyId, new DecimalPropertyData((DecimalPropertyData)pdata));
         else if (pdata.getPropertyType() == EnumPropertyType.HTML)
            newProperties.put(propertyId, new HtmlPropertyData((HtmlPropertyData)pdata));
         else if (pdata.getPropertyType() == EnumPropertyType.ID)
            newProperties.put(propertyId, new IdPropertyData((IdPropertyData)pdata));
         else if (pdata.getPropertyType() == EnumPropertyType.INTEGER)
            newProperties.put(propertyId, new IntegerPropertyData((IntegerPropertyData)pdata));
         else if (pdata.getPropertyType() == EnumPropertyType.STRING)
            newProperties.put(propertyId, new StringPropertyData((StringPropertyData)pdata));
         else if (pdata.getPropertyType() == EnumPropertyType.URI)
            newProperties.put(propertyId, new UriPropertyData((UriPropertyData)pdata));
      }

      // Update properties that may/must be different from original object.
      ((IdPropertyData)newProperties.get(CMIS.OBJECT_ID)).setValue(newDocumentId);
      ((IdPropertyData)newProperties.get(CMIS.VERSION_SERIES_ID)).setValue(versionSeriesId);
      ((StringPropertyData)newProperties.get(CMIS.CREATED_BY)).setValue(user != null ? user.getIdentity().getUserId()
         : null);
      ((DateTimePropertyData)newProperties.get(CMIS.CREATION_DATE)).setValue(date);
      ((StringPropertyData)newProperties.get(CMIS.LAST_MODIFIED_BY)).setValue(user != null ? user.getIdentity()
         .getUserId() : null);
      ((DateTimePropertyData)newProperties.get(CMIS.LAST_MODIFICATION_DATE)).setValue(date);
      ((BooleanPropertyData)newProperties.get(CMIS.IS_MAJOR_VERSION))
         .setValue(versioningState == EnumVersioningState.MAJOR);
      ((StringPropertyData)newProperties.get(CMIS.VERSION_LABEL))
         .setValue(versioningState == EnumVersioningState.CHECKEDOUT ? "pwc" : "current");
      ((BooleanPropertyData)newProperties.get(CMIS.IS_VERSION_SERIES_CHECKED_OUT))
         .setValue(versioningState == EnumVersioningState.CHECKEDOUT);
      ((IdPropertyData)newProperties.get(CMIS.VERSION_SERIES_CHECKED_OUT_ID))
         .setValue(versioningState == EnumVersioningState.CHECKEDOUT ? newDocumentId : null);
      ((StringPropertyData)newProperties.get(CMIS.VERSION_SERIES_CHECKED_OUT_BY))
         .setValue(versioningState == EnumVersioningState.CHECKEDOUT && user != null ? user.getIdentity().getUserId()
            : null);
      if (name != null)
         ((StringPropertyData)newProperties.get(CMIS.NAME)).setValue(name);

      CmisTypeDefinitionType typeDefinition = getTypeDefinition(source.getTypeId());
      List<CmisPropertyDefinitionType> propertyDefinitions = typeDefinition.getPropertyDefinition();
      for (CmisProperty customProp : properties.getProperty())
      {
         if (newProperties.get(customProp.getPropertyDefinitionId()) == null)
            continue; // skip already existed properties, e.g. 'cmis:name'
         CmisPropertyDefinitionType customPropDef =
            getPropertyDefinition(propertyDefinitions, customProp.getPropertyDefinitionId());
         if (customPropDef == null)
            throw new ConstraintException("Unsupported property " + customProp.getPropertyDefinitionId());
         setPropertyData(newProperties, customProp);
      }

      if (folder != null)
      {
         checkExists(folder);
         validateName(folder, ((StringPropertyData)newProperties.get(CMIS.NAME)).getValue());
         storage.getChildren(folder.getObjectId()).add(newDocumentId);
         storage.getParents(newDocumentId).add(folder.getObjectId());
      }
      else
      {
         storage.getUnfiled().add(newDocumentId);
      }
      // Even type is not versionable version series must contains one Document. 
      storage.getVersions(versionSeriesId).add(newDocumentId);
      CmisAccessControlListType mergedAcl = null;
      if (addAcl != null)
         mergedAcl = mergeAcls(null, addAcl, removeAcl);
      ContentStream content = getContentStream(source, null, 0, -1);
      if (content != null)
         storage.putContent(newDocumentId, copyOfContent(content));

      InmemoryObjectData document = new InmemoryObjectData(newDocumentId, newProperties, policies, mergedAcl);
      putObject(document);
      return document;
   }

   protected CmisObjectIdentifier createFolder(CmisObjectIdentifier folder, CmisTypeDefinitionType typeDefinition,
      CmisPropertiesType properties, CmisAccessControlListType addAcl, CmisAccessControlListType removeAcl,
      Collection<String> policies) throws StorageException, NameConstraintViolationException, CmisRuntimeException
   {
      String name = getName(properties);
      if (name == null)
         throw new NameConstraintViolationException("Name is not provided.");
      String newFolderId = InMemStorage.generateId();
      Map<String, PropertyData<?>> newProperties = new ConcurrentHashMap<String, PropertyData<?>>();
      Calendar date = Calendar.getInstance();
      List<CmisPropertyDefinitionType> propertyDefinitions = typeDefinition.getPropertyDefinition();
      addPropertyAdapter(newProperties, new IdPropertyData(getPropertyDefinition(propertyDefinitions, CMIS.OBJECT_ID),
         newFolderId));
      addPropertyAdapter(newProperties, new StringPropertyData(getPropertyDefinition(propertyDefinitions, CMIS.NAME),
         name));
      addPropertyAdapter(newProperties, new IdPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.OBJECT_TYPE_ID), typeDefinition.getId()));
      addPropertyAdapter(newProperties, new IdPropertyData(
         getPropertyDefinition(propertyDefinitions, CMIS.BASE_TYPE_ID), typeDefinition.getBaseId().value()));
      addPropertyAdapter(newProperties, new StringPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.CREATED_BY), user != null ? user.getIdentity().getUserId() : null));
      addPropertyAdapter(newProperties, new DateTimePropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.CREATION_DATE), date));
      addPropertyAdapter(newProperties, new StringPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.LAST_MODIFIED_BY), user != null ? user.getIdentity().getUserId() : null));
      addPropertyAdapter(newProperties, new DateTimePropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.LAST_MODIFICATION_DATE), date));
      addPropertyAdapter(newProperties, new StringPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.CHANGE_TOKEN), InMemStorage.generateId()));
      addPropertyAdapter(newProperties, new IdPropertyData(getPropertyDefinition(propertyDefinitions, CMIS.PARENT_ID),
         folder.getObjectId()));

      for (CmisProperty customProp : properties.getProperty())
      {
         if (newProperties.get(customProp.getPropertyDefinitionId()) == null)
            continue; // skip already existed properties, e.g. 'cmis:name'
         CmisPropertyDefinitionType customPropDef =
            getPropertyDefinition(propertyDefinitions, customProp.getPropertyDefinitionId());
         if (customPropDef == null)
            throw new ConstraintException("Unsupported property " + customProp.getPropertyDefinitionId());
         setPropertyData(newProperties, customProp);
      }

      checkExists(folder);

      validateName(folder, ((StringPropertyData)newProperties.get(CMIS.NAME)).getValue());
      storage.getChildren(folder.getObjectId()).add(newFolderId);
      storage.getParents(newFolderId).add(folder.getObjectId());
      CmisAccessControlListType mergedAcl = null;
      if (addAcl != null)
         mergedAcl = mergeAcls(null, addAcl, removeAcl);
      InmemoryObjectData newFolder = new InmemoryObjectData(newFolderId, newProperties, policies, mergedAcl);
      putObject(newFolder);
      return newFolder;
   }

   protected CmisObjectIdentifier createPolicy(CmisObjectIdentifier folder, CmisTypeDefinitionType typeDefinition,
      CmisPropertiesType properties, CmisAccessControlListType addAcl, CmisAccessControlListType removeAcl,
      Collection<String> policies) throws StorageException, NameConstraintViolationException, CmisRuntimeException
   {
      String name = getName(properties);
      if (name == null)
         throw new NameConstraintViolationException("Name is not provided.");
      String newPolicyId = InMemStorage.generateId();
      Map<String, PropertyData<?>> newProperties = new ConcurrentHashMap<String, PropertyData<?>>();
      Calendar date = Calendar.getInstance();
      List<CmisPropertyDefinitionType> propertyDefinitions = typeDefinition.getPropertyDefinition();
      addPropertyAdapter(newProperties, new IdPropertyData(getPropertyDefinition(propertyDefinitions, CMIS.OBJECT_ID),
         newPolicyId));
      addPropertyAdapter(newProperties, new IdPropertyData(getPropertyDefinition(propertyDefinitions, CMIS.OBJECT_ID),
         newPolicyId));
      addPropertyAdapter(newProperties, new StringPropertyData(getPropertyDefinition(propertyDefinitions, CMIS.NAME),
         name));
      addPropertyAdapter(newProperties, new IdPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.OBJECT_TYPE_ID), typeDefinition.getId()));
      addPropertyAdapter(newProperties, new IdPropertyData(
         getPropertyDefinition(propertyDefinitions, CMIS.BASE_TYPE_ID), typeDefinition.getBaseId().value()));
      addPropertyAdapter(newProperties, new StringPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.CREATED_BY), user != null ? user.getIdentity().getUserId() : null));
      addPropertyAdapter(newProperties, new DateTimePropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.CREATION_DATE), date));
      addPropertyAdapter(newProperties, new StringPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.LAST_MODIFIED_BY), user != null ? user.getIdentity().getUserId() : null));
      addPropertyAdapter(newProperties, new DateTimePropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.LAST_MODIFICATION_DATE), date));
      addPropertyAdapter(newProperties, new StringPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.CHANGE_TOKEN), InMemStorage.generateId()));

      CmisPropertyString policyTextProperty = (CmisPropertyString)getProperty(properties, CMIS.POLICY_TEXT);
      if (policyTextProperty == null || policyTextProperty.getValue().size() == 0)
         throw new ConstraintException("Required property 'cmis:policyText' is not specified.");
      addPropertyAdapter(newProperties, new StringPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.POLICY_TEXT), policyTextProperty.getValue().get(0)));

      for (CmisProperty customProp : properties.getProperty())
      {
         if (newProperties.get(customProp.getPropertyDefinitionId()) == null)
            continue; // skip already existed properties, e.g. 'cmis:name'
         CmisPropertyDefinitionType customPropDef =
            getPropertyDefinition(propertyDefinitions, customProp.getPropertyDefinitionId());
         if (customPropDef == null)
            throw new ConstraintException("Unsupported property " + customProp.getPropertyDefinitionId());
         setPropertyData(newProperties, customProp);
      }

      if (folder != null)
      {
         checkExists(folder);
         validateName(folder, name);
         storage.getChildren(folder.getObjectId()).add(newPolicyId);
         storage.getParents(newPolicyId).add(folder.getObjectId());
      }
      else
      {
         storage.getUnfiled().add(newPolicyId);
      }
      CmisAccessControlListType mergedAcl = null;
      if (addAcl != null)
         mergedAcl = mergeAcls(null, addAcl, removeAcl);

      InmemoryObjectData newPolicy = new InmemoryObjectData(newPolicyId, newProperties, policies, mergedAcl);
      putObject(newPolicy);
      return newPolicy;
   }

   protected CmisObjectIdentifier createRelationship(CmisTypeDefinitionType typeDefinition,
      CmisObjectIdentifier source, CmisObjectIdentifier target, CmisPropertiesType properties,
      CmisAccessControlListType addAcl, CmisAccessControlListType removeAcl, Collection<String> policies)
      throws StorageException, NameConstraintViolationException, CmisRuntimeException
   {
      String name = getName(properties);
      if (name == null)
         throw new NameConstraintViolationException("Name is not provided.");
      String newRelationshipId = InMemStorage.generateId();
      Map<String, PropertyData<?>> newProperties = new ConcurrentHashMap<String, PropertyData<?>>();
      Calendar date = Calendar.getInstance();
      List<CmisPropertyDefinitionType> propertyDefinitions = typeDefinition.getPropertyDefinition();
      addPropertyAdapter(newProperties, new IdPropertyData(getPropertyDefinition(propertyDefinitions, CMIS.OBJECT_ID),
         newRelationshipId));
      addPropertyAdapter(newProperties, new IdPropertyData(getPropertyDefinition(propertyDefinitions, CMIS.OBJECT_ID),
         newRelationshipId));
      addPropertyAdapter(newProperties, new StringPropertyData(getPropertyDefinition(propertyDefinitions, CMIS.NAME),
         name));
      addPropertyAdapter(newProperties, new IdPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.OBJECT_TYPE_ID), typeDefinition.getId()));
      addPropertyAdapter(newProperties, new IdPropertyData(
         getPropertyDefinition(propertyDefinitions, CMIS.BASE_TYPE_ID), typeDefinition.getBaseId().value()));
      addPropertyAdapter(newProperties, new StringPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.CREATED_BY), user != null ? user.getIdentity().getUserId() : null));
      addPropertyAdapter(newProperties, new DateTimePropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.CREATION_DATE), date));
      addPropertyAdapter(newProperties, new StringPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.LAST_MODIFIED_BY), user != null ? user.getIdentity().getUserId() : null));
      addPropertyAdapter(newProperties, new DateTimePropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.LAST_MODIFICATION_DATE), date));
      addPropertyAdapter(newProperties, new StringPropertyData(getPropertyDefinition(propertyDefinitions,
         CMIS.CHANGE_TOKEN), InMemStorage.generateId()));
      addPropertyAdapter(newProperties, new IdPropertyData(getPropertyDefinition(propertyDefinitions, CMIS.SOURCE_ID),
         source.getObjectId()));
      addPropertyAdapter(newProperties, new IdPropertyData(getPropertyDefinition(propertyDefinitions, CMIS.TARGET_ID),
         target.getObjectId()));

      for (CmisProperty customProp : properties.getProperty())
      {
         if (newProperties.get(customProp.getPropertyDefinitionId()) == null)
            continue; // skip already existed properties, e.g. 'cmis:name'
         CmisPropertyDefinitionType customPropDef =
            getPropertyDefinition(propertyDefinitions, customProp.getPropertyDefinitionId());
         if (customPropDef == null)
            throw new ConstraintException("Unsupported property " + customProp.getPropertyDefinitionId());
         setPropertyData(newProperties, customProp);
      }

      CmisAccessControlListType mergedAcl = null;
      if (addAcl != null)
         mergedAcl = mergeAcls(null, addAcl, removeAcl);

      return new InmemoryObjectData(newRelationshipId, newProperties, policies, mergedAcl);
   }

   protected void deleteContentStream(CmisObjectIdentifier document, String changeToken) throws StorageException,
      UpdateConflictException, CmisRuntimeException
   {
      checkExists(document);
      storage.removeContent(document.getObjectId());
      // Update content properties.
      ((InmemoryObjectData)document).getPropertyData(CMIS.CONTENT_STREAM_FILE_NAME).setValue(null);
      ((InmemoryObjectData)document).getPropertyData(CMIS.CONTENT_STREAM_ID).setValue(null);
      ((IntegerPropertyData)((InmemoryObjectData)document).getPropertyData(CMIS.CONTENT_STREAM_LENGTH))
         .setValue(BigInteger.valueOf(0));
      ((InmemoryObjectData)document).getPropertyData(CMIS.CONTENT_STREAM_MIME_TYPE).setValue(null);
   }

   protected void deleteObject(CmisObjectIdentifier object, boolean deleteAllVersion) throws UpdateConflictException,
      StorageException, CmisRuntimeException
   {
      if (hasChildren(object))
         throw new CmisRuntimeException("Object " + object + " has children.");
      for (Iterator<String> iterator = storage.getParents(object.getObjectId()).iterator(); iterator.hasNext();)
      {
         String parentId = iterator.next();
         storage.getChildren(parentId).remove(object.getObjectId());
      }
      storage.removeContent(object.getObjectId());
      storage.removeChildren(object.getObjectId());
      storage.removeParents(object.getObjectId());
      storage.objects.remove(object.getObjectId());
   }

   protected void deleteTree(CmisObjectIdentifier folder, boolean deleteAllVersions, final List<String> failedDelete,
      EnumUnfileObject unfileObject, final boolean continueOnFailure) throws UpdateConflictException, StorageException,
      CmisRuntimeException
   {
      checkExists(folder);
      // success status keeper
      final boolean[] success = new boolean[1];
      success[0] = true;
      CmisVisitor deleteVisitor = new CmisVisitor()
      {
         private boolean abort = false;

         public void visit(CmisObjectIdentifier object)
         {
            if (abort)
               return;
            if (object.getBaseType() == EnumBaseObjectTypeIds.CMIS_FOLDER)
            {
               for (ItemsIterator<CmisObjectIdentifier> children = getChildren(object, null); children.hasNext();)
                  children.next().accept(this);
            }
            try
            {
               deleteObject(object, true);
            }
            catch (Throwable t)
            {
               //t.printStackTrace();
               success[0] = false;
               if (!continueOnFailure)
                  abort = true;
            }
         }
      };
      folder.accept(deleteVisitor);
      if (!success[0])
      {
         checkExists(folder);
         // Check objects that was not removed.
         CmisVisitor failedDeleteVisitor = new CmisVisitor()
         {
            public void visit(CmisObjectIdentifier object)
            {
               if (object.getBaseType() == EnumBaseObjectTypeIds.CMIS_FOLDER && hasChildren(object))
               {
                  for (ItemsIterator<CmisObjectIdentifier> children = getChildren(object, null); children.hasNext();)
                     children.next().accept(this);
               }
               failedDelete.add(object.getObjectId());
            }
         };
         folder.accept(failedDeleteVisitor);
      }
   }

   protected CmisAllowableActionsType getAllowableActions(CmisObjectIdentifier object) throws CmisRuntimeException
   {
      // TODO Auto-generated method stub
      return null;
   }

   protected List<CmisObjectIdentifier> getAppliedPolicies(CmisObjectIdentifier object) throws CmisRuntimeException
   {
      checkExists(object);
      List<CmisObjectIdentifier> policies = new ArrayList<CmisObjectIdentifier>();
      for (String policyId : ((InmemoryObjectData)object).getPolicies())
         policies.add(getObject(policyId));
      return policies;
   }

   protected ItemsIterator<CmisObjectIdentifier> getChildren(CmisObjectIdentifier folder, String orderBy)
      throws CmisRuntimeException
   {
      checkExists(folder);
      if (!hasChildren(folder))
         return new BaseItemsIterator<CmisObjectIdentifier>(new ArrayList<CmisObjectIdentifier>(0));
      List<CmisObjectIdentifier> result = new ArrayList<CmisObjectIdentifier>();
      for (String chId : storage.getChildren(folder.getObjectId()))
         result.add(getObject(chId));
      return new BaseItemsIterator<CmisObjectIdentifier>(result);
   }

   protected ContentStream getContentStream(CmisObjectIdentifier object, String streamId, long offset, long length)
      throws ConstraintException, CmisRuntimeException
   {
      checkExists(object);
      return storage.getContent(object.getObjectId());
   }

   protected CmisObjectIdentifier getFolderParent(CmisObjectIdentifier folder) throws CmisRuntimeException
   {
      checkExists(folder);
      // Folder always have ONLY one parent.
      return getObject(storage.getParents(folder.getObjectId()).iterator().next());
   }

   protected CmisObjectIdentifier getObject(String objectId) throws ObjectNotFoundException, CmisRuntimeException
   {
      CmisObjectIdentifier object = storage.objects.get(objectId);
      if (object == null)
         throw new ObjectNotFoundException("Object " + objectId + " not found.");
      return object;
   }

   protected CmisObjectIdentifier getObjectByPath(String path) throws ObjectNotFoundException, CmisRuntimeException
   {
      if (!path.startsWith("/"))
         path = "/" + path;
      StringTokenizer tokenizer = new StringTokenizer(path, "/");
      String point = InMemStorage.ROOT_FOLDER_ID;
      while (tokenizer.hasMoreTokens())
      {
         if (point == null)
            break;
         String segName = tokenizer.nextToken();
         Set<String> children = null;
         if (storage.hasChildren(point))
            children = storage.getChildren(point);
         if (children == null || children.isEmpty())
         {
            point = null;
         }
         else
         {
            for (String id : children)
            {
               CmisObjectIdentifier seg = getObject(id);
               String name = seg.getName();
               if ((EnumBaseObjectTypeIds.CMIS_FOLDER == seg.getBaseType() || !tokenizer.hasMoreElements())
                  && segName.equals(name))
               {
                  point = id;
                  break;
               }
               point = null;
            }
         }
      }

      if (point == null)
         throw new ObjectNotFoundException("Path '" + path + "' not found.");
      return getObject(point);
   }

   protected List<CmisObjectIdentifier> getObjectParents(CmisObjectIdentifier object) throws CmisRuntimeException
   {
      checkExists(object);
      if (!storage.hasParents(object.getObjectId()))
         return Collections.emptyList();
      List<CmisObjectIdentifier> parents = new ArrayList<CmisObjectIdentifier>();
      for (String parentId : storage.getParents(object.getObjectId()))
         parents.add(getObject(parentId));
      return parents;
   }

   protected ItemsIterator<CmisObjectIdentifier> getObjectRelationships(CmisObjectIdentifier object,
      EnumRelationshipDirection direction, String typeId, boolean includeSubRelationshipTypes,
      boolean includeAllowableActions, PropertyFilter propertyFilter) throws CmisRuntimeException
   {
      return new BaseItemsIterator<CmisObjectIdentifier>(new ArrayList<CmisObjectIdentifier>(0));
   }

   protected CmisPropertiesType getProperties(CmisObjectIdentifier object, PropertyFilter propertyFilter)
      throws CmisRuntimeException
   {
      checkExists(object);
      CmisPropertiesType properties = new CmisPropertiesType();
      for (String propertyId : ((InmemoryObjectData)object).getPropertyNames())
         properties.getProperty().add(((InmemoryObjectData)object).getPropertyData(propertyId).getProperty());
      return null;
   }

   protected CmisProperty getProperty(CmisObjectIdentifier object, String propertyId) throws CmisRuntimeException
   {
      checkExists(object);
      PropertyData<?> propertyData = ((InmemoryObjectData)object).getPropertyData(propertyId);
      if (propertyData == null)
         return null;
      return propertyData.getProperty();
   }

   protected boolean hasChildren(CmisObjectIdentifier object) throws CmisRuntimeException
   {
      checkExists(object);
      return object.getBaseType() == EnumBaseObjectTypeIds.CMIS_FOLDER && storage.hasChildren(object.getObjectId());
   }

   protected boolean hasContent(CmisObjectIdentifier document) throws CmisRuntimeException
   {
      checkExists(document);
      return null != storage.getContent(document.getObjectId());
   }

   protected CmisAccessControlListType mergeAcls(CmisAccessControlListType existedAcl,
      CmisAccessControlListType addAcl, CmisAccessControlListType removeAcl)
   {
      Map<String, Set<String>> cache = new HashMap<String, Set<String>>();
      addAclToPermissionMap(cache, existedAcl);
      addAclToPermissionMap(cache, addAcl);
      removeAclFromPermissionMap(cache, removeAcl);
      return createAclFromPermissionMap(cache);
   }

   protected CmisObjectType moveObject(CmisObjectIdentifier object, CmisObjectIdentifier target,
      CmisObjectIdentifier source) throws UpdateConflictException, StorageException, CmisRuntimeException
   {
      // TODO Auto-generated method stub
      return null;
   }

   protected void putObject(CmisObjectIdentifier object)
   {
      storage.objects.put(object.getObjectId(), object);
   }

   protected void removePolicy(CmisObjectIdentifier object, CmisObjectIdentifier policy) throws CmisRuntimeException
   {
      checkExists(object);
      checkExists(policy);
      ((InmemoryObjectData)object).getPolicies().remove(policy.getObjectId());
   }

   protected void setContentStream(CmisObjectIdentifier document, ContentStream content, String changeToken)
      throws UpdateConflictException, IOException, StorageException, CmisRuntimeException
   {// TODO : not use change token here
      checkExists(document);
      storage.contents.put(document.getObjectId(), content);
      // Update content properties.
      ((StringPropertyData)((InmemoryObjectData)document).getPropertyData(CMIS.CONTENT_STREAM_FILE_NAME))
         .setValue(document.getName());
      ((IdPropertyData)((InmemoryObjectData)document).getPropertyData(CMIS.CONTENT_STREAM_ID)).setValue(document
         .getObjectId());
      ((IntegerPropertyData)((InmemoryObjectData)document).getPropertyData(CMIS.CONTENT_STREAM_LENGTH))
         .setValue(BigInteger.valueOf(content.length()));
      ((StringPropertyData)((InmemoryObjectData)document).getPropertyData(CMIS.CONTENT_STREAM_MIME_TYPE))
         .setValue(content.getMediaType());

   }

   @Deprecated
   protected void setContentStream(CmisObjectIdentifier document, ContentStream content)
      throws UpdateConflictException, IOException, StorageException, CmisRuntimeException
   {
      // TODO Auto-generated method stub

   }

   protected CmisObjectType updateProperties(CmisObjectIdentifier object, String changeToken,
      CmisPropertiesType properties) throws ConstraintException, NameConstraintViolationException,
      UpdateConflictException, StorageException, CmisRuntimeException
   {// TODO : not use change token here
      // TODO Auto-generated method stub
      return null;
   }

   @Deprecated
   protected CmisObjectType updateProperties(CmisObjectIdentifier object, CmisPropertiesType properties)
      throws ConstraintException, NameConstraintViolationException, UpdateConflictException, StorageException,
      CmisRuntimeException
   {
      // TODO Auto-generated method stub
      return null;
   }
}
