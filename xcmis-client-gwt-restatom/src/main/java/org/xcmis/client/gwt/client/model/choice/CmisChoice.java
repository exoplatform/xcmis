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

package org.xcmis.client.gwt.client.model.choice;

import org.xcmis.client.gwt.client.rest.QName;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 
 * @version $Id: 
 *
 */
public class CmisChoice
{
   /**
    * Display name.
    */
   protected String displayName;

   /**
    * Other attributes.
    */
   private Map<QName, String> otherAttributes = new HashMap<QName, String>();

   /**
    * Gets the value of the displayName property.
    * 
    * @return
    *     possible object is
    *     {@link String }
    *     
    */
   public String getDisplayName()
   {
      return displayName;
   }

   /**
    * Sets the value of the displayName property.
    * 
    * @param value
    *     allowed object is
    *     {@link String }
    *     
    */
   public void setDisplayName(String value)
   {
      this.displayName = value;
   }

   /**
    * Gets a map that contains attributes that aren't bound to any typed property on this class.
    * 
    * <p>
    * the map is keyed by the name of the attribute and 
    * the value is the string value of the attribute.
    * 
    * the map returned by this method is live, and you can add new attribute
    * by updating the map directly. Because of this design, there's no setter.
    * 
    * 
    * @return
    *     always non-null
    */
   public Map<QName, String> getOtherAttributes()
   {
      return otherAttributes;
   }

}
