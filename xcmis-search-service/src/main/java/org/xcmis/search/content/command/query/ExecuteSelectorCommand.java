/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.xcmis.search.content.command.query;

import org.apache.commons.lang.Validate;
import org.xcmis.search.content.command.InvocationContext;
import org.xcmis.search.content.command.VisitableCommand;
import org.xcmis.search.content.interceptors.Visitor;
import org.xcmis.search.model.Limit;
import org.xcmis.search.model.constraint.Constraint;
import org.xcmis.search.model.ordering.Ordering;
import org.xcmis.search.model.source.Selector;
import org.xcmis.search.model.source.SelectorName;

import java.util.List;
import java.util.Map;

/**
 * Command for execution query with one single {@link Selector} filtered by
 * {@link Constraint}, limited and ordered if needed.
 * 
 */
public class ExecuteSelectorCommand implements VisitableCommand
{
   private final SelectorName selector;

   private final List<Constraint> constrains;

   private final Limit limit;

   private final List<Ordering> orderings;

   private final Map<String, Object> bindVariablesValues;

   /**
    * @param source
    * @param constrain
    * @param limit
    * @param orderings
    */
   public ExecuteSelectorCommand(SelectorName selector, List<Constraint> constrains, Limit limit,
      List<Ordering> orderings, Map<String, Object> bindVariablesValues)
   {
      this.bindVariablesValues = bindVariablesValues;
      Validate.notNull(selector, "The selector argument may not be null");
      //Validate.notNull(constrain, "The constrain argument may not be null");
      //Validate.notNull(limit, "The limit argument may not be null");
      Validate.notNull(orderings, "The orderings argument may not be null");
      Validate.notNull(bindVariablesValues, "The bindVariablesValues argument may not be null");
      this.selector = selector;
      this.constrains = constrains;
      this.limit = limit;
      this.orderings = orderings;
   }

   /**
    * @return the bindVariablesValues
    */
   public Map<String, Object> getBindVariablesValues()
   {
      return bindVariablesValues;
   }

   /**
    * @return the selector
    */
   public SelectorName getSelector()
   {
      return selector;
   }

   /**
    * @return the constrain
    */
   public List<Constraint> getConstrains()
   {
      return constrains;
   }

   /**
    * @return the limit
    */
   public Limit getLimit()
   {
      return limit;
   }

   /**
    * @return the orderings
    */
   public List<Ordering> getOrderings()
   {
      return orderings;
   }

   /**
    * @see org.xcmis.search.content.command.VisitableCommand#acceptVisitor(org.xcmis.search.content.command.InvocationContext,
    *      org.xcmis.search.content.interceptors.Visitor)
    */
   public Object acceptVisitor(InvocationContext ctx, Visitor visitor) throws Throwable
   {
      return visitor.visitExecuteSelectorCommand(ctx, this);
   }

}
