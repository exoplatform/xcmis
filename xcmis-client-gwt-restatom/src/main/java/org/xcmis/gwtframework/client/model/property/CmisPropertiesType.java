/**
 *  Copyright (C) 2010 eXo Platform SAS.
 *
 *  This is free software; you can redistribute it and/or modify it
 *  under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation; either version 2.1 of
 *  the License, or (at your option) any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this software; if not, write to the Free
 *  Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.xcmis.gwtframework.client.model.property;

import org.xcmis.gwtframework.client.util.QName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 
 * @version $Id: 
 *
 */
public class CmisPropertiesType
{
   /**
    * Property.
    */
   protected List<CmisProperty> property;

   /**
    * List any.
    */
   protected List<Object> any;

   /**
    * Other attributes.
    */
   private Map<QName, String> otherAttributes = new HashMap<QName, String>();
   
   /**
    * @return List containing {@link CmisProperty}
    */
   public List<CmisProperty> getProperty()
   {
      if (property == null)
      {
         property = new ArrayList<CmisProperty>();
      }
      return this.property;
   }

   /**
    * @return List containing {@link Object}
    */
   public List<Object> getAny()
   {
      if (any == null)
      {
         any = new ArrayList<Object>();
      }
      return this.any;
   }

   /**
    * @return always non-null 
    */
   public Map<QName, String> getOtherAttributes()
   {
      return otherAttributes;
   }

}
