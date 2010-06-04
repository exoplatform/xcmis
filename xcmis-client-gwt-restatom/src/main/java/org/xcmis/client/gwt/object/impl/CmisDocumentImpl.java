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

import java.util.HashSet;

import org.xcmis.client.gwt.object.CmisDocument;
import org.xcmis.client.gwt.object.CmisObject;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class CmisDocumentImpl extends CmisObjectImpl implements CmisDocument
{

   /**
    * Is latest version.
    */
   private Boolean latestVersion;

   /**
    * Is major version.
    */
   private Boolean majorVersion;

   /**
    * Is latest major version.
    */
   private Boolean latestMajorVersion;

   /**
    * Is version series checked out.
    */
   private Boolean versionSeriesCheckedOut;

   /**
    * Version series id.
    */
   private String versionSeriesId;

   /**
    * Version series checked out id
    */
   private String versionSeriesCheckedOutId;

   /**
    * Version series checked out by
    */
   private String versionSeriesCheckedOutBy;

   /**
    * Version label
    */
   private String versionLabel;

   /**
    * Conent stream mime type
    */
   private String contentStreamMimeType;

   /**
    * Conent stream length
    */
   private Long contentStreamLenght;
   
   
   public CmisDocumentImpl(CmisObject object)
   {
      super(object.getProperties().getProperties(), object.getACL(), object.isExactACL(), 
         new HashSet<String>(object.getPolicyIds()), object.getRelationship(), object.getRenditions(), 
         object.getAllowableActions(), object.getChangeInfo(), object.getObjectInfo(), 
         object.getPathSegment());
   }

   
   
   /**
    * @see org.xcmis.client.gwt.object.CmisDocument#getContentStreamLenght()
    */
   public Long getContentStreamLenght()
   {
      return contentStreamLenght;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisDocument#getContentStreamMimeType()
    */
   public String getContentStreamMimeType()
   {
      return contentStreamMimeType;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisDocument#getLatestMajorVersion()
    */
   public Boolean getLatestMajorVersion()
   {
      return latestMajorVersion;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisDocument#getLatestVersion()
    */
   public Boolean getLatestVersion()
   {
      return latestVersion;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisDocument#getMajorVersion()
    */
   public Boolean getMajorVersion()
   {
      return majorVersion;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisDocument#getVersionLabel()
    */
   public String getVersionLabel()
   {
      return versionLabel;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisDocument#getVersionSeriesCheckedOut()
    */
   public Boolean getVersionSeriesCheckedOut()
   {
      return versionSeriesCheckedOut;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisDocument#getVersionSeriesCheckedOutBy()
    */
   public String getVersionSeriesCheckedOutBy()
   {
      return versionSeriesCheckedOutBy;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisDocument#getVersionSeriesCheckedOutId()
    */
   public String getVersionSeriesCheckedOutId()
   {
      return versionSeriesCheckedOutId;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisDocument#getVersionSeriesId()
    */
   public String getVersionSeriesId()
   {
      return versionSeriesId;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisDocument#setContentStreamLenght(java.lang.Long)
    */
   public void setContentStreamLenght(Long contentStreamLenght)
   {
      this.contentStreamLenght = contentStreamLenght;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisDocument#setContentStreamMimeType(java.lang.String)
    */
   public void setContentStreamMimeType(String contentStreamMimeType)
   {
      this.contentStreamMimeType = contentStreamMimeType;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisDocument#setLatestMajorVersion(java.lang.Boolean)
    */
   public void setLatestMajorVersion(Boolean latestMajorVersion)
   {
      this.latestMajorVersion = latestMajorVersion;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisDocument#setLatestVersion(java.lang.Boolean)
    */
   public void setLatestVersion(Boolean latestVersion)
   {
      this.latestVersion = latestVersion;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisDocument#setMajorVersion(java.lang.Boolean)
    */
   public void setMajorVersion(Boolean majorVersion)
   {
      this.majorVersion = majorVersion;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisDocument#setVersionLabel(java.lang.String)
    */
   public void setVersionLabel(String versionLabel)
   {
      this.versionLabel = versionLabel;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisDocument#setVersionSeriesCheckedOut(java.lang.Boolean)
    */
   public void setVersionSeriesCheckedOut(Boolean versionSeriesCheckedOut)
   {
      this.versionSeriesCheckedOut = versionSeriesCheckedOut;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisDocument#setVersionSeriesCheckedOutBy(java.lang.String)
    */
   public void setVersionSeriesCheckedOutBy(String versionSeriesCheckedOutBy)
   {
      this.versionSeriesCheckedOutBy = versionSeriesCheckedOutBy;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisDocument#setVersionSeriesCheckedOutId(java.lang.String)
    */
   public void setVersionSeriesCheckedOutId(String versionSeriesCheckedOutId)
   {
      this.versionSeriesCheckedOutId = versionSeriesCheckedOutId;
   }

   /**
    * @see org.xcmis.client.gwt.object.CmisDocument#setVersionSeriesId(java.lang.String)
    */
   public void setVersionSeriesId(String versionSeriesId)
   {
      this.versionSeriesId = versionSeriesId;
   }

}
