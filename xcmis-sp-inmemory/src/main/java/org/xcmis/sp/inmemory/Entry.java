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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
final class Entry
{

   private final Map<String, Value> values;

   private final Map<String, Set<String>> permissions;

   private final Set<String> policies;

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

   public void addPolicy(PolicyData policy)
   {
      policies.add(policy.getObjectId());
   }

   public BaseType getBaseTypeId()
   {
      Value value = values.get(CmisConstants.BASE_TYPE_ID);
      return value == null ? null : BaseType.fromValue(value.getStrings()[0]);
   }

   public ByteArrayValue getContent()
   {
      return (ByteArrayValue)values.get(DocumentDataImpl.CONTENT);
   }

   public String getId()
   {
      Value value = values.get(CmisConstants.OBJECT_ID);
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
      Value value = values.get(CmisConstants.OBJECT_TYPE_ID);
      return value == null ? null : value.getStrings()[0];
   }

   public Value getValue(String id)
   {
      return values.get(id);
   }

   public Map<String, Value> getValues()
   {
      return values;
   }

   public void removePolicy(PolicyData policy)
   {
      policies.remove(policy.getObjectId());
   }

   public void setContent(ByteArrayValue bytes)
   {
      values.put(DocumentDataImpl.CONTENT, bytes);
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
