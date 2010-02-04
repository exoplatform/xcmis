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

import org.xcmis.core.EnumPropertiesBase;
import org.xcmis.core.EnumPropertiesDocument;
import org.xcmis.core.EnumPropertiesFolder;
import org.xcmis.core.EnumPropertiesPolicy;
import org.xcmis.core.EnumPropertiesRelationship;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface CMIS
{

   String CMIS_PREFIX = "cmis";

   String CMIS_NS_URI = "http://docs.oasis-open.org/ns/cmis/core/200908/";

   String SUPPORTED_VERSION = "1.0";

   String ROOT_FOLDER_NAME = "CMIS_Root_Folder";

   String WILDCARD = "*";
   
   // TODO need have configurable ?
   int MAX_ITEMS = 10;

   /* 2^16 */
   BigInteger MAX_STRING_LENGTH = BigInteger.valueOf(65536);

   // TODO : get smarter about precisions
   BigInteger PRECISION = BigInteger.valueOf(32);

   BigDecimal MAX_DECIMAL_VALUE = BigDecimal.valueOf(Double.MAX_VALUE);

   BigDecimal MIN_DECIMAL_VALUE = BigDecimal.valueOf(Double.MIN_VALUE);

   BigInteger MAX_INTEGER_VALUE = BigInteger.valueOf(Long.MAX_VALUE);

   BigInteger MIN_INTEGER_VALUE = BigInteger.valueOf(Long.MIN_VALUE);

   // --------- Properties -----------

   String IS_LATEST_VERSION = EnumPropertiesDocument.CMIS_IS_LATEST_VERSION.value();

   String IS_LATEST_MAJOR_VERSION = EnumPropertiesDocument.CMIS_IS_LATEST_MAJOR_VERSION.value();

   String IS_MAJOR_VERSION = EnumPropertiesDocument.CMIS_IS_MAJOR_VERSION.value();

   String CREATED_BY = EnumPropertiesBase.CMIS_CREATED_BY.value();

   String CREATION_DATE = EnumPropertiesBase.CMIS_CREATION_DATE.value();

   String LAST_MODIFIED_BY = EnumPropertiesBase.CMIS_LAST_MODIFIED_BY.value();

   String LAST_MODIFICATION_DATE = EnumPropertiesBase.CMIS_LAST_MODIFICATION_DATE.value();

   String CHANGE_TOKEN = EnumPropertiesBase.CMIS_CHANGE_TOKEN.value();

   String VERSION_SERIES_ID = EnumPropertiesDocument.CMIS_VERSION_SERIES_ID.value();

   String VERSION_LABEL = EnumPropertiesDocument.CMIS_VERSION_LABEL.value();

   String SOURCE_ID = EnumPropertiesRelationship.CMIS_SOURCE_ID.value();

   String TARGET_ID = EnumPropertiesRelationship.CMIS_TARGET_ID.value();

   String CHECKIN_COMMENT = EnumPropertiesDocument.CMIS_CHECKIN_COMMENT.value();

   String IS_VERSION_SERIES_CHECKED_OUT =
      EnumPropertiesDocument.CMIS_IS_VERSION_SERIES_CHECKED_OUT.value();

   String VERSION_SERIES_CHECKED_OUT_BY =
      EnumPropertiesDocument.CMIS_VERSION_SERIES_CHECKED_OUT_BY.value();

   String VERSION_SERIES_CHECKED_OUT_ID =
      EnumPropertiesDocument.CMIS_VERSION_SERIES_CHECKED_OUT_ID.value();

   String NAME = EnumPropertiesBase.CMIS_NAME.value();

   String OBJECT_ID = EnumPropertiesBase.CMIS_OBJECT_ID.value();

   String OBJECT_TYPE_ID = EnumPropertiesBase.CMIS_OBJECT_TYPE_ID.value();

   String BASE_TYPE_ID = EnumPropertiesBase.CMIS_BASE_TYPE_ID.value();

   String IS_IMMUTABLE = EnumPropertiesDocument.CMIS_IS_IMMUTABLE.value();

   String CONTENT_STREAM_LENGTH = EnumPropertiesDocument.CMIS_CONTENT_STREAM_LENGTH.value();

   String CONTENT_STREAM_MIME_TYPE = EnumPropertiesDocument.CMIS_CONTENT_STREAM_MIME_TYPE.value();

   String CONTENT_STREAM_FILE_NAME = EnumPropertiesDocument.CMIS_CONTENT_STREAM_FILE_NAME.value();

   String CONTENT_STREAM_ID = EnumPropertiesDocument.CMIS_CONTENT_STREAM_ID.value();

   String POLICY_TEXT = EnumPropertiesPolicy.CMIS_POLICY_TEXT.value();

   String PARENT_ID = EnumPropertiesFolder.CMIS_PARENT_ID.value();

   String PATH = EnumPropertiesFolder.CMIS_PATH.value();

   String ALLOWED_CHILD_OBJECT_TYPE_IDS =
      EnumPropertiesFolder.CMIS_ALLOWED_CHILD_OBJECT_TYPE_IDS.value();

}
