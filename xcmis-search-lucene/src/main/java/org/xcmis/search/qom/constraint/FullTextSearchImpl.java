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

import org.exoplatform.services.jcr.datamodel.InternalQName;
import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.lucene.search.visitor.QueryObjectModelVisitor;

import java.util.HashSet;
import java.util.Set;

import javax.jcr.query.qom.FullTextSearch;
import javax.jcr.query.qom.StaticOperand;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id$
 */
public class FullTextSearchImpl extends ConstraintImpl implements FullTextSearch
{
   /**
    * Full-text search expression.
    */
   private final StaticOperand fullTextSearchExpression;

   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(getClass().getName());

   /**
    * Name of the property.
    */
   private final InternalQName propertyName;

   /**
    * Name of the selector against which to apply this constraint.
    */
   private final InternalQName selectorName;

   /**
    * @param selectorName - Name of the selector against which to apply this
    *          constraint.
    * @param propertyName - Name of the property.
    * @param fullTextSearchExpression - Full-text search expression.
    */
   public FullTextSearchImpl(LocationFactory locationFactory, InternalQName selectorName, InternalQName propertyName,
      StaticOperand fullTextSearchExpression)
   {
      super(locationFactory);
      this.selectorName = selectorName;
      this.propertyName = propertyName;
      this.fullTextSearchExpression = fullTextSearchExpression;
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
   public String getPropertyName()
   {
      return getJCRName(propertyName);
   }

   /**
    * {@inheritDoc}
    */
   public InternalQName getPropertyQName()
   {
      return propertyName;
   }

   /**
    * {@inheritDoc}
    */
   public String getSelectorName()
   {
      return getJCRName(selectorName);
   }

   @Override
   public Set<String> getSelectorsNames()
   {
      Set<String> selectors = new HashSet<String>();
      selectors.add(getSelectorName());
      return selectors;
   }

   public StaticOperand getFullTextSearchExpression()
   {
      return fullTextSearchExpression;
   }
}
