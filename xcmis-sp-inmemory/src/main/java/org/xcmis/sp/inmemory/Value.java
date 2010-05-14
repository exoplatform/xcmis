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

package org.xcmis.sp.inmemory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Calendar;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
abstract class Value
{

   public Boolean[] getBooleans()
   {
      return null;
   }

   public Calendar[] getDates()
   {
      return null;
   }

   public BigDecimal[] getDecimals()
   {
      return null;
   }

   public BigInteger[] getIntegers()
   {
      return null;
   }

   public String[] getStrings()
   {
      return null;
   }

   public URI[] getURI()
   {
      return null;
   }

   public boolean isBoolean()
   {
      return false;
   }

   public boolean isDate()
   {
      return false;
   }

   public boolean isDecimal()
   {
      return false;
   }

   public boolean isInteger()
   {
      return false;
   }

   public boolean isString()
   {
      return false;
   }

   public boolean isURI()
   {
      return false;
   }
}
