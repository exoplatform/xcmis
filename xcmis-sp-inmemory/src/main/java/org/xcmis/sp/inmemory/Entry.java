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

import org.xcmis.spi.CMIS;
import org.xcmis.spi.model.BaseType;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
final class Entry
{

   private String id;

   private BaseType baseType;

   private String type;

   private Map<String, Value> values;

   private Map<String, Set<String>> permissions;

   private Set<String> policies;

   public Entry(BaseType baseType, String type)
   {
      this.baseType = baseType;
      this.type = type;
      this.values = new HashMap<String, Value>();
      this.values.put(CMIS.BASE_TYPE_ID, new StringValue(baseType.value()));
      this.values.put(CMIS.OBJECT_TYPE_ID, new StringValue(type));
   }

   /**
    * Copy constructor. For creation copy of already existed entry.
    *
    * @param id entry id
    * @param baseType base type id
    * @param type type id
    * @param values property values
    * @param permissions permissions
    * @param policies applied policies
    */
   public Entry(String id, BaseType baseType, String type, Map<String, Value> values,
      Map<String, Set<String>> permissions, Set<String> policies)
   {
      this.id = id;
      this.baseType = baseType;
      this.type = type;
      this.values = values;
      this.permissions = permissions;
      this.policies = policies;
   }

   public void addPolicy(String policy)
   {
      if (policies == null)
      {
         policies = new HashSet<String>();
      }
      policies.add(policy);
   }

   public BaseType getBaseType()
   {
      return baseType;
   }

   public String getId()
   {
      return id;
   }

   public Map<String, Set<String>> getPermissions()
   {
      if (permissions == null)
      {
         permissions = new HashMap<String, Set<String>>();
      }
      return permissions;
   }

   public Collection<String> getPolicies()
   {
      if (policies == null)
      {
         policies = new HashSet<String>();
      }
      return policies;
   }

   public String getType()
   {
      return type;
   }

   public Value getValue(String id)
   {
      if (values == null)
      {
         return null;
      }
      return values.get(id);
   }

   public Map<String, Value> getValues()
   {
      if (values == null)
      {
         values = new HashMap<String, Value>();
      }
      return values;
   }

   public void removePolicy(String policy)
   {
      if (policies == null)
      {
         return;
      }
      policies.remove(policy);
   }

   public void setId(String id)
   {
      this.id = id;
   }

   public void setPermissions(Map<String, Set<String>> permissions)
   {
      if (this.permissions == null)
      {
         this.permissions = new HashMap<String, Set<String>>();
      }
      else
      {
         this.permissions.clear();
      }
      if (permissions != null)
      {
         this.permissions.putAll(permissions);
      }
   }

   public void setValue(String id, Value value)
   {
      if (values == null)
      {
         values = new HashMap<String, Value>();
      }
      values.put(id, value);
   }

   public void setValues(Map<String, Value> values)
   {
      if (this.values == null)
      {
         this.values = new HashMap<String, Value>();
      }
      this.values.putAll(values);
   }

}
