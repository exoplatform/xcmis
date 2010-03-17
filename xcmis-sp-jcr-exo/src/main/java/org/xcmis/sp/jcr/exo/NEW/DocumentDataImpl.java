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

package org.xcmis.sp.jcr.exo.NEW;

import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.TypeDefinition;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.DocumentData;

import javax.jcr.Node;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class DocumentDataImpl extends AbstractObjectData implements DocumentData
{

   public DocumentDataImpl(Node node, TypeDefinition type)
   {
      super(node, type);
   }

   public void cancelCheckout() throws StorageException
   {
      // TODO Auto-generated method stub

   }

   public DocumentData checkin(boolean major, String checkinComment) throws ConstraintException, StorageException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public DocumentData checkout() throws ConstraintException, VersioningException, StorageException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public ContentStream getContentStream()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getContentStreamMimeType()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getVersionLabel()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getVersionSeriesCheckedOutBy()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getVersionSeriesCheckedOutId()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getVersionSeriesId()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public boolean hasContent()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isLatestMajorVersion()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isLatestVersion()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isMajorVersion()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isPWC()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isVersionSeriesCheckedOut()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public void setContentStream(ContentStream contentStream) throws ConstraintException
   {
      // TODO Auto-generated method stub

   }

}
