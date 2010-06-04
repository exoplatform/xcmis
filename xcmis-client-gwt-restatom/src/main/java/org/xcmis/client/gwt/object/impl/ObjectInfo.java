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
package org.xcmis.client.gwt.object.impl;

import java.util.Date;

import org.xcmis.client.gwt.model.EnumBaseObjectTypeIds;


/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey
 *         Zavizionov</a>
 * @version $Id: ObjectInfoImpl.java 34360 2009-07-22 23:58:59Z sunman $
 * 
 */
public class ObjectInfo
{

   // Common

   private EnumBaseObjectTypeIds baseType;

   private String typeId;

   private String id;

   private String name;

   private String createdBy;

   private Date creationDate;

   private String lastModifiedBy;

   private Date lastModificationDate;

   private String changeToken;

   public ObjectInfo()
   {
   }

   // Common

   public ObjectInfo(EnumBaseObjectTypeIds baseType, String typeId, String id, String name, String createdBy,
      Date creationDate, String lastModifiedBy, Date lastModificationDate, String changeToken, String parentId,
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
   }

   /**
    * {@inheritDoc}
    */
   public EnumBaseObjectTypeIds getBaseType()
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
   public Date getCreationDate()
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
   public Date getLastModificationDate()
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

   // ------------------- Setters --------------------------

   // Common

   public void setBaseType(EnumBaseObjectTypeIds baseType)
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

   public void setCreationDate(Date creationDate)
   {
      this.creationDate = creationDate;
   }

   public void setLastModifiedBy(String lastModifiedBy)
   {
      this.lastModifiedBy = lastModifiedBy;
   }

   public void setLastModificationDate(Date lastModificationDate)
   {
      this.lastModificationDate = lastModificationDate;
   }

   public void setChangeToken(String changeToken)
   {
      this.changeToken = changeToken;
   }

}
