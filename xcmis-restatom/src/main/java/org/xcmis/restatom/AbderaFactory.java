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

package org.xcmis.restatom;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.xcmis.restatom.abdera.CMISExtensionFactory;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class AbderaFactory
{

   private static AtomicReference<Abdera> ainst = new AtomicReference<Abdera>();

   public static Abdera getInstance()
   {
      Abdera abdera = ainst.get();
      if (abdera == null)
      {
         Abdera a = new Abdera();
         Factory factory = a.getFactory();
         factory.registerExtension(new CMISExtensionFactory());
         ainst.compareAndSet(null, a);
         abdera = ainst.get();
      }
      return abdera;
   }

}
