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

package org.xcmis.spi;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CMIS.java 260 2010-03-03 15:53:42Z andrew00x $
 */
public interface CMIS
{

   String CMIS_PREFIX = "cmis";

   String CMIS_NS_URI = "http://docs.oasis-open.org/ns/cmis/core/200908/";

   String SUPPORTED_VERSION = "1.0";

   String ROOT_FOLDER_NAME = "CMIS_Root_Folder";

   String WILDCARD = "*";

   String DOCUMENT = "cmis:document";

   String FOLDER = "cmis:folder";

   String POLICY = "cmis:policy";

   String RELATIONSHIP = "cmis:relationship";

   // TODO need have configurable ?
   int MAX_ITEMS = 10;

   /* 2^16 */
   int MAX_STRING_LENGTH = 65536;

   BigDecimal MAX_DECIMAL_VALUE = BigDecimal.valueOf(Double.MAX_VALUE);

   BigDecimal MIN_DECIMAL_VALUE = BigDecimal.valueOf(Double.MIN_VALUE);

   BigInteger MAX_INTEGER_VALUE = BigInteger.valueOf(Long.MAX_VALUE);

   BigInteger MIN_INTEGER_VALUE = BigInteger.valueOf(Long.MIN_VALUE);

   // --------- Properties -----------

   String IS_LATEST_VERSION = "cmis:isLatestVersion";

   String IS_LATEST_MAJOR_VERSION = "cmis:isLatestMajorVersion";

   String IS_MAJOR_VERSION = "cmis:isMajorVersion";

   String CREATED_BY = "cmis:createdBy";

   String CREATION_DATE = "cmis:creationDate";

   String LAST_MODIFIED_BY = "cmis:lastModifiedBy";

   String LAST_MODIFICATION_DATE = "cmis:lastModificationDate";

   String CHANGE_TOKEN = "cmis:changeToken";

   String VERSION_SERIES_ID = "cmis:versionSeriesId";

   String VERSION_LABEL = "cmis:versionLabel";

   String SOURCE_ID = "cmis:sourceId";

   String TARGET_ID = "cmis:targetId";

   String CHECKIN_COMMENT = "cmis:checkinComment";

   String IS_VERSION_SERIES_CHECKED_OUT = "cmis:isVersionSeriesCheckedOut";

   String VERSION_SERIES_CHECKED_OUT_BY = "cmis:versionSeriesCheckedOutBy";

   String VERSION_SERIES_CHECKED_OUT_ID = "cmis:versionSeriesCheckedOutId";

   String NAME = "cmis:name";

   String OBJECT_ID = "cmis:objectId";

   String OBJECT_TYPE_ID = "cmis:objectTypeId";

   String BASE_TYPE_ID = "cmis:baseTypeId";

   String IS_IMMUTABLE = "cmis:isImmutable";

   String CONTENT_STREAM_LENGTH = "cmis:contentStreamLength";

   String CONTENT_STREAM_MIME_TYPE = "cmis:contentStreamMimeType";

   String CONTENT_STREAM_FILE_NAME = "cmis:contentStreamFileName";

   String CONTENT_STREAM_ID = "cmis:contentStreamId";

   String POLICY_TEXT = "cmis:policyText";

   String PARENT_ID = "cmis:parentId";

   String PATH = "cmis:path";

   String ALLOWED_CHILD_OBJECT_TYPE_IDS = "cmis:allowedChildObjectTypeIds";

}
