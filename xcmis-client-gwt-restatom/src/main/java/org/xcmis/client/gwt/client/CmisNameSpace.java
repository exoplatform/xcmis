/**
 *  Copyright (C) 2010 eXo Platform SAS.
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xcmis.client.gwt.client;

import org.xcmis.client.gwt.client.model.EnumBaseObjectTypeIds;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Anna Zhuleva</a>
 * @version $Id:
 */
public class CmisNameSpace
{
   /**
    * Constructor.
    */
   protected CmisNameSpace()
   {
      throw new UnsupportedOperationException(); // prevents calls from subclass
   }
   
   /**
    * CMIS prefix.
    */
   public static final String CMIS_PREFIX = "cmis:";

   /**
    * CMISRA prefix.
    */
   public static final String CMISRA_PREFIX = "cmisra:";

   /**
    * App accept.
    */
   public static final String APP_ACCEPT = "accept";

   /**
    * App collection.
    */
   public static final String APP_COLLECTION = "collection";

   /**
    * Atom author.
    */
   public static final String ATOM_AUTHOR = "author";

   /**
    * Atom content.
    */
   public static final String ATOM_CONTENT = "content";

   /**
    * Atom email.
    */
   public static final String ATOM_EMAIL = "email";

   /**
    * Atom entry.
    */
   public static final String ATOM_ENTRY = "entry";

   /**
    * Atom id.
    */
   public static final String ATOM_ID = "id";

   /**
    * Atom link.
    */
   public static final String ATOM_LINK = "link";

   /**
    * Atom name.
    */
   public static final String ATOM_NAME = "name";

   /**
    * Atom published.
    */
   public static final String ATOM_PUBLISHED = "published";

   /**
    * Atom summary.
    */
   public static final String ATOM_SUMMARY = "summary";

   /**
    * Atom title.
    */
   public static final String ATOM_TITLE = "title";

   /**
    * Atom updated.
    */
   public static final String ATOM_UPDATED = "updated";

   /**
    * Atom URI.
    */
   public static final String ATOM_URI = "uri";

   /**
    * Base type document.
    */
   public static final String BASE_TYPE_DOCUMENT = EnumBaseObjectTypeIds.CMIS_DOCUMENT.value();

   /**
    * Base type folder.
    */
   public static final String BASE_TYPE_FOLDER = EnumBaseObjectTypeIds.CMIS_FOLDER.value();

   /**
    * Base type policy.
    */
   public static final String BASE_TYPE_POLICY = EnumBaseObjectTypeIds.CMIS_POLICY.value();

   /**
    * Base type relationship.
    */
   public static final String BASE_TYPE_RELATIONSHIP = EnumBaseObjectTypeIds.CMIS_RELATIONSHIP.value();

   /**
    * CMIS ACL capability.
    */
   public static final String CMIS_ACL_CAPABILITY = CMIS_PREFIX + "aclCapability";

   /**
    * CMIS allowable actions.
    */
   public static final String CMIS_ALLOWABLE_ACTIONS = CMIS_PREFIX + "allowableActions";
   
   /**
    * Allowable actions.
    */
   public static final String ALLOWABLE_ACTIONS = "allowableActions";

   /**
    * CMIS allowed child object type ids.
    */
   public static final String CMIS_ALLOWED_CHILD_OBJECT_TYPE_IDS = CMIS_PREFIX + "allowedChildObjectTypeIds";

   /**
    * CMIS base id.
    */
   public static final String CMIS_BASE_ID = CMIS_PREFIX + "baseId";

   /**
    * CMIS base type id.
    */
   public static final String CMIS_BASE_TYPE_ID = CMIS_PREFIX + "baseTypeId";

   /**
    * CMIS can delete object.
    */
   public static final String CMIS_CAN_DELETE_OBJECT = CMIS_PREFIX + "canDeleteObject";

   /**
    * CMIS can update properties.
    */
   public static final String CMIS_CAN_UPDATE_PROPERTIES = CMIS_PREFIX + "canUpdateProperties";

   /**
    * CMIS can get folder tree.
    */
   public static final String CMIS_CAN_GET_FOLDER_TREE = CMIS_PREFIX + "canGetFolderTree";

   /**
    * CMIS can get properties.
    */
   public static final String CMIS_CAN_GET_PROPERTIES = CMIS_PREFIX + "canGetProperties";

   /**
    * CMIS cat get object relationships.
    */
   public static final String CMIS_CAN_GET_OBJECT_RELATIONSHIPS = CMIS_PREFIX + "canGetObjectRelationships";

   /**
    * CMIS can get object parents.
    */
   public static final String CMIS_CAN_GET_OBJECT_PARENTS = CMIS_PREFIX + "canGetObjectParents";

   /**
    * CMIS can get folder parent.
    */
   public static final String CMIS_CAN_GET_FOLDER_PARENT = CMIS_PREFIX + "canGetFolderParent";

   /**
    * CMIS can get descendants.
    */
   public static final String CMIS_CAN_GET_DESCENDANTS = CMIS_PREFIX + "canGetDescendants";

   /**
    * CMIS can move object.
    */
   public static final String CMIS_CAN_MOVE_OBJECT = CMIS_PREFIX + "canMoveObject";

   /**
    * CMIS can delete content stream.
    */
   public static final String CMIS_CAN_DELETE_CONTENT_STREAM = CMIS_PREFIX + "canDeleteContentStream";

   /**
    * CMIS can check out.
    */
   public static final String CMIS_CAN_CHECK_OUT = CMIS_PREFIX + "canCheckOut";

   /**
    * CMIS can cancel check out.
    */
   public static final String CMIS_CAN_CANCEL_CHECK_OUT = CMIS_PREFIX + "canCancelCheckOut";

   /**
    * CMIS can check in.
    */
   public static final String CMIS_CAN_CHECK_IN = CMIS_PREFIX + "canCheckIn";

   /**
    * CMIS can set content stream.
    */
   public static final String CMIS_CAN_SET_CONTENT_STREAM = CMIS_PREFIX + "canSetContentStream";

   /**
    * CMIS can get all versions.
    */
   public static final String CMIS_CAN_GET_ALL_VERSIONS = CMIS_PREFIX + "canGetAllVersions";

   /**
    * CMIS can add object to folder.
    */
   public static final String CMIS_CAN_ADD_OBJECT_TO_FOLDER = CMIS_PREFIX + "canAddObjectToFolder";

   /**
    * CMIS can remove object from folder.
    */
   public static final String CMIS_CAN_REMOVE_OBJECT_FROM_FOLDER = CMIS_PREFIX + "canRemoveObjectFromFolder";

   /**
    * CMIS can get content stream.
    */
   public static final String CMIS_CAN_GET_CONTENT_STREAM = CMIS_PREFIX + "canGetContentStream";

   /**
    * CMIS can apply policy.
    */
   public static final String CMIS_CAN_APPLY_POLICY = CMIS_PREFIX + "canApplyPolicy";

   /**
    * CMIS can get applied policies.
    */
   public static final String CMIS_CAN_GET_APPLIED_POLICIES = CMIS_PREFIX + "canGetAppliedPolicies";

   /**
    * CMIS can remove policy.
    */
   public static final String CMIS_CAN_REMOVE_POLICY = CMIS_PREFIX + "canRemovePolicy";

   /**
    * CMIS can get children.
    */
   public static final String CMIS_CAN_GET_CHILDREN = CMIS_PREFIX + "canGetChildren";

   /**
    * CMIS can create document.
    */
   public static final String CMIS_CAN_CREATE_DOCUMENT = CMIS_PREFIX + "canCreateDocument";

   /**
    * CMIS can create folder.
    */
   public static final String CMIS_CAN_CREATE_FOLDER = CMIS_PREFIX + "canCreateFolder";

   /**
    * CMIS can create relationship.
    */
   public static final String CMIS_CAN_CREATE_RELATIONSHIP = CMIS_PREFIX + "canCreateRelationship";

   /**
    * CMIS can delete tree.
    */
   public static final String CMIS_CAN_DELETE_TREE = CMIS_PREFIX + "canDeleteTree";

   /**
    * CMIS can get renditions.
    */
   public static final String CMIS_CAN_GET_RENDITIONS = CMIS_PREFIX + "canGetRenditions";

   /**
    * CMIS can get ACL.
    */
   public static final String CMIS_CAN_GET_ACL = CMIS_PREFIX + "canGetACL";

   /**
    * CMIS can apply ACL.
    */
   public static final String CMIS_CAN_APPLY_ACL = CMIS_PREFIX + "canApplyACL";

   /**
    * CMIS capabilities.
    */
   public static final String CMIS_CAPABILITIES = CMIS_PREFIX + "capabilities";

   /**
    * CMIS capability ACL.
    */
   public static final String CMIS_CAPABILITY_ACL = CMIS_PREFIX + "capabilityACL";

   /**
    * CMIS capability all version searchable.
    */
   public static final String CMIS_CAPABILITY_ALL_VERSION_SEARCHABLE = CMIS_PREFIX + "capabilityAllVersionsSearchable";

   /**
    * CMIS capability changes.
    */
   public static final String CMIS_CAPABILITY_CHANGES = CMIS_PREFIX + "capabilityChanges";

   /**
    * CMIS capability content stream updatability.
    */
   public static final String CMIS_CAPABILITY_CONTENTSTREAM_UPDATABILITY =
      CMIS_PREFIX + "capabilityContentStreamUpdatability";

   /**
    * CMIS capability get descendants.
    */
   public static final String CMIS_CAPABILITY_GET_DESCENDANTS = CMIS_PREFIX + "capabilityGetDescendants";

   /**
    * CMIS capability get folder tree.
    */
   public static final String CMIS_CAPABILITY_GET_FOLDER_TREE = CMIS_PREFIX + "capabilityGetFolderTree";

   /**
    * CMIS capability join.
    */
   public static final String CMIS_CAPABILITY_JOIN = CMIS_PREFIX + "capabilityJoin";

   /**
    * CMIS capability multifiling.
    */
   public static final String CMIS_CAPABILITY_MULTIFILING = CMIS_PREFIX + "capabilityMultifiling";

   /**
    * CMIS capability PWC searchable.
    */
   public static final String CMIS_CAPABILITY_PWC_SEARCHABLE = CMIS_PREFIX + "capabilityPWCSearchable";

   /**
    * CMIS capability PWC updateable.
    */
   public static final String CMIS_CAPABILITY_PWC_UPDATEABLE = CMIS_PREFIX + "capabilityPWCUpdatable";

   /**
    * CMIS capability query.
    */
   public static final String CMIS_CAPABILITY_QUERY = CMIS_PREFIX + "capabilityQuery";

   /**
    * CMIS capability renditions.
    */
   public static final String CMIS_CAPABILITY_RENDITIONS = CMIS_PREFIX + "capabilityRenditions";

   /**
    * CMIS capability unfiling.
    */
   public static final String CMIS_CAPABILITY_UNFILING = CMIS_PREFIX + "capabilityUnfiling";

   /**
    * CMIS capability version specific filing.
    */
   public static final String CMIS_CAPABILITY_VERSION_SPECIFIC_FILING = CMIS_PREFIX + "capabilityVersionSpecificFiling";

   /**
    * CMIS cardinality.
    */
   public static final String CMIS_CARDINALITY = CMIS_PREFIX + "cardinality";

   /**
    * CMIS checkin comment.
    */
   public static final String CMIS_CHECKIN_COMMENT = CMIS_PREFIX + "checkinComment";

   /**
    * CMIS content stream file name.
    */
   public static final String CMIS_CONTENT_STREAM_FILE_NAME = CMIS_PREFIX + "contentStreamFileName";

   /**
    * CMIS content stream id.
    */
   public static final String CMIS_CONTENT_STREAM_ID = CMIS_PREFIX + "contentStreamId";

   /**
    * CMIS content stream length.
    */
   public static final String CMIS_CONTENT_STREAM_LENGTH = CMIS_PREFIX + "contentStreamLength";

   /**
    * CMIS content stream mime type.
    */
   public static final String CMIS_CONTENT_STREAM_MIME_TYPE = CMIS_PREFIX + "contentStreamMimeType";

   /**
    * CMIS controllable policy.
    */
   public static final String CMIS_CONTROLLABLE_POLICY = CMIS_PREFIX + "controllablePolicy";

   /**
    * CMIS controllable ACL.
    */
   public static final String CMIS_CONTROLLABLE_ACL = CMIS_PREFIX + "controllableACL";

   /**
    * CMIS creatable.
    */
   public static final String CMIS_CREATABLE = CMIS_PREFIX + "creatable";
   
   /**
    * CMIS change token.
    */
   public static final String CMIS_CHANGE_TOKEN = CMIS_PREFIX + "changeToken";

   /**
    * CMIS created by.
    */
   public static final String CMIS_CREATED_BY = CMIS_PREFIX + "createdBy";

   /**
    * CMIS creation date.
    */
   public static final String CMIS_CREATING_DATE = CMIS_PREFIX + "creationDate";

   /**
    * CMIS description.
    */
   public static final String CMIS_DESCRIPTION = CMIS_PREFIX + "description";

   /**
    * CMIS display name.
    */
   public static final String CMIS_DISPLAY_NAME = CMIS_PREFIX + "displayName";

   /**
    * CMIS document.
    */
   public static final String CMIS_DOCUMENT = "cmis:document";

   /**
    * CMIS fileable.
    */
   public static final String CMIS_FILEABLE = CMIS_PREFIX + "fileable";

   /**
    * CMIS folder.
    */
   public static final String CMIS_FOLDER = "cmis:folder";

   /**
    * CMIS full text indexed.
    */
   public static final String CMIS_FULL_TEXT_INDEXED = CMIS_PREFIX + "fulltextIndexed";

   /**
    * CMIS id.
    */
   public static final String CMIS_ID = CMIS_PREFIX + "id";

   /**
    * CMIS included in supertype query.
    */
   public static final String CMIS_INCLUDED_IN_SUPERTYPE_QUERY = CMIS_PREFIX + "includedInSupertypeQuery";

   /**
    * CMIS inherited.
    */
   public static final String CMIS_INHERITED = CMIS_PREFIX + "inherited";

   /**
    * CMIS is immutable.
    */
   public static final String CMIS_IS_IMMUTABLE = CMIS_PREFIX + "isImmutable";

   /**
    * CMIS is latest major version.
    */
   public static final String CMIS_IS_LATEST_MAJOR_VERSION = CMIS_PREFIX + "isLatestMajorVersion";

   /**
    * CMIS is latest version.
    */
   public static final String CMIS_IS_LATEST_VERSION = CMIS_PREFIX + "isLatestVersion";

   /**
    * CMIS is major version.
    */
   public static final String CMIS_IS_MAJOR_VERSION = CMIS_PREFIX + "isMajorVersion";

   /**
    * CMIS is version series checked out.
    */
   public static final String CMIS_IS_VERSION_SERIES_CHECKEDOUT = CMIS_PREFIX + "isVersionSeriesCheckedOut";

   /**
    * CMIS last modification date.
    */
   public static final String CMIS_LAST_MODIFICATION_DATE = CMIS_PREFIX + "lastModificationDate";

   /**
    * CMIS last modified by.
    */
   public static final String CMIS_LAST_MODIFIED_BY = CMIS_PREFIX + "lastModifiedBy";

   /**
    * CMIS latest change token.
    */
   public static final String CMIS_LATEST_CHANGE_TOKEN = CMIS_PREFIX + "latestChangeLogToken";

   /**
    * CMIS local name.
    */
   public static final String CMIS_LOCAL_NAME = CMIS_PREFIX + "localName";

   /**
    * CMIS local namespace.
    */
   public static final String CMIS_LOCAL_NAMESPACE = CMIS_PREFIX + "localNamespace";

   /**
    * CMIS max items.
    */
   public static final String CMIS_MAX_ITEMS = CMIS_PREFIX + "maxItems";

   /**
    * CMIS max lenght.
    */
   public static final String CMIS_MAX_LENGHT = CMIS_PREFIX + "maxLength";

   /**
    * CMIS max value.
    */
   public static final String CMIS_MAX_VALUE = CMIS_PREFIX + "maxValue";

   //public static final String CMIS_TYPE_ID = CMIS_PREFIX + "typeId";

   /**
    * CMIS min value.
    */
   public static final String CMIS_MIN_VALUE = CMIS_PREFIX + "minValue";

   /**
    * CMIS name.
    */
   public static final String CMIS_NAME = CMIS_PREFIX + "name";

   /**
    * CMIS object id.
    */
   public static final String CMIS_OBJECT_ID = CMIS_PREFIX + "objectId";

   /**
    * CMIS object type id.
    */
   public static final String CMIS_OBJECT_TYPE_ID = CMIS_PREFIX + "objectTypeId";

   /**
    * CMIS open choice.
    */
   public static final String CMIS_OPEN_CHOICE = CMIS_PREFIX + "openChoice";

   /**
    * CMIS orderable.
    */
   public static final String CMIS_ORDERABLE = CMIS_PREFIX + "orderable";

   /**
    * CMIS parent id.
    */
   public static final String CMIS_PARENT_ID = CMIS_PREFIX + "parentId";

   /**
    * CMIS path.
    */
   public static final String CMIS_PATH = CMIS_PREFIX + "path";

   /**
    * CMIS policy text.
    */
   public static final String CMIS_POLICY_TEXT = CMIS_PREFIX + "policyText";

   /**
    * CMIS precision.
    */
   public static final String CMIS_PRECISION = CMIS_PREFIX + "precision";

   /**
    * CMIS principal anonymous.
    */
   public static final String CMIS_PRINCIPAL_ANONYMOUS = CMIS_PREFIX + "principalAnonymous";

   /**
    * CMIS principal anyone.
    */
   public static final String CMIS_PRINCIPAL_ANYONE = CMIS_PREFIX + "principalAnyone";

   /**
    * CMIS product name.
    */
   public static final String CMIS_PRODUCT_NAME = CMIS_PREFIX + "productName";

   /**
    * CMIS product version.
    */
   public static final String CMIS_PRODUCT_VERSION = CMIS_PREFIX + "productVersion";

   /**
    * CMIS propagation.
    */
   public static final String CMIS_PROPAGATION = CMIS_PREFIX + "propagation";

   /**
    * CMIS properties.
    */
   public static final String CMIS_PROPERTIES = CMIS_PREFIX + "properties";

   /**
    * CMIS property boolean.
    */
   public static final String CMIS_PROPERTY_BOOLEAN = CMIS_PREFIX + "propertyBoolean";

   /**
    * CMIS property boolean definition.
    */
   public static final String CMIS_PROPERTY_BOOLEAN_DEFINITION = CMIS_PREFIX + "propertyBooleanDefinition";

   /**
    * CMIS property date time.
    */
   public static final String CMIS_PROPERTY_DATE_TIME = CMIS_PREFIX + "propertyDateTime";

   /**
    * CMIS property datetime definition.
    */
   public static final String CMIS_PROPERTY_DATETIME_DEFINITION = CMIS_PREFIX + "propertyDateTimeDefinition";

   /**
    * CMIS property decimal definition.
    */
   public static final String CMIS_PROPERTY_DECIMAL_DEFINITION = CMIS_PREFIX + "propertyDecimalDefinition";

   /**
    * CMIS property html definition.
    */
   public static final String CMIS_PROPERTY_HTML_DEFINITION = CMIS_PREFIX + "propertyHTMLDefinition";

   /**
    * CMIS property id.
    */
   public static final String CMIS_PROPERTY_ID = CMIS_PREFIX + "propertyId";

   /**
    * CMIS property id definition.
    */
   public static final String CMIS_PROPERTY_ID_DEFINITION = CMIS_PREFIX + "propertyIdDefinition";

   /**
    * CMIS property integer.
    */
   public static final String CMIS_PROPERTY_INTEGER = CMIS_PREFIX + "propertyInteger";

   /**
    * CMIS property integer definition.
    */
   public static final String CMIS_PROPERTY_INTEGER_DEFINITION = CMIS_PREFIX + "propertyIntegerDefinition";

   /**
    * CMIS property definition.
    */
   public static final String CMIS_PROPERTY_DEFINITION = CMIS_PREFIX + "propertyDefinition";

   /**
    * CMIS property string.
    */
   public static final String CMIS_PROPERTY_STRING = CMIS_PREFIX + "propertyString";

   /**
    * CMIS property string definition.
    */
   public static final String CMIS_PROPERTY_STRING_DEFINITION = CMIS_PREFIX + "propertyStringDefinition";

   /**
    * CMIS property type.
    */
   public static final String CMIS_PROPERTY_TYPE = CMIS_PREFIX + "propertyType";

   /**
    * CMIS property URI.
    */
   public static final String CMIS_PROPERTY_URI = CMIS_PREFIX + "propertyUri";

   /**
    * CMIS property URI definition.
    */
   public static final String CMIS_PROPERTY_URI_DEFINITION = CMIS_PREFIX + "propertyUriDefinition";

   /**
    * CMIS private working copy.
    */
   public static final String CMIS_PWC = CMIS_PREFIX + "privateWorkingCopy";

   /**
    * CMIS query.
    */
   public static final String CMIS_QUERY = CMIS_PREFIX + "query";

   /**
    * CMIS query name.
    */
   public static final String CMIS_QUERY_NAME = CMIS_PREFIX + "queryName";

   /**
    * CMIS queryable.
    */
   public static final String CMIS_QUERYABLE = CMIS_PREFIX + "queryable";

   /**
    * CMIS repository description.
    */
   public static final String CMIS_REPOSITORY_DESCRIPTION = CMIS_PREFIX + "repositoryDescription";

   /**
    * CMIS repository id.
    */
   public static final String CMIS_REPOSITORY_ID = CMIS_PREFIX + "repositoryId";

   /**
    * CMIS repository info.
    */
   public static final String CMIS_REPOSITORY_INFO = CMISRA_PREFIX + "repositoryInfo";

   /**
    * CMIS repository name.
    */
   public static final String CMIS_REPOSITORY_NAME = CMIS_PREFIX + "repositoryName";

   /**
    * CMIS repository relationship.
    */
   public static final String CMIS_REPOSITORY_RELATIONSHIP = CMIS_PREFIX + "repositoryRelationship";

   /**
    * CMIS repository URI.
    */
   public static final String CMIS_REPOSITORY_URI = CMIS_PREFIX + "repositoryURI";

   /**
    * CMIS required.
    */
   public static final String CMIS_REQUIRED = CMIS_PREFIX + "required";

   /**
    * CMIS return allowable actions.
    */
   public static final String CMIS_RETURN_ALLOWABLE_ACTIONS = CMIS_PREFIX + "returnAllowableActions";

   /**
    * CMIS root folder id.
    */
   public static final String CMIS_ROOT_FOLDER_ID = CMIS_PREFIX + "rootFolderId";

   /**
    * CMIS search all versions.
    */
   public static final String CMIS_SEARCH_ALL_VERSIONS = CMIS_PREFIX + "searchAllVersions";

   /**
    * CMIS skip count.
    */
   public static final String CMIS_SKIP_COUNT = CMIS_PREFIX + "skipCount";

   /**
    * CMIS source id.
    */
   public static final String CMIS_SOURCE_ID = CMIS_PREFIX + "sourceId";

   /**
    * CMIS statement.
    */
   public static final String CMIS_STATEMENT = CMIS_PREFIX + "statement";

   /**
    * CMIS target id.
    */
   public static final String CMIS_TARGET_ID = CMIS_PREFIX + "targetId";

   /**
    * CMIS thin client URI.
    */
   public static final String CMIS_THIN_CLIENT_URI = CMIS_PREFIX + "thinClientURI";

   /**
    * CMIS tree type.
    */
   public static final String CMIS_TREE_TYPE = "application/cmistree+xml";

   /**
    * CMIS updatability.
    */
   public static final String CMIS_UPDATABILITY = CMIS_PREFIX + "updatability";

   /**
    * CMIS value.
    */
   public static final String CMIS_VALUE = CMIS_PREFIX + "value";

   /**
    * CMIS vendor name.
    */
   public static final String CMIS_VENDOR_NAME = CMIS_PREFIX + "vendorName";

   /**
    * CMIS content stream allowed.
    */
   public static final String CMIS_CONTENT_STREAM_ALLOWED = CMIS_PREFIX + "contentStreamAllowed";

   /**
    * CMIS version label.
    */
   public static final String CMIS_VERSION_LABEL = CMIS_PREFIX + "versionLabel";

   /**
    * CMIS version series checkedout by.
    */
   public static final String CMIS_VERSION_SERIES_CHECKEDOUT_BY = CMIS_PREFIX + "versionSeriesCheckedOutBy";

   /**
    * CMIS version series checkedout id.
    */
   public static final String CMIS_VERSION_SERIES_CHECKEDOUT_ID = CMIS_PREFIX + "versionSeriesCheckedOutId";

   /**
    * CMIS version series id.
    */
   public static final String CMIS_VERSION_SERIES_ID = CMIS_PREFIX + "versionSeriesId";

   /**
    * CMIS version supported.
    */
   public static final String CMIS_VERSION_SUPPORTED = CMIS_PREFIX + "cmisVersionSupported";

   /**
    * CMISRA children.
    */
   public static final String CMISRA_CHILDREN = CMISRA_PREFIX + "children";

   /**
    * CMISRA collection type.
    */
   public static final String CMISRA_COLLECTION_TYPE = CMISRA_PREFIX + "collectionType";

   /**
    * CMISRA object.
    */
   public static final String CMISRA_OBJECT = CMISRA_PREFIX + "object";

   /**
    * CMISRA type.
    */
   public static final String CMISRA_TYPE = CMISRA_PREFIX + "type";

   /**
    * CMIS ACL.
    */
   public static final String CMIS_ACL = CMIS_PREFIX + "acl";
   
   /**
    * ACL.
    */
   public static final String ACL = "acl";
   
   /**
    * CMIS permission.
    */
   public static final String CMIS_PERMISSION = CMIS_PREFIX + "permission";
   
   /**
    * CMIS principal.
    */
   public static final String CMIS_PRINCIPAL = CMIS_PREFIX + "principal";
   
   /**
    * CMIS principal id.
    */
   public static final String CMIS_PRINCIPAL_ID = CMIS_PREFIX + "principalId";
   
   /**
    * CMIS direct.
    */
   public static final String CMIS_DIRECT = CMIS_PREFIX + "direct";
   
   /**
    * Collection.
    */
   public static final String COLLECTION = "collection";

   /**
    * Content.
    */
   public static final String CONTENT = "content";

   /**
    * Create_document.
    */
   public static final String CREATE_DOCUMENT = "createDocument";

   /**
    * Create folder.
    */
   public static final String CREATE_FOLDER = "createFolder";

   /**
    * Create policy.
    */
   public static final String CREATE_POLICY = "createPolicy";

   /**
    * Display name.
    */
   public static final String DISPLAY_NAME = "displayName";

   /**
    * Entry.
    */
   public static final String ENTRY = "entry";

   /**
    * Feed.
    */
   public static final String FEED = "feed";

   /**
    * Feed_type.
    */
   public static final String FEED_TYPE = "application/atom+xml; type=feed";

   /**
    * Href.
    */
   public static final String HREF = "href";

   /**
    * Id.
    */
   public static final String ID = "id";

   /**
    * Local name.
    */
   public static final String LOCAL_NAME = "localName";

   /**
    * Name.
    */
   public static final String NAME = "name";

   /**
    * Property definition id.
    */
   public static final String PROPERTY_DEFINITION_ID = "propertyDefinitionId";

   /**
    * Query name.
    */
   public static final String QUERY_NAME = "queryName";

   /**
    * Relation.
    */
   public static final String RELATION = "rel";

   /**
    * Relationship.
    */
   public static final String RELATIONSHIP = "relationship";

   /**
    * Source.
    */
   public static final String SOURCE = "src";

   /**
    * Summary.
    */
   public static final String SUMMARY = "summary";

   /**
    * Title.
    */
   public static final String TITLE = "title";

   /**
    * Type.
    */
   public static final String TYPE = "type";

   /**
    * Workspace.
    */
   public static final String WORKSPACE = "workspace";

   /**
    * XMLNS app.
    */
   
   public static final String XMLNS_APP = "xmlns:app";
   /**
    * XMLNS app value.
    */
   public static final String XMLNS_APP_VALUE = "http://www.w3.org/2007/app";

   /**
    * XMLNS atom.
    */
   public static final String XMLNS_ATOM = "xmlns:atom";

   /**
    * XMLNS atom value.
    */
   public static final String XMLNS_ATOM_VALUE = "http://www.w3.org/2005/Atom";

   /**
    * XMLNS CMIS.
    */
   public static final String XMLNS_CMIS = "xmlns:cmis";

   /**
    * XMLNS CMIS value.
    */
   public static final String XMLNS_CMIS_VALUE = "http://docs.oasis-open.org/ns/cmis/core/200901";

   /**
    * XMLNS CMISM.
    */
   public static final String XMLNS_CMISM = "xmlns:cmism";

   /**
    * XMLNS CMISM value.
    */
   public static final String XMLNS_CMISM_VALUE = "http://docs.oasis-open.org/ns/cmis/messaging/200901";

   /**
    * XMLNS CMISRA.
    */
   public static final String XMLNS_CMISRA = "xmlns:cmisra";

   /**
    * XMLNS CMISRA VALUE.
    */
   public static final String XMLNS_CMISRA_VALUE = "http://docs.oasis-open.org/ns/cmis/restatom/200901";
}
