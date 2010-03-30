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

import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisPropertyDefinitionType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
abstract class AbstractPropertyData<T, V extends CmisProperty> implements PropertyData<T>
{
   protected String propertyId;

   protected String queryName;

   protected String displayName;

   protected String localName;

   protected List<T> values;

   public AbstractPropertyData(CmisPropertyDefinitionType propDef, T value)
   {
      this.propertyId = propDef.getId();
      this.queryName = propDef.getQueryName();
      this.displayName = propDef.getDisplayName();
      this.localName = propDef.getLocalName();
      this.values = new ArrayList<T>(1);
      if (value != null)
         this.values.add(value);
   }

   public AbstractPropertyData(CmisPropertyDefinitionType propDef, List<T> values)
   {
      this.propertyId = propDef.getId();
      this.queryName = propDef.getQueryName();
      this.displayName = propDef.getDisplayName();
      this.localName = propDef.getLocalName();
      this.values = new ArrayList<T>();
      if (values != null)
      {
         for (T b : values)
         {
            if (b != null)
               this.values.add(b);
         }
      }
   }

   public AbstractPropertyData(AbstractPropertyData<T, V> a)
   {
      this.propertyId = a.getPropertyId();
      this.queryName = a.getQueryName();
      this.displayName = a.getDisplayName();
      this.localName = a.getLocalName();
      this.values = new ArrayList<T>(a.getValues());
   }

   protected AbstractPropertyData()
   {
   }

   public String getDisplayName()
   {
      return displayName;
   }

   public String getLocalName()
   {
      return localName;
   }

   public String getPropertyId()
   {
      return propertyId;
   }

   public String getQueryName()
   {
      return queryName;
   }

   public T getValue()
   {
      if (values.size() > 0)
         return values.get(0);
      return null;
   }

   public List<T> getValues()
   {
      return new ArrayList<T>(values);
   }

   public void setValue(T value)
   {
      this.values.clear();
      if (value != null)
         this.values.add(value);
   }

   public void setValues(List<T> value)
   {
      this.values.clear();
      if (value != null)
      {
         for (T v : value)
         {
            // be sure no null in the list
            if (v != null)
               this.values.add(v);
         }
      }
   }

   public abstract void updateFromProperty(V property);

}
