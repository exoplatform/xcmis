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
import org.xcmis.spi.model.TypeDefinition;

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
    * @return 'cmis:versionLabel' property
    */
   String getVersionLabel();

   /**
    * Shortcut to 'cmis:versionSeriesCheckedOutBy' property.
    *
    * @return 'cmis:versionSeriesCheckedOutBy' property
    */
   String getVersionSeriesCheckedOutBy();

   /**
    * Shortcut to 'cmis:versionSeriesCheckedOutId' property.
    *
    * @return 'cmis:versionSeriesCheckedOutId' property
    */
   String getVersionSeriesCheckedOutId();

   /**
    * Shortcut to 'cmis:versionSeriesId' property.
    *
    * @return 'cmis:versionSeriesId' property
    */
   String getVersionSeriesId();

   /**
    * Shortcut to 'cmis:isLatestMajorVersion' property.
    *
    * @return 'cmis:isLatestMajorVersion' property
    */
   boolean isLatestMajorVersion();

   /**
    * Shortcut to 'cmis:isLatestVersion' property.
    *
    * @return 'cmis:isLatestVersion' property
    */
   boolean isLatestVersion();

   /**
    * Shortcut to 'cmis:isMajorVersion' property.
    *
    * @return 'cmis:isMajorVersion' property
    */
   boolean isMajorVersion();

   /**
    * Shortcut to 'cmis:isVersionSeriesCheckedOut' property.
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

   //

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
    * @param contentStream {@link ContentStream} or <code>null</code>
    * @throws ConstraintException if document type definition attribute
    *         {@link TypeDefinition#getContentStreamAllowed()} is 'notallowed'
    *         and specified <code>contentStream</code> is other then
    *         <code>null</code> or if
    *         {@link TypeDefinition#getContentStreamAllowed()} attribute is
    *         'required' and <code>contentStream</code> is <code>null</code>
    * @throws IOException TODO
    */
   void setContentStream(ContentStream contentStream) throws ConstraintException, IOException;

   /**
    * Check does current document has content or not.
    *
    * @return <code>true</code> if has content and <code>false</code> if not
    */
   boolean hasContent();

   /**
    * Discard checkout operation. See {@link Connection#cancelCheckout(String)}.
    *
    * @throws StorageException if any storage error occurs
    */
   void cancelCheckout() throws StorageException;

   /**
    * Set private working copy as latest (current) version of.
    *
    * @param major is major
    * @param checkinComment check-in comment
    * @param properties the document properties
    * @param content the document content stream
    * @param addACL the list of ACEs to be added
    * @param removeACL the list of ACEs to be removed
    * @param policies the list of policy IDs
    * @return DocumentData document
    * @throws ConstraintException if the object is not versionable
    * @throws StorageException if newly version of Document can't be saved in
    *         storage cause to its internal problem
    */
   DocumentData checkin(boolean major, String checkinComment, Map<String, Property<?>> properties,
      ContentStream content, List<AccessControlEntry> addACL, List<AccessControlEntry> removeACL,
      Collection<ObjectData> policies) throws ConstraintException, StorageException;

   /**
    * Create PWC from this document. Properties and content (optionally) of this
    * document copied to PWC.
    *
    * @return PWC
    * @throws ConstraintException if the object is not versionable
    * @throws VersioningException if object is not latest version of document
    *         version and it is not supported to checked-out other then latest
    *         version
    * @throws StorageException if newly created PWC was not saved in storage
    *         cause to storage internal problem
    */
   DocumentData checkout() throws ConstraintException, VersioningException, StorageException;

   /**
    * @return <code>true</code> if current Document is private working copy and
    *         <code>false</code> otherwise
    */
   boolean isPWC();

   //   Collection<DocumentData> getAllVersions() throws CmisRuntimeException;

}
