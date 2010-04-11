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

package org.xcmis.spi.utils;

import org.xcmis.spi.CmisConstants;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: MimeType.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class MimeType
{
   /** Type. */
   private final String type;

   /** Sub-type. */
   private final String subType;

   /**
    * Create instance of MimeType.
    * 
    * @param type the name of type
    * @param subType the name of sub-type
    */
   public MimeType(String type, String subType)
   {
      this.type =
         type == null || type.length() == 0 ? CmisConstants.WILDCARD
            : ((type = type.trim()).length() == 0 ? CmisConstants.WILDCARD : type.toLowerCase());
      this.subType =
         subType == null || subType.length() == 0 ? CmisConstants.WILDCARD
            : ((subType = subType.trim()).length() == 0 ? CmisConstants.WILDCARD : subType.toLowerCase());
   }

   /**
    * Create instance of MimeType from the string.
    * 
    * @param source string that represents media-type in form 'type/sub-type'.
    *           If <code>source</code> is <code>null</code> or empty
    *           then it is the same as pass '*&#47;*'.
    *           All parameters after ';' in <code>source</code> will be ignored. 
    * @return MimeType
    */
   // TODO : support for parameter. 
   public static MimeType fromString(String source)
   {
      if (source == null || source.length() == 0 || source.charAt(0) == ';')
         return new MimeType(null, null);
      int p = source.indexOf(';');
      if (p > 0)
         source = source.substring(0, p);
      p = source.indexOf('/');
      // Strings such as 'type/' is tolerable.
      if (p == -1 || p == source.length())
         return new MimeType(source, null);
      return new MimeType(source.substring(0, p), source.substring(p + 1));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object other)
   {
      if (other == null)
         return false;
      if (!(other instanceof MimeType))
         return false;
      MimeType otherMimeType = (MimeType)other;
      return type.equalsIgnoreCase(otherMimeType.type) && subType.equalsIgnoreCase(otherMimeType.subType);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode()
   {
      int hash = 9;
      hash = hash * 31 + type.hashCode();
      hash = hash * 31 + subType.hashCode();
      return hash;
   }

   /**
    * Check is one mime-type compatible to other. Function is not commutative.
    * E.g. image/* compatible with image/png, image/jpeg, but image/png is not compatible 
    * with image/*. 
    *   
    * @param other MimeType to be checked for compatible with this.
    * @return TRUE if MimeTypes compatible FALSE otherwise
    */
   public boolean match(MimeType other)
   {
      if (other == null)
         return false;
      return type.equals(CmisConstants.WILDCARD) //
         || (type.equalsIgnoreCase(other.type) //
         && (subType.equals(CmisConstants.WILDCARD) || subType.equalsIgnoreCase(other.subType)));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString()
   {
      return type + "/" + subType;
   }

   /**
    * @return get type
    */
   public String getSubType()
   {
      return subType;
   }

   /**
    * @return get sub-type
    */
   public String getType()
   {
      return type;
   }

}
