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

package org.xcmis.client.gwt.client.model;

/**
 * Decimal property precision. At the moment support 32-bit and 64-bit
 * precision.
 * 
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public enum Precision {
   /** 32-bit precision. */
   Bit32(32),
   /** 64-bit precision. */
   Bit64(64);

   /**
    * Value
    */
   private final int value;

   /**
    * @param value value
    */
   private Precision(int value)
   {
      this.value = value;
   }

   /**
    * @return int
    */
   public int getValue()
   {
      return value;
   }

   /**
    * @param value value
    * @return value
    */
   public static Precision fromValue(int value)
   {
      for (Precision e : Precision.values())
      {
         if (e.value == value)
            return e;
      }
      throw new IllegalArgumentException(Integer.toString(value));
   }

   /**
    * @see java.lang.Enum#toString()
    */
   @Override
   public String toString()
   {
      return Integer.toString(value);
   }
}
