package org.xcmis.client.gwt.model;

public enum EnumPropertiesDocument {

    CMIS_IS_IMMUTABLE("cmis:isImmutable"),
    CMIS_IS_LATEST_VERSION("cmis:isLatestVersion"),
    CMIS_IS_MAJOR_VERSION("cmis:isMajorVersion"),
    CMIS_IS_LATEST_MAJOR_VERSION("cmis:isLatestMajorVersion"),
    CMIS_VERSION_LABEL("cmis:versionLabel"),
    CMIS_VERSION_SERIES_ID("cmis:versionSeriesId"),
    CMIS_IS_VERSION_SERIES_CHECKED_OUT("cmis:isVersionSeriesCheckedOut"),
    CMIS_VERSION_SERIES_CHECKED_OUT_BY("cmis:versionSeriesCheckedOutBy"),
    CMIS_VERSION_SERIES_CHECKED_OUT_ID("cmis:versionSeriesCheckedOutId"),
    CMIS_CHECKIN_COMMENT("cmis:checkinComment"),
    CMIS_CONTENT_STREAM_LENGTH("cmis:contentStreamLength"),
    CMIS_CONTENT_STREAM_MIME_TYPE("cmis:contentStreamMimeType"),
    CMIS_CONTENT_STREAM_FILE_NAME("cmis:contentStreamFileName"),
    CMIS_CONTENT_STREAM_ID("cmis:contentStreamId");
    
    /**
    * Value
    */
   private final String value;

    /**
    * @param v value
    */
   EnumPropertiesDocument(String v) {
        value = v;
    }

    /**
    * @return {@link String} value
    */
   public String value() {
        return value;
    }

    /**
    * @param v value
    * @return {@link EnumPropertiesDocument} enum value
    */
   public static EnumPropertiesDocument fromValue(String v) {
        for (EnumPropertiesDocument c: EnumPropertiesDocument.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
