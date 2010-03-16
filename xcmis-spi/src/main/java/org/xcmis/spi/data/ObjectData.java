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

package org.xcmis.spi.data;

import org.xcmis.spi.AccessControlEntry;
import org.xcmis.spi.BaseType;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.NotSupportedException;
import org.xcmis.spi.RelationshipDirection;
import org.xcmis.spi.Storage;
import org.xcmis.spi.TypeDefinition;
import org.xcmis.spi.Permission.BasicPermissions;
import org.xcmis.spi.impl.CmisVisitor;
import org.xcmis.spi.object.PropertyData;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: ObjectData.java 316 2010-03-09 15:20:28Z andrew00x $
 */
public interface ObjectData
{

   void accept(CmisVisitor visitor);

   // ACL

   /**
    * Get ACL currently applied to object. If ACL capability is not supported
    * then this method must throw {@link NotSupportedException}.
    * 
    * @param acl ACL that should replace currently applied ACL
    * @throws ConstraintException if current object is not controllable by ACL,
    *         see {@link TypeDefinition#isControllableACL()}.
    */
   void setACL(List<AccessControlEntry> acl) throws ConstraintException;

   /**
    * Get ACL currently applied to object. If ACL capability is not supported
    * then this method must throw {@link NotSupportedException}.
    * 
    * @param onlyBasicPermissions if <code>true</code> then only CMIS basic
    *        permissions {@link BasicPermissions} must be returned if
    *        <code>false</code> then basic permissions and repository specific
    *        permissions must be returned
    * @return applied ACL. If there is no ACL applied to object or if object is
    *         not controllable by ACL empty list must be returned, never
    *         <code>null</code>
    * @see BasicPermissions
    */
   List<AccessControlEntry> getACL(boolean onlyBasicPermissions);

   // Policies

   /**
    * Applied specified policy to the current object. If Policy object type is
    * not supported then this method must throw {@link NotSupportedException}.
    * 
    * @param policy policy to be applied
    * @throws ConstraintException if current object is not controllable by
    *         Policy, see {@link TypeDefinition#isControllablePolicy()}.
    */
   void applyPolicy(PolicyData policy) throws ConstraintException;

   /**
    * Get policies applied to the current object. If Policy object type is not
    * supported then this method must throw {@link NotSupportedException}.
    * 
    * @return applied Policies. If there is no policies applied to object or if
    *         object is not controllable by policy then empty list must be
    *         returned, never <code>null</code>
    */
   Collection<PolicyData> getPolicies();

   /**
    * Remove specified policy from object. This method must not remove Policy
    * object itself. If Policy object type is not supported then this method
    * must throw {@link NotSupportedException}.
    * 
    * @param policy policy object
    * @throws ConstraintException if current object is not controllable by
    *         Policy, see {@link TypeDefinition#isControllablePolicy()}.
    */
   void removePolicy(PolicyData policy) throws ConstraintException;

   // ------
   
   /**
    * @return <code>true</code> if current object is newly created and was not
    *         persisted yet. If may be created via
    *         {@link Storage#createDocument(FolderData, String, org.xcmis.spi.VersioningState)}
    *         , {@link Storage#createFolder(FolderData, String)}, etc.
    */
   boolean isNew();

   /**
    * @return base type of object
    * @see BaseType
    */
   BaseType getBaseType();

   /**
    * Shortcut to 'cmis:changeToken' property.
    * 
    * @return 'cmis:changeToken' property
    */
   String getChangeToken();

   /**
    * Shortcut to 'cmis:createdBy' property.
    * 
    * @return 'cmis:createdBy' property
    */
   String getCreatedBy();

   /**
    * Shortcut to 'cmis:creationDate' property.
    * 
    * @return 'cmis:creationDate' property
    */
   Calendar getCreationDate();

   /**
    * Shortcut to 'cmis:lastModifiedBy' property.
    * 
    * @return 'cmis:lastModifiedBy' property
    */
   String getLastModifiedBy();

   /**
    * Shortcut to 'cmis:lastModificationDate' property.
    * 
    * @return 'cmis:lastModificationDate' property
    */
   Calendar getLatsModificationDate();

   /**
    * Shortcut to 'cmis:name' property.
    * 
    * @return 'cmis:name' property
    */
   String getName();

   /**
    * Shortcut to 'cmis:objectId' property.
    * 
    * @return 'cmis:objectId' property
    */
   String getObjectId();

   /**
    * Get object parent.
    * 
    * @return parent of current object
    * @throws ConstraintException if object has more then one parent or if
    *         current object is root folder
    */
   FolderData getParent() throws ConstraintException;

   /**
    * Get collections of parent folders. It may contains exactly one object for
    * single-filed and empty collection for unfiled object or root folder.
    * 
    * @return collection of object's parents
    */
   Collection<FolderData> getParents();

   /**
    * Objects relationships.
    * 
    * @param direction relationship's direction.
    * @param typeId relationship type id. If <code>null</code> then return
    *        relationships of all types
    * @param includeSubRelationshipTypes if <code>true</code>, then the return
    *        all relationships whose object types are descendant types of
    *        <code>typeId</code>.
    * @return relationships
    * @see RelationshipDirection
    */
   ItemsIterator<RelationshipData> getRelationships(RelationshipDirection direction, String typeId,
      boolean includeSubRelationshipTypes);

   /**
    * @return type id
    */
   String getTypeId();

   /**
    * @return type definition of object
    */
   TypeDefinition getTypeDefinition();

   /**
    * Shortcut setter for 'cmis:name' property.
    * 
    * @throws NameConstraintViolationException if <i>cmis:name</i> specified in
    *         properties throws conflict
    */
   void setName(String name) throws NameConstraintViolationException;

   /**
    * @return object properties, never <code>null</code>
    */
   PropertyData getPropertyData();

   /**
    * Get the content stream with specified id. Often it should be rendition
    * stream. If object has type other then Document and
    * <code>streamId == null</code> then this method return <code>null</code>.
    * For Document objects default content stream will be returned.
    * 
    * @param streamId content stream id
    * @return content stream or <code>null</code>
    */
   ContentStream getContentStream(String streamId);

}
