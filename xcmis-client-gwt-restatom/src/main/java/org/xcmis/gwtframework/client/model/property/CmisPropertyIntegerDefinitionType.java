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

import org.xcmis.gwtframework.client.model.choice.CmisChoiceInteger;


import java.util.ArrayList;
import java.util.List;

/**
 * @author 
 * @version $Id:
 *
 */
public class CmisPropertyIntegerDefinitionType extends CmisPropertyDefinitionType
{

   /**
    * Default value.
    */
   protected CmisPropertyInteger defaultValue;

   /**
    * Max value.
    */
   protected Long maxValue;

   /**
    * Min value.
    */
   protected Long minValue;

   /**
    * Choice.
    */
   protected List<CmisChoiceInteger> choice;

   /**
    * Gets the value of the defaultValue property.
    * 
    * @return
    *     possible object is
    *     {@link CmisPropertyInteger }
    *     
    */
   public CmisPropertyInteger getDefaultValue()
   {
      return defaultValue;
   }

   /**
    * Sets the value of the defaultValue property.
    * 
    * @param value
    *     allowed object is
    *     {@link CmisPropertyInteger }
    *     
    */
   public void setDefaultValue(CmisPropertyInteger value)
   {
      this.defaultValue = value;
   }

   /**
    * Gets the value of the maxValue property.
    * 
    * @return
    *     possible object is
    *     {@link BigInteger }
    *     
    */
   public Long getMaxValue()
   {
      return maxValue;
   }

   /**
    * @param value value
    */
   public void setMaxValue(Long value)
   {
      this.maxValue = value;
   }

   /**
    * @return {@link Long}
    */
   public Long getMinValue()
   {
      return minValue;
   }

   /**
    * @param value value
    */
   public void setMinValue(Long value)
   {
      this.minValue = value;
   }

   /**
    * @return {@link CmisChoiceInteger}
    */
   public List<CmisChoiceInteger> getChoice()
   {
      if (choice == null)
      {
         choice = new ArrayList<CmisChoiceInteger>();
      }
      return this.choice;
   }

}
