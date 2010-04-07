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

import org.xcmis.spi.CMIS;
import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.data.BaseContentStream;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.Document;
import org.xcmis.spi.data.Folder;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.data.Relationship;
import org.xcmis.spi.model.ContentStreamAllowed;
import org.xcmis.spi.model.RelationshipDirection;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.VersioningState;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
class DocumentImpl extends BaseObjectData implements Document
{

   static String latestLabel = "latest";

   static String pwcLabel = "pwc";

   static byte[] EMPTY_CONTENT = new byte[0];

   private ContentStream contentStream;

   protected final VersioningState versioningState;

   public DocumentImpl(Entry entry, TypeDefinition type, StorageImpl storage)
   {
      super(entry, type, storage);
      this.versioningState = null;
   }

   public DocumentImpl(Folder parent, TypeDefinition type, VersioningState versioningState, StorageImpl storage)
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
            props.put(CMIS.IS_VERSION_SERIES_CHECKED_OUT, new BooleanValue(false));
            props.put(CMIS.VERSION_SERIES_CHECKED_OUT_ID, new StringValue());
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public Document checkin(boolean major, String checkinComment) throws ConstraintException, StorageException
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
            props.put(CMIS.IS_VERSION_SERIES_CHECKED_OUT, new BooleanValue(false));
            props.put(CMIS.VERSION_SERIES_CHECKED_OUT_ID, new StringValue());
            props.put(CMIS.IS_LATEST_VERSION, new BooleanValue(false));
            // update version labels
            props.put(CMIS.VERSION_LABEL, new StringValue("" + i++));
         }

         entry.setValue(CMIS.IS_LATEST_VERSION, new BooleanValue(true));
         entry.setValue(CMIS.VERSION_LABEL, new StringValue(latestLabel));
         entry.setValue(CMIS.IS_MAJOR_VERSION, new BooleanValue(major));
         if (checkinComment != null)
         {
            entry.setValue(CMIS.CHECKIN_COMMENT, new StringValue(checkinComment));
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
   public Document checkout() throws ConstraintException, VersioningException, StorageException
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
         pwc.setValue(CMIS.OBJECT_ID, new StringValue(pwcId));
         pwc.setValue(CMIS.CREATED_BY, new StringValue());
         pwc.setValue(CMIS.CREATION_DATE, new DateValue(Calendar.getInstance()));
         pwc.setValue(CMIS.IS_LATEST_VERSION, new BooleanValue(false));
         pwc.setValue(CMIS.IS_MAJOR_VERSION, new BooleanValue(false));
         pwc.setValue(CMIS.VERSION_LABEL, new StringValue(pwcLabel));
         pwc.setValue(CMIS.IS_VERSION_SERIES_CHECKED_OUT, new BooleanValue(true));
         pwc.setValue(CMIS.VERSION_SERIES_CHECKED_OUT_ID, new StringValue(pwcId));
         pwc.setValue(CMIS.VERSION_SERIES_CHECKED_OUT_BY, new StringValue());

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
            props.put(CMIS.IS_VERSION_SERIES_CHECKED_OUT, new BooleanValue(true));
            props.put(CMIS.VERSION_SERIES_CHECKED_OUT_ID, new StringValue(pwcId));
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

         return new DocumentImpl(pwc, storage.getTypeDefinition(getTypeId(), true), storage);
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
         return new BaseContentStream(bytes, getName(), getString(CMIS.CONTENT_STREAM_FILE_NAME));
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

      if (streamId == null || streamId.equals(getString(CMIS.CONTENT_STREAM_ID)))
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
      return getString(CMIS.CONTENT_STREAM_MIME_TYPE);
   }

   /**
    * {@inheritDoc}
    */
   public String getVersionLabel()
   {
      return getString(CMIS.VERSION_LABEL);
   }

   /**
    * {@inheritDoc}
    */
   public String getVersionSeriesCheckedOutBy()
   {
      return getString(CMIS.VERSION_SERIES_CHECKED_OUT_BY);
   }

   /**
    * {@inheritDoc}
    */
   public String getVersionSeriesCheckedOutId()
   {
      return getString(CMIS.VERSION_SERIES_CHECKED_OUT_ID);
   }

   /**
    * {@inheritDoc}
    */
   public String getVersionSeriesId()
   {
      return getString(CMIS.VERSION_SERIES_ID);
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

      return storage.contents.get(getObjectId()) != EMPTY_CONTENT;
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
      Boolean latest = getBoolean(CMIS.IS_LATEST_VERSION);
      return latest == null ? true : latest;
   }

   /**
    * {@inheritDoc}
    */
   public boolean isMajorVersion()
   {
      Boolean major = getBoolean(CMIS.IS_MAJOR_VERSION);
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
      Boolean checkout = getBoolean(CMIS.IS_VERSION_SERIES_CHECKED_OUT);
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

      if (isNew())
      {
         id = StorageImpl.generateId();
         String vsId = StorageImpl.generateId();
         entry.setValue(CMIS.OBJECT_ID, new StringValue(id));
         entry.setValue(CMIS.OBJECT_TYPE_ID, new StringValue(getTypeId()));
         entry.setValue(CMIS.BASE_TYPE_ID, new StringValue(getBaseType().value()));
         entry.setValue(CMIS.CREATED_BY, new StringValue());
         entry.setValue(CMIS.CREATION_DATE, new DateValue(Calendar.getInstance()));
         entry.setValue(CMIS.VERSION_SERIES_ID, new StringValue(vsId));
         entry.setValue(CMIS.IS_LATEST_VERSION, new BooleanValue(true));
         entry.setValue(CMIS.IS_MAJOR_VERSION, new BooleanValue(versioningState == VersioningState.MAJOR));
         entry.setValue(CMIS.VERSION_LABEL, new StringValue(versioningState == VersioningState.CHECKEDOUT ? pwcLabel
            : latestLabel));
         entry.setValue(CMIS.IS_VERSION_SERIES_CHECKED_OUT, new BooleanValue(
            versioningState == VersioningState.CHECKEDOUT));
         if (versioningState == VersioningState.CHECKEDOUT)
         {
            entry.setValue(CMIS.VERSION_SERIES_CHECKED_OUT_ID, new StringValue(id));
            entry.setValue(CMIS.VERSION_SERIES_CHECKED_OUT_BY, new StringValue());
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

      entry.setValue(CMIS.LAST_MODIFIED_BY, new StringValue());
      entry.setValue(CMIS.LAST_MODIFICATION_DATE, new DateValue(Calendar.getInstance()));
      entry.setValue(CMIS.CHANGE_TOKEN, new StringValue(StorageImpl.generateId()));

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

               String mediaType = contentStream.getMediaType();
               if (mediaType == null)
               {
                  mediaType = "application/octet-stream";
               }
               entry.setValue(CMIS.CONTENT_STREAM_MIME_TYPE, new StringValue(mediaType));
            }
            else
            {
               content = EMPTY_CONTENT;
            }

            storage.contents.put(id, content);
         }
         catch (IOException e)
         {
            throw new CmisRuntimeException("Unable add content for document. " + e.getMessage(), e);
         }
      }
      else if (isNew())
      {
         storage.contents.put(id, EMPTY_CONTENT);
      }

      storage.properties.get(id).putAll(entry.getValues());
      storage.policies.get(id).addAll(entry.getPolicies());
      storage.permissions.get(id).putAll(entry.getPermissions());
   }

   protected void delete() throws ConstraintException, StorageException
   {
      ItemsIterator<Relationship> relationships = getRelationships(RelationshipDirection.EITHER, null, true);
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
