package org.xcmis.spi.model;

import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.Connection;

import java.util.Calendar;

/**
 * Object information holder. Particular this info is useful for AtomPub
 * binding. AtomPub binding needs some information to build correct Atom
 * document but this info may be excluded by property filter provided by client
 * application. Methods for retrieving CMIS object in {@link Connection} has
 * additional parameter <code>includeObjectInfo</code>, e.g.
 * {@link Connection#getObject(String, boolean, org.xcmis.spi.IncludeRelationships, boolean, boolean, boolean, String, String)}
 * . If this parameter is <code>true</code> caller if method must get additional
 * information about object, see {@link CmisObject#getObjectInfo()}.
 *
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey
 *         Zavizionov</a>
 * @version $Id: ObjectInfo.java 34360 2009-07-22 23:58:59Z sunman $
 */
public final class ObjectInfo
{

   // Common

   private BaseType baseType;

   private String typeId;

   private String id;

   private String name;

   private String createdBy;

   private Calendar creationDate;

   private String lastModifiedBy;

   private Calendar lastModificationDate;

   private String changeToken;

   // Folder

   private String parentId;

   // Document

   private Boolean latestVersion;

   private Boolean majorVersion;

   private Boolean latestMajorVersion;

   private String versionSeriesId;

   private String versionSeriesCheckedOutId;

   private String versionSeriesCheckedOutBy;

   private String versionLabel;

   private String contentStreamMimeType;

   // Relationship

   private String sourceId;

   private String targetId;

   public ObjectInfo()
   {
   }

   public ObjectInfo(BaseType baseType, String typeId, String id, String name, String createdBy, Calendar creationDate,
      String lastModifiedBy, Calendar lastModificationDate, String changeToken, String parentId, Boolean latestVersion,
      Boolean majorVersion, Boolean latestMajorVersion, String versionSeriesId, String versionSeriesCheckedOutId,
      String versionSeriesCheckedOutBy, String versionLabel, String contentStreamMimeType, String sourceId,
      String targetId)
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
      this.parentId = parentId;
      this.latestVersion = latestVersion;
      this.majorVersion = majorVersion;
      this.latestMajorVersion = latestMajorVersion;
      this.versionSeriesId = versionSeriesId;
      this.versionSeriesCheckedOutId = versionSeriesCheckedOutId;
      this.versionSeriesCheckedOutBy = versionSeriesCheckedOutBy;
      this.versionLabel = versionLabel;
      this.contentStreamMimeType = contentStreamMimeType;
      this.sourceId = sourceId;
      this.targetId = targetId;
   }

   // Common
   /**
    * Base object type.
    *
    * @return base type
    * @see BaseType
    */
   public BaseType getBaseType()
   {
      return baseType;
   }

   /**
    * Object type id.
    *
    * @return type id
    */
   public String getTypeId()
   {
      return typeId;
   }

   /**
    * Property {@link CmisConstants#OBJECT_ID}.
    *
    * @return object id
    */
   public String getId()
   {
      return id;
   }

   /**
    * Property {@link CmisConstants#NAME}.
    *
    * @return object name
    */
   public String getName()
   {
      return name;
   }

   /**
    * Property {@link CmisConstants#CREATED_BY}.
    *
    * @return principal id whose created object
    */
   public String getCreatedBy()
   {
      return createdBy;
   }

   /**
    * Property {@link CmisConstants#CREATION_DATE}.
    *
    * @return creation date
    */
   public Calendar getCreationDate()
   {
      return creationDate;
   }

   /**
    * Property {@link CmisConstants#LAST_MODIFIED_BY}.
    *
    * @return principal id whose made last modification
    */
   public String getLastModifiedBy()
   {
      return lastModifiedBy;
   }

   /**
    * Property {@link CmisConstants#LAST_MODIFICATION_DATE}.
    *
    * @return last modification date
    */
   public Calendar getLastModificationDate()
   {
      return lastModificationDate;
   }

   /**
    * Property {@link CmisConstants#CHANGE_TOKEN}.
    *
    * @return change token property or <code>null</code> if change token feature
    *         is not supported
    */
   public String getChangeToken()
   {
      return changeToken;
   }

   // Folder

   /**
    * Property {@link CmisConstants#PARENT_ID}.
    *
    * @return <code>null</code> for object with base type other then
    *         'cmis:folder'
    */
   public String getParentId()
   {
      return parentId;
   }

   // Document

   /**
    * Property {@link CmisConstants#IS_LATEST_VERSION}.
    *
    * @return <code>null</code> for non-document object and always
    *         <code>true</code> versionable document. Not versionable document
    *         also has exactly one version
    */
   public Boolean isLatestVersion()
   {
      return latestVersion;
   }

   /**
    * Property {@link CmisConstants#IS_MAJOR_VERSION}.
    *
    * @return <code>null</code> for non-document object not versionable document
    */
   public Boolean isMajorVersion()
   {
      return majorVersion;
   }

   /**
    * Property {@link CmisConstants#IS_LATEST_MAJOR_VERSION}.
    *
    * @return <code>null</code> for non-document object or not versionable
    *         document
    */
   public Boolean isLatestMajorVersion()
   {
      return latestMajorVersion;
   }

   /**
    * Property {@link CmisConstants#VERSION_SERIES_ID}.
    *
    * @return <code>null</code> for non-document object
    */
   public String getVersionSeriesId()
   {
      return versionSeriesId;
   }

   /**
    * Property {@link CmisConstants#VERSION_SERIES_CHECKED_OUT_ID}.
    *
    * @return id of checked-out document if any. Always <code>null</code> for
    *         non-document object or not versionable document
    */
   public String getVersionSeriesCheckedOutId()
   {
      return versionSeriesCheckedOutId;
   }

   /**
    * Property {@link CmisConstants#VERSION_SERIES_CHECKED_OUT_BY}.
    *
    * @return principal id whose checked-out document if any. Always
    *         <code>null</code> for non-document object or not versionable
    *         document
    */
   public String getVersionSeriesCheckedOutBy()
   {
      return versionSeriesCheckedOutBy;
   }

   /**
    * Property {@link CmisConstants#VERSION_LABEL}.
    *
    * @return <code>null</code> for non-document object
    */
   public String getVersionLabel()
   {
      return versionLabel;
   }

   /**
    * Property {@link CmisConstants#CONTENT_STREAM_MIME_TYPE}.
    *
    * @return <code>null</code> for non-document object or document without
    *         content
    */
   public String getContentStreamMimeType()
   {
      return contentStreamMimeType;
   }

   // Relationship

   /**
    * Property {@link CmisConstants#SOURCE_ID}.
    *
    * @return <code>null</code> for objects with base type other then
    *         'cmis:relationship'
    */
   public String getSourceId()
   {
      return sourceId;
   }

   /**
    * Property {@link CmisConstants#TARGET_ID}.
    *
    * @return <code>null</code> for objects with base type other then
    *         'cmis:relationship'
    */
   public String getTargetId()
   {
      return targetId;
   }

   // ------------------- Setters --------------------------

   // Common

   public void setBaseType(BaseType baseType)
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

   public void setCreationDate(Calendar creationDate)
   {
      this.creationDate = creationDate;
   }

   public void setLastModifiedBy(String lastModifiedBy)
   {
      this.lastModifiedBy = lastModifiedBy;
   }

   public void setLastModificationDate(Calendar lastModificationDate)
   {
      this.lastModificationDate = lastModificationDate;
   }

   public void setChangeToken(String changeToken)
   {
      this.changeToken = changeToken;
   }

   // Folder

   public void setParentId(String parentId)
   {
      this.parentId = parentId;
   }

   // Document

   public void setLatestVersion(Boolean latestVersion)
   {
      this.latestVersion = latestVersion;
   }

   public void setMajorVersion(Boolean majorVersion)
   {
      this.majorVersion = majorVersion;
   }

   public void setLatestMajorVersion(Boolean latestMajorVersion)
   {
      this.latestMajorVersion = latestMajorVersion;
   }

   public void setVersionSeriesId(String versionSeriesId)
   {
      this.versionSeriesId = versionSeriesId;
   }

   public void setVersionSeriesCheckedOutId(String versionSeriesCheckedOutId)
   {
      this.versionSeriesCheckedOutId = versionSeriesCheckedOutId;
   }

   public void setVersionSeriesCheckedOutBy(String versionSeriesCheckedOutBy)
   {
      this.versionSeriesCheckedOutBy = versionSeriesCheckedOutBy;
   }

   public void setVersionLabel(String versionLabel)
   {
      this.versionLabel = versionLabel;
   }

   public void setContentStreamMimeType(String contentStreamMimeType)
   {
      this.contentStreamMimeType = contentStreamMimeType;
   }

   // Relationship

   public void setSourceId(String sourceId)
   {
      this.sourceId = sourceId;
   }

   public void setTargetId(String targetId)
   {
      this.targetId = targetId;
   }
}
