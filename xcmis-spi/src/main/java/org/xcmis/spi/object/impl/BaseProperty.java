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

package org.xcmis.spi.object.impl;

import org.xcmis.spi.object.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * Base implementation of CMIS property.
 * 
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class BaseProperty<T> implements Property<T>
{

   private String id;

   private String queryName;

   private String localName;

   private String displayName;

   private List<T> values;

   /**
    * Default constructor.
    */
   public BaseProperty()
   {
   }

   public BaseProperty(String id, String queryName, String localName, String displayName, T value)
   {
      this.id = id;
      this.queryName = queryName;
      this.localName = localName;
      this.displayName = displayName;
      if (value != null)
      {
         this.values = new ArrayList<T>(1);
         this.values.add(value);
      }
   }

   public BaseProperty(String id, String queryName, String localName, String displayName, List<T> values)
   {
      this.id = id;
      this.queryName = queryName;
      this.localName = localName;
      this.displayName = displayName;
      if (values != null)
      {
         this.values = new ArrayList<T>(values);
      }
   }

   /**
    * {@inheritDoc}
    */
   public String getId()
   {
      return id;
   }

   /**
    * {@inheritDoc}
    */
   public String getQueryName()
   {
      return queryName;
   }

   /**
    * {@inheritDoc}
    */
   public String getLocalName()
   {
      return localName;
   }

   /**
    * {@inheritDoc}
    */
   public String getDisplayName()
   {
      return displayName;
   }

   /**
    * {@inheritDoc}
    */
   public List<T> getValues()
   {
      if (values == null)
      {
         values = new ArrayList<T>();
      }
      return values;
   }

   /**
    * Setter for displayName.
    * 
    * @param displayName
    */
   public void setDisplayName(String displayName)
   {
      this.displayName = displayName;
   }

   /**
    * Setter for id.
    * 
    * @param id
    */
   public void setId(String id)
   {
      this.id = id;
   }

   /**
    * Setter for localName.
    * 
    * @param localName
    */
   public void setLocalName(String localName)
   {
      this.localName = localName;
   }

   /**
    * Setter for queryName.
    * 
    * @param queryName
    */
   public void setQueryName(String queryName)
   {
      this.queryName = queryName;
   }

}
