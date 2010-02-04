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
import org.exoplatform.services.jcr.datamodel.QPath;
import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.lucene.search.visitor.QueryObjectModelVisitor;

import javax.jcr.query.qom.SameNodeJoinCondition;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id$
 */
public class SameNodeJoinConditionImpl extends JoinConditionImpl implements SameNodeJoinCondition
{
   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(getClass().getName());

   /**
    * Name of the first selector.
    */
   private final InternalQName selector1Name;

   /**
    * Name of the second selector.
    */
   private final InternalQName selector2Name;

   /**
    * Path relative to the second selector.
    */

   private final QPath selector2Path;

   /**
    * @param selector1Name - Name of the first selector.
    * @param selector2Name - Name of the second selector.
    * @param selector2Path - Path relative to the second selector.
    */
   public SameNodeJoinConditionImpl(LocationFactory locationFactory, InternalQName selector1Name,
      InternalQName selector2Name, QPath selector2Path)
   {
      super(locationFactory);
      this.selector1Name = selector1Name;
      this.selector2Name = selector2Name;
      this.selector2Path = selector2Path;
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
   public String getSelector2Path()
   {
      return getJCRPath(selector2Path);
   }

   /**
    * @return selector2Path
    */
   public QPath getSelector2QPath()
   {
      return selector2Path;
   }

   /**
    * {@inheritDoc}
    */
   public String toString()
   {
      return getSelector1Name() + "=" + getSelector2Name() + " path:" + getSelector2Path();
   }
}
