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
package org.xcmis.spi;

import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.Property;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface DocumentData extends ObjectData
{
   /**
    * Shortcut to 'cmis:versionLabel' property.
    * 
    * Implementation Compatibility: Optional. Repository versioning specific.
    * Otherwise "Version Property Values are repository-specific when a document is defined as non-versionable."
    *
    * @return 'cmis:versionLabel' property
    */
   String getVersionLabel();

   /**
    * Shortcut to 'cmis:versionSeriesCheckedOutBy' property.
    * 
    * Implementation Compatibility: Optional. Repository versioning specific.
    * Otherwise "Version Property Values are repository-specific when a document is defined as non-versionable."
    *
    * @return 'cmis:versionSeriesCheckedOutBy' property
    */
   String getVersionSeriesCheckedOutBy();

   /**
    * Shortcut to 'cmis:versionSeriesCheckedOutId' property.
    * 
    * Implementation Compatibility: Optional. Repository versioning specific.
    * Otherwise "Version Property Values are repository-specific when a document is defined as non-versionable."
    *
    * @return 'cmis:versionSeriesCheckedOutId' property
    */
   String getVersionSeriesCheckedOutId();

   /**
    * Shortcut to 'cmis:versionSeriesId' property.
    * 
    * Implementation Compatibility: Optional. Repository versioning specific.
    * Otherwise "Version Property Values are repository-specific when a document is defined as non-versionable."
    *
    * @return 'cmis:versionSeriesId' property
    */
   String getVersionSeriesId();

   /**
    * Shortcut to 'cmis:isLatestMajorVersion' property.
    *
    * Implementation Compatibility: Optional. Repository versioning specific.
    * Otherwise "Version Property Values are repository-specific when a document is defined as non-versionable."
    * 
    * @return 'cmis:isLatestMajorVersion' property
    */
   boolean isLatestMajorVersion();

   /**
    * Shortcut to 'cmis:isLatestVersion' property.
    * 
    * Implementation Compatibility: Optional. Repository versioning specific.
    * Otherwise "Version Property Values are repository-specific when a document is defined as non-versionable."
    *
    * @return 'cmis:isLatestVersion' property
    */
   boolean isLatestVersion();

   /**
    * Shortcut to 'cmis:isMajorVersion' property.
    * 
    * Implementation Compatibility: Optional. Repository versioning specific.
    * Otherwise "Version Property Values are repository-specific when a document is defined as non-versionable."
    * 
    * @return 'cmis:isMajorVersion' property
    */
   boolean isMajorVersion();

   /**
    * Shortcut to 'cmis:isVersionSeriesCheckedOut' property.
    *
    * Implementation Compatibility: Optional. Repository versioning specific.
    * Otherwise "Version Property Values are repository-specific when a document is defined as non-versionable."
    * 
    * @return 'cmis:isVersionSeriesCheckedOut' property
    */
   boolean isVersionSeriesCheckedOut();

   /**
    * Shortcut to 'cmis:contentStreamMimeType' property.
    *
    * @return 'cmis:contentStreamMimeType' property or <code>null</code> if
    *         document has not content
    */
   String getContentStreamMimeType();

   /**
    * Get document content stream.
    *
    * @return content stream or <code>null</code> if document has not content
    * @throws IOException if an I/O error occurs
    */
   ContentStream getContentStream() throws IOException;

   /**
    * Set content stream to document. If <code>contentStream</code> is
    * <code>null</code> then existed content of this document will be removed.
    * 
    * Implementation Compatibility: SHOULD be implemented if 
    * capabilityContentStreamUpdatability = anytime
    * or
    * capabilityContentStreamUpdatability = pwconly and isPWC() = true
    *
    * @param contentStream {@link ContentStream} or <code>null</code>
    * @throws IOException if any i/o error occurs
    * @throws VersioningException if object is not current version and storage
    *         do not support update other then latest version
    * @throws UpdateConflictException if object that is no longer current (as
    *         determined by the storage)
    * @throws StorageException if object's content stream can not be updated
    *         (save changes) cause to storage internal problem
    */
   void setContentStream(ContentStream contentStream) throws IOException, UpdateConflictException, VersioningException,
      StorageException;

   /**
    * Check does current document has content or not.
    *
    * @return <code>true</code> if has content and <code>false</code> if not
    */
   boolean hasContent();

   /**
    * Discard checkout operation. See {@link Connection#cancelCheckout(String)}.
    *
    * Implementation Compatibility: Optional. Repository versioning specific.
    * 
    * @throws VersioningException if object is non-current document version and
    *         'cancel checkout' action and not supported for non-current version
    *         of document
    * @throws UpdateConflictException if object that is no longer current (as
    *         determined by the storage)
    * @throws StorageException if changes can't be saved cause to storage
    *         internal problem
    */
   void cancelCheckout() throws VersioningException, UpdateConflictException, StorageException;

   /**
    * Set private working copy as latest (current) version of document.
    * 
    * Implementation Compatibility: Optional. Repository versioning specific.
    *
    * @param major is major
    * @param checkinComment check-in comment
    * @param properties the document properties. May be <code>null</code> if
    *        properties are not changed
    * @param content the document content stream. May be <code>null</code> if
    *        content is not changed
    * @param acl the list of ACEs to be applied to new version of document. May
    *        be <code>null</code> or empty list
    * @param policies the list of policies. May be <code>null</code> or empty
    *        collection
    * @return new version of document
    * @throws NameConstraintViolationException if <i>cmis:name</i> specified in
    *         properties throws conflict
    * @throws UpdateConflictException if object that is no longer current (as
    *         determined by the storage)
    * @throws StorageException if newly version of Document can't be saved in
    *         storage cause to its internal problem
    */
   DocumentData checkin(boolean major, String checkinComment, Map<String, Property<?>> properties,
      ContentStream content, List<AccessControlEntry> acl, Collection<PolicyData> policies)
      throws NameConstraintViolationException, UpdateConflictException, StorageException;

   /**
    * Create PWC from this document. Properties and content (optionally) of this
    * document copied to PWC.
    * 
    * Implementation Compatibility: Optional. Repository versioning specific.
    *
    * @return PWC
    * @throws VersioningException if one of the following conditions are met:
    *         <ul>
    *         <li>object is not latest version of document version and it is not
    *         supported to checked-out other then latest version</li>
    *         <li>version series already have one checked-out document. It is
    *         not possible to have more then one PWC at time</li>
    *         </ul>
    * @throws UpdateConflictException if object that is no longer current (as
    *         determined by the storage)
    * @throws StorageException if newly created PWC was not saved in storage
    *         cause to storage internal problem
    */
   DocumentData checkout() throws VersioningException, UpdateConflictException, StorageException;

   /**
    * Implementation Compatibility: Optional. Repository versioning specific.
    * Otherwise "Version Property Values are repository-specific when a document is defined as non-versionable."
    * 
    * @return <code>true</code> if current Document is private working copy and
    *         <code>false</code> otherwise
    */
   boolean isPWC();
}
