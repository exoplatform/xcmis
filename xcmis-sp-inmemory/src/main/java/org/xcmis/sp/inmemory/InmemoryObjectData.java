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

package org.xcmis.sp.inmemory;

import org.xcmis.core.CmisAccessControlListType;
import org.xcmis.core.EnumBaseObjectTypeIds;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.impl.CmisObjectIdentifier;
import org.xcmis.spi.impl.CmisVisitor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class InmemoryObjectData implements CmisObjectIdentifier
{

   private EnumBaseObjectTypeIds baseType;

   private String name;

   private String objectId;

   private String typeId;

   private Set<String> policies;

   private CmisAccessControlListType acl;

   private final Map<String, PropertyData<?>> properties;

   public InmemoryObjectData(String objectId, Map<String, PropertyData<?>> properties, Collection<String> policies,
      CmisAccessControlListType acl)
   {
      this.objectId = objectId;
      this.properties = properties;
      if (policies != null)
         this.policies = new HashSet<String>(policies);
      this.acl = acl;
      this.baseType = EnumBaseObjectTypeIds.fromValue((String)properties.get(CMIS.BASE_TYPE_ID).getValue());
      this.typeId = (String)properties.get(CMIS.OBJECT_TYPE_ID).getValue();
      this.name = (String)properties.get(CMIS.NAME).getValue();
   }

   public void accept(CmisVisitor visitor)
   {
      visitor.visit(this);
   }

   @Override
   public boolean equals(Object other)
   {
      if (other == null)
         return false;
      if (getClass() != other.getClass())
         return false;
      return objectId.equals(((InmemoryObjectData)other).objectId);
   }

   public CmisAccessControlListType getAcl()
   {
      return acl;
   }

   public EnumBaseObjectTypeIds getBaseType()
   {
      return baseType;
   }

   public String getName()
   {
      return name;
   }

   public String getObjectId()
   {
      return objectId;
   }

   public Set<String> getPolicies()
   {
      if (policies == null)
         policies = new HashSet<String>();
      return policies;
   }

   public PropertyData<?> getPropertyData(String propertyId)
   {
      return properties.get(propertyId);
   }
   
   public String[] getPropertyNames()
   {
      Set<String> keys = properties.keySet(); 
      return keys.toArray(new String[keys.size()]);
   }

   public String getTypeId()
   {
      return typeId;
   }

   @Override
   public int hashCode()
   {
      int hash = 7;
      hash = hash * 31 + objectId.hashCode();
      return hash;
   }

   public void setAcl(CmisAccessControlListType acl)
   {
      this.acl = acl;
   }

   @Override
   public String toString()
   {
      return new StringBuilder('{').append("id: ").append(objectId).append("; type: ").append(typeId)
         .append("; name: ").append(name).append('}').toString();
   }

}
