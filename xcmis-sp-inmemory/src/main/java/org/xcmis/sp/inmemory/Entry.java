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
import org.xcmis.spi.model.TypeDefinition;

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

   private Map<String, Value> values;

   private Map<String, Set<String>> permissions;

   private Set<String> policies;

   public Entry()
   {
      this.values = new HashMap<String, Value>();
      this.policies = new HashSet<String>();
      this.permissions = new HashMap<String, Set<String>>();
   }

   public Entry(Map<String, Value> values, Map<String, Set<String>> permissions, Set<String> policies)
   {
      this.values = values;
      this.permissions = permissions;
      this.policies = policies;
   }

   public Entry copy()
   {
      Entry e = new Entry();
      e.values = new HashMap<String, Value>(values);
      e.permissions = new HashMap<String, Set<String>>(permissions);
      e.policies = new HashSet<String>(policies);
      return e;
   }

   public Entry(Map<String, Value> values2, HashMap<String, Set<String>> hashMap, HashSet<String> hashSet,
      TypeDefinition typeDefinition, StorageImpl storageImpl)
   {
      // TODO Auto-generated constructor stub
   }

   public void addPolicy(String policy)
   {
      policies.add(policy);
   }

   public String getId()
   {
      Value value = values.get(CMIS.OBJECT_ID);
      return value == null ? null : value.getStrings()[0];
   }

   public Map<String, Set<String>> getPermissions()
   {
      return permissions;
   }

   public Collection<String> getPolicies()
   {
      return policies;
   }

   public String getTypeId()
   {
      Value value = values.get(CMIS.OBJECT_TYPE_ID);
      return value == null ? null : value.getStrings()[0];
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
      return values;
   }

   public void removePolicy(String policy)
   {
      policies.remove(policy);
   }

   public void setValue(String id, Value value)
   {
      values.put(id, value);
   }

   public void setValues(Map<String, Value> values)
   {
      this.values.putAll(values);
   }

   public String toString()
   {
      return getId();
   }

}
