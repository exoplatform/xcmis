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

import java.util.HashMap;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class ValueMap extends HashMap<String, Value[]>
{
   private static final long serialVersionUID = 110204414589524515L;

//   public void add(String key, Value value)
//   {
//      if (value == null)
//      {
//         return;
//      }
//      List<Value> list = getList(key);
//      list.add(value);
//   }

   public Value getFirst(String key)
   {
      Value[] v = get(key);
      return v != null && v.length > 0 ? v[0] : null;
   }

   public void putSingle(String key, Value value)
   {
      if (value == null)
      {
         return;
      }
      put(key, new Value[]{value});
   }

//   public List<Value> getList(String key)
//   {
//      List<Value> list = get(key);
//      if (list == null)
//      {
//         list = new ArrayList<Value>();
//         put(key, list);
//      }
//      return list;
//   }

}
