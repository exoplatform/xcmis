package org.xcmis.spi.object;

import java.util.Calendar;

/**
 * This interface is provided methods for object information holder.
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey Zavizionov</a>
 * @version $Id: ObjectInfo.java 34360 2009-07-22 23:58:59Z sunman $
 */
public interface ObjectInfo
{
   /**
    * CMIS.OBJECT_ID
    */
   String getId();

   /**
    * CMIS.NAME
    */
   String getName();

   /**
    * CMIS.CREATED_BY
    */
   String getCreatedBy();

   /**
    * CMIS.CREATION_DATE
    */
   Calendar getCreationDate();

   /**
    * CMIS.LAST_MODIFICATION_DATE
    */
   Calendar getLastModificationDate();

   /**
    * CMIS.BASE_TYPE_ID
    */
   String getBaseTypeId();

   /**
    * CMIS.PARENT_ID
    */
   String getParentId();

   /**
    * CMIS.IS_LATEST_VERSION
    */
   Boolean isLatestVersion();

   /**
    * CMIS.IS_LATEST_MAJOR_VERSION
    */
   Boolean isLatestMajorVersion();

   /**
    * CMIS.VERSION_SERIES_ID
    */
   String getVersionSeriesId();

   /**
    * CMIS.CONTENT_STREAM_MIME_TYPE
    */
   String getContentStreamMimeType();

   /**
    * CMIS.CHANGE_TOKEN
    */
   String getChangeToken();

   /**
    * CMIS.TARGET_ID
    */
   String getTargetId();

   /**
    * CMIS.VERSION_SERIES_CHECKED_OUT_ID
    */
   String getVersionSeriesCheckedOutId();

   /**
    * CMIS.SOURCE_ID
    */
   String getSourceId();

}
