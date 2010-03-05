/*
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.xcmis.search.model.operand;

import org.xcmis.search.Visitors;
import org.xcmis.search.model.QueryElement;
import org.xcmis.search.model.constraint.Comparison;
import org.xcmis.search.model.source.SelectorName;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A dynamic operand used in a {@link Comparison} constraint.
 */
public abstract class DynamicOperand implements QueryElement
{
   private static final long serialVersionUID = 1L;

   private final Set<SelectorName> selectorNames;

   /**
    * Create a arithmetic dynamic operand that operates upon the supplied selector name(s).
    * 
    * @param selectorNames the selector names
    * @throws IllegalArgumentException if the name list is null or empty, or if any of the values are null
    */
   protected DynamicOperand(Collection<SelectorName> selectorNames)
   {
      if (selectorNames == null)
      {
         throw new IllegalArgumentException("selectorNames[0] shoud not be null");
      }
      this.selectorNames = Collections.unmodifiableSet(new LinkedHashSet<SelectorName>(selectorNames));
   }

   /**
    * Create a arithmetic dynamic operand that operates upon the selector names given by the supplied dynamic operand(s).
    * 
    * @param operand the operand defining the selector names
    * @throws IllegalArgumentException if the operand is null
    */
   protected DynamicOperand(DynamicOperand operand)
   {
      if (operand == null)
      {
         throw new IllegalArgumentException("operand shoud not be null");
      }
      this.selectorNames = operand.getSelectorNames(); // immutable, so we can reference it directly
   }

   /**
    * Create a arithmetic dynamic operand that operates upon the selector names given by the supplied dynamic operand(s).
    * 
    * @param operands the operands defining the selector names
    * @throws IllegalArgumentException if the operand is null
    */
   protected DynamicOperand(DynamicOperand... operands)
   {
      if (operands == null)
      {
         throw new IllegalArgumentException("operands shoud not be null");
      }
      Set<SelectorName> names = new LinkedHashSet<SelectorName>();
      for (DynamicOperand operand : operands)
      {
         names.addAll(operand.getSelectorNames());
      }
      this.selectorNames = Collections.unmodifiableSet(names);
   }

   /**
    * Create a arithmetic dynamic operand that operates upon the selector names given by the supplied dynamic operand(s).
    * 
    * @param operands the operands defining the selector names
    * @throws IllegalArgumentException if the operand is null
    */
   protected DynamicOperand(Iterable<? extends DynamicOperand> operands)
   {
      if (operands == null)
      {
         throw new IllegalArgumentException("operands shoud not be null");
      }
      Set<SelectorName> names = new LinkedHashSet<SelectorName>();
      for (DynamicOperand operand : operands)
      {
         names.addAll(operand.getSelectorNames());
      }
      this.selectorNames = Collections.unmodifiableSet(names);
   }

   /**
    * Create a arithmetic dynamic operand that operates upon the supplied selector name(s).
    * 
    * @param selectorNames the selector names
    * @throws IllegalArgumentException if the selector names array is null or empty, or if any of the values are null
    */
   protected DynamicOperand(SelectorName... selectorNames)
   {
      if (selectorNames.length == 1)
      {
         if (selectorNames == null)
         {
            throw new IllegalArgumentException("selectorNames[0] shoud not be null");
         }
         this.selectorNames = Collections.singleton(selectorNames[0]);
      }
      else
      {
         if (selectorNames == null)
         {
            throw new IllegalArgumentException("selectorNames[0] shoud not be null");
         }
         this.selectorNames =
            Collections.unmodifiableSet(new LinkedHashSet<SelectorName>(Arrays.asList(selectorNames)));
         int i = 0;
         for (SelectorName name : this.selectorNames)
         {
            if (name == null)
            {
               throw new IllegalArgumentException("selectorNames[" + i + "] shoud not be null");
            }
         }
      }
   }

   /**
    * Get the selector symbols to which this operand applies.
    * 
    * @return the immutable ordered set of non-null selector names used by this operand; never null and never empty
    */
   public Set<SelectorName> getSelectorNames()
   {
      return selectorNames;
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return Visitors.readable(this);
   }
}
