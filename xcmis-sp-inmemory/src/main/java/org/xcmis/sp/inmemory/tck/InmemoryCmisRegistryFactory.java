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

package org.xcmis.sp.inmemory.tck;

import org.xcmis.spi.CmisRegistry;
import org.xcmis.spi.CmisRegistryFactory;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: InmemoryCmisRegistryFactory.java 1500 2010-08-05 09:00:58Z
 *          andrew00x $
 */
public class InmemoryCmisRegistryFactory implements CmisRegistryFactory
{

   private CmisRegistry reg = new CmisRegistry();

   public InmemoryCmisRegistryFactory()
   {
      reg.addStorage(new org.xcmis.sp.inmemory.StorageProviderImpl("cmis1", "cmis1", "", -1L, -1L, null));
   }

   public CmisRegistry getRegistry()
   {
      return reg;
   }

}
