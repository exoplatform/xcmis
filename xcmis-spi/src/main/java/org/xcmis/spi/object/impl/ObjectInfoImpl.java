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
package org.xcmis.spi.object.impl;

import org.xcmis.spi.object.ObjectInfo;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey
 *         Zavizionov</a>
 * @version $Id: ObjectInfoImpl.java 34360 2009-07-22 23:58:59Z sunman $
 * 
 */
public class ObjectInfoImpl implements ObjectInfo
{

   private String baseTypeId;

   private String createdBy;

   private Calendar creationDate;

   private String id;

   private Boolean latestMajorVersion;

   private Boolean latestVersion;

   private Calendar lastModificationDate;

   private String name;

   private String parentId;

   private String versionSeriesId;

   private String contentStreamMimeType;

   private String changeToken;

   private String targetId;

   private String versionSeriesCheckedOutId;

   private String sourceId;

   public String getBaseTypeId()
   {
      return baseTypeId;
   }

   public String getChangeToken()
   {
      return changeToken;
   }

   public String getContentStreamMimeType()
   {
      return contentStreamMimeType;
   }

   public String getCreatedBy()
   {
      return createdBy;
   }

   public Calendar getCreationDate()
   {
      return creationDate;
   }

   public String getId()
   {
      return id;
   }

   public Calendar getLastModificationDate()
   {
      return lastModificationDate;
   }

   public String getName()
   {
      return name;
   }

   public String getParentId()
   {
      return parentId;
   }

   public String getSourceId()
   {
      return sourceId;
   }

   public String getTargetId()
   {
      return targetId;
   }

   public String getVersionSeriesCheckedOutId()
   {
      return versionSeriesCheckedOutId;
   }

   public String getVersionSeriesId()
   {
      return versionSeriesId;
   }

   public Boolean isLatestMajorVersion()
   {
      return latestMajorVersion;
   }

   public Boolean isLatestVersion()
   {
      return latestVersion;
   }

   public void setBaseTypeId(String baseTypeId)
   {
      this.baseTypeId = baseTypeId;
   }

   public void setChangeToken(String changeToken)
   {
      this.changeToken = changeToken;
   }

   public void setContentStreamMimeType(String contentStreamMimeType)
   {
      this.contentStreamMimeType = contentStreamMimeType;
   }

   public void setCreatedBy(String createdBy)
   {
      this.createdBy = createdBy;
   }

   public void setCreationDate(GregorianCalendar creationDate)
   {
      this.creationDate = creationDate;
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public void setIsLatestMajorVersion(boolean latestMajorVersion)
   {
      this.latestMajorVersion = latestMajorVersion;
   }

   public void setIsLatestVersion(boolean latestVersion)
   {
      this.latestVersion = latestVersion;
   }

   public void setLastModificationDate(GregorianCalendar lastModificationDate)
   {
      this.lastModificationDate = lastModificationDate;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public void setParentId(String parentId)
   {
      this.parentId = parentId;
   }

   public void setSourceId(String sourceId)
   {
      this.sourceId = sourceId;
   }

   public void setTargetId(String targetId)
   {
      this.targetId = targetId;
   }

   public void setVersionSeriesCheckedOutId(String versionSeriesCheckedOutId)
   {
      this.versionSeriesCheckedOutId = versionSeriesCheckedOutId;
   }

   public void setVersionSeriesId(String versionSeriesId)
   {
      this.versionSeriesId = versionSeriesId;
   }

}
