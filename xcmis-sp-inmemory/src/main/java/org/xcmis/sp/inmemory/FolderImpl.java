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
import org.xcmis.spi.TypeDefinition;
import org.xcmis.spi.data.ContentStream;
import org.xcmis.spi.data.Folder;
import org.xcmis.spi.data.ObjectData;
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
class FolderImpl implements Folder
{



   public FolderImpl()
   {

   }

   public void addObject(ObjectData object) throws ConstraintException
   {
      // TODO Auto-generated method stub

   }

   public ItemsIterator<ObjectData> getChildren(String orderBy)
   {
      // TODO Auto-generated method stub
      return null;
   }

   public String getPath()
   {
      // TODO Auto-generated method stub
      return null;
   }

   public boolean hasChildren()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isAllowedChildType(String typeId)
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isRoot()
   {
      // TODO Auto-generated method stub
      return false;
   }

   public void removeObject(ObjectData object)
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
