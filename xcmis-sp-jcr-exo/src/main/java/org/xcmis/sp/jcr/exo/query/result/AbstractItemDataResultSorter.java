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
import org.xcmis.search.result.ResultSorter;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.RepositoryException;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@gmail.com">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34027 2009-07-15 23:26:43Z
 *          aheritier $
 */
public abstract class AbstractItemDataResultSorter implements ResultSorter
{

   /** The item data consumer. */
   protected final ItemDataConsumer itemDataConsumer;

   /** The item cache map. */
   protected final Map<String, ItemData> itemCache;

   /**
    * The Constructor.
    * 
    * @param itemDataConsumer the item data consumer
    */
   public AbstractItemDataResultSorter(ItemDataConsumer itemDataConsumer)
   {
      this(itemDataConsumer, new HashMap<String, ItemData>());
   }

   /**
    * The Constructor.
    * 
    * @param itemDataConsumer the item data consumer
    * @param itemCache the item cache
    */
   public AbstractItemDataResultSorter(ItemDataConsumer itemDataConsumer, Map<String, ItemData> itemCache)
   {
      super();
      this.itemDataConsumer = itemDataConsumer;
      this.itemCache = itemCache;
   }

   /**
    * Return item by uuid.
    * 
    * @param uuid String uuid
    * @return ItemData
    * @throws RepositoryException if any errors in CMIS repository occurs
    */
   protected synchronized ItemData getItemByUuid(String uuid) throws RepositoryException
   {
      ItemData data = itemCache.get(uuid);
      if (data == null)
      {
         data = itemDataConsumer.getItemData(uuid);
         if (data != null)
            itemCache.put(uuid, data);
      }
      return data;
   }

   /**
    * Gets the node data.
    * 
    * @param uuid the uuid
    * @return NodeData
    * @throws RepositoryException if any errors in CMIS repository occurs
    */
   protected NodeData getNodeData(String uuid) throws RepositoryException
   {
      ItemData itemData = getItemByUuid(uuid);
      if (itemData == null || !itemData.isNode())
         return null;
      return (NodeData)itemData;
   }
}
