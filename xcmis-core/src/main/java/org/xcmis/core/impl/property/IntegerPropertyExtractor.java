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
import org.xcmis.core.CmisPropertyDefinitionType;
import org.xcmis.core.CmisPropertyInteger;
import org.xcmis.core.EnumCardinality;
import org.xcmis.core.EnumPropertyType;
import org.xcmis.spi.RepositoryException;
import org.xcmis.spi.object.Entry;

import java.math.BigInteger;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: IntegerPropertyExtractor.java 2 2010-02-04 17:21:49Z andrew00x $
 */
public class IntegerPropertyExtractor extends AbstractPropertyExtractor implements PropertyExtractor
{

   /**
    * {@inheritDoc}
    */
   public CmisProperty getProperty(Entry cmisEntry, CmisPropertyDefinitionType propDef) throws RepositoryException
   {
      if (EnumCardinality.MULTI == propDef.getCardinality())
      {
         BigInteger[] values = cmisEntry.getIntegers(propDef.getId()); // may throw exception
         CmisPropertyInteger prop = new CmisPropertyInteger();
         prop.setDisplayName(propDef.getId());
         prop.setLocalName(propDef.getId());
         prop.setPropertyDefinitionId(propDef.getId());
         prop.setQueryName(propDef.getQueryName());
         if (values != null)
         {
            for (BigInteger value : values)
            {
               if (value != null)
                  prop.getValue().add(value);
            }
         }
         return prop;
      }
      else
      {
         BigInteger value = cmisEntry.getInteger(propDef.getId()); // may throw exception
         CmisPropertyInteger prop = new CmisPropertyInteger();
         prop.setDisplayName(propDef.getId());
         prop.setLocalName(propDef.getId());
         prop.setPropertyDefinitionId(propDef.getId());
         prop.setQueryName(propDef.getQueryName());
         if (value != null)
            prop.getValue().add(value);
         return prop;
      }
   }

   /**
    * {@inheritDoc}
    */
   public EnumPropertyType getPropertyType()
   {
      return EnumPropertyType.INTEGER;
   }

}
