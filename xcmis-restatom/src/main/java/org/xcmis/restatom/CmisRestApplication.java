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

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * CmisRestApplication.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CmisRestApplication.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class CmisRestApplication extends Application
{

   /**
    * {@inheritDoc}
    */
   public Set<Class<?>> getClasses()
   {
      Set<Class<?>> s = new HashSet<Class<?>>();
      s.add(AtomCmisService.class);
      return s;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<Object> getSingletons()
   {
      Set<Object> s = new HashSet<Object>();
      s.add(new AbderaResponseEntityProvider());
      s.add(new AbderaElementEntityProvider());
      return s;
   }

}
