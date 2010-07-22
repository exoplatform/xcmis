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
import org.xcmis.spi.ContentStream;
import org.xcmis.spi.DocumentData;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.PolicyData;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.UpdateConflictException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.RelationshipDirection;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.utils.MimeType;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: DocumentDataImpl.java 1197 2010-05-28 08:15:37Z
 *          alexey.zavizionov@gmail.com $
 */
class DocumentDataImpl extends BaseObjectData implements DocumentData
{

   public DocumentDataImpl(Entry entry, TypeDefinition type, StorageImpl storage)
   {
      super(entry, type, storage);
   }

   /**
    * {@inheritDoc}
    */
   public void cancelCheckout() throws UpdateConflictException, VersioningException, StorageException
   {
      synchronized (storage)
      {
         String vsId = getVersionSeriesId();
         String pwcId = storage.workingCopies.get(vsId);
         if (pwcId == null)
         {
            return;
         }

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
            Entry ventry = storage.entries.get(version);
            ventry.setValue(CmisConstants.IS_VERSION_SERIES_CHECKED_OUT, new BooleanValue(false));
            ventry.setValue(CmisConstants.VERSION_SERIES_CHECKED_OUT_ID, /*new StringValue()*/null);
         }
      }
   }

   /**
    * {@inheritDoc}
    */
   public DocumentData checkin(boolean major, String checkinComment, Map<String, Property<?>> properties,
      ContentStream contentStream, List<AccessControlEntry> acl, Collection<PolicyData> policies)
      throws NameConstraintViolationException, UpdateConflictException, StorageException
   {
      if (!isPWC())
      {
         throw new CmisRuntimeException("Current object is not Private Working Copy.");
      }

      synchronized (storage)
      {
         String pwcId = getObjectId();
         int i = 1;
         for (Iterator<String> iterator = storage.versions.get(getVersionSeriesId()).iterator(); iterator.hasNext();)
         {
            String version = iterator.next();
            Entry ventry = storage.entries.get(version);
            ventry.setValue(CmisConstants.IS_VERSION_SERIES_CHECKED_OUT, new BooleanValue(false));
            ventry.setValue(CmisConstants.IS_LATEST_VERSION, new BooleanValue(false));
            ventry.setValue(CmisConstants.IS_LATEST_MAJOR_VERSION, new BooleanValue(false));
            ventry.setValue(CmisConstants.VERSION_SERIES_CHECKED_OUT_BY, /*new StringValue()*/null);
            ventry.setValue(CmisConstants.VERSION_SERIES_CHECKED_OUT_ID, /*new StringValue()*/null);
            // update version labels
            ventry.setValue(CmisConstants.VERSION_LABEL, new StringValue("" + i++));
         }

         if (properties != null)
         {
            for (Property<?> property : properties.values())
            {
               doSetProperty(property);
            }
         }

         try
         {
            if (contentStream != null)
            {
               doSetContentStream(contentStream);
            }
         }
         catch (IOException ioe)
         {
            throw new StorageException("Unable checkin PWC. " + ioe.getMessage(), ioe);
         }

         entry.setValue(CmisConstants.VERSION_LABEL, new StringValue(PropertyDefinitions.LATEST_LABEL));
         entry.setValue(CmisConstants.IS_LATEST_VERSION, new BooleanValue(true));
         entry.setValue(CmisConstants.IS_MAJOR_VERSION, new BooleanValue(major));
         entry.setValue(CmisConstants.IS_LATEST_MAJOR_VERSION, new BooleanValue(major));
         entry.setValue(CmisConstants.IS_VERSION_SERIES_CHECKED_OUT, new BooleanValue(false));
         entry.setValue(CmisConstants.VERSION_SERIES_CHECKED_OUT_ID, /*new StringValue()*/null);
         entry.setValue(CmisConstants.VERSION_SERIES_CHECKED_OUT_BY, /*new StringValue()*/null);
         if (checkinComment != null)
         {
            entry.setValue(CmisConstants.CHECKIN_COMMENT, new StringValue(checkinComment));
         }
         storage.workingCopies.remove(getVersionSeriesId());
         storage.versions.get(getVersionSeriesId()).add(pwcId);
      }
      return this;
   }

   /**
    * {@inheritDoc}
    */
   public DocumentData checkout() throws UpdateConflictException, VersioningException, StorageException
   {
      synchronized (storage)
      {
         if (storage.workingCopies.get(getVersionSeriesId()) != null)
         {
            throw new VersioningException("Version series already checked-out. "
               + "Not allowed have more then one PWC for version series at a time.");
         }

         Entry pwc = new Entry();
         pwc.setValue(CmisConstants.OBJECT_TYPE_ID, new StringValue(type.getId()));
         pwc.setValue(CmisConstants.BASE_TYPE_ID, new StringValue(type.getBaseId().value()));
         pwc.setValue(CmisConstants.VERSION_SERIES_ID, new StringValue(this.getVersionSeriesId()));
         String pwcId = StorageImpl.generateId();
         pwc.setValue(CmisConstants.OBJECT_ID, new StringValue(pwcId));
         pwc.setValue(CmisConstants.NAME, new StringValue(getName()));
         String userId = storage.getCurrentUser();
         pwc.setValue(CmisConstants.CREATED_BY, new StringValue(userId));
         pwc.setValue(CmisConstants.LAST_MODIFIED_BY, new StringValue(userId));
         Calendar cal = Calendar.getInstance();
         pwc.setValue(CmisConstants.CREATION_DATE, new DateValue(cal));
         pwc.setValue(CmisConstants.LAST_MODIFICATION_DATE, new DateValue(cal));
         pwc.setValue(CmisConstants.IS_LATEST_VERSION, new BooleanValue(false));
         pwc.setValue(CmisConstants.IS_MAJOR_VERSION, new BooleanValue(false));
         pwc.setValue(CmisConstants.VERSION_LABEL, new StringValue(PropertyDefinitions.PWC_LABEL));
         pwc.setValue(CmisConstants.IS_VERSION_SERIES_CHECKED_OUT, new BooleanValue(true));
         pwc.setValue(CmisConstants.VERSION_SERIES_CHECKED_OUT_ID, new StringValue(pwcId));
         pwc.setValue(CmisConstants.VERSION_SERIES_CHECKED_OUT_BY, new StringValue(userId));

         ByteArrayValue contentValue = (ByteArrayValue)entry.getValue(PropertyDefinitions.CONTENT);
         if (contentValue != null)
         {
            // check is max memory size reached
            byte[] src = contentValue.getBytes();
            storage.validateMemSize(src);

            byte[] bytes = new byte[src.length];
            System.arraycopy(src, 0, bytes, 0, bytes.length);
            pwc.setValue(PropertyDefinitions.CONTENT, new ByteArrayValue(bytes));

            String mimeType = getContentStreamMimeType();
            if (mimeType != null)
            {
               pwc.setValue(CmisConstants.CONTENT_STREAM_MIME_TYPE, new StringValue(mimeType));
            }

            String charset = getString(CmisConstants.CHARSET);
            if (charset != null)
            {
               entry.setValue(CmisConstants.CHARSET, new StringValue(charset));
            }

            entry.setValue(CmisConstants.CONTENT_STREAM_LENGTH, new IntegerValue(BigInteger.valueOf(bytes.length)));
         }

         for (Iterator<String> iterator = storage.versions.get(getVersionSeriesId()).iterator(); iterator.hasNext();)
         {
            String version = iterator.next();
            Entry ventry = storage.entries.get(version);
            ventry.setValue(CmisConstants.IS_VERSION_SERIES_CHECKED_OUT, new BooleanValue(true));
            ventry.setValue(CmisConstants.VERSION_SERIES_CHECKED_OUT_ID, new StringValue(pwcId));
            ventry.setValue(CmisConstants.VERSION_SERIES_CHECKED_OUT_BY, new StringValue(userId));
         }

         storage.entries.put(pwcId, pwc);
         for (String parent : storage.parents.get(getObjectId()))
         {
            storage.children.get(parent).add(pwcId);
         }
         storage.parents.put(pwcId, new CopyOnWriteArraySet<String>(storage.parents.get(getObjectId())));

         storage.workingCopies.put(getVersionSeriesId(), pwcId);

         DocumentDataImpl pwcObject =
            new DocumentDataImpl(pwc, //
               new TypeDefinition(type.getId(), type.getBaseId(), type.getQueryName(), type.getLocalName(), type
                  .getLocalNamespace(), type.getParentId(), type.getDisplayName(), type.getDescription(), type
                  .isCreatable(), type.isFileable(), type.isQueryable(), type.isFulltextIndexed(), type
                  .isIncludedInSupertypeQuery(), type.isControllablePolicy(), type.isControllableACL(), type
                  .isVersionable(), type.getAllowedSourceTypes(), type.getAllowedTargetTypes(), type
                  .getContentStreamAllowed(), PropertyDefinitions.getAll(getTypeId())), //
               storage);

         return pwcObject;
      }
   }

   /**
    * {@inheritDoc}
    */
   public ContentStream getContentStream()
   {
      ByteArrayValue contentValue = (ByteArrayValue)entry.getValue(PropertyDefinitions.CONTENT);
      if (contentValue != null/* && contentValue.getBytes().length > 0*/)
      {
         MimeType mimeType = MimeType.fromString(getString(CmisConstants.CONTENT_STREAM_MIME_TYPE));
         String charset = getString(CmisConstants.CHARSET);
         if (charset != null)
         {
            mimeType.getParameters().put(CmisConstants.CHARSET, charset);
         }
         return new BaseContentStream(contentValue.getBytes(), getName(), mimeType);
      }
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public ContentStream getContentStream(String streamId)
   {
      if (streamId == null || streamId.equals(getString(CmisConstants.CONTENT_STREAM_ID)))
      {
         return getContentStream();
      }
      try
      {
         if (storage.renditionManager != null)
         {
            return storage.renditionManager.getStream(this, streamId);
         }
         return null;
      }
      catch (Exception e)
      {
         throw new CmisRuntimeException("Unable get rendition stream. " + e.getMessage(), e);
      }
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
      ByteArrayValue cv = (ByteArrayValue)entry.getValue(PropertyDefinitions.CONTENT);
      return cv != null && cv.getBytes().length > 0;
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
   public void setContentStream(ContentStream contentStream) throws IOException, UpdateConflictException,
      VersioningException, StorageException
   {
      doSetContentStream(contentStream);
      save();
   }

   private void doSetContentStream(ContentStream contentStream) throws IOException, StorageException
   {
      if (contentStream == null || contentStream.getStream() == null)
      {
         entry.setValue(PropertyDefinitions.CONTENT, /*new ByteArrayValue(new byte[0])*/null);
         entry.setValue(CmisConstants.CONTENT_STREAM_MIME_TYPE, /*new StringValue()*/null);
         entry.setValue(CmisConstants.CHARSET, /*new StringValue()*/null);
         entry.setValue(CmisConstants.CONTENT_STREAM_LENGTH, new IntegerValue(BigInteger.valueOf(0)));
      }
      else
      {
         ByteArrayValue cv = ByteArrayValue.fromStream(contentStream.getStream());
         storage.validateMemSize(cv.getBytes());

         MimeType mimeType = contentStream.getMediaType();
         entry.setValue(PropertyDefinitions.CONTENT, cv);
         entry.setValue(CmisConstants.CONTENT_STREAM_MIME_TYPE, new StringValue(mimeType.getBaseType()));
         String charset = mimeType.getParameter(CmisConstants.CHARSET);
         if (charset != null)
         {
            entry.setValue(CmisConstants.CHARSET, new StringValue(charset));
         }
         entry
            .setValue(CmisConstants.CONTENT_STREAM_LENGTH, new IntegerValue(BigInteger.valueOf(cv.getBytes().length)));
      }
   }

   protected void delete() throws StorageException, UpdateConflictException, VersioningException
   {
      ItemsIterator<RelationshipData> relationships = getRelationships(RelationshipDirection.EITHER, null, true);
      if (relationships.hasNext())
      {
         throw new StorageException("Object can't be deleted cause to storage referential integrity. "
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
         storage.entries.remove(objectId);
         for (String parent : storage.parents.get(objectId))
         {
            storage.children.get(parent).remove(objectId);
         }
         storage.parents.remove(objectId);
         storage.unfiled.remove(objectId);
         for (String version : storage.versions.get(getVersionSeriesId()))
         {
            storage.entries.remove(version);
         }
         storage.versions.remove(vsId);
         String pwcId = storage.workingCopies.remove(vsId);
         if (pwcId != null)
         {
            storage.entries.remove(pwcId);
         }
      }
   }
}
