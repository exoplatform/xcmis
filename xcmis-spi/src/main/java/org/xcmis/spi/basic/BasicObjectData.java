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
package org.xcmis.spi.basic;

import org.xcmis.spi.FolderData;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NotSupportedException;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.ObjectDataVisitor;
import org.xcmis.spi.PolicyData;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.RelationshipDirection;
import org.xcmis.spi.model.TypeDefinition;

import java.util.Collection;
import java.util.List;

/**
 * Default Object Data impl
 */
public abstract class BasicObjectData implements ObjectData
{

   /**
    * {@inheritDoc}
    */
   public void accept(ObjectDataVisitor visitor)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */
   public void applyPolicy(PolicyData policy)
   {
      throw new NotSupportedException();
   }

   /**
    * {@inheritDoc}
    */
   public List<AccessControlEntry> getACL(boolean onlyBasicPermissions)
   {
      throw new NotSupportedException();
   }

   /**
    * {@inheritDoc}
    */
   public String getChangeToken()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   public Collection<FolderData> getParents()
   {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */
   public Collection<PolicyData> getPolicies()
   {
      throw new NotSupportedException();
   }

   /**
    * {@inheritDoc}
    */
   public ItemsIterator<RelationshipData> getRelationships(RelationshipDirection direction, TypeDefinition type,
      boolean includeSubRelationshipTypes)
   {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */
   public void removePolicy(PolicyData policy)
   {
      throw new NotSupportedException();
   }

   /**
    * {@inheritDoc}
    */
   public void setACL(List<AccessControlEntry> acl)
   {
      throw new NotSupportedException();
   }

}
