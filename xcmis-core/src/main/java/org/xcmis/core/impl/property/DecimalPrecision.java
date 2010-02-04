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

package org.xcmis.core.impl.property;

/**
 * Precision in bits supported by decimal property.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public enum DecimalPrecision
{

   /**
    * 32 bits. 
    */
   BITS_32(32),
   /**
    * 64 bits.
    */
   BITS_64(64);

   /**
    * Get DecimalPrecision from integer value.
    * 
    * @param value the integer value
    * @return DecimalPrecision
    */
   public static DecimalPrecision fromInteger(int value)
   {
      for (DecimalPrecision dp : DecimalPrecision.values())
         if (dp.value == value)
            return dp;
      return null;
   }

   /**
    * value.
    */
   private final int value;

   //

   /**
    * @param value value
    */
   private DecimalPrecision(int value)
   {
      this.value = value;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return Integer.toString(value);
   }

}
