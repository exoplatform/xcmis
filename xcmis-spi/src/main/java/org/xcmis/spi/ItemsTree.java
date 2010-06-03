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

import java.util.List;

/**
 * Reflect tree hierarchy of CMIS object.
 * 
 * @author <a href="mailto:andrey00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class ItemsTree<T>
{

   private final T container;

   private final List<ItemsTree<T>> children;

   public ItemsTree(T element, List<ItemsTree<T>> children)
   {
      this.container = element;
      this.children = children;
   }

   /**
    * @return element (node) in three hierarchy
    */
   public T getContainer()
   {
      return container;
   }

   /**
    * @return children of object returned by {@link #getContainer()}
    */
   public List<ItemsTree<T>> getChildren()
   {
      return children;
   }
}
