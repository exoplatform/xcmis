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
package org.xcmis.search.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:Sergey.Kabashnyuk@exoplatform.org">Sergey Kabashnyuk</a>
 * @version $Id: exo-jboss-codetemplates.xml 34360 2009-07-22 23:58:59Z ksm $
 *
 */
public class Assert
{
   /**
    * Test transitive equals of same objects
    * @param objects
    */
   public static void assertAllEqual(Object[] objects)
   {
      /**
       * The point of checking each pair is to make sure that equals is
       * transitive per the contract of {@link Object#equals(java.lang.Object)}.
       */
      for (int i = 0; i < objects.length; i++)
      {
         assertFalse(objects[i].equals(null));
         for (int j = 0; j < objects.length; j++)
         {
            assertIsEqual(objects[i], objects[j]);
         }
      }
   }

   public static void assertIsEqual(Object one, Object two)
   {
      assertTrue(one.equals(two));
      assertTrue(two.equals(one));
      assertEquals(one.hashCode(), two.hashCode());
   }

   public static void assertIsNotEqual(Object one, Object two)
   {
      assertReflexiveAndNull(one);
      assertReflexiveAndNull(two);
      assertFalse(one.equals(two));
      assertFalse(two.equals(one));
   }

   public static void assertReflexiveAndNull(Object object)
   {
      assertTrue(object.equals(object));
      assertFalse(object.equals(null));
   }
}
