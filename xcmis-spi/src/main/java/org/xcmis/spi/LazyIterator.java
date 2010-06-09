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

package org.xcmis.spi;

import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public abstract class LazyIterator<T> implements ItemsIterator<T>
{

   protected T next;

   /**
    * {@inheritDoc}
    */
   public boolean hasNext()
   {
      return next != null;
   }

   /**
    * {@inheritDoc}
    */
   public T next()
   {
      if (next == null)
      {
         throw new NoSuchElementException();
      }
      T n = next;
      fetchNext();
      return n;
   }

   /**
    * {@inheritDoc}
    */
   public void remove()
   {
      throw new UnsupportedOperationException("remove");
   }

   /**
    * {@inheritDoc}
    */
   public void skip(int skip) throws NoSuchElementException
   {
      while (skip-- > 0)
      {
         fetchNext();
         if (next == null)
         {
            throw new NoSuchElementException();
         }
      }
   }

   /**
    * To fetch next item and set it in field <code>next</code>
    */
   protected abstract void fetchNext();

}
