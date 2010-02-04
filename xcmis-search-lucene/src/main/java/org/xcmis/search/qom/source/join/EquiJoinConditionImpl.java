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
package org.xcmis.search.qom.source.join;

import org.exoplatform.services.jcr.datamodel.InternalQName;
import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.lucene.search.visitor.QueryObjectModelVisitor;

import javax.jcr.query.qom.EquiJoinCondition;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id$
 */
public class EquiJoinConditionImpl extends JoinConditionImpl implements EquiJoinCondition
{
   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(getClass().getName());

   /**
    * Property name in the first selector.
    * 
    * @return the property name; non-null
    */
   private final InternalQName property1Name;

   /**
    * Property name in the second selector.
    * 
    * @return the property name; non-null
    */
   private final InternalQName property2Name;

   /**
    * Name of the first selector.
    * 
    * @return the selector name; non-null
    */
   private final InternalQName selector1Name;

   /**
    * Name of the second selector.
    * 
    * @return the selector name; non-null
    */
   private final InternalQName selector2Name;

   /**
    * @param selector1Name - Name of the first selector.
    * @param property1Name - Property name in the first selector.
    * @param selector2Name - Name of the second selector.
    * @param property2Name - Property name in the second selector.
    */
   public EquiJoinConditionImpl(LocationFactory locationFactory, InternalQName selector1Name,
      InternalQName property1Name, InternalQName selector2Name, InternalQName property2Name)
   {
      super(locationFactory);
      this.property1Name = property1Name;
      this.property2Name = property2Name;
      this.selector1Name = selector1Name;
      this.selector2Name = selector2Name;
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
   public String getProperty1Name()
   {
      return getJCRName(property1Name);
   }

   /**
    * {@inheritDoc}
    */
   public String getProperty2Name()
   {
      return getJCRName(property2Name);
   }

   /**
    * {@inheritDoc}
    */
   public InternalQName getProperty1QName()
   {
      return property1Name;
   }

   /**
    * {@inheritDoc}
    */
   public InternalQName getProperty2QName()
   {
      return property2Name;
   }

   /**
    * {@inheritDoc}
    */
   public String getSelector1Name()
   {
      return getJCRName(selector1Name);
   }

   /**
    * {@inheritDoc}
    */
   public String getSelector2Name()
   {
      return getJCRName(selector2Name);
   }

   /**
    * {@inheritDoc}
    */
   public String toString()
   {
      return getSelector1Name() + ":" + getProperty1Name() + "=" + getSelector2Name() + "=" + getProperty2Name();
   }

}
