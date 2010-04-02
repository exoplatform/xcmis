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
package org.xcmis.spi.model.impl;

import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.ObjectInfo;

import java.util.Calendar;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey
 *         Zavizionov</a>
 * @version $Id: ObjectInfoImpl.java 34360 2009-07-22 23:58:59Z sunman $
 * 
 */
public class ObjectInfoImpl implements ObjectInfo
{

   // Common

   private BaseType baseType;

   private String typeId;

   private String id;

   private String name;

   private String createdBy;

   private Calendar creationDate;

   private String lastModifiedBy;

   private Calendar lastModificationDate;

   private String changeToken;

   // Folder

   private String parentId;

   // Document

   private Boolean latestVersion;

   private Boolean majorVersion;

   private Boolean latestMajorVersion;

   private String versionSeriesId;

   private String versionSeriesCheckedOutId;

   private String versionSeriesCheckedOutBy;

   private String versionLabel;

   private String contentStreamMimeType;

   // Relationship

   private String sourceId;

   private String targetId;

   public ObjectInfoImpl()
   {
   }

   // Common

   public ObjectInfoImpl(BaseType baseType, String typeId, String id, String name, String createdBy,
      Calendar creationDate, String lastModifiedBy, Calendar lastModificationDate, String changeToken, String parentId,
      Boolean latestVersion, Boolean majorVersion, Boolean latestMajorVersion, String versionSeriesId,
      String versionSeriesCheckedOutId, String versionSeriesCheckedOutBy, String versionLabel,
      String contentStreamMimeType, String sourceId, String targetId)
   {
      this.baseType = baseType;
      this.typeId = typeId;
      this.id = id;
      this.name = name;
      this.createdBy = createdBy;
      this.creationDate = creationDate;
      this.lastModifiedBy = lastModifiedBy;
      this.lastModificationDate = lastModificationDate;
      this.changeToken = changeToken;
      this.parentId = parentId;
      this.latestVersion = latestVersion;
      this.majorVersion = majorVersion;
      this.latestMajorVersion = latestMajorVersion;
      this.versionSeriesId = versionSeriesId;
      this.versionSeriesCheckedOutId = versionSeriesCheckedOutId;
      this.versionSeriesCheckedOutBy = versionSeriesCheckedOutBy;
      this.versionLabel = versionLabel;
      this.contentStreamMimeType = contentStreamMimeType;
      this.sourceId = sourceId;
      this.targetId = targetId;
   }

   /**
    * {@inheritDoc}
    */
   public BaseType getBaseType()
   {
      return baseType;
   }

   /**
    * {@inheritDoc}
    */
   public String getTypeId()
   {
      return typeId;
   }

   /**
    * {@inheritDoc}
    */
   public String getId()
   {
      return id;
   }

   /**
    * {@inheritDoc}
    */
   public String getName()
   {
      return name;
   }

   /**
    * {@inheritDoc}
    */
   public String getCreatedBy()
   {
      return createdBy;
   }

   /**
    * {@inheritDoc}
    */
   public Calendar getCreationDate()
   {
      return creationDate;
   }

   /**
    * {@inheritDoc}
    */
   public String getLastModifiedBy()
   {
      return lastModifiedBy;
   }

   /**
    * {@inheritDoc}
    */
   public Calendar getLastModificationDate()
   {
      return lastModificationDate;
   }

   /**
    * {@inheritDoc}
    */
   public String getChangeToken()
   {
      return changeToken;
   }

   // Folder

   /**
    * {@inheritDoc}
    */
   public String getParentId()
   {
      return parentId;
   }

   // Document

   /**
    * {@inheritDoc}
    */
   public Boolean isLatestVersion()
   {
      return latestVersion;
   }

   /**
    * {@inheritDoc}
    */
   public Boolean isMajorVersion()
   {
      return majorVersion;
   }

   /**
    * {@inheritDoc}
    */
   public Boolean isLatestMajorVersion()
   {
      return latestMajorVersion;
   }

   /**
    * {@inheritDoc}
    */
   public String getVersionSeriesId()
   {
      return versionSeriesId;
   }

   /**
    * {@inheritDoc}
    */
   public String getVersionSeriesCheckedOutId()
   {
      return versionSeriesCheckedOutId;
   }

   public String getVersionSeriesCheckedOutBy()
   {
      return versionSeriesCheckedOutBy;
   }

   /**
    * {@inheritDoc}
    */
   public String getVersionLabel()
   {
      return versionLabel;
   }

   /**
    * {@inheritDoc}
    */
   public String getContentStreamMimeType()
   {
      return contentStreamMimeType;
   }

   // Relationship

   /**
    * {@inheritDoc}
    */
   public String getSourceId()
   {
      return sourceId;
   }

   /**
    * {@inheritDoc}
    */
   public String getTargetId()
   {
      return targetId;
   }

   // ------------------- Setters --------------------------

   // Common

   public void setBaseType(BaseType baseType)
   {
      this.baseType = baseType;
   }

   public void setTypeId(String typeId)
   {
      this.typeId = typeId;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public void setCreatedBy(String createdBy)
   {
      this.createdBy = createdBy;
   }

   public void setCreationDate(Calendar creationDate)
   {
      this.creationDate = creationDate;
   }

   public void setLastModifiedBy(String lastModifiedBy)
   {
      this.lastModifiedBy = lastModifiedBy;
   }

   public void setLastModificationDate(Calendar lastModificationDate)
   {
      this.lastModificationDate = lastModificationDate;
   }

   public void setChangeToken(String changeToken)
   {
      this.changeToken = changeToken;
   }

   // Folder

   public void setParentId(String parentId)
   {
      this.parentId = parentId;
   }

   // Document

   public void setLatestVersion(Boolean latestVersion)
   {
      this.latestVersion = latestVersion;
   }

   public void setMajorVersion(Boolean majorVersion)
   {
      this.majorVersion = majorVersion;
   }

   public void setLatestMajorVersion(Boolean latestMajorVersion)
   {
      this.latestMajorVersion = latestMajorVersion;
   }

   public void setVersionSeriesId(String versionSeriesId)
   {
      this.versionSeriesId = versionSeriesId;
   }

   public void setVersionSeriesCheckedOutId(String versionSeriesCheckedOutId)
   {
      this.versionSeriesCheckedOutId = versionSeriesCheckedOutId;
   }

   public void setVersionSeriesCheckedOutBy(String versionSeriesCheckedOutBy)
   {
      this.versionSeriesCheckedOutBy = versionSeriesCheckedOutBy;
   }

   public void setVersionLabel(String versionLabel)
   {
      this.versionLabel = versionLabel;
   }

   public void setContentStreamMimeType(String contentStreamMimeType)
   {
      this.contentStreamMimeType = contentStreamMimeType;
   }

   // Relationship

   public void setSourceId(String sourceId)
   {
      this.sourceId = sourceId;
   }

   public void setTargetId(String targetId)
   {
      this.targetId = targetId;
   }

}
