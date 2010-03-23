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
package org.xcmis.search.content;

import org.apache.commons.lang.Validate;
import org.xcmis.search.value.PropertyType;

import java.io.InputStream;

/**
 * Property of content.
 */
public class Property<V>
{
   private final PropertyType type;

   private final String name;

   private final ContentValue<V> value;

   /**
    * @param type
    * @param name
    * @param value
    */
   public Property(PropertyType type, String name, ContentValue<V> value)
   {
      super();
      this.type = type;
      this.name = name;
      this.value = value;
   }

   /**
    * @return the type
    */
   public PropertyType getType()
   {
      return type;
   }

   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }

   /**
    * @return the value
    */
   public ContentValue<V> getValue()
   {
      return value;
   }

   public static abstract class ContentValue<V>
   {
      private final long length;

      /**
       * @param length
       */
      public ContentValue(long length)
      {
         super();
         this.length = length;
      }

      /**
       * 
       * @return value of the property.
       */
      public abstract V getValue();

      /**
       * 
       * @return the length of the value.
       */
      public long getLength()
      {
         return length;
      }

   }

   public static class SimpleValue<V> extends ContentValue<V>
   {

      /**
       * @param value
       */
      public SimpleValue(V value, long length)
      {
         super(length);
         this.value = value;
      }

      private final V value;

      /**
       * @see org.xcmis.search.content.Property.ContentValue#getValue()
       */
      public V getValue()
      {
         return value;
      }

   }

   public static class BinaryValue extends ContentValue<InputStream>
   {
      private final InputStream value;

      private final String mimeType;

      private final String encoding;

      private final long length;

      /**
       * @param value
       */
      public BinaryValue(InputStream value, String mimeType, String encoding, long length)
      {
         super(length);
         Validate.notNull(value, "The value argument may not be null");
         this.value = value;
         this.mimeType = mimeType;
         this.encoding = encoding;
         this.length = length;
      }

      /**
       * @return the mimeType
       */
      public String getMimeType()
      {
         return mimeType;
      }

      /**
       * @return the encoding
       */
      public String getEncoding()
      {
         return encoding;
      }

      /**
       * @see org.xcmis.search.content.Property.ContentValue#getValue()
       */
      public InputStream getValue()
      {
         return value;
      }

      /**
       * @see org.xcmis.search.content.Property.ContentValue#getLength()
       */
      public long getLength()
      {
         return length;
      }

   }

}
