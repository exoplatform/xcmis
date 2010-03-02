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
package org.xcmis.spi.object;

import java.util.GregorianCalendar;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id: ObjectInfoImpl.java 34360 2009-07-22 23:58:59Z sunman $
 *
 */
public class ObjectInfoImpl implements ObjectInfo
{

   private String baseTypeId;

   private String contentStreamFileName;

   private String contentStreamId;

   private String createdBy;

   private GregorianCalendar creationDate;

   private String id;

   private boolean latestMajorVersion;

   private boolean latestVersion;

   private boolean majorVersion;

   private boolean versionSeriesCheckedOut;

   private GregorianCalendar lastModificationDate;

   private String lastModifiedBy;

   private String name;

   private String objectTypeId;

   private String parentId;

   private String versionLabel;

   private String versionSeriesId;

   private String contentStreamMimeType;

   /**
    * @see org.xcmis.spi.object.ObjectInfo#getBaseTypeId()
    */
   public String getBaseTypeId()
   {
      return baseTypeId;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#getContentStreamFileName()
    */
   public String getContentStreamFileName()
   {
      return contentStreamFileName;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#getContentStreamId()
    */
   public String getContentStreamId()
   {
      return contentStreamId;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#getCreatedBy()
    */
   public String getCreatedBy()
   {
      return createdBy;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#getCreationDate()
    */
   public GregorianCalendar getCreationDate()
   {
      return creationDate;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#getId()
    */
   public String getId()
   {
      return id;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#getLastModificationDate()
    */
   public GregorianCalendar getLastModificationDate()
   {
      return lastModificationDate;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#getLastModifiedBy()
    */
   public String getLastModifiedBy()
   {
      return lastModifiedBy;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#getName()
    */
   public String getName()
   {
      return name;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#getObjectTypeId()
    */
   public String getObjectTypeId()
   {
      return objectTypeId;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#getParentId()
    */
   public String getParentId()
   {
      return parentId;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#getVersionLabel()
    */
   public String getVersionLabel()
   {
      return versionLabel;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#getVersionSeriesId()
    */
   public String getVersionSeriesId()
   {
      return versionSeriesId;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#isLatestMajorVersion()
    */
   public boolean isLatestMajorVersion()
   {
      return latestMajorVersion;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#isLatestVersion()
    */
   public boolean isLatestVersion()
   {
      return latestVersion;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#isMajorVersion()
    */
   public boolean isMajorVersion()
   {
      return majorVersion;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#isVersionSeriesCheckedOut()
    */
   public boolean isVersionSeriesCheckedOut()
   {
      return versionSeriesCheckedOut;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#setBaseTypeId(java.lang.String)
    */
   public void setBaseTypeId(String baseTypeId)
   {
      this.baseTypeId = baseTypeId;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#setContentStreamFileName(java.lang.String)
    */
   public void setContentStreamFileName(String contentStreamFileName)
   {
      this.contentStreamFileName = contentStreamFileName;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#setContentStreamId(java.lang.String)
    */
   public void setContentStreamId(String contentStreamId)
   {
      this.contentStreamId = contentStreamId;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#setCreatedBy(java.lang.String)
    */
   public void setCreatedBy(String createdBy)
   {
      this.createdBy = createdBy;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#setCreationDate(java.util.GregorianCalendar)
    */
   public void setCreationDate(GregorianCalendar creationDate)
   {
      this.creationDate = creationDate;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#setId(java.lang.String)
    */
   public void setId(String id)
   {
      this.id = id;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#setIsLatestMajorVersion(boolean)
    */
   public void setIsLatestMajorVersion(boolean latestMajorVersion)
   {
      this.latestMajorVersion = latestMajorVersion;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#setIsLatestVersion(boolean)
    */
   public void setIsLatestVersion(boolean latestVersion)
   {
      this.latestVersion = latestVersion;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#setIsMajorVersion(boolean)
    */
   public void setIsMajorVersion(boolean majorVersion)
   {
      this.majorVersion = majorVersion;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#setIsVersionSeriesCheckedOut(boolean)
    */
   public void setIsVersionSeriesCheckedOut(boolean versionSeriesCheckedOut)
   {
      this.versionSeriesCheckedOut = versionSeriesCheckedOut;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#setLastModificationDate(java.util.GregorianCalendar)
    */
   public void setLastModificationDate(GregorianCalendar lastModificationDate)
   {
      this.lastModificationDate = lastModificationDate;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#setLastModifiedBy(java.lang.String)
    */
   public void setLastModifiedBy(String lastModifiedBy)
   {
      this.lastModifiedBy = lastModifiedBy;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#setName(java.lang.String)
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#setObjectTypeId(java.lang.String)
    */
   public void setObjectTypeId(String objectTypeId)
   {
      this.objectTypeId = objectTypeId;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#setParentId(java.lang.String)
    */
   public void setParentId(String parentId)
   {
      this.parentId = parentId;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#setVersionLabel(java.lang.String)
    */
   public void setVersionLabel(String versionLabel)
   {
      this.versionLabel = versionLabel;
   }

   /**
    * @see org.xcmis.spi.object.ObjectInfo#setVersionSeriesId(java.lang.String)
    */
   public void setVersionSeriesId(String versionSeriesId)
   {
      this.versionSeriesId = versionSeriesId;
   }
   
   public String getContentStreamMimeType()
   {
      return contentStreamMimeType;
   }
   
   public void setContentStreamMimeType(String contentStreamMimeType)
   {
      this.contentStreamMimeType = contentStreamMimeType;
   }

}
