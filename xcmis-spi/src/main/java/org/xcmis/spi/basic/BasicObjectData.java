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
package org.xcmis.sp.basic;

import java.util.Collection;
import java.util.List;

import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.ObjectDataVisitor;
import org.xcmis.spi.PolicyData;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.RelationshipDirection;
import org.xcmis.spi.model.TypeDefinition;
/**
 * Default Object Data impl
 */
public abstract class BasicObjectData implements ObjectData {

  /* (non-Javadoc)
   * @see org.xcmis.spi.ObjectData#accept(org.xcmis.spi.ObjectDataVisitor)
   */
  public void accept(ObjectDataVisitor visitor) {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see org.xcmis.spi.ObjectData#applyPolicy(org.xcmis.spi.PolicyData)
   */
  public void applyPolicy(PolicyData policy) throws ConstraintException {
    throw new UnsupportedOperationException();

  }

  /* (non-Javadoc)
   * @see org.xcmis.spi.ObjectData#getACL(boolean)
   */
  public List<AccessControlEntry> getACL(boolean onlyBasicPermissions) {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see org.xcmis.spi.ObjectData#getChangeToken()
   */
  public String getChangeToken() {
   
    return null;
   
  }


  /* (non-Javadoc)
   * @see org.xcmis.spi.ObjectData#getParents()
   */
  public Collection<FolderData> getParents() {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see org.xcmis.spi.ObjectData#getPolicies()
   */
  public Collection<PolicyData> getPolicies() {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see org.xcmis.spi.ObjectData#getRelationships(org.xcmis.spi.model.RelationshipDirection, org.xcmis.spi.model.TypeDefinition, boolean)
   */
  public ItemsIterator<RelationshipData> getRelationships(
      RelationshipDirection direction, TypeDefinition type,
      boolean includeSubRelationshipTypes) {
    throw new UnsupportedOperationException();
  }


  /* (non-Javadoc)
   * @see org.xcmis.spi.ObjectData#removePolicy(org.xcmis.spi.PolicyData)
   */
  public void removePolicy(PolicyData policy) throws ConstraintException {
    throw new UnsupportedOperationException();
  }

  /* (non-Javadoc)
   * @see org.xcmis.spi.ObjectData#setACL(java.util.List)
   */
  public void setACL(List<AccessControlEntry> acl) throws ConstraintException {
    throw new UnsupportedOperationException();
  }

}
