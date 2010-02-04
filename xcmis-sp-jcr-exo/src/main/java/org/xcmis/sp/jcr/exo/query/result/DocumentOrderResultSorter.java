/**
 * Copyright (C) 2010 eXo Platform SAS.
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

package org.xcmis.sp.jcr.exo.query.result;

import org.exoplatform.services.jcr.dataflow.ItemDataConsumer;
import org.exoplatform.services.jcr.datamodel.ItemData;
import org.exoplatform.services.jcr.datamodel.QPath;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.result.ScoredRow;

import java.util.Map;

import javax.jcr.RepositoryException;

/**
 * Created by The eXo Platform SAS. Implements a ResultSorter that returns the
 * score nodes in document order.
 * 
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id$
 */
public class DocumentOrderResultSorter extends AbstractItemDataResultSorter
{

   /** Class logger. */
   private static final Log LOG = ExoLogger.getLogger(DocumentOrderResultSorter.class);

   /** The selector name. */
   private final String selectorName;

   /**
    * The Constructor.
    * 
    * @param itemMgr the item mgr
    * @param itemCache the item cache
    * @param selectorName the selector name
    */
   public DocumentOrderResultSorter(final ItemDataConsumer itemMgr, final Map<String, ItemData> itemCache,
      final String selectorName)
   {
      super(itemMgr, itemCache);
      this.selectorName = selectorName;
   }

   /**
    * The Constructor.
    * 
    * @param itemMgr the item mgr
    * @param selectorName the selector name
    */
   public DocumentOrderResultSorter(final ItemDataConsumer itemMgr, final String selectorName)
   {
      super(itemMgr);
      this.selectorName = selectorName;
   }

   /**
    * {@inheritDoc}
    */
   public int compare(ScoredRow o1, ScoredRow o2)
   {
      if (o1.equals(o2))
         return 0;
      try
      {
         final QPath path1 = getPath(o1.getNodeIdentifer(selectorName));
         final QPath path2 = getPath(o2.getNodeIdentifer(selectorName));
         // TODO should be checked
         if (path1 == null || path2 == null)
            return 0;
         return path1.compareTo(path2);
      }
      catch (final RepositoryException e)
      {
         LOG.error(e.getLocalizedMessage());
      }
      return 0;
   }

   /**
    * Gets the path.
    * 
    * @param id the id
    * @return the path
    * @throws RepositoryException the repository exception
    */
   private QPath getPath(final String id) throws RepositoryException
   {
      ItemData item = getItemByUuid(id);
      if (item != null)
         return item.getQPath();
      return null;
   }

}
