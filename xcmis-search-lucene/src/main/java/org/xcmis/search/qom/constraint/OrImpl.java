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
package org.xcmis.search.qom.constraint;

import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.lucene.search.visitor.QueryObjectModelVisitor;
import org.xcmis.search.qom.AbstractQueryObjectModelNode;

import java.util.HashSet;
import java.util.Set;

import javax.jcr.query.qom.Constraint;
import javax.jcr.query.qom.Or;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: OrImpl.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class OrImpl extends ConstraintImpl implements Or
{
   /**
    * First constraint.
    */
   private final Constraint constraint1;

   /**
    * Second constraint.
    */
   private final Constraint constraint2;

   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(getClass().getName());

   /**
    * @param constraint1 - First constraint.
    * @param constraint2 - Second constraint.
    */
   public OrImpl(LocationFactory locationFactory, Constraint constraint1, Constraint constraint2)
   {
      super(locationFactory);
      this.constraint1 = constraint1;
      this.constraint2 = constraint2;
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
   public Constraint getConstraint1()
   {
      return constraint1;
   }

   /**
    * {@inheritDoc}
    */
   public Constraint getConstraint2()
   {
      return constraint2;
   }

   @Override
   public Set<String> getSelectorsNames()
   {
      Set<String> selectors = new HashSet<String>();
      selectors.addAll(((ConstraintImpl)constraint1).getSelectorsNames());
      selectors.addAll(((ConstraintImpl)constraint2).getSelectorsNames());
      return selectors;
   }

   /**
    * {@inheritDoc}
    */
   public void reloadLocation(LocationFactory locationFactory)
   {
      super.reloadLocation(locationFactory);
      ((AbstractQueryObjectModelNode)constraint1).reloadLocation(locationFactory);
      ((AbstractQueryObjectModelNode)constraint2).reloadLocation(locationFactory);
   }

   /**
    * {@inheritDoc}
    */
   public String toString()
   {
      return constraint1 + " or " + constraint2;
   }

}
