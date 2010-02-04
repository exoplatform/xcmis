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
package org.xcmis.search.qom;

import org.exoplatform.services.jcr.datamodel.InternalQName;
import org.exoplatform.services.jcr.datamodel.QPath;
import org.exoplatform.services.jcr.impl.core.LocationFactory;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.lucene.search.visitor.QueryObjectModelVisitor;

import javax.jcr.RepositoryException;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id$
 */
public abstract class AbstractQueryObjectModelNode
{
   /**
    * Location factory
    */
   private LocationFactory locationFactory;

   /**
    * Class logger.
    */
   private final Log log = ExoLogger.getLogger(getClass().getName());

   public AbstractQueryObjectModelNode(final LocationFactory locationFactory)
   {
      super();
      this.locationFactory = locationFactory;
   }

   /**
    * Accepts a <code>visitor</code> and calls the appropriate visit method
    * depending on the type of this QOM node.
    * 
    * @param visitor the visitor.
    * @param context user defined data, which is passed to the visit method.
    */
   public abstract Object accept(QueryObjectModelVisitor visitor, Object context) throws Exception;

   public LocationFactory getLocationFactory()
   {
      return locationFactory;
   }

   /**
    * Change namespace location.
    * 
    * @param newlocationFactory
    */
   public void reloadLocation(final LocationFactory newlocationFactory)
   {
      locationFactory = newlocationFactory;

   }

   /**
    * Returns the prefixed JCR name for the given qualified name or
    * <code>null</code> if <code>name</code> is <code>null</code>.
    * 
    * @param name the qualified name.
    * @return the prefixed JCR name or <code>name.toString()</code> if an unknown
    *         namespace URI is encountered.
    */
   protected String getJCRName(final InternalQName name)
   {
      if (name == null)
      {
         return null;
      }
      try
      {
         return locationFactory.createJCRName(name).getAsString();
      }
      catch (final RepositoryException e)
      {
         return name.toString();
      }

   }

   /**
    * Returns the prefixed JCR path for the given qualified path or
    * <code>null</code> if <code>path</code> is <code>null</code>.
    * 
    * @param path qualified path
    * @return prefixed JCR path or <code>path.toString()</code> if an unknown
    *         namespace URI is encountered.
    */
   protected String getJCRPath(final QPath path)
   {
      if (path == null)
      {
         return null;
      }
      try
      {
         return locationFactory.createJCRPath(path).getAsString(false);
      }
      catch (final RepositoryException e)
      {
         return path.toString();
      }
   }

}
