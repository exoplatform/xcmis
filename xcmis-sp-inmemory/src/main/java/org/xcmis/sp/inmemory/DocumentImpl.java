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
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.Document;
import org.xcmis.spi.model.ContentStreamAllowed;
import org.xcmis.spi.model.TypeDefinition;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
class DocumentImpl extends BaseObjectData implements Document
{

   private ContentStream contentStream;

   public DocumentImpl(Entry entry, TypeDefinition type, StorageImpl storage)
   {
      super(entry, type, storage);
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

      ByteArrayContentStream content = storage.contents.get(entry.getId());
      return content;
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
      // TODO
   }

}
