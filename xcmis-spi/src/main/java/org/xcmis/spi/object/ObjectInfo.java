package org.xcmis.spi.object;

import java.util.GregorianCalendar;

/**
 * This interface is provided methods for object information holder.
 *  
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id: ObjectInfo.java 34360 2009-07-22 23:58:59Z sunman $
 *
 */
public interface ObjectInfo
{
   /**
    *  CMIS.OBJECT_ID
    */
   public String getId();

   public void setId(String id);

   /**
    * CMIS.NAME
    */
   public String getName();

   public void setName(String name);

   /**
    * CMIS.CREATED_BY
    */
   public String getCreatedBy();

   public void setCreatedBy(String createdBy);

   /**
    * CMIS.CREATION_DATE
    */
   public GregorianCalendar getCreationDate();

   public void setCreationDate(GregorianCalendar creationDate);

   /**
    * CMIS.LAST_MODIFICATION_DATE
    */
   public GregorianCalendar getLastModificationDate();

   public void setLastModificationDate(GregorianCalendar lastModificationDate);

   /**
    *  CMIS.BASE_TYPE_ID
    */
   public String getBaseTypeId();

   public void setBaseTypeId(String baseTypeId);

   /**
    * CMIS.PARENT_ID
    */
   public String getParentId();

   public void setParentId(String parentId);

   /**
    * CMIS.IS_LATEST_VERSION
    */
   public boolean isLatestVersion();

   public void setIsLatestVersion(boolean latestVersion);

   /**
    * CMIS.IS_LATEST_MAJOR_VERSION
    */
   public boolean isLatestMajorVersion();

   public void setIsLatestMajorVersion(boolean latestMajorVersion);

   /**
    * CMIS.VERSION_SERIES_ID
    */
   public String getVersionSeriesId();

   public void setVersionSeriesId(String versionSeriesId);

   /**
    * CMIS.CONTENT_STREAM_MIME_TYPE
    */
   public String getContentStreamMimeType();

   public void setContentStreamMimeType(String contentStreamMimeType);

   /**
    * CMIS.CHANGE_TOKEN
    */
   public String getChangeToken();

   public void setChangeToken(String changeToken);

   /**
    * CMIS.TARGET_ID
    */
   public String getTargetId();

   public void setTargetId(String targetId);

   /**
    * CMIS.VERSION_SERIES_CHECKED_OUT_ID
    */
   public String getVersionSeriesCheckedOutId();

   public void setVersionSeriesCheckedOutId(String versionSeriesCheckedOutId);

   /**
    * CMIS.SOURCE_ID 
    */
   public String getSourceId();

   public void setSourceId(String sourceId);

}