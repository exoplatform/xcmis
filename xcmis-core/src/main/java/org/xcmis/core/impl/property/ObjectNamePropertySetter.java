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

package org.xcmis.core.impl.property;

import org.xcmis.core.CmisProperty;
import org.xcmis.core.CmisPropertyString;
import org.xcmis.core.EnumPropertyType;
import org.xcmis.spi.CMIS;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.object.Entry;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com.ua>Alexey Zavizionov</a>
 * @version $Id: ObjectNamePropertySetter.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class ObjectNamePropertySetter implements PropertySetter
{

   /**
    * {@inheritDoc}
    */
   public void setProperty(EnumPropertyType propertyType, Entry cmis, CmisProperty property) throws RepositoryException
   {
      String name = null;
      if (((CmisPropertyString)property).getValue().size() > 0)
         name = ((CmisPropertyString)property).getValue().get(0);
      cmis.setName(name);
   }

   /**
    * {@inheritDoc}
    */
   public String getPropertyId()
   {
      return CMIS.NAME;
   }

}
