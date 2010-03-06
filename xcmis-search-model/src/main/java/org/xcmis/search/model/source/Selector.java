/*
 * Copyright (C) 2009 eXo Platform SAS.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.xcmis.search.model.source;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.xcmis.search.QueryObjectModelVisitor;
import org.xcmis.search.VisitException;
import org.xcmis.search.Visitors;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: SelectorImpl.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class Selector extends Source
{
   /**
    * 
    */
   private static final long serialVersionUID = 1474532281798814862L;

   /**
    * Selector alias.
    */
   private final SelectorName alias;

   /**
    * Name of the selector.
    */
   private final SelectorName name;

   private final int hcode;

   /**
    * Create a selector with a name.
    * 
    * @param name
    *           the name for this selector
    * @throws IllegalArgumentException
    *            if the selector name is null
    */
   public Selector(SelectorName name)
   {
      this(name, null);
   }

   /**
    * Create a selector with the supplied name and alias.
    * 
    * @param name
    *           the name for this selector
    * @param alias
    *           the alias for this selector; may be null
    * @throws IllegalArgumentException
    *            if the selector name is null
    */
   protected Selector(SelectorName name, SelectorName alias)
   {
      Validate.notNull(name, "The name argument may not be null");

      this.name = name;
      this.alias = alias;

      this.hcode = new HashCodeBuilder()
                   .append(name)
                   .append(alias)
                   .toHashCode();

   }

   /**
    * {@inheritDoc}
    */
   public void accept(QueryObjectModelVisitor visitor) throws VisitException
   {
      visitor.visit(this);
   }

   /**
    * Get the alias name for this source, if there is one.
    * 
    * @return the alias name, or null if there is none.
    */
   public SelectorName getAlias()
   {
      return alias;
   }

   /**
    * Get the alias if this selector has one, or the name.
    * 
    * @return the alias or name; never null
    */
   public SelectorName getAliasOrName()
   {
      return alias != null ? alias : name;
   }

   /**
    * Get the name for this selector.
    * 
    * @return the selector name; never null
    */
   public SelectorName getName()
   {
      return name;
   }

   /**
    * Determine if this selector has an alias.
    * 
    * @return true if this selector has an alias, or false otherwise.
    */
   public boolean hasAlias()
   {
      return alias != null;
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj)
   {
      if (obj == null)
      {
         return false;
      }
      if (obj == this)
      {
         return true;
      }
      if (obj.getClass() != getClass())
      {
         return false;
      }
      Selector rhs = (Selector)obj;

      return new EqualsBuilder()
                    .append(name, rhs.name)
                    .append(alias, rhs.alias)
                    .isEquals();
   }

   /**
    * {@inheritDoc}
    * 
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      return hcode;
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
