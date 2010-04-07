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
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.Document;
import org.xcmis.spi.data.Folder;
import org.xcmis.spi.data.ObjectData;
import org.xcmis.spi.model.TypeDefinition;
import org.xcmis.spi.model.VersioningState;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
class DocumentCopy extends DocumentImpl
{

   private final Document source;

   public DocumentCopy(Document source, Folder parent, TypeDefinition type, VersioningState versioningState,
      StorageImpl storage)
   {
      super(parent, type, versioningState, storage);
      this.source = source;
   }

   protected void save() throws StorageException
   {
      String name = getName();
      if (name == null || name.length() == 0)
      {
         throw new NameConstraintViolationException("Object name may noy be null or empty string.");
      }

      if (parent != null)
      {
         for (ItemsIterator<ObjectData> iterator = parent.getChildren(null); iterator.hasNext();)
         {
            if (name.equals(iterator.next().getName()))
            {
               throw new NameConstraintViolationException("Object with name " + name
                  + " already exists in parent folder.");
            }
         }
      }

      String id = StorageImpl.generateId();
      entry.setValue(CMIS.OBJECT_ID, new StringValue(id));
      entry.setValue(CMIS.OBJECT_TYPE_ID, new StringValue(getTypeId()));
      entry.setValue(CMIS.BASE_TYPE_ID, new StringValue(getBaseType().value()));
      entry.setValue(CMIS.CREATED_BY, new StringValue(""));
      entry.setValue(CMIS.CREATION_DATE, new DateValue(Calendar.getInstance()));
      entry.setValue(CMIS.VERSION_SERIES_ID, new StringValue(StorageImpl.generateId()));
      entry.setValue(CMIS.IS_LATEST_VERSION, new BooleanValue(true));
      entry.setValue(CMIS.IS_MAJOR_VERSION, new BooleanValue(versioningState == VersioningState.MAJOR));
      entry.setValue(CMIS.VERSION_LABEL, new StringValue(versioningState == VersioningState.CHECKEDOUT ? pwcLabel
         : latestLabel));
      entry.setValue(CMIS.IS_VERSION_SERIES_CHECKED_OUT,
         new BooleanValue(versioningState == VersioningState.CHECKEDOUT));
      entry.setValue(CMIS.LAST_MODIFIED_BY, new StringValue(""));
      entry.setValue(CMIS.LAST_MODIFICATION_DATE, new DateValue(Calendar.getInstance()));
      entry.setValue(CMIS.CHANGE_TOKEN, new StringValue(StorageImpl.generateId()));
      if (versioningState == VersioningState.CHECKEDOUT)
      {
         entry.setValue(CMIS.VERSION_SERIES_CHECKED_OUT_ID, new StringValue(id));
         entry.setValue(CMIS.VERSION_SERIES_CHECKED_OUT_BY, new StringValue(""));
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

      // TODO : copy the other properties from source.

      ContentStream contentStream = source.getContentStream();

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

      storage.contents.put(id, content);
      storage.properties.get(id).putAll(entry.getValues());
      storage.policies.get(id).addAll(entry.getPolicies());
      storage.permissions.get(id).putAll(entry.getPermissions());
   }
}
