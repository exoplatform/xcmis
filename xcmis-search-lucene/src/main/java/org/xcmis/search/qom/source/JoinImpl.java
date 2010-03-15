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
package org.xcmis.search.qom.source;

import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.lucene.search.visitor.QueryObjectModelVisitor;
import org.xcmis.search.qom.AbstractQueryObjectModelNode;

import java.util.HashSet;
import java.util.Set;

import javax.jcr.query.qom.Join;
import javax.jcr.query.qom.JoinCondition;
import javax.jcr.query.qom.Source;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: JoinImpl.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class JoinImpl extends SourceImpl implements Join
{
   /**
    * Join condition.
    */
   private final JoinCondition joinCondition;

   /**
    * Join type.
    */
   private final String joinType;

   /**
    * Left node-tuple source.
    */
   private final Source left;

   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(getClass().getName());

   /**
    * Right node-tuple source.
    */
   private final Source right;

   /**
    * @param left - Left node-tuple source.
    * @param right - Right node-tuple source.
    * @param joinType - Join type.
    * @param joinCondition - join condition.
    */
   public JoinImpl(LocationFactory locationFactory, Source left, Source right, String joinType,
      JoinCondition joinCondition)
   {
      super(locationFactory);
      this.left = left;
      this.right = right;
      this.joinType = joinType;
      this.joinCondition = joinCondition;
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
   public JoinCondition getJoinCondition()
   {
      return joinCondition;
   }

   /**
    * {@inheritDoc}
    */
   public String getJoinType()
   {
      return joinType;
   }

   /**
    * {@inheritDoc}
    */
   public Source getLeft()
   {

      return left;
   }

   /**
    * {@inheritDoc}
    */
   public Source getRight()
   {
      return right;
   }

   @Override
   public Set<String> getSelectorsNames()
   {
      Set<String> selectors = new HashSet<String>();
      selectors.addAll(((SourceImpl)left).getSelectorsNames());
      selectors.addAll(((SourceImpl)right).getSelectorsNames());
      return selectors;
   }

   /**
    * {@inheritDoc}
    */
   public void reloadLocation(LocationFactory locationFactory)
   {
      super.reloadLocation(locationFactory);
      ((AbstractQueryObjectModelNode)left).reloadLocation(locationFactory);
      ((AbstractQueryObjectModelNode)right).reloadLocation(locationFactory);
   }

   /**
    * {@inheritDoc}
    */
   public String toString()
   {
      return "Left:" + left + " Right:" + right + "joinType=" + joinType + "joinCondition:" + joinCondition;
   }

}
