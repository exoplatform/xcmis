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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xcmis.spi.ConstraintException;
import org.xcmis.spi.FolderData;
import org.xcmis.spi.ItemsIterator;
import org.xcmis.spi.NotSupportedException;
import org.xcmis.spi.ObjectData;
import org.xcmis.spi.ObjectDataVisitor;
import org.xcmis.spi.PolicyData;
import org.xcmis.spi.PropertyFilter;
import org.xcmis.spi.RelationshipData;
import org.xcmis.spi.model.AccessControlEntry;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.RelationshipDirection;
import org.xcmis.spi.model.TypeDefinition;

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
      Collection <FolderData> parents = new ArrayList<FolderData>();
      try {
        FolderData parent = this.getParent();
        if(parent != null)
          parents.add(parent);
      } catch (ConstraintException e) {
        // Nothing?
        //e.printStackTrace();
      }
      return parents;
   }

   /**
    * {@inheritDoc}
    */
   public Map<String, Property<?>> getProperties(PropertyFilter filter) {
     
     Collection <Property<?>> allprops = getProperties().values();
     Map<String, Property<?>> properties = new HashMap<String, Property<?>>();
     
     for(Property<?> prop : allprops) {
       String queryName = prop.getQueryName();
       if (filter.accept(queryName))
       {
          properties.put(prop.getId(), prop);
       }
     }
     
     return properties;

   }

   /**
    * {@inheritDoc}
    */
   public Property<?> getProperty(String id) {
     
     return getProperties().get(id);
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

      throw new NotSupportedException();
      //return new BaseItemsIterator<RelationshipData>(new ArrayList<RelationshipData>());
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
