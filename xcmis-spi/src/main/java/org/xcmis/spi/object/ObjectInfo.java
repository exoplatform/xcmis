package org.xcmis.spi.object;

import org.xcmis.spi.BaseType;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.Connection;

import java.util.Calendar;

/**
 * This interface is provided methods for object information holder. Particular
 * this info is useful for AtomPub binding. AtomPub binding needs some
 * information to build correct Atom document but this info may be excluded by
 * property filter provided by client application. Method for retrieving CMIS
 * object in {@link Connection} has additional parameter
 * <code>includeObjectInfo</code>, e.g.
 * {@link Connection#getObject(String, boolean, org.xcmis.spi.IncludeRelationships, boolean, boolean, boolean, String, String)}
 * . If this parameter is <code>true</code> caller if method must get additional
 * information about object, see {@link CmisObject#getObjectInfo()}.
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey
 *         Zavizionov</a>
 * @version $Id: ObjectInfo.java 34360 2009-07-22 23:58:59Z sunman $
 */
public interface ObjectInfo
{
   // Common
   /**
    * Base object type.
    * 
    * @return base type
    * @see BaseType
    */
   BaseType getBaseType();

   /**
    * Object type id.
    * 
    * @return type id
    */
   String getTypeId();

   /**
    * Property {@link CMIS#OBJECT_ID}.
    * 
    * @return object id
    */
   String getId();

   /**
    * Property {@link CMIS#NAME}.
    * 
    * @return object name
    */
   String getName();

   /**
    * Property {@link CMIS#CREATED_BY}.
    * 
    * @return principal id whose created object
    */
   String getCreatedBy();

   /**
    * Property {@link CMIS#CREATION_DATE}.
    * 
    * @return creation date
    */
   Calendar getCreationDate();

   /**
    * Property {@link CMIS#LAST_MODIFIED_BY}.
    * 
    * @return principal id whose made last modification
    */
   String getLastModifiedBy();

   /**
    * Property {@link CMIS#LAST_MODIFICATION_DATE}.
    * 
    * @return last modification date
    */
   Calendar getLastModificationDate();

   /**
    * Property {@link CMIS#CHANGE_TOKEN}.
    * 
    * @return change token property or <code>null</code> if change token feature
    *         is not supported
    */
   String getChangeToken();

   // Folder

   /**
    * Property {@link CMIS#PARENT_ID}.
    * 
    * @return <code>null</code> for object with base type other then
    *         'cmis:folder'
    */
   String getParentId();

   // Document

   /**
    * Property {@link CMIS#IS_LATEST_VERSION}.
    * 
    * @return <code>null</code> for non-document object and always
    *         <code>true</code> versionable document. Not versionable document
    *         also has exactly one version
    */
   Boolean isLatestVersion();

   /**
    * Property {@link CMIS#IS_MAJOR_VERSION}.
    * 
    * @return <code>null</code> for non-document object not versionable document
    */
   Boolean isMajorVersion();

   /**
    * Property {@link CMIS#IS_LATEST_MAJOR_VERSION}.
    * 
    * @return <code>null</code> for non-document object or not versionable
    *         document
    */
   Boolean isLatestMajorVersion();

   /**
    * Property {@link CMIS#VERSION_SERIES_ID}.
    * 
    * @return <code>null</code> for non-document object
    */
   String getVersionSeriesId();

   /**
    * Property {@link CMIS#VERSION_SERIES_CHECKED_OUT_ID}.
    * 
    * @return id of checked-out document if any. Always <code>null</code> for
    *         non-document object or not versionable document
    */
   String getVersionSeriesCheckedOutId();

   /**
    * Property {@link CMIS#VERSION_SERIES_CHECKED_OUT_BY}.
    * 
    * @return principal id whose checked-out document if any. Always
    *         <code>null</code> for non-document object or not versionable
    *         document
    */
   String getVersionSeriesCheckedOutBy();

   /**
    * Property {@link CMIS#VERSION_LABEL}.
    * 
    * @return <code>null</code> for non-document object
    */
   String getVersionLabel();

   /**
    * Property {@link CMIS#CONTENT_STREAM_MIME_TYPE}.
    * 
    * @return <code>null</code> for non-document object or document without
    *         content
    */
   String getContentStreamMimeType();

   // Relationship

   /**
    * Property {@link CMIS#SOURCE_ID}.
    * 
    * @return <code>null</code> for objects with base type other then
    *         'cmis:relationship'
    */
   String getSourceId();

   /**
    * Property {@link CMIS#TARGET_ID}.
    * 
    * @return <code>null</code> for objects with base type other then
    *         'cmis:relationship'
    */
   String getTargetId();

}
