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

import javax.jcr.query.qom.DescendantNodeJoinCondition;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: DescendantNodeJoinConditionImpl.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class DescendantNodeJoinConditionImpl extends JoinConditionImpl implements DescendantNodeJoinCondition
{
   /**
    * Name of the ancestor selector.
    */
   private final InternalQName ancestorSelectorName;

   /**
    * Name of the descendant selector.
    */
   private final InternalQName descendantSelectorName;

   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(getClass().getName());

   /**
    * @param ancestorSelectorName - Name of the ancestor selector.
    * @param descendantSelectorName - Name of the descendant selector.
    */
   public DescendantNodeJoinConditionImpl(LocationFactory locationFactory, InternalQName descendantSelectorName,
      InternalQName ancestorSelectorName)
   {
      super(locationFactory);
      this.ancestorSelectorName = ancestorSelectorName;
      this.descendantSelectorName = descendantSelectorName;
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
   public String getAncestorSelectorName()
   {
      return getJCRName(ancestorSelectorName);
   }

   /**
    * {@inheritDoc}
    */
   public String getDescendantSelectorName()
   {
      return getJCRName(descendantSelectorName);
   }

   /**
    * {@inheritDoc}
    */
   public String toString()
   {
      return "ancestorSelectorName:" + ancestorSelectorName + " descendantSelectorName:" + descendantSelectorName;
   }
}
