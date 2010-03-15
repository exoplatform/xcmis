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

package org.xcmis.spi.data;

import org.xcmis.spi.CmisRuntimeException;
import org.xcmis.spi.StorageException;

import java.util.Collection;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface DocumentData extends ObjectData
{
   String getVersionLabel();

   String getVersionSeriesCheckedOutBy();

   String getVersionSeriesCheckedOutId();

   String getVersionSeriesId();

   boolean isLatestMajorVersion();

   boolean isLatestVersion();

   boolean isMajorVersion();

   boolean isVersionSeriesCheckedOut();

   //
   ContentStream getContentStream();

   void setContentStream(ContentStream contentStream);
   
   boolean hasContent();

   //
   void cancelCheckout() throws StorageException, CmisRuntimeException;

   DocumentData checkin(boolean major, String checkinComment) throws StorageException, CmisRuntimeException;

   DocumentData checkout() throws StorageException, CmisRuntimeException;

   //
   Collection<DocumentData> getAllVersions() throws CmisRuntimeException;

}
