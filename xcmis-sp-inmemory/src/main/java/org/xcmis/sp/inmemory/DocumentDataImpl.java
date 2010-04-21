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

import org.xcmis.spi.BaseContentStream;
import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.DocumentData;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.model.ContentStreamAllowed;
import org.xcmis.spi.model.RelationshipDirection;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.VersioningState;
import org.xcmis.spi.utils.MimeType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
class DocumentDataImpl extends BaseObjectData implements DocumentData
{

   static String latestLabel = "latest";

   static String pwcLabel = "pwc";

   static byte[] EMPTY_CONTENT = new byte[0];

   private ContentStream contentStream;

   protected final VersioningState versioningState;

   public DocumentDataImpl(Entry entry, TypeDefinition type, StorageImpl storage)
   {
      super(entry, type, storage);
      this.versioningState = null;
   }

   public DocumentDataImpl(FolderData parent, TypeDefinition type, VersioningState versioningState, StorageImpl storage)
   {
      super(parent, type, storage);
      this.versioningState = versioningState;
   }

   public void cancelCheckout() throws StorageException
   {
      if (!type.isVersionable())
      {
         throw new ConstraintException("Object is not versionable.");
      }

      synchronized (storage)
      {
         String vsId = getVersionSeriesId();
         String pwcId = storage.workingCopies.get(vsId);
         if (pwcId == null)
         {
            return;
         }

         storage.properties.remove(pwcId);
         storage.policies.remove(pwcId);
         storage.permissions.remove(pwcId);
         storage.contents.remove(pwcId);
         for (String parent : storage.parents.get(pwcId))
         {
            storage.children.get(parent).remove(pwcId);
         }
         storage.parents.remove(pwcId);
         storage.unfiled.remove(pwcId);
         storage.workingCopies.remove(vsId);

         for (Iterator<String> iterator = storage.versions.get(getVersionSeriesId()).iterator(); iterator.hasNext();)
         {
            String version = iterator.next();
            Map<String, Value> props = storage.properties.get(version);
            props.put(CmisConstants.IS_VERSION_SERIES_CHECKED_OUT, new BooleanValue(false));
            props.put(CmisConstants.VERSION_SERIES_CHECKED_OUT_ID, new StringValue());
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public DocumentData checkin(boolean major, String checkinComment) throws ConstraintException, StorageException
   {
      if (!type.isVersionable())
      {
         throw new ConstraintException("Object is not versionable.");
      }

      if (!isPWC())
      {
         throw new ConstraintException("Current object is not Private Working Copy.");
      }

      synchronized (storage)
      {
         String pwcId = getObjectId();
         int i = 1;
         for (Iterator<String> iterator = storage.versions.get(getVersionSeriesId()).iterator(); iterator.hasNext();)
         {
            String version = iterator.next();
            Map<String, Value> props = storage.properties.get(version);
            props.put(CmisConstants.IS_VERSION_SERIES_CHECKED_OUT, new BooleanValue(false));
            props.put(CmisConstants.VERSION_SERIES_CHECKED_OUT_ID, new StringValue());
            props.put(CmisConstants.IS_LATEST_VERSION, new BooleanValue(false));
            // update version labels
            props.put(CmisConstants.VERSION_LABEL, new StringValue("" + i++));
         }

         entry.setValue(CmisConstants.IS_LATEST_VERSION, new BooleanValue(true));
         entry.setValue(CmisConstants.VERSION_LABEL, new StringValue(latestLabel));
         entry.setValue(CmisConstants.IS_MAJOR_VERSION, new BooleanValue(major));
         if (checkinComment != null)
         {
            entry.setValue(CmisConstants.CHECKIN_COMMENT, new StringValue(checkinComment));
         }
         save();
         storage.workingCopies.remove(getVersionSeriesId());
         storage.versions.get(getVersionSeriesId()).add(pwcId);
      }

      return this;
   }

   /**
    * {@inheritDoc}
    */
   public DocumentData checkout() throws ConstraintException, VersioningException, StorageException
   {
      if (isNew())
      {
         throw new UnsupportedOperationException("Unable checkout newly created Document.");
      }

      if (!type.isVersionable())
      {
         throw new ConstraintException("Object is not versionable.");
      }

      synchronized (storage)
      {
         if (storage.workingCopies.get(getVersionSeriesId()) != null)
         {
            throw new VersioningException("Version series already checked-out. "
               + "Not allowed have more then one PWC for version series at a time.");
         }

         Entry pwc = new Entry();
         pwc.setValues(entry.getValues());
         String pwcId = StorageImpl.generateId();
         pwc.setValue(CmisConstants.OBJECT_ID, new StringValue(pwcId));
         pwc.setValue(CmisConstants.CREATED_BY, new StringValue());
         pwc.setValue(CmisConstants.CREATION_DATE, new DateValue(Calendar.getInstance()));
         pwc.setValue(CmisConstants.IS_LATEST_VERSION, new BooleanValue(false));
         pwc.setValue(CmisConstants.IS_MAJOR_VERSION, new BooleanValue(false));
         pwc.setValue(CmisConstants.VERSION_LABEL, new StringValue(pwcLabel));
         pwc.setValue(CmisConstants.IS_VERSION_SERIES_CHECKED_OUT, new BooleanValue(true));
         pwc.setValue(CmisConstants.VERSION_SERIES_CHECKED_OUT_ID, new StringValue(pwcId));
         pwc.setValue(CmisConstants.VERSION_SERIES_CHECKED_OUT_BY, new StringValue());

         byte[] content = storage.contents.get(getObjectId());
         byte[] pwcContent;

         if (content == EMPTY_CONTENT)
         {
            pwcContent = EMPTY_CONTENT;
         }
         else
         {
            pwcContent = new byte[content.length];
            System.arraycopy(content, 0, pwcContent, 0, pwcContent.length);
         }

         for (Iterator<String> iterator = storage.versions.get(getVersionSeriesId()).iterator(); iterator.hasNext();)
         {
            String version = iterator.next();
            Map<String, Value> props = storage.properties.get(version);
            props.put(CmisConstants.IS_VERSION_SERIES_CHECKED_OUT, new BooleanValue(true));
            props.put(CmisConstants.VERSION_SERIES_CHECKED_OUT_ID, new StringValue(pwcId));
         }

         storage.contents.put(pwcId, pwcContent);

         for (String parent : storage.parents.get(getObjectId()))
         {
            storage.children.get(parent).add(pwcId);
         }
         storage.parents.put(pwcId, new CopyOnWriteArraySet<String>(storage.parents.get(getObjectId())));
         storage.properties.put(pwcId, new ConcurrentHashMap<String, Value>(pwc.getValues()));
         storage.policies.put(pwcId, new CopyOnWriteArraySet<String>());
         storage.permissions.put(pwcId, new ConcurrentHashMap<String, Set<String>>());

         storage.workingCopies.put(getVersionSeriesId(), pwcId);

         return new DocumentDataImpl(pwc, storage.getTypeDefinition(getTypeId(), true), storage);
      }
   }

   /**
    * {@inheritDoc}
    */
   public ContentStream getContentStream()
   {
      if (isNew())
      {
         throw new UnsupportedOperationException("getContentStream");
      }

      byte[] bytes = storage.contents.get(getObjectId());
      if (bytes != null)
      {
         MimeType mimeType = MimeType.fromString(getString(CmisConstants.CONTENT_STREAM_MIME_TYPE));
         String charset = getString(CmisConstants.CHARSET);
         if (charset != null)
         {
            mimeType.getParameters().put(CmisConstants.CHARSET, charset);
         }
         return new BaseContentStream(bytes, getName(), mimeType);
      }
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public ContentStream getContentStream(String streamId)
   {
      if (isNew())
      {
         throw new UnsupportedOperationException("getContentStream");
      }

      if (streamId == null || streamId.equals(getString(CmisConstants.CONTENT_STREAM_ID)))
      {
         return getContentStream();
      }

      // TODO renditions
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public String getContentStreamMimeType()
   {
      return getString(CmisConstants.CONTENT_STREAM_MIME_TYPE);
   }

   /**
    * {@inheritDoc}
    */
   public String getVersionLabel()
   {
      return getString(CmisConstants.VERSION_LABEL);
   }

   /**
    * {@inheritDoc}
    */
   public String getVersionSeriesCheckedOutBy()
   {
      return getString(CmisConstants.VERSION_SERIES_CHECKED_OUT_BY);
   }

   /**
    * {@inheritDoc}
    */
   public String getVersionSeriesCheckedOutId()
   {
      return getString(CmisConstants.VERSION_SERIES_CHECKED_OUT_ID);
   }

   /**
    * {@inheritDoc}
    */
   public String getVersionSeriesId()
   {
      return getString(CmisConstants.VERSION_SERIES_ID);
   }

   /**
    * {@inheritDoc}
    */
   public boolean hasContent()
   {
      if (isNew())
      {
         return false;
      }

      return storage.contents.get(getObjectId()).length != 0;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isLatestMajorVersion()
   {
      return isLatestVersion() && isMajorVersion();
   }

   /**
    * {@inheritDoc}
    */
   public boolean isLatestVersion()
   {
      Boolean latest = getBoolean(CmisConstants.IS_LATEST_VERSION);
      return latest == null ? true : latest;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isMajorVersion()
   {
      Boolean major = getBoolean(CmisConstants.IS_MAJOR_VERSION);
      return major == null ? false : major;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isPWC()
   {
      if (isNew())
      {
         return false;
      }

      return getObjectId().equals(getVersionSeriesCheckedOutId());
   }

   /**
    * {@inheritDoc}
    */
   public boolean isVersionSeriesCheckedOut()
   {
      Boolean checkout = getBoolean(CmisConstants.IS_VERSION_SERIES_CHECKED_OUT);
      return checkout == null ? false : checkout;
   }

   /**
    * {@inheritDoc}
    */
   public void setContentStream(ContentStream contentStream) throws ConstraintException
   {
      if (type.getContentStreamAllowed() == ContentStreamAllowed.REQUIRED && contentStream == null)
      {
         throw new ConstraintException("Content stream required for object of type " + getTypeId()
            + ", it can't be null.");
      }
      if (type.getContentStreamAllowed() == ContentStreamAllowed.NOT_ALLOWED && contentStream != null)
      {
         throw new ConstraintException("Content stream not allowed for object of type " + getTypeId());
      }

      this.contentStream = contentStream;
   }

   protected void save() throws StorageException
   {
      String name = getName();
      if (name == null || name.length() == 0)
      {
         throw new NameConstraintViolationException("Object name may not be null or empty string.");
      }

      if (parent != null)
      {
         for (ItemsIterator<ObjectData> iterator = parent.getChildren(null); iterator.hasNext();)
         {
            ObjectData object = iterator.next();
            if (object.getObjectId().equals(getObjectId()))
            {
               continue;
            }
            if (name.equals(object.getName()))
            {
               throw new NameConstraintViolationException("Object with name " + name
                  + " already exists in parent folder.");
            }
         }
      }

      String id;
      boolean isNew = isNew();

      if (isNew)
      {
         id = StorageImpl.generateId();
         String vsId = StorageImpl.generateId();
         entry.setValue(CmisConstants.OBJECT_ID, new StringValue(id));
         entry.setValue(CmisConstants.OBJECT_TYPE_ID, new StringValue(getTypeId()));
         entry.setValue(CmisConstants.BASE_TYPE_ID, new StringValue(getBaseType().value()));
         entry.setValue(CmisConstants.CREATED_BY, new StringValue());
         entry.setValue(CmisConstants.CREATION_DATE, new DateValue(Calendar.getInstance()));
         entry.setValue(CmisConstants.VERSION_SERIES_ID, new StringValue(vsId));
         entry.setValue(CmisConstants.IS_LATEST_VERSION, new BooleanValue(true));
         entry.setValue(CmisConstants.IS_MAJOR_VERSION, new BooleanValue(versioningState == VersioningState.MAJOR));
         entry.setValue(CmisConstants.VERSION_LABEL, new StringValue(versioningState == VersioningState.CHECKEDOUT
            ? pwcLabel : latestLabel));
         entry.setValue(CmisConstants.IS_VERSION_SERIES_CHECKED_OUT, new BooleanValue(
            versioningState == VersioningState.CHECKEDOUT));
         if (versioningState == VersioningState.CHECKEDOUT)
         {
            entry.setValue(CmisConstants.VERSION_SERIES_CHECKED_OUT_ID, new StringValue(id));
            entry.setValue(CmisConstants.VERSION_SERIES_CHECKED_OUT_BY, new StringValue());
         }

         if (parent != null)
         {
            storage.children.get(parent.getObjectId()).add(id);

            Set<String> parents = new CopyOnWriteArraySet<String>();
            parents.add(parent.getObjectId());
            storage.parents.put(id, parents);
         }
         else
         {
            storage.unfiled.add(id);
            storage.parents.put(id, new CopyOnWriteArraySet<String>());
         }

         storage.properties.put(id, new ConcurrentHashMap<String, Value>());
         storage.policies.put(id, new CopyOnWriteArraySet<String>());
         storage.permissions.put(id, new ConcurrentHashMap<String, Set<String>>());
         List<String> versions = new CopyOnWriteArrayList<String>();
         storage.versions.put(vsId, versions);
         if (versioningState != VersioningState.CHECKEDOUT)
         {
            versions.add(id);
         }
         else
         {
            storage.workingCopies.put(vsId, id);
         }
      }
      else
      {
         id = getObjectId();
      }

      entry.setValue(CmisConstants.LAST_MODIFIED_BY, new StringValue());
      entry.setValue(CmisConstants.LAST_MODIFICATION_DATE, new DateValue(Calendar.getInstance()));
      entry.setValue(CmisConstants.CHANGE_TOKEN, new StringValue(StorageImpl.generateId()));

      if (contentStream != null)
      {
         try
         {
            byte[] content;
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            InputStream in = contentStream.getStream();
            if (in != null)
            {
               byte[] buf = new byte[1024];
               int r = -1;
               while ((r = in.read(buf)) != -1)
               {
                  bout.write(buf, 0, r);
               }

               content = bout.toByteArray();

               MimeType mimeType = contentStream.getMediaType();

               entry.setValue(CmisConstants.CONTENT_STREAM_MIME_TYPE, new StringValue(mimeType.getBaseType()));
               String charset = mimeType.getParameter(CmisConstants.CHARSET);
               if (charset != null)
               {
                  entry.setValue(CmisConstants.CHARSET, new StringValue(charset));
               }
               entry
                  .setValue(CmisConstants.CONTENT_STREAM_LENGTH, new IntegerValue(BigInteger.valueOf(content.length)));
            }
            else
            {
               entry.setValue(CmisConstants.CONTENT_STREAM_MIME_TYPE, new StringValue(""));
               entry.setValue(CmisConstants.CONTENT_STREAM_LENGTH, new IntegerValue(BigInteger.valueOf(0)));
               content = EMPTY_CONTENT;
            }

            storage.contents.put(id, content);
         }
         catch (IOException e)
         {
            throw new CmisRuntimeException("Unable add content for document. " + e.getMessage(), e);
         }
      }
      else if (isNew)
      {
         storage.contents.put(id, EMPTY_CONTENT);
      }

      contentStream = null;

      storage.properties.get(id).putAll(entry.getValues());
      storage.policies.get(id).addAll(entry.getPolicies());
      storage.permissions.get(id).putAll(entry.getPermissions());
   }

   protected void delete() throws ConstraintException, StorageException
   {
      ItemsIterator<RelationshipData> relationships = getRelationships(RelationshipDirection.EITHER, null, true);
      if (relationships.hasNext())
      {
         throw new ConstraintException("Object can't be deleted cause to storage referential integrity. "
            + "Object is source or target at least one Relationship.");
      }

      if (isPWC())
      {
         cancelCheckout();
      }
      else
      {
         String objectId = getObjectId();
         String vsId = getVersionSeriesId();
         storage.properties.remove(objectId);
         storage.policies.remove(objectId);
         storage.permissions.remove(objectId);
         storage.contents.remove(objectId);
         for (String parent : storage.parents.get(objectId))
         {
            storage.children.get(parent).remove(objectId);
         }
         storage.parents.remove(objectId);
         storage.unfiled.remove(objectId);
         storage.versions.get(vsId).remove(objectId);
      }
   }
}
