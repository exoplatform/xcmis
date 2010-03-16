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

package org.xcmis.spi;


import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: PropertyFilter.java 316 2010-03-09 15:20:28Z andrew00x $
 */
public class PropertyFilter
{

   /** Property filter for all properties. */
   public static final String ALL_FILTER = CMIS.WILDCARD;

   /** Property filter for all properties. */
   public static final PropertyFilter ALL;

   static
   {
      ALL = new PropertyFilter();
      ALL.retrievalAllProperties = true;
   }

   /** Characters that split. */
   private static final Pattern SPLITTER = Pattern.compile("\\s*,\\s*");

   /** Characters that not allowed in property name. */
   private static final String ILLEGAL_CHARACTERS = ",\"'\\.()";

   /**
    * Property names.
    */
   private Set<String> propertyNames;

   /**
    * Is all properties requested.
    */
   private boolean retrievalAllProperties = false;

   /**
    * Construct new Property Filter.
    * 
    * @param filterString the string that contains either '*' or comma-separated list
    *          of properties names. An arbitrary number of space allowed before
    *          and after each comma.
    * @throws FilterNotValidException if <code>filterString</code> is invalid
    */
   public PropertyFilter(String filterString) throws FilterNotValidException
   {
      if (filterString == null || filterString.length() == 0)
      {
         this.retrievalAllProperties = true;
         return;
      }

      filterString = filterString.trim();
      if (ALL_FILTER.equals(filterString))
      {
         this.retrievalAllProperties = true;
      }
      else
      {
         this.propertyNames = new HashSet<String>();
         for (String token : SPLITTER.split(filterString))
         {
            if (token.length() > 0 && !token.equals(ALL_FILTER))
            {
               for (char ch : token.toCharArray())
               {
                  if (Character.isWhitespace(ch) || ILLEGAL_CHARACTERS.indexOf(ch) != -1)
                  {
                     String msg = "Invalid filter \"" + filterString + "\" contains illegal characters.";
                     throw new FilterNotValidException(msg);
                  }
               }
               this.propertyNames.add(token);
            }
            else
            {
               // String contains empty token or some tokens and special token '*'
               String msg =
                  "Invalid filter \"" + filterString
                     + "\". Filter must contains either '*' OR comma-separated list of properties.";
               throw new FilterNotValidException(msg);
            }
         }
      }
   }

   /**
    *  Private create instance of PropertyFilter.
    */
   private PropertyFilter()
   {
   }

   /**
    * {@inheritDoc}
    */
   public boolean accept(String name)
   {
      return retrievalAllProperties || propertyNames.contains(name);
   }

}
