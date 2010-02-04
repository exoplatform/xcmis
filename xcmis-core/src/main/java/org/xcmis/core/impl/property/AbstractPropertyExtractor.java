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

import org.xcmis.core.CmisPropertyDefinitionType;
import org.xcmis.core.CmisTypeDefinitionType;

import java.util.Iterator;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public abstract class AbstractPropertyExtractor
{

   /**
    * Retrieves the property definition.
    * 
    * @param typeDef CmisTypeDefinitionType.
    * @param propertyId string property Id.
    * @return  propdef CmisPropertyDefinitionType.
    */
   protected CmisPropertyDefinitionType getPropertyDefinition(CmisTypeDefinitionType typeDef, String propertyId)
   {
      CmisPropertyDefinitionType propDef = null;
      for (Iterator<CmisPropertyDefinitionType> iter = typeDef.getPropertyDefinition().iterator(); iter.hasNext()
         && propDef == null;)
      {
         CmisPropertyDefinitionType def = iter.next();
         if (propertyId.equals(def.getId()))
            propDef = def;
      }
      return propDef;
   }

}
