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

package org.xcmis.spi.object;

import org.xcmis.core.CmisAccessControlEntryType;
import org.xcmis.core.CmisTypeDefinitionType;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.core.EnumRelationshipDirection;
import org.xcmis.core.EnumVersioningState;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.InvalidArgumentException;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.StreamNotSupportedException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Calendar;
import java.util.List;

/**
 * CMIS object's entry point.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface Entry
{

   /**
    * Apply policy to the current object.
    * 
    * @param policy the policy which should be applied to object
    * @throws ConstraintException if current object is not controllable
    *            by policy
    * @throws RepositoryException any other CMIS repository errors
    */
   public void applyPolicy(Entry policy) throws ConstraintException, RepositoryException;

   /**
    * Add child object.
    * 
    * @param child child object
    * @throws ConstraintException if object MAY NOT have children or
    *            children of specified type is not allowed by constraint.
    * @throws RepositoryException any other CMIS repository errors
    */
   void addChild(Entry child) throws ConstraintException, RepositoryException;

   /**
    * Add new ACEs to ACL of object.
    * 
    * @param add permissions to be added
    * @return actual list of ACEs
    * @throws ConstraintException if any of the following conditions are met:
    *            <ul>
    *            <li>The specified object's Object-Type definition's attribute for
    *            <i>controllableACL</i> is FALSE</li>
    *            <li>At least one of the specified values for permission in ANY of the
    *            ACEs does not match ANY of the permissionNames as returned by
    *            getACLCapability and is not a CMIS Basic permission</li>
    *            </ul>
    * @throws RepositoryException any other CMIS repository errors
    */
   List<CmisAccessControlEntryType> addPermissions(List<CmisAccessControlEntryType> add)
      throws ConstraintException, RepositoryException;

   /**
    * Add relationship using current object as source.
    * 
    * @param name the name of relationship object
    * @param target the target object of relationship
    * @param relationshipType the relationship type 
    * @return newly created relationship
    * @throws InvalidArgumentException if supplied <code>target</code> object is
    *            not independent object
    * @throws ConstraintException if target's object type does not
    *            support this as source or source's object type does not
    *            support target
    * @throws RepositoryException any other CMIS repository errors
    */
   Entry addRelationship(String name, Entry target, CmisTypeDefinitionType relationshipType)
      throws InvalidArgumentException, ConstraintException, RepositoryException;

   /**
    * Can apply policy to object.
    * 
    * @return TRUE if policy can be applied FALSE otherwise
    */
   boolean canAddPolicy();

   /**
    * Can object be filed to folder. If repository does not support
    * unfiling/multi-filing capabilities then this method MUST return FALSE.
    * 
    * @return TRUE if object can be filed to folder FALSE otherwise
    */
   boolean canAddToFolder();

   /**
    * Can apply ACEs to object.
    * 
    * @return TRUE if ACEs can be applied to object FALSE otherwise
    */
   boolean canApplyACL();

   /**
    * Can discard the check-out operation for this document.
    * 
    * @return TRUE if check-out can be discarded FALSE otherwise
    */
   boolean canCancelCheckOut();

   /**
    * Can the document be checked-in.
    * 
    * @return TRUE if document can be checked-in FALSE otherwise
    */
   boolean canCheckIn();

   /**
    * Can the document be checked-out.
    * 
    * @return TRUE if document can be checked-out FALSE otherwise
    */
   boolean canCheckOut();

   /**
    * Can create document in this folder.
    * 
    * @return TRUE if document can be created FALSE otherwise
    */
   boolean canCreateDocument();

   /**
    * can create folder using this folder as parent.
    * 
    * @return TRUE if folder can be created FALSE otherwise
    */
   boolean canCreateFolder();

//   /**
//    * Can create policy in this folder.
//    * 
//    * @return TRUE if policy can be created FALSE otherwise
//    */
//   boolean canCreatePolicy(); XXX Removed from schemas

   /**
    * Can create relationship in which this object is a source or target.
    * 
    * @return TRUE if relationship can be created FALSE otherwise
    */
   boolean canCreateRelationship();

   /**
    * Can delete this object.
    * 
    * @return TRUE if object can be deleted FALSE otherwise
    */
   boolean canDelete();

   /**
    * Can delete document's content stream.
    * 
    * @return TRUE if content stream can be deleted FALSE otherwise
    */
   boolean canDeleteContent();

   /**
    * Can delete folder and all its descendants.
    * 
    * @return TRUE if can delete folder FALSE otherwise
    */
   boolean canDeleteTree();

   /**
    * Can get ACL for this object.
    * 
    * @return TRUE if can get ACL FALSE otherwise
    */
   boolean canGetACL();

   /**
    * Can get all versions of this document.
    * 
    * @return TRUE if can get all document's version FALSE otherwise
    */
   boolean canGetAllVersions();

   /**
    * Can get all policies applied to this object.
    * 
    * @return TRUE if can get policies FALSE otherwise
    */
   boolean canGetAppliedPolicies();

   /**
    * Can get children of the folder.
    * 
    * @return TRUE if can get children FALSE otherwise
    */
   boolean canGetChildren();

   /**
    * Can view document's content.
    * 
    * @return TRUE if can view content FALSE otherwise
    */
   boolean canGetContent();

   /**
    * Can get all descendants of the folder.
    * 
    * @return FALSE if can get descendants FALSE otherwise
    */
   boolean canGetDescendants();

   /**
    * Can get parent folder to this folder.
    * 
    * @return FALSE if this folder has parent folder FALSE otherwise
    */
   boolean canGetFolderParent();

   /**
    * Can get set of folder that are children (direct or not) of this folder.
    * 
    * @return TRUE if this can get folder tree FALSE otherwise
    */
   boolean canGetFolderTree();

   /**
    * Can get folder(s) that contain(s) this object.
    * 
    * @return TRUE if can get parent(s) FALSE otherwise
    */
   boolean canGetParents();

   /**
    * Can get object properties.
    * 
    * @return return TRUE if can get object properties FALSE otherwise
    */
   boolean canGetProperties();

   /**
    * Can get object's relationship.
    * 
    * @return TRUE if can get object's relationship FALSE otherwise
    */
   boolean canGetRelationships();

   /**
    * Can get object's renditions.
    * 
    * @return TRUE if can get object's renditions FALSE otherwise
    */
   boolean canGetRenditions();

   /**
    * Can move this object in other folder.
    * 
    * @return TRUE if object can be moved FALSE otherwise
    */
   boolean canMove();

   /**
    * Can be document removed from folder. If repository does not support
    * unfiling/multi-filing capabilities then this method MUST return false.
    * 
    * @return TRUE if object can be removed from folder FALSE otherwise
    */
   boolean canRemoveFromFolder();

   /**
    * Can remove policy applied to this object.
    * 
    * @return TRUE if policy can be removed FALSE otherwise
    */
   boolean canRemovePolicy();

   /**
    * Can set content stream for the document.
    * 
    * @return TRUE if can set content FALSE otherwise
    */
   boolean canSetContent();

   /**
    * Can update object properties.
    * 
    * @return TRUE if properties can be updated FALSE otherwise
    */
   boolean canUpdateProperties();

   /**
    * Create child object with specified type.
    * 
    * @param type CMIS object type
    * @param name object name
    * @param versioningState versioning state for newly created object. If object
    *          type is not versionable this parameter has not any effect for new
    *          object.
    * @return newly created child object
    * @throws ConstraintException if object MAY NOT have children
    * @throws RepositoryException any other CMIS repository errors
    */
   Entry createChild(CmisTypeDefinitionType type, String name, EnumVersioningState versioningState)
      throws ConstraintException, RepositoryException;

   /**
    * Remove this object and all its child if any exists.
    * 
    * @throws RepositoryException any CMIS repository errors
    * @throws ConstraintException if object may not be deleted because it
    *            violate any repository constraints
    */
   void delete() throws RepositoryException, ConstraintException;

   /**
    * Get policies applied to the current object.
    * 
    * @return policies of this object. If object has not any policies then
    *            empty list will be returned instead <code>null</code>.
    * @throws RepositoryException any other CMIS repository errors
    */
   List<Entry> getAppliedPolicies() throws RepositoryException;

   /**
    * Get object's named property as <code>boolean</code>. May throw
    * InvalidArgumentException if property with specified <code>name</code>
    * can't be represented as <code>boolean</code>.
    * 
    * @param name property name
    * @return property value
    * @throws InvalidArgumentException if property with <code>name</code> can't
    *            be represented as <code>boolean</code> 
    * @throws RepositoryException any other CMIS repository errors
    */
   boolean getBoolean(String name) throws InvalidArgumentException, RepositoryException;

   /**
    * Get object's multi-valued named property as array of <code>Boolean[]</code>.
    * May throw InvalidArgumentException if property with specified name can't be
    * represented as <code>Boolean</code> array.
    * 
    * @param name property name
    * @return property value. or <code>null</code> if property is not set
    * @throws InvalidArgumentException if property with <code>name</code> can't
    *            be represented as <code>boolean[]</code> 
    * @throws RepositoryException any other CMIS repository errors
    */
   boolean[] getBooleans(String name) throws InvalidArgumentException, RepositoryException;

   /**
    * Id of user that checked out private working copy (if any exists).
    * 
    * @return user's id or <code>null</code> if no checked-out object in
    *            version series
    * @throws RepositoryException if any exception in CMIS repository occurs
    */
   String getCheckedOutBy() throws RepositoryException;

   /**
    * Id of checked out document (if any exists).
    * 
    * @return id of checked out document or <code>null</code> if no checked-out
    *            object in version series
    * @throws RepositoryException if any exception in CMIS repository occurs
    */
   String getCheckedOutId() throws RepositoryException;

   /**
    * Version check-in comment.
    * 
    * @return the checkInComment that was specified when current version was
    *            checked-in
    * @throws RepositoryException if any exception in CMIS repository occurs
    */
   String getCheckInComment() throws RepositoryException;

   /**
    * Get CMIS children object iterator.
    * 
    * @return children of this object
    * @throws UnsupportedOperationException if object MAY NOT have children
    * @throws RepositoryException any other CMIS repository errors
    */
   ItemsIterator<Entry> getChildren() throws UnsupportedOperationException, RepositoryException;

   /**
    * Get CMIS children object iterator.
    * 
    * @param orderBy order rule. If null then children will be sorted in repository
    *           specific order.
    * @return children of this object
    * @throws UnsupportedOperationException if object MAY NOT have children
    * @throws RepositoryException any other CMIS repository errors
    */
   ItemsIterator<Entry> getChildren(String orderBy) throws UnsupportedOperationException, RepositoryException;

   /**
    * Get document's content.
    *
    * @param streamId stream id if this parameter is null then document content
    *           stream will be returned otherwise rendition stream with specified
    *           <code>streamId</code> will be returned
    * @return content of document 
    * @throws ConstraintException if object does not have content stream
    *            or rendition stream
    * @throws RepositoryException any other CMIS repository errors
    */
   ContentStream getContent(String streamId) throws ConstraintException, RepositoryException;

   /**
    * Get object's named property as <code>Calendar</code>. May throw
    * InvalidArgumentException if property with specified <code>name</code>
    * can't be represented as <code>Calendar</code>.
    * 
    * @param name property name
    * @return property value or <code>null</code> if property is not set.
    * @throws InvalidArgumentException if property with <code>name</code> can't
    *            be represented as <code>Calendar</code> 
    * @throws RepositoryException any other CMIS repository errors
    */
   Calendar getDate(String name) throws InvalidArgumentException, RepositoryException;

   /**
    * Get object's multi-valued named property as array of <code>Calendar</code>.
    * May throw InvalidArgumentException if property with specified <code>name</code>
    * can't be represented as <code>Calendar</code> array.
    * 
    * @param name property name
    * @return property value or <code>null</code> if property is not set.
    * @throws InvalidArgumentException if property with <code>name</code> can't
    *            be represented as <code>Calendar[]</code> 
    * @throws RepositoryException any other CMIS repository errors
    */
   Calendar[] getDates(String name) throws InvalidArgumentException, RepositoryException;

   /**
    * Get object's named property as <code>BigDecimal</code>. May throw
    * InvalidArgumentException if property with specified <code>name</code>
    * can't be represented as <code>BigDecimal</code>.
    * 
    * @param name property name
    * @return property value or <code>null</code> if property is not set.
    * @throws InvalidArgumentException if property with <code>name</code> can't
    *            be represented as <code>BigDecimal</code> 
    * @throws RepositoryException any other CMIS repository errors
    */
   BigDecimal getDecimal(String name) throws InvalidArgumentException, RepositoryException;

   /**
    * Get object's multi-valued named property as array of <code>BigDecimal</code>.
    * May throw InvalidArgumentException if property with specified <code>name</code>
    * can't be represented as <code>BigDecimal</code> array.
    * 
    * @param name property name
    * @return property value or <code>null</code> if property is not set.
    * @throws InvalidArgumentException if property with <code>name</code> can't
    *            be represented as <code>BigDecimal[]</code> 
    * @throws RepositoryException any other CMIS repository errors
    */
   BigDecimal[] getDecimals(String name) throws InvalidArgumentException,
      RepositoryException;

   /**
    * Get object's named property as <code>String</code> that is HTML.
    * May throw InvalidArgumentException if property with specified <code>name</code>
    * can't be represented as <code>String</code>. HTML property is not guaranteed
    * to be validated in any way. The validation behavior is entirely repository
    * specific.
    * 
    * @param name property name
    * @return property value or <code>null</code> if property is not set.
    * @throws InvalidArgumentException if property with <code>name</code> can't
    *            be represented as <code>String</code> or if validation of HTML
    *            content is failed.
    * @throws RepositoryException any other CMIS repository errors
    */
   String getHTML(String name) throws InvalidArgumentException, RepositoryException;

   /**
    * Get object's multi-valued named property as array of
    * <code>String</code> that is HTML. May throw InvalidArgumentException if
    * property with specified name can't be represented as <code>String</code>
    * array. HTML property is not guaranteed to be validated in any way.
    * The validation behavior is entirely repository specific.
    * 
    * @param name property name
    * @return property value or <code>null</code> if property is not set.
    * @throws InvalidArgumentException if property with <code>name</code> can't
    *            be represented as <code>String</code> or if validation of HTML
    *            content is failed.
    * @throws RepositoryException any other CMIS repository errors
    */
   String[] getHTMLs(String name) throws InvalidArgumentException, RepositoryException;

   /**
    * Get object's named property as <code>BigInteger</code>. May throw
    * InvalidArgumentException if property with specified <code>name</code>
    * can't be represented as <code>BigInteger</code>.
    * 
    * @param name property name
    * @return property value or <code>null</code> if property is not set.
    * @throws InvalidArgumentException if property with <code>name</code> can't
    *            be represented as <code>BigInteger</code>
    * @throws RepositoryException any other CMIS repository errors
    */
   BigInteger getInteger(String name) throws InvalidArgumentException, RepositoryException;

   /**
    * Get object's multi-valued named property as array of
    * <code>BigInteger</code>. May throw InvalidArgumentException if property
    * with specified name can't be represented as <code>BigInteger</code> array.
    * 
    * @param name property name
    * @return property value or <code>null</code> if property is not set.
    * @throws InvalidArgumentException if property with <code>name</code> can't
    *            be represented as <code>BigInteger[]</code>
    * @throws RepositoryException any other CMIS repository errors
    */
   BigInteger[] getIntegers(String name) throws InvalidArgumentException,
      RepositoryException;

   /**
    * Get object name.
    * 
    * @return object name
    * @throws RepositoryException any CMIS repository errors
    */
   String getName() throws RepositoryException;

   /**
    * Get object id.
    * 
    * @return object id
    */
   String getObjectId();

   /**
    * Get ancestors of this object.
    * 
    * @return parents of this object. May return object empty set if object has
    *            not parent, never null. If repository does not support
    *            multi-filing the array has exactly one item. 
    *         
    * @throws RepositoryException any CMIS repository errors
    */
   List<Entry> getParents() throws RepositoryException;

   /**
    * Get list of ACEs applied to object.
    * 
    * @return actual list of ACEs. If no ACEs then empty list is returned never null.
    * @throws RepositoryException any CMIS repository errors
    */
   List<CmisAccessControlEntryType> getPermissions() throws RepositoryException;

   /**
    * Get object's relationships.
    * 
    * @param direct must return relationships where the current object is the
    *           source, the target or both
    * @param includeSubRelatioshipTypes true if must return descendant types of
    *           specified typeId
    * @param relationshipType relationship type if this parameter is
    *           <code>null</code> then relationship objects of all types will be
    *           returned. Also may be returned all descendant-types if
    *           <code>includeSubRelatioshipTypes</code> is TRUE
    * @return set of existing relationships.
    * @throws UnsupportedOperationException if object MAY NOT have relationships
    *           cause it is not independent object (other Relationship object)
    * @throws RepositoryException any CMIS repository errors
    */
   ItemsIterator<Entry> getRelationships(EnumRelationshipDirection direct, boolean includeSubRelatioshipTypes,
      CmisTypeDefinitionType relationshipType) throws UnsupportedOperationException, RepositoryException;

   /**
    * Get base object type.
    * 
    * @return base object type
    */
   EnumBaseObjectTypeIds getScope();

   /**
    * Get object's named property as <code>String</code>. May throw
    * InvalidArgumentException if property with specified <code>name</code>
    * can't be represented as <code>String</code>.
    * 
    * @param name property name
    * @return property value or <code>null</code> if property is not set.
    * @throws InvalidArgumentException if property with <code>name</code> can't
    *            be represented as <code>String</code>
    * @throws RepositoryException any other CMIS repository errors
    */
   String getString(String name) throws InvalidArgumentException, RepositoryException;

   /**
    * Get object's multi-valued named property as array of <code>String</code>.
    * May throw InvalidArgumentException if property with specified <code>name</code>
    * can't be represented as <code>String</code> array.
    * 
    * @param name property name
    * @return property value or <code>null</code> if property is not set.
    * @throws InvalidArgumentException if property with <code>name</code> can't
    *            be represented as <code>String[]</code>
    * @throws RepositoryException any other CMIS repository errors
    */
   String[] getStrings(String name) throws InvalidArgumentException, RepositoryException;

   /**
    * Get CMIS Object type.
    * 
    * @return type of object
    */
   CmisTypeDefinitionType getType();

   /**
    * Get object's named property as <code>URI</code>. May throw
    * InvalidArgumentException if property with specified name can't be
    * represented as <code>URI</code>.
    * 
    * @param name property name
    * @return property value or <code>null</code> if property is not set
    * @throws InvalidArgumentException if property with specified <code>name</code>
    *            can't be represented as <code>URI</code>
    * @throws RepositoryException any other CMIS repository errors
    */
   URI getURI(String name) throws InvalidArgumentException, RepositoryException;

   /**
    * Get object's multi-valued named property as array of <code>URI</code>. May
    * throw InvalidArgumentException if property with specified name can't be
    * represented as <code>URI</code> array.
    * 
    * @param name property name
    * @return property value or <code>null</code> if property is not set
    * @throws InvalidArgumentException if property with specified <code>name</code>
    *            can't be represented as <code>URI[]</code>
    * @throws RepositoryException any other CMIS repository errors
    */
   URI[] getURIs(String name) throws InvalidArgumentException, RepositoryException;

   /**
    * Version label.
    * 
    * @return the version label or <code>null</code> if current version has
    *            not label or labels are not supported at all.
    * @throws RepositoryException if any exception in CMIS repository occurs
    */
   String getVersionLabel() throws RepositoryException;

   /**
    * Version series id of document.
    * 
    * @return version series id or <code>null</code> if object has type that is
    *            differ to Document. But Document object (even it is not
    *            versionable) always has version series. Version series of not
    *            versionable Document contains single object.
    * @throws RepositoryException if any exception in CMIS repository occurs
    */
   String getVersionSeriesId() throws RepositoryException;

   /**
    * Check is current version latest in version series.
    * 
    * @return TRUE if current version is latest
    * @throws RepositoryException if any exception in CMIS repository occurs
    */
   boolean isLatest() throws RepositoryException;

   /**
    * Check is current version latest in version series.
    * 
    * @return TRUE if current version is latest major
    * @throws RepositoryException if any exception in CMIS repository occurs
    */
   boolean isLatestMajor() throws RepositoryException;

   /**
    * Check is current version major.
    * 
    * @return TRUE if version is major
    * @throws RepositoryException if any exception in CMIS repository occurs
    */
   boolean isMajor() throws RepositoryException;

   /**
    * Check is object versionable.
    *  
    * @return TRUE if this object is versionable FALSE otherwise
    * @throws RepositoryException if any exception in CMIS repository occurs
    */
   boolean isVersionable() throws RepositoryException;

   /**
    * Remove object with <code>objectId</code> from current folder child
    * @param objectId
    * @return
    * @throws UnsupportedOperationException if object MAY NOT have children
    * @throws InvalidArgumentException if object with specified <code>objectId</code>
    *            is not in children list of current folder
    * @throws RepositoryException any other CMIS repository errors
    */
   Entry removeChild(String objectId) throws UnsupportedOperationException, InvalidArgumentException,
      RepositoryException;

   /**
    * Remove ACEs from ACL of object.
    * 
    * @param remove permissions to be removed from ACL
    * @return actual list of ACEs
    * @throws ConstraintException if any of the following conditions are met:
    *          <ul>
    *          <li>The specified object's Object-Type definition's attribute for
    *          controllableACL is FALSE.</li>
    *          <li>At least one of the specified values for permission in ANY of the
    *          ACEs does not match ANY of the permissionNames as returned by
    *          getACLCapability and is not a CMIS Basic permission</li>
    *          </ul>
    * @throws RepositoryException any other CMIS repository errors
    */
   List<CmisAccessControlEntryType> removePermissions(List<CmisAccessControlEntryType> remove)
      throws ConstraintException, RepositoryException;

   /**
    * Remove the policy from this object.
    * 
    * @param policy the policy which should be removed from the object
    * @throws RepositoryException any CMIS repository errors
    */
   void removePolicy(Entry policy) throws RepositoryException;

   /**
    * Save object's modifications in storage. This method may not be required,
    * dependent on back end storage modifications may be saved immediately after
    * updates.
    * 
    * @throws RepositoryException if error occurs when try to store object in
    *           repository
    */
   void save() throws RepositoryException;

   /**
    * Set single-valued boolean property. May throw ConstraintViolationException
    * if object does not support supplied named property or its format.
    * 
    * @param name property name 
    * @param value property value
    * @return this entry
    * @throws ConstraintException if object does not support supplied
    *           named property or its format
    * @throws RepositoryException any other CMIS repository errors
    */
   Entry setBoolean(String name, boolean value) throws ConstraintException, RepositoryException;

   /**
    * Set multi-valued boolean property. May throw ConstraintViolationException
    * if object does not support supplied named property or its format.
    * 
    * @param name property name
    * @param value property value. If value is <code>null</code> property will be reset.
    * @return this entry
    * @throws ConstraintException if object does not support supplied
    *           named property or its format
    * @throws RepositoryException any other CMIS repository errors
    */
   Entry setBooleans(String name, boolean[] value) throws ConstraintException, RepositoryException;

   /**
    * Set object content stream.
    * 
    * @param content the content stream. If this parameter is null it minds stream will be
    *          removed
    * @return this entry
    * @throws IOException if any i/o error occurs
    * @throws StreamNotSupportedException if object does not supports stream,
    *           object type definition attribute 'contentStreamAllowed' is set to
    *           'not allowed'
    * @throws RepositoryException any other CMIS repository errors
    */
   Entry setContent(ContentStream content) throws IOException, StreamNotSupportedException, RepositoryException;

   /**
    * Set single-valued date property. May throw ConstraintViolationException
    * if object does not support supplied named property or its format.
    * 
    * @param name property name
    * @param value property value. If value is <code>null</code> property will be reset.
    * @return this entry
    * @throws ConstraintException if object does not support supplied
    *           named property or its format
    * @throws RepositoryException any other CMIS repository errors
    */
   Entry setDate(String name, Calendar value) throws ConstraintException, RepositoryException;

   /**
    * Set multi-valued date property. May throw ConstraintViolationException
    * if object does not support supplied named property or its format.
    * 
    * @param name property name
    * @param value property value. If value is <code>null</code> property will be reset.
    * @return this entry
    * @throws ConstraintException if object does not support supplied
    *           named property or its format
    * @throws RepositoryException any other CMIS repository errors
    */
   Entry setDates(String name, Calendar[] value) throws ConstraintException, RepositoryException;

   /**
    * Set single-valued decimal property. May throw ConstraintViolationException
    * if object does not support supplied named property or its format.
    * 
    * @param name property name
    * @param value property value. If value is <code>null</code> property will be reset.
    * @return this entry
    * @throws ConstraintException if object does not support supplied
    *           named property or its format
    * @throws RepositoryException any other CMIS repository errors
    */
   Entry setDecimal(String name, BigDecimal value) throws ConstraintException, RepositoryException;

   /**
    * Set multi-valued decimal property. May throw ConstraintViolationException
    * if object does not support supplied named property or its format.
    * 
    * @param name property name
    * @param value property value. If value is <code>null</code> property will be reset.
    * @return this entry
    * @throws ConstraintException if object does not support supplied
    *           named property or its format
    * @throws RepositoryException any other CMIS repository errors
    */
   Entry setDecimals(String name, BigDecimal[] value) throws ConstraintException, RepositoryException;

   /**
    * Set single-valued html property. May throw ConstraintViolationException
    * if object does not support supplied named property or its format. HTML
    * property is not guaranteed to be validated in any way. The validation
    * behavior is entirely repository specific.
    * 
    * @param name property name
    * @param value property value. If value is <code>null</code> property will be reset.
    * @return this entry
    * @throws ConstraintException if object does not support supplied
    *            named property or its format or if validation of <code>value</code>
    *            as HTML content is failed.
    * @throws RepositoryException any other CMIS repository errors
    */
   Entry setHTML(String name, String value) throws ConstraintException, RepositoryException;

   /**
    * Set multi-valued html property. May throw ConstraintViolationException
    * if object does not support supplied named property or its format. HTML
    * property is not guaranteed to be validated in any way. The validation
    * behavior is entirely repository specific.
    * 
    * @param name property name
    * @param value property value. If value is <code>null</code> property will be reset.
    * @return this entry
    * @throws ConstraintException if object does not support supplied
    *            named property or its format or if validation of <code>value</code>
    *            as HTML content is failed.
    * @throws RepositoryException any other CMIS repository errors
    */
   Entry setHTMLs(String name, String[] value) throws ConstraintException, RepositoryException;

   /**
    * Set single-valued integer property. May throw ConstraintViolationException
    * if object does not support supplied named property or its format.
    * 
    * @param name property name
    * @param value property value. If value is <code>null</code> property will be reset.
    * @return this entry
    * @throws ConstraintException if object does not support supplied
    *           named property or its format
    * @throws RepositoryException any other CMIS repository errors
    */
   Entry setInteger(String name, BigInteger value) throws ConstraintException, RepositoryException;

   /**
    * Set multi-valued integer property. May throw ConstraintViolationException
    * if object does not support supplied named property or its format.
    * 
    * @param name property name
    * @param value property value. If value is <code>null</code> property will be reset.
    * @return this entry
    * @throws ConstraintException if object does not support supplied
    *           named property or its format
    * @throws RepositoryException any other CMIS repository errors
    */
   Entry setIntegers(String name, BigInteger[] value) throws ConstraintException, RepositoryException;

   /**
    * Set new name for entry.
    * 
    * @param name new entry name
    * @throws NameConstraintViolationException in new name violate name constraint
    * @throws RepositoryException any other CMIS repository errors
    */
   void setName(String name) throws NameConstraintViolationException, RepositoryException;

   /**
    * Set single-valued string property. May throw ConstraintViolationException
    * if object does not support supplied named property or its format.
    * 
    * @param name property name
    * @param value property value. If value is <code>null</code> property will be reset.
    * @return this entry
    * @throws ConstraintException if object does not support supplied
    *           named property or its format
    * @throws RepositoryException any other CMIS repository errors
    */
   Entry setString(String name, String value) throws ConstraintException, RepositoryException;

   /**
    * Set multi-valued string property. May throw ConstraintViolationException
    * if object does not support supplied named property or its format.
    * 
    * @param name property name
    * @param value property value. If value is <code>null</code> property will be reset.
    * @return this entry
    * @throws ConstraintException if object does not support supplied
    *           named property or its format
    * @throws RepositoryException any other CMIS repository errors
    */
   Entry setStrings(String name, String[] value) throws ConstraintException, RepositoryException;

   /**
    * Set single-valued <code>URI</code> property. May throw
    * ConstraintViolationException if object does not support supplied
    * named property or its format.
    * 
    * @param name property name
    * @param value property value. If value is <code>null</code> property will be reset.
    * @return this entry
    * @throws ConstraintException if object does not support supplied
    *           named property or its format
    * @throws RepositoryException any other CMIS repository errors
    */
   Entry setURI(String name, URI value) throws ConstraintException, RepositoryException;

   /**
    * Set multi-valued <code>URI</code> property. May throw
    * ConstraintViolationException if object does not support supplied
    * named property or its format.
    *  
    * @param name property name
    * @param value property value. If value is <code>null</code> property will be reset.
    * @return this entry
    * @throws ConstraintException if object does not support supplied
    *           named property or its format
    * @throws RepositoryException any other CMIS repository errors
    */
   Entry setURIs(String name, URI[] value) throws ConstraintException, RepositoryException;

}
