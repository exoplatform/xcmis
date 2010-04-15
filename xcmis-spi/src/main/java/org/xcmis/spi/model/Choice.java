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

package org.xcmis.spi.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Indicates an explicit ordered set of single values allowed for the property.
 *
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Choice<T>
{

   private T[] values;

   private String displayName;

   private List<Choice<T>> choices;

   public Choice()
   {
   }

   public Choice(T[] values, String displayName)
   {
      this.values = values;
      this.displayName = displayName;
   }

   /**
    * Sub-choices of current choice.
    *
    * @return sub-choices. May give <code>null</code> or empty list if no
    *         sub-choices provided
    */
   public List<Choice<T>> getChoices()
   {
      if (choices == null)
      {
         choices = new ArrayList<Choice<T>>();
      }
      return choices;
   }

   /**
    * Display name of choice. It is used for presentation purpose.
    *
    * @return choice display name
    */
   public String getDisplayName()
   {
      return displayName;
   }

   /**
    * Values of choice. One of it will be stored as value of property it
    * selected.
    *
    * @return choice values
    */
   public T[] getValues()
   {
      return values;
   }

   /**
    * Set choice display name.
    *
    * @param displayName display name
    */
   public void setDisplayName(String displayName)
   {
      this.displayName = displayName;
   }

   /**
    * Set choice values.
    *
    * @param values choice values
    */
   public void setValues(T[] values)
   {
      this.values = values;
   }
}
