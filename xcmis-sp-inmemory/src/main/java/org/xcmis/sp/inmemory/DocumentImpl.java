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
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.data.BaseContentStream;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.Document;
import org.xcmis.spi.data.Folder;
import org.xcmis.spi.model.ContentStreamAllowed;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.VersioningState;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Set;
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
      // TODO Auto-generated method stub

   }

   public Document checkin(boolean major, String checkinComment) throws ConstraintException, StorageException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Document checkout() throws ConstraintException, VersioningException, StorageException
   {
      // TODO Auto-generated method stub
      return null;
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

      byte[] bytes = storage.contents.get(entry.getId());
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

      return storage.contents.get(entry.getId()) != null;
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

      return entry.getId().equals(getVersionSeriesCheckedOutId());
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

   @Override
   protected void save() throws StorageException
   {
      String name = getName();
      if (name == null || name.length() == 0)
      {
         throw new NameConstraintViolationException("Object name may noy be null or empty string.");
      }

      if (isNew())
      {
         String id = StorageImpl.generateId();
         entry.setValue(CMIS.OBJECT_ID, //
            new StringValue(id));

         entry.setValue(CMIS.CREATED_BY, //
            new StringValue(""));
         entry.setValue(CMIS.CREATION_DATE, //
            new DateValue(Calendar.getInstance()));
         entry.setValue(CMIS.VERSION_SERIES_ID, //
            new StringValue(StorageImpl.generateId()));
         entry.setValue(CMIS.IS_LATEST_VERSION, //
            new BooleanValue(true));
         entry.setValue(CMIS.IS_MAJOR_VERSION, //
            new BooleanValue(versioningState == VersioningState.MAJOR));
         entry.setValue(CMIS.VERSION_LABEL, //
            new StringValue(versioningState == VersioningState.CHECKEDOUT ? pwcLabel : latestLabel));
         entry.setValue(CMIS.IS_VERSION_SERIES_CHECKED_OUT, //
            new BooleanValue(versioningState == VersioningState.CHECKEDOUT));
         if (versioningState == VersioningState.CHECKEDOUT)
         {
            entry.setValue(CMIS.VERSION_SERIES_CHECKED_OUT_ID, //
               new StringValue(id));
            entry.setValue(CMIS.VERSION_SERIES_CHECKED_OUT_BY, //
               new StringValue(""));
         }

         storage.children.get(parent.getObjectId()).add(id);

         Set<String> parents = new CopyOnWriteArraySet<String>();
         parents.add(parent.getObjectId());
         storage.parents.put(id, parents);
      }
      entry.setValue(CMIS.LAST_MODIFIED_BY, //
         new StringValue(""));
      entry.setValue(CMIS.LAST_MODIFICATION_DATE, //
         new DateValue(Calendar.getInstance()));
      entry.setValue(CMIS.CHANGE_TOKEN, //
         new StringValue(StorageImpl.generateId()));

      byte[] content;

      if (contentStream != null)
      {
         try
         {
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
         }
         catch (IOException e)
         {
            throw new CmisRuntimeException("Unable add content for document. " + e.getMessage(), e);
         }
      }
      else
      {
         content = EMPTY_CONTENT;
      }
      storage.contents.put(entry.getId(), content);
      storage.entries.put(entry.getId(), entry);
   }
}
