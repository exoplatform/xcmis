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

package org.xcmis.spi.impl;

import org.xcmis.spi.ItemsList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ItemsListImpl<T> implements ItemsList<T>
{

   protected int numItems = -1;

   protected boolean hasMoreItems;

   protected List<T> list;

   public ItemsListImpl(List<T> list)
   {
      this.list = list;
   }

   public ItemsListImpl()
   {
   }

   /**
    * {@inheritDoc}
    */
   public int getNumItems()
   {
      return numItems;
   }

   /**
    * {@inheritDoc}
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
    * {@inheritDoc}
    */
   public boolean isHasMoreItems()
   {
      return hasMoreItems;
   }

   public void setNumItems(int numItems)
   {
      System.out.println(">>> alexey: ItemsListImpl.setNumItems numItems = " + numItems);
      this.numItems = numItems;
   }

   public void setHasMoreItems(boolean hasMoreItems)
   {
      this.hasMoreItems = hasMoreItems;
   }

}
