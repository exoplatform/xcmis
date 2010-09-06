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

import org.xcmis.spi.CmisConstants;
import org.xcmis.spi.PolicyData;
import org.xcmis.spi.model.BaseType;
import org.xcmis.spi.model.Property;
import org.xcmis.spi.model.PropertyType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

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
   }

   public Entry(Map<String, Value> values, Set<String> policies, Map<String, Set<String>> permissions)
   {
      this.values = values != null ? new ConcurrentHashMap<String, Value>(values) : null;
      this.policies = policies != null ? new CopyOnWriteArraySet<String>(policies) : null;
      this.permissions = permissions != null ? new ConcurrentHashMap<String, Set<String>>(permissions) : null;
   }

   public void addPolicy(PolicyData policy)
   {
      getPolicies().add(policy.getObjectId());
   }

   public BaseType getBaseTypeId()
   {
      Value value = getValues().get(CmisConstants.BASE_TYPE_ID);
      if (value != null)
      {
         String[] strs = value.getStrings();
         return strs.length > 0 ? BaseType.fromValue(strs[0]) : null;
      }
      return null;
   }

   public String getId()
   {
      Value value = getValues().get(CmisConstants.OBJECT_ID);
      if (value != null)
      {
         String[] strs = value.getStrings();
         return strs.length > 0 ? strs[0] : null;
      }
      return null;
   }

   public Map<String, Set<String>> getPermissions()
   {
      if (permissions == null)
      {
         permissions = new ConcurrentHashMap<String, Set<String>>();
      }
      return permissions;
   }

   public Collection<String> getPolicies()
   {
      if (policies == null)
      {
         policies = new CopyOnWriteArraySet<String>();
      }
      return policies;
   }

   public String getTypeId()
   {
      Value value = getValues().get(CmisConstants.OBJECT_TYPE_ID);
      if (value != null)
      {
         String[] strs = value.getStrings();
         return strs.length > 0 ? strs[0] : null;
      }
      return null;
   }

   public Value getValue(String id)
   {
      return getValues().get(id);
   }

   public Map<String, Value> getValues()
   {
      if (values == null)
      {
         values = new ConcurrentHashMap<String, Value>();
      }
      return values;
   }

   public void removePolicy(PolicyData policy)
   {
      getPolicies().remove(policy.getObjectId());
   }

   public void setPermissions(Map<String, Set<String>> permissions)
   {
      Map<String, Set<String>> ps = getPermissions();
      ps.clear();
      ps.putAll(permissions);
   }

   @SuppressWarnings("unchecked")
   public void setProperty(Property<?> property)
   {
      if (property.getType() == PropertyType.BOOLEAN)
      {
         List<Boolean> booleans = (List<Boolean>)property.getValues();
         setValue(property.getId(), new BooleanValue(booleans));
      }
      else if (property.getType() == PropertyType.DATETIME)
      {
         List<Calendar> dates = (List<Calendar>)property.getValues();
         setValue(property.getId(), new DateValue(dates));
      }
      else if (property.getType() == PropertyType.DECIMAL)
      {
         List<BigDecimal> decimals = (List<BigDecimal>)property.getValues();
         setValue(property.getId(), new DecimalValue(decimals));
      }
      else if (property.getType() == PropertyType.INTEGER)
      {
         List<BigInteger> integers = (List<BigInteger>)property.getValues();
         setValue(property.getId(), new IntegerValue(integers));
      }
      else if (property.getType() == PropertyType.URI)
      {
         List<URI> uris = (List<URI>)property.getValues();
         setValue(property.getId(), new UriValue(uris));
      }
      else if (property.getType() == PropertyType.STRING || property.getType() == PropertyType.HTML
         || property.getType() == PropertyType.ID)
      {
         List<String> text = (List<String>)property.getValues();
         setValue(property.getId(), new StringValue(text));
      }
   }

   public void setValue(String id, Value value)
   {
      Map<String, Value> vs = getValues();
      if (value == null)
      {
         vs.remove(id);
      }
      else
      {
         vs.put(id, value);
      }
   }

   public void setValues(Map<String, Value> values)
   {
      if (values != null)
      {
         for (Map.Entry<String, Value> e : values.entrySet())
         {
            setValue(e.getKey(), e.getValue());
         }
      }
   }

   public String toString()
   {
      return getId();
   }

}
