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
import org.xcmis.core.CmisPropertyDateTime;
import org.xcmis.core.CmisPropertyDefinitionType;
import org.xcmis.core.EnumCardinality;
import org.xcmis.core.EnumPropertyType;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.object.Entry;
import org.xcmis.spi.utils.CmisUtils;

import java.util.Calendar;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class DatePropertyExtractor extends AbstractPropertyExtractor implements PropertyExtractor
{

   /**
    * {@inheritDoc}
    */
   public CmisProperty getProperty(Entry cmisEntry, CmisPropertyDefinitionType propDef) throws RepositoryException
   {
      if (EnumCardinality.MULTI == propDef.getCardinality())
      {
         Calendar[] values = cmisEntry.getDates(propDef.getLocalName()); // may throw exception
         CmisPropertyDateTime prop = new CmisPropertyDateTime();
         prop.setDisplayName(propDef.getDisplayName());
         prop.setLocalName(propDef.getLocalName());
         prop.setPropertyDefinitionId(propDef.getId());
         prop.setQueryName(propDef.getQueryName());
         if (values != null)
         {
            for (Calendar value : values)
            {
               if (value != null)
                  prop.getValue().add(CmisUtils.fromCalendar(value));
            }
         }
         return prop;
      }
      else
      {
         Calendar value = cmisEntry.getDate(propDef.getLocalName()); // may throw exception
         CmisPropertyDateTime prop = new CmisPropertyDateTime();
         prop.setDisplayName(propDef.getDisplayName());
         prop.setLocalName(propDef.getLocalName());
         prop.setPropertyDefinitionId(propDef.getId());
         prop.setQueryName(propDef.getQueryName());
         if (value != null)
            prop.getValue().add(CmisUtils.fromCalendar(value));
         return prop;
      }
   }

   /**
    * {@inheritDoc}
    */
   public EnumPropertyType getPropertyType()
   {
      return EnumPropertyType.DATETIME;
   }

}
