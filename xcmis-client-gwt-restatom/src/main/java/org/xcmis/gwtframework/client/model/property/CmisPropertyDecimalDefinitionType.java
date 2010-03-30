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

import org.xcmis.gwtframework.client.model.choice.CmisChoiceDecimal;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 
 * @version $Id:
 *
 */
public class CmisPropertyDecimalDefinitionType extends CmisPropertyDefinitionType
{

   /**
    * Default value.
    */
   protected CmisPropertyDecimal defaultValue;

   /**
    * Max value.
    */
   protected Double maxValue;

   /**
    * Min value.
    */
   protected Double minValue;

   /**
    * Precision.
    */
   protected Integer precision;

   /**
    * Choice.
    */
   protected List<CmisChoiceDecimal> choice;

   /**
    * Gets the value of the defaultValue property.
    * 
    * @return
    *     possible object is
    *     {@link CmisPropertyDecimal }
    *     
    */
   public CmisPropertyDecimal getDefaultValue()
   {
      return defaultValue;
   }

   /**
    * Sets the value of the defaultValue property.
    * 
    * @param value
    *     allowed object is
    *     {@link CmisPropertyDecimal }
    *     
    */
   public void setDefaultValue(CmisPropertyDecimal value)
   {
      this.defaultValue = value;
   }

   /**
    * Gets the value of the maxValue property.
    * 
    * @return
    *     possible object is
    *     {@link BigDecimal }
    *     
    */
   public Double getMaxValue()
   {
      return maxValue;
   }

   /**
    * Sets the value of the maxValue property.
    * 
    * @param value
    *     allowed object is
    *     {@link BigDecimal }
    *     
    */
   public void setMaxValue(Double value)
   {
      this.maxValue = value;
   }

   /**
    * Gets the value of the minValue property.
    * 
    * @return
    *     possible object is
    *     {@link BigDecimal }
    *     
    */
   public Double getMinValue()
   {
      return minValue;
   }

   /**
    * Sets the value of the minValue property.
    * 
    * @param value
    *     allowed object is
    *     {@link BigDecimal }
    *     
    */
   public void setMinValue(Double value)
   {
      this.minValue = value;
   }

   /**
    * Gets the value of the precision property.
    * 
    * @return
    *     possible object is
    *     {@link BigInteger }
    *     
    */
   public Integer getPrecision()
   {
      return precision;
   }

   /**
    * Sets the value of the precision property.
    * 
    * @param value
    *     allowed object is
    *     {@link BigInteger }
    *     
    */
   public void setPrecision(Integer value)
   {
      this.precision = value;
   }

   /**
    * Gets the value of the choice property.
    * 
    * <p>
    * This accessor method returns a reference to the live list,
    * not a snapshot. Therefore any modification you make to the
    * returned list will be present inside the JAXB object.
    * This is why there is not a <CODE>set</CODE> method for the choice property.
    * 
    * <p>
    * For example, to add a new item, do as follows:
    * <pre>
    *    getChoice().add(newItem);
    * </pre>
    * 
    * 
    * <p>
    * Objects of the following type(s) are allowed in the list
    * {@link CmisChoiceDecimal }
    * 
    * @return List containing {@link CmisChoiceDecimal}
    * 
    */
   public List<CmisChoiceDecimal> getChoice()
   {
      if (choice == null)
      {
         choice = new ArrayList<CmisChoiceDecimal>();
      }
      return this.choice;
   }

}
