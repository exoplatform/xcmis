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

package org.xcmis.sp.inmemory;

import org.xcmis.spi.AccessControlEntry;
import org.xcmis.spi.BaseType;
import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NameConstraintViolationException;
import org.xcmis.spi.PropertyFilter;
import org.xcmis.spi.RelationshipDirection;
import org.xcmis.spi.StorageException;
import org.xcmis.spi.TypeDefinition;
import org.xcmis.spi.VersioningException;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.Document;
import org.xcmis.spi.data.Folder;
import org.xcmis.spi.data.Policy;
import org.xcmis.spi.data.Relationship;
import org.xcmis.spi.impl.CmisVisitor;
import org.xcmis.spi.object.Property;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class DocumentImpl implements Document
{

   public void cancelCheckout() throws StorageException
   {
      // TODO Auto-generated method stub

   }

   public Document checkin(boolean major, String checkinComment) throws ConstraintException, StorageException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Document checkout() throws ConstraintException, VersioningException, StorageException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public ContentStream getContentStream()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getContentStreamMimeType()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getVersionLabel()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getVersionSeriesCheckedOutBy()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getVersionSeriesCheckedOutId()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getVersionSeriesId()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public boolean hasContent()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isLatestMajorVersion()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isLatestVersion()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isMajorVersion()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isPWC()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isVersionSeriesCheckedOut()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public void setContentStream(ContentStream contentStream) throws ConstraintException
   {
      // TODO Auto-generated method stub

   }

   public void accept(CmisVisitor visitor)
   {
      // TODO Auto-generated method stub

   }

   public void applyPolicy(Policy policy) throws ConstraintException
   {
      // TODO Auto-generated method stub

   }

   public List<AccessControlEntry> getACL(boolean onlyBasicPermissions)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public BaseType getBaseType()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getChangeToken()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public ContentStream getContentStream(String streamId)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getCreatedBy()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Calendar getCreationDate()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Calendar getLastModificationDate()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getLastModifiedBy()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getName()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getObjectId()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Folder getParent() throws ConstraintException
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Collection<Folder> getParents()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Collection<Policy> getPolicies()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Map<String, Property<?>> getProperties()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Property<?> getProperty(String id)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public ItemsIterator<Relationship> getRelationships(RelationshipDirection direction, TypeDefinition type,
      boolean includeSubRelationshipTypes)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public Map<String, Property<?>> getSubset(PropertyFilter filter)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public TypeDefinition getTypeDefinition()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getTypeId()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public boolean isNew()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public void removePolicy(Policy policy) throws ConstraintException
   {
      // TODO Auto-generated method stub

   }

   public void setACL(List<AccessControlEntry> acl) throws ConstraintException
   {
      // TODO Auto-generated method stub

   }

   public void setName(String name) throws NameConstraintViolationException
   {
      // TODO Auto-generated method stub

   }

   public void setProperties(Map<String, Property<?>> properties) throws ConstraintException,
      NameConstraintViolationException
   {
      // TODO Auto-generated method stub

   }

   public void setProperty(Property<?> property) throws ConstraintException
   {
      // TODO Auto-generated method stub

   }

}
