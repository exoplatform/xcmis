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
package org.xcmis.client.gwt.object;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public interface CmisDocument extends CmisObject
{
   /**
    * @return {@link Boolean} the latestVersion
    */
   Boolean getLatestVersion();

   /**
    * @param latestVersion the latestVersion to set
    */
   void setLatestVersion(Boolean latestVersion);

   /**
    * @return  {@link Boolean} the majorVersion
    */
   Boolean getMajorVersion();

   /**
    * @param majorVersion the majorVersion to set
    */
   void setMajorVersion(Boolean majorVersion);

   /**
    * @return {@link String} the latestMajorVersion
    */
   Boolean getLatestMajorVersion();

   /**
    * @param latestMajorVersion the latestMajorVersion to set
    */
   void setLatestMajorVersion(Boolean latestMajorVersion);

   /**
    * @return {@link String} the versionSeriesId
    */
   String getVersionSeriesId();

   /**
    * @param versionSeriesId the versionSeriesId to set
    */
   void setVersionSeriesId(String versionSeriesId);

   /**
    * @return {@link String} the versionSeriesCheckedOutId
    */
   String getVersionSeriesCheckedOutId();

   /**
    * @param versionSeriesCheckedOutId the versionSeriesCheckedOutId to set
    */
   void setVersionSeriesCheckedOutId(String versionSeriesCheckedOutId);

   /**
    * @return {@link String} the versionSeriesCheckedOutBy
    */
   String getVersionSeriesCheckedOutBy();

   /**
    * @param versionSeriesCheckedOutBy the versionSeriesCheckedOutBy to set
    */
   void setVersionSeriesCheckedOutBy(String versionSeriesCheckedOutBy);

   /**
    * @return {@link String} the versionLabel
    */
   String getVersionLabel();

   /**
    * @param versionLabel the versionLabel to set
    */
   void setVersionLabel(String versionLabel);

   /**
    * @return {@link String} the contentStreamMimeType
    */
   String getContentStreamMimeType();

   /**
    * @param contentStreamMimeType the contentStreamMimeType to set
    */
   void setContentStreamMimeType(String contentStreamMimeType);

   /**
    * @return {@link Long} the contentStreamLenght
    */
   Long getContentStreamLenght();

   /**
    * @param contentStreamLenght the contentStreamLenght to set
    */
   void setContentStreamLenght(Long contentStreamLenght);

   /**
    * @return {@link Boolean} the versionSeriesCheckedOut
    */
   Boolean getVersionSeriesCheckedOut();

   /**
    * @param versionSeriesCheckedOut the versionSeriesCheckedOut to set
    */
   void setVersionSeriesCheckedOut(Boolean versionSeriesCheckedOut);

}
