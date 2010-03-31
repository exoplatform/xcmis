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

import java.util.ArrayList;
import java.util.List;

/**
 * @author 
 * @version $Id: 
 *
 */
public class CmisChoiceBoolean extends CmisChoice
{

   /**
    * Value.
    */
   protected List<Boolean> value;

   /**
    * Choice.
    */
   protected List<CmisChoiceBoolean> choice;

   /**
    * Gets the value of the value property.
    * 
    * <p>
    * This accessor method returns a reference to the live list,
    * not a snapshot. Therefore any modification you make to the
    * returned list will be present inside the JAXB object.
    * This is why there is not a <CODE>set</CODE> method for the value property.
    * 
    * <p>
    * For example, to add a new item, do as follows:
    * <pre>
    *    getValue().add(newItem);
    * </pre>
    * 
    * 
    * <p>
    * Objects of the following type(s) are allowed in the list
    * {@link Boolean }
    * 
    * @return List containing {@link Boolean}
    * 
    */
   public List<Boolean> getValue()
   {
      if (value == null)
      {
         value = new ArrayList<Boolean>();
      }
      return this.value;
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
    * {@link CmisChoiceBoolean }
    *  
    * @return List containing {@link CmisChoiceBoolean} 
    * 
    */
   public List<CmisChoiceBoolean> getChoice()
   {
      if (choice == null)
      {
         choice = new ArrayList<CmisChoiceBoolean>();
      }
      return this.choice;
   }

}
