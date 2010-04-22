/*
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

package org.xcmis.spi;

import java.util.ArrayList;
import java.util.List;

/**
 * List of CMIS items. It contains list itself and additional information such
 * as {@link #isHasMoreItems()} and {@link #getNumItems()}.
 *
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com">Alexey
 *         Zavizionov</a>
 * @version $Id: CmisObjectInFolderList.java 34360 2009-07-22 23:58:59Z sunman $
 */
public final class ItemsList<T>
{

   private int numItems = -1;

   private boolean hasMoreItems;

   private List<T> list;

   /**
    * Default constructor.
    */
   public ItemsList()
   {
   }

   /**
    * Constructor.
    * 
    * @param list the list of items.
    */
   public ItemsList(List<T> list)
   {
      this.list = list;
   }

   /**
    * @return set of items
    */
   public List<T> getItems()
   {
      if (list == null)
      {
         list = new ArrayList<T>();
      }
      return list;
   }

   /**
    * @return total number of items. It is not need to be equals to number of
    *         items in current list {@link #getItems()}. It may be equals to
    *         number of items in current list only if this list contains all
    *         requested items and no more pages available. This method must
    *         return -1 if total number of items in unknown.
    */
   public int getNumItems()
   {
      return numItems;
   }

   public void setNumItems(int numItems)
   {
      this.numItems = numItems;
   }

   /**
    * @return <code>false</code> if this is last sub-set of items in paging
    */
   public boolean isHasMoreItems()
   {
      return hasMoreItems;
   }

   public void setHasMoreItems(boolean hasMoreItems)
   {
      this.hasMoreItems = hasMoreItems;
   }

}
