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

package org.xcmis.sp.inmemory;

import java.net.URI;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class UriValue extends Value
{

   private final URI[] values;

   public UriValue()
   {
      this.values = new URI[0];
   }

   public UriValue(URI value)
   {
      this.values = new URI[]{value};
   }

   public UriValue(List<URI> l)
   {
      this.values = l.toArray(new URI[l.size()]);
   }

   public UriValue(URI[] a)
   {
      this.values = new URI[a.length];
      System.arraycopy(a, 0, this.values, 0, a.length);
   }

   @Override
   public URI[] getURI()
   {
      return values;
   }

   @Override
   public boolean isURI()
   {
      return true;
   }

}
