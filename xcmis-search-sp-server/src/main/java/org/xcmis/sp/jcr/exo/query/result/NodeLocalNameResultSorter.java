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
import org.exoplatform.services.jcr.datamodel.NodeData;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xcmis.search.result.ScoredRow;

import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.query.qom.NodeName;
import javax.jcr.query.qom.Ordering;
import javax.jcr.query.qom.QueryObjectModelConstants;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34027 2009-07-15 23:26:43Z
 *          aheritier $
 */
public class NodeLocalNameResultSorter extends AbstractItemDataResultSorter
{

   /** Class logger. */
   private static final Log LOG = ExoLogger.getLogger(NodeLocalNameResultSorter.class);

   /** The ordering. */
   private final Ordering ordering;

   /**
    * The Constructor.
    * 
    * @param itemDataConsumer the item data consumer
    * @param itemCache the item cache
    * @param ordering the ordering
    */
   public NodeLocalNameResultSorter(ItemDataConsumer itemDataConsumer, Map<String, ItemData> itemCache,
      Ordering ordering)
   {
      super(itemDataConsumer, itemCache);
      this.ordering = ordering;
   }

   /**
    * The Constructor.
    * 
    * @param itemDataConsumer the item data consumer
    * @param ordering the ordering
    */
   public NodeLocalNameResultSorter(ItemDataConsumer itemDataConsumer, Ordering ordering)
   {
      super(itemDataConsumer);
      this.ordering = ordering;
   }

   /**
    * {@inheritDoc}
    */
   public int compare(ScoredRow o1, ScoredRow o2)
   {
      NodeName propertyValueOrdering = (NodeName)ordering.getOperand();
      String uuid1 = o1.getNodeIdentifer(propertyValueOrdering.getSelectorName());
      String uuid2 = o2.getNodeIdentifer(propertyValueOrdering.getSelectorName());

      if (uuid1 == null && uuid2 == null)
         return 0;
      if (uuid1 == null)
         return -1;
      if (uuid2 == null)
         return 1;

      NodeData nodeData1;
      NodeData nodeData2;
      try
      {
         nodeData1 = getNodeData(uuid1);
         nodeData2 = getNodeData(uuid2);
      }
      catch (RepositoryException e)
      {
         if (LOG.isDebugEnabled())
            LOG.debug(e.getLocalizedMessage());
         return 0;
      }

      if (nodeData1 == null && nodeData2 == null)
         return 0;
      if (nodeData1 == null)
         return -1;
      if (nodeData2 == null)
         return 1;

      String name1 = nodeData1.getQPath().getName().getName();
      String name2 = nodeData2.getQPath().getName().getName();

      int result = name1.compareTo(name2);
      if (result != 0)
      {
         result *= QueryObjectModelConstants.JCR_ORDER_DESCENDING.equals(ordering.getOrder()) ? -1 : 1;
         return result;
      }
      return 0;
   }

}
