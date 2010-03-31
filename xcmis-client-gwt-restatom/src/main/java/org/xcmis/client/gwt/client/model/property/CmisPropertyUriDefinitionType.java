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

package org.xcmis.client.gwt.client.model.property;

import org.xcmis.client.gwt.client.model.choice.CmisChoiceUri;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 
 * @version $Id:
 *
 */
public class CmisPropertyUriDefinitionType extends CmisPropertyDefinitionType
{

   /**
    * Default value.
    */
   protected CmisPropertyUri defaultValue;

   /**
    * Choice.
    */
   protected List<CmisChoiceUri> choice;

   /**
    * @return {@link CmisPropertyUri}
    */
   public CmisPropertyUri getDefaultValue()
   {
      return defaultValue;
   }

   /**
    * @param value value
    */
   public void setDefaultValue(CmisPropertyUri value)
   {
      this.defaultValue = value;
   }

   /**
    * @return List containing {@link CmisChoiceUri}
    */
   public List<CmisChoiceUri> getChoice()
   {
      if (choice == null)
      {
         choice = new ArrayList<CmisChoiceUri>();
      }
      return this.choice;
   }

}
