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
package org.xcmis.search.qom.ordering;

import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.lucene.search.visitor.QueryObjectModelVisitor;
import org.xcmis.search.qom.AbstractQueryObjectModelNode;

import javax.jcr.query.qom.DynamicOperand;
import javax.jcr.query.qom.Ordering;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id$
 */
public class OrderingImpl extends AbstractQueryObjectModelNode implements Ordering
{
   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(getClass().getName());

   /**
    * The operand by which to order.
    */
   private final DynamicOperand operand;

   /**
    * The order.
    */
   private final String order;

   /**
    * @param operand - The operand by which to order.
    * @param order - The order.
    */
   public OrderingImpl(LocationFactory locationFactory, DynamicOperand operand, String order)
   {
      super(locationFactory);
      this.operand = operand;
      this.order = order;
   }

   /**
    * {@inheritDoc}
    */
   public Object accept(QueryObjectModelVisitor visitor, Object context) throws Exception
   {
      return visitor.visit(this, context);
   }

   /**
    * {@inheritDoc}
    */
   public DynamicOperand getOperand()
   {
      return operand;
   }

   /**
    * {@inheritDoc}
    */
   public String getOrder()
   {
      return order;
   }

   /**
    * {@inheritDoc}
    */
   public void reloadLocation(LocationFactory locationFactory)
   {
      super.reloadLocation(locationFactory);
      ((AbstractQueryObjectModelNode)operand).reloadLocation(locationFactory);
   }
}
