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
package org.xcmis.search.model.column;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.xcmis.search.QueryObjectModelVisitor;
import org.xcmis.search.VisitException;
import org.xcmis.search.Visitors;
import org.xcmis.search.model.QueryElement;
import org.xcmis.search.model.source.SelectorName;

/**
 * Created by The eXo Platform SAS.
 * 
 */
public class Column implements QueryElement
{
   /**
    * Serial version UID.
    */
   private static final long serialVersionUID = 7650452286649408047L;

   /**
    * Name of the column.
    */
   private final String columnName;

   /**
    * Name of the property.
    */
   private final String propertyName;

   /**
    * Name of the selector.
    */
   private final SelectorName selectorName;

   /**
    * Object hash code.
    */
   private final int hcode;

   /**
    * Include a column for each of the single-valued, accessible properties on
    * the node identified by the selector.
    * 
    * @param selectorName
    *           the selector name
    */
   public Column(SelectorName selectorName)
   {
      this(selectorName, "*", "*");
   }

   /**
    * A column with the given name representing the named property on the node
    * identified by the selector.
    * 
    * @param selectorName
    *           the selector name
    * @param propertyName
    *           the name of the property
    * @param columnName
    *           the name of the column
    */
   public Column(SelectorName selectorName, String propertyName, String columnName)
   {
      Validate.notNull(selectorName, "The selectorName argument may not be null");
      Validate.notNull(propertyName, "The propertyName argument may not be null");
      Validate.notNull(columnName, "The columnName argument may not be null");
      this.selectorName = selectorName;
      this.propertyName = propertyName;
      this.columnName = columnName;
      this.hcode = new HashCodeBuilder().append(selectorName).append(propertyName).append(columnName).toHashCode();
   }

   /**
    * @see org.xcmis.search.model.QueryElement#accept(org.xcmis.search.QueryObjectModelVisitor)
    */
   public void accept(QueryObjectModelVisitor visitor) throws VisitException
   {

      visitor.visit(this);
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
      Column rhs = (Column)obj;

      return new EqualsBuilder().append(selectorName, rhs.selectorName).append(propertyName, rhs.propertyName).append(
         columnName, rhs.columnName).isEquals();

   }

   /**
    * Get the name of the column.
    * 
    * @return the column name; or null if this represents all selectable columsn
    *         on the {@link #getSelectorName() selector}
    */
   public final String getColumnName()
   {
      return columnName;
   }

   /**
    * Get the name of the property that this column represents.
    * 
    * @return the property name; or null if this represents all selectable
    *         columns on the {@link #getSelectorName() selector}
    */
   public final String getPropertyName()
   {
      return propertyName;
   }

   /**
    * Get the name of the selector for the node.
    * 
    * @return the selector name; never null
    */
   public final SelectorName getSelectorName()
   {
      return selectorName;
   }

   /**
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {

      return Visitors.readable(this);
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
    * Create a copy of this Column except that uses the supplied selector name
    * instead.
    * 
    * @param newSelectorName
    *           the new selector name
    * @return a new Column with the supplied selector name and the property and
    *         column names from this object; never null
    * @throws IllegalArgumentException
    *            if the supplied selector name is null
    */
   public Column with(SelectorName newSelectorName)
   {
      return new Column(newSelectorName, propertyName, columnName);
   }

}
